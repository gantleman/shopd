package com.github.gantleman.shopd.service.impl;

import java.util.List;

import com.github.gantleman.shopd.da.*;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 文辉 on 2017/7/19.
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired(required = false)
    private AdminMapper adminMapper;

    @Autowired(required = false)
    private CacheService cacheService;

    @Override
    public Admin selectByName(Admin admin) {
        BDBEnvironmentManager.getInstance();
        AdminDA adminDA=new AdminDA(BDBEnvironmentManager.getMyEntityStore());
        List<Admin> ra = adminDA.findAllChatByAdminNameAndPassword( admin.getAdminname(), admin.getPassword());

        if( ra.size() >= 1 )
            return ra.get(0);
        else {
           Admin sqlra = adminMapper.selectByName(admin);
           if( sqlra != null)
           {
               adminDA.saveAdmin(sqlra);

               ///cache
               cacheService.eventAdd("Admin");
               
               ///TODO:redis
               ///TODO:time
           }
           return sqlra;
        }
    }
}
