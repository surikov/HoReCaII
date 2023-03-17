//ReportAssortimentFlagmanov
package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;
import android.content.Context;

public class ReportAssortimentFlagmanov extends Report_Base {
	Numeric dateRun = new Numeric();
	public static String menuLabel() {
		return "Ассортимент по флагманам";//"Бонусы для ТП";
	}
	public static String folderKey() {
		return "assortimentPoFlagmanam";
	}
	public  String getMenuLabel() {
		return "Ассортимент по флагманам";//"Бонусы для ТП";
	}
	public  String getFolderKey() {
		return "assortimentPoFlagmanam";
	}
	public ReportAssortimentFlagmanov(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dFrom = Cfg.formatMills(Numeric.string2double(b.child("dateRun").value.property.value()), "dd.MM.yyyy");
			return dFrom;
		}
		catch (Throwable t) {
			//
		}
		return "?";
	}
	@Override
	public String getOtherDescription(String key) {

		return "";
	}
	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		try {
			b = Bough.parseXML(xml);
		}
		catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		dateRun.value(Numeric.string2double(b.child("dateRun").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());

		b.child("dateRun").value.is("" + dateRun.value());

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//long d = new Date().getTime();
		Date mb = new Date();
		//mb.setDate(1);
		long t=mb.getTime();//+1000*60*60*24;
		b.child("dateRun").value.is("" + t);
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {
			String p = "{}";
String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName="АссортиментПоФлагманам";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			serviceName=t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				           //+ "GolovaNew"//
				           +Settings.selectedBase1C()//
				           + "/hs/Report/"//
				           + serviceName+"/" + Cfg.whoCheckListOwner()//
				           + "?param=" + e//
				;
		q=q+tagForFormat( queryKind);
		//System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
	propertiesForm.child(new Knob(context)//
			.labelText.is("Удалить")//
			.afterTap.is(new Task() {
				@Override
				public void doTask() {
					//expectRequery.start(activityReports);
					activityReports.promptDeleteRepoort(ReportAssortimentFlagmanov.this, currentKey);
				}
			})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 1 +1+ 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

}
