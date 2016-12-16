package com.kaishengit.web.filter;


import javax.servlet.*;
import java.io.IOException;

/**
 * Created by jiahao0 on 2016/12/16.
 */
public class AbstractFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
