package two.work.hrm;

import java.util.HashMap;
import java.util.Map;

import two.share.PrjAndCrmShareInfo;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.MD5;
import weaver.general.Util;

/**
 * ��������û���������
 * 
 * @author hongyun.xu
 * 
 */
public class RandomLogin extends BaseBean {
	private String loginId;// ��¼�˺�
	private String password;// ����
	private final String EXPERTNUM = super
			.getPropValue("work_dev", "EXPERTNUM");// Ԥ��ר�Һ�

	/**
	 * ����ĳЩר�ҵ��˺�
	 * 
	 * @param members
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map updateAccount(String[] members) {
		cleanExpert(members);
		String id = checkExpertNum();
		if ("".equals(id)) {
			return null;
		}
		String[] ids = id.split(",");
		String loginid = "";
		String password = "";
		String v_sql = "";
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		for (int i = 0; i < ids.length && i < members.length; i++) {
			loginid = "pw" + createRandom(3);
			password = createRandom(5);
			v_sql = " update hrmresource set loginid = '" + loginid
					+ "',password = '" + new MD5().getMD5ofStr(password)
					+ "' where id = '" + ids[i] + "'";
			rs.executeSql(v_sql);
			v_sql = " update nd_ExpertTeam set loginid='" + ids[i]
					+ "',password='" + password + "' where id='" + members[i]
					+ "'";
			rs1.executeSql(v_sql);
		}
		return checkExpertNum(members, 1);
	}

	/**
	 * ���Ŀǰ�м���ר���˺ſ���
	 * 
	 * @return
	 */
	public String checkExpertNum() {
		String result = "";
		String v_sql = " select id from hrmresource where id in(" + EXPERTNUM
				+ ") and loginid is null and password is null ";
		RecordSet rs = new RecordSet();
		rs.executeSql(v_sql);
		while (rs.next()) {
			result += Util.null2String(rs.getString("id")) + ",";
		}
		return result;
	}

	/**
	 * ���ר���˺ź�����
	 * 
	 * @param membersר��id����
	 * @returnר���˺š�����ļ��ϣ�����ר��Id��ֵ��_�˺�@_����
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map checkExpertNum(String[] members, Object... objects) {
		Map map = new HashMap();
		String v_sql = "";
		String v_loginid = "";
		String v_password = "";
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		for (int i = 0; i < members.length; i++) {
			v_sql = " select loginid,password from nd_ExpertTeam where id='"
					+ members[i] + "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				v_loginid = Util.null2String(rs.getString("loginid"));
				v_password = Util.null2String(rs.getString("password"));
				v_sql = " select loginid from hrmresource where id = '"
						+ v_loginid + "'";
				rs1.executeSql(v_sql);
				if (rs1.next()) {
					map.put(members[i],
							"_" + Util.null2String(rs1.getString("loginid"))
									+ "@_" + v_password);
				} else {
					if (objects != null) {
						map.put(members[i], "_���޿ɷ�����˺�@_");
					} else {
						map.put(members[i], "_@_");
					}
				}
			}
		}
		return map;
	}

	/**
	 * �������ר���˺�
	 */
	public void cleanAllExpert() {
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		String v_sql = "update hrmresource set loginid = null, password = null where id in ("
				+ EXPERTNUM + ")";
		rs.executeSql(v_sql);
		v_sql = "update nd_ExpertTeam set loginid=null,password=null";
		rs1.executeSql(v_sql);
		new PrjAndCrmShareInfo().cleanAllShare();//�������ר�ҵ�Ȩ��
	}

	/**
	 * ���ָ��ר���˺�
	 * 
	 * @param membersר��Id����
	 */
	public void cleanExpert(String[] members) {
		String v_userId = "";
		String v_sql = "";
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		for (int i = 0; i < members.length; i++) {
			v_sql = " select loginid from nd_ExpertTeam where id='"
					+ members[i] + "'";// ��userId
			rs.executeSql(v_sql);
			if (rs.next()) {
				v_userId = Util.null2String(rs.getString("loginid"));
			}
			new PrjAndCrmShareInfo().cleanShare(v_userId);// ���Ȩ��
			v_sql = " update nd_ExpertTeam set loginid= null,password=null where id='"
					+ members[i] + "'";// ���ר�ұ�
			rs1.executeSql(v_sql);
			v_sql = " update hrmresource set loginid = null,password =null where id = '"
					+ v_userId + "'";// ���������Դ��
			rs2.executeSql(v_sql);
		}
	}

	/**
	 * ͨ����¼�˺Ų�ѯHrmid
	 * 
	 * @param loginid
	 * @return
	 */
	public String getHrmidByLoginid(String loginid) {
		if ("".equals(loginid)) {
			return "";
		}
		String hrmid = "";
		RecordSet rs = new RecordSet();
		rs.executeSql("select id from hrmresource where loginid = '" + loginid
				+ "'");
		if (rs.next()) {
			hrmid = Util.null2String(rs.getString("id"));
		}
		return hrmid;
	}

	public RandomLogin(String loginId, String password) {
		super();
		this.loginId = loginId;
		this.password = password;
	}

	public String getEXPERTNUM() {
		return EXPERTNUM;
	}

	public RandomLogin() {
		super();
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * ���������
	 * 
	 * @param num
	 * @return
	 */
	public String createRandom(int num) {
		String result = "";
		char c = 0;
		int n = 0;
		for (int i = 0; i < num; i++) {
			if (((int) ((Math.random() * 10))) % 2 == 0) {
				c = (char) (97 + (int) ((Math.random() * 26)));
				result += c;
			} else {
				n = (int) ((Math.random() * 10));
				result += n;
			}
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(new RandomLogin().createRandom(3));
	}
}
