<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>shopd</title>
    <script src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/css/bootstrap.min.css">
    <script src="${pageContext.request.contextPath}/css/bootstrap/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/sort.js"></script>
    <script src="${pageContext.request.contextPath}/js/holder.js"></script>
    <!-- 	<script>
            $(document).ready(function(){
                $(".list-group-item").hover(function(){
                    $(this).children("div.sort-detail").show();
                },function(){
                    $(this).children("div.sort-detail").hide();
                });
            })
        </script> -->
    <script>
        /*$(document).ready(function () {
            $(".data-item-li").children("div").addClass("to-big");
        });*/
    </script>
</head>
<body>
<div id="main" class="container">
    <!-- <div id="header">
        <ul id="header_left">
            <li id="login"><a href="">login</a></li>
            <li id="register"><a href="">register</a></li>
        </ul>
        <ul>
            <li><a href="">Shopping Cart</a></li>
            <li><a href="">Collector</a></li>
            <li><a href="">客服</a></li>
        </ul>
    </div>
    <div>

    </div> -->
    <div id="header">
        <%@ include file="header.jsp" %>
        <%--<%
            String userId = (String) session.getAttribute("userId");
            //out.println(userId);
            String username = (String) session.getAttribute("username");
            if (username == null) {
        %>
        <div class="row">
            <div class="col-md-4" role="navigation">
                <!-- <h1 style="font-size: 20px;margin-top: 9px">东大咸鱼</h1> -->

                <ul class="nav nav-pills">

                    <li><a href="./login.jsp" style="color: #F22E00">Please login</a></li>


                    <li><a href="./register.jsp">regist</a></li>
                </ul>
            </div>
            <div class="col-md-8">
                <ul class="nav nav-pills pull-right">
                    <li><a href="./login.jsp"> <span
                            class="glyphicon glyphicon-comment"></span> news
                    </a></li>
                    <li><a href="./login.jsp"> <span
                            class="glyphicon glyphicon-shopping-cart" style="color: #F22E00"></span>
                        Shopping Cart
                    </a></li>
                    <li><a href="./login.jsp"> <span
                            class="glyphicon glyphicon-star"></span> Collector
                    </a></li>
                </ul>
            </div>
        </div>
        <div id="header-nav">
            <nav class="navbar navbar-default" id="header-nav-middle">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed"
                                data-toggle="collapse"
                                data-target="#bs-example-navbar-collapse-1"
                                aria-expanded="false">
                            <span class="sr-only">Toggle navigation</span> <span
                                class="icon-bar"></span> <span class="icon-bar"></span> <span
                                class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="./index.jsp"><!-- <img alt="Brand" style="display: inline-block;" src="./image/tao.jpg" width="20" height="20"> --><span class="logo-word">pocket flea market</span></a>
                    </div>

                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse"
                         id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li><a class="a-color" href="./index.jsp">home page</a></li>
                            <li><a class="a-color" href="./login.jsp">Publish idle</a></li>
                            <li class="dropdown"><a class="a-color" href="./login.jsp"
                                                    class="dropdown-toggle" data-toggle="dropdown" role="button"
                                                    aria-haspopup="true" aria-expanded="false">My idleness <span
                                    class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="login.jsp">On sale</a></li>
                                    <li><a href="login.jsp">In Transaction</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="./login.jsp">news</a></li>
                                </ul></li>
                        </ul>

                        <form class="navbar-form navbar-right" role="search" method="get" action="./searchResult.jsp">
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Search" name="keyword">
                            </div>
                            <button type="submit" class="btn btn-default">
                                <span class="glyphicon glyphicon-search" aria-label="搜索"></span>
                            </button>
                        </form>
                    </div>
                    <!-- /.navbar-collapse -->
                </div>
                <!-- /.container-fluid -->
            </nav>
        </div>
        <%
        } else {
        %>
        <div class="row">
            <div class="col-md-4" role="navigation">
                <!-- <h1 style="font-size: 20px;margin-top: 9px">东大咸鱼</h1> -->

                <ul class="nav nav-pills">
                    <li class="info-a"><a href="./info.jsp"
                                          style="color: #F22E00"><%=username%><span class="glyphicon glyphicon-triangle-bottom" style="font-size: 5px;margin-left: 7px;" aria-hidden="true"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="./info.jsp">Account Management</a></li>
                            <li role="separator" class="divider"></li>
                            <li><a href="./login.jsp" class="login-out">Logout</a></li>

                        </ul></li>
                    <li><a href="register.jsp">register</a></li>
                </ul>
            </div>
            <div class="col-md-8">
                <ul class="nav nav-pills pull-right">
                    <li><a href="./chat.jsp"> <span
                            class="glyphicon glyphicon-comment"></span> message
                    </a></li>
                    <li><a href="./shopcart.jsp"> <span
                            class="glyphicon glyphicon-shopping-cart" style="color: #F22E00"></span>
                        ShoppingCart
                    </a></li>
                    <li><a href="./favorite.jsp"> <span
                            class="glyphicon glyphicon-star"></span> Collector
                    </a></li>
                </ul>
            </div>
        </div>
        <div id="header-nav">
            <nav class="navbar navbar-default" id="&lt;%&ndash;header-nav-middle&ndash;%&gt;">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed"
                                data-toggle="collapse"
                                data-target="#bs-example-navbar-collapse-1"
                                aria-expanded="false">
                            <span class="sr-only">Toggle navigation</span> <span
                                class="icon-bar"></span> <span class="icon-bar"></span> <span
                                class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="./index.jsp"><span class="logo-word">shopd</span></a>
                    </div>

                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse"
                         &lt;%&ndash;id="bs-example-navbar-collapse-1"&ndash;%&gt;>
                        <ul class="nav navbar-nav">
                            <li><a class="a-color" href="./index.jsp">home page</a></li>
                            <li><a class="a-color" href="./release.jsp">Publish idle</a></li>
                            <li class="dropdown"><a class="a-color" href="./info.jsp"
                                                    class="dropdown-toggle" data-toggle="dropdown" role="button"
                                                    aria-haspopup="true" aria-expanded="false">My idleness <span
                                    class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="info.jsp">On sale</a></li>
                                    <li><a href="info.jsp">In Transaction</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="./chat.jsp">news</a></li>
                                </ul></li>
                        </ul>

                        <form class="navbar-form navbar-right" role="search" method="get" action="./searchResult.jsp">
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Search" name="keyword">
                            </div>
                            <button type="submit" class="btn btn-default">
                                <span class="glyphicon glyphicon-search" aria-label="search"></span>
                            </button>
                        </form>
                    </div>
                    <!-- /.navbar-collapse -->
                </div>
                <!-- /.container-fluid -->
            </nav>
        </div>
        <%
            }
        %>--%>
        <!-- 旋转图 -->
        <div class="header-bottom">
            <div class="sort">
                <div class="sort-channel">
                    <ul class="sort-channel-list list-group">
                        <li class="list-group-item"><a href="${pageContext.request.contextPath}/category?cate=Digital">Digital</a>
                            <div class="sort-detail">
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Mobile phone">Mobile phone</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=Mobile phone shell">Mobile phone shell</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Charger">Charger</a> <a href="">Battery</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Headset">Headset</a>
                                    </dd>
                                </dl>
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=camera">camera</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=Camera lens">Camera lens</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Single reflex">Single reflex</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Film">Film</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Video shooting">Video shooting</a>
                                    </dd>
                                </dl>
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Computer">Computer</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=Display">Display</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Graphics card">Graphics card</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Hard disk">Hard disk</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Memory">Memory</a>
                                    </dd>
                                </dl>
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Video and sound">Video and sound</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=MP3">MP3</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=sound">sound</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Microphone">Microphone</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Radio">Radio</a>
                                    </dd>
                                </dl>
                            </div>
                        </li>
                        <li class="list-group-item"><a href="${pageContext.request.contextPath}/category?cate=Daily expenses">Daily expenses</a>
                            <div class="sort-detail">
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Skin care">Skin care</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=Shampoo">Shampoo</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Shower Gel">Shower Gel</a> <a href="">Facial Cleanser</a> <a
                                            href="">Liquid soap</a>
                                    </dd>
                                </dl>
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Beauty makeup">Beauty makeup</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=Facial mask">Facial mask</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Lipstick">Lipstick</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Defend against">Defend against</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Perfume">Perfume</a>
                                    </dd>
                                </dl>
                                <dl class="dl-hor">
                                    <dt>
                                        <a href="${pageContext.request.contextPath}/category?cate=Home Furnishing">Home Furnishing</a>
                                    </dt>
                                    <dd>
                                        <a href="${pageContext.request.contextPath}/category?cate=bookshelf">bookshelf</a><a href="${pageContext.request.contextPath}/category?cate=鞋柜">鞋柜</a>
                                        <a href="${pageContext.request.contextPath}/category?cate=Coat hanger">Coat hanger</a> <a
                                            href="${pageContext.request.contextPath}/category?cate=Desk lamp">Desk lamp</a>
                                    </dd>
                                </dl>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div id="mycarousel" class="carousel slide" data-ride="carousel">
                <div class="carousel-inner">
                    <div class="item active">
                        <img src="${pageContext.request.contextPath}/image/4.jpg" alt="">
                    </div>

                    <div class="item">
                        <img src="${pageContext.request.contextPath}/image/3.jpg" alt="">
                    </div>
                    <div class="item">
                        <img src="${pageContext.request.contextPath}/image/5.jpg" alt="">
                    </div>
                    <div class="item">
                        <img src="${pageContext.request.contextPath}/image/6.jpg" alt="">
                    </div>
                </div>

                <ol class="carousel-indicators">
                    <li data-target="#mycarousel" data-slide-to="0" class="active"></li>
                    <li data-target="#mycarousel" data-slide-to="1"></li>
                    <li data-target="#mycarousel" data-slide-to="2"></li>
                    <li data-target="#mycarousel" data-slide-to="3"></li>
                </ol>

                <a class="left carousel-control" href="#mycarousel" role="button"
                   data-slide="prev" style="display: none;"> <span
                        class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                    <span class="sr-only">Previous</span>
                </a> <a class="right carousel-control" href="#mycarousel" role="button"
                        data-slide="next" style="display: none;"> <span
                    class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
            </div>
            <div class="clear-float"></div>
        </div>
    </div>
    <div class="content">

        <c:if test="${!empty digGoods}">
            <div class="module">
                <div class="hd">
                    <h2>Digital</h2>
                    <hr>
                </div>

                <div class="bd">
                    <div class="data">
                        <ul>
                            <c:forEach items="${digGoods}" var="goods">
                                <li class="data-item-li">
                                    <div class="to-big">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${goods.goodsid}"><img src="/shopimage/${goods.imagePaths[0].path}" alt=""
                                                        width="200" height="200"/>
                                        </a>
                                    </div>
                                    <p class="text-right">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${goods.goodsid}">${goods.goodsname}</a>
                                    </p>
                                    <div class="text-right">
                                        <b>￥${goods.price}</b>
                                    </div>
                                    <div>
                                        <c:if test="${goods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart btn btn-default"
                                                    data-id="${goods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <c:if test="${!goods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart-empty btn btn-default"
                                                    data-id="${goods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <!-- <button class="like-button1 glyphicon glyphicon-heart-empty btn btn-default "></button> -->
                                    </div>
                                </li>
                            </c:forEach>


                            <div class="clear-float" style="clear: both;"></div>
                        </ul>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${!empty houseGoods}">
            <div class="module">
                <div class="hd">
                    <h2>appliances</h2>
                    <hr>
                </div>

                <div class="bd">
                    <div class="data">
                        <ul>
                            <c:forEach items="${houseGoods}" var="housegoods">
                                <li class="data-item-li">
                                    <div class="to-big">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${housegoods.goodsid}"> <img
                                                src="/shopimage/${housegoods.imagePaths[0].path}" alt=""
                                                width="200" height="200">
                                        </a>
                                    </div>
                                    <p class="text-right">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${housegoods.goodsid}">${housegoods.goodsname}</a>
                                    </p>
                                    <div class="text-right">
                                        <b>￥${housegoods.price}</b>
                                    </div>
                                    <div>
                                        <c:if test="${housegoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart btn btn-default"
                                                    data-id="${housegoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <c:if test="${!housegoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart-empty btn btn-default"
                                                    data-id="${housegoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <!-- <button class="like-button1 glyphicon glyphicon-heart-empty btn btn-default "></button> -->
                                    </div>
                                </li>
                            </c:forEach>

                            <div class="clear-float" style="clear: both;"></div>
                        </ul>

                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${!empty colGoods}">
            <div class="module">
                <div class="hd">
                    <h2>Clothes</h2>
                    <hr>
                </div>

                <div class="bd">
                    <div class="data">
                        <ul>
                            <c:forEach items="${colGoods}" var="colgoods">
                                <li class="data-item-li">
                                    <div class="to-big">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${colgoods.goodsid}"> <img
                                                src="/shopimage/${colgoods.imagePaths[0].path}" alt=""
                                                width="200" height="200">
                                        </a>
                                    </div>
                                    <p class="text-right">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${colgoods.goodsid}">${colgoods.goodsname}</a>
                                    </p>
                                    <div class="text-right">
                                        <b>￥${colgoods.price}</b>
                                    </div>
                                    <div>
                                        <c:if test="${colgoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart btn btn-default"
                                                    data-id="${colgoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <c:if test="${!colgoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart-empty btn btn-default"
                                                    data-id="${colgoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <!-- <button class="like-button1 glyphicon glyphicon-heart-empty btn btn-default "></button> -->
                                    </div>
                                </li>
                            </c:forEach>

                            <div class="clear-float" style="clear: both;"></div>
                        </ul>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${!empty bookGoods}">
            <div class="module">
                <div class="hd">
                    <h2>book</h2>
                    <hr>
                </div>

                <div class="bd">
                    <div class="data">
                        <ul>
                            <c:forEach items="${bookGoods}" var="bookgoods">
                                <li class="data-item-li">
                                    <div class="to-big">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${bookgoods.goodsid}"> <img
                                                src="/shopimage/${bookgoods.imagePaths[0].path}" alt=""
                                                width="200" height="200">
                                        </a>
                                    </div>
                                    <p class="text-right">
                                        <a href="${pageContext.request.contextPath}/detail?goodsid=${bookgoods.goodsid}">${bookgoods.goodsname}</a>
                                    </p>
                                    <div class="text-right">
                                        <b>￥${bookgoods.price}</b>
                                    </div>
                                    <div>
                                        <c:if test="${bookgoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart btn btn-default"
                                                    data-id="${bookgoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <c:if test="${!bookgoods.fav}">
                                            <button
                                                    class="like-button glyphicon glyphicon-heart-empty btn btn-default"
                                                    data-id="${bookgoods.goodsid}"
                                                    style="display: none;"></button>
                                        </c:if>
                                        <!-- <button class="like-button1 glyphicon glyphicon-heart-empty btn btn-default "></button> -->
                                    </div>
                                </li>
                            </c:forEach>

                            <div class="clear-float" style="clear: both;"></div>
                        </ul>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>


