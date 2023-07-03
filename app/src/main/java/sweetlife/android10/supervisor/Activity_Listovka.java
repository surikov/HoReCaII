package sweetlife.android10.supervisor;

import android.app.Activity;

import android.view.*;
import android.content.*;
import android.os.Bundle;

import java.util.*;

import android.database.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;

//import reactive.ui.OrderItemInfo;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import sweetlife.android10.utils.DateTimeHelper;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

import reactive.ui.Layoutless;

class ListCellInfo {
	public ListCellInfo(String artikul, String name, String edizm, double price, double skidka) {
		this.artikul = artikul;
		this.name = name;
		this.price = price;
		this.edizm = edizm;
		this.skidka = skidka;
	}

	String name = "";
	String artikul = "";
	String edizm = "";
	double price = 0.0;
	double skidka = 0.0;
}

public class Activity_Listovka extends Activity implements ITableColumnsNames {
	Layoutless layoutless;
	WebRender brwsr;
	String path = "/sdcard/horeca/listovka.html";
	//String pathCat = "/sdcard/horeca/listovka.html";

	int productPageSize = 30;
	Numeric productOffset = new Numeric();

	String dataOtgruzki = "0";
	String clientID = "x'82b90050568b3c6811e8b04a065686f9'";
	String polzovatelID = "x'bba320677c60fed011e9262ba38aa289'";
	String sklad = "0";

	ArrayList<ListCellInfo> cellRows = new ArrayList<ListCellInfo>();

	Bough catData = new Bough();
	boolean mIsCRAvailable = false;

