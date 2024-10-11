package reactive.ui;

import android.database.sqlite.*;
import android.media.MediaScannerConnection;
import android.app.admin.*;
import android.content.pm.*;
import android.view.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.*;
import android.graphics.*;
import android.hardware.*;
import android.text.InputType;
//import android.util.*;
import android.webkit.MimeTypeMap;
import android.widget.*;

import java.text.ParseException;
import java.util.*;

import android.content.res.*;
import android.view.inputmethod.InputMethodManager;

import sweetlife.android10.*;
import sweetlife.android10.log.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;
import java.text.*;

import tee.binding.Bough;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;


//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.google.firebase.messaging.*;
//import com.google.firebase.*;


public class Auxiliary{

	//
	//
	//
	//
	//
	public final static String reactiveVersion = "7.02";
	private static final char[] FIRST_CHAR = new char[256];
	private static final char[] SECOND_CHAR = new char[256];
	private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final byte[] DIGITS = new byte['f' + 1];
	public static int colorBackground = 0x66ff0000;
	public static int textColorPrimary = 0x6600ff00;
	public static int textColorHint = 0x660000ff;
	public static int textColorHighlight = 0x66ffff00;
	public static int textColorLink = 0x6600ffff;
	public static int colorLine = 0x66ff00ff;
	public static Paint paintLine = null;
	public static int colorSelection = 0x663399ff;
	//public static int colorSelection = 0x99ff0000;
	public static float density = 1;
	public static int tapSize = 8;
	public static SensorEventListener sensorEventListener = null;
	public static double accelerometerX = 0;
	public static double accelerometerY = 0;
	public static double accelerometerZ = 0;
	//public static double accelerometerAverageX = 0;
	//public static double accelerometerAverageY = 0;
	//public static double accelerometerAverageZ = 0;
	public static double accelerometerNoise = 1.0;
	public static SimpleDateFormat mssqlTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat sqliteTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat sqliteDate = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd.MM.yyyy");
	public static SimpleDateFormat short1cDate = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat rusDate = new SimpleDateFormat("dd.MM.yy");
	public static SimpleDateFormat rusTimeDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
	public static double latitude = 0;//https://www.google.ru/maps/@56.3706531,44.0456248,10.25z - latitude+":"+longitude
	public static double longitude = 0;//east-west, долгота
	public static long gpsTime = 0;//last time

	public static int colorTransparent = 0x00000000;
	public static int colorGrey = 0xffdddddd;
	public static int colorPink = 0xffff6699;
	public static int colorGreen = 0xffddffdd;

	//
	//public static final int startMediaGalleryID = 22779984;
	//
	private static LocationListener locationListener = null;

	static{
		for(int i = 0; i < 256; i++){
			FIRST_CHAR[i] = HEX_DIGITS[(i >> 4) & 0xF];
			SECOND_CHAR[i] = HEX_DIGITS[i & 0xF];
		}
		for(int i = 0; i <= 'F'; i++){
			DIGITS[i] = -1;
		}
		for(byte i = 0; i < 10; i++){
			DIGITS['0' + i] = i;
		}
		for(byte i = 0; i < 6; i++){
			DIGITS['A' + i] = (byte)(10 + i);
			DIGITS['a' + i] = (byte)(10 + i);
		}
	}

	public static String version(Context c){
		String v = "?";
		try{
			v = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
		}catch(Throwable t){
			v = t.toString();
		}
		return v;
	}

	public static String pad(String text, int length, char ch){
		length = length - text.length();
		for(int i = 0; i < length; i++){
			text = ch + text;
		}
		return text;
	}

	public static int transparent(int color, double transparency){
		int r = color;
		int t = (int)(255.0 * transparency);
		r = (color & 0x00ffffff) + (t << 24);
		return r;
	}

	public static void showMap(Activity a, double currentLatitude, double currentLongitude, double toLatitude, double toLongitude){
		String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=18&q=%f,%f(A)", currentLatitude, currentLongitude, toLatitude, toLongitude);
		if(toLatitude == 0 && toLongitude == 0){
			uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=18", currentLatitude, currentLongitude);
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		a.startActivity(intent);
	}

	public static void ___stopGPS(Activity activity){
		if(locationListener != null){
			//
		}
	}

	public static void __startGPS(final Activity activity){
		try{
			if(locationListener == null){

				LocationManager locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
				locationListener = new LocationListener(){
					@Override
					public void onLocationChanged(Location location){
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						gpsTime = location.getTime();
						//System.out.println("onLocationChanged " + latitude + ":" + longitude + ", " + gpsTime);
						//writeGPS(activity);
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras){
						//System.out.println("onStatusChanged " + provider + ", " + status);
					}

					@Override
					public void onProviderEnabled(String provider){
						//System.out.println("onProviderEnabled " + provider);
					}

					@Override
					public void onProviderDisabled(String provider){
						//System.out.println("onProviderDisabled " + provider);
					}
				};
				if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, locationListener);
				}else{
					//System.out.println("LocationManager.NETWORK_PROVIDER");
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, locationListener);
				}
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	public static void initThemeConstants(Context context){
		TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{ //
				android.R.attr.colorBackground//
				, android.R.attr.textColorPrimary//
				, android.R.attr.textColorHint//
				, android.R.attr.textColorHighlight//
				, android.R.attr.textColorLink //
		});
		colorBackground = array.getColor(0, colorBackground);
		textColorPrimary = array.getColor(1, textColorPrimary);
		textColorHint = array.getColor(2, textColorHint);
		textColorHighlight = array.getColor(3, textColorHighlight);
		textColorLink = array.getColor(4, textColorLink);
		array.recycle();
		if((textColorPrimary & 0x00ffffff) > 0x00666666){//darkonlight
			colorLine = transparent(textColorPrimary, 0.2);
			colorSelection = transparent(textColorLink, 0.3);
		}else{//lightondark
			colorLine = transparent(textColorPrimary, 0.1);
			colorSelection = transparent(textColorLink, 0.2);
		}
		paintLine = new Paint();
		paintLine.setColor(Auxiliary.colorLine);
		paintLine.setAntiAlias(true);
		paintLine.setFilterBitmap(true);
		paintLine.setDither(true);
		//colorLine=transparent(textColorPrimary,0.2);
		//colorSelection=transparent(textColorLink,0.2);
		density = context.getResources().getDisplayMetrics().density;
		tapSize = (int)(60.0 * density);
	}

