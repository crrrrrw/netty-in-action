package com.crw.nettyinaction.codec.customcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * @param ctx 上下文对象
     * @param in  入站的byteBuf
     * @param out List集合，将解码后的数据传给下一个handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("ByteToLongDecoder decode...");
        // 大于8字节才能转为Long
        if(in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
