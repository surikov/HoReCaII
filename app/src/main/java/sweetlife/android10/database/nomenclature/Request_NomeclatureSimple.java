package sweetlife.android10.database.nomenclature;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_NomeclatureSimple implements ITableColumnsNames, ISearchBy {
	public static Cursor RequestNomenlatureGroupsWithoutParent(SQLiteDatabase db) {
		return RequestNomenlatureGroupsWithParent(db, "x'00'");
	}
	public static Cursor RequestNomenlatureGroupsWithParent(SQLiteDatabase db, String parent) {
		String sqlString = "select n._id, n._IDRRef, n.Naimenovanie as Naimenovanie from Nomenklatura n"//
				+ " left join Nomenklatura  ch on ch.roditel=n._idrref"//
		//String sqlString = "select n._id, n._IDRRef, n.Naimenovanie as Naimenovanie from Nomenklatura_sorted n"//
		//		+ " left join Nomenklatura_sorted  ch on ch.roditel=n._idrref"//

				+ " where n.EtoGruppa=x'01' and n.Roditel = " + parent //
				+ " group by n._IDRRef"//
				+ " having count(ch._idrref)>0"//
				+ " order by n.Naimenovanie "//
		;
		System.out.println("Request_NomeclatureSimple.RequestNomenlatureGroupsWithParent - " + sqlString);
		return db.rawQuery(sqlString, null);
	}
	public static Cursor RequestNomenclatureByParent(SQLiteDatabase db, String parent) {
		String sqlString = "select n._id, n._IDRRef [_IDRRef],n.Artikul [Artikul], n.Naimenovanie [Naimenovanie] " //
				+ "\n\t ,p.[_IDRRef] [OsnovnoyProizvoditel] "//
				+ "\n\t  ,p.[Naimenovanie] [ProizvoditelNaimenovanie] from Nomenklatura n " //
				+ "\n\t  left join [Proizvoditel] p on p.[_IDRRef] = n.OsnovnoyProizvoditel "//
				+ "\n\t  where EtoGruppa=x'00' and Roditel = " + parent //
				+ "\n\t  group by n._IDRRef"//
				+ "\n\t  order by Naimenovanie "//
		;
		//System.out.println("--------Request_NomeclatureSimple.RequestNomenclatureByParent.Request " + sqlString);
		return db.rawQuery(sqlString, null);
	}
	public static Cursor RequestNomenclatureBySearchString(SQLiteDatabase db, String searchString, int searchBy) {
		String sqlString = "select n._id, n._IDRRef [_IDRRef],n.Artikul [Artikul], n.Naimenovanie [Naimenovanie] " //
				//+ ",p.[_IDRRef] [OsnovnoyProizvoditel] "//
				//+ ",x'00' as [OsnovnoyProizvoditel] " + ",p.[Naimenovanie] [ProizvoditelNaimenovanie] from Nomenklatura_sorted n " //
				+ ",x'00' as [OsnovnoyProizvoditel] " + ",p.[Naimenovanie] [ProizvoditelNaimenovanie] from Nomenklatura n " //

				+ " left join [Proizvoditel] p on p.[_IDRRef] = n.OsnovnoyProizvoditel "//
				+ " where n.EtoGruppa=x'00' "//
				//+ " where 1=1 "//
				
				
		;
		if (searchBy == SEARCH_ARTICLE) {
			sqlString = sqlString + " and ( n.[Artikul] = '" + searchString + "' )";
		}
		else {
			if (searchBy == SEARCH_NAME) {
				sqlString = sqlString + " and ( n.[UpperName] like '%" + searchString.toUpperCase() + "%')";
			}
			else {
				if (searchBy == SEARCH_IDRREF) {
					sqlString = sqlString + " and ( n.[_IDRRef] = " + searchString + " )";
				}
				else {
					if (searchBy == SEARCH_VENDOR) {
						sqlString = sqlString + " and ( p.[Naimenovanie] like '" + searchString + "' || '%')";
					}
				}
			}
		}
		sqlString = sqlString + " group by n._IDRRef";
		//System.out.println("RequestNomenclatureBySearchString.Request " + sqlString);
		Cursor c = db.rawQuery(sqlString, null);
		//System.out.println("RequestNomenclatureBySearchString.Request done");
		return c;
	}
	public static int get_id(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(ID));
	}
	public static String getIDRRef(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(IDRREF)));
	}
	public static String getArtikul(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(ARTIKUL));
	}
	public static String getNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(NAIMENOVANIE));
	}
	public static String getProizvoditelID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(OSNOVNOY_PROIZVODITEL)));
	}
	public static String getProizvoditelNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(PROIZVODITEL_NAIMENOVANIE));
	}
}
