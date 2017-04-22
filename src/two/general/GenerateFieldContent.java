package two.general;

import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.workflow.field.BrowserComInfo;

public class GenerateFieldContent {
	GenerateCommon gener = new GenerateCommon();

	/**
	 * 得到添加页面字段
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// 字段ID
		String isview = formBean.getIsview();// 是否编辑
		String isedit = formBean.getIsedit();// 是否编辑
		String ismand = formBean.getIsmandatory();// 是否必填
		String isevent = formBean.getIsevent();// 是否有事件
		String fieldname = fieldBean.getFieldname();// 字段名称
		String fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		String textheight = fieldBean.getTextheight();// 显示高度
		String imgheight = fieldBean.getImgheight();// 图片高度
		String imgwidth = fieldBean.getImgwidth();// 图片宽度
		String defvalue = fieldBean.getDefaultvalue();// 默认值
		String transmethod = fieldBean.getTransmethod();// 转换方法
		String pageType = "Add";
		String fieldname1 = fieldname;
		// fieldname=fieldname+Suffix;//名字后缀
		String v_sql = "";
		defvalue = "".equals(defvalue) ? "\"\"" : defvalue;
		RecordSet rs = new RecordSet();
		if (defvalue.length() >= 4) {
			valueStr = "value=\"<%=v_" + defvalue + "%>\"";
		}
		valueStr = "".equals(valueStr) ? "value=\"\"" : valueStr;
		String tmpStr = "";// 临时用
		String mandStr = gener.getMandStr(ismand, fieldBean);// 必填信息
		String eventStr = " X1X=\"XLX"
				+ fieldname.replaceFirst(fieldname.substring(0, 1), fieldname
						.substring(0, 1).toUpperCase()) + "(this.id)\" ";// 事件
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 输入框
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 文件框	
		String hiddStr = "<input type=hidden  X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// 隐藏框
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// 按钮
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// 日期按钮
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" >\n</textarea>";// 多行文本框
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// 选择框
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// 下拉框

		if ("1".equals(isview)&&"0".equals(isedit)) {// 只显示
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// 循环开始
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // 单行文本框
				String mandChange = "";// 文本档必填事件
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // 单行文本框中的文本
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // 单行文本框中的整型
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // 单行文本框中的浮点型
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("4")) {
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				}
			}// 单行文本框条件结束
			else if (fieldType.equals("2")) {// 多行文本框
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // 多行文本框条件结束 // 浏览按钮
				// (涉及workflow_browserurl表)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
				String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
				// 多行文本框结束
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// 选择框开始
				buffer.append(checkStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // 选择框 select开始
				v_sql = "select * from fla_SelectItem where fieldid ="
						+ fieldid + " order by listorder,id ";
				String tmpvalue = "";
				String tempname = "";
				String isdefault = "";
				tmpStr = "\t\t\t<option value=\"-1\" ></option>\n";
				rs.execute(v_sql);
				while (rs.next()) {
					tmpvalue = Util.null2String(rs.getString("selectvalue"));
					tempname = Util.null2String(rs.getString("selectname"));
					isdefault = Util.null2String(rs.getString("isdefault"));
					tmpStr += "\t\t\t<option value=\""
							+ tmpvalue
							+ "\" "
							+ isdefault.replace("n", "").replace("y",
									"selected") + " >" + tempname
							+ "</option>\n";
				}
				buffer.append(selectStr.replace("XSX", mandStr).replace("X1X",
						tmpStr)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("6")) {// 附件上传
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");				
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// 特殊字段
				buffer.append(inputStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			}
		}
		return buffer;
	}

	/**
	 * 得到编辑页面字段
	 */
	public StringBuffer getEditFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// 字段ID
		String isedit = formBean.getIsedit();// 是否编辑
		String isview = formBean.getIsview();// 是否编辑
		String ismand = formBean.getIsmandatory();// 是否必填
		String isevent = formBean.getIsevent();// 是否有事件
		String fieldname = fieldBean.getFieldname();// 字段名称
		String fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		String textheight = fieldBean.getTextheight();// 显示高度
		String imgheight = fieldBean.getImgheight();// 图片高度
		String imgwidth = fieldBean.getImgwidth();// 图片宽度
		String defvalue = fieldBean.getDefaultvalue();// 默认值
		String transmethod = fieldBean.getTransmethod();// 转换方法
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// 名字后缀
		String v_sql = "";
		defvalue = "";
		String pageType = "Edit";
		RecordSet rs = new RecordSet();
		valueStr = "".equals(valueStr) ? "value=\"<%=v_" + fieldname1 + "%>\""
				: valueStr;
		String tmpStr = "";// 临时用
		String mandStr = gener.getMandStr(ismand, fieldBean);// 必填信息
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// 事件
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 输入框
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 文件框		

		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// 隐藏框
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// 按钮
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// 日期按钮		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" ><%=v_" + fieldname1
				+ "%></textarea>";// 多行文本框
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// 选择框
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// 下拉框

