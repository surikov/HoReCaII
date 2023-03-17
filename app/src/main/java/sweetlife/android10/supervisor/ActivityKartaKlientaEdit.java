package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;

import java.util.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

public class ActivityKartaKlientaEdit extends Activity {
    DataGrid gridKontragent;
    DataGrid gridNomenklatura;
    Bough dataKontragent;
    Bough dataNomenklatura;
    ColumnDescription columnKontragent = new ColumnDescription();
    ColumnDescription columnNomenklatura = new ColumnDescription();
    Note name = new Note();
    Note komm = new Note();
    Note number = new Note();
    Note uin = new Note();
    Layoutless layoutless;
    MenuItem menuNewKlient;
    MenuItem menuNewArtikul;
    //MenuItem menuDelete;
    MenuItem menuUpload;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuNewKlient = menu.add("Добавить контрагента");
        menuNewArtikul = menu.add("Добавить номенклатуру");
        //menuDelete = menu.add("Удалить карту");
        menuUpload = menu.add("Выгрузить");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == menuNewKlient) {
            addKontragent();
            return true;
        }
        if (item == menuNewArtikul) {
            addNomenklatura();
            return true;
        }
		/*if (item == menuDelete) {
			deleteDoc();
			return true;
		}*/
        if (item == menuUpload) {
            uploadAndSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bough b = Auxiliary.bundle2bough(getIntent().getExtras());
        uin.value(b.child("uin").value.property.value());
        //System.out.println(b.dumpXML());
        String sql = "select nazvanie as nazvanie, kommentarii as kommentarii, number as number from KartaKlientaDok2 where uin='" + uin.value() + "'";
        Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        name.value(data.child("row").child("nazvanie").value.property.value());
        komm.value(data.child("row").child("kommentarii").value.property.value());
        number.value(data.child("row").child("number").value.property.value());
        //System.out.println("data "+data.dumpXML());
        readData();
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        createGUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillGUI();
    }

    void createGUI() {

        layoutless.child(new RedactText(this)//
                .text.is(name)
                .width().is(Auxiliary.tapSize * 5)
                .height().is(Auxiliary.tapSize)
        );
        layoutless.child(new RedactText(this)//
                .text.is(komm)
                .left().is(Auxiliary.tapSize * 5)
                .width().is(layoutless.width().property.minus(Auxiliary.tapSize * 5))
                .height().is(Auxiliary.tapSize)
        );
        gridKontragent = new DataGrid(this);
        layoutless.child(gridKontragent//
                .center.is(true)//
                .columns(new Column[]{ //
                                //
                                columnKontragent.title.is("Контрагенты")//
                                        .width.is(layoutless.width().property.divide(2).minus(2))//
                        }
                )//
                .top().is(Auxiliary.tapSize)//
                .width().is(layoutless.width().property.divide(2).minus(2))//
                .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
        );
        gridNomenklatura = new DataGrid(this);
        layoutless.child(gridNomenklatura//
                .center.is(true)//
                .columns(new Column[]{ //
                                //
                                columnNomenklatura.title.is("Номенклатура")//
                                        .width.is(layoutless.width().property.divide(2))//
                        }
                )//
                .top().is(Auxiliary.tapSize)//
                .left().is(layoutless.width().property.divide(2))//
                .width().is(layoutless.width().property.divide(2))//
                .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
        );
    }

    void readData() {
        String sql = "select kontragenty.naimenovanie as naimenovanie,kontragenty.kod as kod,_idrref as _idrref from KartaKlientaKlient2 join kontragenty on kontragenty._idrref=KartaKlientaKlient2.vladelec where uin='" + uin.value() + "'";
        dataKontragent = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        sql = "select nomenklatura.naimenovanie as naimenovanie,nomenklatura.artikul as artikul,_idrref as _idrref from KartaKlientaNomenklatura2 join nomenklatura on nomenklatura ._idrref=KartaKlientaNomenklatura2.tovar where uin='" + uin.value() + "'";
        dataNomenklatura = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));

    }

    void fillGUI() {
        //System.out.println("fillGUI");
        this.setTitle("Карта клиента " + uin.value());
        gridKontragent.clearColumns();
        for (int i = 0; i < dataKontragent.children.size(); i++) {
            Bough row = dataKontragent.children.get(i);
            final String _idrref = row.child("_idrref").value.property.value();
            final String naimenovanie = row.child("naimenovanie").value.property.value();
            Task tap = new Task() {

                @Override
                public void doTask() {
                    deleteKontragent(_idrref, naimenovanie);
                }
            };
            columnKontragent.cell(naimenovanie, tap, "код: " + row.child("kod").value.property.value());
        }
        gridKontragent.refresh();

        gridNomenklatura.clearColumns();
        for (int i = 0; i < dataNomenklatura.children.size(); i++) {
            Bough row = dataNomenklatura.children.get(i);
            final String _idrref = row.child("_idrref").value.property.value();
            final String naimenovanie = row.child("naimenovanie").value.property.value();
            Task tap = new Task() {

                @Override
                public void doTask() {
                    deleteNomenklatura(_idrref, naimenovanie);
					/*
					Intent intent = new Intent();
					intent.putExtra("uin", uin);
					intent.setClass(ActivityKartaKlienta.this, sweetlife.horeca.supervisor.ActivityKartaKlientaEdit.class);
					startActivity(intent);
					*/
                }
            };
            columnNomenklatura.cell(naimenovanie, tap, "артикул: " + row.child("artikul").value.property.value());
        }
        gridNomenklatura.refresh();
    }

    void addKontragent() {
        String sql = "select"//
                + "\n		kontragenty._idrref as _idrref"//
                + "\n		,kontragenty.kod as kod"//
                + "\n		,kontragenty.naimenovanie as naimenovanie"//
                + "\n	from MarshrutyAgentov"//
                + "\n		join kontragenty on kontragenty._idrref=MarshrutyAgentov.kontragent"//
                + "\n	group by kontragenty._idrref"//
                + "\n	order by kontragenty.naimenovanie;";
        final Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        Vector<String> names = new Vector<String>();
        for (int i = 0; i < bough.children.size(); i++) {
            names.add(bough.children.get(i).child("naimenovanie").value.property.value());
        }
        final Numeric sel = new Numeric();
        Auxiliary.pickSingleChoice(ActivityKartaKlientaEdit.this, names.toArray(new String[0]), sel, null//
                , new Task() {
                    @Override
                    public void doTask() {

                        //dataKontragent
                        Bough s = bough.children.get(sel.value().intValue());

                        Bough row = new Bough().name.is("row");
                        row.child("kod").value.is(s.child("kod").value.property.value());
                        row.child("naimenovanie").value.is(s.child("naimenovanie").value.property.value());
                        row.child("_idrref").value.is(s.child("_idrref").value.property.value());
                        dataKontragent.children.add(row);
                        //System.out.println(s.dumpXML());
                        //System.out.println(dataKontragent.dumpXML());
                        fillGUI();
                    }
                }, null, null, null, null);
    }

    void addNomenklatura() {
        //Intent intent = new Intent();
        //intent.setClass(ActivityKartaKlientaEdit.this, Activity_Nomenclature.class);
        //intent.putExtra(CLIENT_ID, mBidData.getClientID());
        //intent.putExtra(ORDER_AMOUNT, mBidData.getBid().getSumma() + mBidData.getFoodStuffs().getAmount());
        //startActivityForResult(intent,sweetlife.horeca.consts.IExtras.ADD_NOMENCATURE);
        final Note art = new Note();
        Auxiliary.pickString(ActivityKartaKlientaEdit.this, "Артикул", art, "Добавить", new Task() {

            @Override
            public void doTask() {
                String sql = "select _idrref as _idrref, naimenovanie as naimenovanie,artikul as artikul from nomenklatura_sorted where artikul='" + art.value() + "';";
                Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
                //System.out.println(bough.dumpXML());
                Bough row = new Bough().name.is("row");
                row.child("artikul").value.is(bough.child("row").child("artikul").value.property.value());
                row.child("naimenovanie").value.is(bough.child("row").child("naimenovanie").value.property.value());
                row.child("_idrref").value.is(bough.child("row").child("_idrref").value.property.value());
                dataNomenklatura.children.add(row);
                fillGUI();
            }

        });
    }

    void uploadAndSave() {
        //System.out.println("uploadAndSave");
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
                + "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/KartaKlienta\">"//
                + "\n	  <SOAP-ENV:Body>"//
                + "\n	    <ns1:Zagruzit>"//
                + "\n	      <ns1:Dok>"//
                + "\n	        <ns1:Nazvanie>" + name.value() + "</ns1:Nazvanie>"//
                + "\n	        <ns1:Kommentarii>" + komm.value() + "</ns1:Kommentarii>"//
                //+ "\n	        <ns1:UIN>47bb13b8-2656-11e6-83ad-3cd92b037e6c</ns1:UIN>"//
                + "\n	        <ns1:UIN>" + uin.value() + "</ns1:UIN>"//
                + "\n	        ";
        for (int i = 0; i < dataKontragent.children.size(); i++) {

            xml = xml + "\n	          <ns1:Vlad><ns1:Kod>" + dataKontragent.children.get(i).child("kod").value.property.value() + "</ns1:Kod></ns1:Vlad>";
        }
        xml = xml + "\n	        "//
                + "\n	        ";
        for (int i = 0; i < dataNomenklatura.children.size(); i++) {

            xml = xml + "\n	          <ns1:Tov><ns1:Art>" + dataNomenklatura.children.get(i).child("artikul").value.property.value() + "</ns1:Art></ns1:Tov>";
        }
        xml = xml + "\n	        "//
                + "\n	      </ns1:Dok>"//
                + "\n	    </ns1:Zagruzit>"//
                + "\n	  </SOAP-ENV:Body>"//
                + "\n	</SOAP-ENV:Envelope>";
        //System.out.println(xml);
        final RawSOAP rawSOAP = new RawSOAP();
        rawSOAP.url.is(Settings.getInstance().getBaseURL() + "KartaKlienta.1cws")//
                .responseEncoding.is("cp-1251")//
                .xml.is(xml)//
                .timeout.is(3 * 60 * 1000)//
        ;
        Report_Base.startPing();
        //rawSOAP.startNow();
        rawSOAP.afterError.is(new Task() {
            @Override
            public void doTask() {
                String txt = "" + rawSOAP.statusCode.property.value() + rawSOAP.statusDescription.property.value() + rawSOAP.exception.property.value().toString();
                Auxiliary.warn("Ошибка: " + txt, ActivityKartaKlientaEdit.this);
            }
        });
        rawSOAP.afterSuccess.is(new Task() {
            @Override
            public void doTask() {
                //System.out.println("afterSuccess "+rawSOAP.statusCode.property.value());
                if (rawSOAP.statusCode.property.value() >= 100 && rawSOAP.statusCode.property.value() <= 300//
                ) {
                    String txt = rawSOAP.data//.dumpXML();
                            .child("soap:Body")//
                            .child("m:ZagruzitResponse")//
                            .child("m:return")//
                            .value.property.value();
                    //System.out.println(rawSOAP.data.dumpXML());
                    if (txt.startsWith("ок")) {
                        Auxiliary.warn("Выгружено. В планшетах данные появятся после утреннего обновления.", ActivityKartaKlientaEdit.this);
                        String key = txt.substring(3);
                        //System.out.println("key "+key);
                        uin.value(key);
                        fillGUI();
                    } else {
                        Auxiliary.warn("Результат: " + txt, ActivityKartaKlientaEdit.this);
                    }
                } else {
                    Auxiliary.warn("Ошибка " + rawSOAP.statusCode.property.value() + ", " + rawSOAP.statusDescription.property.value(), ActivityKartaKlientaEdit.this);
                    //System.out.println("Ошибка "+rawSOAP.statusCode.property.value()+", "+rawSOAP.statusDescription.property.value());
                }
            }
        });
        rawSOAP.startLater(this, "Выгрузка", Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
    }

    void deleteDoc() {
        //System.out.println("deleteDoc");
        Auxiliary.warn("Удаление только через офис", ActivityKartaKlientaEdit.this);
    }

    void deleteKontragent(final String _idrref, String name) {
        //System.out.println("deleteKontragent "+_idrref);
        Auxiliary.pickConfirm(ActivityKartaKlientaEdit.this, name, "Удалить", new Task() {

            @Override
            public void doTask() {
                //String sql="delete from KartaKlientaKlient2 where uin='"+uin.value()+"' and vladelec=x'"+_idrref+"';";
                //ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
                for (int i = 0; i < dataKontragent.children.size(); i++) {
                    Bough row = dataKontragent.children.get(i);
                    //System.out.println(row.dumpXML());

                    if (row.child("_idrref").value.property.value().equals(_idrref)) {
                        //System.out.println("remove");
                        dataKontragent.children.remove(i);
                        break;
                    }
                }
                fillGUI();
            }
        });
    }

    void deleteNomenklatura(final String _idrref, String name) {
        //System.out.println("deleteNomenklatura "+_idrref);
        Auxiliary.pickConfirm(ActivityKartaKlientaEdit.this, name, "Удалить", new Task() {

            @Override
            public void doTask() {
                for (int i = 0; i < dataNomenklatura.children.size(); i++) {
                    Bough row = dataNomenklatura.children.get(i);
                    //System.out.println(row.dumpXML());

                    if (row.child("_idrref").value.property.value().equals(_idrref)) {
                        //System.out.println("remove");
                        dataNomenklatura.children.remove(i);
                        break;
                    }
                }
                fillGUI();

            }
        });
    }
}
