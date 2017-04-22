package two.common;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import two.util.DateUtil;
import weaver.conn.RecordSet;
import weaver.crm.Maint.CustomerInfoComInfo;
import weaver.docs.docs.DocComInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;
import weaver.proj.Maint.ProjectInfoComInfo;

public class CommonInfoByID extends BaseBean {
	DepartmentComInfo deptInfo = null;// 部门信息
	ProjectInfoComInfo projInfo = null;// 项目信息
	CustomerInfoComInfo crmInfo = null;// 客户信息
	ResourceComInfo hrmInfo = null;// 人力资源
	DocComInfo docInfo = null;// 文档
	SubCompanyComInfo orgInfo=null;//机构

	RecordSet rs = null;
	String v_sql = "";

	private void initObjectInfo(String str) {
		try {
			if ("dept".equals(str) && deptInfo == null) {
				deptInfo = new DepartmentComInfo();// 部门信息
			} else if ("proj".equals(str) && projInfo == null) {
				projInfo = new ProjectInfoComInfo();// 部门信息
			} else if ("crm".equals(str) && crmInfo == null) {
				crmInfo = new CustomerInfoComInfo();// 客户信息
			} else if ("hrm".equals(str) && hrmInfo == null) {
				hrmInfo = new ResourceComInfo();// 人力资源
			} else if ("doc".equals(str) && docInfo == null) {
				docInfo = new DocComInfo();// 文档
			} else if ("org".equals(str) && orgInfo == null) {
				orgInfo = new SubCompanyComInfo();// 机构
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据项目得到客户id
	 */
	public String LookCrmIDByProjID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("proj");// 初始化项目信息
			return projInfo.getProjectInfodesc(id);
		}
		return "";
	}

	/**
	 * 根据项目编号查询项目名稱： <功能详细描述>
	 */
	public String LookNameByProjID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("proj");// 初始化项目信息
			return projInfo.getProjectInfoname(id);
		}
		return "";
	}

	/**
	 * 根据部门编号查询部门名稱： <功能详细描述>
	 */
	public String LookNameByDeptID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("dept");// 初始化部门信息
			return deptInfo.getDepartmentname(id);
		}
		return "";
	}
	
	/**
	 * 根据机构编号查询机构名稱： <功能详细描述>
	 */
	public String LookNameByOrgID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("org");// 初始化部门信息
			return orgInfo.getSubCompanyname(id);
		}
		return "";
	}
	
	

	/**
	 * 根据客户的编号查询客户姓名：投資主體 <功能详细描述>
	 */
	public String LookNameByCRMID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("crm");// 初始化客户信息
			return crmInfo.getCustomerInfoname(id);
		}
		return "";
	}

	/**
	 * 根据客户的编号查询客户姓名：投資主體 带URL<功能详细描述>
	 */
	public String LookNameByCRMID(String id, String url) {
		String code = "";
		if (!"".equals(id)) {
			this.initObjectInfo("crm");// 初始化客户信息
			code = "<a href=\"/CRM/data/ViewCustomer.jsp?CustomerID=" + id
					+ "\">" + crmInfo.getCustomerInfoname(id) + "</a>";
		}
		return code;
	}

	/**
	 * 根据員工的编号查询員工
	 */
	public String LookNameByHrmID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("hrm");// 初始化人员信息
			String value = hrmInfo.getLastname(id);
			if ("1".equals(id) && "".equals(value)) {
				value = "系统管理员";
			}
			return value;
		}
		return "";
	}

	/**
	 * 根据公司人员编号查询多个人员的值
	 * 
	 */
	public String LookNameByMoreHrmID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			id = "," + id + ",";
			id = id.replace(",,", ",");
			v_sql = "SELECT distinct lastname FROM hrmresource WHERE instr('"+ id + "',','||id||',')>0";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				restr += "," + rs.getString("lastname");
			}
			restr = restr.startsWith(",") ? restr.substring(1) : restr;
		}
		return restr;
	}

	/**
	 * 根据文档编号得到文档名称
	 */
	public String LookNameByDocID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select imagefilename from imagefile where imagefileid='"
					+ id + "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("imagefilename"));
			}
			if ("".equals(restr)) {
				this.initObjectInfo("doc");// 初始化文档信息
				restr = docInfo.getDocname(id);
			}
		}
		return restr;
	}

	/**
	 * 根据行业ID得到行业名称
	 */
	public String LookNameBySectorID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,fullname as cname from  crm_sectorinfo where id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据业务ID得到业务名称
	 */
	public String LookNameByBusID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id, buscode as cname from  nd_business where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据银行ID得到银行名称
	 */
	public String LookNameByCoopID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_coopbank where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据业务品种ID得到业务品种名称
	 */
	public String LookNameByYWPZID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_businessproduct where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据合同类型ID得到合同类型名称
	 */
	public String LookNameByContTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_contractTypes where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据资料类型ID得到资料类型名称
	 */
	public String LookNameByDatumTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_datumtype where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据反担保措施分类类型ID得到反担保措施分类类型名称
	 */
	public String LookNameByReverseTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id, typeName  as cname from  db_reverseType where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据项目ID得到业务集
	 */
	public HashMap LookBusMapByProjID(String id) {
		HashMap<String, String> busMap = new HashMap<String, String>();
		if (!"".equals(id)) {
			String v_key = "", v_value = "";// 键值，显示值
			v_sql = "select id,cname from  nd_business where prjid='" + id
					+ "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				v_key = Util.null2String(rs.getString("id"));
				v_value = Util.null2String(rs.getString("cname"));
				busMap.put(v_key, v_value);
			}
		}
		return busMap;
	}

	/**
	 * 根据评委人员编号查询多评委的名称
	 */
	public String LookNameByJudgesID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			id = "," + id + ",";
			id = id.replace(",,", ",");
			v_sql = "SELECT distinct cname FROM nd_ExpertTeam WHERE instr('"
					+ id + "',','||id||',')>0";
			// //System.out.println(sql);
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				restr += "," + rs.getString("cname");
			}
			restr = restr.startsWith(",") ? restr.substring(1) : restr;
		}
		return restr;
	}

	/**
	 * 根据select值得到名称
	 */
	public String LookNameBySelectID(String selectvalue, String fieldid) {
		String restr = "";
		if ("-1".equals(selectvalue)) {
			restr = "";
		} else if (!"".equals(fieldid)) {
			v_sql = "select selectname as cname from  fla_SelectItem where fieldid='"
					+ fieldid + "' and selectvalue='" + selectvalue + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 通过id得到档案类型
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByArchivesTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select archivestype from db_archivestype where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("archivestype"));
			}
		}
		return restr;
	}

	/**
	 * 通过id得到档案柜号
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByStockCabinetID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select stockcabinet from db_stockcabinet where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("stockcabinet"));
			}
		}
		return restr;
	}

	/**
	 * 通过id得到档案层号
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByStockLayerID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select stocklayer from db_stocklayer where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("stocklayer"));
			}
		}
		return restr;
	}

	/**
	 * 通过id得到档案名称
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByArchivesID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select cname from nd_archives where id='" + id + "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 通过id得到评审会委员名称
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByMeetJudgesID(String Judges, String userids) {
		String restr = "";
		if (!"-1".equals(Judges)) {
			restr = "外部评委:" + this.LookNameByJudgesID(Judges);
		} else if (!"-1".equals(userids)) {
			restr = "内部评委:" + this.LookNameByMoreHrmID(userids);
		}
		return restr;
	}

	/**
	 * 通过id得到会议编码
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByMeetCodeID(String id, String type) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select meetcode as cname from nd_appraisalmeet where id='"
					+ id + "'";
			if ("2".equals(type)) {
				v_sql = "select meetcode as cname from nd_businessmeet where id='"
						+ id + "'";
			}
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * 根据requestid值得到具体表名称
	 */
	public String LookTableByRequestID(String requestid) {
		String restr = "";
		if (!"".equals(requestid)) {
			v_sql = "select (select tablename from workflow_bill where id=formid) as tablename from workflow_base bas where exists (select 1 from workflow_Requestbase req where req.workflowid=bas.id and req.requestid='"
					+ requestid + "')";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("tablename"));
			}
		}
		return restr;
	}

	/**
	 * 得到日期
	 */
	public String getDateInfo(String date, String fmt) {
		if (!"".equals(date)) {
			SimpleDateFormat myFmt = null;
			if ("YMDHMS".equalsIgnoreCase(fmt)) {
				myFmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			} else {
				myFmt = new SimpleDateFormat("yyyy-MM-dd");
			}
			try {
				Date da = myFmt.parse(date);
				date = myFmt.format(da);
			} catch (Exception e) {
				// System.out.println(e.getMessage());
			}
			return date;
		}
		return "";
	}

	/**
	 * 返回千分位
	 * */
	public String NumberFormat(String str, String align) {
		if (!"".equals(str)) {
			str = str.replace(",", "");
			NumberFormat format = NumberFormat.getInstance();
			str = format.format(Double.parseDouble(str));
			if ("".equals(align)) {
				return str;
			} else if (",L,LEFT,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"left\">" + str + "</div>";
			} else if (",R,RIGHT,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"right\">" + str + "</div>";
			} else if (",C,CENTER,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"center\">" + str + "</div>";
			}
			return str;
		}
		return "";
	}

	/**
	 * 执行SQL语句并返回一条结果返回格式如：字段$值,： <功能详细描述>
	 */
	public Map executeSql(String v_sql) {
		Map map = new HashMap();
		String fields = "";
		String values = "";
		if (!"".equals(v_sql)) {
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				List list = Arrays.asList(rs.getColumnName());
				for (int i = 0; i < list.size(); i++) {
					fields = Util.null2String((String) list.get(i));
					values = Util.null2String(rs.getString(fields));
					map.put(fields.toLowerCase(), values);
				}
			}
		}
		return map;
	}

	/**
	 * 执行SQL语句没有返回： <功能详细描述>
	 */
	public boolean executeSql(String str, String oper) {
		boolean flag = false;
		if (!"".equals(str)) {
			rs = new RecordSet();
			if (rs.executeSql(str)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 传入SQL返回表头
	 * 
	 * @param sql
	 * @return
	 */
	public List getFields(String sql) {
		if ("".equals(sql)) {
			return new LinkedList();
		}
		rs = new RecordSet();
		rs.executeSql(sql.toUpperCase());
		List list = Arrays.asList(rs.getColumnName());
		return list;
	}

	/**
	 * 传入SQL返回执行结果
	 * 
	 * @param sql
	 * @return
	 */
	public List getObjValue(String sql) {
		Map map = null;
		String fields = "";
		String values = "";
		rs = new RecordSet();
		List reList = new ArrayList();
		rs.executeSql(sql.toUpperCase());
		List list = Arrays.asList(rs.getColumnName());
		while (rs.next()) {
			Iterator ir = list.iterator();
			map = new HashMap();
			fields = "";
			values = "";
			while (ir.hasNext()) {
				fields = (String) ir.next();
				values = rs.getString(fields);
				values = (values == null) ? "" : values;
				map.put(fields, values);
			}
			reList.add(map);
		}
		return reList;
	}

	public String getFlag(String flag) {
		if ("".equals(flag) || "1".equals(flag)) {
			return "true";
		} else {
			return "false";
		}
	}

	public Object invoke(String className, String methodName, Object... objects) {
		Object object = new Object();
		try {
			Class clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					Class[] clazz1 = m.getParameterTypes();
					object = m.invoke(obj, objects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * @param tablename
	 *            表名
	 * @param field
	 *            需更新字段
	 */
	public void getLevelId(String tablename, String field) {
		rs = new RecordSet();
		RecordSet uprs = new RecordSet();
		String id = "", pid = "";
		String v_sql = "select * from " + tablename + " where dr=0 ";
		String v_cids = "";
		rs.execute(v_sql);
		while (rs.next()) {
			id = rs.getString("id");
			pid = rs.getString("pid");
			v_cids = this.getPid(tablename, pid);
			v_cids = v_cids + id;
			v_sql = "update " + tablename + " set " + field + "=',"
					+ v_cids.toString() + ",' where id='" + id + "'";
			uprs.execute(v_sql);
		}
	}

	private String getPid(String tablename, String pid) {
		String v_cids = "";
		RecordSet rs1 = new RecordSet();
		String v_sql = "select * from " + tablename + " where dr=0 and id="
				+ pid;
		String id = "";
		rs1.execute(v_sql);
		if (rs1.next()) {
			id = rs1.getString("id");
			pid = rs1.getString("pid");
			v_cids = id + "," + v_cids;
			v_cids = getPid(tablename, pid) + v_cids;
		}
		return v_cids;
	}

	/**
	 * 通过单位id得到单位名称
	 * 
	 * @param uid
	 * @return
	 */
	public String LookNameByUtilID(String uid) {
		String result = "";
		String v_sql = " select unitname from LgcAssetUnit   where id = '"
				+ uid + "' ";
		RecordSet rs = new RecordSet();
		rs.executeSql(v_sql);
		if (rs.next()) {
			result = Util.null2String(rs.getString("unitname"));
		}
		return result;
	}

	public String LookImgByType(String str) {
		String result = "";
		if ("1".equals(str)) {
			result = super.getPropValue("work_dev", "effectYes");
		} else if ("0".equals(str) || "".equals(str)) {
			result = super.getPropValue("work_dev", "effectNo");
		}
		return result;
	}
	
	public String LookOperBySearch(String str) {
		String result = "";
		result+="<a class=\"oper\" href=\"javascript:theDelRow('"+str+"');\">删除</a> ";
		result+="<a class=\"oper\" href=\"javascript:theEditRow('"+str+"');\">编辑</a> ";
		result+="<a class=\"viewoper\" href=\"javascript:theViewRow('"+str+"');\">查看</a>";
		return result;
	}
	public ArrayList LookOperBySearch(String strID,String OtherInfo) {
		 ArrayList returnList = new ArrayList();
		 String[] otherInfos = Util.TokenizerString2(OtherInfo, "_");
		 String userid=Util.null2String(otherInfos[0]);//用户ID
		 String billid=Util.null2String(otherInfos[1]);//表单ID 如:1:search
		 String canEdit=Util.null2String(otherInfos[2]);//是否有编辑权限
		 returnList.add(canEdit);
		 returnList.add(canEdit);
		 returnList.add("true");
		return returnList;
	}
	/**
	 * 得到当前日期
	 * */
	public String getNowDate() {
		return DateUtil.getNowDate("1");
	}

	/**
	 * 得到当前日期
	 * */
	public String getNowDate(String str) {
		// 1 年月日，2 年月，3年，4月,5 年月日时分秒
		return DateUtil.getNowDate(str);
	}

	/**
	 * 将字符串日期'yyyy-MM-dd'转为大写日期，如：'2011-11-21'转换为'二O一一年十一月二十一日'
	 * 
	 * @param date
	 * @return
	 */
	public String stringToUppderDate(String date) {
		return DateUtil.stringToUppderDate(date);
	}

	/**
	 * 字符日期转为"xxxx年xx月xx日"
	 * 
	 * @param date
	 * @return
	 */
	public static String stringToStringDate(String date) {
		return DateUtil.stringToStringDate(date);
	}
	/**
	 * 删除前后逗号
	 * @param date
	 * @return
	 */	
	public String delComma(String str){
		String v_tmpStr=Util.null2String(str);
		v_tmpStr=v_tmpStr.replace(",,", ",");//去除连续逗号
		v_tmpStr=v_tmpStr.startsWith(",")?v_tmpStr.substring(1):v_tmpStr;//清除前逗号
		v_tmpStr=v_tmpStr.endsWith(",")?v_tmpStr.substring(0,v_tmpStr.length()-1):v_tmpStr;//清除后逗号
		return v_tmpStr;
	}
	public String getBusType(String busid) {
		if ("".equals(busid)) {
			return "";
		}
		String type = "";
		String temp = "";
		String sql = "select ywpz from nd_business where id='" + busid + "'";
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		if (rs.next()) {
			temp = Util.null2String(rs.getString("ywpz"));
		}
		if ("5".equals(temp)) {
			type = "委托";
		} else {
			type = "担保";
		}
		return type;
	}

	/**
	 * 通过当前用户查询专家号
	 * @param userid
	 * @return 
	 */
	public String LookJudidByUserid(String userid) {
		String judid = "";
		String sql = "select id from nd_expertteam where loginid = '"+userid+"'";
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		if (rs.next()) {
			judid = Util.null2String(rs.getString("id"));
		}
		return judid;
	}

}
