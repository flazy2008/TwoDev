package weaver.workflow.html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import weaver.system.code.CodeBuild;
import weaver.system.code.CoderBean;
import weaver.systeminfo.SystemEnv;
import weaver.workflow.datainput.DynamicDataInput;
import weaver.workflow.field.DetailFieldComInfo;
import weaver.workflow.field.FieldComInfo;
import weaver.workflow.field.FieldTypeComInfo;
import weaver.workflow.field.HtmlElement;
import weaver.workflow.field.SpecialFieldInfo;
import weaver.workflow.field.SystemElement;
import weaver.workflow.mode.FieldInfo;
import weaver.workflow.request.RequestDoc;
import weaver.workflow.request.RequestPreAddinoperateManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import weaver.workflow.workflow.WFNodeDtlFieldManager;
import weaver.workflow.workflow.WFNodeFieldManager;
import weaver.workflow.workflow.WfLinkageInfo;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;

public class WFLayoutToHtml extends BaseBean{

	private HttpServletRequest request;
	private User user;
	private int iscreate;
	private String wfformhtml;//解析后的表单
	private String htmlLayout;//模板
	private StringBuffer jsStr;//放javascript代码
	private StringBuffer vbsStr;//放vbs代码
	private Hashtable otherPara_hs;
	private String needcheck;//必填检查用
	private StringBuffer htmlHiddenElementsb;//隐藏的input

	private String billtablename;//单据主表表名

	public WFLayoutToHtml(){
		wfformhtml = "";
		htmlLayout = "";
		jsStr = new StringBuffer();
		vbsStr = new StringBuffer();
		iscreate = 0;
		otherPara_hs = new Hashtable();
		needcheck = "";
		htmlHiddenElementsb = new StringBuffer();
		billtablename = "";
	}

	/***
	 * 被页面调用的解析显示模板的方法。具体事件调用其它方法处理
	 * @return
	 */
	public Hashtable analyzeLayout(){
		Hashtable ret_hs = new Hashtable();
		try{
			RecordSet rs = new RecordSet();
			WFNodeFieldManager wFNodeFieldManager = new WFNodeFieldManager();
			int modeid = Util.getIntValue(request.getParameter("modeid"), 0);
			String syspath = "";
			rs.execute("select * from workflow_nodehtmllayout where id="+modeid);
			if(rs.next()){
				syspath = Util.null2String(rs.getString("syspath"));
			}
			htmlLayout = wFNodeFieldManager.readHtmlFile(syspath);//模板
			//解析模板，替换字段和节点意见
			analyzeLayoutElement();
			
			ret_hs.put("wfformhtml", wfformhtml);
		}catch(Exception e){
			writeLog(e);
		}
		return ret_hs;
	}

	public void analyzeLayoutElement(){
		analyzeFormSplitPage();
		getMainTableElement();
		getNodeRemark();
		getDetailTableElement();
	}

