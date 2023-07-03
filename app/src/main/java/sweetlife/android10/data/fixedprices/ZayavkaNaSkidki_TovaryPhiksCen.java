package sweetlife.android10.data.fixedprices;

import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class ZayavkaNaSkidki_TovaryPhiksCen extends NomenclatureBasedItem {

	private double mCena;
	private double mObyazatelstva; //Товарооборот

	public ZayavkaNaSkidki_TovaryPhiksCen(int _id,
										  int nomerStroki,
										  String nomenklaturaID,
										  String artikul,
										  String nomenklaturaNaimenovanie,
										  double cena,
										  double obyazatelstva,
										  String zayavka,
										  boolean New //, String skidkaProcent//
										  //, String skidkaNaimenovanie//
	) {
		super(_id, nomerStroki, nomenklaturaID, artikul, nomenklaturaNaimenovanie, zayavka, New//
				//,skidkaProcent
				//,skidkaNaimenovanie
		);

		mCena = cena;
		mObyazatelstva = obyazatelstva;
	}

	public void setToDataBase(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		if (mNew) {

			values.put("NomerStroki", mNomerStroki);
			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("Cena", mCena);
			values.put("Obyazatelstva", mObyazatelstva);
			values.put("Artikul", mArtikul);
			values.put("_ZayavkaNaSkidki_IDRRef", Hex.decodeHexWithPrefix(mZayavka_IDRRef));

			DatabaseHelper.insertInTranzaction(db, "ZayavkaNaSkidki_TovaryPhiksCen", values);

		} else {

			values.put("Cena", mCena);
			values.put("Obyazatelstva", mObyazatelstva);

			DatabaseHelper.updateInTranzaction(db, "ZayavkaNaSkidki_TovaryPhiksCen", values, "_id=" + String.valueOf(m_id), null);
		}
	}

	public double getCena() {

		return mCena;
	}

	public void setCena(double cena) {

		mCena = cena;
	}

	public double getObyazatelstva() {

		return mObyazatelstva;
	}

	public void setObyazatelstva(double obyazatelstva) {

		mObyazatelstva = obyazatelstva;
	}
}