	MenuItem menuSortABC;
	MenuItem menuSortPrice;
	MenuItem menuSortSkidka;
	int sortABC = 111;
	int sortPrice = 222;
	int sortSkidka = 333;
	int sortMode = sortABC;
	String selectedCat = null;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuSortABC = menu.add("По алфавиту");
		menuSortPrice = menu.add("По цене");
		menuSortSkidka = menu.add("По скидке");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuSortABC) {
			sortMode = sortABC;
			if (selectedCat == null) {
				this.fillGUI(true);
			} else {
				this.fillCat(true);
			}
			return true;
		}
		if (item == menuSortPrice) {
			sortMode = sortPrice;
			if (selectedCat == null) {
				this.fillGUI(true);
			} else {
				this.fillCat(true);
			}
			return true;
		}
		if (item == menuSortSkidka) {
			sortMode = sortSkidka;
			if (selectedCat == null) {
				this.fillGUI(true);
			} else {
				this.fillCat(true);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle("Газета (листовка)");
		Preferences.init(this);
		Bough b = Auxiliary.activityExatras(this);
		System.out.println(b.dumpXML());
		dataOtgruzki = b.child("dataOtgruzki").value.property.value();
		clientID = b.child("clientID").value.property.value();
		polzovatelID = b.child("polzovatelID").value.property.value();
		sklad = b.child("sklad").value.property.value();
		createGUI();
		//final Vector<Bough> catData = new Bough();
		Expect requery = new Expect().status.is("Ожидание ответа").task.is(new Task() {
			@Override
			public void doTask() {
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()//
						+ "/hs/Planshet/GetBannerForTablet/"//
						//+ ApplicationHoreca.getInstance().hrcSelectedRoute();
				+Cfg.selectedOrDbHRC();
				System.out.println(url);
				try {
					byte[] b = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String s = new String(b, "utf-8");
					System.out.println("GetBannerForTablet "+s);
					Bough d = Bough.parseJSON("{row:" + s + "}");
					//System.out.println(d.dumpXML());
					catData.children = d.children;
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				fillGUI(false);
			}
		});
		requery.start(this);
        /*String html = this.htmlPageHead();
        for (int i = 0; i < 9; i++) {
            html = html + this.htmlCell("artikul","cena","info");
        }
        html = html + this.htmlPageBottom();*/

	}

	void fillGUI(boolean fromCache) {
		String html = composeData(fromCache);
		//System.out.println(html);
		Auxiliary.writeTextToFile(new File(path), html, "utf-8");
		brwsr.go("file://" + path);
	}

	void fillCat(boolean fromCache) {
		System.out.println("fillCat " + selectedCat);
		mIsCRAvailable = true;
		String html = composeCat(fromCache);
		Auxiliary.writeTextToFile(new File(path), html, "utf-8");
		brwsr.go("file://" + path);
	}

	void readCellsData(boolean fromCache) {
		if (!fromCache) {
			cellRows.clear();
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
			String sql = Request_NomenclatureBase.composeSQLall_Old(//
					dataOtgruzki//
					, clientID//
					, polzovatelID//
					, ""//
					, ""//
					, "newSkidki.comment='Газета/листовка'"//
					, ISearchBy.SEARCH_CUSTOM//
					, false//
					, false//
					, sklad//
					, 200//
					, 0
					, false//
					, false//
					, null
					, null
					, false
					, false
					, null
					, null
					, null
			);
			//System.out.println(sql);
			final Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(b.dumpXML());
			for (int i = 0; i < b.children.size(); i++) {
				Bough row = b.children.get(i);
				cellRows.add(new ListCellInfo(
						row.child("Artikul").value.property.value()
						, row.child("Naimenovanie").value.property.value()
						, row.child("EdinicyIzmereniyaNaimenovanie").value.property.value()
						, Numeric.string2double(row.child("Cena").value.property.value())
						, Numeric.string2double(row.child("CenaSoSkidkoy").value.property.value())

				));
			/*
			html = html + this.htmlCell(row.child("Artikul").value.property.value()
					, row.child("CenaSoSkidkoy").value.property.value() + "р. / " + row.child("EdinicyIzmereniyaNaimenovanie").value.property.value()
					, row.child("Naimenovanie").value.property.value());
			*/
			}
		}
	}

	String gridCellsHTML() {
		String html = "";
		Collections.sort(cellRows, new Comparator<ListCellInfo>() {
			public int compare(ListCellInfo s1, ListCellInfo s2) {
				try {
					if (sortMode == sortABC) {
						return s1.name.compareTo(s2.name);
					} else {
						if (sortMode == sortPrice) {
							return (int) (s1.skidka - s2.skidka);
						} else {
							return (int) (100 * ((s1.skidka / s1.price) - (s2.skidka / s2.price)));
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return 0;
			}
		});
		for (int i = 0; i < cellRows.size(); i++) {
			double skidka=Math.round(cellRows.get(i).skidka);
			double cena=Math.round(cellRows.get(i).price);
			double ratio=Math.round(100*skidka/cena);
			//System.out.println(cellRows.get(i).artikul+" - "+skidka+" / "+cena+" : "+ratio);
			html = html + this.htmlCell(
					cellRows.get(i).artikul
					, "" + cellRows.get(i).price + "р. / " + cellRows.get(i).edizm
					, "" + cellRows.get(i).skidka + "р. / " + cellRows.get(i).edizm

					//ratio+ " = " + skidka + " :  " + cena + " /" + sortMode
					, cellRows.get(i).name);
		}
		return html;
	}

	String composeData(boolean fromCache) {
		String html = this.htmlPageHead(false);
		readCellsData(fromCache);
		html = html + gridCellsHTML();
		//System.out.println(b.dumpXML());
		html = html + this.htmlPageBottom();
		return html;
	}

	void readCatData(boolean fromCache) {
		if (!fromCache) {
			cellRows.clear();
			String artikuls = "false";
			for (int i = 0; i < this.catData.children.size(); i++) {
				if (this.catData.children.get(i).child("НомерДокумента").value.property.value().trim().equals(selectedCat.trim())) {
					Vector<Bough> arts = this.catData.children.get(i).children("Товары");
					for (int nn = 0; nn < arts.size(); nn++) {
						String aa = arts.get(nn).child("Артикул").value.property.value();
						artikuls = artikuls + " or n.artikul='" + aa + "'";
					}
				}
			}
			String sql = Request_NomenclatureBase.composeSQLall_Old(//
					dataOtgruzki//
					, clientID//
					, polzovatelID//
					, ""//
					, ""//
					, artikuls//
					, ISearchBy.SEARCH_CUSTOM//
					, false//
					, false//
					, sklad//
					, 200//
					, 0
					, false//
					, false//
					, null
					, null
					, false
					, false
					, null
					, null
					, null
			);
			final Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			System.out.println(b.dumpXML());
			for (int i = 0; i < this.catData.children.size(); i++) {
				if (this.catData.children.get(i).child("НомерДокумента").value.property.value().trim().equals(selectedCat.trim())) {
					Vector<Bough> arts = this.catData.children.get(i).children("Товары");
					for (int nn = 0; nn < arts.size(); nn++) {
						String artikul = arts.get(nn).child("Артикул").value.property.value().trim();
						String name = "?";
						double price = 0.0;
						double skidka = 0.0;
						String edizm = "?";
						for (int rr = 0; rr < b.children.size(); rr++) {
							if (b.children.get(rr).child("Artikul").value.property.value().trim().equals(artikul)) {
								name = b.children.get(rr).child("Naimenovanie").value.property.value().trim();
								skidka = Numeric.string2double(b.children.get(rr).child("CenaSoSkidkoy").value.property.value());
								price = Numeric.string2double(b.children.get(rr).child("Cena").value.property.value());
								edizm = b.children.get(rr).child("EdinicyIzmereniyaNaimenovanie").value.property.value().trim();
								break;
							}
						}
						//html = html + this.htmlCell(artikul, price, name);
						cellRows.add(new ListCellInfo(
								artikul
								, name
								, edizm
								, price
								, skidka
						));
					}
				}
			}
		}
	}


	String composeCat(boolean fromCache) {
		this.setTitle("Баннер №" + selectedCat);
		String html = this.htmlPageHead(true);
		readCatData(fromCache);
		html = html + gridCellsHTML();
		html = html + this.htmlPageBottom();
		return html;
	}

	void createGUI() {

		int winW = Auxiliary.screenWidth(this);
		int winH = Auxiliary.screenWidth(this);
		brwsr = new WebRender(this).afterLink.is(new Task() {
			@Override
			public void doTask() {
				final android.net.Uri uri = android.net.Uri.parse(brwsr.url.property.value());
				//System.out.println("uri " + uri);
				String num = ("" + uri).split("=")[1];
				if (uri.toString().indexOf("selectArtikul") > -1) {
					selectArtikul(num);
				} else {
					selectedCat = num;
					fillCat(false);
				}
			}
		});
		layoutless.child(brwsr//
				.width().is(winW)//
				.height().is(winH)//
		);
	}


	void selectArtikul(String art) {
		System.out.println("tap " + art);
		String sql = Request_NomenclatureBase.composeSQL(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, art//
				, ISearchBy.SEARCH_ARTICLE//
				, false//
				, false//
				, sklad//
				, 3 * productPageSize//
				, productOffset.value().intValue()
				, false
				, false
				, false
				, null, null
		);
		Cursor cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Intent resultIntent = new Intent();
			String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
//            boolean mIsCRAvailable=false;
			if (Request_NomenclatureBase.getMinCena(cursor) == null || !mIsCRAvailable) {
				//System.out.println(": 1");
				resultIntent.putExtra(MIN_CENA, 0.00D);
				resultIntent.putExtra(MAX_CENA, 0.00D);
				if (vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
				) {
					//System.out.println(": 2");
					resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
				} else {
					System.out.println(": 3");
					resultIntent.putExtra(VID_SKIDKI, "x'00'");
					resultIntent.putExtra(CENA_SO_SKIDKOY, 0.00D);
				}
			} else {
				//System.out.println(": 4");
				if (vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
				) {
					//System.out.println(": 5");
					resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
				} else {
					//System.out.println(": 6");
					resultIntent.putExtra(VID_SKIDKI, "x'00'");
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCena(cursor)));
				}
				resultIntent.putExtra(MIN_CENA, Double.parseDouble(Request_NomenclatureBase.getMinCena(cursor)));
				resultIntent.putExtra(MAX_CENA, Double.parseDouble(Request_NomenclatureBase.getMaxCena(cursor)));
			}
			String sale = Request_NomenclatureBase.getSkidka(cursor);
			//System.out.println(": 7");
			if (sale != null) {
				//System.out.println(": 8");
				try {
					resultIntent.putExtra(SKIDKA, Double.parseDouble(sale));
				} catch (Throwable t) {
					System.out.println(t.getMessage());
					resultIntent.putExtra(SKIDKA, 0.00D);
				}
			} else {
				resultIntent.putExtra(SKIDKA, 0.00D);
			}
			resultIntent.putExtra(CENA, Double.parseDouble(Request_NomenclatureBase.getCena(cursor)));
			NomenclatureCountHelper helper = new NomenclatureCountHelper(Double.parseDouble(Request_NomenclatureBase.getMinNorma(cursor)), Double.parseDouble(Request_NomenclatureBase.getKoephphicient(cursor)));
			resultIntent.putExtra(COUNT, helper.ReCalculateCount(0));
			resultIntent.putExtra(NOMENCLATURE_ID, Request_NomenclatureBase.getIDRRef(cursor));
			resultIntent.putExtra(NAIMENOVANIE, Request_NomenclatureBase.getNaimenovanie(cursor));
			resultIntent.putExtra(ARTIKUL, Request_NomenclatureBase.getArtikul(cursor));
			resultIntent.putExtra(OSNOVNOY_PROIZVODITEL, Request_NomenclatureBase.getProizvoditelID(cursor));
			resultIntent.putExtra(EDINICY_IZMERENIYA_ID, Request_NomenclatureBase.getEdinicyIzmereniyaID(cursor));
			resultIntent.putExtra(EDINICY_IZMERENIYA_NAIMENOVANIE, Request_NomenclatureBase.getEdinicyIzmereniyaNaimenovanie(cursor));
			resultIntent.putExtra(KOEPHICIENT, Double.parseDouble(Request_NomenclatureBase.getKoephphicient(cursor)));
			resultIntent.putExtra(MIN_NORMA, Double.parseDouble(Request_NomenclatureBase.getMinNorma(cursor)));
			resultIntent.putExtra(BASE_PRICE, Request_NomenclatureBase.getBasePrice(cursor));
			resultIntent.putExtra(LAST_PRICE, Request_NomenclatureBase.getLastPrice(cursor));

			setResult(RESULT_OK, resultIntent);
			finish();
		}
	}

	String htmlPageHead(boolean noCat) {
		String html = "<html>\n" +
				"\n" +
				"<head>\n" +
				"	<title>HoReCa</title>\n" +
				"	<meta charset=\"utf-8\">\n" +
				"	<meta name=\"viewport\" \n" +
				"		content=\"initial-scale=1.0, width=device-width, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0, shrink-to-fit=no\" />\n" +
				"	<style>\n" +
				"		html{\n" +
				"			font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;\n" +
				"			font-size: 0.33cm;\n" +
				"			background-color: #fff;\n" +
				"		}\n" +
				"		.mainDiv {\n" +
				"		  	display: flex;\n" +
				"			flex-wrap: wrap;\n" +
				"			align-content: flex-start;\n" +
				"			justify-content: center;\n" +
				"		}\n" +
				"		.bigCell {\n" +
				"			padding: 0cm;\n" +
				"			margin: 0.25cm;\n" +
				"			display: flex;\n" +
				"			flex-direction: row\n" +
				"		}\n" +
				"		.bigImg {\n" +
				"			max-width:15cm;\n" +
				"			max-height:5cm;\n" +
				"			padding: 0cm;\n" +
				"			margin: 0cm;\n" +
				"		}\n" +
				"		.oneCell {\n" +
				"			width2: 33%;\n" +
				"			border-radius: 0.25cm;\n" +
				"			border: 1px solid #ccc;\n" +
				"			padding: 0.5cm;\n" +
				"			margin: 0.25cm;\n" +
				"			display: flex;\n" +
				"			flex-direction: row\n" +
				"		}\n" +
				"		.cellImg{\n" +
				"			max-width:3cm;\n" +
				"			max-height:3cm;\n" +
				"		}\n" +
				"		.imgColumn{\n" +
				"			width:3cm;\n" +
				"			height:3cm;\n" +
				"			background-color2: #fcc;\n" +
				"			padding: 0cm;\n" +
				"			margin: 0cm;\n" +
				"			border2: 1px solid #ccc;\n" +
				"		}\n" +		
				"		.infoColumn{\n" +
				"			width:4cm;\n" +
				"			padding-left: 0.25cm;\n" +
				"			display: flex;\n" +
				"		  	flex-direction: column;\n" +
				"		}\n" +
				"		.countCell{\n" +
				"			margin-top: auto;\n" +
				"		}\n" +
				"		.priceOld{\n" +
				"			text-decoration: line-through;\n" +
				"			color: #999999;\n" +
				"		}\n" +
				"		.priceNew{\n" +
				"			color: #c36;\n" +
				"		}\n" +
				"		a {\n" +
				"		    color: #000;\n" +
				"		}\n" +
				"		a:link {\n" +
				"		    text-decoration: none;\n" +
				"		}\n" +
				"\n" +
				"		a:visited {\n" +
				"		    text-decoration: none;\n" +
				"		}\n" +
				"\n" +
				"		a:hover {\n" +
				"		    text-decoration: none;\n" +
				"		}\n" +
				"\n" +
				"		a:active {\n" +
				"		    text-decoration: none;\n" +
				"		}\n" +
				"	</style>\n" +
				"</head>\n" +
				"\n" +
				"<body>\n";
		if (!noCat) {
			html = html + "	<div class='mainDiv'>" +
					htmlCat() +
					"	</div>";
		}
		html = html + "	<div class='mainDiv'>";
		return html;
	}

	String htmlCat() {
		/*
		String html = "\n<a href='selectCat?cat=123'>\n" +
				"			<div class=\"bigCell\">\n" +
				"				<img class='bigImg' src='https://httpfiles.swlife.ru/Img_TabPrev8aa5fc1e-6c26-48fa-804c-3508d5ea6e6a.JPEG' />\n" +
				"			</div>\n" +
				"		</a>" +
				"\n<a href='selectCat?cat=123'>\n" +
				"			<div class=\"bigCell\">\n" +
				"				<img class='bigImg' src='https://swlife.ru/image/catalog/custom/home-top-2.jpg' />\n" +
				"			</div>\n" +
				"		</a>";
		*/
		String html = "";
		for (int i = 0; i < catData.children.size(); i++) {
			html = html + "\n<a href='fillCat?cat=" + catData.children.get(i).child("НомерДокумента").value.property.value() + "'>\n" +
					"			<div class=\"bigCell\">\n" +
					"				<img class='bigImg' src='" + catData.children.get(i).child("УРЛПревью").value.property.value() + "' />\n" +
					"			</div>\n" +
					"		</a>";
		}
		return html;
	}

	String htmlCell(String artikul, String old, String cena, String info) {
		String html = "\n		<a href='selectArtikul?art=" + artikul + "'>\n" +
				"			<div class=\"oneCell\">\n" +
				"				<div class='imgColumn'>\n" +
				//"					<img class='cellImg' src='http://89.109.7.162/GolovaNew/hs/Prilozhenie/ПревьюФотоНоменклатуры?Артикул=" + artikul + "' />\n" +
				"					<img class='cellImg' src='https://files.swlife.ru/photo/" + artikul + ".jpg' />\n" +
				"				</div>\n" +
				"				<div class='infoColumn'>\n" +
				"					<div class='artikulInfo'>" + info + "</div>\n" +
				"					<div class='countCell'>\n" +
				"						<div class='priceOld'>" + old + "</div>\n" +
				"						<div class='priceNew'>" + cena + "</div>\n" +
				//"						<div class='buyCell'>- 8 +</div>\n" +
				"					</div>\n" +
				"				</div>\n" +
				"			</div>\n" +
				"		</a>";
		return html;
	}

	String htmlPageBottom() {
		String html = "\n	</div>\n" +
				"</body>\n" +
				"\n" +
				"</html>";
		return html;
	}
}
