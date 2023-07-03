package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.NomenclatureBasedCountItem;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ZayavkaPokupatelya_Service extends NomenclatureBasedCountItem {

	private String mSoderzhanie;
	private double mCena;
	private double mSumma;

	public ZayavkaPokupatelya_Service(int _id,
									  int nomerStroki,
									  String nomenklaturaID,
									  String artikul,
									  String nomenklaturaNaimenovanie,
									  String zayavka,
									  String soderzhanie,
									  double kolichestvo,
									  double cena,
									  double summa,
									  boolean New//, String skidkaProcent//
									  //, String skidkaNaimenovanie//
	) {
		super(_id, nomerStroki,
				nomenklaturaID,
				artikul,
				nomenklaturaNaimenovanie,
				zayavka,
				1,
				1,
				"x'00'",
				"шт",
				kolichestvo,
				New
				//,skidkaProcent
				//,skidkaNaimenovanie
		);

		mSoderzhanie = soderzhanie;
		mCena = cena;
		mSumma = summa;
	}

	@Override
	public void setToDataBase(SQLiteDatabase db) {

		ContentValues values = new ContentValues();

		if (mNew) {

			values.put("NomerStroki", mNomerStroki);
			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("Soderzhanie", mSoderzhanie);
			values.put("Kolichestvo", mKolichestvo);
			values.put("Cena", mCena);
			values.put("Summa", mSumma);
			values.put("_ZayavkaPokupatelyaIskhodyaschaya_IDRRef", Hex.decodeHexWithPrefix(mZayavka_IDRRef));

			m_id = (int) db.insert("ZayavkaPokupatelyaIskhodyaschaya_Uslugi", null, values);
			mNew = false;
		} else {

			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("Soderzhanie", mSoderzhanie);
			values.put("Kolichestvo", mKolichestvo);
			values.put("Cena", mCena);
			values.put("Summa", mSumma);

			db.update("ZayavkaPokupatelyaIskhodyaschaya_Uslugi", values, "_id=" + String.valueOf(m_id), null);
		}
	}

	public String getSoderzhanie() {
		return mSoderzhanie;
	}

	public void setSoderzhanie(String soderzhanie) {
		mSoderzhanie = soderzhanie;
	}

	public double getKolichestvo() {
		return mKolichestvo;
	}

	public void setKolichestvo(Integer kolichestvo) {
		mKolichestvo = kolichestvo;

		mSumma = mCena * mKolichestvo;
	}

	public double getCena() {
		return mCena;
	}

	public void setCena(Double cena) {
		mCena = cena;
	}

	public double getSumma() {
		return mSumma;
	}

	public void setSumma(Double summa) {
		mSumma = summa;
	}
}
