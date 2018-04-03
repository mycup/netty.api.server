package com.mezzomedia.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mezzomedia.server.core.ApplicationChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 
 * <pre>
 * </pre>
 *
 * @author skan
 * @since 2018. 2. 19.
 * @version 
 *
 * Copyright (C) 2018 by Mezzomedia.Inc. All right reserved.
 */
@Component
public class AdvertisementServer {

	
	@Value("${netty.server.port:8080}")
	private int tcpPort;
	
	
	public void start (){
			EventLoopGroup parentGroup = new NioEventLoopGroup(1);
			EventLoopGroup childGroup = new NioEventLoopGroup();
			try{
				ServerBootstrap sb = new ServerBootstrap();
				sb.group(parentGroup, childGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)						// 상세한 Channel 구현을 위해 옵션을 지정할 수 있습니다.
					//.handler(new LoggingHandler(LogLevel.INFO))	
					.childHandler(new ApplicationChannelInitializer());			// 새롭게 액세스된 Channel을 처리합니다.   ChannelInitializer는 특별한 핸들러로 새로운 Channel의 환경 구성을 도와 주는 것이 목적입니다. 

				// 인커밍 커넥션을 액세스하기 위해 바인드하고 시작합니다.
				ChannelFuture cf = sb.bind(tcpPort).sync();
				
				// 서버 소켓이 닫힐때까지 대기합니다.
				cf.channel().closeFuture().sync();
			}catch(Exception e){
				e.printStackTrace();
			}
			finally{
				parentGroup.shutdownGracefully();
				childGroup.shutdownGracefully();
			}
		}
}
