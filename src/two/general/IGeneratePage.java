package two.general;

import java.util.List;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.PageInfoBean;

public interface IGeneratePage {
	/**
	 * �����ļ������
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void GeneratePage(String billid,BillBean billBean, String pageType);// �����ļ������

	/**
	 * ��������
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void CreateProc(String billid,BillBean billBean, String pageType);// ��������

	/**
	 * ������̨�ļ�
	 * 
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 */
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean);// ������̨�ļ�
	/**
	 * ������ҳ��
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 * @return
	 */
	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean);//������ҳ��

}
