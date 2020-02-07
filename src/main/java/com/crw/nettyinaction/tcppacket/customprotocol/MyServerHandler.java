package com.crw.nettyinaction.tcppacket.customprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;


@Slf4j
public class MyServerHandler extends SimpleChannelInboundHandler<MsgProtocol> {

    private int cnt;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MsgProtocol msg) throws Exception {
        log.info("Server received msg dataLength: {}", msg.getDataLength());
        log.info("Server received msg data: {}", new String(msg.getData(), CharsetUtil.UTF_8));
        log.info("Server received count : {} ", ++cnt);

        // 回复消息
        String respStr = "recall" + new Random().nextInt(100) + " ";
        MsgProtocol resp = new MsgProtocol();
        resp.setData(respStr.getBytes());
        resp.setDataLength(respStr.getBytes().length);

        ctx.writeAndFlush(resp);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
