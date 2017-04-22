package two.commondwr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import two.common.CommonCode;
import two.common.CommonLog;
import two.common.CyclingIntermediary;
import two.work.hrm.RandomLogin;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;

/**
 * 通用DWR
 * 
 * @author Yang Faquan
 * @version [版本号, 2010-9-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CommonDWR {
	RecordSet rs = null;
	List list = null;
	List reList = null;
	ReturnValue returnValue = new ReturnValue();
	Map map = null;
	String fields = "";
	String values = "";
	String flag = "0";

	/**
	 * 通过DWR执行更新，删除SQL语句
	 * 
	 * @param sql
	 * @return 执行成功返回"1"，失败返回"0"
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String executeSql(String sql) {
		flag = "0";
		rs = new RecordSet();
		if (rs.executeSql(sql)) {
			flag = "1";
		}
		return flag;
	}

	/**
	 * 通过DWR执行更新，存储过程
	 * 
	 * @param sql
	 * @return 执行成功返回"1"，失败返回"0"
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String executeProc(String sql, String arg) {
		flag = "0";
		rs = new RecordSet();
		if (rs.executeProc(sql, arg)) {
			flag = "1";
		}
		return flag;
	}

	/**
	 * 生成业务表单编号
	 * 
	 * @param formId
	 * @param userId
	 */
	public String createCode(String formId, String userId) {
		return new CommonCode().createCode(formId, userId);
	}

	/**
	 * 生成客户编号
	 * 
	 * @param company分公司
	 * @param mainNature主题性质
	 * @return
	 */
	public String createCustomerCode(String company, String mainNature) {
		return new CommonCode().createCustomerCode(company, mainNature);
	}

	/**
	 * 记录删除操作
	 * 
	 * @param tableName
	 * @param ids
	 * @param user
	 * @return
	 */
	public void writeLog(String tableName, String ids, String userid,
			String clientip) {
		new CommonLog().writeLog(tableName, ids, userid, clientip);
	}

	/**
	 * 通用DWR查询并返回对象数据 主要用于通用DWR查询 相关数据功能,
	 * 在使用此方法之前请先建立好相应的表commondwrsql(id,dwrsql,...)
	 * 
	 * @param id
	 *            ,param sql中的参数形式如 select * from dual where ID=:ID
	 *            sql中的参数均不区分大小写
	 * @return 返回一个JSON对象数据保存于dataList中一 返回数据格式
	 *         如：{"dataList":[{"ID":"1","NAME":"flazy1"
	 *         },{"ID":"2","NAME":"flazy2"},....],"root":""}
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String getObjValueByTable(String id, String param) {
		return this.getObjValue(this.getFormSql(id, param));
	}

	/**
	 * 通用DWR查询并返回数组数据 主要用于通用DWR查询 相关数据功能,
	 * 在使用此方法之前请先建立好相应的表commondwrsql(id,dwrsql,...)
	 * 
	 * @param id
	 *            ,param 请使用JSON对像 sql中的参数形式如 select * from dual where ID=:ID
	 *            sql中的参数均不区分大小写
	 * @param sql
	 * @return 返回一个JSON对象数据保存于dataList中 返回数据格式
	 *         如：{"dataList":[{"ID":["1","2"],"NAME"
	 *         :["flazy1","flazy2"]}],"root":""}
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String getArrValueByTable(String id, String param) {
		return this.getArrValue(this.getFormSql(id, param));
	}

	/**
	 * 格式化参数 主要是将SQL语句里面的参数与传入的参数进行替换
	 * 
	 * @param id
	 * @param param
	 *            请使用JSON对像 sql中的参数形式如 select * from dual where ID=:ID
	 *            sql中的参数均不区分大小写
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	private String getFormSql(String id, String param) {
		String sql = "";
		rs = new RecordSet();
		param = (null == param) ? "" : param.toUpperCase();
		rs.executeSql("select * from commondwrsql where 1=1 and id='" + id
				+ "'");
		if (rs.next()) {
			sql = rs.getString(2);
			sql = (null == sql) ? "" : sql.toUpperCase();
		}
		if ((!"".equals(sql)) && (!"".equals(param))) {
			JSONObject JSON = JSONObject.fromObject(param);
			for (Iterator iter = JSON.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				key = (null == key) ? "" : ":" + key;
				String value = (String) JSON.get(key.replace(":", ""));
				value = (null == value) ? "" : value;
				sql = sql.replace(key, value);
			}
		}
		return sql;
	}

	/**
	 * 通用DWR查询并返回对象数据 主要用于通用DWR查询 相关数据功能
	 * 
	 * @param sql
	 * @return 返回一个JSON对象数据保存于dataList中一 返回数据格式
	 *         如：{"dataList":[{"ID":"1","NAME":"flazy1"
	 *         },{"ID":"2","NAME":"flazy2"},....],"root":""}
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String getObjValue(String sql) {
		rs = new RecordSet();
		reList = new ArrayList();
		rs.executeSql(sql);
		list = Arrays.asList(rs.getColumnName());
		Iterator ir = null;
		while (rs.next()) {
			ir = list.iterator();
			map = new HashMap();
			fields = "";
			values = "";
			while (ir.hasNext()) {
				fields = Util.null2String((String) ir.next()).toUpperCase();
				values = Util.null2String(rs.getString(fields));
				map.put(fields, values);
			}
			reList.add(map);
		}
		returnValue.setDataList(reList);
		JSONObject obj = JSONObject.fromObject(returnValue);
		// System.out.println("obj---->" + obj);
		return obj.toString();
	}

	/**
	 * 通用DWR查询并返回数组数据 主要用于通用DWR查询 相关数据功能
	 * 
	 * @param sql
	 * @return 返回一个JSON对象数据保存于dataList中 返回数据格式 如：{"dataList":[
	 *         'HYN','黄岩','HUANGYAN','HY'],['YFQ','杨柳','','YFQ'],"root":""}
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String getArrValue(String sql) {
		rs = new RecordSet();
		reList = new ArrayList();
		List listvalue = null;
		Iterator ir = null;
		rs.executeSql(sql);
		list = Arrays.asList(rs.getColumnName());
		while (rs.next()) {
			ir = list.iterator();
			listvalue = new ArrayList();
			fields = "";
			values = "";
			while (ir.hasNext()) {
				fields = Util.null2String((String) ir.next()).toUpperCase();
				values = Util.null2String(rs.getString(fields));
				listvalue.add(values);
			}
			reList.add(listvalue);
		}
		returnValue.setDataList(reList);
		JSONObject obj = JSONObject.fromObject(returnValue);
		// System.out.println("obj---->" + obj);
		return obj.toString();
	}

	/**
	 * 清除所有专家登录账号
	 */
	public void cleanAllExpert() {
		new RandomLogin().cleanAllExpert();
	}

	/**
	 * 循环选择评估机构2012/1/9
	 * 
	 * @author hongyun.xu
	 * @return 评估机构Id_评估机构名称
	 */
	public String cyclingIntermediary(String v_busid) {
		return new CyclingIntermediary().cyclingIntermediary(v_busid);
	}
}
