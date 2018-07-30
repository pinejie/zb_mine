package zibo.utils;

import java.io.*;

/**
 * <IO常用方法>
 * @author BONC
 *
 */
public  class IOUtil {
	/**
	 * <获取缓冲输入流>
	 * @param fileName 文件路径
	 */
	public static BufferedReader getBufferedReader(String fileName,String decode) throws IOException{
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName),decode));
	}
	
	/**
	 * <获取GB2312缓冲输入流>
	 * @param fileName 文件路径
	 */
	public static BufferedReader getBufferedReaderOfGB2312(String fileName) throws IOException{
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"gb2312"));
	}
	/**
	 * <获取utf8缓冲输入流>
	 * @param fileName 文件路径
	 */
	public static BufferedReader getBufferedReaderOfUTF8(String fileName) throws IOException{
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"utf-8"));
	}
	/**
	 * <关闭缓冲输入流>
	 * @param reader 输入流
	 */
	public static void closeReader(Reader reader) throws IOException{
		if(null != reader){
			reader.close();
		}
	}
	/**
	 * <获取缓冲输出流>
	 * @param filePath 文件路径
	 */
	public static BufferedOutputStream getBufferedOutputStream(String filePath) throws IOException{
		return new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	}
	/**
	 * <关闭缓冲输出流>
	 * @param writer 输出流
	 */
	public static void closeWriter(OutputStream writer) throws IOException{
		if(null != writer){
			writer.close();
		}
	}
}
