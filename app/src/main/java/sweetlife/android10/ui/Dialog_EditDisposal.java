package sweetlife.android10.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import sweetlife.android10.Settings;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.ClientsListAdapter;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.UploadDocumentAsyncTask;
import sweetlife.android10.data.disposal.DisposalsXMLParser;
import sweetlife.android10.data.disposal.FilesListAdapter;
import sweetlife.android10.data.disposal.RasporyazhenieNaOtgruzku;
import sweetlife.android10.database.Request_ClientsList;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.CameraCaptureHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import sweetlife.android10.R;

public class Dialog_EditDisposal extends Activity_Base implements Observer {
	private RasporyazhenieNaOtgruzku mDisposal;
	private Calendar mDate;
	private ListView mFilesList;
	private EditText mEditDate;
	private EditText mEditNomer;
	private EditText mEditComment;
	private EditText mEditClient;
	private EditText mEditAmount;
	private Button mBtnUpload;
	private FilesListAdapter mFilesListAdapter;
	private int mListPositionForDelete = NULL_LIST_VALUE;
	private boolean mIsEditable = true;
	private boolean mHasChanges = false;
	private Request_ClientsList mClientsRequestHelper;
	private AlertDialog mClientsDialog;
	private CameraCaptureHelper mCameraHelper;
	String hexKlient = null;

