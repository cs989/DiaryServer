package com.cs.hospital.config;

import com.cs.hospital.controller.DiaryController;
import com.cs.hospital.model.Department;
import com.cs.hospital.model.Focus;
import com.cs.hospital.model.Image;
import com.cs.hospital.model.Message;
import com.cs.hospital.model.Patient;
import com.cs.hospital.model.Profession;
import com.cs.hospital.model.RecordDay;
import com.cs.hospital.model.UserApp;
import com.cs.hospital.model.Version;
import com.cs.hospital.model.Video;
import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

public class JfinalConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setBaseUploadPath(ConstantsUtil.filePath);
		me.setMaxPostSize(100*1024*1024);//最大传输为100M
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		me.add("Diary",DiaryController.class);
//		me.add("/Message", MessageController.class);
//		me.add("/Patient", PatientController.class);
//		me.add("/login", LoginController.class);
//		me.add("/RecordDay", RecordDayController.class);
//		me.add("/user", UserController.class);
	}

	@Override
	public void configEngine(Engine me) {

	}

	@Override
	public void configPlugin(Plugins me) {
		String jdbcUrl = "jdbc:mysql://localhost:3306/diary";
		String userName = "root";
		String password = "HUAIhuai989";
		DruidPlugin dp = new DruidPlugin(jdbcUrl, userName, password);
		me.add(dp);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);

//		arp.addMapping("Department", Department.class);
		arp.addMapping("Department", "did", Department.class); // 定义
//		arp.addMapping("Image", Image.class);
		arp.addMapping("Image", "iid", Image.class); // 定义
//		arp.addMapping("Message", Message.class);
		arp.addMapping("Message", "mid", Message.class); // 定义
//		arp.addMapping("Patient", Patient.class);
		arp.addMapping("Patient", "pid", Patient.class); // 定义
//		arp.addMapping("Profession", Profession.class);
		arp.addMapping("Profession", "pid", Profession.class); // 定义
//		arp.addMapping("RecordDay", RecordDay.class);
		arp.addMapping("RecordDay", "rid", RecordDay.class); // 定义
//		arp.addMapping("UserApp", UserApp.class);
		arp.addMapping("UserApp", "uid", UserApp.class); // 定义
//		arp.addMapping("Video", Video.class);
		arp.addMapping("Video", "did", Video.class); // 定义
		arp.addMapping("Focus", "fid", Focus.class); // 定义
		arp.addMapping("Version", "vid", Version.class); // 定义

		me.add(arp);
	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {

	}

}
