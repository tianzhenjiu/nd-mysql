package io.mqttpush.mysql.common;

import io.mqttpush.mysql.packet.MysqlPacket;
import io.mqttpush.mysql.packet.response.ServerAuthPacket;
import io.netty.util.AttributeKey;

public class AttributeKeys {
	
	
	
	public static AttributeKey<Boolean> isLoginedKey=AttributeKey.valueOf("isLoginedKey");
	
	
	public static AttributeKey<ServerAuthPacket> authPacketKey=AttributeKey.valueOf("authPacketKey");


	/**
	 * 记录被拆包的版本
	 */
	public static AttributeKey<MysqlPacket> haLfPacket=AttributeKey.valueOf("haLfPacket");


	public static AttributeKey<Options> optionKey=AttributeKey.valueOf("optionKey");

	public static AttributeKey<Integer> fieldCountKey= AttributeKey.valueOf("fieldCountKey");


}
