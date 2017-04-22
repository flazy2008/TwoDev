// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ImageFileIdUpdate.java

package weaver.docs.docs;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class ImageFileIdUpdate extends BaseBean
{

	private static ImageFileIdUpdate imageFileIdUpdate = new ImageFileIdUpdate();
	private String billtablename;

	public ImageFileIdUpdate()
	{
		imageFileIdUpdate = getInstance();
	}

	public synchronized int getImageFileNewId()
	{
		int i = imageFileIdUpdate.getImageFileNewId(billtablename);
		return i;
	}

	private synchronized int getImageFileNewId(String s)
	{
		int i = -1;
		try
		{
			RecordSet recordset = new RecordSet();
			recordset.executeProc("SequenceIndex_SelectFileid", "");
			if (recordset.next())
				i = Util.getIntValue(recordset.getString(1), -1);
		}
		catch (Exception exception)
		{
			i = -1;
			exception.printStackTrace();
		}
		return i;
	}

	private static ImageFileIdUpdate getInstance()
	{
		return imageFileIdUpdate;
	}

}
