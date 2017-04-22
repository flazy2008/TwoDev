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
	String p_workShare=super.getPropValue("work_dev","WorkShare");//�Ƿ���Ȩ�޿���
	String v_sql="select count(1) as cnt from work_shareinfo where billid='XbillidX' and ctype='XctypeX'";

	public boolean checkUserRight(String processname, Map parammap, User user) {
		boolean flag = false;
		flag = this.checkUserRight(processname, "", parammap, user);
		return flag;
	}
	public boolean checkUserRight(String processname,String p_operType, Map parammap, User user) {
		boolean flag = false;
		p_operType="".equals(p_operType)?processname:p_operType;//�Ƿ񵥶�����Ȩ������ view,edit,add,search
		String v_isfromreq="0";
		
		if(parammap!=null){
			String p_isfromreq = Util.null2String((String)parammap.get("isfromreq"));//�Ƿ���������
			String p_requestid = Util.null2String((String)parammap.get("requestid"));//����ID
			String p_nodeid = Util.null2String((String)parammap.get("nodeid"));//�ڵ�ID
			if("1".equals(p_isfromreq)||!"".equals(p_requestid)||!"".equals(p_nodeid)){
				v_isfromreq="1";//1 ��ʾ�����̽���
			}
		}
		
		if("0".equals(p_workShare)||user.getUID()==1){//û�п���Ȩ�ޣ�����Ϊϵͳ����Ա
			flag=true;
		}else if(parammap==null){//û��ȡ������
			flag=false;
		}else if("1".equals(v_isfromreq)){//���̽���
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
		if(p_operType.indexOf("add")>-1||p_operType.indexOf("edit")>-1){//����
			p_sharelevel="2";
		}else{
			p_sharelevel="1,2";
		}
		if("0".equals((String)common.executeSql(v_sql.replace("XctypeX","1").replace("XbillidX", p_billid)).get("cnt"))){
			flag=true;//û�жԸñ�����Ȩ������
		}else{
			if(this.getUserRight(p_billid, "3", "0", parammap, user)){//flag=true ��ʾ����û�з���Ȩ��// 0 ��Ŀ��Ƭ���� 1 ���̽���
				flag=false;
			}else{
				flag = this.getUserRight(p_billid,p_sharelevel,"0",parammap,user);
			}
		}
		return flag;
	}	
	public boolean checkWORUserRight(String processname,String p_operType, Map parammap, User user) {
		boolean flag = false;
		String p_wfoper=Util.null2String((String)parammap.get("oper"));//0 �鿴 1 �༭
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//�ڵ�ID
		String p_billid=Util.null2String(processname.split(":")[0]);
		String p_sharelevel="1";
		if(p_operType.indexOf("add")>-1||p_operType.indexOf("edit")>-1||p_wfoper.indexOf("1,2")>-1){//����
			p_sharelevel="2";
		}else{
			p_sharelevel="1,2";
		}		
		if((p_operType.indexOf("edit")>-1||p_operType.indexOf("add")>-1)&&"0".equals(p_wfoper)){//����
			flag=false;
		}else if("".equals(p_nodeid)){//���̽���û��ȡ���ڵ�ID
			flag=false;
		}else if("0".equals((String)common.executeSql(v_sql.replace("XctypeX","1").replace("XbillidX", p_billid)).get("cnt"))){
			flag=true;//û�жԸñ�����Ȩ������
		}else{
			if(this.getUserRight(p_billid, "3", "1 ", parammap, user)){//flag=true ��ʾ����û�з���Ȩ��// 0 ��Ŀ��Ƭ���� 1 ���̽���
				flag=false;
			}else{
				flag = this.getUserRight(p_billid,p_sharelevel,"1",parammap,user);
			}
		}
		return flag;
	}
	public boolean getUserRight(String p_billid,String p_sharelevel,String v_ctype,Map parammap,User user) {
		boolean flag = false;
		//0 Ĭ��Ϊ��Ŀ��Ƭ���� 1    Ϊ���̽���  ctype
		String p_prjid=Util.null2String((String)parammap.get("prjid"));//��Ŀ
		String p_busid=Util.null2String((String)parammap.get("busid"));//ҵ��
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//�ڵ�ID
		list = shareInfo.getList(v_ctype,p_billid,p_nodeid,p_sharelevel);
		String p_userid= user.getUID()+"";
		String p_deptid= user.getUserDepartment()+"";
		String v_sharetype = "";// ��������
		String v_seclevel = "";// ��ȫ����n
		String v_rolelevel = "";// ��ɫ����
		String v_sharelevel = "";// ����ĵȼ�
		String v_userid = "";// ��Ա
		String v_deptid = "";// ����
		String v_roleid = "";// ��ɫ
		String v_alluser = "";// ������
		String v_sql="";
		String v_cnt="";
		for (WorkShareInfo info : list) {
			v_sharetype = info.getSharetype();// ��������
			v_seclevel = info.getSeclevel();// ��ȫ����
			v_rolelevel = info.getRolelevel();// ��ɫ����
			v_sharelevel = info.getSharelevel();// ����ĵȼ�
			v_userid = info.getUserid();// ��Ա
			v_deptid = info.getDeptid();// ����
			v_roleid = info.getRoleid();// ��ɫ
			v_alluser = info.getAlluser();// ������
			if ("5".equals(v_sharetype)) {// ��Ŀ��Ա
				v_sql="select count(1) as cnt from prj_projectinfo prj where prj.id='"+p_prjid+"' and (prj.manager='"+p_userid+"' or ','||prj.members||',' like ',"+p_userid+",')";
				v_cnt=Util.null2String((String)common.executeSql(v_sql).get("cnt"));
				if(!"0".equals(v_cnt)){
					flag=true;
				}
			} else if ("4".equals(v_sharetype)) {// ������+��ȫ����
				if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())){
					flag=true;
				}
			} else if ("3".equals(v_sharetype)) {// ��ɫ+����
				if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())){
					v_sql="select count(1) as cnt from hrmrolemembers where roleid='"+v_roleid+"' and rolelevel>="+v_rolelevel+" and resourceid='"+p_userid+"' ";
					v_cnt=Util.null2String((String)common.executeSql(v_sql).get("cnt"));
					if(!"0".equals(v_cnt)){
						flag=true;
					}
				}
			} else if ("2".equals(v_sharetype)) {// ����+��ȫ����
				if(Util.getIntValue(v_seclevel)<=Util.getIntValue(user.getSeclevel())&&v_deptid.equals(p_deptid)){
					flag=true;
				}
			} else if ("1".equals(v_sharetype)) {// ������Դ
				if(v_userid.equals(p_userid)){
					flag=true;
				}
			}
			if(flag==true){//���ҵ�Ȩ���˳�ѭ��
				break;
			}
		}
		return flag;
	}
}
