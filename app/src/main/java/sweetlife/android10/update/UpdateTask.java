package sweetlife.android10.update;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.acra.ErrorReporter;

import reactive.ui.Auxiliary;
import reactive.ui.RawSOAP;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IUpdaterConsts;
import sweetlife.android10.database.FLAGS;
import sweetlife.android10.database.UpdateTempTables;
import sweetlife.android10.gps.GPSInfo;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.supervisor.Report_Base;
import sweetlife.android10.ui.Activity_Login;
import sweetlife.android10.utils.Compress;
import sweetlife.android10.utils.Decompress;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.MemoryStatus;
import sweetlife.android10.utils.NetworkHelper;
import sweetlife.android10.utils.ftpClient;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import sweetlife.android10.R;
import tee.binding.Bough;

//
public class UpdateTask extends ManagedAsyncTask<Integer> implements IUpdaterConsts, FilenameFilter {
	public static final int SUCCESSFULL = -1;
	public static final int UPDATE_APP_FINISHED = 0;
	public static final int ERROR_FTP_DOWNLOAD_XML = 1;
	public static final int ERROR_PARSE_XML = 2;
	public static final int ERROR_FTP_DOWNLOAD_APK = 3;
	public static final int ERROR_APK_FILE_NOT_FOUND = 4;
	public static final int ERROR_NOT_CONNECTED = 5;
	public static final int ERROR_FTP_DOWNLOAD_DELTA = 6;
	public static final int ERROR_UNZIP_DELTA = 7;
	public static final int ERROR_FREE_SPACE = 8;
	public static final int ERROR_NOT_FTP_CONNECTED = 9;
	public static final int UPDATE_APP_VERSION = 10;
	public static final int UNKNOWN_IMEI = 123;
	public static final int LOCKED_IMEI = 124;
	private Context mApplicationContext;
	private UpdateChecker mUpdateChecker;
	private SQLiteDatabase mDB;
	private Settings mSettings;
	//private Timer mExecuteQueriesTimer;
	private DeltaUpdater mDeltaUpdater;
	//private SQLiteDatabase mDBLogging;
	//private ExecuteQueriesTask mExecuteQueriesTask;
	private boolean mIsTodayFirstTime = true;
	private boolean mIsRestored = false;
	private String mNoneStableVersion = "";
	private String mFTPDeltaFileName = "";

	public UpdateTask(Context appContext, SQLiteDatabase db) {
		super("" , appContext);
		mApplicationContext = appContext;
		mSettings = Settings.getInstance();
		mUpdateChecker = new UpdateChecker();
		mDB = db;
		mDeltaUpdater = new DeltaUpdater(mDB, this);
		//mDBLogging = ApplicationHoreca.getInstance().getLogDataBase();
	}

	public void logAndPublishProcess(String s) {
		LogHelper.debug("logAndPublishProcess: " + s);
		publishProgress(s);
	}


