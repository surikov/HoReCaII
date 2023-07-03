package sweetlife.android10.data.disposal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class RasporyazhenieNaOtgruzku extends NomenclatureBasedDocument implements Parcelable {

	private double mSumma;
	private String mKommentariy;
	private boolean mVygruzhen;
	private ArrayList<String> mFiles;

	public RasporyazhenieNaOtgruzku(Parcel in) {

		readFromParcel(in);
	}

	public RasporyazhenieNaOtgruzku(int _id,
									String idRRef,
									Date date,
									String nomer,
									String kontragentID,
									String kontragentKod,
									String kontragentName,
									double summa,
									String kommentariy,
									boolean vygruzhen,
									boolean New,
									ArrayList<String> files) {


		m_id = _id;
		mIDRRef = idRRef;
		mDate = date;
		mNomer = nomer;
		mKontragentID = kontragentID;
		mKontragentKod = kontragentKod;
		mKontragentName = kontragentName;
		mProveden = vygruzhen;
		mNew = New;


		mSumma = summa;
		mKommentariy = kommentariy;
		mVygruzhen = vygruzhen;
		mFiles = files;
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

	public boolean isVygruzhen() {

		return mVygruzhen;
	}

	public void setSumma(double summa) {

		mSumma = summa;
	}

	public double getSumma() {

		return mSumma;
	}

	public void addFile(String file) {

		mFiles.add(file);
	}

	public void removeFile(int index) {

		mFiles.remove(index);
	}

	public ArrayList<String> getFiles() {

		return mFiles;
	}

	public void setKommentariy(String kommentariy) {

		mKommentariy = kommentariy;
	}

	public String getKommentariy() {

		return mKommentariy;
	}

	public RasporyazhenieNaOtgruzku(SQLiteDatabase db) {

		Calendar today = Calendar.getInstance();

		m_id = 0;
		mIDRRef = Hex.generateIDRRefString();
		mDate = today.getTime();
		mNomer = generateDocumentNumber(db);
		mKontragentID = "x'00'";
		mKontragentKod = "";
		mKontragentName = "";
		mSumma = 0.00D;
		mKommentariy = "";
		mProveden = false;
		mVygruzhen = false;
		mNew = true;
		mFiles = new ArrayList<String>();
	}

	@Override
	protected void setDocumentNumberProperties() {

		mDocumentTableName = "RasporyazhenieNaOtgruzku";
		mDocumentNumberLenght = 9;
		mDocumentNumberPrefix = "";
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		super.writeToParcel(dest, flags);

		dest.writeDouble(mSumma);
		dest.writeString(mKommentariy);

		int filesListSize = mFiles.size();
		dest.writeInt(filesListSize);
		if (filesListSize != 0) {

			dest.writeStringArray(mFiles.toArray(new String[filesListSize]));
		}
	}

	protected void readFromParcel(Parcel in) {

		super.readFromParcel(in);

		mSumma = in.readDouble();
		mKommentariy = in.readString();

		mVygruzhen = mProveden;

		int filesListSize = in.readInt();
		mFiles = new ArrayList<String>();

		if (filesListSize != 0) {

			String[] stringsArray = new String[filesListSize];
			in.readStringArray(stringsArray);
			Collections.addAll(mFiles, stringsArray);
		}
	}

	public static final Parcelable.Creator<RasporyazhenieNaOtgruzku> CREATOR =
			new Parcelable.Creator<RasporyazhenieNaOtgruzku>() {

				public RasporyazhenieNaOtgruzku createFromParcel(Parcel in) {

					return new RasporyazhenieNaOtgruzku(in);
				}

				public RasporyazhenieNaOtgruzku[] newArray(int size) {

					return new RasporyazhenieNaOtgruzku[size];
				}
			};

	private void writeFiles(SQLiteDatabase db) {

		db.beginTransactionNonExclusive();

		try {

			for (String file : mFiles) {

				Cursor cursor = db.rawQuery("select * from RasporyazhenieNaOtgruzku_Phayly " +
						"where Put='" + file + "' and _RasporyazhenieNaOtgruzku_IDRRef = " + mIDRRef, null);

				if (cursor != null) {

					if (!cursor.moveToFirst()) {

						db.execSQL("insert into RasporyazhenieNaOtgruzku_Phayly (Put, _RasporyazhenieNaOtgruzku_IDRRef) " +
								"values ( '" + file + "', " + mIDRRef + " )");
					}

					cursor.close();
				}
			}

			db.setTransactionSuccessful();
		} finally {

			db.endTransaction();
		}
	}

	@Override
	public void writeToDataBase(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		if (mNew) {

			values.put("_IDRRef", Hex.decodeHexWithPrefix(mIDRRef));
			values.put("Data", DateTimeHelper.SQLDateString(mDate));
			values.put("Nomer", mNomer);
			values.put("Vygruzhen", Hex.decodeHexWithPrefix(mVygruzhen ? "x'01'" : "x'00'"));
			values.put("Kontragent", Hex.decodeHexWithPrefix(mKontragentID));
			values.put("Summa", mSumma);
			values.put("Kommentariy", mKommentariy);
			values.put("Otvetstvennyy", mOtvetstvennyyKod);

			DatabaseHelper.insertInTranzaction(db, mDocumentTableName, values);
		} else {

			values.put("Vygruzhen", Hex.decodeHexWithPrefix(mVygruzhen ? "x'01'" : "x'00'"));
			values.put("Kontragent", Hex.decodeHexWithPrefix(mKontragentID));
			values.put("Summa", mSumma);
			values.put("Kommentariy", mKommentariy);

			DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_id=" + String.valueOf(m_id), null);
		}

		writeFiles(db);
	}

	@Override
	public String getSerializedXML(SQLiteDatabase db) throws IllegalArgumentException,
			IllegalStateException, IOException {

		DisposalsXMLSerializer serializer = new DisposalsXMLSerializer(db, this);
		return serializer.SerializeXML();
	}

	@Override
	public void writeUploaded(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		values.put("Vygruzhen", Hex.decodeHexWithPrefix("x'01'"));

		DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_IDRRef=" + String.valueOf(mIDRRef), null);
	}
}
