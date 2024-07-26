package sweetlife.android10.supervisor;

import java.util.Date;
import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.database.Requests;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityCheckDocs extends Activity {

    public static String tpCode = "000001001";
    public static String tpKontrCode = "000001005";

    public static String svCode = "000001002";
    public static String svKontrCode = "000001012";

    public static String rdCode = "000001003";
    public static String rdKontrCode = "000001022";

    public static String skladCode = "000001084";

    public static String snabCode = "000001157";
    public static String snabKontrCode = "000001162";

    public int gridPageSize = 30;
    public Numeric gridOffset = new Numeric();
    Layoutless layoutless;
    MenuItem menuDobavitCheckList;
    MenuItem menuDobavitAudit;
    MenuItem menuClearUploaded;
    MenuItem menuClearAll;
    DataGrid dataGrid;
    //ColumnText columnIploaded = new ColumnText();
    ColumnDate columnDate = new ColumnDate().format.is("dd.MM.yyyy");
    ColumnDescription columnTerritory = new ColumnDescription();
    Bough gridData;
    public Expect requery = new Expect().status.is("Подождите...")//
            .task.is(new Task() {
                @Override
                public void doTask() {
                    requeryData();
                }
            }).afterDone.is(new Task() {
                @Override
                public void doTask() {
                    refreshGUI();
                }
            })//
            ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuDobavitCheckList = menu.add("Добавить Чек-лист");
        menuDobavitAudit= menu.add("Добавить аудит");
        menuClearUploaded = menu.add("Удалить все выгруженные");
        menuClearAll = menu.add("Удалить все чек-листы");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item == menuDobavitCheckList) {

            promptPodrazdelenie(false);
            return true;
        }
        if (item == menuDobavitAudit) {

            promptPodrazdelenie(true);
            return true;
        }
        if (item == menuClearUploaded) {
            promptClearUploaded();
            return true;
        }
        if (item == menuClearAll) {
            promptClearAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requery.start(this);
    }

    void promptClearUploaded() {
        Auxiliary.pickConfirm(this, "Удалить все выгруженные чек-листы", "Удалить", new Task() {
            @Override
            public void doTask() {
                String sql = "delete from PokazateliChekListaItem where doc_id in (select _id from PokazateliChekListaDoc where vigruzhen=1);";
                ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
                sql = "delete from PokazateliChekListaDoc where vigruzhen=1;";
                ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
                requery.start(ActivityCheckDocs.this);
            }
        });
    }

    void promptClearAll() {
        Auxiliary.pickConfirm(this, "Удалить все выгруженные чек-листы", "Удалить", new Task() {
            @Override
            public void doTask() {
                String sql = "delete from PokazateliChekListaItem;";
                ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
                sql = "delete from PokazateliChekListaDoc;";
                ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
                requery.start(ActivityCheckDocs.this);
            }
        });
    }

    void promptPodrazdelenie(final boolean isAudit) {
        if(Requests.IsSyncronizationDateLater(0)){
            Auxiliary.warn("Без обновления создавать чек-листы запрещено",this);
            return;
        }
        final Numeric nn = new Numeric().value(0);
        final Vector<Bough> names = new Vector<Bough>();
        String currentKod = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim();

        for (int i = 0; i < Cfg.territory().children.size(); i++) {
            String ihrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
            if (ihrc.trim().length() > 1) {
                names.add(Cfg.territory().children.get(i));
            }
        }
        if (names.size() > 1) {
            String sql = "select naimenovanie as name, kod as kod from sklady where pometkaudaleniya=x'00' order by name";
            final Bough skl = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
            int cnt=names.size();
            if(!isAudit) {
                cnt=names.size() + skl.children.size();
            }
            final String[] ters = new String[cnt];//new String[names.size() + skl.children.size()];
            for (int i = 0; i < names.size(); i++) {
                ters[i] = names.get(i).child("territory").value.property.value()//
                        + " (" + names.get(i).child("hrc").value.property.value().trim() + ")";
            }
            if(!isAudit) {
                for (int i = 0; i < skl.children.size(); i++) {
                    ters[names.size() + i] = skl.children.get(i).child("name").value.property.value();
                }
            }
            Auxiliary.pickSingleChoice(ActivityCheckDocs.this, ters, nn, null, new Task() {
                @Override
                public void doTask() {
                    String hrc = "";
                    String skladKod = "";
                    if (nn.value().intValue() < names.size()) {
                        hrc = names.get(nn.value().intValue()).child("hrc").value.property.value().trim();
                    } else {
                        skladKod = skl.children.get(nn.value().intValue() - names.size()).child("kod").value.property.value().trim();
                    }
                    //String hrc = names.get(nn.value().intValue()).child("hrc").value.property.value().trim();
                    //String kod = names.get(nn.value().intValue()).child("kod").value.property.value().trim();
                    //String territory = names.get(nn.value().intValue()).child("territory").value.property.value().trim();
                    String date = Auxiliary.sqliteDate.format(new Date());
                    //System.out.println("new " + date + ": " + hrc);
                    addFolders(date, hrc, skladKod,isAudit);
                    Intent intent = new Intent(ActivityCheckDocs.this, ActivityCheckDayPodr.class);
                    intent.putExtra("date_key", date);
                    intent.putExtra("for_hrc", hrc);
                    intent.putExtra("sklad_kod", skladKod);
                    intent.putExtra("is_audit", ""+isAudit);
                    startActivity(intent);
                }
            }, null, null, null, null);
        } else {
            Auxiliary.warn("Нет подчинённых территорий", this);
        }
    }

    void addFolders(String date_key, String for_hrc, String skladKod,boolean isAudit) {
		//for_hrc="hrc300";
        System.out.println("addFolders: " + for_hrc + ", " + date_key + ", " + skladKod);
        if (isAudit) {
            addFolderItems(ActivityCheckDocs.snabCode, date_key, for_hrc, "",isAudit);
        }else{
            if (skladKod.length() > 0) {
                addFolderItems(ActivityCheckDocs.skladCode, date_key, "", skladKod,isAudit);
            } else {
                for (int i = 0; i < Cfg.territory().children.size(); i++) {
                    Bough one = Cfg.territory().children.get(i);
                    if (one.child("hrc").value.property.value().trim().equals(for_hrc)) {
                        String territory = one.child("territory").value.property.value().trim();
                        String p[] = territory.split("/");
                        System.out.println("prompt " + territory);
                        if (p[0].trim().length() > 0
								|| (p.length>3 && p[3].trim().equals("VIP Н.Новгород")
						)) {
                            System.out.println("promptNewTP " + for_hrc + ", " + p[p.length - 1]);
                            addFolderItems(ActivityCheckDocs.tpCode, date_key, for_hrc, "",isAudit);
                        } else {
                            if (p[1].trim().length() > 0) {
                                System.out.println("promptNewSV " + for_hrc + ", " + p[p.length - 1]);
                                addFolderItems(ActivityCheckDocs.svCode, date_key, for_hrc, "",isAudit);
                            } else {
                                System.out.println("promptNewRD " + for_hrc + ", " + p[p.length - 1]);
                                addFolderItems(ActivityCheckDocs.rdCode, date_key, for_hrc, "",isAudit);
                            }
                        }
                        return;
                    }
                }
            }
        }
        //skladCode
    }

    void addFolderItems(String parentcode, String date_key, String for_hrc, String for_sklad,boolean isAudit) {
        System.out.println("addFolderItems " + parentcode + ", " + date_key + ", " + for_hrc + ", " + for_sklad);
        String sql = "select prnt.code as parentcode,prnt.description as parentlabel,itms.code as itemcode,itms.description as itemlabel"//
                + "\n  from PokazateliChekLista itms"//
                + "\n  left join PokazateliChekLista prnt on prnt._idrref=itms.parent"//
                + "\n  where prnt.code='" + parentcode + "'"//
                + "\n  order by prnt.sortOrder,itms.sortOrder";
        System.out.println(sql);
        Bough rootList = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        //System.out.println(rootList.dumpXML());
        String[] rootItems = new String[rootList.children.size()];
        for (int i = 0; i < rootList.children.size(); i++) {
            rootItems[i] = rootList.children.get(i).child("itemlabel").value.property.value().trim();

            String code = rootList.children.get(i).child("itemcode").value.property.value().trim();
            System.out.println(code+": "+rootItems[i]);
            if (code.equals(ActivityCheckDocs.tpKontrCode)
                    || code.equals(ActivityCheckDocs.svKontrCode)
                    || code.equals(ActivityCheckDocs.rdKontrCode)
                    || code.equals(ActivityCheckDocs.skladCode)
                    || code.equals(ActivityCheckDocs.snabKontrCode)
            ) {
                //promptAddKontragent(code, hrc);
            } else {
                //createNewList(code, "", hrc);
                long id = ActivityCheckDayPodr.findOrCreateDocAndItems(for_hrc, for_sklad, code, "",isAudit);
            }
        }
    }

    void createGUI() {
        this.setTitle("Чек-листы");
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        dataGrid = new DataGrid(this).center.is(true)//
                .pageSize.is(gridPageSize)//
                .dataOffset.is(gridOffset)//
                .beforeFlip.is(new Task() {
                    @Override
                    public void doTask() {
                        requeryGridData();
                        flipGrid();
                    }
                });
        layoutless.child(dataGrid//
                .columns(new Column[]{ //
                        //columnIploaded.title.is("Выгружен").width.is(Auxiliary.tapSize * 2)//
                        //,
                        columnDate.title.is("Дата").width.is(Auxiliary.tapSize * 2)//
                        , columnTerritory.title.is("Территория").width.is(Auxiliary.tapSize * 9) //
                })//
                .width().is(layoutless.width().property)//
                .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
        );
        layoutless.child(new Knob(this)//
                .labelText.is("Добавить чек-лист")//
                .afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        promptPodrazdelenie(false);
                    }
                })//
                .left().is(0)//
                .top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
                .width().is(layoutless.width().property.divide(2))//
                .height().is(Auxiliary.tapSize)//
        );
        layoutless.child(new Knob(this)//
                .labelText.is("Добавить аудит")//
                .afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        promptPodrazdelenie(true);
                    }
                })//
                .left().is(layoutless.width().property.divide(2))//
                .top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
                .width().is(layoutless.width().property.divide(2))//
                .height().is(Auxiliary.tapSize)//
        );
    }

    public void requeryGridData() {
        String sql = "select"//
                + " d._id as doc_id"//
                + "  , d.data as doc_data"//
                + " , date(d.data/1000, 'unixepoch') as sortdata"//
                + " ,d.podr as hrc"//
				+ " ,d.nsv as nsv"//
                + " ,o.naimenovanie  as naimenovanie"//
                + " ,sk.naimenovanie  as skladname"
                +" ,a.stub as audit_id"
                + " from PokazateliChekListaDoc d"//
                + " left join sklady sk on trim(sk.kod)=trim(d.podr)"
                + " left join polzovateli u on trim(u.kod)=trim(d.podr)"//
                + " left join Podrazdeleniya o on o._idrref=u.podrazdelenie"//
                + " left join (select doc_id as stub from PokazateliChekListaItem itms"
                + " join  PokazateliChekLista pnkt on pnkt._idrref=itms.Pokazatel_id"
                + " left join  PokazateliChekLista fldr on pnkt.parent=fldr._idrref"
                + " left join  PokazateliChekLista rootfldr on fldr.parent=rootfldr._idrref"
                + " left join  PokazateliChekLista rootrootfldr on rootfldr.parent=rootrootfldr._idrref"
                + " where fldr.code='"+snabCode+"' or rootfldr.code='"+snabCode+"' or rootrootfldr.code='"+snabCode+"'"
                + " 	) a on a.stub=d._id"
                + " group by sortdata, hrc"//
                + " order by doc_data desc";//sortdata desc,naimenovanie";
        System.out.println(sql);
        gridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
        //System.out.println(gridData.dumpXML());
    }

    public void flipGrid() {
        dataGrid.clearColumns();
        if (gridData != null) {
            for (int i = 0; i < gridData.children.size(); i++) {
                Bough row = gridData.children.get(i);
                final String date_key = row.child("sortdata").value.property.value();
                final String for_hrc = row.child("hrc").value.property.value();
                final String audit_id = row.child("audit_id").value.property.value();
				final String nsv = row.child("nsv").value.property.value();
                Task task = new Task() {
                    @Override
                    public void doTask() {
                        //System.out.println("start " + date_key + "/" + for_hrc);
                        Intent intent = new Intent(ActivityCheckDocs.this, ActivityCheckDayPodr.class);
                        intent.putExtra("date_key", date_key);
                        if (Auxiliary.isNumeric(for_hrc)) {
                            intent.putExtra("for_hrc", "");
                            intent.putExtra("sklad_kod", for_hrc);
                        } else {
                            intent.putExtra("for_hrc", for_hrc);
                            intent.putExtra("sklad_kod", "");
                        }
                        intent.putExtra("is_audit", ""+(audit_id.length()>0));
                        startActivity(intent);
                    }
                };
				/*String v = row.child("vigruzhen2").value.property.value();
				columnIploaded.cell(v.equals("1")
						                    ? "да"
						                    : "нет", task);*/
                columnDate.cell((long) Numeric.string2double(row.child("doc_data").value.property.value()), task);
                String info = row.child("hrc").value.property.value().trim();
                if (Auxiliary.isNumeric(for_hrc)) {
                    info = "";
                }
                if (audit_id.length()>0) {
                    info = "аудит "+info;
                }
                if(nsv.trim().length()>0){
                	info=info+": "+nsv;
				}
                columnTerritory.cell(row.child("naimenovanie").value.property.value().trim() + row.child("skladname").value.property.value().trim(), task, info);
            }
        }
    }

    public void requeryData() {
        requeryGridData();
    }

    public void refreshGUI() {
        flipGrid();
        dataGrid.refresh();
        //System.out.println("refreshGUI");
    }
}
