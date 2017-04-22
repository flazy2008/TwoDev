package two.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import two.common.CommonLog;
import weaver.conn.RecordSet;
import weaver.file.FileUploadToPath;
import weaver.general.Util;
import weaver.hrm.User;

/**
 * ����Excel���뵽���ݿ��Servlet
 * 
 * @author xuhongyun
 * 
 */
public class ImportExcelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		FileUploadToPath fu = new FileUploadToPath(request); // �ϴ�EXCEL�ļ�
		String filename = Util.null2String(fu.uploadFiles("importExcel")); // ��ȡEXCEL·��
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().removeAttribute("user");

		String tableName = Util.null2String(fu.getParameter("tableName"));
		String billid = Util.null2String(fu.getParameter("billid"));

		if ("".equals(filename)) {// ûѡ���ļ�
			request.getSession().setAttribute("result", "��ѡ����Ҫ�ϴ���Excel�ļ���");
			response.sendRedirect("/flazyform/Import.jsp?billid=" + billid);
		} else {
			String[][] values = readExcel(filename);// ��Excel������ά������
			String result = saveData(billid, tableName, values, user);// ��������
			if ("".equals(result)) {
				response.sendRedirect("/flazyform/GeneratePage/" + tableName
						+ "/List_View.jsp");
			} else {
				request.getSession().setAttribute("result", result);
				response.sendRedirect("/flazyform/Import.jsp?billid=" + billid);
			}

		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	/**
	 * ��ȡExcel�ļ��������ݱ��浽��ά���鲢����
	 * 
	 * @param pathname
	 *            ��Ҫ��ȡ��Excel�ļ�
	 * @return ������ݵĶ�ά����
	 */
	public String[][] readExcel(String pathname) {
		String[][] values = null;
		try {
			// ���ļ�
			Workbook book = Workbook.getWorkbook(new java.io.File(pathname));
			// ȡ�õ�һ��sheet
			Sheet sheet = book.getSheet(0);
			// ȡ������
			int rows = sheet.getRows();
			// ȡ������
			int cols = sheet.getColumns();
			values = new String[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					values[i][j] = Util.null2String(sheet.getCell(j, i).getContents()).trim();
				}
			}
			// �ر��ļ�
			book.close();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	RecordSet rs = new RecordSet();

	/**
	 * ��������
	 * 
	 * @param values
	 * @param fieldlabels
	 */
	public String saveData(String billid, String tableName, String[][] values,
			User user) {
		List<String> errorRows = null;// ���������Ϣ�ļ���
		String result = "";
		String v_sql = "";
		String fieldlabel = "";
		List<String> fieldnames = new ArrayList<String>();// ���ݿ��ֶμ���
		List<String> fieldTops = getFieldTopsForExcel(values);// Excel��ͷ����
		List<String> fieldlabels = getFieldTopsForModel(billid, fieldnames);// �õ��ֶ�����������Ҫ�������ݵı�ͷ�ļ���
		if (checkTableTop(fieldlabels, fieldTops)) {
			 errorRows = transformArray(billid, values);
			if (errorRows.size() < 1) {
				v_sql = "insert into " + tableName + "(";
				for (String s : fieldnames) {
					v_sql += s + ",";
				}
				v_sql = v_sql.substring(0, v_sql.length() - 1) + ") values(";

				for (int j = 1; j < values.length; j++) {
					for (int k = 0; k < values[j].length; k++) {
						fieldlabel += "'" + values[j][k] + "',";
					}

					fieldlabel = fieldlabel.substring(0,
							fieldlabel.length() - 1) + ")";
					insertDatabase(v_sql, fieldlabel, tableName, user);
					fieldlabel = "";
				}
			} else {
				for (String string : errorRows) {
					result += string + "\\n";
				}
			}
		} else {
			result = "����������ģ�壡��";
		}
		return result;
	}

	/**
	 * �������ݣ���¼��־
	 * 
	 * @param v_sql
	 * @param fieldlabel
	 * @param tableName
	 * @param user
	 */
	public void insertDatabase(String v_sql, String fieldlabel,
			String tableName, User user) {
		v_sql += fieldlabel;
		rs.executeSql(v_sql);
		rs.executeSql("select max(id) as mid from " + tableName);
		rs.next();
		int tableId = rs.getInt("mid");
		new CommonLog().writeLog(tableName, tableId + "", user);// ��¼��־
	}

	/**
	 * �жϱ�ͷ�Ƿ���ͬ
	 * 
	 * @return ��ͬ����true������ͬ����false
	 */
	public boolean checkTableTop(List<String> fieldlabels,
			List<String> fieldTops) {
		return fieldlabels.equals(fieldTops) ? true : false;
	}

	/**
	 * �õ�Excel�ı�ͷ
	 * 
	 * @param values
	 * @return
	 */
	public List<String> getFieldTopsForExcel(String[][] values) {
		List<String> fieldTops = new ArrayList<String>();
		for (int i = 0; i < values[0].length; i++) {
			fieldTops.add(values[0][i].trim().toUpperCase());
		}
		return fieldTops;
	}

	/**
	 * �õ�ģ���ͷ
	 * 
	 * @param billid
	 * @return ���ݿ��б������Ҫ�������ݵı�ͷ��Ϣ
	 */
	public List<String> getFieldTopsForModel(String billid,
			List<String> fieldnames) {

		List<String> fieldlabels = new ArrayList<String>();
		String v_sql = "select fieldname,fieldlabel from fla_billfield where isimp=1 and billid='"
				+ billid + "' order by viewtype,dsporder,id";
		rs.executeSql(v_sql);
		String fieldlabel = "";
		while (rs.next()) {
			fieldlabel = Util.null2String(rs.getString("fieldlabel")).toUpperCase();
			fieldnames.add(Util.null2String(rs.getString("fieldname")).toUpperCase());
			fieldlabels.add(fieldlabel);
		}
		return fieldlabels;
	}

	/**
	 * ת����ά����������ֵ
	 * 
	 * @param impsql
	 * @param n
	 * @param values
	 */
	public void transform(String impsql, int n, String[][] values,
			List<String> errorRows) {
		if (!"".equals(impsql)) {
			RecordSet recordSet = new RecordSet();
			String cellvalue = "";
			for (int i = 1; i < values.length; i++) {
				for (int j = 0; j < values[i].length; j++) {
					if (j == n) {
						cellvalue = values[i][n];
						if (!"".equals(cellvalue)) {
							recordSet.executeSql(impsql.replace("X1X", "'"
									+ cellvalue + "'"));
							if (recordSet.next()) {
								values[i][n] = Util.null2String(recordSet
										.getString(1));
							} else {
								errorRows.add("��" + (i + 1) + "�У���" + (n + 1)
										+ "�� ֵ��" + cellvalue + "��ϵͳ��û�ҵ���Ӧֵ!");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ת����ά����
	 * 
	 * @param billid
	 * @param values
	 * @return �������ݵļ���
	 */
	public List<String> transformArray(String billid, String[][] values) {
		List<String> errorRows = new ArrayList<String>();
		String v_sql = "select impsql from fla_billfield where isimp=1 and billid='"
				+ billid + "' order by viewtype,dsporder,id";
		rs.executeSql(v_sql);
		String impsql = "";
		int n = 0;
		while (rs.next()) {
			impsql = Util.null2String(rs.getString("impsql"));
			this.transform(impsql, n++, values, errorRows);
		}
		return errorRows;
	}
}
