package two.share;

import java.util.LinkedList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkShareInfo {
	private String id = "";//��¼ID
	private String billid = "";// ��ID
	private String ctype = "";// ����  0 ��Ŀ��Ƭ,1 ����
	private String sharetype = "";// ��������
	private String seclevel = "";// ��ȫ����
	private String rolelevel = "";// ��ɫ����
	private String sharelevel = "";// ����ĵȼ�
	private String userid = "";// ��Ա
	private String deptid = "";// ����
	private String roleid = "";// ��ɫ
	private String alluser = "";// ������
	List<WorkShareInfo> list =null;
	WorkShareInfo shareInfo=null;
	RecordSet RecordSet = new RecordSet();
	String v_sql="";
	String SqlWhere="";
	
	public List<WorkShareInfo> getList(String p_ctype,String p_billid,String p_nodeid,String p_sharelevel) {
		list= new LinkedList<WorkShareInfo>();
		String  v_id ="";
		String  v_billid ="";//��ID
		String  v_ctype ="";//����
		String  v_sharetype ="";//��������
		String  v_seclevel ="";//��ȫ����
		String  v_rolelevel ="";//��ɫ����
		String  v_sharelevel ="";//����ĵȼ�
		String  v_userid ="";//��Ա
		String  v_deptid ="";//����
		String  v_roleid ="";//��ɫ
		String  v_alluser ="";//������
		SqlWhere=" and ctype='"+p_ctype+"' ";
		if("1".equals(p_ctype)){//�ڵ�ID
			SqlWhere=SqlWhere+" and nodeid='"+p_nodeid+"' ";
		}
		if(!"".equals(p_billid)){//��ID
			SqlWhere=SqlWhere+" and billid='"+p_billid+"' ";
		}
		if(!"".equals(p_sharelevel)){//Ȩ�޲�Ϊ��
			SqlWhere=SqlWhere+" and sharelevel in("+p_sharelevel+") ";
		}
		  v_sql=" select * from work_shareinfo where 1=1 "+SqlWhere+" order by id ";
		  RecordSet.executeSql(v_sql);
		  while(RecordSet.next()){
			v_id = Util.null2String(RecordSet.getString("id"));//��ͬ���
			v_billid = Util.null2String(RecordSet.getString("billid"));//��ID
			v_ctype = Util.null2String(RecordSet.getString("ctype"));//����
			v_sharetype = Util.null2String(RecordSet.getString("sharetype"));//��������
			v_seclevel = Util.null2String(RecordSet.getString("seclevel"));//��ȫ����
			v_rolelevel = Util.null2String(RecordSet.getString("rolelevel"));//��ɫ����
			v_sharelevel = Util.null2String(RecordSet.getString("sharelevel"));//����ĵȼ�
			v_userid = Util.null2String(RecordSet.getString("userid"));//��Ա
			v_deptid = Util.null2String(RecordSet.getString("deptid"));//����
			v_roleid = Util.null2String(RecordSet.getString("roleid"));//��ɫ
			v_alluser = Util.null2String(RecordSet.getString("alluser"));//������
			shareInfo=new WorkShareInfo(v_id, v_billid, v_ctype, v_sharetype, v_seclevel, v_rolelevel, v_sharelevel, v_userid, v_deptid, v_roleid, v_alluser);
			list.add(shareInfo);
		  }
		return list;
	}
	public WorkShareInfo() {
	}
	public WorkShareInfo(String id, String billid, String ctype,
			String sharetype, String seclevel, String rolelevel,
			String sharelevel, String userid, String deptid, String roleid,
			String alluser) {
		this.id = id;
		this.billid = billid;
		this.ctype = ctype;
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
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
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
