package io.mqttpush.mysql.packet.response;

import io.mqttpush.mysql.packet.MysqlPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper=false)
@Data
@ToString
public class OKPacket extends MysqlPacket{

	
	byte  header;
	
	long affectedRows;
	
	long lastInsertid;
	
	
	int statusFlag;
	
	int warningsCount;
	
	
	String humanReadableInfo;
	
	String sessionStateChanges;
	
	/**
	 * 
	 * 
	Type	Name	Description
	int<1>	header	[00] or [fe] the OK packet header
	int<lenenc>	affected_rows	affected rows
	int<lenenc>	last_insert_id	last insert-id
	if capabilities & CLIENT_PROTOCOL_41 {
	  int<2>	status_flags	Status Flags
	  int<2>	warnings	number of warnings
	} elseif capabilities & CLIENT_TRANSACTIONS {
	  int<2>	status_flags	Status Flags
	}
	if capabilities & CLIENT_SESSION_TRACK {
	  string<lenenc>	info	human readable status information
	  if status_flags & SERVER_SESSION_STATE_CHANGED {
	    string<lenenc>	session_state_changes	session state info
	  }
	} else {
	  string<EOF>	info	human readable status information
	}**/
}
