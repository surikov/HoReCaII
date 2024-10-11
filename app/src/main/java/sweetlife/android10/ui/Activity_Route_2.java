package sweetlife.android10.ui;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.view.*;

import sweetlife.android10.data.common.*;
import sweetlife.android10.database.*;
import sweetlife.android10.log.*;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.supervisor.ActivityVseAnketi;
import sweetlife.android10.supervisor.ActivityZayavkaVozmehenie;
import sweetlife.android10.update.*;
import sweetlife.android10.utils.*;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.SystemHelper;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
import reactive.ui.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.TimeZone;
import java.util.Vector;

import sweetlife.android10.gps.*;
import sweetlife.android10.*;

public class Activity_Route_2 extends Activity{

	Note popUpURL = new Note();

	public static Toggle tolkoZaDatu = new Toggle().value(false);
	Layoutless layoutless;
	DataGrid2 dataGrid;
	ColumnText columnKod;
	ColumnDescription columnClient;
	ColumnText columnPn;
	ColumnText columnVt;
	ColumnText columnSr;
	ColumnText columnCh;
	ColumnText columnPt;
	ColumnText columnSb;
	ColumnText columnMenu;

	Note clientNameFilterText = new Note();
	//String lastFilter = "";

	WebRender popup;

	Toggle hidePopUp = new Toggle().value(true);
	//public static int lastGridY=0;
	//public static int lastGridOffset=0;
	//ColumnText columnToDen;
	//ColumnText columnToNar;
	//ColumnText columnNacnk;
	int gridPageSize = 99;
	Toggle hideGPSwarning = new Toggle();
	Note textGPSwarning = new Note().value("Нет GPS координат!");
	Bough gridData = new Bough();
	public static Numeric gridOffset = new Numeric();//.value(100);
	//public static int gridScroll = 0;
	//Note limitSQL = new Note().value("\n	limit " + (gridPageSize * 3) + " offset ").append(gridOffset.asNote());
	Note limitSQL = new Note().value("\n	limit " + gridPageSize + " offset ").append(gridOffset.asNote());
	//boolean canRequery = false;
	//MenuItem menuVigrusit;
	//MenuItem menuNezakrVizit;
	//MenuItem menuDocumenti;
	//MenuItem menuGPSinformacia;
	MenuItem menuRasporyazheniaNaOtgruzku;
	MenuItem menuPerebitNakladnuyu;
	MenuItem menuZayavkaNaPostavku;
	MenuItem menuOtchety;
	MenuItem menuDataCheck;
	//MenuItem menuResetExchange;
	MenuItem menuMatricaTP;
	//MenuItem menuMap;
	//MenuItem menuLimit;
	//MenuItem menuTest;
	MenuItem menuAnketi;
	MenuItem menuHelp;
	MenuItem menuPlanObuchenia;
	MenuItem menuFinDocs;
	MenuItem menuVizitGroup;
	MenuItem menuZapiski;
	MenuItem menuPoKassamDlyaTP;
	MenuItem menuZayavkaVnutrenneePeremechenie;
	MenuItem menuCheckDocs;
	MenuItem menuZayavkaVozmehenie;
	MenuItem menuMarshrutDogovora;
	//MenuItem menuTONacenka;
	MenuItem menuDannieMercury;
	MenuItem menuChangeUser;
	MenuItem menuDobavitKlientaVMarsgrut;

	MenuItem menuFirebaseMesasages;
	MenuItem menuZayavkaNaPerevodVdosudebnye;
	MenuItem menuPeredatIsprNakl;

	MenuItem menuKontaktnayaInformacia;
	MenuItem menuIskluchenieVizitov;


	Note currentHRCName = new Note().value("[все]");
	Numeric zaDatu = new Numeric().value(0);
	Numeric otgruzkaNaDatu = new Numeric();
	Expect requery22222 = new Expect().status.is("Подождите...").task.is(new Task(){
		@Override
		public void doTask(){
			//System.out.println("requery.doTask");
			//if(canRequery) {
			requeryGridData();
			//}
		}
	}).afterDone.is(new Task(){
		@Override
		public void doTask(){
			//if(canRequery) {
			refreshGUI();
			//layoutless.requestLayout();
			//}
		}
	});

	public void resetGrid(){
		//System.out.println("resetGrid");
		gridOffset.value(0);
		//lastGridY=0;
		//reShowGrid();
		//requery.start(this);
		requery22222.start(this);
		refreshGridDayTitles();
	}

	void setupOtguzka(){
		Calendar today = Calendar.getInstance();
		today.setTime(NomenclatureBasedDocument.nextWorkingDate(today));
		ApplicationHoreca.getInstance().setShippingDate(today);
		Calendar c = ApplicationHoreca.getInstance().getShippingDate();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		long t = c.getTime().getTime();
		otgruzkaNaDatu.value((double)t);//((double) ApplicationHoreca.getInstance().getShippingDate().getTime().getTime()));
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//System.out.println("onCreate");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		GPS.StopAndUnbindServiceIfRequired(this);

		addUpperFields();
		addRouteGrid();
		refreshGridDayTitles();
		addBottomButtons();
		popup = new WebRender(this);
		addPopUp();

		GPS.StopAndUnbindServiceIfRequired(this);
		//canRequery=true;
		initPopUp();
	}

	void addPopUp(){
		layoutless.child(new Decor(this)

				.background.is(0x99000000)
				.hidden().is(hidePopUp)
				.left().is(0)//
				.top().is(0)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		layoutless.child(new Decor(this)

				.background.is(0xffeeeeee)
				.hidden().is(hidePopUp)
				.left().is(0.5 * Auxiliary.tapSize - 1)//
				.top().is(0.5 * Auxiliary.tapSize - 1)//
				.width().is(layoutless.width().property.minus(1 * Auxiliary.tapSize).plus(2))//
				.height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize).plus(2))//
		);