		if ("1".equals(isview)&&"0".equals(isedit)) {// 只显示
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// 循环开始
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // 单行文本框
				String mandChange = "";// 文本档必填事件
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // 单行文本框中的文本
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // 单行文本框中的整型
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // 单行文本框中的浮点型
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("4")) {
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				}
			}// 单行文本框条件结束
			else if (fieldType.equals("2")) {// 多行文本框
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // 多行文本框条件结束 // 浏览按钮
				// (涉及workflow_broswerurl表)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
				String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
				// 多行文本框结束
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
					defvalue="" + fieldname1 + "  ";
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// 选择框开始
				tmpStr = "<%=\"1\".equals(v_" + fieldname1
					+ ")?\"checked\":\"\"%>";
				buffer.append(checkStr.replace("X1X", mandStr+tmpStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // 选择框 select开始
				v_sql = "select * from fla_SelectItem where fieldid ="
						+ fieldid + " order by listorder,id ";
				String tmpvalue = "";
				String tempname = "";
				String isdefault = "";
				String select = "<%=\"X1X\".equals(v_" + fieldname1
						+ ")?\"selected\":\"\"%>";
				tmpStr = "\t\t\t<option value=\"-1\" "
						+ select.replace("X1X", "-1") + "></option>\n";
				rs.execute(v_sql);
				while (rs.next()) {
					select = "<%=\"X1X\".equals(v_" + fieldname1
							+ ")?\"selected\":\"\"%>";
					tmpvalue = Util.null2String(rs.getString("selectvalue"));
					tempname = Util.null2String(rs.getString("selectname"));
					select = select.replace("X1X", tmpvalue);
					tmpStr += "\t\t\t<option value=\"" + tmpvalue + "\" "
							+ select + " >" + tempname + "</option>\n";
				}
				buffer.append(selectStr.replace("XSX", mandStr).replace("X1X",
						tmpStr)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("6")) {// 附件上传
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");			
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// 特殊字段
				buffer.append(inputStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			}
		}
		return buffer;
	}

	/**
	 * 列表添加页面
	 */
	public StringBuffer getListAddFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// 字段ID
		String isedit = formBean.getIsedit();// 是否编辑
		String ismand = formBean.getIsmandatory();// 是否必填
		String isevent = formBean.getIsevent();// 是否有事件
		String fieldname = fieldBean.getFieldname();// 字段名称
		String fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		String textheight = fieldBean.getTextheight();// 显示高度
		String imgheight = fieldBean.getImgheight();// 图片高度
		String imgwidth = fieldBean.getImgwidth();// 图片宽度
		String defvalue = fieldBean.getDefaultvalue();// 默认值
		String transmethod = fieldBean.getTransmethod();// 转换方法
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// 名字后缀
		String v_sql = "";
		String pageType = "Add";
		defvalue = "".equals(defvalue) ? "\"\"" : defvalue;
		RecordSet rs = new RecordSet();
		if (defvalue.length() >= 4) {
			valueStr = "value=\"<%=v_" + defvalue + "%>\"";
		}
		valueStr = "".equals(valueStr) ? "value=\"\"" : valueStr;
		String tmpStr = "";// 临时用
		String mandStr = gener.getMandStr(ismand, fieldBean);// 必填信息
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// 事件
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 输入框
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 文件框		
		
		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// 隐藏框
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// 按钮
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// 日期按钮		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" >\n</textarea>";// 多行文本框
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// 选择框
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// 下拉框

		if ("0".equals(isedit)) {// 只显示
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// 循环开始
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // 单行文本框
				String mandChange = "";// 文本档必填事件
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // 单行文本框中的文本
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // 单行文本框中的整型
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\""+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // 单行文本框中的浮点型
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"thecountSum(this.name,this.myname);"
							+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("4")) {
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"thecountSum(this.name,this.myname);"
							+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				}
			}// 单行文本框条件结束
			else if (fieldType.equals("2")) {// 多行文本框
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // 多行文本框条件结束 // 浏览按钮
				// (涉及workflow_broswerurl表)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
				String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
				// 多行文本框结束
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// 选择框开始
				buffer.append(checkStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // 选择框 select开始
				v_sql = "select * from fla_SelectItem where fieldid ="
						+ fieldid + " order by listorder,id ";
				String tmpvalue = "";
				String tempname = "";
				String isdefault = "";
				tmpStr = "\t\t\t<option value=\"-1\" ></option>\n";
				rs.execute(v_sql);
				while (rs.next()) {
					tmpvalue = Util.null2String(rs.getString("selectvalue"));
					tempname = Util.null2String(rs.getString("selectname"));
					isdefault = Util.null2String(rs.getString("isdefault"));
					tmpStr += "\t\t\t<option value=\""
							+ tmpvalue
							+ "\" "
							+ isdefault.replace("n", "").replace("y",
									"selected") + " >" + tempname
							+ "</option>\n";
				}
				buffer.append(selectStr.replace("XSX", mandStr).replace("X1X",
						tmpStr)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("6")) {// 附件上传
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// 特殊字段
				buffer.append(inputStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			}
		}
		return buffer;
	}

	/**
	 * 列表编辑页面
	 */
	public StringBuffer getListEditFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// 字段ID
		String isedit = formBean.getIsedit();// 是否编辑
		String ismand = formBean.getIsmandatory();// 是否必填
		String isevent = formBean.getIsevent();// 是否有事件
		String fieldname = fieldBean.getFieldname();// 字段名称
		String fieldlabel = fieldBean.getFieldlabel();// 字段显示名
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		String textheight = fieldBean.getTextheight();// 显示高度
		String imgheight = fieldBean.getImgheight();// 图片高度
		String imgwidth = fieldBean.getImgwidth();// 图片宽度
		String defvalue = fieldBean.getDefaultvalue();// 默认值
		String transmethod = fieldBean.getTransmethod();// 转换方法
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// 名字后缀
		String v_sql = "";
		defvalue = "";
		String pageType = "Edit";

		RecordSet rs = new RecordSet();
		valueStr = "".equals(valueStr) ? "value=\"<%=v_" + fieldname1 + "%>\""
				: valueStr;
		String tmpStr = "";// 临时用
		String mandStr = gener.getMandStr(ismand, fieldBean);// 必填信息
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// 事件
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 输入框
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// 文件框				
		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// 隐藏框
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// 按钮
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// 日期按钮		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" ><%=v_" + fieldname1
				+ "%></textarea>";// 多行文本框
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// 选择框
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// 下拉框

		if ("0".equals(isedit)) {// 只显示
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// 循环开始
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // 单行文本框
				String mandChange = "";// 文本档必填事件
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // 单行文本框中的文本
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // 单行文本框中的整型
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\""+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // 单行文本框中的浮点型
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"thecountSum(this.name,this.myname);"
							+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("4")) {
					tmpStr = "onKeyPress=\"ItemNum_KeyPress(this.name);\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"thecountSum(this.name,this.myname);"
							+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				}
			}// 单行文本框条件结束
			else if (fieldType.equals("2")) {// 多行文本框
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // 多行文本框条件结束 // 浏览按钮
				// (涉及workflow_broswerurl表)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // 浏览按钮弹出页面的url
				String linkurl = comInfo.getLinkurl(htmltype); // 浏览值点击的时候链接的url//
				// 多行文本框结束
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
					defvalue="" + fieldname1 + "  ";
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// 选择框开始
				tmpStr = "<%=\"1\".equals(v_" + fieldname1
				+ ")?\"checked\":\"\"%>";
				buffer.append(checkStr.replace("X1X", mandStr+tmpStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // 选择框 select开始
				v_sql = "select * from fla_SelectItem where fieldid ="
						+ fieldid + " order by listorder,id ";
				String tmpvalue = "";
				String tempname = "";
				String select = "<%=\"X1X\".equals(v_" + fieldname1
						+ ")?\"selected\":\"\"%>";
				tmpStr = "\t\t\t<option value=\"-1\" "
						+ select.replace("X1X", "-1") + "></option>\n";
				rs.execute(v_sql);
				while (rs.next()) {
					select = "<%=\"X1X\".equals(v_" + fieldname1
							+ ")?\"selected\":\"\"%>";
					tmpvalue = Util.null2String(rs.getString("selectvalue"));
					tempname = Util.null2String(rs.getString("selectname"));
					select = select.replace("X1X", tmpvalue);
					tmpStr += "\t\t\t<option value=\"" + tmpvalue + "\" "
							+ select + " >" + tempname + "</option>\n";
				}
				buffer.append(selectStr.replace("XSX", mandStr).replace("X1X",
						tmpStr)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("6")) {// 附件上传
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// 特殊字段
				buffer.append(inputStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			}
		}
		return buffer;
	}

	/**
	 * 得到查看页面字段
	 */
	public StringBuffer getViewFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldname = fieldBean.getFieldname();// 字段名称
		String fieldType = fieldBean.getFieldhtmltype();// Html字段类型如：文本框
		String htmltype = fieldBean.getHtmltype();// 单据的字段的type
		String defvalue = fieldBean.getDefaultvalue();// 默认值
		fieldname = fieldname + Suffix;// 名字后缀
		defvalue = "";
		String pageType = "View";
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		if ("1".equals(formBean.getIsview())) {// 只显示
			if (fieldType.equals("1")&&"2,3,4,5".indexOf(htmltype)>-1) { // 单行文本框
				spanStr = "<span id=\"Span" + fieldname + "\" myspan=\""+fieldname+"\" >XvalueX</span>";// span
			}
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,fieldBean));
			return buffer;
		}
		// 循环开始

		return buffer;
	}
}
