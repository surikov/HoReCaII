package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactFilteredSingleChoice;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;
import android.content.Context;

public class ReportNalichieSkanov extends Report_Base {
	//Numeric dateCreateFrom = new Numeric();
	//Numeric dateCreateTo = new Numeric();
	Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();
	public static String menuLabel() {
		return "Наличие сканов документов";
	}
	public static  String folderKey() {
		return "nalichieSkanov";
	}
	public  String getMenuLabel() {
		return "Наличие сканов документов";
	}
	public   String getFolderKey() {
		return "nalichieSkanov";
	}
	public ReportNalichieSkanov(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			//String s = ActivityWebServicesReports.territory.children.get(i).child("territory").value.property.value() + " (" + ActivityWebServicesReports.territory.children.get(i).child("hrc").value.property.value().trim() + ")";
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return  s;
		}
		catch (Throwable t) {
			//
		}
		return "?";
	}
	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		}
		catch (Throwable t) {
			//
		}
		return "?";
	}
	@Override
	public void readForm(String instanceKey) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		try {
			b = Bough.parseXML(xml);
		}
		catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		//dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		//dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//b.child("docFrom").value.is("" + dateCreateFrom.value());
		//b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("who").value.is("" + whoPlus1.value());
		b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	private String kod(int nn) {
		String r = "";
		if (nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}
	@Override
	public String composeGetQuery(int queryKind) {
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();

		String kontragentKod="";
		if (whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			if (nn >= 0 && nn < Cfg.kontragenty().children.size()) {
				kontragentKod = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
			}
		}
		String p = "{"
				+"\"ВариантОтчета\":\"ТипыДокументов\""//
				+",\"Контрагент\":\"" + kontragentKod + "\""//
				+"}";
		String e = "";
		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName="НаличиеСкановДокументов";
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
				+ serviceName+"/" + hrc//
				+ "?param=" + e//
				;
		q=q+tagForFormat( queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	/*public String composeRequest() {
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "НаличиеСкановДокументов"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(new Date().getTime(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(new Date().getTime(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "\n					</m:Параметры>"//
				+ "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
				+ "\n				</m:getReport>" //
				+ "\n			</soap:Body>"//
				+ "\n		</soap:Envelope>";
		//System.out.println(xml);
		return xml;
	}*/
	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {

			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			RedactFilteredSingleChoice kontragent = new RedactFilteredSingleChoice(context);
			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()+":"+Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					//.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Контрагент", kontragent, Auxiliary.tapSize * 9)//
					.input(context, 2, Auxiliary.tapSize * 0.3, "Территория", terr)//
			/*.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
				@Override
				public void doTask() {
					expectRequery.start(activityReports);
				}
			}), Auxiliary.tapSize * 3)*/
			;
			/*propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//Date mb = new Date();
							//mb.setDate(1);
							
							long d = new Date().getTime();
							//dateFrom.value((double) d);
							dateCreateFrom.value(d - 0 * 24 * 60 * 60 * 1000.0);
							dateCreateTo.value((double) d);
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 4.5 + 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);*/
			propertiesForm.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (5 +0)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportNalichieSkanov.this, currentKey);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (5+1)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
		}
		return propertiesForm;
	}
}
