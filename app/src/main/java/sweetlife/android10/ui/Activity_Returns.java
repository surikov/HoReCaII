package sweetlife.android10.ui;

import java.io.*;
import java.lang.reflect.*;
import java.util.Calendar;
import java.util.regex.*;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.data.returns.ReturnNomenclatureListAdapter;
import sweetlife.android10.data.returns.ReturnsNomenclatureData;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat_Tovary;
import sweetlife.android10.data.returns.ReturnNomenclatureListAdapter.IRemoveItem;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.utils.*;
import sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.*;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import tee.binding.properties.*;
import reactive.ui.*;


public class Activity_Returns extends Activity_Base implements ITableColumnsNames, IRemoveItem{

	//private EditText mEditShippingDate;
	MenuItem menuDelete;
	MenuItem menuClearFiles;
	//MenuItem menuUploadDocument;
	//MenuItem menuAddFile;
	//MenuItem menuDoFoto;
	//String uploadedDocId = "";

	int prichinaNum = 0;
	private ListView mReturnsList;
	private EditText mEditAktPretenziyPath;
	private EditText mEditVersion;
	private ReturnNomenclatureListAdapter mReturnNomenclatureListAdapter;
	private ZayavkaNaVozvrat_Tovary mListItemForDelete = null;
	private boolean mIsEditable = true;
	private boolean mHasChanges = false;
	private ClientInfo mClient;
	private ZayavkaNaVozvrat mZayavka;
	private Calendar mShippingDate;
	private ReturnsNomenclatureData mReturnsNomenclatureData;
	private CameraCaptureHelper mCameraHelper;

