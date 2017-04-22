package two.commonsms;

import mobset.smsSDK;
import mobset.str_SendMsg;
import weaver.general.BaseBean;
import weaver.sms.SmsService;

public class SendSMS extends BaseBean implements SmsService {

	public boolean sendSMS(String smsId, String number, String msg) {
		boolean flag = true;
		String restr = "";
		smsSDK sdk2=SmsConnUitl.getConnectSDK();
		str_SendMsg[] sendMsg = new str_SendMsg[1];
		sendMsg[0] = new str_SendMsg();
		sendMsg[0].strMobile = number; // Ŀ���ֻ����룬����ʱ����ĺ��롣
		sendMsg[0].strMsg = msg; // ��������
		int iRet = sdk2.Sms_Send(sendMsg, 1);
		if (iRet <= 0) {
			flag=false;
			writeLog("���ŷ���ʧ��: smsId:"+smsId+" number:" + number + " msg:" + msg);
			switch (iRet) {
				case -1:restr = "�ӿ�δ��ʼ����";break;
				case -2:restr = "���ͳ�ʱ��";break;
				case -3:restr = "����ʧ�ܣ��������ʺŹ��ڻ����㣡";break;
				case -4:restr = "����ʧ�ܣ����͵���Ϣ�����к������йؼ��֣���ֹ���ͣ�";break;
				case -5:restr = "����ʧ�ܣ����͵�Ŀ�����Ϊ�������û�����ֹ���ͣ�";break;
				default:restr = "������Ϣ��";break;
			}
			writeLog(restr);
		} else {
			flag=true;
		}
		return flag;
	}

}
