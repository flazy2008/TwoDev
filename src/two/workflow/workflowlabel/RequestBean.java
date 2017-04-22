package two.workflow.workflowlabel;

public class RequestBean {
	private String workflowid = "";
	private String nodeid = "";
	private String requestid = "";
	private String labelid = "";
	private String prjid = "";
	private String crmid = "";
	private String busid="";
	private String oper="";
	public RequestBean(String workflowid, String requestid, String labelid,
			String nodeid, String prjid, String crmid,String busid,String oper) {
		this.workflowid = workflowid;
		this.requestid = requestid;
		this.labelid = labelid;
		this.nodeid = nodeid;
		this.prjid = prjid;
		this.crmid = crmid;
		this.busid=busid;
		this.oper=oper;
	}
	public String getWorkflowid() {
		return workflowid;
	}

	public void setWorkflowid(String workflowid) {
		this.workflowid = workflowid;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getLabelid() {
		return labelid;
	}

	public void setLabelid(String labelid) {
		this.labelid = labelid;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getPrjid() {
		return prjid;
	}

	public void setPrjid(String prjid) {
		this.prjid = prjid;
	}

	public String getCrmid() {
		return crmid;
	}

	public void setCrmid(String crmid) {
		this.crmid = crmid;
	}

	public void setBusid(String busid) {
		this.busid = busid;
	}

	public String getBusid() {
		return busid;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getOper() {
		return oper;
	}

}