	private OnCloseListener mOnPopupClose = new OnCloseListener(){
		@Override
		public void onClose(int param){
			System.out.println("mOnPopupClose.onClose");
			mReturnNomenclatureListAdapter.notifyDataSetChanged();
			mHasChanges = true;
		}
	};
	/*
	private OnClickListener mUploadDoc = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			//System.out.println("mUploadDoc");
			//SoglasovanieVozvrata/Создать/hrc22
			if(mReturnsNomenclatureData.getCount() == 0){
				showDialog(IDD_EMPTY_FIELDS);
				return;
			}
			if(!mReturnsNomenclatureData.IsAllDataFilled()){
				showDialog(IDD_EMPTY_FIELDS);
				return;
			}
			Auxiliary.pickConfirm(Activity_Returns.this, "Отправить заявку на возврат", "Отправить", new Task(){
				public void doTask(){
					saveToDB();
					uploadDocument();
				}
			});
		}
	};
	*/
	private OnClickListener mNomenclatureClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			Intent intent = new Intent();
			intent.setClass(Activity_Returns.this, Activity_NomenclatureSimple.class);
			startActivityForResult(intent, ADD_NOMENCATURE);
		}
	};
	private OnClickListener mSaveClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			SaveChangesAndExit();
		}
	};
	private OnClickListener mCameraClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			/*if(uploadedDocId.length()<1){
				Auxiliary.warn("Выгрузите заявку для добавления файла." , Activity_Returns.this);
				return;
			}*/
			//if(mIsEditable)
			startCameraTakePicture();
		}
	};
	private OnClickListener mGalleryClick = new OnClickListener(){
		@Override
		public void onClick(View arg0){
			/*if(uploadedDocId.length()<1){
				Auxiliary.warn("Выгрузите заявку для добавления файла." , Activity_Returns.this);
				return;
			}*/
			//if(mIsEditable)
			Auxiliary.startMediaGallery(Activity_Returns.this,GET_GALLERY_PICTURE2);
		}
	};

	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.act_returns);
		super.onCreate(savedInstanceState);
		ReadExtras();
		setTitle("Заявка на возврат от " + mClient.getName());//+": "+getIntent().getExtras().getInt("prichina")+": "+prichinaNum);
		InitializeControls();
		mCameraHelper = new CameraCaptureHelper();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuDelete = menu.add("Удалить заявку на возврат");
		//menuUploadDocument = menu.add("Выгрузить заявку");
		//menuAddFile = menu.add("Добавить файл");
		//menuDoFoto = menu.add("Добавить фото");
		menuClearFiles = menu.add("Удалить все вложения");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuDelete){
			promptDelete();
			return true;
		}
		if(item == menuClearFiles){
			promptClear();
			return true;
		}

		/*if(item == menuUploadDocument){
			promptUploadDocument();
			return true;
		}
		if(item == menuAddFile){
			promptFile();
			return true;
		}
		if(item == menuDoFoto){
			promptCamera();
			return true;
		}*/
		return false;
	}

	/*
		void ____uploadDocument(){
			final Bough result = new Bough();
			//final Note response = new Note();
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
					+ "/hs/SoglasovanieVozvrata/Создать/"
					+ Cfg.whoCheckListOwner();
			String txt = "{"
					+ "\n	\"NumberDoc\": \"" + this.mZayavka.getNomer() + "\""
					+ "\n	,\"CodClient\": \"" + this.mZayavka.getClientKod() + "\""
					+ "\n	,\"Comment\": \"" + this.mZayavka.getComment().replace('\"', '\'') + "\""
					+ "\n	,\"Товары\":[";
			String delmtr = "";
			for(int ii = 0; ii < mReturnsNomenclatureData.getCount(); ii++){
				ZayavkaNaVozvrat_Tovary tovar=mReturnsNomenclatureData.getNomenclature(ii);
				int prichina = tovar.getPrichina();
				int TovarnyVid = 0;
				if (prichina >= 100) {
					prichina = prichina - 100;
					TovarnyVid = 1;
				}
				txt = txt + "\n		"+delmtr+"{"
						+ "\n		\"Article\": \""+tovar.getArtikul()+"\""
						+ "\n		,\"Quantity\": "+tovar.getKolichestvo()+""
						+ "\n		,\"Prim\": "+prichina+""
						+ "\n		,\"NumberNac\": \""+tovar.getNomerNakladnoy()+"\""
						+ "\n		,\"DateNac\": \""+Auxiliary.short1cDate.format(tovar.getDataNakladnoy().getTime())+"\""
						+ "\n		,\"TovarnyVid\": "+TovarnyVid
						+ "\n		}";
				delmtr = ",";
			}
			txt = txt+ "\n	]"
					+ "\n}";
			System.out.println(url);
			System.out.println(txt);
			final String txtdata=txt;
			new Expect().status.is("Отправка").task.is(new Task(){
				@Override
				public void doTask(){
					result.children = Auxiliary.loadTextFromPrivatePOST(url, txtdata, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()).children;
					System.out.println(result.dumpXML());
				}
			}).afterDone.is(new Task(){
				@Override
				public void doTask(){
					String raw = result.child("raw").value.property.value();
					Bough data = Bough.parseJSON(raw);
					String soobchenie = data.child("Сообщение").value.property.value();
					String status = data.child("Статус").value.property.value();
					try{
						Pattern pattern = Pattern.compile("\\d*-\\d*");
						Matcher matcher = pattern.matcher(soobchenie);
						if(matcher.find()){
							uploadedDocId = matcher.group(0);
						}
					}catch(Throwable tt){
						tt.printStackTrace();
					}
					Auxiliary.warn("Отправка: " + soobchenie, Activity_Returns.this);
					if(uploadedDocId.length()>1 && status.equals("0")){
						lockDocument();
					}
				}
			}).start(this);
		}
		*/
/*
	void __lockDocument(){
		if(uploadedDocId.length() > 1){
			System.out.println("lock " + uploadedDocId);

			mIsEditable = false;
			mEditAktPretenziyPath = (EditText)findViewById(R.id.edit_bill_file);
			mEditAktPretenziyPath.setEnabled(mIsEditable);

			mEditVersion = (EditText)findViewById(R.id.edit_comment);
			mEditVersion.setEnabled(mIsEditable);

			Button btnUpload = (Button)findViewById(R.id.btn_upload_vozvrat);
			btnUpload.setEnabled(mIsEditable);

			Button btnNomenclature = (Button)findViewById(R.id.btn_nomeclature);
			btnNomenclature.setEnabled(mIsEditable);

			Button btnSave = (Button)findViewById(R.id.btn_save);
			btnSave.setEnabled(mIsEditable);

			//ImageButton btnCamera = (ImageButton)findViewById(R.id.btn_camera);
			//btnCamera.setEnabled(!mIsEditable);

			//ImageButton btnGallery = (ImageButton)findViewById(R.id.btn_gallery);
			//btnGallery.setEnabled(!mIsEditable);

			mZayavka.mNomer = uploadedDocId;
			mZayavka.mProveden = true;
			saveToDB();
		}
	}
	*/
