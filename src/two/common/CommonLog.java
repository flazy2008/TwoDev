package two.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;

public class CommonLog {
	private List<CommonLog> list = new ArrayList<CommonLog>();//
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String tablefield = "";// �ֶ���
	private String newval = "";// ��ֵ
	private String oldval = "";// ��ֵ
	private String newview = "";// ����ʾֵ
	private String oldview = "";// ����ʾֵ
	private String tablename = "";// ����
	private String tableid = "";// ��ID
	private String v_id = "";//

	private User user = null;// �û�

	public CommonLog() {
	}

	public CommonLog(String tablefield, String newval, String oldval,
			String newview, String oldview, String tablename, String tableid,
			String v_id, User user) {
		super();
		this.tablefield = tablefield;
		this.newval = newval;
		this.oldval = oldval;
		this.newview = newview;
		this.oldview = oldview;
		this.tablename = tablename;
		this.tableid = tableid;
		this.v_id = v_id;
		this.user = user;
	}

	public void AddLogModel(String tablefield, String newval, String oldval,
			String newview, String oldview, String tablename, String tableid,
			String v_id, User user) throws Throwable {

		if (!oldval.equals(newval)) {
			CommonLog bean = new CommonLog(tablefield, newval, oldval, newview,
					oldview, tablename, tableid, v_id, user);
			list.add(bean);
		}
	}

	/**
	 * �Ա�����ɾ������־��¼������ʱֱ�ӵ��÷���
	 * 
	 * @param tableName
	 *            ����
	 * @param ids
	 *            ��ɾ����Id�ļ��ϵ�String��ʽ
	 * @param userid
	 *            �û�Id
	 * @param clientip
	 *            ������Ip��ַ
	 */
	public void writeLog(String tableName, String ids, String userid,
			String clientip) {
		List<String> idList = Util.TokenizerString(ids, ",");

		RecordSet rs = new RecordSet();
		String v_updateDate = sdf.format(new Date());
		String orgid = "";
		String department = "";

		rs.executeSql("select departmentid,subcompanyid1 from hrmresource where id="
				+ userid);
		if (rs.next()) {
			department = rs.getString("departmentid");
			orgid = rs.getString("subcompanyid1");
		}

		for (String id : idList) {
			intoDatabasa(tableName, id, "", "", "", v_updateDate, userid,
					orgid, department, "ɾ��", clientip);
		}
	}

	/**
	 * Excel�������ݼ�¼
	 * @param tableName
	 * @param tableId
	 * @param user
	 */
	public void writeLog(String tablename, String tableid,String tablefield,String oldview,String newview,String type, User user) {
		String updateDate = sdf.format(new Date());
		String orgid = user.getUserSubCompany1() + "";
		String department = user.getUserDepartment() + "";
		String userid = user.getUID() + "";
		String clientip = user.getLoginip();
		this.intoDatabasa(tablename, tableid, tablefield, oldview, newview, updateDate, userid,orgid, department, type, clientip);
	}
	/**
	 * Excel�������ݼ�¼
	 * @param tableName
	 * @param tableId
	 * @param user
	 */
	public void writeLog(String tableName, String tableId, User user) {
		String v_updateDate = sdf.format(new Date());
		String orgid = user.getUserSubCompany1() + "";
		String department = user.getUserDepartment() + "";
		String userid = user.getUID() + "";
		String clientip = user.getLoginip();
		intoDatabasa(tableName, tableId, "������¼", "", "", v_updateDate, userid,
				orgid, department, "����", clientip);
	}

