package com.stac.netty.socket.server;

import io.netty.channel.EventLoopGroup;

/**
 * Netty 配置类
 * @author yzh
 */
public class EchoServerConfig {

    /**
     *  端口编号
     */
    private int port;

    /**
     * 主线程池
     */
    private EventLoopGroup boss ;

    /**
     *  IO 操作线程池
     */
    private EventLoopGroup work ;

    public EchoServerConfig(int port, EventLoopGroup boss, EventLoopGroup work) {
        this.port = port;
        this.boss = boss;
        this.work = work;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public EventLoopGroup getBoss() {
        return boss;
    }

    public void setBoss(EventLoopGroup boss) {
        this.boss = boss;
    }

    public EventLoopGroup getWork() {
        return work;
    }

    public void setWork(EventLoopGroup work) {
        this.work = work;
    }
}
