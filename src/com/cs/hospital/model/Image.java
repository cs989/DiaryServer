package com.cs.hospital.model;

import com.jfinal.plugin.activerecord.Model;

public class Image extends Model<Image>{
	public static final Image dao = new Image();
	
	public RecordDay getRecord(){
		return RecordDay.dao.findById(get("rid").toString());
	}
}
