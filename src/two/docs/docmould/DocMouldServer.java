package two.docs.docmould;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DBstep.iMsgServer2000;

import weaver.conn.RecordSet;
import weaver.docs.bookmark.MouldBookMarkComInfo;
import weaver.docs.docs.DocInfo;
import weaver.general.Util;

public class DocMouldServer {
	/**
	 * ����WebOffice ����ĵ��ã�������ʾģ���϶����SQL����ȡ��ǩ��Ϣ�ŵ�DocInfo��
	 * @param msgObj
	 *            WebOffice ����ķ�������JavaBean
	 * @return ���ִ�гɹ�����true ũ��ר�� ������-�Ȩ
	 */
	public boolean loadBookMarks(DocInfo docInfo,iMsgServer2000 msgObj) {
		String v_params = Util.null2String(msgObj.GetMsgByName("DOCMOULDPRAMS"));
		String mouldid=docInfo.getTemplate();//�ĵ�ģ����		
		String prjid = v_params.split("#")[0];
		String crmid = v_params.split("#")[1];
		String requestid = v_params.split("#")[2];
		boolean flag = false;
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		List list = new ArrayList();
		Map bookMarkValueMap = new HashMap();
		String fields = "";
		String values = "";
		String v_sql = " select * from work_DocMouldlableToSql where mouldid='"+ mouldid+"'";
		System.out.println("v_sql:"+v_sql);
		rs.executeSql(v_sql);
		if (rs.next()) {
			String v_sqlinfo = Util.null2String(rs.getString("sqlinfo")).toLowerCase();
			v_sqlinfo = v_sqlinfo.replace("${projid}", prjid).replace(
					"${crmid}", crmid).replace("${requestid}", requestid);
			rs1.executeSql(v_sqlinfo);
			list = Arrays.asList(rs1.getColumnName());
			Iterator it = list.iterator();
			if (rs1.next()) {
				while (it.hasNext()) {
					fields = (String) it.next();
					values = Util.null2String(rs1.getString(fields));
					values = ("".equals(values)) ? "��" : values;
					bookMarkValueMap.put(fields, values);
				}
			} else {
				while (it.hasNext()) {
					fields = (String) it.next();
					values = "��";
					bookMarkValueMap.put(fields, values);
				}
			}
			this.setBookMarks(msgObj, bookMarkValueMap,mouldid);
		}
		return flag;
	}
	/**
	 * ������ǩ��Ϣ
	 * @param msgObj
	 * @param bookMarkValueMap
	 * @param mouldid
	 */
	
	private void setBookMarks(iMsgServer2000 msgObj,Map bookMarkValueMap,String mouldid){
		MouldBookMarkComInfo mbmci=null;
		try {
			mbmci = new MouldBookMarkComInfo();
			RecordSet rs = new RecordSet();
			String v_bookmarkid="";//��ǩID
			String v_bookmarkname="";//��ǩ����
			String v_sqlfield="";//��Ӧ���ֶ�
			String v_bookmarkvalue="";//��ǩֵ 
			String v_sql = " select * from work_DocMouldlableToSql where mouldid='"+ mouldid+"'";
			rs.executeSql(v_sql);
			while(rs.next()){
				v_bookmarkid = Util.null2String(rs.getString("bookmarkid"));
				v_sqlfield = Util.null2String(rs.getString("sqlfield"));
				if("1,2,3,4,5,6".indexOf(v_sqlfield)>-1){
					v_bookmarkvalue=this.getNowDate(v_sqlfield);
				}else{
					v_bookmarkvalue=(String)bookMarkValueMap.get(v_sqlfield);
				}
				v_bookmarkname = mbmci.getMouldBookMarkName(v_bookmarkid);
				msgObj.SetMsgByName(v_bookmarkname,v_bookmarkvalue);
				System.out.println("v_bookmarkname:"+v_bookmarkname+"v_bookmarkvalue:"+v_bookmarkvalue);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getNowDate(String str) {
		String redate = "";
		// 1 �����գ�2 ���£�3�꣬4��,5 ������ʱ����
		Date date = new Date();
		DateFormat df=null;
		if ("1".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM-dd");
			redate = df.format(date);
		}else if ("2".equals(str)) {
			df = new SimpleDateFormat("yyyy");
			redate = df.format(date);
		} else if ("3".equals(str)) {
			df = new SimpleDateFormat("MM");
			redate = df.format(date);
		}  else if ("4".equals(str)) {
			df = new SimpleDateFormat("dd");
			redate = df.format(date);
		} else if ("5".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM-dd 24hh:mm:ss");
			redate = df.format(date);
		} else if ("6".equals(str)) {
			df = new SimpleDateFormat("yyyy-MM");
			redate = df.format(date);
		} 
		return redate;
	}
}
