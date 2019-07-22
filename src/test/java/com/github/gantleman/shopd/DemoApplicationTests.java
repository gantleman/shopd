package com.github.gantleman.shopd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gantleman.shopd.da.UserDA;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.jobs.AdminJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.HttpUtils;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.ServerConfig;

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

	@Autowired
    private ServerConfig serverConfig;
	
	@Test
	public void contextLoads() {

	}

	@Test
	public void t2() {		
		//打开数据库和存储环境
/*		BDBEnvironmentManager.getInstance();
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


		BDBEnvironmentManager.getMyEnvironment().sync();*/
	}
	
	private static void printAllData(UserDA userDA) {
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
		/*BDBEnvironmentManager.getInstance();
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
		List<User> ru = userDA.findAllWhitStamp(System.currentTimeMillis());
		System.out.println("stamp return user:" + ru.size() + ":::" + (System.currentTimeMillis()-stamp));

		stamp = System.currentTimeMillis();
		System.out.println("del user:" + stamp);
		for( int i= 0;  i< 1000; i++){
			userDA.removedUserById(i);
		}
		System.out.println("del user:" + (System.currentTimeMillis()-stamp));

		
		BDBEnvironmentManager.getMyEnvironment().sync();*/
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
			e.printStackTrace();
		}
	}

	@Test
	public void t6() {
		ru.sAddAndTime("key", 100, 0);
		System.out.println("serverurl--------" +serverConfig.getUrl());
	}

	@Test
	public void t7() {
		ru.hset("routeconfig", "/admin/activity/show", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/activity/showjson", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/activity/add", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/activity/addResult", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/activity/update", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/activity/delete", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/order/send", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/order/sendGoods", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/order/receiver", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/order/complete", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/showjson", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/show", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/add", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/update", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/delete", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/addGoodsSuccess", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/addCategory", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/addCategoryResult", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/saveCate", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/deleteCate", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/goods/show", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/login", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/confirmLogin", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/logout", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/user/showjson", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/user/show", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/user/delete", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/addCart", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/showcart", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/cartjson", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/deleteCart", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/update", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/chat", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/chatto", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/getMessage", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/admin/chat", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/adminchat", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/sendMessage", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/chatrobot", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/login", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/register", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/registerresult", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/loginconfirm", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/information", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/saveInfo", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/info/address", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/saveAddr", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/deleteAddr", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/insertAddr", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/info/list", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/deleteList", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/info/favorite", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/savePsw", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/finishList", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/logout", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/detail", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/search", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/collect", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/deleteCollect", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/category", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/comment", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/main", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/order", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/orderFinish", "http://127.0.1.1:8081");
		ru.hset("routeconfig", "/verificationcodeimg", "http://127.0.1.1:808181");
	}


	//@Test
	public void t8() {
		Map<String, String> headers = new HashMap<>(); 
		Map<String, String> querys = new HashMap<>();                
		querys.put("id", "1");
		try {
			httputils.doGet("http://127.0.1.1:8081", "/activitypage", headers, querys);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
