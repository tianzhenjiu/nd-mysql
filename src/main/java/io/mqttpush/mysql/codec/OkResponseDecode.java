package io.mqttpush.mysql.codec;

import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.Capbilities;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.mqttpush.mysql.packet.response.OKPacket;
import io.mqttpush.mysql.packet.response.ServerAuthPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * 
 * @author tianzhenjiu
 *
 */
public class OkResponseDecode extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (!(msg instanceof MysqlPacket)) {
			super.channelRead(ctx, msg);
			return;
		}

		MysqlPacket mysqlPacket = (MysqlPacket) msg;

		ByteBuf in = mysqlPacket.getBytebuf();
		in.markReaderIndex();


		short header = in.readUnsignedByte();

		if (header != 0x00) {

			in.resetReaderIndex();
			ctx.fireChannelRead(msg);
			return;
		}
		
		
		Channel channel=ctx.channel();
		
		
		AttributeKey<ServerAuthPacket> authPacketKey=AttributeKeys.authPacketKey;
		
		ServerAuthPacket authPacket=channel.attr(authPacketKey).get();
		
		OKPacket okPacket=decodeOkPacket(in, authPacket);

		System.out.println(okPacket);


	}

	public OKPacket decodeOkPacket(ByteBuf in,ServerAuthPacket authPacket) {

		OKPacket okPacket = new OKPacket();
		okPacket.setHeader((byte) 0x00);

		
		okPacket.setAffectedRows(MysqlBuffUtil.readLenencInt(in));
		

		okPacket.setLastInsertid(MysqlBuffUtil.readLenencInt(in));
		
		
		int serverCapbility=authPacket.getServerCapbility();
		


		if(in.readableBytes()>4){
			if((serverCapbility&Capbilities.CLIENT_PROTOCOL_41)!=0) {

				okPacket.setStatusFlag(MysqlBuffUtil.readInt(in));
				okPacket.setWarningsCount(MysqlBuffUtil.readInt(in));

			}
		}

		if(in.readableBytes()>0){
			if((serverCapbility&Capbilities.CLIENT_SESSION_TRACK)!=0) {

				int strlen=(int)MysqlBuffUtil.readLenencInt(in);
				okPacket.setHumanReadableInfo(MysqlBuffUtil.readString(in, "utf-8", strlen));


				int statusFlag=okPacket.getStatusFlag();

				if((statusFlag&Capbilities.SERVER_SESSION_STATE_CHANGED)!=0) {

					strlen=(int)MysqlBuffUtil.readLenencInt(in);

					okPacket.setSessionStateChanges(MysqlBuffUtil.readString(in, "utf-8", strlen));
				}



			}else {
				okPacket.setHumanReadableInfo(new String(MysqlBuffUtil.readUtil0(in)));
			}
		}

		
		
		
		return okPacket;
	}


}
