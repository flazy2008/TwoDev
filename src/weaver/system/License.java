// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   License.java

package weaver.system;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import weaver.conn.RecordSet;
import weaver.file.Prop;
import weaver.general.*;

// Referenced classes of package weaver.system:
//			GetPhysicalAddress

public class License extends BaseBean {

	Date newdate;
	long datetime;
	Timestamp timestamp;
	String currentdate;
	String currenttime;
	private String companyname;
	private String license;
	private String licensecode;
	private String software;
	private String hrmnum;
	private String expiredate;
	private String message;
	private String licensepass;
	private String concurrentFlag;
	private StaticObj staticobj;
	private ArrayList infos;

	public License() {
		newdate = new Date();
		datetime = newdate.getTime();
		timestamp = new Timestamp(datetime);
		currentdate = (new StringBuilder())
				.append(timestamp.toString().substring(0, 4)).append("-")
				.append(timestamp.toString().substring(5, 7)).append("-")
				.append(timestamp.toString().substring(8, 10)).toString();
		currenttime = (new StringBuilder())
				.append(timestamp.toString().substring(11, 13)).append(":")
				.append(timestamp.toString().substring(14, 16)).append(":")
				.append(timestamp.toString().substring(17, 19)).toString();
		companyname = "";
		license = "";
		licensecode = "";
		software = "";
		hrmnum = "";
		expiredate = "";
		message = "";
		licensepass = "";
		concurrentFlag = "";
		staticobj = null;
		infos = null;
		staticobj = StaticObj.getInstance();
		OutLicensecode();
	}

