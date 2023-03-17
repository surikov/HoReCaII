package sweetlife.android10.supervisor;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityZayavkaVozmehenie extends Activity {
	Layoutless layoutless;
	MenuItem menuDobavit;
	DataGrid dataGrid;
	ColumnText columnUploaded = new ColumnText();
	ColumnDate columnDate = new ColumnDate().format.is("dd.MM.yyyy");
	ColumnText columnKontragent = new ColumnText();
	ColumnText columnNumber = new ColumnText();
	Bough gridData;
	public int gridPageSize = 30;
	public Numeric gridOffset = new Numeric();
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGUI();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuDobavit = menu.add("Добавить");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuDobavit) {
			promptNew();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		super.onResume();
		requery.start(this);
	}
	void promptNew() {
		final Note nomer = new Note();
		final Numeric date = new Numeric();
		final Note kament = new Note();
		Auxiliary.pick(this, "Заявка на возмещение", new SubLayoutless(this)//
				.child(new Decor(this).labelText.is("Дата").labelAlignRightCenter().top().is(Auxiliary.tapSize * 0).width().is(100).height().is(Auxiliary.tapSize))//
				.child(new RedactDate(this).date.is(date).left().is(110).top().is(Auxiliary.tapSize * 0).width().is(200).height().is(Auxiliary.tapSize * 0.9))//
				.child(new Decor(this).labelText.is("Номер").labelAlignRightCenter().top().is(Auxiliary.tapSize * 1).width().is(100).height().is(Auxiliary.tapSize))//
				.child(new RedactText(this).text.is(nomer).left().is(110).top().is(Auxiliary.tapSize * 1).width().is(200).height().is(Auxiliary.tapSize * 0.9))//
				.child(new Decor(this).labelText.is("Комментарий").labelAlignRightCenter().top().is(Auxiliary.tapSize * 2).width().is(100).height().is(Auxiliary.tapSize))//
				.child(new RedactText(this).text.is(kament).left().is(110).top().is(Auxiliary.tapSize * 2).width().is(400).height().is(Auxiliary.tapSize * 0.9))//
				.width().is(Auxiliary.tapSize * 30)//
				.height().is(Auxiliary.tapSize * 4)//
				, "Добавить", new Task() {
					@Override
					public void doTask() {
						long d = date.value().longValue();
						String sql = "insert into ZayavkaVozmehenie (nomer,data,kod,uploaded) values ('"//
								+ nomer.value() //
								+ "','"//
								+ d //
								+ "','"//
								+ kament.value() //
								+ "',0);";
						//System.out.println(sql);
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						requery.start(ActivityZayavkaVozmehenie.this);
					}
				}, null, null, null, null);
	}
	void createGUI() {
		this.setTitle("Заявки на возмещение");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		dataGrid = new DataGrid(this).center.is(true)//
		.pageSize.is(gridPageSize)//
		.dataOffset.is(gridOffset)//
		.beforeFlip.is(new Task() {
			@Override
			public void doTask() {
				//requeryGridData();
				//flipGrid();
			}
		});
		layoutless.child(dataGrid//
				.columns(new Column[] { //
						columnUploaded.title.is("Выгружен").width.is(Auxiliary.tapSize * 2)//
								, columnDate.title.is("Дата").width.is(Auxiliary.tapSize * 2)//
								, columnNumber.title.is("Номер").width.is(Auxiliary.tapSize * 3) //
								, columnKontragent.title.is("Комментарий").width.is(Auxiliary.tapSize * 7) //
						})//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
				);
	}
	public void requeryGridData() {
		String sql = "select"//
				+ " _id as _id"//
				+ " , data as data"//
				+ " ,kod as kod"//
				+ " ,nomer as nomer"//
				+ " ,uploaded  as uploaded"//
				+ " from ZayavkaVozmehenie "//
				+ " order by uploaded,data,nomer,kod desc"//
				+ " limit " + (gridPageSize * 3) //
				+ " offset " + gridOffset.value()//
				+ ";";
		//System.out.println(sql);
		gridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(gridData.dumpXML());
	}
	void upload(final String _id, String data, String nomer) {
		String d = Cfg.formatMills(Numeric.string2double(data), "yyyyMMdd");
		String hrc = Cfg.whoCheckListOwner();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//
				+ "\n	<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://ws.swl/ZayavkaVozmehenie\">"//
				+ "\n	  <SOAP-ENV:Body>"//
				+ "\n	    <ns1:Vigruska>"//
				+ "\n	      <ns1:Nomer>" + nomer + "</ns1:Nomer>"//14-0157693
				+ "\n	      <ns1:Data>" + d + "</ns1:Data>"//20160427
				+ "\n	      <ns1:Otv>" + hrc + "</ns1:Otv>"//hrc222
				+ "\n	    </ns1:Vigruska>"//
				+ "\n	  </SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>";
		//System.out.println(xml);
		final RawSOAP r = new RawSOAP();
		r.xml.is(xml)//
		.url.is(Settings.getInstance().getBaseURL() + "ZayavkaVozmehenie.1cws")//
		.afterError.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn("Ошибка: " + r.exception.property.value(), ActivityZayavkaVozmehenie.this);
			}
		})//
		.afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				if (r.statusCode.property.value() >= 100 //
						&& r.statusCode.property.value() <= 300//
						&& r.exception.property.value() == null//
				) {
					String response = r.data.child("soap:Body").child("m:VigruskaResponse").child("m:return").value.property.value();
					Auxiliary.warn("Результат: " + response, ActivityZayavkaVozmehenie.this);
					if ("ок".equals(response.trim())) {
						String sql = "update ZayavkaVozmehenie set uploaded=1 where _id=" + _id;
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						requery.start(ActivityZayavkaVozmehenie.this);
					}
				}
				else {
					Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityZayavkaVozmehenie.this);
				}
				//System.out.println(r.data.dumpXML());
			}
		})//
				.startLater(this, "Отправка",Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
	}
	void promptAction(final String _id, final String data, final String nomer, final String uploaded) {
		Auxiliary.pick3Choice(this, "Заявка на возмещение", "Номер " + nomer, "Удалить", new Task() {
			@Override
			public void doTask() {
				Auxiliary.pickConfirm(ActivityZayavkaVozmehenie.this, "Удалить заявку", "Удалить", new Task() {
					@Override
					public void doTask() {
						String sql = "delete from ZayavkaVozmehenie where _id=" + _id;
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						requery.start(ActivityZayavkaVozmehenie.this);
					}
				});
			}
		}, "Выгрузить", new Task() {
			@Override
			public void doTask() {
				if (uploaded.equals("0")) {
					upload(_id, data, nomer);
				}
				else {
					Auxiliary.warn("Заявка уже выгружена", ActivityZayavkaVozmehenie.this);
				}
			}
		}, null, null);
	}
	public void flipGrid() {
		dataGrid.clearColumns();
		if (gridData != null) {
			for (int i = 0; i < gridData.children.size(); i++) {
				Bough row = gridData.children.get(i);
				final String _id = row.child("_id").value.property.value();
				final String data = row.child("data").value.property.value();
				final String nomer = row.child("nomer").value.property.value();
				final String uploaded = row.child("uploaded").value.property.value();
				Task task = new Task() {
					@Override
					public void doTask() {
						promptAction(_id, data, nomer, uploaded);
					}
				};
				String v = row.child("uploaded").value.property.value();
				columnUploaded.cell(v.equals("1") ? "да" : "нет", task);
				columnDate.cell((long) Numeric.string2double(data), task);
				columnNumber.cell(nomer, task);
				columnKontragent.cell(row.child("kod").value.property.value().trim(), task);
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
