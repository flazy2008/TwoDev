package two.work.hrm;

import java.util.HashMap;
import java.util.Map;

import two.share.PrjAndCrmShareInfo;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.MD5;
import weaver.general.Util;

/**
 * 随机生成用户名及密码
 * 
 * @author hongyun.xu
 * 
 */
public class RandomLogin extends BaseBean {
	private String loginId;// 登录账号
	private String password;// 密码
	private final String EXPERTNUM = super
			.getPropValue("work_dev", "EXPERTNUM");// 预留专家号

	/**
	 * 更新某些专家的账号
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
	 * 检查目前有几个专家账号可用
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
	 * 检查专家账号和密码
	 * 
	 * @param members专家id数组
	 * @return专家账号、密码的集合，键：专家Id，值：_账号@_密码
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
						map.put(members[i], "_已无可分配的账号@_");
					} else {
						map.put(members[i], "_@_");
					}
				}
			}
		}
		return map;
	}

	/**
	 * 清出所有专家账号
	 */
	public void cleanAllExpert() {
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		String v_sql = "update hrmresource set loginid = null, password = null where id in ("
				+ EXPERTNUM + ")";
		rs.executeSql(v_sql);
		v_sql = "update nd_ExpertTeam set loginid=null,password=null";
		rs1.executeSql(v_sql);
		new PrjAndCrmShareInfo().cleanAllShare();//清除所有专家的权限
	}

	/**
	 * 清出指定专家账号
	 * 
	 * @param members专家Id数组
	 */
	public void cleanExpert(String[] members) {
		String v_userId = "";
		String v_sql = "";
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		for (int i = 0; i < members.length; i++) {
			v_sql = " select loginid from nd_ExpertTeam where id='"
					+ members[i] + "'";// 查userId
			rs.executeSql(v_sql);
			if (rs.next()) {
				v_userId = Util.null2String(rs.getString("loginid"));
			}
			new PrjAndCrmShareInfo().cleanShare(v_userId);// 清除权限
			v_sql = " update nd_ExpertTeam set loginid= null,password=null where id='"
					+ members[i] + "'";// 清除专家表
			rs1.executeSql(v_sql);
			v_sql = " update hrmresource set loginid = null,password =null where id = '"
					+ v_userId + "'";// 清除人力资源表
			rs2.executeSql(v_sql);
		}
	}

	/**
	 * 通过登录账号查询Hrmid
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
	 * 生成随机数
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
