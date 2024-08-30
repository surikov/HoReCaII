//ReportTovarnieNakladmie
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

public class ReportTovarnieNakladmie extends Report_Base{

	Note naklNumber = new Note();
	Numeric kind = new Numeric().value(0);
	Numeric queryDate = new Numeric();

	static Integer tempInitKind = null;
	static String tempInitNum = null;

	public ReportTovarnieNakladmie(ActivityWebServicesReports p){
		super(p);
	}

	public static String menuLabel(){
		return "Печать УПД";
	}

	public static String folderKey(){
		return "tovarnieNakladmie";
	}

	public String getMenuLabel(){
		return "Печать УПД";
	}

	public String getFolderKey(){
		return "tovarnieNakladmie";
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String num = b.child("naklNumber").value.property.value();
			return "№" + num;
		}catch(Throwable t){
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key){
		return "";
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
		naklNumber.value(b.child("naklNumber").value.property.value());
		kind.value(Numeric.string2double(b.child("kind").value.property.value()));
		queryDate.value(Numeric.string2double(b.child("queryDate").value.property.value()));
		/*if(kind.value()==3) {
			Task none=null;
			this.activityReports.brwsr.afterLink.is(none);
		}else{
			this.activityReports.brwsr.afterLink.is(this.activityReports.interceptTask);
		}*/
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		b.child("naklNumber").value.is(naklNumber.value());
		b.child("kind").value.is("" + kind.value());
		b.child("queryDate").value.is("" + queryDate.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		/*if(kind.value()==3) {
			Task none=null;
			this.activityReports.brwsr.afterLink.is(none);
		}else{
			this.activityReports.brwsr.afterLink.is(this.activityReports.interceptTask);
		}*/
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		if(tempInitNum != null)
			b.child("naklNumber").value.is(tempInitNum);
		if(tempInitKind != null)
			b.child("kind").value.is("" + tempInitKind);
		b.child("queryDate").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		tempInitKind = null;
		tempInitNum = null;
	}

	@Override
	public String composeRequest(){
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind){
		String dataNa = Auxiliary.short1cDate.format(new Date(queryDate.value().longValue()));
		String p = "{";
		p = p + "\"НомерНакладной\":\"" + naklNumber.value().trim() + "\"";
		if(queryDate.value() > 0){
			p = p + ",\"ДатаНакладной\":\"" + dataNa + "\"";
		}
		p = p + "}";
		String e = "";
		try{
			e = URLEncoder.encode(p, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "ПечатьПоУмолчанию";
		if(kind.value().intValue() == 1)
			serviceName = "ПечатьТОРГ12";
		if(kind.value().intValue() == 2)
			serviceName = "ПечатьУПД";
		try{
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName
				+ "/" + Cfg.whoCheckListOwner()//
				+ "?param=" + e//
				;

		q = q + tagForFormat(queryKind);

		if(kind.value().intValue() == 3){
			q = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
					//"https://testservice.swlife.ru/shatov"
					+ "/hs/Planshet/GetCertificates/"
					+ naklNumber.value().trim()
					+ "/" + dataNa
					+ "?param=0"
			;
			q = q + tagForFormat(queryKind);
		}

		System.out.println("composeGetQuery " + q);
		return q;
	}
	/*@Override
	public void exportXLS(String to, Context context) {
		System.out.println("exportXLS "+to);
		Auxiliary.warn("Сертификаты можно получить только в .xls или .html",context);
		if(kind.value().intValue() == 3){
			Auxiliary.warn("Сертификаты можно получить только в .xls или .html",context);
		}else{
			super.exportXLS(to,context);
		}
	}*/
	@Override
	public boolean canUseXLS(){
		if(kind.value().intValue() == 3){
			return false;
		}else{
			return true;
		}
	}
	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){
			propertiesForm = new SubLayoutless(context);

			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "№ накладной", new RedactText(context).text.is(naklNumber))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "Дата накладной", new RedactDate(context).date.is(queryDate).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Вид", new RedactSingleChoice(context).selection.is(kind)
							.item("по умолчанию")
							.item("ТОРГ12")
							.item("печать УПД")
							.item("сертификаты")
					)//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 3 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportTovarnieNakladmie.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 4 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

	@Override
	public String interceptActions(String s){
		if(kind.value().intValue() == 3){
			System.out.println("interceptActions start");
			System.out.println(s);
			System.out.println("interceptActions done");
			Bough data = Bough.parseJSON("{data:" + s + "}");
			java.util.Vector<Bough> all = data.children("data");
			String html = "<html>"
					+ "	<head>"
					+ "		<meta charset=\"utf-8\">"
					+ "		<title>...</title>"
					+ "	</head>"
					+ "	<body>"
					+ "		<p>Сертификаты</p>";
			for(int i = 0; i < all.size(); i++){
				String url = all.get(i).value.property.value().trim();
				if(url.length() > 1){
					html = html + "		<p><a href='" + url + "'>" + url + "</a></p>";
					html = html + "		<p><img style='width:50%' src='" + url + "'/></p>";
				}
			}
			html = html + "	</body>"
					+ "	</html>";
			System.out.println(data.dumpXML());
			return html;
		}else{
			return s;
		}
	}
}