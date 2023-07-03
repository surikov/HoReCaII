package sweetlife.android10.supervisor;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import reactive.ui.*;
import sweetlife.android10.Settings;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.*;

/*
https://service.swlife.ru/hrc120107/hs/Report/НакладныеНаКонтроле/region1?param={"ВариантОтчета":"НакладныеНаКонтролеДляПродаж","ДатаНачала":"20230601","ДатаОкончания":"20230615","Подразделение":"00768"}
*/
public class ReportNakladnieNaKontrole extends Report_Base {
	//Numeric queryDate = new Numeric();
	Numeric docFrom = new Numeric();
	Numeric docTo = new Numeric();
	Numeric territory = new Numeric();

	public ReportNakladnieNaKontrole(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "Накладные на контроле";
	}

	public static String folderKey() {
		return "nakladnienakontrole";
	}

	public String getMenuLabel() {
		return "Накладные на контроле";
	}

	public String getFolderKey() {
		return "nakladnienakontrole";
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
				if (i > 0) {
					String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
					return s;
				}
			} catch (Throwable t) {
				//

		}
		return "Все";
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
		//b.child("queryDate").value.is("" + new Date().getTime());
		b.child("docFrom").value.is("" + docFrom.value());
		b.child("docTo").value.is("" + docTo.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Calendar from = Calendar.getInstance();
		from.set(Calendar.DAY_OF_MONTH, 1);
		//from.add(Calendar.DAY_OF_MONTH, -1);
		b.child("docFrom").value.is("" + from.getTimeInMillis());
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
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
		Calendar from = Calendar.getInstance();
		from.setTimeInMillis(docFrom.value().longValue());
		Calendar to = Calendar.getInstance();
		to.setTimeInMillis(docTo.value().longValue());

		String p = "НакладныеНаКонтроле/" + Cfg.whoCheckListOwner()//
				+ "?param={\"ВариантОтчета\":\"НакладныеНаКонтролеДляПродаж\""
				+ ",\"ДатаНачала\":\"" + Cfg.formatMills(from.getTimeInMillis(), "yyyyMMdd") + "\""
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(to.getTimeInMillis(), "yyyyMMdd") + "\"";
		if (territory.value() > 0) {
			String kodPodr = Cfg.kodPodrazdeleni(territory.value().intValue() - 1);
			p = p + ",\"Подразделение\":\"" + kodPodr + "\"";
		}
		p = p + "}"//
		;
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ e//
				;
		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			terr.item("[Все]");
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
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportNakladnieNaKontrole.this, currentKey);
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
