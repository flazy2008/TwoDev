package two.general.util;

import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import weaver.conn.RecordSet;
import weaver.general.Util;

public class FieldContentUtil {
	String v_sql = "";
	private String fieldid = "";
	private String billid = "";// 表单ID
	private String fieldname = "";// 数据库字段名称
	private String pfieldname = "";// 数据库字段名称(原始的)
	private String fieldlabel = "";// 字段显示名
	private String fielddbtype = "";//数据字段类型如：int varchar2
	private String fieldhtmltype = "";// 字段表现形式 如：文本框，多行文本框
	private String type = "";//字段类型如文本框中的金额
	private String viewtype = "";//显示类型如：0表示主表
	private String detailtable = "";//明细表明
	private String textheight = "";//文本框的高度
	private String defvalue = "";//默认值如：user,deptid
	private String transmethod = "";//转换方法
	private String pageType="";//页面类型
	private String isview = "";// 是否显示
	private String isedit = "";// 是否编辑
	private String ismand = "";// 是否必须
	private String islog = "";// 是否记录操作日志
	private String isevent = "";// 是否有事件
	private void initFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String pageType,String suffix){
		this.fieldid = fieldBean.getFieldid();
		this.billid = fieldBean.getBillid();// 表单ID
		this.fieldname = fieldBean.getFieldname()+suffix;// 数据库字段名称
		this.pfieldname= fieldBean.getFieldname();// 数据库字段名称(原始的)
		this.fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		this.fielddbtype = fieldBean.getFielddbtype();//数据字段类型如：int varchar2
		this.fieldhtmltype = fieldBean.getFieldhtmltype();// 字段表现形式 如：文本框，多行文本框
		this.type = fieldBean.getType();//字段类型如文本框中的金额
		this.viewtype = fieldBean.getViewtype();//显示类型如：0表示主表
		this.detailtable = fieldBean.getDetailtable();//明细表明
		this.textheight = fieldBean.getTextheight();//文本框的高度
		this.defvalue = fieldBean.getDefaultvalue();//默认值如：user,deptid
		this.transmethod = fieldBean.getTransmethod();//转换方法
		this.pageType=pageType;//页面类型
		this.isview = viewBean.getIsview();// 是否显示
		this.isedit = viewBean.getIsedit();// 是否编辑
		this.ismand = viewBean.getIsmandatory();// 是否必须
		this.islog = viewBean.getIslog();// 是否记录操作日志
		this.isevent =viewBean.getIsevent();// 是否有事件
	}
	/**
	 * 得到值字段
	 */
	public StringBuffer getValueFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix,String prefix) {
		this.initFieldInfo(fieldBean, viewBean,"",suffix);//初始化
		String tmpStr = "";// 临时用
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//值
		String valueName="XvtX_"+pfieldname+"";
		String valueSpan="";//显示值  默认为实际值
		
		if(!"".equals(prefix)){
			valueStr=valueStr.replace("XvtX", prefix);//值
			valueName=valueName.replace("XvtX", prefix);
		}else if("1".equals(viewtype)){
			valueStr=valueStr.replace("XvtX", "v1");//值
			valueName=valueName.replace("XvtX", "v1");
		}else{
			valueStr=valueStr.replace("XvtX", "v");//值
			valueName=valueName.replace("XvtX", "v");
		}
		
		valueSpan=valueStr;//显示值  默认为实际值
		
		if("".equals(transmethod)&&"4".equals(fieldhtmltype)){
			transmethod="common.LookImgByType";
		}
		if(!"".equals(transmethod)){
			valueSpan = " <%=" + transmethod + "(" + valueName + ")" + "%> ";
		}
		
		if ("1,2,3,4,6".indexOf(fieldhtmltype)>-1){
			buffer.append(valueSpan);
		}else if (fieldhtmltype.equals("5")) { // 选择框 select开始
			tmpStr = " <%=common.LookNameBySelectID(" + valueName + ",\""+fieldid+"\")%> ";
			buffer.append(tmpStr);
		} else if (fieldhtmltype.equals("7")) {// 特殊字段
			String displayname = "";
			String linkaddress = "";
			String descriptivetext = "";
			v_sql = "select * from fla_specialfield where fieldid ="+ fieldid + " order by id ";
			rs.execute(v_sql);
			if (rs.next()) {
				displayname = Util.null2String(rs.getString("displayname"));
				linkaddress = Util.null2String(rs.getString("linkaddress"));
				descriptivetext = Util.null2String(rs.getString("descriptivetext"));
			}
			if (type.equals("1")) {
				valueSpan=HtmlUtil.ASTR.replace("${value}", linkaddress).replace("${text}", displayname);
			}else if (type.equals("2")) {
				valueSpan=descriptivetext;
			}
			buffer.append(valueSpan);
		}
		return buffer;
	}	
	/**
	 * 得到编辑页面字段
	 */
	public StringBuffer getViewFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"View",suffix);//初始化
		String tmpStr = "";// 临时用
		String htmlStr="";//html表单信息
		String spanStr="";//Span信息
		String mandStr = "";// 文本档必填事件
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//值
		String valueSpan="";//显示值
		String valueName="";
		if("".equals(transmethod)&&"4".equals(fieldhtmltype)){
			transmethod="common.LookImgByType";
		}
		if(!"".equals(transmethod)){
			valueSpan = " <%=" + transmethod + "(XvtX_" + pfieldname + ")" + "%> ";
		}else{
			valueSpan=valueStr;
		}
		if("1".equals(viewtype)){
			valueStr=valueStr.replace("XvtX", "v1");//值
			valueSpan=valueSpan.replace("XvtX", "v1");//显示
			valueName="v1_"+pfieldname;
		}else{
			valueStr=valueStr.replace("XvtX", "v");//值
			valueSpan=valueSpan.replace("XvtX", "v");//显示
			valueName="v_"+pfieldname;
		}
		if (fieldhtmltype.equals("1")) { // 单行文本框
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname);
			if ("2,3,4,5".indexOf(type)>-1) { // 单行文本框中的文本
				spanStr=spanStr.replace("${value}",valueSpan).replace("X1X", "myname=\""+pfieldname+"\"");
			}else{
				spanStr=spanStr.replace("${value}",valueSpan).replace("X1X", "");
			}
			buffer.append(spanStr+"\n");
			// 单行文本框条件结束
		}else if ("2,3,4".indexOf(fieldhtmltype)>-1) {// 多行文本框
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan).replace("X1X", "");
			buffer.append(spanStr+"\n");			
		}else if (fieldhtmltype.equals("5")) { // 选择框 select开始
			tmpStr = " <%=common.LookNameBySelectID(" + valueName + ",\""+fieldid+"\")%> ";
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",tmpStr).replace("X1X", "");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("6")) {// 附件上传
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan).replace("X1X", "");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// 特殊字段
			String displayname = "";
			String linkaddress = "";
			String descriptivetext = "";
			v_sql = "select * from fla_specialfield where fieldid ="+ fieldid + " order by id ";
			rs.execute(v_sql);
			if (rs.next()) {
				displayname = Util.null2String(rs.getString("displayname"));
				linkaddress = Util.null2String(rs.getString("linkaddress"));
				descriptivetext = Util.null2String(rs.getString("descriptivetext"));			
			}
			if (type.equals("1")) {
				tmpStr=HtmlUtil.ASTR.replace("${value}", linkaddress).replace("${text}", displayname);
			}else if (type.equals("2")) {
				tmpStr=descriptivetext;
			}
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",tmpStr).replace("X1X", "");
			buffer.append(spanStr+"\n");
		}
		return buffer;
	}	
	/**
	 * 得到编辑页面字段
	 */
	public StringBuffer getEditFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"Edit",suffix);//初始化
		String tmpStr = "";// 临时用
		String htmlStr="";//html表单信息
		String spanStr="";//Span信息
		String mandStr = "";// 文本档必填事件
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//值
		String valueSpan="";//显示值
		String valueName="";
		if(!"".equals(transmethod)){
			valueSpan = " <%=" + transmethod + "(XvtX_" + pfieldname + ")" + "%> ";
		}else{
			valueSpan=valueStr;
		}
		if("1".equals(viewtype)){
			valueStr=valueStr.replace("XvtX", "v1");//值
			valueSpan=valueSpan.replace("XvtX", "v1");//显示
			valueName="v1_"+pfieldname;
		}else{
			valueStr=valueStr.replace("XvtX", "v");//值
			valueSpan=valueSpan.replace("XvtX", "v");//显示
			valueName="v_"+pfieldname;
		}
		
		if("0".equals(isedit)&&!"7".equals(fieldhtmltype)){
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X", "").replace("${value}", valueStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", valueSpan);
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
			return buffer;
		}
		if ("1".equals(ismand)) {
			mandStr = " mand="+fieldhtmltype+" onChange=\""+ HtmlUtil.mandOnChange + "\"";
		}

		if (fieldhtmltype.equals("1")) { // 单行文本框
			htmlStr=HtmlUtil.INPUTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("${value}", valueStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			if (type.equals("1")) { // 单行文本框中的文本
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr);
				buffer.append(htmlStr+"\n");
			} else if (type.equals("2")) { // 单行文本框中的整型
				tmpStr = " onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr+tmpStr);
				htmlStr.replace("${size}", "10").replace("${maxlength}", "15");
				buffer.append(htmlStr+"\n");
			} else if (type.equals("3") ||type.equals("4") || type.equals("5")) { // 单行文本框中的浮点型
				tmpStr = " onKeyPress=\"ItemNum_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				if("".equals(mandStr)){
					tmpStr +=" onChange=\"theCountSum(this.myname);\""; 
				}else{
					mandStr=mandStr.replace("onChange=\"", "onChange=\"theCountSum(this.myname);");
				}
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "15").replace("X1X", mandStr+tmpStr);
				buffer.append(htmlStr+"\n");
			}
			buffer.append(spanStr+"\n");
			// 单行文本框条件结束
		}else if (fieldhtmltype.equals("2")) {// 多行文本框
			htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
			htmlStr=htmlStr.replace("${value}",valueStr).replace("X1X", mandStr);
			if (type.equals("2")) {//编辑器
				htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}",valueStr).replace("X1X", mandStr+" htmledit=1 ");
			}
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");			
		} else if (fieldhtmltype.equals("3")) { // 多行文本框条件结束 // 浏览按钮
			// 多行文本框结束
			if (type.equals("2")) {
				htmlStr=HtmlUtil.DATASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}", "");				
				buffer.append(htmlStr + "\n");
			}else{
				htmlStr=HtmlUtil.BUTTSTR.replace("${name}", fieldname).replace("${pname}", pfieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("X1X", "");
				buffer.append(htmlStr+"\n");
			}
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}", valueStr).replace("${title}", fieldlabel).replace("X1X", mandStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", valueSpan);
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("4")) {// 选择框开始
			tmpStr = "<%=\"1\".equals(" + valueName+ ")?\"checked\":\"\"%>";
			htmlStr=HtmlUtil.CHECKSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr.replace("onChange", "onClick")+tmpStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("5")) { // 选择框 select开始
			v_sql = "select * from fla_SelectItem where fieldid ="+ fieldid + " order by listorder,id ";
			String tmpvalue = "";
			String tempname = "";
			String selected = "<%=\"XsValX\".equals(" + valueName+ ")?\"selected\":\"\"%>";
			tmpStr = HtmlUtil.OPTIONSTR.replace("${value}", "-1").replace("X1X", selected.replace("XsValX", "-1")).replace("${text}", "--请选择--");
			rs.execute(v_sql);
			while (rs.next()) {
				tmpvalue = Util.null2String(rs.getString("selectvalue"));
				tempname = Util.null2String(rs.getString("selectname"));
				tmpStr += HtmlUtil.OPTIONSTR.replace("${value}",tmpvalue).replace("X1X",selected.replace("XsValX",tmpvalue)).replace("${text}", tempname);
			}
			htmlStr=HtmlUtil.SELECTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr);
			htmlStr=htmlStr.replace("${text}", tmpStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("6")) {// 附件上传
			htmlStr=HtmlUtil.FILESTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X","");
			htmlStr=htmlStr.replace("${text}", tmpStr);
			buffer.append(htmlStr+"\n");//文件域
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}",valueStr).replace("X1X","").replace("${title}", fieldlabel);//隐藏域
			buffer.append(htmlStr+"\n");
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan);
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// 特殊字段
			String displayname = "";
			String linkaddress = "";
			String descriptivetext = "";
			v_sql = "select * from fla_specialfield where fieldid ="+ fieldid + " order by id ";
			rs.execute(v_sql);
			if (rs.next()) {
				displayname = Util.null2String(rs.getString("displayname"));
				linkaddress = Util.null2String(rs.getString("linkaddress"));
				descriptivetext = Util.null2String(rs.getString("descriptivetext"));			
			}
			if (type.equals("1")) {
				tmpStr=HtmlUtil.ASTR.replace("${value}", linkaddress).replace("${text}", displayname);
			}else if (type.equals("2")) {
				tmpStr=descriptivetext;
			}
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}",tmpStr);
			buffer.append(spanStr+"\n");
		}
		return buffer;
	}
	/**
	 * 得到添加页面字段
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean) {
		return this.getAddFieldInfo(fieldBean, viewBean, "");
	}
	/**
	 * 得到添加页面字段
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"Add",suffix);//初始化
		String tmpStr = "";// 临时用
		String htmlStr="";//html表单信息
		String spanStr="";//Span信息
		String mandStr = "";// 文本档必填事件
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String tmpDefvalue="";//默认值
		String tmpDefSpanHtml="";//默认值
		if(!"".equals(defvalue)&&"1,3".indexOf(fieldhtmltype)>-1){
			tmpDefvalue="<%=v_"+pfieldname+"%>";
			if(!"".equals(transmethod)){
				tmpDefSpanHtml = " <%=" + transmethod + "(v_" + pfieldname + ")" + "%> ";
			}
		}
		if("0".equals(isedit)&&!"7".equals(fieldhtmltype)){
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X", "").replace("${value}", tmpDefvalue);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", tmpDefSpanHtml);
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
			return buffer;
		}
		if ("1".equals(ismand)) {
			mandStr = " mand="+fieldhtmltype+" onChange=\""+ HtmlUtil.mandOnChange + "\"";
		}

		if (fieldhtmltype.equals("1")) { // 单行文本框
			htmlStr=HtmlUtil.INPUTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("${value}", tmpDefvalue);
			if (type.equals("1")) { // 单行文本框中的文本
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr);
				buffer.append(htmlStr+"\n");
			} else if (type.equals("2")) { // 单行文本框中的整型
				tmpStr = " onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr+tmpStr);
				htmlStr.replace("${size}", "10").replace("${maxlength}", "15");
				buffer.append(htmlStr+"\n");
			} else if (type.equals("3") ||type.equals("4") || type.equals("5")) { // 单行文本框中的浮点型
				tmpStr = " onKeyPress=\"ItemNum_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				if("".equals(mandStr)){
					tmpStr +=" onChange=\"theCountSum(this.myname);\""; 
				}else{
					mandStr=mandStr.replace("onChange=\"", "onChange=\"theCountSum(this.myname);");
				}
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "15").replace("X1X", mandStr+tmpStr);
				buffer.append(htmlStr+"\n");
			}
			// 单行文本框条件结束
		}else if (fieldhtmltype.equals("2")) {// 多行文本框
			htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
			htmlStr=htmlStr.replace("${value}", "").replace("X1X", mandStr);
			if (type.equals("2")) {//编辑器
				htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}", "").replace("X1X", mandStr+" htmledit=1 ");
			}
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("3")) { // 多行文本框条件结束 // 浏览按钮
			// 多行文本框结束
			if (type.equals("2")) {
				htmlStr=HtmlUtil.DATASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}", "");				
				buffer.append(htmlStr + "\n");
			}else{
				htmlStr=HtmlUtil.BUTTSTR.replace("${name}", fieldname).replace("${pname}", pfieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("X1X", "");
				buffer.append(htmlStr+"\n");
			}
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}", tmpDefvalue).replace("${title}", fieldlabel).replace("X1X", mandStr);
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("4")) {// 选择框开始
			htmlStr=HtmlUtil.CHECKSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr.replace("onChange", "onClick"));
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("5")) { // 选择框 select开始
			v_sql = "select * from fla_SelectItem where fieldid ="+ fieldid + " order by listorder,id ";
			String tmpvalue = "";
			String tempname = "";
			String isdefault = "";
			tmpStr = HtmlUtil.OPTIONSTR.replace("${value}", "-1").replace("X1X", "").replace("${text}", "--请选择--");
			rs.execute(v_sql);
			while (rs.next()) {
				tmpvalue = Util.null2String(rs.getString("selectvalue"));
				tempname = Util.null2String(rs.getString("selectname"));
				isdefault = Util.null2String(rs.getString("isdefault"));
				tmpStr += HtmlUtil.OPTIONSTR.replace("${value}",tmpvalue).replace("X1X", isdefault.replace("n", "").replace("y","selected")).replace("${text}", tempname);
			}
			htmlStr=HtmlUtil.SELECTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr);
			htmlStr=htmlStr.replace("${text}", tmpStr);
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("6")) {// 附件上传
			htmlStr=HtmlUtil.FILESTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X","");
			htmlStr=htmlStr.replace("${text}", tmpStr);
			buffer.append(htmlStr+"\n");//文件域
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}", "").replace("X1X","").replace("${title}", fieldlabel);//隐藏域
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// 特殊字段
			String displayname = "";
			String linkaddress = "";
			String descriptivetext = "";
			v_sql = "select * from fla_specialfield where fieldid ="+ fieldid + " order by id ";
			rs.execute(v_sql);
			if (rs.next()) {
				displayname = Util.null2String(rs.getString("displayname"));
				linkaddress = Util.null2String(rs.getString("linkaddress"));
				descriptivetext = Util.null2String(rs.getString("descriptivetext"));			
			}
			if (type.equals("1")) {
				tmpStr=HtmlUtil.ASTR.replace("${value}", linkaddress).replace("${text}", displayname);
			}else if (type.equals("2")) {
				tmpStr=descriptivetext;
			}
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}",tmpStr);
		}
		if("".equals(spanStr)){
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}",tmpDefSpanHtml);
		}
		buffer.append(spanStr+"\n");
		return buffer;
	}
}
