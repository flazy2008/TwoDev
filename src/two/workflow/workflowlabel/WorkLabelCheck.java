package two.workflow.workflowlabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.docs.mould.DocMouldComInfo;
import weaver.general.Util;
import weaver.workflow.workflow.WorkflowComInfo;

public class WorkLabelCheck {
	public void LabelCheck(RequestBean requestBean, WorkLabelBean labelBean,ViewLabelBean viewBean) {
		String restr = "0";
		String v_sql = "";
		String v_cnt = "";
		RecordSet recordSet = new RecordSet();
		String dbtype=recordSet.getDBType();//数据类型
		String id = labelBean.getId();// 流程ID
		String mainworkflowid = labelBean.getMainnodeid();// 流程ID
		String mainnodeid = labelBean.getMainnodeid();// 节点ID
		String ctype = labelBean.getCtype();// 类型
		String labelid = labelBean.getLabelid();// 标签ID
		String childworkflowid = labelBean.getChildworkflowid();// 需发起的流程ID
		String childnodeid = labelBean.getChildnodeid();// 到达节点
		String expression = labelBean.getExpression();// 表达式
		String expressionvalue = labelBean.getExpressionvalue();// 表达式值
		String url = labelBean.getUrl();// 连接地址
		String viewtype = labelBean.getViewtype();// 显示类型
		String childworkflowinfo = labelBean.getChildworkflowinfo();// 说明信息
		String args = labelBean.getArgs();// 参数
		String orders = labelBean.getOrders();// 顺序
		String isvalid = labelBean.getIsvalid();// 是否有效
		String checkwhere=labelBean.getCheckwhere();// 检查条件
		
		String p_crmid = requestBean.getCrmid();//请求变量
		String p_prjid = requestBean.getPrjid();//请求变量
		String p_busid = requestBean.getBusid();//请求变量
		String p_workflowid =requestBean.getWorkflowid();//请求变量
		String p_nodeid =requestBean.getNodeid();//请求变量
		String p_requestid =requestBean.getRequestid();//请求变量
		String p_labelid = requestBean.getLabelid();//请求变量
		String v_labelurl=viewBean.getLabelurl();//连接地址
		String v_labelnameurl=viewBean.getLabelnameurl();//标签地址
		if ("0".equals(viewtype)) {
			restr= "";
		}
		if ("1".equals(ctype)) {// 流程
			String SqlWhere = "";
			if("1".equals(checkwhere)){
				SqlWhere=" and CAST(req.prjids as VARCHAR) in('"+p_prjid+"') ";
				if("oracle".equals(dbtype)){
					SqlWhere=" and req.prjids in('"+p_prjid+"') ";
				}
			}else if("2".equals(checkwhere)){
				SqlWhere=" and CAST(req.prjids as VARCHAR) in('"+p_prjid+"')  and CAST(req.crmids as VARCHAR) in('"+p_crmid+"') ";
				if("oracle".equals(dbtype)){
					SqlWhere=" and req.prjids in('"+p_prjid+"')  and req.crmids in('"+p_crmid+"') ";
				}
			}else if("3".equals(checkwhere)){
				SqlWhere=" and CAST(req.prjids as VARCHAR) in('"+p_prjid+"') and req.busid in('"+p_busid+"') ";
				if("oracle".equals(dbtype)){
					SqlWhere=" and req.prjids in('"+p_prjid+"')  and req.busid='"+p_busid+"'";
				}
			}
			
			WorkflowComInfo workflowComInfo=null;
			try {
				workflowComInfo = new WorkflowComInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			v_sql="select count(1) as cnt,max(requestid) as requestid from workflow_requestbase req where req.workflowid="+childworkflowid+"  and req.currentnodetype in(1,2,3)"+SqlWhere+" ";
			String v_requestid="0";
			recordSet.executeSql(v_sql);
			if (recordSet.next()) {
				v_cnt = Util.null2String(recordSet.getString("cnt"));
				v_requestid =Util.null2String(recordSet.getString("requestid"));
				if (!"0".equals(v_cnt)) {// 表示存在
					RecordSet rs = new RecordSet();
					v_sql = "select count(1) as cnt from workflow_currentoperator oper where 1=1" +
							" and oper.workflowid="+childworkflowid+" and oper.nodeid='"+childnodeid+
							"' and oper.isreject is null and requestid='"+v_requestid+"'";
					rs.executeSql(v_sql);
					if (rs.next()) {
						v_cnt = Util.null2String(rs.getString("cnt"));
						if (!"0".equals(v_cnt)) {// 达到条件
							restr = "1";//返回已完成
						}
					}
					v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/workflow/request/ViewRequest.jsp?requestid="+v_requestid+"&isovertime=');\">"+workflowComInfo.getWorkflowname(childworkflowid)+"</a>";
					v_labelurl="/workflow/request/ViewRequest.jsp?requestid="+v_requestid+"&isovertime=";	
					viewBean.setLabelurl(v_labelurl);
					viewBean.setLabelnameurl(v_labelnameurl);							
				}else if("2".equals(expression)){//如果检查方式为存在检查
					restr = "未发起该流程";//不存在时返回已完成
				}
			}
			//流程检查结束
		} else if ("2".equals(ctype)) {// 创建文档
			DocMouldComInfo mouldComInfo=null;
			try {
				mouldComInfo = new DocMouldComInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String mouldName=mouldComInfo.getDocMouldname(childworkflowid);
			String SqlWhere = "";
			if("1".equals(checkwhere)){
				SqlWhere=" and doc.projectid='"+p_prjid+"' ";
			}else if("2".equals(checkwhere)){
				SqlWhere=" and doc.projectid='"+p_prjid+"' and doc.crmid='"+p_crmid+"' ";
			}
			SqlWhere+=" and doc.keyword='"+mouldName+"'";
			v_sql="select count(1) as cnt from   DocDetail doc  where 1=1 "+SqlWhere;
			recordSet.executeSql(v_sql);
			if (recordSet.next()) {
				v_cnt = Util.null2String(recordSet.getString("cnt"));
				if (!"0".equals(v_cnt)) {// 达到条件
					restr = "1";//返回已完成
				}
			}
		} else if ("4".equals(ctype)) {// 查看已有文档

		} else if ("5".equals(ctype)) {// 相关登记
			LabelBean regbean = new LabelBean(childworkflowid);
			String v_tablename = regbean.getTablename();// 登记表名
			String SqlWhere = "";
			
			if ("-1".equals(expression)) {
				SqlWhere = expressionvalue;
			} else if ("is null".equals(expression)) {
				SqlWhere = " and " + childnodeid + " is null";
			} else if ("is not null".equals(expression)) {
				SqlWhere = " and " + childnodeid + " is not null";
			} else {
				SqlWhere = " and " + childnodeid + " " + expression + "'"
						+ expressionvalue + "'";
			}
			
			if("1".equals(checkwhere)){//查检条件
				SqlWhere+=" and prjid='"+p_prjid+"' ";
			}else if("2".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and crmid='"+p_crmid+"' ";
			}else if("3".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and busid='"+p_busid+"' ";
			}	
			
			v_sql = "select count(1) as cnt from " + v_tablename+ " where 1=1 " + SqlWhere;
			if(!"".equals(v_tablename)){//表名为空时不执行
				recordSet.executeSql(v_sql);
				if (recordSet.next()) {
					v_cnt = Util.null2String(recordSet.getString("cnt"));
					if (!"0".equals(v_cnt)) {// 未达到条件
						restr = "1";
					}
				}
			}
		}else if ("3".equals(ctype)) {// 模块
			String v_tablename = new WorkModuleBean(childnodeid).getTablename();// 模块表名(要检查的表名)
			String SqlWhere ="";
			if("1".equals(checkwhere)){//查检条件
				SqlWhere=" and prjid='"+p_prjid+"' ";
			}else if("2".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and crmid='"+p_crmid+"' ";
			}else if("3".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and busid='"+p_busid+"' ";
			}
			v_sql = "select count(1) as cnt from " + v_tablename+ " where 1=1 " + SqlWhere;
			recordSet.executeSql(v_sql);
			if (recordSet.next()) {
				v_cnt = Util.null2String(recordSet.getString("cnt"));
			}
			if("".equals(expression)){//没有附加条件时
				if (!"0".equals(v_cnt)) {// 未达到条件
					restr = "1";
				}
			}else if(!"0".equals(v_cnt)){//加条件查找
				recordSet.executeSql(v_sql+expression);
				if(recordSet.next()){
					if(v_cnt.equals(Util.null2String(recordSet.getString("cnt")))){
						restr = "1";
					}
				}
			}
		}else if ("6".equals(ctype)) {// 后台检查
			v_sql=childworkflowinfo;//SQL语句
			String revalue="";//返回值
			if("1".equals(childworkflowid)){//SQL语句
				v_sql=v_sql.replace("${prjid}", p_prjid).replace("${crmid}", p_crmid);
				recordSet.executeSql(v_sql);
			}else if ("2".equals(childworkflowid)) {//储存过程
				String ProcPara="";
				char flag=2;
				ProcPara =p_crmid;//客户ID
				ProcPara += flag +p_prjid;//项目
				ProcPara += flag + p_busid;//业务
				ProcPara += flag + p_workflowid;//流程ID
				ProcPara += flag + p_nodeid;//节点ID
				ProcPara += flag + p_requestid;//请求ID
				ProcPara += flag + p_labelid;//标签ID
				
				recordSet.executeProc(v_sql,ProcPara);
				String v_msg=recordSet.getMsg();
				v_labelurl=viewBean.getLabelurl();
				if(!"".equals(v_msg)){
					if(v_msg.indexOf("?")>-1){
						v_labelurl=v_msg+"&isfromreq=1"+v_labelurl;
					}else{
						v_labelurl=v_msg+"?isfromreq=1"+v_labelurl;
					}
					v_labelnameurl=v_labelnameurl.replace("XURLX", v_labelurl);
					viewBean.setLabelurl(v_msg);
					viewBean.setLabelnameurl(v_labelnameurl);
				}
			}
			List<String> fieldlist =null;
			List<Map<String, String>> valueList =null;
			if("1".equals(expression)||"5".equals(expression)){
				Map<String, String> map = null;
				String fields = "";
				String values = "";
				valueList = new ArrayList<Map<String,String>>();
				Iterator ir =null;
				fieldlist = Arrays.asList(recordSet.getColumnName());
				while (recordSet.next()) {
					ir = fieldlist.iterator();
					map = new HashMap<String, String>();
					fields = "";
					values = "";
					while (ir.hasNext()) {
						fields = (String) ir.next();
						values = Util.null2String(recordSet.getString(fields));
						map.put(fields, values);
					}
					valueList.add(map);
				}
			}
			
			if("-1".equals(expression)){//返回值等于
				if(recordSet.next()){
					revalue=Util.null2String(recordSet.getString(1));
				}
				if(revalue.equals(expressionvalue)){
					restr = "1";
				}
			}else if("1".equals(expression)){//返回列值相等
				boolean reflag=true;
				if(valueList.size()>0){
					String comparevalue="";
					for (String	str: fieldlist) {
						comparevalue=valueList.get(0).get(str);
						for (Map<String, String> map : valueList) {
							if(!comparevalue.equals(map.get(str))){
								reflag=false;
								break;
							}
						}
						if(!reflag){
							break;
						}
					}
				}
				if(reflag){
					restr = "1";
				}
			}else if("5".equals(expression)){//返回行值相等
				boolean reflag = true;
				if (valueList.size() > 0) {
					String comparevalue = "";
					for (Map<String, String> map : valueList) {
						comparevalue =map.get(fieldlist.get(0));
						for (String str : fieldlist) {
							if (!comparevalue.equals(map.get(str))) {
								reflag = false;
								break;
							}
						}
						if (!reflag) {
							break;
						}
					}
				}
				if (reflag) {
					restr = "1";
				}
			}else if("2".equals(expression)){//有返回值
				if(recordSet.next()){
					restr = "1";
				}
			}else if("3".equals(expression)){//无返回值
				if(!recordSet.next()){
					restr = "1";
				}
			}else if("4".equals(expression)){//flag值等
				revalue=recordSet.getFlag()+"";
				if(expressionvalue.equals(revalue)){
					restr = "1";
				}
			}
		}
		viewBean.setIsend(restr);
	}
}
