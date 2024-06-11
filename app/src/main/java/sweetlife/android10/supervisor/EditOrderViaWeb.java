package sweetlife.android10.supervisor;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.DatePicker;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Expect;
import reactive.ui.RawSOAP;
import reactive.ui.RedactDate;
import reactive.ui.RedactNumber;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.fixedprices.FixedPricesNomenclatureData;
import sweetlife.android10.data.fixedprices.ZayavkaNaSkidki;
import sweetlife.android10.ui.*;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

public class EditOrderViaWeb {
	public Vector<OrderItemInfo> orderItems = new Vector<OrderItemInfo>();
	public Context context;
	ActivityWebServicesReports me;
	String documentDate;
	String hrc;
	String shipDate;
	//String currentKlientName;
	String currentKlientKod;
	String currentDogovorName;
	public static String documentNumber = "";
	Note comment = new Note();

	void start() {
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение..." ).task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is(Settings.getInstance().getBaseURL() + "ChangeOfOrders.1cws" )//
						.xml.is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
						+ "\n<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
						+ "\n	<S:Body>"//
						+ "\n		<Gat xmlns=\"http://ws.swl/ChangeOrders\">"//
						+ "\n			<Namber>" + documentNumber + "</Namber>"//
						+ "\n			<Date>" + ActivityWebServicesReports.reformatDate(documentDate) + "</Date>"//
						+ "\n		</Gat>"//
						+ "\n	</S:Body>"//
						+ "\n</S:Envelope>"//
				);
				Report_Base.startPing();
				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				System.out.println("r.data " + r.data.dumpXML());
				Vector<Bough> items = r.data.child("soap:Body" )//
						.child("m:GatResponse" )//
						.child("m:return" )//
						.child("m:Table" )//
						.children;
				for (int i = 0; i < items.size(); i++) {
					OrderItemInfo info = new OrderItemInfo();
					info.artikul = items.get(i).child("Article" ).value.property.value();
					info.cena = Numeric.string2double(items.get(i).child("m:Cena" ).value.property.value());
					info.min = Numeric.string2double(items.get(i).child("m:Min" ).value.property.value());
					info.max = Numeric.string2double(items.get(i).child("m:Max" ).value.property.value());
					info.kolichestvo = Numeric.string2double(items.get(i).child("KolVo" ).value.property.value());
					info.naimenovanie = nomenklaturaNaimenovanieByArtikul(info.artikul);
					orderItems.add(info);

					String sql = "select pp.Stoimost/pp.kolichestvo as poslednyaa"
							//+"\n		,nn.skladEdIzm || ' по ' || nn.skladEdVes || 'кг' as [EdinicyIzmereniyaNaimenovanie] "
							+ "\n	from Prodazhi_last pp"
							+ "\n		join Nomenklatura_sorted nn on nn._IDRRef=pp.nomenklatura and nn.artikul='" + info.artikul + "'"
							+ "\n		join DogovoryKontragentov_strip dog on pp.DogovorKontragenta=dog._IDRRef"
							+ "\n		join Kontragenty kk on kk._idrref=dog.vladelec and kk.kod='" + currentKlientKod + "'"
							+ "\n	limit 1;";
					Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					info.poslednyaya = bough.child("row" ).child("poslednyaa" ).value.property.value();
					String sql2 = "select nn.skladEdIzm || ' по ' || nn.skladEdVes || 'кг' as [EdinicyIzmereniyaNaimenovanie], nn.kvant as MinNorma, nn.otchEdKoef as [Koephphicient]"
							+ "\n	from Nomenklatura_sorted nn"
							+ "\n	where nn.artikul='" + info.artikul + "'"
							+ "\n	limit 1;";
					Bough bough2 = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql2, null));
					//System.out.println(sql2);
					info.edizm = bough2.child("row" ).child("EdinicyIzmereniyaNaimenovanie" ).value.property.value();
					info.minNorma = bough2.child("row" ).child("MinNorma" ).value.property.value();
					info.koephphicient = bough2.child("row" ).child("Koephphicient" ).value.property.value();


				}
				comment = r.data.child("soap:Body" )//
						.child("m:GatResponse" )//
						.child("m:return" )//
						.child("m:Head" )//
						.child("Comment" ).value.property;
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				promptActionReportStatusyZakazov();
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).start(context);
	}

	void promptChangeDate() {
		Calendar c = Calendar.getInstance();
		final DateFormat from = new SimpleDateFormat("dd.MM.yyyy" );
		try {
			Date d = from.parse(shipDate);
			c.setTime(d);
			new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Calendar newCalendar = Calendar.getInstance();
					newCalendar.set(Calendar.YEAR, year);
					newCalendar.set(Calendar.MONTH, monthOfYear);
					newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					shipDate = from.format(newCalendar.getTime());

					requestChangeOrderState("5" );
				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)//
			).show();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	void promptSendDelete() {
		Auxiliary.pickConfirm(context, "Пометить на удаление", "Удалить", new Task() {
			@Override
			public void doTask() {
				requestChangeOrderState("2" );
			}
		});
	}
