package main.java.com.study.nio.basic.server;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA 14.
 * User: karl.zhao
 * Time: 2015/11/25 0025.
 *
 * 运行流程：
 * 1. a.创建一个：ServerSocketChannel作为Acceptor
 *    b.create Selector用于轮询监听是否有连接
 *    c.将selector与ServerSocketChannel绑定(或者说是注册),注册完毕后将监听作为OP_ACCEPT
 *    d.ServerSocketChannel设置为异步方式
 *
 *
 * 2.while(1){
 *      a.让selector轮询channel
 *      b.当有channel被连接时or有数据数据时 进入I/O操作
 * }
 *
 * 3.判断I/O操作仅限连接时有效的
 *   a.如果连接请求，将监听设置为 OP_READ
 *   b.如果是数据传输 先创建ByteBuffer用于缓冲数据->将channel要读写数据至于前面的buffer
 *                                             如果buffer为空就关闭此channel
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException ex) {
            }
        }

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
