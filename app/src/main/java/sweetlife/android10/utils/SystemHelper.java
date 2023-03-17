package sweetlife.android10.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;

import java.io.*;
import java.util.*;

public class SystemHelper {

	public static String getDiviceID(Context context) {
		String deviceID = "None";
		try {
			TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//System.out.println("tManager.getDeviceId() "+tManager.getDeviceId());
			deviceID = ""+tManager.getDeviceId();
		} catch(Throwable t) {
			deviceID = ""+t.getMessage();
		}
		return deviceID;
	}

	public static boolean IsNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static String getRealPathFromURI(Uri contentUri, Activity activity) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static byte[] readBytesFromFile(File file) {
		byte[] b = new byte[(int) file.length()];
		try {
			//int length = (int) file.length();
			//byte[] array = new byte[length];
			InputStream in = new FileInputStream(file);
			//int offset = 0;
			in.read(b);
			//for(int i=0;i<array.length;i++){
			//	System.out.println(i+": "+array[i]);
			//}
			/*while (offset < length) {
				offset =offset+ in.read(array, offset, (length - offset));
			}*/
			in.close();
		} catch(Throwable t) {
			System.out.println(t.getMessage());
		}
		return b;
	}

	public static Vector<String> readTextFromFile(File aFile) {
		Vector<String> result = new Vector<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				while((line = input.readLine()) != null) {
					result.add(line);
					//contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch(Throwable t) {
			System.out.println(t.getMessage());
		}
		return result;
	}

	public static boolean writeTextToFile(File aFile, String aContents) {
		try {
			Writer output = new BufferedWriter(new FileWriter(aFile));
			output.write(aContents);
			output.close();
		} catch(Throwable t) {
			System.out.println(t.getMessage());
		}
		return false;
	}

	public static String extract(String line, int startSymbol, int endSymbol) {
		String r = "";
		int start = line.indexOf(startSymbol);
		if(start > -1) {
			int end = line.indexOf(endSymbol, start + 1);
			if(end > -1) {
				r = line.substring(start + 1, end);
			}
		}
		return r;
	}
}
