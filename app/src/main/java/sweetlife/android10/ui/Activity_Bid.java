package sweetlife.android10.ui;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;
import java.util.Date;

import org.apache.http.impl.cookie.DateUtils;

import jxl.format.*;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableFont;
import jxl.write.WritableCellFormat;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.ISQLConsts;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.ContractInfo;
import sweetlife.android10.data.common.ExtraChargeInfo;
import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.data.orders.BidData;
import sweetlife.android10.data.orders.ContractsAdapter;
import sweetlife.android10.data.orders.FoodStuffListAdapter;
import sweetlife.android10.data.orders.FoodstuffsData;
import sweetlife.android10.data.orders.PaymentTypeAdapter;
import sweetlife.android10.data.orders.ServicesData;
import sweetlife.android10.data.orders.ServicesListAdapter;
import sweetlife.android10.data.orders.TraficsData;
import sweetlife.android10.data.orders.TrafiksListAdapter;
import sweetlife.android10.data.orders.ZayavkaPokupatelya;
import sweetlife.android10.data.orders.ZayavkaPokupatelya_Foodstaff;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Report_Base;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener;
import sweetlife.android10.*;
import tee.binding.*;
import tee.binding.task.*;
import tee.binding.it.*;

import android.app.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.OnTabChangeListener;

import sweetlife.android10.supervisor.*;

public class Activity_Bid extends Activity_Base implements OnTabChangeListener, ITableColumnsNames, ISQLConsts{

	private static final int IDD_DELETE = 101;
	private static final int IDD_SAVE_CHANGES = 102;
	private static final int IDD_IS_EMPTY = 103;
	private static final int IDD_PAYMENT_NOT_SELECT = 104;
	private static final int IDD_ALREADY_IN_LIST = 106;
	public static boolean hideNacenkaStatus = true;

	static final String Rstringfact_order="Наценка факт заказа";
	static final String Rstringnot_available="Недоступно";

	String nomerDokumenta1C = "";
	String nomerDokumentaTablet = "";
	boolean no_assortiment = false;

	int sortMode = FoodStuffListAdapter.sortByName;
	MenuItem menuExport;
	MenuItem menuImport;
	MenuItem menuFromSpecificacia;
	MenuItem menuDelete;
	MenuItem menuOtchety;
	MenuItem menuRecepty;
	MenuItem menuDostavka;
	MenuItem menuShowHideStatus;
	MenuItem kommercheskoePredlojenie;
	MenuItem menuRequestMailList;
	//MenuItem menuListovkaOtdelaProdazh;
	MenuItem menuKartochkaKlienta;
	MenuItem menuChangeDateRecalculate;


	boolean mIsCRAvailable = false;
	int pageSize = 25;

	int DATA_HISTORY = 0;
	int DATA_HERO = 1;
	int DATA_TOP = 2;
	int DATA_MOTI = 3;
	int DATA_INDIVIDUAL = 4;

	int DATA_CURRENT = DATA_HISTORY;
	Note dataModeCaption = new Note();
	//
	Layoutless layoutless;
	DataGrid gridHistory;
	//DataGrid gridMust;
	//Numeric gridOffsetMust = new Numeric();
	Numeric gridOffsetHistory = new Numeric();
	//public static int dataPageSize = 30;
	Note seekStringHistory = new Note();
	String seekPreHistory = "";
	//int currentOffsetHistory = 0;
	//int currentSizeHistory = 0;
	//boolean historyHasMoreData = false;
	Numeric historyFrom = new Numeric();
	Numeric historyTo = new Numeric();
	//Bough historyRows = null;
	RedactSingleChoice clientForHistory = null;
	Numeric selectedForHistory = new Numeric();
	//boolean historyGridLock = true;
	ColumnText historyPhoto = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyArtikul = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyNomenklatura = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyProizvoditel = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyMinKol = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyKolMest = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyEdIzm = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyRazmSkidki = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyVidSkidki = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnDescription historyPoslCena = new ColumnDescription().headerBackground.is(0xffe3e3e3);
	ColumnText historyMinCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText historyMaxCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	SplitLeftRight slrHistory;
	/*ColumnText mustPhoto = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustArtikul = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustNomenklatura = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustProizvoditel = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustMinKol = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustKolMest = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustEdIzm = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustRazmSkidki = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustVidSkidki = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustPoslCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustMinCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	ColumnText mustMaxCena = new ColumnText().headerBackground.is(0xffe3e3e3);
	SplitLeftRight slrMust;*/
	//Bough historyRows;
	Cursor historyCursor;
	//Cursor mustCursor;
	boolean dogovorEmpty = true;
	boolean lockResetHistoryData = false;
	String sku = "0";
	ExtraChargeInfo info;
	private ApplicationHoreca mAppInstance;
	private TabHost mTabHost;
	private ListView mFoodstuffList;
	private ListView mServicesList;
	private ListView mTrafiksList;
	private Spinner mSpnPaymentType;
	private Spinner mSpnContracts;
	private EditText mEditShippingDate;
	private Button mBtnNomenclature;
	private Button mBtnRecept;
	private Button mBtnFlagman;
	private Button mBtnGazeta;
	private Button mBtnArticle;
	private EditText mEditComment;
	private EditText mEditCustNum;
	private TextView mTextAvailableAmount;
	private TextView mTextOrderAmount;
	private BidData mBidData;
	private Calendar mShippingDate;

	public static boolean canCreateNewOrder(){
		boolean rr = false;
		Cursor cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery("select TekKatalog from consts where TekKatalog='';", null);
		if(cursor.moveToNext()){
			rr = true;
		}
		return rr;
	}

	public static void unLockCreateNewOrder(){
		ApplicationHoreca.getInstance().getDataBase().execSQL("update consts set TekKatalog='';");
	}

	public static void lockCreateNewOrder(String key){
		ApplicationHoreca.getInstance().getDataBase().execSQL("update consts set TekKatalog='" + key + "';");
	}

