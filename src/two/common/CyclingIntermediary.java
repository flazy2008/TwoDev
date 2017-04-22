package two.common;

import weaver.conn.RecordSet;
import weaver.general.Util;

/**
 * 循环选择评估机构 2012/1/9
 * 
 * @author hongyun.xu
 * 
 */
public class CyclingIntermediary {
	public String cyclingIntermediary(String v_busid) {
		RecordSet rs = new RecordSet();
		String result = " _ ";
		String sql = "select id,organizationnames from nd_intermediary where id=(select max(maep) as maep  from nd_revMeasures where maep is not null and busid='"+ v_busid + "')";
		rs.executeSql(sql);
		if (rs.next()) {// 如果找到以前评审机构返回
			result = Util.null2String(rs.getString("id")) + "_"+ Util.null2String(rs.getString("organizationnames"));
		} else {
			sql = "select id,organizationnames from nd_intermediary where id=(select min(id) as id from nd_intermediary where id > (select maep from nd_revMeasures where maep is not null and id =(select max(id) from nd_revMeasures where maep is not null)))";
			rs.executeSql(sql);
			if (rs.next()) {
				result = Util.null2String(rs.getString("id")) + "_"+ Util.null2String(rs.getString("organizationnames"));
			} else {
				sql = "select id,organizationnames from nd_intermediary where id=(select min(id) from nd_intermediary)";
				rs.executeSql(sql);
				if (rs.next()) {
					result = Util.null2String(rs.getString("id"))+ "_"+ Util.null2String(rs.getString("organizationnames"));
				}
			}
		}
		return result;
	}
}
