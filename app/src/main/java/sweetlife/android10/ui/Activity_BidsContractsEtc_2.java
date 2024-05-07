package sweetlife.android10.ui;

import android.app.*;
//import android.app.Activity;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;

import sweetlife.android10.data.common.*;
import sweetlife.android10.data.fixedprices.*;
import sweetlife.android10.data.orders.*;
import sweetlife.android10.data.returns.*;
import sweetlife.android10.data.returns.ZayavkaNaVozvrat_Tovary;
import sweetlife.android10.database.*;
import sweetlife.android10.log.*;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.update.*;
import sweetlife.android10.utils.*;
import sweetlife.android10.utils.DateTimeHelper;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
import reactive.ui.*;

import android.database.sqlite.*;

import java.text.*;
import java.util.*;
import java.io.*;
import java.util.TimeZone;
import java.util.Vector;

import sweetlife.android10.gps.*;
import sweetlife.android10.*;

public class Activity_BidsContractsEtc_2 extends Activity{

	static String checkedTerritoryKod = null;

	final int etiketkaObratnayaSvyazKlientPicture = 734766;
	final int tovarObratnayaSvyazKlientPicture = 1098834;

	Note docNumObratnayaSvyazKlient = new Note();
	Note artikulObratnayaSvyazKlient = new Note();
	Note etiketkaObratnayaSvyazKlient = new Note();
	Note tovarObratnayaSvyazKlient = new Note();
	Note commentObratnayaSvyazKlient = new Note();
	Numeric dateObratnayaSvyazKlient = new Numeric();

	Layoutless layoutless;
	DataGrid dataGrid;
	MenuItem menuPechati;
	MenuItem menuVzaimoraschety;
	MenuItem menuOtchety;
	MenuItem menuDostavkaKarta;
	//MenuItem menuChekList;
	//MenuItem menuKartaKlienta;
	MenuItem menuObnoIstoria;
	MenuItem menuAktSverki;
	MenuItem menuShoMap;
	//MenuItem menuRequestMailList;
	MenuItem menuKlientPeople;
	MenuItem menuObratnayaSvyazKlient;

	MenuItem menuSklad;

	MenuItem menuSendLimit;
	MenuItem menuClearFixPrice;

	//MenuItem menuZayavakaRazdelenieNakladnih;


	MenuItem menuVislatNakladnieNaPochtu;
	//MenuItem menuZayavkaNaPerevodVdosudebnye;
	MenuItem menuKlienta;

	MenuItem menuRassylkaSchetovNaOplatu;

	int gridPageSize = 30;
	Bough gridData = new Bough();
	Numeric gridOffset = new Numeric();
	Note limitSQL = new Note().value("\n	limit " + (gridPageSize * 3) + " offset ").append(gridOffset.asNote());
	ColumnDescription columnInfo = new ColumnDescription();
	ColumnDescription columnComment = new ColumnDescription();
	ColumnDescription columnDate = new ColumnDescription();
	Knob knobZakazy;
	Knob knobFixCena;
	Knob knobVozvrat;
	Knob knobSpec;
	Knob knobDegustacia;
	Knob newZakaz;
	Knob newFixCena;
	Knob newVozvrat;
	Knob newSpecificacia;
	Knob newDegustas;
	private boolean mIsEditable;
	private Calendar mChosedDay;

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuAktSverki = menu.add("Акты сверки клиента");
		menuVzaimoraschety = menu.add("Взаиморасчёты с контрагентом");
		menuVislatNakladnieNaPochtu = menu.add("Выслать накладные на почту");
		menuDostavkaKarta = menu.add("Доставка на карте");
		menuKlienta = menu.add("Заметки");
		//menuZayavakaRazdelenieNakladnih = menu.add("Заявки на разделение накладной");
		menuSendLimit = menu.add("Заявка на увеличение лимита");
		menuKlientPeople = menu.add("Контактные лица клиента");
		menuObnoIstoria = menu.add("Обновить историю");
		menuObratnayaSvyazKlient = menu.add("Обратная связь от клиента");
		menuOtchety = menu.add("Отчёты");
		menuPechati = menu.add("Печати контрагента");
		menuShoMap = menu.add("Показать на карте");
		menuRassylkaSchetovNaOplatu = menu.add("Рассылка счетов на оплату");
		menuClearFixPrice = menu.add("Удалить заявки на фикс.цены");

		//menuChekList = menu.add("Чек-лист");
		//menuKartaKlienta = menu.add("Карта клиента");


		//menuRequestMailList = menu.add("Выслать шаблон заказа");
		//menuSklad = menu.add("Установить склад");


