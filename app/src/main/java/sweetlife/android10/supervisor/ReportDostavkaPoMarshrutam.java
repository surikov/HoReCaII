package sweetlife.android10.supervisor;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

public class ReportDostavkaPoMarshrutam extends Report_Base {

	//Numeric dateCreateFrom = new Numeric();
	//Numeric dateCreateTo = new Numeric();
	Numeric territory = new Numeric();

	public ReportDostavkaPoMarshrutam(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "Маршрутный лист";
	}

	public static String folderKey() {
		return "dostavkaPoMarshrutam";
	}

	public String getMenuLabel() {
		return "Маршрутный лист";
	}

	public String getFolderKey() {
		return "dostavkaPoMarshrutam";
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
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			//String s = ActivityWebServicesReports.territory.children.get(i).child("territory").value.property.value() + " (" + ActivityWebServicesReports.territory.children.get(i).child("hrc").value.property.value().trim() + ")";
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return s;
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
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//b.child("docFrom").value.is("" + dateCreateFrom.value());
		//b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		//b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		//b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
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
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String serviceName = "ЗагрузкаТранспорта";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" //+ Cfg.currentHRC()//
				+ hrc
				+"?empty"
				;
		q=q+tagForFormat( queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}





	@Override
	public SubLayoutless getParametersView(Context context) {
		if(propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for(int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					//.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Территория", terr)//
			/*.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
				@Override
				public void doTask() {
					expectRequery.start(activityReports);
				}
			}), Auxiliary.tapSize * 3)*/;
			/*propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//Date mb = new Date();
							//mb.setDate(1);
							long d = new Date().getTime();
							//dateFrom.value((double) d);
							dateCreateFrom.value(d - 0 * 24 * 60 * 60 * 1000.0);
							dateCreateTo.value((double) d);
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4.5 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);*/
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2.5 + 0 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportDostavkaPoMarshrutam.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2.5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
/*
	@Override
	public String interceptActions(String s) {
		//System.out.println("interceptActions\n-\n-\n-\n-\n-\n\n\n\n\n\n");
		String[] strings = s.split("\n");
		String startWord = "12-";
		String endWord = "от";
		for(int i = 0; i < strings.length; i++) {
			String line = strings[i];
			if(i - 2 > -1 && i + 1 < strings.length - 1) {
				String num = extract(line, startWord, endWord);
				num = num.replace("&nbsp;", "");
				if(num.length() > 2) {
					int start = line.indexOf(startWord);
					int end = line.indexOf(endWord, start + 1);
					//System.out.println("num "+num+" /"+start+" /"+end+"/"+line);
					line = line.substring(0, start)//
							+ "<a href=\"hook"//
							+ "?kind=" + HOOKReportPoVoditelam//
							+ "&" + FIELDDocumentNumber + "=" + num//
							+ "\">" + num + "</a> "//
							+ line.substring(end);
					//System.out.println(line);
					strings[i] = line;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		return sb.toString();
	}*/
}
