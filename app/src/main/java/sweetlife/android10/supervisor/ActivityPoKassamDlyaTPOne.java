package sweetlife.android10.supervisor;

import android.app.*;

import java.text.*;
import java.util.Date;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityPoKassamDlyaTPOne extends Activity {
	MenuItem menuSave;
	MenuItem menuDelete;
	MenuItem menuUpload;
	RedactSingleChoice doverennost;
	Layoutless layoutless;
	Numeric tipSelect = new Numeric();
	Numeric vigruzhen = new Numeric();
	Note vigruzhenText = new Note();
	//Note textSumma = new Note();
	Numeric numSumma = new Numeric();
	//Numeric summa = new Numeric();
	Numeric dogovorSelect = new Numeric();
	RedactSingleChoice dogovor;
	Note nDoverennosti = new Note();
	Numeric doverennostKind = new Numeric();
	Note nNakladnoy = new Note();
	Numeric dataNakladnoy = new Numeric();
	Numeric _id = new Numeric();
	String []doverennostKinds=new String []{"ПСЖ","ПИП","ДСЖ","ДИП"};
	
	//RedactSingleChoice klient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.init(this);
		getID(getIntent().getExtras());
		createGUI();
		requery();
	}
	void getID(Bundle bundle) {
		if (bundle != null) {
			String s = bundle.getString("_id");
			if (s != null) {
				double nn = Numeric.string2double(s);
				if (nn > 0) {
					_id.value(nn);
				}
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuSave = menu.add("Сохранить");
		menuDelete = menu.add("Удалить");
		menuUpload = menu.add("Выгрузить");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuSave) {
			doSave();
			return true;
		}
		if (item == menuDelete) {
			doDelete();
			return true;
		}
		if (item == menuUpload) {
			doUpload();
			return true;
		}
		return false;
	}
	void saveDoc() {
		String klientNumber = "-";
		String dogovorNumer = "-";
		if (dogovorSelect.value().intValue() > 0) {
			//klientNumber = Cfg.dogovora().children.get(dogovorSelect.value().intValue() - 1).child("kontragentKod").value.property.value();
			//dogovorNumer = Cfg.dogovora().children.get(dogovorSelect.value().intValue() - 1).child("dogovorKod").value.property.value();
			CfgDogovorInfo dogovor = Cfg.dogovorByNum(dogovorSelect.value().intValue() - 1);
			CfgKontragentInfo kontragent = Cfg.kontragentByNum(dogovorSelect.value().intValue() - 1);
			if (dogovor != null && kontragent != null) {
				klientNumber = kontragent.kod;
				dogovorNumer = dogovor.kod;
			}
		}
		//System.out.println("dogovorSelect.value().intValue()=" + dogovorSelect.value().intValue() + ", dogovorNumer=" + dogovorNumer + ", klientNumber " + klientNumber);
		//String dov = doverennostKind.value().intValue() == 0 ? "ПСЖ" : "ДСЖ";
		/*String dov = "";
		String phormaOplaty = "";
		if (phormaOplaty.equals("0")) {
			if (doverennostKind.value().intValue() == 0) {
				dov = "ПСЖ";
			}
			else {
				dov = "ДСЖ";
			}
		}
		else {
			if (doverennostKind.value().intValue() == 0) {
				dov = "ПИП";
			}
			else {
				dov = "ДИП";
			}
		}*/
		if (_id.value() > 0) {
			String sql = "update PoKassamDlyaTP set"//
					+ "\n		textSumma='" + numSumma.value() + "'"//
					+ "\n		,Klient='" + klientNumber + "'"//
					+ "\n		,Dogovor='" + dogovorNumer + "'"//
					//+ "\n		,Doverennost='" + dov + "'"//
					+ "\n		,Doverennost='" + doverennostKind.value().intValue() + "'"//
					+ "\n		,NomerDoverennosti='" + nDoverennosti.value() + "'"//
					+ "\n		,NomerNuk='" + nNakladnoy.value() + "'"//
					+ "\n		,DataNuk=" + dataNakladnoy.value()//
					+ "\n		,sozdan=" + tipSelect.value().intValue()//
					+ "\n		,vigruzhen=" + vigruzhen.value().longValue()//
					+ "\n	where _id=" + _id.value().intValue()//
			;
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}
		else {
			ContentValues values = new ContentValues();
			values.put("vigruzhen", vigruzhen.value().longValue());
			values.put("textSumma", "" + numSumma.value());
			values.put("Klient", klientNumber);
			values.put("Dogovor", dogovorNumer);
			//values.put("Doverennost", dov);
			values.put("Doverennost", "" + doverennostKind.value().intValue());
			values.put("NomerDoverennosti", nDoverennosti.value());
			values.put("NomerNuk", nNakladnoy.value());
			values.put("DataNuk", dataNakladnoy.value());
			values.put("sozdan", tipSelect.value().intValue());
			_id.value((double) ApplicationHoreca.getInstance().getDataBase().insert("PoKassamDlyaTP", null, values));
		}
	}
	void doSave() {
		if (vigruzhen.value().intValue() > 1) {
			warnUploaded();
			return;
		}
		saveDoc();
		finish();
	}
	void doDelete() {
		if (_id.value() > 0) {
			int nn = vigruzhen.value().intValue();
			if (nn < 1) {
				String sql = "delete from PoKassamDlyaTP"//
						+ "\n	where _id=" + _id.value().intValue()//
				;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				finish();
			}
			else {
				//System.out.println("vigruzhen.value "+nn+"!"+(nn>1));
				warnUploaded();
			}
		}
	}
	void warnUploaded() {
		Auxiliary.warn("Документ уже выгружен", this);
	}
	String doverennostName(String phormaOplaty, int doverennostSel) {
		/*String dov = "";
		if (phormaOplaty.equals("0")) {
			if (doverennostSel == 0) {
				dov = "ПСЖ";
			}
			else {
				dov = "ДСЖ";
			}
		}
		else {
			if (doverennostSel == 0) {
				dov = "ПИП";
			}
			else {
				dov = "ДИП";
			}
		}
		return dov;
		*/
		return doverennostKinds[doverennostSel];
	}
	void doUpload() {
		if (vigruzhen.value().intValue() > 1) {
			warnUploaded();
			return;
		}
		if (numSumma.value() < 1) {
			Auxiliary.warn("Сумма должна быть больше 0", this);
			return;
		}
		if (tipSelect.value().intValue() < 2) {
			if (dogovorSelect.value().intValue() < 1) {
				Auxiliary.warn("Не выбран договор контрагента", this);
				return;
			}else{				
				CfgDogovorInfo dogovor = Cfg.dogovorByNum(dogovorSelect.value().intValue() - 1);
				//System.out.println(dogovor.deleted+"/"+dogovor.naimenovanie+"/"+dogovor.kod);
				if (!dogovor.deleted.equals("00")) {
					Auxiliary.warn("Договор контрагента удалён, выберите другой", this);
					return;
				}
			}
		}

		if (tipSelect.value().intValue() == 0) {
			if (Numeric.string2double(this.nDoverennosti.value()) < 99) {
				Auxiliary.warn("№ доверенности не заполнен (должен содержать только цифры)", this);
				return;
			}
		}
		if (tipSelect.value().intValue() == 1) {
			if (Numeric.string2double(this.nNakladnoy.value()) < 3) {
				Auxiliary.warn("№ накладной не заполнен", this);
				return;
			}
		}
		saveDoc();
		String xml = "";
		String klientNumber = "";
		String dogovorNumer = "";
		String phormaDogovora = "";
		if (dogovorSelect.value().intValue() > 0) {
			//klientNumber = Cfg.dogovora().children.get(dogovorSelect.value().intValue() - 1).child("kontragentKod").value.property.value();
			//dogovorNumer = Cfg.dogovora().children.get(dogovorSelect.value().intValue() - 1).child("dogovorKod").value.property.value();
			CfgDogovorInfo dogovor = Cfg.dogovorByNum(dogovorSelect.value().intValue() - 1);
			CfgKontragentInfo kontragent = Cfg.kontragentByNum(dogovorSelect.value().intValue() - 1);
			if (dogovor != null && kontragent != null) {
				klientNumber = kontragent.kod;
				dogovorNumer = dogovor.kod;
				phormaDogovora = dogovor.phormaDogovora;
			}
		}
		xml = xml + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"//
				+ "\n	<SOAP-ENV:Envelope"// 
				+ "\n			xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\" "//
				+ "\n			xmlns:soap12bind=\"http://schemas.xmlsoap.org/wsdl/soap12/\" "//
				+ "\n			xmlns:soapbind=\"http://schemas.xmlsoap.org/wsdl/soap/\" "//
				+ "\n			xmlns:tns=\"http://ws.swl/Kassa\""// 
				+ "\n			xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""// 
				+ "\n			xmlns:xsd1=\"http://ws.swl/Kassa\""// 
				+ "\n			xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"//
				+ "\n		<SOAP-ENV:Body>"//
				+ "\n			<xsd1:Vigruzit xmlns:xsd1=\"http://ws.swl/Kassa\">"//
				+ "\n				<xsd1:Dok>"//
				+ "\n					<xsd1:Summa>" + this.numSumma.value() + "</xsd1:Summa>"//
		;
		if (tipSelect.value().intValue() == 2) {
			xml = xml //
					+ "\n					<xsd1:Klient/>"//
					+ "\n					<xsd1:Dogovor/>"//
					+ "\n					<xsd1:Doverennost/>"//
					+ "\n					<xsd1:NomerDoverennosti/>"//
					+ "\n					<xsd1:NomerNuk/>"//
					+ "\n					<xsd1:DataNuk/>"//
			;
		}
		else {
			xml = xml //
					+ "\n					<xsd1:Klient>" + klientNumber + "</xsd1:Klient>"//
					+ "\n					<xsd1:Dogovor>" + dogovorNumer + "</xsd1:Dogovor>"//
			;
			if (tipSelect.value().intValue() == 0) {
				//String dov = doverennostKind.value().intValue() == 0 ? "ПСЖ" : "ДСЖ";
				xml = xml //
						+ "\n					<xsd1:Doverennost>" + doverennostName(phormaDogovora, doverennostKind.value().intValue()) + "</xsd1:Doverennost>"
						+ "\n					<xsd1:NomerDoverennosti>"
						+ this.nDoverennosti.value() + "</xsd1:NomerDoverennosti>"//
						+ "\n					<xsd1:NomerNuk/>"//
						+ "\n					<xsd1:DataNuk/>"//
				;
			}
			else {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date d = new Date();
				d.setTime(dataNakladnoy.value().longValue());
				String dataNakl = df.format(d);
				xml = xml //
						+ "\n					<xsd1:Doverennost/>" //
						+ "\n					<xsd1:NomerDoverennosti/>"//
						+ "\n					<xsd1:NomerNuk>" + nNakladnoy.value() + "</xsd1:NomerNuk>"//
						+ "\n					<xsd1:DataNuk>" + dataNakl + "</xsd1:DataNuk>"//
				;
			}
		}
		xml = xml + "\n				</xsd1:Dok>"//
				+ "\n				<xsd1:Otvetstvennii>" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "</xsd1:Otvetstvennii>"//
				+ "\n			</xsd1:Vigruzit>"//
				+ "\n		</SOAP-ENV:Body>"//
				+ "\n	</SOAP-ENV:Envelope>"//
		;
		String url = Settings.getInstance().getBaseURL() + "kassa.1cws";
		System.out.println(url);
		System.out.println(xml);
		Report_Base.startPing();
		final RawSOAP r = new RawSOAP();
		r.url.is(url)//
		.xml.is(xml)//
		.afterError.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(r.exception.property.value().toString(), ActivityPoKassamDlyaTPOne.this);
			}
		}).afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				System.out.println(r.data.dumpXML());
				String answer = r.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
				if (answer.equals("ок")) {
					vigruzhen.value((double) new Date().getTime());
					saveDoc();
				}
				//Auxiliary.warn("Выгружено: " + answer, ActivityPoKassamDlyaTPOne.this);
				//ActivityPoKassamDlyaTPOne.this.finish();
				Auxiliary.pickConfirm(ActivityPoKassamDlyaTPOne.this, "Результат: " + answer,  "Закрыть", new Task(){

					@Override
					public void doTask() {
						ActivityPoKassamDlyaTPOne.this.finish();
						
					}
					
				});
			}
		}).startLater(this, "Выгрузка",Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
	}
	void requery() {
		String sql = "select * from PoKassamDlyaTP where _id=" + _id.value().intValue();
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		numSumma.value(Numeric.string2double(b.child("row").child("textSumma").value.property.value()));
		//numSumma.value(Numeric.string2double(textSumma.value()));
		doverennostKind.value(Numeric.string2double(b.child("row").child("Doverennost").value.property.value()));
		nDoverennosti.value(b.child("row").child("NomerDoverennosti").value.property.value());
		nNakladnoy.value(b.child("row").child("NomerNuk").value.property.value());
		dataNakladnoy.value(Numeric.string2double(b.child("row").child("DataNuk").value.property.value()));
		vigruzhen.value(Numeric.string2double(b.child("row").child("vigruzhen").value.property.value()));
		tipSelect.value(Numeric.string2double(b.child("row").child("sozdan").value.property.value()));
		String s1 = b.child("row").child("Dogovor").value.property.value();
		if (s1.length() > 2) {
			int i = 0;
			for (int k = 0; k < Cfg.crossDogovora().size(); k++) {
				for (int d = 0; d < Cfg.crossDogovora().get(k).dogovora.size(); d++) {
					if (s1.equals(Cfg.crossDogovora().get(k).dogovora.get(d).kod)) {
						dogovorSelect.value(i + 1);
						break;
					}
					i++;
				}
			}
			/*
			Bough k = Cfg.dogovora();
			for (int i = 0; i < k.children.size(); i++) {
				String s2 = k.children.get(i).child("dogovorKod").value.property.value();
				if (s1.equals(s2)) {
					dogovorSelect.value(i + 1);
					break;
				}
			}*/
		}
		if (vigruzhen.value() > 1) {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			Date d = new Date();
			d.setTime(vigruzhen.value().longValue());
			vigruzhenText.value(df.format(d));
		}
		System.out.println("vigruzhen.value " + vigruzhen.value());
	}
	public static String tipDescriptipon(int nn) {
		if (nn == 0) {
			return "приходный кассовый ордер (наличные)";
		}
		if (nn == 1) {
			return "приходный кассовый ордер (товарный чек)";
		}
		return "инкассация";
	}
	void createGUI() {
		if (_id.value() > 0) {
			this.setTitle("Кассовый чек");
		}
		else {
			this.setTitle("Новый кассовый чек");
		}
		layoutless = new Layoutless(this);
		setContentView(layoutless.innerHeight.is(Auxiliary.tapSize * 9));
		dogovor = new RedactSingleChoice(this).selection.is(dogovorSelect);
		dogovor.item("выберите договор контрагента");
		for (int k = 0; k < Cfg.crossDogovora().size(); k++) {
			for (int d = 0; d < Cfg.crossDogovora().get(k).dogovora.size(); d++) {
				//if (Cfg.crossDogovora().get(k).dogovora.get(d).deleted.equals("01")) {
					dogovor.item(//
							
					Cfg.crossDogovora().get(k).naimenovanie + " (" + Cfg.crossDogovora().get(k).dogovora.get(d).naimenovanie//
							//+", "+Cfg.crossDogovora().get(k).dogovora.get(d).phormaDogovora//		
							
							//+ Cfg.crossDogovora().get(k).dogovora.get(d).deleted
							//+(Cfg.crossDogovora().get(k).dogovora.get(d).deleted.equals("00")?"":Cfg.crossDogovora().get(k).dogovora.get(d).deleted.equals("02")?", холдинг":", удалён")
							+(Cfg.crossDogovora().get(k).dogovora.get(d).deleted.equals("00")?"":", удалён")
							+ ")"//
							);
				//}
			}
		}
		/*Bough k = Cfg.dogovora();
		for (int i = 0; i < k.children.size(); i++) {
			dogovor.item(k.children.get(i).child("kontragentNaimenovanie").value.property.value()//
					+ ", " + k.children.get(i).child("dogovorNaimenovanie").value.property.value()//
			);
		}*/
		layoutless.field(this, 0, "тип", new RedactSingleChoice(this)//
				//.item("Приходный кассовый ордер")//
				//.item("Инкссация")//Внутреннее перемещение средств")//
				.item(tipDescriptipon(0))//
				.item(tipDescriptipon(1))//
				.item(tipDescriptipon(2))//
				.selection.is(tipSelect)//
				, 9 * Auxiliary.tapSize);
		layoutless.field(this, 1, "выгружен", new Decor(this).labelText.is(vigruzhenText).labelAlignLeftCenter());
		layoutless.field(this, 2, "сумма", new RedactNumber(this).number.is(numSumma));
		layoutless.field(this, 3, "договор", dogovor.hidden().is(tipSelect.equals(2)), 14 * Auxiliary.tapSize);
		doverennost = new RedactSingleChoice(this)//
				//.item("ПСЖ/ПИП")//
				//.item("ДСЖ/ДИП")//
				.item(doverennostKinds[0])//
				.item(doverennostKinds[1])//
				.item(doverennostKinds[2])//
				.item(doverennostKinds[3])//
		;
		layoutless.field(this, 4, "доверенность", doverennost.selection.is(doverennostKind).hidden().is(tipSelect.equals(0).not()));
		layoutless.field(this, 5, "№ доверенности", new RedactText(this).text.is(nDoverennosti).hidden().is(tipSelect.equals(0).not()));
		layoutless.field(this, 6, "№ накладной", new RedactText(this).text.is(nNakladnoy).hidden().is(tipSelect.equals(1).not()));
		layoutless.field(this, 7, "дата накладной", new RedactDate(this).format.is("dd.MM.yyyy").date.is(dataNakladnoy).hidden().is(tipSelect.equals(1).not()));
	}
}
