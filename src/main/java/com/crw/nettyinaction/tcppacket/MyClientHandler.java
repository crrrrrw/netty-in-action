package com.crw.nettyinaction.tcppacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int cnt;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        String message = new String(buffer, CharsetUtil.UTF_8);
        log.info("Client received: {} ", message);
        log.info("Client received count : {} ", ++cnt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 使用客户端发送10条数据
        for (int i = 0; i < 10; i++) {
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello, world " + i, CharsetUtil.UTF_8);
            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
