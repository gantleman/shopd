package com.github.gantleman.shopd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.github.gantleman.shopd.da.UserDA;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.jobs.AdminJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.HttpUtils;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.Assert;



@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	RedisUtil ru;

	@Autowired
    HttpUtils httputils;


    @Autowired
    private QuartzManager quartzManager;

    @Autowired
	private AdminJob job;
	
	@Test
	public void contextLoads() {

	}

	@Test
	public void t2() {
		// TODO Auto-generated method stub
		
		//打开数据库和存储环境
		BDBEnvironmentManager.getInstance();
		UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
		
		userDA.saveUser(new User((int) 1L, "A", "root1"));
		userDA.saveUser(new User((int) 2L, "admin", "root2"));
		userDA.saveUser(new User((int) 3L, "A", "root3"));
		userDA.saveUser(new User((int) 4L, "admin", "root4"));
		
		System.out.println(userDA.findAllUserCount());
		System.out.println(userDA.findAllUserByUserNameCount("admin"));
		
		printAllDataByUserName(userDA,"admin");
		
		printAllData(userDA);
		
		userDA.removedUserById((int) 2L);
		
		printAllData(userDA);
		
		userDA.removedUserByUserName("admin");
		
		printAllData(userDA);
		
		userDA.saveUser(new User((int) 1L, "admin", "root1"));
		
		printAllData(userDA);

		User user = userDA.findUserById(5);
		System.out.println(user);


		BDBEnvironmentManager.getMyEnvironment().sync();
	}
	
	private static void printAllData(UserDA userDA) {
		// TODO Auto-generated method stub
		System.out.println("------start--------");
		 List<User> userList=userDA.findAllUser();
		for (User user : userList) {
			    System.out.println(user.getUserid());
				System.out.println(user.getUsername());
				System.out.println(user.getPassword());
		}
		System.out.println("------end--------");
	}
	
	private static void printAllDataByUserName(UserDA userDA,String userName) {
		// TODO Auto-generated method stub
		System.out.println("------start--------");
		 List<User> userList=userDA.findAllUserByUserName(userName);
		for (User user : userList) {
			    System.out.println(user.getUserid());
				System.out.println(user.getUsername());
				System.out.println(user.getPassword());
		}
		System.out.println("------end--------");
	}

	@Test
	public void redis() {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date time=null;

		try {
			time = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertNotNull(ru);
		ru.set("testRedis", time.toString());

		Admin admin;
		admin = new Admin();
		ru.set("admin", admin);
	}

	//@Test
	public void t3() {		
		//打开数据库和存储环境
		BDBEnvironmentManager.getInstance();
		UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

		long stamp = System.currentTimeMillis();
		System.out.println("add user:" + stamp);
		for( int i= 0;  i< 1000000; i++){
			userDA.saveUser(new User((int) i, "aa" + i, "root1"));

			if (i == 10) {
				System.out.println("add user 10:" + (System.currentTimeMillis()-stamp));
			}else if (i == 100) {
				System.out.println("add user 100:" + (System.currentTimeMillis()-stamp));
			}else if (i == 1000) {
				System.out.println("add user 1000:" + (System.currentTimeMillis()-stamp));
				break;
			}else if(i == 10000){
				System.out.println("add user 10000:" + (System.currentTimeMillis()-stamp));
			}else if(i == 100000){
				System.out.println("add user 100000:" + (System.currentTimeMillis()-stamp));
			}
		}
		//System.out.println("add user 1000000:" + (System.currentTimeMillis()-stamp));

		stamp = System.currentTimeMillis();
		System.out.println("stamp user:" + stamp);
		List<User> ru = userDA.findAllUserWhitStamp(System.currentTimeMillis());
		System.out.println("stamp return user:" + ru.size() + ":::" + (System.currentTimeMillis()-stamp));

		stamp = System.currentTimeMillis();
		System.out.println("del user:" + stamp);
		for( int i= 0;  i< 1000; i++){
			userDA.removedUserById(i);
		}
		System.out.println("del user:" + (System.currentTimeMillis()-stamp));

		
		BDBEnvironmentManager.getMyEnvironment().sync();
	}

	@Test
	public void t4() {
		System.out.println("redis exprie value --------"+ ru.getiExprie());
		System.out.println("SimpleName -----------" + BDBEnvironmentManager.class.getSimpleName());
	}

	@Test
	public void t5() {
		try {
			System.out.println("baidu.com --------" + httputils.doGet("http://baidu.com", "/").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void t6() {
		ru.sAddAndTime("key", 100, 0);
	}
}
