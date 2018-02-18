package com.cs.hospital.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.upload.UploadFile;

public class DiaryController extends Controller {

	public void index() {
		renderJson("测试成功！");
	}

	// 上传图片
	public void upLoad() {
		
		List<String> pathList =new ArrayList<>();
		try {
			// 必须先调用getFile方法才能getPara
			for (int i = 0; i <= 100; i++) {
				String curFilePath = ConstantsUtil.getFileName();
				String fileName = ConstantsUtil.getRandomFileName();
				UploadFile file = getFile("file" + i);
				if (file == null)
					break;
				if (file.getFile().length() > 5 * 1024 * 1024) {
					renderJson("文件长度超过限制，必须小于5M");
				} else {
					// 上传文件
					String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀
					fileName = fileName + type; // 对文件重命名取得的文件名+后缀
					String dest = curFilePath + "/" + fileName;
					file.getFile().renameTo(new File(dest));

					String datePath = curFilePath + "\\" + fileName;
					pathList.add(datePath);
				}
			}
			String title = getPara("title");
			String context = getPara("context");
			boolean succeed = Db.tx(new IAtom(){
			    public boolean run() throws SQLException {
			        int count = Db.update("INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)", 100, 123);
			        int count2 = Db.update("update account set cash = cash + ? where id = ?", 100, 456);
			        return count == 1 && count2 == 1;
			    }
			});
			// 存储路径
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		renderJson("success");
	}
}
