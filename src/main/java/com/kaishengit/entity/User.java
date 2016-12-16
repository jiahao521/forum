package com.kaishengit.entity;

import javax.print.attribute.IntegerSyntax;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by jiahao0 on 2016/12/15.
 */
public class User implements Serializable {
    //账号状态
    //0 未激活 ；1 已激活（正常）；2 被禁用
    public static Integer USERSTATE_UNACTIVE = 0;
    public static Integer USERSTATE_ACTIVE = 1;
    public static Integer USERSTATE_DISABLED = 2;

    //新注册账号默认头像
    public static String USER_DEFAULT_AVATOR = "default_avatar.png";

    private Integer id;
    private String username;
    private String password;
    private String email;
    private Integer state;
    private Timestamp createtime;
    private String avatar;
    private String tel;

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }



    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
