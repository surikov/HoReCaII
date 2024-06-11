package sweetlife.android10.gps;

import android.app.Activity;
import android.location.Location;

public interface IGpsLoggerServiceClient {
	public void OnStatusMessage(String message, int color);

	public void On_LocationUpdate(Location loc);

	public void On_LocationNotAvailable();

	public void On_SatelliteCount(int count);

	public void On_StopLogging();

	public Activity Get_Activity();
}
