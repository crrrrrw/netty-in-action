package com.crw.nettyinaction.codec.customcodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class MyServer {

    private static final int port = 9090;

    public static void main(String[] args) throws Exception {
        new MyServer().start();
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 添加 MyServerHandler 到 Channel 的 ChannelPipeline
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline()
                                    // 需指定解码类型
                                    .addLast(new ByteToLongDecoder2())
                                    .addLast(new LongToByteEncoder())
                                    .addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync();
            //System.out.println(MyServer.class.getName() + " started and listen on " + f.channel().localAddress());
            log.info("{} started and listen on {}", MyServer.class.getName(), f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
