package com.study.aio.basic.server;

/**
 * Created by piguanghua on 4/17/18.
 */
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by IntelliJ IDEA 14.
 * User: karl.zhao
 * Time: 2015/11/26 0026.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyneTimeServerHandler> {
    @Override
    public  void completed(AsynchronousSocketChannel result,AsyneTimeServerHandler attachment){
        /**
         * 此处accept的作用在于：当接受完第一个后，如果后期又有了新客户端请求时，接受新请求。最终形成一个循环
         */
        attachment.asynchronousServerSocketChannel.accept(attachment,this);

        ByteBuffer buffer =ByteBuffer.allocate(1024);
        result.read(buffer,buffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc,AsyneTimeServerHandler attachment){
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
