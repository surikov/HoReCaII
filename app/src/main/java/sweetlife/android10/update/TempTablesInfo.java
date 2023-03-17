package sweetlife.android10.update;

import java.util.Hashtable;

import sweetlife.android10.database.FLAGS;


public class TempTablesInfo {

	private long mTempTablesFlags = FLAGS.FLAG_ALL;
	
	private Hashtable<String, Long> mTempTablesList;
	
	public TempTablesInfo() {

		mTempTablesList = new Hashtable<String, Long>();

		mTempTablesList.put("InformationRegisterRecordSet.МаршрутыАгентов", FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_DAYS | FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_CONTRACTS);
		mTempTablesList.put("CatalogObject.Контрагенты", FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_DAYS | FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_CONTRACTS);
		mTempTablesList.put("CatalogObject.ДоговорыКонтрагентов", FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_CONTRACTS);
		mTempTablesList.put("InformationRegisterRecordSet.Лимиты", FLAGS.FLAG_TEMP_LIMITY);
	}
	
	public void checkTable( String tableName ) {
		
		Long flag = mTempTablesList.get(tableName);
		
		if( flag != null ) {
			
			setTempTablesFlags(flag);
		}
	}
	
	public long getTempTablesFlags() {
	
		return mTempTablesFlags;
	}

	public void setTempTablesFlags(long tempTablesFlags) {
		
		mTempTablesFlags += tempTablesFlags;
	}
}
