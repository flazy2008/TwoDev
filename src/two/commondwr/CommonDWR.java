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
 * ͨ��DWR
 * 
 * @author Yang Faquan
 * @version [�汾��, 2010-9-13]
 * @see [�����/����]
 * @since [��Ʒ/ģ��汾]
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
	 * ͨ��DWRִ�и��£�ɾ��SQL���
	 * 
	 * @param sql
	 * @return ִ�гɹ�����"1"��ʧ�ܷ���"0"
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
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
	 * ͨ��DWRִ�и��£��洢����
	 * 
	 * @param sql
	 * @return ִ�гɹ�����"1"��ʧ�ܷ���"0"
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
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
	 * ����ҵ������
	 * 
	 * @param formId
	 * @param userId
	 */
	public String createCode(String formId, String userId) {
		return new CommonCode().createCode(formId, userId);
	}

	/**
	 * ���ɿͻ����
	 * 
	 * @param company�ֹ�˾
	 * @param mainNature��������
	 * @return
	 */
	public String createCustomerCode(String company, String mainNature) {
		return new CommonCode().createCustomerCode(company, mainNature);
	}

	/**
	 * ��¼ɾ������
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
	 * ͨ��DWR��ѯ�����ض������� ��Ҫ����ͨ��DWR��ѯ ������ݹ���,
	 * ��ʹ�ô˷���֮ǰ���Ƚ�������Ӧ�ı�commondwrsql(id,dwrsql,...)
	 * 
	 * @param id
	 *            ,param sql�еĲ�����ʽ�� select * from dual where ID=:ID
	 *            sql�еĲ����������ִ�Сд
	 * @return ����һ��JSON�������ݱ�����dataList��һ �������ݸ�ʽ
	 *         �磺{"dataList":[{"ID":"1","NAME":"flazy1"
	 *         },{"ID":"2","NAME":"flazy2"},....],"root":""}
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public String getObjValueByTable(String id, String param) {
		return this.getObjValue(this.getFormSql(id, param));
	}

	/**
	 * ͨ��DWR��ѯ�������������� ��Ҫ����ͨ��DWR��ѯ ������ݹ���,
	 * ��ʹ�ô˷���֮ǰ���Ƚ�������Ӧ�ı�commondwrsql(id,dwrsql,...)
	 * 
	 * @param id
	 *            ,param ��ʹ��JSON���� sql�еĲ�����ʽ�� select * from dual where ID=:ID
	 *            sql�еĲ����������ִ�Сд
	 * @param sql
	 * @return ����һ��JSON�������ݱ�����dataList�� �������ݸ�ʽ
	 *         �磺{"dataList":[{"ID":["1","2"],"NAME"
	 *         :["flazy1","flazy2"]}],"root":""}
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public String getArrValueByTable(String id, String param) {
		return this.getArrValue(this.getFormSql(id, param));
	}

	/**
	 * ��ʽ������ ��Ҫ�ǽ�SQL�������Ĳ����봫��Ĳ��������滻
	 * 
	 * @param id
	 * @param param
	 *            ��ʹ��JSON���� sql�еĲ�����ʽ�� select * from dual where ID=:ID
	 *            sql�еĲ����������ִ�Сд
	 * @return [����˵��]
	 * 
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
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
	 * ͨ��DWR��ѯ�����ض������� ��Ҫ����ͨ��DWR��ѯ ������ݹ���
	 * 
	 * @param sql
	 * @return ����һ��JSON�������ݱ�����dataList��һ �������ݸ�ʽ
	 *         �磺{"dataList":[{"ID":"1","NAME":"flazy1"
	 *         },{"ID":"2","NAME":"flazy2"},....],"root":""}
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
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
	 * ͨ��DWR��ѯ�������������� ��Ҫ����ͨ��DWR��ѯ ������ݹ���
	 * 
	 * @param sql
	 * @return ����һ��JSON�������ݱ�����dataList�� �������ݸ�ʽ �磺{"dataList":[
	 *         'HYN','����','HUANGYAN','HY'],['YFQ','����','','YFQ'],"root":""}
	 * @return String [��������˵��]
	 * @exception throws [Υ������] [Υ��˵��]
	 * @see [�ࡢ��#��������#��Ա]
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
	 * �������ר�ҵ�¼�˺�
	 */
	public void cleanAllExpert() {
		new RandomLogin().cleanAllExpert();
	}

	/**
	 * ѭ��ѡ����������2012/1/9
	 * 
	 * @author hongyun.xu
	 * @return ��������Id_������������
	 */
	public String cyclingIntermediary(String v_busid) {
		return new CyclingIntermediary().cyclingIntermediary(v_busid);
	}
}
