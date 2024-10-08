package sweetlife.android10.gps;

import java.util.Iterator;

import sweetlife.android10.*;

//import android.location.GpsSatellite;
//import android.location.GpsStatus;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
//import android.location.LocationProvider;
import android.os.Bundle;

class GeneralLocationListener implements LocationListener
		//, GpsStatus.Listener
{

	private static SWLifeGpsService gpsLoggingService;

	GeneralLocationListener(SWLifeGpsService service){

		gpsLoggingService = service;
	}

	@Override
	public void onLocationChanged(Location loc){
		//System.out.println("GeneralLocationListener.onLocationChanged "+loc);
		try{
			if(loc != null){
				gpsLoggingService.SweetLocationChanged(loc, "GeneralLocationListener");
			}
		}catch(Exception ex){
			//gpsLoggingService.SetStatus(ex.getMessage(), SWLifeGpsService.RED_COLOR);
		}

	}

	public void onProviderDisabled(String provider){

		gpsLoggingService.StopGPSStatusListener();
		gpsLoggingService.LocationNotAvailable();
	}

	public void onProviderEnabled(String provider){

		gpsLoggingService.StartGPSStatusListener();
	}

	/*
		public void onStatusChanged(String provider, int status, Bundle extras){

			if(status == LocationProvider.OUT_OF_SERVICE){

				//gpsLoggingService.SetStatus("Установка соединения со спутниками...", SWLifeGpsService.YELLOW_COLOR);
				gpsLoggingService.LocationNotAvailable();
			}

			if(status == LocationProvider.AVAILABLE){

				//gpsLoggingService.SetStatus("GPS сервис запущен. Соединение со спутниками установлено:", SWLifeGpsService.GREEN_COLOR);
			}
		}
	*/
	/*
	public void onGpsStatusChanged(int event){

		switch(event){

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				//gpsLoggingService.SetStatus("GPS сервис запущен.", SWLifeGpsService.GREEN_COLOR);

				//GpsStatus status = gpsLoggingService.getGPSLocationManager().getGpsStatus(null);
				//System.out.println("onGpsStatusChanged status "+status);

				//int maxSatellites = status.getMaxSatellites();

				//Iterator<GpsSatellite> it = status.getSatellites().iterator();
				//int count = 0;
				//while(it.hasNext() && count <= maxSatellites){
				//	GpsSatellite s = it.next();

				//	if(s.usedInFix()){
				//		count++;
				//	}
				//}

				//gpsLoggingService.SetSatelliteInfo(count);

				//				if(count == 0) {

				//					gpsLoggingService.LocationNotAvailable();
				//				}

				break;

			case GpsStatus.GPS_EVENT_STARTED:
				break;

			case GpsStatus.GPS_EVENT_STOPPED:
				break;

		}
	}*/
}