package com.crw.nettyinaction.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.Iterator;

import static io.netty.buffer.Unpooled.buffer;
import static io.netty.buffer.Unpooled.copiedBuffer;

public class ByteBufDemo {

    public static void main(String[] args) {
//        testHeadBuffer();
//        testDirectBuffer();
//        testCompositeBuffer();
//        testRandomAccessIndex();
//        testSlice();
//        testCopy();
//        testGetSet();
        testReadWrite();
//        testByteBuf();
//        byteBufCreateMethod();


    }

    /**
     * 堆缓冲区
     */
    private static void testHeadBuffer() {
        ByteBuf heapBuf = buffer(8);
        heapBuf.writeBytes("abcdefg".getBytes());
        if (heapBuf.hasArray()) { // 检查ByteBuf是否有backing array
            byte[] array = heapBuf.array(); // 获取数据引用
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex(); // 计算第一个字节的偏移量
            System.out.println("偏移量:" + offset);
            int length = heapBuf.readableBytes(); // 可读字节数
            System.out.println("可读字节数:" + length);
        }
    }

    /**
     * 直接缓冲区
     */
    private static void testDirectBuffer() {
        ByteBuf directBuf = Unpooled.directBuffer(8);
        directBuf.writeBytes("abcdefg".getBytes());
        if (!directBuf.hasArray()) { // 检查ByteBuf是否有backing array
            int length = directBuf.readableBytes(); // 可读字节数
            System.out.println("可读字节数:" + length);
            byte[] array = new byte[length];
            directBuf.getBytes(directBuf.readerIndex(), array);// 复制字节到数组
            System.out.println(new String(array));
        }
    }

    /**
     * 复合缓冲区
     */
    private static void testCompositeBuffer() {

        CompositeByteBuf compBuf = Unpooled.compositeBuffer(); // 复合缓冲区
        ByteBuf heapBuf = buffer(8); // 堆缓冲区
        heapBuf.writeBytes("abcd".getBytes());
        ByteBuf directBuf = Unpooled.directBuffer(16); // 直接缓冲区
        heapBuf.writeBytes("efgh".getBytes());
        compBuf.addComponents(heapBuf, directBuf); // 添加ByteBuf到CompositeByteBuf
        compBuf.removeComponent(0); // 删除第一个ByteBuf
        Iterator<ByteBuf> iter = compBuf.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
        // 使用数组访问数据
        if (!compBuf.hasArray()) {
            int length = compBuf.readableBytes();
            byte[] array = new byte[length];
            compBuf.getBytes(compBuf.readerIndex(), array);
            System.out.println(new String(array));
        }
    }

    /**
     * 随机访问索引
     */
    private static void testRandomAccessIndex() {
        ByteBuf buf = buffer(16);
        for (int i = 0; i < 16; i++) {
            buf.writeByte(i + 1);
        }
        //读数据
        for (int i = 0; i < buf.capacity(); i++) {
            System.out.print(buf.getByte(i) + ", ");
        }
    }

