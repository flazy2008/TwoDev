package two.general.bean;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class FieldViewBean {
	private String pageType="";//ҳ������
	private String fieldid = "";// �ֶ�ID
	private String isview = "";// �Ƿ���ʾ
	private String isedit = "";// �Ƿ�༭
	private String ismandatory = "";// �Ƿ����
	private String islog = "";// �Ƿ��¼������־
	private String isevent = "";// �Ƿ����¼�
	RecordSet rs = new RecordSet();
	String v_sql = "";

	public FieldViewBean(String fieldid, String pageType) {
		String isview = "";
		String isedit = "";
		String ismandatory = "";
		String islog = "";
		String isevent = "";
		v_sql = "select * from fla_nodeform nod where nod.pagetype='"+ pageType + "' and fieldid in(" + fieldid + ")";
		rs.executeSql(v_sql);
		if (rs.next()) {
			fieldid = Util.null2String(rs.getString("fieldid"));
			isview = Util.null2String(rs.getString("isview"));
			isedit = Util.null2String(rs.getString("isedit"));
			ismandatory = Util.null2String(rs.getString("ismandatory"));
			islog = Util.null2String(rs.getString("islog"));
			isevent = Util.null2String(rs.getString("isevent"));
		}
		this.pageType=pageType;
		this.fieldid = fieldid;
		this.isview = isview;
		this.isedit = isedit;
		this.ismandatory = ismandatory;
		this.islog = islog;
		this.isevent = isevent;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public String getIsview() {
		return isview;
	}

	public void setIsview(String isview) {
		this.isview = isview;
	}

	public String getIsedit() {
		return isedit;
	}

	public void setIsedit(String isedit) {
		this.isedit = isedit;
	}

	public String getIsmandatory() {
		return ismandatory;
	}

	public void setIsmandatory(String ismandatory) {
		this.ismandatory = ismandatory;
	}

	public String getIslog() {
		return islog;
	}

	public void setIslog(String islog) {
		this.islog = islog;
	}

	public String getIsevent() {
		return isevent;
	}

	public void setIsevent(String isevent) {
		this.isevent = isevent;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

}
