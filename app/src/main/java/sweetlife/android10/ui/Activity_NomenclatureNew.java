package sweetlife.android10.ui;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.impl.cookie.DateUtils;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.*;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.*;
import tee.binding.task.*;
import tee.binding.*;
import tee.binding.it.*;

public class Activity_NomenclatureNew extends Activity {
	Layoutless layoutless;

	//int gridPageSize = 50;
	int itemsMaxCount = 99;
	static Numeric gridOffset = new Numeric();
	//public static int lastGridScrollRowsCount = 0;
	//public static int lastGridOffset = 0;
	//public static int lastGridPage = 0;
	//public static int lastGridScroll = 0;

	static Numeric searchMode = new Numeric().value(1);
	static Numeric searchKuhnya = new Numeric();
	static Numeric searchTochka = new Numeric();

	Bough selectedRow = null;

	RedactSingleChoice choiceKuhnya;
	RedactText searchHistoryBox;
	RedactText searchAllBox;

	static Note searchWord = new Note();
	static Note searchHistoryByName = new Note();

	//Numeric searchFrom = new Numeric();
	//Numeric searchTo = new Numeric();

	int gridCategoriesY = 0;

	static int ShowViewPoisk = 0;
	static int ShowViewNomenklatura = 1;
	static int ShowViewIstoria = 2;

	DataGrid gridCategories;
	ColumnText columnCategories = new ColumnText();

	//DataGrid gridCategoryItems;
	DataGrid2 gridItems = null;
	ColumnText columnArtikul = new ColumnText();
	ColumnText columnProizvoditel = new ColumnText();
	ColumnText columnNomenklatura = new ColumnText();
	ColumnDescription columnMinKolichestvo = new ColumnDescription();
	ColumnDescription columnCena = new ColumnDescription();
	ColumnDescription columnSkidka = new ColumnDescription();
	ColumnDescription columnPosledniaya = new ColumnDescription();

	static Bough itemsData = new Bough();
	//static int lastMode = ShowViewPoisk;

	Note summaZakaza = new Note().value("summaZakaza");
	Task doRefreshData = new Task() {
		public void doTask() {
			System.out.println("set mode");

			//lastMode = mode.value().intValue();
			if (mode.value() == ShowViewNomenklatura) {
				refreshCategotyGrid();
			} else {
				resetItemsGrid();
			}

		}
	};
	static Activity_NomenclatureNew me = null;
	static Numeric mode = new Numeric().value(ShowViewPoisk).afterChange(new Task() {
		public void doTask() {
			if (me == null) {
				//
			} else {
				gridOffset.value(0);
				me.doRefreshData.start();
			}
		}
	}, true);

	RedactSingleChoice tipTochki;
	Bough tipTochkiData;

	static String level1 = "";
	static String level2 = "";
	static String level3 = "";
	static String level4 = "";
	static String level5 = "";

	static int color1 = 0x10000000;
	static int color2 = 0x20000000;
	static int color3 = 0x30000000;
	static int color4 = 0x40000000;
	static int color5 = 0x50000000;

	Task createCategoryTapTask(final String p1, final String p2, final String p3, final String p4, final String p5) {
		return new Task() {
			public void doTask() {
				level1 = p1;
				level2 = p2;
				level3 = p3;
				level4 = p4;
				level5 = p5;
				gridOffset.value(0);
				refreshCategotyGrid();
			}
		};
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//System.out.println("Activity_NomenclatureNew: " + Auxiliary.activityExatras(this).dumpXML());
		buildUI();
	}

	void buildUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		gridCategories = new DataGrid(this).noHead.is(true);
		gridItems = new DataGrid2(this);
		tipTochki = new RedactSingleChoice(this);
/*
		Calendar mHistoryDateFrom = Calendar.getInstance();
		mHistoryDateFrom.add(Calendar.DAY_OF_YEAR, -90);
		searchFrom.value((float) mHistoryDateFrom.getTimeInMillis());
		Calendar mHistoryDateTo = Calendar.getInstance();
		searchTo.value((float) mHistoryDateTo.getTimeInMillis());
*/
		summaZakaza.value("Сумма заказа: " + Auxiliary.activityExatras(this).child(IExtras.ORDER_AMOUNT).value.property.value());

