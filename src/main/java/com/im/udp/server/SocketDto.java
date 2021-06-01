package com.im.udp.server;

public class SocketDto {
    /**
     * 数据业务id
     */
    private String id;
    /**
     * 业务路径
     */
    private String path;
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
}
