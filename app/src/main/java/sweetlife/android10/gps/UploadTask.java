package sweetlife.android10.gps;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.acra.ErrorReporter;
import org.apache.http.HttpStatus;

import reactive.ui.*;
import sweetlife.android10.GPS;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.net.IParserBase.EParserResult;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.supervisor.Report_Base;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.ManagedAsyncTask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import sweetlife.android10.*;
import tee.binding.*;

public class UploadTask extends ManagedAsyncTask<String> implements ITableColumnsNames{
	private int TIMEOUT = 300 * 1000;
	private SQLiteDatabase mDB;
	public String mResultString = null;
	private String mDeviceID;

	public UploadTask(SQLiteDatabase db, String deviceID, Context appContext){
		super(appContext.getString(R.string.msg_send_gps_data), appContext);
		mDB = db;
		mDeviceID = deviceID;
	}

	public EParserResult UploadGPSPoints(String tpCode){
		String sql = "select _id,BeginTime,latitude,longitude from GPSPoints where Upload=0 order by _id limit 333";
		System.out.println("UploadGPSPoints " + tpCode + " " + sql);
		Cursor c;
		int biggestID = 0;
		TimeZone cuTZ = TimeZone.getDefault();
		mResultString = "Выгрузка GPS: нет невыгруженных координат";
		Bough raw = new Bough();
		while(biggestID >= 0){
			biggestID = -1;
			c = mDB.rawQuery(sql, null);
			String jsonBody = "[";
			String dlmtr = "";
			String BeginTime = "";
			double latitude = 0;
			double longitude = 0;
			while(c.moveToNext()){
				biggestID = c.getInt(0);
				BeginTime = c.getString(1);
				latitude = c.getDouble(2);
				longitude = c.getDouble(3);
				jsonBody = jsonBody + dlmtr + "\n{\"time\": \"" + Auxiliary.tryReFormatDate(BeginTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMddHHmmss") + "\""
						+ ", \"Poyas\": " + Math.round(cuTZ.getOffset(new Date().getTime()) / (1000 * 60 * 60))
						+ ", \"lat\": " + latitude
						+ ", \"long\": " + longitude
						+ "}";
				dlmtr = ", ";
			}
			c.close();
			jsonBody = jsonBody + "\n]";
			System.out.println("jsonBody " + jsonBody);
			if(biggestID >= 0){
				mResultString = "";
				logAndPublishProgress("выгрузка GPS за " + BeginTime);
				String url = Settings.getInstance().getBaseURL() + Settings.getInstance().selectedBase1C() + "/hs/GPS/ZagruzkaGPS/" + Cfg.whoCheckListOwner() + "/" + tpCode;
				System.out.println(url);
				//System.out.println(jsonBody);
				byte[] bytes = {};
				try{
					bytes = jsonBody.getBytes("UTF-8");
				}catch(Throwable t){
					t.printStackTrace();
				}
				Bough resp = Auxiliary.loadTextFromPrivatePOST(url, bytes, 180000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
				//System.out.println("resp: " + resp.dumpXML());
				raw.children = Bough.parseJSON(resp.child("raw").value.property.value()).children;
				//System.out.println("raw: " + raw.dumpXML());
				if(!raw.child("Статус").value.property.value().equals("0")){
					mResultString = "Точки GPS не выгружены, повторите выгрузку\n\n(" + raw.child("Сообщение").value.property.value() + ")\n";
					return EParserResult.EError;
				}
				mResultString = "Точки GPS выгружены: " + raw.child("Сообщение").value.property.value() + "\n";
				String upd = "update GPSPoints set Upload=1 where Upload=0 and _id<=" + biggestID;
				mDB.execSQL(upd);
			}
		}
		return EParserResult.EComplete;
	}

	public EParserResult UploadGPSPointsOLD(String tpCode){
		//mDB.execSQL("delete from GPSPoints where date(BeginTime)>date('now','+1 days');");
		String sql = "select _id,BeginTime,latitude,longitude from GPSPoints where Upload=0 order by _id limit 100";
		Cursor c;
		int biggestID = 0;
		TimeZone cuTZ = TimeZone.getDefault();
		//cuTZ.get
		mResultString = "Выгрузка GPS: нет невыгруженных координат";
		while(biggestID >= 0){
			biggestID = -1;
			c = mDB.rawQuery(sql, null);
			String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
					+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
					+ "<soap:Body>"//
					+ "<m:Get xmlns:m=\"http://ws.swlife.ru\">"//
					+ "<m:Paket xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
			String BeginTime = "";
			double latitude = 0;
			double longitude = 0;
			while(c.moveToNext()){
				biggestID = c.getInt(0);
				BeginTime = c.getString(1);
				latitude = c.getDouble(2);
				longitude = c.getDouble(3);
				xml = xml + "\n\t<m:CoordGPS><m:time>" //
						+ BeginTime + "</m:time><m:lat>" //
						+ latitude + "</m:lat><m:long>" //
						+ longitude + "</m:long>"//
						+ "<m:Poyas>" + Math.round(cuTZ.getOffset(new Date().getTime()) / (1000 * 60 * 60)) + "</m:Poyas>"//
						+ "</m:CoordGPS>";
			}
			c.close();
			//xml = xml + "<m:user>" + Requests.getTPCode(mDB
			//, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()
			//		) + "</m:user>";
			xml = xml + "<m:user>" + tpCode + "</m:user>";
			xml = xml + "</m:Paket></m:Get></soap:Body></soap:Envelope>";
			if(biggestID >= 0){
				//Auxiliary.inform(BeginTime, context);
				mResultString = "";
				logAndPublishProgress("выгрузка GPS за " + BeginTime);
				//RawSOAP rawSOAP = new RawSOAP().url.is(Settings.getInstance().getBaseURL()+"wsgetdebt/wsGPSAndroid.1cws").xml.is(xml);
				String gpsURL = Settings.getInstance().getBaseURL() + "wsgetdebt/wsGPSAndroid.1cws";
				//if (Settings.getInstance().isPrimaryGate) {
				gpsURL = Settings.getInstance().getBaseURL() + "wsGPSAndroid.1cws";
				//}
				//RawSOAP rawSOAP = new RawSOAP().url.is(Settings.getInstance().getBaseURL() + "wsgetdebt/wsGPSAndroid.1cws").xml.is(xml);
				RawSOAP rawSOAP = new RawSOAP().url.is(gpsURL).xml.is(xml);
				//System.out.println("biggestID: " + biggestID);
				System.out.println("gpsURL: " + gpsURL);
				Report_Base.startPing();
				rawSOAP.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				if(rawSOAP.statusCode.property.value() >= 100 //
						&& rawSOAP.statusCode.property.value() <= 300//
						&& rawSOAP.exception.property.value() == null//
				){
					//send xml
					String ok = rawSOAP.data.child("soap:Body").child("m:GetResponse").child("m:return").value.property.value();
					if(ok.trim().length() < 1){
						ok = rawSOAP.data.dumpXML();
					}
					mResultString = "Точки GPS выгружены: " + ok;
					//System.out.println("rawSOAP.data.dumpXML(): " + rawSOAP.data.dumpXML());
					String upd = "update GPSPoints set Upload=1 where Upload=0 and _id<=" + biggestID;
					mDB.execSQL(upd);
				}else{
					mResultString = "Точки GPS не выгружены, повторите выгрузку\n\n(" + rawSOAP.statusCode.property.value() + ": " + rawSOAP.exception.property.value() + ")";
					return EParserResult.EError;
				}
				//biggestID = -1;
			}
		}
		//mResultString = "Точки GPS выгружены";
		return EParserResult.EComplete;
	}/*
	private EParserResult UploadGPSPointsOLD() {
		StringBuilder resultString = new StringBuilder();
		GPSInfo gpsInfo = GPS.getGPSInfo();
		ArrayList<CoordGPS> gpsPoints = gpsInfo.getGPSPointsArray();
		if (gpsPoints.size() == 0) {
			mResultString = "GPS отметки: " + mResources.getString(R.string.msg_upload_visits_no_data);
			return EParserResult.EComplete;
		}
		System.out.println("gpsPoints.size(): " + gpsPoints.size());
		GPSPointsXMLSerializer serializer = new GPSPointsXMLSerializer(mDB, gpsPoints);
		String requestString = null;
		try {
			requestString = serializer.SerializeXML();
			System.out.println("requestString.length(): " + requestString.length());
			System.out.println(requestString);
		}
		catch (IOException e1) {
			mProgressDialogMessage = mResources.getString(R.string.gps_points_not_uploaded);
			mResultString = mProgressDialogMessage + " \n" + e1.getMessage() + "\n";
			return EParserResult.EError;
		}
		if (requestString != null && requestString.length() != 0) {
			HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_GPS_POINTS());
			request.setTimeOut(TIMEOUT);
			try {
				int r = request.Execute(requestString);
				if (r != HttpStatus.SC_OK) {
					resultString.append(mResources.getString(R.string.gps_points_not_uploaded)).append("\n");
					mResultString = resultString.toString() + " \nHttpStatus=" + r + "\n";
					return EParserResult.EError;
				}
				resultString.append(mResources.getString(R.string.gps_points_uploaded))//
						//.append(" (" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentName()+", "+ ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieName() + ")")//
						.append("\n");
				//---------------------------
				gpsInfo.setUploadPointGPS();
			}
			catch (Exception e) {
				ErrorReporter.getInstance().putCustomData("handled", "OK");
				ErrorReporter.getInstance().handleSilentException(e);
				resultString.append(mResources.getString(R.string.gps_points_not_uploaded)).append("\n");
				mResultString = resultString.toString() + " \n" + e + "\n";
				return EParserResult.EError;
			}
		}
		mResultString = resultString.toString();
		return EParserResult.EComplete;
	}*/

	public EParserResult UploadVizits(){
		try{
			String mUserKod = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
			String sql = "select * from Vizits where Upload = 0";
			Cursor cursor = mDB.rawQuery(sql, null);
			//System.out.println(Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());
			Bough raw = new Bough();
			if(!cursor.moveToFirst()){
				mResultString = mResultString + "\nВизиты: нет невыгруженных.";
				return EParserResult.EComplete;
			}else{
				TimeZone cuTZ = TimeZone.getDefault();
				String poyas = "" + Math.round(cuTZ.getOffset(new Date().getTime()) / (1000 * 60 * 60));
				String person = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
				String json = "[";
				String dlmtr = "";
				do{
					String activity = null;
					String beginTime = cursor.getString(cursor.getColumnIndex("BeginTime"));
					String endTime = cursor.getString(cursor.getColumnIndex("EndTime"));
					String client = cursor.getString(cursor.getColumnIndex("Client"));
					if(endTime == null){
						endTime = "";
					}
					if((endTime.length() > 0)){
						activity = cursor.getString(cursor.getColumnIndex("Activity"));
					}else{
						endTime = beginTime;
						activity = "Начало визита";
					}
					json = json + dlmtr + "{";
					json = json + "\"Extnumber\":\"" + mDeviceID + "\"";
					json = json + ",\"Client\":\"" + client + "\"";
					json = json + ",\"Begin\":\"" + Auxiliary.tryReFormatDate(beginTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMddHHmmss") + "\"";
					json = json + ",\"End\":\"" + Auxiliary.tryReFormatDate(endTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMddHHmmss") + "\"";
					json = json + ",\"Poyas\":\"" + poyas + "\"";
					json = json + ",\"Activity\":\"" + activity + "\"";
					json = json + ",\"Person\":\"" + person + "\"";
					json = json + "}";
					dlmtr = ",";
				}while(cursor.moveToNext());
				json = json + "]";
				String url = Settings.getInstance().getBaseURL() + Settings.getInstance().selectedBase1C()
						+ "/hs/GPS/ZagruzkaVizit/"
						+ Cfg.whoCheckListOwner()
						+ "/" + person;
				System.out.println("UploadVizits " + url);
				System.out.println("UploadVizits " + json);
				byte[] bytes = {};
				try{
					bytes = json.getBytes("UTF-8");
				}catch(Throwable t){
					t.printStackTrace();
				}
				Bough resp = Auxiliary.loadTextFromPrivatePOST(url, bytes, 180000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
				raw.children = Bough.parseJSON(resp.child("raw").value.property.value()).children;
				if(!raw.child("Статус").value.property.value().equals("0")){
					mResultString = mResultString + "Визиты: \n\n(" + raw.child("Сообщение").value.property.value() + ")\n";
					return EParserResult.EError;
				}
				mResultString = mResultString + "Визиты: " + raw.child("Сообщение").value.property.value() + "\n";
				//mResultString = mResultString + "\nВизиты: загружены.";
			}
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
			GPS.getGPSInfo().setUploadVisits();
		}catch(Throwable t){
			mResultString = mResultString + "\nВизиты: " + t.getMessage();
		}
		return EParserResult.EComplete;
	}

	public EParserResult __UploadVizits(){
		//mDB.execSQL("delete from Vizits where date(BeginTime)>date('now','+1 days');");

		//System.out.println("UploadVizits start");
		StringBuilder resultString = new StringBuilder();
		Cursor cursor = null;
		//TimeZone cuTZ = TimeZone.getDefault();
		try{
			String mUserKod = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
			/*String mUserKod = Requests.getTPCode(mDB
					//, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()
			);
			String tpCode = Requests.getTPCode(ApplicationHoreca.getInstance().getDataBase());
			String tpFIO = Requests.getTPfio(ApplicationHoreca.getInstance().getDataBase());
			String chOwner = sweetlife.horeca.supervisor.Cfg.whoCheckListOwner();
			if (chOwner.length() > 0) {
				String sql = "select l.naimenovanie as name,l.kod as kod from PhizLicaPolzovatelya f \n" +
						"join Polzovateli p on p._idrref=f.polzovatel join PhizicheskieLica l on l._idrref=f.phizlico \n" +
						"where trim(p.kod)='" + chOwner.trim() + "' order by f.period desc;"//
						;
				Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				tpCode = data.child("row").child("kod").value.property.value();
				tpFIO = data.child("row").child("name").value.property.value();
				System.out.println(mUserKod + " => " + tpCode + ": " + tpFIO);
				mUserKod = tpCode;

			}*/
			String sql = "select * from Vizits where Upload = 0";
			//String sql = "select * from Vizits where Upload = 0 or begindate='2016-03-23' or begindate='2016-03-24'";
			cursor = mDB.rawQuery(sql, null);
			if(!cursor.moveToFirst()){
				mResultString = mResultString + "\nВизиты: " + mResources.getString(R.string.msg_upload_visits_no_data);
				return EParserResult.EComplete;
			}
			do{
				System.out.println("UploadVizits next -------------------------------");
				try{
					String endTime = null;
					String activity = null;
					String endField = cursor.getString(cursor.getColumnIndex(ENDTIMEfieldName));
					if(endField == null){
						endField = "";
					}
					if(
						//(endField != null)
						//		&&
							(endField.length() > 0)
					){
						endTime = endField;
						activity = cursor.getString(cursor.getColumnIndex(ACTIVITY));
					}else{
						endTime = cursor.getString(cursor.getColumnIndex(BEGIN_TIME));
						activity = "Начало визита";
					}
					System.out.println("serializer VizitsXMLSerializer: " + cursor.getString(cursor.getColumnIndex(BEGIN_TIME))
							+ " -> " + endTime + "/"
							+ endField + "/"
							+ endField.length() + "/"
					);
					VizitsXMLSerializer serializer = new VizitsXMLSerializer(
							//cursor.getString(cursor.getColumnIndex(TP)), //"010842646"
							mUserKod//
							, cursor.getString(cursor.getColumnIndex(BEGIN_TIME))//
							, endTime//
							, cursor.getString(cursor.getColumnIndex(CLIENT))//
							, activity//
							, mDeviceID//
					);
					String requestString = null;
					System.out.println("UploadVizits 1");
					try{
						requestString = serializer.SerializeXML();
						System.out.println("UploadVizits requestString " + requestString);
						System.out.println("UploadVizits 2");
					}catch(IOException e1){
						System.out.println("UploadVizits 3");
						e1.printStackTrace();
						mProgressDialogMessage = mResources.getString(R.string.msg_upload_visits_fail);
						mResultString = mProgressDialogMessage + " \n" + e1.getMessage();
						return EParserResult.EError;
					}
					System.out.println("UploadVizits 4");
					if(requestString != null && requestString.length() != 0){
						System.out.println("UploadVizits 5");
						HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_VIZITS());
						//HTTPRequest request = new HTTPRequest(Settings.getInstance().getBaseURL() +"wsgetdebt/visitsAndroidtest.1cws");
						System.out.println("UploadVizits 6");
						request.setTimeOut(TIMEOUT);
						try{
							int r = request.Execute(requestString);
							if(r != HttpStatus.SC_OK){
								resultString.append(mResources.getString(R.string.msg_upload_visits_fail)).append("\n");
								mResultString = resultString.toString() + " \nHttpStatus=" + r + "\n";
								//System.out.println(request.getResponse());
								return EParserResult.EError;
							}
						}catch(Exception e){
							ErrorReporter.getInstance().handleSilentException(e);
							resultString.append(mResources.getString(R.string.msg_upload_visits_fail)).append("\n");
							mResultString = resultString.toString() + " \n" + e.getMessage() + "\n";
							return EParserResult.EError;
						}
					}
				}catch(Exception e){
					mProgressDialogMessage = mResources.getString(R.string.msg_upload_visits_fail);
					mResultString = mProgressDialogMessage + " \n" + e.getMessage();
				}
			}
			while(cursor.moveToNext());
			//System.out.println("UploadVizits done");
			GPS.getGPSInfo().setUploadVisits();
		}catch(Exception ex){
			ErrorReporter.getInstance().handleSilentException(ex);
			mProgressDialogMessage = mResources.getString(R.string.msg_upload_visits_fail);
			mResultString = mProgressDialogMessage + " \n" + ex.getMessage() + "\n";

			return EParserResult.EError;
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
		resultString.append(mResources.getString(R.string.msg_upload_visits_success)).append("\n");
		mResultString = mResultString + "\n" + resultString.toString();
		return EParserResult.EComplete;
	}

	public void logAndPublishProgress(String values){
		LogHelper.debug(this.getClass().getCanonicalName() + " logAndPublishProgress " + values);
		/*try {
			publishProgress(values);
		}
		catch (Throwable t) {
			LogHelper.debug(this.getClass().getCanonicalName() + " logAndPublishProgress exception" + t.getMessage());
		}*/
	}

	@Override
	protected String doInBackground(Object... arg0){
		//String tpCode = Requests.getTPCode(ApplicationHoreca.getInstance().getDataBase());
		String tpCode = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
		if(UploadGPSPoints(tpCode) == EParserResult.EComplete){
			logAndPublishProgress(mResources.getString(R.string.msg_upload_visits));
			UploadVizits();
		}
		return mResultString.toString();
	}

	@Override
	protected void onPostExecute(String result){
		LogHelper.debug(this.getClass().getCanonicalName() + ".onPostExecute: " + result);
		Bundle resultData = new Bundle();
		resultData.putString(RESULT_STRING, result);
		mTaskListener.onComplete(resultData);
	}
}
