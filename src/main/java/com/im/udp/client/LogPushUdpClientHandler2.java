package com.im.udp.client;

import com.alibaba.fastjson.JSON;
import com.im.udp.server.SocketDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogPushUdpClientHandler2 extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(LogPushUdpClientHandler2.class);
    /**
     * 向服务器发送消息
     *
     * @param msg 按规则拼接的消息串
     * @param inetSocketAddress 目标服务器地址
     */
    private static Map<String, SocketDto> data = new HashMap<>();
//	private static Channel channel = com.im.udp.client.LogPushUdpClient.getInstance().getChannel();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当channel就绪后。  
        logger.info("client channel is ready!");
//       ctx.writeAndFlush("started");//阻塞直到发送完毕  这一块可以去掉的 
//       NettyUdpClientHandler.sendMessage("你好UdpServer", new InetSocketAddress("127.0.0.1",8888));
//       sendMessageWithInetAddressList(message);
//       logger.info("client send message is: 你好UdpServer");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        // TODO 不确定服务端是否有response 所以暂时先不用处理
        final ByteBuf buf = packet.content();
        int readableBytes = buf.readableBytes();

        byte[] content = new byte[readableBytes];

        buf.readBytes(content);
        String serverMessage = new String(content);
        SocketDto socketDto = JSON.parseObject(serverMessage, SocketDto.class);
        if (socketDto.getSenderType() == 2) {
            if (data.get(socketDto.getId()) != null) {
                data.remove(socketDto.getId());
            }
            System.out.println("服务端收到消息" + JSON.toJSONString(socketDto));
        }
        if (socketDto.getSenderType() == 1) {
            SocketDto dto = new SocketDto();
            dto.setId(socketDto.getId());
            datagramPacket(dto, new InetSocketAddress("127.0.0.1", 8888));
        }
        logger.info("reserveServerResponse is: " + serverMessage);
    }


    public static void sendMessage(final SocketDto dto, final InetSocketAddress inetSocketAddress) {
        if (dto == null) {
            throw new NullPointerException("msg is null");
        }
        // TODO 这一块的msg需要做处理 字符集转换和Bytebuf缓冲区
        senderInternal(datagramPacket(dto, inetSocketAddress));
    }

    /**
     * 发送数据包并监听结果
     *
     * @param datagramPacket
     */
    public static void senderInternal(final DatagramPacket datagramPacket, List<Channel> channelList) {
        for (Channel channel : channelList) {
            if (channel != null) {
                channel.writeAndFlush(datagramPacket).addListener(new GenericFutureListener<ChannelFuture>() {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        boolean success = future.isSuccess();
                        if (logger.isInfoEnabled()) {
                            logger.info("Sender datagramPacket result : " + success);
                        }
                    }
                });
            }
        }
    }

    /**
     * 组装数据包
     *
     * @param dto               消息串
     * @param inetSocketAddress 服务器地址
     * @return DatagramPacket
     */
    private static DatagramPacket datagramPacket(SocketDto dto, InetSocketAddress inetSocketAddress) {
        ByteBuf dataBuf = Unpooled.copiedBuffer(JSON.toJSONString(dto), Charset.forName("UTF-8"));
        DatagramPacket datagramPacket = new DatagramPacket(dataBuf, inetSocketAddress);
        return datagramPacket;
    }

    /**
     * 发送数据包服务器无返回结果
     *
     * @param datagramPacket
     */
    private static void senderInternal(final DatagramPacket datagramPacket) {
        logger.info("com.im.udp.client.LogPushUdpClient.channel" + LogPushUdpClient2.channel);
        if (LogPushUdpClient2.channel != null) {
            LogPushUdpClient2.channel.writeAndFlush(datagramPacket).addListener(new GenericFutureListener<ChannelFuture>() {
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

}
