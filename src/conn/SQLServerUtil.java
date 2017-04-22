package conn;

import java.sql.*;

public class SQLServerUtil
{

	public SQLServerUtil()
	{
	} 

	public static Connection getConnect()
	{
		Connection conn = null;
		String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String URLNAME = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=ecology_group";
		String NAME = "sa";  
		String PASS = "1";
		try
		{
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URLNAME, NAME, PASS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}

	public static void colseConn(Connection conn, PreparedStatement pstmt, ResultSet rs)
	{
		if (rs != null)
			try
			{
				rs.close();
			}
			catch (Exception exception) { }
		if (pstmt != null)
			try
			{
				pstmt.close();
			}
			catch (Exception exception1) { }
		if (conn != null)
			try
			{
				conn.close();
			}
			catch (Exception exception2) { }
	}

	
	public static void main(String[] args) {
		System.out.println(getConnect());
	}
}
