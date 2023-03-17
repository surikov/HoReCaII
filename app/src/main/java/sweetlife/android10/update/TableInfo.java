package sweetlife.android10.update;

import java.util.Hashtable;

public class TableInfo {

	private String                       mSQLTableName;
	private Hashtable<String,TableField> mFields;

	public TableInfo() {

		mFields = new Hashtable<String,TableField>();
	}

	public String getSQLTableName() {
		
		return mSQLTableName;
	}

	public void setSQLTableName(String sqlTableName) {
		
		mSQLTableName = sqlTableName;
	}

	public Hashtable<String,TableField> getFields() {
		
		return mFields;
	}

	public void setFields(Hashtable<String,TableField> fields) {
		
		mFields = fields;
	}
}
