package sweetlife.android10.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

public abstract class ManagedAsyncTask<Result> extends AsyncTask<Object, String, Result> {
	public static String RESULT_STRING = "result_string";
	public static String RESULT_INTEGER = "result_int";
	public Context context = null;

	public ManagedAsyncTask(String progressDialogMessage, Context appContext) {
		mProgressDialogMessage = progressDialogMessage;
		mResources = appContext.getResources();
		context = appContext;
	}

	public interface ITaskListener {
		void onProgressUpdate(String message);
		void onComplete(Bundle resultData);
	}

	protected String mProgressDialogMessage;
	protected ITaskListener mTaskListener;
	protected Resources mResources;

	public String getProgressMessage() {
		return mProgressDialogMessage;
	}
	public void setListener(ITaskListener listener) {
		mTaskListener = listener;
	}
	protected void onProgressUpdate(String... values) {
		mProgressDialogMessage = values[0];
		mTaskListener.onProgressUpdate(mProgressDialogMessage);
	};
}
