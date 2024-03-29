
package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.*;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportKontragentySPrilojeniem extends Report_Base{
	Numeric territory = new Numeric();
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric vMatrice = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel(){
		return "Контрагенты с приложением";
	}

	public static String folderKey(){
		return "kontragentySPrilojeniem";
	}

	public String getMenuLabel(){
		return "Контрагенты с приложением";
	}

	public String getFolderKey(){
		return "kontragentySPrilojeniem";
	}

	public ReportKontragentySPrilojeniem(ActivityWebServicesReports p){
		super(p);
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
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
			return s;
		}catch(Throwable t){
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
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		vMatrice.value(Numeric.string2double(b.child("vMatrice").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		b.child("territory").value.is("" + territory.value());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("vMatrice").value.is("" + vMatrice.value());
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();

		b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		b.child("who").value.is("0");
		//b.child("territory").value.is("" + (Cfg.territory().children.size() - 1));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	//http://89.109.7.162/hrc120107/hs/Report/%D0%90%D0%9A%D0%91/hrc229/?param=%7b%22Sravnenie%22:true,%22%D0%94%D0%B0%D1%82%D0%B0%D0%9D%D0%B0%D1%87%D0%B0%D0%BB%D0%B0%22:%2220200414%22,%22%D0%94%D0%B0%D1%82%D0%B0%D0%9E%D0%BA%D0%BE%D0%BD%D1%87%D0%B0%D0%BD%D0%B8%D1%8F%22:%2220200416%22%7d
	//http://89.109.7.162/hrc120107/hs/Report/АКБ/hrc229/?param={"Sravnenie":true,"ДатаНачала":"20200414","ДатаОкончания":"20200416"}
	@Override
	public String composeGetQuery(int queryKind){
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String kon = "";
		if(whoPlus1.value().intValue() > 0){
			int nn = whoPlus1.value().intValue() - 1;
			String rr = "-123456789";
			if(nn >= 0 && nn < Cfg.kontragenty().children.size()){
				rr = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
			}
			kon = ",\"Контрагент\":\"" + rr + "\"";
		}
		String p = "{\"ВМатрице\":" + (vMatrice.value() == 0 ? "\"Истина\"" : "\"Ложь\"")
				+ ",\"ДатаНачала\":\"" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd")
				+ "\",\"ДатаОкончания\":\"" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd")+"\""
				+ kon
				+ "}";
		String e = "";
		try{
			e = URLEncoder.encode(p, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName = "КонтрагентыСПриложением";
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
				+ serviceName + "/" + hrc//
				+ "?param=" + e//
				;
		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public String composeRequest(){
		return null;
		//http://89.109.7.162/shatov/hs/Report/АКБ/hrc229/?param={"Sravnenie":true,"ДатаНач":"20200414","ДатаКон":"20200416","ДатаНачала":"20200414","ДатаОкончания":"20200416"}
        /*int i = territory.value().intValue();
        String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
        String xml = ""//
                + "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
                + "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
                + "\n			<soap:Body>"//
                + "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
                + "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
                + "АКБПоТП"//
                + "</m:Имя>"//
                + "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
                + "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
                + "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc//Cfg.currentHRC()
                + "</m:КодПользователя>"//
                + "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
                + "\n					</m:Параметры>"//
                + "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
                + "\n				</m:getReport>" //
                + "\n			</soap:Body>"//
                + "\n		</soap:Envelope>";
        //System.out.println(xml);
        return xml;*/
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
			RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
			kontr.selection.is(whoPlus1);
			kontr.item("[Все контрагенты]");
			for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontr.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()+": "+Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}




			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "В матрице", new RedactSingleChoice(context).item("Да").item("Нет").selection.is(vMatrice))//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Контрагент", kontr, Auxiliary.tapSize * 9)//
					.input(context, 5, Auxiliary.tapSize * 0.3, "Территория", terr)//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 0 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);

			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportKontragentySPrilojeniem.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}

		return propertiesForm;
	}
}

