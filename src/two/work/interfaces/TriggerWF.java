package two.work.interfaces;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import two.common.CommonInfoByID;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;
import weaver.system.SysCreateWF;
import weaver.workflow.workflow.WorkflowComInfo;

/**
 * @author hongyun.xu
 * 
 */
public class TriggerWF extends BaseAction {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	RecordSetTrans rs = null;
	RecordSet recordSet = null;

	public TriggerWF() {
		super();
		rs = new RecordSetTrans();
		recordSet = new RecordSet();
	}

	@Override
	public String execute(RequestInfo req) {
		isCreateDYWYH(req);
		isCreateDSFPG(req);
		return SUCCESS;
	}

	/**
	 * 判断是否发起“定价委员会”，如需发起，直接发起
	 * 
	 * @param req请求ID
	 */
	public void isCreateDYWYH(RequestInfo req) {
		String busid = (String) getParams(req).get("busid");
		String sql = " select count(id) as cnt from nd_revMeasures where dr=0  and iscommittee = 1 and  busid = '"+ busid + "'";
		recordSet.executeSql(sql);
		recordSet.next();
		if(recordSet.getInt("cnt")>0){
			createWF(req, 190, "定价委员会");
		}
	}

	/**
	 * 判断是否发起“第三方评估”，如需发起，直接发起
	 * 
	 * @param req请求ID
	 */
	public void isCreateDSFPG(RequestInfo req) {
		String busid = (String) getParams(req).get("busid");
		String sql = "select count(id) as cnt from nd_revMeasures where dr=0  and ismaep = 1 and  busid = '"+ busid + "'";
		recordSet.executeSql(sql);
		recordSet.next();
		if(recordSet.getInt("cnt")>0){
			createWF(req, 128, "第三方评估");
		}
	}

	/**
	 * 发起流程
	 * 
	 * @param req请求ID
	 * @param workflowid流程ID
	 * @param workflowname流程名称
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createWF(RequestInfo req, int workflowid, String workflowname) {
		String prjid = "";// 项目名称

		Map paramMap = getParams(req);
		prjid = (String) paramMap.get("prjid");
		Map mapInfo = getPrjInfoByPrjid(prjid);

		String tbr = (String) mapInfo.get("creater");// 项目立项人
		String tbbu = (String) mapInfo.get("department");// 立项部门
		String tbrq = (String) mapInfo.get("createdate");// 立项日期
		String crmid = (String) paramMap.get("crmid");// 客户名称
		String busid = (String) paramMap.get("busid");// 相关业务
		try {
			ArrayList valueList = new ArrayList();
			String sql = "";
			WorkflowComInfo workflowcomInfo = new WorkflowComInfo();
			String formid = Util.null2String(workflowcomInfo
					.getFormId(workflowid + ""));
			sql = "select fieldname as name from workflow_billfield where viewtype=0 and billid = "
					+ formid + " order by dsporder ";
			recordSet.executeSql(sql);
			while (recordSet.next()) {
				String fieldname = recordSet.getString("name");
				if ("tbr".equals(fieldname)) {
					valueList.add(tbr);
				} else if ("tbbu".equals(fieldname)) {
					valueList.add(tbbu);
				} else if ("tbrq".equals(fieldname)) {
					valueList.add(tbrq);
				} else if ("crmid".equals(fieldname)) {
					valueList.add(crmid);
				} else if ("busid".equals(fieldname)) {
					valueList.add(busid);
				} else if ("prjid".equals(fieldname)) {
					valueList.add(prjid);
				} else {
					valueList.add("");
				}
			}
			int creater = req.getRequestManager().getUserId();
			new SysCreateWF().setWorkflowInfo(workflowid, workflowname + "-"
					+ new CommonInfoByID().LookNameByHrmID(creater + "") + "-"
					+ sdf.format(new Date()), creater, valueList);
		} catch (Exception e) {
			System.out.println(sdf.format(new Date()) + ":触发流程异常");
		}
	}

	/**
	 * 通过项目Id，得到项目基本信息
	 * 
	 * @param prjid
	 * @return装有项目基本信息的集合
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getPrjInfoByPrjid(String prjid) {
		Map map = new HashMap();
		String sql = "select name,creater,createdate,department from Prj_Projectinfo where id='"
				+ prjid + "'";
		try {
			recordSet.executeSql(sql);
			if (recordSet.next()) {
				map.put("name", recordSet.getString("name"));
				map.put("creater", recordSet.getString("creater"));
				map.put("createdate", recordSet.getString("createdate"));
				map.put("department", recordSet.getString("department"));
			}
			return map;
		} catch (Exception e) {
			System.out.println(sdf.format(new Date()) + ":加载项目基本信息错误");
		}
		return map;
	}

	/**
	 * 从原流程中提取要用的参数信息
	 * 
	 * @param req
	 * @return参数信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParams(RequestInfo req) {
		Map map = new HashMap();
		try {
			String sql = "select crmids,prjids,busid,lastoperator from workflow_requestbase  where  requestid = '"
					+ req.getRequestid() + "'";
			rs = req.getRsTrans();
			if (rs == null) {
				rs = new RecordSetTrans();
			}
			rs.executeSql(sql);
			if (rs.next()) {
				map.put("prjid", Util.null2String(rs.getString("prjids")));
				map.put("crmid", Util.null2String(rs.getString("crmids")));
				map.put("busid", Util.null2String(rs.getString("busid")));
			}
			rs = null;
			return map;
		} catch (Exception e) {
			System.out.println(sdf.format(new Date()) + ":提取参数信息错误");
		}
		return map;
	}
}
