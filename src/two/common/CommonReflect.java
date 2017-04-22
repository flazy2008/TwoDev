package two.common;

import java.lang.reflect.Method;


public class CommonReflect {
	public Object invoke(String className, String methodName, Object[] objects) {
		Object object = new Object();
		try {
			Class clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					Class[] clazz1 = m.getParameterTypes();
					object = m.invoke(obj, objects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static void main(String[] args) {
		new CommonReflect().invoke("two.common.CommonInfoByID", "LookNameByHrmID",new Object[]{"30"} );
	}
}
