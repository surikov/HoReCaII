package sweetlife.android10.supervisor;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.GPS;
import sweetlife.android10.Settings;
import sweetlife.android10.gps.GPSInfo;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.it.Toggle;
import tee.binding.task.Task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

public class ActivityCheckList extends Activity {

	Layoutless layoutless;
	Bough extras = new Bough();
	String doc_id = "";
	String podr = "";
	String kontragent = "";
	String kontragentName = "";
	int vigruzhen = 0;
	Numeric propSplit = new Numeric();
	Numeric klientSplit = new Numeric();
	MenuItem menuUdalit;
	MenuItem menuAddKontragent;
	MenuItem menuRemoveKontragent;
	MenuItem menuVigruzit;
	DataGrid itemsGrid;
	DataGrid propGrid;
	DataGrid klientGrid;
	ColumnText columnItemName = new ColumnText();
	ColumnText columnItemValue = new ColumnText();
	ColumnDescription columnProp = new ColumnDescription();
	ColumnDescription columnKlientName = new ColumnDescription();
	Bough itemsGridData;
	//public int itemsGridPageSize = 30;
	//public Numeric itemsGridOffset = new Numeric();
	Bough propGridData;
	Bough klientGridData;
	//public int propGridPageSize = 30;
	//public Numeric propGridOffset = new Numeric();
	String sss;
	String odr;
	String nsv;
	String vsp;
	String dat;
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

	public static Bough activityExatras(Activity activity) {
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
	}

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


