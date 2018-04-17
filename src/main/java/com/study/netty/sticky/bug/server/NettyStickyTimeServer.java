package com.study.netty.sticky.bug.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by piguanghua on 4/17/18.
 */
public class NettyStickyTimeServer {
    public void bind(int port)throws Exception{
        /* 配置服务端的NIO线程组 */
        // NioEventLoopGroup类 是个线程组，包含一组NIO线程，用于网络事件的处理
        // （实际上它就是Reactor线程组）。
        // 创建的2个线程组，1个是服务端接收客户端的连接，另一个是进行SocketChannel的
        // 网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup WorkerGroup = new NioEventLoopGroup();

        try {
            // ServerBootstrap 类，是启动NIO服务器的辅助启动类,降低服务端开发难度
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,WorkerGroup)
                    .channel(NioServerSocketChannel.class)   //将Channel设置为ServerChannel
                    .option(ChannelOption.SO_BACKLOG,1024) //设置TCP参数？
                    .childHandler(new ChildChannelHandler());

            // 绑定端口,同步等待成功  作用等待绑定完成
            ChannelFuture f= b.bind(port).sync();

            // 阻塞式 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }finally {
            // 释放线程池资源
            bossGroup.shutdownGracefully();
            WorkerGroup.shutdownGracefully();
        }
    }

    //类似于Reactor模式中Handler 处理网络I/O事件 添加责任链
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        @Override
        protected  void initChannel(SocketChannel arg0)throws Exception{
            arg0.pipeline().addLast(new NettyStickyTimeServerHandler());
        }
    }

    public static void main(String[]args)throws Exception{
        int port = 8080;
        if(args!=null && args.length>0){
            try {
                port = Integer.valueOf(args[0]);
            }
            catch (NumberFormatException ex){}
        }
        new NettyStickyTimeServer().bind(port);
    }

}
