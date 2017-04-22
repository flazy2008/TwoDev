package two.general.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.general.GCONST;
import weaver.workflow.field.BrowserComInfo;

public class GenerateUtil {
	//得到主表请求信息
	public String getMainRequestStr(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			tmpStr=HtmlUtil.OPER_MAINREQUEST;
			if(isList==true){tmpStr=HtmlUtil.OPER_DETAILREQUEST.replace("\n\t\tv1_","\n\t\tv_");}//列表显示
			
			tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
			if("1".equals(billBean.getHasfileup())){
				tmpStr=tmpStr.replace("request.", "fu.");
			}
			if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // 单行文本框 并且不是整型
				tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}else{
				tmpStr=tmpStr.replace("XCommaX", "");
			}
			if(isList==false||"1".equals(viewBean.getIsview()))
			v_restr.append(tmpStr);//添加数据
		}
		// 附件
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if ("6".equals(fieldBean.getFieldhtmltype())) { 
				tmpStr=HtmlUtil.OPER_UPLOADREQUEST;
				if(isList==true){tmpStr=HtmlUtil.OPER_LISTLOADREQUEST;}//列表显示
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if(isList==false||"1".equals(viewBean.getIsview())){
					v_restr.append(tmpStr);//添加数据
				}
				tmpStr="\nif(!\"\".equals(v_FileXnameX)){v_XnameX = v_FileXnameX;}//XmarkX";
				if(isList==true){tmpStr=tmpStr.replace("\n", "\n\t\t");};
				if(isList==false||"1".equals(viewBean.getIsview())){
					v_restr.append(tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));//添加数据
				}
			}
		}
		return v_restr.toString();
	}
	//得到明细请求信息
	public String getDetailRequestStr(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"1");//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())){	
				tmpStr=HtmlUtil.OPER_DETAILREQUEST.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(billBean.getHasfileup())){
					tmpStr=tmpStr.replace("request.", "fu.");
				}
				if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // 单行文本框 并且不是整型
					tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
				}else{
					tmpStr=tmpStr.replace("XCommaX", "");
				}
				v_restr.append(tmpStr);//添加数据
			}
		}
		return v_restr.toString();
	}
	//得到字段定义信息
	public String getFieldStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())||"0".equals(fieldBean.getViewtype())){
				tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v_"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(fieldBean.getViewtype())){
					tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v1_"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				}
				v_restr.append("\t"+tmpStr);//添加数据
			}
		}
		// 附件
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if ("6".equals(fieldBean.getFieldhtmltype())&&"0".equals(fieldBean.getViewtype())) { 
				tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v_File"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				v_restr.append("\t"+tmpStr);//添加数据
			}
		}		
		return v_restr.toString();
	}
	//得到字段定义信息
	public String getFieldRecordStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())||"0".equals(fieldBean.getViewtype())){
				tmpStr=HtmlUtil.FIELDRECORDSTR.replace("XFieldX", "v_"+fieldBean.getFieldname()).replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(fieldBean.getViewtype())){
					tmpStr=HtmlUtil.FIELDRECORDSTR.replace("XFieldX", "v1_"+fieldBean.getFieldname()).replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				}
				v_restr.append("\t"+tmpStr);//添加数据
			}
		}
		return v_restr.toString();
	}
	//得到过程参数信息
	public String getProcParaStr(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		boolean isfirst=true;
		for (FieldInfoBean fieldBean : list) {
			tmpStr="\n\tProcPara += flag + v_XnameX;//XmarkX";
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())){
				if("Add".equals(pageBean.getPagetype())&&isfirst==true){
					tmpStr="\n\tProcPara = v_XnameX;//XmarkX";
					isfirst=false;
				}
				if(isList==true){tmpStr=tmpStr.replace("\n\t", "\n\t\t");}
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				v_restr.append(tmpStr);//添加数据
			}
		}
		return v_restr.toString();
	}
	//得到过程参数信息
	public String getDetailProcParaStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//查出字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			tmpStr="\n\t\tProcPara += flag + v1_XnameX;//XmarkX";
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())){
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				v_restr.append(tmpStr);//添加数据
			}
		}
		return v_restr.toString();
	}
	//得到页面请求参数
	public String getParameterInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		String parameter="";//临时用
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsmandatory())){
				parameter+= "&XnameX=\"+p_XnameX+\"".replace("XnameX", fieldBean.getFieldname());
			}
		}
		if(isList==true){
			parameter=parameter.length()>0?parameter.substring(1):"";
		}
		return parameter;
	}	
	//得到页面请求参数
	public String getArgsFieldInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		StringBuffer restr=new StringBuffer();
		String tmpStr="";//临时用
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsedit())&&isList==true||"1".equals(viewBean.getIsmandatory())){
				tmpStr="\nString p_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX";//临时用
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				restr.append(tmpStr);
			}
		}
		return restr.toString();
	}
	//查询条件
	public String getSQLWhereInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		String v_mandStr="";
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			String tmpStr = "if(!X1X){//XmarkX\n\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n}\n";//临时用
			String tmpStr2 = "XnameX=\"+p_XnameX+\"";
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsedit())){
				String fieldname= fieldBean.getFieldname();//数据库字段名称(原始的)
				String fieldlabel = fieldBean.getFieldlabel();//字段显示名
				String fieldhtmltype = fieldBean.getFieldhtmltype();//字段表现形式 如：文本框，多行文本框
				String type = fieldBean.getType();//字段类型如文本框中的金额
				if ("1".equals(fieldhtmltype)&&"1".equals(type)||"2".equals(fieldhtmltype)) { //单行/多行文本框
					tmpStr2 = "XnameX like '%\"+p_XnameX+\"%'";//单行文档框
				}
				tmpStr = tmpStr.replace("X1X", "\"\".equals(p_XnameX)").replace("XWHERE1X", tmpStr2);
				if("1".equals(viewBean.getIsmandatory())){
					v_mandStr=v_mandStr+" and "+tmpStr2.replace("XnameX",fieldname);
				}else{
					v_restr.append(tmpStr.replace("XnameX", fieldname).replace("XmarkX", fieldlabel));
				}
			}
		}
		if(!"".equals(v_mandStr)){
			v_mandStr="\nSqlWhere=SqlWhere+\""+v_mandStr+"\";\n";
		}
		return v_mandStr+v_restr.toString();
	}
	
	//记录日志
	public String getWriteLogInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		String LOGSTR="\n\t\tCommonLog.AddLogModel(\"X2X\",v_X1X,CommonOutData.getData(\"X1X\"),X4X,X3X,v_tableName,v_resid,v_id,user);";
		if(isList==true){LOGSTR=LOGSTR.replace("\n\t\t", "\n\t\t\t");}
		StringBuffer restr = new StringBuffer();
		FieldViewBean viewBean=null;
		String fieldname="";//字段名称
		String transmethod ="";//转换方法
		String fieldlabel="";//字段显示名
		String islog="";//是否记录日志
		String fieldid ="";//字段ID
		
		String v_tranTmp1="";
		String v_tranTmp2="";
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			islog=viewBean.getIslog();
			if("1".equals(islog)){
				v_tranTmp1="";v_tranTmp2="";
				fieldname = fieldBean.getFieldname();// 字段名称
				fieldlabel = fieldBean.getFieldlabel();// 字段显示名
				transmethod= fieldBean.getTransmethod();//转换方法
				if("5".equals(fieldBean.getFieldhtmltype())){// Html字段类型如：文本框
					fieldid = fieldBean.getFieldid();// 字段ID
					v_tranTmp1 = "common.LookNameBySelectID(v_" + fieldname + ",\""+fieldid+"\")";
					v_tranTmp2="common.LookNameBySelectID(CommonOutData.getData(\""+fieldname+"\"),\""+fieldid+"\")";
				}
				if(!"".equals(transmethod)){
					v_tranTmp1 =transmethod+"(v_"+fieldname+")";
					v_tranTmp2 =transmethod+"(CommonOutData.getData(\""+fieldname+"\"))";
				}
				v_tranTmp1="".equals(v_tranTmp1)?"\"\"":v_tranTmp1;
				v_tranTmp2="".equals(v_tranTmp2)?"\"\"":v_tranTmp2;
				restr.append(LOGSTR.replace("X1X", fieldname).replace("X2X", fieldlabel).replace("X3X", v_tranTmp2).replace("X4X", v_tranTmp1));
			}
		}
		return restr.toString();
	}
	//得到权限信息
	public String getShareInfo(BillBean billBean, PageInfoBean pageBean){
		StringBuffer restr=new StringBuffer();
		restr.append("\nString s_prjid = Util.null2String(request.getParameter(\"prjid\"));//相关项目");
		restr.append("\nString s_crmid = Util.null2String(request.getParameter(\"crmid\"));//相关客户");
		restr.append("\nString s_busid = Util.null2String(request.getParameter(\"busid\"));//相关业务");
		restr.append("\nString s_isfromreq = Util.null2String(request.getParameter(\"isfromreq\"));//是否来自流程");
		restr.append("\nString s_oper = Util.null2String(request.getParameter(\"oper\"));//流程中的操作类型");
		restr.append("\nString s_requestid = Util.null2String(request.getParameter(\"requestid\"));//请求ID");
		restr.append("\nString s_nodeid = Util.null2String(request.getParameter(\"nodeid\"));//节点ID");

		restr.append("\n\nMap parammap =new HashMap();//权限验证中用到的参数");
		restr.append("\nparammap.put(\"prjid\",s_prjid);//相关项目");
		restr.append("\nparammap.put(\"crmid\",s_crmid);//相关客户");
		restr.append("\nparammap.put(\"busid\",s_busid);//相关业务");
		restr.append("\nparammap.put(\"isfromreq\",s_isfromreq);//是否来自流程");
		restr.append("\nparammap.put(\"oper\",s_oper);//流程中的操作类型");
		restr.append("\nparammap.put(\"requestid\",s_requestid);//请求ID");
		restr.append("\nparammap.put(\"nodeid\",s_nodeid);//节点ID");
		
		restr.append("\n\nparammap=(Map)session.getAttribute(fla_billid.split(\":\")[0]+\":\"+s_prjid);");
		
		restr.append("\n\nif(!BillLimitVarify.checkUserRight(fla_billid,parammap,user)) {");
		restr.append("\n\tresponse.sendRedirect(\"/notice/noright.jsp\") ;");
		restr.append("\n\treturn ;");
		restr.append("\n}");
		
		return restr.toString();
	}
	//得到默认值设置
	public String getDefFieldInfo(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"");//查出所有主字段
		StringBuffer restr=new StringBuffer();
		String defvalue="";//默认值
		String tmpStr="";//临时用
		
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			defvalue=fieldBean.getDefaultvalue();
			if(!"".equals(defvalue)){
				tmpStr="\nString v_XnameX = Util.null2String(request.getParameter(\"XdefnameX\"));//XmarkX";//临时用
				if("userid".equals(defvalue)){
					tmpStr="\nString v_XnameX=userid;".replace("XnameX", fieldBean.getFieldname());
				}else if("deptid".equals(defvalue)){
					tmpStr="\nString v_XnameX=user.getUserDepartment()+\"\";".replace("XnameX", fieldBean.getFieldname());
				}else if("orgid".equals(defvalue)){
					tmpStr="\nString v_XnameX=user.getUserSubCompany1()+\"\";".replace("XnameX", fieldBean.getFieldname());
				}else{
					tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XdefnameX", defvalue).replace("XmarkX", fieldBean.getFieldlabel());
				}
				restr.append(tmpStr);
			}
		}
		return restr.toString();
	}
	// 得到文件路径
	public String getFilePath(String... arrpath) {
		String path = GCONST.getRootPath() + "flazyform";
		for (String tmp : arrpath) {
			path += File.separatorChar + tmp;
		}
		return path;
	}

	// 判断文件是否存在
	public boolean isExistFile(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	/**
	 *生成浏览框
	 */
	public String getBrowse(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"");//查出所有主字段
		StringBuffer buffer = new StringBuffer();
		String tmpStr = "sub onShowXnameX(data)\n\tdata=replace(data,\"Butt\",\"\")\n";
		tmpStr += "\tids = window.showModalDialog(\"XurlX\")\n";
		tmpStr += "\tif (Not IsEmpty(ids)) then\n\tif ids(0)<> \"\" then\n";
		tmpStr += "\t\tdocument.getElementById(\"Span\"+data).innerHtml = ids(1)\n";
		tmpStr += "\t\tdocument.getElementById(data).value=ids(0)\n";
		tmpStr += "\telse\n";
		tmpStr += "\t\tdocument.getElementById(\"Span\"+data).innerHtml = \"\"\n";
		tmpStr += "\t\tdocument.getElementById(data).value=\"\"\n";
		tmpStr += "\tend if\n\tend if\nend sub\n\n";
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			// 多行文本框条件结束 // 浏览按钮
			if ("1".equals(viewBean.getIsview())&&"3".equals(fieldBean.getFieldhtmltype())) {
				BrowserComInfo comInfo = new BrowserComInfo();
				String htmltype = fieldBean.getType();// 单据的字段的type
				String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
				String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
				if(!htmltype.equals("2")) {
					buffer.append(tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XurlX", url));
				}
			}
		}
		return buffer.toString();
	}
	public String getCOLGROUP(int left,int rigth,int cnt){
		String tmpStr="<COL width=\""+left+"%\">\n<COL width=\""+rigth+"%\">\n";
		StringBuffer buffer = new StringBuffer();
		while(cnt>0){
			buffer.append(tmpStr);
			cnt--;
		}
		return buffer.toString();
	}
	/**
	 * 写文件
	 */
	public void writeFile(String fileName, String str) {
		File file = new File(fileName);
		BufferedWriter writer = null;
		try {
			if (!file.exists()) {
				this.createDir(fileName);
				file.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件
	 */
	public static String readFile(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer str = new StringBuffer();
		String tmpstr = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((tmpstr = reader.readLine()) != null) {
				str.append(tmpstr + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	// 创建文件夾
	public void createDir(String fileName) {
		File file = new File(fileName.substring(0, fileName
				.lastIndexOf(File.separatorChar)));
		if (!file.exists()) {
			file.mkdir();
		}
	}
}
