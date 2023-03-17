package sweetlife.android10.supervisor;

import java.io.File;
import java.net.*;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.*;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportObjedineniaKlientov extends Report_Base {


	Numeric whoPlus1 = new Numeric();


	public ReportObjedineniaKlientov(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {//list
		return "Объединения клиентов";
	}

	public static String folderKey() {
		return "objedineniaklientov";
	}

	public String getMenuLabel() {//form
		return "Объединения клиентов";
	}

	public String getFolderKey() {
		return "objedineniaklientov";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			//String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			return "";
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
			return kontr;
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

		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));

	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());

		b.child("who").value.is("" + whoPlus1.value());

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());

		b.child("who").value.is("0");

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



		String p = "{\"Контрагент\":\"";
		if(whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			p = p + "" + kod(nn) + "";
		}

p = p +  "\"}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "ОбъединенияКлиентов";
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
						   +Cfg.whoCheckListOwner()//
				           //+ Cfg.currentHRC()//
				           + "?param=" + e//
				;
		q=q+tagForFormat( queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		//if (propertiesForm == null) {
		propertiesForm = new SubLayoutless(context);


		RedactSingleChoice kontr = new RedactSingleChoice(context);
		kontr.selection.is(whoPlus1);
		kontr.item("[Все контрагенты]");
		for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
			kontr.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
		}

		propertiesForm//
				.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//

				.input(context, 1, Auxiliary.tapSize * 0.3, "Контрагент", kontr)//
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
				                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 1 + 1 + 0.5)))//
				                     .width().is(Auxiliary.tapSize * 2.5)//
				                     .height().is(Auxiliary.tapSize * 0.8)//
		);
		propertiesForm.child(new Knob(context)//
				                     .labelText.is("Удалить")//
				                     .afterTap.is(new Task() {
					@Override
					public void doTask() {
						//expectRequery.start(activityReports);
						activityReports.promptDeleteRepoort(ReportObjedineniaKlientov.this, currentKey);
					}
				})//
				                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
				                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 *1 + 2 + 0.5)))//
				                     .width().is(Auxiliary.tapSize * 2.5)//
				                     .height().is(Auxiliary.tapSize * 0.8)//
		);
		//}
		propertiesForm.innerHeight.is(Auxiliary.tapSize * 14);
		return propertiesForm;
	}
}
