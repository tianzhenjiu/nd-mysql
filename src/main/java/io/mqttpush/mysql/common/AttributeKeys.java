package io.mqttpush.mysql.common;

import io.mqttpush.mysql.packet.response.ServerAuthPacket;
import io.netty.util.AttributeKey;

public class AttributeKeys {
	
	
	
	public static AttributeKey<Boolean> isLoginedKey=AttributeKey.valueOf("isLoginedKey");
	
	
	public static AttributeKey<ServerAuthPacket> authPacketKey=AttributeKey.valueOf("authPacketKey");

}
