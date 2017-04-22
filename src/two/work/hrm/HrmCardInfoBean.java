package two.work.hrm;

public class HrmCardInfoBean {
	private String userid = "";// 人员
	private String carddate = "";// 打卡日期
	private String intime = "";// 早上打卡时间
	private String outtime = "";// 下班时间
	private String isexcep = "";// 是否异常
	private String belate = ""; // 迟到
	private String leaveearly = "";// 早退
	private String vacate = "";// 请假
	private String vacatetype = "";// 请假类型
	private String evection = "";// 出差
	private String beout = "";// 外出
	private String nowork = "";// 旷工

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCarddate() {
		return carddate;
	}

	public void setCarddate(String carddate) {
		this.carddate = carddate;
	}

	public String getIntime() {
		return intime;
	}

	public void setIntime(String intime) {
		this.intime = intime;
	}

	public String getOuttime() {
		return outtime;
	}

	public void setOuttime(String outtime) {
		this.outtime = outtime;
	}

	public String getIsexcep() {
		return isexcep;
	}

	public void setIsexcep(String isexcep) {
		this.isexcep = isexcep;
	}

	public String getBelate() {
		return belate;
	}

	public void setBelate(String belate) {
		this.belate = belate;
	}

	public String getLeaveearly() {
		return leaveearly;
	}

	public void setLeaveearly(String leaveearly) {
		this.leaveearly = leaveearly;
	}

	public String getVacate() {
		return vacate;
	}

	public void setVacate(String vacate) {
		this.vacate = vacate;
	}

	public String getVacatetype() {
		return vacatetype;
	}

	public void setVacatetype(String vacatetype) {
		this.vacatetype = vacatetype;
	}

	public String getEvection() {
		return evection;
	}

	public void setEvection(String evection) {
		this.evection = evection;
	}

	public String getBeout() {
		return beout;
	}

	public void setBeout(String beout) {
		this.beout = beout;
	}

	public void setNowork(String nowork) {
		this.nowork = nowork;
	}

	public String getNowork() {
		return nowork;
	}

}
