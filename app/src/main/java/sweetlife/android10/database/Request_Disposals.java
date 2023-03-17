package sweetlife.android10.database;

import java.util.ArrayList;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_Disposals implements ITableColumnsNames {

	public static Cursor Request(SQLiteDatabase db, String dateFrom, String dateTo) {

		String sqlString = "select z._id [_id], z._IDRRef [_IDRRef], z.Data [Data]," +
				"z.Nomer [Nomer], z.Vygruzhen [Vygruzhen], z.[Kontragent] [Kontragent], " +
				"k.Naimenovanie [KontragentNaimenovanie], z.Summa [Summa], " +
				"z.[Kommentariy] [Kommentariy], k.[Kod] [Kod] " +
				"from RasporyazhenieNaOtgruzku z " +
				"inner join Kontragenty k on k.[_IDRRef] = z.[Kontragent] " +
				"where date(z.Data) >= date('" + dateFrom + "') and date(z.Data) <= date('" + dateTo + "') " +
				"order by z.Nomer DESC";

		return db.rawQuery(sqlString, null);    	
	}

	public static ArrayList<String> RequestFilesList(SQLiteDatabase db, String rasporyazhenieNaOtgruzku_IDRRef) {

		ArrayList<String> filesList = new ArrayList<String>();
		Cursor cursor = null;

		try {

			String sqlString = "select _id, Put " +
					"from RasporyazhenieNaOtgruzku_Phayly z " +
					"where _RasporyazhenieNaOtgruzku_IDRRef = " + rasporyazhenieNaOtgruzku_IDRRef;

			cursor = db.rawQuery(sqlString, null);

			if(cursor != null && cursor.moveToFirst()) {

				do {

					filesList.add(cursor.getString(1));
				}
				while(cursor.moveToNext());
			}
		}
		finally {

			if( cursor != null && !cursor.isClosed() ) {

				cursor.close();
			}
		}
		return filesList;
	}

	public static int get_id( Cursor cursor ) {

		return cursor.getInt(cursor.getColumnIndex( ID ));
	}

	public static String getIDRRef( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( IDRREF )));
	}
	
	public static String getKontragentID( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( KONTRAGENT )));
	}

	public static java.util.Date getData( Cursor cursor ) {

		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex( DATA )));
	}

	public static String getNomer( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( NOMER ));
	}

	public static boolean isUploaded( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( VYGRUZHEN ))).compareTo("x'01'") == 0 ? true : false;
	}

	public static String getKontragentNaimanovanie( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KONTRAGENT_NAME ));
	}
	
	public static String getKontragentKod( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KOD ));
	}

	public static String getKommentariy( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KOMMENTARIY ));
	}

	public static double getSumma( Cursor cursor ) {

		return cursor.getDouble(cursor.getColumnIndex( SUMMA ));
	}
}
