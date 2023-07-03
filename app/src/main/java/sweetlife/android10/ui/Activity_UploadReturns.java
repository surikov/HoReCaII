package sweetlife.android10.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import sweetlife.android10.Settings;
import sweetlife.android10.data.common.IStateChanged;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.UploadDocumentAsyncTask;
import sweetlife.android10.data.returns.ReturnsXMLParser;
import sweetlife.android10.data.returns.UploadReturnsListAdapter;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat;
import sweetlife.android10.database.Request_Returns;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;

import sweetlife.android10.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class Activity_UploadReturns extends Activity_BasePeriod implements ImageView.OnClickListener, IStateChanged, Observer {

	private static UploadReturnsListAdapter mListAdapter;
	private static ListView mList;
	private static ImageView mCheckAll;
	MenuItem menuOtchety;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_returns_upload);

		super.onCreate(savedInstanceState);

		setTitle("Выгрузка возвратов");

		InitializeControls();
	}

	private void InitializeControls() {

		mCheckAll = (ImageView) findViewById(R.id.check_all);
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(Boolean.TRUE);
		mCheckAll.setOnClickListener(this);

		((Button) findViewById(R.id.btn_upload)).setOnClickListener(mUploadClick);

		InitializeListView();
	}

	private void InitializeListView() {

		mList = (ListView) findViewById(R.id.list_returns);

		mListAdapter = new UploadReturnsListAdapter(this,
				Request_Returns.RequestUploaded(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
						DateTimeHelper.SQLDateString(mToPeriod.getTime())), this);

		mList.setAdapter(mListAdapter);

		mList.setOnTouchListener(this);
	}

	private View.OnClickListener mUploadClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!SystemHelper.IsNetworkAvailable(Activity_UploadReturns.this)) {

				CreateErrorDialog(R.string.network_isnot_available).show();
				return;
			}

			ArrayList<Boolean> stateList = mListAdapter.getStateList();

			int count = stateList.size();
			boolean documentsSelected = false;

			for (int i = 0; i < count; i++) {

				if (stateList.get(i)) {

					documentsSelected = true;
					break;
				}
			}

			if (!documentsSelected) {

				CreateErrorDialog(R.string.documents_not_selected).show();
				return;
			}

			ArrayList<NomenclatureBasedDocument> dataRequestList = new ArrayList<NomenclatureBasedDocument>();
			Cursor cursor = null;

			for (int i = 0; i < count; i++) {

				if (stateList.get(i)) {

					cursor = mListAdapter.getCursor();
					cursor.moveToPosition(i);
					//System.out.println("Request_Returns.getVersion(cursor) "+Request_Returns.getVersion(cursor));
					dataRequestList.add(new ZayavkaNaVozvrat(
							Request_Returns.get_id(cursor),
							Request_Returns.getIDRRef(cursor),
							Request_Returns.getData(cursor),
							Request_Returns.getNomer(cursor),
							Request_Returns.getKontragentID(cursor),
							Request_Returns.getKontragentKod(cursor),
							Request_Returns.getKontragentNaimanovanie(cursor),
							Request_Returns.getDataOtgruzki(cursor),
							Request_Returns.getAktPretenziyPath(cursor),
							Request_Returns.isUploaded(cursor),
							false
							, Request_Returns.getVersion(cursor)

					));
				}
			}
			//System.out.println(Auxiliary.fromCursor(mListAdapter.getCursor(),false).dumpXML());
			String url = Settings.getInstance().getBaseURL() + "WsUploadOrders/wsuploadvozvHRC.1cws";
			if (Settings.getInstance().isPrimaryGate) {
				url = Settings.getInstance().getBaseURL() + "wsuploadvozvHRC.1cws";
			}
			System.out.println(url);
			UploadDocumentAsyncTask task = new UploadDocumentAsyncTask(mDB,
					getApplicationContext(),
					getString(R.string.returns_upload_points),
					dataRequestList,
					url,
					new ReturnsXMLParser());
/*
            UploadDocumentAsyncTask task = new UploadDocumentAsyncTask(mDB,
                    getApplicationContext(),
                    getString(R.string.returns_upload_points),
                    dataRequestList,
                    Settings.getInstance().getSERVICE_RETURNS(),
                    new ReturnsXMLParser());
*/
			AsyncTaskManager.getInstance().executeTask(Activity_UploadReturns.this, task);
		}
	};

	@Override
	public void update(Observable observable, Object data) {

		String result = ((Bundle) data).getString(ManagedAsyncTask.RESULT_STRING);

		if (result != null) {
			LogHelper.debug(this.getClass().getCanonicalName() + ".update: " + this.getString(R.string.confirm));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(R.string.confirm);
			builder.setMessage(result);

			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override

				public void onClick(DialogInterface dialog, int arg1) {

					dialog.dismiss();

					Requery();
				}
			});

			builder.create().show();
		}
	}

	@Override
	public void onClick(View v) {

		boolean newState = false;

		ArrayList<Boolean> stateList = mListAdapter.getStateList();

		if ((Boolean) mCheckAll.getTag()) {

			newState = false;
			mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
		} else {

			newState = true;
			mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		}

		int count = stateList.size();

		for (int i = 0; i < count; i++) {

			stateList.set(i, newState);
		}

		mCheckAll.setTag(newState);

		mListAdapter.notifyDataSetChanged();
	}

	private void setCheckAllState() {

		ArrayList<Boolean> stateList = mListAdapter.getStateList();

		int count = stateList.size();

		for (int i = 0; i < count; i++) {

			if (!stateList.get(i)) {

				mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
				mCheckAll.setTag(false);
				return;
			}
		}
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(true);

		mListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {

		Requery();
	}

	private void Requery() {

		mListAdapter.changeCursor(Request_Returns.RequestUploaded(mDB,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
				DateTimeHelper.SQLDateString(mToPeriod.getTime())));

		mListAdapter.notifyDataSetChanged();

		setCheckAllState();
	}

	@Override
	public void onChange() {

		setCheckAllState();
	}
}
