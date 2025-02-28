package sweetlife.android10.ui;

import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.data.fixedprices.*;
import reactive.ui.Auxiliary;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.fixedprices.FixedPricesNomenclatureData;
import sweetlife.android10.data.fixedprices.FixedPricesNomenclatureListAdapter;
import sweetlife.android10.data.fixedprices.ZayavkaNaSkidki;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener;
import sweetlife.android10.*;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_FixedPrices extends Activity_BasePeriod implements IExtras, ITableColumnsNames{

	MenuItem menuDelete;
	MenuItem menuClone;
	private SQLiteDatabase mDB;
	private ListView mFixedPricesList;
	private EditText mEditComment;
	private FixedPricesNomenclatureListAdapter mFixedPricesNomenclatureListAdapter;
	private int mListPositionForDelete = NULL_LIST_VALUE;
	private boolean mIsEditable = true;
	private boolean mHasChanges = false;
	private ClientInfo mClient;
	private ZayavkaNaSkidki mZayavka;
	private FixedPricesNomenclatureData mFixedPricesNomenclatureData;
	private OnCloseListener mOnPopupClose = new OnCloseListener(){
		@Override
		public void onClose(int param){
			mFixedPricesNomenclatureListAdapter.notifyDataSetChanged();
			mHasChanges = true;
		}
	};
	private OnClickListener mNomenclatureClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			Intent intent = new Intent();
			intent.setClass(Activity_FixedPrices.this, Activity_NomenclatureSimple.class);
			startActivityForResult(intent, ADD_NOMENCATURE);
		}
	};
	private OnClickListener mSaveClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			SaveChangesAndExit();
		}
	};

	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.act_fixed_prices);
		super.onCreate(savedInstanceState);
		mDB = ApplicationHoreca.getInstance().getDataBase();
		ReadExtras();
		setTitle("Заявка на фиксированные цены от  " + mClient.getName());
		((TextView)findViewById(R.id.text_from_date)).setText("Время действия с:");//R.string.action_date_since);
		InitializeControls();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuClone = menu.add("Сделать копию заявки");
		menuDelete = menu.add("Удалить заявку на фикс.цены");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuDelete){
			promptDelete();
			return true;
		}
		if(item == menuClone){
			promptClone();
			return true;
		}
		return false;
	}

	void doClone(){

		//Parcel dest=new Parcel();
		//mZayavka.writeToParcel( dest, 0);
		//mZayavka = extras.getParcelable(FIXED_PRICES_BID);
		ZayavkaNaSkidki clone = new ZayavkaNaSkidki(mZayavka.getClientID(), mDB);
		clone.setKommentariy(mZayavka.getKommentariy());
		clone.setVremyaNachalaSkidkiPhiksCen(mZayavka.getVremyaNachalaSkidkiPhiksCen());
		clone.setVremyaOkonchaniyaSkidkiPhiksCen(mZayavka.getVremyaOkonchaniyaSkidkiPhiksCen());
		clone.writeToDataBase(mDB);
		FixedPricesNomenclatureData cloneData = new FixedPricesNomenclatureData(mDB, clone);

		//mFixedPricesNomenclatureData.mNomenclaureList
		int cnt = mFixedPricesNomenclatureData.getCount();
		for(int i = 0; i < cnt; i++){
			ZayavkaNaSkidki_TovaryPhiksCen one = mFixedPricesNomenclatureData.getNomenclature(i);
			cloneData.newFixedPriceNomenclature(one.getNomenklaturaID(), one.getArtikul(), one.getNomenklaturaNaimenovanie());
			ZayavkaNaSkidki_TovaryPhiksCen nz = cloneData.getNomenclature(cloneData.getCount() - 1);
			nz.setCena(one.getCena());
			nz.setObyazatelstva(one.getObyazatelstva());
			//nz.WriteToDataBase(mDB);
		}
		cloneData.WriteToDataBase(mDB);
	}

	void promptClone(){
		Auxiliary.pickConfirm(this, "Сделать копию заявки на фикс.цены", "Копировать", new Task(){
			@Override
			public void doTask(){
				doClone();
				Activity_FixedPrices.this.finish();
			}
		});
	}

	void promptDelete(){
		Auxiliary.pickConfirm(this, "Удаление заявки на фикс.цены", "Удалить", new Task(){
			@Override
			public void doTask(){
				mDB.execSQL("delete from ZayavkaNaSkidki where _IDRRef=" + mZayavka.getIDRRef() + ";");
				mDB.execSQL("delete from ZayavkaNaSkidki_TovaryPhiksCen where _ZayavkaNaSkidki_IDRRef=" + mZayavka.getIDRRef() + ";");
				Activity_FixedPrices.this.finish();
			}
		});
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate){
	}

	private void ReadExtras(){
		Bundle extras = getIntent().getExtras();
		mClient = new ClientInfo(mDB, extras.getString(CLIENT_ID));
		mIsEditable = extras.getBoolean(IS_EDITABLE);
		mZayavka = extras.getParcelable(FIXED_PRICES_BID);
		if(mZayavka == null){
			mZayavka = new ZayavkaNaSkidki(mClient.getID(), mDB);
		}
		long fromMs = extras.getLong("nachalo");
		if(fromMs > 0){
			mFromPeriod = Calendar.getInstance();
			mFromPeriod.setTimeInMillis(fromMs);
			mToPeriod = Calendar.getInstance();
			mToPeriod.setTimeInMillis(fromMs);
		}else{
			mFromPeriod = Calendar.getInstance();
			mFromPeriod.setTime(mZayavka.getVremyaNachalaSkidkiPhiksCen());
			mToPeriod = Calendar.getInstance();
			mToPeriod.setTime(mZayavka.getVremyaOkonchaniyaSkidkiPhiksCen());
		}


		UpdateDate();
	}

	void addArt(String art, String id, String name, double artprice){
		if(mFixedPricesNomenclatureData.IsNomenclatureAlreadyInList(id)){
			return;
		}
		mFixedPricesNomenclatureData.newFixedPriceNomenclatureWithPrice(id, art, name, artprice);
		mFixedPricesNomenclatureListAdapter.notifyDataSetChanged();
		mHasChanges = true;
	}

	@Override
	public Object onRetainNonConfigurationInstance(){
		return mFixedPricesNomenclatureData;
	}

	private void InitializeListView(){
		@SuppressWarnings("deprecation")
		Object savedObj = (Object)getLastNonConfigurationInstance();
		if(savedObj != null){
			mFixedPricesNomenclatureData = (FixedPricesNomenclatureData)savedObj;
		}else{
			mFixedPricesNomenclatureData = new FixedPricesNomenclatureData(mDB, mZayavka);
		}
		mFixedPricesNomenclatureListAdapter = new FixedPricesNomenclatureListAdapter(mFixedPricesNomenclatureData);
		mFixedPricesList = (ListView)findViewById(R.id.list_fixed_prices);
		mFixedPricesList.setAdapter(mFixedPricesNomenclatureListAdapter);
		mFixedPricesList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
				ShowEditDialog(arg1, position);
			}
		});
		registerForContextMenu(mFixedPricesList);

		Bundle extras = getIntent().getExtras();
		String listart = extras.getString("change");
		if(listart != null){
			String listprices = extras.getString("prices");
			String[] arts = listart.split(",");
			String[] prices = listprices.split(",");
			for(int ii = 0; ii < arts.length; ii++){
				String sql = "select _idrref as irf,naimenovanie as name from nomenklatura where artikul='" + arts[ii].trim() + "';";
				Bough bb = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				if(bb.child("row").child("irf").value.property.value().length() > 0){
					double artprice = Numeric.string2double(prices[ii].trim());
					addArt(arts[ii].trim()
							, "x'" + bb.child("row").child("irf").value.property.value() + "'"
							, bb.child("row").child("name").value.property.value()
							, artprice
					);
				}
			}
		}
	}

	private void ShowEditDialog(View anchor, int position){
		Popup_EditFixedPriceNomenclature popup = new Popup_EditFixedPriceNomenclature(anchor, mOnPopupClose, mFixedPricesNomenclatureData.getNomenclature(position));
		popup.showLikeQuickAction();
	}

	private void InitializeControls(){
		InitializeListView();
		mEditComment = (EditText)findViewById(R.id.edit_comment);
		mEditComment.setEnabled(mIsEditable);
		mEditComment.setText(mZayavka.getKommentariy());
		Button btnNomenclature = (Button)findViewById(R.id.btn_nomeclature);
		btnNomenclature.setEnabled(mIsEditable);
		btnNomenclature.setOnClickListener(mNomenclatureClick);
		Button btnSave = (Button)findViewById(R.id.btn_save);
		btnSave.setEnabled(mIsEditable);
		btnSave.setOnClickListener(mSaveClick);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.add(Menu.NONE, IDM_LIST_DELETE, Menu.NONE, R.string.delete);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem menu){
		switch(menu.getItemId()){
			case IDM_LIST_DELETE:
				AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)menu.getMenuInfo();
				mListPositionForDelete = menuInfo.position;
				showDialog(IDD_DELETE);
				break;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id){
		LogHelper.debug(this.getClass().getCanonicalName() + ".onCreateDialog: " + id);
		switch(id){
			case IDD_DELETE:{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.confirm);
				builder.setMessage(R.string.quest_delete);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						mFixedPricesNomenclatureData.Remove(mListPositionForDelete);
						mFixedPricesNomenclatureListAdapter.notifyDataSetChanged();
						mHasChanges = true;
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						dialog.dismiss();
					}
				});
				return builder.create();
			}
			case IDD_SAVE_CHANGES:{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.confirm);
				builder.setMessage(R.string.quest_save_changes);
				builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						if(mFixedPricesNomenclatureData.getCount() != 0){
							SaveChangesAndExit();
						}else{
							showDialog(IDD_IS_EMPTY);
						}
					}
				});
				builder.setNegativeButton(R.string.not_save, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						setResult(RESULT_CANCELED, null);
						finish();
					}
				});
				return builder.create();
			}
			case IDD_IS_EMPTY:
				return CreateErrorDialog(R.string.msg_empty_order);
			case IDD_ALREADY_IN_LIST:
				return CreateErrorDialog(R.string.msg_already_in_list);
			case IDD_EMPTY_FIELDS:
				return CreateErrorDialog(R.string.empty_table_fields);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch(requestCode){
				case ADD_NOMENCATURE:
					if(mFixedPricesNomenclatureData.IsNomenclatureAlreadyInList(data.getStringExtra(NOMENCLATURE_ID))){
						CreateErrorDialog(R.string.msg_already_in_list).show();
						return;
					}

					if(Cfg.noSmartPro(data.getStringExtra(ARTIKUL),Activity_FixedPrices.this)){
						/*
						mFixedPricesNomenclatureData.newFixedPriceNomenclature(data.getStringExtra(NOMENCLATURE_ID), data.getStringExtra(ARTIKUL), data.getStringExtra(NAIMENOVANIE));
						mFixedPricesNomenclatureListAdapter.notifyDataSetChanged();
						mHasChanges = true;
						*/
						checkAvailable(data);
					}
					break;
			}
		}
	}
	void checkAvailable(Intent data){
		Cfg.checkFixValid(this, new Task(){
					public void doTask(){
						mFixedPricesNomenclatureData.newFixedPriceNomenclature(data.getStringExtra(NOMENCLATURE_ID), data.getStringExtra(ARTIKUL), data.getStringExtra(NAIMENOVANIE));
						mFixedPricesNomenclatureListAdapter.notifyDataSetChanged();
						mHasChanges = true;
					}
				}, data.getStringExtra(ARTIKUL)
				, ApplicationHoreca.getInstance().getClientInfo().getKod()
				, Auxiliary.short1cDate.format(mFromPeriod.getTime())
				, Auxiliary.short1cDate.format(mToPeriod.getTime())
		);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK & mHasChanges & mFixedPricesNomenclatureData.getCount() != 0){
			showDialog(IDD_SAVE_CHANGES);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	private void SaveChangesAndExit(){


		if(mFixedPricesNomenclatureData.getCount() == 0){
			finish();
			return;
		}
		if(!mFixedPricesNomenclatureData.IsAllDataFilled()){
			showDialog(IDD_EMPTY_FIELDS);
			return;
		}

		System.out.println("klient " + mZayavka.getClientID());
		/*
		CheckBox check_owner_too = ((CheckBox)findViewById(R.id.check_owner_too));
		if(check_owner_too.isChecked()){
			String sql = "select k2.kod as kod,k2.naimenovanie as name,k2._idrref as idrf"
					+ " from Kontragenty k1"
					+ " join Kontragenty k2 on k2._IDRRef=k1.GolovnoyKontragent"
					+ " where k1._IDRRef=" + mZayavka.getClientID();
			Bough owner = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			System.out.println("change " + "x'" + owner.child("row").child("idrf").value.property.value() + "'"
					+ "/" + owner.child("row").child("kod").value.property.value()
					+ "/" + owner.child("row").child("name").value.property.value());
			mZayavka.setClient("x'" + owner.child("row").child("idrf").value.property.value() + "'"
					, owner.child("row").child("kod").value.property.value()
					, owner.child("row").child("name").value.property.value()
			);
		}
*/

		mFixedPricesNomenclatureData.WriteToDataBase(mDB);
		mZayavka.setVremyaNachalaSkidkiPhiksCen(mFromPeriod.getTime());
		mZayavka.setVremyaOkonchaniyaSkidkiPhiksCen(mToPeriod.getTime());
		mZayavka.setKommentariy(mEditComment.getText().toString());
		mZayavka.writeToDataBase(mDB);
		setResult(RESULT_OK);
		finish();
	}
}
