// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffDetAbsentFromWorkManager.java

package weaver.hrm.report.schedulediff;

import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.*;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;

// Referenced classes of package weaver.hrm.report.schedulediff:
//			HrmScheduleDiffListComparator, HrmScheduleDiffUtil

public class HrmScheduleDiffDetAbsentFromWorkManager extends BaseBean
{

	private User user;
	private boolean sortForResult;

	public HrmScheduleDiffDetAbsentFromWorkManager()
	{
		user = null;
		sortForResult = true;
	}

	private boolean getHasApproved(int i, String s, String s1, String s2, int j)
	{
		boolean flag = false;
		RecordSet recordset = new RecordSet();
		double d = 0.0D;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(" select b.fromDate,b.fromTime,b.toDate,b.toTime ").append("   from Workflow_Requestbase a,Bill_BoHaiLeave b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s).append(" ").append(s2).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s1).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s).append(" ").append(s2).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s1).append("' ");
		recordset.executeSql(stringbuffer.toString());
		while (recordset.next()) 
		{
			String s3 = Util.null2String(recordset.getString("fromDate"));
			String s6 = Util.null2String(recordset.getString("toDate"));
			String s9 = Util.null2String(recordset.getString("fromTime"));
			String s12 = Util.null2String(recordset.getString("toTime"));
			if (s3.compareTo(s) < 0)
			{
				s9 = s1;
				if (s6.compareTo(s) > 0)
					return true;
				int k = s12.compareTo(s2);
				if (k >= 0)
					return true;
			} else
			if (s6.compareTo(s) > 0)
			{
				s12 = s2;
				int l = s9.compareTo(s1);
				if (l <= 0)
					return true;
			} else
			{
				int i1 = s9.compareTo(s1);
				int l2 = s12.compareTo(s2);
				if (i1 <= 0 && l2 >= 0)
					return true;
			}
			if (s9.compareTo(s12) < 0)
				d += Util.getDoubleValue(hrmschedulediffutil.getTotalWorkingDays(s, s9, s, s12, j), 0.0D);
		}
		stringbuffer = new StringBuffer();
		stringbuffer.append(" select b.fromDate,b.fromTime,b.toDate,b.toTime ").append("   from Workflow_Requestbase a,Bill_BoHaiEvection b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s).append(" ").append(s2).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s1).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s).append(" ").append(s2).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s1).append("' ");
		recordset.executeSql(stringbuffer.toString());
		while (recordset.next()) 
		{
			String s4 = Util.null2String(recordset.getString("fromDate"));
			String s7 = Util.null2String(recordset.getString("toDate"));
			String s10 = Util.null2String(recordset.getString("fromTime"));
			String s13 = Util.null2String(recordset.getString("toTime"));
			if (s4.compareTo(s) < 0)
			{
				s10 = s1;
				if (s7.compareTo(s) > 0)
					return true;
				int j1 = s13.compareTo(s2);
				if (j1 >= 0)
					return true;
			} else
			if (s7.compareTo(s) > 0)
			{
				s13 = s2;
				int k1 = s10.compareTo(s1);
				if (k1 <= 0)
					return true;
			} else
			{
				int l1 = s10.compareTo(s1);
				int i3 = s13.compareTo(s2);
				if (l1 <= 0 && i3 >= 0)
					return true;
			}
			if (s10.compareTo(s13) < 0)
				d += Util.getDoubleValue(hrmschedulediffutil.getTotalWorkingDays(s, s10, s, s13, j), 0.0D);
		}
		stringbuffer = new StringBuffer();
		stringbuffer.append(" select b.fromDate,b.fromTime,b.toDate,b.toTime ").append("   from Workflow_Requestbase a,Bill_BoHaiOut b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s).append(" ").append(s2).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s1).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s).append(" ").append(s2).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s1).append("' ");
		recordset.executeSql(stringbuffer.toString());
		while (recordset.next()) 
		{
			String s5 = Util.null2String(recordset.getString("fromDate"));
			String s8 = Util.null2String(recordset.getString("toDate"));
			String s11 = Util.null2String(recordset.getString("fromTime"));
			String s14 = Util.null2String(recordset.getString("toTime"));
			if (s5.compareTo(s) < 0)
			{
				s11 = s1;
				if (s8.compareTo(s) > 0)
					return true;
				int i2 = s14.compareTo(s2);
				if (i2 >= 0)
					return true;
			} else
			if (s8.compareTo(s) > 0)
			{
				s14 = s2;
				int j2 = s11.compareTo(s1);
				if (j2 <= 0)
					return true;
			} else
			{
				int k2 = s11.compareTo(s1);
				int j3 = s14.compareTo(s2);
				if (k2 <= 0 && j3 >= 0)
					return true;
			}
			if (s11.compareTo(s14) < 0)
				d += Util.getDoubleValue(hrmschedulediffutil.getTotalWorkingDays(s, s11, s, s14, j), 0.0D);
		}
		if (d >= 1.0D)
			flag = true;
		return flag;
	}

	public List getScheduleList(String s, String s1, int i, int j, int k)
	{
		ArrayList arraylist = new ArrayList();
		Object obj = null;
		if (s == null || s.trim().equals("") || s1 == null || s1.trim().equals("") || s.compareTo(s1) > 0)
			return arraylist;
		try
		{
			DepartmentComInfo departmentcominfo = new DepartmentComInfo();
			RecordSet recordset = new RecordSet();
			Object obj1 = null;
			String s2 = "";
			String s4 = "";
			String s6 = "";
			String s8 = "";
			String s10 = "";
			String s12 = "";
			String s14 = "";
			String s16 = "";
			Object obj2 = null;
			Object obj3 = null;
			Object obj4 = null;
			boolean flag = false;
			String s20 = "";
			String s21 = "";
			boolean flag2 = false;
			boolean flag3 = true;
			HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
			hrmschedulediffutil.setUser(user);
			s20 = s;
			while (!flag2) 
			{
				if (s20.equals(s1))
					flag2 = true;
				boolean flag4 = hrmschedulediffutil.getIsWorkday(s20);
				if (!flag4)
				{
					String s22 = TimeUtil.dateAdd(s20, 1);
					s20 = s22;
				} else
				{
					Map map = hrmschedulediffutil.getOnDutyAndOffDutyTimeMap(s20, i);
					String s18 = Util.null2String((String)map.get("onDutyTimeAM"));
					String s19 = Util.null2String((String)map.get("offDutyTimePM"));
					StringBuffer stringbuffer = new StringBuffer();
					stringbuffer.append(" select subCompanyId1 as subCompanyId,departmentId,id as resourceId,lastName as resourceName,status,id as signId ").append("   from HrmResource ").append("  where status in(0,1,2,3) ");
					if ("oracle".equals(recordset.getDBType()))
						stringbuffer.append(" and loginid is not null ");
					else
						stringbuffer.append(" and loginid is not null and loginid<>'' ");
					stringbuffer.append("     and( ").append("          not exists( ").append("              select 1 ").append("               from HrmScheduleSign ").append("              where signDate='").append(s20).append("' ").append("                and signType='1' ").append("                and userType='1' ").append("                and isInCom='1' ").append("                and userId=HrmResource.id  ").append("          ) ").append("         or exists( ").append("             select 1 ").append("               from ( ").append("                   select userId,min(signTime) as signTime ").append("                     from HrmScheduleSign ").append("                    where signDate='").append(s20).append("' ").append("                      and signType='1' ").append("                      and userType='1' ").append("                      and isInCom='1' ").append("                     group by userId ").append("               )TempTable ").append("              where signTime>='").append(s19).append(":00' ").append("                and userId=HrmResource.id ").append("         ) ").append("         or exists( ").append("             select 1 ").append("               from ( ").append("                   select userId,max(signTime) as signTime ").append("                     from HrmScheduleSign ").append("                    where signDate='").append(s20).append("' ").append("                      and signType='2' ").append("                      and userType='1' ").append("                      and isInCom='1' ").append("                     group by userId ").append("               )TempTable ").append("              where signTime<='").append(s18).append(":00' ").append("                and userId=HrmResource.id ").append("         ) ").append(" )\t ");
					if (i > 0)
						stringbuffer.append(" and  subCompanyId1=").append(i);
					if (j > 0)
						stringbuffer.append(" and  departmentId=").append(j);
					if (k > 0)
						stringbuffer.append(" and  id=").append(k);
					if (sortForResult)
						stringbuffer.append("  order by subCompanyId1 asc,departmentId asc,id asc ");
					recordset.executeSql(stringbuffer.toString());
					while (recordset.next()) 
					{
						String s3 = Util.null2String(recordset.getString("subCompanyId"));
						String s5 = Util.null2String(recordset.getString("departmentId"));
						String s7 = departmentcominfo.getDepartmentname(s5);
						String s9 = Util.null2String(recordset.getString("resourceId"));
						String s11 = Util.null2String(recordset.getString("resourceName"));
						String s13 = Util.null2String(recordset.getString("status"));
						String s15 = hrmschedulediffutil.getStatusNameOfHrmResource(s13);
						String s17 = Util.null2String(recordset.getString("signId"));
						boolean flag1 = getHasApproved(Util.getIntValue(s9, -1), s20, s18, s19, Util.getIntValue(s3));
						if (!flag1)
						{
							HashMap hashmap = new HashMap();
							hashmap.put("subCompanyId", s3);
							hashmap.put("departmentId", s5);
							hashmap.put("departmentName", s7);
							hashmap.put("resourceId", s9);
							hashmap.put("resourceName", s11);
							hashmap.put("statusName", s15);
							hashmap.put("currentDate", s20);
							hashmap.put("signId", s17);
							arraylist.add(hashmap);
						}
					}
					String s23 = TimeUtil.dateAdd(s20, 1);
					s20 = s23;
				}
			}
			if (sortForResult && arraylist.size() >= 2)
				Collections.sort(arraylist, new HrmScheduleDiffListComparator());
			return arraylist;
		}
		catch (Exception e)
		{
			return arraylist;
		}
	}

	public void setSortForResult(boolean flag)
	{
		sortForResult = flag;
	}

	public void setUser(User user1)
	{
		user = user1;
	}
}
