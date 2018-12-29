package com.stac.netty.socket.client;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

/**
 * Echo 客户端
 * @Sharable 标记该类的实例可以被多个 Channel 共享
 */
@Component
@ChannelHandler.Sharable
public class EchoClientHandler  extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 当被通知 Channel  是活跃的时候，发送一条消息
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks !",CharsetUtil.UTF_8));
    }


    /**
     * 每当接收数据时，都会调用这个方法。
     * @param ctx
     * @param in
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.println("客户端接收到：" + in.toString(CharsetUtil.UTF_8));
    }


    /**
     * 发生异常时候，记录错误，并关闭 Channel
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
