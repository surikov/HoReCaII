package sweetlife.android10.data.fixedprices;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_FixedPrices;
import sweetlife.android10.utils.DateTimeHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sweetlife.android10.R;

public class FixedPricesListAdapter extends ZoomListCursorAdapter {

	public FixedPricesListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		FixedPricesHolder holder = (FixedPricesHolder)row.getTag();

		holder.populateFrom( cursor, getRowTextFontSize() );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row=LayoutInflater.from(context).inflate(R.layout.row_fixed_prices, view, false);

		FixedPricesHolder holder = new FixedPricesHolder(row);	

		row.setTag(holder);

		return(row);
	}

	public class FixedPricesHolder {

		private ImageView mImageUploaded=null;
		private TextView mTextNumber=null;
		private TextView mTextDate=null;
		private TextView mTextKontragent=null;
		private TextView mTextComment=null;
		private TextView mTextBeginDate=null;
		private TextView mTextEndDate=null;

		FixedPricesHolder(View row) {

			mImageUploaded    = (ImageView)row.findViewById(R.id.image_uploaded);
			mTextNumber       = (TextView)row.findViewById(R.id.text_number);
			mTextDate         = (TextView)row.findViewById(R.id.text_date);
			mTextComment       = (TextView)row.findViewById(R.id.text_comment);
			mTextKontragent       = (TextView)row.findViewById(R.id.text_kontragent);
			mTextBeginDate       = (TextView)row.findViewById(R.id.text_date_since);
			mTextEndDate = (TextView)row.findViewById(R.id.text_date_till);
		}

		void populateFrom(Cursor cursor, float rowTextFontSize) {

			if( Request_FixedPrices.isUploaded(cursor) ) {
				
				mImageUploaded.setImageResource(android.R.drawable.checkbox_on_background);
			}
			else { 
			
				mImageUploaded.setImageResource(android.R.drawable.checkbox_off_background);
			}
			
			mTextNumber.setText(Request_FixedPrices.getNomer(cursor));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextDate.setText(DateTimeHelper.UIDateString(Request_FixedPrices.getData(cursor)));
			mTextDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextKontragent.setText(Request_FixedPrices.getKontragentNaimanovanie(cursor));
			mTextKontragent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextComment.setText(Request_FixedPrices.getKommentariy(cursor));
			mTextComment.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextBeginDate.setText(DateTimeHelper.UIDateString(Request_FixedPrices.getVremyaNachalaSkidkiPhiksCen(cursor)));
			mTextBeginDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextEndDate.setText(DateTimeHelper.UIDateString(Request_FixedPrices.getVremyaOkonchaniyaSkidkiPhiksCen(cursor)));
			mTextEndDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
		}
	}
}
