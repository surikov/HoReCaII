package sweetlife.android10.supervisor;

import java.util.Date;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityCheckDayPodr extends Activity {

	/*
		public static String tpCode = "000001001";
		public static String tpKontrCode = "000001005";
		public static String svCode = "000001002";
		public static String svKontrCode = "000001012";
		public static String rdCode = "000001003";
		public static String rdKontrCode = "000001022";
		*/
	public int gridPageSize = 30;
	public Numeric gridOffset = new Numeric();
	Layoutless layoutless;
	MenuItem menuDobavit;
	MenuItem menuStajer;
	//MenuItem menuKontragent;
	DataGrid dataGrid;
	ColumnText columnIploaded = new ColumnText();
	//ColumnText columnStatus = new ColumnText();
	ColumnDescription columnFolder = new ColumnDescription();
	ColumnText columnKomment = new ColumnText();
	Bough gridData;
	String date_key = "";
	String for_hrc = "";
	String sklad_kod = "";
	String stajer_kod = "";
	boolean is_audit = false;

	public Expect requery = new Expect()//
			.status.is("Подождите...")//
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

	public void setStajer() {
		//stajer_kod
		//long id = findToday(hrcval, parentCode, kontrKod);
		/*String sql = "select _id as _id,* from PokazateliChekListaDoc"//
				+ " where podr='" + this.for_hrc + "'"//
				+ " and date(data/1000, 'unixepoch')='" + this.date_key + "';"//
				;
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println("setStajer " + sql+": "+items.dumpXML());*/
		String sql = "update PokazateliChekListaDoc set nsv='" + this.stajer_kod + "' where podr='" + this.for_hrc + "'"//
				+ " and date(data/1000, 'unixepoch')='" + this.date_key + "';"//
				;
		System.out.println("setStajer " + sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		resetTitle();
	}

	void readStajer() {
		String sql = "select nsv as nsv from PokazateliChekListaDoc where podr='" + this.for_hrc + "'"//
				+ " and date(data/1000, 'unixepoch')='" + this.date_key + "';"//
				;
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		this.stajer_kod = items.child("row").child("nsv").value.property.value();
	}

	public static long findOrCreateDocAndItems(String forhrc, String forsklad, String parentCode, String kontrKod, boolean isAudit) {
		String hrcval = forhrc;
		if (forsklad.length() > 0) {
			hrcval = forsklad;
		}
		long id = findToday(hrcval, parentCode, kontrKod);
		System.out.println("findOrCreateDocAndItems " + id + ", " + hrcval + ", " + parentCode + ", " + kontrKod + ", " + forsklad + ", " + forhrc);
		if (id < 1) {
			String sql = "insert into PokazateliChekListaDoc (odr,sss,Otvetstvennii,Data,Podr) values ("//
					+ "'" + kontrKod + "'"//
					+ ",'" + parentCode + "'"//
					+ ",'" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "'"//
					+ "," + new Date().getTime() + ""//
					+ ",'" + hrcval + "'"//
					+ ")";
			System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			id = findToday(hrcval, parentCode, kontrKod);
			sql = "insert into pokazatelicheklistaitem (pokazatel_id,doc_id)"//
					+ "  select case when p3._idrref is null then p2._idrref else p3._idrref end as item_idrref"//
					+ "   ,d._id as main_id"//
					+ "  from pokazatelicheklistadoc d"//
					+ "   join pokazatelicheklista p1 on d.sss=p1.code"//
					+ "   join pokazatelicheklista p2 on p2.parent=p1._idrref"//
					+ "   left join pokazatelicheklista p3 on p3.parent=p2._idrref"//
					+ "  where d._id=" + id;
			System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}
		return id;
	}

	public static long findToday(String hrc, String parentCode, String kontrKod) {
		String sql = "select _id as _id from PokazateliChekListaDoc"//
				+ " where podr='" + hrc + "'"//
				+ " and SSS='" + parentCode + "'"//
				+ " and ODR='" + kontrKod + "'"//
				+ " and date(data/1000, 'unixepoch')=date()"//
				;
		//System.out.println("findToday: " + sql);
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(items.dumpXML());
		return (long) Numeric.string2double(items.child("row").child("_id").value.property.value());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuDobavit = menu.add("Добавить чек-лист");
		//menuKontragent = menu.add("Добавить контрагента");
		menuStajer = menu.add("Назначить стажёра");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuDobavit) {
			/*Intent intent = new Intent();
			intent.setClass(Activity_Route.this, Dialog_Upload.class);
			startActivity(intent);*/
			//promptTPSVRD();
			//_promptNew();
			promptFolders();
			return true;
		}
		if (item == menuStajer) {
			promptStajer();
			return true;
		}
		/*if(item == menuKontragent) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		requery.start(this);
	}

	void resetTitle() {
		String auxHRC = (stajer_kod.trim().length() > 0) ? (": " + stajer_kod) : "";
		if (sklad_kod.length() > 0) {
			this.setTitle("Чек-листы " + date_key + "/склад " + sklad_kod + auxHRC);
		} else {
			if (is_audit) {
				this.setTitle("Чек-листы " + date_key + "/аудит" + auxHRC);
			} else {
				this.setTitle("Чек-листы " + date_key + "/" + for_hrc + auxHRC);
			}
		}
	}

	void createGUI() {
		this.date_key = Auxiliary.activityExatras(this).child("date_key").value.property.value();
		this.for_hrc = Auxiliary.activityExatras(this).child("for_hrc").value.property.value();
		this.sklad_kod = Auxiliary.activityExatras(this).child("sklad_kod").value.property.value();
		this.is_audit = Auxiliary.activityExatras(this).child("is_audit").value.property.value().toLowerCase().trim().equals("true");

		//System.out.println(Auxiliary.activityExatras(this).dumpXML());
		readStajer();
		setStajer();
		//resetTitle();

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
						columnIploaded.title.is("Выгружен").width.is(Auxiliary.tapSize * 2)//
						, columnFolder.title.is("Чек-лист").width.is(Auxiliary.tapSize * 9)//
						, columnKomment.title.is("Комментарий").width.is(Auxiliary.tapSize * 5) //
				})//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this)//
				.labelText.is("Добавить контрагента")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptKontragentForHRC();
					}
				})//
				.left().is(0)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(layoutless.width().property)//
				.height().is(Auxiliary.tapSize)//
		);
	}

	public void requeryGridData() {
		String podr = this.for_hrc.trim();
		if (this.sklad_kod.length() > 0) {
			podr = this.sklad_kod;
		}
		String sql = "select"//
				+ "\n d._id as doc_id"//
				+ "\n , d.data as data"//
				+ "\n , date(d.data/1000, 'unixepoch') as sortdate"//
				+ "\n ,d.podr as podr"//
				+ "\n ,d.vigruzhen as vigruzhen"//
				+ "\n ,o.naimenovanie  as naimenovanie"//
				+ "\n ,u.kod as hrc"//
				+ "\n ,i.description as folder"//
				+ "\n ,k.naimenovanie as kontragent"//
				+ "\n ,d.vsp as komment"//
				+ "\n from PokazateliChekListaDoc d"//
				+ "\n left join polzovateli u on trim(u.kod)=trim(d.podr)"//
				+ "\n left join Podrazdeleniya o on o._idrref=u.podrazdelenie"//
				+ "\n left join pokazatelicheklista i on i.code=d.sss"//
				+ "\n left join kontragenty k on d.odr=k.kod"//
				//+ " where trim(hrc)='" + this.for_hrc + "' and sortdate='" + this.date_key + "'"//
				+ "\n where sortdate='" + this.date_key + "' and trim(d.podr)='" + podr.trim() + "'"//
				+ "\n order by i.sortorder,kontragent";
		System.out.println(sql);
		gridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
	}

	public void flipGrid() {
		dataGrid.clearColumns();
		if (gridData != null) {
			for (int i = 0; i < gridData.children.size(); i++) {
				Bough row = gridData.children.get(i);
				final String m = row.child("doc_id").value.property.value();
				Task task = new Task() {
					@Override
					public void doTask() {
						//System.out.println("start intent " + m);
						/*
						Intent intent = new Intent(ActivityCheckDocs.this, ActivityCheckList2.class);
						intent.putExtra("doc_id", m);
						//System.out.println("start intent");
						startActivity(intent);
						*/
						Intent intent = new Intent(ActivityCheckDayPodr.this, ActivityCheckList2.class);
						intent.putExtra("doc_id", m);
						//System.out.println("start intent");
						startActivity(intent);
					}
				};
				String v = row.child("vigruzhen").value.property.value();
				columnIploaded.cell(v.equals("1")
						? "да"
						: "нет", task);
				columnKomment.cell(row.child("komment").value.property.value(), task);
				columnFolder.cell(row.child("folder").value.property.value().trim()
						//+", "+row.child("hrc").value.property.value().trim()//
						, task//
						//, row.child("hrc").value.property.value().trim()//
						, row.child("kontragent").value.property.value().trim()//
				);
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

	void promptKontragentForHRC() {
		if (this.date_key.equals(Auxiliary.sqliteDate.format(new Date()))) {
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				Bough one = Cfg.territory().children.get(i);
				if (one.child("hrc").value.property.value().trim().equals(this.for_hrc)) {
					String territory = one.child("territory").value.property.value().trim();
					String p[] = territory.split("/");
					System.out.println("prompt " + territory);
					if (this.is_audit) {
						System.out.println("prompt audit " + this.for_hrc);
						//promptNewTP(this.for_hrc);
						promptAddKontragent(ActivityCheckDocs.snabKontrCode, this.for_hrc);
					} else {
						if (p[0].trim().length() > 0 || (p.length > 3 && p[3].trim().equals("VIP Н.Новгород"))) {
							//if(p[0].trim().length() > 0) {
							System.out.println("promptNewTP " + this.for_hrc);
							//promptNewTP(this.for_hrc);
							promptAddKontragent(ActivityCheckDocs.tpKontrCode, this.for_hrc);
						} else {
							if (p[1].trim().length() > 0) {
								System.out.println("promptNewSV " + this.for_hrc);
								//promptNewSV(this.for_hrc);
								promptAddKontragent(ActivityCheckDocs.svKontrCode, this.for_hrc);
							} else {
								System.out.println("promptNewRD " + this.for_hrc);
								//promptNewRD(this.for_hrc);
								promptAddKontragent(ActivityCheckDocs.rdKontrCode, this.for_hrc);
							}
						}
					}
					break;
				}
			}
		} else {
			Auxiliary.warn("Контрагентов можно добавить только на сегодняшний день. Создайте новый чек-лист", this);
		}
	}

	void promptStajer() {
		String sql = "select po.kod as hrc,fio.naimenovanie as fio "
				+ "\n	from Polzovateli po "
				+ "\n		join PhizLicaPolzovatelya fp on fp.Polzovatel=po._idrref"
				+ "\n		join PhizicheskieLica fio on fio._IDRRef=fp.PhizLico"
				+ "\n	where po.PometkaUdaleniya<>x'01' and po.kod like 'pers%'"
				+ "\n	group by po.kod"
				+ "\n	order by po.kod,fp.DataKon asc"
				+ "\n	limit 2000;";
		final Bough users = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		String[] data = new String[users.children.size() + 1];
		data[0] = "[нет]";
		for (int i = 0; i < users.children.size(); i++) {
			data[i + 1] = users.children.get(i).child("hrc").value.property.value().trim() + ": " + users.children.get(i).child("fio").value.property.value().trim();
		}
		final Numeric nn = new Numeric();
		Auxiliary.pickSingleChoice(ActivityCheckDayPodr.this, data, nn, null, new Task() {
			@Override
			public void doTask() {
				if (nn.value().intValue() > 0) {
					stajer_kod = users.children.get(nn.value().intValue() - 1).child("hrc").value.property.value().trim();
				} else {
					stajer_kod = "";
				}
				//ApplicationHoreca.getInstance().getDataBase().execSQL("");
				//System.out.println("stajer "+stajer_kod);
				setStajer();

			}
		}, null, null, null, null);
	}

	void promptFolders() {
		if (this.date_key.equals(Auxiliary.sqliteDate.format(new Date()))) {
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				Bough one = Cfg.territory().children.get(i);
				if (one.child("hrc").value.property.value().trim().equals(this.for_hrc)) {
					String territory = one.child("territory").value.property.value().trim();
					String p[] = territory.split("/");
					System.out.println("prompt " + territory);

					if (p[0].trim().length() > 0) {
						System.out.println("promptNewTP " + this.for_hrc);
						promptNewTP(this.for_hrc);
					} else {
						if (p[1].trim().length() > 0) {
							System.out.println("promptNewSV " + this.for_hrc);
							promptNewSV(this.for_hrc);
						} else {
							System.out.println("promptNewRD " + this.for_hrc);
							promptNewRD(this.for_hrc);
						}
					}
					break;
				}
			}
		} else {
			Auxiliary.warn("Создайте новый чек-лист на сегодняшний день", this);
		}
	}

	void promptNewTP(String hrc) {
		promptMenuKind(ActivityCheckDocs.tpCode, hrc, "");
	}

	void promptNewSV(String hrc) {
		promptMenuKind(ActivityCheckDocs.svCode, hrc, "");
	}

	void promptNewRD(String hrc) {
		promptMenuKind(ActivityCheckDocs.rdCode, hrc, "");
	}

	void promptMenuKind(String kindCode, final String hrc, final String sklad) {
		String sql = "select prnt.code as parentcode,prnt.description as parentlabel,itms.code as itemcode,itms.description as itemlabel"//
				+ "  from PokazateliChekLista itms"//
				+ "  left join PokazateliChekLista prnt on prnt._idrref=itms.parent"//
				+ "  where prnt.code='" + kindCode + "'"//
				+ "  order by prnt.sortOrder,itms.sortOrder";
		final Bough rootList = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(rootList.dumpXML());
		String[] rootItems = new String[rootList.children.size()];
		for (int i = 0; i < rootList.children.size(); i++) {
			rootItems[i] = rootList.children.get(i).child("itemlabel").value.property.value().trim();
		}
		final Numeric nn = new Numeric();
		Auxiliary.pickSingleChoice(ActivityCheckDayPodr.this, rootItems, nn, null, new Task() {
			@Override
			public void doTask() {
				String code = rootList.children.get(nn.value().intValue()).child("itemcode").value.property.value().trim();
				//System.out.println(rootList.children.get(nn.value().intValue()).dumpXML());
				if (code.equals(ActivityCheckDocs.tpKontrCode) || code.equals(ActivityCheckDocs.svKontrCode) || code.equals(ActivityCheckDocs.rdKontrCode)) {
					promptAddKontragent(code, hrc);
				} else {
					createNewList(code, "", hrc, sklad);
				}
			}
		}, null, null, null, null);
	}

	void createNewList(String code, String kontragent, String hrc, String sklad) {
		long id = findOrCreateDocAndItems(hrc, sklad, code, kontragent, is_audit);
		System.out.println("createNewList " + code + ", " + kontragent + ", " + hrc);
		Intent intent = new Intent(ActivityCheckDayPodr.this, ActivityCheckList2.class);
		intent.putExtra("doc_id", "" + id);
		startActivity(intent);
	}

	void promptAddKontragent(final String kindCode, final String hrc) {
		System.out.println("promptAddKontragent " + Cfg.kontragenty().dumpXML());
		String sql = "select p.kod as kod from Podrazdeleniya p join Polzovateli hrc on p._idrref=hrc.Podrazdelenie where trim(hrc.kod)='" + hrc + "'";
		System.out.println("promptAddKontragent " + sql);
		Bough kodRow = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(kodRow.dumpXML());
		String kod = kodRow.child("row").child("kod").value.property.value();
		final Bough kk = Cfg.kontragentyByKod(kod);
		System.out.println(kk.dumpXML());
		final String[] items = new String[kk.children.size()];
		for (int i = 0; i < kk.children.size(); i++) {
			items[i] = kk.children.get(i).child("kod").value.property.value() + ": " + kk.children.get(i).child("naimenovanie").value.property.value();
		}
		final Numeric defaultSelection = new Numeric();

		Auxiliary.pickFilteredChoice(this, items, defaultSelection, new Task() {
			@Override
			public void doTask() {
				String kodkontragent = kk.children.get(defaultSelection.value().intValue()).child("kod").value.property.value();
				createNewList(kindCode, kodkontragent, hrc, "");
			}
		});
		/*
		Auxiliary.pickSingleChoice(this, items, defaultSelection, null, new Task() {
			@Override
			public void doTask() {
				String kodkontragent = kk.children.get(defaultSelection.value().intValue()).child("kod").value.property.value();
				createNewList(kindCode, kodkontragent, hrc,"");
			}
		}, null, null, null, null);
		*/
	}
}
