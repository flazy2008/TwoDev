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
	private String tablefield = "";// 字段名
	private String newval = "";// 新值
	private String oldval = "";// 旧值
	private String newview = "";// 新显示值
	private String oldview = "";// 旧显示值
	private String tablename = "";// 表名
	private String tableid = "";// 表ID
	private String v_id = "";//

	private User user = null;// 用户

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
	 * 对表单数据删除的日志记录，调用时直接调该方法
	 * 
	 * @param tableName
	 *            表名
	 * @param ids
	 *            待删除表Id的集合的String形式
	 * @param userid
	 *            用户Id
	 * @param clientip
	 *            操作者Ip地址
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
					orgid, department, "删除", clientip);
		}
	}

	/**
	 * Excel导入数据记录
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
	 * Excel导入数据记录
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
		intoDatabasa(tableName, tableId, "整条记录", "", "", v_updateDate, userid,
				orgid, department, "导入", clientip);
	}

	/**
	 * 对表单数据的修改或者增加的记录日志，调用时先调AddLogModel方法
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
		String v_tableid = "";// 表ID
		String v_tablename = "";// 表名
		User v_user = null;// 用户
		String v_id = "";
		String v_type = "";

		for (CommonLog modelBean : list) {
			v_type = "修改";
			v_newval = modelBean.getNewval();
			v_newview = modelBean.getNewview();
			v_oldval = modelBean.getOldval();
			v_oldview = modelBean.getOldview();
			v_newview = "".equals(v_newview) ? v_newval : v_newview;// 显示值为""时显示实际值
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
				v_type = "新增";
			}
			intoDatabasa(v_tablename, v_tableid, v_tablefield, v_oldview,
					v_newview, v_updateDate, v_userId, v_orgid, v_department,
					v_type, v_clientip);
		}
	}

	/**
	 * 将数据写进数据库
	 * 
	 * @param v_tablename
	 *            表名
	 * @param v_tableid
	 *            表Id
	 * @param v_tablefield
	 *            属性
	 * @param v_oldview
	 *            修改前值
	 * @param v_newview
	 *            修改后值
	 * @param v_updateDate
	 *            操作时间
	 * @param v_userId
	 *            操作者Id
	 * @param v_orgid
	 *            分部Id
	 * @param v_department
	 *            部门Id
	 * @param v_type
	 *            操作类型（增加、修改、删除）
	 * @param v_clientip
	 *            操作者Ip地址
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
		return rs.executeSql(v_sql);// 写入数据
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
