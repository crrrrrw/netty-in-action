package com.crw.nettyinaction.rpc.provider;

import com.crw.nettyinaction.rpc.api.HelloService;

public class HelloServiceImpl implements HelloService {
    public String hello(String msg) {
        return "hello," + msg;
    }
}
