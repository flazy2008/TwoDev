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
	 * �õ����ҳ���ֶ�
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// �ֶ�ID
		String isview = formBean.getIsview();// �Ƿ�༭
		String isedit = formBean.getIsedit();// �Ƿ�༭
		String ismand = formBean.getIsmandatory();// �Ƿ����
		String isevent = formBean.getIsevent();// �Ƿ����¼�
		String fieldname = fieldBean.getFieldname();// �ֶ�����
		String fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		String textheight = fieldBean.getTextheight();// ��ʾ�߶�
		String imgheight = fieldBean.getImgheight();// ͼƬ�߶�
		String imgwidth = fieldBean.getImgwidth();// ͼƬ���
		String defvalue = fieldBean.getDefaultvalue();// Ĭ��ֵ
		String transmethod = fieldBean.getTransmethod();// ת������
		String pageType = "Add";
		String fieldname1 = fieldname;
		// fieldname=fieldname+Suffix;//���ֺ�׺
		String v_sql = "";
		defvalue = "".equals(defvalue) ? "\"\"" : defvalue;
		RecordSet rs = new RecordSet();
		if (defvalue.length() >= 4) {
			valueStr = "value=\"<%=v_" + defvalue + "%>\"";
		}
		valueStr = "".equals(valueStr) ? "value=\"\"" : valueStr;
		String tmpStr = "";// ��ʱ��
		String mandStr = gener.getMandStr(ismand, fieldBean);// ������Ϣ
		String eventStr = " X1X=\"XLX"
				+ fieldname.replaceFirst(fieldname.substring(0, 1), fieldname
						.substring(0, 1).toUpperCase()) + "(this.id)\" ";// �¼�
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �����
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �ļ���	
		String hiddStr = "<input type=hidden  X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// ���ؿ�
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// ��ť
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// ���ڰ�ť
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" >\n</textarea>";// �����ı���
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// ѡ���
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// ������

		if ("1".equals(isview)&&"0".equals(isedit)) {// ֻ��ʾ
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// ѭ����ʼ
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // �����ı���
				String mandChange = "";// �ı��������¼�
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // �����ı����е��ı�
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // �����ı����е�����
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // �����ı����еĸ�����
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
			}// �����ı�����������
			else if (fieldType.equals("2")) {// �����ı���
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // �����ı����������� // �����ť
				// (�漰workflow_browserurl��)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
				String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
				// �����ı������
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// ѡ���ʼ
				buffer.append(checkStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // ѡ��� select��ʼ
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
			} else if (fieldType.equals("6")) {// �����ϴ�
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");				
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// �����ֶ�
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
	 * �õ��༭ҳ���ֶ�
	 */
	public StringBuffer getEditFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// �ֶ�ID
		String isedit = formBean.getIsedit();// �Ƿ�༭
		String isview = formBean.getIsview();// �Ƿ�༭
		String ismand = formBean.getIsmandatory();// �Ƿ����
		String isevent = formBean.getIsevent();// �Ƿ����¼�
		String fieldname = fieldBean.getFieldname();// �ֶ�����
		String fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		String textheight = fieldBean.getTextheight();// ��ʾ�߶�
		String imgheight = fieldBean.getImgheight();// ͼƬ�߶�
		String imgwidth = fieldBean.getImgwidth();// ͼƬ���
		String defvalue = fieldBean.getDefaultvalue();// Ĭ��ֵ
		String transmethod = fieldBean.getTransmethod();// ת������
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// ���ֺ�׺
		String v_sql = "";
		defvalue = "";
		String pageType = "Edit";
		RecordSet rs = new RecordSet();
		valueStr = "".equals(valueStr) ? "value=\"<%=v_" + fieldname1 + "%>\""
				: valueStr;
		String tmpStr = "";// ��ʱ��
		String mandStr = gener.getMandStr(ismand, fieldBean);// ������Ϣ
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// �¼�
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �����
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �ļ���		

		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// ���ؿ�
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// ��ť
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// ���ڰ�ť		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" ><%=v_" + fieldname1
				+ "%></textarea>";// �����ı���
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// ѡ���
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// ������

		if ("1".equals(isview)&&"0".equals(isedit)) {// ֻ��ʾ
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// ѭ����ʼ
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // �����ı���
				String mandChange = "";// �ı��������¼�
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // �����ı����е��ı�
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // �����ı����е�����
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\"" + mandChange + "\" myname=\""
							+ fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // �����ı����еĸ�����
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
			}// �����ı�����������
			else if (fieldType.equals("2")) {// �����ı���
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // �����ı����������� // �����ť
				// (�漰workflow_broswerurl��)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
				String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
				// �����ı������
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
					defvalue="" + fieldname1 + "  ";
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// ѡ���ʼ
				tmpStr = "<%=\"1\".equals(v_" + fieldname1
					+ ")?\"checked\":\"\"%>";
				buffer.append(checkStr.replace("X1X", mandStr+tmpStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // ѡ��� select��ʼ
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
			} else if (fieldType.equals("6")) {// �����ϴ�
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");			
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// �����ֶ�
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
	 * �б����ҳ��
	 */
	public StringBuffer getListAddFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// �ֶ�ID
		String isedit = formBean.getIsedit();// �Ƿ�༭
		String ismand = formBean.getIsmandatory();// �Ƿ����
		String isevent = formBean.getIsevent();// �Ƿ����¼�
		String fieldname = fieldBean.getFieldname();// �ֶ�����
		String fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		String textheight = fieldBean.getTextheight();// ��ʾ�߶�
		String imgheight = fieldBean.getImgheight();// ͼƬ�߶�
		String imgwidth = fieldBean.getImgwidth();// ͼƬ���
		String defvalue = fieldBean.getDefaultvalue();// Ĭ��ֵ
		String transmethod = fieldBean.getTransmethod();// ת������
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// ���ֺ�׺
		String v_sql = "";
		String pageType = "Add";
		defvalue = "".equals(defvalue) ? "\"\"" : defvalue;
		RecordSet rs = new RecordSet();
		if (defvalue.length() >= 4) {
			valueStr = "value=\"<%=v_" + defvalue + "%>\"";
		}
		valueStr = "".equals(valueStr) ? "value=\"\"" : valueStr;
		String tmpStr = "";// ��ʱ��
		String mandStr = gener.getMandStr(ismand, fieldBean);// ������Ϣ
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// �¼�
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �����
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �ļ���		
		
		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// ���ؿ�
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// ��ť
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// ���ڰ�ť		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" >\n</textarea>";// �����ı���
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// ѡ���
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// ������

		if ("0".equals(isedit)) {// ֻ��ʾ
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// ѭ����ʼ
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // �����ı���
				String mandChange = "";// �ı��������¼�
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // �����ı����е��ı�
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // �����ı����е�����
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\""+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // �����ı����еĸ�����
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
			}// �����ı�����������
			else if (fieldType.equals("2")) {// �����ı���
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // �����ı����������� // �����ť
				// (�漰workflow_broswerurl��)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
				String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
				// �����ı������
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// ѡ���ʼ
				buffer.append(checkStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // ѡ��� select��ʼ
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
			} else if (fieldType.equals("6")) {// �����ϴ�
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// �����ֶ�
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
	 * �б�༭ҳ��
	 */
	public StringBuffer getListEditFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldid = formBean.getFieldid();// �ֶ�ID
		String isedit = formBean.getIsedit();// �Ƿ�༭
		String ismand = formBean.getIsmandatory();// �Ƿ����
		String isevent = formBean.getIsevent();// �Ƿ����¼�
		String fieldname = fieldBean.getFieldname();// �ֶ�����
		String fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		String textheight = fieldBean.getTextheight();// ��ʾ�߶�
		String imgheight = fieldBean.getImgheight();// ͼƬ�߶�
		String imgwidth = fieldBean.getImgwidth();// ͼƬ���
		String defvalue = fieldBean.getDefaultvalue();// Ĭ��ֵ
		String transmethod = fieldBean.getTransmethod();// ת������
		String fieldname1 = fieldname;
		fieldname = fieldname + Suffix;// ���ֺ�׺
		String v_sql = "";
		defvalue = "";
		String pageType = "Edit";

		RecordSet rs = new RecordSet();
		valueStr = "".equals(valueStr) ? "value=\"<%=v_" + fieldname1 + "%>\""
				: valueStr;
		String tmpStr = "";// ��ʱ��
		String mandStr = gener.getMandStr(ismand, fieldBean);// ������Ϣ
		String eventStr = " X1X=\"XLX"
				+ fieldname1.replaceFirst(fieldname1.substring(0, 1),
						fieldname1.substring(0, 1).toUpperCase())
				+ "(this.id)\" ";// �¼�
		String commonStr = " id=\"XLX" + fieldname + "\" name=\"XLX"
				+ fieldname + "\"  title=\"" + fieldlabel + "\" ";
		commonStr = "1".equals(isevent) ? commonStr
				+ eventStr.replace("X1X", "onChange")
						.replace("XLX", "onChange") : commonStr;
		String inputStr = "<input type=text " + commonStr.replace("XLX", "")
				+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �����
		String fileStr = "<input type=file onchange=\"this.value=trim(this.value)\"" + commonStr.replace("XLX", "File")
		+ valueStr + " size=\"10\"  maxlength=\"X4X\" X1X >";// �ļ���				
		String hiddStr = "<input type=hidden X1X "
				+ commonStr.replace("XLX", "") + " " + valueStr + " />";// ���ؿ�
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		String buttStr = "<button " + commonStr.replace("XLX", "Butt")
				+ " class=\"Browser\" "
				+ eventStr.replace("X1X", "onClick").replace("XLX", "onShow")
				+ " X1X ></button>";// ��ť
		String DateStr = "<button " + commonStr.replace("XLX", "Butt")+ " valueInput=\""+fieldname+"\" valueSpan=\"Span"+fieldname+"\"   class=\"calendar\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\" X1X ></button>";// ���ڰ�ť		
		String textareaStr = "<textarea " + commonStr.replace("XLX", "")
				+ " X1X rows=2 cols=\"30\" ><%=v_" + fieldname1
				+ "%></textarea>";// �����ı���
		String checkStr = "<input type=checkbox  "
				+ commonStr.replace("XLX", "") + "  value=\"1\" X1X >";// ѡ���
		String selectStr = "<select " + commonStr.replace("XLX", "")
				+ " XSX >\nX1X\t\t</select>";// ������

		if ("0".equals(isedit)) {// ֻ��ʾ
			buffer.append(hiddStr.replace("X1X", "") + "\n");
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
					fieldBean));
			return buffer;
		}
		// ѭ����ʼ
		if ("1".equals(isedit)) {
			if (fieldType.equals("1")) { // �����ı���
				String mandChange = "";// �ı��������¼�
				if ("1".equals(ismand)) {
					mandChange = gener.mandOnChange;
				}
				if (htmltype.equals("1")) { // �����ı����е��ı�
					buffer.append(inputStr.replace("X4X", "150").replace("X1X",
							mandStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("2")) { // �����ı����е�����
					tmpStr = "onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
					tmpStr += " onChange=\""+ mandChange + "\" myname=\"" + fieldname1 + "\" ";
					buffer.append(inputStr.replace("X4X", "15").replace("X1X",
							mandStr + tmpStr)
							+ "\n");
					buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
							fieldBean));
				} else if (htmltype.equals("3") || htmltype.equals("5")) { // �����ı����еĸ�����
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
			}// �����ı�����������
			else if (fieldType.equals("2")) {// �����ı���
				buffer.append(textareaStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
				if (htmltype.equals("2")) {

				}
			} else if (fieldType.equals("3")) { // �����ı����������� // �����ť
				// (�漰workflow_broswerurl��)
				BrowserComInfo comInfo = new BrowserComInfo();
				String url = comInfo.getBrowserurl(htmltype); // �����ť����ҳ���url
				String linkurl = comInfo.getLinkurl(htmltype); // ���ֵ�����ʱ�����ӵ�url//
				// �����ı������
				if (htmltype.equals("2")) {
					buffer.append(DateStr.replace("X1X", "") + "\n");
					defvalue="" + fieldname1 + "  ";
				}else{
					buffer.append(buttStr.replace("X1X", "") + "\n");
				}
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("4")) {// ѡ���ʼ
				tmpStr = "<%=\"1\".equals(v_" + fieldname1
				+ ")?\"checked\":\"\"%>";
				buffer.append(checkStr.replace("X1X", mandStr+tmpStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("5")) { // ѡ��� select��ʼ
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
			} else if (fieldType.equals("6")) {// �����ϴ�
				buffer.append(fileStr.replace("X4X", "15").replace("X1X",
						mandStr).replace("XvalueX", defvalue)
						+ "\n");
				buffer.append(hiddStr.replace("X1X", mandStr) + "\n");
				buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,
						fieldBean));
			} else if (fieldType.equals("7")) {// �����ֶ�
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
	 * �õ��鿴ҳ���ֶ�
	 */
	public StringBuffer getViewFieldInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean, String valueStr,
			String Suffix) {
		StringBuffer buffer = new StringBuffer();
		String fieldname = fieldBean.getFieldname();// �ֶ�����
		String fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
		String htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
		String defvalue = fieldBean.getDefaultvalue();// Ĭ��ֵ
		fieldname = fieldname + Suffix;// ���ֺ�׺
		defvalue = "";
		String pageType = "View";
		String spanStr = "<span id=\"Span" + fieldname + "\" >XvalueX</span>";// span
		if ("1".equals(formBean.getIsview())) {// ֻ��ʾ
			if (fieldType.equals("1")&&"2,3,4,5".indexOf(htmltype)>-1) { // �����ı���
				spanStr = "<span id=\"Span" + fieldname + "\" myspan=\""+fieldname+"\" >XvalueX</span>";// span
			}
			buffer.append(gener.getSpanStr(spanStr, defvalue, pageType,fieldBean));
			return buffer;
		}
		// ѭ����ʼ

		return buffer;
	}
}
