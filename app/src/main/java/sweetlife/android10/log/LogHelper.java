package sweetlife.android10.log;

import java.text.SimpleDateFormat;

import android.database.Cursor;

import java.util.*;

import sweetlife.android10.utils.DateTimeHelper;

import sweetlife.android10.ApplicationHoreca;

//вспомогательный класс для записи логов
public class LogHelper {
	//объявление переменных с типом выполняемой операции
	public static final String LOG_MESSAGE_RESERVE_DB = "Создание резервной копии базы данных";
	public static final String LOG_MESSAGE_RESERVE_DELTA = "Создание резервной копии дельты обновлений";
	public static final String LOG_MESSAGE_RESERVE_APP = "Создание резервной копии приложения";
	public static final String LOG_MESSAGE_RESERVE_UPDATE_XML = "Создание резервной копии файла обновлений";
	public static final String LOG_MESSAGE_RESTORE_DB = "Восстановление базы данных из резервной копии";
	public static final String LOG_MESSAGE_RESTORE_DELTA = "Восстановление дельты обновлений из резервной копии";
	public static final String LOG_MESSAGE_RESTORE_UPDATE_XML = "Восстановление файла обновлений из резервной копии";
	public static final String LOG_MESSAGE_RESTORE_APP = "Восстановление приложения из резервной копии";
	public static final String LOG_MESSAGE_REREAD_DATA = "Данные для восстановления перезачитаны";
	public static final String LOG_MESSAGE_UPDATE_XML_DOWNLOADED = "Загрузка файла обновлений";
	public static final String LOG_MESSAGE_PARSE_UPDATE_XML = "Чтение файла обновлений";
	public static final String LOG_MESSAGE_APP_DOWNLOAD = "Загрузка приложения";
	public static final String LOG_MESSAGE_APP_UPDATE = "Обновление приложения";
	public static final String LOG_MESSAGE_DELTA_DOWNLOAD = "Загрузка дельты обновлений";
	public static final String LOG_MESSAGE_DELTA_UNZIP = "Распаковка дельты обновлений";
	private static final String LOG_MESSAGE_DELTA_PARSE = "Чтение дельты обновлений";
	public static final String LOG_MESSAGE_DELTA_RESPONSE_ZIP = "Запаковка ответа для дельты обновлений";
	public static final String LOG_MESSAGE_UPLOAD_RESPONSE = "Выгрузка результатов чтения дельты обновлений";
	public static final String LOG_MESSAGE_UPDATE_DB_STRUCTURE = "Обновление структуры базы данных";
	public static final String LOG_MESSAGE_SETTINGS_DOWNLOAD = "Загрузка файла настроек";
	public static final String LOG_MESSAGE_DBLOGGING_CREATE = "Создание базы данных логов";
	public static final String LOG_MESSAGE_CONNECT_TO_INTERNET = "Подключение к интернет";
	public static final String LOG_MESSAGE_CONNECT_TO_FTP = "Подключение к FTP серверу";
	public static final String LOG_MESSAGE_FREE_SPACE = "Проверка свободного места";
	public static final String LOG_MESSAGE_NO_DB_RESERVE_COPY = "Поиск резервной копии базы данных";
	public static final String LOG_MESSAGE_BAD_APP_VERSION = "Нестабильно работающий релиз";
	public static final String LOG_MESSAGE_UPLOAD_VIZIT = "Выгрузка визитов";
	public static final String LOG_MESSAGE_UPLOAD_GPS = "Выгрузка GPS данных";
	public static final String LOG_MESSAGE_UPLOAD_ORDERS = "Выгрузка заказов";
	public static final String LOG_MESSAGE_UPDATE_CONTRACTS = "Обновление договоров";
	public static final String LOG_MESSAGE_UPDATE_AVAILABLE_AMOUNT = "Обновление доступных сумм";
	public static final String LOG_MESSAGE_UPLOAD_LOG_EMAIL = "Выгрузка логов на Email";
	public static final String LOG_MESSAGE_UPLOAD_LOG_FTP = "Выгрузка логов на FTP";
	public static final String LOG_OWNER_UPDATE = "Обновление";
	public static final String LOG_OWNER_ROUTE_TP = "Маршрут ТП";
	public static final String LOG_OWNER_UPLOAD_LOG = "Логирование";
	public static final String LOG_TYPE_SUCCESS = "Успешно";
	public static final String LOG_TYPE_ERROR = "Ошибка";

