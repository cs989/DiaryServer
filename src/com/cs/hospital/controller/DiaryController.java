package com.cs.hospital.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cs.hospital.Bean.RecordBean;
import com.cs.hospital.model.Image;
import com.cs.hospital.model.Message;
import com.cs.hospital.model.Patient;
import com.cs.hospital.model.RecordDay;
import com.cs.hospital.model.UserApp;
import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

public class DiaryController extends Controller {

	// 测试
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
		boolean isFocus = getParaToBoolean("isFocus", false);
		int uid = getParaToInt("uid", 0);
		int pro_id = getParaToInt("pro_id", 3);
		if (pro_id == 3) {
			if (uid != 0) {
				try {
					Page<RecordDay> recordDayPage = RecordDay.dao.paginate(pageIndex, pageSize,
							"SELECT r.rid,r.pid,r.uid,r.title,r.content,r.ftime,u.name,p.purl,m.msg_count",
							"FROM recordday r LEFT JOIN userapp u  ON  r.uid = u.uid LEFT JOIN patient p ON r.pid = p.pid LEFT JOIN (SELECT rid,COUNT(rid) msg_count FROM message WHERE isshow = 1 GROUP BY rid ) m ON r.rid = m.rid WHERE r.isshow = 1 AND r.pid IN (SELECT p.pid FROM patient p INNER JOIN userapp u WHERE  p.pno=u.lname AND u.uid =?) ORDER BY r.ftime DESC",
							uid);
					renderJson(recordDayPage);
				} catch (Exception ex) {
					// ex.printStackTrace();
					renderJson(ex.toString());
				}

			}
		} else {
			String tepsql = "";
			if (isFocus)
				tepsql = "INNER JOIN (SELECT * FROM  focus WHERE uid = " + uid + " ) f ON r.pid=f.pid";
			else {
				tepsql = "";
			}
			try {
				if (uid != 0) {
					Page<RecordDay> recordDayPage = RecordDay.dao.paginate(pageIndex, pageSize,
							"SELECT r.rid,r.pid,r.uid,r.title,r.content,r.ftime,u.name,p.purl,m.msg_count",
							"FROM recordday r LEFT JOIN userapp u  ON  r.uid = u.uid LEFT JOIN patient p ON r.pid = p.pid LEFT JOIN (SELECT rid,COUNT(rid) msg_count FROM message where isshow = 1 GROUP BY rid ) m ON r.rid = m.rid "
									+ tepsql + " WHERE r.isshow = 1 ORDER BY r.ftime DESC");

					renderJson(recordDayPage);
				}

			} catch (Exception ex) {
				// ex.printStackTrace();
				renderJson(ex.toString());
			}
		}
	}

	// 分页获取患者信息
	public void getPatientList() {

		int pageIndex = getParaToInt("pageIndex", 1);
		int pageSize = getParaToInt("pageSize", 10);
		int uid = getParaToInt("uid", 0);
		boolean isFocus = getParaToBoolean("isFocus", false);
		String tepsql = "";
		if (isFocus)
			tepsql = "(f.uid = ?)";
		else {
			tepsql = "(f.uid IS NULL OR f.uid = ?)";
		}
		try {
			Page<Patient> recordDayPage = Patient.dao.paginate(pageIndex, pageSize, "SELECT p.*,u.name,f.uid AS userid",
					"FROM patient p LEFT JOIN userapp  u  ON p.uid = u.uid LEFT JOIN (SELECT * FROM focus WHERE uid = ?) f ON p.pid = f.pid WHERE p.isshow = 1 AND u.isshow = 1 AND "
							+ tepsql + " ORDER BY p.ptime DESC",
					uid, uid);

			renderJson(recordDayPage);
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}

	}

	// 创建患者信息（不含图片）
	public void creategPatient() {
		String pno = getPara("pno");
		int uid = getParaToInt("uid", 0);
		String name = getPara("name");
		String sex = getPara("sex");
		String birthday = getPara("birthday");
		if (birthday.equals(""))
			birthday = "2000-01-01 00:00:00";
		else {
			birthday = birthday + " 00:00:00";
		}
		String tel = getPara("tel");
		String ptime = ConstantsUtil.getDateFormat4mysql();
		String pcondition = getPara("pcondition");
		String purl = getPara("purl");
		try {
			if (uid != 0) {
				RecordBean.CreatPatient(pno, uid, name, sex, birthday, tel, ptime, pcondition, purl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 创建患者信息（含图片）
	public void creategPatientImage() {

		try {
			String curFilePath = ConstantsUtil.getFileName();
			String fileName = ConstantsUtil.getRandomFileName();
			UploadFile file = getFile("file0");
			String datePath = "";
			if (file.getFile().length() > 5 * 1024 * 1024) {
				renderJson("文件长度超过限制，必须小于5M");
			} else {
				// 上传文件
				String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀
				fileName = fileName + type; // 对文件重命名取得的文件名+后缀
				String dest = curFilePath + "/" + fileName;
				file.getFile().renameTo(new File(dest));

				datePath = ConstantsUtil.getDateFormat() + "/" + fileName;
			}
			String pno = getPara("pno");
			int uid = getParaToInt("uid", 0);
			String name = getPara("name");
			String sex = getPara("sex");
			String birthday = getPara("birthday");
			if (birthday.equals(""))
				birthday = "2000-01-01 00:00:00";
			else {
				birthday = birthday + " 00:00:00";
			}
			String tel = getPara("tel");
			String ptime = ConstantsUtil.getDateFormat4mysql();
			String pcondition = getPara("pcondition");
			String purl = datePath;
			if (uid != 0) {
				RecordBean.CreatPatient(pno, uid, name, sex, birthday, tel, ptime, pcondition, purl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 修改患者信息（不含图片）
	public void updatePatientByPid() {
		int pid = getParaToInt("pid", 0);
		String pno = getPara("pno");
		int uid = getParaToInt("uid", 0);
		String name = getPara("name");
		String sex = getPara("sex");
		String birthday = getPara("birthday");
		String tel = getPara("tel");
		String ptime = ConstantsUtil.getDateFormat4mysql();
		String pcondition = getPara("pcondition");
		String purl = getPara("purl");
		try {
			if (pid != 0 && uid != 0) {
				RecordBean.UpdatePatient(pid, pno, uid, name, sex, birthday, tel, ptime, pcondition, purl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 修改患者信息（含图片）
	public void updategPatientByPidImage() {

		try {
			String curFilePath = ConstantsUtil.getFileName();
			String fileName = ConstantsUtil.getRandomFileName();
			UploadFile file = getFile("file0");
			String datePath = "";
			if (file.getFile().length() > 5 * 1024 * 1024) {
				renderJson("文件长度超过限制，必须小于5M");
			} else {
				// 上传文件
				String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀
				fileName = fileName + type; // 对文件重命名取得的文件名+后缀
				String dest = curFilePath + "/" + fileName;
				file.getFile().renameTo(new File(dest));

				datePath = ConstantsUtil.getDateFormat() + "/" + fileName;
			}
			int pid = getParaToInt("pid", 0);
			String pno = getPara("pno");
			int uid = getParaToInt("uid", 0);
			String name = getPara("name");
			String sex = getPara("sex");
			String birthday = getPara("birthday");
			String tel = getPara("tel");
			String ptime = ConstantsUtil.getDateFormat4mysql();
			String pcondition = getPara("pcondition");
			String purl = datePath;
			if (pid != 0 && uid != 0) {
				RecordBean.UpdatePatient(pid, pno, uid, name, sex, birthday, tel, ptime, pcondition, purl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 获取患者信息
	public void getPatientByPid() {
		int pid = getParaToInt("pid", 0);
		try {
			if (pid != 0) {
				renderJson(Patient.dao.findById(pid));
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	public void searchPatientBytext() {
		int pageIndex = getParaToInt("pageIndex", 1);
		int pageSize = getParaToInt("pageSize", 10);
		String seachtext = getPara("seachtext");
		try {
			Page<Patient> recordDayPage = Patient.dao.paginate(pageIndex, pageSize, "SELECT *",
					"FROM patient WHERE pno LIKE '%" + seachtext + "%' OR NAME LIKE '%" + seachtext + "%'");
			renderJson(recordDayPage);
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 删除患者信息
	public void deletePatientByPid() {
		int pid = getParaToInt("pid", 0);
		try {
			if (pid != 0) {
				int count1 = Db.update("update patient set isshow = 0 where pid = ?", pid);
				if (count1 == 1)
					renderJson("success");
			}
			renderJson("failed");
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
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
						"SELECT m.*,u.name,u.uurl FROM message m LEFT JOIN userapp u ON m.uid = u.uid WHERE m.isshow = 1 AND u.isshow = 1 AND m.rid = ? ORDER BY m.time",
						rid);
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
		int uid = getParaToInt("uid", 0);
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

	// 修改记录信息不含有图片
	public void updateRecord() {
		List<String> pathList = new ArrayList<>();
		int uid = getParaToInt("uid", 0);
		int pid = getParaToInt("pid", 0);// 病人id
		int pro_id = getParaToInt("pro_id", 3);// 病人id
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
			if (rid == 0) {
				if (pro_id == 3) {
					Patient patient = Patient.dao.findFirst(
							"SELECT p.* FROM patient p INNER JOIN userapp u ON p.pno=u.lname and u.uid = ?", uid);
					pid = patient.getInt("pid");
				}
				RecordBean.CreatRecord(pathList, uid, pid, title, content, ftime);
			} else {
				RecordBean.UpdateRecord(pathList, title, content, ftime, rid);
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
		renderJson("success");
	}

	// 修改记录信息含有图片
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
			int uid = getParaToInt("uid", 0);
			int pid = getParaToInt("pid", 0);// 病人id
			int pro_id = getParaToInt("pro_id", 3);// 病人id
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
			if (rid == 0) {
				if (pro_id == 3) {
					Patient patient = Patient.dao.findFirst(
							"SELECT p.* FROM patient p INNER JOIN userapp u ON p.pno=u.lname and u.uid = ?", uid);
					pid = patient.getInt("pid");
				}
				RecordBean.CreatRecord(pathList, uid, pid, title, content, ftime);
			} else
				RecordBean.UpdateRecord(pathList, title, content, ftime, rid);
			// 存储路径
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
		renderJson("success");
	}

	public void loginCheck() {
		String username = getPara("username");
		String password = getPara("password");
		try {
			renderJson(Db.findFirst("SELECT uid,pid FROM userapp where lname = ? and password = ? and isshow = 1 ",
					username, password));
		} catch (Exception ex) {
			renderJson(ex.toString());
		}
	}

	// 创建用户信息
	public void createUser() {
		String name = getPara("name");
		String sex = getPara("sex");
		String tel = getPara("tel");
		String ptime = ConstantsUtil.getDateFormat4mysql();
		String lname = getPara("lname");
		String password = getPara("password");
		String uurl = getPara("uurl");
		try {
			UserApp userapp = UserApp.dao.findFirst("SELECT * FROM userapp u where u.lname = ?", lname);
			if (userapp == null) {
				RecordBean.CreateUser(name, sex, tel, ptime, lname, password, uurl);
				renderJson("success");
			} else {
				renderJson("用户名已存在！");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 创建用户信息（含图片）
	public void createUserImage() {

		try {
			String curFilePath = ConstantsUtil.getFileName();
			String fileName = ConstantsUtil.getRandomFileName();
			UploadFile file = getFile("file0");
			String datePath = "";
			if (file.getFile().length() > 5 * 1024 * 1024) {
				renderJson("文件长度超过限制，必须小于5M");
			} else {
				// 上传文件
				String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀
				fileName = fileName + type; // 对文件重命名取得的文件名+后缀
				String dest = curFilePath + "/" + fileName;
				file.getFile().renameTo(new File(dest));

				datePath = ConstantsUtil.getDateFormat() + "/" + fileName;
			}
			String name = getPara("name");
			String sex = getPara("sex");
			String tel = getPara("tel");
			String ptime = ConstantsUtil.getDateFormat4mysql();
			String lname = getPara("lname");
			String password = getPara("password");
			String uurl = datePath;
			UserApp userapp = UserApp.dao.findFirst("SELECT * FROM userapp u where u.lname = ?", lname);
			if (userapp == null) {

				RecordBean.CreateUser(name, sex, tel, ptime, lname, password, uurl);
				renderJson("success");
			} else {
				renderJson("用户名已存在！");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 修改患者信息（不含图片）
	public void updateUserByUid() {
		int uid = getParaToInt("uid", 0);
		String name = getPara("name");
		String sex = getPara("sex");
		String tel = getPara("tel");
		String ptime = ConstantsUtil.getDateFormat4mysql();
		String password = getPara("password");
		String uurl = getPara("uurl");
		try {
			if (uid != 0) {
				RecordBean.UpdateUser(uid, name, sex, tel, ptime, password, uurl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 修改患者信息（含图片）
	public void updateUserByUidImage() {

		try {
			String curFilePath = ConstantsUtil.getFileName();
			String fileName = ConstantsUtil.getRandomFileName();
			UploadFile file = getFile("file0");
			String datePath = "";
			if (file.getFile().length() > 5 * 1024 * 1024) {
				renderJson("文件长度超过限制，必须小于5M");
			} else {
				// 上传文件
				String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀
				fileName = fileName + type; // 对文件重命名取得的文件名+后缀
				String dest = curFilePath + "/" + fileName;
				file.getFile().renameTo(new File(dest));

				datePath = ConstantsUtil.getDateFormat() + "/" + fileName;
			}
			int uid = getParaToInt("uid", 0);
			String name = getPara("name");
			String sex = getPara("sex");
			String tel = getPara("tel");
			String ptime = ConstantsUtil.getDateFormat4mysql();
			String password = getPara("password");
			String uurl = datePath;
			if (uid != 0) {
				RecordBean.UpdateUser(uid, name, sex, tel, ptime, password, uurl);
				renderJson("success");
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	// 获取患者信息
	public void getUserByUid() {
		int uid = getParaToInt("uid", 0);
		try {
			if (uid != 0) {
				renderJson(UserApp.dao.findById(uid));
			} else {
				renderJson("failed");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			renderJson(ex.toString());
		}
	}

	public void updateFocus() {
		int uid = getParaToInt("uid", 0); // 用户id
		int pid = getParaToInt("pid", 0);
		boolean isCreate = getParaToBoolean("isCreate", false);
		try {
			RecordBean.updateFocus(pid, uid, isCreate);
		} catch (Exception ex) {
			renderJson(ex.toString());
		}
		if (isCreate)
			renderJson("create");
		else
			renderJson("delete");

	}

	public void getVersionCode() {
		try {
			renderJson(Db.findFirst("SELECT * FROM VERSION GROUP BY ptime DESC"));
		} catch (Exception ex) {
			renderJson(ex.toString());
		}
	}
}
