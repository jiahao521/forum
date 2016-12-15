<%--
  Created by IntelliJ IDEA.
  User: jiahao0
  Date: 2016/12/15
  Time: 16:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="header-bar">
    <div class="container">
        <a href="/home" class="brand">
            <i class="fa fa-reddit-alien"></i>
        </a>
        <ul class="unstyled inline pull-right">
            <c:choose>
                <c:when test="${not empty sessionScope.curr_user}">

                <li>
                    <a href="#">
                        <img src="http://oi2efjpoz.bkt.clouddn.com/default_avatar.png?imageView2/1/w/40/h/40/interlace/0/q/100"
                             class="img-circle" alt="">
                    </a>
                </li>
                <li>
                    <a href=""><i class="fa fa-plus"></i></a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-bell"></i></a>
                </li>
                <li>
                    <a href="setting.html"><i class="fa fa-cog"></i></a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-sign-out"></i></a>
                </li>
                </c:when>
                <c:otherwise>
                    <li><a href="/login"><i class="fa fa-sign-in"></i></a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</div>
