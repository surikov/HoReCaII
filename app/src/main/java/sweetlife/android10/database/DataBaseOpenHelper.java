package sweetlife.android10.database;

import sweetlife.android10.Settings;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataBaseOpenHelper extends SQLiteOpenHelper implements BaseColumns {

	private static final int    DATABASE_VERSION = 2;
	public static final String DATABASE_NAME    = Settings.getInstance().getTABLET_DATABASE_FILE();

	public DataBaseOpenHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {

	}
}
