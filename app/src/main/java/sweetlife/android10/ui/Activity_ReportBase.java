package sweetlife.android10.ui;

import java.util.Date;
import java.util.Observable;

import org.apache.http.HttpStatus;

import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.ReportsXMLParser;
import sweetlife.android10.reports.ReportsXMLSerializer;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DialogTask;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.UIHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import sweetlife.android10.R;

public abstract class Activity_ReportBase extends Activity_BasePeriod implements IReportConsts, IDialogTaskAction {
	private final int TIMEOUT = 300*1000;
	protected String mReportType;
	protected String mReportFilePath;
	public final static String HOOKReportOrderState = "HookReportOrderState";
	public final static String FIELDDocumentDate = "documentDate";
	public final static String FIELDShipDate = "shipDate";
	public final static String FIELDDocumentNumber = "documentNumber";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EXTRA_REPORT_TYPE)) {
			mReportType = extras.getString(EXTRA_REPORT_TYPE);
			setTitle(extras.getString(EXTRA_REPORT_NAME));
		}
		InitializeControls();
	}
	protected void InitializeControls() {
		((Button) findViewById(R.id.btn_generate)).setOnClickListener(mGenerateReportClick);
	}

	protected OnClickListener mGenerateReportClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (validateData()) {
				ShowReport();
			}
		}
	};

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		// Nothing to do
	}
	protected void ShowReport() {
		DialogTask task = new DialogTask(getString(R.string.request_report), getApplicationContext(), this);
		AsyncTaskManager.getInstance().executeTask(this, task);
	}
	protected abstract String reportRequest() throws Exception;
	protected String reportRequest(ReportInfo reportInfo, ReportsXMLSerializer serializer) throws Exception {
		String requestStr = serializer.SerializeXML();
		if (requestStr != null && requestStr.length() != 0) {
			HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_REPORTS());
			request.setTimeOut(TIMEOUT);
			if (request.Execute(requestStr) != HttpStatus.SC_OK) {
				return null;
			}
			return request.getResponse();
		}
		return null;
	}
	protected boolean validateData() {
		if (DateTimeHelper.getOnlyDateInfo(mFromPeriod).compareTo(DateTimeHelper.getOnlyDateInfo(mToPeriod)) > 0) {
			UIHelper.MsgBox(getString(R.string.error), getString(R.string.error_date), this, null);
			return false;
		}
		return true;
	}
	protected void handleError(int errorCode) {
		switch (errorCode) {
		case ERROR_REQUEST_DATA:
			UIHelper.MsgBox(getString(R.string.error), getString(R.string.error_web_service_access), this, null);
			return;
		case ERROR_PARSE_DATA:
			UIHelper.MsgBox(getString(R.string.error), getString(R.string.error_data_saving), this, null);
			return;
		}
	}
	public void htmlHook() {
		//LogHelper.debug(this.getClass().getCanonicalName()+" update for "+mReportFilePath);
	}
	@Override
	public void update(Observable observable, Object data) {
		int errorCode = ((Bundle) data).getInt(ManagedAsyncTask.RESULT_INTEGER);
		if (errorCode == DialogTask.ERROR_NONE) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_REPORT_FILE_PATH, mReportFilePath);
			intent.setClass(Activity_ReportBase.this, Activity_ReportShowReport.class);
			startActivity(intent);
			finish();
		}
		else {
			handleError(errorCode);
		}
		super.update(observable, data);
	}
	@Override
	public int onAction() {
		String responceXML = null;
		try {
			responceXML = reportRequest();
		}
		catch (Exception e) {
			e.printStackTrace();
			LogHelper.debug(this.getClass().getCanonicalName() + " " + e.getMessage());
			return ERROR_REQUEST_DATA;
		}
		if (responceXML != null && responceXML.length() != 0) {
			ReportsXMLParser parser = new ReportsXMLParser();
			try {
				parser.Parse(responceXML);
				mReportFilePath = parser.getReportFilePath();
				htmlHook();
			}
			catch (Exception e) {
				e.printStackTrace();
				LogHelper.debug(this.getClass().getCanonicalName() + " " + e.getMessage());
				return ERROR_PARSE_DATA;
			}
		}
		else {
			LogHelper.debug(this.getClass().getCanonicalName() + " no responceXML");
			return ERROR_REQUEST_DATA;
		}
		return DialogTask.ERROR_NONE;
	}
	
}
