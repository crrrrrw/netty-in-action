package com.crw.nettyinaction.codec.protobufdemo2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class EchoClientHandler extends SimpleChannelInboundHandler<DataInfo.BaseMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataInfo.BaseMsg msg) throws Exception {
        System.out.println("Client received: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 随机发送 Student 或 Teacher 对象
        int x = new Random().nextInt(2);
        DataInfo.BaseMsg msg;
        if (x == 0) {
            msg = DataInfo.BaseMsg.newBuilder()
                    .setDataType(DataInfo.BaseMsg.DataType.STUDENT)
                    .setStudent(DataInfo.Student.newBuilder().setId(2).setAge(23).setName("小李").build()).build();
        } else {
            msg = DataInfo.BaseMsg.newBuilder()
                    .setDataType(DataInfo.BaseMsg.DataType.TEACHER)
                    .setTeacher(DataInfo.Teacher.newBuilder().setId(5).setAge(35).setName("王老师").build()).build();
        }


        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
