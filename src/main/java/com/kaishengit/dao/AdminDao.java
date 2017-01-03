package com.kaishengit.dao;


import com.kaishengit.entity.Admin;
import com.kaishengit.util.DbHelp;
import org.apache.commons.dbutils.handlers.BeanHandler;
/**
 * Created by jiahao0 on 2017/1/2.
 */

public class AdminDao {


    public Admin findAdminByName(String adminName) {
        String sql = "select * from t_admin where adminname = ?";
        return DbHelp.query(sql,new BeanHandler<>(Admin.class),adminName);
    }
}
