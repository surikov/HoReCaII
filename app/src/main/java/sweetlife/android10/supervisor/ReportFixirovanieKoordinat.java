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

public class ReportFixirovanieKoordinat extends Report_Base {
	Numeric docFrom = new Numeric();
	Numeric docTo = new Numeric();
	Numeric territory = new Numeric();


	public static String menuLabel() {
		return "Фиксирование координат";
	}

	public static String folderKey() {
		return "fixkoord";
	}

	public String getMenuLabel() {
		return "Фиксирование координат";
	}

	public String getFolderKey() {
		return "fixkoord";
	}

	public ReportFixirovanieKoordinat(ActivityWebServicesReports p) {
		super(p);
	}

	@Override
	public String getOtherDescription(String key) {
		return " ";
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
		try {
			b = Bough.parseXML(xml);
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		docFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		docTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + docFrom.value());
		b.child("docTo").value.is("" + docTo.value());
		b.child("territory").value.is("" + territory.value());
		//b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Calendar from = Calendar.getInstance();
		//from.set(Calendar.DAY_OF_MONTH, 1);
		from.add(Calendar.DAY_OF_MONTH, -1);
		b.child("docFrom").value.is("" + from.getTimeInMillis());
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		System.out.println("writeDefaultForm: " + instanceKey + ": " + xml);
	}

	@Override
	public String composeGetQuery(int queryKind) {
		Calendar from = Calendar.getInstance();
		from.setTimeInMillis(docFrom.value().longValue());
		Calendar to = Calendar.getInstance();
		to.setTimeInMillis(docTo.value().longValue());
		String hrc=Cfg.whoCheckListOwner();
		if (territory.value() > 0) {
			hrc= Cfg.territory().children.get(territory.value().intValue()).child("hrc").value.property.value();
		}
		String q = Settings.getInstance().getBaseURL()//
				+ Settings.selectedBase1C()//
				+ "/hs/Planshet/OtchetKoordinat/"//
				+ "/" + Cfg.formatMills(from.getTimeInMillis(), "yyyyMMdd")
				+ "/" + Cfg.formatMills(to.getTimeInMillis(), "yyyyMMdd")
				+ "/" + hrc;
		q = q + tagForFormat(queryKind);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			//terr.item("[Все]");
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(docFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "до", new RedactDate(context).date.is(docTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							Date mb = new Date();
							//mb.setDate(1);
							docFrom.value(mb.getTime() - 0 * 24 * 60 * 60 * 1000.0);
							long d = new Date().getTime();
							docTo.value(d - 0 * 24 * 60 * 60 * 1000.0);
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
							activityReports.promptDeleteRepoort(ReportFixirovanieKoordinat.this, currentKey);
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

	@Override
	public String interceptActions(String s) {
		String[] strings = s.split("\n");
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];
			if (i - 2 > -1 && i + 1 < strings.length - 1) {
				String num = extract(line, '№', '<');
				if (num.length() > 2) {
					int start = line.indexOf('№');
					int end = line.indexOf("<", start + 1);
					line = line.substring(0, start)//
							+ "№<a href=\"hook"//
							+ "?kind=" + HOOKReportFixKoordinat//
							+ "&" + FIELDDocumentNumber + "=" + num
							+ "\">" + num + "</a>"//
							+ line.substring(end);
					System.out.println("url line: " + line);
					strings[i] = line;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}
