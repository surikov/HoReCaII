package sweetlife.android10.data.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.utils.DateTimeHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class NomenclatureBasedDocument implements Parcelable {
	protected Integer mDocumentNumber = 0;
	protected Integer mDocumentNumberLenght = 10;
	protected String mDocumentNumberPrefix = "";
	protected String mDocumentTableName;
	protected int m_id;
	protected String mIDRRef;
	protected Date mDate;
	public String mNomer;
	protected String mOtvetstvennyyKod;
	protected boolean mProveden;
	protected String mKontragentID;
	protected String mKontragentName;
	protected String mKontragentKod;
	protected boolean mNew;

	public NomenclatureBasedDocument() {
		mOtvetstvennyyKod = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod();
		mDocumentNumberPrefix = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim();
		setDocumentNumberProperties();
	}
	protected void readFromParcel(Parcel in) {
		m_id = in.readInt();
		mIDRRef = in.readString();
		mDate = new Date(in.readLong());
		mNomer = in.readString();
		mOtvetstvennyyKod = in.readString();
		mKontragentID = in.readString();
		mKontragentKod = in.readString();
		mKontragentName = in.readString();
		boolean[] array = new boolean[2];
		in.readBooleanArray(array);
		mProveden = array[0];
		mNew = array[1];
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(m_id);
		dest.writeString(mIDRRef);
		dest.writeLong(mDate.getTime());
		dest.writeString(mNomer);
		dest.writeString(mOtvetstvennyyKod);
		dest.writeString(mKontragentID);
		dest.writeString(mKontragentKod);
		dest.writeString(mKontragentName);
		dest.writeBooleanArray(new boolean[] { mProveden, mNew });
	}
	public abstract String getSerializedXML(SQLiteDatabase db) throws IllegalArgumentException, IllegalStateException, IOException;
	protected void setDocumentNumberProperties() {
	}
	private void _readDocumentNomer(SQLiteDatabase db) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select Nomer from " + mDocumentTableName + " where _id = " + "(select max(_id) from " + mDocumentTableName + ")", null);
			if (cursor.moveToFirst()) {
				String nomer = cursor.getString(0);
				try {
					mDocumentNumber = Integer.parseInt(nomer.substring(mDocumentNumberPrefix.length(), nomer.length()));
				}
				catch (Throwable t) {
					//t.printStackTrace();
					System.out.println("readDocumentNomer: can't parse " + nomer);
					mDocumentNumber = Math.round(0);
				}
				cursor.close();
			}
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	public abstract void writeUploaded(SQLiteDatabase db);
	public abstract void writeToDataBase(SQLiteDatabase db);
	public String getOtvetstvennyyKod() {
		return mOtvetstvennyyKod;
	}
	public String getIDRRef() {
		return mIDRRef;
	}
	public Date getDate() {
		return mDate;
	}
	public void setDate(Date date) {
		mDate = date;
	}
	public boolean isNew() {
		return mNew;
	}
	public void setNew(boolean New) {
		mNew = New;
	}
	protected String generateDocumentNumber(SQLiteDatabase db) {
		String s=mDocumentNumberPrefix;
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmssSSS");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		s=s+"-"+format.format(new java.util.Date());
		//s=s+"-"+new java.util.Date().getTime();
		//System.out.println("generateDocumentNumber "+s);
		/*
		readDocumentNomer(db);
		mDocumentNumber++;
		StringBuilder resultNumber = new StringBuilder(mDocumentNumberPrefix + mDocumentNumber.toString());
		while (resultNumber.length() < mDocumentNumberLenght) {
			resultNumber.insert(mDocumentNumberPrefix.length(), '0');
		}
		return resultNumber.toString();*/
		return s;
	}
	public static Date nextWorkingDate(Calendar chosedDay) {
		Calendar rigthNow = DateTimeHelper.getOnlyDateInfo(chosedDay);
		switch (rigthNow.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SATURDAY:
			rigthNow.roll(Calendar.DAY_OF_YEAR, 2);
			break;
		default:
			rigthNow.roll(Calendar.DAY_OF_YEAR, 1);
			break;
		}
		return rigthNow.getTime();
	}
	public String getNomer() {
		return mNomer;
	}
	public void setClient(String id, String kod, String name) {
		mKontragentID = id;
		mKontragentName = name;
		mKontragentKod = kod;
	}
	public String getClientID() {
		return mKontragentID;
	}
	public String getClientKod() {
		return mKontragentKod;
	}
	public String getClientName() {
		return mKontragentName;
	}
	public boolean isProveden() {
		return mProveden;
	}
}
