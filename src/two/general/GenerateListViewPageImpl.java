package two.general;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

public class GenerateListViewPageImpl implements IGeneratePage {
	public int index = 0;
	GenerateCommon gener = new GenerateCommon();
	
	@Override
	public void CreateProc(String billid, BillBean billBean, String pageType) {
	}

	@Override
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean) {
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
	@Override
	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean) {
		GenerateCommon gener = new GenerateCommon();
		Map<String, String> map = null;
		String str = null;
		RecordSet rs = new RecordSet();
		String v_sql = "";
		StringBuffer initStr = new StringBuffer();// 其它初始化信息
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			buffer.append(this.getTRInfo(fieldBean, formBean, pageBean));
		}
		String fileName =gener.getFilePath("modelPage","List_View.jsp");
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_View.jsp");
		str = gener.readFile(fileName);
		initStr.append(buffer);
		str = str.replace("${titlename}", billBean.getNamelabel());
		str = str.replace("${sqlwhere}", this.getSQLWhereInfo(beans));
		str = str.replace("${fla_billid}", billBean.getId());//表单ID
		str = str.replace("${XTableX}", billBean.getTablename());//表单名称
		str = str.replace("${content}", initStr);
		map = this.getOtherInfo(beans);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		str = str.replace("${browse}", gener.getBrowse(beans));//浏览框
		gener.writeFile(toFile, str);
		v_sql = "update fla_bill set viewpage='X1X' where id="+ billBean.getId();
		v_sql = v_sql.replace("X1X", toFile.replace(GCONST.getRootPath() + "",File.separatorChar + ""));
		rs.executeSql(v_sql);
		return buffer;
	}

	public Map<String, String> getOtherInfo(List<FieldInfoBean> beans) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer th = new StringBuffer();
		StringBuffer tdspan = new StringBuffer();
		StringBuffer fieldStr = new StringBuffer();
		StringBuffer fieldRS = new StringBuffer();
		StringBuffer argsStr = new StringBuffer();
		String tmpStr1="\tString  v_XnameX =\"\";//XmarkX\n";
		String tmpStr2="\tv_XnameX = Util.null2String(RecordSet.getString(\"XnameX\"));//XmarkX\n";	
		String tmpStr3 = "\tString p_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX\n";
		String sumname="";
		int rownum = 1;
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {
				th.append("\t<th>" + fieldBean.getFieldlabel() + "</th>\n");
				fieldStr.append(tmpStr1.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
				fieldRS.append(tmpStr2.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
				if ("1".equals(fieldBean.getFieldhtmltype())&& !"1".equals(fieldBean.getHtmltype())) {
					if(!"2".equals(fieldBean.getHtmltype())){//为整数时不求和
					sumname+=","+fieldBean.getFieldname();
					}
					tdspan.append("\t\t<td><span class=\"fontred\" id=\"SpanSum"+ fieldBean.getFieldname() + "\"></span></td>\n");
				} else {
					tdspan.append("\t\t<td></td>\n");
				}
				rownum++;
			}
			if ("1".equals(formBean.getIsedit())) {
				argsStr.append(tmpStr3.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
			}
		}
		map.put("${fieldStr}", "\n"+fieldStr.toString());
		map.put("${argsStr}", "\n"+argsStr.toString());		
		map.put("${fieldRS}", "\n"+fieldRS.toString());
		map.put("${th}", th.toString());
		sumname=sumname.length()>0?sumname.substring(1):"";
		map.put("${sumname}", sumname);
		map.put("${tdspan}", tdspan.toString());
		map.put("${colnumber}", rownum + "");
		return map;
	}

	public StringBuffer getTRInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean) {
		StringBuffer buffer = new StringBuffer();
		GenerateFieldContent fieldContent = new GenerateFieldContent();
		String isview = formBean.getIsview();// 是否显示
		String trStart = "\t\t<TD class=Field>\n";
		String trEnd = "\t\t</TD>\n";
		String valueStr = "";
		if ("1".equals(isview)) {
			buffer.append(trStart);
			buffer.append("\t\t"+ fieldContent.getViewFieldInfo(fieldBean, formBean, pageBean, valueStr, ""));
			buffer.append(trEnd);
		}
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
