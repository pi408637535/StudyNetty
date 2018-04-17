package com.study.netty.sticky.bug.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by piguanghua on 4/17/18.
 */
public class NettyStickyTimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    // 用于网络的读写操作
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "utf-8").substring(0, req.length - System.getProperty("line.separator").length());
        System.out.println("The time server receiver order:" + body + "; the counter is:" + ++counter);


        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
                System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime += System.getProperty("line.separator");   // System.getProperty("line.separator")，获取/n的作用
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);

        // 当客户端和服务端建立tcp成功之后，Netty的NIO线程会调用channelActive
        // 发送查询时间的指令给服务端。
        // 调用ChannelHandlerContext的writeAndFlush方法，将请求消息发送给服务端
        // 当服务端应答时，channelRead方法被调用
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();   // 它的作用是把消息发送队列中的消息写入SocketChannel中发送给对方
        // 为了防止频繁的唤醒Selector进行消息发送，Netty的write方法，并不直接将消息写入SocketChannel中
        // 调用write方法只是把待发送的消息发到缓冲区中，再调用flush，将发送缓冲区中的消息
        // 全部写到SocketChannel中。
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
