package two.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import weaver.general.TimeUtil;

/**
 * ���ڲ���������
 * 
 * @author xhy
 * 
 */
/**
 * @author hp
 * 
 */
public class DateUtil {
	public static final String FORMAT_STRING = "yyyy-MM-dd";
	private static SimpleDateFormat format = new SimpleDateFormat(FORMAT_STRING);

	// ����ת��Ϊ��Сд
	public static String dataToUpper(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		return numToUpper(year) + "��" + monthToUppder(month) + "��"
				+ dayToUppder(day) + "��";
	}

	// ������ת��Ϊ��д
	public static String numToUpper(int num) {
		// String u[] = {"��","Ҽ","��","��","��","��","½","��","��","��"};
		String u[] = { "O", "һ", "��", "��", "��", "��", "��", "��", "��", "��" };
		char[] str = String.valueOf(num).toCharArray();
		String rstr = "";
		for (int i = 0; i < str.length; i++) {
			rstr = rstr + u[Integer.parseInt(str[i] + "")];
		}
		return rstr;
	}

	// ��ת��Ϊ��д
	public static String monthToUppder(int month) {
		if (month < 10) {
			return numToUpper(month);
		} else if (month == 10) {
			return "ʮ";
		} else {
			return "ʮ" + numToUpper(month - 10);
		}
	}

	// ��ת��Ϊ��д
	public static String dayToUppder(int day) {
		if (day < 20) {
			return monthToUppder(day);
		} else {
			char[] str = String.valueOf(day).toCharArray();
			if (str[1] == '0') {
				return numToUpper(Integer.parseInt(str[0] + "")) + "ʮ";
			} else {
				return numToUpper(Integer.parseInt(str[0] + "")) + "ʮ"
						+ numToUpper(Integer.parseInt(str[1] + ""));
			}
		}
	}

	/**
	 * �ַ�ת��д����
	 * 
	 * @param date
	 * @return
	 */
	public static String stringToUppderDate(String date) {
		try {
			return dataToUpper(format.parse(date));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * �ַ�����תΪ"xxxx��xx��xx��"
	 * 
	 * @param date
	 * @return
	 */
	public static String stringToStringDate(String date) {
		String result = "";
		String[] split = date.split("-");
		if (split.length == 3) {
			result = split[0] + "��" + split[1] + "��" + split[2] + "��";
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(stringToStringDate("2011-10-31"));
	}
	/**
	 * �õ���ǰ����
	 * */
	public static String getNowDate(String str) {
		String redate = "";
		// 1 �����գ�2 ���£�3�꣬4��,5 ������ʱ����
		Date date = new Date();
		DateFormat df=null;
		if ("1".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM-dd");
			redate = df.format(date);
		}else if ("2".equals(str)) {
			df = new SimpleDateFormat("yyyy");
			redate = df.format(date);
		} else if ("3".equals(str)) {
			df = new SimpleDateFormat("MM");
			redate = df.format(date);
		}  else if ("4".equals(str)) {
			df = new SimpleDateFormat("dd");
			redate = df.format(date);
		} else if ("5".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM-dd 24hh:mm:ss");
			redate = df.format(date);
		} else if ("6".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM");
			redate = df.format(date);
		} 
		return redate;
	}
	/**
	 * �õ�������
	 * 
	 * @param date
	 *            ���ڣ�����Ϊ"yyyy-MM-dd"��ʽ,��ʽ���󽫷���"-1"
	 * @return���������磺����һ
	 */
	public static String getWeekDays(String date) {
		String result = "-1";
		int tem = TimeUtil.dateWeekday(date);
		switch (tem) {
		case 1:
			result = "����һ";
			break;
		case 2:
			result = "���ڶ�";
			break;
		case 3:
			result = "������";
			break;
		case 4:
			result = "������";
			break;
		case 5:
			result = "������";
			break;
		case 6:
			result = "������";
			break;
		case 0:
			result = "������";
			break;
		default:
			result = "-1";
		}
		return result;
	}

}