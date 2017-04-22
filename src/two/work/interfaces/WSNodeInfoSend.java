package two.work.interfaces;

import two.common.CommonInfoByID;
import two.common.RequestOutData;
import two.share.DocShareInfo;
import weaver.conn.RecordSet;
import weaver.docs.docs.DocViewer;
import weaver.file.FileUpload;
import weaver.general.BaseBean;
import weaver.hrm.User;
import weaver.workflow.workflow.WorkflowComInfo;

public class WSNodeInfoSend {
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
	public void sendWorkFlow(RequestOutData outData, FileUpload fu,String workflowid, String requestid, String v_nodeid,String v_isremark, String v_userid,boolean isupdate,User user) {
		try {	
			v_sql = "select * from workflow_currentoperator  where userid ='"+ v_userid + "' and requestid='" + requestid+"' and nodeid='" + v_nodeid + "'";// 查询那些用户已转发了
			rs.executeSql(v_sql);
			if(rs.next()){
				if(isupdate==true){//如果要更新
					v_sql="update workflow_currentoperator set isremark='9',viewtype=0,iscomplete=0 where userid ='"+v_userid+"' and requestid='"+requestid+"' and nodeid='"+v_nodeid+"'";
					common.executeSql(v_sql,"update");
				}
			}else{
				this.sendCurrOper(outData, fu, workflowid, requestid, v_nodeid,v_isremark, v_userid,user);
			}
			DocShareInfo.setDocShareInfo(outData.getData("wjzw"), v_userid,"0", "1");
			docViewer.setDocShareByDoc(outData.getData("wjzw"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendCurrOper(RequestOutData outData, FileUpload fu,String workflowid, String requestid, String v_nodeid,String v_isremark, String v_userid,User user){
		RecordSet recordSet=new RecordSet();
		WorkflowComInfo workflowComInfo =null;
		try{
			workflowComInfo = new WorkflowComInfo();
			String workflowtype=workflowComInfo.getWorkflowtype(workflowid);
			ProcPara = requestid;
			ProcPara += flag + v_userid;
			ProcPara += flag + "0"; 
			ProcPara += flag + workflowid; 
			ProcPara += flag + workflowtype; 
			ProcPara += flag + "0";//usertype 
			ProcPara += flag + v_isremark;//
			ProcPara += flag + v_nodeid; 	
			ProcPara += flag + "-1";
			ProcPara += flag + "0"; 
			ProcPara += flag + "100";//显示位置	
			ProcPara += flag + "-1"; 
			recordSet.executeProc("workflow_CurrentOperator_I",ProcPara);//写入数据
		}catch (Exception e) {
			new BaseBean().writeLog("异常:"+e.getMessage());
		}
	}
}