	public static void hideSoftKeyboard(Activity activity){
		try{
			InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(Throwable t){
			//t.printStackTrace();
		}
	}

	public static boolean isOnline(Context c){
		try{
			ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()){
				return true;
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return false;
	}

	//public static SQLiteDatabase connectSQLiteDatabase(String path, Context c//
	//	, int mode//Context.MODE_WORLD_WRITEABLE
	//) {
	/*
	SQLiteOpenHelper openHelper = new SQLiteOpenHelper(c, path, null, version) {
		@Override
		public void onCreate(SQLiteDatabase db) {
			//
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//
		}
		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//
		}
	};
	SQLiteDatabase r = openHelper.getWritableDatabase();
	*/
	//SQLiteDatabase	db = c.openOrCreateDatabase(path, mode, null);
	//return db;
	//}
	public static byte[] string2Hex(String hexString){
		int length = hexString.length();
		if((length & 0x01) != 0){
			//throw new IllegalArgumentException("Odd number of characters: " + hexString);
			hexString = "0" + hexString;
			length++;
		}
		//boolean badHex = false;
		byte[] out = new byte[length >> 1];
		for(int i = 0, j = 0; j < length; i++){
			int c1 = hexString.charAt(j);
			int c2 = hexString.charAt(j + 1);
			j = j + 2;
			byte d1 = DIGITS[c1];
			byte d2 = DIGITS[c2];
			if(c1 > 'f'){
				//badHex = true;
				//break;
				c1 = '0';
			}
			if(d1 == -1){
				//badHex = true;
				//break;
				d1 = '0';
			}
			if(c2 > 'f'){
				//badHex = true;
				//break;
				c2 = '0';
			}
			if(d2 == -1){
				//badHex = true;
				//break;
				d2 = '0';
			}
			out[i] = (byte)(d1 << 4 | d2);
		}
		/*if (badHex) {
			throw new IllegalArgumentException("Invalid hexadecimal digit: " + hexString);
		}*/
		return out;
	}

	public static String hex2String(byte[] array){
		try{
			char[] cArray = new char[array.length * 2];
			for(int i = 0, j = 0; i < array.length; i++){
				int index = array[i] & 0xFF;
				cArray[j++] = FIRST_CHAR[index];
				cArray[j++] = SECOND_CHAR[index];
			}
			return new String(cArray);
		}catch(Throwable t){
			return "";
		}
	}

	public static int lastUpdatedCount(SQLiteDatabase db){
		int cnt = (int)Numeric.string2double(fromCursor(db.rawQuery(
				"select changes() as cnt;"
				, null)).child("row").child("cnt").value.property.value());
		return cnt;
	}

	public static Bough fromCursor(Cursor cursor){
		return fromCursor(cursor, false);
	}

	public static Bough fromCursor(Cursor cursor, boolean parseDate){
		Bough bough = new Bough().name.is("cursor");
		while(cursor.moveToNext()){
			Bough row = new Bough().name.is("row");
			for(int i = 0; i < cursor.getColumnCount(); i++){
				String name = cursor.getColumnName(i);
				String value = null;
				try{
					value = cursor.getString(i);
					if(parseDate){
						try{
							java.util.Date d = null;
							if(value.length() > 12){
								d = sqliteTime.parse(value);
							}else{
								d = sqliteDate.parse(value);
							}
							value = "" + d.getTime();
						}catch(Throwable t){
							//nor date nor time
						}
					}
				}catch(Throwable t){
					//can't getString due blob
					byte[] b = cursor.getBlob(i);
					value = hex2String(b);
				}
				if(value == null){
					value = "";
				}
				row.child(new Bough().name.is(name).value.is(value));
			}
			bough.child(row);
			//System.out.println(row.dumpXML());
		}
		cursor.close();
		return bough;
	}

	public static String tryReFormatDate(String date, String from, String to){
		String r = "";
		if(date.trim().length() > 0){
			SimpleDateFormat fromDate = new SimpleDateFormat(from);
			SimpleDateFormat toDate = new SimpleDateFormat(to);
			try{
				Date d = fromDate.parse(date);
				r = toDate.format(d);
			}catch(ParseException e){
				System.out.println("tryReFormatDate: " + date + ", " + from + ", " + to);
				e.printStackTrace();
				r = date;
			}
		}
		return r;
	}

	public static String tryReFormatDate3(String date, String from, String to){
		String r = "";
		if(date.trim().length() > 0){
			SimpleDateFormat fromDate = new SimpleDateFormat(from);
			fromDate.setTimeZone(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat toDate = new SimpleDateFormat(to);
			try{
				Date d = fromDate.parse(date);
				r = toDate.format(d);
			}catch(ParseException e){
				System.out.println("tryReFormatDate: " + date + ", " + from + ", " + to);
				e.printStackTrace();
				r = date;
			}
		}
		return r;
	}

	public static String ru2transliterate(String message){
		char[] abcCyr = {' '
				, 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'
				, 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
		String[] abcLat = {" "
				, "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja"
				, "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < message.length(); i++){
			for(int x = 0; x < abcCyr.length; x++){
				if(message.charAt(i) == abcCyr[x]){
					builder.append(abcLat[x]);
				}
			}
		}
		return builder.toString();
	}

	public static void file2uri(Activity aa, final java.io.File file){
		android.media.MediaScannerConnection.scanFile(aa, new String[]{file.getAbsolutePath()}, null
				, new android.media.MediaScannerConnection.OnScanCompletedListener(){
					public void onScanCompleted(String path, Uri uri){
						System.out.println("onScanCompleted: " + uri.getPath());
						System.out.println("from: " + file.getAbsolutePath());
					}
				});
	}

	public static String safeFileName(String nme){
		return //Auxiliary.ru2transliterate(
				nme.replace('.', '_')
						.replace('!', '_')
						.replace(')', '_')
						.replace('(', '_')
						.replace('\'', '_')
						.replace('\\', '_')
						.replace('/', '_')
						.replace(' ', '_')
						.replace('?', '_')
						.replace('*', '_')
						.replace(':', '_')
						.replace('\"', '_')
						.replace(',', '_')
						.replace('&', '_')
						.replace("_", "")
				//)
				;
	}

	public static String tryReFormatDateGMT(String date, String from, String to, int hours){
		String r = "";
		SimpleDateFormat fromDate = new SimpleDateFormat(from);
		SimpleDateFormat toDate = new SimpleDateFormat(to);
		try{
			Date d = fromDate.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			calendar.add(Calendar.HOUR_OF_DAY, hours);
			d = calendar.getTime();
			r = toDate.format(d);
		}catch(ParseException e){
			System.out.println("tryReFormatDate: " + date + ", " + from + ", " + to);
			e.printStackTrace();
			r = date;
		}
		return r;
	}

	public static String cursorString(Cursor cursor, String name){
		String value = "";
		try{
			value = cursor.getString(cursor.getColumnIndex(name));
			if(value == null){
				return "";
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return value;
	}

	public static double cursorDouble(Cursor cursor, String name){
		double value = 0;
		try{
			value = cursor.getDouble(cursor.getColumnIndex(name));
		}catch(Throwable t){
			t.printStackTrace();
		}
		return value;
	}

	public static String cursorDate(Cursor cursor, String name){
		String value = "";
		try{
			value = cursor.getString(cursor.getColumnIndex(name));
			if(value == null){
				return "";
			}
			if(value.length() > 9){
				java.util.Date d = sqliteDate.parse(value);
				value = "" + d.getTime();
			}else{
				value = "0";
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return value;
	}

	public static String cursorTime(Cursor cursor, String name){
		String value = "";
		try{
			value = cursor.getString(cursor.getColumnIndex(name));
			if(value == null){
				return "";
			}
			if(value.length() > 12){
				java.util.Date d = sqliteTime.parse(value);
				value = "" + d.getTime();
			}else{
				value = "0";
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return value;
	}


	public static String cursorBlob(Cursor cursor, String name){
		String value = "";
		try{
			byte[] b = cursor.getBlob(cursor.getColumnIndex(name));
			if(b == null){
				return "";
			}
			value = hex2String(b);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return value;
	}

	public static Bough fromStrictCursor(Cursor cursor, String[] strings, String[] dates, String[] times, String[] blobs){
		Bough bough = new Bough().name.is("cursor");
		while(cursor.moveToNext()){
			Bough row = new Bough().name.is("row");
			if(strings != null){
				for(int f = 0; f < strings.length; f++){
					String name = strings[f];
					row.child(new Bough().name.is(name).value.is(cursorString(cursor, name)));
				}
			}
			if(dates != null){
				for(int f = 0; f < dates.length; f++){
					String name = dates[f];
					row.child(new Bough().name.is(name).value.is(cursorDate(cursor, name)));
				}
			}
			if(times != null){
				for(int f = 0; f < times.length; f++){
					String name = times[f];
					row.child(new Bough().name.is(name).value.is(cursorTime(cursor, name)));
				}
			}
			if(blobs != null){
				for(int f = 0; f < blobs.length; f++){
					String name = blobs[f];
					row.child(new Bough().name.is(name).value.is(cursorBlob(cursor, name)));
				}
			}
			bough.child(row);
		}
		return bough;
	}

	public static String loadTextFromResource(Context context, int resourceID){
		String txt = "";
		try{
			InputStream inputStream = context.getResources().openRawResource(resourceID);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int i;
			i = inputStream.read();
			while(i != -1){
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
			txt = byteArrayOutputStream.toString();
		}catch(Throwable t){
			t.printStackTrace();
		}
		return txt;
	}

	public static String pathForMediaURItest(Context context, Uri uri){
		try{
			System.out.println("pathForMediaURI " + uri.toString());
		/*for (PackageInfo pack : context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
			ProviderInfo[] providers = pack.providers;
			if (providers != null) {
				for(int ii=0;ii<providers.length;ii++){
					System.out.println(ii+": " + providers[ii].toString());
				}
			}
		}*/
			Uri provider = Uri.parse("content://com.android.providers.media.documents/");
			System.out.println("provider " + provider);
			Cursor cursor = context.getContentResolver().query(provider, null, null, null, null);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}

	public static String pathForMediaURI(Context context, Uri uri){
		System.out.println("pathForMediaURI " + uri);
		try{
			System.out.println("Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
			System.out.println("isDocumentUri " + DocumentsContract.isDocumentUri(context, uri));
			if(Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)){
				String docId;
				String[] split;
				String type;
				System.out.println("isExternalStorageDocument " + isExternalStorageDocument(uri));
				if(isExternalStorageDocument(uri)){
					docId = DocumentsContract.getDocumentId(uri);
					split = docId.split(":");
					type = split[0];
					if("primary".equalsIgnoreCase(type)){
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}
				}else{
					if(isDownloadsDocument(uri)){
						docId = DocumentsContract.getDocumentId(uri);
						Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId).longValue());
						return getDataColumn(context, contentUri, (String)null, (String[])null);
					}
					if(isMediaDocument(uri)){
						docId = DocumentsContract.getDocumentId(uri);
						split = docId.split(":");
						type = split[0];
						Uri contentUri = null;
						System.out.println("type " + type + ", " + docId);
						if("image".equals(type)){
							contentUri = Images.Media.EXTERNAL_CONTENT_URI;
						}else{
							if("video".equals(type)){
								contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
							}else{
								if("audio".equals(type)){
									contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
								}/*else{
									contentUri = Uri.parse("content://com.android.providers.media.documents/document/"+split[1]);
								}*/
							}
						}

						String selection = "_id=?";
						String[] selectionArgs = new String[]{split[1]};
						System.out.println("contentUri " + contentUri);
						System.out.println("selection " + selection);
						System.out.println("selectionArgs " + selectionArgs[0]);

						/*for (android.content.pm.PackageInfo pack : context.getPackageManager().getInstalledPackages(android.content.pm.PackageManager.GET_PROVIDERS)) {
							android.content.pm.ProviderInfo[] providers = pack.providers;
							if (providers != null) {
								for (android.content.pm.ProviderInfo provider : providers) {
									System.out.println("provider info: " + provider.authority);
								}
							}
						}*/

						String dataColumn = getDataColumn(context, contentUri, selection, selectionArgs);
						//String path=pathFromFileURI(context,uri);
						//System.out.println("path " + path);
						return dataColumn;
					}
				}
			}else{
				if("content".equalsIgnoreCase(uri.getScheme())){
					return getDataColumn(context, uri, (String)null, (String[])null);
				}
				if("file".equalsIgnoreCase(uri.getScheme())){
					return uri.getPath();
				}
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}

	public static String pathFromFileURI(Context inContext, Uri uri){
		Cursor cursor = inContext.getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

		return cursor.getString(idx);
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs){
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};
		try{
			ContentResolver contentResolver = context.getContentResolver();
			cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

			//cursor = contentResolver.query(uri, projection, null, null, null);
			//Bough ll=Auxiliary.fromCursor(cursor);
			//System.out.println("contentResolver "+ll.dumpXML());

			if(cursor != null && cursor.moveToFirst()){
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri){
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri){
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri){
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static Bough loadTextFromPublicPOST(String pathurl, byte[] data, int timeout){
		Bough r = new Bough().name.is("result");
		try{
			HttpURLConnection httpURLConnection = null;
			URL link = new URL(pathurl);
			httpURLConnection = (HttpURLConnection)link.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.connect();
			//httpURLConnection.setRequestProperty("charset", charset);
			if(data != null){
				String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

				OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
				writer.write(base64);
				writer.close();
			}
			//System.out.println("loadTextFromPOST response: "+httpURLConnection.getResponseCode()+", "+httpURLConnection.getResponseMessage());
			r.child("code").value.is("" + httpURLConnection.getResponseCode());
			r.child("message").value.is(httpURLConnection.getResponseMessage());
			InputStream inputStream = httpURLConnection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int intgr = bufferedInputStream.read(bytes);
			while(intgr > -1){
				byteArrayOutputStream.write(bytes, 0, intgr);
				intgr = bufferedInputStream.read(bytes);
			}
			bufferedInputStream.close();
			byte[] raw = byteArrayOutputStream.toByteArray();
			r.child("raw").value.is(new String(raw));
			httpURLConnection.disconnect();
		}catch(Throwable t){
			r.child("message").value.is(r.child("message").value.property.value() + " / " + t.toString());
			t.printStackTrace();
		}
		return r;
	}

	public static Bough loadTextFromPrivatePOST(String pathurl, byte[] data, int timeout, String login, String password){
		return loadTextFromPrivatePOST(pathurl, data, timeout, login, password, false);
	}

	public static Bough loadTextFromPrivatePOST(String pathurl, byte[] data, int timeout, String login, String password, boolean noBase64){
		if(login == null || password == null){
			return loadTextFromPublicPOST(pathurl, data, timeout);
		}
		Bough r = new Bough().name.is("result");
		try{
			HttpURLConnection httpURLConnection = null;
			URL link = new URL(pathurl);
			httpURLConnection = (HttpURLConnection)link.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setChunkedStreamingMode(0);
			String userCredentials = login + ":" + password;
			String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), android.util.Base64.DEFAULT));
			httpURLConnection.setRequestProperty("Authorization", basicAuth);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.connect();
			//httpURLConnection.setRequestProperty("charset", charset);

			if(data != null){
				//System.out.println(noBase64);
				if(noBase64){
					OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
					writer.write(new String(data, "UTF-8"));
					writer.close();
				}else{
					String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
					//System.out.println(base64);
					OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
					writer.write(base64);
					writer.close();
				}
			}
			//System.out.println("loadTextFromPOST response: "+httpURLConnection.getResponseCode()+", "+httpURLConnection.getResponseMessage());
			r.child("code").value.is("" + httpURLConnection.getResponseCode());
			r.child("message").value.is(httpURLConnection.getResponseMessage());
			try{
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				int intgr = bufferedInputStream.read(bytes);
				while(intgr > -1){
					byteArrayOutputStream.write(bytes, 0, intgr);
					intgr = bufferedInputStream.read(bytes);
				}
				bufferedInputStream.close();
				byte[] raw = byteArrayOutputStream.toByteArray();
				r.child("raw").value.is(new String(raw));
				r.child("message").value.is(r.child("message").value.property.value() + " / " + new String(raw));
			}catch(Throwable t2){
				t2.printStackTrace();
				r.child("message").value.is(r.child("message").value.property.value() + " / " + t2.toString());
			}
			try{
				InputStream input = httpURLConnection.getErrorStream();
				if(input != null){
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					int nn;
					while((nn = input.read(data)) != -1){
						output.write(data, 0, nn);
					}
					input.close();

					String s2 = new String(output.toByteArray(), "UTF-8");// "windows-1251");
					r.child("message").value.is(r.child("message").value.property.value() + " / " + s2);
				}
			}catch(Throwable t3){
				t3.printStackTrace();
				r.child("message").value.is(r.child("message").value.property.value() + " / " + t3.toString());
			}
			httpURLConnection.disconnect();
		}catch(Throwable t){
			r.child("message").value.is(r.child("message").value.property.value() + " / " + t.toString());
			t.printStackTrace();
		}
		return r;
	}

	public static Bough loadTextFromPublicPOST(String pathurl, String text, int timeout, String charset){
		return loadTextFromPublicPOST(pathurl, text, timeout, charset, null, null);
	}

	public static Bough loadTextFromPublicPOST(String pathurl, String text, int timeout, String charset, String contentType, String accept){
		System.out.println("loadTextFromPOST: " + pathurl + ", " + timeout + ", " + charset + ", " + text);
		Bough rr = new Bough().name.is("result");
		try{
			HttpURLConnection httpURLConnection = null;
			URL link = new URL(pathurl);
			httpURLConnection = (HttpURLConnection)link.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			//httpURLConnection.setRequestProperty("Content-Type", "application/json");
			if(contentType != null){
				httpURLConnection.setRequestProperty("Content-Type", contentType);
			}
			//httpURLConnection.setRequestProperty("Accept","application/json");
			if(accept != null){
				httpURLConnection.setRequestProperty("Accept", accept);
			}
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setRequestProperty("charset", charset);
			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			byte[] post = text.getBytes(charset);
			bufferedOutputStream.write(post);
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			outputStream.close();
			//System.out.println("loadTextFromPOST response: "+httpURLConnection.getResponseCode()+", "+httpURLConnection.getResponseMessage());
			rr.child("code").value.is("" + httpURLConnection.getResponseCode());
			rr.child("message").value.is(httpURLConnection.getResponseMessage());
			InputStream inputStream = httpURLConnection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int intgr = bufferedInputStream.read(bytes);
			while(intgr > -1){
				byteArrayOutputStream.write(bytes, 0, intgr);
				intgr = bufferedInputStream.read(bytes);
			}
			bufferedInputStream.close();
			byte[] raw = byteArrayOutputStream.toByteArray();
			rr.child("raw").value.is(new String(raw));
		}catch(Throwable t){
			rr.child("message").value.is(rr.child("message").value.property.value() + " / " + t.toString());
			t.printStackTrace();
		}
		return rr;
	}

	public static Bough loadTextFromPrivatePOST(String pathurl, String text, int timeout, String charset, String login, String password){
		byte[] post = new byte[1];
		try{
			post = text.getBytes(charset);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return loadTextFromPrivatePOST(pathurl, post, timeout, charset, login, password);
	}

	public static Bough loadTextFromPrivatePOST(String pathurl, byte[] post, int timeout, String charset, String login, String password){
		System.out.println("loadTextFromPOST: " + pathurl + ", " + timeout + ", " + charset + ", " + login + ", " + password);
		if(login == null || password == null){
			//return loadTextFromPublicPOST(pathurl, text, timeout, charset);
			return loadTextFromPublicPOST(pathurl, new String(post), timeout, charset);
		}
		//login = "bot28";
		//password ="28bot";
		Bough r = new Bough().name.is("result");
		try{
			HttpURLConnection httpURLConnection = null;
			URL link = new URL(pathurl);
			httpURLConnection = (HttpURLConnection)link.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setRequestProperty("charset", charset);
			String userCredentials = login + ":" + password;
			String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), android.util.Base64.DEFAULT));
			httpURLConnection.setRequestProperty("Authorization", basicAuth);
			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			//byte[] post = text.getBytes(charset);
			bufferedOutputStream.write(post);
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			outputStream.close();
			//System.out.println("loadTextFromPOST response: "+httpURLConnection.getResponseCode()+", "+httpURLConnection.getResponseMessage());
			r.child("code").value.is("" + httpURLConnection.getResponseCode());
			r.child("message").value.is(httpURLConnection.getResponseMessage());
			InputStream inputStream = httpURLConnection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int intgr = bufferedInputStream.read(bytes);
			while(intgr > -1){
				byteArrayOutputStream.write(bytes, 0, intgr);
				intgr = bufferedInputStream.read(bytes);
			}
			bufferedInputStream.close();
			byte[] raw = byteArrayOutputStream.toByteArray();
			System.out.println("raw: " + (new String(raw, "windows-1251")));

			r.child("raw").value.is(new String(raw, charset));
		}catch(Throwable t){
			//t.printStackTrace();
			r.child("message").value.is(r.child("message").value.property.value() + " / " + t.toString());
		}
		return r;
	}

	public static String parseChildOrRaw(String msg, String tag){
		String txt = msg;
		try{
			Bough bb = Bough.parseJSON(msg);
			String mm = bb.child(tag).value.property.value();
			if(mm.length() > 0){
				txt = mm;
			}
		}catch(Throwable tt){
			tt.printStackTrace();
		}
		return txt;
	}

	public static byte[] loadFileFromPrivateURL(String pathurl, String login, String password) throws Exception{
		return loadFileFromPrivateURL(pathurl, login, password, "windows-1251");
	}

	public static byte[] loadFileFromPrivateURL(String pathurl, String login, String password, String encoding) throws Exception{
		if(login == null || password == null){
			return loadFileFromPublicURL(pathurl);
		}
		//login = "bot28";
		//password ="28bot";
		InputStream input = null;
		ByteArrayOutputStream output = null;
		HttpURLConnection connection = null;

		URL url = new URL(pathurl);
		System.out.println("loadFileFromPrivateURL login " + login + ", password " + password + ": " + url);
		connection = (HttpURLConnection)url.openConnection();
		String userCredentials = login + ":" + password;
		String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), android.util.Base64.DEFAULT));
		connection.setRequestProperty("Authorization", basicAuth);
		connection.setUseCaches(false);
		//connection.setRequestProperty("charset", "UTF-8");
		connection.connect();
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
			throw new Exception("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage() + " for " + url.toString());
		}
		byte data[] = new byte[1024];
		int nn;

		input = connection.getInputStream();
		output = new ByteArrayOutputStream();
		while((nn = input.read(data)) != -1){
			output.write(data, 0, nn);
		}
		input.close();
		//String s = new String(output.toByteArray(), "windows-1251");
		String s = new String(output.toByteArray(), encoding);

		System.out.println("loadFileFromPrivateURL " + connection.getResponseCode() + ": " + s);

		input = connection.getErrorStream();
		if(input != null){
			output = new ByteArrayOutputStream();
			while((nn = input.read(data)) != -1){
				output.write(data, 0, nn);
			}
			input.close();

			String s2 = new String(output.toByteArray(), encoding);
			if(s2.length() > 0){
				s = s + "\n" + s2;
			}
		}

		//System.out.println("loadFileFromPrivateURL " + connection.getResponseCode() + ": " + s);

		return output.toByteArray();
	}


	public static Bough checkPrivateURL(String pathurl, String login, String password) throws Exception{
		String errorText = "";
		String responseText = "";
		String exception = "";
		int responseStatus = 0;
		//String result = "";
		Bough result = new Bough();
		HttpURLConnection connection = null;
		URL url = new URL(pathurl);
		connection = (HttpURLConnection)url.openConnection();
		String userCredentials = login + ":" + password;
		String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), android.util.Base64.DEFAULT));
		connection.setRequestProperty("Authorization", basicAuth);
		connection.setUseCaches(false);
		connection.connect();
		responseStatus = connection.getResponseCode();
		//System.out.println("checkPrivateURL responseStatus " + responseStatus);
		try{
			byte data[] = new byte[1024];
			int nn;
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while((nn = input.read(data)) != -1){
				output.write(data, 0, nn);
			}
			input.close();
			responseText = new String(output.toByteArray(), "UTF-8");
			//System.out.println("checkPrivateURL responseText " + responseText);
		}catch(Throwable t){
			t.printStackTrace();
			exception = exception + "\n" + t.getMessage();
		}
		try{
			byte data[] = new byte[1024];
			int nn;
			InputStream input = connection.getErrorStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			if(input != null){
				while((nn = input.read(data)) != -1){
					output.write(data, 0, nn);
				}
				input.close();
				errorText = new String(output.toByteArray(), "UTF-8");
				//System.out.println("checkPrivateURL errorText " + errorText);
			}
		}catch(Throwable t){
			t.printStackTrace();
			exception = exception + "\n" + t.getMessage();
		}
		//if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
		//	result = "" + responseStatus + "\n" + errorText + "\n" + responseText;
		//}
		result.child("status").value.is("" + responseStatus);
		result.child("error").value.is(errorText);
		result.child("data").value.is(responseText);
		result.child("exception").value.is(exception);
		return result;
	}

	public static byte[] loadFileFromPublicURL(String pathurl) throws Exception{
		InputStream input = null;
		ByteArrayOutputStream output = null;
		HttpURLConnection connection = null;
		URL url = new URL(pathurl);
		//System.out.println("loadFileFromPublicURL " + url);
		connection = (HttpURLConnection)url.openConnection();
		connection.connect();
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
			throw new Exception("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
		}
		input = connection.getInputStream();
		output = new ByteArrayOutputStream();
		byte data[] = new byte[1024];
		int nn;
		while((nn = input.read(data)) != -1){
			output.write(data, 0, nn);
		}
		input.close();
		return output.toByteArray();
	}

	public static Bitmap loadBitmapFromPublicURL(String url){
		Bitmap bitmap = null;
		/*URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;*/
		try{
			/*m = new URL(url);
			i = (InputStream) m.getContent();
			bis = new BufferedInputStream(i, 1024 * 8);
			out = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bis.close();
			byte[] data = out.toByteArray();*/
			byte[] data = loadFileFromPublicURL(url);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}catch(Throwable t){
			t.printStackTrace();
			//System.err.println(t.getMessage());
		}
		return bitmap;
	}

	public static void inform(String s, Context context){
		//System.out.println("inform: " + s);
		Toast.makeText(context, s, Toast.LENGTH_LONG).show();
	}

	/*public static void warn(String s, Context context) {
		warnText(s+"\n\n"
					  +"При возникновении проблем обращайтесь в тех.поддержку по тел. 0775",context);
	}*/
	public static void warn(String s, Context context){
		System.out.println("warn: " + s);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(s);
		builder.create().show();
	}

	public static void bye(String message, final Activity activity){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setMessage(message);
		dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener(){
			public void onDismiss(DialogInterface dialog){
				activity.finish();
			}
		});
		dialogBuilder.create().show();
	}

	public static void pickText(Context context//
			, String title//
			, final Note text//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final EditText input = new EditText(context);
		input.setSingleLine(false);
		input.setMinLines(3);
		input.setGravity(android.view.Gravity.LEFT | android.view.Gravity.TOP);
		input.setText(text.value());
		//input.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
		builder.setView(input);
		builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				text.value("" + input.getText());
				if(callbackPositiveBtn != null){
					callbackPositiveBtn.start();
				}
			}
		});
		builder.create().show();
	}

