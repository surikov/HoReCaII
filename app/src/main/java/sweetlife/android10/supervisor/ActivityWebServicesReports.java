package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;
import android.media.*;
import android.content.*;
import android.graphics.*;
import android.net.*;

import java.util.*;

import android.database.sqlite.*;

import java.util.regex.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;

import java.net.*;
import java.nio.channels.*;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.orders.FoodstuffsData;
import sweetlife.android10.ui.*;
import sweetlife.android10.utils.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;
import java.text.*;

public class ActivityWebServicesReports extends Activity{
	public static final int FILE_SELECT_SKD_RESULT = 112234;
	public static final int FILE_PEREBIT_SKD_RESULT = 223322;
	//public static final int NOMENKLATURA_NEW = 555333;
	public static String selectedDocNum = "";
	public static Bitmap icon_doc_add;
	public static Bitmap icon_ok;
	public static EditOrderViaWeb editor;// = new EditOrderViaWeb();
	public Report_Base preReport = null;
	public String preKey = null;
	Bough mapData = new Bough().name.is("mapData");
	Numeric reportSplit = new Numeric();
	Numeric propSplit = new Numeric();
	ColumnBitmap reportIcon;
	ColumnDescription reportName;
	SubLayoutless propertiesForm;
	DataGrid2 reportGrid;
	WebRender brwsr;
	MenuItem menuExportXLS;
	MenuItem menuExportPDF;
	MenuItem menuSendHTML;
	MenuItem menuSendXLS;
	MenuItem menuSendPDF;
	static int reportGridScrollViewY = 0;

	final public static int etiketkaIDPicture = 734766;
	final public static int tovarIDPicture = 1098834;
	public static Note etiketkaObratnayaSvyazKlient = new Note();
	public static Note tovarObratnayaSvyazKlient = new Note();
	static Note docNumObratnayaSvyazKlient = new Note();
	//static Note artikulObratnayaSvyazKlient = new Note();
	static Note commentObratnayaSvyazKlient = new Note();
	//static Numeric dateObratnayaSvyazKlient = new Numeric();


	Note seekReport = new Note();

	public static String goLastPageReportName = "startup";
	public static boolean needRefresh = false;

	MenuItem menuClear;

	Expect refreshFog = new Expect()//
			.task.is(new Task(){
				@Override
				public void doTask(){
					if(brwsr != null){
						while(!brwsr.isEnabled()){
							try{
								Thread.sleep(99);
							}catch(Throwable t){
								break;
							}
						}
					}
				}
			})//
			.afterDone.is(new Task(){
				@Override
				public void doTask(){
					//
				}
			})//
			.status.is("Подождите....");
	private Layoutless layoutless;

	public static String reformatDate(String d){
		try{
			DateFormat from = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat to = new SimpleDateFormat("yyyy-MM-dd");
			d = to.format(from.parse(d));
		}catch(Throwable t){
			t.printStackTrace();
		}
		//System.out.println("d now is "+d);
		return d;
	}

	public static String reformatDate2(String d){
		try{
			DateFormat from = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat to = new SimpleDateFormat("yyyyMMdd");
			d = to.format(from.parse(d));
		}catch(Throwable t){
			t.printStackTrace();
		}
		//System.out.println("d now is "+d);
		return d;
	}

	public static String reformatDate3(String d){
		try{
			DateFormat from = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat to = new SimpleDateFormat("yyyy-MM-dd");
			d = to.format(from.parse(d));
		}catch(Throwable t){
			t.printStackTrace();
		}
		//System.out.println("d now is "+d);
		return d;
	}

	public static double dateOnly(Calendar c){
		return dateOnly(c, 0);
	}

	public static double dateOnly(Calendar c, int days){
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTimeInMillis();
	}

	public static String pad2(int n){
		String r = "" + n;
		if(n < 10){
			r = "0" + r;
		}
		return r;
	}

	public static String naimenovanieKontragenta(int nn){
		if(nn < Cfg.kontragenty().children.size()){
			return Cfg.kontragenty().children.get(nn).child("naimenovanie").value.property.value();
		}
		return "(неизвестный контрагент " + nn + ")";
	}

