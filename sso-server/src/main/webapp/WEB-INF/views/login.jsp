<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/10/29
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面模板</title>
    <meta name="description" content="Free Admin Template Based On Twitter Bootstrap 3.x">
    <!-- 包含头部信息用于适应不同设备 -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 包含 bootstrap 样式表 -->
    <link rel="stylesheet" href="http://apps.bdimg.com/libs/bootstrap/3.2.0/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <div class="tab-content-area">
        <div class="tab-pane">
            <form id="loginForm" action="/sso/login?redirect=${param.redirect}" method="post">
                <fieldset>
                    <legend>登录</legend>
                    <label>用户名</label>
                    <input type="text" name="username">
                    <label>密码</label>
                    <input type="password" name="password">
                    <button class="button" onclick="submit()">登录</button>
                </fieldset>
            </form>
        </div>
    </div>
</div>

<!-- 包含 jQuery 库 -->
<script src="http://apps.bdimg.com/libs/jquery/2.1.1/jquery.min.js"></script>
<!-- 合并了 Bootstrap JavaScript 插件 -->
<script src="http://apps.bdimg.com/libs/bootstrap/3.2.0/js/bootstrap.min.js"></script>

<script type="text/javascript">
    function submit() {
        $("#loginForm").submit();
    }
</script>
</body>
</html>
