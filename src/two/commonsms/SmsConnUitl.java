package two.commonsms;

import mobset.smsSDK;
import weaver.general.BaseBean;

public class SmsConnUitl {
	private static smsSDK sdk = null;
	static BaseBean bean = new BaseBean();
	public static int iRet = -1;

	private SmsConnUitl() {
	}

	public static smsSDK getConnectSDK() {
		String restr = "";
		if (sdk == null || iRet != 0) {
			DisConnect();
			sdk = new smsSDK();
			bean.writeLog("��ʼ��ȡ����....");
			iRet = sdk.Sms_Connect("www.mobset.com", 116504, "Admin", "292591",300);
			switch (iRet) {
				case 0: restr = "���ӷ������ɹ�....";break;
				case -1:restr = "���ӷ�����ʧ��....";break;
				case -2:restr = "��¼��ʱ��";break;
				case -3:restr = "��¼ʧ�ܣ��ʺ�����";break;
				case -4:restr = "��¼ʧ�ܣ���ͬ�ʺ����ڱ𴦵�¼��";break;
				case -5:restr = "��¼ʧ�ܣ��ʺŹ���Ƶ����¼�����Ժ����ԣ�";break;
				default:restr = "������Ϣ��";break;
			}
			bean.writeLog(restr);
		}
		return sdk;
	}

	public static void DisConnect() {
		if (sdk != null) {
			sdk.Sms_DisConnect();
			sdk = null;
		}
	}
}
