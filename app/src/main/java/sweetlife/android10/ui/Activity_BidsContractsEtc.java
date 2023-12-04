package sweetlife.android10.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.TimeZone;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.GPS;
import sweetlife.android10.Settings;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.fixedprices.FixedPricesListAdapter;
import sweetlife.android10.data.fixedprices.ZayavkaNaSkidki;
import sweetlife.android10.data.nomenclature.NomenclatureSavedData;
import sweetlife.android10.data.orders.BidsListAdapter;
import sweetlife.android10.data.orders.ZayavkaPokupatelya;
import sweetlife.android10.data.returns.ReturnsListAdapter;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat_Tovary;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.database.Request_FixedPrices;
import sweetlife.android10.database.Request_Returns;
import sweetlife.android10.database.Requests;
import sweetlife.android10.gps.GPSInfo;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.update.UpdateTask;
//import sweetlife.horeca.reports.KontragentInfo;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.utils.UIHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;
import sweetlife.android10.utils.UIHelper.IMessageBoxCallbackInteger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableLayout.LayoutParams;

import sweetlife.android10.R;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class Activity_BidsContractsEtc extends Activity_Base implements IDialogTaskAction {

	static String checkTerriKod = null;
	private final String IS_NEED_REQUERY_TEMP_TABLES = "IsNeedRequeryTempTables";
	private final int IDD_END_VISIT = 202;
	//private Spinner mSpinnerVizitsResults;
	AutoCompleteTextView autoCompleteTextViewVizit;
	//Bough kontragentData;
	DataGrid specificaciaGrid;
	ColumnText specificaciaStatus;
	ColumnText specificaciaDate;
	ColumnText specificaciaFrom;
	ColumnText specificaciaTo;
	ColumnText specificaciaComment;
	DataGrid degustaciaGrid;
	ColumnText degustaciaStatus;
	ColumnText degustaciaData;
	ColumnText degustaciaKomment;
	MenuItem menuPechati;
	MenuItem menuOtchety;
	//MenuItem menuChekList;
	MenuItem menuKartaKlienta;
	MenuItem menuObnoIstoria;
	MenuItem menuShoMap;
	MenuItem menuRequestMailList;
	private ApplicationHoreca mAppInstance;
	private BidsListAdapter mBidsListAdapter;
	private ListView mBidsList;
	private ReturnsListAdapter mReturnsListAdapter;
	private ListView mReturnsList;
	private FixedPricesListAdapter mFixedPricesListAdapter;
	private ListView mFixedPricesList;
	private TextView mAmount;
	private ArrayList<String> mVizitsResults;
	//public static ClientInfo mClient;
	private boolean mIsEditable;
	private Calendar mChosedDay;
	private double mAvailableAmount;
	private boolean mIsNeedRequeryTempTables = true;
	private GPSInfo mGPSInfo;
	OnClickListener mBtnBeginVizit = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.debug("mBtnBeginVizit");
			//ApplicationHoreca.getInstance().StartGPSLog();
			if (!mGPSInfo.estDalnieVizity(mAppInstance.getClientInfo())) {
				long distanceToClient = GPSInfo.isTPNearClient(mAppInstance.getClientInfo().getLat(), mAppInstance.getClientInfo().getLon());
				if (distanceToClient == GPSInfo.GPS_NOT_AVAILABLE) {
					CreateErrorDialog("GPS данные недоступны. Невозможно начать визит."//R.string.gps_vizit_not_available
					).show();
				} else {
					final String timeString = mGPSInfo.getVizitTimeString();
					if (distanceToClient > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()) {
						String warning = "Удаление от контрагента " + distanceToClient + " метров. Начать визит?";
						if (mAppInstance.getClientInfo().getLat() == 0) {
							warning = "У клиента не зафиксированы координаты. Начать визит?";
						}
						UIHelper.MsgBox(getString(R.string.confirm), warning, Activity_BidsContractsEtc.this, new IMessageBoxCallbackInteger() {
							@Override
							public void MessageBoxResult(int which) {
								if (!mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod())) {
									UIHelper.MsgBox(getString(R.string.confirm), "Сегодня уже был визит. Начать визит заново?", Activity_BidsContractsEtc.this, new IMessageBoxCallbackInteger() {
										@Override
										public void MessageBoxResult(int which) {
											mGPSInfo.BeginVizit(mAppInstance.getClientInfo().getKod());
											UIHelper.quickWarning(getString(R.string.visit_begin) + ": " + timeString, Activity_BidsContractsEtc.this);
											//Toast.makeText(getApplicationContext(), getString(R.string.visit_begin), Toast.LENGTH_SHORT).show();
										}
									}, null);
								} else {
									mGPSInfo.BeginVizit(mAppInstance.getClientInfo().getKod());
									UIHelper.quickWarning(getString(R.string.visit_begin) + ": " + timeString, Activity_BidsContractsEtc.this);
									//Toast.makeText(getApplicationContext(), getString(R.string.visit_begin), Toast.LENGTH_SHORT).show();
								}
							}
						}, null);
					} else {
						if (!mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod())) {
							UIHelper.MsgBox(getString(R.string.confirm), "Сегодня уже был визит. Начать визит заново?", Activity_BidsContractsEtc.this, new IMessageBoxCallbackInteger() {
								@Override
								public void MessageBoxResult(int which) {
									mGPSInfo.BeginVizit(mAppInstance.getClientInfo().getKod());
									UIHelper.quickWarning(getString(R.string.visit_begin) + ": " + timeString, Activity_BidsContractsEtc.this);
									//Toast.makeText(getApplicationContext(), getString(R.string.visit_begin), Toast.LENGTH_SHORT).show();
								}
							}, null);
						} else {
							mGPSInfo.BeginVizit(mAppInstance.getClientInfo().getKod());
							UIHelper.quickWarning(getString(R.string.visit_begin) + ": " + timeString, Activity_BidsContractsEtc.this);
							//Toast.makeText(getApplicationContext(), getString(R.string.visit_begin), Toast.LENGTH_SHORT).show();
						}
					}
				}
			} else {
				UIHelper.quickWarning("Сначала нужно закончить предыдущий визит " + mGPSInfo.kontragentyVizitov(mAppInstance.getClientInfo()), Activity_BidsContractsEtc.this);
				//Toast.makeText(getApplicationContext(), "Сначала нужно закончить предыдущий визит.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private boolean mWantExit = false;

	//boolean tempFlag = true;
	//Task specificaciaTap;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_bids_contracts_etc);
		mAppInstance = ApplicationHoreca.getInstance();
		mGPSInfo = GPS.getGPSInfo();
		if (savedInstanceState != null) {
			mIsNeedRequeryTempTables = savedInstanceState.getBoolean(IS_NEED_REQUERY_TEMP_TABLES, true);
		}
		ReadExtras();
		InitializeTabHost();
		ConstructButtons();
		ConstructBidsList();
		ConstructFixedPricesList();
		ConstructReturnsList();
		//UpdateTablesAfterClientChoose.getDolgiPoDocumentam();
		String neMarshrut = "";
		if (mAppInstance.getClientInfo().neMarshrut) {
			neMarshrut = "(не в маршруте)";
		}
		setTitle(neMarshrut + mAppInstance.getClientInfo().getName() + " " + mAppInstance.getClientInfo().dolgMessage);
		LogHelper.debug("Client " + mAppInstance.getClientInfo().getName());
	}

	@Override
	protected void onDestroy() {
		//
		LogHelper.debug(this.getClass().getCanonicalName() + ".onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".onSaveInstanceState");
		outState.putBoolean(IS_NEED_REQUERY_TEMP_TABLES, mIsNeedRequeryTempTables);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogHelper.debug(this.getClass().getCanonicalName() + ".onResume, mIsNeedRequeryTempTables is " + mIsNeedRequeryTempTables);
		//if (mIsEditable && mIsNeedRequeryTempTables) {
		//DialogTask task = new DialogTask(getString(R.string.msg_prepare_kontagent), getApplicationContext(), this, this);
		//sweetlife.horeca.utils.ManagedAsyncTask.ITaskListener k=new sweetlife.horeca.utils.ManagedAsyncTask.ITaskListener();
		//task.setListener(new ManagedAsyncTask.ITaskListener(){});
		//AsyncTaskManager.getInstance().executeTask(this, task);
		//mIsNeedRequeryTempTables = false;
		//}
		//setTitle(mAppInstance.getClientInfo().getName()+" (долги по накладным: "+mAppInstance.getClientInfo().dolgMessage+")");
		requerySpecificaciaGrid();
		requeryDegustaciaGrid();
	}

	public void resetTitle() {
		setTitle(mAppInstance.getClientInfo().getName() + " (долги по накладным: " + mAppInstance.getClientInfo().dolgMessage + ")");
	}

	private void ReadExtras() {
		Bundle extras = getIntent().getExtras();
		mAppInstance.setClientInfo(new ClientInfo(mDB, extras.getString(CLIENT_ID)));
		mChosedDay = Calendar.getInstance();
		mChosedDay.setTimeInMillis(extras.getLong(CHOOSED_DAY, mChosedDay.getTimeInMillis()));
		mIsEditable = DateTimeHelper.getOnlyDateInfo(mChosedDay)//
				.compareTo(DateTimeHelper.getOnlyDateInfo(Calendar.getInstance()))//
				== 0
				? true
				: false;
		if (mIsEditable) {
			if (Requests.IsSyncronizationDateLater(-5)) {
				//LogHelper.debug("Requests.IsSyncronizationDateLater( -5 ) true");
				findViewById(R.id.layout_add).setVisibility(View.GONE);
				findViewById(R.id.secondtab).setVisibility(View.GONE);
				CreateErrorDialog(R.string.msg_sync_date_later5).show();
				mIsEditable = false;
			} else {
				//LogHelper.debug("Requests.IsSyncronizationDateLater( -5 ) false");
			}
		} else {
			UIHelper.quickWarning("Заявки на " + DateTimeHelper.UIDateString(mChosedDay.getTime()), this);
		}
	}

	private void InitializeTabHost() {
		TabHost tabHost = (TabHost) findViewById(R.id.tab_host);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("tab_orders").setIndicator(makeTabIndicator(getString(R.string.orders))).setContent(R.id.firsttab));
		if (mIsEditable) {
			tabHost.addTab(tabHost.newTabSpec("tab_contracts").setIndicator(makeTabIndicator(getString(R.string.available_amount))).setContent(R.id.secondtab));
		}
		tabHost.addTab(tabHost.newTabSpec("tab_fixed_prices").setIndicator(makeTabIndicator(getString(R.string.fixed_prices))).setContent(R.id.thirdtab));
		tabHost.addTab(tabHost.newTabSpec("tab_returns").setIndicator(makeTabIndicator(getString(R.string.returns_for_clients))).setContent(R.id.fourthtab));
		//tabHost.addTab(tabHost.newTabSpec("tab_degustacia").setIndicator(makeTabIndicator("Дегустация")).setContent(R.id.fourthtab));
		InitializeTabHostSpesifikaciiTab(tabHost);
		InitializeTabHostDegustaciaTab(tabHost);
		tabHost.setCurrentTab(0);
	}

	void InitializeTabHostDegustaciaTab(TabHost tabHost) {
		degustaciaStatus = new ColumnText();
		degustaciaData = new ColumnText();
		degustaciaKomment = new ColumnText();
		degustaciaStatus.title.is("").width.is(200);
		final int w = Auxiliary.screenWidth(this);
		TabHost.TabSpec tab_degustacia = tabHost.newTabSpec("tab_degustacia");
		tab_degustacia.setIndicator(makeTabIndicator("Дегустация"));
		tab_degustacia.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				//System.out.println("createTabContent " + tag);
				Layoutless specLayoutless = new Layoutless(Activity_BidsContractsEtc.this);
				degustaciaGrid = new DataGrid(Activity_BidsContractsEtc.this);
				specLayoutless.child(new Decor(Activity_BidsContractsEtc.this)//
						.sketch(new SketchPlate()//
								.tint(new TintLinearGradient()//
										.fromColor.is(0xff768594)//
										.toColor.is(0xffdddede)//
										.fromX.is(0)//
										.fromY.is(0)//
										.toX.is(0)//
										.toY.is(38)//
								)//
								.width.is(w)//
								.height.is(38)//
						)//
						.background.is(0xff00ff00)//
						.width().is(w)//
						.height().is(38)//
				);
				specLayoutless.child(degustaciaGrid//
						.headerHeight.is(38)//
						.columns(new Column[]{//
								degustaciaStatus.title.is("Выгружена").width.is(w * 0.1)//
								, degustaciaData.title.is("Дата отгр.").width.is(w * 0.1)//
								, degustaciaKomment.title.is("Комментарий").width.is(w * 0.8) //
						})//
						.width().is(specLayoutless.width().property)//
						.height().is(specLayoutless.height().property.minus(0.8 * Auxiliary.tapSize))//
				);
				specLayoutless.child(new Knob(Activity_BidsContractsEtc.this)//
						.afterTap.is(new Task() {
							@Override
							public void doTask() {
								dobavitDegustaciu();
							}
						})//
						.labelText.is("Добавить")//
						.left().is(specLayoutless.width().property.minus(250))//
						.top().is(specLayoutless.height().property.minus(0.8 * Auxiliary.tapSize))//
						.width().is(250)//
						.height().is(0.8 * Auxiliary.tapSize)//
				);
				requeryDegustaciaGrid();
				return specLayoutless;
			}
		});
		tabHost.addTab(tab_degustacia);
	}

	void InitializeTabHostSpesifikaciiTab(TabHost tabHost) {
		/*
		kontragentData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(//
				"select"//
						+ "\n 		Kontragenty._idrref as _IDRRef"//
						+ "\n		,Kontragenty.kod as Kod"//
						+ "\n		,Kontragenty.Naimenovanie as Naimenovanie"//
						+ "\n	from MarshrutyAgentov"//
						+ "\n		join Kontragenty on Kontragenty._idrref=MarshrutyAgentov.Kontragent"//
						+ "\n	group by Kontragenty.kod"//
						+ "\n	order by Kontragenty.Naimenovanie"//
						+ "\n	limit 999;", null));
		final RedactSingleChoice kontragents = new RedactSingleChoice(this);
		kontragents.setTextSize(16);
		kontragents.item("[Все контрагенты]");
		for (int i = 0; i < kontragentData.children.size(); i++) {
			Bough row = kontragentData.children.get(i);
			kontragents.item(row.child("Naimenovanie").value.property.value());
		}
		*/
		specificaciaStatus = new ColumnText();
		specificaciaDate = new ColumnText();
		specificaciaFrom = new ColumnText();
		specificaciaTo = new ColumnText();
		specificaciaComment = new ColumnText();
		specificaciaStatus.title.is("").width.is(200);
		final int w = Auxiliary.screenWidth(this);
		TabHost.TabSpec tab_specifikacii = tabHost.newTabSpec("tab_specifikacii");
		tab_specifikacii.setIndicator(makeTabIndicator("Спецификации"));
		tab_specifikacii.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				//System.out.println("createTabContent " + tag);
				//Layoutless.fillBaseColors(Activity_BidsContractsEtc.this);
				Layoutless specLayoutless = new Layoutless(Activity_BidsContractsEtc.this);
				/*specLayoutless.child(kontragents//
						.width().is(500)//
						.height().is(0.8 * Layoutless.tapSize)//
						);*/
				/*specLayoutless.child(new RedactText(Activity_BidsContractsEtc.this)//
						.text.is("")//
								//.left().is(500 + 8)//
								.width().is(specLayoutless.width().property.minus(250))//
								.height().is(0.8 * Layoutless.tapSize)//
						);*/
				specificaciaGrid = new DataGrid(Activity_BidsContractsEtc.this);
				specLayoutless.child(new Decor(Activity_BidsContractsEtc.this)//
						.sketch(new SketchPlate()//
								.tint(new TintLinearGradient()//
										.fromColor
										//.is(0xffff0000)
										.is(0xff768594)//
										.toColor.is(0xffdddede)//
										//.is(0xff0000ff)//
										.fromX.is(0)//
										.fromY.is(0)//
										.toX.is(0)//
										.toY.is(38)//
								)//
								.width.is(w)//
								.height.is(38)//
						)//
						.background.is(0xff00ff00)//
						.width().is(w)//
						.height().is(38)//
				);
				specLayoutless.child(specificaciaGrid//
						.headerHeight.is(38)//
						.columns(new Column[]{//
								specificaciaStatus.title.is("Выгружена").width.is(w * 0.1)//
								, specificaciaDate.title.is("Дата").width.is(w * 0.1)//
								, specificaciaFrom.title.is("Время действия начало").width.is(w * 0.1)//
								, specificaciaTo.title.is("Время действия конец").width.is(w * 0.1)//
								, specificaciaComment.title.is("Комментарий").width.is(w * 0.6) //
						})//
						//.top().is(0.8 * Layoutless.tapSize)//
						.width().is(specLayoutless.width().property)//
						.height().is(specLayoutless.height().property.minus(0.8 * Auxiliary.tapSize))//
				);
				/*specLayoutless.child(new Knob(Activity_BidsContractsEtc.this)//
						.labelText.is("Найти")//
								.left().is(specLayoutless.width().property.minus(250))//
								.width().is(250)//
								.height().is(0.8 * Layoutless.tapSize)//
						);*/
				specLayoutless.child(new Knob(Activity_BidsContractsEtc.this)//
						.afterTap.is(new Task() {
							@Override
							public void doTask() {
								dobavitSpecificaciu();
							}
						})//
						.labelText.is("Добавить")//
						.left().is(specLayoutless.width().property.minus(250))//
						.top().is(specLayoutless.height().property.minus(0.8 * Auxiliary.tapSize))//
						.width().is(250)//
						.height().is(0.8 * Auxiliary.tapSize)//
				);
				//Button v = new Button(Activity_BidsContractsEtc.this);
				//System.out.println("createTabContent " + specLayoutless.getWidth());
				requerySpecificaciaGrid();
				return specLayoutless;
			}
		});
		tabHost.addTab(tab_specifikacii);
	}

	void requeryDegustaciaGrid() {
		LogHelper.debug(this.getClass().getCanonicalName() + ".requeryDegustaciaGrid");
		if (degustaciaGrid == null) {
			return;
		}
		String sql = "select _id,otgruzka,comment,kontragent,status"//
				+ " from ZayavkaNaDegustaciu "//
				+ " where kontragent=" + ApplicationHoreca.getInstance().getClientInfo().getID().trim()//
				+ " order by status,otgruzka desc"//
				+ " limit 50";
		//System.out.println("requeryDegustaciaGrid " + sql);
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		degustaciaStatus.clear();
		degustaciaData.clear();
		degustaciaKomment.clear();
		for (int i = 0; i < b.children.size(); i++) {
			Bough row = b.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					doEditDegustaciu(_id);
				}
			};
			int statusNum = (int) Numeric.string2double(row.child("status").value.property.value());
			String statusText = "Не выгружен";
			if (statusNum == 1) {
				statusText = "Выгружен";
			}
			degustaciaStatus.cell(statusText, tap);
			degustaciaData.cell(row.child("otgruzka").value.property.value(), tap);
			degustaciaKomment.cell(row.child("comment").value.property.value(), tap);
		}
		LogHelper.debug(this.getClass().getCanonicalName() + ".requeryDegustaciaGrid executed");
		degustaciaGrid.refresh();
		LogHelper.debug(this.getClass().getCanonicalName() + ".requeryDegustaciaGrid end");
	}

	void requerySpecificaciaGrid() {
		LogHelper.debug(this.getClass().getCanonicalName() + ".requerySpecificaciaGrid");
		if (specificaciaGrid == null) {
			return;
		}
		String sql = "select _id,createDate,fromDate,toDate,comment,hrc,kod,status"//
				+ " from ZayavkaNaSpecifikasia"//
				+ " where kod='" + ApplicationHoreca.getInstance().getClientInfo().getKod().trim() + "'"//
				+ " order by toDate desc,fromDate desc"//
				+ " limit 50";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		specificaciaStatus.clear();
		specificaciaDate.clear();
		specificaciaFrom.clear();
		specificaciaTo.clear();
		specificaciaComment.clear();
		for (int i = 0; i < b.children.size(); i++) {
			Bough row = b.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {

					doEditSpecificacia(_id);
				}
			};
			int statusNum = (int) Numeric.string2double(row.child("status").value.property.value());
			String statusText = "Не выгружен";
			if (statusNum == 1) {
				statusText = "Выгружен";
			}
			specificaciaStatus.cell(statusText, tap);
			specificaciaDate.cell(row.child("createDate").value.property.value(), tap);
			specificaciaFrom.cell(row.child("fromDate").value.property.value(), tap);
			specificaciaTo.cell(row.child("toDate").value.property.value(), tap);
			specificaciaComment.cell(row.child("comment").value.property.value(), tap);
		}
		LogHelper.debug(this.getClass().getCanonicalName() + ".requerySpecificaciaGrid executed");
		specificaciaGrid.refresh();
		LogHelper.debug(this.getClass().getCanonicalName() + ".requerySpecificaciaGrid end");
	}

	void doEditSpecificacia(String _id) {
		Intent intent = new Intent();
		intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidSpecificaciya.class);
		startActivity(intent);
	}

	void doEditDegustaciu(String _id) {
		Intent intent = new Intent();
		intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidDegustacia.class);
		startActivity(intent);
	}

	void dobavitDegustaciu() {
		Intent intent = new Intent();
		//intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidDegustacia.class);
		startActivity(intent);
	}

	void dobavitSpecificaciu() {
		//System.out.println("new Specificacia");
		Intent intent = new Intent();
		intent.setClass(this, Activity_BidSpecificaciya.class);
		startActivity(intent);
	}

	@Override
	public void update(Observable observable, Object data) {
		ConstructContractsLists();
	}

	@Override
	public int onAction() {
		//UpdateTablesAfterClientChoose.UpdateAll(mDB, mAppInstance.getClientInfo().getID(), DateTimeHelper.SQLDateString(mAppInstance.getShippingDate().getTime()));
		//setTitle(mAppInstance.getClientInfo().getName()+" (долги по накладным: "+mAppInstance.getClientInfo().dolgMessage+")");
		//super.onAction();
		return 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*
			if (mGPSInfo.IsVizitBegin(mAppInstance.getClientInfo().getKod()) && mIsEditable) {
				String msg=getString(R.string.vizit_not_complete);
				UIHelper.MsgBox(getString(R.string.confirm),msg , this, getString(R.string.yes), getString(R.string.no), new IMessageBoxCallbackInteger() {
					@SuppressWarnings("deprecation")
					public void MessageBoxResult(int which) {
						mWantExit = true;
						showDialog(IDD_END_VISIT);
					}
				}, new IMessageBoxCallbackInteger() {
					public void MessageBoxResult(int which) {
						finish();
					}
				});
				return true;
			}
			*/
		}
		return super.onKeyDown(keyCode, event);
	}

	private void ConstructBidsList() {
		((Button) findViewById(R.id.btn_add)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				StartBidsActivity(ORDER_UPDATE, null, mAvailableAmount - Request_Bids.getBidsAmount(mDB, DateTimeHelper.SQLDateString(Calendar.getInstance().getTime())));
			}
		});
		mBidsListAdapter = new BidsListAdapter(this, Request_Bids.Request(mDB, mAppInstance.getClientInfo().getID(), DateTimeHelper.SQLDateString(Calendar.getInstance().getTime())));
		mBidsList = (ListView) findViewById(R.id.list_bids);
		mBidsList.setAdapter(mBidsListAdapter);
		mBidsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = mBidsListAdapter.getCursor();
				cursor.moveToPosition(position);
				ZayavkaPokupatelya bid = new ZayavkaPokupatelya(//
						Request_Bids.get_id(cursor)//
						, Request_Bids.getIDRRef(cursor)//
						, Request_Bids.getData(cursor)//
						, Request_Bids.getNomer(cursor)//
						, Request_Bids.isProveden(cursor)//
						, Request_Bids.getDataOtgruzki(cursor)//
						, Request_Bids.getDogovorKontragenta(cursor)//
						, Request_Bids.getKommentariy(cursor)//
						, Request_Bids.getKontragentID(cursor)//
						, Request_Bids.getKontragentKod(cursor)//
						, Request_Bids.getKontragentNaimanovanie(cursor)//
						, Request_Bids.getSumma(cursor)//
						, Request_Bids.getTipOplaty(cursor)//
						, Request_Bids.getTipOplatyPoryadok(cursor)//
						, Request_Bids.getSebestoimost(cursor)//
						, false);
				double amount = mAvailableAmount + bid.getSumma() - Request_Bids.getBidsAmount(mDB, DateTimeHelper.SQLDateString(Calendar.getInstance().getTime()));
				StartBidsActivity(ORDER_UPDATE, bid, amount);
			}
		});
		//registerForContextMenu(mBidsList);
	}

	private void StartBidsActivity(int requestCode, ZayavkaPokupatelya bid, double amount) {
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc.this, Activity_Bid.class);
		intent.putExtra(CLIENT_ID, mAppInstance.getClientInfo().getID());
		intent.putExtra(AVAILABLE_AMOUNT, mAvailableAmount - Request_Bids.getBidsAmount(mDB, DateTimeHelper.SQLDateString(Calendar.getInstance().getTime())));
		if (bid != null) {
			intent.putExtra(BID, bid);
			if (!mIsEditable || bid.isProveden()) {
				intent.putExtra(IS_EDITABLE, false);
			}
		}
		startActivityForResult(intent, requestCode);
	}

	private void ConstructFixedPricesList() {
		((Button) findViewById(R.id.btn_add_fixed_price)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				StartFixedPricesActivity(true, null);
			}
		});
		mFixedPricesListAdapter = new FixedPricesListAdapter(this, Request_FixedPrices.Request(mDB, mAppInstance.getClientInfo().getID()));
		mFixedPricesList = (ListView) findViewById(R.id.list_fixed_prices);
		mFixedPricesList.setAdapter(mFixedPricesListAdapter);
		mFixedPricesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = mFixedPricesListAdapter.getCursor();
				cursor.moveToPosition(position);
				ZayavkaNaSkidki bid = new ZayavkaNaSkidki(Request_FixedPrices.get_id(cursor)
						, Request_FixedPrices.getIDRRef(cursor)
						, Request_FixedPrices.getData(cursor)
						, Request_FixedPrices.getNomer(cursor)
						, mAppInstance.getClientInfo().getID()
						, mAppInstance.getClientInfo().getKod()
						, mAppInstance.getClientInfo().getName()
						, Request_FixedPrices.getVremyaNachalaSkidkiPhiksCen(cursor)
						, Request_FixedPrices.getVremyaOkonchaniyaSkidkiPhiksCen(cursor)
						, Request_FixedPrices.getKommentariy(cursor)
						, Request_FixedPrices.isUploaded(cursor)
						, false);
				StartFixedPricesActivity(!Request_FixedPrices.isUploaded(cursor), bid);
			}
		});
	}

	private void ConstructReturnsList() {
		((Button) findViewById(R.id.btn_add_returns)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				StartReturnsActivity(true, null);
			}
		});
		mReturnsListAdapter = new ReturnsListAdapter(this, Request_Returns.Request(mDB, mAppInstance.getClientInfo().getID()));
		mReturnsList = (ListView) findViewById(R.id.list_returns);
		mReturnsList.setAdapter(mReturnsListAdapter);
		mReturnsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = mReturnsListAdapter.getCursor();
				cursor.moveToPosition(position);
				ZayavkaNaVozvrat bid = new ZayavkaNaVozvrat(Request_Returns.get_id(cursor)
						, Request_Returns.getIDRRef(cursor)
						, Request_Returns.getData(cursor)
						, Request_Returns.getNomer(cursor)
						, mAppInstance.getClientInfo().getID()
						, mAppInstance.getClientInfo().getKod()
						, mAppInstance.getClientInfo().getName()
						, Request_Returns.getDataOtgruzki(cursor)
						, Request_Returns.getAktPretenziyPath(cursor)
						, Request_Returns.isUploaded(cursor)
						, false, Request_Returns.getVersion(cursor));
				StartReturnsActivity(!Request_Returns.isUploaded(cursor), bid);
			}
		});
	}

	private void StartReturnsActivity(final boolean isEditable, ZayavkaNaVozvrat bid) {
		if (bid == null) {
			final Numeric defaultSelection = new Numeric();
			Auxiliary.pickSingleChoice(this, ZayavkaNaVozvrat_Tovary.ZayavkaNaVozvratPrichina, defaultSelection, "Причина", new Task() {
				@Override
				public void doTask() {
					if (defaultSelection.value() > 0) {
						Intent intent = new Intent();
						intent.setClass(Activity_BidsContractsEtc.this, Activity_Returns.class);
						intent.putExtra(CLIENT_ID, mAppInstance.getClientInfo().getID());
						intent.putExtra(IS_EDITABLE, isEditable);
						intent.putExtra("prichina", defaultSelection.value().intValue());
						startActivityForResult(intent, RETURNS_ADD);
					}
				}
			}, null, null, null, null);
		} else {
			Intent intent = new Intent();
			intent.setClass(Activity_BidsContractsEtc.this, Activity_Returns.class);
			intent.putExtra(CLIENT_ID, mAppInstance.getClientInfo().getID());
			intent.putExtra(IS_EDITABLE, isEditable);
			intent.putExtra(RETURNS_BID, bid);
			startActivityForResult(intent, RETURNS_ADD);
		}
	}

	private void StartFixedPricesActivity(boolean isEditable, ZayavkaNaSkidki bid) {
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc.this, Activity_FixedPrices.class);
		intent.putExtra(CLIENT_ID, mAppInstance.getClientInfo().getID());
		intent.putExtra(IS_EDITABLE, isEditable);
		if (bid != null) {
			intent.putExtra(FIXED_PRICES_BID, bid);
		}
		startActivityForResult(intent, FIXED_PRICES_ADD);
	}

	private void ConstructButtons() {
		if (!mIsEditable) {
			findViewById(R.id.layout_add).setVisibility(View.GONE);
			findViewById(R.id.secondtab).setVisibility(View.GONE);
			((Button) findViewById(R.id.btn_beginvisit)).setVisibility(View.GONE);
			((Button) findViewById(R.id.btn_endvisit)).setVisibility(View.GONE);
			((Button) findViewById(R.id.btn_add_returns)).setVisibility(View.GONE);
			((Button) findViewById(R.id.btn_add_fixed_price)).setVisibility(View.GONE);
			return;
		} else {
			if (mAppInstance.getClientInfo().neMarshrut) {
				((Button) findViewById(R.id.btn_beginvisit)).setVisibility(View.GONE);
				((Button) findViewById(R.id.btn_endvisit)).setVisibility(View.GONE);
				((Button) findViewById(R.id.btn_add_returns)).setVisibility(View.GONE);
				((Button) findViewById(R.id.btn_add)).setVisibility(View.GONE);
			}
			((Button) findViewById(R.id.btn_add)).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					StartBidsActivity(ORDER_UPDATE, null, mAvailableAmount - Request_Bids.getBidsAmount(mDB, DateTimeHelper.SQLDateString(Calendar.getInstance().getTime())));
				}
			});
			((Button) findViewById(R.id.btn_beginvisit)).setOnClickListener(mBtnBeginVizit);
			((Button) findViewById(R.id.btn_endvisit)).setOnClickListener(new View.OnClickListener() {
				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					if (mojnoZakrytVizit()) {
						showDialog(IDD_END_VISIT);
					}
					//ApplicationHoreca.getInstance().StartGPSLog();
					/*if (mGPSInfo.IsVizitBegin(mAppInstance.getClientInfo().getKod())) {
						showDialog(IDD_END_VISIT);
					}
					else {
						UIHelper.quickWarning("Сначала нужно визит нужно начать.", Activity_BidsContractsEtc.this);
						//Toast.makeText(getApplicationContext(), "Сначала нужно визит нужно начать.", Toast.LENGTH_SHORT).show();
					}*/
				}
			});
			mVizitsResults = GPSInfo.getVisitsResultsList(mDB);
		}
	}

	boolean mojnoZakrytVizit() {
		long duration = 0;
		int coarse = 0;
		String sql = "select BeginTime from Vizits where EndTime is null and Client = " + mAppInstance.getClientInfo().getKod();
		//mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod());
		Cursor cursor = mDB.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			//DateFormat df;
			SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			mDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				java.util.Date beginDate = mDateTimeFormat.parse(cursor.getString(0));
				java.util.Date now = new java.util.Date();
				duration = now.getTime() - beginDate.getTime();
				//System.out.println(beginDate);
				//System.out.println(now);
				//System.out.println(duration);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		cursor.close();
		//if (duration < 1000 * 60 * 15) {
		if (duration < 1000 * 60 * 5) {
			coarse = (int) (duration / (1000.0 * 60));
			Auxiliary.warn("Визит должен длиться не меньше 5 мин., прошло только " + coarse + " мин.", Activity_BidsContractsEtc.this);
			return false;
		}
		long distanceToClient = GPSInfo.isTPNearClient(mAppInstance.getClientInfo().getLat(), mAppInstance.getClientInfo().getLon());
		if (distanceToClient == GPSInfo.GPS_NOT_AVAILABLE) {
			Auxiliary.warn("GPS данные недоступны.", Activity_BidsContractsEtc.this);
			return false;
		}
		if (distanceToClient > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()) {
			Auxiliary.warn("Удаление от контрагента " + distanceToClient + " метров.", Activity_BidsContractsEtc.this);
			return false;
		}
		return true;
	}

	boolean netOtkrytyhUdalennyhVizitov() {
		//mGPSInfo.IsVizitBegin(mAppInstance.getClientInfo().getKod());
		double lat = mAppInstance.getClientInfo().getLat();
		double lon = mAppInstance.getClientInfo().getLon();
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		//
		menu.add(Menu.NONE, IDM_LIST_DELETE, Menu.NONE, R.string.delete);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
			case IDM_LIST_DELETE:
				//System.out.println("delete record?");
				Auxiliary.pick3Choice(this, "Удаление заказа", "Вы уверены?", "Удалить", new Task() {
					@Override
					public void doTask() {
						try {
							int n = mBidsList.getSelectedItemPosition();
							long id = mBidsListAdapter.getItemId(n);
							//System.out.println("delete record..." + id + "/" + n);
							mBidsListAdapter.getCursor().requery();
							mBidsListAdapter.notifyDataSetChanged();
							UpdateAvailableAmount();
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}, "Отмена", null, "", null);
				break;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".onCreateDialog: " + id);
		switch (id) {
			case IDD_END_VISIT: {
				/*if (GPSInfo.isTPNearClient(mClient.getLat(), mClient.getLon()) == GPSInfo.GPS_NOT_AVAILABLE) {
					CreateErrorDialog(R.string.gps_end_vizit_not_available).show();
					return super.onCreateDialog(id);
				}*/
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View layout = LayoutInflater.from(this).inflate(R.layout.dialog_end_visit, (ViewGroup) findViewById(R.id.layout_root));
				//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mVizitsResults);
				//arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//mSpinnerVizitsResults = (Spinner) layout.findViewById(R.id.combo_activnost);
				//mSpinnerVizitsResults.setAdapter(arrayAdapter);
				autoCompleteTextViewVizit = (AutoCompleteTextView) layout.findViewById(R.id.autoCompleteTextViewVizit);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mVizitsResults);
				//System.out.println(adapter);
				autoCompleteTextViewVizit.setAdapter(adapter);
				autoCompleteTextViewVizit.setText("взят заказ");
				builder.setView(layout);
				builder.setTitle("Результат визита");
				//getString(R.string.choose_action));
				builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String result = autoCompleteTextViewVizit.getText().toString();
						if (result.trim().length() > 3) {
							mGPSInfo.EndVisit(mAppInstance.getClientInfo().getKod()//
									//, mVizitsResults.get(mSpinnerVizitsResults.getSelectedItemPosition())//
									//, autoCompleteTextViewVizit.getText().toString());
									, result);
							if (!mWantExit) {
								UIHelper.quickWarning(getString(R.string.vizit_complete), Activity_BidsContractsEtc.this);
								//Toast.makeText(getApplicationContext(), getString(R.string.vizit_complete), 
								//		Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							} else {
								finish();
							}
						} else {
							Auxiliary.warn("Заполните результат визита!", Activity_BidsContractsEtc.this);
						}
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mWantExit = false;
						dialog.dismiss();
					}
				});
				return builder.create();
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogHelper.debug(this.getClass().getCanonicalName() + ".onActivityResult");
		switch (requestCode) {
			case ORDER_UPDATE:
				if (resultCode == RESULT_OK) {
					mBidsListAdapter.getCursor().requery();
					mBidsListAdapter.notifyDataSetChanged();
					UpdateAvailableAmount();
				}
				NomenclatureSavedData.getInstance().CleanData();
				break;
			case RETURNS_ADD:
				if (resultCode == RESULT_OK) {
					mReturnsListAdapter.getCursor().requery();
					mReturnsListAdapter.notifyDataSetChanged();
				}
				break;
			case FIXED_PRICES_ADD:
				if (resultCode == RESULT_OK) {
					mFixedPricesListAdapter.getCursor().requery();
					mFixedPricesListAdapter.notifyDataSetChanged();
				}
				break;
		}
		mIsNeedRequeryTempTables = false;
	}

	//---------------------------------------------------------------------------------------
	//  Contracts part
	//---------------------------------------------------------------------------------------
	private void ConstructContractsLists() {
		mAmount = (TextView) findViewById(R.id.text_total);
		ConstructContractsTable_0();
		ConstructContractsTable_1();
		ConstructContractsTable_2();
	}

	private void ConstructContractsTable_0() {
		TableLayout layout = (TableLayout) findViewById(R.id.table_contracts_0);
		TableRow row = null;
		TextView textContractsGroup = null;
		TextView textLimit = null;
		TextView textDelay = null;
		String sqlStr = "select distinct gd._id [_id], gd.[_IDRRef], gd.[Naimenovanie] [GruppyDogovorovNaimenovanie], l.[Limit], l.[Otsrochka] "// 
				+ "from DogovoryKontragentov dk "//
				+ "inner join GruppyDogovorov gd on dk.[GruppaDogovorov] = gd.[_IDRRef] "//
				+ "inner join Cur_Limity l on gd.[_IDRRef] = l.SpisokDogovorov "//
				+ "where dk.[Vladelec]="//
				+ mAppInstance.getClientInfo().getID();// + " and dk.PometkaUdaleniya = x'00'";
		Cursor cursor = mDB.rawQuery(sqlStr, null);
		if (cursor.moveToFirst()) {
			do {
				row = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_contracts_0, null).findViewById(R.id.contracts_tablerow_0);
				textContractsGroup = (TextView) row.findViewById(R.id.text_contracts_group);
				textContractsGroup.setText(cursor.getString(2));
				textLimit = (TextView) row.findViewById(R.id.text_limit);
				textLimit.setText(cursor.getString(3));
				textDelay = (TextView) row.findViewById(R.id.text_delay);
				textDelay.setText(cursor.getString(4));
				layout.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			while (cursor.moveToNext());
		}
		cursor.close();
	}

	private void ConstructContractsTable_1() {
		TableLayout layout = (TableLayout) findViewById(R.id.table_contracts_1);
		TableRow row = null;
		TextView textDate = null;
		TextView textDocument = null;
		TextView textAmount = null;
		String sqlStr = "select dsz.[Period], case when dsz.[_RecordKind] = 0 then dsz.[Summa] else -dsz.[Summa] end [Summa], ifnull(gd.[Naimenovanie], '') [GruppyDogovorovNaimenovanie], " + "case when dsz.[Registrator_0] = x'00000086' then 'Доступная сумма ' ||  gd.[Naimenovanie] || ' от ' || strftime('%d.%m.%Y', dsz.[Period]) " + "when dsz.[Registrator_0] = x'00000091' then ifnull('Заказ покупателя исходящий ' || zpi.Nomer || ' от ' || strftime('%d.%m.%Y %H:%M:%S', zpi.[Data]), '') " + "else '' end Naimenovanie " + "from DostupnayaSummaZakaza dsz " + "inner join (select date(c.[RaschitannyyDolg], '+1 day') [date] from Consts c) cday on dsz.[Period] >= cday.[date] " + "left join GruppyDogovorov gd on dsz.[GruppaDogovorov_2] = gd.[_IDRRef] " + "inner join DogovoryKontragentov dk on dk.[GruppaDogovorov] = gd.[_IDRRef] " + "left join DolgiKlientov dok on dsz.[Registrator_0] = x'00000086' and dsz.[Registrator_1] = dok._IDRRef " + "left join ZayavkaPokupatelyaIskhodyaschaya zpi on dsz.[Registrator_0] = x'00000091' and dsz.[Registrator_1] = zpi._IDRRef " + "where dsz.Aktivnost = x'01' and dsz.[_RecordKind] = 0 and dk.[Vladelec] = " + mAppInstance.getClientInfo().getID();
		Cursor contractsAmountCursor = mDB.rawQuery(sqlStr, null);
		if (contractsAmountCursor.moveToFirst()) {
			do {
				row = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_contracts_1, null).findViewById(R.id.contracts_tablerow_1);
				textDate = (TextView) row.findViewById(R.id.text_date);
				textDate.setText((String) DateTimeHelper.UIDateString(DateTimeHelper.SQLDateToDate(contractsAmountCursor.getString(0))));
				textDocument = (TextView) row.findViewById(R.id.text_document);
				textDocument.setText(contractsAmountCursor.getString(3));
				textAmount = (TextView) row.findViewById(R.id.text_amount);
				textAmount.setText(DecimalFormatHelper.format(contractsAmountCursor.getDouble(1)));
				layout.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			while (contractsAmountCursor.moveToNext());
		}
		SetAvailableAmount(contractsAmountCursor);
		if (contractsAmountCursor != null && !contractsAmountCursor.isClosed()) {
			contractsAmountCursor.close();
		}
	}

	private void ConstructContractsTable_2() {
		TableLayout layout = (TableLayout) findViewById(R.id.table_contracts_2);
		TableRow row = null;
		ImageView imClose = null;
		TextView textCode = null;
		TextView textName = null;
		TextView textContractsGroup = null;
		String sqlStr = "select dk._id, dk.[_IDRRef], dk.[Kod], dk.[Naimenovanie] [DogovoryKontragentovNaimenovanie], dk.Zakryt, " + "ifnull(gd.[Naimenovanie], '') [GruppyDogovorovNaimenovanie] "//
				+ "from DogovoryKontragentov_strip dk " //
				+ "left join GruppyDogovorov gd on dk.[GruppaDogovorov] = gd.[_IDRRef] " //
				+ "where dk.[Vladelec]=" + mAppInstance.getClientInfo().getID();
		Cursor cursor = mDB.rawQuery(sqlStr, null);
		if (cursor.moveToFirst()) {
			do {
				row = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_contracts_2, null).findViewById(R.id.contracts_tablerow_2);
				textCode = (TextView) row.findViewById(R.id.text_code);
				textCode.setText(cursor.getString(2));
				textName = (TextView) row.findViewById(R.id.text_name);
				textName.setText(cursor.getString(3));
				textContractsGroup = (TextView) row.findViewById(R.id.text_contracts_group);
				textContractsGroup.setText(cursor.getString(5));
				imClose = (ImageView) row.findViewById(R.id.img_close);
				if (cursor.getBlob(4)[0] == 1) {
					imClose.setImageResource(android.R.drawable.checkbox_on_background);
				} else {
					imClose.setImageResource(android.R.drawable.checkbox_off_background);
				}
				layout.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			while (cursor.moveToNext());
		}
		cursor.close();
	}

	private void UpdateAvailableAmount() {
		if (mAmount != null) {
			mAmount.setText(DecimalFormatHelper.format(mAvailableAmount - Request_Bids.getBidsAmount(mDB, DateTimeHelper.SQLDateString(Calendar.getInstance().getTime()))));
		}
	}

	private void SetAvailableAmount(Cursor cursor) {
		mAvailableAmount = 0.00;
		if (cursor.moveToFirst()) {
			do {
				mAvailableAmount += cursor.getDouble(3);
			}
			while (cursor.moveToNext());
		}
		UpdateAvailableAmount();
	}

	void showPechati() {
		//System.out.println("showPechati");
		Intent intent = new Intent();
		intent.setClass(this, Activity_Pechati.class);
		startActivity(intent);
	}

	/*
		void startCheckList() {
			long id = ActivityCheckDocs.findOrCreateNew(checkTerriKod);
			String cuKl = mAppInstance.getClientInfo().getKod().trim();
			ActivityCheckList.addForKontragent(cuKl, checkTerriKod, "" + id);
			Intent intent = new Intent(Activity_BidsContractsEtc.this, ActivityCheckList.class);
			intent.putExtra("doc_id", "" + id);
			intent.putExtra("kontragent", "" + cuKl);
			startActivity(intent);
		}

		void showChekList() {

			if(checkTerriKod != null) {
				startCheckList();
				return;
			}
			final Numeric nn = new Numeric().value(0);
			final Vector<Bough> names = new Vector<Bough>();
			for(int i = 0; i < Cfg.territory().children.size(); i++) {
				if(!ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().equals(//
						Cfg.territory().children.get(i).child("hrc").value.property.value().trim())) {
					names.add(Cfg.territory().children.get(i));
				}
			}
			if(names.size() > 0) {
				final String[] ters = new String[names.size()];
				for(int i = 0; i < names.size(); i++) {
					ters[i] = names.get(i).child("territory").value.property.value()//
									  + " (" + names.get(i).child("hrc").value.property.value().trim() + ")";
				}
				Auxiliary.pickSingleChoice(Activity_BidsContractsEtc.this, ters, nn, null, new Task() {
					@Override
					public void doTask() {
						checkTerriKod = names.get(nn.value().intValue()).child("kod").value.property.value().trim();

						startCheckList();
					}
				}, null, null, null, null);
			}
			else {
				Auxiliary.warn("Нет подчинённых территорий", this);
			}
		}
	*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOtchety = menu.add("Отчёты");
		menuPechati = menu.add("Печати контрагента");
		//menuChekList = menu.add("Чек-лист");
		menuKartaKlienta = menu.add("Карта клиента");
		menuObnoIstoria = menu.add("Обновить историю");
		menuShoMap = menu.add("Показать на карте");
		menuRequestMailList = menu.add("Выслать шаблон заказа");
		return true;
	}

	void doObnoIstoria() {//http://89.109.7.162/GolovaNew/hs/ObnovlenieInfo/Istoriya?klient=82496
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ObnovlenieInfo/Istoriya?klient=" + mAppInstance.getClientInfo().getKod().trim();
		//final String url = Settings.getInstance().getBaseURL() + "GolovaNew/hs/ObnovlenieInfo/DannyeMarshruta?hrc=" + mAppInstance.getCurrentAgent().getAgentName().trim();
		//System.out.println(url);
		new Expect().task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String s = new String(b, "utf-8");
					String[] commands = s.split("\n");
					for (int i = 0; i < commands.length; i++) {
						String sql = commands[i].trim();
						if (sql.length() > 1) {
							//System.out.println(i + ": " + sql);
							Activity_BidsContractsEtc.this.mDB.execSQL(sql);
						}
					}
					UpdateTask.refreshProdazhi_last(Activity_BidsContractsEtc.this.mDB);
				} catch (Exception e) {
					//
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).status.is("Обновление БД").start(this);
	}

	void doRequestMailList() {
		//Note email=new Note();
		Auxiliary.pickText(this, "E-mail для отправки", Settings.emailToSend, "Отправить", new Task() {
			@Override
			public void doTask() {
				//System.out.println(Settings.emailToSend.value());
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuPechati) {
			showPechati();
			return true;
		}
		/*if(item == menuChekList) {
			showChekList();
			return true;
		}*/
		if (item == menuObnoIstoria) {
			doObnoIstoria();
			return true;
		}
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(Activity_BidsContractsEtc.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if (item == menuKartaKlienta) {
			Intent intent = new Intent();
			//intent.putExtra("_id", "" +mAppInstance.getClientInfo().getKod());
			intent.setClass(Activity_BidsContractsEtc.this, sweetlife.android10.supervisor.ActivityKartaKlienta.class);
			startActivity(intent);
			return true;
		}
		if (item == menuShoMap) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityGPSMap.class);
			this.startActivityForResult(intent, 0);
			return true;
		}
		if (item == menuRequestMailList) {
			doRequestMailList();
			return true;
		}
		return false;
	}
}
