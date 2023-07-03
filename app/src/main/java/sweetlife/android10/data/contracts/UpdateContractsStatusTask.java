package sweetlife.android10.data.contracts;

import java.io.IOException;
import java.util.ArrayList;

import org.acra.ErrorReporter;
import org.apache.http.HttpStatus;

import sweetlife.android10.Settings;
import sweetlife.android10.database.Request_Contracts;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.ManagedAsyncTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


public class UpdateContractsStatusTask extends ManagedAsyncTask<Integer> {

	private int TIMEOUT = 300 * 1000;

	public static int SUCCESS = 0;
	public static int ERROR = 1;

	private SQLiteDatabase mDB;

	public UpdateContractsStatusTask(String progressDialogMessage,
									 Context appContext,
									 SQLiteDatabase db) {
		super(progressDialogMessage, appContext);

		mDB = db;
	}


	@Override
	protected Integer doInBackground(Object... arg0) {

		ArrayList<String> codConstractList = Request_Contracts.getContractsCodesForAllInRoute(mDB);

		ContractsXMLSerializer serializer = new ContractsXMLSerializer(codConstractList);

		String requestString = null;
		try {

			requestString = serializer.SerializeXML();

		} catch (IOException e1) {
			ErrorReporter.getInstance().putCustomData("handled", "serialize except");
			ErrorReporter.getInstance().handleSilentException(e1);
			return ERROR;
		}

		if (requestString != null && requestString.length() != 0) {

			HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_CONTRACTS_CODES());

			request.setTimeOut(TIMEOUT);

			try {
				if (request.Execute(requestString) != HttpStatus.SC_OK) {

					ErrorReporter.getInstance().putCustomData("handled", "execute except");
					ErrorReporter.getInstance().putCustomData("Contracts != SC_OK ", request.getResponse());
					ErrorReporter.getInstance().handleSilentException(null);
					return ERROR;
				}
			} catch (Exception e) {

				ErrorReporter.getInstance().putCustomData("handled", "execute except");
				ErrorReporter.getInstance().handleSilentException(e);
				return ERROR;
			}

			String responseString = request.getResponse();

			try {

				ContractsXMLParser parser = new ContractsXMLParser();

				Request_Contracts.updateStatus(mDB, parser.Parse(responseString));
			} catch (Exception e) {

				ErrorReporter.getInstance().putCustomData("handled", "parse except");
				ErrorReporter.getInstance().handleSilentException(e);
				return ERROR;
			}

		}

		return SUCCESS;
	}

	@Override
	protected void onPostExecute(Integer result) {

		Bundle resultData = new Bundle();

		resultData.putInt(RESULT_INTEGER, result);

		mTaskListener.onComplete(resultData);
	}

	@Override
	public String getProgressMessage() {

		return mProgressDialogMessage;
	}

}