		//menuZayavkaNaPerevodVdosudebnye = menu.add("Заявка на перевод в досудебные");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuPechati){
			showPechati();
			return true;
		}
		if(item == menuVzaimoraschety){
			showVzaimoraschety();
			return true;
		}
		/*if(item == menuChekList) {
			showChekList();
			return true;
		}*/
		if(item == menuObnoIstoria){
			doObnoIstoria();
			return true;
		}
		if(item == menuOtchety){
			Intent intent = new Intent();
			intent.setClass(Activity_BidsContractsEtc_2.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		/*if(item == menuKartaKlienta) {
			Intent intent = new Intent();
			//intent.putExtra("_id", "" +mAppInstance.getClientInfo().getKod());
			intent.setClass(Activity_BidsContractsEtc_2.this, sweetlife.horeca.supervisor.ActivityKartaKlienta.class);
			startActivity(intent);
			return true;
		}*/
		if(item == menuShoMap){
			Intent intent = new Intent();
			//intent.setClass(this, sweetlife.horeca.supervisor.ActivityGPSMap.class);
			intent.setClass(this, sweetlife.android10.supervisor.ActivityYandexMapKontragent.class);

			this.startActivityForResult(intent, 0);
			return true;
		}
		if(item == menuDostavkaKarta){
			startCarMap();
            /*
            Intent intent = new Intent();
            //intent.putExtra("carGPSid","6021");
            //https://testservice.swlife.ru/golovanew/hs/ObnovlenieInfo/ПолучитьТекущийАвтомобиль/88319
            intent.putExtra("carGPSid", "6117");
            intent.putExtra("title", "+7333222333, Pupuipuk Kkkk, kididk");
            intent.setClass(this, sweetlife.horeca.supervisor.ActivityCarDelivery.class);
            this.startActivityForResult(intent, 0);
            */
			return true;
		}

        /*if (item == menuRequestMailList) {
            promptRequestMailList(this, new Vector<String>());
            return true;
        }*/
		if(item == menuSklad){
			doRequestSklad();
			return true;
		}

		if(item == menuSendLimit){
			promptLimit();
			return true;
		}
		/*
		if(item == menuZayavakaRazdelenieNakladnih){
			promptZayavkaRazdelenieNakladnih();
			return true;
		}
*/
		if(item == menuClearFixPrice){
			promptDeleteFixPrices();
			return true;
		}
		if(item == menuKlientPeople){
			promptKlientPeople();
			return true;
		}
		if(item == menuVislatNakladnieNaPochtu){
			promptVislatNakladnieNaPochtu();
			return true;
		}
		if(item == menuKlienta){
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.ui.ActivityMenuKlienta.class);
			//intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
			this.startActivityForResult(intent, 0);
			return true;
		}
		if(item == menuAktSverki){
			promptAktSverki();
			return true;
		}
		if(item == menuRassylkaSchetovNaOplatu){
			promptRassylkaSchetovNaOplatu();
			return true;
		}
		if(item == menuObratnayaSvyazKlient){
			promptObratnayaSvyazKlient();
			return true;
		}
/*
		if (item == menuZayavkaNaPerevodVdosudebnye) {
			promptZayavkaNaPerevodVdosudebnye();
			return true;
		}
*/


		return false;
	}

	/*
		void promptZayavkaNaPerevodVdosudebnye(){
			Auxiliary.pickConfirm(this,"Заявка на перевод в досудебные","Отправить",new Task(){
				@Override
				public void doTask() {
					sendZayavkaNaPerevodVdosudebnye();
				}
			});
		}
		void sendZayavkaNaPerevodVdosudebnye(){

			final Note result = new Note();
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/CreateClientTransferRequest/"
							+ ApplicationHoreca.getInstance().getClientInfo().getKod() ;
					//url="https://testservice.swlife.ru/okunev_hrc/hs/Planshet/CreateClientTransferRequest/277793";
					try {
						byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						Bough rr=Bough.parseJSON(new String(b, "UTF-8"));
						//result.value(new String(b, "UTF-8"));
						result.value(rr.child("Message").value.property.value());
					} catch (Throwable t) {
						t.printStackTrace();
						result.value(t.getMessage());
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					Auxiliary.warn(result.value(), Activity_BidsContractsEtc_2.this);
				}
			}).status.is("Подождите...").start(this);
		}*/
	void sendRassylkaSchetovNaOplatu(final String email){
		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/CreateClientMailScore/"
						+ ApplicationHoreca.getInstance().getClientInfo().getKod()
						+ "/" + email;
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					result.value(new String(b, "UTF-8"));
				}catch(Throwable t){
					t.printStackTrace();
					result.value(t.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(result.value(), Activity_BidsContractsEtc_2.this);
			}
		}).status.is("Подождите...").start(this);
	}

	void promptRassylkaSchetovNaOplatuNew(){
		final Note newEmail = new Note();
		Auxiliary.pickString(this, "", newEmail, "Добавить e-mail в рассылку", new Task(){
			@Override
			public void doTask(){
				if(newEmail.value().contains("@")){
					sendRassylkaSchetovNaOplatu(newEmail.value());
				}else{
					Auxiliary.warn("Введите e-mail", Activity_BidsContractsEtc_2.this);
				}
			}
		});
	}

	void promptRassylkaSchetovNaOplatuDelete(final String email){
		Auxiliary.pickConfirm(Activity_BidsContractsEtc_2.this, "Удалить e-mail из рассылки", "Удалить", new Task(){
			@Override
			public void doTask(){
				final Note result = new Note();
				new Expect().task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/Planshet/DeleteClientMailScore/"
									+ ApplicationHoreca.getInstance().getClientInfo().getKod()
									+ "/" + email.trim();

							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							result.value(new String(bytes, "UTF-8"));
						}catch(Exception e){
							e.printStackTrace();
							result.value(e.getMessage());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.inform(result.value(), Activity_BidsContractsEtc_2.this);
					}
				}).status.is("Удаление...").start(Activity_BidsContractsEtc_2.this);
			}
		});
	}

	void promptRassylkaSchetovNaOplatuList(final Vector<Bough> data){

		String[] listItems = new String[data.size()];
		for(int i = 0; i < data.size(); i++){

			String one = data.get(i).child("Mail").value.property.value();
			System.out.println(one);
			listItems[i] = one;
		}
		final Numeric defaultSelection = new Numeric();
		String title = "Рассылка счетов на оплату";
		Task afterSelect = new Task(){
			@Override
			public void doTask(){
				promptRassylkaSchetovNaOplatuDelete(data.get(defaultSelection.value().intValue()).child("Mail").value.property.value());
			}
		};
		String positiveButtonTitle = "Добавить";
		Task callbackPositiveBtn = new Task(){
			@Override
			public void doTask(){
				promptRassylkaSchetovNaOplatuNew();
			}
		};
		Auxiliary.pickSingleChoice(this, listItems, defaultSelection, title, afterSelect, positiveButtonTitle, callbackPositiveBtn, null, null);
	}

	boolean okObratnayaSvyazKlient(){
		if(docNumObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не заполнен номер реализации", this);
			return false;
		}
		if(dateObratnayaSvyazKlient.value() < 3){
			Auxiliary.warn("Не заполнена дата реализации", this);
			return false;
		}
		if(commentObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не заполнен комментарий", this);
			return false;
		}
		if(artikulObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не указан артикул", this);
			return false;
		}
		if(etiketkaObratnayaSvyazKlient.value().length() < 3 && tovarObratnayaSvyazKlient.value().length() < 3){
			Auxiliary.warn("Не добавлены фото", this);
			return false;
		}
		return true;
	}

	void promptObratnayaSvyazKlient(){

		Auxiliary.pick(this, "", new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("Обратная связь").top().is(Auxiliary.tapSize * 0.5).left().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("№ реализации").labelAlignRightTop().top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(docNumObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("дата реализации").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactDate(this).date.is(dateObratnayaSvyazKlient).format.is("dd.MM.yyyy").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("комментарий").labelAlignRightTop().top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(commentObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 3.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("артикул").labelAlignRightTop().top().is(Auxiliary.tapSize * 4.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(artikulObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("фото этикетки").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(etiketkaObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 7.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task(){
							@Override
							public void doTask(){
								etiketkaObratnayaSvyazKlient.value("");
							}
						}).left().is(Auxiliary.tapSize * 7.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(this).labelText.is("...").afterTap.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.startMediaGallery(Activity_BidsContractsEtc_2.this, etiketkaObratnayaSvyazKlientPicture);
							}
						}).left().is(Auxiliary.tapSize * 8.5).top().is(Auxiliary.tapSize * 6).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("фото товара").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 7).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(tovarObratnayaSvyazKlient).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 7.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task(){
							@Override
							public void doTask(){
								tovarObratnayaSvyazKlient.value("");
							}
						}).left().is(Auxiliary.tapSize * 7.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//
						.child(new Knob(this).labelText.is("...").afterTap.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.startMediaGallery(Activity_BidsContractsEtc_2.this, tovarObratnayaSvyazKlientPicture);
							}
						}).left().is(Auxiliary.tapSize * 8.5).top().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1.0).height().is(Auxiliary.tapSize * 1))//


						.width().is(Auxiliary.tapSize * 10)//
						.height().is(Auxiliary.tapSize * 11)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						if(okObratnayaSvyazKlient()){
							sendObratnayaSvyazKlient();
						}
					}
				}, null, null, null, null);
	}

	void sendObratnayaSvyazKlient(){
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/FeedbackCustomer/Создать/" + Cfg.whoCheckListOwner();
		System.out.println("url " + url);
		String data = "{"
				+ "\n	\"NumberDoc\": \"" + Auxiliary.mssqlTime.format(new Date()) + "\","
				+ "\n	\"CodClient\": \"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\","
				+ "\n	\"Товары\": [{"
				+ "\n			\"Article\": \"" + artikulObratnayaSvyazKlient.value() + "\","
				+ "\n			\"NumberNac\": \"" + docNumObratnayaSvyazKlient.value() + "\","
				+ "\n			\"DateNac\": \"" + Auxiliary.sqliteDate.format(new Date(dateObratnayaSvyazKlient.value().longValue())) + "\","
				+ "\n			\"Comment\": \"" + commentObratnayaSvyazKlient.value() + "\""
				+ "\n		}"
				+ "\n	],"
				+ "\n	\"Files\": [";
		if(etiketkaObratnayaSvyazKlient.value().length() > 3){
			String filePath = etiketkaObratnayaSvyazKlient.value().trim();
			String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
			String[] spl = filePath.split("\\.");
			String rash = spl[spl.length - 1];
			data = data + "\n		{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			if(tovarObratnayaSvyazKlient.value().length() > 3){
				filePath = tovarObratnayaSvyazKlient.value().trim();
				encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
				spl = filePath.split("\\.");
				rash = spl[spl.length - 1];
				data = data + "\n		,{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			}
		}else{
			if(tovarObratnayaSvyazKlient.value().length() > 3){
				String filePath = tovarObratnayaSvyazKlient.value().trim();
				String encodedFile = android.util.Base64.encodeToString(SystemHelper.readBytesFromFile(new File(filePath)), android.util.Base64.NO_WRAP);
				String[] spl = filePath.split("\\.");
				String rash = spl[spl.length - 1];
				data = data + "\n		{\"File\": \"" + encodedFile + "\", \"rassh\": \"" + rash + "\"}";
			}
		}
		data = data + "\n	]"
				+ "\n}"
				+ "\n";
		final String body = data;
		//https://service.swlife.ru/hrc120107/hs/FeedbackCustomer/Создать/hrc221
		//if(etiketkaObratnayaSvyazKlient.value().length() < 3 && tovarObratnayaSvyazKlient.value().length() < 3){
		//14:44
		System.out.println("body " + body);
		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//Bough b = Auxiliary.loadTextFromPublicPOST(url, post, 99, "UTF-8");
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, body.getBytes("UTF-8"), 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					System.out.println(b.dumpXML());
					result.value(Bough.parseJSON(b.child("raw").value.property.value()).child("Сообщение").value.property.value());

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Результат: " + result.value(), Activity_BidsContractsEtc_2.this);
			}
		}).status.is("Отправка").start(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		System.out.println("onActivityResult " + requestCode + "/" + resultCode + "/" + data);
		super.onActivityResult(requestCode, resultCode, data);
		String filePath = null;
		Uri uri = null;
		if(resultCode == RESULT_OK){
			switch(requestCode){
				case etiketkaObratnayaSvyazKlientPicture:
					uri = data.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать " + uri, Activity_BidsContractsEtc_2.this);
						return;
					}
					etiketkaObratnayaSvyazKlient.value(filePath);
					break;
				case tovarObratnayaSvyazKlientPicture:
					uri = data.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать " + uri, Activity_BidsContractsEtc_2.this);
						return;
					}
					tovarObratnayaSvyazKlient.value(filePath);
					break;
			}
		}
	}

	void promptRassylkaSchetovNaOplatu(){
		final Vector<Bough> items = new Vector<Bough>();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/GetClientMailScore/" + ApplicationHoreca.getInstance().getClientInfo().getKod();//88319
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = "{item:" + new String(b, "UTF-8") + "}";
					//System.out.println(txt);
					Bough data = Bough.parseJSON(txt);
					Vector<Bough> allitems = data.children("item");
					for(int i = 0; i < allitems.size(); i++){
						items.add(allitems.get(i));
					}
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				promptRassylkaSchetovNaOplatuList(items);
			}
		}).status.is("Подождите...").start(this);
	}

	void promptAktSverki(){
		final Vector<Bough> items = new Vector<Bough>();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/GetClientMail/" + ApplicationHoreca.getInstance().getClientInfo().getKod();//88319
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = "{item:" + new String(b, "UTF-8") + "}";
					Bough data = Bough.parseJSON(txt);
					//System.out.println(data.dumpXML());
					Vector<Bough> allitems = data.children("item");
					for(int i = 0; i < allitems.size(); i++){
						items.add(allitems.get(i));
					}
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				prompAktSverkiList(items);
			}
		}).status.is("Подождите...").start(this);
	}

	void prompAktSverkiList(final Vector<Bough> data){
		String[] listItems = new String[data.size()];
		for(int i = 0; i < data.size(); i++){

			String one = data.get(i).child("Mail").value.property.value() + ", " + Auxiliary.tryReFormatDate(data.get(i).child("Data").value.property.value(), "yyyyMMdd", "dd.MM.yyyy");
			System.out.println(one);
			listItems[i] = one;
		}
		final Numeric defaultSelection = new Numeric();
		String title = "Рассылка актов сверки";
		Task afterSelect = new Task(){
			@Override
			public void doTask(){
				prompAktSverkiDelete(data.get(defaultSelection.value().intValue()).child("Mail").value.property.value());
			}
		};
		String positiveButtonTitle = "Добавить";
		Task callbackPositiveBtn = new Task(){
			@Override
			public void doTask(){
				prompAktSverkiNew();
			}
		};
		Auxiliary.pickSingleChoice(this, listItems, defaultSelection, title, afterSelect, positiveButtonTitle, callbackPositiveBtn, null, null);
	}

	void prompAktSverkiDelete(final String Mail){
		//System.out.println("- " + KodKlienta + "/" + Mail);
		Auxiliary.pickConfirm(Activity_BidsContractsEtc_2.this, "Удалить e-mail из рассылки", "Удалить", new Task(){
			@Override
			public void doTask(){
				final Note result = new Note();
				new Expect().task.is(new Task(){
					@Override
					public void doTask(){
						try{
							String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/Planshet/DeleteClientMail/" + ApplicationHoreca.getInstance().getClientInfo().getKod()
									+ "/" + Mail.trim();

							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							result.value(new String(bytes, "UTF-8"));
						}catch(Exception e){
							e.printStackTrace();
							result.value(e.getMessage());
						}
					}
				}).afterDone.is(new Task(){
					@Override
					public void doTask(){
						Auxiliary.inform(result.value(), Activity_BidsContractsEtc_2.this);
					}
				}).status.is("Удаление...").start(Activity_BidsContractsEtc_2.this);
			}
		});
	}

	void prompAktSverkiNew(){
		final Note email = new Note();
		final Numeric date = new Numeric();
		Auxiliary.pick(this, "Настройка отправки актов сверки"//
				, new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("Дата").labelAlignRightTop()//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 1.0)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).date.is(date).format.is("dd.MM.yyyy")//
								.left().is(Auxiliary.tapSize * 4)//
								.top().is(Auxiliary.tapSize * 0.5)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 1))//
						.child(new Decor(this).labelText.is("e-mail").labelAlignRightTop()//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 2.0)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(this).text.is(email)//
								.left().is(Auxiliary.tapSize * 4)//
								.top().is(Auxiliary.tapSize * 1.5)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 1))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 4.5)//
				, "Добавить", new Task(){
					@Override
					public void doTask(){
						final Note result = new Note();
						new Expect().task.is(new Task(){
							@Override
							public void doTask(){
								try{
									Calendar c1 = Calendar.getInstance();
									c1.setTimeInMillis(date.value().longValue());
									String dt = Auxiliary.short1cDate.format(c1.getTime());
									System.out.println("send new " + email.value() + ", " + dt);
									String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
											+ "/hs/Planshet/CreateClientMail/" + ApplicationHoreca.getInstance().getClientInfo().getKod()
											+ "/" + email.value().trim()
											+ "/" + dt;//88319
									byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
									result.value(new String(bytes, "UTF-8"));
								}catch(Exception e){
									e.printStackTrace();
									result.value(e.getMessage());
								}
							}
						}).afterDone.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.inform(result.value(), Activity_BidsContractsEtc_2.this);
							}
						}).status.is("Отправка...").start(Activity_BidsContractsEtc_2.this);
					}
				}, null, null, null, null);
	}

	void openCarMap(String title, String key){
		Intent intent = new Intent();
		intent.putExtra("carGPSid", key);
		intent.putExtra("title", title);
		intent.setClass(this, sweetlife.android10.supervisor.ActivityCarDelivery.class);
		this.startActivity(intent);
	}

	void startCarMap(){
/*
{
    "Данные": [
        {
        "ГосНомер": "М728ХО",
        "АПИ": "7193",
        "Водитель": "Ефремов Алексей Сергеевич",
        "Телефон": "79066362216"
        }
    ],
    "Сообщение": ""
}
I/System.out: <>
I/System.out: 	<Данные>
I/System.out: 		<ГосНомер>М728ХО</ГосНомер>
I/System.out: 		<АПИ>7193</АПИ>
I/System.out: 		<Водитель>Ефремов Алексей Сергеевич</Водитель>
I/System.out: 		<Телефон>79066362216</Телефон>
I/System.out: 	</Данные>
I/System.out: 	<Сообщение></Сообщение>
I/System.out: </>
*/
		final Note title = new Note();
		final Note key = new Note();

		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ObnovlenieInfo/%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C%D0%A2%D0%B5%D0%BA%D1%83%D1%89%D0%B8%D0%B9%D0%90%D0%B2%D1%82%D0%BE%D0%BC%D0%BE%D0%B1%D0%B8%D0%BB%D1%8C/" + ApplicationHoreca.getInstance().getClientInfo().getKod();//88319
				//String url = "https://service.swlife.ru/hrc120107/hs/ObnovlenieInfo/%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C%D0%A2%D0%B5%D0%BA%D1%83%D1%89%D0%B8%D0%B9%D0%90%D0%B2%D1%82%D0%BE%D0%BC%D0%BE%D0%B1%D0%B8%D0%BB%D1%8C/" + ApplicationHoreca.getInstance().getClientInfo().getKod();//88319
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(b, "UTF-8");
					Bough data = Bough.parseJSON(txt);
					System.out.println(data.dumpXML());
					key.value(data.child("Данные").child("АПИ").value.property.value());
					title.value(
							data.child("Данные").child("Телефон").value.property.value()
									+ ", " + data.child("Данные").child("Водитель").value.property.value()
									+ ", гос.номер " + data.child("Данные").child("ГосНомер").value.property.value()
					);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				if(key.value().length() > 1){
					openCarMap(title.value(), key.value());
				}else{
					Auxiliary.warn("Нет доставки на данный момент", Activity_BidsContractsEtc_2.this);
				}
			}
		}).status.is("Подождите...").start(this);
	}

	void promptVislatNakladnieNaPochtu(){
		System.out.println("promptVislatNakladnieNaPochtu");
		if(Settings.startVislatNakladnieNaPochtu.value() <= 0){
			Calendar c1 = Calendar.getInstance();
			c1.set(Calendar.DAY_OF_MONTH, 1);
			//String d1 = Auxiliary.short1cDate.format(c1.getTime());
			Settings.startVislatNakladnieNaPochtu.value((double)c1.getTimeInMillis());
		}
		if(Settings.endVislatNakladnieNaPochtu.value() <= 0){
			Calendar c2 = Calendar.getInstance();
			//String d2 = Auxiliary.short1cDate.format(c2.getTime());
			Settings.endVislatNakladnieNaPochtu.value((double)c2.getTimeInMillis());
		}
		Auxiliary.pick(this, "Выслать накладные"//
				, new SubLayoutless(this)//
						.child(new RedactToggle(this).labelText.is("Головной контрагент").yes.is(Settings.emailPoINNToSend)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 0.0)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("Период")//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 1.0)//
								.width().is(Auxiliary.tapSize * 5 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(this).date.is(Settings.startVislatNakladnieNaPochtu).format.is("dd.MM.yyyy")//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 1.5)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 1))//
						.child(new RedactDate(this).date.is(Settings.endVislatNakladnieNaPochtu).format.is("dd.MM.yyyy")//
								.left().is(Auxiliary.tapSize * 3.5)//
								.top().is(Auxiliary.tapSize * 1.5)//
								.width().is(Auxiliary.tapSize * 3)//
								.height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("e-mail")//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 3.0)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(this).text.is(Settings.emailToSend)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 3.5)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 8)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						String from = Auxiliary.short1cDate.format(new Date(Settings.startVislatNakladnieNaPochtu.value().longValue()));
						String to = Auxiliary.short1cDate.format(new Date(Settings.endVislatNakladnieNaPochtu.value().longValue()));

						final String url =
								Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
										+ "/hs/Prilozhenie/InvoiceInMail/"
										+ ApplicationHoreca.getInstance().getClientInfo().getKod()
										+ "/" + Settings.emailToSend.value()
										+ "/" + from
										+ "/" + to
										+ "/" + ((Settings.emailPoINNToSend.value() == true) ? "1" : "0")
										+ "/" + Cfg.whoCheckListOwner();

						System.out.println(url);
						final Note result = new Note();
						new Expect().task.is(new Task(){
							@Override
							public void doTask(){
								try{
									byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
									result.value(new String(bytes, "UTF-8"));
								}catch(Exception e){
									e.printStackTrace();
									result.value(e.getMessage());
								}
							}
						}).afterDone.is(new Task(){
							@Override
							public void doTask(){
								Auxiliary.warn("Результат: " + result.value(), Activity_BidsContractsEtc_2.this);
							}
						}).status.is("Отправка...").start(Activity_BidsContractsEtc_2.this);
					}
				}, null, null, null, null);
	}
