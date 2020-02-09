package com.crw.nettyinaction.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
@Data
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private String result; // 调用结果
    private String args; // 请求参数

    // 调用 1
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    // 调用 3
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify(); // 拿到结果后，唤醒等待的线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 被代理对象调用，发送数据给服务器 -> wait -> 等待 notify -> 返回结果
     *
     * @return
     * @throws Exception
     */
    // 调用 2
    public synchronized Object call() throws Exception {
        context.writeAndFlush(args);
        wait();// 等待返回结果唤醒
        return result;
    }
}
