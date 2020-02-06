package com.crw.nettyinaction.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket 使用一种被称作“Upgrade handshake（升级握手）”的机制将标准的 HTTP 或HTTPS 协议转为 WebSocket。
 * 因此，使用 WebSocket 的应用程序将始终以 HTTP/S 开始，然后进行升级。这种升级发生在什么时候取决于具体的应用;
 * 可以在应用启动的时候，或者当一个特定的 URL 被请求的时候。
 * 在我们的应用中，仅当 URL 请求以“/ws”结束时，我们才升级协议为WebSocket。否则，服务器将使用基本的 HTTP/S。
 * 一旦连接升级，之后的数据传输都将使用 WebSocket 。
 */
public class WebSoketServer {

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
                                    // 通过http协议编解码
                                    .addLast(new HttpServerCodec())
                                    // 以块方式写
                                    .addLast(new ChunkedWriteHandler())
                                    /**
                                     * 因为http数据在传输过程中是分段的，HttpObjectAggregator 用来将多个段聚合起来。
                                     * 当浏览器发送大量数据时，会发送多次http请求
                                     */
                                    .addLast(new HttpObjectAggregator(8192))
                                    /**
                                     * 1.对应websocket, 它的数据时以帧(frame) 的形式传递。
                                     * 2.浏览器请求 ws://localhost:9090/xxx 表示请求的uri。
                                     * 3.WebSocketFrame 定义了六种不同的 frame
                                     * 3. WebSocketServerProtocolHandler 核心功能是将 http 协议升级为 ws 协议，保持长连接。
                                     * 4. WebSocket 是通过一个 101 的状态码实现升级：
                                     * Request URL: ws://localhost:9090/hello
                                     * Request Method: GET
                                     * Status Code: 101 Switching Protocols
                                     * connection: upgrade
                                     * upgrade: websocket
                                     */
                                    .addLast(new WebSocketServerProtocolHandler("/hello"))
                                    .addLast(new MyTextWebSocketFrameHandler());
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
