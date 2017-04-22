package two.workflow.workflowlabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weaver.docs.docs.DocComInfo;
import weaver.docs.mould.DocMouldComInfo;
import weaver.general.Util;
import weaver.workflow.workflow.WorkflowComInfo;

public class WorkflowLabel {
	private List<ViewLabelBean> labelBeans = null;
	private Map<String, ViewLabelBean> mouldMap=null;
	List<WorkLabelBean> labels = null;
	private ViewLabelBean viewBean = null;

	public List<ViewLabelBean> getAllViewBeans(RequestBean requestBean) {
		WorkLabelCheck labelCheck=new WorkLabelCheck();
		labelBeans = new ArrayList<ViewLabelBean>();// 返回所有标签
		mouldMap=new HashMap<String, ViewLabelBean>();//模块标签
		labels = new WorkLabelBean(requestBean, "").getLists();// 所有标签
		DocMouldComInfo mouldComInfo = null;
		WorkflowComInfo workflowComInfo=null;
		DocComInfo docComInfo=null;//文档信息
		LabelBean regBean=null;
		WorkModuleBean moduleBean=null;//模块信息
		try {
			mouldComInfo = new DocMouldComInfo();
			workflowComInfo=new WorkflowComInfo();
			docComInfo=new DocComInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}// 文档信息
		String v_sql = "";
		String tmp = "";
		String ctype = "";// 显示类型
		String childworkflowid = "";// 需发起的流程ID
		String childnodeid = "";// 到达节点
		String expression = "";// 表达式
		String expressionvalue = "";// 表达式值
		String url = "";// 连接地址
		String viewtype = "";// 显示类型
		String childworkflowinfo = "";// 说明信息
		String args = "";// 参数
		String checkwhere="";// 检查条件
		
		String v_labeltype = "";// 类型查看、选做、必做
		String v_labelinfo = "";// 显示信息如：查看事宜(文档)
		String v_labelname = "";// 显示名称
		String v_labelnameurl="";//带连接的名称
		String v_labelurl = "";// 带连接的显示名称
		String v_isend = "";// 是否完成
		String v_viewtmp="";//临时变量
		
		String v_prjid = requestBean.getPrjid();//请求变量
		String v_crmid = requestBean.getCrmid();//请求变量
		String v_busid = requestBean.getBusid();//请求变量
		for (WorkLabelBean labelBean : labels) {
			v_viewtmp="&prjid="+v_prjid+"&crmid="+v_crmid+"&busid="+v_busid;
			childworkflowid = labelBean.getChildworkflowid();
			childnodeid = labelBean.getChildnodeid();
			expressionvalue=labelBean.getExpressionvalue();
			viewtype = labelBean.getViewtype();
			ctype = labelBean.getCtype();
			args=labelBean.getArgs();
			url=labelBean.getUrl();
			expression = labelBean.getExpression();// 表达式
			checkwhere=labelBean.getCheckwhere();// 检查条件
			if(!"1".equals(ctype)){
				v_viewtmp+="&requestid="+requestBean.getRequestid()+"&nodeid="+requestBean.getNodeid();
			}
			if ("1".equals(ctype)) {
				v_labelname=workflowComInfo.getWorkflowname(childworkflowid);
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/workflow/request/AddRequest.jsp?workflowid="+childworkflowid+"&isagent=0&beagenter=0"+v_viewtmp+"');\">"+v_labelname+"</a>";
				v_labelurl="/workflow/request/AddRequest.jsp?workflowid="+childworkflowid+"&isagent=0&beagenter=0"+v_viewtmp;
				viewBean = new ViewLabelBean(viewtype,"事宜(流程)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("2".equals(ctype)){
				v_labelname=mouldComInfo.getDocMouldname(childworkflowid);
				v_viewtmp+="&mainid=9&fromFlowDoc=&requestid=0&secid=22&subid=17&topage=&showsubmit=1&hrmid=&from=&docsubject="+v_labelname+"&keyword="+v_labelname+"&invalidationdate=&docmodule="+childworkflowid+"";
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/docs/docs/DocAddExt.jsp?isfromtable=1"+v_viewtmp+"');\">"+v_labelname+"</a>";
				v_labelurl="/docs/docs/DocAddExt.jsp?isfromtable=1"+v_viewtmp;
				viewBean = new ViewLabelBean(viewtype,"事宜(文档)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("4".equals(ctype)){
				v_labelname=docComInfo.getDocname(childworkflowid);
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/docs/docs/DocDsp.jsp?id="+childworkflowid+"');\">"+v_labelname+"</a>";
				v_labelurl="/docs/docs/DocDsp.jsp?id="+childworkflowid;
				viewBean = new ViewLabelBean(viewtype,"事宜(文档)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("5".equals(ctype)){//登记
				regBean=new LabelBean(childworkflowid);
				v_labelname=regBean.getLabelname();
				v_labelurl=regBean.getUrl();
				if(v_labelurl.indexOf("?")>-1){
					v_labelurl=v_labelurl+"&isfromreq=1"+args;
				}else{
					v_labelurl=v_labelurl+"?isfromreq=1"+args;
				}
				v_labelurl+=v_viewtmp;//公共参数信息
				if("view".equalsIgnoreCase(requestBean.getOper())){
					v_labelurl+="&oper=0";//操作类型 0,查看，1、编辑
				}else{
					v_labelurl+="&oper="+viewtype;//操作类型
				}
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('"+v_labelurl+"');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"事宜(登记)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
				
			}else if("3".equals(ctype)){//模块
				moduleBean=new WorkModuleBean(childworkflowid);
				String v_id=moduleBean.getId();//模块ID
				v_labelname=moduleBean.getLabelname();
				String v_checkPID=new WorkModuleBean(childnodeid).getPid();
				v_labelurl="/flazyform/workflow/work_module/work_module.jsp?isfromreq=1&panelTitle=0&moduleid="+v_id;
				v_labelurl+=v_viewtmp+args;//公共参数信息
				if("view".equalsIgnoreCase(requestBean.getOper())){
					v_labelurl+="&oper=0";//操作类型 0,查看，1、编辑
				}else{
					v_labelurl+="&oper="+viewtype;//操作类型
				}
				v_labelurl+="&limittype="+expressionvalue+"&checkID="+childnodeid+"&checkPID="+v_checkPID;
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('"+v_labelurl+"');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"事宜(模块)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
				labelCheck.LabelCheck(requestBean, labelBean,viewBean);//检查是否完成
				setMouldViewBean(v_id, viewBean);
			}else if("7".equals(ctype)){
				childworkflowinfo=labelBean.getChildworkflowinfo();
				v_labelname=mouldComInfo.getDocMouldname(childworkflowid);
				viewBean = new ViewLabelBean(viewtype,"说明信息","说明信息","<pre>"+childworkflowinfo+"</pre>","",v_isend);
			}else if("6".equals(ctype)){//后台检查
				v_labelname=childnodeid;//显示信息；
				v_labelurl=args+v_viewtmp;//公共参数信息
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('XURLX');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"事宜(后台)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}
			viewBean.setLabeltype(ctype);//标签类型;
			if(!"7".equals(ctype)&&!"3".equals(ctype)){//类型不能为说明信息
				labelCheck.LabelCheck(requestBean, labelBean,viewBean);//检查是否完成
			}
			if(!"3".equals(ctype)){//为模块时多条记录只显示一条记录
				labelBeans.add(viewBean);
			}
		}//结束循环
		Iterator it = mouldMap.keySet().iterator();
		String key ="";
        while (it.hasNext()) {
            key = Util.null2String((String) it.next());
            labelBeans.add(0,mouldMap.get(key));
        }
		return labelBeans;
	}
	private void setMouldViewBean(String v_id,ViewLabelBean viewBean){
		//System.out.println("模块："+viewBean.toString());
		if(!mouldMap.containsKey(v_id)){//不存在
			mouldMap.put(v_id, viewBean);
		}else{//以下为存在
			if("0".equals(viewBean.getIsend())){//存在未结束	
				if("1".equals(mouldMap.get(v_id).getIsend())){//旧的已结束
					mouldMap.put(v_id, viewBean);
				}else{//旧的未结束
					int v_old =Util.getIntValue( mouldMap.get(v_id).getViewtype());
					int v_new =Util.getIntValue( viewBean.getViewtype());
					if(v_new>=v_old){
						mouldMap.put(v_id, viewBean);
					}
				}
			}
		}
	}
}
