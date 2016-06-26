package com.xeehoo.rpc.service;

import com.xeehoo.rpc.model.User;

import java.util.Date;


public class UserServiceImpl implements UserService{
	public User getUser(Integer id){
		User u = new User();
		u.setAge(42);
		u.setName("wangzh");
		u.setBirth(new Date());
		
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return u;
	}
}