    /**
     * 派生一个副本。
     * 调用duplicate()、slice()、slice(int index, int length)等方法可以创建一个现有缓冲区的视图（现有缓冲区与原有缓冲区是指向相同内存）。
     * 衍生的缓冲区有独立的readerIndex和writerIndex和标记索引。
     * 如果需要现有的缓冲区的全新副本，可以使用copy()获得。
     */
    private static void testSlice() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty!", Charset.defaultCharset());
        ByteBuf sliced = buf.slice();
        System.out.println(sliced.toString(Charset.defaultCharset()));
        buf.setByte(0, 'J');
        System.out.println(buf.getByte(0) == sliced.getByte(0));
        sliced.setByte(0, 'N');
        System.out.println(buf.getByte(0) == sliced.getByte(0));
    }

    private static void testCopy() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty!", Charset.defaultCharset());
        ByteBuf sliced = buf.copy();
        System.out.println(sliced.toString(Charset.defaultCharset()));
        buf.setByte(0, 'J');
        System.out.println(buf.getByte(0) == sliced.getByte(0));
        sliced.setByte(0, 'N');
        System.out.println(buf.getByte(0) == sliced.getByte(0));
    }

    private static void testGetSet() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty!", Charset.defaultCharset());
        System.out.println(buf.readerIndex()); // 0
        System.out.println(buf.writerIndex()); // 6
        System.out.println(buf.getBoolean(0));  // true
        System.out.println(buf.getBoolean(6)); // false
        System.out.println(buf.getByte(0)); // 78 'N'
        buf.setByte(0, 'J');
        System.out.println(buf.getByte(0)); // 74 'J'
        System.out.println(buf.readerIndex()); // 0
        System.out.println(buf.writerIndex()); // 6
        System.out.println(buf.getUnsignedByte(1)); // 101 'e'
        System.out.println(buf.getInt(1)); // 1702130809
        System.out.println(buf.getLong(1)); // 7310596158722670592
        ByteBuf buf1 = buf.getBytes(0, new byte[10]);
        System.out.println(buf1.toString(buf1.readerIndex(), buf1.writerIndex(), Charset.defaultCharset())); // Jetty!
    }

    private static void testReadWrite() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty!", Charset.defaultCharset());
        System.out.println(buf.readerIndex()); // 0
        System.out.println(buf.writerIndex()); // 6
        boolean readBoolean = buf.readBoolean(); // readerIndex + 1
        System.out.println(readBoolean); // true
        System.out.println(buf.readerIndex()); // 1
        buf.readBytes(buf, 2); // readerIndex + 2 , writerIndex + 2
        System.out.println(buf.readerIndex()); // 3
        System.out.println(buf.writerIndex()); // 8
        buf.writeInt('s'); // writerIndex处写入int值， 并且 writerIndex + 4
        int readInt = buf.readInt(); // readerIndex + 4
        System.out.println(readInt); // 1954095461
        System.out.println(buf.readerIndex()); // 7
        System.out.println(buf.writerIndex()); // 12
        buf.writeLong('z'); // writerIndex处写入long值， 并且 writerIndex + 8
        long readLong = buf.readLong(); // readerIndex + 8
        System.out.println(readLong); // 8358680919573266432
        System.out.println(buf.readerIndex()); // 15
        System.out.println(buf.writerIndex()); // 20
        System.out.println(buf.getBytes(0, new byte[20]).toString(0, 20, Charset.defaultCharset())); // Netty!et   s       z

        // 其他方法
        System.out.println(buf.isReadable()); // true
        System.out.println(buf.isWritable()); // true
        System.out.println(buf.readableBytes()); // 5
        System.out.println(buf.writableBytes()); // 44
        System.out.println(buf.capacity()); // 64
        System.out.println(buf.maxCapacity()); // 2147483647 == Integer.MAX_VALUE
        System.out.println(buf.hasArray()); // true
        System.out.println(new String(buf.array())); // Netty!et   s       z
    }

    private static void testByteBuf() {
        //1、创建缓冲区
        ByteBuf heapBuffer = buffer(8);
        System.out.println("init... " + heapBuffer);

        //2、写入缓冲区内容
        heapBuffer.writeBytes("测试测试测试测试".getBytes());
        System.out.println("writed... " + heapBuffer);

        //3、创建字节数组
        byte[] b = new byte[heapBuffer.readableBytes()];
        System.out.println("create byte... " + b[11]);

        //4、复制内容到字节数组b
        heapBuffer.readBytes(b);
        System.out.println("copy byte... " + b[11]);

        //5、字节数组转字符串
        String str = new String(b);
        System.out.println("byte to string... " + str);
    }

    private static void byteBufCreateMethod() {
        // ByteBuf的四种声明方式
        ByteBuf heapBuf = buffer();
        System.out.println(heapBuf);

        ByteBuf directBuffer = Unpooled.directBuffer();
        System.out.println(directBuffer);

        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(new byte[128]);
        System.out.println(wrappedBuffer);

        ByteBuf copiedBuffer = copiedBuffer(new byte[128]);
        System.out.println(copiedBuffer);
    }

}
