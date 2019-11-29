package io.mqttpush.mysql.codec;

import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.mysql.common.Options;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 */
public class TextDecode extends ChannelInboundHandlerAdapter{


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        if (!(msg instanceof MysqlPacket)) {
            super.channelRead(ctx, msg);
            return;
        }




        Channel channel=ctx.channel();

        Options options=channel.attr(AttributeKeys.optionKey).get();

        if(options == null||options!=Options.Query){
            ctx.fireChannelRead(msg);
            return;
        }

        MysqlPacket mysqlPacket = (MysqlPacket) msg;



        ByteBuf content = mysqlPacket.getBytebuf();

        System.out.println("输出结果\t\t");
        while(content.readableBytes()>0){

            int fieldPreVal= (int)MysqlBuffUtil.readLenencInt(content);

            if(fieldPreVal==0xfb){
                System.out.print("NULL");
            }
            else{
                String fieleValue= MysqlBuffUtil.readString(content,"utf-8",fieldPreVal);
                System.out.print(fieleValue);
            }

            System.out.print("\t");
        }

        System.out.println();



    }
}
