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
		labelBeans = new ArrayList<ViewLabelBean>();// �������б�ǩ
		mouldMap=new HashMap<String, ViewLabelBean>();//ģ���ǩ
		labels = new WorkLabelBean(requestBean, "").getLists();// ���б�ǩ
		DocMouldComInfo mouldComInfo = null;
		WorkflowComInfo workflowComInfo=null;
		DocComInfo docComInfo=null;//�ĵ���Ϣ
		LabelBean regBean=null;
		WorkModuleBean moduleBean=null;//ģ����Ϣ
		try {
			mouldComInfo = new DocMouldComInfo();
			workflowComInfo=new WorkflowComInfo();
			docComInfo=new DocComInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}// �ĵ���Ϣ
		String v_sql = "";
		String tmp = "";
		String ctype = "";// ��ʾ����
		String childworkflowid = "";// �跢�������ID
		String childnodeid = "";// ����ڵ�
		String expression = "";// ���ʽ
		String expressionvalue = "";// ���ʽֵ
		String url = "";// ���ӵ�ַ
		String viewtype = "";// ��ʾ����
		String childworkflowinfo = "";// ˵����Ϣ
		String args = "";// ����
		String checkwhere="";// �������
		
		String v_labeltype = "";// ���Ͳ鿴��ѡ��������
		String v_labelinfo = "";// ��ʾ��Ϣ�磺�鿴����(�ĵ�)
		String v_labelname = "";// ��ʾ����
		String v_labelnameurl="";//�����ӵ�����
		String v_labelurl = "";// �����ӵ���ʾ����
		String v_isend = "";// �Ƿ����
		String v_viewtmp="";//��ʱ����
		
		String v_prjid = requestBean.getPrjid();//�������
		String v_crmid = requestBean.getCrmid();//�������
		String v_busid = requestBean.getBusid();//�������
		for (WorkLabelBean labelBean : labels) {
			v_viewtmp="&prjid="+v_prjid+"&crmid="+v_crmid+"&busid="+v_busid;
			childworkflowid = labelBean.getChildworkflowid();
			childnodeid = labelBean.getChildnodeid();
			expressionvalue=labelBean.getExpressionvalue();
			viewtype = labelBean.getViewtype();
			ctype = labelBean.getCtype();
			args=labelBean.getArgs();
			url=labelBean.getUrl();
			expression = labelBean.getExpression();// ���ʽ
			checkwhere=labelBean.getCheckwhere();// �������
			if(!"1".equals(ctype)){
				v_viewtmp+="&requestid="+requestBean.getRequestid()+"&nodeid="+requestBean.getNodeid();
			}
			if ("1".equals(ctype)) {
				v_labelname=workflowComInfo.getWorkflowname(childworkflowid);
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/workflow/request/AddRequest.jsp?workflowid="+childworkflowid+"&isagent=0&beagenter=0"+v_viewtmp+"');\">"+v_labelname+"</a>";
				v_labelurl="/workflow/request/AddRequest.jsp?workflowid="+childworkflowid+"&isagent=0&beagenter=0"+v_viewtmp;
				viewBean = new ViewLabelBean(viewtype,"����(����)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("2".equals(ctype)){
				v_labelname=mouldComInfo.getDocMouldname(childworkflowid);
				v_viewtmp+="&mainid=9&fromFlowDoc=&requestid=0&secid=22&subid=17&topage=&showsubmit=1&hrmid=&from=&docsubject="+v_labelname+"&keyword="+v_labelname+"&invalidationdate=&docmodule="+childworkflowid+"";
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/docs/docs/DocAddExt.jsp?isfromtable=1"+v_viewtmp+"');\">"+v_labelname+"</a>";
				v_labelurl="/docs/docs/DocAddExt.jsp?isfromtable=1"+v_viewtmp;
				viewBean = new ViewLabelBean(viewtype,"����(�ĵ�)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("4".equals(ctype)){
				v_labelname=docComInfo.getDocname(childworkflowid);
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('/docs/docs/DocDsp.jsp?id="+childworkflowid+"');\">"+v_labelname+"</a>";
				v_labelurl="/docs/docs/DocDsp.jsp?id="+childworkflowid;
				viewBean = new ViewLabelBean(viewtype,"����(�ĵ�)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}else if("5".equals(ctype)){//�Ǽ�
				regBean=new LabelBean(childworkflowid);
				v_labelname=regBean.getLabelname();
				v_labelurl=regBean.getUrl();
				if(v_labelurl.indexOf("?")>-1){
					v_labelurl=v_labelurl+"&isfromreq=1"+args;
				}else{
					v_labelurl=v_labelurl+"?isfromreq=1"+args;
				}
				v_labelurl+=v_viewtmp;//����������Ϣ
				if("view".equalsIgnoreCase(requestBean.getOper())){
					v_labelurl+="&oper=0";//�������� 0,�鿴��1���༭
				}else{
					v_labelurl+="&oper="+viewtype;//��������
				}
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('"+v_labelurl+"');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"����(�Ǽ�)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
				
			}else if("3".equals(ctype)){//ģ��
				moduleBean=new WorkModuleBean(childworkflowid);
				String v_id=moduleBean.getId();//ģ��ID
				v_labelname=moduleBean.getLabelname();
				String v_checkPID=new WorkModuleBean(childnodeid).getPid();
				v_labelurl="/flazyform/workflow/work_module/work_module.jsp?isfromreq=1&panelTitle=0&moduleid="+v_id;
				v_labelurl+=v_viewtmp+args;//����������Ϣ
				if("view".equalsIgnoreCase(requestBean.getOper())){
					v_labelurl+="&oper=0";//�������� 0,�鿴��1���༭
				}else{
					v_labelurl+="&oper="+viewtype;//��������
				}
				v_labelurl+="&limittype="+expressionvalue+"&checkID="+childnodeid+"&checkPID="+v_checkPID;
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('"+v_labelurl+"');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"����(ģ��)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
				labelCheck.LabelCheck(requestBean, labelBean,viewBean);//����Ƿ����
				setMouldViewBean(v_id, viewBean);
			}else if("7".equals(ctype)){
				childworkflowinfo=labelBean.getChildworkflowinfo();
				v_labelname=mouldComInfo.getDocMouldname(childworkflowid);
				viewBean = new ViewLabelBean(viewtype,"˵����Ϣ","˵����Ϣ","<pre>"+childworkflowinfo+"</pre>","",v_isend);
			}else if("6".equals(ctype)){//��̨���
				v_labelname=childnodeid;//��ʾ��Ϣ��
				v_labelurl=args+v_viewtmp;//����������Ϣ
				v_labelnameurl="<a href=\"javascript:openFullWindowForXtable('XURLX');\">"+v_labelname+"</a>";
				viewBean = new ViewLabelBean(viewtype,"����(��̨)",v_labelname,Util.toScreen(v_labelnameurl,7),v_labelurl,v_isend);
			}
			viewBean.setLabeltype(ctype);//��ǩ����;
			if(!"7".equals(ctype)&&!"3".equals(ctype)){//���Ͳ���Ϊ˵����Ϣ
				labelCheck.LabelCheck(requestBean, labelBean,viewBean);//����Ƿ����
			}
			if(!"3".equals(ctype)){//Ϊģ��ʱ������¼ֻ��ʾһ����¼
				labelBeans.add(viewBean);
			}
		}//����ѭ��
		Iterator it = mouldMap.keySet().iterator();
		String key ="";
        while (it.hasNext()) {
            key = Util.null2String((String) it.next());
            labelBeans.add(0,mouldMap.get(key));
        }
		return labelBeans;
	}
	private void setMouldViewBean(String v_id,ViewLabelBean viewBean){
		//System.out.println("ģ�飺"+viewBean.toString());
		if(!mouldMap.containsKey(v_id)){//������
			mouldMap.put(v_id, viewBean);
		}else{//����Ϊ����
			if("0".equals(viewBean.getIsend())){//����δ����	
				if("1".equals(mouldMap.get(v_id).getIsend())){//�ɵ��ѽ���
					mouldMap.put(v_id, viewBean);
				}else{//�ɵ�δ����
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