		layoutless.child(popup
				.hidden().is(hidePopUp)
				//.url.is(popUpURL)
				//.url.is("https://service.swlife.ru/hrc120107/hs/ObnovlenieInfo/КартинкаПриНачалеРаботы/000278668/20230926")
				.left().is(0.5 * Auxiliary.tapSize)//
				.top().is(0.5 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property.minus(1 * Auxiliary.tapSize))//
				.height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this)
				.afterTap.is(new Task(){
					public void doTask(){
						showPrePopUpPage();
					}
				})
				.labelText.is("←")
				.hidden().is(hidePopUp)
				.left().is(0.5 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(2.5 * Auxiliary.tapSize))
				.width().is(1 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this)
				.afterTap.is(new Task(){
					public void doTask(){
						showNextPopUpPage();
					}
				})
				.labelText.is("→")
				.hidden().is(hidePopUp)
				.left().is(layoutless.width().property.minus(1.5 * Auxiliary.tapSize))//
				.top().is(layoutless.height().property.minus(2.5 * Auxiliary.tapSize))//
				.width().is(1 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this)
				.afterTap.is(new Task(){
					public void doTask(){
						hidePopUp.value(true);
					}
				})
				.labelText.is("✕")
				.hidden().is(hidePopUp)
				.left().is(layoutless.width().property.minus(1.5 * Auxiliary.tapSize))
				.top().is(0.5 * Auxiliary.tapSize)//
				.width().is(1 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize));

		popup.afterLink.is(new Task(){
			@Override
			public void doTask(){
				try{
					final android.net.Uri uri = android.net.Uri.parse(popup.url.property.value());
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(browserIntent);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		});


	}

	void addUpperFields(){
		Calendar today = Calendar.getInstance();
		zaDatu.value(((double)today.getTimeInMillis()));
		setupOtguzka();
		otgruzkaNaDatu.afterChange(new Task(){
			public void doTask(){
				Calendar c = Calendar.getInstance();
				c.setTime(new Date(otgruzkaNaDatu.value().longValue()));
				ApplicationHoreca.getInstance().setShippingDate(c);
			}
		});
		tolkoZaDatu.afterChange(new Task(){
			public void doTask(){
				//System.out.println("change tolkoZaDatu");
				resetGrid();
			}
		}, true);
		zaDatu.afterChange(new Task(){
			public void doTask(){
				if(!tolkoZaDatu.value()){
					tolkoZaDatu.value(true);
				}else{
					//canRequery=true;
					resetGrid();
				}
			}
		}, true);
		layoutless.child(new RedactToggle(this)//
				.yes.is(tolkoZaDatu)//
				.labelText.is("Только за")//
				.left().is(0.5 * Auxiliary.tapSize)//
				.top().is(0)//
				.width().is(2 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new RedactDate(this)//
				.date.is(zaDatu)//
				.format.is("dd.MM.yyyy")//
				.left().is(2.5 * Auxiliary.tapSize)//
				.top().is(0)//
				.width().is(2.5 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Decor(this)//
				.labelText.is("Дата отгрузки")//
				.labelAlignRightCenter()//
				.left().is(4.7 * Auxiliary.tapSize)//
				.top().is(0)//
				.width().is(2 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new RedactDate(this)//
				.date.is(otgruzkaNaDatu)//
				.format.is("dd.MM.yyyy")//
				.left().is(7 * Auxiliary.tapSize)//
				.top().is(0)//
				.width().is(2.5 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Decor(this)//
				.labelText.is(textGPSwarning)//
				.labelStyleLargeNormal()
				.background.is(0xffffff00)
				.labelColor.is(0xffff0000)
				//.labelStyleLargeNormal()//
				.labelAlignCenterCenter()//
				.hidden().is(hideGPSwarning)//
				.left().is(10 * Auxiliary.tapSize)//
				.top().is(0 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property.minus(10 * Auxiliary.tapSize))//
				.height().is(1 * Auxiliary.tapSize)//
		);
		layoutless.child(new Decor(this)//
				.background.is(0x11000000)//
				.left().is(0)//
				.top().is(1 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(0.5 * Auxiliary.tapSize)//
		);

		layoutless.child(new Decor(this)//
				.labelText.is("🔎")//
				.labelAlignRightCenter()//
				.left().is(layoutless.width().property.minus(3.5 * Auxiliary.tapSize))//
				.top().is(0)//
				.width().is(0.5 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new RedactText(this)//
				.text.is(clientNameFilterText)
				.left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
				.top().is(0)//
				.width().is(2.5 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		clientNameFilterText.afterChange(new Task(){
			public void doTask(){
				System.out.println("clientNameFilterText [" + clientNameFilterText.value() + "]");
				resetGrid();
			}
		}, true);

	}

	void refreshGridDayTitles(){
		String filterAgent = "";
		if(Cfg.isChangedHRC()){
			filterAgent = " join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)='" + Cfg.selectedOrDbHRC() + "'";
		}else{
			//
		}
		String sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=1 group by OsnovnoyKlientTorgovoyTochki";
		System.out.println("refreshGridDayTitles " + sql);
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnPn.title.is("Пн/" + data.children.size());
		sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=2 group by OsnovnoyKlientTorgovoyTochki";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnVt.title.is("Вт/" + data.children.size());
		sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=3 group by OsnovnoyKlientTorgovoyTochki";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnSr.title.is("Ср/" + data.children.size());
		sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=4 group by OsnovnoyKlientTorgovoyTochki";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnCh.title.is("Чт/" + data.children.size());
		sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=5 group by OsnovnoyKlientTorgovoyTochki";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnPt.title.is("Пт/" + data.children.size());
		sql = "select count(k.OsnovnoyKlientTorgovoyTochki) as cc from MarshrutyAgentov m join Kontragenty k on k._idrref=m.kontragent" + filterAgent + " where DenNedeli=6 group by OsnovnoyKlientTorgovoyTochki";
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		columnSb.title.is("Сб/" + data.children.size());
		//columnPn.header(this);
		//columnPn.header(this)
	}

	void addRouteGrid(){
		dataGrid = new DataGrid2(this).center.is(true)//
				.headerHeight.is(1 * Auxiliary.tapSize)
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
				.beforeFlip.is(new Task(){
					@Override
					public void doTask(){
						requeryGridData();
						flipGrid();
					}
				});
		columnKod = new ColumnText();
		columnClient = new ColumnDescription();
		columnPn = new ColumnText();
		columnVt = new ColumnText();
		columnSr = new ColumnText();
		columnCh = new ColumnText();
		columnPt = new ColumnText();
		columnSb = new ColumnText();
		columnMenu = new ColumnText();
		layoutless.child(dataGrid//
				.headerHeight.is(0.5 * Auxiliary.tapSize)//
				.columns(new Column[]{//
						columnKod.title.is("Код").width.is(1.5 * Auxiliary.tapSize)
						, columnClient.title.is("Контрагент").width.is(Auxiliary.screenWidth(this) - 8.5 * Auxiliary.tapSize)
						, columnPn.title.is("пн").width.is(1 * Auxiliary.tapSize)
						, columnVt.title.is("вт").width.is(1 * Auxiliary.tapSize)
						, columnSr.title.is("ср").width.is(1 * Auxiliary.tapSize)
						, columnCh.title.is("чт").width.is(1 * Auxiliary.tapSize)
						, columnPt.title.is("пт").width.is(1 * Auxiliary.tapSize)
						, columnSb.title.is("ср").width.is(1 * Auxiliary.tapSize)
						/*
						, columnPn.title.is("Пн/"+cnt1 ).width.is(1 * Auxiliary.tapSize)
						, columnVt.title.is("Вт/"+cnt2 ).width.is(1 * Auxiliary.tapSize)
						, columnSr.title.is("Ср/"+cnt3 ).width.is(1 * Auxiliary.tapSize)
						, columnCh.title.is("Чт/"+cnt4 ).width.is(1 * Auxiliary.tapSize)
						, columnPt.title.is("Пт/"+cnt5 ).width.is(1 * Auxiliary.tapSize)
						, columnSb.title.is("Сб/"+cnt6 ).width.is(1 * Auxiliary.tapSize)
						*/
						, columnMenu.title.is(" ").width.is(1 * Auxiliary.tapSize)
				})//
				.left().is(0)//
				.top().is(1 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize))//
		);

	}

	void addBottomButtons(){
		layoutless.child(new Decor(this)//
				.background.is(0x11000000)//
				.left().is(0)//
				.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
				.width().is(layoutless.width().property)//
				.height().is(1)//
		);
		final Numeric vigruzitN = new Numeric();
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						final String code = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
						String tpFIO = Cfg.polzovatelFIO(Cfg.whoCheckListOwner());
						Auxiliary.pickSingleChoice(Activity_Route_2.this, new String[]{//
								"GPS (" + tpFIO + ")", "Заявки", "Фиксированные цены"
								, "Возвраты от покупателей"
						}, vigruzitN, "Выгрузить", new Task(){
							public void doTask(){
								if(vigruzitN.value() == 0){
									final UploadTask task = new UploadTask(ApplicationHoreca.getInstance().getDataBase()
											, SystemHelper.getDiviceID(Activity_Route_2.this)
											, getApplicationContext());
									new Expect().status.is("Выгрузка визитов...")//
											.task.is(new Task(){
												public void doTask(){
													try{
														task.UploadGPSPoints(code);
														System.out.println("task.mResultString " + task.mResultString);
														task.UploadVizits();
														System.out.println("task.mResultString " + task.mResultString);
													}catch(Throwable t){
														t.printStackTrace();
													}
												}
											})
											.afterDone.is(new Task(){
												public void doTask(){
													Auxiliary.warn(task.mResultString, Activity_Route_2.this);
												}
											}).start(Activity_Route_2.this);
								}
								if(vigruzitN.value() == 1){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_UploadBids.class);
									startActivity(intent);
								}
								if(vigruzitN.value() == 2){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_UploadFixedPrices.class);
									startActivity(intent);
								}
								if(vigruzitN.value() == 3){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_UploadReturns.class);
									startActivity(intent);
								}
							}
						}, null, null, null, null);
					}
				})//
				.labelText.is("Выгрузить")//
				.left().is(0)//
				.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		final Numeric docN = new Numeric();
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						Auxiliary.pickSingleChoice(Activity_Route_2.this, new String[]{//
								"Визиты ТП", "Заявки исходящие", "GPS отметки"
						}, docN, "Документы", new Task(){
							public void doTask(){
								if(docN.value() == 0){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_Doc_Visits.class);
									startActivity(intent);
								}
								if(docN.value() == 1){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_Doc_Bids.class);
									startActivity(intent);
								}
								if(docN.value() == 2){
									Intent intent = new Intent();
									intent.setClass(Activity_Route_2.this, Activity_Doc_GPS_Points.class);
									startActivity(intent);
								}
							}
						}, null, null, null, null);
					}
				})//
				.labelText.is("Документы")//
				.left().is(3 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						Intent intent = new Intent();
						intent.setClass(Activity_Route_2.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
						startActivity(intent);
					}
				})//
				.labelText.is("Отчёты")//
				.left().is(6 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						Intent intent = new Intent();
						intent.setClass(Activity_Route_2.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
						intent.putExtra(ActivityWebServicesReports.goLastPageReportName, ReportStatusyZakazov.folderKey());
						startActivity(intent);
					}
				})//
				.labelText.is("Статусы")//
				.left().is(9 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						Intent intent = new Intent();
						intent.setClass(Activity_Route_2.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
						intent.putExtra(ActivityWebServicesReports.goLastPageReportName, ReportStatistikaZakazov2.folderKey());
						startActivity(intent);
					}
				})//
				.labelText.is("Статистика")//
				.left().is(12 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this)//
				.afterTap.is(new Task(){
					public void doTask(){
						promptTerritory();
					}
				}).labelText.is(currentHRCName)//
				.left().is(15 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(layoutless.width().property.minus(15 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize)//
		);
	}

	void showNextPopUpPage(){
		System.out.println("showNextPopUpPage");
		//hidePopUp.value(true);
		if(currentPopupIdx < popupList.children.size() - 1){
			currentPopupIdx++;
			showPopUpPage();
		}else{
			hidePopUp.value(true);
		}
	}

	void showPrePopUpPage(){
		System.out.println("showPrePopUpPage");
		//hidePopUp.value(true);
		if(currentPopupIdx > 0){
			currentPopupIdx--;
			showPopUpPage();
		}
	}

	void showPopUpPage(){
		//popUpURL.value()
		hidePopUp.value(false);
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/ObnovlenieInfo/КартинкаПриНачалеРаботы/"
				+ popupList.children.get(currentPopupIdx).child("Номер").value.property.value()
				+ "/" + popupList.children.get(currentPopupIdx).child("Дата").value.property.value();
		popup.login.is(Cfg.whoCheckListOwner());
		popup.password.is(Cfg.hrcPersonalPassword());
		popup.go(url);
	}

	Bough popupList = new Bough();
	int currentPopupIdx = 0;

	//static boolean popUpShow=true;
	void initPopUp(){
		final Note popupdata = new Note();
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/ObnovlenieInfo/ПриНачалеРаботы/"
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(b, "UTF-8");
					//Bough rr = Bough.parseJSON(txt);
					System.out.println("initPopUp " + txt);
					//popupList.children = rr.children("Результат" );
					popupdata.value(txt);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Bough rr = Bough.parseJSON(popupdata.value());
				popupList.children = rr.children("Результат");
				if(popupList.children.size() > 0){
					if(needToShowPopUp(popupdata.value())){
						showPopUpPage();
					}
				}
			}
		}).status.is("Подождите...").start(this);
	}

	boolean needToShowPopUp(String data){
		String now = Auxiliary.rusDate.format(new Date());
		//String flag = now + data;
		File culog = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/currentpopup.txt");
		File lastlog = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/lastpopup.txt");
		Auxiliary.writeTextToFile(culog, now + data);
		String curuse = Auxiliary.strings2text(Auxiliary.readTextFromFile(culog));
		String lastuse = Auxiliary.strings2text(Auxiliary.readTextFromFile(lastlog));
		if(curuse.equals(lastuse)){
			return false;
		}else{
			Auxiliary.writeTextToFile(lastlog, now + data);
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//menuVigrusit = menu.add("Выгрузить");
		//menuDocumenti = menu.add("Документы");
		//menuGPSinformacia = menu.add("GPS информация" );
		//menuTest = menu.add("Do test");
		menuAnketi = menu.add("Анкеты заявок на новых клиентов");
		menuDannieMercury = menu.add("Данные Меркурий");
		menuZayavkaVnutrenneePeremechenie = menu.add("Заявка на внутреннее перемещение");
		menuZayavkaVozmehenie = menu.add("Заявки на возмещение");
		menuZayavkaNaPerevodVdosudebnye = menu.add("Заявка на вывод/перевод в СБ");
		menuDobavitKlientaVMarsgrut = menu.add("Заявки на добавление клиента в маршрут");
		menuPerebitNakladnuyu = menu.add("Заявки на изменение накладной");
		menuZayavkaNaPostavku = menu.add("Заявки на поставку");
		menuIskluchenieVizitov = menu.add("Исключение визитов");
		menuPoKassamDlyaTP = menu.add("Кассовые чеки");
		menuKontaktnayaInformacia = menu.add("Контактная информация");
		menuMatricaTP = menu.add("Матрицы ТП");
		menuVizitGroup = menu.add("Начать объединенный визит");
		menuMarshrutDogovora = menu.add("Обновить маршрут и договоры");
		menuOtchety = menu.add("Отчёты");
		menuPeredatIsprNakl = menu.add("Передать исправленную накладную");
		menuPlanObuchenia = menu.add("План обучения");
		//menuMap = menu.add("Положение на карте" );
		menuDataCheck = menu.add("Проверка БД");
		menuRasporyazheniaNaOtgruzku = menu.add("Распоряжения на отгрузку");
		menuZapiski = menu.add("Служебные записки на договоры");
		menuChangeUser = menu.add("Сменить пользователя");
		menuFirebaseMesasages = menu.add("Сообщения");
		menuHelp = menu.add("Справочная документация");
		menuFinDocs = menu.add("Фин/Юр. документы");
		menuCheckDocs = menu.add("Чек-листы");


		//menuResetExchange = menu.add("Очистка БД");


		//menuLimit = menu.add("Установка лимитов контрагентов");

		//menuVizitGroup = menu.add("Открыть группу визитов");
		//menuNezakrVizit = menu.add("Удалить все визиты");


		// menuTONacenka = menu.add("Обновить показатели");


		return true;
	}

	void doMarshrutDogovora(){
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ObnovlenieInfo/DannyeMarshruta?hrc=" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim();
		//final String url = Settings.getInstance().getBaseURL() + "GolovaNew/hs/ObnovlenieInfo/DannyeMarshruta?hrc=" + mAppInstance.getCurrentAgent().getAgentName().trim();
		//System.out.println(url);
		final Note n = new Note();
		final Note statusText = new Note();
		statusText.value("Обновление БД");
		n.value("Маршрут и договоры обновлены");
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					System.out.println("DannyeMarshruta size: " + b.length);
					String s = new String(b, "utf-8");
					String[] commands = s.split("\n");
					System.out.println("commands.length: " + commands.length);
					if(commands.length > 3){
						ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MarshrutyAgentov;");
						for(int i = 0; i < commands.length; i++){
							String sql = commands[i].trim();
							if(sql.length() > 1){
								//System.out.println(i + ": " + sql);0
								ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
								if(i % 300 == 0){
									System.out.println("sql: " + i + ": " + sql);
									statusText.value("Обновление " + (Math.round(100 * i / commands.length)) + "%");
								}
							}
						}
						System.out.println("refreshDogovoryKontragentov_strip");
						UpdateTask.refreshDogovoryKontragentov_strip(ApplicationHoreca.getInstance().getDataBase());
						UpdateTempTables.UpdateMarshrutyAgentovContracts(ApplicationHoreca.getInstance().getDataBase());
					}else{
						n.value("Нет доступа к данным. Проверьте интернет и повторите запрос.");
						System.out.println("Empty list");
					}
				}catch(Exception e){
					e.printStackTrace();
					//System.out.println(e.toString());
					n.value("Error: " + e.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				System.out.println("DannyeMarshruta done");
				resetGrid();

				Auxiliary.warn("" + //
						n.value(), Activity_Route_2.this);
			}
		}).status.is(statusText).start(this);
	}

	void dataCheck(){
		//System.out.println("integrity...");
		final Note n = new Note();
		n.value("Ошибка проверки БД");
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				//DatabaseHelper.forceUpperCase(mDB);
				boolean b = ApplicationHoreca.getInstance().getDataBase().isDatabaseIntegrityOk();
				String dbCheck = b
						? "ок"
						: "повреждён";
				//System.out.println("integrity " + dbCheck);
				if(b){
					//mDB.execSQL("	vacuum;	");
					DatabaseHelper.forceUpperCase(ApplicationHoreca.getInstance().getDataBase());
				}
				String s = "";
				if(b){
					//
					//UpdateTask.setupAndStripData(mDB, null);
					String sql = "select \"Номенклатура\" as tag, 1+count(Nomenklatura._id) as nn from Nomenklatura"//
							+ "\n	union"//
							+ "\n	select \"Текущие цены остатков партий\" as tag, 1+count(TekuschieCenyOstatkovPartiy._id) as nn from TekuschieCenyOstatkovPartiy"//
							+ "\n	union"//
							+ "\n	select \"Цены номенклатуры склада\" as tag, 1+count(CenyNomenklaturySklada._id) as nn from CenyNomenklaturySklada"//
							+ "\n	union"//
							+ "\n	select \"Запреты отгрузок ответственного\" as tag, 1+count(ZapretOtgruzokOtvetsvennogo._id) as nn from ZapretOtgruzokOtvetsvennogo"//
							+ "\n	union"//
							+ "\n	select \"Адреса по складам\" as tag, 1+count(AdresaPoSkladam._id) as nn from AdresaPoSkladam"//
							;
					Bough t = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					for(int i = 0; i < t.children.size(); i++){
						s = s + "\n" + t.children.get(i).child("tag").value.property.value() + ": " + t.children.get(i).child("nn").value.property.value();
					}
				}
				n.value(dbCheck + s);
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Файл базы данных: " + //
						n.value(), Activity_Route_2.this);
			}
		}).status.is("Проверка БД").start(this);
	}

	void mDBexecSQL(String sql){
		//System.out.println(sql);
		try{
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	void startResetExchange(){
		final Note error = new Note().value("Перезайдите в приложение (обновление будет закачано заново).");
		new Expect().status.is("Подождите...")//
				.task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String sql = "delete from GPSPoints";// where date(BeginTime)<date('now','-1 days') or Upload>0;";
							mDBexecSQL(sql);
							sql = "delete from ZapretOtgruzokOtvetsvennogo";
							mDBexecSQL(sql);
							sql = "delete from SyncLog";
							mDBexecSQL(sql);
							//sql = "delete from Vizits;";
							//mDB.execSQL(sql);
							sql = "delete from Polzovateli where PometkaUdaleniya=X'01'";
							mDBexecSQL(sql);
							sql = "delete from RasporyazhenieNaOtgruzku";
							mDBexecSQL(sql);
							sql = "delete from RasporyazhenieNaOtgruzku_Phayly";
							mDBexecSQL(sql);
							sql = "delete from ZakazPokupatelya";
							mDBexecSQL(sql);
							sql = "delete from ZakazPokupatelya_Tovary";
							mDBexecSQL(sql);
							sql = "delete from ZakazPokupatelya_Uslugi";
							mDBexecSQL(sql);
							sql = "delete from Zapiski";
							mDBexecSQL(sql);
							sql = "delete from ZapiskiFiles";
							mDBexecSQL(sql);
							sql = "delete from ZapretOtgruzokOtvetsvennogo";
							mDBexecSQL(sql);
							sql = "delete from ZapretSkidokProizv";
							mDBexecSQL(sql);
							sql = "delete from ZapretSkidokTov";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaDegustaciu";
							mDBexecSQL(sql);
							sql = "delete from ZapretyNaOtguzku";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaDegustaciuNomenklatura";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaSkidki";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaSkidki_TovaryPhiksCen";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaSpecifikasia";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaSpecifikasiaNomenklatura";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaVozvrat";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaNaVozvrat_Tovary";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaPokupatelyaIskhodyaschaya";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Smvz";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Tovary";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Traphiki";
							mDBexecSQL(sql);
							sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Uslugi";
							mDBexecSQL(sql);
							sql = "delete from Prodazhi";
							mDBexecSQL(sql);
							sql = "delete from AdresaPoSkladam where _id not in (select"//
									+ " _id"//
									+ " from AdresaPoSkladam a1"//
									+ " where a1.period = (select max(period) from AdresaPoSkladam a2"//
									+ " where a1.nomenklatura = a2.nomenklatura"//
									+ " and a1.baza = a2.baza) and a1.sklad <> X'00000000000000000000000000000000' and a1.sklad <> X'00'"//
									+ " group by a1.baza, a1.sklad, a1.nomenklatura)"//
							;
							mDBexecSQL(sql);
							sql = "delete from Cur_Limity";
							mDBexecSQL(sql);
							sql = "delete from TekuschieCenyOstatkovPartiy";
							mDBexecSQL(sql);
							sql = "delete from Vizits";
							mDBexecSQL(sql);
							sql = "delete from DlyaRaschetaNacenkiVNetbuke where period < date('now','-2 month')";
							mDBexecSQL(sql);
							sql = "delete from GruppyDogovorov where _idrref in"//
									+ " (select GruppyDogovorov._idrref from GruppyDogovorov"//
									+ " join DogovoryKontragentov on DogovoryKontragentov.GruppaDogovorov=GruppyDogovorov._idrref"//
									+ " join Kontragenty on Kontragenty._idrref=DogovoryKontragentov.vladelec"//
									+ " and Kontragenty.PometkaUdaleniya=x'01')";
							mDBexecSQL(sql);
							sql = "delete from DogovoryKontragentov where _idrref in"//
									+ " (select DogovoryKontragentov._idrref from DogovoryKontragentov"//
									+ " join Kontragenty on Kontragenty._idrref=DogovoryKontragentov.vladelec" //
									+ " and Kontragenty.PometkaUdaleniya=x'01')";
							mDBexecSQL(sql);
							sql = "delete from Kontragenty where PometkaUdaleniya=x'01'";
							mDBexecSQL(sql);
							sql = "delete from FiksirovannyeCeny where DataOkonchaniya<date('now','-1 day')";
							mDBexecSQL(sql);
							sql = "delete from Limity where _id not in"//
									+ " (select _id from ("//
									+ " select _id,max(period) from Limity group by SpisokDogovorov"//
									+ " ))";
							mDBexecSQL(sql);
							sql = "delete from NakopitelnyeSkidki where DataOkonchaniya<date('now','-1 day')";
							mDBexecSQL(sql);
							//System.out.println(sql);
							//sql = "delete from Nomenklatura where _idrref not in (select nomenklatura from AdresaPoSkladam) and etoGruppa=x'01'";
							//mDBexecSQL(sql);
							sql = "delete from EdinicyIzmereniya where vladelec_2 not in (select _idrref from nomenklatura)";
							mDBexecSQL(sql);
							sql = "delete from VelichinaKvantovNomenklatury where nomenklatura not in (select _idrref from nomenklatura)";
							mDBexecSQL(sql);
							sql = "delete from MatricaX;";
							mDBexecSQL(sql);
							sql = "delete from MatricaRowsX;";
							mDBexecSQL(sql);
							sql = "delete from MatricaSvodX;";
							mDBexecSQL(sql);
							sql = "delete from AnketaKlienta;";
							mDBexecSQL(sql);
							sql = "delete from AnketaKlientaContacts;";
							mDBexecSQL(sql);
							//
							//
							File files = new File("/sdcard/horeca/reserve/delta/");
							for(File file: files.listFiles()){
								boolean b = file.delete();
								//System.out.println(file.getAbsolutePath() + " deleted: " + b);
							}
							//DatabaseHelper.forceUpperCase(mDB);
						}catch(Throwable t){
							error.value("Ошибка: " + t.toString());
							t.printStackTrace();
						}
					}
				})//
				.afterCancel.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn("Ошибка: операция прервана.", Activity_Route_2.this);
					}
				})//
				.afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.warn(error.value(), Activity_Route_2.this);
					}
				})//
				.start(this);
	}

	void resetExchange(){
		Auxiliary.pick3Choice(this, "Очистка БД", "Удалить все обновления, визиты, заказы, заявки, историю?", "Да", new Task(){
			@Override
			public void doTask(){
				startResetExchange();
			}
		}, null, null, null, null);
	}

	void doVizitGroup(){
		//System.out.println("doVizitGroup");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String d = format.format(new java.util.Date());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(zaDatu.value().longValue());
		String sql = "select"//
				+ "\n		k.GeographicheskayaShirota as shirota"//
				+ "\n		,k.GeographicheskayaDolgota as dolgota"//
				+ "\n		,k.naimenovanie as name"//
				+ "\n		,k.kod as kod"//
				+ "\n		,ifnull((select max(begindate) from vizits v where v.client=k.kod),'?') as last"//
				+ "\n	from kontragenty k"//
				+ "\n		join marshrutyagentov m on m.kontragent=k._idrref"//
				+ "\n		where m.dennedeli=" + (calendar.get(Calendar.DAY_OF_WEEK) - 1)//
				+ "\n		group by k._idrref"//
				+ "\n		having last<>'" + d + "'"//
				+ "\n	order by k.naimenovanie";
		//System.out.println(sql);
		Cursor c = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
		Vector<String> v = new Vector<String>();
		int t = 0;
		final They<Integer> defaultSelection = new They<Integer>();
		final Vector<String> ids = new Vector<String>();
		while(c.moveToNext()){
			double shirota = c.getDouble(0);
			double dolgota = c.getDouble(1);
			String kod = c.getString(3);
			String name = c.getString(2);
			long distanceToClient = GPSInfo.isTPNearClient(shirota, dolgota);
			//System.out.println(name+": "+shirota+"x"+dolgota+": "+distanceToClient);
			if(distanceToClient >= 0 && distanceToClient < Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()){
				v.add(kod + ": " + name);// + ": " + shirota + "x" + dolgota + ": " + distanceToClient + ": " + kod);
				defaultSelection.insert(t, t);
				ids.add(kod);
				t++;
				//System.out.println("add " + name);
			}else{
				//System.out.println("too far " + name);
			}
		}
		String[] names = new String[v.size()];
		for(int i = 0; i < v.size(); i++){
			names[i] = v.get(i);
		}
		if(ids.size() > 0){
			Auxiliary.pickMultiChoice(this, names, defaultSelection, "Открыть визиты", new Task(){
				@Override
				public void doTask(){
					//System.out.println("ok");
					for(int i = 0; i < defaultSelection.size(); i++){
						Integer n = defaultSelection.at(i);
						//System.out.println("ok " + n + ": " + ids.get(n));
						GPS.getGPSInfo().BeginVizit(ids.get(n));
					}
					//adapter = new RouteListAdapter(Activity_Route.this, clientsRequestHelper.Request(mDB, 0));
					//mRouteList.setAdapter(adapter);
					/*boolean filter = false;
					if (mCheckFilterByDate != null) {
						filter = mCheckFilterByDate.isChecked();
					}
					FilterListByDate(filter);*/
					//canRequery=true;
					resetGrid();
				}
			});
		}else{
			Auxiliary.warn("Нет клиентов или GPS-данные недоступны", this);
		}
		/*Bough b=Auxiliary.fromCursor(this.mDB.rawQuery(sql, null));
		for(int i=0;i<b.children.size();i++){
			Bough row=b.children.get(i);
			double shirota=Numeric.string2double(row.child("shirota").value.property.value());
			double dolgota=Numeric.string2double(row.child("dolgota").value.property.value());
			String id=row.child("id").value.property.value();
			String name=row.child("name").value.property.value();
			System.out.println(name+": "+shirota+"x"+dolgota);
		}*/
	}

	void doNezakrVizit(){
		Auxiliary.pick3Choice(this, "Удаление визитов", "Удалить все визиты?", "Да", new Task(){
			@Override
			public void doTask(){
				String sql = "delete from Vizits;";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				/*boolean filter = false;
				if (mCheckFilterByDate != null) {
					filter = mCheckFilterByDate.isChecked();
				}
				FilterListByDate(filter);*/
				//canRequery=true;
				resetGrid();
			}
		}, null, null, null, null);
	}

	public void doHelp(){
		String url = Settings.getInstance().getBaseFileStoreURL() + "android/help/";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	public void doPlanObuchenia(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityPlanObucheniaAll.class);
		this.startActivityForResult(intent, 0);
	}

	public void doFinDoc(){
		String url = Settings.getInstance().getBaseURL() + "findoc/";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	void doPoKassamDlyaTP(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityPoKassamDlyaTPAll.class);
		this.startActivityForResult(intent, 0);
	}

	public void doZapiski(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityZapiski.class);
		this.startActivityForResult(intent, 0);
	}

	void doDannieMercury(){
		Intent intent = new Intent();
		intent.setClass(this, Activity_DannieMercuryAll.class);
		this.startActivity(intent);
	}

	void replaceHRC(String newHRC){
		ApplicationHoreca.getInstance().getCurrentAgent().setAgentName(newHRC);
		ApplicationHoreca.getInstance().getDataBase().execSQL("update android set kod='" + newHRC + "' where trim(kod)!='12-';");
		ApplicationHoreca.getInstance().getDataBase().execSQL("update cur_users set name='" + newHRC + "' where type=2;");
		Bough rez = new Bough().name.is("config");
		rez.child("userKey").value.is(newHRC.trim());
		rez.child("updateKey").value.is(newHRC.trim());
		Auxiliary.writeTextToFile(new File("/sdcard/horeca/Horeca2.xml"), "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + rez.dumpXML());
	}

	void promptSelectUser(){
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/HorecaU.xml")));
		Bough configuration = Bough.parseXML(xml);
		if(configuration != null){
			Vector<Bough> list = configuration.children("user");
			if(list.size() > 0){
				final String[] hrcs = new String[list.size()];
				final String[] labels = new String[list.size()];
				for(int i = 0; i < hrcs.length; i++){
					hrcs[i] = list.get(i).child("hrc").value.property.value();
					labels[i] = hrcs[i] + " (" + list.get(i).child("label").value.property.value() + ")";
				}
				final Numeric sel = new Numeric();
				Auxiliary.pickSingleChoice(this, labels, sel, null, new Task(){
					@Override
					public void doTask(){
						String newHRC = hrcs[sel.value().intValue()];
						System.out.println("selected " + newHRC);
						replaceHRC(newHRC);
						doMarshrutDogovora();
					}
				}, null, null, null, null);
		/*Auxiliary.pickConfirm(this,"Test","Ok",new Task(){
			@Override
			public void doTask() {
				System.out.println("done");
			}
		});*/
			}else{
				Auxiliary.warn("Запрещено менять пользователей", this);
			}
		}else{
			Auxiliary.warn("Запрещено менять пользователей", this);
		}
	}
/*
	void promptDobavitKlientaVMarsgrut() {
		//Bough b=Auxiliary.loadTextFromPrivatePOST();
		new Expect().task.is(new Task() {
			@Override
			public void doTask() {
				String q = "{\"TerritoryKod\":\"х0028\",\"ClientKod\":\"117744\",\"Date\":\"2019-04-01T00:00:00\""//
						           + ",\"Days\":[{\"Day\":\"вт\",\"Time\":\"0001-01-01T13:13:00\"}"//
								                 + ",{\"Day\": \"ср\",\"Time\":\"0001-01-01T14:14:00\"}"//
								                 + "],\"UserKod\":\""+Cfg.currentHRC()+"\"}";
				Bough b = Auxiliary.loadTextFromPrivatePOST(Settings.getInstance().getBaseURL() //
																	+ "/cehan_hrc/hs/DobavlenieKlientovVMatricu/"//
						                                            //+ "hrc120107/hs/DobavlenieKlientovVMatricu/"//
						, q, 30 * 1000, "UTF-8", Cfg.hrcPersonalLogin, Cfg.hrcPersonalPassword);
				System.out.println(q);
				System.out.println(b.dumpXML());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
			}
		})//
				.status.is("Подождите.....")//
				.start(Activity_Route_2.this);
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		/*if (item == menuVigrusit) {
			Intent intent = new Intent();
			intent.setClass(this, Dialog_Upload.class);
			startActivity(intent);
			return true;
		}
		if (item == menuDocumenti) {
			Intent intent = new Intent();
			intent.setClass(this, Dialog_Documents.class);
			startActivity(intent);
			return true;
		}*/
		if(item == menuZayavkaVozmehenie){
			Intent intent = new Intent();
			intent.setClass(this, ActivityZayavkaVozmehenie.class);
			startActivity(intent);
			return true;
		}
		if(item == menuZayavkaVnutrenneePeremechenie){
			Intent intent = new Intent();
			intent.setClass(this, ActivityZayavkaVnutrenneePeremechenie.class);
			startActivity(intent);
			return true;
		}

		if(item == menuPerebitNakladnuyu){
			Intent intent = new Intent();
			intent.setClass(this, ActivityZayavkaIzmenenieNakladnoy.class);
			startActivity(intent);
			return true;
		}
		if(item == menuZayavkaNaPostavku){
			Intent intent = new Intent();
			intent.setClass(this, ActivityZayavkaNaPostavku.class);
			startActivity(intent);
			return true;
		}

		if(item == menuMarshrutDogovora){
			doMarshrutDogovora();
			return true;
		}
        /*if (item == menuTONacenka) {
            doTONacenka();
            return true;
        }*/
		if(item == menuDannieMercury){
			doDannieMercury();
			return true;
		}
		if(item == menuChangeUser){
			promptSelectUser();
			return true;
		}
		if(item == menuFirebaseMesasages){
			Intent intent = new Intent();
			intent.setClass(this, Activity_FireBaseMessages.class);
			startActivity(intent);
			return true;
		}

		if(item == menuCheckDocs){
			Intent intent = new Intent();
			intent.setClass(this, ActivityCheckDocs.class);
			startActivity(intent);
			return true;
		}
		/*
		if(item == menuGPSinformacia){
			Intent intent = new Intent();
			intent.setClass(this, Activity_GpsInfo.class);
			startActivity(intent);
			return true;
		}
		*/
		if(item == menuDobavitKlientaVMarsgrut){
			//promptDobavitKlientaVMarsgrut();
			Intent intent = new Intent();
			intent.setClass(this, Activity_IzmenitMarshrutList.class);
			startActivity(intent);
			return true;
		}
		if(item == menuRasporyazheniaNaOtgruzku){
			Intent intent = new Intent();
			intent.setClass(this, Activity_Disposals.class);
			startActivity(intent);
			return true;
		}
		if(item == menuOtchety){
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if(item == menuDataCheck){
			dataCheck();
			return true;
		}
        /*if (item == menuResetExchange) {
            resetExchange();
            return true;
        }*/
		if(item == menuMatricaTP){
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityMatricaList.class);
			startActivity(intent);
			return true;
		}
		/*if(item == menuMap){
			Intent intent = new Intent();
			intent.setClass(this, ActivityGPSMap.class);
			this.startActivityForResult(intent, 0);
			return true;
		}*/
		/*if(item == menuLimit) {
			Intent intent = new Intent();
			intent.setClass(this, ActivityLimitList.class);
			this.startActivityForResult(intent, 0);
			return true;
		}*/
/*
		if(item == menuTest){
			doTest();
			return true;
		}*/
		if(item == menuAnketi){
			Intent intent = new Intent();
			intent.setClass(this, ActivityVseAnketi.class);
			this.startActivityForResult(intent, 0);
			return true;
		}
		if(item == menuVizitGroup){
			doVizitGroup();
			return true;
		}
        /*if (item == menuNezakrVizit) {
            doNezakrVizit();
            return true;
        }*/
		if(item == menuHelp){
			doHelp();
			return true;
		}
		if(item == menuPlanObuchenia){
			doPlanObuchenia();
			return true;
		}
		if(item == menuFinDocs){
			doFinDoc();
			return true;
		}
		if(item == menuZapiski){
			doZapiski();
			return true;
		}
		if(item == menuPoKassamDlyaTP){
			doPoKassamDlyaTP();
			return true;
		}
		if(item == this.menuPeredatIsprNakl){
			doPeredatIsprNakl();
			return true;
		}
		if(item == this.menuKontaktnayaInformacia){
			doKontaktnayaInformacia();
			return true;
		}
		if(item == menuZayavkaNaPerevodVdosudebnye){
			promptZayavkaNaPerevodVdosudebnye();
			return true;
		}
		if(item == menuIskluchenieVizitov){
			promptIskluchenieVizitov();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
		void doTest(){
			String testData = "<raw>\n" +
					"\t<Статус>1</Статус>\n" +
					"\t<Сообщение></Сообщение>\n" +
					"\t<ДанныеПоЗаказам>\n" +
					"\t\t<ВнешнийНомер>HRC682-0829123639817</ВнешнийНомер>\n" +
					"\t\t<Статус>1</Статус>\n" +
					"\t\t<Сообщение></Сообщение>\n" +
					"\t\t<Заказы>\n" +
					"\t\t\t<Номер>12-1855056</Номер>\n" +
					"\t\t\t<Тип>Заказ покупателя</Тип>\n" +
					"\t\t\t<Статус>2</Статус>\n" +
					"\t\t\t<Сообщение></Сообщение>\n" +
					"\t\t\t<НеПодтвержденныеПозиции>\n" +
					"\t\t\t\t<Номенклатура>79580, Майонез Печагин 67%Professional ведро 940 мл/880 гр</Номенклатура>\n" +
					"\t\t\t\t<КоличествоЗаказано>24</КоличествоЗаказано>\n" +
					"\t\t\t\t<КоличествоДефицит>23</КоличествоДефицит>\n" +
					"\t\t\t\t<КоличествоПодтверждено>1</КоличествоПодтверждено>\n" +
					"\t\t\t\t<ДатаПоступления>20240829</ДатаПоступления>\n" +
					"\t\t\t\t<Текст>с 06/06 повышение на масло  5-10,5%</Текст>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>79581</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин Экстра 67% ведро 3 л/2,82 кг</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>143</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>79805</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин оливковый 67% 940 мл/880 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>60</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>83674</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин провансаль дой-пак 67% 420 мл/395 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>154</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>83173</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин 56% Professional 930 мл/880 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>30</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>110540</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Efko Food Веганез 56% 3л</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>2</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t</НеПодтвержденныеПозиции>\n" +
					"\t\t</Заказы>\n" +
					"\t</ДанныеПоЗаказам>\n" +
					"\t<ДанныеПоЗаказам>\n" +
					"\t\t<ВнешнийНомер>HRC682-0829125920686</ВнешнийНомер>\n" +
					"\t\t<Статус>1</Статус>\n" +
					"\t\t<Сообщение></Сообщение>\n" +
					"\t\t<Заказы>\n" +
					"\t\t\t<Номер>12-1855058</Номер>\n" +
					"\t\t\t<Тип>Заказ покупателя</Тип>\n" +
					"\t\t\t<Статус>2</Статус>\n" +
					"\t\t\t<Сообщение></Сообщение>\n" +
					"\t\t\t<НеПодтвержденныеПозиции>\n" +
					"\t\t\t\t<Номенклатура>110540, Майонез Efko Food Веганез 56% 3л</Номенклатура>\n" +
					"\t\t\t\t<КоличествоЗаказано>24</КоличествоЗаказано>\n" +
					"\t\t\t\t<КоличествоДефицит>22</КоличествоДефицит>\n" +
					"\t\t\t\t<КоличествоПодтверждено>2</КоличествоПодтверждено>\n" +
					"\t\t\t\t<ДатаПоступления></ДатаПоступления>\n" +
					"\t\t\t\t<Текст></Текст>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>79580</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин 67%Professional ведро 940 мл/880 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>0</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>79581</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин Экстра 67% ведро 3 л/2,82 кг</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>143</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>79805</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин оливковый 67% 940 мл/880 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>60</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>83173</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Печагин 56% Professional 930 мл/880 гр</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>30</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>116235</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Юг Profi Провансаль 67% 5л</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>82</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>116661</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез Pechagin Professional Profi 67% 5л Ведро</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>130</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t\t<Аналоги>\n" +
					"\t\t\t\t\t<Артикул>110535</Артикул>\n" +
					"\t\t\t\t\t<Наименование>Майонез EFKO FOOD Professional универсальный 67% 10л/9,34кг</Наименование>\n" +
					"\t\t\t\t\t<ДоступноеКоличество>83</ДоступноеКоличество>\n" +
					"\t\t\t\t</Аналоги>\n" +
					"\t\t\t</НеПодтвержденныеПозиции>\n" +
					"\t\t</Заказы>\n" +
					"\t</ДанныеПоЗаказам>\n" +
					"</raw>";
			Intent intent = new Intent();
			intent.setClass(this, UploadOrderResult.class);
			intent.putExtra("data", testData);
			this.startActivityForResult(intent, UploadOrderResult.UploadDialogResult);

		}
	*/
	void promptIskluchenieVizitov(){
		Numeric territory = new Numeric();

		Numeric dateFrom = new Numeric().value((float)(new Date().getTime()));
		Numeric dateTo = new Numeric().value((float)(new Date().getTime()));
		Numeric num = new Numeric();

		RedactSingleChoice terr = new RedactSingleChoice(this);
		terr.selection.is(territory);
		for(int i = 0; i < Cfg.territory().children.size(); i++){
			String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
					+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			terr.item(s);
		}

		Auxiliary.pick(this, "", new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("Исключение визитов").labelAlignLeftTop()//.background.is(0xff00ffff)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(0.3 * Auxiliary.tapSize)
								.width().is(Auxiliary.tapSize * 15 - Auxiliary.tapSize)
								.height().is(Auxiliary.tapSize * 0.5))//
						.child(terr
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.0)
								.width().is(Auxiliary.tapSize * 15 - Auxiliary.tapSize)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).date.is(dateFrom).format.is("dd.MM.yyyy")
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 2.0)
								.width().is(Auxiliary.tapSize * 6.5)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new Decor(this).labelText.is("до").labelAlignCenterCenter()
								.left().is(Auxiliary.tapSize * 7)
								.top().is(2 * Auxiliary.tapSize)
								.width().is(Auxiliary.tapSize * 1)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).date.is(dateTo).format.is("dd.MM.yyyy")
								.left().is(Auxiliary.tapSize * 8.0)
								.top().is(Auxiliary.tapSize * 2.0)
								.width().is(Auxiliary.tapSize * 6.5)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactSingleChoice(this).selection.is(num)
								.item("Больничный")
								.item("Вакансии")
								//.item("Отпуск" )
								.item("Административный")
								.item("Не работает нетбук")
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 3.0)
								.width().is(Auxiliary.tapSize * 15 - Auxiliary.tapSize)
								.height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 15)//
						.height().is(Auxiliary.tapSize * 6)//
				, "Добавить", new Task(){
					@Override
					public void doTask(){
						sendIskluchenieVizitov(false, dateFrom.value().longValue(), dateTo.value().longValue(), "" + num.value().intValue(), territory.value().intValue());
					}
				}, "Удалить", new Task(){
					@Override
					public void doTask(){
						sendIskluchenieVizitov(true, dateFrom.value().longValue(), dateTo.value().longValue(), "" + num.value().intValue(), territory.value().intValue());
					}
				}, null, null);
	}

	void sendIskluchenieVizitov(boolean udalit, long from, long to, String num2, int hrcNN){

		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String num = num2;
				if(num2.equals("0"))
					num = "1";
				if(num2.equals("1"))
					num = "2";
				if(num2.equals("2"))
					num = "4";
				if(num2.equals("3"))
					num = "5";
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
						+ "/hs/Employees/AdminOtpusk"
						+ "/" + Cfg.territory().children.get(hrcNN).child("hrc").value.property.value().trim()
						+ "/" + num
						+ "/" + Auxiliary.short1cDate.format(new Date(from))
						+ "/" + Auxiliary.short1cDate.format(new Date(to))
						+ "/" + (udalit ? "Истина" : "Ложь");
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url
							//,"bot28","Molgav1024"
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
							, "UTF-8"
					);
					String txt = new String(b, "UTF-8");
					result.value(txt);
					System.out.println(txt);
					//Bough rr = Bough.parseJSON(txt);
					//result.value(new String(b, "UTF-8"));
					//result.value(rr.child("Message" ).value.property.value());
				}catch(Throwable t){
					t.printStackTrace();
					result.value(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), Activity_Route_2.this);
			}
		}).status.is("Подождите...").start(this);
	}

	void promptZayavkaNaPerevodVdosudebnye(){
		String sql = "select kontragenty.kod as kod,kontragenty.naimenovanie as naimenovanie from kontragenty"//
				+ " group by kontragenty.kod"//
				+ " order by kontragenty.naimenovanie";
		final Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		final Vector<String> names = new Vector<String>();
		final Vector<String> kods = new Vector<String>();
		for(int i = 0; i < bough.children.size(); i++){
			Bough row = bough.children.get(i);
			names.add(bough.children.get(i).child("kod").value.property.value() + ": " + bough.children.get(i).child("naimenovanie").value.property.value());
			kods.add(bough.children.get(i).child("kod").value.property.value());
		}
		final Numeric sel = new Numeric();
		Auxiliary.pickFilteredChoice(this, names.toArray(new String[0]), sel//
				, new Task(){
					@Override
					public void doTask(){
						final String kod = kods.get(sel.value().intValue());
						String msg = "Отправить " + (names.get(sel.value().intValue())) + " в досудебные?";
						Auxiliary.pickConfirm(Activity_Route_2.this, msg, "Отправить заявку", new Task(){
									@Override
									public void doTask(){
										sendZayavkaNaPerevodVdosudebnye(kod);
									}
								}

						);

					}
				});
	}

	void sendZayavkaNaPerevodVdosudebnye(final String selectedKlientKod){

		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/CreateClientTransferRequest/"
						+ selectedKlientKod;
				//+ ApplicationHoreca.getInstance().getClientInfo().getKod() ;
				//url="https://testservice.swlife.ru/okunev_hrc/hs/Planshet/CreateClientTransferRequest/277793";
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					Bough rr = Bough.parseJSON(new String(b, "UTF-8"));
					//result.value(new String(b, "UTF-8"));
					result.value(rr.child("Message").value.property.value());
				}catch(Throwable t){
					t.printStackTrace();
					result.value(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), Activity_Route_2.this);
			}
		}).status.is("Подождите...").start(this);
	}

	void doKontaktnayaInformacia(){
		final Bough result = new Bough();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String toURL = "https://service.swlife.ru/hrc120107/hs/Planshet/GetContactInformation/" + Cfg.whoCheckListOwner();
					System.out.println(toURL);

					byte[] output = Auxiliary.loadFileFromPrivateURL(toURL, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					//byte[] output = Auxiliary.loadFileFromPrivateURL(toURL, "bot28", "28bot");
					String s = new String(output);
					result.child("result").children = Bough.parseJSON(s).children;
				}catch(Throwable t){
					t.printStackTrace();
					result.child("error").value.is(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				promptKontaktnayaInformacia(result);
			}
		}).status.is("Подождите...").start(Activity_Route_2.this);
	}

	void updateKontaktnayaInformacia(final String fio, final String tel, final String email){
		final Bough result = new Bough();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String text = "{\"FIO\" : \"" + fio + "\", \"Phone\" : \"" + tel + "\", \"Mail\" : \"" + email + "\"}\n";
					String toURL = "https://service.swlife.ru/hrc120107/hs/Planshet/UpdateContactInformation/" + Cfg.whoCheckListOwner();
					System.out.println(toURL);
					System.out.println(text);
					Bough tt = Auxiliary.loadTextFromPrivatePOST(toURL, text, 30 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					//Bough tt=Auxiliary.loadTextFromPrivatePOST(toURL, text, 30*1000, "UTF-8", "bot28", "28bot");
					result.child("result").children = tt.children;
				}catch(Throwable t){
					t.printStackTrace();
					result.child("error2").value.is(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				System.out.println(result.dumpXML());
				Auxiliary.warn("Отправлено "
								+ " \n" + result.child("result").child("message").value.property.value()
								+ " \n" + result.child("result").child("raw").value.property.value()
						, Activity_Route_2.this);
			}
		}).status.is("Подождите...").start(Activity_Route_2.this);
	}

	void promptKontaktnayaInformacia(final Bough result){
		System.out.println(result.dumpXML());
		/*
		I/System.out: <>
I/System.out: 	<result>
I/System.out: 		<FIO>Черкасова Татьяна Павловна</FIO>
I/System.out: 		<Phone>79872196232</Phone>
I/System.out: 		<Mail>cherkasova@swlife.nnov.ru</Mail>
I/System.out: 	</result>
I/System.out: </>
		*/
		Auxiliary.pick(this, Cfg.whoCheckListOwner() + " " + result.child("error").value.property.value(), new SubLayoutless(this)//
						/*
												.child(new Decor(this).labelText.is("ФИО").labelAlignRightBottom()
														.left().is(Auxiliary.tapSize * 0).top().is(Auxiliary.tapSize * 0.5)
														.width().is(Auxiliary.tapSize * 2.0).height().is(Auxiliary.tapSize * 0.7))//
												.child(new Decor(this).labelText.is(result.child("result").child("FIO").value.property).labelAlignLeftBottom()
														.left().is(Auxiliary.tapSize * 2.5).top().is(Auxiliary.tapSize * 0.5)
														.width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.7))//
						*/
						.child(new Decor(this).labelText.is("e-mail").labelAlignRightBottom()
								.left().is(Auxiliary.tapSize * 0).top().is(Auxiliary.tapSize * 0.5)
								.width().is(Auxiliary.tapSize * 2.0).height().is(Auxiliary.tapSize * 0.7))//
						.child(new Decor(this).labelText.is(result.child("result").child("Mail").value.property).labelAlignLeftBottom()
								.left().is(Auxiliary.tapSize * 2.5).top().is(Auxiliary.tapSize * 0.5)
								.width().is(Auxiliary.tapSize * 6).height().is(Auxiliary.tapSize * 0.7))//


						.child(new Decor(this).labelText.is("ФИО").labelAlignRightBottom()
								.left().is(Auxiliary.tapSize * 0).top().is(Auxiliary.tapSize * 1.5)
								.width().is(Auxiliary.tapSize * 2.0).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(this)
								.text.is(result.child("result").child("FIO").value.property)
								.left().is(Auxiliary.tapSize * 2.5).top().is(Auxiliary.tapSize * 1.5)
								.width().is(Auxiliary.tapSize * 6).height().is(Auxiliary.tapSize * 0.7))//


						.child(new Decor(this).labelText.is("тел.").labelAlignRightBottom()
								.left().is(Auxiliary.tapSize * 0).top().is(Auxiliary.tapSize * 2.5)
								.width().is(Auxiliary.tapSize * 2.0).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(this)
								.text.is(result.child("result").child("Phone").value.property)
								.left().is(Auxiliary.tapSize * 2.5).top().is(Auxiliary.tapSize * 2.5)
								.width().is(Auxiliary.tapSize * 6).height().is(Auxiliary.tapSize * 0.7))//

						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 6)//
				, "Обновить", new Task(){
					@Override
					public void doTask(){
						//System.out.println(result.dumpXML());
						String Phone = result.child("result").child("Phone").value.property.value().trim();
						String FIO = result.child("result").child("FIO").value.property.value();
						if(Phone.length() != 11){
							Auxiliary.warn("Тел. номер должен быть 11 символов", Activity_Route_2.this);
						}else{
							updateKontaktnayaInformacia(FIO
									, Phone
									, result.child("result").child("Mail").value.property.value()
							);
						}
					}
				}, null, null, null, null);
	}


	void doPeredatIsprNakl(){
		final Note nomerNakladnoy = new Note();
		final Numeric dataNakladnoy = new Numeric();
		Auxiliary.pick(this, "Номер и дата накладной", new SubLayoutless(this)//
						.child(new RedactText(this).text.is(nomerNakladnoy)
								.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).date.is(dataNakladnoy).format.is("dd.MM.yyyy")
								.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 6)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						//https://service.swlife.ru/hrc120107/hs/Prilozhenie/AddToReprint/17-0200356/20210831/bot26
						Date dd = new Date(dataNakladnoy.value().longValue());
						String sdate = Auxiliary.short1cDate.format(dd);
						String url = Settings.getInstance().getBaseURL();
						url = url + Settings.selectedBase1C();
						url = url + "/hs/Prilozhenie/AddToReprint";
						url = url + "/" + nomerNakladnoy.value();
						url = url + "/" + sdate;
						url = url + "/" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod();
						System.out.println(url);
						final String toURL = url;
						final Note res = new Note();
						new Expect().task.is(new Task(){
							@Override
							public void doTask(){
								try{
									byte[] output = Auxiliary.loadFileFromPrivateURL(toURL, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
									//byte[] output = Auxiliary.loadFileFromPrivateURL(toURL, "bot28","28bot");
									String s = new String(output);
									res.value(s);
								}catch(Throwable t){
									t.printStackTrace();
									res.value(t.getMessage());
								}
							}
						}).afterDone.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.warn(res.value(), Activity_Route_2.this);
							}
						}).status.is("Подождите...").start(Activity_Route_2.this);
					}
				}, null, null, null, null);

	}
