package com.im.udp.client;

import com.im.udp.server.SocketDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.UUID;

public class LogPushUdpClient2 {
    private final Bootstrap bootstrap;
    public final NioEventLoopGroup workerGroup;
    public static Channel channel;
    private static final Charset ASCII = Charset.forName("ASCII");

    public void start() throws Exception {
        try {
            channel = bootstrap.bind(2222).sync().channel();
            channel.closeFuture().await(1000);
        } finally {
//        	workerGroup.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public static LogPushUdpClient2 getInstance() {
        return logPushUdpClient.INSTANCE;
    }

    private static final class logPushUdpClient {
        static final LogPushUdpClient2 INSTANCE = new LogPushUdpClient2();
    }

    private LogPushUdpClient2() {
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//				pipeline.addLast(new StringDecoder(ASCII))  
//                .addLast(new StringEncoder(ASCII))
                        pipeline.addLast(new LogPushUdpClientHandler2());
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        LogPushUdpClient2.getInstance().start();
        SocketDto dto = new SocketDto();
        dto.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        dto.setPath("userAdd2");
        dto.setSenderType(2);
        dto.setContent("fdsafdsa2");
        LogPushUdpClientHandler2.sendMessage(dto, new InetSocketAddress("localhost", 12345));
    }
}

