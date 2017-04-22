package two.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilLogger {
	/** ��ǰ��Ŀ·�� */
	static String path = "D:";
	static File file;
	/**
	 * ��̬�飬��������͵��õķ���
	 */
	static {
		createLogRoot();
		createLogOfDay();
		createLogOfFile();
	}
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * ������־��Ŀ¼
	 * 
	 */
	public static String createLogRoot() {
		// ��Ŀ·��+�ָ���+��Ŀ¼����log��
		path += File.separator + "log";
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		return path;
	}

	/**
	 * ������־��Ŀ¼
	 * 
	 */
	public static void createLogOfDay() {
		// ��Ŀ¼·��+�ָ���+��Ŀ¼��������ʱ�����ڣ�
		path += File.separator + getYMD();
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
	}

	/**
	 * ������¼��־
	 * 
	 */
	public static void createLogOfFile() {
		// ��Ŀ¼��+�ָ���+��־����lis+��ǰʱ��+��׺��
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
	 * �������ʱ�����ڣ������գ������ַ������� ������¼��־����Ŀ¼ʱ��Ϊ��Ŀ¼����
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
	 * �������ʱ��ʱ�䣨ʱ���룩�����ַ������� ������¼��־ʱ���ļ�����
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
			raf.write(("error��" + message + sdf.format(new Date()) + "\r\n")
					.getBytes());
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
