package sweetlife.android10.update;

import java.util.concurrent.LinkedBlockingQueue;

import android.database.sqlite.SQLiteDatabase;
import sweetlife.android10.consts.IUpdaterConsts;
import sweetlife.android10.update.QueryInfo.QueryType;
import java.util.*;

public class QueriesList {
	int pollCounter = 0;
	int putCounter = 0;
	long analizeCounter = 0;
	//long vacuumCounter = 0;
	long startTime = new Date().getTime();
	int warnPollCounter = 0;
	int warnPutCounter = 0;
	int step = 213;
	public long coarseRecordCount = 0;
	private static LinkedBlockingQueue<QueryInfo> mQueries;
	UpdateTask updateTask;
	private SQLiteDatabase mDB;
	boolean mHasErrors = false;
	QueriesList(UpdateTask ut, SQLiteDatabase db) {
		//this.fileSize=fileSize;
		this.updateTask = ut;
		mQueries = new LinkedBlockingQueue<QueryInfo>();
		mDB = db;
		analizeCounter = new Date().getTime();
		//vacuumCounter = new Date().getTime();
	}
	void inform() {
		try {
			int min = putCounter;
			if (min > pollCounter) {
				min = pollCounter;
			}
			//restCounter = restCounter + min;
			//long coarse = fileSize / 700;
			int part = (int) (100 * min / coarseRecordCount);
			if (part > 99) {
				part = 99;
			}
			long nowTime = new Date().getTime();
			int minut = (int) ((nowTime - startTime) / 60000);
			updateTask.logAndPublishProcess("Обновлено " + part + "% (" + putCounter
			//+ "/" + pollCounter 
					+ " записей, " + minut + " мин."
					//+preTable
					+")");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	public synchronized void _doVacuum() {
		//System.out.println("start vacuum");
		try {
			mDB.execSQL("	vacuum;	");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		//System.out.println("done vacuum");
	}
	public synchronized void doAnalize() {
		//System.out.println("start rest");
		inform();
		try {

			mDB.execSQL("	analyze;	");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		//System.out.println("done rest");
	}
	public synchronized void putQuery(QueryInfo info) throws InterruptedException {
		putCounter++;
		if (putCounter > warnPutCounter + step) {
			warnPutCounter = putCounter;
			inform();
			//LogHelper.debug("put "+putCounter+" records");
		}
		mQueries.put(info);
		if (getCount() > IUpdaterConsts.TASK_COUNT_IN_TRANSACTION) {
			ExecuteQueries();
		}
		analizeCounter++;
		if (analizeCounter + 3 * 60 * 1000 < new Date().getTime()) {
			analizeCounter = new Date().getTime();
			doAnalize();
			analizeCounter = new Date().getTime();
		}
		/*vacuumCounter++;
		if (vacuumCounter + 3 * 60 * 1000 < new Date().getTime()) {
			vacuumCounter = new Date().getTime();
			//vacuumCounter = 0;
			doVacuum();
			vacuumCounter = new Date().getTime();
		}*/
	}
	public synchronized int getCount() {
		return mQueries.size();
	}
	public synchronized QueryInfo poll() {
		pollCounter++;
		//if (pollCounter > warnPollCounter + step) {
		//warnPollCounter = pollCounter;
		//updateTask.logAndPublishProcess("Прочитано " + putCounter + ", записано " + pollCounter + " записей");
		//LogHelper.debug("poll "+pollCounter+" records");
		//inform();
		//}
		QueryInfo qi = mQueries.poll();
		/*if (qi.getTableName().equals("DogovoryKontragentov")) {
			qi.dump();
		}*/
		return qi;
	}
	public void ExecuteQueries() {
		try {
			int queriesCount = getCount();
			//System.out.println("ExecuteQueries start "+queriesCount);
			QueryInfo query = null;
			QueryInfo.QueryType type = QueryType.qtNONE;
			int currentQueryIndex = 0;
			long queryResult = -1;
			mDB.beginTransaction();
			for (currentQueryIndex = 0; currentQueryIndex < queriesCount; currentQueryIndex++) {
				try {
					query = poll();
					if (query.getTableName().toLowerCase().equals("podrazdeleniya")) {
						//ContentValues cv=query.getValues();
						byte[] b = query.getValues().getAsByteArray("[EtoGruppa]");
						if (b != null) {
							for (int i = 0; i < b.length; i++) {
								//System.out.println(b[i] + "/ ");
							}
						}
						//System.out.print(" ");
						//System.out.println("mark "+query.getValues().getAsString("[EtoGruppa]"));
						//System.out.println("mark "+query.getValues().getAsByteArray("EtoGruppa").length);
						query.dump();
					}
					type = query.getType();
					//query.dump();
					if (type == QueryType.qtUPDATE) {
						queryResult = mDB.update(query.getTableName(), query.getValues(), query.getWhereCondition(), null);
						if (queryResult <= 0) {
							type = QueryType.qtINSERT;
						}
					}
					if (type == QueryType.qtINSERT) {
						queryResult = mDB.insert(query.getTableName(), null, query.getValues());
					}
					//					if( type == QueryType.qtDELETE ) {
					//
					//						queryResult = mDB.delete(query.getTableName(), query.getWhereCondition(), null);
					//					}
					if (queryResult <= 0) {
						mHasErrors = true;
					}
					query.setWhereCondition(null);
					query.setTableName(null);
					query.clearValues();
					query = null;
				} catch (Exception e) {
					e.printStackTrace();
					mHasErrors = true;
				}
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			mHasErrors = true;
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
		//System.out.println("ExecuteQueries done");
	}
}
