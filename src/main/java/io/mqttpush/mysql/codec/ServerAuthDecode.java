package io.mqttpush.mysql.codec;

import java.io.IOException;

import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.Security;
import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.Capbilities;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.mqttpush.mysql.packet.response.ServerAuthPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * 
 * 
 * @author tianzhenjiu
 *
 */
@SuppressWarnings("unused")
public class ServerAuthDecode extends ChannelInboundHandlerAdapter{
	
	
	
	
	  private static final String CODE_PAGE_1252 = "Cp1252";

	  
	public static final int SEED_LENGTH = 20;
	
	
	String  username="root";
	
	String  password="abc123qwe";

	String dbname="redt";
	
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		
		
		 AttributeKey<Boolean> isLoginedKey=AttributeKeys.isLoginedKey;
		 
		
		if(!(msg instanceof MysqlPacket)) {
			super.channelRead(ctx, msg);
			return;
		}
		
		Channel channel=ctx.channel();
	
		
		Boolean isLogined=channel.attr(isLoginedKey).get();
		if(isLogined!=null&&isLogined) {
			ctx.fireChannelRead(msg);
			return;
		}
		
		
		MysqlPacket mysqlPacket=(MysqlPacket)msg;
		
		ByteBuf authHeader=mysqlPacket.getBytebuf();



		ServerAuthPacket  authPacket=new ServerAuthPacket();
		authPacket.setPacketLength(mysqlPacket.getPacketLength());
		authPacket.setPacketNumber(authPacket.getPacketNumber());




		decodeServerAuthPacket(authHeader,authPacket);
		
		AttributeKey<ServerAuthPacket> authPacketKey=AttributeKeys.authPacketKey;
		
		channel.attr(authPacketKey).set(authPacket);
		
