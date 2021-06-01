package com.im.udp.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);

    private static Map<String, SocketDto> data = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
            throws Exception {
        // 接受client的消息
        logger.info("开始接收来自client的数据");
        final ByteBuf buf = msg.content();
        int readableBytes = buf.readableBytes();
        byte[] content = new byte[readableBytes];
        buf.readBytes(content);
        SocketDto dto = JSON.parseObject(content, SocketDto.class);

        if (dto.getSenderType() == 1) {
            data.remove(dto.getId());
            logger.info("客户端收到消息 is: " + JSON.toJSONString(dto));
        }
        if (dto.getSenderType() == 2) {
            SocketDto temp = new SocketDto();
            temp.setId(dto.getId());
            temp.setSenderType(2);
            ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(JSON.toJSONString(temp).getBytes()), msg.sender()));
            logger.info("clientMessage is: " + JSON.toJSONString(dto));
        }
    }

}
