package sweetlife.android10.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import reactive.ui.Auxiliary;
import reactive.ui.Expect;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.data.common.IStateChanged;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.orders.UploadBidsAsyncTask;
import sweetlife.android10.data.orders.UploadBidsListAdapter;
import sweetlife.android10.data.orders.ZayavkaPokupatelya;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import sweetlife.android10.R;
import tee.binding.*;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

public class Activity_UploadBids extends Activity_BasePeriod implements ImageView.OnClickListener, IStateChanged, Observer {
	static boolean showUploaded = false;
	private static UploadBidsListAdapter mListAdapter;
	private static ListView mList;
	private static ImageView mCheckAll;
	MenuItem menuOtchety;

	public static String composeUploadOrderString(String key) {
		SQLiteDatabase mDB= ApplicationHoreca.getInstance().getDataBase();
		String request = "";
		String sql = "select doc.Nomer as Nomer,doc.DataOtgruzki as DataOtgruzki,dgvr.Kod as Kod,kntr.kod as kntr,doc.tipOplaty as tipOplaty,doc.Kommentariy as kommentariy from"//
				+ "\n ZayavkaPokupatelyaIskhodyaschaya doc"//
				+ "\n join DogovoryKontragentov_strip dgvr on dgvr._idrref=doc.DogovorKontragenta"//
				+ "\n join Kontragenty kntr on kntr._idrref=doc.Kontragent"//
				+ "\n where doc.nomer='" + key + "'";
		System.out.println("composeUploadOrderString " + sql);
		Bough doc = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println("doc " + doc.dumpXML());
		request = request + "\"ВнешнийНомер\": \"" + doc.child("row").child("Nomer").value.property.value() + "\"";
		request = request + ",\"ДатаОтгрузки\": \"" + Auxiliary.tryReFormatDate(doc.child("row").child("DataOtgruzki").value.property.value(), "yyyy-MM-dd", "yyyyMMdd") + "\"";


		String tipOplaty = doc.child("row").child("tipOplaty").value.property.value();
		//String tipOplatyLabel = "ТовЧек";
		String tipOplatyLabel = "нет типа оплаты";
		//if (tipOplaty.compareToIgnoreCase("b8a71648be9e99d3492dab9257e5d773") == 0) {
		if (("x'" + tipOplaty.trim() + "'").compareToIgnoreCase(Cfg.tip_nalichnie) == 0) {
			tipOplatyLabel = "Нал";
		} else {
			//if (tipOplaty.compareToIgnoreCase("838d51b55d9490754fb77f3d5fe02c1e") == 0) {
			if (("x'" + tipOplaty.trim() + "'").compareToIgnoreCase(Cfg.tip_beznal) == 0) {
				tipOplatyLabel = "БезНал";
			} else {
				if (("x'" + tipOplaty.trim() + "'").compareToIgnoreCase(Cfg.tip_tovcheck) == 0) {
					tipOplatyLabel = "ТовЧек";
				}
			}
		}
		request = request + ",\"ТипОплаты\": \"" + tipOplatyLabel + "\"";
		request = request + ",\"КодДоговора\": \"" + doc.child("row").child("Kod").value.property.value() + "\"";
		request = request + ",\"КодКонтрагента\": \"" + doc.child("row").child("kntr").value.property.value() + "\"";

		request = request + ",\"КомментарийТП\": \"" + Activity_Bid.comment2comment(doc.child("row").child("kommentariy").value.property.value()) + "\"";
		request = request + ",\"НомерЗаказаКлиента\": \"" + Activity_Bid.comment2num(doc.child("row").child("kommentariy").value.property.value()) + "\"";


		sql = "select tovar.vidSkidki as vidSkidki,tovar.cenaSoSkidkoy as cenaSoSkidkoy,  n.artikul as artikul , tovar.kolichestvo as kolichestvo from"//
				+ "\n ZayavkaPokupatelyaIskhodyaschaya doc"//
				+ "\n join ZayavkaPokupatelyaIskhodyaschaya_Tovary tovar on tovar._ZayavkaPokupatelyaIskhodyaschaya_IDRRef=doc._idrref"//
				+ "\n   join nomenklatura n on n._idrref=tovar.nomenklatura"//
				+ "\n where doc.nomer='" + key + "'";
		Bough tovar = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println("tovar " + tovar.dumpXML());

		if (tovar.children.size() > 0) {
			request = request + ",\"Товары\":[";
			for (int tc = 0; tc < tovar.children.size(); tc++) {
				if (tc > 0) {
					request = request + ",";
				}
				Bough row = tovar.children.get(tc);
				request = request + "{";
				request = request + "\"Артикул\": \"" + row.child("artikul").value.property.value() + "\"";
				request = request + ",\"Количество\": " + row.child("kolichestvo").value.property.value() + "";
				//request = request + ",\"vidSkidki\": " + row.child("vidSkidki").value.property.value() + "";
				//if (row.child("vidSkidki").value.property.value().trim().toUpperCase().equals("99D730123902D40541B2F8954FE1E089")) {
				System.out.println("vidSkidki::::::::::" + row.child("vidSkidki").value.property.value() + "::::::::::");

				if (//wrong
						row.child("vidSkidki").value.property.value().trim().toUpperCase().equals("X'" + Cfg.skidkaIdOldCenovoyeReagirovanie + "'")
								|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals("X'" + Cfg.skidkaIdCenovoyeReagirovanie + "'")
								|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals("X'" + Cfg.skidkaIdAutoReagirovanie + "'")
								//right
								|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals(Cfg.skidkaIdOldCenovoyeReagirovanie)
								|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals(Cfg.skidkaIdCenovoyeReagirovanie)
								|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals(Cfg.skidkaIdAutoReagirovanie)
				) {
					request = request + ",\"Цена\": " + row.child("cenaSoSkidkoy").value.property.value() + "";
					request = request + ",\"ЦР\": true";
				} else {
					if (row.child("vidSkidki").value.property.value().trim().toUpperCase().equals("X'" + Cfg.skidkaId_TGCR + "'")
							|| row.child("vidSkidki").value.property.value().trim().toUpperCase().equals(Cfg.skidkaId_TGCR)) {
						request = request + ",\"Цена\": " + row.child("cenaSoSkidkoy").value.property.value() + "";
						request = request + ",\"ТГ\": true";
					} else {
						request = request + ",\"Цена\": " + row.child("cenaSoSkidkoy").value.property.value() + "";
						request = request + ",\"ЦР\": false";
					}
				}

				request = request + "}";
			}
			request = request + "]";
		}


		sql = "select n.artikul as artikul , usluga.kolichestvo as kolichestvo from "//
				+ "\n ZayavkaPokupatelyaIskhodyaschaya doc"//
				+ "\n join ZayavkaPokupatelyaIskhodyaschaya_Uslugi usluga on usluga._ZayavkaPokupatelyaIskhodyaschaya_IDRRef=doc._idrref"//
				+ "\n join nomenklatura n on n._idrref=usluga.nomenklatura"//
				+ "\n where doc.nomer='" + key + "'";
		Bough usluga = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println("usluga " + usluga.dumpXML());


		if (usluga.children.size() > 0) {
			request = request + ",\"Услуги\":[";
			for (int tc = 0; tc < usluga.children.size(); tc++) {
				if (tc > 0) {
					request = request + ",";
				}
				Bough row = usluga.children.get(tc);
				request = request + "{";
				request = request + "\"Артикул\": \"" + row.child("artikul").value.property.value() + "\"";
				request = request + ",\"Количество\": " + row.child("kolichestvo").value.property.value() + "";
				request = request + "}";
			}
			request = request + "]";
		}


		sql = "select n.artikul as artikul , trafik.kolichestvo as kolichestvo , trafik.data as data from "//
				+ "\n ZayavkaPokupatelyaIskhodyaschaya doc"//
				+ "\n join ZayavkaPokupatelyaIskhodyaschaya_Traphiki trafik on trafik._ZayavkaPokupatelyaIskhodyaschaya_IDRRef=doc._idrref"//
				+ "\n join nomenklatura n on n._idrref=trafik.nomenklatura"//
				+ "\n where doc.nomer='" + key + "'";
		Bough trafik = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println("trafik " + trafik.dumpXML());
		if (trafik.children.size() > 0) {
			request = request + ",\"Трафики\":[";
			for (int tc = 0; tc < trafik.children.size(); tc++) {
				if (tc > 0) {
					request = request + ",";
				}
				Bough row = trafik.children.get(tc);
				request = request + "{";
				request = request + "\"Артикул\": \"" + row.child("artikul").value.property.value() + "\"";
				request = request + ",\"Количество\": " + row.child("kolichestvo").value.property.value() + "";
				request = request + ",\"ДатаОтгрузки\": \"" + Auxiliary.tryReFormatDate(row.child("data").value.property.value(), "yyyy-MM-dd", "yyyyMMdd") + "\"";
				request = request + "}";
			}
			request = request + "]";
		}
		return request;
	}

