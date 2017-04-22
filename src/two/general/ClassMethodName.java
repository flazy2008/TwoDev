package two.general;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ClassMethodName {
	public Map getMethodName(String str) {
		Map map = new HashMap();
		try {
			Class cls = Class.forName(str);// 加载UserBean类到内存中！获取一个Class对象
			map = this.getMethodName(cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public Map getMethodName(Class cls) {
		Map map = new TreeMap();
		try {
			Method[] methods = cls.getMethods();// 得到某类的所有方法
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
