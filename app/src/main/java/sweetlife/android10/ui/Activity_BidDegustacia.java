package sweetlife.android10.ui;

//import sweetlife.horeca.R.menu;

import java.util.Calendar;

import reactive.ui.*;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.consts.ITableColumnsNames;
//import sweetlife.horeca.reports.ActivityReports;
//import sweetlife.horeca.reports.KontragentInfo;
import sweetlife.android10.utils.DateTimeHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class Activity_BidDegustacia extends Activity {
	Layoutless layoutless;
	Numeric otgruzka = new Numeric().value(ActivityWebServicesReports.dateOnly(Calendar.getInstance(), 1));
	ColumnText columnName;
	ColumnText columnArtikul;
	ColumnText columnCena;
	ColumnText columnKolichestvo;
	ColumnText columnEdizm;
	ColumnText columnSumma;
	DataGrid grid;
	Note id = new Note();
	Note comment = new Note();
	Numeric status = new Numeric();
	MenuItem menuDelete;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id.value(extras.getString("_id"));
		}
		layoutless = new Layoutless(this);
		init();
		setContentView(layoutless);
		if (id.value().equals("")) {
			this.setTitle("Добавление дегустации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
		} else {
			if (status.value() > 0) {
				this.setTitle("Просмотр дегустации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
			} else {
				this.setTitle("Редактирование дегустации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuDelete = menu.add("Удалить заявку на дегустацию");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuDelete) {
			promptDelete();
			return true;
		}
		return false;
	}

	void promptDelete() {
		Auxiliary.pickConfirm(this, "Удаление заявки на дегустацию", "Удалить", new Task() {
			@Override
			public void doTask() {
				ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZayavkaNaDegustaciu where _id=" + id.value() + ";");
				ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZayavkaNaDegustaciuNomenklatura where _id=" + id.value() + ";");
				Activity_BidDegustacia.this.finish();
			}
		});
	}

	void init() {
		if (!id.value().equals("")) {
			String sql = "select comment,otgruzka,status from ZayavkaNaDegustaciu where _id=" + id.value();
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			comment.value(b.child("row").child("comment").value.property.value());
			otgruzka.value((double) DateTimeHelper.SQLDateToDate(b.child("row").child("otgruzka").value.property.value()).getTime());
			//toDate.value((double) DateTimeHelper.SQLDateToDate(b.child("row").child("toDate").value.property.value()).getTime());
			status.value(Numeric.string2double(b.child("row").child("status").value.property.value()));
		}
		columnArtikul = new ColumnText();
		columnName = new ColumnText();
		columnCena = new ColumnText();
		columnKolichestvo = new ColumnText();
		columnEdizm = new ColumnText();
		columnSumma = new ColumnText();
		int w = Auxiliary.screenWidth(this);
		columnArtikul.title.is("Артикул").width.is(w * 0.1);
		columnName.title.is("Номенклатура").width.is(w * 0.5);
		columnCena.title.is("Цена").width.is(w * 0.1);
		columnKolichestvo.title.is("Кол-во").width.is(w * 0.1);
		columnEdizm.title.is("Ед.изм").width.is(w * 0.1);
		columnSumma.title.is("Сумма").width.is(w * 0.1);
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("Дата ")//
				.width().is(100)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new RedactDate(this)//
				.format.is("dd.MM.yyyy")//
				.date.is(otgruzka).left().is(100)//
				.width().is(250)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("Комментарий ").left().is(350)//
				.width().is(200)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new RedactText(this)//
				.text.is(comment).left().is(550)//
				.width().is(900)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		//
		grid = new DataGrid(this);
		layoutless.child(grid//
				.columns(new Column[]{columnArtikul, columnName, columnCena, columnKolichestvo, columnEdizm, columnSumma})//
				.top().is(Auxiliary.tapSize * 0.8)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 2 * 0.8))//
		);
		//
		layoutless.child(new Knob(this)//
				.labelText.is("Номенклатура")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						doShowNomenklatureShooser();
					}
				})//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize * 0.8))//
				.width().is(250)//
				.height().is(Auxiliary.tapSize * 0.8)//
				.hidden().is(status.same(1))//
		);
		layoutless.child(new Knob(this)//
				.labelText.is("Выгрузить")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						doUpload();
					}
				})//
				.left().is(250)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize * 0.8))//
				.width().is(250)//
				.height().is(Auxiliary.tapSize * 0.8)//
				.hidden().is(status.same(1))//
		);
		layoutless.child(new Knob(this)//
				.labelText.is("Удалить")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						doDeleteSpecificacia();
					}
				})//
				.left().is(250 + 250)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize * 0.8))//
				.width().is(250)//
				.height().is(Auxiliary.tapSize * 0.8)//
				.hidden().is(id.same("").or(status.same(1)))//
		);
		layoutless.child(new Knob(this)//
				.labelText.is("Сохранить")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						doSave(true);
					}
				}).top().is(layoutless.height().property.minus(Auxiliary.tapSize * 0.8))//
				.left().is(layoutless.width().property.minus(250))//
				.width().is(250)//
				.height().is(Auxiliary.tapSize * 0.8)//
				.hidden().is(status.same(1))//
		);
		//Layoutless nn=layoutless;
		//nn.setFocusable(true);
		//nn.setFocusableInTouchMode(true);
		//nn.requestFocus();
		refreshItems();
	}

	void refreshItems() {
		columnArtikul.clear();
		columnName.clear();
		columnCena.clear();
		columnKolichestvo.clear();
		columnEdizm.clear();
		columnSumma.clear();
		if (!id.value().equals("")) {
			String sql = "select"//
					+ " ZayavkaNaDegustaciuNomenklatura._id as aid"//
					+ ",ZayavkaNaDegustaciuNomenklatura.nomenklatura as nomenklatura"//
					+ ",ZayavkaNaDegustaciuNomenklatura.kolichestvo as kolichestvo"//
					+ ",nomenklatura.naimenovanie as naimenovanie"//
					+ ",nomenklatura.artikul as artikul"//
					//+ ",EdinicyIzmereniya_strip.naimenovanie as edizm"//
					+ ",nomenklatura.skladEdIzm as edizm"//
					+ ",TekuschieCenyOstatkovPartiy.Cena as sebestoimost" //
					+ ",TekuschieCenyOstatkovPartiy.Cena * ZayavkaNaDegustaciuNomenklatura.kolichestvo as summa" //
					//+ " ,VelichinaKvantovNomenklatury.Kolichestvo as minNorma" //
					+ " ,nomenklatura.kvant as minNorma" //
					+ " from ZayavkaNaDegustaciuNomenklatura "//
					+ " join nomenklatura on nomenklatura._IDRRef=ZayavkaNaDegustaciuNomenklatura.nomenklatura"//
					+ " left join TekuschieCenyOstatkovPartiy on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura"//
					//+ " join EdinicyIzmereniya_strip on nomenklatura.EdinicaKhraneniyaOstatkov=EdinicyIzmereniya_strip._idrref"//
					//+ " join VelichinaKvantovNomenklatury_strip VelichinaKvantovNomenklatury on VelichinaKvantovNomenklatury.nomenklatura=nomenklatura.[_IDRRef] "// 
					+ " where ZayavkaNaDegustaciuNomenklatura.parent=" + id.value()//
					+ " order by nomenklatura.artikul"//
					;
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(sql);
			//System.out.println(b.dumpXML());
			for (int i = 0; i < b.children.size(); i++) {
				Bough row = b.children.get(i);
				//columnArtikul, columnName, columnOldCena, columnNewCena
				final String artikul = row.child("artikul").value.property.value();
				final String aid = row.child("aid").value.property.value();
				final String naimenovanie = row.child("naimenovanie").value.property.value();
				final String sebestoimost = row.child("sebestoimost").value.property.value();
				final String kolichestvo = row.child("kolichestvo").value.property.value();
				final String edizm = row.child("edizm").value.property.value();
				final String summa = row.child("summa").value.property.value();
				final String minNorma = row.child("minNorma").value.property.value();
				Task tap = new Task() {
					@Override
					public void doTask() {
						doShowEditArtikul(aid, kolichestvo, naimenovanie, minNorma);//Artikul, Naimenovanie, Numeric.string2double(old), Numeric.string2double(Oborot));
					}
				};
				if (status.value() > 0) {
					tap = null;
				}
				columnArtikul.cell(artikul, tap);
				columnName.cell(naimenovanie, tap);
				columnCena.cell(sebestoimost, tap);
				columnKolichestvo.cell(kolichestvo, tap);
				columnEdizm.cell(edizm, tap);
				columnSumma.cell(summa, tap);
			}
			grid.refresh();
		}
	}

	void doShowEditArtikul(final String aid, String kolichestvo, String naimenovanie, String minNorma) {
		double kol = Numeric.string2double(kolichestvo);
		final Numeric n = new Numeric().value(kol);
		final double step = Numeric.string2double(minNorma);
		SubLayoutless rake = new SubLayoutless(Activity_BidDegustacia.this);
		rake.width().is(Auxiliary.tapSize * 10);
		rake.height().property.value(Auxiliary.tapSize * 4);
		rake.child(new RedactNumber(this)//
				.number.is(n)//
				.width().is(Auxiliary.tapSize * 7)//
				.height().is(Auxiliary.tapSize)//
				.left().is(Auxiliary.tapSize * 0.5)//
		);
		rake.child(new Knob(this)//
				.labelText.is("+")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						n.value(n.value() + step);
					}
				})//
				.left().is(Auxiliary.tapSize * 7.5)//
				.width().is(Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		rake.child(new Knob(this)//
				.labelText.is("-")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {

						n.value(n.value() - step);
						if (n.value() < 0) {
							n.value(0);
						}
					}
				})//
				.left().is(Auxiliary.tapSize * 8.5)//
				.width().is(Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize)//
		);
		Auxiliary.pick(this, naimenovanie, rake, "Изменить", new Task() {
			@Override
			public void doTask() {
				String sql = "update ZayavkaNaDegustaciuNomenklatura set kolichestvo=" + n.value() + " where _id=" + aid;
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				refreshItems();
			}
		}, "Удалить", new Task() {
			@Override
			public void doTask() {
				doAskDeleteArtikul(aid);
			}
		}, null, null);
		/*
		Auxiliary.pickNumber(Activity_BidDegustacia.this, naimenovanie, n, "Изменить", new Task() {
			@Override
			public void doTask() {
				String sql = "update ZayavkaNaDegustaciuNomenklatura set kolichestvo=" + n.value() + " where _id=" + aid;
				System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				refreshItems();
			}
		}, "Удалить", new Task() {
			@Override
			public void doTask() {
				doAskDeleteArtikul(aid);
			}
		});
		*/
	}

	void doSave(boolean close) {
		long nn = -1;
		if (id.value().equals("")) {
			java.util.Date datyeFrom = new java.util.Date();
			datyeFrom.setTime(otgruzka.value().longValue());
			String sql = "insert into ZayavkaNaDegustaciu  (otgruzka,status,comment,kontragent) values ("//
					+ "\n'" + DateTimeHelper.SQLDateString(datyeFrom) + "'"//
					+ ", 0"//
					+ ", '" + comment.value().trim().replace('\'', '"').replace('\n', ' ') + "'"//
					+ ", " + ApplicationHoreca.getInstance().getClientInfo().getID().trim()//
					+ ")";
			//System.out.println(sql);
			SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
			nn = statement.executeInsert();
			id.value("" + nn);
			statement.close();
		} else {
			java.util.Date datyeFrom = new java.util.Date();
			datyeFrom.setTime(otgruzka.value().longValue());
			String sql = "update ZayavkaNaDegustaciu set "//
					+ " otgruzka='" + DateTimeHelper.SQLDateString(datyeFrom) + "'"//
					+ ", status=0"//
					+ ", comment='" + comment.value().trim().replace('\'', '"').replace('\n', ' ') + "'"//
					+ ", kontragent=" + ApplicationHoreca.getInstance().getClientInfo().getID().trim()//
					+ " where _id=" + id.value();
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}
		if (close) {
			this.finish();
		}
	}

	void doDropSpecFromDB() {
		String sql = "delete from ZayavkaNaDegustaciuNomenklatura where parent=" + id.value();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "delete from ZayavkaNaDegustaciu where _id=" + id.value();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}

	void doDeleteSpecificacia() {
		Auxiliary.pick3Choice(this, "Удаление дегустации", "Вы уверены?", "Удалить", new Task() {
			@Override
			public void doTask() {
				doDropSpecFromDB();
			}
		}, null, null, null, null);
		//System.out.println("doDeleteSpecificacia ");
	}

	void doUpload() {
		//System.out.println("doUpload ");
		doSave(false);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(otgruzka.value().longValue());
		int toY = calendar.get(Calendar.YEAR);
		int toM = calendar.get(Calendar.MONTH) + 1;
		int toD = calendar.get(Calendar.DAY_OF_MONTH);
		String sql = "select"//
				+ " ZayavkaNaDegustaciuNomenklatura._id as aid"//
				+ ",ZayavkaNaDegustaciuNomenklatura.nomenklatura as nomenklatura"//
				+ ",ZayavkaNaDegustaciuNomenklatura.kolichestvo as kolichestvo"//
				+ ",nomenklatura.naimenovanie as naimenovanie"//
				+ ",nomenklatura.artikul as artikul"//
				//+ ",EdinicyIzmereniya_strip.naimenovanie as edizm"//
				+ ",nomenklatura.skladEdIzm as edizm"//
				+ ",TekuschieCenyOstatkovPartiy.Cena as sebestoimost" //
				+ ",TekuschieCenyOstatkovPartiy.Cena * ZayavkaNaDegustaciuNomenklatura.kolichestvo as summa" //
				+ " from ZayavkaNaDegustaciuNomenklatura "//
				+ " join nomenklatura on nomenklatura._IDRRef=ZayavkaNaDegustaciuNomenklatura.nomenklatura"//
				+ " left join TekuschieCenyOstatkovPartiy on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura"//
				//+ " join EdinicyIzmereniya_strip on nomenklatura.EdinicaKhraneniyaOstatkov=EdinicyIzmereniya_strip._idrref"//
				+ " where ZayavkaNaDegustaciuNomenklatura.parent=" + id.value()//
				+ " order by nomenklatura.artikul"//
				;
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(sql);
		//System.out.println(b.dumpXML());
		//for (int i = 0; i < b.children.size(); i++) {}
		//http://89.109.7.162/Degustaciya.1cws?wsdl
		String artikul = "";
		boolean flag = false;
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/Degustaciya\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Get>"//
				+ "\n	      <ns1:Dok>"//
				+ "\n	        <ns1:Klient>" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "</ns1:Klient>"//
				+ "\n	        <ns1:Data>" + toY + ActivityWebServicesReports.pad2(toM) + ActivityWebServicesReports.pad2(toD) + "</ns1:Data>"//
				+ "\n	        <ns1:Tablica>"//
				;
		for (int i = 0; i < b.children.size(); i++) {
			Bough row = b.children.get(i);
			xml = xml + "\n	          <ns1:Str>"//
					+ "\n	            <ns1:Artikul>" + row.child("artikul").value.property.value() + "</ns1:Artikul>"//
					+ "\n	            <ns1:Kolvo>" + row.child("kolichestvo").value.property.value() + "</ns1:Kolvo>"//
					+ "\n	          </ns1:Str>"//
			;
			if (Numeric.string2double(row.child("kolichestvo").value.property.value()) <= 0) {
				flag = true;
				artikul = row.child("artikul").value.property.value();
				break;
			}
		}
		if (flag) {
			Auxiliary.warn("Для артикула " + artikul + " не указано количество.", this);
			return;
		}
		xml = xml + "\n	        </ns1:Tablica>"//
				+ "\n	        <ns1:Commentarii>" + comment.value() + "</ns1:Commentarii>"//
				+ "\n	      </ns1:Dok>"//
				+ "\n	      <ns1:Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</ns1:Otvetstvennii>"//
				+ "\n	    </ns1:Get>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>"//
		;
		System.out.println(xml);
		final String soapXML = xml;
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is(Settings.getInstance().getBaseURL() + "DegustaciyaNov.1cws")//
						//r.url.is(Settings.getInstance().getBaseURL() + "Degustaciyatest.1cws")//
						.xml.is(soapXML);
				Report_Base.startPing();

				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (r.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), Activity_BidDegustacia.this);
					r.exception.property.value().printStackTrace();
				} else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						System.out.println(r.data.dumpXML());
						String rez = r.data.child("soap:Body")//
								.child("m:GetResponse")//
								.child("m:return")//
								.value.property.value();

						if (rez.trim().toUpperCase().equals("OK")) {
							/*java.util.Date datyeFrom = new java.util.Date();
							datyeFrom.setTime(fromDate.value().longValue());
							java.util.Date dateTo = new java.util.Date();
							dateTo.setTime(toDate.value().longValue());*/
							String sql = "update ZayavkaNaDegustaciu set "//
									+ " status=1"//
									+ " where _id=" + id.value();
							//System.out.println(sql);
							ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
							//finish();
							Auxiliary.bye("Результат: " //
									+ rez, Activity_BidDegustacia.this);
						} else {
							Auxiliary.warn("Ошибка: " //
									+ rez, Activity_BidDegustacia.this);
						}
					} else {
						Auxiliary.inform("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), Activity_BidDegustacia.this);
					}
				}
				//refreshTask.start();
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//refreshTask.start();
			}
		}).start(this);
	}

	void doAskDeleteArtikul(final String aid) {
		//System.out.println("delete " + Artikul);
		Auxiliary.pick3Choice(this, "Удаление номенклатуры", "Вы уверены?", "Удалить", new Task() {
			@Override
			public void doTask() {
				doDropArtikulFromGrid(aid);
			}
		}, null, null, null, null);
	}

	void doDropArtikulFromGrid(String aid) {
		String sql = "delete from ZayavkaNaDegustaciuNomenklatura where _id=" + aid;
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		refreshItems();
	}

	void doReplaceArtikulInGrid(final String Artikul, final String Naimenovanie, final double newCena, final double whatOborot) {
		grid.refresh();
	}

	void doShowNomenklatureShooser() {
		//System.out.println("doSearch");
		Intent intent = new Intent();
		intent.setClass(this, Activity_Nomenclature.class);
		intent.putExtra(IExtras.CLIENT_ID, ApplicationHoreca.getInstance().getClientInfo().getID());//mBidData.getClientID());
		intent.putExtra(IExtras.ORDER_AMOUNT, 0.0);//mBidData.getBid().getSumma() + mBidData.getFoodStuffs().getAmount());
		intent.putExtra(IExtras.DEGUSTACIA_POISK, "1");
		startActivityForResult(intent, IExtras.ADD_NOMENCATURE);
	}

	void doInsertNewArtikul(String nomenklaturaID) {
		//System.out.println("doInsertNewArtikul " + nomenklaturaID);
		doSave(false);
		String sql = "insert into ZayavkaNaDegustaciuNomenklatura (parent,nomenklatura,kolichestvo) values ("//
				+ id.value()//
				+ "," + nomenklaturaID//
				+ ",0"//
				+ ")";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		refreshItems();
		//grid.refresh();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("doInsertNewArtikul "+RESULT_OK);
		if (resultCode == RESULT_OK) {
			doInsertNewArtikul(data.getStringExtra(ITableColumnsNames.NOMENCLATURE_ID));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Auxiliary.hideSoftKeyboard(this);
	}
}
