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

public class ReportNSK extends Report_Base {

	Numeric queryDate = new Numeric();

	public ReportNSK(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "НСК";
	}

	public static String folderKey() {
		return "nsk";
	}

	public String getMenuLabel() {
		return "НСК";
	}

	public String getFolderKey() {
		return "nsk";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String q = Cfg.formatMills(Numeric.string2double(b.child("queryDate").value.property.value()), "dd.MM.yyyy");
			return q;
		} catch(Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key) {
		return " ";
	}

	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		try {
			b = Bough.parseXML(xml);
		} catch(Throwable t) {
			//
		}
		if(b == null) {
			b = new Bough();
		}
		queryDate.value(Numeric.string2double(b.child("queryDate").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("queryDate").value.is("" + queryDate.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("queryDate").value.is("" + new Date().getTime());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {

		String p = "{\"Дата\":\"" + Cfg.formatMills(queryDate.value(), "yyyyMMdd") + "\",\"ВариантОтчета\":\"ДляМП\"}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName="НСК";
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
		if(propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Дата", new RedactDate(context).date.is(queryDate).format.is("dd.MM.yyyy"))//
			;
			propertiesForm.child(new Knob(context)//
					                     .labelText.is("На сегодня")//
					                     .afterTap.is(new Task() {
						@Override
						public void doTask() {
							long d = new Date().getTime();
							queryDate.value((double) d);
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
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportNSK.this, currentKey);
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