package sweetlife.android10.gps;

import android.app.Activity;
import android.location.Location;

public interface IGpsLoggerServiceClient
{
	public void OnStatusMessage(String message, int color );

	public void OnLocationUpdate(Location loc);
	
	public void OnLocationNotAvailable();
	
	public void OnSatelliteCount(int count);

    public void OnStopLogging();
	
	public Activity GetActivity();
}
