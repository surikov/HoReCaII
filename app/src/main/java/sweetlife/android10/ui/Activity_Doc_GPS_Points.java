package sweetlife.android10.ui;

import java.text.*;
import java.util.*;

import reactive.ui.*;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.R;
import tee.binding.task.Task;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.*;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Doc_GPS_Points extends Activity_BasePeriod {
	private ListView mPointsList;
	private GPSPointsListAdapter mGPSPointstListAdapter;
	MenuItem menuOtchety;
	MenuItem menuRefresh;
	MenuItem menuResetUpload;
	int TIME_NEW = 0xff000000;
	int TIME_MIDDLE = 0x99000000;
	int TIME_OLD = 0x33000000;
	int middleMinutes = 30;
	int newMinutes = 3;
	//int TIME_UPLOADED = 0x000000;
	//int TIME_FRESH = 0x0000ff;
	int hourFifference = +3;//+4;
	final static String uploaded = "да";
	final static String notuploaded = "нет";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	;
	SimpleDateFormat to = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	;
	public static boolean autoRefreshDone = false;

	void startAutoRefresh() {
		System.out.println("startAutoRefresh");

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				autoRefreshDone = false;
				while (!autoRefreshDone) {
					try {
						publishProgress();
						Thread.sleep(20 * 1000);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Void... progress) {
				OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
			}
		}.execute();

		/*
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(!autoRefreshDone){
					try {
						OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
						Thread.sleep(30*1000);
					}
					catch (Throwable t) {
						t.printStackTrace();
					}
				}				
			}}).start(); */
	}

	void stopAutoRefresh() {
		System.out.println("startAutoRefresh");
		autoRefreshDone = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//startAutoRefresh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//stopAutoRefresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuRefresh = menu.add("Обновить");
		menuOtchety = menu.add("Отчёты");
		menuResetUpload = menu.add("Повторить выгрузку");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if (item == menuRefresh) {
			//Cursor cursor = RequestGPSPoints(DateTimeHelper.SQLDateString(fromDate), DateTimeHelper.SQLDateString(toDate));
			//mGPSPointstListAdapter.changeCursor(cursor);
			//mGPSPointstListAdapter.notifyDataSetChanged();
			OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
			return true;
		}
		if (item == menuResetUpload) {
			promptResetUpload();
			return true;
		}
		return true;
	}

	void promptResetUpload() {
		Auxiliary.pickConfirm(this, "Пометить все точки как невыгруженные", "Да", new Task() {

			@Override
			public void doTask() {
				//String sql="update Vizits set upload=0;";
				//mDB.execSQL(sql);
				String sql = "update GPSPoints set upload=0;";
				mDB.execSQL(sql);
				OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
			}

		});
	}

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_doc_gps_points);
		super.onCreate(savedInstanceState);
		setTitle(R.string.doc_gps_points);
		Cursor cursor = RequestGPSPoints(DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime()));
		mPointsList = (ListView) findViewById(R.id.list_gps_points);
		mGPSPointstListAdapter = new GPSPointsListAdapter(this, cursor);
		mPointsList.setAdapter(mGPSPointstListAdapter);
		mPointsList.setOnTouchListener(this);
	}

	private Cursor RequestGPSPoints(String dateFrom, String dateTo) {
		System.out.println("RequestGPSPoints from " + dateFrom + " to " + dateTo);
		String sql = "select _id,BeginDate,BeginTime,longitude,latitude,Upload from GPSPoints where date(BeginDate) >= '" //
				+ dateFrom + "' and date(BeginDate) <= '"//
				+ dateTo + "'" //
				+ "ORDER BY _id desc";
		return mDB.rawQuery(sql, null);
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		//System.out.println("OnDateChanged from " + fromDate + " to " + toDate);
		Cursor cursor = RequestGPSPoints(DateTimeHelper.SQLDateString(fromDate), DateTimeHelper.SQLDateString(toDate));
		mGPSPointstListAdapter.changeCursor(cursor);
		mGPSPointstListAdapter.notifyDataSetChanged();
	}

	public class GPSPointsListAdapter extends ZoomListCursorAdapter {
		public GPSPointsListAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			GPSPointsHolder holder = (GPSPointsHolder) row.getTag();
			holder.populateFrom(cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View row = inflater.inflate(R.layout.row_gps_points_list, parent, false);
			GPSPointsHolder holder = new GPSPointsHolder(row);
			row.setTag(holder);
			return (row);
		}

		public class GPSPointsHolder implements ITableColumnsNames {
			private TextView date = null;
			private TextView time = null;
			private TextView shirota = null;
			private TextView dolgota = null;

			GPSPointsHolder(View row) {
				date = (TextView) row.findViewById(R.id.row_date);
				time = (TextView) row.findViewById(R.id.row_time);
				shirota = (TextView) row.findViewById(R.id.row_shirota);
				dolgota = (TextView) row.findViewById(R.id.row_dolgota);
			}

			void populateFrom(Cursor cursor) {
				int upload = cursor.getInt(cursor.getColumnIndex(UPLOAD));
				float rowTextFontSize = 17;//11;//getRowTextFontSize();
				//int uploadColor = 0xff000000;
				int transparency = TIME_NEW;
				/*if (upload > 0) {
					//rowTextFontSize = 14;
					uploadColor = TIME_UPLOADED;
				}*/
				String timeString = cursor.getString(cursor.getColumnIndex(BEGIN_TIME));
				Date dateTime = null;
				try {
					long now = new Date().getTime();
					long pointDate = simpleDateFormat.parse(timeString).getTime() + hourFifference * 60 * 60 * 1000L;
					dateTime = new Date(pointDate);
					//System.out.println(now+" / "+pointDate);
					if (now - pointDate < newMinutes * 60 * 1000L) {
						transparency = TIME_NEW;
					} else {
						if (now - pointDate < middleMinutes * 60 * 1000L) {
							transparency = TIME_MIDDLE;
						} else {
							transparency = TIME_OLD;
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				int color = //uploadColor + 
						transparency;
				//System.out.println("rowTextFontSize "+rowTextFontSize);
				if (upload > 0) {
					date.setText(uploaded);
				} else {
					date.setText(notuploaded);
				}
				//date.setText(DateTimeHelper.UIDateString(DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex(BEGIN_DATE)))));
				//date.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				date.setTextColor(color);
				date.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				time.setText(to.format(dateTime));
				//timeString.substring(timeString.lastIndexOf("T") + 1) + " ( +4 часа Московское время )");
				time.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				time.setTextColor(color);
				shirota.setText(new Double(cursor.getDouble(cursor.getColumnIndex(LONGITUDE))).toString());
				shirota.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				shirota.setTextColor(color);
				dolgota.setText(new Double(cursor.getDouble(cursor.getColumnIndex(LATITUDE))).toString());
				dolgota.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				dolgota.setTextColor(color);
			}
		}
	}
}
