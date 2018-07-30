package zibo.utils;

public class StringUtils {
	/**
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isEmpty(String str){
		if (null == str) {
			return true;
		}
		if ("".equals(str.trim())) {
			return true;
		}
		return false;
	}
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}

	/**
	 * 去掉字符串两端的字字符串
	 * @param str 原字符串
	 * @param trim 要trim的子字符串
	 * @return String
	 */
	public static String trim(String str, String trim) {
		if (str.startsWith(trim)) {
			str = str.substring(trim.length());
		}
		if (str.endsWith(trim)) {
			str = str.substring(0, str.indexOf(trim) + 1);
		}
		return str;
	}

}
