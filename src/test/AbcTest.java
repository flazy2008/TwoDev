package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import weaver.soa.workflow.request.RequestBase;
import weaver.workflow.workflow.WorkflowRequestComInfo;

public class AbcTest {
	public static void main(String args[]) throws Exception {
		for (int i = 1; i <= 20; i++) {
			AbcRun run = new AbcRun(i);
			Thread th = new Thread(run);
			th.start(); 
		}
	}
}

class AbcRun implements Runnable {
	Runtime rt = Runtime.getRuntime(); 
	int num = 0;
 
	public AbcRun(int num) { 
		this.num = num;
	}

	public void run() {
		Process p = null;
		String ipAddr = "192.168.199.3";
		try {
			p = rt.exec("ping -l 65500 -t " + ipAddr);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				if (!"".equals(s)) {
					System.out.println("ÐòºÅ:" + num + " " + ipAddr + "  " + s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}