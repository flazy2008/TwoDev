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
 * JDBC�����ݿ�ͨ���Բ����ķ�װ�� ͨ�����࣬���Ի�����ݿ����Ӷ���Ͷ����ݿ���ɾ�Ĳ��ͨ���Բ���
 * 
 * 
 * @author xuhongyun
 * 
 */
public class OutDBUtil {
	/** JDBC���Ӷ��� */
	private Connection conn;

	/** Ԥ����� SQL ���Ķ��� */
	private PreparedStatement pst;
	private String dateStr;

	public OutDBUtil() {
	}
	public OutDBUtil(String datStr) {
		this.dateStr = datStr;
	}

	/**
	 * �������ݿ����Ӷ���
	 * 
	 * @return JDBC���Ӷ���
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 * @throws ClassNotFoundException
	 *             �Ҳ�����Ӧ����
	 */
	public Connection getConnection() {
		DataSource ds = (DataSource) StaticObj.getServiceByFullname(dateStr,DataSource.class);
		conn = ds.getConnection();
		return conn;

	}
	public Connection getConnection(String CLASSNAME, String SQLURL,
			String USER, String PASSWORD) throws SQLException,
			ClassNotFoundException {
		// ͨ���������������
		Class.forName(CLASSNAME);
		// ͨ��DriverManager��ľ�̬�����������ݿ����Ӷ���
		conn = DriverManager.getConnection(SQLURL, USER, PASSWORD);
		return conn;
	}


	/**
	 * �ͷ���Դ���رղ�ѯ��������ر�Ԥ�����SQL���Ķ����JDBC���Ӷ���
	 * 
	 * @param rs
	 *            ��ѯ�����
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
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
	 * �ͷ���Դ���ر�Ԥ�����SQL���Ķ����JDBC���Ӷ���
	 * 
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 */
	public void close() throws SQLException {
		this.close(null);
	}

	/**
	 * ����sql��ռλ��
	 * 
	 * @param pst
	 *            Ԥ����� SQL ���Ķ���
	 * @param objects
	 *            ����sql��ռλ���ľ������
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 */
	public void fillPrepareStatement(PreparedStatement pst, Object... objects)
			throws SQLException {
		// �жϲ�������û��ռλ�������û�У���������
		if (objects == null) {
			return;
		}
		// ����һ�����α������������ö�Ӧ�Ĳ������ڵڼ���ռλ��
		int n = 1;
		// �����������жϲ�������������
		for (Object o : objects) {
			// �����ʱ�����ͣ�����Ҫת��Ϊjava.sql���е�ʱ������
			if (o instanceof java.util.Date) {
				// ��Object���͵�oǿתΪjava.util���е�ʱ������
				java.util.Date oldDate = (java.util.Date) o;
				// �õ�ʱ���һ������ֵ
				long time = oldDate.getTime();
				// ��һ������ֵ����һ��java.sql���е�ʱ�����Ͷ���
				java.sql.Timestamp newDate = new java.sql.Timestamp(time);
				// ���������õ���Ӧ��ռλ����
				pst.setTimestamp(n++, newDate);
			} else if (o == null) {
				// ����ǿ�ֵ���룬���ڶ�Ӧ��ռλ��������ֵ
				pst.setNull(n++, Types.NULL);
			} else {
				// ����������Ļ����������ͣ����ڶ�Ӧ��ռλ������Object�������
				pst.setObject(n++, o);
			}
		}
	}

	/**
	 * ͨ���Բ�ѯ���ݿ����
	 * 
	 * @param sql
	 *            ��ѯ����ַ���
	 * @param objects
	 *            ����sql��ռλ���ľ������
	 * @return ��ѯ�����
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 * @throws ClassNotFoundException
	 *             �Ҳ�����Ӧ����
	 */
	public ResultSet executeQuery(String sql, Object... objects) {
		// ������ݿ�û�����ӣ��������ݿ����Ӷ���
		ResultSet rs = null;
		try {
			if (conn == null)
				getConnection();
			// ��sql������Ԥ����
			pst = conn.prepareStatement(sql);
			// ����sql��ռλ��
			fillPrepareStatement(pst, objects);
			// ִ�в�ѯ����
			rs = pst.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * ͨ��������ɾ�������ݿ����
	 * 
	 * @param sql
	 *            ��Ӧ������ɾ���Ĳ�������ַ���
	 * @param objects
	 *            ����sql��ռλ���ľ������
	 * @return ��Ӱ�������
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 * @throws ClassNotFoundException
	 *             �Ҳ�����Ӧ����
	 */
	public int executeUpdate(String sql, Object... objects) {
		int resault = -1;
		try {
			// ������ݿ�û�����ӣ��������ݿ����Ӷ���
			if (conn == null)
				getConnection();
			// ����һ�����α��������ڱ�����Ӱ�������
			// ��sql������Ԥ����
			pst = conn.prepareStatement(sql);
			// ����sql��ռλ��
			fillPrepareStatement(pst, objects);
			// ִ�ж�Ӧ����
			resault = pst.executeUpdate();
			// �ͷ���Դ
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resault;
	}

}
