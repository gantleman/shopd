package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.AddressDA;
import com.github.gantleman.shopd.da.AddressUserDA;
import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.AddressExample;
import com.github.gantleman.shopd.entity.AddressUser;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.AddressJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;  

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired(required = false)
    private AddressMapper addressMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private AddressJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Address";

    private String classname_extra = "Address_User";
    
    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, AddressJob.class, null, job);
        }
    }
    
    @Override
    public List<Address> getAllAddressByUser(Integer userID, String url) {
        List<Address> re = new ArrayList<Address>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("address_u"+userID.toString());
            if(ro != null){
                for (Object id : ro) {
                    Address r =  getAddressByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);               
            }
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/addressuserpage", userID);
            }else{
                RefreshUserDBD(userID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("address_u"+userID.toString());
                if(ro != null){
                    for (Object id : ro) {
                        Address r =  getAddressByKey((Integer)id, url);
                        if (r != null)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);               
                }
            }
        }
        return re;
    }

    @Override
    public Address getAddressByKey(Integer addressid, String url) {
        Address re = null;
        Integer pageId = cacheService.PageID(addressid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Address) redisu.hget(classname, addressid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/addresspage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                re = (Address) redisu.hget(classname, addressid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public void updateByPrimaryKeySelective(Address iaddress) {
        RefreshDBD(cacheService.PageID(iaddress.getAddressid()), false);

        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        Address address = addressDA.findAddressById(iaddress.getAddressid());
 
        if (address != null && address.getStatus() != CacheService.STATUS_DELETE)
        {
            if(iaddress.getStatus()== null)
                iaddress.setStatus(CacheService.STATUS_UPDATE);
            
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
            addressDA.saveAddress(address);

            //Re-publish to redis
            redisu.hset(classname, address.getAddressid().toString(), (Object)address, 0);
        }
    }

    public void deleteByPrimaryKey_extra(Address address) {
        RefreshUserDBD(address.getUserid(), false, false);

        BDBEnvironmentManager.getInstance();
        AddressUserDA addressUserDA=new AddressUserDA(BDBEnvironmentManager.getMyEntityStore());
        AddressUser addressUser = addressUserDA.findAddressUserById(address.getUserid());

        if(addressUser != null){
            addressUser.removeAddressList(address.getAddressid());

            if(addressUser.getAddressSize() >= 1){
                //Re-publish to redis
                 redisu.setRemove("address_u" + address.getUserid().toString(), address.getAddressid());
            } else if(addressUser.getAddressSize() == 0){
                //list empty}
                addressUserDA.removedAddressUserById(addressUser.getUserid());
                //Re-publish to redis
                redisu.del("address_u" + address.getUserid().toString());
            }
            addressUserDA.saveAddressUser(addressUser);
            BDBEnvironmentManager.getMyEntityStore().sync();
        }
    }

    @Override
    public void deleteByPrimaryKey(Integer addressid) {
        RefreshDBD(cacheService.PageID(addressid), false);

        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        Address address = addressDA.findAddressById(addressid);
 
        if (address != null)
        {
             address.setStatus(CacheService.STATUS_DELETE);
             addressDA.saveAddress(address);

             //Re-publish to redis
             redisu.hdel(classname, address.getAddressid().toString());
             
             deleteByPrimaryKey_extra(address);
        }   
    }

    public void insertSelective_extra(Address address) {
        //add to AddressUserDA
        RefreshUserDBD(address.getUserid(), false, false);
        BDBEnvironmentManager.getInstance();
        AddressUserDA addressUserDA=new AddressUserDA(BDBEnvironmentManager.getMyEntityStore());
        AddressUser addressUser = addressUserDA.findAddressUserById(address.getUserid());
        if(addressUser == null){
            addressUser = new AddressUser();
        }
        addressUser.addAddressList(address.getAddressid());
        addressUserDA.saveAddressUser(addressUser);

        //Re-publish to redis
        redisu.sAdd("address_u" + address.getUserid().toString(), address.getAddressid()); 
    }

    @Override
    public void insertSelective(Address address) {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        address.setAddressid(new Long(id).intValue());
        address.setStatus(CacheService.STATUS_INSERT);
        addressDA.saveAddress(address);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, address.getAddressid().toString(), (Object)address, 0);

        insertSelective_extra(address);
    }

    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        AddressUserDA addressUserDA=new AddressUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                AddressUser addressUser = addressUserDA.findAddressUserById(i);
                if(addressUser != null){
                    addressUserDA.removedAddressUserById(addressUser.getUserid());
                    redisu.del("address_u"+addressUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (addressUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Address address = addressDA.findAddressById(i);
                if(address != null){
                    if(null ==  address.getStatus()) {
                        addressDA.removedAddressById(address.getAddressid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  address.getStatus() && 1 == addressMapper.deleteByPrimaryKey(address.getAddressid())) {
                        addressDA.removedAddressById(address.getAddressid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  address.getStatus()  && 1 == addressMapper.insert(address)) {
                        addressDA.removedAddressById(address.getAddressid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  address.getStatus() && 1 == addressMapper.updateByPrimaryKey(address)) {
                        addressDA.removedAddressById(address.getAddressid());
                    }
                    redisu.hdel(classname, address.getAddressid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (addressDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, AddressJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Address> re = new ArrayList<Address>();          
            AddressExample addressExample = new AddressExample();
            addressExample.or().andAddressidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andAddressidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = addressMapper.selectByExample(addressExample);
            for (Address value : re) {
                redisu.hset(classname, value.getAddressid().toString(), value);
                addressDA.saveAddress(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Address r = addressDA.findAddressById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        AddressUserDA addressUserDA=new AddressUserDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<Address> re = new ArrayList<Address>();          
            AddressExample addressExample = new AddressExample();
            addressExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = addressMapper.selectByExample(addressExample);
            
            for (Address value : re) {
                AddressUser addressUser = addressUserDA.findAddressUserById(value.getUserid());
                if(addressUser == null){
                    addressUser = new AddressUser();
                }
                addressUser.addAddressList(value.getAddressid());

                redisu.sAdd("address_u"+value.getUserid().toString(), (Object)value.getAddressid());

                if(andAll && userID == value.getUserid()){
                    RefreshDBD(cacheService.PageID(value.getAddressid()), refresRedis);
                }

                addressUserDA.saveAddressUser(addressUser);
            }

            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    AddressUser r = addressUserDA.findAddressUserById(i);
                    if(r!= null){
                        List<Integer> li = r.getAddressList();
                        for(Integer addressid: li){
                          redisu.sAdd("address_u"+r.getUserid().toString(), (Object)addressid);   
                        }  
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}