package com.cs.hospital.model;

import com.jfinal.plugin.activerecord.Model;

public class Video  extends Model<Video>{
	public static final Video dao = new Video();
	
	public RecordDay getRecord(){
		return RecordDay.dao.findById(get("rid").toString());
	}
}