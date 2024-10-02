package sweetlife.android10.ui;

import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.*;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public abstract class Activity_BasePeriod extends Activity_Base {
	public final String SI_FROM_DATE = "from_date";
	public final String SI_TO_DATE = "to_date";
	public EditText mEditFromDate;
	public EditText mEditToDate;
	protected Calendar mFromPeriod;
	protected Calendar mToPeriod;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEditFromDate = (EditText) findViewById(R.id.edit_from_date);
		mEditToDate = (EditText) findViewById(R.id.edit_to_date);
		Button btnFromDate = (Button) findViewById(R.id.btn_from_date);
		btnFromDate.setOnClickListener(mFromDateClick);
		Button btnToDate = (Button) findViewById(R.id.btn_to_date);
		btnToDate.setOnClickListener(mToDateClick);
		InitializeDate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SI_FROM_DATE, mFromPeriod);
		outState.putSerializable(SI_TO_DATE, mToPeriod);
		super.onSaveInstanceState(outState);
	}

	public void InitializeDate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mToPeriod = (Calendar) savedInstanceState.getSerializable(SI_TO_DATE);
			mFromPeriod = (Calendar) savedInstanceState.getSerializable(SI_FROM_DATE);
		} else {
			mToPeriod = Calendar.getInstance();
			mFromPeriod = Calendar.getInstance();
			mEditToDate.setText(DateTimeHelper.UIDateString(mToPeriod.getTime()));
			mEditFromDate.setText(DateTimeHelper.UIDateString(mFromPeriod.getTime()));
		}
	}

	protected void UpdateDate() {
		mEditToDate.setText(DateTimeHelper.UIDateString(mToPeriod.getTime()));
		mEditFromDate.setText(DateTimeHelper.UIDateString(mFromPeriod.getTime()));
	}

	private OnClickListener mFromDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mFromPeriod.set(year, monthOfYear, dayOfMonth);
					mEditFromDate.setText(DateTimeHelper.UIDateString(mFromPeriod.getTime()));
					OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
				}
			};
			new DatePickerDialog(Activity_BasePeriod.this, dateSetListener, mFromPeriod.get(Calendar.YEAR), mFromPeriod.get(Calendar.MONTH), mFromPeriod.get(Calendar.DAY_OF_MONTH))
					.show();
		}
	};
	private OnClickListener mToDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mToPeriod.set(year, monthOfYear, dayOfMonth);
					mEditToDate.setText(DateTimeHelper.UIDateString(mToPeriod.getTime()));
					OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
				}
			};
			new DatePickerDialog(Activity_BasePeriod.this, dateSetListener, mToPeriod.get(Calendar.YEAR), mToPeriod.get(Calendar.MONTH), mToPeriod.get(Calendar.DAY_OF_MONTH))
					.show();
		}
	};

	protected abstract void OnDateChanged(Date fromDate, Date toDate);
}
