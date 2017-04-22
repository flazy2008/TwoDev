package two.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import weaver.conn.RecordSet;
import weaver.general.Util;

/**
 * 导出EXCEL 通过传入的SQL生成EXCEL文件
 * 
 * @author Administrator
 * @version [版本号, 2010-11-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ExportExcelServlet extends HttpServlet {
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		OutputStream os = response.getOutputStream();
		String filename = Util.null2String(request.getParameter("filename"));
		String mysql = Util.null2String(request.getParameter("mysql"));
		String expfields = Util.null2String(request.getParameter("expfields"));
		String sqlWhere=Util.null2String(request.getParameter("sqlWhere"));
		mysql="".equals(sqlWhere)?mysql:" select * from ( "+mysql+" ) tmp1 where 1=1  "+sqlWhere;
		//System.out.println(mysql);
		filename = "".equals(filename) ? "temp001" : filename;
		RecordSet rs = new RecordSet();
		try {
			rs.executeSql(mysql.toUpperCase());
			List list = Arrays.asList(rs.getColumnName());
			if (!"".equals(expfields)) {//前台传入值后
				list = Arrays.asList(expfields.split(","));
			}
			Iterator it = list.iterator();
			String v_field = "";
			int row = 0;
			int col = 0;
			WritableWorkbook wbook = Workbook.createWorkbook(os);
			WritableSheet wsheet = wbook.createSheet(filename, 0);
			Label label = null;
			while (it.hasNext()) {
				v_field = (String) it.next();
				label = new Label(col, 0, v_field);
				wsheet.addCell(label);
				col++;
			}
			String fields = "";
			String values = "";
			row = 1;// 行
			while (rs.next()) {
				Iterator ir = list.iterator();
				fields = "";
				values = "";
				col = 0;// 列
				while (ir.hasNext()) {
					fields = (String) ir.next();
					values = Util.null2String(rs.getString(fields));
					label = new Label(col, row, values);// Label(列号,行号 ,内容 )
					wsheet.addCell(label);
					col++;
				}
				row++;
			}
			response.setHeader("Content-disposition", "attachment;"
					+ "filename="
					+ new String(filename.getBytes("GBK"), "ISO_8859_1")
					+ ".xls");
			response.setContentType("application/vnd.ms-excel");
			wbook.write();
			wbook.close();
			os.flush();
			os.close();
		} catch (Exception e) {
		}
	}
}
