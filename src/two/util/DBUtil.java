package two.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * JDBC�����ݿ�ͨ���Բ����ķ�װ�� ͨ�����࣬���Ի�����ݿ����Ӷ���Ͷ����ݿ���ɾ�Ĳ��ͨ���Բ���
 * 
 * 
 * @author xuhongyun
 * 
 */
public class DBUtil {
	/** ���������� */
	private final String CLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/** JDBC����·�� */
	private final String SQLURL = "jdbc:sqlserver://192.168.0.73:1433;DatabaseName=ZKTime";

	/** ���ݿ��û��� */
	private final String USER = "sa";

	/** ���ݿ��û����� */
	private final String PASSWORD = "1";

	/** JDBC���Ӷ��� */
	private Connection con;

	/** Ԥ����� SQL ���Ķ��� */
	private PreparedStatement pst;

	/**
	 * �������ݿ����Ӷ���
	 * 
	 * @return JDBC���Ӷ���
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 * @throws ClassNotFoundException
	 *             �Ҳ�����Ӧ����
	 */
	public Connection getConnection() throws SQLException,
			ClassNotFoundException {
		// ͨ���������������
		Class.forName(CLASSNAME);
		// ͨ��DriverManager��ľ�̬�����������ݿ����Ӷ���
		con = DriverManager.getConnection(SQLURL, USER, PASSWORD);
		return con;

	}

	/**
	 * �ͷ���Դ���رղ�ѯ��������ر�Ԥ�����SQL���Ķ����JDBC���Ӷ���
	 * 
	 * @param rs
	 *            ��ѯ�����
	 * @throws SQLException
	 *             ���ݿ����Ӵ���
	 */
	public void close(ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (pst != null)
			pst.close();
		// �����Ӷ���Ϊ����û�б��ر�ʱ���ر����Ӷ���
		if (con != null && !con.isClosed())
			con.close();
		// ��ն�Ӧ���ݣ�������´β�����Ӱ��
		pst = null;
		con = null;

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
	public ResultSet executeQuery(String sql, Object... objects)
			throws SQLException, ClassNotFoundException {
		// ������ݿ�û�����ӣ��������ݿ����Ӷ���
		if (con == null)
			getConnection();
		// ��sql������Ԥ����
		pst = con.prepareStatement(sql);
		// ����sql��ռλ��
		fillPrepareStatement(pst, objects);
		// ִ�в�ѯ����
		return pst.executeQuery();
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
	public int executeUpdate(String sql, Object... objects)
			throws SQLException, ClassNotFoundException {
		// ������ݿ�û�����ӣ��������ݿ����Ӷ���
		if (con == null)
			getConnection();
		// ����һ�����α��������ڱ�����Ӱ�������
		int resault = -1;
		// ��sql������Ԥ����
		pst = con.prepareStatement(sql);
		// ����sql��ռλ��
		fillPrepareStatement(pst, objects);
		// ִ�ж�Ӧ����
		resault = pst.executeUpdate();
		// �ͷ���Դ
		close();
		return resault;
	}

}
