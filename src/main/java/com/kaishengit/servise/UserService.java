package com.kaishengit.servise;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kaishengit.dao.UserDao;
import com.kaishengit.entity.User;
import com.kaishengit.utils.Config;
import com.kaishengit.utils.EmailUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Created by jiahao0 on 2016/12/15.
 */
public class UserService {
    private UserDao userDao = new UserDao();

    //设置发送邮件的token缓存
    //expireAfterWrite设置失效时间
    private static Cache<String,String> cache =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(10, TimeUnit.HOURS)
                    .build();

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }


    //验证是否账号已被占用
    public boolean validateUsername(String username) {
        //保留用户名
        String name = Config.get("no.signup.usernames");
        List<String> nameList = Arrays.asList(name.split(","));
        if(nameList.equals(username)) {
            return false;
        }
        return userDao.findByUserName(username) == null;

    }

    //新用户注册
    public void saveNewUser(String username,String password,String email,String tel) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5Hex(Config.get("user.password.salt") + password));
        user.setEmail(email);
        user.setState(User.USERSTATE_UNACTIVE);
        user.setAvatar(User.USER_DEFAULT_AVATOR);
        user.setTel(tel);

        userDao.save(user);

        //用子线程发送邮件验证
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //发送激活邮件,加上随机uuid码
                String uuid = UUID.randomUUID().toString();
                String url = "https://www.kaishengit.com/user/active? = " + uuid;

                //放入缓存10小时
                cache.put(uuid,username);
                //发送激活html
                String html = "<h3> " +username+ "</h3>请点击该<a href='" + url+ ">链接</a>激活账号";

                EmailUtil.sendHtmlEmail(email,"用户激活邮件",html);


            }
        });
        thread.start();


    }






}
