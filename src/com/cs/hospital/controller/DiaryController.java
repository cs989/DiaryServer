package com.cs.hospital.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cs.hospital.Bean.RecordBean;
import com.cs.hospital.model.Image;
import com.cs.hospital.model.Message;
import com.cs.hospital.model.RecordDay;
import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class DiaryController extends Controller {

	public void index() {
		renderJson("测试成功！");
	}

	// 删除info的Record记录
	public void deleteRecord() {
		int rid = getParaToInt("rid", 0);
		try {
			if (rid != 0) {
				int count1 = Db.update("update recordday set isshow = 0 where rid = ?", rid);
				if (count1 == 1)
					renderJson("success");
			}
			renderJson("failed");
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 分页获取记录信息
	public void getRecordList() {
		int pageIndex = getParaToInt("pageIndex", 1);
		int pageSize = getParaToInt("pageSize", 10);
		Page<RecordDay> recordDayPage = RecordDay.dao.paginate(pageIndex, pageSize,
				"SELECT r.rid,r.pid,r.uid,r.title,r.content,r.ftime,u.name,p.purl,m.msg_count",
				"FROM recordday r LEFT JOIN userapp u  ON  r.uid = u.uid LEFT JOIN patient p ON r.pid = p.pid LEFT JOIN (SELECT rid,COUNT(rid) msg_count FROM message where isshow = 1 GROUP BY rid ) m ON r.rid = m.rid WHERE r.isshow = 1 ORDER BY r.ftime DESC");

		renderJson(recordDayPage);

	}

	// 根据rid获取image的对象列表
	public void getRecordByRid() {

		int rid = getParaToInt("rid", 0);
		try {
			if (rid != 0) {
				renderJson(RecordDay.dao.findById(rid));
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 根据rid获取image的对象列表
	public void getImageByRid() {

		int rid = getParaToInt("rid", 0);
		try {
			if (rid != 0) {
				List<Image> imageList = RecordDay.dao.findById(rid).getImages();

				//
				// List<Record> imageList = Db.find(
				// "SELECT m.*,u.name FROM message m LEFT JOIN userapp u ON
				// m.uid = u.uid WHERE m.rid = ?", rid);
				renderJson(imageList);
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 根据rid获取message的对象列表
	public void getMsgByRid() {

		int rid = getParaToInt("rid", 0);
		try {
			if (rid != 0) {

				List<Message> messageList = Message.dao.find(
						"SELECT m.*,u.name FROM message m LEFT JOIN userapp u ON m.uid = u.uid WHERE m.isshow = 1 AND u.isshow = 1 AND m.rid = ? ORDER BY m.time", rid);
				renderJson(messageList);
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 根据rid获取message的对象列表
	public void createMsg() {

		String content = getPara("content");
		int rid = getParaToInt("rid", 0);
		int uid = getParaToInt("uid", 1);
		String time = ConstantsUtil.getDateFormat4mysql();
		try {
			if (rid != 0 && uid != 0) {
				new Message().set("rid", rid).set("uid", uid).set("content", content).set("time", time).save();
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 根据rid获取message的对象列表
	public void deleteMsgByMid() {

		int mid = getParaToInt("mid", 0);
		try {
			if (mid != 0) {
				Message.dao.findById(mid).set("isshow", 0).update();
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 不含有图片
	public void updateRecord() {
		List<String> pathList = new ArrayList<>();
		int uid = 1; // 用户id
		int pid = 1; // 病人id
		String netPath = getPara("netPath");
		String[] ary = netPath.split(",");
		for (String s : ary) {
			if (s.length() > 0)
				pathList.add(s);
		}
		String title = getPara("title");
		String content = getPara("content");
		String ftime = ConstantsUtil.getDateFormat4mysql();
		int rid = getParaToInt("rid", 0);
		try {
			if (rid == 0)
				RecordBean.CreatRecord(pathList, uid, pid, title, content, ftime);
			else {
				RecordBean.UpdateRecord(pathList, uid, pid, title, content, ftime, rid);
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
		renderJson("success");
	}

	// 上传记录信息含有图片
	public void updateRecordImage() {

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
			int uid = 1; // 用户id
			int pid = 1; // 病人id
			String netPath = getPara("netPath");
			String[] ary = netPath.split(",");
			for (String s : ary) {
				if (s.length() > 0)
					pathList.add(s);
			}
			String title = getPara("title");
			String content = getPara("content");
			String ftime = ConstantsUtil.getDateFormat4mysql();
			int rid = getParaToInt("rid", 0);
			if (rid == 0)
				RecordBean.CreatRecord(pathList, uid, pid, title, content, ftime);
			else
				RecordBean.UpdateRecord(pathList, uid, pid, title, content, ftime, rid);
			// 存储路径
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
		renderJson("success");
	}
}
