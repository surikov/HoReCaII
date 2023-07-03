package sweetlife.android10.data.orders;

import java.util.Date;

import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.database.Request_TrafiksList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TraficsData extends NomenclatureBasedDocumentItems {

	public TraficsData(SQLiteDatabase db, NomenclatureBasedDocument zayavka) {
		super(db, zayavka);

		Cursor cursor = Request_TrafiksList.Request(db, mZayavka.getIDRRef());

		if (cursor != null && cursor.moveToFirst()) {

			ReadFromDataBase(cursor, db);

			//cursor.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void ReadFromDataBase(Cursor cursor, SQLiteDatabase db) {

		ZayavkaPokupatelya_Trafik trafik;

		int nomerStroki;

		do {

			nomerStroki = Request_TrafiksList.getNomerStroki(cursor);

			if (mNomenclatureNumber < nomerStroki) {

				mNomenclatureNumber = nomerStroki;
			}

			trafik = new ZayavkaPokupatelya_Trafik(
					Request_TrafiksList.get_id(cursor),
					nomerStroki,
					Request_TrafiksList.getNomenklaturaID(cursor),
					Request_TrafiksList.getArtikul(cursor),
					Request_TrafiksList.getNomenklaturaNaimenovanie(cursor),
					mZayavka.getIDRRef(),
					Request_TrafiksList.getEdinicaIzmereniyaID(cursor),
					Request_TrafiksList.getEdinicaIzmereniya(cursor),
					Request_TrafiksList.getKolichestvo(cursor),
					Request_TrafiksList.getData(cursor),
					Request_TrafiksList.getKommentariy(cursor),
					Request_TrafiksList.getMinNorma(cursor),
					Request_TrafiksList.getKoefficientMest(cursor),
					false //,"",""
					, Request_TrafiksList.getVS(cursor)
			);

			mNomenclaureList.add(trafik);
		}
		while (cursor.moveToNext());
	}

	public ZayavkaPokupatelya_Trafik getTrafik(int index) {

		return (ZayavkaPokupatelya_Trafik) mNomenclaureList.get(index);
	}

	public void newTrafik(String nomenklaturaID,
						  String artikul,
						  String nomenklaturaNaimenovanie,
						  String edinicaIzmereniyaID,
						  String edinicaIzmereniyaName,
						  double kolichestvo,
						  double minNorma,
						  double koefMest,
						  Date date,
						  String comment) {

		mNomenclaureList.add(new ZayavkaPokupatelya_Trafik(
				0,
				++mNomenclatureNumber,
				nomenklaturaID,
				artikul,
				nomenklaturaNaimenovanie,
				mZayavka.getIDRRef(),
				edinicaIzmereniyaID,
				edinicaIzmereniyaName,
				minNorma,
				date,
				comment,
				minNorma,
				koefMest,
				true// ,"",""
				, false
		));
	}

	@Override
	public void WriteToDataBase(SQLiteDatabase db) {
		//System.out.println("WriteToDataBase "+mNomenclaureList);
		db.beginTransactionNonExclusive();

		try {

			for (NomenclatureBasedItem item : mNomenclaureList) {
				//System.out.println("WriteToDataBase "+item.getArtikul()+ " "+item.getNomenklaturaNaimenovanie());
				item.setToDataBase(db);
			}

			for (Integer id : mIDsForDelete) {

				db.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Traphiki where _id = " + id.toString());
			}

			db.setTransactionSuccessful();

		} finally {

			db.endTransaction();

		}
	}

	@Override
	public boolean IsAllDataFilled() {

		for (NomenclatureBasedItem item : mNomenclaureList) {

			if (((ZayavkaPokupatelya_Trafik) item).getData() == null) {

				return false;
			}
		}
		return true;
	}

	public boolean IsTrafikAlreadyInList(String nomenklatureID) {

		for (NomenclatureBasedItem item : mNomenclaureList) {

			if (item.getNomenklaturaID().toUpperCase()
					.compareToIgnoreCase(nomenklatureID.toUpperCase()) == 0) {

				return true;
			}
		}

		return false;
	}
}
