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

public class EditStandPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//�ֶ���Ϣ
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		Map<String, String> map = new HashMap<String, String>();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_Operation.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","List_Operation.jsp");//ģ���ļ�
		if(gener.isExistFile(toFile)){//�Ѵ�������ļ�
			fromfile=toFile;
		}
		String modelStr =gener.readFile(fromfile);//ģ���е�����
		String hasupload=billBean.getHasfileup();//�и���
		//�滻����
		modelStr=modelStr.replace("${Edit_procName}", "fla_"+billBean.getTablename()+"_ledit");
		modelStr=modelStr.replace("${XtableX}", billBean.getTablename());
		if("1".equals(hasupload)){//���ڸ���
			modelStr=modelStr.replace("//${upload}",HtmlUtil.OPER_FILEUPLOAD);
			modelStr=modelStr.replace("request.", "fu.");
		}else{
			modelStr=modelStr.replace("//${upload}","");
		}
		modelStr=modelStr.replace("//${fieldStr}",gener.getFieldStr(billBean, pageBean,"0").replace("\t", ""));//�����ֶ�
		modelStr=modelStr.replace("${Edit_mainrequest}",gener.getMainRequestStr(billBean, pageBean,true));//�õ�ҳ��������Ϣ
		modelStr=modelStr.replace("${Edit_procPara}",gener.getProcParaStr(billBean, pageBean,true));//�õ��洢������Ϣ
		modelStr=modelStr.replace("${Edit_procName}", "fla_"+billBean.getTablename()+"_ledit");
		modelStr=modelStr.replace("${Edit_log}", gener.getWriteLogInfo(billBean, pageBean,true));//д��־
		
		gener.writeFile(toFile, modelStr);
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
		this.CreateMainProc(billBean, pageBean);//�����������
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Stand_Edit.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","Stand_Edit.jsp");//ģ���ļ�
		String modelStr =gener.readFile(fromfile);//ģ���е�����
		String hasupload=billBean.getHasfileup();//�и���
		String maviewtype=pageBean.getMainviewtype();//�õ�ҳ�����ʾ
		RecordSet rs=new RecordSet();
		String v_sql="";
		//�滻���ݿ�ʼ
		modelStr = modelStr.replace("${titlename}", billBean.getNamelabel());
		modelStr = modelStr.replace("${fla_billid}", billBean.getId());//��ID
		modelStr = modelStr.replace("${XTableX}", billBean.getTablename());//������
		modelStr = modelStr.replace("${shareinfo}",gener.getShareInfo(billBean, pageBean));//Ȩ����Ϣ
		modelStr=modelStr.replace("//${fieldStr}",gener.getFieldStr(billBean, pageBean,"0").replace("\t", ""));//�����ֶ�
		modelStr=modelStr.replace("//${fieldRecord}",gener.getFieldRecordStr(billBean, pageBean,"0"));//�����¼��
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//������
		if("1".equals(hasupload)){//���ڸ���
			modelStr = modelStr.replace("${enctype}","enctype=\"multipart/form-data\"");//���ñ��ύ����
		}else{
			modelStr = modelStr.replace("${enctype}","");//���ñ��ύ����
		}
		//����������Ϣ
		for (Map.Entry<String, String> ent : this.getMainRowInfo(billBean, pageBean).entrySet()) {
			modelStr = modelStr.replace(ent.getKey(), ent.getValue());
		}
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set managepage='X1X' where id="+billBean.getId();
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
	//�õ������е���Ϣ
	private Map<String, String> getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ���ϸ�ֶ�
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer trContent=new StringBuffer();
		StringBuffer dtContent=new StringBuffer();
		String sumNameStr="";//�ϼ��ֶ�
		String tmpTR="\n<TR>XTDcontentX</TR>\n";
		String tmpTD="\n<TD class=label>XnameX</TD>\n<TD class=alignl>\nXcontentX</TD>\n";
		String tmpStr="";//��ʱ��
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Edit");
			if("1".equals(viewBean.getIsview())){
				index++;
				if ("1".equals(fieldBean.getFieldhtmltype())&&"3,4,5".indexOf(fieldBean.getType())>-1) {
					sumNameStr=","+fieldBean.getFieldname();
				}
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldname()).replace("XcontentX", fieldContentUtil.getEditFieldInfo(fieldBean, viewBean, "_<%=index%>"));
				dtContent.append(tmpStr);//��ϸ���е�����
				if(index%4==0){
					trContent.append(tmpTR.replace("XTDcontentX", dtContent));
					dtContent=new StringBuffer();
				}
			}
		}
		for(int i=1;i<=(4-index%4)&&(index%4!=0);i++){
			tmpStr=tmpTD.replace("XnameX","&nbsp;").replace("\nXcontentX","&nbsp;");
			dtContent.append(tmpStr);//��ϸ���е�����
		}
		if(index%4!=0&&dtContent.length()>0){//�������
			trContent.append(tmpTR.replace("XTDcontentX", dtContent));
		}
		sumNameStr=sumNameStr.length()>0?sumNameStr.substring(1):"";
		map.put("${content}",trContent.toString());
		map.put("${sumname}",sumNameStr);
		return map;
	}
	//�����������
	private void CreateMainProc(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> beans =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ���ϸ�ֶ�
		RecordSet rs=new RecordSet();
		rs.setChecksql(false);
		boolean isoracle="oracle".equals(rs.getDBType())?true:false;
		String v_sql=isoracle?HtmlUtil.ORACLE_LEDIT:HtmlUtil.MSSQL_LEDIT;//���ݿ���̴������
		String fieldname="";
		String upfields="";
		String fields="";
		String args="";
		String args2="";
		
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : beans) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Edit");
			if("1".equals(viewBean.getIsview())){
				fieldname=fieldBean.getFieldname();
				if(isoracle){
					args=args+"v_"+fieldname+" varchar2,";
					fields=fields+","+fieldname;
					args2=args2+",v_"+fieldname;
					upfields=upfields+","+fieldname+"=v_"+fieldname;
				}else{
					args=args+"@"+fieldname+" varchar(200),";
					fields=fields+","+fieldname;
					args2=args2+",@"+fieldname;
					upfields=upfields+","+fieldname+"=@"+fieldname;
				}
			}
		}
		fields=fields.length()>0?fields.substring(1):fields;
		args2=args2.length()>0?args2.substring(1):args2;
		upfields=upfields.length()>0?upfields.substring(1):upfields;
		v_sql=v_sql.replace("XtableX", billBean.getTablename());
		v_sql=v_sql.replace("X1X", args);//�β�
		v_sql=v_sql.replace("X10X", upfields);
		v_sql=v_sql.replace("X11X", fields);//�ֶ�
		v_sql=v_sql.replace("X12X", args2);//����ֵ
		if(!isoracle){
			rs.executeSql("DROP PROCEDURE fla_XtableX_ledit".replace("XtableX", billBean.getTablename()));
		}
		rs.executeSql(v_sql);
	}
}
