package com.github.gantleman.shopd.service;

import java.util.List;

import com.github.gantleman.shopd.entity.Comment;

public interface CommentService {
    //only read
    public Comment getCommentByKey(Integer commentid, String url);

    public List<Comment> selectByGoodsID(Integer goodsid, String url);

    //have write
    public void insertSelective(Comment comment);

    public void Clean_extra(Boolean all);

    public void Clean(Boolean all) ;

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis);
}
