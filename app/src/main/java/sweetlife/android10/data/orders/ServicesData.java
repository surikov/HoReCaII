package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.database.Request_ServicesList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ServicesData extends NomenclatureBasedDocumentItems {

	public ServicesData(SQLiteDatabase db, NomenclatureBasedDocument zayavka) {
		super(db, zayavka);

		Cursor cursor = Request_ServicesList.Request(db, mZayavka.getIDRRef());

		if (cursor != null && cursor.moveToFirst()) {

			ReadFromDataBase(cursor, db);

			cursor.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void ReadFromDataBase(Cursor cursor, SQLiteDatabase db) {

		ZayavkaPokupatelya_Service service;

		int nomerStroki;

		do {

			nomerStroki = Request_ServicesList.getNomerStroki(cursor);

			if (mNomenclatureNumber < nomerStroki) {

				mNomenclatureNumber = nomerStroki;
			}

			service = new ZayavkaPokupatelya_Service(
					Request_ServicesList.get_id(cursor),
					nomerStroki,
					Request_ServicesList.getNomenklaturaID(cursor),
					Request_ServicesList.getArtikul(cursor),
					Request_ServicesList.getNomenklaturaNaimenovanie(cursor),
					mZayavka.getIDRRef(),
					Request_ServicesList.getSoderganie(cursor),
					Request_ServicesList.getKolichestvo(cursor),
					Request_ServicesList.getCena(cursor),
					Request_ServicesList.getSumma(cursor),
					false// ,"",""
			);

			mNomenclaureList.add(service);
		}
		while (cursor.moveToNext());
	}

	public void newService(String nomenklaturaID,
						   String artikul,
						   String nomenklaturaNaimenovanie,
						   String soderganie,
						   double cena,
						   double kolichestvo) {

		mNomenclaureList.add(new ZayavkaPokupatelya_Service(0,
				++mNomenclatureNumber,
				nomenklaturaID,
				artikul,
				nomenklaturaNaimenovanie,
				mZayavka.getIDRRef(),
				soderganie,
				kolichestvo,
				cena,
				kolichestvo * cena,
				true//,"",""
		));
	}

	public ZayavkaPokupatelya_Service getService(int index) {

		return (ZayavkaPokupatelya_Service) mNomenclaureList.get(index);
	}

	@Override
	public void WriteToDataBase(SQLiteDatabase db) {

		db.beginTransactionNonExclusive();

		try {

			for (NomenclatureBasedItem item : mNomenclaureList) {

				item.setToDataBase(db);
			}

			for (Integer id : mIDsForDelete) {

				db.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Uslugi where _id = " + id.toString());
			}


			db.setTransactionSuccessful();

		} finally {

			db.endTransaction();

		}
	}

	@Override
	public boolean IsAllDataFilled() {

		return true;
	}

	public double getAmount() {

		double amount = 0;

		for (NomenclatureBasedItem item : mNomenclaureList) {

			amount += ((ZayavkaPokupatelya_Service) item).getSumma();
		}
		return amount;
	}
}
