package com.cs.hospital.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cs.hospital.model.Image;
import com.cs.hospital.model.RecordDay;
import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

public class DiaryController extends Controller {

	public void index() {
		renderJson("测试成功！");
	}

	public void getRecordList() {
		int pageIndex = getParaToInt("pageIndex", 1);
		int pageSize = getParaToInt("pageSize", 10);
		Page<RecordDay> recordDayPage = RecordDay.dao.paginate(pageIndex, pageSize,
				"SELECT r.rid,r.pid,r.uid,r.title,r.content,r.ftime,u.name,p.purl,m.msg_count",
				"FROM recordday r LEFT JOIN userapp u  ON  r.uid = u.uid AND r.isshow = 1 LEFT JOIN patient p ON r.pid = p.pid LEFT JOIN (SELECT rid,COUNT(rid) msg_count FROM message GROUP BY rid ) m ON r.rid = m.rid ORDER BY r.ftime DESC");
		
		renderJson(recordDayPage);

	}

	// 上传记录信息
	public void createRecord() {

		List<String> pathList = new ArrayList<>();
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

					String datePath = ConstantsUtil.getDateFormat() + "/" + fileName;
					pathList.add(datePath);
				}
			}
			int uid = 1; //用户id
			int pid = 1; //病人id
			String title = getPara("title");
			String content = getPara("content");
			String ftime = ConstantsUtil.getDateFormat4mysql();
			boolean succeed = Db.tx(new IAtom() {
				public boolean run() throws SQLException {
					RecordDay recordDay = new RecordDay().set("uid", uid).set("pid", pid).set("title", title).set("content", content)
							.set("ftime", ftime);
					recordDay.save();
					int rid = recordDay.get("rid");
					if (pathList.size() > 0) {
						for (String path : pathList) {
							Image image = new Image().set("rid", rid).set("iurl", path).set("utime", ftime);
							if (image.save()) {
								continue;
							} else {
								return false;
							}
						}
						return true;
					}

					return true;
				}
			});
			// 存储路径
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		renderJson("success");
	}
}
