package sweetlife.android10.supervisor;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.RedactFilteredSingleChoice;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.content.*;

import java.util.*;

import reactive.ui.*;

import java.net.*;

import sweetlife.android10.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

public class ReportVozvratyPoPodrazelen extends Report_Base {
	Numeric docFrom = new Numeric();
	Numeric docTo = new Numeric();
	Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel() {
		return "Возвраты по подразделениям";
	}

	public static String folderKey() {
		return "vozvratyPoPodrazelen";
	}

	public String getMenuLabel() {
		return "Возвраты по подразделениям";
	}

	public String getFolderKey() {
		return "vozvratyPoPodrazelen";
	}

	public ReportVozvratyPoPodrazelen(ActivityWebServicesReports p) {
		super(p);
	}

	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return s;
		} catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		} catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		System.out.println("readForm: " + instanceKey + ": " + xml);
		try {
			b = Bough.parseXML(xml);
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		docFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		docTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + docFrom.value());
		b.child("docTo").value.is("" + docTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		System.out.println("writeForm: " + instanceKey + ": " + xml);
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Calendar from = Calendar.getInstance();
		from.set(Calendar.DAY_OF_MONTH, 1);
		b.child("docFrom").value.is("" + from.getTimeInMillis());
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		System.out.println("writeDefaultForm: " + instanceKey + ": " + xml);
	}

	@Override
	public String composeGetQuery(int queryKind) {
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		Calendar from = Calendar.getInstance();
		from.setTimeInMillis(docFrom.value().longValue());
		Calendar to = Calendar.getInstance();
		to.setTimeInMillis(docTo.value().longValue());
		String p = "{\"";
		p = p + "ВариантОтчета\":\"ДляПланшета\"";
		p = p + ",\"ДатаНачала\":\""
				+ Cfg.formatMills(from.getTimeInMillis(), "yyyyMMdd")
				+ "\",\"ДатаОкончания\":\""
				+ Cfg.formatMills(to.getTimeInMillis(), "yyyyMMdd")
				+ "\"";
		if (whoPlus1.value().intValue() > 0) {
			Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
			int nn = whoPlus1.value().intValue() - 1;
			nn = (nn < 0 || nn >= kontragenty.children.size()) ? 0 : nn;
			String kod = kontragenty.children.get(nn).child("kod").value.property.value();
			p = p + ",\"Контрагент\":\"" + kod + "\"";
		}
		p = p + "}";

		//System.out.println(p);

		String e = "";

		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "ВозвратыПоПодразделениямNew";
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
				+ serviceName + "/" + hrc//
				+ "?param=" + e//
				;
		q = q + tagForFormat(queryKind);
		//System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
			kontr.selection.is(whoPlus1);
			Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
			kontr.item("[Все контрагенты]");
			for (int i = 0; i < kontragenty.children.size(); i++) {
				kontr.item(kontragenty.children.get(i).child("kod").value.property.value() + ": " + kontragenty.children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(docFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "до", new RedactDate(context).date.is(docTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Контрагент", kontr, Auxiliary.tapSize * 9)//
			//.input(context, 3, Auxiliary.tapSize * 0.3, "Вариант", new RedactSingleChoice(context).selection.is(modeNew).item("Свод ТП").item("Свод по СТМ"))//
			//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
			/*.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
				@Override
				public void doTask() {
					expectRequery.start(activityReports);
				}
			}), Auxiliary.tapSize * 3)*/
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							Date mb = new Date();
							mb.setDate(1);
							docFrom.value(mb.getTime() - 0 * 24 * 60 * 60 * 1000.0);
							long d = new Date().getTime();
							//dateFrom.value((double) d);
							docTo.value(d - 0 * 24 * 60 * 60 * 1000.0);
							//dateCreateTo.value((double) d);
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportVozvratyPoPodrazelen.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
}
