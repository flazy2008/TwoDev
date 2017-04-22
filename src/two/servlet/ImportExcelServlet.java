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
 * 处理Excel导入到数据库的Servlet
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
		FileUploadToPath fu = new FileUploadToPath(request); // 上传EXCEL文件
		String filename = Util.null2String(fu.uploadFiles("importExcel")); // 获取EXCEL路径
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().removeAttribute("user");

		String tableName = Util.null2String(fu.getParameter("tableName"));
		String billid = Util.null2String(fu.getParameter("billid"));

		if ("".equals(filename)) {// 没选择文件
			request.getSession().setAttribute("result", "请选择需要上传的Excel文件！");
			response.sendRedirect("/flazyform/Import.jsp?billid=" + billid);
		} else {
			String[][] values = readExcel(filename);// 将Excel读到二维数组中
			String result = saveData(billid, tableName, values, user);// 保存数据
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
	 * 读取Excel文件，将数据保存到二维数组并返回
	 * 
	 * @param pathname
	 *            将要读取的Excel文件
	 * @return 存放数据的二维数组
	 */
	public String[][] readExcel(String pathname) {
		String[][] values = null;
		try {
			// 打开文件
			Workbook book = Workbook.getWorkbook(new java.io.File(pathname));
			// 取得第一个sheet
			Sheet sheet = book.getSheet(0);
			// 取得行数
			int rows = sheet.getRows();
			// 取得列数
			int cols = sheet.getColumns();
			values = new String[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					values[i][j] = Util.null2String(sheet.getCell(j, i).getContents()).trim();
				}
			}
			// 关闭文件
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
	 * 保存数据
	 * 
	 * @param values
	 * @param fieldlabels
	 */
	public String saveData(String billid, String tableName, String[][] values,
			User user) {
		List<String> errorRows = null;// 保存错误信息的集合
		String result = "";
		String v_sql = "";
		String fieldlabel = "";
		List<String> fieldnames = new ArrayList<String>();// 数据库字段集合
		List<String> fieldTops = getFieldTopsForExcel(values);// Excel表头集合
		List<String> fieldlabels = getFieldTopsForModel(billid, fieldnames);// 得到字段名并返回需要导入数据的表头的集合
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
			result = "请下载最新模板！！";
		}
		return result;
	}

	/**
	 * 存入数据，记录日志
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
		new CommonLog().writeLog(tableName, tableId + "", user);// 记录日志
	}

	/**
	 * 判断表头是否相同
	 * 
	 * @return 相同返回true，不相同返回false
	 */
	public boolean checkTableTop(List<String> fieldlabels,
			List<String> fieldTops) {
		return fieldlabels.equals(fieldTops) ? true : false;
	}

	/**
	 * 得到Excel的表头
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
	 * 得到模板表头
	 * 
	 * @param billid
	 * @return 数据库中保存的需要导入数据的表头信息
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
	 * 转换二维数组中特殊值
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
								errorRows.add("第" + (i + 1) + "行，第" + (n + 1)
										+ "列 值：" + cellvalue + "在系统中没找到对应值!");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 转换二维数组
	 * 
	 * @param billid
	 * @param values
	 * @return 错误数据的集合
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
