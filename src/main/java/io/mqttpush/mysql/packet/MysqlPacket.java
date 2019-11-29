package io.mqttpush.mysql.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 
 * @author tianzhenjiu
 *
 */
@Data
public class MysqlPacket {

	/**
	 * mysql报文长度
	 */
	int packetLength;
	
	/**
	 *mysql报文序号
	 *
	 * see The sequence-id is incremented with each packet and may wrap around. It starts at 0 and is reset to 0 when a new command begins in the Command Phase.
	 *
	 *
	 *
	 * 所以如果是一条新的命令number需要重置为0
	 * 相同的命令自加
	 */
	byte packetNumber;
	
	
	
	ByteBuf bytebuf;
}
