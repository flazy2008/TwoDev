package two.general.bean;

import java.util.LinkedList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class FieldInfoBean {
	private String fieldid = "";
	private String billid = "";// ��ID
	private String fieldname = "";// ���ݿ��ֶ�����
	private String fieldlabel = "";// �ֶ���ʾ��
	private String fielddbtype = "";//�����ֶ������磺int varchar2
	private String fieldhtmltype = "";// �ֶα�����ʽ �磺�ı��򣬶����ı���
	private String type = "";//�ֶ��������ı����еĽ��
	private String viewtype = "";//��ʾ�����磺0��ʾ����
	private String detailtable = "";//��ϸ����
	private String textheight = "";//�ı���ĸ߶�
	private String defaultvalue = "";//Ĭ��ֵ�磺user,deptid
	private String transmethod = "";//ת������
//	private String dsporder = "";//��ʾ˳��
//	private String fromuser = "";
//	private String childfieldid = "";
//	private String imgheight = "";
//	private String imgwidth = "";
//	private String expsql = "";
//	private String impsql = "";
//	private String isimp = "";
	private String v_sql = "";
	public FieldInfoBean() {
	}
	// ��ID
	public List<FieldInfoBean> getAllFields(String billid,String viewtype) {
		LinkedList<FieldInfoBean> list = new LinkedList<FieldInfoBean>();
		v_sql = "select * from fla_billfield where billid='" + billid+ "' and viewtype in ("+viewtype+") order by viewtype,detailtable,dsporder,id";
		if("".equals(viewtype)){
			v_sql = "select * from fla_billfield where billid='" + billid+ "'  order by viewtype,detailtable,dsporder,id";
		}
		RecordSet rs = new RecordSet();
		rs.executeSql(v_sql);
		while (rs.next()) {
			String fieldid = Util.null2String(rs.getString("id"));
			list.add(new FieldInfoBean(fieldid));
		}
		return list;
	}	
	public FieldInfoBean(String fieldid) {
		v_sql = "select * from fla_billfield  where id in(" + fieldid + ")";
		RecordSet rs = new RecordSet();
		rs.executeSql(v_sql);
		if (rs.next()) {
			this.fieldid = Util.null2String(rs.getString("id"));
			this.billid = Util.null2String(rs.getString("billid"));
			this.fieldname = Util.null2String(rs.getString("fieldname"));
			this.fieldlabel = Util.null2String(rs.getString("fieldlabel"));
			this.fielddbtype = Util.null2String(rs.getString("fielddbtype"));
			this.fieldhtmltype = Util.null2String(rs.getString("fieldhtmltype"));
			this.type = Util.null2String(rs.getString("type"));
			this.viewtype = Util.null2String(rs.getString("viewtype"));
			this.detailtable = Util.null2String(rs.getString("detailtable"));
			this.textheight = Util.null2String(rs.getString("textheight"));
			this.defaultvalue = Util.null2String(rs.getString("defaultvalue"));
			this.transmethod = Util.null2String(rs.getString("transmethod"));			
//			this.fromuser = Util.null2String(rs.getString("fromuser"));
//			this.dsporder = Util.null2String(rs.getString("dsporder"));
//			this.childfieldid = Util.null2String(rs.getString("childfieldid"));
//			this.imgheight = Util.null2String(rs.getString("imgheight"));
//			this.imgwidth = Util.null2String(rs.getString("imgwidth"));
//			this.expsql = Util.null2String(rs.getString("expsql"));
//			this.impsql = Util.null2String(rs.getString("impsql"));
//			this.isimp = Util.null2String(rs.getString("isimp"));
		}
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String id) {
		this.fieldid = id;
	}

	public String getBillid() {
		return billid;
	}

	public void setBillid(String billid) {
		this.billid = billid;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldlabel() {
		return fieldlabel;
	}

	public void setFieldlabel(String fieldlabel) {
		this.fieldlabel = fieldlabel;
	}

	public String getFielddbtype() {
		return fielddbtype;
	}

	public void setFielddbtype(String fielddbtype) {
		this.fielddbtype = fielddbtype;
	}

	public String getFieldhtmltype() {
		return fieldhtmltype;
	}

	public void setFieldhtmltype(String fieldhtmltype) {
		this.fieldhtmltype = fieldhtmltype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getViewtype() {
		return viewtype;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}

	public String getDetailtable() {
		return detailtable;
	}

	public void setDetailtable(String detailtable) {
		this.detailtable = detailtable;
	}


	public String getTextheight() {
		return textheight;
	}

	public void setTextheight(String textheight) {
		this.textheight = textheight;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getTransmethod() {
		return transmethod;
	}

	public void setTransmethod(String transmethod) {
		this.transmethod = transmethod;
	}
}
