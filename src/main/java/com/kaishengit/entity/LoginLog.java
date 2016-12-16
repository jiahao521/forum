package com.kaishengit.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by jiahao0 on 2016/12/15.
 */
public class LoginLog implements Serializable{
    private Integer id;
    private String ip;
    private Timestamp logintime;
    private Integer userid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp getLogintime() {
        return logintime;
    }

    public void setLogintime(Timestamp logintime) {
        this.logintime = logintime;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}
