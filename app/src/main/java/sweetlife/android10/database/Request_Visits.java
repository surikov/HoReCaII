package sweetlife.android10.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_Visits {

	public static Cursor Request(SQLiteDatabase db, String dateFrom, String dateTo) {
	
		String sql = "select vtp._id, date(vtp.BeginDate), vtp.[BeginTime], vtp.[EndTime], " +
			"vtp.[Client], k.[Naimenovanie] [KlientNaimenovanie], vtp.[Activity], vtp.Upload " +
			
			"from Vizits vtp " +
			"inner join Kontragenty k on k.[Kod] = vtp.[Client] " +
			
            "where date(vtp.[BeginDate]) >= '" + dateFrom + 
            "' and date(vtp.[BeginDate]) <= '" + dateTo + "'";
		
		Cursor cursor = db.rawQuery(sql, null);
		
		return( cursor );    	  
	}
	
	public static String getDate( Cursor cursor ) {
		
		return cursor.getString(1);
	}

	public static String getStartTime( Cursor cursor ) {
		
		return cursor.getString(2);
	}

	public static String getEndTime( Cursor cursor ) {
		
		return cursor.getString(3);
	}
	
	public static String getKontragent( Cursor cursor ) {
		
		return cursor.getString(5);
	}
	
	public static String getActivity( Cursor cursor ) {
		
		return cursor.getString(6);
	}
	
	public static boolean isUpload( Cursor cursor ) {
		
		return cursor.getInt(7) == 1 ? true : false;
	}
}