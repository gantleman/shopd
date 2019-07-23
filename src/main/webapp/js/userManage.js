/**
 * Created by 文辉 on 2017/7/23.
 */
$(document).ready(function () {

    var path = $("#path").text();

    var currentPage = 1;

    to_page(path, 1);


});

$(document).on("click",".templatemo-delete-btn",function () {
    var goodsname = $(this).parents("tr").find("td:eq(1)").text();
    var goodsid = $(this).parents("tr").find("td:eq(0)").text();
    swal({
            title: "Determine deletion " + goodsname + " ？",
            type: "warning",
            showCancelButton: true,
            cancelButtonText:"cancel",
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Make sure to delete!",
            closeOnConfirm: false,
        },
        function () {
            /*swal("删除！", "你的虚拟文件已经被删除。", "success");*/
            $.ajax({
                url: "/admin/user/delete/" + goodsid,
                type: "DELETE",
                success:function (result) {
                    swal(result.msg, "","success");
                    to_page('/shop',currentPage);
                },
                error:function () {
                    /*to_page('/shop',currentPage);*/
                }
            });
        });
});

function to_page(path, page) {
    $.ajax({
        url: path + "/admin/user/showjson",
        data: "page=" + page,
        type: "get",
        success: function (result) {

            //解析显示
            build_user_table(path, result);

            //页面信息
            build_page_info(path, result);

            //分页
            build_page_nav(path, result);

            currentPage = page;
        }
    });
}

function build_user_table(path,result) {
    $("#goodsinfo tbody").empty();
    var goods = result.info.pageInfo.list;
    $.each(goods, function (index,item) {
        var userid = $("<td></td>").append(item.userid);
        var username = $("<td></td>").append(item.username);
        var email = $("<td></td>").append(item.email);
        var telephone = $("<td></td>").append(item.telephone);

        var deleteBtn = $("<button></button>").addClass("templatemo-delete-btn").append("delete");

        var deleteTd = $("<td></td>").append(deleteBtn);


        $("<tr></tr>").append(userid)
            .append(username)
            .append(email)
            .append(telephone)
            .append(deleteTd).appendTo("#goodsinfo tbody");
    })
}

function build_page_info(path,result) {
    $("#page-info-area").empty();
    $("#page-info-area").append("Current "+ result.info.pageInfo.pageNum +" Pages, total "+ result.info.pageInfo.pages +" Pages, total "+ result.info.pageInfo.total +" Record")
}

function build_page_nav(path,result) {
    $("#page-div-nav ul").empty();
    var pageUl = $("<ul></ul>").addClass("pagination")

    var firstPage = $("<li></li>").append($("<a aria-label=\"Next\"></a>")
        .append($("<span aria-hidden=\"true\"></span>")
            .append("first page")));

    var prePage = $("<li></li>").append($("<a aria-label=\"Next\"></a>")
        .append($("<span aria-hidden=\"true\"><i class=\"fa fa-backward\"></i></span>")));

    if(!result.info.pageInfo.hasPreviousPage) {
        prePage.addClass("li-none");
    } else {
        prePage.click(function () {
            to_page('/shop',result.info.pageInfo.prePage);
        });
    }

    //跳转
    firstPage.click(function () {
        to_page('/shop',1);
    });

    var nextPage = $("<li></li>").append($("<a aria-label=\"Next\"></a>")
        .append($("<span aria-hidden=\"true\"><i class=\"fa fa-forward\"></i></span>")));

    var lastPage = $("<li></li>").append($("<a aria-label=\"Next\"></a>")
        .append($("<span aria-hidden=\"true\"></span>")
            .append("Last")));

    if(!result.info.pageInfo.hasNextPage) {
        nextPage.addClass("li-none");
    } else {
        nextPage.click(function () {
            to_page('/shop',result.info.pageInfo.nextPage);
        });
    }

    lastPage.click(function () {
        to_page('/shop',result.info.pageInfo.lastPage);
    });

    pageUl.append(firstPage).append(prePage);

    $.each(result.info.pageInfo.navigatepageNums,function (index,item) {
        var numLi = $("<li></li>").append($("<a></a>")
            .append($("<span aria-hidden=\"true\"></span>").append(item)));
        if(result.info.pageInfo.pageNum === item) {
            numLi.addClass("active");
        }
        numLi.click(function () {
            to_page('/shop',item);
        });
        pageUl.append(numLi);
    });

    pageUl.append(nextPage).append(lastPage).appendTo("#page-div-nav");
}