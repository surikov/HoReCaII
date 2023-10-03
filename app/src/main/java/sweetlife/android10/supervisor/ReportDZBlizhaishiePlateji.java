package sweetlife.android10.supervisor;

import java.io.File;
import java.util.Date;

import reactive.ui.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.*;


import java.net.URLEncoder;

import sweetlife.android10.Settings;

public class ReportDZBlizhaishiePlateji extends Report_Base {

	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	//Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public ReportDZBlizhaishiePlateji(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "ДЗ ближайшие платежи";
	}

	public static String folderKey() {
		return "dzBlizhaishiePlateji";
	}

	public String getMenuLabel() {
		return "ДЗ ближайшие платежи";
	}

	public String getFolderKey() {
		return "dzBlizhaishiePlateji";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("dateFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("dateTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		} catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key) {
		try {
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
			Bough b = Bough.parseXML(xml);
			Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
			/*Bough kontragenty = Cfg.kontragenty();
			if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
				kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
			}*/
			int nn = (int) Numeric.string2double(b.child("who").value.property.value());
			//whoPlus1.value().intValue();
			if (nn >= 0 && nn < kontragenty.children.size()) {

			} else {
				nn = 0;
			}
			String name = kontragenty.children.get(nn).child("naimenovanie").value.property.value();
			//System.out.println("---getOtherDescription "+whoPlus1.value());
			return name;
		} catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		try {
			b = Bough.parseXML(xml);
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		dateFrom.value(Numeric.string2double(b.child("dateFrom").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("dateTo").value.property.value()));
		//territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		//System.out.println("---readForm "+whoPlus1.value());
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("dateFrom").value.is("" + dateFrom.value());
		b.child("dateTo").value.is("" + dateTo.value());
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Date mb = new Date();
		mb.setDate(1);
		b.child("dateFrom").value.is("" + mb.getTime());
		d = new Date().getTime();
		b.child("dateTo").value.is("" + d);
		b.child("who").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {
		/*
		https://service.swlife.ru/hrc120107/hs/Report/ВзаиморасчетыСПокупателями/hrc29
		?param={"ВариантОтчета":"БлижайшиеПлатежи","ДатаНачала":"20230701","ДатаОкончания":"20230709","Контрагент":"20151"}
		*/
		Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
		String kontragent = "";

		int nn = whoPlus1.value().intValue();
		if (nn > 0) {
			nn = nn - 1;
			nn = (nn < 0 || nn >= kontragenty.children.size()) ? 0 : nn;
			String kod = kontragenty.children.get(nn).child("kod").value.property.value();
			kontragent = ",\"Контрагент\":\"" + kod + "\"";
		}
		String p = "{"
				+ "\"ДатаНачала\":\"" + Cfg.formatMills(dateFrom.value(), "yyyyMMdd") + "\""//
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateTo.value(), "yyyyMMdd") + "\""//
				//+",\"ВариантОтчета\":\"ВзаиморасчетыСПокупателями\""//
				+ ",\"ВариантОтчета\":\"БлижайшиеПлатежи\""//
				//+ ",\"Контрагент\":\"" + kod + "\""//
				+ kontragent
				+ "}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "ВзаиморасчетыСПокупателями";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			serviceName = t.getMessage();
		}

		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" + Cfg.whoCheckListOwner()//
				+ "?param=" + e//
				;


		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
			kontr.selection.is(whoPlus1);
			Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
			kontr.item("[Все контрагенты]");
			for (int i = 0; i < kontragenty.children.size(); i++) {
				kontr.item(kontragenty.children.get(i).child("kod").value.property.value()
						+ ": " + kontragenty.children.get(i).child("naimenovanie").value.property.value());
			}


			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Дата от", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "до", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Контрагент", kontr, Auxiliary.tapSize * 9)//
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							long d = new Date().getTime();
							Date mb = new Date();
							mb.setDate(1);
							dateFrom.value((float) mb.getTime());

							float dd = new Date().getTime();
							dateTo.value(dd);
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4 + 0.5)))//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							activityReports.promptDeleteRepoort(ReportDZBlizhaishiePlateji.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
}