	int checkIMEI() {
		//ERROR_FTP_DOWNLOAD_XML
		//http://89.109.7.162/hrc120107/hs/surikovimei/352101051639433
		//return SUCCESSFULL;
		//Cfg.stripimei
		/*if(ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().equals("HRC00")){
			System.out.println("no check for hrc00");
			return SUCCESSFULL;
		}*/
		try {
			//byte[] bytes=Auxiliary.loadFileFromURL("http://89.109.7.162/hrc120107/hs/surikovimei/" + Cfg.stripimei);
			String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/surikovimei/" + Cfg.device_id();
			System.out.println(url);
			byte[] bytes = Auxiliary.loadFileFromPrivateURL(url.trim(), Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			String json = new String(bytes, "UTF-8");
			Bough bough = Bough.parseJSON(json);
			String cuHRC = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().toUpperCase();
			//System.out.println(cuHRC);
			//System.out.println(bough.dumpXML());
			for (int i = 0; i < bough.children.size(); i++) {
				String h = bough.children.get(i).child("код").value.property.value().trim().toUpperCase();
				String z = bough.children.get(i).child("запрет").value.property.value().trim().toUpperCase();
				//System.out.println(h+"/"+z);
				if (h.equals(cuHRC) || cuHRC.equals("HRC00")) {
					if (z.equals("0")) {
						//System.out.println("found");
						return SUCCESSFULL;
					} else {
						return LOCKED_IMEI;
					}
				}
			}
			//not found
			return SUCCESSFULL;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return UNKNOWN_IMEI;
	}

	@Override
	protected Integer doInBackground(Object... arg0) {
		//System.out.println(this.getClass().getCanonicalName() + ".doInBackground: set priority");
		try {
			int tid = android.os.Process.myTid();
			//System.out.println(this.getClass().getCanonicalName()+": priority before change = " + android.os.Process.getThreadPriority(tid));
			int pre = Thread.currentThread().getPriority();
			//System.out.println(this.getClass().getCanonicalName()+": current priority before change = "+Thread.currentThread().getPriority());
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
			//System.out.println(this.getClass().getCanonicalName() + ": priority " + pre + " => " + android.os.Process.getThreadPriority(tid));
			//System.out.println(this.getClass().getCanonicalName()+": current priority after change = " + Thread.currentThread().getPriority());
		} catch (Throwable t) {
			System.out.println(this.getClass().getCanonicalName() + ": " + t.getMessage());
		}
		UpdateTask.logUpdateStart();

		sweetlife.android10.utils.DatabaseHelper.adjustDataBase(mDB);
		//logAndPublishProcess("Анализ БД");
		//mDB.execSQL("analyze");
		logAndPublishProcess(mResources.getString(R.string.msg_connected));
		//Читаем данные из базы логирования относительно резервного восстановления и первого на сегодня запуска
		//int result = ReadDataFromLogDatabase();
		int result = SUCCESSFULL;
		if (result != SUCCESSFULL) {
			return result;
		}

		//result = checkIMEI();
		//if (result != SUCCESSFULL) {
		//    return result;
		//}
		//		if(true) return UPDATE_APP_FINISHED;
		//Проверяем наличие свободного места
		logAndPublishProcess(mResources.getString(R.string.msg_check_free_space));
		if (!HasFreeSpace()) {
			return ERROR_FREE_SPACE;
		}
		logAndPublishProcess(mResources.getString(R.string.title_progress_update_temp_tables));
		//Обновляем временные таблицы
		UpdateTempTables(FLAGS.FLAG_TEMP_ALL | FLAGS.FLAG_TEMP_LIMITY);
		//Если первый запуск сегодня
		//if (mIsTodayFirstTime) {
		//Создаем резервную копию базы данных
		//CreateDatabaseBackup();
		//}
		logAndPublishProcess(mResources.getString(R.string.msg_connected));
		//Если нет доспупа к сети интернет прерываем update
		if (!NetworkHelper.IsNetworkConnectionAvailable(mApplicationContext)) {
			return ERROR_NOT_FTP_CONNECTED;
		}
		//Загружаем и обновляем настройки
		//		DownloadAndReadSettingsXML();
		//Загружаем и парсим файл update.xml
		if ((result = DownloadAndReadUpdateXML()) != SUCCESSFULL) {
			return result;
		}
		//Проверяем есть ли обновления приложения
		if (IsNeedAppUpdate(mUpdateChecker.getServerAppVersion())) {
			return DownloadAndExecuteAppUpdate();
		}
		//Проверяем если было восстановление и версия приложения не поднялась - завершаем обновление
		//if (mIsRestored) {
		//return UPDATE_APP_FINISHED;
		//LogHelper.debug(this.getClass().getCanonicalName() + ".mIsRestored: " + mIsRestored);
		//}
		/*
		//скачиваем и парсим дельту
				if ((result = DownloadAndReadDelta()) != SUCCESSFULL) {
					sweetlife.horeca.utils.DatabaseHelper.adjustDataBase(mDB);
					setupAndStripData(mDB, this);
					return result;
				}
		*/
		//LogHelper.setLastUpdateStatus(LogHelper.LOG_TYPE_SUCCESS);
		//SendResponseXML();
		//LogHelper.logUpdateStart();
		//LogHelper.logUpdateEnd();
		//Обновляем временные таблицы
		//UpdateTempTables(mDeltaUpdater.getTempTablesInfo().getTempTablesFlags());
		//logAndPublishProcess("Анализ базы данных");
		//sweetlife.horeca.utils.DatabaseHelper.adjustDataBase(mDB);
		//setupAndStripData(mDB, this);
		//mDB.execSQL("analyze");
		//SendResponseXML();
		//Обновляем временные таблицы
		update2();
		logAndPublishProcess("Анализ базы данных");
		UpdateTempTables(mDeltaUpdater.getTempTablesInfo().getTempTablesFlags());
		sweetlife.android10.utils.DatabaseHelper.adjustDataBase(mDB);
		setupAndStripData(mDB, this);

		//Создаем резервную копию базы данных
		CreateDatabaseBackup();
		UpdateTask.logUpdateFinished();
		//System.out.println("d 101715 "+Auxiliary.fromCursor(mDB.rawQuery("select n._id,n.[_IDRRef],n.[Artikul] from Nomenklatura_sorted n where n.artikul=101715;",null)).dumpXML());
		return UPDATE_APP_FINISHED;
		//----------->*/
		//update2();
		//return 0;
	}

	Calendar stripDate(Calendar d) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date r = sdf.parse(sdf.format(d.getTime()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(r);
		return cal;
	}

	void _testPrice() {
		String sql = "select count(price.vladelec),price.vladelec from Price price where price.vladelec=x'9E44002264FA89D811E17D93D0AD2C32' group by price.vladelec limit 100;";
		//System.out.println(sql+" "+Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());
		sql = "select count(price.vladelec),price.vladelec from Price_strip price where price.vladelec=x'9E44002264FA89D811E17D93D0AD2C32' group by price.vladelec limit 100;";
		//System.out.println(sql+" "+Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());
	}


	public static void logUpdateStart() {
		System.out.println("logUpdateBegin");
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
				//       +"/hs/ObnovlenieInfo/ЗаписатьНачалоОбновления/"+Cfg.currentHRC();
				+ "/hs/ObnovlenieInfo/%D0%97%D0%B0%D0%BF%D0%B8%D1%81%D0%B0%D1%82%D1%8C%D0%9D%D0%B0%D1%87%D0%B0%D0%BB%D0%BE%D0%9E%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F/" + Cfg.whoCheckListOwner();
		try {
			System.out.println(url);
			byte[] bytes = Auxiliary.loadFileFromPrivateURL(url.trim(), Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			String json = new String(bytes, "UTF-8");
			Bough bough = Bough.parseJSON(json);
			System.out.println(bough.dumpXML());
			Cfg.currentLogUpdateKey = bough.child("Сессия").value.property.value();
			System.out.println("Cfg.currentLogUpdateKey: " + Cfg.currentLogUpdateKey);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void logUpdateFinished() {
		System.out.println("logUpdateDone");
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
				//+"/hs/ObnovlenieInfo/ЗаписатьОкончаниеОбновления/"+Cfg.currentHRC()+"/"+logUpdateKey;
				+ "/hs/ObnovlenieInfo/%D0%97%D0%B0%D0%BF%D0%B8%D1%81%D0%B0%D1%82%D1%8C%D0%9E%D0%BA%D0%BE%D0%BD%D1%87%D0%B0%D0%BD%D0%B8%D0%B5%D0%9E%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F/"
				+ Cfg.whoCheckListOwner() + "/" + Cfg.currentLogUpdateKey;
		try {
			System.out.println(url);
			byte[] bytes = Auxiliary.loadFileFromPrivateURL(url.trim(), Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			String json = new String(bytes, "UTF-8");
			Bough bough = Bough.parseJSON(json);
			System.out.println(bough.dumpXML());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	boolean update2() {
		System.out.println("update2");
		Calendar lastSuccessfulUpdate;
		//logUpdateBegin();
		try {
			lastSuccessfulUpdate = stripDate(LogHelper.getLastSuccessfulUpdate());
			//lastSuccessfulUpdate.add(Calendar.DAY_OF_MONTH, -1);
			Calendar now = stripDate(Calendar.getInstance());
			now.add(Calendar.DAY_OF_MONTH, 1);



			String hrc = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim();
			System.out.println("lastSuccessfulUpdate " + lastSuccessfulUpdate);
			lastSuccessfulUpdate.add(Calendar.DAY_OF_MONTH, 1);
			while (lastSuccessfulUpdate.getTimeInMillis() <= now.getTimeInMillis()) {
				updateCommon(lastSuccessfulUpdate);
				lastSuccessfulUpdate.add(Calendar.DAY_OF_MONTH, 1);
				//testPrice();
				System.out.println("lastSuccessfulUpdate " + lastSuccessfulUpdate);
			}
			//testPrice();


			updatePersonal(hrc);
			//testPrice();
			//logUpdateDone();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	void updateCommon(Calendar when) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String key = sdf.format(when.getTime());
		String dt = Auxiliary.rusDate.format(when.getTime());
		System.out.println("updateCommon " + Settings.getInstance().getBaseFileStoreURL() + "update2/common/" + key + ".zip");
		//logAndPublishProcess("обновление " + key);
		//if (executeUpdate("обновление " + key, "http://89.109.7.162/androbmen/update2/common/" + key + ".zip", key + ".sql")) {
		if (executeUpdate("обновление за " + dt, Settings.getInstance().getBaseFileStoreURL() + "update2/common/" + key + ".zip" , key + ".sql")) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(when.getTimeInMillis());
			//c.add(Calendar.DAY_OF_MONTH, 1);
			//
			LogHelper.setLastSuccessfulUpdate(c);
			/*
			c.set(Calendar.HOUR_OF_DAY, 1);
			c.set(Calendar.MINUTE, 1);
			c.set(Calendar.SECOND, 1);
			c.set(Calendar.MILLISECOND, 1);
			//
			Calendar now = Calendar.getInstance();
			//
			now.set(Calendar.HOUR_OF_DAY, 1);
			now.set(Calendar.MINUTE, 1);
			now.set(Calendar.SECOND, 1);
			now.set(Calendar.MILLISECOND, 2);
			//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println(sdf.format(c.getTime()) + " / " + c.getTimeInMillis() + " ? " + sdf.format(now.getTime()) + " / " + now.getTimeInMillis());
			if (now.getTimeInMillis() > c.getTimeInMillis()) {
				LogHelper.setLastSuccessfulUpdate(c);
			}
			//LogHelper.setLastSuccessfulUpdate(syncCalendar);
			*/
		}
	}

	void updatePersonal(String hrc) {
		//System.out.println("updatePersonal " + hrc);
		//logAndPublishProcess(" обновление " + hrc);
		//executeUpdate("обновление " + hrc, "http://89.109.7.162/androbmen/update2/personal/" + hrc + ".zip", hrc + ".sql");
		executeUpdate("обновление " + hrc, Settings.getInstance().getBaseFileStoreURL() + "update2/personal/" + hrc + ".zip" , hrc + ".sql");
		int numRead = getLastKey();
		//String wsURL = "http://89.109.7.162/hrc120107/hs/OthetOPrihtenii/" + hrc + "/" + numRead + "?komment=";
		String wsURL = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/OthetOPrihtenii/" + hrc + "/" + numRead + "?komment=";
		//String wsURL = Settings.getInstance().getBaseURL()+"GolovaNew/hs/OthetOPrihtenii/" + hrc + "/" + numRead + "?komment=";
		byte[] b;
		try {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm");
			//String version=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			String updateComment="v"+Activity_Login.packageVersion+"-"+sdf.format(c.getTime());
			wsURL = wsURL + updateComment;
			System.out.println("send " + wsURL);
			b = Auxiliary.loadFileFromPrivateURL(wsURL, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			logAndPublishProcess("Результат: " + new String(b, "UTF-8"));//02-14 17:46:14.369: I/System.out(9843): response: ок
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int getLastKey() {
		String sql = "select NomerVersiiKonphiguracii from Consts limit 1;";
		//System.out.println(sql);
		try {
			Cursor c = mDB.rawQuery(sql, null);
			c.moveToNext();
			return c.getInt(0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return 0;
	}

	void executeFile(String comment, File file) throws Exception {
		System.out.println("executeFile " + comment);
		long len = file.length();
		long re = 0;
		long pre = 0;
		//long lineCounter = 0;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = bufferedReader.readLine();
		mDB.beginTransaction();
		try {
			while (line != null) {
				re = re + line.length() + 2;
				if (pre < re - 100000) {
					logAndPublishProcess(comment + " - " + ((int) (100 * re / len)) + "%");//+pre+"/"+re+"/"+len);
					//System.out.println(pre + " / " + re + " / " + len + " / " + lineCounter + " / " + new java.util.Date());
					pre = re;
					try {
						System.runFinalization();
						Runtime.getRuntime().gc();
						System.gc();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				executeString(line);
				line = bufferedReader.readLine();
				//lineCounter++;
			}
			executeString(line);
			mDB.setTransactionSuccessful();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
		bufferedReader.close();
		//System.out.println("done executeFile " + comment);
	}

	boolean executeString(String sql) {
		if (sql != null) {
			if (sql.length() > 1) {
				try {
					//SQLiteDatabase mDB
					//System.out.println("executeString " + sql.length() + ": " + sql);
					//System.out.println("... " + sql.substring(sql.length() - 150, sql.length()));
					mDB.execSQL(sql.trim());
				} catch (Throwable t) {
					System.out.println("executeString exception for " + sql);
					t.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	boolean executeUpdate(String comment, String url, String entry) {
		System.out.println("executeUpdate " + url);
		logAndPublishProcess(comment);
		try {
			byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			File file = new File("/sdcard/horeca/extract.sql");
			saveFromZip(bytes, entry, file);
			executeFile(comment, file);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
		//System.out.println("done executeUpdate " + url);
		return true;
	}

	void saveFromZip(byte[] bytes, String entry, File file) throws Exception {
		System.out.println("saveFromZip " + entry + " to " + file.getPath());
		//file.createNewFile();
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			//System.out.println("zipEntry " + zipEntry.getName());
			if (zipEntry.getName().equals(entry)) {
				int sz = 9999;
				//ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				byte buffer[] = new byte[sz];
				int le = zipInputStream.read(buffer, 0, sz);
				while (le > -1) {
					//byteArrayOutputStream.write(buffer,0,le);
					fileOutputStream.write(buffer, 0, le);
					le = zipInputStream.read(buffer, 0, sz);
					//System.out.println("read " + le);
				}
				//byteArrayOutputStream.close();
				fileOutputStream.close();
				zipInputStream.close();
				break;
			}
			zipEntry = zipInputStream.getNextEntry();
		}
		//System.out.println("done saveFromZip " + entry + " to " + file.getPath());
	}

	public static void tempAdjustPrice(SQLiteDatabase mDB) {
		//System.out.println("Анализ БД - Прайс");
		mDB.execSQL("	drop table if exists Price_strip;	");
		mDB.execSQL("	CREATE TABLE Price_strip (_id integer primary key asc,[_IDRRef] blob null,[Number] nvarchar(10) null,[Vladelec] blob null,[Nomenklatura] blob null,[Trafik] nvarchar(5) null);	");
		mDB.execSQL("	insert into Price_strip (	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	from Price	"//
				+ "\n	group by _idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	;	");
		//System.out.println("delete from Price;");
		mDB.execSQL("	delete from Price;	");
		System.out.println("insert Price;");
		mDB.execSQL("	insert into Price (	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	) select _idrref,number, vladelec,nomenklatura,trafik from Price_strip" + "\n	;	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_IDRRef on Price_strip(_IDRRef);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Nomenklatura on Price_strip(Nomenklatura);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Number on Price_strip(Number);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Trafik on Price_strip(Trafik);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Vladelec on Price_strip(Vladelec);	");

		mDB.execSQL("	analyze;	");
		System.out.println("done analyze after Price;");
	}

	public static void setupAndStripData(SQLiteDatabase mDB, UpdateTask upd) {
		//if(1==1)return;
		System.out.println("setupStrippedData");
		GPSInfo.lockInsert = true;
		if (upd != null) {
			upd.logAndPublishProcess("Проверка БД");
		}
		//mDB.execSQL("	vacuum;	");
		mDB.execSQL("	analyze;	");
		String sql = "";
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Прайс");
		}
		System.out.println("Анализ БД - Прайс");
		mDB.execSQL("	drop table if exists Price_strip;	");
		mDB.execSQL("	CREATE TABLE Price_strip (_id integer primary key asc,[_IDRRef] blob null,[Number] nvarchar(10) null,[Vladelec] blob null,[Nomenklatura] blob null,[Trafik] nvarchar(5) null);	");
		mDB.execSQL("	insert into Price_strip (	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	from Price	"//
				+ "\n	group by _idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	;	");
		System.out.println("delete from Price;");
		mDB.execSQL("	delete from Price;	");
		mDB.execSQL("	insert into Price (	"//
				+ "\n	_idrref,number, vladelec,nomenklatura,trafik	"//
				+ "\n	) select _idrref,number, vladelec,nomenklatura,trafik from Price_strip" + "\n	;	");
		//String pricestat="select count(price.vladelec),price.vladelec from Price_strip price group by price.vladelec limit 100;";
		//Bough b=Auxiliary.fromCursor(mDB.rawQuery(pricestat, null));
		//System.out.println(b.dumpXML());


		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Величины квантования");
		}
		mDB.execSQL("	CREATE INDEX IX_Price_strip_IDRRef on Price_strip(_IDRRef);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Nomenklatura on Price_strip(Nomenklatura);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Number on Price_strip(Number);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Trafik on Price_strip(Trafik);	");
		mDB.execSQL("	CREATE INDEX IX_Price_strip_Vladelec on Price_strip(Vladelec);	");
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Номенклатура");
		}
		System.out.println("Анализ БД - Номенклатура");
		mDB.execSQL("	drop table if exists Nomenklatura_sorted;	");

		mDB.execSQL("	CREATE TABLE Nomenklatura_sorted (	"
				+ "\n		_id integer primary key asc autoincrement, [_IDRRef] blob null,[_Version] timestamp null,[PometkaUdaleniya] blob null,[Predopredelennyy] blob null	"//
				+ "\n		,[Roditel] blob null,[EtoGruppa] blob null,[Kod] nchar (10) null,[Naimenovanie] nvarchar (160) null,[NaimenovaniePolnoe] text null	"//
				+ "\n		,[Artikul] nvarchar (50) null,[EdinicaKhraneniyaOstatkov] blob null,[BazovayaEdinicaIzmereniya] blob null,[StavkaNDS] blob null,[Kommentariy] text null	"//
				+ "\n		,[Usluga] blob null,[Nabor] blob null,[NomenklaturnayaGruppa] blob null,[NomenklaturnayaGruppaZatrat] blob null,[VesovoyKoephphicientVkhozhdeniya] numeric null	"//
				+ "\n		,[VestiUchetPoKharakteristikam] blob null,[OtvetstvennyyMenedzherZaPokupki] blob null,[OsnovnoyPostavschik] blob null,[StatyaZatrat] blob null	"//
				+ "\n		,[OsnovnoeIzobrazhenie] blob null,[StranaProiskhozhdeniya] blob null,[NomerGTD] blob null,[EdinicaDlyaOtchetov] blob null,[Vesovoy] blob null	"//
				+ "\n		,[PoryadokPriPechatiPraysLista] numeric null,[OsnovnoyProizvoditel] blob null,[Prioritet] numeric null,[MinimalnyyOstatok] numeric null,[VetKategoriya] blob null	"//
				+ "\n		,[ProcentEstestvennoyUbyli] numeric null,[SrokGodnosti] numeric null,[TovarPodZakaz] blob null,[Brend] blob null,[TovarPodZakazKazan] blob null, UpperName text null	"//
				+ "\n		, skladEdIzm text, skladEdVes real, skladEdKoef real, otchEdIzm text, otchdEdVes real, otchEdKoef real, kvant real, product blob null, tegi text, mark blob null, brand blob null"//
				+ "\n	);	");
		//System.out.println("101715 "+Auxiliary.fromCursor(mDB.rawQuery("select n._id,n.[_IDRRef],n.[Artikul] from Nomenklatura_sorted n where n.artikul=101715;",null)).dumpXML());
		sql = "	insert into Nomenklatura_sorted (	"//
				+ "\n			_IDRRef,_Version,PometkaUdaleniya,Predopredelennyy,Roditel,EtoGruppa,Kod,Naimenovanie,NaimenovaniePolnoe,Artikul,EdinicaKhraneniyaOstatkov	"//
				+ "\n			,BazovayaEdinicaIzmereniya,StavkaNDS,Kommentariy,Usluga,Nabor,NomenklaturnayaGruppa,NomenklaturnayaGruppaZatrat	"//
				+ "\n			,VesovoyKoephphicientVkhozhdeniya,VestiUchetPoKharakteristikam,OtvetstvennyyMenedzherZaPokupki,OsnovnoyPostavschik,StatyaZatrat	"//
				+ "\n			,OsnovnoeIzobrazhenie,StranaProiskhozhdeniya,NomerGTD,EdinicaDlyaOtchetov,Vesovoy,PoryadokPriPechatiPraysLista,OsnovnoyProizvoditel	"//
				+ "\n			,Prioritet,MinimalnyyOstatok,VetKategoriya,ProcentEstestvennoyUbyli,SrokGodnosti,TovarPodZakaz,Brend,TovarPodZakazKazan,UpperName	"//
				+ "\n			, skladEdIzm, skladEdVes, skladEdKoef, otchEdIzm, otchdEdVes, otchEdKoef, kvant, product, tegi, mark, brand	"//
				+ "\n			)	"//
				+ "\n		select 	"//
				+ "\n			n._IDRRef, n._Version, n.PometkaUdaleniya, n.Predopredelennyy, n.Roditel, n.EtoGruppa, n.Kod, n.Naimenovanie, n.NaimenovaniePolnoe, n.Artikul, n.EdinicaKhraneniyaOstatkov	"//
				+ "\n			, n.BazovayaEdinicaIzmereniya, n.StavkaNDS, n.Kommentariy, n.Usluga, n.Nabor, n.NomenklaturnayaGruppa, n.NomenklaturnayaGruppaZatrat	"//
				+ "\n			, n.VesovoyKoephphicientVkhozhdeniya, n.VestiUchetPoKharakteristikam, n.OtvetstvennyyMenedzherZaPokupki, n.OsnovnoyPostavschik, n.StatyaZatrat	"//
				+ "\n			, n.OsnovnoeIzobrazhenie, n.StranaProiskhozhdeniya, n.NomerGTD, n.EdinicaDlyaOtchetov, n.Vesovoy, n.PoryadokPriPechatiPraysLista, n.OsnovnoyProizvoditel	"//
				+ "\n			, n.Prioritet, n.MinimalnyyOstatok, n.VetKategoriya, n.ProcentEstestvennoyUbyli, n.SrokGodnosti, n.TovarPodZakaz, n.Brend, n.TovarPodZakazKazan, n.UpperName	"//
				+ "\n			, n.skladEdIzm, n.skladEdVes, n.skladEdKoef, n.otchEdIzm, n.otchdEdVes, n.otchEdKoef, n.kvant, n.product, tegi, mark, brand	"//
				+ "\n		from nomenklatura n	"//
				//+ "\n		join Price on Price.nomenklatura=n._idrref	or n.roditel=X'00000000000000000000000000000000'"//
				//+ "\n		join Price_artikul on Price_artikul.nomenklatura=n._idrref"//
				+ "\n		join AssortimentNaSklade on AssortimentNaSklade.NomenklaturaPostavshhik=n._idrref or AssortimentNaSklade.NomenklaturaPostavshhik=x'00'"//
				+ "\n		where n.EtoGruppa<>x'01'"//
				+ "\n		group by n._idrref	"//
				+ "\n		order by naimenovanie	"//
				+ "\n	;	";
		//System.out.println(sql);
		mDB.execSQL(sql);
		//System.out.println("a 101715 "+Auxiliary.fromCursor(mDB.rawQuery("select n._id,n.[_IDRRef],n.[Artikul] from Nomenklatura_sorted n where n.artikul=101715;",null)).dumpXML());
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Номенклатура группы");
		}
		System.out.println("Анализ БД - Номенклатура группы");
		sql = "	insert into Nomenklatura_sorted (	"//
				+ "\n			_IDRRef,_Version,PometkaUdaleniya,Predopredelennyy,Roditel,EtoGruppa,Kod,Naimenovanie,NaimenovaniePolnoe,Artikul,EdinicaKhraneniyaOstatkov	"//
				+ "\n			,BazovayaEdinicaIzmereniya,StavkaNDS,Kommentariy,Usluga,Nabor,NomenklaturnayaGruppa,NomenklaturnayaGruppaZatrat	"//
				+ "\n			,VesovoyKoephphicientVkhozhdeniya,VestiUchetPoKharakteristikam,OtvetstvennyyMenedzherZaPokupki,OsnovnoyPostavschik,StatyaZatrat	"//
				+ "\n			,OsnovnoeIzobrazhenie,StranaProiskhozhdeniya,NomerGTD,EdinicaDlyaOtchetov,Vesovoy,PoryadokPriPechatiPraysLista,OsnovnoyProizvoditel	"//
				+ "\n			,Prioritet,MinimalnyyOstatok,VetKategoriya,ProcentEstestvennoyUbyli,SrokGodnosti,TovarPodZakaz,Brend,TovarPodZakazKazan,UpperName	"//
				+ "\n			, skladEdIzm, skladEdVes, skladEdKoef, otchEdIzm, otchdEdVes, otchEdKoef, kvant, tegi, mark, brand	"//
				+ "\n			)	"//
				+ "\n		select 	"//
				+ "\n			n._IDRRef, n._Version, n.PometkaUdaleniya, n.Predopredelennyy, n.Roditel, n.EtoGruppa, n.Kod, n.Naimenovanie, n.NaimenovaniePolnoe, n.Artikul, n.EdinicaKhraneniyaOstatkov	"//
				+ "\n			, n.BazovayaEdinicaIzmereniya, n.StavkaNDS, n.Kommentariy, n.Usluga, n.Nabor, n.NomenklaturnayaGruppa, n.NomenklaturnayaGruppaZatrat	"//
				+ "\n			, n.VesovoyKoephphicientVkhozhdeniya, n.VestiUchetPoKharakteristikam, n.OtvetstvennyyMenedzherZaPokupki, n.OsnovnoyPostavschik, n.StatyaZatrat	"//
				+ "\n			, n.OsnovnoeIzobrazhenie, n.StranaProiskhozhdeniya, n.NomerGTD, n.EdinicaDlyaOtchetov, n.Vesovoy, n.PoryadokPriPechatiPraysLista, n.OsnovnoyProizvoditel	"//
				+ "\n			, n.Prioritet, n.MinimalnyyOstatok, n.VetKategoriya, n.ProcentEstestvennoyUbyli, n.SrokGodnosti, n.TovarPodZakaz, n.Brend, n.TovarPodZakazKazan, n.UpperName	"//
				+ "\n			, n.skladEdIzm, n.skladEdVes, n.skladEdKoef, n.otchEdIzm, n.otchdEdVes, n.otchEdKoef, n.kvant, tegi, mark, brand	"//
				+ "\n		from nomenklatura n	"//
				+ "\n		where n.EtoGruppa=x'01'"//
				+ "\n		order by n.naimenovanie	"//
				+ "\n	;	";
		//System.out.println(sql);
		mDB.execSQL(sql);
        /*
        sql = "	insert into Nomenklatura_sorted (	"//
                + "\n			_IDRRef,_Version,PometkaUdaleniya,Predopredelennyy,Roditel,EtoGruppa,Kod,Naimenovanie,NaimenovaniePolnoe,Artikul,EdinicaKhraneniyaOstatkov	"//
                + "\n			,BazovayaEdinicaIzmereniya,StavkaNDS,Kommentariy,Usluga,Nabor,NomenklaturnayaGruppa,NomenklaturnayaGruppaZatrat	"//
                + "\n			,VesovoyKoephphicientVkhozhdeniya,VestiUchetPoKharakteristikam,OtvetstvennyyMenedzherZaPokupki,OsnovnoyPostavschik,StatyaZatrat	"//
                + "\n			,OsnovnoeIzobrazhenie,StranaProiskhozhdeniya,NomerGTD,EdinicaDlyaOtchetov,Vesovoy,PoryadokPriPechatiPraysLista,OsnovnoyProizvoditel	"//
                + "\n			,Prioritet,MinimalnyyOstatok,VetKategoriya,ProcentEstestvennoyUbyli,SrokGodnosti,TovarPodZakaz,Brend,TovarPodZakazKazan,UpperName	"//
                + "\n			, skladEdIzm, skladEdVes, skladEdKoef, otchEdIzm, otchdEdVes, otchEdKoef, kvant	"//
                + "\n			)	"//
                + "\n		select 	"//
                + "\n			n._IDRRef, n._Version, n.PometkaUdaleniya, n.Predopredelennyy, n.Roditel, n.EtoGruppa, n.Kod, n.Naimenovanie, n.NaimenovaniePolnoe, n.Artikul, n.EdinicaKhraneniyaOstatkov	"//
                + "\n			, n.BazovayaEdinicaIzmereniya, n.StavkaNDS, n.Kommentariy, n.Usluga, n.Nabor, n.NomenklaturnayaGruppa, n.NomenklaturnayaGruppaZatrat	"//
                + "\n			, n.VesovoyKoephphicientVkhozhdeniya, n.VestiUchetPoKharakteristikam, n.OtvetstvennyyMenedzherZaPokupki, n.OsnovnoyPostavschik, n.StatyaZatrat	"//
                + "\n			, n.OsnovnoeIzobrazhenie, n.StranaProiskhozhdeniya, n.NomerGTD, n.EdinicaDlyaOtchetov, n.Vesovoy, n.PoryadokPriPechatiPraysLista, n.OsnovnoyProizvoditel	"//
                + "\n			, n.Prioritet, n.MinimalnyyOstatok, n.VetKategoriya, n.ProcentEstestvennoyUbyli, n.SrokGodnosti, n.TovarPodZakaz, n.Brend, n.TovarPodZakazKazan, n.UpperName	"//
                + "\n			, n.skladEdIzm, n.skladEdVes, n.skladEdKoef, n.otchEdIzm, n.otchdEdVes, n.otchEdKoef, n.kvant	"//
                + "\n		from nomenklatura n	"//
                + "\n		where (n.roditel=X'00000000000000000000000000000000' or n.roditel=X'00')"//
                //+ "\n		join Price on Price.nomenklatura=n._idrref"//
                //+ "\n		group by n._idrref	"//
                //+ "\n		order by n.naimenovanie	"//
                + "\n	;	";
        //System.out.println(sql);
        mDB.execSQL(sql);
		//System.out.println("b 101715 "+Auxiliary.fromCursor(mDB.rawQuery("select n._id,n.[_IDRRef],n.[Artikul] from Nomenklatura_sorted n where n.artikul=101715;",null)).dumpXML());
        if (upd != null) {
            upd.logAndPublishProcess("Анализ БД - Номенклатура подгруппы");
        }
        System.out.println("Анализ БД - Номенклатура подгруппы");
        sql = "	insert into Nomenklatura_sorted (	"//
                + "\n			_IDRRef,_Version,PometkaUdaleniya,Predopredelennyy,Roditel,EtoGruppa,Kod,Naimenovanie,NaimenovaniePolnoe,Artikul,EdinicaKhraneniyaOstatkov	"//
                + "\n			,BazovayaEdinicaIzmereniya,StavkaNDS,Kommentariy,Usluga,Nabor,NomenklaturnayaGruppa,NomenklaturnayaGruppaZatrat	"//
                + "\n			,VesovoyKoephphicientVkhozhdeniya,VestiUchetPoKharakteristikam,OtvetstvennyyMenedzherZaPokupki,OsnovnoyPostavschik,StatyaZatrat	"//
                + "\n			,OsnovnoeIzobrazhenie,StranaProiskhozhdeniya,NomerGTD,EdinicaDlyaOtchetov,Vesovoy,PoryadokPriPechatiPraysLista,OsnovnoyProizvoditel	"//
                + "\n			,Prioritet,MinimalnyyOstatok,VetKategoriya,ProcentEstestvennoyUbyli,SrokGodnosti,TovarPodZakaz,Brend,TovarPodZakazKazan,UpperName	"//
                + "\n			, skladEdIzm, skladEdVes, skladEdKoef, otchEdIzm, otchdEdVes, otchEdKoef, kvant	"//
                + "\n			)	"//
                + "\n		select 	"//
                + "\n			n._IDRRef, n._Version, n.PometkaUdaleniya, n.Predopredelennyy, n.Roditel, n.EtoGruppa, n.Kod, n.Naimenovanie, n.NaimenovaniePolnoe, n.Artikul, n.EdinicaKhraneniyaOstatkov	"//
                + "\n			, n.BazovayaEdinicaIzmereniya, n.StavkaNDS, n.Kommentariy, n.Usluga, n.Nabor, n.NomenklaturnayaGruppa, n.NomenklaturnayaGruppaZatrat	"//
                + "\n			, n.VesovoyKoephphicientVkhozhdeniya, n.VestiUchetPoKharakteristikam, n.OtvetstvennyyMenedzherZaPokupki, n.OsnovnoyPostavschik, n.StatyaZatrat	"//
                + "\n			, n.OsnovnoeIzobrazhenie, n.StranaProiskhozhdeniya, n.NomerGTD, n.EdinicaDlyaOtchetov, n.Vesovoy, n.PoryadokPriPechatiPraysLista, n.OsnovnoyProizvoditel	"//
                + "\n			, n.Prioritet, n.MinimalnyyOstatok, n.VetKategoriya, n.ProcentEstestvennoyUbyli, n.SrokGodnosti, n.TovarPodZakaz, n.Brend, n.TovarPodZakazKazan, n.UpperName	"//
                + "\n			, n.skladEdIzm, n.skladEdVes, n.skladEdKoef, n.otchEdIzm, n.otchdEdVes, n.otchEdKoef, n.kvant	"//
                + "\n		from nomenklatura n	"//
                + "\n		join nomenklatura nr on n.roditel=nr._idrref	"//
                + "\n		where nr.roditel=X'00000000000000000000000000000000' or nr.roditel=X'00'"//
                //+ "\n		join Price on Price.nomenklatura=n._idrref"//
                //+ "\n		group by n._idrref	"//
                //+ "\n		order by n.naimenovanie	"//
                + "\n	;	";
        //System.out.println(sql);
        mDB.execSQL(sql);
		//System.out.println("c 101715 "+Auxiliary.fromCursor(mDB.rawQuery("select n._id,n.[_IDRRef],n.[Artikul] from Nomenklatura_sorted n where n.artikul=101715;",null)).dumpXML());

		mDB.execSQL("delete from Nomenklatura_sorted where _id in (select _id from Nomenklatura_sorted n group by n._idrref having count(_idrref)>1);");
*/
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Номенклатура");
		}
		mDB.execSQL("	CREATE INDEX IX_Nomenklatura_sorted on Nomenklatura_sorted(_IDRRef);	");
		mDB.execSQL("	CREATE INDEX IX_Nomenklatura_sorted_Artikul on Nomenklatura_sorted(Artikul);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_EdenicaKhraneniyaOstatkov] ON Nomenklatura_sorted([EdinicaKhraneniyaOstatkov]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_EdinicaDlyaOtchetov] ON Nomenklatura_sorted([EdinicaDlyaOtchetov]);	");
		mDB.execSQL("	CREATE INDEX IX_Nomenklatura_sorted_IDRref on Nomenklatura_sorted(_IDRRef);	");
		mDB.execSQL("	CREATE INDEX IX_Nomenklatura_sorted_Roditel on Nomenklatura_sorted(Roditel);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_OsnovnoyProizvoditel] ON Nomenklatura_sorted([OsnovnoyProizvoditel]);	");

		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_skladEdIzm] ON Nomenklatura_sorted([skladEdIzm]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_skladEdVes] ON Nomenklatura_sorted([skladEdVes]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_skladEdKoef] ON Nomenklatura_sorted([skladEdKoef]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_otchEdIzm] ON Nomenklatura_sorted([otchEdIzm]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_otchdEdVes] ON Nomenklatura_sorted([otchdEdVes]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_otchEdKoef] ON Nomenklatura_sorted([otchEdKoef]);	");
		mDB.execSQL("	CREATE INDEX [IX_Nomenklatura_sorted_kvant] ON Nomenklatura_sorted([kvant]);	");

		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Адреса по складам");
		}
		System.out.println("Анализ БД - Адреса по складам");
		mDB.execSQL("	drop table if exists AdresaPoSkladam_last;	");
		mDB.execSQL("	CREATE TABLE AdresaPoSkladam_last (	" //
				+ "\n	_id integer primary key asc autoincrement, [Period] date null,[Baza] blob null,[Nomenklatura] blob null,[Sklad] blob null,[Traphik] blob null	"//
				+ "\n	);	");
		mDB.execSQL("	insert into AdresaPoSkladam_last (	"//
				+ "\n			Period,Baza,Nomenklatura,Sklad,Traphik	"//
				+ "\n		)	"//
				+ "\n		select 	"//
				+ "\n			Period,Baza,Nomenklatura,Sklad,Traphik	"//
				+ "\n		from AdresaPoSkladam a1	"//
				+ "\n			where a1.period=(select max(period) from AdresaPoSkladam a2	"//
				+ "\n				where a1.nomenklatura=a2.nomenklatura	"//
				+ "\n					and a1.baza=a2.baza	"//
				+ "\n				) and a1.sklad<>X'00000000000000000000000000000000'	 and a1.sklad<>X'00'	"//
				+ "\n		group by a1.baza,a1.sklad,a1.nomenklatura	"//
				+ "\n	;	");
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Адреса по складам");
		}
		mDB.execSQL("	CREATE INDEX IX_AdresaPoSkladam_last_Period on AdresaPoSkladam_last (Period);	");
		mDB.execSQL("	CREATE INDEX IX_AdresaPoSkladam_last_Nomenklatura on AdresaPoSkladam_last (Nomenklatura);	");
		mDB.execSQL("	CREATE INDEX IX_AdresaPoSkladam_last_Baza on AdresaPoSkladam_last (Baza);	");
		mDB.execSQL("	CREATE INDEX IX_AdresaPoSkladam_last_Sklad on AdresaPoSkladam_last (Sklad);	");
		mDB.execSQL("	CREATE INDEX IX_AdresaPoSkladam_last_Traphik on AdresaPoSkladam_last (Traphik);	");
		//////////////////
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Договоры контрагентов");
		}
		refreshDogovoryKontragentov_strip(mDB);
		/*
		System.out.println("Анализ БД - Договоры контрагентов");
		mDB.execSQL("	drop table if exists DogovoryKontragentov_strip;	");
		mDB.execSQL("	CREATE TABLE DogovoryKontragentov_strip (	" //
				+ "\n	_id integer primary key autoincrement, [_IDRRef] blob null,[_Version] timestamp null,[PometkaUdaleniya] blob null,[Predopredelennyy] blob null,[Vladelec] blob null,[Roditel] blob null,[EtoGruppa] blob null,[Kod] nchar (10) null,[Naimenovanie] nvarchar (150) null,[Data] date null,[Nomer] nvarchar (20) null,[ValyutaVzaimoraschetov] blob null,[VedenieVzaimoraschetov] blob null,[VidVzaimoraschetov] blob null,[VidUsloviyDogovora] blob null,[DerzhatRezervBezOplatyOgranichennoeVremya] blob null,[DopustimayaSummaZadolzhennosti] numeric null,[DopustimoeChisloDneyZadolzhennosti] numeric null,[Kommentariy] text null,[KontrolirovatSummuZadolzhennosti] blob null,[KontrolirovatChisloDneyZadolzhennosti] blob null,[ObosoblennyyUchetTovarovPoZakazamPokupateley] blob null,[Organizaciya] blob null,[ProcentKomissionnogoVoznagrazhdeniya] numeric null,[ProcentPredoplaty] numeric null,[SposobRaschetaKomissionnogoVoznagrazhdeniya] blob null,[UdalitTipSkidkiNacenki] blob null,[TipCen_0] blob null,[TipCen_1] blob null,[TipCen_2] blob null,[ChisloDneyRezervaBezOplaty] numeric null,[VidDogovora] blob null,[UchetAgentskogoNDS] blob null,[VidAgentskogoDogovora] blob null,[KontrolirovatDenezhnyeSredstvaKomitenta] blob null,[RaschetyVUslovnykhEdinicakh] blob null,[Podrazdelenie] blob null,[GruppaDogovorov] blob null,[DataOkonchaniya] date null,[Zakryt] blob null,[ZadolzhennostVBankDnyakh] blob null,[ReytingPoOplate] numeric null,[BespalletnayaPostavka] blob null,[Khoreka_VidOplaty] blob null,[OtdelnayaPoslednyayaOplachennayaNakladnaya] blob null,[PhormaDogovora] nvarchar (100) null,[Dostavka] nvarchar (200) null	"//
				+ "\n	);	");
		sql = "	insert into DogovoryKontragentov_strip (	"//
				+ "\n [_IDRRef],[_Version],[PometkaUdaleniya],[Predopredelennyy],[Vladelec],[Roditel],[EtoGruppa] "//
				+ " ,[Kod],[Naimenovanie],[Data],[Nomer],[ValyutaVzaimoraschetov],[VedenieVzaimoraschetov] "//
				+ " ,[VidVzaimoraschetov],[VidUsloviyDogovora],[DerzhatRezervBezOplatyOgranichennoeVremya] "//
				+ " ,[DopustimayaSummaZadolzhennosti],[DopustimoeChisloDneyZadolzhennosti],[Kommentariy] "//
				+ " ,[KontrolirovatSummuZadolzhennosti],[KontrolirovatChisloDneyZadolzhennosti],[ObosoblennyyUchetTovarovPoZakazamPokupateley] "//
				+ " ,[Organizaciya],[ProcentKomissionnogoVoznagrazhdeniya],[ProcentPredoplaty] "//
				+ " ,[SposobRaschetaKomissionnogoVoznagrazhdeniya],[UdalitTipSkidkiNacenki],[TipCen_0] "//
				+ " ,[TipCen_1],[TipCen_2],[ChisloDneyRezervaBezOplaty],[VidDogovora] "//
				+ " ,[UchetAgentskogoNDS],[VidAgentskogoDogovora],[KontrolirovatDenezhnyeSredstvaKomitenta] "//
				+ " ,[RaschetyVUslovnykhEdinicakh],[Podrazdelenie],[GruppaDogovorov],[DataOkonchaniya] "//
				+ " ,[Zakryt],[ZadolzhennostVBankDnyakh],[ReytingPoOplate],[BespalletnayaPostavka] "//
				+ " ,[Khoreka_VidOplaty],[OtdelnayaPoslednyayaOplachennayaNakladnaya],[PhormaDogovora]  "//
				+ " ,[Dostavka] "//
				+ "\n		)	"//
				+ "\n		select "//
				+ "\n		d2.[_IDRRef],d2.[_Version],d2.[PometkaUdaleniya],d2.[Predopredelennyy],d2.[Vladelec],d2.[Roditel],d2.[EtoGruppa],d2.[Kod],d2.[Naimenovanie],d2.[Data],d2.[Nomer] "//
				+ "\n		,d2.[ValyutaVzaimoraschetov],d2.[VedenieVzaimoraschetov],d2.[VidVzaimoraschetov],d2.[VidUsloviyDogovora],d2.[DerzhatRezervBezOplatyOgranichennoeVremya] "//
				+ "\n		,d2.[DopustimayaSummaZadolzhennosti],d2.[DopustimoeChisloDneyZadolzhennosti],d2.[Kommentariy],d2.[KontrolirovatSummuZadolzhennosti] "//
				+ "\n		,d2.[KontrolirovatChisloDneyZadolzhennosti],d2.[ObosoblennyyUchetTovarovPoZakazamPokupateley],d2.[Organizaciya],d2.[ProcentKomissionnogoVoznagrazhdeniya] "//
				+ "\n		,d2.[ProcentPredoplaty],d2.[SposobRaschetaKomissionnogoVoznagrazhdeniya],d2.[UdalitTipSkidkiNacenki],d2.[TipCen_0],d2.[TipCen_1],d2.[TipCen_2] "//
				+ "\n		,d2.[ChisloDneyRezervaBezOplaty],d2.[VidDogovora],d2.[UchetAgentskogoNDS],d2.[VidAgentskogoDogovora],d2.[KontrolirovatDenezhnyeSredstvaKomitenta] "//
				+ "\n		,d2.[RaschetyVUslovnykhEdinicakh],d2.[Podrazdelenie],d2.[GruppaDogovorov],d2.[DataOkonchaniya],d2.[Zakryt],d2.[ZadolzhennostVBankDnyakh],d2.[ReytingPoOplate] "//
				+ "\n		,d2.[BespalletnayaPostavka],d2.[Khoreka_VidOplaty],d2.[OtdelnayaPoslednyayaOplachennayaNakladnaya],d2.[PhormaDogovora],d2.[Dostavka] "//
				+ "\n		from DogovoryKontragentov d2 "//
				+ "\n		join ( "//
				+ "\n			select k2._idrref as id "//
				+ "\n				from MarshrutyAgentov m "//
				+ "\n					join kontragenty k1 on k1._idrref=m.kontragent "//
				+ "\n					join DogovoryKontragentov d1 on d1.vladelec=k1._idrref "//
				+ "\n					join GruppyDogovorov g on d1.gruppadogovorov=g._idrref "//
				+ "\n					join DogovoryKontragentov d2 on d2.gruppadogovorov=g._idrref "//
				+ "\n					join kontragenty k2 on d2.vladelec=k2._idrref "//
				+ "\n				where d1.gruppadogovorov<>X'00000000000000000000000000000000' and d1.gruppadogovorov<>X'00' "//
				+ "\n			union "//
				+ "\n			select k1._idrref as id "//
				+ "\n				from MarshrutyAgentov m "//
				+ "\n					join kontragenty k1 on k1._idrref=m.kontragent "//
				+ "\n					join DogovoryKontragentov d1 on d1.vladelec=k1._idrref "//
				+ "\n				where d1.gruppadogovorov=X'00000000000000000000000000000000' or d1.gruppadogovorov=X'00' "//
				+ "\n			group by id "//
				+ "\n			) k on d2.vladelec=k.id "//
				//+ "\n			where d2.[PometkaUdaleniya]=x'00'"
				+ "\n	;	";
		//System.out.println(sql);
		mDB.execSQL(sql);
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Договоры контрагентов");
		}
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_id on DogovoryKontragentov_strip(_id);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_idrref on DogovoryKontragentov_strip(_idrref);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_vladelec on DogovoryKontragentov_strip(vladelec);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_roditel on DogovoryKontragentov_strip(roditel);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_zakryt on DogovoryKontragentov_strip(zakryt);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_GruppaDogovorov on DogovoryKontragentov_strip(GruppaDogovorov);	");
		*/
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Цены номенклатуры склада");
		}
		System.out.println("Анализ БД - Цены номенклатуры склада");
		mDB.execSQL("	drop table if exists CenyNomenklaturySklada_last;	");
		mDB.execSQL("	CREATE TABLE CenyNomenklaturySklada_last (	"//
				+ "\n	_id integer primary key asc autoincrement,[Period] date null,[Registrator] blob null,[NomerStroki] numeric null,[Aktivnost] blob null	"//
				+ "\n	,[TipCen] blob null,[Nomenklatura] blob null,[Sklad] blob null,[Valyuta] blob null,[Cena] numeric null,[EdinicaIzmereniya] blob null	"//
				+ "\n	,[ProcentSkidkiNacenki] numeric null	"//
				+ "\n	);	");
		mDB.execSQL("	insert into CenyNomenklaturySklada_last (	"//
				+ "\n	Period,Registrator,NomerStroki,Aktivnost,TipCen,Nomenklatura,Sklad,Valyuta,Cena,EdinicaIzmereniya,ProcentSkidkiNacenki	"//
				+ "\n	)	"//
				+ "\n	select 	"//
				+ "\n	Period,Registrator,NomerStroki,Aktivnost,TipCen,Nomenklatura,Sklad,Valyuta,Cena,EdinicaIzmereniya,ProcentSkidkiNacenki	"//
				+ "\n	from CenyNomenklaturySklada c1	"//
				+ "\n	join Nomenklatura_sorted on Nomenklatura_sorted._idrref=c1.nomenklatura	"//
				+ "\n	where c1.Period=(select max(c2.Period) from CenyNomenklaturySklada c2 where c1.nomenklatura=c2.nomenklatura)	"//
				+ "\n	group by nomenklatura	"//
				+ "\n	;	");
		mDB.execSQL("	insert into CenyNomenklaturySklada_last (	"//
				+ "\n	Period,Registrator,NomerStroki,Aktivnost,TipCen,Nomenklatura,Sklad,Valyuta,Cena,EdinicaIzmereniya,ProcentSkidkiNacenki	"//
				+ "\n	)	"//
				+ "\n	select 	"//
				+ "\n	Period,Registrator,NomerStroki,Aktivnost,TipCen,Nomenklatura,Sklad,Valyuta,Cena,EdinicaIzmereniya,ProcentSkidkiNacenki	"//
				+ "\n	from CenyNomenklaturySklada c1	"//
				+ "\n	join Nomenklatura_sorted on Nomenklatura_sorted._idrref=c1.nomenklatura	"//
				+ "\n	where date(c1.period) >= date('now')	"//
				+ "\n	group by nomenklatura	"//
				+ "\n	;	");
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Цены номенклатуры склада");
		}
		mDB.execSQL("	CREATE INDEX IX_CenyNomenklaturySklada_last_Nomenklatura on CenyNomenklaturySklada_last(Nomenklatura);	");
		mDB.execSQL("	CREATE INDEX IX_CenyNomenklaturySklada_last_Period on CenyNomenklaturySklada_last(Period);	");
		mDB.execSQL("	CREATE INDEX IX_CenyNomenklaturySklada_last_Cena on CenyNomenklaturySklada_last(Cena);	");
		/*if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Единицы измерения");
		}
		System.out.println("Анализ БД - Единицы измерения");*/
		mDB.execSQL("	drop table if exists EdinicyIzmereniya_strip;	");
		/*
		mDB.execSQL("	CREATE TABLE EdinicyIzmereniya_strip (	"//
				+ "\n	_id integer primary key asc autoincrement, [_IDRRef] blob null,[_Version] timestamp null,[PometkaUdaleniya] blob null	"//
				+ "\n	,[Predopredelennyy] blob null,[Vladelec_0] blob null,[Vladelec_1] blob null,[Vladelec_2] blob null,[Kod] nchar (12) null	"//
				+ "\n	,[Naimenovanie] nvarchar (50) null,[EdinicaPoKlassiphikatoru] blob null,[Ves] numeric null,[Obem] numeric null,[DolyaPallety] numeric null	"//
				+ "\n	,[Koephphicient] numeric null,[DolyaPalletyDlyaOtgruzki] numeric null	"//
				+ "\n	);	");
		mDB.execSQL("	insert into EdinicyIzmereniya_strip (	"//
				+ "\n	_IDRRef,_Version,PometkaUdaleniya	"//
				+ "\n	,Predopredelennyy,Vladelec_0,Vladelec_1,Vladelec_2,Kod,Naimenovanie,EdinicaPoKlassiphikatoru	"//
				+ "\n	,Ves,Obem,DolyaPallety,Koephphicient,DolyaPalletyDlyaOtgruzki	"//
				+ "\n	)	"//
				+ "\n	select 	"//
				+ "\n	ediz._IDRRef,ediz._Version,ediz.PometkaUdaleniya	"//
				+ "\n	,ediz.Predopredelennyy,ediz.Vladelec_0,ediz.Vladelec_1,ediz.Vladelec_2,ediz.Kod,ediz.Naimenovanie,ediz.EdinicaPoKlassiphikatoru	"//
				+ "\n	,ediz.Ves,ediz.Obem,ediz.DolyaPallety,ediz.Koephphicient,ediz.DolyaPalletyDlyaOtgruzki	"//
				+ "\n	from EdinicyIzmereniya ediz	"//
				+ "\n	join nomenklatura_sorted n on n.EdinicaDlyaOtchetov=ediz._idrref or n.EdinicaKhraneniyaOstatkov=ediz._idrref	"//
				+ "\n	group by ediz._idrref	"//
				+ "\n	;	");
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Единицы измерения");
		}
		mDB.execSQL("	CREATE INDEX IX_EdinicyIzmereniya_strip_IDRRef on EdinicyIzmereniya_strip(_IDRRef);	");
		*/

		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Текущие цены остатков партий");
		}
		System.out.println("Анализ БД - Текущие цены остатков партий");
		mDB.execSQL("	drop table if exists TekuschieCenyOstatkovPartiy_strip;	");
		mDB.execSQL("	CREATE TABLE TekuschieCenyOstatkovPartiy_strip (	"//
				+ "\n	_id integer primary key asc autoincrement, [Nomenklatura] blob null,[Cena] numeric null,[UstanavlivaetsyaVruchnuyu] blob null	"//
				+ "\n	);	");
		mDB.execSQL("	insert into TekuschieCenyOstatkovPartiy_strip (	"//
				+ "\n	Nomenklatura,Cena,UstanavlivaetsyaVruchnuyu	"//
				+ "\n	)	"//
				+ "\n	select 	"//
				+ "\n	Nomenklatura,Cena,UstanavlivaetsyaVruchnuyu	"//
				+ "\n	from TekuschieCenyOstatkovPartiy	"//
				+ "\n	join Nomenklatura_sorted n on n._idrref=TekuschieCenyOstatkovPartiy.nomenklatura	"//
				+ "\n	group by n._idrref	"//
				+ "\n	;	");
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Текущие цены остатков партий");
		}
		mDB.execSQL("	CREATE INDEX IX_TekuschieCenyOstatkovPartiy_strip_Cena on TekuschieCenyOstatkovPartiy_strip(Cena);	");
		mDB.execSQL("	CREATE INDEX IX_TekuschieCenyOstatkovPartiy_strip_Nomenklatura on TekuschieCenyOstatkovPartiy_strip(nomenklatura);	");
		/*if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Величины квантования");
		}
		System.out.println("Анализ БД - Величины квантования");*/
		mDB.execSQL("	drop table if exists VelichinaKvantovNomenklatury_strip;	");
		/*
		mDB.execSQL("	CREATE TABLE VelichinaKvantovNomenklatury_strip (	"//
				+ "\n	_id integer primary key asc autoincrement, [Nomenklatura] blob null,[Kvant] blob null,[Sklad] blob null,[Kolichestvo] numeric null,[_SimpleKey] blob null	"//
				+ "\n	);	");
		mDB.execSQL("	insert into VelichinaKvantovNomenklatury_strip (	"//
				+ "\n	Nomenklatura,Kvant,Sklad,Kolichestvo,_SimpleKey	"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n	Nomenklatura,Kvant,Sklad,Kolichestvo,_SimpleKey	"//
				+ "\n	from VelichinaKvantovNomenklatury	"//
				+ "\n	join nomenklatura_sorted on nomenklatura_sorted._idrref=VelichinaKvantovNomenklatury.Nomenklatura	"//
				+ "\n	group by VelichinaKvantovNomenklatury.Nomenklatura	"//
				+ "\n	;	");
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Величины квантования");
		}
		mDB.execSQL("	CREATE INDEX [IX_VelichinaKvantovNomenklatury_strip_Nomenklatura] ON [VelichinaKvantovNomenklatury_strip] ([Nomenklatura]);	");
		*/
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - категории номенклатуры");
		}
		System.out.println("Анализ БД - категории номенклатуры");
		Cfg.refreshNomenklatureGroups(mDB);

		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Фикс. цены");
		}
		System.out.println("Анализ БД - Фикс. цены");
		mDB.execSQL("	drop table if exists FiksirovannyeCeny_actual;	");
		mDB.execSQL("	CREATE TABLE FiksirovannyeCeny_actual (	"//
				+ "\n	_id integer primary key asc,[Period] date null,[Registrator] blob null"//
				+ "\n	,[PoluchatelSkidki] blob null,[Nomenklatura] blob null,[FixCena] numeric null"//
				+ "\n	,[DataOkonchaniya] date null,[Obyazatelstva] numeric null	"//
				+ "\n	);	");
		/*
		mDB.execSQL("	insert into FiksirovannyeCeny_actual (Period,Registrator,PoluchatelSkidki,Nomenklatura,FixCena,DataOkonchaniya,Obyazatelstva)"//
				+ "\n		select Period,Registrator,PoluchatelSkidki,Nomenklatura,FixCena,DataOkonchaniya,Obyazatelstva"//
				+ "\n			from FiksirovannyeCeny"//
				+ "\n			join marshrutyagentov on marshrutyagentov.kontragent=FiksirovannyeCeny.poluchatelskidki"//
				+ "\n			where dataokonchaniya>=date('now') and period<=date('now') "//	
				+ "\n			group by PoluchatelSkidki,Nomenklatura"//
				+ "\n	;	");
		*/


		System.out.println("clear Skidki prilojenie,smartpro,internet");
		mDB.execSQL("delete from skidki where polzovatel=X'80610050568B3C6811E851EC0C38C23A'");
		mDB.execSQL("delete from skidki where polzovatel=X'BBC320677C60FED011ECB65846ED9A78'");
		mDB.execSQL("delete from skidki where polzovatel=X'8215002264FA89D811E111E6B52DD7BA'");




		String cuHRC = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().toUpperCase();
		if (!cuHRC.equals("HRC00")) {
			mDB.execSQL("	insert into FiksirovannyeCeny_actual (Period,Registrator,PoluchatelSkidki,Nomenklatura,FixCena,DataOkonchaniya,Obyazatelstva)"//
					+ "\n		select Period,Registrator,PoluchatelSkidki,Nomenklatura,FixCena,DataOkonchaniya,Obyazatelstva"//
					+ "\n				from kontragenty"//
					+ "\n					join marshrutyagentov on marshrutyagentov.kontragent=kontragenty._idrref"//
					+ "\n					join FiksirovannyeCeny on kontragenty._idrref=FiksirovannyeCeny.poluchatelskidki or kontragenty.GolovnoyKontragent=FiksirovannyeCeny.poluchatelskidki"//
					+ "\n					join nomenklatura n on n._idrref=FiksirovannyeCeny.nomenklatura"//
					+ "\n				where dataokonchaniya>=date('now') and period<=date('now')"//
					+ "\n				group by PoluchatelSkidki,Nomenklatura"//
					+ "\n	;	");
		} else {
			if (upd != null) {
				upd.logAndPublishProcess("skip for hrc00 Анализ БД - Фикс. цены");
			}
		}
		if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Фикс. цены");
		}
		mDB.execSQL("	CREATE INDEX [IX_FiksirovannyeCeny_actual_Period] ON [FiksirovannyeCeny_actual] ([Period]);	");
		mDB.execSQL("	CREATE INDEX [IX_FiksirovannyeCeny_actual_PoluchatelSkidki] ON [FiksirovannyeCeny_actual] ([PoluchatelSkidki]);	");
		mDB.execSQL("	CREATE INDEX [IX_FiksirovannyeCeny_actual_Nomenklatura] ON [FiksirovannyeCeny_actual] ([Nomenklatura]);	");
		mDB.execSQL("	CREATE INDEX [IX_FiksirovannyeCeny_actual_DataOkonchaniya] ON [FiksirovannyeCeny_actual] ([DataOkonchaniya]);	");
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД - Продажи");
		}
		refreshProdazhi_last(mDB);


		System.out.println("noVacuum " + Activity_Login.noVacuum);
		if (!Activity_Login.noVacuum) {
			if (upd != null) {
				upd.logAndPublishProcess("Сжатие БД");
			}
			System.out.println("Сжатие БД");
			mDB.execSQL("	vacuum;	");
		}
		if (upd != null) {
			upd.logAndPublishProcess("Анализ БД");
		}
		System.out.println("Анализ БД");
		mDB.execSQL("	analyze;	");
		if (upd != null) {
			upd.logAndPublishProcess("Завершение проверки БД");
		}
		System.out.println("Завершение проверки БД");
		//System.out.println("strip done");
		GPSInfo.lockInsert = false;
	}



	public static void refreshProdazhi_CR(SQLiteDatabase mDB, String kontragentID) {
		System.out.println("Анализ БД - Продажи ЦР " + kontragentID);
		mDB.execSQL("	drop table if exists Prodazhi_CR;	");
		mDB.execSQL("	CREATE TABLE Prodazhi_CR (	"//
				+ "\n	_id integer primary key asc autoincrement, [Period] date null,[Registrator_0] blob null,[Registrator_1] blob null,[NomerStroki] numeric null	"//
				+ "\n	,[Aktivnost] blob null,[Nomenklatura] blob null,[KharakteristikaNomenklatury] blob null,[ZakazPokupatelya_0] blob null,[ZakazPokupatelya_1] blob null	"//
				+ "\n	,[ZakazPokupatelya_2] blob null,[DogovorKontragenta] blob null,[DokumentProdazhi_0] blob null,[DokumentProdazhi_1] blob null	"//
				+ "\n	,[DokumentProdazhi_2] blob null,[Proekt] blob null,[Podrazdelenie] blob null,[Kolichestvo] numeric null,[Stoimost] numeric null	"//
				+ "\n	,[StoimostBezSkidok] numeric null,[Sebestoimost] numeric null,[SummaVozvrataNacenki] numeric null	"//
				+ "\n	,[kartaKod] text null,[VidSkidki] blob null"//
				+ "\n	);	");
		String sql = "	insert into Prodazhi_CR (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,kartaKod,VidSkidki"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		max(Period) as Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,main.Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,'',main.VidSkidki"//
				+ "\n	from Prodazhi main"//
				+ "\n		join dogovorykontragentov_strip d1 on d1._idrref=main.dogovorkontragenta "//
				//+ "\n				and (ifnull(main.VidSkidki,x'00') = x'"+Cfg.skidkaIdCenovoyeReagirovanie+"' or ifnull(main.VidSkidki,x'00') = x'"+Cfg.skidkaIdAutoReagirovanie+"')"
				+ "\n				and d1.vladelec = " + kontragentID + ""
				+ "\n		group by d1.vladelec,main.nomenklatura"//
				+ "\n		order by main.period desc"//
				+ "\n		limit 50000"//
				+ "\n	;	";
		//System.out.println("Prodazhi_CR " + sql);
		mDB.execSQL(sql);
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_CR_Nomenklatura] ON [Prodazhi_CR] ([Nomenklatura]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_CR_Period] ON [Prodazhi_CR] ([Period]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_CR_Stoimost] ON [Prodazhi_CR] ([Stoimost]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_CR_kolichestvo] ON [Prodazhi_CR] ([kolichestvo]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_CR_Dogovorkontragenta ] ON [Prodazhi_CR] ([DogovorKontragenta]);	");

	}

	public static void refreshProdazhi_last(SQLiteDatabase mDB) {
		System.out.println("Анализ БД - Продажи");
		mDB.execSQL("	drop table if exists Prodazhi_last;	");
		mDB.execSQL("	CREATE TABLE Prodazhi_last (	"//
				+ "\n	_id integer primary key asc autoincrement, [Period] date null,[Registrator_0] blob null,[Registrator_1] blob null,[NomerStroki] numeric null	"//
				+ "\n	,[Aktivnost] blob null,[Nomenklatura] blob null,[KharakteristikaNomenklatury] blob null,[ZakazPokupatelya_0] blob null,[ZakazPokupatelya_1] blob null	"//
				+ "\n	,[ZakazPokupatelya_2] blob null,[DogovorKontragenta] blob null,[DokumentProdazhi_0] blob null,[DokumentProdazhi_1] blob null	"//
				+ "\n	,[DokumentProdazhi_2] blob null,[Proekt] blob null,[Podrazdelenie] blob null,[Kolichestvo] numeric null,[Stoimost] numeric null	"//
				+ "\n	,[StoimostBezSkidok] numeric null,[Sebestoimost] numeric null,[SummaVozvrataNacenki] numeric null	"//
				+ "\n	,[kartaKod] text null"//
				+ "\n	);	");
		String sql = "	insert into Prodazhi_last (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,kartaKod"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		max(Period) as Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,main.Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,''"//
				+ "\n	from Prodazhi main"//
				+ "\n		join dogovorykontragentov d1 on d1._idrref=main.dogovorkontragenta "//
				+ "\n				and ifnull(main.VidSkidki,x'00') <> x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Gazeta + "'"//914f887f023f24874f33033ac1cacceb'"
				+ "\n				and ifnull(main.VidSkidki,x'00') <> x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_PoOtvetstvennove + "'"//b6642d98b55a8d5e48c45c1c3731b72e'"
				+ "\n				and ifnull(main.VidSkidki,x'00') <> x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Promokod + "'"//a24b2ee11974f13a4fbac0f8a4e35589'"
				+ "\n				and ifnull(main.VidSkidki,x'00') <> x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Targetnie + "'"//a0c42e5e2beab7e74a98e440d5099464'"
				+ "\n		group by d1.vladelec,main.nomenklatura"//
				+ "\n		order by main.period desc"//
				+ "\n		limit 50000"//
				+ "\n	;	";
		//System.out.println("Prodazhi_last without gazeta " + sql);
		mDB.execSQL(sql);
		sql = "	insert into Prodazhi_last (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,kartaKod"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		max(Period) as Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,main.Podrazdelenie,Kolichestvo	"//
				+ "\n		,0,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,''"//
				+ "\n	from Prodazhi main"//
				+ "\n		join dogovorykontragentov d1 on d1._idrref=main.dogovorkontragenta "//
				+ "\n				and (ifnull(main.VidSkidki,x'00') = x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Gazeta + "'"//914f887f023f24874f33033ac1cacceb'"
				+ "\n				    or ifnull(main.VidSkidki,x'00') = x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_PoOtvetstvennove + "'"//b6642d98b55a8d5e48c45c1c3731b72e'"
				+ "\n				    or ifnull(main.VidSkidki,x'00') = x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Promokod + "'"//a24b2ee11974f13a4fbac0f8a4e35589'"
				+ "\n				    or ifnull(main.VidSkidki,x'00') = x'" + sweetlife.android10.supervisor.Cfg.skidkaId_x_Targetnie + "'"//a0c42e5e2beab7e74a98e440d5099464')"
				+ "\n				    )"
				+ "\n		group by d1.vladelec,main.nomenklatura"//
				+ "\n		order by main.period desc"//
				+ "\n		limit 50000"//
				+ "\n	;	";
		//System.out.println("Prodazhi_last for gazeta " + sql);
		mDB.execSQL(sql);
		mDB.execSQL("	insert into Prodazhi_last (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n		,kartaKod"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		date('2020-01-01') as Period	"//
				+ "\n		,0 as Registrator_0	"//
				+ "\n		,0 as Registrator_1	"//
				+ "\n		,0 as NomerStroki	"//
				+ "\n		,0 as Aktivnost	"//
				+ "\n		,nomenklatura._idrref as Nomenklatura	"//
				+ "\n		,0 as KharakteristikaNomenklatury	"//
				+ "\n		,0 as ZakazPokupatelya_0	"//
				+ "\n		,0 as ZakazPokupatelya_1	"//
				+ "\n		,0 as ZakazPokupatelya_2	"//
				+ "\n		,dogovorykontragentov._idrref as DogovorKontragenta	"//
				+ "\n		,0 as DokumentProdazhi_0	"//
				+ "\n		,0 as DokumentProdazhi_1	"//
				+ "\n		,0 as DokumentProdazhi_2	"//
				+ "\n		,0 as Proekt	"//
				+ "\n		,0 as Podrazdelenie	"//
				+ "\n		,0 as Kolichestvo	"//
				+ "\n		,0 as Stoimost	"//
				+ "\n		,0 as StoimostBezSkidok	"//
				+ "\n		,0 as Sebestoimost	"//
				+ "\n		,0 as SummaVozvrataNacenki	"//
				+ "\n		,1 as kartaKod	"//
				+ "\n	from KartaKlientaDok2	"//
				+ "\n		join KartaKlientaKlient2 on KartaKlientaKlient2.UIN=KartaKlientaDok2.UIN	"//
				+ "\n		join KartaKlientaNomenklatura2 on KartaKlientaNomenklatura2.UIN=KartaKlientaDok2.UIN	"//
				+ "\n		join nomenklatura on nomenklatura._idrref=KartaKlientaNomenklatura2.tovar	"//
				+ "\n		join kontragenty on kontragenty._idrref=KartaKlientaKlient2.vladelec	"//
				+ "\n		join dogovorykontragentov on dogovorykontragentov.vladelec=kontragenty._idrref	"//
				+ "\n	group by KartaKlientaKlient2.vladelec,KartaKlientaNomenklatura2.tovar	"//
				+ "\n	;	");
        /*
        mDB.execSQL("update Prodazhi_last set Stoimost=0"
                + "\n			where EXISTS("
                + "\n					select _id"
                + "\n			from Prodazhi"
                + "\n			where Prodazhi_last.Registrator_1=Prodazhi.Registrator_1"
                + "\n			and Prodazhi_last.period=Prodazhi.period"
                + "\n			and Prodazhi_last.dogovorkontragenta=Prodazhi.dogovorkontragenta"
                + "\n			and Prodazhi_last.nomenklatura=Prodazhi.nomenklatura"
                + "\n			and ("
                + "\n				Prodazhi.VidSkidki=x'914f887f023f24874f33033ac1cacceb'"
                + "\n				or Prodazhi.VidSkidki=x'b6642d98b55a8d5e48c45c1c3731b72e'"
                + "\n				or Prodazhi.VidSkidki=x'a24b2ee11974f13a4fbac0f8a4e35589'"
                + "\n				or Prodazhi.VidSkidki=x'a0c42e5e2beab7e74a98e440d5099464'"
                + "\n		)"
                + "\n	);");
         */


		/*
		mDB.execSQL("	insert into Prodazhi_last (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,main.Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n	from nomenklatura_sorted n	"//
				+ "\n		join Prodazhi main on n._idrref=main.Nomenklatura	"//
				+ "\n		join dogovorykontragentov d1 on d1._idrref=main.dogovorkontragenta "//
				+ "\n			and main.period=("//
				+ "\n				select max(sub.period) from Prodazhi sub"//
				+ "\n					join dogovorykontragentov d2 on d2._idrref=sub.dogovorkontragenta" //
				+ "\n					and sub.nomenklatura=main.nomenklatura "//
				+ "\n					and d1.vladelec=d2.vladelec"//
				+ "\n				)	"//
				+ "\n	;	");
		
		System.out.println("delete from Prodazhi;");
		mDB.execSQL("	delete from Prodazhi;	");
		mDB.execSQL("	insert into Prodazhi (	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n	)	"//
				+ "\n	select	"//
				+ "\n		Period,Registrator_0,Registrator_1,NomerStroki,Aktivnost,Nomenklatura,KharakteristikaNomenklatury,ZakazPokupatelya_0,ZakazPokupatelya_1	"//
				+ "\n		,ZakazPokupatelya_2,DogovorKontragenta,DokumentProdazhi_0,DokumentProdazhi_1,DokumentProdazhi_2,Proekt,Podrazdelenie,Kolichestvo	"//
				+ "\n		,Stoimost,StoimostBezSkidok,Sebestoimost,SummaVozvrataNacenki	"//
				+ "\n	from Prodazhi_last	"//
				+ "\n	;	");
		*/
		/*if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Продажи");
		}*/
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_last_Nomenklatura] ON [Prodazhi_last] ([Nomenklatura]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_last_Period] ON [Prodazhi_last] ([Period]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_last_Stoimost] ON [Prodazhi_last] ([Stoimost]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_last_kolichestvo] ON [Prodazhi_last] ([kolichestvo]);	");
		mDB.execSQL("	CREATE INDEX [IX_Prodazhi_last_Dogovorkontragenta ] ON [Prodazhi_last] ([DogovorKontragenta]);	");

	}

	public static void refreshDogovoryKontragentov_strip(SQLiteDatabase mDB) {
		System.out.println("Анализ БД - Договоры контрагентов");
		mDB.execSQL("	drop table if exists DogovoryKontragentov_strip;	");
		mDB.execSQL("	CREATE TABLE DogovoryKontragentov_strip (	" //
				+ "\n	_id integer primary key autoincrement, [_IDRRef] blob null,[_Version] timestamp null,[PometkaUdaleniya] blob null,[Predopredelennyy] blob null,[Vladelec] blob null,[Roditel] blob null,[EtoGruppa] blob null,[Kod] nchar (10) null,[Naimenovanie] nvarchar (150) null,[Data] date null,[Nomer] nvarchar (20) null,[ValyutaVzaimoraschetov] blob null,[VedenieVzaimoraschetov] blob null,[VidVzaimoraschetov] blob null,[VidUsloviyDogovora] blob null,[DerzhatRezervBezOplatyOgranichennoeVremya] blob null,[DopustimayaSummaZadolzhennosti] numeric null,[DopustimoeChisloDneyZadolzhennosti] numeric null,[Kommentariy] text null,[KontrolirovatSummuZadolzhennosti] blob null,[KontrolirovatChisloDneyZadolzhennosti] blob null,[ObosoblennyyUchetTovarovPoZakazamPokupateley] blob null,[Organizaciya] blob null,[ProcentKomissionnogoVoznagrazhdeniya] numeric null,[ProcentPredoplaty] numeric null,[SposobRaschetaKomissionnogoVoznagrazhdeniya] blob null,[UdalitTipSkidkiNacenki] blob null,[TipCen_0] blob null,[TipCen_1] blob null,[TipCen_2] blob null,[ChisloDneyRezervaBezOplaty] numeric null,[VidDogovora] blob null,[UchetAgentskogoNDS] blob null,[VidAgentskogoDogovora] blob null,[KontrolirovatDenezhnyeSredstvaKomitenta] blob null,[RaschetyVUslovnykhEdinicakh] blob null,[Podrazdelenie] blob null,[GruppaDogovorov] blob null,[DataOkonchaniya] date null,[Zakryt] blob null,[ZadolzhennostVBankDnyakh] blob null,[ReytingPoOplate] numeric null,[BespalletnayaPostavka] blob null,[Khoreka_VidOplaty] blob null,[OtdelnayaPoslednyayaOplachennayaNakladnaya] blob null,[PhormaDogovora] nvarchar (100) null,[Dostavka] nvarchar (200) null	"//
				+ "\n	);	");
		String sql = "	insert into DogovoryKontragentov_strip (	"//
				+ "\n [_IDRRef],[_Version],[PometkaUdaleniya],[Predopredelennyy],[Vladelec],[Roditel],[EtoGruppa] "//
				+ " ,[Kod],[Naimenovanie],[Data],[Nomer],[ValyutaVzaimoraschetov],[VedenieVzaimoraschetov] "//
				+ " ,[VidVzaimoraschetov],[VidUsloviyDogovora],[DerzhatRezervBezOplatyOgranichennoeVremya] "//
				+ " ,[DopustimayaSummaZadolzhennosti],[DopustimoeChisloDneyZadolzhennosti],[Kommentariy] "//
				+ " ,[KontrolirovatSummuZadolzhennosti],[KontrolirovatChisloDneyZadolzhennosti],[ObosoblennyyUchetTovarovPoZakazamPokupateley] "//
				+ " ,[Organizaciya],[ProcentKomissionnogoVoznagrazhdeniya],[ProcentPredoplaty] "//
				+ " ,[SposobRaschetaKomissionnogoVoznagrazhdeniya],[UdalitTipSkidkiNacenki],[TipCen_0] "//
				+ " ,[TipCen_1],[TipCen_2],[ChisloDneyRezervaBezOplaty],[VidDogovora] "//
				+ " ,[UchetAgentskogoNDS],[VidAgentskogoDogovora],[KontrolirovatDenezhnyeSredstvaKomitenta] "//
				+ " ,[RaschetyVUslovnykhEdinicakh],[Podrazdelenie],[GruppaDogovorov],[DataOkonchaniya] "//
				+ " ,[Zakryt],[ZadolzhennostVBankDnyakh],[ReytingPoOplate],[BespalletnayaPostavka] "//
				+ " ,[Khoreka_VidOplaty],[OtdelnayaPoslednyayaOplachennayaNakladnaya],[PhormaDogovora]  "//
				+ " ,[Dostavka] "//
				+ "\n		)	"//
				+ "\n		select "//
				+ "\n		d2.[_IDRRef],d2.[_Version],d2.[PometkaUdaleniya],d2.[Predopredelennyy],d2.[Vladelec],d2.[Roditel],d2.[EtoGruppa],d2.[Kod],d2.[Naimenovanie],d2.[Data],d2.[Nomer] "//
				+ "\n		,d2.[ValyutaVzaimoraschetov],d2.[VedenieVzaimoraschetov],d2.[VidVzaimoraschetov],d2.[VidUsloviyDogovora],d2.[DerzhatRezervBezOplatyOgranichennoeVremya] "//
				+ "\n		,d2.[DopustimayaSummaZadolzhennosti],d2.[DopustimoeChisloDneyZadolzhennosti],d2.[Kommentariy],d2.[KontrolirovatSummuZadolzhennosti] "//
				+ "\n		,d2.[KontrolirovatChisloDneyZadolzhennosti],d2.[ObosoblennyyUchetTovarovPoZakazamPokupateley],d2.[Organizaciya],d2.[ProcentKomissionnogoVoznagrazhdeniya] "//
				+ "\n		,d2.[ProcentPredoplaty],d2.[SposobRaschetaKomissionnogoVoznagrazhdeniya],d2.[UdalitTipSkidkiNacenki],d2.[TipCen_0],d2.[TipCen_1],d2.[TipCen_2] "//
				+ "\n		,d2.[ChisloDneyRezervaBezOplaty],d2.[VidDogovora],d2.[UchetAgentskogoNDS],d2.[VidAgentskogoDogovora],d2.[KontrolirovatDenezhnyeSredstvaKomitenta] "//
				+ "\n		,d2.[RaschetyVUslovnykhEdinicakh],d2.[Podrazdelenie],d2.[GruppaDogovorov],d2.[DataOkonchaniya],d2.[Zakryt],d2.[ZadolzhennostVBankDnyakh],d2.[ReytingPoOplate] "//
				+ "\n		,d2.[BespalletnayaPostavka],d2.[Khoreka_VidOplaty],d2.[OtdelnayaPoslednyayaOplachennayaNakladnaya],d2.[PhormaDogovora],d2.[Dostavka] "//
				+ "\n		from DogovoryKontragentov d2 "//
				+ "\n		join ( "//
				+ "\n			select k2._idrref as id "//
				+ "\n				from MarshrutyAgentov m "//
				+ "\n					join kontragenty k1 on k1._idrref=m.kontragent "//
				+ "\n					join DogovoryKontragentov d1 on d1.vladelec=k1._idrref "//
				+ "\n					join GruppyDogovorov g on d1.gruppadogovorov=g._idrref "//
				+ "\n					join DogovoryKontragentov d2 on d2.gruppadogovorov=g._idrref "//
				+ "\n					join kontragenty k2 on d2.vladelec=k2._idrref "//
				+ "\n				where d1.gruppadogovorov<>X'00000000000000000000000000000000' and d1.gruppadogovorov<>X'00' "//
				+ "\n			union "//
				+ "\n			select k1._idrref as id "//
				+ "\n				from MarshrutyAgentov m "//
				+ "\n					join kontragenty k1 on k1._idrref=m.kontragent "//
				+ "\n					join DogovoryKontragentov d1 on d1.vladelec=k1._idrref "//
				+ "\n				where d1.gruppadogovorov=X'00000000000000000000000000000000' or d1.gruppadogovorov=X'00' "//
				+ "\n			group by id "//
				+ "\n			) k on d2.vladelec=k.id "//
				//+ "\n			where d2.[PometkaUdaleniya]=x'00'"
				+ "\n	;	";
		//System.out.println(sql);
		mDB.execSQL(sql);
		/*if (upd != null) {
			upd.logAndPublishProcess("Индексирование БД - Договоры контрагентов");
		}*/
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_id on DogovoryKontragentov_strip(_id);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_idrref on DogovoryKontragentov_strip(_idrref);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_vladelec on DogovoryKontragentov_strip(vladelec);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_roditel on DogovoryKontragentov_strip(roditel);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_zakryt on DogovoryKontragentov_strip(zakryt);	");
		mDB.execSQL("	CREATE INDEX IX_DogovoryKontragentov_strip_GruppaDogovorov on DogovoryKontragentov_strip(GruppaDogovorov);	");

	}

	@Override
	protected void onPostExecute(Integer result) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".onPostExecute: " + result);
		/*if (mExecuteQueriesTimer != null) {
			mExecuteQueriesTimer.purge();
			mExecuteQueriesTimer.cancel();
			mExecuteQueriesTimer = null;
		}*/
		Bundle resultData = new Bundle();
		resultData.putInt(RESULT_INTEGER, result);
		mTaskListener.onComplete(resultData);
		super.onPostExecute(result);
	}