	public static long pickDate(Context context//
			, final Numeric date){
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(date.value().longValue());
		if(date.value() == 0){
			c.setTimeInMillis(new Date().getTime());
		}
		new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener(){
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
				Calendar newCalendar = Calendar.getInstance();
				newCalendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				newCalendar.setTimeInMillis(date.value().longValue());
				newCalendar.set(Calendar.YEAR, year);
				newCalendar.set(Calendar.MONTH, monthOfYear);
				newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				date.value((double)newCalendar.getTimeInMillis());
			}
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
		return 0;
	}

	public static void pickString(Context context//
			, String title//
			, final Note text//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn
	){
		pickString(context, title, text, positiveButtonTitle, callbackPositiveBtn, null, null, null, null);
	}

	public static void pickString(Context context//
			, String title//
			, final Note text//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
			, String negativeButtonTitle//
			, final Task callbackNegativeBtn//

	){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final EditText input = new EditText(context);
		input.setText(text.value());
		input.setSingleLine(true);
		//input.setEllipsize(TextUtils.TruncateAt.END);
		//input.setInputType(EditorInfo.type_text_fl.TYPE_TEXT_FLAG_MULTI_LINE);
		builder.setView(input);
		builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				text.value("" + input.getText());
				if(callbackPositiveBtn != null){
					callbackPositiveBtn.start();
				}
			}
		});
		if(callbackNeutralBtn != null){
			builder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					text.value("" + input.getText());
					callbackNeutralBtn.start();
				}
			});
		}
		if(callbackNegativeBtn != null){
			builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					text.value("" + input.getText());
					callbackNegativeBtn.start();
				}
			});
		}
		builder.create().show();
	}

	public static void pickNumber(Context context//
			, String title//
			, final Numeric num//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
	){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		input.setText("" + num.value());
		builder.setView(input);
		if(callbackPositiveBtn != null){
			builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					double nn = num.value();
					try{
						nn = Double.parseDouble(input.getText().toString());
					}catch(Throwable t){
						t.printStackTrace();
					}
					num.value(nn);
					callbackPositiveBtn.start();
				}
			});
		}
		if(callbackNeutralBtn != null){
			builder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					callbackNeutralBtn.start();
				}
			});
		}
		builder.create().show();
	}

	public static String formatNonEmptyNumber(String num, String name){
		String rr = "";
		if(num.trim().length() > 0){
			if(!num.trim().equals("0")){
				rr = num + name;
			}
		}
		return rr;
	}

	public static void pickFilteredChoice(Context context, final String[] items, final Numeric selection, final Task callbackSelect){
		pickFilteredChoice(context, items, selection, null, callbackSelect, null, null, null, null, null, null);
	}

	public static void pickFilteredChoice(Context context, final String[] items, final Numeric selection, String title, final Task callbackSelect
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
			, String negativeButtonTitle//
			, final Task callbackNegativeBtn//
	){
		final DataGrid grid = new DataGrid(context);
		final ColumnText lines = new ColumnText();
		final Note filter = new Note();
		//Numeric dialogWidth = new Numeric();
		final AlertDialog alertDialog = Auxiliary.pick(context
				, title
				, new SubLayoutless(context)//
						.child(new RedactText(context).text.is(filter)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 14)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(grid.noHead.is(true).columns(new Column[]{
										lines.title.is("data").width.is(Auxiliary.tapSize * 14)
								})
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.0)
								.width().is(Auxiliary.tapSize * 14)
								.height().is(Auxiliary.tapSize * 6.5))//
						.width().is(Auxiliary.tapSize * 15)//
						.height().is(Auxiliary.tapSize * 10)//
				, positiveButtonTitle, callbackPositiveBtn//
				, neutralButtonTitle, callbackNeutralBtn//
				, negativeButtonTitle, callbackNegativeBtn
		);

		final Task refreshList = new Task(){
			public void doTask(){
				grid.clearColumns();
				for(int i = 0; i < items.length; i++){
					if(items[i].toUpperCase().indexOf(filter.value().trim().toUpperCase()) > -1){
						final int nn = i;
						Task rowtap = new Task(){
							public void doTask(){
								selection.value(nn);
								alertDialog.dismiss();
								callbackSelect.doTask();
							}
						};
						lines.cell(items[i], rowtap);
					}
				}
				grid.refresh();
			}
		};
		filter.afterChange(new Task(){
			@Override
			public void doTask(){
				refreshList.doTask();
			}
		});
		refreshList.doTask();
	}

	public static void pickSingleChoice(Context context, CharSequence[] items, final Numeric defaultSelection){
		pickSingleChoice(context, items, defaultSelection, null, null, null, null, null, null);
	}

	public static AlertDialog pickSingleChoice(Context context, CharSequence[] items, final Numeric defaultSelection//
			, String title//
			, final Task afterSelect//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
	){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		int nn = -1;
		if(defaultSelection != null){
			nn = defaultSelection.value().intValue();
		}
		if(title != null){
			builder.setTitle(title);
		}
		builder.setSingleChoiceItems(items, nn, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(defaultSelection != null){
					defaultSelection.value(which);
				}
				if(afterSelect != null){
					afterSelect.start();
				}
			}
		});
		if(callbackPositiveBtn != null){
			builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					callbackPositiveBtn.start();
				}
			});
		}
		if(callbackNeutralBtn != null){
			builder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					callbackNeutralBtn.start();
				}
			});
		}
		AlertDialog r = builder.create();
		r.show();
		return r;
		//builder.create().show();
	}

	public static void refreshFilteredMultiChoice(DataGrid grid, Vector<CheckRow> rows, Note filter
			, ColumnText lines, ColumnBitmap up, ColumnBitmap down
			, Task onState
	){
		if(grid != null){
			//System.out.println("filter " + filter.property.value());
			grid.clearColumns();
			//String ss = "";
			for(int i = 0; i < rows.size(); i++){
				if(rows.get(i).txt.toUpperCase().indexOf(filter.value().trim().toUpperCase()) > -1){
					if(rows.get(i).checked){
						final int nn = i;
						Task rowtap = new Task(){
							public void doTask(){
								rows.get(nn).checked = false;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
							}
						};
						lines.cell("✔ " + rows.get(i).txt, rowtap);
						Task tapUp = new Task(){
							public void doTask(){
								onState.doTask2(rows.get(nn).id, "1");
								int yy = grid.scrollView.getScrollY();
								rows.get(nn).state3 = 1;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
								grid.scrollView.scrollTo(0, yy);
							}
						};
						Task tapDown = new Task(){
							public void doTask(){
								onState.doTask2(rows.get(nn).id, "0");
								int yy = grid.scrollView.getScrollY();
								rows.get(nn).state3 = 2;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
								grid.scrollView.scrollTo(0, yy);
							}
						};
						if(rows.get(nn).state3 == 1){
							up.cell(Auxiliary.bmLike, tapUp);
						}else{
							up.cell(Auxiliary.bmLikeOff, tapUp);
						}
						if(rows.get(nn).state3 == 2){
							down.cell(Auxiliary.bmDislike, tapDown);
						}else{
							down.cell(Auxiliary.bmDislikeOff, tapDown);
						}
					}
				}
			}
			//setText(ss);
			for(int i = 0; i < rows.size(); i++){
				if(rows.get(i).txt.toUpperCase().indexOf(filter.value().trim().toUpperCase()) > -1){
					if(!rows.get(i).checked){
						final int nn = i;
						Task rowtap = new Task(){
							public void doTask(){
								rows.get(nn).checked = true;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
							}
						};
						lines.cell("" + rows.get(i).txt, rowtap);
						Task tapUp = new Task(){
							public void doTask(){
								onState.doTask2(rows.get(nn).id, "1");
								int yy = grid.scrollView.getScrollY();
								rows.get(nn).state3 = 1;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
								grid.scrollView.scrollTo(0, yy);
							}
						};
						Task tapDown = new Task(){
							public void doTask(){
								onState.doTask2(rows.get(nn).id, "0");
								int yy = grid.scrollView.getScrollY();
								rows.get(nn).state3 = 2;
								refreshFilteredMultiChoice(grid, rows, filter, lines, up, down, onState);
								grid.scrollView.scrollTo(0, yy);
							}
						};
						if(rows.get(nn).state3 == 1){
							up.cell(Auxiliary.bmLike, tapUp);
						}else{
							up.cell(Auxiliary.bmLikeOff, tapUp);
						}
						if(rows.get(nn).state3 == 2){
							down.cell(Auxiliary.bmDislike, tapDown);
						}else{
							down.cell(Auxiliary.bmDislikeOff, tapDown);
						}
					}
				}
			}
			grid.refresh();


		}
		//setTextLabel();

	}

	static Bitmap bmLike = null;
	static Bitmap bmLikeOff = null;
	static Bitmap bmDislike = null;
	static Bitmap bmDislikeOff = null;

	public static void pickFilteredMultiChoice(Context context, Vector<CheckRow> rows//
			, String positiveButtonTitle, final Task callbackPositiveBtn
			, String neutralButtonTitle, final Task callBackNeutralBtn
			, String negativeButtonTitle, final Task callbackNegativeBtn
			, String title
			, Task onState
	){
		if(bmLike == null){
			bmLike = Auxiliary.scaledBitmapFromResource(context, R.drawable.like, Auxiliary.tapSize / 2, Auxiliary.tapSize / 2);
			bmLikeOff = Auxiliary.scaledBitmapFromResource(context, R.drawable.likeoff, Auxiliary.tapSize / 2, Auxiliary.tapSize / 2);
			bmDislike = Auxiliary.scaledBitmapFromResource(context, R.drawable.dislike, Auxiliary.tapSize / 2, Auxiliary.tapSize / 2);
			bmDislikeOff = Auxiliary.scaledBitmapFromResource(context, R.drawable.dislikeoff, Auxiliary.tapSize / 2, Auxiliary.tapSize / 2);
		}
		DataGrid grid = new DataGrid(context);
		ColumnText lines = new ColumnText();
		ColumnBitmap stateUp = new ColumnBitmap();
		ColumnBitmap stateDown = new ColumnBitmap();
		Note filter = new Note();
		AlertDialog alertDialog = Auxiliary.pick(context, title, new SubLayoutless(context)//
						.child(new RedactText(context).text.is(filter)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 10)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(grid.noHead.is(true).columns(
										new Column[]{
												lines.title.is("data").width.is(Auxiliary.tapSize * 8.5)
												, stateUp.noVerticalBorder.is(true).title.is("up").width.is(Auxiliary.tapSize * 0.5)
												, stateDown.noVerticalBorder.is(true).title.is("down").width.is(Auxiliary.tapSize * 1)
										})
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.0)
								.width().is(Auxiliary.tapSize * 10)
								.height().is(Auxiliary.tapSize * 8))//
						.width().is(Auxiliary.tapSize * 11)//
						.height().is(Auxiliary.tapSize * 9)//
				, positiveButtonTitle, callbackPositiveBtn
				, negativeButtonTitle, callbackNegativeBtn
				, neutralButtonTitle, callBackNeutralBtn);
		//refreshFilteredMultiChoice(grid, rows, filter, lines);
		filter.afterChange(new Task(){
			@Override
			public void doTask(){


				refreshFilteredMultiChoice(grid, rows, filter, lines, stateUp, stateDown, onState);
			}
		});
	}

	public static void pickMultiChoice(Context context, CharSequence[] items, final They<Integer> defaultSelection//
	){
		pickMultiChoice(context, items, defaultSelection, null, null, null, null, null);
	}

	public static void pickMultiChoice(Context context, CharSequence[] items, final They<Integer> defaultSelection//
			, String positiveButtonTitle, final Task callbackPositiveBtn
	){
		pickMultiChoice(context, items, defaultSelection, positiveButtonTitle, callbackPositiveBtn, null, null, null);
	}

	public static void pickMultiChoice(Context context, CharSequence[] items, final They<Integer> defaultSelection//
			, String positiveButtonTitle, final Task callbackPositiveBtn
			, String negativeButtonTitle, final Task callbackNegativeBtn
			, String title
	){
		if(items.length > 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			boolean[] checks = new boolean[items.length];
			for(int i = 0; i < defaultSelection.size(); i++){
				int n = defaultSelection.at(i);
				if(n >= 0 && n < checks.length){
					checks[n] = true;
				}
			}
			if(title != null)
				builder.setTitle(title);
			builder.setMultiChoiceItems(items, checks, new DialogInterface.OnMultiChoiceClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked){
					if(isChecked){
						for(int i = 0; i < defaultSelection.size(); i++){
							int n = defaultSelection.at(i);
							if(n == which){
								return;
							}
						}
						//System.out.println("insert "+which);
						defaultSelection.insert(0, which);
					}else{
						for(int i = 0; i < defaultSelection.size(); i++){
							int n = defaultSelection.at(i);
							if(n == which){
								//System.out.println("drop "+n+" at "+i);
								defaultSelection.delete(defaultSelection.at(i));
								return;
							}
						}
					}
				}
			});
			if(callbackPositiveBtn != null){
				builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
						if(callbackPositiveBtn != null){
							callbackPositiveBtn.start();
						}
					}
				});
			}
			if(callbackNegativeBtn != null){
				builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
						if(callbackNegativeBtn != null){
							callbackNegativeBtn.start();
						}
					}
				});
			}
			builder.create().show();
		}
	}

	public static boolean isNumeric(String strNum){
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("-?\\d+(\\.\\d+)?");
		if(strNum == null){
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	public static void pickConfirm(Context context//
			, String message//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
	){
		pick3Choice(context, null, message, positiveButtonTitle, callbackPositiveBtn, null, null, null, null);
	}

	public static void alertBreak(String s, Context context){
		//System.out.println("warn: " + s);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(s);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public static void pick3Choice(Context context//
			, String title//
			, String message//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
			, String negativeButtonTitle//
			, final Task callbackNegativeBtn//
	){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		if(title != null){
			dialogBuilder.setTitle(title);
		}
		if(message != null){
			dialogBuilder.setMessage(message);
		}
		dialogBuilder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(callbackNegativeBtn != null){
					callbackNegativeBtn.start();
				}
			}
		});
		dialogBuilder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(callbackNeutralBtn != null){
					callbackNeutralBtn.start();
				}
			}
		});
		dialogBuilder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(callbackPositiveBtn != null){
					callbackPositiveBtn.start();
				}
			}
		});
		dialogBuilder.create().show();
	}

	public static void pickDate(Context context, final java.util.Calendar current, final Numeric result, final Task onSet){
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(current.getTimeInMillis());
		if(Math.abs(current.getTimeInMillis()) < 10000){
			c.setTimeInMillis(new Date().getTime());
		}
		new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener(){
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
				Calendar newCalendar = Calendar.getInstance();
				newCalendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				newCalendar.setTimeInMillis(current.getTimeInMillis());
				newCalendar.set(Calendar.YEAR, year);
				newCalendar.set(Calendar.MONTH, monthOfYear);
				newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				System.out.println(current);
				System.out.println(newCalendar);
				result.value((double)newCalendar.getTimeInMillis());
				onSet.start();
			}
		}//
				, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))//
				.show();
		//Auxiliary.loadFileFromPrivateURL()
	}

	public static AlertDialog pick(Context context//
			, String title//
			, final Rake rake//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
			, String negativeButtonTitle//
			, final Task callbackNegativeBtn//
	){
		return pickOrCancel(context, title, rake, positiveButtonTitle, callbackPositiveBtn, neutralButtonTitle, callbackNeutralBtn, negativeButtonTitle, callbackNegativeBtn, null);
	}

	public static AlertDialog pickOrCancel(Context context//
			, String title//
			, final Rake rake//
			, String positiveButtonTitle//
			, final Task callbackPositiveBtn//
			, String neutralButtonTitle//
			, final Task callbackNeutralBtn//
			, String negativeButtonTitle//
			, final Task callbackNegativeBtn//
			, Task onCancel
	){
		//final Vector<Task>dumbGoogle=new  Vector<Task>();
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		final RelativeLayout rl = new RelativeLayout(context);
		dialogBuilder.setView(rl);

		//final View toRemove=null;
		if(title != null){
			dialogBuilder.setTitle(title);
		}
		if(rake != null){
			if(rake.view() != null){
				//toRemove=rake.view();
				//System.out.println("min r1 "+rake.width().property.value().intValue()+"x"+ rake.height().property.value().intValue());
				rl.setMinimumWidth(rake.width().property.value().intValue());
				rl.setMinimumHeight(rake.height().property.value().intValue());
				//dialogBuilder.setView(rake.view());
				//dialogBuilder.setView(null);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(//
						rake.width().property.value().intValue()//
						, rake.height().property.value().intValue());
				//params.leftMargin = (int) (left.property.value() + dragX.property.value());
				//params.topMargin = (int) (top.property.value() + dragY.property.value());
				rake.view().setLayoutParams(params);
				rl.addView(rake.view());
			}
		}
		dialogBuilder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				//dumbGoogle.get(0).start();
				if(rake != null){
					rl.removeView(rake.view());
				}
				dialog.dismiss();
				if(callbackNegativeBtn != null){
					callbackNegativeBtn.start();
				}
			}
		});
		dialogBuilder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				//dumbGoogle.get(0).start();
				if(rake != null){
					rl.removeView(rake.view());
				}
				dialog.dismiss();
				if(callbackNeutralBtn != null){
					callbackNeutralBtn.start();
				}
			}
		});
		dialogBuilder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				//dumbGoogle.get(0).start();
				if(rake != null){
					rl.removeView(rake.view());
				}
				dialog.dismiss();
				if(callbackPositiveBtn != null){
					callbackPositiveBtn.start();
				}
			}
		});
		dialogBuilder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog){
				//dumbGoogle.get(0).start();
				if(rake != null){
					rl.removeView(rake.view());
				}
				if(onCancel != null){
					onCancel.doTask();
				}
			}
		});
		AlertDialog d = dialogBuilder.create();
		d.show();
		if(rake != null){
			if(rake.view() != null){
				d.getWindow().setLayout(rake.width().property.value().intValue(), rake.height().property.value().intValue());
				//System.out.println("pick "+rake.width().property.value().intValue()+"x"+ rake.height().property.value().intValue());
			}
		}

		return d;
	}

	public static String strings2text(Vector<String> s){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.size(); i++){
			sb.append(s.get(i));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static Vector<String> readTextFromFile(File file){
		return readTextFromFile(file, "UTF-8");
	}

	public static Vector<String> x_readTextFromFile(Context context, String fileName, String encoding){
		File filesDir = context.getFilesDir();
		File file = new File(filesDir, fileName);
		Vector<String> result = new Vector<String>();
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
			BufferedReader input = new BufferedReader(inputStreamReader);
			String line = null;

			while((line = input.readLine()) != null){
				System.out.println("= " + line);
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return result;
	}

	//public static int requestPermissionsCode = 873737273;


	public static Vector<String> readTextFromFile(File file, String encoding){
		//System.out.println("readTextFromFile " + file.getAbsolutePath());
		Vector<String> result = new Vector<String>();
		try{
			//BufferedReader input = new BufferedReader(new FileReader(aFile));
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
			BufferedReader input = new BufferedReader(inputStreamReader);
			//BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			try{
				String line = null;
				while((line = input.readLine()) != null){
					result.add(line);
					//contents.append(System.getProperty("line.separator"));
				}
			}finally{
				input.close();
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return result;
	}

	public static boolean writeTextToFile(File aFile, String aContents){
		try{
			Writer output = new BufferedWriter(new FileWriter(aFile));
			output.write(aContents);
			output.flush();
			output.close();
		}catch(Throwable t){
			System.out.println(t.getMessage());
		}
		return false;
	}

	public static boolean writeTextToFile(File aFile, String aContents, String charset){
		try{
			FileOutputStream fos = new FileOutputStream(aFile);
			fos.write(aContents.getBytes(charset));
			fos.flush();
			fos.close();
		}catch(Throwable t){
			//System.out.println(t.getMessage());
		}
		return false;
	}

	public static String getNetworkType(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork != null){
			return activeNetwork.getTypeName();
		}else{
			return null;
		}
	}

	public static boolean isNetworkConnected(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork == null){
			return false;
		}else{
			if(activeNetwork.isConnectedOrConnecting()){
				return true;
			}else{
				return false;
			}
		}
	}

	public static long checkAccessToURL(URL url, Context ctx){
		long time = -1;
		try{
			long start = System.currentTimeMillis();
			String hostAddress = InetAddress.getByName(url.getHost()).getHostAddress();
			long dnsResolved = System.currentTimeMillis();
			Socket socket = new Socket(hostAddress, url.getPort());
			socket.close();
			long probeFinish = System.currentTimeMillis();
			time = probeFinish - start;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return time;
	}

	/*
		public static void pick(Context context, CharSequence[] items, final Numeric defaultSelection) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (defaultSelection != null) {
						defaultSelection.value(which);
					}
				}
			});
			builder.create().show();
		}*/
	public static void startFile(final Activity activity, File file){
		final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl("file://" + file.getAbsolutePath()));
		final Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(android.net.Uri.fromFile(file), mime);
		//Intent chooser = Intent.createChooser(intent, file.getAbsolutePath());
		//activity.startActivity(chooser);
		MediaScannerConnection.scanFile(activity, new String[]{file.getAbsolutePath()}, null
				, new MediaScannerConnection.OnScanCompletedListener(){
					public void onScanCompleted(String path, Uri uri){
						intent.putExtra(Intent.EXTRA_STREAM, uri);
						intent.setDataAndType(uri, mime);//"text/html"
						activity.startActivity(intent);
					}
				});
	}
	/*public static DevicePolicyManager getDpm(Context context) {
		return (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
	}

	public static ComponentName getAdmin(Context context) {
		return new ComponentName(context, MyDevicePolicyReceiver.class);
	}

	public static void addMyRestrictions(Context context) {
		getDpm(context).addUserRestriction(getAdmin(context), UserManager.DISALLOW_INSTALL_APPS);
		getDpm(context).addUserRestriction(getAdmin(context), UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
	}

	public static void clearMyRestrictions(Context context) {
		getDpm(context).clearUserRestriction(getAdmin(context), UserManager.DISALLOW_INSTALL_APPS);
		getDpm(context).clearUserRestriction(getAdmin(context), UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
	}*/

	public static void startFile(final Activity activity, String action, final String mime, File file){
		//String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl("file://" + path));
		final Intent intent = new Intent();
		intent.setAction(action);//android.content.Intent.ACTION_VIEW
		//intent.setDataAndType(android.net.Uri.fromFile(file), mime);//"text/html"
		//startActivity(intent);
		//Intent chooser = Intent.createChooser(intent, file.getAbsolutePath());
		//activity.startActivity(chooser);

		MediaScannerConnection.scanFile(activity, new String[]{file.getAbsolutePath()}, null
				, new MediaScannerConnection.OnScanCompletedListener(){
					public void onScanCompleted(String path, Uri uri){
						intent.putExtra(Intent.EXTRA_STREAM, uri);
						intent.setDataAndType(uri, mime);//"text/html"
						activity.startActivity(intent);
					}
				});

	}

	public static int screenWidth(Activity activity){
		int w = 0;
		try{
			android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			w = dm.widthPixels;
		}catch(Throwable t){
			t.printStackTrace();
		}
		return w;
	}

	public static Bitmap screenshot(View v){
		v.setDrawingCacheEnabled(true);
		return v.getDrawingCache();
	}

	public static int screenHeight(Activity activity){
		int h = 0;
		try{
			android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			h = dm.heightPixels;
		}catch(Throwable t){
			t.printStackTrace();
		}
		return h;
	}

	public static Bitmap scaledBitmapFromResource(Context context, int id, int width, int height){
		Bitmap b = null;
		try{
			b = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), id), width, height, true);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return b;
	}

	public static Bitmap bitmapFromResource(Context context, int id){
		Bitmap b = null;
		try{
			b = BitmapFactory.decodeResource(context.getResources(), id);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return b;
	}

	public static void createAbsolutePathForFolder(String path){
		String s = File.separator;
		String[] names = path.split(File.separator);
		String r = names[0];
		for(int i = 1; i < names.length; i++){
			r = r + File.separator + names[i];
			new File(r).mkdirs();
		}
	}

	public static void createAbsolutePathForFile(String path){
		//String s = File.separator;
		String[] names = path.split(File.separator);
		String r = names[0];
		for(int i = 1; i < names.length - 1; i++){
			r = r + File.separator + names[i];
			//System.out.println("mkdirs " + r);
			new File(r).mkdirs();
		}
	}

	public static void exportResource(Context context, String path, int id){
		if(!(new File(path)).exists()){
			try{
				//System.out.println("exportResource write " + path);
				/*if (!(new File(path)).exists()) {
				System.out.println(	path+": "+new File(path+name).mkdirs());
				}*/
				byte[] buffer = null;
				InputStream fIn = context.getResources().openRawResource(id);
				int size = 0;
				size = fIn.available();
				buffer = new byte[size];
				fIn.read(buffer);
				fIn.close();
				FileOutputStream save;
				save = new FileOutputStream(path);
				save.write(buffer);
				save.flush();
				save.close();
			}catch(Throwable t){
				t.printStackTrace();
			}
		}else{
			//System.out.println("exportResource skip " + path);
		}
	}

	public static java.util.Date date(String mills){
		if(mills == null){
			return null;
		}
		if(mills.length() == 0){
			return null;
		}
		java.util.Date d = new java.util.Date();
		d.setTime((long)Numeric.string2double(mills));
		return d;
	}

	public static boolean startSensorEventListener(Activity activity, final Task task){
		try{
			SensorManager sensorManager = (SensorManager)activity.getSystemService(android.content.Context.SENSOR_SERVICE);
			sensorEventListener = new SensorEventListener(){
				@Override
				public void onSensorChanged(SensorEvent event){
					//System.out.println("Auxiliary.startSensorEventListener.onSensorChanged " + event);
					try{
						//accelerometerAverageX=0.5*(accelerometerAverageX+event.values[0]);
						//accelerometerAverageY=0.5*(accelerometerAverageX+event.values[1]);
						//accelerometerAverageZ=0.5*(accelerometerAverageX+event.values[2]);
						if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
							if(Math.abs(accelerometerX - event.values[0]) > accelerometerNoise//
									|| Math.abs(accelerometerY - event.values[1]) > accelerometerNoise//
									|| Math.abs(accelerometerZ - event.values[2]) > accelerometerNoise//
							){
								accelerometerX = event.values[0];
								accelerometerY = event.values[1];
								accelerometerZ = event.values[2];
								//System.out.println("average "+Auxiliary.accelerometerX+" x "+Auxiliary.accelerometerY+" x "+Auxiliary.accelerometerZ);
								//System.out.println("now "+Auxiliary.accelerometerAverageX+" x "+Auxiliary.accelerometerAverageY+" x "+Auxiliary.accelerometerAverageZ);
								task.start();
							}
						}
					}catch(Throwable t){
						t.printStackTrace();
					}
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy){
					//System.out.println("Auxiliary.startSensorEventListener.onAccuracyChanged " + accuracy + ": " + sensor);
				}
			};
			sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			return true;
		}catch(Throwable t){
			t.printStackTrace();
		}
		sensorEventListener = null;
		return false;
	}

	public static boolean stopSensorEventListener(Activity activity){
		if(sensorEventListener == null){
			return true;
		}
		try{
			SensorManager sensorManager = (SensorManager)activity.getSystemService(android.content.Context.SENSOR_SERVICE);
			sensorManager.unregisterListener(sensorEventListener);
			return true;
		}catch(Throwable t){
			t.printStackTrace();
		}
		return false;
	}

	public static Bough activityExatras(Activity activity){
		Intent intent = activity.getIntent();
		Bundle extras = intent.getExtras();
		return bundle2bough(extras);
	}

	public static Bough bundle2bough(Bundle bundle){
		Bough bough = new Bough();
		if(bundle == null){
			bough.name.is("null");
		}else{
			bough.name.is("extra");
			for(String key: bundle.keySet()){
				String value = "" + bundle.get(key);
				//System.out.println(key + ": " + value);
				bough.child(key).value.is(value);
			}
		}
		return bough;
	}

	public static Uri saveBitmap(Context inContext, Bitmap inImage){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public static void sendNotification(String messageTitle, String messageBody, Context packageContext, Class<?> cls){
		System.out.println("------------------sendNotification: " + messageBody);
		//Intent intent = new Intent(this, MainActivity.class);
		Intent intent = new Intent(packageContext, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(packageContext, 0 // Request code
				, intent,
				PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

		String channelId = "swtestchan";//getString(R.string.default_notification_channel_id);
		Uri defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
		androidx.core.app.NotificationCompat.Builder notificationBuilder =
				new androidx.core.app.NotificationCompat.Builder(packageContext, channelId)
						//.setSmallIcon(R.drawable.ic_stat_ic_notification)
						.setSmallIcon(sweetlife.android10.R.drawable.svlogoshadow)
						//.setContentTitle("Новое сообщение")//getString(R.string.fcm_message))
						.setContentTitle(messageTitle)
						.setContentText(messageBody)
						.setAutoCancel(true)
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent);

		android.app.NotificationManager notificationManager =
				(android.app.NotificationManager)packageContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId,
					"Channel human readable title",
					android.app.NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0 // ID of notification
				, notificationBuilder.build());

	}

	public static File createImageFile() throws IOException{
		String albumName = "Horeca3";
		String dcim = "/dcim/";
		File storageDir = null;
		File getAlbumStorageDir = new File(Environment.getExternalStorageDirectory() + dcim + albumName);
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			storageDir = getAlbumStorageDir;
			if(storageDir != null){
				if(!storageDir.mkdirs()){
					if(!storageDir.exists()){

						//

					}
				}
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String imageFileName = "photo" + timeStamp + "_";
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		return image;
	}

	public static java.io.File startCamera(Activity aa, final int resultID){
		//https://www.howtodoandroid.com/capture-image-android/
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		try{
			java.io.File imFile = Auxiliary.createImageFile();
			System.out.println("camera file " + imFile.getAbsolutePath());
			//Uri ff = Uri.fromFile(imFile);
			//System.out.println("Uri " + ff.toString());
			Uri uri = androidx.core.content.FileProvider.getUriForFile(aa, "sweetlife.android10.fileprovider", imFile);
			System.out.println("Uri2 " + uri.toString());
			//takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, ff);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			aa.startActivityForResult(takePictureIntent, resultID);
			return imFile;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}


	}

	public static void startMediaGallery(Activity aa, final int resultID){
		System.out.println("startMediaGallery "+resultID);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		try{
			Intent ch = Intent.createChooser(intent, "Выбор");
			aa.startActivityForResult(ch, resultID);
		}catch(android.content.ActivityNotFoundException ex){
			ex.printStackTrace();
		}
	}
	/*public static String pathForMediaURI(final Context context, final Uri uri) {
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			else {
				if (isDownloadsDocument(uri)) {
					String id = DocumentsContract.getDocumentId(uri);
					Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
					return getDataColumn(context, contentUri, null, null);
				}
				else {
					if (isMediaDocument(uri)) {
						String docId = DocumentsContract.getDocumentId(uri);
						String[] split = docId.split(":");
						String type = split[0];
						Uri contentUri = null;
						if ("image".equals(type)) {
							contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
						}
						else {
							if ("video".equals(type)) {
								contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
							}
							else {
								if ("audio".equals(type)) {
									contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
								}
							}
						}
						String selection = "_id=?";
						String[] selectionArgs = new String[] { split[1] };
						return getDataColumn(context, contentUri, selection, selectionArgs);
					}
				}
			}
		}
		else {
			if ("content".equalsIgnoreCase(uri.getScheme())) {
				return getDataColumn(context, uri, null, null);
			}
			else {
				if ("file".equalsIgnoreCase(uri.getScheme())) {
					return uri.getPath();
				}
			}
		}
		return null;
	}*/

}
