package sweetlife.android10.supervisor;

import android.content.*;

import java.io.*;
import java.net.*;
import java.util.*;

import reactive.ui.*;
import sweetlife.android10.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class ReportMelkieZakazy extends Report_Base{
	Numeric dateShipTo = new Numeric();
	Numeric territory = new Numeric();

	public ReportMelkieZakazy(ActivityWebServicesReports p){
		super(p);
	}

	public static String menuLabel(){//list
		return "Мелкие заказы";
	}

	public static String folderKey(){
		return "melkieZakazy";
	}

	public String getMenuLabel(){//form
		return "Мелкие заказы";
	}

	public String getFolderKey(){
		return "melkieZakazy";
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String shto = Cfg.formatMills(Numeric.string2double(b.child("shipTo").value.property.value()), "dd.MM.yyyy");
			return shto;
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
			return s;
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
		dateShipTo.value(Numeric.string2double(b.child("shipTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		b.child("shipTo").value.is("" + dateShipTo.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("shipTo").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind){

		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String ship = Cfg.formatMills(dateShipTo.value(), "yyyyMMdd");
		//https://service.swlife.ru/hrc120107/hs/Report/АнализМелкихЗаказов/supermos2_hrc?param={"ВариантОтчета":"МелкиеЗаказы","ДатаОтгрузки":"20250227"}
		String url = Settings.getInstance().getBaseURL()
				+ Settings.selectedBase1C()
				+ "/hs/Report/"
				+ "АнализМелкихЗаказов"
				+ "/" + hrc
				+ "?param={\"ВариантОтчета\":\"МелкиеЗаказы\",\"ДатаОтгрузки\":\"" + ship + "\"}";
		return url;
	}

	@Override
	public SubLayoutless getParametersView(Context context){
		propertiesForm = new SubLayoutless(context);
		RedactSingleChoice terr = new RedactSingleChoice(context);
		terr.selection.is(territory);
		for(int i = 0; i < Cfg.territory().children.size(); i++){
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			terr.item(s);
		}
		propertiesForm//
				.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)
				.input(context, 1, Auxiliary.tapSize * 0.3, "Дата доставка", new RedactDate(context).date.is(dateShipTo).format.is("dd.MM.yyyy"))
				.input(context, 2, Auxiliary.tapSize * 0.3, "Территория", terr)
		;

		propertiesForm.child(new Knob(context)//
				.labelText.is("На завтра")//
				.afterTap.is(new Task(){
					@Override
					public void doTask(){
						long d = new Date().getTime();
						dateShipTo.value(d + 1 * 24 * 60 * 60 * 1000.0);
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
						//expectRequery.start(activityReports);
						activityReports.promptDeleteRepoort(ReportMelkieZakazy.this, currentKey);
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
