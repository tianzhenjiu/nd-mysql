package io.mqttpush.mysql.codec;

import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.Options;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author tianzhenjiu
 */
public class EofResponseDecode extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof MysqlPacket)) {
            super.channelRead(ctx, msg);
            return;
        }

        MysqlPacket mysqlPacket = (MysqlPacket) msg;

        ByteBuf in = mysqlPacket.getBytebuf();
        in.markReaderIndex();


        short header = in.readUnsignedByte();

        if (header != 0xfe) {

            in.resetReaderIndex();
            ctx.fireChannelRead(msg);
            return;
        }


        Channel channel=ctx.channel();


        System.out.println("报文结束--");
        Options options=channel.attr(AttributeKeys.optionKey).getAndRemove();

        in.skipBytes(in.readableBytes());
        if(options != null){



            switch (options){

                case Column_Definition:
                    channel.attr(AttributeKeys.optionKey).set(Options.Query);
                    break;

                    default :
                        break;
            }

        }



    }
}
