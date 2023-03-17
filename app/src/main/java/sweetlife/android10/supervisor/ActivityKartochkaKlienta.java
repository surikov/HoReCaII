package sweetlife.android10.supervisor;

import java.util.*;

import sweetlife.android10.consts.*;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.database.Requests;
import sweetlife.android10.database.nomenclature.ISearchBy;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityKartochkaKlienta extends Activity implements ITableColumnsNames{

    int productPageSize = 30;
    Numeric productOffset = new Numeric();

    String dataOtgruzki = "0";
    String clientID = "x'82b90050568b3c6811e8b04a065686f9'";
    String polzovatelID = "x'bba320677c60fed011e9262ba38aa289'";
    String sklad = "0";

    boolean mIsCRAvailable = false;

    Layoutless layoutless;
    DataGrid itemsGrid;
    ColumnDescription columnItemNaimenovanie = new ColumnDescription();
    ColumnText columnItemArtikul = new ColumnText();
    //Bough itemsGridData;
    Vector<Bough> rows = new Vector<Bough>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.init(this);
        Bough b = Auxiliary.activityExatras(this);
        System.out.println(b.dumpXML());
        dataOtgruzki = b.child("dataOtgruzki").value.property.value();
        clientID = b.child("clientID").value.property.value();
        polzovatelID = b.child("polzovatelID").value.property.value();
        sklad = b.child("sklad").value.property.value();

        createGUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //requery.start(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        Auxiliary.pick3Choice(this, null, "Отменить все изменения перед выходом?", "Выгрузить", new Task() {
            //Auxiliary.pickConfirm(this, "Отменить все изменения перед выходом?", "Выгрузить", new Task() {
            @Override
            public void doTask() {
                sendData();
            }
        }, null, null, "Не сохранять", new Task() {
            //Auxiliary.pickConfirm(this, "Отменить все изменения перед выходом?", "Выгрузить", new Task() {
            @Override
            public void doTask() {
                ActivityKartochkaKlienta.this.finish();
            }
        });
        */

    }

    void createGUI() {
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        this.setTitle(ApplicationHoreca.getInstance().getClientInfo().getKod() + ": " + ApplicationHoreca.getInstance().getClientInfo().getName());
        itemsGrid = new DataGrid(this).center.is(true)//
                .pageSize.is(999)//itemsGridPageSize)//
        //.dataOffset.is(itemsGridOffset)//
				           /* .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryItemGridData();
						flipItemsGrid();
					}
				})*/;
        layoutless.child(itemsGrid//
                //.noHead.is(true)//
                .columns(new Column[]{ //
                        columnItemArtikul.title.is("Артикул").width.is(Auxiliary.tapSize * 2)//
                        , columnItemNaimenovanie.title.is("Наименование").width.is(Auxiliary.tapSize * 16) //
                })//
                .left().is(0).top().is(0)//
                .width().is(layoutless.width().property)//
                .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
        );
        layoutless.child(new Knob(this).labelText.is("Выгрузить").afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        promptVigruzit();
                    }
                })//
                        .left().is(0)//
                        .top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
                        .width().is(layoutless.width().property.divide(2))//
                        .height().is(Auxiliary.tapSize)//
        );
        layoutless.child(new Knob(this).labelText.is("Добавить").afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        promptDobavit();
                    }
                })//
                        .left().is(layoutless.width().property.divide(2))//
                        .top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
                        .width().is(layoutless.width().property.divide(2))//
                        .height().is(Auxiliary.tapSize)//
        );
        loadData();
    }

    void promptVigruzit() {
        Auxiliary.pickConfirm(this, "Выгрузить артикулы?", "Выгрузить", new Task() {
            @Override
            public void doTask() {
                sendData();
            }
        });
    }

    void promptDobavit() {
        Intent intent = new Intent();
        intent.setClass(ActivityKartochkaKlienta.this, sweetlife.android10.ui.Activity_Nomenclature.class);
        intent.putExtra(IExtras.CLIENT_ID, ApplicationHoreca.getInstance().getClientInfo().getID());
        intent.putExtra(IExtras.ORDER_AMOUNT, 0);
        startActivityForResult(intent, IExtras.ADD_NOMENCATURE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult " + requestCode + "/" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == IExtras.ADD_NOMENCATURE) {
                String art = data.getStringExtra(ITableColumnsNames.ARTIKUL);
                String name = this.getName(art);
                Bough row = new Bough().name.is("row");
                row.child("Артикул").value.is(art);
                row.child("name").value.is(name);
                rows.add(row);
                flipItemsGrid();
                itemsGrid.refresh();
                sendData();
            }
        }
    }

    void sendData() {
        //final String url = "http://89.109.7.162/hrc120107/hs/ObnovlenieInfo/%D0%97%D0%B0%D0%BF%D0%B8%D1%81%D0%B0%D1%82%D1%8C%D0%9A%D0%B0%D1%80%D1%82%D0%BE%D1%87%D0%BA%D1%83%D0%9A%D0%BB%D0%B8%D0%B5%D0%BD%D1%82%D0%B0/"
        //        + ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
        final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
                +"/hs/ObnovlenieInfo/%D0%97%D0%B0%D0%BF%D0%B8%D1%81%D0%B0%D1%82%D1%8C%D0%9A%D0%B0%D1%80%D1%82%D0%BE%D1%87%D0%BA%D1%83%D0%9A%D0%BB%D0%B8%D0%B5%D0%BD%D1%82%D0%B0/"
                + ApplicationHoreca.getInstance().getClientInfo().getKod().trim();

        //final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
        //        + "/hs/FileRasporazenie/" + this.selectedDocNum + "/" + Cfg.currentHRC() + "/?rash=" + rash;

        String data = "[";
        String dlmtr = "";
        for (int i = 0; i < rows.size(); i++) {
            data = data + dlmtr + "{\"Артикул\":\"" + rows.get(i).child("Артикул").value.property.value()+ "\""
                    + "\"Комментарий\":\"" + rows.get(i).child("Комментарий").value.property.value()+ "\""
                    + "\"ПотенциальнаяЦена\":\"" + rows.get(i).child("ПотенциальнаяЦена").value.property.value() + "\""
                    + "\"Подсказка\":\"" + rows.get(i).child("Подсказка").value.property.value() + "\"}";
            dlmtr = ", ";
        }
        data = data + "]";
        System.out.println(url);
        System.out.println(data);
        final String txt = data;
        final Note response=new Note();
        Expect expect = new Expect().status.is("Отправка").task.is(new Task() {
            @Override
            public void doTask() {
                //Bough result = Auxiliary.loadTextFromPublicPOST(url, txt, 21000, "UTF-8");
                Bough result = Auxiliary.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8",Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());

                System.out.println(result.dumpXML());
				response.value(result.child("raw").value.property.value());

            }
        }).afterDone.is(new Task() {
            @Override
            public void doTask() {
            	Auxiliary.warn("Отправлено: "+response.value(),ActivityKartochkaKlienta.this);
                //ActivityKartochkaKlienta.this.finish();
                flipItemsGrid();
                itemsGrid.refresh();
            }
        });
        expect.start(this);
        //
    }

    public void flipItemsGrid() {
        itemsGrid.clearColumns();
        if (rows != null) {
            for (int i = 0; i < rows.size(); i++) {
                Bough row = rows.get(i);
                final String art = row.child("Артикул").value.property.value();
                final String name = row.child("name").value.property.value();
                Task tap = new Task() {
                    @Override
                    public void doTask() {
                        //promptDelete(art, name);
                        promptInfo(art);
                    }
                };//mOrder
                String descr=row.child("Комментарий").value.property.value();
                if(descr.length()>0)descr=descr+", подсказка: ";
                descr=descr+row.child("Подсказка").value.property.value();
                columnItemNaimenovanie.cell(descr, tap,name+", потенц. цена: "+row.child("ПотенциальнаяЦена").value.property.value());
                columnItemArtikul.cell(art, tap);
            }
        }
    }
