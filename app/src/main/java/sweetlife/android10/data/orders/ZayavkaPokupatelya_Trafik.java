package sweetlife.android10.data.orders;

import java.util.Date;
import sweetlife.android10.data.common.NomenclatureBasedCountItem;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ZayavkaPokupatelya_Trafik extends NomenclatureBasedCountItem {
	private Date mData;
	private String mKommentariy;
	public boolean vetSpravka = false;
	public ZayavkaPokupatelya_Trafik(int _id//
			, int nomerStroki//
			, String nomenklaturaID//
			, String artikul//
			, String nomenklaturaNaimenovanie//
			, String zayavka//
			, String edinicaIzmereniyaID,//
			String edinicaIzmereniyaName//
			, double kolichestvo//
			, Date data//
			, String kommentariy//
			, double minNorma//
			, double koefMest//
			, boolean New//, String skidkaProcent//
			//, String skidkaNaimenovanie//
			, boolean vSpravka) {
		super(_id//
				, nomerStroki//
				, nomenklaturaID//
				, artikul//
				, nomenklaturaNaimenovanie//
				, zayavka//
				, minNorma//
				, koefMest//
				, edinicaIzmereniyaID//
				, edinicaIzmereniyaName//
				, kolichestvo//
				, New//,skidkaProcent
																																											//,skidkaNaimenovanie
		);
		vetSpravka=vSpravka;
		mData = data;
		mKommentariy = kommentariy;
	}
	@Override
	public void setToDataBase(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		if (mNew) {
			values.put("NomerStroki", mNomerStroki);
			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("EdinicaIzmereniya", mEdinicaIzmereniyaID);
			values.put("Kolichestvo", mKolichestvo);
			values.put("Data", DateTimeHelper.SQLDateString(mData));
			values.put("Kommentariy", mKommentariy);
			values.put("vs", vetSpravka ? 1 : 0);
			values.put("_ZayavkaPokupatelyaIskhodyaschaya_IDRRef", Hex.decodeHexWithPrefix(mZayavka_IDRRef));
			m_id = (int) db.insert("ZayavkaPokupatelyaIskhodyaschaya_Traphiki", null, values);
			mNew = false;
		}
		else {
			values.put("Kolichestvo", mKolichestvo);
			values.put("Data", DateTimeHelper.SQLDateString(mData));
			values.put("Kommentariy", mKommentariy);
			values.put("vs", vetSpravka ? 1 : 0);
			db.update("ZayavkaPokupatelyaIskhodyaschaya_Traphiki", values, "_id=" + String.valueOf(m_id), null);
		}
	}
	public Date getData() {
		return mData;
	}
	public void setData(Date data) {
		mData = data;
	}
	public String getKommentariy() {
		return mKommentariy;
	}
	public void setKommentariy(String kommentariy) {
		mKommentariy = kommentariy;
	}
}
