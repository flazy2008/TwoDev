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

public class ViewDetailPageImpl implements ICreatePage {
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
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"View.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","DetailView.jsp");//模板文件
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
		modelStr=modelStr.replace("//${fieldStr}",gener.getFieldStr(billBean, pageBean,"0").replace("\t", ""));//定义字段
		modelStr=modelStr.replace("//${fieldRecord}",gener.getFieldRecordStr(billBean, pageBean,"0"));//定义记录集
		modelStr = modelStr.replace("//${argsStr}",gener.getArgsFieldInfo(billBean, pageBean,false));//查询条件
		modelStr = modelStr.replace("${parameter}",gener.getParameterInfo(billBean, pageBean,false));//参数
		if("1".equals(maviewtype)){//单列显示
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP1);//表格样式
		}else{//两列显示
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP2);//表格样式
		}
		modelStr = modelStr.replace("${colSpan}",""+2*Util.getIntValue(maviewtype, 1));
		modelStr = modelStr.replace("${content}",this.getMainRowInfo(billBean, pageBean));//表单内容
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//表单内容
		

		//明细表信息
		modelStr=modelStr.replace("//${dtfieldStr}",gener.getFieldStr(billBean, pageBean,"1").replace("\t", ""));//定义字段
		modelStr=modelStr.replace("//${dtfieldRecord}",gener.getFieldRecordStr(billBean, pageBean,"1"));//定义记录集
		for (Map.Entry<String, String> ent : this.getOtherInfo(billBean, pageBean).entrySet()) {
			modelStr = modelStr.replace(ent.getKey(), ent.getValue());
		}
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set viewpage='X1X' where id="+billBean.getId();
		v_sql=v_sql.replace("X1X", toFile.replace(GCONST.getRootPath()+"", File.separatorChar+""));
		rs.executeSql(v_sql);
	}

	@Override
	public void MainInfo(BillBean billBean, PageInfoBean pageBean) {
		this.CreatePage(billBean, pageBean);//生成主文件
	
	}
	//得到主表行的信息
	private String getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到主表字段
		int maviewtype=Util.getIntValue(pageBean.getMainviewtype(),1);//得到页面的显示
		StringBuffer restr=new StringBuffer();
		StringBuffer trContent=new StringBuffer();
		String tmpTR="<TR>\nXTDcontentX</TR><TR><TD class=Line colspan="+maviewtype*2+"></TD></TR>\n\n";
		String tmpTD="<TD>XnameX</TD>\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//临时用
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "View");
			if("1".equals(viewBean.getIsview())){
				index++;
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldlabel());
				tmpStr=tmpStr.replace("XcontentX", fieldContentUtil.getViewFieldInfo(fieldBean, viewBean, ""));
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
	//得到基本的标签信息
	private Map<String, String> getOtherInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "1");//得到明细字段
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer dtContent=new StringBuffer();
		String dtsumtdStr="";//小计列
		String dtthStr="";//标题列
		String sumNameStr="";//合同字段
		String tmpTD="\n<TD class=Field>\nXcontentX</TD>\n";
		String tmpStr="";//临时用
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Edit");
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
				dtContent.append(tmpStr);//明细表列的内容
			}
		}
		sumNameStr=sumNameStr.length()>0?sumNameStr.substring(1):"";
		map.put("${dtcolSpan}",(index+1)+"");
		map.put("${dtth}",dtthStr);
		map.put("${dtcontent}",dtContent.toString());
		map.put("${sumname}",sumNameStr);
		map.put("${dtsumtd}",dtsumtdStr);
		return map;
	}
}
