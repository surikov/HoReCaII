package sweetlife.android10.ui;

import reactive.ui.Auxiliary;
import reactive.ui.Expect;
import reactive.ui.*;
import reactive.ui.Layoutless;
import sweetlife.android10.database.Requests;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IAppConsts;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.Decompress;
import sweetlife.android10.utils.ftpClient;

import android.Manifest;
import android.app.*;
import android.app.AlertDialog;
import android.content.*;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.pm.*;

import androidx.core.app.ActivityCompat;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sweetlife.android10.R;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.Task;

public class Activity_Login extends Activity {
	Layoutless layoutless;
	Note loginHRC = new Note().value("");
	Note password = new Note().value("");
	Toggle showPassword = new Toggle().value(false);
	Toggle mainProxy = new Toggle().value(true);
	Toggle stopUpdate = new Toggle();
	Toggle downloadDB = new Toggle();
	Toggle stopVacuum = new Toggle();
	Note updateWarning = new Note();
	public static String hrcpasswordName = "hrcpassword";
	static String coarse = "";
	final static public int ResultFromPermission = 3883783;
	public static boolean noVacuum = false;
	public static String packageVersion = "0";
	boolean skipPrepareLogin = false;
	Task taskPersonalLogin = new Task() {

		@Override
		public void doTask() {
			//startRequestPermissions();
/*
			System.out.println("---");
			Cfg.hrcPersonalPasswordCached = password.value();
			System.out.println("---");
			SharedPreferences settings = getSharedPreferences(IAppConsts.PREFS_FILE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(hrcpasswordName, password.value());
			editor.commit();
			Cfg.requeryFirebaseToken();
			doLogin();
			*/
			checkPermissionsOrLogin();
		}
	};


