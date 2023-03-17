package sweetlife.android10.update;

import java.util.Hashtable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TablesInfo {
	private Hashtable<String, TableInfo> mRelationsTables;
	private SQLiteDatabase mDB;
	private boolean mIsDataLoaded;

	TablesInfo(SQLiteDatabase db) {
		mDB = db;
		setIsDataLoaded(false);
		mRelationsTables = new Hashtable<String, TableInfo>();
		FillRaletionsTables();
	}
	public Hashtable<String, TableInfo> getRelationsTables() {
		return mRelationsTables;
	}
	public void setRelationsTables(Hashtable<String, TableInfo> relationsTables) {
		
		mRelationsTables = relationsTables;
	}
	public TableInfo getTable(String tableName) {
		//System.out.println("getTable '"+tableName+"'");
		return mRelationsTables.get(tableName);
	}
	private void FillRaletionsTables() {
		Cursor cursorTable = mDB.rawQuery("select _id, UpdateCategoryName || '.' || [1cName] [Name], [sqliteName] from _RelationsTableHelper", null);
		Cursor cursorField = null;
		if (cursorTable != null && cursorTable.moveToFirst()) {
			do {
				TableInfo fields = new TableInfo();
				fields.setSQLTableName(cursorTable.getString(2));
				//System.out.println("FillRaletionsTables "+cursorTable.getString(0)+"/"+cursorTable.getString(1)+"/"+cursorTable.getString(2));
				cursorField = mDB.rawQuery("select Update1cName [Name], [sqliteName], [sqlFieldType] from _RelationsFieldsHelper where _tableId = ?", new String[] { cursorTable.getString(0) });
				if (cursorField != null && cursorField.moveToFirst()) {
					do {
						TableField valueTableEntry = new TableField(cursorField.getInt(2), cursorField.getString(1));
						fields.getFields().put(cursorField.getString(0), valueTableEntry);
					}
					while (cursorField.moveToNext());
					cursorField.close();
					cursorField = null;
				}
				//System.out.println("mRelationsTables.put '"+cursorTable.getString(1)+"'");
				mRelationsTables.put(cursorTable.getString(1), fields);
			}
			while (cursorTable.moveToNext());
			cursorTable.close();
			cursorTable = null;
		}
		setIsDataLoaded(true);
	}
	public boolean IsDataLoaded() {
		return mIsDataLoaded;
	}
	public void setIsDataLoaded(boolean isDataLoaded) {
		mIsDataLoaded = isDataLoaded;
	}
}
