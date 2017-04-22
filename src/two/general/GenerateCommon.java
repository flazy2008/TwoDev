package two.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import weaver.general.GCONST;
import weaver.workflow.field.BrowserComInfo;

public class GenerateCommon {
	String mandOnChange = "isMand(this,0);";

	public String getSpanStr(String spanStr, String defvalue, String pageType,
			FieldInfoBean fieldBean) {
		String tmpStr = "";
		spanStr="\t\t"+spanStr;
		String transmethod = fieldBean.getTransmethod();
		String fieldname = fieldBean.getFieldname();
		if (defvalue.length() >= 4) {
			if ("".equals(transmethod)) {
				tmpStr = " <%=v_" + defvalue + "%> ";
			} else {
				tmpStr = " <%=" + transmethod + "(v_" + defvalue + ")" + "%> ";
			}
			return spanStr.replace("XvalueX", tmpStr) + "\n";
		} else if (!"".equals(transmethod) && !"Add".equals(pageType)) {
			tmpStr = " <%=" + transmethod + "(v_" + fieldname + ")" + "%> ";
			return spanStr.replace("XvalueX", tmpStr) + "\n";
		}else if("View".equals(pageType)){
			tmpStr = " <%=v_" + fieldname + "%> ";
			if("5".equals(fieldBean.getFieldhtmltype())){// Html字段类型如：文本框
				String fieldid = fieldBean.getNodeBean().getFieldid();// 字段ID
				tmpStr = " <%=common.LookNameBySelectID(v_" + fieldname + ",\""+fieldid+"\")%> ";
			}
			return spanStr.replace("XvalueX", tmpStr) + "\n";
		} else {
			return spanStr.replace("XvalueX", "") + "\n";
		}
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

	// 替换金额字段的逗号
	public String getRepComma(String tmpStr2, FieldInfoBean fieldBean) {
		String str = "";
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		if (fieldType.equals("1")) { // 单行文本框
			if (!htmltype.equals("1")) { // 单行文本框中的整型
				str = tmpStr2.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}
		}
		if ("".equals(str)) {
			str = tmpStr2.replace("XCommaX", "");
		}
		return str;
	}

	// 字段必填
	public String getMandStr(String ismand, FieldInfoBean fieldBean) {
		String str = " mand=X1X ";
		if ("1".equals(ismand)) {
			String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
			String htmltype = fieldBean.getHtmltype();// 单据的字段的type
			if (fieldType.equals("1")) { // 单行文本框
				if (htmltype.equals("1")) { // 单行文本框中的文本
					str = str.replace("X1X", fieldType + " onChange=\""
							+ mandOnChange + "\"");
				} else {
					str = str.replace("X1X", fieldType);
				}
			} else {
				str = str.replace("X1X", fieldType + " onChange=\""
						+ mandOnChange + "\"");
			}
		} else {
			str = "";
		}
		return str;
	}

	/**
	 *生成浏览框
	 */
	public StringBuffer getBrowse(List<FieldInfoBean> beans) {
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
		for (FieldInfoBean fieldBean : beans) {
			FieldViewBean formBean = fieldBean.getNodeBean();
			String fieldname = fieldBean.getFieldname();// 字段名称
			if ("1".equals(formBean.getIsview())) {
				String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
				if (fieldType.equals("3")) { // 多行文本框条件结束 // 浏览按钮
					// (涉及workflow_broswerurl表)
					BrowserComInfo comInfo = new BrowserComInfo();
					String htmltype = fieldBean.getHtmltype();// 单据的字段的type
					String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
					String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
					fieldname = fieldname.replaceFirst(fieldname
							.substring(0, 1), fieldname.substring(0, 1)
							.toUpperCase());
					if(!htmltype.equals("2")) {
					buffer.append(tmpStr.replace("XnameX", fieldname).replace(
							"XurlX", url));
					}
				}
			}
		}
		return buffer;
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
	public String getCOLGROUP(int left,int rigth,int cnt){
		String tmpStr="<COL width=\""+left+"%\">\n<COL width=\""+rigth+"%\">\n";
		StringBuffer buffer = new StringBuffer();
		while(cnt>0){
			buffer.append(tmpStr);
			cnt--;
		}
		return buffer.toString();
	}
	// 得到空字段
	public String getTDInfo(String fieldlabel,String tdcontent) {
		fieldlabel="".equals(fieldlabel)?"&nbsp;":fieldlabel;
		tdcontent="".equals(tdcontent)?"&nbsp;":tdcontent;
		StringBuffer buffer = new StringBuffer();
		buffer.append("<TD>"+fieldlabel+"</TD>\n");
		buffer.append("<TD class=Field>\n"+tdcontent+"</TD>\n");
		return buffer.toString();
	}
	//记录日志
	public String getWriteLogInfo(List<FieldInfoBean> beans){
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean =null;
		String fieldname="";//字段名称
		String transmethod ="";//转换方法
		String fieldlabel="";//字段显示名
		String islog="";//是否记录日志
		String fieldType ="";//Html字段类型如：文本框
		String fieldid ="";//字段ID
		
		String v_tranTmp1="";
		String v_tranTmp2="";
		String tmpstr="\n\t\t\t\tCommonLog.AddLogModel(\"X2X\",v_X1X,CommonOutData.getData(\"X1X\"),X4X,X3X,v_tableName,reid,v_id,user);";
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			islog=formBean.getIslog();
			if("1".equals(islog)){
				v_tranTmp1="";
				v_tranTmp2="";
				fieldname = fieldBean.getFieldname();// 字段名称
				fieldlabel = fieldBean.getFieldlabel();// 字段显示名
				transmethod= fieldBean.getTransmethod();//转换方法
				fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
				if("5".equals(fieldBean.getFieldhtmltype())){// Html字段类型如：文本框
					fieldid = fieldBean.getNodeBean().getFieldid();// 字段ID
					v_tranTmp1 = "common.LookNameBySelectID(v_" + fieldname + ",\""+fieldid+"\")";
					v_tranTmp2="common.LookNameBySelectID(CommonOutData.getData(\""+fieldname+"\"),\""+fieldid+"\")";
				}
				if(!"".equals(transmethod)){
					v_tranTmp1 =transmethod+"(v_"+fieldname+")";
					v_tranTmp2 =transmethod+"(CommonOutData.getData(\""+fieldname+"\"))";
				}
				v_tranTmp1="".equals(v_tranTmp1)?"\"\"":v_tranTmp1;
				v_tranTmp2="".equals(v_tranTmp2)?"\"\"":v_tranTmp2;
				buffer.append(tmpstr.replace("X1X", fieldname).replace("X2X", fieldlabel).replace("X3X", v_tranTmp2).replace("X4X", v_tranTmp1));
			}
		}
		return buffer.toString();
	}
}
