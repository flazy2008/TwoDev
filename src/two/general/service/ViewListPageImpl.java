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

public class ViewListPageImpl implements ICreatePage {
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
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_View.jsp");//Ҫд����ļ�
		String fromfile = gener.getFilePath("modelPage","List_View.jsp");//ģ���ļ�
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
		StringBuffer dtContent=new StringBuffer();
		String parameter="";//������Ϣ
		String dtsumtdStr="";//С����
		String dtthStr="";//������
		String sumNameStr="";//�ϼ��ֶ�
		String tmpTD="\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//��ʱ��
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "View");
			if("1".equals(viewBean.getIsview())){
				index++;
				dtthStr+="\t<TH>XnameX</TH>\n".replace("XnameX", fieldBean.getFieldlabel());
				if ("1".equals(fieldBean.getFieldhtmltype())&&"3,4,5".indexOf(fieldBean.getType())>-1) {
					sumNameStr=","+fieldBean.getFieldname();
					dtsumtdStr+="\t<TD><span class=\"fontred\" id=\"SpanSum"+ fieldBean.getFieldname() + "\"></span></TD>\n";
				}else{
					dtsumtdStr+="\t<TD></TD>\n";
				}
				tmpStr=tmpTD.replace("XcontentX", fieldContentUtil.getViewFieldInfo(fieldBean, viewBean, "_<%=index%>"));
				dtContent.append(tmpStr);//��ϸ���е�����
				if("1".equals(viewBean.getIsmandatory())){
					parameter+= "&XnameX=\"+p_XnameX+\"".replace("XnameX", fieldBean.getFieldname());
				}
			}
		}
		sumNameStr=sumNameStr.length()>0?sumNameStr.substring(1):"";
		parameter=parameter.length()>0?parameter.substring(1):"";
		map.put("${parameter}",parameter);
		map.put("${colSpan}",(index+2)+"");
		map.put("${dtth}",dtthStr);
		map.put("${content}",dtContent.toString());
		map.put("${sumname}",sumNameStr);
		map.put("${sumtd}",dtsumtdStr);
		return map;
	}
}
