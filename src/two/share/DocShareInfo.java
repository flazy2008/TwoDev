package two.share;

import weaver.conn.RecordSet;
import weaver.general.Util;


public class DocShareInfo {
	/**
	 * 此方法用于设置 项目表的权限显示用 -客户主要是调用这个方法，主表表起控制作用
	 */
	private static void setDocShare(String docid,String userid,String sharelevel){
		RecordSet rs=new RecordSet();
		String v_cnt="";
		String v_sql="";
		v_sql="select count(1) as cnt from docshare where sharetype=1 and docid='"+docid+"' and userid='"+userid+"' ";
		rs.executeSql(v_sql);
		if(rs.next()){
			v_cnt=Util.null2String(rs.getString("cnt"));
		}
		if("0".equals(v_cnt)){//没有查询到记录
			v_sql="insert into docshare(docid, sharetype, seclevel, rolelevel, sharelevel, userid, roleid, foralluser,orggroupid, downloadlevel)values('"+docid+"', '1', '0', '0','"+sharelevel+"','"+userid+"', '0', '0', '0', '1')";
			executeSql(v_sql);
		}else{//查询到记录后查询共享类型是否为编辑权限
			v_sql="update docshare set seclevel=0, rolelevel=0, sharelevel='"+sharelevel+"', roleid=0, foralluser=0,orggroupid=0, downloadlevel=1 where  sharetype=1 and docid='"+docid+"' and userid='"+userid+"'";
			executeSql(v_sql);
		}
	}
	/**
	 * 此方法用于设置 项目的操作权限及显示信息，调用此方法
	 */
	public static void setDocShareInfo(String docid,String userids,String usertype,String sharelevel){
		for (int i = 0; i < userids.split(",").length; i++) {
			String userid=Util.null2String(userids.split(",")[i]);
			if(!"".equals(userid)){
				setDocShare(docid, userid, sharelevel);//权限信息
			}
		}
	}
	private static void executeSql(String v_sql){
		RecordSet rs=new RecordSet();
		rs.executeSql(v_sql);
		rs.next();
	}
}
