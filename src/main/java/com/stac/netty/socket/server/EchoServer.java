package com.stac.netty.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 引导服务器本身
 * 1、绑定到服务器将在其上监听并接受传入连接请求的端口；
 * 2、配置 Channel ，以将有关的入站消息通知给 EchoServerHandler 实例。
 *
 * @author yzh
 */

@Service
public class EchoServer {

    @Autowired
    private EchoServerInit serverInit;

    private ServerBootstrap serverBootstrap;

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);

    /**
     * 启动 Netty Socket 服务
     * @throws InterruptedException
     */
    @PostConstruct
    public void initServer() throws InterruptedException {
        LOGGER.info("Netty Socket 开始启动");
        //创建 EventLoopGroup, 指定NioEventLoopGroup来接收和处理新的连接.
        EchoServerConfig serverConfig = new EchoServerConfig(8000,new NioEventLoopGroup(),new NioEventLoopGroup());
        serverBootstrap = serverInit.init(serverConfig).initServerBootstrap();
        serverInit.start(serverBootstrap);
    }

    @PreDestroy
    public void destroy(){
        LOGGER.info("释放资源");
        serverInit.destroy();
    }
}
