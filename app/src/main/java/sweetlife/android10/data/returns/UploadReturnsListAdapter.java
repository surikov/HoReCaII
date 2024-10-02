package sweetlife.android10.data.returns;

import java.util.ArrayList;

import sweetlife.android10.data.common.IStateChanged;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_Returns;
import sweetlife.android10.utils.DateTimeHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sweetlife.android10.*;

public class UploadReturnsListAdapter extends ZoomListCursorAdapter {

	private ArrayList<Boolean> mStateList = new ArrayList<Boolean>();

	private IStateChanged mStateChangedHandler;

	public UploadReturnsListAdapter(Context context, Cursor cursor, IStateChanged stateChangedHandler) {
		super(context, cursor);

		mStateChangedHandler = stateChangedHandler;

		if (cursor.moveToFirst()) {

			int count = cursor.getCount();

			for (int i = 0; i < count; i++) {

				mStateList.add(new Boolean(true));
			}
		}
	}

	@Override
	public void changeCursor(Cursor cursor) {

		mStateList.clear();

		if (cursor.moveToFirst()) {

			int count = cursor.getCount();

			for (int i = 0; i < count; i++) {

				mStateList.add(new Boolean(true));
			}
		}

		super.changeCursor(cursor);
	}

	public void finallize() {

		mStateList.clear();
	}

	public ArrayList<Boolean> getStateList() {

		return mStateList;
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		BidHolder holder = (BidHolder) row.getTag();

		holder.populateFrom(cursor, cursor.getPosition());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_returns_upload, view, false);

		BidHolder holder = new BidHolder(row);

		row.setTag(holder);

		return (row);
	}

	public class BidHolder {

		private TextView mTextNumber = null;
		private TextView mTextDate = null;
		private TextView mTextClient = null;
		private TextView mTextShippingDate = null;
		public ImageView mState = null;

		BidHolder(View row) {

			mTextNumber = (TextView) row.findViewById(R.id.text_number);
			mTextDate = (TextView) row.findViewById(R.id.text_date);
			mTextClient = (TextView) row.findViewById(R.id.text_kontragent);
			mTextShippingDate = (TextView) row.findViewById(R.id.text_shipping_date);
			mState = (ImageView) row.findViewById(R.id.image_upload);

			mState.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {

					ImageView imState = (ImageView) view;

					if (mStateList.get((Integer) mState.getTag())) {

						imState.setImageResource(android.R.drawable.checkbox_off_background);
						mStateList.set((Integer) mState.getTag(), false);
					} else {

						imState.setImageResource(android.R.drawable.checkbox_on_background);
						mStateList.set((Integer) mState.getTag(), true);
					}

					mStateChangedHandler.onChange();
				}
			});
		}

		void populateFrom(Cursor cursor, int position) {

			float rowTextFontSize = getRowTextFontSize();

			mTextNumber.setText(Request_Returns.getNomer(cursor));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextDate.setText(DateTimeHelper.UIDateString(Request_Returns.getData(cursor)));
			mTextDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextClient.setText(Request_Returns.getKontragentNaimanovanie(cursor));
			mTextClient.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mTextShippingDate.setText(DateTimeHelper.UIDateString(Request_Returns.getDataOtgruzki(cursor)));
			mTextShippingDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			if (mStateList.size() == cursor.getCount()) {

				if (mStateList.get(position)) {

					mState.setImageResource(android.R.drawable.checkbox_on_background);
				} else {

					mState.setImageResource(android.R.drawable.checkbox_off_background);
				}
			} else {

				mState.setImageResource(android.R.drawable.checkbox_on_background);
			}

			mState.setTag(new Integer(position));
		}
	}
}
