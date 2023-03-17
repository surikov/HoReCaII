package sweetlife.android10.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_ClientsList implements ITableColumnsNames {
	public static final int ORDERBY_KOD = 0;
	public static final int ORDERBY_NAIMENOVANIE = 1;
	public static final int ORDERBY_DEN1 = 2;
	public static final int ORDERBY_DEN2 = 3;
	public static final int ORDERBY_DEN3 = 4;
	public static final int ORDERBY_DEN4 = 5;
	public static final int ORDERBY_DEN5 = 6;
	public static final int ORDERBY_DEN6 = 7;
	public static final int ORDERBY_PORYADOK = 8;
	public Cursor Request(SQLiteDatabase db, Integer dayOfWeek, Calendar calendar) {
		//System.out.println("Request_ClientsList start");
		UpdateTempTables.UpdateAllNomenclatureTables(db);
		/*String req = "select"//
				+ "\n\t\t m._id"//
				+ "\n	, k.[Kod] [Kod]"//
				+ "\n	, m.Kontragent [Kontragent]" //
				+ "\n	 ,k.[Naimenovanie] [Naimenovanie], min(m.[Poryadok]) [Poryadok] "//
				+ "\n	 ,max(case when m.[DenNedeli] = 1 then 1 else 0 end) Den1 " //
				+ "\n	 ,max(case when m.[DenNedeli] = 2 then 1 else 0 end) Den2 "//
				+ "\n	 ,max(case when m.[DenNedeli] = 3 then 1 else 0 end) Den3 " //
				+ "\n	 ,max(case when m.[DenNedeli] = 4 then 1 else 0 end) Den4 "//
				+ "\n	 ,max(case when m.[DenNedeli] = 5 then 1 else 0 end) Den5 " //
				+ "\n	 ,max(case when m.[DenNedeli] = 6 then 1 else 0 end) Den6 "//
				+ "\n	 ,mad.[KolichestvoOtkrytykhDogovorov] [KolichestvoOtkrytykhDogovorov] " //
				+ "\n	 ,mad.[KolichestvoDogovorov] [KolichestvoDogovorov] " //
				+ "\n	 ,(select max(BeginDate) from Vizits where Vizits.Client=k.Kod) as lastVizitDate "//
				+ "\n	 ,(select Activity from Vizits where Vizits.Client=k.Kod and BeginDate=(select max(BeginDate) from Vizits where Vizits.Client=k.Kod)) as lastVizitStatus "//
				+ "\n	 ,(select max(EndTime) from Vizits where Vizits.Client=k.Kod) as lastVizitEnd "//
				+ "\n from MarshrutyAgentov m  "//
				//+ "inner join Cur_PolzovatelyVPodrazselenii cpp on m.Agent = cpp._IDRref  " //
				+ "\n\t inner join Kontragenty k on k.[_IDRRef] = m.[Kontragent] "//
				//+ " left join Vizits on Vizits.Client=k.Kod and Vizits.BeginDate=(select max(BeginDate) from Vizits where Vizits.Client=k.Kod)"//
				+ "\n\t inner join  Cur_MarshrutyAgentov_Dogovora mad on m.[Kontragent] = mad.[Kontragent] "//
		;
		if (dayOfWeek != 0) {
			req = req + "\n where m.[DenNedeli] = " + dayOfWeek.toString() + "\n group by m.[Kontragent] \n order by [Poryadok], [Naimenovanie]";
		}
		else {
			req = req + "\n group by m.[Kontragent] order by [Naimenovanie]";
		}*/
		String filterAgent = "";
		//if (ApplicationHoreca.getInstance().currentHRCmarshrut.length() > 0) {
		if (Cfg.isChangedHRC()) {
			//filterAgent = "\n		join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)=\"" + ApplicationHoreca.getInstance().currentHRCmarshrut.trim() + "\"";
			filterAgent = "\n		join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)=\"" + Cfg.selectedOrDbHRC() + "\"";
		}else{

		}
		String whereDay = "";
		String sortDay = "\n	order by neMarshrut,[Naimenovanie]";
		if (calendar == null) {
		}
		else {
			//Calendar calendar=Calendar.getInstance();
			int weekNum = calendar.get(Calendar.WEEK_OF_YEAR);
			int nedelya = 1;
			if (weekNum == 2 * ((int) (weekNum * 0.5))) {
				nedelya = 2;
			}
			whereDay = "\n where nedelya=0 or nedelya=" + nedelya;
			if (dayOfWeek != 0) {
				whereDay = "\n	where m.[DenNedeli] = " + dayOfWeek.toString() + " and (nedelya=0 or nedelya=" + nedelya + ")";
				sortDay = "\n	order by neMarshrut,[Poryadok],[Naimenovanie]";
			}
		}
		String req = "select"//
				+ "\n		m._id as _id"//
				+ "\n		,k.[Kod] [Kod]"//
				+ "\n		,m.Kontragent [Kontragent]"//
				+ "\n		,k.[Naimenovanie] [Naimenovanie]"//
				+ "\n		, min(m.[Poryadok]) [Poryadok]"// 
				+ "\n		,max(case when m.[DenNedeli] = 1 then 1 else 0 end) Den1"// 
				+ "\n		,max(case when m.[DenNedeli] = 2 then 1 else 0 end) Den2"// 
				+ "\n		,max(case when m.[DenNedeli] = 3 then 1 else 0 end) Den3"// 
				+ "\n		,max(case when m.[DenNedeli] = 4 then 1 else 0 end) Den4"// 
				+ "\n		,max(case when m.[DenNedeli] = 5 then 1 else 0 end) Den5"// 
				+ "\n		,max(case when m.[DenNedeli] = 6 then 1 else 0 end) Den6"// 
				+ "\n		,mad.[KolichestvoOtkrytykhDogovorov] [KolichestvoOtkrytykhDogovorov]"// 
				+ "\n		,mad.[KolichestvoDogovorov] [KolichestvoDogovorov]"// 
				+ "\n		,(select max(BeginDate) from Vizits where Vizits.Client=k.Kod) as lastVizitDate"// 
				+ "\n		,(select Activity from Vizits where Vizits.Client=k.Kod and BeginDate"//
				+ "\n			=(select max(BeginDate) from Vizits where Vizits.Client=k.Kod)) as lastVizitStatus"// 
				+ "\n		,(select max(EndTime) from Vizits where Vizits.Client=k.Kod) as lastVizitEnd"// 
				+ "\n		,0 as neMarshrut"//
				+ "\n	from MarshrutyAgentov m"//	
				+ "\n		inner join Kontragenty k on k.[_IDRRef] = m.[Kontragent]"// 
				+ "\n		inner join	Cur_MarshrutyAgentov_Dogovora mad on m.[Kontragent] = mad.[Kontragent]"//
				+ filterAgent//
				+ whereDay//
				+ "\n	group by m.[Kontragent]"// 
				+ "\n union"//
				+ "\n select"//
				+ "\n			k._id as _id"//
				+ "\n			,k.Kod as Kod"//
				+ "\n			,k._idrref as Kontragent"//
				+ "\n			,'(не в маршруте)' || k.[Naimenovanie] as Naimenovanie"//
				+ "\n			,100 as Poryadok"//
				+ "\n			,0 as Den1"//
				+ "\n			,0 as Den2"//
				+ "\n			,0 as Den3"//
				+ "\n			,0 as Den4"//
				+ "\n			,0 as Den5"//
				+ "\n			,0 as Den6"//
				+ "\n			,0 as KolichestvoOtkrytykhDogovorov"//
				+ "\n			,0 as KolichestvoDogovorov"//
				+ "\n			,0 as lastVizitDate"//
				+ "\n			,0 as lastVizitStatus"//
				+ "\n			,0 as lastVizitEnd"//
				+ "\n			,1 as neMarshrut"//
				+ "\n	from kontragenty k"//
				+ "\n	join (select golovnoykontragent"//
				+ "\n		from MarshrutyAgentov m"//
				+ "\n		join kontragenty k1 on k1._idrref=m.kontragent"//
				+ "\n		) k2 on k._idrref=k2.golovnoykontragent"//
				+ "\n	where k._idrref not in (select kontragent from MarshrutyAgentov group by kontragent)"//
				+ "\n	group by k._idrref"//
				+ sortDay//
				+ "\n	";
		/*if (ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim().toUpperCase().equals("HRC00")) {
			req = "select"//
					+ "\n		m._id as _id"//
					+ "\n		,k.[Kod] [Kod]"//
					+ "\n		,m.Kontragent [Kontragent]"//
					+ "\n		,k.[Naimenovanie] [Naimenovanie]"//
					+ "\n		,0 [Poryadok]"// 
					+ "\n		,0 Den1"// 
					+ "\n		,0 Den2"// 
					+ "\n		,0 Den3"// 
					+ "\n		,0 Den4"// 
					+ "\n		,0 Den5"// 
					+ "\n		,0 Den6"// 
					+ "\n		,1 [KolichestvoOtkrytykhDogovorov]"// 
					+ "\n		,1 [KolichestvoDogovorov]"// 
					+ "\n		,'2001-01-01' as lastVizitDate"// 
					+ "\n		,'' as lastVizitStatus"// 
					+ "\n		,'2001-01-01' as lastVizitEnd"// 
					+ "\n		,0 as neMarshrut"//
					+ "\n	from MarshrutyAgentov m"//	
					+ "\n		inner join Kontragenty k on k.[_IDRRef] = m.[Kontragent]"// 
					+ "\n		inner join	Cur_MarshrutyAgentov_Dogovora mad on m.[Kontragent] = mad.[Kontragent]"// 
					+ whereDay//
					+ "\n	group by m.[Kontragent]"// 
					+ sortDay//
					+ "\n	";
		}*/
		//System.out.println("Request_ClientsList " + req);
		Cursor c = db.rawQuery(req, null);
		//System.out.println("done Request_ClientsList: " + c);
		return c;
	}
	public static boolean getClientVisitDone(Cursor cursor) {
		String vizitEnd = null;
		try {
			String lastVizitEnd=cursor.getString(cursor.getColumnIndex("lastVizitEnd"));
			//System.out.println("lastVizitEnd "+lastVizitEnd);
			if(!lastVizitEnd.equals("0")){
			vizitEnd = DateTimeHelper.UIDateString(DateTimeHelper.SQLDateToDate(lastVizitEnd));}
		}
		catch (Throwable t) {
			//
		}
		if (DateTimeHelper.UIDateString(new Date()).equals(vizitEnd)) {
			return true;
		}
		else {
			return false;
		}
	}
	public static boolean getClientVisitStartedNotFinished(Cursor cursor) {
		String vizitDate = null;
		vizitDate = cursor.getString(cursor.getColumnIndex("lastVizitDate"));
		String vizitStatus = cursor.getString(cursor.getColumnIndex("lastVizitStatus"));
		if (vizitDate != null && vizitStatus == null) {
			return true;
		}
		else {
			return false;
		}
	}
	public static String getClientName(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex(NAIMENOVANIE));
		if (name.startsWith("(не в маршруте)")) {
			return name;
		}
		//String vizitDate=cursor.getString(cursor.getColumnIndex("lastVizitDate"));
		String vizitDate = null;
		try {
			vizitDate = DateTimeHelper.UIDateString(DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex("lastVizitDate"))));
		}
		catch (Throwable t) {
			//
		}
		if (vizitDate == null) {
			vizitDate = "нет визитов";
		}
		String vizitStatus = cursor.getString(cursor.getColumnIndex("lastVizitStatus"));
		if (vizitStatus == null) {
			vizitStatus = "не завершён";
		}
		return (name + " (" + vizitDate + ": " + vizitStatus + ")");
	}
	public static String getClientCode(Cursor cursor) {
		return (cursor.getString(cursor.getColumnIndex(KOD)));
	}
	public static int getMo(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_1));
	}
	public static int getTh(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_2));
	}
	public static int getWd(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_3));
	}
	public static int getCh(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_4));
	}
	public static int getFr(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_5));
	}
	public static int getSt(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DEN_6));
	}
	public static ArrayList<Integer> getRouteList(SQLiteDatabase db, String clientID) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select [Den1], [Den2], [Den3], [Den4], [Den5], [Den6] " + "from Cur_MarshrutyAgentov where [Kontragent] = " + clientID, null);
			return getRouteListArray(cursor);
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	private static ArrayList<Integer> getRouteListArray(Cursor cursor) {
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		if (cursor.moveToFirst()) {
			do {
				if (cursor.getInt(0) == 1) {
					resultList.add(Calendar.MONDAY);
				}
				if (cursor.getInt(1) == 1) {
					resultList.add(Calendar.TUESDAY);
				}
				if (cursor.getInt(2) == 1) {
					resultList.add(Calendar.WEDNESDAY);
				}
				if (cursor.getInt(3) == 1) {
					resultList.add(Calendar.THURSDAY);
				}
				if (cursor.getInt(4) == 1) {
					resultList.add(Calendar.FRIDAY);
				}
				if (cursor.getInt(5) == 1) {
					resultList.add(Calendar.SATURDAY);
				}
			}
			while (cursor.moveToNext());
		}
		return resultList;
	}
	public static Integer getOpenContactsCount(Cursor cursor) {
		return new Integer(cursor.getInt(cursor.getColumnIndex(KOLICHESTVO_OTKRYTYKH_DOGOVOROV)));
	}
	public static Integer getContactsCount(Cursor cursor) {
		return new Integer(cursor.getInt(cursor.getColumnIndex(KOLICHESTVO_DOGOVOROV)));
	}
	public static String getClientID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(KONTRAGENT)));
	}
}
