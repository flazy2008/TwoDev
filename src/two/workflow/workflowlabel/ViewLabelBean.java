package two.workflow.workflowlabel;

public class ViewLabelBean {
	private String labeltype = "";// ���Ͳ鿴��ѡ��������
	private String viewtype = "";// ���Ͳ鿴��ѡ��������
	private String labelinfo = "";// ��ʾ��Ϣ�磺�鿴����(�ĵ�)
	private String labelname = "";// ��ʾ����
	private String labelnameurl = "";// �����ӵ���ʾ����
	private String labelurl = "";// ��ǩ����
	private String isend = "";// �Ƿ����

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
		return "����:"+this.viewtype+" ����> "+"��ʾ��Ϣ:"+this.labelinfo+" ����> "+"�Ƿ����:"+this.isend;
	}
}
