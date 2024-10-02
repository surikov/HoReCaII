package sweetlife.android10.ui;

import java.util.Observable;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.update.UpdateTask;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.UIHelper;
import sweetlife.android10.utils.UIHelper.IMessageBoxCallbackInteger;

import sweetlife.android10.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Activity_Update extends Activity_Base {

	private final String UPDATE_IS_IN_PROGRESS = "in_progress";

	private boolean mIsManualUpdate = false;
	private boolean mIsInProgress = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//Activity_UploadBids.logToFile("updateCreate.txt", "Activity_Update.onCreate");
		try {
			setContentView(R.layout.act_update);

			setTitleWithVersionOwner();

			CheckIfManualUpdate();

			if (savedInstanceState != null) {

				mIsInProgress = savedInstanceState.getBoolean(UPDATE_IS_IN_PROGRESS, false);
			} else {

			}
			startUpdateTask();
		} catch (Throwable t) {
			//Activity_UploadBids.logToFile("Activity_Update.catch.txt", t.getMessage());
		}
		//Activity_UploadBids.logToFile("updateCreate.done.txt", "done Activity_Update.onCreate");
		Cfg.requeryFirebaseToken(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putBoolean(UPDATE_IS_IN_PROGRESS, mIsInProgress);

		super.onSaveInstanceState(outState);
	}

	void startUpdateTask() {
		System.out.println("Activity_Update.startUpdateTask");
		//Activity_UploadBids.logToFile("startUpdateCatch.txt",""+mIsInProgress);
		if (!mIsInProgress) {
			LogHelper.debug(this.getClass().getCanonicalName() + " create UpdateTask");

			UpdateTask task = new UpdateTask(this, mDB);
			mIsInProgress = true;
			AsyncTaskManager.getInstance().executeTask(this, task);


		}
	}

	@Override
	protected void onResume() {

		startUpdateTask();
		super.onResume();
	}

	private void CheckIfManualUpdate() {

		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.containsKey(MANUAL_UPDATE)) {

			mIsManualUpdate = true;
		}
	}

	private void CloseActivity() {

		if (mIsManualUpdate) {

			UIHelper.MsgBox(getString(R.string.update),
					getString(R.string.update_complete),
					this, new IMessageBoxCallbackInteger() {

						@Override
						public void MessageBoxResult(int which) {

							finish();
						}
					});
		} else {

			Intent intent = new Intent();
			/*if(ApplicationHoreca.getInstance().modeAndroid6) {
				intent.setClass(Activity_Update.this, Activity_Route_2.class);
			}else{*/
			//intent.setClass(Activity_Update.this, Activity_Route.class);
			intent.setClass(Activity_Update.this, Activity_Route_2.class);
			//}
			startActivity(intent);
			finish();
		}
	}


	@Override
	public void update(Observable observable, Object data) {

		int error = ((Bundle) data).getInt(ManagedAsyncTask.RESULT_INTEGER);

		switch (error) {

			case UpdateTask.UPDATE_APP_FINISHED:

				LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_OWNER_UPDATE, null);
				CloseActivity();
				break;

			case UpdateTask.UPDATE_APP_VERSION:

				//finish();
				break;

			case UpdateTask.ERROR_FREE_SPACE:

				LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_FREE_SPACE, null);
				ShowErrorDialog(R.string.msg_error_free_space);
				break;

			case UpdateTask.ERROR_APK_FILE_NOT_FOUND:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_APP_UPDATE, null);
				ShowErrorDialog(R.string.msg_error_apk_file_not_found);
				break;

			case UpdateTask.ERROR_FTP_DOWNLOAD_APK:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_APP_DOWNLOAD, null);
				ShowErrorDialog(R.string.msg_error_ftp_download_apk);
				break;

			case UpdateTask.ERROR_FTP_DOWNLOAD_XML:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_UPDATE_XML_DOWNLOADED, null);
				ShowErrorDialog(R.string.msg_error_ftp_download_update_xml);
				break;
			case UpdateTask.UNKNOWN_IMEI:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, "Неизвестный IMEI " + Cfg.device_id(), null);
				ShowErrorDialog("Проверьте инет, невозможно проверить IMEI " + Cfg.device_id() + '/' + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim());
				break;
			case UpdateTask.LOCKED_IMEI:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, "Заблокированный IMEI " + Cfg.device_id(), null);
				ShowErrorDialog("Запрещено обновление для IMEI " + Cfg.device_id() + '/' + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim());
				break;

			case UpdateTask.ERROR_FTP_DOWNLOAD_DELTA:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_DELTA_DOWNLOAD, null);
				ShowErrorDialog(R.string.msg_error_ftp_download_delta);
				break;

			case UpdateTask.ERROR_PARSE_XML:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_PARSE_UPDATE_XML, null);
				ShowErrorDialog(R.string.msg_error_parse_update_xml);
				break;

			case UpdateTask.ERROR_NOT_CONNECTED:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_CONNECT_TO_INTERNET, null);
				ShowErrorDialog(R.string.msg_error_not_connected);
				break;

			case UpdateTask.ERROR_NOT_FTP_CONNECTED:

				LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_CONNECT_TO_FTP, null);
				ShowErrorDialog(R.string.msg_error_not_ftp_connected);
				break;
		}
	}

	protected void ShowErrorDialog(int message) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".ShowErrorDialog: " + this.getString(message));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.error);
		builder.setMessage(message);

		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override

			public void onClick(DialogInterface dialog, int arg1) {

				dialog.dismiss();

				CloseActivity();
			}
		});

		builder.create().show();
	}

	protected void ShowErrorDialog(String message) {
		LogHelper.debug(this.getClass().getCanonicalName() + ".ShowErrorDialog: " + message);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.error);
		builder.setMessage(message);

		builder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
			@Override

			public void onClick(DialogInterface dialog, int arg1) {

				dialog.dismiss();

				CloseActivity();
			}
		});

		builder.create().show();
	}
}