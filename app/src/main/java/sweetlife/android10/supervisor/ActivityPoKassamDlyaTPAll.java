package sweetlife.android10.supervisor;

import android.app.Activity;
import android.view.MenuItem;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;

import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;

public class ActivityPoKassamDlyaTPAll extends Activity {
	Layoutless layoutless;
	MenuItem menuDobavit;
	DataGrid dataGrid;
	ColumnText columnKontagentDogovor;
	//ColumnText columnTip;
	ColumnText columnTextSumma;
	ColumnDate columnData;
	int gridPageSize = 30;
	Bough data;
	Numeric gridOffset = new Numeric();

	void tap(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityPoKassamDlyaTPOne.class);
		intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGUI();
		requery();
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
			Intent intent = new Intent();
			intent.setClass(ActivityPoKassamDlyaTPAll.this, ActivityPoKassamDlyaTPOne.class);
			this.startActivityForResult(intent, 0);
			//startActivity(intent);
			//this.onActivityResult(requestCode, resultCode, data)
			return true;
		}
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		requery();
	}
	void requery() {
		dataGrid.clearColumns();
		String sql = "select * from PoKassamDlyaTP"//
				+ " order by"//
				+" case when vigruzhen>10 then 2 else 1 end"//
				+" ,vigruzhen desc, _id desc"//
				+ " limit " + (gridPageSize * 3)// 
				+ " offset " + gridOffset.value().intValue();
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			String d = row.child("Dogovor").value.property.value();
			String tipText=ActivityPoKassamDlyaTPOne.tipDescriptipon((int)Numeric.string2double(row.child("sozdan").value.property.value()));
			String dogText = tipText;
			if (row.child("sozdan").value.property.value().equals("0")) {
				//CfgDogovorInfo dogovor=Cfg.dogovorByKod(d);
				CfgKontragentInfo kontragent=Cfg.kontragentByDogovor(d);
				if( kontragent!=null){
					dogText = tipText+": "+kontragent.naimenovanie;
				}
				/*for (int n = 0; n < Cfg.dogovora().children.size(); n++) {
					String dg = Cfg.dogovora().children.get(n).child("dogovorKod").value.property.value();
					if (dg.equals(d)) {
						dogText = tipText+": "+Cfg.dogovora().children.get(n).child("kontragentNaimenovanie").value.property.value();
						break;
					}
				}*/
			}
			
			columnKontagentDogovor.cell(dogText, tap);
			//columnTip.cell(tipText, tap);
			columnTextSumma.cell(row.child("textSumma").value.property.value(), tap);
			columnData.cell((long) Numeric.string2double(row.child("vigruzhen").value.property.value()), tap);
		}
		dataGrid.refresh();
	}
	void createGUI() {
		this.setTitle("Кассовые чеки");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		dataGrid = new DataGrid(this);
		columnTextSumma = new ColumnText();
		columnKontagentDogovor = new ColumnText();
		//columnTip= new ColumnText();
		columnData = new ColumnDate().format.is("dd.MM.yyyy");
		layoutless.child(dataGrid//
				.center.is(true)//
						.columns(new Column[] { columnData.title.is("выгружено") //
								//,columnTip.title.is("тип") //
								, columnKontagentDogovor.title.is("договор контрагента").width.is(700) //
								, columnTextSumma.title.is("сумма").width.is(100) //
								})//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				);
	}
}
