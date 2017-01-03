package com.kaishengit.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kaishengit.dao.LoginLogDao;
import com.kaishengit.dao.NotifyDao;
import com.kaishengit.dao.UserDao;
import com.kaishengit.entity.LoginLog;
import com.kaishengit.entity.Notify;
import com.kaishengit.entity.User;
import com.kaishengit.exception.ServiceException;
import com.kaishengit.util.Config;
import com.kaishengit.util.EmailUtil;
import com.kaishengit.util.Page;
import com.kaishengit.util.StringUtils;
import com.kaishengit.vo.UserVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private NotifyDao notifyDao = new NotifyDao();

    //设置发送邮件的token缓存
    //expireAfterWrite设置失效时间
    private static Cache<String,String> cache =
            CacheBuilder.newBuilder()
            .expireAfterWrite(8, TimeUnit.HOURS)
            .build();

    //发送找回密码邮件的Token缓存
    private static Cache<String,String> passwordCache =
            CacheBuilder.newBuilder()
            .expireAfterWrite(60,TimeUnit.MINUTES)
            .build();

    //限制操作频率的缓存
    private static Cache<String,String> activeCache =
            CacheBuilder.newBuilder()
            .expireAfterWrite(60,TimeUnit.SECONDS)
            .build();


    //验证是否账号已被占用
    public boolean validateUserName(String username) {
        //保留用户名
        String name = Config.get("no.signup.usernames");
        List<String> nameList = Arrays.asList(name.split(","));

        if(nameList.contains(username)) {
            return false;
        }
        return  userDao.findByUserName(username) == null;
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    //新用户注册
    public void saveNewUser(String username, String password, String email, String phone) {

        User user = new User();
        user.setUsername(username);
        user.setAvatar(User.DEFAULT_AVATAR_NAME);
        user.setEmail(email);
        user.setPassword(DigestUtils.md5Hex(Config.get("user.password.salt") + password));
        user.setState(User.USERSTATE_UNACTIVE);
        user.setPhone(phone);

        userDao.save(user);

        //用子线程发送邮件验证
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //发送激活邮件,加上随机uuid码
                String uuid = UUID.randomUUID().toString();

                String url = //"http://localhost/user/active?_="+uuid;
                        "http://bbs.kaishengit.com/user/active?_="+uuid;
                //放入缓存6个小时
                cache.put(uuid,username);
                //发送激活html
                String html ="<h3> " + username + "</h3>请点击该<a href='" + url + "'>链接</a>激活账号" + "来自豪友网";

                EmailUtil.sendHtmlEmail(email,"用户激活邮件",html);
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

    /**
     * 用户登录
     * @param username
     * @param password
     * @param ip
     * @return user
     */
    public User login(String username, String password, String ip) {

        User user = userDao.findByUserName(username);

        if(user != null && DigestUtils.md5Hex(Config.get("user.password.salt") + password).equals(user.getPassword())) {

            if(user.getState().equals(User.USERSTATE_ACTIVE)) {
                //记录登录日志
                LoginLog log = new LoginLog();
                log.setIp(ip);
                log.setUserId(user.getId());

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

    /**
     * 用户找回密码
     * @param sessionId 客户端的sessionID,限制客户端的操作频率
     * @param type 找回密码方式 email | phone
     * @param value 电子邮件地址 | 手机号码
     */
    public void foundPassword(String sessionId, String type, String value) {

        if(activeCache.getIfPresent(sessionId) == null) {

            if("phone".equals(type)) {
                //TODO 待做功能，根据手机号码找回密码
            } else if("email".equals(type)) {

                User user = userDao.findByEmail(value);
                if(user != null) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uuid = UUID.randomUUID().toString();

                            //String url = "http://localhost/foundpassword/newpassword?token=" + uuid;
                            String url = "http://bbs.kaishengit.com/foundpassword/newpassword?token=" + uuid;

                            passwordCache.put(uuid,user.getUsername());

                            String html = user.getUsername()+"<br>请点击该<a href='"+url+"'>链接</a>进行找回密码操作，链接在60分钟内有效";
                            EmailUtil.sendHtmlEmail(value,"密码找回邮件",html);
                        }
                    });

                    thread.start();
                }
            }

            activeCache.put(sessionId,"xxxxx");
        } else {

            throw new ServiceException("操作频率过快");
        }
    }

    /**
     * 根据找回密码的链接获取找回密码的用户
     * @param token 根据token确认用户，以及链接是否真实
     * @return
     */
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

    /**
     * 重置密码
     * @param id
     * @param token 找回密码的token
     * @param password 新密码
     */
    public void resetPassword(String id, String token, String password) {
        if(passwordCache.getIfPresent(token) == null) {

            throw new ServiceException("token过期或错误");
        } else {

            String username = passwordCache.getIfPresent(token);
            User user = userDao.findByUserName(username);
            user.setPassword(DigestUtils.md5Hex(Config.get("user.password.salt")+password));
            userDao.update(user);

            //删除token
            passwordCache.invalidate(token);
            logger.info("{} 重置了密码",user.getUsername());
        }
    }

    /**
     * 修改用户邮箱
     * @param user
     * @param email
     */
    public void updateEmail(User user, String email) {
        user.setEmail(email);
        userDao.update(user);
    }

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     * @param user
     */
    public void updatePassword(User user, String oldPassword, String newPassword) {

        //密码加盐
        String salt = Config.get("user.password.salt");
        if(DigestUtils.md5Hex(salt + oldPassword).equals(user.getPassword())) {

            newPassword = DigestUtils.md5Hex(salt + newPassword);
            user.setPassword(newPassword);
            userDao.update(user);
        } else {

            throw new ServiceException("初始密码输入错误");
        }
    }

    /**
     * 修改头像
     * @param user
     * @param fileKey
     */
    public void updateAvatar(User user, String fileKey) {
        user.setAvatar(fileKey);
        userDao.update(user);
    }

    public List<Notify> findNotifyListByUser(User user) {

        return notifyDao.findByUserId(user.getId());
    }

    //更新用户状态
    public void updateNotifyStateByIds(String ids) {

        String idArray[] = ids.split(",");
        for (int i= 0 ;i <idArray.length;i++ ){

            Notify notify = notifyDao.findById(idArray[i]);
            notify.setState(Notify.NOTIFY_STATE_READ);
            notify.setReadtime(new Timestamp(DateTime.now().getMillis()));
            notifyDao.update(notify);
        }

    }

    public Page<UserVo> findUserList(Integer pageNo) {

        Integer count = userDao.count();
        Page<UserVo> page = new Page<>(count,pageNo);
        List<User> userList =  userDao.findAllUsers(page);
        List<UserVo> userVoList = new ArrayList<>();

       for (User user:userList){

           UserVo userVo = userDao.findUserVo(user.getId());
           userVoList.add(userVo);
       }
        page.setItems(userVoList);
        return page;
    }

    public void updateUserState(String userid, Integer userState) {

        if(StringUtils.isNumeric(userid)){

            User user = userDao.findById(Integer.valueOf(userid));
            user.setState(userState);
            userDao.update(user);
        } else {

            throw new ServiceException("参数异常");
        }
    }
}
