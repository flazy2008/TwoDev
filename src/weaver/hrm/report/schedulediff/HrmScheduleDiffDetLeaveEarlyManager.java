// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffDetLeaveEarlyManager.java

package weaver.hrm.report.schedulediff;

import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.*;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;

// Referenced classes of package weaver.hrm.report.schedulediff:
//			HrmScheduleDiffUtil, HrmScheduleDiffListComparator

public class HrmScheduleDiffDetLeaveEarlyManager extends BaseBean
{

	private User user;
	private boolean sortForResult;

	public HrmScheduleDiffDetLeaveEarlyManager()
	{
		user = null;
		sortForResult = true;
	}

	public void setUser(User user1)
	{
		user = user1;
	}

	public void setSortForResult(boolean flag)
	{
		sortForResult = flag;
	}

	public List getScheduleList(String s, String s1, int i, int j, int k)
	{
		ArrayList arraylist;
		arraylist = new ArrayList();
		Object obj = null;
		if (s == null || s.trim().equals("") || s1 == null || s1.trim().equals("") || s.compareTo(s1) > 0)
			return arraylist;
		ArrayList arraylist2;
		DepartmentComInfo departmentcominfo = null;
		try {
			departmentcominfo = new DepartmentComInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		String s18 = "";
		String s20 = "";
		Object obj2 = null;
		Object obj3 = null;
		Object obj4 = null;
		boolean flag = false;
		String s24 = "";
		String s25 = "";
		boolean flag2 = false;
		boolean flag3 = true;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		hrmschedulediffutil.setUser(user);
		s24 = s;
		while (!flag2) 
		{
			if (s24.equals(s1))
				flag2 = true;
			boolean flag4 = hrmschedulediffutil.getIsWorkday(s24);
			if (!flag4)
			{
				String s26 = TimeUtil.dateAdd(s24, 1);
				s24 = s26;
			} else
			{
				Map map = hrmschedulediffutil.getOnDutyAndOffDutyTimeMap(s24, i);
				String s22 = Util.null2String((String)map.get("onDutyTimeAM"));
				String s23 = Util.null2String((String)map.get("offDutyTimePM"));
				StringBuffer stringbuffer = new StringBuffer();
				stringbuffer.append(" select * from( ");
				if ("oracle".equals(recordset.getDBType()))
					stringbuffer.append(" select b.subCompanyId1 as subCompanyId,b.departmentId,b.id as resourceId,b.lastName as resourceName,b.status,max(a.id) as signId ,a.signDate,max(a.signTime) as signTime ");
				else
					stringbuffer.append(" select top 1000000 b.subCompanyId1 as subCompanyId,b.departmentId,b.id as resourceId,b.lastName as resourceName,b.status,max(a.id) as signId ,a.signDate,max(a.signTime) as signTime ");
				stringbuffer.append("   from HrmScheduleSign a,HrmResource b ").append("  where a.userId=b.id ").append("    and a.signDate='").append(s24).append("'").append("    and a.signType='2' ").append("    and a.isInCom='1' ").append("    and a.userType='1' ").append("    and a.signTime>'").append(s22).append(":00' ");
				if (i > 0)
					stringbuffer.append(" and  b.subCompanyId1=").append(i);
				if (j > 0)
					stringbuffer.append(" and  b.departmentId=").append(j);
				if (k > 0)
					stringbuffer.append(" and  b.id=").append(k);
				stringbuffer.append("    and b.status in(0,1,2,3) ");
				if ("oracle".equals(recordset.getDBType()))
					stringbuffer.append(" and b.loginid is not null ");
				else
					stringbuffer.append(" and b.loginid is not null and b.loginid<>'' ");
				stringbuffer.append("  group by b.subCompanyId1 ,b.departmentId,b.id ,b.lastName ,b.status ,a.signDate");
				if (sortForResult)
					stringbuffer.append("  order by b.subCompanyId1 asc,b.departmentId asc,b.id asc,a.signDate asc ");
				stringbuffer.append(" )TempTable where signTime<'").append(s23).append(":00' ");
				recordset.executeSql(stringbuffer.toString());
				do
				{
					if (!recordset.next())
						break;
					String s3 = Util.null2String(recordset.getString("subCompanyId"));
					String s5 = Util.null2String(recordset.getString("departmentId"));
					String s7 = departmentcominfo.getDepartmentname(s5);
					String s9 = Util.null2String(recordset.getString("resourceId"));
					String s11 = Util.null2String(recordset.getString("resourceName"));
					String s13 = Util.null2String(recordset.getString("status"));
					String s15 = hrmschedulediffutil.getStatusNameOfHrmResource(s13);
					String s17 = Util.null2String(recordset.getString("signId"));
					String s19 = Util.null2String(recordset.getString("signDate"));
					String s21 = Util.null2String(recordset.getString("signTime"));
					boolean flag1 = getHasApproved(Util.getIntValue(s9, -1), s24, s21, s23);
					if (!flag1)
					{
						HashMap hashmap = new HashMap();
						hashmap.put("subCompanyId", s3);
						hashmap.put("departmentId", s5);
						hashmap.put("departmentName", s7);
						hashmap.put("resourceId", s9);
						hashmap.put("resourceName", s11);
						hashmap.put("statusName", s15);
						hashmap.put("currentDate", s24);
						hashmap.put("signId", s17);
						hashmap.put("signDate", s19);
						hashmap.put("signTime", s21);
						arraylist.add(hashmap);
					}
				} while (true);
				String s27 = TimeUtil.dateAdd(s24, 1);
				s24 = s27;
			}
		}
		if (sortForResult && arraylist.size() >= 2)
			Collections.sort(arraylist, new HrmScheduleDiffListComparator());
		arraylist2 = arraylist;
		return arraylist2;
	}

	private boolean getHasApproved(int i, String s, String s1, String s2)
	{
		boolean flag = false;
		String s3 = getLastMinOfSignTime((new StringBuilder()).append(s).append(" ").append(s1).toString());
		RecordSet recordset = new RecordSet();
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(" select 1 ").append("   from Workflow_Requestbase a,Bill_BoHaiLeave b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s3).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s2).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s3).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s2).append("' ");
		recordset.executeSql(stringbuffer.toString());
		if (recordset.next())
		{
			flag = true;
			return flag;
		}
		stringbuffer = new StringBuffer();
		stringbuffer.append(" select 1 ").append("   from Workflow_Requestbase a,Bill_BoHaiEvection b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s3).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s2).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s3).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s2).append("' ");
		recordset.executeSql(stringbuffer.toString());
		if (recordset.next())
		{
			flag = true;
			return flag;
		}
		stringbuffer = new StringBuffer();
		stringbuffer.append(" select 1 ").append("   from Workflow_Requestbase a,Bill_BoHaiOut b ").append("  where a.requestId=b.requestId ").append("    and a.currentNodeType='3' ").append("    and resourceId=").append(i);
		if (recordset.getDBType().equals("oracle"))
			stringbuffer.append("    and concat(concat(fromDate,' '),fromTime)<='").append(s3).append("' ").append("    and concat(concat(toDate,' '),toTime)>='").append(s).append(" ").append(s2).append("' ");
		else
			stringbuffer.append("    and fromDate+' '+fromTime<='").append(s3).append("' ").append("    and toDate+' '+toTime>='").append(s).append(" ").append(s2).append("' ");
		recordset.executeSql(stringbuffer.toString());
		if (recordset.next())
		{
			flag = true;
			return flag;
		} else
		{
			return flag;
		}
	}

	private String getLastMinOfSignTime(String s)
	{
		String s1 = "";
		if (s == null || s.trim().equals("") || s.length() < 19)
		{
			return s1;
		} else
		{
			String s2 = s.substring(0, 16);
			return s2;
		}
	}
}
