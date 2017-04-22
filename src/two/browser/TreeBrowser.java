package two.browser;

import two.common.CommonInfoByID;
import weaver.conn.RecordSet;
import weaver.general.Util;

public class TreeBrowser {
	private static final String PATH = "/flazyform/param/tree/ChildrenNode.jsp";
	
	/**   
	 * @param sql
	 * @param pid
	 * @param ctype分4种情况，“1”表示所有节点复选；“2”表示所有节点单选；“3”表示叶子节点复选；“4”表示叶子节点单选(默认)
	 * @return
	 */
	public String getNodeByParentId(String sql,String pid,String ctype) {
		String v_sql = sql+" and pid='"+pid+"' order by id ";
		RecordSet recordSet = new RecordSet();
		String v_content = "";      
		String v_tmp = "tree.add(new WebFXLoadTreeItem('XNAMEX','"+PATH+"?pamar="+sql+":XIDX:"+ctype+"','','','/images/treeimages/Home.gif','/images/treeimages/Home.gif'));";
		if("1".equals(ctype)){//所有节点复选
			v_tmp = "tree.add(new WebFXLoadCheckBoxTreeItem('XNAMEX','XIDX','"+PATH+"?pamar="+sql+":XIDX:"+ctype+"','','/images/treeimages/Home.gif','/images/treeimages/Home.gif','',''));";
		}else if("2".equals(ctype)){//所有节点单选
			v_tmp = "tree.add(new WebFXLoadRadioTreeItem('XNAMEX','XIDX','"+PATH+"?pamar="+sql+":XIDX:"+ctype+"','','/images/treeimages/Home.gif','/images/treeimages/Home.gif','',''));";
		}                                               
		String v_id = "";
		String v_name = "";
		recordSet.executeSql(v_sql);
		while (recordSet.next()) {
			v_id = Util.null2String(recordSet.getString("id"));
			v_name = Util.null2String(recordSet.getString("name"));
			v_content += v_tmp.replace("XNAMEX", v_name).replace("XIDX", v_id);
		}
		return v_content;
	}

	public String getChildrenByParentId(String sql,String pid,String ctype) {
		String v_sql = sql+" and pid='"+pid+"' order by id ";
		CommonInfoByID infoByID = new CommonInfoByID();
		RecordSet recordSet = new RecordSet();
		String v_content = "";
		
		
		String v_tmp = "";
		String v_name = "";
		String v_id = "";
		String v_cnt = "";
		recordSet.executeSql(v_sql);
		while (recordSet.next()) {
			v_name = recordSet.getString("name");
			v_id = recordSet.getString("id");
			v_cnt =Util.null2String((String)infoByID.executeSql("select count(id) as cnt from ("+sql+") t2 where pid="+v_id).get("cnt"));
			if("0".equals(v_cnt)){
				v_tmp = "<tree text='XNAMEX'  XTYPEX icon='/images/treeimages/friend.gif'  openIcon='/images/treeimages/friend.gif' value='XIDX' id='XIDX'/>";
				if("2".equals(ctype)||"4".equals(ctype)){
					v_tmp = v_tmp.replace("XTYPEX", "type='radio'");
				}else if("1".equals(ctype)||"3".equals(ctype)){
					v_tmp = v_tmp.replace("XTYPEX", "type='check'");
				}
			}else{
				v_tmp = "<tree text='XNAMEX' XTYPEX src='"+PATH+"?pamar="+sql+":XIDX:"+ctype+"' icon='/images/treeimages/friend.gif' openIcon='/images/treeimages/friend.gif' value='XIDX' id='XIDX'/>";
				if("1".equals(ctype)){
					v_tmp = v_tmp.replace("XTYPEX", "type='check'");
				}else if("2".equals(ctype)){
					v_tmp = v_tmp.replace("XTYPEX", "type='radio'");
				}else{
					v_tmp = v_tmp.replace("XTYPEX", "");
				}
			}
			v_content += v_tmp.replaceAll("XNAMEX", v_name).replaceAll("XIDX",v_id);
		}
		return v_content;
	}
}
