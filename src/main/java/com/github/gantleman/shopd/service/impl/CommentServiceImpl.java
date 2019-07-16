package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.CommentDA;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.service.jobs.CommentJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private CommentJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Comment";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, CommentJob.class, null, job);
        }
    }

    @Override
    public void insertSelective(Comment comment){
        //send do
        RefreshDBD(comment.getGoodsid());

        BDBEnvironmentManager.getInstance();
        CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        comment.setCommentid(new Long(id).intValue());
        comment.MakeStamp();
        comment.setStatus(2);
        commentDA.saveComment(comment);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Comment_u" + comment.getGoodsid().toString(), 0, comment.getCommentid()); 
        redisu.hset(classname, comment.getCommentid().toString(), (Object)comment, 0);
    }

    @Override
    public List<Comment> selectByGoodsID(Integer goodsid) {
        List<Comment> re = new ArrayList<>();
        if(redisu.hasKey("Comment_u"+goodsid.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Comment_u"+goodsid.toString());
            re = new ArrayList<Comment>();
            for (Object id : ro) {
                Comment r =  (Comment) redisu.hget(classname, ((Integer)id).toString());
                if (r != null)
                    re.add(r);
            }
            redisu.expire("Comment_u"+goodsid.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            CommentExample commentExample=new CommentExample();
            commentExample.or().andGoodsidEqualTo(goodsid);
            
            List<Comment> lre = commentMapper.selectByExample(commentExample);

            ///read and write
            if(!redisu.hasKey("Comment_u"+goodsid.toString())) {
                for( Comment item : lre ){
                    redisu.sAdd("Comment_u"+goodsid.toString(), (Object)item.getCommentid());
                    redisu.hset(classname, item.getCommentid().toString(), item);

                    if (item != null)
                    re.add(item);
                }
                redisu.expire("Comment_u"+goodsid.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());
        List<Comment> lcomment = commentDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Comment comment : lcomment) {
            if(null ==  comment.getStatus()) {
                commentDA.removedCommentById(comment.getCommentid());
            }

            if(2 ==  comment.getStatus()  && 1 == commentMapper.insert(comment)) {
                commentDA.removedCommentById(comment.getCommentid());
            }
        }

        if (commentDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD(Integer goodsid) {
        ///init
       if (cacheService.IsCache(classname, goodsid)) {
           BDBEnvironmentManager.getInstance();
           CommentDA commentDA=new CommentDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<Comment> re = new ArrayList<Comment>();

           CommentExample commentExample = new CommentExample();
           commentExample.or().andGoodsidEqualTo(goodsid);

           re = commentMapper.selectByExample(commentExample);
           for (Comment value : re) {
               value.MakeStamp();
               commentDA.saveComment(value);

               redisu.sAddAndTime("Comment_u"+goodsid.toString(), 0, value.getCommentid()); 
               redisu.hset(classname, value.getCommentid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, CommentJob.class, null, job);          
           }
       }
   }
}