	void adjustVipTerritoryParent() {
		if (Cfg.whoCheckListOwner().toLowerCase().trim().equals("supervip_hrc")) {
			String sql = "update Podrazdeleniya set roditel=x'A003002264FA89D811E08BB0690A5B4A' where kod='х0067';";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		} else {
			if (Cfg.whoCheckListOwner().toLowerCase().trim().equals("region_c")) {
				String sql = "update Podrazdeleniya set roditel=x'BBAF20677C60FED011EA1749382D4BBE' where kod='х0084';";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		}
	}

	void startPermissions() {
		Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:sweetlife.android10"));
		startActivityForResult(intent, ResultFromPermission);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult: " + resultCode + ": " + requestCode);
		if (requestCode == ResultFromPermission) {
			//if (Environment.isExternalStorageManager()) {
			//System.out.println("now Environment.isExternalStorageManager(): " + Environment.isExternalStorageManager());
			//}
			//testRead();
			checkPermissionsOrLogin();
		}
		//testEnter();
	}

	void promptPermissions() {
		new AlertDialog.Builder(this)
				.setTitle("Подтверждение доступа")
				.setMessage("Нажмите ОК и в списке приложений включите перключатель для HoReCa v3")
				//.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						//Toast.makeText(MainActivity333.this, "Yaay", Toast.LENGTH_SHORT).show();
						startPermissions();
					}
				})
				//.setNegativeButton(android.R.string.no, null)
				.show();
	}

	void checkPermissionsOrLogin() {
/*
		this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
				, Manifest.permission.ACCESS_FINE_LOCATION
				, Manifest.permission.ACCESS_BACKGROUND_LOCATION
		}, this.ResultFromPermission);
		*/
		if (!Environment.isExternalStorageManager()) {
			promptPermissions();
		} else {
			prepareLogin();
			appLogin();
		}
	}

	void checkPermissionAndPrepare() {
		if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
				|| this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
				|| this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
		) {
			this.requestPermissions(new String[]{
					Manifest.permission.ACCESS_COARSE_LOCATION
					, Manifest.permission.ACCESS_FINE_LOCATION
					, Manifest.permission.ACCESS_BACKGROUND_LOCATION
			}, this.ResultFromPermission);
		}
		/*System.out.println("checkPermissionsOrLogin");
		System.out.println(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
		System.out.println(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
		System.out.println(this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION));
		*/
		if (Environment.isExternalStorageManager()) {
			prepareLogin();
			skipPrepareLogin = true;
		}
	}

	void prepareLogin() {
		if (skipPrepareLogin) {

		} else {
			ApplicationHoreca.getInstance().InitializeDB();
			ApplicationHoreca.getInstance().FillAgentsInfo();
			setTitleWithVersionOwner();

		}
	}

	void _____reinstall(Activity activity) throws Exception {
		System.out.println("reinstall");

		String path = "/sdcard/horeca/Horeca3.apk";
		File file = new File(path);

		PackageInstaller packageInstaller = activity.getPackageManager().getPackageInstaller();
		PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
		int sessionId = packageInstaller.createSession(sessionParams);
		//int sessionId = packageInstaller.createSession(new android.content.pm.PackageInstaller.SessionParams(
		//		android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL
		//));
		//Auxiliary.clearMyRestrictions(activity);
		PackageInstaller.Session session = packageInstaller.openSession(sessionId);

		//System.out.println(ff.length());
		OutputStream sessionOutputStream = session.openWrite("updateHoreca3", 0, file.length());
		FileInputStream fileInputStream = new FileInputStream(path);
		//BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(sessionOutputStream);
		int byteRead = -1;
		//int cntr = 0;
		byte[] buffer = new byte[100100];
		while ((byteRead = fileInputStream.read(buffer)) != -1) {
			//bufferedOutputStream.write(byteRead);
			sessionOutputStream.write(buffer, 0, byteRead);
			//System.out.println( "byteRead: " + byteRead);
			//if (cntr % 100000 == 0) System.out.println(cntr + ": " + byteRead);
			//cntr++;
		}
		//System.out.println("read " + cntr);

		System.out.println("sync");
		session.fsync(sessionOutputStream);


		fileInputStream.close();
		sessionOutputStream.close();
		session.close();
		//bufferedOutputStream.close();
		//sessionOutputStream.close();
		System.out.println("start apk");
		session = packageInstaller.openSession(sessionId);
		IntentSender statusReceiver = null;
		//Intent intent = new Intent(activity, activity.getClass());
		Intent intent = new Intent();
		intent.setClass(activity, sweetlife.android10.UpdateApk.class);

		//PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, sessionId, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		int requestCode = 332211321;
		int flags = 0;
		PendingIntent pendingIntent = PendingIntent.getService(activity, requestCode, intent, flags);
		IntentSender intentSender = pendingIntent.getIntentSender();
		System.out.println("commit");
		session.commit(intentSender);
		session.close();
		System.out.println("done");
	}

	void appLogin() {

		appLoginNext();
	}

	void appLoginNext() {
		/*if (1 == 1) {
			try {
				sweetlife.android10.update.AppUpdater.reinstall(this,"/sdcard/horeca/Horeca3.apk");
			} catch (Throwable tt) {
				tt.printStackTrace();
				System.out.println(tt.getMessage());
			}
			return;
		}*/
		noVacuum = stopVacuum.value();
		if (!mainProxy.value()) Settings.getInstance().setSecondaryURL();
		//Cfg.currentIMEI(this, user.getName().trim());
		Cfg.hrcPersonalPasswordCached = password.value();
		System.out.println("---");
		SharedPreferences settings = getSharedPreferences(IAppConsts.PREFS_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(hrcpasswordName, password.value());
		editor.commit();
		//Cfg.requeryFirebaseToken();
		System.out.println("saved password " + password.value());


		if (downloadDB.value()) {
			promptReplaceDB();
		} else {
			if (stopUpdate.value()) {
				adjustVipTerritoryParent();
				Intent intent = new Intent();
				intent.setClass(Activity_Login.this, Activity_Route_2.class);
				sweetlife.android10.utils.DatabaseHelper.adjustDataBase(ApplicationHoreca.getInstance().getDataBase());
				startActivity(intent);

			} else {

				Intent intent = new Intent();
				intent.setClass(Activity_Login.this, Activity_Update.class);
				startActivity(intent);
			}
			sweetlife.android10.SweetStart ss = new sweetlife.android10.SweetStart();
			ss.initSchedule(this);
			finish();
		}
	}

	void promptReplaceDB() {
		Auxiliary.pickConfirm(Activity_Login.this, "Скачать новую базу?\n\nВнимание!\nВся информация и имеющиеся заказы будут удалены.", "Удалить и скачать", new Task() {
			@Override
			public void doTask() {
				final Expect ex = new Expect();
				new Expect()//
						.task.is(new Task() {
					@Override
					public void doTask() {
						try {
							doDownloadReplaceDB();
						} catch (Throwable t) {
							//Auxiliary.inform("Ошибка: " + t.getMessage(), Activity_Login.this);
							ex.cancel.is(true);
							t.printStackTrace();
							coarse = "" + t.getMessage();
						}
					}
				})//
						.afterDone.is(new Task() {
					@Override
					public void doTask() {
						if (ex.cancel.property.value()) {
							doDownloadReplaceDBCancel();
						} else {
							doDownloadReplaceDBOk();
						}
					}
				})//
						.afterCancel.is(new Task() {
					@Override
					public void doTask() {
						doDownloadReplaceDBCancel();
					}
				})//
						.status.is("Ждите...")//
						.start(Activity_Login.this);
			}
		});
		return;
	}


	public void startCheckAccess() {
		System.out.println("startCheckAccess");
		Note result = new Note().value("!");
		new Expect().status.is("Проверка доступа").task.is(new Task() {
			public void doTask() {
				String access = Settings.check_1C_access();
				result.value(access);
			}
		}).afterDone.is(new Task() {
			public void doTask() {
				if (result.value().length() > 0) {
					Auxiliary.warn("Неверный пароль или нет доступа, просмотр отчётов и выгрузка документов недоступны:\n\n" + result.value(), Activity_Login.this);
				}
			}
		}).start(this);
	}
/*
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		System.out.println("onRequestPermissionsResult " + requestCode);
		for (int nn = 0; nn < permissions.length; nn++) {
			int flag=99;
			if(grantResults.length>=nn){
				flag=grantResults[nn];
			}
			//System.out.println("" + nn + ": " + permissions[nn]);
			int testPermission=this.checkSelfPermission(permissions[nn]);
			boolean yes = this.shouldShowRequestPermissionRationale(permissions[nn]);
			System.out.println("onRequestPermissionsResult " + permissions[nn] + ": show " + yes+", test "+testPermission+", granted "+flag);
		}
		try {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/Horeca2.xml";


			System.out.println("closed: " + path);
		}catch(Throwable t){
			t.printStackTrace();
		}

		//Settings.getInstance();
		//System.out.println("Settings.getInstance().cfg().dumpXML()");
		//System.out.println(Settings.getInstance().cfg().dumpXML());
		//ApplicationHoreca.getInstance().InitializeDB();
		//ApplicationHoreca.getInstance().FillAgentsInfo();
		//setTitleWithVersionOwner();
	}


	public void startRequestPermissions() {
		String[] permissionsList = new String[]{
				"android.permission.READ_EXTERNAL_STORAGE"
				, "android.permission.WRITE_EXTERNAL_STORAGE"
				, "android.permission.READ_MEDIA_VIDEO"
				, "android.permission.READ_MEDIA_IMAGES"
				//, Manifest.permission.READ_EXTERNAL_STORAGE
		};
		for (int nn = 0; nn < permissionsList.length; nn++) {
			int testPermission=this.checkSelfPermission(permissionsList[nn]);
			boolean yes = this.shouldShowRequestPermissionRationale(permissionsList[nn]);
			System.out.println("shouldShowRequestPermissionRationale " + permissionsList[nn] + ": " + yes+", "+testPermission);
		}
		System.out.println("requestPermissions " + permissionsList.length);
		this.requestPermissions(permissionsList, Auxiliary.requestPermissionsCode);
	}*/

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Activity_Login.onCreate " + Auxiliary.bundle2bough(savedInstanceState).dumpXML());
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		//SharedPreferences settings = getSharedPreferences(IAppConsts.PREFS_FILE_NAME, 0);
		String apkReinstallStatus = Auxiliary.activityExatras(this).child("apkReinstallStatus").value.property.value();
		if (apkReinstallStatus.length() > 0) {
			if (apkReinstallStatus.equals("0")) {
				Auxiliary.warn("Установлена новая версия приложения", this);
			} else {
				Auxiliary.warn("Ощибка установки: " + apkReinstallStatus, this);
			}
		}

		//startRequestPermissions();
		//setTitleWithVersionOwner();

		SharedPreferences settings = getSharedPreferences(IAppConsts.PREFS_FILE_NAME, 0);
		password.value(settings.getString(hrcpasswordName, ""));

		layoutless.child(new RedactToggle(this).labelText.is("Первичный шлюз").yes.is(mainProxy)
				.top().is(1 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		).child(new RedactToggle(this).labelText.is("Вспомогательный шлюз").yes.is(mainProxy.not())
				.top().is(1.5 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		).child(new RedactToggle(this).labelText.is("Запретить обновление").yes.is(stopUpdate)
				.top().is(2 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		).child(new RedactToggle(this).labelText.is("Скачать новую базу").yes.is(downloadDB)
				.top().is(2.5 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		).child(new RedactToggle(this).labelText.is("Запретить сжатие").yes.is(stopVacuum)
				.top().is(3 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		);
/*
hrc210 - 96B407
hrc213 - K1WLPB
supernn_hrc - JRA61P
*/
        /*if (
                Cfg.currentHRC().equals("supernn_hrc")//город12
                        || Cfg.currentHRC().equals("hrc511")
                        || Cfg.currentHRC().equals("hrc29")
                        || Cfg.currentHRC().equals("hrc28")
                        || Cfg.currentHRC().equals("hrc27")
                        || Cfg.currentHRC().equals("hrc23")
                        || Cfg.currentHRC().equals("hrc213")
                        || Cfg.currentHRC().equals("hrc210")

                        || Cfg.currentHRC().equals("supernn2_hrc")
                        || Cfg.currentHRC().equals("hrc417")
                        || Cfg.currentHRC().equals("hrc268")
                        || Cfg.currentHRC().equals("hrc26")
                        || Cfg.currentHRC().equals("hrc25")
                        || Cfg.currentHRC().equals("hrc229")
                        || Cfg.currentHRC().equals("hrc22")
                        || Cfg.currentHRC().equals("hrc212")
                        || Cfg.currentHRC().equals("hrc211")

                        || Cfg.currentHRC().equals("superobl_hrc")
                        || Cfg.currentHRC().equals("hrc405")
                        || Cfg.currentHRC().equals("hrc304")
                        || Cfg.currentHRC().equals("hrc242")
                        || Cfg.currentHRC().equals("hrc227")
                        || Cfg.currentHRC().equals("hrc222")
                        || Cfg.currentHRC().equals("hrc215")
                        || Cfg.currentHRC().equals("hrc214")
//-------------------------------------------------------------------------------дзержинск заволжье кстово арзамас
                        || Cfg.currentHRC().equals("superobl2_hrc")
                        || Cfg.currentHRC().equals("hrc360")
                        || Cfg.currentHRC().equals("hrc335")
                        || Cfg.currentHRC().equals("hrc306")
                        || Cfg.currentHRC().equals("hrc273")
                        || Cfg.currentHRC().equals("hrc231")
                        || Cfg.currentHRC().equals("hrc220")

                        || Cfg.currentHRC().equals("superobl4_hrc")
                        || Cfg.currentHRC().equals("hrc305")
                        || Cfg.currentHRC().equals("hrc287")
                        || Cfg.currentHRC().equals("hrc274")
                        || Cfg.currentHRC().equals("hrc224")
                        || Cfg.currentHRC().equals("hrc219")

                        || Cfg.currentHRC().equals("superobl3_hrc")
                        || Cfg.currentHRC().equals("hrc353")
                        || Cfg.currentHRC().equals("hrc281")
                        || Cfg.currentHRC().equals("hrc267")
                        || Cfg.currentHRC().equals("hrc266")
                        || Cfg.currentHRC().equals("hrc228")
                        || Cfg.currentHRC().equals("hrc226")
                        || Cfg.currentHRC().equals("hrc217")
//-------------------------------------------------------------------------------саранск
                        || Cfg.currentHRC().equals("superul_hrc")
                        || Cfg.currentHRC().equals("supersar_hrc")
                        || Cfg.currentHRC().equals("superpen_hrc")
                        || Cfg.currentHRC().equals("hrc546")
                        || Cfg.currentHRC().equals("hrc536")
                        || Cfg.currentHRC().equals("hrc535")
                        || Cfg.currentHRC().equals("hrc431")
                        || Cfg.currentHRC().equals("hrc420")
                        || Cfg.currentHRC().equals("hrc411")
                        || Cfg.currentHRC().equals("hrc406")
                        || Cfg.currentHRC().equals("hrc395")
                        || Cfg.currentHRC().equals("hrc386")
                        || Cfg.currentHRC().equals("hrc384")
                        || Cfg.currentHRC().equals("hrc382")
                        || Cfg.currentHRC().equals("hrc375")
                        || Cfg.currentHRC().equals("hrc357")
                        || Cfg.currentHRC().equals("hrc356")
                        || Cfg.currentHRC().equals("hrc325")
                        || Cfg.currentHRC().equals("hrc324")
                        || Cfg.currentHRC().equals("hrc322")
                        || Cfg.currentHRC().equals("hrc319")
                        || Cfg.currentHRC().equals("hrc290")
                        || Cfg.currentHRC().equals("hrc279")
                        || Cfg.currentHRC().equals("hrc264")
                        || Cfg.currentHRC().equals("hrc263")
                        || Cfg.currentHRC().equals("hrc262")
                        || Cfg.currentHRC().equals("hrc261")
                        || Cfg.currentHRC().equals("hrc249")
                        || Cfg.currentHRC().equals("hrc248")
                        || Cfg.currentHRC().equals("hrc247")
//----------------------------------------------------------------------владимир
                        || Cfg.currentHRC().equals("superyar2_hrc")
                        || Cfg.currentHRC().equals("superyar_hrc")
                        || Cfg.currentHRC().equals("supervlad2_hrc")
                        || Cfg.currentHRC().equals("supervlad_hrc")
                        || Cfg.currentHRC().equals("superkos_hrc")
                        || Cfg.currentHRC().equals("superivan_hrc")
                        || Cfg.currentHRC().equals("hrc525")
                        || Cfg.currentHRC().equals("hrc524")
                        || Cfg.currentHRC().equals("hrc522")
                        || Cfg.currentHRC().equals("hrc509")
                        || Cfg.currentHRC().equals("hrc509")
                        || Cfg.currentHRC().equals("hrc506")
                        || Cfg.currentHRC().equals("hrc490")
                        || Cfg.currentHRC().equals("hrc460")
                        || Cfg.currentHRC().equals("hrc457")
                        || Cfg.currentHRC().equals("hrc448")
                        || Cfg.currentHRC().equals("hrc436")
                        || Cfg.currentHRC().equals("hrc432")
                        || Cfg.currentHRC().equals("hrc430")
                        || Cfg.currentHRC().equals("hrc394")
                        || Cfg.currentHRC().equals("hrc390")
                        || Cfg.currentHRC().equals("hrc389")
                        || Cfg.currentHRC().equals("hrc387")
                        || Cfg.currentHRC().equals("hrc383")
                        || Cfg.currentHRC().equals("hrc377")
                        || Cfg.currentHRC().equals("hrc374")
                        || Cfg.currentHRC().equals("hrc362")
                        || Cfg.currentHRC().equals("hrc350")
                        || Cfg.currentHRC().equals("hrc340")
                        || Cfg.currentHRC().equals("hrc308")
                        || Cfg.currentHRC().equals("hrc299")
                        || Cfg.currentHRC().equals("hrc298")
                        || Cfg.currentHRC().equals("hrc297")
                        || Cfg.currentHRC().equals("hrc296")
                        || Cfg.currentHRC().equals("hrc295")
                        || Cfg.currentHRC().equals("hrc293")
                        || Cfg.currentHRC().equals("hrc292")
                        || Cfg.currentHRC().equals("hrc289")
                        || Cfg.currentHRC().equals("hrc288")
//------------------------------------------------------------воронеж
                        || Cfg.currentHRC().equals("supervoronezh2_hrc")
                        || Cfg.currentHRC().equals("supervoronezh_hrc")
                        || Cfg.currentHRC().equals("supertula_hrc")
                        || Cfg.currentHRC().equals("superstos_hrc")
                        || Cfg.currentHRC().equals("superryaz_hrc")
                        || Cfg.currentHRC().equals("superlip_hrc")
                        || Cfg.currentHRC().equals("hrc566")
                        || Cfg.currentHRC().equals("hrc528")
                        || Cfg.currentHRC().equals("hrc527")
                        || Cfg.currentHRC().equals("hrc523")
                        || Cfg.currentHRC().equals("hrc520")
                        || Cfg.currentHRC().equals("hrc515")
                        || Cfg.currentHRC().equals("hrc514")
                        || Cfg.currentHRC().equals("hrc510")
                        || Cfg.currentHRC().equals("hrc502")
                        || Cfg.currentHRC().equals("hrc501")
                        || Cfg.currentHRC().equals("hrc500")
                        || Cfg.currentHRC().equals("hrc499")
                        || Cfg.currentHRC().equals("hrc494")
                        || Cfg.currentHRC().equals("hrc486")
                        || Cfg.currentHRC().equals("hrc478")
                        || Cfg.currentHRC().equals("hrc477")
                        || Cfg.currentHRC().equals("hrc474")
                        || Cfg.currentHRC().equals("hrc469")
                        || Cfg.currentHRC().equals("hrc466")
                        || Cfg.currentHRC().equals("hrc465")
                        || Cfg.currentHRC().equals("hrc464")
                        || Cfg.currentHRC().equals("hrc452")
                        || Cfg.currentHRC().equals("hrc451")
                        || Cfg.currentHRC().equals("hrc442")
                        || Cfg.currentHRC().equals("hrc435")
                        || Cfg.currentHRC().equals("hrc434")
                        || Cfg.currentHRC().equals("hrc433")
                        || Cfg.currentHRC().equals("hrc401")
                        || Cfg.currentHRC().equals("hrc396")
                        || Cfg.currentHRC().equals("hrc388")
//---------------------------------------------------чуваш
                        || Cfg.currentHRC().equals("superkir_hrc")
                        || Cfg.currentHRC().equals("superiola_hrc")
                        || Cfg.currentHRC().equals("superchuv_hrc")
                        || Cfg.currentHRC().equals("supercheb_hrc")
                        || Cfg.currentHRC().equals("hrc557")
                        || Cfg.currentHRC().equals("hrc518")
                        || Cfg.currentHRC().equals("hrc497")
                        || Cfg.currentHRC().equals("hrc484")
                        || Cfg.currentHRC().equals("hrc441")
                        || Cfg.currentHRC().equals("hrc413")
                        || Cfg.currentHRC().equals("hrc404")
                        || Cfg.currentHRC().equals("hrc393")
                        || Cfg.currentHRC().equals("hrc376")
                        || Cfg.currentHRC().equals("hrc370")
                        || Cfg.currentHRC().equals("hrc351")
                        || Cfg.currentHRC().equals("hrc332")
                        || Cfg.currentHRC().equals("hrc327")
                        || Cfg.currentHRC().equals("hrc326")
                        || Cfg.currentHRC().equals("hrc318")
                        || Cfg.currentHRC().equals("hrc303")
                        || Cfg.currentHRC().equals("hrc291")
                        || Cfg.currentHRC().equals("hrc286")
                        || Cfg.currentHRC().equals("hrc278")
                        || Cfg.currentHRC().equals("hrc277")
                        || Cfg.currentHRC().equals("hrc276")
                        || Cfg.currentHRC().equals("hrc271")
                        || Cfg.currentHRC().equals("hrc269")
                        || Cfg.currentHRC().equals("hrc24")
                        || Cfg.currentHRC().equals("hrc239")
                        || Cfg.currentHRC().equals("hrc235")
                        || Cfg.currentHRC().equals("hrc233")
                        || Cfg.currentHRC().equals("hrc232")
                        || Cfg.currentHRC().equals("hrc230")
                        || Cfg.currentHRC().equals("hrc225")
                        || Cfg.currentHRC().equals("hrc223")
                        || Cfg.currentHRC().equals("hrc221")
                        || Cfg.currentHRC().equals("hrc218")
                        || Cfg.currentHRC().equals("hrc21")
//---------------------------------------------------spb
                        || Cfg.currentHRC().equals("superspb3_hrc")
                        || Cfg.currentHRC().equals("superspb2_hrc")
                        || Cfg.currentHRC().equals("superspb_hrc")
                        || Cfg.currentHRC().equals("hrc568")
                        || Cfg.currentHRC().equals("hrc567")
                        || Cfg.currentHRC().equals("hrc556")
                        || Cfg.currentHRC().equals("hrc555")
                        || Cfg.currentHRC().equals("hrc554")
                        || Cfg.currentHRC().equals("hrc553")
                        || Cfg.currentHRC().equals("hrc552")
                        || Cfg.currentHRC().equals("hrc550")
                        || Cfg.currentHRC().equals("hrc549")
                        || Cfg.currentHRC().equals("hrc548")
                        || Cfg.currentHRC().equals("hrc547")
                        || Cfg.currentHRC().equals("hrc544")
                        || Cfg.currentHRC().equals("hrc541")
                        || Cfg.currentHRC().equals("hrc540")
                        || Cfg.currentHRC().equals("hrc539")
                        || Cfg.currentHRC().equals("hrc538")
                        || Cfg.currentHRC().equals("hrc537")
                        || Cfg.currentHRC().equals("hrc533")
                        || Cfg.currentHRC().equals("hrc532")
                        || Cfg.currentHRC().equals("hrc531")
                        || Cfg.currentHRC().equals("hrc530")
                        || Cfg.currentHRC().equals("hrc529")
//-------------------------------------kazan
                        || Cfg.currentHRC().equals("supervipkaz_hrc")
                        || Cfg.currentHRC().equals("supernch_hrc")
                        || Cfg.currentHRC().equals("superkaz3_hrc")
                        || Cfg.currentHRC().equals("superkaz2_hrc")
                        || Cfg.currentHRC().equals("superkaz_hrc")
                        || Cfg.currentHRC().equals("superalm_hrc")
                        || Cfg.currentHRC().equals("hrc571")
                        || Cfg.currentHRC().equals("hrc559")
                        || Cfg.currentHRC().equals("hrc526")
                        || Cfg.currentHRC().equals("hrc521")
                        || Cfg.currentHRC().equals("hrc519")
                        || Cfg.currentHRC().equals("hrc508")
                        || Cfg.currentHRC().equals("hrc507")
                        || Cfg.currentHRC().equals("hrc505")
                        || Cfg.currentHRC().equals("hrc504")
                        || Cfg.currentHRC().equals("hrc503")
                        || Cfg.currentHRC().equals("hrc498")
                        || Cfg.currentHRC().equals("hrc493")
                        || Cfg.currentHRC().equals("hrc492")
                        || Cfg.currentHRC().equals("hrc491")
                        || Cfg.currentHRC().equals("hrc485")
                        || Cfg.currentHRC().equals("hrc480")
                        || Cfg.currentHRC().equals("hrc459")
                        || Cfg.currentHRC().equals("hrc458")
                        || Cfg.currentHRC().equals("hrc447")
                        || Cfg.currentHRC().equals("hrc440")
                        || Cfg.currentHRC().equals("hrc416")
                        || Cfg.currentHRC().equals("hrc414")
                        || Cfg.currentHRC().equals("hrc410")
                        || Cfg.currentHRC().equals("hrc409")
                        || Cfg.currentHRC().equals("hrc399")
                        || Cfg.currentHRC().equals("hrc385")
                        || Cfg.currentHRC().equals("hrc381")
                        || Cfg.currentHRC().equals("hrc323")
                        || Cfg.currentHRC().equals("hrc316")
                        || Cfg.currentHRC().equals("hrc285")
                        || Cfg.currentHRC().equals("hrc282")
                        || Cfg.currentHRC().equals("hrc272")
                        || Cfg.currentHRC().equals("hrc270")
                        || Cfg.currentHRC().equals("hrc265")
                        || Cfg.currentHRC().equals("hrc260")
                        || Cfg.currentHRC().equals("hrc259")
                        || Cfg.currentHRC().equals("hrc258")
                        || Cfg.currentHRC().equals("hrc257")
                        || Cfg.currentHRC().equals("hrc256")
                        || Cfg.currentHRC().equals("hrc255")
                        || Cfg.currentHRC().equals("hrc254")
                        //---------------------|| Cfg.currentHRC().equals("hrc252")
                        || Cfg.currentHRC().equals("hrc251")
                        || Cfg.currentHRC().equals("hrc250")
                        || Cfg.currentHRC().equals("hrc240")

//------------------------------moscow
                        || Cfg.currentHRC().equals("supersevobl_hrc")
                        || Cfg.currentHRC().equals("supermvz_hrc")
                        || Cfg.currentHRC().equals("supermvv_hrc")
                        || Cfg.currentHRC().equals("supermsv_hrc")
                        || Cfg.currentHRC().equals("supermsev_hrc")
                        || Cfg.currentHRC().equals("supermse_hrc")
                        || Cfg.currentHRC().equals("supermosvip2_hrc")
                        || Cfg.currentHRC().equals("supermosvip_hrc")
                        || Cfg.currentHRC().equals("supermosobl_hrc")
                        || Cfg.currentHRC().equals("supermoscen_hrc")
                        || Cfg.currentHRC().equals("supermos_hrc")
                        || Cfg.currentHRC().equals("supermcv_hrc")
                        || Cfg.currentHRC().equals("supermcs_hrc")
                        || Cfg.currentHRC().equals("puratos_msk")
                        || Cfg.currentHRC().equals("hrc565")

                        || Cfg.currentHRC().equals("hrc565")
                        || Cfg.currentHRC().equals("hrc564")
                        || Cfg.currentHRC().equals("hrc563")
                        || Cfg.currentHRC().equals("hrc562")
                        || Cfg.currentHRC().equals("hrc561")
                        || Cfg.currentHRC().equals("hrc558")
                        || Cfg.currentHRC().equals("hrc545")
                        || Cfg.currentHRC().equals("hrc543")
                        || Cfg.currentHRC().equals("hrc542")
                        || Cfg.currentHRC().equals("hrc534")
                        || Cfg.currentHRC().equals("hrc517")
                        || Cfg.currentHRC().equals("hrc516")
                        || Cfg.currentHRC().equals("hrc512")
                        || Cfg.currentHRC().equals("hrc496")
                        || Cfg.currentHRC().equals("hrc495")
                        || Cfg.currentHRC().equals("hrc489")
                        || Cfg.currentHRC().equals("hrc488")
                        || Cfg.currentHRC().equals("hrc483")
                        || Cfg.currentHRC().equals("hrc482")
                        || Cfg.currentHRC().equals("hrc481")
                        || Cfg.currentHRC().equals("hrc479")
                        || Cfg.currentHRC().equals("hrc476")
                        || Cfg.currentHRC().equals("hrc475")
                        || Cfg.currentHRC().equals("hrc473")
                        || Cfg.currentHRC().equals("hrc472")
                        || Cfg.currentHRC().equals("hrc471")
                        || Cfg.currentHRC().equals("hrc470")
                        || Cfg.currentHRC().equals("hrc468")
                        || Cfg.currentHRC().equals("hrc467")
                        || Cfg.currentHRC().equals("hrc463")
                        || Cfg.currentHRC().equals("hrc462")
                        || Cfg.currentHRC().equals("hrc461")
                        || Cfg.currentHRC().equals("hrc456")
                        || Cfg.currentHRC().equals("hrc455")
                        || Cfg.currentHRC().equals("hrc454")
                        || Cfg.currentHRC().equals("hrc453")
                        || Cfg.currentHRC().equals("hrc450")
                        || Cfg.currentHRC().equals("hrc449")
                        || Cfg.currentHRC().equals("hrc446")
                        || Cfg.currentHRC().equals("hrc445")
                        || Cfg.currentHRC().equals("hrc444")
                        || Cfg.currentHRC().equals("hrc443")
                        || Cfg.currentHRC().equals("hrc439")
                        || Cfg.currentHRC().equals("hrc438")
                        || Cfg.currentHRC().equals("hrc429")
                        || Cfg.currentHRC().equals("hrc428")
                        || Cfg.currentHRC().equals("hrc427")
                        || Cfg.currentHRC().equals("hrc426")
                        || Cfg.currentHRC().equals("hrc425")
                        || Cfg.currentHRC().equals("hrc424")
                        || Cfg.currentHRC().equals("hrc423")
                        || Cfg.currentHRC().equals("hrc422")
                        || Cfg.currentHRC().equals("hrc421")
                        || Cfg.currentHRC().equals("hrc419")
                        || Cfg.currentHRC().equals("hrc418")
                        || Cfg.currentHRC().equals("hrc415")
                        || Cfg.currentHRC().equals("hrc412")
                        || Cfg.currentHRC().equals("hrc408")
                        || Cfg.currentHRC().equals("hrc407")
                        || Cfg.currentHRC().equals("hrc403")
                        || Cfg.currentHRC().equals("hrc402")
                        || Cfg.currentHRC().equals("hrc398")
                        || Cfg.currentHRC().equals("hrc397")
                        || Cfg.currentHRC().equals("hrc392")
                        || Cfg.currentHRC().equals("hrc391")
                        || Cfg.currentHRC().equals("hrc380")
                        || Cfg.currentHRC().equals("hrc373")
                        || Cfg.currentHRC().equals("hrc372")
                        || Cfg.currentHRC().equals("hrc371")
                        || Cfg.currentHRC().equals("hrc365")
                        || Cfg.currentHRC().equals("hrc346")
                        || Cfg.currentHRC().equals("hrc435")
                        || Cfg.currentHRC().equals("hrc344")
                        || Cfg.currentHRC().equals("hrc343")
                        || Cfg.currentHRC().equals("hrc342")
                        || Cfg.currentHRC().equals("hrc341")
                        || Cfg.currentHRC().equals("hrc339")
                        || Cfg.currentHRC().equals("hrc338")
                        || Cfg.currentHRC().equals("hrc337")
                        || Cfg.currentHRC().equals("hrc336")
                        || Cfg.currentHRC().equals("hrc334")
                        || Cfg.currentHRC().equals("hrc333")
                        || Cfg.currentHRC().equals("hrc331")
                        || Cfg.currentHRC().equals("hrc329")
                        || Cfg.currentHRC().equals("hrc328")
                        || Cfg.currentHRC().equals("hrc320")
                        || Cfg.currentHRC().equals("hrc317")
                        || Cfg.currentHRC().equals("hrc315")
                        || Cfg.currentHRC().equals("hrc313")
                        || Cfg.currentHRC().equals("hrc311")
                        || Cfg.currentHRC().equals("hrc310")
                        || Cfg.currentHRC().equals("hrc294")
//vip
                        || Cfg.currentHRC().equals("supervip_tender")
                        || Cfg.currentHRC().equals("supervip_hrc")
                        || Cfg.currentHRC().equals("hrc560")
                        || Cfg.currentHRC().equals("hrc307")
                        || Cfg.currentHRC().equals("hrc301")
                        || Cfg.currentHRC().equals("hrc300")
                        || Cfg.currentHRC().equals("hrc280")
                        || Cfg.currentHRC().equals("hrc246")
                        || Cfg.currentHRC().equals("hrc243")
                        || Cfg.currentHRC().equals("hrc238")



        ) {*/
		layoutless.child(new Decor(this).labelText.is("Пароль")
				.top().is(4 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new Knob(this).labelText.is("Показать пароль").afterTap.is(new Task() {
					public void doTask() {
						showPassword.value(true);
					}
				})

						.hidden().is(showPassword)
						.top().is(4.5 * Auxiliary.tapSize)
						.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
						.width().is(5 * Auxiliary.tapSize)
						.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new RedactText(this).text.is(password)
				.hidden().is(showPassword.not())
				//.password.is(true)
				.top().is(4.5 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new Knob(this).labelText.is(loginHRC).afterTap.is(taskPersonalLogin)
				.top().is(5.5 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new Decor(this).labelText.is(updateWarning)
				.labelColor.is(0xffcc0000)
				.top().is(7 * Auxiliary.tapSize)
				.left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
				.width().is(15 * Auxiliary.tapSize)
				.height().is(3 * Auxiliary.tapSize)
		);
       /* } else {
            layoutless.child(new Knob(this).labelText.is(loginHRC).afterTap.is(taskCommonLogin)
                    .top().is(4 * Auxiliary.tapSize)
                    .left().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))
                    .width().is(5 * Auxiliary.tapSize)
                    .height().is(1 * Auxiliary.tapSize)
            );
        }*/

		//List<String>list= Auxiliary.getListOfFakeLocationApps(this);
		/*
		long ms=new Date().getTime();
		int i1=(int)ms;
		Long lo=new Long(ms);
		int i2=lo.intValue();
		System.out.println("::::::::::::::::"+ms+"/"+i1+"/"+lo+"/"+i2+"/"+Integer.MIN_VALUE+"/"+Integer.MAX_VALUE);
		*/
		checkPermissionAndPrepare();
		startCheckAccess();
	}


	void setTitleWithVersionOwner() {
		try {
			String chOwner = sweetlife.android10.supervisor.Cfg.whoCheckListOwner();
			if (chOwner.length() > 0) {
				tee.binding.Bough data = Auxiliary.fromCursor(
						ApplicationHoreca.getInstance().getDataBase().rawQuery(
								"select l.naimenovanie as name from PhizLicaPolzovatelya f join Polzovateli p on p._idrref=f.polzovatel join PhizicheskieLica l on l._idrref=f.phizlico where trim(p.kod)='"
										+ chOwner + "' order by f.period desc;"
								, null));
				chOwner = chOwner + "/" + data.child("row").child("name").value.property.value();
			}
			packageVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			setTitle(chOwner
					+ "/" + getString(R.string.app_name)//
					//+ "  " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName
					+ "  " + packageVersion
					+ ", " + Settings.getInstance().getBaseIP()//
					+ "/" + Settings.getInstance().selectedBase1C()//
					+ " " + Settings.getInstance().selectedWSDL()//
			);

			if (sweetlife.android10.supervisor.Cfg.whoCheckListOwner().length() > 0) {
				stopUpdate.value(true);
			} else {
				if (!Requests.IsSyncronizationDateLater(0)) {
					stopUpdate.value(true);
				}
			}
			//loginHRC.value("Вход для " + Cfg.databaseHRC());
			//loginHRC.value("Вход для " + Cfg.databaseHRC()+"/"+Cfg.whoCheckListOwner());
			loginHRC.value("Вход для " + Cfg.whoCheckListOwner());

			boolean noCRAvailable = Requests.IsSyncronizationDateLater(0);
			if (noCRAvailable) {
				Calendar syncCalendar = LogHelper.getLastSuccessfulUpdate();
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				updateWarning.value("Последнее успешное обновление " + sdf.format(syncCalendar.getTime()) + ".\nНеобходимо обновить данные.");
			}
		} catch (Throwable e) {
			setTitle(e.getMessage());
			Auxiliary.warn("База данных повреждена", Activity_Login.this);
		}
	}

	void doDownloadReplaceDB() throws Exception {
		System.out.println("doDownloadReplaceDB");
		String url = "swlife_database.zip";
		System.out.println("url " + url);
		ftpClient.downloadFile(Settings.getInstance().getTABLET_WORKING_DIR(), url);
		System.out.println("doDownloadReplaceDB downloaded");
		Bough rez = new Bough().name.is("config");
		//System.out.println("'" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "'");
		//System.out.println("'" + ApplicationHoreca.getInstance().getCurrentAgent().updateKod+ "'");
		ApplicationHoreca.getInstance().getDataBase().close();
		System.out.println("doDownloadReplaceDB db closed");
		rez.child("userKey").value.is(ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim());
		rez.child("updateKey").value.is(ApplicationHoreca.getInstance().getCurrentAgent().updateKod);
		System.out.println("doDownloadReplaceDB user infa catched");
		Auxiliary.writeTextToFile(new File("/sdcard/horeca/Horeca2.xml"), "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + rez.dumpXML());
		System.out.println("doDownloadReplaceDB config writed");
		Decompress decompressor = new Decompress();
		decompressor.unzip(Settings.getInstance().getTABLET_WORKING_DIR(), Settings.getInstance().getTABLET_WORKING_DIR() + "swlife_database.zip");
		System.out.println("doDownloadReplaceDB done");
	}

	void doDownloadReplaceDBOk() {
		System.out.println("doDownloadReplaceDBOk");
		Auxiliary.pickConfirm(Activity_Login.this, "Перзайдите в приложение и обновите базу", "Закрыть", new Task() {
			@Override
			public void doTask() {
				finish();
			}
		});
	}

	void doDownloadReplaceDBCancel() {
		System.out.println("doDownloadReplaceDBCancel");
		Auxiliary.pickConfirm(Activity_Login.this, "Ошибка замены базы: " + coarse, "Закрыть", new Task() {
			@Override
			public void doTask() {
				finish();
			}
		});
	}
}
