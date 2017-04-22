package two.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * JDBC和数据库通用性操作的封装， 通过此类，可以获得数据库连接对象和对数据库增删改查的通用性操作
 * 
 * 
 * @author xuhongyun
 * 
 */
public class DBUtil {
	/** 驱动类名称 */
	private final String CLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/** JDBC连接路径 */
	private final String SQLURL = "jdbc:sqlserver://192.168.0.73:1433;DatabaseName=ZKTime";

	/** 数据库用户名 */
	private final String USER = "sa";

	/** 数据库用户密码 */
	private final String PASSWORD = "1";

	/** JDBC连接对象 */
	private Connection con;

	/** 预编译的 SQL 语句的对象 */
	private PreparedStatement pst;

	/**
	 * 创建数据库连接对象
	 * 
	 * @return JDBC连接对象
	 * @throws SQLException
	 *             数据库连接错误
	 * @throws ClassNotFoundException
	 *             找不到对应的类
	 */
	public Connection getConnection() throws SQLException,
			ClassNotFoundException {
		// 通过反射加载驱动类
		Class.forName(CLASSNAME);
		// 通过DriverManager类的静态方法创建数据库连接对象
		con = DriverManager.getConnection(SQLURL, USER, PASSWORD);
		return con;

	}

	/**
	 * 释放资源，关闭查询结果集、关闭预编译的SQL语句的对象和JDBC连接对象
	 * 
	 * @param rs
	 *            查询结果集
	 * @throws SQLException
	 *             数据库连接错误
	 */
	public void close(ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (pst != null)
			pst.close();
		// 在连接对象不为空且没有被关闭时，关闭连接对象
		if (con != null && !con.isClosed())
			con.close();
		// 清空对应数据，避免对下次操作的影响
		pst = null;
		con = null;

	}

	/**
	 * 释放资源，关闭预编译的SQL语句的对象和JDBC连接对象
	 * 
	 * @throws SQLException
	 *             数据库连接错误
	 */
	public void close() throws SQLException {
		this.close(null);
	}

	/**
	 * 补齐sql中占位符
	 * 
	 * @param pst
	 *            预编译的 SQL 语句的对象
	 * @param objects
	 *            补齐sql中占位符的具体参数
	 * @throws SQLException
	 *             数据库连接错误
	 */
	public void fillPrepareStatement(PreparedStatement pst, Object... objects)
			throws SQLException {
		// 判断操作中有没有占位符，如果没有，不做处理
		if (objects == null) {
			return;
		}
		// 声明一个整形变量，用于设置对应的参数属于第几个占位符
		int n = 1;
		// 遍历参数，判断参数的数据类型
		for (Object o : objects) {
			// 如果是时间类型，则需要转换为java.sql包中的时间类型
			if (o instanceof java.util.Date) {
				// 将Object类型的o强转为java.util包中的时间类型
				java.util.Date oldDate = (java.util.Date) o;
				// 得到时间的一个毫秒值
				long time = oldDate.getTime();
				// 以一个毫秒值创建一个java.sql包中的时间类型对象
				java.sql.Timestamp newDate = new java.sql.Timestamp(time);
				// 将数据设置到对应的占位符中
				pst.setTimestamp(n++, newDate);
			} else if (o == null) {
				// 如果是空值输入，则在对应的占位符中填充空值
				pst.setNull(n++, Types.NULL);
			} else {
				// 如果是其他的基本数据类型，则在对应的占位符中以Object类型填充
				pst.setObject(n++, o);
			}
		}
	}

	/**
	 * 通用性查询数据库操作
	 * 
	 * @param sql
	 *            查询语句字符串
	 * @param objects
	 *            补齐sql中占位符的具体参数
	 * @return 查询结果集
	 * @throws SQLException
	 *             数据库连接错误
	 * @throws ClassNotFoundException
	 *             找不到对应的类
	 */
	public ResultSet executeQuery(String sql, Object... objects)
			throws SQLException, ClassNotFoundException {
		// 如果数据库没有连接，创建数据库连接对象
		if (con == null)
			getConnection();
		// 将sql语句进行预编译
		pst = con.prepareStatement(sql);
		// 补齐sql中占位符
		fillPrepareStatement(pst, objects);
		// 执行查询操作
		return pst.executeQuery();
	}

	/**
	 * 通用性增、删、改数据库操作
	 * 
	 * @param sql
	 *            对应的增、删、改操作语句字符串
	 * @param objects
	 *            补齐sql中占位符的具体参数
	 * @return 受影响的行数
	 * @throws SQLException
	 *             数据库连接错误
	 * @throws ClassNotFoundException
	 *             找不到对应的类
	 */
	public int executeUpdate(String sql, Object... objects)
			throws SQLException, ClassNotFoundException {
		// 如果数据库没有连接，创建数据库连接对象
		if (con == null)
			getConnection();
		// 声明一个整形变量，用于保存受影响的行数
		int resault = -1;
		// 将sql语句进行预编译
		pst = con.prepareStatement(sql);
		// 补齐sql中占位符
		fillPrepareStatement(pst, objects);
		// 执行对应操作
		resault = pst.executeUpdate();
		// 释放资源
		close();
		return resault;
	}

}
