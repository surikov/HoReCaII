package sweetlife.android10.ui;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.*;
import android.os.*;
import android.util.Base64;
import android.view.*;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.impl.cookie.DateUtils;

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import reactive.ui.*;

import sweetlife.android10.*;
import sweetlife.android10.consts.IExtras;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.*;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.utils.*;
import tee.binding.task.*;
import tee.binding.*;
import tee.binding.it.*;


public class Activity_NomenclatureNew extends Activity{

	private boolean stopBackgroundActions = true;

	public static String lastSelectedArtikul = null;
	static Numeric gridOffset = new Numeric();
	static Numeric searchMode = new Numeric().value(3);
	static Numeric searchKuhnya = new Numeric();
	static Numeric searchTochka = new Numeric();
	static Note searchWord = new Note();
	static Note searchHistoryByName = new Note();
	static int ShowViewPoisk = 0;
	static int ShowViewNomenklatura = 1;
	static int ShowViewIstoria = 2;
	static Bough itemsData = new Bough();
	static Activity_NomenclatureNew me = null;
	static Numeric mode = new Numeric().value(ShowViewPoisk).afterChange(new Task(){
		public void doTask(){
			if(me == null){
				//
			}else{
				//System.out.println("now mode is " + mode.value());
				gridOffset.value(0);
				me.doRefreshData.start();
			}
		}
	}, true);
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
	Layoutless layoutless;
	int itemsMaxCount = 33;
	Bough selectedRow = null;
	RedactSingleChoice choiceKuhnya;
	RedactText searchHistoryBox;
	RedactText searchAllBox;
	MenuItem menuExportAssortiment;
	//public static Toggle filterByStar = new Toggle();
	//public static Toggle filterBySTM = new Toggle().value(false);
	Note requeryGridDataMessage = new Note();
	int gridCategoriesY = 0;
	DataGrid gridCategories;
	ColumnText columnCategories = new ColumnText();
	DataGrid2 gridItems = null;
	ColumnText columnArtikul = new ColumnText();
	ColumnText columnProizvoditel = new ColumnText();
	ColumnText columnNomenklatura = new ColumnText();
	ColumnDescription columnMinKolichestvo = new ColumnDescription();
	ColumnDescription columnCena = new ColumnDescription();
	ColumnDescription columnSkidka = new ColumnDescription();
	ColumnDescription columnPosledniaya = new ColumnDescription();
	Note summaZakaza = new Note().value("summaZakaza");
	Numeric filterStmStarRecomendaciaKorzina = new Numeric().afterChange(new Task(){
		public void doTask(){
			if(me == null){
				//
			}else{
				//System.out.println("now mode is " + mode.value());
				gridOffset.value(0);
				me.doRefreshData.start();
			}
		}
	}, true);
	RedactSingleChoice choiceStmStarRecomendaciaKorzina;
	RedactSingleChoice tipTochki;
	Bough tipTochkiData;
	Task doRefreshData = new Task(){
		public void doTask(){
			System.out.println("doRefreshData mode " + mode.value());
			if(Activity_NomenclatureNew.this.stopBackgroundActions){
				System.out.println("doRefreshData stopBackgroundActions " + Activity_NomenclatureNew.this.stopBackgroundActions);
			}
			if(mode.value() == ShowViewNomenklatura){
				refreshCategotyGrid();
			}else{
				resetItemsGrid();
			}
		}
	};

