package sweetlife.android10.supervisor;

import java.io.File;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;

import android.content.*;

import java.net.URLEncoder;
import java.util.*;

import reactive.ui.*;
import tee.binding.task.*;
import tee.binding.it.*;

import java.util.regex.*;

public class ReportRealizacii extends Report_Base{
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();

	public static String menuLabel(){
		return "Реализации товаров";
	}

	public static String folderKey(){
		return "realizaciiTovarow";
	}

	public String getMenuLabel(){
		return "Реализации товаров";
	}

	public String getFolderKey(){
		return "realizaciiTovarow";
	}

	public ReportRealizacii(ActivityWebServicesReports p){
		super(p);
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("from").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("to").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		}catch(Throwable t){
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			int i = (int)Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return "" + s;
		}catch(Throwable t){
			//
		}
		return "?";
	}

	@Override
	public void readForm(String instanceKey){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		try{
			b = Bough.parseXML(xml);
		}catch(Throwable t){
			//
		}
		if(b == null){
			b = new Bough();
		}
		dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("from").value.is("" + (d));
		b.child("to").value.is("" + (d));
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind){
		//https://service.swlife.ru/hrc120107/hs/Report/ВаловаяПрибыль/hrc600?param={"КлючВарианта":"1e5cb9fe-9853-4733-9973-a9064bb4a9d0","ДатаНачала":"20240521","ДатаОкончания":"20240521"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String p = "{\"КлючВарианта\":\"1e5cb9fe-9853-4733-9973-a9064bb4a9d0\""
				+ ",\"ДатаНачала\":\"" + Cfg.formatMills(dateFrom.value(), "yyyyMMdd") + "\""//
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateTo.value(), "yyyyMMdd") + "\""//
				;
		p = p + "}";
		String e = "";
		try{
			e = URLEncoder.encode(p, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "СтатусыЗаказов";
		try{
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/ВаловаяПрибыль/"//
				+ hrc//
				+ "?param=" + e//
				;
		System.out.println(q);
		q = q + tagForFormat(queryKind);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for(int i = 0; i < Cfg.territory().children.size(); i++){
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							long d = new Date().getTime();
							dateFrom.value((double)d);
							dateTo.value((double)d);
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
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
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
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							activityReports.promptDeleteRepoort(ReportRealizacii.this, currentKey);
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
	public String interceptActions(String s){
		String[] strings = s.split("\n");
		for(int i = 0; i < strings.length; i++){
			Pattern pattern = Pattern.compile("([\\s\\S]*)>(\\d\\d-\\d*) от (\\d\\d.\\d\\d.\\d\\d\\d\\d) \\d+:\\d+:\\d+, арт.(\\S*) ([\\s\\S]*)<([\\s\\S]*)");
			Matcher matcher = pattern.matcher(strings[i]);
			//System.out.println(matcher.groupCount() + ": " + strings[i]);
			if(matcher.matches()){
				String start = matcher.group(1) + ">";
				String doc = matcher.group(2);
				String date = matcher.group(3);
				String art = matcher.group(4);
				String name = matcher.group(5);
				String end = "<" + matcher.group(6);
				strings[i] = start
						+ "<a href=\"hook?kind=" + HOOKArtDocDateSent
						+ "&" + FIELDDocumentNumber + "=" + doc
						+ "&" + FIELDShipDate + "=" + date
						+ "&" + FIELDArtikul + "=" + art
						+ "\" >" + art + "</a>: " + name
						+ end;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}