void promptRasporyajenieNaOtgruzku(){
	Intent intent = new Intent();
	intent.setClass(context, Dialog_EditDisposal.class);
	intent.putExtra("is_editable", true);
	String sql="select _idrref as _idrref from kontragenty where kod="+currentKlientKod;
	String hexKlient="x'"+Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null)).child("row").child("_idrref").value.property.value()+"'";
	intent.putExtra("hexKlient",hexKlient);
	context.startActivity(intent);
}
	void promptForceApprove() {
		Auxiliary.pickConfirm(context, "Провести без ограничений на наценку", "Провести", new Task() {
			@Override
			public void doTask() {
				sendForceApprove();
			}
		});
	}

	void sendForceApprove() {
		final Bough b = new Bough();
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/ZakaziPokupatelya/ProvestiPoNacenke/" + documentNumber
				+ "/" + Auxiliary.tryReFormatDate(shipDate, "dd.MM.yyyy", "yyyMMdd" )
				+ "/" + Cfg.whoCheckListOwner();
		System.out.println("sendForceApprove " + url);
		Task sendTask = new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8" );
					//String txt = Auxiliary.parseChildOrRaw(msg, "Message");
					//result.value(result.value() + "\n" + msg);
					Bough dump = Bough.parseJSONorThrow(msg);
					System.out.println(dump.dumpXML());
					b.child("ДанныеПоЗаказам").child("Заказы").children=dump.children;

					me.preReport.writeCurrentPage();
				} catch (Throwable t) {
					t.printStackTrace();
					//b.value(b.value() + "\n" + t.getMessage());
				}
			}
		};
		Task afterSend = new Task() {
			@Override
			public void doTask() {
				//Auxiliary.warn(result.value(), context);
				Activity_UploadBids.buildDialogResult(context,"Проведение заказов по наценке", b);
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		};
		Expect expect = new Expect().status.is("Подождите" ).task.is(sendTask).afterDone.is(afterSend);
		expect.start(context);
	}

	void openSchetNaOplatu() {
		System.out.println("openSchetNaOplatu " + documentNumber);
		ReportPechatSchetaNaOplatu.tempInitKind = 0;
		me.tapReport2(ReportPechatSchetaNaOplatu.folderKey());
	}

	void promptActionReportStatusyZakazov() {
		final Numeric nn = new Numeric();
		//currentKlientKod="0";
		Auxiliary.pickSingleChoice(context, new String[]{//
						"Провести"//
						, "Пометить заказ на удаление"//
						, "Изменить заказ"//, "Изменить номенклатуру"//
						, "Перенести дату"//
						, "Счёт на оплату"//
						//, "Заявка на фикс.цену"//
						, "Изменить договор или оплату"//
						//, "Добавить номенклатуру"//
						, "Пересчитать цены"//
						, "Повторить заказ"//
						, "Сменить контрагента в заказе"//
						, "Удаление мелких заказов"//
						, "Провести (для РД)"//
						, "Распоряжение на отгрузку"//
				}, nn//
				, hrc + ": №" + documentNumber //+ "/" + documentDate
						+ ", отгрузка " + shipDate//
				, new Task() {
					@Override
					public void doTask() {
						if (nn.value().intValue() == 0) {
							requestChangeOrderState("1" );
						}
						if (nn.value().intValue() == 1) {
							promptSendDelete();
						}
						if (nn.value().intValue() == 2) {
							requestItemsChangeNew();
						}
						if (nn.value().intValue() == 3) {
							promptChangeDate();
						}
						if (nn.value().intValue() == 4) {
							openSchetNaOplatu();
						}
						//if (nn.value().intValue() == 5) {
						//	promptFixCena();
						//}
						if (nn.value().intValue() == 5) {
							promptDogovorTipOplaty();
						}
						/*
						if (nn.value().intValue() == 6) {
							promptNewItem();
						}*/
						if (nn.value().intValue() == 6) {
							promptRecalculate();
						}
						if (nn.value().intValue() == 7) {
							promptCloneOrder();
						}
						if (nn.value().intValue() == 8) {
							promptReplaceOrderClient();
						}
						if (nn.value().intValue() == 9) {
							promptUdalenieMelkihZakazov();
						}
						if (nn.value().intValue() == 10) {
							promptForceApprove();
						}
						if (nn.value().intValue() == 11) {
							promptRasporyajenieNaOtgruzku();
						}

					}
				}, null, null, null, null);
	}

	String nomenklaturaNaimenovanieByArtikul(String artikul) {
		Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(//
				"select Naimenovanie from nomenklatura where artikul='" + artikul + "' limit 1;"//
				, null));
		return bough.child("row" ).child("Naimenovanie" ).value.property.value();
	}

	void sendUdalenieMelkihZakazov() {
		final Note result = new Note().value("Удаление мелких заказов:" );
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/ApproveSmallOrders/" + documentNumber;
		System.out.println("sendUdalenieMelkihZakazov " + url);
		Task sendTask = new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8" );
					/*System.out.println(msg);
					String txt = msg;
					Bough bb = Bough.parseJSON(msg);
					String mm = bb.child("Message").value.property.value();
					if (mm.length() > 0) {
						txt = mm;
					}
					*/
					String txt = Auxiliary.parseChildOrRaw(msg, "Message" );
					result.value(result.value() + "\n" + txt);
					me.preReport.writeCurrentPage();
				} catch (Throwable t) {
					t.printStackTrace();
					result.value(result.value() + "\n" + t.getMessage());
				}
			}
		};
		Task afterSend = new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(result.value(), context);
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		};
		Expect expect = new Expect().status.is("Подождите" ).task.is(sendTask).afterDone.is(afterSend);
		expect.start(context);
	}

	void promptUdalenieMelkihZakazov() {
		Auxiliary.pickConfirm(context, "Удалить " + documentNumber + " из мелких заказов?", "Удалить", new Task() {
			@Override
			public void doTask() {
				sendUdalenieMelkihZakazov();
			}
		});
	}

	void promptReplaceOrderClient() {
		final Bough b = new Bough();
		String sql = "select kk.kod as kod, kk.naimenovanie as naimenovanie"
				+ " from MarshrutyAgentov mm join Kontragenty kk on kk._idrref=mm.Kontragent"
				+ " group by kk.kod, kk.naimenovanie"
				+ " order by kk.naimenovanie";
		final Bough kk = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		final String[] items = new String[kk.children.size()];
		for (int i = 0; i < kk.children.size(); i++) {
			items[i] = kk.children.get(i).child("kod" ).value.property.value() + ": " + kk.children.get(i).child("naimenovanie" ).value.property.value();
		}
		final Numeric defaultSelection = new Numeric();
		Auxiliary.pickFilteredChoice(context, items, defaultSelection, new Task() {
			@Override
			public void doTask() {
				System.out.println(kk.children.get(defaultSelection.value().intValue()).dumpXML());
				final String url = //"https://service.swlife.ru/hrc120107/hs/ZakaziPokupatelya/IzmenitKontragenta/"
						Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZakaziPokupatelya/IzmenitKontragenta/"
								+ documentNumber
								+ "/" + kk.children.get(defaultSelection.value().intValue()).child("kod" ).value.property.value()
								+ "/" + Cfg.whoCheckListOwner().trim();
				System.out.println(url);
				Expect expect = new Expect().status.is("Подождите" ).task.is(new Task() {
					@Override
					public void doTask() {
						try {
							byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
							String msg = new String(bytes, "UTF-8" );
							System.out.println(msg);
							//b.child("result" ).value.is(msg);
							Bough dump = Bough.parseJSONorThrow(msg);
							System.out.println(dump.dumpXML());
							b.child("ДанныеПоЗаказам").child("Заказы").children=dump.children;

							me.preReport.writeCurrentPage();
						} catch (Throwable t) {
							t.printStackTrace();
							b.child("result" ).value.is(t.toString());
						}
					}
				}).afterDone.is(new Task() {
					@Override
					public void doTask() {
						//Auxiliary.warn(b.child("result" ).value.property.value(), context);
						Activity_UploadBids.buildDialogResult(context,"Смена контрагента", b);
						me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
					}
				});
				expect.start(context);
			}
		});
	}


	void promptDogovorTipOplaty() {
		System.out.println("promptDogovorTipOplaty " + currentKlientKod);

		SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
		//String sql = "select hex(_idrref) as id from kontragenty where trim(naimenovanie)='" + currentKlientName + "';";
		String sql = "select hex(_idrref) as id from kontragenty where kod=" + currentKlientKod.trim() + ";";
		Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		if (bb.child("row" ).child("id" ).value.property.value().trim().length() < 7) {
			Auxiliary.warn("Не найден контрагент " + currentKlientKod, context);
			return;
		}
		String clientID = "x'" + bb.child("row" ).child("id" ).value.property.value() + "'";
		sql = "select "
				+ "	case when (dgvr.Podrazdelenie = x'00000000000000000000000000000000' or dgvr.Podrazdelenie = x'00') then 0 else 1 end podrazdelenie"
				+ "	,ifnull(dgvr.ProcentPredoplaty,0) as procent"
				+ "	,kod as kod"
				+ "	,dgvr.naimenovanie as name"
				+ "	from DogovoryKontragentov dgvr "
				+ "	where dgvr.PometkaUdaleniya=x'00' "
				+ "	and dgvr.vladelec=" + clientID
				+ "	limit 100;";
		System.out.println("promptDogovorTipOplaty " + sql);
		bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		System.out.println(bb.dumpXML());
		Vector<String> labels = new Vector<String>();
		final Vector<String> kods = new Vector<String>();
		final Vector<String> tips = new Vector<String>();
		for (int i = 0; i < bb.children.size(); i++) {
			Bough row = bb.children.get(i);
			String kod = row.child("kod" ).value.property.value();
			String name = row.child("name" ).value.property.value();
			double podrazdelenie = Numeric.string2double(row.child("podrazdelenie" ).value.property.value());
			double procent = Numeric.string2double(row.child("procent" ).value.property.value());
			if ((podrazdelenie == 0) && (procent == 0)) {
				System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " нал" );
				System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " товчек" );
				labels.add(name + ", нал." );
				kods.add(kod);
				tips.add("0" );
				labels.add(name + ", тов.чек" );
				kods.add(kod);
				tips.add("2" );
			} else {
				if ((podrazdelenie == 0) && (procent == 100)) {
					System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " нал" );
					System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " безнал" );
					labels.add(name + ", нал." );
					kods.add(kod);
					tips.add("0" );
					labels.add(name + ", безнал." );
					kods.add(kod);
					tips.add("1" );
				} else {
					//System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " нал");
					System.out.println(kod + "/" + podrazdelenie + "/" + procent + "/" + name + " безнал" );
					//labels.add(name + ", нал.");
					//kods.add(kod);
					//tips.add("0");
					labels.add(name + ", безнал." );
					kods.add(kod);
					tips.add("1" );
				}
			}
		}
		if (labels.size() > 0) {
			final String[] list = new String[labels.size()];
			labels.copyInto(list);
			final Numeric idx = new Numeric().value(-1);
			Auxiliary.pickSingleChoice(context, list, idx);
			idx.afterChange(new Task() {
				public void doTask() {
					System.out.println(list[idx.value().intValue()]);
					sendDogovorTipOplaty(documentNumber
							, Auxiliary.tryReFormatDate(shipDate, "dd.MM.yyyy", "yyyyMMdd" )

							, tips.get(idx.value().intValue())
							, kods.get(idx.value().intValue())
					);
				}
			}, true);
		} else {
			Auxiliary.warn("Нет доступных договоров.", context);
		}

	}

	void sendDogovorTipOplaty(final String nomerZakaza, final String dataZakaza, final String nomerFormiOplaty, final String kodDogovora) {
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите" ).task.is(new Task() {
			@Override
			public void doTask() {
				try {
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/ZakaziPokupatelya/IzmenitFormuIDogovor"
							+ "/" + URLEncoder.encode(nomerZakaza.trim(), "utf-8" )
							+ "/" + URLEncoder.encode(dataZakaza.trim(), "utf-8" )
							+ "/" + URLEncoder.encode(nomerFormiOplaty.trim(), "utf-8" )
							+ "/" + URLEncoder.encode(kodDogovora.trim(), "utf-8" )
							+ "/" + URLEncoder.encode(Cfg.whoCheckListOwner().trim(), "utf-8" );
					Report_Base.startPing();
					System.out.println(url);
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(bytes, "UTF-8" );
					System.out.println(txt);
					Bough dump = Bough.parseJSONorThrow(txt);
					System.out.println(dump.dumpXML());
					//System.out.println(msg);
					//b.child("result" ).value.is(msg);
					b.child("ДанныеПоЗаказам").child("Заказы").children=dump.children;
					me.preReport.writeCurrentPage();
				} catch (Throwable t) {
					t.printStackTrace();
					b.child("error" ).value.is(t.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Activity_UploadBids.buildDialogResult(context,"Смена договора или типа оплаты", b);
				/*Bough bb = Bough.parseJSONorThrow(response);
				if(bb.children.size() > 0){
					String msg = "Выгрузка (" + result.child("result").child("code").value.property.value()
							+ ", " + result.child("result").child("message").value.property.value() + "):\n"
							+ bb.child("Сообщение").value.property.value();
					showUploadResult(msg, bb);
				}else{
					System.out.println("Empty " + result.dumpXML());
					String msg = " \nВозможны ошибки при выгрузке" //+ result.dumpXML();
							+ "\n\nПроверьте статус заказов в отчёте, возможно необходимо удалить повторы"//
							+ "\n\nТекст ответа:"//
							+ result.dumpXML().substring(0, 160).replace("\n", "").replace("  ", " ");
					Auxiliary.alertBreak(msg, Activity_UploadBids.this);
				}*/
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		});
		expect.start(context);

	}
/*
	void promptFixCena() {
		//System.out.println("promptFixCena");
		final Numeric it = new Numeric().value(-1);
		String[] labels = new String[orderItems.size()];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = orderItems.get(i).artikul//
					+ ": " + orderItems.get(i).naimenovanie//
					+ ", " + orderItems.get(i).kolichestvo//
					+ " по " + orderItems.get(i).cena + "руб.";
			if (orderItems.get(i).min > 0 && orderItems.get(i).max > 0) {
				labels[i] = labels[i] + "(от " + orderItems.get(i).min + " до " + orderItems.get(i).max + "руб.)";
			}
		}
		String c = comment.value();
		if (c.trim().length() > 0) {
			c = " (" + c + ")";
		}
		Auxiliary.pickSingleChoice(context, labels, it//
				, "Заказ " + documentNumber + " от " + documentDate + " на " + shipDate + c//
				, new Task() {
					@Override
					public void doTask() {
						promptFixCenaItem(it.value().intValue());
					}
				}, null, null, null, null);
	}*/

	void promptRecalculate() {
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите" ).task.is(new Task() {
			@Override
			public void doTask() {
				try {
					//https://testservice.swlife.ru/golovanew/hs/ZakaziPokupatelya/PereschetCen
					//{"Номер":"13-1722357", "Дата":"20211209"}
					String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
							+ "/hs/ZakaziPokupatelya/PereschetCen";
					String text = "{\"Номер\":\"" + documentNumber + "\", \"Дата\":\"" + ActivityWebServicesReports.reformatDate2(documentDate) + "\"}";
					//Report_Base.startPing();
					System.out.println(url + ": " + text);

					Bough result;
					result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8" ), 12000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					//b.child("result" ).value.is(result.child("message" ).value.property.value());
					System.out.println(result.dumpXML());
					b.child("ДанныеПоЗаказам").child("Заказы").children=result.child("raw").children;


					me.preReport.writeCurrentPage();
				} catch (Throwable t) {
					t.printStackTrace();
					b.child("result" ).value.is(t.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				//Auxiliary.warn(b.child("result" ).value.property.value(), context);
				Activity_UploadBids.buildDialogResult(context,"Пересчёт цен", b);
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		});
		expect.start(context);
	}

	void requestItemsChangeNew() {
		final Bough bb = new Bough();

		Expect expect = new Expect().status.is("Подождите" ).task.is(new Task() {
			@Override
			public void doTask() {
				try {
					String url =
							//"https://testservice.swlife.ru/dbutenko_hrc"
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/ZakaziPokupatelya/PoluchitSostavZakazov/" + Cfg.whoCheckListOwner();
					String text = "[{\"НомерДокумента\":\"" + documentNumber + "\", \"ДатаДокумента\":\"" + ActivityWebServicesReports.reformatDate3(documentDate) + "\"}]";
					Bough result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8"), 12000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);

					//url = "https://service.swlife.ru/hrc120107/hs/ZakaziPokupatelya/PoluchitSostavZakazov/hrc703";
					//text = "[{\"НомерДокумента\":\"12-2059882\", \"ДатаДокумента\":\"2023-10-13\"}]";
					//Bough result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8" ), 12000, "bot28", "Molgav1024", true);

					bb.child("result" ).value.is(result.child("message" ).value.property.value());
					String rawText = result.child("raw" ).value.property.value();
					Bough raw = Bough.parseJSON(rawText);
					System.out.println("requestItemsChange\n" + url + "\n" + text + "\nrawText[" + rawText + "]" );
					bb.child("raw" ).children = raw.children;
					//me.preReport.writeCurrentPage();
					if (rawText.trim().length() < 1) {
						bb.child("raw" ).child("Сообщение" ).value.property.value(bb.child("raw" ).child("Сообщение" ).value.property.value() + "\nнет данных " + rawText);
					}
				} catch (Throwable tt) {
					tt.printStackTrace();
					bb.child("raw" ).child("Сообщение" ).value.property.value(bb.child("raw" ).child("Сообщение" ).value.property.value() + "\n" + tt.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (bb.child("raw" ).child("Сообщение" ).value.property.value().trim().length() > 0) {
					Auxiliary.warn(bb.child("raw" ).child("Сообщение" ).value.property.value(), me);
				} else {
					//createUpdatableOrder(b.child("raw"));
					String client_id = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(
							"select \"x'\" || hex(_idrref) || \"'\" as id from Kontragenty where trim(kod)='"
									+ bb.child("raw" ).child("Данные" ).child("КодКонтрагента" ).value.property.value().trim() + "';"
							, null)).child("row" ).child("id" ).value.property.value();
					String oplatanum = Cfg.tip_nalichnie;//"Наличная";
					if (bb.child("raw" ).child("Данные" ).child("ТипОплаты" ).value.property.value().equals("Безналичная" )) {
						oplatanum = Cfg.tip_beznal;
					}
					if (bb.child("raw" ).child("Данные" ).child("ТипОплаты" ).value.property.value().equals("Товарный чек" )) {
						oplatanum = Cfg.tip_tovcheck;
					}
					String dogovor_idrref = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(
							"select \"x'\" || hex(_idrref) || \"'\" as id from DogovoryKontragentov where trim(kod)='"
									+ bb.child("raw" ).child("Данные" ).child("КодДоговора" ).value.property.value().trim() + "';"
							, null)).child("row" ).child("id" ).value.property.value();
					if (client_id.trim().length() < 1) {
						Auxiliary.warn("Не найден клиент " + bb.child("raw" ).child("Данные" ).child("КодКонтрагента" ).value.property.value().trim(), me);
						return;
					}
					if (dogovor_idrref.trim().length() < 1) {
						Auxiliary.warn("Не найден договор " + bb.child("raw" ).child("Данные" ).child("КодДоговора" ).value.property.value().trim(), me);
						return;
					}
					try {
						Date tt = Auxiliary.mssqlTime.parse(bb.child("raw" ).child("Данные" ).child("ДатаОтгрузки" ).value.property.value());
						Numeric dateShip = new Numeric().value((double) tt.getTime());
						//Auxiliary.tryReFormatDate(b.child("raw").child("Данные").child("ДатаОтгрузки").value.property.value(),"yyyy-MM-ddThh:mm:ss",""));
						//System.out.println("client_id " + client_id);
						//System.out.println("oplatanum " + oplatanum);
						//System.out.println("dogovor_idrref " + dogovor_idrref);
						//System.out.println("dateShip " + dateShip);
						//System.out.println("requestItemsChangeNew " + b.child("raw").dumpXML());

						Intent intent = new Intent();
						intent.setClass(context, Activity_Bid.class);
						intent.putExtra("client_id", client_id);
						intent.putExtra("no_assortiment", true);
						intent.putExtra("dogovor_idrref", dogovor_idrref);
						intent.putExtra("oplatanum", oplatanum);
						intent.putExtra("dateShip", "" + dateShip.value());
						intent.putExtra("nomerDokumenta1C", bb.child("raw" ).child("Данные" ).child("НомерДокумента" ).value.property.value().trim());
						intent.putExtra("nomerDokumentaTablet", bb.child("raw" ).child("Данные" ).child("ВнешнийНомер" ).value.property.value().trim());
						String raw = bb.child("raw" ).dumpXML();
						intent.putExtra("raw_data", "" + raw);
						Activity_Bid.unLockCreateNewOrder();
						me.needRefresh = true;
						context.startActivity(intent);
					} catch (Throwable ttt) {
						ttt.printStackTrace();
					}
				}
			}
		});
		expect.start(context);
	}

	void promptCloneOrder() {
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите" ).task.is(new Task() {
			@Override
			public void doTask() {
				try {
					String url =
							//"https://testservice.swlife.ru/dbutenko_hrc"
							Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
									+ "/hs/ZakaziPokupatelya/PoluchitSostavZakazov/" + Cfg.whoCheckListOwner();
					String text = "[{\"НомерДокумента\":\"" + documentNumber + "\", \"ДатаДокумента\":\"" + ActivityWebServicesReports.reformatDate3(documentDate) + "\"}]";
					//System.out.println(url + ": " + text);
					Bough result;
					result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8" ), 12000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					b.child("result" ).value.is(result.child("message" ).value.property.value());
					String rawText = result.child("raw" ).value.property.value();
					//System.out.println(rawText);
					Bough raw = Bough.parseJSON(rawText);
					//System.out.println("promptCloneOrder\n"+url+"\n"+text+"\n"+raw.dumpXML());
					b.child("raw" ).children = raw.children;
					me.preReport.writeCurrentPage();
				} catch (Throwable t) {
					t.printStackTrace();
					b.child("result" ).value.is(t.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				//Auxiliary.warn(b.child("result").value.property.value(), context);
				//System.out.println(b.dumpXML());
				//me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
				createCloneOrder(b);
			}
		});
		expect.start(context);
	}

	void createUpdatableOrder(final Bough data) {
		System.out.println("createUpdatableOrder " + data.dumpXML());

		currentKlientKod = data.child("Данные" ).child("КодКонтрагента" ).value.property.value();
		System.out.println("currentKlientKod " + currentKlientKod);
		SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
		String sql = "select hex(_idrref) as id from kontragenty where kod=" + currentKlientKod.trim() + ";";
		Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		if (bb.child("row" ).child("id" ).value.property.value().trim().length() < 7) {
			Auxiliary.warn("Не найден контрагент " + currentKlientKod, context);
			return;
		}
		String client_id = "x'" + bb.child("row" ).child("id" ).value.property.value() + "'";
		System.out.println("client_id " + client_id);



/*
		int dogovor_idrref=dogovor_idrrefs.get(idx.value().intValue());
		int oplatanum=tips.get(idx.value().intValue());
		String dateShip= "" + dateShip.value();
		String raw_data = data.child("raw").dumpXML();

		Intent intent = new Intent();
		intent.setClass(context, Activity_Bid.class);

		String client_id = "x'" + bb.child("row").child("id").value.property.value() + "'";
		intent.putExtra("client_id", client_id);

		intent.putExtra("dogovor_idrref",dogovor_idrref );
		intent.putExtra("oplatanum",oplatanum);
		intent.putExtra("dateShip",dateShip);
		intent.putExtra("raw_data", raw_data);

		Activity_Bid.unLockCreateNewOrder();
		context.startActivity(intent);
		*/
	}

	void createCloneOrder(final Bough data) {
		SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
		//String sql = "select hex(_idrref) as id from kontragenty where trim(naimenovanie)='" + currentKlientName + "';";
		String sql = "select hex(_idrref) as id from kontragenty where kod=" + currentKlientKod.trim() + ";";
		Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		if (bb.child("row" ).child("id" ).value.property.value().trim().length() < 7) {
			Auxiliary.warn("Не найден контрагент " + currentKlientKod, context);
			return;
		}
		final String clientID = "x'" + bb.child("row" ).child("id" ).value.property.value() + "'";
		sql = "select "
				+ "	case when (dgvr.Podrazdelenie = x'00000000000000000000000000000000' or dgvr.Podrazdelenie = x'00') then 0 else 1 end podrazdelenie"
				+ "	,ifnull(dgvr.ProcentPredoplaty,0) as procent"
				+ "	,kod as kod"
				+ "	,_idrref as _idrref"
				+ "	,dgvr.naimenovanie as name"
				+ "	from DogovoryKontragentov_strip dgvr "
				+ "	where dgvr.PometkaUdaleniya=x'00' "
				+ "	and dgvr.vladelec=" + clientID
				+ "	limit 100;";
		bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		Vector<String> labels = new Vector<String>();
		final Vector<String> dogovor_idrrefs = new Vector<String>();
		final Vector<String> tips = new Vector<String>();
		for (int i = 0; i < bb.children.size(); i++) {
			Bough row = bb.children.get(i);
			String dogovor_idrref = "x'" + row.child("_idrref" ).value.property.value() + "'";
			String name = row.child("name" ).value.property.value();
			double podrazdelenie = Numeric.string2double(row.child("podrazdelenie" ).value.property.value());
			double procent = Numeric.string2double(row.child("procent" ).value.property.value());
			if ((podrazdelenie == 0) && (procent == 0)) {
				labels.add(name + ", Наличная" );
				dogovor_idrrefs.add(dogovor_idrref);
				tips.add(Cfg.tip_nalichnie);
				labels.add(name + ", Товарный чек" );
				dogovor_idrrefs.add(dogovor_idrref);
				tips.add(Cfg.tip_tovcheck);
			} else {
				if ((podrazdelenie == 0) && (procent == 100)) {
					labels.add(name + ", Наличная" );
					dogovor_idrrefs.add(dogovor_idrref);
					tips.add(Cfg.tip_nalichnie);
					labels.add(name + ", Безналичная" );
					dogovor_idrrefs.add(dogovor_idrref);
					tips.add(Cfg.tip_beznal);
				} else {
					//labels.add(name + ", Наличная");
					//dogovor_idrrefs.add(dogovor_idrref);
					//tips.add(Cfg.tip_nalichnie);
					labels.add(name + ", Безналичная" );
					dogovor_idrrefs.add(dogovor_idrref);
					tips.add(Cfg.tip_beznal);
				}
			}
		}
		final String[] list = new String[labels.size()];
		labels.copyInto(list);
		final Numeric idx = new Numeric().value(0);
		int margin = Auxiliary.tapSize * 1;
		Calendar today = Calendar.getInstance();
		today.setTime(NomenclatureBasedDocument.nextWorkingDate(today));
		final Numeric dateShip = new Numeric().value((double) today.getTime().getTime());
		//System.out.println("today " + today);
		//System.out.println("data " + data.dumpXML());
		SubLayoutless subLayoutless = new SubLayoutless(context);
		subLayoutless.child(new Decor(context).labelText.is("Доставка" ).labelAlignRightCenter().labelStyleMediumNormal()//
				.left().is(margin).top().is(margin)//
				.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
		);
		subLayoutless.child(new RedactDate(context).date.is(dateShip).format.is("dd.MM.yyyy" )//
				.left().is(Auxiliary.tapSize * 3.5 + margin).top().is(margin)//
				.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
		);
		subLayoutless.child(new Decor(context).labelText.is("Договор и оплата" ).labelAlignRightCenter().labelStyleMediumNormal()//
				.left().is(margin).top().is(margin + Auxiliary.tapSize * 1)//
				.width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize)//
		);
		RedactSingleChoice dogovory = new RedactSingleChoice(context);
		for (int i = 0; i < labels.size(); i++) {
			System.out.println(labels.get(i) + " : " + currentDogovorName + " : " + idx.value());
			dogovory.item(labels.get(i));
			if (labels.get(i).equals(currentDogovorName)) {
				idx.value(i);
			}
		}
		subLayoutless.child(dogovory.selection.is(idx)
				.left().is(Auxiliary.tapSize * 3.5 + margin).top().is(margin + Auxiliary.tapSize * 1)//
				.width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.8)//
		);
		subLayoutless.width().is(Auxiliary.tapSize * 8 + margin * 2).height().is(Auxiliary.tapSize * 6);
		Auxiliary.pick(context, "", subLayoutless, "Скопировать", new Task() {
			@Override
			public void doTask() {
				Intent intent = new Intent();
				intent.setClass(context, Activity_Bid.class);
				intent.putExtra("client_id", clientID);
				intent.putExtra("dogovor_idrref", dogovor_idrrefs.get(idx.value().intValue()));
				intent.putExtra("oplatanum", tips.get(idx.value().intValue()));
				intent.putExtra("dateShip", "" + dateShip.value());
				String raw = data.child("raw" ).dumpXML();
				intent.putExtra("raw_data", "" + raw);

				Activity_Bid.unLockCreateNewOrder();
				context.startActivity(intent);
			}
		}, null, null, null, null);
	}
