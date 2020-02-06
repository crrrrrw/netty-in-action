package com.crw.nettyinaction.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.net.InetSocketAddress;

public class EchoServer {

    private static final int port = 9090;

    public static void main(String[] args) throws Exception {
        new EchoServer().start();// 呼叫服务器的 start() 方法
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline()
                                    // 需指定解码类型
                                    .addLast(new ProtobufDecoder(StudentVO.Student.getDefaultInstance()))
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
