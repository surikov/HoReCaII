package sweetlife.android10.ui;

import sweetlife.android10.data.orders.*;

import java.io.File;
import java.util.*;

import sweetlife.android10.database.Requests;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.UIHelper;
import sweetlife.android10.utils.UIHelper.*;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import java.net.*;

import sweetlife.android10.*;
import tee.binding.Bough;

public class Activity_ReportShowReport extends Activity_Base implements IReportConsts
		//, sweetlife.horeca.utils.ManagedAsyncTask.ITaskListener
{
	String mReportFilePath;
	Activity_ReportShowReport me = this;
	boolean changeOrderRowsHook = false;
	String documentNumber = "";
	String documentDate = "";
	String documentComment = "";
	String shipDate = "";
	Vector<String> descriptions;
	Vector<String> article;
	Vector<String> cena;
	Vector<String> kolvo;
	Vector<String> name;
	WebView webView;
	MenuItem menuSaveAs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_report);
		Bundle extras = getIntent().getExtras();
		mReportFilePath = extras.getString(EXTRA_REPORT_FILE_PATH);
		webView = (WebView) findViewById(R.id.webview);
		LogHelper.debug(this.getClass().getCanonicalName() + ".onCreate file://" + mReportFilePath);
		webView.loadUrl("file://" + mReportFilePath);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSaveFormData(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new ReportWebViewClient());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UIHelper.MsgBox(getString(R.string.report), getString(R.string.exit_report), this, getString(R.string.ok), getString(R.string.cancel),
					new IMessageBoxCallbackInteger() {
						public void MessageBoxResult(int which) {
							DeleteReport();
							finish();
						}
					}, null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ReportWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String urlString) {
			LogHelper.debug(this.getClass().getCanonicalName() + " catch " + urlString);
			//view.loadUrl (url); 
			//UIHelper.MsgBox("menu", urlString, me);
			try {
				android.net.Uri uri = android.net.Uri.parse(urlString);
				//LogHelper.debug(this.getClass().getCanonicalName() + " kind is " + uri.getQueryParameter("kind"));				
				if (uri.getQueryParameter("kind").equals(Activity_ReportBase.HOOKReportOrderState)) {
					documentNumber = uri.getQueryParameter(Activity_ReportBase.FIELDDocumentNumber);
					documentDate = uri.getQueryParameter(Activity_ReportBase.FIELDDocumentDate);
					shipDate = uri.getQueryParameter(Activity_ReportBase.FIELDShipDate);
					//String polzov=ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim();
					//LogHelper.debug(this.getClass().getCanonicalName() + " " + Activity_ReportBase.FIELDDocumentNumber + " is " + documentNumber);
					//LogHelper.debug(this.getClass().getCanonicalName() + " " + Activity_ReportBase.FIELDDocumentDate + " is " + documentDate);
					UIHelper.MsgBox("Действия с документом", "Заказ: " + documentNumber + " от " + documentDate, me//
							, "Провести", new IMessageBoxCallbackInteger() {
								@Override
								public void MessageBoxResult(int which) {
									//LogHelper.debug("approve");
									UploadApproveOrder task = new UploadApproveOrder("Подтверждение заказа "// 
											+ documentNumber + " от " + documentDate//
											, getApplicationContext()//
											, mDB//
											, UploadApproveOrder.ThatDone_APPROVE//
											, documentNumber//
											, documentDate//
											, shipDate//
											, null
											, documentComment
									);
									//task.setListener(me);
									AsyncTaskManager.getInstance().addObserver(me);
									AsyncTaskManager.getInstance().executeTask(me, task);
								}
							}//
							, "Пометить на удаление", new IMessageBoxCallbackInteger() {
								@Override
								public void MessageBoxResult(int which) {
									//LogHelper.debug("delete");
									UploadApproveOrder task = new UploadApproveOrder("Пометка заказа "// 
											+ documentNumber + " от " + documentDate + " на удаление"//
											, getApplicationContext()//
											, mDB//
											, UploadApproveOrder.ThatDone_DROP//
											, documentNumber//
											, documentDate//
											, shipDate//
											, null

											, documentComment);
									//task.setListener(me);
									AsyncTaskManager.getInstance().addObserver(me);
									AsyncTaskManager.getInstance().executeTask(me, task);
								}
							}//
							, "Изменить", new IMessageBoxCallbackInteger() {
								@Override
								public void MessageBoxResult(int which) {
									//LogHelper.debug("open");
									//UIHelper.MsgBox("System", "Not implemented yet", me);
									changeOrderRowsHook = true;
									UploadApproveOrder task = new UploadApproveOrder("Данные заказа "// 
											+ documentNumber + " от " + documentDate //
											, getApplicationContext()//
											, mDB//
											, UploadApproveOrder.ThatDone_GET//
											, documentNumber//
											, documentDate//
											, shipDate//
											, null
											, documentComment
									);
									//task.setListener(me);
									AsyncTaskManager.getInstance().addObserver(me);
									AsyncTaskManager.getInstance().executeTask(me, task);
								}
							}//
					);
				}
			} catch (Throwable t) {
				LogHelper.debug(this.getClass().getCanonicalName() + " " + t.getMessage());
			}
			return true;
		}
	}

	private void DeleteReport() {
		if (mReportFilePath != null) {
			File fileForDelete = new File(mReportFilePath);
			if (fileForDelete.exists()) {
				fileForDelete.delete();
			}
		}
	}

	void resetDescription() {
		descriptions = new Vector<String>();
		for (int i = 0; i < name.size(); i++) {
			String row = article.get(i)//
					+ ": " + name.get(i)//
					+ ", цена " + cena.get(i)//
					+ " (" + kolvo.get(i) + " шт.)"//
					;
			descriptions.add(row);
		}
	}

	void fillChangeOrder(String resultString) {
		//System.out.println("------------------------"+);
		Bough bough;
		try {
			bough = Bough.parseXML(resultString);
		} catch (Throwable t) {
			bough = new Bough();
			UIHelper.quickWarning("Невозможно прочитать заявку", me);
		}
		article = new Vector<String>();
		cena = new Vector<String>();
		kolvo = new Vector<String>();
		name = new Vector<String>();
		documentComment = bough.child("soap:Body").child("m:GatResponse").child("m:return").child("m:Head").child("Comment").value.property.value();
		//.child("Date").value.property.value();
		Vector<Bough> r = bough.child("soap:Body").child("m:GatResponse").child("m:return").child("m:Table").children("m:Stroki");
		for (int i = 0; i < r.size(); i++) {
			String nom = Requests.getNomenclatureNameFromArtikul(mDB, r.get(i).child("Article").value.property.value());
			name.add(nom);
			article.add(r.get(i).child("Article").value.property.value());
			cena.add(r.get(i).child("m:Cena").value.property.value());
			kolvo.add(r.get(i).child("KolVo").value.property.value());
			/*String row = r.get(i).child("Article").value.property.value()//
					+ ": " + nom//
					+ ", цена " + r.get(i).child("m:Cena").value.property.value()//
					+ " (" + r.get(i).child("KolVo").value.property.value() + " шт.)"//
			;*/
			//descriptions.add(row);
		}
		resetDescription();
	}

	void sendChangeOrder() {
		changeOrderRowsHook = false;
		Bough b = new Bough().name.is("Table");
		for (int i = 0; i < this.cena.size(); i++) {
			Bough stroki = new Bough().name.is("Stroki");
			stroki.child("Article").attribute.is(true).value.is(article.get(i));
			stroki.child("KolVo").attribute.is(true).value.is(kolvo.get(i));
			stroki.child("Cena").value.is(cena.get(i));
			b.child(stroki);
		}
		UploadApproveOrder task = new UploadApproveOrder("Сохранение заказа "// 
				+ documentNumber + " от " + documentDate //
				, getApplicationContext()//
				, mDB//
				, UploadApproveOrder.ThatDone_CHANGE//
				, documentNumber//
				, documentDate//

				, shipDate//
				, b
				, this.documentComment
		);
		//task.setListener(me);
		AsyncTaskManager.getInstance().addObserver(me);
		AsyncTaskManager.getInstance().executeTask(me, task);
	}

	void showChangeOrder() {
		String cm = documentComment;
		if (cm.length() > 0)
			cm = " (" + documentComment + ")";
		UIHelper.MsgBoxList("Заказ №" + documentNumber //
						+ " от " + documentDate + cm, me, descriptions//
				, new IMessageBoxCallbackInteger() {
					@Override
					public void MessageBoxResult(int which) {
						showChangeItem(which);
					}
				}//
				, "Сохранить", new IMessageBoxCallbackInteger() {
					@Override
					public void MessageBoxResult(int which) {
						//showChangeItem(which);
						sendChangeOrder();
					}
				}//
				, "Комментарий", new IMessageBoxCallbackInteger() {
					@Override
					public void MessageBoxResult(int which) {
						//showChangeItem(which);
						showChangeComment();
					}
				});
	}

	void showChangeComment() {
		UIHelper.MsgBoxString("Заказ №" + documentNumber + " от " + documentDate//
				, "Комментарий"//
				, me//
				, documentComment//
				, "Изменить", new IMessageBoxCallbackString() {
					@Override
					public void MessageBoxResult(String n) {
						documentComment = n;
						showChangeOrder();
					}
				}//
		);
	}

	void showChangeItem(final int which) {
		UIHelper.MsgBoxDouble(//				
				"Заказ №" + documentNumber + " от " + documentDate//
				, descriptions.get(which)//
				, me//
				, Double.parseDouble(kolvo.get(which))//
				, "Изменить", new IMessageBoxCallbackDouble() {
					@Override
					public void MessageBoxResult(double num) {
						//System.out.println("set " + num + " for " + which);
						kolvo.setElementAt("" + num, which);
						resetDescription();
						showChangeOrder();
					}
				}//
				, "Список", new IMessageBoxCallbackDouble() {
					@Override
					public void MessageBoxResult(double num) {
						showChangeOrder();
					}
				}//
				, "Удалить", new IMessageBoxCallbackDouble() {
					@Override
					public void MessageBoxResult(double num) {
						//showChangeOrder();
						article.remove((int) which);
						cena.remove((int) which);
						kolvo.remove((int) which);
						name.remove((int) which);
						descriptions.remove((int) which);
						showChangeOrder();
					}
				}//
		);
	}

	@Override
	public void update(Observable observable, Object data) {
		observable.deleteObserver(me);
		Bundle bundle = (Bundle) data;
		String resultString = bundle.getString(sweetlife.android10.utils.ManagedAsyncTask.RESULT_STRING);
		if (changeOrderRowsHook) {
			fillChangeOrder(resultString);
			showChangeOrder();
		} else {
			UIHelper.MsgBox("Результат", "" + resultString, me, new IMessageBoxCallbackInteger() {
				@Override
				public void MessageBoxResult(int which) {
					me.finish();
				}
			});
		}
	}

	/*@Override
	public void onProgressUpdate(String message) {
		LogHelper.debug(this.getClass().getCanonicalName() + " onProgressUpdate " + message);
	}
	@Override
	public void onComplete(Bundle resultData) {
		UIHelper.MsgBox("Результат", "" + resultData.getString(sweetlife.horeca.utils.ManagedAsyncTask.RESULT_STRING), me);
		this.finish();
	}*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//LogHelper.debug(this.getClass().getCanonicalName() + " onCreateOptionsMenu");
		menuSaveAs = menu.add("Сохранить в файл");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected( item);
		LogHelper.debug(this.getClass().getCanonicalName() + " onOptionsItemSelected " + item.getTitle());
		if (item == menuSaveAs) {
			UIHelper.quickWarning("Not implemented yet.", this);
			return true;
		}
		return false;
	}
}
