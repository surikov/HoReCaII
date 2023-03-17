package sweetlife.android10.utils;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import reactive.ui.Auxiliary;

public class AsyncTaskManager extends Observable implements ManagedAsyncTask.ITaskListener {
	private Activity mActivity;
	private ProgressDialog mProgressDialog;
	private ManagedAsyncTask<?> mAsyncTask;
	private Bundle mResultData;
	private static AsyncTaskManager mInstance;

	private AsyncTaskManager() {
		//System.out.println("new AsyncTaskManager");
	}
	public static AsyncTaskManager getInstance() {
		if (mInstance == null) {
			mInstance = new AsyncTaskManager();
		}
		return mInstance;
	}
	public void executeTask(Activity activity, ManagedAsyncTask<?> task) {
		//System.out.println("AsyncTaskManager.executeTask " + task.getProgressMessage());
		mResultData = null;
		mAsyncTask = task;
		mAsyncTask.setListener(this);
		mAsyncTask.execute();
		mActivity = activity;
		showProgressDialog();
	}
	public void attach(Activity activity, Observer observer) {
		//System.out.println("AsyncTaskManager.attach");
		mActivity = activity;
		showProgressDialog();
		if (observer != null) {
			addObserver(observer);
		}
		if (mResultData != null) {
			onComplete(mResultData);
		}
	}
	public void detach() {
		//System.out.println("AsyncTaskManager.detach");
		deleteObservers();
		mActivity = null;
		hideProgressDialog();
	}
	private void hideProgressDialog() {
		//System.out.println("AsyncTaskManager.hideProgressDialog");
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
	private void showProgressDialog() {
		//System.out.println("AsyncTaskManager.showProgressDialog");
		if (mActivity != null && mAsyncTask != null && mProgressDialog == null && mAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			mProgressDialog = new ProgressDialog(mActivity);
			mProgressDialog.setMessage(mAsyncTask.getProgressMessage());
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
	}
	@Override
	public void onComplete(Bundle resultData) {
		System.out.println("AsyncTaskManager.onComplete "+ Auxiliary.bundle2bough(resultData).dumpXML());
		if (countObservers() > 0) {
			hideProgressDialog();
			setChanged();
			notifyObservers(resultData);
			mResultData = null;
		}
		else {
			mResultData = resultData;
		}
	}
	@Override
	public void onProgressUpdate(String message) {
		System.out.println("AsyncTaskManager.onProgressUpdate "+message);
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.setMessage(message);
		}
	}
}
