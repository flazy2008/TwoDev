package two.workflow.workflowlabel;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class LabelBean {
	private String id = "";// ID
	private String labelname = "";// 标签名称
	private String tablename = "";// 表名
	private String ctype = "";// 所属类别
	private String viewtype = "";// 显示类别
	private String url = "";// 连接
	private String isvalid = "";// 是否有效
	private String v_sql = "";
	RecordSet RecordSet = new RecordSet();

	public LabelBean(String id) {
		v_sql = " select * from work_label where 1=1 and id='"+id+"'";
		RecordSet.executeSql(v_sql);
		if (RecordSet.next()) {
			this.id = Util.null2String(RecordSet.getString("id"));// 合同编号
			this.labelname = Util.null2String(RecordSet.getString("labelname"));// 标签名称
			this.tablename = Util.null2String(RecordSet.getString("tablename"));// 表名
			this.ctype = Util.null2String(RecordSet.getString("ctype"));// 所属类别
			this.viewtype = Util.null2String(RecordSet.getString("viewtype"));// 显示类别
			this.url = Util.null2String(RecordSet.getString("url"));// 连接
			this.isvalid = Util.null2String(RecordSet.getString("isvalid"));// 是否有效
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
