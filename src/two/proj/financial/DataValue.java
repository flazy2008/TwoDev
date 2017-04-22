package two.proj.financial;

import java.util.Comparator;

import two.common.CommonInfoByID;
import weaver.general.Util;


public class DataValue implements Comparator<DataValue>{
	CommonInfoByID common=new CommonInfoByID();
	String restr="";
	String v_sql="";
	private String tabletype="";
	private String index = "";
	private String subject = "";
	private String value1 = "";
	private String value2 = "";

	public DataValue() {
		// TODO Auto-generated constructor stub
	}

	public DataValue(String tabletype,String index, String subject, String value1, String value2) {
		this.tabletype=tabletype;
		this.index = index;
		this.subject = subject;
		this.value1 = value1;
		this.value2 = value2;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSubjectID() {
		v_sql=" select id from  db_accounttype where ctype='"+tabletype+"' and rowindex='"+index+"' ";
		restr=Util.null2String((String)common.executeSql(v_sql).get("id"));
		return restr;
	}
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public void setTabletype(String tabletype) {
		this.tabletype = tabletype;
	}

	public String getTabletype() {
		return tabletype;
	}
	@Override
	public String toString() {
		return this.tabletype.replace("0", "资产负债表").replace("1", "利润表").replace("2", "现金流量表")+":"+this.index + " : " + this.subject + " : " + this.value1 + " : "+ this.value2;
	}

	@Override
	public int compare(DataValue o1, DataValue o2) {
		if (Integer.parseInt(o1.getIndex()) > Integer.parseInt(o2.getIndex())) {
			return 1;
		} else {
			return -1;
		}
	}
}
