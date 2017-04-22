// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SysRemindWorkflow.java

package weaver.system;

import java.util.Calendar;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.resource.ResourceComInfo;
import weaver.workflow.request.*;

public class SysRemindWorkflow extends BaseBean {

	private static RequestIdUpdate requestIdUpdate = new RequestIdUpdate();

	public SysRemindWorkflow() {
	}

	public int make(String requestname, int docid, int crmid, int prjid, int meetingid, int resource, String operators,
			String remark) throws Exception {
		if (operators == null || "".equals(operators) || "0".equals(operators))
			return 0;
		String as[] = Util.TokenizerString2(operators, ",");
		if (as == null || as.length == 0)
			return 0;
		RecordSet recordset = new RecordSet();
		RequestManager requestmanager = new RequestManager();
		RequestCheckAddinRules requestcheckaddinrules = new RequestCheckAddinRules();
		User user = new User();
		char c = Util.getSeparator();
		String s3 = "";
		Calendar calendar = Calendar.getInstance();
		String s4 = Util.add0(calendar.get(1), 4) + "-"
				+ Util.add0(calendar.get(2) + 1, 2) + "-"
				+ Util.add0(calendar.get(5), 2);
		String s5 = Util.add0(calendar.get(11), 2) + ":"
				+ Util.add0(calendar.get(12), 2) + ":"
				+ Util.add0(calendar.get(13), 2);
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = resource;
		int j5 = 0;
		int k5 = 0;
		String s6 = "submit";
		String s7 = "0";
		String s8 = "";
		String s9 = "0";
		String s10 = s9;
		String s11 = "0";
		String s12 = "";
		String s13 = s4;
		String s14 = s5;
		String s15 = "";
		String s16 = "";
		user.setUid(resource);
		user.setLogintype("1");
		user.setLanguage(7);
		j1 = requestIdUpdate.getRequestNewId()[0];
		String s17 = "select t2.id, t2.formid, t2.workflowtype, t2.messageType, t3.nodeid from workflow_sysworkflow t1,workflow_base t2 , workflow_flownode t3 where t2.id = t1.workflowid and t2.id = t3.workflowid and t1.id = 1 and t3.nodetype='0' ";
		recordset.executeSql(s17);
		if (recordset.next()) {
			k1 = Util.getIntValue(recordset.getString("formid"), 0);
			i2 = Util.getIntValue(recordset.getString("id"), 0);
			s8 = recordset.getString("workflowtype");
			l1 = Util.getIntValue(recordset.getString("nodeid"), 0);
			k5 = recordset.getInt("messageType");
		}
		int l5 = l1;
		String s18 = String.valueOf(docid);
		String s19 = String.valueOf(crmid);
		String s20 = resource + "," + operators;
		String s21 = String.valueOf(prjid);
		String s22 = "";
		String s23 = "<br>"
				+ (new ResourceComInfo()).getLastname(String.valueOf(resource)) + " "
				+ s4;
		requestmanager.setSrc(s6);
		requestmanager.setIscreate(s7);
		requestmanager.setRequestid(j1);
		requestmanager.setWorkflowid(i2);
		requestmanager.setWorkflowtype(s8);
		requestmanager.setIsremark(l2);
		requestmanager.setFormid(k1);
		requestmanager.setIsbill(k2);
		requestmanager.setBillid(j2);
		requestmanager.setNodeid(l1);
		requestmanager.setNodetype(s9);
		requestmanager.setRequestname(requestname);
		requestmanager.setRequestlevel(s11);
		requestmanager.setRemark(s23);
		requestmanager.setUser(user);
		requestmanager.setDocids(s18);
		requestmanager.setCrmids(s19);
		requestmanager.setHrmids(s20);
		requestmanager.setPrjids(s21);
		remark = Util.toHtml100(remark);
		String s24 = "update workflow_form set document = '" + docid
				+ "',Customer = " + crmid + ",Project = " + prjid + ",relatmeeting = "
				+ meetingid + ",resource_n = " + resource + ",mutiresource = '" + operators
				+ "',remark = '" + remark + "' where requestid=" + j1;
		recordset
				.executeSql("insert into workflow_form (requestid,billformid,billid) values("
						+ j1 + "," + k1 + "," + j2 + ")");
		recordset.executeSql(s24);
		s3 = "" + j1 + c + i2 + c + l5 + c + s10 + c + l1 + c + s9 + c + s12
				+ c + i3 + c + j3 + c + requestname + c + i5 + c + s13 + c + s14 + c + k3
				+ c + s15 + c + s16 + c + i4 + c + j5 + c + l3 + c + j4 + c
				+ k4 + c + s18 + c + s19 + c + s20 + c + s21 + c + s22 + c + k5;
		recordset.executeProc("workflow_Requestbase_Insert", s3);
		s3 = "" + j1 + c + resource + c + l4 + c + i2 + c + s8 + c + 0 + c + "0" + c
				+ "" + l1 + c + "-1" + c + "0" + c + "0" + c + 0;
		recordset.executeProc("workflow_CurrentOperator_I", s3);
		requestcheckaddinrules.resetParameter();
		requestcheckaddinrules.setRequestid(j1);
		requestcheckaddinrules.setObjid(l1);
		requestcheckaddinrules.setObjtype(1);
		requestcheckaddinrules.setIsbill(k2);
		requestcheckaddinrules.setFormid(k1);
		requestcheckaddinrules.checkAddinRules();
		boolean flag = requestmanager.flowNextNode();
		if (flag)
			requestmanager.saveRequestLog();
		return j1;
	}

	public void setCRMSysRemind(String s, int i, int j, String s1, String s2)
			throws Exception {
		setSysRemindInfo(s, 0, i, 0, 0, j, s1, s2);
	}

