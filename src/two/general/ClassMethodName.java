package two.general;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ClassMethodName {
	public Map getMethodName(String str) {
		Map map = new HashMap();
		try {
			Class cls = Class.forName(str);// ����UserBean�ൽ�ڴ��У���ȡһ��Class����
			map = this.getMethodName(cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public Map getMethodName(Class cls) {
		Map map = new TreeMap();
		try {
			Method[] methods = cls.getMethods();// �õ�ĳ������з���
			String fieldname = "";
			for (Method a : methods) {
				fieldname = a.getName();
				if (fieldname.indexOf("Look") > -1
						|| fieldname.indexOf("get") > -1) {
					map.put("common." + fieldname, "args:"
							+ a.getGenericParameterTypes().length + " "
							+ fieldname);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
