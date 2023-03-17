package reactive.ui;

import android.view.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
/*
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
*/
import android.content.*;
import java.net.*;
import android.view.animation.*;
import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.it.*;
import java.io.*;
import java.text.*;

public class Preferences {
	static Hashtable<String, Numeric> integers = new Hashtable<String, Numeric>();
	static Hashtable<String, Note> strings = new Hashtable<String, Note>();
	static Hashtable<String, Toggle> toggles = new Hashtable<String, Toggle>();
	//static Preferences me;
	static SharedPreferences preferences;
	private Preferences() {
	}
	public static void init(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}
	public static void save() {
		try {
			//System.out.println("save preferences");
			SharedPreferences.Editor editor = preferences.edit();
			for (Enumeration<String> e = integers.keys(); e.hasMoreElements();) {
				String k = e.nextElement();
				editor.putInt(k, integers.get(k).value().intValue());
				//System.out.println("save preference: "+k+"="+integers.get(k).value().intValue());
			}
			for (Enumeration<String> e = strings.keys(); e.hasMoreElements();) {
				String k = e.nextElement();
				editor.putString(k, strings.get(k).value());
				//System.out.println("save preference: "+k+"="+integers.get(k).value().intValue());
			}
			for (Enumeration<String> e = toggles.keys(); e.hasMoreElements();) {
				String k = e.nextElement();
				editor.putBoolean(k, toggles.get(k).value());
				//System.out.println("save preference: "+k+"="+integers.get(k).value().intValue());
			}
			editor.commit();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	public static Numeric integer(String name, int minValue, int defaultValue, int maxValue) {
		Numeric n = integer(name, defaultValue);
		if (n.value() < minValue) {
			n.value(minValue);
		}
		if (n.value() > maxValue) {
			n.value(maxValue);
		}
		return n;
	}
	public static Numeric integer(String name, int defaultValue) {
		//System.out.println("read preference: "+name);
		Numeric n = integers.get(name);
		if (n == null) {
			int storedPreference = preferences.getInt(name, defaultValue);
			n = new Numeric().value(storedPreference);
			integers.put(name, n);
			//System.out.println("create preference: "+name+"="+n.value().intValue());
		}
		return n;
	}
	public static Note string(String name, String defaultValue) {
		Note n = strings.get(name);
		if (n == null) {
			String storedPreference = preferences.getString(name, defaultValue);
			n = new Note().value(storedPreference);
			strings.put(name, n);
		}
		return n;
	}
	public static Toggle toggle(String name, boolean defaultValue) {
		Toggle n = toggles.get(name);
		if (n == null) {
			boolean storedPreference = preferences.getBoolean(name, defaultValue);
			n = new Toggle().value(storedPreference);
			toggles.put(name, n);
		}
		return n;
	}
}
