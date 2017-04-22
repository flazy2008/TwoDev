package two.workflow.workflowlabel;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkModuleBean {
	private String id = "";// ID
	private String pid = "";// �ϼ�ID
	private String labelname = "";// ��ǩ����
	private String tablename = "";// ����
	private String url = "";// ����
	private String orders = "";// ��ʾ˳��
	private String isleaf = "";// �Ƿ�Ҷ�ӽڵ�
	private String isvalid = "";// �Ƿ���Ч
	String v_sql = "";
	RecordSet RecordSet = new RecordSet();

	public WorkModuleBean(String id) {
		v_sql = " select * from work_module where 1=1 and id='" + id + "'";
		RecordSet.executeSql(v_sql);
		if (RecordSet.next()) {
			this.id = Util.null2String(RecordSet.getString("id"));// ��ͬ���
			this.pid = Util.null2String(RecordSet.getString("pid"));// �ϼ�ID
			this.labelname = Util.null2String(RecordSet.getString("labelname"));// ��ǩ����
			this.tablename = Util.null2String(RecordSet.getString("tablename"));// ����
			this.url = Util.null2String(RecordSet.getString("url"));// ����
			this.orders = Util.null2String(RecordSet.getString("orders"));// ��ʾ˳��
			this.isleaf = Util.null2String(RecordSet.getString("isleaf"));// �Ƿ�Ҷ�ӽڵ�
			this.isvalid = Util.null2String(RecordSet.getString("isvalid"));// �Ƿ���Ч
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