	private boolean HasFreeSpace() {
		logAndPublishProcess(mResources.getString(R.string.msg_check_free_space));
		if (MemoryStatus.getAvailableExternalMemorySize() < mSettings.getMINIMAL_FREE_SPACE()) {
			return false;
		}
		return true;
	}

	private void UpdateTempTables(long flags) {
		logAndPublishProcess(mResources.getString(R.string.title_progress_update_temp_tables));
		UpdateTempTables.update(mDB, flags);
	}

	private void CreateDatabaseBackup() {
		logAndPublishProcess(mResources.getString(R.string.msg_reserve_db));
		if (mIsTodayFirstTime) {
			System.out.println("CreateDatabaseBackup start");
			boolean b = mDB.isDatabaseIntegrityOk();
			System.out.println("done integrity check");
			if (b) {
				Backup.reserveDatabase();
			} else {
				System.out.println("corrupted database");
			}
			//System.out.println("new backup");
			//Backup._reserveDatabase();
			System.out.println("done CreateDatabaseBackup");
		}
		/*if (!Backup.reserveDatabase()) {
			LogNoneStableAppVersion();
			ErrorReporter.getInstance().putCustomData("CreateDatabaseBackup", "NoneStableVersion=" + mNoneStableVersion);
			ErrorReporter.getInstance().handleSilentException(null);
		}*/
	}

