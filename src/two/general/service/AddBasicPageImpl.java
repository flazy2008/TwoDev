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

public class AddBasicPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//�ֶ���Ϣ
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		Map<String, String> map = new HashMap<String, String>();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Operation.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","BasicOperation.jsp");//ģ���ļ�
		String modelStr =gener.readFile(fromfile);//ģ���е�����
		String hasupload=billBean.getHasfileup();//�и���
		//�滻����
		modelStr=modelStr.replace("${Add_procName}", "fla_"+billBean.getTablename()+"_add");
		modelStr=modelStr.replace("${XtableX}", billBean.getTablename());
		if("1".equals(hasupload)){//���ڸ���
			modelStr=modelStr.replace("//${upload}",HtmlUtil.OPER_FILEUPLOAD);
			modelStr=modelStr.replace("request.", "fu.");
		}else{
			modelStr=modelStr.replace("//${upload}","");
		}
		modelStr=modelStr.replace("${mainrequest}",gener.getMainRequestStr(billBean, pageBean,false));//�õ�ҳ��������Ϣ
		modelStr=modelStr.replace("//${Add_procPara}",gener.getProcParaStr(billBean, pageBean,false));//�õ��洢������Ϣ
		modelStr=modelStr.replace("${Add_log}", gener.getWriteLogInfo(billBean, pageBean,false));//д��־

		gener.writeFile(toFile, modelStr);
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
		this.CreateMainProc(billBean, pageBean);//������������
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Add.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","BasicAdd.jsp");//ģ���ļ�
		String modelStr =gener.readFile(fromfile);//ģ���е�����
		String hasupload=billBean.getHasfileup();//�и���
		String maviewtype=pageBean.getMainviewtype();//�õ�ҳ�����ʾ
		RecordSet rs=new RecordSet();
		String v_sql="";
		//�滻���ݿ�ʼ
		modelStr = modelStr.replace("${titlename}", billBean.getNamelabel());
		modelStr = modelStr.replace("${fla_billid}", billBean.getId());//����ID
		modelStr = modelStr.replace("${XTableX}", billBean.getTablename());//��������
		modelStr = modelStr.replace("${shareinfo}",gener.getShareInfo(billBean, pageBean));//Ȩ����Ϣ
		modelStr = modelStr.replace("${deffield}",gener.getDefFieldInfo(billBean, pageBean));//Ĭ��ֵ����
		if("1".equals(hasupload)){//���ڸ���
			modelStr = modelStr.replace("${enctype}","enctype=\"multipart/form-data\"");//���ñ����ύ����
		}else{
			modelStr = modelStr.replace("${enctype}","");//���ñ����ύ����
		}
		if("1".equals(maviewtype)){//������ʾ
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP1);//������ʽ
		}else{//������ʾ
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP2);//������ʽ
		}
		modelStr = modelStr.replace("${colSpan}",""+2*Util.getIntValue(maviewtype, 1));
		modelStr = modelStr.replace("${content}",this.getMainRowInfo(billBean, pageBean));//��������
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//��������
 
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set createpage='X1X' where id="+billBean.getId();
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
	private String getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ������ֶ�
		int maviewtype=Util.getIntValue(pageBean.getMainviewtype(),1);//�õ�ҳ�����ʾ
		StringBuffer restr=new StringBuffer();
		StringBuffer trContent=new StringBuffer();
		String tmpTR="<TR>\nXTDcontentX</TR><TR><TD class=Line colspan="+maviewtype*2+"></TD></TR>\n\n";
		String tmpTD="<TD>XnameX</TD>\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//��ʱ��
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Add");
			if("1".equals(viewBean.getIsview())){
				index++;
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldlabel());
				tmpStr=tmpStr.replace("XcontentX", fieldContentUtil.getAddFieldInfo(fieldBean, viewBean));
				trContent.append(tmpStr);
				if(index%(maviewtype)==0){
					restr.append(tmpTR.replace("XTDcontentX", trContent));
					trContent=new StringBuffer();
				}
			}
		}
		if(index%(maviewtype)!=0&&trContent.length()>0){
			trContent.append(tmpTD.replace("XnameX", "&nbsp;").replace("\nXcontentX", "&nbsp;"));
			restr.append(tmpTR.replace("XTDcontentX", trContent));
			trContent=new StringBuffer();
		}
		return restr.toString();
	}
	//������������
	private void CreateMainProc(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> beans =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ������ֶ�
		RecordSet rs=new RecordSet();
		rs.setChecksql(false);
		boolean isoracle="oracle".equals(rs.getDBType())?true:false;
		String v_sql=isoracle?HtmlUtil.ORACLE_ADD:HtmlUtil.MSSQL_ADD;//���ݿ���̴������
		String fieldname="";
		String fields="";
		String args="";
		String args2="";
		
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : beans) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Add");
			if("1".equals(viewBean.getIsview())){
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
		fields=fields.length()>0?fields.substring(1):fields;
		args2=args2.length()>0?args2.substring(1):args2;
		v_sql=v_sql.replace("XtableX", billBean.getTablename());
		v_sql=v_sql.replace("X1X", args);//�β�
		v_sql=v_sql.replace("X11X", fields);//�ֶ�
		v_sql=v_sql.replace("X12X", args2);//����ֵ
		if(!isoracle){
			rs.executeSql("DROP PROCEDURE fla_XtableX_add".replace("XtableX", billBean.getTablename()));
		}
		rs.executeSql(v_sql);
	}
}