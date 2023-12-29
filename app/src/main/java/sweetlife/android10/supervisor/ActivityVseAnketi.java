package sweetlife.android10.supervisor;

import android.app.Activity;

import android.os.*;
import android.view.*;
import android.content.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import sweetlife.android10.Settings;
import tee.binding.task.*;
import tee.binding.*;
import tee.binding.it.*;

public class ActivityVseAnketi extends Activity {
	MenuItem menuAdd;
	MenuItem menuClear;
	MenuItem menuList;
	Layoutless layoutless;
	DataGrid grid;
	ColumnText vigruz;
	ColumnText terr;
	ColumnText name;
	Bough data;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuAdd = menu.add("Добавить");
		menuClear = menu.add("Удалить всё");
		menuList = menu.add("Выгруженные заявки");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuAdd) {
			Intent intent = new Intent();
			intent.setClass(this, ActivityOdnaAnketa.class);
			this.startActivityForResult(intent, 0);
			return true;
		}
		if (item == menuClear) {
			this.promptClear();
			return true;
		}
		if (item == menuList) {
			this.promptList();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	void promptList() {
		final Note msg = new Note();
		final Bough rows = new Bough();
		new Expect().status.is("Подождите").task.is(new Task() {
			@Override
			public void doTask() {
				String hrc = Cfg.selectedOrDbHRC();
				//hrc = "hrc336";
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaKlienta/SpisokZajavok/" + hrc;
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String data = new String(bytes, "UTF-8");
					Bough result = Bough.parseJSON(data);
					System.out.println("result: " + result.dumpXML());
					rows.children = result.children("Doc");
				} catch (Throwable t) {
					msg.value(msg.value() + '\n' + t.getMessage());
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (rows.children.size() > 0) {
					String[] items = new String[rows.children.size()];
					for (int i = 0; i < rows.children.size(); i++) {
						items[i] = "№" + rows.children.get(i).child("НомерЗаявки").value.property.value()
								+ " от " + rows.children.get(i).child("Дата").value.property.value()
								+ ": " + rows.children.get(i).child("Наименование").value.property.value();
					}
					final Numeric sel = new Numeric();
					Auxiliary.pickSingleChoice(ActivityVseAnketi.this, items, sel
							, "Получить шаблон договора"
							, new Task() {
								public void doTask() {
									System.out.println(rows.children.get(sel.value().intValue()).dumpXML());
									sendTemplate(rows.children.get(sel.value().intValue()).child("НомерЗаявки").value.property.value());
								}
							}, null, null, null, null
					);
				} else {
					Auxiliary.warn("Нет обработанных заявок" + msg.value(), ActivityVseAnketi.this);
				}
			}
		}).start(this);

	}

	void sendTemplate(final String docNum) {
		final Note msg = new Note();
		final Bough rows = new Bough();
		new Expect().status.is("Подождите").task.is(new Task() {
			@Override
			public void doTask() {
				String hrc = Cfg.selectedOrDbHRC();
				String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaKlienta/Dogovor/"+docNum+"/" + hrc;
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String data = new String(bytes, "UTF-8");
					Bough result = Bough.parseJSON(data);
					System.out.println("result: " + result.dumpXML());
					msg.value(msg.value() + '\n' + result.child("Сообщение").value.property.value());
				} catch (Throwable t) {
					msg.value(msg.value() + '\n' + t.getMessage());
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn("Отправка: " + msg.value(), ActivityVseAnketi.this);
			}
		}).start(this);
	}

	void promptClear() {
		Auxiliary.pickConfirm(this, "Удалить все анкеты", "Удалить", new Task() {
			public void doTask() {
				doClear();
			}
		});
	}

	void doClear() {
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AnketaKlienta");
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AnketaKlientaContacts");
		requery();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Анкеты новых клиентов");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		grid = new DataGrid(this);
		vigruz = new ColumnText();
		terr = new ColumnText();
		name = new ColumnText();
		grid.columns(new Column[]{ //
				//
				vigruz.title.is("Выгружен").width.is(4 * Auxiliary.tapSize)//
				, terr.title.is("Территория").width.is(4 * Auxiliary.tapSize)// 
				, name.title.is("Наименование").width.is(14 * Auxiliary.tapSize) //
		});
		layoutless.child(grid.width().is(layoutless.width().property).height().is(layoutless.height().property));
		requery();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		requery();
	}

	void tap(String id) {


		Intent intent = new Intent();
		intent.setClass(this, ActivityOdnaAnketa.class);
		intent.putExtra("_id", "" + id);
		this.startActivityForResult(intent, 0);
	}

	void requery() {
		String sql = "select"//
				+ " z._id,vigruzhen,p.Naimenovanie as Podrazdelenie,z.Naimenovanie"//
				+ " from AnketaKlienta z"//
				+ " left join Podrazdeleniya p on p.kod=z.Podrazdelenie"
				+ " order by vigruzhen,p.Naimenovanie,z.Naimenovanie";
		//System.out.println(sql);
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		grid.clearColumns();
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(_id);
				}
			};
			vigruz.cell(row.child("vigruzhen").value.property.value().length()<5 ? "нет" : "да", tap);
			terr.cell(row.child("Podrazdelenie").value.property.value(), tap);
			name.cell(row.child("Naimenovanie").value.property.value(), tap);
		}
		grid.refresh();
	}
}
