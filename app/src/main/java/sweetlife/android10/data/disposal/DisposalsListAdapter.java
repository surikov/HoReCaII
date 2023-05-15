package sweetlife.android10.data.disposal;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Disposals;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sweetlife.android10.R;

public class DisposalsListAdapter extends ZoomListCursorAdapter {

	public DisposalsListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		DisposalsHolder holder = (DisposalsHolder)row.getTag();
		holder.populateFrom( cursor, 24);
		//holder.populateFrom( cursor, getRowTextFontSize() );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_disposals, view, false);

		DisposalsHolder holder = new DisposalsHolder(row);	

		row.setTag(holder);

		return(row);
	}

	public class DisposalsHolder {

		private ImageView mImageUploaded=null;
		private TextView mTextNumber=null;
		private TextView mTextDate=null;
		private TextView mTextKontragent=null;
		private TextView mTextComment=null;
		private TextView mTextAmount=null;

		DisposalsHolder(View row) {

			mImageUploaded    = (ImageView)row.findViewById(R.id.image_uploaded);
			mTextNumber       = (TextView)row.findViewById(R.id.text_number);
			mTextDate         = (TextView)row.findViewById(R.id.text_date);
			mTextComment       = (TextView)row.findViewById(R.id.text_comment);
			mTextKontragent       = (TextView)row.findViewById(R.id.text_kontragent);
			mTextAmount       = (TextView)row.findViewById(R.id.text_amount);
		}

		void populateFrom(Cursor cursor, float rowTextFontSize) {

			if( Request_Disposals.isUploaded(cursor) ) {
				
				mImageUploaded.setImageResource(android.R.drawable.checkbox_on_background);
			}
			else { 
			
				mImageUploaded.setImageResource(android.R.drawable.checkbox_off_background);
			}
			
			mTextNumber.setText(Request_Disposals.getNomer(cursor));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextDate.setText(DateTimeHelper.UIDateString(Request_Disposals.getData(cursor)));
			mTextDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextKontragent.setText(Request_Disposals.getKontragentNaimanovanie(cursor));
			mTextKontragent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextComment.setText(Request_Disposals.getKommentariy(cursor));
			mTextComment.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			
			mTextAmount.setText(DecimalFormatHelper.format(Request_Disposals.getSumma(cursor)));
			mTextAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
		}
	}
}
