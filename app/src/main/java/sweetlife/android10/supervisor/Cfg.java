package sweetlife.android10.supervisor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import android.app.*;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.content.*;
import android.os.*;
//import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import jxl.format.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.R;
import sweetlife.android10.consts.IAppConsts;
import sweetlife.android10.data.orders.*;
import sweetlife.android10.ui.*;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import com.google.firebase.messaging.*;
import com.google.firebase.*;


public class Cfg{

	public static String skidkaId_x_Gazeta = "914f887f023f24874f33033ac1cacceb".toUpperCase();//' – газета/листовка – не считать при расчете
	public static String skidkaIdNakopitelnaya = "913ad1938af7144e4322ac76b890cfce".toUpperCase();//' – накопительная
	public static String skidkaIdIndividualnaya = "9b6e6ab7e8559cea4d3787ee115c6103".toUpperCase();//' – индивидуальная
	public static String skidkaIdRazovaya = "9f6db59fa1f6542c4c8b9685a6dc516c".toUpperCase();//' – разовая
	public static String skidkaIdPosummeRazovaya = "9838ada09fd4e0364fb8f6233b11912b".toUpperCase();//' – разовая по сумме
	public static String skidkaIdFixirovannaya = "bda5601eb37a75774f39ea16ad78895c".toUpperCase();//' – фиксированная
	public static String skidkaIdBonus = "b5b90ad6b83b804b483e1802fc095631".toUpperCase();//' – бонус
	public static String skidkaIdOldCenovoyeReagirovanie = "99D730123902D40541B2F8954FE1E089".toUpperCase();//' – старое ценовое реагирование
	public static String skidkaIdCenovoyeReagirovanie = "88cadabd4b9935494244b411e0721582".toUpperCase();//' – ценовое реагирование
	public static String skidkaIdAutoReagirovanie = "8f338dd128720fe14038cbfe45e48bd7".toUpperCase();//' – auto ценовое реагирование
	public static String skidkaId_x_PoOtvetstvennove = "b6642d98b55a8d5e48c45c1c3731b72e".toUpperCase();//' – по ответственному – не считать при расчете
	public static String skidkaId_x_Promokod = "a24b2ee11974f13a4fbac0f8a4e35589".toUpperCase();//' – промокод – не считать
	public static String skidkaId_x_Targetnie = "a0c42e5e2beab7e74a98e440d5099464".toUpperCase();//' – таргетные – не считать
	public static String skidkaId_Heroy = "11111111111111111111111111111111".toUpperCase();//' – Герой
	public static String skidkaId_TGCR = "22222222222222222222222222222222".toUpperCase();//' – Герой+ЦР
	public static String skidkaId_Rasprodaja = "a869e5d02faf61e3498c93c59670b2ae".toUpperCase();//' – Распродажа

	public static final String workFolder = "/sdcard/horeca/";

	//public static final String wialonToken = "b59fccaa778dfbf1af3debc7921a17c9F3CCFD40C584F030D602B0829550B5A63A0CF971";
	public static final String keshProgramnihInterfeysov1Ctokenwialon = "6510cc38c1e24baf0a8c1272e7f7ef489694CEF6EFC1B638CBCFAB8DDB510B4EFEA2136F";

	public final static String MAPKIT_API_KEY = "53d587be-32c1-479b-b31e-081102369e30";

	public static final String tip_nalichnie = "x'B8A71648BE9E99D3492DAB9257E5D773'";
	public static final String tip_beznal = "x'838D51B55D9490754FB77F3D5FE02C1E'";
	public static final String tip_tovcheck = "x'AB638B5B4E5DAB774EDC12B2542FFFED'";
	public static final String tip_unknown = "x'00'";

	private static String currentHRCmarshrut = "";
	private static String currentHRC_idrref = "";
	private static String currentKodPodrazdelenia = "";

	private static String lastHRCID = "";
	private static String checkListOwner = "";
	private static Bough checkListOwnerCfg = null;
	//public static final String dbFile = "swlife_database";
	//private static String HRC = null;
	private static String dbHRC = null;
	//private static String routeHRC = null;
	//private static String replacedHRC = null;

	private static Bough territoriesCache = null;
	private static Bough kontragentyCache = null;
	//private static Bough crossKontragentyCache = null;
	//private static Bough crossDogovoraCache = null;
	//private static Bough kontragentyDogovorCache = null;
	private static String podrazdeleniaKod = null;
	private static Vector<CfgKontragentInfo> all;
	private static String hrcimei = null;//"HRCIMEI";//imei
	private static String deviceid = null;//"DEVICEID";//stripimei

	//public static boolean useNewSkidkaCalculation = false;

	public static Vector<String> crGroupCache = new Vector<String>();
	public static Vector<Double> crCenaCache = new Vector<Double>();
	public static Vector<String> crArtikulCache = new Vector<String>();

	public static String currentLogUpdateKey = "None";

	public static String currentFirebaseToken = null;

    /*
        public static String imei="?";
        public static String stripimei="?";
    public static void currentIMEI(Activity a,String hrc){
        TelephonyManager tm = (TelephonyManager)a.getSystemService(a.TELEPHONY_SERVICE);
        stripimei = tm.getDeviceId();
        imei= hrc+":"+stripimei;
    }
    */


	//public static String hrcPersonalPasswordCached = "Hm7LPRvg";//hrc252";//"hrc252";//hrc252/hrc22
	public static String hrcPersonalPasswordCached = "";

	/*public static String hrcPersonalLogin(){
		if(hrcPersonalLoginCached.equals("")){

		}
		return hrcPersonalLoginCached;
	}*/
	public static String hrcPersonalPassword(){
		if(hrcPersonalPasswordCached.equals("")){
			android.content.Context context = ApplicationHoreca.getInstance().getApplicationContext();
			SharedPreferences settings = context.getSharedPreferences(IAppConsts.PREFS_FILE_NAME, 0);
			hrcPersonalPasswordCached = settings.getString(Activity_Login.hrcpasswordName, "");
		}
		return hrcPersonalPasswordCached;
		//return "Molgav1024";
	}

	public static String whoCheckListOwner(){
		if(checkListOwnerCfg == null){
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/HorecaL.xml")));
			try{
				checkListOwnerCfg = Bough.parseXML(xml);
				checkListOwner = checkListOwnerCfg.child("hrc").value.property.value();
			}catch(Throwable t){
				t.printStackTrace();
				checkListOwnerCfg = new Bough();
				checkListOwner = Cfg.DBHRC();
			}
			if(checkListOwnerCfg == null){
				checkListOwnerCfg = new Bough();
			}
		}
		return checkListOwner;
		//return "bot28";
	}


