package sweetlife.android10.data.returns;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Returns;
import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.*;

import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ReturnsListAdapter extends ZoomListCursorAdapter {

	public ReturnsListAdapter(Context context, Cursor c) {
		super(context, c);

	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		ReturnHolder holder = (ReturnHolder) row.getTag();

		holder.populateFrom(cursor, getRowTextFontSize());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_returns, view, false);

		ReturnHolder holder = new ReturnHolder(row);

		row.setTag(holder);

		return (row);
	}

	public class ReturnHolder {

		private ImageView mImageUploaded = null;
		private TextView mTextNumber = null;
		private TextView mTextDate = null;
		private TextView mTextClient = null;
		private TextView mTextShippingDate = null;

		ReturnHolder(View row) {

			mImageUploaded = (ImageView) row.findViewById(R.id.image_uploaded);
			mTextNumber = (TextView) row.findViewById(R.id.text_number);
			mTextDate = (TextView) row.findViewById(R.id.text_date);
			mTextClient = (TextView) row.findViewById(R.id.text_kontragent);
			mTextShippingDate = (TextView) row.findViewById(R.id.text_shipping_date);
		}

		void populateFrom(Cursor cursor, float rowTextFontSize) {

			if (Request_Returns.isUploaded(cursor)) {

				mImageUploaded.setImageResource(android.R.drawable.checkbox_on_background);
			} else {

				mImageUploaded.setImageResource(android.R.drawable.checkbox_off_background);
			}

			mTextNumber.setText(Request_Returns.getNomer(cursor));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextDate.setText(DateTimeHelper.UIDateString(Request_Returns.getData(cursor)));
			mTextDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextClient.setText(Request_Returns.getKontragentNaimanovanie(cursor));
			mTextClient.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextShippingDate.setText(DateTimeHelper.UIDateString(Request_Returns.getDataOtgruzki(cursor)));
			mTextShippingDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
		}
	}
}