		String sql = "select _idrref as _idrref,naimenovanie as naimenovanie from TipyTorgovihTochek where deletionmark=x'00' order by naimenovanie";
		tipTochkiData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		tipTochki.item("Любой тип");
		for (int i = 0; i < tipTochkiData.children.size(); i++) {
			tipTochki.item(tipTochkiData.children.get(i).child("naimenovanie").value.property.value());
		}
		layoutless.child(new Knob(this).labelText.is("Поиск").locked().is(mode.equals(ShowViewPoisk)).afterTap.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("knobViewPoisk");
				mode.value(ShowViewPoisk);
			}
		}).left().is(layoutless.width().property.multiply(0.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Номенклатура").locked().is(mode.equals(ShowViewNomenklatura)).afterTap.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("knobViewNomenklatura");
				mode.value(ShowViewNomenklatura);
			}
		}).left().is(layoutless.width().property.multiply(1.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("История").locked().is(mode.equals(ShowViewIstoria)).afterTap.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("knobViewIstoria");
				mode.value(ShowViewIstoria);
			}
		}).left().is(layoutless.width().property.multiply(2.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Добавить").afterTap.is(new Task() {
			public void doTask() {
				returnSelectedRow();
			}
		}).left().is(layoutless.width().property.minus(Auxiliary.tapSize * 3 * 1)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 3).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Фото").afterTap.is(new Task() {
			public void doTask() {
				openFoto();
			}
		}).left().is(layoutless.width().property.minus(Auxiliary.tapSize * 3 * 2)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 3).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Запрос цен").afterTap.is(new Task() {
			public void doTask() {
				requestPriceNew();
			}
		}).left().is(layoutless.width().property.minus(Auxiliary.tapSize * 3 * 3)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 3).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Сертификат").afterTap.is(new Task() {
			public void doTask() {
				openCertificate();
			}
		}).left().is(layoutless.width().property.minus(Auxiliary.tapSize * 3 * 4)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 3).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Decor(this).labelText.is(summaZakaza).labelAlignLeftCenter().left().is(Auxiliary.tapSize * 0.5).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 5).height().is(1 * Auxiliary.tapSize));
		//ShowViewPoisk
		choiceKuhnya = new RedactSingleChoice(this);
		layoutless.child(choiceKuhnya
				.item("Любая кухня")
				.item("Кавказская")
				.item("Европейская")
				.item("Итальянская")
				.item("Русская")
				.item("Японская")
				.item("Американская")
				.selection.is(searchKuhnya)
				.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(tipTochki
				.selection.is(searchTochka)
				.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(3 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new RedactSingleChoice(this)
				.item("Артикул")
				.item("Наименование")
				.item("Производитель")
				//.item("Тег")
				.selection.is(searchMode)
				.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(6 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		searchAllBox = new RedactText(this).text.is(searchWord);
		layoutless.child(searchAllBox.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(9 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(layoutless.width().property.minus(13 * Auxiliary.tapSize)).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
			public void doTask() {
				searchWord.value("");
			}
		}).hidden().is(mode.equals(ShowViewPoisk).not()).left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(1 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Найти").afterTap.is(new Task() {
			public void doTask() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchAllBox.getWindowToken(), 0);
				gridOffset.value(0);
				resetItemsGrid();
			}
		}).hidden().is(mode.equals(ShowViewPoisk).not()).left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		//ShowViewNomenklatura
		layoutless.child(gridCategories.pageSize.is(999).columns(new Column[]{
				columnCategories.width.is(5 * Auxiliary.tapSize)
		}).hidden().is(mode.equals(ShowViewNomenklatura).not()).left().is(0).top().is(1 * Auxiliary.tapSize).width().is(5 * Auxiliary.tapSize).height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize)));
		//ShowViewIstoria
		//layoutless.child(new Decor(this).labelAlignRightBottom().labelText.is("Период с").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		//layoutless.child(new RedactDate(this).date.is(searchFrom).format.is("dd.MM.yy").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(2 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		//layoutless.child(new Decor(this).labelAlignRightBottom().labelText.is("по").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(4 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(1 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		//layoutless.child(new RedactDate(this).date.is(searchTo).format.is("dd.MM.yy").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(5 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new Decor(this).labelAlignRightBottom().labelText.is("наименование").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		searchHistoryBox = new RedactText(this).text.is(searchHistoryByName);
		layoutless.child(searchHistoryBox.hidden().is(mode.equals(ShowViewIstoria).not()).left().is(2 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(layoutless.width().property.minus(6 * Auxiliary.tapSize)).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
			public void doTask() {
				searchHistoryByName.value("");
				//startScrollBack();
			}
		}).hidden().is(mode.equals(ShowViewIstoria).not()).left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(1 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Поиск").afterTap.is(new Task() {
			public void doTask() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchHistoryBox.getWindowToken(), 0);
				gridOffset.value(0);
				resetItemsGrid();
			}
		}).hidden().is(mode.equals(ShowViewIstoria).not()).left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		//grid
		layoutless.child(gridItems.center.is(true)
				.pageSize.is(itemsMaxCount)//gridPageSize)
				.dataOffset.is(gridOffset)//
				.beforeFlip.is(new Task() {
					public void doTask() {
						requeryGridData();
						flipGrid();
					}
				}).columns(new Column[]{
						columnArtikul.title.is("Артикул").width.is(1.5 * Auxiliary.tapSize)
						, columnNomenklatura.title.is("Номенклатура").width.is(8 * Auxiliary.tapSize)
						, columnProizvoditel.title.is("Пр-ль").width.is(2 * Auxiliary.tapSize)
						, columnMinKolichestvo.title.is("Min/Квант").width.is(2 * Auxiliary.tapSize)
						, columnCena.title.is("Цена").width.is(3.5 * Auxiliary.tapSize)
						, columnSkidka.title.is("Скидка").width.is(2.5 * Auxiliary.tapSize)
						, columnPosledniaya.title.is("Последняя цена").width.is(2 * Auxiliary.tapSize)
				})
				.left().is(new Numeric().value(5 * Auxiliary.tapSize).when(mode.equals(ShowViewNomenklatura)).otherwise(0))
				.top().is(new Numeric().value(1 * Auxiliary.tapSize).when(mode.equals(ShowViewNomenklatura)).otherwise(2 * Auxiliary.tapSize))
				.width().is(new Numeric().bind(layoutless.width().property.minus(5 * Auxiliary.tapSize))
						.when(mode.equals(ShowViewNomenklatura))
						.otherwise(new Numeric().bind(layoutless.width().property.minus(0 * Auxiliary.tapSize))))
				.height().is(new Numeric().bind(layoutless.height().property.minus(2 * Auxiliary.tapSize))
						.when(mode.equals(ShowViewNomenklatura))
						.otherwise(new Numeric().bind(layoutless.height().property.minus(3 * Auxiliary.tapSize))))
		);
		//
		//if (lastMode == ShowViewIstoria) {
			/*if (lastGridScrollRowsCount > 0) {
				//gridOffset.value(lastGridOffset);
				//this.gridCategoryItems.currentPage=lastGridPage;
				int backOffset = gridOffset.value().intValue() + lastGridScrollRowsCount;// -gridPageSize*1;
				System.out.println("onResume gridOffset " + this.gridOffset.value() + " backOffset " + backOffset+" , "+lastGridScrollRowsCount);
				//if (backOffset < 0) {backOffset = 0;}
				gridOffset.value(backOffset);
			}*/
			/*if (lastGridOffset > 0) {
				System.out.println("restore " + lastGridOffset + "/" + lastGridScroll);
				gridOffset.value(lastGridOffset);
				lastGridOffset = 0;
			}*/
		//}
		//refreshCategotyGrid();
		//mode.value(lastMode);
		//me=this;
		//doRefreshData.start();
	}


	void requeryGridData() {
		//System.out.println("requeryGridData " + mode.value());
		if (mode.value() == ShowViewIstoria) {
			//Date fr = new Date();
			//fr.setTime(searchFrom.value().longValue());
			//Date to = new Date();
			//to.setTime(searchTo.value().longValue());
			String sql = Request_NomenclatureBase.composeSQLall(//
					DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
					, ApplicationHoreca.getInstance().getClientInfo().getID()//
					, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
					, null//DateTimeHelper.SQLDateString(fr)//
					, null//DateTimeHelper.SQLDateString(to)//
					, searchHistoryByName.value()//
					, ISearchBy.SEARCH_NAME
					, false//
					, true//
					, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
					, itemsMaxCount//gridPageSize * 3//
					//, gridHistory.dataOffset.property.value().intValue()//
					, gridOffset.value().intValue()//
					, false//
					, false, null, null, false, false, null, null, null);
			//System.out.println(sql);
			itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		} else {
			if (mode.value() == ShowViewPoisk) {
				//if (searchWord.value().length() > 0) {
				int searchBy = sweetlife.android10.database.nomenclature.ISearchBy.SEARCH_ARTICLE;
				if (searchMode.value() == 1) searchBy = ISearchBy.SEARCH_NAME;
				if (searchMode.value() == 2) searchBy = ISearchBy.SEARCH_VENDOR;
				//if (searchMode.value() == 3) searchBy = ISearchBy.SEARCH_TAG;

				String kuhnya = null;
				if (searchKuhnya.value() > 0) kuhnya = choiceKuhnya.items.get(searchKuhnya.value().intValue());
				String tochka = null;
				if (searchTochka.value() > 0) tochka = tipTochkiData.children.get(searchTochka.value().intValue() - 1).child("_idrref").value.property.value();
				String sql = Request_Search.composeSQLall(//
						DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
						, ApplicationHoreca.getInstance().getClientInfo().getID()//
						, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
						, ""//
						, ""//
						, searchWord.value()//
						, searchBy//
						, false//
						, false//
						, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()
						, itemsMaxCount//gridPageSize * 3
						, gridOffset.value().intValue()
						, false
						, false
						, kuhnya
						, tochka
						, false
						, false
						//, _receptID
						, null
						, null
						, null
				);
				//System.out.println(sql);
				itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				//}
			} else {
				if (mode.value() == ShowViewNomenklatura) {
					if (level1.trim().length() > 0) {
						String parent = "x'" + level1 + "'";
						if (level2.trim().length() > 0) parent = "x'" + level2 + "'";
						if (level3.trim().length() > 0) parent = "x'" + level3 + "'";
						if (level4.trim().length() > 0) parent = "x'" + level4 + "'";
						if (level5.trim().length() > 0) parent = "x'" + level5 + "'";
						String sqlString = Request_NomenclatureBase.composeSQL(//
								DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
								, ApplicationHoreca.getInstance().getClientInfo().getID()//
								, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
								, ""//
								, ""//
								, parent//
								, ISearchBy.SEARCH_CHILDREN//
								, false//
								, false//
								, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()
								, itemsMaxCount//gridPageSize * 3
								, gridOffset.value().intValue()
								, false
								, false
								, false
								, null
								, null
						);
						//System.out.println(sqlString);
						itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sqlString, null));
					} else {
						itemsData = new Bough();
					}
				}
			}
		}
		//System.out.println("gridOffset.value() " + gridOffset.value());
	}

	Intent SetActivityResult() {
		System.out.println(this.getClass().getCanonicalName() + ": SetActivityResult");
		Intent resultIntent = new Intent();
		String vidSkidki = Request_NomenclatureBase.calculateVidSkidki(
				Numeric.string2double(selectedRow.child("Nacenka").value.property.value())
				, selectedRow.child("VidSkidki").value.property.value());
		double MinCena = Numeric.string2double(selectedRow.child("MinCena").value.property.value());
		double cenaSoSkidkoy = Request_NomenclatureBase.calculateCenaSoSkidkoy(
				Numeric.string2double(selectedRow.child("Cena").value.property.value())
				, Numeric.string2double(selectedRow.child("Skidka").value.property.value())
				, selectedRow.child("VidSkidki").value.property.value()
				, Numeric.string2double(selectedRow.child("MinCena").value.property.value())
				, Numeric.string2double(selectedRow.child("Nacenka").value.property.value())
		);
		if (MinCena == 0 || Requests.IsSyncronizationDateLater(0)) {
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MIN_CENA, 0.00D);
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MAX_CENA, 0.00D);
			if (vidSkidki.length() > 0) {
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, selectedRow.child("VidSkidki").value.property.value());
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, cenaSoSkidkoy);
			} else {
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, "x'00'");
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, 0.00D);
			}
		} else {
			if (vidSkidki.length() > 0) {
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, vidSkidki);
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, cenaSoSkidkoy);
			} else {
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, "x'00'");
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, Numeric.string2double(selectedRow.child("Cena").value.property.value()));
			}
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MIN_CENA, Numeric.string2double(selectedRow.child("MinCena").value.property.value()));
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MAX_CENA, Numeric.string2double(selectedRow.child("MaxCena").value.property.value()));
		}
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.SKIDKA, Numeric.string2double(selectedRow.child("Skidka").value.property.value()));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA, Numeric.string2double(selectedRow.child("Cena").value.property.value()));
		NomenclatureCountHelper helper = new NomenclatureCountHelper(
				Numeric.string2double(selectedRow.child("MinNorma").value.property.value())
				, Numeric.string2double(selectedRow.child("Koephphicient").value.property.value()));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.COUNT, helper.ReCalculateCount(0));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.NOMENCLATURE_ID, "x'" + selectedRow.child("_IDRRef").value.property.value() + "'");
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.NAIMENOVANIE, selectedRow.child("Naimenovanie").value.property.value());
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.ARTIKUL, selectedRow.child("Artikul").value.property.value());
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.OSNOVNOY_PROIZVODITEL, "x'" + selectedRow.child("OsnovnoyProizvoditel").value.property.value() + "'");
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.EDINICY_IZMERENIYA_ID, "x'" + selectedRow.child("EdinicyIzmereniyaID").value.property.value() + "'");
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.EDINICY_IZMERENIYA_NAIMENOVANIE, selectedRow.child("EdinicyIzmereniyaNaimenovanie").value.property.value());
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.KOEPHICIENT, Numeric.string2double(selectedRow.child("Koephphicient").value.property.value()));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MIN_NORMA, Numeric.string2double(selectedRow.child("MinNorma").value.property.value()));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.BASE_PRICE, Numeric.string2double(selectedRow.child("BasePrice").value.property.value()));
		resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.LAST_PRICE, Numeric.string2double(selectedRow.child("LastPrice").value.property.value()));
		return resultIntent;
	}

	void openFoto() {
		if (selectedRow == null) {
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		} else {
			String artikul = selectedRow.child("Artikul").value.property.value();
			String name = selectedRow.child("Naimenovanie").value.property.value();
			Intent intent = new Intent();
			intent.putExtra(sweetlife.android10.reports.ActivityPhoto.artikulField, artikul);
			intent.putExtra(sweetlife.android10.reports.ActivityPhoto.nameField, name);
			intent.setClass(Activity_NomenclatureNew.this, sweetlife.android10.reports.ActivityPhoto.class);
			Activity_NomenclatureNew.this.startActivity(intent);
		}
	}


	void requestPriceNew() {

		if (selectedRow != null) {
			/*String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
					+ "\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/DanniePoTovaram\" xmlns:ns2=\"DanniePoTovaram\">"//
					+ "\n  <SOAP-ENV:Body>"//
					+ "\n    <ns2:Get>"//
					+ "\n      <ns2:Spisok>"//
					;
			xml = xml//
					+ "\n        <ns1:Str>"//
					+ "\n          <ns1:Artikul>" + selectedRow.child("Artikul").value.property.value() + "</ns1:Artikul>"//
					+ "\n          <ns1:NaSklade></ns1:NaSklade>"//
					+ "\n          <ns1:Dostupno></ns1:Dostupno>"//
					+ "\n          <ns1:TekuhayaCena></ns1:TekuhayaCena>"//
					+ "\n          <ns1:Prais></ns1:Prais>"//
					+ "\n          <ns1:MinPorog></ns1:MinPorog>"//
					+ "\n        </ns1:Str>"//
			;
			xml = xml//
					+ "\n      </ns2:Spisok>"//
					+ "\n      <ns2:Otvetstvenniy>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod() + "</ns2:Otvetstvenniy>"//
					+ "\n    </ns2:Get>"//
					+ "\n  </SOAP-ENV:Body>"//
					+ "\n</SOAP-ENV:Envelope>"//
			;*/
			//String xml = composeXMLpriceRenew(selectedRow.child("Artikul").value.property.value());
			Vector<String> artikuls = new Vector<String>();
			artikuls.add(selectedRow.child("Artikul").value.property.value());
			final Note resultMessage = new Note().value("Проверка цен номенклатуры");
			Cfg.sendRequestPriceNew(this, artikuls, 9999, resultMessage, new Task() {
				public void doTask() {
					Auxiliary.warn(resultMessage.value(), Activity_NomenclatureNew.this);
					refreshCategotyGrid();


				}
			});
		} else {
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}
	}

	void openCertificate() {
		if (selectedRow == null) {
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		} else {
			final String artikul = selectedRow.child("Artikul").value.property.value();
			final String date = Auxiliary.short1cDate.format(new Date());
			final Note result = new Note();
			final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Cert" + artikul + "_" + date + ".xls";
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ObmenCVoditelem/SertArt/" + artikul + "/" + date + "/xls";
			Expect expectRequery = new Expect()//
					.status.is("Подождите.....")//
					.task.is(new Task() {
						@Override
						public void doTask() {
							try {
								byte[] raw = android.util.Base64.decode(Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()), Base64.DEFAULT);
								FileOutputStream fileOutputStream = null;
								fileOutputStream = new FileOutputStream(toPath);
								fileOutputStream.write(raw, 0, raw.length);
								fileOutputStream.close();
							} catch (Throwable t) {
								t.printStackTrace();
								result.value(t.getMessage());
							}
						}
					}).afterDone.is(new Task() {
						@Override
						public void doTask() {
							Auxiliary.warn("Файл Cert" + artikul + "_" + date + ".xls сохранён в папку Download. " + result.value(), Activity_NomenclatureNew.this);
						}
					});
			expectRequery.start(Activity_NomenclatureNew.this);
		}
	}

	void returnSelectedRow() {
		if (selectedRow == null) {
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		} else {
			//System.out.println("tap " + selectedRow.dumpXML());
			Intent intent = SetActivityResult();
			if (intent != null) {
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}


	void flipGrid() {
		//int last=Activity_NomenclatureNew.this.gridCategoryItems.lastClickedRow;
		System.out.println("flipGrid: rows " + itemsData.children.size());//+", last "+last);
		selectedRow = null;
		gridItems.clearColumns();
		String[] mDateFormatStrings = new String[]{
				"yyyy-MM-dd'T'HH:mm:ss"
				, "yyyy-MM-dd HH:mm:ss"
				, "yyyy-MM-dd",
				//				"yyyy-MM-dd hh:mm:ss",
				"yyyy-MM-dd hh:mm"
				, "yyyy-MM-dd hh:mm:ss"
				, "yyyy-MM-dd hh:mm:ss.sss"
				, "yyyy-MM-dd'T'hh:mm"
				, "yyyy-MM-dd'T'hh:mm:ss"
				, "yyyy-MM-dd'T'hh:mm:ss.sss"
				, "hh:mm"
				, "hh:mm:ss"
				, "hh:mm:ss.sss"
				, "yyyyMMdd'T'hh:mm:ss"
				, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
		};
		if (itemsData != null) {

			for (int ii = 0; ii < itemsData.children.size(); ii++) {
				final Bough row = itemsData.children.get(ii).createClone();
				Task tap = new Task() {
					public void doTask() {
						selectedRow = row;
						//System.out.println("tap " + selectedRow.dumpXML());

					}
				};
				int artikulBackground = 0;
				int naimenovanieBackGround = 0;
				int cenaBackground = 0;
				String LastSell = itemsData.children.get(ii).child("LastSell").value.property.value();
				if (LastSell != null) {
					if (LastSell.length() > 0) {
						try {
							java.util.Date d = DateUtils.parseDate(LastSell, mDateFormatStrings);
							java.util.Calendar now = Calendar.getInstance();
							now.set(Calendar.DAY_OF_MONTH, 1);
							now.add(Calendar.DAY_OF_MONTH, -1);
							if (d.before(now.getTime())) {
								artikulBackground = 0xffff6666;
							}
						} catch (Throwable tr) {
							//tr.printStackTrace();
						}
					}
				}
				double CENA = Numeric.string2double(itemsData.children.get(ii).child("Cena").value.property.value());
				double MIN_CENA = Numeric.string2double(itemsData.children.get(ii).child("MinCena").value.property.value());
				double MAX_CENA = Numeric.string2double(itemsData.children.get(ii).child("MaxCena").value.property.value());
				double BASE_PRICE = Numeric.string2double(itemsData.children.get(ii).child("BasePrice").value.property.value());

				//int fakt = (int) (100 * (cena - basePrice) / basePrice);
				//mPrice.setText(DecimalFormatHelper.format(cena) + "/" + fakt + "%");
				//}

				if (itemsData.children.get(ii).child("mustListId").value.property.value().trim().length() > 0) {
					naimenovanieBackGround = Settings.colorTop20;
				}
				String cenaNacenka = Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "р");
				String minCenaText = itemsData.children.get(ii).child("MinCena").value.property.value();
				String maxCenaText = itemsData.children.get(ii).child("MaxCena").value.property.value();
				if (!sweetlife.android10.ui.Activity_Bid.hideNacenkaStatus) {
					int procent = (int) (100.0 * (CENA - BASE_PRICE) / BASE_PRICE);
					if (procent >= 25) {
						cenaBackground = Settings.colorNacenka25;
					}
					cenaNacenka = Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "") + "р/" + procent + "%";
					if (MIN_CENA > 0) {
						minCenaText = minCenaText + "/" + ((int) (100.0 * (MIN_CENA - BASE_PRICE) / BASE_PRICE)) + "%";
					}
					maxCenaText = maxCenaText + "/" + ((int) (100.0 * (MAX_CENA - BASE_PRICE) / BASE_PRICE)) + "%";

				}

				//if (CENA == 0) {System.out.println(itemsData.children.get(ii).dumpXML());}
				columnArtikul.cell(itemsData.children.get(ii).child("Artikul").value.property.value()
						, artikulBackground
						, tap);
				columnNomenklatura.cell(itemsData.children.get(ii).child("Naimenovanie").value.property.value()
						, naimenovanieBackGround
						, tap);
				columnProizvoditel.cell(itemsData.children.get(ii).child("ProizvoditelNaimenovanie").value.property.value()
						, tap);
				columnMinKolichestvo.cell(itemsData.children.get(ii).child("MinNorma").value.property.value()
								+ "/" + itemsData.children.get(ii).child("Koephphicient").value.property.value()
						, tap
						, itemsData.children.get(ii).child("EdinicyIzmereniyaNaimenovanie").value.property.value());
				columnCena.cell(//Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "р")
						cenaNacenka
						, cenaBackground
						, tap
						, minCenaText + " - " + maxCenaText);
				columnSkidka.cell(Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Skidka").value.property.value(), "р")
						, tap
						, itemsData.children.get(ii).child("VidSkidki").value.property.value());
				//columnPosledniaya.cell(Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("LastPrice").value.property.value(), "р")
				//		, tap
				//		, Auxiliary.tryReFormatDate(itemsData.children.get(ii).child("LastSell").value.property.value(), "yyyy-MM-dd", "dd.MM.yyyy"));
				String lastDate = Auxiliary.tryReFormatDate(itemsData.children.get(ii).child("LastSell").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
				if (lastDate.length()>0) {
					columnPosledniaya.cell(lastDate
							, tap
							, Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("LastPrice").value.property.value(), "р")
									+ "/" + itemsData.children.get(ii).child("lastSellCount").value.property.value()
					);
				} else {
					columnPosledniaya.cell(" ", tap);
				}

			}
		}
	}

	void resetItemsGrid() {
		//System.out.println("resetItemsGrid");

		new Expect().status.is("Поиск...").task.is(new Task() {
			public void doTask() {
				requeryGridData();
			}
		}).afterDone.is(new Task() {
			public void doTask() {
				//System.out.println("resetItemsGrid " + gridOffset.value() + "/" + lastGridScroll+":"+gridCategoryItems.scrollView.getScrollY() + "/" + gridCategoryItems.currentPage );
/*
				if (mode.value() == ShowViewIstoria) {
					if (lastGridPage == 1) {
						flipGrid();
					} else {
						if (lastGridPage == 2) {
							flipGrid();
							flipGrid();
						}
					}

				}*/
				flipGrid();

				gridItems.refresh();
				//System.out.println("resetItemsGrid "+gridOffset.value());
/*
				System.out.println("resetItemsGrid " + gridOffset.value() + "/" + lastGridScroll+":"+gridCategoryItems.scrollView.getScrollY() + "/" + gridCategoryItems.currentPage );


				if (mode.value() == ShowViewIstoria) {
					System.out.println("scroll start "+lastGridScroll);
					//gridOffset..value(lastGridOffset);
					//lastGridScroll
					gridCategoryItems.scrollView.refreshScroll(lastGridScroll);
				}
				System.out.println("resetItemsGrid " + gridOffset.value() + "/" + lastGridScroll+":"+gridCategoryItems.scrollView.getScrollY() + "/" + gridCategoryItems.currentPage );
				//lastGridPage = 0;
				lastGridScroll = 0;*/

				//startScrollBack();
			}
		}).start(this);
	}

	/*
		void startScrollBack() {
			if (mode.value() == ShowViewIstoria) {
				if (lastGridScroll != 0) {
					System.out.println("startScrollBack");
					gridCategoryItems.append();
					gridCategoryItems.currentPage++;
					gridCategoryItems.append();
					gridCategoryItems.currentPage++;
					gridCategoryItems.append();
					gridCategoryItems.currentPage++;
					new android.os.Handler().postDelayed(new Runnable() {
						public void run() {
							scrollBackToLastPosition();
						}
					}, 999);
				}
			}
		}
	*/
