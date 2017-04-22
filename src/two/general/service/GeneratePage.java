package two.general.service;

import two.general.bean.BillBean;
import two.general.bean.PageInfoBean;

public class GeneratePage {
	public void doCreatePage(String billid, String pageType,String mainviewtype, String isOper, String isProc) {
		BillBean billBean = new BillBean(billid);
		PageInfoBean pageBean = new PageInfoBean(billid, pageType, isOper,isProc);
		String dtviewtype = pageBean.getDtviewtype();
		if ("Add".equals(pageType)) {// Ìí¼ÓÒ³Ãæ
			ICreatePage addPage = null;
			if ("1,2".indexOf(mainviewtype) > -1) {
				addPage = new AddBasicPageImpl();
				if ("1".equals(dtviewtype)) {
					addPage = new AddDetailPageImpl();
				}
			} else if ("3".equals(mainviewtype)) {
				addPage = new AddListPageImpl();
			} else if ("4".equals(mainviewtype)) {
				addPage = new AddStandPageImpl();
			}
			addPage.MainInfo(billBean, pageBean);
		} else if ("Edit".equals(pageType)) {
			ICreatePage editPage = null;
			if ("1,2".indexOf(mainviewtype) > -1) {
				editPage = new EditBasicPageImpl();
				if ("1".equals(dtviewtype)) {
					editPage = new EditDetailPageImpl();
				}
			} else if ("3".equals(mainviewtype)) {
				editPage = new EditListPageImpl();
			} else if ("4".equals(mainviewtype)) {
				editPage = new EditStandPageImpl();
			}
			editPage.MainInfo(billBean, pageBean);
		} else if ("View".equals(pageType)) {
			ICreatePage viewPage = null;
			if ("1,2".indexOf(mainviewtype) > -1) {
				viewPage = new ViewBasicPageImpl();
				if ("1".equals(dtviewtype)) {
					viewPage = new ViewDetailPageImpl();
				}
			} else if ("3".equals(mainviewtype)) {
				viewPage = new ViewListPageImpl();
			} else if ("4".equals(mainviewtype)) {
				viewPage = new ViewStandPageImpl();
			}
			viewPage.MainInfo(billBean, pageBean);
		} else if ("Search".equals(pageType)) {
			ICreatePage searchPage = new SearchPageImpl();
			searchPage.MainInfo(billBean, pageBean);
		} else if ("Browser".equals(pageType)) {
			ICreatePage browserPage = new BrowserPageImpl();
			if ("3,4".indexOf(mainviewtype) > -1) {
				browserPage = new BrowserMutiPageImpl();
			}
			browserPage.MainInfo(billBean, pageBean);
		}
	}
}
