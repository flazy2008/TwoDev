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

public class AddStandPageImpl implements ICreatePage {
	FieldContentUtil fieldContentUtil=new FieldContentUtil();//字段信息
	@Override
	public void CreateOper(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		Map<String, String> map = new HashMap<String, String>();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"List_Operation.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","List_Operation.jsp");//模板文件
		String modelStr =gener.readFile(fromfile);//模板中的内容
		String hasupload=billBean.getHasfileup();//有附件
		//替换内容
		modelStr=modelStr.replace("${Add_procName}", "fla_"+billBean.getTablename()+"_ladd");
		modelStr=modelStr.replace("${XtableX}", billBean.getTablename());
		if("1".equals(hasupload)){//存在附件
			modelStr=modelStr.replace("//${upload}",HtmlUtil.OPER_FILEUPLOAD);
			modelStr=modelStr.replace("request.", "fu.");
		}else{
			modelStr=modelStr.replace("//${upload}","");
		}
		modelStr=modelStr.replace("//${fieldStr}",gener.getFieldStr(billBean, pageBean,"0").replace("\t", ""));//定义字段
		modelStr=modelStr.replace("${Add_mainrequest}",gener.getMainRequestStr(billBean, pageBean,true));//得到页面请求信息
		modelStr=modelStr.replace("${Add_procPara}",gener.getProcParaStr(billBean, pageBean,true));//得到存储过程信息
		modelStr=modelStr.replace("${Add_procName}", "fla_"+billBean.getTablename()+"_ladd");
		modelStr=modelStr.replace("${Add_log}", gener.getWriteLogInfo(billBean, pageBean,true));//写日志
		
		gener.writeFile(toFile, modelStr);
	}

	@Override
	public void CreateProc(BillBean billBean, PageInfoBean pageBean) {
		this.CreateMainProc(billBean, pageBean);//生成主表过程
	}

	@Override
	public void CreatePage(BillBean billBean, PageInfoBean pageBean) {
		GenerateUtil gener=new GenerateUtil();
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Stand_Add.jsp");//要写入的文件
		String fromfile = gener.getFilePath("modelPage","Stand_Add.jsp");//模板文件
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
		modelStr = modelStr.replace("${deffield}",gener.getDefFieldInfo(billBean, pageBean));//默认值设置
		modelStr = modelStr.replace("${browse}",gener.getBrowse(billBean, pageBean));//表单内容
		if("1".equals(hasupload)){//存在附件
			modelStr = modelStr.replace("${enctype}","enctype=\"multipart/form-data\"");//设置表单提交类型
		}else{
			modelStr = modelStr.replace("${enctype}","");//设置表单提交类型
		}
		//主表其它信息
		for (Map.Entry<String, String> ent : this.getMainRowInfo(billBean, pageBean).entrySet()) {
			modelStr = modelStr.replace(ent.getKey(), ent.getValue());
		}
		gener.writeFile(toFile, modelStr);
		v_sql="update fla_bill set createpage='X1X' where id="+billBean.getId();
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
	private Map<String, String> getMainRowInfo(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> list =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到明细字段
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer trContent=new StringBuffer();
		StringBuffer dtContent=new StringBuffer();
		String sumNameStr="";//合计字段
		String tmpTR="\n<TR>XTDcontentX</TR>\n";
		String tmpTD="\n<TD class=label>XnameX</TD>\n<TD class=alignl>\nXcontentX</TD>\n";
		String tmpStr="";//临时用
		int index=0;
		FieldViewBean viewBean = null;
		for (FieldInfoBean fieldBean : list) {
			viewBean=new FieldViewBean(fieldBean.getFieldid(), "Add");
			if("1".equals(viewBean.getIsview())){
				index++;
				if ("1".equals(fieldBean.getFieldhtmltype())&&"3,4,5".indexOf(fieldBean.getType())>-1) {
					sumNameStr=","+fieldBean.getFieldname();
				}
				tmpStr=tmpTD.replace("XnameX", fieldBean.getFieldname()).replace("XcontentX", fieldContentUtil.getAddFieldInfo(fieldBean, viewBean,"_1"));
				dtContent.append(tmpStr);//明细表列的内容
				if(index%4==0){
					trContent.append(tmpTR.replace("XTDcontentX", dtContent));
					dtContent=new StringBuffer();
				}
			}
		}
		for(int i=1;i<=(4-index%4)&&(index%4!=0);i++){
			tmpStr=tmpTD.replace("XnameX","&nbsp;").replace("\nXcontentX","&nbsp;");
			dtContent.append(tmpStr);//明细表列的内容
		}
		if(index%4!=0&&dtContent.length()>0){//补充空列
			trContent.append(tmpTR.replace("XTDcontentX", dtContent));
		}
		sumNameStr=sumNameStr.length()>0?sumNameStr.substring(1):"";
		map.put("${content}",trContent.toString());
		map.put("${sumname}",sumNameStr);
		return map;
	}
	//创建主表过程
	private void CreateMainProc(BillBean billBean, PageInfoBean pageBean) {
		List<FieldInfoBean> beans =new FieldInfoBean().getAllFields(billBean.getId(), "0");//得到明细字段
		RecordSet rs=new RecordSet();
		rs.setChecksql(false);
		boolean isoracle="oracle".equals(rs.getDBType())?true:false;
		String v_sql=isoracle?HtmlUtil.ORACLE_LADD:HtmlUtil.MSSQL_LADD;//数据库过程创建语句
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
		v_sql=v_sql.replace("X1X", args);//形参
		v_sql=v_sql.replace("X11X", fields);//字段
		v_sql=v_sql.replace("X12X", args2);//变量值
		if(!isoracle){
			rs.executeSql("DROP PROCEDURE fla_XtableX_ladd".replace("XtableX", billBean.getTablename()));
		}
		rs.executeSql(v_sql);
	}
}
