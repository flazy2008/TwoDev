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
			bean.writeLog("开始获取连接....");
			iRet = sdk.Sms_Connect("www.mobset.com", 116504, "Admin", "292591",300);
			switch (iRet) {
				case 0: restr = "连接服务器成功....";break;
				case -1:restr = "连接服务器失败....";break;
				case -2:restr = "登录超时！";break;
				case -3:restr = "登录失败，帐号有误！";break;
				case -4:restr = "登录失败，相同帐号已在别处登录！";break;
				case -5:restr = "登录失败，帐号过于频繁登录，请稍后再试！";break;
				default:restr = "其它信息！";break;
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
