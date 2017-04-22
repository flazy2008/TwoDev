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
	//�õ�����������Ϣ
	public String getMainRequestStr(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			tmpStr=HtmlUtil.OPER_MAINREQUEST;
			if(isList==true){tmpStr=HtmlUtil.OPER_DETAILREQUEST.replace("\n\t\tv1_","\n\t\tv_");}//�б���ʾ
			
			tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
			if("1".equals(billBean.getHasfileup())){
				tmpStr=tmpStr.replace("request.", "fu.");
			}
			if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // �����ı��� ���Ҳ�������
				tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}else{
				tmpStr=tmpStr.replace("XCommaX", "");
			}
			if(isList==false||"1".equals(viewBean.getIsview()))
			v_restr.append(tmpStr);//�������
		}
		// ����
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if ("6".equals(fieldBean.getFieldhtmltype())) { 
				tmpStr=HtmlUtil.OPER_UPLOADREQUEST;
				if(isList==true){tmpStr=HtmlUtil.OPER_LISTLOADREQUEST;}//�б���ʾ
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if(isList==false||"1".equals(viewBean.getIsview())){
					v_restr.append(tmpStr);//�������
				}
				tmpStr="\nif(!\"\".equals(v_FileXnameX)){v_XnameX = v_FileXnameX;}//XmarkX";
				if(isList==true){tmpStr=tmpStr.replace("\n", "\n\t\t");};
				if(isList==false||"1".equals(viewBean.getIsview())){
					v_restr.append(tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));//�������
				}
			}
		}
		return v_restr.toString();
	}
	//�õ���ϸ������Ϣ
	public String getDetailRequestStr(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"1");//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())){	
				tmpStr=HtmlUtil.OPER_DETAILREQUEST.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(billBean.getHasfileup())){
					tmpStr=tmpStr.replace("request.", "fu.");
				}
				if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // �����ı��� ���Ҳ�������
					tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
				}else{
					tmpStr=tmpStr.replace("XCommaX", "");
				}
				v_restr.append(tmpStr);//�������
			}
		}
		return v_restr.toString();
	}
	//�õ��ֶζ�����Ϣ
	public String getFieldStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())||"0".equals(fieldBean.getViewtype())){
				tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v_"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(fieldBean.getViewtype())){
					tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v1_"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				}
				v_restr.append("\t"+tmpStr);//�������
			}
		}
		// ����
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if ("6".equals(fieldBean.getFieldhtmltype())&&"0".equals(fieldBean.getViewtype())) { 
				tmpStr=HtmlUtil.FIELDSTR.replace("XFieldX", "v_File"+fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				v_restr.append("\t"+tmpStr);//�������
			}
		}		
		return v_restr.toString();
	}
	//�õ��ֶζ�����Ϣ
	public String getFieldRecordStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())||"0".equals(fieldBean.getViewtype())){
				tmpStr=HtmlUtil.FIELDRECORDSTR.replace("XFieldX", "v_"+fieldBean.getFieldname()).replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				if("1".equals(fieldBean.getViewtype())){
					tmpStr=HtmlUtil.FIELDRECORDSTR.replace("XFieldX", "v1_"+fieldBean.getFieldname()).replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				}
				v_restr.append("\t"+tmpStr);//�������
			}
		}
		return v_restr.toString();
	}
	//�õ����̲�����Ϣ
	public String getProcParaStr(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
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
				v_restr.append(tmpStr);//�������
			}
		}
		return v_restr.toString();
	}
	//�õ����̲�����Ϣ
	public String getDetailProcParaStr(BillBean billBean, PageInfoBean pageBean,String viewType){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),viewType);//����ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			tmpStr="\n\t\tProcPara += flag + v1_XnameX;//XmarkX";
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsview())){
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				v_restr.append(tmpStr);//�������
			}
		}
		return v_restr.toString();
	}
	//�õ�ҳ���������
	public String getParameterInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		String parameter="";//��ʱ��
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
	//�õ�ҳ���������
	public String getArgsFieldInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		StringBuffer restr=new StringBuffer();
		String tmpStr="";//��ʱ��
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsedit())&&isList==true||"1".equals(viewBean.getIsmandatory())){
				tmpStr="\nString p_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX";//��ʱ��
				tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				restr.append(tmpStr);
			}
		}
		return restr.toString();
	}
	//��ѯ����
	public String getSQLWhereInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		String v_mandStr="";
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			String tmpStr = "if(!X1X){//XmarkX\n\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n}\n";//��ʱ��
			String tmpStr2 = "XnameX=\"+p_XnameX+\"";
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			if("1".equals(viewBean.getIsedit())){
				String fieldname= fieldBean.getFieldname();//���ݿ��ֶ�����(ԭʼ��)
				String fieldlabel = fieldBean.getFieldlabel();//�ֶ���ʾ��
				String fieldhtmltype = fieldBean.getFieldhtmltype();//�ֶα�����ʽ �磺�ı��򣬶����ı���
				String type = fieldBean.getType();//�ֶ��������ı����еĽ��
				if ("1".equals(fieldhtmltype)&&"1".equals(type)||"2".equals(fieldhtmltype)) { //����/�����ı���
					tmpStr2 = "XnameX like '%\"+p_XnameX+\"%'";//�����ĵ���
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
	
	//��¼��־
	public String getWriteLogInfo(BillBean billBean, PageInfoBean pageBean,boolean isList){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		String LOGSTR="\n\t\tCommonLog.AddLogModel(\"X2X\",v_X1X,CommonOutData.getData(\"X1X\"),X4X,X3X,v_tableName,v_resid,v_id,user);";
		if(isList==true){LOGSTR=LOGSTR.replace("\n\t\t", "\n\t\t\t");}
		StringBuffer restr = new StringBuffer();
		FieldViewBean viewBean=null;
		String fieldname="";//�ֶ�����
		String transmethod ="";//ת������
		String fieldlabel="";//�ֶ���ʾ��
		String islog="";//�Ƿ��¼��־
		String fieldid ="";//�ֶ�ID
		
		String v_tranTmp1="";
		String v_tranTmp2="";
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			islog=viewBean.getIslog();
			if("1".equals(islog)){
				v_tranTmp1="";v_tranTmp2="";
				fieldname = fieldBean.getFieldname();// �ֶ�����
				fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
				transmethod= fieldBean.getTransmethod();//ת������
				if("5".equals(fieldBean.getFieldhtmltype())){// Html�ֶ������磺�ı���
					fieldid = fieldBean.getFieldid();// �ֶ�ID
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
	//�õ�Ȩ����Ϣ
	public String getShareInfo(BillBean billBean, PageInfoBean pageBean){
		StringBuffer restr=new StringBuffer();
		restr.append("\nString s_prjid = Util.null2String(request.getParameter(\"prjid\"));//�����Ŀ");
		restr.append("\nString s_crmid = Util.null2String(request.getParameter(\"crmid\"));//��ؿͻ�");
		restr.append("\nString s_busid = Util.null2String(request.getParameter(\"busid\"));//���ҵ��");
		restr.append("\nString s_isfromreq = Util.null2String(request.getParameter(\"isfromreq\"));//�Ƿ���������");
		restr.append("\nString s_oper = Util.null2String(request.getParameter(\"oper\"));//�����еĲ�������");
		restr.append("\nString s_requestid = Util.null2String(request.getParameter(\"requestid\"));//����ID");
		restr.append("\nString s_nodeid = Util.null2String(request.getParameter(\"nodeid\"));//�ڵ�ID");

		restr.append("\n\nMap parammap =new HashMap();//Ȩ����֤���õ��Ĳ���");
		restr.append("\nparammap.put(\"prjid\",s_prjid);//�����Ŀ");
		restr.append("\nparammap.put(\"crmid\",s_crmid);//��ؿͻ�");
		restr.append("\nparammap.put(\"busid\",s_busid);//���ҵ��");
		restr.append("\nparammap.put(\"isfromreq\",s_isfromreq);//�Ƿ���������");
		restr.append("\nparammap.put(\"oper\",s_oper);//�����еĲ�������");
		restr.append("\nparammap.put(\"requestid\",s_requestid);//����ID");
		restr.append("\nparammap.put(\"nodeid\",s_nodeid);//�ڵ�ID");
		
		restr.append("\n\nparammap=(Map)session.getAttribute(fla_billid.split(\":\")[0]+\":\"+s_prjid);");
		
		restr.append("\n\nif(!BillLimitVarify.checkUserRight(fla_billid,parammap,user)) {");
		restr.append("\n\tresponse.sendRedirect(\"/notice/noright.jsp\") ;");
		restr.append("\n\treturn ;");
		restr.append("\n}");
		
		return restr.toString();
	}
	//�õ�Ĭ��ֵ����
	public String getDefFieldInfo(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"");//����������ֶ�
		StringBuffer restr=new StringBuffer();
		String defvalue="";//Ĭ��ֵ
		String tmpStr="";//��ʱ��
		
		FieldViewBean viewBean=null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			defvalue=fieldBean.getDefaultvalue();
			if(!"".equals(defvalue)){
				tmpStr="\nString v_XnameX = Util.null2String(request.getParameter(\"XdefnameX\"));//XmarkX";//��ʱ��
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
	/**
	 *���������
	 */
	public String getBrowse(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"");//����������ֶ�
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
			// �����ı����������� // �����ť
			if ("1".equals(viewBean.getIsview())&&"3".equals(fieldBean.getFieldhtmltype())) {
				BrowserComInfo comInfo = new BrowserComInfo();
				String htmltype = fieldBean.getType();// ���ݵ��ֶε�type
				String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
				String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
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
}
