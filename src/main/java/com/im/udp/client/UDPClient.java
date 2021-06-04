package com.im.udp.client;

import com.alibaba.fastjson.JSON;
import com.im.udp.server.SocketDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.UUID;

public class UDPClient {
    private static final Logger logger = LoggerFactory.getLogger(UDPClient.class);
    private final String host;//服务端 ip
    private final int port;//服务端端口
    private final int clientPort;//客户端端口
    private final NioEventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap;
    private Channel channel;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public UDPClient(String host, int port, int clientPort) {
        this.host = host;
        this.port = port;
        this.clientPort = clientPort;
        bootstrap = builder();
        connect();
    }

    private Bootstrap builder() {
        return new Bootstrap().group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(initializer());
    }

    private ChannelInitializer<NioDatagramChannel> initializer() {
        return new ChannelInitializer<NioDatagramChannel>() {
            protected void initChannel(NioDatagramChannel ch) throws Exception {
                ch.pipeline().addLast(
//                        getSslHandler(),
//                        new IdleStateHandler(30, 30, 30),
//                        new PacketDecoder(),
//                        new PacketEncoder(),
//                        new KeepAliveHandler(lock, result));
                        getWorkHandler()
                );
            }
        };
    }

    private WorkHandler getWorkHandler() {
        return new WorkHandler();
    }

    private void connect() {
        try {
            channel = bootstrap.bind(clientPort).sync().channel();
            channel.closeFuture().await(1000);
        } catch (Exception e) {
            logger.error("连接失败", e);
        } finally {
//            group.shutdownGracefully();
        }
    }


    private DatagramPacket datagramPacket(SocketDto dto, InetSocketAddress inetSocketAddress) {
        ByteBuf dataBuf = Unpooled.copiedBuffer(JSON.toJSONString(dto), Charset.forName("UTF-8"));
        DatagramPacket datagramPacket = new DatagramPacket(dataBuf, inetSocketAddress);
        return datagramPacket;
    }


    /*private ChannelFuture writeAndFlush(DatagramPacket out) {
        return out instanceof OutputBuffer
                ? channel.writeAndFlush(out).addListener((OutputBuffer) out)
                : channel.writeAndFlush(out);
    }*/

    /**
     * 发送数据包服务器无返回结果
     *
     * @param datagramPacket
     */
    private void senderInternal(final DatagramPacket datagramPacket) {
        if (this.channel != null) {
            this.channel.writeAndFlush(datagramPacket).addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    boolean success = future.isSuccess();
                    if (logger.isInfoEnabled()) {
                        logger.info("Sender datagramPacket result : " + success);
                    }
                }
            });
        } else {
            throw new NullPointerException("channel is null");
        }
    }

    public void send(final SocketDto dto, final InetSocketAddress inetSocketAddress) {
        if (dto == null) {
            throw new NullPointerException("msg is null");
        }
        // TODO 这一块的msg需要做处理 字符集转换和Bytebuf缓冲区
        senderInternal(datagramPacket(dto, inetSocketAddress));
    }


    public static void main(String[] args) {
        UDPClient client = new UDPClient("localhost", 12345, 5231);
//        client.send();
        SocketDto dto = new SocketDto();
        dto.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        dto.setPath("userAdd");
        dto.setSenderType(2);
        dto.setContent("fdsafdsa");
        client.send(dto, new InetSocketAddress(client.getHost(), client.getPort()));
    }
}
