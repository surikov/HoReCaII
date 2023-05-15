package sweetlife.android10.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Column;
import reactive.ui.*;
import reactive.ui.DataGrid;
import reactive.ui.Layoutless;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.supervisor.ActivityWebView;
import sweetlife.android10.supervisor.Activity_FireBaseMessages;
import sweetlife.android10.supervisor.Cfg;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.Task;

public class Activity_Zametki extends Activity {
	DataGrid dataGrid;
	Layoutless layoutless;
	Bough gridData = new Bough();
	ColumnText txt;
	ColumnDate dat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String name = ApplicationHoreca.getInstance().getClientInfo().getName();
		this.setTitle("Заметки по контрагенту " + name);
		requeryGridData();
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		dataGrid = new DataGrid(this);
		txt = new ColumnText();
		dat=new ColumnDate();
		layoutless.child(dataGrid//
				.beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryGridData();
						flipGrid();
					}
				}).noHead.is(true).columns(						new Column[]{
						dat.format.is("dd.MM.yy").title.is("Дата").width.is(Auxiliary.tapSize*2)
								,txt.title.is("Сообщения").width.is(layoutless.width().property.minus(Auxiliary.tapSize*2))
						}
				)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		)
				.child(new Knob(this).labelText.is("Добавить").afterTap.is(new Task() {
					public void doTask() {
						promptNew();
					}
				})
						.left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))
						.width().is(3 * Auxiliary.tapSize)
						.height().is(Auxiliary.tapSize))
		;

		flipGrid();
		dataGrid.refresh();
	}

	void requeryGridData() {
		gridData.children.removeAllElements();
		String sql = "select _id as _id, dateCreate as dateCreate,zametka as zametka from Zametki where kontragentKod=" + ApplicationHoreca.getInstance().getClientInfo().getKod() + " order by dateCreate desc";
		gridData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
	}

	void flipGrid() {
		//System.out.println("flipGrid gridOffset "+this.gridOffset.value());
		dataGrid.clearColumns();
		//System.out.println("clearColumns flipGrid gridOffset "+this.gridOffset.value());
		//System.out.println(gridData.dumpXML());

		for (int i = 0; i < gridData.children.size(); i++) {
			Bough row = gridData.children.get(i);
			final String id = row.child("_id").value.property.value();
			Task tapTask = new Task() {
				public void doTask() {
					System.out.println(id);
					promptDelete(id);
				}
			};
			dat.cell((long)Numeric.string2double(row.child("dateCreate").value.property.value()), tapTask);
			txt.cell(row.child("zametka").value.property.value(), tapTask);
		}
		//System.out.println("after flipGrid gridOffset "+this.gridOffset.value());
	}

	void promptNew() {
		final Note note = new Note();
		Auxiliary.pickString(this, "Новая заметка", note, "Добавить", new Task() {
			public void doTask() {
				addNew(note.value());
				requeryGridData();
				flipGrid();
				dataGrid.refresh();
			}
		});
	}

	public static void addNew(String txt) {
		String sql = "insert into Zametki (dateCreate,kontragentKod,zametka) values ("
				+ new Date().getTime()
				+ "," + ApplicationHoreca.getInstance().getClientInfo().getKod()
				+ ",'" + txt.replace("\n", " ").replace("\r", " ").replace("'", "\"") + "'"
				+ ")";
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

	}

	void promptDelete(final String id) {
		Auxiliary.pickConfirm(this, "Удалить строку", "Удалить", new Task() {
			public void doTask() {
				doDelete(id);
				requeryGridData();
				flipGrid();
				dataGrid.refresh();
			}
		});
	}

	void doDelete(String id) {
		String sql = "delete from Zametki where _id=" + id;
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

	}
}