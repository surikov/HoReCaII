package sweetlife.android10.supervisor;

import android.app.Activity;
import android.os.*;
import android.view.*;
import android.content.*;
import android.net.*;

import android.database.sqlite.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

//import sweetlife.horeca.reports.ActivityReports;


public class ActivityLimitEdit extends Activity {

	private static final int FILE_SELECT_RESULT = 111;
	Layoutless layoutless;
	String _id = "";
	String kod1C = "";
	Numeric valueLimit = new Numeric();
	Numeric valueOtsrochka = new Numeric();
	Note valueKommentariy = new Note();
	Toggle filled = new Toggle();
	Numeric izmDate = new Numeric();
	ColumnText columnPath;
	MenuItem menuSave;
	MenuItem menuAdd;
	DataGrid grid;
	int pageSize = 30;
	Numeric gridOffset = new Numeric();
	Bough data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			String s = bundle.getString("_id");
			if(s != null) {
				_id = s;
			}
		}
		this.setTitle("Лимиты: " + _id);
		composeGUI();
		fillGUI();
	}

	void saveDB(String kod, String limit, String delay, String rem) {
		if(_id.trim().length()>0) {
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from LimitiList where _id=" + this._id.trim() + ";");
		}
		String sql = "insert into LimitiList ("//

				             + "\n Podrazdelenie, Data, DataIzm, Otvetstvenniy, Kommentarii"//
				             + "\n ) values ("//
				             + "\n '" + kod + "', " + delay + ", " + limit //
				             + ", " + ApplicationHoreca.getInstance().getClientInfo().getKod() + ",'" + rem + "'"//
				             + "\n )";
		System.out.println(sql);
		try {
			SQLiteStatement statement = ApplicationHoreca.getInstance().getDataBase().compileStatement(sql);
			long id = statement.executeInsert();
			_id = "" + id;
			Auxiliary.warn("Заявка сохранена. Добавьте файлы из меню.", ActivityLimitEdit.this);
			kod1C = kod;
			fillGUI();
		} catch(Throwable t) {
			t.printStackTrace();
			Auxiliary.warn(t.getMessage(), ActivityLimitEdit.this);
		}
	}

	void vigruzit() {
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/UvelLimita/" + Cfg.whoCheckListOwner() + "/";
		//final String url = "http://89.109.7.162/shatov/hs/UvelLimita/" + Cfg.currentHRC() + "/";
		final String limit = "" + this.valueLimit.value();
		final String delay = "" + this.valueOtsrochka.value();
		final String rem = this.valueKommentariy.value();
		ApplicationHoreca.getInstance().getClientInfo().getKod();
		final String post = "{\"Лимит\":\"" + limit + "\",\"Отсрочка\":\"" + delay + "\",\"КодКлиента\":\"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\",\"Комментарий\":\"" + rem + "\"}";
		System.out.println(url);
		System.out.println(post);
		final Bough raw = new Bough();
		new Expect().task.is(new Task() {
			@Override
			public void doTask() {
				try {
					Bough b = Auxiliary.loadTextFromPrivatePOST(url, post.getBytes("UTF-8"), 33000,Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword(), true);
					System.out.println(b.dumpXML());
					String t = b.child("raw").value.property.value();
					Bough json = Bough.parseJSON(t);
					raw.child("НомерЗаявки").value.is(json.child("НомерЗаявки").value.property.value());
					raw.child("Статус").value.is(json.child("Статус").value.property.value());
					raw.child("Сообщение").value.is(json.child("Сообщение").value.property.value());
					raw.child("message").value.is(b.child("message").value.property.value());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				System.out.println(raw.dumpXML());
				String kod = raw.child("НомерЗаявки").value.property.value();
				//_id
				if(kod.trim().length() > 1) {
					saveDB(kod, limit, delay, rem);
				}
				else {
					saveDB("", limit, delay, rem);
					Auxiliary.warn(raw.child("message").value.property.value() + "\n" + raw.child("Сообщение").value.property.value(), ActivityLimitEdit.this);
				}
			}
		}).status.is("Отправка заявки").start(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item == menuSave) {
			promptSave();
		}
		if(item == menuAdd) {
			promptAdd();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//System.out.println("onActivityResult is " + requestCode);
		switch(requestCode) {
			case FILE_SELECT_RESULT: {
				if(resultCode == RESULT_OK) {
					Uri uri = intent.getData();
					String path = Auxiliary.pathForMediaURI(this, uri);
					if(path != null && path.length() > 5) {
						sendFile(path);
					}
					else {
						Auxiliary.warn("Выберите файл из памяти устройства. Невозможно присоединить " + uri, this);
					}
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	void sendFile(final String filePath) {
		System.out.println(filePath);
		String[] spl=filePath.split("\\.");
		String rash=spl[spl.length-1];
		//http://89.109.7.162/hrc120107/hs/FileUvelLimita/000000001
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() //
				                   + "/hs/FileUvelLimita/" + this.kod1C + "/?rash="+rash;
//final String url = "http://89.109.7.162/shatov/hs/FileUvelLimita/" + this.kod1C + "/?rash="+rash;
		System.out.println(url);

		final Bough result=new Bough();

		try {
			File iofile = new File(filePath);
			int length = (int) iofile.length();
			final byte[] bytes =new byte[length];
			FileInputStream fileInputStream = new FileInputStream(iofile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			dataInputStream.readFully(bytes);
			dataInputStream.close();
			System.out.println(bytes.length);
			final Bough raw = new Bough();
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					try {
						Bough b = Auxiliary.loadTextFromPrivatePOST(url, bytes, 33000, Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword(), false);
						System.out.println(b.dumpXML());
						result.child("raw").value.is(b.child("raw").value.property.value());
						result.child("message").value.is(b.child("message").value.property.value());

					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					if(result.child("raw").value.property.value().equals("Всё ОК")){
						saveFile(filePath);
					}
					Auxiliary.warn(result.child("message").value.property.value()+": "+result.child("raw").value.property.value(),ActivityLimitEdit.this);
				}
			}).status.is("Отправка файла").start(this);
		}catch(Throwable t){
			Auxiliary.warn(t.getMessage(),this);
		}
	}
	void saveFile(String path){
		String sql="insert into limitidogovor (limitilist,kommentariitp) values ("//
						   +"'"+this.kod1C//
						   +"','"+path+"'"//
				           +")";
		System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		fillGUI();
	}

	void showFileChooser() {
		//System.out.println("showFileChooser");
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			Intent chooser = Intent.createChooser(intent, "Выбрать файл");
			startActivityForResult(chooser, FILE_SELECT_RESULT);
		} catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	void promptAdd() {
		if(kod1C.equals("")) {
			Auxiliary.warn("Сначала нужно выгрузить заявку.", ActivityLimitEdit.this);
		}
		else {
			showFileChooser();
		}
	}

	void promptSave() {
		if(kod1C.equals("")) {
			Auxiliary.pickConfirm(this, "Выгрузить документ?", "Выгрузить", new Task() {
				@Override
				public void doTask() {
					vigruzit();
				}
			});
		}
		else {
			System.out.println("uploaded already: " + this.kod1C);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuSave = menu.add("Выгрузить заявку");
		menuAdd = menu.add("Добавить файл");
		return true;
	}

	void fillGUI() {
		grid.clearColumns();
		if(kod1C.equals("")) {
			filled.value(false);
		}
		else {
			filled.value(true);
		}
		requery();
		grid.refresh();
	}

	void composeGUI() {
		columnPath = new ColumnText();
		if(this._id.trim().length() > 0) {
			Bough d = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(//
					"select LimitiList._id as _id, DataIzm as limitval, Data as delay,Kommentarii as rem,Podrazdelenie as kod from LimitiList where _id=" + this._id, null));
			kod1C = d.child("row").child("kod").value.property.value();
			valueLimit.value(Numeric.string2double(d.child("row").child("limitval").value.property.value()));
			valueOtsrochka.value(Numeric.string2double(d.child("row").child("delay").value.property.value()));
			valueKommentariy.value(d.child("row").child("rem").value.property.value());
		}
		this.setTitle("Лимиты: " + _id+" "+kod1C);
		grid = new DataGrid(this);
		layoutless//
				.child(new Decor(this).labelText.is("лимит").labelAlignRightCenter().labelStyleMediumNormal()//
						       .left().is(0 * Auxiliary.tapSize).top().is(0 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize - 8).height().is(Auxiliary.tapSize)//
				)//
				.child(new RedactNumber(this).number.is(this.valueLimit).hidden().is(filled)//
						       .left().is(3 * Auxiliary.tapSize).top().is(8 + 0 * Auxiliary.tapSize).width().is(6 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(this.valueLimit.asNote()).labelAlignLeftCenter().hidden().is(filled.not())//
						       .left().is(3 * Auxiliary.tapSize).top().is(8 + 0 * Auxiliary.tapSize).width().is(6 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is("отсрочка").labelAlignRightCenter().labelStyleMediumNormal()//
						       .left().is(9 * Auxiliary.tapSize).top().is(0 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize - 8).height().is(Auxiliary.tapSize)//
				)//
				.child(new RedactNumber(this).number.is(this.valueOtsrochka).hidden().is(filled)//
						       .left().is(11 * Auxiliary.tapSize).top().is(8 + 0 * Auxiliary.tapSize).width().is(6 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(this.valueOtsrochka.asNote()).labelAlignLeftCenter().hidden().is(filled.not())//
						       .left().is(11 * Auxiliary.tapSize).top().is(8 + 0 * Auxiliary.tapSize).width().is(6 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is("комментарий").labelAlignRightCenter().labelStyleMediumNormal()//
						       .left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(3 * Auxiliary.tapSize - 8).height().is(Auxiliary.tapSize)//
				)//
				.child(new RedactText(this).text.is(this.valueKommentariy).hidden().is(filled)//
						       .left().is(3 * Auxiliary.tapSize).top().is(8 + 1 * Auxiliary.tapSize).width().is(14 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(this.valueKommentariy).labelAlignLeftCenter().hidden().is(filled.not())//
						       .left().is(3 * Auxiliary.tapSize).top().is(8 + 1 * Auxiliary.tapSize).width().is(14 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize)//
				)//
		;
		layoutless.child(grid//
				                 .columns(new Column[]{ //
						                 columnPath.title.is("Файл").width.is(layoutless.width().property)//
				                 })//
				                 .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requery();
					}
				})//
				                 .pageSize.is(pageSize)//
				                 .dataOffset.is(gridOffset)//
				                 .top().is(2 * Auxiliary.tapSize)//
				                 .width().is(layoutless.width().property)//
				                 .height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
	}

	void requery() {
		if(_id.equals("")) {
			return;
		}
		String sql="select kommentariitp as path from limitidogovor where limitilist='"//
						   +this.kod1C//
						   +"'";
		System.out.println(sql);
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(data.dumpXML());
		for(int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			columnPath.cell(row.child("path").value.property.value());
		}
		/*String sql = "select LimitiDogovor._id as _id"//
				             + ", LimitiList "//
				             + ", Gruppa "//
				             + ", TO1 "//
				             + ", TO2 "//
				             + ", TOPlan "//
				             + ", Otsrochka "//
				             + ", OtsrochkaPlan "//
				             + ", LimitValue "//
				             + ", LimitSV "//
				             + ", LimitPlan "//
				             + ", KommentariiSv "//
				             + ", KommentariiFin "//
				             + ", Poruchitelstvo "//
				             + ", LimitPoDogovoru "//
				             + ", Territiriya "//
				             + ", LimitRachet "//
				             + ", PlanTO "//
				             + ", KommentariiTP"//
				             + ", GruppyDogovorov.Naimenovanie as gruppaName"//
				             + " from LimitiDogovor"//
				             + " left join GruppyDogovorov on GruppyDogovorov.kod=Gruppa" //
				             + " where LimitiList=" + _id //
				             + " order by LimitiDogovor._id"//
				             + " limit " + (pageSize * 3) + " offset " + gridOffset.value().intValue();
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for(int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String id = row.child("_id").value.property.value();
			Task tap = new Task() {
				@Override
				public void doTask() {
					tap(id);
				}
			};
			columnPath.cell(row.child("gruppaName").value.property.value(), tap);
		}*/
	}
/*
	void tap(String id) {
		//
	}*/

	@Override
	protected void onResume() {
		super.onResume();
		grid.clearColumns();
		requery();
		grid.refresh();
	}
}
