package two.servlet;

import java.util.List;

public class Page {
	private int totalCount = 0;//  ��ǰҳ��
	private int start = 0;//  ��ʼ����
	private int limit = 10;// ÿҳ��ʾ����
	private List dataList;//  ����
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
