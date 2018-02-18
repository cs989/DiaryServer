package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Department extends Model<Department>{
	public static final Department dao = new Department();
	
	public List<UserApp> getUsers() {
		return UserApp.dao.find("select * from UserApp where pid=?",  get("pid").toString());
	}
}
