package com.crw.nettyinaction.codec.customcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * decode 会根据接收到的数据被调用多次，直到确定了没有新的元素被添加到list中，
     * 或者 byteBuf 没有更多的可读字节为止。
     * 如果 list 里不为空，就会把 list 的内容传递给下一个 ChannelInboundHandler，
     * 该处理器的方法也会被调用多次
     *
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
