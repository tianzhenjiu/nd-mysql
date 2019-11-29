package io.mqttpush.mysql.packet.response;

import io.mqttpush.mysql.packet.MysqlPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper=false)
@Data
@ToString
public class ErrorPacket extends MysqlPacket{

    /**
     * int<1>	header	[ff] header of the ERR packet
     int<2>	error_code	error-code
     if capabilities & CLIENT_PROTOCOL_41 {
     string[1]	sql_state_marker	# marker of the SQL State
     string[5]	sql_state	SQL State
     }
     string<EOF>	error_message
     *
     *
     */
	short header;

	int errorCode;

	char  sqlStateMarket;

	String sqlState;


	String  errorMessage;



}
