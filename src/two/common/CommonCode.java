package two.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class CommonCode {
	public String createCode(String formId, String userId) {
		String v_code = "";
		String v_sql = "select formid from fla_numbering where formid="
				+ formId + " and isvalid=1";
		RecordSet recordSet = new RecordSet();
		recordSet.executeSql(v_sql);
		if (!recordSet.next()) {
			return "该表单还未设置编号规则，请  联系管理员！";
		}

		String v_letter_num = "";// 字母
		String v_movesn_num = "";// 流水号
		String v_years_num = "";// 年
		String v_months_num = "";// 月
		String v_days_num = "";// 日
		String v_string1_num = "";// 字符串一
		String v_string2_num = "";// 字符串二
		String v_string3_num = "";// 字符串三
		String v_string4_num = "";// 字符串四
		String v_string5_num = "";// 字符串五
		String v_string6_num = "";// 字符串六
		String v_string7_num = "";// 字符串七
		String v_deptid_num = "";// 部门

		String v_letter = "";// 字母
		String v_years = "";// 年
		String v_months = "";// 月
		String v_days = "";// 日
		String v_string1 = "";// 字符串一
		String v_string2 = "";// 字符串二
		String v_string3 = "";// 字符串三
		String v_string4 = "";// 字符串四
		String v_string5 = "";// 字符串五
		String v_string6 = "";// 字符串六
		String v_string7 = "";// 字符串七
		String v_deptid = "";// 部门

		String v_movesn = "";// 流水号位数
		String v_aloneYear = "";// 按年单独流水
		String v_aloneMonth = "";// 按月单独流水
		String v_aloneDay = "";// 按日单独流水
		String v_aloneDept = "";// 按部门单独流水

		Map numMap = new HashMap();

		v_sql = "select * from fla_numbering where formid=" + formId;
		RecordSet recordSet1 = new RecordSet();
		recordSet1.executeSql(v_sql);
		if (recordSet1.next()) {

			v_movesn_num = Util.null2String(recordSet1.getString("movesn_num"));// 字母
			v_letter_num = Util.null2String(recordSet1.getString("letter_num"));// 字母
			v_years_num = Util.null2String(recordSet1.getString("years_num"));// 年
			v_months_num = Util.null2String(recordSet1.getString("months_num"));// 月
			v_days_num = Util.null2String(recordSet1.getString("days_num"));// 日
			v_string1_num = Util.null2String(recordSet1
					.getString("string1_num"));// 字符串一
			v_string2_num = Util.null2String(recordSet1
					.getString("string2_num"));// 字符串二
			v_string3_num = Util.null2String(recordSet1
					.getString("string3_num"));// 字符串三
			v_string4_num = Util.null2String(recordSet1
					.getString("string4_num"));// 字符串四
			v_string5_num = Util.null2String(recordSet1
					.getString("string5_num"));// 字符串五
			v_string6_num = Util.null2String(recordSet1
					.getString("string6_num"));// 字符串六
			v_string7_num = Util.null2String(recordSet1
					.getString("string7_num"));// 字符串七
			v_deptid_num = Util.null2String(recordSet1.getString("deptid_num"));// 部门

			v_letter = Util.null2String(recordSet1.getString("letter"));// 字母
			v_years = Util.null2String(recordSet1.getString("years"));// 年
			v_months = Util.null2String(recordSet1.getString("months"));// 月
			v_days = Util.null2String(recordSet1.getString("days"));// 日
			v_string1 = Util.null2String(recordSet1.getString("string1"));// 字符串一
			v_string2 = Util.null2String(recordSet1.getString("string2"));// 字符串二
			v_string3 = Util.null2String(recordSet1.getString("string3"));// 字符串三
			v_string4 = Util.null2String(recordSet1.getString("string4"));// 字符串四
			v_string5 = Util.null2String(recordSet1.getString("string5"));// 字符串五
			v_string6 = Util.null2String(recordSet1.getString("string6"));// 字符串六
			v_string7 = Util.null2String(recordSet1.getString("string7"));// 字符串七
			v_deptid = Util.null2String(recordSet1.getString("deptid"));// 部门

			v_movesn = Util.null2String(recordSet1.getString("movesn"));// 流水号位数
			v_aloneYear = Util.null2String(recordSet1.getString("aloneYear"));// 按年单独流水
			v_aloneMonth = Util.null2String(recordSet1.getString("aloneMonth"));// 按月单独流水
			v_aloneDay = Util.null2String(recordSet1.getString("aloneDay"));// 按日单独流水
			v_aloneDept = Util.null2String(recordSet1.getString("aloneDept"));// 按部门单独流水

			String v1_aloneYear = (new Date().getYear() + 1900) + "";
			String v1_aloneMonth = (new Date().getMonth() + 1) + "";
			if (v1_aloneMonth.length() == 1) {
				v1_aloneMonth = "0" + v1_aloneMonth;
			}
			String v1_aloneDay = (new Date().getDate()) + "";
			if (v1_aloneDay.length() == 1) {
				v1_aloneDay = "0" + v1_aloneDay;
			}
			String v1_aloneDept = "系统管理员";
			v_sql = "select departmentcode from HrmDepartment  where id=(select departmentid from hrmresource where id="
					+ userId + ")";
			RecordSet recordSet2 = new RecordSet();
			recordSet2.executeSql(v_sql);
			if (recordSet2.next()) {
				v1_aloneDept = Util.null2String(recordSet2
						.getString("departmentcode"));
			}
			String move_sn = this.judgeCode(v_aloneYear, v_aloneMonth,
					v_aloneDay, v_aloneDept, v1_aloneDept, formId, v_movesn);

			if ("1".equals(v_years)) {
				v_years = v1_aloneYear;
			}
			if ("1".equals(v_months)) {
				v_months = v1_aloneMonth;
			}
			if ("1".equals(v_days)) {
				v_days = v1_aloneDay;
			}
			if ("1".equals(v_deptid)) {
				v_deptid = v1_aloneDept;
			}

			numMap.put(v_movesn_num, move_sn);
			numMap.put(v_letter_num, v_letter);
			numMap.put(v_years_num, v_years);
			numMap.put(v_months_num, v_months);
			numMap.put(v_days_num, v_days);
			numMap.put(v_string1_num, v_string1);
			numMap.put(v_string2_num, v_string2);
			numMap.put(v_string3_num, v_string3);
			numMap.put(v_string4_num, v_string4);
			numMap.put(v_string5_num, v_string5);
			numMap.put(v_string6_num, v_string6);
			numMap.put(v_string7_num, v_string7);
			numMap.put(v_deptid_num, v_deptid);

			String v_before = "";
			String v_after = "";

			String temp = "";
			for (int i = 1; i < 14; i++) {
				temp = (String) numMap.get(i + "");
				if (!"".equals(temp)) {
					if (v_movesn_num.equals(i + "")) {
						v_before = v_code;
					}
					if (i > Util.getIntValue(v_movesn_num)) {
						v_after += temp;
					}
					v_code += temp;
				}
			}

			v_sql = "insert into fla_numbering_code(code, move_sn, revision,before_,after_) values('"
					+ v_code
					+ "', '"
					+ move_sn
					+ "', 1,'"
					+ v_before
					+ "','"
					+ v_after + "')";
			RecordSet recordSet3 = new RecordSet();
			recordSet3.executeSql(v_sql);
		}
		return v_code;
	}

	/**
	 * 判断流水号
	 * 
	 * @param v_aloneYear按年单独流水
	 * @param v_aloneMonth按月单独流水
	 * @param v_aloneDay按天单独流水
	 * @param v_aloneDept按部门单独流水
	 * @param formId表单Id
	 * @param v_movesn流水号位数
	 * @return流水号
	 */
	public String judgeCode(String v_aloneYear, String v_aloneMonth,
			String v_aloneDay, String v_aloneDept, String v1_aloneDept,
			String formId, String v_movesn) {
		String move_sn = "";
		String v1_aloneYear = (new Date().getYear() + 1900) + "";
		String v1_aloneMonth = (new Date().getMonth() + 1) + "";
		if (v1_aloneMonth.length() == 1) {
			v1_aloneMonth = "0" + v1_aloneMonth;
		}
		String v1_aloneDay = (new Date().getDate()) + "";
		if (v1_aloneDay.length() == 1) {
			v1_aloneDay = "0" + v1_aloneDay;
		}

		String v_sqlWhere = "";
		if ("1".equals(v_aloneYear)) {
			v_sqlWhere += " and years=" + v1_aloneYear;
		} else {
			v_sqlWhere += " and years is null";
		}
		if ("1".equals(v_aloneMonth)) {
			v_sqlWhere += " and months=" + v1_aloneMonth;
		} else {
			v_sqlWhere += " and months is null";
		}
		if ("1".equals(v_aloneDay)) {
			v_sqlWhere += " and days=" + v1_aloneDay;
		} else {
			v_sqlWhere += " and days is null";
		}
		if ("1".equals(v_aloneDept)) {
			v_sqlWhere += " and deptid='" + v1_aloneDept + "'";
		} else {
			v_sqlWhere += " and deptid is null";
		}
		String v_sql = "select maxmovesn from fla_numbering_move_sn where formid="
				+ formId + v_sqlWhere;
		RecordSet recordSet2 = new RecordSet();
		recordSet2.executeSql(v_sql);
		if (recordSet2.next()) {
			move_sn = Util.null2String(recordSet2.getString("maxmovesn"));
			RecordSet recordSet4 = new RecordSet();
			v_sql = "update fla_numbering_move_sn set maxmovesn=maxmovesn+1 where 1=1 and formid="+formId
					+ v_sqlWhere ;
			recordSet4.executeSql(v_sql);
		} else {
			move_sn = "1";   
			String v_sql1 = "insert into fla_numbering_move_sn(formid,";
			String v_sql2 = "maxmovesn)values('" + formId + "',";
			if ("1".equals(v_aloneYear)) {
				v_sql1 += "years,";
				v_sql2 += "'" + v1_aloneYear + "',";
			}
			if ("1".equals(v_aloneMonth)) {
				v_sql1 += "months,";
				v_sql2 += "'" + v1_aloneMonth + "',";
			}
			if ("1".equals(v_aloneDay)) {
				v_sql1 += "days,";
				v_sql2 += "'" + v1_aloneDay + "',";
			}
			if ("1".equals(v_aloneDept)) {
				v_sql1 += "deptid,";
				v_sql2 += "'" + v1_aloneDept + "',";
			}
			v_sql = v_sql1.substring(0, v_sql1.trim().length()) + v_sql2 + "2)";
			RecordSet recordSet3 = new RecordSet();
			recordSet3.executeSql(v_sql);
		}
		return codeAddToMany(move_sn, Util.getIntValue(v_movesn));
	}

	/**
	 * 将流水号增加到指定位数
	 * 
	 * @return补齐位数的流水号
	 */
	public String codeAddToMany(String code, int many) {
		String result = "";
		int num = code.length();
		if (num > many) {
			result = "流水号位数超过设定值";
		} else if (num < many) {
			result = "0" + code;
			result = this.codeAddToMany(result, many);
		} else {
			result = code;
		}
		return result;
	}

	/**
	 * 生成修订号
	 * 
	 * @param code流水号
	 * @return带修订号的流水号
	 */
	public String createRevision(String code) {
		String v_code = "";
		if ("".equals(code)) {
			return "ERROR:无流水号传入，请联系管理员！";
		}
		String v_sql = "select move_sn,revision,before_,after_ from fla_numbering_code  where code='"
				+ code + "'";
		RecordSet recordSet = new RecordSet();
		if (recordSet.executeSql(v_sql)) {
			if (recordSet.next()) {
				String v_move_sn = Util.null2String(recordSet
						.getString("move_sn"));
				String v_revision = Util.null2String(recordSet
						.getString("revision"));
				String v_before = Util.null2String(recordSet
						.getString("before_"));
				String v_after = Util
						.null2String(recordSet.getString("after_"));
				v_code = v_before + v_move_sn + "-" + v_revision + v_after;
				RecordSet recordSet1 = new RecordSet();
				recordSet1
						.executeSql("update fla_numbering_code set  revision=revision+1 where  code='"
								+ code + "'");
			} else {
				return "没有对应流水号";
			}
		} else {
			return "ERROR:SQL出错，请联系管理员！";
		}
		return v_code;
	}

	/**
	 * 生成客户编号
	 * 
	 * @param company分公司
	 * @param mainNature主题性质
	 * @return
	 */
	public String createCustomerCode(String company, String mainNature) {
		String v_code = "";
		String v_companyCode = "@";// 分公司代号
		String v_mainNatureCode = "@";// 主体性质
		String v_serialnumber = "";// 流水号
		RecordSet recordSet = new RecordSet();
		RecordSet recordSet1 = new RecordSet();
		RecordSet recordSet2 = new RecordSet();
		RecordSet recordSet3 = new RecordSet();
		String v_sql = "select code from nd_filiale  where id=" + company;
		recordSet.executeSql(v_sql);// 查分公司代号
		if (recordSet.next()) {
			v_companyCode = Util.null2String(recordSet
					.getString("code"));
		}

		v_sql = "select description from CRM_CustomerDesc where id ='"
				+ mainNature + "'";
		recordSet1.executeSql(v_sql);// 查主体性质
		if (recordSet1.next()) {
			v_mainNatureCode = Util.null2String(recordSet1
					.getString("description"));
		}

		v_sql = "select serialnumber from nd_customercode where companycode='"
				+ v_companyCode + "' and mainnature='" + v_mainNatureCode + "'";
		recordSet2.executeSql(v_sql);
		if (recordSet2.next()) {
			v_serialnumber = Util.null2String(recordSet2
					.getString("serialnumber"));
			v_sql = "update nd_customercode set serialnumber = serialnumber+1 where companycode  = '"
					+ v_companyCode
					+ "' and mainnature   = '"
					+ v_mainNatureCode + "'";
			recordSet3.executeSql(v_sql);
		} else {
			v_serialnumber = "1";
			v_sql = " insert into nd_customercode(companycode, mainnature, serialnumber)values('"
					+ v_companyCode + "', '" + v_mainNatureCode + "','2')";
			recordSet3.executeSql(v_sql);
		}
		v_serialnumber = this.codeAddToMany(v_serialnumber, 4);
		v_code = v_companyCode + v_mainNatureCode + v_serialnumber;
		return v_code;
	}
	
	
	/**
	 * 生成项目编号
	 * @param crmid 客户Id
	 * @return项目编号
	 */
	public String createProjectCode(String crmid){
		String v_code = "";
		String v_crmCode = "@";//客户编号
		String v_industry = "@";//行业类型
		String v_serialnumber = "";//流水号
		
		RecordSet recordSet = new RecordSet();
		RecordSet recordSet1 = new RecordSet();
		RecordSet recordSet2 = new RecordSet();
		RecordSet recordSet3 = new RecordSet();
		
		String v_sql = "select crmcode , sector from crm_customerinfo  where id="+crmid;
		recordSet.executeSql(v_sql);//查询客户编号和行业类型
		if(recordSet.next()){
			v_crmCode = Util.null2String(recordSet.getString("crmcode"));
			v_industry = Util.null2String(recordSet.getString("sector"));
			v_sql = " select description from Crm_Sectorinfo  where id ='"+v_industry+"'";
			recordSet3.executeSql(v_sql);
			if(recordSet3.next()){
				v_industry = Util.null2String(recordSet3.getString("description"));
			}else{
				v_industry = "@";
			}
		}
		
		v_sql = "select serialnumber from  nd_projectCode where crmcode = '"+v_crmCode+"' and industry = '"+v_industry+"'";
		recordSet1.executeSql(v_sql);
		if(recordSet1.next()){
			v_serialnumber = Util.null2String(recordSet1.getString("serialnumber"));
			v_sql = "update nd_projectcode set serialnumber = serialnumber+1 where crmcode = '"+v_crmCode+"' and  industry = '"+v_industry+"'";
			recordSet2.executeSql(v_sql);
		}else{
			v_serialnumber = "1";
			v_sql = "insert into nd_projectcode(crmcode, industry, serialnumber)values('"+v_crmCode+"', '"+v_industry+"', '2')";
			recordSet2.executeSql(v_sql);
		}
		v_serialnumber = this.codeAddToMany(v_serialnumber, 2);
		v_code = v_crmCode + v_industry + v_serialnumber;
		return v_code;
	}
	
	
}
