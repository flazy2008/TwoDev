package two.workflow.workflowlabel;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class LabelBean {
	private String id = "";// ID
	private String labelname = "";// ��ǩ����
	private String tablename = "";// ����
	private String ctype = "";// �������
	private String viewtype = "";// ��ʾ���
	private String url = "";// ����
	private String isvalid = "";// �Ƿ���Ч
	private String v_sql = "";
	RecordSet RecordSet = new RecordSet();

	public LabelBean(String id) {
		v_sql = " select * from work_label where 1=1 and id='"+id+"'";
		RecordSet.executeSql(v_sql);
		if (RecordSet.next()) {
			this.id = Util.null2String(RecordSet.getString("id"));// ��ͬ���
			this.labelname = Util.null2String(RecordSet.getString("labelname"));// ��ǩ����
			this.tablename = Util.null2String(RecordSet.getString("tablename"));// ����
			this.ctype = Util.null2String(RecordSet.getString("ctype"));// �������
			this.viewtype = Util.null2String(RecordSet.getString("viewtype"));// ��ʾ���
			this.url = Util.null2String(RecordSet.getString("url"));// ����
			this.isvalid = Util.null2String(RecordSet.getString("isvalid"));// �Ƿ���Ч
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabelname() {
		return labelname;
	}

	public void setLabelname(String labelname) {
		this.labelname = labelname;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getViewtype() {
		return viewtype;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(String isvalid) {
		this.isvalid = isvalid;
	}

}
