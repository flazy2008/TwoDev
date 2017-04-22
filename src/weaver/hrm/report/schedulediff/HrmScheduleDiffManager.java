// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffManager.java

package weaver.hrm.report.schedulediff;

import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.*;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;

// Referenced classes of package weaver.hrm.report.schedulediff:
//			HrmScheduleDiffDetAbsentFromWorkManager, HrmScheduleDiffDetBeLateManager, HrmScheduleDiffDetLeaveEarlyManager, HrmScheduleDiffDetNoSignManager, 
//			HrmScheduleDiffUtil

public class HrmScheduleDiffManager extends BaseBean {

	private String criticalOfAandBForAM;
	private String criticalOfAandBForPM;
	private User user;

	public HrmScheduleDiffManager() {
		criticalOfAandBForAM = "09:00:00";
		criticalOfAandBForPM = "17:00:00";
		user = null;
	}
	public List getScheduleList(String fromDate, String toDate, int orgid, int deptid, int k) {
		ArrayList arraylist = new ArrayList();
		Object obj = null;
		HashMap hashmap1 = new HashMap();
		RecordSet recordset = new RecordSet();
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(" select id,lastName,departmentId ").append(
				"   from HrmResource ").append("  where status in(0,1,2,3) ");
		if ("oracle".equals(recordset.getDBType()))
			stringbuffer.append(" and loginid is not null ");
		else
			stringbuffer.append(" and loginid is not null and loginid<>'' ");
		if (orgid > 0)
			stringbuffer.append(" and  subCompanyId1=").append(orgid);
		if (deptid > 0)
			stringbuffer.append(" and  departmentId=").append(deptid);
		if (k > 0)
			stringbuffer.append(" and  id=").append(k);
		stringbuffer
				.append("  order by subCompanyId1 asc,departmentId asc,id asc ");
		try {
			DepartmentComInfo departmentcominfo = new DepartmentComInfo();
			boolean flag = false;
			String s2 = "";
			boolean flag1 = false;
			String s4 = "";
			int j1 = 0;
			recordset.executeSql(stringbuffer.toString());
			System.out.println("v_sql_1:"+stringbuffer);
			while (recordset.next()) {
				int l = recordset.getInt("departmentId");
				String s3 = departmentcominfo.getDepartmentname(String
						.valueOf(l));
				int i1 = recordset.getInt("id");
				String s5 = Util.null2String(recordset.getString("lastName"));
				HashMap hashmap = new HashMap();
				hashmap.put("departmentName", s3);
				hashmap.put("resourceName", s5);
				hashmap1.put(String.valueOf(i1), String.valueOf(j1));
				arraylist.add(hashmap);
				j1++;
			}
			updateDataOfBeLate(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			updateDataOfLeaveEarly(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			updateDataOfAbsentFromWork(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			updateDataOfNoSign(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			updateDataOfLeave(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			updateDataOfEvection(arraylist, hashmap1, fromDate, toDate, orgid, deptid, k);
			return arraylist;
		} catch (Exception e) {
			return arraylist;
		}
	}

	public int getTotalWorkingDays(String s, String s1) {
		int i = 0;
		if (s == null || s.trim().equals("") || s1 == null
				|| s1.trim().equals("") || s.compareTo(s1) > 0)
			return i;
		String s2 = "";
		String s3 = "";
		boolean flag = false;
		boolean flag1 = true;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		hrmschedulediffutil.setUser(user);
		s2 = s;
		while (!flag) {
			if (s2.equals(s1))
				flag = true;
			boolean flag2 = hrmschedulediffutil.getIsWorkday(s2);
			if (!flag2) {
				String s4 = TimeUtil.dateAdd(s2, 1);
				s2 = s4;
			} else {
				String s5 = TimeUtil.dateAdd(s2, 1);
				s2 = s5;
				i++;
			}
		}
		return i;
	}

	public void setUser(User user1) {
		user = user1;
	}

	private void updateDataOfAbsentFromWork(List list, Map map, String s,
			String s1, int i, int j, int k) {
		HrmScheduleDiffDetAbsentFromWorkManager hrmschedulediffdetabsentfromworkmanager = new HrmScheduleDiffDetAbsentFromWorkManager();
		hrmschedulediffdetabsentfromworkmanager.setUser(user);
		hrmschedulediffdetabsentfromworkmanager.setSortForResult(false);
		List list1 = hrmschedulediffdetabsentfromworkmanager.getScheduleList(s,
				s1, i, j, k);
		Object obj = null;
		String s2 = "";
		byte byte0 = -1;
		Object obj1 = null;
		String s4 = "";
		for (int i1 = 0; i1 < list1.size(); i1++) {
			Map map1 = (Map) list1.get(i1);
			String s3 = Util.null2String((String) map1.get("resourceId"));
			int l = Util.getIntValue((String) map.get(s3), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				String s5 = (String) map2.get("absentFromWork");
				s5 = String.valueOf(Util.getIntValue(s5, 0) + 1);
				map2.put("absentFromWork", s5);
			}
		}

	}

	private void updateDataOfBeLate(List list, Map map, String s, String s1,
			int i, int j, int k) {
		HrmScheduleDiffDetBeLateManager hrmschedulediffdetbelatemanager = new HrmScheduleDiffDetBeLateManager();
		hrmschedulediffdetbelatemanager.setUser(user);
		hrmschedulediffdetbelatemanager.setSortForResult(false);
		List list1 = hrmschedulediffdetbelatemanager.getScheduleList(s, s1, i,
				j, k);
		Object obj = null;
		String s2 = "";
		String s4 = "";
		byte byte0 = -1;
		Object obj1 = null;
		String s6 = "";
		String s8 = "";
		for (int i1 = 0; i1 < list1.size(); i1++) {
			Map map1 = (Map) list1.get(i1);
			String s3 = Util.null2String((String) map1.get("resourceId"));
			String s5 = Util.null2String((String) map1.get("signTime"));
			int l = Util.getIntValue((String) map.get(s3), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				if (s5.compareTo(criticalOfAandBForAM) < 0) {
					String s7 = (String) map2.get("beLateA");
					s7 = String.valueOf(Util.getIntValue(s7, 0) + 1);
					map2.put("beLateA", s7);
				} else {
					String s9 = (String) map2.get("beLateB");
					s9 = String.valueOf(Util.getIntValue(s9, 0) + 1);
					map2.put("beLateB", s9);
				}
			}
		}

	}

	private void updateDataOfEvection(List list, Map map, String s, String s1,
			int i, int j, int k) {
		ArrayList arraylist = new ArrayList();
		Object obj = null;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		hrmschedulediffutil.setUser(user);
		String s2 = "";
		String s5 = "";
		String s8 = "";
		String s11 = "";
		String s14 = "";
		String s17 = "";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer
				.append(
						" select c.id as resourceId,b.fromDate,b.fromTime,b.toDate,b.toTime ")
				.append(
						"   from Workflow_Requestbase a,Bill_BoHaiEvection b,HrmResource c ")
				.append("  where a.requestId=b.requestId ").append(
						"    and b.resourceId=c.id ").append(
						"    and a.currentNodeType='3' ").append(
						"    and c.status in(0,1,2,3) ");
		if (!s.equals(""))
			stringbuffer.append(" and  b.toDate>='").append(s).append("'");
		if (!s1.equals(""))
			stringbuffer.append(" and  b.fromDate<='").append(s1).append("'");
		if (i > 0)
			stringbuffer.append(" and  c.subCompanyId1=").append(i);
		if (j > 0)
			stringbuffer.append(" and  c.departmentId=").append(j);
		if (k > 0)
			stringbuffer.append(" and  c.id=").append(k);
		RecordSet recordset = new RecordSet();
		System.out.println("v_sql_2:"+stringbuffer);
		recordset.executeSql(stringbuffer.toString());
		HashMap hashmap;
		for (; recordset.next(); arraylist.add(hashmap)) {
			String s3 = Util.null2String(recordset.getString("resourceId"));
			String s6 = Util.null2String(recordset.getString("fromDate"));
			String s9 = Util.null2String(recordset.getString("fromTime"));
			String s12 = Util.null2String(recordset.getString("toDate"));
			String s15 = Util.null2String(recordset.getString("toTime"));
			hashmap = new HashMap();
			hashmap.put("resourceId", s3);
			hashmap.put("fromDate", s6);
			hashmap.put("fromTime", s9);
			hashmap.put("toDate", s12);
			hashmap.put("toTime", s15);
		}

		byte byte0 = -1;
		Object obj1 = null;
		String s19 = "";
		for (int i1 = 0; i1 < arraylist.size(); i1++) {
			Map map1 = (Map) arraylist.get(i1);
			String s4 = Util.null2String((String) map1.get("resourceId"));
			String s7 = Util.null2String((String) map1.get("fromDate"));
			String s10 = Util.null2String((String) map1.get("fromTime"));
			String s13 = Util.null2String((String) map1.get("toDate"));
			String s16 = Util.null2String((String) map1.get("toTime"));
			if (s7.compareTo(s) < 0) {
				s7 = s;
				s10 = "00:00";
			}
			if (s13.compareTo(s1) > 0) {
				s13 = s1;
				s16 = "23:59";
			}
			String s18 = hrmschedulediffutil.getTotalWorkingDays(s7, s10, s13,
					s16, i);
			int l = Util.getIntValue((String) map.get(s4), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				String s20 = (String) map2.get("evection");
				s20 = Util.getPointValue(Util.round(String.valueOf(Util
						.getDoubleValue(s20, 0.0D)
						+ Util.getDoubleValue(s18, 0.0D)), 2));
				map2.put("evection", s20);
			}
		}

	}

	private void updateDataOfLeave(List list, Map map, String s, String s1,
			int i, int j, int k) {
		ArrayList arraylist = new ArrayList();
		Object obj = null;
		HrmScheduleDiffUtil hrmschedulediffutil = new HrmScheduleDiffUtil();
		hrmschedulediffutil.setUser(user);
		String s2 = "";
		String s5 = "";
		String s8 = "";
		String s11 = "";
		String s14 = "";
		String s17 = "";
		String s20 = "";
		String s23 = "";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer
				.append(
						" select c.id as resourceId,b.fromDate,b.fromTime,b.toDate,b.toTime,b.leaveDays,b.leaveType,b.otherLeaveType ")
				.append(
						"   from Workflow_Requestbase a,Bill_BoHaiLeave b,HrmResource c ")
				.append("  where a.requestId=b.requestId ").append(
						"    and b.resourceId=c.id ").append(
						"    and a.currentNodeType='3' ").append(
						"    and c.status in(0,1,2,3) ");
		if (!s.equals(""))
			stringbuffer.append(" and  b.toDate>='").append(s).append("'");
		if (!s1.equals(""))
			stringbuffer.append(" and  b.fromDate<='").append(s1).append("'");
		if (i > 0)
			stringbuffer.append(" and  c.subCompanyId1=").append(i);
		if (j > 0)
			stringbuffer.append(" and  c.departmentId=").append(j);
		if (k > 0)
			stringbuffer.append(" and  c.id=").append(k);
		RecordSet recordset = new RecordSet();
		System.out.println("v_sql_3:"+stringbuffer);
		recordset.executeSql(stringbuffer.toString());
		HashMap hashmap;
		for (; recordset.next(); arraylist.add(hashmap)) {
			String s3 = Util.null2String(recordset.getString("resourceId"));
			String s6 = Util.null2String(recordset.getString("fromDate"));
			String s9 = Util.null2String(recordset.getString("fromTime"));
			String s12 = Util.null2String(recordset.getString("toDate"));
			String s15 = Util.null2String(recordset.getString("toTime"));
			String s18 = Util.null2String(recordset.getString("leaveDays"));
			String s21 = Util.null2String(recordset.getString("leaveType"));
			String s24 = Util
					.null2String(recordset.getString("otherLeaveType"));
			hashmap = new HashMap();
			hashmap.put("resourceId", s3);
			hashmap.put("fromDate", s6);
			hashmap.put("fromTime", s9);
			hashmap.put("toDate", s12);
			hashmap.put("toTime", s15);
			hashmap.put("leaveDays", s18);
			hashmap.put("leaveType", s21);
			hashmap.put("otherLeaveType", s24);
		}

		byte byte0 = -1;
		Object obj1 = null;
		String s26 = "";
		String s31 = "";
		String s34 = "";
		String s37 = "";
		for (int i1 = 0; i1 < arraylist.size(); i1++) {
			Map map1 = (Map) arraylist.get(i1);
			String s4 = Util.null2String((String) map1.get("resourceId"));
			String s22 = Util.null2String((String) map1.get("leaveType"));
			String s25 = Util.null2String((String) map1.get("otherLeaveType"));
			String s7 = Util.null2String((String) map1.get("fromDate"));
			String s10 = Util.null2String((String) map1.get("fromTime"));
			String s13 = Util.null2String((String) map1.get("toDate"));
			String s16 = Util.null2String((String) map1.get("toTime"));
			if (s7.compareTo(s) < 0) {
				s7 = s;
				s10 = "00:00";
			}
			if (s13.compareTo(s1) > 0) {
				s13 = s1;
				s16 = "23:59";
			}
			String s19 = hrmschedulediffutil.getTotalWorkingDays(s7, s10, s13,
					s16, i);
			int l = Util.getIntValue((String) map.get(s4), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				if (s22.trim().equals("1")) {
					String s27 = Util.null2String((String) map2
							.get("privateAffairLeave"));
					s27 = String.valueOf(Util.getDoubleValue(s27, 0.0D)
							+ Util.getDoubleValue(s19, 0.0D));
					map2.put("privateAffairLeave", s27);
				} else if (s22.trim().equals("2")) {
					String s28 = Util.null2String((String) map2
							.get("sickLeave"));
					s28 = String.valueOf(Util.getDoubleValue(s28, 0.0D)
							+ Util.getDoubleValue(s19, 0.0D));
					map2.put("sickLeave", s28);
				} else if (s22.trim().equals("3")) {
					String s29 = Util.null2String((String) map2
							.get("otherLeaveB"));
					s29 = String.valueOf(Util.getDoubleValue(s29, 0.0D)
							+ Util.getDoubleValue(s19, 0.0D));
					map2.put("otherLeaveB", s29);
					String s35 = hrmschedulediffutil.getBillSelectName(180,
							"leaveType", Util.getIntValue(s22, -1));
					String s38 = hrmschedulediffutil.getBillSelectName(180,
							"otherLeaveType", Util.getIntValue(s25, -1));
					String s32 = Util.null2String((String) map2.get("remark"));
					if (s32.trim().equals(""))
						s32 = s32 + s35 + "£¨" + s38 + "£©";
					else
						s32 = s32 + "£¬" + s35 + "£¨" + s38 + "£©";
					map2.put("remark", s32);
				} else if (s22.trim().equals("4")) {
					String s30 = Util.null2String((String) map2
							.get("otherLeaveA"));
					s30 = String.valueOf(Util.getDoubleValue(s30, 0.0D)
							+ Util.getDoubleValue(s19, 0.0D));
					map2.put("otherLeaveA", s30);
					String s36 = hrmschedulediffutil.getBillSelectName(180,
							"leaveType", Util.getIntValue(s22, -1));
					String s39 = hrmschedulediffutil.getBillSelectName(180,
							"otherLeaveType", Util.getIntValue(s25, -1));
					String s33 = Util.null2String((String) map2.get("remark"));
					if (s33.trim().equals(""))
						s33 = s33 + s36 + "£¨" + s39 + "£©";
					else
						s33 = s33 + "£¬" + s36 + "£¨" + s39 + "£©";
					map2.put("remark", s33);
				}
			}
		}

	}

	private void updateDataOfLeaveEarly(List list, Map map, String s,
			String s1, int i, int j, int k) {
		HrmScheduleDiffDetLeaveEarlyManager hrmschedulediffdetleaveearlymanager = new HrmScheduleDiffDetLeaveEarlyManager();
		hrmschedulediffdetleaveearlymanager.setUser(user);
		hrmschedulediffdetleaveearlymanager.setSortForResult(false);
		List list1 = hrmschedulediffdetleaveearlymanager.getScheduleList(s, s1,
				i, j, k);
		Object obj = null;
		String s2 = "";
		String s4 = "";
		byte byte0 = -1;
		Object obj1 = null;
		String s6 = "";
		String s8 = "";
		for (int i1 = 0; i1 < list1.size(); i1++) {
			Map map1 = (Map) list1.get(i1);
			String s3 = Util.null2String((String) map1.get("resourceId"));
			String s5 = Util.null2String((String) map1.get("signTime"));
			int l = Util.getIntValue((String) map.get(s3), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				if (s5.compareTo(criticalOfAandBForPM) > 0) {
					String s7 = (String) map2.get("leaveEarlyA");
					s7 = String.valueOf(Util.getIntValue(s7, 0) + 1);
					map2.put("leaveEarlyA", s7);
				} else {
					String s9 = (String) map2.get("leaveEarlyB");
					s9 = String.valueOf(Util.getIntValue(s9, 0) + 1);
					map2.put("leaveEarlyB", s9);
				}
			}
		}

	}

	private void updateDataOfNoSign(List list, Map map, String s, String s1,
			int i, int j, int k) {
		HrmScheduleDiffDetNoSignManager hrmschedulediffdetnosignmanager = new HrmScheduleDiffDetNoSignManager();
		hrmschedulediffdetnosignmanager.setUser(user);
		hrmschedulediffdetnosignmanager.setSortForResult(false);
		List list1 = hrmschedulediffdetnosignmanager.getScheduleList(s, s1, i,
				j, k);
		Object obj = null;
		String s2 = "";
		byte byte0 = -1;
		Object obj1 = null;
		String s4 = "";
		for (int i1 = 0; i1 < list1.size(); i1++) {
			Map map1 = (Map) list1.get(i1);
			String s3 = Util.null2String((String) map1.get("resourceId"));
			int l = Util.getIntValue((String) map.get(s3), -1);
			if (l >= 0) {
				Map map2 = (Map) list.get(l);
				String s5 = (String) map2.get("noSign");
				s5 = String.valueOf(Util.getIntValue(s5, 0) + 1);
				map2.put("noSign", s5);
			}
		}

	}
}
