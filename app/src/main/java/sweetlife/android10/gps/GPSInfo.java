package sweetlife.android10.gps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.ISQLConsts;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.consts.ITableNames;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.DatabaseHelper;
import tee.binding.*;
import tee.binding.it.Numeric;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class GPSInfo implements ISQLConsts, ITableColumnsNames, ITableNames{
	public static final long GPS_NOT_AVAILABLE = -1;
	private String mPhizlicoCode; //Need from PhizicheskieLica table
	private SQLiteDatabase mDB;
	private SimpleDateFormat mDateTimeFormat;
	private SimpleDateFormat mDateTimeFormatNoShift;
	public static boolean lockInsertGPS = false;

	public GPSInfo(SQLiteDatabase db, String agentID){
		//System.out.println("GPSInfo create");
		mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		mDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		mDateTimeFormatNoShift = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		mDB = db;
		//mPhizlicoCode = Requests.getTPCode(mDB
		//, agentID
		//		);
		//System.out.println("GPSInfo created");
		mPhizlicoCode = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
	}

	public String getAgentCode(){
		return mPhizlicoCode;
	}

	public int HasUnfinishedVisits(){
		if(IsDatabaseOpened()){
			Cursor cursor = null;
			try{
				String sql = "select count(Vizits._id)"//
						+ " from Vizits"//
						+ " join kontragenty on vizits.client=kontragenty.kod"//
						+ " join MarshrutyAgentov on MarshrutyAgentov.kontragent=kontragenty._idrref"//
						+ " where EndTime is null";
				cursor = mDB.rawQuery(sql, null);
				if(cursor.moveToFirst()){
					return cursor.getInt(0);
				}
				return 0;
			}finally{
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return 0;
	}

	public String kontragentyVizitov(ClientInfo client){
		String names = "";

		try{
			Location clientLocation = new Location("ClientLocation");
			clientLocation.setLatitude(client.getLat());
			clientLocation.setLongitude(client.getLon());
			String sql = "select k.Naimenovanie,GeographicheskayaShirota,GeographicheskayaDolgota from Vizits v join Kontragenty k on k.kod=v.client where v.EndTime is null";
			Bough opened = Auxiliary.fromCursor(mDB.rawQuery(sql, null));

			for(int i = 0; i < opened.children.size(); i++){
				double shir = Numeric.string2double(opened.children.get(i).child("GeographicheskayaShirota").value.property.value());
				double dol = Numeric.string2double(opened.children.get(i).child("GeographicheskayaDolgota").value.property.value());
				//System.out.println("Naimenovanie "+opened.children.get(i).child("Naimenovanie").value.property.value());
				//System.out.println("shir "+shir);
				//System.out.println("dol "+dol);
				Location opLocation = new Location("opLocation");
				opLocation.setLatitude(shir);
				opLocation.setLongitude(dol);

				float distance = opLocation.distanceTo(clientLocation);
				//System.out.println("distance "+distance);
				//if(distance > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()){
				if(distance > Settings.MAX_DISTANCE_TO_CLIENT){
					names = names + " /" + opened.children.get(i).child("Naimenovanie").value.property.value();
				}
			}

		}catch(Throwable t){
			t.printStackTrace();
		}
		return names;
	}

	public boolean estDalnieVizity(ClientInfo client){
		Cursor cursor = null;
		try{
			String sql = "select _id from Vizits where EndTime is null and Client = " + client.getKod();
			cursor = mDB.rawQuery(sql, null);
			if(cursor.moveToFirst()){
				cursor.close();
				return true;
			}else{
				//cursor.close();
				//System.out.println("lat " + client.getLat());
				//System.out.println("lon " + client.getLon());
				Location clientLocation = new Location("ClientLocation");
				clientLocation.setLatitude(client.getLat());
				clientLocation.setLongitude(client.getLon());
				sql = "select k.Naimenovanie,GeographicheskayaShirota,GeographicheskayaDolgota from Vizits v join Kontragenty k on k.kod=v.client where v.EndTime is null and Client <> " + client.getKod();
				Bough opened = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				//System.out.println(opened.dumpXML());
				for(int i = 0; i < opened.children.size(); i++){
					double shir = Numeric.string2double(opened.children.get(i).child("GeographicheskayaShirota").value.property.value());
					double dol = Numeric.string2double(opened.children.get(i).child("GeographicheskayaDolgota").value.property.value());
					//System.out.println("Naimenovanie "+opened.children.get(i).child("Naimenovanie").value.property.value());
					//System.out.println("shir "+shir);
					//System.out.println("dol "+dol);
					Location opLocation = new Location("opLocation");
					opLocation.setLatitude(shir);
					opLocation.setLongitude(dol);

					float distance = opLocation.distanceTo(clientLocation);
					//System.out.println("distance "+distance);
					//if(distance > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()){
					if(distance > Settings.MAX_DISTANCE_TO_CLIENT){
						if(cursor != null && !cursor.isClosed()){
							cursor.close();
						}
						return true;
					}
				}
			}
		}catch(Throwable t){
			t.printStackTrace();
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return false;
	}

	public boolean IsVizitBegin(String clientCode){
		if(IsDatabaseOpened()){
			Cursor cursor = null;
			try{
				String sql = "select _id from Vizits where EndTime is null and Client = " + clientCode;
				cursor = mDB.rawQuery(sql, null);
				if(cursor.moveToFirst()){
					return true;
				}
				return false;
			}finally{
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return false;
	}

	public String findLastVizitBegin(String clientCode){
		String sql = "select beginTime as beginTime from Vizits where Client = " + clientCode + " order by beginTime desc limit 1";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String utc=b.child("row").child("beginTime").value.property.value();
		return Auxiliary.tryReFormatDate3(utc,"yyyy-MM-dd'T'HH:mm:ss","dd.MM.yyyy HH:mm:ss");
	}

	public String findLastVizitEnd(String clientCode){
		String sql = "select endTime as endTime from Vizits where Client = " + clientCode + " order by beginTime desc limit 1";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String utc =b.child("row").child("endTime").value.property.value();
		return Auxiliary.tryReFormatDate3(utc,"yyyy-MM-dd'T'HH:mm:ss","dd.MM.yyyy HH:mm:ss");
	}

	public String findPreVizitTimeDaily(String clientCode){
		if(IsDatabaseOpened()){
			Cursor cursor = null;
			try{
				String sql = "select beginTime from Vizits where strftime('%Y-%m-%d','now')=strftime('%Y-%m-%d',beginTime) and Client = " + clientCode + " order by beginTime desc";
				cursor = mDB.rawQuery(sql, null);
				if(cursor.moveToFirst()){
					String txt = cursor.getString(0);
					SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					java.util.Date tm = fromDate.parse(txt);
					tm.setHours(tm.getHours() + 3);
					SimpleDateFormat toDate = new SimpleDateFormat("HH:mm:ss");
					//String beginTime=Auxiliary.tryReFormatDate(txt,"yyyy-MM-dd'T'HH:mm:ss","HH:mm:ss");
					String beginTime = toDate.format(tm);
					return beginTime;
				}
				return null;
			}catch(Throwable t){
				t.printStackTrace();
			}finally{
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return null;
	}

	public boolean IsFirstVizitDaily(String clientCode){
		if(IsDatabaseOpened()){
			Cursor cursor = null;
			try{
				String sql = "select EndTime from Vizits where strftime('%Y-%m-%d','now')=strftime('%Y-%m-%d',EndTime) and Client = " + clientCode;
				cursor = mDB.rawQuery(sql, null);
				if(cursor.moveToFirst()){
					return false;
				}
				return true;
			}finally{
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return true;
	}

	public void CleanPointGPS(String date){
	}

	public void CleanVisit(String allBeforeDate){
	}

	public void setUploadPointGPS(){
		ContentValues updateValues = new ContentValues();
		updateValues.put(UPLOAD, TRUE);
		DatabaseHelper.updateInTranzaction(mDB, "GPSPoints", updateValues, null, null);
	}

	public void setUploadVisits(){
		System.out.println("setUploadVisits");
		ContentValues updateValues = new ContentValues();
		updateValues.put(UPLOAD, TRUE);
		DatabaseHelper.updateInTranzaction(mDB, VizitsTableName, updateValues, "Upload = 0 and EndTime is not null", null);

	}

	public synchronized void insertGpsPoint(Calendar time, double lat, double lon, String comment){
		if(lockInsertGPS){
			//System.out.println("insertPoint locked");
			//return;
		}
		//System.out.println("insertGpsPoint " + time.getTime() + " / " + time.getTimeZone().getDisplayName()+": "+comment);
		ContentValues initialValues = new ContentValues();
		/*
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){

					time.roll(Calendar.DAY_OF_YEAR, false);
				}*/
		Date date = new java.sql.Date(time.getTimeInMillis());
		initialValues.put(BEGIN_DATE, date.toString());
		initialValues.put(BEGIN_TIME, mDateTimeFormat.format(date));
		initialValues.put(LONGITUDE, lat);
		initialValues.put(LATITUDE, lon);//IsoDate.dateToString(coordGPS.time, IsoDate.DATE_TIME));
		initialValues.put(UPLOAD, FALSE);
		initialValues.put("comment", comment);
		if(IsDatabaseOpened()){
			//LogHelper.debug(this.getClass().getCanonicalName() + " insertPoint "+mDateTimeFormat.format(date));
			DatabaseHelper.insertInTranzaction(mDB, "GPSPoints", initialValues);
		}
	}

	public synchronized boolean BeginVizit(String clientCode,long distance){
		if(clientCode == null || clientCode.length() == 0){
			return false;
		}
		//System.out.println("BeginVizit " + clientCode);
		ContentValues initialValues = new ContentValues();
		String timeString = currentUTCdateTime();//getVizitTimeString();
		//java.util.Date date=new java.util.Date();
		initialValues.put(BEGIN_DATE, new java.sql.Date(System.currentTimeMillis()).toString());
		initialValues.put(BEGIN_TIME, timeString);
		initialValues.put(CLIENT, clientCode);
		initialValues.put(TP, mPhizlicoCode);
		initialValues.put(UPLOAD, FALSE);
		initialValues.put("gpsbegin", distance);
		if(IsDatabaseOpened()){
			DatabaseHelper.insertInTranzaction(mDB, VizitsTableName, initialValues);
		}
		return false;
	}

	public String currentUTCdateTime(){
		Calendar satellitesTime = Calendar.getInstance();
		long tim = satellitesTime.getTimeInMillis();
		return mDateTimeFormat.format(new java.util.Date(tim));
	}

	public String _____getVizitTimeString(){
		Calendar satellitesTime = Calendar.getInstance();
		//satellitesTime.setTimeInMillis(Session.getGPSTime());
		/*
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){

					satellitesTime.roll(Calendar.DAY_OF_YEAR, false);
				}*/
		long tim = satellitesTime.getTimeInMillis();
		String s = "";//mDateTimeFormat.format(new java.util.Date());
		if(tim < 1){
			//LogHelper.debug("wrong satellitesTime: " + satellitesTime.getTime());
			//s = mDateTimeFormat.format(new java.util.Date());
		}else{
			s = mDateTimeFormat.format(new java.util.Date(tim));
		}
		//java.util.Date d=new java.util.Date();  
		//String s= mDateTimeFormatNoShift.format(d);
		//LogHelper.debug(this.getClass().getCanonicalName() + " getVizitTimeString " + s);
		return s;
	}

	public boolean EndVisit(String clientCode, String action,double distance){
		if(!IsVizitBegin(clientCode)){
			return false;
		}
		ContentValues initialValues = new ContentValues();
		if(action != null && action.length() != 0){
			initialValues.put(ACTIVITY, action);
		}
		String timeString = currentUTCdateTime();//getVizitTimeString();
		//System.out.println(END_TIME + ": " + timeString);
		initialValues.put(ENDTIMEfieldName, timeString);
		initialValues.put("gpsfinish", distance);
		if(IsDatabaseOpened()){
			DatabaseHelper.updateInTranzaction(mDB, VizitsTableName, initialValues, "Client = " + clientCode + " and EndTime is null", null);
			//DatabaseHelper.updateInTranzaction(mDB, VISITS, initialValues, "Client = " + clientCode + " and EndTime is null", null);
			//DatabaseHelper.updateInTranzaction(mDB, VISITS, initialValues, "Client = " + clientCode + " and ((EndTime is null) or (length(EndTime)<4) or (EndTime=BeginTime))", null);
		}else{
			return false;
		}
		return true;
	}

	boolean IsDatabaseOpened(){
		if(mDB != null && mDB.isOpen()){
			return true;
		}
		//System.out.println("IsDatabaseOpened = false!");
		return false;
	}

	public static double lastLatitude(){
		Bough last = getLastSavedGPSpoin();
		double latitude = Numeric.string2double(last.child("row").child("latitude").value.property.value());
		return latitude;
	}

	public static double lastLongitude(){
		Bough last = getLastSavedGPSpoin();
		double longitude = Numeric.string2double(last.child("row").child("longitude").value.property.value());
		return longitude;
	}

	public static long lastDateTime(){
		Bough last = getLastSavedGPSpoin();
		String timeString = last.child("row").child("beginTime").value.property.value();
		try{
			Date date = Auxiliary.mssqlTime.parse(timeString);
			long ms = date.getTime();
			Calendar cal = Calendar.getInstance();
			TimeZone tz = cal.getTimeZone();
			int offset = tz.getRawOffset();
			//return ms + 3 * 60 * 60 * 1000;
			return ms + offset;
		}catch(Throwable t){
			t.printStackTrace();
			return -1;
		}
	}

	public static Bough getLastSavedGPSpoin(){
		String sql = "select beginTime as beginTime, '' || longitude as longitude, '' || latitude as latitude from GPSPoints order by beginTime desc limit 1;";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		return b;
	}

	public ArrayList<CoordGPS> getGPSPointsArray(){
		ArrayList<CoordGPS> list = new ArrayList<CoordGPS>();
		Cursor cursor = mDB.query("GPSPoints", new String[]{BEGIN_TIME, LATITUDE, LONGITUDE},
				//"date(BeginDate) = date() and Upload = " + FALSE, null, null, null, null);
				" Upload = " + FALSE, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				CoordGPS coordGPS = new CoordGPS( //IsoDate.stringToDate(cursor.getString(0), IsoDate.DATE_TIME)
						cursor.getString(cursor.getColumnIndex(BEGIN_TIME)), cursor.getDouble(cursor.getColumnIndex(LATITUDE)), cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
				list.add(coordGPS);
			}while(cursor.moveToNext());
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
			cursor = null;
		}
		return list;
	}

	public static long isTPNearClient(double lat, double lon){
		Location clientLocation = new Location("ClientLocation");
		clientLocation.setLatitude(lat);
		clientLocation.setLongitude(lon);
		String sql = "select beginTime as beginTime, longitude as longitude, latitude as latitude from GPSPoints where datetime(BeginTime)>datetime('now','-1 minutes') order by beginTime desc limit 123456;";
		//		System.out.println(sql);
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		java.util.Vector<Bough> rows = data.children("row");
		float minFor1m = 987654321;
		for(int ii = 0; ii < rows.size(); ii++){
			Location point = new Location("MyLocation");
			point.setLongitude(Numeric.string2double(rows.get(ii).child("latitude").value.property.value()));
			point.setLatitude(Numeric.string2double(rows.get(ii).child("longitude").value.property.value()));
			float distance = point.distanceTo(clientLocation);
			//System.out.println(rows.get(ii).dumpXML()+": "+distance);
			if(minFor1m > distance){
				minFor1m = distance;
			}
		}
		return (long)minFor1m;
	}

	public static boolean isExistsGPS1m(){
		String sql = "select beginTime as beginTime, longitude as longitude, latitude as latitude from GPSPoints where datetime(BeginTime)>datetime('now','-1 minutes') order by beginTime desc limit 123456;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		if(data.children("row").size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public static long isTPNearClient121212121(double lat, double lon){
		/*
		//if (System.currentTimeMillis() == 0 || Session.getLocalTime() == 0) {
		if (System.currentTimeMillis() == 0 || SWLifeGpsService.whenSavedGPSms == 0) {
			return GPS_NOT_AVAILABLE;
		}
		//if ((System.currentTimeMillis() - Session.getLocalTime()) > Settings.getInstance().getSPY_GPS_PERIOD()) {
		if ((System.currentTimeMillis() - SWLifeGpsService.whenSavedGPSms) > Settings.getInstance().getSPY_GPS_PERIOD()) {
			return GPS_NOT_AVAILABLE;
		}
		*/
		Location clientLocation = new Location("ClientLocation");
		clientLocation.setLatitude(lat);
		clientLocation.setLongitude(lon);
		Location lastKnownLocation = new Location("MyLocation");


		//lastKnownLocation.setLatitude(Session.getLatitude());
		lastKnownLocation.setLongitude(GPSInfo.lastLatitude());
		//lastKnownLocation.setLongitude(Session.getLongitude());
		lastKnownLocation.setLatitude(GPSInfo.lastLongitude());
		return Float.valueOf(lastKnownLocation.distanceTo(clientLocation)).longValue();
	}

	public static ArrayList<String> getVisitsResultsList(SQLiteDatabase db){
		ArrayList<String> vizitsResults = new ArrayList<String>();
		String sql = "select [Naimenovanie] from RezultatVizita order by [Kod]";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor != null && cursor.moveToFirst()){
			do{
				vizitsResults.add(cursor.getString(0));
			}while(cursor.moveToNext());
			cursor.close();
		}
		return vizitsResults;
	}

}
