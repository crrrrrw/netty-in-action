package com.crw.nettyinaction.codec.customcodec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("Client received: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Long msg = 123123123L;
        // System.out.println("client send msg : " + msg);
        // ctx.writeAndFlush(msg);

        /**
         * 1. 传送一个16字节的字符串，测试 ByteToLongDecoder decode方法调用次数 ： 2次
         * 2. 但是 LongToByteEncoder encode方法没有被调用，why？
         * 原因：LongToByteEncoder extends MessageToByteEncoder<Long> ，
         * MessageToByteEncoder的write方法中：
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                ByteBuf buf = null;
                 try {
                    if (this.acceptOutboundMessage(msg)) { // 判断当前数据是不是应该处理的类型,如果是则处理，不是则跳过 encode方法
                        I cast = msg;
                        buf = this.allocateBuffer(ctx, msg, this.preferDirect);

                         try {
                            this.encode(ctx, cast, buf);
                         } finally {
                            ReferenceCountUtil.release(msg);
                         }

                         if (buf.isReadable()) {
                            ctx.write(buf, promise);
                         } else {
                            buf.release();
                            ctx.write(Unpooled.EMPTY_BUFFER, promise);
                         }

                         buf = null;
                    } else {
                        ctx.write(msg, promise);
                    }
             } catch (EncoderException var17) {
                                throw var17;
             } catch (Throwable var18) {
                                throw new EncoderException(var18);
             } finally {
                if (buf != null) {
                    buf.release();
                }

            }

         }

         */
        ctx.writeAndFlush(Unpooled.copiedBuffer("shdkjhwuhsjkdh", CharsetUtil.UTF_8));
    }
}
