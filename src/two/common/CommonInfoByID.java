package two.common;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import two.util.DateUtil;
import weaver.conn.RecordSet;
import weaver.crm.Maint.CustomerInfoComInfo;
import weaver.docs.docs.DocComInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;
import weaver.proj.Maint.ProjectInfoComInfo;

public class CommonInfoByID extends BaseBean {
	DepartmentComInfo deptInfo = null;// ������Ϣ
	ProjectInfoComInfo projInfo = null;// ��Ŀ��Ϣ
	CustomerInfoComInfo crmInfo = null;// �ͻ���Ϣ
	ResourceComInfo hrmInfo = null;// ������Դ
	DocComInfo docInfo = null;// �ĵ�
	SubCompanyComInfo orgInfo=null;//����

	RecordSet rs = null;
	String v_sql = "";

	private void initObjectInfo(String str) {
		try {
			if ("dept".equals(str) && deptInfo == null) {
				deptInfo = new DepartmentComInfo();// ������Ϣ
			} else if ("proj".equals(str) && projInfo == null) {
				projInfo = new ProjectInfoComInfo();// ������Ϣ
			} else if ("crm".equals(str) && crmInfo == null) {
				crmInfo = new CustomerInfoComInfo();// �ͻ���Ϣ
			} else if ("hrm".equals(str) && hrmInfo == null) {
				hrmInfo = new ResourceComInfo();// ������Դ
			} else if ("doc".equals(str) && docInfo == null) {
				docInfo = new DocComInfo();// �ĵ�
			} else if ("org".equals(str) && orgInfo == null) {
				orgInfo = new SubCompanyComInfo();// ����
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������Ŀ�õ��ͻ�id
	 */
	public String LookCrmIDByProjID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("proj");// ��ʼ����Ŀ��Ϣ
			return projInfo.getProjectInfodesc(id);
		}
		return "";
	}

	/**
	 * ������Ŀ��Ų�ѯ��Ŀ���Q�� <������ϸ����>
	 */
	public String LookNameByProjID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("proj");// ��ʼ����Ŀ��Ϣ
			return projInfo.getProjectInfoname(id);
		}
		return "";
	}

	/**
	 * ���ݲ��ű�Ų�ѯ�������Q�� <������ϸ����>
	 */
	public String LookNameByDeptID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("dept");// ��ʼ��������Ϣ
			return deptInfo.getDepartmentname(id);
		}
		return "";
	}
	
	/**
	 * ���ݻ�����Ų�ѯ�������Q�� <������ϸ����>
	 */
	public String LookNameByOrgID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("org");// ��ʼ��������Ϣ
			return orgInfo.getSubCompanyname(id);
		}
		return "";
	}
	
	

	/**
	 * ���ݿͻ��ı�Ų�ѯ�ͻ�������Ͷ�Y���w <������ϸ����>
	 */
	public String LookNameByCRMID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("crm");// ��ʼ���ͻ���Ϣ
			return crmInfo.getCustomerInfoname(id);
		}
		return "";
	}

	/**
	 * ���ݿͻ��ı�Ų�ѯ�ͻ�������Ͷ�Y���w ��URL<������ϸ����>
	 */
	public String LookNameByCRMID(String id, String url) {
		String code = "";
		if (!"".equals(id)) {
			this.initObjectInfo("crm");// ��ʼ���ͻ���Ϣ
			code = "<a href=\"/CRM/data/ViewCustomer.jsp?CustomerID=" + id
					+ "\">" + crmInfo.getCustomerInfoname(id) + "</a>";
		}
		return code;
	}

	/**
	 * ���݆T���ı�Ų�ѯ�T��
	 */
	public String LookNameByHrmID(String id) {
		if (!"".equals(id)) {
			this.initObjectInfo("hrm");// ��ʼ����Ա��Ϣ
			String value = hrmInfo.getLastname(id);
			if ("1".equals(id) && "".equals(value)) {
				value = "ϵͳ����Ա";
			}
			return value;
		}
		return "";
	}

	/**
	 * ���ݹ�˾��Ա��Ų�ѯ�����Ա��ֵ
	 * 
	 */
	public String LookNameByMoreHrmID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			id = "," + id + ",";
			id = id.replace(",,", ",");
			v_sql = "SELECT distinct lastname FROM hrmresource WHERE instr('"+ id + "',','||id||',')>0";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				restr += "," + rs.getString("lastname");
			}
			restr = restr.startsWith(",") ? restr.substring(1) : restr;
		}
		return restr;
	}

	/**
	 * �����ĵ���ŵõ��ĵ�����
	 */
	public String LookNameByDocID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select imagefilename from imagefile where imagefileid='"
					+ id + "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("imagefilename"));
			}
			if ("".equals(restr)) {
				this.initObjectInfo("doc");// ��ʼ���ĵ���Ϣ
				restr = docInfo.getDocname(id);
			}
		}
		return restr;
	}

	/**
	 * ������ҵID�õ���ҵ����
	 */
	public String LookNameBySectorID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,fullname as cname from  crm_sectorinfo where id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ����ҵ��ID�õ�ҵ������
	 */
	public String LookNameByBusID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id, buscode as cname from  nd_business where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ��������ID�õ���������
	 */
	public String LookNameByCoopID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_coopbank where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ����ҵ��Ʒ��ID�õ�ҵ��Ʒ������
	 */
	public String LookNameByYWPZID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_businessproduct where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ���ݺ�ͬ����ID�õ���ͬ��������
	 */
	public String LookNameByContTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_contractTypes where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ������������ID�õ�������������
	 */
	public String LookNameByDatumTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id,cname from  db_datumtype where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ���ݷ�������ʩ��������ID�õ���������ʩ������������
	 */
	public String LookNameByReverseTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			v_sql = "select id, typeName  as cname from  db_reverseType where dr=0 and id='"
					+ id + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ������ĿID�õ�ҵ��
	 */
	public HashMap LookBusMapByProjID(String id) {
		HashMap<String, String> busMap = new HashMap<String, String>();
		if (!"".equals(id)) {
			String v_key = "", v_value = "";// ��ֵ����ʾֵ
			v_sql = "select id,cname from  nd_business where prjid='" + id
					+ "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				v_key = Util.null2String(rs.getString("id"));
				v_value = Util.null2String(rs.getString("cname"));
				busMap.put(v_key, v_value);
			}
		}
		return busMap;
	}

	/**
	 * ������ί��Ա��Ų�ѯ����ί������
	 */
	public String LookNameByJudgesID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			id = "," + id + ",";
			id = id.replace(",,", ",");
			v_sql = "SELECT distinct cname FROM nd_ExpertTeam WHERE instr('"
					+ id + "',','||id||',')>0";
			// //System.out.println(sql);
			rs = new RecordSet();
			rs.executeSql(v_sql);
			while (rs.next()) {
				restr += "," + rs.getString("cname");
			}
			restr = restr.startsWith(",") ? restr.substring(1) : restr;
		}
		return restr;
	}

	/**
	 * ����selectֵ�õ�����
	 */
	public String LookNameBySelectID(String selectvalue, String fieldid) {
		String restr = "";
		if ("-1".equals(selectvalue)) {
			restr = "";
		} else if (!"".equals(fieldid)) {
			v_sql = "select selectname as cname from  fla_SelectItem where fieldid='"
					+ fieldid + "' and selectvalue='" + selectvalue + "'";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ͨ��id�õ���������
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByArchivesTypeID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select archivestype from db_archivestype where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("archivestype"));
			}
		}
		return restr;
	}

	/**
	 * ͨ��id�õ��������
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByStockCabinetID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select stockcabinet from db_stockcabinet where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("stockcabinet"));
			}
		}
		return restr;
	}

	/**
	 * ͨ��id�õ��������
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByStockLayerID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select stocklayer from db_stocklayer where id='" + id
					+ "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("stocklayer"));
			}
		}
		return restr;
	}

	/**
	 * ͨ��id�õ���������
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByArchivesID(String id) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select cname from nd_archives where id='" + id + "'";
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ͨ��id�õ������ίԱ����
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByMeetJudgesID(String Judges, String userids) {
		String restr = "";
		if (!"-1".equals(Judges)) {
			restr = "�ⲿ��ί:" + this.LookNameByJudgesID(Judges);
		} else if (!"-1".equals(userids)) {
			restr = "�ڲ���ί:" + this.LookNameByMoreHrmID(userids);
		}
		return restr;
	}

	/**
	 * ͨ��id�õ��������
	 * 
	 * @param id
	 * @return
	 */
	public String LookNameByMeetCodeID(String id, String type) {
		String restr = "";
		if (!"".equals(id)) {
			rs = new RecordSet();
			v_sql = "select meetcode as cname from nd_appraisalmeet where id='"
					+ id + "'";
			if ("2".equals(type)) {
				v_sql = "select meetcode as cname from nd_businessmeet where id='"
						+ id + "'";
			}
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("cname"));
			}
		}
		return restr;
	}

	/**
	 * ����requestidֵ�õ����������
	 */
	public String LookTableByRequestID(String requestid) {
		String restr = "";
		if (!"".equals(requestid)) {
			v_sql = "select (select tablename from workflow_bill where id=formid) as tablename from workflow_base bas where exists (select 1 from workflow_Requestbase req where req.workflowid=bas.id and req.requestid='"
					+ requestid + "')";
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				restr = Util.null2String(rs.getString("tablename"));
			}
		}
		return restr;
	}

	/**
	 * �õ�����
	 */
	public String getDateInfo(String date, String fmt) {
		if (!"".equals(date)) {
			SimpleDateFormat myFmt = null;
			if ("YMDHMS".equalsIgnoreCase(fmt)) {
				myFmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			} else {
				myFmt = new SimpleDateFormat("yyyy-MM-dd");
			}
			try {
				Date da = myFmt.parse(date);
				date = myFmt.format(da);
			} catch (Exception e) {
				// System.out.println(e.getMessage());
			}
			return date;
		}
		return "";
	}

	/**
	 * ����ǧ��λ
	 * */
	public String NumberFormat(String str, String align) {
		if (!"".equals(str)) {
			str = str.replace(",", "");
			NumberFormat format = NumberFormat.getInstance();
			str = format.format(Double.parseDouble(str));
			if ("".equals(align)) {
				return str;
			} else if (",L,LEFT,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"left\">" + str + "</div>";
			} else if (",R,RIGHT,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"right\">" + str + "</div>";
			} else if (",C,CENTER,".indexOf(("," + align + ",").toUpperCase()) > -1) {
				str = "<div align=\"center\">" + str + "</div>";
			}
			return str;
		}
		return "";
	}

	/**
	 * ִ��SQL��䲢����һ��������ظ�ʽ�磺�ֶ�$ֵ,�� <������ϸ����>
	 */
	public Map executeSql(String v_sql) {
		Map map = new HashMap();
		String fields = "";
		String values = "";
		if (!"".equals(v_sql)) {
			rs = new RecordSet();
			rs.executeSql(v_sql);
			if (rs.next()) {
				List list = Arrays.asList(rs.getColumnName());
				for (int i = 0; i < list.size(); i++) {
					fields = Util.null2String((String) list.get(i));
					values = Util.null2String(rs.getString(fields));
					map.put(fields.toLowerCase(), values);
				}
			}
		}
		return map;
	}

	/**
	 * ִ��SQL���û�з��أ� <������ϸ����>
	 */
	public boolean executeSql(String str, String oper) {
		boolean flag = false;
		if (!"".equals(str)) {
			rs = new RecordSet();
			if (rs.executeSql(str)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * ����SQL���ر�ͷ
	 * 
	 * @param sql
	 * @return
	 */
	public List getFields(String sql) {
		if ("".equals(sql)) {
			return new LinkedList();
		}
		rs = new RecordSet();
		rs.executeSql(sql.toUpperCase());
		List list = Arrays.asList(rs.getColumnName());
		return list;
	}

	/**
	 * ����SQL����ִ�н��
	 * 
	 * @param sql
	 * @return
	 */
	public List getObjValue(String sql) {
		Map map = null;
		String fields = "";
		String values = "";
		rs = new RecordSet();
		List reList = new ArrayList();
		rs.executeSql(sql.toUpperCase());
		List list = Arrays.asList(rs.getColumnName());
		while (rs.next()) {
			Iterator ir = list.iterator();
			map = new HashMap();
			fields = "";
			values = "";
			while (ir.hasNext()) {
				fields = (String) ir.next();
				values = rs.getString(fields);
				values = (values == null) ? "" : values;
				map.put(fields, values);
			}
			reList.add(map);
		}
		return reList;
	}

	public String getFlag(String flag) {
		if ("".equals(flag) || "1".equals(flag)) {
			return "true";
		} else {
			return "false";
		}
	}

	public Object invoke(String className, String methodName, Object... objects) {
		Object object = new Object();
		try {
			Class clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					Class[] clazz1 = m.getParameterTypes();
					object = m.invoke(obj, objects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * @param tablename
	 *            ����
	 * @param field
	 *            ������ֶ�
	 */
	public void getLevelId(String tablename, String field) {
		rs = new RecordSet();
		RecordSet uprs = new RecordSet();
		String id = "", pid = "";
		String v_sql = "select * from " + tablename + " where dr=0 ";
		String v_cids = "";
		rs.execute(v_sql);
		while (rs.next()) {
			id = rs.getString("id");
			pid = rs.getString("pid");
			v_cids = this.getPid(tablename, pid);
			v_cids = v_cids + id;
			v_sql = "update " + tablename + " set " + field + "=',"
					+ v_cids.toString() + ",' where id='" + id + "'";
			uprs.execute(v_sql);
		}
	}

	private String getPid(String tablename, String pid) {
		String v_cids = "";
		RecordSet rs1 = new RecordSet();
		String v_sql = "select * from " + tablename + " where dr=0 and id="
				+ pid;
		String id = "";
		rs1.execute(v_sql);
		if (rs1.next()) {
			id = rs1.getString("id");
			pid = rs1.getString("pid");
			v_cids = id + "," + v_cids;
			v_cids = getPid(tablename, pid) + v_cids;
		}
		return v_cids;
	}

	/**
	 * ͨ����λid�õ���λ����
	 * 
	 * @param uid
	 * @return
	 */
	public String LookNameByUtilID(String uid) {
		String result = "";
		String v_sql = " select unitname from LgcAssetUnit   where id = '"
				+ uid + "' ";
		RecordSet rs = new RecordSet();
		rs.executeSql(v_sql);
		if (rs.next()) {
			result = Util.null2String(rs.getString("unitname"));
		}
		return result;
	}

	public String LookImgByType(String str) {
		String result = "";
		if ("1".equals(str)) {
			result = super.getPropValue("work_dev", "effectYes");
		} else if ("0".equals(str) || "".equals(str)) {
			result = super.getPropValue("work_dev", "effectNo");
		}
		return result;
	}
	
	public String LookOperBySearch(String str) {
		String result = "";
		result+="<a class=\"oper\" href=\"javascript:theDelRow('"+str+"');\">ɾ��</a> ";
		result+="<a class=\"oper\" href=\"javascript:theEditRow('"+str+"');\">�༭</a> ";
		result+="<a class=\"viewoper\" href=\"javascript:theViewRow('"+str+"');\">�鿴</a>";
		return result;
	}
	public ArrayList LookOperBySearch(String strID,String OtherInfo) {
		 ArrayList returnList = new ArrayList();
		 String[] otherInfos = Util.TokenizerString2(OtherInfo, "_");
		 String userid=Util.null2String(otherInfos[0]);//�û�ID
		 String billid=Util.null2String(otherInfos[1]);//��ID ��:1:search
		 String canEdit=Util.null2String(otherInfos[2]);//�Ƿ��б༭Ȩ��
		 returnList.add(canEdit);
		 returnList.add(canEdit);
		 returnList.add("true");
		return returnList;
	}
	/**
	 * �õ���ǰ����
	 * */
	public String getNowDate() {
		return DateUtil.getNowDate("1");
	}

	/**
	 * �õ���ǰ����
	 * */
	public String getNowDate(String str) {
		// 1 �����գ�2 ���£�3�꣬4��,5 ������ʱ����
		return DateUtil.getNowDate(str);
	}

	/**
	 * ���ַ�������'yyyy-MM-dd'תΪ��д���ڣ��磺'2011-11-21'ת��Ϊ'��Oһһ��ʮһ�¶�ʮһ��'
	 * 
	 * @param date
	 * @return
	 */
	public String stringToUppderDate(String date) {
		return DateUtil.stringToUppderDate(date);
	}

	/**
	 * �ַ�����תΪ"xxxx��xx��xx��"
	 * 
	 * @param date
	 * @return
	 */
	public static String stringToStringDate(String date) {
		return DateUtil.stringToStringDate(date);
	}
	/**
	 * ɾ��ǰ�󶺺�
	 * @param date
	 * @return
	 */	
	public String delComma(String str){
		String v_tmpStr=Util.null2String(str);
		v_tmpStr=v_tmpStr.replace(",,", ",");//ȥ����������
		v_tmpStr=v_tmpStr.startsWith(",")?v_tmpStr.substring(1):v_tmpStr;//���ǰ����
		v_tmpStr=v_tmpStr.endsWith(",")?v_tmpStr.substring(0,v_tmpStr.length()-1):v_tmpStr;//����󶺺�
		return v_tmpStr;
	}
	public String getBusType(String busid) {
		if ("".equals(busid)) {
			return "";
		}
		String type = "";
		String temp = "";
		String sql = "select ywpz from nd_business where id='" + busid + "'";
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		if (rs.next()) {
			temp = Util.null2String(rs.getString("ywpz"));
		}
		if ("5".equals(temp)) {
			type = "ί��";
		} else {
			type = "����";
		}
		return type;
	}

	/**
	 * ͨ����ǰ�û���ѯר�Һ�
	 * @param userid
	 * @return 
	 */
	public String LookJudidByUserid(String userid) {
		String judid = "";
		String sql = "select id from nd_expertteam where loginid = '"+userid+"'";
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		if (rs.next()) {
			judid = Util.null2String(rs.getString("id"));
		}
		return judid;
	}

}
