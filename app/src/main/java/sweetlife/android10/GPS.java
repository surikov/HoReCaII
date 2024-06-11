package sweetlife.android10;

import java.util.Calendar;

import android.content.*;
import android.database.sqlite.*;
import android.location.*;
import android.os.*;

import sweetlife.android10.gps.*;
import sweetlife.android10.log.*;

public class GPS {
	private static boolean done = false;
	private static SWLifeGpsService mGPSService = null;
	private static Intent mGPSServiceIntent = null;
	private static GPSInfo mGPSInfo = null;
	private static SQLiteDatabase mDB = null;
	private static String agentIDstr = null;
	private static ServiceConnection mGpsServiceConnection = null;
	private static PowerManager.WakeLock mWakeLock = null;

	public static boolean SetServiceClient(IGpsLoggerServiceClient client) {
		//System.out.println("SetServiceClient "+client+" for "+mGPSService);
		if (mGPSService != null) {
			mGPSService.SetServiceClient(client);
			return true;
		}
		return false;
	}

	public static void DeleteServiceClient() {
		//System.out.println("DeleteServiceClient");
		if (mGPSService != null) {
			mGPSService.DeleteServiceClient();
		}
	}

	private static void StartAndBindService(final Context context) {
		//System.out.println("StartAndBindService");
		//sweetlife.horeca.monitor.SQLexec.go();
		//if (mGPSService == null) {
		//LogHelper.debug(this.getClass().getCanonicalName() + ".StartAndBindService");
		mGPSServiceIntent = new Intent(context, SWLifeGpsService.class);
		mGPSServiceIntent.putExtra("period", Settings.getInstance().getSPY_GPS_PERIOD());
		mGpsServiceConnection = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				//System.out.println("onServiceDisconnected " + name);
				mGPSService = null;
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				//System.out.println("onServiceConnected " + name);
				mGPSService = ((SWLifeGpsService.GpsLoggingBinder) service).getService();
				//mGPSService.bindService(mGPSServiceIntent, mGpsServiceConnection, Context.BIND_AUTO_CREATE);
				//getGPSInfo();
				mGPSService.SetLocationChangeClient(new ILocationChange() {
					@Override
					public void newLocationPoint(Location location,String comment) {
						//System.out.println("OnLocationUpdate");
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(location.getTime());
						Calendar now = Calendar.getInstance();
						if (Math.abs(now.getTimeInMillis() - calendar.getTimeInMillis()) < 2 * 24 * 60 * 60 * 1000) {
							getGPSInfo().insertGpsPoint(calendar, location.getLatitude(), location.getLongitude(),comment);
						}
					}
				});
			}
		};
		context.bindService(mGPSServiceIntent, mGpsServiceConnection, Context.BIND_AUTO_CREATE);
		Session.setBoundToService(true);
		//}
		//System.out.println("done StartAndBindService");
	}

	/*public static void StartGPSService(Context context) {
		StartAndBindService(context);
	}*/
	public static void StopAndUnbindServiceIfRequired(Context context) {
		//System.out.println("StopAndUnbindServiceIfRequired " + Session.isBoundToService());
		try {
			mGPSInfo = null;
			if (Session.isBoundToService() && mGpsServiceConnection != null) {
				try {
					context.unbindService(mGpsServiceConnection);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				Session.setBoundToService(false);
			}
			if (!Session.isStarted() && mGPSServiceIntent != null) {
				try {
					context.stopService(mGPSServiceIntent);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				mGPSServiceIntent = null;
			}
			if (mGPSService != null) {
				mGPSService.stopSelf();
				mGPSService = null;
				mGpsServiceConnection = null;
			}
			DestroyWakeLock();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void StartGPSLog(Context context, SQLiteDatabase db, String agentid) {
		//System.out.println("StartGPSLog " + mGPSService);
		if (mGPSService == null) {
			mDB = db;
			agentIDstr = agentid;
			StartAndBindService(context);
			LogHelper.debug(context.getClass().getCanonicalName() + " StartGPSLog");
			getGPSInfo();
			InitializeWakeLock(context);
			/*mGPSService.SetLocationChangeClient(new ILocationChange() {
				@Override
				public void OnLocationUpdate(Location location) {
					//System.out.println("OnLocationUpdate");
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(location.getTime());
					getGPSInfo().insertPoint(calendar, location.getLatitude(), location.getLongitude());
				}
			});*/
		} else {
			LogHelper.debug(context.getClass().getCanonicalName() + " already StartGPSLog " + mGPSService);
		}
		//if (mGPSService != null && !mGPSService.HasLocationChangeClient()) {
		//if ( !mGPSService.HasLocationChangeClient()){
		//LogHelper.debug(context.getClass().getCanonicalName() + " StartGPSLog");
		//getGPSInfo();
		/*SetServiceClient(new IGpsLoggerServiceClient(){

			@Override
			public void OnStatusMessage(String message, int color) {
				System.out.println("OnStatusMessage");
				
			}

			@Override
			public void OnLocationUpdate(Location loc) {
				System.out.println("OnLocationUpdate");
				
			}

			@Override
			public void OnLocationNotAvailable() {
				 System.out.println("OnLocationNotAvailable");
				
			}

			@Override
			public void OnSatelliteCount(int count) {
				System.out.println("OnSatelliteCount");	
			}

			@Override
			public void OnStopLogging() {
				System.out.println("OnStopLogging");
				
			}

			@Override
			public Activity GetActivity() {
				System.out.println("GetActivity");
				return null;
			}});*/
		/*mGPSService.SetLocationChangeClient(new ILocationChange() {
			@Override
			public void OnLocationUpdate(Location location) {
				//System.out.println("OnLocationUpdate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(location.getTime());
				getGPSInfo().insertPoint(calendar, location.getLatitude(), location.getLongitude());
			}
		});*/
		//}
	}/*
		public static void StopGPSLog(Context context) {
		System.out.println("StopGPSLog " + mGPSService);
		try {
			StopAndUnbindServiceIfRequired(context);
			mGPSServiceIntent = null;
			mGPSInfo = null;
			mDB = null;
			agentIDstr = null;
			mGpsServiceConnection = null;
			if (mGPSService != null) {
				mGPSService = null;
				mGPSService.DeleteLocationChangeClient();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		}*/

	static void InitializeWakeLock(Context c) {
		if (mWakeLock == null) {

			PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
			//mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotDimScreen");
			mWakeLock.acquire();
		}
	}

	static void DestroyWakeLock() {
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}


	public static GPSInfo getGPSInfo() {
		//System.out.println("mGPSInfo is " + mGPSInfo);
		if (mGPSInfo == null) {
			//LogHelper.debug(this.getClass().getCanonicalName() + ".mGPSInfo == null");
			mGPSInfo = new GPSInfo(mDB, agentIDstr);
			//System.out.println("now mGPSInfo is " + mGPSInfo);
		}
		//System.out.println("mGPSInfo "+mGPSInfo);
		return mGPSInfo;
	}
}
