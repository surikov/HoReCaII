package sweetlife.android10.supervisor;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.task.Task;
import android.app.Activity;
import android.os.Bundle;

public class ActivityPlanObucheniaAll extends Activity {
	DataGrid dataGrid;
	ColumnText columnData;
	ColumnText columnDenNedeli;
	ColumnText columnPodrazdelenie;
	ColumnText columnFIO;
	ColumnText columnKommentariy;
	Numeric searchDate = new Numeric();
	Layoutless layoutless;
	int gridPageSize = 90;
	Numeric gridOffset = new Numeric();
	Bough data;
	RawSOAP rawSOAP;
	//MenuItem menuCreate;
	String error = null;
	Expect requery = new Expect().status.is("Обновление данных").task.is(new Task() {
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
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuCreate = menu.add("Добавить новый план");
		return true;
	}*/
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuCreate) {
			menuCreate();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	/*void menuCreate() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityPlanObucheniaOne.class);
		//intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		this.setTitle("План полевых обучений");
		setContentView(layoutless);
		createGUI();
		//ActivityWebServicesReports.initDB();
	}
	@Override
	protected void onResume() {
		super.onResume();
		//requery.start(this);
	}
	void createGUI() {
		dataGrid = new DataGrid(this);
		columnData = new ColumnText();
		columnDenNedeli = new ColumnText();
		columnPodrazdelenie = new ColumnText();
		columnFIO = new ColumnText();
		columnKommentariy = new ColumnText();
		searchDate.value((double) new Date().getTime()).afterChange(new Task() {
			@Override
			public void doTask() {
				requery.start(ActivityPlanObucheniaAll.this);
			}
		}, true);
		layoutless//
				.child(new RedactDate(this)//
				.date.is(searchDate)//
				.format.is("dd.MM.yyyy").top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(layoutless.width().property.minus(Auxiliary.tapSize * 3))//
						.height().is(Auxiliary.tapSize)//
				)//
				.child(new Knob(this)//
				.labelText.is("Сохранить")//
				.afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptSendData();
					}
				}).top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.left().is(layoutless.width().property.minus(Auxiliary.tapSize * 3))//
						.width().is(Auxiliary.tapSize * 3)//
						.height().is(Auxiliary.tapSize)//
				)//
				.child(dataGrid//
				.center.is(true)//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
						/*.beforeFlip.is(new Task() {
							@Override
							public void doTask() {
								requeryGridData();
								flipGrid();
							}
						})*/
						.columns(new Column[] { //
								//
										columnData.title.is("Дата").width.is(2 * Auxiliary.tapSize)//
										, columnDenNedeli.title.is("День").width.is(1 * Auxiliary.tapSize) //
										, columnPodrazdelenie.title.is("Подразделение").width.is(4 * Auxiliary.tapSize) //
										, columnFIO.title.is("Ф.И.О.").width.is(6 * Auxiliary.tapSize) //
										, columnKommentariy.title.is("Комментарий").width.is(8 * Auxiliary.tapSize) //.title.is("Ф.И.О.").width.is(2 * Auxiliary.tapSize) //
								})//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
						//.top().is(Auxiliary.tapSize)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				);
		requery.start(ActivityPlanObucheniaAll.this);
	}
	void promptSendData() {
		Auxiliary.pickConfirm(this, "Сохранить план обучений", "Сохранить", new Task() {
			@Override
			public void doTask() {
				sendData();
			}
		});
	}
	void sendData() {
		new Expect().status.is("Выгрузка данных").task.is(new Task() {
			@Override
			public void doTask() {
				postData();
			}
		})//
		.afterDone.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("done");
				if (rawSOAP.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + rawSOAP.exception.property.value().getMessage(),ActivityPlanObucheniaAll.this);
					rawSOAP.exception.property.value().printStackTrace();
				}
				else {
					if (rawSOAP.statusCode.property.value() >= 100 && rawSOAP.statusCode.property.value() <= 300) {
						String res=rawSOAP.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
						
						if(res.equals("ок"))
						{
							Auxiliary.warn("План сохранён",ActivityPlanObucheniaAll.this);
						ActivityPlanObucheniaAll.this.finish();
						}else{
							Auxiliary.warn("Результат: " + rawSOAP.data.dumpXML(),ActivityPlanObucheniaAll.this);
						}
					}
					else {
						Auxiliary.warn("Статус: " + rawSOAP.statusCode.property.value() + ": " + rawSOAP.statusDescription.property.value(),ActivityPlanObucheniaAll.this);
					}
				}
			}
		})//
				.start(this);
	}
	void postData() {
		//System.out.println("sendData");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(searchDate.value().longValue());
		String year = "" + c.get(Calendar.YEAR);
		int nmonth = 1 + c.get(Calendar.MONTH);
		String month = "" + nmonth;
		if (nmonth < 10) {
			month = "0" + nmonth;
		}
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/PlanObuchenii\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Vigruzit>"//
				+ "\n	      <ns1:PlanObuchenii>"//
				+ "\n	        <ns1:Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim() + "</ns1:Otvetstvennii>"//
				+ "\n	        <ns1:Period>" + year + month + "01" + "</ns1:Period>"//
		;
		Vector<Bough> rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").children("m:Tabl");
		for (int i = 0; i < rows.size(); i++) {
			String k = rows.get(i).child("m:Komment").value.property.value();
			k = k.replace('>', '`');
			k = k.replace('<', '`');
			k = k.replace('&', '`');
			xml = xml + "\n	        <ns1:Tabl>"//
					+ "\n	          <ns1:Data>" + rows.get(i).child("m:Data").value.property.value() + "</ns1:Data>"//
					+ "\n	          <ns1:Podr>" + rows.get(i).child("m:Podr").value.property.value() + "</ns1:Podr>"//
					+ "\n	          <ns1:Komment>" + k + "</ns1:Komment>"//
					+ "\n	        </ns1:Tabl>"//
			;
		}
		xml = xml + "\n	      </ns1:PlanObuchenii>"//
				+ "\n	    </ns1:Vigruzit>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>"//
		;
		//System.out.println(xml);
		rawSOAP = new RawSOAP();
		rawSOAP//
		.url.is(Settings.getInstance().getBaseURL() + "PlanObuchenii.1cws")//
		.xml.is(xml)//
		;
		Report_Base.startPing();
				rawSOAP.startNow(Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
		//System.out.println("status " + rawSOAP.statusCode.property.value());
		
	}
	public void requeryData() {
		rawSOAP = new RawSOAP();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(searchDate.value().longValue());
		String year = "" + c.get(Calendar.YEAR);
		int nmonth = 1 + c.get(Calendar.MONTH);
		String month = "" + nmonth;
		if (nmonth < 10) {
			month = "0" + nmonth;
		}
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/PlanObuchenii\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Perechitat>"//
				+ "\n	      <ns1:Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim() + "</ns1:Otvetstvennii>"//
				+ "\n	      <ns1:Period>" + year + month + "01" + "</ns1:Period>"//
				+ "\n	    </ns1:Perechitat>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>";
		//System.out.println(xml);
		rawSOAP//
		.url.is(Settings.getInstance().getBaseURL() + "PlanObuchenii.1cws")//
		.xml.is(xml)//
		;
		Report_Base.startPing();
				
				rawSOAP.startNow(Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
		data = null;
		//System.out.println(rawSOAP.data.dumpXML());
		if (rawSOAP.exception.property.value() != null) {
			error = rawSOAP.exception.property.value().getMessage();
		}
		else {
			if (rawSOAP.statusCode.property.value() >= 100 && rawSOAP.statusCode.property.value() <= 300) {
				data = rawSOAP.data;
				Vector<Bough> rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").children("m:Tabl");
				for (int i = 0; i < rows.size(); i++) {
					String mData = rows.get(i).child("m:Data").value.property.value();
					String date = mData.substring(6, 8) + "." + mData.substring(4, 6) + "." + mData.substring(0, 4);
					rows.get(i).child("date").value.property.value(date);
					Calendar ca = Calendar.getInstance();
					ca.set(Calendar.YEAR, Integer.parseInt(mData.substring(0, 4)));
					ca.set(Calendar.MONTH, Integer.parseInt(mData.substring(4, 6)) - 1);
					ca.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mData.substring(6, 8)));
					String denNed = ca.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
					rows.get(i).child("denNed").value.property.value(denNed);
					String podr = rows.get(i).child("m:Podr").value.property.value().trim();
					String sql = "select"//
							+ " Podrazdeleniya.naimenovanie as podr"//
							+ " ,PhizicheskieLica.naimenovanie as fio"//
							+ " from Podrazdeleniya"// 
							+ " left join Polzovateli on Polzovateli.Podrazdelenie=Podrazdeleniya._idrref"//
							+ " left join PhizLicaPolzovatelya on PhizLicaPolzovatelya.polzovatel=Polzovateli._idrref"//
							+ " left join PhizicheskieLica on PhizicheskieLica._idrref=PhizLicaPolzovatelya.phizlico"//
							+ " where Podrazdeleniya.kod=\"" + podr + "\""//
							+ " order by PhizLicaPolzovatelya.period desc;"//
					;
					Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					String podrName = b.child("row").child("podr").value.property.value();
					String fioName = b.child("row").child("fio").value.property.value();
					rows.get(i).child("podrName").value.property.value(podrName);
					rows.get(i).child("fioName").value.property.value(fioName);
				}
			}
			else {
				error = rawSOAP.statusCode.property.value() + ": " + rawSOAP.statusDescription.property.value();
			}
		}
	}
	public void refreshGUI() {
		dataGrid.clearColumns();
		if (data == null) {
			Auxiliary.warn("Ошибка: " + error, ActivityPlanObucheniaAll.this);
		}
		else {
			//System.out.println(data.dumpXML());
			Vector<Bough> rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").children("m:Tabl");
			for (int i = 0; i < rows.size(); i++) {
				//System.out.println(rows.get(i).dumpXML());
				final String dt = rows.get(i).child("date").value.property.value();
				String dne = rows.get(i).child("denNed").value.property.value();
				final String podr = rows.get(i).child("m:Podr").value.property.value();
				final Note kmt = rows.get(i).child("m:Komment").value.property;
				Task tap = new Task() {
					@Override
					public void doTask() {
						/*System.out.println("tap "+dt);
						Intent intent = new Intent();
						intent.setClass(ActivityPlanObucheniaAll.this, ActivityPlanObucheniaOne.class);
						intent.putExtra("data", "" + dt);
						intent.putExtra("den", "" + dne);
						intent.putExtra("podr", "" + podr);
						ActivityPlanObucheniaAll.this.startActivityForResult(intent, 0);*/
						promptPodrasd(dt, podr);
					}
				};
				Task tapKom = new Task() {
					@Override
					public void doTask() {
						/*System.out.println("tap "+dt);
						Intent intent = new Intent();
						intent.setClass(ActivityPlanObucheniaAll.this, ActivityPlanObucheniaOne.class);
						intent.putExtra("data", "" + dt);
						intent.putExtra("den", "" + dne);
						intent.putExtra("podr", "" + podr);
						ActivityPlanObucheniaAll.this.startActivityForResult(intent, 0);*/
						promptKomment(dt, kmt);
					}
				};
				columnData.cell(dt, tap);
				columnDenNedeli.cell(dne, tap);
				columnPodrazdelenie.cell(rows.get(i).child("podrName").value.property.value(), tap);
				columnFIO.cell(rows.get(i).child("fioName").value.property.value(), tap);
				columnKommentariy.cell(kmt.value(), tapKom);
			}
		}
		dataGrid.refresh();
	}
	void promptPodrasd(final String date, String kod) {
		//System.out.println("promptPodrasd " + date + ": " + kod);
		final String[] ters = new String[Cfg.territory().children.size() + 1];
		ters[0] = "[нет]";
		final Numeric nn = new Numeric().value(0);
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
					+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			;
			ters[i + 1] = s;
			if (Cfg.territory().children.get(i).child("kod").value.property.value().equals(kod)) {
				nn.value(i + 1);
			}
		}
		Auxiliary.pickSingleChoice(ActivityPlanObucheniaAll.this, ters, nn, null, new Task() {
			@Override
			public void doTask() {
				String newKod = "";
				if (nn.value() > 0) {
					newKod = Cfg.territory().children.get(nn.value().intValue() - 1).child("kod").value.property.value();
				}
				String sql = "select"//
						+ " Podrazdeleniya.naimenovanie as podr"//
						+ " ,PhizicheskieLica.naimenovanie as fio"//
						+ " from Podrazdeleniya"// 
						+ " left join Polzovateli on Polzovateli.Podrazdelenie=Podrazdeleniya._idrref"//
						+ " left join PhizLicaPolzovatelya on PhizLicaPolzovatelya.polzovatel=Polzovateli._idrref"//
						+ " left join PhizicheskieLica on PhizicheskieLica._idrref=PhizLicaPolzovatelya.phizlico"//
						+ " where Podrazdeleniya.kod=\"" + newKod + "\""//
						+ " order by PhizLicaPolzovatelya.period desc;"//
				;
				//System.out.println(sql);
				Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				//System.out.println(b.dumpXML());
				String podrName = b.child("row").child("podr").value.property.value();
				String fioName = b.child("row").child("fio").value.property.value();
				Vector<Bough> rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").children("m:Tabl");
				for (int i = 0; i < rows.size(); i++) {
					if (rows.get(i).child("date").value.property.value().equals(date)) {
						rows.get(i).child("podrName").value.property.value(podrName);
						rows.get(i).child("fioName").value.property.value(fioName);
						rows.get(i).child("m:Podr").value.property.value(newKod);
					}
				}
				refreshGUI();
			}
		}, null, null, null, null);
	}
	void promptKomment(String date, Note km) {
		//System.out.println("promptKomment " + data + ": " + km);
		//Note t=new Note().value(km);
		Auxiliary.pickString(this, "Комментарий", km, "ОК", new Task() {
			@Override
			public void doTask() {
				refreshGUI();
			}
		});
	}
	/*
	void requeryGridData() {
		System.out.println("requeryGridData");
		String sql = "select * from PlanPolevihObucheniy;"//
				+ " order by m.nomer  limit " + (gridPageSize * 3)// 
				+ " offset " + gridOffset.value().intValue();
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/PlanObuchenii\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Perechitat>"//
				+ "\n	      <ns1:Otvetstvennii>"+"region2"+"</ns1:Otvetstvennii>"//
				+ "\n	      <ns1:Period>"+"20150601"+"</ns1:Period>"//
				+ "\n	    </ns1:Perechitat>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>"//
		;
	}
	void flipGrid() {
		System.out.println("flipGrid");
		dataGrid.clearColumns();
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			columnNomer.cell(row.child("utverjdenPom").value.property.value().equals("1") ? "да" : "нет", tap);
			columnPeriod.cell((long) Numeric.string2double(row.child("data").value.property.value()), tap);
			columnOvetstvenniy.cell(row.child("nomer").value.property.value(), tap);
			columnPoyasnenia.cell(row.child("periodDeystvia").value.property.value(), tap);
		}
	}
	void tap(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityPlanObucheniaOne.class);
		intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}*/
}
