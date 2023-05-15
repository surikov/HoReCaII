package sweetlife.android10.data.common;

import java.util.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class NomenclatureBasedDocumentItems {

	protected NomenclatureBasedDocument mZayavka;
	protected Integer mNomenclatureNumber = 0;
	public ArrayList<NomenclatureBasedItem> mNomenclaureList;
	protected ArrayList<Integer> mIDsForDelete;

	public NomenclatureBasedDocumentItems(SQLiteDatabase db, NomenclatureBasedDocument zayavka) {

		mIDsForDelete = new ArrayList<Integer>();

		mNomenclaureList = new ArrayList<NomenclatureBasedItem>();

		mZayavka = zayavka;
	}

	protected abstract void ReadFromDataBase(Cursor cursorReturnsNomenclature, SQLiteDatabase db);

	public abstract void WriteToDataBase(SQLiteDatabase db);

	public abstract boolean IsAllDataFilled();

	public boolean IsNomenclatureAlreadyInList(String nomenklatureID) {

		for (NomenclatureBasedItem tovar : mNomenclaureList) {

			if (tovar.getNomenklaturaID().toUpperCase()
					.compareToIgnoreCase(nomenklatureID.toUpperCase()) == 0) {

				return true;
			}
		}

		return false;
	}

	public String artikul(Integer index) {
		NomenclatureBasedItem item = mNomenclaureList.get(index);
		return item.getArtikul();
	}

	public void Remove(Integer index) {
		//System.out.println(this.getClass().getCanonicalName()+".Remove "+index);
		NomenclatureBasedItem item = mNomenclaureList.get(index);

		if (!item.isNew()) {

			mIDsForDelete.add(item.get_id());
		}

		mNomenclaureList.remove(item);
	}

	public int getCount() {

		return mNomenclaureList.size();
	}

}
