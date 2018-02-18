package com.cs.hospital.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Profession extends Model<Profession>{
	public static final Profession dao = new Profession();
	
	public List<Profession> getProfessions() {
		return Profession.dao.find("select * from Profession where pid=?",  get("pid").toString());
	}
}