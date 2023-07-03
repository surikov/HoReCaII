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

public class ReportProdajiFlagmanov extends Report_Base {

	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public ReportProdajiFlagmanov(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "Продажи флагманов";
	}

	public static String folderKey() {
		return "prodajiFlagmanov";
	}

	public String getMenuLabel() {
		return "Продажи флагманов";
	}

	public String getFolderKey() {
		return "prodajiFlagmanov";
	}

	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("dateFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("dateTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		} catch(Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key) {
		/*try {
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
			Bough b = Bough.parseXML(xml);
			Bough kontragenty= Cfg.kontragenty();
			if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
				kontragenty= Cfg.kontragenty(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
			}
			int nn=(int)Numeric.string2double(b.child("who").value.property.value());
					//whoPlus1.value().intValue();
			if(nn >= 0 && nn < kontragenty.children.size()) {

			}else{
				nn=0;
			}
			String name=kontragenty.children.get(nn).child("naimenovanie").value.property.value();
			//System.out.println("---getOtherDescription "+whoPlus1.value());
			return name;
		} catch(Throwable t) {
			//
		}*/
		return "";
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
		dateFrom.value(Numeric.string2double(b.child("dateFrom").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("dateTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		//System.out.println("---readForm "+whoPlus1.value());
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("dateFrom").value.is("" + dateFrom.value());
		b.child("dateTo").value.is("" + dateTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Date mb = new Date();
		mb.setDate(1);
		b.child("dateFrom").value.is("" + mb.getTime());
		d = new Date().getTime();
		//b.child("dateFrom").value.is("" + d);
		b.child("dateTo").value.is("" + d);
		//b.child("shipFrom").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		//b.child("shipTo").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		b.child("who").value.is("0");
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
		Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
		/*Bough kontragenty = Cfg.kontragenty();
		if(ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
			kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
		}*/
		int nn = whoPlus1.value().intValue() - 1;
		//nn=(nn < 0 || nn >= kontragenty.children.size())?0:nn;
		String kod = "";
		if(nn < 0 || nn >= kontragenty.children.size()) {
			kod = "";
		}
		else {
			kod = ",\"Контрагент\":\"" + kontragenty.children.get(nn).child("kod").value.property.value() + "\"";
		}
		//http://89.109.7.162/hrc120107/hs/Report/ПродажиФлагманов/supermoscen_hrc?param={"ВариантОтчета":"ПродажиФлагманов","ДатаНач":"20190226","ДатаКон":"20190226"}
		String p = "{" //
				           + "\"ДатаНачала\":\"" + Cfg.formatMills(dateFrom.value(), "yyyyMMdd") + "\""//
				           + ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateTo.value(), "yyyyMMdd") + "\""//
				           + ",\"ВариантОтчета\":\"ПродажиФлагмановПланшет\""//ПродажиФлагманов
				           //+",\"Контрагент\":\""+kod+"\""//
				           + kod//
				           + "}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "ПродажиФлагманов";
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
				           + hrc + "?param=" + e//
				;
		//q="http://testservice.swlife.ru/lipuzhin_hrc/hs/Report/ПродажиФлагманов/zentr?param={\"ВариантОтчета\":\"ПродажиФлагманов\",\"ДатаНачала\":\"20201201\",\"ДатаОкончания\":\"20201203\"}";
		//q="https://testservice.swlife.ru/lipuzhin_hrc/hs/Report/ПродажиФлагманов/zentr?param="+e;
		/*q = "https://testservice.swlife.ru/lipuzhin_hrc/hs/Report/"//
				+ serviceName + "/" //+ Cfg.currentHRC()//
				+ hrc + "?param=" + e//
		;*/
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
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
			kontr.selection.is(whoPlus1);
			kontr.item("[Все контрагенты]");
			Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
			/*Bough kontragenty = Cfg.kontragenty();
			if(ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
				kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
			}*/
			for(int i = 0; i < kontragenty.children.size(); i++) {
				kontr.item(kontragenty.children.get(i).child("kod").value.property.value()
				+": "+kontragenty.children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Дата от", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "до", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "контрагент", kontr, Auxiliary.tapSize * 9)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "территория", terr)//
			;
			propertiesForm.child(new Knob(context)//
					                     .labelText.is("На сегодня")//
					                     .afterTap.is(new Task() {
						@Override
						public void doTask() {
							long d = new Date().getTime();
							Date mb = new Date();
							mb.setDate(1);
							dateFrom.value((float) mb.getTime());
							float dd = new Date().getTime();
							dateTo.value(dd);
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
							activityReports.promptDeleteRepoort(ReportProdajiFlagmanov.this, currentKey);
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