package zibo.dataMonitor;

import zibo.utils.StringUtils;

import java.util.*;

/**
 * <配置文件类>
 * @author BONC
 *
 */
public class PropertiesInfo {
	private static final String PATH ="config/path";
	private static Map<String,String> map;
	static{
		map = new HashMap<>();
		String path = getProperties(PATH).get("path");
		map.putAll(getProperties(path));
	}
	/**
	 * <获取指定配置文件中的信息>
	 * @param filePath 配置文件路径
	 * @return Map
	 */
	private static Map<String,String> getProperties(String filePath){
		ResourceBundle resourceBundle = ResourceBundle.getBundle(filePath);
		Set<?> set = resourceBundle.keySet();
		Iterator<?> i = set.iterator();
		Map<String,String> map = new HashMap<>();
		while (i.hasNext()) {
			String key = i.next().toString();
			String value = resourceBundle.getString(key);
			map.put(key, value);
		}
		return map;
	}
	/**
	 * <获取指定配置信息>
	 * @param key 键
	 * @return value
	 */
	public static String get(String key){
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException("key为空，获取value异常");
		}
		return map.get(key);
	}
	/**
	 * <获取整数>
	 * @param key 键
	 * @return value
	 */
	public static Integer getInt(String key){
		String value = get(key);
		if (StringUtils.isEmpty(value)) {
			throw new RuntimeException("获取配置文件中整型数据的原始数据为空");
		}
		return Integer.valueOf(value);
	}
	public  static String[] getList(String key){
		String value = get(key);
		if (StringUtils.isEmpty(value)) {
			throw new RuntimeException("获取配置文件中整型数据的原始数据为空");
		}
		return value.split(",");
	}
	/**
	 * <获取相同前缀的配置信息>
	 * @param keyPre  键前缀
	 * @return List
	 */
	public static List<String> getOfPre(String keyPre){
		List<String> list = new ArrayList<>();
		Set<String> set = map.keySet();
		Iterator<String> i = set.iterator();
		while (i.hasNext()) {
			String key = i.next();
			if (key.startsWith(keyPre)) {
				list.add(map.get(key));
			}
		}
		return list;
	}
}
