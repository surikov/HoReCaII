package sweetlife.android10.supervisor;

import java.util.Date;
import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.app.Activity;
import android.os.Bundle;

public class ActivityZayavkaIzmenenieNakladnoy extends Activity {
    static Bough allData = new Bough();
    public int gridPageSize = 30;
    public Numeric gridOffset = new Numeric();
    /*
        https://testservice.swlife.ru/shatov/hs/PerebitieNakladnoy/НомерРеализации/КодОтветственного/ДатаРеализации
        https://testservice.swlife.ru/shatov/hs/PerebitieNakladnoy/12-0008305/hrc211/20190104
        {
            "Комментарий": "Комментарий"
            "НовыйКонтрагент": 80682
            "НовыйТипОплаты": "Нал"
            "Цены":[
            {
                "Номенклатура": "94916"
                "НоваяЦена": 20.35
                "ЧерезФиксированнуюЦену": "да"
            }
            ]
            "Перенос":[
            {
                "Номенклатура": "57045"
            }
            ]
        }
    {
        "Статус": 0,
        "Сообщение": "Успешно создана заявка.",
        "НомерЗаявки": 5
    }
    */
    Layoutless layoutless;
    DataGrid dataGrid;
    ColumnDescription columnAction = new ColumnDescription();
    ColumnText columnArtikul = new ColumnText();
    Bough gridData;
    Note nRealizac = new Note().value("");
    Note kodKlienta = new Note().value("");
    Numeric tipOplaty = new Numeric().value(0);
    Numeric numPrichina = new Numeric().value(0);
    Note cmnt = new Note().value("");
    Numeric datReal = new Numeric().value(0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAll();
        /*
        allData.child("nomenklatura").children.removeAllElements();
        Bough change1 = new Bough().name.is("edit");
        change1.child("artikul").value.is("121212");
        change1.child("cena").value.is("3.55");
        change1.child("fix").value.is("1");
        allData.child("nomenklatura").child(change1);
        Bough move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("333222333");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("2223311");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("6767676");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("499977");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("666622");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("111111999");
        allData.child("nomenklatura").child(move1);

        move1 = new Bough().name.is("move");
        move1.child("artikul").value.is("9999");
        allData.child("nomenklatura").child(move1);

        change1 = new Bough().name.is("edit");
        change1.child("artikul").value.is("33333333");
        change1.child("cena").value.is("55.11");
        change1.child("fix").value.is("0");
        allData.child("nomenklatura").child(change1);

        change1 = new Bough().name.is("edit");
        change1.child("artikul").value.is("777666");
        change1.child("cena").value.is("111.11");
        change1.child("fix").value.is("0");
        allData.child("nomenklatura").child(change1);
*/
        createGUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAll();
    }

    void saveAll() {
        allData.child("docNum").value.is(nRealizac.value());
        allData.child("docDate").value.is(datReal.asString().value());
        allData.child("kodKlienta").value.is(kodKlienta.value());
        allData.child("tipOplaty").value.is("" + tipOplaty.value());
        allData.child("comment").value.is(cmnt.value());
        allData.child("numPrichina").value.is(""+numPrichina.value());

    }

    void loadAll() {
        nRealizac.value(allData.child("docNum").value.property.value());
        datReal.value(Numeric.string2double(allData.child("docDate").value.property.value()));
        cmnt.value(allData.child("comment").value.property.value());
        kodKlienta.value(allData.child("kodKlienta").value.property.value());
        tipOplaty.value(Numeric.string2double(allData.child("tipOplaty").value.property.value()));
        numPrichina.value(Numeric.string2double(allData.child("numPrichina").value.property.value()));

    }

