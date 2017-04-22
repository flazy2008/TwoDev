package two.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import two.common.CommonInfoByID;
import weaver.general.Util;

/**
 * 公共查询
 * 通过传入的SQL输出json 对象
 * @author  杨发权
 * @version  [版本号, 2010-11-4]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class CommonSearchManage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		CommonInfoByID common=new CommonInfoByID();
		//分页开始数字,
		String v_start = Util.null2String(request.getParameter("start"));
		//分页结束数字,
		String v_limit = Util.null2String(request.getParameter("limit"));
		String mysql = Util.null2String(request.getParameter("mysql"));
		//搜索条件
		String sqlWhere=Util.null2String(request.getParameter("sqlWhere"));
		mysql="".equals(sqlWhere)?mysql:" select * from ( "+mysql+" ) tmp1 where 1=1  "+sqlWhere;
		String v_mysql="select count(1) mycnt from ( "+mysql+" ) mysql ";
		//System.out.println(mysql);
		//System.out.println(v_mysql);
		String v_mysql2= "";
		//统计个数		
		String v_cnt=Util.null2String((String)common.executeSql(v_mysql).get("mycnt"));	
		int cnt=Util.getIntValue(v_cnt, 0);
		int start=Util.getIntValue(v_start, 0);
		int limit=Util.getIntValue(v_limit, 20);
		int end =start+limit;
		//查询数据
		v_mysql2="select * from ( select myt1.*,rownum as run from ( " + mysql + " ) myt1 ) myt2 where myt2.run>" + start
					+ " AND myt2.run<= " + end;
		//System.out.println(v_mysql2);
		//返回查询结果
		List prolist=common.getObjValue(v_mysql2);
		//分页
		Page mypage = new Page();
		mypage.setStart(start);
		mypage.setLimit(limit);
		mypage.setTotalCount(cnt);
		mypage.setDataList(prolist);
		//将分页转成json
		JSONObject obj = JSONObject.fromObject(mypage);
		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=GBK");
		out.print(obj);
		out.flush();
		out.close();
	}

}
