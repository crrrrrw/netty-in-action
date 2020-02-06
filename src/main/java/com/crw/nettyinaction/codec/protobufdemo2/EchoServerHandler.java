package com.crw.nettyinaction.codec.protobufdemo2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoServerHandler extends SimpleChannelInboundHandler<DataInfo.BaseMsg> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DataInfo.BaseMsg msg) throws Exception {
        System.out.println("Server received: " + msg);

        DataInfo.BaseMsg.DataType dataType = msg.getDataType();
        if (dataType == DataInfo.BaseMsg.DataType.STUDENT) {
            DataInfo.Student student = msg.getStudent();
            System.out.println("<学生信息>  姓名 : " + student.getName() + "，年龄 : " + student.getAge());
        } else if (dataType == DataInfo.BaseMsg.DataType.TEACHER) {
            DataInfo.Teacher teacher = msg.getTeacher();
            System.out.println("<教师信息>  姓名 : " + teacher.getName() + "，年龄 : " + teacher.getAge());
        } else {
            System.out.println("wrong dateType");
        }

        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
