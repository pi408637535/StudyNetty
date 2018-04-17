package main.java.com.study.nio.basic.client;

/**
 * Created by piguanghua on 4/17/18.
 * 运行流程
 * 1.创建 SocketChannel和Selector 且绑定监听
 * 2.selector监听是否有channel绑定或者有数据请求，如果有绑定就将其筛选出来
 * 3.对筛选出来的Channel判断 a.如果连接可用，判断是否连接完毕，完毕后，将监听设置为OP_READ
 *                        b.对于有数据请求的I/O 创建buffer -> channel读写的数据放置与 buffer中,读取
 */
public class TimeClient {
    public static void main(String[]agrs){
        int port = 8080;
        String hostAddr = "127.0.0.1";

        if(agrs!=null && agrs.length==1){
            try {
                port = Integer.valueOf(agrs[0]);
            }
            catch (NumberFormatException ex){}
        }else if(agrs!=null && agrs.length==2){
            try {
                hostAddr=agrs[0];
                port = Integer.valueOf(agrs[1]);
            }
            catch (NumberFormatException ex){}
        }

        for (int i=1;i<=1000;i++) {
            TimeClientHandle tc = new TimeClientHandle(hostAddr, port);
            Thread tt = new Thread(tc,"线程"+i);
            try {
                //   tt.sleep(1000);
                tt.start();
            }
            catch (Exception e){}
        }
    }
}