	/**
	 * 解析表单分tab页
	 * 如果有分tab的情况，则必须在流程表单的最开始出设置第一个tab标签，解析时，从第一个标签的位置开始设置tab页
	 */
	public void analyzeFormSplitPage(){
		//<div class="formSplitPage"
		wfformhtml = htmlLayout;
		int pos = htmlLayout.toLowerCase().indexOf("formsplitpage");
		if(pos > -1){//需要分tab页
			try{
				StringBuffer tabHead_sb = new StringBuffer();//流程页面内tab的头，最后加在模板文件前面
				tabHead_sb.append("\n").append("<table id=\"tabHeadDiv\" name=\"tabHeadDiv\" class=\"tabHeadDiv\" cellspacing=\"0\"><tr>");
				int tabCount = 0;
				String content1 = "";
				String content2 = "";
				while(pos > -1){
					int lastDiv1 = htmlLayout.toLowerCase().lastIndexOf("<div", pos);//找到的formSplitPage之前的第一个<div
					int firstDiv2 = htmlLayout.toLowerCase().indexOf("</div>", pos);//找到的formSplitPage之后的第一个</div>
					content1 = wfformhtml.substring(0, lastDiv1);
					content2 = wfformhtml.substring(firstDiv2+6);
					String nameStr = wfformhtml.substring(lastDiv1+4, firstDiv2);
					String divStr = "";
					if(tabCount > 0){//不是第一个tab，需要终结前面的div
						divStr = "</div>\n";
					}
					divStr += "<div id=\"formsplitdiv"+tabCount+"\" name=\"formsplitdiv"+tabCount+"\" class=\"formSplitDiv\" ";
					String classStr = "In";
					if(tabCount > 0){
						divStr += " style=\"display:none\" ";//不是第一个，就隐藏掉
						classStr = "Out";//不是第一个，就设置未选中class
					}
					divStr += " >";
					wfformhtml = content1 + divStr + content2;
					htmlLayout = wfformhtml;
					//处理tab页名称
					int spanindex1 = nameStr.toLowerCase().indexOf(">");
					String name = "";
					if(spanindex1>-1){
						name = nameStr.substring(spanindex1+1);
					}
					if("".equals(name)){
						name = (SystemEnv.getHtmlLabelName(23825,user.getLanguage()) + "&nbsp;" + (tabCount+1));
					}
					byte[] b = name.getBytes();
					tabHead_sb.append("<td id=\"formsplitspan"+tabCount+"\" name=\"formsplitspan"+tabCount+"\" class=\"formSplitSpan"+classStr+"\" style=\"width:"+(b.length*12+10)+"px\" onclick=\"javascript:changeFormSplitPage("+tabCount+");\">&nbsp;&nbsp;&nbsp;"+name+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
					//tabHead_sb.append("<span name=\"borderformsplitspan\" class=\"borderFormSplitSpan\" ></span>");
					tabCount++;
					pos = htmlLayout.toLowerCase().indexOf("formsplitpage");
				}
				tabHead_sb.append("<td name=\"lastformsplitspan\" class=\"lastFormSplitSpan\" >&nbsp;</td>");
				tabHead_sb.append("</tr></table>").append("\n").append(wfformhtml).append("</div>");
				wfformhtml = tabHead_sb.toString();
				htmlLayout = wfformhtml;
			}catch(Exception e){
				writeLog(e);
			}
		}
	}

	/**
	 * 处理主子段的字段转化，同时处理明细字段的显示名转化
	 */
	public void getMainTableElement(){
		try{
			String sql = "";
			RecordSet rs = new RecordSet();
			FieldComInfo fieldComInfo = new FieldComInfo();
			FieldTypeComInfo fieldTypeComInfo = new FieldTypeComInfo();
			ResourceComInfo resourceComInfo = new ResourceComInfo();
			WfLinkageInfo wfLinkageInfo = new WfLinkageInfo();
			SystemElement systemElement = new SystemElement();
			int requestid = Util.getIntValue(request.getParameter("requestid"), 0);
			int billid = Util.getIntValue(request.getParameter("billid"), 0);
			int workflowid = Util.getIntValue(request.getParameter("workflowid"), 0);
			int nodeid = Util.getIntValue(request.getParameter("nodeid"), 0);
			int nodetype = Util.getIntValue(request.getParameter("nodetype"), 0);//创建页面正好不传过去，所以是0
			int isbill = Util.getIntValue(request.getParameter("isbill"), 0);
			int formid = Util.getIntValue(request.getParameter("formid"), 0);
			int isremark = Util.getIntValue(request.getParameter("isremark"), 0);
			int isprint = Util.getIntValue(request.getParameter("isprint"), 0);
			String canDelAcc="";
			rs.executeSql("select candelacc from workflow_base where id="+workflowid);
			if(rs.next()){
				canDelAcc = Util.null2String(rs.getString("candelacc"));
			}
			String docCategory = "";
			rs.executeSql("select b.docCategory from workflow_requestbase a,workflow_base b where a.workflowid=b.id and a.requestid="+requestid );
			if (rs.next()) {
				docCategory= rs.getString("docCategory");
			}
			int currentnodetype = -1;
			rs.executeProc("workflow_Requestbase_SByID", ""+requestid);
			if(rs.next()){
				currentnodetype = Util.getIntValue(rs.getString("currentnodetype"), 0);
			}
			otherPara_hs.put("workflowid", ""+workflowid);
			otherPara_hs.put("isremark", ""+isremark);
			otherPara_hs.put("nodeid", ""+nodeid);
			otherPara_hs.put("isbill", ""+isbill);
			otherPara_hs.put("nodetype", ""+nodetype);
			otherPara_hs.put("canDelAcc", canDelAcc);
			otherPara_hs.put("docCategory", docCategory);
			otherPara_hs.put("iscreate", ""+iscreate);
			otherPara_hs.put("isprint", ""+isprint);

			ArrayList fieldidList = new ArrayList();//表单的所有主表字段列表
			ArrayList detailFieldidList = new ArrayList();//表单的所有明细表字段列表
			ArrayList fieldhtmltypeList = new ArrayList();			//字段的htmltype队列
			ArrayList fieldtypeList = new ArrayList();				//字段的type队列
			ArrayList fielddbtypeList = new ArrayList();			//字段的数据库字段类型队列
			Hashtable fieldname_hs = new Hashtable();//表字段在数据库的字段名字
			Hashtable isview_hs = new Hashtable();//是否显示
			Hashtable isedit_hs = new Hashtable();//是否可编辑
			Hashtable ismand_hs = new Hashtable();//是否必填
			Hashtable fieldlabel_hs = new Hashtable();//字段的显示名。这个显示名现在只用在temptitle里，用于必填提示
			Hashtable fieldvalue_hs = new Hashtable();//字段的值。如果是创建流程时，则为空
			if(isbill == 0){
				sql = "select nf.*, ff.isdetail, fl.fieldlable, ff.groupid, '' as fieldname from workflow_nodeform nf left join workflow_formfield ff on nf.fieldid=ff.fieldid and ff.formid="+formid+" left join workflow_fieldlable fl on fl.fieldid=nf.fieldid and fl.formid="+formid+" and fl.langurageid="+user.getLanguage()+" where nf.nodeid="+nodeid+" order by nf.orderid, ff.fieldorder";
			}else if(isbill == 1){
				//单据的明细字段信息先拿出来，具体分组以后再查数据库
				sql = "select nf.*, bf.viewtype as isdetail, bf.fieldlabel as fieldlable, detailtable as groupid, bf.fieldname, bf.fieldhtmltype, bf.type, bf.fielddbtype,bf.textheight,bf.imgheight,bf.imgwidth from workflow_nodeform nf left join workflow_billfield bf on nf.fieldid=bf.id and bf.billid="+formid+" where nf.nodeid="+nodeid+" order by nf.orderid, bf.dsporder";
			}
			rs.execute(sql);
			while(rs.next()){
				int isdetail_tmp = Util.getIntValue(rs.getString("isdetail"), 0);
				int fieldid_tmp = Util.getIntValue(rs.getString("fieldid"), 0);
				int isview_tmp = Util.getIntValue(rs.getString("isview"), 0);
				int isedit_tmp = Util.getIntValue(rs.getString("isedit"), 0);
				int ismand_tmp = Util.getIntValue(rs.getString("ismandatory"), 0);
				String fieldname = Util.null2String(rs.getString("fieldname"));
				if(isbill == 0){
					fieldname = fieldComInfo.getFieldname(""+fieldid_tmp);
				}
				String labelName_tmp = "";
				if(isbill==0){
					labelName_tmp = Util.null2String(rs.getString("fieldlable"));
				}else{
					int labelid_tmp = Util.getIntValue(rs.getString("fieldlable"));
					labelName_tmp = SystemEnv.getHtmlLabelName(labelid_tmp, user.getLanguage());
				}
				//流程名称、紧急程度、短信情况特殊处理
				if(fieldid_tmp == -1){
					labelName_tmp = SystemEnv.getHtmlLabelName(21192, user.getLanguage());
				}else if(fieldid_tmp == -2){
					labelName_tmp = SystemEnv.getHtmlLabelName(15534, user.getLanguage());
				}else if(fieldid_tmp == -3){
					labelName_tmp = SystemEnv.getHtmlLabelName(17586, user.getLanguage());
				}
				labelName_tmp = Util.toScreenForWorkflow(labelName_tmp);
				if(isdetail_tmp == 0){
					fieldidList.add(""+fieldid_tmp);
					int htmltype_tmp = 0;
					int type_tmp = 0;
					String dbtype_tmp = "";
					if(isbill == 0){
						htmltype_tmp = Util.getIntValue(fieldComInfo.getFieldhtmltype(""+fieldid_tmp), 0);
						type_tmp = Util.getIntValue(fieldComInfo.getFieldType(""+fieldid_tmp), 0);
						dbtype_tmp = Util.null2String(fieldComInfo.getFielddbtype(""+fieldid_tmp));
					}else{
						htmltype_tmp = Util.getIntValue(rs.getString("fieldhtmltype"), 0);
						type_tmp = Util.getIntValue(rs.getString("type"), 0);
						dbtype_tmp = Util.null2String(rs.getString("fielddbtype"));
					}
					fieldhtmltypeList.add(""+htmltype_tmp);
					fieldtypeList.add(""+type_tmp);
					fielddbtypeList.add(dbtype_tmp);
					if(htmltype_tmp==6){
						if(isbill == 0){
							otherPara_hs.put("fieldimgwidth"+fieldid_tmp,""+fieldComInfo.getImgWidth(""+fieldid_tmp));
							otherPara_hs.put("fieldimgheight"+fieldid_tmp,""+fieldComInfo.getImgHeight(""+fieldid_tmp));
							otherPara_hs.put("fieldimgnum"+fieldid_tmp,""+fieldComInfo.getImgNumPreRow(""+fieldid_tmp));
						}else{
							otherPara_hs.put("fieldimgwidth"+fieldid_tmp,""+Util.getIntValue(rs.getString("imgwidth"), 0));
							otherPara_hs.put("fieldimgheight"+fieldid_tmp,""+Util.getIntValue(rs.getString("imgheight"), 0));
							otherPara_hs.put("fieldimgnum"+fieldid_tmp,""+Util.getIntValue(rs.getString("textheight"), 0));
						}
					}
				}else{
					detailFieldidList.add(""+fieldid_tmp);
				}
				fieldname_hs.put("fieldname"+fieldid_tmp, fieldname);
				isview_hs.put("isview"+fieldid_tmp, ""+isview_tmp);
				isedit_hs.put("isedit"+fieldid_tmp, ""+isedit_tmp);
				ismand_hs.put("ismand"+fieldid_tmp, ""+ismand_tmp);
				fieldlabel_hs.put("fieldlabel"+fieldid_tmp, labelName_tmp);
			}
			//把主、明细字段队列也放进去，在单个实现页面字段的JAVA类中，需要判断其他字段的主或明细属性
			otherPara_hs.put("fieldidList", fieldidList);
			otherPara_hs.put("detailFieldidList", detailFieldidList);
			otherPara_hs.put("fieldtypeList", fieldtypeList);
			otherPara_hs.put("isedit_hs", isedit_hs);

			Hashtable inoperatefield_hs = new Hashtable();
			String prjid = Util.null2String(request.getParameter("prjid"));
			String reqid = Util.null2String(request.getParameter("reqid"));
			String docid = Util.null2String(request.getParameter("docid"));
			String hrmid = Util.null2String(request.getParameter("hrmid"));
			String crmid = Util.null2String(request.getParameter("crmid"));
			if(iscreate == 1){
				//获取节点前附加操作
				RequestPreAddinoperateManager requestPreAddM = new RequestPreAddinoperateManager();
				requestPreAddM.setCreater(user.getUID());
				requestPreAddM.setOptor(user.getUID());
				requestPreAddM.setWorkflowid(workflowid);
				requestPreAddM.setNodeid(nodeid);
				Hashtable getPreAddRule_hs = requestPreAddM.getPreAddRule();
				inoperatefield_hs = (Hashtable)getPreAddRule_hs.get("inoperatefield_hs");
				fieldvalue_hs = (Hashtable)getPreAddRule_hs.get("inoperatevalue_hs");
			}else{
				//查每个字段的值
				if(isbill == 0){
					rs.executeProc("workflow_FieldValue_Select",""+requestid);	   // 从workflow_form表中查
					rs.next();
					for(int i=0; i<fieldidList.size(); i++){
						int fieldid_tmp = Util.getIntValue((String)fieldidList.get(i));
						if(fieldid_tmp <= 0){
							continue;
						}
						String fieldname = Util.null2String((String)fieldname_hs.get("fieldname"+fieldid_tmp));
						fieldvalue_hs.put("inoperatevalue"+fieldid_tmp, Util.null2String(rs.getString(fieldname)));//这里为了和节点前附加操作的一样，所以用“inoperatevalue”作为Key
					}
				}else{
					rs.executeSql("select tablename from workflow_bill where id = " + formid);//查询工作流单据表的信息
					rs.next();
					billtablename = rs.getString("tablename");
					rs.executeSql("select * from " + billtablename + " where id = " + billid);
					if(rs.next()){
						for(int i=0; i<fieldidList.size(); i++){
							int fieldid_tmp = Util.getIntValue((String)fieldidList.get(i));
							if(fieldid_tmp <= 0){
								continue;
							}
							String fieldname = Util.null2String((String)fieldname_hs.get("fieldname"+fieldid_tmp));
							fieldvalue_hs.put("inoperatevalue"+fieldid_tmp, Util.null2String(rs.getString(fieldname)));
						}
					}
				}
				rs.execute("select requestname, requestlevel, messagetype from workflow_requestbase where requestid="+requestid);
				if(rs.next()){
					String requestname_tmp = Util.null2String(rs.getString("requestname"));
					fieldvalue_hs.put("inoperatevalue-1", requestname_tmp);
					int requestlevel_tmp = Util.getIntValue(rs.getString("requestlevel"), 0);
					fieldvalue_hs.put("inoperatevalue-2", ""+requestlevel_tmp);
					int messagetype_tmp = Util.getIntValue(rs.getString("messagetype"), 0);
					fieldvalue_hs.put("inoperatevalue-3", ""+messagetype_tmp);
				}
			}

			SpecialFieldInfo specialFieldInfo = new SpecialFieldInfo();
			RequestDoc requestDoc = new RequestDoc();
			HashMap specialfield = specialFieldInfo.getFormSpecialField();//特殊字段的字段信息
			otherPara_hs.put("specialfield", specialfield);
			
			int creater= Util.getIntValue(request.getParameter("creater"),0);
			int creatertype=Util.getIntValue(request.getParameter("creatertype"),0);
			String currentdate = Util.null2String(request.getParameter("currentdate"));
			String currenttime = Util.null2String(request.getParameter("currenttime"));
			
			CodeBuild cbuild = new CodeBuild(formid);
			//CodeBuild cbuild = new CodeBuild(formid,""+isbill,workflowid,creater,creatertype);
			CoderBean cb = cbuild.getFlowCBuild();
			String isUse = cb.getUserUse();  //是否使用流程编号
			String fieldCode=Util.null2String(cb.getCodeFieldId());
			ArrayList memberList = cb.getMemberList();			
			boolean hasHistoryCode=cbuild.hasHistoryCode(rs,workflowid);	
			int fieldIdSelect=-1;
			int departmentFieldId=-1;
			int subCompanyFieldId=-1;
			int supSubCompanyFieldId=-1;
			int yearFieldId=-1;
			int yearFieldHtmlType = 0;
			int monthFieldId=-1;
			int dateFieldId=-1;

			for (int i=0;i<memberList.size();i++){
				String[] codeMembers = (String[])memberList.get(i);
				String codeMemberName = codeMembers[0];
				String codeMemberValue = codeMembers[1];
				if("22755".equals(codeMemberName)){
					fieldIdSelect=Util.getIntValue(codeMemberValue,-1);
				}else if("22753".equals(codeMemberName)){
					supSubCompanyFieldId=Util.getIntValue(codeMemberValue,-1);
				}else if("141".equals(codeMemberName)){
					subCompanyFieldId=Util.getIntValue(codeMemberValue,-1);
				}else if("124".equals(codeMemberName)){
					departmentFieldId=Util.getIntValue(codeMemberValue,-1);
				}else if("445".equals(codeMemberName)){
					yearFieldId=Util.getIntValue(codeMemberValue,-1);
				}else if("6076".equals(codeMemberName)){
					monthFieldId=Util.getIntValue(codeMemberValue,-1);
				}else if("390".equals(codeMemberName)||"16889".equals(codeMemberName)){
					dateFieldId=Util.getIntValue(codeMemberValue,-1);
				}
			}
			
			String codeFields = Util.null2String(cbuild.haveCode());
			otherPara_hs.put("codeFields", codeFields);
			
			otherPara_hs.put("isUse", isUse);
			otherPara_hs.put("fieldCode", fieldCode);
			otherPara_hs.put("hasHistoryCode", String.valueOf(hasHistoryCode));						
			
			ArrayList flowDocs = requestDoc.getDocFiled(""+workflowid); //得到流程建文挡的发文号字段
			String codeField = "";
			if (flowDocs!=null && flowDocs.size()>0){
				codeField = ""+flowDocs.get(0);
			}
			otherPara_hs.put("codeField", codeField);
			int keywordismand = 0;
			int keywordisedit = 0;
			int titleFieldId=0;
			int keywordFieldId=0;
			String workflowname = "";
			int messageType = 0;
			rs.execute("select titleFieldId,keywordFieldId,workflowname,messageType from workflow_base where id="+workflowid);
			if(rs.next()){
				titleFieldId = Util.getIntValue(rs.getString("titleFieldId"),0);
				keywordFieldId = Util.getIntValue(rs.getString("keywordFieldId"),0);
				workflowname = Util.null2String(rs.getString("workflowname"));
				messageType = Util.getIntValue(rs.getString("messageType"), 0);
			}
			otherPara_hs.put("titleFieldId", ""+titleFieldId);
			otherPara_hs.put("keywordFieldId", ""+keywordFieldId);

			int defaultName = Util.getIntValue(request.getParameter("defaultName"), 0);

			HttpSession session = (HttpSession)request.getSession(false);
			User user = (User)request.getSession(true).getAttribute("weaver_user@bean");
			int isaffirmancebody = Util.getIntValue((String)session.getAttribute(user.getUID()+"_"+requestid+"isaffirmance"), 0);//是否需要提交确认
			int reEditbody = Util.getIntValue((String)session.getAttribute(user.getUID()+"_"+requestid+"reEdit"), 0);//是否需要提交确认
			int isviewonly = 0;
			if(isremark!=0 || nodetype==3 || isprint==1){
				isviewonly = 1;
			}
			int mustNoEdit = 0;
			if(isaffirmancebody==1 && reEditbody==0){
				mustNoEdit = 1;
				isviewonly = 0;
			}
			otherPara_hs.put("isviewonly", ""+isviewonly);
			otherPara_hs.put("mustNoEdit", ""+mustNoEdit);
			//获得触发字段名
			DynamicDataInput ddi = new DynamicDataInput(""+workflowid);
			String trrigerfield = ddi.GetEntryTriggerFieldName();
			ArrayList selfieldsadd = wfLinkageInfo.getSelectField(workflowid, nodeid, 0);
			ArrayList changefieldsadd = wfLinkageInfo.getChangeField(workflowid, nodeid, 0);
			otherPara_hs.put("trrigerfield", trrigerfield);
			otherPara_hs.put("selfieldsadd", selfieldsadd);
			otherPara_hs.put("changefieldsadd", changefieldsadd);

			getFieldAttr();//取出字段取值属性，放到otherPara_hs里，同时拼必要的页面JS方法
			//在这里先把明细字段的标签都处理掉
			for(int i=0; i<detailFieldidList.size(); i++){
				int fieldid_tmp = Util.getIntValue((String)detailFieldidList.get(i));
				//替换字段显示名。这里如果找到就替换，如果找不到就不处理
				String fieldlabel_tmp = Util.null2String((String)fieldlabel_hs.get("fieldlabel"+fieldid_tmp));
				String content1 = "";
				String content2 = "";
				int pos = htmlLayout.toLowerCase().indexOf("$label"+fieldid_tmp+"$");
				while(pos > -1){
					content1 = wfformhtml.substring(0, pos);
					content2 = wfformhtml.substring(pos+1);
					int pos1 = content1.lastIndexOf("<");
					int pos2 = content2.indexOf(">");
					if(pos1 > -1){
						content1 = content1.substring(0, pos1);
					}
					if(pos2 > -1){
						content2 = content2.substring(pos2+1);
					}
					wfformhtml = content1 + fieldlabel_tmp + content2;
					htmlLayout = wfformhtml;
					pos = htmlLayout.toLowerCase().indexOf("$label"+fieldid_tmp+"$");
				}
			}
			ArrayList fckfieldidList = new ArrayList();
			String newfromdate = "a";
			String newenddate = "b";
			//开始根据字段id队列循环解析
			HtmlElement object = null;
			for(int i=0; i<fieldidList.size(); i++){
				int fieldid_tmp = Util.getIntValue((String)fieldidList.get(i));
				String fieldname_tmp = Util.null2String((String)fieldname_hs.get("fieldname"+fieldid_tmp));
				int fieldhtmltype_tmp = Util.getIntValue((String)fieldhtmltypeList.get(i), 0);
				int type_tmp = Util.getIntValue((String)fieldtypeList.get(i));
				int groupid_tmp = 0;
				int isview_tmp = Util.getIntValue((String)isview_hs.get("isview"+fieldid_tmp), 0);
				int isedit_tmp = Util.getIntValue((String)isedit_hs.get("isedit"+fieldid_tmp), 0);
				int ismand_tmp = Util.getIntValue((String)ismand_hs.get("ismand"+fieldid_tmp), 0);
				if(mustNoEdit == 1){
					isedit_tmp = 0;
					ismand_tmp = 0;
				}
				String fielddbtype_tmp = Util.null2String((String)fielddbtypeList.get(i));

				int tmpmanagerid = 0;
				if(fieldname_tmp.equals("manager") && currentnodetype!=3){//manager字段
					int beagenter = user.getUID();
					//获得被代理人
					rs.executeSql("select agentorbyagentid from workflow_currentoperator where usertype=0 and isremark='0' and requestid="+requestid+" and userid="+user.getUID()+" and nodeid="+nodeid+" order by id desc");
					if(rs.next()){
						int tembeagenter = rs.getInt(1);
						if(tembeagenter>0){
							beagenter = tembeagenter;
						}
					}
					tmpmanagerid = Util.getIntValue(resourceComInfo.getManagerID(""+beagenter), 0);
					if(isview_tmp == 0){
						htmlHiddenElementsb.append("\n").append("<input type=\"hidden\" id=\"field"+fieldid_tmp+"\" name=\"field"+fieldid_tmp+"\" value=\""+tmpmanagerid+"\">").append("\n");
					}
					isedit_tmp = 0;
					ismand_tmp = 0;
					//continue;
				}

				otherPara_hs.put("fielddbtype", fielddbtype_tmp);
				if(fieldname_tmp.equals("begindate")){
					newfromdate = "field"+fieldid_tmp;	  //开始日期,主要为开始日期不大于结束日期进行比较
				}
				if(fieldname_tmp.equals("enddate")){
					newenddate = "field"+fieldid_tmp;	 //结束日期,主要为开始日期不大于结束日期进行比较
				}
				if(fieldid_tmp == keywordFieldId){
					keywordismand = ismand_tmp;
					keywordisedit = isedit_tmp;
				}
				if((""+yearFieldId).equals(""+fieldid_tmp)){
					yearFieldHtmlType = fieldhtmltype_tmp; 
				}				
				
				int isdetail_tmp = 0;
				int fieldlength_tmp = 0;
				int fieldheight_tmp = 4;
				if(fieldhtmltype_tmp==1 && type_tmp==1){//单文本中的文本
					if((fielddbtype_tmp.toLowerCase()).indexOf("varchar")>-1){
						fieldlength_tmp = Util.getIntValue(fielddbtype_tmp.substring(fielddbtype_tmp.indexOf("(")+1, fielddbtype_tmp.length()-1));
					}
				}else if(fieldhtmltype_tmp == 2){//多行文本框
					if(isbill == 0){
						rs.execute("select textheight from workflow_formdict where id="+fieldid_tmp);
					}else{
						rs.execute("select textheight from workflow_billfield where id="+fieldid_tmp+" and billid="+formid);
					}
					if(rs.next()){
						fieldheight_tmp = Util.getIntValue(rs.getString("textheight"), 4);
						otherPara_hs.put("fieldheight", ""+fieldheight_tmp);
					}
				}
				otherPara_hs.put("httprequest", request);
				otherPara_hs.put("requestid", ""+requestid);
				otherPara_hs.put("userid", ""+user.getUID());

				//记录必填标志
				if(ismand_tmp==1 && !codeField.equals(""+fieldid_tmp)){
					if(fieldid_tmp > 0){
						needcheck += (",field"+fieldid_tmp);
					}else{
						if(fieldid_tmp == -1){
							needcheck += (",requestname");
						}
					}
				}else if(fieldid_tmp==-1 && isedit_tmp==1){
					needcheck += (",requestname");
					ismand_tmp = 1;
				}

				String fieldlabel_tmp = Util.null2String((String)fieldlabel_hs.get("fieldlabel"+fieldid_tmp));
				String fieldvalue_tmp = Util.null2String((String)fieldvalue_hs.get("inoperatevalue"+fieldid_tmp));
				String inoperatefield_tmp = Util.null2String((String)inoperatefield_hs.get("inoperatefield"+fieldid_tmp));
				if(!"1".equals(inoperatefield_tmp) && iscreate==1){//没有设置节点前附加操作 ，并且是创建时
					if(fieldhtmltype_tmp == 3){
						if((type_tmp==8 || type_tmp==135) && !prjid.equals("")){	   //浏览按钮为项目,从前面的参数中获得项目默认值
							fieldvalue_tmp = "" + Util.getIntValue(prjid,0);
						}else if((type_tmp==9 || type_tmp==37) && !docid.equals("")){ //浏览按钮为文档,从前面的参数中获得文档默认值
							fieldvalue_tmp = "" + Util.getIntValue(docid,0);
						}else if((type_tmp==1 ||type_tmp==17||type_tmp==165||type_tmp==166) && !hrmid.equals("")){ //浏览按钮为人,从前面的参数中获得人默认值
							fieldvalue_tmp = "" + Util.getIntValue(hrmid,0);
						}else if((type_tmp==7 || type_tmp==18) && !crmid.equals("")){ //浏览按钮为CRM,从前面的参数中获得CRM默认值
							fieldvalue_tmp = "" + Util.getIntValue(crmid,0);
						}else if((type_tmp==16 || type_tmp==152 || type_tmp==171) && !reqid.equals("")){ //浏览按钮为REQ,从前面的参数中获得REQ默认值
							fieldvalue_tmp = "" + Util.getIntValue(reqid,0);
						}else if((type_tmp==4 || type_tmp==57 || type_tmp==167 || type_tmp==168) && !hrmid.equals("")){ //浏览按钮为部门,从前面的参数中获得人默认值(由人力资源的部门得到部门默认值)
							fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getDepartmentID(hrmid),0);
						}else if(type_tmp==24 && !hrmid.equals("")){ //浏览按钮为职务,从前面的参数中获得人默认值(由人力资源的职务得到职务默认值)
							fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getJobTitle(hrmid),0);
						}else if(type_tmp==32 && !hrmid.equals("")){ //浏览按钮为职务,从前面的参数中获得人默认值(由人力资源的职务得到职务默认值)
							fieldvalue_tmp = "" + Util.getIntValue(request.getParameter("TrainPlanId"),0);
						}else if((type_tmp==164 || type_tmp==169 || type_tmp==170) && !hrmid.equals("")){ //浏览按钮为分部,从前面的参数中获得人默认值(由人力资源的分部得到分部默认值)
							fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getSubCompanyID(hrmid),0);
						}else if(type_tmp == 2){//日期
							fieldvalue_tmp = TimeUtil.getCurrentDateString();
						}else if(type_tmp == 19){//时间
							fieldvalue_tmp = TimeUtil.getCurrentTimeString().substring(11, 16);
						}
					}
				}
				if(fieldname_tmp.equals("manager") && currentnodetype!=3){
					fieldvalue_tmp = "" + tmpmanagerid;
				}
				String content1 = "";
				String content2 = "";
				//替换字段显示名。这里如果找到就替换，如果找不到就不处理
				int pos = htmlLayout.toLowerCase().indexOf("$label"+fieldid_tmp+"$");
				while(pos > -1){
					content1 = wfformhtml.substring(0, pos);
					content2 = wfformhtml.substring(pos+1);
					int pos1 = content1.lastIndexOf("<");
					int pos2 = content2.indexOf(">");
					if(pos1 > -1){
						content1 = content1.substring(0, pos1);
					}
					if(pos2 > -1){
						content2 = content2.substring(pos2+1);
					}
					wfformhtml = content1 + fieldlabel_tmp + content2;
					htmlLayout = wfformhtml;
					pos = htmlLayout.toLowerCase().indexOf("$label"+fieldid_tmp+"$");
				}
				//替换字段
				pos = htmlLayout.toLowerCase().indexOf("$field"+fieldid_tmp+"$");
				if(pos > -1){
					content1 = wfformhtml.substring(0, pos);
					content2 = wfformhtml.substring(pos+1);
					int pos1 = content1.lastIndexOf("<");
					int pos2 = content2.indexOf(">");
					if(pos1 > -1){
						content1 = content1.substring(0, pos1);
					}
					if(pos2 > -1){
						content2 = content2.substring(pos2+1);
					}
					isview_tmp = 1;
					if(fieldid_tmp > 0){
						if(fieldhtmltype_tmp==2 && type_tmp==2 && (isedit_tmp==0 || isviewonly==1)){//有Fck字段，并且显示且不能编辑
							fckfieldidList.add("FCKiframe"+fieldid_tmp);
						}
						try {
							object = (HtmlElement)Class.forName(fieldTypeComInfo.getClassname(""+fieldhtmltype_tmp)).newInstance();
							Hashtable ret_hs = object.getHtmlElementString(fieldid_tmp, fieldname_tmp, type_tmp, fieldlabel_tmp, fieldlength_tmp, isdetail_tmp, groupid_tmp, fieldvalue_tmp, isviewonly, 1, isedit_tmp, ismand_tmp, user, otherPara_hs);
							String inputStr_tmp = Util.null2String((String)ret_hs.get("inputStr"));
							//retStr += inputStr_tmp;
							wfformhtml = content1 + inputStr_tmp + content2;
							htmlLayout = wfformhtml;
							String jsStr_t = Util.null2String((String)ret_hs.get("jsStr"));
							jsStr.append("\n").append(jsStr_t).append("\n");
						}catch(Exception e){
							writeLog(e);
						}
					}else{//流程标题、紧急程度、短信提醒3个特殊字段
						Hashtable ret_hs = new Hashtable();
						systemElement.setIsviewonly(isviewonly);
						systemElement.setIsview(isview_tmp);
						systemElement.setIsedit(isedit_tmp);
						systemElement.setIsmand(ismand_tmp);
						systemElement.setLanguageid(user.getLanguage());
						systemElement.setOtherPara(otherPara_hs);
						systemElement.setFieldvalue(fieldvalue_tmp);
						if(fieldid_tmp == -1){
							if(isview_tmp==0 && isviewonly==0){//保证流程标题不空。如果不显示，就存默认值
								defaultName = 1;
							}
							if(defaultName==1 && iscreate==1){
								fieldvalue_tmp = Util.toScreenToEdit( workflowname+"-"+user.getLastname()+"-"+TimeUtil.getCurrentDateString(), user.getLanguage());
								systemElement.setFieldvalue(fieldvalue_tmp);
							}
							ret_hs = systemElement.getRequestName();
						}else if(fieldid_tmp == -2){
							ret_hs = systemElement.getRequestLevel();
						}else if(fieldid_tmp == -3){
							ret_hs = systemElement.getMessageType(messageType);
						}
						String inputStr_tmp = Util.null2String((String)ret_hs.get("inputStr"));
						wfformhtml = content1 + inputStr_tmp + content2;
						htmlLayout = wfformhtml;
					}
				}else{
					//如果是3个系统字段，则需要放一个隐藏的input
					if(fieldid_tmp == -1){
						if(iscreate==1 && "".equals(fieldvalue_tmp)){
							fieldvalue_tmp = Util.null2String(workflowname+"-"+user.getLastname()+"-"+TimeUtil.getCurrentDateString());
						}
						htmlHiddenElementsb.append("\n").append("<input type=\"hidden\" id=\"requestname\" name=\"requestname\" value=\""+Util.toScreenToEdit(fieldvalue_tmp, user.getLanguage())+"\">"+"\n");
					}else if(fieldid_tmp == -2){
						htmlHiddenElementsb.append("\n").append("<input type=\"hidden\" id=\"requestlevel\" name=\"requestlevel\" value=\""+fieldvalue_tmp+"\">"+"\n");
					}else if(fieldid_tmp == -3){
						htmlHiddenElementsb.append("\n").append("<input type=\"hidden\" id=\"messageType\" name=\"messageType\" value=\""+fieldvalue_tmp+"\">"+"\n");
					}
				}
			}
			//拼JS方法
			jsStr.append("\n");
			jsStr.append("function checktimeok(){<!-- 结束日期不能小于开始日期 -->").append("\n");
			jsStr.append("\tif (\""+newenddate+"\"!=\"b\" && \""+newfromdate+"\"!=\"a\" && document.frmmain."+newenddate+".value!=\"\"){").append("\n");
			jsStr.append("\t\tYearFrom=document.frmmain."+newfromdate+".value.substring(0,4);").append("\n");
			jsStr.append("\t\tMonthFrom=document.frmmain."+newfromdate+".value.substring(5,7);").append("\n");
			jsStr.append("\t\tDayFrom=document.frmmain."+newfromdate+".value.substring(8,10);").append("\n");
			jsStr.append("\t\tYearTo=document.frmmain."+newenddate+".value.substring(0,4);").append("\n");
			jsStr.append("\t\tMonthTo=document.frmmain."+newenddate+".value.substring(5,7);").append("\n");
			jsStr.append("\t\tDayTo=document.frmmain."+newenddate+".value.substring(8,10);").append("\n");
			jsStr.append("\t\tif(!DateCompare(YearFrom, MonthFrom, DayFrom,YearTo, MonthTo,DayTo )){").append("\n");
			jsStr.append("\t\t\twindow.alert(\""+SystemEnv.getHtmlLabelName(15273,user.getLanguage())+"\");").append("\n");
			jsStr.append("\t\t\treturn false;").append("\n");
			jsStr.append("\t\t}").append("\n");
			jsStr.append("\t}").append("\n");
			jsStr.append("\t\treturn true;").append("\n");
			jsStr.append("}").append("\n");

