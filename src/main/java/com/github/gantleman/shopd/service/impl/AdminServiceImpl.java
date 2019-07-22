package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.AdminDA;
import com.github.gantleman.shopd.dao.AdminMapper;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.AdminExample;
import com.github.gantleman.shopd.service.AdminService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.AdminJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired(required = false)
    private AdminMapper adminMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private AdminJob job;

    private String classname = "Admin";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, AdminJob.class, null, job);
        }
    }

    @Override
    public Admin selectByName(Admin admin) {
        //Repeated queries for non-existent accounts can lead to drastic performance degradation
        //Only blacklists can be used to shield malicious attacks
        Admin ra = new Admin();
        ///first check localDB
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        List<Admin> lra = adminDA.findAllChatByAdminNameAndPassword( admin.getAdminname(), admin.getPassword());
        if(!lra.isEmpty()){
            ra = lra.get(0);
        }else{
            ///second
            AdminExample adminExample = new AdminExample();
            adminExample.or().andAdminnameEqualTo(admin.getAdminname());
            List<Admin> sqlra = adminMapper.selectByExample(adminExample);    
            if(!sqlra.isEmpty()){
                //cache page
                RefreshDBD(cacheService.PageID(sqlra.get(0).getAdminid()), false);

                //third
                if(sqlra.get(0).getPassword().equals(admin.getPassword()))
                    ra = sqlra.get(0);
            }
        }
  
        return ra;
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Admin admin = adminDA.findAdminById(i);
                if(admin != null){
                    if(null ==  admin.getStatus()) {
                        adminDA.removedAdminById(admin.getAdminid());
                    }
                }
            }
        }
        if (adminDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, AdminJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Admin> re = new ArrayList<Admin>();          
            AdminExample adminExample = new AdminExample();
            adminExample.or().andAdminidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            adminExample.or().andAdminidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = adminMapper.selectByExample(adminExample);
            for (Admin value : re) {
                adminDA.saveAdmin(value);
            }
            BDBEnvironmentManager.getMyEntityStore().sync();
       }
    }  
}
