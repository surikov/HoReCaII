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

public class GPSInfo implements ISQLConsts, ITableColumnsNames, ITableNames {
	public static final long GPS_NOT_AVAILABLE = -1;
	private String mPhizlicoCode; //Need from PhizicheskieLica table
	private SQLiteDatabase mDB;
	private SimpleDateFormat mDateTimeFormat;
	private SimpleDateFormat mDateTimeFormatNoShift;
	public static boolean lockInsert = false;
	public GPSInfo(SQLiteDatabase db, String agentID) {
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
	public String getAgentCode() {
		return mPhizlicoCode;
	}
	public int HasUnfinishedVisits() {
		if (IsDatabaseOpened()) {
			Cursor cursor = null;
			try {
				String sql = "select count(Vizits._id)"//
						+ " from Vizits"//
						+ " join kontragenty on vizits.client=kontragenty.kod"//
						+ " join MarshrutyAgentov on MarshrutyAgentov.kontragent=kontragenty._idrref"//
						+ " where EndTime is null";
				cursor = mDB.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					return cursor.getInt(0);
				}
				return 0;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
		return 0;
	}
	public String kontragentyVizitov(ClientInfo client){
		String names="";
		
		try {
				Location clientLocation = new Location("ClientLocation");
				clientLocation.setLatitude(client.getLat());
				clientLocation.setLongitude(client.getLon());
				String sql = "select k.Naimenovanie,GeographicheskayaShirota,GeographicheskayaDolgota from Vizits v join Kontragenty k on k.kod=v.client where v.EndTime is null";
				Bough opened = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				
				for (int i = 0; i < opened.children.size(); i++) {
					double shir=Numeric.string2double(opened.children.get(i).child("GeographicheskayaShirota").value.property.value());
					double dol=Numeric.string2double(opened.children.get(i).child("GeographicheskayaDolgota").value.property.value());
					//System.out.println("Naimenovanie "+opened.children.get(i).child("Naimenovanie").value.property.value());
					//System.out.println("shir "+shir);
					//System.out.println("dol "+dol);
					Location opLocation = new Location("opLocation");
					opLocation.setLatitude(shir);
					opLocation.setLongitude(dol);
					
					float distance = opLocation.distanceTo(clientLocation);
					//System.out.println("distance "+distance);
					if(distance>300){
						names=names+" /"+opened.children.get(i).child("Naimenovanie").value.property.value();
					}
				}
			
		} catch (Throwable t) {
			t.printStackTrace();
		} 
		return names;
	}
	public boolean estDalnieVizity(ClientInfo client) {
		Cursor cursor = null;
		try {
			String sql = "select _id from Vizits where EndTime is null and Client = " + client.getKod();
			cursor = mDB.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				cursor.close();
				return true;
			} else {
				//cursor.close();
				//System.out.println("lat " + client.getLat());
				//System.out.println("lon " + client.getLon());
				Location clientLocation = new Location("ClientLocation");
				clientLocation.setLatitude(client.getLat());
				clientLocation.setLongitude(client.getLon());
				sql = "select k.Naimenovanie,GeographicheskayaShirota,GeographicheskayaDolgota from Vizits v join Kontragenty k on k.kod=v.client where v.EndTime is null and Client <> " + client.getKod();
				Bough opened = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				//System.out.println(opened.dumpXML());
				for (int i = 0; i < opened.children.size(); i++) {
					double shir=Numeric.string2double(opened.children.get(i).child("GeographicheskayaShirota").value.property.value());
					double dol=Numeric.string2double(opened.children.get(i).child("GeographicheskayaDolgota").value.property.value());
					//System.out.println("Naimenovanie "+opened.children.get(i).child("Naimenovanie").value.property.value());
					//System.out.println("shir "+shir);
					//System.out.println("dol "+dol);
					Location opLocation = new Location("opLocation");
					opLocation.setLatitude(shir);
					opLocation.setLongitude(dol);
					
					float distance = opLocation.distanceTo(clientLocation);
					//System.out.println("distance "+distance);
					if(distance>300){
						if (cursor != null && !cursor.isClosed()) {
							cursor.close();
						}
						return true;
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return false;
	}
	public boolean IsVizitBegin(String clientCode) {
		if (IsDatabaseOpened()) {
			Cursor cursor = null;
			try {
				String sql = "select _id from Vizits where EndTime is null and Client = " + clientCode;
				cursor = mDB.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					return true;
				}
				return false;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
		return false;
	}
	public String findPreVizitTimeDaily(String clientCode) {
		if (IsDatabaseOpened()) {
			Cursor cursor = null;
			try {
				String sql = "select beginTime from Vizits where strftime('%Y-%m-%d','now')=strftime('%Y-%m-%d',beginTime) and Client = " + clientCode+" order by beginTime desc";
				cursor = mDB.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
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
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
		return null;
	}
	public boolean IsFirstVizitDaily(String clientCode) {
		if (IsDatabaseOpened()) {
			Cursor cursor = null;
			try {
				String sql = "select EndTime from Vizits where strftime('%Y-%m-%d','now')=strftime('%Y-%m-%d',EndTime) and Client = " + clientCode;
				cursor = mDB.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					return false;
				}
				return true;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
		return true;
	}
	public void CleanPointGPS(String date) {
	}
	public void CleanVisit(String allBeforeDate) {
	}
	public void setUploadPointGPS() {
		ContentValues updateValues = new ContentValues();
		updateValues.put(UPLOAD, TRUE);
		DatabaseHelper.updateInTranzaction(mDB, GPS_POINTS, updateValues, null, null);
	}
	public void setUploadVisits() {
		//System.out.println("setUploadVisits");
		ContentValues updateValues = new ContentValues();
		updateValues.put(UPLOAD, TRUE);
		DatabaseHelper.updateInTranzaction(mDB, VISITS, updateValues, "Upload = 0 and EndTime is not null", null);
	}
	public synchronized void insertPoint(Calendar time, double lat, double lon) {
		if (lockInsert) {
			//System.out.println("insertPoint locked");
			//return;
		}
		//System.out.println("insertPoint " + time.getTime() + " / " + time.getTimeZone().getDisplayName());
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
		if (IsDatabaseOpened()) {
			//LogHelper.debug(this.getClass().getCanonicalName() + " insertPoint "+mDateTimeFormat.format(date));
			DatabaseHelper.insertInTranzaction(mDB, GPS_POINTS, initialValues);
		}
	}
	public synchronized boolean BeginVizit(String clientCode) {
		if (clientCode == null || clientCode.length() == 0) {
			return false;
		}
		//System.out.println("BeginVizit " + clientCode);
		ContentValues initialValues = new ContentValues();
		String timeString = getVizitTimeString();
		//java.util.Date date=new java.util.Date();
		initialValues.put(BEGIN_DATE, new java.sql.Date(System.currentTimeMillis()).toString());
		initialValues.put(BEGIN_TIME, timeString);
		initialValues.put(CLIENT, clientCode);
		initialValues.put(TP, mPhizlicoCode);
		initialValues.put(UPLOAD, FALSE);
		if (IsDatabaseOpened()) {
			DatabaseHelper.insertInTranzaction(mDB, VISITS, initialValues);
		}
		return false;
	}
	public String getVizitTimeString() {
		Calendar satellitesTime = Calendar.getInstance();
		satellitesTime.setTimeInMillis(Session.getGPSTime());
		/*
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){

					satellitesTime.roll(Calendar.DAY_OF_YEAR, false);
				}*/
		long tim = satellitesTime.getTimeInMillis();
		String s = "";//mDateTimeFormat.format(new java.util.Date());
		if (tim < 1) {
			//LogHelper.debug("wrong satellitesTime: " + satellitesTime.getTime());
			//s = mDateTimeFormat.format(new java.util.Date());
		} else {
			s = mDateTimeFormat.format(new java.util.Date(tim));
		}
		//java.util.Date d=new java.util.Date();  
		//String s= mDateTimeFormatNoShift.format(d);
		//LogHelper.debug(this.getClass().getCanonicalName() + " getVizitTimeString " + s);
		return s;
	}
	public boolean EndVisit(String clientCode, String action) {
		if (!IsVizitBegin(clientCode)) {
			return false;
		}
		ContentValues initialValues = new ContentValues();
		if (action != null && action.length() != 0) {
			initialValues.put(ACTIVITY, action);
		}
		String timeString = getVizitTimeString();
		//System.out.println(END_TIME + ": " + timeString);
		initialValues.put(END_TIME, timeString);
		if (IsDatabaseOpened()) {
			DatabaseHelper.updateInTranzaction(mDB, VISITS, initialValues, "Client = " + clientCode + " and EndTime is null", null);
		} else {
			return false;
		}
		return true;
	}
	boolean IsDatabaseOpened() {
		if (mDB != null && mDB.isOpen()) {
			return true;
		}
		//System.out.println("IsDatabaseOpened = false!");
		return false;
	}
	public static Bough getLastSavedGPSpoin(){
		String sql="select beginTime as beginTime, '' || longitude as longitude, '' || latitude as latitude from GPSPoints order by beginTime desc limit 1;";
		Bough b=Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql,null));
		return b;
	}
	public ArrayList<CoordGPS> getGPSPointsArray() {
		ArrayList<CoordGPS> list = new ArrayList<CoordGPS>();
		Cursor cursor = mDB.query(GPS_POINTS, new String[] { BEGIN_TIME, LATITUDE, LONGITUDE },
		//"date(BeginDate) = date() and Upload = " + FALSE, null, null, null, null);
				" Upload = " + FALSE, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				CoordGPS coordGPS = new CoordGPS( //IsoDate.stringToDate(cursor.getString(0), IsoDate.DATE_TIME)
						cursor.getString(cursor.getColumnIndex(BEGIN_TIME)), cursor.getDouble(cursor.getColumnIndex(LATITUDE)), cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
				list.add(coordGPS);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return list;
	}
	public static long isTPNearClient(double lat, double lon) {
		if (System.currentTimeMillis() == 0 || Session.getLocalTime() == 0) {
			return GPS_NOT_AVAILABLE;
		}
		if ((System.currentTimeMillis() - Session.getLocalTime()) > Settings.getInstance().getSPY_GPS_PERIOD()) {
			return GPS_NOT_AVAILABLE;
		}
		Location clientLocation = new Location("ClientLocation");
		clientLocation.setLatitude(lat);
		clientLocation.setLongitude(lon);
		Location lastKnownLocation = new Location("MyLocation");
		lastKnownLocation.setLatitude(Session.getLatitude());
		lastKnownLocation.setLongitude(Session.getLongitude());
		return Float.valueOf(lastKnownLocation.distanceTo(clientLocation)).longValue();
	}
	public static ArrayList<String> getVisitsResultsList(SQLiteDatabase db) {
		ArrayList<String> vizitsResults = new ArrayList<String>();
		String sql = "select [Naimenovanie] from RezultatVizita order by [Kod]";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				vizitsResults.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}
		return vizitsResults;
	}

}
