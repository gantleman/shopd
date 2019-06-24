package com.github.gantleman.shopd.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.AdminDA;
import com.github.gantleman.shopd.dao.AdminMapper;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.service.AdminService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.HttpUtils;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.gantleman.shopd.service.jobs.AdminJob;



@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired(required = false)
    private AdminMapper adminMapper;

    @Autowired(required = false)
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;
    
    @Autowired
    private HttpUtils httputils;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private AdminJob job;

    @PostConstruct
    public void init() {
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        if (0 == adminDA.IsEmpty()) {
            ///create time todo
            quartzManager.addJob("Admin","Admin","Admin","Admin", AdminJob.class, null, job);
        }
    }

    @Override
    public Admin selectByName(Admin admin) {
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        List<Admin> ra = adminDA.findAllChatByAdminNameAndPassword( admin.getAdminname(), admin.getPassword());

        Admin sqlra;
        if( ra.size() >= 1 )
            sqlra =  ra.get(0);
        else {
            sqlra = adminMapper.selectByName(admin);
            if( sqlra != null)
            {
                adminDA.saveAdmin(sqlra);
                ///cache
                cacheService.eventAdd("Admin");

                 ///create time todo
                 quartzManager.addJob("Admin","Admin","Admin","Admin", AdminJob.class, null, job);
            }
            //redis
            redisu.hset("Admin", sqlra.getAdminid().toString(), sqlra, 0);
        }
        return sqlra;
    }

    @Override
    public void SaveBack() {
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        List<Admin> ladmin = adminDA.findAllUserWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Admin admin : ladmin) {
            if(1 == adminMapper.updateByPrimaryKey(admin)) {
                adminDA.removedAdminById(admin.getAdminid());
            }
        }

        if (1 == adminDA.IsEmpty()){
            cacheService.Archive("Admin");
        }
    }
}
