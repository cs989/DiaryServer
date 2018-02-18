package com.cs.hospital.controller;

import java.io.File;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

public class LoginController extends Controller {

	public void index() {
		// User user = User.dao.findFirst("select * from User where uid=1");
		// renderJson(user);

		// String username = getPara("userName");
		// String passwd = getPara("passwd");

		UploadFile file = getFile("file");
		
		

		File delfile = new File(file.getUploadPath() + "\\" + file.getFileName());
		
		
		// File file = getPara("file");
		// if(LoginUtil.CheckLogin(username, passwd))
		renderJson("success");
		// else
		// renderJson("faild");
		// username = getPara(0);
		// passwd = getPara(1);
		// String str = getPara();
		// if (username == null || username.equals("")) {
		// renderJson("不可输入为空");
		// }

	}

	public void exit() {

	}
}
