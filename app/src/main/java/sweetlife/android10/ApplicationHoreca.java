package sweetlife.android10;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import android.app.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import org.acra.*;
//import org.acra.annotation.*;
//import androidx.multidex.MultiDexApplication;

import reactive.ui.Auxiliary;
import sweetlife.android10.data.common.Agent;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.orders.ZayavkaPokupatelya;
import sweetlife.android10.database.DataBaseOpenHelper;
import sweetlife.android10.database.Requests;
import sweetlife.android10.log.*;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.Hex;
import tee.binding.Bough;

/*
@ReportsCrashes(
//		formKey = "dEVMSndGOWRTMEVGeGNjUVIxWUdOMXc6MQ",
//formKey = "dENQSU9vX1pNTkVZOEd2ZzJXR1g4QlE6MQ",
formKey = "dEdNMU1LY0NVdkQycDV4emxMVmlXM3c6MQ"//
//, mailTo = "tablet@swlife.nnov.ru"//
, customReportContent = { ReportField.APP_VERSION_NAME//
		, ReportField.DEVICE_ID//
		, ReportField.ANDROID_VERSION//
		, ReportField.APP_VERSION_CODE //
		, ReportField.PHONE_MODEL //
		, ReportField.CUSTOM_DATA //
		, ReportField.STACK_TRACE //
})*/
public class ApplicationHoreca
		//extends androidx.multidex.MultiDexApplication
		extends Application
		// implements ILocationChange
{
	//private SQLiteDatabase mLogDB = null;
	//public static String currentHRCmarshrut = "";
	//public static String currentIDmarshrut = "";
	//public static String currentKodPodrazdelenia = "";
	private static ApplicationHoreca mInstance;
	//public boolean modeAndroid6 = true;
	private SQLiteDatabase mDB = null;
	private boolean mCRdisabledFirstTimeShow = false;
	//private GPSInfo mGPSInfo = null;
	//private SWLifeGpsService mGPSService = null;
	private Logger mLogger = null;
	private LogHandler mLogHandler = null;
	private ArrayList<Agent> mAgents = null;
	private Agent mCurrentAgent = null;
	private Calendar mShippingDate = null;
	private ClientInfo mClient = null;

	//public static ZayavkaPokupatelya lastZayavkaPokupatelya=null;


	//boolean firstPointInsert = false;
	public static ApplicationHoreca getInstance(){
		return mInstance;
	}

	/*public static String hrcSelectedRoute() {
		if (currentHRCmarshrut.length() > 1) {
			return currentHRCmarshrut;
		}
		else {
			return Cfg.databaseHRC();
		}
	}*/
	@Override
	public void onCreate(){
		//System.out.println(this.getClass().getCanonicalName() + ".onCreate");
		//ACRA.init(this);
		super.onCreate();
		mInstance = this;
		//InitializeLogs();
		//InitializeDB();
		//FillAgentsInfo();
		//StartAndBindService();
		//getGPSInfo();
		AsyncTaskManager.getInstance();
		mShippingDate = Calendar.getInstance();
		mShippingDate.setTime(NomenclatureBasedDocument.nextWorkingDate(mShippingDate));
		LogHandler.cleanUpDebugLog();
		//testOptimization();
		/*
		String sql="select count(price._id),number,vladelec,naimenovanie from price left join podrazdeleniya p on p._idrref=price.vladelec group by vladelec";
		Bough b=Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println(b.dumpXML());
		*/
		/*DecimalFormat df= new DecimalFormat("000,000,000");
		String base="000,000,000";
		
		String s="12345";
		int nn=Integer.parseInt(s);
		String f=base.substring(base.length()-s.length());
		System.out.println(f);
		DecimalFormat defr= new DecimalFormat(f);
		System.out.println(defr.format(nn));
		*/
		//test();
		//System.out.println("firebase test start");
	}

	String hx(int n){
		if(n == 0){
			return "ff";
		}
		if(n == 1){
			return "99";
		}
		return "33";
	}

	void test(){
		int i = 0;
		for(int r = 0; r < 3; r++){
			for(int g = 0; g < 3; g++){
				for(int b = 0; b < 3; b++){
					if(!(r == g && g == b)){
						//System.out.println("else if (n == " + i + ")color = '#" + hx(r) + hx(g) + hx(b) + "';");
						//System.out.println(hx(r) + hx(g) + hx(b));
						i++;
					}
				}
			}
		}
	}

	public void InitializeDB(){
		//System.out.println(this.getClass().getCanonicalName() + ".InitializeDB");
		if(mDB == null || !mDB.isOpen()){
			//System.out.println(Settings.getInstance().toString());
			//System.out.println(Settings.getInstance().getTABLET_DATABASE_FILE());
			//System.out.println("DataBaseOpenHelper " + DataBaseOpenHelper.DATABASE_NAME);
			DataBaseOpenHelper openHelper = new DataBaseOpenHelper(this);
			mDB = openHelper.getWritableDatabase();
		}
	}

	public SQLiteDatabase getDataBase(){
		if(mDB == null || !mDB.isOpen()){
			InitializeDB();
		}
		return mDB;
	}

	private void DestroyDatabase(){
		if(mDB != null){
			if(mDB.isOpen()){
				mDB.close();
			}
			mDB = null;
		}
	}

	public Agent getCurrentAgent(){
		if(mCurrentAgent == null){
			if(mAgents == null){
				FillAgentsInfo();
			}
			return mAgents.get(0);
		}
		return mCurrentAgent;
	}

	/*
	void fillTargetCondition() {
		targetCondition = "";
		//targetCondition = " and baza=" + ISklady.HORECA_ID;
		try {
			String sql = "select Podrazdeleniya.IspolzovanieLogisticheskogoMarshruta from Podrazdeleniya"//
					+ "\n\t join Polzovateli on Polzovateli.podrazdelenie=Podrazdeleniya._idrref"// 
					+ "\n\t where Polzovateli._idrref=" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr() + ";";
			Cursor cursor=mDB.rawQuery(sql, null);
			cursor.moveToFirst();
			String s=Hex.encodeHex(cursor.getBlob(1)); 
			cursor.close();
		}
		catch (Throwable t) {
			LogHelper.debug(this.getClass().getCanonicalName() + " fillTargetCondition: " + t.getMessage());
		}
	}*/
	void readAgentConfig(){
		System.out.println("readAgentConfig");
		//try {
		//String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/Horeca2.xml")));
		try{
			//Bough b = Bough.parseXML(xml);
			//System.out.println(b.dumpXML());
			String userKey = Settings.cfg().child("userKey").value.property.value().trim();
			String updateKey = Settings.cfg().child("updateKey").value.property.value().trim();
			if(userKey.length() > 1 && updateKey.length() > 1){
				mDB.execSQL("update android set kod='" + updateKey + "' where trim(kod)!='12-';");
				mDB.execSQL("update cur_users set name='" + userKey + "' where type=2;");
			}
			System.out.println("readAgentConfig " + userKey + "/" + updateKey);
		}catch(Throwable t){
			t.printStackTrace();
		}
		/*}
		catch (Throwable t) {
			t.printStackTrace();
		}*/
	}

	public void FillAgentsInfo(){
		LogHelper.debug(this.getClass().getCanonicalName() + ".FillAgentsInfo");
		try{
			readAgentConfig();
			Requests.UpdateCurPolzovatelyVPodrazselenii(mDB);
			if(mAgents != null){
				mAgents.clear();
			}else{
				mAgents = new ArrayList<Agent>();
			}
			//Cursor cursor = mDB.rawQuery("select * from Cur_PolzovatelyVPodrazselenii", null);
			//LogHelper.debug(this.getClass().getCanonicalName() + ".adjustColumn");
			sweetlife.android10.utils.DatabaseHelper.adjustColumn(mDB, "blob null", "ИспользованиеЛогистическогоМаршрута", "ИспользованиеЛогистическогоМаршрута", "IspolzovanieLogisticheskogoMarshruta", "Podrazdeleniya", 1);
		/*try {
			System.out.println("add IspolzovanieLogisticheskogoMarshruta");
			Cursor tt = mDB.rawQuery("select count(_id) from _RelationsFieldsHelper where sqliteName='IspolzovanieLogisticheskogoMarshruta'", null);
			tt.moveToFirst();
			int cnt = tt.getInt(0);
			if (cnt < 1) {
				System.out.println("IspolzovanieLogisticheskogoMarshruta not found");
				tt = mDB.rawQuery("select _id from _RelationsTableHelper where sqliteName='Podrazdeleniya'", null);
				tt.moveToFirst();
				int id=tt.getInt(0);
				mDB.rawQuery("insert into _RelationsFieldsHelper"//
						+ " (_tableId,[1cName],[Update1cName],sqliteName, sqlFieldType)"//
						+ " values ("+id+",'ИспользованиеЛогистическогоМаршрута','ИспользованиеЛогистическогоМаршрута','IspolzovanieLogisticheskogoMarshruta',1)"//
				, null);
			}
			mDB.rawQuery("alter table Podrazdeleniya add column IspolzovanieLogisticheskogoMarshruta blob null", null);
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
		}*/
			Bough tb = Auxiliary.fromCursor(mDB.rawQuery("select kod as kod from android where trim(kod)!='12-';", null));
			//System.out.println(tb.dumpXML());
			String updateKey = "!";
			if(tb.children.size() > 0){
				updateKey = tb.children.get(0).child("kod").value.property.value();
			}
			String sql = "select"//
					+ "\n pp._id,pp._IDRRef, pp.Kod, pp.Kod, pp.Podrazdelenie, pd.Naimenovanie [PodrazdelenieNaimenovanie], pd.Kod [PodrazdelenieKod],pd.etoGruppa as supervisor"//
					//+ "\n\t ,pd.IspolzovanieLogisticheskogoMarshruta as skladPodrazdeleniya"//
					+ "\n ,(select IspolzovanieLogisticheskogoMarshruta "//
					+ "\n 	from Podrazdeleniya  "//
					+ "\n 	where Podrazdeleniya._IDRRef=pp.Podrazdelenie or Podrazdeleniya.roditel=pp.Podrazdelenie "//
					+ "\n 	order by IspolzovanieLogisticheskogoMarshruta desc "//
					+ "\n 	limit 1)  as skladPodrazdeleniya "//
					+ "\nfrom Polzovateli pp "//
					+ "\ninner join (select Name from Cur_Users where Type = 2 ) sn on trim(pp.[Kod]) = trim(sn.Name)"//
					+ "\ninner join Podrazdeleniya pd on pd.[_IDRRef] = pp.Podrazdelenie"//
					;
			//System.out.println(sql);
			Cursor cursor = mDB.rawQuery(sql, null);
			if(cursor.moveToFirst()){
				do{
					Agent agent = new Agent();
					agent.set_id(cursor.getInt(0));
					agent.setAgentID(cursor.getBlob(1));
					agent.setAgentIDstr(Hex.encodeHex(cursor.getBlob(1)));
					agent.setAgentKod(cursor.getString(2));
					agent.setAgentName(cursor.getString(3));
					agent.setPodrazdelenieID(cursor.getBlob(4));
					agent.setPodrazdelenieIDstr(Hex.encodeHex(cursor.getBlob(4)));
					agent.setPodrazdelenieName(cursor.getString(5));
					agent.setPodrazdelenieKod(cursor.getString(6));
					agent.setAgentSuperVisorStr(Hex.encodeHex(cursor.getBlob(7)));
					agent.updateKod = updateKey;
					//agent.skladPodrazdeleniya="x'00'";
					try{
						agent.setSkladPodrazdeleniya(
								//.skladPodrazdeleniya =
								Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex("skladPodrazdeleniya"))));
					}catch(Throwable t){
						agent.setPodrazdelenieIDstr("unknow");
						//t.printStackTrace();
						System.out.println("agent.setPodrazdelenieIDstr(\"unknow\");");
					}
					mAgents.add(agent);
					System.out.println("agent.getSkladPodrazdeleniya(): " + agent.getSkladPodrazdeleniya());
					System.out.println("agent.getAgentIDstr(): " + agent.getAgentIDstr());
					System.out.println("agent.getAgentKod(): " + agent.getAgentKod());
					System.out.println("agent.getAgentName(): " + agent.getAgentName());
					System.out.println("agent.getPodrazdelenieKod(): " + agent.getPodrazdelenieKod());
					System.out.println("agent.getPodrazdelenieName(): " + agent.getPodrazdelenieName());
					System.out.println("agent.getAgentSupervisorStr(): " + agent.getAgentSupervisorStr());
				}
				while(cursor.moveToNext());
			}else{
				//System.out.println("empty");
				String s = "select trim(name) as n from Cur_Users where Type = 2";
				Bough b = Auxiliary.fromCursor(mDB.rawQuery(s, null));
				Agent agent = new Agent();
				agent.setAgentKod(b.child("row").child("n").value.property.value());
				agent.updateKod = updateKey;
				agent.set_id(0);
				agent.setAgentID(new byte[]{});
				agent.setAgentIDstr("");
				agent.setAgentName(b.child("row").child("n").value.property.value());
				agent.setPodrazdelenieID(new byte[]{});
				agent.setPodrazdelenieIDstr("");
				agent.setPodrazdelenieName("[нет подразделения]");
				agent.setPodrazdelenieKod("");
				agent.setAgentSuperVisorStr("");
				agent.setPodrazdelenieIDstr("");
				agent.setSkladPodrazdeleniya("");
				mAgents.add(agent);
			}
			cursor.close();
			cursor = null;
			if(mAgents.size() < 1){
				Agent agent = new Agent();
				agent.setAgentName("[error]");
				mAgents.add(agent);
			}
		}catch(Throwable t){
			LogHelper.debug(this.getClass().getCanonicalName() + ".FillAgentsInfo " + t.getMessage());
		}
	}

	/*
	@Override
	public void OnLocationUpdate(Location location) {
		//System.out.println(location);
		if (firstPointInsert) {
			LogHelper.debug(this.getClass().getCanonicalName() + " first OnLocationUpdate " + new java.util.Date(location.getTime()));
		}
		firstPointInsert = false;
		//if (mGPSInfo != null) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(location.getTime());
		getGPSInfo()
		//mGPSInfo
				.insertPoint(calendar, location.getLatitude(), location.getLongitude());
		//LogHelper.debug(this.getClass().getCanonicalName() + " gps "+calendar.getTime());
		//}
	}
	*/
	public boolean isCRdisabledFirstTimeShow(){

		return mCRdisabledFirstTimeShow;
	}

	public void setCRdisabledFirstTimeShow(boolean cRdisabledFirstTimeShow){
		mCRdisabledFirstTimeShow = cRdisabledFirstTimeShow;
	}

	/*private void InitializeLogs() {
		System.out.println(this.getClass().getCanonicalName() + ".InitializeLogs");
		if (mLogDB == null) {
			//LogHelper.debug(this.getClass().getCanonicalName() + ".InitializeLogs");
			LoggingOpenHelper logOpenHelper = null;
			if (mLogDB == null || !mLogDB.isOpen()) {
				logOpenHelper = new LoggingOpenHelper(this);
				mLogDB = logOpenHelper.getWritableDatabase();
			}
			if (mLogger == null) {
				mLogger = Logger.getLogger("com.swlife");
				mLogHandler = new LogHandler(mLogDB);
				mLogger.addHandler(mLogHandler);
			}
		}
		LogHelper.debug(this.getClass().getCanonicalName() + " start logging, debug is " + Settings.DEBUG_MODE);
	}*/
	/*public Logger getLogger() {
		if (mLogger == null) {
			InitializeLogs();
		}
		return mLogger;
	}*/
	/*private void DestroyLogs() {
		LogHelper.debug(this.getClass().getCanonicalName() + " stop logging");
		if (mLogger != null && mLogHandler != null) {
			mLogger.removeHandler(mLogHandler);
			mLogger = null;
		}
		if (mLogDB != null) {
			mLogDB.close();
			mLogDB = null;
		}
	}
	public SQLiteDatabase getLogDataBase() {
		if (mLogDB == null || !mLogDB.isOpen()) {
			InitializeLogs();
		}
		return mLogDB;
	}*/
	public void CleanWhenExit(){
		//StopGPSLog();
		//StopAndUnbindServiceIfRequired();
		//mGPSInfo = null;
		DestroyDatabase();
		//DestroyWakeLock();
		//DestroyLogs();
		//sweetlife.horeca.monitor.SQLexec.end();
	}

	public Calendar getShippingDate(){
		return mShippingDate;
	}

	public void setShippingDate(Calendar c){
		mShippingDate.setTime(c.getTime());
	}

	public void setShippingDate(int year, int month, int day){
		mShippingDate.set(year, month, day, 0, 0, 2);
	}

	public ClientInfo getClientInfo(){
		if(mClient == null){
			String sql = "select _IDRRef as id, Kod, Naimenovanie from kontragenty join MarshrutyAgentov on MarshrutyAgentov.Kontragent=kontragenty._idrref order by kontragenty.Naimenovanie limit 1;";
			Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(data.dumpXML());
			String id = data.child("row").child("id").value.property.value();
			ClientInfo clientInfo = new ClientInfo(ApplicationHoreca.getInstance().getDataBase(), "x'" + id + "'");
			setClientInfo(clientInfo);
		}
		return mClient;
	}

	public void setClientInfo(ClientInfo n){

		mClient = n;
		Cfg.refreshSkidkiKontragent(ApplicationHoreca.getInstance().getClientInfo().getKod(), " date( 'now' ) ");
	}

	void testOptimization(){
		//System.out.println("testOptimization");
		String sql = " 	  select n._id 														 " //
				+ " 	  	,n.[_IDRRef] 													 " //
				+ " 	  	,n.[Artikul] 													 " //
				+ " 	  	,n.[Naimenovanie] "/* || ' (склад ' || case (select sklad from AdresaPoSkladam_last where nomenklatura=n._idrref  and baza=x'AAFFF658AE67DCE94696B419219D8E1C' order by sklad  limit 1) when x'967E50505450303011DA00E63437669D' then 8 when x'967E50505450303011DA00E6343766A0' then 10 when x'970E001438C58CB411DAFF8AE294A2D7' then 12 when X'82AE002264FA89D811E0E3C13D6A574C' then 14 else '?' end || ')' as Naimenovanie													 " //
											+ " 	  	,n.[OsnovnoyProizvoditel] 													 " //
											+ " 	  	,p.Naimenovanie as ProizvoditelNaimenovanie 													 " //
											+ " 	  	,CenyNomenklaturySklada.Cena as Cena 													 " //
											+ " 	  	,0 as Skidka 													 " //
											+ " 	  	,0 as CenaSoSkidkoy 													 " //
											+ " 	  	,x'00' as VidSkidki 													 " //
											+ " 	  	,eho.[Naimenovanie] as [EdinicyIzmereniyaNaimenovanie] 													 " //
											+ " 	  	,VelichinaKvantovNomenklatury.Kolichestvo as MinNorma 													 " //
											+ " 	  	,ei.Koephphicient as [Koephphicient] 													 " //
											+ " 	   	,ei._IDRRef as [EdinicyIzmereniyaID] 													 " //
											+ " 	   	,n.Roditel as Roditel 													 " //
											+ " 	  	,case when ifnull(nk1.ProcentSkidkiNacenki,nk2.ProcentSkidkiNacenki)>0													 " //
											+ " 	  		then 0												 " //
											+ " 	  		else (1.0+ifnull(n1.nacenka,ifnull(n2.nacenka,ifnull(n3.nacenka,ifnull(n4.nacenka,n5.nacenka))))/100.0)*TekuschieCenyOstatkovPartiy.Cena												 " //
											+ " 	  		end as MinCena 												 " //
											+ " 	  	,(1.0+const.MaksNacenkaCenyPraysa/100.0)*CenyNomenklaturySklada.Cena as MaxCena 													 " //
											+ " 	  	,TekuschieCenyOstatkovPartiy.cena as BasePrice 													 " //
											+ " 	  	,Prodazhi.Stoimost/Prodazhi.kolichestvo as LastPrice 													 " //
											+ " 	  	,ifnull(nk1.ProcentSkidkiNacenki,nk2.ProcentSkidkiNacenki) as Nacenka 													 " //
											+ " 	  	,ZapretSkidokTov.Individualnye as ZapretSkidokTovIndividualnye 													 " //
											+ " 	  	,ZapretSkidokTov.Nokopitelnye as ZapretSkidokTovNokopitelnye 													 " //
											+ " 	  	,ZapretSkidokTov.Partner as ZapretSkidokTovPartner 													 " //
											+ " 	  	,ZapretSkidokTov.Razovie as ZapretSkidokTovRazovie 													 " //
											+ " 	  	,ZapretSkidokTov.Nacenki as ZapretSkidokTovNacenki 													 " //
											+ " 	  	,ZapretSkidokProizv.Individualnye as ZapretSkidokProizvIndividualnye 													 " //
											+ " 	  	,ZapretSkidokProizv.Nokopitelnye as ZapretSkidokProizvNokopitelnye 													 " //
											+ " 	  	,ZapretSkidokProizv.Partner as ZapretSkidokProizvPartner 													 " //
											+ " 	  	,ZapretSkidokProizv.Razovie as ZapretSkidokProizvRazovie 													 " //
											+ " 	  	,ZapretSkidokProizv.Nacenki as ZapretSkidokProizvNacenki 													 " //
											+ " 	  	,ifnull(fx1.FixCena,fx2.FixCena) as FiksirovannyeCeny 													 " //
											+ " 	  	,(select max(ProcentSkidkiNacenki) from SkidkaPartneraKarta													 " //
											+ " 	  			where PoluchatelSkidki=n.[_IDRRef]											 " //
											+ " 	  			and date(period)<=date(parameters.dataOtgruzki) 											 " //
											+ " 	  			and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) ) 											 " //
											+ " 	  		as SkidkaPartneraKarta												 " //
											+ " 	  	,(select max(ProcentSkidkiNacenki) from NakopitelnyeSkidki													 " //
											+ " 	  			where PoluchatelSkidki=parameters.kontragent											 " //
											+ " 	  			and date(period)<=date(parameters.dataOtgruzki) 											 " //
											+ " 	  			and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) ) 											 " //
											+ " 	  		as NakopitelnyeSkidki												 " //
											+ " 	  	,Prodazhi.period as LastSell 													 " //
											*/
				//+ " 	  	,(select sklad from AdresaPoSkladam_last where nomenklatura=n._idrref and baza=x'A756BEA77AB71E2F45CD824C4AB4178F' and Traphik=x'00' limit 1) as skladKazan 													 " //
				//+ " 	  	,(select sklad from AdresaPoSkladam_last where nomenklatura=n._idrref and baza=x'AAFFF658AE67DCE94696B419219D8E1C' and Traphik=x'00' limit 1) as skladHoreca 													 " //
				//+ " 	  	,ZO1.zapret as zo1,ZO2.zapret as zo2,ZO3.zapret as zo3,ZO4.zapret as zo4 													 " //
				//+ " 	 	,eho.ves as vesedizm													 " //
				+ " 	   	from Nomenklatura_sorted n 													 " //
				+ " 	  	cross join Consts const 													 " //
				+ " 	  	cross join (select 													 " //
				+ " 	  			'2013-06-25' as dataOtgruzki 											 " //
				+ " 	  			,x'896618A90562E07411E218E37E2B4CD4' as kontragent 											 " //
				+ " 	  			,x'97C100304885BA0D11DBB4FC2938C56C' as polzovatel 											 " //
				+ " 	  		) parameters 												 " //
				/*
				+ " cross join DogovoryKontragentov_strip  "//
				+ " 	on DogovoryKontragentov_strip.vladelec=x'896618A90562E07411E218E37E2B4CD4' "//
				
				
				+ " 	  	 cross join Prodazhi_last Prodazhi on" //
				+ " 	  				Prodazhi.nomenklatura=n.[_IDRRef] 										 " //
				+ " 	  				and Prodazhi.period>=date('2013-03-26') 										 " //
				+ " 	  				and Prodazhi.period<=date('2013-06-24') 										 " //
				+ " 					and Prodazhi.DogovorKontragenta=DogovoryKontragentov_strip._IDRref"//
				*/ + " 	  	cross join TekuschieCenyOstatkovPartiy_strip TekuschieCenyOstatkovPartiy on TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef] 													 " //
				+ " 	  	cross join Polzovateli on Polzovateli._idrref=parameters.polzovatel 													 " //
				+ " 	  	cross join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie 													 " //
				//+ " 	  	cross join kontragenty on kontragenty._idrref=parameters.kontragent 													 " //
				+ " 	  	cross join CenyNomenklaturySklada_last CenyNomenklaturySklada on CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 													 " //
				/*
				+ " 	  	left join Prodazhi_last Prodazhi on Prodazhi.DogovorKontragenta in (select DogovoryKontragentov_strip._IDRref from DogovoryKontragentov_strip where DogovoryKontragentov_strip.vladelec=parameters.kontragent ) 													 " //
				+ " 	  				and Prodazhi.nomenklatura=n.[_IDRRef] 										 " //
				+ " 	  				and Prodazhi.period>=date('2013-03-26') 										 " //
				+ " 	  				and Prodazhi.period<=date('2013-06-24') 										 " //
				*/ + " 	  	cross join Prodazhi_last Prodazhi on Prodazhi.DogovorKontragenta in (select DogovoryKontragentov_strip._IDRref from DogovoryKontragentov_strip where DogovoryKontragentov_strip.vladelec=parameters.kontragent ) 													 " //
				+ " 	  				and Prodazhi.nomenklatura=n.[_IDRRef] 										 " //
				+ " 	  				and date(Prodazhi.period)>date('2013-03-25') 										 " //
				+ " 	  				and date(Prodazhi.period)<date('2013-06-23') 										 " //
				/*
				+ " 	  	left join EdinicyIzmereniya_strip eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef 													 " //
				+ " 	  	left join EdinicyIzmereniya_strip ei on n.EdinicaDlyaOtchetov = ei._IDRRef 													 " //
				+ " 	  	left join NacenkiKontr nk1 on nk1.PoluchatelSkidki=parameters.kontragent 													 " //
				+ " 	  			and nk1.Period=(select max(Period) from NacenkiKontr 											 " //
				+ " 	  				where PoluchatelSkidki=parameters.kontragent 										 " //
				+ " 	  					and date(period)<=date(parameters.dataOtgruzki) and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) and podrazdelenie=p1._idrref									 " //
				+ " 	  				) 										 " //
				+ " 	  	left join (select ProcentSkidkiNacenki from NacenkiKontr 													 " //
				+ " 	  			join kontragenty on kontragenty._idrref=x'896618A90562E07411E218E37E2B4CD4'											 " //
				+ " 	  			where PoluchatelSkidki=kontragenty.GolovnoyKontragent											 " //
				+ " 	  				and period=(										 " //
				+ " 	  					select max(period)									 " //
				+ " 	  					from NacenkiKontr									 " //
				+ " 	  					join kontragenty on kontragenty._idrref=x'896618A90562E07411E218E37E2B4CD4'									 " //
				+ " 	  					where PoluchatelSkidki=kontragenty.GolovnoyKontragent and date(DataOkonchaniya)>=date('2013-06-25')									 " //
				+ " 	  					)									 " //
				+ " 	  			limit 1											 " //
				+ " 	  		) nk2												 " //
				+ " 	  	left join ZapretSkidokTov on ZapretSkidokTov.Nomenklatura=n.[_IDRRef] 													 " //
				+ " 	  			and ZapretSkidokTov.Period=(select max(Period) from ZapretSkidokTov 											 " //
				+ " 	  				where Nomenklatura=n.[_IDRRef] 										 " //
				+ " 	  					and date(period)<=date(parameters.dataOtgruzki) 									 " //
				+ " 	  				) 										 " //
				+ " 	  	left join ZapretSkidokProizv on ZapretSkidokProizv.Proizvoditel=n.[OsnovnoyProizvoditel] 													 " //
				+ " 	  				and ZapretSkidokProizv.Period=(select max(Period) from ZapretSkidokProizv 										 " //
				+ " 	  					where Proizvoditel=n.[OsnovnoyProizvoditel] 									 " //
				+ " 	  						and date(period)<=date(parameters.dataOtgruzki) 								 " //
				+ " 	  					) 									 " //
				+ " 	  	left join Podrazdeleniya p2  on p1.roditel=p2._idrref 													 " //
				+ " 	  	left join Podrazdeleniya p3  on p2.roditel=p3._idrref 													 " //
				+ " 	  	left join Podrazdeleniya p4  on p3.roditel=p4._idrref 													 " //
				+ " 	  	left join ZapretyNaOtguzku ZO1 on ZO1.Nomenklatura=n.[_idrref] and ZO1.ObjectZapreta=p1._idrref and ZO1.Period=(select max(Period) from ZapretyNaOtguzku z where z.Nomenklatura=n.[_idrref] and z.ObjectZapreta=p1._idrref)													 " //
				+ " 	  	left join ZapretyNaOtguzku ZO2 on ZO2.Nomenklatura=n.[_idrref] and ZO2.ObjectZapreta=p2._idrref and ZO2.Period=(select max(Period) from ZapretyNaOtguzku z where z.Nomenklatura=n.[_idrref] and z.ObjectZapreta=p2._idrref)													 " //
				+ " 	  	left join ZapretyNaOtguzku ZO3 on ZO3.Nomenklatura=n.[_idrref] and ZO3.ObjectZapreta=p3._idrref and ZO3.Period=(select max(Period) from ZapretyNaOtguzku z where z.Nomenklatura=n.[_idrref] and z.ObjectZapreta=p3._idrref)													 " //
				+ " 	  	left join ZapretyNaOtguzku ZO4 on ZO4.Nomenklatura=n.[_idrref] and ZO4.ObjectZapreta=p4._idrref and ZO4.Period=(select max(Period) from ZapretyNaOtguzku z where z.Nomenklatura=n.[_idrref] and z.ObjectZapreta=p4._idrref)													 " //
				+ " 	  	left join MinimalnyeNacenkiProizvoditeley_1 n1  on n1.podrazdelenie=p1._idrref 													 " //
				+ " 	  				and n1.NomenklaturaProizvoditel_2=n._idrref 										 " //
				+ " 	  				and n1.period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 										 " //
				+ " 	  					where podrazdelenie=p1._idrref 									 " //
				+ " 	  					and NomenklaturaProizvoditel_2=n._idrref 									 " //
				+ " 	  					) 									 " //
				+ " 	  	left join MinimalnyeNacenkiProizvoditeley_1 n2  on n2.podrazdelenie=p2._idrref 													 " //
				+ " 	  				and n2.NomenklaturaProizvoditel_2=n._idrref 										 " //
				+ " 	  				and n2.period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 										 " //
				+ " 	  					where podrazdelenie=p2._idrref 									 " //
				+ " 	  					and NomenklaturaProizvoditel_2=n._idrref 									 " //
				+ " 	  					) 									 " //
				+ " 	  	left join MinimalnyeNacenkiProizvoditeley_1 n3  on n3.podrazdelenie=p3._idrref 													 " //
				+ " 	  				and n3.NomenklaturaProizvoditel_2=n._idrref 										 " //
				+ " 	  				and n3.period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 										 " //
				+ " 	  					where podrazdelenie=p3._idrref 									 " //
				+ " 	  					and NomenklaturaProizvoditel_2=n._idrref 									 " //
				+ " 	  					) 									 " //
				+ " 	  	left join MinimalnyeNacenkiProizvoditeley_1 n4  on n4.podrazdelenie=p4._idrref 													 " //
				+ " 	  				and n4.NomenklaturaProizvoditel_2=n._idrref 										 " //
				+ " 	  				and n4.period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 										 " //
				+ " 	  					where podrazdelenie=p4._idrref 									 " //
				+ " 	  					and NomenklaturaProizvoditel_2=n._idrref 									 " //
				+ " 	  					) 									 " //
				+ " 	  	left join MinimalnyeNacenkiProizvoditeley_1 n5  on n5.podrazdelenie=X'00000000000000000000000000000000' 													 " //
				+ " 	  				and n5.NomenklaturaProizvoditel_2=n._idrref 										 " //
				+ " 	  				and n5.period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 										 " //
				+ " 	  					where podrazdelenie=X'00000000000000000000000000000000' 									 " //
				+ " 	  					and NomenklaturaProizvoditel_2=n._idrref 									 " //
				+ " 	  					) 									 " //
				+ " 	  	left join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef 													 " //
				+ " 	  	left join VelichinaKvantovNomenklatury_strip VelichinaKvantovNomenklatury on VelichinaKvantovNomenklatury.nomenklatura=n.[_IDRRef] 													 " //
				+ " 	  	left join FiksirovannyeCeny_actual fx1 on fx1.DataOkonchaniya=( 													 " //
				+ " 	  					select max(DataOkonchaniya) from FiksirovannyeCeny_actual 									 " //
				+ " 	  						where PoluchatelSkidki=parameters.kontragent 								 " //
				+ " 	  						and date(period)<=date(parameters.dataOtgruzki) 								 " //
				+ " 	  						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) 								 " //
				+ " 	  						and Nomenklatura=n.[_IDRRef] 								 " //
				+ " 	  				) and fx1.PoluchatelSkidki=parameters.kontragent 										 " //
				+ " 	  				and fx1.Nomenklatura=n.[_IDRRef] 										 " //
				+ " 	  	left join FiksirovannyeCeny_actual fx2 on fx2.DataOkonchaniya=( 													 " //
				+ " 	  					select max(DataOkonchaniya) from FiksirovannyeCeny_actual 									 " //
				+ " 	  						where PoluchatelSkidki=kontragenty.GolovnoyKontragent 								 " //
				+ " 	  						and date(period)<=date(parameters.dataOtgruzki) 								 " //
				+ " 	  						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) 								 " //
				+ " 	  						and Nomenklatura=n.[_IDRRef] 								 " //
				+ " 	  				) and fx2.PoluchatelSkidki=kontragenty.GolovnoyKontragent 										 " //
				+ " 	  				and fx2.Nomenklatura=n.[_IDRRef] 										 " //
				*/ + " 	  where n.TovarPodZakaz=x'00'														 " //
				//+"  and Prodazhi.kolichestvo > 0 "
				//+ " 	  and 	skladHoreca is not null  and ifnull(zo1,x'00')<>x'01' and ifnull(zo2,x'00')<>x'01' and ifnull(zo3,x'00')<>x'01' and ifnull(zo4,x'00')<>x'01'													 " //
				+ " 	  limit 275 offset 0														 ";
		try{
			//System.out.println("1");
			Cursor c = mDB.rawQuery(sql, null);
			//System.out.println("2");
			//System.out.println("first " + c.moveToFirst());
			//tee.binding.Bough b = Auxiliary.fromCursor(c);
			//System.out.println("3");
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
}
