package io.mqttpush;

import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestSeed {
	
	public static void main(String[] args) throws Exception {
		
		
//
//		String seed="U]Sg/Q-+-@=NP(s01DCa";
//
//		String password="Readt123!";
//
//
//		byte[] bs=Security.scramble411(password, seed,"Cp1252");
//
//		for (int i = 0; i < bs.length; i++) {
//			System.out.print(bs[i]+"\t");
//		}
//		System.out.println();


		ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer();

		byteBuf.writeByte(0xa2);

		byteBuf.writeByte(0x00);

		byteBuf.writeByte(0x00);


		//System.out.println(byteBuf.readUnsignedByte());

		System.out.println(byteBuf.readByte()&0xff);

		
		//System.out.println(new String(Security.scramble411("".getBytes(), "".getBytes())));
	}

}
