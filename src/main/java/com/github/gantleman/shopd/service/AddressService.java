package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.*;


import java.util.List;

/**
 * Created by 文辉 on 2017/7/25.
 */
public interface AddressService {
    public List<Address> getAllAddressByExample(AddressExample addressExample);

    public void updateByPrimaryKeySelective(Address address);

    public void deleteByPrimaryKey(Integer addressid);

    public void insert(Address address);

    public void  insertSelective(Address address);

    public Address selectByPrimaryKey(Integer addressid);
}
