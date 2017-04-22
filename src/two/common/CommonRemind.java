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
	private List<RemindBean> emailList;// �ʼ�����
	private List<RemindBean> SMSList;// ���ŷ���
	private List<RemindBean> WFList;// ���̷���

	/**
	 * ϵͳ���ʱ���ø÷���
	 * 
	 * @throws Exception
	 */
	public void sysDetection() throws Exception {
		System.out.println(new java.util.Date() + "ɨ����������");
		emailList = new ArrayList<RemindBean>();
		SMSList = new ArrayList<RemindBean>();
		WFList = new ArrayList<RemindBean>();
		List<RemindBean> list = loadRemindRecord();// �����ݿ��в���������
		for (RemindBean remindBean : list) {
			// "0"Ϊ���̼�أ�"1"Ϊ�����
			if ("0".equals(remindBean.getType())) {
				WFMonitoring(remindBean);
			} else if ("1".equals(remindBean.getType())) {
				FormMonitoring(remindBean);
			}
		}
		System.out.println(new java.util.Date() + "�ʼ����ѣ�" + emailList.size()
				+ "��");
		System.out.println(new java.util.Date() + "�������ѣ�" + SMSList.size()
				+ "��");
		System.out
				.println(new java.util.Date() + "�������ѣ�" + WFList.size() + "��");
		this.sendByWF();
		this.sendBySMS();
		this.sendByEmail();
	}

	/**
	 * ���̼��
	 * 
	 * @param remindBean
	 */
	public void WFMonitoring(RemindBean remindBean) {
		RecordSet recordSet = new RecordSet();
		String v_date = "to_date('" + new Date(System.currentTimeMillis())
				+ "','yyyy-MM-dd')";// ��ǰʱ��
		String v_flow = remindBean.getProcessname();// ����id
		String v_node = remindBean.getMonitornode();// �ڵ�id
		String v_togglecondition = remindBean.getTogglecondition();// ��������
		String v_days = remindBean.getDays();// ʱ��
		String v_remindgroup = remindBean.getRemindgroup();// ������
		String v_users = remindBean.getUsers();// ���ѳ�Ա

		String v_sql = "select requestid,userid from workflow_currentoperator where 1=1 and workflowid="
				+ v_flow + " and nodeid = " + v_node;
		String v_sqlWhere = "";
		if ("0".equals(v_togglecondition)) {// ����δ����
			v_sqlWhere = " and floor(" + v_date
					+ " - to_date(receivedate,'yyyy-MM-dd')" + ")=" + v_days
					+ " and operatedate is null  and isreject is null";

		} else if ("1".equals(v_togglecondition)) {// �����
			v_sqlWhere = " and floor(" + v_date
					+ " - to_date(operatedate,'yyyy-MM-dd')" + ")=" + v_days
					+ " and isreject is null";
		} else if ("5".equals(v_togglecondition)) {// δ����������
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

				if ("0".equals(v_remindgroup)) {// "0"��ʾ'���̽�����'
					remindBean.setUsers("," + v_userid);
				} else if ("2".equals(v_remindgroup)) {// "2"��ʾ'�������ϼ�'
					remindBean.setUsers("," + checkSuperId(v_userid));
				} else if ("3".equals(v_remindgroup)) {// "3"��ʾ'�ϼ����ϼ�'
					remindBean.setUsers(","
							+ checkSuperId(checkSuperId(v_userid)));
				} else if ("4".equals(v_remindgroup)) {// "4"��ʾ'��ɫ'
					remindBean.setUsers(getUsersByRoles(v_users));
				} else if ("6".equals(v_remindgroup)) {// ��Ŀ����
					String v_prjid = remindBean.getPrjid();
					v_sql = " select manager from Prj_ProjectInfo where id='"
							+ v_prjid + "'";
					remindBean.setUsers(","
							+ Util.null2String((String) new CommonInfoByID()
									.executeSql(v_sql).get("manager")));
				} else if (!"".equals(remindBean.getCrmid()) && 0 != 0) {// 0!=0��Ч
					remindBean.setUsers(remindBean.getCrmid());
					sendToCustomer(remindBean);// ���͵��ͻ�
					return;
				}
				rb = remindBean;
				this.judgeWay(rb);// �жϷ��ͷ�ʽ
			}
		} else {
			return;// ���ݴ���
		}

	}

	/**
	 * ����ֱ���ϼ�id������userId�����ط���ֱ���ϼ�Id
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
	 * �����
	 */
	public void FormMonitoring(RemindBean remindBean) {
		String v_tableName = "";// ������
		String v_formId = remindBean.getProcessname();// ��Id
		String v_sqlWhere = remindBean.getSqlcondition();// sql����
		String v_field = remindBean.getMonitornode();// ѡ���ֶ�
		String v_togglecondition = remindBean.getTogglecondition();// ��������
		String v_days = remindBean.getDays();// ����
		String v_remindgroup = remindBean.getRemindgroup();// ������
		String v_users = remindBean.getUsers();// ���ѳ�Ա
		RecordSet recordSet = new RecordSet();

		String v_date = "to_date('" + new Date(System.currentTimeMillis())
				+ "','yyyy-MM-dd')";

		if ("".equals(v_formId)) {
			return;// û��ѡ���
		}

		String v_sql = "select tablename from work_label where id=" + v_formId;
		rs.executeSql(v_sql);
		if (rs.next()) {
			v_tableName = rs.getString("tablename");
		} else {
			return;// û��ѡ���
		}
		v_sql = "select id from " + v_tableName + " where 1=1 ";
		if ("2".equals(v_togglecondition)) {// ѡ���ֶ�ǰ
			v_sqlWhere += " and floor(to_date(" + v_field + ",'yyyy-MM-dd')-"
					+ v_date + ")=" + v_days;
		} else if ("3".equals(v_togglecondition)) {// ѡ���ֶκ�
			v_sqlWhere += " and floor(" + v_date + "-to_date(" + v_field
					+ ",'yyyy-MM-dd'))=" + v_days;
		} else if ("4".equals(v_togglecondition)) {// ������¼��
			v_sqlWhere += " and floor(" + v_date
					+ "-to_date(createdate,'yyyy-MM-dd'))=" + v_days;
		}
		v_sql = v_sql + v_sqlWhere;
		if (rs.executeSql(v_sql)) {
			while (rs.next()) {// ������������������
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
				if ("4".equals(v_remindgroup)) {// "4"��ʾ'��ɫ'
					remindBean.setUsers(getUsersByRoles(v_users));
				} else if ("1".equals(v_remindgroup)) {// "1"��ʾ'�������ֶ�'
					v_sql = "select " + v_users + " from " + v_tableName
							+ " where id = " + recodeId;
					System.out.println(new java.util.Date() + "\t" + v_sql);
					recordSet.executeSql(v_sql);
					if (recordSet.next()) {
						String name = recordSet.getString(v_users);
						if (v_users.endsWith("crmid")) {
							sendToCustomer(remindBean);// ���͵��ͻ�
							return;
						} else {
							if (name.startsWith(",")) {
								remindBean.setUsers(name);
							} else {
								remindBean.setUsers("," + name);
							}
						}
					}
				} else if (!"".equals(remindBean.getCrmid()) && 0 != 0) {// &&0!=0��Ч����
					remindBean.setUsers(remindBean.getCrmid());
					sendToCustomer(remindBean);// ���͵��ͻ�
					return;
				}
				rb = remindBean;
				this.judgeWay(rb);// �жϷ��ͷ�ʽ
			}
		} else
			return;// ���ݴ���
	}

	/**
	 * ���͵��ͻ�
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
					this.sendByEmail(v_email, v_msg);// �ʼ�����
				}
			}
		} else if ("2".equals(v_way)) {
			String v_mobile = "";
			v_sql = "select phone from CRM_CustomerInfo where id=" + v_crmid;
			if (recordSet.executeSql(v_sql)) {
				if (recordSet.next()) {
					v_mobile = recordSet.getString("phone");
					this.sendBySMS(v_mobile, v_msg);// ���ŷ���
				}
			}
		}
	}

	/**
	 * �ж����ѷ�ʽ,'0'ΪEmail���ѡ�'1'Ϊ�������ѡ�'2'Ϊ��������
	 * 
	 * @param remindBean����RemindBean
	 *            ,��RemindBean�����Ӧ�ļ���
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
	 * �����ɫidת��Ϊ��������Դid ���ɫid��ʽΪ:",xx,xx,xx",ע���һλΪ����
	 * ���صĶ�������Դid��ʽΪ:",xx,xx,xx",��һλҲ�Ƕ���
	 * 
	 * @param roleids���ɫid
	 * @return��������Դid
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
	 * �����ݿ��ѯ��Ҫ���ѵļ�¼
	 * 
	 * @return���Ѽ�¼��List����
	 */
	public List<RemindBean> loadRemindRecord() {
		List<RemindBean> list = new ArrayList<RemindBean>();
		RecordSet recordSet = new RecordSet();
		String sql = "select ctype,processname,monitornode,sqlcondition,togglecondition,days,content,way,remindgroup,users,remark from work_remind where dr=0";
		recordSet.executeSql(sql);
		while (recordSet.next()) {
			String v_type = Util.null2String(recordSet.getString("ctype"));// ������
			String v_processname = Util.null2String(recordSet
					.getString("processname"));// ����/������
			String v_monitornode = Util.null2String(recordSet
					.getString("monitornode"));// ��ؽڵ㣨�ֶΣ�
			String v_sqlcondition = Util.null2String(recordSet
					.getString("sqlcondition"));// sql����
			String v_togglecondition = Util.null2String(recordSet
					.getString("togglecondition"));// ��������
			String v_days = Util.null2String(recordSet.getString("days"));// ʱ�䣨�죩
			String v_content = Util.null2String(recordSet.getString("content"));// ��������
			String v_way = Util.null2String(recordSet.getString("way"));// ���ѷ�ʽ
			String v_remindgroup = Util.null2String(recordSet
					.getString("remindgroup"));// ������
			String v_users = Util.null2String(recordSet.getString("users"));// ������Ա
			String v_remark = Util.null2String(recordSet.getString("remark"));// ��ע
			list.add(new RemindBean(v_type, v_processname, v_monitornode,
					v_sqlcondition, v_togglecondition, v_days, v_content,
					v_way, v_remindgroup, v_users, v_remark));
		}
		return list;
	}

	/**
	 * ϵͳͨ����������
	 */
	public void sendByWF() {
		SysRemindWorkflow remindWorkflow = new SysRemindWorkflow();
		// ��Ϣͳһ�ԡ�ϵͳ����Ա����ݷ���
		for (RemindBean remindBean : WFList) {
			System.out.println(("����ʱ���ݣ�" + remindBean));
			String v_content = remindBean.getContent();
			int v_crmid = "".equals(remindBean.getCrmid()) ? 0 : Integer
					.parseInt(remindBean.getCrmid());
			int v_prjid = "".equals(remindBean.getPrjid()) ? 0 : Integer
					.parseInt(remindBean.getPrjid());
			String v_users = "".equals(remindBean.getUsers()) ? "0"
					: remindBean.getUsers().trim().substring(1);

			try {
				int rid = remindWorkflow.make("ϵͳ����", 0, v_crmid, v_prjid, 0,
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
	 * ϵͳͨ����������
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
					rs.executeSql(v_sql + v_userids[i]);// ��ѯ�ƶ��绰
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
	 * ���ŷ���
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
			System.out.println("��������Ϣ�����룺" + mobile + ",���ݣ�" + msg);
		} else {
			System.out.println("�ƶ��绰��������!!"
					+ new Timestamp(System.currentTimeMillis()));
		}
	}

	/**
	 * ϵͳͨ��E-mail����
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
					rs.executeSql(v_sql + v_userids[i]);// ��ѯ����
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
	 * E-mail����
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
			System.out.println("�ʼ��ѷ��ͣ�title:"+title+"E-mail:" + mail + ", msg:" + msg);
		} catch (Exception e) {
			System.out.println("�ʼ�����ʧ�ܣ�E-mail:" + mail + ", msg:" + msg);
			System.out.println("email = "+email);
			e.printStackTrace();
		}
	}
}
