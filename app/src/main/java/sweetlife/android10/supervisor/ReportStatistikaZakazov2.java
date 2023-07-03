package sweetlife.android10.supervisor;

import java.io.File;
import java.net.*;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.*;
import reactive.ui.RedactText;
import reactive.ui.SubLayoutless;
import sweetlife.android10.*;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportStatistikaZakazov2 extends Report_Base {

	Numeric dateShipFrom = new Numeric();
	Numeric dateShipTo = new Numeric();
	//Numeric dateCreateFrom = new Numeric();
	//Numeric dateCreateTo = new Numeric();
	//Numeric dateKontragent = new Numeric();
	Note artikul = new Note();
	Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();
	Numeric kind = new Numeric();
	Numeric letuchka = new Numeric();
	String[] kindKeys = new String[]{
			"НеПодтвержденные", "ЧастичноНеПодтвержденные", "ВсеЗаказы"
	};
	String[] kindLabels = new String[]{
			"Неподтвержденные", "Частично неподтвержденные", "Все заказы"
	};

	public ReportStatistikaZakazov2(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {//list
		return "Статистика Заказов";
	}

	public static String folderKey() {
		return "statistikaZakazov2";
	}

	public String getMenuLabel() {//form
		return "Статистика Заказов";
	}

	public String getFolderKey() {
		return "statistikaZakazov2";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			String shfrom = Cfg.formatMills(Numeric.string2double(b.child("shipFrom").value.property.value()), "dd.MM.yyyy");
			String shto = Cfg.formatMills(Numeric.string2double(b.child("shipTo").value.property.value()), "dd.MM.yyyy");
			return shfrom + " - " + shto + " от " + dfrom + " - " + dto;
		} catch(Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String kontr = "все контрагенты";
			int wh = (int) Numeric.string2double(b.child("who").value.property.value());
			if(wh > 0) {
				int nn = wh - 1;
				kontr = ActivityWebServicesReports.naimenovanieKontragenta(nn);
			}
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return kontr + ", " + s;
		} catch(Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		try {
			b = Bough.parseXML(xml);
		} catch(Throwable t) {
			//
		}
		if(b == null) {
			b = new Bough();
		}
		//dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		//dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		dateShipFrom.value(Numeric.string2double(b.child("shipFrom").value.property.value()));
		dateShipTo.value(Numeric.string2double(b.child("shipTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		letuchka.value(Numeric.string2double(b.child("letuchka").value.property.value()));
		kind.value(Numeric.string2double(b.child("kind").value.property.value()));
		artikul.value(b.child("artikul").value.property.value());
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//b.child("docFrom").value.is("" + dateCreateFrom.value());
		//b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("shipFrom").value.is("" + dateShipFrom.value());
		b.child("shipTo").value.is("" + dateShipTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("who").value.is("" + whoPlus1.value());
		b.child("letuchka").value.is("" + letuchka.value());

		b.child("kind").value.is("" + kind.value());
		b.child("artikul").value.is("" + artikul.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("docFrom").value.is("" + (d - 7 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		b.child("shipFrom").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("shipTo").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		b.child("who").value.is("0");
		b.child("letuchka").value.is("0");
		b.child("kind").value.is("0");
		b.child("artikul").value.is("");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	private String kod(int nn) {
		String r = "-123456789";
		if(nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}

	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {

		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();

		String p = "{\"ДатаНачала\":\"" + Cfg.formatMills(dateShipFrom.value(), "yyyyMMdd") + "\""//
				           + ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateShipTo.value(), "yyyyMMdd") + "\""//
				           + ",\"ВариантОтчета\":\"ДляПродаж\"";
		if(whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			p = p + ",\"Контрагент\":\"" + kod(nn) + "\"";
		}
		p = p + ",\"ВариантПодтверждения\":\"" + kindKeys[kind.value().intValue()] + "\"";
		if(artikul.value().trim().length()>1){
			p = p + ",\"Номенклатура\":\"" + artikul.value().trim() + "\"";
		}
		if(letuchka.value()>0){
			p = p + ",\"Летучка\":true" ;
		}
		//p = p + ",\"Летучка\":" + ((letuchka.value())>0?"true":"false");


		p = p + "}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "СтатистикаЗаказов";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				           //+ "GolovaNew"//
						   //+"cehan_hrc"//
				           + Settings.selectedBase1C()//
				           + "/hs/Report/"//
				           + serviceName + "/" //
						   +hrc//
				           //+ Cfg.currentHRC()//
				           + "?param=" + e//
				;
		System.out.println(q);

		q=q+tagForFormat( queryKind);
		//System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		//if (propertiesForm == null) {
		propertiesForm = new SubLayoutless(context);
		RedactSingleChoice terr = new RedactSingleChoice(context);
		terr.selection.is(territory);
		for(int i = 0; i < Cfg.territory().children.size(); i++) {
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			terr.item(s);
		}
		RedactSingleChoice letuchkaControl = new RedactSingleChoice(context);
		letuchkaControl.item("Все заказы");
		letuchkaControl.item("Только летучка");
		letuchkaControl.selection.is(letuchka);

		RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
		kontr.selection.is(whoPlus1);
		kontr.item("[Все контрагенты]");
		for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
			kontr.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()+": "+Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
		}
		RedactSingleChoice kinds = new RedactSingleChoice(context);
		kinds.selection.is(kind);
		kinds.item(kindLabels[0]).item(kindLabels[1]).item(kindLabels[2]);
		propertiesForm//
				.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
				//.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
				//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
				.input(context, 1, Auxiliary.tapSize * 0.3, "Доставка с", new RedactDate(context).date.is(dateShipFrom).format.is("dd.MM.yyyy"))//
				.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateShipTo).format.is("dd.MM.yyyy"))//
				.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
				.input(context, 4, Auxiliary.tapSize * 0.3, "Контрагент", kontr, Auxiliary.tapSize * 9)//
				.input(context, 5, Auxiliary.tapSize * 0.3, "Вариант отчёта", kinds)//
				.input(context, 6, Auxiliary.tapSize * 0.3, "Летучка", letuchkaControl)//
		.input(context, 7, Auxiliary.tapSize * 0.3, "Артикул", new RedactText(context).text.is(artikul))//
			/*
					.input(context, 7, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
						@Override
						public void doTask() {
							expectRequery.start(activityReports);
						}
					}), Auxiliary.tapSize * 3)//
					*/;
			/*propertiesForm.child(new RedactDate(context)//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * 0.3))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize *(0.5+ 1 * 1.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
					);*/
		propertiesForm.child(new Knob(context)//
				                     .labelText.is("На завтра")//
				                     .afterTap.is(new Task() {
					@Override
					public void doTask() {
						long d = new Date().getTime();
						//dateCreateFrom.value(d - 7 * 24 * 60 * 60 * 1000.0);
						//dateCreateTo.value(d + 0 * 24 * 60 * 60 * 1000.0);
						dateShipFrom.value(d + 1 * 24 * 60 * 60 * 1000.0);
						dateShipTo.value(d + 1 * 24 * 60 * 60 * 1000.0);
						expectRequery.start(activityReports);
					}
				})//
				                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
				                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 8 + 0.5)))//
				                     .width().is(Auxiliary.tapSize * 2.5)//
				                     .height().is(Auxiliary.tapSize * 0.8)//
		);
		propertiesForm.child(new Knob(context)//
				                     .labelText.is("Обновить")//
				                     .afterTap.is(new Task() {
					@Override
					public void doTask() {
						expectRequery.start(activityReports);
					}
				})//
				                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
				                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 8 + 1 + 0.5)))//
				                     .width().is(Auxiliary.tapSize * 2.5)//
				                     .height().is(Auxiliary.tapSize * 0.8)//
		);
		propertiesForm.child(new Knob(context)//
				                     .labelText.is("Удалить")//
				                     .afterTap.is(new Task() {
					@Override
					public void doTask() {
						//expectRequery.start(activityReports);
						activityReports.promptDeleteRepoort(ReportStatistikaZakazov2.this, currentKey);
					}
				})//
				                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
				                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 8 + 2 + 0.5)))//
				                     .width().is(Auxiliary.tapSize * 2.5)//
				                     .height().is(Auxiliary.tapSize * 0.8)//
		);
		//}
		propertiesForm.innerHeight.is(Auxiliary.tapSize * 14);
		return propertiesForm;
	}
}
