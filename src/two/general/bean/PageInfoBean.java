package two.general.bean;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class PageInfoBean {
	private String pagetype="";
	private String billid="";
	private String mainviewtype="";
	private String dtviewtype="";
	private String writelog="";
	private String isOper="";
	private String isProc="";
	RecordSet rs = new RecordSet();
	String v_sql = "";

	public PageInfoBean(String billid,String pageType,String isOper,String isProc) {
		v_sql = "select * from fla_nodePage bill where billid in(" + billid + ") and pagetype='"+pageType+"'";
		rs.executeSql(v_sql);
		if (rs.next()) {
			this.pagetype= Util.null2String(rs.getString("pagetype"));
			this.billid= Util.null2String(rs.getString("billid"));
			this.mainviewtype= Util.null2String(rs.getString("mainviewtype"));
			this.dtviewtype= Util.null2String(rs.getString("dtviewtype"));
			this.writelog= Util.null2String(rs.getString("writelog"));
			this.isOper=isOper;
			this.isProc=isProc;
		}
	}

	public String getPagetype() {
		return pagetype;
	}

	public void setPagetype(String pagetype) {
		this.pagetype = pagetype;
	}

	public String getBillid() {
		return billid;
	}

	public void setBillid(String billid) {
		this.billid = billid;
	}

	public String getMainviewtype() {
		return mainviewtype;
	}

	public void setMainviewtype(String mainviewtype) {
		this.mainviewtype = mainviewtype;
	}

	public String getDtviewtype() {
		return dtviewtype;
	}

	public void setDtviewtype(String dtviewtype) {
		this.dtviewtype = dtviewtype;
	}

	public String getWritelog() {
		return writelog;
	}

	public void setWritelog(String writelog) {
		this.writelog = writelog;
	}

	public String getIsOper() {
		return isOper;
	}

	public void setIsOper(String isOper) {
		this.isOper = isOper;
	}

	public String getIsProc() {
		return isProc;
	}

	public void setIsProc(String isProc) {
		this.isProc = isProc;
	}

}