			jsStr.append("\n");
			jsStr.append("function changeKeyword(){").append("\n");
			if(titleFieldId>0&&keywordFieldId>0){
				jsStr.append("\tvar titleObj=document.getElementById(\"field"+titleFieldId+"\");").append("\n");
				jsStr.append("\tvar keywordObj=document.getElementById(\"field"+keywordFieldId+"\");").append("\n");
				jsStr.append("\tif(titleObj!=null&&keywordObj!=null){").append("\n");
				jsStr.append("\t\tdocument.getElementById(\"workflowKeywordIframe\").src=\"/docs/sendDoc/WorkflowKeywordIframe.jsp?operation=UpdateKeywordData&docTitle=\"+titleObj.value+\"&docKeyword=\"+keywordObj.value;").append("\n");
				jsStr.append("\t}").append("\n");
			}else if(titleFieldId==-3&&keywordFieldId>0){
				jsStr.append("\tvar titleObj=document.getElementById(\"requestname\");").append("\n");
				jsStr.append("\tvar keywordObj=document.getElementById(\"field"+keywordFieldId+"\");").append("\n");
				jsStr.append("\tif(titleObj!=null&&keywordObj!=null){").append("\n");
				jsStr.append("\tdocument.getElementById(\"workflowKeywordIframe\").src=\"/docs/sendDoc/WorkflowKeywordIframe.jsp?operation=UpdateKeywordData&docTitle=\"+titleObj.value+\"&docKeyword=\"+keywordObj.value;").append("\n");
				jsStr.append("\t}").append("\n");
			}
			jsStr.append("}").append("\n");

			jsStr.append("\n");
			jsStr.append("function updateKeywordData(strKeyword){").append("\n");
			if(keywordFieldId>0){
				jsStr.append("\tvar keywordObj=document.getElementById(\"field"+keywordFieldId+"\");").append("\n");
				jsStr.append("\tvar keywordismand="+keywordismand+";").append("\n");
				jsStr.append("\tvar keywordisedit="+keywordisedit+";").append("\n");
				jsStr.append("\tif(keywordObj!=null){").append("\n");
				jsStr.append("\t\tif(keywordisedit==1){").append("\n");
				jsStr.append("\t\t\tkeywordObj.value=strKeyword;").append("\n");
				jsStr.append("\t\t\tif(keywordismand==1){").append("\n");
				jsStr.append("\t\t\t\tcheckinput('field"+keywordFieldId+"','field"+keywordFieldId+"span');").append("\n");
				jsStr.append("\t\t\t}").append("\n");
				jsStr.append("\t\t}else{").append("\n");
				jsStr.append("\t\t\tkeywordObj.value=strKeyword;").append("\n");
				jsStr.append("\t\t\tfield"+keywordFieldId+"span.innerHTML=strKeyword;").append("\n");
				jsStr.append("\t\t}").append("\n");
				jsStr.append("\t}").append("\n");
			}
			jsStr.append("}").append("\n");

			jsStr.append("\n");
			if(titleFieldId==-3 && keywordFieldId>0){
				jsStr.append("changeKeyword();").append("\n");
			}

			jsStr.append("\n");
			jsStr.append("function onShowKeyword(isbodymand){").append("\n");
			if(keywordFieldId>0){
				jsStr.append("\tvar keywordObj=document.getElementById(\"field"+keywordFieldId+"\");").append("\n");
				jsStr.append("\tif(keywordObj!=null){").append("\n");
				jsStr.append("\t\tstrKeyword=keywordObj.value;").append("\n");
				jsStr.append("\t\ttempUrl=escape(\"/docs/sendDoc/WorkflowKeywordBrowserMulti.jsp?strKeyword=\"+strKeyword);").append("\n");
				jsStr.append("\t\ttempUrl=tempUrl.replace(/%A0/g,'%20');").append("\n");
				jsStr.append("\t\treturnKeyword=window.showModalDialog(\"/systeminfo/BrowserMain.jsp?url=\"+tempUrl);").append("\n");

				jsStr.append("\t\tif(typeof(returnKeyword)!=\"undefined\"){").append("\n");
				jsStr.append("\t\t\tkeywordObj.value=returnKeyword;").append("\n");
				jsStr.append("\t\t\tif(isbodymand==1){").append("\n");
				jsStr.append("\t\t\t\tcheckinput('field"+keywordFieldId+"','field"+keywordFieldId+"span');").append("\n");
				jsStr.append("\t\t\t}").append("\n");
				jsStr.append("\t\t}").append("\n");
				jsStr.append("\t}").append("\n");
			}
			jsStr.append("}").append("\n");

			ArrayList currentdateList=Util.TokenizerString(currentdate,"-") ;
			int departmentId=Util.getIntValue(resourceComInfo.getDepartmentID(""+creater),-1);
			DepartmentComInfo departmentComInfo=new DepartmentComInfo();
			int subCompanyId=Util.getIntValue(departmentComInfo.getSubcompanyid1(""+departmentId),-1);
			SubCompanyComInfo subCompanyComInfo=new SubCompanyComInfo();
			int supSubCompanyId=Util.getIntValue(subCompanyComInfo.getSupsubcomid(""+subCompanyId),-1);
			if(supSubCompanyId<=0){
				supSubCompanyId=subCompanyId;//若上级分部为空，则认为上级分部为分部
			}
			
			jsStr.append("var workflowId="+workflowid+";\n");
			jsStr.append("var formId="+formid+";\n");
			jsStr.append("var isBill="+isbill+";\n");
			jsStr.append("var yearId=-1;\n");
			jsStr.append("var monthId=-1;\n");
			jsStr.append("var dateId=-1;\n");
			jsStr.append("var fieldId=-1;\n");
			jsStr.append("var fieldValue=-1;\n");
			jsStr.append("var supSubCompanyId=-1;\n");
			jsStr.append("var subCompanyId=-1;\n");
			jsStr.append("var departmentId=-1;\n");
			jsStr.append("var recordId=-1;\n");

			jsStr.append("var yearFieldValue=-1;\n");
			jsStr.append("var yearFieldHtmlType=-1;\n");
			jsStr.append("var monthFieldValue=-1;\n");
			jsStr.append("var dateFieldValue=-1;\n");	

			jsStr.append("function initDataForWorkflowCode(){\n");
			jsStr.append("\tyearId=-1;\n");
			jsStr.append("\tmonthId=-1;\n");
			jsStr.append("\tdateId=-1;\n");
			jsStr.append("\tfieldId=-1;\n");
			jsStr.append("\tfieldValue=-1;\n");
			jsStr.append("\tsupSubCompanyId=-1;\n");
			jsStr.append("\tsubCompanyId=-1;\n");
			jsStr.append("\tdepartmentId=-1;\n");
			jsStr.append("\trecordId=-1;\n");

			jsStr.append("\tyearFieldValue=-1;\n");
			jsStr.append("\tyearFieldHtmlType="+yearFieldHtmlType+";\n");
			jsStr.append("\tmonthFieldValue=-1;\n");
			jsStr.append("\tdateFieldValue=-1;\n");	

			jsStr.append("\tif(document.getElementById(\"field"+yearFieldId+"\")!=null){\n");
			jsStr.append("\t\tif(yearFieldHtmlType==5){//年份为下拉框\n");
			jsStr.append("\t\t\ttry{\n");
			jsStr.append("\t\t\t\tobjYear=document.getElementById(\"field"+yearFieldId+"\");\n");
			jsStr.append("\t\t\t\tyearId=objYear.options[objYear.selectedIndex].text;\n"); 
			jsStr.append("\t\t\t}catch(e){\n");
			jsStr.append("\t\t\t}\n");
			jsStr.append("\t\t}else{\n");
			jsStr.append("\t\t\tyearFieldValue=document.getElementById(\"field"+yearFieldId+"\").value;\n");
			jsStr.append("\t\t\tif(yearFieldValue.indexOf(\"-\")>0){\n");
			jsStr.append("\t\t\t\tvar yearFieldValueArray = yearFieldValue.split(\"-\") ;\n");
			jsStr.append("\t\t\t\tif(yearFieldValueArray.length>=1){\n");
			jsStr.append("\t\t\t\t\tyearId=yearFieldValueArray[0];\n");
			jsStr.append("\t\t\t\t}\n");
			jsStr.append("\t\t\t}else{\n");
			jsStr.append("\t\t\t\tyearId=yearFieldValue;\n");
			jsStr.append("\t\t\t}\n");
			jsStr.append("\t\t}\n");
			jsStr.append("\t}\n");

			jsStr.append("\tif(document.getElementById(\"field"+monthFieldId+"\")!=null){\n");
			jsStr.append("\t\tmonthFieldValue=document.getElementById(\"field"+monthFieldId+"\").value;\n");
			jsStr.append("\t\tif(monthFieldValue.indexOf(\"-\")>0){\n");
			jsStr.append("\t\t\tvar monthFieldValueArray = monthFieldValue.split(\"-\") ;\n");
			jsStr.append("\t\t\tif(monthFieldValueArray.length>=2){\n");
			jsStr.append("\t\t\t\tyearId=monthFieldValueArray[0];\n");
			jsStr.append("\t\t\t\tmonthId=monthFieldValueArray[1];\n");
			jsStr.append("\t\t\t}\n");
			jsStr.append("\t\t}\n");
			jsStr.append("\t}\n");

