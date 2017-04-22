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
		String dbtype=recordSet.getDBType();//��������
		String id = labelBean.getId();// ����ID
		String mainworkflowid = labelBean.getMainnodeid();// ����ID
		String mainnodeid = labelBean.getMainnodeid();// �ڵ�ID
		String ctype = labelBean.getCtype();// ����
		String labelid = labelBean.getLabelid();// ��ǩID
		String childworkflowid = labelBean.getChildworkflowid();// �跢�������ID
		String childnodeid = labelBean.getChildnodeid();// ����ڵ�
		String expression = labelBean.getExpression();// ���ʽ
		String expressionvalue = labelBean.getExpressionvalue();// ���ʽֵ
		String url = labelBean.getUrl();// ���ӵ�ַ
		String viewtype = labelBean.getViewtype();// ��ʾ����
		String childworkflowinfo = labelBean.getChildworkflowinfo();// ˵����Ϣ
		String args = labelBean.getArgs();// ����
		String orders = labelBean.getOrders();// ˳��
		String isvalid = labelBean.getIsvalid();// �Ƿ���Ч
		String checkwhere=labelBean.getCheckwhere();// �������
		
		String p_crmid = requestBean.getCrmid();//�������
		String p_prjid = requestBean.getPrjid();//�������
		String p_busid = requestBean.getBusid();//�������
		String p_workflowid =requestBean.getWorkflowid();//�������
		String p_nodeid =requestBean.getNodeid();//�������
		String p_requestid =requestBean.getRequestid();//�������
		String p_labelid = requestBean.getLabelid();//�������
		String v_labelurl=viewBean.getLabelurl();//���ӵ�ַ
		String v_labelnameurl=viewBean.getLabelnameurl();//��ǩ��ַ
		if ("0".equals(viewtype)) {
			restr= "";
		}
		if ("1".equals(ctype)) {// ����
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
				if (!"0".equals(v_cnt)) {// ��ʾ����
					RecordSet rs = new RecordSet();
					v_sql = "select count(1) as cnt from workflow_currentoperator oper where 1=1" +
							" and oper.workflowid="+childworkflowid+" and oper.nodeid='"+childnodeid+
							"' and oper.isreject is null and requestid='"+v_requestid+"'";
					rs.executeSql(v_sql);
					if (rs.next()) {
						v_cnt = Util.null2String(rs.getString("cnt"));
						if (!"0".equals(v_cnt)) {// �ﵽ����
							restr = "1";//���������
						}
					}
					v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/workflow/request/ViewRequest.jsp?requestid="+v_requestid+"&isovertime=');\">"+workflowComInfo.getWorkflowname(childworkflowid)+"</a>";
					v_labelurl="/workflow/request/ViewRequest.jsp?requestid="+v_requestid+"&isovertime=";	
					viewBean.setLabelurl(v_labelurl);
					viewBean.setLabelnameurl(v_labelnameurl);							
				}else if("2".equals(expression)){//�����鷽ʽΪ���ڼ��
					restr = "δ���������";//������ʱ���������
				}
			}
			//���̼�����
		} else if ("2".equals(ctype)) {// �����ĵ�
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
				if (!"0".equals(v_cnt)) {// �ﵽ����
					restr = "1";//���������
				}
			}
		} else if ("4".equals(ctype)) {// �鿴�����ĵ�

		} else if ("5".equals(ctype)) {// ��صǼ�
			LabelBean regbean = new LabelBean(childworkflowid);
			String v_tablename = regbean.getTablename();// �ǼǱ���
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
			
			if("1".equals(checkwhere)){//�������
				SqlWhere+=" and prjid='"+p_prjid+"' ";
			}else if("2".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and crmid='"+p_crmid+"' ";
			}else if("3".equals(checkwhere)){
				SqlWhere+=" and prjid='"+p_prjid+"' and busid='"+p_busid+"' ";
			}	
			
			v_sql = "select count(1) as cnt from " + v_tablename+ " where 1=1 " + SqlWhere;
			if(!"".equals(v_tablename)){//����Ϊ��ʱ��ִ��
				recordSet.executeSql(v_sql);
				if (recordSet.next()) {
					v_cnt = Util.null2String(recordSet.getString("cnt"));
					if (!"0".equals(v_cnt)) {// δ�ﵽ����
						restr = "1";
					}
				}
			}
		}else if ("3".equals(ctype)) {// ģ��
			String v_tablename = new WorkModuleBean(childnodeid).getTablename();// ģ�����(Ҫ���ı���)
			String SqlWhere ="";
			if("1".equals(checkwhere)){//�������
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
			if("".equals(expression)){//û�и�������ʱ
				if (!"0".equals(v_cnt)) {// δ�ﵽ����
					restr = "1";
				}
			}else if(!"0".equals(v_cnt)){//����������
				recordSet.executeSql(v_sql+expression);
				if(recordSet.next()){
					if(v_cnt.equals(Util.null2String(recordSet.getString("cnt")))){
						restr = "1";
					}
				}
			}
		}else if ("6".equals(ctype)) {// ��̨���
			v_sql=childworkflowinfo;//SQL���
			String revalue="";//����ֵ
			if("1".equals(childworkflowid)){//SQL���
				v_sql=v_sql.replace("${prjid}", p_prjid).replace("${crmid}", p_crmid);
				recordSet.executeSql(v_sql);
			}else if ("2".equals(childworkflowid)) {//�������
				String ProcPara="";
				char flag=2;
				ProcPara =p_crmid;//�ͻ�ID
				ProcPara += flag +p_prjid;//��Ŀ
				ProcPara += flag + p_busid;//ҵ��
				ProcPara += flag + p_workflowid;//����ID
				ProcPara += flag + p_nodeid;//�ڵ�ID
				ProcPara += flag + p_requestid;//����ID
				ProcPara += flag + p_labelid;//��ǩID
				
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
			
			if("-1".equals(expression)){//����ֵ����
				if(recordSet.next()){
					revalue=Util.null2String(recordSet.getString(1));
				}
				if(revalue.equals(expressionvalue)){
					restr = "1";
				}
			}else if("1".equals(expression)){//������ֵ���
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
			}else if("5".equals(expression)){//������ֵ���
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
			}else if("2".equals(expression)){//�з���ֵ
				if(recordSet.next()){
					restr = "1";
				}
			}else if("3".equals(expression)){//�޷���ֵ
				if(!recordSet.next()){
					restr = "1";
				}
			}else if("4".equals(expression)){//flagֵ��
				revalue=recordSet.getFlag()+"";
				if(expressionvalue.equals(revalue)){
					restr = "1";
				}
			}
		}
		viewBean.setIsend(restr);
	}
}
