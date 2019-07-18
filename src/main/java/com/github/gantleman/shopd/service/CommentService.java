package com.github.gantleman.shopd.service;

import java.util.List;

import com.github.gantleman.shopd.entity.Comment;

public interface CommentService {
    //only read
    public Comment getCommentByKey(Integer commentid, String url);

    public List<Comment> selectByGoodsID(Integer goodsid, String url);

    //have write
    public void insertSelective(Comment comment);

    public void TickBack_extra();

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis);
}
