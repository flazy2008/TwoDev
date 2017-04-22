package two.general.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class BrowserMutiPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//�ֶ���Ϣ
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"MutiBrowser.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","MutiBrowser.jsp");//ģ���ļ�
		String modelStr =gener.readFile(fromfile);//ģ���е�����
		String maviewtype=pageBean.getMainviewtype();//�õ�ҳ�����ʾ
		RecordSet rs=new RecordSet();
		String v_sql="";
		//�滻���ݿ�ʼ
		modelStr = modelStr.replace("${titlename}", billBean.getNamelabel());
		modelStr = modelStr.replace("${fla_billid}", billBean.getId());//��ID
		modelStr = modelStr.replace("${XTableX}", billBean.getTablename());//������
		modelStr = modelStr.replace("${shareinfo}",gener.getShareInfo(billBean, pageBean));//Ȩ����Ϣ
		modelStr=modelStr.replace("//${fieldStr}",this.getFieldStr(billBean, pageBean).replace("\t", ""));//�����ֶ�
		modelStr = modelStr.replace("${colgroup}", this.getCOLGROUP(billBean, pageBean));//�����ʽ

		modelStr = modelStr.replace("${colSpan}",""+2*(Util.getIntValue(maviewtype, 1)+1));
		modelStr = modelStr.replace("${content}",this.getMainRowInfo(billBean, pageBean));//������
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//������
		modelStr = modelStr.replace("//${sqlwhere}",this.getSQLWhereInfo(billBean, pageBean).toString());//��ѯ����
		for (Map.Entry<String, String> ent : this.getOtherInfo(billBean, pageBean).entrySet()) {
			modelStr = modelStr.replace(ent.getKey(), ent.getValue());
		}
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set browserpage='X1X' where id="+billBean.getId();
		v_sql=v_sql.replace("X1X", toFile.replace(GCONST.getRootPath()+"", File.separatorChar+""));
		rs.executeSql(v_sql);
	}

	@Override
	public void MainInfo(BillBean billBean, PageInfoBean pageBean) {
		this.CreatePage(billBean, pageBean);//�������ļ�
		if("1".equals(pageBean.getIsOper())){//���ɴ���ҳ��
			this.CreateOper(billBean, pageBean);
		}
		if("1".equals(pageBean.getIsProc())){//���ɹ���
			this.CreateProc(billBean, pageBean);
		}
	}	
	//�õ������ı�ǩ��Ϣ
	private Map<String, String> getOtherInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ�������Ϣ
		Map<String, String> map = new HashMap<String, String>();
		String tmpStr="\tString rs_XnameX = Util.null2String(RecordSet.getString(\"XnameX\"));//XmarkX\n";//��ʱ��
		String fieldRecordStr="";//���ݲ�ѯ
		String viewFieldStr="";//��ʾֵ
		String tdcolgroup="";//��
		String coltdStr="";//��Ԫ����
		String dtthStr="";//������
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Browser");
			if("1".equals(viewBean.getIsview())){
				index++;
				if("".equals(viewFieldStr)){viewFieldStr=fieldBean.getFieldname();}//��ʾֵ
				dtthStr+="\t<TH>XnameX</TH>\n".replace("XnameX", fieldBean.getFieldlabel());
				coltdStr+="\t<TD style=\"word-break:break-all\">XvalueX</TD>\n".replace("XvalueX",fieldContentUtil.getValueFieldInfo(fieldBean, viewBean, "", "rs"));
				fieldRecordStr+=tmpStr.replace("XnameX",fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
				tdcolgroup+="<COL width=\"XcolwidthX%\">\n";
			}
		}
		tdcolgroup=tdcolgroup.replace("XcolwidthX",""+100/index);
		map.put("${dtth}",dtthStr);
		map.put("${XViewFieldX}",viewFieldStr);
		map.put("//${fieldRecord}",fieldRecordStr);
		map.put("${coltd}",coltdStr);
		map.put("${tdcolgroup}",tdcolgroup);
		return map;
	}	
	//�õ�����������Ϣ
	private String getFieldStr(BillBean billBean, PageInfoBean pageBean){
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(),"0");//����������ֶ�
		StringBuffer v_restr=new StringBuffer();
		FieldViewBean viewBean=null;
		String tmpStr = "";// ��ʱ��
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(),pageBean.getPagetype());
			tmpStr=HtmlUtil.OPER_MAINREQUEST;
			tmpStr=tmpStr.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel());
			if ("1".equals(fieldBean.getFieldhtmltype())&&!"1".equals(fieldBean.getType())) { // �����ı��� ���Ҳ�������
				tmpStr=tmpStr.replace("XCommaX", ".replaceAll(\",\",\"\")");
			}else{
				tmpStr=tmpStr.replace("XCommaX", "");
			}
			if("1".equals(viewBean.getIsedit()))
			v_restr.append(tmpStr);//�������
		}
		return v_restr.toString();
	}	
	//�õ���ѯ����
	private StringBuffer getSQLWhereInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ������ֶ�
		StringBuffer buffer=new StringBuffer();
		String tmpStr="";//��ʱ��
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Browser");
			String tmpStr1 = "if(!X1X){//XmarkX\n\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n}\n";
			String tmpStr2 = "XnameX=\"+v_XnameX+\"";
			if("1".equals(viewBean.getIsedit())){
				String fieldname= fieldBean.getFieldname();//���ݿ��ֶ�����(ԭʼ��)
				String fieldlabel = fieldBean.getFieldlabel();//�ֶ���ʾ��
				String fieldhtmltype = fieldBean.getFieldhtmltype();//�ֶα�����ʽ �磺�ı��򣬶����ı���
				String type = fieldBean.getType();//�ֶ��������ı����еĽ��
				if ("1".equals(fieldhtmltype)&&"1".equals(type)||"2".equals(fieldhtmltype)||
						"3".equals(fieldhtmltype)&&"2".equals(type)) { //����/�����ı���//����
					tmpStr2 = "XnameX like '%\"+v_XnameX+\"%'";//�����ĵ���
				}
				if ("5".equals(fieldhtmltype)) {//ѡ���
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
		int colspan = Util.getIntValue(mainView, 1) -1;
		String tmpStr="";//��ʱ��
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
		}// �õ���ͷ��Ϣ
		return tmpStr;
	}
	//�õ������е���Ϣ
	private String getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ������ֶ�
		int maviewtype=Util.getIntValue(pageBean.getMainviewtype(),1)-1;//�õ�ҳ�����ʾ
		StringBuffer trContent=new StringBuffer();
		StringBuffer tdContent=new StringBuffer();
		String tmpTR="<TR>\nXTDcontentX</TR><TR><TD class=Line colspan="+maviewtype*2+"></TD></TR>\n\n";
		String tmpTD="<TD>XnameX</TD>\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//��ʱ��
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Browser");
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
			tdContent.append(tmpStr);//��ϸ���е�����
		}
		if(index%maviewtype!=0&&tdContent.length()>0){//�������
			trContent.append(tmpTR.replace("XTDcontentX", tdContent));
		}
		return trContent.toString();
	}
}
