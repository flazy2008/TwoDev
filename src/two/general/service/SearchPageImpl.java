package two.general.service;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import two.general.util.FieldContentUtil;
import two.general.util.GenerateUtil;
import two.general.util.HtmlUtil;
import weaver.conn.RecordSet;
import weaver.general.GCONST;
import weaver.general.Util;
import weaver.workflow.field.BrowserComInfo;

public class SearchPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//字段信息
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Search.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","Search.jsp");//模板文件
		String modelStr =gener.readFile(fromfile);//模板中的内容
		String hasupload=billBean.getHasfileup();//有附件
		String maviewtype=pageBean.getMainviewtype();//得到页面的显示
		RecordSet rs=new RecordSet();
		String v_sql="";
		//替换内容开始
		modelStr = modelStr.replace("${titlename}", billBean.getNamelabel());
		modelStr = modelStr.replace("${fla_billid}", billBean.getId());//表单ID
		modelStr = modelStr.replace("${XTableX}", billBean.getTablename());//表单名称
		modelStr = modelStr.replace("${shareinfo}",gener.getShareInfo(billBean, pageBean));//权限信息
		modelStr=modelStr.replace("//${fieldStr}",this.getFieldStr(billBean, pageBean).replace("\t", ""));//定义字段
		modelStr = modelStr.replace("${colgroup}", this.getCOLGROUP(billBean, pageBean));//表格样式

		modelStr = modelStr.replace("${colSpan}",""+2*(Util.getIntValue(maviewtype, 1)+1));
		modelStr = modelStr.replace("${content}",this.getMainRowInfo(billBean, pageBean));//表单内容
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//表单内容
		modelStr = modelStr.replace("${parameter}",gener.getParameterInfo(billBean, pageBean,true));//参数
		modelStr = modelStr.replace("//${argsStr}",gener.getArgsFieldInfo(billBean, pageBean,false));//查询条件
		modelStr = modelStr.replace("//${sqlwhere}",this.getSQLWhereInfo(billBean, pageBean).toString());//查询条件
		modelStr = modelStr.replace("//${colStr}",this.getColStrInfo(billBean, pageBean));//得到列信息

		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set searchpage='X1X' where id="+billBean.getId();
		v_sql=v_sql.replace("X1X", toFile.replace(GCONST.getRootPath()+"", File.separatorChar+""));
		rs.executeSql(v_sql);
	}

	@Override
	public void MainInfo(BillBean billBean, PageInfoBean pageBean) {
		this.CreatePage(billBean, pageBean);//生成主文件
		if("1".equals(pageBean.getIsOper())){//生成处理页面
			this.CreateOper(billBean, pageBean);
		}
		if("1".equals(pageBean.getIsProc())){//生成过程
			this.CreateProc(billBean, pageBean);
		}
	}
	//得到列表信息
	private String getColStrInfo(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		String v_restr="";
		FieldViewBean viewBean=null;
		int index=0;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			String tmpStr1 = "\n\t\"<col width=\\\"XcolwidthX\\\" text=\\\"XmarkX\\\" column=\\\"XnameX\\\" orderkey=\\\"XnameX\\\" X1X X2X/>\"+";
			String urlStr = "target=\\\"_blank\\\" linkkey=\\\"XkeyX\\\" linkvaluecolumn=\\\"XnameX\\\" href=\\\"XurlX\\\"";
			String tranStr = "transmethod=\\\"Xm1X\\\" Xm2X ";
			if ("1".equals(viewBean.getIsview())) {
				index++;
				String fieldname= fieldBean.getFieldname();//数据库字段名称(原始的)
				String fieldlabel = fieldBean.getFieldlabel();//字段显示名
				String fieldhtmltype = fieldBean.getFieldhtmltype();//字段表现形式 如：文本框，多行文本框
				String type = fieldBean.getType();//字段类型如文本框中的金额
				String tranmethod = fieldBean.getTransmethod();// 转换方法
				if ("3".equals(fieldhtmltype)) {//浏览框
					BrowserComInfo comInfo = new BrowserComInfo();
					String linkurl = Util.null2String(comInfo.getLinkurl(type)); // 浏览值点击的时候链接的url
					if (!"".equals(linkurl)) {
						String key = this.getUrlArgs(linkurl);
						linkurl = linkurl.replace(key, "");
						urlStr = urlStr.replace("XkeyX",key.substring(1).replace("=", "")).replace("XnameX", fieldname).replace("XurlX", linkurl);
						tmpStr1 = tmpStr1.replace("X1X", urlStr);
					}
				}else if ("4".equals(fieldhtmltype)) {
					tranStr=tranStr.replace("Xm2X", "");
					tranStr = tranStr.replace("Xm1X","two.common.CommonInfoByID.LookImgByType");
					tmpStr1 = tmpStr1.replace("X2X", tranStr);					
				}else if ("5".equals(fieldhtmltype)) {
					String fieldid = fieldBean.getFieldid();// 字段ID
					tranStr=tranStr.replace("Xm2X", "otherpara=\\\""+fieldid+"\\\"");
					tranStr = tranStr.replace("Xm1X","two.common.CommonInfoByID.LookNameBySelectID");
					tmpStr1 = tmpStr1.replace("X2X", tranStr);					
				}
				if (!"".equals(tranmethod)) {
					tranStr = tranStr.replace("Xm2X", "").replace("Xm1X", tranmethod.replace("common.", "two.common.CommonInfoByID."));
					tmpStr1 = tmpStr1.replace("X2X", tranStr);
				}
				tmpStr1 = tmpStr1.replace("X1X", "").replace("X2X", "");
				v_restr = v_restr+tmpStr1.replace("XnameX", fieldname).replace("XmarkX", fieldlabel);
			}
		}
		v_restr=v_restr.replace("XcolwidthX",""+1350/(index+1));
		return v_restr.toString();
	}
	//参数替换
	private String getUrlArgs(String str) {
		String reStr = "";
		Matcher m = Pattern.compile("&.+?=").matcher(str);
		if (m.find()) {
			reStr = Util.null2String(m.group());
		} else if (str.lastIndexOf("?") > -1) {
			reStr = str.substring(str.lastIndexOf("?"));
		}
		return reStr;
	}	
	//得到主表请求信息
	private String getFieldStr(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//查出所有主字段
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// 临时用
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			tmpStr=HtmlUtil.OPER_MAINREQUEST;
			tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
			if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // 单行文本框 并且不是整型
				tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}else{
				tmpStr=tmpStr.replace("XCommaX", "");
			}
			if("1".equals(viewBean.getIsedit()))
			v_restr.append(tmpStr);//添加数据
		}
		return v_restr.toString();
	}	
	//得到查询条件
	private StringBuffer getSQLWhereInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到主表字段
		StringBuffer buffer=new StringBuffer();
		String tmpStr="";//临时用
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Search");
			String tmpStr1 = "if(!X1X){//XmarkX\n\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n}\n";
			String tmpStr2 = "XnameX=\"+v_XnameX+\"";
			if("1".equals(viewBean.getIsedit())){
				String fieldname= fieldBean.getFieldname();//数据库字段名称(原始的)
				String fieldlabel = fieldBean.getFieldlabel();//字段显示名
				String fieldhtmltype = fieldBean.getFieldhtmltype();//字段表现形式 如：文本框，多行文本框
				String type = fieldBean.getType();//字段类型如文本框中的金额
				if ("1".equals(fieldhtmltype)&&"1".equals(type)||"2".equals(fieldhtmltype)||
						"3".equals(fieldhtmltype)&&"2".equals(type)) { //单行/多行文本框//日期
					tmpStr2 = "XnameX like '%\"+v_XnameX+\"%'";//单行文档框
				}
				if ("5".equals(fieldhtmltype)) {//选择框
					tmpStr = tmpStr1.replace("X1X", "(\"-1\".equals(v_XnameX)||\"\".equals(v_XnameX))").replace("XWHERE1X", tmpStr2);
				}else{
					tmpStr = tmpStr1.replace("X1X", "\"\".equals(v_XnameX)").replace("XWHERE1X", tmpStr2);
				}
				buffer.append(tmpStr.replace("XnameX", fieldname).replace("XmarkX", fieldlabel));
			}
		}
		return buffer;
	}
	private String getCOLGROUP(BillBean billBean, PageInfoBean pageBean){
		GenerateUtil gener=new GenerateUtil();
		String mainView = pageBean.getMainviewtype();
		int colspan = Util.getIntValue(mainView, 1) + 1;
		String tmpStr="";//临时用
		if ("1".equals(colspan + "")) {
			tmpStr = gener.getCOLGROUP(30, 70, colspan);
		} else if ("2".equals(colspan + "")) {
			tmpStr = gener.getCOLGROUP(20, 30, colspan);
		} else if ("3".equals(colspan + "")) {
			tmpStr = gener.getCOLGROUP(13, 20, colspan);
		} else if ("4".equals(colspan + "")) {
			tmpStr = gener.getCOLGROUP(11, 14, colspan);
		}else if ("5".equals(colspan + "")) {
			tmpStr = gener.getCOLGROUP(9, 11, colspan);
		}// 得到表头信息
		return tmpStr;
	}
	//得到主表行的信息
	private String getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到主表字段
		int maviewtype=Util.getIntValue(pageBean.getMainviewtype(),1)+1;//得到页面的显示
		StringBuffer trContent=new StringBuffer();
		StringBuffer tdContent=new StringBuffer();
		String tmpTR="<TR>\nXTDcontentX</TR><TR><TD class=Line colspan="+maviewtype*2+"></TD></TR>\n\n";
		String tmpTD="<TD>XnameX</TD>\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//临时用
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Search");
			if("1".equals(viewBean.getIsedit())){
				index++;
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldlabel());
				tmpStr=tmpStr.replace("XcontentX", fieldContentUtil.getEditFieldInfo(fieldBean, viewBean, ""));
				tdContent.append(tmpStr);
				if(index%(maviewtype)==0){
					trContent.append(tmpTR.replace("XTDcontentX", tdContent));
					tdContent=new StringBuffer();
				}
			}
		}
		for(int i=1;i<=(maviewtype-index%maviewtype)&&(index%maviewtype!=0);i++){
			tmpStr=tmpTD.replace("XnameX","&nbsp;").replace("\nXcontentX","&nbsp;");
			tdContent.append(tmpStr);//明细表列的内容
		}
		if(index%maviewtype!=0&&tdContent.length()>0){//补充空列
			trContent.append(tmpTR.replace("XTDcontentX", tdContent));
		}
		return trContent.toString();
	}
}
