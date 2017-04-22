package two.share;

import weaver.conn.RecordSet;
import weaver.general.Util;


public class ProjShareInfo {
	/**
	 * �˷����������� ��Ŀ�Ĳ���Ȩ�ޣ���ϸ�����������
	 */
	public static void setProjShareDetail(String prjid,String userids,String sharelevel){
		for (int i = 0; i < userids.split(",").length; i++) {
			String userid=Util.null2String(userids.split(",")[i]);
			if(!"".equals(userid)){
				setProjShareDetail(prjid, userid, "1", sharelevel);
			}
		}
	}
	/**
	 * �˷����������� ��Ŀ�Ĳ���Ȩ�޼���ʾ��Ϣ������ô˷���.
	 */
	public static void setProjShareInfo(String prjid,String userids,String usertype,String sharelevel){
		for (int i = 0; i < userids.split(",").length; i++) {
			String userid=Util.null2String(userids.split(",")[i]);
			if(!"".equals(userid)){
				setProjShareDetail(prjid, userid,usertype, sharelevel);//Ȩ����Ϣ
				if("1".equals(usertype)){
					setProjShare(prjid, userid, sharelevel);//��ʾ��Ϣ
				}
			}
		}

	}
	/**
	 * �˷����������� ��Ŀ�Ĳ���Ȩ�ޣ�
	 */
	public static void setProjShareDetail(String prjid,String userid,String usertype,String sharelevel){
		RecordSet rs=new RecordSet();
		String v_sql="";
		String v_cnt="";
		v_sql="select count(1) as cnt from prjsharedetail prj where prj.prjid='"+prjid+"' and userid='"+userid+"' and usertype='"+usertype+"'";
		rs.executeSql(v_sql);
		if(rs.next()){
			v_cnt=Util.null2String(rs.getString("cnt"));
		}
		if("0".equals(v_cnt)){//û�в�ѯ����¼
			v_sql="insert into prjsharedetail(prjid, userid, usertype, sharelevel) values('"+prjid+"', '"+userid+"', '"+usertype+"','"+sharelevel+"')";
			executeSql(v_sql);
		}else{//��ѯ����¼���ѯ���������Ƿ�Ϊ�༭Ȩ��
			v_sql="select 1 from prjsharedetail prj where prj.prjid='"+prjid+"' and userid='"+userid+"' and usertype='"+usertype+"' and sharelevel=2 ";
			rs.executeSql(v_sql);
			if(!rs.next()){//���û���ҵ��༭Ȩ�޵ļ�¼
				v_sql="update prjsharedetail set sharelevel ='"+sharelevel+"' where prjid='"+prjid+"' and userid='"+userid+"' and usertype='"+usertype+"'";
				executeSql(v_sql);
			}
		}
	}
	/**
	 * �˷����������� ��Ŀ���Ȩ����ʾ��
	 */
	private static void setProjShare(String prjid,String userid,String sharelevel){
		RecordSet rs=new RecordSet();
		String v_cnt="";
		String v_sql="";
		v_sql="select count(1) as cnt from prj_shareinfo prj  where  prj.sharetype=1 and prj.relateditemid='"+prjid+"' and prj.userid='"+userid+"'";
		rs.executeSql(v_sql);
		if(rs.next()){
			v_cnt=Util.null2String(rs.getString("cnt"));
		}
		if("0".equals(v_cnt)){//û�в�ѯ����¼
			v_sql="insert into prj_shareinfo(relateditemid, sharetype, seclevel, rolelevel, sharelevel, userid, departmentid, roleid, foralluser, crmid)values('"+prjid+"', 1, 10, 0, '"+sharelevel+"', '"+userid+"', 0, 0, 0, 0)";
			executeSql(v_sql);
		}else{//��ѯ����¼���ѯ���������Ƿ�Ϊ�༭Ȩ��
			v_sql="select 1 from prj_shareinfo prj where  prj.sharetype=1 and prj.relateditemid='"+prjid+"' and prj.userid='"+userid+"' and sharelevel=2 ";
			rs.executeSql(v_sql);
			if(!rs.next()){//���û���ҵ��༭Ȩ�޵ļ�¼
				v_sql="update prj_shareinfo set sharelevel ='"+sharelevel+"'   where  sharetype=1 and relateditemid='"+prjid+"' and userid='"+userid+"'";
				executeSql(v_sql);
			}
		}
	}
	private static void executeSql(String v_sql){
		RecordSet rs=new RecordSet();
		rs.executeSql(v_sql);
		rs.next();
	}
}
