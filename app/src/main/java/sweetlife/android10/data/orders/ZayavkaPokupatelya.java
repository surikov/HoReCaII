package sweetlife.android10.data.orders;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class ZayavkaPokupatelya extends NomenclatureBasedDocument implements Parcelable {
	private Date mDataOtgruzki;
	private String mDogovorKontragenta;
	private String mKommentariy;
	private double mSummaDokumenta;
	private String mTipOplaty;
	private double mSebestoimost;
	private int mTipOplatyPoryadok;
	final static public int DOSTAVKA_OBICHNAYA = 0;
	final static public int DOSTAVKA_DOVERENNOST = 1;
	final static public int DOSTAVKA_PECHAT = 2;
	public int dostavkaKind = DOSTAVKA_OBICHNAYA;
	public String dostvkaKoment = "";
	public double dostvkaVozvrNakl = 0;

	public ZayavkaPokupatelya(Integer _id//
			, String idRRef//
			, Date date//
			, String nomer//
			, boolean vygruzhen//
			, Date dataOtgruzki//
			, String dogovorKontragentaID//
			, String kommentariy//
			, String kontragentID//
			, String kontragentKod//
			, String kontragentName//
			, Double SummaDokumenta//
			, String tipOplatyID//
			, Integer tipOplatyPoryadok//
			, Double sebestoimost//
			, boolean New//
	) {
		//System.out.println(this.getClass().getName() + " 1 start");
		m_id = _id;
		mIDRRef = idRRef;
		mDate = date;
		mNomer = nomer;
		mKontragentID = kontragentID;
		mKontragentKod = kontragentKod;
		mKontragentName = kontragentName;
		mProveden = vygruzhen;
		mNew = New;
		mKommentariy = kommentariy;
		mDataOtgruzki = dataOtgruzki;
		mDogovorKontragenta = dogovorKontragentaID;
		mSummaDokumenta = SummaDokumenta;
		mTipOplaty = tipOplatyID;
		mTipOplatyPoryadok = tipOplatyPoryadok;
		mSebestoimost = sebestoimost;
		readDostavka(ApplicationHoreca.getInstance().getDataBase());
		//System.out.println(this.getClass().getName() + " 1 end");
	}

	public ZayavkaPokupatelya(SQLiteDatabase db, ClientInfo client, Calendar chosedDay) {
		//System.out.println(this.getClass().getName() + " 2 start");
		Calendar today = Calendar.getInstance();
		m_id = 0;
		mIDRRef = Hex.generateIDRRefString();
		mDate = today.getTime();
		mKontragentID = client.getID();
		mKontragentKod = client.getKod();
		mKontragentName = client.getName();
		mNomer = generateDocumentNumber(db);
		mKommentariy = "";
		mProveden = false;
		mNew = true;
		mKommentariy = "";
		mDataOtgruzki = chosedDay.getTime();
		mDogovorKontragenta = "x'00'";
		mSummaDokumenta = 0;
		mTipOplaty = "x'00'";
		mTipOplatyPoryadok = 0;
		mSebestoimost = 0;
		readDostavka(db);
		//System.out.println(this.getClass().getName() + " 2 end");
	}

	public void setTipOplaty(String type) {
		mTipOplaty = type;
	}

	public ZayavkaPokupatelya(Parcel in) {
		readFromParcel(in);
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

	public String getTipOplatyName() {
		return getTipOplatyName(mTipOplaty);
	}

	public static String getTipOplatyName(String typOplatyID) {
		if (typOplatyID.compareToIgnoreCase(Cfg.tip_nalichnie) == 0) {
			return "Наличная";
		} else {
			if (typOplatyID.compareToIgnoreCase(Cfg.tip_beznal) == 0) {
				return "Безналичная";
			} else {
				if (typOplatyID.compareToIgnoreCase(Cfg.tip_tovcheck) == 0) {
					return "Товарный чек";
				}
			}
		}
		return "???";
	}

	public String getTipOplatyForUpload() {
		if (mTipOplaty.compareToIgnoreCase(Cfg.tip_nalichnie) == 0) {
			return "Нал";
		} else {
			if (mTipOplaty.compareToIgnoreCase(Cfg.tip_beznal) == 0) {
				return "БезНал";
			} else {
				if (mTipOplaty.compareToIgnoreCase(Cfg.tip_tovcheck) == 0) {
					return "ТовЧек";
				}
			}
		}
		return "Empty";
	}

	public String getTipOplaty() {
		return mTipOplaty;
	}

	public Date getShippingDate() {
		return mDataOtgruzki;
	}

	public long getShippingDateInMillis() {
		return mDataOtgruzki.getTime();
	}

	public void setShippingDate(long milliseconds) {
		mDataOtgruzki.setTime(milliseconds);
	}

	public double getSumma() {
		return mSummaDokumenta;
	}

	public void setSumma(double amount) {
		mSummaDokumenta = amount;
	}

	public String getComment() {
		return mKommentariy;
	}

	public void setComment(String comment) {
		mKommentariy = comment;
	}

	protected void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		//System.out.println(this.getClass().getName() + " readFromParcel start");
		mDataOtgruzki = new java.util.Date(in.readLong());
		mDogovorKontragenta = in.readString();
		mKommentariy = in.readString();
		mSummaDokumenta = in.readDouble();
		mTipOplaty = in.readString();
		mTipOplatyPoryadok = in.readInt();
		mSebestoimost = in.readDouble();
		dostavkaKind = in.readInt();
		dostvkaKoment = in.readString();
		dostvkaVozvrNakl = in.readDouble();
		//System.out.println(this.getClass().getName() + " readFromParcel end");
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		//System.out.println(this.getClass().getName() + " writeToParcel start");
		dest.writeLong(mDataOtgruzki.getTime());
		dest.writeString(mDogovorKontragenta);
		dest.writeString(mKommentariy);
		dest.writeDouble(mSummaDokumenta);
		dest.writeString(mTipOplaty);
		dest.writeInt(mTipOplatyPoryadok);
		dest.writeDouble(mSebestoimost);
		dest.writeInt(dostavkaKind);
		dest.writeString(dostvkaKoment);
		dest.writeDouble(dostvkaVozvrNakl);
		//System.out.println(this.getClass().getName() + " writeToParcel end");
	}

	public static final Parcelable.Creator<ZayavkaPokupatelya> CREATOR = new Parcelable.Creator<ZayavkaPokupatelya>() {
		public ZayavkaPokupatelya createFromParcel(Parcel in) {
			return new ZayavkaPokupatelya(in);
		}

		public ZayavkaPokupatelya[] newArray(int size) {
			return new ZayavkaPokupatelya[size];
		}
	};

	public void setContract(String contractID) {
		mDogovorKontragenta = contractID;
	}

	public String getDogovorKontragenta() {
		return mDogovorKontragenta;
	}

	public void setSebestoimost(double sebestoimost) {
		mSebestoimost = sebestoimost;
	}

	@Override
	protected void setDocumentNumberProperties() {
		mDocumentTableName = "ZayavkaPokupatelyaIskhodyaschaya";
		mDocumentNumberLenght = 10;
	}

	@Override
	public String getSerializedXML(SQLiteDatabase db) throws IllegalArgumentException, IllegalStateException, IOException {
		BidsXMLSerializer serializer = new BidsXMLSerializer(db, this);
		return serializer.SerializeXML();
	}

	@Override
	public void writeUploaded(SQLiteDatabase db) {
		//System.out.println(this.getClass().getCanonicalName() + ".writeUploaded");
		db.beginTransactionNonExclusive();
		try {
			ContentValues values = new ContentValues();
			values.put("Proveden", Hex.decodeHexWithPrefix("x'01'"));
			values.put("Nomer", mNomer);
			//System.out.println(this.getClass().getCanonicalName() + " /" + mDocumentTableName + ": " + String.valueOf(m_id));
			db.update(mDocumentTableName, values, "_id=" + String.valueOf(m_id), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	void updateDostavka(SQLiteDatabase db) {
		String sql = "delete from ZayavkaPokupatelyaIskhodyaschaya_Smvz where parent=" + mIDRRef;
		db.execSQL(sql);
		sql = "insert into ZayavkaPokupatelyaIskhodyaschaya_Smvz (parent,Komentary,Kind,dostvkaVozvrNakl) values ("//
				+ mIDRRef//
				+ ",'" + dostvkaKoment.replace('\n', ' ').replace('\'', '"').replace('\r', ' ') + "'"//
				+ "," + dostavkaKind //
				+ "," + dostvkaVozvrNakl //
				+ ")";
		db.execSQL(sql);
	}

	void readDostavka(SQLiteDatabase db) {
		String sql = "select Komentary,Kind,dostvkaVozvrNakl from ZayavkaPokupatelyaIskhodyaschaya_Smvz where parent=" + mIDRRef + " limit 1";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			dostvkaKoment = c.getString(0);
			dostavkaKind = c.getInt(1);
			dostvkaVozvrNakl = c.getDouble(2);
		}
		if (c != null) {
			c.close();
		}
	}

	@Override
	public void writeToDataBase(SQLiteDatabase db) {
		//System.out.println(this.getClass().getName() + " writeToDataBase start "+mNew);
		ContentValues values = new ContentValues();
		if (mNew) {
			values.put("_IDRRef", Hex.decodeHexWithPrefix(mIDRRef));
			values.put("Data", (new java.sql.Date(Calendar.getInstance().getTimeInMillis())).toString());
			values.put("Nomer", mNomer);
			values.put("Proveden", Hex.decodeHexWithPrefix(mProveden ? "x'01'" : "x'00'"));
			values.put("DataOtgruzki", (new java.sql.Date(mDataOtgruzki.getTime())).toString());
			values.put("DogovorKontragenta", Hex.decodeHexWithPrefix(mDogovorKontragenta));
			values.put("Kommentariy", mKommentariy);
			values.put("Kontragent", Hex.decodeHexWithPrefix(mKontragentID));
			values.put("Otvetstvennyy", mOtvetstvennyyKod);
			values.put("SummaDokumenta", mSummaDokumenta);
			values.put("TipOplaty", Hex.decodeHexWithPrefix(mTipOplaty));
			values.put("Sebestoimost", mSebestoimost);
			m_id = (int) db.insert(mDocumentTableName, null, values);
			updateDostavka(db);
			if (m_id > 0) {
				mNew = false;
			}
		} else {
			values.put("Proveden", Hex.decodeHexWithPrefix(mProveden ? "x'01'" : "x'00'"));
			values.put("DataOtgruzki", (new java.sql.Date(mDataOtgruzki.getTime())).toString());
			values.put("DogovorKontragenta", Hex.decodeHexWithPrefix(mDogovorKontragenta));
			values.put("Kommentariy", mKommentariy);
			values.put("SummaDokumenta", mSummaDokumenta);
			values.put("TipOplaty", Hex.decodeHexWithPrefix(mTipOplaty));
			values.put("Sebestoimost", mSebestoimost);
			db.update(mDocumentTableName, values, "_id=" + String.valueOf(m_id), null);
			updateDostavka(db);
		}
		//System.out.println("save mDataOtgruzki "+mDataOtgruzki);
		//System.out.println(this.getClass().getName() + " writeToDataBase end");
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
