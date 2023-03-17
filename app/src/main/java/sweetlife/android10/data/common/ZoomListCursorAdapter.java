package sweetlife.android10.data.common;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import java.util.*;

public abstract class ZoomListCursorAdapter extends CursorAdapter implements IZoomList {

	public Vector<String>artikuls=new Vector<String>();
	public Vector<String>sklad=new Vector<String>();

	public interface OnSelectedPositionChangeListener {

		void OnPositionChange( ZoomListCursorAdapter adapter , int position);
	}

	protected OnSelectedPositionChangeListener mListener;

	protected float   mRowTextFontSize=16;

	private   int     mSelectedPos = -1;

	@SuppressWarnings("deprecation")
	public ZoomListCursorAdapter(Context context, Cursor c, OnSelectedPositionChangeListener listener) {
		super(context, c);

		mListener = listener;
	}

	@SuppressWarnings("deprecation")
	public ZoomListCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public void setSelectedPosition(int pos) {

		if( mSelectedPos != pos ) {

			mSelectedPos = pos;
			
			if( mListener != null && pos != -1 ) {

				mListener.OnPositionChange( this, pos );
			}

			notifyDataSetChanged();
		}
	}

	public int getSelectedPosition() {

		return mSelectedPos;
	}

	public float getRowTextFontSize() {

		return mRowTextFontSize;
	}

	public void setRowTextFontSize(float rowTextFontSize) {

		mRowTextFontSize = rowTextFontSize;
	}
}
