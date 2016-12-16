package com.kaishengit.web.user;

import com.kaishengit.web.BaseServlet;
import org.apache.commons.codec.binary.Base32;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jiahao0 on 2016/12/16.
 */
@WebServlet("/validate/email")
public class ValidateEmailServlet extends BaseServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
