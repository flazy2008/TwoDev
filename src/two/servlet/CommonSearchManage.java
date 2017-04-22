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
 * ������ѯ
 * ͨ�������SQL���json ����
 * @author  �Ȩ
 * @version  [�汾��, 2010-11-4]
 * @see  [�����/����]
 * @since  [��Ʒ/ģ��汾]
 */
public class CommonSearchManage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		CommonInfoByID common=new CommonInfoByID();
		//��ҳ��ʼ����,
		String v_start = Util.null2String(request.getParameter("start"));
		//��ҳ��������,
		String v_limit = Util.null2String(request.getParameter("limit"));
		String mysql = Util.null2String(request.getParameter("mysql"));
		//��������
		String sqlWhere=Util.null2String(request.getParameter("sqlWhere"));
		mysql="".equals(sqlWhere)?mysql:" select * from ( "+mysql+" ) tmp1 where 1=1  "+sqlWhere;
		String v_mysql="select count(1) mycnt from ( "+mysql+" ) mysql ";
		//System.out.println(mysql);
		//System.out.println(v_mysql);
		String v_mysql2= "";
		//ͳ�Ƹ���		
		String v_cnt=Util.null2String((String)common.executeSql(v_mysql).get("mycnt"));	
		int cnt=Util.getIntValue(v_cnt, 0);
		int start=Util.getIntValue(v_start, 0);
		int limit=Util.getIntValue(v_limit, 20);
		int end =start+limit;
		//��ѯ����
		v_mysql2="select * from ( select myt1.*,rownum as run from ( " + mysql + " ) myt1 ) myt2 where myt2.run>" + start
					+ " AND myt2.run<= " + end;
		//System.out.println(v_mysql2);
		//���ز�ѯ���
		List prolist=common.getObjValue(v_mysql2);
		//��ҳ
		Page mypage = new Page();
		mypage.setStart(start);
		mypage.setLimit(limit);
		mypage.setTotalCount(cnt);
		mypage.setDataList(prolist);
		//����ҳת��json
		JSONObject obj = JSONObject.fromObject(mypage);
		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=GBK");
		out.print(obj);
		out.flush();
		out.close();
	}

}
