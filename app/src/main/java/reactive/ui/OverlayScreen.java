package reactive.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//https://github.com/shaobin0604/Android-HomeKey-Locker/blob/master/HomeLockerLib/src/main/java/io/github/homelocker/lib/HomeKeyLocker.java
public class OverlayScreen extends AlertDialog {
	OverlayScreen me = null;

	public void lock(Activity activity) {
		if (me == null) {
			me = new OverlayScreen(activity);
			me.show();
		}
	}
	public void unlock() {
		if (me != null) {
			me.dismiss();
			me = null;
		}
	}
	private OverlayScreen(Activity activity) {
		super(activity);
		//super(activity, R.style.OverlayDialog);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.type = TYPE_SYSTEM_ALERT;
		params.dimAmount = 0.0F; // transparent
		params.width = 0;
		params.height = 0;
		params.gravity = Gravity.BOTTOM;
		getWindow().setAttributes(params);
		getWindow().setFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL, 0xffffff);
		setOwnerActivity(activity);
		setCancelable(false);
	}
	public final boolean dispatchTouchEvent(MotionEvent motionevent) {
        return true;
    }

    protected final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FrameLayout framelayout = new FrameLayout(getContext());
        framelayout.setBackgroundColor(0);
        setContentView(framelayout);
    }
}
