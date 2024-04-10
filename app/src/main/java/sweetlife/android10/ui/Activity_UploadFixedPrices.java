package sweetlife.android10.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import tee.binding.task.*;
import tee.binding.*;
import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.data.common.IStateChanged;
import sweetlife.android10.data.fixedprices.FixedPriceXMLParser;
import sweetlife.android10.data.fixedprices.UploadFixedPricesListAdapter;
import sweetlife.android10.data.fixedprices.ZayavkaNaSkidki;
import sweetlife.android10.database.Request_FixedPrices;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;
import sweetlife.android10.R;
import sweetlife.android10.data.fixedprices.*;
import sweetlife.android10.supervisor.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class Activity_UploadFixedPrices extends Activity_BasePeriod implements ImageView.OnClickListener, IStateChanged, Observer {

	private static UploadFixedPricesListAdapter mListAdapter;
	private static ListView mList;
	private static ImageView mCheckAll;
	FixedPriceXMLParser fixedPriceXMLParser;
	MenuItem menuOtchety;
	private View.OnClickListener mUploadClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!SystemHelper.IsNetworkAvailable(Activity_UploadFixedPrices.this)) {
				CreateErrorDialog(R.string.network_isnot_available).show();
				return;
			}
			ArrayList<Boolean> stateList = mListAdapter.getStateList();
			int count = stateList.size();
			boolean documentsSelected = false;
			for (int i = 0; i < count; i++) {
				if (stateList.get(i)) {
					documentsSelected = true;
					break;
				}
			}
			if (!documentsSelected) {
				CreateErrorDialog("Не выбраны документы на выгрузку.").show();
				return;
			}
			//final ArrayList<NomenclatureBasedDocument> dataRequestList = new ArrayList<NomenclatureBasedDocument>();
			final ArrayList<ZayavkaNaSkidki> dataRequestList = new ArrayList<ZayavkaNaSkidki>();
			Cursor cursor = null;
			for (int i = 0; i < count; i++) {
				if (stateList.get(i)) {
					cursor = mListAdapter.getCursor();
					cursor.moveToPosition(i);
					dataRequestList.add(new ZayavkaNaSkidki(Request_FixedPrices.get_id(cursor), Request_FixedPrices.getIDRRef(cursor), Request_FixedPrices.getData(cursor), Request_FixedPrices.getNomer(cursor), Request_FixedPrices.getKontragentID(cursor), Request_FixedPrices.getKontragentKod(cursor), Request_FixedPrices.getKontragentNaimanovanie(cursor), Request_FixedPrices.getVremyaNachalaSkidkiPhiksCen(cursor), Request_FixedPrices.getVremyaOkonchaniyaSkidkiPhiksCen(cursor), Request_FixedPrices.getKommentariy(cursor), Request_FixedPrices.isUploaded(cursor), false));
				}
			}
			//AsyncTaskManager.getInstance().executeTask(Activity_UploadFixedPrices.this, task);
			/*
			fixedPriceXMLParser = new FixedPriceXMLParser();
			UploadDocumentAsyncTask task = new UploadDocumentAsyncTask(mDB, getApplicationContext(), getString(R.string.fixed_prices_upload_points), dataRequestList, Settings.getInstance().getSERVICE_FIXED_PRICES(), fixedPriceXMLParser);
			System.out.println("onClick " + task);
			AsyncTaskManager.getInstance().executeTask(Activity_UploadFixedPrices.this, task);
			*/
			//
			//final String baseURL = "http://10.10.5.2/GolovaNew/hs/";
			final String baseURL = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/";
			final Bough result = new Bough();
			System.out.println(dataRequestList.size());
			new Expect().status.is("Выгрузка заявок на фикс. цены").task.is(new Task() {
				public void doTask() {
					java.text.SimpleDateFormat yyyyMMdd = new java.text.SimpleDateFormat("yyyyMMdd");
					//String url = baseURL + "ZayavkiNaFiksCeny/" + ApplicationHoreca.getInstance().hrcSelectedRoute();
					String url = baseURL + "ZayavkiNaFiksCeny/" + Cfg.selectedOrDbHRC();
					String post = "[";
					String nxtLine = "";
					for (int i = 0; i < dataRequestList.size(); i++) {
						ZayavkaNaSkidki zs = dataRequestList.get(i);
						//System.out.println("" + zs.getKommentariy() + "/" + zs.getNomer() + "/" + zs.getVremyaNachalaSkidkiPhiksCen() + "/" + zs.getVremyaOkonchaniyaSkidkiPhiksCen() + "/" + zs.getClientKod() + "/" + zs.getClientName());
						post = post + nxtLine;
						nxtLine = ",";
						post = post + "{";
						//post = post + "\"ВнешнийНомер\":\"" + ApplicationHoreca.getInstance().hrcSelectedRoute() + zs.getNomer() + "\"";
						post = post + "\"ВнешнийНомер\":\"" + Cfg.selectedOrDbHRC() + zs.getNomer() + "\"";
						post = post + ",\"НачалоПериода\":\"" + yyyyMMdd.format(zs.getVremyaNachalaSkidkiPhiksCen()) + "\"";
						post = post + ",\"КонецПериода\":\"" + yyyyMMdd.format(zs.getVremyaOkonchaniyaSkidkiPhiksCen()) + "\"";
						post = post + ",\"Контрагент\":\"" + zs.getClientKod() + "\"";
						post = post + ",\"Комментарий\":\"" + zs.getKommentariy().replaceAll("\"", "'") + "\"";
						post = post + ",\"Номенклатура\":[";
						FixedPricesNomenclatureData rows = new FixedPricesNomenclatureData(mDB, zs);
						String nxtSub = "";
						for (int s = 0; s < rows.getCount(); s++) {
							ZayavkaNaSkidki_TovaryPhiksCen tov = rows.getNomenclature(s);
							//System.out.println(tov.getArtikul() + "/" + tov.getCena() + "/" + tov.getObyazatelstva());
							post = post + nxtSub;
							nxtSub = ",";
							post = post + "{";
							post = post + "\"Артикул\":\"" + tov.getArtikul() + "\"";
							post = post + ",\"Цена\":\"" + tov.getCena() + "\"";
							post = post + "}";
						}
						post = post + "]";
						post = post + "}";
					}
					post = post + "]";
					System.out.println("post is " + post);
					//Bough txt = Auxiliary.loadTextFromPOST(url, post, 300 * 1000, "UTF-8");
					Bough txt = Auxiliary.loadTextFromPrivatePOST(url, post, 300 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					//Bough txt = Auxiliary.loadTextFromPrivatePOST(url, post, 300 * 1000, "UTF-8", Cfg.hrcPersonalLogin,Cfg.hrcPersonalPassword);

					System.out.println("post result is " + txt.dumpXML());
					String response = txt.child("raw").value.property.value();
					result.child("raw").value.property.value(response);
					Bough b = Bough.parseJSON("{\"row\":" + response + "}").name.is("response");
					result.children.add(b);
					System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
					System.out.println(result.dumpXML());
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				}
			})//
					.afterDone.is(new Task() {
				public void doTask() {
					//
					String txt = "";
					java.util.Vector<Bough> rows = result.child("response").children("row");
					if (rows.size() > 0) {
						for (int i = 0; i < rows.size(); i++) {
							String r = rows.get(i).child("Результат").value.property.value();
							String n = rows.get(i).child("ВнешнийНомер").value.property.value();
							if (r.equals("Выполнено")) {
								for (int k = 0; k < dataRequestList.size(); k++) {
									//if ((ApplicationHoreca.getInstance().hrcSelectedRoute() + dataRequestList.get(k).getNomer().trim()).equals(n.trim())) {
									if ((Cfg.selectedOrDbHRC() + dataRequestList.get(k).getNomer().trim()).equals(n.trim())) {
										dataRequestList.get(k).writeUploaded(mDB);
										break;
									}
								}
							} else {
							}
							txt = txt + "Заявка " + n + ": " + r + "\n";
						}
					} else {
						txt = result.child("raw").value.property.value();
					}
					Auxiliary.warn(txt, Activity_UploadFixedPrices.this);
					//document.writeUploaded(mDB);
					Requery();
				}
			}).start(Activity_UploadFixedPrices.this)//
			//
			;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOtchety = menu.add("Отчёты");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_fixedprices_upload);
		super.onCreate(savedInstanceState);
		setTitle("Выгрузка заявок на фиксированные цены");
		InitializeControls();
	}

	private void InitializeControls() {
		mCheckAll = (ImageView) findViewById(R.id.check_all);
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(Boolean.TRUE);
		mCheckAll.setOnClickListener(this);
		((Button) findViewById(R.id.btn_upload)).setOnClickListener(mUploadClick);
		InitializeListView();
	}

	private void InitializeListView() {
		mList = (ListView) findViewById(R.id.list_fixed_prices);
		mListAdapter = new UploadFixedPricesListAdapter(this, Request_FixedPrices.RequestUploaded(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime())), this);
		mList.setAdapter(mListAdapter);
		mList.setOnTouchListener(this);
	}

	@Override
	public void update(Observable observable, Object data) {
		String result = ((Bundle) data).getString(ManagedAsyncTask.RESULT_STRING);
		if (result != null) {
			String s = fixedPriceXMLParser.comment;
			LogHelper.debug(this.getClass().getCanonicalName() + ".update: " + this.getString(R.string.confirm));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirm);
			builder.setMessage(result + "\n" + s);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					Requery();
				}
			});
			builder.create().show();
		}
	}

	@Override
	public void onClick(View v) {
		boolean newState = false;
		ArrayList<Boolean> stateList = mListAdapter.getStateList();
		if ((Boolean) mCheckAll.getTag()) {
			newState = false;
			mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
		} else {
			newState = true;
			mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		}
		int count = stateList.size();
		for (int i = 0; i < count; i++) {
			stateList.set(i, newState);
		}
		mCheckAll.setTag(newState);
		mListAdapter.notifyDataSetChanged();
	}

	private void setCheckAllState() {
		ArrayList<Boolean> stateList = mListAdapter.getStateList();
		int count = stateList.size();
		for (int i = 0; i < count; i++) {
			if (!stateList.get(i)) {
				mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);
				mCheckAll.setTag(false);
				return;
			}
		}
		mCheckAll.setImageResource(android.R.drawable.checkbox_on_background);
		mCheckAll.setTag(true);
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		Requery();
	}

	private void Requery() {
		mListAdapter.changeCursor(Request_FixedPrices.RequestUploaded(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime())));
		mListAdapter.notifyDataSetChanged();
		setCheckAllState();
	}

	@Override
	public void onChange() {
		setCheckAllState();
	}
}
