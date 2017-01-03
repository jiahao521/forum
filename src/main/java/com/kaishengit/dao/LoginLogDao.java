package com.kaishengit.dao;

import com.kaishengit.entity.LoginLog;
import com.kaishengit.util.DbHelp;
import org.apache.commons.dbutils.handlers.BeanHandler;
/**
 * Created by jiahao0 on 2016/12/26.
 */
public class LoginLogDao {
    public void save(LoginLog log) {
        String sql = "insert into t_login_log(ip,userid) values(?,?)";
        DbHelp.update(sql,log.getIp(),log.getUserId());
    }

    public LoginLog findLastLogin(Integer userid) {
        String sql = "select * from t_login_log where userid = ? order by logintime desc limit 0,1";
        return DbHelp.query(sql,new BeanHandler<>(LoginLog.class),userid);
    }
}