    void createGUI() {
        this.setTitle("Заявки на изменение накладной");
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        dataGrid = new DataGrid(this).center.is(true)//
                .pageSize.is(gridPageSize)//
                .dataOffset.is(gridOffset)//
                .beforeFlip.is(new Task() {
                    @Override
                    public void doTask() {
                        //
                    }
                });

        layoutless.child(new Decor(this).labelText.is("Реализация №").labelAlignLeftCenter()
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(0.5 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this).text.is(nRealizac)
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(3 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize)
        );
        /*layoutless.child(new Decor(this).labelText.is("дата").labelAlignLeftCenter()
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(6.5 * Auxiliary.tapSize)
                .width().is(2 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
        );*/
        layoutless.child(new RedactDate(this).date.is(datReal).format.is("dd.MM.yyyy")
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(5.5 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize)
        );

        layoutless.child(new RedactSingleChoice(this)
                .item("Укажите причину")
                .item("Ошибка в цене")
                .item("Ошибка в форме оплаты")
                .item("Сменить контрагента")
                .item("Разделение накладной")
                .selection.is(numPrichina)
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(8 * Auxiliary.tapSize)
                .width().is(4 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize)
        );

        layoutless.child(new Decor(this).labelText.is("Код клиента").labelAlignLeftCenter()
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(12 * Auxiliary.tapSize)
                .width().is(2 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this).text.is(kodKlienta)
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(14 * Auxiliary.tapSize)
                .width().is(2 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize)
        );

        layoutless.child(new Decor(this).labelText.is("Тип оплаты").labelAlignLeftCenter()
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(16 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactSingleChoice(this)
                .item("Не менять")
                .item("Нал")
                .item("БезНал")
                .item("ТовЧек")
                .selection.is(tipOplaty)
                .top().is(0.5 * Auxiliary.tapSize)
                .left().is(18 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize).height().is(1 * Auxiliary.tapSize)
        );




        layoutless.child(new Decor(this).labelText.is("Комментарий").labelAlignLeftCenter()
                .top().is(1.5 * Auxiliary.tapSize)
                .left().is(0.5 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this).text.is(cmnt)
                .top().is(1.5 * Auxiliary.tapSize)
                .left().is(3 * Auxiliary.tapSize)
                .width().is(layoutless.width().property.minus(3.5 * Auxiliary.tapSize))
                .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(dataGrid//
                .columns(new Column[]{ //
                        columnArtikul.title.is("артикул").width.is(Auxiliary.tapSize * 3)//
                        , columnAction.title.is("действие").width.is(Auxiliary.tapSize * 8)//
                })//
                .top().is(2.5 * Auxiliary.tapSize)
                .width().is(layoutless.width().property)//
                .height().is(layoutless.height().property.minus(4.5 * Auxiliary.tapSize))//
        );
        layoutless.child(new Knob(this).labelText.is("Изменить").afterTap.is(new Task() {
                    public void doTask() {
                        final Note newArt = new Note();
                        final Numeric newPrice = new Numeric();
                        final Toggle fix = new Toggle();
                        Auxiliary.pick(ActivityZayavkaIzmenenieNakladnoy.this, "Изменить", new SubLayoutless(ActivityZayavkaIzmenenieNakladnoy.this)//
                                        .child(new Decor(ActivityZayavkaIzmenenieNakladnoy.this).labelText.is("Артикул").labelAlignRightCenter().top().is(Auxiliary.tapSize * 0).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize))//
                                        .child(new RedactText(ActivityZayavkaIzmenenieNakladnoy.this).text.is(newArt).left().is(Auxiliary.tapSize * 3.5).top().is(Auxiliary.tapSize * 0).width().is(Auxiliary.tapSize * 6).height().is(Auxiliary.tapSize * 0.9))//

										.child(new Decor(ActivityZayavkaIzmenenieNakladnoy.this).labelText.is("Новая цена").labelAlignRightCenter().top().is(Auxiliary.tapSize * 1).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize))//
                                        .child(new RedactNumber(ActivityZayavkaIzmenenieNakladnoy.this).number.is(newPrice).left().is(Auxiliary.tapSize * 3.5).top().is(Auxiliary.tapSize * 1).width().is(Auxiliary.tapSize * 6).height().is(Auxiliary.tapSize * 0.9))//
                                        //.child(new Decor(this).labelText.is("Через фикс.цену").labelAlignRightCenter().top().is(Auxiliary.tapSize * 2).width().is(100).height().is(Auxiliary.tapSize))//
                                        .child(new RedactToggle(ActivityZayavkaIzmenenieNakladnoy.this).yes.is(fix).labelText.is("Через фикс.цену").left().is(Auxiliary.tapSize * 3.5).top().is(Auxiliary.tapSize * 2).width().is(400).height().is(Auxiliary.tapSize * 0.9))//
                                        .width().is(Auxiliary.tapSize * 10)//
                                        .height().is(Auxiliary.tapSize * 6)//
                                , "Добавить", new Task() {
                                    @Override
                                    public void doTask() {
                                        Bough change1 = new Bough().name.is("edit");
                                        change1.child("artikul").value.is(newArt.value());
                                        change1.child("cena").value.is("" + newPrice.value());
                                        change1.child("fix").value.is(fix.value() ? "да" : "нет");
                                        allData.child("nomenklatura").child(change1);
                                        reFillGrid();
                                    }
                                }, null, null, null, null);
                    }
                })
                        .top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
                        .left().is(0.5 * Auxiliary.tapSize)
                        .width().is(3 * Auxiliary.tapSize)
                        .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new Knob(this).labelText.is("Разделить").afterTap.is(new Task() {
                    public void doTask() {
                        final Note newArt = new Note();
                        Auxiliary.pickString(ActivityZayavkaIzmenenieNakladnoy.this, "Артикул", newArt, "Сохранить", new Task() {
                            public void doTask() {
                                Bough move1 = new Bough().name.is("move");
                                move1.child("artikul").value.is(newArt.value());
                                allData.child("nomenklatura").child(move1);
                                reFillGrid();
                            }
                        });
                    }
                })
                        .top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
                        .left().is(3.5 * Auxiliary.tapSize)
                        .width().is(3 * Auxiliary.tapSize)
                        .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new Knob(this).labelText.is("Отправить").afterTap.is(new Task() {
                    public void doTask() {
                        sendData();
                    }
                })
                        .top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
                        .left().is(layoutless.width().property.minus(3.5 * Auxiliary.tapSize))
                        .width().is(3 * Auxiliary.tapSize)
                        .height().is(1 * Auxiliary.tapSize)
        );

        reFillGrid();
    }

