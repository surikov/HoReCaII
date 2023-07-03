package sweetlife.android10.ui;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureGroupsListAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureSimpleListAdapter;
import sweetlife.android10.data.nomenclature.NomenclatureSimpleSavedData;
import sweetlife.android10.database.nomenclature.Request_NomeclatureSimple;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

import sweetlife.android10.R;


public class Activity_NomenclatureSimple extends Activity_Base implements ITableColumnsNames, OnTabChangeListener {

	private TabHost mTabHost;
	private EditText mEditSearch;
	private RadioButton mRadioActicule;
	private RadioButton mRadioName;
	private RadioButton mRadioVendor;

	private NomenclatureGroupsListAdapter mNomenclatureGroupsListAdapter;
	private NomenclatureSimpleListAdapter mNomenclatureListAdapter;
	private NomenclatureSimpleListAdapter mSearchListAdapter;

	private ZoomListCursorAdapter mCurrentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_nomenclature_simple);

		super.onCreate(savedInstanceState);

		InitializeTabHost();

		((Button) findViewById(R.id.btn_save)).setOnClickListener(mSaveClick);

		mCurrentListAdapter = mNomenclatureListAdapter;
	}

	private void InitializeTabHost() {

		mTabHost = (TabHost) findViewById(R.id.tab_host);
		mTabHost.setup();

		mTabHost.addTab(mTabHost.newTabSpec("tab_nomenclature_simple").setIndicator(makeTabIndicator("Номенклатура")).setContent(R.id.nomenclature));
		mTabHost.addTab(mTabHost.newTabSpec("tab_nomenclature_simple_search").setIndicator(makeTabIndicator("Поиск")).setContent(R.id.search));

		mTabHost.setOnTabChangedListener(this);

		InitializeNomeclatureTab();
		InitializeSearchTab();
	}

	private void InitializeNomeclatureTab() {

		InitializeGroupsListView();
		InitializeNomenclatureListView();
	}

	private void InitializeSearchTab() {

		InitializeSearchListView();

		NomenclatureSimpleSavedData savedData = new NomenclatureSimpleSavedData();

		mEditSearch = (EditText) findViewById(R.id.edit_search);
		mEditSearch.setText(savedData.getSearchString());
		mEditSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
						(event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode()
								== KeyEvent.KEYCODE_ENTER)) {

					SearchBtnClick();
					return true;
				}

				return false;
			}
		});

		Button btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				SearchBtnClick();
			}
		});

		mRadioActicule = (RadioButton) findViewById(R.id.radio_articule);
		/*mRadioActicule.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ){
					mEditSearch.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL);

					mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

					InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					keyboard.restartInput(mEditSearch);
				}
			}
		});*/

		mRadioVendor = (RadioButton) findViewById(R.id.radio_vendor);
		mRadioVendor.setOnCheckedChangeListener(mSearchRadioTextType);
		mRadioName = (RadioButton) findViewById(R.id.radio_name);
		mRadioName.setOnCheckedChangeListener(mSearchRadioTextType);

		if (savedData.getSearchBy() == Request_NomeclatureSimple.SEARCH_ARTICLE) {

			mRadioActicule.setChecked(true);
			/*
			mEditSearch.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL);

			mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
			*/

			mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

			mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

			InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.restartInput(mEditSearch);
		} else if (savedData.getSearchBy() == Request_NomeclatureSimple.SEARCH_NAME) {

			mRadioName.setChecked(true);
			mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

			mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

			InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.restartInput(mEditSearch);

		} else {

			mRadioVendor.setChecked(true);
			mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT |
					InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

			mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

			InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.restartInput(mEditSearch);
		}

		mEditSearch.selectAll();
	}

	private void SearchBtnClick() {

		String searchString = mEditSearch.getText().toString().toUpperCase();

		if (searchString.length() != 0) {

			mSearchListAdapter.setSelectedPosition(-1);

			mSearchListAdapter.changeCursor(Request_NomeclatureSimple
					.RequestNomenclatureBySearchString(mDB, searchString, getSearchByType()));

			mSearchListAdapter.notifyDataSetChanged();
		}
	}

	private int getSearchByType() {

		if (mRadioActicule.isChecked()) {

			return Request_NomeclatureSimple.SEARCH_ARTICLE;
		} else if (mRadioName.isChecked()) {

			return Request_NomeclatureSimple.SEARCH_NAME;
		} else {

			return Request_NomeclatureSimple.SEARCH_VENDOR;
		}
	}

	OnCheckedChangeListener mSearchRadioTextType = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT |
						InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

				mEditSearch.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.restartInput(mEditSearch);
			}
		}
	};

	private void InitializeSearchListView() {

		ListView listView = (ListView) findViewById(R.id.list_search);

		mSearchListAdapter = new NomenclatureSimpleListAdapter(this, null);

		listView.setOnItemClickListener(mOnListItemClick);

		listView.setAdapter(mSearchListAdapter);
	}

	private void InitializeGroupsListView() {

		ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_groups);

		mNomenclatureGroupsListAdapter = new NomenclatureGroupsListAdapter(
				Request_NomeclatureSimple.RequestNomenlatureGroupsWithoutParent(mDB), this, true, mDB);

		listView.setOnChildClickListener(mOnChildGroupClickListener);

		listView.setAdapter(mNomenclatureGroupsListAdapter);
	}

	private void InitializeNomenclatureListView() {

		ListView listView = (ListView) findViewById(R.id.list_nomenclature_simple);

		mNomenclatureListAdapter = new NomenclatureSimpleListAdapter(this, null);

		listView.setOnItemClickListener(mOnListItemClick);

		listView.setAdapter(mNomenclatureListAdapter);
	}

	OnChildClickListener mOnChildGroupClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
									int groupPosition, int childPosition, long id) {

			Cursor cursor = mNomenclatureGroupsListAdapter.getChild(groupPosition, childPosition);

			UpdateNomenclatureListView(Request_NomeclatureSimple.getIDRRef(cursor));

			return false;
		}

	};

	private void UpdateNomenclatureListView(String parentID) {

		mNomenclatureListAdapter.setSelectedPosition(-1);
		mNomenclatureListAdapter.changeCursor(Request_NomeclatureSimple.RequestNomenclatureByParent(mDB, parentID));
	}

	private OnItemClickListener mOnListItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position,
								long arg3) {

			mCurrentListAdapter.setSelectedPosition(position);
			mCurrentListAdapter.getCursor().moveToPosition(position);
		}
	};

	private OnClickListener mSaveClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Intent intent = SetActivityResult();

			if (intent != null) {

				setResult(RESULT_OK, intent);

				finish();
			}
		}
	};

	private Intent SetActivityResult() {

		if (mCurrentListAdapter.getSelectedPosition() == -1) {

			CreateErrorDialog(R.string.msg_error_nomenclature_id).show();
			return null;
		}

		Cursor cursor = mCurrentListAdapter.getCursor();
		cursor.moveToPosition(mCurrentListAdapter.getSelectedPosition());

		Intent resultIntent = new Intent();

		resultIntent.putExtra(NOMENCLATURE_ID, Request_NomeclatureSimple.getIDRRef(cursor));
		resultIntent.putExtra(NAIMENOVANIE, Request_NomeclatureSimple.getNaimenovanie(cursor));
		resultIntent.putExtra(ARTIKUL, Request_NomeclatureSimple.getArtikul(cursor));

		resultIntent.putExtra(OSNOVNOY_PROIZVODITEL, Request_NomeclatureSimple.getProizvoditelID(cursor));

		return resultIntent;
	}

	@Override
	public void onTabChanged(String tabSpec) {

		mNomenclatureListAdapter.setSelectedPosition(-1);
		mSearchListAdapter.setSelectedPosition(-1);

		if (tabSpec.compareToIgnoreCase("tab_nomenclature_simple_search") == 0) {

			mCurrentListAdapter = mSearchListAdapter;
		} else if (tabSpec.compareToIgnoreCase("tab_nomenclature_simple") == 0) {

			mCurrentListAdapter = mNomenclatureListAdapter;
		}
	}
}
