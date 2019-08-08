package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.CommentDA;
import com.github.gantleman.shopd.da.CommentGoodsDA;
import com.github.gantleman.shopd.dao.CommentMapper;
import com.github.gantleman.shopd.entity.Comment;
import com.github.gantleman.shopd.entity.CommentExample;
import com.github.gantleman.shopd.entity.CommentGoods;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.CommentService;
import com.github.gantleman.shopd.service.jobs.CommentJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private CommentJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Comment";

    private String classname_extra = "Comment_User";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, CommentJob.class, null, job);
        }
    }

    @Override
    public Comment getCommentByKey(Integer commentid, String url) {
        Comment re = null;
        Integer pageId = cacheService.PageID(commentid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Comment) redisu.hget(classname, commentid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/commentpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                re = (Comment) redisu.hget(classname, commentid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    public void insertSelective_extra(Comment comment) {
        //add to CommentGoodsDA
        RefreshUserDBD(comment.getGoodsid(), false, false);
        BDBEnvironmentManager.getInstance();
        CommentGoodsDA commentGoodsDA=new CommentGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        CommentGoods commentGoods = commentGoodsDA.findCommentGoodsById(comment.getGoodsid());
        if(commentGoods == null){
            commentGoods = new CommentGoods();
        }
        commentGoods.addCommentList(comment.getCommentid());
        commentGoodsDA.saveCommentGoods(commentGoods);

        //Re-publish to redis
        redisu.sAdd("comment_g" + comment.getGoodsid().toString(), comment.getCommentid()); 
    }

    @Override
    public void insertSelective(Comment comment){
        BDBEnvironmentManager.getInstance();
        CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        comment.setCommentid(new Long(id).intValue());
        comment.setStatus(CacheService.STATUS_INSERT);
        commentDA.saveComment(comment);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, comment.getCommentid().toString(), (Object)comment, 0);

        insertSelective_extra(comment);
    }

    @Override
    public List<Comment> selectByGoodsID(Integer goodsid, String url) {
        List<Comment> re = new ArrayList<Comment>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(goodsid).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("comment_g"+goodsid.toString());
            re = new ArrayList<Comment>();
            for (Object id : ro) {
                Comment r =  getCommentByKey((Integer)id, url);
                if (r != null)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsid).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/commentgoodspage", goodsid);
            }else{
                RefreshUserDBD(goodsid, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(goodsid).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("comment_g"+goodsid.toString());
                re = new ArrayList<Comment>();
                for (Object id : ro) {
                    Comment r =  getCommentByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsid).toString(), 1);
            }
        }
        return re;
    }

    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        CommentGoodsDA commentGoodsDA=new CommentGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                CommentGoods commentGoods = commentGoodsDA.findCommentGoodsById(i);
                if(commentGoods != null){
                    commentGoodsDA.removedCommentGoodsById(commentGoods.getGoodsid());
                    redisu.del("comment_g"+commentGoods.getGoodsid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (commentGoodsDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Comment comment = commentDA.findCommentById(i);
                if(comment != null){
                    if(null ==  comment.getStatus()) {
                        commentDA.removedCommentById(comment.getCommentid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  comment.getStatus() && 1 == commentMapper.deleteByPrimaryKey(comment.getCommentid())) {
                        commentDA.removedCommentById(comment.getCommentid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  comment.getStatus()  && 1 == commentMapper.insert(comment)) {
                        commentDA.removedCommentById(comment.getCommentid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  comment.getStatus() && 1 == commentMapper.updateByPrimaryKey(comment)) {
                        commentDA.removedCommentById(comment.getCommentid());
                    }
                    redisu.hdel(classname, comment.getCommentid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (commentDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, CommentJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Comment> re = new ArrayList<Comment>();          
            CommentExample commentExample = new CommentExample();
            commentExample.or().andCommentidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andCommentidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = commentMapper.selectByExample(commentExample);
            for (Comment value : re) {
                redisu.hset(classname, value.getCommentid().toString(), value);
                commentDA.saveComment(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Comment r = commentDA.findCommentById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer goodsID, boolean andAll, boolean refresRedis) {
        BDBEnvironmentManager.getInstance();
        CommentGoodsDA commentGoodsDA=new CommentGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(goodsID))) {
            /// init
            List<Comment> re = new ArrayList<Comment>();          
            CommentExample commentExample = new CommentExample();
            commentExample.or().andGoodsidGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(goodsID)))
            .andGoodsidGreaterThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(goodsID)));

            re = commentMapper.selectByExample(commentExample);
            for (Comment value : re) {
                CommentGoods commentGoods  = commentGoodsDA.findCommentGoodsById(value.getGoodsid());
                if(commentGoods == null){
                    commentGoods = new CommentGoods();
                }
                
                redisu.sAdd("comment_g"+value.getGoodsid().toString(), (Object)value.getCommentid());

                if(andAll){ 
                    RefreshDBD(cacheService.PageID(value.getCommentid()), refresRedis);
                }

                commentGoodsDA.saveCommentGoods(commentGoods);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(goodsID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(goodsID));
                Integer l = cacheService.PageEnd(cacheService.PageID(goodsID));
                for(;i < l; i++){
                    CommentGoods r = commentGoodsDA.findCommentGoodsById(i);
                    if(r!= null){
                        List<Integer> li = r.getCommentList();
                        for(Integer id: li){
                            redisu.sAdd("comment_g"+r.getGoodsid().toString(), (Object)id); 
                        }
                    } 
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsID).toString(), 1);
            }
        }
    }
}