/*
	void promptZayavkaRazdelenieNakladnih(){
		final Calendar current = Calendar.getInstance();
		final Numeric result = new Numeric();
		Auxiliary.pickDate(this, current, result, new Task(){
			public void doTask(){
				String date = Auxiliary.short1cDate.format(new Date(result.value().longValue()));
				//System.out.println("result " + date + ", " + ApplicationHoreca.getInstance().getClientInfo().getKod() + ", " + ApplicationHoreca.getInstance().getClientInfo().getName());
				String message = "Отправить заявку на разделение накладных на "
						+ Auxiliary.rusDate.format(new Date(result.value().longValue()))
						+ " для клиента " + ApplicationHoreca.getInstance().getClientInfo().getName();
				Auxiliary.pickConfirm(Activity_BidsContractsEtc_2.this, message, "Отправить", new Task(){
					public void doTask(){
						sendZayavkaRazdelenieNakladnih("" + ApplicationHoreca.getInstance().getClientInfo().getKod(), date);
					}
				});
			}
		});
	}
*/
/*
	void sendZayavkaRazdelenieNakladnih(String kod, String data){
		//https://service.swlife.ru/hrc120107/hs/RazdelenieNakladnih/104717/20230104
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/RazdelenieNakladnih/" + kod + "/" + data;
		//System.out.println(url);
		final Note res = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String s = new String(b, "utf-8");
					res.value(s);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(res.value(), Activity_BidsContractsEtc_2.this);
			}
		}).status.is("Отправка данных").start(this);
	}
*/
	void promptLimit(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityLimitList.class);
		this.startActivityForResult(intent, 0);
	}

	void ___testPromptLimit(){
		//Report_Base.startPing();
		//final String url="http://89.109.7.162/shatov/hs/UvelLimita/"+Cfg.currentHRC()+"/";
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/UvelLimita/" + Cfg.whoCheckListOwner() + "/";
		String limit = "123";
		String delay = "321";
		String rem = "Йцукерт";
		ApplicationHoreca.getInstance().getClientInfo().getKod();
		//final String post="{\"Лимит\":\"100\",\"Отсрочка\":\"10\",\"КодКлиента\":\"133623\",\"Комментарий\":\"слёзно прошу\"}";
		final String post = "{\"Лимит\":\"" + limit + "\",\"Отсрочка\":\"" + delay + "\",\"КодКлиента\":\"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\",\"Комментарий\":\"" + rem + "\"}";
		System.out.println(url);
		System.out.println(post);
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					//Bough b = Auxiliary.loadTextFromPublicPOST(url, post, 99, "UTF-8");
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, post.getBytes("UTF-8"), 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					System.out.println(b.dumpXML());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//
			}
		}).status.is("Отправка заявки").start(this);
	}

	public void resetTitle(){
		setTitle(ApplicationHoreca.getInstance().getClientInfo().getName() + " (долги по накладным: " + ApplicationHoreca.getInstance().getClientInfo().dolgMessage + ")");
	}

	void doRequestSklad(){
		if(Cfg.userLevel(Cfg.DBHRC()) > 0){
			ApplicationHoreca a = ApplicationHoreca.getInstance();
			ClientInfo ci = a.getClientInfo();
			String kod = ci.getKod();
			String poluchitBazuOtgruzki = "%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C%D0%91%D0%B0%D0%B7%D1%83%D0%9E%D1%82%D0%B3%D1%80%D1%83%D0%B7%D0%BA%D0%B8";//ПолучитьБазуОтгрузки
			final String url = Settings.getInstance().getBaseURL() //
					+ Settings.selectedBase1C()//
					//+ "GolovaNew"//
					+ "/hs/Kontragenti/" + poluchitBazuOtgruzki + "/" //
					+ kod.trim();
			final Note sklad = new Note();
			new Expect().task.is(new Task(){
				@Override
				public void doTask(){
					try{
						byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						String s = new String(b, "utf-8");
						//System.out.println("result " + s);
						String[] a = s.split(":");
						if(a[0].equals("ОК")){
							String skladNum = a[1];
							sklad.value(skladNum);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}).afterDone.is(new Task(){
				@Override
				public void doTask(){
					if(!sklad.value().equals("")){
						promptSklad(sklad.value());
					}
				}
			}).status.is("Получение данных").start(this);
		}else{
			Auxiliary.warn("Только для супервайзеров", this);
		}
	}

	void promptSklad(String skladNum){
		final Numeric sel = new Numeric();
		if(skladNum.equals("000000009")){
			sel.value(1);
		}
		Auxiliary.pickSingleChoice(this, new String[]{"Только 8 склад", "8 и 17 склад" }, sel//
				, "Склады отгрузок"//
				, new Task(){
					@Override
					public void doTask(){
						//System.out.println(sel.value());
						String sklad = "000000007";
						if(sel.value() > 0){
							sklad = "000000009";
						}
						sendSklad(sklad);
					}
				}//
				, null//
				, null//
				, null//
				, null);
	}

	void sendSklad(String sklad){
		ApplicationHoreca a = ApplicationHoreca.getInstance();
		ClientInfo ci = a.getClientInfo();
		String kod = ci.getKod();
		String izmenitBazuOtgruzki = "%D0%98%D0%B7%D0%BC%D0%B5%D0%BD%D0%B8%D1%82%D1%8C%D0%91%D0%B0%D0%B7%D1%83%D0%9E%D1%82%D0%B3%D1%80%D1%83%D0%B7%D0%BA%D0%B8";//ИзменитьБазуОтгрузки
		final String url = Settings.getInstance().getBaseURL() //
				+ Settings.selectedBase1C()//
				//+ "GolovaNew"//
				+ "/hs/Kontragenti/" + izmenitBazuOtgruzki + "/" //
				+ kod.trim() + "/" + sklad;
		final Note res = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String s = new String(b, "utf-8");
					//System.out.println("result " + s);
					res.value(s);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(res.value(), Activity_BidsContractsEtc_2.this);
			}
		}).status.is("Отправка данных").start(this);
	}

	public static void promptRequestMailList(final Activity a, final Vector<String> artikul){
		System.out.println("promptRequestMailList: " + artikul.size());
		for(int i = 0; i < artikul.size(); i++){
			System.out.println(i + ": " + artikul.get(i));
		}
		Auxiliary.pick(a, "Выслать шаблон заказа"//
				, new SubLayoutless(a)//
						.child(new RedactToggle(a).labelText.is("По спецификации").yes.is(Settings.emailPoSpecificacii)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 0.5)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 1))//
						.child(new RedactToggle(a).labelText.is("По ИНН").yes.is(Settings.emailPoINNToSend)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 1.5)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 1))//
						.child(new RedactToggle(a).labelText.is("Номенклатура по группам").yes.is(Settings.emailGroupingToSend)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 2.5)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 1))//
						.child(new Decor(a).labelText.is("e-mail")//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 3.5)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(a).text.is(Settings.emailToSend)//
								.left().is(Auxiliary.tapSize * 0.5)//
								.top().is(Auxiliary.tapSize * 4)//
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize)//
								.height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 7)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						sendRequestMailList(a, artikul, Settings.emailToSend.value(), ApplicationHoreca.getInstance().getClientInfo().getKod());
					}
				}, null, null, null, null);
	}

	/*
		void _doRequestMailList(Activity a) {
			//Note email=new Note();
			Auxiliary.pickString(this, "На какой e-mail отправить", Settings.emailToSend, "Отправить", new Task() {
				@Override
				public void doTask() {
					//System.out.println(Settings.emailToSend.value());
					sendRequestMailList(a,Settings.emailToSend.value(), ApplicationHoreca.getInstance().getClientInfo().getKod());
				}
			});
		}
	*/
	public static void sendRequestMailList(final Activity a, final Vector<String> artikul, String email, String kodKlient){
		//String url=Settings.getInstance().getBaseURL()+"/hrc120107/hs/ZakazNaShablon/121162/20190101/20190301/surikov@swlife.nnov.ru";
		//Принята заявка на отправку шаблона для заказа
		String da = "%D0%B4%D0%B0";
		String net = "%D0%BD%D0%B5%D1%82";
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.MONTH, -3);
		String d1 = Auxiliary.short1cDate.format(c1.getTime());
		Calendar c2 = Calendar.getInstance();
		String d2 = Auxiliary.short1cDate.format(c2.getTime());
		String poINN = Settings.emailPoINNToSend.value() ? da : net;
		String poSpec = Settings.emailPoSpecificacii.value() ? da : net;

		// ? "%D0%B4%D0%B0"
		//"да"
		//: "%D0%BD%D0%B5%D1%82"//"нет""http://89.109.7.162/shatov"
		String rawlist = Settings.emailGroupingToSend.value() ? net : da;
		//curl -X POST -d '103576,102050,91602' http://89.109.7.162/shatov/hs/ZakazNaShablon/135847/20100101/20200813/surikov@swlife.nnov.ru/нет/да
		final String url =
				Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
						//"http://89.109.7.162/shatov"
						//"http://testservice.swlife.ru/shatov"
						+ "/hs/ZakazNaShablon/"
						+ kodKlient + "/" + d1 + "/" + d2 + "/" + email + "/" + poINN + "/" + rawlist
						+ "/" + poSpec;


		//final String url =  "http://89.109.7.162/shatov/hs/ZakazNaShablon/" + kodKlient + "/" + d1 + "/" + d2 + "/" + email + "/" + poINN+ "/" + rawlist;


		System.out.println(url);
		final Note result = new Note();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					String artLine = "";
					String artDelimiter = "";
					for(int i = 0; i < artikul.size(); i++){
						artLine = artLine + artDelimiter + artikul.get(i);
						artDelimiter = ",";
					}
					//byte[] b = Auxiliary.loadFileFromPublicURL(url);
					//Bough resp=Auxiliary.loadTextFromPublicPOST(url, artLine, 15000, "UTF-8");
					System.out.println("artLine: " + artLine);
					Bough resp = Auxiliary.loadTextFromPrivatePOST(url, artLine.getBytes("UTF-8"), 180000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					System.out.println("resp: " + resp.dumpXML());
					//String txt = new String(b, "UTF-8");
					//result.value(txt);
					result.value(resp.child("message").value.property.value()
							+ ": " + resp.child("raw").value.property.value()
					);
				}catch(Exception e){
					e.printStackTrace();
					result.value(e.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Результат: " + result.value(), a);
			}
		}).status.is("Отправка...").start(a);
	}

	private void readExtras(){
		Bundle extras = getIntent().getExtras();
		ApplicationHoreca.getInstance().setClientInfo(new ClientInfo(ApplicationHoreca.getInstance().getDataBase(), extras.getString("client_id")));
		mChosedDay = Calendar.getInstance();
		mChosedDay.setTimeInMillis(extras.getLong("day", mChosedDay.getTimeInMillis()));
		mIsEditable = DateTimeHelper.getOnlyDateInfo(mChosedDay)//
				.compareTo(DateTimeHelper.getOnlyDateInfo(Calendar.getInstance()))//
				== 0
				? true
				: false;
		if(mIsEditable){
			if(Requests.IsSyncronizationDateLater(-5)){
				//LogHelper.debug("Requests.IsSyncronizationDateLater( -5 ) true");
				//findViewById(R.id.layout_add).setVisibility(View.GONE);
				//findViewById(R.id.secondtab).setVisibility(View.GONE);
				//CreateErrorDialog(R.string.msg_sync_date_later5).show();
				Auxiliary.warn("Последняя сихронизация была 5 дней назад. Создание заказов невозможно.", Activity_BidsContractsEtc_2.this);
				mIsEditable = false;
			}else{
				//LogHelper.debug("Requests.IsSyncronizationDateLater( -5 ) false");
			}
		}else{
			UIHelper.quickWarning("Заявки на " + DateTimeHelper.UIDateString(mChosedDay.getTime()), this);
		}
	}

	void doObnoIstoria(){//http://89.109.7.162/GolovaNew/hs/ObnovlenieInfo/Istoriya?klient=82496
		ApplicationHoreca a = ApplicationHoreca.getInstance();
		ClientInfo ci = a.getClientInfo();
		String kod = ci.getKod();
		final String url = Settings.getInstance().getBaseURL() //
				+ Settings.selectedBase1C() + "/hs/ObnovlenieInfo/Istoriya?klient=" //
				+ kod.trim();
		//final String url = Settings.getInstance().getBaseURL() + "GolovaNew/hs/ObnovlenieInfo/DannyeMarshruta?hrc=" + mAppInstance.getCurrentAgent().getAgentName().trim();
		System.out.println(url);
		final Numeric cntr = new Numeric();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String s = new String(b, "utf-8");
					String[] commands = s.split("\n");
					for(int i = 0; i < commands.length; i++){
						String sql = commands[i].trim();
						if(sql.length() > 1){
							//System.out.println(i + ": " + sql);
							ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
							//Activity_BidsContractsEtc_2.this.mDB.execSQL(sql);
							cntr.value(cntr.value() + 1);
						}
					}
					//UpdateTask.refreshProdazhi_last(Activity_BidsContractsEtc_2.this.mDB);
					UpdateTask.refreshProdazhi_last(ApplicationHoreca.getInstance().getDataBase());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn("Реализаций обновлено " + cntr.value().intValue(), Activity_BidsContractsEtc_2.this);
			}
		}).status.is("Обновление БД").start(this);
	}
