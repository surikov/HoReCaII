package sweetlife.android10.ui;

import java.util.Calendar;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.reports.ReportInfo;
import sweetlife.android10.reports.ReportKpiInfo;
import sweetlife.android10.reports.ReportKpiXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.R;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class Activity_ReportKPI extends Activity_ReportBase {

	private EditText mEditShippingDate;

	private Calendar mShippingDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_report_kpi);

		super.onCreate(savedInstanceState);

		setTitle(R.string.kpi);
	}

	@Override
	protected void InitializeControls() {

		super.InitializeControls();

		mShippingDate = Calendar.getInstance();
		mShippingDate.roll(Calendar.DAY_OF_YEAR, true);

		mEditShippingDate = (EditText) findViewById(R.id.edit_shipping_date);
		mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));

		Button btnShippingDate = (Button) findViewById(R.id.btn_shipping_date);
		btnShippingDate.setOnClickListener(mShippingDateClick);
	}

	;

	private OnClickListener mShippingDateClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			DatePickerDialog.OnDateSetListener dateSetListener =
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view,
											  int year,
											  int monthOfYear,
											  int dayOfMonth) {

							mShippingDate.set(year, monthOfYear, dayOfMonth);

							mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));
						}
					};

			new DatePickerDialog(Activity_ReportKPI.this,
					dateSetListener,
					mShippingDate.get(Calendar.YEAR),
					mShippingDate.get(Calendar.MONTH),
					mShippingDate.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	@Override
	protected String reportRequest() throws Exception {

		ReportInfo reportInfo = new ReportKpiInfo(REPORT_TYPE_KPI,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00",
				DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59",
				ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim(),
				mShippingDate);

		return reportRequest(reportInfo, new ReportKpiXMLSerializer(reportInfo));
	}

}
