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
			if("5".equals(fieldBean.getFieldhtmltype())){// Html�ֶ������磺�ı���
				String fieldid = fieldBean.getNodeBean().getFieldid();// �ֶ�ID
				tmpStr = " <%=common.LookNameBySelectID(v_" + fieldname + ",\""+fieldid+"\")%> ";
			}
			return spanStr.replace("XvalueX", tmpStr) + "\n";
		} else {
			return spanStr.replace("XvalueX", "") + "\n";
		}
	}

	// �õ��ļ�·��
	public String getFilePath(String... arrpath) {
		String path = GCONST.getRootPath() + "flazyform";
		for (String tmp : arrpath) {
			path += File.separatorChar + tmp;
		}
		return path;
	}

	// �ж��ļ��Ƿ����
	public boolean isExistFile(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	// �滻����ֶεĶ���
	public String getRepComma(String tmpStr2, FieldInfoBean fieldBean) {
		String str = "";
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		if (fieldType.equals("1")) { // �����ı���
			if (!htmltype.equals("1")) { // �����ı����е�����
				str = tmpStr2.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}
		}
		if ("".equals(str)) {
			str = tmpStr2.replace("XCommaX", "");
		}
		return str;
	}

	// �ֶα���
	public String getMandStr(String ismand, FieldInfoBean fieldBean) {
		String str = " mand=X1X ";
		if ("1".equals(ismand)) {
			String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
			String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
			if (fieldType.equals("1")) { // �����ı���
				if (htmltype.equals("1")) { // �����ı����е��ı�
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
	 *���������
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
			String fieldname = fieldBean.getFieldname();// �ֶ�����
			if ("1".equals(formBean.getIsview())) {
				String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
				if (fieldType.equals("3")) { // �����ı����������� // �����ť
					// (�漰workflow_broswerurl��)
					BrowserComInfo comInfo = new BrowserComInfo();
					String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
					String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
					String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
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
	 * д�ļ�
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
	 * ��ȡ�ļ�
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

	// �����ļ��A
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
	// �õ����ֶ�
	public String getTDInfo(String fieldlabel,String tdcontent) {
		fieldlabel="".equals(fieldlabel)?"&nbsp;":fieldlabel;
		tdcontent="".equals(tdcontent)?"&nbsp;":tdcontent;
		StringBuffer buffer = new StringBuffer();
		buffer.append("<TD>"+fieldlabel+"</TD>\n");
		buffer.append("<TD class=Field>\n"+tdcontent+"</TD>\n");
		return buffer.toString();
	}
	//��¼��־
	public String getWriteLogInfo(List<FieldInfoBean> beans){
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean =null;
		String fieldname="";//�ֶ�����
		String transmethod ="";//ת������
		String fieldlabel="";//�ֶ���ʾ��
		String islog="";//�Ƿ��¼��־
		String fieldType ="";//Html�ֶ������磺�ı���
		String fieldid ="";//�ֶ�ID
		
		String v_tranTmp1="";
		String v_tranTmp2="";
		String tmpstr="\n\t\t\t\tCommonLog.AddLogModel(\"X2X\",v_X1X,CommonOutData.getData(\"X1X\"),X4X,X3X,v_tableName,reid,v_id,user);";
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			islog=formBean.getIslog();
			if("1".equals(islog)){
				v_tranTmp1="";
				v_tranTmp2="";
				fieldname = fieldBean.getFieldname();// �ֶ�����
				fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
				transmethod= fieldBean.getTransmethod();//ת������
				fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
				if("5".equals(fieldBean.getFieldhtmltype())){// Html�ֶ������磺�ı���
					fieldid = fieldBean.getNodeBean().getFieldid();// �ֶ�ID
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
