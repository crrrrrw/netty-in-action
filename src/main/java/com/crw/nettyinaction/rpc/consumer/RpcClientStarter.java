package com.crw.nettyinaction.rpc.consumer;

import com.crw.nettyinaction.rpc.NettyClient;
import com.crw.nettyinaction.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClientStarter {


    // 定义协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        HelloService helloService = (HelloService) client.getBean(HelloService.class, providerName);
        String result = helloService.hello("world");
        log.info("helloService.hello result : {}", result);
    }

}
