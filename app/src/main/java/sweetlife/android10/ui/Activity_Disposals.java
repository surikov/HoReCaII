package sweetlife.android10.ui;

import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.disposal.DisposalsListAdapter;
import sweetlife.android10.data.disposal.RasporyazhenieNaOtgruzku;
import sweetlife.android10.database.Request_Disposals;
import sweetlife.android10.utils.DateTimeHelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import sweetlife.android10.R;

public class Activity_Disposals extends Activity_BasePeriod {
	private SQLiteDatabase mDB;
	MenuItem menuOtchety;
	private ListView mDisposalsList;
	private DisposalsListAdapter mDisposalsListAdapter;

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_disposals);
		super.onCreate(savedInstanceState);
		mDB = ApplicationHoreca.getInstance().getDataBase();
		setTitle(R.string.disposals_for_shipment);
		InitializeListView();
	}
	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		mDisposalsListAdapter.changeCursor(Request_Disposals.Request(mDB, DateTimeHelper.SQLDateString(fromDate), DateTimeHelper.SQLDateString(toDate)));
		mDisposalsListAdapter.notifyDataSetChanged();
	}
	private void InitializeListView() {
		((Button) findViewById(R.id.btn_add_disposal)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				StartEditDisposalActivity(true, null);
			}
		});
		Calendar today = Calendar.getInstance();
		mDisposalsListAdapter = new DisposalsListAdapter(this, Request_Disposals.Request(mDB, DateTimeHelper.SQLDateString(today.getTime()), DateTimeHelper.SQLDateString(today.getTime())));
		mDisposalsList = (ListView) findViewById(R.id.list_disposals);
		mDisposalsList.setAdapter(mDisposalsListAdapter);
		mDisposalsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = mDisposalsListAdapter.getCursor();
				cursor.moveToPosition(position);
				RasporyazhenieNaOtgruzku disposal = new RasporyazhenieNaOtgruzku(Request_Disposals.get_id(cursor), Request_Disposals.getIDRRef(cursor), Request_Disposals.getData(cursor), Request_Disposals.getNomer(cursor), Request_Disposals.getKontragentID(cursor), Request_Disposals.getKontragentKod(cursor), Request_Disposals.getKontragentNaimanovanie(cursor), Request_Disposals.getSumma(cursor), Request_Disposals.getKommentariy(cursor), Request_Disposals.isUploaded(cursor), false,
						Request_Disposals.RequestFilesList(mDB, Request_Disposals.getIDRRef(cursor)));
				StartEditDisposalActivity(!Request_Disposals.isUploaded(cursor), disposal);
			}
		});
	}
	private void StartEditDisposalActivity(boolean isEditable, RasporyazhenieNaOtgruzku disposal) {
		Intent intent = new Intent();
		intent.setClass(Activity_Disposals.this, Dialog_EditDisposal.class);
		intent.putExtra(IS_EDITABLE, isEditable);
		if (disposal != null) {
			intent.putExtra(DISPOSAL, disposal);
		}
		startActivityForResult(intent, DISPOSAL_ADD);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case DISPOSAL_ADD:
			mDisposalsListAdapter.getCursor().requery();
			mDisposalsListAdapter.notifyDataSetChanged();
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOtchety = menu.add("Отчёты");
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
		return true;
	}
}
