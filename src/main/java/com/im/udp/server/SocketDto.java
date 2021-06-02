package com.im.udp.server;

public class SocketDto {
    /**
     * 唯一ID
     */
    private String uniqueId;
    /**
     * 数据业务id
     */
    private String id;
    /**
     * 业务路径
     */
    private String path;
    /**
     * 消息类型：1-普通消息，2-回复通知
     */
    private Integer msgType;
    /**
     * 1 服务端
     * 2 客户端
     */
    private Integer senderType;
    /**
     * 内容
     */
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