	public static void ___logToFile(String ext, String txt) {

		SimpleDateFormat sqliteTime = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_SSS");
		String name = "/sdcard/horeca/log/" + sqliteTime.format(new Date()) + "." + ext;
		System.out.println("logToFile " + name + ": " + txt);
		Auxiliary.createAbsolutePathForFile(name);
		Auxiliary.writeTextToFile(new File(name), txt, "utf-8");
	}

	private View.OnClickListener nextUploadClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			System.out.println("start upload bids");
			((Button) findViewById(R.id.btn_upload)).setEnabled(false);
/*
			if(!(v==null)){
				Auxiliary.alertBreak("p[robe msg",Activity_UploadBids.this);
				System.out.println("test");
			}*/

			if (!SystemHelper.IsNetworkAvailable(Activity_UploadBids.this)) {
				Auxiliary.warn("Нет подключения к сети. Проверьте инет.", Activity_UploadBids.this);
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
				Auxiliary.warn("Не выбран ни один документ.", Activity_UploadBids.this);
				return;
			}
			//ArrayList<NomenclatureBasedDocument> dataRequestList = new ArrayList<NomenclatureBasedDocument>();
			SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat toDate = new SimpleDateFormat("yyyyMMdd");
			int docCount = 0;
			String request = "[";
			for (int i = 0; i < count; i++) {
				if (stateList.get(i)) {
					Cursor cursor = mListAdapter.getCursor();
					cursor.moveToPosition(i);
					String key = Request_Bids.getNomer(cursor);
					System.out.println("key " + key);
					if (docCount > 0) {
						request = request + ",{";
					} else {
						request = request + "{";
					}

					request = request + composeUploadOrderString(key);


					request = request + "}";
					docCount++;
				}
			}
			request = request + "]";
			//final String url = "http://89.109.7.162/GolovaNew/hs/ZakaziPokupatelya/" +  ApplicationHoreca.getInstance().hrcSelectedRoute();
			//final String url = Settings.getInstance().getBaseURL() + "cehan_hrc/hs/ZakaziPokupatelya/" + ApplicationHoreca.getInstance().hrcSelectedRoute();
			//final String url = "http://10.10.5.26/golovanew/hs/ZakaziPokupatelya/" + ApplicationHoreca.getInstance().hrcSelectedRoute();
			//
			//final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()+"/hs/ZakaziPokupatelya/" + ApplicationHoreca.getInstance().hrcSelectedRoute();
			//.url.is("http://testservice.swlife.ru/lipuzhin_hrc/ChekList.1cws")//