/*
    void updateNacenkaDB() {
        try {
            //String url = "http://89.109.7.162/hrc120107/";
            String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/";
            url = url + "hs/ObnovlenieInfo/%D0%9F%D1%80%D0%BE%D0%B8%D0%B7%D0%B2%D0%BE%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5%D0%94%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5/%D0%9F%D0%BE%D0%BA%D0%B0%D0%B7%D0%B0%D1%82%D0%B5%D0%BB%D0%B8%D0%A2%D0%9E?hrc=";
            url = url + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod();
            byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.hrcPersonalLogin, Cfg.hrcPersonalPassword);
            String txt = new String(bytes);
            //System.out.println(txt);
            Bough b = Bough.parseJSON("{data:" + txt + "}");
            //System.out.println(b.dumpXML());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 0; i < b.children.size(); i++) {
                Bough row = b.children.get(i);
                String EdinicaPlanirovaniya = "";
                String toNarost = "" + Math.round(Double.valueOf(row.child("ТОНарост").value.property.value()) / 1000);
                String planToNarost = "" + Math.round(Double.valueOf(row.child("ПлановыйТОНарост").value.property.value()) / 1000);
                String podr = row.child("Подразделение").value.property.value();
                String toDenPlan = "" + Math.round(Double.valueOf(row.child("ПлановыйТОНаДень").value.property.value()) / 1000);
                String dt = df.format(new Date());
                String r = "ТО " + toNarost + "/" + planToNarost + "т.р., за " + dt + " - " + toDenPlan + "т.р.";
                String sql = "update Podrazdeleniya set EdinicaPlanirovaniya='" + r + "' where kod='" + podr + "';";
                System.out.println(sql);
                ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
*/

	/*
		void doTONacenka() {
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					updateNacenkaDB();
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					//System.out.println("done");
				}
			}).status.is("Подождите...").start(Activity_Route_2.this);
		}
	*/
