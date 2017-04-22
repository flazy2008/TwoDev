package two.work.cronjob;

import two.common.CommonRemind;
import weaver.interfaces.schedule.BaseCronJob;

public class RemindCronJob extends BaseCronJob {
	public void execute() {
		try {
			new CommonRemind().sysDetection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
