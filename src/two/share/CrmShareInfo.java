package two.share;

import weaver.conn.RecordSet;
import weaver.general.Util;


public class CrmShareInfo {
	/**
	 * 此方法用于设置 项目表的权限显示用 -客户主要是调用这个方法，主表表起控制作用
	 */
	private static void setCrmShare(String crmid,String userid,String sharelevel){
		RecordSet rs=new RecordSet();
		String v_cnt="";
		String v_sql="";
		v_sql="select count(1) as cnt from crm_shareinfo where sharetype=1 and relateditemid='"+crmid+"' and userid='"+userid+"' and contents='"+userid+"'";
		rs.executeSql(v_sql);
		if(rs.next()){
			v_cnt=Util.null2String(rs.getString("cnt"));
		}
		if("0".equals(v_cnt)){//没有查询到记录
			v_sql="insert into crm_shareinfo(relateditemid, sharetype, seclevel, rolelevel, sharelevel, userid, departmentid, roleid, foralluser, crmid, contents)values('"+crmid+"', 1, 10, 0, '"+sharelevel+"', '"+userid+"', 0, 0, 0, 0,'"+userid+"')";
			executeSql(v_sql);
		}else{//查询到记录后查询共享类型是否为编辑权限
			v_sql="select 1 from crm_shareinfo where  sharetype=1 and relateditemid='"+crmid+"' and userid='"+userid+"'  and contents='"+userid+"' and sharelevel=2 ";
			rs.executeSql(v_sql);
			if(!rs.next()){//如果没有找到编辑权限的记录
				v_sql="update crm_shareinfo set sharelevel ='"+sharelevel+"'   where  sharetype=1 and relateditemid='"+crmid+"' and userid='"+userid+"' and contents='"+userid+"'";
				executeSql(v_sql);
			}
		}
	}
	/**
	 * 此方法用于设置 项目的操作权限及显示信息，调用此方法
	 */
	public static void setCrmShareInfo(String crmid,String userids,String usertype,String sharelevel){
		for (int i = 0; i < userids.split(",").length; i++) {
			String userid=Util.null2String(userids.split(",")[i]);
			if(!"".equals(userid)){
				//setCrmShareDetail(crmid, userid,usertype, sharelevel);//权限信息
				setCrmShare(crmid, userid, sharelevel);//权限信息
			}
		}
	}
	/**
	 * 此方法用于设置 项目的操作权限，好像不起作用
	 */
	@Deprecated
	public static void setCrmShareDetail(String crmid,String userid,String usertype,String sharelevel){
		RecordSet rs=new RecordSet();
		String v_sql="";
		String v_cnt="";
		v_sql="select count(1) as cnt from crmsharedetail where crmid='"+crmid+"' and userid='"+userid+"' and usertype='"+usertype+"'";
		rs.executeSql(v_sql);
		if(rs.next()){
			v_cnt=Util.null2String(rs.getString("cnt"));
		}
		if("0".equals(v_cnt)){//没有查询到记录
			v_sql="insert into crmsharedetail(crmid, userid, usertype, sharelevel) values('"+crmid+"', '"+userid+"', '"+usertype+"','"+sharelevel+"')";
			executeSql(v_sql);
		}else{//查询到记录后查询共享类型是否为编辑权限
			v_sql="select 1 from crmsharedetail where crmid='"+crmid+"' and userid='"+userid+"' and usertype='"+usertype+"' and sharelevel=2 ";
			rs.executeSql(v_sql);
			if(!rs.next()){//如果没有找到编辑权限的记录
				v_sql="update crmsharedetail set sharelevel ='"+sharelevel+"' where crmid='"+crmid+"' and userid='"+userid+"' and usertype='"+usertype+"'";
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
