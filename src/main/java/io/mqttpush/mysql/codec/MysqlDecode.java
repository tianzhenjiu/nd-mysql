package io.mqttpush.mysql.codec;

import java.util.List;

import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
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



	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


		byte[] packetLens = new byte[3];

		MysqlPacket mysqlPacketLast=null;

		Channel channel=ctx.channel();

		MysqlPacket mysqlPacket=null;

		if((mysqlPacket=channel.attr(AttributeKeys.haLfPacket).getAndRemove())!=null){

			if(processHalfPacket(mysqlPacket,in,channel)){

				out.add(mysqlPacket);
				System.out.println(mysqlPacket);
			}

		}


		while(in.readableBytes()>3){

			in.readBytes(packetLens);

			int packetLength = (packetLens[0]&0xff) | (packetLens[1]&0xff) << 8 | (packetLens[2]&0xff) << 16;

			byte packetNumber = in.readByte();

			mysqlPacket = new MysqlPacket();

			mysqlPacket.setPacketLength(packetLength);
			mysqlPacket.setPacketNumber(packetNumber);



			int  remain=in.readableBytes();



			/**
			 *
			 *
			 * 说明此时发生了拆包
			 *
			 */
			ByteBuf byteBuf =ctx.alloc().buffer(mysqlPacket.getPacketLength());

			if(remain<mysqlPacket.getPacketLength()){

				mysqlPacket.setBytebuf(byteBuf);

				byteBuf.writeBytes(in);
				channel.attr(AttributeKeys.haLfPacket).set(mysqlPacket);

				return;
			}






			try{
				in.readBytes(byteBuf);
			}catch (Exception e){
				e.printStackTrace();
				System.out.println(mysqlPacketLast);
			}
			mysqlPacket.setBytebuf(byteBuf);
			mysqlPacketLast=mysqlPacket;

			System.out.println(mysqlPacket);
			out.add(mysqlPacket);
		}


	}


	/**
	 *
	 * 处理半包
	 * @param mysqlPacket
	 * @param in
	 * @param channel
	 * @return
	 */
	boolean processHalfPacket(MysqlPacket mysqlPacket,ByteBuf in,Channel channel){

		int packetLength=mysqlPacket.getPacketLength();

		int  remainByte=in.readableBytes();

		ByteBuf byteBuf = mysqlPacket.getBytebuf();


		int remainPacketLen=packetLength-byteBuf.readableBytes();


		/**
		 *
		 *
		 * 说明此时发生了拆包
		 *
		 */
		if(remainByte<remainPacketLen){

			byteBuf.writeBytes(in);
			channel.attr(AttributeKeys.haLfPacket).set(mysqlPacket);
			return false;

		}

		byteBuf.writeBytes(in, remainPacketLen);

		return true;

	}

}
