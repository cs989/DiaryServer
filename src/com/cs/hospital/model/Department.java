package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Department extends Model<Department>{
	public static final Department dao = new Department();
	
	public List<User> getUsers() {
		return User.dao.find("select * from User where pid=?",  get("pid").toString());
	}
}
