//package io.mqttpush;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import io.mqttpush.mysql.common.MysqlBuffUtil;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.ByteBufAllocator;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//import io.netty.util.AttributeKey;
//
///**
// *
// * @author tianzhenjiu
// *
// */
//public class ChannelDecode extends ByteToMessageDecoder {
//
//
//
//	  private static final String CODE_PAGE_1252 = "Cp1252";
//
//
//
//	private static final int CLIENT_LONG_PASSWORD = 0x00000001; /* new more secure passwords */
//	private static final int CLIENT_FOUND_ROWS = 0x00000002;
//	private static final int CLIENT_LONG_FLAG = 0x00000004; /* Get all column flags */
//	protected static final int CLIENT_CONNECT_WITH_DB = 0x00000008;
//	private static final int CLIENT_COMPRESS = 0x00000020; /* Can use compression protcol */
//	private static final int CLIENT_LOCAL_FILES = 0x00000080; /* Can use LOAD DATA LOCAL */
//	private static final int CLIENT_PROTOCOL_41 = 0x00000200; // for > 4.1.1
//	private static final int CLIENT_INTERACTIVE = 0x00000400;
//	protected static final int CLIENT_SSL = 0x00000800;
//	private static final int CLIENT_TRANSACTIONS = 0x00002000; // Client knows about transactions
//	protected static final int CLIENT_RESERVED = 0x00004000; // for 4.1.0 only
//	protected static final int CLIENT_SECURE_CONNECTION = 0x00008000;
//	private static final int CLIENT_MULTI_STATEMENTS = 0x00010000; // Enable/disable multiquery support
//	private static final int CLIENT_MULTI_RESULTS = 0x00020000; // Enable/disable multi-results
//	private static final int CLIENT_PLUGIN_AUTH = 0x00080000;
//	private static final int CLIENT_CONNECT_ATTRS = 0x00100000;
//	private static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 0x00200000;
//	private static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 0x00400000;
//	private static final int CLIENT_SESSION_TRACK = 0x00800000;
//	private static final int CLIENT_DEPRECATE_EOF = 0x01000000;
//
//	protected static final int HEADER_LENGTH = 4;
//
//	protected static final int AUTH_411_OVERHEAD = 33;
//
//	public static final int SEED_LENGTH = 20;
//
//	public static final int MYSQL_COLLATION_INDEX_utf8 = 33;
//
//	private static final String MYSQL_CHARSET_NAME_utf8 = "utf8";
//
//
//
//	AttributeKey<Boolean> isLoginKey=AttributeKey.valueOf("isLoginKey");
//
//
//
//	@Override
//	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
//
//		Channel channel=ctx.channel();
//
//
//		Boolean isLogined=channel.attr(isLoginKey).get();
//
//		if(isLogined!=null&&
//				isLogined) {
//
//			return;
//		}
//
//
//		if (in.readableBytes() <= 0) {
//			return;
//		}
//
//
//		byte[] packetLens = new byte[3];
//		in.readBytes(packetLens);
//
//		int pLen = packetLens[0] | packetLens[1] << 8 | packetLens[2] << 16;
//
//		byte sequenceid = in.readByte();
//
//		byte protocol = in.readByte();
//
//		byte[] buff = MysqlBuffUtil.readUtil0(in);
//
//		String serverVersion = new String(buff);
//
//		if (in.readableBytes() <= 0) {
//			return;
//		}
//		long threadId = MysqlBuffUtil.readLong(in);
//
//		/**
//		 *
//		 *
//		 *
//		 *
//		 * ���Э��汾����9  ���ǹ̶���8λ�����
//		 * ���Э��汾С����9�����Ƕ���\0
//		 */
//		if (protocol > 9) {
//
//			buff = new byte[8];
//			in.readBytes(buff);
//			in.readByte();
//
//		} else {
//			buff = MysqlBuffUtil.readUtil0(in);
//		}
//
//		String seed = new String(buff);
//
//
//		/**
//		 *
//		 * ������Ȩ�ܱ�ʶ ��16λ
//		 */
//		int capbility1 = MysqlBuffUtil.readInt(in);
//
//
//		/**
//		 *
//		 * �ַ����� ռ8λ
//		 */
//		byte charset = in.readByte();
//
//		/**
//		 *
//		 * ������״̬  16λ
//		 */
//		int serverStatus = MysqlBuffUtil.readInt(in);
//
//
//		/**
//		 *
//		 *
//		 * ������Ȩ�ܱ�ʶ ��16λ
//		 */
//		int capbility2 = MysqlBuffUtil.readInt(in);
//
//
//
//		int authPluginDataLength = 0;
//
//		String seedPart2 = null;
//
//		/**
//		 *
//		 * �ϲ��ߵ�Ȩ�ܱ�ʶ
//		 */
//		int serverCapbility = capbility1 | capbility2 << 16;
//
//		if ((serverCapbility & CLIENT_PLUGIN_AUTH) != 0) {
//			// read length of auth-plugin-data (1 byte)
//			authPluginDataLength = in.readByte() & 0xff;
//		} else {
//			// read filler ([00])
//			in.readByte();
//		}
//
//		/**
//		 * �������
//		 */
//		in.skipBytes(10);
//
//		if ((serverCapbility & CLIENT_SECURE_CONNECTION) != 0) {
//
//			StringBuilder newSeed;
//
//			/**
//			 *
//			 * �������ս���ܳ��ȣ������ܳ���-ǰ��İ�λ�õ���λ�� ����ȡ�ڶ�������ս��
//			 */
//			if (authPluginDataLength > 0) {
//
//				seedPart2 = MysqlBuffUtil.readString(in, "ASCII", authPluginDataLength - 8);
//				seedPart2=seedPart2.substring(0,seedPart2.length()-1);
//				newSeed = new StringBuilder(authPluginDataLength);
//			} else {
//				seedPart2 = new String(MysqlBuffUtil.readUtil0(in));
//				newSeed = new StringBuilder(SEED_LENGTH);
//			}
//			newSeed.append(seed);
//			newSeed.append(seedPart2);
//			seed = newSeed.toString();
//		}
//
//		String pluginName = null;
//		// Due to Bug#59453 the auth-plugin-name is missing the terminating NUL-char in
//		// versions prior to 5.5.10 and 5.6.2.
//		if ((serverCapbility & CLIENT_PLUGIN_AUTH) != 0) {
//
//			if (protocol > 9) {
//				pluginName = MysqlBuffUtil.readString(in, "ASCII", authPluginDataLength);
//			} else {
//				pluginName = new String(MysqlBuffUtil.readUtil0(in));
//			}
//		}
//
//		System.out.println(pluginName);
//		System.out.println(serverVersion);
//		System.out.println(seed);
//
//		in.skipBytes(in.readableBytes());
//
//		loginAuth(pluginName, serverCapbility, "readt", seed, "Readt123!", ctx.alloc(), ctx.channel());
//
//
//
//		channel.attr(isLoginKey).set(true);
//
//	}
//
//
//
//
//	public  Future<?> loginAuth(String pluginName, int serverCapbility, String username, String seed, String password,
//			ByteBufAllocator bufAllocator, Channel channel) throws IOException{
//
//
//		int clientParam=0;
//		int maxThreeBytes =256*256*256-1;
//
//
//		clientParam|=CLIENT_PROTOCOL_41;
//
//		clientParam|=CLIENT_LONG_PASSWORD;
//
//		clientParam|=CLIENT_LONG_FLAG;
//
//		clientParam |= CLIENT_SECURE_CONNECTION;
//
//		clientParam|=CLIENT_PLUGIN_AUTH;
//
//
//		ByteBuf byteBuf=bufAllocator.buffer();
//
//
//		MysqlBuffUtil.writeLong(byteBuf, clientParam);
//		MysqlBuffUtil.writeLong(byteBuf, maxThreeBytes);
//
//		byteBuf.writeByte(33);
//
//		byteBuf.writeBytes(new byte[23]);
//
//		byteBuf.writeBytes(username.getBytes(CODE_PAGE_1252));
//		byteBuf.writeByte(0x00);
//
//
//
//
//		byte[] bschallenge=null;
//		try {
//
//			bschallenge = Security.scramble411(password, seed, CODE_PAGE_1252);
//			MysqlBuffUtil.writeWithLength(byteBuf, bschallenge);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		byteBuf.writeBytes(pluginName.getBytes(CODE_PAGE_1252));
//		byteBuf.writeByte(0x00);
//
//
//
//		ByteBuf writeBuff = bufAllocator.buffer();
//
//
//		MysqlBuffUtil.writeLongInt(writeBuff, byteBuf.readableBytes());
//
//		writeBuff.writeByte((byte) 1);
//
//		writeBuff.writeBytes(byteBuf);
//
//		return channel.writeAndFlush(writeBuff);
//	}
//}
