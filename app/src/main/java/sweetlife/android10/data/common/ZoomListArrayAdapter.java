package sweetlife.android10.data.common;

import android.util.Log;
import android.widget.BaseAdapter;

public abstract class ZoomListArrayAdapter extends BaseAdapter implements IZoomList {

	protected float   mRowTextFontSize=16;
	
	public float getRowTextFontSize() {
		return 29;
		//return mRowTextFontSize;
	}

	public void setRowTextFontSize(float rowTextFontSize) {
		Log.d("sw","----------------------------\n\n\n\nZoomListArrayAdapter.setRowTextFontSize "+rowTextFontSize+"\n\n\n\n\n------------------------------------");
		mRowTextFontSize = rowTextFontSize;
	}
}
