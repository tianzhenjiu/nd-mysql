package io.mqttpush.mysql.codec;

import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.Capbilities;
import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.mysql.packet.MysqlPacket;
import io.mqttpush.mysql.packet.response.ErrorPacket;
import io.mqttpush.mysql.packet.response.ServerAuthPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 *
 * @author tianzhenjiu
 */
public class ErrorResponseDecode extends ChannelInboundHandlerAdapter{


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

        if (header != 0xff) {

            in.resetReaderIndex();
            ctx.fireChannelRead(msg);
            return;
        }



        Channel channel=ctx.channel();

        AttributeKey<ServerAuthPacket> authPacketKey= AttributeKeys.authPacketKey;

        ServerAuthPacket authPacket=channel.attr(authPacketKey).get();

        ErrorPacket errorPacket=decodeErrorPacket(in,authPacket);

        System.out.println(errorPacket);

    }




    public ErrorPacket decodeErrorPacket(ByteBuf in,ServerAuthPacket authPacket){

        ErrorPacket errorPacket=new ErrorPacket();
        errorPacket.setHeader((byte)0xff);

        errorPacket.setErrorCode(MysqlBuffUtil.readInt(in));


        int serverCapbility=authPacket.getServerCapbility();


        if(in.readableBytes()>6){
            if((serverCapbility& Capbilities.CLIENT_PROTOCOL_41)!=0) {

                errorPacket.setSqlStateMarket( (char)in.readByte());
                errorPacket.setSqlState(MysqlBuffUtil.readString(in,"utf-8",5));

            }
        }


        if(in.readableBytes()>0){

            errorPacket.setErrorMessage(new String(MysqlBuffUtil.readUtil0(in)));
        }


        return errorPacket;
    }
}