/*
    void promptDelete(final String art, String name) {
        System.out.println(art + ": " + name);
        Auxiliary.pickConfirm(this, "Удаленить " + art + ": " + name + "?", "Удалить", new Task() {
            @Override
            public void doTask() {
                deleteRow(art);
            }
        });
    }*/
    void promptInfo(final String art){
        final Note cmntry=new Note();
        final Note potcena=new Note();
        final Note podskazka=new Note();
        for(int i=0;i<rows.size();i++){
            Bough row=rows.get(i);
            if(row.child("Артикул").value.property.value().equals(art)){
                cmntry.value(row.child("Комментарий").value.property.value());
                potcena.value(row.child("ПотенциальнаяЦена").value.property.value());
                podskazka.value(row.child("Подсказка").value.property.value());
                break;
            }
        }
        Auxiliary.pick(this, "", new SubLayoutless(this)//
                        .child(new Decor(this).labelText.is("Комментарий")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 0.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(cmntry)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 1.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .child(new Decor(this).labelText.is("Потенциальная цена")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 2.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(potcena)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 3.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .child(new Decor(this).labelText.is("Подсказка")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 4.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(podskazka)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 5.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .width().is(Auxiliary.tapSize * 9)//
                        .height().is(Auxiliary.tapSize * 6)//
                , "Сохранить", new Task() {
                    @Override
                    public void doTask() {
                        for(int i=0;i<rows.size();i++){
                            Bough row=rows.get(i);
                            if(row.child("Артикул").value.property.value().equals(art)){
                                row.child("Комментарий").value.property.value(cmntry.value());
                                row.child("ПотенциальнаяЦена").value.property.value(potcena.value());
                                row.child("Подсказка").value.property.value(podskazka.value());
                                flipItemsGrid();
                                itemsGrid.refresh();
                                sendData();
                                break;
                            }
                        }
                    }
                }, "Удалить", new Task() {
                    @Override
                    public void doTask() {
                        deleteRow(art);
                    }
                }, "В заказ", new Task() {
                    @Override
                    public void doTask() {
                        selectArtikul(art);
                    }
                });
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
            mIsCRAvailable = !Requests.IsSyncronizationDateLater(0);
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
    void deleteRow(String art) {
        for (int i = 0; i < rows.size(); i++) {
            Bough row = rows.get(i);
            String a = row.child("Артикул").value.property.value();
            if (a.equals(art)) {
                rows.remove(i);
                flipItemsGrid();
                itemsGrid.refresh();
                sendData();
                break;
            }
        }
    }

    String getName(String artikul) {
        String r = "(отсутствует в базе)";
        String sql = "select naimenovanie as n from nomenklatura where artikul=" + artikul;
        Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        r = data.child("row").child("n").value.property.value();
        return r;
    }

    /*public void requeryItemGridData() {
        itemsGridData = new Bough();
        System.out.println(itemsGridData.dumpXML());
    }

    public void requeryData() {
        requeryItemGridData();
    }

    public void refreshGUI() {
        flipItemsGrid();
        itemsGrid.refresh();
    }*/
    void loadData() {
        final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()//
                + "/hs/ObnovlenieInfo/%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C%D0%9A%D0%B0%D1%80%D1%82%D0%BE%D1%87%D0%BA%D1%83%D0%9A%D0%BB%D0%B8%D0%B5%D0%BD%D1%82%D0%B0/"//
                + ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
        //System.out.println(url);
        new Expect().task.is(new Task() {
            @Override
            public void doTask() {
                try {
                    //byte[] b = Auxiliary.loadFileFromPublicURL(url);
                    byte[] b = Auxiliary.loadFileFromPrivateURL(url,Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
                    String s = new String(b, "utf-8");
                    Bough d = Bough.parseJSON(s);
                    rows = d.children("КарточкаКлиента");
                    System.out.println(d.dumpXML());
                    for (int i = 0; i < rows.size(); i++) {
                        Bough row = rows.get(i);
                        String art = row.child("Артикул").value.property.value();
                        String name = getName(art);
                        row.child("name").value.is(name);
                    }
                } catch (Exception e) {
                    //
                    e.printStackTrace();
                }
            }
        }).afterDone.is(new Task() {
            @Override
            public void doTask() {
                flipItemsGrid();
                itemsGrid.refresh();
            }
        }).status.is("Обновление").start(this);
    }
}
