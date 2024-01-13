package org.jingtao8a;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelDemo1 {
    public static void main(String[] args) {
        //FileChannel
        try {
            FileChannel channel = new FileInputStream("D:/NettyRpc/data.txt").getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            channel.read(buffer);
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                System.out.println((char)b);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
