
package sweetlife.android10.supervisor;

		import java.io.File;
		import java.net.URLEncoder;
		import java.text.*;
		import java.util.*;

		import reactive.ui.Auxiliary;
		import reactive.ui.*;
		import reactive.ui.Knob;
		import reactive.ui.RedactDate;
		import reactive.ui.RedactSingleChoice;
		import reactive.ui.SubLayoutless;
		import sweetlife.android10.*;
		import tee.binding.Bough;
		import tee.binding.it.*;
		import tee.binding.task.Task;

		import android.content.Context;

public class ReportPoiskKontragentaPoKluchevimPolyam extends Report_Base{
	Note inn = new Note();
	Note name = new Note();
	Note adres = new Note();


	public ReportPoiskKontragentaPoKluchevimPolyam(ActivityWebServicesReports p){
		super(p);
	}

	public static String menuLabel(){
		return "Поиск контрагента по ключевым полям";
	}

	public static String folderKey(){
		return "poiskKontragentaPoKluchevimPolyam";
	}

	public String getMenuLabel(){
		return "Поиск контрагента по ключевым полям";
	}

	public String getFolderKey(){
		return "poiskKontragentaPoKluchevimPolyam";
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String txt = b.child("name").value.property.value();

			return txt;
		}catch(Throwable t){
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String txt = b.child("inn").value.property.value()+" "+b.child("adres").value.property.value();
			return txt;
		}
		catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public void readForm(String instanceKey){
		//System.out.println("readForm-----------------------------------");
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

		name.value(b.child("name").value.property.value());
		inn.value(b.child("inn").value.property.value());
		adres.value(b.child("adres").value.property.value());
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());

		b.child("name").value.is("" + name.value());
		b.child("inn").value.is("" + inn.value());
		b.child("adres").value.is("" + adres.value());

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind){
		String serviceName = "ПоискКонтрагентаПоКлючевымПолям";

		String p = "{\"ИНН\":\"" + inn.value()+ "\""
				+ ",\"Наименование\":\"" + name.value() + "\""
				+ ",\"Адрес\":\"" + adres.value() + "\""
				+ "}";
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName
				+ "/" + Cfg.whoCheckListOwner()//
				+ "?param=" + p//
				;
		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public String composeRequest(){
		return null;
	}


	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){



			propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 19)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Наименование", new RedactText(context).text.is(name))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "ИНН", new RedactText(context).text.is(inn))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Адрес", new RedactText(context).text.is(adres))//
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
					.width().is(Auxiliary.tapSize * 3)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);

			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportPoiskKontragentaPoKluchevimPolyam.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 3)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);


		}

		return propertiesForm;
	}

}

