package sweetlife.android10.gps;


import android.app.Application;
import android.location.Location;

public class Session extends Application {

	private static boolean gpsEnabled;
	private static boolean isStarted;
	private static boolean isUsingGps;
	//private static String lastStatus;
	//private static int lastStatusColor;
	//private static int satellites;
	//private static boolean notificationVisible;
	//private static Location currentLocationInfo;
	private static boolean isBound;

	public static String access_token_1221="";

	//private static double latitude;
	//private static double longitude;
	//private static long gps_time = 0;
	//private static long local_time = 0;

	public static boolean isGpsEnabled() {

		return gpsEnabled;
	}

	public static void setGpsEnabled(boolean gpsEnabled) {

		Session.gpsEnabled = gpsEnabled;
	}

	public static boolean isStarted() {

		return isStarted;
	}

	public static void setStarted(boolean isStarted) {

		Session.isStarted = isStarted;
	}

	public static boolean isUsingGps() {
//
		return isUsingGps;
	}

	public static void setUsingGps(boolean isUsingGps) {

		Session.isUsingGps = isUsingGps;
	}

	//public static String getCurrentStatus() {

	//	return lastStatus;
	//}

	//public static void setCurrentStatus(String currentStatus, int color) {
		//System.out.println("setCurrentStatus "+currentStatus);
	//	Session.lastStatus = currentStatus;
	//	Session.lastStatusColor = color;
	//}

	//public static int getCurrentStatusColor() {

	//	return Session.lastStatusColor;
	//}

	//public static int getSatelliteCount() {

	//	return satellites;
	//}

	//public static void setSatelliteCount(int satellites) {

	//	Session.satellites = satellites;
	//}

	//public static boolean isNotificationVisible() {

	//	return notificationVisible;
	//}

	//public static void setNotificationVisible(boolean notificationVisible) {

	//	Session.notificationVisible = notificationVisible;
	//}
/*
	public static double getCurrentLatitude() {

		if (getCurrentLocationInfo() != null) {

			return getCurrentLocationInfo().getLatitude();
		} else {

			return 0;
		}
	}*/
/*
	public static boolean hasValidLocation() {

		return (getCurrentLocationInfo() != null && getCurrentLatitude() != 0 && getCurrentLongitude() != 0);
	}*/
/*
	public static double getCurrentLongitude() {

		if (getCurrentLocationInfo() != null) {

			return getCurrentLocationInfo().getLongitude();
		} else {

			return 0;
		}
	}*/
/*
	public static void setCurrentLocationInfo(Location currentLocationInfo) {

		Session.currentLocationInfo = currentLocationInfo;
	}*/
/*
	public static Location getCurrentLocationInfo() {

		return currentLocationInfo;
	}
*/
	public static void setBoundToService(boolean isBound) {

		Session.isBound = isBound;
	}

	public static boolean isBoundToService() {

		return isBound;
	}
/*
	public static double getLongitude() {
		return longitude;
	}*/
/*
	public static void setLongitude(double longitude) {
		Session.longitude = longitude;
	}*/
/*
	public static double getLatitude() {
		return latitude;
	}*/
/*
	public static void setLatitude(double latitude) {
		Session.latitude = latitude;
	}
*/
	/*public static long _getGPSTime() {
		return gps_time;
	}*/
/*
	public static void setGPSTime(long time) {
		Session.gps_time = time;
	}
*/
/*
	public static long getLocalTime() {
		return local_time;
	}

	public static void setLocalTime(long local_time) {
		Session.local_time = local_time;
	}*/
}
