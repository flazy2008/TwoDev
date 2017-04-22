package two.share;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import two.common.CommonInfoByID;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;

public class BillFieldVarify extends BaseBean {
	WorkFieldShareInfo shareInfo = new WorkFieldShareInfo();
	CommonInfoByID common=new CommonInfoByID();
	Set<String> notEditSet=null;
	Set<String> notViewSet=null;
	List<WorkFieldShareInfo> list = null;
	String p_FieldShare=super.getPropValue("work_dev","FieldShare");//是否开启权限控制
	
	public BillFieldVarify() {
		notEditSet=new HashSet<String>();
		notViewSet=new HashSet<String>();
	}
	public String getFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		String v_restr="[]";//返回字符串
		String v_isfromreq="0";
		p_sharelevel="".equals(p_sharelevel)?"1":p_sharelevel;//权限类型 1 不可编辑 2不可见
		
		if("0".equals(p_FieldShare)||user.getUID()==1){//没有开启权限，或者为系统管理员
			return v_restr;
		}
		
		if(parammap!=null){
			String p_isfromreq = Util.null2String((String)parammap.get("isfromreq"));//是否来自流程
			String p_requestid = Util.null2String((String)parammap.get("requestid"));//流程ID
			String p_nodeid = Util.null2String((String)parammap.get("nodeid"));//节点ID
			if("1".equals(p_isfromreq)||!"".equals(p_requestid)||!"".equals(p_nodeid)){
				v_isfromreq="1";//1 表示从流程进入
			}
		}
		if("1".equals(v_isfromreq)){//流程中进入
			this.getWORFieldShare(processname, parammap, user,p_sharelevel);
		}else {
			this.getCardFieldShare(processname, parammap, user, p_sharelevel);
		}
		if("1".equals(p_sharelevel)){
			if(processname.indexOf("view")>-1||processname.indexOf("search")>-1){
				v_restr="[]";//浏览页面不需 返回不可编辑的字段。
			}else{
				v_restr=notEditSet.toString();//1 不可编辑的字符串
			}
		}else {
			v_restr=notViewSet.toString();//1 2不可见的字符串
		}
		return v_restr;
	}
	public boolean getWORFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		boolean flag = false;
		String p_billid=Util.null2String(processname.split(":")[0]);//表单ID
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//节点ID
		list = shareInfo.getList("1",p_billid,p_nodeid,p_sharelevel);
		String v_fieldname ="";//字段名称
		String v_sharelevel = "";// 共享的等级
		for (WorkFieldShareInfo info : list) {
			v_fieldname=info.getFieldname();//字段名
			v_sharelevel = info.getSharelevel();// 共享的等级
			if ("1".equals(v_sharelevel)) {
				notEditSet.add(v_fieldname);//不可编辑的字段
			}else {
				notViewSet.add(v_fieldname);//不可见的字段
			}
		}
		//System.out.println("notViewSet流程:"+notViewSet.toString());
		//System.out.println("hiddenSet流程:"+notEditSet.toString());		
		return flag;
	}
	public boolean getCardFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		boolean flag = false;
		String p_billid=Util.null2String(processname.split(":")[0]);//表单ID
		String p_nodeid="";//节点ID	
		String p_prjid ="";//项目
		if(parammap!=null){
			p_prjid=Util.null2String((String)parammap.get("prjid"));//是否来自流程
		}
		list = shareInfo.getList("0",p_billid,p_nodeid,p_sharelevel);
		
		String v_fieldname ="";//字段名称
		String v_sharelevel = "";// 共享的等级		
		for (WorkFieldShareInfo info : list) {
			v_fieldname=info.getFieldname();//字段名
			v_sharelevel = info.getSharelevel();// 共享的等级
			System.out.println("v_fieldname:"+v_fieldname);
			if(!this.checkUserRight(info, p_prjid, user)){
				continue;
			}
			if ("1".equals(v_sharelevel)) {
				notEditSet.add(v_fieldname);//不可编辑的字段
			}else {
				notViewSet.add(v_fieldname);//不可见的字段
			}
		}
		//System.out.println("notViewSet卡片:"+notViewSet.toString());
		//System.out.println("hiddenSet卡片:"+notEditSet.toString());
		return flag;
	}
	
	public boolean checkUserRight(WorkFieldShareInfo info,String p_prjid,User user){
		boolean flag = false;
		String p_userid= user.getUID()+"";
		String p_deptid= user.getUserDepartment()+"";
		
		String v_sharetype = info.getSharetype();// 共享类型
		String v_seclevel = info.getSeclevel();// 安全级别
		String v_rolelevel = info.getRolelevel();// 角色级别
		String v_userid = info.getUserid();// 人员
		String v_deptid = info.getDeptid();// 部门
		String v_roleid = info.getRoleid();// 角色
		String v_alluser = info.getAlluser();// 所有人
		String v_sql="";
		String v_cnt="";
		if ("5".equals(v_sharetype)) {// 项目人员
			v_sql="select count(1) as cnt from prj_projectinfo prj where prj.id='"+p_prjid+"' and (prj.manager='"+p_userid+"' or ','||prj.members||',' like ',"+p_userid+",')";
			v_cnt=Util.null2String((String)common.executeSql(v_sql).get("cnt"));
			if(!"0".equals(v_cnt)){
				flag=true;
			}
		} else if ("4".equals(v_sharetype)) {// 所有人+安全级别
			if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())){
				flag=true;
			}
		} else if ("3".equals(v_sharetype)) {// 角色+级别
			if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())){
				v_sql="select count(1) as cnt from hrmrolemembers where roleid='"+v_roleid+"' and rolelevel>="+v_rolelevel+" and resourceid='"+p_userid+"' ";
				v_cnt=Util.null2String((String)common.executeSql(v_sql).get("cnt"));
				if(!"0".equals(v_cnt)){
					flag=true;
				}
			}
		} else if ("2".equals(v_sharetype)) {// 部门+安全级别
			if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())&&v_deptid.equals(p_deptid)){
				flag=true;
			}
		} else if ("1".equals(v_sharetype)) {// 人力资源
			if(v_userid.equals(p_userid)){
				flag=true;
			}
		}
		return flag;
	}
}
