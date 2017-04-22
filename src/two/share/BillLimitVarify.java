package two.share;

import java.util.List;
import java.util.Map;

import two.common.CommonInfoByID;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;

public class BillLimitVarify extends BaseBean {
	WorkShareInfo shareInfo = new WorkShareInfo();
	CommonInfoByID common=new CommonInfoByID();
	List<WorkShareInfo> list = null;
	String p_workShare=super.getPropValue("work_dev","WorkShare");//是否开启权限控制
	String v_sql="select count(1) as cnt from work_shareinfo where billid='XbillidX' and ctype='XctypeX'";

	public boolean checkUserRight(String processname, Map parammap, User user) {
		boolean flag = false;
		flag = this.checkUserRight(processname, "", parammap, user);
		return flag;
	}
	public boolean checkUserRight(String processname,String p_operType, Map parammap, User user) {
		boolean flag = false;
		p_operType="".equals(p_operType)?processname:p_operType;//是否单独传入权限类型 view,edit,add,search
		String v_isfromreq="0";
		
		if(parammap!=null){
			String p_isfromreq = Util.null2String((String)parammap.get("isfromreq"));//是否来自流程
			String p_requestid = Util.null2String((String)parammap.get("requestid"));//流程ID
			String p_nodeid = Util.null2String((String)parammap.get("nodeid"));//节点ID
			if("1".equals(p_isfromreq)||!"".equals(p_requestid)||!"".equals(p_nodeid)){
				v_isfromreq="1";//1 表示从流程进入
			}
		}
		
		if("0".equals(p_workShare)||user.getUID()==1){//没有开启权限，或者为系统管理员
			flag=true;
		}else if(parammap==null){//没有取到参数
			flag=false;
		}else if("1".equals(v_isfromreq)){//流程进入
			flag=this.checkWORUserRight(processname, p_operType, parammap, user);
		}else {
			flag=this.checkCardUserRight(processname, p_operType, parammap, user);
		}
		return flag;
	}
	public boolean checkCardUserRight(String processname,String p_operType, Map parammap, User user) {
		boolean flag = false;
		String p_billid=Util.null2String(processname.split(":")[0]);
		String p_sharelevel="1";
		if(p_operType.indexOf("add")>-1||p_operType.indexOf("edit")>-1){//类型
			p_sharelevel="2";
		}else{
			p_sharelevel="1,2";
		}
		if("0".equals((String)common.executeSql(v_sql.replace("XctypeX","1").replace("XbillidX", p_billid)).get("cnt"))){
			flag=true;//没有对该表单进行权限设置
		}else{
			if(this.getUserRight(p_billid, "3", "0", parammap, user)){//flag=true 表示该人没有访问权限// 0 项目卡片进入 1 流程进入
				flag=false;
			}else{
				flag = this.getUserRight(p_billid,p_sharelevel,"0",parammap,user);
			}
		}
		return flag;
	}	
	public boolean checkWORUserRight(String processname,String p_operType, Map parammap, User user) {
		boolean flag = false;
		String p_wfoper=Util.null2String((String)parammap.get("oper"));//0 查看 1 编辑
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//节点ID
		String p_billid=Util.null2String(processname.split(":")[0]);
		String p_sharelevel="1";
		if(p_operType.indexOf("add")>-1||p_operType.indexOf("edit")>-1||p_wfoper.indexOf("1,2")>-1){//类型
			p_sharelevel="2";
		}else{
			p_sharelevel="1,2";
		}		
		if((p_operType.indexOf("edit")>-1||p_operType.indexOf("add")>-1)&&"0".equals(p_wfoper)){//类型
			flag=false;
		}else if("".equals(p_nodeid)){//流程进入没有取到节点ID
			flag=false;
		}else if("0".equals((String)common.executeSql(v_sql.replace("XctypeX","1").replace("XbillidX", p_billid)).get("cnt"))){
			flag=true;//没有对该表单进行权限设置
		}else{
			if(this.getUserRight(p_billid, "3", "1 ", parammap, user)){//flag=true 表示该人没有访问权限// 0 项目卡片进入 1 流程进入
				flag=false;
			}else{
				flag = this.getUserRight(p_billid,p_sharelevel,"1",parammap,user);
			}
		}
		return flag;
	}
	public boolean getUserRight(String p_billid,String p_sharelevel,String v_ctype,Map parammap,User user) {
		boolean flag = false;
		//0 默认为项目卡片进入 1    为流程进入  ctype
		String p_prjid=Util.null2String((String)parammap.get("prjid"));//项目
		String p_busid=Util.null2String((String)parammap.get("busid"));//业务
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//节点ID
		list = shareInfo.getList(v_ctype,p_billid,p_nodeid,p_sharelevel);
		String p_userid= user.getUID()+"";
		String p_deptid= user.getUserDepartment()+"";
		String v_sharetype = "";// 共享类型
		String v_seclevel = "";// 安全级别n
		String v_rolelevel = "";// 角色级别
		String v_sharelevel = "";// 共享的等级
		String v_userid = "";// 人员
		String v_deptid = "";// 部门
		String v_roleid = "";// 角色
		String v_alluser = "";// 所有人
		String v_sql="";
		String v_cnt="";
		for (WorkShareInfo info : list) {
			v_sharetype = info.getSharetype();// 共享类型
			v_seclevel = info.getSeclevel();// 安全级别
			v_rolelevel = info.getRolelevel();// 角色级别
			v_sharelevel = info.getSharelevel();// 共享的等级
			v_userid = info.getUserid();// 人员
			v_deptid = info.getDeptid();// 部门
			v_roleid = info.getRoleid();// 角色
			v_alluser = info.getAlluser();// 所有人
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
			if(flag==true){//查找到权限退出循环
				break;
			}
		}
		return flag;
	}
}