/*
    void doTONacenkaLocal() {
        int y = Calendar.getInstance().get(Calendar.YEAR);
        int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
        final int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < gridData.children.size(); i++) {
            Bough row = gridData.children.get(i);
            final String kod = row.child("Kod").value.property.value();
            final String sql = "ВЫБРАТЬ ПЕРВЫЕ 20"//
                    + "  	СУММА(РеализацияТоваровУслуг.СуммаДокумента) КАК СуммаДокумента"//
                    + "  ИЗ"//
                    + "  	Документ.РеализацияТоваровУслуг КАК РеализацияТоваровУслуг"//
                    + "  ГДЕ"//
                    + "  	РеализацияТоваровУслуг.Контрагент.Код = " + kod//
                    + "  	и началопериода(РеализацияТоваровУслуг.Дата,месяц) = началопериода(датавремя(" + y + "," + m + "," + d + "),месяц)"//
                    ;
            final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/surikovquery/";
            //final String url = "http://89.109.7.162/hrc120107/hs/surikovquery/";
            new Expect().task.is(new Task() {
                @Override
                public void doTask() {
                    try {
                        //System.out.println(sql);
                        Bough result = Auxiliary.loadTextFromPrivatePOST(url, sql, 9000, "UTF-8", Cfg.hrcPersonalLogin, Cfg.hrcPersonalPassword);
                        //System.out.println("result "+result.dumpXML());
                        String response = result.child("raw").value.property.value();
                        //System.out.println("response "+response);
                        Bough b = Bough.parseJSONorThrow(response);
                        //System.out.println(b.child("результат").child("СуммаДокумента").dumpXML());
                        double s = Numeric.string2double(b.child("результат").child("СуммаДокумента").value.property.value().replace(" ", ""));
                        //System.out.println(s);
                        if (s > 0) {
                            String update = "update MarshrutyAgentov set OTnar=" + s + " where kontragent in (select _idrref from kontragenty where kod=" + kod + " limit 1)";
                            //System.out.println(update);
                            ApplicationHoreca.getInstance().getDataBase().execSQL(update);
                            update = "update MarshrutyAgentov set OTden=" + Math.round(s / d) + " where kontragent in (select _idrref from kontragenty where kod=" + kod + " limit 1)";
                            //System.out.println(update);
                            ApplicationHoreca.getInstance().getDataBase().execSQL(update);
                            update = "update MarshrutyAgentov set nacenka=" + Math.round(s % 20) + " where kontragent in (select _idrref from kontragenty where kod=" + kod + " limit 1)";
                            //System.out.println(update);
                            ApplicationHoreca.getInstance().getDataBase().execSQL(update);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }).afterDone.is(new Task() {
                @Override
                public void doTask() {
                    //System.out.println("done");
                }
            }).status.is("Подождите...").start(Activity_Route_2.this);
            //break;
        }
    }
*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//ApplicationHoreca.getInstance().setCRdisabledFirstTimeShow(false);
		//setShippingDayFromUI();
	}

	void requeryGridData(){
		//System.out.println("requeryGridData start");
		gridData.children.removeAllElements();
		String sql = gridSQL();
		//System.out.println("sql " + sql);
		gridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
		//System.out.println("columnClient "+columnClient);
		//System.out.println("requeryGridData done " + gridData.children.size());
	}

	String formatDayToggle(String val){
		String r = "?";
		if(val.trim().equals("1")){
			r = "✔";
		}else{
			r = "";
		}
		return r;
	}

	void flipGrid(){
		//System.out.println("flipGrid gridOffset "+this.gridOffset.value());
		dataGrid.clearColumns();
		//System.out.println("clearColumns flipGrid gridOffset "+this.gridOffset.value());
		//System.out.println(gridData.dumpXML());

		for(int i = 0; i < gridData.children.size(); i++){
			Bough row = gridData.children.get(i);
			final String kk = row.child("Kontragent").value.property.value();
			Task tapTask = new Task(){
				public void doTask(){
					Intent intent = new Intent();
					intent.setClass(Activity_Route_2.this, Activity_BidsContractsEtc_2.class);
					intent.putExtra(sweetlife.android10.consts.IExtras.CLIENT_ID, "x'" + kk + "'");
					intent.putExtra(sweetlife.android10.consts.IExtras.CHOOSED_DAY, new java.util.Date().getTime());
					startActivityForResult(intent, 0);
				}
			};
			double KolichestvoOtkrytykhDogovorov = Numeric.string2double(row.child("KolichestvoOtkrytykhDogovorov").value.property.value());
			double KolichestvoDogovorov = Numeric.string2double(row.child("KolichestvoDogovorov").value.property.value());
			int clientBG = 0xffffffff;
			int kodBG = clientBG;
			String dogovor = "Все договора открыты";
			String name = row.child("Naimenovanie").value.property.value();
			double gcnt = Numeric.string2double(row.child("gcnt").value.property.value());
			if(gcnt > 1){
				name = "<b>" +   name+"</b>";
			}
			int dayColor = 0xffff0000;
			//String lastVizitDate = row.child("lastVizitDate").value.property.value();
			String lastVizitTime = row.child("lastVizitTime").value.property.value();
			String vizitActivity = row.child("lastVizitStatus").value.property.value();
			String lastVizitEnd = row.child("lastVizitEnd").value.property.value();
			String shirota = row.child("shirota").value.property.value();
			String vizitInfo = "";
			int bg1 = clientBG;
			int bg2 = clientBG;
			int bg3 = clientBG;
			int bg4 = clientBG;
			int bg5 = clientBG;
			int bg6 = clientBG;
			String description = "";
			if(name.startsWith("(не в маршруте)")){
				clientBG = 0x11000000;
				kodBG = clientBG;
				dogovor = "";
				bg1 = clientBG;
				bg2 = clientBG;
				bg3 = clientBG;
				bg4 = clientBG;
				bg5 = clientBG;
				bg6 = clientBG;
			}else{
				if(KolichestvoOtkrytykhDogovorov > 0){
					if(KolichestvoOtkrytykhDogovorov < KolichestvoDogovorov){
						clientBG = 0xffccccff;
						dogovor = "Открытых договоров: " + Math.round(KolichestvoOtkrytykhDogovorov) + " из " + Math.round(KolichestvoDogovorov);
					}else{
						dogovor = "Всего договоров: " + Math.round(KolichestvoOtkrytykhDogovorov);
					}
				}else{
					clientBG = 0xffffcccc;
					dogovor = "Закрытых договоров: " + Math.round(KolichestvoDogovorov);
				}
				String msSqlTimeFormatString = "yyyy-MM-dd'T'HH:mm:ss";

				String endTime = "";
				if(lastVizitEnd.length() > 1){
					endTime = Auxiliary.tryReFormatDateGMT(lastVizitEnd, msSqlTimeFormatString, "dd.MM.yy HH:mm:ss", 3);
				}
				String endDate = "";

				if(lastVizitTime.length() > 5){
					endDate = Auxiliary.tryReFormatDate(lastVizitEnd, "yyyy-MM-dd", "dd.MM.yy");
				}
				/*
				String startTime = "";
				if (lastVizitDate.length() > 1) {
					startTime = Auxiliary.tryReFormatDateGMT(lastVizitTime, msSqlTimeFormatString, "dd.MM.yy HH:mm:ss", 3);
				}
				String startDate = "";
				if (lastVizitDate.length() > 1) {
					startDate = Auxiliary.tryReFormatDate(lastVizitDate, "yyyy-MM-dd", "dd.MM.yy");
				}*/
				String todayDate = Auxiliary.tryReFormatDate(Auxiliary.sqliteDate.format(new Date()), "yyyy-MM-dd", "dd.MM.yy");
				//if (lastVizitDate.length() < 3) {
				//if (lastVizitDate.length() < 3) {
				if(lastVizitTime.length() < 5){
					vizitInfo = "нет визитов";
				}else{
					//if (!startDate.equals(endDate)) {
					if(lastVizitEnd.length() < 5){
						String startTime = Auxiliary.tryReFormatDateGMT(lastVizitTime, msSqlTimeFormatString, "dd.MM.yy HH:mm:ss", 3);
						vizitInfo = startTime + " - не завершён";
						dayColor = 0xffffff00;
						kodBG = dayColor;
					}else{
						//vizitInfo = endTime + ", " + vizitActivity;
						//if (!startDate.equals(endDate)) {
						String startTime = Auxiliary.tryReFormatDateGMT(lastVizitTime, msSqlTimeFormatString, "dd.MM.yy HH:mm:ss", 3);
						vizitInfo = startTime + " - " + endTime + ", " + vizitActivity;
						//}
						if(endDate.equals(todayDate)){
							dayColor = 0xff00ff00;
							kodBG = 0xff00ff00;
						}
					}
				}
				description = dogovor + ", " + vizitInfo;
				int dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				if(dayWeek == 2){
					if(row.child("Den1").value.property.value().trim().equals("1")){
						bg1 = dayColor;
						kodBG = dayColor;
					}
				}
				if(dayWeek == 3){
					if(row.child("Den2").value.property.value().trim().equals("1")){
						bg2 = dayColor;
						kodBG = dayColor;
					}
				}
				if(dayWeek == 4){
					if(row.child("Den3").value.property.value().trim().equals("1")){
						bg3 = dayColor;
						kodBG = dayColor;
					}
				}
				if(dayWeek == 5){
					if(row.child("Den4").value.property.value().trim().equals("1")){
						bg4 = dayColor;
						kodBG = dayColor;
					}
				}
				if(dayWeek == 6){
					if(row.child("Den5").value.property.value().trim().equals("1")){
						bg5 = dayColor;
						kodBG = dayColor;
					}
				}
				if(dayWeek == 0){
					if(row.child("Den6").value.property.value().trim().equals("1")){
						bg6 = dayColor;
						kodBG = dayColor;
					}
				}
				if(shirota.length() < 3){
					kodBG = 0xccff33cc;
					description = "Координаты не зафиксированны! " + description;
				}
			}
			columnKod.cell(row.child("Kod").value.property.value(), kodBG, tapTask);
			columnClient.cell(name, clientBG, tapTask, description);

			columnPn.cell(formatDayToggle(row.child("Den1").value.property.value()), bg1, tapTask);
			columnVt.cell(formatDayToggle(row.child("Den2").value.property.value()), bg2, tapTask);
			columnSr.cell(formatDayToggle(row.child("Den3").value.property.value()), bg3, tapTask);
			columnCh.cell(formatDayToggle(row.child("Den4").value.property.value()), bg4, tapTask);
			columnPt.cell(formatDayToggle(row.child("Den5").value.property.value()), bg5, tapTask);
			columnSb.cell(formatDayToggle(row.child("Den6").value.property.value()), bg6, tapTask);
			columnMenu.cell("...", new Task(){
				public void doTask(){

					Auxiliary.pickConfirm(Activity_Route_2.this, "Распоряжение на отгрузку", "Создать", new Task(){
						public void doTask(){
							pickRaspOt("x'" + kk + "'");
						}
					});
				}
			});
		}
		//System.out.println("after flipGrid gridOffset "+this.gridOffset.value());
	}

	void pickRaspOt(String hexKlient){
		Intent intent = new Intent();
		intent.setClass(Activity_Route_2.this, Dialog_EditDisposal.class);
		intent.putExtra("hexKlient", hexKlient);
		startActivity(intent);
	}

	public String gridSQL(){
		String filterAgent = "";
		//if (ApplicationHoreca.getInstance().currentHRCmarshrut.length() > 0) {
		if(Cfg.isChangedHRC()){
			//filterAgent = "\n		join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)=\"" + ApplicationHoreca.getInstance().currentHRCmarshrut + "\"";
			filterAgent = "\n		join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)=\"" + Cfg.selectedOrDbHRC() + "\"";
		}else{
			//
		}
		String whereDay = "";
		String sortDay = "\n	order by neMarshrut,[Naimenovanie]";
		if(tolkoZaDatu.value()){
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(zaDatu.value().longValue());
			int weekNum = calendar.get(Calendar.WEEK_OF_YEAR);
			int nedelya = 1;
			if(weekNum == 2 * ((int)(weekNum * 0.5))){
				nedelya = 2;
			}
			whereDay = "\n where nedelya=0 or nedelya=" + nedelya;
			if(tolkoZaDatu.value()){
				whereDay = "\n	where m.[DenNedeli] = " + (calendar.get(Calendar.DAY_OF_WEEK) - 1) + " and (nedelya=0 or nedelya=" + nedelya + ")";
				sortDay = "\n	order by neMarshrut,[Poryadok],[Naimenovanie]";
			}
		}
		String nameFilter = "";
		if(clientNameFilterText.value().trim().length() > 0){
			nameFilter = " and k.naimenovanie like \"%"
					+ clientNameFilterText.value()
					.trim()
					.replace('"', '\'')
					.replace('%', '\'')
					+ "%\"";
		}
		String req = "select "//
				+ "\n  	* "//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=1 and a.Kontragent=m2.Kontragent) as Den1"//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=2 and a.Kontragent=m2.Kontragent) as Den2"//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=3 and a.Kontragent=m2.Kontragent) as Den3"//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=4 and a.Kontragent=m2.Kontragent) as Den4"//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=5 and a.Kontragent=m2.Kontragent) as Den5"//
				+ "\n  	,(select 1 from MarshrutyAgentov m2 where m2.DenNedeli=6 and a.Kontragent=m2.Kontragent) as Den6"//
				//+ "\n  	,(select max(BeginDate) from Vizits where Vizits.Client=a.Kod) as lastVizitDate"//
				//+ "\n  	,(select Activity from Vizits where Vizits.Client=a.Kod and BeginDate=(select max(BeginDate) from Vizits where Vizits.Client=a.Kod)) as lastVizitStatus"//
				+ "\n  	,(select Activity from Vizits where Vizits.Client=a.Kod and BeginTime=(select max(BeginTime) from Vizits where Vizits.Client=a.Kod)) as lastVizitStatus"//
				//+ "\n  	,(select max(EndTime) from Vizits where Vizits.Client=a.Kod) as lastVizitEnd"//
				+ "\n  	,(select EndTime from Vizits where Vizits.Client=a.Kod and BeginTime=(select max(BeginTime) from Vizits where Vizits.Client=a.Kod)) as lastVizitEnd"//
				//+ "\n  	,(select max(BeginTime) from Vizits where Vizits.Client=a.Kod) as lastVizitTime"//
				+ "\n  	,(select BeginTime from Vizits where Vizits.Client=a.Kod and BeginTime=(select max(BeginTime) from Vizits where Vizits.Client=a.Kod)) as lastVizitTime"//
				+ "\n  from ("//
				+ "\n  	select"//
				+ "\n  			m._id as _id"//
				+ "\n  			,k.[Kod] [Kod]"//
				+ "\n  			,m.Kontragent [Kontragent]"//
				+ "\n  			,k.[Naimenovanie] [Naimenovanie]"//
				+ "\n  			,min(m.[Poryadok]) [Poryadok]"//
				+ "\n  			,mad.[KolichestvoOtkrytykhDogovorov] [KolichestvoOtkrytykhDogovorov]"//
				+ "\n  			,mad.[KolichestvoDogovorov] [KolichestvoDogovorov]"//
				+ "\n  			,0 as neMarshrut"//
				+ "\n  			,k.GeographicheskayaShirota as shirota"//
				+ "\n  			,k.GeographicheskayaDolgota as dolgota"//
				+ "\n  			,golov.gcnt as gcnt"//
				+ "\n  		from MarshrutyAgentov m"//
				+ "\n  			inner join Kontragenty k on k.[_IDRRef] = m.[Kontragent]";
		req = req + nameFilter;
		req = req + "\n  			inner join	Cur_MarshrutyAgentov_Dogovora mad on m.[Kontragent] = mad.[Kontragent]"//
				+ filterAgent//
				+ "\n  			left join (select count(golovnoykontragent) as gcnt,golovnoykontragent as gid from kontragenty group by golovnoykontragent) golov on k._idrref=gid"
				+ whereDay//

				+ "\n  		group by m.[Kontragent]"//
				+ "\n  	   union"//
				+ "\n  	   select"//
				+ "\n  				k._id as _id"//
				+ "\n  				,k.Kod as Kod"//
				+ "\n  				,k._idrref as Kontragent"//
				+ "\n  				,'(не в маршруте)' || k.[Naimenovanie] as Naimenovanie"//
				+ "\n  				,100 as Poryadok"//
				+ "\n  				,0 as KolichestvoOtkrytykhDogovorov"//
				+ "\n  				,0 as KolichestvoDogovorov"//
				+ "\n  				,1 as neMarshrut"//
				+ "\n  			,0 as shirota"//
				+ "\n  			,0 as dolgota"//
				+ "\n  			,0 as gcnt"//
				+ "\n  		from kontragenty k"//
				+ "\n  		join (select golovnoykontragent"//
				+ "\n  			from MarshrutyAgentov m"//
				+ "\n  			join kontragenty k1 on k1._idrref=m.kontragent"//
				+ "\n  			) k2 on k._idrref=k2.golovnoykontragent"//
				+ "\n  		where k._idrref not in (select kontragent from MarshrutyAgentov group by kontragent)";
		req = req + nameFilter;
		req = req + "\n  		group by k._idrref"//
				+ sortDay//
				+ limitSQL.value()//
				+ "  		) a";


		req = req + sortDay;
		System.out.println("gridSQL " + req);
		return req;
	}