			jsStr.append("\tif(document.getElementById(\"field"+dateFieldId+"\")!=null){\n");
			jsStr.append("\t\tdateFieldValue=document.getElementById(\"field"+dateFieldId+"\").value;\n");
			jsStr.append("\t\tif(dateFieldValue.indexOf(\"-\")>0){\n");
			jsStr.append("\t\t\tvar dateFieldValueArray = dateFieldValue.split(\"-\") ;\n");
			jsStr.append("\t\t\tif(dateFieldValueArray.length>=3){\n");
			jsStr.append("\t\t\t\tyearId=dateFieldValueArray[0];\n");
			jsStr.append("\t\t\t\tmonthId=dateFieldValueArray[1];\n");
			jsStr.append("\t\t\t\tdateId=dateFieldValueArray[2];\n");
			jsStr.append("\t\t\t}\n");
			jsStr.append("\t\t}\n");
			jsStr.append("\t}\n");

			if(currentdateList.size()>=1){
				jsStr.append("\tif(yearId==\"\"||yearId<=0){\n");
				jsStr.append("\t\tyearId="+(String)currentdateList.get(0)+";\n");
				jsStr.append("\t}\n");
			}

			if(currentdateList.size()>=2){
				jsStr.append("\tif(monthId==\"\"||monthId<=0){\n");
				jsStr.append("\t\tmonthId="+(String)currentdateList.get(1)+";\n");
				jsStr.append("\t}\n");
			}

			if(currentdateList.size()>=3){
				jsStr.append("\tif(dateId==\"\"||dateId<=0){\n");
				jsStr.append("\t\tdateId="+(String)currentdateList.get(2)+";\n");
				jsStr.append("\t}\n");
			}

			jsStr.append("\tif(document.getElementById(\"field"+fieldIdSelect+"\")!=null){\n");
			jsStr.append("\t\tfieldId="+fieldIdSelect+";\n");
			jsStr.append("\t\tfieldValue=document.getElementById(\"field"+fieldIdSelect+"\").value;\n");
			jsStr.append("\t}\n");

			jsStr.append("\tif(document.getElementById(\"field"+supSubCompanyFieldId+"\")!=null){\n");
			jsStr.append("\t\tsupSubCompanyId=document.getElementById(\"field"+supSubCompanyFieldId+"\").value;\n");
			jsStr.append("\t}\n");
			jsStr.append("\tif(supSubCompanyId==\"\"||supSubCompanyId<=0){\n");
			jsStr.append("\t\tsupSubCompanyId="+supSubCompanyId+";\n");
			jsStr.append("\t}\n");
			
			jsStr.append("\tif(document.getElementById(\"field"+subCompanyFieldId+"\")!=null){\n");
			jsStr.append("\t\tsubCompanyId=document.getElementById(\"field"+subCompanyFieldId+"\").value;\n");
			jsStr.append("\t}\n");
			jsStr.append("\tif(subCompanyId==\"\"||subCompanyId<=0){\n");
			jsStr.append("\t\tsubCompanyId="+subCompanyId+";\n");
			jsStr.append("\t}\n");

			jsStr.append("\tif(document.getElementById(\"field"+departmentFieldId+"\")!=null){\n");
			jsStr.append("\t\tdepartmentId=document.getElementById(\"field"+departmentFieldId+"\").value;\n");
			jsStr.append("\t}\n");
			jsStr.append("\tif(departmentId==\"\"||departmentId<=0){\n");
			jsStr.append("\t\tdepartmentId="+departmentId+";\n");
			jsStr.append("\t}\n");
			jsStr.append("}\n");

			jsStr.append("function onCreateCodeAgain(ismand){\n");
			jsStr.append("\tif(document.getElementById(\"field"+fieldCode+"\")!=null&&document.getElementById(\"field"+fieldCode+"span\")!=null){\n");
			jsStr.append("\t\tinitDataForWorkflowCode();\n");
			jsStr.append("\t\tdocument.getElementById(\"workflowKeywordIframe\").src=\"/workflow/request/WorkflowCodeIframe.jsp?operation=CreateCodeAgain&requestId="+requestid+"&workflowId=\"+workflowId+\"&formId=\"+formId+\"&isBill=\"+isBill+\"&yearId=\"+yearId+\"&monthId=\"+monthId+\"&dateId=\"+dateId+\"&fieldId=\"+fieldId+\"&fieldValue=\"+fieldValue+\"&supSubCompanyId=\"+supSubCompanyId+\"&subCompanyId=\"+subCompanyId+\"&departmentId=\"+departmentId+\"&recordId=\"+recordId+\"&ismand=\"+ismand;\n");
			jsStr.append("\t}\n");
			jsStr.append("}\n");
			
			jsStr.append("function onCreateCodeAgainReturn(newCode,ismand){\n");
			jsStr.append("\tif(typeof(newCode)!=\"undefined\"&&newCode!=\"\"){\n");
			jsStr.append("\t\tdocument.getElementById(\"field"+fieldCode+"\").value=newCode;\n");
			jsStr.append("\t\tdocument.getElementById(\"field"+fieldCode+"span\").innerHTML='';\n");
			jsStr.append("\t\tif(parent.document.getElementById(\"requestmarkSpan\")!=null){\n");
			jsStr.append("\t\t\tparent.document.getElementById(\"requestmarkSpan\").innerText=newCode;\n");
			jsStr.append("\t\t}\n");
			jsStr.append("\t}\n");
			jsStr.append("}\n");

			jsStr.append("function onChooseReservedCode(ismand){\n");
			jsStr.append("\tif(document.getElementById(\"field"+fieldCode+"\")!=null&&document.getElementById(\"field"+fieldCode+"span\")!=null){\n");
			jsStr.append("\t\tinitDataForWorkflowCode();\n");
			jsStr.append("\t\turl=escape(\"/workflow/workflow/showChooseReservedCodeOperate.jsp?workflowId=\"+workflowId+\"&formId=\"+formId+\"&isBill=\"+isBill+\"&yearId=\"+yearId+\"&monthId=\"+monthId+\"&dateId=\"+dateId+\"&fieldId=\"+fieldId+\"&fieldValue=\"+fieldValue+\"&supSubCompanyId=\"+supSubCompanyId+\"&subCompanyId=\"+subCompanyId+\"&departmentId=\"+departmentId+\"&recordId=\"+recordId);\n");	
			jsStr.append("\t\tcon = window.showModalDialog(\"/systeminfo/BrowserMain.jsp?url=\"+url);\n");

			jsStr.append("\t\tif(typeof(con)!=\"undefined\"&&con!=\"\"){\n");
			jsStr.append("\t\t\tdocument.getElementById(\"workflowKeywordIframe\").src=\"/workflow/request/WorkflowCodeIframe.jsp?operation=chooseReservedCode&requestId="+requestid+"&workflowId=\"+workflowId+\"&formId=\"+formId+\"&isBill=\"+isBill+\"&codeSeqReservedIdAndCode=\"+con+\"&ismand=\"+ismand;\n");	
			jsStr.append("\t\t}\n");	
			jsStr.append("\t}\n");
			jsStr.append("}\n");

			jsStr.append("function onNewReservedCode(ismand){\n");
			jsStr.append("\tinitDataForWorkflowCode();\n");
			jsStr.append("\turl=escape(\"/workflow/workflow/showNewReservedCodeOperate.jsp?workflowId=\"+workflowId+\"&formId=\"+formId+\"&isBill=\"+isBill+\"&yearId=\"+yearId+\"&monthId=\"+monthId+\"&dateId=\"+dateId+\"&fieldId=\"+fieldId+\"&fieldValue=\"+fieldValue+\"&supSubCompanyId=\"+supSubCompanyId+\"&subCompanyId=\"+subCompanyId+\"&departmentId=\"+departmentId+\"&recordId=\"+recordId);\n");	
			jsStr.append("\tcon = window.showModalDialog(\"/systeminfo/BrowserMain.jsp?url=\"+url);\n");
			jsStr.append("}\n");			
			
			jsStr.append("\n");
			jsStr.append("function showfieldpop(){").append("\n");
			if(fieldidList.size()<1){
				jsStr.append("\talert(\""+SystemEnv.getHtmlLabelName(22577,user.getLanguage())+"\");").append("\n");
			}
			jsStr.append("}").append("\n");

			jsStr.append("\n");
			jsStr.append("").append("function dyniframesize(){").append("").append("\n");
			jsStr.append("\t").append("var dyniframe;").append("").append("\n");
			for(int i=0; i<fckfieldidList.size(); i++){
				jsStr.append("\t").append("if(document.getElementById){").append("").append("\n");
				jsStr.append("\t\t").append("dyniframe = document.getElementById(\""+fckfieldidList.get(i)+"\");").append("").append("\n");
				jsStr.append("\t\t").append("if (dyniframe && !window.opera){").append("").append("\n");
				jsStr.append("\t\t\t").append("if (dyniframe.contentDocument && dyniframe.contentDocument.body.offsetHeight){").append("").append("\n");
				jsStr.append("\t\t\t\t").append("dyniframe.height = dyniframe.contentDocument.body.offsetHeight+20;").append("").append("\n");
				jsStr.append("\t\t\t").append("}else if (dyniframe.Document && dyniframe.Document.body.scrollHeight){//如果用户的浏览器是IE").append("").append("\n");
				jsStr.append("\t\t\t\t").append("dyniframe.Document.body.bgColor=\"transparent\";").append("").append("\n");
				jsStr.append("\t\t\t\t").append("dyniframe.height = dyniframe.Document.body.scrollHeight+20;").append("").append("\n");
				jsStr.append("\t\t\t").append("}").append("").append("\n");
				jsStr.append("\t\t").append("}").append("").append("\n");
				jsStr.append("\t").append("}").append("").append("\n");
			}
			jsStr.append("").append("}").append("\n");

