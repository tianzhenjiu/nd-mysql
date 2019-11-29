package io.mqttpush.mysql.codec;

import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.mysql.common.Options;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.mqttpush.mysql.packet.response.ColumnDefinitionPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author tianzhenjiu
 */
public class ColumnDefinitionDecode extends ChannelInboundHandlerAdapter {


    Options option=Options.Column_Definition;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof MysqlPacket)) {
            super.channelRead(ctx, msg);
            return;
        }

        Channel channel=ctx.channel();

        Options options=channel.attr(AttributeKeys.optionKey).get();

        if(options == null||options!=Options.Column_Definition){
            ctx.fireChannelRead(msg);
            return;
        }

        System.out.println("字段解析\t\t");
        MysqlPacket mysqlPacket=(MysqlPacket)msg;

        ByteBuf byteBuf=mysqlPacket.getBytebuf();

        if(byteBuf.readableBytes()==1){


            //is field count
            int fieldCount=(byteBuf.readByte()&0xff);
            System.out.println("field count->"+fieldCount);
            return;
        }

        ColumnDefinitionPacket columnDefinitionPacket=decodeColumnDefine(byteBuf);


        System.out.println(columnDefinitionPacket);

    }



    public ColumnDefinitionPacket decodeColumnDefine(ByteBuf byteBuf){

        ColumnDefinitionPacket columnDefinitionPacket=new ColumnDefinitionPacket();

        int length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setCatalog(MysqlBuffUtil.readString(byteBuf,"utf-8",length));

        length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setSchema(MysqlBuffUtil.readString(byteBuf,"utf-8",length));


        length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setTable(MysqlBuffUtil.readString(byteBuf,"utf-8",length));


        length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setOrgTable(MysqlBuffUtil.readString(byteBuf,"utf-8",length));



        length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setName(MysqlBuffUtil.readString(byteBuf,"utf-8",length));



        length=(int)MysqlBuffUtil.readLenencInt(byteBuf);
        columnDefinitionPacket.setOrgName(MysqlBuffUtil.readString(byteBuf,"utf-8",length));


        columnDefinitionPacket.setFixLength(byteBuf.readByte()&0xff);



        columnDefinitionPacket.setCharset(MysqlBuffUtil.readInt(byteBuf));


        columnDefinitionPacket.setColumnLength((int)MysqlBuffUtil.readLong(byteBuf));


        columnDefinitionPacket.setType((byteBuf.readByte()&0xff));



        columnDefinitionPacket.setFlags(MysqlBuffUtil.readInt(byteBuf));


        /**
         * 跳过这些字节
         */
        byteBuf.skipBytes(byteBuf.readableBytes());




        return columnDefinitionPacket;
    }
}
