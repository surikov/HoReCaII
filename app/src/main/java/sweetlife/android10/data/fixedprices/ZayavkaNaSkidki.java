package sweetlife.android10.data.fixedprices;

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

public class ZayavkaNaSkidki extends NomenclatureBasedDocument implements Parcelable {
	
	private Date       mVremyaNachalaSkidkiPhiksCen;
	private Date       mVremyaOkonchaniyaSkidkiPhiksCen;
	private String     mKommentariy;
	private boolean    mVygruzhen;

	public ZayavkaNaSkidki(Parcel in) {

		readFromParcel( in );
	}

	public ZayavkaNaSkidki(int _id,
			String     idRRef,
			Date       date,
			String     nomer,
			String     kontragentID,
			String     kontragentKod,
			String     kontragentName,
			Date       vremyaNachalaSkidkiPhiksCen,
			Date       vremyaOkonchaniyaSkidkiPhiksCen,
			String     kommentariy,
			boolean    vygruzhen,
			boolean    New) {
		
		m_id = _id;
		mIDRRef = idRRef;
		mDate = date;
		mNomer = nomer;
		mKontragentID = kontragentID;
		mKontragentKod = kontragentKod;
		mKontragentName = kontragentName;
		mProveden = vygruzhen;
		mNew = New;

		mVremyaNachalaSkidkiPhiksCen = vremyaNachalaSkidkiPhiksCen;
		mVremyaOkonchaniyaSkidkiPhiksCen = vremyaOkonchaniyaSkidkiPhiksCen;
		mKommentariy = kommentariy;
		mVygruzhen = vygruzhen;
	}
	
	public ZayavkaNaSkidki( String clientID, SQLiteDatabase db ) {
		
		Calendar today = Calendar.getInstance();

		ClientInfo client = new ClientInfo(db, clientID);
		
		m_id = 0;
		mIDRRef = Hex.generateIDRRefString();
		mDate = today.getTime();
		mKontragentID = client.getID();
		mKontragentKod = client.getKod();
		mKontragentName = client.getName();
		mNomer = generateDocumentNumber(db);
		mVremyaNachalaSkidkiPhiksCen = nextWorkingDate(today);
		mVremyaOkonchaniyaSkidkiPhiksCen = nextWorkingDate(today);
		mKommentariy = "";
		mVygruzhen = false;
		mProveden = false;
		mNew = true;
	}
	
	@Override
	protected void setDocumentNumberProperties() {

		mDocumentTableName = "ZayavkaNaSkidki";
		mDocumentNumberLenght = 5;	
		mDocumentNumberPrefix = "";	
	}
	
	@Override
	public int describeContents() {

		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {

		super.writeToParcel(dest, flags);
		
		dest.writeLong(mVremyaNachalaSkidkiPhiksCen.getTime());
		dest.writeLong(mVremyaOkonchaniyaSkidkiPhiksCen.getTime());
		dest.writeString(mKommentariy);
	}

	protected void readFromParcel(Parcel in) {

		super.readFromParcel(in);

		mVremyaNachalaSkidkiPhiksCen =  new Date(in.readLong());
		mVremyaOkonchaniyaSkidkiPhiksCen =  new Date(in.readLong());
		mKommentariy = in.readString();
		mVygruzhen = mProveden;
	}

	public static final Parcelable.Creator<ZayavkaNaSkidki> CREATOR =
			new Parcelable.Creator<ZayavkaNaSkidki>() {

		public ZayavkaNaSkidki createFromParcel(Parcel in) {

			return new ZayavkaNaSkidki(in);
		}

		public ZayavkaNaSkidki[] newArray(int size) {

			return new ZayavkaNaSkidki[size];
		}
	};
	
	public void writeToDataBase( SQLiteDatabase db ) {

		ContentValues values = new ContentValues();
		
		if( mNew ) {

			values.put("_IDRRef", Hex.decodeHexWithPrefix(mIDRRef) );
			values.put("Data", DateTimeHelper.SQLDateString(mDate) );
			values.put("Nomer", mNomer ); 
			values.put("Vygruzhen", Hex.decodeHexWithPrefix(mVygruzhen ? "x'01'" : "x'00'") );
			values.put("Kontragent", Hex.decodeHexWithPrefix(mKontragentID));//
			values.put("VremyaNachalaSkidkiPhiksCen", DateTimeHelper.SQLDateString(mVremyaNachalaSkidkiPhiksCen) );  
			values.put("VremyaOkonchaniyaSkidkiPhiksCen", DateTimeHelper.SQLDateString(mVremyaOkonchaniyaSkidkiPhiksCen) ); 
			values.put("Kommentariy", mKommentariy );
			values.put("Otvetstvennyy", mOtvetstvennyyKod );

			m_id=(int)DatabaseHelper.insertInTranzaction(db, mDocumentTableName, values);

			mNew=false;
		}
		else {

			values.put("Vygruzhen", Hex.decodeHexWithPrefix(mVygruzhen ? "x'01'" : "x'00'") );
			values.put("VremyaNachalaSkidkiPhiksCen", DateTimeHelper.SQLDateString(mVremyaNachalaSkidkiPhiksCen) );  
			values.put("VremyaOkonchaniyaSkidkiPhiksCen", DateTimeHelper.SQLDateString(mVremyaOkonchaniyaSkidkiPhiksCen) ); 
			values.put("Kommentariy", mKommentariy );

			DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_id="+ String.valueOf(m_id), null);
		}
	}
	
	public void setVremyaNachalaSkidkiPhiksCen(Date date) {

		mVremyaNachalaSkidkiPhiksCen = date;
	}
	
	public void setVremyaOkonchaniyaSkidkiPhiksCen(Date date) {

		mVremyaOkonchaniyaSkidkiPhiksCen = date;
	}
	
	public void setKommentariy(String kommentariy) {

		mKommentariy = kommentariy;
	}
	
	public String getIDRRef() {

		return mIDRRef;
	}

	public String getKommentariy() {

		return mKommentariy;
	}
	
	public Date getVremyaNachalaSkidkiPhiksCen() {

		return mVremyaNachalaSkidkiPhiksCen;
	}
	
	public Date getVremyaOkonchaniyaSkidkiPhiksCen() {

		return mVremyaOkonchaniyaSkidkiPhiksCen;
	}

	@Override
	public String getSerializedXML(SQLiteDatabase db) throws IllegalArgumentException, 
	IllegalStateException, IOException {

		FixedPricesXMLSerializer serializer = new FixedPricesXMLSerializer(db, this);
		return serializer.SerializeXML();
	}

	@Override
	public void writeUploaded(SQLiteDatabase db) {
		
		ContentValues values = new ContentValues();
		
		values.put("Vygruzhen", Hex.decodeHexWithPrefix("x'01'") );

		DatabaseHelper.updateInTranzaction(db, mDocumentTableName, values, "_id="+ String.valueOf(m_id), null);
	}
}