/*
    public String gridSQL2222() {
        //System.out.println("gridSQL " + tolkoZaDatu.value());
        String filterAgent = "";
        if (ApplicationHoreca.getInstance().currentHRCmarshrut.length() > 0) {
            filterAgent = "\n		join Polzovateli pz on pz._idrref=m.agent and trim(pz.kod)=\"" + ApplicationHoreca.getInstance().currentHRCmarshrut + "\"";
        } else {
            //
        }
        String whereDay = "";
        String sortDay = "\n	order by neMarshrut,[Naimenovanie]";
        if (tolkoZaDatu.value()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(zaDatu.value().longValue());
            int weekNum = calendar.get(Calendar.WEEK_OF_YEAR);
            int nedelya = 1;
            if (weekNum == 2 * ((int) (weekNum * 0.5))) {
                nedelya = 2;
            }
            whereDay = "\n where nedelya=0 or nedelya=" + nedelya;
            if (tolkoZaDatu.value()) {
                whereDay = "\n	where m.[DenNedeli] = " + (calendar.get(Calendar.DAY_OF_WEEK) - 1) + " and (nedelya=0 or nedelya=" + nedelya + ")";
                sortDay = "\n	order by neMarshrut,[Poryadok],[Naimenovanie]";
            }
        }
        String req = "select"//
                + "\n		m._id as _id"//
                + "\n		,k.[Kod] [Kod]"//
                + "\n		,m.Kontragent [Kontragent]"//
                + "\n		,k.[Naimenovanie] [Naimenovanie]"//
                + "\n		,min(m.[Poryadok]) [Poryadok]"//
                + "\n		,max(case when m.[DenNedeli] = 1 then 1 else 0 end) Den1"//
                + "\n		,max(case when m.[DenNedeli] = 2 then 1 else 0 end) Den2"//
                + "\n		,max(case when m.[DenNedeli] = 3 then 1 else 0 end) Den3"//
                + "\n		,max(case when m.[DenNedeli] = 4 then 1 else 0 end) Den4"//
                + "\n		,max(case when m.[DenNedeli] = 5 then 1 else 0 end) Den5"//
                + "\n		,max(case when m.[DenNedeli] = 6 then 1 else 0 end) Den6"//
                + "\n			,OTden as OTden"//
                + "\n			,OTnar as OTnar"//
                + "\n			,nacenka as nacenka"//
                + "\n		,mad.[KolichestvoOtkrytykhDogovorov] [KolichestvoOtkrytykhDogovorov]"//
                + "\n		,mad.[KolichestvoDogovorov] [KolichestvoDogovorov]"//
                + "\n		,(select max(BeginDate) from Vizits where Vizits.Client=k.Kod) as lastVizitDate"//
                + "\n		,(select Activity from Vizits where Vizits.Client=k.Kod and BeginDate"//
                + "\n			=(select max(BeginDate) from Vizits where Vizits.Client=k.Kod)) as lastVizitStatus"//
                + "\n		,(select max(EndTime) from Vizits where Vizits.Client=k.Kod) as lastVizitEnd"//
                + "\n		,0 as neMarshrut"//
                + "\n		,k.GeographicheskayaShirota as shirota"//
                + "\n		,k.GeographicheskayaDolgota as dolgota"//
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
                + "\n			,0 as OTden"//
                + "\n			,0 as OTnar"//
                + "\n			,0 as nacenka"//
                + "\n			,0 as KolichestvoOtkrytykhDogovorov"//
                + "\n			,0 as KolichestvoDogovorov"//
                + "\n			,0 as lastVizitDate"//
                + "\n			,0 as lastVizitStatus"//
                + "\n			,0 as lastVizitEnd"//
                + "\n			,1 as neMarshrut"//
                + "\n		,0 as shirota"//
                + "\n		,0 as dolgota"//
                + "\n	from kontragenty k"//
                + "\n	join (select golovnoykontragent"//
                + "\n		from MarshrutyAgentov m"//
                + "\n		join kontragenty k1 on k1._idrref=m.kontragent"//
                + "\n		) k2 on k._idrref=k2.golovnoykontragent"//
                + "\n	where k._idrref not in (select kontragent from MarshrutyAgentov group by kontragent)"//
                + "\n	group by k._idrref"//
                + sortDay//
                + limitSQL.value()//
                + "\n	";
        //System.out.println("Request_ClientsList " + req);
        return req;
    }
*/
	//public void requeryData() {
	//System.out.println("requeryData start");
	//requeryGridData();
	//}

	public void refreshGUI(){
		//System.out.println("refreshGUI gridOffset "+this.gridOffset.value()+"/"+lastGridOffset+"/"+this.dataGrid.currentPage);
		flipGrid();
		//System.out.println("after flip gridOffset "+this.gridOffset.value());
		//this.gridOffset.value(lastGridOffset+60);
		//System.out.println("before refresh gridOffset "+this.gridOffset.value()+"/"+this.dataGrid.currentPage);
		//refreshGridDayTitles();
		dataGrid.refresh();
		//System.out.println("after refresh gridOffset "+this.gridOffset.value()+"/"+this.dataGrid.currentPage);
		//dataGrid.currentPage=0;
		//dataGrid.flipNext();
		//System.out.println("after flipNext gridOffset "+this.gridOffset.value()+"/"+this.dataGrid.currentPage);
		//dataGrid.flipNext();
		//System.out.println("after flipNext gridOffset "+this.gridOffset.value()+"/"+this.dataGrid.currentPage);
		//System.out.println("refreshGUI done");
		String title = "";
		Cfg.currentIMEI(this, ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim());
		title = DateTimeHelper.UIDateString(LogHelper.getLastSuccessfulUpdate().getTime())//
				+ ":" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim()//
				//+ "/" + mAppInstance.getCurrentAgent().updateKod //
				+ "/" + ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieName()//
				//+ ", " + Requests.getTPCode(mDB//, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()
				//		)//
				//+ "/" + Requests.getTPfio(ApplicationHoreca.getInstance().getDataBase())//, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr())
				//+ "/" + Cfg.whoCheckListOwner()
				+ "/" + Cfg.polzovatelFIO(Cfg.whoCheckListOwner())
		;
		setTitle(title);
/*
		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		int offset=tz.getRawOffset();
		setTitle(title+": "+(offset/(60*60*1000)));
		*/
		//dataGrid.dump();
		//int vv=lastGridOffset;//+ gridPageSize * (dataGrid.maxPageCount - 1);
		//gridOffset.value(vv);
		//System.out.println("requery " + gridOffset.value());
		//System.out.println("after refreshGUI gridOffset "+this.gridOffset.value());

		//int more=dataGrid.rowHeight.property.value().intValue()*2;
		//System.out.println("scrollToOldPosition gridOffset "+this.gridOffset.value()+"/"+lastGridY+"/"+more+"/"+dataGrid.getScrollY());
		//dataGrid.scrollToOldPosition(lastGridY+more);
		//System.out.println("scroll Position  "+dataGrid.getScrollY());
		this.dataGrid.lockScroll = false;
	}

	/*
		boolean gpsPointsTooOld() {
			try {
				boolean noNew = false;
				String sql = "select count(_id) as cnt from GPSPoints where datetime(BeginTime)>datetime('now', '-2 minutes')";
				Cursor c = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
				if (c.moveToNext()) {
					int cnt = c.getInt(0);
					//System.out.println("gpsPointsTooOld found " + cnt + " points");
					if (cnt > 0) {
						noNew = false;
					} else {
						noNew = true;
					}
				}
				c.close();
				return noNew;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return false;
		}
	*/
	void tryGPS(){
		/*String curCode = Requests.getTPCode(ApplicationHoreca.getInstance().getDataBase());

		String curFIO = Requests.getTPfio(ApplicationHoreca.getInstance().getDataBase());

		String chOwner = sweetlife.horeca.supervisor.Cfg.whoCheckListOwner();
		if (chOwner.length() > 0) {
			String sql = "select l.naimenovanie as name,l.kod as kod from PhizLicaPolzovatelya f \n" +
					"join Polzovateli p on p._idrref=f.polzovatel join PhizicheskieLica l on l._idrref=f.phizlico \n" +
					"where trim(p.kod)='" + chOwner.trim() + "' order by f.period desc;"//
					;
			Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			curCode = data.child("row").child("kod").value.property.value();
			curFIO = data.child("row").child("name").value.property.value();
		}
		final String tpCode = curCode;
		final String tpFIO = curFIO;
		*/
		final String tpCode = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
		final String tpFIO = Cfg.polzovatelFIO(Cfg.whoCheckListOwner());
		final UploadTask task = new UploadTask(ApplicationHoreca.getInstance().getDataBase(), SystemHelper.getDiviceID(Activity_Route_2.this), getApplicationContext());
		System.out.println("tryGPS " + tpCode + ": " + tpFIO);
		new Expect().status.is("Выгрузка координат (" + tpFIO + ")...").task.is(new Task(){
			public void doTask(){
				try{
					task.UploadGPSPoints(tpCode);
					System.out.println("UploadGPSPoints " + task.mResultString);
					task.UploadVizits();
					System.out.println("UploadVizits " + task.mResultString);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			public void doTask(){
				System.out.println("tryGPS " + task.mResultString);
			}
		}).start(Activity_Route_2.this);
	}

	static Date lastTryGPSupload = new Date();

	@Override
	protected void onResume(){
		super.onResume();
		System.out.println("route resume start");
		/*
		if (gridScroll > 0) {
			int backOffset = gridOffset.value().intValue() + gridScroll;// -gridPageSize*1;
			System.out.println("onResume gridOffset " + this.gridOffset.value() + " backOffset " + backOffset);
			//if (backOffset < 0) {backOffset = 0;}
			gridOffset.value(backOffset);
		} else {
			//System.out.println("onResume gridScroll " + gridScroll);
			gridOffset.value(0);
		}
*/
		//dataGrid.dump();
		setupOtguzka();
		//System.out.println("onResume start");
		GPS.StartGPSLog(this, ApplicationHoreca.getInstance().getDataBase(), ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr());
		//System.out.println("StartGPSLog done");
        /*if (gpsPointsTooOld()) {
            hideGPSwarning.value(false);
        } else {
            hideGPSwarning.value(true);
        }*/
		hideGPSwarning.value(true);
		/*
		Calendar c = ApplicationHoreca.getInstance().getShippingDate();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		long t = c.getTime().getTime();
		otgruzkaNaDatu.value((double) t);
		*/
		//System.out.println("canRequery " + canRequery);
		//canRequery = true;
		//requery.start(this);
		//System.out.println("requery started");
		//System.out.println("IMEI ------------------------------ " + Cfg.hrc_imei());
		//reShowGrid();
		this.dataGrid.lockScroll = true;
		requery22222.start(this);

		Date now = new Date();
		long diff = now.getTime() - lastTryGPSupload.getTime();
		//System.out.println(lastTryGPSupload);
		//System.out.println(now);
		//System.out.println(now.getTime()+"/"+ lastTryGPSupload.getTime()+"/"+ diff);
		if(diff > 60 * 60 * 1000){
			lastTryGPSupload = new Date();
			tryGPS();
		}else{
			//System.out.println("skip upload GPS");
		}
		System.out.println("route resume done");
	}

	@Override
	protected void onPause(){
		System.out.println("onPause " + (this.dataGrid.scrollView.getScrollY() / Auxiliary.tapSize) + "+" + gridOffset.value());
		double newOffset = Math.floor(gridOffset.value() + this.dataGrid.scrollView.getScrollY() / Auxiliary.tapSize);
		//newOffset=newOffset+ dataGrid.extraRowScrollCount;
		if(newOffset < 0){
			newOffset = 0;
		}
		gridOffset.value(newOffset);
		//dataGrid.dump();
		//lastGridY=dataGrid.scrollView.getScrollY();
		//lastGridOffset=this.gridOffset.value().intValue();
		/*if (gridOffset.value() > 0) {
			gridScroll = this.dataGrid.scrollView.getScrollY() / this.dataGrid.rowHeight.property.value().intValue();
		} else {
			gridScroll = -this.dataGrid.scrollView.getScrollY() / this.dataGrid.rowHeight.property.value().intValue();
		}*/
		//System.out.println("onPause gridOffset/gridScroll/currentPage " + gridOffset.value() + "/" + gridScroll + "/" + this.dataGrid.currentPage);
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		GPS.StopAndUnbindServiceIfRequired(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed(){
		Auxiliary.pickConfirm(this, "Закрыть приложение?", "Закрыть", new Task(){
			@Override
			public void doTask(){
				Activity_Route_2.super.onBackPressed();
			}
		});
	}

	void promptTerritory(){
		//System.out.println("promptTerritory");
		final String[] ters = new String[Cfg.territory().children.size() + 1];
		ters[0] = "[все]";
		final Numeric nn = new Numeric().value(0);
		for(int i = 0; i < Cfg.territory().children.size(); i++){
			String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
					+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			ters[i + 1] = s;
		}
		System.out.println(Cfg.territory().dumpXML());
		Auxiliary.pickSingleChoice(Activity_Route_2.this, ters, nn, null, new Task(){
			@Override
			public void doTask(){
				String newName = ters[0];
				//ApplicationHoreca.getInstance().currentHRCmarshrut = "";
				if(nn.value() > 0){
					newName = Cfg.territory().children.get(nn.value().intValue() - 1).child("hrc").value.property.value().trim();
					//ApplicationHoreca.getInstance().currentHRCmarshrut = newName;
					//ApplicationHoreca.getInstance().currentIDmarshrut = Cfg.territory().children.get(nn.value().intValue() - 1).child("polzovatelID").value.property.value().trim();
					//ApplicationHoreca.getInstance().currentKodPodrazdelenia = Cfg.territory().children.get(nn.value().intValue() - 1).child("kod").value.property.value().trim();
					//Cfg.setKodPodrazdelenia(Cfg.territory().children.get(nn.value().intValue() - 1).child("kod").value.property.value().trim());
					Cfg.resetHRCmarshrut(
							newName
							, Cfg.territory().children.get(nn.value().intValue() - 1).child("polzovatelID").value.property.value().trim()
							, Cfg.territory().children.get(nn.value().intValue() - 1).child("kod").value.property.value().trim()
					);
					/*
					String sql = "select otd.EdinicaPlanirovaniya as info from Polzovateli usr" //
							+ " join Podrazdeleniya otd on otd._idrref=usr.Podrazdelenie"//
							//+ " where usr._idrref=x'" + ApplicationHoreca.getInstance().currentIDmarshrut + "';";
							+ " where usr._idrref=x'" + Cfg.selectedHRC_idrref() + "';";
					Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					System.out.println(b.dumpXML());
					newName = newName + ": " + b.child("row").child("info").value.property.value();
					*/
				}else{
					newName = "[все]";
					//ApplicationHoreca.getInstance().currentHRCmarshrut = "";
					//ApplicationHoreca.getInstance().currentIDmarshrut = "";
					//ApplicationHoreca.getInstance().currentKodPodrazdelenia = "";
					//Cfg.setKodPodrazdelenia("");
					Cfg.resetHRCmarshrut("", "", "");
				}
				//System.out.println("newName " + newName);
				//currentHRCName = newName;
				currentHRCName.value(newName);
				//setTerritoryButton();
				/*boolean filter = false;
				if (mCheckFilterByDate != null) {
					filter = mCheckFilterByDate.isChecked();
				}*/
				//FilterListByDate(filter);
				//requery.start(Activity_Route_2.this);
				//canRequery=true;
				resetGrid();
			}
		}, null, null, null, null);
	}
/*
	void __reShowGrid() {
		try {
			Expect expect = new Expect().status.is("Подождите...").task.is(new Task() {
				@Override
				public void doTask() {
					//int vv=lastGridOffset+ gridPageSize * (dataGrid.maxPageCount - 1);
					//gridOffset.value(vv);
					requeryGridData();
				}
			})//
					.afterDone.is(new Task() {
						@Override
						public void doTask() {

							refreshGUI();
							if (gridScroll < 0) {
								dataGrid.scrollView.scrollTo(0, -gridScroll * dataGrid.rowHeight.property.value().intValue());
							}
						}
					});
			expect.start(this);
		} catch (Throwable t) {
			t.printStackTrace();
			Auxiliary.warn(t.getMessage(), this);
		}
	}*/
}
