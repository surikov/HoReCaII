package sweetlife.android10.ui;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.ReportTrafiksInfo;
import sweetlife.android10.reports.ReportTrafiksXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import android.os.Bundle;
import android.widget.RadioButton;

import sweetlife.android10.*;

public class Activity_ReportTrafiks extends Activity_ReportBase implements IReportConsts, IDialogTaskAction {

	private Boolean mOnlyShipped;
	private Boolean mOnlyNotShipped;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_report_trafiks);

		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.report_trafiks));
	}

	private void setShippedOrNot() {

		if (((RadioButton) findViewById(R.id.radio_all)).isChecked()) {

			mOnlyNotShipped = false;
			mOnlyShipped = false;
		}
		if (((RadioButton) findViewById(R.id.radio_only_shipped)).isChecked()) {

			mOnlyNotShipped = false;
			mOnlyShipped = true;
		}
		if (((RadioButton) findViewById(R.id.radio_only_not_shipped)).isChecked()) {

			mOnlyNotShipped = true;
			mOnlyShipped = false;
		}
	}

	protected String reportRequest() throws Exception {

		setShippedOrNot();

		ReportInfo reportInfo = new ReportTrafiksInfo(REPORT_TYPE_TRAFIKS,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00",
				DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59",
				ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim(),
				mOnlyShipped,
				mOnlyNotShipped);

		return reportRequest(reportInfo, new ReportTrafiksXMLSerializer(reportInfo));
	}
}