	private boolean mHasChanges = false;
	private boolean mIsOrderEditable = true;
	private boolean mIsOrderPropertiesEditable = true;
	private double mAvailableAmount;
	private int mListPositionForDelete = -1;
	private NomenclatureBasedDocumentItems mNomenclatureBasedDocumentItems;
	private PaymentTypeAdapter mPaymentTypeAdapter;
	private ContractsAdapter mContractsAdapter;
	private boolean mContractsIsFirstTime = true;
	private boolean mShowEditCountAndPricePopup = false;
	private OnClickListener mNomenclatureClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(!dogovorEmpty){
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
			}
			if(mTabHost.getCurrentTabTag().compareTo("tab_foodstuffs") == 0){
				Intent intent = new Intent();
				intent.setClass(Activity_Bid.this, Activity_Nomenclature.class);
				intent.setClass(Activity_Bid.this, Activity_NomenclatureNew.class);
				//intent.putExtra(CLIENT_ID, mBidData.getClientID());
				//intent.putExtra(ORDER_AMOUNT, mBidData.getBid().getSumma() + mBidData.getFoodStuffs().getAmount());
				intent.putExtra(ORDER_AMOUNT, mBidData.getBid().getSumma());
				startActivityForResult(intent, ADD_NOMENCATURE);
			}else{
				if(mTabHost.getCurrentTabTag().compareTo("tab_trafik") == 0){
					Intent intent = new Intent();
					intent.setClass(Activity_Bid.this, Activity_Trafiks.class);
					startActivityForResult(intent, ADD_TRAFIK);
				}else{
					Intent intent = new Intent();
					intent.setClass(Activity_Bid.this, Activity_Services.class);
					startActivityForResult(intent, ADD_SERVICE);
				}
			}
		}
	};

	void setSort(int mode){
		this.sortMode = mode;
		System.out.println("setSort " + this.sortMode);
		((Button)findViewById(R.id.head_btn_nomenclature)).setTextColor(0xff000099);
		((Button)findViewById(R.id.head_btn_count)).setTextColor(0xff000099);
		((Button)findViewById(R.id.head_btn_price)).setTextColor(0xff000099);
		((Button)findViewById(R.id.head_btn_last_price)).setTextColor(0xff000099);
		((Button)findViewById(R.id.head_btn_pricewithsale)).setTextColor(0xff000099);
		((Button)findViewById(R.id.head_btn_number)).setTextColor(0xff000099);
		if(this.sortMode == FoodStuffListAdapter.sortByName){
			((Button)findViewById(R.id.head_btn_nomenclature)).setTextColor(0xffffffff);
		}
		if(this.sortMode == FoodStuffListAdapter.sortByCount){
			((Button)findViewById(R.id.head_btn_count)).setTextColor(0xffffffff);
		}
		if(this.sortMode == FoodStuffListAdapter.sortByPrice){
			((Button)findViewById(R.id.head_btn_price)).setTextColor(0xffffffff);
		}
		if(this.sortMode == FoodStuffListAdapter.sortByLastPrice){
			((Button)findViewById(R.id.head_btn_last_price)).setTextColor(0xffffffff);
		}
		if(this.sortMode == FoodStuffListAdapter.sortByNacenka){
			((Button)findViewById(R.id.head_btn_pricewithsale)).setTextColor(0xffffffff);
		}
		if(this.sortMode == FoodStuffListAdapter.sortByNum){
			((Button)findViewById(R.id.head_btn_number)).setTextColor(0xffffffff);
		}
		((FoodStuffListAdapter)mFoodstuffList.getAdapter()).sortListByMode(this.sortMode);
		((ZoomListArrayAdapter)mFoodstuffList.getAdapter()).notifyDataSetChanged();

	}

	private OnClickListener mGazetaClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(!dogovorEmpty){
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
			}
			//System.out.println("OnClickListener mGazetaClick");
			//promptGazeta();
			promptListovkaOtdelaProdazh();
		}
	};


	private OnClickListener mFlagmanClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(!dogovorEmpty){
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
			}
			if(mTabHost.getCurrentTabTag().compareTo("tab_foodstuffs") == 0){

				Intent intent = new Intent();
				intent.setClass(Activity_Bid.this, Activity_FlagmanChooser.class);

				String clientID = "0";
				String polzovatelID = "0";
				String dataOtgruzki = "0";
				String sklad = "0";
				try{
					clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
					polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
					dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
					sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
				}catch(Throwable ttt){
					ttt.printStackTrace();
				}

				intent.putExtra("clientID", clientID);
				intent.putExtra("dataOtgruzki", dataOtgruzki);
				intent.putExtra("polzovatelID", polzovatelID);
				intent.putExtra("sklad", sklad);

				startActivityForResult(intent, ADD_NOMENCATURE);

			}
		}
	};

	void old_promptReceipt(){
		final Numeric sefIdx = new Numeric().value(-1);
		String sql = "select receptii.naimenovanie as recname,receptii._id as recid from receptii order by receptii.naimenovanie;";
		final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		if(b.children.size() > 0){
			String rows[] = new String[b.children.size()];
			for(int i = 0; i < b.children.size(); i++){
				rows[i] = b.children.get(i).child("recname").value.property.value();
			}
			sefIdx.afterChange(new Task(){
				public void doTask(){
					System.out.println(b.children.get(sefIdx.value().intValue()).child("recname").value.property.value()
							+ "/" + b.children.get(sefIdx.value().intValue()).child("recid").value.property.value()
					);
					openReceipt(b.children.get(sefIdx.value().intValue()).child("recid").value.property.value());
				}
			}, true);
			Auxiliary.pickSingleChoice(Activity_Bid.this, rows, sefIdx);
		}
	}

	void promptReceipt(){
		Intent intent = new Intent();
		intent.setClass(Activity_Bid.this, Activity_STM_chooser.class);
		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}

		intent.putExtra("clientID", clientID);
		intent.putExtra("dataOtgruzki", dataOtgruzki);
		intent.putExtra("polzovatelID", polzovatelID);
		intent.putExtra("sklad", sklad);
		startActivityForResult(intent, ADD_NOMENCATURE);
	}

	void openReceipt(String id){
		Intent intent = new Intent();
		intent.setClass(Activity_Bid.this, Activity_ReceptChooser.class);

		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}

		intent.putExtra("clientID", clientID);
		intent.putExtra("dataOtgruzki", dataOtgruzki);
		intent.putExtra("polzovatelID", polzovatelID);
		intent.putExtra("sklad", sklad);
		intent.putExtra("recid", id);

		startActivityForResult(intent, ADD_NOMENCATURE);
	}

	private OnClickListener mReceptClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(!dogovorEmpty){
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
			}
			if(mTabHost.getCurrentTabTag().compareTo("tab_foodstuffs") == 0){
				promptReceipt();
			}
		}
	};
	private OnClickListener __mReceptClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(!dogovorEmpty){
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
			}
			if(mTabHost.getCurrentTabTag().compareTo("tab_foodstuffs") == 0){
				Intent intent = new Intent();
				intent.setClass(Activity_Bid.this, Activity_ReceptChooser.class);

				String clientID = "0";
				String polzovatelID = "0";
				String dataOtgruzki = "0";
				String sklad = "0";
				try{
					clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
					polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
					dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
					sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
				}catch(Throwable ttt){
					ttt.printStackTrace();
				}

				intent.putExtra("clientID", clientID);
				intent.putExtra("dataOtgruzki", dataOtgruzki);
				intent.putExtra("polzovatelID", polzovatelID);
				intent.putExtra("sklad", sklad);

				startActivityForResult(intent, ADD_NOMENCATURE);
			}
		}
	};
	private OnCloseListener mOnPopupClose = new OnCloseListener(){
		@Override
		public void onClose(int param){
			UpdateAfterAddingNomenclature();
			//UpdateExtraChargeInfo();
			//updateExtraChargeInfo.start();
			clearExtraChargeInfo();
			//updateExtraChargeInfoTask.start();
		}
	};
	private OnClickListener mArticleClick = new OnClickListener(){
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0){
			if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
				showDialog(IDD_PAYMENT_NOT_SELECT);
				return;
			}
			Popup_AddNomenclature dialog = new Popup_AddNomenclature( //
					(View)mBtnArticle//
					, mOnPopupClose//
					, mBidData.getFoodStuffs()//
					, mDB//
					, !Requests.IsSyncronizationDateLater(0)//
			);
			dialog.showLikeQuickAction();
		}
	};

	public static String comment2comment(String cmnt){
		int nn = cmnt.indexOf('|');
		if(nn > -1){
			if(nn > 0){
				return cmnt.substring(0, nn);
			}else{
				return "";
			}
		}else{
			return cmnt;
		}
	}

	public static String comment2num(String cmnt){
		int nn = cmnt.indexOf('|');
		if(nn > -1){
			if(nn < cmnt.length() - 1){
				return cmnt.substring(nn + 1);
			}else{
				return "";
			}
		}else{
			return cmnt;
		}
	}

	public static double __getCenaSoSkidkoy(double scena, double Nacenka, double FiksirovannyeCeny, double SkidkaPartneraKarta, double NakopitelnyeSkidki){
		double cena = scena * (100 + Nacenka) / 100;
		if(FiksirovannyeCeny > 0){
			return FiksirovannyeCeny;
		}
		if(SkidkaPartneraKarta > 0){
			return SkidkaPartneraKarta;
		}
		if(NakopitelnyeSkidki > 0){
			//cena = cena * ((100.0 - NakopitelnyeSkidki) / 100);
		}
		return cena;
	}

	void clearExtraChargeInfo(){
		System.out.println("clearExtraChargeInfo "+hideNacenkaStatus);
		TextView textClientPlan = (TextView)findViewById(R.id.text_plan_client);
		//textClientPlan.setText("");
		textClientPlan.setText("пересчитывается...");
		//updateExtraChargeInfoTask.start();
		updateExtraChargeInfo();
	}

	void updateExtraChargeInfo(){
		String status = "";//Rstringfact_order;
		TextView textClientPlan = (TextView)findViewById(R.id.text_plan_client);
		if(!hideNacenkaStatus){

			try{
				mBidData.UpdateOrderExtraChargeInfo(DateTimeHelper.SQLDateString(mShippingDate.getTime()));
				info = mBidData.getExtraChargeInfo();
				if(info.getOrderFactPersent() != null){
					status = Rstringfact_order + ": " + info.getOrderFactPersent() + ", Вал: " + info.getOrderFactValKg();
				}else{
					status = Rstringfact_order + ": " + Rstringnot_available;
				}
			}catch(Throwable t){
				t.printStackTrace();
				textClientPlan.setText(Rstringfact_order + " " + t.toString());
			}
		}
		textClientPlan.setText(status);
	}


	@Override
	public void onCreate(Bundle savedInstanceState2){
		super.onCreate(savedInstanceState2);
		System.out.println("Activity_Bid onCreate " + Auxiliary.bundle2bough(getIntent().getExtras()).dumpXML());

/*
		System.out.println("android.os.StrictMode.VmPolicy");
		android.os.StrictMode.VmPolicy policy = new android.os.StrictMode.VmPolicy.Builder()
		     .detectAll()
		     .penaltyLog()
		     .build();
		 android.os.StrictMode.setVmPolicy(policy);
		*/


		setContentView(R.layout.act_bid);
		//Layoutless.fillBaseColors(this);
		mAppInstance = ApplicationHoreca.getInstance();
		mBidData = new BidData();
		InitializeTabHost();
		ReadExtras();
		ConstructBottomButtons();
		ConstructShippingDate();
		ConstructFoodStuffsList();
		ConstructServicesList();
		ConstructTrafiksList();


		mTextAvailableAmount = (TextView)findViewById(R.id.text_available_amount);
		if(!mIsOrderEditable){
			mTextAvailableAmount.setVisibility(View.INVISIBLE);
		}
		mTextOrderAmount = (TextView)findViewById(R.id.text_total);
		ConstructContractsSpinner(mBidData.getBid().getDogovorKontragenta());
		dataModeSet();
		try{
			ConstructPaymentTypeSpinner(mContractsAdapter.getSelectedItem().getID(), mBidData.getBid().getTipOplaty());
			mBidData.setExtraChargeInfo(new ExtraChargeInfo(mDB, mBidData.getClientID()));
			new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... params){
					//requeryHistoryData();
					try{
						Thread.sleep(333);
					}catch(Throwable t){
						t.printStackTrace();
					}
					getSKU();
					//requeryHistoryData();
					//requeryMustData();
					return null;
				}

				@Override
				protected void onPostExecute(Void v){


					resetTitle();
					historyFillColumns(false);
					//gridHistory.refresh();
					refreshAfterPaymentSelect();
					//mustFillColumns(false);
					//gridMust.refresh();
					//UpdateExtraChargeInfo();
					//historyGridLock = false;
					//System.out.println("gridHistory.refresh() done");
				}
			}.execute();
			dogovorEmpty = false;
		}catch(Throwable t){
			t.printStackTrace();
			/*UIHelper.MsgBox("Тип оплаты", "Для данного клиента нет договоров " + t.getMessage(), this, new sweetlife.horeca.utils.UIHelper.IMessageBoxCallbackInteger() {
				@Override
				public void MessageBoxResult(int which) {
					Activity_Bid.this.finish();
				}
			});
			this.finish();
			return;*/
			Auxiliary.warn("Для данного клиента нет договоров!", Activity_Bid.this);
		}
		ConstructAdditionalTab();
		UpdateAvailableAmount();
		SetOrderPropertiesEditable(mIsOrderPropertiesEditable);
		//resetTitle();
		/*String clientName = "?";
		try {
			clientName = mAppInstance.getClientInfo().getName();
		}
		catch (Throwable t) {
			System.out.println("mAppInstance.getClientInfo().getName() + : " + t.getMessage());
		}
		setTitle("Заказ " + mBidData.getBid().getNomer()//
				+ " от " + DateTimeHelper.UIDateString(mBidData.getBid().getDate())//
				+ " (" + clientName + ")");*/
		mNomenclatureBasedDocumentItems = mBidData.getFoodStuffs();
		//InitializeExtraChargeInfo();
		//fillTargetCondition();
		composeHistoryTab();
		//composeMustTab();
		if(hideNacenkaStatus){
			TextView textClientPlan = (TextView)findViewById(R.id.text_plan_client);
			textClientPlan.setVisibility(textClientPlan.INVISIBLE);
		}
		//System.out.println("Activity_Bid onCreate done");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//System.out.println("Activity_Bid onSaveInstanceState "+Auxiliary.bundle2bough(outState.getExtras()).dumpXML());
	}

	@Override
	protected void onPause(){
		super.onPause();
		//System.out.println("Activity_Bid onPause");
	}

	@Override
	protected void onStop(){
		super.onStop();
		//System.out.println("Activity_Bid onStop");
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		//System.out.println("Activity_Bid onDestroy");
	}

	@Override
	public void onStart(){
		super.onStart();
		//System.out.println("Activity_Bid onStart");
	}

	@Override
	public void onRestart(){
		super.onRestart();
		//System.out.println("Activity_Bid onRestart");
	}

	@Override
	protected void onResume(){
		//System.out.println("Activity_Bid onResume");
		try{
			//UpdateExtraChargeInfo();
			clearExtraChargeInfo();
			//updateExtraChargeInfoTask.start();
		}catch(Throwable t){
			LogHelper.debug(this.getClass().getCanonicalName() + " onResume " + t.getMessage());
		}
		super.onResume();
		//System.out.println("onResume done");
	}

	void resetTitle(){
		//System.out.println("resetTitle start");
		String clientName = "?";
		try{
			clientName = mAppInstance.getClientInfo().getName();
		}catch(Throwable t){
			System.out.println("mAppInstance.getClientInfo().getName() + : " + t.getMessage());
		}
		//System.out.println("resetTitle клиент " + clientName);
		String dostavka = "";
		if(mBidData.getBid().dostavkaKind == ZayavkaPokupatelya.DOSTAVKA_DOVERENNOST){
			dostavka = "(самовывоз/доверенность: " + mBidData.getBid().dostvkaKoment + ") ";
		}else{
			if(mBidData.getBid().dostavkaKind == ZayavkaPokupatelya.DOSTAVKA_PECHAT){
				dostavka = "(самовывоз/печать: " + mBidData.getBid().dostvkaKoment + ") ";
			}
		}
		//System.out.println("resetTitle доставка " + dostavka);
		getSKU();
		setTitle(mBidData.getBid().getNomer()//
				+ ", " + DateTimeHelper.UIDateString(mBidData.getBid().getDate())//
				+ ", SKU: " + sku + " " + dostavka + clientName);
		System.out.println("resetTitle done");
	}

	void getSKU(){
		java.util.Date d = new java.util.Date(mBidData.getBid().getShippingDateInMillis());
		d.setDate(1);
		//System.out.println("getSKU start " + d);
		DateTimeHelper.SQLDateString(d);
		/*
		String sql = "select count(nomenklatura) as sku"//
				+ "\n	from Prodazhi_last"//
				+ "\n	join DogovoryKontragentov_strip on Prodazhi_last.DogovorKontragenta=DogovoryKontragentov_strip._IDRref"//
				//+ "\n	where period>='" + DateTimeHelper.SQLDateString(d) + "'"//
				+ "\n	where period<='" + DateTimeHelper.SQLDateString(d) + "'"//
				+ "\n	and DogovoryKontragentov_strip.vladelec=" + mAppInstance.getClientInfo().getID();
		*/
		String sql = " select count(nomenklatura) as sku from"//
				+ "\n (select nomenklatura as nomenklatura"//
				+ "\n  	from Prodazhi_last"//
				+ "\n  	join DogovoryKontragentov_strip on Prodazhi_last.DogovorKontragenta=DogovoryKontragentov_strip._IDRref"//
				+ "\n	where period>='" + DateTimeHelper.SQLDateString(d) + "'"//
				+ "\n  	and DogovoryKontragentov_strip.vladelec=" + mAppInstance.getClientInfo().getID()//
				+ "\n union select nomenklatura as nomenklatura"//
				+ "\n  	from ZayavkaPokupatelyaIskhodyaschaya_Tovary t"//
				+ "\n  	join ZayavkaPokupatelyaIskhodyaschaya z on z._idrref=t._ZayavkaPokupatelyaIskhodyaschaya_IDRRef"//
				+ "\n  	where z.kontragent=" + mAppInstance.getClientInfo().getID()//
				+ "\n	and dataOtgruzki>='" + DateTimeHelper.SQLDateString(d) + "'"//
				+ "\n ) a";
		//System.out.println(sql);
		sku = "?";
		try{
			Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
			sku = b.children.get(0).child("sku").value.property.value();
		}catch(Throwable t){
			t.printStackTrace();
		}
		//System.out.println("getSKU done");
	}

	private void ReadExtras(){
		Bundle extras = getIntent().getExtras();

		System.out.println("ReadExtras " + Auxiliary.activityExatras(this).dumpXML());

		mBidData.setClientID(extras.getString(CLIENT_ID));
		mAppInstance.setClientInfo(new ClientInfo(mDB, extras.getString(CLIENT_ID)));

		mIsOrderEditable = extras.getBoolean(IS_EDITABLE, true);
		Calendar choosedDay = (Calendar)mAppInstance.getShippingDate().clone();
		mBidData.setChoosedDay(choosedDay);
		ZayavkaPokupatelya bid = extras.getParcelable("ZayavkaPokupatelya");//BIDZayavkaPokupatelya);

		nomerDokumenta1C = Auxiliary.activityExatras(this).child("nomerDokumenta1C").value.property.value();
		nomerDokumentaTablet = Auxiliary.activityExatras(this).child("nomerDokumentaTablet").value.property.value();
		no_assortiment = Auxiliary.activityExatras(this).child("no_assortiment").value.property.value().equals("true");


		if(bid == null){
			if(!canCreateNewOrder()){
				finish();
			}
			double dateShip = Numeric.string2double(Auxiliary.activityExatras(this).child("dateShip").value.property.value());
			if(dateShip > 0){
				Calendar cc = Calendar.getInstance();
				cc.setTimeInMillis((long)dateShip);
				bid = new ZayavkaPokupatelya(mDB, new ClientInfo(mDB, extras.getString(CLIENT_ID)), cc);
				bid.setContract(Auxiliary.activityExatras(this).child("dogovor_idrref").value.property.value());
				bid.setTipOplaty(Auxiliary.activityExatras(this).child("oplatanum").value.property.value());
				String raw_data = Auxiliary.activityExatras(this).child("raw_data").value.property.value();
				raw_data = raw_data.replace("&", "-");
				Bough raw = Bough.parseXML(raw_data);
				System.out.println("raw " + raw.dumpXML());
				Vector<Bough> tovari = raw.child("Данные").children("Товары");
				mBidData.setBid(bid);
				mBidData.setFoodStuffs(new FoodstuffsData(mDB, mBidData.getBid()));
				cloneOrder(tovari, mBidData.getFoodStuffs());
			}else{
				bid = new ZayavkaPokupatelya(mDB, new ClientInfo(mDB, extras.getString(CLIENT_ID)), (Calendar)mBidData.getChoosedDay().clone());
				mBidData.setBid(bid);
				mBidData.setFoodStuffs(new FoodstuffsData(mDB, mBidData.getBid()));
			}
		}else{
			mIsOrderPropertiesEditable = false;
			mBidData.setBid(bid);
			mBidData.setFoodStuffs(new FoodstuffsData(mDB, mBidData.getBid()));
		}
		mBidData.setServices(new ServicesData(mDB, mBidData.getBid()));
		mBidData.setTrafiks(new TraficsData(mDB, mBidData.getBid()));
		mIsOrderEditable = extras.getBoolean(IS_EDITABLE, true);
		mAvailableAmount = extras.getDouble(AVAILABLE_AMOUNT, 0.00);
	}

	private void InitializeTabHost(){
		mTabHost = (TabHost)findViewById(R.id.tab_host);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("tab_foodstuffs").setIndicator(makeTabIndicator(getString(R.string.foodstuffs))).setContent(R.id.foodstuffstab));
		mTabHost.addTab(mTabHost.newTabSpec("tab_services").setIndicator(makeTabIndicator(getString(R.string.services))).setContent(R.id.servicestab));
		mTabHost.addTab(mTabHost.newTabSpec("tab_trafik").setIndicator(makeTabIndicator(getString(R.string.trafiks))).setContent(R.id.trafikstab));
		mTabHost.addTab(mTabHost.newTabSpec("tab_additional").setIndicator(makeTabIndicator(getString(R.string.additional))).setContent(R.id.additionaltab));
		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(this);
	}

	private void ConstructFoodStuffsList(){
		FoodStuffListAdapter adapter = new FoodStuffListAdapter(mBidData.getFoodStuffs(), Auxiliary.screenWidth(this));
		mFoodstuffList = (ListView)findViewById(R.id.list_foodstuffs);
		mFoodstuffList.setAdapter(adapter);
		if(mIsOrderEditable){
			mFoodstuffList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3){
					//System.out.println("Popup_EditNomenclatureCountPrice");
					ZayavkaPokupatelya_Foodstaff zayavkaPokupatelya_Foodstaff = mBidData.getFoodStuffs().getFoodstuff(position);
					//System.out.println("zayavkaPokupatelya_Foodstaff: " + zayavkaPokupatelya_Foodstaff.getArtikul() + ", " + zayavkaPokupatelya_Foodstaff.getVidSkidki());
					Popup_EditNomenclatureCountPrice popup = new Popup_EditNomenclatureCountPrice(view, mOnPopupClose, zayavkaPokupatelya_Foodstaff
							, mShippingDate.getTimeInMillis());
					popup.showLikeQuickAction();
				}
			});
			registerForContextMenu(mFoodstuffList);
			mFoodstuffList.setOnTouchListener(this);
		}

		Button bb = (Button)findViewById(R.id.head_btn_article);
		System.out.println("=====================");
		System.out.println(bb);
		((Button)findViewById(R.id.head_btn_nomenclature)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByName);
			}
		});
		System.out.println(bb);
		((Button)findViewById(R.id.head_btn_count)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByCount);
			}
		});
		((Button)findViewById(R.id.head_btn_price)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByPrice);
			}
		});
		((Button)findViewById(R.id.head_btn_last_price)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByLastPrice);
			}
		});
		((Button)findViewById(R.id.head_btn_pricewithsale)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByNacenka);
			}
		});
		((Button)findViewById(R.id.head_btn_number)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				setSort(FoodStuffListAdapter.sortByNum);
			}
		});
	}

	private void ConstructTrafiksList(){
		TrafiksListAdapter adapter = new TrafiksListAdapter(mBidData.getTrafiks());
		mTrafiksList = (ListView)findViewById(R.id.list_trafiks);
		mTrafiksList.setAdapter(adapter);
		if(mIsOrderEditable){
			mTrafiksList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3){
					Popup_EditTrafik popup = new Popup_EditTrafik(view, mOnPopupClose, mBidData.getTrafiks().getTrafik(position));
					popup.showLikeQuickAction();
				}
			});
			registerForContextMenu(mTrafiksList);
			mTrafiksList.setOnTouchListener(this);
		}
	}

	private void ConstructServicesList(){
		ServicesListAdapter adapter = new ServicesListAdapter(mBidData.getServices());
		mServicesList = (ListView)findViewById(R.id.list_services);
		mServicesList.setAdapter(adapter);
		if(mIsOrderEditable){
			mServicesList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3){
					Popup_EditNomenclatureCount popup = new Popup_EditNomenclatureCount(view, mOnPopupClose, mBidData.getServices().getService(position));
					popup.showLikeQuickAction();
				}
			});
			registerForContextMenu(mServicesList);
			mServicesList.setOnTouchListener(this);
		}
	}

	private void ConstructShippingDate(){
		mShippingDate = Calendar.getInstance();
		mShippingDate.setTimeInMillis(mBidData.getBid().getShippingDateInMillis());
		//System.out.println(mShippingDate.getTime() + " / " + DateTimeHelper.UIDateString(mShippingDate.getTime()));
		mEditShippingDate = (EditText)findViewById(R.id.edit_shipping);
		mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));
		mEditShippingDate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(mIsOrderPropertiesEditable){//mIsOrderEditable) {
					DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
							mAppInstance.setShippingDate(year, monthOfYear, dayOfMonth);
							mEditShippingDate.setText(DateTimeHelper.UIDateString(mAppInstance.getShippingDate().getTime()));
							Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
							mBidData.getBid().setShippingDate(d.getTime());
							mShippingDate.setTimeInMillis(mBidData.getBid().getShippingDateInMillis());
							//System.out.println(mShippingDate.getTime() + " / " + DateTimeHelper.UIDateString(mShippingDate.getTime()));
							//resetHistoryData();
							//historyFillColumns();
							//gridHistory.flip();
						}
					};
					Calendar shippingDate = mAppInstance.getShippingDate();
					new DatePickerDialog(Activity_Bid.this, dateSetListener, shippingDate.get(Calendar.YEAR), shippingDate.get(Calendar.MONTH), shippingDate.get(Calendar.DAY_OF_MONTH)).show();
				}else{
					promptChangeDateRecalculate();
				}
			}
		});
		//		mEditShippingDate.setOnClickListener(mShippingDateClick);
		//		mBtnShippingDate = (Button)findViewById(R.id.btn_shipping_date);
		//		mBtnShippingDate.setOnClickListener(mShippingDateClick);
	}

	/*
	private OnClickListener mShippingDateClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			DatePickerDialog.OnDateSetListener dateSetListener =
					new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view,
						int year,
						int monthOfYear,
						int dayOfMonth) {

					mShippingDate.set( year, monthOfYear, dayOfMonth );

					mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));

					mBidData.getBid().setShippingDate(mShippingDate.getTimeInMillis());
				}
			};

			new DatePickerDialog( Activity_Bid.this,
					dateSetListener,
					mShippingDate.get(Calendar.YEAR),
					mShippingDate.get(Calendar.MONTH),
					mShippingDate.get(Calendar.DAY_OF_MONTH)).show();

		}
	};
	 */
	private void ConstructBottomButtons(){
		mBtnNomenclature = (Button)findViewById(R.id.btn_nomenclature);
		mBtnArticle = (Button)findViewById(R.id.btn_article);
		mBtnRecept = (Button)findViewById(R.id.btn_recept);
		mBtnFlagman = (Button)findViewById(R.id.btn_flagman);
		mBtnGazeta = (Button)findViewById(R.id.btn_gazeta);

		if(mIsOrderEditable){
			mBtnNomenclature.setTag(new Integer(0));
			mBtnNomenclature.setOnClickListener(mNomenclatureClick);
			mBtnArticle.setOnClickListener(mArticleClick);
			mBtnRecept.setOnClickListener(mReceptClick);
			mBtnFlagman.setOnClickListener(mFlagmanClick);
			mBtnGazeta.setOnClickListener(mGazetaClick);
		}else{
			mBtnNomenclature.setEnabled(false);
			mBtnArticle.setEnabled(false);
			mBtnRecept.setEnabled(false);
			mBtnFlagman.setEnabled(false);
			mBtnGazeta.setEnabled(false);
		}

/*
        mBtnRecept = (Button) findViewById(R.id.btn_recept);
        mBtnArticle = (Button) findViewById(R.id.btn_article);
        if (mIsOrderEditable) {
            mBtnNomenclature.setTag(new Integer(0));
            mBtnNomenclature.setOnClickListener(mNomenclatureClick);
            mBtnArticle.setOnClickListener(mArticleClick);
        } else {
            mBtnNomenclature.setEnabled(false);
            mBtnArticle.setEnabled(false);
        }*/


		/*((Button) findViewById(R.id.btn_show_photo)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int position = mFoodstuffList.getSelectedItemPosition();
				if (position >= 0) {
					sweetlife.horeca.data.orders.ZayavkaPokupatelya_Foodstaff z = mBidData.getFoodStuffs().getFoodstuff(0);
					UIHelper.showPhotoByArtikul(z.getArtikul(), Activity_Bid.this);
				}
				else {
					UIHelper.quickWarning("Не выбрана запсиь.", Activity_Bid.this);
				}
			}
		});*/
		Button btnSave = (Button)findViewById(R.id.btn_add);
		if(mIsOrderEditable){
			btnSave.setOnClickListener(new View.OnClickListener(){
				@SuppressWarnings("deprecation")
				public void onClick(View v){
					if(mHasChanges){
						/*if (
								mBidData.getFoodStuffs().getCount() == 0
										&& mBidData.getServices().getCount() == 0
										&& mBidData.getTrafiks().getCount() == 0
						) {
							showDialog(IDD_IS_EMPTY);
							return;
						}*/
						//SaveChangesBeforeExit();
						//promptMoreItems();
						promptRecomendatciiSelection();
					}else{
						Activity_Bid.this.finish();
					}
				}
			});
			//System.out.println("temporary "+temporary);
			if(nomerDokumenta1C.length() > 1){
				btnSave.setText("Отправить");
			}
		}else{
			btnSave.setEnabled(false);
		}
		/*
		Button btnKartochka = (Button) findViewById(R.id.btn_add);
		btnKartochka.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				doKartochkaKlienta();
			}
		});
*/
	}

	void saveChanges(){
		//System.out.println("Activity_Bid saveChanges");
		if(dogovorEmpty){
			return;
		}
		mBidData.getFoodStuffs().WriteToDataBase(mDB);
		mBidData.getServices().WriteToDataBase(mDB);
		mBidData.getTrafiks().WriteToDataBase(mDB);
		ZayavkaPokupatelya bid = mBidData.getBid();
		bid.setSumma(mBidData.getFoodStuffs().getAmount() + mBidData.getServices().getCount());
		bid.setContract(mContractsAdapter.getSelectedItem().getID());
		bid.setTipOplaty(mPaymentTypeAdapter.GetSelectedItem().getID());
		bid.setComment(mEditComment.getText().toString() + "|" + mEditCustNum.getText().toString());
		bid.setSebestoimost(mBidData.getFoodStuffs().getAmount());
		bid.setShippingDate(mShippingDate.getTimeInMillis());
		bid.writeToDataBase(mDB);
		//getIntent().putExtra("client_id", mAppInstance.getClientInfo().getID());
		//getIntent().putExtra("ZayavkaPokupatelya", bid);
		//getIntent().putExtra("saved", true);
		this.resetTitle();
		//Bundle extras = getIntent().getExtras();
		//System.out.println("Activity_Bid saveChanges "+Auxiliary.bundle2bough(extras).dumpXML());
		//ApplicationHoreca.lastZayavkaPokupatelya=bid;
		lockCreateNewOrder("lock");
	}

	void changeDateRecalculate(long newMs){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(newMs);
		System.out.println("set " + newMs + ": " + calendar);
		ZayavkaPokupatelya bid = mBidData.getBid();
		FoodstuffsData foodstuffsData = mBidData.getFoodStuffs();
		mShippingDate.setTimeInMillis(newMs);
		mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String warnings = ("Дата отгрузки: " + sdf.format(calendar.getTime()) + "\n");
		for(int i = 0; i < foodstuffsData.getCount(); i++){
			ZayavkaPokupatelya_Foodstaff zf = foodstuffsData.getFoodstuff(i);
			warnings = warnings + adjustOrderItem(zf);
		}
		this.saveChanges();

		Auxiliary.warn(warnings, this);
		/*if(warnings.length()>0){
			Auxiliary.warn("Пересчитаны сцены "+warnings,this);
		}*/
		((ZoomListArrayAdapter)mFoodstuffList.getAdapter()).notifyDataSetChanged();
	}

	String adjustOrderItem(ZayavkaPokupatelya_Foodstaff zf){
		String artikul = zf.getArtikul();
		System.out.println(zf.getArtikul()
				+ "art " + zf.getMinimalnayaCena() + "/" + zf.getCenaSoSkidkoy() + "/" + zf.getMaksimalnayaCena()
				+ ", skidka " + zf.getVidSkidki()
				+ ", " + zf.getNomenklaturaNaimenovanie()
		);
		String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
		String polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
		String dataOtgruzki = DateTimeHelper.SQLDateString(mShippingDate.getTime());
		String sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		String sql = Request_NomenclatureBase.composeSQLall(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, artikul//
				, ISearchBy.SEARCH_ARTICLE
				, false//
				, false//
				, sklad//
				, 1, 0, false, false, null, null, false, false, null, null, null, false);
		//System.out.println("execute");
		Cursor cc = mDB.rawQuery(sql, null);
		Bough b = Auxiliary.fromCursor(cc);

		double CENA = Numeric.string2double(b.children.get(0).child("Cena").value.property.value());
		double SKIDKA = Numeric.string2double(b.children.get(0).child("Skidka").value.property.value());
		String VID_SKIDKI = b.children.get(0).child("VidSkidki").value.property.value();


		double MIN_CENA = Math.ceil(Numeric.string2double(b.children.get(0).child("MinCena").value.property.value()) * 100.0) / 100.0;
		double MAX_CENA = Numeric.string2double(b.children.get(0).child("MaxCena").value.property.value());
		double BASE_PRICE = Numeric.string2double(b.children.get(0).child("BasePrice").value.property.value());
		double CENA_SO_SKIDKOY = SKIDKA;
		if(SKIDKA > 0){
			//
		}else{
			CENA_SO_SKIDKOY = CENA;
			if(zf.getVidSkidki().trim().toLowerCase().equals("цр")){
				CENA_SO_SKIDKOY = zf.getCenaSoSkidkoy();
			}
			if(MIN_CENA > 0){
				if(CENA_SO_SKIDKOY > MAX_CENA){
					CENA_SO_SKIDKOY = MAX_CENA;
				}
				if(CENA_SO_SKIDKOY < MIN_CENA){
					CENA_SO_SKIDKOY = MIN_CENA;
				}
			}
		}

		//System.out.println(": "+CENA+": "+SKIDKA+": "+VID_SKIDKI+": "+CENA_SO_SKIDKOY+": "+MIN_CENA+": "+MAX_CENA+": "+BASE_PRICE);
		/*
		System.out.println("min: " + zf.getMinimalnayaCena() + " => " + MIN_CENA);
		System.out.println("soskidkoy: " + zf.getCenaSoSkidkoy() + " => " + CENA_SO_SKIDKOY);
		System.out.println("max: " + zf.getMaksimalnayaCena() + " => " + MAX_CENA);
		System.out.println("vidskidki: " + zf.getVidSkidki() + " => " + VID_SKIDKI);
*/
		String warning = "";
		if(CENA_SO_SKIDKOY != zf.getCenaSoSkidkoy()){
			warning = '\n' + "арт. " + artikul + ": " + zf.getCenaSoSkidkoy() + " => " + CENA_SO_SKIDKOY;
		}
		zf.setMinimalnayaCena(MIN_CENA);
		zf.setCenaSoSkidkoy(CENA_SO_SKIDKOY);
		zf.setMaksimalnayaCena(MAX_CENA);
		return warning;
	}

	private void SaveChangesBeforeExit(){
		saveChanges();
		if(dogovorEmpty){
			Auxiliary.warn("Для клиента без договоров заказы сохранять нельзя.", Activity_Bid.this);
			return;
		}
		setResult(RESULT_OK);
		finish();
	}

	double safeFieldDouble(Bough row, String field){
		double r = 0;
		try{
			String s = row.child(field).value.property.value();
			if(s.length() > 0){
				r = Double.parseDouble(s);
			}
		}catch(Throwable t){
			//skip
		}
		return r;
	}

	void ____addSelectedNomenklatura(Bough row){
		//System.out.println(row.dumpXML());
		FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
		String v_IDRRef = "x'" + row.child("_IDRRef").value.property.value().toUpperCase() + "'";
		String vArtikul = row.child("Artikul").value.property.value();
		String vNaimenovanie = row.child("Naimenovanie").value.property.value();
		String vEdinicyIzmereniyaID = "x'" + row.child("EdinicyIzmereniyaID").value.property.value().toUpperCase() + "'";
		String vEdinicyIzmereniyaNaimenovanie = row.child("EdinicyIzmereniyaNaimenovanie").value.property.value();
		double vMIN_NORMA = safeFieldDouble(row, "MinNorma");
		double vKoephphicient = safeFieldDouble(row, "Koephphicient");
		NomenclatureCountHelper helper = new NomenclatureCountHelper(vMIN_NORMA, vKoephphicient);
		double vCOUNT = helper.ReCalculateCount(0);
		double vCENA = safeFieldDouble(row, "Cena");
		double vSKIDKA = safeFieldDouble(row, "Skidka");
		String vVID_SKIDKI = row.child("VidSkidki").value.property.value();
		double vCENA_SO_SKIDKOY = vSKIDKA > 0 ? vSKIDKA : vCENA;
        /*double vCENA_SO_SKIDKOY = getCenaSoSkidkoy(//
                vCENA//
                , safeFieldDouble(row, "Nacenka") //
                , safeFieldDouble(row, "FiksirovannyeCeny")//
                , safeFieldDouble(row, "SkidkaPartneraKarta")//
                , safeFieldDouble(row, "NakopitelnyeSkidki")//
        );*/
		double vMIN_CENA = safeFieldDouble(row, "MinCena");
		double vMAX_CENA = safeFieldDouble(row, "MaxCena");
		double vmin = vMIN_CENA;
		if(!mIsCRAvailable){
			vmin = 0;
		}
        /*double vSKIDKA = getRazmerSkidki(//
                safeFieldDouble(row, "FiksirovannyeCeny")//
                , safeFieldDouble(row, "SkidkaPartneraKarta")//
                , safeFieldDouble(row, "NakopitelnyeSkidki")//
                , safeFieldDouble(row, "Nacenka"));
        String vVID_SKIDKI = getVidSkidki(//
                safeFieldDouble(row, "FiksirovannyeCeny")//
                , safeFieldDouble(row, "SkidkaPartneraKarta")//
                , safeFieldDouble(row, "NakopitelnyeSkidki")//
                , safeFieldDouble(row, "Nacenka"));*/
		double vKOEPHICIENT = safeFieldDouble(row, "Koephphicient");
		double vBASE_PRICE = safeFieldDouble(row, "BasePrice");
		double vLAST_PRICE = safeFieldDouble(row, "LastPrice");
		foodstuffData.newFoodstuff(//
				v_IDRRef//
				, vArtikul//
				, vNaimenovanie//
				, vEdinicyIzmereniyaID//
				, vEdinicyIzmereniyaNaimenovanie//
				, vCOUNT// data.getDoubleExtra(COUNT, 0.00D)//
				, vCENA// data.getDoubleExtra(CENA, 0.00D)//
				, vCENA_SO_SKIDKOY// data.getDoubleExtra(CENA_SO_SKIDKOY, 0.00D)//
				, vmin//MIN_CENA//minCena(_IDRRef, mAppInstance.getCurrentAgent().getAgentIDstr())// MIN_CENA// data.getDoubleExtra(MIN_CENA, 0.00D)//
				, vMAX_CENA// data.getDoubleExtra(MAX_CENA, 0.00D)//
				, vSKIDKA//data.getDoubleExtra(SKIDKA, 0.00D)//
				, vVID_SKIDKI// data.getStringExtra(VID_SKIDKI)//
				, vMIN_NORMA// data.getDoubleExtra(MIN_NORMA, 0.00D)//
				, vKOEPHICIENT//data.getDoubleExtra(KOEPHICIENT, 0.00D)//
				, vBASE_PRICE// data.getDoubleExtra(BASE_PRICE, 0.00D)//
				, vLAST_PRICE// data.getDoubleExtra(LAST_PRICE, 0.00D)//
				//, data.getStringExtra(SKIDKA_PROCENT)//
				//, data.getStringExtra(SKIDKA_NAIMENOVANIE)//
		);
		//System.out.println("1");
	}

	private void ConstructAdditionalTab(){
		//System.out.println("ConstructAdditionalTab");
		((TextView)findViewById(R.id.text_department)).setText(mAppInstance.getCurrentAgent().getPodrazdelenieName());
		mEditComment = (EditText)findViewById(R.id.edit_comment);
		if(mIsOrderEditable){
			//mEditComment.setText(mBidData.getBid().getComment());
			mEditComment.setText(comment2comment(mBidData.getBid().getComment()));
		}else{
			mEditComment.setEnabled(false);
			mEditComment.setFocusable(false);
		}
		mEditCustNum = (EditText)findViewById(R.id.edit_num);
		if(mIsOrderEditable){
			mEditCustNum.setText(comment2num(mBidData.getBid().getComment()));
		}else{
			mEditCustNum.setEnabled(false);
			mEditCustNum.setFocusable(false);
		}
		UpdateOtvetstvennii();
	}

	private void UpdateOtvetstvennii(){
		TextView textSenior = (TextView)findViewById(R.id.text_senior);
		if(mAppInstance.getCurrentAgent() != null){
			textSenior.setText(mAppInstance.getCurrentAgent().getAgentName());
		}else{
			textSenior.setText("");
		}
	}

	@Override
	protected Dialog onCreateDialog(int id){
		//System.out.println("onCreateDialog " + id + ", " + mListPositionForDelete);
		LogHelper.debug(this.getClass().getCanonicalName() + ".onCreateDialog: " + id + ", mListPositionForDelete " + mListPositionForDelete);
		switch(id){
		/*case IDD_DELETE: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirm);
			builder.setMessage(R.string.quest_delete);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					mNomenclatureBasedDocumentItems.Remove(mListPositionForDelete);
					UpdateAfterAddingNomenclature();
					UpdateAvailableAmount();
					mHasChanges = true;
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			return builder.create();
		}
		case IDM_LIST_KartaKlienta:{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirm);
			builder.setMessage("Добавить в карту клиента "+mListPositionForDelete+", "+mNomenclatureBasedDocumentItems.artikul(mListPositionForDelete));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					//
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			return builder.create();
		}*/
			case IDD_SAVE_CHANGES:{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.confirm);
				builder.setMessage(R.string.quest_save_changes);
				builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						if(mBidData.getFoodStuffs().getCount() != 0){
							SaveChangesBeforeExit();
						}else{
							showDialog(IDD_IS_EMPTY);
						}
					}
				});
				builder.setNegativeButton(R.string.not_save, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						//setResult(RESULT_CANCELED, null);
						setResult(RESULT_OK);
						finish();
					}
				});
				return builder.create();
			}
			case IDD_IS_EMPTY:
				return CreateErrorDialog(R.string.msg_empty_order);
			case IDD_PAYMENT_NOT_SELECT:
				return CreateErrorDialog(R.string.msg_payment_type_not_select);
			case IDD_ALREADY_IN_LIST:
				return CreateErrorDialog(R.string.msg_already_in_list);
		}
		return null;
	}

	/*
	private void UpdateAvailableAmount() {
		double orderAmount = mBidData.getFoodStuffs().getAmount() + mBidData.getServices().getAmount();
		mTextAvailableAmount.setText(DecimalFormatHelper.format(mAvailableAmount - orderAmount));
		mTextOrderAmount.setText(DecimalFormatHelper.format(orderAmount));
	}
	*/


	private void UpdateAvailableAmount(){
		//System.out.println("UpdateAvailableAmount start");
		double orderAmount = mBidData.getFoodStuffs().getAmount() + mBidData.getServices().getAmount();
		//double orderWeight = 0;//mBidData.getFoodStuffs().getWeight();
		double orderWeight =mBidData.getFoodStuffs().getWeight();
		//mBidData.getFoodStuffs().
		/*
		for(NomenclatureBasedItem item: mBidData.getFoodStuffs().mNomenclaureList){
			//amount = amount + ((ZayavkaPokupatelya_Foodstaff) item).getSummaSoSkidkoy();
			String artikul = item.getArtikul();
			double ves = getVes(artikul);
			ZayavkaPokupatelya_Foodstaff one = (ZayavkaPokupatelya_Foodstaff)item;
			one.ves = ves;
			orderWeight = orderWeight + one.getKolichestvo() * ves;
		}*/
		//System.out.println("1");
		//double amount14 = mBidData.getFoodStuffs().getAmount(ISklady.KAZAN_sklad_14);
		//System.out.println("2");
		//double amount10 = mBidData.getFoodStuffs().getAmount(ISklady.HORECA_sklad_10);
		//System.out.println("3");
		//double amount8 = mBidData.getFoodStuffs().getAmount(ISklady.HORECA_sklad_8);
		//System.out.println("4");
		mTextAvailableAmount.setText(DecimalFormatHelper.format(mAvailableAmount - orderAmount));
		mTextOrderAmount.setText(//
				DecimalFormatHelper.format(orderWeight)//
						+ "кг, сумма: " + DecimalFormatHelper.format(orderAmount)//
				//+ ", по складам: 14: " + DecimalFormatHelper.format(amount14)//
				//+ ", 10: " + DecimalFormatHelper.format(amount10)//
				//+ ", 8: " + DecimalFormatHelper.format(amount8)//
		);
		//System.out.println("UpdateAvailableAmount done");
	}

	/*private void InitializeExtraChargeInfo() {

		ExtraChargeInfo info = mBidData.getExtraChargeInfo();

	}*/
	private void UpdateBidExtraChargeInfo1234567890(){
		//if(1==1)return;
		//System.out.println(this.getClass().getCanonicalName() + ": UpdateExtraChargeInfo start");
		mBidData.UpdateOrderExtraChargeInfo(DateTimeHelper.SQLDateString(mShippingDate.getTime()));
		ExtraChargeInfo info = mBidData.getExtraChargeInfo();
		//System.out.println(this.getClass().getCanonicalName() + ": UpdateExtraChargeInfo show");
		String status = "";
		TextView textClientPlan = (TextView)findViewById(R.id.text_plan_client);
		/*
		if (info.getClientPlanPersent() != null) {
			status = getString(R.string.plan_client) + "  " + info.getClientPlanPersent();
		}
		else {
			status = getString(R.string.plan_client) + "  " + getString(R.string.not_available);
		}
		*/
		if(info.getOrderFactPersent() != null){
			status = status + ", " + Rstringfact_order + ": " + info.getOrderFactPersent();
		}else{
			status = status + ", " + Rstringfact_order + ": " + Rstringnot_available;
		}
		/*
		double p = ((int) (1000.0 * info.planPodrazdeleniaNaMesiac)) / 10.0;
		status = status + ", План подразд. на мес.: " + p + "%";
		*/
		double p = ((int)(1000.0 * info.nacenkaFactPodrzdelenia)) / 10.0;
		status = status + ", Наценка факт. подразд.: " + p + "%";
		textClientPlan.setText(status);
		//System.out.println(this.getClass().getCanonicalName() + "UpdateExtraChargeInfo done");
	}

	private void SetOrderPropertiesEditable(boolean isOrderPropertiesEditable){
		//System.out.println("SetOrderPropertiesEditable " + isOrderPropertiesEditable);
		try{
			mIsOrderPropertiesEditable = isOrderPropertiesEditable;

			//mEditShippingDate.setEnabled(//mIsOrderEditable);
			//		mIsOrderPropertiesEditable);

			//		mBtnShippingDate.setEnabled(mIsOrderPropertiesEditable);
			mSpnPaymentType.setEnabled(mIsOrderEditable);
			mSpnContracts.setEnabled(mIsOrderEditable);
			//System.out.println("SetOrderPropertiesEditable done");
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	private void ConstructContractsSpinner(String selectContactID){
		//System.out.println("ConstructContractsSpinner " + selectContactID);
		mContractsAdapter = new ContractsAdapter(mDB, this, android.R.layout.simple_spinner_item, mBidData.getClientID());
		mContractsAdapter.setDropDownViewResource(R.layout.spinner_elem);
		mSpnContracts = (Spinner)findViewById(R.id.combo_contract);
		mSpnContracts.setAdapter(mContractsAdapter);
		if(selectContactID.compareToIgnoreCase("x'00'") != 0){
			for(int i = 0; i < mContractsAdapter.getCount(); i++){
				if(((ContractInfo)mContractsAdapter.getItem(i)).getID().toUpperCase().compareToIgnoreCase(selectContactID.toUpperCase()) == 0){
					mSpnContracts.setSelection(i);
					mContractsAdapter.setPosition(i);
				}
			}
		}else{
			mSpnContracts.setSelection(0);
		}
		//if (mIsOrderEditable) {
		mSpnContracts.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3){
				mContractsAdapter.setPosition(position);
				if(!mContractsIsFirstTime){
					mPaymentTypeAdapter.UpdatePaymentTypes(mContractsAdapter.getSelectedItem().getID());
					mPaymentTypeAdapter.notifyDataSetChanged();
					try{
						mSpnPaymentType.setSelection(1);
					}catch(Throwable t){
						t.printStackTrace();
					}
				}else{
					mContractsIsFirstTime = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){
			}
		});
		//}
		//else {
		//	mSpnContracts.setEnabled(false);
		//}
		mSpnContracts.setEnabled(true);
		//System.out.println("done ConstructContractsSpinner");
	}

	void refreshAfterPaymentSelect(){
		//System.out.println("refreshAfterPaymentSelect isEmpty " + mPaymentTypeAdapter.GetSelectedItem().isEmpty());
		//System.out.println("mPaymentTypeAdapter count " + mPaymentTypeAdapter.getCount());
		//System.out.println("getSelectedItemPosition " + mSpnPaymentType.getSelectedItemPosition());
		//boolean removed=mPaymentTypeAdapter.RemoveEmptyItem();
		//System.out.println("removed empty " + removed);
		if(!mPaymentTypeAdapter.GetSelectedItem().isEmpty()){

			//mPaymentTypeAdapter.RemoveEmptyItem();
			if(mPaymentTypeAdapter.RemoveEmptyItem()){
				mSpnPaymentType.setSelection(mSpnPaymentType.getSelectedItemPosition() - 1);
				System.out.println("removed empty");
			}
			//mSpnPaymentType.setSelection(mSpnPaymentType.getSelectedItemPosition()-1);
			//System.out.println("1 getSelectedItemPosition " + mSpnPaymentType.getSelectedItemPosition());
			fillClientHistoryPrompt();
			//System.out.println("2 getSelectedItemPosition " + mSpnPaymentType.getSelectedItemPosition());
			historyFillColumns(true);
			//System.out.println("3 getSelectedItemPosition " + mSpnPaymentType.getSelectedItemPosition());
			gridHistory.refresh();
			//System.out.println("4 getSelectedItemPosition " + mSpnPaymentType.getSelectedItemPosition());
			//}
		}
		System.out.println("refreshAfterPaymentSelect");
	}

	private void ConstructPaymentTypeSpinner(String contactID, String selection){
		mPaymentTypeAdapter = new PaymentTypeAdapter(mDB, this, android.R.layout.simple_spinner_item, contactID);
		mPaymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpnPaymentType = (Spinner)findViewById(R.id.combo_payment);
		mSpnPaymentType.setAdapter(mPaymentTypeAdapter);
		mSpnPaymentType.setSelection(mPaymentTypeAdapter.GetItemPositionByID(selection));
		//if (mIsOrderEditable) {
		mSpnPaymentType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3){
				//System.out.println("ConstructPaymentTypeSpinner.onItemSelected");
				try{
					refreshAfterPaymentSelect();
					/*if (!mPaymentTypeAdapter.GetSelectedItem().isEmpty()) {
						if (mPaymentTypeAdapter.RemoveEmptyItem()) {
							mSpnPaymentType.setSelection(mSpnPaymentType.getSelectedItemPosition() - 1);
							fillClientHistoryPrompt();
							historyFillColumns(true);
							gridHistory.refresh();
						}
					}*/
				}catch(Throwable t){
					t.printStackTrace();
					Auxiliary.warn(t.toString(), Activity_Bid.this);
				}
				//requeryHistoryData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){
			}
		});
		//}
		//else {
		//	mSpnPaymentType.setEnabled(false);
		//}
		/*mSpnPaymentType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (mSpnPaymentType.getSelectedItemPosition() > 0) {
					if (mIsOrderEditable) {
						//System.out.println("fire");
						int ww = Auxiliary.screenWidth(Activity_Bid.this);
						slr.split.is(ww * 0.7);
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//
			}
		});*/
		mSpnPaymentType.setEnabled(true);
	}

	private void UpdateAfterAddingNomenclature(){
		//sortListByMode(this.sortMode);
		//System.out.println("UpdateAfterAddingNomenclature start");
		//System.out.println("1");
		((ZoomListArrayAdapter)mFoodstuffList.getAdapter()).notifyDataSetChanged();
		((ZoomListArrayAdapter)mServicesList.getAdapter()).notifyDataSetChanged();
		((ZoomListArrayAdapter)mTrafiksList.getAdapter()).notifyDataSetChanged();
		//System.out.println("2");
		SetOrderPropertiesEditable(false);
		//System.out.println("3");
		UpdateAvailableAmount();
		//System.out.println("4");
		warnEmptyEdizm();
		mHasChanges = true;
		this.saveChanges();
		//System.out.println("UpdateAfterAddingNomenclature done");
	}

	void warnEmptyEdizm(){
		ZoomListArrayAdapter zlaa = ((ZoomListArrayAdapter)mFoodstuffList.getAdapter());
	}

	@Override
	public void onTabChanged(String tabSpec){
		layoutless.setVisibility(View.INVISIBLE);
		if(tabSpec.compareToIgnoreCase("tab_foodstuffs") == 0){
			mNomenclatureBasedDocumentItems = mBidData.getFoodStuffs();
			mBtnNomenclature.setText(R.string.nomenclature);
			mBtnNomenclature.setVisibility(Button.VISIBLE);
			mBtnArticle.setVisibility(Button.VISIBLE);
			layoutless.setVisibility(View.VISIBLE);

		}else{
			if(tabSpec.compareToIgnoreCase("tab_services") == 0){
				mNomenclatureBasedDocumentItems = mBidData.getServices();
				mBtnNomenclature.setText(R.string.services);
				mBtnNomenclature.setVisibility(Button.VISIBLE);
				mBtnArticle.setVisibility(Button.INVISIBLE);
			}else{
				if(tabSpec.compareToIgnoreCase("tab_trafik") == 0){
					mNomenclatureBasedDocumentItems = mBidData.getTrafiks();
					mBtnNomenclature.setText(R.string.trafik);
					mBtnNomenclature.setVisibility(Button.VISIBLE);
					mBtnArticle.setVisibility(Button.INVISIBLE);
					//mBtnArticle.setText("Справка");
				}else{
					mBtnNomenclature.setVisibility(Button.INVISIBLE);
					mBtnArticle.setVisibility(Button.INVISIBLE);
				}
			}
		}
	}
