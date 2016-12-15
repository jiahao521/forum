package com.kaishengit.servise;

import com.kaishengit.dao.UserDao;
import com.kaishengit.entity.User;

/**
 * Created by jiahao0 on 2016/12/15.
 */
public class UserService {
    private UserDao userDao = new UserDao();

    //新用户注册
    public void saveNewWser(String username,String password,String email,String tel) {
        User user = new User();
        user.setUsernaem(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setState(User.USERSTATE_UNACTIVE);
        user.setAvatar(User.USER_DEFAULT_AVATOR);
        user.setTel(tel);

        userDao.save(user);

    }
}
