package com.cs.hospital.Bean;

import java.sql.SQLException;
import java.util.List;

import com.cs.hospital.model.Image;
import com.cs.hospital.model.Patient;
import com.cs.hospital.model.RecordDay;
import com.cs.hospital.util.ConstantsUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class RecordBean {

	public static boolean CreatRecord(List<String> pathList, int uid, int pid, String title, String content,
			String ftime) {

		return Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				RecordDay recordDay = new RecordDay().set("uid", uid).set("pid", pid).set("title", title)
						.set("content", content).set("ftime", ftime);
				boolean count1 = recordDay.save();
				int rid = recordDay.get("rid");
				if (pathList != null && pathList.size() > 0) {
					for (String path : pathList) {
						Image image = new Image().set("rid", rid).set("iurl", path).set("utime", ftime);
						if (image.save()) {
							continue;
						} else {
							return false;
						}
					}
					return count1;
				}

				return count1;
			}
		});
	}

	public static boolean UpdateRecord(List<String> pathList, int uid, int pid, String title, String content,
			String utime, int rid) {
		return Db.tx(new IAtom() {
			public boolean run() throws SQLException {

				int count1 = Db.update("update recordday set uid = ?, pid = ?, title = ?, content = ? where rid = ?",
						uid, pid, title, content, rid);
						// Record record = Db.findById("recordday",
						// rid).set("uid", uid).set("pid", pid).set("title",
						// title)
						// .set("content", content).set("utime", utime);

				// boolean count1 = Db.update("recordday", record);
				int count2 = Db.delete("DELETE  FROM image WHERE rid = ?", rid);
				if (pathList != null && pathList.size() > 0) {
					for (String path : pathList) {
						Image image = new Image().set("rid", rid).set("iurl", path).set("utime", utime);
						if (image.save()) {
							continue;
						} else {
							return false;
						}
					}
					return (count1 == 1) && (count2 > -1);
				}

				return (count1 == 1) && (count2 > -1);
			}
		});
	}

	public static boolean CreatPatient(int pno, int uid, String name, String sex, String birthday, String tel,
			String ptime, String pcondition, String purl) {

		return new Patient().set("pno", pno).set("uid", uid).set("name", name).set("sex", sex).set("birthday", birthday)
				.set("tel", tel).set("ptime", ptime).set("pcondition", pcondition).set("purl", purl).save();

	}

	public static boolean UpdatePatient(int pid, int pno, int uid, String name, String sex, String birthday, String tel,
			String ptime, String pcondition, String purl) {

		return Db.update(
				"update recordday set pno = ?, uid = ?, name = ?, sex = ?, birthday = ?, tel = ?, ptime = ?, pcondition = ?, purl = ? where pid = ?",
				pno, uid, name, sex, birthday, tel, ptime, pcondition, purl, pid) == 1;

	}

}
