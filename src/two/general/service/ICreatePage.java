package two.general.service;

import two.general.bean.BillBean;
import two.general.bean.PageInfoBean;

public interface ICreatePage {
	/**
	 * ������ҳ��
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 * @return
	 */
	public void MainInfo(BillBean billBean, PageInfoBean pageBean);//������ҳ��
	/**
	 * �����ļ������
	 * @param billid
	 * @param pageType
	 */
	public void CreatePage(BillBean billBean, PageInfoBean pageBean);// �����ļ�
	/**
	 * ��������
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void CreateProc(BillBean billBean, PageInfoBean pageBean);// ��������

	/**
	 * ������̨�ļ�
	 * 
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 */
	public void CreateOper(BillBean billBean,PageInfoBean pageBean);// ������̨�ļ�

}
