package sweetlife.android10.ui;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.ReportsXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import android.os.Bundle;

import sweetlife.android10.*;


public class Activity_ReportDate extends Activity_ReportBase implements IReportConsts, IDialogTaskAction {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_report_dates);

		super.onCreate(savedInstanceState);
	}

	protected String reportRequest() throws Exception {

		ReportInfo reportInfo = new ReportInfo(mReportType,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00",
				DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59",
				ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim());

		return reportRequest(reportInfo, new ReportsXMLSerializer(reportInfo));
	}
}
