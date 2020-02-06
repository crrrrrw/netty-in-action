package com.crw.nettyinaction.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler extends SimpleChannelInboundHandler<StudentVO.Student> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, StudentVO.Student student) throws Exception {
        System.out.println("Client received: " + student);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        StudentVO.Student z3 = StudentVO.Student.newBuilder().setId(1).setAge(18).setName("z3").build();

        ctx.writeAndFlush(z3);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
