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

public class EditBasicPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//字段信息
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		Map<String, String> map = new HashMap<String, String>();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Operation.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","BasicOperation.jsp");//模板文件
		if(gener.isExistFile(toFile)){//已存在这个文件
			fromfile=toFile;
		}
		String modelStr =gener.readFile(fromfile);//模板中的内容
		String hasupload=billBean.getHasfileup();//有附件
		//替换内容
		modelStr=modelStr.replace("${Edit_procName}", "fla_"+billBean.getTablename()+"_edit");
		modelStr=modelStr.replace("${XtableX}", billBean.getTablename());
		if("1".equals(hasupload)){//存在附件
			modelStr=modelStr.replace("//${upload}",HtmlUtil.OPER_FILEUPLOAD);
			modelStr=modelStr.replace("request.", "fu.");
		}else{
			modelStr=modelStr.replace("//${upload}","");
		}
		modelStr=modelStr.replace("${mainrequest}",gener.getMainRequestStr(billBean, pageBean,false));//得到页面请求信息
		modelStr=modelStr.replace("${Edit_procPara}",gener.getProcParaStr(billBean, pageBean,false));//得到存储过程信息
		modelStr=modelStr.replace("${Edit_log}", gener.getWriteLogInfo(billBean, pageBean,false));//写日志

		gener.writeFile(toFile, modelStr);
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
		this.CreateMainProc(billBean, pageBean);//生成主表过程
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Edit.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","BasicEdit.jsp");//模板文件
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
		if("1".equals(hasupload)){//存在附件
			modelStr = modelStr.replace("${enctype}","enctype=\"multipart/form-data\"");//设置表单提交类型
		}else{
			modelStr = modelStr.replace("${enctype}","");//设置表单提交类型
		}
		if("1".equals(maviewtype)){//单列显示
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP1);//表格样式
		}else{//两列显示
			modelStr = modelStr.replace("${colgroup}", HtmlUtil.ADD_COLGROUP2);//表格样式
		}
		modelStr = modelStr.replace("${colSpan}",""+2*Util.getIntValue(maviewtype, 1));
		modelStr = modelStr.replace("${content}",this.getMainRowInfo(billBean, pageBean));//表单内容
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//表单内容

		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set managepage='X1X' where id="+billBean.getId();
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
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Edit");
			if("1".equals(viewBean.getIsview())){
				index++;
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldlabel());
				tmpStr=tmpStr.replace("XcontentX", fieldContentUtil.getEditFieldInfo(fieldBean, viewBean, ""));
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
	
	//创建主表过程
	private void CreateMainProc(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> beans =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到主表字段
		RecordSet rs=new RecordSet();
		rs.setChecksql(false);
		boolean isoracle="oracle".equals(rs.getDBType())?true:false;
		String v_sql=isoracle?HtmlUtil.ORACLE_EDIT:HtmlUtil.MSSQL_EDIT;//数据库过程创建语句
		String fieldname="";
		String fields="";
		String args="";
		
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : beans) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Edit");
			if("1".equals(viewBean.getIsview())){
				fieldname=fieldBean.getFieldname();
				if(isoracle){
					args=args+"v_"+fieldname+" varchar2,";
					fields=fields+","+fieldname+"=v_"+fieldname;
				}else{
					args=args+"@"+fieldname+" varchar(200),";
					fields=fields+","+fieldname+"=@"+fieldname;
				}
			}
		}
		fields=fields.length()>0?fields.substring(1):fields;
		v_sql=v_sql.replace("XtableX", billBean.getTablename());
		v_sql=v_sql.replace("X1X", args);//形参
		v_sql=v_sql.replace("X11X", fields);//字段	
		if(!isoracle){
			rs.executeSql("DROP PROCEDURE fla_XtableX_edit".replace("XtableX", billBean.getTablename()));
		}
		rs.executeSql(v_sql);
	}
}
