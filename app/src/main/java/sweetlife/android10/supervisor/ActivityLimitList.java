package sweetlife.android10.supervisor;

import android.app.Activity;
import android.os.*;
import android.view.*;
import android.content.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

public class ActivityLimitList extends Activity {

	//MenuItem menuZapolnit;
	//MenuItem menuPerechitat;
	//MenuItem menuVigruzit;
	Layoutless layoutless;
	MenuItem menuNew;
	MenuItem menuClear;
	//Numeric territorySelection = new Numeric();
	ColumnText lidate;
	ColumnDescription kod;
	DataGrid grid;
	int pageSize = 30;
	Numeric gridOffset = new Numeric();
	Bough data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Установка лимитов: " + ApplicationHoreca.getInstance().getClientInfo().getName());
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		grid = new DataGrid(this);
		lidate = new ColumnText();
		kod = new ColumnDescription();
		layoutless.child(grid//
				                 .columns(new Column[]{ //
						                 //
						                 lidate.title.is("Код").width.is(3 * Auxiliary.tapSize)//
						                 , kod.title.is("Заявка").width.is(19 * Auxiliary.tapSize) //
				                 })//
				                 .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requery();
					}
				})//
				                 .pageSize.is(pageSize)//
				                 .dataOffset.is(gridOffset)//
				                 .width().is(layoutless.width().property)//
				                 .height().is(layoutless.height().property)//
		);
		requery();
		grid.refresh();
	}

	@Override
	protected void onResume() {
		super.onResume();
		grid.clearColumns();
		requery();
		grid.refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menuZapolnit = menu.add("Zapolnit");
		//menuPerechitat = menu.add("Perechitat");
		//menuVigruzit = menu.add("Vigruzit");
		menuNew = menu.add("Новая заявка");
		menuClear = menu.add("Удалить всё");
		return true;
	}

	void tap(String _id) {
		//System.out.println(_id);
		openDocument(_id);
	}

	void requery() {
		/*String sql = "select LimitiList._id as _id, Data, Podrazdelenie || ': ' || Podrazdeleniya.naimenovanie as name from LimitiList "//
				+" left join Podrazdeleniya on Podrazdeleniya.kod=LimitiList.Podrazdelenie"
				+ " order by Data desc, Podrazdelenie"//
				+ " limit " + (pageSize * 3) + " offset " + gridOffset.value().intValue();*/
		String sql = "select LimitiList._id as _id, DataIzm as limitval, Data as delay,Kommentarii as rem,Podrazdelenie as kod"
				             +" from LimitiList "//
							 +" where LimitiList.Otvetstvenniy="+ApplicationHoreca.getInstance().getClientInfo().getKod() //
				             + " order by Podrazdelenie desc"//
				             + " limit " + (pageSize * 3) + " offset " + gridOffset.value().intValue();
		System.out.println(sql);
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for(int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			lidate.cell(row.child("kod").value.property.value(), tap);
			kod.cell("лимит: "+row.child("limitval").value.property.value()//
					+", отсрочка: "+row.child("delay").value.property.value()//

					, tap
			,row.child("rem").value.property.value()
			);
		}
		//System.out.println(data.dumpXML());
	}

	/*void promptNewDocument() {
		//Bough row = ActivityMatricaEdit.territory().children.get(territorySelection.value().intValue());
		String[] items = new String[ActivityMatricaEdit.territory().children.size()];
		for (int i = 0; i < ActivityMatricaEdit.territory().children.size(); i++) {
			Bough row = ActivityMatricaEdit.territory().children.get(i);
			String s = row.child("territory").value.property.value()// 
					+ " (" + row.child("hrc").value.property.value().trim()// 
					+ " / " + row.child("kod").value.property.value().trim()//
					+ ")";
			//terrRedactSingleChoice.item(s);
			items[i] = s;
		}
		Auxiliary.pickSingleChoice(this, items, territorySelection, null, new Task() {
			@Override
			public void doTask() {
				Bough row = ActivityMatricaEdit.territory().children.get(territorySelection.value().intValue());
				String hrc = row.child("hrc").value.property.value().trim();
				//System.out.println(territorySelection.value());
				createNewDocument(hrc);
			}
		}, null, null, null, null);
	}*/
	void openDocument(String _id) {
		//String _id = "";
		Intent intent = new Intent();
		intent.setClass(this, sweetlife.android10.supervisor.ActivityLimitEdit.class);
		intent.putExtra("_id", "" + _id);
		startActivity(intent);
	}

	void clearAll() {


		String sql="delete from limitidogovor where limitilist in (select Podrazdelenie from LimitiList where Otvetstvenniy="
				                                                      +ApplicationHoreca.getInstance().getClientInfo().getKod()
				                                                      +" or cast(Otvetstvenniy as int)=0);";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from LimitiList where Otvetstvenniy="
				                                                      +ApplicationHoreca.getInstance().getClientInfo().getKod()
				                                                      +" or cast(Otvetstvenniy as int)=0;");
		onResume();
	}

	void promptClear() {
		Auxiliary.pickConfirm(this, "Удалить все заявки?", "Выгрузить", new Task() {
			@Override
			public void doTask() {
				clearAll();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);
		if(item == menuNew) {
			openDocument("");
		}
		if(item == menuClear) {
			promptClear();
		}
		return super.onOptionsItemSelected(item);
	}
}
