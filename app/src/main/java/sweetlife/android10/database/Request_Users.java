package sweetlife.android10.database;

import sweetlife.android10.data.common.User;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_Users  {

	private SQLiteDatabase m_DB;

	public Request_Users( SQLiteDatabase db ) {
		
		m_DB = db;
	}

	public Cursor RequestAllItems() {
		
		Cursor c = m_DB.rawQuery("SELECT * FROM Cur_Users", null);
		
		return( c );    	  
	}
	
	public User getUser( Cursor cursor ) {
		

		return( new User( cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3) ) );
	}
}