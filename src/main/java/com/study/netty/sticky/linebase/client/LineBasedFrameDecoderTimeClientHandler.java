package com.study.netty.sticky.linebase.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

/**
 * Created by piguanghua on 4/17/18.
 */
public class LineBasedFrameDecoderTimeClientHandler extends ChannelHandlerAdapter{
    // 写日志
    private static final Logger logger =
            Logger.getLogger(LineBasedFrameDecoderTimeClientHandler.class.getName());

    private int counter;
    private byte[] req;

    public LineBasedFrameDecoderTimeClientHandler(){
        req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg)
            throws Exception{
        String body = (String)msg;
        System.out.println("Now is : " + body+";the countor is : "+ ++counter);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        // 当客户端和服务端建立tcp成功之后，Netty的NIO线程会调用channelActive
        // 发送查询时间的指令给服务端。
        // 调用ChannelHandlerContext的writeAndFlush方法，将请求消息发送给服务端
        // 当服务端应答时，channelRead方法被调用

        ByteBuf firstMessage=null;
        for (int i=0;i<100;i++){
            firstMessage = Unpooled.buffer(req.length);
            firstMessage.writeBytes(req);
            ctx.writeAndFlush(firstMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        logger.warning("message from:"+cause.getMessage());
        ctx.close();
    }
}
