package io.mqttpush.mysql.codec;

import java.util.List;

import io.mqttpush.mysql.packet.MysqlPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * 
 * @author tianzhenjiu
 *
 */
public class MysqlDecode extends ByteToMessageDecoder {



//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//
//		if(!(msg instanceof ByteBuf)) {
//			super.channelRead(ctx, msg);
//		}
//
//		ByteBuf in=(ByteBuf)msg;
//
//		byte[] packetLens = new byte[3];
//
//		in.readBytes(packetLens);
//
//		int packetLength = packetLens[0] | packetLens[1] << 8 | packetLens[2] << 16;
//
//		byte packetNumber = in.readByte();
//
//		MysqlPacket mysqlPacket = new MysqlPacket();
//
//		mysqlPacket.setPacketLength(packetLength);
//		mysqlPacket.setPacketNumber(packetNumber);
//
//		ByteBuf byteBuf = ctx.alloc().buffer(mysqlPacket.getPacketLength());
//
//		in.readBytes(byteBuf);
//		mysqlPacket.setBytebuf(byteBuf);
//
//		ctx.fireChannelRead(mysqlPacket);
//	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


		byte[] packetLens = new byte[3];

		MysqlPacket mysqlPacketLast=null;
		while(in.readableBytes()>3){

			in.readBytes(packetLens);

			int packetLength = packetLens[0] | packetLens[1] << 8 | packetLens[2] << 16;

			byte packetNumber = in.readByte();

			MysqlPacket mysqlPacket = new MysqlPacket();

			mysqlPacket.setPacketLength(packetLength);
			mysqlPacket.setPacketNumber(packetNumber);

			ByteBuf byteBuf = ctx.alloc().buffer(mysqlPacket.getPacketLength());


			try{
				in.readBytes(byteBuf);
			}catch (Exception e){
				e.printStackTrace();
				System.out.println(mysqlPacketLast);
				System.exit(0);
			}
			mysqlPacket.setBytebuf(byteBuf);
			mysqlPacketLast=mysqlPacket;

			out.add(mysqlPacket);
		}


	}

}
