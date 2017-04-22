package two.proj.financial;

import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;

public class FinancialData extends BaseBean {
	private String tabletype = "";
	private String subject = "";
	String v_sql = "";
	private double value1 = 0.0;
	private double value2 = 0.0;
	Map<String, FinancialData> data0 = new HashMap<String, FinancialData>();
	RecordSet rs = new RecordSet();
	FinancialData data=null;
	public FinancialData() {
		// TODO Auto-generated constructor stub
	}
	// 资产负债表//利润表//现金流量表

	public void initData(String v_mainid) {
		v_sql = "select * from nd_balancesheet where mainid ='"+v_mainid+"' and dr=0";
		rs.executeSql(v_sql);
		while (rs.next()) {
			data0.put(rs.getString("subjectid"), new FinancialData("0", rs.getString("subjectid"), rs.getDouble("value1"), rs.getDouble("value2")));
		}
		v_sql = "select * from nd_profitsheet where  mainid ='"+v_mainid+"' and dr=0";
		rs.executeSql(v_sql);
		while (rs.next()) {
			data0.put(rs.getString("subjectid"), new FinancialData("1", rs.getString("subjectid"), rs.getDouble("value1"), rs.getDouble("value2")));
		}
		v_sql = "select * from nd_cashflow where   mainid ='"+v_mainid+"' and dr=0";
		rs.executeSql(v_sql);
		while (rs.next()) {
			data0.put(rs.getString("subjectid"), new FinancialData("2",rs.getString("subjectid"), rs.getDouble("value1"), rs.getDouble("value2")));
		}
	}

	public FinancialData(String tabletype, String subject, double value1,double value2) {
		this.tabletype = tabletype;
		this.subject = subject;
		this.value1 = value1;
		this.value2 = value2;
	}

	public String getTabletype() {
		return tabletype;
	}

	public void setTabletype(String tabletype) {
		this.tabletype = tabletype;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public double getValue1() {
		return value1;
	}

	public double getValue1(String subject) {
		data=data0.get(subject);
		if(data!=null){
			return data.getValue1();
		}else{
			return 0.0d;
		}
	}

	public void setValue1(double value1) {
		this.value1 = value1;
	}

	public double getValue2() {
		return value2;
	}

	public double getValue2(String subject) {
		data=data0.get(subject);
		if(data!=null){
			return data.getValue2();
		}else{
			return 0.0d;
		}
	}

	public void setValue2(double value2) {
		this.value2 = value2;
	}

}
