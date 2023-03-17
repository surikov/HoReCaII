package sweetlife.android10.ui;

import java.util.Date;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.data.orders.ZayavkaPokupatelya;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.R;

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

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Doc_Bids extends Activity_BasePeriod {
	private BidsListAdapter mBidsListAdapter;
	private ListView mBidsList;
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
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_doc_bids);
		super.onCreate(savedInstanceState);
		setTitle(R.string.doc_bids);
		Cursor cursor = Request_Bids.RequestPeriod(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime()), false);
		mBidsList = (ListView) findViewById(R.id.bids_list);
		mBidsListAdapter = new BidsListAdapter(this, cursor);
		mBidsList.setAdapter(mBidsListAdapter);
		mBidsList.setOnTouchListener(this);
		mBidsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = mBidsListAdapter.getCursor();
				cursor.moveToPosition(position);
				ZayavkaPokupatelya bid = new ZayavkaPokupatelya(Request_Bids.get_id(cursor)//
						, Request_Bids.getIDRRef(cursor)//
						, Request_Bids.getData(cursor)//
						, Request_Bids.getNomer(cursor)//
						, Request_Bids.isProveden(cursor)//
						, Request_Bids.getDataOtgruzki(cursor)//
						, Request_Bids.getDogovorKontragenta(cursor)//
						, Request_Bids.getKommentariy(cursor)//
						, Request_Bids.getKontragentID(cursor)//
						, Request_Bids.getKontragentKod(cursor)//
						, Request_Bids.getKontragentNaimanovanie(cursor)//
						, Request_Bids.getSumma(cursor)//
						, Request_Bids.getTipOplaty(cursor)//
						, Request_Bids.getTipOplatyPoryadok(cursor)//
						, Request_Bids.getSebestoimost(cursor)//
						, false);
				boolean is_editable = !Request_Bids.isProveden(cursor);
				Intent intent = new Intent();
				intent.setClass(Activity_Doc_Bids.this, Activity_Bid.class);
				intent.putExtra(CLIENT_ID, Request_Bids.getKontragentID(cursor));
				intent.putExtra(IS_EDITABLE, is_editable);
				intent.putExtra(BID, bid);
				startActivityForResult(intent, ORDER_UPDATE);
			}
		});
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mBidsListAdapter.changeCursor(Request_Bids.RequestPeriod(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime()), false));
		mBidsListAdapter.notifyDataSetChanged();
	}
	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		mBidsListAdapter.changeCursor(Request_Bids.RequestPeriod(mDB, DateTimeHelper.SQLDateString(fromDate), DateTimeHelper.SQLDateString(toDate), false));
		mBidsListAdapter.notifyDataSetChanged();
	}

	public class BidsListAdapter extends ZoomListCursorAdapter {
		public BidsListAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}
		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			BidsHolder holder = (BidsHolder) row.getTag();
			holder.populateFrom(cursor);
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View row = inflater.inflate(R.layout.row_doc_bids, parent, false);
			BidsHolder holder = new BidsHolder(row);
			row.setTag(holder);
			return (row);
		}

		public class BidsHolder {
			private TextView date = null;
			private TextView number = null;
			private TextView document_summ = null;
			private TextView kontragent = null;
			private TextView date_shipment = null;
			private TextView comments = null;

			BidsHolder(View row) {
				date = (TextView) row.findViewById(R.id.row_bids_date);
				number = (TextView) row.findViewById(R.id.row_bids_number);
				document_summ = (TextView) row.findViewById(R.id.row_bids_document_summ);
				kontragent = (TextView) row.findViewById(R.id.row_bids_kontragent);
				date_shipment = (TextView) row.findViewById(R.id.row_bids_date_shipment);
				comments = (TextView) row.findViewById(R.id.row_bids_comments);
			}
			void populateFrom(Cursor cursor) {
				float rowTextFontSize = getRowTextFontSize();
				date.setText(DateTimeHelper.UIDateString(Request_Bids.getData(cursor)));
				date.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				number.setText(Request_Bids.getNomer(cursor));
				if (Request_Bids.isProveden(cursor)) {
					number.setTextColor(0xff000000);
				}
				else {
					number.setTextColor(0xff000099);
				}
				number.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				document_summ.setText(String.valueOf(Request_Bids.getSumma(cursor)));
				document_summ.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				kontragent.setText(Request_Bids.getKontragentNaimanovanie(cursor));
				kontragent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				date_shipment.setText(DateTimeHelper.UIDateString(Request_Bids.getDataOtgruzki(cursor)));
				date_shipment.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
				comments.setText(Request_Bids.getKommentariy(cursor));
				comments.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			}
		}
	}
}
