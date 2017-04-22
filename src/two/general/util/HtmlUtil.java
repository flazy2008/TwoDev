package two.general.util;

public class HtmlUtil {
	public static final String mandOnChange = "isMand(this,0);";//������Ϣ
	//A��Ϣ
	public static final String ASTR = "<a href=\"${value}\">${text}</a>";
	//SPAN��Ϣ
	public static final String SPANSTR = "\t<span id=\"Span${name}\">${value}</span>";
	//SPAN��ʾҳ����Ϣ
	public static final String VIEW_SPANSTR = "\t<span id=\"Span${name}\" X1X>${value}</span>";
	// ���ؿ�
	public static final String HIDDSTR = "\t<input type=hidden id=\"${name}\" name=\"${name}\" title=\"${title}\" value=\"${value}\" X1X/>";	
	//�����ı���1
	public static final String INPUTSTR = "\t<input type=text id=\"${name}\" name=\"${name}\" title=\"${title}\" value=\"${value}\" X1X size=\"${size}\" maxlength=\"${maxlength}\">";
	//�ı���2
	public static final String TEXTAREASTR = "\t<textarea id=\"${name}\" name=\"${name}\" title=\"${title}\" X1X rows=2 cols=\"30\">${value}</textarea>";// �����ı���
	//���ڰ�ť3
	public static final String DATASTR = "\t<button class=\"calendar\" id=\"Butt${name}\" name=\"Butt${name}\" title=\"${title}\" valueInput=\"${name}\" valueSpan=\"Span${name}\" onClick=\"gettheDate(this.valueInput,this.valueSpan)\"></button>";// ���ڰ�ť
	// ��ť3
	public static final String BUTTSTR = "\t<button class=\"Browser\" id=\"Butt${name}\" name=\"Butt${name}\" title=\"${title}\" onClick=\"onShow${pname}(this.id)\" X1X></button>";
	//ѡ���4
	public static final String CHECKSTR = "\t<input type=checkbox id=\"${name}\" name=\"${name}\" title=\"${title}\" value=\"1\" X1X/>";// ѡ���
	// ������5
	public static final String SELECTSTR = "\t<select id=\"${name}\" name=\"${name}\" title=\"${title}\" X1X>\n${text}\t</select>";// ������
	// ������5
	public static final String OPTIONSTR = "\t\t<option value=\"${value}\" X1X>${text}</option>\n";
	// �����ϴ�6
	public static final String FILESTR = "\t<input type=file id=\"File${name}\" name=\"File${name}\" title=\"${title}\" onchange=\"this.value=trim(this.value)\" size=\"10\" X1X>";// �ļ���
	//sqlserver���ݿ���ӵ��й��̷���
	public static final String MSSQL_ADD  = "CREATE PROCEDURE fla_XtableX_add (X1X @flag int output, @msg varchar(80) output) as \ninsert into XtableX(X11X) \n\tvalues(X12X) \nselect max(id) as id from XtableX ";
	public static final String MSSQL_LADD = "CREATE PROCEDURE fla_XtableX_ladd(X1X @flag int output, @msg varchar(80) output) as \ninsert into XtableX(X11X) \n\tvalues(X12X) \nselect max(id) as id from XtableX ";
	
	public static final String MSSQL_EDIT = "CREATE PROCEDURE fla_XtableX_edit(@id int,X1X @flag int output, @msg varchar(80) output) as \nUPDATE  XtableX set X11X  where id=@id ";
	public static final String MSSQL_LEDIT= "CREATE PROCEDURE fla_XtableX_ledit(@id varchar(200), X1X @flag int output, @msg varchar(80) output) as \nUPDATE  XtableX set X10X  where id=@id \nif len(@id)=0 or @@ROWCOUNT = 0 begin \n\tinsert into XtableX(X11X) \n\tvalues(X12X) \nend \nselect isnull(@id,max(id)) as id from XtableX ";
	//oracle���ݿ���ӵ��й��̷���
	public static final String ORACLE_ADD ="CREATE OR REPLACE PROCEDURE fla_XtableX_add (X1X flag out integer , msg out varchar2, thecursor IN OUT cursor_define.weavercursor ) AS \nbegin \n\tinsert into XtableX(X11X) \n\tvalues(X12X); \nopen thecursor for \n\tselect max(id) as id from XtableX; \nend;";
	public static final String ORACLE_LADD="CREATE OR REPLACE PROCEDURE fla_XtableX_ladd(X1X flag out integer , msg out varchar2, thecursor IN OUT cursor_define.weavercursor ) AS \nbegin \n\tinsert into XtableX(X11X) \n\tvalues(X12X); \nopen thecursor for \n\tselect max(id) as id from XtableX; \nend;";
	
	public static final String ORACLE_EDIT ="CREATE OR REPLACE PROCEDURE fla_XtableX_edit (v_id integer,X1X flag out integer , msg out varchar2, thecursor IN OUT cursor_define.weavercursor )  AS \nbegin \n\tUPDATE  XtableX set X11X  where id=v_id ; \nend;";
	public static final String ORACLE_LEDIT="CREATE OR REPLACE PROCEDURE fla_XtableX_ledit(v_id integer,X1X flag out integer , msg out varchar2, thecursor IN OUT cursor_define.weavercursor )  AS \nbegin \n\tUPDATE  XtableX set X10X  where id=v_id ; \nif length(v_id)=0 or sql%rowcount=0 then \n\tinsert into XtableX(X11X) \n\tvalues(X12X); \nend if; \nopen thecursor for \n\tselect nvl(v_id,max(id)) as id from XtableX; \nend;";
	//��Ӳ���ҳ���������Ϣ
	public static final String ADD_OPERREQ="String v_XnameX = Util.null2String(request.getParameter(\"XnameX\"))XCommaX;//XmarkX\n";
	//�ֶζ���
	public static final String FIELDSTR="String XFieldX =\"\";//XmarkX\n";
	//�ֶζ���
	public static final String FIELDRECORDSTR="XFieldX= Util.null2String(RecordSet.getString(\"XnameX\"));//XmarkX\n";
	//�ϴ��ļ���
	public static final String OPER_FILEUPLOAD="FileUpload fu = new FileUpload(request);";
	//����ҳ������
	public static final String OPER_MAINREQUEST="\nString v_XnameX = Util.null2String(request.getParameter(\"XnameX\"))XCommaX;//XmarkX";
	//����ҳ������
	public static final String OPER_DETAILREQUEST="\n\t\tv1_XnameX = Util.null2String(request.getParameter(\"XnameX_\"+v_tmp))XCommaX;//XmarkX";	
	//����ҳ�渽��
	public static final String OPER_UPLOADREQUEST="\nString v_FileXnameX = Util.null2String(fu.uploadFiles(\"FileXnameX\"));//XmarkX";
	//����ҳ�渽��
	public static final String OPER_LISTLOADREQUEST="\n\t\tv_FileXnameX = Util.null2String(fu.uploadFiles(\"FileXnameX_\"+v_tmp));//XmarkX";	
	//���ҳ��
	public static final String ADD_COLGROUP1="<COL width=\"30%\">\n<COL width=\"70%\">\n";
	public static final String ADD_COLGROUP2="<COL width=\"20%\">\n<COL width=\"30%\">\n<COL width=\"20%\">\n<COL width=\"30%\">\n";
}