	public static void setLastSuccessfulUpdate(Calendar syncCalendar) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"YYYY-MM-DD HH:MM:SS.SSS"
		String date = f.format(syncCalendar.getTime());
		String sql = "insert into SyncLog (endTime) values ('" + date + "');";
		System.out.println("=====> setLastSuccessfulUpdate " + sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	public static Calendar getLastSuccessfulUpdate() {
		//ApplicationHoreca.getInstance().getDataBase().execSQL("delete from synclog;");
		//ApplicationHoreca.getInstance().getDataBase().execSQL("delete from synclog where _id>=2494 and _id<=2498");
		//ApplicationHoreca.getInstance().getDataBase().execSQL("delete from synclog where _id=2862");


		String sqlLog = "select endTime from SyncLog order by endTime desc limit 1;";
		Cursor cursor = null;
		Calendar syncCalendar = Calendar.getInstance();
		syncCalendar.add(Calendar.DAY_OF_MONTH, -30);
		//System.out.println("getLastSuccessfulUpdate set fake "+syncCalendar);
		try {
			cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery(sqlLog, null);
			if (cursor.moveToFirst()) {
				syncCalendar = Calendar.getInstance();
				syncCalendar.setTime(DateTimeHelper.SQLDateToDate(cursor.getString(0)));
				syncCalendar = DateTimeHelper.getOnlyDateInfo(syncCalendar);
				//System.out.println("getLastSuccessfulUpdate found "+syncCalendar);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return syncCalendar;
	}

	/*public static Calendar _getLastSuccessfulUpdate() {
		String sqlLog = "select max(date(dateadded))"//
				+ " from " + LoggingOpenHelper.LOG_TABLE_NAME// 
				+ " where message like '%" + LogHelper.LOG_MESSAGE_DELTA_PARSE + "%'"//
				+ " and type like '%" + LogHelper.LOG_TYPE_SUCCESS + "%'";
		Cursor cursor = null;
		Calendar syncCalendar = null;
		try {
			cursor = ApplicationHoreca.getInstance().getLogDataBase().rawQuery(sqlLog, null);
			if (cursor.moveToFirst()) {
				syncCalendar = Calendar.getInstance();
				syncCalendar.setTime(DateTimeHelper.SQLDateToDate(cursor.getString(0)));
				syncCalendar = DateTimeHelper.getOnlyDateInfo(syncCalendar);
			}
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		debug("getLastSuccessfulUpdate "+syncCalendar.getTime());
		return syncCalendar;
	}*/
	public static void _logUpdateEnd() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"YYYY-MM-DD HH:MM:SS.SSS"
		String date = f.format(new Date());
		String sql = "insert into SyncLog (endTime) values ('" + date + "');";
		//System.out.println("logUpdateEnd "+sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	public static void _logUpdateStart() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"YYYY-MM-DD HH:MM:SS.SSS"
		String date = f.format(new Date());
		String sql = "insert into SyncLog (startTime) values ('" + date + "');";
		//System.out.println("logUpdateStart "+sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	/*
	public static void setLastUpdateStatus(String type, SQLiteDatabase db) {
		debug("setLastUpdateStatus "+type);
		LogMessage m = new LogMessage();
		m.Type = type;
		m.Owner = LOG_OWNER_UPDATE;
		m.Message = LOG_MESSAGE_DELTA_PARSE;
		m.Path = null;
		//SQLiteDatabase db = ApplicationHoreca.getInstance().getLogDataBase();
		LogHandler.WriteLogRecord(db, m);
	}
	public static void setLastUpdateStatus(String type) {
		debug("setLastUpdateStatus "+type);
		LogMessage m = new LogMessage();
		m.Type = type;
		m.Owner = LOG_OWNER_UPDATE;
		m.Message = LOG_MESSAGE_DELTA_PARSE;
		m.Path = null;
		SQLiteDatabase db = ApplicationHoreca.getInstance().getLogDataBase();
		LogHandler.WriteLogRecord(db, m);
	}
	*/
	//функция записи логов
	public synchronized static void writeLog(String sType, String sOwner, String sMessage, String sPath) {
		debug(sType + ": " + sOwner + ": " + sMessage + ": " + sPath);
		/* switch off
		try {
			debug(sType + ": " + sOwner + ": " + sMessage + ": " + sPath);
			//создание нового сообщения и заполнение входными данными
			LogMessage message = new LogMessage();
			message.Type = sType;
			message.Owner = sOwner;
			message.Message = sMessage;
			message.Path = sPath;
			ApplicationHoreca.getInstance().getLogger().log(Level.INFO, "", message);
			
		}
		catch (Exception ex) {
		}//игнорируем ошибки
		*/
	}

	public synchronized static void debug(String sMessage) {
		System.out.println("LogHelper.debug " + sMessage);
		/*
		try {
			Log.d("sw", sMessage);
			SQLiteDatabase db = ApplicationHoreca.getInstance().getLogDataBase();
			if (db == null) {
				Log.d("sw",  "log db is null /"+sMessage);
			}
			else {
				LogHandler.WriteDebugRecord(db, sMessage);
			}
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
			//Log.v("sw", t.getMessage());
		}
		*/
	}
}
