package two.common;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import two.commonsms.SendSMS;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.proj.Maint.ProjectInfoComInfo;
import weaver.system.SysRemindWorkflow;

public class CommonRemind extends BaseBean {
	private RecordSet rs = new RecordSet();
	private List<RemindBean> emailList;// 邮件发送
	private List<RemindBean> SMSList;// 短信发送
	private List<RemindBean> WFList;// 流程发送

	/**
	 * 系统检测时调用该方法
	 * 
	 * @throws Exception
	 */
	public void sysDetection() throws Exception {
		System.out.println(new java.util.Date() + "扫描提醒数据");
		emailList = new ArrayList<RemindBean>();
		SMSList = new ArrayList<RemindBean>();
		WFList = new ArrayList<RemindBean>();
		List<RemindBean> list = loadRemindRecord();// 在数据库中查提醒数据
		for (RemindBean remindBean : list) {
			// "0"为流程监控，"1"为表单监控
			if ("0".equals(remindBean.getType())) {
				WFMonitoring(remindBean);
			} else if ("1".equals(remindBean.getType())) {
				FormMonitoring(remindBean);
			}
		}
		System.out.println(new java.util.Date() + "邮件提醒：" + emailList.size()
				+ "条");
		System.out.println(new java.util.Date() + "短信提醒：" + SMSList.size()
				+ "条");
		System.out
				.println(new java.util.Date() + "流程提醒：" + WFList.size() + "条");
		this.sendByWF();
		this.sendBySMS();
		this.sendByEmail();
	}

	/**
	 * 流程监控
	 * 
	 * @param remindBean
	 */
	public void WFMonitoring(RemindBean remindBean) {
		RecordSet recordSet = new RecordSet();
		String v_date = "to_date('" + new Date(System.currentTimeMillis())
				+ "','yyyy-MM-dd')";// 当前时间
		String v_flow = remindBean.getProcessname();// 流程id
		String v_node = remindBean.getMonitornode();// 节点id
		String v_togglecondition = remindBean.getTogglecondition();// 触发条件
		String v_days = remindBean.getDays();// 时间
		String v_remindgroup = remindBean.getRemindgroup();// 提醒组
		String v_users = remindBean.getUsers();// 提醒成员

		String v_sql = "select requestid,userid from workflow_currentoperator where 1=1 and workflowid="
				+ v_flow + " and nodeid = " + v_node;
		String v_sqlWhere = "";
		if ("0".equals(v_togglecondition)) {// 接收未处理
			v_sqlWhere = " and floor(" + v_date
					+ " - to_date(receivedate,'yyyy-MM-dd')" + ")=" + v_days
					+ " and operatedate is null  and isreject is null";

		} else if ("1".equals(v_togglecondition)) {// 处理后
			v_sqlWhere = " and floor(" + v_date
					+ " - to_date(operatedate,'yyyy-MM-dd')" + ")=" + v_days
					+ " and isreject is null";
		} else if ("5".equals(v_togglecondition)) {// 未处理间断提醒
			v_sqlWhere = " and mod(floor(" + v_date
					+ " - to_date(receivedate,'yyyy-MM-dd'))," + v_days
					+ ")=0 and operatedate is null and  isreject is null ";
		}
		v_sql += v_sqlWhere;
		System.out.println(v_sql);
		if (recordSet.executeSql(v_sql)) {
			while (recordSet.next()) {
				String v_requestId = "";
				String v_userid = "";
				RemindBean rb = new RemindBean();
				RequestOutData outData = new RequestOutData();
				v_requestId = recordSet.getString("requestId");
				v_userid = recordSet.getString("userid");
				outData.loadData(v_requestId);
				remindBean.setPrjid(outData.getData("prjid"));
				remindBean.setCrmid(outData.getData("crmid"));
				if ("".equals(remindBean.getCrmid())) {
					try {
						remindBean.setCrmid(new ProjectInfoComInfo()
								.getProjectInfodesc(remindBean.getPrjid()));
					} catch (Exception e) {

					}
				}

				if ("0".equals(v_remindgroup)) {// "0"表示'流程接收人'
					remindBean.setUsers("," + v_userid);
				} else if ("2".equals(v_remindgroup)) {// "2"表示'接收人上级'
					remindBean.setUsers("," + checkSuperId(v_userid));
				} else if ("3".equals(v_remindgroup)) {// "3"表示'上级的上级'
					remindBean.setUsers(","
							+ checkSuperId(checkSuperId(v_userid)));
				} else if ("4".equals(v_remindgroup)) {// "4"表示'角色'
					remindBean.setUsers(getUsersByRoles(v_users));
				} else if ("6".equals(v_remindgroup)) {// 项目主调
					String v_prjid = remindBean.getPrjid();
					v_sql = " select manager from Prj_ProjectInfo where id='"
							+ v_prjid + "'";
					remindBean.setUsers(","
							+ Util.null2String((String) new CommonInfoByID()
									.executeSql(v_sql).get("manager")));
				} else if (!"".equals(remindBean.getCrmid()) && 0 != 0) {// 0!=0无效
					remindBean.setUsers(remindBean.getCrmid());
					sendToCustomer(remindBean);// 发送到客户
					return;
				}
				rb = remindBean;
				this.judgeWay(rb);// 判断发送方式
			}
		} else {
			return;// 数据错误
		}

	}

