package sweetlife.android10.database;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_Bids implements ITableColumnsNames {
	private static String getRequestStrng() {
		return "select"//
				+ " distinct zp._id"//
				+ " ,zp._IDRRef"// 
				+ " ,zp.[Nomer]"// 
				+ " ,zp.[Proveden]"//
				+ " ,zp.[DataOtgruzki] [DataOtgruzki]"//
				+ " ,zp.[DogovorKontragenta]"//
				+ " ,zp.[Kommentariy]"//
				+ " ,zp.[Kontragent] "//
				+ " ,zp.[SummaDokumenta] [Summa]"//
				+ " ,zp.[TipOplaty]"//
				+ " ,kntr.[Naimenovanie] [KontragentNaimenovanie] "//
				+ " ,tiO.[Poryadok] [TipOplatyPoryadok]"//
				+ " ,zp.[Data] "//
				+ " ,zp.[Otvetstvennyy]"//
				+ " ,zp.[Sebestoimost]"//
				+ " ,dk.[Naimenovanie] [DogovorKontragentaNaimenovanie] " //
				+ " ,kntr.[Kod] [Kod] " //
				+ " from [ZayavkaPokupatelyaIskhodyaschaya] zp " //
				+ " inner join MarshrutyAgentov m on zp.[Kontragent] = m.[Kontragent] "//
				+ " inner join Kontragenty kntr on m.[Kontragent] = kntr.[_idrref] "// 
				+ " left join TipyOplaty tiO on zp.[TipOplaty] = tiO._IDRRef "// 
				+ " left join DogovoryKontragentov dk on zp.[DogovorKontragenta] = dk.[_IDRRef] "//
		;
	}
	public static Cursor Request(SQLiteDatabase db, String client, String date) {
		String sqlString = getRequestStrng() + " where zp.[Kontragent] = " + client + " and date(zp.[Data]) = date('" + date + "')";
		//System.out.println("Request_Bids.Request "+sqlString);
		return db.rawQuery(sqlString, null);
	}
	public static Cursor RequestPeriod(SQLiteDatabase db, String dateFrom, String dateTo, boolean onlyUploaded) {
		
		String sql = getRequestStrng() + " where date(zp.[Data]) >= date('" + dateFrom + "') and date(zp.[Data]) <= date('" + dateTo + "')";
		//System.out.println("Request_Bids.RequestPeriod "+sql);
		if (onlyUploaded) {
			sql = sql + " and zp.[Proveden] == x'00'";
		}
		//System.out.println("Request_Bids.RequestPeriod "+sql);
		return db.rawQuery(sql, null);
	}
	public static double getBidsAmount(SQLiteDatabase db, String date) {
		String sqlStr = "select sum(SummaDokumenta) from ZayavkaPokupatelyaIskhodyaschaya zp where date(zp.[Data]) = date('" + date + "')";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				return cursor.getDouble(0);
			}
			return 0;
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static int getTipOplatyPoryadok(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(TIP_OPLATY_PORYADOK));
	}
	public static boolean isProveden(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(PROVEDEN))).compareTo("x'01'") == 0 ? true : false;
	}
	public static String getTipOplaty(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(TIP_OPLATY)));
	}
	public static double getSumma(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(SUMMA));
	}
	public static double getSebestoimost(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(SEBESTOIMOST));
	}
	public static int get_id(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(ID));
	}
	public static String getIDRRef(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(IDRREF)));
	}
	public static java.util.Date getData(Cursor cursor) {
		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex(DATA)));
	}
	public static java.util.Date getDataOtgruzki(Cursor cursor) {
		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex(SHIPPING_DATE)));
	}
	public static String getNomer(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(NOMER));
	}
	public static String getKontragentNaimanovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(KONTRAGENT_NAME));
	}
	public static String getKommentariy(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(KOMMENTARIY));
	}
	public static String getKontragentID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(KONTRAGENT)));
	}
	public static String getKontragentKod(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(KOD));
	}
	public static String getDogovorKontragenta(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(DOGOVORKONTRAGENTA)));
	}
	public static String getDogovorKontragentaName(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(DOGOVOR_KONTRAGENTA_NAME));
	}
}