/*
	void promptFile(){

	}

	void promptCamera(){

	}*/
	void promptClear(){
		if(mIsEditable){
			Auxiliary.pickConfirm(this, "Удаление вложений из заявки", "Удалить", new Task(){
				@Override
				public void doTask(){
					mEditAktPretenziyPath.setText("");
					mZayavka.setAktPretenziyPath("");
					mZayavka.writeToDataBase(mDB);
				}
			});
		}else{
			Auxiliary.warn("Заявка уже выгружена", this);
		}
	}

	void promptDelete(){
		Auxiliary.pickConfirm(this, "Удаление заявки на возврат", "Удалить", new Task(){
			@Override
			public void doTask(){
				mDB.execSQL("delete from ZayavkaNaVozvrat where _IDRRef=" + mZayavka.getIDRRef() + ";");
				mDB.execSQL("delete from ZayavkaNaVozvrat_Tovary where _ZayavkaNaVozvrat_IDRRef=" + mZayavka.getIDRRef() + ";");
				Activity_Returns.this.finish();
			}
		});
	}

	private void ReadExtras(){
		Bundle extras = getIntent().getExtras();
		mIsEditable = extras.getBoolean(IS_EDITABLE);
		mClient = new ClientInfo(mDB, extras.getString(CLIENT_ID));
		mZayavka = extras.getParcelable(RETURNS_BID);
		/*if(mZayavka!=null){
			if(mZayavka.mProveden){
				uploadedDocId = mZayavka.mNomer;
				mIsEditable=false;
			}
		}*/
		int p = extras.getInt("prichina");
		prichinaNum = 0;
		switch(p){
		/*case 1:
			prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_BRAK;
			break;*/
			case 1:
				prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_NEUSTROILOKACHESTVO;
				break;
			case 2:
				prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_PERESORT;
				break;
			case 3:
				prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_CREDIT_NOTA;
				break;
			case 4:
				prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_KOROTKIE_SROKI;
				break;
			case 5:
				prichinaNum = ZayavkaNaVozvrat_Tovary.REASON_OSHIBKA_PRI_ZAKAZE;
				break;
		}
		//System.out.println("prichinaNum "+prichinaNum);
		if(mZayavka == null){
			mClient = new ClientInfo(mDB, extras.getString(CLIENT_ID));
			mZayavka = new ZayavkaNaVozvrat(mClient.getID(), mDB);
		}
		mShippingDate = Calendar.getInstance();
		mShippingDate.setTime(mZayavka.getDataOtgruzki());
	}

	private void InitializeListView(){
		mReturnsNomenclatureData = new ReturnsNomenclatureData(mDB, mZayavka);
		mReturnNomenclatureListAdapter = new ReturnNomenclatureListAdapter(mReturnsNomenclatureData, mOnPopupClose, mIsEditable, this);
		mReturnsList = (ListView)findViewById(R.id.list_returns);
		mReturnsList.setAdapter(mReturnNomenclatureListAdapter);
	}

	void promptDeletePath(){
		if(mIsEditable){
			if(mEditAktPretenziyPath.getText().toString().length() > 1){
				String[] items= mEditAktPretenziyPath.getText().toString().split(",");
				final Numeric selection = new Numeric();
				Auxiliary.pickSingleChoice(this, items, selection, "Удаление файла акта претензий", new Task(){
					public void doTask(){
						deleteSelectedPath(selection.value().intValue());
					}
				}, null, null, null, null);
			}
		}
	}

	void deleteSelectedPath(int nn){
		String[] items= mEditAktPretenziyPath.getText().toString().split(",");
		String txt="";
		String delim="";
		for(int ii=0;ii<items.length;ii++){
			if(ii!=nn){
				txt=txt+delim+items[ii];
				delim=",";
			}
		}
		mEditAktPretenziyPath.setText(txt);
		this.mHasChanges=true;
	}

	private void InitializeControls(){
		/*if(this.mZayavka.mProveden){
			mIsEditable=false;
			uploadedDocId=this.mZayavka.getNomer();
		}*/
		InitializeListView();
		//mEditShippingDate = (EditText) findViewById(R.id.edit_returns_date);
		//mEditShippingDate.setOnClickListener(mShippingDateClick);
		//mEditShippingDate.setEnabled(mIsEditable);
		//Button btnShippingDate = (Button) findViewById(R.id.btn_returns_date);
		//btnShippingDate.setEnabled(mIsEditable);
		//btnShippingDate.setOnClickListener(mShippingDateClick);
		mEditAktPretenziyPath = (EditText)findViewById(R.id.edit_bill_file);
		mEditAktPretenziyPath.setEnabled(mIsEditable);
		mEditAktPretenziyPath.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				promptDeletePath();
			}
		});

		mEditVersion = (EditText)findViewById(R.id.edit_comment);
		mEditVersion.setEnabled(mIsEditable);