			//final String url = "http://testservice.swlife.ru/lipuzhin_hrc/hs/ZakaziPokupatelya/"+ Cfg.currentHRC();
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZakaziPokupatelya/" + Cfg.whoCheckListOwner();
			//final String url =  "http://testservice.swlife.ru/cehan_hrc/hs/ZakaziPokupatelya/"+ Cfg.currentHRC();

			//System.out.println(Cfg.hrcPersonalLogin+": "+Cfg.hrcPersonalPassword+": "+url);
			final Bough result = new Bough();
			final String post //="[{\n" + "\t\t\"ВнешнийНомер\": \"0930062645330\",\n" + "\t\t\"ДатаОтгрузки\": \"20171024\",\n" + "\t\t\"ТипОплаты\": \"БезНал\",\n" + "\t\t\"КодДоговора\": \"27364\",\n" + "\t\t\"КодКонтрагента\": 80075,\n" + "\t\t\"Товары\": [{\n" + "\t\t\t\t\"Артикул\": \"78411\",\n" + "\t\t\t\t\"Количество\": 500\n" + "\t\t\t}\n" + "\t\t]\n" + "\t}\n" + "]";
					= request;
			//logToFile("post.json", request);
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					System.out.println("post: " + post);
					try {
						//Bough txt = Auxiliary.loadTextFromPOST(url, post, 300 * 1000, "UTF-8");
						//Bough txt = Auxiliary.loadTextFromPrivatePOST(url, post, 300 * 1000, "UTF-8","hrc429","123nop");
						Bough txt = Auxiliary.loadTextFromPrivatePOST(url, post, 300 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());

						System.out.println("post result is " + txt.dumpXML());
						//result.child("response").value.is(txt);
						result.children.add(txt);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			})//
					.afterDone.is(new Task() {
				@Override
				public void doTask() {
					String msg = "";
					System.out.println("afterDone");
					try {
						String response = result.child("result").child("raw").value.property.value();
						//logToFile("response.json", response);
						Bough b = Bough.parseJSONorThrow(response);
						//Bough b = Bough.parseJSON(result.child("result").child("raw").value.property.value());
						//System.out.println("result is "+result);
						//System.out.println("b is "+b.dumpXML());
						if (b.children.size() > 0) {
							msg = msg + "Выгрузка (" + result.child("result").child("code").value.property.value() + ", " + result.child("result").child("message").value.property.value() + "):\n" + b.child("Сообщение").value.property.value() + "\n";
							Vector<Bough> forOrders = b.children("ДанныеПоЗаказам");
							for (int rr = 0; rr < forOrders.size(); rr++) {
								Bough row = forOrders.get(rr);
								//System.out.println("row " + row.dumpXML());
								String rowNum = row.child("ВнешнийНомер").value.property.value();
								double rowStatus = Numeric.string2double(row.child("Статус").value.property.value());

								msg = msg + rowNum + "(" + rowStatus + ")" + "\n";
								msg = msg + row.child("Сообщение").value.property.value() + "\n";
								Vector<Bough> forSub = row.children("Заказы");
								for (int zz = 0; zz < forSub.size(); zz++) {
									Bough line = forSub.get(zz);
									double subStatus = Numeric.string2double(line.child("Статус").value.property.value());
									if (subStatus < 1) {
										rowStatus = 0;
									}
									msg = msg + line.child("Тип").value.property.value() + " №: " + line.child("Номер").value.property.value() + "\n";
									msg = msg + line.child("Сообщение").value.property.value() + "\n";
									Vector<Bough> nepodtv = line.children("НеПодтвержденныеПозиции");
									if (nepodtv.size() > 0) {
										msg = msg + "дефицит:\n";
									}
									for (int np = 0; np < nepodtv.size(); np++) {
										Bough nepoitem = nepodtv.get(np);
										String dodt = nepoitem.child("ДатаПоступления").value.property.value();
										if (dodt.trim().length() > 0) {
											dodt = ", поступление " + Auxiliary.tryReFormatDate(dodt, "yyyyMMdd", "dd.MM.yyyy");
										}
										msg = msg + nepoitem.child("Номенклатура").value.property.value() //
												+ ": " + nepoitem.child("КоличествоДефицит").value.property.value() //
												+ " из " + nepoitem.child("КоличествоЗаказано").value.property.value() //
												+ dodt //
												+ "\n";
									}
									if (nepodtv.size() > 0) {
										msg = msg + "\n";
									}
								}
								if (rowStatus > 0) {
									System.out.println("lock " + rowNum);
									String sql = "update ZayavkaPokupatelyaIskhodyaschaya set proveden=x'01' where nomer='" + rowNum.trim() + "';";
									mDB.execSQL(sql);
								}
							}
						} else {
							System.out.println("Empty " + result.dumpXML());
							msg = msg + " \nВозможны ошибки при выгрузке" //+ result.dumpXML();
									+ "\n\nПроверьте статус заказов в отчёте, возможно необходимо удалить повторы"//
									+ "\n\nТекст ответа:"//
									+ result.dumpXML().substring(0, 160).replace("\n", "").replace("  ", " ");
						}
					} catch (Throwable t) {
						msg = msg + "/ Ошибка /" + t.toString() + "/" //
						;
					}

					//Auxiliary.warn(msg, Activity_UploadBids.this);
					Auxiliary.alertBreak(msg, Activity_UploadBids.this);
					Requery();
				}
			})//
					.status.is("Подождите...")//
					.start(Activity_UploadBids.this);
			//
			System.out.println("done upload bids");
		}
	};
	private View.OnClickListener mUploadClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			System.out.println("------------------");
			if (!SystemHelper.IsNetworkAvailable(Activity_UploadBids.this)) {
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
				CreateErrorDialog(R.string.documents_not_selected).show();
				return;
			}
			ArrayList<NomenclatureBasedDocument> dataRequestList = new ArrayList<NomenclatureBasedDocument>();
			Cursor cursor = null;
			for (int i = 0; i < count; i++) {
				if (stateList.get(i)) {
					cursor = mListAdapter.getCursor();
					cursor.moveToPosition(i);
					String key = Request_Bids.getNomer(cursor);
					String[] keys = key.split(",");
					/*System.out.println("key: "+key);
					System.out.println(keys.length);
					System.out.println(key.split("п").length);*/
					if (keys.length > 1) {
						key = keys[keys.length - 1];
					}
					key = key.trim();
					dataRequestList.add(new ZayavkaPokupatelya(Request_Bids.get_id(cursor)//
							, Request_Bids.getIDRRef(cursor)//
							, Request_Bids.getData(cursor)//
							, key//Request_Bids.getNomer(cursor)//
							, false//
							, Request_Bids.getDataOtgruzki(cursor)//
							, Request_Bids.getDogovorKontragenta(cursor)//
							, Request_Bids.getKommentariy(cursor)//
							, Request_Bids.getKontragentID(cursor)//
							, Request_Bids.getKontragentKod(cursor)//
							, Request_Bids.getKontragentNaimanovanie(cursor)//
							, Request_Bids.getSumma(cursor)//
							, Request_Bids.getTipOplaty(cursor)//
							, Request_Bids.getTipOplatyPoryadok(cursor)//
							, Request_Bids.getSebestoimost(cursor)//
							, false));
				}
			}
			UploadBidsAsyncTask task = new UploadBidsAsyncTask(mDB//
					, getApplicationContext()//
					, getString(R.string.orders_upload)//
					, dataRequestList//
					, Settings.getInstance().getSERVICE_ORDERS()//
			);
			AsyncTaskManager.getInstance().executeTask(Activity_UploadBids.this, task);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_bids_upload);
		super.onCreate(savedInstanceState);
		setTitle("Выгрузка заявок на заказы");
		InitializeControls();
	}

	private void InitializeControls() {
		mCheckAll = (ImageView) findViewById(R.id.check_all);
		mCheckAll.setImageResource(android.R.drawable.checkbox_off_background);//.checkbox_on_background);
		mCheckAll.setTag(Boolean.FALSE);//.TRUE);
		mCheckAll.setOnClickListener(this);
		/*if (Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC29")//
				|| Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC28")//
				|| Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC210")//
				|| Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC23")//
				|| Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC213")//
				|| Activity_Route.hrcSelectedRoute().trim().toUpperCase().equals("HRC27")//
				) {
			((Button) findViewById(R.id.btn_upload)).setOnClickListener(nextUploadClick);
		} else {
			((Button) findViewById(R.id.btn_upload)).setOnClickListener(mUploadClick);
		}*/
		((Button) findViewById(R.id.btn_upload)).setOnClickListener(nextUploadClick);
		//((Button) findViewById(R.id.btn_upload)).setOnClickListener(mUploadClick);
		InitializeListView();
	}

	private void InitializeListView() {
		mList = (ListView) findViewById(R.id.list_bids);
		mListAdapter = new UploadBidsListAdapter(this, Request_Bids.RequestPeriod(mDB, DateTimeHelper.SQLDateString(mFromPeriod.getTime()), DateTimeHelper.SQLDateString(mToPeriod.getTime()), true), this);
		mList.setAdapter(mListAdapter);
		mList.setOnTouchListener(this);
	}

	@Override
	public void update(Observable observable, Object data) {
		String result = ((Bundle) data).getString(ManagedAsyncTask.RESULT_STRING);
		if (result != null) {
			LogHelper.debug(this.getClass().getCanonicalName() + ".update: " + this.getString(R.string.confirm));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirm);
			builder.setMessage(result);
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
		showUploaded = newState;
		//mListAdapter.notifyDataSetChanged();
		Requery();
	}

	/*private void setCheckAllState() {
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
		//mListAdapter.notifyDataSetChanged();
		Requery();
	}*/
	@Override
	protected void OnDateChanged(Date fromDate, Date toDate) {
		Requery();
	}

	private void Requery() {
		System.out.println("showUploaded " + showUploaded);
		mListAdapter.changeCursor(Request_Bids//
				.RequestPeriod(mDB//
						, DateTimeHelper.SQLDateString(mFromPeriod.getTime())//
						, DateTimeHelper.SQLDateString(mToPeriod.getTime())//
						, !showUploaded));
		mListAdapter.notifyDataSetChanged();
		//setCheckAllState();
	}

	@Override
	public void onChange() {
		//setCheckAllState();
	}

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
}
