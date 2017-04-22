package two.work.cronjob;

import two.work.hrm.HrmScheduleData;
import weaver.interfaces.schedule.BaseCronJob;

public class HrmScheduleCronJob extends BaseCronJob {
	public void execute() {
		try {
			new HrmScheduleData().loadScheduleData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
