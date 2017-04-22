package two.work.hrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.report.schedulediff.HrmScheduleDiffDetBeLateManager;

public class HrmScheduleManager {
	private String criticalForAM;
	private String criticalForPM;
	private User user;
	private String v_sql = "";
	private String SqlWhere = "";
	HrmScheduleUtil scheduleUtil = new HrmScheduleUtil();

	public HrmScheduleManager() {
		criticalForAM = "09:00:00";
		criticalForPM = "17:30:00";
		user = null;
	}

	public List getScheduleList(String fromDate, String toDate, int orgid,
			int deptid, int resourceid) {
		ArrayList reList = new ArrayList();
		ArrayList dataList = null;
		HashMap rowMap = new HashMap();
		SqlWhere = "";
		RecordSet recordset = new RecordSet();
		if (orgid > 0) {
			SqlWhere += "  and subCompanyId1='" + orgid + "'";
		}
		if (deptid > 0) {
			SqlWhere += "  and departmentId='" + deptid + "'";
		}
		if (resourceid > 0) {
			SqlWhere += "  and id='" + resourceid + "'";
		}

		v_sql = "select id, lastName, departmentId  from HrmResource where status in (0, 1, 2, 3)   and loginid is not null "
				+ SqlWhere
				+ " order by subCompanyId1 asc, departmentId asc, id asc";
		try {
			DepartmentComInfo departmentcominfo = new DepartmentComInfo();
			boolean flag = false;
			String s2 = "";
			boolean flag1 = false;
			String s4 = "";
			int rowindex = 0;
			recordset.executeSql(v_sql);
			while (recordset.next()) {
				int v_deptid = recordset.getInt("departmentId");
				String v_deptname = departmentcominfo
						.getDepartmentname(v_deptid + "");
				int v_userid = recordset.getInt("id");
				String v_username = Util.null2String(recordset
						.getString("lastName"));

				HashMap userMap = new HashMap();
				userMap.put("resourceid", v_userid);
				userMap.put("departmentName", v_deptname);
				userMap.put("resourceName", v_username);
				dataList = scheduleUtil.getScheduleList(v_userid, fromDate,toDate);
				this.countScheduleData(userMap, dataList);
				reList.add(userMap);
				rowindex++;
			}
			return reList;
		} catch (Exception e) {
			return reList;
		}
	}

	private void countScheduleData(HashMap userMap, ArrayList dataList) {// 统计数据
		int belate = 0; // 迟到
		int leaveearly = 0;// 早退
		int vacate = 0;// 请假
		int evection = 0;// 出差
		int beout = 0;// 外出
		int nowork = 0;// 旷工
		for (int i = 0; i < dataList.size(); i++) {
			HrmCardInfoBean bean = (HrmCardInfoBean) dataList.get(i);
			if(!"".equals(bean.getBelate())&&!"0".equals(bean.getBelate())){
				System.out.println("bean.getBelate():"+bean.getBelate());
				belate++;
			}
			if(!"".equals(bean.getLeaveearly())&&!"0".equals(bean.getLeaveearly())){
				leaveearly++;
			}
			if(!"".equals(bean.getVacate())&&!"0".equals(bean.getVacate())){
				vacate++;
			}
			if(!"".equals(bean.getEvection())&&!"0".equals(bean.getEvection())){
				evection++;
			}
			if(!"".equals(bean.getBeout())&&!"0".equals(bean.getBeout())){
				beout++;
			}
			if(!"".equals(bean.getNowork())&&!"0".equals(bean.getNowork())){
				nowork++;
			}
		}
		userMap.put("belate", belate);//迟到
		userMap.put("leaveearly", leaveearly);// 早退
		userMap.put("vacate", vacate);//请假
		userMap.put("evection", evection);//出差
		userMap.put("beout", beout);//外出
		userMap.put("nowork", nowork);// 旷工
	}

}
