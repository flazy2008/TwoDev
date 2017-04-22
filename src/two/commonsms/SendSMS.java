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
		sendMsg[0].strMobile = number; // 目标手机号码，测试时请更改号码。
		sendMsg[0].strMsg = msg; // 短信内容
		int iRet = sdk2.Sms_Send(sendMsg, 1);
		if (iRet <= 0) {
			flag=false;
			writeLog("短信发送失败: smsId:"+smsId+" number:" + number + " msg:" + msg);
			switch (iRet) {
				case -1:restr = "接口未初始化！";break;
				case -2:restr = "发送超时！";break;
				case -3:restr = "发送失败，可能是帐号过期或余额不足！";break;
				case -4:restr = "发送失败，发送的信息内容中含有敏感关键字，禁止发送！";break;
				case -5:restr = "发送失败，发送的目标号码为黑名单用户，禁止发送！";break;
				default:restr = "其它信息！";break;
			}
			writeLog(restr);
		} else {
			flag=true;
		}
		return flag;
	}

}
