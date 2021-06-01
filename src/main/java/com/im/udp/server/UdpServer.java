package com.im.udp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UdpServer {
    @Value("${netty.port}")
    private Integer port;
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup acceptGroup;
    private Channel channel;

    public void start() throws Exception {
        try {
            channel = bootstrap.bind("localhost", port).sync().channel();
            System.out.println("com.im.udp.server.UdpServer start success" + port);
            channel.closeFuture().await();
        } finally {
            acceptGroup.shutdownGracefully();
        }
    }


    public Channel getChannel() {
        return channel;
    }

    public static UdpServer getInstance() {
        return UdpServerHolder.INSTANCE;
    }

    private static final class UdpServerHolder {
        static final UdpServer INSTANCE = new UdpServer();
    }

    private UdpServer() {
        bootstrap = new Bootstrap();
        acceptGroup = new NioEventLoopGroup();
        bootstrap.group(acceptGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch)
                            throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new UdpServerHandler());
                    }
                });
    }

}
