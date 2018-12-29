package com.stac.netty.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * Netty  Socket  初始化
 * @author yzh
 */
@Service
public class EchoServerInit {

    private volatile int port;

    private volatile EventLoopGroup boss ;

    private volatile EventLoopGroup work ;

    private volatile Channel channel;

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServerInit.class);

    /**
     * 初始化对象
     * @param serverConfig
     * @return
     * @throws IllegalStateException
     */
    public EchoServerInit  init(EchoServerConfig serverConfig) throws IllegalStateException{

        if (serverConfig.getPort() == 0 ) {
            throw new IllegalStateException("port 没设置");
        }
        if (serverConfig.getBoss() == null) {
            throw new IllegalStateException("boss 线程池没设置");
        }
        if (serverConfig.getWork() == null){
            throw new IllegalStateException("work 线程池没设置");
        }
        this.port = serverConfig.getPort();
        this.boss = serverConfig.getBoss();
        this.work = serverConfig.getWork();
        return this;
    }

    public ServerBootstrap initServerBootstrap(){
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建 bootstrap
        ServerBootstrap bootstrap;
        try {
            //创建 bootstrap
            bootstrap = new ServerBootstrap();
            bootstrap.group(boss,work)
                    // channel 的类型指定为NioServerSocketChannel.
                    //指定所使用的传输 channel
                    .channel(NioServerSocketChannel.class)
                    // 设置本地地址, 服务器将绑定到这个地址监听新的连接请求.
                    .localAddress(new InetSocketAddress(port))
                    // 当新的连接到来时, 一个新的子Channel将会被创建, 而ChannelInitializer将会把EchoServerhandler
                    // 添加到该子handler的ChannelPipeline中。
                    //这一步是关键！！！这个必须要设置，之后的 bind() 方法中会检测（源码）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            //EchoServerHandler被标记为 @Shareable，所以我们可以总是使用同样的实例
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
           return bootstrap;
        }catch (Exception e){
            LOGGER.error("【配置ServerBootstrap出错】：" + e.getMessage() );
            return null;
        }
    }

    /**
     * 启动 Netty Socket
     * @param bootstrap
     * @throws InterruptedException
     */
    public void start(ServerBootstrap bootstrap) throws InterruptedException {
        try {
            // 绑定服务器，并等待绑定完成。 调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();
            LOGGER.info("Netty Socket 启动完成，监听端口：" + port);
            // 阻塞直到服务器的Channel关闭。获取 Channel 的CloseFuture，并且阻塞当前线程直到它完成
            channel =future.channel();
            channel.closeFuture().sync();
            LOGGER.info("Netty Socket 关闭");
        }catch (InterruptedException  e){
            LOGGER.error("【出现异常】: " + e.getMessage());
        }finally {
            // 关闭EventLoopGroup,并且释放所有的资源，包括被创建的线程。
            LOGGER.info("【释放资源】");
            boss.shutdownGracefully().sync();
            work.shutdownGracefully().sync();
        }
    }

    /**
     * 关闭服务，释放资源
     */
    public void destroy() {
        LOGGER.info("shutdown Netty Server start ~~");
        if (channel != null){
            channel.close();
        }
        if(boss!=null){
            boss.shutdownGracefully();
        }
        if (work != null){
            work.shutdownGracefully();
        }
        LOGGER.info("shutdown Netty Server success !");
    }
}