		loginAuth(authPacket,username,password,channel).addListener((listener)->{
			if(listener.isSuccess()) {
				channel.attr(isLoginedKey).set(true);
			}
		});
		
		
		
		
	}
	
	

	
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 解析得到服务器的随机数以及服务器权能
	 * @param in
	 * @return
	 */
	public void  decodeServerAuthPacket(ByteBuf in,ServerAuthPacket authPacket) {
		
		byte protocol = in.readByte();

		byte[] buff = MysqlBuffUtil.readUtil0(in);

		String serverVersion = new String(buff);

		
		long threadId = MysqlBuffUtil.readLong(in);

		/**
		 * 
		 * 
		 * 
		 * 
		 * 如果协议版本大于9 就是固定的8位随机数 如果协议版本小等于9，就是读到\0
		 */
		if (protocol > 9) {

			buff = new byte[8];
			in.readBytes(buff);
			in.readByte();

		} else {
			buff = MysqlBuffUtil.readUtil0(in);
		}

		String seed = new String(buff);

		/**
		 * 
		 * 服务器权能标识 低16位
		 */
		int capbility1 = MysqlBuffUtil.readInt(in);

		/**
		 * 
		 * 字符编码 占8位
		 */
		byte charset = in.readByte();

		/**
		 * 
		 * 服务器状态 16位
		 */
		int serverStatus = MysqlBuffUtil.readInt(in);

		/**
		 * 
		 * 
		 * 服务器权能标识 高16位
		 */
		int capbility2 = MysqlBuffUtil.readInt(in);

		int authPluginDataLength = 0;

		String seedPart2 = null;

		/**
		 * 
		 * 合并高低权能标识
		 */
		int serverCapbility = capbility1 | capbility2 << 16;

		if ((serverCapbility & Capbilities.CLIENT_PLUGIN_AUTH) != 0) {
			// read length of auth-plugin-data (1 byte)
			authPluginDataLength = in.readByte() & 0xff;
		} else {
			// read filler ([00])
			in.readByte();
		}

		/**
		 * 跳过填充
		 */
		in.skipBytes(10);

		if ((serverCapbility & Capbilities.CLIENT_SECURE_CONNECTION) != 0) {

			StringBuilder newSeed;

			/**
			 * 
			 * 如果有挑战数总长度，就用总长度-前面的八位得到的位数 来读取第二部分挑战数
			 */
			if (authPluginDataLength > 0) {

				seedPart2 = MysqlBuffUtil.readString(in, "ASCII", authPluginDataLength - 8);
				seedPart2 = seedPart2.substring(0, seedPart2.length() - 1);
				newSeed = new StringBuilder(authPluginDataLength);
			} else {
				seedPart2 = new String(MysqlBuffUtil.readUtil0(in));
				newSeed = new StringBuilder(SEED_LENGTH);
			}
			newSeed.append(seed);
			newSeed.append(seedPart2);
			seed = newSeed.toString();
		}

		String pluginName = null;
		// Due to Bug#59453 the auth-plugin-name is missing the terminating NUL-char in
		// versions prior to 5.5.10 and 5.6.2.
		if ((serverCapbility & Capbilities.CLIENT_PLUGIN_AUTH) != 0) {

			if (protocol > 9) {
				pluginName = MysqlBuffUtil.readString(in, "ASCII", authPluginDataLength);
			} else {
				pluginName = new String(MysqlBuffUtil.readUtil0(in));
			}
		}
		

		
		
		authPacket.setProtocol(protocol);
		authPacket.setServerVersion(serverVersion);
		
		authPacket.setSeed(seed);
		authPacket.setCharset(charset);
		authPacket.setServerCapbility(serverCapbility);
		
		authPacket.setPluginName(pluginName);

		
	
	}

	
	
	
	public  ChannelFuture loginAuth(ServerAuthPacket authPacket, String username, String password,Channel channel) throws IOException{
	
		
		
	
		String seed=authPacket.getSeed();
		 
		String pluginName=authPacket.getPluginName();
		
		int serverCapbility=authPacket.getServerCapbility();
		
		
		int clientParam=0;
		
		int maxThreeBytes =256*256*256-1;


		//之命使用411协议
		clientParam|=Capbilities.CLIENT_PROTOCOL_41;
		
		clientParam|=Capbilities.CLIENT_LONG_PASSWORD; 
		
		clientParam|=Capbilities.CLIENT_LONG_FLAG;
		
		clientParam |= Capbilities.CLIENT_SECURE_CONNECTION;


		//使用插件式验证方式
		clientParam|=Capbilities.CLIENT_PLUGIN_AUTH;


		if(dbname.length()>0){
			clientParam|=Capbilities.CLIENT_CONNECT_WITH_DB;
		}

		
		ByteBuf byteBuf=channel.alloc().buffer();
	
		
		MysqlBuffUtil.writeLong(byteBuf, clientParam);
		MysqlBuffUtil.writeLong(byteBuf, maxThreeBytes);
		
		byteBuf.writeByte(33);
		
		byteBuf.writeBytes(new byte[23]);
		
		byteBuf.writeBytes(username.getBytes(CODE_PAGE_1252));
		byteBuf.writeByte(0x00);




		
	
		byte[] bschallenge=null;
		try {
			
			bschallenge = Security.scramble411(password, seed, CODE_PAGE_1252);
			MysqlBuffUtil.writeWithLength(byteBuf, bschallenge);
		} catch (Exception e) {
			e.printStackTrace();
		}


		if((clientParam&Capbilities.CLIENT_CONNECT_WITH_DB)!=0){
			if(dbname.length()>0){

				byteBuf.writeBytes(dbname.getBytes());
				byteBuf.writeByte(0x00);
			}
		}


		if((clientParam&Capbilities.CLIENT_PLUGIN_AUTH)!=0){

			byteBuf.writeBytes(pluginName.getBytes(CODE_PAGE_1252));
			byteBuf.writeByte(0x00);
		}

		ByteBuf writeBuff = channel.alloc().buffer();
		
		
		MysqlBuffUtil.writeLongInt(writeBuff, byteBuf.readableBytes());

		writeBuff.writeByte((byte) authPacket.getPacketNumber()+1);

		writeBuff.writeBytes(byteBuf);
		
		return channel.writeAndFlush(writeBuff);
	}
	

}