	public static void setCurrentFirebaseToken(final Context context, String token){
		Cfg.currentFirebaseToken = token;
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/IDPlanshet";
		//final String url = "https://service.swlife.ru/hrc120107/ru_RU/hs/Planshet/IDPlanshet";
		final String params = "{\"Данные\":[{\"ТорговыйПредставитель\":\"" + Cfg.whoCheckListOwner() + "\",\"IDПланшета\":\"" + token + "\"}]}";
		System.out.println("setCurrentFirebaseToken " + url);
		System.out.println("params " + params);
		Note msg = new Note();
		new Expect()
				.task.is(new Task(){
			@Override
			public void doTask(){
				try{
					Bough b = Auxiliary.loadTextFromPrivatePOST(url
							, params.getBytes("UTF-8"), 32000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
							, true
					);
					System.out.println(b.dumpXML());
					msg.value(b.child("message").value.property.value());
				}catch(Throwable t){

					t.printStackTrace();
				}
			}
		})
				.afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Результат: " + msg.value(), context);
			}
		})
				.status.is("Ждите...")
				.silentStart();

       /* try {
            Bough b = Auxiliary.loadTextFromPrivatePOST(url
                    , params.getBytes("UTF-8"), 32000, Cfg.hrcPersonalLogin, Cfg.hrcPersonalPassword
                    ,true
            );
            System.out.println(b.dumpXML());
        }catch(Throwable t){
            t.printStackTrace();
        }*/

	}


	public static void requeryFirebaseToken(Context context){//final Task afterDone) {
		//if (Cfg.currentFirebaseToken == null) {
		System.out.println("requeryFirebaseToken start");

		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				FirebaseApp.initializeApp(context);
				FirebaseMessaging.getInstance().getToken()
						.addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>(){
							@Override
							public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task){
								if(!task.isSuccessful()){
									System.out.println(task.getException());
									return;
								}
								String token = task.getResult();
								//System.out.println("requeryFirebaseToken token ====================");
								//cYvgX8ihSMyFeEU_VpPPvZ:APA91bG2osDBV_qyAGb4wzu369Hu1EyaIq7DMqPIm5vu_y35GpnVouSYjBlE2_dOB4B1Us0WPmq8cFGplqX2dx96sKHwDJT3juuLbsCSrIwEuSKZ2Uht6_NkZ34ExG4HEnjUA9okHr29
								Cfg.setCurrentFirebaseToken(context, token);
								//System.out.println(token);
								//afterDone.start();
							}
						});
			}
		}).status.is("Ждите...").silentStart();



            /*com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                    if (!task.isSuccessful()) {
                        System.out.println(task.getException());
                        return;
                    }
                    String token = task.getResult();
                    System.out.println("requeryFirebaseToken token ====================");
                    //cYvgX8ihSMyFeEU_VpPPvZ:APA91bG2osDBV_qyAGb4wzu369Hu1EyaIq7DMqPIm5vu_y35GpnVouSYjBlE2_dOB4B1Us0WPmq8cFGplqX2dx96sKHwDJT3juuLbsCSrIwEuSKZ2Uht6_NkZ34ExG4HEnjUA9okHr29
                    Cfg.setCurrentFirebaseToken( token);
                    System.out.println(token);
                    //afterDone.start();
                }
            });*/
		//} else {
		//afterDone.start();
		//System.out.println("requeryFirebaseToken skip");
		//}
	}

	public static String findFizLicoKod(String hrc){
		String sql = "select l.naimenovanie as name,l.kod as kod from PhizLicaPolzovatelya f \n" +
				"join Polzovateli p on p._idrref=f.polzovatel join PhizicheskieLica l on l._idrref=f.phizlico \n" +
				"where trim(p.kod)='" + hrc + "' order by f.period desc;"//
				;
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String tpCode = data.child("row").child("kod").value.property.value();
		return tpCode;
	}


	public static void currentIMEI(Activity a, String hrc){
	/*try {
		if(deviceid == null) {
			TelephonyManager tm = (TelephonyManager) a.getSystemService(a.TELEPHONY_SERVICE);
			deviceid = ""+tm.getDeviceId();
		}

	}catch(Throwable t){
		deviceid=""+t.getMessage();
	}*/
		if(deviceid == null){
			deviceid = sweetlife.android10.utils.SystemHelper.getDiviceID(a);
		}
		hrcimei = hrc + ":" + deviceid;
	}

	public static int userLevel(String hrc){
		String sql = "select count(*) as cnt from Polzovateli p"//
				+ " join Podrazdeleniya t1 on t1._idrref=p.podrazdelenie"//
				+ " join Podrazdeleniya t2 on t1._idrref=t2.roditel"//
				+ " join Podrazdeleniya t3 on t2._idrref=t3.roditel"//
				+ " where p.kod='" + hrc + "';";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		if(!data.child("row").child("cnt").value.property.value().equals("0")){
			return 2;
		}
		sql = "select count(*) as cnt from Polzovateli p"//
				+ " join Podrazdeleniya t1 on t1._idrref=p.podrazdelenie"//
				+ " join Podrazdeleniya t2 on t1._idrref=t2.roditel"//
				+ " where p.kod='" + hrc + "';";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		if(!data.child("row").child("cnt").value.property.value().equals("0")){
			return 1;
		}
		return 0;
	}

	public static String device_id(){
		if(deviceid == null){
			return "null";
		}else{
			return deviceid;
		}
	}

	public static String hrc_imei(){
		if(hrcimei == null){
			return "hrc987654321";
		}else{
			return hrcimei;
		}
	}

	public static String DBHRC(){
		if(dbHRC == null){
			String sql = "select name as hrc from cur_users where _id=2;";
			Bough user = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			dbHRC = user.child("row").child("hrc").value.property.value();
			/*if(HRC.trim().equals("hrc252")){
				hrcPersonalLogin="hrc252";
				hrcPersonalPassword="hrc25202";
			}else{
				if(HRC.trim().equals("hrc22")){
					hrcPersonalLogin="hrc22";
					hrcPersonalPassword="hrc2202";
				}
			}*/
			//System.out.println("****************"+hrcLogin+"/"+hrcPassword);
		}
		return dbHRC;
	}

	public static void resetHRCmarshrut(String hrc, String polzovatel_idrref, String podraxdelenieKod){
		currentHRCmarshrut = hrc;
		currentHRC_idrref = polzovatel_idrref;
		currentKodPodrazdelenia = podraxdelenieKod;
	}

	public static String polzovatelFIO(String hrc){
		String sql = "select"//
				+ " PhizicheskieLica.naimenovanie as fio"//
				+ " from Polzovateli"//
				+ " left join PhizLicaPolzovatelya on PhizLicaPolzovatelya.polzovatel=Polzovateli._idrref"//
				+ " left join PhizicheskieLica on PhizicheskieLica._idrref=PhizLicaPolzovatelya.phizlico"//
				+ " where Polzovateli.kod='" + hrc + "'"//
				+ " order by PhizLicaPolzovatelya.period desc;"//
				;
		//System.out.println("polzovatelFIO "+sql);
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String fioName = b.child("row").child("fio").value.property.value();
		return fioName;
	}

	//public static void setKodPodrazdelenia(String kod){
	//	 currentKodPodrazdelenia=kod;
	//}
	public static String selectedKodPodrazdelenia(){
		return currentKodPodrazdelenia;
	}

	public static String selectedHRC_idrref(){
		return currentHRC_idrref;
	}

	public static String selectedOrDbHRC(){
		if(currentHRCmarshrut.length() > 1){
			return currentHRCmarshrut;
		}else{
			return Cfg.DBHRC();
		}
	}

	public static boolean isChangedHRC(){
		if(currentHRCmarshrut.length() > 1){
			return true;
		}else{
			return false;
		}
	}


	public static Vector<String> reportNames(String reportKey){
		Vector<String> names = new Vector<String>();
		Auxiliary.createAbsolutePathForFolder(workFolder + "supervisor/reports/" + reportKey + "/parameters");
		Auxiliary.createAbsolutePathForFolder(workFolder + "supervisor/reports/" + reportKey + "/pages");
		String[] pars = new File(workFolder + "/supervisor/reports/" + reportKey + "/parameters").list();
		Arrays.sort(pars);
		for(int i = 0; i < pars.length; i++){
			names.add(pars[i]);
		}
		return names;
	}

	public static String formatMills(double mills, String format){
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = new Date();
		d.setTime((long)mills);
		return f.format(d);
	}

	public static String pathToXML(String reportKey, String instanceKey){
		return workFolder(reportKey) + "/parameters/" + instanceKey + ".xml";
	}

	public static String pathToHTML(String reportKey, String instanceKey){
		return workFolder(reportKey) + "/pages/" + instanceKey + ".html";
	}

	public static String workFolder(String reportKey){
		return Cfg.workFolder + "supervisor/reports/" + reportKey;
	}

	public static int lastSQLiteChanesCount(){
		int rr = 0;
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select changes() as changesCount;", null));
		rr = (int)Numeric.string2double(data.child("row").child("changesCount").value.property.value());
		return rr;
	}

	public static void sendRequestPriceNew(Activity activity, Vector<String> artikuls, int timeout, final Note resultMessage, final Task afterFinishOrCancel){
		final String soapXML = Cfg.composeXMLpriceRenew(artikuls);
		final RawSOAP rr = new RawSOAP().timeout.is(timeout);
		new Expect().status.is(resultMessage).task.is(new Task(){
			@Override
			public void doTask(){

				String url = Settings.getInstance().getBaseURL() + "DanniePoTovaram.1cws";

				System.out.println("url " + url);
				System.out.println("soapXML " + soapXML);

				rr.url.is(url).xml.is(soapXML);
				rr.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				System.out.println("sendRequestPriceNew " + rr.statusCode.property.value() + "/" + rr.exception.property.value());
				if(rr.exception.property.value() != null){
					resultMessage.value(resultMessage.value() + "\n" + "Исключение: " + rr.exception.property.value().getMessage());
					rr.exception.property.value().printStackTrace();
				}else{
					System.out.println("result sendRequestPriceNew " + rr.data.dumpXML());
					if(rr.statusCode.property.value() >= 100 && rr.statusCode.property.value() <= 300){
						Vector<Bough> rows = rr.data.child("soap:Body").child("m:GetResponse").child("m:return").children;
						int r = 0;
						for(int i = 0; i < rows.size(); i++){
							Bough b = rows.get(i);
							int kkk = Cfg.updateMinMaxPriceForArtikul(b.child("Artikul").value.property.value()//
									, b.child("TekuhayaCena").value.property.value().replace(',', '.').replaceAll("\\s+", "")//текущие цены остатков партий
									, b.child("Prais").value.property.value().replace(',', '.').replaceAll("\\s+", "")//цены номенклатуры
									, b.child("MinPorog").value.property.value().replace(',', '.').replaceAll("\\s+", "")//минимальные наценки производителейц
							);
							r = r + kkk;
						}
						resultMessage.value(resultMessage.value() + "\n" + "Всего обновлено строк: " + ((int)r));

					}else{
						resultMessage.value(resultMessage.value() + "\n" + "Ошибка: " + rr.statusCode.property.value() + ": " + rr.statusDescription.property.value() + ", statusCode " + rr.statusCode.property.value());
					}
				}
				afterFinishOrCancel.start();
			}
		}).afterCancel.is(new Task(){
			@Override
			public void doTask(){
				resultMessage.value(resultMessage.value() + "\n" + "Омена");
				afterFinishOrCancel.start();
			}
		}).start(activity);
	}

	public static String composeXMLpriceRenew(Vector<String> artikuls){
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/DanniePoTovaram\" xmlns:ns2=\"DanniePoTovaram\">"//
				+ "\n  <SOAP-ENV:Body>"//
				+ "\n    <ns2:Get>"//
				+ "\n      <ns2:Spisok>";
		for(int i = 0; i < artikuls.size(); i++){
			xml = xml + "\n        <ns1:Str>"//
					+ "\n          <ns1:Artikul>" + artikuls.get(i) + "</ns1:Artikul>"//
					+ "\n          <ns1:NaSklade></ns1:NaSklade>"//
					+ "\n          <ns1:Dostupno></ns1:Dostupno>"//
					+ "\n          <ns1:TekuhayaCena></ns1:TekuhayaCena>"//
					+ "\n          <ns1:Prais></ns1:Prais>"//
					+ "\n          <ns1:MinPorog></ns1:MinPorog>"//
					+ "\n        </ns1:Str>";
		}
		xml = xml + "\n      </ns2:Spisok>"//
				+ "\n      <ns2:Otvetstvenniy>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod() + "</ns2:Otvetstvenniy>"//
				+ "\n    </ns2:Get>"//
				+ "\n  </SOAP-ENV:Body>"//
				+ "\n</SOAP-ENV:Envelope>";
		return xml;
	}

	public static int updateMinMaxPriceForArtikul(String Artikul, String TekuhayaCena, String Prais, String MinPorog){
		System.out.println("updateByRequestNew: " + Artikul + ", " + TekuhayaCena + ", " + Prais + ", " + MinPorog + ".");
		/*ApplicationHoreca.getInstance().getDataBase().execSQL("delete from CenyNomenklaturySklada_last"//
				+ "\n		where _id in ("//
				+ "\n			select CenyNomenklaturySklada_last._id from CenyNomenklaturySklada_last"//
				+ "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
				+ "\n	;");
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from CenyNomenklaturySklada"//
				+ "\n		where _id in ("//
				+ "\n			select CenyNomenklaturySklada._id from CenyNomenklaturySklada_last"//
				+ "\n				join CenyNomenklaturySklada on CenyNomenklaturySklada.period=CenyNomenklaturySklada_last.period and CenyNomenklaturySklada.nomenklatura=CenyNomenklaturySklada_last.nomenklatura"//
				+ "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
				+ "\n	;");
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from TekuschieCenyOstatkovPartiy_strip"//
				+ "\n		where _id in ("//
				+ "\n			select TekuschieCenyOstatkovPartiy_strip._id from TekuschieCenyOstatkovPartiy_strip"//
				+ "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy_strip.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
				+ "\n	;");
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from TekuschieCenyOstatkovPartiy"//
				+ "\n		where _id in ("//
				+ "\n			select TekuschieCenyOstatkovPartiy._id from TekuschieCenyOstatkovPartiy"//
				+ "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
				+ "\n	;");*/
		int r = 0;
		if(Prais.trim().length() > 0){
			r = 1;
			String sql = "update CenyNomenklaturySklada_last"//
					+ "\n		set cena=" + Prais//
					+ "\n		where _id in ("//
					+ "\n			select CenyNomenklaturySklada_last._id from CenyNomenklaturySklada_last"//
					+ "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
					+ "\n	;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			if(Cfg.lastSQLiteChanesCount() < 1){
				sql = "insert into CenyNomenklaturySklada_last (period,nomenklatura,cena)"
						+ " select date('now') as period, nn._idrref as nomenklatura," + Prais + " as cena from nomenklatura nn where artikul='" + Artikul + "' limit 1;";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
			sql = "update CenyNomenklaturySklada"//
					+ "\n		set cena=" + Prais//
					+ "\n		where _id in ("//
					+ "\n			select CenyNomenklaturySklada._id from CenyNomenklaturySklada_last"//
					+ "\n				join CenyNomenklaturySklada on CenyNomenklaturySklada.period=CenyNomenklaturySklada_last.period and CenyNomenklaturySklada.nomenklatura=CenyNomenklaturySklada_last.nomenklatura"//
					+ "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
					+ "\n	;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			if(Cfg.lastSQLiteChanesCount() < 1){
				sql = "insert into CenyNomenklaturySklada (period,nomenklatura,cena)"
						+ " select date('now') as period, nn._idrref as nomenklatura," + Prais + " as cena from nomenklatura nn where artikul='" + Artikul + "' limit 1;";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		}
		if(TekuhayaCena.trim().length() > 0){
			r = 1;
			String sql = "update TekuschieCenyOstatkovPartiy_strip"//
					+ "\n		set cena=" + TekuhayaCena//
					+ "\n		where _id in ("//
					+ "\n			select TekuschieCenyOstatkovPartiy_strip._id from TekuschieCenyOstatkovPartiy_strip"//
					+ "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy_strip.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
					+ "\n	;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			if(Cfg.lastSQLiteChanesCount() < 1){
				sql = "insert into TekuschieCenyOstatkovPartiy_strip (nomenklatura,cena)"
						+ " select nn._idrref as nomenklatura," + TekuhayaCena + " as cena from nomenklatura nn where artikul='" + Artikul + "' limit 1;";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
			sql = "update TekuschieCenyOstatkovPartiy"//
					+ "\n		set cena=" + TekuhayaCena//
					+ "\n		where _id in ("//
					+ "\n			select TekuschieCenyOstatkovPartiy._id from TekuschieCenyOstatkovPartiy"//
					+ "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
					+ "\n	;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			if(Cfg.lastSQLiteChanesCount() < 1){
				r = 1;
				sql = "insert into TekuschieCenyOstatkovPartiy (nomenklatura,cena)"
						+ " select nn._idrref as nomenklatura," + TekuhayaCena + " as cena from nomenklatura nn where artikul='" + Artikul + "' limit 1;";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		}
		if(MinPorog.trim().length() > 0){
			String sql = "update MinimalnyeNacenki"//
					+ "\n		set nacenka=" + MinPorog//
					+ "\n		where _id in ("//
					+ "\n			select MinimalnyeNacenki._id from MinimalnyeNacenki"//
					+ "\n				join nomenklatura on nomenklatura._idrref=MinimalnyeNacenki.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
					+ "\n	;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}
		return r;
	}

	private static void fillTerritoriesCache(){
		//System.out.println("fillTerritoriesCache " + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().toLowerCase());
		/*
		String sql = "select hrc.kod as hrc,children.naimenovanie as territory,children.kod as kod"//
				+ "\n	from Polzovateli currentPolzovatel"//
				+ "\n		join Cur_Users on trim(currentPolzovatel.naimenovanie)=trim(Cur_Users.Name)"//
				+ "\n		join Podrazdeleniya currentPodrazd on currentPodrazd._idrref=currentPolzovatel.Podrazdelenie"//
				+ "\n		join Podrazdeleniya children on children.Roditel=currentPodrazd._idrref or children._idrref=currentPodrazd._idrref"//
				+ "\n		join Polzovateli hrc on children._idrref=hrc.Podrazdelenie"//
				+ "\n	where hrc.pometkaudaleniya=x'00'"//
				+ "\n	group by hrc._idrref"//
				+ "\n	order by children.naimenovanie,hrc.kod";
		*/
		String sql = "select ifnull(p5.naimenovanie,'') || ' / ' || ifnull(p4.naimenovanie,'') || ' / ' || ifnull(p3.naimenovanie,'') || ' / ' || ifnull(p2.naimenovanie,'') || ' / ' || ifnull(p1.naimenovanie,'') as territory"//
				+ "\n		,hrc1.kod as hrc"//
				+ "\n		,p1.kod as kod"//
				+ "\n		,hrc1._idrref as polzovatelID"//
				+ "\n		,p1.etogruppa as etogruppa"//
				+ "\n	from Podrazdeleniya p1"//
				+ "\n		left join Podrazdeleniya p2 on p1.Roditel=p2._idrref"//
				+ "\n		left join Podrazdeleniya p3 on p2.Roditel=p3._idrref"//
				+ "\n		left join Podrazdeleniya p4 on p3.Roditel=p4._idrref"//
				+ "\n		left join Podrazdeleniya p5 on p4.Roditel=p5._idrref"//
				+ "\n		left join Polzovateli hrc1 on p1._idrref=hrc1.Podrazdelenie"//
				+ "\n		left join Polzovateli hrc2 on p2._idrref=hrc2.Podrazdelenie"//
				+ "\n		left join Polzovateli hrc3 on p3._idrref=hrc3.Podrazdelenie"//
				+ "\n		left join Polzovateli hrc4 on p4._idrref=hrc4.Podrazdelenie"//
				+ "\n		left join Polzovateli hrc5 on p5._idrref=hrc5.Podrazdelenie"//
				+ "\n		left join Cur_Users cu1 on trim(hrc1.kod)=trim(cu1.Name)"//
				+ "\n		left join Cur_Users cu2 on trim(hrc2.kod)=trim(cu2.Name)"//
				+ "\n		left join Cur_Users cu3 on trim(hrc3.kod)=trim(cu3.Name)"//
				+ "\n		left join Cur_Users cu4 on trim(hrc4.kod)=trim(cu4.Name)"//
				+ "\n		left join Cur_Users cu5 on trim(hrc5.kod)=trim(cu5.Name)"//
				+ "\n	where hrc is not null and (cu1.name is not null or cu2.name is not null or cu3.name is not null or cu4.name is not null)"//
				+ "\n	    and hex(p1.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(p2.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(p3.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(p4.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(p5.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(hrc1.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(hrc2.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(hrc3.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(hrc4.pometkaudaleniya)<>'01'"//
				+ "\n	    and hex(hrc5.pometkaudaleniya)<>'01'"//
				+ "\n	group by p1._idrref"//
				+ "\n	order by p5.naimenovanie, p4.naimenovanie, p3.naimenovanie, p2.naimenovanie, p1.naimenovanie"//
				;
		if(ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().toLowerCase().equals("hrc00")){
			sql = "select ifnull(p3.naimenovanie,'') || ' / ' || ifnull(p2.naimenovanie,'') || ' / ' || ifnull(p1.naimenovanie,'') as territory"//
					+ "\n		,hrc1.kod as hrc"//
					+ "\n		,p1.kod as kod"//
					+ "\n		,hrc1._idrref as polzovatelID"//
					+ "\n		,p1.etogruppa as etogruppa"//
					+ "\n	from Podrazdeleniya p1"//
					+ "\n		left join Podrazdeleniya p2 on p1.Roditel=p2._idrref"//
					+ "\n		left join Podrazdeleniya p3 on p2.Roditel=p3._idrref"//
					+ "\n		left join Polzovateli hrc1 on p1._idrref=hrc1.Podrazdelenie"//
					+ "\n		left join Polzovateli hrc2 on p2._idrref=hrc2.Podrazdelenie"//
					+ "\n		left join Polzovateli hrc3 on p3._idrref=hrc3.Podrazdelenie"//
					+ "\n	where hrc1.kod<>''"//
					+ "\n	group by p1._idrref"//
					+ "\n	order by p3.naimenovanie, p2.naimenovanie, p1.naimenovanie"//
			;
		}
		territoriesCache = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println("fillTerritoriesCache " + sql);
		//System.out.println(territoriesCache.dumpXML());
		//System.out.println("done territoriesCache");
	}

	/*private static void fillDogovorCache() {
		String sql = "select "//
				+ "\n		d.kod as dogovorKod"//
				+ "\n		,kontragenty.kod as kontragentKod"//
				+ "\n		,kontragenty.naimenovanie as kontragentNaimenovanie"//
				+ "\n		,d.naimenovanie as dogovorNaimenovanie"//
				+ "\n	from kontragenty"//
				+ "\n		join DogovoryKontragentov d on d.vladelec=kontragenty._idrref"//
				+ "\n		join MarshrutyAgentov on Kontragenty._idrref=MarshrutyAgentov.Kontragent"//
				+ "\n	group by d._idrref,kontragenty._idrref"//
				+ "\n	order by kontragenty.naimenovanie,d.naimenovanie"//
		;
		kontragentyDogovorCache = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
	}*/
	private static void fillKontragentyCacheByKod(String kod){
		//System.out.println("Cfg.fillKontragentyCache " + kod);
		podrazdeleniaKod = kod;
		String sql = "select p1.kod as k1,p2.kod as k2,p3.kod as k3, Kontragenty._idrref as _idrref"//
				+ "\n		,kontragenty.kod as kod,kontragenty.naimenovanie as naimenovanie"//
				+ "\n	from kontragenty"//
				+ "\n		join MarshrutyAgentov on Kontragenty._idrref=MarshrutyAgentov.Kontragent"//
				+ "\n		join Podrazdeleniya p1 on p1._idrref=kontragenty.podrazdelenie"//
				+ "\n		left join Podrazdeleniya p2 on p2._idrref=p1.roditel"//
				+ "\n		left join Podrazdeleniya p3 on p3._idrref=p2.roditel"//
				;
		if(ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().toLowerCase().equals("hrc00")){
			//
		}else{
			if(podrazdeleniaKod.length() > 0){
				sql = sql + "\n	where p1.kod='" + podrazdeleniaKod + "' or p2.kod='" + podrazdeleniaKod + "' or p3.kod='" + podrazdeleniaKod + "'"//
				;
			}
		}
		sql = sql + "\n	group by kontragenty.kod"//
				+ "\n	order by kontragenty.naimenovanie"//
		;
		//System.out.println(sql);
		kontragentyCache = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println("done kontragentyCache");
	}

	public static Bough vseKontragenty(){
		String sql = "select p1.kod as k1,p2.kod as k2,p3.kod as k3, Kontragenty._idrref as _idrref"//
				+ "\n		,kontragenty.kod as kod,kontragenty.naimenovanie as naimenovanie,kontragenty.inn as inn"//
				+ "\n	from kontragenty"//
				+ "\n		left join MarshrutyAgentov on Kontragenty._idrref=MarshrutyAgentov.Kontragent"//
				+ "\n		left join Podrazdeleniya p1 on p1._idrref=kontragenty.podrazdelenie"//
				+ "\n		left join Podrazdeleniya p2 on p2._idrref=p1.roditel"//
				+ "\n		left join Podrazdeleniya p3 on p3._idrref=p2.roditel"//
				;
		sql = sql + "\n	group by kontragenty.kod"//
				+ "\n	order by kontragenty.naimenovanie"//
		;
		return Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
	}

	public static Bough territory(){
		if(territoriesCache == null){
			fillTerritoriesCache();
		}
		return territoriesCache;
	}

	public static Bough kontragenty(){
		return kontragentyByKod(ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieKod());
	}

	public static Bough kontragentyForSelectedMarshrut(){
		if(Cfg.isChangedHRC()){
			return kontragentyByKod(selectedKodPodrazdelenia());
		}else{
			return kontragenty();
		}
	}

	public static Bough kontragentyByKod(String kod){
		if(kontragentyCache == null || (!kod.equals(podrazdeleniaKod))){
			fillKontragentyCacheByKod(kod);
		}
		return kontragentyCache;
	}

	public static CfgKontragentInfo kontragentByDogovor(String dogovorKod){
		for(int k = 0; k < crossDogovora().size(); k++){
			for(int d = 0; d < crossDogovora().get(k).dogovora.size(); d++){
				if(dogovorKod.equals(crossDogovora().get(k).dogovora.get(d).kod)){
					return crossDogovora().get(k);
				}
			}
		}
		return null;
	}

	public static CfgKontragentInfo kontragentByNum(int nn){
		int i = 0;
		for(int k = 0; k < crossDogovora().size(); k++){
			for(int d = 0; d < crossDogovora().get(k).dogovora.size(); d++){
				if(nn == i){
					return crossDogovora().get(k);
				}
				i++;
			}
		}
		return null;
	}

	public static CfgDogovorInfo dogovorByNum(int nn){
		int i = 0;
		for(int k = 0; k < crossDogovora().size(); k++){
			for(int d = 0; d < crossDogovora().get(k).dogovora.size(); d++){
				if(nn == i){
					return crossDogovora().get(k).dogovora.get(d);
				}
				i++;
			}
		}
		return null;
	}

	public static CfgDogovorInfo dogovorByKod(String dogovorKod){
		for(int k = 0; k < crossDogovora().size(); k++){
			for(int d = 0; d < crossDogovora().get(k).dogovora.size(); d++){
				if(dogovorKod.equals(crossDogovora().get(k).dogovora.get(d).kod)){
					return crossDogovora().get(k).dogovora.get(d);
				}
			}
		}
		return null;
	}

	/*
	select
	--k1.naimenovanie,k2.naimenovanie
	hex(k1._idrref) as kliend
	,hex(k1.GolovnoyKontragent) as golovnoy
	--,*
	from kontragenty k1
	--join kontragenty k2 on k1.GolovnoyKontragent=k2._idrref and k1._idrref<>k1.GolovnoyKontragent
	where k1.kod=122043




	select
		plzvtl.naimenovanie
		,pdr1.naimenovanie,hex(pdr1._idrref) as podrid1
		,pdr2.naimenovanie,hex(pdr2._idrref) as podrid2
		,pdr3.naimenovanie,hex(pdr3._idrref) as podrid3
		,pdr4.naimenovanie,hex(pdr4._idrref) as podrid4
		,pdr5.naimenovanie,hex(pdr5._idrref) as podrid5
	from Polzovateli plzvtl
		left join Podrazdeleniya pdr1 on pdr1._idrref=plzvtl.podrazdelenie
		left join Podrazdeleniya pdr2 on pdr2._idrref=pdr1.Roditel
		left join Podrazdeleniya pdr3 on pdr3._idrref=pdr2.Roditel
		left join Podrazdeleniya pdr4 on pdr4._idrref=pdr3.Roditel
		left join Podrazdeleniya pdr5 on pdr5._idrref=pdr4.Roditel
	where plzvtl.kod='supervip_hrc'





	select ss.DataNachala as datastart
				,ss.DataOkonchaniya as dataend
				,ns.Artikul as artikul
				,ss.Znachenie as price
				--,kk.kod as kontragent
				--,pz.kod as polzovatel
				--,pdr.naimenovanie as podrazdelenie
				,case
					when VidSkidki=X'914f887f023f24874f33033ac1cacceb' then 'Газета/листовка x'
					when VidSkidki=X'913ad1938af7144e4322ac76b890cfce' then 'Накопительная'
					when VidSkidki=X'9b6e6ab7e8559cea4d3787ee115c6103' then 'Индивидуальная'
					when VidSkidki=X'9f6db59fa1f6542c4c8b9685a6dc516c' then 'Разовая'
					when VidSkidki=X'bda5601eb37a75774f39ea16ad78895c' then 'Фикс.цена'
					when VidSkidki=X'88cadabd4b9935494244b411e0721582' then 'ЦР'
					when VidSkidki=X'b6642d98b55a8d5e48c45c1c3731b72e' then 'По ответственному x'
					when VidSkidki=X'a24b2ee11974f13a4fbac0f8a4e35589' then 'Промокод x'
					when VidSkidki=X'a0c42e5e2beab7e74a98e440d5099464' then 'Таргетная x'
					when VidSkidki=X'9838ada09fd4e0364fb8f6233b11912b' then 'Разовая по сумме'
					when VidSkidki=X'b5b90ad6b83b804b483e1802fc095631' then 'Бонус'
				end as comment
		from skidki ss
			join Nomenklatura ns on ns._idrref=ss.nomenklatura and ss.DataOkonchaniya>=date('now')
			--left join Podrazdeleniya pdr on pdr._idrref=ss.podrazdelenie
			--left join Polzovateli pz on pz._idrref=ss.Polzovatel
			--left join Kontragenty kk on kk._idrref=ss.kontragent
	--	where holding.kod=122043 --or golovnoy.kod=122043
	where
		ss.kontragent=x'B88E6940E1C3703D4A522A4CB2DE7093'
		or ss.kontragent=x'96243CD92B037E6C11E5611C73357424'
		or ss.podrazdelenie=x'976618A90562E07411E378F3D35199CE'
		or ss.podrazdelenie=x'A003002264FA89D811E08BB0690A5B4A'
		or ss.podrazdelenie=x'BBAF20677C60FED011EA1749382D4BBE'
		or ss.podrazdelenie=x'BBAF20677C60FED011EA1749215684BF'
		--or ss.podrazdelenie=x'976618A90562E07411E378F3D35199CE'
	order by VidSkidki
	;
	 */
	public static String skidkiLastKontragentKod = "";
	public static String skidkiLastHRC = "";

	public static void refreshNomenklatureGroups(SQLiteDatabase mDB){
		//System.out.println("refreshNomenklatureGroups");
		mDB.execSQL("drop table if exists nomenklatura_counts;");
		mDB.execSQL("create table nomenklatura_counts (cnt integer,rdtl blob primary key,name text);");
		mDB.execSQL("insert into nomenklatura_counts (cnt,rdtl,name) select count(da._idrref) as cnt,da.roditel as rdtl,da.naimenovanie as name from Nomenklatura_sorted da where da.EtoGruppa=x'00' and da.PometkaUdaleniya=x'00' group by da.roditel;");
		mDB.execSQL("drop table if exists nomenklatura_groups;");
		mDB.execSQL("create table nomenklatura_groups ("
				+ " cat1 text,key1 blob,count1 integer,rod1 blob"
				+ " ,cat2 text,key2 blob,count2 integer,rod2 blob"
				+ " ,cat3 text,key3 blob,count3 integer,rod3 blob"
				+ " ,cat4 text,key4 blob,count4 integer,rod4 blob"
				+ " ,cat5 text,key5 blob,count5 integer,rod5 blob"
				+ " );");
		/*mDB.execSQL("insert into nomenklatura_groups (cat1,key1,count1,rod1 ,cat2,key2,count2,rod2 ,cat3,key3,count3,rod3 ,cat4,key4,count4,rod4 ,cat5,key5,count5,rod5)"
				+ "\n	select n1.naimenovanie as cat1,n1._IDRRef as key1,cnt1.cnt as count1,n1.roditel as rod1"
				+ "\n 		,n2.naimenovanie as cat2, n2._IDRRef as key2,cnt2.cnt as count2,n2.roditel as rod2"
				+ "\n 		,n3.naimenovanie as cat3, n3._IDRRef as key3,cnt3.cnt as count3,n3.roditel as rod3"
				+ "\n 		,n4.naimenovanie as cat4, n4._IDRRef as key4,cnt4.cnt as count4,n4.roditel as rod4"
				+ "\n 		,n5.naimenovanie as cat5, n5._IDRRef as key5,cnt5.cnt as count5,n5.roditel as rod5"
				+ "\n 	from nomenklatura_sorted n1"
				+ "\n		left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n 		left join nomenklatura_sorted n2 on n1._IDRRef=n2.Roditel and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00'"
				+ "\n		left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n		left join nomenklatura_sorted n3 on n2._IDRRef=n3.Roditel and n3.EtoGruppa=x'01' and n3.PometkaUdaleniya=x'00'"
				+ "\n		left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n		left join nomenklatura_sorted n4 on n3._IDRRef=n4.Roditel and n4.EtoGruppa=x'01' and n4.PometkaUdaleniya=x'00'"
				+ "\n		left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n		left join nomenklatura_sorted n5 on n4._IDRRef=n5.Roditel and n5.EtoGruppa=x'01' and n5.PometkaUdaleniya=x'00'"
				+ "\n		left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n 	where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00' and (count1>0 or count2>0 or count3>0 or count4>0 or count5>0)"
				+ "\n	order by cat1,cat2,cat3,cat4,cat5;");*/
		String sql = "insert into nomenklatura_groups (cat1,count1,rod1,key1 ,cat2,count2,rod2,key2 ,cat3,count3,rod3,key3 ,cat4,count4,rod4,key4 ,cat5,count5,rod5,key5)"
				+ "\n	select cat1,count1,rod1,key1 ,cat2,count2,rod2,key2 ,cat3,count3,rod3,key3 ,cat4,count4,rod4,key4 ,cat5,count5,rod5,key5 from ("
				+ "\n			select n1.naimenovanie as cat1,cnt1.cnt as count1,n1.roditel as rod1,n1._IDRRef as key1"
				+ "\n				,null as cat2,null as count2,null as rod2,null as key2"
				+ "\n				,null as cat3,null as count3,null as rod3,null as key3"
				+ "\n				,null as cat4,null as count4,null as rod4,null as key4"
				+ "\n				,null as cat5,null as count5,null as rod5,null as key5"
				+ "\n			from nomenklatura_sorted n1"
				+ "\n				left join nomenklatura_sorted n2 on n2.roditel=n1._idrref"
				+ "\n				left join nomenklatura_sorted n3 on n3.roditel=n2._idrref"
				+ "\n				left join nomenklatura_sorted n4 on n4.roditel=n3._idrref"
				+ "\n				left join nomenklatura_sorted n5 on n5.roditel=n4._idrref"
				+ "\n				left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n				left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n				left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n				left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n				left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n			where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'"
				+ "\n				and (cnt1.cnt>0 or cnt2.cnt>0 or cnt3.cnt>0 or cnt4.cnt>0 or cnt5.cnt>0)"
				+ "\n		union select n1.naimenovanie as cat1,0 as count1,n1.roditel as rod1,n1._IDRRef as key1"
				+ "\n				,n2.naimenovanie as cat2,cnt2.cnt as count2,n2.roditel as rod2,n2._IDRRef as key2"
				+ "\n				,null as cat3,null as count3,null as rod3,null as key3"
				+ "\n				,null as cat4,null as count4,null as rod4,null as key4"
				+ "\n				,null as cat5,null as count5,null as rod5,null as key5"
				+ "\n			from nomenklatura_sorted n1"
				+ "\n				join nomenklatura_sorted n2 on n2.roditel=n1._idrref"
				+ "\n				left join nomenklatura_sorted n3 on n3.roditel=n2._idrref"
				+ "\n				left join nomenklatura_sorted n4 on n4.roditel=n3._idrref"
				+ "\n				left join nomenklatura_sorted n5 on n5.roditel=n4._idrref"
				+ "\n				left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n				left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n				left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n				left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n				left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n			where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'"
				+ "\n				and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00'"
				+ "\n				and (cnt2.cnt>0 or cnt3.cnt>0 or cnt4.cnt>0 or cnt5.cnt>0)"
				+ "\n		union select n1.naimenovanie as cat1,0 as count1,n1.roditel as rod1,n1._IDRRef as key1"
				+ "\n				,n2.naimenovanie as cat2,0 as count2,n2.roditel as rod2,n2._IDRRef as key2"
				+ "\n				,n3.naimenovanie as cat3,cnt3.cnt as count3,n3.roditel as rod3,n3._IDRRef as key3"
				+ "\n				,null as cat4,null as count4,null as rod4,null as key4"
				+ "\n				,null as cat5,null as count5,null as rod5,null as key5"
				+ "\n			from nomenklatura_sorted n1"
				+ "\n				join nomenklatura_sorted n2 on n2.roditel=n1._idrref"
				+ "\n				join nomenklatura_sorted n3 on n3.roditel=n2._idrref"
				+ "\n				left join nomenklatura_sorted n4 on n4.roditel=n3._idrref"
				+ "\n				left join nomenklatura_sorted n5 on n5.roditel=n4._idrref"
				+ "\n				left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n				left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n				left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n				left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n				left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n			where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'"
				+ "\n				and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00'"
				+ "\n				and n3.EtoGruppa=x'01' and n3.PometkaUdaleniya=x'00'"
				+ "\n				and (cnt3.cnt>0 or cnt4.cnt>0 or cnt5.cnt>0)"
				+ "\n		union select n1.naimenovanie as cat1,0 as count1,n1.roditel as rod1,n1._IDRRef as key1"
				+ "\n				,n2.naimenovanie as cat2,0 as count2,n2.roditel as rod2,n2._IDRRef as key2"
				+ "\n				,n3.naimenovanie as cat3,0 as count3,n3.roditel as rod3,n3._IDRRef as key3"
				+ "\n				,n4.naimenovanie as cat4,cnt4.cnt as count4,n4.roditel as rod4,n4._IDRRef as key4"
				+ "\n				,null as cat5,null as count5,null as rod5,null as key5"
				+ "\n			from nomenklatura_sorted n1"
				+ "\n				join nomenklatura_sorted n2 on n2.roditel=n1._idrref"
				+ "\n				join nomenklatura_sorted n3 on n3.roditel=n2._idrref"
				+ "\n				join nomenklatura_sorted n4 on n4.roditel=n3._idrref"
				+ "\n				left join nomenklatura_sorted n5 on n5.roditel=n4._idrref"
				+ "\n				left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n				left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n				left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n				left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n				left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n			where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'"
				+ "\n				and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00'"
				+ "\n				and n3.EtoGruppa=x'01' and n3.PometkaUdaleniya=x'00'"
				+ "\n				and n4.EtoGruppa=x'01' and n4.PometkaUdaleniya=x'00'"
				+ "\n				and (cnt4.cnt>0 or cnt5.cnt>0)"
				+ "\n		union select n1.naimenovanie as cat1,0 as count1,n1.roditel as rod1,n1._IDRRef as key1"
				+ "\n				,n2.naimenovanie as cat2,0 as count2,n2.roditel as rod2,n2._IDRRef as key2"
				+ "\n				,n3.naimenovanie as cat3,0 as count3,n3.roditel as rod3,n3._IDRRef as key3"
				+ "\n				,n4.naimenovanie as cat4,0 as count4,n4.roditel as rod4,n4._IDRRef as key4"
				+ "\n				,n5.naimenovanie as cat5,cnt5.cnt as count5,n5.roditel as rod5,n5._IDRRef as key5"
				+ "\n			from nomenklatura_sorted n1"
				+ "\n				join nomenklatura_sorted n2 on n2.roditel=n1._idrref"
				+ "\n				join nomenklatura_sorted n3 on n3.roditel=n2._idrref"
				+ "\n				join nomenklatura_sorted n4 on n4.roditel=n3._idrref"
				+ "\n				join nomenklatura_sorted n5 on n5.roditel=n4._idrref"
				+ "\n				left join nomenklatura_counts cnt1 on n1._IDRRef=cnt1.rdtl"
				+ "\n				left join nomenklatura_counts cnt2 on n2._IDRRef=cnt2.rdtl"
				+ "\n				left join nomenklatura_counts cnt3 on n3._IDRRef=cnt3.rdtl"
				+ "\n				left join nomenklatura_counts cnt4 on n4._IDRRef=cnt4.rdtl"
				+ "\n				left join nomenklatura_counts cnt5 on n5._IDRRef=cnt5.rdtl"
				+ "\n			where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'"
				+ "\n				and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00'"
				+ "\n				and n3.EtoGruppa=x'01' and n3.PometkaUdaleniya=x'00'"
				+ "\n				and n4.EtoGruppa=x'01' and n4.PometkaUdaleniya=x'00'"
				+ "\n				and n5.EtoGruppa=x'01' and n5.PometkaUdaleniya=x'00'"
				+ "\n				and cnt5.cnt>0"
				+ "\n	) uu";

		System.out.println("refreshNomenklatureGroups " + sql);
		mDB.execSQL(sql);
	}

	public static void refreshSkidkiKontragent(String kod){
		if(skidkiLastKontragentKod.equals(kod)
				//&& skidkiLastHRC.equals(ApplicationHoreca.getInstance().hrcSelectedRoute())
				&& skidkiLastHRC.equals(Cfg.selectedOrDbHRC())
		){
			//
		}else{
			String sql = "select k1._idrref as klient,k1.GolovnoyKontragent as golovnoy from kontragenty k1 where k1.kod=" + kod;
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			String kontr_idrref = b.child("row").child("klient").value.property.value();
			String golov_idrref = b.child("row").child("golovnoy").value.property.value();
			sql = "select pdr1._idrref as p1,pdr2._idrref as p2,pdr3._idrref as p3,pdr4._idrref as p4,pdr5._idrref as p5"
					+ "\n from Polzovateli plzvtl"
					+ "\n 	left join Podrazdeleniya pdr1 on pdr1._idrref=plzvtl.podrazdelenie"
					+ "\n 	left join Podrazdeleniya pdr2 on pdr2._idrref=pdr1.Roditel"
					+ "\n 	left join Podrazdeleniya pdr3 on pdr3._idrref=pdr2.Roditel"
					+ "\n 	left join Podrazdeleniya pdr4 on pdr4._idrref=pdr3.Roditel"
					+ "\n 	left join Podrazdeleniya pdr5 on pdr5._idrref=pdr4.Roditel"
					//+ "\n where plzvtl.kod='" + ApplicationHoreca.getInstance().hrcSelectedRoute() + "';";
					+ "\n where plzvtl.kod='" + Cfg.selectedOrDbHRC() + "';";
			//System.out.println( sql);
			b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));

			//System.out.println( terr.dumpXML());

			String p1 = b.child("row").child("p1").value.property.value();
			String p2 = b.child("row").child("p2").value.property.value();
			String p3 = b.child("row").child("p3").value.property.value();
			String p4 = b.child("row").child("p4").value.property.value();
			String p5 = b.child("row").child("p5").value.property.value();
			//ApplicationHoreca.getInstance().getDataBase().execSQL("delete from skidki where DataOkonchaniya<date();");
			ApplicationHoreca.getInstance().getDataBase().execSQL("create table if not exists SkidkiLast ("//
					+ "\n		_id integer primary key asc autoincrement"//
					+ "\n		,datastart date null"//
					+ "\n		,dataend date null"//
					+ "\n		,nomenklatura blob null"//
					+ "\n		,price Number"//
					+ "\n		,comment text"//
					+ "\n	);");
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from SkidkiLast;");
			ApplicationHoreca.getInstance().getDataBase().execSQL("create index if not exists IX_SkidkiLast_datastart on SkidkiLast(datastart)");
			ApplicationHoreca.getInstance().getDataBase().execSQL("create index if not exists IX_SkidkiLast_dataend on SkidkiLast(dataend)");
			ApplicationHoreca.getInstance().getDataBase().execSQL("create index if not exists IX_SkidkiLast_nomenklatura on SkidkiLast(nomenklatura)");

			sql = "insert into SkidkiLast (datastart,dataend,nomenklatura,price,comment) "//
					+ "\n	select ss.DataNachala as datastart,ss.DataOkonchaniya as dataend,ns._idrref as nomenklatura,ss.Znachenie as price"//
					+ "\n			,case "//
					+ "\n				when VidSkidki=X'" + skidkaId_x_Gazeta + "' then 'Газета/листовка'"// no
					+ "\n				when VidSkidki=X'" + skidkaIdNakopitelnaya + "' then 'Накопительная' "//
					+ "\n				when VidSkidki=X'" + skidkaIdIndividualnaya + "' then 'Индивидуальная' "//over
					+ "\n				when VidSkidki=X'" + skidkaIdRazovaya + "' then 'Разовая' "//
					+ "\n				when VidSkidki=X'" + skidkaIdFixirovannaya + "' then 'Фикс.цена' "//over
					+ "\n				when VidSkidki=X'" + skidkaIdCenovoyeReagirovanie + "' then 'ЦР'"//
					+ "\n				when VidSkidki=X'" + skidkaId_Rasprodaja + "' then 'Распродажа'"//
					//+ "\n				when VidSkidki=X'b6642d98b55a8d5e48c45c1c3731b72e' then 'Скидка по ответственному'"// no
					+ "\n				when VidSkidki=X'" + skidkaId_x_Promokod + "' then 'Промокод' "// no
					+ "\n				when VidSkidki=X'" + skidkaId_x_Targetnie + "' then 'Таргетные' "// no
					+ "\n				when VidSkidki=X'" + skidkaIdPosummeRazovaya + "' then 'Разовая по сумме' "//
					+ "\n				when VidSkidki=X'" + skidkaIdBonus + "' then 'Бонус' "//
					+ "\n				else '' "//
					+ "\n			end as comment"//
					+ "\n		from skidki ss"//
					+ "\n			join Nomenklatura ns on ns._idrref=ss.nomenklatura and ss.DataOkonchaniya>=date('now')"//
			;
			//+ "\n			left join Kontragenty kk on kk._idrref=skidki.kontragent and kk.kod='" + kod + "' and DataOkonchaniya>=date('now')"//
			sql = sql + "\n		where ss.kontragent=x'" + kontr_idrref + "' or ss.kontragent=x'" + golov_idrref + "'";
			sql = sql + "\n		    or ss.podrazdelenie=x'" + p1 + "'";
			if(p2.length() > 3)
				sql = sql + "\n		    or ss.podrazdelenie=x'" + p2 + "'";
			if(p3.length() > 3)
				sql = sql + "\n		    or ss.podrazdelenie=x'" + p3 + "'";
			if(p4.length() > 3)
				sql = sql + "\n		    or ss.podrazdelenie=x'" + p4 + "'";
			if(p5.length() > 3)
				sql = sql + "\n		    or ss.podrazdelenie=x'" + p5 + "'";
			sql = sql + "\n		;";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from SkidkiLast where nullif(comment,'') is null;");
			skidkiLastKontragentKod = kod;
			//skidkiLastHRC = ApplicationHoreca.getInstance().hrcSelectedRoute();
			skidkiLastHRC = Cfg.selectedOrDbHRC();

			//System.out.println("skidkiLastKontragentKod " + skidkiLastKontragentKod + "/" + skidkiLastHRC + "/" + sql);
			//System.out.println("refreshPriceKontragent " + skidkiLastKontragentKod + "/" + skidkiLastHRC + "/" + (new Date()));
			refreshPriceKontragent();
			//refreshArtikleCount();
			// System.out.println("done refreshPriceKontragent " + skidkiLastKontragentKod + "/" + skidkiLastHRC + "/" + (new Date()));
		}
	}

	public static void refreshArtikleCount(){
		String sql = "drop index if exists idx_atricle_count;";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "drop table if exists atricle_count;";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

		sql = "create table atricle_count (artikul text, cnt integer);";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into atricle_count select n.Artikul as artikul, count(n.Artikul) as cnt"
				+ "\n		from Kontragenty ko"
				+ "\n		join ZayavkaPokupatelyaIskhodyaschaya za on za.kontragent=ko._IDRRef"
				+ "\n		join ZayavkaPokupatelyaIskhodyaschaya_Tovary tova on tova._ZayavkaPokupatelyaIskhodyaschaya_IDRRef=za._IDRRef"
				+ "\n		join nomenklatura n on n._idrref=tova.nomenklatura"
				+ "\n	where za.DataOtgruzki>=date()"
				+ "\n		and ko.kod=" + skidkiLastKontragentKod
				+ "\n	group by n.Artikul"
				+ "\n	order by ko.naimenovanie;";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "create index idx_atricle_count on atricle_count(artikul);";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

	}

	public static void refreshPriceKontragent(){
		//ApplicationHoreca.getInstance().getDataBase().execSQL("drop table AssortimentCurrent;");
		ApplicationHoreca.getInstance().getDataBase().execSQL("create table if not exists AssortimentCurrent(\n" +
				"  _id integer\n" +
				"  , nomenklatura_idrref blob null\n" +
				"  , zapret blob null\n" +
				"  , trafic blob null\n" +
				"  , klient_idrref blob null\n" +
				"  , podrazdelenie_idrref blob null\n" +
				"  , parent1_idrref blob null\n" +
				"  , parent2_idrref blob null\n" +
				"  , parent3_idrref blob null\n" +
				"  , parent4_idrref blob null\n" +
				"  , common_idrref  blob null\n" +
				");");
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AssortimentCurrent;");
		ApplicationHoreca.getInstance().getDataBase().execSQL("create index if not exists IX_AssortimentCurrent_nomenklatura_idrref on AssortimentCurrent(nomenklatura_idrref);");
		String sql = "";
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic  , klient_idrref             , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select   AssortimentNaSklade._id,NomenklaturaPostavshhik,zapret,trafic,KontragentPodrazdelenie   ,null                  ,null           ,null           ,null           ,null           ,null \n" +
				"  from  AssortimentNaSklade \n" +
				"  join Kontragenty on AssortimentNaSklade.KontragentPodrazdelenie=Kontragenty._IDRRef\n" +
				"  left join Kontragenty chld on Kontragenty._IDRRef=chld.GolovnoyKontragent\n" +
				"  where Kontragenty.kod=" + skidkiLastKontragentKod + " or chld.kod=" + skidkiLastKontragentKod +
				//"  left join Kontragenty prnt on prnt._IDRRef=Kontragenty.GolovnoyKontragent\n" +
				//"  where Kontragenty.kod="+skidkiLastKontragentKod+" or prnt.kod="+skidkiLastKontragentKod +
				" group by NomenklaturaPostavshhik;";
		System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic  , klient_idrref             , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)"
				+ "\n  select   AssortimentNaSklade._id,NomenklaturaPostavshhik,zapret,trafic,KontragentPodrazdelenie   ,null                  ,null           ,null           ,null           ,null           ,null"
				+ "\n  from  AssortimentNaSklade "
				+ "\n  join Kontragenty on Kontragenty.kod=" + skidkiLastKontragentKod + "  and AssortimentNaSklade.KontragentPodrazdelenie=Kontragenty.VidDostavki"
				+ "\n group by NomenklaturaPostavshhik;";
		System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);


		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,podr._IDRRef           ,null           ,null           ,null           ,null           ,null\n" +
				"  from Polzovateli\n" +
				"    join Podrazdeleniya podr on podr._IDRRef=Polzovateli.Podrazdelenie\n" +
				"    join AssortimentNaSklade aa on aa.KontragentPodrazdelenie=podr._IDRRef\n" +
				"  where Polzovateli.Kod='" + skidkiLastHRC + "' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,null                   ,p1._IDRRef     ,null           ,null           ,null           ,null\n" +
				"  from Polzovateli\n" +
				"    join Podrazdeleniya podr on podr._IDRRef=Polzovateli.Podrazdelenie\n" +
				"    left join Podrazdeleniya p1 on p1._IDRRef=podr.Roditel\n" +
				"    join AssortimentNaSklade aa on aa.KontragentPodrazdelenie=p1._IDRRef\n" +
				"  where Polzovateli.Kod='" + skidkiLastHRC + "' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,null                   ,null           ,p2._IDRRef     ,null           ,null           ,null\n" +
				"  from Polzovateli\n" +
				"    join Podrazdeleniya podr on podr._IDRRef=Polzovateli.Podrazdelenie\n" +
				"    left join Podrazdeleniya p1 on p1._IDRRef=podr.Roditel\n" +
				"    left join Podrazdeleniya p2 on p2._IDRRef=p1.Roditel\n" +
				"    join AssortimentNaSklade aa on aa.KontragentPodrazdelenie=p2._IDRRef\n" +
				"  where Polzovateli.Kod='" + skidkiLastHRC + "' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,null                   ,null           ,null           ,p3._IDRRef        ,null        ,null\n" +
				"  from Polzovateli\n" +
				"    join Podrazdeleniya podr on podr._IDRRef=Polzovateli.Podrazdelenie\n" +
				"    left join Podrazdeleniya p1 on p1._IDRRef=podr.Roditel\n" +
				"    left join Podrazdeleniya p2 on p2._IDRRef=p1.Roditel\n" +
				"    left join Podrazdeleniya p3 on p3._IDRRef=p2.Roditel\n" +
				"    join AssortimentNaSklade aa on aa.KontragentPodrazdelenie=p3._IDRRef\n" +
				"  where Polzovateli.Kod='" + skidkiLastHRC + "' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,null                   ,null           ,null           ,null           ,p4._IDRRef     ,null\n" +
				"  from Polzovateli\n" +
				"    join Podrazdeleniya podr on podr._IDRRef=Polzovateli.Podrazdelenie\n" +
				"    left join Podrazdeleniya p1 on p1._IDRRef=podr.Roditel\n" +
				"    left join Podrazdeleniya p2 on p2._IDRRef=p1.Roditel\n" +
				"    left join Podrazdeleniya p3 on p3._IDRRef=p2.Roditel\n" +
				"    left join Podrazdeleniya p4 on p4._IDRRef=p3.Roditel\n" +
				"    join AssortimentNaSklade aa on aa.KontragentPodrazdelenie=p4._IDRRef\n" +
				"  where Polzovateli.Kod='" + skidkiLastHRC + "' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "insert into AssortimentCurrent   (_id, nomenklatura_idrref, zapret,trafic, klient_idrref               , podrazdelenie_idrref, parent1_idrref, parent2_idrref, parent3_idrref, parent4_idrref, common_idrref)\n" +
				"  select aa._id,NomenklaturaPostavshhik,aa.zapret,aa.trafic,null                                   ,null                   ,null           ,null           ,null           ,null           ,x'00'\n" +
				"  from AssortimentNaSklade aa\n" +
				"  where aa.KontragentPodrazdelenie=x'00' and aa.NomenklaturaPostavshhik not in (select nomenklatura_idrref from AssortimentCurrent)\n" +
				";";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	public static String findCurrentDogGroupIDRREF(){
		String kod = ApplicationHoreca.getInstance().getClientInfo().getKod();
		String sql = "select grupdog._idrref as _idrref"
				+ " from Kontragenty k1"
				+ " join DogovoryKontragentov dog1 on dog1.vladelec=k1._IDRRef"
				+ " join GruppyDogovorov grupdog on dog1.GruppaDogovorov=grupdog._IDRRef"
				+ " where k1.kod='" + ApplicationHoreca.getInstance().getClientInfo().getKod().trim() + "';";
		Bough dog = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//BBB320677C60FED011EA35F5A9390059
		return dog.child("row").child("_idrref").value.property.value();
	}

	public static void saveCRGroup(String artikul, double cr){


		String idrf = findCurrentDogGroupIDRREF();
		System.out.println("saveCRGroup " + idrf + "/" + artikul + "/" + cr);
		if(idrf.length() > 0){
			for(int i = 0; i < crGroupCache.size(); i++){
				if(crGroupCache.get(i).equals(idrf) && crArtikulCache.get(i).equals(artikul)){
					crCenaCache.set(i, cr);
					return;
				}
			}
			crGroupCache.add(idrf);
			crArtikulCache.add(artikul);
			crCenaCache.add(cr);
		}
	}

	public static double findCRGroup(String artikul){

		//float cr = 0;


		String idrf = findCurrentDogGroupIDRREF();
		//System.out.println("findCRGroup " + idrf + "/" + artikul);
		for(int i = 0; i < crGroupCache.size(); i++){
			if(crGroupCache.get(i).equals(idrf) && crArtikulCache.get(i).equals(artikul)){
				//System.out.println("found " + crCenaCache.get(i));
				return crCenaCache.get(i).doubleValue();
			}
		}
		return 0;
	}

	public static Vector<CfgKontragentInfo> crossDogovora(){
		/*
		System.out.println("check crossDogovora");
		System.out.println("getAgentName "+ApplicationHoreca.getInstance().getCurrentAgent().getAgentName()+".");
		System.out.println("getAgentIDstr "+ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()+".");
		System.out.println("currentHRCmarshrut "+Activity_Route.currentHRCmarshrut+".");
		System.out.println("currentIDmarshrut "+Activity_Route.currentIDmarshrut+".");
		System.out.println("lastHRCID "+lastHRCID+".");
		*/
		final Runtime runtime = Runtime.getRuntime();
		final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
		final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
		final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
		//System.out.println("memory "+usedMemInMB+"/"+maxHeapSizeInMB+"/"+availHeapSizeInMB);
		if(all == null || (!lastHRCID.equals(//ApplicationHoreca.getInstance().currentIDmarshrut
				Cfg.selectedHRC_idrref()
		))){
			//System.out.println("read crossDogovora");
			//lastHRCID = ApplicationHoreca.getInstance().currentIDmarshrut;
			lastHRCID = Cfg.selectedHRC_idrref();
			String sqlX = "";
			if(lastHRCID.length() > 1){
				sqlX = "\n join MarshrutyAgentov m on m.kontragent=k._idrref and m.agent=x'" + lastHRCID + "'";
			}
			all = new Vector<CfgKontragentInfo>();
			String sql = "	select k._idrref as idrref"//
					+ "\n	  		,k.kod as kod"//
					+ "\n	  		,k.Naimenovanie as naimenovanie"//
					+ "\n	  		,d.Naimenovanie as dogName"//
					+ "\n	  		,d.Kod as dogKod"//
					+ "\n	  		,d.PhormaDogovora as phormaDogovora"//
					//+ "\n	  		,g.[_IDRRef] as GruppyDogovorov"//
					+ "\n	  		,d.[GruppaDogovorov] as GruppyDogovorov"//
					+ "\n	  		,d.PometkaUdaleniya as pometkaUdaleniya"//
					+ "\n	  	from Kontragenty k"//
					+ "\n	  		join DogovoryKontragentov_strip d on d.[Vladelec] = k.[_IDRRef] and d.[GruppaDogovorov]<>X'00000000000000000000000000000000' and d.[GruppaDogovorov]<>X'00'"//
					//+ "\n	  		join GruppyDogovorov g on d.[GruppaDogovorov] = g.[_IDRRef]"//
					+ sqlX//
					+ "\n group by k._idrref,d._idrref"//
					+ "\n	union"//
					+ "\n	select k._idrref as idrref"//
					+ "\n	  		,k.kod as kod"//
					+ "\n	  		,k.Naimenovanie as naimenovanie"//
					+ "\n	  		,d.Naimenovanie as dogName"//
					+ "\n	  		,d.Kod as dogKod"//
					+ "\n	  		,d.PhormaDogovora as phormaDogovora"//
					+ "\n	  		,X'00000000000000000000000000000000' as GruppyDogovorov"//
					+ "\n	  		,d.PometkaUdaleniya as pometkaUdaleniya"//
					+ "\n	  	from Kontragenty k"//
					+ "\n	  		join DogovoryKontragentov_strip d on d.[Vladelec] = k.[_IDRRef] and (d.[GruppaDogovorov]=X'00000000000000000000000000000000' or d.[GruppaDogovorov]=X'00')"//
					+ sqlX//
					+ "\n group by k._idrref,d._idrref"//
					+ "\n	order by naimenovanie,dogName limit 1234;"//
					;
			System.out.println("crossDogovora " + sql);
			Bough crossDogovoraCache = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			System.out.println(crossDogovoraCache.dumpXML());
			for(int i = 0; i < crossDogovoraCache.children.size(); i++){
				Bough b = crossDogovoraCache.children.get(i);
				CfgDogovorInfo dogovor = new CfgDogovorInfo();
				dogovor.kod = b.child("dogKod").value.property.value();
				dogovor.naimenovanie = b.child("dogName").value.property.value();
				dogovor.GruppyDogovorov = b.child("GruppyDogovorov").value.property.value();
				dogovor.phormaDogovora = b.child("phormaDogovora").value.property.value();
				dogovor.deleted = b.child("pometkaUdaleniya").value.property.value();
				/*System.out.println(b.child("naimenovanie").value.property.value()
						+" / "+dogovor.naimenovanie+" / "+
						dogovor.GruppyDogovorov+" / "+
						dogovor.phormaDogovora+" / ");*/
				boolean found = false;
				for(int n = 0; n < all.size(); n++){
					if(all.get(n).kod.equals(b.child("kod").value.property.value())){
						//System.out.println("merge	" + all.get(n).naimenovanie);
						all.get(n).dogovora.add(dogovor);
						found = true;
						break;
					}
				}
				if(!found){
					CfgKontragentInfo k = new CfgKontragentInfo();
					k.kod = b.child("kod").value.property.value();
					k.naimenovanie = b.child("naimenovanie").value.property.value();
					k.dogovora.add(dogovor);
					//System.out.println(i+" new	" + k.naimenovanie);
					all.add(k);
				}
				//b.child("groups").child("group").value.is(b.child("GruppyDogovorov").value.property.value());
				//System.out.println(i + " " + b.child("GruppyDogovorov").value.property.value());
			}
			/*for (int k = 0; k < Cfg.crossDogovora().size(); k++) {
				System.out.println("	" + Cfg.crossDogovora().get(k).naimenovanie);
				for (int d = 0; d < Cfg.crossDogovora().get(k).dogovora.size(); d++) {
					System.out.println("		" + Cfg.crossDogovora().get(k).dogovora.get(d).naimenovanie //
							+ ", gruppa: " + Cfg.crossDogovora().get(k).dogovora.get(d).GruppyDogovorov);
				}
			}*/
			//System.out.println("done crossDogovoraCache " + crossDogovoraCache.dumpXML());
		}
		return all;
	}

	public static String kodPodrazdeleni(int nn){
		if(nn >= 0 && nn < Cfg.territory().children.size()){
			String kod = Cfg.territory().children.get(nn).child("kod").value.property.value();
			//System.out.println("Cfg.kodPodrazdeleni " + kod);
			return kod;
		}else{
			System.out.println("Cfg.kodPodrazdeleni not found");
			return "";
		}
	}

	public static String kodKontragenta(int nn){
		//Cfg.kontragenty(kodPodrazdeleniya).children.get(OsnovnoiKlientTT.value().intValue() - 1).child("kod").value.property.value();
		if(nn >= 0 && nn < Cfg.vseKontragenty().children.size()){
			String kod = Cfg.vseKontragenty().children.get(nn).child("kod").value.property.value();
			return kod;
		}else{
			return "";
		}
	}

	public static String kodKontragenta(int nn, String kodPodrazdeleniya){
		//Cfg.kontragenty(kodPodrazdeleniya).children.get(OsnovnoiKlientTT.value().intValue() - 1).child("kod").value.property.value();
		if(nn >= 0 && nn < Cfg.kontragentyByKod(kodPodrazdeleniya).children.size()){
			String kod = Cfg.kontragentyByKod(kodPodrazdeleniya).children.get(nn).child("kod").value.property.value();
			return kod;
		}else{
			return "";
		}
	}

	public static void exportArtikulsList(Activity activity, String fileName, String kodKlient, Vector<Vector<String>> rows){
		try{
			String xname = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".xls";
			File xfile = new File(xname);
			jxl.WorkbookSettings wbSettings = new jxl.WorkbookSettings();
			wbSettings.setLocale(new java.util.Locale("ru", "RU"));
			//ZayavkaPokupatelya bid = mBidData.getBid();
			jxl.write.WritableWorkbook workbook = jxl.Workbook.createWorkbook(xfile, wbSettings);
			workbook.setColourRGB(Colour.PLUM, 229, 0, 81);
			workbook.setColourRGB(Colour.LAVENDER, 195, 189, 217);
			workbook.setColourRGB(Colour.BROWN, 55, 38, 128);
			workbook.createSheet("" + kodKlient, 0);
			jxl.write.WritableSheet excelSheet = workbook.getSheet(0);
			excelSheet.setColumnView(1, 6);
			excelSheet.setColumnView(2, 12);
			excelSheet.setColumnView(3, 60);
			excelSheet.setColumnView(4, 6);
			excelSheet.setColumnView(5, 12);
			excelSheet.setColumnView(6, 12);
			excelSheet.setColumnView(7, 12);
			jxl.write.Label label;
			InputStream inStream = activity.getResources().openRawResource(sweetlife.android10.R.raw.export_header);
			byte[] logo = new byte[inStream.available()];
			int nn = 0;
			while(inStream.available() > 0){
				logo[nn] = (byte)inStream.read();
				nn++;
			}
			jxl.write.WritableImage img = new jxl.write.WritableImage(1, 1, 7, 1, logo);
			excelSheet.addImage(img);
			jxl.write.WritableFont titleFont = new jxl.write.WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BROWN);
			jxl.write.WritableCellFormat titleFormat = new jxl.write.WritableCellFormat(titleFont);
			titleFormat.setAlignment(Alignment.CENTRE);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			excelSheet.mergeCells(1, 2, 7, 2);
			excelSheet.setRowView(2, 900);
			excelSheet.addCell(new jxl.write.Label(1, 2, "Коммерческое предложение", titleFormat));
			WritableFont whiteFont = new WritableFont(WritableFont.createFont("Arial"), 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
			WritableCellFormat headerFormat = new WritableCellFormat(whiteFont);
			int headerSkip = 4;
			headerFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			headerFormat.setAlignment(Alignment.CENTRE);
			headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			headerFormat.setBackground(Colour.LAVENDER);
			label = new jxl.write.Label(1, headerSkip - 1, "№", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(2, headerSkip - 1, "Код", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(3, headerSkip - 1, "Наименование", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(4, headerSkip - 1, "Ед.изм.", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(5, headerSkip - 1, "Мин. партия отгрузки", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(6, headerSkip - 1, "Кол-во в месте", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(7, headerSkip - 1, "Цена", headerFormat);
			excelSheet.addCell(label);
			jxl.write.WritableCellFormat cellFormat = new jxl.write.WritableCellFormat();
			cellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			cellFormat.setAlignment(Alignment.CENTRE);
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			jxl.write.WritableCellFormat catFormat = new jxl.write.WritableCellFormat(whiteFont);
			catFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			catFormat.setBackground(Colour.PLUM);
			catFormat.setAlignment(Alignment.CENTRE);
			catFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			jxl.write.WritableCellFormat subFormat = new jxl.write.WritableCellFormat(whiteFont);
			subFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			subFormat.setBackground(Colour.PLUM);
			subFormat.setAlignment(Alignment.CENTRE);
			subFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			excelSheet.setRowView(headerSkip - 1, 900);
			excelSheet.setRowView(1, 5500);
			//int cnt = mBidData.getFoodStuffs().getCount();
			int cnt = rows.size();

			Collections.sort(rows, new Comparator<Vector<String>>(){
				@Override
				public int compare(Vector<String> a, Vector<String> b){
					return a.get(7).compareTo(b.get(7)) * 1000000 + a.get(6).compareTo(b.get(6)) * 1000 + a.get(1).compareTo(b.get(1));
				}
			});
			String cat = "";
			String subcat = "";
			int catcount = 0;
			for(int i = 0; i < cnt; i++){
				if(!cat.equals(rows.get(i).get(7))){
					cat = rows.get(i).get(7);
					excelSheet.mergeCells(1, headerSkip + i + catcount, 7, headerSkip + i + catcount);
					label = new jxl.write.Label(1, headerSkip + i + catcount, "" + cat, catFormat);
					excelSheet.addCell(label);
					excelSheet.setRowView(headerSkip + i + catcount, 500);
					catcount++;
				}
				if(!subcat.equals(rows.get(i).get(6))){
					subcat = rows.get(i).get(6);
					label = new jxl.write.Label(1, headerSkip + i + catcount, "" + subcat, subFormat);
					excelSheet.addCell(label);
					excelSheet.mergeCells(1, headerSkip + i + catcount, 7, headerSkip + i + catcount);
					excelSheet.setRowView(headerSkip + i + catcount, 300);
					catcount++;
				}
				label = new jxl.write.Label(1, headerSkip + i + catcount, "" + (1 + i), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(2, headerSkip + i + catcount, rows.get(i).get(0), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(3, headerSkip + i + catcount, rows.get(i).get(1), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(4, headerSkip + i + catcount, rows.get(i).get(2), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(5, headerSkip + i + catcount, rows.get(i).get(3), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(6, headerSkip + i + catcount, rows.get(i).get(4), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(7, headerSkip + i + catcount, rows.get(i).get(5), cellFormat);
				excelSheet.addCell(label);
			}
			excelSheet.setRowView(headerSkip + cnt + catcount + 1, 4500);
			InputStream inStream2 = activity.getResources().openRawResource(R.raw.export_bottom);
			byte[] logo2 = new byte[inStream2.available()];
			int nn2 = 0;
			while(inStream2.available() > 0){
				logo2[nn2] = (byte)inStream2.read();
				nn2++;
			}
			jxl.write.WritableImage img2 = new jxl.write.WritableImage(1, headerSkip + cnt + catcount + 1, 7, 1, logo2);
			excelSheet.addImage(img2);
			workbook.write();
			workbook.close();
			Auxiliary.startFile(activity, xfile);
		}catch(Throwable t){
			t.printStackTrace();
			Auxiliary.warn("Ошибка: " + t.getMessage(), activity);
		}
	}

/*
    public static String kodKontragentaByIndex(int nn) {
        Bough bb=Cfg.vseKontragenty();
        if (nn >= 0 && nn < bb.children.size()) {
            String kod = bb.children.get(nn).child("kod").value.property.value();
            return kod;
        } else {
            return "";
        }
    }*/
}