package two.util;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OraclePreparedStatement;
import oracle.sql.CLOB;
import weaver.general.BaseBean;

public class OracleClob extends BaseBean {
	Connection con = null;

	public OracleClob() {
		OutDBUtil dbUtil = new OutDBUtil();
		try {
			String url = super.getPropValue("weaver", "ecology.url");
			String user = super.getPropValue("weaver", "ecology.user");
			String password = super.getPropValue("weaver", "ecology.password");

			con = dbUtil.getConnection("oracle.jdbc.driver.OracleDriver", url,user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean updateClob(String tableName, String fieldName, String id,
			String content) {
		boolean flag = false;
		try {
			String v_sql = "UPDATE " + tableName + " set " + fieldName
					+ "=? Where id='" + id + "' ";

			System.out.println(v_sql);
			con.setAutoCommit(false); // conn为Connection对象
			// 创建并实例化一个CLOB对象
			CLOB clob = new CLOB((OracleConnection) con);
			clob = oracle.sql.CLOB.createTemporary((OracleConnection) con,
					true, 1);
			// 对CLOB对象赋值
			clob.putChars(1, content.toCharArray());
			OracleConnection OCon = (OracleConnection) con;
			OraclePreparedStatement pstmt = (OraclePreparedStatement) OCon
					.prepareCall(v_sql);
			pstmt.setCLOB(1, clob);
			int i = pstmt.executeUpdate();
			pstmt.close();
			OCon.commit();
			OCon = null;
			con = null;
			if (i > 0) {
				flag = true;
			}
			return flag;
		} catch (SQLException e) {
			System.out.println("OracleClob出错啦！");
		}
		return false;
	}
}
