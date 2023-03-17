package sweetlife.android10.update;

import android.content.ContentValues;
import java.util.*;
import sweetlife.android10.utils.Hex;

public class QueryInfo {
	public enum QueryType {
		qtNONE, qtINSERT, qtUPDATE, qtDELETE
	}

	private String mTableName;
	private QueryType mType;
	private ContentValues mValues;
	private String mWhereCondition;

	QueryInfo() {
		mTableName = null;
		mType = QueryType.qtNONE;
		mValues = new ContentValues();
		mWhereCondition = null;
	}
	protected void finalize() throws Throwable {
		mType = null;
		mValues.clear();
		mWhereCondition = null;
		mTableName = null;
	};
	QueryInfo(String tableName, QueryType type, ContentValues values, String whereCondition) {
		mTableName = tableName;
		mType = type;
		mValues = new ContentValues();
		if (values != null) {
			mValues.putAll(values);
		}
		mWhereCondition = whereCondition;
	}
	public String getTableName() {
		return mTableName;
	}
	public void setTableName(String tableName) {
		mTableName = tableName;
	}
	public QueryType getType() {
		return mType;
	}
	public void setType(QueryType type) {
		mType = type;
	}
	public ContentValues getValues() {
		return mValues;
	}
	public boolean IsReady() {
		if (mType == QueryType.qtNONE || mTableName == null) {
			return false;
		}
		if (mWhereCondition == null && mType == QueryType.qtDELETE) {
			return false;
		}
		if (mValues.size() == 0 && mType != QueryType.qtDELETE) {
			return false;
		}
		return true;
	}
	public void clearValues() {
		mValues.clear();
	}
	public QueryInfo clone() {
		if (IsReady()) {
			QueryInfo query = new QueryInfo(mTableName, mType, mValues, mWhereCondition);
			return query;
		}
		return null;
	}
	public String getWhereCondition() {
		return mWhereCondition;
	}
	public void setWhereCondition(String whereCondition) {
		mWhereCondition = whereCondition;
	}
	public void dump() {
		//Hex.decodeHex(fakePriceVladelecKey)
		//System.out.print( mType + ", mTableName: " + mTableName + ", mWhereCondition: " + mWhereCondition+"; values ");
		Iterator<String> i = mValues.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			String value = mValues.getAsString(key);
			if(key.startsWith("[B@")){
				value =Hex.encodeHex(
				mValues.getAsByteArray(key));
			}
			
			//System.out.print(": " + key + "=" + value);
		}
		Set<String> set = mValues.keySet();
		//for(int i=0;i<set.size();i++){
		//foreach(s in)
		//System.out.print(": "+mValues.g
		//}
		//System.out.println();
	}
}
