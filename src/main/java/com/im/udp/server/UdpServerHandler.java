package com.im.udp.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
        System.out.println("content= " + new String(content, "utf-8"));
        SocketDto dto = JSON.parseObject(content, SocketDto.class);
        //保存 用户消息
        //发送消息保存

        if (dto.getSenderType() == 1) {
            data.remove(dto.getId());
            logger.info("客户端收到消息 is: " + JSON.toJSONString(dto));
        }
        if (dto.getSenderType() == 2) {
            SocketDto temp = new SocketDto();
            temp.setId(dto.getId());
            temp.setSenderType(2);
//            new InetSocketAddress("",1245);
            System.out.println("ip=" + msg.sender().getHostName() + ",port=" + msg.sender().getPort());
            ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(JSON.toJSONString(temp).getBytes()), msg.sender()));
            ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(JSON.toJSONString(temp).getBytes()), new InetSocketAddress("localhost", 2222)));

            logger.info("clientMessage is: " + JSON.toJSONString(dto));
        }
    }

    /*private void setMsg(){
        ChannelHandlerContext ctx, DatagramPacket msg
        ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(JSON.toJSONString(temp).getBytes()), msg.sender()));
    }*/


}
