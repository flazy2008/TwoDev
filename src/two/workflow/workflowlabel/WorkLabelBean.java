package two.workflow.workflowlabel;

import java.util.LinkedList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class WorkLabelBean {
	private String id = "";// 流程ID
	private String mainworkflowid = "";// 流程ID
	private String mainnodeid = "";// 节点ID
	private String ctype = "";// 类型
	private String labelid = "";// 标签ID
	private String childworkflowid = "";// 需发起的流程ID
	private String childnodeid = "";// 到达节点
	private String expression = "";// 表达式
	private String expressionvalue = "";// 表达式值
	private String url = "";// 连接地址
	private String viewtype = "";// 显示类型
	private String childworkflowinfo = "";// 说明信息
	private String args = "";// 参数
	private String orders = "";// 顺序
	private String isvalid = "";// 是否有效
	private String checkwhere="";//检查条件
	private List<WorkLabelBean> list = null;// 用于放字段信息
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

	// 表单ID
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
		String mainworkflowid = "";// 流程ID
		String mainnodeid = "";// 节点ID
		String ctype = "";// 类型
		String labelid = "";// 标签ID
		String childworkflowid = "";// 需发起的流程ID
		String childnodeid = "";// 到达节点
		String expression = "";// 表达式
		String expressionvalue = "";// 表达式值
		String url = "";// 连接地址
		String viewtype = "";// 显示类型
		String childworkflowinfo = "";// 说明信息
		String args = "";// 参数
		String orders = "";// 顺序
		String checkwhere="";//检查条件
		String isvalid = "";// 是否有效
		RecordSet.executeSql(v_sql);
		while (RecordSet.next()) {
			id = Util.null2String(RecordSet.getString("id"));// 合同编号
			mainworkflowid = Util.null2String(RecordSet
					.getString("mainworkflowid"));// 流程ID
			mainnodeid = Util.null2String(RecordSet.getString("mainnodeid"));// 节点ID
			ctype = Util.null2String(RecordSet.getString("ctype"));// 类型
			labelid = Util.null2String(RecordSet.getString("labelid"));// 标签ID
			childworkflowid = Util.null2String(RecordSet
					.getString("childworkflowid"));// 需发起的流程ID
			childnodeid = Util.null2String(RecordSet.getString("childnodeid"));// 到达节点
			expression = Util.null2String(RecordSet.getString("expression"));// 表达式
			expressionvalue = Util.null2String(RecordSet
					.getString("expressionvalue"));// 表达式值
			url = Util.null2String(RecordSet.getString("url"));// 连接地址
			viewtype = Util.null2String(RecordSet.getString("viewtype"));// 显示类型
			childworkflowinfo = Util.null2String(RecordSet
					.getString("childworkflowinfo"));// 说明信息
			args = Util.null2String(RecordSet.getString("args"));// 参数
			orders = Util.null2String(RecordSet.getString("orders"));// 顺序
			isvalid = Util.null2String(RecordSet.getString("isvalid"));// 是否有效
			checkwhere=Util.null2String(RecordSet.getString("checkwhere"));;//检查条件
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
