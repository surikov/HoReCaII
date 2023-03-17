package sweetlife.android10;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;

import reactive.ui.Auxiliary;
import sweetlife.android10.ui.Activity_Login;

//import androidx.annotation.Nullable;


public class UpdateApk extends Service {
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("UpdateApk onStartCommand flags:" + flags + ", startId:" + startId + ", intent:" + intent);
		//int t=PackageInstaller.STATUS_PENDING_USER_ACTION;
		int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999);
		System.out.println("status:" + status);
		if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
			System.out.println("PackageInstaller.STATUS_PENDING_USER_ACTION " + status);
			Intent confirmationIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);

			confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(confirmationIntent);

			System.out.println("started confirmationIntent "+confirmationIntent);
		} else {
			if (status == PackageInstaller.STATUS_SUCCESS) {
				System.out.println("PackageInstaller.STATUS_SUCCESS " + status);
			} else {
				System.out.println("unknown " + status);
				//Auxiliary.warn("status "+status,this);

			}
			relogin(status);
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	void relogin(int status){
		System.out.println("relogin status " + status);
		Intent intent = new Intent();
		intent.setClass(this, Activity_Login.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.putExtra("apkReinstallStatus",status);
		startActivity(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("UpdateApk onBind " + intent);
		return null;
	}
}