	public void setCompanyname(String s) {
		companyname = s.trim();
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setLicense(String s) {
		license = s.trim();
	}

	public String getLicense() {
		return license;
	}

	public void setLicensecode(String s) {
		licensecode = s.trim();
	}

	public String getLicensecode() {
		return licensecode;
	}

	public void setSoftware(String s) {
		software = s.trim();
	}

	public String getSoftware() {
		return software;
	}

	public void setHrmnum(String s) {
		hrmnum = s.trim();
	}

	public String getHrmnum() {
		return hrmnum;
	}

	public void setExpiredate(String s) {
		expiredate = s.trim();
	}

	public String getExpiredate() {
		return expiredate;
	}

	public void setLicensepass(String s) {
		licensepass = s.trim();
	}

	public void setConcurrentFlag(String s) {
		concurrentFlag = s.trim();
	}

	public String getConcurrentFlag() {
		return concurrentFlag;
	}

	public String InLicense() {
		RecordSet recordset = new RecordSet();
		String s = (new StringBuilder())
				.append("update license set companyname='").append(companyname)
				.append("',software='").append(software).append("',license='")
				.append(license).append("',hrmnum=").append(hrmnum)
				.append(",expiredate='").append(expiredate).append("'")
				.toString();
		boolean flag = recordset.execute(s);
		s = (new StringBuilder()).append("update HrmCompany set companyname='")
				.append(companyname).append("'").toString();
		boolean flag1 = recordset.execute(s);
		if (!flag || !flag1) {
			return "6";
		} else {
			message = CkLicense(currentdate);
			return message;
		}
	}

	public void removeLicenseComInfo() {
		staticobj.removeObject("LicenseComInfo");
	}

	public void ReadFromFile(String s) {
		try {
			if (staticobj.getObject("LicenseComInfo") == null) {
				infos = new ArrayList();
				File file = new File(s);
				FileInputStream fileinputstream = new FileInputStream(file);
				int i = (int) file.length();
				if (i > 0) {
					byte abyte0[] = new byte[i];
					for (int j = 0; (long) j < file.length(); j++)
						abyte0[j] = (byte) fileinputstream.read();

					String s1 = Encoder.decrypt(abyte0,
							licensecode.substring(0, 16));
					new BaseBean().writeLog("-------"+s1);
					System.out.println("-------"+s1);
					s1 = s1.substring(0, s1.lastIndexOf("|"));
					infos = Util.TokenizerString(s1, "|");
					fileinputstream.close();
					staticobj.putRecordToObj("LicenseComInfo", "infos", infos);
				}
			} else {
				infos = (ArrayList) (ArrayList) staticobj.getRecordFromObj(
						"LicenseComInfo", "infos");
			}
			if (infos.size() == 5) {
				companyname = (String) infos.get(0);
				license = (String) infos.get(1);
				software = (String) infos.get(2);
				hrmnum = (String) infos.get(3);
				expiredate = (String) infos.get(4);
			}
			if (infos.size() == 6) {
				companyname = (String) infos.get(0);
				license = (String) infos.get(1);
				software = (String) infos.get(2);
				hrmnum = (String) infos.get(3);
				expiredate = (String) infos.get(4);
				concurrentFlag = (String) infos.get(5);
			}
		} catch (Exception exception) {
			writeLog(exception);
		}
	}

	public String OutLicensecode() {
		String s = Util.null2String((String) staticobj
				.getObject("StaticLicenseCode"));
		if (s.equals(""))
			s = MakeLicensecode();
		licensecode = s;
		return licensecode;
	}

	public String MakeLicensecode() {
		GetPhysicalAddress getphysicaladdress = new GetPhysicalAddress();
		licensecode = Util.getEncrypt(getphysicaladdress.getPhysicalAddress());
		staticobj.putObject("StaticLicenseCode", licensecode);
		return licensecode;
	}

	public String CkLicense(String s) {
		String s1 = "";
		String s2 = "";
		RecordSet recordset = new RecordSet();
		s1 = "select companyname from license";
		boolean flag = recordset.execute(s1);
		if (!flag)
			return "2";
		if (recordset.next())
			s2 = recordset.getString("companyname").trim();
		String s3 = (new StringBuilder()).append(GCONST.getRootPath())
				.append("license").append(File.separatorChar)
				.append(licensecode).append(".license").toString();
		ReadFromFile(s3);
		if (expiredate.compareTo(s) < 0)
			return "4";
		String s4 = hrmnum;
		String s5 = "";
		if ("1".equals(concurrentFlag))
			s5 = (new StringBuilder()).append(s2).append(licensecode)
					.append(software).append(s4).append(expiredate)
					.append(concurrentFlag).toString();
		else
			s5 = (new StringBuilder()).append(s2).append(licensecode)
					.append(software).append(s4).append(expiredate).toString();
		if (!license.equals("") && license.equals(Util.getEncrypt(s5)))
			message = "1";
		else
			message = "0";
		if (CkHrmnum() >= 1)
			message = "5";
		return message;
	}

	public int CkHrmnum() {
		int i = 0;
		int j = 0;
		int k = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = (new StringBuilder()).append(GCONST.getRootPath())
				.append("license").append(File.separatorChar)
				.append(licensecode).append(".license").toString();
		ReadFromFile(s1);
		j = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap")) {
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		if ("1".equals(concurrentFlag)) {
			i = -1;
		} else {
			recordset.execute(s);
			if (recordset.next())
				k = recordset.getInt(1);
			i = k - j;
		}
		return i;
	}

	public int CkUnusedHrmnum() {
		int i = 0;
		int j = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = (new StringBuilder()).append(GCONST.getRootPath())
				.append("license").append(File.separatorChar)
				.append(licensecode).append(".license").toString();
		ReadFromFile(s1);
		i = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap")) {
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		recordset.execute(s);
		if (recordset.next())
			j = recordset.getInt(1);
		return i - j;
	}

	public int CkUsedHrmnum() {
		int i = 0;
		int j = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = (new StringBuilder()).append(GCONST.getRootPath())
				.append("license").append(File.separatorChar)
				.append(licensecode).append(".license").toString();
		ReadFromFile(s1);
		i = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap")) {
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		recordset.execute(s);
		if (recordset.next())
			j = recordset.getInt(1);
		return j;
	}
}
