// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffUtil.java

package weaver.hrm.report.schedulediff;

import java.math.BigDecimal;
import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.*;
import weaver.hrm.User;
import weaver.systeminfo.SystemEnv;

public class HrmScheduleDiffUtil extends BaseBean
{

	private BigDecimal workHoursPerDayDefault;
	private String onDutyTimeAMDefault;
	private String offDutyTimeAMDefault;
	private String onDutyTimePMDefault;
	private String offDutyTimePMDefault;
	private User user;

	public HrmScheduleDiffUtil()
	{
		workHoursPerDayDefault = new BigDecimal(7.1669999999999998D);
		onDutyTimeAMDefault = "08:30";
		offDutyTimeAMDefault = "11:40";
		onDutyTimePMDefault = "13:30";
		offDutyTimePMDefault = "17:30";
		user = null;
	}

	private BigDecimal addTotalWorkingHoursByTimeInterval(BigDecimal bigdecimal, long l)
	{
		BigDecimal bigdecimal1 = new BigDecimal(0.0D);
		try
		{
			bigdecimal1 = bigdecimal.add((new BigDecimal(l)).divide(new BigDecimal(3600), 4, 4));
			return bigdecimal1;
		}
		catch (Exception e)
		{
			return bigdecimal1;
		}
	}

	public String getBillSelectName(int i, String s, int j)
	{
		String s1 = "";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(" select selectName ").append("   from workflow_billfield a,workflow_SelectItem b ").append("  where a.id=b.fieldId ").append("    and a.billId=").append(i).append("    and a.fieldName='").append(s).append("' ").append("    and b.selectValue=").append(j);
		RecordSet recordset = new RecordSet();
		recordset.executeSql(stringbuffer.toString());
		if (recordset.next())
			s1 = Util.null2String(recordset.getString("selectName"));
		return s1;
	}

