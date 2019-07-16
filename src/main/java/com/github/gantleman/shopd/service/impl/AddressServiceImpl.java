package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.AddressDA;
import com.github.gantleman.shopd.da.AddressUserDA;
import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.dao.AddressUserMapper;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.AddressExample;
import com.github.gantleman.shopd.entity.AddressUser;
import com.github.gantleman.shopd.entity.AddressUserExample;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.AddressJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;  

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired(required = false)
    private AddressMapper addressMapper;

    @Autowired(required = false)
    private AddressUserMapper addressUserMapper;

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
    public List<Address> getAllAddressByUserID(Integer UserID, String url) {
        List<Address> re = null;

        if(redisu.hasKey("Address_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Address_u"+UserID.toString());
            re = new ArrayList<Address>();
            for (Object id : ro) {
                Address r =  getAddressByKey((Integer)id, url);
                if (r != null)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/addressuserpage", UserID);
            }else{
                RefreshUserDBD(UserID, true, true);
            }

            if(redisu.hasKey("Address_u"+UserID.toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("Address_u"+UserID.toString());
                re = new ArrayList<Address>();
                for (Object id : ro) {
                    Address r =  getAddressByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
            }
        }
        return re;
    }

    @Override
    public Address getAddressByKey(Integer addressid, String url) {
        Address re = null;
        Integer pageId = cacheService.PageID(addressid);
        if(!redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Address) redisu.hget(classname, addressid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/addresspage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname, addressid.toString())) {
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
            List<Integer> addressList = new ArrayList<>();
            addressList.remove(address.getAddressid());
            JSONArray jsonarray = JSONArray.fromObject(addressList);
            addressUser.setAddressList(jsonarray.toString());

            if(addressUser.getAddressSize() >= 1){
                addressUser.setAddressSize(addressUser.getAddressSize() - 1);
                //Re-publish to redis
                 redisu.setRemove("Address_u" + address.getUserid().toString(), address.getAddressid());
            } else if(addressUser.getAddressSize() == 0){
                //list empty
                if(addressUser.getStatus() == CacheService.STATUS_INSERT){
                    addressUserDA.removedAddressUserById(addressUser.getUserid());
                }else{
                    addressUser.setStatus(CacheService.STATUS_DELETE);
                }
                //Re-publish to redis
                redisu.del("Address_u" + address.getUserid().toString());
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
             redisu.hdel(classname, address.getAddressid().toString(), 0);
             
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
            List<Integer> addressIdList = new ArrayList<>();
            addressIdList.add(address.getAddressid());
            JSONArray jsonArray = JSONArray.fromObject(addressIdList);

            addressUser = new AddressUser();
            addressUser.setAddressSize(1); 
            addressUser.setAddressList(jsonArray.toString());
            addressUser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> addressIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(addressUser.getAddressList());
            addressIdList = JSONArray.toList(jsonArray,Integer.class);
            addressIdList.add(address.getAddressid());

            addressUser.setAddressSize(addressUser.getAddressSize() + 1); 
            addressUser.setAddressList(jsonArray.toString());
            if(addressUser.getStatus() == null || addressUser.getStatus() == CacheService.STATUS_DELETE)
                addressUser.setStatus(CacheService.STATUS_UPDATE);
        }
        addressUserDA.saveAddressUser(addressUser);

        //Re-publish to redis
        redisu.sAdd("Address_u" + address.getUserid().toString(), address.getAddressid()); 
    }

    @Override
    public void insertSelective(Address address) {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
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
    public void TickBack_extra() {
        BDBEnvironmentManager.getInstance();
        AddressUserDA addressUserDA=new AddressUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                AddressUser addressUser = addressUserDA.findAddressUserById(i);
                if(addressUser != null){
                    if(null ==  addressUser.getStatus()) {
                        addressUserDA.removedAddressUserById(addressUser.getUserid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  addressUser.getStatus() && 1 == addressUserMapper.deleteByPrimaryKey(addressUser.getUserid())) {
                        addressUserDA.removedAddressUserById(addressUser.getUserid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  addressUser.getStatus()  && 1 == addressUserMapper.insert(addressUser)) {
                        addressUserDA.removedAddressUserById(addressUser.getUserid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  addressUser.getStatus() && 1 == addressUserMapper.updateByPrimaryKey(addressUser)) {
                        addressUserDA.removedAddressUserById(addressUser.getUserid());
                    }
                    redisu.del("Address_u"+addressUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (addressUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
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

        TickBack_extra();
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (cacheService.IsCache(classname, pageID)) {
            BDBEnvironmentManager.getInstance();
            AddressDA addressDA=new AddressDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Address> re = new ArrayList<Address>();          
            AddressExample addressExample = new AddressExample();
            addressExample.or().andAddressidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            addressExample.or().andAddressidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = addressMapper.selectByExample(addressExample);
            for (Address value : re) {
                redisu.hset(classname, value.getAddressid().toString(), value);
                addressDA.saveAddress(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            quartzManager.addJob(classname,classname,classname,classname, AddressJob.class, null, job);
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
        if (cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<AddressUser> re = new ArrayList<AddressUser>();          
            AddressUserExample addressUserExample = new AddressUserExample();
            addressUserExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)));
            addressUserExample.or().andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = addressUserMapper.selectByExample(addressUserExample);
            for (AddressUser value : re) {
                addressUserDA.saveAddressUser(value);

                List<Integer> addressIdList = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(value.getAddressList());
                addressIdList = JSONArray.toList(jsonArray, Integer.class);

                for(Integer addressId: addressIdList){
                    redisu.sAdd("Address_u"+value.getUserid().toString(), (Object)addressId);
                }

                if(andAll && userID == value.getUserid() && value.getAddressSize() != 0){  
                    for(Integer addressId: addressIdList){
                        RefreshDBD(cacheService.PageID(addressId), refresRedis);
                    }
                }
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    AddressUser r = addressUserDA.findAddressUserById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.sAdd("Address_u"+r.getUserid().toString(), (Object)r.getAddressList()); 
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}