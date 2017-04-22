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
	String p_FieldShare=super.getPropValue("work_dev","FieldShare");//�Ƿ���Ȩ�޿���
	
	public BillFieldVarify() {
		notEditSet=new HashSet<String>();
		notViewSet=new HashSet<String>();
	}
	public String getFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		String v_restr="[]";//�����ַ���
		String v_isfromreq="0";
		p_sharelevel="".equals(p_sharelevel)?"1":p_sharelevel;//Ȩ������ 1 ���ɱ༭ 2���ɼ�
		
		if("0".equals(p_FieldShare)||user.getUID()==1){//û�п���Ȩ�ޣ�����Ϊϵͳ����Ա
			return v_restr;
		}
		
		if(parammap!=null){
			String p_isfromreq = Util.null2String((String)parammap.get("isfromreq"));//�Ƿ���������
			String p_requestid = Util.null2String((String)parammap.get("requestid"));//����ID
			String p_nodeid = Util.null2String((String)parammap.get("nodeid"));//�ڵ�ID
			if("1".equals(p_isfromreq)||!"".equals(p_requestid)||!"".equals(p_nodeid)){
				v_isfromreq="1";//1 ��ʾ�����̽���
			}
		}
		if("1".equals(v_isfromreq)){//�����н���
			this.getWORFieldShare(processname, parammap, user,p_sharelevel);
		}else {
			this.getCardFieldShare(processname, parammap, user, p_sharelevel);
		}
		if("1".equals(p_sharelevel)){
			if(processname.indexOf("view")>-1||processname.indexOf("search")>-1){
				v_restr="[]";//���ҳ�治�� ���ز��ɱ༭���ֶΡ�
			}else{
				v_restr=notEditSet.toString();//1 ���ɱ༭���ַ���
			}
		}else {
			v_restr=notViewSet.toString();//1 2���ɼ����ַ���
		}
		return v_restr;
	}
	public boolean getWORFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		boolean flag = false;
		String p_billid=Util.null2String(processname.split(":")[0]);//��ID
		String p_nodeid=Util.null2String((String)parammap.get("nodeid"));//�ڵ�ID
		list = shareInfo.getList("1",p_billid,p_nodeid,p_sharelevel);
		String v_fieldname ="";//�ֶ�����
		String v_sharelevel = "";// ����ĵȼ�
		for (WorkFieldShareInfo info : list) {
			v_fieldname=info.getFieldname();//�ֶ���
			v_sharelevel = info.getSharelevel();// ����ĵȼ�
			if ("1".equals(v_sharelevel)) {
				notEditSet.add(v_fieldname);//���ɱ༭���ֶ�
			}else {
				notViewSet.add(v_fieldname);//���ɼ����ֶ�
			}
		}
		//System.out.println("notViewSet����:"+notViewSet.toString());
		//System.out.println("hiddenSet����:"+notEditSet.toString());		
		return flag;
	}
	public boolean getCardFieldShare(String processname,Map parammap, User user,String p_sharelevel) {
		boolean flag = false;
		String p_billid=Util.null2String(processname.split(":")[0]);//��ID
		String p_nodeid="";//�ڵ�ID	
		String p_prjid ="";//��Ŀ
		if(parammap!=null){
			p_prjid=Util.null2String((String)parammap.get("prjid"));//�Ƿ���������
		}
		list = shareInfo.getList("0",p_billid,p_nodeid,p_sharelevel);
		
		String v_fieldname ="";//�ֶ�����
		String v_sharelevel = "";// ����ĵȼ�		
		for (WorkFieldShareInfo info : list) {
			v_fieldname=info.getFieldname();//�ֶ���
			v_sharelevel = info.getSharelevel();// ����ĵȼ�
			System.out.println("v_fieldname:"+v_fieldname);
			if(!this.checkUserRight(info, p_prjid, user)){
				continue;
			}
			if ("1".equals(v_sharelevel)) {
				notEditSet.add(v_fieldname);//���ɱ༭���ֶ�
			}else {
				notViewSet.add(v_fieldname);//���ɼ����ֶ�
			}
		}
		//System.out.println("notViewSet��Ƭ:"+notViewSet.toString());
		//System.out.println("hiddenSet��Ƭ:"+notEditSet.toString());
		return flag;
	}
	
	public boolean checkUserRight(WorkFieldShareInfo info,String p_prjid,User user){
		boolean flag = false;
		String p_userid= user.getUID()+"";
		String p_deptid= user.getUserDepartment()+"";
		
		String v_sharetype = info.getSharetype();// ��������
		String v_seclevel = info.getSeclevel();// ��ȫ����
		String v_rolelevel = info.getRolelevel();// ��ɫ����
		String v_userid = info.getUserid();// ��Ա
		String v_deptid = info.getDeptid();// ����
		String v_roleid = info.getRoleid();// ��ɫ
		String v_alluser = info.getAlluser();// ������
		String v_sql="";
		String v_cnt="";
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
		return flag;
	}
}