	public Dialog_EditDisposal() {
		mClientsRequestHelper = new Request_ClientsList();
		mCameraHelper = new CameraCaptureHelper();
	}

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_edit_disposal);
		super.onCreate(savedInstanceState);
		setTitle("Распоряжение на отгрузку");
		Bundle extras = getIntent().getExtras();
		mIsEditable = extras.getBoolean(IS_EDITABLE);
		hexKlient = extras.getString("hexKlient");
		String kontragentkod=extras.getString("kontragentkod");
		if(kontragentkod==null){
			//
		}else{

		}
		//
		if (hexKlient == null) {
			//
		} else {
			mIsEditable = true;
		}

		if (savedInstanceState != null) {
			ReadParams(savedInstanceState);
			mDate = (Calendar) savedInstanceState.getSerializable(CHOOSED_DAY);
		} else {
			ReadParams(getIntent().getExtras());
			mDate = Calendar.getInstance();
			mDate.setTime(mDisposal.getDate());
		}
		//mIsEditable=true;
		//setTitle(R.string.disposals_for_shipment+hexKlient+mIsEditable);
		InitializeControls();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(IS_EDITABLE, mIsEditable);
		outState.putParcelable(DISPOSAL, mDisposal);
		outState.putSerializable(CHOOSED_DAY, mDate);
		super.onSaveInstanceState(outState);
	}

	private void ReadParams(Bundle states) {
		mIsEditable = states.getBoolean(IS_EDITABLE);
		hexKlient = states.getString("hexKlient");
		if (hexKlient == null) {
			//
		} else {
			mIsEditable = true;
		}
		mDisposal = states.getParcelable(DISPOSAL);
		if (mDisposal == null) {
			mDisposal = new RasporyazhenieNaOtgruzku(mDB);
		}
		if (hexKlient == null) {
			//
		} else {
			ClientInfo info = new ClientInfo(mDB, hexKlient);
			mDisposal.setClient(info.getID(), info.getKod(), info.getName());
		}
	}

	private void InitializeControls() {
		mEditDate = (EditText) findViewById(R.id.edit_date);
		mEditDate.setOnClickListener(mDateClick);
		mEditDate.setEnabled(mIsEditable);
		mEditDate.setText(DateTimeHelper.UIDateString(mDisposal.getDate()));
		Button btnDate = (Button) findViewById(R.id.btn_date);
		btnDate.setEnabled(mIsEditable);
		btnDate.setOnClickListener(mDateClick);
		mEditNomer = (EditText) findViewById(R.id.edit_nomer);
		mEditNomer.setEnabled(mIsEditable);
		mEditNomer.setText(mDisposal.getNomer());
		mEditClient = (EditText) findViewById(R.id.edit_client);
		mEditClient.setOnClickListener(mClientClick);
		mEditClient.setEnabled(mIsEditable);
		mEditClient.setText(mDisposal.getClientName());
		Button btnClient = (Button) findViewById(R.id.btn_client);
		btnClient.setEnabled(mIsEditable);
		btnClient.setOnClickListener(mClientClick);
		mEditAmount = (EditText) findViewById(R.id.edit_amount);
		mEditAmount.setEnabled(mIsEditable);
		mEditAmount.setOnKeyListener(mKeyListener);
		double amount = mDisposal.getSumma();
		if (amount != 0) {
			mEditAmount.setText(DecimalFormatHelper.format(amount));
		}
		mEditComment = (EditText) findViewById(R.id.edit_comment);
		mEditComment.setEnabled(mIsEditable);
		mEditComment.setText(mDisposal.getKommentariy());
		mEditComment.setOnKeyListener(mKeyListener);
		mBtnUpload = (Button) findViewById(R.id.btn_upload);
		mBtnUpload.setEnabled(mIsEditable && !mDisposal.isNew());
		mBtnUpload.setOnClickListener(mUploadClick);
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setEnabled(mIsEditable);
		btnSave.setOnClickListener(mSaveClick);
		ImageButton btnCamera = (ImageButton) findViewById(R.id.btn_camera);
		btnCamera.setEnabled(mIsEditable);
		btnCamera.setOnClickListener(mCameraClick);
		ImageButton btnGallery = (ImageButton) findViewById(R.id.btn_gallery);
		btnGallery.setEnabled(mIsEditable);
		btnGallery.setOnClickListener(mGalleryClick);
		Button btnClose = (Button) findViewById(R.id.btn_close);
		btnClose.setEnabled(mIsEditable);
		btnClose.setOnClickListener(mCloseClick);
		InitializeListView();
	}

	private void InitializeListView() {
		mFilesListAdapter = new FilesListAdapter(mDisposal.getFiles());
		mFilesList = (ListView) findViewById(R.id.list_files);
		mFilesList.setAdapter(mFilesListAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.add(Menu.NONE, IDM_LIST_DELETE, Menu.NONE, R.string.delete);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
			case IDM_LIST_DELETE:
				AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menu.getMenuInfo();
				mListPositionForDelete = menuInfo.position;
				showDialog(IDD_DELETE);
				break;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".onCreateDialog: " + id);
		switch (id) {
			case IDD_DELETE: {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.confirm);
				builder.setMessage(R.string.quest_delete);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						mDisposal.removeFile(mListPositionForDelete);
						mFilesListAdapter.notifyDataSetChanged();
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
			case IDD_SAVE_CHANGES: {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.confirm);
				builder.setMessage(R.string.quest_save_changes);
				builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if (SaveChanges()) {
							setResult(RESULT_OK);
							finish();
						}
					}
				});
				builder.setNegativeButton(R.string.not_save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						setResult(RESULT_CANCELED, null);
						finish();
					}
				});
				return builder.create();
			}
			case IDD_ALREADY_IN_LIST:
				return CreateErrorDialog(R.string.msg_already_in_list);
			case IDD_EMPTY_FIELDS:
				return CreateErrorDialog(R.string.empty_fields);
			case IDD_WORKING_WITH_CAMERA_ERROR:
				return CreateErrorDialog(R.string.working_wth_camera_error);
		}
		return super.onCreateDialog(id);
	}

	private View.OnKeyListener mKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode != KeyEvent.KEYCODE_BACK) {
				mHasChanges = true;
			}
			return false;
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK & mHasChanges) {
			showDialog(IDD_SAVE_CHANGES);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener mClientClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ShowClientsListDialog();
		}
	};
	private OnClickListener mUploadClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (SaveChanges()) {
				if (!SystemHelper.IsNetworkAvailable(Dialog_EditDisposal.this)) {
					CreateErrorDialog(R.string.network_isnot_available).show();
					return;
				}
				ArrayList<NomenclatureBasedDocument> list = new ArrayList<NomenclatureBasedDocument>();
				list.add(mDisposal);

				System.out.println("klient " + mDisposal.getClientName());
				System.out.println(Settings.getInstance().getSERVICE_DISPOSALS());

				UploadDocumentAsyncTask task = new UploadDocumentAsyncTask(mDB, getApplicationContext(), getString(R.string.disposals_upload_points), list
						, Settings.getInstance().getSERVICE_DISPOSALS()
						, new DisposalsXMLParser());
				AsyncTaskManager.getInstance().executeTask(Dialog_EditDisposal.this, task);
			}
		}
	};

	@Override
	public void update(Observable observable, Object data) {
		String result = ((Bundle) data).getString(ManagedAsyncTask.RESULT_STRING);
		if (result != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirm);
			builder.setMessage(result);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					setResult(RESULT_OK);
					finish();
				}
			});
			builder.create().show();
		}
	}

	private OnClickListener mSaveClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SaveChanges();
		}
	};
	private OnClickListener mCloseClick = new OnClickListener() {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			if (mHasChanges) {
				showDialog(IDD_SAVE_CHANGES);
			} else {
				setResult(RESULT_OK);
				finish();
			}
		}
	};
	private OnClickListener mCameraClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startCameraTakePicture();
		}
	};
	private OnClickListener mGalleryClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startMediaGallery();
		}
	};
	private OnClickListener mDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mDate.set(year, monthOfYear, dayOfMonth);
					mEditDate.setText(DateTimeHelper.UIDateString(mDate.getTime()));
				}
			};
			new DatePickerDialog(Dialog_EditDisposal.this, dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	@SuppressWarnings("deprecation")
	private void startCameraTakePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraHelper.createImageFile()));
		} catch (IOException e) {
			showDialog(IDD_WORKING_WITH_CAMERA_ERROR);
			return;
		}
		startActivityForResult(takePictureIntent, GET_CAMERA_PICTURE);
	}

	private void startMediaGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, GET_GALLERY_PICTURE);
	}

	@SuppressWarnings("deprecation")
	private boolean SaveChanges() {
		if (mEditComment.getText().toString().length() == 0) {
			showDialog(IDD_EMPTY_FIELDS);
			return true;
		}
		if (mEditClient.getText().toString().length() == 0) {
			showDialog(IDD_EMPTY_FIELDS);
			return false;
		}
		if (mEditAmount.getText().toString().length() == 0) {
			showDialog(IDD_EMPTY_FIELDS);
			return false;
		}
		mDisposal.setDate(mDate.getTime());
		mDisposal.setKommentariy(mEditComment.getText().toString());
		mDisposal.setSumma(Double.parseDouble(mEditAmount.getText().toString()));
		mDisposal.writeToDataBase(mDB);
		mDisposal.setNew(false);
		mHasChanges = false;
		mBtnUpload.setEnabled(true);
		return true;
	}

	private void ShowClientsListDialog() {
		ListView clientsList = (ListView) getLayoutInflater().inflate(R.layout.dialog_list, (ListView) findViewById(R.id.list));
		clientsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ListView clientsList = (ListView) adapterView;
				Cursor cursor = ((ClientsListAdapter) clientsList.getAdapter()).getCursor();
				AddClientToList(Request_ClientsList.getClientID(cursor));
				mClientsDialog.dismiss();
			}
		});
		ClientsListAdapter clientsListAdapter = new ClientsListAdapter(this, mClientsRequestHelper.Request(mDB, 0, null));
		clientsList.setAdapter(clientsListAdapter);
		LogHelper.debug(this.getClass().getCanonicalName() + ".ShowClientsListDialog: " + getString(R.string.choose_client));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setView(clientsList);
		dialogBuilder.setTitle("Клиенты в маршруте");
		mClientsDialog = dialogBuilder.create();
		mClientsDialog.show();
	}

	private void AddClientToList(String clientID) {
		ClientInfo info = new ClientInfo(mDB, clientID);
		mDisposal.setClient(info.getID(), info.getKod(), info.getName());
		mEditClient.setText(info.getName());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case GET_GALLERY_PICTURE:
					String filePath = null;
					try {
						filePath = SystemHelper.getRealPathFromURI(data.getData(), this);
					} catch (Exception e) {
					}
					if (filePath == null) {
						showDialog(IDD_BAD_FILE_PATH);
						return;
					}
					mDisposal.addFile(filePath);
					mFilesListAdapter.notifyDataSetChanged();
					break;
				case GET_CAMERA_PICTURE:
					mCameraHelper.galleryAddPic(this);
					mDisposal.addFile(mCameraHelper.getCurrentPhotoPath());
					mFilesListAdapter.notifyDataSetChanged();
					break;
			}
		}
	}
}
