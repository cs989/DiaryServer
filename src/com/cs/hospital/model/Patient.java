package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Patient extends Model<Patient>{
	public static final Patient dao = new Patient();
	
	public List<RecordDay> getCollects() {
		return RecordDay.dao.find("select * from RecordDay where pid=?",  get("pid").toString());
	}
}