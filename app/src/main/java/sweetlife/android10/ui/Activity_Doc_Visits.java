package sweetlife.android10.ui;

import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Expect;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Visits;
import sweetlife.android10.gps.UploadTask;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.SystemHelper;
import sweetlife.android10.R;
import tee.binding.task.*;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Doc_Visits extends Activity_BasePeriod {

	private VisitListAdapter mVisitsListAdapter;
	private ListView mVisitsList;
	MenuItem menuOtchety;
	MenuItem menuVizity;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOtchety = menu.add("Отчёты");
		menuVizity = menu.add("Повторить выгрузку");
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
		if (item == menuVizity) {
			uploadAgain();
			return true;
		}
		return true;
	}

	void uploadAgain() {
		String sql = "update Vizits set upload=0 where beginDate='" + DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "';";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		//String sql="select * from Vizits where beginDate='"+DateTimeHelper.SQLDateString(mFromPeriod.getTime())+"';";
		//System.out.println(sql);
		//final Note res=new Note();
		final UploadTask task = new UploadTask(ApplicationHoreca.getInstance().getDataBase(), SystemHelper.getDiviceID(Activity_Doc_Visits.this), getApplicationContext());
		new Expect().status.is("Выгрузка визитов...").task.is(new Task() {
			public void doTask() {
				try {
					task.UploadVizits();
					System.out.println("task.mResultString " + task.mResultString);
					//res.value(task.mResultString);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			public void doTask() {
				Auxiliary.warn(task.mResultString, Activity_Doc_Visits.this);
			}
		}).start(Activity_Doc_Visits.this);
	}

	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_doc_visits);

		super.onCreate(savedInstanceState);

		setTitle("Визиты по дате");

		Cursor cursor = Request_Visits.Request(mDB,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
				DateTimeHelper.SQLDateString(mToPeriod.getTime()));

		mVisitsList = (ListView) findViewById(R.id.visit_list);

		mVisitsListAdapter = new VisitListAdapter(this, cursor);

		mVisitsList.setAdapter(mVisitsListAdapter);

		mVisitsList.setOnTouchListener(this);

	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {

		Cursor cursor = Request_Visits.Request(mDB,
				DateTimeHelper.SQLDateString(fromDate),
				DateTimeHelper.SQLDateString(toDate));

		mVisitsListAdapter.changeCursor(cursor);
		mVisitsListAdapter.notifyDataSetChanged();
	}

	public class VisitListAdapter extends ZoomListCursorAdapter {

		public VisitListAdapter(Context context, Cursor cursor) {

			super(context, cursor);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {

			VisitHolder holder = (VisitHolder) row.getTag();

			holder.populateFrom(cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);

			View row = inflater.inflate(R.layout.row_visitlist, parent, false);

			VisitHolder holder = new VisitHolder(row);

			row.setTag(holder);

			return (row);
		}


		public class VisitHolder {

			private ImageView uploaded = null;
			private TextView date = null;
			private TextView start_time = null;
			private TextView end_time = null;
			private TextView kontragent = null;
			private TextView activity = null;

			VisitHolder(View row) {

				uploaded = (ImageView) row.findViewById(R.id.image_uploaded);
				date = (TextView) row.findViewById(R.id.row_visit_date);
				start_time = (TextView) row.findViewById(R.id.row_visit_start_time);
				end_time = (TextView) row.findViewById(R.id.row_visit_end_time);
				kontragent = (TextView) row.findViewById(R.id.row_visit_kontragent);
				activity = (TextView) row.findViewById(R.id.row_visit_activity);
			}

			void populateFrom(Cursor cursor) {

				if (Request_Visits.isUpload(cursor)) {

					uploaded.setImageResource(android.R.drawable.checkbox_on_background);
				} else {

					uploaded.setImageResource(android.R.drawable.checkbox_off_background);
				}

				float rowTextFontSize = getRowTextFontSize();

				date.setText(DateTimeHelper.UIDateString(DateTimeHelper.SQLDateToDate(Request_Visits.getDate(cursor))));
				date.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				String timeString = null;
				try {

					timeString = Request_Visits.getStartTime(cursor);

					start_time.setText(timeString.substring(timeString.lastIndexOf("T") + 1));
				} catch (Exception e) {

					start_time.setText("");
				}

				try {

					timeString = Request_Visits.getEndTime(cursor);
					end_time.setText(timeString.substring(timeString.lastIndexOf("T") + 1));
				} catch (Exception e) {

					end_time.setText("");
				}

				start_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				end_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				kontragent.setText(Request_Visits.getKontragent(cursor));
				kontragent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				activity.setText(Request_Visits.getActivity(cursor));
				activity.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			}
		}
	}
}
