package com.crw.nettyinaction.tcppacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;


@Slf4j
public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int cnt;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        String message = new String(buffer, CharsetUtil.UTF_8);
        log.info("Server received: {} ", message);
        log.info("Server received count : {} ", ++cnt);
        ctx.writeAndFlush(Unpooled.copiedBuffer("recall" + new Random().nextInt(100) + " ", CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
