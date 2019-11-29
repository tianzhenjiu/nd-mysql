package io.mqttpush.mysql.packet.response;

import io.mqttpush.mysql.packet.MysqlPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author tianzhenjiu
 */
@EqualsAndHashCode(callSuper=false)
@Data
@ToString
public class ColumnDefinitionPacket extends MysqlPacket {

    /**
     * lenenc_str     catalog
     lenenc_str     schema
     lenenc_str     table
     lenenc_str     org_table
     lenenc_str     name
     lenenc_str     org_name
     lenenc_int     length of fixed-length fields [0c]
     2              character set
     4              column length
     1              type
     2              flags
     1              decimals
     2              filler [00] [00]
     if command was COM_FIELD_LIST {
     lenenc_int     length of default-values
     string[$len]   default values
     }
     *
     */

    String catalog;

    String schema;

    String table;


    String orgTable;


    String name;

    String orgName;

    int fixLength;

    int charset;

    int columnLength;


    int  type;

    int flags;

    int decimals;

}
