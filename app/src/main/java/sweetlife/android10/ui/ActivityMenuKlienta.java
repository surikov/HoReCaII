package sweetlife.android10.ui;

import android.app.*;
//import android.app.Activity;
import android.os.*;

import sweetlife.android10.supervisor.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
import reactive.ui.*;

import sweetlife.android10.*;

public class ActivityMenuKlienta extends Activity {
	Layoutless layoutless;
	DataGrid itemsGrid;
	ColumnDescription columnItemNaimenovanie = new ColumnDescription();
	//Vector<Bough> rows = new Vector<Bough>();
	Bough dataItems = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGUI();
	}

	void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle("Заметки клиента " + ApplicationHoreca.getInstance().getClientInfo().getKod() + ". " + ApplicationHoreca.getInstance().getClientInfo().getName());
		itemsGrid = new DataGrid(this).center.is(true)//
				.pageSize.is(999)//itemsGridPageSize)//
		;
		layoutless.child(itemsGrid//
				.noHead.is(true)//
				.columns(new Column[]{ //
						columnItemNaimenovanie.title.is("Наименование").width.is(Auxiliary.tapSize * 16) //
				})//
				.left().is(0)
				.top().is(0 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this).labelText.is("Рецепт").afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptRecept();
					}
				})//
						.left().is(layoutless.width().property.minus(8 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 4)//
						.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).labelText.is("Описание").afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptDescription();
					}
				})//
						.left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 4)//
						.height().is(Auxiliary.tapSize)//
		);
		loadData();
	}

	void promptDescription() {
		final Note txt = new Note();
		Auxiliary.pickString(this, "Описание", txt, "Добавить", new Task() {
			@Override
			public void doTask() {
				Bough b = new Bough();
				b.name.property.value("Dishes");
				b.value.property.value(txt.value());
				//"Dishes")
				dataItems.child("document").children.add(b);
				sendData();
			}
		});
	}

	void promptRecept() {
		final Numeric sefIdx = new Numeric().value(-1);
		String sql = "select receptii.naimenovanie as recname,_idrref as _idrref from receptii order by receptii.naimenovanie;";
		final Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		if (b.children.size() > 0) {
			String rows[] = new String[b.children.size()];
			for (int i = 0; i < b.children.size(); i++) {
				rows[i] = b.children.get(i).child("recname").value.property.value();
			}
			sefIdx.afterChange(new Task() {
				public void doTask() {
					int idx = sefIdx.value().intValue();
					System.out.println(b.children.get(idx).child("recname").value.property.value()
							+ " / " + b.children.get(idx).child("_idrref").value.property.value()
					);
					Bough newItem = new Bough();
					newItem.name.property.value("Recipes");
					newItem.value.property.value("X'" + b.children.get(idx).child("_idrref").value.property.value() + "'");
					//"Dishes")
					dataItems.child("document").children.add(newItem);
					sendData();
				}
			}, true);
			Auxiliary.pickSingleChoice(this, rows, sefIdx);
		}
	}

	void sendData() {
		if (dataItems != null) {
			System.out.println(dataItems.dumpXML());
			String data = "{ \"client\": "
					+ ApplicationHoreca.getInstance().getClientInfo().getKod() + ", \"action\": \"save\""
					+ ",\"document\": { \"Client\":"
					+ ApplicationHoreca.getInstance().getClientInfo().getKod() + " ";
			/*String Recipes = "";
			String dlmtr = "";
			for (int i = 0; i < dataItems.child("document").children.size(); i++) {
				if (dataItems.child("document").children.get(i).name.property.value().equals("Recipes")) {
					Recipes = Recipes + dlmtr + "\"" + dataItems.child("document").children.get(i).value.property.value() + "\"";
					dlmtr = ", ";
				}
			}*/
			String Dishes = "";
			String dlmtr = "";
			for (int i = 0; i < dataItems.child("document").children.size(); i++) {
				if (dataItems.child("document").children.get(i).name.property.value().equals("Dishes")) {
					Dishes = Dishes + dlmtr + "\"" + dataItems.child("document").children.get(i).value.property.value() + "\"";
					dlmtr = ", ";
				}
			}
			/////////////
			String Recipes = "";
			for (int i = 0; i < dataItems.child("document").children.size(); i++) {
				if (dataItems.child("document").children.get(i).name.property.value().equals("Recipes")) {
					String idrref=dataItems.child("document").children.get(i).value.property.value();
					String sql = "select naimenovanie as naimenovanie from receptii where _idrref="
							+ dataItems.child("document").children.get(i).value.property.value() + ";";
					Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					String name = b.child("row").child("naimenovanie").value.property.value();
					Dishes = Dishes + dlmtr + "\"" + name + "\"";
					dlmtr = ", ";
				}
			}
			/////////////
			data = data + ", \"Recipes\": [ " + Recipes + " ] ";
			data = data + ", \"Dishes\": [ " + Dishes + " ] ";
			data = data + " } }";
			System.out.println(data);
			final String json = data;
			final Note message = new Note();
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/SmartDriveExchange/Menu/";
			Expect expect = new Expect().status.is("Обновление списка").task.is(new Task() {
				@Override
				public void doTask() {
					try {
						Bough b = Auxiliary.loadTextFromPrivatePOST(url, json, 60 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						System.out.println(b.dumpXML());
						String m = b.child("message").value.property.value();
						System.out.println("m " + m);
						String r = b.child("raw").value.property.value();
						if (!m.trim().equals("OK")) {
							message.value(b.child("raw").value.property.value());
							System.out.println("1message.value() " + message.value());
						} else {
							dataItems = Bough.parseJSON(r);
							System.out.println(dataItems.dumpXML());
							String mm = dataItems.child("message").value.property.value();
							System.out.println("mm " + mm);
							if (!mm.trim().equals("success")) {
								message.value(mm);
								System.out.println("2message.value() " + message.value());
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
						message.value(message.value() + " " + t.getMessage());
						System.out.println("3message.value() " + message.value());
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					if (message.value().trim().length() > 0) {
						Auxiliary.warn("Ошибка выгрузки: " + message.value(), ActivityMenuKlienta.this);
					}
					loadData();
				}
			});
			expect.start(this);
		}
	}

	public void loadData() {
		//ActivityMenuKlienta.this.rows.removeAllElements();
		//rows.removeAllElements();
		String kod = ApplicationHoreca.getInstance().getClientInfo().getKod();
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/SmartDriveExchange/Menu/";
		final String data = "{\"client\":\"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\",\"action\":\"open\"}";
		final Note message = new Note();
		System.out.println(url);
		Expect expect = new Expect().status.is("Обновление списка").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, data, 60 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String m = b.child("message").value.property.value();
					String r = b.child("raw").value.property.value();
					if (!m.trim().equals("OK")) {
						message.value(m);
					}
					dataItems = Bough.parseJSON(r);
					System.out.println(dataItems.dumpXML());
					String mm = dataItems.child("message").value.property.value();
					if (!mm.trim().equals("success")) {
						message.value(message.value() + " " + m);
					}
/*
I/System.out: <>
I/System.out: 	<client>122936</client>
I/System.out: 	<action>open</action>
I/System.out: 	<document>
I/System.out: 		<Client>122936</Client>
I/System.out: 		<Recipes>X'BBBA20677C60FED011EB775B81E581D6'</Recipes>
I/System.out: 		<Recipes>X'BBB720677C60FED011EAEEA9F4EAB9CF'</Recipes>
I/System.out: 		<Recipes>X'BBB720677C60FED011EAEDE2B8F433F6'</Recipes>
I/System.out: 		<Dishes>ййй</Dishes>
I/System.out: 		<Dishes>ффф</Dishes>
I/System.out: 		<Dishes></Dishes>
I/System.out: 	</document>
I/System.out: 	<message>success</message>
I/System.out: </>
*/
				} catch (Throwable t) {
					t.printStackTrace();
					message.value(message.value() + " " + t.getMessage());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (message.value().trim().length() > 0) {
					Auxiliary.warn("Ошибка: " + message.value(), ActivityMenuKlienta.this);
				}
				flipItemsGrid();
				itemsGrid.refresh();
			}
		});
		expect.start(this);
	}

	public void flipItemsGrid() {
		itemsGrid.clearColumns();
		if (dataItems != null) {
			System.out.println("dataItems "+dataItems.dumpXML());
			for (int i = 0; i < dataItems.child("document").children.size(); i++) {
				final int nn = i;
				Task tap = new Task() {
					@Override
					public void doTask() {
						if (dataItems != null) {
							Auxiliary.pickConfirm(ActivityMenuKlienta.this, "Удалить строку", "Удалить", new Task() {
								@Override
								public void doTask() {
									dataItems.child("document").children.removeElementAt(nn);
									sendData();
								}
							});
						}
					}
				};
				if (dataItems.child("document").children.get(i).name.property.value().equals("Recipes")) {
					String sql = "select naimenovanie as naimenovanie from receptii where _idrref="
							+ dataItems.child("document").children.get(i).value.property.value() + ";";
					Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
					String name = b.child("row").child("naimenovanie").value.property.value();
					columnItemNaimenovanie.cell(name, tap, "рецепт");
				} else {
					if (dataItems.child("document").children.get(i).name.property.value().equals("Dishes")) {
						columnItemNaimenovanie.cell(dataItems.child("document").children.get(i).value.property.value(), tap, "описание");
					}
				}
			}
		}
	}
}
