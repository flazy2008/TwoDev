package two.general;

import java.util.List;

import two.general.bean.BillBean;
import two.general.bean.FieldInfoBean;
import two.general.bean.PageInfoBean;
import two.general.service.IAddPage;

public class GeneratePage {
	public void GeneratePage(String billid, String pageType,String mainviewtype, String generateOper, String generateProc) {
		List<FieldInfoBean> beans =new FieldInfoBean().getAllFields(billid, "");
		BillBean billBean = new BillBean(billid);
		PageInfoBean pageBean = new PageInfoBean(billid, pageType);
		if ("Add".equals(pageType)) {
			IAddPage addPage=new two.general.service.GenerateAddPageImpl()
			generatePage = new GenerateAddPageImpl();
			if ("3".equals(mainviewtype)) {
				generatePage = new GenerateListAddPageImpl();
			}else if ("4".equals(mainviewtype)) {//��׼��ʾ
				generatePage = new GenerateStandAddPageImpl();
			}
		} else if ("Edit".equals(pageType)) {
			generatePage = new GenerateEditPageImpl();
			if ("3".equals(mainviewtype)) {
				generatePage = new GenerateListEditPageImpl();
			}else if ("4".equals(mainviewtype)) {//��׼��ʾ
				generatePage = new GenerateStandEditPageImpl();
			}			
		} else if ("View".equals(pageType)) {
			generatePage = new GenerateViewPageImpl();
			if ("3".equals(mainviewtype)) {
				generatePage = new GenerateListViewPageImpl();
			}else if ("4".equals(mainviewtype)) {//��׼��ʾ
				generatePage = new GenerateStandViewPageImpl();
			}
		}else if ("Search".equals(pageType)) {
			generatePage = new GenerateSearchPageImpl();
		}else if ("Browser".equals(pageType)) {
			generatePage = new GenerateBrowserPageImpl();
		}
		generatePage.GeneratePage(billid, billBean, pageType);// ����ҳ��
		if ("1".equals(generateProc)) {// ���ɴ洢����
			generatePage.CreateProc(billid, billBean, pageType);
		}
		if ("1".equals(generateOper)) {// ���ɲ���ҳ��
			generatePage.GenerateOperation(beans, billBean, pageBean);
		}
	}
}
