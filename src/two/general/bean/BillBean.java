package two.general.bean;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class BillBean {
	private String id="";
	private String namelabel="";
	private String tablename="";
	private String formdes="";
	private String detailtablename="";
	private String createpage="";
	private String managepage="";
	private String operationpage="";
	private String viewpage="";
	private String searchpage="";
	private String browserpage="";
	private String hasfileup="";
	private String hashtmledit="";
	private String orders="";
	RecordSet rs = new RecordSet();
	String v_sql = "";

	public BillBean(String billid) {
		v_sql = "select * from fla_bill bill where id in(" + billid + ")";
		rs.executeSql(v_sql);
		if (rs.next()) {
			this.id= Util.null2String(rs.getString("id"));
			this.namelabel= Util.null2String(rs.getString("namelabel"));
			this.tablename= Util.null2String(rs.getString("tablename"));
			this.formdes= Util.null2String(rs.getString("formdes"));
			this.detailtablename= Util.null2String(rs.getString("detailtablename"));
			this.createpage= Util.null2String(rs.getString("createpage"));
			this.managepage= Util.null2String(rs.getString("managepage"));
			this.operationpage= Util.null2String(rs.getString("operationpage"));
			this.viewpage= Util.null2String(rs.getString("viewpage"));
			this.searchpage= Util.null2String(rs.getString("searchpage"));
			this.browserpage= Util.null2String(rs.getString("browserpage"));
			this.hasfileup= Util.null2String(rs.getString("hasfileup"));
			this.hashtmledit= Util.null2String(rs.getString("hashtmledit"));
			this.orders= Util.null2String(rs.getString("orders"));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNamelabel() {
		return namelabel;
	}

	public void setNamelabel(String namelabel) {
		this.namelabel = namelabel;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getFormdes() {
		return formdes;
	}

	public void setFormdes(String formdes) {
		this.formdes = formdes;
	}

	public String getDetailtablename() {
		return detailtablename;
	}

	public void setDetailtablename(String detailtablename) {
		this.detailtablename = detailtablename;
	}

	public String getCreatepage() {
		return createpage;
	}

	public void setCreatepage(String createpage) {
		this.createpage = createpage;
	}

	public String getManagepage() {
		return managepage;
	}

	public void setManagepage(String managepage) {
		this.managepage = managepage;
	}

	public String getOperationpage() {
		return operationpage;
	}

	public void setOperationpage(String operationpage) {
		this.operationpage = operationpage;
	}

	public String getViewpage() {
		return viewpage;
	}

	public void setViewpage(String viewpage) {
		this.viewpage = viewpage;
	}

	public String getSearchpage() {
		return searchpage;
	}

	public void setSearchpage(String searchpage) {
		this.searchpage = searchpage;
	}

	public String getBrowserpage() {
		return browserpage;
	}

	public void setBrowserpage(String browserpage) {
		this.browserpage = browserpage;
	}

	public String getHasfileup() {
		return hasfileup;
	}

	public void setHasfileup(String hasfileup) {
		this.hasfileup = hasfileup;
	}

	public String getHashtmledit() {
		return hashtmledit;
	}

	public void setHashtmledit(String hashtmledit) {
		this.hashtmledit = hashtmledit;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

}
