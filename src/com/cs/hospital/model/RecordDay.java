package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class RecordDay  extends Model<RecordDay>{
	public static final RecordDay dao = new RecordDay();
	
	public Patient getPatient(){
		return Patient.dao.findById(get("pid"));
	}
	
	public UserApp getUser(){
		return UserApp.dao.findById(get("uid"));
	}
	
	public List<Image> getImages() {
		return Image.dao.find("select * from Image where rid=?",  get("rid").toString());
	}
	
	public List<Video> getVideos() {
		return Video.dao.find("select * from Video where rid=?",  get("rid").toString());
	}
	
	public List<Message> getMessages() {
		return Message.dao.find("select * from Message where rid=?",  get("rid").toString());
	}
}