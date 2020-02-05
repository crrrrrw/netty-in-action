package com.crw.nettyinaction.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }

            if (Objects.nonNull(eventType)) {
                int cnt = count.incrementAndGet();
                System.out.println(ctx.channel().remoteAddress() + " 超时了，发生了" + eventType);
                if (cnt == 3) ctx.channel().close();
            }

        }
    }
}
