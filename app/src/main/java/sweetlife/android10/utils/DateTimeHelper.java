package sweetlife.android10.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
public class DateTimeHelper {
	private static SimpleDateFormat mSQLDateFormat;
	private static SimpleDateFormat mSQLDateShortTimeFormat;
	private static SimpleDateFormat mSQLDateTimeFormat;
	private static SimpleDateFormat mUIDateFormat;
	private static SimpleDateFormat mYYYYMMDD;
	private static String[] mDateFormatStrings;
	static {
		mYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		mSQLDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mSQLDateShortTimeFormat = new SimpleDateFormat("dd.MM.yy HH-mm");
		mSQLDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		mSQLDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mUIDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		mDateFormatStrings = new String[]{"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
				//				"yyyy-MM-dd hh:mm:ss",
				"yyyy-MM-dd hh:mm", "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm:ss.sss", "yyyy-MM-dd'T'hh:mm", "yyyy-MM-dd'T'hh:mm:ss", "yyyy-MM-dd'T'hh:mm:ss.sss", "hh:mm", "hh:mm:ss", "hh:mm:ss.sss", "yyyyMMdd'T'hh:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};
	}

	public static java.sql.Date DateToSQLDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
	public static String dateYYYYMMDD(java.util.Date date) {
		return mYYYYMMDD.format(date);
	}
	public static String SQLDateString(java.util.Date date) {
		return mSQLDateFormat.format(date);
	}
	public static String SQLDateTimeString(java.util.Date date) {
		return mSQLDateTimeFormat.format(date);
	}
	public static String SQLDateShortTimeString(String date) {
		String s = "";
		try {
			s = mSQLDateShortTimeFormat.format(SQLDateToDate(date));
		} catch (Throwable t) {
			s = t.getMessage();
		}
		return s;
	}
	public static String UIDateString(java.util.Date date) {
		return mUIDateFormat.format(date);
	}
	public static java.util.Date UIStringDate(String date) {
		try {
			return mUIDateFormat.parse(date);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	public static java.sql.Timestamp DateToSQLTimestamp(java.util.Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
	public static java.util.Date SQLDateToDate(String strDate) {
		try {
			return DateUtils.parseDate(strDate, mDateFormatStrings);
		} catch (DateParseException e) {
			e.printStackTrace();
		}
		return Calendar.getInstance().getTime();
	}
	public static Calendar getOnlyDateInfo(Calendar dateTime) {
		dateTime.clear(Calendar.HOUR);
		dateTime.clear(Calendar.HOUR_OF_DAY);
		dateTime.clear(Calendar.MINUTE);
		dateTime.clear(Calendar.SECOND);
		dateTime.clear(Calendar.MILLISECOND);
		return dateTime;
	}
}
