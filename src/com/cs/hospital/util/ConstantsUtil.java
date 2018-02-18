package com.cs.hospital.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ConstantsUtil {
	
	public static String filePath = "D:/software/apache-tomcat-7.0.84-windows-x64/apache-tomcat-7.0.84/webapps/upload/";
	
	public static String getDateFormat() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		return df.format(new Date());
	}

	public static String getFileName() {
		String path = ConstantsUtil.filePath + getDateFormat();
		File file = new File(path);

		if (!file.exists()) {
			System.out.println("文件路径不存在：" + filePath);
			// 如果不存在就创建文件
			file.mkdir();
			System.out.println("创建文件路径成功");
		}
		return path;
	}

	public static String getRandomFileName() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");

		String str = simpleDateFormat.format(new Date());

		Random random = new Random();

		int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

		return rannum + str;// 当前时间
	}

}
