package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Address;
import java.util.List;

public interface AddressService {

    //only read
    public List<Address> getAllAddressByUserID(Integer UserID);
    
    public Address selectByPrimaryKey(Integer addressid);
    
    public Address getAddressByKey(Integer addressid);
     ///have write
    public void updateByPrimaryKeySelective(Address address);

    public void deleteByPrimaryKey(Integer addressid);

    public void  insertSelective(Address address);

    public void TickBack();
}
