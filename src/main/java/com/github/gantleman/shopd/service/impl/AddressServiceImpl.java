package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.AddressDA;
import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.AddressExample;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.AddressJob;
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

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired(required = false)
    private AddressMapper addressMapper;

    @Autowired(required = false)
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private AddressJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Address";
    
    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, AddressJob.class, null, job);
        }
    }

    @Override
    public Address getAddressByKey(Integer addressid) {
        return addressMapper.selectByPrimaryKey(addressid);
    }
    
    @Override
    public List<Address> getAllAddressByUserID(Integer UserID) {
        List<Address> re = null;

        if(redisu.hasKey("Address_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Address_u"+UserID.toString());
            re = new ArrayList<Address>();
            for (Object id : ro) {
                Address r =  (Address) redisu.hget(classname, ((Integer)id).toString());
                if (r != null)
                    re.add(r);
            }
            redisu.expire("Address_u"+UserID.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            AddressExample addressExample=new AddressExample();
            addressExample.or().andUseridEqualTo(UserID);
            
            re = addressMapper.selectByExample(addressExample);

            ///read and write
            if(!redisu.hasKey("Address_u"+UserID.toString())) {
                for( Address item : re ){
                    redisu.sAdd("Address_u"+UserID.toString(), (Object)item.getAddressid());
                    redisu.hset(classname, item.getAddressid().toString(), item);
                }
                redisu.expire("Address_u"+UserID.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public Address selectByPrimaryKey(Integer addressid) {
        Address re = null;
        if(redisu.hHasKey(classname, addressid.toString())) {
            //read redis
            Object o = redisu.hget(classname, addressid.toString());
            re = (Address) o;

            redisu.expire("Address_u"+re.getUserid().toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            re = addressMapper.selectByPrimaryKey(addressid);
            ///init
            if (re != null && !redisu.hHasKey("Address_u", re.getUserid().toString())) {
                //write redis
                AddressExample addressExample=new AddressExample();
                addressExample.or().andUseridEqualTo(re.getUserid());
                
                List<Address> lre = addressMapper.selectByExample(addressExample);

                ///read and write
                if(!redisu.hasKey("Address_u"+re.getUserid().toString())) {
                    for( Address item : lre ){
                        redisu.sAdd("Address_u"+re.getUserid().toString(), (Object)item.getAddressid());
                        redisu.hset(classname, item.getAddressid().toString(), item);
                    }
                    redisu.expire("Address_u"+re.getUserid().toString(), 0);
                    redisu.expire(classname, 0);
                }                  
            }
        }
        return re;
    }

    @Override
    public void updateByPrimaryKeySelective(Address iaddress) {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        Address address = addressDA.findAddressById(iaddress.getAddressid());
 
        if (address != null)
        {
            iaddress.MakeStamp();
            iaddress.setStatus(3);
            
            if(iaddress.getCity() != null){
                address.setCity(iaddress.getCity());
            }
            if(iaddress.getConname() != null){
                address.setConname(iaddress.getConname());
            }
            if(iaddress.getContel() != null){
                address.setContel(iaddress.getContel());
            }
            if(iaddress.getCounty() != null){
                address.setCounty(iaddress.getCounty());
            }
            if(iaddress.getDetailaddr() != null){
                address.setDetailaddr(iaddress.getDetailaddr());
            }
            if(iaddress.getProvince() != null){
                address.setProvince(iaddress.getProvince());
            }
            if(iaddress.getUserid() != null){
                address.setUserid(iaddress.getUserid());
            }
            addressDA.saveAddress(address);

            //Re-publish to redis
            redisu.hset(classname, address.getAddressid().toString(), (Object)address, 0);
        } else {

            address = addressMapper.selectByPrimaryKey(iaddress.getAddressid());
            if (null != address) {
                ///init
                RefreshDBD(address.getUserid());

                if (address != null)
                {
                    iaddress.MakeStamp();
                    iaddress.setStatus(3);
                    
                    if(iaddress.getCity() != null){
                        address.setCity(iaddress.getCity());
                    }
                    if(iaddress.getConname() != null){
                        address.setConname(iaddress.getConname());
                    }
                    if(iaddress.getContel() != null){
                        address.setContel(iaddress.getContel());
                    }
                    if(iaddress.getCounty() != null){
                        address.setCounty(iaddress.getCounty());
                    }
                    if(iaddress.getDetailaddr() != null){
                        address.setDetailaddr(iaddress.getDetailaddr());
                    }
                    if(iaddress.getProvince() != null){
                        address.setProvince(iaddress.getProvince());
                    }
                    if(iaddress.getUserid() != null){
                        address.setUserid(iaddress.getUserid());
                    }
                    addressDA.saveAddress(address);
        
                    //Re-publish to redis
                    redisu.hset(classname, address.getAddressid().toString(), (Object)address, 0);
                }
            }
        }
    }

    @Override
    public void deleteByPrimaryKey(Integer addressid) {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        Address address = addressDA.findAddressById(addressid);
 
        if (address != null)
        {
             address.MakeStamp();
             address.setStatus(1);
             addressDA.saveAddress(address);

             //Re-publish to redis
             redisu.setRemove("Address_u" + address.getUserid().toString(), address.getAddressid());
             redisu.hdel(classname, address.getAddressid().toString(), 0);
        } else {

            address = addressMapper.selectByPrimaryKey(addressid);

            if (null != address) {
                ///init
                RefreshDBD(address.getUserid());

                if (address != null)
                {
                    address.MakeStamp();
                    address.setStatus(1);
                    addressDA.saveAddress(address);

                    //Re-publish to redis
                    redisu.setRemove("Address_u" + address.getUserid().toString(), address.getAddressid());
                    redisu.hdel(classname, address.getAddressid().toString(), 0);
                } 
            }
        }
    }

    @Override
    public void insertSelective(Address address) {
        RefreshDBD(address.getUserid());

        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        address.setAddressid(new Long(id).intValue());
        address.MakeStamp();
        address.setStatus(2);
        addressDA.saveAddress(address);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Address_u" + address.getUserid().toString(), 0, address.getAddressid()); 
        redisu.hset(classname, address.getAddressid().toString(), (Object)address, 0);
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        List<Address> laddress = addressDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Address address : laddress) {
            if(null ==  address.getStatus()) {
                addressDA.removedAddressById(address.getAddressid());
            }

            if(1 ==  address.getStatus() && 1 == addressMapper.deleteByPrimaryKey(address.getAddressid())) {
                addressDA.removedAddressById(address.getAddressid());
            }

            if(2 ==  address.getStatus()  && 1 == addressMapper.insert(address)) {
                addressDA.removedAddressById(address.getAddressid());
            }

            if(3 ==  address.getStatus() && 1 == addressMapper.updateByPrimaryKey(address)) {
                addressDA.removedAddressById(address.getAddressid());
            }
        }

        if (addressDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }


    public void RefreshDBD(Integer userid) {
         ///init
        if (cacheService.IsCache(classname, userid)) {
            BDBEnvironmentManager.getInstance();
            AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());

            Set<Integer> id = new HashSet<Integer>();
            List<Address> re = new ArrayList<Address>();

            AddressExample addressExample=new AddressExample();
            addressExample.or().andUseridEqualTo(userid);

            re = addressMapper.selectByExample(addressExample);
            for (Address value : re) {
                value.MakeStamp();
                addressDA.saveAddress(value);

                redisu.sAddAndTime("Address_u"+userid.toString(), 0, value.getAddressid()); 
                redisu.hset(classname, value.getAddressid().toString(), value, 0);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();

            id.add(userid);
            cacheService.eventAdd(classname, id);
            
            if(cacheService.IsCache(classname)){         
                quartzManager.addJob(classname,classname,classname,classname, AddressJob.class, null, job);          
            }
        }
    }
}