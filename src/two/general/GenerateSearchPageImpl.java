package two.general;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.FieldViewBean;
import two.general.bean.PageInfoBean;
import weaver.conn.RecordSet;
import weaver.general.GCONST;
import weaver.general.Util;
import weaver.workflow.field.BrowserComInfo;

public class GenerateSearchPageImpl implements IGeneratePage {
	public int index = 0;
	GenerateCommon gener = new GenerateCommon();

	@Override
	public void CreateProc(String billid, BillBean billBean, String pageType) {
	}

	// �õ�ҳ������
	public Map<String, String> getOperationInfo(List<FieldInfoBean> beans,
			PageInfoBean pageBean) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	@Override
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean) {

	}

	public String getUrlArgs(String str) {
		String reStr = "";
		Matcher m = Pattern.compile("&.+?=").matcher(str);
		if (m.find()) {
			reStr = Util.null2String(m.group());
		} else if (str.lastIndexOf("?") > -1) {
			reStr = str.substring(str.lastIndexOf("?"));
		}
		return reStr;
	}

	public Map<String, String> getOtherInfo(List<FieldInfoBean> beans) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer buffer = new StringBuffer();
		StringBuffer fieldStr = new StringBuffer();
		FieldViewBean formBean = null;
		String fieldname = "";
		String fieldlabel = "";
		String fieldType = "";
		String htmltype = "";// ���ݵ��ֶε�type
		String tranmethod = "";// Ĭ��ֵ
		for (FieldInfoBean fieldBean : beans) {
			String tmpStr1 = "\tString v_XnameX = Util.null2String(request.getParameter(\"XnameX\"));//XmarkX\n";
			String tmpStr = "\t\"<col width=\\\"60\\\" text=\\\"XmarkX\\\" column=\\\"XnameX\\\" orderkey=\\\"XnameX\\\" X1X X2X/>\"+\n";
			String urlStr = "target=\\\"_blank\\\" linkkey=\\\"XkeyX\\\" linkvaluecolumn=\\\"XnameX\\\" href=\\\"XurlX\\\"";
			String tranStr = "transmethod=\\\"Xm1X\\\" Xm2X ";
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsview())) {
				fieldname = fieldBean.getFieldname();// �ֶ���
				fieldlabel = fieldBean.getFieldlabel();// ��ʾ��
				fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
				htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
				tranmethod = fieldBean.getTransmethod();// ת������
				if ("3".equals(fieldType)) {//�����
					BrowserComInfo comInfo = new BrowserComInfo();
					String linkurl = Util.null2String(comInfo
							.getLinkurl(htmltype)); // ���ֵ�����ʱ�����ӵ�url
					if (!"".equals(linkurl)) {
						String key = this.getUrlArgs(linkurl);
						linkurl = linkurl.replace(key, "");
						urlStr = urlStr.replace("XkeyX",
								key.substring(1).replace("=", "")).replace(
								"XnameX", fieldname).replace("XurlX", linkurl);
						tmpStr = tmpStr.replace("X1X", urlStr);
					}
				}
				if ("5".equals(fieldType)) {
					String fieldid = formBean.getFieldid();// �ֶ�ID
					tranStr=tranStr.replace("Xm2X", "otherpara=\\\""+fieldid+"\\\"");
					tranStr = tranStr.replace("Xm1X","two.common.CommonInfoByID.LookNameBySelectID");
					tmpStr = tmpStr.replace("X2X", tranStr);					
				}
				if (!"".equals(tranmethod)) {
					tranStr = tranStr.replace("Xm2X", "").replace("Xm1X", tranmethod.replace(
							"common.", "two.common.CommonInfoByID."));
					tmpStr = tmpStr.replace("X2X", tranStr);
				}
				tmpStr = tmpStr.replace("X1X", "").replace("X2X", "");
				if ("1".equals(formBean.getIsedit())) {// �༭ʱ��ʾ
					fieldStr.append(tmpStr1.replace("XnameX", fieldname)
							.replace("XmarkX", fieldlabel));
				}
				buffer.append(tmpStr.replace("XnameX", fieldname).replace(
						"XmarkX", fieldlabel));
			}
		}
		map.put("${colStr}", "\n" + buffer.toString());
		map.put("${fieldStr}", "\n" + fieldStr.toString());
		return map;
	}

	/**
	 * �õ�����Ϣ
	 */
	public StringBuffer getTRInfo(FieldInfoBean fieldBean,
			FieldViewBean formBean, PageInfoBean pageBean) {
		StringBuffer buffer = new StringBuffer();
		GenerateFieldContent fieldContent = new GenerateFieldContent();
		String isview = formBean.getIsview();// �Ƿ���ʾ
		String valueStr = "";
		String Suffix = "";
		String trStart = "\n<TR>\n";
		String trEnd = "</TR><TR><TD class=Line colspan=X1X></TD></TR>\n";
		String fieldlabel = fieldBean.getFieldlabel();
		String tdcontent = fieldContent.getEditFieldInfo(fieldBean, formBean,
				pageBean, valueStr, Suffix).toString();// td����
		String mainView = pageBean.getMainviewtype();
		int colspan = Util.getIntValue(mainView, 1) + 1;
		trEnd = trEnd.replace("X1X", colspan * 2 + "");// ����
		if (this.index % colspan == 0) {
			buffer.append(trStart);
		}
		this.index++;
		buffer.append(gener.getTDInfo(fieldlabel, tdcontent));
		if (this.index % colspan == 0) {
			buffer.append(trEnd);
		}
		return buffer;
	}

	public StringBuffer getSQLWhereInfo(List<FieldInfoBean> beans) {
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean = null;
		String fieldname = "";
		String fieldlabel = "";
		String fieldType = "";// Html�ֶ������磺�ı���
		String htmltype = "";// ���ݵ��ֶε�type
		for (FieldInfoBean fieldBean : beans) {
			String tmpStr = "if(!X1X){//XmarkX\n\tSqlWhere=SqlWhere+\" and XWHERE1X\";\n}\n";
			String tmpStr2 = "XnameX=\"+v_XnameX+\"";
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsedit())) {
				fieldname = fieldBean.getFieldname();// �ֶ���
				fieldlabel = fieldBean.getFieldlabel();// ��ʾ��
				fieldType = fieldBean.getFieldhtmltype();// Html�ֶ������磺�ı���
				htmltype = fieldBean.getHtmltype();// ���ݵ��ֶε�type
				if (fieldType.equals("1")) { // �����ı���
					if (htmltype.equals("1")) { // �����ı����е��ı�
						tmpStr2 = "XnameX like '%\"+v_XnameX+\"%'";
					}
				} else if (fieldType.equals("2")) {
					tmpStr2 = "XnameX like '%\"+v_XnameX+\"%'";
				}else if(fieldType.equals("3")) {
					if (htmltype.equals("2")) {//����
						tmpStr2 = "XnameX like '%\"+v_XnameX+\"%'";
					}
				}
				if (fieldType.equals("5")) {//ѡ���
					tmpStr = tmpStr.replace("X1X", "(\"-1\".equals(v_XnameX)||\"\".equals(v_XnameX))").replace("XWHERE1X", tmpStr2);
				}else{
					tmpStr = tmpStr.replace("X1X", "\"\".equals(v_XnameX)").replace("XWHERE1X", tmpStr2);
				}
				buffer.append(tmpStr.replace("XnameX", fieldname).replace(
						"XmarkX", fieldlabel));
			}
		}
		return buffer;
	}

	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean) {
		GenerateCommon gener = new GenerateCommon();
		Map<String, String> map = null;
		String str = null;
		RecordSet rs = new RecordSet();
		String v_sql = "";
		StringBuffer initStr = new StringBuffer();// ������ʼ����Ϣ
		StringBuffer buffer = new StringBuffer();
		FieldViewBean formBean = null;
		for (FieldInfoBean fieldBean : beans) {
			formBean = fieldBean.getNodeBean();
			if ("1".equals(formBean.getIsedit())) {// �༭ʱ��ʾ
				buffer.append(this.getTRInfo(fieldBean, formBean, pageBean));
			}
		}
		String mainView = pageBean.getMainviewtype();
		String fileName = gener.getFilePath("modelPage", "Search.jsp");
		String toFile = gener.getFilePath("GeneratePage", billBean
				.getTablename(), "Search.jsp");
		str = gener.readFile(fileName);
		String tableHeadStr = "";// ��ͷ��Ϣ
		String trStart = "<TR class=Title><TH colSpan=X1X>��׼</TH></TR>\n<TR class=Spacing><TD class=Line1 colSpan=X1X></TD></TR>\n";
		String trEnd = "</TR><TR><TD class=Line colspan=X1X></TD></TR>\n";
		int colspan = Util.getIntValue(mainView, 1) + 1;
		if ("1".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(30, 70, colspan) + "<TBODY>\n\n";
		} else if ("2".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(20, 30, colspan) + "<TBODY>\n\n";
		} else if ("3".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(13, 20, colspan) + "<TBODY>\n\n";
		} else if ("4".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(11, 14, colspan) + "<TBODY>\n\n";
		}else if ("5".equals(colspan + "")) {
			tableHeadStr = gener.getCOLGROUP(9, 11, colspan) + "<TBODY>\n\n";
		}// �õ���ͷ��Ϣ
		tableHeadStr += trStart.replace("X1X", colspan * 2 + "");
		if (this.index % colspan != 0) {//���һ�м�TR
			buffer.append(trEnd.replace("X1X", colspan * 2 + ""));
		}
		initStr.append(buffer);
		str = str.replace("${tablehead}", tableHeadStr);// �����ʽ
		str = str.replace("${titlename}", billBean.getNamelabel());
		str = str.replace("${sqlwhere}", this.getSQLWhereInfo(beans));
		str = str.replace("${content}", initStr);
		str = str.replace("${fla_billid}", billBean.getId());//��ID
		str = str.replace("${XTableX}", billBean.getTablename());//������
		map = this.getOtherInfo(beans);
		for (Map.Entry<String, String> ent : map.entrySet()) {
			str = str.replace(ent.getKey(), ent.getValue());
		}
		str = str.replace("${browse}", gener.getBrowse(beans));// �����
		gener.writeFile(toFile, str);
		v_sql = "update fla_bill set searchpage='X1X' where id="
				+ billBean.getId();
		v_sql = v_sql.replace("X1X", toFile.replace(GCONST.getRootPath() + "",
				File.separatorChar + ""));
		rs.executeSql(v_sql);
		return buffer;
	}

	@Override
	public void GeneratePage(String billid, BillBean billBean, String pageType) {
		List<FieldInfoBean> beans = new FieldInfoBean(billid, pageType, "0,1")
				.getList();
		PageInfoBean pageBean = new PageInfoBean(billid, pageType);
		this.GenerateMainInfo(beans, billBean, pageBean);// ����������Ϣ
	}
}
