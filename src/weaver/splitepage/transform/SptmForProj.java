// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SptmForProj.java

package weaver.splitepage.transform;

import java.util.ArrayList;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;
import weaver.proj.Maint.ProjectStatusComInfo;
import weaver.proj.Maint.ProjectTypeComInfo;
import weaver.proj.Maint.WorkTypeComInfo;
import weaver.systeminfo.SystemEnv;

public class SptmForProj {

	private ProjectTypeComInfo pci;
	private WorkTypeComInfo wci;
	private SystemEnv sysEnv;
	private ProjectStatusComInfo statusComInfo;

	public SptmForProj() {
		try {
			pci = new ProjectTypeComInfo();
			wci = new WorkTypeComInfo();
			sysEnv = new SystemEnv();
			statusComInfo = new ProjectStatusComInfo();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public String getIsSelect(String s) {
		if ("0".equals(s))
			return "<img src='/images/BacoCross.gif'>";
		else
			return "<img src='/images/BacoCheck.gif'>";
	}

	public String getMembers(String s, String s1) throws Exception {
		String s2 = "";
		ArrayList arraylist = Util.TokenizerString(s, ",");
		ResourceComInfo resourcecominfo = new ResourceComInfo();
		for (int i = 0; i < arraylist.size(); i++)
			s2 = s2
					+ "<a href='/hrm/resource/HrmResource.jsp?id="
					+ arraylist.get(i)
					+ "'>"
					+ Util.toScreen(resourcecominfo.getResourcename(String
							.valueOf(String.valueOf(arraylist.get(i)))), Util
							.getIntValue(s1)) + "</a> ";

		return s2;
	}

	public String getProjStatusName(String s, String s1) {
		return statusComInfo.getProjectStatusdesc(s);
	}

	public String getProjTypeName(String s) {
		String s1 = pci.getProjectTypename(s);
		return "<a href='/proj/Maint/EditProjectType.jsp?id=" + s + "'>" + s1
				+ "</a>";
	}

	public String getTemletCount(String s) {
		String s1 = "0";
		RecordSet recordset = new RecordSet();
		recordset
				.executeSql("select count(id) from Prj_Template where protypeid="
						+ s);
		if (recordset.next())
			s1 = String.valueOf(Util.getIntValue(recordset.getString(1), 0));
		return "( <a href='EditProjectType.jsp?id=" + s + "#templet'>" + s1
				+ "</a> )";
	}

	public String getTemplateStatus(String s, String s1) {
		String s2 = "";
		if (s.equals("0") || s.equals("3"))
			s2 = SystemEnv.getHtmlLabelName(220, Util.getIntValue(s1));
		if (s.equals("1"))
			s2 = SystemEnv.getHtmlLabelName(225, Util.getIntValue(s1));
		if (s.equals("2"))
			s2 = SystemEnv.getHtmlLabelName(2242, Util.getIntValue(s1));
		return s2;
	}

	public String getWorkTypeName(String s) {
		String s1 = wci.getWorkTypename(s);
		return "<a href='/proj/Maint/EditWorkType.jsp?id=" + s + "'>" + s1
				+ "</a>";
	}

	public String linkToProjTask(String s, String s1) {
		return "<a href='/proj/process/ViewProcess.jsp?ProjID=" + s
				+ "' target=\"_fullwindow\">"
				+ SystemEnv.getHtmlLabelName(367, Util.getIntValue(s1))
				+ "</a>";
	}
}
