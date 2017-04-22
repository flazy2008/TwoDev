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
import weaver.conn.RecordSet;
import weaver.general.GCONST;

public class ViewStandPageImpl implements ICreatePage {
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
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Stand_View.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","Stand_View.jsp");//ģ���ļ�
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
		modelStr = modelStr.replace("//${argsStr}",gener.getArgsFieldInfo(billBean, pageBean,true));//��ѯ����
		modelStr = modelStr.replace("//${sqlwhere}",gener.getSQLWhereInfo(billBean, pageBean));//��ѯ����

		for (Map.Entry<String, String> ent : this.getMainRowInfo(billBean, pageBean).entrySet()) {
			modelStr = modelStr.replace(ent.getKey(), ent.getValue());
		}
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set viewpage='X1X' where id="+billBean.getId();
		v_sql=v_sql.replace("X1X", toFile.replace(GCONST.getRootPath()+"", File.separatorChar+""));
		rs.executeSql(v_sql);
	}

	@Override
	public void MainInfo(BillBean billBean, PageInfoBean pageBean) {
		this.CreatePage(billBean, pageBean);//�������ļ�
	
	}
	//�õ������е���Ϣ
	private Map<String, String> getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//�õ���ϸ�ֶ�
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer trContent=new StringBuffer();
		StringBuffer dtContent=new StringBuffer();
		String sumNameStr="";//�ϼ��ֶ�
		String parameter="";//������Ϣ
		String tmpTR="\n<TR>XTDcontentX</TR>\n";
		String tmpTD="\n<TD class=label>XnameX</TD>\n<TD class=alignl>\nXcontentX</TD>\n";
		String tmpStr="";//��ʱ��
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "View");
			if("1".equals(viewBean.getIsview())){
				index++;
				if ("1".equals(fieldBean.getFieldhtmltype())&&"3,4,5".indexOf(fieldBean.getType())>-1) {
					sumNameStr=","+fieldBean.getFieldname();
				}
				if("1".equals(viewBean.getIsmandatory())){
					parameter+= "&XnameX=\"+p_XnameX+\"".replace("XnameX", fieldBean.getFieldname());
				}
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldname()).replace("XcontentX", fieldContentUtil.getViewFieldInfo(fieldBean, viewBean, "_<%=index%>"));
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
		parameter=parameter.length()>0?parameter.substring(1):"";
		map.put("${parameter}",parameter);
		map.put("${content}",trContent.toString());
		map.put("${sumname}",sumNameStr);
		return map;
	}
}
