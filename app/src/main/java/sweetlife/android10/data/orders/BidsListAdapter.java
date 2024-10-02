package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sweetlife.android10.*;

public class BidsListAdapter extends ZoomListCursorAdapter {

	public BidsListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		BidsHolder holder = (BidsHolder) row.getTag();

		holder.populateFrom(cursor, getRowTextFontSize());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_bids, view, false);

		BidsHolder holder = new BidsHolder(row);

		row.setTag(holder);

		return (row);
	}

	public class BidsHolder {

		private TextView mTextNumber = null;
		private TextView mTextContract = null;
		private TextView mTextPaymentType = null;
		private TextView mTextShippingDate = null;
		private TextView mTextAmount = null;

		BidsHolder(View row) {

			mTextNumber = (TextView) row.findViewById(R.id.text_number);
			mTextContract = (TextView) row.findViewById(R.id.text_contract);
			mTextPaymentType = (TextView) row.findViewById(R.id.text_paymenttype);
			mTextShippingDate = (TextView) row.findViewById(R.id.text_shippingdate);
			mTextAmount = (TextView) row.findViewById(R.id.text_amount);
		}

		void populateFrom(Cursor cursor, float rowTextFontSize) {

			mTextNumber.setText(Request_Bids.getNomer(cursor));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextContract.setText(Request_Bids.getDogovorKontragentaName(cursor));
			mTextContract.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextPaymentType.setText(ZayavkaPokupatelya.getTipOplatyName(Request_Bids.getTipOplaty(cursor)));
			mTextPaymentType.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextShippingDate.setText(DateTimeHelper.UIDateString(Request_Bids.getDataOtgruzki(cursor)));
			mTextShippingDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextAmount.setText(DecimalFormatHelper.format(Request_Bids.getSumma(cursor)));
			mTextAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
		}
	}
}
