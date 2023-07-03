package sweetlife.android10.data.returns;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class ZayavkaNaVozvrat extends NomenclatureBasedDocument implements Parcelable {

	private Date mDataOtgruzki;
	private String mAktPretenziyPath;
	private String mVersion = "";

	public ZayavkaNaVozvrat(Parcel in) {

		readFromParcel(in);
	}

	public ZayavkaNaVozvrat(int _id,
							String idRRef,
							Date date,
							String nomer,
							String kontragentID,
							String kontragentKod,
							String kontragentName,
							Date dataOtgruzki,
							String aktPretenziyPath,
							boolean proveden,
							boolean New
			, String _Version
	) {

		m_id = _id;
		mIDRRef = idRRef;
		mDate = date;
		mNomer = nomer;
		mKontragentID = kontragentID;
		mKontragentKod = kontragentKod;
		mKontragentName = kontragentName;
		mDataOtgruzki = dataOtgruzki;
		mAktPretenziyPath = aktPretenziyPath;
		mProveden = proveden;
		mNew = New;
		mVersion = _Version;
	}

	public ZayavkaNaVozvrat(String clientID, SQLiteDatabase db) {

		Calendar today = Calendar.getInstance();

		ClientInfo mClient = new ClientInfo(db, clientID);

		m_id = 0;
		mIDRRef = Hex.generateIDRRefString();
		mDate = today.getTime();
		mNomer = generateDocumentNumber(db);
		mKontragentID = mClient.getID();
		mKontragentKod = mClient.getKod();
		mKontragentName = mClient.getName();
		mNomer = generateDocumentNumber(db);
		mDataOtgruzki = nextWorkingDate(today);
		mAktPretenziyPath = null;
		mProveden = false;
		mNew = true;
		mVersion = "";
	}

	@Override
	protected void setDocumentNumberProperties() {

		mDocumentTableName = "ZayavkaNaVozvrat";
		mDocumentNumberLenght = 9;
	}

	public void setDataOtgruzki(Date dataOtgruzki) {

		mDataOtgruzki = dataOtgruzki;
	}

	public void setAktPretenziyPath(String aktPretenziyPath) {

		//System.out.println("setAktPretenziyPath "+aktPretenziyPath);
		mAktPretenziyPath = aktPretenziyPath;
	}

	public String getAktPretenziyPath() {

		//System.out.println("getAktPretenziyPath "+mAktPretenziyPath);
		return mAktPretenziyPath;
	}

	public void setVersion(String a) {
		//System.out.println("setVersion "+a);
		mVersion = a;
	}

	public String getVersion() {
		//System.out.println("getVersion "+mVersion);
		return mVersion;
	}

	public String getComment() {

		return "" + mVersion;
	}

	public Date getDataOtgruzki() {

		return mDataOtgruzki;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		super.writeToParcel(dest, flags);

		dest.writeLong(mDataOtgruzki.getTime());
		dest.writeString(mAktPretenziyPath);
		dest.writeString(mVersion);
	}

	protected void readFromParcel(Parcel in) {

		super.readFromParcel(in);

		mDataOtgruzki = new Date(in.readLong());
		mAktPretenziyPath = in.readString();
		mVersion = in.readString();
	}

	public static final Parcelable.Creator<ZayavkaNaVozvrat> CREATOR =
			new Parcelable.Creator<ZayavkaNaVozvrat>() {

				public ZayavkaNaVozvrat createFromParcel(Parcel in) {

					return new ZayavkaNaVozvrat(in);
				}

				public ZayavkaNaVozvrat[] newArray(int size) {

					return new ZayavkaNaVozvrat[size];
				}
			};

	public void writeToDataBase(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		if (mNew) {

			values.put("_IDRRef", Hex.decodeHexWithPrefix(mIDRRef));
			values.put("Data", DateTimeHelper.SQLDateString(mDate));
			values.put("Nomer", mNomer);
			values.put("Proveden", Hex.decodeHexWithPrefix(mProveden ? "x'01'" : "x'00'"));
			values.put("Kontragent", Hex.decodeHexWithPrefix(mKontragentID));
			values.put("DataOtgruzki", DateTimeHelper.SQLDateString(mDataOtgruzki));
			values.put("AktPretenziy", mAktPretenziyPath);
			values.put("_Version", mVersion);
			values.put("Otvetstvennyy", mOtvetstvennyyKod);

			DatabaseHelper.insertInTranzaction(db, mDocumentTableName, values);

		} else {

			values.put("Proveden", Hex.decodeHexWithPrefix(mProveden ? "x'01'" : "x'00'"));
			values.put("DataOtgruzki", DateTimeHelper.SQLDateString(mDataOtgruzki));
			values.put("AktPretenziy", mAktPretenziyPath);
			values.put("_Version", mVersion);


			DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_id=" + String.valueOf(m_id), null);
		}
	}

	@Override
	public String getSerializedXML(SQLiteDatabase db) throws IllegalArgumentException,
			IllegalStateException, IOException {

		ReturnsXMLSerializer serializer = new ReturnsXMLSerializer(db, this);

		return serializer.SerializeXML();
	}

	@Override
	public void writeUploaded(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		values.put("Proveden", Hex.decodeHexWithPrefix("x'01'"));

		DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_id=" + String.valueOf(m_id), null);
	}
}
