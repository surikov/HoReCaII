package sweetlife.android10.supervisor;

import android.app.Activity;

import android.os.*;
import android.view.*;
import android.content.*;
import android.text.InputType;
import android.net.*;

import java.util.*;

import android.database.sqlite.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.gps.GPSInfo;
import sweetlife.android10.gps.SWLifeGpsService;
import sweetlife.android10.gps.Session;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.text.*;

public class ActivityOdnaAnketa extends Activity {
	String _id = null;
	Numeric vigruzhen = new Numeric();
	MenuItem menuSave;
	MenuItem menuDelete;
	MenuItem menuContact;
	MenuItem menuVigruzit;
	Layoutless layoutless;
	Numeric Podrazdelenie = new Numeric();
	Numeric Komissioner = new Numeric();
	Numeric UrFizLico = new Numeric();
	Note FizOther = new Note();
	Numeric shirota = new Numeric();
	Numeric dolgota = new Numeric();
	Note Naimenovanie = new Note();
	Note Komentariy = new Note();
	//Numeric TipUrLica = new Numeric();
	Numeric GolovnoyKontragent = new Numeric();
	Numeric PredokNum = new Numeric();
	Note INN = new Note();
	Note KPP = new Note();
	Note UrAdres = new Note();
	Note osobennostiKlienta = new Note();
	Note BIK = new Note();
	Note NomerScheta = new Note();
	RedactSingleChoice tiptt;
	RedactSingleChoice choiceVidKuhni;
	//RedactSingleChoice selKomissioner;
	//Numeric PK = new Numeric();
	Numeric PotencialniyKlient = new Numeric().value(1);
	Numeric PKorNew = new Numeric().value(0);
	Note Viveska = new Note();
	Note KommentariiPropusk = new Note();
	Note osobennostiRejimaRaboty = new Note();//ОсобенностиРежимаРаботы

	Toggle NuzhenPropusk = new Toggle();
	Toggle SmenaUrLicaSrazu = new Toggle();
	Toggle BezPechati2 = new Toggle();

	Note FaktAdres = new Note();
	Note AdresDostavki = new Note();
	Note AdresLoc = new Note();
	Numeric Bolshegruz = new Numeric();

	Note KolichestvoMest = new Note();
	Note VidKuhni = new Note();
	Numeric selVidKuhni = new Numeric();
	Numeric TipTT = new Numeric();
	//Note VremyaRaboti = new Note();
	//Note fullVremyaRaboti = new Note();
	//Numeric VremyaRabotiStart = new Numeric();
	//Numeric VremyaRabotiEnd = new Numeric();
	Numeric den01 = new Numeric();
	Numeric den02 = new Numeric();
	Numeric den11 = new Numeric();
	Numeric den12 = new Numeric();
	Numeric den21 = new Numeric();
	Numeric den22 = new Numeric();
	Numeric den31 = new Numeric();
	Numeric den32 = new Numeric();
	Numeric den41 = new Numeric();
	Numeric den42 = new Numeric();
	Numeric den51 = new Numeric();
	Numeric den52 = new Numeric();
	Numeric den61 = new Numeric();
	Numeric den62 = new Numeric();
	Numeric den71 = new Numeric();
	Numeric den72 = new Numeric();
	RedactSingleChoice PKNew;

	RedactFilteredSingleChoice PotencialniyKlientChoice;
	RedactFilteredSingleChoice PredokChoise;