			jsStr.append("").append("if(window.addEventListener){").append("\n");
			jsStr.append("\t").append("window.addEventListener(\"load\",dyniframesize,false);").append("\n");
			jsStr.append("").append("}else if(window.attachEvent){").append("\n");
			jsStr.append("\t").append("window.attachEvent(\"onload\",dyniframesize);").append("\n");
			jsStr.append("").append("}else{").append("\n");
			jsStr.append("\t").append("window.onload=dyniframesize;").append("\n");
			jsStr.append("").append("}").append("\n");
			//System.out.println(wfformhtml);//开发中测试用
		}catch(Exception e){
			writeLog(e);
		}
	}

	//获得节点签字意见，放到模板上指定位置
	public void getNodeRemark(){
		try{
			int workflowid = Util.getIntValue(request.getParameter("workflowid"), 0);
			int nodeid = Util.getIntValue(request.getParameter("nodeid"), 0);
			int requestid = Util.getIntValue(request.getParameter("requestid"), 0);
			RecordSet rs = new RecordSet();
			ArrayList nodeidList = new ArrayList();//nodeid队列，这样可以释放rs对象。
			String sql = "select fn.nodeid from workflow_flownode fn where fn.workflowid="+workflowid+" order by fn.nodetype, fn.nodeid";
			rs.execute(sql);
			while(rs.next()){
				int nodeid_tmp = Util.getIntValue(rs.getString("nodeid"), 0);
				if(nodeid_tmp > 0){
					nodeidList.add(""+nodeid_tmp);
				}
			}
			Hashtable hasnoderemark_hs = new Hashtable();
			Hashtable noderemark_hs = new Hashtable();
			FieldInfo fieldInfo = new FieldInfo();
			fieldInfo.setRequestid(requestid);
			fieldInfo.setUser(user);
			for(int i=0; i<nodeidList.size(); i++){
				int nodeid_tmp = Util.getIntValue((String)nodeidList.get(i), 0);
				String content1 = "";
				String content2 = "";
				//循环替换节点显示意见。这里如果找到就替换，如果找不到就不处理
				int pos = htmlLayout.toLowerCase().indexOf("$node"+nodeid_tmp+"$");
				while(pos > -1){
					content1 = wfformhtml.substring(0, pos);
					content2 = wfformhtml.substring(pos+1);
					int pos1 = content1.lastIndexOf("<");
					int pos2 = content2.indexOf(">");
					if(pos1 > -1){
						content1 = content1.substring(0, pos1);
					}
					if(pos2 > -1){
						content2 = content2.substring(pos2+1);
					}
					String nodemark = "";
					//要获得一次节点信息的数据库查询量很大，所以用Hashtable暂存。如果取过，就拿缓存里的。
					int hasnoderemark_tmp = Util.getIntValue((String)hasnoderemark_hs.get("node"+nodeid_tmp), 0);
					if(hasnoderemark_tmp == 0){
						nodemark = fieldInfo.GetNodeRemark(workflowid, nodeid_tmp, nodeid, 2);
						hasnoderemark_hs.put("node"+nodeid_tmp, "1");
						noderemark_hs.put("node"+nodeid_tmp, nodemark);
					}else{
						nodemark = Util.null2String((String)noderemark_hs.get("node"+nodeid_tmp));
					}
					
					String nodeRemar_tmp = "<br>";
					if(nodemark.indexOf("/weaver/weaver.file.FileDownload?fileid=") >= 0){
						List nodeRemarkListOfBeenSplited = fieldInfo.getNodeRemarkListOfBeenSplited(nodemark);
						Map nodeRemarkOfBeenSplitedMap = null;
						String imageNodeRemark = null;
						String strNodeRemark = null;
						for(int j=0;j<nodeRemarkListOfBeenSplited.size();j++){
							nodeRemarkOfBeenSplitedMap = (Map)nodeRemarkListOfBeenSplited.get(j);
							imageNodeRemark = (String)nodeRemarkOfBeenSplitedMap.get("imageNodeRemark");
							strNodeRemark = (String)nodeRemarkOfBeenSplitedMap.get("strNodeRemark");
							if(imageNodeRemark!=null && !"".equals(imageNodeRemark)){
								nodeRemar_tmp += ("<img src=\"" + imageNodeRemark + "\"></img>");
							}
							nodeRemar_tmp += (strNodeRemark + "<br>");
						}
					}else if(nodemark.indexOf("/weaver/weaver.file.ImgFileDownload?userid=") >= 0){
						/*
						ArrayList nodeRemarkListOfBeenSplited = FieldInfo.getNodeRemarkListOfBeenSplited(fieldInfo.dropScript(nodemark),new ArrayList());
						BaseBean wfsbean = fieldInfo.getWfsbean();
						int rowheight = Util.getIntValue(wfsbean.getPropValue("WFSignatureImg","imgheight"),0);
						int imgshowtpe = Util.getIntValue(wfsbean.getPropValue("WFSignatureImg","imgshowtpe"),2);
						Map nodeRemarkOfBeenSplitedMap = null;
						String imageNodeRemark = null;
						String strNodeRemark = null;
						int n=0;
						for(int j=0;j<nodeRemarkListOfBeenSplited.size();j++){
							nodeRemarkOfBeenSplitedMap=(Map)nodeRemarkListOfBeenSplited.get(j);
							imageNodeRemark=(String)nodeRemarkOfBeenSplitedMap.get("imageNodeRemark");
							strNodeRemark=(String)nodeRemarkOfBeenSplitedMap.get("strNodeRemark");
							nodeRemar_tmp += ("<img src=\"" + imageNodeRemark + "\"></img>");
							nodeRemar_tmp += (strNodeRemark + "<br>");
						}
						*/
						nodeRemar_tmp = nodemark;
					}else{
						nodeRemar_tmp = nodemark;
					}
					if(nodeRemar_tmp.indexOf("<br>") == 0){
						nodeRemar_tmp = nodeRemar_tmp.substring(4, nodeRemar_tmp.length());
					}
					if(nodeRemar_tmp.endsWith("<br>")){
						nodeRemar_tmp = nodeRemar_tmp.substring(0, nodeRemar_tmp.length()-4);
					}
					wfformhtml = content1 + nodeRemar_tmp + content2;
					htmlLayout = wfformhtml;
					pos = htmlLayout.toLowerCase().indexOf("$node"+nodeid_tmp+"$");
				}
			}
		}catch(Exception e){
			writeLog(e);
		}
	}

	/**
	 * 处理明细字段的转化。不需要处理字段显示名
	 */
	public void getDetailTableElement(){
		try{
			String sql = "";
			RecordSet rs_oldDetail = new RecordSet();//保留，只用于查已有的明细数据
			RecordSet rs_group = new RecordSet();
			RecordSet rs = new RecordSet();
			RecordSet rs_tmp = new RecordSet();
			DetailFieldComInfo detailFieldComInfo = new DetailFieldComInfo();
			FieldTypeComInfo fieldTypeComInfo = new FieldTypeComInfo();
			WfLinkageInfo wfLinkageInfo = new WfLinkageInfo();
			ResourceComInfo resourceComInfo = new ResourceComInfo();
			int requestid = Util.getIntValue(request.getParameter("requestid"), 0);
			int billid = Util.getIntValue(request.getParameter("billid"), 0);
			int workflowid = Util.getIntValue(request.getParameter("workflowid"), 0);
			int nodeid = Util.getIntValue(request.getParameter("nodeid"), 0);
			int nodetype = Util.getIntValue(request.getParameter("nodetype"), 0);
			int isbill = Util.getIntValue(request.getParameter("isbill"), 0);
			int formid = Util.getIntValue(request.getParameter("formid"), 0);
			int isremark = Util.getIntValue(request.getParameter("isremark"), 0);
			int isviewonly = Util.getIntValue((String)otherPara_hs.get("isviewonly"), 0);
			int mustNoEdit = Util.getIntValue((String)otherPara_hs.get("mustNoEdit"), 0);
			String prjid = Util.null2String(request.getParameter("prjid"));
			String reqid = Util.null2String(request.getParameter("reqid"));
			String docid = Util.null2String(request.getParameter("docid"));
			String hrmid = Util.null2String(request.getParameter("hrmid"));
			String crmid = Util.null2String(request.getParameter("crmid"));
			if("".equals(hrmid) && "1".equals(this.user.getLogintype())){
				hrmid = ""+this.user.getUID();
			}else if("".equals(crmid) && "2".equals(this.user.getLogintype())){
				crmid = ""+this.user.getUID();
			}
			int isprint = Util.getIntValue(request.getParameter("isprint"), 0);
			HttpSession session = (HttpSession)request.getSession(false);
			int _intervenorright = Util.getIntValue((String)session.getAttribute(user.getUID()+"_"+requestid+"intervenorright"),0);
			String dlbodychangattrstr = "";
			HtmlElement object = null;

			DynamicDataInput ddidetail = new DynamicDataInput(workflowid+"");
			String trrigerdetailfield = ddidetail.GetEntryTriggerDetailFieldName();
			ArrayList seldefieldsadd = wfLinkageInfo.getSelectField(workflowid, nodeid, 1);
			ArrayList changedefieldsadd = wfLinkageInfo.getChangeField(workflowid, nodeid, 1);
			otherPara_hs.put("seldefieldsadd", seldefieldsadd);
			otherPara_hs.put("changedefieldsadd", changedefieldsadd);
			otherPara_hs.put("trrigerdetailfield", trrigerdetailfield);

			Hashtable detailField2Groupid_hs = new Hashtable();
			//把单据的明细字段表明映射为groupid
			if(isbill == 1){
				int cx = 0;
				sql = "select tablename from Workflow_billdetailtable where billid="+formid+" order by orderid";
				rs_tmp.execute(sql);
				while(rs_tmp.next()){
					String tablename_tmp = Util.null2String(rs_tmp.getString("tablename"));
					if(!"".equals(tablename_tmp)){
						detailField2Groupid_hs.put(tablename_tmp, ""+cx);
						cx++;
					}
				}
			}

			Hashtable detailFieldid_hs = new Hashtable();//表单的所有明细表字段
			Hashtable fieldhtmltype_hs = new Hashtable();			//字段的htmltype队列
			Hashtable fieldtype_hs = new Hashtable();				//字段的type队列
			Hashtable fielddbtype_hs = new Hashtable();			//字段的数据库字段类型队列
			Hashtable fieldname_hs = new Hashtable();//表字段在数据库的字段名字
			Hashtable isview_hs = new Hashtable();//是否显示
			Hashtable isedit_hs = new Hashtable();//是否可编辑
			Hashtable ismand_hs = new Hashtable();//是否必填
			Hashtable fieldlabel_hs = new Hashtable();//字段显示名
			if(isbill == 0){
				sql = "select nf.*, fl.fieldlable, ff.groupid, '' as fieldname from workflow_nodeform nf left join workflow_formfield ff on nf.fieldid=ff.fieldid and ff.formid="+formid+" left join workflow_fieldlable fl on fl.fieldid=nf.fieldid and fl.formid="+formid+" and fl.langurageid="+user.getLanguage()+" where nf.nodeid="+nodeid+" and ff.isdetail='1' order by nf.orderid, ff.fieldorder";
			}else if(isbill == 1){
				//单据的明细字段信息先拿出来，具体分组以后再查数据库
				sql = "select nf.*, bf.fieldlabel as fieldlable, detailtable as groupid, bf.fieldname, bf.fieldhtmltype, bf.type, bf.fielddbtype from workflow_nodeform nf left join workflow_billfield bf on nf.fieldid=bf.id and bf.billid="+formid+" where nf.nodeid="+nodeid+" and bf.viewtype=1 order by nf.orderid, bf.dsporder";
			}
			rs_tmp.execute(sql);
			while(rs_tmp.next()){
				int fieldid_tmp = Util.getIntValue(rs_tmp.getString("fieldid"), 0);
				int isview_tmp = Util.getIntValue(rs_tmp.getString("isview"), 0);
				int isedit_tmp = Util.getIntValue(rs_tmp.getString("isedit"), 0);
				int ismand_tmp = Util.getIntValue(rs_tmp.getString("ismandatory"), 0);
				String fieldname = Util.null2String(rs_tmp.getString("fieldname"));
				if(isbill == 0){
					fieldname = detailFieldComInfo.getFieldname(""+fieldid_tmp);
				}
				String labelName_tmp = "";
				if(isbill==0){
					labelName_tmp = Util.null2String(rs_tmp.getString("fieldlable"));
				}else{
					int labelid_tmp = Util.getIntValue(rs_tmp.getString("fieldlable"));
					labelName_tmp = SystemEnv.getHtmlLabelName(labelid_tmp, user.getLanguage());
				}
				labelName_tmp = Util.toScreenForWorkflow(labelName_tmp);
				String groupid_tmp = "";
				if(isbill == 0){
					groupid_tmp = ""+Util.getIntValue(rs_tmp.getString("groupid"), 0);
				}else{
					String groupname_tmp = ""+Util.null2String(rs_tmp.getString("groupid"));
					groupid_tmp = ""+Util.getIntValue((String)detailField2Groupid_hs.get(groupname_tmp), -1);
				}
				ArrayList detailFieldidList = (ArrayList)detailFieldid_hs.get("detailfieldList_"+groupid_tmp);
				if(detailFieldidList == null){
					detailFieldidList = new ArrayList();
				}
				detailFieldidList.add(""+fieldid_tmp);
				detailFieldid_hs.put("detailfieldList_"+groupid_tmp, detailFieldidList);
				int htmltype_tmp = 0;
				int type_tmp = 0;
				String dbtype_tmp = "";
				if(isbill == 0){
					htmltype_tmp = Util.getIntValue(detailFieldComInfo.getFieldhtmltype(""+fieldid_tmp), 0);
					type_tmp = Util.getIntValue(detailFieldComInfo.getFieldType(""+fieldid_tmp), 0);
					dbtype_tmp = Util.null2String(detailFieldComInfo.getFielddbtype(""+fieldid_tmp));
				}else{
					htmltype_tmp = Util.getIntValue(rs_tmp.getString("fieldhtmltype"), 0);
					type_tmp = Util.getIntValue(rs_tmp.getString("type"), 0);
					dbtype_tmp = Util.null2String(rs_tmp.getString("fielddbtype"));
				}
				fieldhtmltype_hs.put("fieldhtmltype"+fieldid_tmp, ""+htmltype_tmp);
				fieldtype_hs.put("fieldtype"+fieldid_tmp, ""+type_tmp);
				fielddbtype_hs.put("fielddbtype"+fieldid_tmp, dbtype_tmp);
				fieldname_hs.put("fieldname"+fieldid_tmp, fieldname);
				isview_hs.put("isview"+fieldid_tmp, ""+isview_tmp);
				isedit_hs.put("isedit"+fieldid_tmp, ""+isedit_tmp);
				ismand_hs.put("ismand"+fieldid_tmp, ""+ismand_tmp);
				fieldlabel_hs.put("fieldlabel"+fieldid_tmp, labelName_tmp);
			}
			otherPara_hs.put("detailFieldid_hs", detailFieldid_hs);
			//获取节点前附加操作
			Hashtable inoperatefield_hs = new Hashtable();
			Hashtable fieldvalue_hs = new Hashtable();//节点前附加操作的值
			RequestPreAddinoperateManager requestPreAddM = new RequestPreAddinoperateManager();
			requestPreAddM.setCreater(user.getUID());
			requestPreAddM.setOptor(user.getUID());
			requestPreAddM.setWorkflowid(workflowid);
			requestPreAddM.setNodeid(nodeid);
			Hashtable getPreAddRule_hs = requestPreAddM.getPreAddRule();
			inoperatefield_hs = (Hashtable)getPreAddRule_hs.get("inoperatefield_hs");
			fieldvalue_hs = (Hashtable)getPreAddRule_hs.get("inoperatevalue_hs");

			//明细字段间操作的数据
			ArrayList colCalAry = new ArrayList();
			String rowCalItemStr1,colCalItemStr1,mainCalStr1;
			rowCalItemStr1 = new String("");
			colCalItemStr1 = new String("");
			mainCalStr1 = new String("");
			rs_tmp.executeProc("Workflow_formdetailinfo_Sel",formid+"");
			while(rs_tmp.next()){
				rowCalItemStr1 = Util.null2String(rs_tmp.getString("rowCalStr"));
				colCalItemStr1 = Util.null2String(rs_tmp.getString("colCalStr"));
				mainCalStr1 = Util.null2String(rs_tmp.getString("mainCalStr"));
				//System.out.println("rowCalItemStr1 = " + rowCalItemStr1);
			}
			StringTokenizer stk = new StringTokenizer(colCalItemStr1,";");
			while(stk.hasMoreTokens()){
				colCalAry.add(stk.nextToken());
			}

			if(isbill == 0){
				sql = "select distinct groupid from workflow_formfield where formid="+formid+" and isdetail='1' order by groupid";
			}else{
				sql = "select tablename as groupid, title from Workflow_billdetailtable where billid="+formid+" order by orderid";
			}
			rs_group.execute(sql);
			int groupCount = 0;
			int derecorderindex = 0;

			while(rs_group.next()){
				String submitdtlid = "";
				StringBuffer addJsSb = new StringBuffer();
				StringBuffer delJsSb = new StringBuffer();
				String addJSExt = "";
				int groupid_tmp = -1;
				if(isbill == 0){
					groupid_tmp = Util.getIntValue(rs_group.getString("groupid"), 0);
				}else{
					groupid_tmp = groupCount;
				}
				String hiddenElementStr = "";
				String addJsStr = "";
				WFNodeDtlFieldManager wFNodeDtlFieldManager = new WFNodeDtlFieldManager();
				wFNodeDtlFieldManager.resetParameter();
				wFNodeDtlFieldManager.setNodeid(nodeid);
				wFNodeDtlFieldManager.setGroupid(groupid_tmp);
				wFNodeDtlFieldManager.selectWfNodeDtlField();
				String dtladd = wFNodeDtlFieldManager.getIsadd();//其实这个条件意义不大，因为添加按钮根据页面本身的来，不做任何解析
				String dtldelete = wFNodeDtlFieldManager.getIsdelete();//这个条件的意义在于，已有的明细字段，在最前面是不是有checkbox
				String dtledit = wFNodeDtlFieldManager.getIsedit();
				String isprintnulldetail = wFNodeDtlFieldManager.getIshide();

				String groupName_tmp = "";
				if(isbill == 0){
					groupName_tmp = ""+Util.getIntValue(rs_group.getString("groupid"), 0);
				}else{
					groupName_tmp = ""+Util.null2String(rs_group.getString("groupid"));;
				}
				boolean shouldHidden = false;
				if(_intervenorright == 1){
					shouldHidden = true;
				}
				if(isprint==1 && !"1".equals(isprintnulldetail)){
					if(isbill == 1){
						if(billtablename.indexOf("formtable_main_")==0&&groupName_tmp.indexOf("formtable_main_")==0){//新表单
							rs.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid+" order by b.id");
						}else if(Util.getIntValue(""+formid)<0){	 //数据中心模块创建的明细报表
							rs.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid+" order by b.inputid");
						}else{
							rs.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid);
						}
						if(rs.getCounts()<=0){
							shouldHidden = true;
						}
					}else{
						rs.executeSql(" select * from Workflow_formdetail where requestid ="+requestid+" and groupId="+groupName_tmp+"  order by id");
						if(rs.getCounts()<=0){
							shouldHidden = true;
						}
					}
				}
				if(shouldHidden == true){
					//明细字段不显示，包括标题栏。这里用JS方法把它隐藏掉
					jsStr.append("\n");
					jsStr.append("").append("function doHiddenDetail"+groupCount+"(){").append("\n");
					jsStr.append("\t").append("try{").append("\n");
					jsStr.append("\t\t").append("jQuery(\"#table"+groupCount+"button\").hide();").append("\n");
					jsStr.append("\t\t").append("jQuery(\"#div"+groupCount+"button\").hide();").append("\n");
					jsStr.append("\t\t").append("jQuery(\"#oTable"+groupCount+"\").hide();").append("\n");
					jsStr.append("\t").append("}catch(e){}").append("\n");
					jsStr.append("").append("}").append("\n");

					jsStr.append("\t").append("window.attachEvent(\"onload\", doHiddenDetail"+groupCount+");").append("\n");
					groupCount++;
					continue;
				}

				if(isviewonly==1 || !"1".equals(dtladd)){//把添加按钮删掉
					int pos_tmp = htmlLayout.toLowerCase().indexOf("$addbutton"+groupid_tmp+"$");
					while(pos_tmp > -1){
						String content1_tmp = wfformhtml.substring(0, pos_tmp);
						String content2_tmp = wfformhtml.substring(pos_tmp+1);
						int pos1 = content1_tmp.lastIndexOf("<");
						
						
						int pos2 = content2_tmp.toLowerCase().indexOf("<button");
						int pos3 = content2_tmp.toLowerCase().indexOf("</button>");
						if(pos1 > -1){
							content1_tmp = content1_tmp.substring(0, pos1);
						}
						if(pos3>-1 && (pos2==-1 || pos3<pos2)){
							content2_tmp = content2_tmp.substring(pos3+9);
						}
						wfformhtml = content1_tmp + "" + content2_tmp;
						htmlLayout = wfformhtml;
						pos_tmp = htmlLayout.toLowerCase().indexOf("$addbutton"+groupid_tmp+"$");
					}
				}
				if(isviewonly==1){//把删除按钮删掉
					int pos_tmp = htmlLayout.toLowerCase().indexOf("$delbutton"+groupid_tmp+"$");
					while(pos_tmp > -1){
						String content1_tmp = wfformhtml.substring(0, pos_tmp);
						String content2_tmp = wfformhtml.substring(pos_tmp+1);
						int pos1 = content1_tmp.lastIndexOf("<");
						
						
						int pos2 = content2_tmp.toLowerCase().indexOf("<button");
						int pos3 = content2_tmp.toLowerCase().indexOf("</button>");
						if(pos1 > -1){
							content1_tmp = content1_tmp.substring(0, pos1);
						}
						if(pos3>-1 && (pos2==-1 || pos3<pos2)){
							content2_tmp = content2_tmp.substring(pos3+9);
						}
						wfformhtml = content1_tmp + "" + content2_tmp;
						htmlLayout = wfformhtml;
						pos_tmp = htmlLayout.toLowerCase().indexOf("$delbutton"+groupid_tmp+"$");
					}
				}

				ArrayList detailFieldList = (ArrayList)detailFieldid_hs.get("detailfieldList_"+groupid_tmp);
				//先拼新建、删除行的Js方法的头
				addJsSb.append("\n").append("function addRow"+groupid_tmp+"(groupid){").append("\n");
				delJsSb.append("\n").append("function deleteRow"+groupid_tmp+"(groupid){").append("\n");

				int nodesnum = 0;//填到下面的2个hidden的input里
				String needcheck_tmp = "";//addrow里面的新增的字段的必填控制
				boolean defshowsum = false;//是否需要合计
				//用eweaver的方式，把每个明细字段都做在一个table里，然后只要这个table里面的tr加1行就行了
				addJsSb.append("\n").append("var initDetailfields=\"\";").append("\n");
				if(detailFieldList!=null && detailFieldList.size()>0){
					//这里取出已有的明细字段的值
					if(iscreate == 0){
						if(isbill == 0){
							rs_oldDetail.executeSql("select * from Workflow_formdetail where requestid ="+requestid+"  and groupId="+groupid_tmp+" order by id");
						}else{
							if(billtablename.indexOf("formtable_main_")==0 && groupName_tmp.indexOf("formtable_main_")==0){//新表单
								rs_oldDetail.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid+" order by b.id");
							}else if(formid < 0){		//数据中心模块创建的明细报表
								rs_oldDetail.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid+" order by b.inputid");
							}else{
								rs_oldDetail.executeSql("select b.* from "+billtablename+" a,"+groupName_tmp+" b where a.id=b.mainid and a.requestid ="+requestid);
							}
						}
					}
					int pos = -1;
					//这里先循环一次，找出哪个字段才是第一个明细字段
					int firstDetailFieldid = 0;
					for(int i=0; i<detailFieldList.size(); i++){
						int fieldid_tmp = Util.getIntValue((String)detailFieldList.get(i), 0);
						int pos_tmp = htmlLayout.toLowerCase().indexOf("$field"+fieldid_tmp+"$");
						if(pos_tmp > -1){
							if(pos == -1){
								pos = pos_tmp;
								firstDetailFieldid = fieldid_tmp;
							}else{
								if(pos_tmp < pos){
									pos = pos_tmp;
									firstDetailFieldid = fieldid_tmp;
								}
							}
						}
						if(defshowsum == false){
							if(colCalAry.indexOf("detailfield_"+fieldid_tmp) > -1){
								defshowsum = true;
							}
						}
					}
					otherPara_hs.put("firstDetailFieldid", ""+firstDetailFieldid);
					//这里拼方法的开始内容
					addJsSb.append("\t").append("var rowindex = parseInt(document.getElementById(\"indexnum"+groupid_tmp+"\").value);").append("\n");
					addJsSb.append("\t").append("var curindex = parseInt(document.getElementById(\"nodesnum"+groupid_tmp+"\").value);").append("\n");
					addJsSb.append("\t").append("if(document.getElementById('submitdtlid"+groupid_tmp+"').value==''){").append("\n");
					addJsSb.append("\t\t").append("document.getElementById('submitdtlid"+groupid_tmp+"').value=rowindex;").append("\n");
					addJsSb.append("\t").append("}else{").append("\n");
					addJsSb.append("\t\t").append("document.getElementById('submitdtlid"+groupid_tmp+"').value+=\",\"+rowindex;").append("\n");
					addJsSb.append("\t").append("}").append("\n");
					addJsSb.append("\t").append("var oRow;").append("\n");
					addJsSb.append("\t").append("var oCell;").append("\n");
					addJsSb.append("\t").append("var oDiv;").append("\n");
					addJsSb.append("\t").append("var sHtml;").append("\n");

					delJsSb.append("\t").append("try{").append("\n");
					delJsSb.append("\t\t").append("var oTable=document.all('detailFieldTable"+firstDetailFieldid+"');").append("\n");
					delJsSb.append("\t\t").append("var len = document.forms[0].elements.length;").append("\n");
					delJsSb.append("\t\t").append("var curindex=parseInt(document.all(\"nodesnum\"+groupid).value);").append("\n");
					delJsSb.append("\t\t").append("var i=0;").append("\n");
					delJsSb.append("\t\t").append("var rowsum1 = 0;").append("\n");
					delJsSb.append("\t\t").append("var objname = \"check_node_\"+groupid;").append("\n");
					delJsSb.append("\t\t").append("for(i=len-1; i >= 0;i--) {").append("\n");
					delJsSb.append("\t\t\t").append("if (document.forms[0].elements[i].name==objname){").append("\n");
					delJsSb.append("\t\t\t\t").append("rowsum1 += 1;").append("\n");
					delJsSb.append("\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t").append("}").append("\n");
					delJsSb.append("\t\t").append("for(i=len-1; i>=0; i--) {").append("\n");
					delJsSb.append("\t\t\t").append("if(document.forms[0].elements[i].name==objname){").append("\n");
					//delJsSb.append("\t\t\t\t").append("rowsum1--;").append("\n");
					delJsSb.append("\t\t\t\t").append("if(document.forms[0].elements[i].checked==true){").append("\n");

					delJsSb.append("\t\t\t\t\t").append("var nodecheckObj;").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("var delid;").append("\n");
					delJsSb.append("\t\t\t\t\t").append("try{").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("for(var cc=0; cc<oTable.rows(rowsum1).cells(0).children(0).children(0).children.length; cc++){").append("\n");
					
					delJsSb.append("\t\t\t\t\t\t\t").append("if(oTable.rows(rowsum1).cells(0).children(0).children(0).children(cc).tagName==\"INPUT\"){").append("\n");
					
					delJsSb.append("\t\t\t\t\t\t\t\t").append("nodecheckObj = oTable.rows(rowsum1).cells(0).children(0).children(0).children(cc);").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("delid = nodecheckObj.value;").append("\n");
					
					delJsSb.append("\t\t\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t").append("}catch(e){}").append("\n");


					delJsSb.append("\t\t\t\t\t").append("//记录被删除的旧记录 id串").append("\n");
					delJsSb.append("\t\t\t\t\t").append("if(oTable.rows(rowsum1).cells(0).children.length>0 && oTable.rows(rowsum1).cells(0).children(0).children.length>1){").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("if(document.getElementById(\"deldtlid\"+groupid).value!=''){").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("//老明细").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("document.getElementById(\"deldtlid\"+groupid).value+=\",\"+oTable.rows(rowsum1).cells(0).children(0).children(1).value;").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("}else{").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("//新明细").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("document.getElementById(\"deldtlid\"+groupid).value=oTable.rows(rowsum1).cells(0).children(0).children(1).value;").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t").append("//从提交序号串中删除被删除的行").append("\n");
					delJsSb.append("\t\t\t\t\t").append("var submitdtlidArray=document.getElementById(\"submitdtlid\"+groupid).value.split(',');").append("\n");
					delJsSb.append("\t\t\t\t\t").append("document.getElementById(\"submitdtlid\"+groupid).value=\"\";").append("\n");
					delJsSb.append("\t\t\t\t\t").append("var k;").append("\n");
					delJsSb.append("\t\t\t\t\t").append("for(k=0; k<submitdtlidArray.length; k++){").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("if(submitdtlidArray[k]!=delid){").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("if(document.getElementById(\"submitdtlid\"+groupid).value==''){").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t\t").append("document.getElementById(\"submitdtlid\"+groupid).value = submitdtlidArray[k];").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("}else{").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t\t").append("document.getElementById(\"submitdtlid\"+groupid).value += \",\"+submitdtlidArray[k];").append("\n");
					delJsSb.append("\t\t\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t\t").append("}").append("\n");
					delJsSb.append("\t\t\t\t\t").append("}").append("\n");

					pos = -1;
					String content1 = "";
					String content2 = "";
					for(int i=0; i<detailFieldList.size(); i++){
						int fieldid_tmp = Util.getIntValue((String)detailFieldList.get(i), 0);
						String fieldname_tmp = Util.null2String((String)fieldname_hs.get("fieldname"+fieldid_tmp));
						int fieldhtmltype_tmp = Util.getIntValue((String)fieldhtmltype_hs.get("fieldhtmltype"+fieldid_tmp), 0);
						int type_tmp = Util.getIntValue((String)fieldtype_hs.get("fieldtype"+fieldid_tmp), 0);
						String fielddbtype_tmp = Util.null2String((String)fielddbtype_hs.get("fielddbtype"+fieldid_tmp));
						int isview_tmp = Util.getIntValue((String)isview_hs.get("isview"+fieldid_tmp), 0);
						int isedit_tmp = Util.getIntValue((String)isedit_hs.get("isedit"+fieldid_tmp), 0);
						int ismand_tmp = Util.getIntValue((String)ismand_hs.get("ismand"+fieldid_tmp), 0);
						if(mustNoEdit == 1){
							isedit_tmp = 0;
							ismand_tmp = 0;
						}
						String fieldlabel_tmp = Util.null2String((String)fieldlabel_hs.get("fieldlabel"+fieldid_tmp));
						int fieldlength_tmp = 0;
						int fieldheight_tmp = 4;
						if(fieldhtmltype_tmp==1 && type_tmp==1){//单文本中的文本
							if((fielddbtype_tmp.toLowerCase()).indexOf("varchar")>-1){
								fieldlength_tmp = Util.getIntValue(fielddbtype_tmp.substring(fielddbtype_tmp.indexOf("(")+1, fielddbtype_tmp.length()-1));
							}
						}else if(fieldhtmltype_tmp == 2){//多行文本框
							if(isbill == 0){
								rs_tmp.execute("select textheight from workflow_formdictdetail where id="+fieldid_tmp);
							}else{
								rs_tmp.execute("select textheight from workflow_billfield where id="+fieldid_tmp+" and billid="+formid);
							}
							if(rs_tmp.next()){
								fieldheight_tmp = Util.getIntValue(rs_tmp.getString("textheight"), 4);
								otherPara_hs.put("fieldheight", ""+fieldheight_tmp);
							}
						}
						otherPara_hs.put("fielddbtype", fielddbtype_tmp);
						pos = htmlLayout.toLowerCase().indexOf("$field"+fieldid_tmp+"$");
						if(pos > -1){
							content1 = wfformhtml.substring(0, pos);
							content2 = wfformhtml.substring(pos+1);
							int pos1 = content1.lastIndexOf("<");
							int pos2 = content2.indexOf(">");
							if(pos1 > -1){
								content1 = content1.substring(0, pos1);
							}
							if(pos2 > -1){
								content2 = content2.substring(pos2+1);
							}
							//这里拼字段
							String inputStr_tmp = "\n<table class=\"ListStyle\" id=\"detailFieldTable"+fieldid_tmp+"\" name=\"detailFieldTable"+fieldid_tmp+"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" >\n";
							inputStr_tmp += "<tbody>\n";
							inputStr_tmp += "<tr height=\"0\"><td>"+"</td></tr>\n";
							rs_oldDetail.beforFirst();
							boolean isttLight = false;
							submitdtlid = "";
							derecorderindex = 0;
							//这里取以前已经有的明细字段的值
							while(rs_oldDetail.next()){
								String fieldvalue_tmp = Util.null2String(rs_oldDetail.getString(fieldname_tmp));
								isttLight = !isttLight;
								if(isttLight == true){
									inputStr_tmp += "<tr class=\"datalight\"><td class=\"field\" style=\"border-bottom:1px #ffffff solid\">";
								}else{
									inputStr_tmp += "<tr class=\"datadark\"><td class=\"field\" style=\"border-bottom:1px #ffffff solid\">";
								}
								if(ismand_tmp==1 && fieldhtmltype_tmp!=4){
									needcheck += ",field" + fieldid_tmp + "_"+derecorderindex+"";
								}
								if(firstDetailFieldid == fieldid_tmp){
									inputStr_tmp += "<div id=\"firstFielddiv"+fieldid_tmp+"\"><span name=\"detailCheckSpan"+groupid_tmp+"\"><input type=\"checkbox\" name=\"check_node_"+groupid_tmp+"\" value=\""+derecorderindex+"\" ";
									if(isviewonly==1 || !"1".equals(dtldelete)){
										inputStr_tmp += " disabled ";
									}
									inputStr_tmp += ">&nbsp;&nbsp;</span>";
									inputStr_tmp += "<input type=\"hidden\" name=\"dtl_id_"+groupid_tmp+"_"+derecorderindex+"\" value=\""+rs_oldDetail.getString("id")+"\">";
									inputStr_tmp += "<span name=\"detailIndexSpan"+groupid_tmp+"\" style=\"width:20px;padding-top:2px;\">"+(derecorderindex+1)+"</span>";
								}
								try {
									int isedit_value = isedit_tmp;
									if(!"1".equals(dtledit)){
										isedit_value = 0;
									}
									otherPara_hs.put("derecorderindex", ""+derecorderindex);
									object = (HtmlElement)Class.forName(fieldTypeComInfo.getClassname(""+fieldhtmltype_tmp)).newInstance();
									Hashtable ret_hs = object.getHtmlElementString(fieldid_tmp, fieldname_tmp, type_tmp, fieldlabel_tmp, fieldlength_tmp, 1, groupid_tmp, fieldvalue_tmp, isviewonly, 1, isedit_value, ismand_tmp, user, otherPara_hs);
									inputStr_tmp += Util.null2String((String)ret_hs.get("inputStr"));
									if(firstDetailFieldid == fieldid_tmp){
										inputStr_tmp += "</div>";
									}
									addJsStr += Util.null2String((String)ret_hs.get("addJsStr"));
									hiddenElementStr += Util.null2String((String)ret_hs.get("hiddenElementStr"));
									//retStr += inputStr_tmp;
									wfformhtml = content1 + inputStr_tmp + content2;
									htmlLayout = wfformhtml;
									String jsStr_t = Util.null2String((String)ret_hs.get("jsStr"));
									jsStr.append("\n").append(jsStr_t).append("\n");
								}catch(Exception e){
									writeLog(e);
								}
								inputStr_tmp += "</td></tr>\n";
								if("".equals(submitdtlid)){
									submitdtlid = ""+derecorderindex;
								}else{
									submitdtlid += ","+derecorderindex;
								}
								derecorderindex++;
							}

							nodesnum = derecorderindex;
							inputStr_tmp += "\n</tbody>\n";

							//需要合计的话，在这里多加一个<tfoot> Start
							if(defshowsum){
								inputStr_tmp += "\n<tfoot>\n";
								inputStr_tmp += "<tr class=\"header\">\n";
								if(firstDetailFieldid == fieldid_tmp){
									inputStr_tmp += "<td>"+SystemEnv.getHtmlLabelName(358,user.getLanguage())+"\n";
								}else{
									inputStr_tmp += "<td>\n";
								}
								inputStr_tmp += "<span id=\"sum"+fieldid_tmp+"\" style=\"color:#ff0000\"></span>\n";
								inputStr_tmp += "<input type=\"hidden\" id=\"sumvalue"+fieldid_tmp+"\" name=\"sumvalue"+fieldid_tmp+"\">";
								inputStr_tmp += "</td>\n";
								inputStr_tmp += "</tr>\n";
								inputStr_tmp += "\n</tfoot>\n";
							}
							//需要合计的话，在这里多加一个</tfoot> End

							inputStr_tmp += "</table>";

							wfformhtml = content1 + inputStr_tmp + content2;
							htmlLayout = wfformhtml;

							if(isviewonly==0){//不是仅查看的情况下才去拼JS方法。方法头那里不去控制了，下面的控制下，节约速度
								if(ismand_tmp==1 && fieldhtmltype_tmp!=4){
									needcheck_tmp += ",field" + fieldid_tmp + "_\"+rowindex+\"";
								}
								//这里拼JS方法
								//addRow
								addJsSb.append("\t").append("").append("\n");
								addJsSb.append("\t").append("oRow = detailFieldTable"+fieldid_tmp+".insertRow(curindex+1);").append("\n");
								addJsSb.append("\t").append("oCell = oRow.insertCell(-1);").append("\n");
								addJsSb.append("\t").append("oCell.className = \"detailAddTd\";").append("\n");
								addJsSb.append("\t").append("oDiv = document.createElement(\"div\");").append("\n");
								addJsSb.append("\t").append("sHtml=\"\";").append("\n");
								if(firstDetailFieldid == fieldid_tmp){
									addJsSb.append("\t").append("sHtml += \"<span name='detailCheckSpan"+groupid_tmp+"'><input type='checkbox' name='check_node_"+groupid_tmp+"' value='\"+rowindex+\"'>&nbsp;&nbsp;</span>\";").append("\n");
									addJsSb.append("\t").append("sHtml += \"<input type='hidden' name='dtl_id_"+groupid_tmp+"' value=''>\";").append("\n");
									addJsSb.append("\t").append("sHtml += \"<span name='detailIndexSpan"+groupid_tmp+"' style='width:20px;padding-top:2px;'>\"+(curindex+1)+\"</span>\";").append("\n");
								}
								//addJsSb.append("\t").append("sHtml += \""+fieldid_tmp+"+\"+curindex;").append("\n");//该句为开发调试使用

								//这里获得字段用于AddRow的sHtml的值
								try {
									String fieldvalue_tmp = "";
									String inoperatefield_tmp = Util.null2String((String)inoperatefield_hs.get("inoperatefield"+fieldid_tmp));
									if("1".equals(inoperatefield_tmp)){
										fieldvalue_tmp = Util.null2String((String)fieldvalue_hs.get("inoperatevalue"+fieldid_tmp));
									}else{//没有设置节点前附加操作
										if(fieldhtmltype_tmp == 3){
											if((type_tmp==8 || type_tmp==135) && !prjid.equals("")){		//浏览按钮为项目,从前面的参数中获得项目默认值
												fieldvalue_tmp = "" + Util.getIntValue(prjid,0);
											}else if((type_tmp==9 || type_tmp==37) && !docid.equals("")){	//浏览按钮为文档,从前面的参数中获得文档默认值
												fieldvalue_tmp = "" + Util.getIntValue(docid,0);
											}else if((type_tmp==1 ||type_tmp==17||type_tmp==165||type_tmp==166) && !hrmid.equals("")){	//浏览按钮为人,从前面的参数中获得人默认值
												fieldvalue_tmp = "" + Util.getIntValue(hrmid,0);
											}else if((type_tmp==7 || type_tmp==18) && !crmid.equals("")){	//浏览按钮为CRM,从前面的参数中获得CRM默认值
												fieldvalue_tmp = "" + Util.getIntValue(crmid,0);
											}else if((type_tmp==16 || type_tmp==152 || type_tmp==171) && !reqid.equals("")){	//浏览按钮为REQ,从前面的参数中获得REQ默认值
												fieldvalue_tmp = "" + Util.getIntValue(reqid,0);
											}else if((type_tmp==4 || type_tmp==57 || type_tmp==167 || type_tmp==168) && !hrmid.equals("")){ //浏览按钮为部门,从前面的参数中获得人默认值(由人力资源的部门得到部门默认值)
												fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getDepartmentID(hrmid),0);
											}else if(type_tmp==24 && !hrmid.equals("")){ //浏览按钮为职务,从前面的参数中获得人默认值(由人力资源的职务得到职务默认值)
												fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getJobTitle(hrmid),0);
											}else if(type_tmp==32 && !hrmid.equals("")){ //浏览按钮为职务,从前面的参数中获得人默认值(由人力资源的职务得到职务默认值)
												fieldvalue_tmp = "" + Util.getIntValue(request.getParameter("TrainPlanId"),0);
											}else if((type_tmp==164 || type_tmp==169 || type_tmp==170) && !hrmid.equals("")){ //浏览按钮为分部,从前面的参数中获得人默认值(由人力资源的分部得到分部默认值)
												fieldvalue_tmp = "" + Util.getIntValue(resourceComInfo.getSubCompanyID(hrmid),0);
											}else if(type_tmp == 2){//日期
												fieldvalue_tmp = TimeUtil.getCurrentDateString();
											}else if(type_tmp == 19){//时间
												fieldvalue_tmp = TimeUtil.getCurrentTimeString().substring(11, 16);
											}
										}
									}
									int isedit_value = isedit_tmp;
									otherPara_hs.put("derecorderindex", "\"+rowindex+\"");
									object = (HtmlElement)Class.forName(fieldTypeComInfo.getClassname(""+fieldhtmltype_tmp)).newInstance();
									//这里的isviewonly肯定是0
									Hashtable ret_hs = object.getHtmlElementString(fieldid_tmp, fieldname_tmp, type_tmp, fieldlabel_tmp, fieldlength_tmp, 1, groupid_tmp, fieldvalue_tmp, 0, 1, isedit_value, ismand_tmp, user, otherPara_hs);
									String addRowElement = Util.null2String((String)ret_hs.get("inputStr"));
									addJsSb.append("\t").append("sHtml += \""+addRowElement+"\";").append("\n");
									addJSExt += Util.null2String((String)ret_hs.get("jsStr"));//类似SQL初始化之类的JS操作
									addJsStr += Util.null2String((String)ret_hs.get("addJsStr"));
									hiddenElementStr += Util.null2String((String)ret_hs.get("hiddenElementStr"));
								}catch(Exception e){
									writeLog(e);
								}
								//addJsSb.append("\t").append("sHtml += \"&nbsp;\";").append("\n");
								addJsSb.append("\t").append("sHtml += \"&nbsp;\";").append("\n");
								addJsSb.append("\t").append("oDiv.innerHTML = sHtml;").append("\n");
								addJsSb.append("\t").append("oCell.appendChild(oDiv);").append("\n");

								if (trrigerdetailfield.indexOf("field"+fieldid_tmp)>=0){
									addJsSb.append("\t").append("initDetailfields+=\"field"+fieldid_tmp+"_\"+rowindex+\",\"").append("\n");
								}

								//deleteRow
								delJsSb.append("\t\t\t\t\t").append("detailFieldTable"+fieldid_tmp+".deleteRow(rowsum1);").append("\n");
							}
						}
					}
				}
				addJsSb.append("\t").append("datainputd(initDetailfields);").append("\n");
				addJsSb.append("\t").append("document.getElementById(\"indexnum"+groupid_tmp+"\").value = rowindex*1 + 1;").append("\n");
				addJsSb.append("\t").append("document.getElementById(\"nodesnum"+groupid_tmp+"\").value = curindex*1 + 1;").append("\n");
				addJsSb.append("\t").append(addJsStr).append("\n");
				//必填控制
				if(!"".equals(needcheck_tmp)){
					addJsSb.append("\t").append("document.all(\"needcheck\").value += \","+needcheck_tmp+"\";").append("\n");
				}

				addJsSb.append("\t").append("try{").append("\n");
				addJsSb.append("\t\t").append(addJSExt).append("\n");
				addJsSb.append("\t").append("}catch(e){}").append("\n");

				addJsSb.append("\t").append("try{").append("\n");
				addJsSb.append("\t\t").append("calSum(groupid);").append("\n");
				addJsSb.append("\t").append("}catch(e){}").append("\n");

				addJsSb.append("}").append("\n");//addRow终结

				delJsSb.append("\t\t\t\t\t").append("curindex--;").append("\n");
				delJsSb.append("\t\t\t\t").append("}").append("\n");
				delJsSb.append("\t\t\t\t").append("rowsum1--;").append("\n");
				delJsSb.append("\t\t\t").append("}").append("\n");
				delJsSb.append("\t\t").append("}").append("\n");
				delJsSb.append("\t\t").append("document.getElementById(\"nodesnum\"+groupid).value=curindex;").append("\n");
				delJsSb.append("\t\t\t").append("calSum(groupid);").append("\n");
				//delJsSb.append("\t").append("}catch(e){alert(e);}").append("\n");//开发调试时使用。正式使用用下面那句
				delJsSb.append("\t").append("}catch(e){}").append("\n");
				//修改序号 Start
				delJsSb.append("\t").append("try{").append("\n");
				delJsSb.append("\t\t").append("var indexNum = jQuery(\"span[name='detailIndexSpan"+groupid_tmp+"']\").length;").append("\n");
				delJsSb.append("\t\t").append("for(var k=1; k<=indexNum; k++){").append("\n");
				delJsSb.append("\t\t\t").append("jQuery(\"span[name='detailIndexSpan"+groupid_tmp+"']\").get(k-1).innerHTML = k;").append("\n");
				delJsSb.append("\t\t").append("}").append("\n");
				delJsSb.append("\t").append("}catch(e){}").append("\n");
				//修改序号 End

				delJsSb.append("}").append("\n");//deleteRow终结
				jsStr.append(addJsSb.toString()).append("\n").append(delJsSb.toString()).append("\n");
				//先这样写，等处理编辑页面时再修改
				htmlHiddenElementsb.append("<input type=\"hidden\" id=\"nodesnum"+groupid_tmp+"\" name=\"nodesnum"+groupid_tmp+"\" value=\""+nodesnum+"\">").append("\n");
				htmlHiddenElementsb.append("<input type=\"hidden\" id=\"indexnum"+groupid_tmp+"\" name=\"indexnum"+groupid_tmp+"\" value=\""+nodesnum+"\">").append("\n");
				htmlHiddenElementsb.append("<input type=\"hidden\" id=\"submitdtlid"+groupid_tmp+"\" name=\"submitdtlid"+groupid_tmp+"\" value=\""+submitdtlid+"\">").append("\n");
				htmlHiddenElementsb.append("<input type=\"hidden\" id=\"deldtlid"+groupid_tmp+"\" name=\"deldtlid"+groupid_tmp+"\" value=\"\">").append("\n");
				htmlHiddenElementsb.append("<input type=\"hidden\" id=\"colcalnames\" name=\"colcalnames\" value=\"\">").append("\n");
				htmlHiddenElementsb.append(hiddenElementStr).append("\n");
				groupCount++;
			}

			//这里拼所有明细字段组公用的JS
			ArrayList rowCalAry = new ArrayList();
			ArrayList rowCalSignAry = new ArrayList();
			ArrayList mainCalAry = new ArrayList();
			ArrayList tmpAry = null;

			StringTokenizer stk2 = new StringTokenizer(rowCalItemStr1,";");

			ArrayList newRowCalArray = new ArrayList();

			while(stk2.hasMoreTokens()){
				rowCalAry.add(stk2.nextToken(";"));
			}
			stk2 = new StringTokenizer(mainCalStr1,";");
			while(stk2.hasMoreTokens()){
				mainCalAry.add(stk2.nextToken(";"));
			}
			//jsStr.append("\t").append("").append("\n");

			
			jsStr.append("").append("rowindex = "+derecorderindex+";").append("\n");
			jsStr.append("").append("curindex = "+derecorderindex+";").append("\n");

			jsStr.append("").append("function calSumPrice(){").append("\n");
			jsStr.append("\t").append("try{").append("\n");
			jsStr.append("\t").append("var temv1;").append("\n");
			String temStr = "";
			for(int i=0; i<rowCalAry.size(); i++){
				temStr = "";
				String calExp = (String)rowCalAry.get(i);
				ArrayList calExpList=DynamicDataInput.FormatString(calExp);
				jsStr.append("\t").append("try{").append("\n");
				jsStr.append("\t\t").append("var i;").append("\n");
				jsStr.append("\t\t").append("try{").append("\n");
				jsStr.append("\t\t\t").append("var nowobj=window.event.srcElement.name.toString();").append("\n");
				jsStr.append("\t\t\t").append("if(nowobj.indexOf('_')>-1){").append("\n");
				jsStr.append("\t\t\t\t").append("i=nowobj.substring(nowobj.indexOf('_')+1);").append("\n");
				jsStr.append("\t\t\t").append("}").append("\n");
				jsStr.append("\t\t").append("}catch(e){").append("\n");
				jsStr.append("\t\t\t").append("i = rowindexAll;").append("\n");
				jsStr.append("\t\t").append("}").append("\n");
				for(int j=0;j<calExpList.size();j++){
					calExp=(String)calExpList.get(j);
					String targetStr="";
					if(calExp.indexOf("innerHTML")>0){
						targetStr=calExp.substring(0,calExp.indexOf("innerHTML")-1);
						jsStr.append("\t\t").append("if("+targetStr+"){").append("\n");
						jsStr.append("\t\t").append(calExp+"; ").append("\n");
						jsStr.append("\t\t").append("if("+calExp.substring(0,calExp.indexOf("innerHTML")-9)+").datatype=='int') "+calExp.substring(0,calExp.indexOf("="))+"=toPrecision("+calExp.substring(0,calExp.indexOf("innerHTML")-9)+").value,0);else "+calExp.substring(0,calExp.indexOf("="))+"=toPrecision("+calExp.substring(0,calExp.indexOf("innerHTML")-9)+").value,3);}").append("\n");
					}else{
						if(calExp.indexOf("value")>0){  
							targetStr =calExp.substring(0,calExp.indexOf("value")-1);
							jsStr.append("\t\t").append("if("+targetStr+"){").append("\n");
							jsStr.append("\t\t").append(calExp+"; ").append("\n");
							jsStr.append("\t\t").append("if("+calExp.substring(0,calExp.indexOf("value")-1)+".datatype=='int') "+calExp.substring(0,calExp.indexOf("="))+"=toPrecision("+calExp.substring(0,calExp.indexOf("="))+",0);else "+calExp.substring(0,calExp.indexOf("="))+"=toPrecision("+calExp.substring(0,calExp.indexOf("="))+",3);}").append("\n");
						}
					}
				}
				jsStr.append("\t").append("}catch(e){}").append("\n");
			}
			jsStr.append("\t").append("}catch(e){}").append("\n");
			jsStr.append("").append("}").append("\n");

			jsStr.append("").append("function calMainField(obj){").append("\n");
			jsStr.append("\t").append("try{").append("\n");
			jsStr.append("\t").append("var rows=0;").append("\n");
			for(int i=0;i<groupCount;i++){
				jsStr.append("\t").append("var temprow=parseInt(document.all('indexnum"+i+"').value);").append("\n");
				jsStr.append("\t").append("if(temprow>rows) rows=temprow;").append("\n");
			}
			jsStr.append("\t").append("if(rowindex<rows){").append("\n");
			jsStr.append("\t\t").append("rowindex=rows;").append("\n");
			jsStr.append("\t").append("}").append("\n");	
			for(int i=0;i<mainCalAry.size();i++){
				String str2 =  mainCalAry.get(i).toString();
				int idx = str2.indexOf("=");
				String str3 = str2.substring(0,idx);
				str3 = str3.substring(str3.indexOf("_")+1);
				String str4 = str2.substring(idx);
				str4 = str4.substring(str4.indexOf("_")+1);
				
				jsStr.append("\t").append("var sum=0;").append("\n");
				jsStr.append("\t").append("var temStr;").append("\n");
				jsStr.append("\t").append("for(i=0; i<rowindex; i++){").append("\n");

				jsStr.append("\t\t").append("try{").append("\n");
				jsStr.append("\t\t\t").append("temStr=document.all(\"field"+str4+"_\"+i).value;").append("\n");
				jsStr.append("\t\t\t").append("if(temStr+\"\"!=\"\"){").append("\n");
				jsStr.append("\t\t\t\t").append("sum+=temStr*1;").append("\n");
				jsStr.append("\t\t\t").append("}").append("\n");
				jsStr.append("\t\t").append("}catch(e){;}").append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("\t\t").append("if(document.all(\"field"+str3+"\")){").append("\n");
				jsStr.append("\t\t").append("if(document.all(\"field"+str3+"\").datatype+''==\"int\")").append("\n");
				jsStr.append("\t\t\t").append("document.all(\"field"+str3+"\").value=toPrecision(sum,0);").append("\n");
				jsStr.append("\t\t").append("else").append("\n");
				jsStr.append("\t\t\t").append("document.all(\"field"+str3+"\").value=toPrecision(sum,3);").append("\n");
				jsStr.append("\t\t").append("}").append("\n");
				jsStr.append("\t\t").append("if(document.all(\"field"+str3+"span\")){").append("\n");
				jsStr.append("\t\t\t").append("if(document.all(\"field"+str3+"\")&&document.all(\"field"+str3+"\").type==\"text\"){").append("\n");
				jsStr.append("\t\t\t\t").append("document.all(\"field"+str3+"span\").innerHTML=\"\";").append("\n");
				jsStr.append("\t\t\t").append("}else{").append("\n");
				jsStr.append("\t\t\t\t").append("if(document.all(\"field"+str3+"\").datatype+''==\"int\"){").append("\n");
				jsStr.append("\t\t\t\t\t").append("document.all(\"field"+str3+"span\").innerHTML=toPrecision(sum,0);").append("\n");
				jsStr.append("\t\t\t\t").append("}else{").append("\n");
				jsStr.append("\t\t\t\t\t").append("document.all(\"field"+str3+"span\").innerHTML=toPrecision(sum,3);").append("\n");
				jsStr.append("\t\t\t\t").append("}").append("\n");
				jsStr.append("\t\t\t").append("}").append("\n");
				jsStr.append("\t\t").append("}").append("\n");
			}
			jsStr.append("\t").append("}catch(e){}").append("\n");
			jsStr.append("").append("}").append("\n");

			jsStr.append("").append("function calSum(obj){").append("\n");
			jsStr.append("\t").append("try{").append("\n");
			jsStr.append("\t").append("calSumPrice();").append("\n");
			jsStr.append("\t").append("var rows=0;").append("\n");
			jsStr.append("\t").append("var temprow=parseInt(document.getElementById('indexnum'+obj).value);").append("\n");
			jsStr.append("\t").append("if(temprow>rows){rows=temprow;}").append("\n");
			jsStr.append("\t").append("if(rowindex<rows){").append("\n");
			jsStr.append("\t\t").append("rowindex=rows;").append("\n");
			jsStr.append("\t").append("}").append("\n");
			jsStr.append("\t").append("var sum=0;").append("\n");
			jsStr.append("\t").append("var temStr;").append("\n");
			
			for(int i=0; i<colCalAry.size(); i++){
				String str = colCalAry.get(i).toString();
				str = str.substring(str.indexOf("_")+1);
			
				jsStr.append("\t").append("sum=0;").append("\n");
				jsStr.append("\t").append("for(i=0; i<rowindex; i++){").append("\n");
				jsStr.append("\t\t").append("try{").append("\n");
				jsStr.append("\t\t\t").append("temStr=document.all(\"field"+str+"_\"+i).value;").append("\n");
				jsStr.append("\t\t\t").append("if(temStr+\"\"!=\"\"){").append("\n");
				jsStr.append("\t\t\t\t").append("sum+=temStr*1;").append("\n");
				jsStr.append("\t\t\t").append("}").append("\n");
				jsStr.append("\t\t").append("}catch(e){}").append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("\t\t").append("if(document.all(\"sum"+str+"\")){").append("\n");
				jsStr.append("\t\t").append("document.all(\"sum"+str+"\").innerHTML=toPrecision(sum,3)+\"&nbsp;\";").append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("\t").append("if(document.all(\"sumvalue"+str+"\")){").append("\n");
				jsStr.append("\t\t").append("document.all(\"sumvalue"+str+"\").value=toPrecision(sum,3);").append("\n");
				jsStr.append("\t").append("}").append("\n");
			}

			jsStr.append("\t").append("calMainField(obj);").append("\n");
			jsStr.append("\t").append("}catch(e){}").append("\n");
			jsStr.append("").append("}").append("\n");

			if(iscreate == 0){
				for(int i=0; i<groupCount; i++){
					jsStr.append("").append("calSum("+i+");").append("\n");
				}
				jsStr.append("").append("setTimeout(\"doTriggerDetailInit()\",1000);").append("\n");
				jsStr.append("").append("function doTriggerDetailInit(){").append("\n");
				jsStr.append("\t").append("try{").append("\n");
				jsStr.append("\t").append("var tempS = \""+trrigerdetailfield+"\";").append("\n");
				jsStr.append("\t").append("var tempA = \"\";").append("\n");
				jsStr.append("\t").append("if(tempS.length>0){").append("\n");
				jsStr.append("\t\t").append("tempA = tempS.split(\",\");").append("\n");
				jsStr.append("\t\t").append("for(var i=0;i<tempA.length;i++){").append("\n");
				jsStr.append("\t\t\t").append("datainputd(tempA[i]);").append("\n");
				jsStr.append("\t\t").append("}").append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("\t").append("}catch(e){}").append("\n");
				jsStr.append("").append("}").append("\n");
			}
		}catch(Exception e){
			writeLog(e);
		}
	}

	public void getFieldAttr(){

		ArrayList sqlfieldidList = new ArrayList();
		ArrayList sqlcontentList = new ArrayList();
		ArrayList attrfieldidList = new ArrayList();
		ArrayList attrcontentList = new ArrayList();
		RecordSet rs = new RecordSet();
		try{
			ResourceComInfo resourceComInfo = new ResourceComInfo();
			int requestid = Util.getIntValue(request.getParameter("requestid"), 0);
			int nodeid = Util.getIntValue(request.getParameter("nodeid"), 0);
			int creater = user.getUID();
			int credept = Util.getIntValue(resourceComInfo.getDepartmentID(""+creater), 0);
			int currentuser = user.getUID();
			int currentdept = Util.getIntValue(resourceComInfo.getDepartmentID(""+currentuser), 0);
			if(iscreate == 0){
				HttpSession session = (HttpSession)request.getSession(false);
				creater = Util.getIntValue((String)session.getAttribute(user.getUID()+"_"+requestid+"creater"), 0);
				credept = Util.getIntValue(resourceComInfo.getDepartmentID(""+creater), 0);
			}
			ArrayList paraList = new ArrayList();
			paraList.add("currentuser");
			paraList.add("currentdept");
			paraList.add("wfcreater");
			paraList.add("wfcredept");
			ArrayList valueList = new ArrayList();
			valueList.add(""+currentuser);
			valueList.add(""+currentdept);
			valueList.add(""+creater);
			valueList.add(""+credept);
			//获得字段SQL属性
			rs.execute("select * from workflow_nodefieldattr where nodeid="+nodeid);
			while(rs.next()){
				int fieldid_tmp = Util.getIntValue(rs.getString("fieldid"), 0);
				String sqlcontent_tmp = Util.null2String(rs.getString("attrcontent")).trim();
				int index = sqlcontent_tmp.indexOf("doFieldSQL(\"");
				if(index > -1){
					sqlcontent_tmp = analyzeSql(sqlcontent_tmp, paraList, valueList);
					sqlfieldidList.add(""+fieldid_tmp);
					sqlcontentList.add(sqlcontent_tmp);
					sqlcontent_tmp = sqlcontent_tmp.substring(index+12);
					index = sqlcontent_tmp.lastIndexOf("\")");
					if(index > -1){
						sqlcontent_tmp = sqlcontent_tmp.substring(0, index);
						sqlcontent_tmp = sqlcontent_tmp.trim();
					}
					sqlcontent_tmp = sqlcontent_tmp.trim();
					htmlHiddenElementsb.append("\n\t<input type=\"hidden\" id=\"fieldsql"+fieldid_tmp+"\" name=\"fieldsql"+fieldid_tmp+"\" value=\"").append(sqlcontent_tmp.replaceAll("\"", "&quot;")).append("\">");
				}else{
					index = sqlcontent_tmp.indexOf("doFieldMath(\"");
					if(index > -1){
						sqlcontent_tmp = sqlcontent_tmp.substring(index+13);
						index = sqlcontent_tmp.lastIndexOf("\")");
						if(index > -1){
							sqlcontent_tmp = sqlcontent_tmp.substring(0, index);
							sqlcontent_tmp = sqlcontent_tmp.trim();
						}
						sqlcontent_tmp = sqlcontent_tmp.trim();
						attrfieldidList.add(""+fieldid_tmp);
						attrcontentList.add(sqlcontent_tmp);
					}
				}
			}
		}catch(Exception e){
			writeLog(e);
		}
		otherPara_hs.put("sqlfieldidList", sqlfieldidList);
		otherPara_hs.put("sqlcontentList", sqlcontentList);
		otherPara_hs.put("attrfieldidList", attrfieldidList);
		otherPara_hs.put("attrcontentList", attrcontentList);
		ArrayList fieldidList = (ArrayList)otherPara_hs.get("fieldidList");
		ArrayList fieldtypeList = (ArrayList)otherPara_hs.get("fieldtypeList");
		Hashtable isedit_hs = (Hashtable)otherPara_hs.get("isedit_hs");

		//数字数值型字段之间的计算、赋值操作
		for(int i=0; i<attrfieldidList.size(); i++){
			String attrfieldid_tmp = Util.null2String((String)attrfieldidList.get(i));
			String attrcontent_tmp = Util.null2String((String)attrcontentList.get(i));
			jsStr.append("\n");
			jsStr.append("").append("function doMathFieldAttr"+attrfieldid_tmp+"(){").append("\n");
			String attrcontent_js = "";
			int indexFieldHead = attrcontent_tmp.indexOf("$");
			while(indexFieldHead > -1){
				attrcontent_js += attrcontent_tmp.substring(0, indexFieldHead);
				attrcontent_tmp = attrcontent_tmp.substring(indexFieldHead+1);
				int indexFieldTail = attrcontent_tmp.indexOf("$");
				String cFieldid = attrcontent_tmp.substring(0, indexFieldTail);
				int type_tmp = 0;
				if(fieldidList!=null && fieldtypeList!=null && fieldidList.indexOf(""+cFieldid)>-1){
					type_tmp = Util.getIntValue((String)fieldtypeList.get(fieldidList.indexOf(""+cFieldid)));
				}
				if(type_tmp == 5){//金额千分位
					attrcontent_js += ("(document.getElementById(\"field"+cFieldid+"\").value.replace(/,/g,\"\"))*1.0");
				}else{
					attrcontent_js += ("document.getElementById(\"field"+cFieldid+"\").value*1.0");
				}
				attrcontent_tmp = attrcontent_tmp.substring(indexFieldTail+1);
				indexFieldHead = attrcontent_tmp.indexOf("$");
			}
			attrcontent_js += attrcontent_tmp;
			int type_tmp = 0;
			if(fieldidList!=null && fieldtypeList!=null && fieldidList.indexOf(""+attrfieldid_tmp)>-1){
				type_tmp = Util.getIntValue((String)fieldtypeList.get(fieldidList.indexOf(""+attrfieldid_tmp)));
			}
			int isedit_tmp = 0;
			if(isedit_hs != null){
				isedit_tmp = Util.getIntValue((String)isedit_hs.get("isedit"+attrfieldid_tmp));
			}
			if(type_tmp==4){//金额转换
				if(isedit_tmp == 1){
					jsStr.append("").append("try{").append("\n");
					jsStr.append("\t").append("if(document.getElementById(\"field_lable"+attrfieldid_tmp+"\")){").append("\n");
					jsStr.append("\t\t").append("document.getElementById(\"field_lable"+attrfieldid_tmp+"\").value = ").append(attrcontent_js).append(";\n");
					jsStr.append("\t").append("}").append("\n");
					jsStr.append("").append("}catch(e){}").append("\n");
					jsStr.append("").append("try{").append("\n");
					jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\")){").append("\n");
					jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"\").value = ").append(attrcontent_js).append(";\n");
					jsStr.append("\t").append("}").append("\n");
					jsStr.append("").append("}catch(e){}").append("\n");
					jsStr.append("").append("try{").append("\n");
					jsStr.append("\t").append("numberToFormat('"+attrfieldid_tmp+"');").append(";\n");
					jsStr.append("").append("}catch(e){}").append("\n");
				}else if(isedit_tmp == 0){
					jsStr.append("").append("try{").append("\n");
					jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\")){").append("\n");
					jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"\").value = ").append(attrcontent_js).append(";\n");
					jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"span\").innerHTML = ").append("milfloatFormat(document.getElementById(\"field"+attrfieldid_tmp+"\").value);").append(";\n");
					jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"ncspan\").innerHTML = ").append("numberChangeToChinese(document.getElementById(\"field"+attrfieldid_tmp+"\").value);").append(";\n");
					jsStr.append("\t").append("}").append("\n");
					jsStr.append("").append("}catch(e){}").append("\n");
				}
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("doformat"+attrfieldid_tmp+"();").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
			}else if(type_tmp == 5){//金额千分位
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\")){").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"\").value = ").append(attrcontent_js).append(";\n");
				jsStr.append("\t").append("}else{").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"span\").innerHTML = ").append(attrcontent_js).append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
				jsStr.append("").append("changeToThousands('field"+attrfieldid_tmp+"');").append("\n");
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\").type==\"hidden\"){").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"span\").innerHTML = ").append("document.getElementById(\"field"+attrfieldid_tmp+"\").value;").append(";\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("doformat"+attrfieldid_tmp+"();").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
			}else{
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\")){").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"\").value = ").append(attrcontent_js).append(";\n");
				jsStr.append("\t").append("}else{").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"span\").innerHTML = ").append(attrcontent_js).append("\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("if(document.getElementById(\"field"+attrfieldid_tmp+"\").type==\"hidden\"){").append("\n");
				jsStr.append("\t\t").append("document.getElementById(\"field"+attrfieldid_tmp+"span\").innerHTML = ").append(attrcontent_js).append(";\n");
				jsStr.append("\t").append("}").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
				jsStr.append("").append("try{").append("\n");
				jsStr.append("\t").append("doformat"+attrfieldid_tmp+"();").append("\n");
				jsStr.append("").append("}catch(e){}").append("\n");
			}
			jsStr.append("").append("}").append("\n");
		}

	}

	/**
	 * 
	 * @param sql
	 * @param paraList	currentuser,currentdept,creater,credept
	 * @return
	 */
	public String analyzeSql(String sql, ArrayList paraList, ArrayList valueList){
		String sqlStr = sql;
		try{
			for(int i=0; i<paraList.size(); i++){
				String para = Util.null2String((String)paraList.get(i));
				String value = Util.null2String((String)valueList.get(i));
				sqlStr = sqlStr.replace("$"+para+"$", value);
			}
		}catch(Exception e){
			writeLog(e);
			sqlStr = sql;
		}
		return sqlStr;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getIscreate() {
		return iscreate;
	}
	public void setIscreate(int iscreate) {
		this.iscreate = iscreate;
	}

	public StringBuffer getJsStr() {
		return jsStr;
	}

	public void setJsStr(StringBuffer jsStr) {
		this.jsStr = jsStr;
	}

	public StringBuffer getVbsStr() {
		return vbsStr;
	}

	public void setVbsStr(StringBuffer vbsStr) {
		this.vbsStr = vbsStr;
	}

	public Hashtable getOtherPara_hs() {
		return otherPara_hs;
	}

	public void setOtherPara_hs(Hashtable otherPara_hs) {
		this.otherPara_hs = otherPara_hs;
	}

	public String getNeedcheck() {
		return needcheck;
	}

	public void setNeedcheck(String needcheck) {
		this.needcheck = needcheck;
	}


	public StringBuffer getHtmlHiddenElementsb() {
		return htmlHiddenElementsb;
	}


	public void setHtmlHiddenElementsb(StringBuffer htmlHiddenElementsb) {
		this.htmlHiddenElementsb = htmlHiddenElementsb;
	}


}
