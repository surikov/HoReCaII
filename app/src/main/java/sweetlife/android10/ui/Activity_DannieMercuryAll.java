package sweetlife.android10.ui;

import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.os.*;
import android.view.*;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.supervisor.Cfg;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.Task;

public class Activity_DannieMercuryAll extends Activity {
	Layoutless layoutless;
	ColumnDescription columnWhoGuid;
	ColumnDescription columnFileComment;
	DataGrid grid;
	int pageSize = 30;
	Numeric gridOffset = new Numeric();
	Bough data;
	MenuItem menuDobavit;
	Numeric sel = new Numeric();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle("Данные Меркурий");
		composeGUI();
		requery();
	}

	@Override
	protected void onResume() {
		super.onResume();
		grid.clearColumns();
		requery();
		grid.refresh();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuDobavit) {
			promptiDobavit();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuDobavit = menu.add("Добавить");
		return true;
	}

	void openOne(String id) {
		Intent intent = new Intent();
		intent.putExtra("id", "" + id);
		intent.setClass(Activity_DannieMercuryAll.this, Activity_DannieMercuryOne.class);
		Activity_DannieMercuryAll.this.startActivity(intent);
	}

	void promptiDobavit() {
		String[] names = new String[Cfg.kontragenty().children.size()];
		for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
			names[i] = Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value();
		}
		Auxiliary.pickSingleChoice(this, names, sel, "Контрагент", new Task() {
			@Override
			public void doTask() {
				String kod = Cfg.kontragenty().children.get(sel.value().intValue()).child("kod").value.property.value();
				String sql = "insert into DannieMercury (klient,comment) values (" + kod + ",'Не выгружен');";
				SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
				//System.out.println(sql);

				long inserted = statement.executeInsert();
				openOne("" + inserted);
				/*
				Intent intent = new Intent();
				intent.putExtra("id", "" + id);
				intent.setClass(Activity_DannieMercuryAll.this, Activity_DannieMercuryOne.class);
				Activity_DannieMercuryAll.this.startActivity(intent);
				*/
			}
		}, null, null, null, null);
	}

	void requery() {
		String sql = "select DannieMercury._id as id,comment,klient,guid,file,saved,kontragenty.naimenovanie as naimenovanie"//
				+ " from DannieMercury"//
				+ " join kontragenty on kontragenty.kod=DannieMercury.klient"//
				+ " order by kontragenty.naimenovanie,comment"//
				+ " limit " + (pageSize * 3) + " offset " + gridOffset.value().intValue()//
				;
		//System.out.println(sql);
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			//System.out.println(row.dumpXML());
			final String id = row.child("id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					openOne("" + id);
				}
			};
			columnWhoGuid.cell(row.child("naimenovanie").value.property.value(), tap, row.child("guid").value.property.value());
			columnFileComment.cell(row.child("file").value.property.value(), tap, row.child("comment").value.property.value());
		}
	}

	void composeGUI() {
		columnWhoGuid = new ColumnDescription();
		columnFileComment = new ColumnDescription();
		grid = new DataGrid(this);
		layoutless.child(grid//
				.columns(new Column[]{ //
						//
						columnWhoGuid.title.is("Контрагент").width.is(8 * Auxiliary.tapSize)//
						, columnFileComment.title.is("Комментарий").width.is(layoutless.width().property.minus(8 * Auxiliary.tapSize))//
				})//
				.beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requery();
					}
				})//
				.pageSize.is(pageSize)//
				.dataOffset.is(gridOffset)//
				.top().is(0 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
	}


}
