
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

public class ReportObratnaiaSvyazPoDogovoram extends Report_Base{
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();

	public static String menuLabel(){
		return "Обратная связь по договорам";
	}

	public static String folderKey(){
		return "obratnaiaSvyazPoDogovoram";
	}

	public String getMenuLabel(){
		return "Обратная связь по договорам";
	}

	public String getFolderKey(){
		return "obratnaiaSvyazPoDogovoram";
	}

	public ReportObratnaiaSvyazPoDogovoram(ActivityWebServicesReports p){
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
		//выама https://service.swlife.ru/hrc120107/hs/Report/ОбратнаяСвязьОтКлиента/hrc632?param={"ВариантОтчета":"ПерепискаДО","ДатаНачала":"20241201","ДатаОкончания":"20241229"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String p = "{\"ВариантОтчета\":\"ПерепискаДО\""
				+ ",\"ДатаНачала\":\"" + Cfg.formatMills(dateFrom.value(), "yyyyMMdd") + "\""//
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateTo.value(), "yyyyMMdd") + "\""//
				;
		p = p + "}";
		//https://service.swlife.ru/hrc120107/hs/Report/ОбратнаяСвязьОтКлиента/supertula2_hrc?param={"КлючВарианта":"89ef59e8-d6bb-47ad-a787-379c10f5422b","ДатаНачала":"20230420","ДатаОкончания":"20250420"}
		String q = Settings.getInstance().getBaseURL()//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/ОбратнаяСвязьОтКлиента/"//
				+ hrc//
				+ "?param=" + p;
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
							Calendar cc=Calendar.getInstance();
							cc.add(Calendar.MONTH,-1);
							long from=cc.getTimeInMillis();
							dateFrom.value((double)from);
							long to = new Date().getTime();
							dateTo.value((double)to);
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
							activityReports.promptDeleteRepoort(ReportObratnaiaSvyazPoDogovoram.this, currentKey);
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
			//Pattern pattern = Pattern.compile("([\\s\\S]*)>(\\d\\d-\\d*) от (\\d\\d.\\d\\d.\\d\\d\\d\\d) \\d+:\\d+:\\d+, арт.(\\S*) ([\\s\\S]*)<([\\s\\S]*)");
			Pattern pattern = Pattern.compile("([\\s\\S]*)№ (\\d\\d-\\d*)([\\s\\S]*)");
			Matcher matcher = pattern.matcher(strings[i]);
			//System.out.println(matcher.groupCount() + ": " + strings[i]);
			if(matcher.matches()){
				String start = matcher.group(1) ;
				String doc = matcher.group(2);
				String end =  matcher.group(3);
				String newString= start
						+ "<a href=\"hook?kind=" + HOOKObratnayaSviaz
						+ "&" + FIELDDocumentNumber + "=" + doc
						+ "\" >№" + doc + "</a>"
						+ end;
				System.out.println(newString );
				System.out.println( strings[i]);
				//System.out.println(start);
				//System.out.println(doc);
				//System.out.println(end);
				strings[i]=newString;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}
