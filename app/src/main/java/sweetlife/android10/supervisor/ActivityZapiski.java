package sweetlife.android10.supervisor;

import android.app.Activity;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;

import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

public class ActivityZapiski extends Activity {
	Layoutless layoutless;
	DataGrid dataGrid;
	ColumnText columnKontagent;
	ColumnText columnName;
	ColumnText columnNomer;
	ColumnDate columnData;
	ColumnText columnPrichina;
	MenuItem menuNew;
	int gridPageSize = 30;
	Bough data;
	Numeric gridOffset = new Numeric();

	void tap(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityZapiska.class);
		intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}
	public void requery() {
		dataGrid.clearColumns();
		String sql = "select"//
				+ " Zapiski._id,Zapiski.nomer,Zapiski.data,Zapiski.kontragent,Zapiski.naimenovanie,Zapiski.prichina"//
				+" ,(select kontragenty.naimenovanie from kontragenty where kontragenty.kod=Zapiski.kontragent limit 1) as kontragentlink"
				+ " from Zapiski"//
				+ " order by kontragentlink,Zapiski.naimenovanie,data,nomer limit " + (gridPageSize * 3)//
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
			columnKontagent.cell(//row.child("kontragent").value.property.value()+": "+
			row.child("kontragentlink").value.property.value(), tap);
			columnName.cell(row.child("naimenovanie").value.property.value(), tap);
			columnNomer.cell(//row.child("_id").value.property.value() + "/" + 
			row.child("nomer").value.property.value(), tap);
			columnData.cell((long)Numeric.string2double(
					row.child("data").value.property.value()), tap);
			columnPrichina.cell(row.child("prichina").value.property.value(), tap);

		}
		dataGrid.refresh();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuNew = menu.add("Новая служебная записка");
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean r = super.onOptionsItemSelected(item);
		if (item == menuNew) {
			Intent intent = new Intent();
			intent.setClass(this, ActivityZapiska.class);
			//startActivity(intent);
			this.startActivityForResult(intent, 0);
			return true;
		}
		return r;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		requery();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Служебные записки на договоры");
		Preferences.init(this);
		createGUI();
		requery();
	}
	void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		dataGrid = new DataGrid(this);
		columnKontagent = new ColumnText();
		columnName = new ColumnText();
		columnNomer = new ColumnText();
		columnData = new ColumnDate().format.is("dd.MM.yyyy");
		columnPrichina = new ColumnText();
		layoutless.child(dataGrid//
				.center.is(true)//
						.columns(new Column[] { columnNomer.title.is("#") //
								, columnData.title.is("дата") //
								, columnKontagent.title.is("контрагент").width.is(400) //
								, columnName.title.is("наименование").width.is(300) //
								, columnPrichina.title.is("причина") //
								})//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				);
	}
}
