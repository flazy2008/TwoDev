package two.general.service;

import two.general.bean.BillBean;
import two.general.bean.PageInfoBean;

public interface ICreatePage {
	/**
	 * 生成主页面
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 * @return
	 */
	public void MainInfo(BillBean billBean, PageInfoBean pageBean);//生成主页面
	/**
	 * 生成文件主入口
	 * @param billid
	 * @param pageType
	 */
	public void CreatePage(BillBean billBean, PageInfoBean pageBean);// 生成文件
	/**
	 * 创建过程
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void CreateProc(BillBean billBean, PageInfoBean pageBean);// 创建过程

	/**
	 * 创建后台文件
	 * 
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 */
	public void CreateOper(BillBean billBean,PageInfoBean pageBean);// 创建后台文件

}
