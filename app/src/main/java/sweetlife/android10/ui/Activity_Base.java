package sweetlife.android10.ui;

import java.util.Observable;
import java.util.Observer;

import reactive.ui.Auxiliary;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.data.common.IZoomList;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.AsyncTaskManager;

import sweetlife.android10.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class Activity_Base extends Activity implements OnTouchListener, IExtras, Observer {
	private static final int ZOOM_INC_WIDTH_SIZE = 5;
	private static final int ZOOM_INC_HEIGHT_SIZE = 3;
	private static final int ZOOM_INC_FONT_SIZE = 2;
	private static final int ZOOM_DIFFENT_BETWEEN_ROW_AND_HEADER = 4;
	private static final int NONE = 0;
	private static final int ZOOM = 1;
	private static final int ZOOM_IN = 2;
	private static final int ZOOM_OUT = 3;
	public static final int IDM_LIST_DELETE = 101;
	public static final int IDM_LIST_KartaKlienta = 1012;
	public static final int NULL_LIST_VALUE = -1;
	public static final int IDD_DELETE = 101;
	public static final int IDD_SAVE_CHANGES = 102;
	public static final int IDD_IS_EMPTY = 103;
	public static final int IDD_ALREADY_IN_LIST = 106;
	public static final int IDD_BAD_FILE_PATH = 107;
	public static final int IDD_EMPTY_FILE_PATH = 108;
	public static final int IDD_EMPTY_FIELDS = 109;
	public static final int IDD_WORKING_WITH_CAMERA_ERROR = 110;
	protected static int mDensity;
	protected static DisplayMetrics mDisplayMetrics;
	//Zoom zoom
	private float mPrevFingersDist = 1f;
	private int mZoomDirection = 0;
	private int mZoomMode = NONE;
	protected SQLiteDatabase mDB;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogHelper.debug(this.getClass().getCanonicalName() + " onCreate");
		System.out.println(this.getClass().getCanonicalName() + " onCreate");
		SetDisplayMetrics();
		mDB = ApplicationHoreca.getInstance().getDataBase();
	}
	@Override
	public void update(Observable observable, Object data) {
	}
	@Override
	protected void onResume() {
		LogHelper.debug(this.getClass().getCanonicalName() + " onResume");
		//ApplicationHoreca.getInstance()
		
		AsyncTaskManager.getInstance().attach(this, this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		LogHelper.debug(this.getClass().getCanonicalName() + " onPause");
		AsyncTaskManager.getInstance().detach();
		super.onPause();
	}
	public SQLiteDatabase getDataBase() {

		return mDB;
	}
	protected void setTitleWithVersionOwner() {
		try {
			String chOwner=sweetlife.android10.supervisor.Cfg.whoCheckListOwner();
			if(chOwner.length()>0){
				tee.binding.Bough data=Auxiliary.fromCursor(
				getDataBase().rawQuery(
						"select l.naimenovanie as name from PhizLicaPolzovatelya f join Polzovateli p on p._idrref=f.polzovatel join PhizicheskieLica l on l._idrref=f.phizlico where trim(p.kod)='"
								+chOwner+"' order by f.period desc;"
						,null));
				chOwner=chOwner+"/"+data.child("row").child("name").value.property.value();
				//System.out.println(data.dumpXML());
			}
			setTitle(chOwner
					+"/"+getString(R.string.app_name)//
					+ "  " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName
			+", "+ Settings.getInstance().getBaseIP()//
			+"/"+Settings.getInstance().selectedBase1C()//
					+" "+Settings.getInstance().selectedWSDL()//
			);
		}
		catch (NameNotFoundException e) {
			//Activity_UploadBids.logToFile("setTitleWithVersionOwner.txt",e.getMessage());
		}
	}
	private void SetDisplayMetrics() {
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		mDensity = mDisplayMetrics.densityDpi;
	}
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			mPrevFingersDist = getFingersSpacing(event);
			mZoomMode = ZOOM;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			Log.d("sw","----------------------------\n\n\n\nmActivity_Base.ZoomMode "+mZoomMode+"\n\n\n\n\n------------------------------------");
			if (mZoomMode != NONE) {
				if (mZoomDirection == ZOOM_IN) {
					ZoomListView(view, ZOOM_INC_WIDTH_SIZE * -1, ZOOM_INC_HEIGHT_SIZE * -1, ZOOM_INC_FONT_SIZE * -1);
				}
				else {
					ZoomListView(view, ZOOM_INC_WIDTH_SIZE, ZOOM_INC_HEIGHT_SIZE, ZOOM_INC_FONT_SIZE);
				}
				mZoomDirection = NONE;
				mZoomMode = NONE;
			}
			Log.d("sw","----------------------------\n\n\n\nnow mActivity_Base.ZoomMode "+mZoomMode+"\n\n\n\n\n------------------------------------");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mZoomMode == ZOOM) {
				float newDist = getFingersSpacing(event);
				if (newDist > mPrevFingersDist) {
					mZoomDirection = ZOOM_OUT;
				}
				else {
					mZoomDirection = ZOOM_IN;
				}
			}
			break;
		}
		return false;
	}
	protected void ZoomListView(View view, int widthDelta, int heightDelta, int fontDelta) {
		//System.out.println("\n\n\n\n\nZOOM_DIFFENT_BETWEEN_ROW_AND_HEADER "+ZOOM_DIFFENT_BETWEEN_ROW_AND_HEADER);
		Log.d("sw","----------------------------\n\n\n\nZoomListView\n\n\n\n\n------------------------------------");
		RelativeLayout parent = (RelativeLayout) view.getParent();
		LinearLayout buttonsLayout = null;
		for (int i = 0; i < parent.getChildCount(); i++) {
			buttonsLayout = (LinearLayout) parent.getChildAt(0);
			if (buttonsLayout != null) {
				break;
			}
		}
		Button button = (Button) buttonsLayout.getChildAt(0);
		if ((button.getTextSize() > 16f && fontDelta < 0) || (button.getTextSize() <= 28f && fontDelta > 0)) {
			buttonsLayout.getLayoutParams().width = buttonsLayout.getLayoutParams().width + buttonsLayout.getChildCount() * widthDelta;
			buttonsLayout.invalidate();
			increaseFontSize(button, fontDelta);
			button.getLayoutParams().height = button.getLayoutParams().height + heightDelta;
			for (int i = 1; i < buttonsLayout.getChildCount(); i++) {
				button = (Button) buttonsLayout.getChildAt(i);
				increaseFontSize(button, fontDelta);
				button.getLayoutParams().height = button.getLayoutParams().height + heightDelta;
			}
			view.getLayoutParams().width = view.getLayoutParams().width + buttonsLayout.getChildCount() * widthDelta;
			ListView list = (ListView) view;
			//System.out.println("button.getTextSize() "+button.getTextSize());
			
			((IZoomList) list.getAdapter()).setRowTextFontSize(button.getTextSize() + ZOOM_DIFFENT_BETWEEN_ROW_AND_HEADER);
			list.invalidate();
		}
	}
	protected void increaseFontSize(TextView view, int increaseValue) {
		float size = view.getTextSize();
		switch (mDensity) {
		case DisplayMetrics.DENSITY_LOW:
			size = (size + increaseValue) / 0.75f;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			size = size + increaseValue;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			size = (size + increaseValue) / 1.5f;
			break;
		default:
			break;
		}
		view.setTextSize(size);
		view.invalidate();
	}
	protected TextView makeTabIndicator(String text) {
		TextView tabView = new TextView(this);
		tabView.setHeight(50);
		tabView.setText(text);
		//tabView.setTextSize(16);
		//tabView.setTextColor(Color.WHITE);
		tabView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		Resources res = getResources();
		//Drawable draw = res.getDrawable(R.drawable.tab_indicator);
		//tabView.setBackgroundDrawable(draw);
		tabView.setPadding(13, 0, 13, 0);
		return tabView;
	}
	public int convertDensityPixel(int dip) {
		return (int) (dip * getResources().getDisplayMetrics().density);
	}
	protected float getFingersSpacing(MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			//return FloatMath.sqrt(x * x + y * y);
			return (float)Math.sqrt(x * x + y * y);
		}
		catch (Exception e) {
		}
		return 0;
	}
	protected Dialog CreateErrorDialog(int message) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".CreateErrorDialog: " + this.getString(message));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	protected Dialog CreateErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
}