	public void setDocSysRemind(String s, int i, int j, String s1, String s2)
			throws Exception {
		setSysRemindInfo(s, i, 0, 0, 0, j, s1, s2);
	}

	public void setMeetingSysRemind(String s, int i, int j, String s1, String s2)
			throws Exception {
		setSysRemindInfo(s, 0, 0, 0, i, j, s1, s2);
	}

	public void setPrjSysRemind(String s, int i, int j, String s1, String s2)
			throws Exception {
		setSysRemindInfo(s, 0, 0, i, 0, j, s1, s2);
	}

	public void setSysRemindInfo(String s, int i, int j, int k, int l, int i1,
			String s1, String remark) throws Exception {
		if (s1 == null || "".equals(s1) || "0".equals(s1))
			return;
		String as[] = Util.TokenizerString2(s1, ",");
		if (as == null || as.length == 0)
			return;
		RecordSet recordset = new RecordSet();
		RequestManager requestmanager = new RequestManager();
		RequestCheckAddinRules requestcheckaddinrules = new RequestCheckAddinRules();
		User user = new User();
		char c = Util.getSeparator();
		String s3 = "";
		Calendar calendar = Calendar.getInstance();
		String s4 = Util.add0(calendar.get(1), 4) + "-"
				+ Util.add0(calendar.get(2) + 1, 2) + "-"
				+ Util.add0(calendar.get(5), 2);
		String s5 = Util.add0(calendar.get(11), 2) + ":"
				+ Util.add0(calendar.get(12), 2) + ":"
				+ Util.add0(calendar.get(13), 2);
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = i1;
		int j5 = 0;
		int k5 = 0;
		String operator = "submit";
		String s7 = "0";
		String s8 = "";
		String s9 = "0";
		String s10 = s9;
		String s11 = "0";
		String s12 = "";
		String s13 = s4;
		String s14 = s5;
		String s15 = "";
		String s16 = "";
		user.setUid(i1);
		user.setLogintype("1");
		user.setLanguage(7);
		j1 = requestIdUpdate.getRequestNewId()[0];
		String s17 = "select t2.id, t2.formid, t2.workflowtype, t2.messageType, t3.nodeid from workflow_sysworkflow t1,workflow_base t2 , workflow_flownode t3 where t2.id = t1.workflowid and t2.id = t3.workflowid and t1.id = 1 and t3.nodetype='0' ";
		recordset.executeSql(s17);
		if (recordset.next()) {
			k1 = Util.getIntValue(recordset.getString("formid"), 0);
			i2 = Util.getIntValue(recordset.getString("id"), 0);
			s8 = recordset.getString("workflowtype");
			l1 = Util.getIntValue(recordset.getString("nodeid"), 0);
			k5 = recordset.getInt("messageType");
		}
		int l5 = l1;
		String s18 = String.valueOf(i);
		String s19 = String.valueOf(j);
		String s20 = i1 + "," + s1;
		String s21 = String.valueOf(k);
		String s22 = "";
		String s23 = "<br>"
				+ (new ResourceComInfo()).getLastname(String.valueOf(i1)) + " "
				+ s4;
		requestmanager.setSrc(operator);
		requestmanager.setIscreate(s7);
		requestmanager.setRequestid(j1);
		requestmanager.setWorkflowid(i2);
		requestmanager.setWorkflowtype(s8);
		requestmanager.setIsremark(l2);
		requestmanager.setFormid(k1);
		requestmanager.setIsbill(k2);
		requestmanager.setBillid(j2);
		requestmanager.setNodeid(l1);
		requestmanager.setNodetype(s9);
		requestmanager.setRequestname(s);
		requestmanager.setRequestlevel(s11);
		requestmanager.setRemark(s23);
		requestmanager.setUser(user);
		requestmanager.setDocids(s18);
		requestmanager.setCrmids(s19);
		requestmanager.setHrmids(s20);
		requestmanager.setPrjids(s21);
		remark = Util.toHtml100(remark);
		String s24 = "update workflow_form set document = '" + i
				+ "',Customer = " + j + ",Project = " + k + ",relatmeeting = "
				+ l + ",resource_n = " + i1 + ",mutiresource = '" + s1
				+ "',remark = '" + remark + "' where requestid=" + j1;
		recordset
				.executeSql("insert into workflow_form (requestid,billformid,billid) values("
						+ j1 + "," + k1 + "," + j2 + ")");
		recordset.executeSql(s24);
		s3 = "" + j1 + c + i2 + c + l5 + c + s10 + c + l1 + c + s9 + c + s12
				+ c + i3 + c + j3 + c + s + c + i5 + c + s13 + c + s14 + c + k3
				+ c + s15 + c + s16 + c + i4 + c + j5 + c + l3 + c + j4 + c
				+ k4 + c + s18 + c + s19 + c + s20 + c + s21 + c + s22 + c + k5;
		recordset.executeProc("workflow_Requestbase_Insert", s3);
		s3 = "" + j1 + c + i1 + c + l4 + c + i2 + c + s8 + c + 0 + c + "0" + c
				+ "" + l1 + c + "-1" + c + "0" + c + "0" + c + 0;
		recordset.executeProc("workflow_CurrentOperator_I", s3);
		requestcheckaddinrules.resetParameter();
		requestcheckaddinrules.setRequestid(j1);
		requestcheckaddinrules.setObjid(l1);
		requestcheckaddinrules.setObjtype(1);
		requestcheckaddinrules.setIsbill(k2);
		requestcheckaddinrules.setFormid(k1);
		requestcheckaddinrules.checkAddinRules();
		boolean flag = requestmanager.flowNextNode();
		if (flag)
			requestmanager.saveRequestLog();
	}

}
