package two.workflow.workflowlabel;

import java.util.LinkedList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkLabelBean {
	private String id = "";// ����ID
	private String mainworkflowid = "";// ����ID
	private String mainnodeid = "";// �ڵ�ID
	private String ctype = "";// ����
	private String labelid = "";// ��ǩID
	private String childworkflowid = "";// �跢�������ID
	private String childnodeid = "";// ����ڵ�
	private String expression = "";// ���ʽ
	private String expressionvalue = "";// ���ʽֵ
	private String url = "";// ���ӵ�ַ
	private String viewtype = "";// ��ʾ����
	private String childworkflowinfo = "";// ˵����Ϣ
	private String args = "";// ����
	private String orders = "";// ˳��
	private String isvalid = "";// �Ƿ���Ч
	private String checkwhere="";//�������
	private List<WorkLabelBean> list = null;// ���ڷ��ֶ���Ϣ
	private String v_sql = "";
	RecordSet RecordSet = null;

	RequestBean requestBean = null;
	String v_ctype = "";

	public WorkLabelBean(RequestBean requestBean, String v_ctype) {
		this.requestBean=requestBean;
		this.ctype=v_ctype;
	}

	public List<WorkLabelBean> getLists() {
		return this.getWorkLabels(this.requestBean, this.v_ctype);
	}

	// ��ID
	public List<WorkLabelBean> getWorkLabels(RequestBean requestBean,
			String v_ctype) {
		list = new LinkedList<WorkLabelBean>();
		RecordSet = new RecordSet();
		WorkLabelBean labelBean = null;

		String SqlWhere = "";
		String workflowid = requestBean.getWorkflowid();
		String nodeid = requestBean.getNodeid();
		String v_labelid = requestBean.getLabelid();
		if (!"".equals(v_ctype)) {
			SqlWhere += " and ctype=" + v_ctype;
		}
		if (!"".equals(v_labelid)) {
			SqlWhere += " and labelid=" + v_labelid;
		}
		v_sql = "select * from work_flownodechild where isvalid=1 and  mainworkflowid='"
				+ workflowid
				+ "' and mainnodeid='"
				+ nodeid
				+ "' "
				+ SqlWhere
				+ "  order by orders,ctype,id";

		String id = "";
		String mainworkflowid = "";// ����ID
		String mainnodeid = "";// �ڵ�ID
		String ctype = "";// ����
		String labelid = "";// ��ǩID
		String childworkflowid = "";// �跢�������ID
		String childnodeid = "";// ����ڵ�
		String expression = "";// ���ʽ
		String expressionvalue = "";// ���ʽֵ
		String url = "";// ���ӵ�ַ
		String viewtype = "";// ��ʾ����
		String childworkflowinfo = "";// ˵����Ϣ
		String args = "";// ����
		String orders = "";// ˳��
		String checkwhere="";//�������
		String isvalid = "";// �Ƿ���Ч
		RecordSet.executeSql(v_sql);
		while (RecordSet.next()) {
			id = Util.null2String(RecordSet.getString("id"));// ��ͬ���
			mainworkflowid = Util.null2String(RecordSet
					.getString("mainworkflowid"));// ����ID
			mainnodeid = Util.null2String(RecordSet.getString("mainnodeid"));// �ڵ�ID
			ctype = Util.null2String(RecordSet.getString("ctype"));// ����
			labelid = Util.null2String(RecordSet.getString("labelid"));// ��ǩID
			childworkflowid = Util.null2String(RecordSet
					.getString("childworkflowid"));// �跢�������ID
			childnodeid = Util.null2String(RecordSet.getString("childnodeid"));// ����ڵ�
			expression = Util.null2String(RecordSet.getString("expression"));// ���ʽ
			expressionvalue = Util.null2String(RecordSet
					.getString("expressionvalue"));// ���ʽֵ
			url = Util.null2String(RecordSet.getString("url"));// ���ӵ�ַ
			viewtype = Util.null2String(RecordSet.getString("viewtype"));// ��ʾ����
			childworkflowinfo = Util.null2String(RecordSet
					.getString("childworkflowinfo"));// ˵����Ϣ
			args = Util.null2String(RecordSet.getString("args"));// ����
			orders = Util.null2String(RecordSet.getString("orders"));// ˳��
			isvalid = Util.null2String(RecordSet.getString("isvalid"));// �Ƿ���Ч
			checkwhere=Util.null2String(RecordSet.getString("checkwhere"));;//�������
			labelBean = new WorkLabelBean(id, mainworkflowid, mainnodeid,
					ctype, labelid, childworkflowid, childnodeid, expression,
					expressionvalue, url, viewtype, childworkflowinfo, args,
					orders, isvalid,checkwhere);
			list.add(labelBean);
		}
		return list;
	}

	public WorkLabelBean(String id, String mainworkflowid, String mainnodeid,
			String ctype, String labelid, String childworkflowid,
			String childnodeid, String expression, String expressionvalue,
			String url, String viewtype, String childworkflowinfo, String args,
			String orders, String isvalid,String checkwhere) {
		this.id = id;
		this.mainworkflowid = mainworkflowid;
		this.mainnodeid = mainnodeid;
		this.ctype = ctype;
		this.labelid = labelid;
		this.childworkflowid = childworkflowid;
		this.childnodeid = childnodeid;
		this.expression = expression;
		this.expressionvalue = expressionvalue;
		this.url = url;
		this.viewtype = viewtype;
		this.childworkflowinfo = childworkflowinfo;
		this.args = args;
		this.orders = orders;
		this.isvalid = isvalid;
		this.checkwhere=checkwhere;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMainworkflowid() {
		return mainworkflowid;
	}

	public void setMainworkflowid(String mainworkflowid) {
		this.mainworkflowid = mainworkflowid;
	}

	public String getMainnodeid() {
		return mainnodeid;
	}

	public void setMainnodeid(String mainnodeid) {
		this.mainnodeid = mainnodeid;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getLabelid() {
		return labelid;
	}

	public void setLabelid(String labelid) {
		this.labelid = labelid;
	}

	public String getChildworkflowid() {
		return childworkflowid;
	}

	public void setChildworkflowid(String childworkflowid) {
		this.childworkflowid = childworkflowid;
	}

	public String getChildnodeid() {
		return childnodeid;
	}

	public void setChildnodeid(String childnodeid) {
		this.childnodeid = childnodeid;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpressionvalue() {
		return expressionvalue;
	}

	public void setExpressionvalue(String expressionvalue) {
		this.expressionvalue = expressionvalue;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getViewtype() {
		return viewtype;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}

	public String getChildworkflowinfo() {
		return childworkflowinfo;
	}

	public void setChildworkflowinfo(String childworkflowinfo) {
		this.childworkflowinfo = childworkflowinfo;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public String getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(String isvalid) {
		this.isvalid = isvalid;
	}

	public void setCheckwhere(String checkwhere) {
		this.checkwhere = checkwhere;
	}

	public String getCheckwhere() {
		return checkwhere;
	}

}
