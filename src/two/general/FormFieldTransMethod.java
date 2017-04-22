// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FormFieldTransMethod.java

package two.general;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.systeminfo.label.LabelComInfo;
import weaver.workflow.field.BrowserComInfo;

// Referenced classes of package weaver.general:
//			BaseBean, Util

public class FormFieldTransMethod extends BaseBean {

	private LabelComInfo labelinfo;
	private BrowserComInfo browserComInfo;
	private RecordSet rs;

	public FormFieldTransMethod() {
		try {
			labelinfo = new LabelComInfo();
			browserComInfo = new BrowserComInfo();
			rs = new RecordSet();
		} catch (Exception e) {
			writeLog(e);
		}
	}

	public String getFieldname(String s, String s1) {
		return labelinfo.getLabelname(s, s1);
	}

	public String getFieldDetail(String s, String s1) {
		return "<a href='/workflow/field/editfield.jsp?src=editfield&isused=true&fieldid="
				+ s1 + "'>" + s + "</a>";
	}

	public String getViewType(String s, String s1) {
		String s2 = "";
		if (s.equals("0")) {
			s2 = labelinfo.getLabelname("18020", s1);
		}
		if (s.equals("1")) {
			s2 = labelinfo.getLabelname("18550", s1);
		}
		return s2;
	}

	public String getHTMLType(String s, String s1) {
		String s2 = "";
		if (s.equals("1")) {
			s2 = labelinfo.getLabelname("688", s1);
		} else if (s.equals("2")) {
			s2 = labelinfo.getLabelname("689", s1);
		} else if (s.equals("3")) {
			s2 = labelinfo.getLabelname("695", s1);
		} else if (s.equals("4")) {
			s2 = labelinfo.getLabelname("691", s1);
		} else if (s.equals("5")) {
			s2 = labelinfo.getLabelname("690", s1);
		} else if (s.equals("6")) {
			s2 = labelinfo.getLabelname("17616", s1);
		} else if (s.equals("7")) {
			s2 = labelinfo.getLabelname("21691", s1);
		}
		return s2;
	}

	public String getFieldType(String s, String s1) {
		String as[] = Util.TokenizerString2(s1, "+");
		String s2 = as[0];
		String s3 = as[1];
		String s4 = as[2];
		String s5 = "";
		if (s2.equals("1")) {
			if (s.equals("1")) {
				s5 = labelinfo.getLabelname("608", s4);
			} else if (s.equals("2")) {
				s5 = labelinfo.getLabelname("696", s4);
			} else if (s.equals("3")) {
				s5 = labelinfo.getLabelname("697", s4);
			} else if (s.equals("4")) {
				s5 = labelinfo.getLabelname("18004", s4);
			} else if (s.equals("5")) {
				s5 = labelinfo.getLabelname("22395", s4);
			}
		} else if (s2.equals("2")) {
			s5 = labelinfo.getLabelname("689", s4);
		} else if (s2.equals("3")) {
			s5 = labelinfo
					.getLabelname(browserComInfo.getBrowserlabelid(s), s4);
		} else if (s2.equals("4")) {
			s5 = labelinfo.getLabelname("691", s4);
		} else if (s2.equals("5")) {
			rs
					.executeSql("select listorder,selectname from fla_SelectItem where fieldid="
							+ s3 + " order by listorder,selectvalue ");
			while (rs.next()) {
				if (!s5.equals(""))
					s5 = s5 + "<br>";
				s5 = s5 + rs.getString("selectname");
			}
		} else if (s2.equals("6")) {
			s5 = labelinfo.getLabelname("17616", s4);
		} else if (s2.equals("7")) {
			if (s.equals("1")) {
				s5 = labelinfo.getLabelname("21692", s4);
			} else if (s.equals("2")) {
				s5 = labelinfo.getLabelname("21693", s4);
			}
		}
		return s5;
	}

	public String getCanCheckBox(String s) {
		String as[] = Util.TokenizerString2(s, "+");
		String s1 = as[0];
		String s2 = as[1];
		String s3 = as[2];
		String s4 = as[3];
		String s5 = as[4];
		String s6 = "true";
		String s7 = "";
		if (s2.equals("1")) {
			s7 = s4;
		} else {
			rs.executeSql("select tablename from fla_bill where id=" + s5);
			if (rs.next())
				s7 = rs.getString("tablename");
		}
		String s8 = "select top 1 " + s1 + " from " + s7 + " where " + s1
				+ " not like '' ";
		if (rs.getDBType().equals("oracle"))
			s8 = "select " + s1 + " from " + s7 + " where rownum<2 and " + s1
					+ " is not null or " + s1 + " != ''";
		rs.executeSql(s8);
		if (rs.next())
			s6 = "false";
		return s6;
	}
}
