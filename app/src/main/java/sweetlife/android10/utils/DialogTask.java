package sweetlife.android10.utils;

import android.content.Context;
import android.os.Bundle;

public class DialogTask extends ManagedAsyncTask<Integer>{

	public static int ERROR_NONE;
	sweetlife.android10.ui.Activity_BidsContractsEtc back;
	private IDialogTaskAction mAction;

	public DialogTask(
			String progressMessage,
			Context appContext, 
			IDialogTaskAction action ) {
	
		this(progressMessage, appContext,action,null);

		//mAction  = action;
	}

	public DialogTask(
			String progressMessage,
			Context appContext, 
			IDialogTaskAction action 
			,sweetlife.android10.ui.Activity_BidsContractsEtc f
			) {
	
		super(progressMessage, appContext);
back=f;
		mAction  = action;
	}
	@Override
	protected Integer doInBackground(Object... arg0) {

		if( mAction != null ) {
			
			return mAction.onAction();
		}

		return ERROR_NONE;
	}

	@Override
	protected void onPostExecute(Integer result) {

		Bundle resultData = new Bundle();
		
		resultData.putInt(RESULT_INTEGER, result);
		
		mTaskListener.onComplete(resultData);
		if(back!=null){
			
			//System.out.println("here!");
			back.resetTitle();
		}
	}

	public interface IDialogTaskAction {

		int onAction();
	}
}
