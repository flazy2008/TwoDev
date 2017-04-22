package two.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import weaver.general.StaticObj;
import weaver.interfaces.datasource.DataSource;

/**
 * JDBC和数据库通用性操作的封装， 通过此类，可以获得数据库连接对象和对数据库增删改查的通用性操作
 * 
 * 
 * @author xuhongyun
 * 
 */
public class OutDBUtil {
	/** JDBC连接对象 */
	private Connection conn;

	/** 预编译的 SQL 语句的对象 */
	private PreparedStatement pst;
	private String dateStr;

	public OutDBUtil() {
	}
	public OutDBUtil(String datStr) {
		this.dateStr = datStr;
	}

	/**
	 * 创建数据库连接对象
	 * 
	 * @return JDBC连接对象
	 * @throws SQLException
	 *             数据库连接错误
	 * @throws ClassNotFoundException
	 *             找不到对应的类
	 */
	public Connection getConnection() {
		DataSource ds = (DataSource) StaticObj.getServiceByFullname(dateStr,DataSource.class);
		conn = ds.getConnection();
		return conn;

	}
	public Connection getConnection(String CLASSNAME, String SQLURL,
			String USER, String PASSWORD) throws SQLException,
			ClassNotFoundException {
		// 通过反射加载驱动类
		Class.forName(CLASSNAME);
		// 通过DriverManager类的静态方法创建数据库连接对象
		conn = DriverManager.getConnection(SQLURL, USER, PASSWORD);
		return conn;
	}


	/**
	 * 释放资源，关闭查询结果集、关闭预编译的SQL语句的对象和JDBC连接对象
	 * 
	 * @param rs
	 *            查询结果集
	 * @throws SQLException
	 *             数据库连接错误
	 */
	public void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
			pst = null;
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
	public ResultSet executeQuery(String sql, Object... objects) {
		// 如果数据库没有连接，创建数据库连接对象
		ResultSet rs = null;
		try {
			if (conn == null)
				getConnection();
			// 将sql语句进行预编译
			pst = conn.prepareStatement(sql);
			// 补齐sql中占位符
			fillPrepareStatement(pst, objects);
			// 执行查询操作
			rs = pst.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
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
	public int executeUpdate(String sql, Object... objects) {
		int resault = -1;
		try {
			// 如果数据库没有连接，创建数据库连接对象
			if (conn == null)
				getConnection();
			// 声明一个整形变量，用于保存受影响的行数
			// 将sql语句进行预编译
			pst = conn.prepareStatement(sql);
			// 补齐sql中占位符
			fillPrepareStatement(pst, objects);
			// 执行对应操作
			resault = pst.executeUpdate();
			// 释放资源
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resault;
	}

}
