package com.cs.hospital.util;

public class StringUtil {

	public static boolean StringIsNullOrEmpty(String str) {

		if (str == null || str.equals(""))
			return true;
		return false;
	}
}
