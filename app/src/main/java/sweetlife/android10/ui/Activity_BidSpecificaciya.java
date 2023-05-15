package sweetlife.android10.ui;

//import sweetlife.horeca.R.menu;

import android.app.*;

import java.util.Calendar;
import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
//import sweetlife.horeca.reports.ActivityReports;
//import sweetlife.horeca.reports.KontragentInfo;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import sweetlife.android10.supervisor.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class Activity_BidSpecificaciya extends Activity {
	Layoutless layoutless;
	//ApplicationHoreca mAppInstance;
	//Bough kontragentData;
	Numeric fromDate = new Numeric().value(ActivityWebServicesReports.dateOnly(Calendar.getInstance(), 0));
	Numeric toDate = new Numeric().value(ActivityWebServicesReports.dateOnly(Calendar.getInstance(), 30));
	Note comment = new Note().value("");
	Toggle CheckOZIR = new Toggle();
	//Vector<String>kontragents=new Vector<String>();
	Vector<Double> prices = new Vector<Double>();
	ColumnText columnName;
	ColumnText columnArtikul;
	ColumnText columnNewCena;
	ColumnText columnOldCena;
	ColumnText columnOborot;
	ColumnText columnIzm;
	ColumnText columnNacenka;
	ColumnText columnSebestoimost;
	DataGrid grid;
	public static boolean sortByName=false;
	//boolean isNew = true;
	Note id = new Note();
	Numeric status = new Numeric();
	MenuItem menuImport;
	MenuItem menuDelete;
	MenuItem menuClone;
	MenuItem menuSortMode;

	void doImport() {
		if (status.value() > 0) {
			Auxiliary.warn("Спецификация уже выгружена", this);
		}
		try {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			String pasteData = "";
			ClipData clipData = clipboard.getPrimaryClip();
			ClipData.Item item = clipData.getItemAt(0);
			pasteData = item.getText().toString();
			String[] t = pasteData.split("\n");
			java.util.Vector<java.util.Vector<String>> rows = new java.util.Vector<java.util.Vector<String>>();
			for (int i = 0; i < t.length; i++) {
				String[] r = t[i].split("\t");
				if (r != null) {
					java.util.Vector<String> row = new java.util.Vector<String>();
					for (int k = 0; k < r.length; k++) {
						row.add(r[k]);
					}
					rows.add(row);
				}
			}
			final String[] artikuls = new String[rows.size()];
			final Double[] cenas = new Double[rows.size()];
			CharSequence[] items = new String[rows.size()];
			final They<Integer> defaultSelection = new They<Integer>();
			String clientID = "0";
			String polzovatelID = "0";
			String dataOtgruzki = "0";
			String sklad = "0";
			try {
				clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
				dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
				sklad = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			} catch (Throwable ttt) {
				ttt.printStackTrace();
			}
			for (int i = 0; i < rows.size(); i++) {
				String artikul = "";
				String name = "";
				String cenaS = "";
				double cena = 1.0;
				if (rows.get(i).size() > 0) {
					artikul = rows.get(i).get(0);
				}
				if (rows.get(i).size() > 1) {
					name = rows.get(i).get(1);
				}
				if (rows.get(i).size() > 2) {
					cenaS = rows.get(i).get(2);
					try {
						cena = Double.parseDouble(cenaS);
					} catch (Throwable e) {
						//e.printStackTrace();
					}
				}
				artikuls[i] = artikul;
				cenas[i] = cena;
				items[i] = artikul + ": " + name + ": " + cena;
			}
			String custom = "1=0";
			for (int i = 0; i < artikuls.length; i++) {
				if (artikuls[i].trim().length() > 3) {
					custom = custom + " or n.[Artikul] = '" + artikuls[i] + "'";
				}
			}
			String sql = Request_NomenclatureBase.composeSQL(//
					dataOtgruzki//
					, clientID//
					, polzovatelID//
					, ""//
					, ""//
					, custom//
					, ISearchBy.SEARCH_CUSTOM//
					, false//
					, false//
					, sklad//
					, 200//
					, 0, false
					, false, false, null, null);
			//final Bough b = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
			final Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			for (int i = 0; i < artikuls.length; i++) {
				String a = artikuls[i].trim();
				for (int n = 0; n < b.children.size(); n++) {
					String f = b.children.get(n).child("Artikul").value.property.value().trim();
					if (f.equals(a)) {
						defaultSelection.insert(defaultSelection.size(), i);
						break;
					}
				}
			}
			Auxiliary.pickMultiChoice(this, items, defaultSelection, "ok", new Task() {
				@Override
				public void doTask() {
					doSave(false);
					for (int i = 0; i < artikuls.length; i++) {
						boolean checked = false;
						for (int ds = 0; ds < defaultSelection.size(); ds++) {
							if (defaultSelection.at(ds) == i) {
								checked = true;
								break;
							}
						}
						if (checked) {
							String a = artikuls[i].trim();
							for (int n = 0; n < b.children.size(); n++) {
								String f = b.children.get(n).child("Artikul").value.property.value().trim();
								if (a.equals(f)) {
									String nam = b.children.get(n).child("Naimenovanie").value.property.value().trim();
									String BasePrice = b.children.get(n).child("BasePrice").value.property.value().trim();
									double CENA = Numeric.string2double(b.children.get(n).child("Cena").value.property.value());
									double cen = cenas[i];
									//System.out.println(f+": "+nam+": "+BasePrice+": "+CENA+": "+cen);
									String sql = "insert into ZayavkaNaSpecifikasiaNomenklatura (parent,Artikul,Cena,oborot) values ("//
											+ id.value()//
											+ ", '" + f + "'"//
											+ ", " + cen + ""//
											+ ", " + 0 + ""//
											+ ")";
									//System.out.println(sql);
									//statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
									//nn = statement.executeInsert();
									//statement.close();
									ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
									break;
								}
							}
						}
					}

					refreshItems();
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
			Auxiliary.warn("Не скопированы данные для импорта", this);
		}
	}
void sortModeMenuTitle(){
	menuSortMode.setTitle(
			sortByName?"Сортировать по артикулу":"Сортировать по наименованию"
	);
}
	void toggleSortMode(){
		sortByName=!sortByName;
		menuSortMode.setTitle(
				sortByName?"Сортировать по артикулу":"Сортировать по наименованию"
		);
		refreshItems();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuImport = menu.add("Импорт");
		menuClone = menu.add("Копировать спецификацию");
		menuDelete = menu.add("Удалить спецификацию");
		menuSortMode=menu.add("Сортировать");
		sortModeMenuTitle();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuImport) {
			doImport();
			return true;
		}
		if (item == menuDelete) {
			promptDelete();
			return true;
		}
		if (item == menuClone) {
			promptClone();
			return true;
		}
		if (item == menuSortMode) {
			toggleSortMode();
			return true;
		}

		return false;
	}

	void doClone() {
		id.value("");
		fromDate.value(ActivityWebServicesReports.dateOnly(Calendar.getInstance(), 0));
		toDate.value(ActivityWebServicesReports.dateOnly(Calendar.getInstance(), 1));
		this.setTitle("Копирование спецификации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
		doSave(false);
		init();
	}

	void promptClone() {
		Auxiliary.pickConfirm(this, "Сделать копию спецификации", "Копировать", new Task() {
			@Override
			public void doTask() {
				doClone();
			}
		});
	}

	void promptDelete() {
		Auxiliary.pickConfirm(this, "Удаление спецификации", "Удалить", new Task() {
			@Override
			public void doTask() {
				ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZayavkaNaSpecifikasia where _id=" + id.value() + ";");
				ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZayavkaNaSpecifikasiaNomenklatura where parent=" + id.value() + ";");
				Activity_BidSpecificaciya.this.finish();
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity__bid_specificaciya);
		//mAppInstance = ApplicationHoreca.getInstance();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id.value(extras.getString("_id"));
		}
		layoutless = new Layoutless(this);
		init();
		setContentView(layoutless);
		if (id.value().equals("")) {
			this.setTitle("Добавление спецификации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
		} else {
			if (status.value() > 0) {
				this.setTitle("Просмотр спецификации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
			} else {
				this.setTitle("Редактирование спецификации (" + ApplicationHoreca.getInstance().getClientInfo().getName() + ")");
			}
		}
	}

	void init() {
		if (!id.value().equals("")) {
			String sql = "select createDate,fromDate,toDate,status,comment,hrc,kod from ZayavkaNaSpecifikasia where _id=" + id.value();
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));

			comment.value(b.child("row").child("comment").value.property.value());
			String parts[] = comment.value().split("~");

			if (parts.length > 1) {
				CheckOZIR.value(parts[0].equals("y") ? true : false);
				comment.value(parts[1]);
			}

			fromDate.value((double) DateTimeHelper.SQLDateToDate(b.child("row").child("fromDate").value.property.value()).getTime());
			toDate.value((double) DateTimeHelper.SQLDateToDate(b.child("row").child("toDate").value.property.value()).getTime());
			status.value(Numeric.string2double(b.child("row").child("status").value.property.value()));
		}
		/*
		kontragentData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(//
				"select"//
						+ "\n 		Kontragenty._idrref as _IDRRef"//
						+ "\n		,Kontragenty.kod as Kod"//
						+ "\n		,Kontragenty.Naimenovanie as Naimenovanie"//
						+ "\n	from MarshrutyAgentov"//
						+ "\n		join Kontragenty on Kontragenty._idrref=MarshrutyAgentov.Kontragent"//
						+ "\n	group by Kontragenty.kod"//
						+ "\n	order by Kontragenty.Naimenovanie"//
						+ "\n	limit 999;", null));
		final RedactMultiChoice kontragents = new RedactMultiChoice(this);
		kontragents.setTextSize(16);
		//kontragents.item("[Все контрагенты]");
		for (int i = 0; i < kontragentData.children.size(); i++) {
			Bough row = kontragentData.children.get(i);
			kontragents.item(row.child("Naimenovanie").value.property.value());
		}*/
		columnArtikul = new ColumnText();

		columnName = new ColumnText();
		columnOldCena = new ColumnText();
		columnNewCena = new ColumnText();
		columnOborot = new ColumnText();
		columnIzm = new ColumnText();
		columnNacenka = new ColumnText();
		columnSebestoimost = new ColumnText();
		int w = Auxiliary.screenWidth(this);
		columnArtikul.title.is("Артикул").width.is(w * 0.06);
		columnName.title.is("Номенклатура").width.is(w * 0.52);
		columnOldCena.title.is("Цена действующая").width.is(w * 0.08);
		columnOborot.title.is("Обяз-во по товар-ту").width.is(w * 0.06);
		columnNewCena.title.is("Цена новая").width.is(w * 0.08);
		columnIzm.title.is("Изм., %").width.is(w * 0.06);
		columnNacenka.title.is("Наценка, % план").width.is(w * 0.06);
		columnSebestoimost.title.is("Себестоимость").width.is(w * 0.08);
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("Период с ")//
				.width().is(100)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new RedactDate(this)//
				.format.is("dd.MM.yyyy")//
				.date.is(fromDate)
				.left().is(100)//
				.width().is(200)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("по ")//
				.left().is(100 + 200)
				//.top().is(Layoutless.tapSize * 0.8)//
				.width().is(50)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new RedactDate(this)//
				.format.is("dd.MM.yyyy")//
				.date.is(toDate)//
				.left().is(100 + 200 + 50)//
				//.top().is(Layoutless.tapSize * 0.8)//
				.width().is(200)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		layoutless.child(new RedactToggle(this).yes.is(CheckOZIR).labelText.is("отдать на проверку в ОЗИР")//
				.left().is(100 + 200 + 50 + 200)//
				.width().is(600)//
				//.top().is(Auxiliary.tapSize * 0.8)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		/*
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("Контрагенты ")//
						.left().is(100 + 200)//
						.width().is(100)//
						.height().is(Layoutless.tapSize * 0.8)//
				);
		
		layoutless.child(kontragents//
				//kontragents
				.left().is(100 + 200 + 100)//
				.width().is(layoutless.width().property.minus(100 + 200 + 100))//
				.height().is(Layoutless.tapSize * 0.8)//
				);
		*/
		layoutless.child(new Decor(this)//
				.labelAlignRightCenter()//
				.labelText.is("Комментарий ")//
				.left().is(0)//100 + 200 + 50 + 200)//
				.width().is(180)//
				.top().is(Auxiliary.tapSize * 0.8)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		//RedactText c = new RedactText(this);
		//c.setFocusable(false);
		//c.setFocusableInTouchMode(true);
		layoutless.child(new RedactText(this)//
				.text.is(comment)//
				.left().is(180)//100 + 200 + 50 + 200 + 180)//
				.top().is(Auxiliary.tapSize * 0.8)//
				.width().is(layoutless.width().property.minus(180))//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		//
		grid = new DataGrid(this);
		layoutless.child(grid//
				.columns(new Column[]{columnArtikul, columnName, columnSebestoimost, columnOldCena, columnOborot, columnNewCena, columnIzm, columnNacenka})//
				.top().is(Auxiliary.tapSize * 2 * 0.8)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 3 * 0.8))//
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
		if (!id.value().equals("")) {
			String sql = "select"//
					+ " ZayavkaNaSpecifikasiaNomenklatura.Artikul as Artikul"//
					+ ",ZayavkaNaSpecifikasiaNomenklatura.Cena as newCena"//
					+ ",ZayavkaNaSpecifikasiaNomenklatura.oborot as Oborot"//
					+ ",nomenklatura.naimenovanie as Naimenovanie"//
					+ ",CenyNomenklaturySklada.Cena as oldCena" //
					+ ",TekuschieCenyOstatkovPartiy.Cena as Sebestoimost" //
					+ " from ZayavkaNaSpecifikasiaNomenklatura"//
					+ " join nomenklatura on nomenklatura.artikul=ZayavkaNaSpecifikasiaNomenklatura.Artikul"//
					+ " join CenyNomenklaturySklada on nomenklatura._idrref=CenyNomenklaturySklada.nomenklatura and CenyNomenklaturySklada.period=(select max(Period) from CenyNomenklaturySklada where nomenklatura=nomenklatura._idrref)"//
					+ " join TekuschieCenyOstatkovPartiy on nomenklatura._idrref=TekuschieCenyOstatkovPartiy.nomenklatura" + " where ZayavkaNaSpecifikasiaNomenklatura.parent=" + id.value()//
					;
			if(sortByName) {
				sql = sql + " order by nomenklatura.naimenovanie";
			}else{
				sql = sql + " order by ZayavkaNaSpecifikasiaNomenklatura.Artikul";
			}

			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(sql);
			//System.out.println(b.dumpXML());
			grid.clearColumns();
			for (int i = 0; i < b.children.size(); i++) {
				Bough row = b.children.get(i);
				//columnArtikul, columnName, columnOldCena, columnNewCena
				final String Artikul = row.child("Artikul").value.property.value();
				final String Naimenovanie = row.child("Naimenovanie").value.property.value();
				final String old = row.child("oldCena").value.property.value();
				final String newCena = row.child("newCena").value.property.value();
				final String Oborot = row.child("Oborot").value.property.value();
				double Sebestoimost = Numeric.string2double(row.child("Sebestoimost").value.property.value());
				double dNewCena = Numeric.string2double(newCena);
				double dOldCena = Numeric.string2double(old);
				final int izm = (int) (100 * (dNewCena - dOldCena) / dOldCena);
				final int nac = (int) (100 * (dNewCena - Sebestoimost) / Sebestoimost);
				Task tap = new Task() {
					@Override
					public void doTask() {
						doShowEditArtikul(Artikul, Naimenovanie, Numeric.string2double(old), Numeric.string2double(Oborot));
					}
				};
				columnArtikul.cell(Artikul, tap);
				columnName.cell(Naimenovanie, tap);
				columnOldCena.cell(old, tap);
				columnOborot.cell(Oborot, tap);
				columnNewCena.cell(newCena, tap);
				columnIzm.cell(DecimalFormatHelper.format(izm), tap);
				columnNacenka.cell(DecimalFormatHelper.format(nac), tap);
				columnSebestoimost.cell("" + Sebestoimost, tap);
			}
			grid.refresh();
		}
	}

	void doSave(boolean close) {
		//System.out.println("doSave "+close);
		long nn = -1;
		if (id.value().equals("")) {
			java.util.Date datyeFrom = new java.util.Date();
			datyeFrom.setTime(fromDate.value().longValue());
			java.util.Date dateTo = new java.util.Date();
			dateTo.setTime(toDate.value().longValue());
			String sql = "insert into ZayavkaNaSpecifikasia (createDate,fromDate,toDate,status,comment,hrc,kod) values ("//
					+ "'" + DateTimeHelper.SQLDateString(new java.util.Date()) + "'"//
					+ ", '" + DateTimeHelper.SQLDateString(datyeFrom) + "'"//
					+ ", '" + DateTimeHelper.SQLDateString(dateTo) + "'"//
					+ ", 0"//
					+ ", '" + comment.value().trim().replace('\'', '"').replace('\n', ' ') + "'"//
					+ ", '" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "'"//
					+ ", '" + ApplicationHoreca.getInstance().getClientInfo().getKod().trim() + "'"//
					+ ")";
			//System.out.println(sql);
			SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
			nn = statement.executeInsert();
			id.value("" + nn);
			statement.close();
			for (int i = 0; i < columnArtikul.count(); i++) {
				double newCena = Numeric.string2double(columnNewCena.strings.get(i));
				double oborot = Numeric.string2double(columnOborot.strings.get(i));
				sql = "insert into ZayavkaNaSpecifikasiaNomenklatura (parent,Artikul,Cena,oborot) values ("//
						+ nn//
						+ ", '" + columnArtikul.strings.get(i) + "'"//
						+ ", " + newCena + ""//
						+ ", " + oborot + ""//
						+ ")";
				//System.out.println(sql);
				//statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
				//nn = statement.executeInsert();
				//statement.close();
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
			//System.out.println("doSave " + nn);
		} else {
			java.util.Date datyeFrom = new java.util.Date();
			datyeFrom.setTime(fromDate.value().longValue());
			java.util.Date dateTo = new java.util.Date();
			dateTo.setTime(toDate.value().longValue());
			String sql = "update ZayavkaNaSpecifikasia set "//
					+ "createDate='" + DateTimeHelper.SQLDateString(new java.util.Date()) + "'"//
					+ ", fromDate='" + DateTimeHelper.SQLDateString(datyeFrom) + "'"//
					+ ", toDate='" + DateTimeHelper.SQLDateString(dateTo) + "'"//
					+ ", status=0"//
					+ ", comment='" + (CheckOZIR.value() ? "y~" : "~") + comment.value().trim().replace('\'', '"').replace('\n', ' ') + "'"//
					+ ", hrc='" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "'"//
					+ ", kod='" + ApplicationHoreca.getInstance().getClientInfo().getKod().trim() + "'"//
					+ " where _id=" + id.value();
			//System.out.println(sql);
			//SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
			//statement.executeInsert();
			//statement.close();
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "delete from ZayavkaNaSpecifikasiaNomenklatura where parent=" + id.value();
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			for (int i = 0; i < columnArtikul.count(); i++) {
				double newCena = Numeric.string2double(columnNewCena.strings.get(i));
				double oborot = Numeric.string2double(columnOborot.strings.get(i));
				sql = "insert into ZayavkaNaSpecifikasiaNomenklatura (parent,Artikul,Cena,oborot) values ("//
						+ id.value()//
						+ ", '" + columnArtikul.strings.get(i) + "'"//
						+ ", " + newCena + ""//
						+ ", " + oborot + ""//
						+ ")";
				//System.out.println(sql);
				// statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
				//long nn = statement.executeInsert();
				//id.value(""+nn);
				//statement.close();
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		}
		if (close) {
			this.finish();
		}
		//return nn;
	}

	void doDropSpecFromDB() {
		String sql = "delete from ZayavkaNaSpecifikasiaNomenklatura where parent=" + id.value();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "delete from ZayavkaNaSpecifikasia where _id=" + id.value();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}

	void doDeleteSpecificacia() {
		Auxiliary.pick3Choice(this, "Удаление спецификации", "Вы уверены?", "Удалить", new Task() {
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
		/*int dateY = 2000;
		int dateM = 1;
		int dateD = 22;*/
		/*
		String txml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "\n	<soap:Envelope"//
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""//
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""//
				+ " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n		<soap:Body>"//
				+ "\n			<UploadRequest xmlns=\"http://ws.swl/RequestForSpecific\">"//
				+ "\n				<Header>"//
				+ "\n					<Number>1</Number>"//
				+ "\n					<Date>"+ dateY + "-" + ActivityReports.pad2(dateM) + "-" + ActivityReports.pad2(dateD) + "T00:00:00</Date>"//
				+ "\n					<User>"+ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim()+"</User>"//
				+ "\n					<Comment>"+comment.value().trim().replace('\'', '"').replace('\n', ' ').replace('>', '-').replace('<', ' ')+"</Comment>"//
				+ "\n					<DateBegin>"+ dateY + "-" + ActivityReports.pad2(dateM) + "-" + ActivityReports.pad2(dateD) + "T00:00:00</DateBegin>"//
				+ "\n					<DateEnd>"+ dateY + "-" + ActivityReports.pad2(dateM) + "-" + ActivityReports.pad2(dateD) + "T00:00:00</DateEnd>"//
				+ "\n				</Header>"//
				+ "\n				<tpFixPrice>";
		for (int i = 0; i < columnArtikul.count(); i++) {
			double newCena = Numeric.string2double(columnNewCena.strings.get(i));
			double oborot = Numeric.string2double(columnOborot.strings.get(i));
			txml = txml + "\n					<tpString>"//
					+ "\n						<Article>" + columnArtikul.strings.get(i) + "</Article>"//
					+ "\n						<Price>" + newCena + "</Price>"//
					+ "\n						<Ob>" + oborot + "</Ob>"//
					+ "\n					</tpString>";
		}
		txml = txml + "\n				</tpFixPrice>"//
				+ "\n				<tpKlient>"//
				+ "\n					<Str>"//
				+ "\n						<Kod>" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "</Kod>"//
				+ "\n						<Type>0</Type>"//
				+ "\n					</Str>"//
				+ "\n				</tpKlient>"//
				+ "\n			</UploadRequest>"//
				+ "\n		</soap:Body>"//
				+ "\n	</soap:Envelope>"//
		;
		*/
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(fromDate.value().longValue());
		int fromY = calendar.get(Calendar.YEAR);
		int fromM = calendar.get(Calendar.MONTH) + 1;
		int fromD = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(toDate.value().longValue());
		int toY = calendar.get(Calendar.YEAR);
		int toM = calendar.get(Calendar.MONTH) + 1;
		int toD = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = Calendar.getInstance();
		int nowY = calendar.get(Calendar.YEAR);
		int nowM = calendar.get(Calendar.MONTH) + 1;
		int nowD = calendar.get(Calendar.DAY_OF_MONTH);
		String txml = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:req=\"http://ws.swl/RequestForSpecific\">"//
				+ "\n <soap:Header/>"//
				+ "\n <soap:Body>"//
				+ "\n <req:UploadRequest>"//
				+ "\n <req:Document>"//
				+ "\n 	<req:Header>"//
				+ "\n 		<req:Number>1</req:Number>"//
				+ "\n 		<req:Date>" + nowY + "-" + ActivityWebServicesReports.pad2(nowM) + "-" + ActivityWebServicesReports.pad2(nowD) + "T00:00:00</req:Date>"//
				+ "\n 		<req:User>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</req:User>"//
				+ "\n 		<req:Comment>" + comment.value().trim().replace('\'', '"').replace('\n', ' ').replace('>', '-').replace('<', ' ') + "</req:Comment>"//
				+ "\n 		<req:DateBegin>" + fromY + "-" + ActivityWebServicesReports.pad2(fromM) + "-" + ActivityWebServicesReports.pad2(fromD) + "T00:00:00</req:DateBegin>"//
				+ "\n 		<req:DateEnd>" + toY + "-" + ActivityWebServicesReports.pad2(toM) + "-" + ActivityWebServicesReports.pad2(toD) + "T00:00:00</req:DateEnd>"//
				+ "\n 		<req:CheckOZIR>" + (CheckOZIR.value() ? "true" : "false") + "</req:CheckOZIR>"//

				+ "\n 	</req:Header>"//
				+ "\n 	<req:tpFixPrice>";
		for (int i = 0; i < columnArtikul.count(); i++) {
			double newCena = Numeric.string2double(columnNewCena.strings.get(i));
			double oborot = Numeric.string2double(columnOborot.strings.get(i));
			txml = txml + "\n 		<req:tpString>"//
					+ "\n 			<req:Article>" + columnArtikul.strings.get(i) + "</req:Article>"//
					+ "\n 			<req:Price>" + newCena + "</req:Price>"//
					+ "\n 			<req:Ob>" + oborot + "</req:Ob>"//
					+ "\n 		</req:tpString>";
		}
		txml = txml + "\n 	</req:tpFixPrice>"//
				+ "\n 	<req:tpKlient>"//
				+ "\n 			<req:Str>"//
				+ "\n 				<req:Kod>" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "</req:Kod>"//
				+ "\n 				<req:Type>1</req:Type>"//
				+ "\n 			</req:Str>"//
				+ "\n 	</req:tpKlient>"//
				+ "\n </req:Document>"//
				+ "\n </req:UploadRequest>"//
				+ "\n </soap:Body>"//
				+ "\n </soap:Envelope>";
		System.out.println(txml);
		final String soapXML = txml;
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is(Settings.getInstance().getBaseURL() + "RequestForSpecific.1cws")//
						//r.url.is(Settings.getInstance().getBaseURL()+"RequestForSpecifictest.1cws")//
						.xml.is(soapXML);
				Report_Base.startPing();

				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("RequestForSpecific.1cws " + r.rawResponse);
				if (r.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + r.exception.property.value().getMessage(), Activity_BidSpecificaciya.this);
					r.exception.property.value().printStackTrace();

				} else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						String rez = r.data.child("soap:Body")//
								.child("m:UploadRequestResponse")//
								.child("m:return")//
								.value.property.value();
						Auxiliary.warn("Результат: " //
								+ rez, Activity_BidSpecificaciya.this);
						if (rez.trim().equals("Выполнено")) {
							java.util.Date datyeFrom = new java.util.Date();
							datyeFrom.setTime(fromDate.value().longValue());
							java.util.Date dateTo = new java.util.Date();
							dateTo.setTime(toDate.value().longValue());
							String sql = "update ZayavkaNaSpecifikasia set "//
									+ " status=1"//
									+ " where _id=" + id.value();
							//System.out.println(sql);
							ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
							finish();
						}
					} else {
						Auxiliary.inform("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), Activity_BidSpecificaciya.this);
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

	void doAskDeleteArtikul(final String Artikul) {
		//System.out.println("delete " + Artikul);
		Auxiliary.pick3Choice(this, "Удаление номенклатуры", "Вы уверены?", "Удалить", new Task() {
			@Override
			public void doTask() {
				doDropArtikulFromGrid(Artikul);
			}
		}, null, null, null, null);
	}

	void doDropArtikulFromGrid(String Artikul) {
		for (int i = 0; i < columnArtikul.count(); i++) {
			if (columnArtikul.strings.get(i).equals(Artikul)) {
				int nn = i;
				columnArtikul.strings.remove(nn);
				columnName.strings.remove(nn);
				columnOldCena.strings.remove(nn);
				columnOborot.strings.remove(nn);
				columnNewCena.strings.remove(nn);
				columnIzm.strings.remove(nn);
				columnNacenka.strings.remove(nn);
				columnSebestoimost.strings.remove(nn);
				break;
			}
		}
		grid.refresh();
	}

	void doReplaceArtikulInGrid(final String Artikul, final String Naimenovanie, final double newCena, final double whatOborot) {
		//System.out.println("change " + Artikul + " to " + newCena);
		Task tap = new Task() {
			@Override
			public void doTask() {
				doShowEditArtikul(Artikul, Naimenovanie, newCena, whatOborot);
			}
		};
		for (int i = 0; i < columnArtikul.count(); i++) {
			if (columnArtikul.strings.get(i).equals(Artikul)) {
				Calendar nxt = Calendar.getInstance();
				nxt.roll(Calendar.DAY_OF_MONTH, 2);
				String sql = Request_NomenclatureBase.composeSQL(//
						DateTimeHelper.SQLDateString(nxt.getTime())//
						, ApplicationHoreca.getInstance().getClientInfo().getID()//
						, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
						, DateTimeHelper.SQLDateString(new java.util.Date())//
						, DateTimeHelper.SQLDateString(new java.util.Date())//
						, Artikul //
						, ISearchBy.SEARCH_ARTICLE//
						, false//
						, false//
						, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
						, 1//
						, 0, false
						, false, false, null, null);
				//System.out.println(sql);
				Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				//System.out.println(b.dumpXML());
				double sebestoimost = Numeric.string2double(b.child("row").child("BasePrice").value.property.value());
				double cena = Numeric.string2double(b.child("row").child("Cena").value.property.value());
				final double izm = (100 * (newCena - cena) / cena);
				final double nac = (100 * (newCena - sebestoimost) / sebestoimost);
				columnName.strings.set(i, "" + Naimenovanie);
				columnArtikul.strings.set(i, "" + Artikul);
				columnNewCena.strings.set(i, "" + newCena);
				columnOldCena.strings.set(i, "" + cena);
				columnOborot.strings.set(i, "" + whatOborot);
				columnIzm.strings.set(i, DecimalFormatHelper.format(izm));
				columnNacenka.strings.set(i, DecimalFormatHelper.format(nac));
				columnSebestoimost.strings.set(i, "" + sebestoimost);
				/*double Sebestoimost = Numeric.string2double(row.child("Sebestoimost").value.property.value());
				double dOldCena= Numeric.string2double(old);
				final int izm=(int)(100*(newCena-dOldCena)/dOldCena);
				final int nac=(int)(100*(newCena-Sebestoimost)/Sebestoimost);
				*/
				columnName.tasks.set(i, tap);
				columnArtikul.tasks.set(i, tap);
				columnNewCena.tasks.set(i, tap);
				columnOldCena.tasks.set(i, tap);
				columnOborot.tasks.set(i, tap);
				columnIzm.tasks.set(i, tap);
				columnNacenka.tasks.set(i, tap);
				columnSebestoimost.tasks.set(i, tap);
			}
		}
		grid.refresh();
	}

	void doShowEditArtikul(final String Artikul, final String Naimenovanie, final double nwcena, final double oborot) {
		if (status.value() > 0) {
			return;
		}
		//Auxiliary.pick3Choice(this, "", message, positiveButtonTitle, callbackPositiveBtn, neutralButtonTitle, callbackNeutralBtn, negativeButtonTitle, callbackNegativeBtn)
		final Numeric whatPrice = new Numeric().value(nwcena);
		final Numeric whatOborot = new Numeric().value(oborot);
		Auxiliary.pick(this, Artikul + ": " + Naimenovanie, new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("Цена новая").labelAlignRightCenter().top().is(0).width().is(Auxiliary.tapSize * 4.5).height().is(Auxiliary.tapSize))//
						.child(new RedactNumber(this).number.is(whatPrice).left().is(Auxiliary.tapSize * 5).top().is(0).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize))//
						.child(new Decor(this).labelText.is("Обяз-во по товарообороту").labelAlignRightCenter().top().is(Auxiliary.tapSize * 1).width().is(Auxiliary.tapSize * 4.5).height().is(Auxiliary.tapSize))//
						.child(new RedactNumber(this).number.is(whatOborot).left().is(Auxiliary.tapSize * 5).top().is(Auxiliary.tapSize * 1).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 10)//
						.height().is(Auxiliary.tapSize * 6)//
				, "Изменить", new Task() {
					@Override
					public void doTask() {
						doReplaceArtikulInGrid(Artikul, Naimenovanie, whatPrice.value(), whatOborot.value());
						doSave(false);
					}
				}, "Удалить", new Task() {
					@Override
					public void doTask() {
						doAskDeleteArtikul(Artikul);
					}
				}, null, null);
		/*
		Auxiliary.pickNumber(this, Artikul + ": " + Naimenovanie, whatPrice, "Изменить", new Task() {
			@Override
			public void doTask() {
				doReplaceArtikulInGrid(Artikul, whatPrice.value());
			}
		}, "Удалить", new Task() {
			@Override
			public void doTask() {
				doAskDeleteArtikul(Artikul);
			}
		});
		*/
	}

	void doShowNomenklatureShooser() {
		System.out.println("doShowNomenklatureShooser " + ApplicationHoreca.getInstance().getClientInfo().getName());
		Intent intent = new Intent();
		intent.setClass(this, Activity_Nomenclature.class);
		intent.putExtra(IExtras.CLIENT_ID, ApplicationHoreca.getInstance().getClientInfo().getID());//mBidData.getClientID());
		intent.putExtra(IExtras.ORDER_AMOUNT, 0.0);//mBidData.getBid().getSumma() + mBidData.getFoodStuffs().getAmount());
		startActivityForResult(intent, IExtras.ADD_NOMENCATURE);
	}

	void doInsertNewArtikul(String nomenklaturaID) {
		System.out.println("doInsertNewArtikul " + nomenklaturaID);
		//System.out.println("doSearch " + data.getStringExtra(ITableColumnsNames.NOMENCLATURE_ID));
		Calendar nxt = Calendar.getInstance();
		nxt.roll(Calendar.DAY_OF_MONTH, 2);
		String sql = Request_NomenclatureBase.composeSQL(//
				DateTimeHelper.SQLDateString(nxt.getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, DateTimeHelper.SQLDateString(new java.util.Date())//
				, DateTimeHelper.SQLDateString(new java.util.Date())//
				, nomenklaturaID //
				, ISearchBy.SEARCH_IDRREF//
				, false//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
				, 1//
				, 0, false, false, false, null, null);
		//System.out.println(sql);
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(b.dumpXML());
		final String Naimenovanie = b.child("row").child("Naimenovanie").value.property.value();
		final String Artikul = b.child("row").child("Artikul").value.property.value();
		final double old = Numeric.string2double(b.child("row").child("Cena").value.property.value());
		final double basePrice = Numeric.string2double(b.child("row").child("BasePrice").value.property.value());
		//final String Oborot = b.child("row").child("Oborot").value.property.value();
		//final Numeric nn = new Numeric().value(Numeric.string2double(old));
		//final double nn=Numeric.string2double(old);
		boolean found = false;
		for (int i = 0; i < columnArtikul.count(); i++) {
			if (columnArtikul.strings.get(i).equals(Artikul)) {
				found = true;
				break;
			}
		}
		if (found) {
			Auxiliary.warn("Номенклатура уже есть в списке.", this);
		} else {
			Task tap = new Task() {
				@Override
				public void doTask() {
					doShowEditArtikul(Artikul, Naimenovanie, old, 0);
				}
			};
			columnArtikul.cell(Artikul, tap);
			columnName.cell(Naimenovanie, tap);
			columnOldCena.cell("" + old, tap);
			columnNewCena.cell("" + old, tap);
			columnOborot.cell("0", tap);
			columnIzm.cell("0", tap);
			columnNacenka.cell("0", tap);
			columnSebestoimost.cell("0", tap);
			grid.refresh();
			doShowEditArtikul(Artikul, Naimenovanie, old, 0);
			/*
			Auxiliary.pickNumber(this, Artikul + ": " + Naimenovanie, nn, "Добавить", new Task() {
				@Override
				public void doTask() {
					//System.out.println("add " + nn.value());
					Task tap = new Task() {
						@Override
						public void doTask() {
							doShowEditArtikul(Artikul, Naimenovanie, nn.value(), old, "");
						}
					};
					columnArtikul.cell(Artikul, tap);
					columnName.cell(Naimenovanie, tap);
					columnOldCena.cell(old, tap);
					columnNewCena.cell("" + nn.value(), tap);
					columnOborot.cell("", tap);
					grid.flip();
				}
			}, null, null);
			*/
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String s = data.getStringExtra(ITableColumnsNames.NOMENCLATURE_ID);
			System.out.println("onActivityResult RESULT_OK " + s);
			doInsertNewArtikul(s);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Auxiliary.hideSoftKeyboard(this);
	}
}
