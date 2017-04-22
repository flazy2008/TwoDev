package two.general.service;

import weaver.general.BaseBean;
import weaver.hrm.resource.ResourceComInfo;

public class Test extends BaseBean {
	public static void main(String[] args) {
		String check_per=",";
		check_per=check_per.endsWith(",")?check_per.substring(0, check_per.length()-1):check_per;
		System.out.println(check_per);

	}
}
