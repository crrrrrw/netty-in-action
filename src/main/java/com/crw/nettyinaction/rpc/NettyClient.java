package com.crw.nettyinaction.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NettyClient {

    private static String host = "127.0.0.1";
    private static int port = 9090;

    public static ExecutorService executor
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler client;

    public NettyClient() {
    }

    // init
    private static void initClient() {
        client = new NettyClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(client); // 这里需使用初始化的 NettyClient
                        }
                    });

            b.connect(host, port).sync();
        } catch (Exception e) {
            log.error("", e);
        }
//        finally {
//            try {
//                group.shutdownGracefully().sync();
//            } catch (InterruptedException e) {
//                log.error("", e);
//            }
//        }
    }

    /**
     * 获取一个代理对象
     *
     * @param serviceClass 接口类
     * @param providerName 协议头
     * @return
     */
    public Object getBean(final Class<?> serviceClass, final String providerName) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class<?>[]{serviceClass}
                , (proxy, method, args) -> {
                    if (Objects.isNull(client)) {
                        initClient();
                    }

                    client.setArgs(providerName + args[0]);
                    return executor.submit(client).get();
                });
    }

}
