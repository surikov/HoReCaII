package sweetlife.android10.ui;

import java.util.Date;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Orders;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import sweetlife.android10.R;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Doc_Orders extends Activity_BasePeriod {

	private OrderListAdapter mOrdersListAdapter;

	private ListView mOrdersList;

	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_doc_orders);

		super.onCreate(savedInstanceState);

		setTitle(R.string.doc_orders);

		Cursor cursor = Request_Orders.Request(mDB,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
				DateTimeHelper.SQLDateString(mToPeriod.getTime()));

		mOrdersList = (ListView) findViewById(R.id.order_list);

		mOrdersListAdapter = new OrderListAdapter(this, cursor);

		mOrdersList.setAdapter(mOrdersListAdapter);

		mOrdersList.setOnTouchListener(this);
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {

		mOrdersListAdapter.changeCursor(Request_Orders.Request(mDB,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
				DateTimeHelper.SQLDateString(mToPeriod.getTime())));
		mOrdersListAdapter.notifyDataSetChanged();
	}

	public class OrderListAdapter extends ZoomListCursorAdapter {

		public OrderListAdapter(Context context, Cursor cursor) {

			super(context, cursor);

		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {

			OrderHolder holder = (OrderHolder) row.getTag();

			holder.populateFrom(cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);

			View row = inflater.inflate(R.layout.row_orderlist, parent, false);

			OrderHolder holder = new OrderHolder(row);

			row.setTag(holder);

			return (row);
		}

		public class OrderHolder {

			private TextView date = null;
			private TextView number = null;
			private TextView kontragent = null;
			private TextView type_payment = null;
			private TextView document_summ = null;
			private TextView date_shipment = null;
			private TextView comments = null;

			OrderHolder(View row) {

				date = (TextView) row.findViewById(R.id.row_order_date);
				number = (TextView) row.findViewById(R.id.row_order_number);
				kontragent = (TextView) row.findViewById(R.id.row_order_kontragent);
				type_payment = (TextView) row.findViewById(R.id.row_order_type_payment);
				document_summ = (TextView) row.findViewById(R.id.row_order_document_summ);
				date_shipment = (TextView) row.findViewById(R.id.row_order_date_shipment);
				comments = (TextView) row.findViewById(R.id.row_order_comments);
			}

			void populateFrom(Cursor cursor) {

				float rowTextFontSize = getRowTextFontSize();

				date.setText(Request_Orders.getDate(cursor));
				date.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				number.setText(Request_Orders.getNumber(cursor));
				number.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				kontragent.setText(Request_Orders.getKontragent(cursor));
				kontragent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				type_payment.setText(Request_Orders.getTypePayment(cursor));
				type_payment.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				document_summ.setText(DecimalFormatHelper.format(Request_Orders.getDocumentSumm(cursor)));
				document_summ.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				date_shipment.setText(Request_Orders.getDateShipment(cursor));
				date_shipment.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

				comments.setText(Request_Orders.getComments(cursor));
				comments.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			}
		}
	}
}
