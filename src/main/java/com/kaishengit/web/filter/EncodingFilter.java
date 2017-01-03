package com.kaishengit.web.filter;

import com.kaishengit.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by jiahao0 on 2016/12/16.
 * 字符过滤
 */
public class EncodingFilter extends AbstractFilter {

    private String encoding = "UTF-8";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String result = filterConfig.getInitParameter("encoding");

        if(StringUtils.isNotEmpty(result)) {
            encoding = result;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(encoding);
        servletResponse.setCharacterEncoding(encoding);

        //放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
