package com.kaishengit.dao;

import com.kaishengit.entity.User;
import com.kaishengit.utils.DbHelp;
import org.apache.commons.dbutils.handlers.BeanHandler;

/**
 * Created by jiahao0 on 2016/12/15.
 */
public class UserDao {

    public void save(User user) {
        String sql = "insert into t_user(username,password,email,state,avatar,tel) values (?,?,?,?,?,?)";
        DbHelp.update(sql,user.getUsername(),user.getPassword(),user.getEmail(),user.getState(),user.getAvatar(),user.getTel());
    }


    public User findByUserName(String username) {
        String sql = "select * from t_user where username = ?";
        return  DbHelp.query(sql,new BeanHandler<>(User.class),username);
    }

    public User findByEmail(String email) {
        String sql = "select * from t_user where email = ?";
        return DbHelp.query(sql,new BeanHandler<>(User.class),email);
    }
}
