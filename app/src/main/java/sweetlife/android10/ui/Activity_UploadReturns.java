package sweetlife.android10.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import sweetlife.android10.Settings;
import sweetlife.android10.data.common.IStateChanged;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.UploadDocumentAsyncTask;
import sweetlife.android10.data.returns.*;
import sweetlife.android10.database.Request_Returns;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;

import sweetlife.android10.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;
import sweetlife.android10.utils.*;
import tee.binding.properties.*;
import reactive.ui.*;
import sweetlife.android10.supervisor.*;
import java.util.regex.*;

public class Activity_UploadReturns extends Activity_BasePeriod implements ImageView.OnClickListener, IStateChanged, Observer{

	private static UploadReturnsListAdapter mListAdapter;
	private static ListView mList;
	private static ImageView mCheckAll;
	MenuItem menuOtchety;

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuOtchety = menu.add("Отчёты");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuOtchety){
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){

		setContentView(R.layout.act_returns_upload);

		super.onCreate(savedInstanceState);

		setTitle("Выгрузка возвратов");

		InitializeControls();
	}

	private void InitializeControls(){

		mCheckAll = (ImageView)findViewById(R.id.check_all);
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(Boolean.TRUE);
		mCheckAll.setOnClickListener(this);

		((Button)findViewById(R.id.btn_upload)).setOnClickListener(mUploadClick);

		InitializeListView();
	}

	private void InitializeListView(){

		mList = (ListView)findViewById(R.id.list_returns);

		mListAdapter = new UploadReturnsListAdapter(this,
				Request_Returns.RequestUploaded(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
						DateTimeHelper.SQLDateString(mToPeriod.getTime())), this);

		mList.setAdapter(mListAdapter);

		mList.setOnTouchListener(this);
	}

	private View.OnClickListener mUploadClick = new OnClickListener(){
		@Override
		public void onClick(View v){
			if(!SystemHelper.IsNetworkAvailable(Activity_UploadReturns.this)){
				CreateErrorDialog(R.string.network_isnot_available).show();
				return;
			}
			ArrayList<Boolean> stateList = mListAdapter.getStateList();
			int count = stateList.size();
			boolean documentsSelected = false;
			for(int i = 0; i < count; i++){
				if(stateList.get(i)){
					documentsSelected = true;
					break;
				}
			}
			if(!documentsSelected){
				CreateErrorDialog("Не выбраны документы на выгрузку.").show();
				return;
			}
			ArrayList<NomenclatureBasedDocument> dataRequestList = new ArrayList<NomenclatureBasedDocument>();
			java.util.Vector<ZayavkaNaVozvrat> all = new java.util.Vector<ZayavkaNaVozvrat>();
			Cursor cursor = null;
			for(int i = 0; i < count; i++){
				if(stateList.get(i)){
					cursor = mListAdapter.getCursor();
					cursor.moveToPosition(i);
					all.add(new ZayavkaNaVozvrat(
							//dataRequestList.add(new ZayavkaNaVozvrat(
							Request_Returns.get_id(cursor),
							Request_Returns.getIDRRef(cursor),
							Request_Returns.getData(cursor),
							Request_Returns.getNomer(cursor),
							Request_Returns.getKontragentID(cursor),
							Request_Returns.getKontragentKod(cursor),
							Request_Returns.getKontragentNaimanovanie(cursor),
							Request_Returns.getDataOtgruzki(cursor),
							Request_Returns.getAktPretenziyPath(cursor),
							Request_Returns.isUploaded(cursor),
							false
							, Request_Returns.getVersion(cursor)
					));
				}
			}
			/*
			String url = Settings.getInstance().getBaseURL() + "WsUploadOrders/wsuploadvozvHRC.1cws";
			url = Settings.getInstance().getBaseURL() + "wsuploadvozvHRC.1cws";
			System.out.println(url);
			UploadDocumentAsyncTask task = new UploadDocumentAsyncTask(mDB,
					getApplicationContext(),
					getString(R.string.returns_upload_points),
					dataRequestList,
					url,
					new ReturnsXMLParser());
			AsyncTaskManager.getInstance().executeTask(Activity_UploadReturns.this, task);
			*/
			uploadSelected(all);
		}
	};

	String sendOne(ZayavkaNaVozvrat one){
		String log = "";
		try{
			String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
					+ "/hs/SoglasovanieVozvrata/Создать/"
					+ Cfg.whoCheckListOwner();
			System.out.println(url);
			String txt = "{"
					+ "\n	\"NumberDoc\": \"" + one.getNomer() + "\""
					+ "\n	,\"CodClient\": \"" + one.getClientKod() + "\""
					+ "\n	,\"Comment\": \"" + one.getComment().replace('\"', '\'') + "\""
					+ "\n	,\"Товары\":[";
			String delmtr = "";
			ReturnsNomenclatureData mReturnsNomenclatureData = new ReturnsNomenclatureData(mDB, one);
			for(int ii = 0; ii < mReturnsNomenclatureData.getCount(); ii++){
				ZayavkaNaVozvrat_Tovary tovar = mReturnsNomenclatureData.getNomenclature(ii);
				int prichina = tovar.getPrichina();
				int TovarnyVid = 0;
				if(prichina >= 100){
					prichina = prichina - 100;
					TovarnyVid = 1;
				}
				txt = txt + "\n		" + delmtr + "{"
						+ "\n			\"Article\": \"" + tovar.getArtikul() + "\""
						+ "\n			,\"Quantity\": " + tovar.getKolichestvo() + ""
						+ "\n			,\"Prim\": " + prichina + ""
						+ "\n			,\"NumberNac\": \"" + tovar.getNomerNakladnoy() + "\""
						+ "\n			,\"DateNac\": \"" + Auxiliary.short1cDate.format(tovar.getDataNakladnoy().getTime()) + "\""
						+ "\n			,\"TovarnyVid\": " + TovarnyVid
						+ "\n		}";
				delmtr = ",";
			}
			txt = txt + "\n	]";
			//txt = txt + "\n	,\"Files\":[";
			//delmtr = "";
			String[] paths = one.getAktPretenziyPath().split(",");
			/*
			for(int ii = 0; ii < paths.length; ii++){
				String filePath = paths[ii].trim();
				if(filePath.length()>1){
					String[] spl = filePath.split("\\.");
					String rash = spl[spl.length - 1];
					String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new java.io.File(filePath)), android.util.Base64.NO_WRAP);
					txt = txt + delmtr + "{"
							+ "\n				\"File\": \"" + encodedFile + "\""
							+ "\n				,\"rassh\": \"" + rash + "\""
							+ "\n			}";
					delmtr = ",";
				}
			}
			txt = txt + "\n		]";
			*/
			String filePath = paths[0].trim();
			String[] spl = filePath.split("\\.");
			String rash = spl[spl.length - 1];
			String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new java.io.File(filePath)), android.util.Base64.NO_WRAP);
			txt = txt + "\n	,\"File\": \"" + encodedFile + "\""
					+ "\n		,\"rassh\": \"" + rash + "\"";
			txt = txt + "\n	}";
			System.out.println(txt);
			Bough result = Auxiliary.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			System.out.println(result.dumpXML());
			String raw = result.child("raw").value.property.value();
			Bough data = Bough.parseJSON(raw);
			String soobchenie = data.child("Сообщение").value.property.value();
			log=log+soobchenie;
			String status = data.child("Статус").value.property.value();
			if(status.equals("0")){
				Pattern pattern = Pattern.compile("\\d*-\\d*");
				Matcher matcher = pattern.matcher(soobchenie);
				if(matcher.find()){
					String uploadedDocId = matcher.group(0);
					one.mProveden=true;
					one.writeToDataBase(mDB);
					url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/SoglasovanieVozvrata/tpFile/"
							+ Cfg.whoCheckListOwner();
					for(int ff = 1; ff < paths.length; ff++){
						filePath = paths[ff].trim();
						spl = filePath.split("\\.");
						rash = spl[spl.length - 1];
						encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new java.io.File(filePath)), android.util.Base64.NO_WRAP);
						txt = "{"
								+ "\n	\"NumberDoc\": \"" + uploadedDocId + "\""
								+ "\n	,\"File\": \"" + encodedFile + "\""
								+ "\n	,\"rassh\": \"" + rash + "\""
								+ "\n	}";
						result = Auxiliary.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						System.out.println(result.dumpXML());
					}
				}
			}
			//return soobchenie;
		}catch(Throwable t){
			t.printStackTrace();
			log = log + "\n" + t.getMessage();
		}
		return log;
	}

	void uploadSelected(java.util.Vector<ZayavkaNaVozvrat> all){
		final Note msg = new Note().value("");
		new Expect().status.is("Отправка").task.is(new Task(){
			@Override
			public void doTask(){
				for(int ii = 0; ii < all.size(); ii++){
					msg.value(msg.value() + "\n" + sendOne(all.get(ii)));
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Результат: " + msg.value(), Activity_UploadReturns.this);
			}
		}).start(this);
	}

	@Override
	public void update(Observable observable, Object data){

		String result = ((Bundle)data).getString(ManagedAsyncTask.RESULT_STRING);

		if(result != null){
			LogHelper.debug(this.getClass().getCanonicalName() + ".update: " + this.getString(R.string.confirm));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(R.string.confirm);
			builder.setMessage(result);

			builder.setNegativeButton("OK", new DialogInterface.OnClickListener(){
				@Override

				public void onClick(DialogInterface dialog, int arg1){

					dialog.dismiss();

					Requery();
				}
			});

			builder.create().show();
		}
	}

	@Override
	public void onClick(View v){

		boolean newState = false;

		ArrayList<Boolean> stateList = mListAdapter.getStateList();

		if((Boolean)mCheckAll.getTag()){

			newState = false;
			mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
		}else{

			newState = true;
			mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		}

		int count = stateList.size();

		for(int i = 0; i < count; i++){

			stateList.set(i, newState);
		}

		mCheckAll.setTag(newState);

		mListAdapter.notifyDataSetChanged();
	}

	private void setCheckAllState(){

		ArrayList<Boolean> stateList = mListAdapter.getStateList();

		int count = stateList.size();

		for(int i = 0; i < count; i++){

			if(!stateList.get(i)){

				mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
				mCheckAll.setTag(false);
				return;
			}
		}
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(true);

		mListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate){

		Requery();
	}

	private void Requery(){

		mListAdapter.changeCursor(Request_Returns.RequestUploaded(mDB,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()),
				DateTimeHelper.SQLDateString(mToPeriod.getTime())));

		mListAdapter.notifyDataSetChanged();

		setCheckAllState();
	}

	@Override
	public void onChange(){

		setCheckAllState();
	}
}
