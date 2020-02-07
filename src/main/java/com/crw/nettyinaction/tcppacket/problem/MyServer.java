package com.crw.nettyinaction.tcppacket.problem;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * TCP是以流的方式来处理数据，一个完整的包可能会被TCP拆分成多个包进行发送，也可能把小的封装成一个大的数据包发送。
 * <p>
 * TCP粘包/分包的原因：
 * - 应用程序写入的字节大小大于套接字发送缓冲区的大小，会发生拆包现象，而应用程序写入数据小于套接字缓冲区大小，
 * 网卡将应用多次写入的数据发送到网络上，这将会发生粘包现象；
 * - 进行MSS大小的TCP分段，当TCP报文长度-TCP头部长度>MSS的时候将发生拆包
 * - 以太网帧的payload（净荷）大于MTU（1500字节）进行ip分片。
 * <p>
 * 解决方法:
 * 消息定长：FixedLengthFrameDecoder 类
 * 包尾增加特殊字符分割：行分隔符类：LineBasedFrameDecoder 或 自定义分隔符类 ：DelimiterBasedFrameDecoder
 * 将消息分为消息头和消息体：LengthFieldBasedFrameDecoder 类。分为有头部的拆包与粘包、长度字段在前且有头部的拆包与粘包、多扩展头部的拆包与粘包。
 */
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
                                    .addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync();
            log.info("{} started and listen on {}", MyServer.class.getName(), f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