	RedactFilteredSingleChoice GolovnoyKontragentChoice;
	//RedactSingleChoice Predok;
	ColumnText FIO;
	ColumnText Telefon;
	ColumnText Dolztost;
	ColumnText Rol;
	int gridPageSize = 30;
	Numeric gridOffset = new Numeric();
	DataGrid dataGrid;
	//String[] allVidKuhni=new String[]{"Восточная", "Европейская", "Итальянская", "Японская", "Русская"};
	String[] allVidKuhni = new String[]{"Кавказская", "Европейская", "Итальянская", "Японская", "Русская", "Американская"};
	//String[] allFormaN=new String[]{"СетьЛокальная", "СетьФедеральная", "ОтдельностоящееЗаведение"};
	String[] allFormaN = new String[]{"СетьЛокальная", "СетьФедеральная", "ОтдельностоящееЗаведение"};
	String[] allFormaT = new String[]{"Сеть локальная", "Сеть федеральная", "Отдельностоящее заведение"};
	Numeric allFormaS = new Numeric();
	SimpleDateFormat timeFormat;// = new SimpleDateFormat("HH:mm");
	Bough vseKontragenty;// = Cfg.vseKontragenty();
	Task fillFromINN = new Task() {
		public void doTask() {
			//System.out.println(INN.value());
			final Bough bb = new Bough();
			new Expect().status.is("Поиск по " + INN.value()).task.is(new Task() {
				@Override
				public void doTask() {
					try {
						//String url = "https://testservice.swlife.ru/simutkin_hrc/en_US/hs/NewClient/New/" + INN.value().trim();
						//String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/NewClient/New/" + INN.value().trim();
						//String url = "https://testservice.swlife.ru/simutkin_hrc/en_US/hs/NewClient/INN/7702211710";
						String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/NewClient/INN/" + INN.value().trim();
						byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						String txt = new String(bytes);
						bb.children.add(bb.parseJSON(txt).name.is("data"));

					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					System.out.println(bb.dumpXML());
					Vector<Bough> children = bb.child("data").children;
					if (children.size() > 0) {
						String[] items = new String[bb.child("data").children.size()];
						for (int i = 0; i < children.size(); i++) {
							System.out.println("" + i + ": " + children.get(i).dumpXML());
							items[i] = children.get(i).child("НаименованиеЮрЛица").value.property.value();
						}
						final Numeric selection = new Numeric();
						Auxiliary.pickSingleChoice(ActivityOdnaAnketa.this, items, selection, null, new Task() {
							@Override
							public void doTask() {
								Bough selected = bb.child("data").children.get(selection.value().intValue());
								System.out.println("tap " + selected.dumpXML());
								UrAdres.value(selected.child("ЮрАдрес").value.property.value());
								Naimenovanie.value(selected.child("НаименованиеЮрЛица").value.property.value());
								if (selected.child("НаименованиеПолное").value.property.value().length() > 5) {
									Naimenovanie.value(selected.child("НаименованиеПолное").value.property.value());
								}
								FaktAdres.value(selected.child("ФактАдрес").value.property.value());
								String gkod = bb.children.get(0).child("ГоловнойКонтрагент").value.property.value();
								GolovnoyKontragent.value(0);
								Bough kon = Cfg.vseKontragenty();
								for (int i = 0; i < kon.children.size(); i++) {
									if (kon.children.get(i).child("kod").value.property.value().equals(gkod)) {
										GolovnoyKontragent.value(1 + i);
										break;
									}
								}
								if (selected.child("КПП").value.property.value().length() == 9) {
									KPP.value(selected.child("КПП").value.property.value());
								}
								if (INN.value().trim().length() == 12) {
									Auxiliary.inform("По ИП необходимо указать полный адрес прописки", ActivityOdnaAnketa.this);
								}
							}
						}, null, null, null, null);
					}
					/*String msg = bb.children.get(0).child("Ошибка").value.property.value();
					msg = msg + "\n" + bb.children.get(0).child("НаименованиеЮЛ").value.property.value();
					msg = msg + "\n" + bb.children.get(0).child("ЮрАдрес").value.property.value();
					msg = msg + "\n" + bb.children.get(0).child("ФактАдрес").value.property.value();
					msg = msg + "\n" + bb.children.get(0).child("ГоловнойКонтрагент").value.property.value();
					if (INN.value().trim().length() == 12) {
						msg = msg + "\n\nПо ИП необходимо указать полный адрес прописки";
					}
					//System.out.println(msg);
					Auxiliary.pickConfirm(ActivityOdnaAnketa.this, msg, "Вставить", new Task() {
						@Override
						public void doTask() {
							System.out.println(bb.dumpXML());
							UrAdres.value(bb.children.get(0).child("ЮрАдрес").value.property.value());
							Naimenovanie.value(bb.children.get(0).child("НаименованиеЮЛ").value.property.value());
							FaktAdres.value(bb.children.get(0).child("ФактАдрес").value.property.value());
							String gkod = bb.children.get(0).child("ГоловнойКонтрагент").value.property.value();
							GolovnoyKontragent.value(0);
							Bough kon = Cfg.vseKontragenty();
							for (int i = 0; i < kon.children.size(); i++) {
								if (kon.children.get(i).child("kod").value.property.value().equals(gkod)) {
									GolovnoyKontragent.value(1 + i);
									break;
								}
							}

						}
					});*/
				}
			}).start(ActivityOdnaAnketa.this);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuContact = menu.add("Добавить контакт");
		menuSave = menu.add("Сохранить");
		menuVigruzit = menu.add("Выгрузить");
		menuDelete = menu.add("Удалить");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (vigruzhen.value() > 0) {
			Auxiliary.pickConfirm(this, "Анкета уже выгружена", "Разрешить изменение", new Task() {
				@Override
				public void doTask() {
					vigruzhen.value(0);
				}
			});
			return true;
		}

		if (item == menuSave) {
			if (vigruzhen.value() > 0) {
				Auxiliary.warn("Анкета уже выгружена", this);
				return super.onOptionsItemSelected(item);
			}
			save();
			finish();
			return true;
		}
		if (item == menuVigruzit) {
			//vigruzhen.value(0);
			if (vigruzhen.value() > 0) {
				Auxiliary.warn("Анкета уже выгружена", this);
				return super.onOptionsItemSelected(item);
			}
			upload();
			return true;
		}
		if (item == menuDelete) {
			if (vigruzhen.value() > 0) {
				Auxiliary.warn("Анкета уже выгружена", this);
				return super.onOptionsItemSelected(item);
			}
			Auxiliary.pickConfirm(this, "Удаление заявки", "Удалить", new Task() {
				@Override
				public void doTask() {
					delete();
					finish();
				}
			});
			return true;
		}
		if (item == menuContact) {
			if (vigruzhen.value() > 0) {
				Auxiliary.warn("Анкета уже выгружена", this);
				return super.onOptionsItemSelected(item);
			}
			save();
			Intent intent = new Intent();
			intent.setClass(this, ActivityOdnaAnketaContact.class);
			intent.putExtra("anketaId", "" + _id);
			this.startActivityForResult(intent, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	boolean check() {
		if(this.PKorNew.value()==1){
			Bough contacts = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select * from AnketaKlientaContacts where anketaId=" + _id, null));
			if(contacts.children.size()<1){
				Auxiliary.warn("Клиент не ПК, добавьте контактные лица", this);
				return false;
			}
		}
		if (Naimenovanie.value().trim().length() < 2) {
			Auxiliary.warn("Заполните наименование", this);
			return false;
		}
		/*if ((BIK.value().trim().length()) != 9 && (PotencialniyKlient.value() > 0)) {//ne pk
			Auxiliary.warn("БИК должен быть из 9 цифр", this);
			return false;
		}*/
		if ((KPP.value().trim().length() != 9) && (PotencialniyKlient.value() > 0) && (UrFizLico.value() < 5)) {//ne pk
			Auxiliary.warn("КПП должен быть из 9 цифр", this);
			return false;
		}
		if (Viveska.value().trim().length() < 2) {
			Auxiliary.warn("Заполните поле \"вывеска\"", this);
			return false;
		}
		if (FaktAdres.value().trim().length() < 10) {
			Auxiliary.warn("Заполните факт. адрес", this);
			return false;
		}
		if (AdresLoc.value().trim().length() < 5) {
			Auxiliary.warn("Заполните адрес локализации", this);
			return false;
		}
		if (UrAdres.value().trim().length() < 5) {
			Auxiliary.warn("Заполните юр. адрес", this);
			return false;
		}
		if(PKorNew.value()==0){
			if(INN.value().trim().length()>0){
				Auxiliary.warn("Для ПК нужен пустой ИНН", this);
				return false;
			}
		}else{
			if (NomerScheta.value().length() != 20) {//ne pk
				Auxiliary.warn("Для нового ИНН Расчётный счёт должен быть из 20 цифр", this);
				return false;
			}
		}

		//if ((this.VremyaRaboti.value().trim().length() < 3) && (PotencialniyKlient.value() > 0)) {//ne pk
		/*if ((this.VremyaRabotiStart.value() < 12345 && this.VremyaRabotiEnd.value() < 12345) && (PotencialniyKlient.value() > 0)) {//ne pk
			Auxiliary.warn("Заполните время приёма товара", this);
			return false;
		}*/
		if(PKorNew.value()!=0) {
			if (PotencialniyKlient.value() != 0) {
				if (UrFizLico.value() > 4) {//== 5) {
					if ((INN.value().length() != 12) && (PotencialniyKlient.value() > 0)) {//ne pk
						Auxiliary.warn("ИНН для физ. лица должен быть из 12 цифр", this);
						return false;
					}
				} else {
					if ((INN.value().length() != 10) && (PotencialniyKlient.value() > 0)) {//ne pk
						Auxiliary.warn("ИНН для юр. лица должен быть из 10 цифр", this);
						return false;
					}
				}
			}
		}
		int kn = PotencialniyKlient.value().intValue();
		if (kn > 1) {
			/*for (int i = 0; i < PotencialniyKlientChoice.items.size(); i++) {
				System.out.println(i + ": " + PotencialniyKlientChoice.items.get(i));
			}*/
			String n = PotencialniyKlientChoice.items.get(kn);
			//if (!n.trim().startsWith("ПК")) {
			if (n.indexOf("ПК ") < 0) {
				Auxiliary.warn("Контрагент (" + n + ") не ПК", this);
				return false;
			}
		}
		/*if ((NomerScheta.value().length() != 20) && (PotencialniyKlient.value() > 0)) {//ne pk
			Auxiliary.warn("Расчётный счёт должен быть из 20 цифр", this);
			return false;
		}*/

		//NomerScheta
		if ((NomerScheta.value().length() != 20) && (PotencialniyKlient.value() > 0)) {//ne pk
			Auxiliary.warn("Расчётный счёт должен быть из 20 цифр", this);
			return false;
		}
		/*
		boolean existedINN = false;
		if (INN.value().trim().length() > 0) {
			String fountINN = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase()
					.rawQuery("select inn as inn from kontragenty where inn='" + INN.value().trim() + "' limit 1;", null))
					.child("row").child("inn").value.property.value();
		}
		if ((NomerScheta.value().length() != 20) && (!existedINN)) {//ne pk
			Auxiliary.warn("Для нового ИНН Расчётный счёт должен быть из 20 цифр", this);
			return false;
		}*/



		return true;
	}

	void select() {
		//System.out.println("" + this.getClass() + " select");
		String sql = "select "
				+ "vigruzhen"
				+ ", Podrazdelenie "
				+ ", UrFizLico "
				+ ", FizOther"
				+ ", ''||shirota as shirota"
				+ ", ''||dolgota as dolgota"
				+ ", Naimenovanie "
				+ ", OsnovnoiKlientTT "
				+ ", osobennostiRejimaRaboty "

				+ ", INN "
				+ ", KPP "
				+ ", UrAdres "
				+ ", BIK"
				+ ", NomerScheta "
				+ ", PotencialniyKlient"
				+ ", Viveska "
				+ ", FaktAdres "
				+ ", AdresDostavki "
				+ ", AdresLoc "
				+ ", Bolshegruz "
				+ ", KolichestvoMest "
				+ ", VidKuhni "
				+ ", TipTT "
				+ ", VremyaRaboti"
				+ ", komentariy" //
				+ ", Komissioner"
				+ ", formatkuhni"
				+ ", predok"
				+ ", NuzhenPropusk"
				+ " from AnketaKlienta where _id=" + _id//
				;
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(data.dumpXML());
		vigruzhen.value(Numeric.string2double(data.child("row").child("vigruzhen").value.property.value()));

		String podrName = data.child("row").child("Podrazdelenie").value.property.value();
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			String pkod = Cfg.territory().children.get(i).child("kod").value.property.value();
			if (pkod.equals(podrName)) {
				Podrazdelenie.value(i);
			}
		}
		//Bough kon = Cfg.kontragentyByKod(podrName);
		Bough kon = Cfg.vseKontragenty();
		//System.out.println(kon.dumpXML());
		//Podrazdelenie.value(Numeric.string2double(data.child("row").child("Podrazdelenie").value.property.value()));
		UrFizLico.value(Numeric.string2double(data.child("row").child("UrFizLico").value.property.value()));
		FizOther.value(data.child("row").child("FizOther").value.property.value());
		shirota.value(Numeric.string2double(data.child("row").child("shirota").value.property.value()));
		dolgota.value(Numeric.string2double(data.child("row").child("dolgota").value.property.value()));
		Naimenovanie.value(data.child("row").child("Naimenovanie").value.property.value());
		Komissioner.value(Numeric.string2double(data.child("row").child("Komissioner").value.property.value()));
		String[] kmntr = data.child("row").child("komentariy").value.property.value().split("~");
		Komentariy.value("");
		osobennostiKlienta.value("");
		if (kmntr.length > 0) {
			Komentariy.value(kmntr[0]);
			if (kmntr.length > 1) {
				osobennostiKlienta.value(kmntr[1]);
			}
		}

		GolovnoyKontragent.value(0);
		String o = data.child("row").child("OsnovnoiKlientTT").value.property.value();
		System.out.println("kod='" + o + "'");
		for (int i = 0; i < kon.children.size(); i++) {
			//System.out.println("check '"+kon.child("kod").value.property.value()+"'");
			if (kon.children.get(i).child("kod").value.property.value().equals(o)) {
				GolovnoyKontragent.value(i + 1);
				//System.out.println("found GolovnoyKontragent " + i);
				break;
			}
		}

		PredokNum.value(0);
		o = data.child("row").child("predok").value.property.value();
		for (int i = 0; i < vseKontragenty.children.size(); i++) {
			if (vseKontragenty.children.get(i).child("kod").value.property.value().equals(o)) {
				PredokNum.value(i + 1);
				break;
			}
		}

		INN.value(data.child("row").child("INN").value.property.value());
		KPP.value(data.child("row").child("KPP").value.property.value());
		UrAdres.value(data.child("row").child("UrAdres").value.property.value());
		BIK.value(data.child("row").child("BIK").value.property.value());
		NomerScheta.value(data.child("row").child("NomerScheta").value.property.value());
		//PotencialniyKlient.value(Numeric.string2double(data.child("row").child("PotencialniyKlient").value.property.value()));
		String p = data.child("row").child("PotencialniyKlient").value.property.value();
		System.out.println("PotencialniyKlient " + p);
		if (p.equals("ПК")) {
			PotencialniyKlient.value(0);
			PKorNew.value(0);
		} else {
			PKorNew.value(1);
			PotencialniyKlient.value(0);
			//System.out.println("kod='"+o+"'");
			for (int i = 0; i < vseKontragenty.children.size(); i++) {
				//System.out.println("check '"+kon.child("kod").value.property.value()+"'");
				if (vseKontragenty.children.get(i).child("kod").value.property.value().equals(p)) {
					PotencialniyKlient.value(i + 1);
					//System.out.println("found "+i);
					break;
				}
			}
		}
		parseNuzhenPropusk(data.child("row").child("NuzhenPropusk").value.property.value());
		/*NuzhenPropusk.value(
				data.child("row").child("NuzhenPropusk").value.property.value().equals("1")
						|| data.child("row").child("NuzhenPropusk").value.property.value().equals("3")
		);
		SmenaUrLicaSrazu.value(
				data.child("row").child("NuzhenPropusk").value.property.value().equals("2")
						|| data.child("row").child("NuzhenPropusk").value.property.value().equals("3")
		);*/
		/*NuzhenPropusk.value(false);
		BezPechati.value(false);
		String[] parts = data.child("row").child("NuzhenPropusk").value.property.value().split("~");
		if (parts.length > 0) {
			if (parts[0].equals("1")) {
				NuzhenPropusk.value(true);
			}
			if (parts.length > 1) {
				if (parts[1].equals("1")) {
					BezPechati.value(true);
				}
			}
		}
*/
		Viveska.value(data.child("row").child("Viveska").value.property.value());
		FaktAdres.value(data.child("row").child("FaktAdres").value.property.value());
		AdresDostavki.value(data.child("row").child("AdresDostavki").value.property.value());
		AdresLoc.value(data.child("row").child("AdresLoc").value.property.value());
		Bolshegruz.value(Numeric.string2double(data.child("row").child("Bolshegruz").value.property.value()));
		KolichestvoMest.value(data.child("row").child("KolichestvoMest").value.property.value());
		selVidKuhni.value(Numeric.string2double(data.child("row").child("VidKuhni").value.property.value()));
		TipTT.value(Numeric.string2double(data.child("row").child("TipTT").value.property.value()));

		KommentariiPropusk.value(data.child("row").child("OsnovnoiKlientTT").value.property.value());
		osobennostiRejimaRaboty.value(data.child("row").child("osobennostiRejimaRaboty").value.property.value());

		allFormaS.value(Numeric.string2double(data.child("row").child("formatkuhni").value.property.value()));

		//fullVremyaRaboti.value(data.child("row").child("VremyaRaboti").value.property.value());
		this.setStartEndVremyaRaboti(data.child("row").child("VremyaRaboti").value.property.value());
		dataGrid.clearColumns();
		if (_id != null) {
			Bough contacts = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select * from AnketaKlientaContacts where anketaId=" + _id, null));
			//System.out.println(contacts.dumpXML());
			for (int i = 0; i < contacts.children.size(); i++) {
				Bough row = contacts.children.get(i);
				final String _id = row.child("_id").value.property.value();
				Task tap;
				if (vigruzhen.value() > 0) {
					tap = null;
				} else {
					tap = new Task() {
						@Override
						public void doTask() {
							save();
							Intent intent = new Intent();
							intent.setClass(ActivityOdnaAnketa.this, ActivityOdnaAnketaContact.class);
							intent.putExtra("anketaId", "" + ActivityOdnaAnketa.this._id);
							intent.putExtra("_id", "" + _id);
							ActivityOdnaAnketa.this.startActivityForResult(intent, 0);
						}
					};
				}
				FIO.cell(row.child("FIO").value.property.value(), tap);
				Telefon.cell(row.child("Telefon").value.property.value(), tap);
				Dolztost.cell(row.child("Dolztost").value.property.value(), tap);
				Rol.cell(row.child("Rol").value.property.value(), tap);
			}
		}
		dataGrid.refresh();
	}

	int timeDay(int nn, String days[]) {
		if (days.length > nn) {
			try {

				int t0 = (int) (timeFormat.parse(days[nn].trim().replace('.', ':')).getTime());
				return t0;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return 0;
	}

	void setStartEndVremyaRaboti(String txt) {
		String days[] = txt.split("~");
		den01.value(timeDay(0, days));
		den02.value(timeDay(1, days));
		den11.value(timeDay(2, days));
		den12.value(timeDay(3, days));
		den21.value(timeDay(4, days));
		den22.value(timeDay(5, days));
		den31.value(timeDay(6, days));
		den32.value(timeDay(7, days));
		den41.value(timeDay(8, days));
		den42.value(timeDay(9, days));
		den51.value(timeDay(10, days));
		den52.value(timeDay(11, days));
		den61.value(timeDay(12, days));
		den62.value(timeDay(13, days));
		den71.value(timeDay(14, days));
		den72.value(timeDay(15, days));

		/*
		VremyaRabotiStart.value((int) (new Date().getTime()));
		VremyaRabotiEnd.value((int) (new Date().getTime()));
		System.out.println("setStartEndVremyaRaboti " + fullVremyaRaboti.value());
		String arr[] = fullVremyaRaboti.value().split("-");
		if (arr.length > 1) {
			try {

				int t0 = (int) (timeFormat.parse(arr[0].trim().replace('.', ':')).getTime());
				int t1 = (int) (timeFormat.parse(arr[1].trim().replace('.', ':')).getTime());
				VremyaRabotiStart.value(t0);
				VremyaRabotiEnd.value(t1);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
*/
	}

	String composeVremyaRaboti() {
		//String txt = timeFormat.format(new Date(VremyaRabotiStart.value().longValue()))
		//		+ " - " + timeFormat.format(new Date(VremyaRabotiEnd.value().longValue()));
		String txt = timeFormat.format(new Date(den01.value().longValue()))
				+ "~" + timeFormat.format(new Date(den02.value().longValue()))
				+ "~" + timeFormat.format(new Date(den11.value().longValue()))
				+ "~" + timeFormat.format(new Date(den12.value().longValue()))
				+ "~" + timeFormat.format(new Date(den21.value().longValue()))
				+ "~" + timeFormat.format(new Date(den22.value().longValue()))
				+ "~" + timeFormat.format(new Date(den31.value().longValue()))
				+ "~" + timeFormat.format(new Date(den32.value().longValue()))
				+ "~" + timeFormat.format(new Date(den41.value().longValue()))
				+ "~" + timeFormat.format(new Date(den42.value().longValue()))
				+ "~" + timeFormat.format(new Date(den51.value().longValue()))
				+ "~" + timeFormat.format(new Date(den52.value().longValue()))
				+ "~" + timeFormat.format(new Date(den61.value().longValue()))
				+ "~" + timeFormat.format(new Date(den62.value().longValue()))
				+ "~" + timeFormat.format(new Date(den71.value().longValue()))
				+ "~" + timeFormat.format(new Date(den72.value().longValue()));
		return txt;
	}

	void save() {
		if (_id == null) {
			insert();
		} else {
			update();
		}
	}

	String composeNuzhenPropusk() {
		String np = "";
		if (NuzhenPropusk.value()) {
			if (SmenaUrLicaSrazu.value()) {
				np = "3";
			} else {
				np = "1";
			}
		} else {
			if (SmenaUrLicaSrazu.value()) {
				np = "2";
			} else {
				np = "0";
			}
		}
		return np;
	}

	void parseNuzhenPropusk(String np) {
		NuzhenPropusk.value(false);
		SmenaUrLicaSrazu.value(false);
		if (np.equals("1")) {
			NuzhenPropusk.value(true);
			SmenaUrLicaSrazu.value(false);
		}
		if (np.equals("2")) {
			NuzhenPropusk.value(false);
			SmenaUrLicaSrazu.value(true);
		}
		if (np.equals("3")) {
			NuzhenPropusk.value(true);
			SmenaUrLicaSrazu.value(true);
		}
	}

	void update() {

		String sql = "update AnketaKlienta set"
				+ "\n vigruzhen=" + vigruzhen.value().longValue()//
				+ ", Podrazdelenie= " + "'" + Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue()) + "'"
				+ ", UrFizLico= " + getUrFiz() //
				+ ", FizOther= " + " '" + FizOther.value().replace('\'', '`') + "'"
				+ ", shirota= " + " " + shirota.value()//
				+ ", dolgota= " + " " + dolgota.value()//
				+ ", Komissioner= " + " " + Komissioner.value()//

				+ ", Naimenovanie= " + " '" + Naimenovanie.value().replace('\'', '`') + "'"
				//+ ", OsnovnoiKlientTT= " + " '" + getOsnovnoyKlient() + "'"
				+ ", OsnovnoiKlientTT= " + " '" + KommentariiPropusk.value() + "'"
				+ ", osobennostiRejimaRaboty= " + " '" + osobennostiRejimaRaboty.value() + "'"

				+ ", predok=  '" + this.getPredok() + "'"
				+ ", INN= " + " '" + INN.value().replace('\'', '`') + "'"
				+ ", KPP= " + " '" + KPP.value().replace('\'', '`') + "'"
				+ ", UrAdres= " + " '" + UrAdres.value().replace('\'', '`') + "'"
				+ ", BIK=" + " '" + BIK.value().replace('\'', '`') + "'"
				+ ", NomerScheta= " + " '" + NomerScheta.value().replace('\'', '`') + "'"
				+ ", PotencialniyKlient=" + " '" + getPotencialniyKlient() + "'"
				+ ", Viveska= " + " '" + Viveska.value().replace('\'', '`') + "'"
				+ ", FaktAdres= " + " '" + FaktAdres.value().replace('\'', '`') + "'"
				+ ", AdresDostavki= " + " '" + AdresDostavki.value().replace('\'', '`') + "'"
				+ ", AdresLoc= " + " '" + AdresLoc.value().replace('\'', '`') + "'"
				+ ", Bolshegruz= " + " " + (Bolshegruz.value() == 0 ? "0" : "1") //
				+ ", KolichestvoMest= " + " '" + KolichestvoMest.value().replace('\'', '`') + "'"
				//+ ", VidKuhni= " + " '" + VidKuhni.value().replace('\'', '`') + "'"
				+ ", VidKuhni= " + " '" + selVidKuhni.value() + "'"
				//+ ", NuzhenPropusk= '" + (NuzhenPropusk.value() ? "1" : "0") + "~" + (BezPechati.value() ? "1" : "0") + "'"
				//+ ", NuzhenPropusk= '" + (NuzhenPropusk.value() ? "1" : "0")  + "'"
				+ ", NuzhenPropusk= '" + composeNuzhenPropusk() + "'"
				+ ", TipTT= " + TipTT.value().intValue() //
				+ ", formatkuhni= " + allFormaS.value().intValue() //

				+ ", VremyaRaboti='" + composeVremyaRaboti() + "'"
				//+ ", VremyaRaboti=" + " '" + composeVremyaRaboti() + "'"
				//+ ", VremyaRaboti=" + " '" + VremyaRaboti.value().replace('\'', '`') + "'"
				//+ ", komentariy=" + " '" + Komentariy.value().replace('\'', '`') + "'"
				+ ", komentariy=" + " '" + Komentariy.value().replace('\'', '`') + "~" + osobennostiKlienta.value().replace('\'', '`') + "'"
				+ "\n where _id=" + _id + ";";
		System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	void delete() {
		if (_id != null) {
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AnketaKlientaContacts where anketaId=" + _id);
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AnketaKlienta where _id=" + _id);
		}
	}

	String getUrFiz() {
		String urFiz = "6";
		if (UrFizLico.value() == 0) {
			urFiz = "0";
		}
		if (UrFizLico.value() == 1) {
			urFiz = "1";
		}
		if (UrFizLico.value() == 2) {
			urFiz = "2";
		}
		if (UrFizLico.value() == 3) {
			urFiz = "3";
		}
		if (UrFizLico.value() == 4) {
			urFiz = "4";
		}
		if (UrFizLico.value() == 5) {
			urFiz = "5";
		}
		return urFiz;
	}

	/*String getOsnovnoyKlient() {
		String oklient = "";
		//String kod = Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue());
		if (GolovnoyKontragent.value() > 0) {
			oklient = Cfg.kodKontragenta(GolovnoyKontragent.value().intValue() - 1);
			//oklient = Cfg.kontragenty(kod).children.get(OsnovnoiKlientTT.value().intValue() - 1).child("kod").value.property.value();
		}
		return oklient;
	}
*/

	String getPredok() {
		String oklient = "";
		String kod = Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue());
		if (PredokNum.value() > 0) {
			//oklient = Cfg.kodKontragenta(PredokNum.value().intValue() - 1, kod);
			oklient = kodByIndex(PredokNum.value().intValue() - 1);

			//oklient = Cfg.kontragenty(kod).children.get(OsnovnoiKlientTT.value().intValue() - 1).child("kod").value.property.value();
		}
		return oklient;
	}

	String kodByIndex(int nn) {
		//Bough bb=Cfg.vseKontragenty();
		if (nn >= 0 && nn < vseKontragenty.children.size()) {
			String kod = vseKontragenty.children.get(nn).child("kod").value.property.value();
			return kod;
		} else {
			return "";
		}
	}

	String getPotencialniyKlient() {
		String pklient = "";
		//if (PotencialniyKlient.value() == 0) {
		System.out.println("getPotencialniyKlient/" + PKorNew.value() + "/" + PotencialniyKlient.value());
		if (PKorNew.value() == 0) {
			pklient = "ПК";
		} else {
			if (PotencialniyKlient.value() > 1) {
				//pklient = Cfg.kontragenty(kod).children.get(PotencialniyKlient.value().intValue() - 2).child("kod").value.property.value();
				//pklient = Cfg.kodKontragenta(PotencialniyKlient.value().intValue() - 1, Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue()));
				//pklient = Cfg.vseKontragenty(PotencialniyKlient.value().intValue() - 1, Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue()));
				pklient = kodByIndex(PotencialniyKlient.value().intValue() - 1);
			}
		}
		return pklient;
	}

	void insert() {
		//String kod = Cfg.territory().children.get(Podrazdelenie.value().intValue()).child("kod").value.property.value();
		//String tt = tiptt.items.get(TipTT.value().intValue());
		//TipTT
		String sql = "insert into AnketaKlienta ("
				+ "\nvigruzhen"
				+ ", Podrazdelenie "
				+ ", UrFizLico "
				+ ", FizOther "
				+ ", shirota "
				+ ", dolgota "
				+ ", Komissioner"
				+ ", Naimenovanie "
				+ ", OsnovnoiKlientTT "
				+ ", osobennostiRejimaRaboty "

				+ ", predok "
				+ ", INN "
				+ ", KPP "
				+ ", UrAdres "
				+ ", BIK"
				+ ", NomerScheta "
				+ ", PotencialniyKlient"
				+ ", Viveska "
				+ ", FaktAdres "
				+ ", AdresDostavki "
				+ ", AdresLoc "
				+ ", Bolshegruz "
				+ ", KolichestvoMest "
				+ ", VidKuhni "
				+ ", TipTT "
				+ ", formatkuhni  "
				+ ", VremyaRaboti"
				+ ", komentariy"
				+ ", NuzhenPropusk"
				+ "\n) values ("
				+ "\n" + vigruzhen.value()//
				+ ", '" + Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue()) + "'"
				+ ", " + getUrFiz() //
				+ ", '" + FizOther.value().replace('\'', '`') + "'"
				+ ", " + shirota.value()//
				+ ", " + dolgota.value()//
				+ ", " + Komissioner.value()//

				+ ", '" + Naimenovanie.value().replace('\'', '`') + "'"
				//+ ", '" + getOsnovnoyKlient() + "'"
				+ ", '" + KommentariiPropusk.value().replace('\'', '`') + "'"
				+ ", '" + osobennostiRejimaRaboty.value().replace('\'', '`') + "'"

				+ ", '" + this.getPredok() + "'"
				+ ", '" + INN.value().replace('\'', '`') + "'"
				+ ", '" + KPP.value().replace('\'', '`') + "'"
				+ ", '" + UrAdres.value().replace('\'', '`') + "'"
				+ ", '" + BIK.value().replace('\'', '`') + "'"
				+ ", '" + NomerScheta.value().replace('\'', '`') + "'"
				+ ", '" + getPotencialniyKlient() + "'"
				+ ", '" + Viveska.value().replace('\'', '`') + "'"
				+ ", '" + FaktAdres.value().replace('\'', '`') + "'"
				+ ", '" + AdresDostavki.value().replace('\'', '`') + "'"
				+ ", '" + AdresLoc.value().replace('\'', '`') + "'"

				+ ", " + (Bolshegruz.value() == 0 ? "0" : "1") //
				+ ", '" + KolichestvoMest.value().replace('\'', '`') + "'"
				//+ ", '" + VidKuhni.value().replace('\'', '`') + "'"
				+ ", '" + selVidKuhni.value() + "'"

				+ ", " + TipTT.value().intValue() //
				+ ", " + allFormaS.value().intValue() //

				+ ", '" + composeVremyaRaboti() + "'"
				//+ ", '" + VremyaRaboti.value().replace('\'', '`') + "'"
				//+ ", '" + Komentariy.value().replace('\'', '`') + "'"
				+ ", '" + Komentariy.value().replace('\'', '`') + "~" + osobennostiKlienta.value().replace('\'', '`') + "'"

				//+ ", " + (NuzhenPropusk.value() ? "1" : "0")//
				+ ", " + composeNuzhenPropusk()//

				//+ ", '" + (NuzhenPropusk.value() ? "1" : "0") + "~" + (BezPechati.value() ? "1" : "0")+"'"
				+ "\n );";
		System.out.println(sql);
		SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
		long id = statement.executeInsert();
		_id = "" + id;
		//ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		//this.finish();
	}

	String findCurTTKod() {
		String sql = "select naimenovanie as naimenovanie,kod as kod from TipyTorgovihTochek where deletionMark=x'00' order by naimenovanie;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		return data.children.get(TipTT.value().intValue()).child("kod").value.property.value();
	}

	String quoteSafe(String txt) {
		return txt.trim().replace("\"", "\\\"");
	}

	String composeJSONbody() {
		String kod = Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue());
		String tip = "ИП";
		String urFiz = "1";
		if (UrFizLico.value() == 0) {
			tip = "ООО";
			urFiz = "0";
		}
		if (UrFizLico.value() == 1) {
			tip = "ЗАО";
			urFiz = "0";
		}
		if (UrFizLico.value() == 2) {
			tip = "ОАО";
			urFiz = "0";
		}
		if (UrFizLico.value() == 3) {
			tip = "ПО";
			urFiz = "0";
		}
		if (UrFizLico.value() == 4) {
			tip = FizOther.value();
			urFiz = "0";
		}
		if (UrFizLico.value() == 5) {
			tip = "ИП";
			urFiz = "1";
		}
		if (UrFizLico.value() == 6) {
			tip = "СЗ";
			urFiz = "1";
		}
		String oklient = "";
		if (GolovnoyKontragent.value() > 0) {
			oklient = Cfg.vseKontragenty().children.get(GolovnoyKontragent.value().intValue() - 1).child("kod").value.property.value();
		}
		String predok = "";
		if (PredokNum.value() > 0) {
			predok = vseKontragenty.children.get(PredokNum.value().intValue() - 1).child("kod").value.property.value();
		}
		String pklient = "";
		if (PotencialniyKlient.value() > 1) {
			//pklient = Cfg.kontragentyByKod(kod).children.get(PotencialniyKlient.value().intValue() - 1).child("kod").value.property.value();
			pklient = vseKontragenty.children.get(PotencialniyKlient.value().intValue() - 1).child("kod").value.property.value();
		}
		String pk = "false";
		if (PKorNew.value() == 0) {
			pk = "true";
		}
		Bough contacts = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select * from AnketaKlientaContacts where anketaId=" + _id, null));
		String jsontext = "{"
				+ "\n					\"Podrazdelenie\":\"" + kod + "\""
				+ "\n					,\"Otvetstvennii\":\"" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "\""
				+ "\n					,\"Naimenovanie\":\"" + quoteSafe(Naimenovanie.value()) + "\""
				+ "\n					,\"TipUrLica\":\"" + tip + "\""
				+ "\n					,\"INN\":\"" + INN.value() + "\""
				+ "\n					,\"KPP\":\"" + KPP.value() + "\""
				+ "\n					,\"OsnovnoiKlientTT\":\"\""
				+ "\n					,\"BIK\":\"" + BIK.value() + "\""
				+ "\n					,\"NomerScheta\":\"" + NomerScheta.value() + "\""
				+ "\n					,\"Viveska\":\"" + quoteSafe(Viveska.value()) + "\""
				+ "\n					,\"TipTT\":\"" + findCurTTKod() + "\""
				+ "\n					,\"VremyaRaboti\":\"\""
				+ "\n					,\"PotencialniyKlient\":\"" + pklient + "\""
				+ "\n					,\"AdresDostavki\":\"" + quoteSafe(AdresDostavki.value()) + "\""
				+ "\n					,\"UrAdres\":\"" + quoteSafe(UrAdres.value()) + "\""
				+ "\n					,\"FaktAdres\":\"" + quoteSafe(FaktAdres.value()) + "\""
				+ "\n					,\"VidKuhni\":\"" + allVidKuhni[selVidKuhni.value().intValue()] + "\""
				+ "\n					,\"PK\":\"" + pk + "\""
				+ "\n					,\"Bolshegruz\":\"" + (Bolshegruz.value() == 0 ? "true" : "false") + "\""
				+ "\n					,\"KolichestvoMest\":\"" + KolichestvoMest.value() + "\""
				+ "\n					,\"UrFizLico\":" + urFiz + ""
				+ "\n					,\"Kommentarii\":\"" + quoteSafe(Komentariy.value()) + "\"";
		if (contacts.children.size() > 0) {
			jsontext = jsontext + "\n					,\"KL\":[";
			for (int i = 0; i < contacts.children.size(); i++) {
				Bough row = contacts.children.get(i);
				jsontext = jsontext + "\n					" + (i > 0 ? "," : "") + "{"
						+ "\n						\"FIO\":\"" + quoteSafe(row.child("FIO").value.property.value()) + "\""
						+ "\n						,\"Telefon\":\"" + quoteSafe(row.child("Telefon").value.property.value()) + "\""
						+ "\n						,\"Dolztost\":\"" + quoteSafe(row.child("Dolztost").value.property.value()) + "\""
						+ "\n						,\"Rol\":\"" + ActivityOdnaAnketaContact.rolKodByName(row.child("Rol").value.property.value()) + "\""
						+ "\n					}"
				;
			}
			jsontext = jsontext + "\n					]";
		}
		jsontext = jsontext + "\n					,\"Komissioner\":\"" + (Komissioner.value() == 0 ? "2" : "6") + "\"";
		jsontext = jsontext + "\n					,\"Format\":\"" + allFormaN[allFormaS.value().intValue()] + "\"";
		jsontext = jsontext + "\n					,\"AdresLoc\":\"" + quoteSafe(AdresLoc.value()) + "\"";
		jsontext = jsontext + "\n					,\"Predok\":\"\"";
		jsontext = jsontext + "\n					,\"NuzhenPropusk\":" + (NuzhenPropusk.value() ? "true" : "false") + "";
		jsontext = jsontext + "\n					,\"GolovnojKontragent\":\"" + oklient + "\"";
		jsontext = jsontext + "\n					,\"SmenaUrLica\":\"" + predok + "\"";
		jsontext = jsontext + "\n					,\"Osobennosti\":\"" + quoteSafe(osobennostiKlienta.value()) + "\"";
		jsontext = jsontext + "\n					,\"Den01\":\"" + timeFormat.format(new Date(den01.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den02\":\"" + timeFormat.format(new Date(den02.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den11\":\"" + timeFormat.format(new Date(den11.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den12\":\"" + timeFormat.format(new Date(den12.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den21\":\"" + timeFormat.format(new Date(den21.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den22\":\"" + timeFormat.format(new Date(den22.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den31\":\"" + timeFormat.format(new Date(den31.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den32\":\"" + timeFormat.format(new Date(den32.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den41\":\"" + timeFormat.format(new Date(den41.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den42\":\"" + timeFormat.format(new Date(den42.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den51\":\"" + timeFormat.format(new Date(den51.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den52\":\"" + timeFormat.format(new Date(den52.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den61\":\"" + timeFormat.format(new Date(den61.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den62\":\"" + timeFormat.format(new Date(den62.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den71\":\"" + timeFormat.format(new Date(den71.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"Den72\":\"" + timeFormat.format(new Date(den72.value().longValue())) + "\"";
		jsontext = jsontext + "\n					,\"SmenaUrLicaSrazu\":\"" + (SmenaUrLicaSrazu.value() ? "1" : "0") + "\"";
		jsontext = jsontext + "\n					,\"KommentariiPropusk\":\"" + quoteSafe(KommentariiPropusk.value()) + "\"";
		jsontext = jsontext + "\n					,\"ОсобенностиРежимаРаботы\":\"" + quoteSafe(osobennostiRejimaRaboty.value()) + "\"";
		jsontext = jsontext + "\n					,\"КомментарийКРежимуРаботы\":\"" + quoteSafe(osobennostiRejimaRaboty.value()) + "\"";

		jsontext = jsontext + "\n}";
		return jsontext;
	}

	void upload() {
		if (vigruzhen.value() > 0) {
			Auxiliary.warn("Анкета уже выгружена", this);
			return;
		}
		if (check()) {

			final String json = composeJSONbody();
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaKlienta";
			//final String url = "https://testservice.swlife.ru/velinsky_hrc/hs/ZayavkaNaKlienta";
			final Note msg = new Note();
			final Note status = new Note();
			final Note ok = new Note();
			System.out.println(json);
			/*System.out.println(url);
			System.out.println(Cfg.whoCheckListOwner());
			System.out.println(Cfg.hrcPersonalPassword());*/
			Expect expect = new Expect().status.is("Отправка").task.is(new Task() {
				@Override
				public void doTask() {
					try {
						Bough result = Auxiliary.loadTextFromPrivatePOST(url, json, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						System.out.println("result: " + result.dumpXML());
						String raw = result.child("raw").value.property.value();
						Bough data = Bough.parseJSON(raw);
						System.out.println("data: " + data.dumpXML());
						status.value(data.child("Статус").value.property.value());
						msg.value(result.child("message").value.property.value() + " " + data.child("Сообщение").value.property.value());
						ok.value(result.child("message").value.property.value());
					} catch (Throwable t) {
						msg.value(t.getMessage());
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					String warning = "";
					System.out.println("ok.value(): '" + ok.value() + "'");
					//if (status.value().equals("0")) {
					if (ok.value().equals("OK")) {
						long ms = new Date().getTime();
						System.out.println("date ms: '" + ms + "'/'" + ((int) ms) + "'/'" + ((long) ms) + "'");
						vigruzhen.value((double) ms);
						warning = "Заявка отправлена";
					} else {
						warning = msg.value();
					}
					//Auxiliary.inform("Результат " + msg.value(), ActivityOdnaAnketa.this);

					save();
					vigruzhen.value(0);
					Auxiliary.warn("Результат " + msg.value(), ActivityOdnaAnketa.this);
					//finish();
					/*
					{
						"Статус": 0,
						"Сообщение": "Успешно создана заявка.",
						"НомерЗаявки": ""
					}
					*/
				}
			});
			expect.start(this);
		}
	}

	/*
	void upload() {
		if (vigruzhen.value() > 0) {
			Auxiliary.warn("Анкета уже выгружена", this);
			return;
		}
		if (check()) {
			//String kod = Cfg.territory().children.get(Podrazdelenie.value().intValue()).child("kod").value.property.value();
			String kod = Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue());
			String tip = "ИП";
			String urFiz = "1";
			if (UrFizLico.value() == 0) {
				tip = "ООО";
				urFiz = "0";
			}
			if (UrFizLico.value() == 1) {
				tip = "ЗАО";
				urFiz = "0";
			}
			if (UrFizLico.value() == 2) {
				tip = "ОАО";
				urFiz = "0";
			}
			if (UrFizLico.value() == 3) {
				tip = "ПО";
				urFiz = "0";
			}
			if (UrFizLico.value() == 4) {
				tip = FizOther.value();
				urFiz = "0";
			}
			if (UrFizLico.value() == 5) {
				tip = "ИП";
				urFiz = "1";
			}
			if (UrFizLico.value() == 6) {
				tip = "СЗ";
				urFiz = "1";
			}
			String oklient = "";
			if (GolovnoyKontragent.value() > 0) {
				oklient = Cfg.vseKontragenty().children.get(GolovnoyKontragent.value().intValue() - 1).child("kod").value.property.value();
			}
			String predok = "";
			if (PredokNum.value() > 0) {
				//predok = Cfg.kontragentyByKod(kod).children.get(PredokNum.value().intValue() - 1).child("kod").value.property.value();
				predok = vseKontragenty.children.get(PredokNum.value().intValue() - 1).child("kod").value.property.value();
			}
			String pklient = "";
			if (PotencialniyKlient.value() > 1) {
				//pklient = Cfg.kontragentyByKod(kod).children.get(PotencialniyKlient.value().intValue() - 1).child("kod").value.property.value();
				pklient = vseKontragenty.children.get(PotencialniyKlient.value().intValue() - 1).child("kod").value.property.value();
			}
			String pk = "false";
			if (PKorNew.value() == 0) {
				pk = "true";
			}
			Bough contacts = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select * from AnketaKlientaContacts where anketaId=" + _id, null));
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "\n	<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "\n		<soap:Body>"
					+ "\n			<Vigruzit xmlns=\"http://ws.swl/ZayavkaNaKlienta\">"
					+ "\n				<Zayavka>"
					+ "\n					<Podrazdelenie>" + kod + "</Podrazdelenie>"
					+ "\n					<Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</Otvetstvennii>"
					+ "\n					<Naimenovanie>" + Naimenovanie.value() + "</Naimenovanie>"
					+ "\n					<TipUrLica>" + tip + "</TipUrLica>"
					+ "\n					<INN>" + INN.value() + "</INN>"
					+ "\n					<KPP>" + KPP.value() + "</KPP>"
					//+ "\n					<OsnovnoiKlientTT>" + oklient + "</OsnovnoiKlientTT>"

					+ "\n					<OsnovnoiKlientTT></OsnovnoiKlientTT>"
					+ "\n					<BIK>" + BIK.value() + "</BIK>"
					+ "\n					<NomerScheta>" + NomerScheta.value() + "</NomerScheta>"
					+ "\n					<Viveska>" + Viveska.value() + "</Viveska>"
					//+ "\n					<TipTT>" + tiptt.items.get(TipTT.value().intValue()) + "</TipTT>"
					+ "\n					<TipTT>" + findCurTTKod() + "</TipTT>"


					//+ "\n					<VremyaRaboti>" + composeVremyaRaboti() + "</VremyaRaboti>"
					+ "\n					<VremyaRaboti></VremyaRaboti>"

					+ "\n					<PotencialniyKlient>" + pklient + "</PotencialniyKlient>"
					+ "\n					<AdresDostavki>" + AdresDostavki.value() + "</AdresDostavki>"

					+ "\n					<UrAdres>" + UrAdres.value() + "</UrAdres>"
					+ "\n					<FaktAdres>" + FaktAdres.value() + "</FaktAdres>"
					//+ "\n					<VidKuhni>" + selVidKuhni.value() + "</VidKuhni>"
					+ "\n					<VidKuhni>" + allVidKuhni[selVidKuhni.value().intValue()] + "</VidKuhni>"

					+ "\n					<PK>" + pk + "</PK>"
					+ "\n					<Bolshegruz>" + (Bolshegruz.value() == 0 ? "true" : "false") + "</Bolshegruz>"
					+ "\n					<KolichestvoMest>" + KolichestvoMest.value() + "</KolichestvoMest>"
					+ "\n					<UrFizLico>" + urFiz + "</UrFizLico>"
					//+ "\n					<NuzhenPropusk>" + (NuzhenPropusk.value() ? "Истина" : "Ложь") + "</NuzhenPropusk>"

					+ "\n					<Kommentarii>" + Komentariy.value() + "</Kommentarii>"
					//+ "\n					<Predok>" + predok + "</Predok>"

					;
			if (contacts.children.size() > 0) {
				for (int i = 0; i < contacts.children.size(); i++) {
					Bough row = contacts.children.get(i);
					xml = xml + "\n					<KL>"
							+ "\n						<FIO>" + row.child("FIO").value.property.value() + "</FIO>"
							+ "\n						<Telefon>" + row.child("Telefon").value.property.value() + "</Telefon>"
							+ "\n						<Dolztost>" + row.child("Dolztost").value.property.value() + "</Dolztost>"
							+ "\n						<Rol>" + ActivityOdnaAnketaContact.rolKodByName(row.child("Rol").value.property.value())
							//row.child("Rol").value.property.value()
							+ "</Rol>"
							+ "\n					</KL>"
					;
				}
			}
			xml = xml + "\n					<Komissioner>" + (Komissioner.value() == 0 ? "2" : "6") + "</Komissioner>";
			xml = xml + "\n					<Format>" + allFormaN[allFormaS.value().intValue()] + "</Format>";
			xml = xml + "\n					<AdresLoc>" + AdresLoc.value() + "</AdresLoc>";
			xml = xml + "\n					<Predok></Predok>";
			xml = xml + "\n					<NuzhenPropusk>" + (NuzhenPropusk.value() ? "true" : "false") + "</NuzhenPropusk>";
			xml = xml + "\n					<GolovnojKontragent>" + oklient + "</GolovnojKontragent>";
			xml = xml + "\n					<SmenaUrLica>" + predok + "</SmenaUrLica>";
			xml = xml + "\n					<Osobennosti>" + osobennostiKlienta.value() + "</Osobennosti>";
			xml = xml + "\n					<Den01>" + timeFormat.format(new Date(den01.value().longValue())) + "</Den01>";
			xml = xml + "\n					<Den02>" + timeFormat.format(new Date(den02.value().longValue())) + "</Den02>";
			xml = xml + "\n					<Den11>" + timeFormat.format(new Date(den11.value().longValue())) + "</Den11>";
			xml = xml + "\n					<Den12>" + timeFormat.format(new Date(den12.value().longValue())) + "</Den12>";
			xml = xml + "\n					<Den21>" + timeFormat.format(new Date(den21.value().longValue())) + "</Den21>";
			xml = xml + "\n					<Den22>" + timeFormat.format(new Date(den22.value().longValue())) + "</Den22>";
			xml = xml + "\n					<Den31>" + timeFormat.format(new Date(den31.value().longValue())) + "</Den31>";
			xml = xml + "\n					<Den32>" + timeFormat.format(new Date(den32.value().longValue())) + "</Den32>";
			xml = xml + "\n					<Den41>" + timeFormat.format(new Date(den41.value().longValue())) + "</Den41>";
			xml = xml + "\n					<Den42>" + timeFormat.format(new Date(den42.value().longValue())) + "</Den42>";
			xml = xml + "\n					<Den51>" + timeFormat.format(new Date(den51.value().longValue())) + "</Den51>";
			xml = xml + "\n					<Den52>" + timeFormat.format(new Date(den52.value().longValue())) + "</Den52>";
			xml = xml + "\n					<Den61>" + timeFormat.format(new Date(den61.value().longValue())) + "</Den61>";
			xml = xml + "\n					<Den62>" + timeFormat.format(new Date(den62.value().longValue())) + "</Den62>";
			xml = xml + "\n					<Den71>" + timeFormat.format(new Date(den71.value().longValue())) + "</Den71>";
			xml = xml + "\n					<Den72>" + timeFormat.format(new Date(den72.value().longValue())) + "</Den72>";
			//xml = xml + "\n					<BezPechati>" + (BezPechati.value() ? "true" : "false") + "</BezPechati>";
			//xml = xml + "\n					<BezPechati>false</BezPechati>";
			xml = xml + "\n					<SmenaUrLicaSrazu>" + (SmenaUrLicaSrazu.value() ? "1" : "0") + "</SmenaUrLicaSrazu>";
			xml = xml + "\n				</Zayavka>"
					+ "\n			</Vigruzit>"
					+ "\n		</soap:Body>"
					+ "\n	</soap:Envelope>";
			//System.out.println(xml);
			//String url=Settings.getInstance().getBaseURL() + "ZayavkaNaKlientaTest.1cws";
			//String url=Settings.getInstance().getBaseURL() + "ZayavkaNaKlientatest.1cws";
			String url = Settings.getInstance().getBaseURL() + "ZayavkaNaKlienta.1cws";
			//url = "https://testservice.swlife.ru/simutkin_hrc/ru_RU/ws/ZayavkaNaKlienta";
			System.out.println(url + " ->" + xml);
			final RawSOAP r = new RawSOAP();
			r.xml.is(xml)//
					.url.is(url)//
					//.url.is(Settings.getInstance().getBaseURL() + "ZayavkaNaKlientatest.1cws")//
					//.url.is(Settings.getInstance().getBaseURL() + "ZayavkaNaKlienta.1cws")//
					.afterError.is(new Task() {
				@Override
				public void doTask() {
					Auxiliary.warn("Ошибка: " + r.exception.property.value(), ActivityOdnaAnketa.this);
				}
			})//
					.afterSuccess.is(new Task() {
				@Override
				public void doTask() {
					if (r.statusCode.property.value() >= 100 //
							&& r.statusCode.property.value() <= 300//
							&& r.exception.property.value() == null//
					) {
						String response = r.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
						Auxiliary.warn("Результат: " + response, ActivityOdnaAnketa.this);
						if ("ок".equals(response.trim())) {
							vigruzhen.value((int) new Date().getTime());
							save();
							finish();
						}
					} else {
						Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityOdnaAnketa.this);
					}
					//System.out.println(r.data.dumpXML());
				}
			})//
					.startLater(this, "Отправка", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
		}
	}
*/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setTitle("Анкета нового клиента");
		timeFormat = new SimpleDateFormat("HH:mm");
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		vseKontragenty = Cfg.vseKontragenty();
		dataGrid = new DataGrid(this);
		FIO = new ColumnText();
		Telefon = new ColumnText();
		Dolztost = new ColumnText();
		Rol = new ColumnText();
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		PotencialniyKlientChoice = new RedactFilteredSingleChoice(this);
		PKNew = new RedactSingleChoice(this);
		PKNew.item("ПК");
		PKNew.item("Новый");
		GolovnoyKontragentChoice = new RedactFilteredSingleChoice(this);//.filter.is(INN);
		PredokChoise = new RedactFilteredSingleChoice(this);
		RedactSingleChoice PodrazdelenieChoice = new RedactSingleChoice(this);
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			PodrazdelenieChoice.item(Cfg.territory().children.get(i).child("territory").value.property.value());
		}
		Podrazdelenie.afterChange(new Task() {
			@Override
			public void doTask() {
				//String kod = Cfg.territory().children.get(Podrazdelenie.value().intValue()).child("kod").value.property.value();
				//String kod = Cfg.kodPodrazdeleni(Podrazdelenie.value().intValue());
				//Bough k = Cfg.kontragentyByKod(kod);

				PotencialniyKlientChoice.items.removeAllElements();
				GolovnoyKontragentChoice.items.removeAllElements();
				PredokChoise.items.removeAllElements();
				//PotencialniyKlientChoice.item("[потенциальный клиент]");
				//PotencialniyKlientChoice.item("[новый клиент]");
				PotencialniyKlientChoice.item("[нет]");
				GolovnoyKontragentChoice.item("[нет]");
				PredokChoise.item("[нет]");
				for (int i = 0; i < vseKontragenty.children.size(); i++) {
					//if (k.children.get(i).child("naimenovanie").value.property.value().startsWith("ПК")) {
					PotencialniyKlientChoice.item(vseKontragenty.children.get(i).child("kod").value.property.value() + ": " + vseKontragenty.children.get(i).child("naimenovanie").value.property.value());
					//}
					//OsnovnoiKlientTTChoice.item(k.children.get(i).child("naimenovanie").value.property.value());
					GolovnoyKontragentChoice.item(vseKontragenty.children.get(i).child("inn").value.property.value() + ": " + vseKontragenty.children.get(i).child("naimenovanie").value.property.value());
					PredokChoise.item(vseKontragenty.children.get(i).child("naimenovanie").value.property.value());
				}
			}
		});
		layoutless.field(this, 0, "Подразделение", PodrazdelenieChoice.selection.is(Podrazdelenie), 10 * Auxiliary.tapSize);
		layoutless.child(new Decor(this).labelText.is("Координаты")//
				.labelAlignRightCenter()//
				.left().is(layoutless.shiftX.property)//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 1 * Auxiliary.tapSize))//
				.width().is(layoutless.width().property.multiply(0.3))//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		layoutless.child(new RedactNumber(this).number.is(shirota)//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(0.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 1 * Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		layoutless.child(new RedactNumber(this).number.is(dolgota)//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(3.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 1 * Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).labelText.is("текущие").afterTap.is(new Task() {
					@Override
					public void doTask() {
						//shirota.value(Session.getLatitude());
						shirota.value(GPSInfo.lastLatitude());
						//dolgota.value(Session.getLongitude());
						dolgota.value(GPSInfo.lastLongitude());
					}
				})//
						.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(6.1 * Auxiliary.tapSize)))//
						.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 1 * Auxiliary.tapSize))//
						.width().is(2 * Auxiliary.tapSize)//
						.height().is(0.8 * Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).labelText.is("карта").afterTap.is(new Task() {
					@Override
					public void doTask() {
						String url = "https://maps.google.ru/maps?ll=" + shirota.value() + "," + dolgota.value() + "&z=17";
						//System.out.println(url);
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(browserIntent);
					}
				})//
						.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(8.1 * Auxiliary.tapSize)))//
						.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 1 * Auxiliary.tapSize))//
						.width().is(2 * Auxiliary.tapSize)//
						.height().is(0.8 * Auxiliary.tapSize)//
		);
		//layoutless.field(this, 2, "Головной контрагент", OsnovnoiKlientTTChoice.selection.is(OsnovnoiKlientTT), 10 * Auxiliary.tapSize);
		//layoutless.field(this, 5, "КПП", new RedactText(this).text.is(KPP));
		layoutless.field(this, 2, "Тип", new RedactSingleChoice(this).selection.is(UrFizLico)//
						.item("Юр. лицо (ООО)")//
						.item("Юр. лицо (ЗАО)")//
						.item("Юр. лицо (ОАО)")//
						.item("Юр. лицо (ПО)")//
						.item("Юр. лицо (другое)")//
						.item("Физ. лицо (ИП)")//
				//.item("Физ. лицо (СЗ)")//
		);
		layoutless.child(new Decor(this).labelText.is("КПП").labelAlignRightCenter().hidden().is(UrFizLico.equals(5))//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(new Numeric().value(5.1 * Auxiliary.tapSize))))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 6 * Auxiliary.tapSize))//
				.width().is(1.5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		RedactText kp = new RedactText(this);
		kp.setInputType(InputType.TYPE_CLASS_NUMBER);
		layoutless.child(kp.text.is(KPP).hidden().is(UrFizLico.more(4))//.equals(5))//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(new Numeric().value(7.1 * Auxiliary.tapSize)
						//.when(UrFizLico.equals(4)).otherwise(7.1 * Auxiliary.tapSize)
				)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 6 * Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		layoutless.child(new RedactText(this).text.is(FizOther).hidden().is(UrFizLico.equals(4).not())//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 2 * Auxiliary.tapSize))//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);


		layoutless.field(this, 4, new Note().value("Фамилия, инициалы ИП").when(UrFizLico.more(4)//.equals(5)
				).otherwise("Наименование ЮЛ")//
				, new RedactText(this).text.is(Naimenovanie), 10 * Auxiliary.tapSize);
		layoutless.field(this, 5, "Комментарий", new RedactText(this).text.is(Komentariy), 10 * Auxiliary.tapSize);
		RedactText bik = new RedactText(this).text.is(BIK);
		bik.setInputType(InputType.TYPE_CLASS_NUMBER);
		layoutless.field(this, 6, "БИК банка", bik);
		RedactText rs = new RedactText(this).text.is(NomerScheta);
		rs.setInputType(InputType.TYPE_CLASS_NUMBER);
		layoutless.field(this, 7, "Расчётный счёт", rs);
		layoutless.field(this, 8, "Комиссионер", new RedactSingleChoice(this).item("ООО Свит Лайф Фудсервис").item("ИП Гусев А.П.").selection.is(Komissioner));
		layoutless.field(this, 9, "Вывеска", new RedactText(this).text.is(Viveska));
		/*layoutless.child(new RedactToggle(this).yes.is(this.BezPechati).labelText.is("без печати")//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 9 * Auxiliary.tapSize))//
				.width().is(5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);*/
		layoutless.field(this, 10, "Факт. адрес", new RedactText(this).text.is(FaktAdres), 10 * Auxiliary.tapSize);
		layoutless.field(this, 11, "Адрес доставки", new RedactText(this).text.is(AdresDostavki), 10 * Auxiliary.tapSize);
		layoutless.field(this, 12, "Адрес локализации", new RedactText(this).text.is(AdresLoc), 10 * Auxiliary.tapSize);
		layoutless.field(this, 13, "Подъезд большегруза", new RedactSingleChoice(this).item("да").item("нет").selection.is(Bolshegruz));


		RedactText km = new RedactText(this).text.is(KolichestvoMest);
		km.setInputType(InputType.TYPE_CLASS_NUMBER);
		layoutless.field(this, 14, "Количество посад. мест", km);
		choiceVidKuhni = new RedactSingleChoice(this).selection.is(selVidKuhni)//
				.item(allVidKuhni[0])//
				.item(allVidKuhni[1])//
				.item(allVidKuhni[2])//
				.item(allVidKuhni[3])//
				.item(allVidKuhni[4])//
				.item(allVidKuhni[5])//
		;
		//layoutless.field(this, 14, "Вид кухни", new RedactText(this).text.is(VidKuhni));
		layoutless.field(this, 15, "Вид кухни", choiceVidKuhni);

		tiptt = new RedactSingleChoice(this).selection.is(TipTT);
		String sql = "select naimenovanie as naimenovanie,kod as kod from TipyTorgovihTochek where deletionMark=x'00' order by naimenovanie;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(sql+": "+data.dumpXML());
		for (int i = 0; i < data.children.size(); i++) {
			tiptt.item(data.children.get(i).child("naimenovanie").value.property.value());
			//System.out.println(i+": "+data.children.get(i).child("naimenovanie").value.property.value());
		}
		layoutless.field(this, 16, "Тип торговой точки", tiptt);
		//choiceForma = new RedactSingleChoice(this).selection.is(TipTT);
		layoutless.child(new RedactSingleChoice(this).selection.is(allFormaS)//
				.item(allFormaT[0])//
				.item(allFormaT[1])//
				.item(allFormaT[2])//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 14 * Auxiliary.tapSize))//
				.width().is(5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);


		//layoutless.field(this, 13, "Тип торговой точки", new RedactText(this).text.is(TipTT));
		//allFormaT

		layoutless.field(this, 17, "Контрагент", PKNew.selection.is(PKorNew), 10 * Auxiliary.tapSize);
		layoutless.field(this, 18, "На этого ПК", PotencialniyKlientChoice.selection.is(PotencialniyKlient), 10 * Auxiliary.tapSize);
		RedactText inn = new RedactText(this);
		inn.setInputType(InputType.TYPE_CLASS_NUMBER);
		layoutless.field(this, 3, "ИНН", inn.text.is(INN));
		layoutless.child(new Knob(this).labelText.is("Заполнить по ИНН").afterTap.is(this.fillFromINN)
				.width().is(Auxiliary.tapSize * 4)
				.height().is(Auxiliary.tapSize * 0.8)
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 3 * Auxiliary.tapSize))
		);


		layoutless.field(this, 19, "Головной контрагент", GolovnoyKontragentChoice.selection.is(GolovnoyKontragent), 10 * Auxiliary.tapSize);
		layoutless.field(this, 20, "Юр. адрес", new RedactText(this).text.is(UrAdres), 10 * Auxiliary.tapSize);
		//layoutless.field(this, 20, "Время приёма товара", new RedactText(this).text.is(VremyaRaboti) );


		layoutless.field(this, 21, "Время работы", new RedactTime(this).time.is(den01));
		layoutless.child(new RedactTime(this).time.is(den02).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 21 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 22, "пн", new RedactTime(this).time.is(den11));
		layoutless.child(new RedactTime(this).time.is(den12).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 22 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 23, "вт", new RedactTime(this).time.is(den21));
		layoutless.child(new RedactTime(this).time.is(den22).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 23 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 24, "ср", new RedactTime(this).time.is(den31));
		layoutless.child(new RedactTime(this).time.is(den32).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 24 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 25, "чт", new RedactTime(this).time.is(den41));
		layoutless.child(new RedactTime(this).time.is(den42).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 25 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 26, "пт", new RedactTime(this).time.is(den51));
		layoutless.child(new RedactTime(this).time.is(den52).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 26 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 27, "сб", new RedactTime(this).time.is(den61));
		layoutless.child(new RedactTime(this).time.is(den62).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 27 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));
		layoutless.field(this, 28, "вс", new RedactTime(this).time.is(den71));
		layoutless.child(new RedactTime(this).time.is(den72).left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 28 * Auxiliary.tapSize)).width().is(5 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize));

		layoutless.field(this, 29, "Смена юр.лица", PredokChoise.selection.is(PredokNum), 10 * Auxiliary.tapSize);
		layoutless.child(new RedactToggle(this).yes.is(SmenaUrLicaSrazu).labelText.is("сразу")//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(10.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 29 * Auxiliary.tapSize))//
				.width().is(5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);


		layoutless.field(this, 30, "Особенности клиента", new RedactText(this).text.is(osobennostiKlienta), 10 * Auxiliary.tapSize);
		layoutless.field(this, 31, "Комментарии к пропуску", new RedactText(this).text.is(KommentariiPropusk), 10 * Auxiliary.tapSize);
		layoutless.field(this, 32, "Особенности режима работы", new RedactText(this).text.is(osobennostiRejimaRaboty), 10 * Auxiliary.tapSize);

		layoutless.child(new RedactToggle(this).yes.is(NuzhenPropusk).labelText.is("нужен пропуск")//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(10.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 31 * Auxiliary.tapSize))//
				.width().is(5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);

		layoutless.innerHeight.is(0.8 * 40 * Auxiliary.tapSize);

		layoutless.child(dataGrid//
				.columns(new Column[]{ //
						//
						FIO.title.is("ФИО").width.is(5 * Auxiliary.tapSize)//
						, Telefon.title.is("телефон").width.is(5 * Auxiliary.tapSize) //
						, Dolztost.title.is("должность").width.is(5 * Auxiliary.tapSize) //
						, Rol.title.is("роль").width.is(5 * Auxiliary.tapSize) //
				})//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
				.center.is(true)//
				.width().is(layoutless.width().property)//
				.height().is(4 * Auxiliary.tapSize)//
				.top().is(layoutless.shiftY.property.plus(0.8 * 33 * Auxiliary.tapSize)));
		//setData();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			System.out.println("vigruzhen.value() " + vigruzhen.value());
			String s = bundle.getString("_id");
			if (s != null) {
				_id = s;
				this.setTitle("Анкета: " + _id);//+" "+((vigruzhen.value() > 0)?"выгружена":"не выгружена"));
				select();
			} else {
				this.setTitle("Новая анкета");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (_id != null) {
			select();
		}
	}
}
