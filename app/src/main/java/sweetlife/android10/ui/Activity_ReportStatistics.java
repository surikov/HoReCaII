package sweetlife.android10.ui;

import java.util.Calendar;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.database.Request_ClientsList;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.reports.ClientsListAdapter;
import sweetlife.android10.reports.ReportStatisticsInfo;
import sweetlife.android10.reports.ReportStatisticsXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import sweetlife.android10.R;

public class Activity_ReportStatistics extends Activity_ReportBase {
	private EditText mEditBeginOrder;
	private EditText mEditEndOrder;
	private Calendar mBeginOrder;
	private Calendar mEndOrder;
	private Request_ClientsList mClientsRequestHelper;
	private AlertDialog mClientDialog;
	private EditText mClientEdit;
	private String mClientCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_report_statistics);
		super.onCreate(savedInstanceState);
		setTitle(R.string.orders_statistics);
		mClientsRequestHelper = new Request_ClientsList();
		/*Calendar from1year=Calendar.getInstance();
		from1year.roll(Calendar.YEAR, -1);
		Calendar to1year=Calendar.getInstance();
		to1year.roll(Calendar.YEAR, 1);		
		mToPeriod = to1year;
		mFromPeriod =from1year;*/
		InitializeControls();
	}/*
	@Override
	public void InitializeDate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mToPeriod = (Calendar) savedInstanceState.getSerializable(SI_TO_DATE);
			mFromPeriod = (Calendar) savedInstanceState.getSerializable(SI_FROM_DATE);
		}
		else {
			mToPeriod = Calendar.getInstance();
			mFromPeriod = Calendar.getInstance();
			//Calendar from1year=Calendar.getInstance();
			mFromPeriod.roll(Calendar.YEAR, -1);
			//Calendar to1year=Calendar.getInstance();
			mToPeriod.roll(Calendar.YEAR, 1);		
			//mToPeriod = to1year;
			//mFromPeriod =from1year;
			mEditToDate.setText(DateTimeHelper.UIDateString(mToPeriod.getTime()));
			mEditFromDate.setText(DateTimeHelper.UIDateString(mFromPeriod.getTime()));
		}
	}
	*/
	
	@Override
	protected String reportRequest() throws Exception {
		ReportStatisticsInfo reportInfo = new ReportStatisticsInfo(REPORT_TYPE_STATISTICS//
				, DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00"//
				, DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59"//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim()//
				, mClientCode//
				, mBeginOrder//
				, mEndOrder//
		);
		return reportRequest(reportInfo, new ReportStatisticsXMLSerializer(reportInfo));
	}
	@Override
	protected void InitializeControls() {
		super.InitializeControls();
		Button btnClients = (Button) findViewById(R.id.btn_client);
		btnClients.setOnClickListener(mChooseClientClick);
		mClientEdit = (EditText) findViewById(R.id.edit_client);
		
		mBeginOrder = Calendar.getInstance();
		mEndOrder = Calendar.getInstance();
		mBeginOrder.roll(Calendar.YEAR, -1);
		mBeginOrder.set(Calendar.MONTH, 1);
		mBeginOrder.set(Calendar.DAY_OF_YEAR, 1);
		mEndOrder.roll(Calendar.YEAR, 1);
		mEndOrder.set(Calendar.MONTH, 1);
		mEndOrder.set(Calendar.DAY_OF_YEAR, 1);
		
		
		mEditBeginOrder = (EditText) findViewById(R.id.edit_order_from_date);
		mEditBeginOrder.setText(DateTimeHelper.UIDateString(mBeginOrder.getTime()));
		mEditEndOrder = (EditText) findViewById(R.id.edit_order_to_date);
		mEditEndOrder.setText(DateTimeHelper.UIDateString(mEndOrder.getTime()));
		findViewById(R.id.btn_order_from_date).setOnClickListener(mBeginDateClick);
		findViewById(R.id.btn_order_to_date).setOnClickListener(mEndDateClick);
	};

	private OnClickListener mBeginDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mBeginOrder.set(year, monthOfYear, dayOfMonth);
					mEditBeginOrder.setText(DateTimeHelper.UIDateString(mBeginOrder.getTime()));
				}
			};
			new DatePickerDialog(Activity_ReportStatistics.this, dateSetListener, mBeginOrder.get(Calendar.YEAR), mBeginOrder.get(Calendar.MONTH),
					mBeginOrder.get(Calendar.DAY_OF_MONTH)).show();
		}
	};
	private OnClickListener mEndDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mEndOrder.set(year, monthOfYear, dayOfMonth);
					mEditEndOrder.setText(DateTimeHelper.UIDateString(mEndOrder.getTime()));
				}
			};
			new DatePickerDialog(Activity_ReportStatistics.this, dateSetListener, mEndOrder.get(Calendar.YEAR), mEndOrder.get(Calendar.MONTH), mEndOrder.get(Calendar.DAY_OF_MONTH))
					.show();
		}
	};
	private OnClickListener mChooseClientClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ShowClientsListDialog();
		}
	};

	private void ShowClientsListDialog() {
		ListView clientsList = (ListView) getLayoutInflater().inflate(R.layout.dialog_list, (ListView) findViewById(R.id.list));
		clientsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ListView clientsList = (ListView) adapterView;
				SetClient(((ClientsListAdapter) clientsList.getAdapter()).getCursor());
				mClientDialog.dismiss();
			}
		});
		ClientsListAdapter clientsListAdapter = new ClientsListAdapter(this, mClientsRequestHelper.Request(mDB, 0,null));
		clientsList.setAdapter(clientsListAdapter);
		LogHelper.debug(this.getClass().getCanonicalName() + ".ShowClientsListDialog: " + getString(R.string.choose_client));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setView(clientsList);
		dialogBuilder.setTitle(getString(R.string.choose_client));
		mClientDialog = dialogBuilder.create();
		mClientDialog.show();
	}
	private void SetClient(Cursor cursor) {
		mClientEdit.setText(Request_ClientsList.getClientName(cursor));
		mClientCode = Request_ClientsList.getClientCode(cursor);
	}
}
