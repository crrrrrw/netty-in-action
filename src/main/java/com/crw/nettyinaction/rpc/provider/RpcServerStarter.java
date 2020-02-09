package com.crw.nettyinaction.rpc.provider;

import com.crw.nettyinaction.rpc.NettyServer;

/**
 * Rpc
 */
public class RpcServerStarter {

    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1", 9090);
    }
}
