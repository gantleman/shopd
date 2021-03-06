package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Address;
import java.util.List;

public interface AddressService {

    //only read
    public List<Address> getAllAddressByUser(Integer UserID, String url);
    
    public Address getAddressByKey(Integer addressid, String url);
     ///have write
    public void updateByPrimaryKeySelective(Address address);

    public void deleteByPrimaryKey(Integer addressid);

    public void  insertSelective(Address address);

    public void Clean(Boolean all) ;

    public void Clean_extra(Boolean all);

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);
}
