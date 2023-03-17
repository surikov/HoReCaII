package sweetlife.android10.database;

import java.util.Calendar;

import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.DateTimeHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Requests {
	public static boolean CanDoCashPayments(SQLiteDatabase db, String podrazdelenieID) {
		String sqlStr = "select count(*) from DostupnostPlatezheyNalichnymiDlyaPodrazdeleniy " //
				+ "where Podrazdelenie = "// 
				+ podrazdelenieID;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				if (cursor.getInt(0) == 0) {
					return false;
				}
				return true;
			}
			return false;
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static String ___getTPCode(SQLiteDatabase db
			//, String agentID
			) {
		//System.out.println("getTPCode");
		String kod = "000000";
		Cursor cursor = null;
		try {
			/*String sql = "select Kod from PhizicheskieLica pl "// 
					+ " inner join "//
					+ " ( " //
					+ "   select plp.[PhizLico] PhizLicoID, max(date(Period))"// 
					+ " from PhizLicaPolzovatelya plp "//
					+ "   inner join Polzovateli p on p.[_IDRRef]=plp.[Polzovatel] "// 
					//+ "   where  date(plp.DataKon)>date() and date(Period) <= date() and  p.[_IDRRef]= "// 
					+ "   where   date(Period) <= date() and  p.[_IDRRef]= "//
					+ agentID //
					+ " ) "//
					+ " pol on pl.[_IDRREf] = pol.[PhizLicoID]"//
			;*/
			String sql = "select PhizicheskieLica.kod"//
					+ "\n	from PhizLicaPolzovatelya"//
					+ "\n		join PhizicheskieLica on PhizicheskieLica._IDRREf = PhizLicaPolzovatelya.PhizLico"//
					+ "\n		join Polzovateli on Polzovateli._IDRRef=PhizLicaPolzovatelya.Polzovatel"//
					+ "\n		join cur_users on trim(cur_users.name)=trim(Polzovateli.kod)"//
					+ "\n	where date(PhizLicaPolzovatelya.Period) <= date()"//
					+ "\n		and date(PhizLicaPolzovatelya.DataKon) >= date()"//
					+ "\n	order by PhizLicaPolzovatelya.Period desc"//
			;
			//System.out.println(sql);
			if(db==null){
				//System.out.println("getTPCode: db is null");
				return "not found";
			}
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				//System.out.println("read kod");
				kod = cursor.getString(0);
			}
			//System.out.println("kod is '" + kod + "'");
			//Bough b=Auxiliary.fromCursor(db.rawQuery("select plp.[PhizLico] PhizLicoID, max(date(Period)) from PhizLicaPolzovatelya plp    inner join Polzovateli p on p.[_IDRRef]=plp.[Polzovatel]    where   date(Period) <= date() and  p.[_IDRRef]= x'97C100304885BA0D11DBB4FC2938C56C'", null));
			//System.out.println(b.dumpXML());
			//System.out.println("done");
			return kod;
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static String ___getTPfio(SQLiteDatabase db
			//, String agentID
			) {
		//System.out.println("getTPfio");
		Cursor cursor = null;
		try {
			/*String sql = "select naimenovanie from PhizicheskieLica pl "//
					+ " inner join "//
					+ " ( " //
					+ "   select plp.[PhizLico] PhizLicoID, max(date(Period))"// 
					+ " from PhizLicaPolzovatelya plp "//
					+ "   inner join Polzovateli p on p.[_IDRRef]=plp.[Polzovatel] "// 
					//+ "   where  date(plp.DataKon)>date() and date(Period) <= date() and  p.[_IDRRef]= "// 
					+ "   where   date(Period) <= date() and  p.[_IDRRef]= "//
					+ agentID //
					+ " ) "//
					+ " pol on pl.[_IDRREf] = pol.[PhizLicoID]"//
			;*/
			String sql = "select PhizicheskieLica.naimenovanie"//
					+ "\n	from PhizLicaPolzovatelya"//
					+ "\n		join PhizicheskieLica on PhizicheskieLica._IDRREf = PhizLicaPolzovatelya.PhizLico"//
					+ "\n		join Polzovateli on Polzovateli._IDRRef=PhizLicaPolzovatelya.Polzovatel"//
					+ "\n		join cur_users on trim(cur_users.name)=trim(Polzovateli.kod)"//
					+ "\n	where date(PhizLicaPolzovatelya.Period) <= date()"//
					+ "\n		and date(PhizLicaPolzovatelya.DataKon) >= date()"//
					+ "\n	order by PhizLicaPolzovatelya.Period desc"//
			;
			//System.out.println(sql);
			//System.out.println(Auxiliary.fromCursor(db.rawQuery(sql, null)).dumpXML());
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			}
			return "[не найден]";
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static String getContractCodeByID(SQLiteDatabase db, String ContractID) {
		Cursor cursor = null;
		try {
			String sql = "select Kod from DogovoryKontragentov_strip where _IDRRef = " + ContractID;
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			}
			return "";
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static long daysBetween(Calendar startDate, Calendar endDate) {
		if (startDate.after(endDate)) {
			return -daysBetween(endDate, startDate);
		}
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}
	//static boolean IsSyncronizationDateLaterState=false;
	//static boolean IsSyncronizationDateLaterUpdated=false;
	public static boolean _IsSyncronizationDateLater(int days) {
		try{
			//
		}catch(Throwable t){
			t.printStackTrace();
		}
		return true;
	}
	public static boolean IsSyncronizationDateLater(int days) {
		/*if(IsSyncronizationDateLaterUpdated){
			System.out.println("IsSyncronizationDateLaterState already "+IsSyncronizationDateLaterState);
			return IsSyncronizationDateLaterState;
		}*/
		//if(days==0)return true;
		Calendar syncCalendar = LogHelper.getLastSuccessfulUpdate();
		syncCalendar.set(Calendar.HOUR_OF_DAY, 0);
		syncCalendar.set(Calendar.MINUTE, 0);
		syncCalendar.set(Calendar.SECOND, 0);
		syncCalendar.set(Calendar.MILLISECOND, 0);
		//LogHelper.debug("IsSyncronizationDateLater " + days + " (last is " + syncCalendar.getTime() + ")");
		if (syncCalendar != null) {
			//Calendar today=Calendar.getInstance();
			//long diff=daysBetween(syncCalendar,today);
			//
			Calendar today = DateTimeHelper.getOnlyDateInfo(Calendar.getInstance());
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			//LogHelper.debug("today is  " + today.getTime());
			//int year = syncCalendar.get(Calendar.YEAR);
			//today.roll(Calendar.DAY_OF_YEAR, days);
			today.add(Calendar.DAY_OF_YEAR, days);
			//LogHelper.debug("shift for "+days+" is  " + today.getTime());
			//today.set(Calendar.YEAR, year);
			//LogHelper.debug("now " + today.getTime() + " compare to " + syncCalendar.getTime() + " difference is " + today.compareTo(syncCalendar));
			if (today.compareTo(syncCalendar) > 0) {
				//LogHelper.debug("IsSyncronizationDateLater true");
				//IsSyncronizationDateLaterState=true;
				//IsSyncronizationDateLaterUpdated=true;
				//return IsSyncronizationDateLaterState;
				return true;
			}
		}
		//LogHelper.debug("IsSyncronizationDateLater false");
		//IsSyncronizationDateLaterState=false;
		//IsSyncronizationDateLaterUpdated=true;
		//return IsSyncronizationDateLaterState;
		return false;
		/*
				String sqlLog = "select max(date(dateadded)) from " + LoggingOpenHelper.LOG_TABLE_NAME + 
				" where message like '%" + LogHelper.LOG_MESSAGE_DELTA_PARSE + "%' and type like '%" + 
				LogHelper.LOG_TYPE_SUCCESS + "%'";

				Cursor cursor = null; 

				try {

					cursor = ApplicationHoreca.getInstance().getLogDataBase().rawQuery(sqlLog, null);

					if( cursor.moveToFirst() ) {

						Calendar syncCalendar= Calendar.getInstance();
						syncCalendar.setTime(DateTimeHelper.SQLDateToDate(cursor.getString(0)));
						syncCalendar = DateTimeHelper.getOnlyDateInfo( syncCalendar );

						Calendar today = DateTimeHelper.getOnlyDateInfo(Calendar.getInstance());
						today.roll(Calendar.DAY_OF_YEAR, days);

						if( today.compareTo(syncCalendar) <= 0 ) {

							return false;
						} 
					}
				}
				finally {

					if( cursor != null && !cursor.isClosed() ) {

						cursor.close();
					}
				}

				return true;*/
	}
	public static void UpdateCurPolzovatelyVPodrazselenii(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists Cur_PolzovatelyVPodrazselenii";
		String sqlCreate = "create table Cur_PolzovatelyVPodrazselenii (_id integer primary key asc , [_IDRRef] blob, " + "[Kod] nvarchar(100), [Naimenovanie] nvarchar(200), " + " [Podrazdelenie] blob, [PodrazdelenieNaimenovanie] nvarchar(100), PodrazdelenieKod nvarchar(100) )";
		String sqlInsert = "insert into Cur_PolzovatelyVPodrazselenii("//
				+ "\n			[_IDRRef], [Kod], [Naimenovanie], " //
				+ "\n			[Podrazdelenie], [PodrazdelenieNaimenovanie], [PodrazdelenieKod]"//
				+ "\n			) "//
				+ "\n	select pp._IDRRef, pp.Kod, pp.Naimenovanie, " //
				+ "\n			pp.Podrazdelenie, pd.Naimenovanie [PodrazdelenieNaimenovanie], pd.Kod [PodrazdelenieKod] "//
				+ "\n	from Polzovateli pp"//
				+ "\n	inner join (" //
				+ "\n		select Name from Cur_Users where Type = 2 "//
				+ "\n		) sn on trim(pp.[Kod]) = sn.Name " //
				//		"inner join NastroykiPolzovateley_0 np on cast(np.[Kod] as integer) = 2 " + 
				//		"inner join NastroykiPolzovateley_1 npd on np.[_IDRRef] = npd.Nastroyka and npd.[Polzovatel] = pp._IDRRef " +            
				+ "\n	inner join Podrazdeleniya pd on pd.[_IDRRef] = pp.Podrazdelenie";
		//System.out.println("UpdateCurPolzovatelyVPodrazselenii " + sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX if not exists IX_Cur_PolzovatelyVPodrazselenii on Cur_PolzovatelyVPodrazselenii (_IDRRef)");
	}
	public static String getNomenclatureNameFromArtikul(SQLiteDatabase db, String artikul) {
		String sqlStr = "select Naimenovanie from Nomenklatura where Artikul = " + artikul;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			}
			return "";
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public static String getKontragentyNaimenovaniePoKod(SQLiteDatabase db, int kod) {
		String sqlStr = "select Naimenovanie from Kontragenty where Kod = " + kod;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			}
			return "";
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
