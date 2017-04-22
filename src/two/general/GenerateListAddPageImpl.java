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

public class GenerateListAddPageImpl implements IGeneratePage {
	public int index = 0;
	GenerateCommon gener = new GenerateCommon();
	//得到页面请求
	public Map<String, String> getOperationInfo(List<FieldInfoBean> beans,
			PageInfoBean pageBean) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer fieldStr = new StringBuffer();
		StringBuffer mainrequest=new StringBuffer();
		StringBuffer procPara=new StringBuffer();
		boolean flag=true;
		String tmpStr1="\tString  v_XnameX =\"\";//XmarkX\n";
		String tmpStr2 = "\t\t\tv_XnameX = Util.null2String(request.getParameter(\"XnameX_\"+v_tmp))XCommaX;//XmarkX\n";
		String tmpStr3="\t\t\tProcPara += flag + v_XnameX;//XmarkX\n";
		FieldViewBean formBean =null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			fieldStr.append(tmpStr1.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
			if ("1".equals(formBean.getIsview())) {
				mainrequest.append(gener.getRepComma(tmpStr2, fieldBean).replace("XnameX", fieldBean.getFieldname()).replace(
								"XmarkX", fieldBean.getFieldlabel()));
				if(flag==true){//第一行
					procPara.append(tmpStr3.replace("+= flag +", " =").replace("XnameX", fieldBean.getFieldname()).replace(
							"XmarkX", fieldBean.getFieldlabel()));
					flag=false;
					continue;
				}
				procPara.append(tmpStr3.replace("XnameX", fieldBean.getFieldname()).replace(
						"XmarkX", fieldBean.getFieldlabel()));
			}
		}
		map.put("${fieldStr}", "\n"+fieldStr.toString());
		map.put("${Add_mainrequest}", "\n"+mainrequest.toString());
		map.put("${Add_procPara}", "\n"+procPara.toString());
		return map;
	}
	@Override
	public void CreateProc(String billid, BillBean billBean, String pageType) {
		RecordSet  rs=new RecordSet();
		rs.setChecksql(false);
		boolean isoracle="oracle".equals(rs.getDBType())?true:false;
		List<FieldInfoBean> beans = new FieldInfoBean(billid, pageType, "0,1").getList();
		String v_sql="CREATE PROCEDURE fla_XtableX_linsert( X1X @flag int output, @msg varchar(80) output) as" +
				" \ninsert into XtableX(X11X) \nvalues(X12X)" +
				" \nselect max(id) as id from XtableX ";
		String v1_sql=" drop PROCEDURE fla_XtableX_linsert ";
		String args="";
		String fields="";
		String args2="";
		String fieldname="";
		if(isoracle){
			v_sql="CREATE OR REPLACE PROCEDURE fla_XtableX_linsert (X1X flag out integer , msg out varchar2, thecursor IN OUT cursor_define.weavercursor )  AS " +
					" \nbegin \ninsert into XtableX(X11X) \nvalues(X12X); \nopen thecursor for \n\tselect max(id) as id from XtableX; \nend;";
		}
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean=fieldBean.getNodeBean();
			if("1".equals(formBean.getIsview())){
				fieldname=fieldBean.getFieldname();
				if(isoracle){
					args=args+"v_"+fieldname+" varchar2,";
					fields=fields+","+fieldname;
					args2=args2+",v_"+fieldname;
				}else{
					args=args+"@"+fieldname+" varchar(200),";
					fields=fields+","+fieldname;
					args2=args2+",@"+fieldname;
				}
			}
		}
		fields=fields.substring(1);
		args2=args2.substring(1);
		v_sql=v_sql.replace("XtableX", billBean.getTablename());
		v_sql=v_sql.replace("X1X", args);
		v_sql=v_sql.replace("X11X", fields);
		v_sql=v_sql.replace("X12X", args2);
		if(!isoracle){
			rs.executeSql(v1_sql.replace("XtableX", billBean.getTablename()));
		}
		rs.executeSql(v_sql);
	}

	@Override
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean) {
		String str = null;
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer initStr = new StringBuffer();// 其它初始化信息
		StringBuffer buffer = new StringBuffer();
		String mainView = pageBean.getMainviewtype();
		String fileName = gener.getFilePath("modelPage","List_Operation.jsp");
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_Operation.jsp");
		str = gener.readFile(fileName);
		map = this.getOperationInfo(beans, pageBean);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		str=str.replace("${Add_procName}", "fla_"+billBean.getTablename()+"_linsert");
		str=str.replace("${XtableX}", billBean.getTablename());
		str=str.replace("${LAdd_log}", gener.getWriteLogInfo(beans));//写日志
		gener.writeFile(toFile, str);
		
	}
	@Override
	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean) {
		int index = 0;
		Map<String, String> map = null;
		String str = null;
		RecordSet rs = new RecordSet();
		String v_sql = "";
		StringBuffer initStr = new StringBuffer();// 其它初始化信息
		String tableHeadStr = "";// 表头信息
		StringBuffer buffer = new StringBuffer();
		String mainView = pageBean.getMainviewtype();
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			buffer.append(this.getTRInfo(fieldBean, formBean, pageBean));
		}
		String fileName =gener.getFilePath("modelPage","List_Add.jsp");
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_Add.jsp");		
		str = gener.readFile(fileName);
		initStr.append(buffer);
		str = str.replace("${titlename}", billBean.getNamelabel());
		// str=str.replace("${initsql}", "\n"+this.getInitSql(beans, pageBean,
		// billBean));
		str = str.replace("${content}", initStr);
		str = str.replace("${fla_billid}", billBean.getId());
		str = str.replace("${fla_billid}", billBean.getId());//表单ID
		str = str.replace("${XTableX}", billBean.getTablename());//表单名称

		map = this.getOtherInfo(beans);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		str = str.replace("${browse}", gener.getBrowse(beans));// 浏览框
		gener.writeFile(toFile, str);
		v_sql = "update fla_bill set createpage='X1X' where id="
				+ billBean.getId();
		v_sql = v_sql.replace("X1X", toFile.replace(GCONST.getRootPath() + "",
				File.separatorChar + ""));
		rs.executeSql(v_sql);
		return buffer;
	}

	public Map<String, String> getOtherInfo(List<FieldInfoBean> beans) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer th = new StringBuffer();
		StringBuffer tdspan = new StringBuffer();
		StringBuffer defbuff = new StringBuffer();
		String tmpStr = "\tString v_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX\n";
		String sumname = "";
		int rownum = 1;
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {
				if (!"".equals(fieldBean.getDefaultvalue())) {
					defbuff.append(tmpStr.replace("XnameX", fieldBean.getDefaultvalue()).replace("XmarkX", fieldBean.getFieldlabel()));
				}
				th.append("\t<th>" + fieldBean.getFieldlabel() + "</th>\n");
				if ("1".equals(fieldBean.getFieldhtmltype())
						&& !"1".equals(fieldBean.getHtmltype())) {
					if(!"2".equals(fieldBean.getHtmltype())){//为整数时不求和
						sumname += "," + fieldBean.getFieldname();
					}
					tdspan.append("\t\t<td><span class=\"fontred\" id=\"Span"
							+ fieldBean.getFieldname() + "\"></span></td>\n");
				} else {
					tdspan.append("\t\t<td></td>\n");
				}
				rownum++;
			}
		}
		map.put("${th}", th.toString());
		sumname=sumname.length()>0?sumname.substring(1):"";
		map.put("${sumname}", sumname);
		map.put("${tdspan}", tdspan.toString());
		map.put("${colnumber}", rownum + "");
		map.put("${defvalue}", "\n"+defbuff.toString());		
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
		String Suffix = "_1";
		if ("1".equals(isview)) {
			buffer.append(trStart);
			buffer.append("\t\t"
					+ fieldContent.getListAddFieldInfo(fieldBean, formBean,
							pageBean, valueStr, Suffix));
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
