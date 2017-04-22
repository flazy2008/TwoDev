package two.general;

import java.util.List;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.PageInfoBean;

public interface IGeneratePage {
	/**
	 * 生成文件主入口
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void GeneratePage(String billid,BillBean billBean, String pageType);// 生成文件主入口

	/**
	 * 创建过程
	 * 
	 * @param billid
	 * @param pageType
	 */
	public void CreateProc(String billid,BillBean billBean, String pageType);// 创建过程

	/**
	 * 创建后台文件
	 * 
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 */
	public void GenerateOperation(List<FieldInfoBean> beans, BillBean billBean,
			PageInfoBean pageBean);// 创建后台文件
	/**
	 * 生成主页面
	 * @param beans
	 * @param billBean
	 * @param pageBean
	 * @return
	 */
	public StringBuffer GenerateMainInfo(List<FieldInfoBean> beans,
			BillBean billBean, PageInfoBean pageBean);//生成主页面

}
