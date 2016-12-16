package com.kaishengit.servise;

import com.kaishengit.dao.UserDao;
import com.kaishengit.entity.User;
import com.kaishengit.utils.Config;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jiahao0 on 2016/12/15.
 */
public class UserService {
    private UserDao userDao = new UserDao();

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

}
