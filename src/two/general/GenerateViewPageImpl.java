package two.general;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.conn.RecordSet;
import weaver.general.GCONST;
import weaver.general.Util;

public class GenerateViewPageImpl implements IGeneratePage {
	public int index = 0;
	GenerateCommon gener = new GenerateCommon();

	@Override
	public void CreateProc(String billid, BillBean billBean, String pageType) {
	}

	// 得到页面请求
	public Map<String, String> getOperationInfo(List<FieldInfoBean> beans,
			PageInfoBean pageBean) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	@Override
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean) {

	}

	public String getUrlArgs(String str) {
		String reStr = "";
		Matcher m = Pattern.compile("&.+?=").matcher(str);
		if (m.find()) {
			reStr = Util.null2String(m.group());
		} else if (str.lastIndexOf("?") > -1) {
			reStr = str.substring(str.lastIndexOf("?"));
		}
		return reStr;
	}
	/**
	 * 得到其它信息
	 */
	public Map<String, String> getOtherInfo(List<FieldInfoBean> beans) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer fieldStr = new StringBuffer();
		StringBuffer fieldRS = new StringBuffer();
		StringBuffer argsStr = new StringBuffer();
		String tmpStr1="\tString  v_XnameX =\"\";//XmarkX\n";
		String tmpStr2="\tv_XnameX = Util.null2String(RecordSet.getString(\"XnameX\"));//XmarkX\n";
		String tmpStr3 = "\tString p_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX\n";		
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {
				fieldStr.append(tmpStr1.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
				fieldRS.append(tmpStr2.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
			}
			if ("1".equals(formBean.getIsedit())) {
				argsStr.append(tmpStr3.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
			}
		}
		map.put("${argsStr}", "\n"+argsStr.toString());
		map.put("${fieldStr}", "\n"+fieldStr.toString());
		map.put("${fieldRS}", "\n"+fieldRS.toString());
		return map;
	}

	/**
	 * 得到表信息
	 */
	public StringBuffer getTRInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean) {
		StringBuffer buffer = new StringBuffer();
		GenerateFieldContent fieldContent = new GenerateFieldContent();
		String isview = formBean.getIsview();// 是否显示
		String valueStr = "";
		String Suffix = "";
		String trStart = "\n<TR>\n";
		String trEnd = "</TR><TR><TD class=Line colspan=X1X></TD></TR>\n";
		String fieldlabel = fieldBean.getFieldlabel();
		String tdcontent = fieldContent.getViewFieldInfo(fieldBean, formBean,
				pageBean, valueStr, Suffix).toString();// td内容
		String mainView = pageBean.getMainviewtype();
		int colspan = Util.getIntValue(mainView, 1);
		trEnd = trEnd.replace("X1X", colspan * 2 + "");// 结束
		if (this.index % colspan == 0) {
			buffer.append(trStart);
		}
		this.index++;
		buffer.append(gener.getTDInfo(fieldlabel, tdcontent));
		if (this.index % colspan == 0) {
			buffer.append(trEnd);
		}
		return buffer;
	}

	public StringBuffer getSQLWhereInfo(List<FieldInfoBean> beans) {
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean = null;
		String fieldname = "";
		String fieldlabel = "";
		String fieldType = "";// Html字段类型如：文本框
		String htmltype = "";// 单据的字段的type
		for (FieldInfoBean fieldBean : beans) {
			String tmpStr = "\tif(!X1X){//XmarkX\n\t\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n\t}\n";
			String tmpStr2 = "XnameX=\"+p_XnameX+\"";
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsedit())) {
				fieldname = fieldBean.getFieldname();// 字段名
				fieldlabel = fieldBean.getFieldlabel();// 显示名
				fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
				htmltype = fieldBean.getHtmltype();// 单据的字段的type
				if (fieldType.equals("1")) { // 单行文本框
					if (htmltype.equals("1")) { // 单行文本框中的文本
						tmpStr2 = "XnameX like '%\"+p_XnameX+\"%'";
					}
				} else if (fieldType.equals("2")) {
					tmpStr2 = "XnameX like '%\"+p_XnameX+\"%'";
				}
				tmpStr = tmpStr.replace("X1X", "\"\".equals(p_XnameX)").replace("XWHERE1X", tmpStr2);
				buffer.append(tmpStr.replace("XnameX", fieldname).replace(
						"XmarkX", fieldlabel));
			}
		}
		return buffer;
	}

	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean) {
		GenerateCommon gener = new GenerateCommon();
		Map<String, String> map = null;
		StringBuffer buffer = new StringBuffer();
		String str = null;
		RecordSet rs = new RecordSet();
		String v_sql = "";	
		String mainView = pageBean.getMainviewtype();
		String fileName = gener.getFilePath("modelPage", "View.jsp");
		String toFile = gener.getFilePath("GeneratePage", billBean.getTablename(), "View.jsp");

		String tableHeadStr = "";// 表头信息
		String trStart = "<TR class=Title><TH colSpan=X1X>标准</TH></TR>\n<TR class=Spacing><TD class=Line1 colSpan=X1X></TD></TR>\n";
		String trEnd = "</TR><TR><TD class=Line colspan=X1X></TD></TR>\n";		
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {// 编辑时显示
				buffer.append(this.getTRInfo(fieldBean, formBean, pageBean));
			}
		}
		int colspan = Util.getIntValue(pageBean.getMainviewtype(), 1) ;
		if (this.index % colspan != 0) {//最后一行加TR
			buffer.append(trEnd.replace("X1X", colspan * 2 + ""));
		}
		
		if ("1".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(30, 70, colspan) + "<TBODY>\n\n";
		} else if ("2".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(20, 30, colspan) + "<TBODY>\n\n";
		} // 得到表头信息
		tableHeadStr += trStart.replace("X1X", colspan * 2 + "");		
		
		str = gener.readFile(fileName);
		str = str.replace("${tablehead}", tableHeadStr);// 表格样式
		str = str.replace("${titlename}", billBean.getNamelabel());
		str = str.replace("${sqlwhere}", this.getSQLWhereInfo(beans));
		str = str.replace("${content}", buffer);//得到内容
		str = str.replace("${fla_billid}", billBean.getId());//表单ID
		str = str.replace("${XTableX}", billBean.getTablename());//表单名称
		map = this.getOtherInfo(beans);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		//str = str.replace("${browse}", gener.getBrowse(beans));// 浏览框
		gener.writeFile(toFile, str);
		v_sql = "update fla_bill set viewpage='X1X' where id="
				+ billBean.getId();
		v_sql = v_sql.replace("X1X", toFile.replace(GCONST.getRootPath() + "",
				File.separatorChar + ""));
		rs.executeSql(v_sql);
		return buffer;
	}

	@Override
	public void GeneratePage(String billid, BillBean billBean, String pageType) {
		List<FieldInfoBean> beans = new FieldInfoBean(billid, pageType, "0,1")
				.getList();
		PageInfoBean pageBean = new PageInfoBean(billid, pageType);
		this.GenerateMainInfo(beans, billBean, pageBean);// 生成主表信息
	}
}
