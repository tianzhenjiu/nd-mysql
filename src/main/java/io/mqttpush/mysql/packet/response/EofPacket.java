package io.mqttpush.mysql.packet.response;

import io.mqttpush.mysql.packet.MysqlPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tianzhenjiu
 */
@EqualsAndHashCode(callSuper=false)
@Data
@ToString
public class EofPacket extends MysqlPacket {

    short header;

    int statusFlag;

    int warningsCount;
}
