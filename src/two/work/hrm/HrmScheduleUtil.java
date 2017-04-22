package two.work.hrm;

import java.util.ArrayList;
import java.util.HashMap;

import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.hrm.report.schedulediff.HrmScheduleDiffUtil;


public class HrmScheduleUtil {
	private String criticalForAM;
	private String criticalForPM;
	RecordSet recordSet=new RecordSet();
	ArrayList reList =null;
	HrmScheduleDiffUtil scheduleUtil=new HrmScheduleDiffUtil();
	HashMap rowMap = null;
	String v_sql="";
	public HrmScheduleUtil() {
		criticalForAM = "09:00:00";
		criticalForPM = "17:30:00";
	}
	public  ArrayList getScheduleList(int resourceid,String v_fromdate,String v_enddate){
		reList = new ArrayList();
		rowMap = new HashMap();
		HrmCardInfoBean infoBean=null;
		String v_tmpDate="";
		int v_day=TimeUtil.dateInterval(v_fromdate,v_enddate) ;
		int v_index=0;
	    while(v_index<=v_day){
			v_tmpDate=TimeUtil.dateAdd(v_fromdate,v_index++);
			infoBean=new HrmCardInfoBean();
			infoBean.setUserid(resourceid+"");
			infoBean.setCarddate(v_tmpDate);
			v_sql="select * from nd_HrmCardInfo where userid='"+resourceid+"' and carddate='"+v_tmpDate+"' order by isexcep ";
			recordSet.executeSql(v_sql);
			if(recordSet.next()){
				infoBean.setIntime(Util.null2String(recordSet.getString("intime")));
				infoBean.setOuttime(Util.null2String(recordSet.getString("outtime")));
				infoBean.setIsexcep(Util.null2String(recordSet.getString("isexcep")));//是否异常设置
			}else{
				infoBean.setIsexcep("1");//是否异常设置为1
			}
			if(!scheduleUtil.getIsWorkday(v_tmpDate)){//不是工作日
				infoBean.setIsexcep("0");//是否异常设置为1
			}
			reList.add(infoBean);
	    }
	    this.updateScheduleData();
	    return reList;
	}
	private void updateScheduleData(){
		HrmCardInfoBean infoBean=null;
		for (int i=0;i<reList.size();i++) {
			infoBean=(HrmCardInfoBean)reList.get(i);
			if("0".equals(infoBean.getIsexcep())){//没有异常
				continue;
			}
			this.isEvection(infoBean);//是否出差
			this.isVacate(infoBean);//是否请假
			this.isBeout(infoBean);//是否外出
			this.isBelateorLeaveearlyorWork(infoBean);//是否迟到或早退或旷工
		}
	}
	private void isEvection(HrmCardInfoBean infoBean){//是否出差
		if("0".equals(infoBean.getIsexcep())){//没有异常
			return;
		}
		v_sql="select count(1) as cnt from formtable_main_3 where '"+infoBean.getCarddate()+"'>=caksa and '"+infoBean.getCarddate()+"'<=caisf and sqr='"+infoBean.getUserid()+"'";
		recordSet.executeSql(v_sql);
		recordSet.next();
		String v_cnt=Util.null2String(recordSet.getString("cnt"));
		//System.out.println("v_sql:"+v_sql+"   resourceid:"+infoBean.getUserid()+" v_cnt:"+v_cnt);
		if(!"0".equals(v_cnt)){
			infoBean.setEvection("1");
			infoBean.setIsexcep("0");
		}
	}
	private void isVacate(HrmCardInfoBean infoBean){//是否请假
		if("0".equals(infoBean.getIsexcep())){//没有异常
			return;
		}
		v_sql="select count(1) as cnt from formtable_main_58 where '"+infoBean.getCarddate()+"'>=ksrq and '"+infoBean.getCarddate()+"'<=jsrq and singm='"+infoBean.getUserid()+"'";
		recordSet.executeSql(v_sql);
		recordSet.next();
		String v_cnt=Util.null2String(recordSet.getString("cnt"));
		if(!"0".equals(v_cnt)){
			infoBean.setVacate("1");
			infoBean.setIsexcep("0");
		}
	}
	private void isBeout(HrmCardInfoBean infoBean){//是否外出
		if("0".equals(infoBean.getIsexcep())){//没有异常
			return;
		}
		v_sql="select count(1) as cnt from formtable_main_59 where '"+infoBean.getCarddate()+"'>=kaish and '"+infoBean.getCarddate()+"'<=jiehus and xn='"+infoBean.getUserid()+"'";
		recordSet.executeSql(v_sql);
		recordSet.next();
		String v_cnt=Util.null2String(recordSet.getString("cnt"));
		if(!"0".equals(v_cnt)){
			infoBean.setBeout("1");
			infoBean.setIsexcep("0");
		}
	}
	private void isBelateorLeaveearlyorWork(HrmCardInfoBean infoBean){//是否迟到或早退或旷工
		if("0".equals(infoBean.getIsexcep())){//没有异常
			return;
		}
		String v_intime=infoBean.getIntime();
		String v_outtime=infoBean.getOuttime();
		if("".equals(v_intime)&&"".equals(v_outtime)){
			infoBean.setNowork("1");
		}else if("".equals(v_intime)||"".equals(v_outtime)){
			infoBean.setNowork("0.5");
		}
		if(!"".equals(v_intime)&&criticalForAM.compareTo(v_intime)>0){//criticalForAM>v_intime
			infoBean.setBelate("1");//迟到
		}
		if(!"".equals(v_outtime)&&criticalForPM.compareTo(v_outtime)>0){//criticalForPM>v_intime
			infoBean.setBeout("1");//早退
		}
		
	}
}
