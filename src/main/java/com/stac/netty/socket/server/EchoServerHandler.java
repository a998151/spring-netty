package com.stac.netty.socket.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 实现的核心业务逻辑
 * @Sharable 标志该Handler可以被多个Handler安全的共享。
 * @author  yzh
 */
@Component
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

    /**
     * 因为需要处理所有接收到的数据，所以重写了 channelRead()
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        logger.info("【Netty 接收到的数据】： " + in.toString(CharsetUtil.UTF_8));

        //业务逻辑处理
        //.........

        // 接收的消息发给发送者, 注意，这还没有冲刷数据

        ByteBuf out = Unpooled.buffer();
        out.writeBytes("你好啊".getBytes());
        ctx.writeAndFlush(out);
    }

    /**
     * 关闭
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 消息flush到远程节点并且关闭该channel。
        // 冲刷所有待审消息到远程节点。关闭通道后，操作完成
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 打印异常堆栈跟踪
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close(); //关闭通道
    }
}