	/**
	 * �Ա����ݵ��޸Ļ������ӵļ�¼��־������ʱ�ȵ�AddLogModel����
	 */
	public void writeLog() {
		String v_updateDate = sdf.format(new Date());
		String v_userId = "";
		String v_department = "";
		String v_clientip = "";
		String v_orgid = "";
		String v_newval = "";
		String v_newview = "";
		String v_oldval = "";
		String v_oldview = "";
		String v_tablefield = "";//
		String v_tableid = "";// ��ID
		String v_tablename = "";// ����
		User v_user = null;// �û�
		String v_id = "";
		String v_type = "";

		for (CommonLog modelBean : list) {
			v_type = "�޸�";
			v_newval = modelBean.getNewval();
			v_newview = modelBean.getNewview();
			v_oldval = modelBean.getOldval();
			v_oldview = modelBean.getOldview();
			v_newview = "".equals(v_newview) ? v_newval : v_newview;// ��ʾֵΪ""ʱ��ʾʵ��ֵ
			v_oldview = "".equals(v_oldview) ? v_oldval : v_oldview;

			v_tablefield = modelBean.getTablefield();
			v_tableid = modelBean.getTableid();
			v_tablename = modelBean.getTablename();
			v_user = modelBean.getUser();
			v_id = modelBean.getV_id();
			v_department = v_user.getUserDepartment() + "";
			v_clientip = v_user.getLoginip();
			v_orgid = v_user.getUserSubCompany1() + "";
			v_userId = v_user.getUID() + "";
			if ("".equals(v_id)) {
				v_type = "����";
			}
			intoDatabasa(v_tablename, v_tableid, v_tablefield, v_oldview,
					v_newview, v_updateDate, v_userId, v_orgid, v_department,
					v_type, v_clientip);
		}
	}

	/**
	 * ������д�����ݿ�
	 * 
	 * @param v_tablename
	 *            ����
	 * @param v_tableid
	 *            ��Id
	 * @param v_tablefield
	 *            ����
	 * @param v_oldview
	 *            �޸�ǰֵ
	 * @param v_newview
	 *            �޸ĺ�ֵ
	 * @param v_updateDate
	 *            ����ʱ��
	 * @param v_userId
	 *            ������Id
	 * @param v_orgid
	 *            �ֲ�Id
	 * @param v_department
	 *            ����Id
	 * @param v_type
	 *            �������ͣ����ӡ��޸ġ�ɾ����
	 * @param v_clientip
	 *            ������Ip��ַ
	 * @return
	 */
	public boolean intoDatabasa(String v_tablename, String v_tableid,
			String v_tablefield, String v_oldview, String v_newview,
			String v_updateDate, String v_userId, String v_orgid,
			String v_department, String v_type, String v_clientip) {
		RecordSet rs = new RecordSet();
		String v_sql = "insert into fla_CommonLog(tablename,tableid,tablefield,oldval,newval,updatedate,loginnum,orgid,department,type,clientip) values"
				+ "('"
				+ v_tablename
				+ "','"
				+ v_tableid
				+ "','"
				+ v_tablefield
				+ "','"
				+ v_oldview
				+ "','"
				+ v_newview
				+ "','"
				+ v_updateDate
				+ "','"
				+ v_userId
				+ "','"
				+ v_orgid
				+ "','"
				+ v_department
				+ "','" + v_type + "','" + v_clientip + "')";
		return rs.executeSql(v_sql);// д������
	}

	public List<CommonLog> getList() {
		return list;
	}

	public String getNewval() {
		return newval;
	}

	public String getNewview() {
		return newview;
	}

	public String getOldval() {
		return oldval;
	}

	public String getOldview() {
		return oldview;
	}

	public String getTablefield() {
		return tablefield;
	}

	public String getTableid() {
		return tableid;
	}

	public String getTablename() {
		return tablename;
	}

	public User getUser() {
		return user;
	}

	public String getV_id() {
		return v_id;
	}

	public void setList(List<CommonLog> list) {
		this.list = list;
	}

	public void setNewval(String newval) {
		this.newval = newval;
	}

	public void setNewview(String newview) {
		this.newview = newview;
	}

	public void setOldval(String oldval) {
		this.oldval = oldval;
	}

	public void setOldview(String oldview) {
		this.oldview = oldview;
	}

	public void setTablefield(String tablefield) {
		this.tablefield = tablefield;
	}

	public void setTableid(String tableid) {
		this.tableid = tableid;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setV_id(String v_id) {
		this.v_id = v_id;
	}

}
