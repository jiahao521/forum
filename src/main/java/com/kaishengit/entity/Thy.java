package com.kaishengit.entity;

import java.sql.Timestamp;

/**
 * Created by jiahao0 on 2017/1/2.
 */
public class Thy {

    private Integer userid;
    private Integer topicid;
    private Timestamp createtime;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getTopicid() {
        return topicid;
    }

    public void setTopicid(Integer topicid) {
        this.topicid = topicid;
    }

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }
}
