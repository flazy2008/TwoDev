package conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleUtil {
	public static Connection getConn() {
		Connection conn = null;
		try { 
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			String url = "jdbc:oracle:thin:@10.10.10.159:1521:hanhuamis"; // orcl为数据库的SID
			String user = "misdba";
			String password = "1";  
			conn = DriverManager.getConnection(url, user, password);

		} catch (Exception e) {  
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn; 
	}

	public static Statement getResult() {
		Statement pst = null;
		try {
			Connection conn = OracleUtil.getConn();
			conn = OracleUtil.getConn();
			pst = conn.createStatement();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pst;

	}

	public static void closeConn(ResultSet rs, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
