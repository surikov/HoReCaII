package sweetlife.android10.supervisor;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.Settings;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.MenuItem;

import java.text.*;

public class ActivityZayavkaVnutrenneePeremechenie extends Activity{
	Layoutless layoutless;
	MenuItem menuDobavit;
	MenuItem menuSend;
	DataGrid dataGrid = null;
	ColumnDescription columnArtikul = new ColumnDescription();
	ColumnText columnKlient = new ColumnText();
	ColumnText columnCount = new ColumnText();

	static Numeric feildDataPostuplenia = new Numeric();
	static Note feildSkladOtpravit = new Note().value("105");
	static Note feildSkladPoluchit = new Note().value("176");
	static Note feildKomentariy = new Note();

	static Note newArtikul = new Note();
	static Note newName = new Note();
	static Note newClient = new Note();
	static Numeric newCount = new Numeric();

	static Note filterWord = new Note();

	Numeric gridOffset = new Numeric();
	int gridPageSize = 300;

	static Bough gridData = new Bough();


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		createGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuDobavit = menu.add("Добавить номенклатуру");
		menuSend = menu.add("Отправить заявку");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		if(item == menuDobavit){
			//pickNomenklatura();
			pickNewItem(true);
			return true;
		}
		if(item == menuSend){
			promptSend();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume(){
		super.onResume();
		refreshGUI();
	}

	void promptSend(){

		String data = "";
		data = data + "{";
		data = data + "\"СкладОтправитель\": \"" + feildSkladOtpravit.value() + "\"";
		data = data + " ,\"СкладПолучатель\": \"" + feildSkladPoluchit.value() + "\"";
		data = data + " ,\"ЖелаемаяДатаПоступления\": \"" + Auxiliary.mssqlTime.format(new java.util.Date(feildDataPostuplenia.value().longValue())) + "\"";
		data = data + " ,\"Комментарий\": \"" + feildKomentariy.value() + "\"";
		data = data + " ,\"МассивЗаказа\": [";
		String del = "";
		for(int ii = 0; ii < gridData.children.size(); ii++){
			Bough row = gridData.children.get(ii);
			data = data + del + "{\"Артикул\": \"" + row.child("artikul").value.property.value() + "\"";
			data = data + ", \"НаименованиеТовара\": \"" + row.child("name").value.property.value() + "\"";
			data = data + ", \"Количество\": \"" + row.child("count").value.property.value() + "\"";
			data = data + ", \"Контрагент\": \"" + row.child("client").value.property.value() + "\"";
			data = data + "}";
			del = ", ";
		}
		data = data + "]}";
		final String txt = data;
		System.out.println(data);
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Planshet/RelocationRequest/" + Cfg.whoCheckListOwner();
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task(){
			@Override
			public void doTask(){
				try{
					Bough result = Auxiliary.loadTextFromPrivatePOST(url, txt.getBytes("utf-8"), 12000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), true);
					b.child("result").value.is(result.child("message").value.property.value());
					System.out.println(result.dumpXML());
				}catch(Throwable t){
					t.printStackTrace();
					b.child("result").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				Auxiliary.warn(b.child("result").value.property.value(), ActivityZayavkaVnutrenneePeremechenie.this);
			}
		});
		expect.start(ActivityZayavkaVnutrenneePeremechenie.this);
	}
void deleteRow( String keyDeleteArtikul,String keyDeleteName){
	for(int ii = 0; ii < gridData.children.size(); ii++){
		if(gridData.children.get(ii).child("artikul").value.property.value().equals(keyDeleteArtikul)
				|| gridData.children.get(ii).child("name").value.property.value().equals(keyDeleteName)
		){
			gridData.children.remove(ii);
		}
	}
}
	void pickNewItem(final boolean isNew){
		/*
		Bough row = new Bough().name.is("row");
		gridData.children.add(0,row);
		row.child("artikul").value.is("art" + Math.random());
		row.child("name").value.is("nam" + Math.random());
		row.child("client").value.is("kod" + Math.random());
		row.child("count").value.is("vsego" + Math.random());
		refreshGUI();
		*/

		String deleteName = null;
		Task deleteTask = null;
		String saveName = "Добавить";
		Task saveTask = new Task(){
			@Override
			public void doTask(){
				Bough row = new Bough().name.is("row");
				gridData.children.add(0, row);
				row.child("artikul").value.is(newArtikul.value());
				row.child("name").value.is(newName.value());
				row.child("count").value.is("" + newCount.value());
				row.child("client").value.is("" + newClient.value());
				refreshGUI();
			}
		};
		if(!isNew){
			final String keyDeleteArtikul = newArtikul.value();
			final String keyDeleteName = newName.value();
			saveName = "Сохранить";
			saveTask = new Task(){
				@Override
				public void doTask(){
					deleteRow(keyDeleteArtikul,keyDeleteName);
					/*
					for(int ii = 0; ii < gridData.children.size(); ii++){
						if(gridData.children.get(ii).child("artikul").value.property.value().equals(keyDeleteArtikul)
								|| gridData.children.get(ii).child("name").value.property.value().equals(keyDeleteName)
						){
							gridData.children.remove(ii);
						}
					}
					*/
					Bough row = new Bough().name.is("row");
					gridData.children.add(0, row);
					row.child("artikul").value.is(newArtikul.value());
					row.child("name").value.is(newName.value());
					row.child("count").value.is("" + newCount.value());
					row.child("client").value.is("" + newClient.value());
					refreshGUI();
				}
			};
			deleteName = "Удалить";
			deleteTask = new Task(){
				@Override
				public void doTask(){
					deleteRow(keyDeleteArtikul,keyDeleteName);
					/*for(int ii = 0; ii < gridData.children.size(); ii++){
						if(gridData.children.get(ii).child("artikul").value.property.value().equals(deleteArtikul)
								|| gridData.children.get(ii).child("name").value.property.value().equals(deleteName)
						){
							gridData.children.remove(ii);
						}
					}*/
					refreshGUI();
				}
			};
		}
		Auxiliary.pick(this, "Номенклатура", new SubLayoutless(this)
						.field(this, 0, "артикул", new RedactText(this).text.is(newArtikul))
						.field(this, 1, "наименование", new RedactText(this).text.is(newName))
						.field(this, 2, "количество", new RedactNumber(this).number.is(newCount))
						.field(this, 3, "код контрагент", new RedactText(this).text.is(newClient))
						.width().is(Auxiliary.tapSize * 9)
						.height().is(Auxiliary.tapSize * 6)

				, "Поиск", new Task(){
					@Override
					public void doTask(){
						pickNomenklatura(isNew);
					}
				}
				, saveName, saveTask
				, deleteName, deleteTask
		);
	}

