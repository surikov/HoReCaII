package sweetlife.android10.gps;

import sweetlife.android10.*;

import android.annotation.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.*;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;


public class SWLifeGpsService extends Service{
	private static int NOTIFICATION_ID;
	private IBinder mBinder = null;
	//private static IGpsLoggerServiceClient mDetailsServiceClient;
	private static ILocationChange mLocationChangeClient;
	private GeneralLocationListener mGPSLocationListener;
	private LocationManager mGPSLocationManager;
	//private static int mPeriod;
	public static int GREEN_COLOR = Color.rgb(0, 251, 51);
	public static int RED_COLOR = Color.rgb(204, 0, 51);
	public static int YELLOW_COLOR = Color.rgb(255, 204, 0);
	public static GnssStatus.Callback gnssStatusCallback = null;

	//public static long lastSavedGPSms = 0;
	//public static long whenSavedGPSms = 0;
	public static long maxLocalGpsDiffMs = 24 * 60 * 60 * 1000;

	@Override
	public void onCreate(){
		super.onCreate();
		mBinder = new GpsLoggingBinder();
	}

	@Override
	public IBinder onBind(Intent intent){
		//System.out.println("onBind "+intent);
		SetPeriod(intent);
		HandleIntent(intent);
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent){
		boolean result = super.onUnbind(intent);
		stopSelf();
		return result;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(intent != null){
			SetPeriod(intent);
			HandleIntent(intent);
		}
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		StopLogging();
		//mDetailsServiceClient = null;
	}

	private void HandleIntent(Intent intent){
		StartLogging();
	}

	public class GpsLoggingBinder extends Binder{
		public SWLifeGpsService getService(){
			return SWLifeGpsService.this;
		}
	}

	//public void SetServiceClient(IGpsLoggerServiceClient client) {
	//	mDetailsServiceClient = client;
	//}

	public void SetLocationChangeClient(ILocationChange client){
		mLocationChangeClient = client;
	}

	//public void DeleteServiceClient() {
	//	mDetailsServiceClient = null;
	//}

	public void DeleteLocationChangeClient(){
		mLocationChangeClient = null;
	}

	public boolean HasLocationChangeClient(){
		return mLocationChangeClient == null ? false : true;
	}

	private void StartLogging(){
		System.out.println("SWLifeGpsService.StartLogging, Session.isStarted() " + Session.isStarted());
		if(Session.isStarted()){
			return;
		}
		LocationNotAvailable();
		startForeground(NOTIFICATION_ID, null);
		Session.setStarted(true);
		StartGpsManager();
		StartGPSStatusListener();
	}

	private void StopLogging(){
		Session.setStarted(false);
		//Session.setCurrentLocationInfo(null);
		StopGPSStatusListener();
		StopGpsManager();
		//StopDetailsServiceClient();
		stopForeground(true);
	}

	public LocationManager getGPSLocationManager(){
		return mGPSLocationManager;
	}

	private void SetPeriod(Intent intent){
		/*if(intent != null){
			Bundle extras = intent.getExtras();
			if(extras != null)
				mPeriod = extras.getInt("period", 20 * 1000);
		}else{
			mPeriod = 20 * 1000;
		}*/
	}

	public void StartGPSStatusListener(){
		CheckGpsStatus();
		if(Session.isGpsEnabled()){

			gnssStatusCallback = new GnssStatus.Callback(){
				public void onStarted(){
					//System.out.println("gnssStatusCallback.onStarted");
				}

				public void onStopped(){
					//System.out.println("gnssStatusCallback.onStopped");
				}

				public void onFirstFix(int ttffMillis){
					//System.out.println("gnssStatusCallback.onFirstFix");
				}

				public void onSatelliteStatusChanged(GnssStatus status){
					//System.out.println("gnssStatusCallback.onSatelliteStatusChanged");
				}
			};
			//mGPSLocationManager.addGpsStatusListener(mGPSLocationListener);
			//SetStatus("Установка соединения со спутниками!", YELLOW_COLOR);
			mGPSLocationManager.registerGnssStatusCallback(gnssStatusCallback, null);
			Session.setUsingGps(true);
		}else{
		}
	}