/*
		Button btnUpload = (Button)findViewById(R.id.btn_upload_vozvrat);
		btnUpload.setEnabled(mIsEditable);
		btnUpload.setOnClickListener(mUploadDoc);
*/
		Button btnNomenclature = (Button)findViewById(R.id.btn_nomeclature);
		btnNomenclature.setEnabled(mIsEditable);
		btnNomenclature.setOnClickListener(mNomenclatureClick);
		Button btnSave = (Button)findViewById(R.id.btn_save);
		btnSave.setEnabled(mIsEditable);
		btnSave.setOnClickListener(mSaveClick);
		ImageButton btnCamera = (ImageButton)findViewById(R.id.btn_camera);
		//btnCamera.setEnabled(!mIsEditable);
		btnCamera.setOnClickListener(mCameraClick);
		ImageButton btnGallery = (ImageButton)findViewById(R.id.btn_gallery);
		//btnGallery.setEnabled(!mIsEditable);
		btnGallery.setOnClickListener(mGalleryClick);
		UpdateControls();
	}

	/*
	private OnClickListener mShippingDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mShippingDate.set(year, monthOfYear, dayOfMonth);
					//mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));
				}
			};
			new DatePickerDialog(Activity_Returns.this, dateSetListener, mShippingDate.get(Calendar.YEAR), mShippingDate.get(Calendar.MONTH),
					mShippingDate.get(Calendar.DAY_OF_MONTH)).show();
		}
	};*/
	private void UpdateControls(){
		//mEditShippingDate.setText(DateTimeHelper.UIDateString(mShippingDate.getTime()));
		mEditAktPretenziyPath.setText(mZayavka.getAktPretenziyPath());
		mEditVersion.setText(mZayavka.getVersion());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void OnRemove(ZayavkaNaVozvrat_Tovary tovar){
		//if(this.uploadedDocId.length()==0){
		mListItemForDelete = tovar;
		showDialog(IDD_DELETE);
		//}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		System.out.println("onActivityResult " + requestCode + "/" + resultCode + "/" + data);

		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch(requestCode){
				case ADD_NOMENCATURE:
					if(mReturnsNomenclatureData.IsNomenclatureAlreadyInList(data.getStringExtra(NOMENCLATURE_ID))){
						CreateErrorDialog(R.string.msg_already_in_list).show();
						return;
					}
					int prichina = 0;
					if(prichinaNum > 0){
						prichina = prichinaNum;
					}else{
						for(int i = 0; i < mReturnsNomenclatureData.mNomenclaureList.size(); i++){
							NomenclatureBasedItem nomenclatureBasedItem = mReturnsNomenclatureData.mNomenclaureList.get(i);
							ZayavkaNaVozvrat_Tovary zayavkaNaVozvrat_Tovary = (ZayavkaNaVozvrat_Tovary)nomenclatureBasedItem;
							prichina = zayavkaNaVozvrat_Tovary.getPrichina();
							break;
						}
					}
					//System.out.println(prichinaNum + "/" + prichina + "====================");
					mReturnsNomenclatureData.newReturnsNomenclature(data.getStringExtra(NOMENCLATURE_ID), data.getStringExtra(ARTIKUL), data.getStringExtra(NAIMENOVANIE), prichina);
					mReturnNomenclatureListAdapter.notifyDataSetChanged();
					mHasChanges = true;
					break;
				case GET_GALLERY_PICTURE2:
					String filePath = null;
					/*
					try {
						filePath = SystemHelper.getRealPathFromURI(data.getData(), this);
						java.io.File f = new java.io.File(filePath);
						LogHelper.debug(this.getClass().getCanonicalName() + " selected " + filePath + ", " + f.length());
					} catch(Exception e) {
						LogHelper.debug(this.getClass().getCanonicalName() + ": " + filePath + " " + e.getMessage());
					}
					*/
					Uri uri = data.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);

					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать " + uri, Activity_Returns.this);
						return;
					}
					//mZayavka.setAktPretenziyPath(filePath);
					//mEditAktPretenziyPath.setText(filePath);
					sendFile(filePath);
					break;
				case GET_CAMERA_PICTURE:
					mCameraHelper.galleryAddPic(this);
					//mZayavka.setAktPretenziyPath(mCameraHelper.getCurrentPhotoPath());
					//mEditAktPretenziyPath.setText(mCameraHelper.getCurrentPhotoPath());
					sendFile(mCameraHelper.getCurrentPhotoPath());
					break;
			}
		}
	}

	void sendFile(final String filePath){
		System.out.println("sendFile " + filePath);
		String path = mZayavka.getAktPretenziyPath();
		if(("" + path).length() > 5){
			path = path + ", ";
		}
		path = path + filePath;
		mEditAktPretenziyPath.setText(path);
		mZayavka.setAktPretenziyPath(path);
		mZayavka.writeToDataBase(mDB);

	}

	/*
	void sendFile(final String filePath){
		System.out.println("sendFile " + filePath);
		String[] spl = filePath.split("\\.");
		String rash = spl[spl.length - 1];
		String encodedFile = Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), Base64.NO_WRAP);
		final String txt = "{"
				+ "\n	\"NumberDoc\": \"" + this.uploadedDocId + "\""
				+ "\n	,\"File\": \"" + encodedFile + "\""
				//+ "\n	,\"DateDoc\": \"20240405\""
				+ "\n	,\"rassh\": \"" + rash + "\""
				+ "\n	}";

		System.out.println("txt " + txt);
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/SoglasovanieVozvrata/tpFile/"
				+ Cfg.whoCheckListOwner();
		System.out.println("url " + url);

		final Bough result = new Bough();
		new Expect().status.is("Отправка").task.is(new Task(){
			@Override
			public void doTask(){
				result.children = Auxiliary.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()).children;
				System.out.println(result.dumpXML());
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				String raw = result.child("raw").value.property.value();
				Bough data = Bough.parseJSON(raw);
				Auxiliary.warn("Отправка: " + data.child("Сообщение").value.property.value(), Activity_Returns.this);
			}
		}).start(this);
	}
*/
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
						mReturnsNomenclatureData.Remove(mListItemForDelete);
						mReturnNomenclatureListAdapter.notifyDataSetChanged();
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
						if(mReturnsNomenclatureData.getCount() != 0){
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
			case IDD_BAD_FILE_PATH:
				return CreateErrorDialog(R.string.bad_file_path);
			case IDD_EMPTY_FILE_PATH:
				return CreateErrorDialog(R.string.empty_bill_file);
			case IDD_EMPTY_FIELDS:
				return CreateErrorDialog(R.string.empty_table_fields);
			case IDD_WORKING_WITH_CAMERA_ERROR:
				return CreateErrorDialog(R.string.working_wth_camera_error);
		}
		return super.onCreateDialog(id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK & mHasChanges & mReturnsNomenclatureData.getCount() != 0){
			showDialog(IDD_SAVE_CHANGES);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	private void SaveChangesAndExit(){
		if(mReturnsNomenclatureData.getCount() == 0){
			finish();
			return;
		}
		if(!mReturnsNomenclatureData.IsAllDataFilled()){
			showDialog(IDD_EMPTY_FIELDS);
			return;
		}
		if(mEditAktPretenziyPath.getText().toString().length() < 3){
			Auxiliary.warn("Добавьте файл с актом претензий.", Activity_Returns.this);
			return;
		}
		saveToDB();
		setResult(RESULT_OK);
		finish();
	}

	void saveToDB(){
		mReturnsNomenclatureData.WriteToDataBase(mDB);
		mZayavka.setDataOtgruzki(mShippingDate.getTime());
		mZayavka.setAktPretenziyPath(mEditAktPretenziyPath.getText().toString());
		mZayavka.setVersion(mEditVersion.getText().toString());
		mZayavka.writeToDataBase(mDB);
		mHasChanges = false;
	}



	@SuppressWarnings("deprecation")
	private void startCameraTakePicture(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try{
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraHelper.createImageFile()));
		}catch(IOException e){
			showDialog(IDD_WORKING_WITH_CAMERA_ERROR);
			return;
		}
		startActivityForResult(takePictureIntent, GET_CAMERA_PICTURE);
	}
}
