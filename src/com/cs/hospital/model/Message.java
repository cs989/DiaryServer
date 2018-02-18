package com.cs.hospital.model;

import com.jfinal.plugin.activerecord.Model;

public class Message extends Model<Message> {
	public static final Message dao = new Message();

	public RecordDay getRecord() {
		return RecordDay.dao.findById(get("rid").toString());
	}

	public UserApp getUsers() {
		return UserApp.dao.findById(get("uid").toString());
	}
}
