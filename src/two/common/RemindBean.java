package two.common;

public class RemindBean {
	private String type;// ������:"0"Ϊ���̼�أ�"1"Ϊ�����
	private String processname;// ����/������
	private String monitornode;// ��ؽڵ㣨�ֶΣ�
	private String sqlcondition;// sql����
	private String togglecondition;// ��������
	private String days;// ʱ�䣨�죩
	private String content;// ��������
	private String way;// ���ѷ�ʽ
	private String remindgroup;// ������
	private String users = "";// ������Ա
	private String remark = "";// ��ע

	private String crmid = "";// �ͻ�id
	private String prjid = "";// ��Ŀid

	public RemindBean() {
	}

	public RemindBean(String type, String processname, String monitornode,
			String sqlcondition, String togglecondition, String days,
			String content, String way, String remindgroup, String users,
			String remark) {
		super();
		this.type = type;
		this.processname = processname;
		this.monitornode = monitornode;
		this.sqlcondition = sqlcondition;
		this.togglecondition = togglecondition;
		this.days = days;
		this.content = content;
		this.way = way;
		this.remindgroup = remindgroup;
		this.users = users;
		this.remark = remark;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	public String getMonitornode() {
		return monitornode;
	}

	public void setMonitornode(String monitornode) {
		this.monitornode = monitornode;
	}

	public String getSqlcondition() {
		return sqlcondition;
	}

	public void setSqlcondition(String sqlcondition) {
		this.sqlcondition = sqlcondition;
	}

	public String getTogglecondition() {
		return togglecondition;
	}

	public void setTogglecondition(String togglecondition) {
		this.togglecondition = togglecondition;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getRemindgroup() {
		return remindgroup;
	}

	public void setRemindgroup(String remindgroup) {
		this.remindgroup = remindgroup;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCrmid() {
		return crmid;
	}

	public void setCrmid(String crmid) {
		this.crmid = crmid;
	}

	public String getPrjid() {
		return prjid;
	}

	public void setPrjid(String prjid) {
		this.prjid = prjid;
	}

	@Override
	public String toString() {
		return "RemindBean [type=" + type + ", processname=" + processname
				+ ", monitornode=" + monitornode + ", sqlcondition="
				+ sqlcondition + ", togglecondition=" + togglecondition
				+ ", days=" + days + ", content=" + content + ", way=" + way
				+ ", remindgroup=" + remindgroup + ", users=" + users
				+ ", remark=" + remark + ", crmid=" + crmid + ", prjid="
				+ prjid + "]";
	}

}
