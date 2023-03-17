package sweetlife.android10.ui;

import java.io.FileOutputStream;
import java.util.*;

import sweetlife.android10.supervisor.*;
import reactive.ui.Auxiliary;
import reactive.ui.Expect;
import reactive.ui.RawSOAP;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.data.common.ZoomListCursorAdapter.OnSelectedPositionChangeListener;
import sweetlife.android10.data.nomenclature.NomenclaturaAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureByGroupListAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.data.nomenclature.NomenclatureGroupsListAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureSavedData;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_History;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import sweetlife.android10.database.nomenclature.Request_NomenclatureGroups;
import sweetlife.android10.database.nomenclature.Request_Search;
import sweetlife.android10.supervisor.Report_Base;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.R;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import sweetlife.android10.log.LogHelper;
import tee.binding.*;
import tee.binding.task.*;
import tee.binding.it.*;

public class Activity_Nomenclature extends Activity_Base implements OnTabChangeListener, View.OnClickListener, ITableColumnsNames, ISearchBy {

    static int selectedSearchMode = 1;//SEARCH_ARTICLE;
    static int selectedSearchByMode = SEARCH_NAME;
    static int selectedSearchByKuhnya = 0;
    static int selectedSearchByTochka = 0;
    static int selectedSearchByRecept = 0;
    static String selectedSearchByReceptIDRREF = null;
    //static String selectedSearchByReceptTitle = "Без рецепта";
    static String selectedSearchTochkaIDRREF = "00";
    static String selectedSearchTochkaTitle = "Любой тип";
    static String lastClientID = "";
    MenuItem menuOtchety;
    //MenuItem menuOstatki;
    boolean _DEGUSTACIA_POISK = false;
    OnClickListener clickSearchMode = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //System.out.println("clickSearchMode");
            final Numeric r = new Numeric().value(selectedSearchMode);
            r.afterChange(new Task() {
                public void doTask() {
                    selectedSearchMode = r.value().intValue();
                    setupSearchModeSelection();
                    System.out.println(selectedSearchMode + ": " + selectedSearchByMode);
                }
            });
            Auxiliary.pickSingleChoice(Activity_Nomenclature.this, new String[]{"Артикул", "Наименование", "Производитель"}, r);
        }
    };
    OnClickListener clickSearchKuhnya = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Numeric r = new Numeric().value(selectedSearchByKuhnya);
            r.afterChange(new Task() {
                public void doTask() {
                    selectedSearchByKuhnya = r.value().intValue();
                    setupSearchModeSelection();
                }
            });
            Auxiliary.pickSingleChoice(Activity_Nomenclature.this, new String[]{"Любая кухня", "Кавказская", "Европейская", "Итальянская", "Русская", "Японская", "Американская"}, r);
        }
    };
    OnClickListener clickSearchTochka = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //System.out.println("clickSearchTochka");
            String sql = "select _idrref as _idrref,naimenovanie as naimenovanie from TipyTorgovihTochek where deletionmark=x'00' order by naimenovanie";
            final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
            String[] rows = new String[b.children.size() + 1];
            rows[0] = "Любой тип";
            for (int i = 0; i < b.children.size(); i++) {
                rows[i + 1] = b.children.get(i).child("naimenovanie").value.property.value();
                //System.out.println(b.children.get(i).dumpXML());
            }
            System.out.println(selectedSearchByTochka + ", " + selectedSearchTochkaIDRREF);
            final Numeric r = new Numeric().value(selectedSearchByTochka);
            r.afterChange(new Task() {
                public void doTask() {
                    selectedSearchByTochka = r.value().intValue();
                    if (selectedSearchByTochka > 0) {
                        selectedSearchTochkaIDRREF = b.children.get(selectedSearchByTochka - 1).child("_idrref").value.property.value();
                        selectedSearchTochkaTitle = b.children.get(selectedSearchByTochka - 1).child("naimenovanie").value.property.value();
                    } else {
                        selectedSearchTochkaIDRREF = "00";
                        selectedSearchTochkaTitle = "Любой тип";
                    }
                    setupSearchModeSelection();
                    //System.out.println(selectedSearchByTochka+", "+selectedSearchTochkaIDRREF+": "+b.children.get(selectedSearchByTochka-1).child("naimenovanie").value.property.value());
                }
            });
            Auxiliary.pickSingleChoice(Activity_Nomenclature.this, rows, r);
        }
    };
    /*OnClickListener clickSearchReceipt = new OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("clickSearchReceipt");
            String sql = "select _IDRRef as _idrref,naimenovanie as naimenovanie from Receptii order by naimenovanie";
            final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
            System.out.println(b.dumpXML());

            final DataGrid dataGrid = new DataGrid(Activity_Nomenclature.this);
            final ColumnText columnText = new ColumnText();
            dataGrid.columns(new Column[]{
                    columnText.width.is(Auxiliary.tapSize * 10)
            });
            final Vector<AlertDialog> link=new Vector<AlertDialog>();
            //columnText.cell("test");
            //columnText.cell("2");
            final Note seekWord=new Note();
            Task refreshList = new Task() {
                public void doTask() {
                    //System.out.println("refreshList");
                    //if(dataGrid.columnsArray!=null) {
                        dataGrid.clearColumns();
                   // }
                    columnText.cell("Без рецепта",new Task(){
                        public void doTask() {
                            selectedSearchByReceptIDRREF = null;
                            selectedSearchByReceptTitle = "Без рецепта";
                            setupSearchModeSelection();
                            link.get(0).cancel();
                            SearchBtnClick();

                        }
                    });
                    for (int i = 0; i < b.children.size(); i++) {
                        final String name=b.children.get(i).child("naimenovanie").value.property.value();
                        final String _idrref=b.children.get(i).child("_idrref").value.property.value();
                        String filter=seekWord.value().trim().toUpperCase();
                        if(filter.length()<1 || (name.toUpperCase().indexOf(filter)>-1)) {
                            columnText.cell(name,new Task(){
                                public void doTask() {
                                    selectedSearchByReceptIDRREF = _idrref;
                                    selectedSearchByReceptTitle = name;
                                    if(selectedSearchByReceptTitle.length()>14){
                                        selectedSearchByReceptTitle=selectedSearchByReceptTitle.substring(0,14);
                                    }
                                    setupSearchModeSelection();
                                    link.get(0).cancel();
                                    SearchBtnClick();
                                }
                            });
                        }
                    }
                    dataGrid.refresh();
                }
            };
            //refreshList.start();
            seekWord.afterChange(refreshList);
            android.app.AlertDialog alertDialog=Auxiliary.pick(Activity_Nomenclature.this//
                    , null//
                    , new SubLayoutless(Activity_Nomenclature.this)//
                            .child(new RedactText(Activity_Nomenclature.this).text.is(seekWord)
                                    .left().is(Auxiliary.tapSize * 0.0)
                                    .top().is(Auxiliary.tapSize * 0.0)
                                    .width().is(Auxiliary.tapSize * 10)
                                    .height().is(Auxiliary.tapSize * 1)
                            )//
                            .child(dataGrid.noHead.is(true)//
                                    //.columns(new Column[]{
                                    //        columnText.width.is(Auxiliary.tapSize * 10)
                                    //})
                                    .left().is(Auxiliary.tapSize * 0.0)
                                    .top().is(Auxiliary.tapSize * 1.0)
                                    .width().is(Auxiliary.tapSize * 10)
                                    .height().is(Auxiliary.tapSize * 10)
                            )//
                            .width().is(Auxiliary.tapSize * 10)//
                            .height().is(Auxiliary.tapSize * 11)////
                    , null//
                    , null//
                    , null//
                    , null//
                    , null//
                    , null//
            );
            link.add(alertDialog);
        }
    };*/
    private TabHost mTabHost;
    private EditText mEditSearch;
    OnCheckedChangeListener mSearchRadioTextType = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.restartInput(mEditSearch);
            }
        }
    };
    private EditText mEditFromDate;
    private EditText mEditToDate;
    private EditText mEditHistorySearch;
    private Button seekHistoryButton;
    //private RadioButton mRadioActicule;
    //private RadioButton mRadioName;
    //private RadioButton mRadioVendor;
    private TextView mTextOrderAmount;
    //private Request_CR mRequestCR;
    private Request_Search mRequestSearch;
    private Request_History mRequestHistory;
    //private Request_MaxPrice mRequestMaxPrice;
    //private ListView mCRListView;
    //private ListView mMaxPriceListView;
    private ListView mSearchListView;
    private ListView mHistoryListView;
    private ListView mNomenclatureByGroupListView;
    private ExpandableListView mNomenclatureGroupsExpandableListView;
    private ZoomListCursorAdapter mCurrentAdapter;
    /*OnClickListener mRequestSkladClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            requestOstatki(v);
        }
    };*/

    OnClickListener mRequestCerificatClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //System.out.println("mRequestCerificatClick");
            Cursor cursor = mCurrentAdapter.getCursor();
            if (cursor != null && cursor.moveToFirst()) {
                int pos = mCurrentAdapter.getSelectedPosition();
                if (pos > -1) {
                    cursor.moveToPosition(pos);
                }
                final String artikul = cursor.getString(2);
                final String date=Auxiliary.short1cDate.format(new Date());
                final Note result=new Note();
                //final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Cert" + artikul+"_" + date + ".pdf";
				//final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Cert" + artikul+"_" + date + ".html";
				final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Cert" + artikul+"_" + date + ".xls";
                //http://service.swlife.ru/hrc120107/hs/ObmenCVoditelem/SertArt/105333/20201221/pdf
                final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
                        //+ "/hs/ObmenCVoditelem/SertArt/"+artikul+"/"+date+"/pdf" ;
						+ "/hs/ObmenCVoditelem/SertArt/"+artikul+"/"+date+"/xls" ;
				//final String url = "https://testservice.swlife.ru/golovanew/hs/ObmenCVoditelem/SertArt/61847/20180101/xls";
				//final String url = "https://testservice.swlife.ru/golovanew/hs/ObmenCVoditelem/SertArt/61847/20180101/pdf";
                System.out.println(""+url);
                Expect expectRequery = new Expect()//
                        .status.is("Подождите.....")//
                        .task.is(new Task() {
                            @Override
                            public void doTask() {
                                try {
                                    //preReport.exportXLS(toPath, Activity_Nomenclature.this);
                                    byte[] raw = android.util.Base64.decode(Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword()), Base64.DEFAULT);
									//byte[] raw = android.util.Base64.decode(Auxiliary.loadFileFromPrivateURL("https://testservice.swlife.ru/golovanew/hs/ObmenCVoditelem/SertArt/61847/20180101/pdf", "hrc297","Hm7LPRvg"), Base64.DEFAULT);
									//byte[] raw=Auxiliary.loadFileFromPrivateURL(url , Cfg.currentHRC(),Cfg.hrcPersonalPassword());
                                    FileOutputStream fileOutputStream = null;
                                    fileOutputStream = new FileOutputStream(toPath);
                                    fileOutputStream.write(raw, 0, raw.length);
                                    fileOutputStream.close();
                                }catch(Throwable t){
                                    t.printStackTrace();
                                    result.value(t.getMessage());
                                }
                            }
                        })//
                        .afterDone.is(new Task() {
                            @Override
                            public void doTask() {
                                //Auxiliary.warn("Файл Cert" + artikul+"_" + date + ".pdf сохранён в папку Download."+result.value(), Activity_Nomenclature.this);
								//Auxiliary.warn("Файл Cert" + artikul+"_" + date + ".html сохранён в папку Download. "+result.value(), Activity_Nomenclature.this);
								Auxiliary.warn("Файл Cert" + artikul+"_" + date + ".xls сохранён в папку Download. "+result.value(), Activity_Nomenclature.this);
                            }
                        })//
                        //.start(activityReports);
                        ;
                expectRequery.start(Activity_Nomenclature.this);
            } else {
                Auxiliary.warn("Найдите номенклатуру для обновления", Activity_Nomenclature.this);
            }
        }};
    String createPriceRequestBody(String[] artikul){
    	String xml="";
    	return xml;
	}
    OnClickListener mRequestPriceClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Cursor cursor = mCurrentAdapter.getCursor();
            if (cursor != null && cursor.moveToFirst()) {
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
                        + "\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/DanniePoTovaram\" xmlns:ns2=\"DanniePoTovaram\">"//
                        + "\n  <SOAP-ENV:Body>"//
                        + "\n    <ns2:Get>"//
                        + "\n      <ns2:Spisok>"//
                        ;
                do {
                    String artikul = cursor.getString(2);
                    xml = xml//
                            + "\n        <ns1:Str>"//
                            + "\n          <ns1:Artikul>" + artikul + "</ns1:Artikul>"//
                            + "\n          <ns1:NaSklade></ns1:NaSklade>"//
                            + "\n          <ns1:Dostupno></ns1:Dostupno>"//
                            + "\n          <ns1:TekuhayaCena></ns1:TekuhayaCena>"//
                            + "\n          <ns1:Prais></ns1:Prais>"//
                            + "\n          <ns1:MinPorog></ns1:MinPorog>"//
                            + "\n        </ns1:Str>"//
                    ;
                }
                while (cursor.moveToNext());
                xml = xml//
                        + "\n      </ns2:Spisok>"//
                        + "\n      <ns2:Otvetstvenniy>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod() + "</ns2:Otvetstvenniy>"//
                        + "\n    </ns2:Get>"//
                        + "\n  </SOAP-ENV:Body>"//
                        + "\n</SOAP-ENV:Envelope>"//
                ;
                sendRequestPrice(xml);
            } else {
                Auxiliary.warn("Найдите номенклатуру для обновления", Activity_Nomenclature.this);
            }
        }
    };
    private Request_NomenclatureBase mCurrentRequest;
    private NomenclatureGroupsListAdapter mNomenclatureGroupsListAdapter;
    private NomenclatureByGroupListAdapter mNomenclatureByGroupListAdapter;
    OnChildClickListener mOnChildGroupClickListener = new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            System.out.println("mOnChildGroupClickListener.onChildClick ");
            //UIHelper.quickWarning("mOnChildGroupClickListener.onChildClick "+new java.util. Date() , Activity_Nomenclature.this);
            Cursor cursor = mNomenclatureGroupsListAdapter.getChild(groupPosition, childPosition);
            mNomenclatureByGroupListAdapter.setSelectedPosition(-1);
            String idrRef = Request_NomenclatureGroups.getIDRRef(cursor);
            String n = cursor.getString(2);
            //System.out.println("group "+n);
            //UIHelper.quickWarning(n, Activity_Nomenclature.this);
            mNomenclatureByGroupListAdapter.changeCursor(Request_NomenclatureGroups.wholeSubFolderContent(mDB, idrRef, _DEGUSTACIA_POISK));
			//mNomenclatureByGroupListAdapter.changeCursor(Request_NomenclatureGroups.RequestNomenclatureByParent(mDB, idrRef, _DEGUSTACIA_POISK));

            return false;
        }
    };
    private double mOrderAmount = 0;
    private Calendar mFromDate;
    private Calendar mToDate;
    private boolean mIsCRAvailable = true;
    private OnClickListener mFromDateClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mFromDate.set(year, monthOfYear, dayOfMonth);
                    mEditFromDate.setText(DateTimeHelper.UIDateString(mFromDate.getTime()));
                    SearchHistory(mEditHistorySearch.getText().toString());
                }
            };
            new DatePickerDialog(Activity_Nomenclature.this, dateSetListener, mFromDate.get(Calendar.YEAR), mFromDate.get(Calendar.MONTH), mFromDate.get(Calendar.DAY_OF_MONTH)).show();
        }
    };
    private OnClickListener mToDateClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mToDate.set(year, monthOfYear, dayOfMonth);
                    mEditToDate.setText(DateTimeHelper.UIDateString(mToDate.getTime()));
                    SearchHistory(mEditHistorySearch.getText().toString());
                }
            };
            new DatePickerDialog(Activity_Nomenclature.this, dateSetListener, mToDate.get(Calendar.YEAR), mToDate.get(Calendar.MONTH), mToDate.get(Calendar.DAY_OF_MONTH)).show();
        }
    };
    /*
        private void InitializeMaxPriceTab() {
            mRequestMaxPrice = new Request_MaxPrice();
            NomenclaturaAdapter adapter = null;
            NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
            Cursor cursor = savedData.getCursorMaxPrice();
            if (cursor == null) {
                adapter = new NomenclaturaAdapter(this, mRequestMaxPrice.Request(mDB, ARTIKUL), null, mIsCRAvailable);
            }
            else {
                adapter = new NomenclaturaAdapter(this, cursor, null, mIsCRAvailable);
            }
            mMaxPriceListView = (ListView) findViewById(R.id.list_maxprice);
            mMaxPriceListView.setAdapter(adapter);
            mMaxPriceListView.setOnItemClickListener(mOnListItemClick);
            mMaxPriceListView.setSelectionFromTop(NomenclatureSavedData.getInstance().getPositionMaxPrice(), 0);
            mMaxPriceListView.setOnTouchListener(this);
        }
    */
    private OnItemClickListener mOnListItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
            ListView list = (ListView) listView;
            mCurrentAdapter = (ZoomListCursorAdapter) list.getAdapter();
            Cursor cursor = mCurrentAdapter.getCursor();
            mCurrentAdapter.setSelectedPosition(position);
            cursor.moveToPosition(position);
        }
    };
    private OnClickListener mSaveClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = SetActivityResult();
            if (intent != null) {
                setResult(RESULT_OK, intent);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(mEditHistorySearch.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
                finish();
            }
        }
    };
    private OnClickListener mPhotoClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            int pos = mCurrentAdapter.getSelectedPosition();
            if (pos > -1) {
                Cursor cursor = mCurrentAdapter.getCursor();
                cursor.moveToPosition(pos);
                String artikul = cursor.getString(2);
                String name = cursor.getString(3);
                //String comment="";
                //UIHelper.quickWarning("Photo for "+pos+": "+artikul, Activity_Nomenclature.this);
                //UIHelper.showPhotoByArtikul(artikul,name,Activity_Nomenclature.this);
                System.out.println("show " + artikul + ": " + name);
                Intent intent = new Intent();
                intent.putExtra(sweetlife.android10.reports.ActivityPhoto.artikulField, artikul);
                intent.putExtra(sweetlife.android10.reports.ActivityPhoto.nameField, name);
                //intent.putExtra("comment", comment);
                intent.setClass(Activity_Nomenclature.this, sweetlife.android10.reports.ActivityPhoto.class);
				Activity_Nomenclature.this.startActivity(intent);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _DEGUSTACIA_POISK = false;
        setContentView(R.layout.act_nomenclature);
        setTitle(R.string.title_nomenclature);
        System.out.println("go");
        mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);
        System.out.println("InitializeTabHost");
        InitializeTabHost();
        ((Button) findViewById(R.id.btn_save)).setOnClickListener(mSaveClick);
        ((Button) findViewById(R.id.btn_show_photo)).setOnClickListener(mPhotoClick);
        ((Button) findViewById(R.id.btn_requestPrice)).setOnClickListener(mRequestPriceClick);
        ((Button) findViewById(R.id.btn_requestCertifikat)).setOnClickListener(mRequestCerificatClick);
        //((Button) findViewById(R.id.btn_requestSklad)).setOnClickListener(mRequestSkladClick);//requestOstatki());
        ((Button) findViewById(R.id.btn_search_mode)).setOnClickListener(clickSearchMode);
        ((Button) findViewById(R.id.btn_search_kuhnya)).setOnClickListener(clickSearchKuhnya);
        ((Button) findViewById(R.id.btn_search_tochka)).setOnClickListener(clickSearchTochka);
        //((Button) findViewById(R.id.btn_receipt)).setOnClickListener(clickSearchReceipt);

        //
        System.out.println("ReadExtras");
        ReadExtras();
        NomenclatureSavedData data = NomenclatureSavedData.getInstance();
        System.out.println("InitializeNomeclatureTab");
        InitializeNomeclatureTab();
        System.out.println("InitializeSearchTab");
        InitializeSearchTab();
        System.out.println("InitializeHistoryTab");
        InitializeHistoryTab();
        //InitializeMaxPriceTab();
        if (mIsCRAvailable) {
            //InitializeCRTab();
        } else {
            if (!ApplicationHoreca.getInstance().isCRdisabledFirstTimeShow()) {
                CreateErrorDialog("Ценовое реагирование недоступно без обновления БД.").show();
                ApplicationHoreca.getInstance().setCRdisabledFirstTimeShow(true);
            }
        }
        //SetOrderByHeaderButtons();
        mCurrentAdapter = mNomenclatureByGroupListAdapter;
        mCurrentRequest = null;
        mTabHost.setCurrentTab(data.getActiveTab());
        mTextOrderAmount = (TextView) findViewById(R.id.text_order_amount);
        mTextOrderAmount.setText(getString(R.string.order_amount) + "  " + DecimalFormatHelper.format(mOrderAmount));
        setupSearchModeSelection();
        if (!lastClientID.equals(ApplicationHoreca.getInstance().getClientInfo().getID())) {
            System.out.println("lastClientID " + lastClientID);
            lastClientID = ApplicationHoreca.getInstance().getClientInfo().getID();
            SearchHistory("");
        }
        System.out.println("done client");
    }

    private void InitializeTabHost() {
        mTabHost = (TabHost) findViewById(R.id.tab_host);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab_search").setIndicator(makeTabIndicator("Поиск")).setContent(R.id.searchtab));
        mTabHost.addTab(mTabHost.newTabSpec("tab_nomenclature").setIndicator(makeTabIndicator("Номенклатура")).setContent(R.id.nomenclaturetab));
        //mTabHost.addTab(mTabHost.newTabSpec("tab_maxprice").setIndicator(makeTabIndicator("Мах.наценка")).setContent(R.id.maxpricetab));
        mTabHost.addTab(mTabHost.newTabSpec("tab_history").setIndicator(makeTabIndicator("История")).setContent(R.id.historytab));
        //if (mIsCRAvailable) {
        //	mTabHost.addTab(mTabHost.newTabSpec("tab_cr").setIndicator(makeTabIndicator("ЦР")).setContent(R.id.crtab));
        //}
        //else {
        //	View v = findViewById(R.id.crtab);
        //	v.setVisibility(View.GONE);
        //}
        mTabHost.setOnTabChangedListener(this);
    }

    private void ReadExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(ORDER_AMOUNT)) {
            mOrderAmount = extras.getDouble(ORDER_AMOUNT);
        }
        if (extras.containsKey(DEGUSTACIA_POISK)) {
            _DEGUSTACIA_POISK = extras.getString(DEGUSTACIA_POISK).equals("1");
        }
        if (_DEGUSTACIA_POISK) {
            System.out.println("degustacia " + _DEGUSTACIA_POISK);
        }
    }

    private Intent SetActivityResult() {
        System.out.println(this.getClass().getCanonicalName() + ": SetActivityResult");
        if (mCurrentAdapter.getSelectedPosition() == -1) {
            CreateErrorDialog(R.string.msg_error_nomenclature_id).show();
            return null;
        }
        Cursor cursor = mCurrentAdapter.getCursor();
        cursor.moveToPosition(mCurrentAdapter.getSelectedPosition());
        Intent resultIntent = new Intent();
        String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
        if (Request_NomenclatureBase.getMinCena(cursor) == null || !mIsCRAvailable) {
            //System.out.println(": 1");
            resultIntent.putExtra(MIN_CENA, 0.00D);
            resultIntent.putExtra(MAX_CENA, 0.00D);
            if (vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
            ) {
                //System.out.println(": 2");
                resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
                resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
            } else {
                System.out.println(": 3");
                resultIntent.putExtra(VID_SKIDKI, "x'00'");
                resultIntent.putExtra(CENA_SO_SKIDKOY, 0.00D);
            }
        } else {
            //System.out.println(": 4");
            if (vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
            ) {
                //System.out.println(": 5");
                resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
                resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
            } else {
                //System.out.println(": 6");
                resultIntent.putExtra(VID_SKIDKI, "x'00'");
                resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCena(cursor)));
            }
            resultIntent.putExtra(MIN_CENA, Double.parseDouble(Request_NomenclatureBase.getMinCena(cursor)));
            resultIntent.putExtra(MAX_CENA, Double.parseDouble(Request_NomenclatureBase.getMaxCena(cursor)));
        }
        String sale = Request_NomenclatureBase.getSkidka(cursor);
        //System.out.println(": 7");
        if (sale != null) {
            //System.out.println(": 8");
            try {
                resultIntent.putExtra(SKIDKA, Double.parseDouble(sale));
            } catch (Throwable t) {
                System.out.println(t.getMessage());
                resultIntent.putExtra(SKIDKA, 0.00D);
            }
        } else {
            resultIntent.putExtra(SKIDKA, 0.00D);
        }
        resultIntent.putExtra(CENA, Double.parseDouble(Request_NomenclatureBase.getCena(cursor)));
        NomenclatureCountHelper helper = new NomenclatureCountHelper(Double.parseDouble(Request_NomenclatureBase.getMinNorma(cursor)), Double.parseDouble(Request_NomenclatureBase.getKoephphicient(cursor)));
        resultIntent.putExtra(COUNT, helper.ReCalculateCount(0));
        resultIntent.putExtra(NOMENCLATURE_ID, Request_NomenclatureBase.getIDRRef(cursor));
        resultIntent.putExtra(NAIMENOVANIE, Request_NomenclatureBase.getNaimenovanie(cursor));
        resultIntent.putExtra(ARTIKUL, Request_NomenclatureBase.getArtikul(cursor));
        resultIntent.putExtra(OSNOVNOY_PROIZVODITEL, Request_NomenclatureBase.getProizvoditelID(cursor));
        resultIntent.putExtra(EDINICY_IZMERENIYA_ID, Request_NomenclatureBase.getEdinicyIzmereniyaID(cursor));
        resultIntent.putExtra(EDINICY_IZMERENIYA_NAIMENOVANIE, Request_NomenclatureBase.getEdinicyIzmereniyaNaimenovanie(cursor));
        resultIntent.putExtra(KOEPHICIENT, Double.parseDouble(Request_NomenclatureBase.getKoephphicient(cursor)));
        resultIntent.putExtra(MIN_NORMA, Double.parseDouble(Request_NomenclatureBase.getMinNorma(cursor)));
        resultIntent.putExtra(BASE_PRICE, Request_NomenclatureBase.getBasePrice(cursor));
        resultIntent.putExtra(LAST_PRICE, Request_NomenclatureBase.getLastPrice(cursor));
        //resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
        //resultIntent.putExtra(SKIDKA_PROCENT, Request_NomenclatureBase.getSkidka(cursor));
        SaveDataBeforeExit();
        System.out.println(this.getClass().getCanonicalName() + ": art: " + Request_NomenclatureBase.getArtikul(cursor));
        return resultIntent;
    }

    @Override
    public void onTabChanged(String tabSpec) {
        //if (tabSpec.compareToIgnoreCase("tab_cr") == 0) {
		/*if (mCRListView != null) {
			mCurrentAdapter = (ZoomListCursorAdapter) mCRListView.getAdapter();
			//mCurrentRequest = mRequestCR;
			mCurrentRequest=null;
		}*/
        //}
        //else
        if (tabSpec.compareToIgnoreCase("tab_nomenclature") == 0) {
            mCurrentAdapter = mNomenclatureByGroupListAdapter;
            mCurrentRequest = null;
        } else
			/*if (tabSpec.compareToIgnoreCase("tab_maxprice") == 0) {
				//mCurrentAdapter = (ZoomListCursorAdapter) mMaxPriceListView.getAdapter();
				//mCurrentRequest = mRequestMaxPrice;
				mCurrentRequest = null;
			}
			else*/ {
            if (tabSpec.compareToIgnoreCase("tab_search") == 0) {
                mCurrentAdapter = (ZoomListCursorAdapter) mSearchListView.getAdapter();
                mCurrentRequest = mRequestSearch;
            } else {
                if (tabSpec.compareToIgnoreCase("tab_history") == 0) {
                    mCurrentAdapter = (ZoomListCursorAdapter) mHistoryListView.getAdapter();
                    mCurrentRequest = mRequestHistory;
                }
            }
        }
		/*if (mCRListView != null) {
			((ZoomListCursorAdapter) mCRListView.getAdapter()).setSelectedPosition(-1);
		}
		if (mMaxPriceListView != null) {
			((ZoomListCursorAdapter) mMaxPriceListView.getAdapter()).setSelectedPosition(-1);
		}*/
        ((ZoomListCursorAdapter) mSearchListView.getAdapter()).setSelectedPosition(-1);
        mNomenclatureByGroupListAdapter.setSelectedPosition(-1);
        //((ZoomListCursorAdapter) mHistoryListView.getAdapter()).setSelectedPosition(-1);
        if (tabSpec.compareToIgnoreCase("tab_search") != 0) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
        }
    }

    /*
    private void InitializeCRTab() {
        mRequestCR = new Request_CR();
        CRListAdapter adapter = null;
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        Cursor cursor = savedData.getCursorCR();
        if (cursor == null) {
            adapter = new CRListAdapter(this, mRequestCR.Request(mDB, ARTIKUL));
        }
        else {
            adapter = new CRListAdapter(this, cursor);
        }
        mCRListView = (ListView) findViewById(R.id.list_cr);
        mCRListView.setAdapter(adapter);
        mCRListView.setSelectionFromTop(savedData.getPositionCR(), 0);
        mCRListView.setOnItemClickListener(mOnListItemClick);
        mCRListView.setOnTouchListener(this);
    }*/
    private void InitializeNomeclatureTab() {
        System.out.println("InitializeGroupsListView");
        InitializeGroupsListView();
        System.out.println("InitializeNomenclatureListView");
        InitializeNomenclatureListView();
        System.out.println("done InitializeNomeclatureTab");
    }

    private void InitializeGroupsListView() {
        mNomenclatureGroupsExpandableListView = (ExpandableListView) findViewById(R.id.list_groups);
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        Cursor cursor = savedData.getCursorGroupsNomenclature();
        if (cursor == null) {
            Cursor c = Request_NomenclatureGroups.categoriesStat(mDB);
            System.out.println("mNomenclatureGroupsListAdapter init");
            mNomenclatureGroupsListAdapter = new NomenclatureGroupsListAdapter(c, this, true, mDB);
            System.out.println("mNomenclatureGroupsListAdapter ready");
        } else {
            mNomenclatureGroupsListAdapter = new NomenclatureGroupsListAdapter(cursor, this, true, mDB);
        }
        System.out.println("mNomenclatureGroupsExpandableListView 1");
        mNomenclatureGroupsExpandableListView.setOnChildClickListener(mOnChildGroupClickListener);
        System.out.println("mNomenclatureGroupsExpandableListView 2");
        mNomenclatureGroupsExpandableListView.setAdapter(mNomenclatureGroupsListAdapter);
        System.out.println("mNomenclatureGroupsExpandableListView 3");
        mNomenclatureGroupsExpandableListView.setSelectionFromTop(savedData.getPositionGroupNomenclature(), 0);
        System.out.println("mNomenclatureGroupsExpandableListView 4");
    }

    private void InitializeNomenclatureListView() {
        System.out.println(this.getClass().getCanonicalName() + ".InitializeNomenclatureListView");
        mNomenclatureByGroupListView = (ListView) findViewById(R.id.list_nomenclature_by_group);
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        Cursor cursor = savedData.getCursorNomenclature();
        if (cursor == null) {
            mNomenclatureByGroupListAdapter = new NomenclatureByGroupListAdapter(this, null, mIsCRAvailable);
        } else {
            mNomenclatureByGroupListAdapter = new NomenclatureByGroupListAdapter(this, cursor, mIsCRAvailable);
        }
        mNomenclatureByGroupListView.setOnItemClickListener(mOnListItemClick);
        mNomenclatureByGroupListView.setAdapter(mNomenclatureByGroupListAdapter);
        mNomenclatureByGroupListView.setSelectionFromTop(savedData.getPositionNomenclature(), 0);
    }

    private void InitializeSearchTab() {
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mEditSearch.setText(savedData.getSearchString());
        mEditSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    SearchBtnClick();
                    return true;
                }
                return false;
            }
        });
        OnSelectedPositionChangeListener onSelectListener = new OnSelectedPositionChangeListener() {
            @Override
            public void OnPositionChange(ZoomListCursorAdapter adapter, int position) {
                mOnListItemClick.onItemClick(mSearchListView, null, position, 0);
            }
        };
        NomenclaturaAdapter adapter = null;
        Cursor cursor = savedData.getCursorSearch();
        if (cursor == null) {
            adapter = new NomenclaturaAdapter(this, null, onSelectListener, mIsCRAvailable, Auxiliary.screenWidth(this));
        } else {
            adapter = new NomenclaturaAdapter(this, cursor, onSelectListener, mIsCRAvailable, Auxiliary.screenWidth(this));
        }
        mSearchListView = (ListView) findViewById(R.id.list_search);
        mSearchListView.setAdapter(adapter);
        mSearchListView.setSelectionFromTop(savedData.getPositionSearch(), 0);
        mSearchListView.setOnItemClickListener(mOnListItemClick);
        mSearchListView.setOnTouchListener(this);
        Button btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchBtnClick();
            }
        });
        //mRadioActicule = (RadioButton) findViewById(R.id.radio_articule);
		/*mRadioActicule.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mEditSearch.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL);
					mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
					InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					keyboard.restartInput(mEditSearch);
				}
			}
		});*/
        //mRadioActicule.setOnCheckedChangeListener(mSearchRadioTextType);
        //mRadioVendor = (RadioButton) findViewById(R.id.radio_vendor);
        //mRadioVendor.setOnCheckedChangeListener(mSearchRadioTextType);
        //mRadioName = (RadioButton) findViewById(R.id.radio_name);
        //mRadioName.setOnCheckedChangeListener(mSearchRadioTextType);
        if (savedData.getSearchBy() == SEARCH_ARTICLE) {
            //mRadioActicule.setChecked(true);
			/*
			mEditSearch.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL);
			mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
			InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.restartInput(mEditSearch);
			*/
            mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.restartInput(mEditSearch);
        } else {
            if (savedData.getSearchBy() == SEARCH_NAME) {
                //mRadioName.setChecked(true);
                mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.restartInput(mEditSearch);
            } else {
                //mRadioVendor.setChecked(true);
                mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.restartInput(mEditSearch);
            }
        }
        mEditSearch.selectAll();
        mEditSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) view).selectAll();
                }
            }
        });
    }

    private void SearchBtnClick() {
        String searchString = mEditSearch.getText().toString();
        if ((searchString.length() > 0) || (selectedSearchByReceptIDRREF != null)) {
            LogHelper.debug(this.getClass().getCanonicalName() + " SearchBtnClick (" + selectedSearchByMode + "): " + searchString);
            ((ZoomListCursorAdapter) mSearchListView.getAdapter()).setSelectedPosition(-1);
            //mRequestSearch = new Request_Search(selectedSearchByMode, searchString);
            String kuhnya = null;
            if (selectedSearchByKuhnya > 0) {
                kuhnya = ((Button) findViewById(R.id.btn_search_kuhnya)).getText().toString().trim();
            }
            String tochka = null;
            if (selectedSearchByTochka > 0) {
                tochka = selectedSearchTochkaIDRREF;
            }
            //String receptID = null;
            mRequestSearch = new Request_Search(selectedSearchByMode, searchString, kuhnya, tochka, _DEGUSTACIA_POISK, selectedSearchByReceptIDRREF);
            System.out.println("selectedSearchByReceptIDRREF " + selectedSearchByReceptIDRREF);
            System.out.println("_DEGUSTACIA_POISK " + DEGUSTACIA_POISK);
            System.out.println("mRequestSearch.mStrQuery: " + mRequestSearch.mStrQuery);
            ZoomListCursorAdapter adapter = (ZoomListCursorAdapter) mSearchListView.getAdapter();
            adapter.changeCursor(mRequestSearch.Request(mDB, ARTIKUL, _DEGUSTACIA_POISK));
            //LogHelper.debug(adapter.getClass().getCanonicalName());
            adapter.notifyDataSetChanged();
        }
        System.out.println("SearchBtnClick " + searchString);
    }

    private void InitializeHistoryTab() {
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        mEditHistorySearch = (EditText) findViewById(R.id.edit_naimenovanie);
        mEditHistorySearch.setText(savedData.getHistorySearchString());
		mEditHistorySearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					SearchHistory(mEditHistorySearch.getText().toString());
					return true;
				}
				return false;
			}
		});

        seekHistoryButton = (Button) findViewById(R.id.btn_search_history);
        seekHistoryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistory(mEditHistorySearch.getText().toString());
            }
        });
        mRequestHistory = new Request_History(_DEGUSTACIA_POISK);
        InitializeHistoryDate();
        NomenclaturaAdapter adapter = null;
        Cursor cursor = savedData.getCursorHistory();
        if (cursor == null) {
            mRequestHistory.Request(mDB//
                    , DateTimeHelper.SQLDateString(mFromDate.getTime())//
                    , DateTimeHelper.SQLDateString(mToDate.getTime()), "", _DEGUSTACIA_POISK);
            adapter = new NomenclaturaAdapter(this, mRequestHistory.getCursor(), null, mIsCRAvailable, Auxiliary.screenWidth(this));
        } else {
            adapter = new NomenclaturaAdapter(this, cursor, null, mIsCRAvailable, Auxiliary.screenWidth(this));
        }
        mHistoryListView = (ListView) findViewById(R.id.list_history);
        mHistoryListView.setAdapter(adapter);
        mHistoryListView.setSelectionFromTop(savedData.getPositionHistory(), 0);
        mHistoryListView.setOnItemClickListener(mOnListItemClick);
        mHistoryListView.setOnTouchListener(this);



















    }

    private void SearchHistory(String searshHistoryString) {
        System.out.println("SearchHistory " + searshHistoryString);
        mRequestHistory.Request(mDB, DateTimeHelper.SQLDateString(mFromDate.getTime()), DateTimeHelper.SQLDateString(mToDate.getTime()), searshHistoryString, _DEGUSTACIA_POISK);
        ZoomListCursorAdapter adapter = (ZoomListCursorAdapter) mHistoryListView.getAdapter();
        adapter.changeCursor(mRequestHistory.getCursor());
        adapter.notifyDataSetChanged();
    }

    private void InitializeHistoryDate() {
        mEditFromDate = (EditText) findViewById(R.id.edit_from_date);
        mEditFromDate.setOnClickListener(mFromDateClick);
        mEditToDate = (EditText) findViewById(R.id.edit_to_date);
        mEditToDate.setOnClickListener(mToDateClick);
        NomenclatureSavedData savedData = NomenclatureSavedData.getInstance();
        mToDate = savedData.getHistoryDateTo();
        mFromDate = savedData.getHistoryDateFrom();
        mEditToDate.setText(DateTimeHelper.UIDateString(mToDate.getTime()));
        mEditFromDate.setText(DateTimeHelper.UIDateString(mFromDate.getTime()));
        //System.out.println("mFromDate "+mFromDate.getTime());
    }

    int updateByRequest(String Artikul, String TekuhayaCena, String Prais, String MinPorog) {
        /*
I/System.out: raw response <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
I/System.out: 	<soap:Body>
I/System.out: 		<m:GetResponse xmlns:m="DanniePoTovaram">
I/System.out: 			<m:return xmlns:xs="http://www.w3.org/2001/XMLSchema"
I/System.out: 					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
I/System.out: 				<Str xmlns="http://ws.swl/DanniePoTovaram">
I/System.out: 					<Artikul>107877</Artikul>
I/System.out: 					<NaSklade>1 233,05</NaSklade>
I/System.out: 					<Dostupno/>
I/System.out: 					<TekuhayaCena>79,83</TekuhayaCena>
I/System.out: 					<Prais>119</Prais>
I/System.out: 					<MinPorog>0</MinPorog>
I/System.out: 				</Str>
I/System.out: 			</m:return>
I/System.out: 		</m:GetResponse>
I/System.out: 	</soap:Body>
I/System.out: </soap:Envelope>
        */
        System.out.println("updateByRequest: " + Artikul + ", " + TekuhayaCena + ", " + Prais + ", " + MinPorog + ".");
        int r = 0;
        //try {
        String sql = "update CenyNomenklaturySklada_last"//
                + "\n		set cena=" + Prais//
                + "\n		where _id in ("//
                + "\n			select CenyNomenklaturySklada_last._id from CenyNomenklaturySklada_last"//
                + "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
                + "\n	;";
        //System.out.println(sql);
        if (Prais.trim().length() > 0) {
            ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            r = 1;
        }
        sql = "update CenyNomenklaturySklada"//
                + "\n		set cena=" + Prais//
                + "\n		where _id in ("//
                + "\n			select CenyNomenklaturySklada._id from CenyNomenklaturySklada_last"//
                + "\n				join CenyNomenklaturySklada on CenyNomenklaturySklada.period=CenyNomenklaturySklada_last.period and CenyNomenklaturySklada.nomenklatura=CenyNomenklaturySklada_last.nomenklatura"//
                + "\n				join nomenklatura on nomenklatura._idrref=CenyNomenklaturySklada_last.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
                + "\n	;";
        //System.out.println(sql);
        if (Prais.trim().length() > 0) {
            ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            r = 1;
        }
        sql = "update TekuschieCenyOstatkovPartiy_strip"//
                + "\n		set cena=" + TekuhayaCena//
                + "\n		where _id in ("//
                + "\n			select TekuschieCenyOstatkovPartiy_strip._id from TekuschieCenyOstatkovPartiy_strip"//
                + "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy_strip.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
                + "\n	;";
        //System.out.println(sql);
        if (TekuhayaCena.trim().length() > 0) {
            ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            r = 1;
        }
        sql = "update TekuschieCenyOstatkovPartiy"//
                + "\n		set cena=" + TekuhayaCena//
                + "\n		where _id in ("//
                + "\n			select TekuschieCenyOstatkovPartiy._id from TekuschieCenyOstatkovPartiy"//
                + "\n				join nomenklatura on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura and nomenklatura.artikul='" + Artikul + "')"//
                + "\n	;";
        //System.out.println(sql);
        if (TekuhayaCena.trim().length() > 0) {
            ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            r = 1;
        }
        sql = "update MinimalnyeNacenkiProizvoditeley_1"//
                + "\n		set nacenka=" + MinPorog//
                + "\n		where _id in ("//
                + "\n			select MinimalnyeNacenkiProizvoditeley_1._id from MinimalnyeNacenkiProizvoditeley_1"//
                + "\n				join nomenklatura on nomenklatura._idrref=MinimalnyeNacenkiProizvoditeley_1.NomenklaturaProizvoditel_2 and nomenklatura.artikul='" + Artikul + "')"//
                + "\n	;";
        //System.out.println(sql);
        if (MinPorog.trim().length() > 0) {
            ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
            r = 1;
        }
		/*} catch(Throwable t) {
			t.printStackTrace();
			Auxiliary.warn("Ошибка: " + t.getMessage().substring(0, 30), Activity_Nomenclature.this);
		}*/
        return r;
    }

    void sendRequestPrice(final String soapXML) {
        //System.out.println(soapXML);
        final String url = Settings.getInstance().getBaseURL() + "DanniePoTovaram.1cws";
        final RawSOAP r = new RawSOAP();
        final Numeric countUpdate = new Numeric();
        new Expect().status.is("Обновление цен в таблице").task.is(new Task() {
            @Override
            public void doTask() {
                r.url.is(url)//
                        .xml.is(soapXML);
                Report_Base.startPing();
                r.startNow(Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
            }
        }).afterDone.is(new Task() {
            @Override
            public void doTask() {
                System.out.println("result r " + r.statusCode.property.value() + "/" + r.exception.property.value());
                if (r.exception.property.value() != null) {
                    Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), Activity_Nomenclature.this);
                    r.exception.property.value().printStackTrace();
                } else {
                    System.out.println("result r " + r.statusCode.property.value());
                    if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {

                        Vector<Bough> rows = r.data.child("soap:Body").child("m:GetResponse").child("m:return").children;
                        System.out.println("dump " + rows.size() + "/" + r.data.dumpXML());
                        int r = 0;
                        for (int i = 0; i < rows.size(); i++) {
                            Bough b = rows.get(i);
							/*System.out.println("Artikul " + b.child("Artikul").value.property.value());
							System.out.println("	TekuhayaCena " + b.child("TekuhayaCena").value.property.value());
							System.out.println("	Prais " + b.child("Prais").value.property.value());
							System.out.println("	MinPorog " + b.child("MinPorog").value.property.value());*/
                            //try {
                            int kkk = updateByRequest(b.child("Artikul").value.property.value()//
                                    , b.child("TekuhayaCena").value.property.value().replace(',', '.').replaceAll("\\s+", "")//текущие цены остатков партий
                                    , b.child("Prais").value.property.value().replace(',', '.').replaceAll("\\s+", "")//цены номенклатуры
                                    , b.child("MinPorog").value.property.value().replace(',', '.').replaceAll("\\s+", "")//минимальные наценки производителейц
                            );
                            countUpdate.value(countUpdate.value() + kkk);
							/*} catch(Throwable t) {
								t.printStackTrace();
							}*/
                        }
                        SearchBtnClick();
                    } else {
                        Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value() + ", statusCode " + r.statusCode.property.value()
								, Activity_Nomenclature.this);
                    }
                }
                //refreshTask.start();
                Auxiliary.warn("Всего обновлено строк: " + countUpdate.value(), Activity_Nomenclature.this);
                if (r.rawResponse != null) {
                    System.out.println("raw response " + r.rawResponse);
                }
            }
        }).afterCancel.is(new Task() {
            @Override
            public void doTask() {
                //refreshTask.start();
            }
        })
                /*.afterDone.is(new Task() {
            @Override
            public void doTask() {
                Auxiliary.warn("Всего обновлено строк: " + countUpdate.value(), Activity_Nomenclature.this);
                if(r.rawResponse!=null)System.out.println("raw response "+r.rawResponse);
            }
        })*/.start(this);
    }

    private void SetOrderByFortab(View tab) {
        if (tab != null) {
            Button button = (Button) tab.findViewById(R.id.head_btn_article);
            button.setOnClickListener(this);
            button = (Button) tab.findViewById(R.id.head_btn_nomenclature);
            button.setOnClickListener(this);
            button = (Button) tab.findViewById(R.id.head_btn_vendor);
            button.setOnClickListener(this);
        }
    }/*
		private void SetOrderByHeaderButtons() {
		//		SetOrderByFortab((View)findViewById(R.id.nomenclaturetab));
		SetOrderByFortab((View) findViewById(R.id.maxpricetab));
		SetOrderByFortab((View) findViewById(R.id.crtab));
		}*/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.head_btn_article) {
            ChangeCursorForOrder(view, ARTIKUL);
        } else {
            if (view.getId() == R.id.head_btn_nomenclature) {
                ChangeCursorForOrder(view, NAIMENOVANIE);
            } else {
                ChangeCursorForOrder(view, PROIZVODITEL_NAIMENOVANIE);
            }
        }
    }

    private void SetHeaderButtonsColor(View view) {
        //View parent = (View) view.getParent();
        //Button button = (Button) parent.findViewById(R.id.head_btn_article);
        //button.setBackgroundResource(R.drawable.bg_list_header_unsel);
        //button = (Button) parent.findViewById(R.id.head_btn_nomenclature);
       // button.setBackgroundResource(R.drawable.bg_list_header_unsel);
        //button = (Button) parent.findViewById(R.id.head_btn_vendor);
        //button.setBackgroundResource(R.drawable.bg_list_header_unsel);
        //view.setBackgroundResource(R.drawable.bg_list_header_sel);
    }

    private void ChangeCursorForOrder(View view, String orderField) {
        System.out.println(this.getClass().getCanonicalName() + ".ChangeCursorForOrder");
        if (mCurrentAdapter != null && mCurrentRequest != null) {
            mCurrentAdapter.changeCursor(mCurrentRequest.Request(mDB, orderField, false));
            mCurrentAdapter.notifyDataSetChanged();
            SetHeaderButtonsColor(view);
        }
    }

    private void SaveDataBeforeExit() {
        NomenclatureSavedData data = NomenclatureSavedData.getInstance();
		/*if (mCRListView != null) {
			data.setCursorCR(((ZoomListCursorAdapter) mCRListView.getAdapter()).getCursor());
			data.setPositionCR(mCRListView.getFirstVisiblePosition());
		}*/
        //data.setCursorMaxPrice(((ZoomListCursorAdapter) mMaxPriceListView.getAdapter()).getCursor());
        //data.setPositionMaxPrice(mMaxPriceListView.getFirstVisiblePosition());
        data.setCursorSearch(((ZoomListCursorAdapter) mSearchListView.getAdapter()).getCursor());
        data.setPositionSearch(mSearchListView.getFirstVisiblePosition());
        data.setCursorNomenclature(((ZoomListCursorAdapter) mNomenclatureByGroupListView.getAdapter()).getCursor());
        data.setPositionNomenclature(mNomenclatureByGroupListView.getFirstVisiblePosition());
        data.setCursorGroupsNomenclature(mNomenclatureGroupsListAdapter.getCursor());
        data.setPositionGroupNomenclature(mNomenclatureGroupsExpandableListView.getFirstVisiblePosition());
        data.setCursorHistory(((ZoomListCursorAdapter) mHistoryListView.getAdapter()).getCursor());
        data.setPositionHistory(mHistoryListView.getFirstVisiblePosition());
        data.setHistorySearchString(mEditHistorySearch.getText().toString());
        data.setHistoryDateFrom(mFromDate);
        data.setHistoryDateTo(mToDate);
        data.setSearchBy(selectedSearchByMode);
        data.setSearchString(mEditSearch.getText().toString());
        data.setActiveTab(mTabHost.getCurrentTab());
    }

    //private int getSearchByType() {
		/*if (mRadioActicule.isChecked()) {
			return SEARCH_ARTICLE;
		}
		else
			if (mRadioName.isChecked()) {
				return SEARCH_NAME;
			}
			else {
				return SEARCH_VENDOR;
			}*/
    //return SEARCH_ARTICLE;
    //}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK & mEditSearch.getText().toString().length() > 0) {
            NomenclatureSavedData data = NomenclatureSavedData.getInstance();
            data.setSearchBy(selectedSearchByMode);
            data.setSearchString(mEditSearch.getText().toString());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(mEditHistorySearch.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuOtchety = menu.add("Отчёты");
        //menuOstatki = menu.add("Получить доступные остатки");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == menuOtchety) {
            Intent intent = new Intent();
            intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
            startActivity(intent);
            return true;
        }
		/*if(item == menuOstatki) {
			requestOstatki();
			return true;
		}*/
        return true;
    }

    void ___requestOstatki(View v) {
		/*String url = "http://89.109.7.162/hrc120107/hs/ObnovlenieInfo/ПолучитьДоступноеКоличество/16811";
		String txt=Auxiliary.loadTextFromPublicPOST(url,"",30000,"UTF-8");
		System.out.println(url);
		System.out.println(txt);*/
        //ListView list = (ListView) listView;
        //mCurrentAdapter = (ZoomListCursorAdapter) list.getAdapter();
        System.out.println("requestOstatki " + v);
        String artikuls = "[";
        String dt = "";
        Cursor cursor = mCurrentAdapter.getCursor();
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                String artikul = cursor.getString(2);
                //cursor.set
                //cursor.getColumnIndex(PROIZVODITEL_NAIMENOVANIE)
                artikuls = artikuls + dt + "{\"Артикул\":\"" + artikul + "\"}";
                dt = ",";
            }
            while (cursor.moveToNext());
        }
        artikuls = artikuls + "]";
        final String artikuly = artikuls;
        System.out.println(artikuly);
        if (dt.equals("")) {
            Auxiliary.warn("Не выбрана номенклатура", this);
            return;
        }
        Expect expect = new Expect().status.is("Подождите").task.is(new Task() {
            @Override
            public void doTask() {
                try {
                    String url = "http://89.109.7.162/hrc120107/hs/ObnovlenieInfo/%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C%D0%94%D0%BE%D1%81%D1%82%D1%83%D0%BF%D0%BD%D0%BE%D0%B5%D0%9A%D0%BE%D0%BB%D0%B8%D1%87%D0%B5%D1%81%D1%82%D0%B2%D0%BE/";
                    url = url + ApplicationHoreca.getInstance().getClientInfo().getKod();
                    //String artikuly="[{\"Артикул\":\"86641\"}]";
                    System.out.println(url);
                    Report_Base.startPing();
                    Bough result = Auxiliary.loadTextFromPublicPOST(url, artikuly, 21000, "UTF-8");
                    //String json=
                    //System.out.println(result.dumpXML());
                    Bough raw = Bough.parseJSON(result.child("raw").value.property.value());
                    System.out.println(raw.dumpXML());
                    Vector<Bough> all = raw.children("Остатки");
                    ZoomListCursorAdapter a1 = (ZoomListCursorAdapter) mSearchListView.getAdapter();
                    ZoomListCursorAdapter a2 = (ZoomListCursorAdapter) mHistoryListView.getAdapter();
                    ZoomListCursorAdapter a3 = (ZoomListCursorAdapter) mNomenclatureByGroupListView.getAdapter();
                    a1.artikuls.removeAllElements();
                    a2.artikuls.removeAllElements();
                    a3.artikuls.removeAllElements();
                    a1.sklad.removeAllElements();
                    a2.sklad.removeAllElements();
                    a3.sklad.removeAllElements();
                    for (int i = 0; i < all.size(); i++) {
                        a1.artikuls.add(all.get(i).child("Артикул").value.property.value());
                        a2.artikuls.add(all.get(i).child("Артикул").value.property.value());
                        a3.artikuls.add(all.get(i).child("Артикул").value.property.value());
                        a1.sklad.add(all.get(i).child("Остаток").value.property.value());
                        a2.sklad.add(all.get(i).child("Остаток").value.property.value());
                        a3.sklad.add(all.get(i).child("Остаток").value.property.value());
                        System.out.println(a1.artikuls.get(i) + "/" + a1.sklad.get(i));
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }).afterDone.is(new Task() {
            @Override
            public void doTask() {
                ZoomListCursorAdapter a1 = (ZoomListCursorAdapter) mSearchListView.getAdapter();
                ZoomListCursorAdapter a2 = (ZoomListCursorAdapter) mHistoryListView.getAdapter();
                ZoomListCursorAdapter a3 = (ZoomListCursorAdapter) mNomenclatureByGroupListView.getAdapter();
                a1.notifyDataSetChanged();
                a2.notifyDataSetChanged();
                a3.notifyDataSetChanged();
                Auxiliary.warn("Доступные остатки обновлены", Activity_Nomenclature.this);
            }
        });
        expect.start(this);
    }

    void setupSearchModeSelection() {


		/*
		static int selectedSearchMode = 1;//SEARCH_ARTICLE;
		static int selectedSearchByMode = SEARCH_NAME;
		static int selectedSearchByKuhnya = 0;
		static int selectedSearchByTochka = 0;
		static String selectedSearchTochkaIDRREF="00";
		*/
        if (selectedSearchMode == 0) {
            selectedSearchByMode = SEARCH_ARTICLE;
            ((Button) findViewById(R.id.btn_search_mode)).setText("Артикул");
        } else {
            if (selectedSearchMode == 1) {
                selectedSearchByMode = SEARCH_NAME;
                ((Button) findViewById(R.id.btn_search_mode)).setText("Наименование");
            } else {
                selectedSearchByMode = SEARCH_VENDOR;
                ((Button) findViewById(R.id.btn_search_mode)).setText("Производитель");
            }
        }
        if (selectedSearchByKuhnya == 0) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Любая кухня");
        }
        if (selectedSearchByKuhnya == 1) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Кавказская");
        }
        if (selectedSearchByKuhnya == 2) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Европейская");
        }
        if (selectedSearchByKuhnya == 3) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Итальянская");
        }
        if (selectedSearchByKuhnya == 4) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Русская");
        }
        if (selectedSearchByKuhnya == 5) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Японская");
        }
        if (selectedSearchByKuhnya == 6) {
            ((Button) findViewById(R.id.btn_search_kuhnya)).setText("Американская");
        }
        ((Button) findViewById(R.id.btn_search_tochka)).setText(selectedSearchTochkaTitle);
        //((Button) findViewById(R.id.btn_receipt)).setText(selectedSearchByReceptTitle);

    }
}
