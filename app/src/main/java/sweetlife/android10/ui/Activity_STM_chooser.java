package sweetlife.android10.ui;

import reactive.ui.*;
import reactive.ui.Auxiliary;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.*;

import tee.binding.*;
import tee.binding.task.*;
import tee.binding.it.*;

public class Activity_STM_chooser extends Activity_Base implements ITableColumnsNames {
	Layoutless layoutless;
	DataGrid brandGrid;

	ColumnText columnBrandName;
	ColumnText columnPadBrand;
	//ColumnDescription columnProduct;

	DataGrid productGrid;
	ColumnText columnProductArtikul;
	ColumnText columnProductNaimenovanie;
	ColumnDescription columnProductProizvoditel;
	ColumnDescription columnProductCena;

	//Note filterSegment = new Note();
	//Note filterProduct = new Note();
	int productPageSize = 30;
	Bough productData;
	Numeric productOffset = new Numeric();

	String brandId="";
	//int receptPageSize = 300;
	//Bough receptData;
	//Numeric receptOffset = new Numeric();
	static String selectedSegmentKod = null;
	//static String selectedIngredientIdrref = null;
	//static String selectedIngredientKluch = null;

	String dataOtgruzki = "0";
	String clientID = "x'82b90050568b3c6811e8b04a065686f9'";
	String polzovatelID = "x'bba320677c60fed011e9262ba38aa289'";
	String sklad = "0";

