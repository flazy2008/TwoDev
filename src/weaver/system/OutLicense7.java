package weaver.system;

import java.io.File;
import java.io.FileOutputStream;

import weaver.general.Encoder;
import weaver.general.MD5;

public class OutLicense7 {
	private String companyname = "";
	private String license = "";
	private String software = "";
	private String hrmnum = "";
	private String expiredate = "";
	private String concurrentFlag = "";
	private String licenseStr = "";
	private String fieldStr = "";
	public static void main(String[] args) {//81C34AF4230081F997F82F6664CE1EBF
		OutLicense7 license = new OutLicense7("重庆瀚华担保有限公司","9A0B015DD3CD6206AE06C48731BD8DEF", "1000", "2012-12-18");
		license.WriteLicense();
	}
	public OutLicense7(String companyname, String license, String hrmnum,String expiredate) {
		this.companyname = companyname;
		this.hrmnum = hrmnum;
		this.license = license;
		licenseStr = companyname + license + "ALL" + hrmnum + expiredate;
		MD5 md5 = new MD5();
		fieldStr = companyname+ "|"+ md5.getMD5ofStr(licenseStr)+ "|ALL|"+ hrmnum+ "|"+ expiredate+ "||62007919720633269196934484644125572769413633302186752018523445204301872653794360360881842555537247965393197415747802506506903723671584628703793333836260354030388947518689965872204033206541014910272499944701702988688212742607209049804094825138611531230934194208981932500985643221725998992712571064487410029036682541030848812140765519637223225765145139855882741906522002982899019793339456823652135011759036268958238757177574418288875728800265820122656046365091479330314745101538118436870558901894325330650271764169556869840948129837865308102242141160663654023226233511587864535620411407839247465963925174664596350137905478414131113010270745640323735177234986441610355908057471594263149725938868668992395366126915888622724407173605869847739145034167970820632710371615733542645664713410254315543976991981455978982517728513007072481157728435771044793437971374903442991275869757002348506042816839394755";
	}

	public void WriteLicense() {
		try {
			File file=new File("d:/license/"+this.license+".license");
			if(!file.exists()){
				new File(file.getParent()).mkdir();
			}
			FileOutputStream fileoutstream = new FileOutputStream(file);
			byte[] aa = Encoder.encrypt(fieldStr, license.substring(0, 16));
			fileoutstream.write(aa);
			fileoutstream.flush();
			fileoutstream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
