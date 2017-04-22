package two.work.interfaces;

import two.common.CommonInfoByID;
import two.common.RequestOutData;
import two.share.DocShareInfo;
import weaver.conn.RecordSet;
import weaver.docs.docs.DocViewer;
import weaver.file.FileUpload;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.workflow.request.RequestComInfo;
import weaver.workflow.request.WFForwardManager;
import weaver.workflow.workflow.WorkflowComInfo;

public class WSNodeAction {
	String ProcPara="";
	char flag = 2;
	String v_sql = "";
	CommonInfoByID common=new CommonInfoByID();
	DocViewer docViewer=new DocViewer();
	RecordSet rs = new RecordSet();
    /**
     * 
     * @param outData 
     * @param fu
     * @param workflowid
     * @param requestid
     * @param v_nodeid
     * @param v_userid
     * @param isupdate
     */
	public void sendWorkFlow(RequestOutData outData, FileUpload fu,String workflowid, String requestid, String v_nodeid, String v_userid,boolean isupdate,User user) {
		try {	
			v_sql = "select * from workflow_currentoperator  where userid ='"+ v_userid + "' and requestid='" + requestid+"' and nodeid='" + v_nodeid + "'";// 查询那些用户已转发了
			rs.executeSql(v_sql);
			if(rs.next()){
				if(isupdate==true){//如果要更新
					v_sql="update workflow_currentoperator set isremark='1',viewtype=0,iscomplete=0 where userid ='"+v_userid+"' and requestid='"+requestid+"' and nodeid='"+v_nodeid+"'";
					common.executeSql(v_sql,"update");
				}
			}else{
				this.writeCurrentOperator(outData, fu, workflowid, requestid, v_nodeid, v_userid,user);
			}
			DocShareInfo.setDocShareInfo(outData.getData("wjzw"), v_userid,"0", "3");
			docViewer.setDocShareByDoc(outData.getData("wjzw"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeCurrentOperator(RequestOutData outData, FileUpload fu,String workflowid, String requestid, String v_nodeid, String v_userid,User user){
		RecordSet recordSet=new RecordSet();
		WorkflowComInfo workflowComInfo =null;
		RequestComInfo requestComInfo=null;
		WFForwardManager wfForwardManager=new WFForwardManager();
		try{
			workflowComInfo = new WorkflowComInfo();
			requestComInfo=new RequestComInfo();
			String workflowtype=workflowComInfo.getWorkflowtype(workflowid);
			int BeForwardid=-1;
			int wfcurrrid=-1;
			ProcPara = requestid;
			ProcPara += flag + v_userid;
			ProcPara += flag + "0"; 
			ProcPara += flag + workflowid; 
			ProcPara += flag + workflowtype; 
			ProcPara += flag + "0";//usertype 
			ProcPara += flag + "1";//转发
			ProcPara += flag + v_nodeid; 	
			ProcPara += flag + "-1";
			ProcPara += flag + "0"; 
			ProcPara += flag + "100";//显示位置	
			ProcPara += flag + "-1"; 
			recordSet.executeProc("workflow_CurrentOperator_I",ProcPara);//写入数据
			v_sql = "select max(id) as id from workflow_currentoperator  where userid ='"+user.getUID()+ "' and requestid='" + requestid+"' and nodeid='" + v_nodeid + "'";
			recordSet.executeSql(v_sql);
			if(recordSet.next()){//创建人操作表ID
				wfcurrrid=Util.getIntValue(recordSet.getString("id"));
			}
			v_sql = "select max(id) as id from workflow_currentoperator  where userid ='"+ v_userid + "' and requestid='" + requestid+"' and nodeid='" + v_nodeid + "'";
			recordSet.executeSql(v_sql);
			if(recordSet.next()){
				BeForwardid=Util.getIntValue(recordSet.getString("id"));
				wfForwardManager.setForwardRight(fu,Util.getIntValue(requestid),Util.getIntValue(workflowid),Util.getIntValue(v_nodeid),user.getUID());
				wfForwardManager.SaveForward(Util.getIntValue(requestid),wfcurrrid,BeForwardid);
			}
		}catch (Exception e) {
			new BaseBean().writeLog("异常:"+e.getMessage());
		}
	}
}
