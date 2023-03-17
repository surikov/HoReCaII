package sweetlife.android10.data.fixedprices;

import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.database.Request_FixedPriceNomenclature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class FixedPricesNomenclatureData extends NomenclatureBasedDocumentItems{

	public FixedPricesNomenclatureData( SQLiteDatabase db, ZayavkaNaSkidki zayavka ) {
		super(db, zayavka);

		Cursor cursor = Request_FixedPriceNomenclature.RequestNomenclature(db, mZayavka.getIDRRef());

		if( cursor != null && cursor.moveToFirst() ) {

			ReadFromDataBase( cursor,db );

			cursor.close();
		}
	}

	protected void ReadFromDataBase( Cursor cursorReturnsNomenclature ,SQLiteDatabase db) {

		ZayavkaNaSkidki_TovaryPhiksCen nomenclature;

		int nomerStroki;

		do {

			nomerStroki = Request_FixedPriceNomenclature.getNomerStroki(cursorReturnsNomenclature);

			if( mNomenclatureNumber < nomerStroki ) {

				mNomenclatureNumber = nomerStroki;
			}

			nomenclature = new ZayavkaNaSkidki_TovaryPhiksCen( 
					Request_FixedPriceNomenclature.get_id(cursorReturnsNomenclature),     
					nomerStroki,                                                       
					Request_FixedPriceNomenclature.getNomenklatura(cursorReturnsNomenclature),      
					Request_FixedPriceNomenclature.getArtikul(cursorReturnsNomenclature),               
					Request_FixedPriceNomenclature.getNomenklaturaNaimenovanie(cursorReturnsNomenclature),      
					Request_FixedPriceNomenclature.getCena(cursorReturnsNomenclature),             
					Request_FixedPriceNomenclature.getObyazatelstva(cursorReturnsNomenclature),      
					mZayavka.getIDRRef(),                                           
					false //,"",""
					);

			mNomenclaureList.add(nomenclature);
		}
		while( cursorReturnsNomenclature.moveToNext() );

	}

	public void newFixedPriceNomenclature(String nomenklaturaID,
			String artikul,
			String nomenklaturaNaimenovanie) {

		mNomenclaureList.add( new ZayavkaNaSkidki_TovaryPhiksCen( 0,
				++mNomenclatureNumber,
				nomenklaturaID,
				artikul,
				nomenklaturaNaimenovanie,
				0,
				0,
				mZayavka.getIDRRef(),
				true//,"",""
				) );	
	}

	public ZayavkaNaSkidki_TovaryPhiksCen getNomenclature( int index ) {

		return (ZayavkaNaSkidki_TovaryPhiksCen) mNomenclaureList.get(index);
	}

	public boolean IsAllDataFilled() {

		for( NomenclatureBasedItem item : mNomenclaureList ) {

			if( ((ZayavkaNaSkidki_TovaryPhiksCen)item).getCena() == 0 ) {

				return false;
			}
		}		
		return true;
	}

	public void WriteToDataBase(SQLiteDatabase db) {

		db.beginTransactionNonExclusive();

		try {

			for( NomenclatureBasedItem item : mNomenclaureList ) {

				item.setToDataBase(db);
			}

			for( Integer id : mIDsForDelete ) {

				db.execSQL( "delete from ZayavkaNaSkidki_TovaryPhiksCen where _id = " + id.toString() );
			}

			db.setTransactionSuccessful();
		}
		finally {

			db.endTransaction();
		}
	}
}
