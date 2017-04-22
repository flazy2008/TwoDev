package two.share;

import java.util.LinkedList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkFieldShareInfo {
	private String id = "";//记录ID
	private String billid ="";//表单ID
	private String fieldid ="";//字段ID
	private String fieldname ="";//字段名称
	private String ctype ="";//类型
	private String workflowid ="";//流程ID
	private String nodeid ="";//节点ID
	private String sharetype ="";//共享类型
	private String seclevel ="";//安全级别
	private String rolelevel ="";//角色级别
	private String sharelevel ="";//共享的等级
	private String userid ="";//人员
	private String deptid ="";//部门
	private String roleid ="";//角色
	private String alluser ="";//所有人
	
	List<WorkFieldShareInfo> list =null;
	WorkFieldShareInfo shareInfo=null;
	RecordSet RecordSet = new RecordSet();
	String v_sql="";
	String SqlWhere="";
	
	public List<WorkFieldShareInfo> getList(String p_ctype,String p_billid,String p_nodeid,String p_sharelevel) {
		list= new LinkedList<WorkFieldShareInfo>();
		String  v_id ="";
		String  v_billid ="";//表单ID
		String  v_fieldid ="";//字段ID
		String  v_fieldname ="";//字段名称
		String  v_ctype ="";//类型
		String  v_workflowid ="";//流程ID
		String  v_nodeid ="";//节点ID
		String  v_sharetype ="";//共享类型
		String  v_seclevel ="";//安全级别
		String  v_rolelevel ="";//角色级别
		String  v_sharelevel ="";//共享的等级
		String  v_userid ="";//人员
		String  v_deptid ="";//部门
		String  v_roleid ="";//角色
		String  v_alluser ="";//所有人
		
		SqlWhere=" and ctype='"+p_ctype+"' ";
		if("1".equals(p_ctype)){//节点ID
			SqlWhere=SqlWhere+" and nodeid='"+p_nodeid+"' ";
		}
		if(!"".equals(p_billid)){//表单ID
			SqlWhere=SqlWhere+" and billid='"+p_billid+"' ";
		}
		if(!"".equals(p_sharelevel)){//权限不为空
			SqlWhere=SqlWhere+" and sharelevel in("+p_sharelevel+") ";
		}
		  v_sql=" select * from work_fieldshareinfo where 1=1 "+SqlWhere+" order by id ";
		  RecordSet.executeSql(v_sql);
		  while(RecordSet.next()){ 
				v_id = Util.null2String(RecordSet.getString("id"));//合同编号
				v_billid = Util.null2String(RecordSet.getString("billid"));//表单ID
				v_fieldid = Util.null2String(RecordSet.getString("fieldid"));//字段ID
				v_fieldname = Util.null2String(RecordSet.getString("fieldname")).toLowerCase();//字段名称
				v_ctype = Util.null2String(RecordSet.getString("ctype"));//类型
				v_workflowid = Util.null2String(RecordSet.getString("workflowid"));//流程ID
				v_nodeid = Util.null2String(RecordSet.getString("nodeid"));//节点ID
				v_sharetype = Util.null2String(RecordSet.getString("sharetype"));//共享类型
				v_seclevel = Util.null2String(RecordSet.getString("seclevel"));//安全级别
				v_rolelevel = Util.null2String(RecordSet.getString("rolelevel"));//角色级别
				v_sharelevel = Util.null2String(RecordSet.getString("sharelevel"));//共享的等级
				v_userid = Util.null2String(RecordSet.getString("userid"));//人员
				v_deptid = Util.null2String(RecordSet.getString("deptid"));//部门
				v_roleid = Util.null2String(RecordSet.getString("roleid"));//角色
				v_alluser = Util.null2String(RecordSet.getString("alluser"));//所有人
				v_fieldname="'"+v_fieldname+"'";
				
			shareInfo=new WorkFieldShareInfo(v_id, v_billid, v_fieldid, v_fieldname, v_ctype, v_workflowid, v_nodeid, v_sharetype, v_seclevel, v_rolelevel, v_sharelevel, v_userid, v_deptid, v_roleid, v_alluser);
			list.add(shareInfo);
		  }
		return list;
	}
	public WorkFieldShareInfo() {
	}
	
	
	public WorkFieldShareInfo(String id, String billid, String fieldid,
			String fieldname, String ctype, String workflowid, String nodeid,
			String sharetype, String seclevel, String rolelevel,
			String sharelevel, String userid, String deptid, String roleid,
			String alluser) {
		this.id = id;
		this.billid = billid;
		this.fieldid = fieldid;
		this.fieldname = fieldname;
		this.ctype = ctype;
		this.workflowid = workflowid;
		this.nodeid = nodeid;
		this.sharetype = sharetype;
		this.seclevel = seclevel;
		this.rolelevel = rolelevel;
		this.sharelevel = sharelevel;
		this.userid = userid;
		this.deptid = deptid;
		this.roleid = roleid;
		this.alluser = alluser;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBillid() {
		return billid;
	}
	public void setBillid(String billid) {
		this.billid = billid;
	}
	public String getFieldid() {
		return fieldid;
	}
	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getWorkflowid() {
		return workflowid;
	}
	public void setWorkflowid(String workflowid) {
		this.workflowid = workflowid;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getSharetype() {
		return sharetype;
	}
	public void setSharetype(String sharetype) {
		this.sharetype = sharetype;
	}
	public String getSeclevel() {
		return seclevel;
	}
	public void setSeclevel(String seclevel) {
		this.seclevel = seclevel;
	}
	public String getRolelevel() {
		return rolelevel;
	}
	public void setRolelevel(String rolelevel) {
		this.rolelevel = rolelevel;
	}
	public String getSharelevel() {
		return sharelevel;
	}
	public void setSharelevel(String sharelevel) {
		this.sharelevel = sharelevel;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDeptid() {
		return deptid;
	}
	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getAlluser() {
		return alluser;
	}
	public void setAlluser(String alluser) {
		this.alluser = alluser;
	}


	
}
