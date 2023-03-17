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
http://89.109.7.162/GolovaNew/hs/Report/НакладныеНаКонтроле/zentr?param={"variant":"НакладныеНаКонтролеДляПродаж"}
НакладныеНаКонтроле – это название отчета
zentr – это имя пользователя
param – это дополнительные параметры в виде json (не обязательные)
из них зарезервированные параметры:
variant – название варианта настроек
data – дата отчета
datanach – дата начала отчета
datakon – дата окончания отчета
*/
public class ReportNakladnieNaKontrole extends Report_Base {
	Numeric queryDate = new Numeric();
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
			String q = Cfg.formatMills(Numeric.string2double(b.child("queryDate").value.property.value()), "dd.MM.yyyy");
			return q;
		} catch (Throwable t) {
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
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		queryDate.value(Numeric.string2double(b.child("queryDate").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("queryDate").value.is("" + new Date().getTime());
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
		String p = "НакладныеНаКонтроле/" + Cfg.whoCheckListOwner()//
				//+ "?param={\"variant\":\"НакладныеНаКонтролеДляПродаж\"}"//
				+ "?param={\"ВариантОтчета\":\"НакладныеНаКонтролеДляПродаж\"}"//
				;
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			e=t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ e//
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