/*
	void scrollBackToLastPosition() {

		if (mode.value() == ShowViewIstoria) {
			//lastGridScroll = -333;
			if (lastGridScroll != 0) {
				double contentHeight = gridCategoryItems.rowHeight.property.value() * (gridCategoryItems.currentPage + 1) * gridCategoryItems.pageSize.property.value();
				System.out.println("gridCategoryItems.rowHeight.property.value() " + gridCategoryItems.rowHeight.property.value());
				System.out.println("gridCategoryItems.currentPage " + gridCategoryItems.currentPage);
				System.out.println("gridOffset.value().intValue() " + gridOffset.value().intValue());
				System.out.println("gridCategoryItems.pageSize.property.value() " + gridCategoryItems.pageSize.property.value());
				System.out.println("scrollBackToLast " + lastGridScroll + "/" + gridCategoryItems.scrollView.getScrollY() + ":" + contentHeight + "/" + gridCategoryItems.lockScroll);
				//gridCategoryItems.append();
				//gridCategoryItems.append();
				gridCategoryItems.scrollView.scrollTo(0, lastGridScroll);
				//gridCategoryItems.scrollView.refreshScroll(lastGridScroll);
				System.out.println("to " + lastGridScroll + "/" + gridCategoryItems.scrollView.getScrollY());
				lastGridScroll = 0;
			}
		}
	}
*/
	void refreshCategotyGrid() {
		gridCategoriesY = gridCategories.scrollView.getScrollY();
		gridCategories.clearColumns();
		/*
		String sql = "select n1.naimenovanie as cat1,n1._IDRRef as key1";
		if (level1.trim().length() > 0) sql = sql + "\n		,n2.naimenovanie as cat2, n2._IDRRef as key2"; else sql = sql + "\n		,'' as cat2, '' as key2";
		if (level2.trim().length() > 0) sql = sql + "\n		,n3.naimenovanie as cat3, n3._IDRRef as key3"; else sql = sql + "\n		,'' as cat3, '' as key3";
		if (level3.trim().length() > 0) sql = sql + "\n		,n4.naimenovanie as cat4, n4._IDRRef as key4"; else sql = sql + "\n		,'' as cat4, '' as key4";
		if (level4.trim().length() > 0) sql = sql + "\n		,n5.naimenovanie as cat5, n5._IDRRef as key5"; else sql = sql + "\n		,'' as cat5, '' as key5";
		sql = sql + "\n	from nomenklatura n1";
		if (level1.length() > 0) sql = sql + "\n		left join nomenklatura n2 on n1._IDRRef=n2.Roditel and n2.EtoGruppa=x'01' and n2.PometkaUdaleniya=x'00' and n2.Roditel=x'" + level1 + "'";
		if (level2.length() > 0) sql = sql + "\n		left join nomenklatura n3 on n2._IDRRef=n3.Roditel and n3.EtoGruppa=x'01' and n3.PometkaUdaleniya=x'00' and n3.Roditel=x'" + level2 + "'";
		if (level3.length() > 0) sql = sql + "\n		left join nomenklatura n4 on n3._IDRRef=n4.Roditel and n4.EtoGruppa=x'01' and n4.PometkaUdaleniya=x'00' and n4.Roditel=x'" + level3 + "'";
		if (level4.length() > 0) sql = sql + "\n		left join nomenklatura n5 on n4._IDRRef=n5.Roditel and n5.EtoGruppa=x'01' and n5.PometkaUdaleniya=x'00' and n5.Roditel=x'" + level4 + "'";
		sql = sql + "\n	where n1.EtoGruppa=x'01' and n1.PometkaUdaleniya=x'00' and n1.Roditel=x'00'";
		sql = sql + "\n	order by cat1,cat2,cat3,cat4,cat5;";
		*/
		String sql = "select cat1 as cat1,key1 as key1, cat2 as cat2,key2 as key2, cat3 as cat3,key3 as key3, cat4 as cat4,key4 as key4, cat5 as cat5,key5 as key5";
		sql = sql + "\n	from nomenklatura_groups allgroups where (rod1=x'00' and key2 is null)";
		if (level1.trim().length() > 0) sql = sql + "\n		or (rod2=x'" + level1 + "' and key3 is null)";
		if (level2.trim().length() > 0) sql = sql + "\n		or (rod3=x'" + level2 + "' and key4 is null)";
		if (level3.trim().length() > 0) sql = sql + "\n		or (rod4=x'" + level3 + "' and key5 is null)";
		if (level4.trim().length() > 0) sql = sql + "\n		or (rod5=x'" + level4 + "')";
		sql = sql + "\n	order by cat1,cat2,cat3,cat4,cat5;";
		//System.out.println(level1 + "/" + level2 + "/" + level3 + "/" + level4 + "/" + level5 + ": " + sql);
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		String lastKey1 = "";
		String lastKey2 = "";
		String lastKey3 = "";
		String lastKey4 = "";
		String lastKey5 = "";
		int rowCounter = 0;
		for (int ii = 0; ii < data.children.size(); ii++) {
			String curKey1 = data.children.get(ii).child("key1").value.property.value().trim();
			String curKey2 = data.children.get(ii).child("key2").value.property.value().trim();
			String curKey3 = data.children.get(ii).child("key3").value.property.value().trim();
			String curKey4 = data.children.get(ii).child("key4").value.property.value().trim();
			String curKey5 = data.children.get(ii).child("key5").value.property.value().trim();
			String curCat1 = data.children.get(ii).child("cat1").value.property.value().trim();
			String curCat2 = data.children.get(ii).child("cat2").value.property.value().trim();
			String curCat3 = data.children.get(ii).child("cat3").value.property.value().trim();
			String curCat4 = data.children.get(ii).child("cat4").value.property.value().trim();
			String curCat5 = data.children.get(ii).child("cat5").value.property.value().trim();
			if (curKey1.length() == 0) {
				rowCounter++;
				columnCategories.cell(curCat1, color1, createCategoryTapTask(curKey1, "", "", "", ""));
			} else {
				if (curKey2.length() == 0) {
					if (curKey1.toUpperCase().equals(level1.toUpperCase())) gridCategoriesY = Auxiliary.tapSize * rowCounter;
					rowCounter++;
					columnCategories.cell(curCat1, color1, createCategoryTapTask(curKey1, "", "", "", ""));
				} else {
					if (curKey3.length() == 0) {
						if (!curKey1.equals(lastKey1)) {
							if (curKey1.toUpperCase().equals(level1.toUpperCase())) gridCategoriesY = Auxiliary.tapSize * rowCounter;
							rowCounter++;
							columnCategories.cell(curCat1, color1);
						}
						rowCounter++;
						columnCategories.cell(curCat2, color2, createCategoryTapTask(curKey1, curKey2, "", "", ""));
					} else {
						if (curKey4.length() == 0) {
							if (!curKey1.equals(lastKey1)) {
								rowCounter++;
								columnCategories.cell(curCat1, color1);
							}
							if (!curKey2.equals(lastKey2)) {
								if (curKey2.toUpperCase().equals(level2.toUpperCase())) gridCategoriesY = Auxiliary.tapSize * rowCounter;
								rowCounter++;
								columnCategories.cell(curCat2, color2);
							}
							rowCounter++;
							columnCategories.cell(curCat3, color3, createCategoryTapTask(curKey1, curKey2, curKey3, "", ""));
						} else {
							if (curKey5.length() == 0) {
								if (!curKey1.equals(lastKey1)) {
									rowCounter++;
									columnCategories.cell(curCat1, color1);
								}
								if (!curKey2.equals(lastKey2)) {
									rowCounter++;
									columnCategories.cell(curCat2, color2);
								}
								if (!curKey3.equals(lastKey3)) {
									if (curKey3.toUpperCase().equals(level3.toUpperCase())) gridCategoriesY = Auxiliary.tapSize * rowCounter;
									rowCounter++;
									columnCategories.cell(curCat3, color3);
								}
								rowCounter++;
								columnCategories.cell(curCat4, color4, createCategoryTapTask(curKey1, curKey2, curKey3, curKey4, ""));
							} else {
								if (!curKey1.equals(lastKey1)) {
									rowCounter++;
									columnCategories.cell(curCat1, color1);
								}
								if (!curKey2.equals(lastKey2)) {
									rowCounter++;
									columnCategories.cell(curCat2, color2);
								}
								if (!curKey3.equals(lastKey3)) {
									rowCounter++;
									columnCategories.cell(curCat3, color3);
								}
								if (!curKey4.equals(lastKey4)) {
									if (curKey4.toUpperCase().equals(level4.toUpperCase())) gridCategoriesY = Auxiliary.tapSize * rowCounter;
									rowCounter++;
									columnCategories.cell(curCat4, color4);
								}
								rowCounter++;
								columnCategories.cell(curCat5, color5, createCategoryTapTask(curKey1, curKey2, curKey3, curKey4, curKey5));
							}
						}
					}
				}
			}
			lastKey1 = curKey1;
			lastKey2 = curKey2;
			lastKey3 = curKey3;
			lastKey4 = curKey4;
			lastKey5 = curKey5;
		}
		gridCategories.refresh();
		gridCategories.scrollView.scrollTo(0, gridCategoriesY);
		resetItemsGrid();
	}

	@Override
	protected void onPause() {
		//System.out.println("onPause");
		//dataGrid.dump();
		//lastGridY=dataGrid.scrollView.getScrollY();
		//lastGridOffset=this.gridOffset.value().intValue();
		//lastGridPage = this.gridCategoryItems.currentPage;
		/*
		if (gridOffset.value() > 0) {
			lastGridScroll = this.gridCategoryItems.scrollView.getScrollY();// this.gridCategoryItems.rowHeight.property.value().intValue();
			lastGridOffset = gridOffset.value().intValue();
		} else {
			lastGridScroll = this.gridCategoryItems.scrollView.getScrollY();// this.gridCategoryItems.rowHeight.property.value().intValue();
			lastGridOffset = 0;
		}
		*/


/*
		if (gridOffset.value() > 0) {
			lastGridScrollRowsCount = this.gridCategoryItems.scrollView.getScrollY() / this.gridCategoryItems.rowHeight.property.value().intValue();
		} else {
			lastGridScrollRowsCount = -this.gridCategoryItems.scrollView.getScrollY() / this.gridCategoryItems.rowHeight.property.value().intValue();
		}
		*/
		//lastGridScrollRowsCount = 3*gridOffset.value().intValue()+this.gridCategoryItems.scrollView.getScrollY() / this.gridCategoryItems.rowHeight.property.value().intValue();
		//System.out.println("onPause gridOffset/gridScroll/currentPage " + gridOffset.value() + "/" + lastGridScrollRowsCount + "/" + this.gridCategoryItems.currentPage);
		//if (mode.value() == ShowViewIstoria) {
			/*if (gridOffset.value() > 0) {
				gridScroll = this.gridCategoryItems.scrollView.getScrollY() / this.gridCategoryItems.rowHeight.property.value().intValue();
			} else {
				gridScroll = -this.gridCategoryItems.scrollView.getScrollY() / this.gridCategoryItems.rowHeight.property.value().intValue();
			}*/
		//lastGridScroll = this.gridCategoryItems.scrollView.getScrollY();
		//lastGridOffset = gridOffset.value().intValue();
/*
			double contentHeight = gridCategoryItems.rowHeight.property.value() * (gridCategoryItems.currentPage + 1) * gridCategoryItems.pageSize.property.value();
			System.out.println("gridCategoryItems.rowHeight.property.value() " + gridCategoryItems.rowHeight.property.value());
			System.out.println("gridCategoryItems.currentPage " + gridCategoryItems.currentPage);
			System.out.println("gridOffset.value().intValue() " + gridOffset.value().intValue());
			System.out.println("gridCategoryItems.pageSize.property.value() " + gridCategoryItems.pageSize.property.value());
			System.out.println("onPause " + lastGridOffset + "/" + lastGridScroll + "/" + contentHeight);
			*/
		//}
		super.onPause();
		me = null;
		double newOffset = Math.floor(gridOffset.value() + this.gridItems.scrollView.getScrollY() / Auxiliary.tapSize);
		if (newOffset < 0) {
			newOffset = 0;
		}
		gridOffset.value(newOffset);
	}

	@Override
	protected void onResume() {
		super.onResume();
		me = this;
		doRefreshData.start();
		/*if (mode.value() == ShowViewIstoria) {
			//if(lastGridScroll>0) {
			System.out.println("resume scroll");
			if (gridScroll < 0) {
				gridCategoryItems.scrollView.scrollTo(0, -gridScroll * gridCategoryItems.rowHeight.property.value().intValue());
				gridScroll = 0;
			}

			//gridOffset..value(lastGridOffset);
			//lastGridScroll
			//gridCategoryItems.scrollView.scrollTo(0, lastGridScroll);
			//}
		}*/
	}
}
