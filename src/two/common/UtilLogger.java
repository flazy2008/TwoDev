package two.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilLogger {
	/** 当前项目路径 */
	static String path = "D:";
	static File file;
	/**
	 * 静态块，创建对象就调用的方法
	 */
	static {
		createLogRoot();
		createLogOfDay();
		createLogOfFile();
	}
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 创建日志根目录
	 * 
	 */
	public static String createLogRoot() {
		// 项目路径+分隔符+根目录名（log）
		path += File.separator + "log";
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		return path;
	}

	/**
	 * 创建日志子目录
	 * 
	 */
	public static void createLogOfDay() {
		// 根目录路径+分隔符+子目录名（运行时的日期）
		path += File.separator + getYMD();
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
	}

	/**
	 * 创建记录日志
	 * 
	 */
	public static void createLogOfFile() {
		// 子目录名+分隔符+日志名（lis+当前时间+后缀）
		path += File.separator + "nd" + getHMS() + ".log";
		file = new File(path);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 获得运行时的日期（年月日），以字符串返回 创建记录日志的子目录时作为子目录名称
	 * 
	 * @return year + month + day
	 */
	public static String getYMD() {
		Calendar today = Calendar.getInstance();
		String year = Integer.toString(today.get(Calendar.YEAR));
		String month = Integer.toString(today.get(Calendar.MONTH) + 1);
		String day = Integer.toString(today.get(Calendar.DAY_OF_MONTH));

		month = (month.length() == 1) ? "0" + month : month;
		day = (day.length() == 1) ? "0" + day : day;
		return year + month + day;
	}

	/**
	 * 获得运行时的时间（时分秒），以字符串返回 创建记录日志时作文件名用
	 * 
	 * @return hour + minute + second
	 */
	public static String getHMS() {
		Calendar today = Calendar.getInstance();
		String hour = Integer.toString(today.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(today.get(Calendar.MINUTE));
		String second = Integer.toString(today.get(Calendar.SECOND));
		hour = (hour.length() == 1) ? "0" + hour : hour;
		minute = (minute.length() == 1) ? "0" + minute : minute;
		second = (second.length() == 1) ? "0" + second : second;
		return hour + minute + second;
	}

	public static void info(String message) {

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(raf.length());
			raf.write(("" + message + "\t" + sdf.format(new Date()) + "\r\n")
					.getBytes());
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void error(String message) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(raf.length());
			raf.write(("error：" + message + sdf.format(new Date()) + "\r\n")
					.getBytes());
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
