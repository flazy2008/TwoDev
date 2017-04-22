package two.workflow.workflowlabel;

public class ViewLabelBean {
	private String labeltype = "";// 类型查看、选做、必做
	private String viewtype = "";// 类型查看、选做、必做
	private String labelinfo = "";// 显示信息如：查看事宜(文档)
	private String labelname = "";// 显示名称
	private String labelnameurl = "";// 带连接的显示名称
	private String labelurl = "";// 标签连接
	private String isend = "";// 是否完成

	public ViewLabelBean() {
	}

	public ViewLabelBean(String viewtype, String labelinfo, String labelname,
			String labelnameurl, String labelurl, String isend) {
		this("", viewtype, labelinfo, labelname, labelnameurl, labelurl, isend);
	}

	public ViewLabelBean(String labeltype, String viewtype, String labelinfo,
			String labelname, String labelnameurl, String labelurl, String isend) {
		this.labeltype = labeltype;
		this.viewtype = viewtype;
		this.labelinfo = labelinfo;
		this.labelnameurl = labelnameurl;
		this.labelname = labelname;
		this.labelurl = labelurl;
		this.isend = isend;
	}

	public String getLabelinfo() {
		return labelinfo;
	}

	public void setLabelinfo(String labelinfo) {
		this.labelinfo = labelinfo;
	}

	public String getLabeltype() {
		return labeltype;
	}

	public void setLabeltype(String labeltype) {
		this.labeltype = labeltype;
	}

	public String getLabelname() {
		return labelname;
	}

	public void setLabelname(String labelname) {
		this.labelname = labelname;
	}

	public String getLabelurl() {
		return labelurl;
	}

	public void setLabelurl(String labelurl) {
		this.labelurl = labelurl;
	}

	public String getIsend() {
		return isend;
	}

	public void setIsend(String isend) {
		this.isend = isend;
	}

	public void setLabelnameurl(String labelnameurl) {
		this.labelnameurl = labelnameurl;
	}

	public String getLabelnameurl() {
		return labelnameurl;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}

	public String getViewtype() {
		return viewtype;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "类型:"+this.viewtype+" ——> "+"显示信息:"+this.labelinfo+" ——> "+"是否完成:"+this.isend;
	}
}
