package com.crw.nettyinaction.codec;

import io.netty.channel.*;

public class EchoServerHandler extends SimpleChannelInboundHandler<StudentVO.Student> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, StudentVO.Student student) throws Exception {
        System.out.println("Server received: " + student);
        ctx.writeAndFlush(student);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
