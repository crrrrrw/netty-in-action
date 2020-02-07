package com.crw.nettyinaction.tcppacket.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("自定义解码器调用");
        // 字节码 ->
        int dataLength = in.readInt();

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        MsgProtocol protocol = new MsgProtocol();
        protocol.setDataLength(dataLength);
        protocol.setData(data);

        out.add(protocol);
    }
}
