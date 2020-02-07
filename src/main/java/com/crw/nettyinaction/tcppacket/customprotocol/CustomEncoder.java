package com.crw.nettyinaction.tcppacket.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomEncoder extends MessageToByteEncoder<MsgProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MsgProtocol msg, ByteBuf out) throws Exception {
        log.info("自定义编码器调用");
        out.writeInt(msg.getDataLength());
        out.writeBytes(msg.getData());
    }
}
