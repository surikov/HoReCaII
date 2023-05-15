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

import tee.binding.*;
import tee.binding.task.*;
import tee.binding.it.*;

public class Activity_ReceptChooser extends Activity_Base implements ITableColumnsNames {
    Layoutless layoutless;
    DataGrid receptGrid;
    DataGrid productGrid;
    ColumnText columnRecept;
    ColumnDescription columnCount;
    ColumnText columnPadRecept;
    //ColumnDescription columnProduct;
    ColumnText columnProductArtikul;
    ColumnText columnProductNaimenovanie;
    ColumnDescription columnProductProizvoditel;
    ColumnDescription columnProductCena;
    Note filterRecept = new Note();
    Note filterProduct = new Note();
    int productPageSize = 30;
    Bough productData;
    Numeric productOffset = new Numeric();
    //int receptPageSize = 300;
    //Bough receptData;
    //Numeric receptOffset = new Numeric();
    static String selectedRecId = null;
    static String selectedIngredientIdrref = null;
    static String selectedIngredientKluch = null;

    String dataOtgruzki = "0";
    String clientID = "x'82b90050568b3c6811e8b04a065686f9'";
    String polzovatelID = "x'bba320677c60fed011e9262ba38aa289'";
    String sklad = "0";

    boolean mIsCRAvailable = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Рецепты");
        Preferences.init(this);
        Bough b = Auxiliary.activityExatras(this);
        System.out.println(b.dumpXML());
        dataOtgruzki = b.child("dataOtgruzki").value.property.value();
        clientID = b.child("clientID").value.property.value();
        polzovatelID = b.child("polzovatelID").value.property.value();
        sklad = b.child("sklad").value.property.value();
        selectedRecId = b.child("recid").value.property.value();
        mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);
        createGUI();
        requeryRecept();
        /*if (receptData.children.size() > 0) {
            Bough row = receptData.children.get(0);
            String idr = row.child("ingidrref").value.property.value();
            String iky = row.child("ingkluch").value.property.value();
            selectedIngredientIdrref = idr;
            selectedIngredientKluch = iky;
        }*/
    }

    void createGUI() {
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        receptGrid = new DataGrid(this);
        productGrid = new DataGrid(this);
        columnCount = new ColumnDescription();
        columnRecept = new ColumnText();
        columnPadRecept = new ColumnText();
        columnProductArtikul = new ColumnText();
        columnProductNaimenovanie = new ColumnText();
        columnProductProizvoditel = new ColumnDescription();
        columnProductCena = new ColumnDescription();
        /*layoutless.child(new RedactText(this).text.is(filterRecept)
                .width().is(Auxiliary.tapSize * 7)//
                .height().is(Auxiliary.tapSize * 1)
                .height().is(Auxiliary.tapSize * 1)
        );*/
        layoutless.child(new Decor(this).labelText.is("поиск по продуктам").labelAlignRightBottom()
                .width().is(Auxiliary.tapSize * 6.5)//
                .height().is(Auxiliary.tapSize * 1)
        );
        layoutless.child(receptGrid//
                //.center.is(true)//
                .columns(new Column[]{
                        columnPadRecept.noVerticalBorder.is(true).title.is(" ").width.is(Auxiliary.tapSize * 0.5) //
                        , columnRecept.noVerticalBorder.is(true).title.is("Ингредиенты").width.is(Auxiliary.tapSize * 5.5) //
                        , columnCount.noVerticalBorder.is(true).title.is(" ").width.is(Auxiliary.tapSize * 1) //
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
                .beforeFlip.is(new Task() {
                    @Override
                    public void doTask() {
                        nextProductData();
                    }
                })
                .left().is(Auxiliary.tapSize * 7)
                .top().is(Auxiliary.tapSize)
                .width().is(layoutless.width().property.minus(Auxiliary.tapSize * 7))//
                .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
        );
        layoutless.child(new Knob(this).labelText.is("Искать")
                .afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        requeryRecept();
                    }
                })
                .width().is(Auxiliary.tapSize * 2)//
                .left().is(layoutless.width().property.minus(Auxiliary.tapSize * 2))
                .height().is(Auxiliary.tapSize * 1)
        );
    }

    void addRecept(Bough row) {
        /*final String sel_id = row.child("recid").value.property.value();
        Task tap = new Task() {
            @Override
            public void doTask() {
                selectedRecId = sel_id;
                selectedIngredientIdrref = null;
                selectedIngredientKluch = null;
                //System.out.println("rece " +selectedRecId);
                requeryRecept();
            }
        };
        int bg = 0x00000000;
        if (row.child("recid").value.property.value().equals(selectedRecId)) {
            bg = 0x22000000;
        }
        columnPadRecept.cell("", bg);
        columnRecept.cell("" + row.child("recname").value.property.value(), bg, tap, "");
        */
        this.setTitle(row.child("recname").value.property.value());
        if (row.child("recid").value.property.value().equals(selectedRecId)) {
            if (selectedIngredientIdrref == null) {
                //System.out.println("now " +selectedIngredientId);
                selectedIngredientIdrref = row.child("ingidrref").value.property.value();
                selectedIngredientKluch = row.child("ingkluch").value.property.value();
            }
            addIngredient(row);
        }
    }

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
                requeryRecept();
            }
        };
        int bg = 0x11000000;
        if (selectedIngredientIdrref.equals(idr) && selectedIngredientKluch.equals(iky)) {
            bg = 0x22006699;
        }
        columnPadRecept.cell("", bg);
        String prodano = row.child("prodano").value.property.value().trim();
        if (prodano.length() < 1) prodano = "0";
        String vsego = row.child("vsego").value.property.value().trim();
        if (vsego.length() < 1) vsego = "0";
        columnRecept.cell("" + row.child("ingname").value.property.value(), bg, tap2);
        columnCount.cell("", bg, tap2, prodano + "/" + vsego);
        //System.out.println(iid+" - " +selectedIngredientId+" / "+(selectedIngredientId==iid));
    }

    public void requeryRecept() {
        int nn = receptGrid.scrollView.getScrollY();
        //System.out.println(nn);
        receptGrid.clearColumns();
        /*String sql = "select"
                + "\n    receptii.naimenovanie as recname,reingred.ingridient as ingname"
                + ",receptii._id as recid,reingred._idrref as ingidrref,reingred.kluch as ingkluch"
                + "\n    from ReceptiiIngridienty reingred"
                + "\n    join receptii on receptii._IDRRef=reingred._IDRRef";
        //if (this.filterRecept.value().length() > 0) {
        //    sql = sql + "\n    where receptii.naimenovanie like '%" + this.filterRecept.value() + "%'";
        //}
        sql = sql + "\n    where receptii._id = "+selectedRecId;
        sql = sql + "\n    order by receptii.naimenovanie,receptii._id,reingred.ingridient,reingred._id";
        */
        String sql = "select receptii.naimenovanie as recname,reingred.ingridient as ingname,receptii._id as recid,reingred._idrref as ingidrref,reingred.kluch as ingkluch"
                + "\n ,vse.cnt as vsego,prod.cnt as prodano"
                + "\n        from ReceptiiIngridienty reingred"
                + "\n        join receptii on receptii._IDRRef=reingred._IDRRef"
                + "\n        join ReceptiiProducty reprod on reprod.Kluch=reingred.kluch"
                + "\n        join nomenklatura n on n.product=reprod.product"
                + "\n        left join (select count(reingred.kluch) as cnt,reingred.kluch as ingkluch"
                + "\n                from ReceptiiIngridienty reingred"
                + "\n                join receptii on receptii._IDRRef=reingred._IDRRef"
                + "\n                join ReceptiiProducty reprod on reprod.Kluch=reingred.kluch and reprod.Kluch=reingred.kluch and reprod._idrref=reingred._idrref"
                + "\n                join nomenklatura_sorted n on n.product=reprod.product"
                + "\n                join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=n.[_IDRRef] and curAssortiment.zapret<>x'01'"
                + "\n                where receptii._id = " + selectedRecId
                + "\n                group by reingred.kluch"
                + "\n                order by receptii.naimenovanie,receptii._id,reingred.ingridient,reingred._id"
                + "\n            ) vse on vse.ingkluch=reingred.kluch"
                + "\n        left join (select count(reingred.kluch) as cnt,reingred.kluch as ingkluch"
                + "\n                from ReceptiiIngridienty reingred"
                + "\n                join receptii on receptii._IDRRef=reingred._IDRRef"
                + "\n                join ReceptiiProducty reprod on reprod.Kluch=reingred.kluch and reprod.Kluch=reingred.kluch and reprod._idrref=reingred._idrref"
                + "\n                join nomenklatura_sorted n on n.product=reprod.product"
                + "\n                join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=n.[_IDRRef] and curAssortiment.zapret<>x'01'"
                + "\n                join DogovoryKontragentov_strip on DogovoryKontragentov_strip.vladelec=" + clientID
                + "\n                join Prodazhi_last Prodazhi on Prodazhi.DogovorKontragenta=DogovoryKontragentov_strip._IDRref and Prodazhi.nomenklatura=n.[_IDRRef]"
                + "\n                where receptii._id = " + selectedRecId
                + "\n                group by reingred.kluch"
                + "\n                order by receptii.naimenovanie,receptii._id,reingred.ingridient,reingred._id"
                + "\n            ) prod on prod.ingkluch=reingred.kluch"
                + "\n        where receptii._id = " + selectedRecId
                + "\n        group by reingred.kluch"
                + "\n        order by receptii.naimenovanie,receptii._id,reingred.ingridient,reingred._id"
                + ";";
        //System.out.println(sql);
        Bough receptData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        //System.out.println(selectedRecId+"/"+selectedIngredientId);
        String _id = "";
        for (int i = 0; i < receptData.children.size(); i++) {
            Bough row = receptData.children.get(i);
            //System.out.println(""+i+": "+row.dumpXML());
            if (_id.equals(row.child("recid").value.property.value())) {
                if (_id.equals(selectedRecId)) {
                    addIngredient(row);
                }
            } else {
                _id = row.child("recid").value.property.value();
                addRecept(row);
            }
        }
        receptGrid.refresh();
        receptGrid.scrollView.scrollTo(0, nn);

        if (selectedIngredientIdrref != null) {
            productOffset.value(0);
            nextProductData();
            productGrid.refresh();
        }
    }

    public void nextProductData() {
        productGrid.clearColumns();

        String slovo = "1=1";
        int tipPoiska = ISearchBy.SEARCH_CUSTOM;
        if (this.filterProduct.value().length() > 0) {
            slovo = this.filterProduct.value();
            tipPoiska = ISearchBy.SEARCH_NAME;
        }

        String sql = Request_NomenclatureBase.composeSQL(//
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


                , false
                , selectedIngredientIdrref, selectedIngredientKluch
        );
        //System.out.println(sql);
        Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        //System.out.println(data.children.size());
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
            /*columnProduct.cell(
                    row.child("Artikul").value.property.value() + ": " + row.child("Naimenovanie").value.property.value()
                    ,tap3
                    , row.child("ProizvoditelNaimenovanie").value.property.value() + ": " + row.child("EdinicyIzmereniyaNaimenovanie").value.property.value()
            );*/
            //
            int clr=0x00ffffff;
            if(row.child("LastPrice").value.property.value().trim().length()>0){
                clr=0xffcccccc;
            }
            columnProductArtikul.cell(art, clr,tap3);
            columnProductNaimenovanie.cell(row.child("Naimenovanie").value.property.value(),clr, tap3);
            columnProductProizvoditel.cell(row.child("ProizvoditelNaimenovanie").value.property.value(),clr
                    , tap3, row.child("MinNorma").value.property.value() + " " + row.child("EdinicyIzmereniyaNaimenovanie").value.property.value());
            columnProductCena.cell(row.child("Cena").value.property.value() + "р",clr
                    , tap3, row.child("MinCena").value.property.value() + " / " + row.child("MaxCena").value.property.value());
        }

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
