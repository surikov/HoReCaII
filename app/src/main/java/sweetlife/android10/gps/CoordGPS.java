package sweetlife.android10.gps;

public class CoordGPS {

	private String mTime;
	private double mLatitude;
	private double mLongitude;
	
	public CoordGPS(String time, double lat, double lon) {
		
		mTime = time;
		mLatitude = lat;
		mLongitude = lon;
	}

	public double getLatitude() {
		
		return mLatitude;
	}

	public void setLatitude(double lat) {
		
		mLatitude = lat;
	}

	public String getTime() {
		
		return mTime;
	}

	public void setTime(String time) {
		
		mTime = time;
	}

	public double getLongitude() {
		
		return mLongitude;
	}

	public void getLongitude(double lon) {
		
		mLongitude = lon;
	}
}
