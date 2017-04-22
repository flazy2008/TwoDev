package two.work.hrm;

import java.sql.ResultSet;
import java.sql.SQLException;

import two.common.CommonInfoByID;
import two.util.DateUtil;
import two.util.OutDBUtil;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.general.Util;

public class HrmScheduleData {
	OutDBUtil dbUtil=new OutDBUtil("datasource.hrmschedule");
	ResultSet rs=null;
	CommonInfoByID common=new CommonInfoByID();
	RecordSet recordSet=new RecordSet();
	RecordSet recordSet2=new RecordSet();
	String v_sql="";
	public boolean loadScheduleData(String v_fromdate,String v_todate){
		boolean flag=false;
		String v_cardid="";
		String v_checktime="";
		String v_checktype="";
		String v_verifycode="";
		String v_sensorid="";
		try {
			v_sql=" delete nd_hrmcheckinout where getdated(checktime,'ymd')>='"+v_fromdate+"'  and   getdated(checktime,'ymd')<='"+v_todate+"' ";
			recordSet.executeSql(v_sql);
			//System.out.print(v_sql);
			v_sql=" select * from checkinout where CONVERT(varchar(10),checktime,120)>='"+v_fromdate+"' and CONVERT(varchar(10),checktime,120)<='"+v_todate+"' ";
			rs=dbUtil.executeQuery(v_sql);
			//System.out.print(v_sql);
			while (rs.next()) {
				v_cardid=Util.null2String(rs.getString("userid"));
				v_checktime=Util.null2String(rs.getString("checktime"));
				v_checktype=Util.null2String(rs.getString("checktype"));
				v_verifycode=Util.null2String(rs.getString("verifycode"));
				v_sensorid=Util.null2String(rs.getString("sensorid"));
				v_sql="insert into nd_hrmcheckinout(cardid, checktime, checktype, verifycode, sensorid) values('"+v_cardid+"', to_date('"+v_checktime.substring(0,19)+"','yyyy-mm-dd hh24:mi:ss'), '"+v_checktype+"', '"+v_verifycode+"', '"+v_sensorid+"')";
				recordSet.executeSql(v_sql);
				flag=true;
			}
			this.writeHrmCardInfo(v_fromdate, v_todate);			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbUtil.close(rs);
		return flag;
	}	
	public boolean loadScheduleData(){
		boolean flag=false;
		String v_todate=DateUtil.getNowDate("1");
		String v_fromdate=TimeUtil.dateAdd(v_todate,-1);
		flag=this.loadScheduleData(v_fromdate, v_todate);
		return flag;
	}
	public void writeHrmCardInfo(String v_fromdate,String v_todate){
		String v_cardid="";
		String v_carddate="";
		String v_intime="";
		String v_outtime="";
		
		v_sql="select cardid, getdated(checktime, 'ymd') as carddate  from nd_hrmcheckinout where getdated(checktime,'ymd')>='"+v_fromdate+"'  and   getdated(checktime,'ymd')<='"+v_todate+"'  group by cardid, getdated(checktime, 'ymd')";
		recordSet.executeSql(v_sql);
		while(recordSet.next()){
			v_cardid=Util.null2String(recordSet.getString("cardid"));
			v_carddate=Util.null2String(recordSet.getString("carddate"));
			v_sql="select to_char(min(checktime), 'yyyy-mm-dd hh24:mi:ss') as carddate from nd_hrmcheckinout where cardid='"+v_cardid+"' and checktime>=to_date('"+v_carddate+" 06:00:00','yyyy-mm-dd hh24:mi:ss') and checktime<=to_date('"+v_carddate+" 12:00:00','yyyy-mm-dd hh24:mi:ss')";
			v_intime=Util.null2String((String)common.executeSql(v_sql).get("carddate"));
			v_sql="select to_char(max(checktime), 'yyyy-mm-dd hh24:mi:ss') as carddate from nd_hrmcheckinout where cardid='"+v_cardid+"' and checktime>=to_date('"+v_carddate+" 12:00:00','yyyy-mm-dd hh24:mi:ss') and checktime<=to_date('"+v_carddate+" 23:59:59','yyyy-mm-dd hh24:mi:ss')";
			v_outtime=Util.null2String((String)common.executeSql(v_sql).get("carddate"));
			v_sql="delete nd_HrmCardInfo where cardid='"+v_cardid+"' and carddate='"+v_carddate+"'";
			common.executeSql(v_sql, "del");//É¾³ý¼ÇÂ¼Êý¾Ý
			
			if(v_outtime.indexOf(" ")==10){
				v_outtime=v_outtime.substring(11);
			}
			if(v_intime.indexOf(" ")==10){
				v_intime=v_intime.substring(11);
			}
			v_sql="insert into nd_hrmcardinfo(cardid, carddate, intime, outtime)values ('"+v_cardid+"', '"+v_carddate+"', '"+v_intime+"', '"+v_outtime+"') ";
			common.executeSql(v_sql, "insert");
		}
		v_sql="update nd_hrmcardinfo t1 set isexcep=case when intime>'09:00:00' or intime is null or outtime is null  or outtime<='17:30:00' then 1 else 0 end, userid=(select t2.resourceid from hrmtimecarduser t2 where t1.cardid=t2.usercode)  where carddate>='"+v_fromdate+"' and carddate<='"+v_todate+"'";
		common.executeSql(v_sql, "update");
	}

}
