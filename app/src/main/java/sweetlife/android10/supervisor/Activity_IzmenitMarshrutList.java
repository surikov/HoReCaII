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

public class Activity_IzmenitMarshrutList extends Activity {

	MenuItem menuAdd;
	Layoutless layoutless;
	DataGrid grid;
	ColumnText dateTerr;
	ColumnDescription dateWho;
	ColumnDate dateMonth;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuAdd = menu.add("Добавить");
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.requery();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if(item == menuAdd) {
			Numeric _id = new Numeric();
			ContentValues values = new ContentValues();
			//values.put("marchrutDate", Calendar.getInstance().getTimeInMillis());
			values.put("marchrutDate", 0);
			values.put("territoryKod", 0);
			_id.value((double) ApplicationHoreca.getInstance().getDataBase().insert("AddKlientDayMarshrut", null, values));
			this.tap("" + _id.value());
			/*Intent intent = new Intent();
			intent.putExtra("_id", "" + _id.value());
			intent.setClass(this, Activity_IzmenitMarshrutOne.class);
			this.startActivity(intent);*/
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void tap(String id) {
		Intent intent = new Intent();
		intent.setClass(this, Activity_IzmenitMarshrutOne.class);
		intent.putExtra("_id", "" + id);
		this.startActivity(intent);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle("Заявки на добавление клиента в маршрут");
		this.grid = new DataGrid(this);
		this.dateTerr = new ColumnText();
		this.dateWho = new ColumnDescription();
		this.dateMonth = new ColumnDate();
		layoutless.child(this.grid.center.is(true)//
				                 .columns(new Column[]{ //
										dateMonth.format.is("LLLL, yyyy").title.is("Месяц").width.is(4 * Auxiliary.tapSize)//
						                 ,this.dateTerr.title.is("Территория").width.is(4 * Auxiliary.tapSize)//
						                 , this.dateWho.title.is("Код клиента").width.is(4 * Auxiliary.tapSize)
				                 }).width().is(layoutless.width().property).height().is(layoutless.height().property));
		requery();
	}

	void requery() {
		String sql = "select "//
							 +" *"
				             +" from AddKlientDayMarshrut"//
				             +" left join Podrazdeleniya on Podrazdeleniya.kod=AddKlientDayMarshrut.territoryKod"//
				             +" order by marchrutDate desc,klientKod limit 100;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		grid.clearColumns();
		for(int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			//System.out.println(row.dumpXML());
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			dateMonth.cell((long)Numeric.string2double(row.child("marchrutDate").value.property.value()), tap);
			dateTerr.cell(row.child("Naimenovanie").value.property.value(), tap);

			dateWho.cell(row.child("klientKod").value.property.value(), tap//
					,row.child("uploadDate").value.property.value().trim().length()>3?"выгружен":"не выгружен");
		}
		grid.refresh();
	}
}