    String tipOplatyParam() {
        if (this.tipOplaty.value().intValue() == 1) {
            return "\n    \"НовыйТипОплаты\": \"Нал\"";
        }
        if (this.tipOplaty.value().intValue() == 2) {
            return "\n    \"НовыйТипОплаты\": \"БезНал\"";
        }
        if (this.tipOplaty.value().intValue() == 3) {
            return "\n    \"НовыйТипОплаты\": \"ТовЧек\"";
        }
        return "";
    }

    String kontragentParam() {
        if (this.kodKlienta.value().trim().length() > 0) {
            return "\n    \"НовыйКонтрагент\": \"" + this.kodKlienta.value().trim()+"\"";
        }
        return "";
    }

    void sendData() {
        String move = "";
        String dmv = "";
        String change = "";
        String delch = "";
        Vector<Bough> rows = this.allData.child("nomenklatura").children;
        for (int i = 0; i < rows.size(); i++) {
            Bough one = rows.get(i);
            System.out.println(one.dumpXML());
            if (one.name.property.value().equals("edit")) {
                change = change + delch + "{\"Номенклатура\": \"" + one.child("artikul").value.property.value() + "\",\"НоваяЦена\": \"" + one.child("cena").value.property.value() + "\",\"ЧерезФиксированнуюЦену\": \"" + one.child("fix").value.property.value() + "\"}";
                delch = ",";
            } else {
                move = move + dmv + "{\"Номенклатура\": \"" + one.child("artikul").value.property.value() + "\"}";
                dmv = ",";
            }
        }

        final String text = "{"
                + "\n    \"Комментарий\": \"" + this.cmnt.value() + "\""
                + "\n    \"Причина\": \"" + this.numPrichina.value().intValue() + "\""

                //+ "\n    ,\"НовыйКонтрагент\": " + this.kodKlienta.value()
                //+ "\n    ,\"НовыйТипОплаты\": \"" + ((this.tipOplaty.value() > 1) ? ("ТовЧек") : ((this.tipOplaty.value() > 0) ? ("БезНал") : ("Нал"))) + "\""
                + kontragentParam()
                + tipOplatyParam()
                + "\n    \"Цены\":[" + change + "]"
                + "\n    \"Перенос\":[" + move + "]"
                + "\n}";
        String dt = Auxiliary.short1cDate.format(new Date(datReal.value().longValue()));
        //final String url="https://testservice.swlife.ru/shatov/hs/PerebitieNakladnoy/"+nRealizac.value()+"/"+Cfg.currentHRC()+"/"+dt;
        final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/PerebitieNakladnoy/" + nRealizac.value() + "/" + Cfg.whoCheckListOwner() + "/" + dt;
        System.out.println(url);
        System.out.println(text);
        System.out.println(allData.dumpXML());
        //Bough result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8"), 12000, Cfg.hrcPersonalLogin, Cfg.hrcPersonalPassword, true);
        final Bough b = new Bough();
        Expect expect = new Expect().status.is("Подождите").task.is(new Task() {
            @Override
            public void doTask() {
                try {
                    Bough result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8"), 12000, Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword(), true);
                    b.child("result").value.is(
                            result.child("message").value.property.value()
                                    + "\n" + result.child("raw").value.property.value()
                    );
                    System.out.println(result.dumpXML());
                } catch (Throwable t) {
                    t.printStackTrace();
                    b.child("result").value.is(t.toString());
                }
            }
        }).afterDone.is(new Task() {
            @Override
            public void doTask() {
                Auxiliary.warn(b.child("result").value.property.value(), ActivityZayavkaIzmenenieNakladnoy.this);
            }
        });
        expect.start(ActivityZayavkaIzmenenieNakladnoy.this);
    }

    void reFillGrid() {
        this.dataGrid.clearColumns();
        Vector<Bough> rows = this.allData.child("nomenklatura").children;
        for (int i = 0; i < rows.size(); i++) {

            String title = "Разделить";
            String descr = "";
            final int ii = i;
            Task tap = new Task() {
                public void doTask() {
                    //System.out.println(allData.child("nomenklatura").children.get(ii).child("artikul").value.property.value());
                    Auxiliary.pickConfirm(ActivityZayavkaIzmenenieNakladnoy.this, "Удалить строку?", "Удалить", new Task() {
                        public void doTask() {
                            allData.child("nomenklatura").children.removeElementAt(ii);
                            reFillGrid();
                        }
                    });
                }
            };
            if (rows.get(i).name.property.value().equals("edit")) {
                title = "Изменить";
                descr = "новая цена: " + rows.get(i).child("cena").value.property.value()
                        + "р., через фикс.цену: "
                        + (rows.get(i).child("fix").value.property.value())
                ;
            }
            columnArtikul.cell(rows.get(i).child("artikul").value.property.value(), tap);
            columnAction.cell(title, tap, descr);
        }
        dataGrid.refresh();
    }
}
