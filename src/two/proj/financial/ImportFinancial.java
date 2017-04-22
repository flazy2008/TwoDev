package two.proj.financial;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import two.common.CommonUtil;
import weaver.conn.RecordSetTrans;

public class ImportFinancial {
	List<DataValue> dataValues = null;
	List<DataValue> errors = new LinkedList<DataValue>();
	boolean flag=true;
	RecordSetTrans trans=new RecordSetTrans();
	public List<DataValue> writeFinancial(String filename,String mainid){
		trans.setAutoCommit(false);
		if(!"".equals(mainid)&&!"".equals(filename)){
			this.writeBalanceAnalysis(filename, mainid);//资产负债表
			this.writeProfitSheet(filename, mainid);//利润表
			this.writeCashFlow(filename, mainid);//现金流量表
		}
		if(errors.size()>0){
			flag=false;
			trans.rollback();
		}else{
			trans.commit();
		}
		return errors;
	}
	//资产负债表
	public void writeBalanceAnalysis(String filename,String mainid) {
		dataValues = new LinkedList<DataValue>();
		String[][] contents = CommonUtil.readExcel(filename, 0);
		DataValue dataValue = null;
		for (int i = 4; i < 68; i++) {
			dataValue = new DataValue("0",contents[i][1], contents[i][0],
					contents[i][2], contents[i][3]);
			dataValues.add(dataValue);
			dataValue = new DataValue("0",contents[i][5], contents[i][4],
					contents[i][6], contents[i][7]);
			dataValues.add(dataValue);
		}
		Collections.sort(dataValues,dataValue);
		for (DataValue tmp : dataValues) {
			this.writeValues(tmp, "nd_balancesheet", mainid,errors);
		}
	}
	//利润表
	public void writeProfitSheet(String filename,String mainid) {
		dataValues = new LinkedList<DataValue>();
		String[][] contents = CommonUtil.readExcel(filename, 1);
		DataValue dataValue = null;
		for (int i = 4; i < 32; i++) {
			dataValue = new DataValue("1",contents[i][1], contents[i][0],
					contents[i][2], contents[i][3]);
			dataValues.add(dataValue);
			dataValue = new DataValue("1",contents[i][5], contents[i][4],
					contents[i][6], contents[i][7]);
			dataValues.add(dataValue);
		}
		Collections.sort(dataValues,dataValue);
		for (DataValue tmp : dataValues) {
			this.writeValues(tmp, "nd_profitsheet", mainid,errors);
		}
	}
	//现金流量表
	public void writeCashFlow(String filename,String mainid) {
		dataValues = new LinkedList<DataValue>();
		String[][] contents = CommonUtil.readExcel(filename, 2);
		DataValue dataValue = null;
		for (int i = 4; i < 33; i++) {
			dataValue = new DataValue("2",contents[i][1], contents[i][0],
					contents[i][2], contents[i][3]);
			dataValues.add(dataValue);
			dataValue = new DataValue("2",contents[i][5], contents[i][4],
					contents[i][6], contents[i][7]);
			dataValues.add(dataValue);
		}
		Collections.sort(dataValues,dataValue);
		for (DataValue tmp : dataValues) {
			this.writeValues(tmp, "nd_cashflow", mainid,errors);
		}
	}
	

	public void writeValues(DataValue dataValue,String tablename,String mainid,List<DataValue> errors) {
		String v_sql="insert into "+tablename+"(mainid, subjectid, rowindex, value1, value2)values('"+mainid+"', '"+dataValue.getSubjectID()+"', '"+dataValue.getIndex()+"', '"+dataValue.getValue1()+"', '"+dataValue.getValue2()+"')";
		try {
			if(!trans.executeSql(v_sql)){
				errors.add(dataValue);
			}
		} catch (Exception e) {
			errors.add(dataValue);
			e.printStackTrace();
		}
	}
}
