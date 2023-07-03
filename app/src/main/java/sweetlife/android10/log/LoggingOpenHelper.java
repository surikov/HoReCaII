package sweetlife.android10.log;

import sweetlife.android10.Settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoggingOpenHelper extends SQLiteOpenHelper {

	//версия базы данных - при смене версии база пересоздается
	private static final int DATABASE_VERSION = 7;
	//путь к базе данных
	public static final String DATABASE_NAME = Settings.getInstance().getTABLET_LOGGING_FILE();
	//имя таблицы для записи логов
	public static final String LOG_TABLE_NAME = "log";
	//sql-запрос создания таблицы
	private static final String LOG_TABLE_CREATE =
			"CREATE TABLE " + LOG_TABLE_NAME + " (" +
					"[_id] integer primary key asc autoincrement, " +
					"[dateadded] DATE, " +
					"[type] TEXT, " +
					"[owner] TEXT, " +
					"[message] TEXT, " +
					"[path] TEXT);";
	//имя таблицы для записи логов отладочного режима
	public static final String LOG_DEBUG_TABLE_NAME = "debuglog";
	//sql-запрос создания таблицы
	private static final String LOG_DEBUG_TABLE_CREATE =
			"CREATE TABLE " + LOG_DEBUG_TABLE_NAME + " (" +
					"[_id] integer primary key asc autoincrement, " +
					"[dateadded] DATETIME, " +
					"[level] TEXT, " +
					"[class] TEXT, " +
					"[method] TEXT, " +
					"[params] TEXT, " +
					"[message] TEXT);";

	//конструктор класса
	public LoggingOpenHelper(Context context) {
		//вызов меода базового класса
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//функция создания таблиц
	@Override
	public void onCreate(SQLiteDatabase db) {

		//создание таблиц базы данных
		db.execSQL(LOG_TABLE_CREATE);
		db.execSQL(LOG_DEBUG_TABLE_CREATE);

		//создание нового сообщения и заполнение входными данными
		/*LogMessage message = new LogMessage();
		message.Type    = LogHelper.LOG_TYPE_SUCCESS;
		message.Owner   = LogHelper.LOG_OWNER_UPDATE;
		message.Message = LogHelper.LOG_MESSAGE_DELTA_PARSE;
		message.Path    = null;
		
		LogHandler.WriteLogRecord(db, message);
		*/
		//LogHelper.setLastUpdateStatus(LogHelper.LOG_TYPE_SUCCESS,db);
	}

	//функция обновления таблиц
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		//уничтожение старых таблиц
		db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + LOG_DEBUG_TABLE_NAME);
		//создание новых таблиц
		onCreate(db);
	}
}
