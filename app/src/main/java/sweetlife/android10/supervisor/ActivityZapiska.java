package sweetlife.android10.supervisor;
import android.app.Activity;

import java.io.File;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.utils.SystemHelper;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

/*
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
		xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
	<soap:Body>
		<m:Get xmlns:m="http://ws.swl/SlNaDogovor">
			<Nomer>000000001</Nomer>
		</m:Get>
	</soap:Body>
</soap:Envelope>
*/
public class ActivityZapiska extends Activity {
	public final static String prichina_0 = "";
	public final static String prichina_1 = "Новый договор";
	public final static String prichina_2 = "Перезаключение";
	public final static String prichina_3 = "Изменение коммерческих условий";
	private static final int FILE_SELECT_RESULT = 111;
	Numeric OsnovnoiKlientTT = new Numeric();
	RedactSingleChoice OsnovnoiKlientTTChoice;
	String[] kindNames = null;
	int[] kindNumbers = null;
	Layoutless layoutless;
	MenuItem menuAdd;
	MenuItem menuSave;
	MenuItem menuDelete;
	MenuItem menuUpload;
	MenuItem menuDownload;
	DataGrid dataGrid;
	ColumnText columnKind;
	ColumnText columnPath;
	Numeric _id = new Numeric();
	Note nomer = new Note();
	int dataRaw = 0;
	Numeric data = new Numeric();
	//Note kontragent = new Note();
	Note naimenovanie = new Note();//.value("[нет]");
	Numeric nalbeznal = new Numeric();
	Note prichinaDescr = new Note();
	Numeric prichinaN = new Numeric();
	Numeric nashklient = new Numeric();
	Note tovarooborot = new Note();
	Note limity = new Note();
	Note prochee = new Note();
	Note otsrochka = new Note();
	Note bonusy = new Note();
	Note minrazmerzakaza = new Note();
	Note poruchitelstvo = new Note();
	Bough files = null;
	public static String getPath(Context context, Uri uri) {
		if (uri.getScheme().equalsIgnoreCase("content")) {
			String[] projection = {"_data"};
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		else {
			if (uri.getScheme().equalsIgnoreCase("file")) {
				return uri.getPath();
			}
		}
		return uri.toString();
	}
	/*Vector<String> _ids = new Vector<String>();
	Vector<Integer> kinds = new Vector<Integer>();
	Vector<String> paths = new Vector<String>();*/
	/*Task tapFile = new Task() {
		@Override
		public void doTask() {
			// TODO Auto-generated method stub
		}
	};*/
	int[] getKindNumbers() {
		if (kindNumbers == null) {
			kindNumbers = new int[]{//
					12//
					, 11//
					, 14//
					, 3//
					, 2//
					, 6//
					, 10//
					, 18//
					, 13//
					, 7//
					, 8//
					, 4//
					, 17//
					, 15//
					, 16//
					, 9 //
					, 22 //
			};
		}
		return kindNumbers;
	}
	String[] getKindNames() {
		if (kindNames == null) {
			kindNames = new String[]{//
					"Анкета клиента"//
					, "Договор аренды / св-во о госрегистрации"//
					, "Договор о переуступке долга"//
					, "Договор поручительства"//
					, "Договор поставки"//
					, "Дополнительное соглашение"//
					, "Паспортные данные"//
					, "Приказ о назначении директора"//
					, "Протокол, письмо"//
					, "Реквизиты, решение, приказ, протокол, письмо"//
					, "Свидетельство ЕГРЮЛ"//
					, "Свидетельство ИНН"//
					, "Свидетельство ОГРН"//
					, "Спецификация"//
					, "Схема проезда к клиенту/поставщику"//
					, "Устав"//
					, "Регистрация Меркурий"//
			};
		}
		return kindNames;
	}
	void prichina(String p) {
		if (p.equals(prichina_1)) {
			prichinaN.value(1);
		}
		else {
			if (p.equals(prichina_2)) {
				prichinaN.value(2);
			}
			else {
				if (p.equals(prichina_3)) {
					prichinaN.value(3);
				}
				else {
					prichinaN.value(0);
				}
			}
		}
	}
	String prichina() {
		if (prichinaN.value().intValue() == 1) {
			return prichina_1;
		}
		if (prichinaN.value().intValue() == 2) {
			return prichina_2;
		}
		if (prichinaN.value().intValue() == 3) {
			return prichina_3;
		}
		return "";
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuAdd = menu.add("Добавить файл");
		menuSave = menu.add("Сохранить и закрыть");
		menuDelete = menu.add("Удалить служебную записку");
		menuUpload = menu.add("Выгрузить на сервер");
		menuDownload = menu.add("Обновить с сервера");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//System.out.println("onOptionsItemSelected is " + item);
		//boolean r = super.onOptionsItemSelected(item);
		if (item == menuAdd) {
			showFileChooser();
			return true;
		}
		if (item == menuSave) {
			doSave();
			this.finish();
			return true;
		}
		if (item == menuDelete) {
			doDelete();
			return true;
		}
		if (item == menuUpload) {
			doSave();
			doUpload();
			return true;
		}
		if (item == menuDownload) {
			doDownload();
			return true;
		}
		return false;
	}
	void doDownload() {
		if (nomer.value().length() > 2) {
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
					+ "\n<soap:Envelope "//
					+ "\n		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "//
					+ "\n		xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "//
					+ "\n		xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
					+ "\n	<soap:Body>"//
					+ "\n		<m:Get xmlns:m=\"http://ws.swl/SlNaDogovor\">"//
					+ "\n			<Nomer>" + nomer.value() + "</Nomer>"//
					+ "\n		</m:Get>"//
					+ "\n	</soap:Body>"//
					+ "\n</soap:Envelope>";
			//System.out.println(xml);
			String url = Settings.getInstance().getBaseURL() + "SlNaDogovor.1cws";
			final RawSOAP r = new RawSOAP();
			r.url.is(url)//
					.xml.is(xml)//
					.afterError.is(new Task() {
				@Override
				public void doTask() {
					Auxiliary.warn(r.exception.property.value().toString(), ActivityZapiska.this);
				}
			}).afterSuccess.is(new Task() {
				@Override
				public void doTask() {
					//System.out.println(r.data.dumpXML());
					Bough d = r.data.child("soap:Body").child("m:GetResponse").child("m:return");
					if (d.child("m:Nazvanie").value.property.value().trim().length() < 1) {
						Auxiliary.warn(d.dumpXML(), ActivityZapiska.this);
					}
					else {
						String kk = d.child("m:KodKlienta").value.property.value();
						for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
							if (kk.trim().equals(Cfg.kontragenty().children.get(i).child("kod").value.property.value())) {
								OsnovnoiKlientTT.value(i + 1);
								break;
							}
						}
						naimenovanie.value(d.child("m:Nazvanie").value.property.value());
						nalbeznal.value(Numeric.string2double(d.child("m:TipOplaty").value.property.value()));
						nashklient.value(Numeric.string2double(d.child("m:FormaDogovora").value.property.value()));
						naimenovanie.value(d.child("m:Nazvanie").value.property.value());
						prichina(d.child("m:Prichina").value.property.value());
						limity.value(d.child("m:Limit").value.property.value());
						otsrochka.value(d.child("m:Otsrochka").value.property.value());
						bonusy.value(d.child("m:Bonus").value.property.value());
						minrazmerzakaza.value(d.child("m:minRazmer").value.property.value());
						poruchitelstvo.value(d.child("m:Poruch").value.property.value());
						tovarooborot.value(d.child("m:TO").value.property.value());
					}
				}
			}).startLater(this, "Обновление",Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
		}
	}
	void doSave() {
		//String sql = "";
		int kn = OsnovnoiKlientTT.value().intValue();
		if (kn > 0 && kn < Cfg.kontragenty().children.size()) {
			kn = (int) Numeric.string2double(Cfg.kontragenty().children.get(kn - 1).child("kod").value.property.value());
		}
		if (_id.value() > 0) {
			String sql = "update Zapiski set"//
					+ " kontragent=" + kn//
					+ ", data=\"" + data.value().longValue() + "\""//
					+ ", naimenovanie=\"" + naimenovanie.value().replace('"', '\'') + "\""//
					+ ", nomer=\"" + nomer.value().replace('"', '\'') + "\""//
					+ ", nalbeznal=\"" + nalbeznal.value() + "\""//
					+ ", prichina=\"" + prichina().replace('"', '\'') + "\""//
					+ ", nashklient=\"" + nashklient.value().intValue() + "\""//
					+ ", tovarooborot=\"" + tovarooborot.value().replace('"', '\'') + "\""//
					+ ", limity=\"" + limity.value().replace('"', '\'') + "\""//
					+ ", otsrochka=\"" + otsrochka.value().replace('"', '\'') + "\""//
					+ ", bonusy=\"" + bonusy.value().replace('"', '\'') + "\""//
					+ ", minrazmerzakaza=\"" + minrazmerzakaza.value().replace('"', '\'') + "\""//
					+ ", poruchitelstvo=\"" + poruchitelstvo.value().replace('"', '\'') + "\""//
					+ ", prochee=\"" + prochee.value().replace('"', '\'') + "\""//
					+ " where _id=" + _id.value().intValue()//
					;
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			/*
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZapiskiFiles where zapiska=" + _id.value().intValue());
			for (int i = 0; i < _ids.size(); i++) {
				String sql2 = "insert into Zapiski (zapiska,kind,path) values ("//
						+ _id.value().intValue()//
						+ "," + kinds.get(i)//
						+ "," + paths.get(i)//
						+ ")"//
				;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql2);
			}
			*/
		}
		else {
			/*sql = "insert into Zapiski (nomer,data,kontragent,naimenovanie,nalbeznal,prichina,nashklient,tovarooborot,limity,otsrochka,bonusy,minrazmerzakaza,poruchitelstvo,prochee) values"//
					+ " ('" + nomer.value()//
					+ "','" + dataRaw//
					+ "','" + kontragent.value()//
					+ "','" + naimenovanie.value() //
					+ "','" + nalbeznal.value()//
					+ "','" + prichina.value()//
					+ "','" + nashklient.value() //
					+ "','" + tovarooborot.value()//
					+ "','" + limity.value()//
					+ "','" + otsrochka.value()//
					+ "','" + bonusy.value()//
					+ "','" + minrazmerzakaza.value()//
					+ "','" + poruchitelstvo.value()//
					+ "','" + prochee.value()//
					+ "');";
			System.out.println(sql);*/
			ContentValues values = new ContentValues();
			values.put("kontragent", kn);
			values.put("data", data.value().longValue());
			values.put("naimenovanie", naimenovanie.value().replace('"', '\''));
			values.put("nalbeznal", nalbeznal.value().intValue());
			values.put("prichina", prichina().replace('"', '\''));
			values.put("nashklient", nashklient.value().intValue());
			values.put("tovarooborot", tovarooborot.value().replace('"', '\''));
			values.put("limity", limity.value().replace('"', '\''));
			values.put("otsrochka", otsrochka.value().replace('"', '\''));
			values.put("bonusy", bonusy.value().replace('"', '\''));
			values.put("minrazmerzakaza", minrazmerzakaza.value().replace('"', '\''));
			values.put("poruchitelstvo", poruchitelstvo.value().replace('"', '\''));
			values.put("prochee", prochee.value().replace('"', '\''));
			_id.value((double) ApplicationHoreca.getInstance().getDataBase().insert("Zapiski", null, values));
			//System.out.println("insert " + _id.value());
		}
	}
	void doDelete() {
		if (_id.value() > 0) {
			Auxiliary.pickConfirm(this, "Удалить записку?", "Удалить", new Task() {
				@Override
				public void doTask() {
					deleteAndClose();
				}
			});
		}
	}
	void deleteAndClose() {
		String sql = "delete from ZapiskiFiles where zapiska=" + _id.value().intValue();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		sql = "delete from Zapiski where _id=" + _id.value().intValue();
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		this.finish();
	}
	void doUpload() {
		int kn = OsnovnoiKlientTT.value().intValue();
		if (kn > 0 && kn < Cfg.kontragenty().children.size()) {
			kn = (int) Numeric.string2double(Cfg.kontragenty().children.get(kn - 1).child("kod").value.property.value());
			if (naimenovanie.value().trim().length() < 2) {
				String naim = Cfg.kontragenty().children.get(OsnovnoiKlientTT.value().intValue() - 1).child("naimenovanie").value.property.value();
				naimenovanie.value(naim);
			}
		}
		if (naimenovanie.value().trim().length() < 2) {
			Auxiliary.warn("Не выбран контрагент и не заполнено наименование!", ActivityZapiska.this);
			return;
		}
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "\n<soap:Envelope "//
				+ "\n		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""//
				+ "\n		xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "//
				+ "\n		xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n	<soap:Body>"//
				+ "\n		<m:Vifruzit xmlns:m=\"http://ws.swl/SlNaDogovor\">"//
				+ "\n			<m:Dok>"//
				+ "\n				<m:TP>";
		if (files != null) {
			for (int i = 0; i < files.children.size(); i++) {
				Bough row = files.children.get(i);
				int k = (int) Numeric.string2double(row.child("kind").value.property.value());
				String pth = row.child("path").value.property.value();
				//System.out.println(pth);

				String rassh = "none";
				String parts[] = pth.split("\\.");
				if (parts.length > 0) {
					if (parts[parts.length - 1].length() < 5) {
						rassh = parts[parts.length - 1];
					}
				}
				//System.out.println(rassh);
				File file=new File(pth);
				String content = Base64.encodeToString(SystemHelper.readBytesFromFile(file), Base64.DEFAULT);
				xml = xml + "\n					<m:str>"//
						+ "\n						<m:Vid>" + getKindNumbers()[k] + "</m:Vid>"//
						+ "\n						<m:File>" + content + "</m:File>"//
						+ "\n						<m:rassh>" + rassh + "</m:rassh>"//
						+ "\n					</m:str>";
			}
		}
		xml = xml + "\n				</m:TP>"//
				+ "\n				<m:KodKlienta>" + kn + "</m:KodKlienta>"//
				+ "\n				<m:Nazvanie>" + naimenovanie.value() + "</m:Nazvanie>"//
				+ "\n				<m:TipOplaty>" + nalbeznal.value().intValue() + "</m:TipOplaty>"//
				+ "\n				<m:FormaDogovora>" + nashklient.value().intValue() + "</m:FormaDogovora>"//
				+ "\n				<m:Prichina>" + prichina() + "</m:Prichina>"//
				+ "\n				<m:Nomer>" + nomer.value() + "</m:Nomer>"//
				+ "\n				<m:Limit>" + limity.value() + "</m:Limit>"//
				+ "\n				<m:Otsrochka>" + otsrochka.value() + "</m:Otsrochka>"//
				+ "\n				<m:Bonus>" + bonusy.value() + "</m:Bonus>"//
				+ "\n				<m:minRazmer>" + minrazmerzakaza.value() + "</m:minRazmer>"//
				+ "\n				<m:Poruch>" + poruchitelstvo.value() + "</m:Poruch>"//
				+ "\n				<m:TO>" + tovarooborot.value() + "</m:TO>"//
				+ "\n			</m:Dok>"//
				+ "\n			<m:Otv>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</m:Otv>"//
				+ "\n		</m:Vifruzit>"//
				+ "\n	</soap:Body>"//
				+ "\n</soap:Envelope>";
		//System.out.println(xml);
		String url = Settings.getInstance().getBaseURL() + "SlNaDogovor.1cws";
		final RawSOAP r = new RawSOAP();
		r.url.is(url)//
				.xml.is(xml)//
				.afterError.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(r.exception.property.value().toString(), ActivityZapiska.this);
			}
		}).afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println(r.data.dumpXML());
				String ret = r.data.child("soap:Body").child("m:VifruzitResponse").child("m:return").value.property.value();
				if (ret.length() > 2) {
					if (ret.startsWith("0")) {
						nomer.value(ret);
						doSave();
						//requery();
						ActivityZapiska.this.finish();
					}
					else {
						Auxiliary.warn(r.data.dumpXML(), ActivityZapiska.this);
					}
				}
				else {
					String s = r.data.dumpXML();
					Auxiliary.warn(s, ActivityZapiska.this);
				}
			}
		}).startLater(this, "Выгрузка",Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.init(this);
		createGUI();
		Bundle bundle = getIntent().getExtras();
		this.setTitle("Новая служебная записки на договоры");
		if (bundle != null) {
			String s = bundle.getString("_id");
			if (s != null) {
				double nn = Numeric.string2double(s);
				if (nn > 0) {
					_id.value(nn);
					//System.out.println("_id.value() " + _id.value());
					requery();
					this.setTitle("Служебная записки на договоры");
				}
			}
		}
	}
	void requery() {
		String sql = "select * from Zapiski where _id=" + _id.value().intValue();
		//System.out.println(sql);
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(b.dumpXML());
		data.value(Numeric.string2double(b.child("row").child("data").value.property.value()));
		String kn = b.child("row").child("kontragent").value.property.value();
		//System.out.println("kn "+kn);
		if (kn.length() > 1) {
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				if (Cfg.kontragenty().children.get(i).child("kod").value.property.value().equals(kn)) {
					OsnovnoiKlientTT.value(i + 1);
					//System.out.println("i "+i);
					break;
				}
			}
		}
		//OsnovnoiKlientTT.value(Numeric.string2double(b.child("row").child("kontragent").value.property.value()));
		nomer.value(b.child("row").child("nomer").value.property.value());
		naimenovanie.value(b.child("row").child("naimenovanie").value.property.value());
		nalbeznal.value(Numeric.string2double(b.child("row").child("nalbeznal").value.property.value()));
		prichina(b.child("row").child("prichina").value.property.value());
		nashklient.value(Numeric.string2double(b.child("row").child("nashklient").value.property.value()));
		//prichina.value(b.child("row").child("prichina").value.property.value());
		tovarooborot.value(b.child("row").child("tovarooborot").value.property.value());
		limity.value(b.child("row").child("limity").value.property.value());
		prochee.value(b.child("row").child("prochee").value.property.value());
		otsrochka.value(b.child("row").child("otsrochka").value.property.value());
		bonusy.value(b.child("row").child("bonusy").value.property.value());
		minrazmerzakaza.value(b.child("row").child("minrazmerzakaza").value.property.value());
		poruchitelstvo.value(b.child("row").child("poruchitelstvo").value.property.value());
		//
		dataGrid.clearColumns();
		if (_id.value() > 0) {
			sql = "select * from ZapiskiFiles where zapiska=" + _id.value().intValue() + " order by kind;";
			//System.out.println(sql);
			files = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(files.dumpXML());
			for (int i = 0; i < files.children.size(); i++) {
				Bough row = files.children.get(i);
				int k = (int) Numeric.string2double(row.child("kind").value.property.value());
				final String fid = row.child("_id").value.property.value();
				final String pth = row.child("path").value.property.value();
				Task tapFile = new Task() {
					@Override
					public void doTask() {
						promptFile(fid, pth);
					}
				};
				columnKind.cell(getKindNames()[k], tapFile);
				columnPath.cell(pth, tapFile);
			}
		}
		dataGrid.refresh();
		//System.out.println("requery done");
	}
	void promptFile(final String _id, final String path) {
		final Numeric nn = new Numeric();
		Auxiliary.pickSingleChoice(this, new String[]{"Открыть файл", "Удалить файл из записки"}, nn, null, new Task() {
			@Override
			public void doTask() {
				//System.out.println("тт "+nn.value());
				if (nn.value().intValue() == 1) {
					deleteSubFile(_id);
				}
				else {
					if (nn.value().intValue() == 0) {
						openSubFile(path);
					}
				}
			}
		}, null, null, null, null);
	}
	void deleteSubFile(final String _id) {
		Auxiliary.pickConfirm(this, "Удаление файл из записки", "Удалить", new Task() {
			@Override
			public void doTask() {
				ApplicationHoreca.getInstance().getDataBase().execSQL("delete from ZapiskiFiles where _id=" + _id);
				requery();
			}
		});
	}
	void openSubFile(String path) {
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl("file://" + path));
		Auxiliary.startFile(this, android.content.Intent.ACTION_VIEW, mime, new File(path));
	}
	private void showFileChooser() {
		//System.out.println("showFileChooser");
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			Intent chooser = Intent.createChooser(intent, "Выбрать файл");
			startActivityForResult(chooser, FILE_SELECT_RESULT);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	void promptFileKind(final String file) {
		final Numeric nn = new Numeric();
		Auxiliary.pickSingleChoice(this, getKindNames(), nn, file, new Task() {
			@Override
			public void doTask() {
				//System.out.println("promptFileKind is " + getKindNames()[nn.value().intValue()] + " for " + file);
				addFile(nn.value().intValue(), file);
			}
		}, null, null, null, null);
	}
	void addFile(int kind, String path) {
		doSave();
		String sql = "insert into ZapiskiFiles (zapiska,kind,path) values ("//
				+ _id.value().intValue()//
				+ "," + kind//
				+ ",\"" + path + "\""//
				+ ");";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		requery();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//System.out.println("onActivityResult is " + requestCode);
		switch (requestCode) {
			case FILE_SELECT_RESULT: {
				if (resultCode == RESULT_OK) {
					Uri uri = intent.getData();
					//URL url;
					//System.out.println("uri is " + uri);
					String path=getPath(this, uri);
					if(path!=null && path.length()>5) {
						promptFileKind(path);
					}else{
						Auxiliary.warn("Выберите файл из памяти устройства. Невозможно присоединить "+uri,ActivityZapiska.this);
					}
				/*
				// Get the Uri of the selected file 
				Uri uri = data.getData();
				Log.d(TAG, "File Uri: " + uri.toString());
				// Get the path
				String path = FileUtils.getPath(this, uri);
				Log.d(TAG, "File Path: " + path);
				// Get the file instance
				// File file = new File(path);
				// Initiate the upload
				*/
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	public void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless.innerHeight.is(Auxiliary.tapSize * 19));
		OsnovnoiKlientTTChoice = new RedactSingleChoice(this);
		Bough k = Cfg.kontragenty();
		OsnovnoiKlientTTChoice.item("[новый]");
		for (int i = 0; i < k.children.size(); i++) {
			OsnovnoiKlientTTChoice.item(k.children.get(i).child("naimenovanie").value.property.value());
		}
		dataGrid = new DataGrid(this);
		columnKind = new ColumnText();
		columnPath = new ColumnText();
		layoutless.field(this, 0, "№", new Decor(this).labelText.is(nomer).labelAlignLeftCenter());
		layoutless.field(this, 1, "Дата", new RedactDate(this).date.is(data).format.is("dd.MM.yyyy"));
		layoutless.field(this, 2, "Контрагент", OsnovnoiKlientTTChoice.selection.is(OsnovnoiKlientTT));
		layoutless.field(this, 3, "Наименование", new RedactText(this).text.is(naimenovanie));
		layoutless.field(this, 4, "Тип оплаты", new RedactSingleChoice(this).item("наличный").item("безналичный").selection.is(nalbeznal));
		//layoutless.field(this, 5, "Причина", new RedactText(this).text.is(prichina));
		layoutless.field(this, 5, "Причина", new RedactSingleChoice(this).selection.is(prichinaN)//
				.item(prichina_0)//
				.item(prichina_1)//
				.item(prichina_2)//
				.item(prichina_3)//
		);
		layoutless.field(this, 6, "Форма договора", new RedactSingleChoice(this).item("официальная наша").item("клиентская").selection.is(nashklient));
		layoutless.field(this, 7, "Товарооборот", new RedactText(this).text.is(tovarooborot));
		layoutless.field(this, 8, "Лимит", new RedactText(this).text.is(limity));
		layoutless.field(this, 9, "Отсрочка", new RedactText(this).text.is(otsrochka));
		layoutless.field(this, 10, "Бонусы", new RedactText(this).text.is(bonusy));
		layoutless.field(this, 11, "Мин. размер заказа", new RedactText(this).text.is(minrazmerzakaza));
		layoutless.field(this, 12, "Поручительство на сумму", new RedactText(this).text.is(poruchitelstvo));
		layoutless.field(this, 13, "Прочее", new RedactText(this).text.is(prochee));
		layoutless.child(dataGrid//
				//.noHead.is(true)//
				.columns(new Column[]{columnKind.title.is("вид").width.is(300) //
						, columnPath.title.is("путь").width.is(500) //
				})//
				.left().is(layoutless.shiftX.property.plus(layoutless.width().property.multiply(0.3).plus(0.1 * Auxiliary.tapSize)))//
				.top().is(layoutless.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * 14 * Auxiliary.tapSize))//
				.width().is(layoutless.width().property.multiply(0.6))//
				.height().is(Auxiliary.tapSize * 7)//
		);
	}
}
