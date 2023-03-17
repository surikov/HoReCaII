package sweetlife.android10.supervisor;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.it.Toggle;
import tee.binding.task.Task;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityCheckList2 extends Activity {

	Layoutless layoutless;
	MenuItem menuUdalit;
	//MenuItem menuAddKontragent;
	//MenuItem menuRemoveKontragent;
	MenuItem menuVigruzit;
	MenuItem menuKommentariy;
	String doc_id = "";
	DataGrid itemsGrid;
	ColumnText columnItemName = new ColumnText();
	ColumnText columnItemValue = new ColumnText();
	Bough itemsGridData;
	String kommentariy = "";
	String kontragent = "";
	String nsv= "";
	String kontragentName = "";
	public Expect requery = new Expect().status.is("Подождите...").task.is(new Task() {
		@Override
		public void doTask() {
			requeryData();
		}
	})//
			.afterDone.is(new Task() {
				@Override
				public void doTask() {
					refreshGUI();
				}
			})//
			;
	//boolean showKommentButton = false;
	//String kodPodrazdelenia = "";
	//String skladKod = "";
	String skladKod = "";
	String forhrc = "";
	String dat = "";

	/*public static Bough activityExatras(Activity activity) {
		Intent intent = activity.getIntent();
		Bundle extras = intent.getExtras();
		return bundle2bough(extras);
	}

	public static Bough bundle2bough(Bundle bundle) {
		Bough bough = new Bough();
		if(bundle == null) {
			bough.name.is("null");
		}
		else {
			bough.name.is("extra");
			for(String key : bundle.keySet()) {
				String value = bundle.getString(key);
				//System.out.println(key + ": " + value);
				bough.child(key).value.is(value);
			}
		}
		return bough;
	}*/

	static void unlock(String doc_id) {
		String sql = "update PokazateliChekListaDoc set vigruzhen =0"//
				+ " where _id=" + doc_id//
				+ ";";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	public static String safe(String desc) {
		desc = desc.replace("<", "");
		desc = desc.replace(">", "");
		desc = desc.replace("&", "&amp;");
		desc = desc.replace("\"", "&quot;");
		return desc;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuKommentariy = menu.add("Комментарий");
		menuUdalit = menu.add("Удалить");
		menuVigruzit = menu.add("Выгрузить чек-лист");
		//menuAddKontragent = menu.add("Добавить контрагента");
		//menuRemoveKontragent = menu.add("Удалить контрагента");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuUdalit) {
			promptUdalit();
			return true;
		}
		if (item == menuVigruzit) {
			promptVigruzit();
			return true;
		}
		if (item == menuKommentariy) {
			promptKommentariy();
			return true;
		}
		/*if(item == menuAddKontragent) {
			promptAddKontragent();
			return true;
		}
		if(item == menuRemoveKontragent) {
			promptRemoveKontragent();
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		requery.start(this);
	}

	/*void promptAddKontragent() {
	}

	void promptRemoveKontragent() {
	}*/
	void promptKommentariy() {
		final Note nn = new Note().value(this.kommentariy);
		Auxiliary.pickString(this, "Комментарий", nn, "Сохранить", new Task() {
			@Override
			public void doTask() {
				ActivityCheckList2.this.kommentariy = nn.value();
				String sql = "update PokazateliChekListaDoc set vsp='" + safe(ActivityCheckList2.this.kommentariy) + "' where _id=" + doc_id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		});
	}

	void promptUdalit() {
		Auxiliary.pickConfirm(this, "Удаление чек-листа", "Удалить", new Task() {
			@Override
			public void doTask() {
				String sql = "delete from PokazateliChekListaItem where doc_id=" + doc_id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				sql = "delete from PokazateliChekListaDoc where _id=" + doc_id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				ActivityCheckList2.this.finish();
			}
		});
	}

	void promptVigruzit() {
		Auxiliary.pickConfirm(this, "Выгрузка чек-листа", "Выгрузить", new Task() {
			@Override
			public void doTask() {
				//if(ActivityCheckList.this.in100m()){
				vigruzka();
				//}
			}
		});
	}

	void vigruzka() {
		String owner = Cfg.whoCheckListOwner();
		if (owner.length() < 1) {
			owner = ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim();
		}
		String stager="";
		if(nsv.length()>0){
			stager=stager+"/"+nsv;
		}
		//if(stager){}
		//kodPodrazdelenia="";
		//skladKod="145";
		String kodsql = "select Podrazdeleniya.kod as kod from Podrazdeleniya join Polzovateli on Polzovateli.podrazdelenie=Podrazdeleniya._IDRRef where trim(Polzovateli.Kod)='" + forhrc.trim() + "';";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(kodsql, null));
		//String podrData=forhrc;//kodPodrazdelenia;
		String podrData = data.child("row").child("kod").value.property.value();
		String skladData = "";
		//if(Auxiliary.isNumeric(kodPodrazdelenia.trim())){
		if (skladKod.length() > 0) {
			podrData = "";
			skladData = skladKod;//kodPodrazdelenia;
		}
		System.out.println("/" + podrData + "/" + skladData);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/ChekList\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Vigruzit>"//
				+ "\n	      <ns1:Chek>"//
				//+ "\n	        <ns1:Podr>" + kodPodrazdelenia + "</ns1:Podr>"//
				+ "\n	        <ns1:Podr>" + podrData + "</ns1:Podr>"//
				//+ "\n	        <ns1:Otvetstvennii>" + owner + "</ns1:Otvetstvennii>"//
				+ "\n	        <ns1:Otvetstvennii>" + owner+stager + "</ns1:Otvetstvennii>"//
				+ "\n	        <ns1:Data>" + dat + "</ns1:Data>"//
				;
		if (kontragent.trim().length() < 1) {
			String sql2 = "select "//
					+ " type as type"//
					+ " ,code as code"//
					+ " ,znachenie as znachenie"//
					+ " ,prim as prim"//
					+ " from PokazateliChekListaItem i"//
					+ " join PokazateliChekLista p on p._idrref=i.pokazatel_id"//
					//+ " where (p.isfolder='false' or p.isfolder=x'00') and (common='true' or common=x'01')"//
					+ " and doc_id='" + doc_id + "'" //
					;
			Bough cmn = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql2, null));
			//System.out.println(sql2 + ": " + cmn.dumpXML());
			for (int i = 0; i < cmn.children.size(); i++) {
				Bough row = cmn.children.get(i);
				xml = xml + "\n				<ns1:TablObshii>";
				xml = xml + "\n					<ns1:Pokazatel>" + row.child("code").value.property.value() + "</ns1:Pokazatel>";
				xml = xml + "\n					<ns1:Znachenie>" + for1C(row.child("znachenie").value.property.value(), row.child("type").value.property.value()) + "</ns1:Znachenie>";
				xml = xml + "\n					<ns1:Prim>" + row.child("prim").value.property.value() + "</ns1:Prim>";
				xml = xml + "\n				</ns1:TablObshii>";
			}
		} else {
			String sql = "select "//
					+ " type as type"//
					+ " ,code as code"//
					+ " ,znachenie as znachenie"//
					+ " ,prim as prim"//
					//+ " ,kontragent as kontragent"//
					+ " from PokazateliChekListaItem i"//
					+ " join PokazateliChekLista p on p._idrref=i.pokazatel_id"//
					//+ " where (p.isfolder='false' or p.isfolder=x'00') and (common='false' or common=x'00')"//
					+ " and doc_id='" + doc_id + "'" //
					;
			Bough ord = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(sql + ": " + ord.dumpXML());
			for (int i = 0; i < ord.children.size(); i++) {
				Bough row = ord.children.get(i);
				xml = xml + "\n			<ns1:TablPoKlientam>";
				xml = xml + "\n				<ns1:Pokazatel>" + row.child("code").value.property.value() + "</ns1:Pokazatel>";
				xml = xml + "\n				<ns1:Znachenie>" + for1C(row.child("znachenie").value.property.value(), row.child("type").value.property.value()) + "</ns1:Znachenie>";
				xml = xml + "\n				<ns1:Kontragent>" + kontragent + "</ns1:Kontragent>";
				xml = xml + "\n				<ns1:Prim>" + row.child("prim").value.property.value() + "</ns1:Prim>";
				xml = xml + "\n			</ns1:TablPoKlientam>";
			}
		}
		//kodPodrazdelenia
		xml = xml + "\n	        <ns1:SSS>" + safe(this.kommentariy) + "</ns1:SSS>"//
				+ "\n	        <ns1:ODR>-</ns1:ODR>"//
				+ "\n	        <ns1:NSV>-</ns1:NSV>"//
				+ "\n	        <ns1:VSP>-</ns1:VSP>"//
				+ "\n	        <ns1:Sklad>" + skladData + "</ns1:Sklad>"//
				+ "\n	      </ns1:Chek>"//
				+ "\n	    </ns1:Vigruzit>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>";
		//safe("");

		final RawSOAP s = new RawSOAP();
		s.afterError.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn("Ошибка выгрузки "+s.exception.property.value().getMessage(), ActivityCheckList2.this);
				requery.start(ActivityCheckList2.this);
			}
		})//
				.afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println(s.rawResponse);
				String tx = s.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
				Auxiliary.warn("Выгружено: " + tx, ActivityCheckList2.this);
				if (tx.equals("ок")) {
					lock();
				}
				requery.start(ActivityCheckList2.this);
				//ActivityCheckList.this.finish();
			}
		})//
				//.url.is(Settings.getInstance().getBaseURL() + "ChekList.1cws")//
				//.url.is("http://testservice.swlife.ru/lipuzhin_hrc/ChekList.1cws")//

				//.url.is(Settings.getInstance().getBaseURL() + "ChekList11111111111111.1cws")//
				.url.is(Settings.getInstance().getBaseURL() + "ChekList.1cws")//
				.xml.is(xml)//
		;
		System.out.println(s.xml.property.value());
		System.out.println(s.url.property.value());
		s.startLater(ActivityCheckList2.this, "Выгрузка чек-листа", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
	}

	String for1C(String txt, String kind) {
		if (txt.length() < 1) {
			return "0";
		} else {
			return txt;
		}
	}

	String _for1C(String txt, String kind) {
		if (kind.equals("Булево")) {
			return Numeric.string2double(txt) > 0
					? "Истина"
					: "Ложь";
		}
		if (kind.equals("Дата")) {
			long d = (long) Numeric.string2double(txt);
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			c.setTimeInMillis(d);
			DateFormat to = new SimpleDateFormat("yyyyMMdd");
			return to.format(c.getTime());
		}
		return safe(txt);
	}

	void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		doc_id = Auxiliary.activityExatras(this).child("doc_id").value.property.value();
		//showKommentButton = Auxiliary.activityExatras(this).child("showKommentButton").value.property.value()=="1";
		String sql = "select"//
				+ "\n d.podr as who"//
				+ "\n     ,d.Otvetstvennii as owner"//
				+ "\n     ,d.data as data"//
				+ "\n     ,p.description as folder"//
				+ "\n     ,i.description as name"//
				+ "\n     ,k.naimenovanie as kontragentName"//
				+ "\n     ,k.kod as kontragent"//
				+ "\n     ,pz.kod as kodPodrazdelenia"//
				+ "\n     ,skl.kod as kodSklada"//
				+ "\n     ,d.vsp as kommentariy"//
				+ "\n     ,d.nsv as nsv"//
				+ "\n     ,d.podr as podr"//
				+ "\n     ,usr.kod as hrc"//
				+ "\n from pokazatelicheklistadoc d"//

				+ "\n     join pokazatelicheklista i on i.code=d.sss"//
				+ "\n     join pokazatelicheklista p on p._idrref=i.parent"//
				+ "\n     left join polzovateli usr on usr.kod=d.podr"//
				+ "\n     left join podrazdeleniya pz on usr.podrazdelenie=pz._idrref"//
				+ "\n     left join sklady skl on trim(skl.kod)=trim(d.podr)"//
				+ "\n     left join kontragenty k on d.odr=k.kod"//
				+ "\n where d._id=" + doc_id + ";";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(sql);
		//Date wh = new Date((long) Numeric.string2double(b.child("row").child("data").value.property.value()));
		//podr = items.child("row").child("podr").value.property.value();
		kontragent = b.child("row").child("kontragent").value.property.value();
		nsv= b.child("row").child("nsv").value.property.value();
		kontragentName = b.child("row").child("kontragentName").value.property.value();
		/*kodPodrazdelenia = b.child("row").child("kodPodrazdelenia").value.property.value();
		if(kodPodrazdelenia.trim().length()<1) {
			kodPodrazdelenia = b.child("row").child("podr").value.property.value();
		}*/
		forhrc = b.child("row").child("hrc").value.property.value().trim();
		skladKod = b.child("row").child("kodSklada").value.property.value().trim();
		kommentariy = b.child("row").child("kommentariy").value.property.value();
		kommentariy = b.child("row").child("kommentariy").value.property.value();
		long d = (long) Numeric.string2double(b.child("row").child("data").value.property.value());
		Date wh = new Date(d);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d);
		DateFormat to = new SimpleDateFormat("yyyyMMdd");
		dat = to.format(c.getTime());
		this.setTitle(Auxiliary.rusDate.format(wh) + ": " + b.child("row").child("who").value.property.value() + "/" + b.child("row").child("name").value.property.value() + " " + kontragentName);
		itemsGrid = new DataGrid(this).center.is(true)//
				.pageSize.is(999)//itemsGridPageSize)//
				//.dataOffset.is(itemsGridOffset)//
				.beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryItemGridData();
						flipItemsGrid();
					}
				});
		layoutless.child(itemsGrid//
				.noHead.is(true)//
				.columns(new Column[]{ //
						columnItemName.title.is("Наименование").width.is(layoutless.width().property.minus(Auxiliary.tapSize * 5))//
						, columnItemValue.title.is("Значение").width.is(Auxiliary.tapSize * 5) //
				})//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		if (kontragent.trim().length() > 1) {
			layoutless.child(new Knob(this)//
					.labelText.is("Комментарий")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							promptKommentariy();
						}
					})//
					.left().is(0)//
					.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
					.width().is(layoutless.width().property.divide(2))//
					.height().is(Auxiliary.tapSize)//
			);
			layoutless.child(new Knob(this)//
					.labelText.is("Выгрузить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							promptVigruzit();
						}
					})//
					.left().is(layoutless.width().property.divide(2))//
					.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
					.width().is(layoutless.width().property.divide(2))//
					.height().is(Auxiliary.tapSize)//
			);
		} else {
			layoutless.child(new Knob(this)//
					.labelText.is("Выгрузить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							promptVigruzit();
						}
					})//
					.left().is(0)//
					.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
					.width().is(layoutless.width().property)//
					.height().is(Auxiliary.tapSize)//
			);
		}
	}

	public void flipItemsGrid() {
		itemsGrid.clearColumns();
		if (itemsGridData != null) {
			String curCat = itemsGridData.child("row").child("description0").value.property.value();
			int cntr = 0;
			int color = 0x0f000000;
			int colorIdx = 0;
			for (int i = 0; i < itemsGridData.children.size(); i++) {
				Bough row = itemsGridData.children.get(i);
				String des = "";
				String cat = "";
				String typ = "";
				if (row.child("description2").value.property.value().trim().length() > 0) {
					des = row.child("description2").value.property.value();
					cat = row.child("description1").value.property.value();
					typ = row.child("type2").value.property.value();
				} else {
					cat = row.child("description0").value.property.value();
					des = row.child("description1").value.property.value();
					typ = row.child("type1").value.property.value();
				}
				//curCat=cat;
				/*final String description = row.child("description0").value.property.value()
						+"/"+row.child("description1").value.property.value()
						+"/"+row.child("description2").value.property.value()
						;*/
				final String description = des;
				final String _id = row.child("_id").value.property.value();
				final String znachenie = row.child("znachenie").value.property.value();
				final String type = typ;//row.child("type").value.property.value();
				final String prim = row.child("prim").value.property.value();
				//String cat = row.child("category").value.property.value();
				if (!cat.equals(curCat)) {
					/*if(color==0xffeeeeee){
						color=0xffffffff;
					}else{
						color=0xffeeeeee;
					}*/
					columnItemName.cell(cat);
					columnItemValue.cell("");
					curCat = cat;
					cntr++;
					if (color == 0x0f000000) {
						color = 0x0f000066;
					} else {
						color = 0x0f000000;
					}
					//colorIdx++;
				}
				/*if(colorIdx == 1) {
					color = 0x33ff0000;
				}
				if(colorIdx == 2) {
					color = 0x33ff9933;
				}
				if(colorIdx == 3) {
					color = 0x33ffff00;
				}
				if(colorIdx == 4) {
					color = 0x3300ff00;
				}
				if(colorIdx == 5) {
					color = 0x3300ffff;
				}
				if(colorIdx == 6) {
					color = 0x330000ff;
				}
				if(colorIdx == 7) {
					color = 0x339966cc;
				}*/
				final int nn = cntr;
				Task tap = new Task() {
					@Override
					public void doTask() {
						//System.out.println(ActivityCheckList2.this.kontragent + ": " + ActivityCheckList2.this.kontragentName);
						//if(in100m()){
						promptValue(true, _id, description, znachenie, prim, type, nn);
						//promptValue(true, _id, description, columnItemValue.cells.get(nn).labelText.property.value(), type, nn);
						//}
					}
				};//mOrder
				columnItemName.cell(//row.child("groupsOrder").value.property.value()+". "+row.child("paramatersOrder").value.property.value()+". "+
						//description,color, tap,"");
						//curCat + ": " +
						description
						//+" /"+type+"/"+prim+"/"+znachenie
						, color, tap);
				columnItemValue.cell(formatZnachenie(znachenie, type, prim), color, tap);
				cntr++;
			}
		}
	}

	public void requeryItemGridData() {
		/*String sql = "select "//
				             + " i._id as _id"//
				             + ",parameters.description as description"//
				             + ",i.znachenie as znachenie "//
				             + ",i.prim as prim "//
				             + ",parameters.type as type"//
				             + " ,groups.description as category"//
				             + " ,parameters.sortorder as parametersOrder"//
				             + " ,groups.sortorder as groupsOrder"//
				             + " from PokazateliChekListaItem i"//
				             + " join PokazateliChekLista parameters on parameters._idrref=i.pokazatel_id"//
				             + " join PokazateliChekLista groups on groups._idrref=parameters.parent"//
				             + " and i.kontragent='" + kontragent + "'"//
				             + " where (parameters.common='false' or parameters.common=x'00') and i.doc_id=" + doc_id//
				             //+ " order by groups.sortorder,parameters.sortorder"//
				+ " order by groups.description,parameters.description"//
				;*/
		String sql = "select"//
				+ " itm._id as _id"//
				+ " ,fo0.description as description0"//
				+ " ,fo1.description as description1"//
				+ " ,fo2.description as description2"//
				+ " ,itm.znachenie as znachenie "//
				+ " ,fo1.type as type1"//
				+ " ,fo2.type as type2"//
				+ " ,itm.prim as prim "//
				+ "  from pokazatelicheklistadoc d"//
				+ "      join pokazatelicheklista fo0 on fo0.code=d.sss"//
				+ "      left join pokazatelicheklista fo1 on fo0._idrref=fo1.parent"//
				+ "      left join pokazatelicheklista fo2 on fo1._idrref=fo2.parent"//
				+ "      join pokazatelicheklistaitem itm on itm.doc_id=d._id and (fo2._idrref=itm.pokazatel_id or fo1._idrref=itm.pokazatel_id)"//
				+ "  where d._id=" + doc_id//
				+ "  order by fo1.sortOrder,fo2.sortOrder";
		//System.out.println(sql);
		itemsGridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(itemsGridData.dumpXML());
	}

	public void requeryData() {
		requeryItemGridData();
	}

	public void refreshGUI() {
		flipItemsGrid();
		itemsGrid.refresh();
	}

	void replaceItemValue(int nn, String v) {
		//columnItemValue.cells.get(2).labelText.property.value("asd");
		columnItemValue.cells.get(nn).labelText.property.value(v);
		//System.out.println("*************"+nn+" / "+v);
	}

	void replacePropValue(int nn, String v, String prim) {
		columnItemValue.cells.get(nn).labelText.property.value(v);
	}

	void promptValue(boolean noRefresh, String _id, String description, String znachenie, String prim, String type, int nn) {
		/*if(type.contains("Булево")) {
			//if(type.equals("Булево")) {
			promptBoolean(noRefresh, _id, description, nn);
			return;
		}
		//if(type.equals("Дата")) {
		if(type.contains("Дата")) {
			promptDate(noRefresh, _id, description, znachenie, nn);
			return;
		}
		promptString(noRefresh, _id, description, znachenie, nn);
*/
		promptItem012(noRefresh, _id, description, nn);
	}

	void promptItem012(final boolean noRefresh, final String _id, String description, final int nn) {
		String gsql = "select znachenie as znachenie,prim as prim from PokazateliChekListaItem where _id=" + _id;
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(gsql, null));
		final Toggle checkBoxValue = new Toggle();
		double n = Numeric.string2double(b.child("row").child("znachenie").value.property.value());
		if (n > 1) {
			checkBoxValue.value(true);
		}
		final Note note = new Note().value(b.child("row").child("prim").value.property.value());
		Auxiliary.pick(this, "", new SubLayoutless(this)//
						.child(new RedactToggle(this).labelText.is(description).yes.is(checkBoxValue).left().is(Auxiliary.tapSize * 0.5).top().is(0).width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(note).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 2)//
				, "Сохранить", new Task() {
					@Override
					public void doTask() {
						String txtVal = "0";
						if (checkBoxValue.value()) {
							txtVal = "2";
						} else {
							txtVal = "1";
						}
						String sql = "update PokazateliChekListaItem set znachenie='"//
								+ txtVal//
								+ "', prim='" + note.value()//
								+ "' where _id=" + _id//
								+ ";";
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						unlock();
						if (noRefresh) {
							replaceItemValue(nn, formatZnachenie(txtVal, "Булево", note.value()));
						} else {
							requery.start(ActivityCheckList2.this);
						}
					}
				}, null, null, "Очистить", new Task() {
					@Override
					public void doTask() {
						String sql = "update PokazateliChekListaItem set znachenie='0', prim='' where _id=" + _id + ";";
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						unlock();
						requery.start(ActivityCheckList2.this);
					}
				});
	}

	String yesNo(String znachenie) {
		if (znachenie.trim().equals("1")) {
			return "нет";
		} else {
			if (znachenie.trim().equals("2")) {
				return "да";
			} else {
				return "";
			}
		}
	}

	String formatZnachenie(String znachenie, String type, String prim) {
		String r = yesNo(znachenie);
		if (prim.trim().length() > 0) {
			r = r + ", " + prim;
		}
		return r;
	}

	String _formatZnachenie(String znachenie, String type, String prim) {
		if (type.contains("Булево")) {
			if (znachenie.trim().length() < 1) {
				return "нет";
			}
			double n = Numeric.string2double(znachenie);
			//return znachenie+";"+(n > 0 ? "да" : "нет");
			String r = n > 0
					? "да"
					: "нет";
			if (prim.trim().length() > 0) {
				r = r + ", " + prim;
			}
			return r;
		}
		if (type.equals("Дата")) {
			long mills = (long) Numeric.string2double(znachenie);
			//new Date().getTime();
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			c.setTimeInMillis(mills);
			DateFormat to = new SimpleDateFormat("dd.MM.yyyy");
			return to.format(c.getTime());
		}
		return znachenie;
	}

	void lock() {
		String sql = "update PokazateliChekListaDoc set vigruzhen =1"//
				+ " where _id=" + doc_id//
				+ ";";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		//System.out.println("skip lock");
	}

	void unlock() {
		/*String sql = "update PokazateliChekListaDoc set vigruzhen =0"//
				+ " where _id=" + doc_id//
				+ ";";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);*/
		unlock(doc_id);
	}
}
