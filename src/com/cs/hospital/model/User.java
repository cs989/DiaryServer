package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User> {
	public static final User dao = new User();

	public Profession getProfession() {
		return Profession.dao.findById(get("pid"));
	}

	public Department getDepartment() {
		return Department.dao.findById(get("did"));
	}

	public List<RecordDay> getRecords() {
		return RecordDay.dao.find("select * from Record where RecordDay=?", get("rid").toString());
	}

	public List<Message> getMessages() {
		return Message.dao.find("select * from Message where uid=?", get("uid").toString());
	}
}