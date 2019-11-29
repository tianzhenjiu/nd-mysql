package io.mqttpush.mysql.common;

import java.nio.charset.Charset;
import java.sql.SQLException;

import io.netty.buffer.ByteBuf;

/**
 * 按照mysql协议读取
 * 
 * @author tianzhenjiu
 *
 */
public class MysqlBuffUtil {



	public static long  readLenencInt(ByteBuf in) {

		int affectedRows1 = in.readUnsignedByte();

		if (affectedRows1 <=0xfb) {
			return affectedRows1;
		}

		switch (affectedRows1) {
			case 0xfc:
				return MysqlBuffUtil.readInt(in);

			case 0xfd:
				return MysqlBuffUtil.readLongInt(in);

			case 0xfe:
				return MysqlBuffUtil.readLong(in);

			case 0xff:
				return MysqlBuffUtil.readLongLong(in);

				default:
					return affectedRows1;
		}

	}

	public static final void writeWithLength(ByteBuf buffer, byte[] src) {
		int length = src.length;
		if (length < 251) {
			buffer.writeByte((byte) length);
		} else if (length < 0x10000L) {
			buffer.writeByte((byte) 252);
			writeInt(buffer, length);
		} else if (length < 0x1000000L) {
			buffer.writeByte((byte) 253);
			writeLongInt(buffer, length);
		} else {
			buffer.writeByte((byte) 254);
			writeLongLong(buffer, length);
		}
		buffer.writeBytes(src);
	}

	public static final void writeLongLong(ByteBuf byteBuf, long i){

		byteBuf.writeByte((byte) (i & 0xff));
		byteBuf.writeByte((byte) (i >>> 8 & 0xff));
		byteBuf.writeByte((byte) (i >>> 16 & 0xff));
		byteBuf.writeByte((byte) (i >>> 24 & 0xff));

		byteBuf.writeByte((byte) (i >>> 32 & 0xff));
		byteBuf.writeByte((byte) (i >>> 40 & 0xff));
		byteBuf.writeByte((byte) (i >>> 48 & 0xff));
		byteBuf.writeByte((byte) (i >>> 56 & 0xff));

	}

	public static final void writeLong(ByteBuf byteBuf, int i) {

		byteBuf.writeByte((byte) (i & 0xff));
		byteBuf.writeByte((byte) (i >>> 8 & 0xff));
		byteBuf.writeByte((byte) (i >>> 16 & 0xff));
		byteBuf.writeByte((byte) (i >>> 24 & 0xff));

	}

	public static void writeLongInt(ByteBuf byteBuf, int i) {

		byteBuf.writeByte((byte) (i & 0xff));
		byteBuf.writeByte((byte) (i >>> 8 & 0xff));
		byteBuf.writeByte((byte) (i >>> 16 & 0xff));
	}

	public static void writeInt(ByteBuf byteBuf, int i) {

		byteBuf.writeByte((byte) (i & 0xff));
		byteBuf.writeByte((byte) (i >>> 8 & 0xff));
	}

	/**
	 * 读取只到遇到\0
	 * 
	 * @param byteBuf
	 * @return
	 */
	public static byte[] readUtil0(ByteBuf byteBuf) {

		byte[] buff = new byte[8];

		byte[] copyBuff = null;

		int pos = 0;

		byte b = 0;
		while (byteBuf.readableBytes() > 0 && (b = byteBuf.readByte()) != 0) {

			buff[pos++] = b;
			if (pos >= buff.length) {
				copyBuff = new byte[buff.length * 2];
				System.arraycopy(buff, 0, copyBuff, 0, buff.length);
				buff = copyBuff;
				copyBuff = null;
			}

		}

		
		return buff;
	}

	public static final String readString(ByteBuf byteBuf, String encoding, int expectedLength) {
		if (byteBuf.readableBytes() < expectedLength) {
			throw new RuntimeException("not  readable");
		}

		byte[] bytes = new byte[expectedLength];
		byteBuf.readBytes(bytes);

		Charset charset = Charset.defaultCharset();

		if (encoding != null) {
			charset = Charset.forName(encoding);
		}

		return new String(bytes, charset);
	}

	public static final long readLong(ByteBuf byteBuf) {

		return ((long) byteBuf.readByte() & 0xff) | (((long) byteBuf.readByte() & 0xff) << 8)
				| ((long) (byteBuf.readByte() & 0xff) << 16) | ((long) (byteBuf.readByte() & 0xff) << 24);
	}
	
	
	public static final long readLongLong(ByteBuf byteBuf) {

		return (   (long) byteBuf.readByte() & 0xff) | (((long) byteBuf.readByte() & 0xff) << 8)
				| ((long) (byteBuf.readByte() & 0xff) << 16) | ((long) (byteBuf.readByte() & 0xff) << 24)
				| ((long) (byteBuf.readByte() & 0xff) << 32) | ((long) (byteBuf.readByte() & 0xff) << 40)
				| ((long) (byteBuf.readByte() & 0xff) << 48) | ((long) (byteBuf.readByte() & 0xff) << 56);
	}

	public static final int readLongInt(ByteBuf byteBuf) {

		return (byteBuf.readByte() & 0xff) | ((byteBuf.readByte() & 0xff) << 8) | ((byteBuf.readByte() & 0xff) << 16);
	}

	public static final int readInt(ByteBuf byteBuf) {

		return (byteBuf.readByte() & 0xff) | ((byteBuf.readByte() & 0xff) << 8);
	}

	
}
