package two.servlet;

import java.util.List;

public class Page {
	private int totalCount = 0;//  当前页数
	private int start = 0;//  开始条数
	private int limit = 10;// 每页显示条数
	private List dataList;//  数据
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public List getDataList() {
		return dataList;
	}
	public void setDataList(List dataList) {
		this.dataList = dataList;
	}
	
}
