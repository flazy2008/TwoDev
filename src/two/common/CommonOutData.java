package two.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class CommonOutData extends BaseBean {
	private HashMap data = null;
	private String tableName=""; 

	public boolean loadData(String tablename, String id, String SqlWhere,String type) {
		boolean flag = false;
		this.data = new HashMap();
		RecordSet recordset = new RecordSet();
		String v_sql = "";
		if ("1".equals(type)) {
			SqlWhere = SqlWhere + " and id='" + id + "'";
		}
		v_sql = "select * from " + tablename + " where 1=1 " + SqlWhere;
		recordset.executeSql(v_sql);
		List list = Arrays.asList(recordset.getColumnName());
		if (recordset.next()) {
			Iterator ir = list.iterator();
			String fields = "";
			String values = "";
			while (ir.hasNext()) {
				fields = Util.null2String((String) ir.next()).toLowerCase();
				values = Util.null2String(recordset.getString(fields));
				data.put(fields, values);
			}
			flag = true;
		}
		return flag;
	}

	public boolean loadData(String tablename, String id) {
		this.tableName=tablename;
		return this.loadData(tablename, id, "","1");
	}
	public boolean loadData(String tablename, String SqlWhere, String empty) {
		this.tableName=tablename;
		return this.loadData(tablename, "", SqlWhere,"2");
	}

	public String getData(String fieldname) {
		return Util.null2String((String) data.get(fieldname));
	}
	public String getTableName() {
		return tableName;
	}

}
