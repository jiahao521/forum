package com.kaishengit.web;

import com.google.gson.Gson;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jiahao0 on 2016/12/15.
 */
public class BaseServlet extends HttpServlet {

    public void forword(String path, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + path + ".jsp").forward(req,resp);
    }

    public void readText(String str, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(str);
        out.flush();
        out.close();
    }


    public void readJson(Object obj, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(new Gson().toJson(obj));
        out.flush();
        out.close();
    }
}