	boolean mIsCRAvailable = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("СТМ");
		Preferences.init(this);
		Bough b = Auxiliary.activityExatras(this);
		System.out.println(b.dumpXML());
		dataOtgruzki = b.child("dataOtgruzki").value.property.value();
		clientID = b.child("clientID").value.property.value();
		polzovatelID = b.child("polzovatelID").value.property.value();
		sklad = b.child("sklad").value.property.value();
		mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);
		sweetlife.android10.supervisor.Cfg.refreshArtikleCount();
		createGUI();
		requeryBrands();
	}


	void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		brandGrid = new DataGrid(this);
		productGrid = new DataGrid(this);
		columnBrandName = new ColumnText();
		columnPadBrand = new ColumnText();

		columnProductArtikul = new ColumnText();
		columnProductNaimenovanie = new ColumnText();
		columnProductProizvoditel = new ColumnDescription();
		columnProductCena = new ColumnDescription();
		/*layoutless.child(new RedactText(this).text.is(filterSegment)
				.width().is(Auxiliary.tapSize * 7)//
				.height().is(Auxiliary.tapSize * 1)
		);*/
		layoutless.child(brandGrid.noHead.is(true)//
				//.center.is(true)//
				.columns(new Column[]{
						columnBrandName.noVerticalBorder.is(true).title.is(" ").width.is(Auxiliary.tapSize * 1) //
						, columnPadBrand.noVerticalBorder.is(true).title.is("Бренд").width.is(Auxiliary.tapSize * 6) //
				})//
				.pageSize.is(500)//
				.dataOffset.is(0)//
				.top().is(0 * Auxiliary.tapSize)
				.width().is(Auxiliary.tapSize * 7)//
				.height().is(layoutless.height().property.minus(0 * Auxiliary.tapSize))//
		);
		/*layoutless.child(new RedactText(this).text.is(filterProduct)
				.width().is(layoutless.width().property.minus(Auxiliary.tapSize * 9))//
				.left().is(Auxiliary.tapSize * 7)
				.height().is(Auxiliary.tapSize * 1)
		);*/

		layoutless.child(productGrid//
				//.center.is(true)//
				.columns(new Column[]{
						columnProductArtikul.title.is("Артикул").width.is(Auxiliary.tapSize * 1.5) //
						, columnProductNaimenovanie.title.is("Наименование").width.is(Auxiliary.tapSize * 7) //(layoutless.width().property.minus(Auxiliary.tapSize * 13.5)) //
						, columnProductProizvoditel.title.is("Производитель").width.is(Auxiliary.tapSize * 3) //
						, columnProductCena.title.is("Цена").width.is(Auxiliary.tapSize * 2) //
				})//
				.pageSize.is(productPageSize)//
				.dataOffset.is(productOffset)//
				.beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						nextProductData();
					}
				})
				.left().is(Auxiliary.tapSize * 7)
				.top().is(0*Auxiliary.tapSize)
				.width().is(layoutless.width().property.minus(Auxiliary.tapSize * 7))//
				.height().is(layoutless.height().property.minus(0*Auxiliary.tapSize))//
		);
		/*layoutless.child(new Knob(this).labelText.is("Искать")
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						requerySegments();
					}
				})
				.width().is(Auxiliary.tapSize * 2)//
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2))
				.height().is(Auxiliary.tapSize * 1)
		);*/
	}

	public void requeryBrands() {
		int nn = brandGrid.scrollView.getScrollY();
		brandGrid.clearColumns();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		String thismonth = Auxiliary.sqliteDate.format(c.getTime());
		String sql = "select br._idrref as idr, br.naimenovanie as name, count(br._idrref) as cnt"
				+ "\n	from brand br"
				+ "\n	join nomenklatura_sorted nn on nn.brand=br._idrref"
				+ "\n	cross join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=nn.[_IDRRef]"
				+ "\n	where curAssortiment.zapret!=x'01' and br.stm=x'01'"
				+ "\n	group by br._idrref"
				+ "\n	order by br.naimenovanie;";
		System.out.println("requerySegments " + sql);
		Bough segmentData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String _id = "";
		for (int i = 0; i < segmentData.children.size(); i++) {
			Bough row = segmentData.children.get(i);
			final String idr=row.child("idr").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					//requeryNomenklatura(idr);
					brandId=idr;
					productOffset.value(0);
					nextProductData();
					productGrid.refresh();
				}
			};
			columnBrandName.cell(row.child("cnt").value.property.value(),0x11000000,tap);
			columnPadBrand.cell(row.child("name").value.property.value(),0x11000000,tap);

		}
		brandGrid.refresh();
		brandGrid.scrollView.scrollTo(0, nn);
		if (selectedSegmentKod != null) {
			productOffset.value(0);
		}
	}
	void nextProductData(){
		System.out.println("requeryNomenklatura brandID "+brandId);
		productGrid.clearColumns();
		String sql = Request_NomenclatureBase.composeSQLall_Old(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, " n.brand = x'"+brandId+"'"//
				, ISearchBy.SEARCH_CUSTOM//
				, false//
				, false//
				, sklad//
				, 3 * productPageSize//
				, productOffset.value().intValue()
				, false
				, false
				, null, null, false
				, false
				, null, null, null
				,true
				,false
				,false
				,false
		);
		System.out.println("nextProductData " + sql);
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		Date lostedLimit = new Date();
		lostedLimit.setDate(1);
		lostedLimit.setHours(0);
		lostedLimit.setMinutes(1);
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			//System.out.println(row.dumpXML());
			final String art = row.child("Artikul").value.property.value();
			Task tap3 = new Task() {
				@Override
				public void doTask() {


					selectArtikul(art);
				}
			};
			int bg = 0x00000000;
			/*if (Numeric.string2double(row.child("LastPrice").value.property.value()) > 0) {
				bg = 0xffdddddd;
			}*/
			String compArt = art;
			if (row.child("LastSell").value.property.value().trim().length() > 0) {
				bg = 0xffdddddd;
				SimpleDateFormat sdf = new SimpleDateFormat(("yyyy-MM-dd"));
				Date val = new Date();
				try {
					val = sdf.parse(row.child("LastSell").value.property.value());
				} catch (Throwable tt) {
					tt.printStackTrace();
				}
				if (val.before(lostedLimit)) {
					bg = 0xffff6699;
				}
				//compArt = art + " " + row.child("LastSell").value.property.value();
			} else {
				if (row.child("artCount").value.property.value().length() > 0) {
					bg = 0xffddffdd;
				}
			}
            /*columnProduct.cell(
                    row.child("Artikul").value.property.value() + ": " + row.child("Naimenovanie").value.property.value()
                    ,tap3
                    , row.child("ProizvoditelNaimenovanie").value.property.value() + ": " + row.child("EdinicyIzmereniyaNaimenovanie").value.property.value()
            );*/


			columnProductArtikul.cell(compArt, bg, tap3);
			columnProductNaimenovanie.cell(row.child("Naimenovanie").value.property.value(), bg, tap3);
			columnProductProizvoditel.cell(row.child("ProizvoditelNaimenovanie").value.property.value(), bg
					, tap3, row.child("MinNorma").value.property.value() + " " + row.child("EdinicyIzmereniyaNaimenovanie").value.property.value());
			columnProductCena.cell(row.child("Cena").value.property.value() + "р", bg
					, tap3, row.child("MinCena").value.property.value() + " / " + row.child("MaxCena").value.property.value());
		}
	}

	void selectArtikul(String art) {
		System.out.println("tap " + art);
        /*String sql = Request_NomenclatureBase.composeSQL(//
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
        );*/
		String sql = Request_NomenclatureBase.composeSQLall(//
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
				, 0 //productOffset.value().intValue()
				, false
				, false
				, null
				, null
				, false
				, false
				, null
				, null
				, this.selectedSegmentKod
		);
		Cursor cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Intent resultIntent = new Intent();
			String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
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
}