	/**
	 * 查找直接上级id，传入userId，返回返回直接上级Id
	 * 
	 * @param userid
	 * @return
	 */
	public String checkSuperId(String userid) {
		RecordSet recordSet = new RecordSet();
		String v_superId = "";
		recordSet.executeSql("select managerid from hrmresource where id="
				+ userid);
		if (recordSet.next()) {
			v_superId = recordSet.getString("managerid");
		}
		return v_superId;
	}

	/**
	 * 表单监控
	 */
	public void FormMonitoring(RemindBean remindBean) {
		String v_tableName = "";// 表单名称
		String v_formId = remindBean.getProcessname();// 表单Id
		String v_sqlWhere = remindBean.getSqlcondition();// sql条件
		String v_field = remindBean.getMonitornode();// 选择字段
		String v_togglecondition = remindBean.getTogglecondition();// 触发条件
		String v_days = remindBean.getDays();// 天数
		String v_remindgroup = remindBean.getRemindgroup();// 提醒组
		String v_users = remindBean.getUsers();// 提醒成员
		RecordSet recordSet = new RecordSet();

		String v_date = "to_date('" + new Date(System.currentTimeMillis())
				+ "','yyyy-MM-dd')";

		if ("".equals(v_formId)) {
			return;// 没有选择表单
		}

		String v_sql = "select tablename from work_label where id=" + v_formId;
		rs.executeSql(v_sql);
		if (rs.next()) {
			v_tableName = rs.getString("tablename");
		} else {
			return;// 没有选择表单
		}
		v_sql = "select id from " + v_tableName + " where 1=1 ";
		if ("2".equals(v_togglecondition)) {// 选择字段前
			v_sqlWhere += " and floor(to_date(" + v_field + ",'yyyy-MM-dd')-"
					+ v_date + ")=" + v_days;
		} else if ("3".equals(v_togglecondition)) {// 选择字段后
			v_sqlWhere += " and floor(" + v_date + "-to_date(" + v_field
					+ ",'yyyy-MM-dd'))=" + v_days;
		} else if ("4".equals(v_togglecondition)) {// 创建记录后
			v_sqlWhere += " and floor(" + v_date
					+ "-to_date(createdate,'yyyy-MM-dd'))=" + v_days;
		}
		v_sql = v_sql + v_sqlWhere;
		if (rs.executeSql(v_sql)) {
			while (rs.next()) {// 多条符合条件的数据
				RemindBean rb = new RemindBean();
				CommonOutData outData = new CommonOutData();
				String recodeId = rs.getString("id");
				outData.loadData(v_tableName, recodeId);
				remindBean.setPrjid(outData.getData("prjid"));
				remindBean.setCrmid(outData.getData("crmid"));
				if ("".equals(remindBean.getCrmid())) {
					try {
						remindBean.setCrmid(new ProjectInfoComInfo()
								.getProjectInfodesc(remindBean.getPrjid()));
					} catch (Exception e) {

					}
				}
				if ("4".equals(v_remindgroup)) {// "4"表示'角色'
					remindBean.setUsers(getUsersByRoles(v_users));
				} else if ("1".equals(v_remindgroup)) {// "1"表示'表单人力字段'
					v_sql = "select " + v_users + " from " + v_tableName
							+ " where id = " + recodeId;
					System.out.println(new java.util.Date() + "\t" + v_sql);
					recordSet.executeSql(v_sql);
					if (recordSet.next()) {
						String name = recordSet.getString(v_users);
						if (v_users.endsWith("crmid")) {
							sendToCustomer(remindBean);// 发送到客户
							return;
						} else {
							if (name.startsWith(",")) {
								remindBean.setUsers(name);
							} else {
								remindBean.setUsers("," + name);
							}
						}
					}
				} else if (!"".equals(remindBean.getCrmid()) && 0 != 0) {// &&0!=0无效代码
					remindBean.setUsers(remindBean.getCrmid());
					sendToCustomer(remindBean);// 发送到客户
					return;
				}
				rb = remindBean;
				this.judgeWay(rb);// 判断发送方式
			}
		} else
			return;// 数据错误
	}

