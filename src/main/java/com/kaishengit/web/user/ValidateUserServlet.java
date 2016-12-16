package com.kaishengit.web.user;

import com.kaishengit.servise.UserService;
import com.kaishengit.utils.StringUtils;
import com.kaishengit.web.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jiahao0 on 2016/12/16.
 */
@WebServlet("/validate/user")
public class ValidateUserServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        //处理URL中文乱码
        username = StringUtils.isToUTF8(username);

        UserService userService = new UserService();
        boolean result = userService.validateUsername(username);

        if(result) {
            readText("true",resp);
        }else {
            readText("false",resp);

        }
    }
}
