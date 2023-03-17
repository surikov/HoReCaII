package sweetlife.android10.supervisor;

import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.app.Activity;
import android.os.Bundle;

public class ActivityZayavkaNaPostavku extends Activity {
    static Bough allData = new Bough();
    public int gridPageSize = 30;
    public Numeric gridOffset = new Numeric();
    Layoutless layoutless;
    DataGrid dataGrid;
    ColumnText columnArtikul = new ColumnText();
    ColumnDescription columnName = new ColumnDescription();
    Bough gridData;

    Note kodKlienta = new Note().value("");
    Note cmnt = new Note().value("");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAll();
        createGUI();
    }
    @Override
    public void onPause() {
        super.onPause();
        saveAll();
    }

    void saveAll() {
        allData.child("kodKlienta").value.is(kodKlienta.value());
        allData.child("comment").value.is(cmnt.value());
    }

    void loadAll() {
        cmnt.value(allData.child("comment").value.property.value());
        kodKlienta.value(allData.child("kodKlienta").value.property.value());
    }
    void createGUI() {
        this.setTitle("Заявки на поставку");
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

        layoutless.child(new Decor(this).labelText.is("Код клиента").labelAlignLeftCenter()
                .top().is(0.0 * Auxiliary.tapSize)
                .left().is(0.5 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this).text.is(kodKlienta)
                .top().is(0 * Auxiliary.tapSize)
                .left().is(3 * Auxiliary.tapSize)
                .width().is(layoutless.width().property.minus(3.5 * Auxiliary.tapSize))
        );

        layoutless.child(new Decor(this).labelText.is("Комментарий").labelAlignLeftCenter()
                .top().is(1.0 * Auxiliary.tapSize)
                .left().is(0.5 * Auxiliary.tapSize)
                .height().is(1 * Auxiliary.tapSize)
                .width().is(2.5 * Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this).text.is(cmnt)
                .top().is(1.0 * Auxiliary.tapSize)
                .left().is(3 * Auxiliary.tapSize)
                .width().is(layoutless.width().property.minus(3.5 * Auxiliary.tapSize))
                .height().is(1 * Auxiliary.tapSize)
        );

        layoutless.child(dataGrid//
                .columns(new Column[]{ //
                        columnArtikul.title.is("артикул").width.is(Auxiliary.tapSize * 3)//
                        , columnName.title.is("наименование").width.is(Auxiliary.tapSize * 8)//
                })//
                .top().is(2 * Auxiliary.tapSize)
                .width().is(layoutless.width().property)//
                .height().is(layoutless.height().property.minus(3.5 * Auxiliary.tapSize))//
        );

        layoutless.child(new Knob(this).labelText.is("Добавить").afterTap.is(new Task() {
                    public void doTask() {
                        promptArtikul();
                    }
                })
                        .top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
                        .left().is(0.5 * Auxiliary.tapSize)
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
    void promptArtikul(){
        final Note art=new Note();
        final Note descr=new Note();
        final Note kolvo=new Note();
        Auxiliary.pick(this, "", new SubLayoutless(this)//
                        .child(new Decor(this).labelText.is("Артикул")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 0.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(art)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 1.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .child(new Decor(this).labelText.is("Наименование")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 2.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(descr)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 3.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .child(new Decor(this).labelText.is("Количество")
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 4.5)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 0.5)
                        )//
                        .child(new RedactText(this).text.is(kolvo)
                                .left().is(Auxiliary.tapSize * 0.5)
                                .top().is(Auxiliary.tapSize * 5.0)
                                .width().is(Auxiliary.tapSize * 8 )
                                .height().is(Auxiliary.tapSize * 1)
                        )//
                        .width().is(Auxiliary.tapSize * 9)//
                        .height().is(Auxiliary.tapSize * 6)//
                , "Добавить", new Task() {
                    @Override
                    public void doTask() {
                        System.out.println(art.value()+"/"+descr.value());
                        Bough one = new Bough().name.is("move");
                        one.child("artikul").value.is(art.value());
                        one.child("name").value.is(descr.value());
                        one.child("kolvo").value.is(kolvo.value());
                        allData.child("nomenklatura").child(one);
                        reFillGrid();
                    }
                }, null, null, null, null);
    }
    void sendData() {
        Vector<Bough> rows = this.allData.child("nomenklatura").children;
        String items="";
        String dlmtr = "";
        for (int i = 0; i < rows.size(); i++) {
            Bough one = rows.get(i);
            String kolvo=one.child("kolvo").value.property.value().trim();
            if(kolvo.length()<1){
                kolvo="0";
            }
            items = items + dlmtr + "{\"Артикул\": \"" + one.child("artikul").value.property.value()
                    + "\",\"ТоварСтрокой\": \"" + one.child("name").value.property.value()
                    + "\",\"Количество\": \"" + kolvo
                    + "\"}";
            dlmtr = ",";
        }
        final String text = "{"
                + "\n    \"Комментарий\": \"" + this.cmnt.value() + "\""
                + "\n    \"Контрагент\": " + this.kodKlienta.value() + ""
                + "\n    \"Товары\":[" + items + "]"
                + "\n}";
        final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaPostavku/" + Cfg.whoCheckListOwner() ;
        //final String url = "https://testservice.swlife.ru/shatov/hs/ZayavkaNaPostavku/" + Cfg.currentHRC() ;
        System.out.println(url);
        System.out.println(text);
        System.out.println(allData.dumpXML());
        final Bough b = new Bough();
        Expect expect = new Expect().status.is("Подождите").task.is(new Task() {
            @Override
            public void doTask() {
                try {
                    Bough result = Auxiliary.loadTextFromPrivatePOST(url, text.getBytes("utf-8"), 12000,Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword(), true);
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
                Auxiliary.warn(b.child("result").value.property.value(), ActivityZayavkaNaPostavku.this);
            }
        });
        expect.start(ActivityZayavkaNaPostavku.this);
    }
    void reFillGrid() {
        this.dataGrid.clearColumns();
        Vector<Bough> rows = this.allData.child("nomenklatura").children;
        for (int i = 0; i < rows.size(); i++) {
            final int ii = i;
            Task tap = new Task() {
                public void doTask() {
                    Auxiliary.pickConfirm(ActivityZayavkaNaPostavku.this, "Удалить строку?", "Удалить", new Task() {
                        public void doTask() {
                            allData.child("nomenklatura").children.removeElementAt(ii);
                            reFillGrid();
                        }
                    });
                }
            };
            columnArtikul.cell(rows.get(i).child("artikul").value.property.value(), tap);
            columnName.cell(rows.get(i).child("name").value.property.value(), tap,"количество: "+rows.get(i).child("kolvo").value.property.value());
        }
        dataGrid.refresh();
    }
}
