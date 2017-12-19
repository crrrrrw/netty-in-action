package com.crw.nettyinaction.transport;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 复习一下 java nio
 * Nio 服务端
 * <p>
 * capacity     缓冲区数组的总长度
 * position     下一个要操作的数据元素的位置
 * limit        缓冲区数组中不可操作的下一个元素的位置：limit<=capacity
 * mark         用于记录当前position的前一个位置或者默认是-1
 */
public class NioService {
    //通道
    private ServerSocketChannel serverSocketChannel = null;
    //发送缓冲池和接收缓冲池
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    //注册器对象（选择器）
    private Selector selector = null;
    //端口
    private int port = 9999;
    //对注册器上面的事件缓存
    private static Map msg = new HashMap<>();

    public NioService(int port) throws Exception {
        this.port = port;
        serverSocketChannel = serverSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //非阻塞
        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO服务器启动：监听端口：" + port);

    }

    //监听
    public void listen() throws Exception {
        while (true) {
            //判断是否有消息事件进来
            int select = selector.select();
            if (select == 0) {
                continue;
            }
            //拿到所有的事件连接
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                //处理每个事件消息
                handleKey(selectionKey);
                //处理完删除
                iterator.remove();

            }

        }
    }


    private void handleKey(SelectionKey selectionKey) throws Exception {
        SocketChannel client = null;
        try {
            //有效的，准备好的
            if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                //开始接收
                client = serverSocketChannel.accept();
                //非阻塞
                client.configureBlocking(false);
                //改变事件状态，读状态
                client.register(selector, SelectionKey.OP_READ);
                //有效的，可读的
            } else if (selectionKey.isValid() && selectionKey.isReadable()) {
                //先把接收缓存池清一下
                receiveBuffer.clear();
                //得到一个通道
                client = (SocketChannel) selectionKey.channel();
                //开始读
                int read = client.read(receiveBuffer);

                if (read > 0) {
                    String string = new String(receiveBuffer.array(), 0, read);

                    msg.put(selectionKey, string);

                    System.out.println("获取到客户端信息：" + string);
                    //读完之后，设置为可写状态
                    client.register(selector, SelectionKey.OP_WRITE);
                }
                //有效的，可写的
            } else if (selectionKey.isValid() && selectionKey.isWritable()) {

                if (!msg.containsKey(selectionKey)) {
                    return;
                }

                client = (SocketChannel) selectionKey.channel();
                //写之前清空
                sendBuffer.clear();

                sendBuffer.put(new String(msg.get(selectionKey) + "发送OK").getBytes());
                //将写模式转变为读模式
                sendBuffer.flip();

                client.write(sendBuffer);
                //状态设置为可读
                client.register(selector, SelectionKey.OP_READ);

            }

        } catch (Exception e) {
            try {
                selectionKey.cancel();
                client.socket().close();
                client.close();
                return;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }


    }


    public static void main(String[] args) {
        try {
            new NioService(9999).listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
