package com.study.netty.basic.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by piguanghua on 4/16/18.
 */
public class TimeServer {
    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    /**
                     * backlog指定了内核为此套接口排队的最大连接个数，对应给定的监听接口，内核要维护两个队列：①未链接队列②以链接队列
                     * 队列判断依据：Tcp三次握手过程中，三个分节来分隔队列：服务器处于listen状态时，收到客户端connect连接时，在未完成队列中
                     *  创建一个新条目，然后用三次握手的第二个分节(服务器响应客户端)，次条目在第三分节到达前，一直保留在未完成连接队列中，若三
                     *  路握手完毕，将该条目移到已链接队列尾部，当进程调用accept时，从已完成队列头部去除一个条目给进行。当已完成队列为空进行进
                     *  入休眠态，知道有新条目进入已完成队列才唤醒。backlog是两个队列的总和
                     *
                     * Tcp参数设置完毕后，用户就设置 Handler
                     *   服务器端的 handler 也有两个, 一个是通过 handler() 方法设置 handler 字段, 另一个是通过 childHandler()
                     *   设置 childHandler 字段. 通过前面的 bossGroup 和 workerGroup 的分析,其实我们在这里可以大胆地猜测: handler字段与accept过程有关,即这个handler负责处理客户端的连接请求;而childHandler就是负责和客户端的连接的IO交互.
                     *
                     *
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new TimeServerHandler());
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new TimeServer().bind(port);
    }
}