	public static boolean propExists(String kontragent, String doc_id, String _idrref) {
		String sql = "select count(_id) as c from PokazateliChekListaItem where kontragent=" + kontragent//
				             + " and doc_id=" + doc_id + " and pokazatel_id=x'" + _idrref + "';";
		//System.out.println(sql);
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		double n = Numeric.string2double(items.child("row").child("c").value.property.value());
		if(n > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public static void addForKontragent(String kontragent, String podr, String doc_id) {
		/*boolean supervisor = ActivityCheckDocs.isSupervisor(podr);
		String sql = "select _idrref as _idrref from PokazateliChekLista where (deletionmark='false' or deletionmark=x'00') and (isfolder='false' or isfolder=x'00') and (common='false' or common=x'00')";
		if(supervisor) {
			sql = sql + " and (PoSV='true' or PoSV=x'01')";
		}
		else {
			sql = sql + " and (PoSV='false' or PoSV=x'00')";
		}
		sql = sql + " order by sortorder;";
		System.out.println("addForKontragent " + podr + " supervisor " + supervisor);
		System.out.println(sql);
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for(int i = 0; i < items.children.size(); i++) {
			Bough row = items.children.get(i);
			String _idrref = row.child("_idrref").value.property.value();
			if(!propExists(kontragent, doc_id, _idrref)) {
				String sql2 = "insert into PokazateliChekListaItem (pokazatel_id,doc_id,kontragent) values ("//
						              + "x'" + _idrref + "'"//
						              + ", " + doc_id //
						              + ", '" + kontragent + "'"//
						              + ");";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql2);
			}
		}
		unlock(doc_id);*/
	}

	void promptUdalit() {
		Auxiliary.pickConfirm(this, "Удаление чек-листа", "Удалить", new Task() {
			@Override
			public void doTask() {
				String sql = "delete from PokazateliChekListaItem where doc_id=" + doc_id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				sql = "delete from PokazateliChekListaDoc where _id=" + doc_id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				ActivityCheckList.this.finish();
			}
		});
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

	String for1C(String txt, String kind) {
		if(kind.equals("Булево")) {
			return Numeric.string2double(txt) > 0
					       ? "Истина"
					       : "Ложь";
		}
		if(kind.equals("Дата")) {
			long d = (long) Numeric.string2double(txt);
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			c.setTimeInMillis(d);
			DateFormat to = new SimpleDateFormat("yyyyMMdd");
			return to.format(c.getTime());
		}
		return safe(txt);
	}

	boolean in100m(String kod) {
		String sql = "select GeographicheskayaShirota as lat, GeographicheskayaDolgota as lon from kontragenty where kod='" + kod/* this.kontragent*/ + "'";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		double lat = Numeric.string2double(b.child("row").child("lat").value.property.value());
		double lon = Numeric.string2double(b.child("row").child("lon").value.property.value());
		;
		long distanceToClient = GPSInfo.isTPNearClient(lat, lon);
		//mAppInstance.getClientInfo().getLat(), mAppInstance.getClientInfo().getLon());
		if(distanceToClient == GPSInfo.GPS_NOT_AVAILABLE) {
			Auxiliary.warn("GPS данные недоступны.", ActivityCheckList.this);
			return false;
		}
		if(distanceToClient > Settings.getInstance().getMAX_DISTANCE_TO_CLIENT()) {
			//if (distanceToClient > 203000) {
			Auxiliary.warn("Удаление от контрагента " + distanceToClient + " метров.", ActivityCheckList.this);
			return false;
		}
		return true;
	}

	String checkFill() {

		return "";
	}

	String emptyFolderWarning(String parent, String kname) {
		String sql = "select description as description from PokazateliChekLista where _idrref=x'" + parent + "';";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		return "Нет заполненных строк в разделе '"//
				       + b.child("row").child("description").value.property.value()//
				       + "' клиента " + kname//
				;
	}

	void vigruzka() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				             + "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/ChekList\">"//
				             + "\n	  <SOAP-ENV:Body>"//
				             + "\n	    <ns1:Vigruzit>"//
				             + "\n	      <ns1:Chek>"//
				             + "\n	        <ns1:Podr>" + podr + "</ns1:Podr>"//
				             + "\n	        <ns1:Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</ns1:Otvetstvennii>"//
				             + "\n	        <ns1:Data>" + dat + "</ns1:Data>"//
				;
		String sql2 = "select "//
				              + " type as type"//
				              + " ,code as code"//
				              + " ,znachenie as znachenie"//
				              + " ,prim as prim"//
				              + " from PokazateliChekListaItem i"//
				              + " join PokazateliChekLista p on p._idrref=i.pokazatel_id"//
				              + " where (p.isfolder='false' or p.isfolder=x'00') and (common='true' or common=x'01')"//
				              + " and doc_id='" + doc_id + "'" //
				;
		Bough cmn = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql2, null));
		//System.out.println(sql2 + ": " + cmn.dumpXML());
		for(int i = 0; i < cmn.children.size(); i++) {
			Bough row = cmn.children.get(i);
			xml = xml + "\n				<ns1:TablObshii>";
			xml = xml + "\n					<ns1:Pokazatel>" + row.child("code").value.property.value() + "</ns1:Pokazatel>";
			xml = xml + "\n					<ns1:Znachenie>" + for1C(row.child("znachenie").value.property.value(), row.child("type").value.property.value()) + "</ns1:Znachenie>";
			xml = xml + "\n					<ns1:Prim>" + row.child("prim").value.property.value() + "</ns1:Prim>";
			xml = xml + "\n				</ns1:TablObshii>";
		}
		String sql = "select "//
				             + " type as type"//
				             + " ,code as code"//
				             + " ,znachenie as znachenie"//
				             + " ,prim as prim"//
				             + " ,kontragent as kontragent"//
				             + " from PokazateliChekListaItem i"//
				             + " join PokazateliChekLista p on p._idrref=i.pokazatel_id"//
				             + " where (p.isfolder='false' or p.isfolder=x'00') and (common='false' or common=x'00')"//
				             + " and doc_id='" + doc_id + "'" //
				;
		Bough ord = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(sql + ": " + ord.dumpXML());
		for(int i = 0; i < ord.children.size(); i++) {
			Bough row = ord.children.get(i);
			xml = xml + "\n		<ns1:TablPoKlientam>";
			xml = xml + "\n			<ns1:Pokazatel>" + row.child("code").value.property.value() + "</ns1:Pokazatel>";
			xml = xml + "\n			<ns1:Znachenie>" + for1C(row.child("znachenie").value.property.value(), row.child("type").value.property.value()) + "</ns1:Znachenie>";
			xml = xml + "\n			<ns1:Kontragent>" + row.child("kontragent").value.property.value() + "</ns1:Kontragent>";
			xml = xml + "\n			<ns1:Prim>" + row.child("prim").value.property.value() + "</ns1:Prim>";
			xml = xml + "\n		</ns1:TablPoKlientam>";
		}
		xml = xml + "\n	        <ns1:SSS>" + safe(sss) + "</ns1:SSS>"//
				      + "\n	        <ns1:ODR>" + safe(odr) + "</ns1:ODR>"//
				      + "\n	        <ns1:NSV>" + safe(nsv) + "</ns1:NSV>"//
				      + "\n	        <ns1:VSP>" + safe(vsp) + "</ns1:VSP>"//
				      + "\n	      </ns1:Chek>"//
				      + "\n	    </ns1:Vigruzit>"//
				      + "\n	  </SOAP-ENV:Body>"//
				      + "\n	</SOAP-ENV:Envelope>";
		//safe("");
		//System.out.println(xml);
		final RawSOAP s = new RawSOAP();
		s.afterError.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn("Ошибка выгрузки", ActivityCheckList.this);
				requery.start(ActivityCheckList.this);
			}
		})//
				.afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println(s.rawResponse);
				String tx = s.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
				Auxiliary.warn("Выгружено: " + tx, ActivityCheckList.this);
				if(tx.equals("ок")) {
					lock();
				}
				requery.start(ActivityCheckList.this);
				//ActivityCheckList.this.finish();
			}
		})//
				.url.is(Settings.getInstance().getBaseURL() + "ChekList.1cws")//
				.xml.is(xml)//
		;
		s.startLater(ActivityCheckList.this, "Выгрузка чек-листа", Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
	}

	void promptVigruzit() {
		//test();
		//columnItemValue.cells.get(2).labelText.property.value("asd");
		String r = checkFill();
		if(r.length() > 0) {
			Auxiliary.warn(r, this);
		}
		else {
			Auxiliary.pickConfirm(this, "Выгрузка чек-листа", "Выгрузить", new Task() {
				@Override
				public void doTask() {
					//if(ActivityCheckList.this.in100m()){
					vigruzka();
					//}
				}
			});
		}
	}

	void promptRemoveKontragent() {
		if(kontragent.trim().length() > 0) {
			Auxiliary.pickConfirm(this, "Удалить " + kontragentName, "Удалить", new Task() {
				@Override
				public void doTask() {
					String sql = "delete from PokazateliChekListaItem where doc_id=" + doc_id + " and kontragent='" + kontragent + "'";
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
					kontragent = "";
					requery.start(ActivityCheckList.this);
					unlock();
				}
			});
		}
	}

	void promptAddKontragent() {
		//System.out.println("promptAddKontragent " + Cfg.kontragenty().dumpXML());
		String[] items = new String[Cfg.kontragenty().children.size()];
		for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
			items[i] = Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value();
		}
		final Numeric defaultSelection = new Numeric();
		Auxiliary.pickSingleChoice(this, items, defaultSelection, null, new Task() {
			@Override
			public void doTask() {
				String kodkontragent = Cfg.kontragenty().children.get(defaultSelection.value().intValue()).child("kod").value.property.value();
				//if(in100m(kod)){
				if(GPS.getGPSInfo().IsVizitBegin(kodkontragent)) {
					addForKontragent(kodkontragent, podr, doc_id);
					kontragent = kodkontragent;
					unlock();
					requery.start(ActivityCheckList.this);
				}
				else {
					Auxiliary.warn("Откройте визит на этого клиента перед добавлением в чек-лист.", ActivityCheckList.this);
				}
			}
		}, null, null, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuUdalit = menu.add("Удалить");
		menuVigruzit = menu.add("Выгрузить");
		menuAddKontragent = menu.add("Добавить контрагента");
		menuRemoveKontragent = menu.add("Удалить контрагента");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if(item == menuUdalit) {
			promptUdalit();
			return true;
		}
		if(item == menuVigruzit) {
			promptVigruzit();
			return true;
		}
		if(item == menuAddKontragent) {
			promptAddKontragent();
			return true;
		}
		if(item == menuRemoveKontragent) {
			promptRemoveKontragent();
			return true;
		}
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

	void findPodr() {
		String sql = "select data as data,vigruzhen as vigruzhen,podr as podr,sss as sss, odr as odr, nsv as nsv,vsp as vsp from PokazateliChekListaDoc where _id='" + doc_id + "';";
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		podr = items.child("row").child("podr").value.property.value();
		sss = items.child("row").child("sss").value.property.value();
		odr = items.child("row").child("odr").value.property.value();
		nsv = items.child("row").child("nsv").value.property.value();
		vsp = items.child("row").child("vsp").value.property.value();
		vigruzhen = (int) Numeric.string2double(items.child("row").child("vigruzhen").value.property.value());
		long d = (long) Numeric.string2double(items.child("row").child("data").value.property.value());
		Calendar c = Calendar.getInstance();
		//c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(d);
		DateFormat to = new SimpleDateFormat("yyyyMMdd");
		dat = to.format(c.getTime());
	}

	void findKontragent() {
		if(kontragent.trim().length() < 1) {
			String sql = "select kontragent as kontragent from PokazateliChekListaItem where doc_id=" + doc_id + " order by kontragent desc limit 1;";
			Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			kontragent = items.child("row").child("kontragent").value.property.value();
		}
		String sql = "select naimenovanie as naimenovanie from Kontragenty where kod='" + kontragent + "';";
		Bough items = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		kontragentName = items.child("row").child("naimenovanie").value.property.value();
	}

	void createGUI() {
		extras = activityExatras(this);
		//System.out.println(extras.dumpXML());
		doc_id = extras.child("doc_id").value.property.value();
		kontragent = extras.child("kontragent").value.property.value();
		//findPodr();
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		/*layoutless.child(new Decor(this)//
				.labelText.is("территория")//
						.width().is(300)//
						.height().is(Auxiliary.tapSize)//
				);*/
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
						                 columnItemName.title.is("Наименование").width.is(Auxiliary.tapSize * 11)//
						                 , columnItemValue.title.is("Значение").width.is(Auxiliary.tapSize * 5) //
				                 })//
				                 .width().is(layoutless.width().property)//
				                 .height().is(layoutless.height().property)//
		);
		int w = Auxiliary.screenWidth(this);
		propSplit.value(w);
		klientSplit.value(w);
		propGrid = new DataGrid(this)//
				           .noHead.is(true)//
				           .pageSize.is(999)//propGridPageSize)//
				           //.dataOffset.is(propGridOffset)//
				           .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryPropGridData();
						flipPropGrid();
					}
				});
		klientGrid = new DataGrid(this)//
				             .noHead.is(true)//
				             .pageSize.is(999)//propGridPageSize)//
				             //.dataOffset.is(propGridOffset)//
				             .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryKlientGridData();
						flipKlientGrid();
					}
				});
		layoutless.child(new Decor(this)//
				                 .background.is(Auxiliary.colorBackground)//
				                 //.bitmap.is(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.swborder), Auxiliary.tapSize / 2, 2 * Auxiliary.tapSize, true))
				                 .left().is(propSplit).width().is(layoutless.width().property)//
				                 .height().is(layoutless.height().property)//
		);
		layoutless.child(new SplitLeftRight(this)//
				                 .position.is(0)//
				                 .split.is(propSplit)//
				                 .rightSide(propGrid//
						                            .columns(new Column[]{ //
								                            columnProp.title.is("Свойство").width.is(Auxiliary.tapSize * 15) //
						                            })//
						                 //.width().is(layoutless.width().property)//
						                 //.height().is(layoutless.height().property)//
				                 )//
				                 .height().is(layoutless.height().property.read())//
				                 .width().is(layoutless.width().property.read())//
		);
		layoutless.child(new Decor(this)//
				                 .background.is(Auxiliary.colorBackground)//
				                 //.bitmap.is(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.swborder), Auxiliary.tapSize / 2, 2 * Auxiliary.tapSize, true))
				                 .left().is(klientSplit).width().is(layoutless.width().property)//
				                 .height().is(layoutless.height().property)//
		);
		layoutless.child(new SplitLeftRight(this)//
				                 .position.is(1)//
				                 .split.is(klientSplit)//
				                 .rightSide(klientGrid.columns(new Column[]{ //
								                 columnKlientName.title.is("Контрагент").width.is(Auxiliary.tapSize * 11) //
						                 })//
				                 )//
				                 .height().is(layoutless.height().property.read())//
				                 .width().is(layoutless.width().property.read())//
		);
	}

	void replaceItemValue(int nn, String v) {
		//columnItemValue.cells.get(2).labelText.property.value("asd");
		columnItemValue.cells.get(nn).labelText.property.value(v);
		//System.out.println("*************"+nn+" / "+v);
	}

	void replacePropValue(int nn, String v, String prim) {
		columnItemValue.cells.get(nn).labelText.property.value(v);
	}

	void promptBoolean(final boolean noRefresh, final String _id, String description, final int nn) {
		String gsql = "select znachenie as znachenie,prim as prim from PokazateliChekListaItem where _id=" + _id;
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(gsql, null));
		final Toggle t = new Toggle();
		double n = Numeric.string2double(b.child("row").child("znachenie").value.property.value());
		if(n > 0) {
			t.value(true);
		}
		final Note note = new Note().value(b.child("row").child("prim").value.property.value());
		Auxiliary.pick(this, "", new SubLayoutless(this)//
				                         .child(new RedactToggle(this).labelText.is(description).yes.is(t).left().is(Auxiliary.tapSize * 0.5).top().is(0).width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 1))//
				                         .child(new RedactText(this).text.is(note).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 9 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
				                         .width().is(Auxiliary.tapSize * 9)//
				                         .height().is(Auxiliary.tapSize * 2)//
				, "Сохранить", new Task() {
					@Override
					public void doTask() {
						String txtVal = "";
						if(t.value()) {
							txtVal = "1";
						}
						else {
							txtVal = "0";
						}
						String sql = "update PokazateliChekListaItem set znachenie='"//
								             + txtVal//
								             + "', prim='" + note.value()//
								             + "' where _id=" + _id//
								             + ";";
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						unlock();
						if(noRefresh) {
							replaceItemValue(nn, formatZnachenie("" + (
									                                          t.value()
											                                          ? "1"
											                                          : "0"
							), "Булево", note.value()));
						}
						else {
							requery.start(ActivityCheckList.this);
						}
					}
				}, null, null, null, null);
	}

	void promptString(final boolean noRefresh, final String _id, String description, final String znachenie, final int nn) {
		final Note text = new Note().value(znachenie);
		Auxiliary.pickString(this, description, text, "Сохранить", new Task() {
			@Override
			public void doTask() {
				String sql = "update PokazateliChekListaItem set znachenie='"//
						             + text.value()//
						             + "' where _id=" + _id//
						             + ";";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				unlock();
				if(noRefresh) {
					replaceItemValue(nn, text.value());
				}
				else {
					requery.start(ActivityCheckList.this);
				}
			}
		});
	}

	void promptDate(final boolean noRefresh, final String _id, String description, final String znachenie, final int nn) {
		final long mills = (long) Numeric.string2double(znachenie);
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(mills);
		if(Math.abs(mills) < 10000) {
			c.setTimeInMillis(new Date().getTime());
		}
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar newCalendar = Calendar.getInstance();
				newCalendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				newCalendar.setTimeInMillis(mills);
				newCalendar.set(Calendar.YEAR, year);
				newCalendar.set(Calendar.MONTH, monthOfYear);
				newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				//date.property.value((double) newCalendar.getTimeInMillis());
				String sql = "update PokazateliChekListaItem set znachenie='"//
						             + newCalendar.getTimeInMillis()//
						             + "' where _id=" + _id//
						             + ";";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				unlock();
				//requery.start(ActivityCheckList.this);
				if(noRefresh) {
					replaceItemValue(nn, formatZnachenie("" + newCalendar.getTimeInMillis(), "Булево", ""));
				}
				else {
					requery.start(ActivityCheckList.this);
				}
			}
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
	}

	void promptValue(boolean noRefresh, String _id, String description, String znachenie, String prim, String type, int nn) {
		if(type.equals("Булево")) {
			promptBoolean(noRefresh, _id, description, nn);
			return;
		}
		if(type.equals("Дата")) {
			promptDate(noRefresh, _id, description, znachenie, nn);
			return;
		}
		promptString(noRefresh, _id, description, znachenie, nn);
	}

	String formatZnachenie(String znachenie, String type, String prim) {
		if(type.equals("Булево")) {
			if(znachenie.trim().length() < 1) {
				return "";
			}
			double n = Numeric.string2double(znachenie);
			//return znachenie+";"+(n > 0 ? "да" : "нет");
			String r = n > 0
					           ? "да"
					           : "нет";
			if(prim.trim().length() > 0) {
				r = r + ", " + prim;
			}
			return r;
		}
		if(type.equals("Дата")) {
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

	public void flipKlientGrid() {
		klientGrid.clearColumns();
		if(klientGridData != null) {
			for(int i = 0; i < klientGridData.children.size(); i++) {
				Bough row = klientGridData.children.get(i);
				final String name = row.child("naimenovanie").value.property.value();
				final String kod = row.child("kod").value.property.value();
				Task tap = new Task() {
					@Override
					public void doTask() {
						//System.out.println(_id+": "+description);
						//promptValue(_id, description, znachenie, type);
						kontragent = kod;
						requery.start(ActivityCheckList.this);
					}
				};
				columnKlientName.cell(name, tap, kod);
			}
		}
	}

	public void flipPropGrid() {
		/*boolean supervisor = ActivityCheckDocs.isSupervisor(podr);
		propGrid.clearColumns();
		if(propGridData != null) {
			int cntr = 0;
			String parent = "";
			for(int i = 0; i < propGridData.children.size(); i++) {
				Bough row = propGridData.children.get(i);
				final String description = row.child("description").value.property.value();
				final String piece = row.child("piece").value.property.value();
				final String _id = row.child("_id").value.property.value();
				final String znachenie = row.child("znachenie").value.property.value();
				final String prim = row.child("prim").value.property.value();
				final String type = row.child("type").value.property.value();
				final int nn = cntr;
				if(!parent.equals(piece)) {
					columnProp.cell(piece, "");
					parent = piece;
				}
				Task tap = new Task() {
					@Override
					public void doTask() {
						//System.out.println(_id+": "+description);
						//promptValue(_id, description, znachenie, type,columnProp,nn);
						promptValue(false, _id, description, znachenie, prim, type, nn);
					}
				};
				columnProp.cell(description, 0x110000ff, tap, formatZnachenie(znachenie, type, prim));
				cntr++;
			}
			if(!supervisor) {
				columnProp.cell("Сильные стороны сотрудника", new Task() {
					@Override
					public void doTask() {
						final Note text = new Note().value(sss);
						Auxiliary.pickString(ActivityCheckList.this, "Сильные стороны сотрудника", text, "Сохранить", new Task() {
							@Override
							public void doTask() {
								String sql = "update PokazateliChekListaDoc set sss='"//
										             + text.value()//
										             + "' where _id=" + doc_id//
										             + ";";
								ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
								unlock();
								requery.start(ActivityCheckList.this);
							}
						});
					}
				}, sss);
				columnProp.cell("Области для развития", new Task() {
					@Override
					public void doTask() {
						final Note text = new Note().value(odr);
						Auxiliary.pickString(ActivityCheckList.this, "Области для развития", text, "Сохранить", new Task() {
							@Override
							public void doTask() {
								String sql = "update PokazateliChekListaDoc set odr='"//
										             + text.value()//
										             + "' where _id=" + doc_id//
										             + ";";
								ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
								unlock();
								requery.start(ActivityCheckList.this);
							}
						});
					}
				}, odr);
				columnProp.cell("Цели на следующее обучение, задачи на самостоятельную проработку", new Task() {
					@Override
					public void doTask() {
						final Note text = new Note().value(nsv);
						Auxiliary.pickString(ActivityCheckList.this, "Цели на следующее обучение, задачи на самостоятельную проработку", text, "Сохранить", new Task() {
							@Override
							public void doTask() {
								String sql = "update PokazateliChekListaDoc set nsv='"//
										             + text.value()//
										             + "' where _id=" + doc_id//
										             + ";";
								ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
								unlock();
								requery.start(ActivityCheckList.this);
							}
						});
					}
				}, nsv);
				columnProp.cell("Вопросы к смежным подразделениям и компании в целом", new Task() {
					@Override
					public void doTask() {
						final Note text = new Note().value(vsp);
						Auxiliary.pickString(ActivityCheckList.this, "Вопросы к смежным подразделениям и компании в целом", text, "Сохранить", new Task() {
							@Override
							public void doTask() {
								String sql = "update PokazateliChekListaDoc set vsp='"//
										             + text.value()//
										             + "' where _id=" + doc_id//
										             + ";";
								ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
								unlock();
								requery.start(ActivityCheckList.this);
							}
						});
					}
				}, vsp);
			}
		}*/
	}

	public void flipItemsGrid() {
		itemsGrid.clearColumns();
		if(itemsGridData != null) {
			String curCat = "";
			int cntr = 0;
			int color = 0x06000000;
			int colorIdx = 0;
			for(int i = 0; i < itemsGridData.children.size(); i++) {
				Bough row = itemsGridData.children.get(i);
				final String description = row.child("description").value.property.value();
				final String _id = row.child("_id").value.property.value();
				final String znachenie = row.child("znachenie").value.property.value();
				final String type = row.child("type").value.property.value();
				final String prim = row.child("prim").value.property.value();
				String cat = row.child("category").value.property.value();
				if(!cat.equals(curCat)) {
					/*if(color==0xffeeeeee){
						color=0xffffffff;
					}else{
						color=0xffeeeeee;
					}*/

					//columnItemName.cell(cat);
					//columnItemValue.cell("");
					curCat = cat;
					//cntr++;
					//colorIdx++;

				}
				if(colorIdx == 1) {
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
				}
				final int nn = cntr;
				Task tap = new Task() {
					@Override
					public void doTask() {
						//System.out.println(ActivityCheckList.this.kontragent + ": " + ActivityCheckList.this.kontragentName);
						//if(in100m()){
						promptValue(true, _id, description, znachenie, prim, type, nn);
						//promptValue(true, _id, description, columnItemValue.cells.get(nn).labelText.property.value(), type, nn);
						//}
					}
				};//mOrder
				columnItemName.cell(//row.child("groupsOrder").value.property.value()+". "+row.child("paramatersOrder").value.property.value()+". "+
						//description,color, tap,"");
						curCat+": "+description, color, tap);
				columnItemValue.cell(formatZnachenie(znachenie, type, prim), color, tap);
				cntr++;
			}
		}
	}

	public void requeryKlientGridData() {
		String sql = "select "//
				             + " k.kod as kod,k.naimenovanie as naimenovanie"//
				             + " from PokazateliChekListaItem p"//
				             + " join kontragenty k on k.kod=p.kontragent"//
				             + " where doc_id=" + doc_id//
				             + " group by k.kod,k.naimenovanie"//
				             + " order by k.naimenovanie"//
				;
		klientGridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(klientGridData.dumpXML());
	}

	public void requeryPropGridData() {
		String sql = "select "//
				             + " i._id as _id"//
				             + ",p.description as description"//
				             + ",i.znachenie as znachenie "//
				             + ",i.prim as prim "//
				             + ",parent.description as piece"//
				             + ",p.type as type"//
				             + " from PokazateliChekListaItem i"//
				             + " join PokazateliChekLista p on p._idrref=i.pokazatel_id"//
				             + " join PokazateliChekLista parent on parent._idrref=p.parent"//
				             + " where (p.common='true' or p.common=x'01') and i.doc_id=" + doc_id//
				             + " order by parent.sortorder,p.sortorder"//
				;
		propGridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(propGridData.dumpXML());
	}

	public void requeryItemGridData() {
		String sql = "select "//
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
				;
		itemsGridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(sql);
		//System.out.println(itemsGridData.dumpXML());
	}

	public void requeryData() {
		findPodr();
		findKontragent();
		requeryPropGridData();
		requeryItemGridData();
		requeryKlientGridData();
	}

	public void refreshGUI() {
		flipPropGrid();
		flipItemsGrid();
		flipKlientGrid();
		itemsGrid.refresh();
		propGrid.refresh();
		klientGrid.refresh();
		//System.out.println("refreshGUI");
		this.setTitle("Чек-лист " + podr + ", " + kontragent + ": " + kontragentName + ", " + dat);
	}
}
