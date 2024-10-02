package sweetlife.android10.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sweetlife.android10.GPS;
import sweetlife.android10.gps.IGpsLoggerServiceClient;
import sweetlife.android10.gps.SWLifeGpsService;
import sweetlife.android10.gps.Session;
import sweetlife.android10.gps.Utilities;
import sweetlife.android10.*;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class Activity_GpsInfo extends Activity_Base implements IGpsLoggerServiceClient
{
	SimpleDateFormat mPointsDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	TextView mTextLatitude;
	TextView mTextLongitude;
	TextView mTextDateTime;
	TextView mTextAltitude;
	TextView mTextSpeed;
	TextView mTextSatellites;
	TextView mTextDirection;
	TextView mTextAccuracy;
	TextView mTextStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_gpsinfo);
		mPointsDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		setTitle(R.string.title_gps_info);
		mTextLatitude = (TextView) findViewById(R.id.txtLatitude);
		mTextLongitude = (TextView) findViewById(R.id.txtLongitude);
		mTextDateTime = (TextView) findViewById(R.id.txtDateTimeAndProvider);
		mTextAltitude = (TextView) findViewById(R.id.txtAltitude);
		mTextSpeed = (TextView) findViewById(R.id.txtSpeed);
		mTextSatellites = (TextView) findViewById(R.id.txtSatellites);
		mTextDirection = (TextView) findViewById(R.id.txtDirection);
		mTextAccuracy = (TextView) findViewById(R.id.txtAccuracy);
		mTextStatus = (TextView) findViewById(R.id.textStatus);
		//if (true != //ApplicationHoreca.getInstance()
		//		GPS.SetServiceClient(this)) {
		//	Session.setCurrentStatus("GPS провайдеры недоступны!", SWLifeGpsService.RED_COLOR);
		//}
		On_LocationNotAvailable();
		//System.out.println("done Activity_GpsInfo.onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//ApplicationHoreca.getInstance()
		//GPS.DeleteServiceClient();
	}

	private void SetStatus(String message, int color) {
		mTextStatus.setText(message);
		mTextStatus.setTextColor(color);
	}

	private void SetSatelliteInfo(int number) {
		//Session.setSatelliteCount(number);
		mTextSatellites.setText(String.valueOf(number));
	}

	private void DisplayLocationInfo(Location loc) {
		try {
			if (loc == null) {
				return;
			}
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(loc.getTime());
			mTextDateTime.setText(mPointsDateTimeFormat.format(new Date(time.getTimeInMillis())));
			mTextLatitude.setText(String.valueOf(loc.getLatitude()));
			mTextLongitude.setText(String.valueOf(loc.getLongitude()));
			if (loc.hasAltitude()) {
				mTextAltitude.setText(String.valueOf(loc.getAltitude()) + getString(R.string.meters));
			} else {
				mTextAltitude.setText(R.string.not_applicable);
			}
			if (loc.hasSpeed()) {
				mTextSpeed.setText(String.valueOf(loc.getSpeed()) + getString(R.string.meters_per_second));
			} else {
				mTextSpeed.setText(R.string.not_applicable);
			}
			if (loc.hasBearing()) {
				float bearingDegrees = loc.getBearing();
				String direction;
				direction = Utilities.GetBearingDescription(bearingDegrees, getBaseContext());
				mTextDirection.setText(direction + "(" + String.valueOf(Math.round(bearingDegrees)) + getString(R.string.degree_symbol) + ")");
			} else {
				mTextDirection.setText(R.string.not_applicable);
			}
			if (!Session.isUsingGps()) {
				mTextSatellites.setText(R.string.not_applicable);
				//Session.setSatelliteCount(0);
			}
			if (loc.hasAccuracy()) {
				float accuracy = loc.getAccuracy();
				mTextAccuracy.setText(getString(R.string.accuracy_within, String.valueOf(accuracy), getString(R.string.meters)));
			} else {
				mTextAccuracy.setText(R.string.not_applicable);
			}
		} catch (Exception ex) {
			SetStatus(getString(R.string.error_displaying, ex.getMessage()), SWLifeGpsService.RED_COLOR);
		}
	}

	@Override
	public void On_StopLogging() {
	}

	@Override
	public void On_LocationUpdate(Location loc) {
		DisplayLocationInfo(loc);
	}

	@Override
	public void On_SatelliteCount(int count) {
		SetSatelliteInfo(count);
	}

	@Override
	public void OnStatusMessage(String message, int color) {
		SetStatus(message, color);
	}

	@Override
	public Activity Get_Activity() {
		return this;
	}

	@Override
	public void On_LocationNotAvailable() {
		//System.out.println("OnLocationNotAvailable");
		mTextLatitude.setText(R.string.not_applicable);
		mTextLongitude.setText(R.string.not_applicable);
		mTextDateTime.setText(R.string.not_applicable);
		mTextAltitude.setText(R.string.not_applicable);
		mTextSpeed.setText(R.string.not_applicable);
		mTextSatellites.setText(R.string.not_applicable);
		mTextDirection.setText(R.string.not_applicable);
		mTextAccuracy.setText(R.string.not_applicable);
		//SetStatus(Session.getCurrentStatus(), Session.getCurrentStatusColor());
	}
}
