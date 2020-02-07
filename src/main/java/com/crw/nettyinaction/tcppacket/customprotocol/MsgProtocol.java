package com.crw.nettyinaction.tcppacket.customprotocol;

import lombok.Data;

/**
 * 自定义协议
 */
@Data
public class MsgProtocol {
    private int dataLength;
    private byte[] data;
}
