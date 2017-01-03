package com.kaishengit.web.user;

import com.kaishengit.entity.User;
import com.kaishengit.service.UserService;
import com.kaishengit.util.StringUtils;
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
        //处理URL中的中文问题
        String username = req.getParameter("username");
        username = StringUtils.isoToUtf8(username);

        UserService userService = new UserService();
        boolean result = userService.validateUserName(username);

        if(result) {
            renderText("true",resp);
        } else {
            renderText("false",resp);
        }

    }
}
