// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HrmScheduleDiffListComparator.java

package weaver.hrm.report.schedulediff;

import java.util.Comparator;
import java.util.Map;
import weaver.general.Util;

public class HrmScheduleDiffListComparator
	implements Comparator
{

	public HrmScheduleDiffListComparator()
	{
	}

	public int compare(Object obj, Object obj1)
	{
		int i = 0;
		Map map = (Map)obj;
		Map map1 = (Map)obj1;
		int j = Util.getIntValue((String)map.get("subCompanyId"), -1);
		int k = Util.getIntValue((String)map1.get("subCompanyId"), -1);
		if (j > k)
		{
			i = 1;
			return i;
		}
		if (j < k)
		{
			i = -1;
			return i;
		}
		int l = Util.getIntValue((String)map.get("departmentId"), -1);
		int i1 = Util.getIntValue((String)map1.get("departmentId"), -1);
		if (l > i1)
		{
			i = 1;
			return i;
		}
		if (l < i1)
		{
			i = -1;
			return i;
		}
		int j1 = Util.getIntValue((String)map.get("resourceId"), -1);
		int k1 = Util.getIntValue((String)map1.get("resourceId"), -1);
		if (j1 > k1)
		{
			i = 1;
			return i;
		}
		if (j1 < k1)
		{
			i = -1;
			return i;
		}
		String s = Util.null2String((String)map.get("signDate"));
		String s1 = Util.null2String((String)map1.get("signDate"));
		if (s.compareTo(s1) > 0)
		{
			i = 1;
			return i;
		}
		if (s.compareTo(s1) < 0)
		{
			i = -1;
			return i;
		}
		int l1 = Util.getIntValue((String)map.get("signId"), -1);
		int i2 = Util.getIntValue((String)map1.get("signId"), -1);
		if (l1 > i2)
		{
			i = 1;
			return i;
		}
		if (l1 < i2)
		{
			i = -1;
			return i;
		} else
		{
			return i;
		}
	}
}
