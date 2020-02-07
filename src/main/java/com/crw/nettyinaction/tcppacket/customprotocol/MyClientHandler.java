package com.crw.nettyinaction.tcppacket.customprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<MsgProtocol> {

    private int cnt;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgProtocol msg) throws Exception {
        log.info("Server received msg dataLength: {}", msg.getDataLength());
        log.info("Server received msg data: {}", new String(msg.getData(), CharsetUtil.UTF_8));
        log.info("Server received count : {} ", ++cnt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 使用客户端发送 5 条数据，使用自定义协议
        for (int i = 0; i < 5; i++) {
            String msg = "hello, world " + i;
            byte[] data = msg.getBytes(CharsetUtil.UTF_8);
            int dataLength = data.length;

            MsgProtocol protocol = new MsgProtocol();
            protocol.setDataLength(dataLength);
            protocol.setData(data);

            ctx.writeAndFlush(protocol);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