/*
	void promptRecomendationItems() {
		//promptImport(artikuls, names);
		Auxiliary.pick3Choice(this, "Дополнительно", "Показать рекомендации?", "Показать", new Task() {
			@Override
			public void doTask() {
				promptRecomendatciiSelection();
			}
		}, "Завершить", new Task() {
			@Override
			public void doTask() {
				Activity_Bid.this.finish();
				if (mHasChanges) saveChanges();
			}
		}, null, null);
	}*/

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		/*
		if (keyCode == KeyEvent.KEYCODE_BACK & mHasChanges & mBidData.getFoodStuffs().getCount() != 0) {
			showDialog(IDD_SAVE_CHANGES);
			//promptMoreItems();
			return true;
		}
		return super.onKeyDown(keyCode, event);
		*/
		if(keyCode == KeyEvent.KEYCODE_BACK & mIsOrderEditable & mBidData.getFoodStuffs().getCount() != 0){
			//promptRecomendationItems();
			if(mHasChanges && (!(nomerDokumenta1C.length() > 1))){
				promptRecomendatciiSelection();
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}else{
			if(mHasChanges)
				saveChanges();
			return super.onKeyDown(keyCode, event);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("onActivityResult " + requestCode + "/" + resultCode);
		if(this.mIsOrderEditable){
			if(resultCode == RESULT_OK){
				switch(requestCode){
					case ADD_NOMENCATURE:
						FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
						if(foodstuffData.IsFoodstuffAlreadyInList(data.getStringExtra(NOMENCLATURE_ID))){
							showDialog(IDD_ALREADY_IN_LIST);
							return;
						}
						System.out.println("add onActivityResult " + data.getStringExtra(ARTIKUL)
								+ ", " + data.getDoubleExtra(SKIDKA, 0.00D)
								+ ", " + data.getStringExtra(VID_SKIDKI)
								+ ", " + data.getDoubleExtra(CENA_SO_SKIDKOY, 0.00D));
						foodstuffData.newFoodstuff(//
								data.getStringExtra(NOMENCLATURE_ID)//
								, data.getStringExtra(ARTIKUL)//
								, data.getStringExtra(NAIMENOVANIE)//
								, data.getStringExtra(EDINICY_IZMERENIYA_ID)//
								, data.getStringExtra(EDINICY_IZMERENIYA_NAIMENOVANIE)//
								, data.getDoubleExtra(COUNT, 0.00D)//
								, data.getDoubleExtra(CENA, 0.00D)//
								, data.getDoubleExtra(CENA_SO_SKIDKOY, 0.00D)//
								, data.getDoubleExtra(MIN_CENA, 0.00D)//
								, data.getDoubleExtra(MAX_CENA, 0.00D)//
								, data.getDoubleExtra(SKIDKA, 0.00D)//
								, data.getStringExtra(VID_SKIDKI)//
								, data.getDoubleExtra(MIN_NORMA, 0.00D)//
								, data.getDoubleExtra(KOEPHICIENT, 0.00D)//
								, data.getDoubleExtra(BASE_PRICE, 0.00D)//
								, data.getDoubleExtra(LAST_PRICE, 0.00D)//
								//, data.getStringExtra(SKIDKA_PROCENT)//
								//, data.getStringExtra(SKIDKA_NAIMENOVANIE)//
						);
						mShowEditCountAndPricePopup = true;
						break;
					case ADD_SERVICE:
						mBidData.getServices().newService(data.getStringExtra(NOMENCLATURE_ID), data.getStringExtra(ARTIKUL), data.getStringExtra(NAIMENOVANIE), data.getStringExtra(SODERGANIE), data.getDoubleExtra(CENA, 0), data.getDoubleExtra(KOLICHESTVO, 0));
						break;
					case ADD_TRAFIK:
						mBidData.getTrafiks().newTrafik(data.getStringExtra(NOMENCLATURE_ID), data.getStringExtra(ARTIKUL), data.getStringExtra(NAIMENOVANIE), data.getStringExtra(EDINICY_IZMERENIYA_ID), data.getStringExtra(EDINICY_IZMERENIYA_NAIMENOVANIE), data.getDoubleExtra(COUNT, 0.00D), data.getDoubleExtra(MIN_NORMA, 0.00D), data.getDoubleExtra(KOEPHICIENT, 0.00D), mShippingDate.getTime(), data.getStringExtra(KOMMENTARIY));
						break;
				}
				UpdateAfterAddingNomenclature();
			}
			//UpdateExtraChargeInfo();
			//updateExtraChargeInfo.start();
			clearExtraChargeInfo();
			//updateExtraChargeInfoTask.start();
			//System.out.println("onActivityResult done");
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		if(hasFocus && mShowEditCountAndPricePopup){
			mShowEditCountAndPricePopup = false;
			Popup_EditNomenclatureCountPrice popup = new Popup_EditNomenclatureCountPrice(mBtnArticle, mOnPopupClose, mBidData.getFoodStuffs().getFoodstuff(0)
					, mShippingDate.getTimeInMillis());
			popup.showLikeQuickAction();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, view, menuInfo);
		//System.out.println("onCreateContextMenu");
		menu.add(Menu.NONE, IDM_LIST_DELETE, Menu.NONE, R.string.delete);
		//menu.add(Menu.NONE, IDM_LIST_KartaKlienta, Menu.NONE, "Добавить в карту клиента");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem menu){
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)menu.getMenuInfo();
		mListPositionForDelete = menuInfo.position;
		//System.out.println("onContextItemSelected " + mListPositionForDelete);
		switch(menu.getItemId()){
			case IDM_LIST_DELETE:
				//System.out.println("delete record?");
				//showDialog(IDD_DELETE);
				showDialogDelete();
				break;
			case IDM_LIST_KartaKlienta:
				//AdapterView.AdapterContextMenuInfo menuInfo2 = (AdapterView.AdapterContextMenuInfo) menu.getMenuInfo();
				//mListPositionForDelete = menuInfo2.position;
				//showDialog(IDM_LIST_KartaKlienta);
				showDialogKartaKlienta();
				break;
		}
		return true;
	}

	void showDialogDelete(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Строка " + mListPositionForDelete);//R.string.confirm);
		builder.setMessage(R.string.quest_delete);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				mNomenclatureBasedDocumentItems.Remove(mListPositionForDelete);
				UpdateAfterAddingNomenclature();
				UpdateAvailableAmount();
				mHasChanges = true;
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				dialog.dismiss();
			}
		});
		//return builder.create();
		builder.create().show();
	}

	void sendKartaKlienta(final String artikul, final String klientKod){
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/KartaKlienta\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Zagruzit>"//
				+ "\n	      <ns1:Dok>"//
				+ "\n	        <ns1:Nazvanie>" + klientKod + "</ns1:Nazvanie>"//
				+ "\n	        <ns1:Kommentarii>" + artikul + "</ns1:Kommentarii>"//
				//+ "\n	        <ns1:UIN>47bb13b8-2656-11e6-83ad-3cd92b037e6c</ns1:UIN>"//
				+ "\n	        <ns1:UIN></ns1:UIN>"//
				+ "\n	        <ns1:Vlad>"//
				+ "\n	          <ns1:Kod>" + klientKod + "</ns1:Kod>"//
				+ "\n	        </ns1:Vlad>"//
				+ "\n	        <ns1:Tov>"//
				+ "\n	          <ns1:Art>" + artikul + "</ns1:Art>"//
				+ "\n	        </ns1:Tov>"//
				+ "\n	      </ns1:Dok>"//
				+ "\n	    </ns1:Zagruzit>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>";
		final RawSOAP rawSOAP = new RawSOAP();
		rawSOAP.url.is(Settings.getInstance().getBaseURL() + "KartaKlienta.1cws")//
				.responseEncoding.is("cp-1251")//
				.xml.is(xml)//
				.timeout.is(3 * 60 * 1000)//
		;
		Report_Base.startPing();
		//rawSOAP.startNow();
		rawSOAP.afterError.is(new Task(){
			@Override
			public void doTask(){
				String txt = "" + rawSOAP.statusCode.property.value() + rawSOAP.statusDescription.property.value() + rawSOAP.exception.property.value().toString();
				Auxiliary.warn("Ошибка: " + txt, Activity_Bid.this);
			}
		});
		rawSOAP.afterSuccess.is(new Task(){
			@Override
			public void doTask(){
				if(rawSOAP.statusCode.property.value() >= 100 && rawSOAP.statusCode.property.value() <= 300//
				){
					String txt = rawSOAP.data//.dumpXML();
							.child("soap:Body")//
							.child("m:ZagruzitResponse")//
							.child("m:return")//
							.value.property.value();
					//System.out.println(rawSOAP.data.dumpXML());
					if(txt.startsWith("ок")){
						Auxiliary.warn("Выгружено", Activity_Bid.this);
						String key = txt.substring(3);
						//System.out.println("key " + key);
						String sql = "insert into KartaKlientaDok (UIN) values ('" + key + "')";
						mDB.execSQL(sql);
						sql = "select _id from KartaKlientaDok where UIN='" + key + "';";
						Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
						String _id = b.child("row").child("_id").value.property.value();
						sql = "insert into KartaKlientaNomenklatura (docId,artikul) values (" + _id + ",'" + artikul + "')";
						mDB.execSQL(sql);
						sql = "insert into KartaKlientaKlient (docId,kod) values (" + _id + ",'" + klientKod + "')";
						mDB.execSQL(sql);
					}else{
						Auxiliary.warn("Результат: " + txt, Activity_Bid.this);
					}
				}
			}
		});
		rawSOAP.startLater(this, "Выгрузка", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
		if(rawSOAP.statusCode.property.value() >= 100 //
				&& rawSOAP.statusCode.property.value() <= 300//
				&& rawSOAP.exception.property.value() == null//
				&& rawSOAP.data != null//
		){
			//txt = extractResult(rawSOAP.data);
			//System.out.println(rawSOAP.data.dumpXML());
			/*String string64 = rawSOAP.data.child("soap:Body")//
					.child("m:getReportResponse")//
					.child("m:return")//
					.child("m:Data")//
			.value.property.value();
			byte[] byte64=Base64.decode(string64, Base64.DEFAULT);
			FileOutputStream fileOutputStream = null;

			 fileOutputStream = new FileOutputStream(to);
			 fileOutputStream.write(byte64, 0, byte64.length);

			 fileOutputStream.close();
			 System.out.println("wrote "+byte64);
			 */
		}else{/*
				txt = "<p>Error<p><pre>"//
					+ "\nstatus: " + rawSOAP.statusCode.property.value()//
					+ "\ndescription: " + rawSOAP.statusDescription.property.value() //
					+ "\ndata: " + rawSOAP.data;
				if (rawSOAP.exception.property.value() != null) {
				txt = txt + "\nexception: " + rawSOAP.exception.property.value().toString();
				}
				txt = txt + "</pre>";
				Auxiliary.writeTextToFile(new File(to), txt, "utf-8");
				*/
		}
	}

	void showDialogKartaKlienta(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		builder.setMessage("Добавить в карту клиента артикул " + mNomenclatureBasedDocumentItems.artikul(mListPositionForDelete));
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				sendKartaKlienta(mNomenclatureBasedDocumentItems.artikul(mListPositionForDelete), mAppInstance.getClientInfo().getKod());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				dialog.dismiss();
			}
		});
		//return builder.create();
		builder.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuOtchety = menu.add("Отчёты");
		menuDostavka = menu.add("Доставка");
		menuDelete = menu.add("Удалить заказ покупателя");
		menuExport = menu.add("Экспорт в файл");
		menuImport = menu.add("Импорт");
		menuFromSpecificacia = menu.add("Заполнить из спецификаций");
		menuShowHideStatus = menu.add("Показать наценку");
		kommercheskoePredlojenie = menu.add("Коммерческое предложение");
		menuRequestMailList = menu.add("Выслать шаблон заказа и выбранное");
		//menuListovkaOtdelaProdazh = menu.add("Листовка отдела продаж");
		menuKartochkaKlienta = menu.add("Карточка клиента");
		menuChangeDateRecalculate = menu.add("Сменить дату отгрузки и пересчитать цены");
		menuRecepty = menu.add("Рецепты");
		return true;
	}

	void promptDostavka(){
		final Numeric selection = new Numeric().value(mBidData.getBid().dostavkaKind);
		final Numeric dostvkaVozvrNakl = new Numeric().value(mBidData.getBid().dostvkaVozvrNakl);
		final Note komentariy = new Note().value(mBidData.getBid().dostvkaKoment);
		Auxiliary.pick(this, "Доставка"//
				, new SubLayoutless(this)//
						.child(new RedactSingleChoice(this)//
								.item("Обычная доставка")//
								.item("Самовывоз (доверенность)")//
								.item("Самовывоз (печать)")//
								.selection.is(selection)//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 0.1).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new Decor(this).labelText.is("комментарий к самовывозу").labelAlignLeftTop().labelStyleSmallNormal()//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 1.1).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new RedactText(this).text.is(komentariy).singleLine.is(true)//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new Decor(this).labelText.is("дата возврата накладной при самовывозе").labelAlignLeftTop().labelStyleSmallNormal()//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 2.6).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(dostvkaVozvrNakl)//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 3).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 4)//
				, "Сохранить", new Task(){
					@Override
					public void doTask(){
						//System.out.println("save " + selection.value() + ": " + komentariy.value());
						mBidData.getBid().dostavkaKind = selection.value().intValue();
						mBidData.getBid().dostvkaKoment = komentariy.value();
						mBidData.getBid().dostvkaVozvrNakl = dostvkaVozvrNakl.value();
						resetTitle();

					}
				}, null, null, null, null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		if(item == menuDelete){
			doMenuDelete();
		}
		if(item == menuDostavka){
			promptDostavka();
		}
		if(item == menuExport){
			doMenuExport();
		}
		if(item == menuImport){
			doMenuImport();
		}
		if(item == menuFromSpecificacia){
			doListFromSpecificacia();
		}

		if(item == menuOtchety){
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if(item == menuShowHideStatus){
			TextView textClientPlan = (TextView)findViewById(R.id.text_plan_client);
			if(hideNacenkaStatus){
				hideNacenkaStatus = false;
				menuShowHideStatus.setTitle("Спрятать наценку");
				textClientPlan.setVisibility(textClientPlan.VISIBLE);
				clearExtraChargeInfo();
				//updateExtraChargeInfoTask.start();
			}else{

				hideNacenkaStatus = true;
				menuShowHideStatus.setTitle("Показать наценку");
				textClientPlan.setVisibility(textClientPlan.INVISIBLE);
			}
			((ZoomListArrayAdapter)mFoodstuffList.getAdapter()).notifyDataSetChanged();
		}
		if(item == kommercheskoePredlojenie){
			doKommercheskoePredlojenie();
			return true;
		}
		if(item == menuRequestMailList){
			promptSendTemplate();
			return true;
		}
		if(item == menuKartochkaKlienta){

			doKartochkaKlienta();
			return true;
		}
		if(item == menuRecepty){
			if(!mIsOrderEditable){
				Auxiliary.warn("Заказ больше нельзя редактировать", this);
			}else{
				old_promptReceipt();
			}
			return true;
		}

		if(item == menuChangeDateRecalculate){
			promptChangeDateRecalculate();
		}
        /*if (item == menuListovkaOtdelaProdazh) {
            promptListovkaOtdelaProdazh();
            return true;
        }*/

		return true;
	}

	void promptChangeDateRecalculate(){
		if(!mIsOrderEditable){
			Auxiliary.warn("Заказ больше нельзя редактировать", this);
		}else{
			if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
				showDialog(IDD_PAYMENT_NOT_SELECT);
			}else{
				//System.out.println("start menuChangeDateRecalculate");
				//Number tt=new Numeric().value(mShippingDate.getTime());
				final Numeric result = new Numeric();
				Auxiliary.pickDate(this, mShippingDate, result, new Task(){
					public void doTask(){
						changeDateRecalculate(result.value().longValue());

					}
				});
			}
		}
	}

	void doKartochkaKlienta(){
		if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
			showDialog(IDD_PAYMENT_NOT_SELECT);

		}else{
			Intent intent = new Intent();
			intent.setClass(this, ActivityKartochkaKlienta.class);
			//startActivity(intent);
			String clientID = "0";
			String polzovatelID = "0";
			String dataOtgruzki = "0";
			String sklad = "0";
			try{
				clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
				dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
				sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			}catch(Throwable ttt){
				ttt.printStackTrace();
			}

			intent.putExtra("clientID", clientID);
			intent.putExtra("dataOtgruzki", dataOtgruzki);
			intent.putExtra("polzovatelID", polzovatelID);
			intent.putExtra("sklad", sklad);

			startActivityForResult(intent, ADD_NOMENCATURE);
		}
	}

	void promptListovkaOtdelaProdazh(){
		//http://89.109.7.162/GolovaNew/hs/Prilozhenie/ПревьюФотоНоменклатуры?Артикул=2838
		Intent intent = new Intent();
		intent.setClass(Activity_Bid.this, sweetlife.android10.supervisor.Activity_Listovka.class);


		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}

		intent.putExtra("clientID", clientID);
		intent.putExtra("dataOtgruzki", dataOtgruzki);
		intent.putExtra("polzovatelID", polzovatelID);
		intent.putExtra("sklad", sklad);

		startActivityForResult(intent, ADD_NOMENCATURE);
	}

	void promptSendTemplate(){
		System.out.println("promptSendTemplate");
		Vector<String> artikul = new Vector<String>();
		FoodstuffsData foodstuffsData = mBidData.getFoodStuffs();
		for(int i = 0; i < foodstuffsData.getCount(); i++){
			ZayavkaPokupatelya_Foodstaff zayavkaPokupatelya_Foodstaff = foodstuffsData.getFoodstuff(i);
			artikul.add(zayavkaPokupatelya_Foodstaff.getArtikul());
			System.out.println("artikul " + zayavkaPokupatelya_Foodstaff.getArtikul());

		}

		Activity_BidsContractsEtc_2.promptRequestMailList(this, artikul);
	}

	void doMenuDelete(){
		if(nomerDokumenta1C.length() > 1){
			Auxiliary.warn("Заказ уже выгружен", this);
		}else{
			Auxiliary.pick3Choice(this, "Удаление заказа из планшета", "Вы уверены?", "Удалить", new Task(){
				@Override
				public void doTask(){
					try{
						//mNomenclatureBasedDocumentItems
						//ZayavkaPokupatelyaIskhodyaschaya
						String s = "delete from ZayavkaPokupatelyaIskhodyaschaya where _idrref=" + mBidData.getBid().getIDRRef();
						//System.out.println(s);
						mDB.execSQL(s);
						String sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Smvz where parent=" + mBidData.getBid().getIDRRef();
						mDB.execSQL(sql);
						setResult(RESULT_OK);
						finish();
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
			}, "Отмена", null, "", null);
		}

	}

	/*public static void createNewOrder(Bough data, Context context) {
		System.out.println(data.dumpXML());
	}*/
	void cloneOrder(Vector<Bough> tovari, FoodstuffsData foodstuffData){

		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}
		for(int kk = 0; kk < tovari.size(); kk++){
			Bough existed = tovari.get(kk);
			System.out.println("cloneOrder tovari " + existed.dumpXML());
			try{
				String sql = Request_NomenclatureBase.composeSQL(//
						dataOtgruzki//
						, clientID//
						, polzovatelID//
						, ""//
						, ""//
						, existed.child("Артикул").value.property.value().trim()
						, ISearchBy.SEARCH_ARTICLE//
						, false//
						, false//
						, sklad//
						, 200//
						, 0, false, false, false, null, null
						, this.no_assortiment);
				Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				Bough found = bb.child("row");
				//System.out.println(sql);
				//System.out.println(bb.dumpXML());
				if(existed.child("Артикул").value.property.value().trim().equals(found.child("Artikul").value.property.value().trim())){
					//System.out.println("found " + found.child("Artikul" ).value.property.value());
					double CENA = Numeric.string2double(found.child("Cena").value.property.value());
					double SKIDKA = Numeric.string2double(found.child("Skidka").value.property.value());
					String VID_SKIDKI = found.child("VidSkidki").value.property.value();
					//System.out.println("found " + kk + ": " + found.child("Naimenovanie").value.property.value() + ": " + VID_SKIDKI);
					double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
					if(SKIDKA == 0 || VID_SKIDKI.toUpperCase().trim().equals("ЦР")){
						if(existed.child("ЦР").value.property.value().equals("true")){
							//System.out.println("ЦР 1");
							if((Numeric.string2double(found.child("MinCena").value.property.value()) <= Numeric.string2double(existed.child("Цена").value.property.value()))
									&& (Numeric.string2double(found.child("MinCena").value.property.value()) > 0)
							){
								//System.out.println("ЦР 2");
								VID_SKIDKI = "x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";
								CENA_SO_SKIDKOY = Numeric.string2double(existed.child("Цена").value.property.value());
							}
						}
						//System.out.println("ЦР 3");
					}

					foodstuffData.newFoodstuff(//
							"x'" + found.child("_IDRRef").value.property.value() + "'"//
							, found.child("Artikul").value.property.value().trim()//
							, found.child("Naimenovanie").value.property.value()//
							, "x'" + found.child("EdinicyIzmereniyaID").value.property.value() + "'"//
							, found.child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
							, Numeric.string2double(existed.child("Количество").value.property.value())//
							, CENA//
							, CENA_SO_SKIDKOY//
							, Numeric.string2double(found.child("MinCena").value.property.value())//
							, Numeric.string2double(found.child("MaxCena").value.property.value())//
							, SKIDKA//
							, VID_SKIDKI//
							, Numeric.string2double(found.child("MinNorma").value.property.value())//
							, Numeric.string2double(found.child("Koephphicient").value.property.value())//
							, Numeric.string2double(found.child("BasePrice").value.property.value())//
							, Numeric.string2double(found.child("LastPrice").value.property.value())//
					);
				}
			}catch(Throwable t){
				t.printStackTrace();
				Auxiliary.warn("" + t.getMessage(), this);
			}
		}
		mHasChanges = true;
		mIsOrderPropertiesEditable = false;

	}

	void cloneOrder22222(Vector<Bough> tovari, FoodstuffsData foodstuffData){
		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}
		String custom = "1=0";
		for(int i = 0; i < tovari.size(); i++){
			custom = custom + " or n.[Artikul] = '" + tovari.get(i).child("Артикул").value.property.value() + "'";
			//System.out.println("t "+i+": "+tovari.get(i).dumpXML());
		}
		String sql = Request_NomenclatureBase.composeSQL(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, custom//
				, ISearchBy.SEARCH_CUSTOM//
				, false//
				, false//
				, sklad//
				, 200//
				, 0, false, false, false, null, null, false);
		final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//System.out.println("dump "+b.dumpXML());
		//tovari.get(0).child("Артикул").value.property.value("123123123");
		for(int i = 0; i < tovari.size(); i++){
			System.out.println("cloneOrder tovari " + tovari.get(i).child("Артикул").value.property.value().trim());
			for(int n = 0; n < b.children.size(); n++){
				if(tovari.get(i).child("Артикул").value.property.value().trim()
						.equals(b.children.get(n).child("Artikul").value.property.value().trim())){
					//System.out.println("found " + b.children.get(n).dumpXML());
					double CENA = Numeric.string2double(b.children.get(n).child("Cena").value.property.value());
					double SKIDKA = Numeric.string2double(b.children.get(n).child("Skidka").value.property.value());
					String VID_SKIDKI = b.children.get(n).child("VidSkidki").value.property.value();

					System.out.println("" + i + ": " + b.children.get(n).child("Naimenovanie").value.property.value() + ": " + VID_SKIDKI);
					double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
					if(SKIDKA == 0){
						if(tovari.get(i).child("ЦР").value.property.value().equals("true")){
							//SKIDKA = Numeric.string2double(tovari.get(i).child("Цена").value.property.value());
							if((Numeric.string2double(b.children.get(n).child("MinCena").value.property.value())
									<= Numeric.string2double(tovari.get(i).child("Цена").value.property.value())
							) && (Numeric.string2double(b.children.get(n).child("MinCena").value.property.value()) > 0)
							){
								VID_SKIDKI = "x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";
								CENA_SO_SKIDKOY = Numeric.string2double(tovari.get(i).child("Цена").value.property.value());
							}
						}
					}
					foodstuffData.newFoodstuff(//
							"x'" + b.children.get(n).child("_IDRRef").value.property.value() + "'"//
							, b.children.get(n).child("Artikul").value.property.value().trim()//
							, b.children.get(n).child("Naimenovanie").value.property.value()//
							, "x'" + b.children.get(n).child("EdinicyIzmereniyaID").value.property.value() + "'"//
							, b.children.get(n).child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
							, Numeric.string2double(tovari.get(i).child("Количество").value.property.value())//
							, CENA//
							, CENA_SO_SKIDKOY//
							, Numeric.string2double(b.children.get(n).child("MinCena").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("MaxCena").value.property.value())//
							, SKIDKA//
							, VID_SKIDKI//
							, Numeric.string2double(b.children.get(n).child("MinNorma").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("Koephphicient").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("BasePrice").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("LastPrice").value.property.value())//
					);
					break;
				}
			}
		}
		mHasChanges = true;
		mIsOrderPropertiesEditable = false;
	}

	void doMenuImport(){
		if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
			showDialog(IDD_PAYMENT_NOT_SELECT);
			return;
		}
		try{
			ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			String pasteData = "";
			ClipData clipData = clipboard.getPrimaryClip();
			ClipData.Item item = clipData.getItemAt(0);
			pasteData = item.getText().toString();
			//System.out.println("pasteData: " + pasteData);
			String[] t = pasteData.split("\n");
			java.util.Vector<java.util.Vector<String>> rows = new java.util.Vector<java.util.Vector<String>>();
			for(int i = 0; i < t.length; i++){
				//System.out.println(i + ": " + t[i]);
				String[] r = t[i].split("\t");
				if(r != null){
					java.util.Vector<String> row = new java.util.Vector<String>();
					for(int k = 0; k < r.length; k++){
						row.add(r[k]);
						//System.out.println(i + "/" + k + ": " + r[k]);
					}
					rows.add(row);
				}
			}
			final String[] artikuls = new String[rows.size()];
			final double[] kolichestvos = new double[rows.size()];
			CharSequence[] items = new String[rows.size()];
			final They<Integer> defaultSelection = new They<Integer>();
			String clientID = "0";
			String polzovatelID = "0";
			String dataOtgruzki = "0";
			String sklad = "0";
			try{
				clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
				dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
				sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			}catch(Throwable ttt){
				ttt.printStackTrace();
			}
			for(int i = 0; i < rows.size(); i++){
				String artikul = "";
				String name = "";
				String kolvo = "";
				double kolichestvo = 0;
				//String kolichestvo = "";
				//double cnt = 1.0;
				if(rows.get(i).size() > 0){
					artikul = rows.get(i).get(0);
				}
				if(rows.get(i).size() > 1){
					name = rows.get(i).get(1);
				}
				if(rows.get(i).size() > 2){
					kolvo = rows.get(i).get(2);
				}
				kolichestvo = Numeric.string2double(kolvo.replace(',', '.').replaceAll("[^\\d.]", ""));
				if(kolichestvo <= 0){
					kolichestvo = 1;
				}
				/*if (rows.get(i).size() > 2) {
					kolichestvo = rows.get(i).get(2);
					try {
						cnt = Double.parseDouble(kolichestvo);
					}
					catch (Throwable e) {
						//e.printStackTrace();
					}
				}*/
				artikuls[i] = artikul.trim();
				kolichestvos[i] = kolichestvo;
				items[i] = artikul + ": " + name + ": " + kolichestvo;
			}
			String custom = "1=0";
			for(int i = 0; i < artikuls.length; i++){
				if(artikuls[i].trim().length() > 3){
					custom = custom + " or n.[Artikul] = '" + artikuls[i] + "'";
				}
			}
			System.out.println("compose 3:");
			String sql = Request_NomenclatureBase.composeSQL(//
					dataOtgruzki//
					, clientID//
					, polzovatelID//
					, ""//
					, ""//
					, custom//
					, ISearchBy.SEARCH_CUSTOM//
					, false//
					, false//
					, sklad//
					, 200//
					, 0, false, false, false, null, null, false);
			final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
			for(int i = 0; i < artikuls.length; i++){
				String a = artikuls[i].trim();
				for(int n = 0; n < b.children.size(); n++){
					String f = b.children.get(n).child("Artikul").value.property.value().trim();
					if(f.equals(a)){
						defaultSelection.insert(defaultSelection.size(), i);
						break;
					}
				}
			}
			Auxiliary.pickMultiChoice(this, items, defaultSelection, "ok", new Task(){
				@Override
				public void doTask(){
					FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
					for(int i = 0; i < artikuls.length; i++){
						boolean checked = false;
						for(int ds = 0; ds < defaultSelection.size(); ds++){
							if(defaultSelection.at(ds) == i){
								checked = true;
								break;
							}
						}
						if(checked){
							//String a = artikuls[i].trim();
							for(int n = 0; n < b.children.size(); n++){
								String f = b.children.get(n).child("Artikul").value.property.value().trim();
								if(f.equals(artikuls[i])){
									double CENA = Numeric.string2double(b.children.get(n).child("Cena").value.property.value());
									final double SKIDKA = Numeric.string2double(b.children.get(n).child("Skidka").value.property.value());
									final String VID_SKIDKI = b.children.get(n).child("VidSkidki").value.property.value();
									final double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
                                    /*double CENA_SO_SKIDKOY = getCenaSoSkidkoy(//
                                            CENA//
                                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                                    );
                                    double SKIDKA = getRazmerSkidki(//
                                            Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                                    );
                                    String VID_SKIDKI = getVidSkidki(//
                                            Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                                    );*/
									foodstuffData.newFoodstuff(//
											"x'" + b.children.get(n).child("_IDRRef").value.property.value() + "'"//
											, artikuls[i]//
											, b.children.get(n).child("Naimenovanie").value.property.value()//
											, "x'" + b.children.get(n).child("EdinicyIzmereniyaID").value.property.value() + "'"//
											, b.children.get(n).child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
											, kolichestvos[i]//Numeric.string2double(b.children.get(n).child("MinNorma").value.property.value())//
											, CENA//
											, CENA_SO_SKIDKOY//
											, Numeric.string2double(b.children.get(n).child("MinCena").value.property.value())//
											, Numeric.string2double(b.children.get(n).child("MaxCena").value.property.value())//
											, SKIDKA//
											, VID_SKIDKI//
											, Numeric.string2double(b.children.get(n).child("MinNorma").value.property.value())//
											, Numeric.string2double(b.children.get(n).child("Koephphicient").value.property.value())//
											, Numeric.string2double(b.children.get(n).child("BasePrice").value.property.value())//
											, Numeric.string2double(b.children.get(n).child("LastPrice").value.property.value())//
									);
									break;
								}
							}
						}
					}
					UpdateAfterAddingNomenclature();
				}
			});
		}catch(Throwable t){
			t.printStackTrace();
			Auxiliary.warn("Не скопированы данные для импорта", this);
		}
	}

	void doListFromSpecificacia(){
		if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
			showDialog(IDD_PAYMENT_NOT_SELECT);
			return;
		}
		String kodKlienta = ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
		String dataOtgruzki = DateTimeHelper.dateYYYYMMDD(ApplicationHoreca.getInstance().getShippingDate().getTime());
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/InfoForZakaz/" + kodKlienta + "/" + dataOtgruzki;

		System.out.println(url);
		//final Note result = new Note();
		final Vector<String> rows = new Vector<String>();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] raw = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(raw, "UTF-8");
					Bough res = Bough.parseJSON(txt);
					Vector<Bough> specs = res.children("Спецификация");
					for(int i = 0; i < specs.size(); i++){
						String spe = specs.get(i).value.property.value().trim();
						if(spe.length() > 3){
							rows.add(spe);
						}
					}
					System.out.println(res.dumpXML());
				}catch(Exception e){
					e.printStackTrace();
					//result.value(e.getMessage());
					//Auxiliary.warn("",Activity_Bid.this);
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//Vector<Bough> specs=res.children("Спецификация");
				showListFromSpecificacia(rows);
			}
		}).status.is("Получение спецификаций...").start(this);
		//showListFromSpecificacia();
	}

	void showListFromSpecificacia(final Vector<String> artikuls){
		final Note status = new Note().value("Подождите...");
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				addListFromSpecificacia(artikuls, status);
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				UpdateAfterAddingNomenclature();
			}
		}).status.is(status).start(this);
	}

	void addListFromSpecificacia(Vector<String> artikuls, Note status){
        /*
        final String[] artikuls = new String[1];
        final double[] kolichestvos = new double[1];
        CharSequence[] items = new String[1];

        artikuls[0] = "100533";
        kolichestvos[0] = 1;
        items[0] = "test";
*/

		String custom = "1=0";
		for(int i = 0; i < artikuls.size(); i++){
			String spe = artikuls.get(i).trim();
			if(spe.length() > 3){
				custom = custom + " or n.[Artikul] = '" + spe + "'";
			}
		}
		String dataOtgruzki = DateTimeHelper.SQLDateString(mShippingDate.getTime());
		String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
		String polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
		String sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		final They<Integer> defaultSelection = new They<Integer>();
		System.out.println("compose 4:");
		String sql = Request_NomenclatureBase.composeSQL(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, custom//
				, ISearchBy.SEARCH_CUSTOM//
				, false//
				, false//
				, sklad//
				, 500//
				, 0, false, false, false, null, null, false);
		final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));


		FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
		for(int i = 0; i < artikuls.size(); i++){
			String a = artikuls.get(i);


			for(int n = 0; n < b.children.size(); n++){
				String f = b.children.get(n).child("Artikul").value.property.value().trim();
				if(f.equals(a)){
					double CENA = Numeric.string2double(b.children.get(n).child("Cena").value.property.value());
					double SKIDKA = Numeric.string2double(b.children.get(n).child("Skidka").value.property.value());
					String VID_SKIDKI = b.children.get(n).child("VidSkidki").value.property.value();
					double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
                    /*double CENA_SO_SKIDKOY = getCenaSoSkidkoy(//
                            CENA//
                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                    );
                    double SKIDKA = getRazmerSkidki(//
                            Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                    );
                    String VID_SKIDKI = getVidSkidki(//
                            Numeric.string2double(b.children.get(n).child("FiksirovannyeCeny").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("SkidkaPartneraKarta").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("NakopitelnyeSkidki").value.property.value())//
                            , Numeric.string2double(b.children.get(n).child("Nacenka").value.property.value())//
                    );*/
					foodstuffData.newFoodstuff(//
							"x'" + b.children.get(n).child("_IDRRef").value.property.value() + "'"//
							, a//
							, b.children.get(n).child("Naimenovanie").value.property.value()//
							, "x'" + b.children.get(n).child("EdinicyIzmereniyaID").value.property.value() + "'"//
							, b.children.get(n).child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
							, 1//Numeric.string2double(b.children.get(n).child("MinNorma").value.property.value())//
							, CENA//
							, CENA_SO_SKIDKOY//
							, Numeric.string2double(b.children.get(n).child("MinCena").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("MaxCena").value.property.value())//
							, SKIDKA//
							, VID_SKIDKI//
							, Numeric.string2double(b.children.get(n).child("MinNorma").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("Koephphicient").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("BasePrice").value.property.value())//
							, Numeric.string2double(b.children.get(n).child("LastPrice").value.property.value())//
					);
					status.value("" + i + ". добавлен артикул " + a);
					//Auxiliary.inform("Добавлен артикул "+a,this);
					break;
				}
			}


		}
	}

	void doHistoryPaneExport(){
		String fileName = "/export" + Math.floor(Math.random() * 10000) + ".xls";
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		requeryHistoryData();
		Bough curdata = Auxiliary.fromCursor(this.historyCursor);
		//System.out.println("curdata: " + curdata.dumpXML());
		//Vector<Bough> datarows = curdata.children("row");

		ZayavkaPokupatelya bid = mBidData.getBid();
		Vector<Bough> datarows = curdata.children("row");
		//System.out.println(curdata.dumpXML());
		int n = datarows.size();
		for(int i = 0; i < n; i++){
			//System.out.println("row " + i + ": " + datarows.get(i).dumpXML());
			Vector<String> item = new Vector<String>();
			String s = datarows.get(i).child("Naimenovanie").value.property.value();//mBidData.getFoodStuffs().getFoodstuff(i).getNomenklaturaNaimenovanie();
			s = s.replaceAll(" \\(склад 8\\)", "");
			s = s.replaceAll(" \\(склад 10\\)", "");
			s = s.replaceAll(" \\(склад 12\\)", "");
			s = s.replaceAll(" \\(склад 14\\)", "");
			s = s.replaceAll(" \\(склад 17\\)", "");
			s = s.replaceAll(" \\(склад \\?\\)", "");
			item.add(datarows.get(i).child("Artikul").value.property.value());//mBidData.getFoodStuffs().getFoodstuff(i).getArtikul());
			item.add(s);
			item.add(datarows.get(i).child("EdinicyIzmereniyaNaimenovanie").value.property.value());//mBidData.getFoodStuffs().getFoodstuff(i).getEdinicaIzmereniyaName());
			item.add(datarows.get(i).child("MinNorma").value.property.value());// + mBidData.getFoodStuffs().getFoodstuff(i).getMinNorma());
			item.add(datarows.get(i).child("Koephphicient").value.property.value());// + mBidData.getFoodStuffs().getFoodstuff(i).getKoefMest());
			item.add(datarows.get(i).child("Cena").value.property.value());// + mBidData.getFoodStuffs().getFoodstuff(i).getCenaSoSkidkoy());
			//item.add(datarows.get(i).child("LastPrice").value.property.value());
			rows.add(item);
			String sql = "select n.naimenovanie as n1,s.naimenovanie as n2,g.naimenovanie as n3"//
					+ " from nomenklatura n"//
					+ " left join nomenklatura s on n.roditel=s._idrref"//
					+ " left join nomenklatura g on s.roditel=g._idrref"//
					+ " where n.artikul='" + item.get(0) + "';";
			//System.out.println(sql);
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(item.get(0)+", "+b.child("row").child("n2").value.property.value()+", "+b.child("row").child("n3").value.property.value());
			item.add(b.child("row").child("n2").value.property.value());
			item.add(b.child("row").child("n3").value.property.value());

		}

		Cfg.exportArtikulsList(this, fileName, bid.getClientKod(), rows);
	}

	void doHistoryPaneExport2222(){
		//gridHistory.exportCurrentDataCSV(Activity_Bid.this, "history.csv", "windows-1251");
		try{
			String xname = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/export" + Math.floor(Math.random() * 10000) + ".xls";
			File xfile = new File(xname);
			jxl.WorkbookSettings wbSettings = new jxl.WorkbookSettings();
			wbSettings.setLocale(new java.util.Locale("ru", "RU"));
			ZayavkaPokupatelya bid = mBidData.getBid();
			jxl.write.WritableWorkbook workbook = jxl.Workbook.createWorkbook(xfile, wbSettings);
			workbook.setColourRGB(Colour.BLUE, 53, 43, 111);
			workbook.setColourRGB(Colour.RED, 220, 44, 94);
			workbook.setColourRGB(Colour.GOLD, 220, 233, 94);
			workbook.createSheet("" + bid.getClientKod(), 0);
			jxl.write.WritableSheet excelSheet = workbook.getSheet(0);
			excelSheet.setColumnView(0, 6);
			excelSheet.setColumnView(1, 12);
			excelSheet.setColumnView(2, 72);
			excelSheet.setColumnView(3, 12);
			excelSheet.setColumnView(4, 12);
			//excelSheet.setColumnView(5, 12);
			excelSheet.setColumnView(6, 12);
			jxl.write.Label label;
			InputStream inStream = this.getResources().openRawResource(R.raw.logo3);
			byte[] logo = new byte[inStream.available()];
			int nn = 0;
			while(inStream.available() > 0){
				logo[nn] = (byte)inStream.read();
				nn++;
			}
			jxl.write.WritableFont titleFont = new jxl.write.WritableFont(WritableFont.ARIAL, 16);
			jxl.write.WritableCellFormat titleFormat = new jxl.write.WritableCellFormat(titleFont);
			jxl.write.WritableImage img = new jxl.write.WritableImage(0, 0, 7, 3, logo);
			excelSheet.addImage(img);
			excelSheet.addCell(new jxl.write.Label(2, 3, "ООО \"Свит Лайф Фудсервис\"", titleFormat));
			excelSheet.addCell(new jxl.write.Label(0, 4, "603058, г. Н. Новгород, ул. Героя Попова, д.43В, офис 1, тел. (831) 215-25-25, e-mail: office_hrc@swlife.nnov.ru"));
			excelSheet.addCell(new jxl.write.Label(0, 5, "ОКПО 80451411, ОГРН 1075258005008, ИНН/КПП 5258068806/525801001 Р/сч. 40702810142020102827,  К/сч. "));
			excelSheet.addCell(new jxl.write.Label(0, 6, "30101810900000000603 в Волго-Вятском Банке Сбербанка РФ г.  Н. Новгорода БИК 042202603"));
			excelSheet.addCell(new jxl.write.Label(2, 7, "Коммерческое предложение", titleFormat));
			/*
			label = new jxl.write.Label(0, 3, "Ваш Менеджер: " + Requests.getTPfio(mDB));
			excelSheet.addCell(label);
			label = new jxl.write.Label(0, 4, "Сервисный Центр, тел:  8-800-200-58-58");
			excelSheet.addCell(label);
			*/
			jxl.write.WritableCellFormat headerFormat = new jxl.write.WritableCellFormat();
			//f.setBackground(jxl.format.Colour.ICE_BLUE);
			int headerSkip = 9;
			headerFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			label = new jxl.write.Label(0, headerSkip - 1, "№", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(1, headerSkip - 1, "Код", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(2, headerSkip - 1, "Наименование", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(3, headerSkip - 1, "Ед.изм.", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(4, headerSkip - 1, "Мин. партия отгрузки", headerFormat);
			excelSheet.addCell(label);
			//label = new jxl.write.Label(5, headerSkip - 1, "Кол-во в месте", headerFormat);
			//excelSheet.addCell(label);
			label = new jxl.write.Label(5, headerSkip - 1, "Цена прайс.", headerFormat);
			excelSheet.addCell(label);
			label = new jxl.write.Label(6, headerSkip - 1, "Послед.цена", headerFormat);
			excelSheet.addCell(label);

			jxl.write.WritableCellFormat cellFormat = new jxl.write.WritableCellFormat();
			cellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			//cellFormat.setWrap(true);
			//jxl.write.WritableFont whiteFont = new jxl.write.WritableFont(jxl.write.WritableFont.ARIAL, 10, jxl.write.WritableFont.BOLD);
			//whiteFont.setColour(Colour.GRAY_50);
			jxl.write.WritableFont catFont = new jxl.write.WritableFont(jxl.write.WritableFont.createFont("Arial"), 10, jxl.write.WritableFont.BOLD, false//
					, jxl.format.UnderlineStyle.NO_UNDERLINE, Colour.GOLD);
			//catFont.setColour(Colour.GOLD);
			//jxl.write.WritableFont wfontSt = new jxl.write.WritableFont(WritableFont.createFont("Arial"), WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, colour);
			//jxl.write.WritableCellFormat catFormat = new jxl.write.WritableCellFormat(catFont);
			jxl.write.WritableCellFormat catFormat = new jxl.write.WritableCellFormat();//catFont);
			catFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			catFormat.setBackground(Colour.BLUE);
			//catFormat.setFont(catFont);
			catFormat.setAlignment(Alignment.CENTRE);
			jxl.write.WritableCellFormat subFormat = new jxl.write.WritableCellFormat();
			subFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			subFormat.setBackground(Colour.RED);
			subFormat.setAlignment(Alignment.CENTRE);
			excelSheet.setRowView(headerSkip - 1, 900);
			excelSheet.setRowView(headerSkip - 2, 900);
			excelSheet.setRowView(headerSkip - 6, 900);
			//int n = mBidData.getFoodStuffs().getCount();
			Vector<Vector<String>> rows = new Vector<Vector<String>>();
			requeryHistoryData();
			Bough curdata = Auxiliary.fromCursor(this.historyCursor);
			System.out.println("curdata: " + curdata.dumpXML());
			Vector<Bough> datarows = curdata.children("row");
			int n = datarows.size();
			for(int i = 0; i < n; i++){
				//System.out.println("row " + i + ": " + datarows.get(i).dumpXML());
				Vector<String> item = new Vector<String>();
				String s = datarows.get(i).child("Naimenovanie").value.property.value();//mBidData.getFoodStuffs().getFoodstuff(i).getNomenklaturaNaimenovanie();
				s = s.replaceAll(" \\(склад 8\\)", "");
				s = s.replaceAll(" \\(склад 10\\)", "");
				s = s.replaceAll(" \\(склад 12\\)", "");
				s = s.replaceAll(" \\(склад 14\\)", "");
				s = s.replaceAll(" \\(склад 17\\)", "");
				s = s.replaceAll(" \\(склад \\?\\)", "");
				item.add(datarows.get(i).child("Artikul").value.property.value());//mBidData.getFoodStuffs().getFoodstuff(i).getArtikul());
				item.add(s);
				item.add(datarows.get(i).child("EdinicyIzmereniyaNaimenovanie").value.property.value());//mBidData.getFoodStuffs().getFoodstuff(i).getEdinicaIzmereniyaName());
				item.add(datarows.get(i).child("MinNorma").value.property.value());// + mBidData.getFoodStuffs().getFoodstuff(i).getMinNorma());
				item.add(" ");// + mBidData.getFoodStuffs().getFoodstuff(i).getKoefMest());
				item.add(datarows.get(i).child("Cena").value.property.value());// + mBidData.getFoodStuffs().getFoodstuff(i).getCenaSoSkidkoy());
				item.add(datarows.get(i).child("LastPrice").value.property.value());
				rows.add(item);

				/*
				s = s.replaceAll(" \\(склад \\?\\)", "");
				item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getArtikul());
				item.add(s);
				item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getEdinicaIzmereniyaName());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getMinNorma());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getKoefMest());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getCenaSoSkidkoy());
				rows.add(item);
				*/


				String sql = "select n.naimenovanie as n1,s.naimenovanie as n2,g.naimenovanie as n3"//
						+ " from nomenklatura n"//
						+ " left join nomenklatura s on n.roditel=s._idrref"//
						+ " left join nomenklatura g on s.roditel=g._idrref"//
						+ " where n.artikul='" + item.get(0) + "';";
				Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				item.add(b.child("row").child("n2").value.property.value());
				item.add(b.child("row").child("n3").value.property.value());
			}
			Collections.sort(rows, new Comparator<Vector<String>>(){
				@Override
				public int compare(Vector<String> a, Vector<String> b){
					return a.get(7).compareTo(b.get(7)) * 1000000 + a.get(6).compareTo(b.get(6)) * 1000 + a.get(1).compareTo(b.get(1));
				}
			});
			String cat = "";
			String subcat = "";
			int catcount = 0;
			for(int i = 0; i < n; i++){
				if(!cat.equals(rows.get(i).get(7))){
					cat = rows.get(i).get(7);
					excelSheet.mergeCells(0, headerSkip + i + catcount, 6, headerSkip + i + catcount);
					label = new jxl.write.Label(0, headerSkip + i + catcount, "" + cat, catFormat);
					excelSheet.addCell(label);
					catcount++;
				}
				if(!subcat.equals(rows.get(i).get(6))){
					subcat = rows.get(i).get(6);
					label = new jxl.write.Label(0, headerSkip + i + catcount, "" + subcat, subFormat);
					excelSheet.addCell(label);
					excelSheet.mergeCells(0, headerSkip + i + catcount, 6, headerSkip + i + catcount);
					catcount++;
				}
				label = new jxl.write.Label(0, headerSkip + i + catcount, "" + (1 + i), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(1, headerSkip + i + catcount, rows.get(i).get(0), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(2, headerSkip + i + catcount, rows.get(i).get(1), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(3, headerSkip + i + catcount, rows.get(i).get(2), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(4, headerSkip + i + catcount, rows.get(i).get(3), cellFormat);
				excelSheet.addCell(label);
				//label = new jxl.write.Label(4, headerSkip + i + catcount, rows.get(i).get(4), cellFormat);
				//excelSheet.addCell(label);
				label = new jxl.write.Label(5, headerSkip + i + catcount, rows.get(i).get(5), cellFormat);
				excelSheet.addCell(label);
				label = new jxl.write.Label(6, headerSkip + i + catcount, rows.get(i).get(6), cellFormat);
				excelSheet.addCell(label);
			}
			workbook.write();
			workbook.close();
			Auxiliary.startFile(this, android.content.Intent.ACTION_VIEW, "application/vnd.ms-excel", xfile);
			System.out.println("export name is " + xname);
		}catch(Throwable t){
			t.printStackTrace();
			Auxiliary.warn("Ошибка: " + t.getMessage(), Activity_Bid.this);
		}

	}

	void doMenuExport(){
		exportBidData(mBidData);
	}

	void exportBidData(BidData mBidData){
		String fileName = "/Заказ "
				+ Auxiliary.safeFileName(ApplicationHoreca.getInstance().getClientInfo().getName())
				+ Math.floor(Math.random() * 10000)
				+ ".xls";
		ZayavkaPokupatelya bid = mBidData.getBid();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		int cnt = mBidData.getFoodStuffs().getCount();
		String lastArt="";
		for(int ii = 0; ii < cnt; ii++){
			Vector<String> item = new Vector<String>();
			String s = mBidData.getFoodStuffs().getFoodstuff(ii).getNomenklaturaNaimenovanie();
			s = s.replaceAll(" \\(склад 8\\)", "");
			s = s.replaceAll(" \\(склад 10\\)", "");
			s = s.replaceAll(" \\(склад 12\\)", "");
			s = s.replaceAll(" \\(склад 14\\)", "");
			s = s.replaceAll(" \\(склад 17\\)", "");
			s = s.replaceAll(" \\(склад \\?\\)", "");
			s = s.replaceAll("СТМ: ", "");
			item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getArtikul());
			item.add(s);
			item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getEdinicaIzmereniyaName());
			item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getMinNorma());
			item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getKoefMest());
			item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getCenaSoSkidkoy());
			if(!lastArt.equals(mBidData.getFoodStuffs().getFoodstuff(ii).getArtikul())){
				rows.add(item);
			}
			lastArt=mBidData.getFoodStuffs().getFoodstuff(ii).getArtikul();
			String sql = "select n.naimenovanie as n1,s.naimenovanie as n2,g.naimenovanie as n3"//
					+ " from nomenklatura n"//
					+ " left join nomenklatura s on n.roditel=s._idrref"//
					+ " left join nomenklatura g on s.roditel=g._idrref"//
					+ " where n.artikul='" + item.get(0) + "';";
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			item.add(b.child("row").child("n2").value.property.value());
			item.add(b.child("row").child("n3").value.property.value());
			/*String txttst = "";
			for(int kk = 0; kk < item.size(); kk++){
				txttst = txttst + '/' + kk + ':' + item.get(kk);
			}*/
		}
		Cfg.exportArtikulsList(this, fileName, bid.getClientKod(), rows);
	}

	void exportBidData222222222(BidData mBidData){
		try{
			String xname = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
					+ "/Заказ "
					+ Auxiliary.safeFileName(ApplicationHoreca.getInstance().getClientInfo().getName())
					+ Math.floor(Math.random() * 10000)
					+ ".xls";
			File xfile = new File(xname);
			jxl.WorkbookSettings wbSettings = new jxl.WorkbookSettings();
			wbSettings.setLocale(new java.util.Locale("ru", "RU"));
			ZayavkaPokupatelya bid = mBidData.getBid();
			jxl.write.WritableWorkbook workbook = jxl.Workbook.createWorkbook(xfile, wbSettings);
			workbook.setColourRGB(Colour.PLUM, 229, 0, 81);
			workbook.setColourRGB(Colour.LAVENDER, 195, 189, 217);
			workbook.setColourRGB(Colour.BROWN, 55, 38, 128);
			workbook.createSheet("" + bid.getClientKod(), 0);
			jxl.write.WritableSheet excelSheet = workbook.getSheet(0);
			excelSheet.setColumnView(1, 6);
			excelSheet.setColumnView(2, 12);
			excelSheet.setColumnView(3, 60);
			excelSheet.setColumnView(4, 6);
			excelSheet.setColumnView(5, 12);
			excelSheet.setColumnView(6, 12);
			excelSheet.setColumnView(7, 12);
			jxl.write.Label label;
			InputStream inStream = this.getResources().openRawResource(R.raw.export_header);
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
			int cnt = mBidData.getFoodStuffs().getCount();
			Vector<Vector<String>> rows = new Vector<Vector<String>>();
			for(int ii = 0; ii < cnt; ii++){
				Vector<String> item = new Vector<String>();
				String s = mBidData.getFoodStuffs().getFoodstuff(ii).getNomenklaturaNaimenovanie();
				s = s.replaceAll(" \\(склад 8\\)", "");
				s = s.replaceAll(" \\(склад 10\\)", "");
				s = s.replaceAll(" \\(склад 12\\)", "");
				s = s.replaceAll(" \\(склад 14\\)", "");
				s = s.replaceAll(" \\(склад 17\\)", "");
				s = s.replaceAll(" \\(склад \\?\\)", "");
				item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getArtikul());
				item.add(s);
				item.add(mBidData.getFoodStuffs().getFoodstuff(ii).getEdinicaIzmereniyaName());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getMinNorma());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getKoefMest());
				item.add("" + mBidData.getFoodStuffs().getFoodstuff(ii).getCenaSoSkidkoy());
				rows.add(item);
				String sql = "select n.naimenovanie as n1,s.naimenovanie as n2,g.naimenovanie as n3"//
						+ " from nomenklatura n"//
						+ " left join nomenklatura s on n.roditel=s._idrref"//
						+ " left join nomenklatura g on s.roditel=g._idrref"//
						+ " where n.artikul='" + item.get(0) + "';";
				Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				item.add(b.child("row").child("n2").value.property.value());
				item.add(b.child("row").child("n3").value.property.value());
				String txttst = "";
				for(int kk = 0; kk < item.size(); kk++){
					txttst = txttst + '/' + kk + ':' + item.get(kk);
				}
			}
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
			InputStream inStream2 = this.getResources().openRawResource(R.raw.export_bottom);
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
			Auxiliary.startFile(this, xfile);
		}catch(Throwable t){
			t.printStackTrace();
			Auxiliary.warn("Ошибка: " + t.getMessage(), Activity_Bid.this);
		}
	}


	void dataModeRoll(){
		DATA_CURRENT++;
		if(DATA_CURRENT > 4){
			DATA_CURRENT = 0;
		}
		dataModeSet();
		gridOffsetHistory.value(0);
		historyFillColumns(true);
		gridHistory.refresh();
	}

	void dataModeSet(){
		if(DATA_CURRENT == DATA_HISTORY){
			dataModeCaption.value("История");
		}
		if(DATA_CURRENT == DATA_HERO){
			dataModeCaption.value("Герои");
		}
		if(DATA_CURRENT == DATA_TOP){
			dataModeCaption.value("Наценка");
		}
		if(DATA_CURRENT == DATA_MOTI){
			dataModeCaption.value("Мотивация");
		}
		if(DATA_CURRENT == DATA_INDIVIDUAL){
			dataModeCaption.value("Скидки");
		}
		System.out.println("dataModeSet: " + DATA_CURRENT + ": " + dataModeCaption.value());

	}

	void composeHistoryTab(){
		//System.out.println("composeHistoryTab start");
		layoutless = (Layoutless)findViewById(R.id.historyLayoutless);
		this.clientForHistory = new RedactSingleChoice(this);
		this.clientForHistory.selection.is(selectedForHistory);
		Calendar historyFromDate = Calendar.getInstance();
		historyFromDate.add(Calendar.DAY_OF_YEAR, -90);
		historyFrom.value((double)historyFromDate.getTimeInMillis());
		new Numeric().bind(historyFrom).afterChange(new Task(){
			@Override
			public void doTask(){
				//currentOffsetHistory = 0;
				//resetHistoryData();
				historyFillColumns(true);
				gridHistory.refresh();
			}
		}, true);
		Calendar historyToDate = Calendar.getInstance();
		historyTo.value((double)historyToDate.getTimeInMillis());
		new Numeric().bind(historyTo).afterChange(new Task(){
			@Override
			public void doTask(){
				//currentOffsetHistory = 0;
				//resetHistoryData();
				historyFillColumns(true);
				gridHistory.refresh();
			}
		}, true);
		//photoIcon = BitmapFactory.decodeResource(getResources(), R.drawable.picture);
		gridHistory = new DataGrid(this).headerHeight.is(47)//
				.dataOffset.is(gridOffsetHistory)//
				.pageSize.is(pageSize)//
				.beforeFlip.is(new Task(){
					@Override
					public void doTask(){
						//resetHistoryData();
						historyFillColumns(true);
					}
				});
		/*gridHistory.afterScroll.is(new Task() {
			@Override
			public void doTask() {
				int scroll = gridHistory.scroll.property.value().intValue();
				//System.out.println("afterScroll "+scroll);
				if (Math.abs(scroll) > 1) {
					if (historyGridLock) {
						return;
					}
					historyGridLock = true;
					//System.out.println("scroll by " + scroll);
					//					if(currentOffsetHistory>0)
					if (currentOffsetHistory > 0 || scroll > 0) {
						currentOffsetHistory = currentOffsetHistory + scroll;
						if (currentSizeHistory >= dataPageSize || scroll < 0) {
							historyReadData();
							if (currentSizeHistory > 0) {
								//System.out.println(currentSizeHistory);
								historyFillGrid();
								gridHistory.reset();
							}
							else {
								currentOffsetHistory = currentOffsetHistory - scroll;
							}
						}
						else {
							currentOffsetHistory = currentOffsetHistory - scroll;
						}
					}
					//if(historyGridLock)return;
					historyGridLock = false;
				}
				gridHistory.resetYScroll();
			}
		});*/
		seekStringHistory.afterChange(new CannyTask(){
			@Override
			public void doTask(){
				if((seekStringHistory.value().length() == 0 && seekPreHistory.length() > 0)){
					//currentOffsetHistory = 0;
					//resetHistoryData();
					gridOffsetHistory.value(0);
					historyFillColumns(true);
					gridHistory.refresh();
				}else{
					if(seekStringHistory.value().length() > 2){
						if(seekStringHistory.value().length() > seekPreHistory.length()){
							//currentOffsetHistory = 0;
							//resetHistoryData();
							gridOffsetHistory.value(0);
							historyFillColumns(true);
							gridHistory.refresh();
						}
					}
				}
				seekPreHistory = seekStringHistory.value();
			}
		}.laziness.is(333), true);
		SubLayoutless gridHistoryPanel = new SubLayoutless(this);
		//System.out.println(layoutless.getWidth());
		//System.out.println(layoutless.width().property.value());
		int ww = Auxiliary.screenWidth(this);
		slrHistory = new SplitLeftRight(this).split.is(ww);//1280);
		layoutless//
				.solid.is(false)//
				.child(new Decor(this)//
						.background.is(0xffe3e3e3)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
						.left().is(slrHistory.split.property)//
				)//
				.child(slrHistory//
						.position.is(1)//
						.rightSide(gridHistoryPanel//
								.child(new Decor(this)//
										.labelText.is("Поиск по названию")//
										.width().is(6 * Auxiliary.tapSize)//
										.height().is(26)//
										.left().is(3.1 * Auxiliary.tapSize)//
										.top().is(4)//
								)//
								.child(new RedactText(this).text.is(seekStringHistory)//
										.singleLine.is(true)//
										.width().is(4.9 * Auxiliary.tapSize)//
										.height().is(0.7 * Auxiliary.tapSize)//
										.left().is(3.1 * Auxiliary.tapSize)//
										.top().is(22)//
								)//
								.child(new Knob(this).labelText.is(dataModeCaption)//
										//История
										//Наценка
										//Мотивация
										.afterTap.is(new Task(){
											@Override
											public void doTask(){
												dataModeRoll();
											}
										})//
										.width().is(2.3 * Auxiliary.tapSize)//
										.height().is(Auxiliary.tapSize)//
										.left().is(4)//
										.top().is(4)//
								)//
								/*



								.child(new KnobImage(this)//
								.bitmap.is(BitmapFactory.decodeResource(getResources(), R.drawable.goprev)).tap.is(new Task() {
									@Override
									public void doTask() {
										System.out.println(currentOffsetHistory);
										if (!seekPreHistory.equals(seekStringHistory.value())) {
											currentOffsetHistory = 0;
											Auxiliary.hideSoftKeyboard(Activity_Bid.this);
											resetHistoryData();
										}
										else {
											if (currentOffsetHistory > 0) {
												currentOffsetHistory = currentOffsetHistory - dataPageSize;
												Auxiliary.hideSoftKeyboard(Activity_Bid.this);
												resetHistoryData();
											}
										}
									}
								}//
										)//
										.width().is(0.8 * Layoutless.tapSize)//
										.height().is(0.8 * Layoutless.tapSize)//
										.left().is(4.2 * Layoutless.tapSize)//
										.top().is(0.1 * Layoutless.tapSize)//
								)//
								.child(new KnobImage(this)//
								.bitmap.is(BitmapFactory.decodeResource(getResources(), R.drawable.gonext)).tap.is(new Task() {
									@Override
									public void doTask() {
										if (!seekPreHistory.equals(seekStringHistory.value())) {
											currentOffsetHistory = 0;
											Auxiliary.hideSoftKeyboard(Activity_Bid.this);
											resetHistoryData();
										}
										else {
											if (historyHasMoreData) {
												currentOffsetHistory = currentOffsetHistory + dataPageSize;
												Auxiliary.hideSoftKeyboard(Activity_Bid.this);
												resetHistoryData();
											}
										}
									}
								}//
										)//
										.width().is(0.8 * Layoutless.tapSize)//
										.height().is(0.8 * Layoutless.tapSize)//
										.left().is(5.0 * Layoutless.tapSize)//
										.top().is(0.1 * Layoutless.tapSize)//
								)//



								*/.child(new Decor(this)//
										.labelText.is("Период с")//.labelAlignRightCenter()//
										.width().is(2.5 * Auxiliary.tapSize)//
										.height().is(26)//
										.left().is(8.0 * Auxiliary.tapSize)//
										.top().is(4)//
								)//
								.child(new RedactDate(this)//
										.date.is(historyFrom).format.is("dd.MM.yyyy")//
										.width().is(2.5 * Auxiliary.tapSize)//
										.height().is(0.7 * Auxiliary.tapSize)//
										.left().is(8.0 * Auxiliary.tapSize)//
										.top().is(22)//
								)//
								.child(new Decor(this)//
										.labelText.is("по")//.labelAlignRightCenter()//
										.width().is(2.5 * Auxiliary.tapSize)//
										.height().is(26)//
										.left().is(10.5 * Auxiliary.tapSize)//
										.top().is(4)//
								)//
								.child(new RedactDate(this)//
										.date.is(historyTo).format.is("dd.MM.yyyy")//
										.width().is(2.5 * Auxiliary.tapSize)//
										.height().is(0.7 * Auxiliary.tapSize)//
										.left().is(10.5 * Auxiliary.tapSize)//
										.top().is(22)//
								)//
								.child(new Knob(this)//
										.labelText.is("Экспорт")//
										.afterTap.is(new Task(){
											@Override
											public void doTask(){
												//gridHistory.exportCurrentDataCSV(Activity_Bid.this, "history.csv", "windows-1251");
												doHistoryPaneExport();
											}
										}).width().is(2.5 * Auxiliary.tapSize)//
										.height().is(1 * Auxiliary.tapSize)//
										.left().is(13.7 * Auxiliary.tapSize)//
										.top().is(0.1 * Auxiliary.tapSize)//
								)//
								.child(gridHistory//
										//.maxRowHeight.is(2)//
										.columns(new Column[]{
												historyArtikul.title.is("Арт-л").width.is(1.2 * Auxiliary.tapSize) //
												, historyNomenklatura.title.is("Номенклатура").width.is(4 * Auxiliary.tapSize)//
												, historyProizvoditel.title.is("Произв-ль").width.is(1.7 * Auxiliary.tapSize)//
												, historyMinKol.title.is("Мин. кол.").width.is(1 * Auxiliary.tapSize)//
												, historyKolMest.title.is("Кол. мест").width.is(1 * Auxiliary.tapSize)//
												, historyEdIzm.title.is("Ед. изм.").width.is(1 * Auxiliary.tapSize)//
												, historyCena.title.is("Цена").width.is(1.5 * Auxiliary.tapSize)//
												, historyRazmSkidki.title.is("Разм. скидки").width.is(1.25 * Auxiliary.tapSize)//
												, historyVidSkidki.title.is("Вид скидки").width.is(1 * Auxiliary.tapSize)//
												, historyPoslCena.title.is("Посл. цена").width.is(1.5 * Auxiliary.tapSize)//
												, historyMinCena.title.is("Мин. цена").width.is(1.5 * Auxiliary.tapSize)//
												, historyMaxCena.title.is("Макс. цена").width.is(1.5 * Auxiliary.tapSize) //
												, historyPhoto.title.is("Фото").width.is(Auxiliary.tapSize) //
										})//
										.top().is(Auxiliary.tapSize + 1)//
										.width().is(layoutless.width().property.minus(slrHistory.split.property).read())//
										.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 2).read())//
								)//
								.child(this.clientForHistory//
										.width().is(layoutless.width().property)//
										.height().is(1 * Auxiliary.tapSize)//
										.left().is(0)//
										.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
								)//
								/*.child(new Decor(this)//
								.background.is(0xff666666)//
										.top().is(Layoutless.tapSize)//
										.width().is(layoutless.width().property)//
										.height().is(1)//
								)*/.child(new Decor(this)//
										.background.is(0xff666666)//
										.top().is(Auxiliary.tapSize + 47)//
										.width().is(layoutless.width().property)//
										.height().is(1)//
								))//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				)//
		;
		//System.out.println("composeHistoryTab done");
	}

	/*void composeMustTab() {
		System.out.println("composeMustTab start");
		gridMust = new DataGrid(this)//
		//.headerHeight.is(47)
		.pageSize.is(pageSize)//
		.dataOffset.is(gridOffsetMust)//
		.beforeFlip.is(new Task() {
			@Override
			public void doTask() {
				mustFillColumns(true);
			}
		});
		SubLayoutless gridMustPanel = new SubLayoutless(this);
		int ww = Auxiliary.screenWidth(this);
		slrMust = new SplitLeftRight(this).split.is(ww);//1280);
		layoutless//
		.solid.is(false)//
				.child(new Decor(this)//
				.background.is(0xffe3e3e3)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
						.left().is(slrMust.split.property)//
				)//
				.child(slrMust//
				.position.is(2)//
						.rightSide(gridMustPanel//
								.child(gridMust//
								.columns(new Column[] { mustArtikul.title.is("Арт-л").width.is(60) //
										, mustNomenklatura.title.is("Номенклатура").width.is(270)//
										, mustProizvoditel.title.is("Произв-ль").width.is(120)//
										, mustMinKol.title.is("Мин. кол.").width.is(40)//
										, mustKolMest.title.is("Кол. мест").width.is(60)//
										, mustEdIzm.title.is("Ед. изм.").width.is(100)//
										, mustCena.title.is("Цена").width.is(70)//
										, mustRazmSkidki.title.is("Разм. скидки").width.is(60)//
										, mustVidSkidki.title.is("Вид скидки").width.is(100)//
										, mustPoslCena.title.is("Посл. цена").width.is(70)//
										, mustMinCena.title.is("Мин. цена").width.is(70)//
										, mustMaxCena.title.is("Макс. цена").width.is(70) //
										, mustPhoto.title.is("Фото").width.is(Auxiliary.tapSize) //
										})//
										//.top().is(Auxiliary.tapSize )//
										.width().is(layoutless.width().property.minus(slrMust.split.property).read())//
										.height().is(layoutless.height().property.read())//
								)//
								.child(new Decor(this)//
								.background.is(0xff666666)//
										.top().is(Auxiliary.tapSize)//
										.width().is(layoutless.width().property)//
										.height().is(1)//
								))//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				)//
		;
		System.out.println("composeMustTab done");
	}*/
	/*void requeryMustData() {
		System.out.println("requeryMustData start");
		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try {
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(mShippingDate.getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}
		catch (Throwable t) {
			LogHelper.debug(this.getClass().getCanonicalName() + ": " + t.getMessage());
		}
		String sql = Request_NomenclatureBase.composeSQL(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, ""//
				, ISearchBy.SEARCH_NAME//
				, false//
				, false//
				, sklad//
				, pageSize*3//
				, gridOffsetMust.value().intValue()//
				, true//
				);
		mustCursor = mDB.rawQuery(sql, null);
		System.out.println("requeryMustData done");
	}*/
	void requeryHistoryData(){
		//LogHelper.debug(this.getClass().getCanonicalName() + ".requeryHistoryData start");
		//System.out.println("requeryHistoryData start");
		if(mPaymentTypeAdapter == null){
			return;
		}
		if(mPaymentTypeAdapter.GetSelectedItem() == null){
			return;
		}
		if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
			return;
		}
		if(lockResetHistoryData){
			Auxiliary.warn("Поиск по истории...", this);
			//System.out.println(".requeryHistoryData already locked");
			return;
		}
		lockResetHistoryData = true;
		System.out.println("resetHistoryData start");
		//LogHelper.debug(this.getClass().getCanonicalName() + ".resetHistoryData start");
		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			//System.out.println("resetHistoryData 1");
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			//System.out.println("resetHistoryData 2");
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			//System.out.println("resetHistoryData 3");
			//Calendar	mShippingDate = Calendar.getInstance();
			//mShippingDate.setTimeInMillis(mBidData.getBid().getShippingDateInMillis());
			dataOtgruzki = DateTimeHelper.SQLDateString(mShippingDate.getTime()
					//mBidData.getBid().getShippingDateInMillis()
					//ApplicationHoreca.getInstance().getShippingDate().getTime()
			);
			//System.out.println("resetHistoryData 3");
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			//System.out.println("resetHistoryData 4");
			clientID = "x'" + Cfg.kontragenty().children.get(this.selectedForHistory.value().intValue()).child("_idrref").value.property.value() + "'";
		}catch(Throwable t){
			LogHelper.debug(this.getClass().getCanonicalName() + ": " + t.getMessage());
		}
		//historyFrom
		Date fr = new Date();
		fr.setTime(historyFrom.value().longValue());
		Date to = new Date();
		to.setTime(historyTo.value().longValue());
		//System.out.println("resetHistoryData compose sql");
		boolean isMustList = false;
		boolean isTop = false;
		boolean isHist = false;
		boolean isSkidka = false;
		boolean isHero = false;
		if(DATA_CURRENT == DATA_TOP){
			isMustList = false;
			isTop = true;
			isHist = false;
			isSkidka = false;
			isHero = false;
		}else{
			if(DATA_CURRENT == DATA_MOTI){
				isMustList = true;
				isTop = false;
				isHist = false;
				isSkidka = false;
				isHero = false;
			}else{
				if(DATA_CURRENT == DATA_INDIVIDUAL){
					isMustList = false;
					isTop = false;
					isHist = false;
					isSkidka = true;
					isHero = false;
				}else{
					if(DATA_CURRENT == DATA_HERO){
						isMustList = false;
						isTop = false;
						isHist = false;
						isSkidka = false;
						isHero = true;
					}else{
						isMustList = false;
						isTop = false;
						isHist = true;
						isSkidka = false;
						isHero = false;
					}
				}
			}
		}
		//System.out.println("requeryHistoryData: "+isHero+"/"+isHist+"/"+DATA_CURRENT+"/"+DATA_HERO+"/"+ISearchBy.SEARCH_HERO);//, (isHero)?ISearchBy.SEARCH_HERO:ISearchBy.SEARCH_NAME//
		String sql = Request_NomenclatureBase.composeSQLall(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, DateTimeHelper.SQLDateString(fr)//
				, DateTimeHelper.SQLDateString(to)//
				, seekStringHistory.value()//
				, (isHero) ? ISearchBy.SEARCH_HERO : ISearchBy.SEARCH_NAME//, ISearchBy.SEARCH_NAME//
				, false//
				, isHist//
				, sklad//
				, pageSize * 3//
				//, gridHistory.dataOffset.property.value().intValue()//
				, gridOffsetHistory.value().intValue()//
				, isMustList//
				, isTop, null, null, isSkidka, false, null, null, null, false);
		//System.out.println("requeryHistoryData " + sql);
		historyCursor = mDB.rawQuery(sql, null);
		//System.out.println("fetched");
		//System.out.println("load history ");
		//System.out.println("resetHistoryData fetch");
		//System.out.println(sql);
		//System.out.println(Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());
		//System.out.println("resetHistoryData fetch");
		//System.out.println("done load history, total " + historyRows.children.size() + ", offset " + gridHistory.dataOffset.property.value());
		//LogHelper.debug(this.getClass().getCanonicalName() + ".resetHistoryData done");
		lockResetHistoryData = false;
		//System.out.println("requeryHistoryData done");
		//LogHelper.debug(this.getClass().getCanonicalName() + ".requeryHistoryData done");
	}




	void fillClientHistoryPrompt(){
		System.out.println("fillClientHistoryPrompt");
		for(int i = 0; i < Cfg.kontragenty().children.size(); i++){
			this.clientForHistory.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			//System.out.println(Cfg.kontragenty().children.get(i).dumpXML());
			if(Cfg.kontragenty().children.get(i).child("kod").value.property.value().equals(ApplicationHoreca.getInstance().getClientInfo().getKod())){
				selectedForHistory.value(i);
			}
		}
		//_idrref
		//System.out.println(ApplicationHoreca.getInstance().getClientInfo().getID());
		selectedForHistory.afterChange(new Task(){
			public void doTask(){
				gridOffsetHistory.value(0);
				historyFillColumns(true);
				gridHistory.refresh();
			}
		}, true);
	}

	void historyFillColumns(boolean requery){
		//LogHelper.debug(this.getClass().getCanonicalName() + ".historyFillColumns start");
		System.out.println("historyFillColumns requery " + requery);
		if(requery){
			requeryHistoryData();
		}
		//System.out.println("historyFillColumns");
		/*if (historyRows == null) {
			System.out.println("historyFillColumns is null");
			return;
		}*/
		//System.out.println("clear columns");
		historyArtikul.clear();
		historyNomenklatura.clear();
		historyProizvoditel.clear();
		historyMinKol.clear();
		historyKolMest.clear();
		historyEdIzm.clear();
		historyCena.clear();
		historyRazmSkidki.clear();
		historyVidSkidki.clear();
		historyPoslCena.clear();
		historyMinCena.clear();
		historyMaxCena.clear();
		historyPhoto.clear();
		mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);
		String[] mDateFormatStrings = new String[]{
				"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
				//				"yyyy-MM-dd hh:mm:ss",
				"yyyy-MM-dd hh:mm", "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm:ss.sss", "yyyy-MM-dd'T'hh:mm", "yyyy-MM-dd'T'hh:mm:ss", "yyyy-MM-dd'T'hh:mm:ss.sss", "hh:mm", "hh:mm:ss", "hh:mm:ss.sss", "yyyyMMdd'T'hh:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
		};
		//for (int i = 0; i < historyRows.children.size(); i++) {
		if(historyCursor == null){
			//System.out.println("historyCursor is null");
			return;
		}
		//System.out.println("fill columns");
		while(historyCursor.moveToNext()){
			//Bough row = historyRows.children.get(i);
			final String Artikul = Auxiliary.cursorString(historyCursor, "Artikul");
			//row.child("Artikul").value.property.value();
			final String Naimenovanie = Auxiliary.cursorString(historyCursor, "Naimenovanie");


			//row.child("Naimenovanie").value.property.value();
			final String _IDRRef = "x'" + Auxiliary.cursorBlob(historyCursor, "_IDRRef").toUpperCase() + "'";
			//"x'" + row.child("_IDRRef").value.property.value().toUpperCase() + "'";
			final String EdinicyIzmereniyaID = "x'" + Auxiliary.cursorBlob(historyCursor, "EdinicyIzmereniyaID").toUpperCase() + "'";
			//"x'" + row.child("EdinicyIzmereniyaID").value.property.value().toUpperCase() + "'";
			final String EdinicyIzmereniyaNaimenovanie = Auxiliary.cursorString(historyCursor, "EdinicyIzmereniyaNaimenovanie");
			//row.child("EdinicyIzmereniyaNaimenovanie").value.property.value();
			/*NomenclatureCountHelper helper = new NomenclatureCountHelper(//
					Numeric.string2double(row.child("MinNorma").value.property.value())//
					, Numeric.string2double(row.child("Koephphicient").value.property.value())//
			);*/
			NomenclatureCountHelper helper = new NomenclatureCountHelper(//
					Auxiliary.cursorDouble(historyCursor, "MinNorma")//
					, Auxiliary.cursorDouble(historyCursor, "Koephphicient")//
			);
			final double COUNT = helper.ReCalculateCount(0);
			final double CENA = Auxiliary.cursorDouble(historyCursor, "Cena");
			//row.child("Cena").value.property.value());
			final double MIN_CENA //=Auxiliary.cursorDouble(historyCursor, "MinCena");
					= Math.ceil(Auxiliary.cursorDouble(historyCursor, "MinCena") * 100.0) / 100.0;
			//Numeric.string2double(row.child("MinCena").value.property.value());
			//
			final double MAX_CENA = Auxiliary.cursorDouble(historyCursor, "MaxCena");
			//Numeric.string2double(row.child("MaxCena").value.property.value());
			/*final double SKIDKA = getRazmerSkidki(//
					Numeric.string2double(row.child("FiksirovannyeCeny").value.property.value())//
					, Numeric.string2double(row.child("SkidkaPartneraKarta").value.property.value())//
					, Numeric.string2double(row.child("NakopitelnyeSkidki").value.property.value())//
					, Numeric.string2double(row.child("Nacenka").value.property.value()));*/
            /*final double SKIDKA = getRazmerSkidki(//
                    Auxiliary.cursorDouble(historyCursor, "FiksirovannyeCeny")//
                    , Auxiliary.cursorDouble(historyCursor, "SkidkaPartneraKarta")//
                    , Auxiliary.cursorDouble(historyCursor, "NakopitelnyeSkidki")//
                    , Auxiliary.cursorDouble(historyCursor, "Nacenka"));
            */
			//final double SKIDKA = Auxiliary.cursorDouble(historyCursor, "Skidka");
			double testSkidka = Auxiliary.cursorDouble(historyCursor, "Skidka");
			//final double SKIDKA = 0;
			/*final String VID_SKIDKI = getVidSkidki(//
					Numeric.string2double(row.child("FiksirovannyeCeny").value.property.value())//
					, Numeric.string2double(row.child("SkidkaPartneraKarta").value.property.value())//
					, Numeric.string2double(row.child("NakopitelnyeSkidki").value.property.value())//
					, Numeric.string2double(row.child("Nacenka").value.property.value()));
			*/
            /*final String VID_SKIDKI = getVidSkidki(//
                    Auxiliary.cursorDouble(historyCursor, "FiksirovannyeCeny")//
                    , Auxiliary.cursorDouble(historyCursor, "SkidkaPartneraKarta")//
                    , Auxiliary.cursorDouble(historyCursor, "NakopitelnyeSkidki")//
                    , Auxiliary.cursorDouble(historyCursor, "Nacenka"));
            */
			final String VID_SKIDKI = Auxiliary.cursorString(historyCursor, "VidSkidki");
			final double MIN_NORMA = Auxiliary.cursorDouble(historyCursor, "MinNorma");
			//Numeric.string2double(row.child("MinNorma").value.property.value());
			final double KOEPHICIENT = Auxiliary.cursorDouble(historyCursor, "Koephphicient");
			//Numeric.string2double(row.child("Koephphicient").value.property.value());
			final double BASE_PRICE = Auxiliary.cursorDouble(historyCursor, "BasePrice");
			//Numeric.string2double(row.child("BasePrice").value.property.value());
			final double LAST_PRICE = Auxiliary.cursorDouble(historyCursor, "LastPrice");
			final String lastSellCount = Auxiliary.cursorString(historyCursor, "lastSellCount");
			//Numeric.string2double(row.child("LastPrice").value.property.value());
			/*final double CENA_SO_SKIDKOY = getCenaSoSkidkoy(CENA//
					, Numeric.string2double(row.child("Nacenka").value.property.value())//
					, Numeric.string2double(row.child("FiksirovannyeCeny").value.property.value())//
					, Numeric.string2double(row.child("SkidkaPartneraKarta").value.property.value())//
					, Numeric.string2double(row.child("NakopitelnyeSkidki").value.property.value())//
					);*/
            /*final double CENA_SO_SKIDKOY = getCenaSoSkidkoy(CENA//
                    , Auxiliary.cursorDouble(historyCursor, "Nacenka")//
                    , Auxiliary.cursorDouble(historyCursor, "FiksirovannyeCeny")//
                    , Auxiliary.cursorDouble(historyCursor, "SkidkaPartneraKarta")//
                    , Auxiliary.cursorDouble(historyCursor, "NakopitelnyeSkidki")//
            );*/

			double soSkidkoy = Request_NomenclatureBase.calculateCenaSoSkidkoy(
					CENA
					, testSkidka
					, Request_NomenclatureBase.calculateVidSkidki(Auxiliary.cursorDouble(historyCursor, "Nacenka"), VID_SKIDKI)
					, MIN_CENA
					, Auxiliary.cursorDouble(historyCursor, "Nacenka")
			);
			/*System.out.println(Artikul+": "+Naimenovanie+": "+VID_SKIDKI+": "+testSkidka);
			if (testSkidka > 0) {
				if(testSkidka<MIN_CENA){
					testSkidka=MIN_CENA;
				}else{
					if(testSkidka>MAX_CENA){
						testSkidka=MAX_CENA;
					}
				}
				soSkidkoy = testSkidka;
			}*/

			//final double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
			final double CENA_SO_SKIDKOY = soSkidkoy;
			/*final double CENA_SO_SKIDKOY = Request_NomenclatureBase.calculateCenaSoSkidkoy(
					CENA
					, testSkidka
					, Request_NomenclatureBase.calculateVidSkidki(Auxiliary.cursorDouble(historyCursor, "Nacenka"), VID_SKIDKI)
					, MIN_CENA
					, Auxiliary.cursorDouble(historyCursor, "Nacenka")
			);*/
			final double SKIDKA = testSkidka;
			Task click = new Task(){
				@Override
				public void doTask(){
					if(!mIsOrderEditable){
						return;
					}
					if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
						showDialog(IDD_PAYMENT_NOT_SELECT);
						return;
					}
					System.out.println("add from history panel " + Artikul
							+ ", CENA " + CENA
							+ ", SKIDKA " + SKIDKA
							+ ", VID_SKIDKI " + VID_SKIDKI
							+ ", CENA_SO_SKIDKOY" + CENA_SO_SKIDKOY
					);

					Auxiliary.hideSoftKeyboard(Activity_Bid.this);
					FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
					if(foodstuffData.IsFoodstuffAlreadyInList(_IDRRef)){
						showDialog(IDD_ALREADY_IN_LIST);
						return;
					}
					double min = MIN_CENA;
					if(!mIsCRAvailable){
						min = 0;
					}
					//min=0;
					foodstuffData.newFoodstuff(//
							_IDRRef//
							, Artikul//
							, Naimenovanie//
							, EdinicyIzmereniyaID//
							, EdinicyIzmereniyaNaimenovanie//
							, COUNT// data.getDoubleExtra(COUNT, 0.00D)//
							, CENA// data.getDoubleExtra(CENA, 0.00D)//
							, CENA_SO_SKIDKOY// data.getDoubleExtra(CENA_SO_SKIDKOY, 0.00D)//
							, min//MIN_CENA//minCena(_IDRRef, mAppInstance.getCurrentAgent().getAgentIDstr())// MIN_CENA// data.getDoubleExtra(MIN_CENA, 0.00D)//
							, MAX_CENA// data.getDoubleExtra(MAX_CENA, 0.00D)//
							, SKIDKA//data.getDoubleExtra(SKIDKA, 0.00D)//
							, VID_SKIDKI// data.getStringExtra(VID_SKIDKI)//
							, MIN_NORMA// data.getDoubleExtra(MIN_NORMA, 0.00D)//
							, KOEPHICIENT//data.getDoubleExtra(KOEPHICIENT, 0.00D)//
							, BASE_PRICE// data.getDoubleExtra(BASE_PRICE, 0.00D)//
							, LAST_PRICE// data.getDoubleExtra(LAST_PRICE, 0.00D)//
							//, data.getStringExtra(SKIDKA_PROCENT)//
							//, data.getStringExtra(SKIDKA_NAIMENOVANIE)//
					);

					System.out.println("done add from history panel");
					UpdateAfterAddingNomenclature();
					System.out.println("mShippingDate " + mShippingDate.toString());
					Popup_EditNomenclatureCountPrice popup = new Popup_EditNomenclatureCountPrice(mBtnArticle, mOnPopupClose, mBidData.getFoodStuffs().getFoodstuff(0)//
							, mShippingDate.getTimeInMillis()
					);
					popup.showLikeQuickAction();
					//System.out.println("3");
				}
			};
			Task photo = new Task(){
				@Override
				public void doTask(){
					Intent intent = new Intent();
					intent.putExtra(sweetlife.android10.reports.ActivityPhoto.artikulField, Artikul);
					intent.putExtra(sweetlife.android10.reports.ActivityPhoto.nameField, Naimenovanie);
					intent.setClass(Activity_Bid.this, sweetlife.android10.reports.ActivityPhoto.class);
					Activity_Bid.this.startActivity(intent);
				}
			};
			int artikulBackground = 0;
			int naimenovanieBackGround = 0;
			int cenaBackground = 0;
			String LastSell = Auxiliary.cursorString(historyCursor, "LastSell");
			//row.child("LastSell").value.property.value();
			if(LastSell != null){
				if(LastSell.length() > 0){
					try{
						java.util.Date d = DateUtils.parseDate(LastSell, mDateFormatStrings);
						java.util.Calendar now = Calendar.getInstance();
						now.set(Calendar.DAY_OF_MONTH, 1);
						now.add(Calendar.DAY_OF_MONTH, -1);
						//now.add(Calendar.DAY_OF_YEAR, -14);
						if(d.before(now.getTime())){
							artikulBackground = 0xffff6666;
						}
						//System.out.println(d+" / "+now.getTime());
					}catch(Throwable tr){
						//tr.printStackTrace();
					}
				}
			}
			//double CenyNomenklaturySklada = Numeric.string2double(row.child("Cena").value.property.value());
			//double TekuschieCenyOstatkovPartiy = Numeric.string2double(row.child("BasePrice").value.property.value());
			//int procent = (int) (100.0 * (CenyNomenklaturySklada - TekuschieCenyOstatkovPartiy) / TekuschieCenyOstatkovPartiy);
			int procent = (int)(100.0 * (CENA - BASE_PRICE) / BASE_PRICE);
			//System.out.println(Artikul+"."+Naimenovanie+":"+CENA+"/"+BASE_PRICE+"="+procent);
			if(procent >= 25){
				cenaBackground = Settings.colorNacenka25;//0xffff9966;
			}
			if(Auxiliary.cursorDouble(historyCursor, "mustListId") > 0){
				naimenovanieBackGround = Settings.colorTop20;
			}
			historyArtikul.cell(Artikul, artikulBackground, click);
			historyNomenklatura.cell(Naimenovanie, naimenovanieBackGround, click);
			if(Naimenovanie.startsWith("СТМ:")){
				historyProizvoditel.cell(Auxiliary.cursorString(historyCursor, "ProizvoditelNaimenovanie"), Settings.colorSTM);
			}else{
				historyProizvoditel.cell(Auxiliary.cursorString(historyCursor, "ProizvoditelNaimenovanie"), Settings.colorTransparent);
			}

			historyMinKol.cell(Auxiliary.cursorString(historyCursor, "MinNorma"), click);
			historyKolMest.cell("" + KOEPHICIENT, click);
			historyEdIzm.cell(EdinicyIzmereniyaNaimenovanie, click);
			historyCena.cell("" + CENA, cenaBackground, click);
			historyRazmSkidki.cell(DecimalFormatHelper.format(SKIDKA), click);
			historyVidSkidki.cell(VID_SKIDKI, click);
			historyPoslCena.cell(DecimalFormatHelper.format(LAST_PRICE), click, lastSellCount);

			historyMinCena.cell("" + MIN_CENA, click);
			historyMaxCena.cell(DecimalFormatHelper.format(MAX_CENA), click);
			historyPhoto.cell("...", photo);
			//historyNomenklatureID.item(row.child("_IDRRef").value.property.value(), click);
			/*if (i == dataPageSize - 1) {
				historyHasMoreData = true;
			}*/
			//cntr++;
			//}//else{
			//i=i-1;
			//}
			//rowNum++;
			//System.out.println(Artikul+": "+Naimenovanie+": "+VID_SKIDKI);
		}
		//System.out.println("flipData ");
		//currentSizeHistory=0;
		//System.out.println("historyFillColumns done");
		if(historyCursor != null){
			historyCursor.close();
			return;
		}
		LogHelper.debug(this.getClass().getCanonicalName() + ".historyFillColumns done");
	}

	/*void mustFillColumns(boolean requery) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".mustFillColumns start");
		if (requery) {
			requeryMustData();
		}
		mustArtikul.clear();
		mustNomenklatura.clear();
		mustProizvoditel.clear();
		mustMinKol.clear();
		mustKolMest.clear();
		mustEdIzm.clear();
		mustCena.clear();
		mustRazmSkidki.clear();
		mustVidSkidki.clear();
		mustPoslCena.clear();
		mustMinCena.clear();
		mustMaxCena.clear();
		mustPhoto.clear();
		mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);

		while (mustCursor.moveToNext()) {
			final String Artikul = Auxiliary.cursorString(mustCursor, "Artikul");
			final String Naimenovanie = Auxiliary.cursorString(mustCursor, "Naimenovanie");
			final String _IDRRef = "x'" + Auxiliary.cursorBlob(mustCursor, "_IDRRef").toUpperCase() + "'";
			final String EdinicyIzmereniyaID = "x'" + Auxiliary.cursorBlob(mustCursor, "EdinicyIzmereniyaID").toUpperCase() + "'";
			final String EdinicyIzmereniyaNaimenovanie = Auxiliary.cursorString(mustCursor, "EdinicyIzmereniyaNaimenovanie");
			NomenclatureCountHelper helper = new NomenclatureCountHelper(//
					Auxiliary.cursorDouble(mustCursor, "MinNorma")//
					, Auxiliary.cursorDouble(mustCursor, "Koephphicient")//
			);
			final double COUNT = helper.ReCalculateCount(0);
			final double CENA = Auxiliary.cursorDouble(mustCursor, "Cena");
			final double MIN_CENA = Math.ceil(Auxiliary.cursorDouble(mustCursor, "MinCena") * 100.0) / 100.0;
			final double MAX_CENA = Auxiliary.cursorDouble(mustCursor, "MaxCena");
			final double SKIDKA = getRazmerSkidki(//
					Auxiliary.cursorDouble(mustCursor, "FiksirovannyeCeny")//
					, Auxiliary.cursorDouble(mustCursor, "SkidkaPartneraKarta")//
					, Auxiliary.cursorDouble(mustCursor, "NakopitelnyeSkidki")//
					, Auxiliary.cursorDouble(mustCursor, "Nacenka"));
			final String VID_SKIDKI = getVidSkidki(//
					Auxiliary.cursorDouble(mustCursor, "FiksirovannyeCeny")//
					, Auxiliary.cursorDouble(mustCursor, "SkidkaPartneraKarta")//
					, Auxiliary.cursorDouble(mustCursor, "NakopitelnyeSkidki")//
					, Auxiliary.cursorDouble(mustCursor, "Nacenka"));
			final double MIN_NORMA = Auxiliary.cursorDouble(mustCursor, "MinNorma");
			final double KOEPHICIENT = Auxiliary.cursorDouble(mustCursor, "Koephphicient");
			final double BASE_PRICE = Auxiliary.cursorDouble(mustCursor, "BasePrice");
			final double LAST_PRICE = Auxiliary.cursorDouble(mustCursor, "LastPrice");
			final double CENA_SO_SKIDKOY = getCenaSoSkidkoy(CENA//
					, Auxiliary.cursorDouble(mustCursor, "Nacenka")//
					, Auxiliary.cursorDouble(mustCursor, "FiksirovannyeCeny")//
					, Auxiliary.cursorDouble(mustCursor, "SkidkaPartneraKarta")//
					, Auxiliary.cursorDouble(mustCursor, "NakopitelnyeSkidki")//
			);
			Task click = new Task() {
				@Override
				public void doTask() {
					if (!mIsOrderEditable) {
						return;
					}
					if (mPaymentTypeAdapter.GetSelectedItem().isEmpty()) {
						showDialog(IDD_PAYMENT_NOT_SELECT);
						return;
					}
					Auxiliary.hideSoftKeyboard(Activity_Bid.this);
					FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
					if (foodstuffData.IsFoodstuffAlreadyInList(_IDRRef)) {
						showDialog(IDD_ALREADY_IN_LIST);
						return;
					}
					double min = MIN_CENA;
					if (!mIsCRAvailable) {
						min = 0;
					}
					foodstuffData.newFoodstuff(//
							_IDRRef//
							, Artikul//
							, Naimenovanie//
							, EdinicyIzmereniyaID//
							, EdinicyIzmereniyaNaimenovanie//
							, COUNT// data.getDoubleExtra(COUNT, 0.00D)//
							, CENA// data.getDoubleExtra(CENA, 0.00D)//
							, CENA_SO_SKIDKOY// data.getDoubleExtra(CENA_SO_SKIDKOY, 0.00D)//
							, min//MIN_CENA//minCena(_IDRRef, mAppInstance.getCurrentAgent().getAgentIDstr())// MIN_CENA// data.getDoubleExtra(MIN_CENA, 0.00D)//
							, MAX_CENA// data.getDoubleExtra(MAX_CENA, 0.00D)//
							, SKIDKA//data.getDoubleExtra(SKIDKA, 0.00D)//
							, VID_SKIDKI// data.getStringExtra(VID_SKIDKI)//
							, MIN_NORMA// data.getDoubleExtra(MIN_NORMA, 0.00D)//
							, KOEPHICIENT//data.getDoubleExtra(KOEPHICIENT, 0.00D)//
							, BASE_PRICE// data.getDoubleExtra(BASE_PRICE, 0.00D)//
							, LAST_PRICE// data.getDoubleExtra(LAST_PRICE, 0.00D)//
							);
					UpdateAfterAddingNomenclature();
					Popup_EditNomenclatureCountPrice popup = new Popup_EditNomenclatureCountPrice(mBtnArticle, mOnPopupClose, mBidData.getFoodStuffs().getFoodstuff(0)//
					);
					popup.showLikeQuickAction();
				}
			};
			Task photo = new Task() {
				@Override
				public void doTask() {
					Intent intent = new Intent();
					intent.putExtra(sweetlife.horeca.reports.ActivityPhoto.artikulField, Artikul);
					intent.putExtra(sweetlife.horeca.reports.ActivityPhoto.nameField, Naimenovanie);
					intent.setClass(Activity_Bid.this, sweetlife.horeca.reports.ActivityPhoto.class);
					Activity_Bid.this.startActivity(intent);
				}
			};
			int artikulBG = Settings.colorTop20;

			int naimenovanieBG = Settings.colorTop20;
			int procent = (int) (100.0 * (CENA - BASE_PRICE) / BASE_PRICE);
			if (procent >= 25) {
				naimenovanieBG = Settings.colorNacenka25;
				artikulBG=Settings.colorNacenka25;
			}
			
			mustArtikul.cell(Artikul, artikulBG, click);
			mustNomenklatura.cell(Naimenovanie, naimenovanieBG, click);
			mustProizvoditel.cell(Auxiliary.cursorString(mustCursor, "ProizvoditelNaimenovanie"), artikulBG, click);
			mustMinKol.cell(Auxiliary.cursorString(mustCursor, "MinNorma"), artikulBG, click);
			mustKolMest.cell("" + KOEPHICIENT, artikulBG, click);
			mustEdIzm.cell(EdinicyIzmereniyaNaimenovanie, artikulBG, click);
			mustCena.cell("" + BASE_PRICE, artikulBG, click);
			mustRazmSkidki.cell(DecimalFormatHelper.format(SKIDKA), artikulBG, click);
			mustVidSkidki.cell(VID_SKIDKI, artikulBG, click);
			mustPoslCena.cell(DecimalFormatHelper.format(LAST_PRICE), artikulBG, click);
			mustMinCena.cell("" + MIN_CENA, artikulBG, click);
			mustMaxCena.cell(DecimalFormatHelper.format(MAX_CENA), artikulBG, click);
			mustPhoto.cell("...", photo);
		}
		LogHelper.debug(this.getClass().getCanonicalName() + ".mustFillColumns done");
	}*/
	void addByArtikul(String art){
		String clientID = "0";
		String polzovatelID = "0";
		String dataOtgruzki = "0";
		String sklad = "0";
		try{
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
			polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		}catch(Throwable ttt){
			ttt.printStackTrace();
		}
		String sql = Request_NomenclatureBase.composeSQL(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, art
				, ISearchBy.SEARCH_ARTICLE
				, false//
				, false//
				, sklad//
				, 200//
				, 0, false, false, false, null, null, false);
		Bough itemInfo = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//System.out.println(sql+" - "+itemInfo.dumpXML());
		if(itemInfo.children.size() > 0){
			int n = 0;
			double CENA = Numeric.string2double(itemInfo.children.get(n).child("Cena").value.property.value());
			double SKIDKA = Numeric.string2double(itemInfo.children.get(n).child("Skidka").value.property.value());
			String VID_SKIDKI = itemInfo.children.get(n).child("VidSkidki").value.property.value();
			double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
			FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
			foodstuffData.newFoodstuff(//
					"x'" + itemInfo.children.get(n).child("_IDRRef").value.property.value() + "'"//
					, art
					, itemInfo.children.get(n).child("Naimenovanie").value.property.value()//
					, "x'" + itemInfo.children.get(n).child("EdinicyIzmereniyaID").value.property.value() + "'"//
					, itemInfo.children.get(n).child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
					, Numeric.string2double(itemInfo.children.get(n).child("MinNorma").value.property.value())//
					, CENA//
					, CENA_SO_SKIDKOY//
					, Numeric.string2double(itemInfo.children.get(n).child("MinCena").value.property.value())//
					, Numeric.string2double(itemInfo.children.get(n).child("MaxCena").value.property.value())//
					, SKIDKA//
					, VID_SKIDKI//
					, Numeric.string2double(itemInfo.children.get(n).child("MinNorma").value.property.value())//
					, Numeric.string2double(itemInfo.children.get(n).child("Koephphicient").value.property.value())//
					, Numeric.string2double(itemInfo.children.get(n).child("BasePrice").value.property.value())//
					, Numeric.string2double(itemInfo.children.get(n).child("LastPrice").value.property.value())//
			);
		}
	}

	void promptImport(final String[] artikuls, final String[] names, boolean flagImport){
		if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
			showDialog(IDD_PAYMENT_NOT_SELECT);
			return;
		}
		try{
/*
			final They<Integer> defaultSelection = new They<Integer>();
			String clientID = "0";
			String polzovatelID = "0";
			String dataOtgruzki = "0";
			String sklad = "0";
			try {
				clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
				dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
				sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			} catch (Throwable ttt) {
				ttt.printStackTrace();
			}

			String custom = "1=0";
			for (int i = 0; i < artikuls.length; i++) {
				if (artikuls[i].trim().length() > 3) {
					custom = custom + " or n.[Artikul] = '" + artikuls[i] + "'";
				}
			}
			//if (flagSelectAll)
			//System.out.println("compose 2:");
			String sql = Request_NomenclatureBase.composeSQL(//
					dataOtgruzki//
					, clientID//
					, polzovatelID//
					, ""//
					, ""//
					, custom//
					, ISearchBy.SEARCH_CUSTOM//
					, false//
					, false//
					, sklad//
					, 200//
					, 0, false, false, false, null, null);
			final Bough itemInfo = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
			String negativeButtonTitle = null;
			Task callbackNegativeBtn = null;
			if (flagImport) {
				for (int i = 0; i < artikuls.length; i++) {
					String a = artikuls[i].trim();
					for (int n = 0; n < itemInfo.children.size(); n++) {
						String f = itemInfo.children.get(n).child("Artikul").value.property.value().trim();
						if (f.equals(a)) {
							defaultSelection.insert(defaultSelection.size(), i);
							break;
						}
					}
				}
			} else {
				negativeButtonTitle = "Закрыть";
				callbackNegativeBtn = new Task() {
					@Override
					public void doTask() {
						if (mHasChanges) saveChanges();
						Activity_Bid.this.finish();
					}
				};
			}
			CharSequence[] items = new String[artikuls.length];
			for (int i = 0; i < artikuls.length; i++) {
				items[i] = names[i];
				for (int n = 0; n < itemInfo.children.size(); n++) {
					String f = itemInfo.children.get(n).child("Artikul").value.property.value().trim();
					if (f.equals(artikuls[i])) {
						double CENA = Numeric.string2double(itemInfo.children.get(n).child("Cena").value.property.value());
						double SKIDKA = Numeric.string2double(itemInfo.children.get(n).child("Skidka").value.property.value());
						String VID_SKIDKI = itemInfo.children.get(n).child("VidSkidki").value.property.value();
						double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
						items[i] = names[i] + ", " + CENA_SO_SKIDKOY + "р.";
					}
				}
			}*/
			Vector<CheckRow> rows = new Vector<CheckRow>();
			for(int ii = 0; ii < artikuls.length; ii++){
				rows.add(new CheckRow(flagImport, artikuls[ii], names[ii]));
			}
			Auxiliary.pickFilteredMultiChoice(this, rows
					, "Закрыть", new Task(){
						@Override
						public void doTask(){
							if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
								showDialog(IDD_PAYMENT_NOT_SELECT);
								return;
							}
							if(mHasChanges)
								saveChanges();
							Activity_Bid.this.finish();
						}
					}
					, "Добавить", new Task(){
						@Override
						public void doTask(){
							for(int kk = 0; kk < rows.size(); kk++){
								if(rows.get(kk).checked){
									System.out.println("ok " + rows.get(kk).id);
									addByArtikul(rows.get(kk).id.trim());
								}
							}
							UpdateAfterAddingNomenclature();
						}
					}
					, null, null
					/*, "Оценить", new Task(){
						@Override
						public void doTask(){
							Vector<String> arts = new Vector<String>();
							for(int kk = 0; kk < rows.size(); kk++){
								if(rows.get(kk).checked){
									arts.add(rows.get(kk).id.trim());
									promptSendGoodBad(arts);
								}
							}
						}
					}*/
					, flagImport ? "Импорт" : "Рекомендации"
					, new Task(){
						@Override
						public void doTask(){
							//
						}

						@Override
						public void doTask2(String art, String rr){
							System.out.println("onState " + art + ": " + rr);
							poslatPalecArtikul(art, rr);
						}
					}
			);
			//👍👎🖒
			/*
			Auxiliary.pickMultiChoice(this, items, defaultSelection
					, "Добавить", new Task() {
						@Override
						public void doTask() {
							FoodstuffsData foodstuffData = mBidData.getFoodStuffs();
							for (int i = 0; i < artikuls.length; i++) {
								boolean checked = false;
								for (int ds = 0; ds < defaultSelection.size(); ds++) {
									if (defaultSelection.at(ds) == i) {
										checked = true;
										break;
									}
								}
								if (checked) {
									for (int n = 0; n < itemInfo.children.size(); n++) {
										String f = itemInfo.children.get(n).child("Artikul").value.property.value().trim();
										if (f.equals(artikuls[i])) {
											double CENA = Numeric.string2double(itemInfo.children.get(n).child("Cena").value.property.value());
											double SKIDKA = Numeric.string2double(itemInfo.children.get(n).child("Skidka").value.property.value());
											String VID_SKIDKI = itemInfo.children.get(n).child("VidSkidki").value.property.value();
											double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
											foodstuffData.newFoodstuff(//
													"x'" + itemInfo.children.get(n).child("_IDRRef").value.property.value() + "'"//
													, artikuls[i]//
													, itemInfo.children.get(n).child("Naimenovanie").value.property.value()//
													, "x'" + itemInfo.children.get(n).child("EdinicyIzmereniyaID").value.property.value() + "'"//
													, itemInfo.children.get(n).child("EdinicyIzmereniyaNaimenovanie").value.property.value()//
													, Numeric.string2double(itemInfo.children.get(n).child("MinNorma").value.property.value())//
													, CENA//
													, CENA_SO_SKIDKOY//
													, Numeric.string2double(itemInfo.children.get(n).child("MinCena").value.property.value())//
													, Numeric.string2double(itemInfo.children.get(n).child("MaxCena").value.property.value())//
													, SKIDKA//
													, VID_SKIDKI//
													, Numeric.string2double(itemInfo.children.get(n).child("MinNorma").value.property.value())//
													, Numeric.string2double(itemInfo.children.get(n).child("Koephphicient").value.property.value())//
													, Numeric.string2double(itemInfo.children.get(n).child("BasePrice").value.property.value())//
													, Numeric.string2double(itemInfo.children.get(n).child("LastPrice").value.property.value())//
											);
											break;
										}
									}
								}
							}
							UpdateAfterAddingNomenclature();
						}
					}
					, negativeButtonTitle, callbackNegativeBtn
					, "Рекомендации для клиента"
			);*/
		}catch(Throwable t){
			t.printStackTrace();
			Auxiliary.warn("Не скопированы данные для импорта", this);
		}
	}

	void poslatPalecArtikul(String artikul, String state){
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String kodKlienta = ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/Planshet/ocenkarekomendacii?KodKlienta=" + kodKlienta + "&Artikul=" + artikul + "&Ocenka=" + state;
					//String url = "https://testservice.swlife.ru/golovanew/hs/Planshet/ocenkarekomendacii?KodKlienta=80005&Artikul=2838&Ocenka=" + (ok ? "1" : "0" );
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(b, "UTF-8");
					System.out.println(txt);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//
			}
		}).status.is("Отправка...").start(this);
	}

	/*
		void promptSendGoodBad(Vector<String> arts){
			if(arts.size() > 0){
				Auxiliary.pick3Choice(this,"Оценка", "Отмеченные артикулы подходят для рекомендаций по этому клиенту"
						, "Да", new Task(){
							@Override
							public void doTask(){
								sendGoodBad(true, arts);
							}
						}, "Нет", new Task(){
							@Override
							public void doTask(){
								sendGoodBad(false, arts);
							}
						},null,null
				);
			}
		}

		void sendGoodBad(final boolean ok, final Vector<String> arts){
			new Expect().task.is(new Task(){
				@Override
				public void doTask(){
					for(int ii = 0; ii < arts.size(); ii++){
						try{
							String url = "https://testservice.swlife.ru/golovanew/hs/Planshet/ocenkarekomendacii?KodKlienta=80005&Artikul=2838&Ocenka=" + (ok ? "1" : "0" );
							byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							String txt = new String(b, "UTF-8" );
							System.out.println(txt);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}).afterDone.is(new Task(){
				@Override
				public void doTask(){
					Auxiliary.warn("Оценка отправлена" , Activity_Bid.this);
				}
			}).status.is("Отправка..." ).start(this);
		}
	*/
	void doKommercheskoePredlojenie(){
		final Numeric dateDay = new Numeric();
		final Numeric artCount = new Numeric().value(50);
		final Numeric tipSelect = new Numeric().value(1);
		RedactSingleChoice choice = new RedactSingleChoice(this);
		String sql = "select _idrref as _idrref,naimenovanie as naimenovanie,kod as kod from TipyTorgovihTochek where deletionmark=x'00' order by naimenovanie";
		final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//String[] rows = new String[b.children.size() + 1];
		//rows[0] = "Любой тип";
		choice.item("По утвержденным фикс. ценам");
		choice.item("По истории продаж");
		for(int i = 0; i < b.children.size(); i++){
			//rows[i + 1] = b.children.get(i).child("naimenovanie").value.property.value();
			//System.out.println(b.children.get(i).dumpXML());
			choice.item(b.children.get(i).child("naimenovanie").value.property.value());
		}
		Auxiliary.pick(this, "Коммерческое предложение"//
				, new SubLayoutless(this)//
						.child(choice.selection.is(tipSelect)//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new Decor(this).labelText.is("количество строк, не более").labelAlignLeftTop().labelStyleSmallNormal()//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 1.7).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new RedactNumber(this).number.is(artCount)//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 2.1).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new Decor(this).labelText.is("дата заказов").labelAlignLeftTop().labelStyleSmallNormal()//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 3.4).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(dateDay)//
								//.hidden().is(selection.equals(0))//
								.left().is(Auxiliary.tapSize * 0.1).top().is(Auxiliary.tapSize * 3.7).width().is(Auxiliary.tapSize * 8.8).height().is(Auxiliary.tapSize * 0.8))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 7)//
				, "Получить", new Task(){
					@Override
					public void doTask(){
						String kodTip = "";
						if(tipSelect.value() > 1){
							kodTip = b.children.get(tipSelect.value().intValue() - 1).child("kod").value.property.value();
						}
						sendKommercheskoePredlojenie(tipSelect.value().intValue() == 0 ? true : false, artCount.value().intValue(), dateDay.value().longValue(), kodTip);
					}
				}, null, null, null, null);
	}

	String enc(String s){
		try{
			return URLEncoder.encode(s, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			return s;
		}
	}

	void sendKommercheskoePredlojenie(boolean fixNotSale, int artCount, long timestamp, String kodTip){
		//http://89.109.7.162/shatov/hs/KomPredlozenie/88668?КолТоваров=500&ДеньПродаж=20191001&ТипТТ=17
		//System.out.println(artCount + "/" + timestamp + "/" + kodTip);

		final Note result = new Note();
		final Note url = new Note();
		if(!fixNotSale){
			String counter = "";
			if(artCount > 0){
				counter = "?" + enc("КолТоваров") + "=" + artCount;
			}else{
				counter = "?" + enc("КолТоваров") + "=50";
			}
			String day = "";
			if(timestamp > 0){
				day = "&" + enc("ДеньПродаж") + "=" + Auxiliary.short1cDate.format(new Date(timestamp));
			}
			String tip = "";
			if(kodTip.length() > 0){
				tip = "&" + enc("ТипТТ") + "=" + kodTip;
			}
			url.value(Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
					+ "/hs/KomPredlozenie/" + ApplicationHoreca.getInstance().getClientInfo().getKod() //
					//+ "/50"
					+ counter + day + tip)
			;
		}else{
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.HOUR, 24);
			String datestring = "" + Auxiliary.short1cDate.format(tomorrow.getTime());
			if(timestamp > 0){
				datestring = "" + Auxiliary.short1cDate.format(new Date(timestamp));
			}
			url.value(Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/KomPredlozenie/FiksCena/"
					//+ "261032"
					+ ApplicationHoreca.getInstance().getClientInfo().getKod().trim()
					+ "/" + datestring + "?КолТоваров=" + artCount);
		}
		System.out.println(url);
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//byte[] b = Auxiliary.loadFileFromPublicURL(url);
					byte[] b = Auxiliary.loadFileFromPrivateURL(url.value(), Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());

					String txt = new String(b, "UTF-8");
					result.value(txt);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Bough b = Bough.parseJSON(result.value());
				//System.out.println(b.dumpXML());
				//System.out.println(url);

				//Vector<String> artikuls = new Vector<String>();
				Vector<Bough> all = b.children("КомПредложение");

				//System.out.println(all.size());
				if(b.child("Статус").value.property.value().equals("0") && all.size() > 0){
					parsePromptImport(all);
					/*
					String[] artikuls = new String[all.size()];
					String[] names = new String[all.size()];
					for (int i = 0; i < all.size(); i++) {
						artikuls[i] = all.get(i).child("Артикул").value.property.value();
						names[i] = all.get(i).child("Артикул").value.property.value() + ": " + all.get(i).child("НаименованиеТовара").value.property.value();
					}
					promptImport(artikuls, names);
*/
				}else{
					Auxiliary.warn(b.child("Сообщение").value.property.value(), Activity_Bid.this);
				}
			}
		}).status.is("Отправка...").start(this);
	}

	void parsePromptImport(Vector<Bough> all){

		String[] artikuls = new String[all.size()];
		String[] names = new String[all.size()];
		for(int i = 0; i < all.size(); i++){
			artikuls[i] = all.get(i).child("Артикул").value.property.value();
			names[i] = all.get(i).child("Артикул").value.property.value() + ": " + all.get(i).child("НаименованиеТовара").value.property.value();
		}
		promptImport(artikuls, names, true);

	}

	void sendUpdateOrder(){
		mBidData.getFoodStuffs().WriteToDataBase(mDB);
		//mBidData.getServices().WriteToDataBase(mDB);
		//mBidData.getTrafiks().WriteToDataBase(mDB);
		ZayavkaPokupatelya bid = mBidData.getBid();
		bid.setSumma(mBidData.getFoodStuffs().getAmount() + mBidData.getServices().getCount());
		bid.setContract(mContractsAdapter.getSelectedItem().getID());
		bid.setTipOplaty(mPaymentTypeAdapter.GetSelectedItem().getID());
		bid.setComment(mEditComment.getText().toString() + "|" + mEditCustNum.getText().toString());
		bid.setSebestoimost(mBidData.getFoodStuffs().getAmount());
		bid.setShippingDate(mShippingDate.getTimeInMillis());
		bid.writeToDataBase(mDB);

		final String post = "[{\"Номер\":\"" + nomerDokumenta1C + "\","
				+ Activity_UploadBids.composeUploadOrderString(mBidData.getBid().getNomer(), nomerDokumentaTablet)
				+ "}]";
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZakaziPokupatelya/" + Cfg.whoCheckListOwner();
		//final Note result = new Note();
		final Bough result = new Bough();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				System.out.println("post: " + post);
				try{
					Bough txt = Auxiliary.loadTextFromPrivatePOST(url, post, 300 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					result.children.add(txt);
					//Bough data = Bough.parseJSON(txt.child("raw").value.property.value());
					//Bough bb = Bough.parseJSONorThrow(response);
					/*System.out.println("post result is " + data.dumpXML());
					result.value(
							data.child("Сообщение").value.property.value()
									+ " " + data.child("ДанныеПоЗаказам").child("Сообщение").value.property.value()
									+ " " + data.child("ДанныеПоЗаказам").child("Заказы").child("Сообщение").value.property.value()
					);*/
				}catch(Throwable t){
					t.printStackTrace();
					//result.value(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
							@Override
							public void doTask(){
								//System.out.println("sendUpdateOrder result " + result.value());
								//Auxiliary.inform("Отправка заказа: " + result.value(), Activity_Bid.this);
								mBidData.getBid().deleteOrder(ApplicationHoreca.getInstance().getDataBase());
								//Activity_Bid.this.finish();
								String response = result.child("result").child("raw").value.property.value();
								try{
									Bough bb = Bough.parseJSONorThrow(response);
									System.out.println("bb is " + bb.dumpXML());
									if(bb.children.size() > 0){
										String msg = "Выгрузка (" + result.child("result").child("code").value.property.value()
												+ ", " + result.child("result").child("message").value.property.value() + "):\n"
												+ bb.child("Сообщение").value.property.value();
										//Activity_UploadBids.buildDialogResult( Activity_Bid.this,"Отправка заказа", bb);
										//Activity_UploadBids.buildDialogResultAndClose( Activity_Bid.this,"Отправка заказа", bb,Activity_Bid.this);
										UploadOrderResult.startUploadResultDialog( Activity_Bid.this,bb.name.is("root"));
										//Activity_Bid.this.finish();
									}else{
										System.out.println("Empty " + result.dumpXML());
										String msg = " \nВозможны ошибки при выгрузке" //+ result.dumpXML();
												+ "\n\nПроверьте статус заказов в отчёте, возможно необходимо удалить повторы"//
												+ "\n\nТекст ответа:"//
												+ result.dumpXML().substring(0, 160).replace("\n", "").replace("  ", " ");
										Auxiliary.alertBreak(msg, Activity_Bid.this);
									}
								}catch(Throwable t){
									String msg = "/ Ошибка /" + t.toString() + "/";
									Auxiliary.alertBreak(msg, Activity_Bid.this);
								}
							}
						}
		).status.is("Подождите...").start(this);
	}

	void promptRecomendatciiSelection(){
		if(nomerDokumenta1C.length() > 1){
			if(mHasChanges){
				sendUpdateOrder();
			}else{
				Activity_Bid.this.finish();
			}
		}else{
			String sql = Request_NomenclatureBase.composeSQLall_Old(//.composeSQLall(//
					DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
					, ApplicationHoreca.getInstance().getClientInfo().getID()//
					, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
					, null//DateTimeHelper.SQLDateString(fr)//
					, null//DateTimeHelper.SQLDateString(to)//
					, ""//
					, ISearchBy.SEARCH_NAME
					, false//
					, false//
					, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
					, 50//gridPageSize * 3//
					//, gridHistory.dataOffset.property.value().intValue()//
					, 0//
					, false//
					, false, null, null, false, false, null, null, null
					, false//,filterBySTM.value()
					, false
					, true
					, false
					, false, false
			);
			//System.out.println(sql);
			Bough itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(itemsData.dumpXML());
			Vector<Bough> all = itemsData.children("row");
			Vector<String> artikuls = new Vector<String>();
			Vector<String> names = new Vector<String>();
			FoodstuffsData foodstuffsData = this.mBidData.getFoodStuffs();
			for(int i = 0; i < all.size(); i++){
				boolean existedArt = false;
				for(int kk = 0; kk < foodstuffsData.getCount(); kk++){
					ZayavkaPokupatelya_Foodstaff zayavkaPokupatelya_Foodstaff = foodstuffsData.getFoodstuff(kk);
					if(zayavkaPokupatelya_Foodstaff.getArtikul().trim().equals(all.get(i).child("Artikul").value.property.value().trim())){
						existedArt = true;
						break;
					}
				}
				if(!existedArt){
					artikuls.add(all.get(i).child("Artikul").value.property.value());
					double CENA = Numeric.string2double(all.get(i).child("Cena").value.property.value());
					double SKIDKA = Numeric.string2double(all.get(i).child("Skidka").value.property.value());
					String VID_SKIDKI = all.get(i).child("VidSkidki").value.property.value();
					double CENA_SO_SKIDKOY = SKIDKA > 0 ? SKIDKA : CENA;
					names.add(all.get(i).child("Artikul").value.property.value()
							+ ": " + all.get(i).child("Naimenovanie").value.property.value()
							+ ", " + CENA_SO_SKIDKOY + "р."
					);
				}
			}
			String[] arrArt = new String[artikuls.size()];
			String[] namArt = new String[artikuls.size()];
			if(artikuls.size() > 0){
				promptImport(artikuls.toArray(arrArt), names.toArray(namArt), false);
			}else{
				if(mPaymentTypeAdapter.GetSelectedItem().isEmpty()){
					showDialog(IDD_PAYMENT_NOT_SELECT);
					return;
				}
				if(mHasChanges){
					saveChanges();
				}
				Activity_Bid.this.finish();
			}
		}
	}
}
