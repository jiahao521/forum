package com.kaishengit.dao;


import com.kaishengit.entity.Thy;
import com.kaishengit.util.DbHelp;
import org.apache.commons.dbutils.handlers.BeanHandler;

/**
 * Created by jiahao0 on 2017/1/2.
 */
public class ThyDao {

    public Thy findByTopicIdAndUserId(Integer userid, Integer topicid){
        String sql = "select * from t_thy where userid = ? and topicid = ?";
        return DbHelp.query(sql,new BeanHandler<>(Thy.class),userid,topicid);
    }


    public void addThy(Thy thy) {
        String sql = "insert into t_thy (userid,topicid)values (?,?)";
        DbHelp.update(sql,thy.getUserid(),thy.getTopicid());
    }


    public void deleteThy(Integer userid, String topicId) {
        String sql = "delete from t_thy where userid = ? and topicid = ?";
        DbHelp.update(sql,userid,Integer.valueOf(topicId));
    }


}