/*
	void showChekList() {

		if(checkedTerritoryKod != null) {
			startCheckList();
			return;
		}
		final Numeric nn = new Numeric().value(0);
		final Vector<Bough> names = new Vector<Bough>();
		for(int i = 0; i < Cfg.territory().children.size(); i++) {
			if(!ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim().equals(//
					Cfg.territory().children.get(i).child("hrc").value.property.value().trim())) {
				names.add(Cfg.territory().children.get(i));
			}
		}
		if(names.size() > 0) {
			final String[] ters = new String[names.size()];
			for(int i = 0; i < names.size(); i++) {
				ters[i] = names.get(i).child("territory").value.property.value()//
						          + " (" + names.get(i).child("hrc").value.property.value().trim() + ")";
			}
			Auxiliary.pickSingleChoice(Activity_BidsContractsEtc_2.this, ters, nn, null, new Task() {
				@Override
				public void doTask() {
					checkedTerritoryKod = names.get(nn.value().intValue()).child("kod").value.property.value().trim();

					startCheckList();
				}
			}, null, null, null, null);
		}
		else {
			Auxiliary.warn("Нет подчинённых территорий", this);
		}
	}
*/
	/*void startCheckList() {
		long id = ActivityCheckDocs.findOrCreateNew(checkedTerritoryKod);
		String cuKl = ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
		ActivityCheckList.addForKontragent(cuKl, checkedTerritoryKod, "" + id);
		Intent intent = new Intent(Activity_BidsContractsEtc_2.this, ActivityCheckList.class);
		intent.putExtra("doc_id", "" + id);
		intent.putExtra("kontragent", "" + cuKl);
		startActivity(intent);
	}*/

	void showPechati(){
		//System.out.println("showPechati");
		Intent intent = new Intent();
		intent.setClass(this, Activity_Pechati.class);
		startActivity(intent);
	}

	void showVzaimoraschety(){
		String curKod = ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
		//System.out.println("showVzaimoraschety "+curKod);
		//ActivityWebServicesReports.goLastPageTempName = "who";
		int nn = 0;
		Bough kk = Cfg.kontragentyForSelectedMarshrut();
		for(int ii = 0; ii < kk.children.size(); ii++){
			Bough row = kk.children.get(ii);
			String rowKod = row.child("kod").value.property.value().trim();
			//System.out.println("check "+curKod);
			if(rowKod.equals(curKod)){
				nn = ii;
				//System.out.println(row.dumpXML());
				break;
			}
		}
		//ActivityWebServicesReports.goLastPageTempValue = "" + nn;
		ReportVzaioraschetySpokupatelem.temporaryWho = nn;
		//System.out.println("showVzaimoraschety "+nn);
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc_2.this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
		intent.putExtra(ActivityWebServicesReports.goLastPageReportName, ReportVzaioraschetySpokupatelem.folderKey());
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//System.out.println("onCreate");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		readExtras();
		resetTitle();
		Cfg.refreshSkidkiKontragent(ApplicationHoreca.getInstance().getClientInfo().getKod());
		Cfg.refreshArtikleCount();
		Cfg.refreshNomenklatureGroups(ApplicationHoreca.getInstance().getDataBase());

		dataGrid = new DataGrid(this).center.is(true)//
				.headerHeight.is(0.5 * Auxiliary.tapSize).pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
				.beforeFlip.is(new Task(){
					@Override
					public void doTask(){
						//requeryGridData();
						//flipGrid();
					}
				});
		layoutless.child(new Decor(this)//
				.background.is(0x11000000)//
				.left().is(0)//
				.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
				.width().is(layoutless.width().property)//
				.height().is(1)//
		);
		knobZakazy = new Knob(this);
		layoutless.child(knobZakazy.afterTap.is(new Task(){
					@Override
					public void doTask(){
						switchPage(0);
					}
				})//
						.labelText.is("Заказы").left().is(layoutless.width().property.multiply(0.0 / 5))//
						.top().is(0)//
						.width().is(layoutless.width().property.divide(5))//
						.height().is(1 * Auxiliary.tapSize)//
		);
		knobFixCena = new Knob(this);
		layoutless.child(knobFixCena.afterTap.is(new Task(){
					@Override
					public void doTask(){
						switchPage(1);
					}
				})//
						.labelText.is("Фиксированные цены")
						.left().is(layoutless.width().property.multiply(1.0 / 5))//
						.top().is(0)//
						.width().is(layoutless.width().property.divide(5))//
						.height().is(1 * Auxiliary.tapSize)//
		);
		knobVozvrat = new Knob(this);
		layoutless.child(knobVozvrat.afterTap.is(new Task(){
					@Override
					public void doTask(){
						switchPage(2);
					}
				})//
						.labelText.is("Возвраты").left().is(layoutless.width().property.multiply(2.0 / 5))//
						.top().is(0)//
						.width().is(layoutless.width().property.divide(5))//
						.height().is(1 * Auxiliary.tapSize)//
		);
		knobSpec = new Knob(this);
		layoutless.child(knobSpec.afterTap.is(new Task(){
					@Override
					public void doTask(){
						switchPage(3);
					}
				})//
						.labelText.is("Спецификации").left().is(layoutless.width().property.multiply(3.0 / 5))//
						.top().is(0)//
						.width().is(layoutless.width().property.divide(5))//
						.height().is(1 * Auxiliary.tapSize)//
		);
		knobDegustacia = new Knob(this);
		layoutless.child(knobDegustacia.afterTap.is(new Task(){
					@Override
					public void doTask(){
						switchPage(4);
					}
				})//
						.labelText.is("Дегустация").left().is(layoutless.width().property.multiply(4.0 / 5))//
						.top().is(0)//
						.width().is(layoutless.width().property.divide(5))//
						.height().is(1 * Auxiliary.tapSize)//
		);
		layoutless.child(dataGrid//
				.columns(new Column[]{//
						columnDate.title.is("Дата").width.is(3 * Auxiliary.tapSize)//
						, columnInfo.title.is("0писание").width.is(Auxiliary.screenWidth(this) / 2 - 3 / 2 * Auxiliary.tapSize)//
						, columnComment.title.is("Комментарий").width.is(Auxiliary.screenWidth(this) / 2 - 3 / 2 * Auxiliary.tapSize)//
				})//
				.left().is(0)//
				.top().is(1 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this).afterTap.is(new Task(){
					@Override
					public void doTask(){
						if(gpsTimeExists30())
							beginVizitButtonClick();
					}
				})//
						.labelText.is("Начало визита").left().is(0)//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).afterTap.is(new Task(){
					@Override
					public void doTask(){
						if(mojnoZakrytVizit()){
							endVizitCasePrompt();
						}
					}
				})//
						.labelText.is("Конец визита").left().is(3 * Auxiliary.tapSize)//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).afterTap.is(new Task(){
					@Override
					public void doTask(){
						promptKlientPeople();
					}
				})//
						.labelText.is("Конт.инф.")
						.left().is(layoutless.width().property.minus(9 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).afterTap.is(new Task(){
					@Override
					public void doTask(){
						zametkiClick();
					}
				})//
						.labelText.is("Заметки").left().is(layoutless.width().property.minus(6 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		newZakaz = new Knob(this);
		layoutless.child(newZakaz.afterTap.is(new Task(){
					@Override
					public void doTask(){
						addButtonClick();
					}
				})//
						.labelText.is("Новый заказ")
						.left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		newFixCena = new Knob(this);
		layoutless.child(newFixCena.afterTap.is(new Task(){
					@Override
					public void doTask(){
						StartFixedPricesActivity(true, null);
					}
				})//
						.labelText.is("Новая заявка").left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		newVozvrat = new Knob(this);
		layoutless.child(newVozvrat.afterTap.is(new Task(){
					@Override
					public void doTask(){
						StartReturnsActivity(true, null);
					}
				})//
						.labelText.is("Новый возврат").left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		newSpecificacia = new Knob(this);
		layoutless.child(newSpecificacia.afterTap.is(new Task(){
					@Override
					public void doTask(){
						dobavitSpecificaciu();
					}
				})//
						.labelText.is("Новая спец.").left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		newDegustas = new Knob(this);
		layoutless.child(newDegustas.afterTap.is(new Task(){
					@Override
					public void doTask(){
						dobavitDegustaciu();
					}
				})//
						.labelText.is("Новая дег.").left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(1 * Auxiliary.tapSize))//
						.width().is(3 * Auxiliary.tapSize)//
						.height().is(1 * Auxiliary.tapSize)//
		);
		//knobZakazy.setEnabled(false);
		refreshGrid();
		switchPage(0);
	}

	void flipGrid(){
	}

	void requeryGridData(){
	}

	void zametkiClick(){
		final Note note = new Note();
		Auxiliary.pickString(this, "Новая заметка", note
				, "Добавить", new Task(){
					public void doTask(){
						Activity_Zametki.addNew(note.value());
					}
				}, "Все заметки", new Task(){
					public void doTask(){
						zametkiOpen();
					}
				}, null, null
		);

	}

	void zametkiOpen(){
		Intent intent = new Intent();
		intent.setClass(this, sweetlife.android10.ui.Activity_Zametki.class);
		this.startActivity(intent);

	}

	void addButtonClick(){
		int ORDER_UPDATE = 5;
		StartBidsActivity(ORDER_UPDATE, null);
	}

	@Override
	protected void onResume(){
		super.onResume();
		Activity_Bid.unLockCreateNewOrder();
		refreshGrid();
	}

	void refreshGrid(){
		dataGrid.clearColumns();
		if(!knobZakazy.isEnabled()){
			readOrders();
		}
		if(!knobFixCena.isEnabled()){
			readFixPrice();
		}
		if(!knobVozvrat.isEnabled()){
			readReturns();
		}
		if(!knobSpec.isEnabled()){
			readSpecificacia();
		}
		if(!knobDegustacia.isEnabled()){
			readDegustacia();
		}
		dataGrid.refresh();
	}

	void readReturns(){
		final ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String sql = "select"//
				+ "\n 		v._id as _id"//
				+ "\n 		,v._idrref as _idrref"//
				+ "\n 		,v.nomer as nomer"//
				+ "\n 		,v.data as data"//
				+ "\n 		,v.dataotgruzki as dataotgruzki "//
				+ "\n 		,v.proveden as proveden"//
				+ "\n 		,count(t._id) as cnt"//
				+ "\n 		,v.AktPretenziy as aktPretenziy"//
				+ "\n 		,v._Version as _Version"//
				+ "\n 		from ZayavkaNaVozvrat v"//
				+ "\n 		join ZayavkaNaVozvrat_Tovary t on t._ZayavkaNaVozvrat_idrref=v._idrref"//
				+ "\n 		where v.kontragent=" + mAppInstance.getClientInfo().getID() + ""//
				+ "\n 		group by v._id,v._idrref,v.nomer,v.data,v.dataotgruzki,v.proveden"//
				+ "\n 		order by dataotgruzki"//
				+ "\n 		limit 77"//
				;
		//System.out.println(sql);
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		gridData = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		for(int i = 0; i < gridData.children.size(); i++){
			final Bough row = gridData.children.get(i);
			Task tapTask = new Task(){
				public void doTask(){
					try{
						ZayavkaNaVozvrat bid //
								= new ZayavkaNaVozvrat(//
								(int)Numeric.string2double(row.child("_id").value.property.value())//
								, "x'" + row.child("_idrref").value.property.value() + "'"//
								, Auxiliary.sqliteDate.parse(row.child("data").value.property.value())//
								, row.child("nomer").value.property.value()//
								, mAppInstance.getClientInfo().getID()//
								, mAppInstance.getClientInfo().getKod()//
								, mAppInstance.getClientInfo().getName()//
								, Auxiliary.sqliteDate.parse(row.child("dataotgruzki").value.property.value())//
								, row.child("aktPretenziy").value.property.value()//
								, row.child("proveden").value.property.value().equals("01")//
								, false, row.child("_Version").value.property.value()//
						);
						StartReturnsActivity(!row.child("vygruzhen").value.property.value().equals("01"), bid);
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
			};
			int bg = 0x00ff0000;
			String vigrugen = "не выгружена";
			if(!row.child("proveden").value.property.value().equals("00")){
				vigrugen = "выгружена";
				bg = 0x3300ff00;
			}
			String when = Auxiliary.tryReFormatDate(row.child("data").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
			columnDate.cell(when, bg, tapTask, vigrugen);
			columnInfo.cell(row.child("nomer").value.property.value(), bg, tapTask//
					, "дата отгрузки " //
							+ Auxiliary.tryReFormatDate(row.child("dataotgruzki").value.property.value(), "yyyy-MM-dd", "dd.MM.yy")//
			);
			columnComment.cell("количество: " + row.child("cnt").value.property.value(), bg, tapTask, "");
		}
	}

	void readDegustacia(){
		final ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String sql = "select _id,otgruzka,comment,kontragent,status"//
				+ " from ZayavkaNaDegustaciu "//
				+ " where kontragent=" + ApplicationHoreca.getInstance().getClientInfo().getID().trim()//
				+ " order by status,otgruzka desc"//
				+ " limit 77";
		//System.out.println(sql);
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		gridData = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
		for(int i = 0; i < gridData.children.size(); i++){
			final Bough row = gridData.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tapTask = new Task(){
				public void doTask(){
					doEditDegustaciu(_id);
				}
			};
			int bg = 0x00ff0000;
			String vigrugen = "не выгружена";
			if(!row.child("status").value.property.value().equals("0")){
				vigrugen = "выгружена";
				bg = 0x3300ff00;
			}
			String when = Auxiliary.tryReFormatDate(row.child("otgruzka").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
			columnDate.cell(vigrugen, bg, tapTask, "");
			columnInfo.cell(//
					when, bg, tapTask, "");
			columnComment.cell(row.child("comment").value.property.value(), bg, tapTask, "");
		}
	}

	void readSpecificacia(){
		final ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String sql = "select _id,createDate,fromDate,toDate,comment,hrc,kod,status"//
				+ " from ZayavkaNaSpecifikasia"//
				+ " where kod='" + mAppInstance.getClientInfo().getKod().trim() + "'"//
				+ " order by toDate desc,fromDate desc"//
				+ " limit 77";
		//System.out.println(sql);
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		gridData = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
		for(int i = 0; i < gridData.children.size(); i++){
			final Bough row = gridData.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tapTask = new Task(){
				public void doTask(){
					doEditSpecificacia(_id);
				}
			};
			int bg = 0x00ff0000;
			String vigrugen = "не выгружена";
			if(!row.child("status").value.property.value().equals("0")){
				vigrugen = "выгружена";
				bg = 0x3300ff00;
			}
			String when = Auxiliary.tryReFormatDate(row.child("createDate").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
			columnDate.cell(when, bg, tapTask, vigrugen);
			columnInfo.cell(//
					"с " //
							+ Auxiliary.tryReFormatDate(row.child("fromDate").value.property.value(), "yyyy-MM-dd", "dd.MM.yy")//
							+ " по " //
							+ Auxiliary.tryReFormatDate(row.child("toDate").value.property.value(), "yyyy-MM-dd", "dd.MM.yy")//
					, bg, tapTask, "");

			String comment = row.child("comment").value.property.value();
			String parts[] = comment.split("~");
			if(parts.length > 1){
				comment = parts[1];
			}
			columnComment.cell(comment, bg, tapTask, "");
		}
	}

	void readFixPrice(){
		final ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String sql = "select"//
				+ "\n 		    z._id as _id"//
				+ "\n 		    ,z.Nomer as nomer"//
				+ "\n 		    ,z._IDRRef as _IDRRef"//
				+ "\n 	    	,z.Data as dateCreate"//
				+ "\n 	    	,z.VremyaNachalaSkidkiPhiksCen as dateStart"//
				+ "\n 	    	,z.VremyaOkonchaniyaSkidkiPhiksCen as dateEnd"//
				+ "\n 	    	,z.Kommentariy as kommentariy"//
				+ "\n 	    	,z.vygruzhen as vygruzhen"//
				+ "\n 		from ZayavkaNaSkidki z "//
				+ "\n 	    	inner join Kontragenty k on k.[_IDRRef] = z.[Kontragent]"//
				+ "\n 		where z.[Kontragent] = " + mAppInstance.getClientInfo().getID() + ""//
				+ "\n 		order by z.VremyaNachalaSkidkiPhiksCen DESC,z.VremyaOkonchaniyaSkidkiPhiksCen DESC"//
				+ "\n      limit 222";
		//System.out.println(sql);
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		gridData = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		for(int i = 0; i < gridData.children.size(); i++){
			final Bough row = gridData.children.get(i);
			Task tapTask = new Task(){
				public void doTask(){
					try{
						ZayavkaNaSkidki bid //
								= new ZayavkaNaSkidki(//
								(int)Numeric.string2double(row.child("_id").value.property.value())//
								, "x'" + row.child("_IDRRef").value.property.value() + "'"//
								, Auxiliary.sqliteDate.parse(row.child("dateCreate").value.property.value())//
								, row.child("nomer").value.property.value()//
								, mAppInstance.getClientInfo().getID()//
								, mAppInstance.getClientInfo().getKod()//
								, mAppInstance.getClientInfo().getName()//
								, Auxiliary.sqliteDate.parse(row.child("dateStart").value.property.value())//
								, Auxiliary.sqliteDate.parse(row.child("dateEnd").value.property.value())//
								, row.child("kommentariy").value.property.value()//
								, row.child("vygruzhen").value.property.value().equals("01")//
								, false);
						StartFixedPricesActivity(!row.child("vygruzhen").value.property.value().equals("01"), bid);
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
			};
			int bg = 0x00ff0000;
			String vigrugen = "не выгружена";
			if(!row.child("vygruzhen").value.property.value().equals("00")){
				vigrugen = "выгружена";
				bg = 0x3300ff00;
			}
			String when = Auxiliary.tryReFormatDate(row.child("dateCreate").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
			columnDate.cell(when, bg, tapTask, vigrugen);
			columnInfo.cell(row.child("nomer").value.property.value(), bg, tapTask//
					, "с " //
							+ Auxiliary.tryReFormatDate(row.child("dateStart").value.property.value(), "yyyy-MM-dd", "dd.MM.yy")//
							+ " по " //
							+ Auxiliary.tryReFormatDate(row.child("dateEnd").value.property.value(), "yyyy-MM-dd", "dd.MM.yy")//
			);
			columnComment.cell(row.child("kommentariy").value.property.value(), bg, tapTask, "");
		}
	}

	void readOrders(){
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String sql = "select"//
				+ "\n 		zp._id as _id"//
				+ "\n 		,zp._idrref as _idrref"//
				+ "\n 		,zp.data as data"//
				+ "\n 		,zp.nomer as nomer"//
				+ "\n 		,zp.dataOtgruzki as dataOtgruzki"//
				+ "\n 		,zp.proveden as proveden"//
				+ "\n 		,zp.dogovorKontragenta as dogovorKontragenta"//
				+ "\n 		,zp.summaDokumenta as summaDokumenta"//
				+ "\n 		,zp.kontragent as kontragent"//
				+ "\n 		,zp.kommentariy as kommentariy"//
				+ "\n 		,zp.tipOplaty as tipOplaty"//
				+ "\n 		,zp.sebestoimost as sebestoimost"//
				+ "\n 		,kntr.Naimenovanie as kontragentNaimenovanie"//
				+ "\n 		,kntr._idrref as kontragent_idrref"//
				+ "\n 		,kntr.kod as kontragent_kod"//
				+ "\n 		,dk.Naimenovanie as dogovorNaimenovanie"//
				+ "\n 		,tiO._idrref as tip_idrref"//
				+ "\n 		,tiO.Poryadok as poryadok"//
				+ "\n 	from [ZayavkaPokupatelyaIskhodyaschaya] zp"//
				+ "\n 		join Kontragenty kntr on zp.[Kontragent] = kntr.[_idrref]"//
				+ "\n 		left join TipyOplaty tiO on zp.[TipOplaty] = tiO._IDRRef"//
				+ "\n 		left join DogovoryKontragentov dk on zp.[DogovorKontragenta] = dk.[_IDRRef]"//
				+ "\n 	where zp.[Kontragent] = " + mAppInstance.getClientInfo().getID() + ""//
				+ "\n 	order by date(zp.dataOtgruzki) desc, zp._id desc"//
				+ "\n 	limit 77"//
				+ "\n 	;";
		//System.out.println(sql);
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		gridData = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
		for(int i = 0; i < gridData.children.size(); i++){
			Bough row = gridData.children.get(i);
			//System.out.println(row.dumpXML());
			final ZayavkaPokupatelya bid = new ZayavkaPokupatelya(//
					(int)Numeric.string2double(row.child("_id").value.property.value())//
					, "x'" + row.child("_idrref").value.property.value() + "'"//
					, DateTimeHelper.SQLDateToDate(row.child("data").value.property.value())//
					, row.child("nomer").value.property.value()//
					, !row.child("proveden").value.property.value().equals("00")//
					, DateTimeHelper.SQLDateToDate(row.child("dataOtgruzki").value.property.value())//
					, "x'" + row.child("dogovorKontragenta").value.property.value() + "'"//
					, row.child("kommentariy").value.property.value()//
					, "x'" + row.child("kontragent").value.property.value() + "'"//
					, row.child("kontragent_kod").value.property.value()//
					, row.child("kontragentNaimenovanie").value.property.value()//
					, Numeric.string2double(row.child("summaDokumenta").value.property.value())//
					, "x'" + row.child("tipOplaty").value.property.value() + "'"//
					, (int)Numeric.string2double(row.child("poryadok").value.property.value())//
					, Numeric.string2double(row.child("sebestoimost").value.property.value())//
					, false);
			Task tapTask = new Task(){
				public void doTask(){
					int ORDER_UPDATE = 5;
					StartBidsActivity(ORDER_UPDATE, bid);
				}
			};
			int bg = 0x00ff0000;
			String vigrugen = "не выгружен";//+row.child("data").value.property.value();
			if(!row.child("proveden").value.property.value().equals("00")){
				vigrugen = "выгружен";
				bg = 0x3300ff00;
			}
			String otgruzka = Auxiliary.tryReFormatDate(row.child("dataOtgruzki").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
			String tipOpl = "Наличная";
			if(row.child("poryadok").value.property.value().equals("1")){
				tipOpl = "Безналичная";
			}
			if(row.child("poryadok").value.property.value().equals("2")){
				tipOpl = "Товарный чек";
			}
			columnDate.cell(otgruzka, bg, tapTask, vigrugen);
			columnInfo.cell(row.child("nomer").value.property.value(), bg, tapTask, row.child("dogovorNaimenovanie").value.property.value() + ", тип оплаты: " + tipOpl);
			columnComment.cell(row.child("summaDokumenta").value.property.value() + "р.", bg, tapTask, "");
		}
	}

	private void StartFixedPricesActivity(boolean isEditable, ZayavkaNaSkidki bid){
		int FIXED_PRICES_ADD = 8;
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc_2.this, Activity_FixedPrices.class);
		intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
		intent.putExtra("is_editable", isEditable);
		if(bid != null){
			intent.putExtra("ZayavkaNaSkidki", bid);
		}
		startActivityForResult(intent, FIXED_PRICES_ADD);
	}

	void promptKlientPeople(){
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc_2.this, ActivityKlientPeople.class);
		intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
		startActivity(intent);
	}

	void promptDeleteFixPrices(){
		Auxiliary.pickConfirm(this, "Удаление заявок на фикс.цены", "Удалить", new Task(){
			@Override
			public void doTask(){
				String idrrf = ApplicationHoreca.getInstance().getClientInfo().getID();
				String sql = "delete from ZayavkaNaSkidki_TovaryPhiksCen where _ZayavkaNaSkidki_IDRRef in (select _idrref from ZayavkaNaSkidki where kontragent=" + idrrf + ");";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				sql = "delete from ZayavkaNaSkidki where kontragent=" + idrrf + ";";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				//Activity_BidsContractsEtc_2.this.finish();
				refreshGrid();
			}
		});
	}


	void StartBidsActivity(int requestCode, ZayavkaPokupatelya bid){
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		Intent intent = new Intent();
		intent.setClass(Activity_BidsContractsEtc_2.this, Activity_Bid.class);
		intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
		if(bid != null){
			//intent.putExtra("newDoc", false);
			intent.putExtra("ZayavkaPokupatelya", bid);
			//ApplicationHoreca.lastZayavkaPokupatelya=bid;

			if(!mIsEditable || bid.isProveden()){
				intent.putExtra("is_editable", false);
			}
			//intent.putExtra("doc", "exists");
		}else{
			Activity_Bid.unLockCreateNewOrder();
			//intent.putExtra("doc", "new");
			//ApplicationHoreca.lastZayavkaPokupatelya=null;
		}
		startActivityForResult(intent, requestCode);
	}

	private void StartReturnsActivity(final boolean isEditable, ZayavkaNaVozvrat bid){
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		if(bid == null){
			final Numeric defaultSelection = new Numeric();
			Auxiliary.pickSingleChoice(this, ZayavkaNaVozvrat_Tovary.ZayavkaNaVozvratPrichina, defaultSelection, "Причина", new Task(){
				@Override
				public void doTask(){
					if(defaultSelection.value() > 0){
						ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
						Intent intent = new Intent();
						intent.setClass(Activity_BidsContractsEtc_2.this, Activity_Returns.class);
						intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
						intent.putExtra("is_editable", isEditable);
						intent.putExtra("prichina", defaultSelection.value().intValue());
						startActivityForResult(intent, 9);
					}
				}
			}, null, null, null, null);
		}else{
			Intent intent = new Intent();
			intent.setClass(Activity_BidsContractsEtc_2.this, Activity_Returns.class);
			intent.putExtra("client_id", mAppInstance.getClientInfo().getID());
			intent.putExtra("is_editable", isEditable);
			intent.putExtra("ZayavkaNaVozvrat", bid);
			startActivityForResult(intent, 9);
		}
	}

	void doEditSpecificacia(String _id){
		Intent intent = new Intent();
		intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidSpecificaciya.class);
		startActivity(intent);
	}

	void doEditDegustaciu(String _id){
		Intent intent = new Intent();
		intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidDegustacia.class);
		startActivity(intent);
	}

	void dobavitDegustaciu(){
		Intent intent = new Intent();
		//intent.putExtra("_id", _id);
		intent.setClass(this, Activity_BidDegustacia.class);
		startActivity(intent);
	}

	void dobavitSpecificaciu(){
		//System.out.println("new Specificacia");
		Intent intent = new Intent();
		intent.setClass(this, Activity_BidSpecificaciya.class);
		startActivity(intent);
	}

	void beginVizit(){
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		GPSInfo mGPSInfo = GPS.getGPSInfo();
		String timeString = Auxiliary.tryReFormatDate3(mGPSInfo.getVizitTimeString(), "yyyy-MM-dd'T'HH:mm:ss", "dd.MM.yyyy HH:mm:ss");
		//String timeString = mGPSInfo.getVizitTimeString();
		mGPSInfo.BeginVizit(mAppInstance.getClientInfo().getKod());
		Auxiliary.warn("Начало визита" + ": " + timeString, Activity_BidsContractsEtc_2.this);
	}

	void promptRepeatVizit(String lastVizitTime){
		Auxiliary.pickConfirm(this, "Уже имеется открытый визит с временем начала " + lastVizitTime + ". Изменить открытый визит?", "Начать", new Task(){
			@Override
			public void doTask(){
				beginVizit();
			}
		});
	}

	void beginVizitButtonClick(){
		LogHelper.debug("mBtnBeginVizit");
		final ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		final GPSInfo mGPSInfo = GPS.getGPSInfo();
		//if (!mGPSInfo.estDalnieVizity(mAppInstance.getClientInfo())) {
		long distanceToClient = GPSInfo.isTPNearClient(
				mAppInstance.getClientInfo().getLat()
				, mAppInstance.getClientInfo().getLon()
		);
		if(distanceToClient == GPSInfo.GPS_NOT_AVAILABLE){
			Auxiliary.warn("GPS данные недоступны. Невозможно начать визит.", this);
		}else{
			if(distanceToClient > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()){
				String warning = "Удаление от контрагента " + distanceToClient + " метров. Начать визит?";
				if(mAppInstance.getClientInfo().getLat() == 0){
					warning = "У клиента не зафиксированы координаты. Начать визит?";
				}
				Auxiliary.pickConfirm(this, warning, "Начать", new Task(){
					@Override
					public void doTask(){
						//if (!mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod())) {
						String beginTime = mGPSInfo.findPreVizitTimeDaily(mAppInstance.getClientInfo().getKod());
						if(beginTime != null){
							promptRepeatVizit(beginTime);
						}else{
							beginVizit();
						}
					}
				});
			}else{
				//if (!mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod())) {
				String beginTime = mGPSInfo.findPreVizitTimeDaily(mAppInstance.getClientInfo().getKod());
				if(beginTime != null){
					promptRepeatVizit(beginTime);
				}else{
					beginVizit();
				}
			}
		}
		//} else {
		//	Auxiliary.warn("Сначала нужно закончить предыдущий визит " + mGPSInfo.kontragentyVizitov(mAppInstance.getClientInfo()), Activity_BidsContractsEtc_2.this);
		//}
	}

	boolean gpsTimeExists30(){
		//if(1==1)return true;

		//if ((new Date().getTime()) - Session.getGPSTime() > 30 * 1000) {
		if((new Date().getTime()) - GPSInfo.lastDateTime() > 30 * 1000){
			Auxiliary.warn("Нет координат за последние 30с. Проверьте GPS и настройки даты/времени.", Activity_BidsContractsEtc_2.this);
			return false;
		}else{
			return true;
		}
	}

	boolean mojnoZakrytVizit(){


		//System.out.println("////////");
		//System.out.println(Session.getGPSTime());
		//System.out.println(new Date(Session.getGPSTime()));
		//System.out.println(new Date().getTime());
		//System.out.println(new Date());
		//System.out.println("////////");
		/*if ((new Date().getTime()) - Session.getGPSTime() > 30 * 1000) {
			Auxiliary.warn("Нет координат за последние 30с. Проверьте GPS и настройки даты/времени.", Activity_BidsContractsEtc_2.this);
			return false;
		}*/


		if(!gpsTimeExists30())
			return false;
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		long duration = 0;
		int coarse = -1;
		//String sql = "select BeginTime as BeginTime from Vizits where ((EndTime is null) or (length(EndTime)<4) or(EndTime=BeginTime)) and Client = " + mAppInstance.getClientInfo().getKod().trim();
		String sql = "select BeginTime as BeginTime from Vizits where EndTime is null and Client = " + mAppInstance.getClientInfo().getKod().trim();
		System.out.println("mojnoZakrytVizit " + sql);
		//mGPSInfo.IsFirstVizitDaily(mAppInstance.getClientInfo().getKod());
		//Cursor cursor = mDB.rawQuery(sql, null);
		Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		//if(cursor.moveToFirst()) {
		SimpleDateFormat userTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//mDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		//userTime.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
		java.util.Date now = new java.util.Date();
		if(b.children.size() > 0){
			//DateFormat df;

			try{
				//java.util.Date beginDate = mDateTimeFormat.parse(cursor.getString(0));
				SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				mDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				java.util.Date beginDate = mDateTimeFormat.parse(b.child("row").child("BeginTime").value.property.value());

				duration = now.getTime() - beginDate.getTime();
				//System.out.println(beginDate);
				//System.out.println(now);
				//System.out.println(duration);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}else{
			Auxiliary.warn("Не найдено незакрытых визитов для контрагента " + mAppInstance.getClientInfo().getKod().trim(), Activity_BidsContractsEtc_2.this);
			return false;
		}
		//cursor.close();
		//if (duration < 1000 * 60 * 15) {
		if(duration < 1000 * 60 * 5){
			coarse = (int)Math.round(duration / (1000.0 * 60));

			Auxiliary.warn("Визит должен длиться не меньше 5 мин.,\nс "
					+ Auxiliary.tryReFormatDate3(b.child("row").child("BeginTime").value.property.value(), "yyyy-MM-dd'T'HH:mm:ss", "dd.MM.yyyy HH:mm:ss")
					+ "\nдо "
					+ userTime.format(now)
					+ "\nпрошло только " + coarse + " мин.", Activity_BidsContractsEtc_2.this);
			return false;
		}
		long distanceToClient = GPSInfo.isTPNearClient(mAppInstance.getClientInfo().getLat(), mAppInstance.getClientInfo().getLon());
		if(distanceToClient == GPSInfo.GPS_NOT_AVAILABLE){
			Auxiliary.warn("GPS данные недоступны.", Activity_BidsContractsEtc_2.this);
			return false;
		}

		if(distanceToClient > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()){
			Auxiliary.warn("Удаление от контрагента " + distanceToClient + " метров."
					, Activity_BidsContractsEtc_2.this);
			return false;
		}
		return true;
	}

	void endVizitCasePrompt(){
		String sql = "select [Naimenovanie] as Naimenovanie from RezultatVizita order by [Kod]";
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		SQLiteDatabase mDB = mAppInstance.getDataBase();
		final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		String[] items = new String[b.children.size()];
		for(int i = 0; i < b.children.size(); i++){
			items[i] = b.children.get(i).child("Naimenovanie").value.property.value();
		}
		final Numeric selection = new Numeric();
		Auxiliary.pickSingleChoice(this, items, selection, "Результат визита", new Task(){
			@Override
			public void doTask(){
				//System.out.println(selection.value());
				GPSInfo mGPSInfo = GPS.getGPSInfo();
				ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
				mGPSInfo.EndVisit(mAppInstance.getClientInfo().getKod()
						, b.children.get(selection.value().intValue()).child("Naimenovanie").value.property.value()
				);
			}
		}, null, null, null, null);
	}

	void switchPage(int nn){
		//System.out.println("switchPage " + nn);
		knobZakazy.setEnabled(nn != 0);
		newZakaz.hidden().is(nn != 0);
		knobFixCena.setEnabled(nn != 1);
		newFixCena.hidden().is(nn != 1);
		knobVozvrat.setEnabled(nn != 2);
		newVozvrat.hidden().is(nn != 2);
		knobSpec.setEnabled(nn != 3);
		newSpecificacia.hidden().is(nn != 3);
		knobDegustacia.setEnabled(nn != 4);
		newDegustas.hidden().is(nn != 4);
		refreshGrid();
	}
}
