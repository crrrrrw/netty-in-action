package com.crw.nettyinaction.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartBeatServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    /**
                                     * 空闲状态处理器
                                     *  readerIdleTime：多久没读操作，就会发送一个心跳检测包
                                     *  writerIdleTime：多久没写操作，就会发送一个心跳检测包
                                     *  allIdleTime：多久即没读写操作，就会发送一个心跳检测包
                                     *
                                     *  心跳会触发一个 IdleStateEvent， IdleStateEvent 触发后会交给 IdleStateHandler 的下一个
                                     *  handler 处理，通过调用下一个 handler 的 userEventTriggered 方法，在该方法中处理 IdleStateEvent
                                     */
                                    .addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
                                    .addLast(new HeartBeatServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(9090).sync();
            future.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
