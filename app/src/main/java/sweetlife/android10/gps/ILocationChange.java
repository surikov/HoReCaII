package sweetlife.android10.gps;

import android.location.Location;

public interface ILocationChange {

	public void newLocationPoint(Location loc,String comment);
}
