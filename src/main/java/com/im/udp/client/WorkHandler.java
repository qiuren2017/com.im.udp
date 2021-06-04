package com.im.udp.client;

import com.alibaba.fastjson.JSON;
import com.im.udp.server.SocketDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class WorkHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkHandler.class);
    private static Map<String, SocketDto> data = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        // TODO 不确定服务端是否有response 所以暂时先不用处理
        final ByteBuf buf = datagramPacket.content();
        int readableBytes = buf.readableBytes();
        byte[] content = new byte[readableBytes];
        buf.readBytes(content);
        String serverMessage = new String(content);
        SocketDto socketDto = JSON.parseObject(serverMessage, SocketDto.class);
        //收到客服端的请求消息，主动response
        //收到客服端的response消息，不做处理
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
        LOGGER.info("reserveServerResponse is: " + serverMessage);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        super.userEventTriggered(ctx, evt);
        LOGGER.error("客户端异常2");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        LOGGER.error("客户端异常", cause);
    }

    private static DatagramPacket datagramPacket(SocketDto dto, InetSocketAddress inetSocketAddress) {
        ByteBuf dataBuf = Unpooled.copiedBuffer(JSON.toJSONString(dto), Charset.forName("UTF-8"));
        DatagramPacket datagramPacket = new DatagramPacket(dataBuf, inetSocketAddress);
        return datagramPacket;
    }

}
