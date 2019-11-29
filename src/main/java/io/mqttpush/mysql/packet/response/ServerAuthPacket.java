package io.mqttpush.mysql.packet.response;

import io.mqttpush.mysql.packet.MysqlPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper=false)
@Data
@ToString
public class ServerAuthPacket extends MysqlPacket{
	
	
	
	byte  protocol;
	
	String serverVersion;
	
	
	long threadId;
	
	
	String seed;
	
	
	byte charset;
	
	
	int serverStatus;
	
	
	int serverCapbility;
	
	String pluginName;
}
