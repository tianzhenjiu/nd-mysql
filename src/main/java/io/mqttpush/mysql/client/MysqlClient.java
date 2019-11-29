package io.mqttpush.mysql.client;

import io.mqttpush.mysql.codec.*;
import io.mqttpush.mysql.common.AttributeKeys;
import io.mqttpush.mysql.common.MysqlBuffUtil;
import io.mqttpush.mysql.common.Options;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Future;

public class MysqlClient {


	static String[] argsNames={"host","port","username","password","dbname"};
	static String[] defaultValues={"localhost","3306","root","root","test"};

	static Channel thisChannel;
	
	public static void main(String[] args) {






		Map<String,String> params=parseCmdArgs(args);

		String	host=params.get(argsNames[0]);
		Integer port=Integer.valueOf(params.get(argsNames[1]));
		String username=params.get(argsNames[2]);
		String password=params.get(argsNames[3]);
		String dbname=params.get(argsNames[4]);

			NioEventLoopGroup eventLoopGroup=new NioEventLoopGroup(2);
			
			
			Bootstrap bootstrap=new Bootstrap();
			
			bootstrap.group(eventLoopGroup).
					channel(NioSocketChannel.class);



			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					
					
					ch.pipeline().addLast(new MysqlDecode());
					
					
					ch.pipeline().addLast(new ServerAuthDecode(username,password,dbname));
					
				
					ch.pipeline().addLast(new OkResponseDecode());


					ch.pipeline().addLast(new ErrorResponseDecode());


					ch.pipeline().addLast(new EofResponseDecode());


					ch.pipeline().addLast(new ColumnDefinitionDecode());


					ch.pipeline().addLast(new TextDecode());
				}
				
			});
			
			thisChannel=bootstrap.connect(host, port).channel();



			Scanner scanner=new Scanner(System.in);

			String line=null;

			while(!"exit".equalsIgnoreCase(line=scanner.nextLine())){

				if(!thisChannel.isActive()){
					continue;
				}

				writeQueryStatement(thisChannel,line);


			}

			System.exit(0);
		

		
		
	}

	public static Map<String,String> parseCmdArgs(String[] args){

		Map<String,String> map=new HashMap<>();


		for (int i=0;i<argsNames.length;i++){

			String value=null;

			if(args.length>i){
				value=args[i];
			}
			else{
				value=defaultValues[i];
			}
			map.put(argsNames[i],value);
		}


		return map;
	}





	static Future<?> writeQueryStatement(Channel channel,String sql){


		ByteBuf writeBuff = channel.alloc().buffer();


		ByteBuf byteBuf=channel.alloc().buffer();

		byteBuf.writeByte(3);
		byteBuf.writeBytes(sql.getBytes());

		MysqlBuffUtil.writeLongInt(writeBuff, byteBuf.readableBytes());

		writeBuff.writeByte((byte)0);

		writeBuff.writeBytes(byteBuf);

		channel.attr(AttributeKeys.optionKey).set(Options.Column_Definition);

		return channel.writeAndFlush(writeBuff);
	}
}
