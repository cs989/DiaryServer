package com.cs.hospital.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cs.hospital.model.UserApp;

public class LoginUtil {

	public static boolean CheckLogin(String userName, String passWord) {
		if (StringUtil.StringIsNullOrEmpty(userName) || StringUtil.StringIsNullOrEmpty(passWord))
			return false;

		try {
			String sql ="select * from user where lname =?";
			
			Map<String, Object> para = new HashMap<String, Object>();
			para.put("passwd", 1);
			List<UserApp> passwd = UserApp.dao.find(sql,userName);
			if(StringUtil.StringIsNullOrEmpty(passwd.toString()))
				return false;

		} catch (Exception ex) {
			return false;
		}

		return true;
	}
}
