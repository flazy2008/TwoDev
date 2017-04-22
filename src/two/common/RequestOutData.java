package two.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class RequestOutData extends BaseBean {
	private HashMap data = null;
	private String tableName = "";

	public boolean loadData(String requestid) {
		boolean flag = false;
		this.data = new HashMap();
		RecordSet recordset = new RecordSet();
		String v_sql = "";
		String tablename = "";
		v_sql = "select (select tablename from workflow_bill where id=formid) as tablename from workflow_base bas where exists (select 1 from workflow_Requestbase req where req.workflowid=bas.id and req.requestid='"
				+ requestid + "')";
		recordset.executeSql(v_sql);
		if (recordset.next()) {
			tablename = Util.null2String(recordset.getString("tablename"));
			this.tableName = tablename;
		} else {
			return false;
		}
		v_sql = "select * from " + tablename + " where requestid='" + requestid
				+ "' ";
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

	public String getData(String fieldname) {
		return Util.null2String((String) data.get(fieldname));
	}

	public String getData(String requestid, String fieldname) {
		if (this.loadData(requestid)) {
			return this.getData(fieldname);
		} else {
			return "";
		}
	}

	public String getTableName() {
		return tableName;
	}
}
