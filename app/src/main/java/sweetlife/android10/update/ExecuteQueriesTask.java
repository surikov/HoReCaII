package sweetlife.android10.update;

import java.util.TimerTask;

import sweetlife.android10.consts.ISQLConsts;
import sweetlife.android10.consts.IUpdaterConsts;
import sweetlife.android10.update.QueryInfo.QueryType;

import android.database.sqlite.SQLiteDatabase;


public class ExecuteQueriesTask extends TimerTask implements ISQLConsts, IUpdaterConsts {

	SQLiteDatabase mDB;
	DeltaUpdater   mDeltaUpdater;

	QueriesList mQueriesList = null;

	boolean mHasErrors = false;

	public boolean HasErrors() {

		return mHasErrors;
	}

	ExecuteQueriesTask( SQLiteDatabase db, DeltaUpdater deltaUpdater ) {

		mDB = db; 
		mDeltaUpdater = deltaUpdater;
	}

	@Override
	public void run() {
		//try{
		//int tid=android.os.Process.myTid();
		//System.out.println(this.getClass().getCanonicalName()+": priority before change = " + android.os.Process.getThreadPriority(tid));
		//System.out.println(this.getClass().getCanonicalName()+": priority before change = "+Thread.currentThread().getPriority());
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);		
		//System.out.println(this.getClass().getCanonicalName()+": priority after change = " + android.os.Process.getThreadPriority(tid));
		//System.out.println(this.getClass().getCanonicalName()+": priority after change = " + Thread.currentThread().getPriority());
		//}catch(Throwable t){
			//System.out.println(this.getClass().getCanonicalName()+": "+t.getMessage());
		//}
		

		if( NeedExecuteQueries() ) {

			ExecuteQueries();
		}
	}

	private boolean NeedExecuteQueries() {

		boolean isParsingComplete = mDeltaUpdater.getDeltaParser().IsComplete();

		mQueriesList = mDeltaUpdater.getQueriesList();

		int queriesCount = mQueriesList.getCount();

		if( !isParsingComplete && queriesCount < TASK_COUNT_IN_TRANSACTION ) {

			return false;
		}

		return true; 
	}

	public void ExecuteQueries() {
//System.out.println("ExecuteQueries next "+TASK_COUNT_IN_TRANSACTION);
		try {

			int queriesCount = mQueriesList.getCount();

			QueryInfo query = null;
			QueryInfo.QueryType type = QueryType.qtNONE;

			int currentQueryIndex = 0;
			long queryResult = -1;

			mDB.beginTransaction();

			for( currentQueryIndex = 0; currentQueryIndex < queriesCount; currentQueryIndex++ ) {

				try { 

					query = mQueriesList.poll();
					type  = query.getType();
					//System.out.println("query.getTableName() "+query.getTableName());
					if( type == QueryType.qtUPDATE ) {

						queryResult = mDB.update(query.getTableName(), query.getValues(), query.getWhereCondition(), null);
						//System.out.println("queryResult "+queryResult);
						if( queryResult <= 0 ) {

							type = QueryType.qtINSERT;
						}
					}
					if( type == QueryType.qtINSERT ) {

						queryResult = mDB.insert(query.getTableName(), null, query.getValues());
					}
//					if( type == QueryType.qtDELETE ) {
//
//						queryResult = mDB.delete(query.getTableName(), query.getWhereCondition(), null);
//					}
					if( queryResult <= 0 ) {

						mHasErrors = true;
					}

					query.setWhereCondition(null);
					query.setTableName(null);
					query.clearValues();
					query = null;
				}
				catch (Exception e) {
					e.printStackTrace();
					mHasErrors = true;
				}
			}

			mDB.setTransactionSuccessful();
		}
		catch(Exception e) {

			mHasErrors = true;
			e.printStackTrace();
		}
		finally {
			mDB.endTransaction();
		} 
		//System.out.println("done ExecuteQueries");
	}
}
