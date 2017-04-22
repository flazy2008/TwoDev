package two.general.util;

import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import weaver.conn.RecordSet;
import weaver.general.Util;

public class FieldContentUtil {
	String v_sql = "";
	private String fieldid = "";
	private String billid = "";// ��ID
	private String fieldname = "";// ���ݿ��ֶ�����
	private String pfieldname = "";// ���ݿ��ֶ�����(ԭʼ��)
	private String fieldlabel = "";// �ֶ���ʾ��
	private String fielddbtype = "";//�����ֶ������磺int varchar2
	private String fieldhtmltype = "";// �ֶα�����ʽ �磺�ı��򣬶����ı���
	private String type = "";//�ֶ��������ı����еĽ��
	private String viewtype = "";//��ʾ�����磺0��ʾ����
	private String detailtable = "";//��ϸ����
	private String textheight = "";//�ı���ĸ߶�
	private String defvalue = "";//Ĭ��ֵ�磺user,deptid
	private String transmethod = "";//ת������
	private String pageType="";//ҳ������
	private String isview = "";// �Ƿ���ʾ
	private String isedit = "";// �Ƿ�༭
	private String ismand = "";// �Ƿ����
	private String islog = "";// �Ƿ��¼������־
	private String isevent = "";// �Ƿ����¼�
	private void initFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String pageType,String suffix){
		this.fieldid = fieldBean.getFieldid();
		this.billid = fieldBean.getBillid();// ��ID
		this.fieldname = fieldBean.getFieldname()+suffix;// ���ݿ��ֶ�����
		this.pfieldname= fieldBean.getFieldname();// ���ݿ��ֶ�����(ԭʼ��)
		this.fieldlabel = fieldBean.getFieldlabel();// �ֶ���ʾ��
		this.fielddbtype = fieldBean.getFielddbtype();//�����ֶ������磺int varchar2
		this.fieldhtmltype = fieldBean.getFieldhtmltype();// �ֶα�����ʽ �磺�ı��򣬶����ı���
		this.type = fieldBean.getType();//�ֶ��������ı����еĽ��
		this.viewtype = fieldBean.getViewtype();//��ʾ�����磺0��ʾ����
		this.detailtable = fieldBean.getDetailtable();//��ϸ����
		this.textheight = fieldBean.getTextheight();//�ı���ĸ߶�
		this.defvalue = fieldBean.getDefaultvalue();//Ĭ��ֵ�磺user,deptid
		this.transmethod = fieldBean.getTransmethod();//ת������
		this.pageType=pageType;//ҳ������
		this.isview = viewBean.getIsview();// �Ƿ���ʾ
		this.isedit = viewBean.getIsedit();// �Ƿ�༭
		this.ismand = viewBean.getIsmandatory();// �Ƿ����
		this.islog = viewBean.getIslog();// �Ƿ��¼������־
		this.isevent =viewBean.getIsevent();// �Ƿ����¼�
	}
	/**
	 * �õ�ֵ�ֶ�
	 */
	public StringBuffer getValueFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix,String prefix) {
		this.initFieldInfo(fieldBean, viewBean,"",suffix);//��ʼ��
		String tmpStr = "";// ��ʱ��
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//ֵ
		String valueName="XvtX_"+pfieldname+"";
		String valueSpan="";//��ʾֵ  Ĭ��Ϊʵ��ֵ
		
		if(!"".equals(prefix)){
			valueStr=valueStr.replace("XvtX", prefix);//ֵ
			valueName=valueName.replace("XvtX", prefix);
		}else if("1".equals(viewtype)){
			valueStr=valueStr.replace("XvtX", "v1");//ֵ
			valueName=valueName.replace("XvtX", "v1");
		}else{
			valueStr=valueStr.replace("XvtX", "v");//ֵ
			valueName=valueName.replace("XvtX", "v");
		}
		
		valueSpan=valueStr;//��ʾֵ  Ĭ��Ϊʵ��ֵ
		
		if("".equals(transmethod)&&"4".equals(fieldhtmltype)){
			transmethod="common.LookImgByType";
		}
		if(!"".equals(transmethod)){
			valueSpan = " <%=" + transmethod + "(" + valueName + ")" + "%> ";
		}
		
		if ("1,2,3,4,6".indexOf(fieldhtmltype)>-1){
			buffer.append(valueSpan);
		}else if (fieldhtmltype.equals("5")) { // ѡ��� select��ʼ
			tmpStr = " <%=common.LookNameBySelectID(" + valueName + ",\""+fieldid+"\")%> ";
			buffer.append(tmpStr);
		} else if (fieldhtmltype.equals("7")) {// �����ֶ�
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
	 * �õ��༭ҳ���ֶ�
	 */
	public StringBuffer getViewFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"View",suffix);//��ʼ��
		String tmpStr = "";// ��ʱ��
		String htmlStr="";//html����Ϣ
		String spanStr="";//Span��Ϣ
		String mandStr = "";// �ı��������¼�
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//ֵ
		String valueSpan="";//��ʾֵ
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
			valueStr=valueStr.replace("XvtX", "v1");//ֵ
			valueSpan=valueSpan.replace("XvtX", "v1");//��ʾ
			valueName="v1_"+pfieldname;
		}else{
			valueStr=valueStr.replace("XvtX", "v");//ֵ
			valueSpan=valueSpan.replace("XvtX", "v");//��ʾ
			valueName="v_"+pfieldname;
		}
		if (fieldhtmltype.equals("1")) { // �����ı���
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname);
			if ("2,3,4,5".indexOf(type)>-1) { // �����ı����е��ı�
				spanStr=spanStr.replace("${value}",valueSpan).replace("X1X", "myname=\""+pfieldname+"\"");
			}else{
				spanStr=spanStr.replace("${value}",valueSpan).replace("X1X", "");
			}
			buffer.append(spanStr+"\n");
			// �����ı�����������
		}else if ("2,3,4".indexOf(fieldhtmltype)>-1) {// �����ı���
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan).replace("X1X", "");
			buffer.append(spanStr+"\n");			
		}else if (fieldhtmltype.equals("5")) { // ѡ��� select��ʼ
			tmpStr = " <%=common.LookNameBySelectID(" + valueName + ",\""+fieldid+"\")%> ";
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",tmpStr).replace("X1X", "");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("6")) {// �����ϴ�
			spanStr=HtmlUtil.VIEW_SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan).replace("X1X", "");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// �����ֶ�
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
	 * �õ��༭ҳ���ֶ�
	 */
	public StringBuffer getEditFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"Edit",suffix);//��ʼ��
		String tmpStr = "";// ��ʱ��
		String htmlStr="";//html����Ϣ
		String spanStr="";//Span��Ϣ
		String mandStr = "";// �ı��������¼�
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String valueStr="<%=XvtX_"+pfieldname+"%>";//ֵ
		String valueSpan="";//��ʾֵ
		String valueName="";
		if(!"".equals(transmethod)){
			valueSpan = " <%=" + transmethod + "(XvtX_" + pfieldname + ")" + "%> ";
		}else{
			valueSpan=valueStr;
		}
		if("1".equals(viewtype)){
			valueStr=valueStr.replace("XvtX", "v1");//ֵ
			valueSpan=valueSpan.replace("XvtX", "v1");//��ʾ
			valueName="v1_"+pfieldname;
		}else{
			valueStr=valueStr.replace("XvtX", "v");//ֵ
			valueSpan=valueSpan.replace("XvtX", "v");//��ʾ
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

		if (fieldhtmltype.equals("1")) { // �����ı���
			htmlStr=HtmlUtil.INPUTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("${value}", valueStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			if (type.equals("1")) { // �����ı����е��ı�
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr);
				buffer.append(htmlStr+"\n");
			} else if (type.equals("2")) { // �����ı����е�����
				tmpStr = " onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr+tmpStr);
				htmlStr.replace("${size}", "10").replace("${maxlength}", "15");
				buffer.append(htmlStr+"\n");
			} else if (type.equals("3") ||type.equals("4") || type.equals("5")) { // �����ı����еĸ�����
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
			// �����ı�����������
		}else if (fieldhtmltype.equals("2")) {// �����ı���
			htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
			htmlStr=htmlStr.replace("${value}",valueStr).replace("X1X", mandStr);
			if (type.equals("2")) {//�༭��
				htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}",valueStr).replace("X1X", mandStr+" htmledit=1 ");
			}
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");			
		} else if (fieldhtmltype.equals("3")) { // �����ı����������� // �����ť
			// �����ı������
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
		} else if (fieldhtmltype.equals("4")) {// ѡ���ʼ
			tmpStr = "<%=\"1\".equals(" + valueName+ ")?\"checked\":\"\"%>";
			htmlStr=HtmlUtil.CHECKSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr.replace("onChange", "onClick")+tmpStr);
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}", "");
			buffer.append(htmlStr+"\n");
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("5")) { // ѡ��� select��ʼ
			v_sql = "select * from fla_SelectItem where fieldid ="+ fieldid + " order by listorder,id ";
			String tmpvalue = "";
			String tempname = "";
			String selected = "<%=\"XsValX\".equals(" + valueName+ ")?\"selected\":\"\"%>";
			tmpStr = HtmlUtil.OPTIONSTR.replace("${value}", "-1").replace("X1X", selected.replace("XsValX", "-1")).replace("${text}", "--��ѡ��--");
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
		} else if (fieldhtmltype.equals("6")) {// �����ϴ�
			htmlStr=HtmlUtil.FILESTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X","");
			htmlStr=htmlStr.replace("${text}", tmpStr);
			buffer.append(htmlStr+"\n");//�ļ���
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}",valueStr).replace("X1X","").replace("${title}", fieldlabel);//������
			buffer.append(htmlStr+"\n");
			spanStr=HtmlUtil.SPANSTR.replace("${name}", fieldname).replace("${value}",valueSpan);
			buffer.append(spanStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// �����ֶ�
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
	 * �õ����ҳ���ֶ�
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean) {
		return this.getAddFieldInfo(fieldBean, viewBean, "");
	}
	/**
	 * �õ����ҳ���ֶ�
	 */
	public StringBuffer getAddFieldInfo(FieldInfoBean fieldBean,FieldViewBean viewBean,String suffix) {
		this.initFieldInfo(fieldBean, viewBean,"Add",suffix);//��ʼ��
		String tmpStr = "";// ��ʱ��
		String htmlStr="";//html����Ϣ
		String spanStr="";//Span��Ϣ
		String mandStr = "";// �ı��������¼�
		RecordSet rs = new RecordSet();
		StringBuffer buffer = new StringBuffer();
		String tmpDefvalue="";//Ĭ��ֵ
		String tmpDefSpanHtml="";//Ĭ��ֵ
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

		if (fieldhtmltype.equals("1")) { // �����ı���
			htmlStr=HtmlUtil.INPUTSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("${value}", tmpDefvalue);
			if (type.equals("1")) { // �����ı����е��ı�
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr);
				buffer.append(htmlStr+"\n");
			} else if (type.equals("2")) { // �����ı����е�����
				tmpStr = " onKeyPress=\"ItemCount_KeyPress();\" onFocus=\"delComma(this);\" onBlur=\"addComma(this);\"";
				tmpStr += " myname=\""+ pfieldname + "\"";
				htmlStr=htmlStr.replace("${size}", "10").replace("${maxlength}", "150").replace("X1X", mandStr+tmpStr);
				htmlStr.replace("${size}", "10").replace("${maxlength}", "15");
				buffer.append(htmlStr+"\n");
			} else if (type.equals("3") ||type.equals("4") || type.equals("5")) { // �����ı����еĸ�����
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
			// �����ı�����������
		}else if (fieldhtmltype.equals("2")) {// �����ı���
			htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
			htmlStr=htmlStr.replace("${value}", "").replace("X1X", mandStr);
			if (type.equals("2")) {//�༭��
				htmlStr=HtmlUtil.TEXTAREASTR.replace("${name}", fieldname).replace("${title}", fieldlabel);
				htmlStr=htmlStr.replace("${value}", "").replace("X1X", mandStr+" htmledit=1 ");
			}
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("3")) { // �����ı����������� // �����ť
			// �����ı������
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
		} else if (fieldhtmltype.equals("4")) {// ѡ���ʼ
			htmlStr=HtmlUtil.CHECKSTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X",mandStr.replace("onChange", "onClick"));
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("5")) { // ѡ��� select��ʼ
			v_sql = "select * from fla_SelectItem where fieldid ="+ fieldid + " order by listorder,id ";
			String tmpvalue = "";
			String tempname = "";
			String isdefault = "";
			tmpStr = HtmlUtil.OPTIONSTR.replace("${value}", "-1").replace("X1X", "").replace("${text}", "--��ѡ��--");
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
		} else if (fieldhtmltype.equals("6")) {// �����ϴ�
			htmlStr=HtmlUtil.FILESTR.replace("${name}", fieldname).replace("${title}", fieldlabel).replace("X1X","");
			htmlStr=htmlStr.replace("${text}", tmpStr);
			buffer.append(htmlStr+"\n");//�ļ���
			htmlStr=HtmlUtil.HIDDSTR.replace("${name}", fieldname).replace("${value}", "").replace("X1X","").replace("${title}", fieldlabel);//������
			buffer.append(htmlStr+"\n");
		} else if (fieldhtmltype.equals("7")) {// �����ֶ�
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
