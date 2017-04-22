package two.work.interfaces;

import two.common.RequestOutData;
import weaver.conn.RecordSet;
import weaver.general.Util;

public class NodeAction {
	String v_sql="";
	RecordSet rs=new RecordSet();
	public void doSaveInfo(RequestOutData outData,String workflowid,String requestid,String nodeid) {
		if(",147,496,".indexOf(","+workflowid+",")>-1&&",672,1801,1802,1599,1831,1832,".indexOf(","+nodeid+",")>-1){
			this.doBusReviewInfo(outData, workflowid, requestid, nodeid);
		}
	}
	/**
	 * 修改业务表中评审信息，评审金额，评审时间
	 */
	private void doBusReviewInfo(RequestOutData outData,String workflowid,String requestid,String nodeid) {
		String v_psamount=Util.null2String(outData.getData("psamount"));
		String v_psqx=Util.null2String(outData.getData("psqx"));
		String v_busid=Util.null2String(outData.getData("busid"));
		v_sql="update nd_business set psamount='"+v_psamount+"',pslimit='"+v_psqx+"' where id='"+v_busid+"'";
		rs.writeLog(v_sql);
		rs.executeSql(v_sql);//执行
	}
}
