package com.github.gantleman.shopd;

import java.io.File;
import java.util.List;

import com.github.gantleman.shopd.da.UserDA;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {

	}

	@Test
	public void t2() {
		// TODO Auto-generated method stub
		
		//打开数据库和存储环境
		BDBEnvironmentManager.getInstance(new File("bdb"),false);
		
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
		
		BDBEnvironmentManager.close();
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
}
