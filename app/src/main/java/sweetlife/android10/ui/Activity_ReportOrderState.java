package sweetlife.android10.ui;

import java.util.Vector;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.ReportOrderStateInfo;
import sweetlife.android10.reports.ReportOrderStateXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.SystemHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import android.os.Bundle;
import android.widget.CheckBox;

import sweetlife.android10.*;

public class Activity_ReportOrderState extends Activity_ReportBase implements IReportConsts, IDialogTaskAction {
	private CheckBox mCheckOnlyCompleted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_report_order_state);
		super.onCreate(savedInstanceState);
		mCheckOnlyCompleted = (CheckBox) findViewById(R.id.check_only_not_completed);
		setTitle(R.string.order_states);
	}

	protected String reportRequest() throws Exception {
		ReportInfo reportInfo = new ReportOrderStateInfo(REPORT_TYPE_ORDER_STATES//
				, DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00"//
				, DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59"//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim()//
				, mCheckOnlyCompleted.isChecked()//
		);
		return reportRequest(reportInfo, new ReportOrderStateXMLSerializer(reportInfo));
	}

	@Override
	public void htmlHook() {
		LogHelper.debug(this.getClass().getCanonicalName() + " start htmlHook for " + mReportFilePath);
		java.io.File f = new java.io.File(mReportFilePath);
		Vector<String> strings = SystemHelper.readTextFromFile(f);
		for (int i = 0; i < strings.size(); i++) {
			String line = strings.get(i);
			if (i - 2 > -1 && i + 1 < strings.size() - 1) {
				String num = SystemHelper.extract(line, '№', '<');
				String dat = SystemHelper.extract(strings.get(i - 2), '>', '<');
				String ship = SystemHelper.extract(strings.get(i + 1), '>', '<');
				if (num.length() > 2) {
					int start = line.indexOf('№');
					int end = line.indexOf("<", start + 1);
					//System.out.println(line);
					line = line.substring(0, start)//
							+ "№<a href=\"hook"//
							+ "?kind=" + HOOKReportOrderState// 
							+ "&" + FIELDDocumentNumber + "=" + num //
							+ "&" + FIELDDocumentDate + "=" + dat//
							+ "&" + FIELDShipDate + "=" + ship//
							+ "\">" + num + "</a>"//
							+ line.substring(end);
					//System.out.println(line);
					strings.set(i, line);
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.size(); i++) {
			sb.append(strings.get(i));
		}
		SystemHelper.writeTextToFile(f, sb.toString());
		LogHelper.debug(this.getClass().getCanonicalName() + " done htmlHook for " + mReportFilePath);
	}
}
