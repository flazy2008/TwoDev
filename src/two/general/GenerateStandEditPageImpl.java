package two.general;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

public class GenerateStandEditPageImpl implements IGeneratePage {
	public int index = 0;	
	GenerateCommon gener = new GenerateCommon();	
	GenerateEditPageImpl editPageImpl=new GenerateEditPageImpl();

	@Override
	public void CreateProc(String billid,BillBean billBean, String pageType) {
		RecordSet  rs=new RecordSet();
		editPageImpl.CreateProc(billid, billBean, pageType);
	}	
	@Override
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean) {
		editPageImpl.GenerateOperation(beans, billBean, pageBean);
	}
	/**
	 * 得到其它信息
	 */
	public Map<String, String> getOtherInfo(List<FieldInfoBean> beans) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer fieldStr = new StringBuffer();
		StringBuffer fieldRS = new StringBuffer();
		String tmpStr1="\tString  v_XnameX =\"\";//XmarkX\n";
		String tmpStr2="\tv_XnameX = Util.null2String(RecordSet.getString(\"XnameX\"));//XmarkX\n";		
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {
				fieldStr.append(tmpStr1.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
				fieldRS.append(tmpStr2.replace("XnameX", fieldBean.getFieldname()).replace("XmarkX", fieldBean.getFieldlabel()));
			}
		}
		map.put("${fieldStr}", "\n"+fieldStr.toString());
		map.put("${fieldRS}", "\n"+fieldRS.toString());
		return map;
	}
	@Override
	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean) {
		Map<String, String> map = null;
		String str = null;
		RecordSet rs = new RecordSet();
		String v_sql = "";
		StringBuffer initStr = new StringBuffer();// 其它初始化信息
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {// 编辑时显示
				buffer.append(this.getTRInfo(fieldBean, formBean, pageBean));
				index++;
			}
		}
		String mainView=pageBean.getMainviewtype();		
		String fileName =gener.getFilePath("modelPage","Stand_Edit.jsp");
		String toFile =gener.getFilePath("GeneratePage",billBean.getTablename(),"Stand_Edit.jsp");
		str = gener.readFile(fileName);
		initStr.append(buffer);
		str=str.replace("${titlename}", billBean.getNamelabel());
		str = str.replace("${content}", initStr);
		str = str.replace("${fla_billid}", billBean.getId());//表单ID
		str = str.replace("${XTableX}", billBean.getTablename());//表单名称
		map = this.getOtherInfo(beans);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		str = str.replace("${browse}", gener.getBrowse(beans));//浏览框
		gener.writeFile(toFile, str);
		v_sql="update fla_bill set managepage='X1X' where id="+billBean.getId();
		v_sql=v_sql.replace("X1X", toFile.replace(GCONST.getRootPath()+"", File.separatorChar+""));
		rs.executeSql(v_sql);
		return buffer;	
		}
	/**
	 * 得到表信息
	 */
	public StringBuffer getTRInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean) {
		StringBuffer buffer = new StringBuffer();
		GenerateFieldContent fieldContent = new GenerateFieldContent();
		String tmpStr="\t<TD width=\"10%\" class=\"label\">X1X</TD>\n\t<TD width=\"15%\" class=\"tdalign\">\n\t\tX2X\t</TD>\n";
		String trStart = "\n<TR>\n";
		String trEnd = "</TR>\n";
		String valueStr = "";
		String Suffix = "";
		String fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		tmpStr=tmpStr.replace("X1X", fieldlabel).replace("X2X", fieldContent.getEditFieldInfo(fieldBean, formBean,
				pageBean, valueStr, Suffix));
		if (index % 4 == 0) {
			buffer.append(trStart);
		}		
		buffer.append(tmpStr);
		if ((index+1) % 4 == 0) {
			buffer.append(trEnd);
		}		
		return buffer;
	}	
	@Override
	public void GeneratePage(String billid,BillBean billBean, String pageType) {
		List<FieldInfoBean> beans = new FieldInfoBean(billid, pageType, "0,1").getList();
		PageInfoBean pageBean = new PageInfoBean(billid, pageType);
		this.GenerateMainInfo(beans, billBean, pageBean);//生成主表信息
	}
}
