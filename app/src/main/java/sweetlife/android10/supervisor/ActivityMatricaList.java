package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;

import java.util.*;

import android.database.sqlite.*;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

public class ActivityMatricaList extends Activity {
	MenuItem menuOtchety;
	MenuItem menuCreate;
	
	Layoutless layoutless;
	DataGrid dataGrid;
	ColumnText columnUtverjden;
	ColumnText columnUtverjdenPom;
	ColumnDate columnData;
	ColumnText columNomer;
	ColumnDate columnPeriodDeystvia;
	ColumnText columnPodrazdelenie;
	//ColumnNumeric columnNacenka;
	//ColumnText columnPolzovatel;
	ColumnDate columnDataZagruzkiMarshruta;
	ColumnText columnOtvetstvenniy;
	int gridPageSize = 30;
	Numeric gridOffset = new Numeric();
	Bough data;

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOtchety = menu.add("Отчёты");
		menuCreate = menu.add("Добавить матрицу ТП");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*if (item == menuTest) {
			menuTest();
			return true;
		}*/
		if (item == menuCreate) {
			menuCreate();
			return true;
		}
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	void webServiceZapolnit() {
		final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Zapolnit xmlns=\"http://ws.swl/Matrica\"><Podr>00023</Podr><Period>20130501</Period></Zapolnit></soap:Body></soap:Envelope>";
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is("http://78.40.186.186/WebMatrica.1cws")//
				.xml.is(xml);
				r.startNow();
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (r.exception.property.value() != null) {
					Auxiliary.inform("Ошибка: " + r.exception.property.value().getMessage(), ActivityMatricaTP.this);
				}
				else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						System.out.println(r.data.dumpXML());
						Auxiliary.inform("Done", ActivityMatricaTP.this);
					}
					else {
						Auxiliary.inform("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityMatricaTP.this);
					}
				}
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).start(this);
	}
	void menuTest() {
		webServiceZapolnit();
	}*/
	void menuCreate() {
		String defNomer = "...";
		Date now = new Date();
		Date period = new Date();
		period.setMonth(1 + period.getMonth());
		//ActivityMatricaEdit.feelTerritories();
		String kod = Cfg.territory().children.get(0).child("kod").value.property.value();
		String sql = "insert into MatricaX ("//
				+ "\n utverjden, utverjdenPom, data, nomer, periodDeystvia, kod, dataZagruzkiMarshruta, otvetstvenniy"//
				+ "\n ) values ("//
				+ "\n 0,0," + now.getTime() + ",'" + defNomer + "'," + period.getTime() + ",'" + kod + "',0,'" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "'"//
				+ "\n )";
		SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
		//System.out.println(sql);
		long id = statement.executeInsert();
		fillGUI();
		tap("" + id);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		this.setTitle("Матрицы ТП");
		setContentView(layoutless);
		createGUI();
		//ActivityWebServicesReports.initDB();
	}
	@Override
	protected void onResume() {
		super.onResume();
		fillGUI();
	}
	void createGUI() {
		dataGrid = new DataGrid(this);
		columnUtverjden = new ColumnText();
		columnUtverjdenPom = new ColumnText();
		columnData = new ColumnDate();
		columNomer = new ColumnText();
		columnPeriodDeystvia = new ColumnDate();
		columnPodrazdelenie = new ColumnText();
		//columnNacenka = new ColumnNumeric();
		//columnPolzovatel = new ColumnText();
		columnDataZagruzkiMarshruta = new ColumnDate();
		columnOtvetstvenniy = new ColumnText();
		layoutless.child(dataGrid//
				.columns(new Column[] { //
				//
						columnUtverjden.title.is("Утверждён").width.is(1.5 * Auxiliary.tapSize)//
						, columnUtverjdenPom.title.is("Утверждён POM").width.is(1.5 * Auxiliary.tapSize) //
						, columnData.format.is("dd.MM.yyyy hh:mm").title.is("Дата").width.is(2.7 * Auxiliary.tapSize) //
						, columNomer.title.is("Номер").width.is(2 * Auxiliary.tapSize) //
						, columnPeriodDeystvia.format.is("MM.yyyy").title.is("Период действия").width.is(2 * Auxiliary.tapSize) //
						, columnPodrazdelenie.title.is("Подразделение").width.is(6.5 * Auxiliary.tapSize) //
						//, columnPolzovatel.title.is("Пользователь").width.is(2 * Auxiliary.tapSize) //
						, columnDataZagruzkiMarshruta.format.is("dd.MM.yyyy").title.is("Дата загрузки маршрута").width.is(3 * Auxiliary.tapSize) //
						, columnOtvetstvenniy.title.is("Ответственный").width.is(2.2 * Auxiliary.tapSize) //
				})//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				);
	}
	void fillGUI() {
//System.out.println("fillGUI start");
		dataGrid.clearColumns();
		String sql = "select"//
				+ " m._id,utverjden, utverjdenPom, data, nomer, periodDeystvia, m.kod, dataZagruzkiMarshruta, otvetstvenniy"//
				+ " ,p.Naimenovanie as podrname, dvuhnedel as dvuhnedel"// 
				+ " from MatricaX m"//
				+ " join Podrazdeleniya p on p.kod=m.kod"// 
				+ " order by m.periodDeystvia,m.nomer  limit " + (gridPageSize * 3)// 
				+ " offset " + gridOffset.value().intValue();
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			columnUtverjden.cell(row.child("utverjden").value.property.value().equals("1") ? "да" : "нет", tap);
			columnUtverjdenPom.cell(row.child("utverjdenPom").value.property.value().equals("1") ? "да" : "нет", tap);
			columnData.cell((long) Numeric.string2double(row.child("data").value.property.value()), tap);
			columNomer.cell(row.child("nomer").value.property.value(), tap);
			//System.out.println(row.child("periodDeystvia").value.property.value());
			columnPeriodDeystvia.cell((long) Numeric.string2double(row.child("periodDeystvia").value.property.value()), tap);
			double dvuhnedel=Numeric.string2double(row.child("dvuhnedel").value.property.value());
			String dvuhnedelName=dvuhnedel>0?"двухнедельный":"однонедельный";
			columnPodrazdelenie.cell(row.child("podrname").value.property.value()+" - "+dvuhnedelName, tap);
			//columnPolzovatel.cell(row.child("polzovatel").value.property.value(), tap);
			columnDataZagruzkiMarshruta.cell((long) Numeric.string2double(row.child("dataZagruzkiMarshruta").value.property.value()), tap);
			columnOtvetstvenniy.cell(row.child("otvetstvenniy").value.property.value(), tap);
			//columnNacenka.cell(Numeric.string2double(row.child("nacenka").value.property.value()), tap);
		}
		dataGrid.refresh();
		//System.out.println("fillGUI done");
	}
	void tap(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMatricaEdit.class);
		intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}
}