	private void StartGpsManager(){
		mGPSLocationListener = new GeneralLocationListener(this);
		mGPSLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mGPSLocationManager.removeUpdates(mGPSLocationListener);
		mGPSLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30*1000, 0, mGPSLocationListener);
		mGPSLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30*1000, 0, mGPSLocationListener);
		//		mGPSLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
		//				1000, 0,
		//				mGPSLocationListener);
	}

	private void StopGpsManager(){
		if(mGPSLocationListener != null){
			mGPSLocationManager.removeUpdates(mGPSLocationListener);
			//			mGPSLocationManager.removeUpdates(mGPSLocationListener);

			mGPSLocationManager.unregisterGnssStatusCallback(gnssStatusCallback);
		}
	}

	private void CheckGpsStatus(){
		if(mGPSLocationManager != null){
			Session.setGpsEnabled(mGPSLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		}
	}

	public void StopGPSStatusListener(){
		if(mGPSLocationListener != null){
			//mGPSLocationManager.removeGpsStatusListener(mGPSLocationListener);
		}
		Session.setUsingGps(false);
		//SetStatus("GPS провайдеры недоступны.", RED_COLOR);
	}

	//void SetStatus(String status, int color) {
	//Session.setCurrentStatus(status, color);
	//if (IsGPSInfoFormVisible()) {
	//	mDetailsServiceClient.OnStatusMessage(status, color);
	//}
	//}

	//private void SetStatus(int stringId, int color) {
	//	String s = getString(stringId);
	//	SetStatus(s, color);
	//}

	public void LocationNotAvailable(){
		CheckGpsStatus();
		//if (IsGPSInfoFormVisible()) {
		//	mDetailsServiceClient.On_LocationNotAvailable();
		//}
	}

	//void StopDetailsServiceClient() {
	//	if (IsGPSInfoFormVisible()) {
	//		mDetailsServiceClient.On_StopLogging();
	//		mDetailsServiceClient = null;
	//	}
	//}


	void SweetLocationChanged(Location loc, String comment){
		//System.out.println("SweetLocationChanged ±" + loc.getAccuracy() + ": " + loc.getLatitude() + "/" + loc.getLongitude());
		if(loc.isFromMockProvider()){
			loc.setLatitude(1);
			loc.setLongitude(1);
		}

		//Session.setLatitude(loc.getLatitude());
		//Session.setLongitude(loc.getLongitude());
		//Session.setGPSTime(loc.getTime());
		//if (IsGPSInfoFormVisible()) {
		//	mDetailsServiceClient.On_LocationUpdate(loc);
		//}
		//if ((System.currentTimeMillis() - Session.getLocalTime()) < mPeriod) {
		//if((System.currentTimeMillis() - lastSavedGPSms) < mPeriod){
		//	return;
		//}
		if(Math.abs(System.currentTimeMillis() - loc.getTime()) > maxLocalGpsDiffMs){
			return;
		}
		//Session.setLocalTime(System.currentTimeMillis());
		//Session.setGPSTime(loc.getTime());
		//lastSavedGPSms = loc.getTime();
		//whenSavedGPSms = System.currentTimeMillis();
		//Session.setCurrentLocationInfo(loc);
		if(mLocationChangeClient != null){
			mLocationChangeClient.newLocationPoint(loc, "±" + loc.getAccuracy() + "м");
		}
	}

	//void SetSatelliteInfo(int count) {
	//	if (IsGPSInfoFormVisible()) {
	//		mDetailsServiceClient.On_SatelliteCount(count);
	//	}
	//}

	//private boolean IsGPSInfoFormVisible() {
	//	return mDetailsServiceClient != null;
	//}
}
