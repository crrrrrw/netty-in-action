package com.crw.nettyinaction.transport;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 复习一下 java nio
 * Nio 客户端
 */
public class NioClient {
    //发送缓冲池和接收缓冲池
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    //注册器对象（选择器）
    private Selector selector = null;

    private InetSocketAddress socketAddress = new InetSocketAddress("localhost", 9999);

    private SocketChannel client = null;

    public NioClient() throws Exception {
        client = SocketChannel.open();

        client.configureBlocking(false);

        client.connect(socketAddress);

        selector = Selector.open();

        client.register(selector, SelectionKey.OP_CONNECT);

    }

    public void clientServer() throws Exception {
        if (client.isConnectionPending()) {
            client.finishConnect();
            System.out.println("客户端连接OK");

            client.register(selector, SelectionKey.OP_WRITE);

        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String string = (String) scanner.next();
            if ("".equals(string)) {
                continue;
            } else if ("exit".equals(string)) {
                System.exit(0);
            }

            handleKey(string);
        }

    }

    private void handleKey(String msg) throws Exception {
        boolean isWait = true;
        Iterator iterator = null;
        Set selectedKeys = null;
        try {
            while (isWait) {
                int select = selector.select();
                if (select == 0) {
                    continue;
                }
                selectedKeys = selector.selectedKeys();
                iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();

                    if (selectionKey.isValid() && selectionKey.isWritable()) {
                        sendBuffer.clear();

                        sendBuffer.put(msg.getBytes());

                        sendBuffer.flip();

                        client.write(sendBuffer);

                        client.register(selector, SelectionKey.OP_READ);

                    } else if (selectionKey.isValid() && selectionKey.isReadable()) {

                        receiveBuffer.clear();
                        int read = client.read(receiveBuffer);
                        if (read > 0) {
                            receiveBuffer.flip();
                            System.out.println("服务器返回的信息：" + new String(receiveBuffer.array(), 0, read));

                            client.register(selector, SelectionKey.OP_WRITE);

                            isWait = false;
                        }

                    }

                }

                iterator.remove();
            }
        } catch (Exception e) {
            ((SelectionKey) selectedKeys).cancel();
            client.socket().close();
            client.close();
            return;
        }

    }


    public static void main(String[] args) {
        try {
            new NioClient().clientServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
