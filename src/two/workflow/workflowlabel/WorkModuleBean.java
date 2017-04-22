package two.workflow.workflowlabel;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkModuleBean {
	private String id = "";// ID
	private String pid = "";// 上级ID
	private String labelname = "";// 标签名称
	private String tablename = "";// 表名
	private String url = "";// 连接
	private String orders = "";// 显示顺序
	private String isleaf = "";// 是否叶子节点
	private String isvalid = "";// 是否有效
	String v_sql = "";
	RecordSet RecordSet = new RecordSet();

	public WorkModuleBean(String id) {
		v_sql = " select * from work_module where 1=1 and id='" + id + "'";
		RecordSet.executeSql(v_sql);
		if (RecordSet.next()) {
			this.id = Util.null2String(RecordSet.getString("id"));// 合同编号
			this.pid = Util.null2String(RecordSet.getString("pid"));// 上级ID
			this.labelname = Util.null2String(RecordSet.getString("labelname"));// 标签名称
			this.tablename = Util.null2String(RecordSet.getString("tablename"));// 表名
			this.url = Util.null2String(RecordSet.getString("url"));// 连接
			this.orders = Util.null2String(RecordSet.getString("orders"));// 显示顺序
			this.isleaf = Util.null2String(RecordSet.getString("isleaf"));// 是否叶子节点
			this.isvalid = Util.null2String(RecordSet.getString("isvalid"));// 是否有效
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public String getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}

	public String getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(String isvalid) {
		this.isvalid = isvalid;
	}

}
