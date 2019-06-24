package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.AddressExample;
import com.github.gantleman.shopd.service.AddressService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService, Job {

    @Autowired(required = false)
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAllAddressByExample(Integer UserID) {

        AddressExample addressExample=new AddressExample();
        addressExample.or().andUseridEqualTo(UserID);
        
        return addressMapper.selectByExample(addressExample);
    }

    @Override
    public void updateByPrimaryKeySelective(Address address) {
        addressMapper.updateByPrimaryKeySelective(address);
    }

    @Override
    public void deleteByPrimaryKey(Integer addressid) {
        addressMapper.deleteByPrimaryKey(addressid);
    }

    @Override
    public void insert(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void insertSelective(Address address) {
        addressMapper.insertSelective(address);
    }

    @Override
    public Address selectByPrimaryKey(Integer addressid) {
        return addressMapper.selectByPrimaryKey(addressid);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}
}
