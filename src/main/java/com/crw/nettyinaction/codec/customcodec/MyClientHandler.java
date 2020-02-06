package com.crw.nettyinaction.codec.customcodec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("Client received: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Long msg = 123123123L;
        System.out.println("client send msg : " + msg);
        ctx.writeAndFlush(msg);
    }
}
