package com.stac.netty.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Echo客户端
 * @author  yzh
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    //创建 Channel 时，向 ChannelPipeline 中添加一个 EchoClientHandler 实例
                    .handler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        public  void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new EchoClientHandler());
                        }
                    });

            //连接到远程节点，阻塞等待直到连接完成
            ChannelFuture f = bootstrap.connect().sync();
            //阻塞，直到 Channel 关闭
            f.channel().closeFuture().sync();
        } finally {
            //关闭线程池并且释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8000;
        new EchoClient(host, port).start();
    }
}
