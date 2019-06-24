package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Address;
import java.util.List;

/**
 * Created by 文辉 on 2017/7/25.
 */
public interface AddressService {

    //only read
    public List<Address> getAllAddressByExample(Integer UserID);
    
    public Address selectByPrimaryKey(Integer addressid);
     ///have write
    public void updateByPrimaryKeySelective(Address address);

    public void deleteByPrimaryKey(Integer addressid);

    public void insert(Address address);

    public void  insertSelective(Address address);

}