	/**
	 * 发送到客户
	 * 
	 * @param remindBean
	 */
	private void sendToCustomer(RemindBean remindBean) {
		RecordSet recordSet = new RecordSet();
		String v_way = remindBean.getWay();
		String v_msg = remindBean.getContent();
		String v_crmid = remindBean.getUsers();
		String v_sql = "";
		if ("0".equals(v_way)) {
			String v_email = "";
			v_sql = "select email from CRM_CustomerInfo where id=" + v_crmid;
			if (recordSet.executeSql(v_sql)) {
				if (recordSet.next()) {
					v_email = recordSet.getString("phone");
					this.sendByEmail(v_email, v_msg);// 邮件发送
				}
			}
		} else if ("2".equals(v_way)) {
			String v_mobile = "";
			v_sql = "select phone from CRM_CustomerInfo where id=" + v_crmid;
			if (recordSet.executeSql(v_sql)) {
				if (recordSet.next()) {
					v_mobile = recordSet.getString("phone");
					this.sendBySMS(v_mobile, v_msg);// 短信发送
				}
			}
		}
	}

	/**
	 * 判断提醒方式,'0'为Email提醒、'1'为流程提醒、'2'为短信提醒
	 * 
	 * @param remindBean传入RemindBean
	 *            ,将RemindBean加入对应的集合
	 */
	public void judgeWay(RemindBean remindBean) {
		String v_way = remindBean.getWay();
		if ("1".equals(v_way)) {
			WFList.add(remindBean);
		} else if ("2".equals(v_way)) {
			SMSList.add(remindBean);
		} else if ("0".equals(v_way)) {
			emailList.add(remindBean);
		}
	}

	/**
	 * 将多角色id转换为多人力资源id 多角色id格式为:",xx,xx,xx",注意第一位为逗号
	 * 返回的多人力资源id格式为:",xx,xx,xx",第一位也是逗号
	 * 
	 * @param roleids多角色id
	 * @return多人力资源id
	 */
	public String getUsersByRoles(String roleids) {
		String users = "";
		roleids = roleids.substring(roleids.indexOf(",") + 1);
		String v_sql = "select distinct(resourceid) from hrmrolemembers  where roleid in("
				+ ("".equals(roleids) ? 0 : roleids) + ")";
		rs.executeSql(v_sql);
		while (rs.next()) {
			users += "," + Util.null2String(rs.getString("resourceid"));
		}
		return users;
	}

	/**
	 * 在数据库查询需要提醒的记录
	 * 
	 * @return提醒记录的List集合
	 */
	public List<RemindBean> loadRemindRecord() {
		List<RemindBean> list = new ArrayList<RemindBean>();
		RecordSet recordSet = new RecordSet();
		String sql = "select ctype,processname,monitornode,sqlcondition,togglecondition,days,content,way,remindgroup,users,remark from work_remind where dr=0";
		recordSet.executeSql(sql);
		while (recordSet.next()) {
			String v_type = Util.null2String(recordSet.getString("ctype"));// 监控类别
			String v_processname = Util.null2String(recordSet
					.getString("processname"));// 流程/表单名称
			String v_monitornode = Util.null2String(recordSet
					.getString("monitornode"));// 监控节点（字段）
			String v_sqlcondition = Util.null2String(recordSet
					.getString("sqlcondition"));// sql条件
			String v_togglecondition = Util.null2String(recordSet
					.getString("togglecondition"));// 触发条件
			String v_days = Util.null2String(recordSet.getString("days"));// 时间（天）
			String v_content = Util.null2String(recordSet.getString("content"));// 提醒内容
			String v_way = Util.null2String(recordSet.getString("way"));// 提醒方式
			String v_remindgroup = Util.null2String(recordSet
					.getString("remindgroup"));// 提醒组
			String v_users = Util.null2String(recordSet.getString("users"));// 提醒人员
			String v_remark = Util.null2String(recordSet.getString("remark"));// 备注
			list.add(new RemindBean(v_type, v_processname, v_monitornode,
					v_sqlcondition, v_togglecondition, v_days, v_content,
					v_way, v_remindgroup, v_users, v_remark));
		}
		return list;
	}

