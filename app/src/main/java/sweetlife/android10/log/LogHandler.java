package sweetlife.android10.log;

import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import sweetlife.android10.utils.DateTimeHelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

//класс для перенаправления вывода логов
public class LogHandler extends Handler {
	private static SQLiteDatabase mDB = null;//ссылка на базу данных логов

	//конструктор класса
	public LogHandler(SQLiteDatabase db) {
		mDB = db;
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	public static void cleanUpDebugLog() {
		//System.out.println("cleanUpDebugLog");
		try {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, -1);
			mDB.delete(LoggingOpenHelper.LOG_DEBUG_TABLE_NAME, "dateadded<?", new String[]{DateTimeHelper.SQLDateTimeString(now.getTime())});
			//WriteDebugRecord(mDB, "cleanUpDebugLog before " + now);
			LogHelper.debug("cleanUpDebugLog before " + now.getTime());
		} catch (Throwable t) {
			System.out.println("cleanUpDebugLog " + t.getMessage());
		}
	}

	//функция записи логов
	@Override
	public void publish(LogRecord record) {
		//получаем параметры записи
		Object[] params = record.getParameters();
		//если параметры имеются
		if (params != null) {
			//если параметр является типом LogMessage
			if (params[0] instanceof LogMessage) {
				WriteLogRecord(mDB, (LogMessage) params[0]);
			}
		}
	}

	//функция записи логов
	public static void WriteLogRecord(SQLiteDatabase logDB, LogMessage message) {
		if (logDB == null) {
			//System.out.println("WriteLogRecord to null /"+message.Type+"/"+message.Owner+"/"+message.Message);
			return;
		}
		//формируем дату в формате базы данных
		String date = DateTimeHelper.DateToSQLDate(Calendar.getInstance().getTime()).toString();
		//если приведение прошло успешно
		if (message != null) {
			//создаем объект с параметрами для добавления записи в базу
			ContentValues values = new ContentValues();
			//добавляем дату если доступна
			if (date != null) {
				values.put("dateadded", date);
			}
			//добавляем тип сообщения если доступен
			if (message.Type != null) {
				values.put("type", message.Type);
			}
			//добавляем владельца сообщения если доступен
			if (message.Owner != null) {
				values.put("owner", message.Owner);
			}
			//добавляем сообщение если доступно
			if (message.Message != null) {
				values.put("message", message.Message);
			}
			//добавляем путь к файле если доступен
			if (message.Path != null) {
				values.put("path", message.Path);
			}
			//добавляем запись в базу данных
			logDB.insert(LoggingOpenHelper.LOG_TABLE_NAME, null, values);
			//выходим из функции
			return;
		}
	}

	public static void WriteDebugRecord(SQLiteDatabase logDB, String message) {
		//формируем дату в формате базы данных
		String date = DateTimeHelper.DateToSQLTimestamp(Calendar.getInstance().getTime()).toString();
		//создаем объект с параметрами для добавления записи в базу
		ContentValues values = new ContentValues();
		values.put("dateadded", date);
		values.put("message", message);
		//добавляем запись в базу данных
		logDB.insert(LoggingOpenHelper.LOG_DEBUG_TABLE_NAME, null, values);
		//выходим из функции
		return;
	}
}
