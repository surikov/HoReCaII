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

public class ReportKontragentyPortalaSmart extends Report_Base {

	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();

	public ReportKontragentyPortalaSmart(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {//list
		return "Контрагенты портала Smart";
	}

	public static String folderKey() {
		return "kontragentysmart";
	}

	public String getMenuLabel() {//form
		return "Контрагенты портала Smart";
	}

	public String getFolderKey() {
		return "kontragentysmart";
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
		dateFrom.value(Numeric.string2double(b.child("dateFrom").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("dateTo").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("dateFrom").value.is("" + dateFrom.value());
		b.child("dateTo").value.is("" + dateTo.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("dateFrom").value.is("" + (d - 0));
		b.child("dateTo").value.is("" + (d + 0));
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}


	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {
		//https://service.swlife.ru/hrc120107/hs/Report/КонтрагентыПорталаSmart/hrc661?param={"ВариантОтчета":"КонтрагентыСмарт","ДатаНачала":"20230901","ДатаОкончания":"20230930"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String p = "{\"ДатаНачала\":\"" + Cfg.formatMills(dateFrom.value(), "yyyyMMdd") + "\""//
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateTo.value(), "yyyyMMdd") + "\""//
				+ ",\"ВариантОтчета\":\"КонтрагентыСмарт\"";
		p = p + "}";
		String serviceName = "КонтрагентыПорталаSmart";
		String q = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Report/" + serviceName + "/" + hrc + "?param=" + p;
		q = q + tagForFormat(queryKind);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		propertiesForm = new SubLayoutless(context);
		RedactSingleChoice terr = new RedactSingleChoice(context);
		terr.selection.is(territory);
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			terr.item(s);
		}
		propertiesForm//
				.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
				.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
				.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
				.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
		;
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
						activityReports.promptDeleteRepoort(ReportKontragentyPortalaSmart.this, currentKey);
					}
				})//
				.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
				.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4 + 2 + 0.5)))//
				.width().is(Auxiliary.tapSize * 2.5)//
				.height().is(Auxiliary.tapSize * 0.8)//
		);
		propertiesForm.innerHeight.is(Auxiliary.tapSize * 14);
		return propertiesForm;
	}
}
