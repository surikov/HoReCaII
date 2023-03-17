package sweetlife.android10.data.returns;

import java.util.Calendar;

import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ZayavkaNaVozvrat_Tovary extends NomenclatureBasedItem {
	public static final String[] ReasonsTypes = { "   "//
			//, "Брак"//
			, "Не устроило качество"//
			, "Пересорт"//
			, "Списание (кредит-нота)"//
			, "Сроки"//


	};
	//public static final int REASON_BRAK = 4;

	//public static final int REASON_OSHIBKA_ASSORTIMENTA = 3;
	//public static final int REASON_NET_V_KLASTERE = 24;




	public static final int REASON_NEUSTROILOKACHESTVO = 17;
	public static final int REASON_PERESORT = 25;
	public static final int REASON_CREDIT_NOTA = 97;
	public static final int REASON_KOROTKIE_SROKI = 5;


	private double mKolichestvo;
	private String mNomerNakladnoy;
	private Calendar mDataNakladnoy;
	private int mPrichina;
	private String mAktPretenziy;

	public ZayavkaNaVozvrat_Tovary(int _id, int nomerStroki, String nomenklaturaID, String artikul, String nomenklaturaNaimenovanie, double kolichestvo, String nomerNakladnoy, Calendar dataNakladnoy, int prichina, String aktPretenziy, String zayavka, boolean New //, String skidkaProcent//
	//, String skidkaNaimenovanie//
	) {
		super(_id, nomerStroki, nomenklaturaID, artikul, nomenklaturaNaimenovanie, zayavka, New//,skidkaProcent//
		//,skidkaNaimenovanie
		);
		mKolichestvo = kolichestvo;
		mNomerNakladnoy = nomerNakladnoy;
		mDataNakladnoy = dataNakladnoy;
		mPrichina = prichina;
		mAktPretenziy = aktPretenziy;
	}
	public void setToDataBase(SQLiteDatabase db) {
		//System.out.println("mKolichestvo: "+mKolichestvo);
		ContentValues values = new ContentValues();
		if (mNew) {
			values.put("NomerStroki", mNomerStroki);
			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("Kolichestvo", mKolichestvo);
			values.put("NomerNakladnoy", mNomerNakladnoy);
			values.put("DataNakladnoy", DateTimeHelper.SQLDateString(mDataNakladnoy.getTime()));
			values.put("Prichina", mPrichina);
			values.put("_ZayavkaNaVozvrat_IDRRef", Hex.decodeHexWithPrefix(mZayavka_IDRRef));
			DatabaseHelper.insertInTranzaction(db, "ZayavkaNaVozvrat_Tovary", values);
		}
		else {
			values.put("Kolichestvo", mKolichestvo);
			values.put("NomerNakladnoy", mNomerNakladnoy);
			values.put("DataNakladnoy", DateTimeHelper.SQLDateString(mDataNakladnoy.getTime()));
			values.put("Prichina", mPrichina);
			DatabaseHelper.updateInTranzaction(db, "ZayavkaNaVozvrat_Tovary", values, "_id=" + String.valueOf(m_id), null);
		}
	}
	public double getKolichestvo() {
		return mKolichestvo;
	}
	public void setKolichestvo(double kolichestvo) {
		mKolichestvo = kolichestvo;
	}
	public String getNomerNakladnoy() {
		return mNomerNakladnoy;
	}
	public void setNomerNakladnoy(String nomerNakladnoy) {
		mNomerNakladnoy = nomerNakladnoy;
	}
	public Calendar getDataNakladnoy() {
		return mDataNakladnoy;
	}
	public String getDataNakladnoyUIString() {
		if (mDataNakladnoy == null) {
			return "";
		}
		else {
			return DateTimeHelper.UIDateString(mDataNakladnoy.getTime());
		}
	}
	public void setDataNakladnoy(Calendar dataNakladnoy) {
		mDataNakladnoy = dataNakladnoy;
	}
	public int getPrichina() {
		//System.out.println("getPrichina "+mPrichina);
		return mPrichina;
	}
	public String getPrichinaString() {
		String s= ReasonsTypes[0];
		switch (mPrichina) {
		//case REASON_BRAK:
		//	s= ReasonsTypes[1];
		case REASON_NEUSTROILOKACHESTVO:
			s= ReasonsTypes[1];
		case REASON_PERESORT:
			s= ReasonsTypes[2];
		case REASON_CREDIT_NOTA:
			s= ReasonsTypes[3];
		case REASON_KOROTKIE_SROKI:
			s= ReasonsTypes[4];

		}
		
		//System.out.println("getPrichinaString "+mPrichina+": "+s);
		return s;
	}
	/*public void setPrichina(int prichina) {
		System.out.println("getPrichina "+prichina);
		mPrichina = prichina;
	}
	public String getAktPretenziy() {
		return mAktPretenziy;
	}
	public void setAktPretenziy(String aktPretenziy) {
		mAktPretenziy = aktPretenziy;
	}*/
}
