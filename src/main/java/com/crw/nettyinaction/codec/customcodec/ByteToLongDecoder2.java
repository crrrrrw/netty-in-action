package com.crw.nettyinaction.codec.customcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * ReplayingDecoder 有一些局限性：
 * 1.不是所有的 ByteBuf操作都被支持，如果调用了一个不被支持的方法，将抛出 UnsupportedOperationException;
 * 2.ReplayingDecoder 在某些情况下可能稍慢于 ByteToMessageDecoder ,比如网络缓慢并且消息复杂时，消息会被
 * 拆成了多个碎片，速度变慢
 */
public class ByteToLongDecoder2 extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("ByteToLongDecoder2 decode...");
        // ReplayingDecoder 不需要判断数据是否足够读取，内部会进行处理判断
        out.add(in.readLong());

    }
}
