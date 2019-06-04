package com.github.gantleman.shopd.entity;

import java.io.Serializable;

public class Cache implements Serializable {
    private String name;

    private Long index;

    private Integer port;

    private Long stamp;

    private String host;

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getStamp() {
        return stamp;
    }

    public void setStamp(Long stamp) {
        this.stamp = stamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }
}