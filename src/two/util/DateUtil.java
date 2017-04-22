package two.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import weaver.general.TimeUtil;

/**
 * 日期操作工具类
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

	// 日期转化为大小写
	public static String dataToUpper(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		return numToUpper(year) + "年" + monthToUppder(month) + "月"
				+ dayToUppder(day) + "日";
	}

	// 将数字转化为大写
	public static String numToUpper(int num) {
		// String u[] = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
		String u[] = { "O", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		char[] str = String.valueOf(num).toCharArray();
		String rstr = "";
		for (int i = 0; i < str.length; i++) {
			rstr = rstr + u[Integer.parseInt(str[i] + "")];
		}
		return rstr;
	}

	// 月转化为大写
	public static String monthToUppder(int month) {
		if (month < 10) {
			return numToUpper(month);
		} else if (month == 10) {
			return "十";
		} else {
			return "十" + numToUpper(month - 10);
		}
	}

	// 日转化为大写
	public static String dayToUppder(int day) {
		if (day < 20) {
			return monthToUppder(day);
		} else {
			char[] str = String.valueOf(day).toCharArray();
			if (str[1] == '0') {
				return numToUpper(Integer.parseInt(str[0] + "")) + "十";
			} else {
				return numToUpper(Integer.parseInt(str[0] + "")) + "十"
						+ numToUpper(Integer.parseInt(str[1] + ""));
			}
		}
	}

	/**
	 * 字符转大写日期
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
	 * 字符日期转为"xxxx年xx月xx日"
	 * 
	 * @param date
	 * @return
	 */
	public static String stringToStringDate(String date) {
		String result = "";
		String[] split = date.split("-");
		if (split.length == 3) {
			result = split[0] + "年" + split[1] + "月" + split[2] + "日";
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(stringToStringDate("2011-10-31"));
	}
	/**
	 * 得到当前日期
	 * */
	public static String getNowDate(String str) {
		String redate = "";
		// 1 年月日，2 年月，3年，4月,5 年月日时分秒
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
	 * 得到星期数
	 * 
	 * @param date
	 *            日期，必须为"yyyy-MM-dd"格式,格式错误将返回"-1"
	 * @return星期数，如：星期一
	 */
	public static String getWeekDays(String date) {
		String result = "-1";
		int tem = TimeUtil.dateWeekday(date);
		switch (tem) {
		case 1:
			result = "星期一";
			break;
		case 2:
			result = "星期二";
			break;
		case 3:
			result = "星期三";
			break;
		case 4:
			result = "星期四";
			break;
		case 5:
			result = "星期五";
			break;
		case 6:
			result = "星期六";
			break;
		case 0:
			result = "星期日";
			break;
		default:
			result = "-1";
		}
		return result;
	}

}