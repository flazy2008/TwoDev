package weaver.system;

import weaver.conn.RecordSet;

public class setLeftMenu {
	public static void getUpdateLeftMenu() {
		try {
			int count = 0;
			String sql = "select * from hrmsubcompany";
			RecordSet rs = new RecordSet();
			rs.executeSql(sql);
			while (rs.next()) {
				String sql2 = "select count(*) as count from leftmenuconfig where resourceid='"
						+ rs.getString("id") + "'";
				RecordSet rs2 = new RecordSet();
				rs2.executeSql(sql2);
				if (rs2.next())
					count = rs2.getInt("count");
				if (count == 0) {
					String strWhere = " where resourcetype=2 and resourceid="
							+ rs.getInt("supsubcomid");
					if (rs.getInt("supsubcomid") == 0)
						strWhere = " where resourcetype=1  and resourceid=1 ";
					String strSql = "insert into leftmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  " +
							"select  distinct  userid,infoid,visible,viewindex,"
							+ rs.getInt("id")
							+ ",2,locked,lockedbyid,usecustomname,customname,customname_e from leftmenuconfig "
							+ strWhere;
					RecordSet rs3 = new RecordSet();
					rs3.executeSql(strSql);
				}
			}
		} catch (Exception e) {
			System.out.println((new StringBuilder("更新左侧菜单异常:")).append(
					e.getMessage()).toString());
		}
	}
}