	public boolean getIsWorkday(String s)
	{
		boolean flag = true;
		try
		{
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append(" select 1 ").append("   from HrmPubHoliday ").append("  where holidayDate='").append(s).append("' ").append("    and changeType=1 ");
			if (user != null && user.getCountryid() != null && !user.getCountryid().equals(""))
				stringbuffer.append(" and countryId=").append(user.getCountryid());
			RecordSet recordset = new RecordSet();
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				flag = false;
				return flag;
			}
			stringbuffer = new StringBuffer();
			stringbuffer.append(" select 1 ").append("   from HrmPubHoliday ").append("  where holidayDate='").append(s).append("' ").append("    and changeType=3 ");
			if (user != null && user.getCountryid() != null && !user.getCountryid().equals(""))
				stringbuffer.append(" and countryId=").append(user.getCountryid());
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				flag = false;
				return flag;
			}
			stringbuffer = new StringBuffer();
			stringbuffer.append(" select 1 ").append("   from HrmPubHoliday ").append("  where holidayDate='").append(s).append("' ").append("    and changeType=2 ");
			if (user != null && user.getCountryid() != null && !user.getCountryid().equals(""))
				stringbuffer.append(" and countryId=").append(user.getCountryid());
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				flag = true;
				return flag;
			}
			int i = TimeUtil.dateWeekday(s);
			String s1 = "";
			String s2 = "";
			stringbuffer = new StringBuffer();
			stringbuffer.append(" select ");
			if (i == 1)
				s1 = "monstarttime1";
			else
			if (i == 2)
				s1 = "tuestarttime1";
			else
			if (i == 3)
				s1 = "wedstarttime1";
			else
			if (i == 4)
				s1 = "thustarttime1";
			else
			if (i == 5)
				s1 = "fristarttime1";
			else
			if (i == 6)
				s1 = "satstarttime1";
			else
			if (i == 0)
				s1 = "sunstarttime1";
			int j = 0;
			if (user != null)
				j = Util.getIntValue(String.valueOf(user.getUserSubCompany1()), 0);
			stringbuffer.append(s1 + " from HrmSchedule where scheduletype = '4' and relatedid = " + j);
			if ("oracle".equals(recordset.getDBType()) || "db2".equals(recordset.getDBType()))
				stringbuffer.append(" and to_date(validedatefrom,'yyyy-mm-dd') <= to_date('" + s + "','yyyy-mm-dd') and to_date(validedateto,'yyyy-mm-dd') >= to_date('" + s + "','yyyy-mm-dd') ");
			else
				stringbuffer.append(" and validedatefrom <= '" + s + "' and validedateto >= '" + s + "' ");
			recordset.executeSql(stringbuffer.toString());
			if (recordset.getCounts() == 0)
			{
				StringBuffer stringbuffer1 = new StringBuffer();
				stringbuffer1.append(" select " + s1);
				stringbuffer1.append(" from HrmSchedule where scheduletype = '3' and relatedid = 0 ");
				if ("oracle".equals(recordset.getDBType()) || "db2".equals(recordset.getDBType()))
					stringbuffer1.append(" and to_date(validedatefrom,'yyyy-mm-dd') <= to_date('" + s + "','yyyy-mm-dd') and to_date(validedateto,'yyyy-mm-dd') >= to_date('" + s + "','yyyy-mm-dd') ");
				else
					stringbuffer1.append(" and validedatefrom <= '" + s + "' and validedateto >= '" + s + "' ");
				recordset.executeSql(stringbuffer1.toString());
				recordset.next();
				String s3 = recordset.getString(1).trim();
				if ("".equals(s3))
				{
					flag = false;
					return flag;
				}
			} else
			{
				recordset.next();
				String s4 = recordset.getString(1).trim();
				if ("".equals(s4))
				{
					flag = false;
					return flag;
				}
			}
			return flag;
		}
		catch (Exception e)
		{
			return flag;
		}
	}

	public Map getOnDutyAndOffDutyTimeMap(String s, int i)
	{
		HashMap hashmap = new HashMap();
		try
		{
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";
			StringBuffer stringbuffer = new StringBuffer();
			int j = TimeUtil.dateWeekday(s);
			RecordSet recordset = new RecordSet();
			stringbuffer.append(" select relateWeekDay ").append("   from HrmPubHoliday ").append("  where holidayDate='").append(s).append("' ").append("    and changeType=2 ");
			if (user != null && user.getCountryid() != null && !user.getCountryid().equals(""))
				stringbuffer.append(" and countryId=").append(user.getCountryid());
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
				j = Util.getIntValue(recordset.getString("relateWeekDay"), 1) - 1;
			stringbuffer = new StringBuffer();
			if (j == 0)
				stringbuffer.append(" select sunStartTime1 as onDutyTimeAM,sunEndTime1 as offDutyTimeAM,sunStartTime2 as onDutyTimePM ,sunEndTime2 as offDutyTimePM ");
			else
			if (j == 1)
				stringbuffer.append(" select monStartTime1 as onDutyTimeAM,monEndTime1 as offDutyTimeAM,monStartTime2 as onDutyTimePM ,monEndTime2 as offDutyTimePM ");
			else
			if (j == 2)
				stringbuffer.append(" select tueStartTime1 as onDutyTimeAM,tueEndTime1 as offDutyTimeAM,tueStartTime2 as onDutyTimePM ,tueEndTime2 as offDutyTimePM ");
			else
			if (j == 3)
				stringbuffer.append(" select wedStartTime1 as onDutyTimeAM,wedEndTime1 as offDutyTimeAM,wedStartTime2 as onDutyTimePM ,wedEndTime2 as offDutyTimePM ");
			else
			if (j == 4)
				stringbuffer.append(" select thuStartTime1 as onDutyTimeAM,thuEndTime1 as offDutyTimeAM,thuStartTime2 as onDutyTimePM ,thuEndTime2 as offDutyTimePM ");
			else
			if (j == 5)
				stringbuffer.append(" select friStartTime1 as onDutyTimeAM,friEndTime1 as offDutyTimeAM,friStartTime2 as onDutyTimePM ,friEndTime2 as offDutyTimePM ");
			else
				stringbuffer.append(" select satStartTime1 as onDutyTimeAM,satEndTime1 as offDutyTimeAM,satStartTime2 as onDutyTimePM ,satEndTime2 as offDutyTimePM ");
			stringbuffer.append("  from HrmSchedule ").append(" where valideDateFrom<='").append(s).append("'").append("   and valideDateTo>='").append(s).append("'").append("   and (scheduleType='3' ").append("        or (scheduleType='4' and relatedId=").append(i).append(")").append("        ) ").append(" order by scheduleType desc,relatedId asc,id desc ");
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				s1 = Util.null2String(recordset.getString("onDutyTimeAM"));
				s2 = Util.null2String(recordset.getString("offDutyTimeAM"));
				s3 = Util.null2String(recordset.getString("onDutyTimePM"));
				s4 = Util.null2String(recordset.getString("offDutyTimePM"));
			}
			if (s1 == null || s1.equals(""))
				s1 = onDutyTimeAMDefault;
			if (s2 == null || s2.equals(""))
				s2 = offDutyTimeAMDefault;
			if (s3 == null || s3.equals(""))
				s3 = onDutyTimePMDefault;
			if (s4 == null || s4.equals(""))
				s4 = offDutyTimePMDefault;
			hashmap.put("onDutyTimeAM", s1);
			hashmap.put("offDutyTimeAM", s2);
			hashmap.put("onDutyTimePM", s3);
			hashmap.put("offDutyTimePM", s4);
			return hashmap;
		}
		catch (Exception e)
		{
			return hashmap;
		}
	}

	public String getStatusNameOfHrmResource(String s)
	{
		String s1 = "";
		if (s == null || s.trim().equals(""))
			return s1;
		int i = 7;
		if (user != null)
			i = user.getLanguage();
		if (s.equals("0"))
			s1 = SystemEnv.getHtmlLabelName(15710, i);
		else
		if (s.equals("1"))
			s1 = SystemEnv.getHtmlLabelName(15711, i);
		else
		if (s.equals("2"))
			s1 = SystemEnv.getHtmlLabelName(480, i);
		else
		if (s.equals("3"))
			s1 = SystemEnv.getHtmlLabelName(15844, i);
		else
		if (s.equals("4"))
			s1 = SystemEnv.getHtmlLabelName(6094, i);
		else
		if (s.equals("5"))
			s1 = SystemEnv.getHtmlLabelName(6091, i);
		else
		if (s.equals("6"))
			s1 = SystemEnv.getHtmlLabelName(6092, i);
		else
		if (s.equals("7"))
			s1 = SystemEnv.getHtmlLabelName(2245, i);
		else
		if (s.equals("10"))
			s1 = SystemEnv.getHtmlLabelName(1831, i);
		return s1;
	}

	public String getTotalWorkingDays(String s, String s1, String s2, String s3, int i)
	{
		String s4 = "";
		if (s == null || s.trim().equals("") || s2 == null || s2.trim().equals("") || s.compareTo(s2) > 0 || s.equals(s2) && s1.compareTo(s3) > 0)
			return s4;
		if (s1 == null || s1.trim().equals(""))
		{
			Map map = getOnDutyAndOffDutyTimeMap(s, i);
			String s5 = Util.null2String((String)map.get("onDutyTimeAM"));
			s1 = s5;
		}
		if (s3 == null || s3.trim().equals(""))
		{
			Map map1 = getOnDutyAndOffDutyTimeMap(s2, i);
			String s6 = Util.null2String((String)map1.get("offDutyTimePM"));
			s3 = s6;
		}
		if (s1 == null || s1.trim().equals("") || s3 == null || s3.trim().equals(""))
			return s4;
		BigDecimal bigdecimal = new BigDecimal(0.0D);
		BigDecimal bigdecimal2 = new BigDecimal(0.0D);
		try
		{
			String s7 = "";
			String s8 = "";
			boolean flag = false;
			boolean flag1 = true;
			Object obj = null;
			String s11 = "";
			String s13 = "";
			String s15 = "";
			String s17 = "";
			long l = 0L;
			s7 = s;
			while (!flag) 
			{
				BigDecimal bigdecimal1 = new BigDecimal(0.0D);
				if (s7.equals(s2))
					flag = true;
				boolean flag2 = getIsWorkday(s7);
				if (!flag2)
				{
					String s9 = TimeUtil.dateAdd(s7, 1);
					s7 = s9;
				} else
				{
					Map map2 = getOnDutyAndOffDutyTimeMap(s7, i);
					String s12 = Util.null2String((String)map2.get("onDutyTimeAM"));
					String s14 = Util.null2String((String)map2.get("offDutyTimeAM"));
					String s16 = Util.null2String((String)map2.get("onDutyTimePM"));
					String s18 = Util.null2String((String)map2.get("offDutyTimePM"));
					if (s7.equals(s) && s7.equals(s2))
					{
						if (s1.compareTo(s12) < 0)
						{
							if (s3.compareTo(s12) >= 0)
								if (s3.compareTo(s12) >= 0 && s3.compareTo(s14) <= 0)
								{
									long l1 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s3 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l1);
								} else
								if (s3.compareTo(s14) > 0 && s3.compareTo(s16) < 0)
								{
									long l2 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l2);
								} else
								if (s3.compareTo(s16) >= 0 && s3.compareTo(s18) <= 0)
								{
									long l3 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s3 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l3);
								} else
								{
									long l4 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l4);
								}
						} else
						if (s1.compareTo(s12) >= 0 && s1.compareTo(s14) <= 0)
						{
							if (s3.compareTo(s12) >= 0 && s3.compareTo(s14) <= 0)
							{
								long l5 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s3 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l5);
							} else
							if (s3.compareTo(s14) > 0 && s3.compareTo(s16) < 0)
							{
								long l6 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s14 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l6);
							} else
							if (s3.compareTo(s16) >= 0 && s3.compareTo(s18) <= 0)
							{
								long l7 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s3 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l7);
							} else
							{
								long l8 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l8);
							}
						} else
						if (s1.compareTo(s14) > 0 && s1.compareTo(s16) < 0)
						{
							if (s3.compareTo(s14) <= 0 || s3.compareTo(s16) >= 0)
								if (s3.compareTo(s16) >= 0 && s3.compareTo(s18) <= 0)
								{
									long l9 = TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s3 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l9);
								} else
								{
									long l10 = TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
									bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l10);
								}
						} else
						if (s1.compareTo(s16) >= 0 && s1.compareTo(s18) <= 0)
							if (s3.compareTo(s16) >= 0 && s3.compareTo(s18) <= 0)
							{
								long l11 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s3 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l11);
							} else
							{
								long l12 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s18 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l12);
							}
					} else
					if (s7.equals(s) && !s7.equals(s2))
					{
						if (s1.compareTo(s12) < 0)
						{
							long l13 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
							bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l13);
						} else
						if (s1.compareTo(s12) >= 0 && s1.compareTo(s14) <= 0)
						{
							long l14 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
							bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l14);
						} else
						if (s1.compareTo(s14) > 0 && s1.compareTo(s16) < 0)
						{
							long l15 = TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
							bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l15);
						} else
						if (s1.compareTo(s16) >= 0 && s1.compareTo(s18) <= 0)
						{
							long l16 = TimeUtil.timeInterval(s7 + " " + s1 + ":00", s7 + " " + s18 + ":00");
							bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l16);
						}
					} else
					if (!s7.equals(s) && s7.equals(s2))
					{
						if (s3.compareTo(s12) >= 0)
							if (s3.compareTo(s12) >= 0 && s3.compareTo(s14) <= 0)
							{
								long l17 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s3 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l17);
							} else
							if (s3.compareTo(s14) > 0 && s3.compareTo(s16) < 0)
							{
								long l18 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l18);
							} else
							if (s3.compareTo(s16) >= 0 && s3.compareTo(s18) <= 0)
							{
								long l19 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s3 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l19);
							} else
							{
								long l20 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
								bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l20);
							}
					} else
					if (!s7.equals(s) && !s7.equals(s2))
					{
						long l21 = TimeUtil.timeInterval(s7 + " " + s12 + ":00", s7 + " " + s14 + ":00") + TimeUtil.timeInterval(s7 + " " + s16 + ":00", s7 + " " + s18 + ":00");
						bigdecimal1 = addTotalWorkingHoursByTimeInterval(bigdecimal1, l21);
					}
					BigDecimal bigdecimal3 = getWorkHoursPerDay(map2);
					bigdecimal2 = bigdecimal2.add(bigdecimal1.divide(bigdecimal3, 2, 4));
					String s10 = TimeUtil.dateAdd(s7, 1);
					s7 = s10;
				}
			}
			s4 = bigdecimal2.toString();
			return s4;
		}
		catch (Exception e)
		{
			return s4;
		}
	}

	private BigDecimal getWorkHoursPerDay()
	{
		BigDecimal bigdecimal = workHoursPerDayDefault;
		try
		{
			String s = "";
			String s1 = "";
			String s2 = "";
			String s3 = "";
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append(" select wedStartTime1 as onDutyTimeAM,wedEndTime1 as offDutyTimeAM,wedStartTime2 as onDutyTimePM ,wedEndTime2 as offDutyTimePM ").append("  from HrmSchedule ").append(" where  scheduleType='3' ").append(" order by relatedId asc,id desc ");
			RecordSet recordset = new RecordSet();
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				s = Util.null2String(recordset.getString("onDutyTimeAM"));
				s1 = Util.null2String(recordset.getString("offDutyTimeAM"));
				s2 = Util.null2String(recordset.getString("onDutyTimePM"));
				s3 = Util.null2String(recordset.getString("offDutyTimePM"));
			}
			if (s == null || s.equals(""))
				s = onDutyTimeAMDefault;
			if (s1 == null || s1.equals(""))
				s1 = offDutyTimeAMDefault;
			if (s2 == null || s2.equals(""))
				s2 = onDutyTimePMDefault;
			if (s3 == null || s3.equals(""))
				s3 = offDutyTimePMDefault;
			String s4 = Util.addTime(Util.subTime(s1, s), Util.subTime(s3, s2));
			ArrayList arraylist = Util.TokenizerString(s4, ":");
			int i = Util.getIntValue((String)arraylist.get(0), 0);
			int j = Util.getIntValue((String)arraylist.get(1), 1);
			double d = (double)j / 60D + (double)i;
			bigdecimal = new BigDecimal(d);
			return bigdecimal;
		}
		catch (Exception e)
		{
			return bigdecimal;
		}
	}

	private BigDecimal getWorkHoursPerDay(String s, int i)
	{
		BigDecimal bigdecimal = workHoursPerDayDefault;
		try
		{
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append(" select wedStartTime1 as onDutyTimeAM,wedEndTime1 as offDutyTimeAM,wedStartTime2 as onDutyTimePM ,wedEndTime2 as offDutyTimePM ").append("  from HrmSchedule ").append(" where valideDateFrom<='").append(s).append("'").append("   and valideDateTo>='").append(s).append("'").append(" where  (scheduleType='3' ").append("        or (scheduleType='4' and relatedId=").append(i).append(")").append("        ) ").append(" order by scheduleType desc,relatedId asc,id desc ");
			RecordSet recordset = new RecordSet();
			recordset.executeSql(stringbuffer.toString());
			if (recordset.next())
			{
				s1 = Util.null2String(recordset.getString("onDutyTimeAM"));
				s2 = Util.null2String(recordset.getString("offDutyTimeAM"));
				s3 = Util.null2String(recordset.getString("onDutyTimePM"));
				s4 = Util.null2String(recordset.getString("offDutyTimePM"));
			}
			if (s1 == null || s1.equals(""))
				s1 = onDutyTimeAMDefault;
			if (s2 == null || s2.equals(""))
				s2 = offDutyTimeAMDefault;
			if (s3 == null || s3.equals(""))
				s3 = onDutyTimePMDefault;
			if (s4 == null || s4.equals(""))
				s4 = offDutyTimePMDefault;
			String s5 = Util.addTime(Util.subTime(s2, s1), Util.subTime(s4, s3));
			ArrayList arraylist = Util.TokenizerString(s5, ":");
			int j = Util.getIntValue((String)arraylist.get(0), 0);
			int k = Util.getIntValue((String)arraylist.get(1), 1);
			double d = (double)k / 60D + (double)j;
			bigdecimal = new BigDecimal(d);
			return bigdecimal;
		}
		catch (Exception e)
		{
			return bigdecimal;
		}
	}

	private BigDecimal getWorkHoursPerDay(Map map)
	{
		BigDecimal bigdecimal = workHoursPerDayDefault;
		try
		{
			String s = Util.null2String((String)map.get("onDutyTimeAM"));
			String s1 = Util.null2String((String)map.get("offDutyTimeAM"));
			String s2 = Util.null2String((String)map.get("onDutyTimePM"));
			String s3 = Util.null2String((String)map.get("offDutyTimePM"));
			if (s == null || s.equals(""))
				s = onDutyTimeAMDefault;
			if (s1 == null || s1.equals(""))
				s1 = offDutyTimeAMDefault;
			if (s2 == null || s2.equals(""))
				s2 = onDutyTimePMDefault;
			if (s3 == null || s3.equals(""))
				s3 = offDutyTimePMDefault;
			String s4 = Util.addTime(Util.subTime(s1, s), Util.subTime(s3, s2));
			ArrayList arraylist = Util.TokenizerString(s4, ":");
			int i = Util.getIntValue((String)arraylist.get(0), 0);
			int j = Util.getIntValue((String)arraylist.get(1), 1);
			double d = (double)j / 60D + (double)i;
			bigdecimal = new BigDecimal(d);
			return bigdecimal;
		}
		catch (Exception e)
		{
			return bigdecimal;
		}
	}

	public void setUser(User user1)
	{
		user = user1;
	}
}
