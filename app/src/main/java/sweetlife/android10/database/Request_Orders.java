package sweetlife.android10.database;

import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.Hex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_Orders {

	//private  static final String cash      = "x'B8A71648BE9E99D3492DAB9257E5D773'";
	//private  static final String none_cash = "x'838D51B55D9490754FB77F3D5FE02C1E'";
	//private  static final String cash_memo = "x'AB638B5B4E5DAB774EDC12B2542FFFED'";

	public static Cursor Request( SQLiteDatabase db, String dateFrom, String dateTo) { 

		String sql = "select zp.[_id], date(zp.[Data]), zp.[Nomer], " +
		"k.[Naimenovanie] [KontragentNaimenovanie], " +
		"zp.[TipOplaty], zp.[SummaDokumenta], date(zp.[DataOtgruzki]), zp.[Kommentariy] " +
		
		"from [ZakazPokupatelya] zp " +       
		
		"inner join Kontragenty k on zp.[Kontragent] = k._IDRRef " +       
		
		"where zp.[PometkaUdaleniya] != x'01' and date(zp.[Data]) >= '" + dateFrom + 
		"' and date(zp.[Data]) <= '" + dateTo + "'";

		return db.rawQuery(sql, null);   	  
	}

	public static String getDate( Cursor cursor ) {

		return cursor.getString(1);
	}

	public static String getNumber( Cursor cursor ) {

		return cursor.getString(2);
	}

	public static String getKontragent( Cursor cursor ) {

		return cursor.getString(3);
	}

	public static String getTypePayment( Cursor cursor ) {

		String paymentType = Hex.encodeHex(cursor.getBlob(4)); 

		if( paymentType.toUpperCase().contains(Cfg.tip_nalichnie.toUpperCase()) ) {

			return "Наличная";
		}

		if( paymentType.toUpperCase().contains(Cfg.tip_beznal.toUpperCase()) ) {

			return "Безналичная";
		}

		if( paymentType.toUpperCase().contains(Cfg.tip_tovcheck.toUpperCase()) ) {

			return "Товарный чек";
		}

		return "Неизвестно";
	}

	public static double getDocumentSumm( Cursor cursor ) {

		return cursor.getDouble(5);
	}

	public static String getDateShipment( Cursor cursor ) {

		return cursor.getString(6);
	}

	public static String getComments( Cursor cursor ) {

		return cursor.getString(7);
	}
}
