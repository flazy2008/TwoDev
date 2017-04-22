// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffDetSignInManager.java

package weaver.hrm.report.schedulediff;

import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.*;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;

// Referenced classes of package weaver.hrm.report.schedulediff:
//			HrmScheduleDiffUtil, HrmScheduleDiffListComparator

public class HrmScheduleDiffDetSignInManager extends BaseBean
{

	private User user;
	private boolean sortForResult;

	public HrmScheduleDiffDetSignInManager()
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
		String s22 = "";
		String s24 = "";
		boolean flag = false;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		hrmschedulediffutil.setUser(user);
		String s25;
		for (String s23 = s; !flag; s23 = s25)
		{
			if (s23.equals(s1))
				flag = true;
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append(" select b.subCompanyId1 as subCompanyId,b.departmentId,b.id as resourceId,b.lastName as resourceName,b.status,min(a.id) as signId ,a.signDate,min(a.signTime) as signTime ").append("   from HrmScheduleSign a,HrmResource b ").append("  where a.userId=b.id ").append("    and a.signDate='").append(s23).append("'").append("    and a.signType='1' ").append("    and a.isInCom='1' ").append("    and a.userType='1' ");
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
				stringbuffer.append("  order by b.subCompanyId1 asc,b.departmentId asc,b.id asc,a.signDate  asc ");
			recordset.executeSql(stringbuffer.toString());
			HashMap hashmap;
			for (; recordset.next(); arraylist.add(hashmap))
			{
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
				hashmap = new HashMap();
				hashmap.put("subCompanyId", s3);
				hashmap.put("departmentId", s5);
				hashmap.put("departmentName", s7);
				hashmap.put("resourceId", s9);
				hashmap.put("resourceName", s11);
				hashmap.put("statusName", s15);
				hashmap.put("currentDate", s23);
				hashmap.put("signId", s17);
				hashmap.put("signDate", s19);
				hashmap.put("signTime", s21);
			}

			s25 = TimeUtil.dateAdd(s23, 1);
		}

		if (sortForResult && arraylist.size() >= 2)
			Collections.sort(arraylist, new HrmScheduleDiffListComparator());
		arraylist2 = arraylist;
		return arraylist2;
	}
}
