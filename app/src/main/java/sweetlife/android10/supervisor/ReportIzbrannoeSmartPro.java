package sweetlife.android10.supervisor;

import android.content.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import reactive.ui.*;
import sweetlife.android10.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;


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

public class ReportIzbrannoeSmartPro extends Report_Base{
	Numeric whoPlus1 = new Numeric();
	Numeric territory = new Numeric();

	public static String menuLabel(){
		return "Избранное SmartPro";
	}

	public static String folderKey(){
		return "izbrannoeSmartPro";
	}

	public String getMenuLabel(){
		return "Избранное SmartPro";
	}

	public String getFolderKey(){
		return "izbrannoeSmartPro";
	}

	public ReportIzbrannoeSmartPro(ActivityWebServicesReports p){
		super(p);
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		String kontr = "все контрагенты";
		try{
			b = Bough.parseXML(xml);

			int wh = (int)Numeric.string2double(b.child("who").value.property.value());
			if(wh > 0 && wh <= Cfg.kontragenty().children.size()){
				kontr = Cfg.kontragenty().children.get(wh - 1).child("naimenovanie").value.property.value();
			}
		}catch(Throwable t){
			//
		}
		return kontr;
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
		//dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		//dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		b.child("who").value.is("" + whoPlus1.value());
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
		//https://service.swlife.ru/hrc120107/hs/Report/ИзбранноеПорталСмартПро/hrc29?param={"КлючВарианта":"afba089c-b0de-4fa7-b8b1-55c16dd21eeb"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String q = Settings.getInstance().getBaseURL()//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/ИзбранноеПорталСмартПро/"//
				+ hrc//
				+ "?param={\"КлючВарианта\":\"afba089c-b0de-4fa7-b8b1-55c16dd21eeb\"";


		int nn = whoPlus1.value().intValue() - 1;
		if(nn >= 0 && nn < Cfg.kontragenty().children.size()){
			q = q + ", \"Контрагент\":\"" + Cfg.kontragenty().children.get(nn).child("kod").value.property.value() + "\"";
		}
		q = q + "}";
		System.out.println(q);
		q = q + tagForFormat(queryKind);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){
			propertiesForm = new SubLayoutless(context);
			RedactFilteredSingleChoice kontragent = new RedactFilteredSingleChoice(context);

			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for(int i = 0; i < Cfg.kontragenty().children.size(); i++){
				kontragent.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()
						+ ": " + Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for(int i = 0; i < Cfg.territory().children.size(); i++){
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Контрагент", kontragent)//
					.input(context, 2, Auxiliary.tapSize * 0.3, "Территория", terr)//
			;
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
							activityReports.promptDeleteRepoort(ReportIzbrannoeSmartPro.this, currentKey);
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
		String kfilter = "(.+)Код.(\\d+).(\\d+)(.+)";
		Pattern kpattern = Pattern.compile(kfilter);
		//String artFilter = "(<TD CLASS=\"R9C0\">)(\\d+)(<\\/TD>)";
		String artFilter = "(<TD CLASS=\"R.+>)(\\d+)(<\\/TD>)";
		Pattern artPattern = Pattern.compile(artFilter);
		//String artFilter2 = "(<TD CLASS=\"R6C0\">)(\\d+)(<\\/TD>)";
		//Pattern artPattern2 = Pattern.compile(artFilter2);
		String preCliendCode = "";
		for(int ii = 0; ii < strings.length; ii++){
			String line = strings[ii].trim();
			Matcher kmatcher = kpattern.matcher(line);
			boolean xsts = kmatcher.matches();
			//System.out.println(xsts+" -> "+line);
			if(xsts){
				preCliendCode = kmatcher.group(2) + kmatcher.group(3);
				line = kmatcher.group(1)
						+ "<a href=\"hook?kind=" + HOOKArtFavSmartPro
						+ "&" + FIELDKlientKod + "=" + preCliendCode + "\" >"
						+ preCliendCode + "</a>"
						+ kmatcher.group(4);
				strings[ii] = line;
			}else{
				Matcher artMatcher = artPattern.matcher(line);
				boolean artXsts = artMatcher.matches();
				if(artXsts){
					String artnum = artMatcher.group(2);
					line = artMatcher.group(1)
							+ "<a href=\"hook?kind=" + HOOKDeleteArtFavSmartPro
							+ "&" + FIELDArtikul + "=" + artnum
							+ "&" + FIELDKlientKod + "=" + preCliendCode + "\" >"
							+ artnum + "</a>"
							+ artMatcher.group(3);
					strings[ii] = line;
				} /* else{
					Matcher artMatcher2 = artPattern2.matcher(line);
					boolean artXsts2 = artMatcher2.matches();
					if(artXsts2){
						String artnum2 = artMatcher2.group(2);
						line = artMatcher2.group(1)
								+ "<a href=\"hook?kind=" + HOOKDeleteArtFavSmartPro
								+ "&" + FIELDArtikul + "=" + artnum2
								+ "&" + FIELDKlientKod + "=" + preCliendCode + "\" >"
								+ artnum2 + "</a>"
								+ artMatcher2.group(3);
						strings[ii] = line;
					}
				}*/
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}