	void pickNomenklatura(final boolean isNew){
		final ColumnText columnName = new ColumnText();
		final int gridPageSize = 55;
		final Numeric gridNameOffset = new Numeric();

		DataGrid2 artikulGrid = new DataGrid2(this).noHead.is(true).pageSize.is(gridPageSize).dataOffset.is(gridNameOffset)
				.columns(new Column[]{columnName.width.is(Auxiliary.tapSize * 9)});

		final AlertDialog dialog = Auxiliary.pick(this, null, new SubLayoutless(this)
						.child(new RedactText(this).text.is(filterWord).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1))
						.child(artikulGrid.top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 9))
						.width().is(Auxiliary.tapSize * 9)
						.height().is(Auxiliary.tapSize * 11)
				, null, null, null, null, null, null);
		artikulGrid.beforeFlip.is(new Task(){
			@Override
			public void doTask(){
				String sql = "select"
						+ " nn.artikul as artikul"
						+ " ,naimenovanie as naimenovanie"
						+ " from nomenklatura nn"
						+ " where nn.artikul<>x'00' and (uppername like '%" + filterWord.value() + "%' or nn.artikul like '%" + filterWord.value() + "%')"
						+ " order by uppername"
						+ " limit " + gridPageSize
						+ " offset " + gridNameOffset.value()
						+ ";";
				Bough artikulData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
				artikulGrid.clearColumns();
				for(int ii = 0; ii < artikulData.children.size(); ii++){
					Bough row = artikulData.children.get(ii);
					final String artikul = row.child("artikul").value.property.value();
					final String name = row.child("naimenovanie").value.property.value();
					Task tap = new Task(){
						@Override
						public void doTask(){
							dialog.cancel();
							System.out.println(artikul + ": " + name);
							newArtikul.value(artikul);
							newName.value(name);
							pickNewItem(isNew);
						}
					};
					columnName.cell(artikul + ": " + name, tap);
				}
			}
		});
		filterWord.afterChange(new Task(){
			@Override
			public void doTask(){
				gridNameOffset.value(0);
				artikulGrid.beforeFlip.property.value().doTask();
				artikulGrid.refresh();
			}
		});
	}


	void createGUI(){
		this.setTitle("Заявки на внутреннее перемещение");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		dataGrid = new DataGrid(this).center.is(true)//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
		;
		layoutless.child(dataGrid//
				.columns(new Column[]{ //
						columnCount.title.is("К-во").width.is(Auxiliary.tapSize * 2)//
						, columnArtikul.title.is("Номенклатура").width.is(Auxiliary.tapSize * 11)//
						, columnKlient.title.is("Контрагент").width.is(Auxiliary.tapSize * 3) //
				})//
				.top().is(Auxiliary.tapSize * 4)
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 4))//
		);
		layoutless.field(this, 0, "Склад отправитель", new Decor(this).labelText.is("Москва").labelAlignLeftCenter());
		layoutless.field(this, 1, "Склад получатель", new Decor(this).labelText.is("Краснодар").labelAlignLeftCenter());
		layoutless.field(this, 2, "Желаемая дата поступления", new RedactDate(this).date.is(feildDataPostuplenia).format.is("dd.MM.yyyy"));
		layoutless.field(this, 3, "Комментарий", new RedactText(this).text.is(feildKomentariy));
	}


	void promptDelete(final String artikul, final String name, final double count, final String client){
		newArtikul.value(artikul);
		newName.value(name);
		newClient.value(client);
		newCount.value(count);
		pickNewItem(false);
		//Auxiliary.pickConfirm(this, "" + artikul + " " + name, "Удалить", new Task(){
		//@Override
		//public void doTask(){
		//		pickNewItem(false);
				/*for(int ii = 0; ii < gridData.children.size(); ii++){
					if(gridData.children.get(ii).child("artikul").value.property.value().equals(artikul)
							|| gridData.children.get(ii).child("name").value.property.value().equals(name)
					){
						gridData.children.remove(ii);
					}
				}
				refreshGUI();*/
		//	}

		//});
	}

	public void fillGrid(){
		dataGrid.clearColumns();

		for(int i = 0; i < gridData.children.size(); i++){
			Bough row = gridData.children.get(i);
			final String artikul = row.child("artikul").value.property.value();
			final String name = row.child("name").value.property.value();
			final String client = row.child("client").value.property.value();
			final String count = row.child("count").value.property.value();
			Task task = new Task(){
				@Override
				public void doTask(){
					promptDelete(artikul, name, Numeric.string2double(count), client);
				}
			};
			columnCount.cell(count, task);
			columnArtikul.cell(artikul, task, name);
			columnKlient.cell(client, task);

				/*
				columnUploaded.cell(v.equals("1") ? "да" : "нет", task);
				columnDate.cell((long)Numeric.string2double(data), task);
				columnNumber.cell(nomer, task);
				columnKontragent.cell(row.child("kod").value.property.value().trim(), task);
				*/
		}

	}


	public void refreshGUI(){
		if(dataGrid != null){
			fillGrid();
			dataGrid.refresh();
			//System.out.println("refreshGUI");
		}
	}
}
