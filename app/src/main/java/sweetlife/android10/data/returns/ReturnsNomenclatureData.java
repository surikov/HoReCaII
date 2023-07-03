package sweetlife.android10.data.returns;

import java.util.Calendar;

import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.database.Request_ReturnsNomenclature;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReturnsNomenclatureData extends NomenclatureBasedDocumentItems {
	public ReturnsNomenclatureData(SQLiteDatabase db, NomenclatureBasedDocument zayavka) {
		super(db, zayavka);
		Cursor cursor = Request_ReturnsNomenclature.RequestNomenclature(db, mZayavka.getIDRRef());
		if (cursor != null && cursor.moveToFirst()) {
			ReadFromDataBase(cursor, db);
			cursor.close();
		}
	}

	protected void ReadFromDataBase(Cursor cursorReturnsNomenclature, SQLiteDatabase db) {
		ZayavkaNaVozvrat_Tovary nomenclature;
		int nomerStroki;
		do {
			nomerStroki = Request_ReturnsNomenclature.getNomerStroki(cursorReturnsNomenclature);
			if (mNomenclatureNumber < nomerStroki) {
				mNomenclatureNumber = nomerStroki;
			}
			Calendar billDate = Calendar.getInstance();
			billDate.setTime(Request_ReturnsNomenclature.getDataNakladnoy(cursorReturnsNomenclature));
			nomenclature = new ZayavkaNaVozvrat_Tovary(Request_ReturnsNomenclature.get_id(cursorReturnsNomenclature), nomerStroki,
					Request_ReturnsNomenclature.getNomenklatura(cursorReturnsNomenclature), Request_ReturnsNomenclature.getArtikul(cursorReturnsNomenclature),
					Request_ReturnsNomenclature.getNomenklaturaNaimenovanie(cursorReturnsNomenclature), Request_ReturnsNomenclature.getKolichestvo(cursorReturnsNomenclature),
					Request_ReturnsNomenclature.getNomerNakladnoy(cursorReturnsNomenclature), billDate, Request_ReturnsNomenclature.getPrichina(cursorReturnsNomenclature),
					Request_ReturnsNomenclature.getAktPretenziy(cursorReturnsNomenclature), mZayavka.getIDRRef(), false //,""
					//,""
			);
			mNomenclaureList.add(nomenclature);
		}
		while (cursorReturnsNomenclature.moveToNext());
	}

	public void newReturnsNomenclature(String nomenklaturaID//
			, String artikul//

			, String nomenklaturaNaimenovanie//
			, int prichina//
	) {
		mNomenclaureList.add(new ZayavkaNaVozvrat_Tovary(0, ++mNomenclatureNumber//
				, nomenklaturaID, artikul, nomenklaturaNaimenovanie//
				, 0, null, null, prichina, null, mZayavka.getIDRRef(), true//,""
				//,""
		));
	}

	public ZayavkaNaVozvrat_Tovary getNomenclature(int index) {
		return (ZayavkaNaVozvrat_Tovary) mNomenclaureList.get(index);
	}

	public boolean IsAllDataFilled() {
		for (NomenclatureBasedItem item : mNomenclaureList) {
			ZayavkaNaVozvrat_Tovary tovar = (ZayavkaNaVozvrat_Tovary) item;
			if (tovar.getDataNakladnoy() == null || tovar.getKolichestvo() == 0 || tovar.getNomerNakladnoy() == null || tovar.getPrichina() == 0) {
				return false;
			}
		}
		return true;
	}

	public void WriteToDataBase(SQLiteDatabase db) {
		db.beginTransactionNonExclusive();
		try {
			for (NomenclatureBasedItem item : mNomenclaureList) {
				item.setToDataBase(db);
			}
			for (Integer id : mIDsForDelete) {
				db.execSQL("delete from ZayavkaNaVozvrat_Tovary where _id = " + id.toString());
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void Remove(ZayavkaNaVozvrat_Tovary item) {
		if (!item.isNew()) {
			mIDsForDelete.add(item.get_id());
		}
		mNomenclaureList.remove(item);
	}
}
