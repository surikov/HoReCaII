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

public class Activity_FlagmanChooser extends Activity_Base implements ITableColumnsNames{
	Layoutless layoutless;
	DataGrid segmentGrid;
	DataGrid productGrid;
	ColumnDescription columnSegment;
	ColumnText columnPadSegment;
	//ColumnDescription columnProduct;
	ColumnText columnProductArtikul;
	ColumnText columnProductNaimenovanie;
	ColumnDescription columnProductProizvoditel;
	ColumnDescription columnProductCena;
	Note filterSegment = new Note();
	Note filterProduct = new Note();
	int productPageSize = 30;
	Bough productData;
	Numeric productOffset = new Numeric();
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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setTitle("Флагманские позиции");
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
		requerySegments();
	}


	void createGUI(){
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		segmentGrid = new DataGrid(this);
		productGrid = new DataGrid(this);
		columnSegment = new ColumnDescription();
		columnPadSegment = new ColumnText();
		columnProductArtikul = new ColumnText();
		columnProductNaimenovanie = new ColumnText();
		columnProductProizvoditel = new ColumnDescription();
		columnProductCena = new ColumnDescription();
		layoutless.child(new RedactText(this).text.is(filterSegment)
				.width().is(Auxiliary.tapSize * 7)//
				.height().is(Auxiliary.tapSize * 1)
		);
		layoutless.child(segmentGrid//
				//.center.is(true)//
				.columns(new Column[]{
						columnPadSegment.noVerticalBorder.is(true).title.is(" ").width.is(Auxiliary.tapSize * 0.5) //
						, columnSegment.noVerticalBorder.is(true).title.is("Флагманы").width.is(Auxiliary.tapSize * 6.5) //
				})//
				.pageSize.is(500)//
				.dataOffset.is(0)//
				.top().is(Auxiliary.tapSize)
				.width().is(Auxiliary.tapSize * 7)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		layoutless.child(new RedactText(this).text.is(filterProduct)
				.width().is(layoutless.width().property.minus(Auxiliary.tapSize * 9))//
				.left().is(Auxiliary.tapSize * 7)
				.height().is(Auxiliary.tapSize * 1)
		);
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
				.beforeFlip.is(new Task(){
					@Override
					public void doTask(){
						nextProductData();
					}
				})
				.left().is(Auxiliary.tapSize * 7)
				.top().is(Auxiliary.tapSize)
				.width().is(layoutless.width().property.minus(Auxiliary.tapSize * 7))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this).labelText.is("Искать")
				.afterTap.is(new Task(){
					@Override
					public void doTask(){
						requerySegments();
					}
				})
				.width().is(Auxiliary.tapSize * 2)//
				.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2))
				.height().is(Auxiliary.tapSize * 1)
		);
	}

	void addFlagman(Bough row){
		final String sel_id = row.child("kod").value.property.value();
		int bg = 0x00000000;
		if(sel_id.equals(selectedSegmentKod)){
			bg = 0x22000000;
		}

		Task tap = new Task(){
			@Override
			public void doTask(){
				selectedSegmentKod = sel_id;
				//selectedIngredientIdrref = null;
				//selectedIngredientKluch = null;
				//System.out.println("rece " +selectedRecId);
				requerySegments();
			}
		};
		String fakt = "0";
		if(row.child("whart").value.property.value().length() > 0){
			fakt = row.child("artcnt").value.property.value();
		}
		columnSegment.cell("" + row.child("name").value.property.value(), bg, tap
				, "план: " + row.child("plan").value.property.value()
						+ ", факт: " + fakt);
		columnPadSegment.cell("", bg);
		//System.out.println(row.dumpXML());

        /*final String sel_id = row.child("recid").value.property.value();
        Task tap = new Task() {
            @Override
            public void doTask() {
                selectedRecId = sel_id;
                selectedIngredientIdrref = null;
                selectedIngredientKluch = null;
                //System.out.println("rece " +selectedRecId);
                requerySegments();
            }
        };
        int bg = 0x00000000;
        if (row.child("recid").value.property.value().equals(selectedRecId)) {
            bg = 0x22000000;
        }
        columnPadRecept.cell("", bg);
        columnRecept.cell("" + row.child("name").value.property.value(), bg, tap, "");
        if (row.child("recid").value.property.value().equals(selectedRecId)) {
            if (selectedIngredientIdrref == null) {
                //System.out.println("now " +selectedIngredientId);
                selectedIngredientIdrref = row.child("ingidrref").value.property.value();
                selectedIngredientKluch = row.child("ingkluch").value.property.value();
            }
            addIngredient(row);
        }*/
	}

	/*
		void addIngredient(Bough row) {
			final String idr = row.child("ingidrref").value.property.value();
			final String iky = row.child("ingkluch").value.property.value();
			Task tap2 = new Task() {
				@Override
				public void doTask() {
					//selectedRecId=sel_id;
					//requeryRecept();
					//selectedIngredientId = iid;
					//System.out.println("ingr " +selectedIngredientId);
					selectedIngredientIdrref = idr;
					selectedIngredientKluch = iky;
					requerySegments();
				}
			};
			int bg = 0x11000000;
			if (selectedIngredientIdrref.equals(idr) && selectedIngredientKluch.equals(iky)) {
				bg = 0x22006699;
			}
			columnPadRecept.cell("", bg);
			columnRecept.cell("", bg, tap2, "" + row.child("ingname").value.property.value());
			//System.out.println(iid+" - " +selectedIngredientId+" / "+(selectedIngredientId==iid));
		}
	*/
	public void requerySegments(){
		int nn = segmentGrid.scrollView.getScrollY();

		segmentGrid.clearColumns();
        /*
        String sql = "select SegmentKod as kod,plan as plan,SegmentName as name from PlanSegmentov"
                + "\n       join Podrazdeleniya on Podrazdeleniya.Kod=territorykod"
                + "\n       join Polzovateli on Polzovateli.Podrazdelenie=Podrazdeleniya._IDRRef"
                + "\n    where Polzovateli.Kod='" + ApplicationHoreca.getInstance().hrcSelectedRoute() + "'"
                ;
        if (this.filterSegment.value().length() > 0) {
            sql = sql + "\n    and SegmentName like '%" + this.filterSegment.value() + "%'";
        }
        sql = sql + "\n    order by SegmentName;";
        */
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		String thismonth = Auxiliary.sqliteDate.format(c.getTime());
        /*
        String sql = "select PlanSegmentov.SegmentKod as kod,PlanSegmentov.plan as plan,SegmentName as name , ifnull(art,0) as artcnt "
                + "\n 	from PlanSegmentov "
                + "\n        join Podrazdeleniya on Podrazdeleniya.Kod=PlanSegmentov.territorykod "
                + "\n        join Polzovateli on Polzovateli.Podrazdelenie=Podrazdeleniya._IDRRef "
                + "\n 	   left join ( "
                + "\n 	   	select plan.SegmentKod as segmentkod,count(nn.Artikul) as art "
                + "\n 			from Kontragenty kk "
                + "\n 				join DogovoryKontragentov_strip dk on dk.vladelec=kk._idrref "
                + "\n 				join Prodazhi_last as pp on dk._idrref=pp.dogovorkontragenta and pp.period>=date('" + thismonth + "') "
                + "\n 				join Nomenklatura nn on nn._idrref=pp.nomenklatura "
                + "\n 				join FlagmanTovar flag on flag.Articul=nn.Artikul "
                + "\n 				join PlanSegmentov plan on plan.SegmentKod=flag.SegmentKod "
                + "\n 				join Podrazdeleniya podr on plan.territoryKod=podr.kod and podr._idrref=dk.podrazdelenie "
                + "\n 				join Polzovateli plz on plz.podrazdelenie=podr._IDRRef and plz.kod='" + ApplicationHoreca.getInstance().hrcSelectedRoute() + "' "
                + "\n 			group by (plan.SegmentKod) "
                + "\n 	   ) ff on ff.segmentkod=PlanSegmentov.SegmentKod "
                + "\n     where Polzovateli.Kod='" + ApplicationHoreca.getInstance().hrcSelectedRoute() + "' ";
        */
		String sql = "select PlanSegmentov.SegmentKod as kod,PlanSegmentov.plan as plan,PlanSegmentov.SegmentName as name , count(ifnull(ff.art,0)) as artcnt, ifnull(ff.art,'') as whart "
				+ "\n    from PlanSegmentov"
				+ "\n        join Podrazdeleniya on Podrazdeleniya.Kod=PlanSegmentov.territorykod"
				+ "\n        join Polzovateli on Polzovateli.Podrazdelenie=Podrazdeleniya._IDRRef "
				+ "\n       left join ("
				+ "\n        select plan.SegmentKod as segmentkod,nn.Artikul as art"
				+ "\n           from Kontragenty kk "
				+ "\n                join DogovoryKontragentov_strip dk on dk.vladelec=kk._idrref"
				+ "\n                join Prodazhi_last as pp on dk._idrref=pp.dogovorkontragenta and pp.period>=date('" + thismonth + "')"
				+ "\n               join Nomenklatura nn on nn._idrref=pp.nomenklatura "
				+ "\n                join FlagmanTovar flag on flag.Articul=nn.Artikul "
				+ "\n                join PlanSegmentov plan on plan.SegmentKod=flag.SegmentKod"
				+ "\n            where kk._idrref=" + clientID + ""
				+ "\n            group by (nn.Artikul)"
				+ "\n       ) ff on ff.segmentkod=PlanSegmentov.SegmentKod"
				//+ "\n     where Polzovateli.Kod='" + ApplicationHoreca.getInstance().hrcSelectedRoute() + "' ";
				+ "\n     where Polzovateli.Kod='" + sweetlife.android10.supervisor.Cfg.selectedOrDbHRC() + "' ";
		if(this.filterSegment.value().length() > 0){
			sql = sql + "\n    and PlanSegmentov.SegmentName like '%" + this.filterSegment.value() + "%'";
		}
		sql = sql + "\n     group by PlanSegmentov.SegmentKod,PlanSegmentov.plan,PlanSegmentov.SegmentName";
		sql = sql + "\n 	order by PlanSegmentov.SegmentName;";
		System.out.println("requerySegments " + sql);
		Bough segmentData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(segmentData.dumpXML());
		String _id = "";
		for(int i = 0; i < segmentData.children.size(); i++){
			Bough row = segmentData.children.get(i);
			//System.out.println(""+i+": "+row.dumpXML());
            /*if (_id.equals(row.child("recid").value.property.value())) {
                if (_id.equals(selectedRecId)) {
                    addIngredient(row);
                }
            } else {
                _id = row.child("recid").value.property.value();
                addFlagman(row);
            }*/
			addFlagman(row);
		}
		segmentGrid.refresh();
		segmentGrid.scrollView.scrollTo(0, nn);
		if(selectedSegmentKod != null){
			productOffset.value(0);
			nextProductData();
			productGrid.refresh();
		}
	}

	public void nextProductData(){
		productGrid.clearColumns();

		String slovo = "1=1";
		int tipPoiska = ISearchBy.SEARCH_CUSTOM;
		if(this.filterProduct.value().length() > 0){
			slovo = this.filterProduct.value();
			tipPoiska = ISearchBy.SEARCH_NAME;
		}

		String sql = Request_NomenclatureBase.composeSQLall(//
				dataOtgruzki//
				, clientID//
				, polzovatelID//
				, ""//
				, ""//
				, slovo//
				, tipPoiska//
				, false//
				, false//
				, sklad//
				, 3 * productPageSize//
				, productOffset.value().intValue()
				, false
				, false
				, null, null, false
				, false
				, null, null, this.selectedSegmentKod, false
		);
		System.out.println("nextProductData " + sql);
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		Date lostedLimit = new Date();
		lostedLimit.setDate(1);
		lostedLimit.setHours(0);
		lostedLimit.setMinutes(1);
		for(int i = 0; i < data.children.size(); i++){
			Bough row = data.children.get(i);
			//System.out.println(row.dumpXML());
			final String art = row.child("Artikul").value.property.value();
			Task tap3 = new Task(){
				@Override
				public void doTask(){


					selectArtikul(art);
				}
			};
			int bg = 0x00000000;//white
			/*if (Numeric.string2double(row.child("LastPrice").value.property.value()) > 0) {
				bg = 0xffdddddd;
			}*/
			String compArt = art;
			if(row.child("artCount").value.property.value().length() > 0){
				bg = 0xffddffdd;//green
			}else{
				if(row.child("LastSell").value.property.value().trim().length() > 0){
					bg = 0xffdddddd;//grey
					SimpleDateFormat sdf = new SimpleDateFormat(("yyyy-MM-dd"));
					Date val = new Date();
					try{
						val = sdf.parse(row.child("LastSell").value.property.value());
					}catch(Throwable tt){
						tt.printStackTrace();
					}
					if(val.before(lostedLimit)){
						bg = 0xffff6699;//pink
					}
					//compArt = art + " " + row.child("LastSell").value.property.value();
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

	void selectArtikul(String art){
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
				, this.selectedSegmentKod, false
		);
		Cursor cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			Intent resultIntent = new Intent();
			String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
			if(Request_NomenclatureBase.getMinCena(cursor) == null || !mIsCRAvailable){
				//System.out.println(": 1");
				resultIntent.putExtra(MIN_CENA, 0.00D);
				resultIntent.putExtra(MAX_CENA, 0.00D);
				if(vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
				){
					//System.out.println(": 2");
					resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
				}else{
					System.out.println(": 3");
					resultIntent.putExtra(VID_SKIDKI, "x'00'");
					resultIntent.putExtra(CENA_SO_SKIDKOY, 0.00D);
				}
			}else{
				//System.out.println(": 4");
				if(vidSkidki.length() > 0 //&& Sales.GetSaleName(vidSkidki).length() != 0
				){
					//System.out.println(": 5");
					resultIntent.putExtra(VID_SKIDKI, Request_NomenclatureBase.getVidSkidki(cursor));
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(cursor)));
				}else{
					//System.out.println(": 6");
					resultIntent.putExtra(VID_SKIDKI, "x'00'");
					resultIntent.putExtra(CENA_SO_SKIDKOY, Double.parseDouble(Request_NomenclatureBase.getCena(cursor)));
				}
				resultIntent.putExtra(MIN_CENA, Double.parseDouble(Request_NomenclatureBase.getMinCena(cursor)));
				resultIntent.putExtra(MAX_CENA, Double.parseDouble(Request_NomenclatureBase.getMaxCena(cursor)));
			}
			String sale = Request_NomenclatureBase.getSkidka(cursor);
			//System.out.println(": 7");
			if(sale != null){
				//System.out.println(": 8");
				try{
					resultIntent.putExtra(SKIDKA, Double.parseDouble(sale));
				}catch(Throwable t){
					System.out.println(t.getMessage());
					resultIntent.putExtra(SKIDKA, 0.00D);
				}
			}else{
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