/*
	void promptNewItem() {
		System.out.println("promptNewItem");
		SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
		//String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
		//String sql = "select hex(_idrref) as id from kontragenty where trim(naimenovanie)='" + currentKlientName + "';";
		String sql = "select hex(_idrref) as id from kontragenty where kod=" + currentKlientKod.trim() + ";";
		Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
		if (bb.child("row").child("id").value.property.value().trim().length() < 7) {
			Auxiliary.warn("Не найден контрагент " + currentKlientKod, context);
			return;
		}
		String clientID = "x'" + bb.child("row").child("id").value.property.value() + "'";
		Intent intent = new Intent();
		intent.setClass(context, sweetlife.android10.ui.Activity_Nomenclature.class);
		intent.putExtra(IExtras.CLIENT_ID, clientID);
		intent.putExtra(IExtras.ORDER_AMOUNT, 0);

		ClientInfo orderOwner = new ClientInfo(mDB, clientID);
		ApplicationHoreca.getInstance().setClientInfo(orderOwner);

		me.startActivityForResult(intent, me.NOMENKLATURA_NEW);
	}*/

	void requestItemsChange() {
		final Numeric it = new Numeric().value(-1);

		String[] labels = new String[orderItems.size()];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = orderItems.get(i).artikul//
					+ ": " + orderItems.get(i).naimenovanie//
					+ ", " + orderItems.get(i).kolichestvo//
					+ " по " + orderItems.get(i).cena + "р.";
			if (orderItems.get(i).min > 0 && orderItems.get(i).max > 0) {
				labels[i] = labels[i] + ", от " + orderItems.get(i).min + " до " + orderItems.get(i).max + "р.";
			}
			if (orderItems.get(i).poslednyaya.trim().length() > 0 && (!orderItems.get(i).poslednyaya.trim().equals("0" ))) {
				labels[i] = labels[i] + ", история " + orderItems.get(i).poslednyaya + "р.";
			}
		}
		String c = comment.value();
		if (c.trim().length() > 0) {
			c = " (" + c + ")";
		}
		Auxiliary.pickFilteredChoice(context, labels, it//
				, "Заказ " + documentNumber + " от " + documentDate + " на " + shipDate + c//
				, new Task() {
					@Override
					public void doTask() {
						promptItem(it.value().intValue());
					}
				}, "Сохранить", new Task() {
					@Override
					public void doTask() {
						requestSaveOrder();
					}
				}, "Комментарий", new Task() {
					@Override
					public void doTask() {
						promptComment();
					}
				}
				, null, null
		);
		/*
		Auxiliary.pickSingleChoice(context, labels, it//
				, "Заказ " + documentNumber + " от " + documentDate + " на " + shipDate + c//
				, new Task() {
					@Override
					public void doTask() {
						promptItem(it.value().intValue());
					}
				}, "Сохранить", new Task() {
					@Override
					public void doTask() {
						requestSaveOrder();
					}
				}, "Комментарий", new Task() {
					@Override
					public void doTask() {
						promptComment();
					}
				});
		*/
	}

	void promptItem(final int n) {
		final OrderItemInfo inf = orderItems.get(n);
		final Numeric nn = new Numeric();
		nn.value(inf.kolichestvo);
		final Numeric itemCena = new Numeric().value(inf.cena);
		final Numeric itemKolvo = new Numeric().value(inf.kolichestvo);
		final String description = "артикул " + inf.artikul + ": " + inf.naimenovanie + ", " + inf.cena + "руб";
		String minMax = "";
		if (inf.min > 0 && inf.max > 0) {
			minMax = " (от " + inf.min + " до " + inf.max + ")";
		}
		RedactNumber cena = new RedactNumber(context);
		cena.setEnabled(inf.min > 0 && inf.max > 0);
		String kolichestvoLabel = "Количество (" + inf.edizm + ", мин. " + inf.minNorma + ", в упаковке " + inf.koephphicient + ")";
		Auxiliary.pick(context, "", new SubLayoutless(context)//
						.child(new Decor(context).labelText.is(description).labelStyleMediumNormal().left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.5)
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 1))//
						.child(new Decor(context).labelText.is("Цена" + minMax).left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.5)
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 1))//
						.child(cena.number.is(itemCena).left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 2.0)
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new Decor(context).labelText.is(kolichestvoLabel).left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 3.0)
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactNumber(context).number.is(itemKolvo).left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 3.5)
								.width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 6)//
				, "Сохранить", new Task() {
					@Override
					public void doTask() {
						//System.out.println("save "+itemCena.value()+"/"+itemKolvo.value());
						inf.cena = itemCena.value();
						inf.kolichestvo = itemKolvo.value();
						requestItemsChange();
					}
				}, "Удалить", new Task() {
					@Override
					public void doTask() {
						orderItems.remove(inf);
						requestItemsChange();
					}
				}, "Фикс.цена", new Task() {
					@Override
					public void doTask() {
						promptFixCenaItem(n);
					}
				});

	}

	/*void promptFixCenaItem(int rowNum) {
		final Numeric n = new Numeric();
		final OrderItemInfo inf = orderItems.get(rowNum);
	}*/
	void promptFixCenaItem(int rowNum) {
		final Numeric n = new Numeric();
		final OrderItemInfo inf = orderItems.get(rowNum);
		System.out.println("promptFixCenaItem " + inf.artikul + ", currentKlientKod " + currentKlientKod);
		Auxiliary.pickNumber(context, "Добавить арт." + inf.artikul + " в заявку на фикс. цену", n, "Добавить", new Task() {
			public void doTask() {
				SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
				//String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				//String sql = "select hex(_idrref) as id from kontragenty where trim(naimenovanie)='" + currentKlientName + "';";
				String sql = "select hex(_idrref) as id from kontragenty where kod=" + currentKlientKod.trim() + ";";
				Bough bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				if (bb.child("row" ).child("id" ).value.property.value().trim().length() < 7) {
					Auxiliary.warn("Не найден контрагент " + currentKlientKod, context);
					return;
				}
				String clientID = "x'" + bb.child("row" ).child("id" ).value.property.value() + "'";
				sql = "select hex(_idrref) as id,naimenovanie as name from nomenklatura where artikul='" + inf.artikul + "';";
				bb = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
				String nomenklaturaID = "x'" + bb.child("row" ).child("id" ).value.property.value() + "'";
				String artikul = inf.artikul;
				String nname = bb.child("row" ).child("name" ).value.property.value();
				ZayavkaNaSkidki mZayavka = new ZayavkaNaSkidki(clientID, mDB);
				FixedPricesNomenclatureData mFixedPricesNomenclatureData = new FixedPricesNomenclatureData(mDB, mZayavka);
				//mFixedPricesNomenclatureData.newFixedPriceNomenclature(mFoodstaff.getNomenklaturaID(), mFoodstaff.getArtikul(), mFoodstaff.getNomenklaturaNaimenovanie());
				mFixedPricesNomenclatureData.newFixedPriceNomenclature(nomenklaturaID, artikul, nname);
				mFixedPricesNomenclatureData.getNomenclature(0).setCena(n.value());
				mFixedPricesNomenclatureData.WriteToDataBase(mDB);
				mZayavka.writeToDataBase(mDB);

				Intent intent = new Intent();
				intent.setClass(context, Activity_FixedPrices.class);
				intent.putExtra("client_id", clientID);
				intent.putExtra("is_editable", true);

				intent.putExtra("ZayavkaNaSkidki", mZayavka);

				context.startActivity(intent);
			}
		}, null, null);
	}


	void promptComment() {
		Auxiliary.pickText(context//
				, "Заказ " + documentNumber + " от " + documentDate + " на " + shipDate//
				, comment//
				, "Сохранить", new Task() {
					@Override
					public void doTask() {
						requestItemsChange();
					}
				});
	}

	void requestSaveOrder() {
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение..." ).task.is(new Task() {
			@Override
			public void doTask() {
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
						+ "\n<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
						+ "\n	<S:Body>"//
						+ "\n		<Change xmlns=\"http://ws.swl/ChangeOrders\">"//
						+ "\n			<Docs>"//
						+ "\n				<Head Namber=\"" + documentNumber + "\" Comment=\"" + comment.value() + "\" ThatDone=\"3\" Date=\"" + ActivityWebServicesReports.reformatDate(documentDate) + "\">"//
						+ "\n					<Polzov>" + hrc + "</Polzov>"//
						+ "\n					<DateOtgruz>" + ActivityWebServicesReports.reformatDate(shipDate) + "</DateOtgruz>"//
						+ "\n				</Head>"//
						+ "\n				<Table>"//
						;
				for (int i = 0; i < orderItems.size(); i++) {
					xml = xml + "\n					<Stroki Article=\"" + orderItems.get(i).artikul + "\" KolVo=\"" + orderItems.get(i).kolichestvo + "\">"//
							+ "\n						<Cena>" + orderItems.get(i).cena + "</Cena>"//
							+ "\n						<Min>0</Min>"//
							+ "\n						<Max>0</Max>"//
							+ "\n					</Stroki>"//
					;
				}
				xml = xml + "\n				</Table>"//
						+ "\n			</Docs>"//
						+ "\n		</Change>"//
						+ "\n	</S:Body>"//
						+ "\n</S:Envelope>";
				r.url.is(Settings.getInstance().getBaseURL() + "ChangeOfOrders.1cws" )//
						.xml.is(xml//
				);
				Report_Base.startPing();
				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				me.preReport.writeCurrentPage();
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("requestSaveOrder "+r.data.dumpXML());
				Bough result=new Bough();


				Bough response=r.data.child("soap:Body" ).child("m:ChangeResponse" ).child("m:return" );
				result.child("Сообщение").value.property.value(response.child("m:Message" ).value.property.value());
				result.child("Номер").value.property.value(response.child("m:Nomer" ).value.property.value());
				Vector<Bough> NePodtverzdeniePozicii=response.children("m:NePodtverzdeniePozicii" );
				for(int ii=0;ii<NePodtverzdeniePozicii.size();ii++){
					Bough one=new Bough().name.is("НеПодтвержденныеПозиции");
					result.children.add(one);
					one.child("Номенклатура").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:Nomenklatura").value.property.value());
					one.child("КоличествоЗаказано").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoZakazano").value.property.value());
					one.child("КоличествоДефицит").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoDeficit").value.property.value());
					one.child("КоличествоПодтверждено").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoPodtverzdeno").value.property.value());
					one.child("ДатаПоступления").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:DataPostupleniya").value.property.value());
				}
				Bough b=new Bough();
				b.child("ДанныеПоЗаказам").child("Заказы").children=result.children;
				/*
				if (r.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), context);
				} else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						Auxiliary.warn("Результат: " //
								+ r.data.child("soap:Body" )//
								.child("m:ChangeResponse" )//
								.child("m:return" )//
								.value.property.value(), context);
					} else {
						Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), context);
					}
				}
				*/
				Activity_UploadBids.buildDialogResult(context,"Сохранение заказа", b);
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).start(context);
	}

	void requestChangeOrderState(final String thatDone) {
		final RawSOAP r = new RawSOAP();

		new Expect().status.is("Выполнение..." ).task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is(Settings.getInstance().getBaseURL() + "ChangeOfOrders.1cws" )//
						.xml.is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
						+ "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
						+ "	<S:Body>"//
						+ "		<Change xmlns=\"http://ws.swl/ChangeOrders\">"//
						+ "			<Docs>"//
						+ "				<Head Namber=\"" + documentNumber + "\" Comment=\"\" ThatDone=\"" + thatDone + "\" Date=\"" + ActivityWebServicesReports.reformatDate(documentDate) + "\">"//
						+ "					<Polzov>" + hrc + "</Polzov>"//
						+ "					<DateOtgruz>" + ActivityWebServicesReports.reformatDate(shipDate) + "</DateOtgruz>"//
						+ "				</Head>"//
						+ "				<Table></Table>"//
						+ "			</Docs>"//
						+ "		</Change>"//
						+ "	</S:Body>"//
						+ "</S:Envelope>"//
				);
				Report_Base.startPing();
				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				me.preReport.writeCurrentPage();
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("requestChangeOrderState "+r.data.dumpXML());
				Bough result=new Bough();
				//result.child("Сообщение").value.property.value(r.data.child("Message" ).value.property.value());
				Bough response=r.data.child("soap:Body" ).child("m:ChangeResponse" ).child("m:return" );
				result.child("Сообщение").value.property.value(response.child("m:Message" ).value.property.value());
				result.child("Номер").value.property.value(response.child("m:Nomer" ).value.property.value());
				Vector<Bough> NePodtverzdeniePozicii=response.children("m:NePodtverzdeniePozicii" );
				for(int ii=0;ii<NePodtverzdeniePozicii.size();ii++){
					Bough one=new Bough().name.is("НеПодтвержденныеПозиции");
					result.children.add(one);
					one.child("Номенклатура").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:Nomenklatura").value.property.value());
					one.child("КоличествоЗаказано").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoZakazano").value.property.value());
					one.child("КоличествоДефицит").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoDeficit").value.property.value());
					one.child("КоличествоПодтверждено").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:KolichestvoPodtverzdeno").value.property.value());
					one.child("ДатаПоступления").value.property.value(NePodtverzdeniePozicii.get(ii).child("m:DataPostupleniya").value.property.value());
				}
				Bough b=new Bough();
				b.child("ДанныеПоЗаказам").child("Заказы").children=result.children;
				/*
				if (r.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), context);
				} else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						Auxiliary.warn("Результат: " //
								+ r.data.child("soap:Body" )//
								.child("m:ChangeResponse" )//
								.child("m:return" )//
								.value.property.value(), context);
					} else {
						Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), context);
					}
				}*/
				Activity_UploadBids.buildDialogResult(context,"Сохранение заказа", b);
				me.tapInstance2(me.preReport.getFolderKey(), me.preKey);
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).start(context);
	}
}

