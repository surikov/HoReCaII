
package sweetlife.android10.supervisor;

import android.content.*;

import java.util.*;

import reactive.ui.*;

import sweetlife.android10.Settings;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

public class ReportZayavkiNaDobavlenieVmatricu extends Report_Base {
	//Numeric dateFrom = new Numeric();
	//Numeric dateTo = new Numeric();
	//Numeric dateShip = new Numeric();
	Numeric territory = new Numeric();

	public ReportZayavkiNaDobavlenieVmatricu(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "Заявки на добавление в матрицу";
	}

	public static String folderKey() {
		return "zayavkiNaDobavlenieVmatricu";
	}


	public String getMenuLabel() {
		return "Заявки на добавление в матрицу";
	}

	public String getFolderKey() {
		return "zayavkiNaDobavlenieVmatricu";
	}

	@Override
	public String getShortDescription(String key) {
		/*Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String d = Cfg.formatMills(Numeric.string2double(b.child("ship").value.property.value()), "dd.MM.yyyy");
			return d;
		} catch (Throwable t) {
			//
		}*/
		return "";
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
		//System.out.println("readForm " + instanceKey);
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
		//dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		//dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		//dateShip.value(Numeric.string2double(b.child("ship").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//b.child("from").value.is("" + dateFrom.value());
		//b.child("to").value.is("" + dateTo.value());
		//b.child("ship").value.is("" + dateShip.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		//b.child("from").value.is("" + (d - 30 * 24 * 60 * 60 * 1000.0));
		//b.child("to").value.is("" + (d));
		//b.child("ship").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println(xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind) {
		String hrc = Cfg.whoCheckListOwner();
		//hrc = "region1";
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/Report/УтверждениеЗаявокНаДобавлениеКлиентовВМатрицу/" + hrc;
		return url;
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
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					//.input(context, 1, Auxiliary.tapSize * 0.3, "Дата отгрузки", new RedactDate(context).date.is(dateShip).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "Территория", terr)//
					/*.input(context, 2, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
						@Override
						public void doTask() {
							expectRequery.start(activityReports);
						}
					}), Auxiliary.tapSize * 3)*/
			;
			/*propertiesForm.child(new Knob(context)//
					.labelText.is("На завтра")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {

							long d = new Date().getTime();

							dateShip.value(d + 1 * 24 * 60 * 60 * 1000.0);

							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);*/
			propertiesForm.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {

							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportZayavkiNaDobavlenieVmatricu.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 2 + 0.5)))//
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
				if (num.length() > 2 ){
					int start = line.indexOf('№');
					int end = line.indexOf("<", start + 1);
						line = line.substring(0, start)//
								+ "№<a href=\"hook"//
								+ "?kind=" + HookZayavkiNaDobavlenieVmatricu//
								+ "&" + FIELDDocumentNumber + "=" + num //
								+ "\">" + num + "</a>"//
								+ line.substring(end);
						System.out.println("url line: "+line);
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
