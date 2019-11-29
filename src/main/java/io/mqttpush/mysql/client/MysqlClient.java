package io.mqttpush.mysql.client;

import io.mqttpush.mysql.codec.ErrorResponseDecode;
import io.mqttpush.mysql.codec.MysqlDecode;
import io.mqttpush.mysql.codec.OkResponseDecode;
import io.mqttpush.mysql.codec.ServerAuthDecode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MysqlClient {

	
	public static void main(String[] args) {
		
		
		//
		
			
			NioEventLoopGroup eventLoopGroup=new NioEventLoopGroup(2);
			
			
			Bootstrap bootstrap=new Bootstrap();
			
			bootstrap.group(eventLoopGroup).
					channel(NioSocketChannel.class);
			
			
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					
					
					ch.pipeline().addLast(new MysqlDecode());
					
					
					ch.pipeline().addLast(new ServerAuthDecode());
					
				
					ch.pipeline().addLast(new OkResponseDecode());


					ch.pipeline().addLast(new ErrorResponseDecode());
				}
				
			});
			
			bootstrap.connect("192.168.31.120", 3306);
		

		
		
	}
}
