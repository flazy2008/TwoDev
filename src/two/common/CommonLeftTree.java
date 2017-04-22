package two.common;

import weaver.common.util.xtree.TreeNode;
import weaver.conn.RecordSet;
import weaver.general.Util;
public class CommonLeftTree {
	public TreeNode getTreeNode(TreeNode treeNode,String pid,String tableName){
		TreeNode childNode =null;
		RecordSet rs=new RecordSet();
		String v_id="";
		String v_pid="";
		String v_labelname="";
		String v_isleaf="";
		
		String nodeId="";
		String v_sql="select * from "+tableName+" where  pid='"+pid+"' order by isleaf";
		rs.executeSql(v_sql);
		while(rs.next()){
			v_id = Util.null2String(rs.getString("id"));//合同编号
			v_pid = Util.null2String(rs.getString("pid"));//上级id
			v_labelname = Util.null2String(rs.getString("labelname"));//标签名称
			v_isleaf= Util.null2String(rs.getString("isleaf"));
			nodeId="tree_" + v_id;
			childNode = new TreeNode();
			childNode.setValue(v_id);
			childNode.setTitle(v_labelname);
			childNode.setNodeId(nodeId);
			childNode.setIcon("/images/treeimages/Home.gif");
			childNode.setNodeXmlSrc("/flazyform/commontree/CommonLeftTree_XML.jsp?id="+ v_id + "&tableName="+tableName+"&nodeid=" +nodeId);
			childNode.setHref("javascript:setTreeNode('" +nodeId+ "')");
			childNode.setTarget("_self");
			if("1".equals(v_isleaf)){
				childNode.setIcon("/images/treeimages/dept16.gif");
				childNode.setHref("javascript:setTreeNode('leaf_" +nodeId+ "')");
				childNode.setNodeXmlSrc(null);
			}
			treeNode.addTreeNode(childNode);
		}
		return treeNode;
	}
}
