package com.kaishengit.servise;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kaishengit.dao.LoginLogDao;
import com.kaishengit.dao.UserDao;
import com.kaishengit.entity.LoginLog;
import com.kaishengit.entity.User;
import com.kaishengit.exception.ServiceException;
import com.kaishengit.utils.Config;
import com.kaishengit.utils.EmailUtil;
import com.kaishengit.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Created by jiahao0 on 2016/12/15.
 */
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);


    private UserDao userDao = new UserDao();
    private LoginLogDao loginLogDao = new LoginLogDao();


    //设置发送邮件的token缓存
    //expireAfterWrite设置失效时间
    private static Cache<String, String> cache =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(10, TimeUnit.HOURS)
                    .build();

    //发送找回密码邮件的Token缓存
    private static Cache<String,String> passwordCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30,TimeUnit.MINUTES)
            .build();
    //限制操作频率的缓存
    private static Cache<String,String> activeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60,TimeUnit.SECONDS)
            .build();

    public User findByEmail(String email) {

        return userDao.findByEmail(email);
    }


    //验证是否账号已被占用
    public boolean validateUsername(String username) {
        //保留用户名
        String name = Config.get("no.signup.usernames");
        List<String> nameList = Arrays.asList(name.split(","));
        if (nameList.contains(username)) {
            return false;
        }
        return userDao.findByUserName(username) == null;

    }

    //新用户注册
    public void saveNewUser(String username, String password, String email, String tel) {
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
                String url = "https://www.kaishengit.com/user/active? = "+uuid;

                //放入缓存10小时
                cache.put(uuid, username);
                //发送激活html
                String html = "<h3> " + username + "</h3>请点击该<a href='" + url + ">链接</a>激活账号";

                EmailUtil.sendHtmlEmail(email, "用户激活邮件", html);
            }
        });
        thread.start();


    }
    //根据token激活对应账号
    public void activeUser(String token) {
        String userName = cache.getIfPresent(token);
        if(userName == null) {
            throw new ServiceException("token无效或已过期");
        } else {
            User user = userDao.findByUserName(userName);
            if(user == null) {
                throw new ServiceException("无法找到对应的账号");
            } else {
                user.setState(User.USERSTATE_ACTIVE);
                userDao.update(user);

                //将缓存中的键值对删除
                cache.invalidate(token);
            }
        }
    }

    public User login(String username, String password, String ip) {
        User user = userDao.findByUserName(username);
        if(user != null && DigestUtils.md5Hex(Config.get("user.password.salt") + password).equals(user.getPassword())) {
            if(user.getState().equals(User.USERSTATE_ACTIVE)) {
                //记录登录日志
                LoginLog log = new LoginLog();
                log.setIp(ip);
                log.setUserid(user.getId());

                loginLogDao.save(log);

                logger.info("{}登录了系统，IP：{}",username,ip);
                return user;

            } else if(User.USERSTATE_UNACTIVE.equals(user.getState())) {
                throw new ServiceException("该账号未激活");
            } else {
                throw new ServiceException("该账号已被禁用");
            }
        } else {
            throw new ServiceException("账号或密码错误");
        }
    }

    // 用户找回密码

    public void foundPassword(String sessionId, String type, String value) {
        if(activeCache.getIfPresent(sessionId) == null) {
            if("phone".equals(type)) {
                //TODO 根据手机号码找回密码
            } else if("email".equals(type)) {
                User user = userDao.findByEmail(value);
                if(user != null) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uuid = UUID.randomUUID().toString();
                            String url = "http://bbs.kaishengit.com/foundpassword/newpassword?token=" + uuid;

                            passwordCache.put(uuid,user.getUsername());
                            String html = user.getUsername()+"<br>请点击该<a href='"+url+"'>链接</a>进行找回密码操作，链接在30分钟内有效";
                            EmailUtil.sendHtmlEmail(value,"密码找回邮件",html);
                        }
                    });
                    thread.start();
                }
            }

            activeCache.put(sessionId,"xxx");
        } else {
            throw new ServiceException("操作频率过快");
        }
    }

    // 根据找回密码的链接获取找回密码的用户

    public User foundPasswordGetUserByToken(String token) {
        String username = passwordCache.getIfPresent(token);
        if(StringUtils.isEmpty(username)) {
            throw new ServiceException("token过期或错误");
        } else {
            User user = userDao.findByUserName(username);
            if(user == null) {
                throw new ServiceException("未找到对应账号");
            } else {
                return user;
            }
        }

    }

    //重置用户密码
    public void resetPassword(String id, String token, String password) {
        if(passwordCache.getIfPresent(token) == null) {
            throw new ServiceException("token过期或错误");
        } else {
            User user = userDao.findById(Integer.valueOf(id));
            user.setPassword(DigestUtils.md5Hex(Config.get("user.password.salt")+password));
            userDao.update(user);
            logger.info("{} 重置了密码",user.getUsername());
        }
    }


}