	/*private void DownloadAndReadSettingsXML() {
		logAndPublishProcess(mResources.getString(R.string.msg_download_settings));
		if (mSettings.update()) {
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_SETTINGS_DOWNLOAD, null);
		}
		else {
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_SETTINGS_DOWNLOAD, null);
		}
	}*/
	private boolean IsNeedAppUpdate(String serverVersion) {
		try {
			String appVersion = mApplicationContext.getPackageManager().getPackageInfo(mApplicationContext.getPackageName(), 0).versionName;
			return mUpdateChecker.IsNeedAppUpdate(appVersion);
		} catch (NameNotFoundException e) {
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "IsNeedAppUpdate");
			ErrorReporter.getInstance().handleSilentException(e);
			return false;
		}
	}

	private int DownloadAndReadUpdateXML() {
		LogHelper.debug(this.getClass().getCanonicalName() + ": DownloadAndReadUpdateXML");
		logAndPublishProcess(mResources.getString(R.string.msg_check_update));
		if (mUpdateChecker.DownloadFile()) {
			if (mUpdateChecker.ReadUpdateFile()) {
				return SUCCESSFULL;
			} else {
				ErrorReporter.getInstance().putCustomData("DownloadAndReadUpdateXML ReadUpdateFile" , "NoneStableVersion " + mNoneStableVersion);
				ErrorReporter.getInstance().handleSilentException(null);
				return ERROR_PARSE_XML;
			}
		} else {
			return ERROR_FTP_DOWNLOAD_XML;
		}
	}

	private int DownloadAndExecuteAppUpdate() {
		String pathToDownloadedFile = null;
		logAndPublishProcess(mResources.getString(R.string.msg_download_update));
		try {
			if (!mIsRestored) {
				pathToDownloadedFile = ftpClient.downloadFile(mSettings.getTABLET_WORKING_DIR(), mUpdateChecker.getPathToFTPFile());
				LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_APP_DOWNLOAD, pathToDownloadedFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_APP_DOWNLOAD, pathToDownloadedFile);
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "DownloadAndExecuteAppUpdate");
			ErrorReporter.getInstance().handleSilentException(e);
			return ERROR_FTP_DOWNLOAD_APK;
		}
		try {
			logAndPublishProcess(mResources.getString(R.string.msg_reserve_app));
			Backup.reserveApp();
		} catch (Exception e) {
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "DownloadAndExecuteAppUpdate Backup.reserveApp");
			ErrorReporter.getInstance().handleSilentException(e);
		}
		try {
			logAndPublishProcess("Install update from " + pathToDownloadedFile);
			AppUpdater.Update(mApplicationContext, pathToDownloadedFile);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "DownloadAndExecuteAppUpdate AppUpdater.Update");
			ErrorReporter.getInstance().handleSilentException(e);
			return ERROR_APK_FILE_NOT_FOUND;
		}
		return UPDATE_APP_VERSION;
	}

	private void LogNoneStableAppVersion() {
		try {
			mNoneStableVersion = mApplicationContext.getPackageManager().getPackageInfo(mApplicationContext.getPackageName(), 0).versionName;
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_BAD_APP_VERSION, mNoneStableVersion);
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "IsNeedAppUpdate");
			ErrorReporter.getInstance().handleSilentException(null);
		} catch (NameNotFoundException e) {
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "IsNeedAppUpdate");
			ErrorReporter.getInstance().handleSilentException(e);
		}
	}

	void xmlToDb(String xmlName) {
		System.out.println("xmlName " + xmlName);
	}

	private int DownloadAndReadDelta() {
		LogHelper.debug(this.getClass().getCanonicalName() + ": DownloadAndReadDelta start");
		logAndPublishProcess(mResources.getString(R.string.msg_check_update));
		String pathToDownloadedFile = null;
		try {
			/*pathToDownloadedFile = IsNeedDeltaUpdate();
			if (pathToDownloadedFile == null) {
				return UPDATE_APP_FINISHED;
			}*/
			String nodeName = null;
			Cursor cursor = mDB.rawQuery("select [Kod] from Android" , null);
			cursor.moveToFirst();
			if (cursor.getString(0).startsWith("12-")) {
				//serverName = cursor.getString(0);
			} else {
				nodeName = cursor.getString(0);
			}
			cursor.moveToNext();
			if (cursor.getString(0).startsWith("12-")) {
				//serverName = cursor.getString(0);
			} else {
				nodeName = cursor.getString(0);
			}
			pathToDownloadedFile = "AndroidExchange_12-_" + nodeName.trim() + ".zip";
			logAndPublishProcess(mResources.getString(R.string.msg_download_delta));
			pathToDownloadedFile = ftpClient.downloadFile(mSettings.getTABLET_DELTA_DIR(), pathToDownloadedFile);
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_DOWNLOAD, pathToDownloadedFile);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "DownloadAndReadDelta ftpClient.downloadFile");
			ErrorReporter.getInstance().handleSilentException(e);
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_DOWNLOAD, pathToDownloadedFile);
			return ERROR_FTP_DOWNLOAD_DELTA;
		}
		try {
			logAndPublishProcess(mResources.getString(R.string.msg_unzip_update));
			Decompress decompressor = new Decompress();
			decompressor.unzip(mSettings.getTABLET_DELTA_DIR(), pathToDownloadedFile);
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_UNZIP, pathToDownloadedFile);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorReporter.getInstance().putCustomData("UpdateTask" , "DownloadAndReadDelta unzip");
			ErrorReporter.getInstance().handleSilentException(e);
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_UNZIP, pathToDownloadedFile);
			return ERROR_UNZIP_DELTA;
		}
		/*java.util.Date d=new java.util.Date();
		int day = d.getDate();
		int month = d.getMonth();
		if (day == 1 && month == 3) {
			System.out.println("yes, clear MarshrutyAgentov");
			mDB.execSQL("delete from MarshrutyAgentov");
		}else{
			System.out.println("no, skip clear MarshrutyAgentov");
		}*/
		System.out.println("clear MarshrutyAgentov");
		mDB.execSQL("delete from MarshrutyAgentov");
		//System.out.println("delete from tovaryDlyaDozakaza;");
		//mDB.execSQL("	delete from tovaryDlyaDozakaza;	");
		System.out.println("clear TekuschieCenyOstatkovPartiy");
		mDB.execSQL("delete from TekuschieCenyOstatkovPartiy");
		System.out.println("clear CenyNomenklaturySklada");
		mDB.execSQL("delete from CenyNomenklaturySklada");
		System.out.println("clear MinimalnyeNacenkiProizvoditeley_1");
		mDB.execSQL("delete from MinimalnyeNacenkiProizvoditeley_1");


		System.out.println("clear SkidkaPartneraKarta");
		Calendar now = Calendar.getInstance();
		//now.add(Calendar.DAY_OF_MONTH, -1);
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String dsql = "delete from SkidkaPartneraKarta where date(DataOkonchaniya)<date('" + f.format(now.getTime()) + "')";
		System.out.println(dsql);
		mDB.execSQL(dsql);
		logAndPublishProcess("Пересчёт прайса");
		UpdateTask.tempAdjustPrice(mDB);
		logAndPublishProcess(mResources.getString(R.string.msg_parse_delta));
		/*if (mExecuteQueriesTimer == null) {
			mExecuteQueriesTask = new ExecuteQueriesTask(mDB, mDeltaUpdater);
			mExecuteQueriesTimer = new Timer();
			mExecuteQueriesTimer.schedule(mExecuteQueriesTask, 0, TIMER_INTERVAL);
		}*/
		long timeStarted, timeEnded, time;
		LogHelper.debug(this.getClass().getCanonicalName() + ": pathToDownloadedFile: " + pathToDownloadedFile);
		if (pathToDownloadedFile != null && pathToDownloadedFile.length() > 3) {
			/*if(pathToDownloadedFile.length() > 3){
				String xmlName=pathToDownloadedFile.substring(0, pathToDownloadedFile.length() - 3) + "xml";
				xmlToDb(xmlName);
				return SUCCESSFULL;
			}*/
			try {
				timeStarted = System.currentTimeMillis();
				mDeltaUpdater.parse(pathToDownloadedFile.substring(0, pathToDownloadedFile.length() - 3) + "xml");
				long timeParse = (System.currentTimeMillis() - timeStarted) / 1000;
				QueriesList queriesList = mDeltaUpdater.getQueriesList();
				while (queriesList.getCount() != 0) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				}
				timeEnded = System.currentTimeMillis();
				time = (timeEnded - timeStarted) / 1000;
				LogHelper.debug(this.getClass().getCanonicalName() + "Records count: " + mDeltaUpdater.getDeltaParser().getRecordsCount() + "; Time Parse - " + String.valueOf(timeParse)
						+ " seconds; Time WriteDB - " + String.valueOf(time) + " seconds");
				if (mDeltaUpdater.getDeltaParser().HasBodyParseErrors() || mDeltaUpdater.getDeltaParser().HasHeaderParseErrors()
					//|| mExecuteQueriesTask.HasErrors()
				) {
					LogHelper.debug(this.getClass().getCanonicalName() + "__________________has errors________________________");
					LogHelper.debug(this.getClass().getCanonicalName() + "__________________has errors________________________");
					LogHelper.debug(this.getClass().getCanonicalName() + "__________________has errors________________________");
				}
				//LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_PARSE, pathToDownloadedFile);
				//LogHelper.setLastUpdateStatus(LogHelper.LOG_TYPE_SUCCESS);
				/*String sql = "select * from KartaKlienta;";
				Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				System.out.println("---------" + sql);
				System.out.println("---------" + b.dumpXML());
				System.out.println("-----------------------");*/
			} catch (Exception e) {
				e.printStackTrace();
				/*
				LogHelper.setLastUpdateStatus(this.getClass().getCanonicalName() + ": " + e.getMessage());
				LogNoneStableAppVersion();
				Backup.restoreDatabase();
				ErrorReporter.getInstance().putCustomData("UpdateTask", "DownloadAndReadDelta parse");
				ErrorReporter.getInstance().handleSilentException(e);
				//LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_PARSE, pathToDownloadedFile);
				LogHelper.setLastUpdateStatus(LogHelper.LOG_TYPE_ERROR);
				return UPDATE_APP_FINISHED;
				*/
			}
		}
		LogHelper.debug(this.getClass().getCanonicalName() + ": DownloadAndReadDelta success");
		return SUCCESSFULL;
	}

	/*private int ReadDataFromLogDatabase() {
		LogHelper.debug(this.getClass().getCanonicalName() + ": ReadDataFromLogDatabase");
		String sqlQuery = "select [message], [path], [dateadded] from " + LoggingOpenHelper.LOG_TABLE_NAME + " where date(dateadded) = '"
				+ DateTimeHelper.SQLDateString(Calendar.getInstance().getTime()) + "'";
		Cursor logCursor = null;
		try {
			logCursor = mDBLogging.rawQuery(sqlQuery, null);
			if (logCursor.moveToFirst()) {
				do {
					if (logCursor.getString(0).compareToIgnoreCase(LogHelper.LOG_MESSAGE_RESERVE_DB) == 0) {
						mIsTodayFirstTime = false;
					}
					else
						if (logCursor.getString(0).compareToIgnoreCase(LogHelper.LOG_MESSAGE_RESTORE_DB) == 0) {
							mIsRestored = true;
						}
				}
				while (logCursor.moveToNext());
			}
			try {
				String appVersion = mApplicationContext.getPackageManager().getPackageInfo(mApplicationContext.getPackageName(), 0).versionName;
				if (mNoneStableVersion.compareToIgnoreCase(appVersion) != 0) {
					mIsRestored = false;
				}
			}
			catch (NameNotFoundException e) {
			}
		}
		finally {
			if (logCursor != null && logCursor.isClosed()) {
				logCursor.close();
			}
		}
		return SUCCESSFULL;
	}*/
	private void DeletePreviousResponseXML() {
		int responseFilesCount = ftpClient.mResponces.size();
		for (int i = 0; i < responseFilesCount; i++) {
			ftpClient.deleteFile(ftpClient.mResponces.get(i));
		}
	}

	void uploadUpdateAnswer(String From, String MessageNo, String ReceivedNo) {
		System.out.println("uploadUpdateAnswer");
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "\n	<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n		<soap:Body><Sozdadim xmlns=\"http://ws.swl/SozdadimXML\">"//
				+ "\n			<From>" + From + "</From>"//
				+ "\n			<MessageNo>" + MessageNo + "</MessageNo>"//
				+ "\n			<ReceivedNo>" + ReceivedNo + "</ReceivedNo>"//
				+ "\n			</Sozdadim>"//
				+ "\n		</soap:Body>"//
				+ "\n	</soap:Envelope>";
		System.out.println(xml);
		RawSOAP r = new RawSOAP();
		r.xml.is(xml);
		r.url.is(Settings.getInstance().getBaseURL() + "SozdadimXML.1cws");
		Report_Base.startPing();
		r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
		try {
			System.out.println("done " + r.exception.property.value() + " / " + r.statusCode.property.value() + " / " + r.data.dumpXML());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void SendResponseXML() {
		DeletePreviousResponseXML();
		logAndPublishProcess(mResources.getString(R.string.msg_upload_response));
		DeltaData deltaData = mDeltaUpdater.getDeltaParser().getDeltaData();
		ResponseXML responseXml = new ResponseXML(deltaData);
		String pathToFile = String.format(mSettings.getFTP_DELTA_NAME(), deltaData.getTo(), deltaData.getFrom());
		String no = String.format("%d" , deltaData.getReceivedNo() + 1);
		int noLen = no.length();
		for (int i = 0; i < 10 - noLen; i++) {
			pathToFile += "0";
		}
		pathToFile += no;
		String ftpDir = "";
		int sepIndex = pathToFile.lastIndexOf(File.separator);
		if (sepIndex > 0) {
			ftpDir = pathToFile.substring(0, sepIndex + 1);
			pathToFile = pathToFile.substring(sepIndex + 1, pathToFile.length());
		}
		String destinationDir = mSettings.getTABLET_WORKING_DIR();
		responseXml.create(destinationDir + pathToFile + ".xml");
		Compress compressor = new Compress();
		compressor.addFiles(new String[]{destinationDir + pathToFile + ".xml"});
		try {
			compressor.zip(destinationDir + pathToFile + ".zip");
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_RESPONSE_ZIP, ftpDir + pathToFile + ".zip");
			try {
				//ftpClient.uploadFile(destinationDir + pathToFile + ".zip", ftpDir + pathToFile + ".zip");
				uploadUpdateAnswer(deltaData.getTo()//
						, String.format("%d" , deltaData.getReceivedNo() + 1)//
						, String.format("%d" , deltaData.getMessageNo()));
				File deleteXmlFile = new File(destinationDir + pathToFile + ".xml");
				System.out.println("delete " + destinationDir + pathToFile + ".xml");
				deleteXmlFile.delete();
				File deleteZipFile = new File(destinationDir + pathToFile + ".zip");
				System.out.println("delete " + destinationDir + pathToFile + ".zip");
				deleteZipFile.delete();
				LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_UPLOAD_RESPONSE, ftpDir + pathToFile + ".zip");
			} catch (Exception e) {
				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_UPLOAD_RESPONSE, ftpDir + pathToFile + ".zip");
			}
		} catch (Exception e) {
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_RESPONSE_ZIP, ftpDir + pathToFile + ".zip");
		}
	}

	private String IsNeedDeltaUpdate() {
		LogHelper.debug("IsNeedDeltaUpdate start");
		String serverName = null;
		String nodeName = null;
		Cursor cursor = mDB.rawQuery("select [Kod] from Android" , null);
		cursor.moveToFirst();
		if (cursor.getString(0).startsWith("12-")) {
			serverName = cursor.getString(0);
		} else {
			nodeName = cursor.getString(0);
		}
		cursor.moveToNext();
		if (cursor.getString(0).startsWith("12-")) {
			serverName = cursor.getString(0);
		} else {
			nodeName = cursor.getString(0);
		}
		cursor.close();
		cursor = null;
		//LogHelper.debug("IsNeedDeltaUpdate start");
		String deltaPath = ftpClient.getPathIfExist(//
				String.format(mSettings.getFTP_DELTA_NAME(), serverName.trim(), nodeName.trim())//
				, String.format(mSettings.getFTP_DELTA_NAME(), nodeName.trim(), serverName.trim())//
		);
		LogHelper.debug("deltaPath is " + deltaPath);
		if (deltaPath == null) {
			return null;
		}
		if (!IsNeedDownloadDelta(deltaPath)) {
			return null;
		}
		return deltaPath;
	}

	private boolean IsNeedDownloadDelta(String pathToDelta) {
		LogHelper.debug("IsNeedDownloadDelta for " + pathToDelta);
		int sepIndex = pathToDelta.lastIndexOf(File.separator);
		if (sepIndex != -1) {
			mFTPDeltaFileName = pathToDelta.substring(sepIndex + 1, pathToDelta.length());
		}
		File dir = new File(mSettings.getTABLET_DELTA_DIR());
		File files[] = dir.listFiles(this);
		boolean r = (files.length == 0);
		LogHelper.debug("IsNeedDownloadDelta " + r);
		return r;
		//return true;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.compareToIgnoreCase(mFTPDeltaFileName) == 0;
	}

	@SuppressWarnings("unused")
	private int RestoreData() {
		try {
			logAndPublishProcess(mResources.getString(R.string.msg_restore_db));
			Backup.restoreDatabase();
		} catch (Exception exRestore) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "Exception restore - " + exRestore);
		}
		return UPDATE_APP_FINISHED;
	}
}
