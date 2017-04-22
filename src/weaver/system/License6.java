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

public class License6 extends BaseBean
{

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

	public License6()
	{
		newdate = new Date();
		datetime = newdate.getTime();
		timestamp = new Timestamp(datetime);
		currentdate = timestamp.toString().substring(0, 4) + "-" + timestamp.toString().substring(5, 7) + "-" + timestamp.toString().substring(8, 10);
		currenttime = timestamp.toString().substring(11, 13) + ":" + timestamp.toString().substring(14, 16) + ":" + timestamp.toString().substring(17, 19);
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

	public int CkHrmnum()
	{
		int i = 0;
		int j = 0;
		int k = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = GCONST.getRootPath() + "license" + File.separatorChar + licensecode + ".license";
		ReadFromFile(s1);
		j = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap"))
		{
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else
		if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		if ("1".equals(concurrentFlag))
		{
			i = -1;
		} else
		{
			recordset.execute(s);
			if (recordset.next())
				k = recordset.getInt(1);
			i = k - j;
		}
		return i;
	}

	public String CkLicense(String s)
	{
		String s1 = "";
		String s2 = "";
		RecordSet recordset = new RecordSet();
		s1 = "select companyname from license";
		boolean flag = recordset.execute(s1);
		if (!flag)
			return "2";
		if (recordset.next())
			s2 = recordset.getString("companyname").trim();
		String s3 = GCONST.getRootPath() + "license" + File.separatorChar + licensecode + ".license";
		ReadFromFile(s3);
		if (expiredate.compareTo(s) < 0)
			return "1";//return "4";修改后
		String s4 = hrmnum;
		String s5 = "";
		if ("1".equals(concurrentFlag))
			s5 = s2 + licensecode + software + s4 + expiredate + concurrentFlag;
		else
			s5 = s2 + licensecode + software + s4 + expiredate;
		if (!license.equals("") && license.equals(Util.getEncrypt(s5)))
			message = "1";
		else
			message = "1";//message = "0";修改后
		if (CkHrmnum() >= 1)
			message = "1";//message = "5";修改后
		return message;
	}

	public int CkUnusedHrmnum()
	{
		int i = 0;
		int j = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = GCONST.getRootPath() + "license" + File.separatorChar + licensecode + ".license";
		ReadFromFile(s1);
		i = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap"))
		{
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else
		if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		recordset.execute(s);
		if (recordset.next())
			j = recordset.getInt(1);
		return i - j;
	}

	public int CkUsedHrmnum()
	{
		int i = 0;
		int j = 0;
		String s = "";
		RecordSet recordset = new RecordSet();
		String s1 = GCONST.getRootPath() + "license" + File.separatorChar + licensecode + ".license";
		ReadFromFile(s1);
		i = Util.getIntValue(hrmnum, 0);
		String s2 = Prop.getPropValue(GCONST.getConfigFile(), "authentic");
		if (s2 != null && s2.equals("ldap"))
		{
			if (recordset.getDBType().equals("oracle"))
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null";
			else
				s = "select count(*) from HrmResource where status in (0,1,2,3) and account is not null and account<>'' ";
		} else
		if (recordset.getDBType().equals("oracle"))
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null";
		else
			s = "select count(*) from HrmResource where status in (0,1,2,3) and loginid is not null and loginid<>'' ";
		recordset.execute(s);
		if (recordset.next())
			j = recordset.getInt(1);
		return j;
	}

	public String InLicense()
	{
		RecordSet recordset = new RecordSet();
		String s = "update license set companyname='" + companyname + "',software='" + software + "',license='" + license + "',hrmnum=" + hrmnum + ",expiredate='" + expiredate + "'";
		boolean flag = recordset.execute(s);
		s = "update HrmCompany set companyname='" + companyname + "'";
		boolean flag1 = recordset.execute(s);
		if (!flag || !flag1)
		{
			return "6";
		} else
		{
			message = CkLicense(currentdate);
			return message;
		}
	}

	public String MakeLicensecode()
	{
		GetPhysicalAddress getphysicaladdress = new GetPhysicalAddress();
		licensecode = Util.getEncrypt(getphysicaladdress.getPhysicalAddress());
		staticobj.putObject("StaticLicenseCode", licensecode);
		return licensecode;
	}

	public String OutLicensecode()
	{
		String s = Util.null2String((String)staticobj.getObject("StaticLicenseCode"));
		if (s.equals(""))
			s = MakeLicensecode();
		licensecode = s;
		return licensecode;
	}

	public void ReadFromFile(String s)
	{
		try
		{
			if (staticobj.getObject("LicenseComInfo") == null)
			{
				infos = new ArrayList();
				File file = new File(s);
				FileInputStream fileinputstream = new FileInputStream(file);
				int i = (int)file.length();
				if (i > 0)
				{
					byte abyte0[] = new byte[i];
					for (int j = 0; (long)j < file.length(); j++)
						abyte0[j] = (byte)fileinputstream.read();

					String s1 = Encoder.decrypt(abyte0, licensecode.substring(0, 16));
					s1 = s1.substring(0, s1.lastIndexOf("|"));
					infos = Util.TokenizerString(s1, "|");
					fileinputstream.close();
					staticobj.putRecordToObj("LicenseComInfo", "infos", infos);
				}
			} else
			{
				infos = (ArrayList)staticobj.getRecordFromObj("LicenseComInfo", "infos");
			}
			if (infos.size() == 5)
			{
				companyname = (String)infos.get(0);
				license = (String)infos.get(1);
				software = (String)infos.get(2);
				hrmnum = (String)infos.get(3);
				expiredate = (String)infos.get(4);
			}
			if (infos.size() == 6)
			{
				companyname = (String)infos.get(0);
				license = (String)infos.get(1);
				software = (String)infos.get(2);
				hrmnum = (String)infos.get(3);
				expiredate = (String)infos.get(4);
				concurrentFlag = (String)infos.get(5);
			}
			if (infos.size() >= 5){//修改后的
				hrmnum = "1000";
				expiredate = "2012-01-01";
			}
		}
		catch (Exception exception)
		{
			writeLog(exception);
		}
	}

	public String getCompanyname()
	{
		return companyname;
	}

	public String getConcurrentFlag()
	{
		return concurrentFlag;
	}

	public String getExpiredate()
	{
		return expiredate;
	}

	public String getHrmnum()
	{
		return hrmnum;
	}

	public String getLicense()
	{
		return license;
	}

	public String getLicensecode()
	{
		return licensecode;
	}

	public String getSoftware()
	{
		return software;
	}

	public void removeLicenseComInfo()
	{
		staticobj.removeObject("LicenseComInfo");
	}

	public void setCompanyname(String s)
	{
		companyname = s.trim();
	}

	public void setConcurrentFlag(String s)
	{
		concurrentFlag = s.trim();
	}

	public void setExpiredate(String s)
	{
		expiredate = s.trim();
	}

	public void setHrmnum(String s)
	{
		hrmnum = s.trim();
	}

	public void setLicense(String s)
	{
		license = s.trim();
	}

	public void setLicensecode(String s)
	{
		licensecode = s.trim();
	}

	public void setLicensepass(String s)
	{
		licensepass = s.trim();
	}

	public void setSoftware(String s)
	{
		software = s.trim();
	}
}