	/**
	 * 系统通过流程提醒
	 */
	public void sendByWF() {
		SysRemindWorkflow remindWorkflow = new SysRemindWorkflow();
		// 信息统一以“系统管理员”身份发出
		for (RemindBean remindBean : WFList) {
			System.out.println(("发送时数据：" + remindBean));
			String v_content = remindBean.getContent();
			int v_crmid = "".equals(remindBean.getCrmid()) ? 0 : Integer
					.parseInt(remindBean.getCrmid());
			int v_prjid = "".equals(remindBean.getPrjid()) ? 0 : Integer
					.parseInt(remindBean.getPrjid());
			String v_users = "".equals(remindBean.getUsers()) ? "0"
					: remindBean.getUsers().trim().substring(1);

			try {
				int rid = remindWorkflow.make("系统提醒", 0, v_crmid, v_prjid, 0,
						1, v_users, v_content);
				System.out.println("v_crmid = " + v_crmid + ", v_prjid = "
						+ v_prjid + ", v_users = " + v_users + ", v_content = "
						+ v_content + " \t rid = " + rid);
			} catch (Exception e) {
				super.writeLog(e.getMessage(), e);
			}
		}
		remindWorkflow = null;
		WFList = null;
	}

	/**
	 * 系统通过短信提醒
	 */
	public void sendBySMS() {
		for (RemindBean remindBean : SMSList) {
			String v_msg = remindBean.getContent();
			String v_users = remindBean.getUsers();
			String[] v_userids;
			String v_sql = "select mobile from HRMRESOURCE where id=";
			if (!"".equals(v_users)) {
				v_userids = v_users.substring(1).split(",");
				for (int i = 0; i < v_userids.length; i++) {
					String v_mobile = "";
					rs.executeSql(v_sql + v_userids[i]);// 查询移动电话
					if (rs.next()) {
						v_mobile = rs.getString("mobile");
						this.sendBySMS(v_mobile, v_msg);
					}
				}
			}
		}
		SMSList = null;
	}

	/**
	 * 短信发送
	 * 
	 * @param mobile
	 * @param msg
	 */
	public void sendBySMS(String mobile, String msg) {
		// RecordSet recordSet = new RecordSet();
		if (mobile.matches("[0-9]+")) {
			// recordSet.executeSql("insert into OutTable(ReceiverMobileNo,Msg,SendTime,IsChinese,ExpressLevel,Sender,status) values("+
			// mobile + ",'" + msg + "',sysdate,1,1,1,0)");
			new SendSMS().sendSMS("-1", mobile, msg);
			System.out.println("待发送信息：号码：" + mobile + ",内容：" + msg);
		} else {
			System.out.println("移动电话号码有误!!"
					+ new Timestamp(System.currentTimeMillis()));
		}
	}

	/**
	 * 系统通过E-mail提醒
	 */
	public void sendByEmail() {
		for (RemindBean remindBean : emailList) {
			String v_content = remindBean.getContent();
			String v_users = remindBean.getUsers();
			String[] v_userids;
			String v_sql = "select email from HRMRESOURCE where id=";
			if (!"".equals(v_users)) {
				v_userids = v_users.substring(1).split(",");
				for (int i = 0; i < v_userids.length; i++) {
					String v_email = "";
					rs.executeSql(v_sql + v_userids[i]);// 查询邮箱
					if (rs.next()) {
						v_email = rs.getString("email");
						this.sendByEmail(v_email, v_content);
					}
				}
			}
		}
		emailList = null;
	}

	/**
	 * E-mail提醒
	 * 
	 * @param email
	 * @param msg
	 * @throws EmailException
	 */
	public void sendByEmail(String mail, String msg) {
		String hostName = super.getPropValue("work_dev", "hostName");
		String smtpPort = super.getPropValue("work_dev", "smtpPort");
		String userName = super.getPropValue("work_dev", "userName");
		String password = super.getPropValue("work_dev", "password");
		String from = super.getPropValue("work_dev", "from");
		String title = super.getPropValue("work_dev", "title");
		Email email = new SimpleEmail();
		email.setCharset("UTF-8");
		email.setHostName(hostName);
		email.setSmtpPort(Util.getIntValue(smtpPort));
		email.setAuthenticator(new DefaultAuthenticator(userName, password));
		email.setTLS(true);
		try {
			email.setFrom(from);
			email.setSubject(title);
			//msg = new String(msg.getBytes("ISO-8859-1"),"GB2312");
			email.setMsg(msg);
			email.addTo(mail);
			email.send();
			System.out.println("邮件已发送：title:"+title+"E-mail:" + mail + ", msg:" + msg);
		} catch (Exception e) {
			System.out.println("邮件发送失败：E-mail:" + mail + ", msg:" + msg);
			System.out.println("email = "+email);
			e.printStackTrace();
		}
	}
}
