package sweetlife.android10.update;

import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import sweetlife.android10.consts.IAppConsts;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DeltaUpdater implements IAppConsts {
	private Handler mParentHandler;
	private SQLiteDatabase mDB;
	private DeltaParser mDeltaParser;
	private TempTablesInfo mTempTablesInfo;
	private TablesInfo mTablesInfo;
	private QueriesList mQueriesList;

	//ExecuteQueriesTask mExecuteQueriesTask;
	public DeltaUpdater(SQLiteDatabase db, UpdateTask ut) {
		mDB = db;
		mQueriesList = new QueriesList(ut, mDB);
		mTablesInfo = new TablesInfo(mDB);
		mTempTablesInfo = new TempTablesInfo();
		mDeltaParser = new DeltaParser(mTablesInfo, mQueriesList, mDB, mTempTablesInfo);
		//mExecuteQueriesTask = new ExecuteQueriesTask(mDB, this);
	}
	public void setParentHandler(Handler parentHandler) {
		mParentHandler = parentHandler;
	}
	public synchronized DeltaParser getDeltaParser() {
		return mDeltaParser;
	}
	public synchronized QueriesList getQueriesList() {
		return mQueriesList;
	}
	public synchronized TempTablesInfo getTempTablesInfo() {
		return mTempTablesInfo;
	}
	private void parse(InputSource source) throws Exception {
		//System.out.println(this.getClass().getCanonicalName()+" parse");
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(mDeltaParser);
		//System.out.println(this.getClass().getCanonicalName()+" start");
		xr.parse(source);
		//System.out.println(this.getClass().getCanonicalName()+" done "+mParentHandler);
		if (mParentHandler != null) {
			Message message = new Message();
			Bundle messageData = new Bundle();
			messageData.putBoolean(MSG_PARSE_ENDED, true);
			message.setData(messageData);
			mParentHandler.sendMessage(message);
		}
		//System.out.println(this.getClass().getCanonicalName()+" end");
	}
	public void parse(String sPathToXMLFile) throws Exception {
		java.io.File f = new java.io.File(sPathToXMLFile);
		int coarseSize = 100;
		mQueriesList.coarseRecordCount = f.length() / coarseSize;
		FileInputStream fis = new FileInputStream(sPathToXMLFile);
		InputSource is = new InputSource(fis);
		parse(is);
	}
	/*
		public Data readData1(InputSource source) throws Exception {

			Data data = null;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			XMLReader xr = sp.getXMLReader();

			m_dataHandler = new DataHandler();
			xr.setContentHandler(m_dataHandler);

			xr.parse(source);

			data = m_dataHandler.getData();

			if( mParentHandler != null ) {

				Message message = new Message();
				Bundle messageData = new Bundle();

				messageData.putBoolean(MSG_PARSE_ENDED, true);
				message.setData(messageData);

				mParentHandler.sendMessage(message);
			}

			return data;
		}

		public Data readData1(String sPathToXMLFile) throws Exception {
		
			return readData(new InputSource(new FileInputStream(sPathToXMLFile)));
		}
		*/
}