	Task createCategoryTapTask(final String p1, final String p2, final String p3, final String p4, final String p5){
		return new Task(){
			public void doTask(){
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
	public void onCreate(Bundle savedInstanceState){
		System.out.println("Activity_NomenclatureNew.onCreate");
		super.onCreate(savedInstanceState);
		Activity_NomenclatureNew.this.stopBackgroundActions = false;
		buildUI();
		doRefreshData.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuExportAssortiment = menu.add("Экспорт всего ассортимента");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		if(item == menuExportAssortiment){
			doExportAssortiment();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void doExportAssortiment(){
		String sql = "select n.[Artikul] as Artikul\n" +
				"	,case curAssortiment.Trafic\n" +
				" 			when x'01' then 'под заказ '\n" +
				" 			else ''\n" +
				" 			end\n" +
				" 		|| case brand.stm\n" +
				" 			when x'01' then 'СТМ: '\n" +
				" 			else ''\n" +
				" 			end\n" +
				" 		|| n.[Naimenovanie]\n" +
				" 		|| ', НДС '\n" +
				" 		|| case n.stavkands\n" +
				" 			when X'9701531AAE7E29E1418D1FB94BB4DD8D' then '18'\n" +
				" 			when X'8C35D1AA082D09C449482233639CB5DC' then '10'\n" +
				" 			when X'96A72E469DF2A8DF4F5BF008C2577B7D' then '20'\n" +
				" 			else ''\n" +
				" 			end\n" +
				" 		|| '%'\n" +
				"  		|| case n.mark\n" +
				" 			when x'01' then ', ЧЗ'\n" +
				" 			else ''\n" +
				" 			end\n" +
				"  		|| case ifnull(top20.nomenklatura,'')\n" +
				" 			when '' then ''\n" +
				" 			else '`'\n" +
				" 			end\n" +
				" 	as Naimenovanie\n" +
				" from Nomenklatura_sorted n \n" +
				"  	cross join Consts const \n" +
				"  	cross join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=n.[_IDRRef]\n" +
				"  	cross join (select \n" +
				"  			'" + DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) + "' as dataOtgruzki \n" +
				"  			," + ApplicationHoreca.getInstance().getClientInfo().getID() + " as kontragent \n" +
				"  			," + ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr() + " as polzovatel \n" +
				"  		) parameters \n" +
				"  	cross join Polzovateli on Polzovateli._idrref=parameters.polzovatel \n" +
				"  	cross join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie \n" +
				"  	cross join kontragenty on kontragenty._idrref=parameters.kontragent \n" +
				"	left join dopmotivaciya_cache top20 on top20.nomenklatura=n._idrref  	\n" +
				"   left join brand on brand._idrref=n.brand\n" +
				"where curAssortiment.zapret!=x'01' \n" +
				"limit 12345 offset 0;";
		System.out.println(sql);
		String txt = "";
		txt = txt + "\nКонтрагент," + ApplicationHoreca.getInstance().getClientInfo().getKod() + ": " + ApplicationHoreca.getInstance().getClientInfo().getName();
		txt = txt + "\nПользователь," + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod();
		txt = txt + "\nОтгрузка," + DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for(int ii = 0; ii < data.children.size(); ii++){
			Bough row = data.children.get(ii);
			txt = txt + "\n" + row.child("Artikul").value.property.value();
			txt = txt + ", " + row.child("Naimenovanie").value.property.value();
		}
		System.out.println(txt);
		String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())
				+ "." + ApplicationHoreca.getInstance().getClientInfo().getKod()
				+ "." + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod()

				+ ".csv";
		Auxiliary.writeTextToFile(new java.io.File(filename), txt, "Windows-1251");
		Auxiliary.warn(filename, this);
	}

	void buildUI(){
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		gridCategories = new DataGrid(this).noHead.is(true);
		gridItems = new DataGrid2(this);
		tipTochki = new RedactSingleChoice(this);
		summaZakaza.value("Сумма заказа: " + Auxiliary.activityExatras(this).child(IExtras.ORDER_AMOUNT).value.property.value());
		String sql = "select _idrref as _idrref,naimenovanie as naimenovanie from TipyTorgovihTochek where deletionmark=x'00' order by naimenovanie";
		tipTochkiData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		tipTochki.item("Любой тип");
		for(int i = 0; i < tipTochkiData.children.size(); i++){
			tipTochki.item(tipTochkiData.children.get(i).child("naimenovanie").value.property.value());
		}
		layoutless.child(new Knob(this).labelText.is("Поиск").locked().is(mode.equals(ShowViewPoisk)).afterTap.is(new Task(){
					@Override
					public void doTask(){
						//System.out.println("knobViewPoisk");
						mode.value(ShowViewPoisk);
					}
				}).left().is(layoutless.width().property.multiply(0.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Номенклатура").locked().is(mode.equals(ShowViewNomenklatura)).afterTap.is(new Task(){
					@Override
					public void doTask(){
						//System.out.println("knobViewNomenklatura");
						mode.value(ShowViewNomenklatura);
					}
				}).left().is(layoutless.width().property.multiply(1.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("История").locked().is(mode.equals(ShowViewIstoria)).afterTap.is(new Task(){
					@Override
					public void doTask(){
						//System.out.println("knobViewIstoria");
						mode.value(ShowViewIstoria);
					}
				}).left().is(layoutless.width().property.multiply(2.0 / 3))//
				.top().is(0).width().is(layoutless.width().property.divide(3)).height().is(1 * Auxiliary.tapSize));


		layoutless.child(new Knob(this).labelText.is("Добавить").afterTap.is(new Task(){
					public void doTask(){
						returnSelectedRow();
					}
				})
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2.5 * 1))
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
				.width().is(Auxiliary.tapSize * 2.5).height()
				.is(1 * Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Фото").afterTap.is(new Task(){
					public void doTask(){
						openFoto();
					}
				})
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2.5 * 2))
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
				.width().is(Auxiliary.tapSize * 2.5)
				.height().is(1 * Auxiliary.tapSize));
		/*
		layoutless.child(new Knob(this).labelText.is("Запрос цен").afterTap.is(new Task(){
					public void doTask(){
						requestPriceNew();
					}
				})
				.locked().is(true)
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2.5 * 3)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 2.5).height().is(1 * Auxiliary.tapSize));
		*/
		layoutless.child(new Knob(this).labelText.is("Обратн.связь").afterTap.is(new Task(){
					public void doTask(){
						promptObratnaya();
					}
				})
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2.5 * 3)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 2.5).height().is(1 * Auxiliary.tapSize));


		layoutless.child(new Knob(this).labelText.is("Сертификат").afterTap.is(new Task(){
			public void doTask(){
				openCertificate();
			}
		}).left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2.5 * 4)).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 2.5).height().is(1 * Auxiliary.tapSize));
		/*
		layoutless.child(new Knob(this)
				.labelText.is(new Note().value("★").when(filterByStar).otherwise("☆"))
				.afterTap.is(new Task() {
					public void doTask() {
						toggleStarFilter();
						resetItemsGrid();
					}
				})
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * (2.5 * 4 + 1)))
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
				.width().is(Auxiliary.tapSize * 1)
				.height().is(1 * Auxiliary.tapSize));

		layoutless.child(new Knob(this)
				.labelText.is(new Note().value("✔ СТМ").when(filterBySTM).otherwise("СТМ"))
				.afterTap.is(new Task() {
					public void doTask() {
						filterBySTM.value(!filterBySTM.value());
						resetItemsGrid();
					}
				})
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * (2.5 * 4 + 1 + 2)))
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
				.width().is(Auxiliary.tapSize * 2)
				.height().is(1 * Auxiliary.tapSize));
*/
		choiceStmStarRecomendaciaKorzina = new RedactSingleChoice(this);
		layoutless.child(choiceStmStarRecomendaciaKorzina
				.item("Вся номенклатура")
				.item("СТМ")
				.item("★")
				.item("Рекомендованные")
				.item("Корзина")
				.item("Распродажа")
				.selection.is(filterStmStarRecomendaciaKorzina)
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * (2.5 * 4 + 5)))
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
				.width().is(5 * Auxiliary.tapSize)
				.height().is(Auxiliary.tapSize)
		);

		layoutless.child(new Decor(this).labelText.is(summaZakaza).labelAlignLeftCenter().left().is(Auxiliary.tapSize * 0.5).top().is(layoutless.height().property.minus(Auxiliary.tapSize)).width().is(Auxiliary.tapSize * 5).height().is(1 * Auxiliary.tapSize));
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
				.item("Поиск. запрос")
				.selection.is(searchMode)
				.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(6 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		searchAllBox = new RedactText(this).text.is(searchWord);
		layoutless.child(searchAllBox.hidden().is(mode.equals(ShowViewPoisk).not()).left().is(9 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(layoutless.width().property.minus(13 * Auxiliary.tapSize)).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("X").afterTap.is(new Task(){
			public void doTask(){
				searchWord.value("");
			}
		}).hidden().is(mode.equals(ShowViewPoisk).not()).left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(1 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Найти").afterTap.is(new Task(){
			public void doTask(){
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchAllBox.getWindowToken(), 0);
				gridOffset.value(0);
				resetItemsGrid();
			}
		}).hidden().is(mode.equals(ShowViewPoisk).not()).left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(gridCategories.pageSize.is(999).columns(new Column[]{
				columnCategories.width.is(5 * Auxiliary.tapSize)
		}).hidden().is(mode.equals(ShowViewNomenklatura).not()).left().is(0).top().is(1 * Auxiliary.tapSize).width().is(5 * Auxiliary.tapSize).height().is(layoutless.height().property.minus(2 * Auxiliary.tapSize)));
		layoutless.child(new Decor(this).labelAlignRightBottom().labelText.is("наименование").hidden().is(mode.equals(ShowViewIstoria).not()).left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		searchHistoryBox = new RedactText(this).text.is(searchHistoryByName);
		layoutless.child(searchHistoryBox.hidden().is(mode.equals(ShowViewIstoria).not()).left().is(2 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(layoutless.width().property.minus(6 * Auxiliary.tapSize)).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("X").afterTap.is(new Task(){
			public void doTask(){
				searchHistoryByName.value("");
			}
		}).hidden().is(mode.equals(ShowViewIstoria).not()).left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(1 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(new Knob(this).labelText.is("Поиск").afterTap.is(new Task(){
			public void doTask(){
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchHistoryBox.getWindowToken(), 0);
				gridOffset.value(0);
				resetItemsGrid();
			}
		}).hidden().is(mode.equals(ShowViewIstoria).not()).left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize)).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize).height().is(Auxiliary.tapSize));
		layoutless.child(gridItems.center.is(true)
				.pageSize.is(itemsMaxCount)//gridPageSize)
				.dataOffset.is(gridOffset)//
				.beforeFlip.is(new Task(){
					public void doTask(){
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
	}

	String searchWeb(){
		//https://horeca-prod-k8s.1221systems.ru/?query=малако
		//1221systems_admin:horfASdsla!g
		String artikuls = "";
		try{
			String word = URLEncoder.encode(searchWord.value(), "UTF-8");//"малако";
			String url = "https://horeca-prod-k8s.1221systems.ru/?limit=300&query=" + word;
			String login = "1221systems_admin";
			String password = "horfASdsla!g";
			//System.out.println(login + ": " + password + ": " + url);

			byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, login, password, "UTF-8");
			String txt = new String(bytes);
			Bough json = Bough.parseJSON(txt);
			System.out.println("json " + json.dumpXML());
			Vector<Bough> ids = json.child("results").children("ids");
			if(ids.size() > 0){
				artikuls = "false";
			}
			for(int i = 0; i < ids.size() && i < 300; i++){
				artikuls = artikuls + " or n.artikul='" + ids.get(i).value.property.value() + "'";
			}
			//System.out.println("arts " + arts);
		}catch(Throwable t){
			t.printStackTrace();
			requeryGridDataMessage.value("Ошибка " + t.getMessage());
		}
		return artikuls;
	}

	void requeryGridData(){

		//searchWeb();
		System.out.println("requeryGridData mode " + mode.value() + "/" + searchWord.value());
		if(mode.value() == ShowViewIstoria){
			String sql = Request_NomenclatureBase.composeSQLall_Old(//.composeSQLall(//
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
					, false, null, null, false, false, null, null, null
					, filterStmStarRecomendaciaKorzina.value() == 1//,filterBySTM.value()
					, filterStmStarRecomendaciaKorzina.value() == 2
					, filterStmStarRecomendaciaKorzina.value() == 3
					, filterStmStarRecomendaciaKorzina.value() == 4
					, filterStmStarRecomendaciaKorzina.value() == 5
					, false
			);

			itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		}else{
			int searchBy = sweetlife.android10.database.nomenclature.ISearchBy.SEARCH_ARTICLE;
			if(mode.value() == ShowViewPoisk){
				String filter = searchWord.value();
				if(searchMode.value() == 3 && searchWord.value().trim().length() > 0){
					filter = searchWeb();
					if(filter.equals("")){
						searchBy = ISearchBy.SEARCH_NAME;
						filter = searchWord.value();
					}else{
						searchBy = ISearchBy.SEARCH_CUSTOM;
					}
				}else{
					if(searchMode.value() == 1 || searchWord.value().trim().length() == 0){
						searchBy = ISearchBy.SEARCH_NAME;
					}
					if(searchMode.value() == 2){
						searchBy = ISearchBy.SEARCH_VENDOR;
					}
				}
				String kuhnya = null;
				if(searchKuhnya.value() > 0){
					kuhnya = choiceKuhnya.items.get(searchKuhnya.value().intValue());
				}
				String tochka = null;
				if(searchTochka.value() > 0){
					tochka = tipTochkiData.children.get(searchTochka.value().intValue() - 1).child("_idrref").value.property.value();
				}
				String sql = Request_Search.composeSQLall_Old(//.composeSQLall(//
						DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
						, ApplicationHoreca.getInstance().getClientInfo().getID()//
						, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
						, ""//
						, ""//
						, filter//searchWord.value()//
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
						, null
						, null
						, null
						, filterStmStarRecomendaciaKorzina.value() == 1//,filterBySTM.value()
						, filterStmStarRecomendaciaKorzina.value() == 2
						, filterStmStarRecomendaciaKorzina.value() == 3
						, filterStmStarRecomendaciaKorzina.value() == 4
						, filterStmStarRecomendaciaKorzina.value() == 5
						, false
				);
				System.out.println("Activity_NomenclatureNew.requeryGridData" + sql);
				itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				//}
			}else{
				if(mode.value() == ShowViewNomenklatura){
					if(level1.trim().length() > 0){
						String parent = "x'" + level1 + "'";
						if(level2.trim().length() > 0)
							parent = "x'" + level2 + "'";
						if(level3.trim().length() > 0)
							parent = "x'" + level3 + "'";
						if(level4.trim().length() > 0)
							parent = "x'" + level4 + "'";
						if(level5.trim().length() > 0)
							parent = "x'" + level5 + "'";
						String sqlString = Request_NomenclatureBase.composeSQLall_Old(//
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
								, false//
								, false, null, null, false, false, null, null, null
								, filterStmStarRecomendaciaKorzina.value() == 1//,filterBySTM.value()
								, filterStmStarRecomendaciaKorzina.value() == 2
								, filterStmStarRecomendaciaKorzina.value() == 3
								, filterStmStarRecomendaciaKorzina.value() == 4
								, filterStmStarRecomendaciaKorzina.value() == 5
								, false
						);
						itemsData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sqlString, null));

					}else{
						itemsData = new Bough();
					}
				}
			}
		}
		System.out.println("requeryGridData done " + itemsData.dumpXML());
	}

	Intent SetActivityResult(){
		//System.out.println(this.getClass().getCanonicalName() + ": SetActivityResult");
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
		if(MinCena == 0 || Requests.IsSyncronizationDateLater(0)){
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MIN_CENA, 0.00D);
			resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.MAX_CENA, 0.00D);
			if(vidSkidki.length() > 0){
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, selectedRow.child("VidSkidki").value.property.value());
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, cenaSoSkidkoy);
			}else{
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, "x'00'");
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, 0.00D);
			}
		}else{
			if(vidSkidki.length() > 0){
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.VID_SKIDKI, vidSkidki);
				resultIntent.putExtra(sweetlife.android10.consts.ITableColumnsNames.CENA_SO_SKIDKOY, cenaSoSkidkoy);
			}else{
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
		resultIntent.putExtra("smartPrice", Numeric.string2double(selectedRow.child("smartprice").value.property.value()));
		return resultIntent;
	}

	void openFoto(){
		if(selectedRow == null){
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}else{
			String artikul = selectedRow.child("Artikul").value.property.value();
			String name = selectedRow.child("Naimenovanie").value.property.value();
			Intent intent = new Intent();
			intent.putExtra(sweetlife.android10.reports.ActivityPhoto.artikulField, artikul);
			intent.putExtra(sweetlife.android10.reports.ActivityPhoto.nameField, name);
			intent.setClass(Activity_NomenclatureNew.this, sweetlife.android10.reports.ActivityPhoto.class);
			Activity_NomenclatureNew.this.startActivity(intent);
		}
	}

	void promptObratnaya(){
		if(selectedRow != null){
			String artikul = selectedRow.child("Artikul").value.property.value();
			String name = selectedRow.child("Naimenovanie").value.property.value();
			String kodKlienta = ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
			String clientName = ApplicationHoreca.getInstance().getClientInfo().getName().trim();
			ActivityWebServicesReports.promptObratnayaSvyazKlient(this, artikul, name, kodKlienta, clientName);
		}else{
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}
	}

	/*
	void requestPriceNew(){
		if(selectedRow != null){
			Vector<String> artikuls = new Vector<String>();
			artikuls.add(selectedRow.child("Artikul").value.property.value());
			final Note resultMessage = new Note().value("Проверка цен номенклатуры");
			Cfg.sendRequestPriceNew(this, artikuls, 9999, resultMessage, new Task(){
				public void doTask(){
					Auxiliary.warn(resultMessage.value(), Activity_NomenclatureNew.this);
					refreshCategotyGrid();
				}
			});
		}else{
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}
	}
*/
	/*
		void toggleStarFilter() {
			filterByStar.value(!filterByStar.value());
		}
	*/
	void openCertificate(){
		if(selectedRow == null){
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}else{
			final String artikul = selectedRow.child("Artikul").value.property.value();
			final String date = Auxiliary.short1cDate.format(new Date());
			final Note result = new Note();
			final String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Cert" + artikul + "_" + date + ".xls";
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ObmenCVoditelem/SertArt/" + artikul + "/" + date + "/xls";
			Expect expectRequery = new Expect()//
					.status.is("Подождите.....")//
					.task.is(new Task(){
						@Override
						public void doTask(){
							try{
								byte[] raw = android.util.Base64.decode(Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()), Base64.DEFAULT);
								FileOutputStream fileOutputStream = null;
								fileOutputStream = new FileOutputStream(toPath);
								fileOutputStream.write(raw, 0, raw.length);
								fileOutputStream.close();
							}catch(Throwable t){
								t.printStackTrace();
								result.value(t.getMessage());
							}
						}
					}).afterDone.is(new Task(){
						@Override
						public void doTask(){
							Auxiliary.warn("Файл Cert" + artikul + "_" + date + ".xls сохранён в папку Download. " + result.value(), Activity_NomenclatureNew.this);
						}
					});
			expectRequery.start(Activity_NomenclatureNew.this);
		}
	}

	void returnSelectedRow(){
		if(selectedRow == null){
			Auxiliary.warn("Не выбрана номенклатура.", Activity_NomenclatureNew.this);
		}else{
			Intent intent = SetActivityResult();
			if(intent != null){
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}

	void toggleStart(String art, int rowNum){
		String nartart = art;
		String sql = "select artikul as artikul from stars where artikul='" + art + "';";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		if(data.children.size() > 0){
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from stars where artikul='" + art + "';");
		}else{
			ApplicationHoreca.getInstance().getDataBase().execSQL("insert into stars (artikul) values ('" + art + "');");
			nartart = "★ " + art;
		}
		columnArtikul.cells.get(rowNum).labelText.is(nartart);
	}

	void flipGrid(){
		selectedRow = null;
		gridItems.clearColumns();
		String[] mDateFormatStrings = new String[]{
				"yyyy-MM-dd'T'HH:mm:ss"
				, "yyyy-MM-dd HH:mm:ss"
				, "yyyy-MM-dd",
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

		if(itemsData != null){
			/*try{
				System.out.println("start flipGrid");
				int kk=0;
				int mm=1/kk;
				System.out.println("mm "+mm);
			}catch(Throwable t){
				t.printStackTrace();
			}*/
			for(int ii = 0; ii < itemsData.children.size(); ii++){
				final Bough row = itemsData.children.get(ii).createClone();
				Task tap = new Task(){
					public void doTask(){
						selectedRow = row;
						lastSelectedArtikul = row.child("Artikul").value.property.value();
					}
				};
				int artikulBackground = 0;
				int naimenovanieBackGround = 0;
				int cenaBackground = 0;
				String LastSell = itemsData.children.get(ii).child("LastSell").value.property.value();
				if(LastSell != null){
					if(LastSell.length() > 0){
						try{
							java.util.Date d = DateUtils.parseDate(LastSell, mDateFormatStrings);
							java.util.Calendar now = Calendar.getInstance();
							now.set(Calendar.DAY_OF_MONTH, 1);
							now.add(Calendar.DAY_OF_MONTH, -1);
							if(d.before(now.getTime())){
								artikulBackground = 0xffff6666;
							}
						}catch(Throwable tr){
							//tr.printStackTrace();
						}
					}
				}
				double CENA = Numeric.string2double(itemsData.children.get(ii).child("Cena").value.property.value());
				double MIN_CENA = Numeric.string2double(itemsData.children.get(ii).child("MinCena").value.property.value());
				double MAX_CENA = Numeric.string2double(itemsData.children.get(ii).child("MaxCena").value.property.value());
				double BASE_PRICE = Numeric.string2double(itemsData.children.get(ii).child("BasePrice").value.property.value());
				double smartprice = Numeric.string2double(itemsData.children.get(ii).child("smartprice").value.property.value());
				if(itemsData.children.get(ii).child("mustListId").value.property.value().trim().length() > 0){
					naimenovanieBackGround = Settings.colorTop20;
				}
				String cenaNacenka = Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "р");
				String minCenaText = itemsData.children.get(ii).child("MinCena").value.property.value();
				String maxCenaText = itemsData.children.get(ii).child("MaxCena").value.property.value();
				if(!sweetlife.android10.ui.Activity_Bid.hideNacenkaStatus){
					int procent = (int)(100.0 * (CENA - BASE_PRICE) / BASE_PRICE);
					if(procent >= 25){
						cenaBackground = Settings.colorNacenka25;
					}
					cenaNacenka = Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "") + "р/" + procent + "%";
					if(MIN_CENA > 0){
						minCenaText = minCenaText + "/" + ((int)(100.0 * (MIN_CENA - BASE_PRICE) / BASE_PRICE)) + "%";
					}
					maxCenaText = maxCenaText + "/" + ((int)(100.0 * (MAX_CENA - BASE_PRICE) / BASE_PRICE)) + "%";
				}
				final String art = itemsData.children.get(ii).child("Artikul").value.property.value();
				final int nn = ii;
				Task arttap = new Task(){
					public void doTask(){
						selectedRow = row;
						toggleStart(art, nn);
					}
				};
				String stars_artikul = itemsData.children.get(ii).child("stars_artikul").value.property.value();
				String artLabe = art;
				if(art.trim().equals(stars_artikul.trim())){
					artLabe = "★ " + art;
				}
				columnArtikul.cell(artLabe
						, artikulBackground
						, arttap);
				columnNomenklatura.cell(itemsData.children.get(ii).child("Naimenovanie").value.property.value()
						, naimenovanieBackGround
						, tap);
				if(itemsData.children.get(ii).child("Naimenovanie").value.property.value().startsWith("СТМ:")){
					columnProizvoditel.cell(itemsData.children.get(ii).child("ProizvoditelNaimenovanie").value.property.value(), Settings.colorSTM, tap);
				}else{
					columnProizvoditel.cell(itemsData.children.get(ii).child("ProizvoditelNaimenovanie").value.property.value(), Settings.colorTransparent, tap);
				}
				//columnProizvoditel.cell(itemsData.children.get(ii).child("ProizvoditelNaimenovanie").value.property.value()
				//		, tap);
				columnMinKolichestvo.cell(itemsData.children.get(ii).child("MinNorma").value.property.value()
								+ "/" + itemsData.children.get(ii).child("Koephphicient").value.property.value()
						, tap
						, itemsData.children.get(ii).child("EdinicyIzmereniyaNaimenovanie").value.property.value());
				if(smartprice > 0){
					//MIN_CENA=0;
					columnCena.cell(//Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "р")
							cenaNacenka
							, Settings.colorSmartPro
							, tap
							, "" + smartprice + "р для SmartPro");
				}else{
					columnCena.cell(//Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Cena").value.property.value(), "р")
							cenaNacenka
							, cenaBackground
							, tap
							, minCenaText + " - " + maxCenaText);
				}

				columnSkidka.cell(Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("Skidka").value.property.value(), "р")
						, tap
						, itemsData.children.get(ii).child("VidSkidki").value.property.value());
				String lastDate = Auxiliary.tryReFormatDate(itemsData.children.get(ii).child("LastSell").value.property.value(), "yyyy-MM-dd", "dd.MM.yy");
				if(lastDate.length() > 0){
					columnPosledniaya.cell(lastDate
							, tap
							, Auxiliary.formatNonEmptyNumber(itemsData.children.get(ii).child("LastPrice").value.property.value(), "р")
									+ "/" + itemsData.children.get(ii).child("lastSellCount").value.property.value()
					);
				}else{
					columnPosledniaya.cell(" ", tap, "");
				}
			}
			if(itemsData.children.size() < 10){
				for(int ii = 0; ii < 10 - itemsData.children.size(); ii++){
					columnArtikul.cell("", 0xffffffff, null);
					columnNomenklatura.cell("", 0xffffffff, null);
					columnProizvoditel.cell("", 0xffffffff, null);
					columnMinKolichestvo.cell("", 0xffffffff, null, "");
					columnCena.cell("", 0xffffffff, null, "");
					columnSkidka.cell("", 0xffffffff, null, "");
					columnPosledniaya.cell("", 0xffffffff, null, "");
				}
			}

			//System.out.println("done flipGrid");
			//highlightLastSelectedRow();
		}
	}

	void highlightLastSelectedRow(){
		//System.out.println("highlightLastSelectedRow lastSelectedArtikul " + lastSelectedArtikul);
		//gridItems.scrollUp();
		for(int ii = 0; ii < itemsData.children.size(); ii++){
			Bough row = itemsData.children.get(ii).createClone();
			String art = itemsData.children.get(ii).child("Artikul").value.property.value();
			if(art.equals(lastSelectedArtikul)){
				//System.out.println("found " + ii);
				gridItems.tapColumnRow(ii, 1);

				return;
			}
		}
		//System.out.println("not found");
	}

	void resetItemsGrid(){
		//System.out.println("resetItemsGrid");
		new Expect().status.is("Поиск...").task.is(new Task(){
			public void doTask(){
				//System.out.println("resetItemsGrid start");
				requeryGridData();
			}
		}).afterDone.is(new Task(){
			public void doTask(){
				flipGrid();
				gridItems.refresh();
				highlightLastSelectedRow();
				//System.out.println("resetItemsGrid done offset " + gridOffset.value() + "/scroll " + gridItems.scrollView.getScrollY());

				if(requeryGridDataMessage.value().trim().length() > 0){
					Auxiliary.warn(requeryGridDataMessage.value(), Activity_NomenclatureNew.this);
					if(mode.value() == ShowViewPoisk){
						searchMode.value(1);
					}
					requeryGridDataMessage.value("");
				}

			}
		}).start(this);
	}

	void refreshCategotyGrid(){
		System.out.println("refreshCategotyGrid");
		gridCategoriesY = gridCategories.scrollView.getScrollY();
		gridCategories.clearColumns();
		String sql = "select cat1 as cat1,key1 as key1, cat2 as cat2,key2 as key2, cat3 as cat3,key3 as key3, cat4 as cat4,key4 as key4, cat5 as cat5,key5 as key5";
		sql = sql + "\n	from nomenklatura_groups allgroups where (rod1=x'00' and key2 is null)";
		if(level1.trim().length() > 0)
			sql = sql + "\n		or (rod2=x'" + level1 + "' and key3 is null)";
		if(level2.trim().length() > 0)
			sql = sql + "\n		or (rod3=x'" + level2 + "' and key4 is null)";
		if(level3.trim().length() > 0)
			sql = sql + "\n		or (rod4=x'" + level3 + "' and key5 is null)";
		if(level4.trim().length() > 0)
			sql = sql + "\n		or (rod5=x'" + level4 + "')";
		sql = sql + "\n	order by cat1,cat2,cat3,cat4,cat5;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println("sql: " + sql);
		System.out.println("rows: " + data.children.size());
		String lastKey1 = "";
		String lastKey2 = "";
		String lastKey3 = "";
		String lastKey4 = "";
		String lastKey5 = "";
		int rowCounter = 0;
		for(int ii = 0; ii < data.children.size(); ii++){
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
			if(curKey1.length() == 0){
				rowCounter++;
				columnCategories.cell(curCat1, color1, createCategoryTapTask(curKey1, "", "", "", ""));
			}else{
				if(curKey2.length() == 0){
					if(curKey1.toUpperCase().equals(level1.toUpperCase())){
						gridCategoriesY = Auxiliary.tapSize * rowCounter;
					}
					rowCounter++;
					columnCategories.cell(curCat1, color1, createCategoryTapTask(curKey1, "", "", "", ""));
				}else{
					if(curKey3.length() == 0){
						if(!curKey1.equals(lastKey1)){
							if(curKey1.toUpperCase().equals(level1.toUpperCase())){
								gridCategoriesY = Auxiliary.tapSize * rowCounter;
							}
							rowCounter++;
							columnCategories.cell(curCat1, color1);
						}
						rowCounter++;
						columnCategories.cell(curCat2, color2, createCategoryTapTask(curKey1, curKey2, "", "", ""));
					}else{
						if(curKey4.length() == 0){
							if(!curKey1.equals(lastKey1)){
								rowCounter++;
								columnCategories.cell(curCat1, color1);
							}
							if(!curKey2.equals(lastKey2)){
								if(curKey2.toUpperCase().equals(level2.toUpperCase())){
									gridCategoriesY = Auxiliary.tapSize * rowCounter;
								}
								rowCounter++;
								columnCategories.cell(curCat2, color2);
							}
							rowCounter++;
							columnCategories.cell(curCat3, color3, createCategoryTapTask(curKey1, curKey2, curKey3, "", ""));
						}else{
							if(curKey5.length() == 0){
								if(!curKey1.equals(lastKey1)){
									rowCounter++;
									columnCategories.cell(curCat1, color1);
								}
								if(!curKey2.equals(lastKey2)){
									rowCounter++;
									columnCategories.cell(curCat2, color2);
								}
								if(!curKey3.equals(lastKey3)){
									if(curKey3.toUpperCase().equals(level3.toUpperCase())){
										gridCategoriesY = Auxiliary.tapSize * rowCounter;
									}
									rowCounter++;
									columnCategories.cell(curCat3, color3);
								}
								rowCounter++;
								columnCategories.cell(curCat4, color4, createCategoryTapTask(curKey1, curKey2, curKey3, curKey4, ""));
							}else{
								if(!curKey1.equals(lastKey1)){
									rowCounter++;
									columnCategories.cell(curCat1, color1);
								}
								if(!curKey2.equals(lastKey2)){
									rowCounter++;
									columnCategories.cell(curCat2, color2);
								}
								if(!curKey3.equals(lastKey3)){
									rowCounter++;
									columnCategories.cell(curCat3, color3);
								}
								if(!curKey4.equals(lastKey4)){
									if(curKey4.toUpperCase().equals(level4.toUpperCase())){
										gridCategoriesY = Auxiliary.tapSize * rowCounter;
									}
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
	protected void onPause(){
		super.onPause();
		System.out.println("Activity_NomenclatureNew.onPause");
		Activity_NomenclatureNew.this.stopBackgroundActions = true;
		me = null;
		double newOffset = Math.floor(gridOffset.value() + this.gridItems.scrollView.getScrollY() / Auxiliary.tapSize);
		if(newOffset < 0){
			newOffset = 0;
		}
		gridOffset.value(newOffset);
		//filterByStar.value(false);
		//System.out.println("onPause " + newOffset + "/" + gridOffset.value() + "/" + this.gridItems.scrollView.getScrollY());
		//filterStmStarRecomendaciaKorzina.value(0);
	}

	@Override
	protected void onResume(){
		super.onResume();
		System.out.println("Activity_NomenclatureNew.onResume");
		Activity_NomenclatureNew.this.stopBackgroundActions = false;
		me = this;
		//doRefreshData.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		System.out.println("requestCode " + requestCode + ", resultCode " + resultCode);
		String filePath = null;
		Uri uri = null;
		if(intent != null){
			System.out.println("intent.getData() " + intent.getData());
			switch(requestCode){
				case ActivityWebServicesReports.etiketkaIDPicture:
					uri = intent.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать этикетку " + uri, Activity_NomenclatureNew.this);
						return;
					}
					ActivityWebServicesReports.etiketkaObratnayaSvyazKlient.value(filePath);
					break;
				case ActivityWebServicesReports.tovarIDPicture:
					uri = intent.getData();
					filePath = Auxiliary.pathForMediaURI(this, uri);
					if(filePath == null){
						Auxiliary.warn("Не удалось прочитать фото " + uri, Activity_NomenclatureNew.this);
						return;
					}
					ActivityWebServicesReports.tovarObratnayaSvyazKlient.value(filePath);
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
}
