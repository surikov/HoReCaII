package sweetlife.android10.ui;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.R;
//import sweetlife.horeca.R.menu;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.*;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import android.os.Bundle;

public class Activity_ReportPredzakazTrafik extends Activity_ReportBase implements IReportConsts, IDialogTaskAction {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		//setContentView(R.layout.act_report_predzakaz_trafik);
		setContentView(R.layout.act_report_predzakaz_trafik);
		super.onCreate(savedInstanceState);
		setTitle("Предзаказы на трафик");
	}

	@Override
	protected String reportRequest() throws Exception {


		ReportInfo reportInfo = new ReportPredzTrafiksInfo(REPORT_TYPE_PREDZTRAFIKS,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00",
				DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59",
				ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim());

		return reportRequest(reportInfo, new ReportPredzTrafiksXMLSerializer(reportInfo));
	}
}
