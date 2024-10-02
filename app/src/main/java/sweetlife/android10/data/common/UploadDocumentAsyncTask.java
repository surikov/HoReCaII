package sweetlife.android10.data.common;

import java.io.IOException;
import java.util.ArrayList;

import org.acra.ErrorReporter;
import org.apache.http.HttpStatus;

import sweetlife.android10.net.IParserBase;
import sweetlife.android10.net.IParserBase.EParserResult;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.ManagedAsyncTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

//import sweetlife.android10.*;

public class UploadDocumentAsyncTask extends ManagedAsyncTask<String> {

	private int TIMEOUT = 300 * 1000;

	private ArrayList<NomenclatureBasedDocument> mDocumentsForUpload;
	private String mRequestURL;
	private IParserBase mParser;

	private SQLiteDatabase mDB;

	public UploadDocumentAsyncTask(
			SQLiteDatabase db,
			Context appContext,
			String dialogMessage,
			ArrayList<NomenclatureBasedDocument> documentsForUpload,
			String requestURL,
			IParserBase parser) {

		super(dialogMessage, appContext);

		mDB = db;

		mProgressDialogMessage = dialogMessage;
		mDocumentsForUpload = documentsForUpload;
		mRequestURL = requestURL;
		mParser = parser;
	}

	@Override
	protected String doInBackground(Object... arg0) {

		StringBuilder resultString = new StringBuilder();

		for (NomenclatureBasedDocument document : mDocumentsForUpload) {

			String requestString = null;
			try {

				requestString = document.getSerializedXML(mDB);

			} catch (IOException e1) {
				ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
				ErrorReporter.getInstance().handleSilentException(e1);
				continue;
			}

			if (requestString != null && requestString.length() != 0) {

				HTTPRequest request = new HTTPRequest(mRequestURL);
				System.out.println("mRequestURL " + mRequestURL);
				System.out.println("requestString " + requestString);
				request.setTimeOut(TIMEOUT);

				try {

					if (request.Execute(requestString) != HttpStatus.SC_OK) {

						resultString.append(mResources.getString(sweetlife.android10.R.string.bad_server_responce)).append("\n");

						//Temporary
						try {
							ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
							ErrorReporter.getInstance().putCustomData("requestString", requestString);
							ErrorReporter.getInstance().putCustomData("mRequestURL", mRequestURL);
							ErrorReporter.getInstance().putCustomData("Document != SC_OK, request.getResponse() ", request.getResponse());
							ErrorReporter.getInstance().handleSilentException(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//Temporary

						continue;
					}
				} catch (Exception e) {

					ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
					ErrorReporter.getInstance().handleSilentException(e);

					resultString.append(mResources.getString(sweetlife.android10.R.string.bad_server_responce)).append("\n");

					continue;
				}

				String responseString = request.getResponse();

				try {

					EParserResult result = mParser.Parse(responseString);

					resultString.append(String.format(
							mParser.getResponseParseResult(mResources),
							document.getNomer()))
							.append("\n");

					if (result == EParserResult.EComplete) {

						document.writeUploaded(mDB);
					}

				} catch (Exception e) {

					ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
					ErrorReporter.getInstance().handleSilentException(e);
					resultString.append(mResources.getString(sweetlife.android10.R.string.bad_server_responce))
							.append("\n");

					continue;
				}

			}
		}
		return resultString.toString();
	}

	@Override
	protected void onPostExecute(String result) {

		Bundle resultData = new Bundle();

		resultData.putString(RESULT_STRING, result);

		mTaskListener.onComplete(resultData);
	}

	@Override
	public String getProgressMessage() {

		return mProgressDialogMessage;
	}

}
