package sweetlife.android10.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.Uri;

import reactive.ui.*;
import sweetlife.android10.ui.Activity_Login;
//import android.support.v4.content.FileProvider;

//import android.content.pm.PackageInstaller;
//import android.support.v4.content.pm.ActivityInfoCompat;
//класс обновления приложения
public class AppUpdater{

	//функция обновляет приложение
	public static void Update(Context a, String pathToApkFile) throws Exception{
		//инициализируем файловую переменную входным путем к файлу
		File file = new File(pathToApkFile);
		System.out.println("**********************************file " + pathToApkFile + " exists: " + file.exists());
		if(!file.exists()){
			throw new Exception("File not found!");
		}
		/*инициализируем данные для запуска обновления
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		System.out.println("start " + Uri.fromFile(file).toString());
		context.startActivity(intent);
		*/
		try{
			//runAPK_old(a,pathToApkFile);
			runAPK_new(a, pathToApkFile);
		}catch(Exception t){
			t.printStackTrace();
			throw t;
			/*try {
				runAPK_10(a,pathToApkFile);
			}catch(Exception t2) {
				t2.printStackTrace();
				throw t2;
			}*/
		}
		System.out.println("started " + Uri.fromFile(file).toString());
	}

	public static void runAPK_10(Context a, String pathToApkFile){
		System.out.println("runAPK_10 " + pathToApkFile);
		/*File file = new File(pathToApkFile);
		//Uri apkUri =Uri.fromFile(file);
		//Uri apkUri = androidx.core.content.FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
		Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
		System.out.println("apkUri " + apkUri);
		Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		intent.setData(apkUri);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		context.startActivity(intent);*/
		//PackageInstaller.Session session = null;
		//android.content.pm.PackageManager packageManager=a.getPackageManager();
		//packageManager.getpackageins
	}

	public static void runAPK_old(Context a, String pathToApkFile){
		System.out.println("runAPK_old " + pathToApkFile);
		File file = new File(pathToApkFile);
		Uri apkUri = Uri.fromFile(file);
		//Uri apkUri = Uri.fromFile(toInstall);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		a.startActivity(intent);
	}

	public static void runAPK_new(Context a, String pathToApkFile) throws Exception{
		reinstall(a, pathToApkFile);
	}

	public static void reinstall(Context context, String path) throws Exception{
		System.out.println("reinstall from " + path);
		File file = new File(path);
		System.out.println("getPackageInstaller");
		PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
		PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
		int sessionId = packageInstaller.createSession(sessionParams);
		PackageInstaller.Session session = packageInstaller.openSession(sessionId);
		OutputStream sessionOutputStream = session.openWrite("updateHoreca3", 0, file.length());
		FileInputStream fileInputStream = new FileInputStream(path);
		int byteRead = -1;
		byte[] buffer = new byte[100100];
		System.out.println("start reading");
		while((byteRead = fileInputStream.read(buffer)) != -1){
			sessionOutputStream.write(buffer, 0, byteRead);
		}
		System.out.println("session.fsync");
		session.fsync(sessionOutputStream);
		fileInputStream.close();
		sessionOutputStream.close();
		//session.close();
		System.out.println("start apk");
		//session = packageInstaller.openSession(sessionId);
		Intent intent = new Intent();
		intent.setClass(context, sweetlife.android10.UpdateApk.class);
		int requestCode = 332211321;
		//int flags=0;
		//int flags = PendingIntent.FLAG_IMMUTABLE;
		int flags=PendingIntent.FLAG_UPDATE_CURRENT;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
			flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
		}
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags);
		PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, flags);
System.out.println("pendingIntent "+pendingIntent);
		IntentSender intentSender = pendingIntent.getIntentSender();
/*
		System.out.println(session.getClass().getCanonicalName()+": "+session);
		String[] namesapk = session.getNames();
		for(int ii = 0; ii < namesapk.length; ii++){
			System.out.println(ii + " name " + namesapk[ii]);
		}
		android.os.PersistableBundle pb = session.getAppMetadata();
		for(String key: pb.keySet()){
			System.out.println("meta "+key + ": " + pb.get(key));
		}*/


		session.commit(intentSender);
		session.close();
		System.out.println("done reinstall");
	}
}