	@Override
	protected void onPause(){
		try{
			if(preReport != null){
				Preferences.string("folder", "").value(preReport.getFolderKey());
				Preferences.string("file", "").value(preKey);
			}
			Preferences.save();
		}catch(Throwable t){
			t.printStackTrace();
		}
		reportGridScrollViewY = reportGrid.scrollView.getScrollY();
		/*System.out.println("save reportGridScrollViewY " + reportGridScrollViewY
				+ ": " + reportGrid.getMeasuredHeight()
				+ ": " + reportGrid.scrollView.getMeasuredHeight()
				+ ": " + reportName.strings.size());*/
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		System.out.println("ActivityWebServicesReports.onCreate");
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Preferences.init(this);
		/*layoutless.afterOnMeasure=new Task(){
			public void doTask(){
				if(reportGrid!=null) {
					System.out.println("scroll to reportGridScrollViewY: "+reportGridScrollViewY
							+": "+reportGrid.getHeight()
							+": "+reportGrid.scrollView.getHeight()
							+": "+reportName.strings.size());
				}
			}
		};*/
		new Expect().status.is("Подождите")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						Cfg.territory();
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						createGUI();
						bindData();
						goLastPage();
						//waitForLayout();

					}
				}).start(this)//
		;

	}

	void waitForReportGridLayout(){
		final View view = reportGrid;
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
			@Override
			public void onGlobalLayout(){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}else{
					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				//int width  = view.getMeasuredWidth();
				//int height = view.getMeasuredHeight();
				//System.out.println("addOnGlobalLayoutListener reportGridScrollViewY: "+reportGridScrollViewY);
				//System.out.println("addOnGlobalLayoutListener reportGridScrollViewY: "+reportGridScrollViewY);
				scrollBack();
			}
		});
	}

	void scrollBack(){
		System.out.println("scrollBack reportGridScrollViewY: " + reportGridScrollViewY
				+ ": " + reportGrid.getMeasuredHeight()
				+ ": " + reportGrid.scrollView.getMeasuredHeight()
				+ ": " + reportName.strings.size());
		if(reportGridScrollViewY > 0){
			reportGrid.scrollView.scrollTo(0, reportGridScrollViewY);
			reportGridScrollViewY = 0;
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		System.out.println("ActivityWebServicesReports.onResume needRefresh " + needRefresh);
		if(needRefresh){
			needRefresh = false;
			//expectRequery.start(this);
			if(preReport != null){
				System.out.println("ActivityWebServicesReports preReport.writeCurrentPage");
				new Expect().task.is(new Task(){
					@Override
					public void doTask(){
						preReport.writeCurrentPage();
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				}).status.is("Обновление....").start(this);
			}
		}
	}

	Report_Base createReport(String key){
		Report_Base report = null;
		if(key.equals(ReportAKBPoTP.folderKey())){
			report = new ReportAKBPoTP(this);
		}
		if(key.equals(ReportRV.folderKey())){
			report = new ReportRV(this);
		}
		if(key.equals(ReportStatistikaVozvratovAktovSverki.folderKey())){
			report = new ReportStatistikaVozvratovAktovSverki(this);
		}
		if(key.equals(ReportPechatSchetaNaOplatu.folderKey())){
			report = new ReportPechatSchetaNaOplatu(this);
		}
		if(key.equals(ReportBonusyDlyaTP.folderKey())){
			report = new ReportBonusyDlyaTP(this);
		}
		if(key.equals(ReportVzaioraschetySpokupatelem.folderKey())){
			report = new ReportVzaioraschetySpokupatelem(this);
		}
		//if (key.equals(ReportRekomendaciiKlientam.folderKey())) {
		//	report = new ReportRekomendaciiKlientam(this);
		//}
		if(key.equals(ReportZakazyVneMarshruta.folderKey())){
			report = new ReportZakazyVneMarshruta(this);
		}
		if(key.equals(ReportZayavkiNaPerebitieNakladnih.folderKey())){
			report = new ReportZayavkiNaPerebitieNakladnih(this);
		}
		if(key.equals(ReportZayavkiNaUvelichenieLimita.folderKey())){
			report = new ReportZayavkiNaUvelichenieLimita(this);
		}
		if(key.equals(ReportAssortimentFlagmanov.folderKey())){
			report = new ReportAssortimentFlagmanov(this);
		}

		if(key.equals(ReportSravnitelniyAnaliz.folderKey())){
			report = new ReportSravnitelniyAnaliz(this);
		}
		if(key.equals(ReportTovarnieVozvrati.folderKey())){
			report = new ReportTovarnieVozvrati(this);
		}
		if(key.equals(ReportTovarnieNakladmie.folderKey())){
			report = new ReportTovarnieNakladmie(this);
		}
		if(key.equals(ReportAktSverki.folderKey())){
			report = new ReportAktSverki(this);
		}
		if(key.equals(ReportVozmeschenie.folderKey())){
			report = new ReportVozmeschenie(this);
		}
		if(key.equals(ReportDegustaciaDlyaTP.folderKey())){
			report = new ReportDegustaciaDlyaTP(this);
		}
		//if (key.equals(ReportDistribucia.folderKey())) {
		//	report = new ReportDistribucia(this);
		//}
		if(key.equals(ReportAcciiDlyaKlientov.folderKey())){
			report = new ReportAcciiDlyaKlientov(this);
		}
		if(key.equals(ReportSkidkiDlyaKlientov.folderKey())){
			report = new ReportSkidkiDlyaKlientov(this);
		}
		//if (key.equals(ReportDostavkaPoVoditelam.folderKey())) {
		//	report = new ReportDostavkaPoVoditelam(this);
		//}
		if(key.equals(ReportDostavkaPoMarshrutam.folderKey())){
			report = new ReportDostavkaPoMarshrutam(this);
		}
		if(key.equals(ReportSKUsOtgruzkami.folderKey())){
			report = new ReportSKUsOtgruzkami(this);
		}

		if(key.equals(ReportMelkieZakazy.folderKey())){
			report = new ReportMelkieZakazy(this);
		}
		if(key.equals(ReportIzbrannoeSmartPro.folderKey())){
			report = new ReportIzbrannoeSmartPro(this);
		}
		if(key.equals(ReportKlassifikaciaKlientov.folderKey())){
			report = new ReportKlassifikaciaKlientov(this);
		}
		if(key.equals(ReportNalichieSkanov.folderKey())){
			report = new ReportNalichieSkanov(this);
		}
		if(key.equals(ReportDZDlyaTP.folderKey())){
			report = new ReportDZDlyaTP(this);
		}
		if(key.equals(ReportDZBlizhaishiePlateji.folderKey())){
			report = new ReportDZBlizhaishiePlateji(this);
		}
		//if (key.equals(ReportDistribuciaPoKluchevimPosiciam.folderKey())) {
		//	report = new ReportDistribuciaPoKluchevimPosiciam(this);
		//}
		if(key.equals(ReportFixirovannieCeny.folderKey())){
			report = new ReportFixirovannieCeny(this);
		}
		if(key.equals(ReportNakladnieNaKontrole.folderKey())){
			report = new ReportNakladnieNaKontrole(this);
		}
		if(key.equals(ReportUsloviaOtgruzki.folderKey())){
			report = new ReportUsloviaOtgruzki(this);
		}
		if(key.equals(ReportLimity.folderKey())){
			report = new ReportLimity(this);
		}
		if(key.equals(ReportVestnik.folderKey())){
			report = new ReportVestnik(this);
		}
		if(key.equals(ReportValovayaPribil.folderKey())){
			report = new ReportValovayaPribil(this);
		}
		if(key.equals(ReportValovayaIerarhia.folderKey())){
			report = new ReportValovayaIerarhia(this);
		}

/*
		if (key.equals(ReportLocalizasiaDlyaTP.folderKey())) {
			report = new ReportLocalizasiaDlyaTP(this);
		}*/
		if(key.equals(ReportObjedineniaKlientov.folderKey())){
			report = new ReportObjedineniaKlientov(this);
		}
		//if (key.equals(ReportOtchetPoKassamDlyaTP.folderKey())) {
		//	report = new ReportOtchetPoKassamDlyaTP(this);
		//}
		if(key.equals(ReportPlanPolevihRabot.folderKey())){
			report = new ReportPlanPolevihRabot(this);
		}
		if(key.equals(ReportPokazateliKPI.folderKey())){
			report = new ReportPokazateliKPI(this);
		}
		if(key.equals(ReportPredzakazyNaTrafiki.folderKey())){
			report = new ReportPredzakazyNaTrafiki(this);
		}


		if(key.equals(ReportZayavkiNaDobavlenieVmatricu.folderKey())){
			report = new ReportZayavkiNaDobavlenieVmatricu(this);
		}
		/*if (key.equals(ReportProbegTPSV.folderKey())) {
			report = new ReportProbegTPSV(this);
		}*/
		if(key.equals(ReportKontragentySPrilojeniem.folderKey())){
			report = new ReportKontragentySPrilojeniem(this);
		}
		if(key.equals(ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta.folderKey())){
			report = new ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta(this);
		}


		if(key.equals(ReportPoiskKontragentaPoKluchevimPolyam.folderKey())){
			report = new ReportPoiskKontragentaPoKluchevimPolyam(this);
		}


		if(key.equals(ReportStatisticaPodpisannihDS.folderKey())){
			report = new ReportStatisticaPodpisannihDS(this);
		}
		if(key.equals(ReportStatistikaZakazov2.folderKey())){
			report = new ReportStatistikaZakazov2(this);
		}


		if(key.equals(ReportKontragentyPortalaSmart.folderKey())){
			report = new ReportKontragentyPortalaSmart(this);
		}


		if(key.equals(ReportProcentZapolneniyaChekListov.folderKey())){
			report = new ReportProcentZapolneniyaChekListov(this);
		}
		//if (key.equals(ReportStatusyRasporjazheniy.folderKey())) {
		//	report = new ReportStatusyRasporjazheniy(this);
		//}
		if(key.equals(ReportStatusyRasporjazheniySKD.folderKey())){
			report = new ReportStatusyRasporjazheniySKD(this);
		}
		if(key.equals(ReportStatusyVozvratov.folderKey())){
			report = new ReportStatusyVozvratov(this);
		}
		if(key.equals(ReportStatusyZakazov.folderKey())){
			report = new ReportStatusyZakazov(this);
		}


		if(key.equals(ReportObratnaiaSvyazOtKlienta.folderKey())){
			report = new ReportObratnaiaSvyazOtKlienta(this);
		}
		if(key.equals(ReportObratnaiaSvyazPoDogovoram.folderKey())){
			report = new ReportObratnaiaSvyazPoDogovoram(this);
		}


		if(key.equals(ReportSvodDlyaTP.folderKey())){
			report = new ReportSvodDlyaTP(this);
		}
		//if (key.equals(ReportTrafiki.folderKey())) {
		//	report = new ReportTrafiki(this);
		//}
		if(key.equals(ReportVipolnenieDopMotivaciy.folderKey())){
			report = new ReportVipolnenieDopMotivaciy(this);
		}
		if(key.equals(ReportSootvetstvieMercury.folderKey())){
			report = new ReportSootvetstvieMercury(this);
		}
		if(key.equals(ReportVizity.folderKey())){
			report = new ReportVizity(this);
		}
		if(key.equals(ReportVipolneniePlanovPoPrilojeniu.folderKey())){
			report = new ReportVipolneniePlanovPoPrilojeniu(this);
		}
		if(key.equals(ReportNSK.folderKey())){
			report = new ReportNSK(this);
		}
		if(key.equals(ReportProdajiFlagmanov.folderKey())){
			report = new ReportProdajiFlagmanov(this);
		}
		if(key.equals(ReportDistribucia2.folderKey())){
			report = new ReportDistribucia2(this);
		}
		if(key.equals(ReportDistribuciaKrasnodar.folderKey())){
			report = new ReportDistribuciaKrasnodar(this);
		}


		if(key.equals(ReportProdajiFlagmanovPoKontragentam.folderKey())){
			report = new ReportProdajiFlagmanovPoKontragentam(this);
		}
		if(key.equals(ReportResultatyUtverjdenihSpecifikaciy.folderKey())){
			report = new ReportResultatyUtverjdenihSpecifikaciy(this);
		}
		if(key.equals(ReportProdazhiSTM.folderKey())){
			report = new ReportProdazhiSTM(this);
		}
		if(key.equals(ReportVozvratyPoPodrazelen.folderKey())){
			report = new ReportVozvratyPoPodrazelen(this);
		}
		if(key.equals(ReportFixirovanieKoordinat.folderKey())){
			report = new ReportFixirovanieKoordinat(this);
		}
		return report;
	}

	void openLastOrCreate(String folder){
		System.out.println("openLastOrCreate " + folder);
		Vector<String> subs = Cfg.reportNames(folder);
		for(int kk = 0; kk < subs.size(); kk++){
			//if (subs.size() > 0) {
			// String instKey = subs.get(0).replace(".xml", "");
			String instKey = subs.get(kk).replace(".xml", "");
			File html = new File(Cfg.pathToHTML(folder, instKey));
			File xml = new File(Cfg.pathToXML(folder, instKey));
			if(instKey.length() > 1 //
					&& html.exists()//
					&& xml.exists()//
			){
				System.out.println("openLastOrCreate reuse " + folder + ":" + instKey);
				resetInstance(folder, instKey);
				return;
			}
		}
		System.out.println("openLastOrCreate new " + folder);
		tapReport2(folder);
	}


	void goLastPage(){
		String goReportName = Auxiliary.activityExatras(this).child(ActivityWebServicesReports.goLastPageReportName).value.property.value().trim();
		System.out.println("goLastPage " + goReportName);

		if(goReportName.equals(ReportStatusyZakazov.folderKey())){
			//tapReport2(ReportStatusyZakazov.folderKey());
			propSplit.value(layoutless.width().property.value());
			reportSplit.value(layoutless.width().property.value());
			openLastOrCreate(ReportStatusyZakazov.folderKey());
		}else{
			if(goReportName.equals(ReportStatistikaZakazov2.folderKey())){
				propSplit.value(layoutless.width().property.value());
				reportSplit.value(layoutless.width().property.value());
				openLastOrCreate(ReportStatistikaZakazov2.folderKey());
			}else{
				if(goReportName.equals(ReportVzaioraschetySpokupatelem.folderKey())){
					propSplit.value(layoutless.width().property.value());
					reportSplit.value(layoutless.width().property.value());
					openLastOrCreate(ReportVzaioraschetySpokupatelem.folderKey());
				}else{
					System.out.println("goLastPage default");
					String folder = Preferences.string("folder", "").value();
					String file = Preferences.string("file", "").value();
					tapInstance2(folder, file);
				}
			}
		}
	}


	void tapReport2(String folderKey){
		reportGridScrollViewY = reportGrid.scrollView.getScrollY();
		if(preReport != null){
			preReport.writeForm(preKey);
		}
		Report_Base report = createReport(folderKey);
		preReport = report;
		try{
			preKey = report.writeNewReport();
		}catch(Throwable t){
			t.printStackTrace();
		}
		hideParameters();
		propertiesForm.child(report.getParametersView(this)//
				.innerHeight.is(Auxiliary.tapSize * 15)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		report.readForm(preKey);
		report.getParametersView(this).hidden().is(false);
		report.setKey(preKey);
		report.expectRequery.start(this);
	}

	void hideParameters(){
		propertiesForm.removeAllViews();
		propertiesForm.children.removeAllElements();
	}

	void tapInstance2(String folderKey, String instanceKey){

		tapInstanceReal(folderKey, instanceKey, false);
	}

	void resetInstance(String folderKey, String instanceKey){
		tapInstanceReal(folderKey, instanceKey, true);
	}

	void tapInstanceReal(String folderKey, String instanceKey, boolean reset){
		//int scrollY=this.brwsr.getScrollY();
		//report.scrollY=this.brwsr.getScrollY();
		System.out.println("tapInstanceReal " + folderKey + "/" + instanceKey + "/" + reset);
		hideParameters();
		Report_Base report = createReport(folderKey);
		if(report != null){

			propertiesForm.child(report.getParametersView(this)//
					.innerHeight.is(Auxiliary.tapSize * 15)//
					.width().is(layoutless.width().property)//
					.height().is(layoutless.height().property)//
			);
			report.setKey(instanceKey);
			if(preReport != null){
				preReport.writeForm(preKey);
			}
			preReport = report;
			preKey = instanceKey;
			String page = Cfg.pathToHTML(folderKey, instanceKey);
			if(reset){
				report.writeDefaultForm(instanceKey);
			}
			report.readForm(instanceKey);
			//System.out.println(instanceKey+": "+page);
			brwsr.go("file://" + page);
			if(reset){
				report.expectRequery.start(this);
			}
			refreshFog.afterDone.is(new Task(){
				@Override
				public void doTask(){
					//
				}
			}).start(this);
		}
	}

	public void goBlankPage(){
		hideParameters();
		Auxiliary.writeTextToFile(new File("/sdcard/horeca/blanc.html"), "<html></html>", "utf-8");
		brwsr.go("file:///sdcard/horeca/blanc.html");
	}

	public void promptDeleteRepoort(final Report_Base report, final String instanceKey){
		Auxiliary.pickConfirm(ActivityWebServicesReports.this, "Удалить отчёт?", "Удалить", new Task(){
			@Override
			public void doTask(){
				reportGridScrollViewY = reportGrid.scrollView.getScrollY();
				deleteReport(report, instanceKey);
				goBlankPage();
				scrollBack();
			}
		});
	}

	void deleteReport(Report_Base report, String instanceKey){
		new File(Cfg.pathToXML(report.getFolderKey(), instanceKey)).delete();
		new File(Cfg.pathToHTML(report.getFolderKey(), instanceKey)).delete();
		resetMenu2();
	}

	void bindData(){
		System.out.println("bindData");
		//reportGridScrollViewY = reportGrid.scrollView.getScrollY();
		int w = Auxiliary.screenWidth(this);
		reportSplit.value(w * 0.8);
		propSplit.value(w * 0.65);
		resetMenu2();
	}

	void addReportMenu2(final String folderKey, String menuLabel){
		String filter = seekReport.value().trim().toUpperCase();
		if(filter.length() > 1){
			if(!menuLabel.toUpperCase().contains(filter)){
				return;
			}
		}
		Task tap = new Task(){
			@Override
			public void doTask(){
				tapReport2(folderKey);
			}
		};
		if(icon_doc_add == null){
			icon_doc_add = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), sweetlife.android10.R.drawable.i_doc_add), (int)Auxiliary.tapSize, (int)Auxiliary.tapSize, true);
		}
		if(icon_ok == null){
			icon_ok = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), sweetlife.android10.R.drawable.i_ok), (int)Auxiliary.tapSize, (int)Auxiliary.tapSize, true);
		}
		reportIcon.cell(icon_doc_add, tap);
		reportName.cell(menuLabel, tap, "");
		Vector<String> subs = Cfg.reportNames(folderKey);
		for(int i = 0; i < subs.size(); i++){
			final String instKey = subs.get(i).replace(".xml", "");
			File html = new File(Cfg.pathToHTML(folderKey, instKey));
			File xml = new File(Cfg.pathToXML(folderKey, instKey));
			if(instKey.length() > 1 //
					&& html.exists()//
					&& xml.exists()//
			){
				reportIcon.cell(icon_ok, new Task(){
					@Override
					public void doTask(){
						tapInstance2(folderKey, instKey);
					}
				});
				Date d = new Date();
				d.setTime(html.lastModified());
				Report_Base report = createReport(folderKey);
				reportName.cell(report.getShortDescription(instKey), new Task(){
					@Override
					public void doTask(){
						tapInstance2(folderKey, instKey);
					}
				}, report.getOtherDescription(instKey));
			}
		}
	}

	void resetMenu2(){
		System.out.println("resetMenu2");
		reportName.clear();
		reportIcon.clear();

		addReportMenu2(ReportAKBPoTP.folderKey(), ReportAKBPoTP.menuLabel());
		addReportMenu2(ReportAcciiDlyaKlientov.folderKey(), ReportAcciiDlyaKlientov.menuLabel());
		addReportMenu2(ReportAktSverki.folderKey(), ReportAktSverki.menuLabel());
		addReportMenu2(ReportAssortimentFlagmanov.folderKey(), ReportAssortimentFlagmanov.menuLabel());
		addReportMenu2(ReportValovayaPribil.folderKey(), ReportValovayaPribil.menuLabel());
		addReportMenu2(ReportValovayaIerarhia.folderKey(), ReportValovayaIerarhia.menuLabel());
		addReportMenu2(ReportVestnik.folderKey(), ReportVestnik.menuLabel());


		addReportMenu2(ReportVzaioraschetySpokupatelem.folderKey(), ReportVzaioraschetySpokupatelem.menuLabel());
		addReportMenu2(ReportVozvratyPoPodrazelen.folderKey(), ReportVozvratyPoPodrazelen.menuLabel());

		//addReportMenu2(ReportVozmeschenie.folderKey(), ReportVozmeschenie.menuLabel());
		addReportMenu2(ReportVipolnenieDopMotivaciy.folderKey(), ReportVipolnenieDopMotivaciy.menuLabel());
		addReportMenu2(ReportVipolneniePlanovPoPrilojeniu.folderKey(), ReportVipolneniePlanovPoPrilojeniu.menuLabel());
		addReportMenu2(ReportVizity.folderKey(), ReportVizity.menuLabel());
		//addReportMenu2(ReportVipolneniePlanov.folderKey(), ReportVipolneniePlanov.menuLabel());
		addReportMenu2(ReportDegustaciaDlyaTP.folderKey(), ReportDegustaciaDlyaTP.menuLabel());

		addReportMenu2(ReportDZBlizhaishiePlateji.folderKey(), ReportDZBlizhaishiePlateji.menuLabel());


		addReportMenu2(ReportDZDlyaTP.folderKey(), ReportDZDlyaTP.menuLabel());

		addReportMenu2(ReportDistribucia2.folderKey(), ReportDistribucia2.menuLabel());
		addReportMenu2(ReportDistribuciaKrasnodar.folderKey(), ReportDistribuciaKrasnodar.menuLabel());

		//addReportMenu2(ReportDistribucia.folderKey(), ReportDistribucia.menuLabel());
		//addReportMenu2(ReportDistribuciaPoKluchevimPosiciam.folderKey(), ReportDistribuciaPoKluchevimPosiciam.menuLabel());
		//addReportMenu2(ReportDostavkaPoVoditelam.folderKey(), ReportDostavkaPoVoditelam.menuLabel());


		addReportMenu2(ReportZakazyVneMarshruta.folderKey(), ReportZakazyVneMarshruta.menuLabel());

		addReportMenu2(ReportZayavkiNaDobavlenieVmatricu.folderKey(), ReportZayavkiNaDobavlenieVmatricu.menuLabel());
		addReportMenu2(ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta.folderKey(), ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta.menuLabel());

		addReportMenu2(ReportZayavkiNaPerebitieNakladnih.folderKey(), ReportZayavkiNaPerebitieNakladnih.menuLabel());
		addReportMenu2(ReportZayavkiNaUvelichenieLimita.folderKey(), ReportZayavkiNaUvelichenieLimita.menuLabel());

		addReportMenu2(ReportIzbrannoeSmartPro.folderKey(), ReportIzbrannoeSmartPro.menuLabel());

		addReportMenu2(ReportKlassifikaciaKlientov.folderKey(), ReportKlassifikaciaKlientov.menuLabel());
		addReportMenu2(ReportKontragentyPortalaSmart.folderKey(), ReportKontragentyPortalaSmart.menuLabel());
		addReportMenu2(ReportKontragentySPrilojeniem.folderKey(), ReportKontragentySPrilojeniem.menuLabel());
		addReportMenu2(ReportLimity.folderKey(), ReportLimity.menuLabel());
		//addReportMenu2(ReportLocalizasiaDlyaTP.folderKey(), ReportLocalizasiaDlyaTP.menuLabel());
		addReportMenu2(ReportDostavkaPoMarshrutam.folderKey(), ReportDostavkaPoMarshrutam.menuLabel());

		addReportMenu2(ReportMelkieZakazy.folderKey(), ReportMelkieZakazy.menuLabel());

		addReportMenu2(ReportNakladnieNaKontrole.folderKey(), ReportNakladnieNaKontrole.menuLabel());
		addReportMenu2(ReportNalichieSkanov.folderKey(), ReportNalichieSkanov.menuLabel());
		addReportMenu2(ReportNSK.folderKey(), ReportNSK.menuLabel());
		addReportMenu2(ReportObratnaiaSvyazOtKlienta.folderKey(), ReportObratnaiaSvyazOtKlienta.menuLabel());
		addReportMenu2(ReportObratnaiaSvyazPoDogovoram.folderKey(), ReportObratnaiaSvyazPoDogovoram.menuLabel());
		addReportMenu2(ReportObjedineniaKlientov.folderKey(), ReportObjedineniaKlientov.menuLabel());
		//addReportMenu2(ReportOtchetPoKassamDlyaTP.folderKey(), ReportOtchetPoKassamDlyaTP.menuLabel());
		addReportMenu2(ReportPechatSchetaNaOplatu.folderKey(), ReportPechatSchetaNaOplatu.menuLabel());
		addReportMenu2(ReportPoiskKontragentaPoKluchevimPolyam.folderKey(), ReportPoiskKontragentaPoKluchevimPolyam.menuLabel());
		addReportMenu2(ReportProcentZapolneniyaChekListov.folderKey(), ReportProcentZapolneniyaChekListov.menuLabel());

		addReportMenu2(ReportTovarnieVozvrati.folderKey(), ReportTovarnieVozvrati.menuLabel());
		addReportMenu2(ReportTovarnieNakladmie.folderKey(), ReportTovarnieNakladmie.menuLabel());
		if(Cfg.territory().children.size() > 1){
			addReportMenu2(ReportPlanPolevihRabot.folderKey(), ReportPlanPolevihRabot.menuLabel());
		}
		addReportMenu2(ReportPokazateliKPI.folderKey(), ReportPokazateliKPI.menuLabel());
		addReportMenu2(ReportPredzakazyNaTrafiki.folderKey(), ReportPredzakazyNaTrafiki.menuLabel());
		//addReportMenu2(ReportProbegTPSV.folderKey(), ReportProbegTPSV.menuLabel());

		addReportMenu2(ReportProdazhiSTM.folderKey(), ReportProdazhiSTM.menuLabel());
		addReportMenu2(ReportProdajiFlagmanov.folderKey(), ReportProdajiFlagmanov.menuLabel());
		addReportMenu2(ReportProdajiFlagmanovPoKontragentam.folderKey(), ReportProdajiFlagmanovPoKontragentam.menuLabel());


		addReportMenu2(ReportRV.folderKey(), ReportRV.menuLabel());

		addReportMenu2(ReportResultatyUtverjdenihSpecifikaciy.folderKey(), ReportResultatyUtverjdenihSpecifikaciy.menuLabel());


		//addReportMenu2(ReportRekomendaciiKlientam.folderKey(), ReportRekomendaciiKlientam.menuLabel());
		addReportMenu2(ReportSvodDlyaTP.folderKey(), ReportSvodDlyaTP.menuLabel());
		addReportMenu2(ReportSkidkiDlyaKlientov.folderKey(), ReportSkidkiDlyaKlientov.menuLabel());
		addReportMenu2(ReportSKUsOtgruzkami.folderKey(), ReportSKUsOtgruzkami.menuLabel());
		addReportMenu2(ReportSootvetstvieMercury.folderKey(), ReportSootvetstvieMercury.menuLabel());
		addReportMenu2(ReportSravnitelniyAnaliz.folderKey(), ReportSravnitelniyAnaliz.menuLabel());
		addReportMenu2(ReportStatistikaVozvratovAktovSverki.folderKey(), ReportStatistikaVozvratovAktovSverki.menuLabel());


		addReportMenu2(ReportStatistikaZakazov2.folderKey(), ReportStatistikaZakazov2.menuLabel());
		addReportMenu2(ReportStatisticaPodpisannihDS.folderKey(), ReportStatisticaPodpisannihDS.menuLabel());

		addReportMenu2(ReportStatusyVozvratov.folderKey(), ReportStatusyVozvratov.menuLabel());
		addReportMenu2(ReportStatusyZakazov.folderKey(), ReportStatusyZakazov.menuLabel());
		//addReportMenu2(ReportStatusyRasporjazheniy.folderKey(), ReportStatusyRasporjazheniy.menuLabel());
		addReportMenu2(ReportStatusyRasporjazheniySKD.folderKey(), ReportStatusyRasporjazheniySKD.menuLabel());

		addReportMenu2(ReportBonusyDlyaTP.folderKey(), ReportBonusyDlyaTP.menuLabel());
		//addReportMenu2(ReportTrafiki.folderKey(), ReportTrafiki.menuLabel());
		addReportMenu2(ReportUsloviaOtgruzki.folderKey(), ReportUsloviaOtgruzki.menuLabel());
		addReportMenu2(ReportFixirovanieKoordinat.folderKey(), ReportFixirovanieKoordinat.menuLabel());
		addReportMenu2(ReportFixirovannieCeny.folderKey(), ReportFixirovannieCeny.menuLabel());

		reportGrid.refresh();
		waitForReportGridLayout();
	}

	void doHOOKPerebitieNakladnoy(final String num){
		Auxiliary.pick3Choice(this, "Заявка №" + num, "Утверждение заявки на перебитие накладной"//
				, "Утвердить", new Task(){
					@Override
					public void doTask(){
						sendPerebitie(true, num);
					}
				}, "Запретить", new Task(){
					@Override
					public void doTask(){
						sendPerebitie(false, num);
					}
				}, "Добавить файл", new Task(){
					@Override
					public void doTask(){
						showPerebitFileChooser(num);
					}
				});
	}

	void showPerebitFileChooser(String num){
		ActivityWebServicesReports.selectedDocNum = num;
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try{
			Intent chooser = Intent.createChooser(intent, "Выбрать файл");
			startActivityForResult(chooser, FILE_PEREBIT_SKD_RESULT);
		}catch(Throwable ex){
			ex.printStackTrace();
		}
	}

	void sendPerebitie(final boolean otkaz, final String num){
		System.out.println("sendPerebitie " + num + "/" + otkaz);
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							//String url = "https://testservice.swlife.ru/shatov"
							+ "/hs/PerebitieNakladnoy"
							+ "/" + URLEncoder.encode("Утвердить", "utf-8")//
							+ "/" + URLEncoder.encode(num, "utf-8")//
							+ "/" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
							+ "/" + URLEncoder.encode((otkaz ? "нет" : "да"), "utf-8")//
							;
					Report_Base.startPing();
					System.out.println(url);
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					System.out.println(msg);
					b.child("result").value.is(msg);
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void doHOOKSKD(final String num){
		ActivityWebServicesReports.selectedDocNum = num;
		Auxiliary.pickConfirm(this, "Распоряжение №" + num, "Добавить файл", new Task(){
			@Override
			public void doTask(){
				showSKDFileChooser();
			}
		});
	}

	void doHOOKVzaimporascheti(final String num){
		Auxiliary.pick3Choice(this, "№" + num, "Создать счёт по реализации."//
				, "На оплату", new Task(){
					@Override
					public void doTask(){
						createSchetNaoplatu(num);
					}
				}, "УПД", new Task(){
					@Override
					public void doTask(){
						createPechatUPD(num);
					}
				}, null, null);
		/*Auxiliary.pickConfirm(this, "Счёт на оплату по реализации №" + num, "Создать"//
				, new Task() {
					@Override
					public void doTask() {
						createSchetNaoplatu(num);
					}
				});*/
	}

	void doHOOKReturn(final String num, final String dat){
		Auxiliary.pick3Choice(this, "Заявка №" + num + " от " + dat, "Утверждение заявки на возврат."//
				, "Утвердить", new Task(){
					@Override
					public void doTask(){
						sendReturnApprove(true, num);
					}
				}, "Запретить", new Task(){
					@Override
					public void doTask(){
						sendReturnApprove(false, num);
					}
				}, "Удалить", new Task(){
					@Override
					public void doTask(){
						sendReturnDelete(num, dat);
					}
				});
	}

	void sendFixirovanieKoordinat(final String num){
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/UtvKoordinat/" + num + "/" + Cfg.whoCheckListOwner();
		final Note result = new Note().value("Утверждение координат");
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					//String txt = Auxiliary.parseChildOrRaw(msg, "Message");
					result.value(result.value() + "\n" + msg);
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendDegustaciaState(final String num, final int state){
		final Note result = new Note().value("Заявка на дегустацию");
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = //https://service.swlife.ru/hrc120107/hs/Planshet/ApproveApplicationTasting/000032664/1
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/ApproveApplicationTasting/" + num + "/" + state;
					//System.out.println(url);
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					/*System.out.println(msg);
					String txt = msg;
					Bough bb = Bough.parseJSON(msg);
					String mm = bb.child("Message").value.property.value();
					if (mm.length() > 0) {
						txt = mm;
					}*/
					String txt = Auxiliary.parseChildOrRaw(msg, "Message");
					result.value(result.value() + "\n" + txt);
				}catch(Throwable t){
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendDeleteSpec(String url){
		System.out.println(url);
		final Note result = new Note().value("Удаление");
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					//String txt = Auxiliary.parseChildOrRaw(msg, "Message");
					result.value(result.value() + "\n" + msg);
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendReportApproveTerrChange(final String num, boolean approve){
		System.out.println("sendReportApproveTerrChange " + num + ": " + approve);
		final Note result = new Note().value("Результат");
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String act = "Ложь/Истина/";
					if(approve){
						act = "Истина/Ложь/";
					}
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaKlienta/UtvIzmPodrr/" + num + "/" + act;
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
							//,"bot28","Molgav1024"
					);
					String msg = new String(bytes, "UTF-8");
					result.value(result.value() + "\n" + msg);
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void promptReportApproveTerrChange(final String num){
		Auxiliary.pick3Choice(this, "№" + num, "Заявка на смену территории"
				, "Утвердить", new Task(){
					@Override
					public void doTask(){
						sendReportApproveTerrChange(num, true);
					}
				}
				, "Отказать", new Task(){
					@Override
					public void doTask(){
						sendReportApproveTerrChange(num, false);
					}
				}
				, null, null
		);
	}

	void sendDropSingleFavSmartPro(String kodKlienta, String art){
		Context cc = this;
		final Note result = new Note();
		String artO = "[{\"article\":\"" + art + "\",\"status\":false}]";
		System.out.println(artO);
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/smartpro/favourites/"
							+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod()
							+ "/" + kodKlienta;
					System.out.println(artO);
					Bough b = Auxiliary.loadTextFromPrivatePOST(url
							, artO.getBytes("UTF-8"), 30 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					Bough raw = Bough.parseJSON(b.child("raw").value.property.value());
					result.value(raw.child("Сообщение").value.property.value());
				}catch(Exception e){
					e.printStackTrace();
					result.value(e.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){

				tapInstanceReal(preReport.getFolderKey(), preKey, true);

				Auxiliary.warn("Отправлено: " + result.value(), cc);
			}
		}).status.is("Отправка").start(this);
	}

	void promptDelFavSmartPro(String art, String kodKlienta){
		Auxiliary.pickConfirm(this, "Удалить артикул " + art, "Удалить", new Task(){
			public void doTask(){
				sendDropSingleFavSmartPro(kodKlienta, art);
			}
		});
	}

	void sendFavSmartPro(String kodKlienta, String artLine){
		Context cc = this;
		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/smartpro/favourites/"
							+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod()
							+ "/" + kodKlienta;
					Bough b = Auxiliary.loadTextFromPrivatePOST(url
							, artLine.getBytes("UTF-8"), 30 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					Bough raw = Bough.parseJSON(b.child("raw").value.property.value());
					result.value(raw.child("Сообщение").value.property.value());
				}catch(Exception e){
					e.printStackTrace();
					result.value(e.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){

				tapInstanceReal(preReport.getFolderKey(), preKey, true);

				Auxiliary.warn("Отправлено: " + result.value(), cc);
			}
		}).status.is("Отправка").start(this);
	}

	void showArtDialog(Vector<String> arts, String kodKlienta){
		Vector<String> preArts = new Vector<String>();
		for(int ii = 0; ii < arts.size(); ii++){
			preArts.add(arts.get(ii));
		}
		VibratNomenklatura dialog = new VibratNomenklatura();
		dialog.showArtDialog(this, arts, new Task(){
			public void doTask(){
				String artline = "";
				String dlmtr = "";
				for(int ii = 0; ii < arts.size(); ii++){
					if(arts.get(ii).trim().length() > 1){
						artline = artline + dlmtr + "{\"article\":\"" + arts.get(ii).trim() + "\",\"status\":true}";
						dlmtr = ", ";
					}
				}
				for(int kk = 0; kk < preArts.size(); kk++){
					boolean toDel=true;
					for(int ii = 0; ii < arts.size(); ii++){
						if(arts.get(ii).trim().equals(preArts.get(kk).trim())){
							toDel=false;
							break;
						}
					}
					if(toDel){
						artline = artline + dlmtr + "{\"article\":\"" + preArts.get(kk).trim() + "\",\"status\":false}";
						dlmtr = ", ";
					}
				}
				System.out.println("artline " + artline);
				sendFavSmartPro(kodKlienta, "[" + artline + "]");
			}
		});
	}

	void promptReportFavSmartPro(String kodKlienta){

		String sql = "select _idrref as _idrref from kontragenty where kod='" + kodKlienta + "';";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		ApplicationHoreca.getInstance().setClientInfo(new ClientInfo(ApplicationHoreca.getInstance().getDataBase()
				, "x'" + data.child("row").child("_idrref").value.property.value() + "'"));

		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/smartpro/favourites/"
				+ Cfg.selectedOrDbHRC()
				+ "/" + kodKlienta;
		//
		final Bough result = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					result.children = Bough.parseJSON(msg).children;
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Vector<String> arts = new Vector<String>();
				Vector<Bough> lines = result.children("ТоварыИзбранное");
				for(int ii = 0; ii < lines.size(); ii++){
					arts.add(lines.get(ii).value.property.value());
				}
				showArtDialog(arts, kodKlienta);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void promptReportDeleteSpec(final String num, final String art){
		final Numeric sel = new Numeric();
		Auxiliary.pickSingleChoice(this, new String[]{"Удалить всю заявку №" + num, "Удалить артикул " + art + " из заявки"}, sel, "№" + num, new Task(){
			@Override
			public void doTask(){
				if(sel.value() == 0){
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkiNaFiksCeny/DeleteSpec/" + num;
					sendDeleteSpec(url);
				}else{
					if(sel.value() == 1){
						String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkiNaFiksCeny/DeleteLineSpec/" + num + "/" + art;
						sendDeleteSpec(url);
					}
				}
			}
		}, null, null, null, null);
	}

	void doHOOKFixirovanieKoordinat(final String num){
		final Numeric sel = new Numeric();
		Auxiliary.pickConfirm(this, "Новые координаты локализации в заявке №" + num, "Утвердить", new Task(){
			@Override
			public void doTask(){
				sendFixirovanieKoordinat(num);
			}
		});
	}

	void doHOOKDegustacia(final String num){
		final Numeric sel = new Numeric();
		Auxiliary.pickSingleChoice(this, new String[]{"Отказать", "Утвердить"}, sel, "№" + num, new Task(){
			@Override
			public void doTask(){
				sendDegustaciaState(num, sel.value().intValue());
			}
		}, null, null, null, null);
	}

	void sendLimityState(final String num, final int state, final String comment){
		final Note result = new Note().value("Заявка на увеличение лимитов");
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{

					String encodedtxt = URLEncoder.encode(comment, "UTF-8");
					if(encodedtxt.length() < 1){
						encodedtxt = "-";
					}
					encodedtxt = encodedtxt.replace("+", "%20");
					encodedtxt = encodedtxt.replace("/", "%2F");
					encodedtxt = encodedtxt.replace("\\", "%5C");
					//System.out.println("sendLimityState "+encodedtxt+"/"+comment);
					String url = //https://service.swlife.ru/hrc120107/hs/Planshet/ApproveApplicationTasting/000032664/1
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/Planshet/ApproveApplicationIncreaseLimit/" + num + "/" + state + "/" + encodedtxt;
					//System.out.println("sendLimityState "+url);
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					String txt = Auxiliary.parseChildOrRaw(msg, "Message");
					result.value(result.value() + "\n" + txt);
				}catch(Throwable t){
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void promptUtverditZayavkuNaDobavlenieKlienta(final String num){
		Auxiliary.pickConfirm(this, "Утвердить заявку №" + num, "Утвердить", new Task(){
			public void doTask(){
				final Note result = new Note().value("Утвердить заявку №" + num);
				Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/KlientVMatricu/?Ndok=" + num + "&Utverdit=1&user=" + Cfg.whoCheckListOwner();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							String msg = new String(bytes, "UTF-8");
							String txt = Auxiliary.parseChildOrRaw(msg, "Message");
							result.value(result.value() + "\n" + txt);
						}catch(Throwable t){
							t.printStackTrace();
							result.value(result.value() + "\n" + t.getMessage());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
				expect.start(ActivityWebServicesReports.this);
			}
		});
	}

	void promptOtkazatZayavkuNaDobavlenieKlienta(final String num){
		Auxiliary.pickConfirm(this, "Отказать заявке №" + num, "Отказать", new Task(){
			public void doTask(){
				final Note result = new Note().value("Отказать заявке №" + num);
				Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/KlientVMatricu/?Ndok=" + num + "&Utverdit=0&user=" + Cfg.whoCheckListOwner();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							String msg = new String(bytes, "UTF-8");
							String txt = Auxiliary.parseChildOrRaw(msg, "Message");
							result.value(result.value() + "\n" + txt);
						}catch(Throwable t){
							t.printStackTrace();
							result.value(result.value() + "\n" + t.getMessage());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(result.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
				expect.start(ActivityWebServicesReports.this);
			}
		});
	}

	void doHOOKObratnayaSviaz(String docNum){
		docNumObratnayaSvyazKlient.value(docNum);
		getChatCurrent();
	}

	/*
		void doHOOKHookArtDocDateSent(String docNum, String docDate, String docArt){
			//System.out.println(docNum + "/" + docDate + "/" + docArt);
			//Auxiliary.warn(docNum + "/" + docDate + "/" + docArt,this);
			docNumObratnayaSvyazKlient.value(docNum);
			artikulObratnayaSvyazKlient.value(docArt);
			try{
				dateObratnayaSvyazKlient.value((double)(Auxiliary.ddMMyyyy.parse(docDate).getTime()));
				//promptObratnayaSvyazKlient(this);
				getChatCurrent();
			}catch(Throwable t){
				Auxiliary.warn("Неверная дата " + docDate + " " + t.getMessage(), this);
			}
		}
	*/
	void getChatCurrent(){
		//String url="https://service.swlife.ru/hrc120107/hs/FeedbackCustomer/receive/hrc221"；
		final String url = Settings.getInstance().getBaseURL()
				+ Settings.selectedBase1C() + "/hs/FeedbackCustomer/receive/" + Cfg.whoCheckListOwner()
				+ "/" + docNumObratnayaSvyazKlient.value();
		System.out.println("url " + url);
		/*String body = "{"
				+ "\n	\"DateNac\": \"" + Auxiliary.mssqlTime.format(new Date(dateObratnayaSvyazKlient.value().longValue())) + "\""
				//+ "\n	,\"CodClient\": \"\"" //\"134567\""// + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\","
				+ "\n	,\"NumberNac\": \"" + docNumObratnayaSvyazKlient.value() + "\""
				+ "\n	,\"Article\": \"" + artikulObratnayaSvyazKlient.value() + "\""
				+ "\n}";*/
		//String body = "{\"NumberNac\": \"" + docNumObratnayaSvyazKlient.value() + "\"}";
		//System.out.println("body " + body);
		final Bough obratnayaSvyazOtKlienta = new Bough();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//Bough b = Auxiliary.loadTextFromPrivatePOST(url, body.getBytes("UTF-8"), 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					//String raw = b.child("raw").value.property.value();
					//Bough data = Bough.parseJSON(raw);
					//System.out.println(data.dumpXML());
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					Bough data = Bough.parseJSON(new String(bytes));
					System.out.println(data.dumpXML());
					//obratnayaSvyazOtKlienta.children = data.child("ОбратнаяСвязьОтКлиента").children;
					obratnayaSvyazOtKlienta.children = data.children;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//Auxiliary.warn("Результат: " + result.value(), activity);
				/*if(obratnayaSvyazOtKlienta.child("Номер").value.property.value().equals("null")){

					promptObratnayaSvyazKlient(ActivityWebServicesReports.this);
				}else{
					promptChatArtMessages(obratnayaSvyazOtKlienta);

				}*/
				promptChatArtMessages(obratnayaSvyazOtKlienta);
			}
		}).status.is("Отправка").start(this);
	}

	void promptChatArtMessages(Bough obratnayaSvyazOtKlienta){
		//Auxiliary.warn(answer.dumpXML(),ActivityWebServicesReports.this);
		System.out.println("promptChatArtMessages " + obratnayaSvyazOtKlienta.dumpXML());
		Vector<Bough> items = obratnayaSvyazOtKlienta.children("Переписка");
		//final String Nomer = obratnayaSvyazOtKlienta.child("Номер").value.property.value();
		String chat = "";
		for(int ii = 0; ii < items.size(); ii++){
			Bough one = items.get(items.size() - ii - 1);
			String line = Auxiliary.tryReFormatDate(one.child("Дата").value.property.value(), "yyyy-MM-dd'T'HH:mm:ss", "dd.MM.yy HH:mm:ss") + ": ";
			line = line + one.child("Пользователь").value.property.value() + ": ";
			line = line + one.child("Текст").value.property.value().trim() + "\n";
			chat = chat + line;
		}
		//System.out.println(chat);
		//Auxiliary.warn(chat,ActivityWebServicesReports.this);
		final Note newStr = new Note();
		Auxiliary.pick(ActivityWebServicesReports.this, "", new SubLayoutless(ActivityWebServicesReports.this)//
						.child(new Decor(ActivityWebServicesReports.this).labelText.is(chat).labelAlignLeftBottom()
								.top().is(Auxiliary.tapSize * 0.5)
								.left().is(Auxiliary.tapSize * 0.5)
								.width().is(Auxiliary.tapSize * 9)
								.height().is(Auxiliary.tapSize * 7))//
						.child(new RedactText(ActivityWebServicesReports.this).text.is(newStr)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 7.5)
								.width().is(Auxiliary.tapSize * 9)
								.height().is(Auxiliary.tapSize * 1))//
						.width().is(Auxiliary.tapSize * 10)//
						.height().is(Auxiliary.tapSize * 10)//
				, "Добавить комментарий", new Task(){
					@Override
					public void doTask(){
						System.out.println("otpr" + newStr.value());
						sendArtikulComment(newStr.value(), docNumObratnayaSvyazKlient.value());
					}
				}, null, null, null, null);
	}

	void sendArtikulComment(String txt, String Nomer){
		//https://service.swlife.ru/hrc120107/hs/FeedbackCustomer/message/hrc221
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/FeedbackCustomer/message/" + Cfg.whoCheckListOwner();
		System.out.println("url " + url);
		String body = "{"
				+ "\n	\"NumberDoc\": \"" + Nomer + "\""
				+ "\n	,\"Comment\": \"" + txt.replace("\n", " ").replace("\r", " ").replace("\"", " ").replace("  ", " ") + "\""
				+ "\n}";
		System.out.println("body " + body);
		final Bough response = new Bough();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, body.getBytes("UTF-8"), 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					String raw = b.child("raw").value.property.value();
					Bough data = Bough.parseJSON(raw);
					//System.out.println(data.dumpXML());
					response.children = data.children;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//Auxiliary.warn("Результат: " + result.value(), activity);
				System.out.println(response.dumpXML());
				getChatCurrent();
				if(response.child("Статус").value.property.value().equals("0")){
					//
				}else{
					Auxiliary.warn("Отправка: " + response.dumpXML(), ActivityWebServicesReports.this);
				}
			}
		}).status.is("Отправка").start(this);
	}

	public static void promptObratnayaSvyazKlient(Activity activity, final String artikul, String name, final String kodKlienta, String clientName){

		Auxiliary.pick(activity, "", new SubLayoutless(activity)//
						.child(new Decor(activity).labelText.is("Обратная связь").top().is(Auxiliary.tapSize * 0.5).left().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(activity).labelText.is("контрагент").labelAlignRightTop().top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new Decor(activity).labelText.is(clientName).labelAlignLeftCenter().left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(activity).labelText.is("номенклатура").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new Decor(activity).labelText.is(name).labelAlignLeftCenter().left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						//.child(new Decor(activity).labelText.is("дата реализации").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						//.child(new RedactDate(activity).date.is(dateObratnayaSvyazKlient).format.is("dd.MM.yyyy").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(activity).labelText.is("комментарий").labelAlignRightTop().top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(activity).text.is(commentObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 3.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						//.child(new Decor(activity).labelText.is("артикул").labelAlignRightTop().top().is(Auxiliary.tapSize * 4.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						//.child(new RedactText(activity).text.is(artikulObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(activity).labelText.is("фото этикетки").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(activity).text.is(etiketkaObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 7.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(activity).labelText.is("X").afterTap.is(new Task(){
							@Override
							public void doTask(){
								etiketkaObratnayaSvyazKlient.value("");
							}
						}).left().is(Auxiliary.tapSize * 7.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(activity).labelText.is("...").afterTap.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.startMediaGallery(activity, etiketkaIDPicture);
							}
						}).left().is(Auxiliary.tapSize * 8.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(activity).labelText.is("фото товара").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 7).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(activity).text.is(tovarObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 7.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(activity).labelText.is("X").afterTap.is(new Task(){
							@Override
							public void doTask(){
								tovarObratnayaSvyazKlient.value("");
							}
						}).left().is(Auxiliary.tapSize * 7.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(activity).labelText.is("...").afterTap.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.startMediaGallery(activity, tovarIDPicture);
							}
						}).left().is(Auxiliary.tapSize * 8.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//


						.width().is(Auxiliary.tapSize * 10)//
						.height().is(Auxiliary.tapSize * 11)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						if(okObratnayaSvyazKlient(activity)){
							sendObratnayaSvyazKlient(activity, artikul, kodKlienta);
						}
					}
				}, null, null, null, null);
	}

	public static boolean okObratnayaSvyazKlient(Activity activity){
		/*if(docNumObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не заполнен номер реализации", activity);
			return false;
		}
		if(dateObratnayaSvyazKlient.value() < 3){
			Auxiliary.warn("Не заполнена дата реализации", activity);
			return false;
		}*/
		if(commentObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не заполнен комментарий", activity);
			return false;
		}
		/*if(artikulObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не указан артикул", activity);
			return false;
		}*/
		if(etiketkaObratnayaSvyazKlient.value().length() < 3 && tovarObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не добавлены фото", activity);
			return false;
		}
		return true;
	}

	public static void sendObratnayaSvyazKlient(Activity activity, String artikul, String kodKlienta){
		final String url = Settings.getInstance().getBaseURL()
				+ Settings.selectedBase1C() + "/hs/FeedbackCustomer/Создать/" + Cfg.whoCheckListOwner();
		System.out.println("url " + url);
		String NumberDoc = artikul + "_" + kodKlienta;/*docNumObratnayaSvyazKlient.value()
				+ "-" + Auxiliary.sqliteDate.format(new Date(dateObratnayaSvyazKlient.value().longValue()))
				+ "-" + artikulObratnayaSvyazKlient.value();*/
		String data = "{"
				+ "\n	\"CodClient\": \"" + kodKlienta + "\""
				+ "\n	,\"Article\": \"" + artikul + "\""
				+ "\n	,\"DateNac\": \"\""
				+ "\n	,\"Comment\": \"" + commentObratnayaSvyazKlient.value() + "\""
				+ "\n	\"Files\": [";
		if(etiketkaObratnayaSvyazKlient.value().length() > 3){
			String filePath = etiketkaObratnayaSvyazKlient.value().trim();
			String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
			String[] spl = filePath.split("\\.");
			String rash = spl[spl.length - 1];
			data = data + "\n		{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			if(tovarObratnayaSvyazKlient.value().length() > 3){
				filePath = tovarObratnayaSvyazKlient.value().trim();
				encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
				spl = filePath.split("\\.");
				rash = spl[spl.length - 1];
				data = data + "\n		,{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			}
		}else{
			if(tovarObratnayaSvyazKlient.value().length() > 3){
				String filePath = tovarObratnayaSvyazKlient.value().trim();
				String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
				String[] spl = filePath.split("\\.");
				String rash = spl[spl.length - 1];
				data = data + "\n		{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			}
		}
		data = data + "\n	]"
				+ "\n}"
				+ "\n";
		final String body = data;
		//https://service.swlife.ru/hrc120107/hs/FeedbackCustomer/Создать/hrc221
		//if(etiketkaObratnayaSvyazKlient.value().length() < 3 && tovarObratnayaSvyazKlient.value().length() < 3){
		//14:44
		System.out.println("body " + body);
		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//Bough b = Auxiliary.loadTextFromPublicPOST(url, post, 99, "UTF-8");
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, body.getBytes("UTF-8"), 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					System.out.println(b.dumpXML());
					result.value(Bough.parseJSON(b.child("raw").value.property.value()).child("Сообщение").value.property.value());

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//getChatCurrent();
				Auxiliary.warn("Результат: " + result.value(), activity);
			}
		}).status.is("Отправка").start(activity);
	}

	void doHOOKHookZayavkiNaDobavlenieVmatricu(final String num){
		//Auxiliary.warn(num,this);
		Auxiliary.pick3Choice(this, "Заявка на добавление клиента", "№" + num
				/*, "Удаление визита", new Task() {
					@Override
					public void doTask() {
						//
					}
				}*/
				, null, null
				, "Утвердить", new Task(){
					@Override
					public void doTask(){
						promptUtverditZayavkuNaDobavlenieKlienta(num);
					}
				}, "Отказать", new Task(){
					@Override
					public void doTask(){
						promptOtkazatZayavkuNaDobavlenieKlienta(num);
					}
				}
		);
	}

	void doHOOKReportLimity(final String num){
		//final Numeric sel=new Numeric();
		final Note coment = new Note();
		/*Auxiliary.pickSingleChoice(this,new String[]{"Отказать","Утвердить"},sel,"№"+num,new Task(){
			@Override
			public void doTask() {
				sendLimityState(num,sel.value().intValue());
			}
		},null,null,null,null);*/
		Auxiliary.pickString(this, "Заявка №" + num, coment
				, "Утвердить", new Task(){
					@Override
					public void doTask(){
						sendLimityState(num, 1, coment.value());
					}
				}, "Отказать", new Task(){
					@Override
					public void doTask(){
						sendLimityState(num, 0, coment.value());
					}
				}, "Отмена", new Task(){
					@Override
					public void doTask(){
						//
					}
				}
		);
	}

	void doHOOKAnswer(final String num, final String art){
		final Note text = new Note();
		Auxiliary.pickString(this, "№" + num + ", арт." + art, text, "Ответить", new Task(){
			public void doTask(){
				sendReturnAnswer(num, art, text.value());
			}
		});
	}

	void sendReturnAnswer(final String num, final String art, final String text){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/SoglasovanieVozvrata/" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
							+ "/" + URLEncoder.encode(num, "utf-8")//
							+ "/" + URLEncoder.encode(art, "utf-8")//
							;
					System.out.println("sendReturnAnswer " + url);
					Report_Base.startPing();
					Bough result;
					result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8"), 12000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					b.child("result").value.is(result.child("message").value.property.value());
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendFixPriceAnswer(final String num, final String art, final String text){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = //"http://10.10.5.2/lednev_hrc/"
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/fixprice/?Ndok=" + URLEncoder.encode(num, "utf-8")//
									+ "&Art=" + URLEncoder.encode(art, "utf-8")//
									+ "&hrc=" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
									+ "&ukaz=2"//
									+ "&chat=" + URLEncoder.encode(text.trim(), "utf-8")//
							;
					Report_Base.startPing();
					Bough result = new Bough();
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					b.child("result").value.is(new String(bytes, "UTF-8"));
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendReturnDelete(final String num, final String dat){
		System.out.println(num + "/" + dat);
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/UdalenieZayavkiNaVozvrat"
									+ "/" + URLEncoder.encode(num.trim(), "utf-8")
									+ "/" + URLEncoder.encode(Auxiliary.tryReFormatDate(dat, "dd.MM.yyyy", "yyyyMMdd"), "utf-8")//
									;
							Report_Base.startPing();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							b.child("result").value.is(new String(bytes));
							preReport.writeCurrentPage();
						}catch(Throwable t){
							t.printStackTrace();
							b.child("result").value.is(t.toString());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendReturnApprove(final boolean approve, final String num){
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task(){
			@Override
			public void doTask(){
				r.url.is(Settings.getInstance().getBaseURL() + "UtvVozvrat.1cws")//
						.xml.is("<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
										+ "\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
										+ "\n	<soap:Body>"//
										+ "\n		<Utv xmlns=\"http://ws.swl/UtvVozvrat\">"//
										+ "\n			<Nomer>" + num + "</Nomer>"//
										+ "\n			<Deistvie>" + (
										approve
												? "0"
												: "1"
								) + "</Deistvie>"//
										+ "\n			<Polzovatel>" + Cfg.whoCheckListOwner() + "</Polzovatel>"//
										+ "\n		</Utv>"//
										+ "\n	</soap:Body>"//
										+ "\n</soap:Envelope>"//
						);
				Report_Base.startPing();
				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				preReport.writeCurrentPage();
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				if(r.exception.property.value() != null){
					Auxiliary.warn("Исключение: " + r.statusCode.property.value() + ": " + r.exception.property.value().getMessage(), ActivityWebServicesReports.this);
				}else{
					if(r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300){
						Auxiliary.warn("Результат: " //
								+ r.data.child("soap:Body")//
								.child("m:UtvResponse")//
								.child("m:return")//
								.value.property.value(), ActivityWebServicesReports.this);
					}else{
						Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityWebServicesReports.this);
					}
				}
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		}).afterCancel.is(new Task(){
			@Override
			public void doTask(){
				//
			}
		}).start(this);
	}

	void sendDropWholeFix(final String num){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = //"http://89.109.7.162/lednev_hrc/hs/fixprice/"//
									//"http://89.109.7.162/shatov"
									Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
											+ "/hs/ZayavkiNaFiksCeny/Delete"
											+ "/" + URLEncoder.encode(num.trim(), "utf-8")
											+ "/" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
									;
							Report_Base.startPing();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							b.child("result").value.is(new String(bytes));
							preReport.writeCurrentPage();
						}catch(Throwable t){
							t.printStackTrace();
							b.child("result").value.is(t.toString());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
		expect.start(ActivityWebServicesReports.this);
	}

	void promptDropWholeFix(final String num){
		Auxiliary.pickConfirm(this, "Удалить заявку №" + num, "Удалить", new Task(){
			public void doTask(){
				sendDropWholeFix(num);
			}
		});
	}

	void sendDropSingleFix(final String num, final String artikul){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{//http://service.swlife.ru/hrc120107/hs/ZayavkiNaFiksCeny/DeleteLine/0000709846/bot35/0
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/ZayavkiNaFiksCeny/DeleteLine"
									+ "/" + URLEncoder.encode(num.trim(), "utf-8")
									+ "/" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
									+ "/" + artikul.trim()//
									;
							Report_Base.startPing();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());

							System.out.println(url + ": " + new String(bytes));
							b.child("result").value.is(new String(bytes));
							preReport.writeCurrentPage();
						}catch(Throwable t){
							t.printStackTrace();
							b.child("result").value.is(t.toString());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
		expect.start(ActivityWebServicesReports.this);
	}

	void promptDropSingleFix(final String num, final String artikul){
		Auxiliary.pickConfirm(this, "Удалить артикул " + artikul + " из заявки №" + num, "Удалить", new Task(){
			public void doTask(){
				sendDropSingleFix(num, artikul);
			}
		});

		/*final Numeric stroka = new Numeric();
		Auxiliary.pickNumber(this, "Номер строки в заявке", stroka, "Удалить", new Task() {
			public void doTask() {
				sendDropSingleFix(num, "" + stroka.value().intValue());
			}
		}, null, null);*/
	}

	void promptPoKontragentu(final String num){
		//System.out.println(num);
		/*Auxiliary.pick(this, "Утвердить всё с таким же контрагентом" , "Утвердить", new Task() {
			public void doTask() {

			}
		});*/
		Calendar cfrom = Calendar.getInstance();
		//cfrom.add(Calendar.DATE, 1);
		Calendar cto = Calendar.getInstance();
		cto.add(Calendar.DATE, 1);
		final Numeric from = new Numeric().value((float)cfrom.getTimeInMillis());
		final Numeric to = new Numeric().value((float)cto.getTimeInMillis());
		Auxiliary.pick(this, "Утвердить всё с таким же контрагентом", new SubLayoutless(this)//
						.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(from)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 3)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(to)
								.left().is(Auxiliary.tapSize * 4)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 3)
								.height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 7.5)//
						.height().is(Auxiliary.tapSize * 4)//
				, "Утвердить", new Task(){
					public void doTask(){
						//SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd")short1cDate
						String dfrom = Auxiliary.short1cDate.format(new Date(from.value().longValue()));
						String dto = Auxiliary.short1cDate.format(new Date(to.value().longValue()));
						//String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
						String sql = "select pz.kod as kod from Polzovateli pp join Podrazdeleniya pz on pp.podrazdelenie=pz._idrref where pp.kod='" + Cfg.whoCheckListOwner() + "'";
						Bough rawdata = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
						String podrkod = rawdata.child("row").child("kod").value.property.value();
						String body = "{\"НачДата\":\"" + dfrom + "\"";
						body = body + ",\"КонДата\":\"" + dto + "\"";
						body = body + ",\"hrc\":\"" + Cfg.whoCheckListOwner() + "\"";
						body = body + ",\"Подразделение\":\"" + podrkod + "\"";
						body = body + ",\"Ndok\":\"" + num + "\"";
						body = body + "}";
						System.out.println(body);
						String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/fixprice/UtvVse";
						ReportFixirovannieCeny.sendApproveAll(ActivityWebServicesReports.this, url, body);
					}
				}, null, null, null, null);
	}

	void doHOOKApproveFixAction(int nn, String art, String num){
		if(nn == 0){
			promptDropWholeFix(num);
		}else{
			if(nn == 1){
				promptDropSingleFix(num, art);
			}else{
				if(nn == 2){
					sendFixApprove(art, num, "1", null);
				}else{
					if(nn == 3){
						sendFixApprove(art, num, "0", null);
					}else{
						if(nn == 4){
							promptFixPriceTxt(num, art);
						}else{
							if(nn == 5){
								allFixApprove();
							}else{
								if(nn == 6){
									promptPoKontragentu(num);
								}else{
									if(nn == 7){
										promptFixPriceNumber(num, art);
									}else{
										if(nn == 8){
											promptFixPriceNumber2(num, art);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	void doHOOKApproveFix(final String art, final String num){
		String[] titles = new String[]{
				"Удалить всю заявку №" + num
				, "Удалить арт." + art + " из заявки №" + num
				, "Утвердить всю заявку №" + num
				, "Отказать всей заявке №" + num
				, "Комментировать заявку №" + num
				, "Утвердить всё найденное"
				, "Утвердить всё с таким же контрагентом"
				, "Указать согласованную цену"
				, "Указать запрошенную цену"
		};
		final Numeric nn = new Numeric();
		Auxiliary.pickSingleChoice(this, titles, nn, null, new Task(){
			public void doTask(){
				System.out.println("selected " + nn.value());
				doHOOKApproveFixAction(nn.value().intValue(), art, num);
			}
		}, null, null, null, null);
	}

	void promptFixPriceTxt(final String num, final String art){
		final Note comment = new Note();
		Auxiliary.pickText(ActivityWebServicesReports.this//
				, "Заявка " + num + ", арт. " + art //
				, comment//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						sendFixPriceAnswer(num, art, comment.value());
					}
				});
	}

	void promptFixPriceNumber2(final String num, final String art){
		final Numeric price = new Numeric();
		Auxiliary.pickNumber(ActivityWebServicesReports.this//
				, "Заявка " + num + ", арт. " + art //
				, price//
				, "Отправить запрошенную цену", new Task(){
					@Override
					public void doTask(){
						sendNewFixPrice2(num, art, price.value());
					}
				}, null, null);
	}

	void promptFixPriceNumber(final String num, final String art){
		final Numeric price = new Numeric();
		Auxiliary.pickNumber(ActivityWebServicesReports.this//
				, "Заявка " + num + ", арт. " + art //
				, price//
				, "Отправить согласованную цену", new Task(){
					@Override
					public void doTask(){
						sendNewFixPrice(num, art, price.value());
					}
				}, null, null);
	}

	void sendNewFixPrice(final String num, final String art, final double price){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String url = //"http://10.10.5.2/lednev_hrc/"
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkiNaFiksCeny/IzmZenuSpec/"
									+ URLEncoder.encode(num, "utf-8")//
									+ "/" + URLEncoder.encode(art, "utf-8")//
									+ "/" + URLEncoder.encode("" + price, "utf-8")//
									+ "/" + Cfg.whoCheckListOwner().trim()//
							;
					Report_Base.startPing();
					Bough result = new Bough();
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					b.child("result").value.is(new String(bytes, "UTF-8"));
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendNewFixPrice2(final String num, final String art, final double price){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//https://service.swlife.ru/hrc120107/hs/ZayavkiNaFiksCeny/IzmZenuTP/номерДокумента/арт/цена/ТП
					//https://service.swlife.ru/hrc120107/hs/ZayavkiNaFiksCeny/IzmZenuTP/0001028077/112261/1234/hrc22
					String url = //"http://10.10.5.2/lednev_hrc/"
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkiNaFiksCeny/IzmZenuTP/"
									+ URLEncoder.encode(num, "utf-8")//
									+ "/" + URLEncoder.encode(art, "utf-8")//
									+ "/" + URLEncoder.encode("" + price, "utf-8")//
									+ "/" + Cfg.whoCheckListOwner().trim()//
							;
					Report_Base.startPing();
					Bough result = new Bough();
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					b.child("result").value.is(new String(bytes, "UTF-8"));
					preReport.writeCurrentPage();
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		});
		expect.start(ActivityWebServicesReports.this);
	}

	void allFixApprove(){
		Auxiliary.pickConfirm(this, "Утвердить все найденные документы?", "Утвердить", new Task(){
			@Override
			public void doTask(){
				sendAllFixDocsApprove();
			}
		});
	}

	void sendAllFixDocsApprove(){
		System.out.println("sendAllFixDocsApprove");
		String page = Cfg.pathToHTML(ActivityWebServicesReports.this.preReport.getFolderKey(), ActivityWebServicesReports.this.preKey);

		final Vector<String> strings = Auxiliary.readTextFromFile(new File(page));
		System.out.println(strings.size() + ": " + page);
		final Bough b = new Bough();
		final Note statustext = new Note().value("Подождите");
		Expect expect = new Expect().status.is(statustext)//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{
							System.out.println("sendAllFixDocsApprove start");
							String result = "";
							//String regexp = "(№(\\S+)\\s*,\\s*+Арт(\\S+\\d+))";
							String regexp = "(>(\\S+)\\s*,\\s*+Арт\\s+(\\d+))";
							Pattern pattern = Pattern.compile(regexp);
							for(int i = 0; i < strings.size(); i++){
								//System.out.println(""+i+": "+strings.get(i));
								Matcher matcher = pattern.matcher(strings.get(i));
								if(matcher.find()){
									System.out.println("sendAllFixDocsApprove found " + strings.get(i));
									String fixDocNum = matcher.group(2);
									String fixArtikul = matcher.group(3);
									String msg = "№" + fixDocNum + ", артикул " + fixArtikul;
									statustext.value(msg);
									String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/fixprice/"
											+ "?Ndok=" + URLEncoder.encode(fixDocNum.trim(), "utf-8") //
											+ "&Art=" + URLEncoder.encode(fixArtikul.trim(), "utf-8") //
											+ "&hrc=" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
											+ "&ukaz=" + URLEncoder.encode("1", "utf-8");
									System.out.println(url);
									byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
									result = result + "\n" + msg + ": " + new String(bytes);

								}
							}
							b.child("result").value.is(result);
							System.out.println("sendAllFixDocsApprove result" + result);
							preReport.writeCurrentPage();
						}catch(Throwable t){
							t.printStackTrace();
							b.child("result").value.is(t.toString());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn("Результат: " + b.child("result").value.property.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
		expect.start(ActivityWebServicesReports.this);
	}

	void sendFixApprove(final String art, final String num, final String approve, final String txt){
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = //"http://89.109.7.162/lednev_hrc/hs/fixprice/"//
									Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/fixprice/" + "?Ndok=" + URLEncoder.encode(num.trim(), "utf-8") //
											+ "&Art=" + URLEncoder.encode(art.trim(), "utf-8") //
											+ "&hrc=" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8")//
											+ "&ukaz=" + URLEncoder.encode(approve.trim(), "utf-8");
							//url="https://service.swlife.ru/hrc120107/hs/fixprice/?Ndok=0000545161&Art=114206&hrc=kazan&ukaz=1";
							Report_Base.startPing();
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							b.child("result").value.is(new String(bytes));
							preReport.writeCurrentPage();
						}catch(Throwable t){
							t.printStackTrace();
							b.child("result").value.is(t.toString());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(b.child("result").value.property.value(), ActivityWebServicesReports.this);
						tapInstance2(preReport.getFolderKey(), preKey);
					}
				});
		expect.start(ActivityWebServicesReports.this);
	}

	void doHOOKTekushieLimityTP(final int nn, String name){
		Numeric lim = new Numeric().value(22);
		int margin = 4;
		final Numeric summa = new Numeric();
		final Numeric kolvo = new Numeric();
		final Numeric plan = new Numeric();
		final Note comment = new Note();
		Auxiliary.pick(ActivityWebServicesReports.this, name//
				, new SubLayoutless(ActivityWebServicesReports.this)//
						.child(new Decor(ActivityWebServicesReports.this).labelText.is("Сумма").labelAlignRightCenter().labelStyleMediumNormal()//
								.left().is(0).top().is(margin)//
								.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
						)//
						.child(new RedactNumber(ActivityWebServicesReports.this).number.is(summa)//
								.left().is(Auxiliary.tapSize * 3 + margin).top().is(margin)//
								.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
						)//
						.child(new Decor(ActivityWebServicesReports.this).labelText.is("Кол-во дней").labelAlignRightCenter().labelStyleMediumNormal()//
								.left().is(0).top().is(Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
						)//
						.child(new RedactNumber(ActivityWebServicesReports.this).number.is(kolvo)//
								.left().is(Auxiliary.tapSize * 3 + margin).top().is(Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
						)//
						.child(new Decor(ActivityWebServicesReports.this).labelText.is("План").labelAlignRightCenter().labelStyleMediumNormal()//
								.left().is(0).top().is(2 * Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
						)//
						.child(new RedactNumber(ActivityWebServicesReports.this).number.is(plan)//
								.left().is(Auxiliary.tapSize * 3 + margin).top().is(2 * Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
						)//
						.child(new Decor(ActivityWebServicesReports.this).labelText.is("Комментарий").labelAlignRightCenter().labelStyleMediumNormal()//
								.left().is(0).top().is(3 * Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
						)//
						.child(new RedactText(ActivityWebServicesReports.this).text.is(comment)//
								.left().is(Auxiliary.tapSize * 3 + margin).top().is(3 * Auxiliary.tapSize + margin)//
								.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
						)//
						.width().is(Auxiliary.tapSize * 8 + margin * 2).height().is(Auxiliary.tapSize * 4)//
				, null, null, null, null, "Отправить", new Task(){
					@Override
					public void doTask(){
						sendLimitChange("" + nn, summa.value(), kolvo.value(), plan.value(), comment.value());
					}
				});
	}

	void sendLimitChange(final String num, final double summa, final double kolvo, final double plan, final String comment){
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task(){
			@Override
			public void doTask(){
				r.url.is(Settings.getInstance().getBaseURL() + "WebPostlimit.1cws")//
						.xml.is("<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
								+ "\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
								+ "\n	<soap:Body>"//
								+ "\n		<priv xmlns=\"http://ws.swl/WebPostlimit\">"//
								+ "\n			<Dom>" + num + "</Dom>"//
								+ "\n			<Summa>" + summa + "</Summa>"//
								+ "\n			<Dney>" + kolvo + "</Dney>"//
								+ "\n			<otv>" + Cfg.whoCheckListOwner() + "</otv>"//
								+ "\n			<planto>" + plan + "</planto>"//
								+ "\n			<Kommentariy>" + comment + "</Kommentariy>"//
								+ "\n		</priv>"//
								+ "\n	</soap:Body>"//
								+ "\n</soap:Envelope>"//
						);
				Report_Base.startPing();
				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				preReport.writeCurrentPage();
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				if(r.exception.property.value() != null){
					Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), ActivityWebServicesReports.this);
				}else{
					if(r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300){
						Auxiliary.warn("Результат: " //
								+ r.data.child("soap:Body")//
								.child("m:privResponse")//
								.child("m:return")//
								.value.property.value(), ActivityWebServicesReports.this);
					}else{
						Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityWebServicesReports.this);
					}
				}
				tapInstance2(preReport.getFolderKey(), preKey);
			}
		}).afterCancel.is(new Task(){
			@Override
			public void doTask(){
				//
			}
		}).start(this);
	}

	void doHookExternalLink(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(intent);
	}

	public Task interceptTask = new Task(){
		@Override
		public void doTask(){
			try{
				//String brwsrurl=
				//final android.net.Uri uri = android.net.Uri.parse(brwsr.url.property.value());
				//System.out.println(uri);

				brwsr.browserScrollY = brwsr.getScrollY();
				System.out.println("browserScrollY " + brwsr.browserScrollY);
				System.out.println("url " + brwsr.url.property.value());

				if(brwsr.url.property.value().contains("files.swlife.ru")){
					doHookExternalLink(brwsr.url.property.value());
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKTekushieLimityTP)){
					//int nn = Integer.parseInt(uri.getQueryParameter(Report_Base.FIELDGruppadogovorov));
					int nn = Integer.parseInt(brwsr.getQueryParameter(Report_Base.FIELDGruppadogovorov));
					String name = brwsr.getQueryParameter(Report_Base.FIELDKontragent).trim();
					doHOOKTekushieLimityTP(nn, name);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKApproveFix)){
					String art = brwsr.getQueryParameter(Report_Base.FIELDFixArt);
					String num = brwsr.getQueryParameter(Report_Base.FIELDFixNum);
					//String row = brwsr.getQueryParameter(Report_Base.FIELDFixRow);
					doHOOKApproveFix(art, num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportReturnState)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					String dat = brwsr.getQueryParameter(Report_Base.FIELDDocumentDate);
					doHOOKReturn(num, dat);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportVzaimoraschety)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKVzaimporascheti(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportStatusSKD)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKSKD(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOkReportPerebitieNakladnoy)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKPerebitieNakladnoy(num);
				}

				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportReturnAnswer)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					String art = brwsr.getQueryParameter(Report_Base.FIELDArtikul);
					doHOOKAnswer(num, art);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportDegustacia)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKDegustacia(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportFixKoordinat)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKFixirovanieKoordinat(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportDeleteSpec)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					String art = brwsr.getQueryParameter(Report_Base.FIELDArtikul);
					promptReportDeleteSpec(num, art);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKArtFavSmartPro)){
					String kodKlienta = brwsr.getQueryParameter(Report_Base.FIELDKlientKod);
					promptReportFavSmartPro(kodKlienta);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKDeleteArtFavSmartPro)){
					String kodKlienta = brwsr.getQueryParameter(Report_Base.FIELDKlientKod);
					String art = brwsr.getQueryParameter(Report_Base.FIELDArtikul);
					promptDelFavSmartPro(art, kodKlienta);
				}

				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportApproveTerrChange)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					promptReportApproveTerrChange(num);
				}


				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportLimity)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKReportLimity(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HookZayavkiNaDobavlenieVmatricu)){
					String num = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					doHOOKHookZayavkiNaDobavlenieVmatricu(num);
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKObratnayaSviaz)){
					doHOOKObratnayaSviaz(brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber));
				}
				/*if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKArtDocDateSent)){
					doHOOKHookArtDocDateSent(brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber)
							, brwsr.getQueryParameter(Report_Base.FIELDShipDate)
							, brwsr.getQueryParameter(Report_Base.FIELDArtikul)
					);
				}*/
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportOrderState)){
					String urlDocumentDate = brwsr.getQueryParameter(Report_Base.FIELDDocumentDate);
					String urlShipDate = brwsr.getQueryParameter(Report_Base.FIELDShipDate);
					String urlKlientKod = brwsr.getQueryParameter(Report_Base.FIELDKlientKod).trim();
					//String urlKlientName = brwsr.getQueryParameter(Report_Base.FIELDKontragent).trim();
					String urlDogovorOplata = brwsr.getQueryParameter(Report_Base.FIELDDogovorOplata).trim();
					//System.out.println("FIELDKontragent " + urlKlientName);

					String documentNumber = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
					//EditOrderViaWeb editor = new EditOrderViaWeb();
					editor = new EditOrderViaWeb();
					editor.me = ActivityWebServicesReports.this;
					editor.context = ActivityWebServicesReports.this;
					editor.documentDate = urlDocumentDate;
					editor.shipDate = urlShipDate;
					//editor.currentKlientName = urlKlientName;
					editor.currentKlientKod = urlKlientKod;

					editor.currentDogovorName = urlDogovorOplata;
					editor.documentNumber = documentNumber;
					//editor.hrc = ApplicationHoreca.getInstance().hrcSelectedRoute();
					editor.hrc = Cfg.selectedOrDbHRC();
					editor.start();
				}
				if(brwsr.getQueryParameter("kind").equals(Report_Base.HOOKReportPoVoditelam)){
					new Expect().task.is(new Task(){
								@Override
								public void doTask(){
									mapData = new Bough().name.is("mapData");
									String documentNumber = brwsr.getQueryParameter(Report_Base.FIELDDocumentNumber);
									String q = "ВЫБРАТЬ первые 19"//
											+ " 		р1.Ссылка.номер,"//
											+ " 		р1.Ссылка.Автомобиль.Api_key КАК apikey,"//
											+ " 		р1.Ссылка.ДатаВыезда КАК ДатаВыезда,"//
											+ " 		р2.Контрагент.Наименование КАК кнтргнт,"//
											+ " 		р2.Ссылка.Автомобиль.ТипАвтомобиля.Наименование КАК авто,"//
											+ " 		р2.Ссылка.Автомобиль.ГосНомер КАК авном,"//
											+ " 		р2.Контрагент.ГеографическаяШирота КАК lt,"//
											+ " 		р2.Контрагент.ГеографическаяДолгота КАК ln"//
											+ " 	ИЗ"//
											+ " 		Документ.Авто_ПутевойЛист.Реализации КАК р1"//
											+ " 	внутреннее соединение"//
											+ " 		Документ.Авто_ПутевойЛист.Реализации р2 по р1.ссылка=р2.ссылка"//
											+ " 	ГДЕ"//
											+ " 		р1.Реализация.Номер = \"" + documentNumber + "\""//
											+ " 		И р1.Ссылка.ДатаВыезда > ДАТАВРЕМЯ(" + (1900 + new Date().getYear()) + ", " + (1 + new Date().getMonth()) + ", " + new Date().getDate() + ", 0, 0, 0)"//
											+ " 	СГРУППИРОВАТЬ ПО"//
											+ " 		р1.Ссылка.номер,"//
											+ " 		р1.Ссылка.Автомобиль.Api_key,"//
											+ " 		р1.Ссылка.ДатаВыезда,"//
											+ " 		р2.Контрагент.Наименование,"//
											+ " 		р2.Ссылка.Автомобиль.ТипАвтомобиля.Наименование,"//
											+ " 		р2.Ссылка.Автомобиль.ГосНомер,"//
											+ " 		р2.Контрагент.ГеографическаяШирота,"//
											+ " 		р2.Контрагент.ГеографическаяДолгота";
									Bough b = Auxiliary.loadTextFromPrivatePOST(Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/surikovquery/"
											, q, 30 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
									Bough raw = Bough.parseJSON(b.child("raw").value.property.value());
									String carKey = raw.child("результат").child("apikey").value.property.value();
									long ms = new Date().getTime();
									String carOnlineURL = "http://api.car-online.ru/v2?get=gpslist&skey=" //
											+ carKey//
											+ "&begin=" + (ms - 10 * 60 * 1000)//
											+ "&end=" + ms;
									Vector<Bough> rr = raw.children("результат");
									Bough autoData = null;
									for(int i = 0; i < rr.size(); i++){
										autoData = new Bough().name.is("client");
										autoData.child("name").value.is(rr.get(i).child("кнтргнт").value.property.value());
										autoData.child("latitude").value.is(rr.get(i).child("lt").value.property.value());
										autoData.child("longitude").value.is(rr.get(i).child("ln").value.property.value());
										mapData.children.add(autoData);
									}
									try{
										byte[] bytes = Auxiliary.loadFileFromPublicURL(carOnlineURL);
										String xml = new String(bytes, "UTF-8");
										Bough data = Bough.parseXML(xml);
										autoData = new Bough().name.is("car");
										autoData.child("nomer").value.is(raw.child("результат").child("авном").value.property.value() + "/" + raw.child("результат").child("авто").value.property.value());
										autoData.child("latitude").value.is(data.child("gps").child("latitude").value.property.value());
										autoData.child("longitude").value.is(data.child("gps").child("longitude").value.property.value());
										mapData.children.add(autoData);
									}catch(Throwable t){
										t.printStackTrace();
									}
								}
							}).afterDone.is(new Task(){
								@Override
								public void doTask(){
									Intent intent = new Intent();
									intent.putExtra("route", mapData.dumpXML());
									intent.setClass(ActivityWebServicesReports.this, sweetlife.android10.supervisor.ActivityGPSMap.class);
									startActivity(intent);
								}
							})//
							.status.is("Подождите.....")//
							.start(ActivityWebServicesReports.this);
				}
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
	};

	void createGUI(){
		int winW = Auxiliary.screenWidth(this);
		int winH = Auxiliary.screenWidth(this);
		reportGrid = new DataGrid2(this).pageSize.is(200);
		reportName = new ColumnDescription();
		reportIcon = new ColumnBitmap();
		brwsr = new WebRender(this).afterLink.is(interceptTask);
		layoutless.child(brwsr//
				.width().is(winW)//
				.height().is(winH)//
		);
		propertiesForm = new SubLayoutless(this);
		layoutless.child(new Decor(this)//
				.background.is(0xffffffff)//
				.left().is(reportSplit)//
				.width().is(winW)//
				.height().is(winH)//
		);
		layoutless.child(new Decor(this)//
				.background.is(0xffffffff)//
				.left().is(propSplit)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		layoutless.child(new SplitLeftRight(this)//properies
				.split.is(propSplit)//
				.rightSide(propertiesForm)
				.height().is(layoutless.height().property)//
				.width().is(layoutless.width().property)//
		);
		layoutless.child(new Decor(this)//
				.background.is(0xffffffff)//
				.left().is(reportSplit)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
/*
		reportGrid
				.noHead.is(true)
				.columns(new Column[]{//
								reportIcon.width.is(Auxiliary.tapSize).noHorizontalBorder.is(true).noVerticalBorder.is(true)//
								, reportName.width.is(layoutless.width().property).noHorizontalBorder.is(true).noVerticalBorder.is(true) //
						}
				)
		.width().is(5*Auxiliary.tapSize)
		.height().is(10*Auxiliary.tapSize)
		;
*/
		layoutless.child(new SplitLeftRight(this)//reports
				.position.is(1).split.is(reportSplit)//
				.rightSide(
						new SubLayoutless(this)
								.child(new RedactText(this)
										.text.is(seekReport.afterChange(new Task(){
											@Override
											public void doTask(){
												System.out.println("seekReport " + seekReport.value());
												resetMenu2();
											}
										}, true))
										.width().is(layoutless.width().property)
										.height().is(1 * Auxiliary.tapSize)
								)
								.child(reportGrid
										.noHead.is(true)
										.columns(new Column[]{//
														reportIcon.width.is(Auxiliary.tapSize).noHorizontalBorder.is(true).noVerticalBorder.is(true)//
														, reportName.width.is(layoutless.width().property).noHorizontalBorder.is(true).noVerticalBorder.is(true) //
												}
										)
										.top().is(1 * Auxiliary.tapSize)
										.width().is(layoutless.width().property)
										.height().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))
								)
				)//
				.height().is(layoutless.height().property)//
				.width().is(layoutless.width().property)//
		);
	}

	void doMenuClrear(){
		Auxiliary.pickConfirm(this, "Удалить все отчёты", "Удалить", new Task(){
			@Override
			public void doTask(){
				String path = Cfg.workFolder + "supervisor/reports/";
				deleteRecursive(new File(path));
				resetMenu2();
			}
		});
	}

	void deleteRecursive(File fileOrDirectory){
		if(fileOrDirectory.isDirectory()){
			for(File child: fileOrDirectory.listFiles()){
				deleteRecursive(child);
			}
		}
		fileOrDirectory.delete();
	}

	void doMenuSendHTML(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		final String path = Cfg.pathToHTML(preReport.getFolderKey(), preKey);
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Отчёт");
		//Uri uri = Uri.fromFile(new File(path));
		//intent.putExtra(Intent.EXTRA_STREAM, uri);
		//startActivity(intent);
		MediaScannerConnection.scanFile(ActivityWebServicesReports.this, new String[]{path}, null
				, new MediaScannerConnection.OnScanCompletedListener(){
					public void onScanCompleted(String path, Uri uri){
						intent.putExtra(Intent.EXTRA_STREAM, uri);
						startActivity(intent);
					}
				});
	}

	void doMenuSendXLS(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		if(!preReport.canUseXLS()){
			Auxiliary.warn("Сертификаты можно получить только в .pdf или .html", this);
			return;
		}
		final String seed = "" + Math.round(Math.random() * 10000);
		//final String name = preReport.getMenuLabel();
		//final String name = preReport.getFileName();
		final String name = Auxiliary.safeFileName(preReport.getFileName() + Math.round(Math.random() * 10000));
		final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name + ".xls";
		Expect expectRequery = new Expect()//
				.status.is("Подождите.....")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						System.out.println("doMenuSendXLS exportXLS " + toPath);
						preReport.exportXLS(toPath, ActivityWebServicesReports.this);
					}
				})//
				.afterDone.is(new Task(){
					@Override
					public void doTask(){
						//Auxiliary.warn("Файл [" + name + seed + ".xls" + "] сохранён в папку Download", ActivityWebServicesReports.this);
						final Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_SUBJECT, "Отчёт");


/*
						System.out.println("doMenuSendXLS toPath " + toPath);///storage/emulated/0/Download/exportСтатусызаказов8510.xls
						System.out.println("doMenuSendXLS file " + new File(toPath).getAbsolutePath());
						System.out.println("doMenuSendXLS fromFile " + Uri.fromFile(new File(toPath)));
						Auxiliary.file2uri(ActivityWebServicesReports.this,new File(toPath));

						Uri uri = Uri.fromFile(new File(toPath));
						*/
						MediaScannerConnection.scanFile(ActivityWebServicesReports.this, new String[]{toPath}, null
								, new MediaScannerConnection.OnScanCompletedListener(){
									public void onScanCompleted(String path, Uri uri){
										intent.putExtra(Intent.EXTRA_STREAM, uri);
										startActivity(intent);
									}
								});

						//intent.putExtra(Intent.EXTRA_STREAM, uri);
						//startActivity(intent);
					}
				})//
				;
		expectRequery.start(this);
	}

	void doMenuSendPDF(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		//final String seed = "" + Math.round(Math.random() * 10000);
		//final String name = preReport.getMenuLabel();
		//final String name = preReport.getFileName();
		final String name = Auxiliary.safeFileName(preReport.getFileName() + Math.round(Math.random() * 10000));
		final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name + ".pdf";
		System.out.println("doMenuSendPDF " + toPath);
		Expect expectRequery = new Expect()//
				.status.is("Подождите.....")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						System.out.println("doMenuSendXLS exportPDF " + toPath);
						preReport.exportPDF(toPath, ActivityWebServicesReports.this);
					}
				})//
				.afterDone.is(new Task(){
					@Override
					public void doTask(){
						//Auxiliary.warn("Файл [" + name + seed + ".xls" + "] сохранён в папку Download", ActivityWebServicesReports.this);
						final Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_SUBJECT, "Отчёт");
						//Uri uri = Uri.fromFile(new File(toPath));
						MediaScannerConnection.scanFile(ActivityWebServicesReports.this, new String[]{toPath}, null
								, new MediaScannerConnection.OnScanCompletedListener(){
									public void onScanCompleted(String path, Uri uri){
										intent.putExtra(Intent.EXTRA_STREAM, uri);
										startActivity(intent);
									}
								});
						//intent.putExtra(Intent.EXTRA_STREAM, uri);
						//startActivity(intent);
					}
				})//
				;
		expectRequery.start(this);
	}

	void doMenuExport2(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		//String name = preReport.getMenuLabel();
		final String name = preReport.getFileName();
		String fromPath = Cfg.pathToHTML(preReport.getFolderKey(), preKey);
		String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name + preKey + ".html";
		File source = new File(fromPath);
		File destination = new File(toPath);
		try{
			FileChannel src = new FileInputStream(source).getChannel();
			FileChannel dst = new FileOutputStream(destination).getChannel();
			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();
			Auxiliary.warn("Файл [" + name + "] сохранён в папку Download", this);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	void doMenuExportXLS(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		if(!preReport.canUseXLS()){
			Auxiliary.warn("Сертификаты можно получить только в .pdf или .html", this);
			return;
		}
		//final String seed = "" + Math.round(Math.random() * 10000);
		//final String name = preReport.getMenuLabel();
		//final String name = preReport.getFileName();
		final String name = Auxiliary.safeFileName(preReport.getFileName() + Math.round(Math.random() * 10000));
		final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name + ".xls";
		Expect expectRequery = new Expect()//
				.status.is("Подождите.....")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						preReport.exportXLS(toPath, ActivityWebServicesReports.this);
					}
				})//
				.afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn("Файл [" + name + ".xls" + "] сохранён в папку Download", ActivityWebServicesReports.this);
					}
				})//
				;
		expectRequery.start(this);
	}


	void doMenuExportPDF(){
		if(preReport == null){
			return;
		}
		if(preKey.length() < 1){
			return;
		}
		//final String seed = "" + Math.round(Math.random() * 10000);
		//final String name = preReport.getMenuLabel();
		final String name = Auxiliary.safeFileName(preReport.getFileName() + Math.round(Math.random() * 10000));
		final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + name + ".pdf";
		Expect expectRequery = new Expect()//
				.status.is("Подождите.....")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						preReport.exportPDF(toPath, ActivityWebServicesReports.this);
					}
				})//
				.afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn("Файл [" + name + ".pdf" + "] сохранён в папку Download", ActivityWebServicesReports.this);
					}
				})//
				;
		expectRequery.start(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuSendHTML = menu.add("Переслать по почте в .html");
		menuSendXLS = menu.add("Переслать по почте в .xls");
		menuSendPDF = menu.add("Переслать по почте в .pdf");

		menuExportPDF = menu.add("Сохранить в файл .pdf");
		menuExportXLS = menu.add("Сохранить в файл .xls");
		menuClear = menu.add("Удалить все отчёты");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuSendHTML){
			doMenuSendHTML();
		}
		if(item == menuSendXLS){
			doMenuSendXLS();
		}
		if(item == menuSendPDF){
			doMenuSendPDF();
		}
		if(item == menuExportXLS){
			doMenuExportXLS();
		}
		if(item == menuExportPDF){
			doMenuExportPDF();
		}
		if(item == menuClear){
			doMenuClrear();
		}
		return false;
	}

	void showSKDFileChooser(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try{
			Intent chooser = Intent.createChooser(intent, "Выбрать файл");
			startActivityForResult(chooser, FILE_SELECT_SKD_RESULT);
		}catch(Throwable ex){
			ex.printStackTrace();
		}
	}

	void createSchetNaoplatu(String num){
		String folderKey = ReportPechatSchetaNaOplatu.folderKey();
		ReportPechatSchetaNaOplatu.tempInitKind = 1;
		EditOrderViaWeb.documentNumber = num;
		tapReport2(folderKey);
	}

	void createPechatUPD(String num){
		String folderKey = ReportTovarnieNakladmie.folderKey();
		ReportTovarnieNakladmie.tempInitKind = 2;
		ReportTovarnieNakladmie.tempInitNum = num;
		tapReport2(folderKey);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		System.out.println("requestCode " + requestCode + ", resultCode " + resultCode);
		String filePath = null;
		Uri uri = null;
		if(intent != null){
			System.out.println("intent.getData() " + intent.getData());
			switch(requestCode){
				case FILE_SELECT_SKD_RESULT:{
					if(resultCode == RESULT_OK){
						uri = intent.getData();
						String path = Auxiliary.pathForMediaURI(this, uri);
						if(path != null && path.length() > 5){
							sendSKDFile(path);
						}else{
							Auxiliary.warn("Выберите файл из памяти устройства. Невозможно присоединить " + uri, this);
						}
					}
					break;
				}
				case FILE_PEREBIT_SKD_RESULT:{
					if(resultCode == RESULT_OK){
						uri = intent.getData();
						String path = Auxiliary.pathForMediaURI(this, uri);
						if(path != null && path.length() > 5){
							sendPerebitFile(path);
						}else{
							Auxiliary.warn("Выберите файл из памяти устройства. Невозможно присоединить " + uri, this);
						}
					}
					break;
				}
				/*
				case etiketkaIDPicture:
					uri = intent.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать " + uri, ActivityWebServicesReports.this);
						return;
					}
					etiketkaObratnayaSvyazKlient.value(filePath);
					break;
				case tovarIDPicture:
					uri = intent.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать " + uri, ActivityWebServicesReports.this);
						return;
					}
					tovarObratnayaSvyazKlient.value(filePath);
					break;
					*/
			/*case NOMENKLATURA_NEW: {
				if (resultCode == RESULT_OK) {
					//String art = intent.getStringExtra(ITableColumnsNames.ARTIKUL);
					//System.out.println("new item "+art);
					SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
					ClientInfo client = ApplicationHoreca.getInstance().getClientInfo();
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, 1);
					try {
						java.util.Date date = Auxiliary.rusDate.parse(editor.documentDate);
						calendar.setTime(date);
					} catch (Throwable t) {
						t.printStackTrace();
					}
					sweetlife.android10.data.orders.ZayavkaPokupatelya zayavkaPokupatelya = new sweetlife.android10.data.orders.ZayavkaPokupatelya(mDB, client, calendar);
					final FoodstuffsData foodstuffData = new FoodstuffsData(mDB, zayavkaPokupatelya);
					foodstuffData.newFoodstuff(//
							intent.getStringExtra(ITableColumnsNames.NOMENCLATURE_ID)//
							, intent.getStringExtra(ITableColumnsNames.ARTIKUL)//
							, intent.getStringExtra(ITableColumnsNames.NAIMENOVANIE)//
							, intent.getStringExtra(ITableColumnsNames.EDINICY_IZMERENIYA_ID)//
							, intent.getStringExtra(ITableColumnsNames.EDINICY_IZMERENIYA_NAIMENOVANIE)//
							, intent.getDoubleExtra(ITableColumnsNames.COUNT, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.CENA, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.CENA_SO_SKIDKOY, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.MIN_CENA, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.MAX_CENA, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.SKIDKA, 0.00D)//
							, intent.getStringExtra(ITableColumnsNames.VID_SKIDKI)//
							, intent.getDoubleExtra(ITableColumnsNames.MIN_NORMA, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.KOEPHICIENT, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.BASE_PRICE, 0.00D)//
							, intent.getDoubleExtra(ITableColumnsNames.LAST_PRICE, 0.00D)//
					);
					View anchor = this.brwsr.getRootView();
					sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener onClose = new sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener() {
						public void onClose(int param) {
							System.out.println("onClose " + param
									+ ", " + foodstuffData.getFoodstuff(0).getKolichestvo()
									+ ", " + foodstuffData.getFoodstuff(0).getCenaSoSkidkoy()
									+ ", " + foodstuffData.getFoodstuff(0).getVidSkidki()
							);
							sendNewOrderItem(editor.documentNumber
									, "" + foodstuffData.getFoodstuff(0).getArtikul()
									, "" + foodstuffData.getFoodstuff(0).getKolichestvo()
									, "" + foodstuffData.getFoodstuff(0).getCenaSoSkidkoy()
									, foodstuffData.getFoodstuff(0).getVidSkidki().toLowerCase().equals("цр") ? "1" : "0"
							);
						}
					};
					Popup_EditNomenclatureCountPrice popup = new Popup_EditNomenclatureCountPrice(anchor, onClose, foodstuffData.getFoodstuff(0));
					popup.showLikeQuickAction();
				}
				break;
			}*/
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/*
		void sendNewOrderItem(String orderNum, String artikul, String count, String price, String cr) {
			//String url="https://service.swlife.ru/hrc120107/hs/ZakaziPokupatelya/DobavitTovarVZakaz/12-1609303/97974/2/123/0/hrc297"

			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
					+ "/hs/ZakaziPokupatelya/DobavitTovarVZakaz"
					+ "/" + orderNum
					+ "/" + artikul
					+ "/" + count
					+ "/" + price
					+ "/" + cr
					+ "/" + Cfg.whoCheckListOwner();
			System.out.println("sendNewOrderItem url " + url);
			final Bough result = new Bough();
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					try {
						byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						String msg = new String(bytes, "UTF-8");
						result.child("result").value.is(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					Auxiliary.warn("Результат: " + result.child("result").value.property.value(), ActivityWebServicesReports.this);
					ActivityWebServicesReports.this.tapInstanceReal(
							ActivityWebServicesReports.this.preReport.getFolderKey()
							, ActivityWebServicesReports.this.preKey
							, false
					);
					//System.out.println("ActivityWebServicesReports.this.preReport.getFolderKey() " + ActivityWebServicesReports.this.preReport.getFolderKey());
					//System.out.println("ActivityWebServicesReports.this.preKey " + ActivityWebServicesReports.this.preKey);
				}
			}).status.is("Отправка файла").start(this);
		}
	*/
	void sendPerebitFile(final String filePath){
		System.out.println("sendPerebitFile " + filePath);
		String[] spl = filePath.split("\\.");
		String rash = spl[spl.length - 1];
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
				+ "/hs/PerebitieNakladnoy"
				+ "/" + "ZagruzFile"
				+ "/" + this.selectedDocNum
				+ "/" + Cfg.whoCheckListOwner()
				+ "/a"
				+ "/b"
				+ "/?rash=" + rash;
		System.out.println("url " + url);
		final Bough result = new Bough();
		try{
			File iofile = new File(filePath);
			int length = (int)iofile.length();
			final byte[] bytes = new byte[length];
			FileInputStream fileInputStream = new FileInputStream(iofile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			dataInputStream.readFully(bytes);
			dataInputStream.close();
			System.out.println(bytes.length);
			final Bough raw = new Bough();
			new Expect().task.is(new Task(){
				@Override
				public void doTask(){
					try{
						System.out.println(url);
						//System.out.println(Cfg.hrcPersonalLogin);
						//System.out.println(Cfg.hrcPersonalPassword);
						Bough b = Auxiliary.loadTextFromPrivatePOST(url, bytes, 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						System.out.println(b.dumpXML());
						result.child("raw").value.is(b.child("raw").value.property.value());
						result.child("message").value.is(b.child("message").value.property.value());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}).afterDone.is(new Task(){
				@Override
				public void doTask(){
					if(result.child("raw").value.property.value().equals("Отправлено")){
						ActivityWebServicesReports.this.tapInstance2(ActivityWebServicesReports.this.preReport.getFolderKey(), ActivityWebServicesReports.this.preKey);
					}
					Auxiliary.warn(result.child("message").value.property.value() + ": " + result.child("raw").value.property.value()
							, ActivityWebServicesReports.this);
				}
			}).status.is("Отправка файла").start(this);
		}catch(Throwable t){
			Auxiliary.warn(t.getMessage(), this);
		}
	}

	void sendSKDFile(final String filePath){
		System.out.println(filePath);
		String[] spl = filePath.split("\\.");
		String rash = spl[spl.length - 1];
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
				+ "/hs/FileRasporazenie/" + this.selectedDocNum + "/" + Cfg.whoCheckListOwner() + "/?rash=" + rash;
		final Bough result = new Bough();
		try{
			File iofile = new File(filePath);
			int length = (int)iofile.length();
			final byte[] bytes = new byte[length];
			FileInputStream fileInputStream = new FileInputStream(iofile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			dataInputStream.readFully(bytes);
			dataInputStream.close();
			System.out.println(bytes.length);
			final Bough raw = new Bough();
			new Expect().task.is(new Task(){
				@Override
				public void doTask(){
					try{
						System.out.println(url);
						//System.out.println(Cfg.hrcPersonalLogin);
						//System.out.println(Cfg.hrcPersonalPassword);
						Bough b = Auxiliary.loadTextFromPrivatePOST(url, bytes, 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						System.out.println(b.dumpXML());
						result.child("raw").value.is(b.child("raw").value.property.value());
						result.child("message").value.is(b.child("message").value.property.value());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}).afterDone.is(new Task(){
				@Override
				public void doTask(){
					if(result.child("raw").value.property.value().equals("Отправлено")){
						ActivityWebServicesReports.this.tapInstance2(ActivityWebServicesReports.this.preReport.getFolderKey(), ActivityWebServicesReports.this.preKey);
					}
					Auxiliary.warn(result.child("message").value.property.value() + ": " + result.child("raw").value.property.value()
							, ActivityWebServicesReports.this);
				}
			}).status.is("Отправка файла").start(this);
		}catch(Throwable t){
			Auxiliary.warn(t.getMessage(), this);
		}
	}
}

class KontragentInfo{
	public String _idrref = "";
	public String kod = "";
	public String naimenovanie = "";
	public Vector<String> groups = new Vector<String>();
}

class OrderItemInfo{
	public String _idrref = "";
	public String artikul = "";
	public String naimenovanie = "";
	public double cena = 0;
	public String poslednyaya = "";
	public String edizm = "";

	public String minNorma = "";
	public String koephphicient = "";

	public double min = 0;
	public double max = 0;
	public double kolichestvo = 0;
}
