package sweetlife.android10.supervisor;

import android.content.*;

import java.util.*;

import reactive.ui.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

public class ReportPlanPolevihRabot extends Report_Base {
	Numeric dateFrom = new Numeric();
	//Numeric dateTo = new Numeric();
	//Numeric territory = new Numeric();
	//Numeric whoPlus1 = new Numeric();
	public  String getMenuLabel() {
		return "План полевых работ";
	}
	public  String getFolderKey() {
		return "planPolevihRabot";
	}
	
	public static String menuLabel() {
		return "План полевых работ";
	}
	public static String folderKey() {
		return "planPolevihRabot";
	}
	public ReportPlanPolevihRabot(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String kontr = "все контрагенты";
			int wh = (int) Numeric.string2double(b.child("who").value.property.value());
			if (wh > 0) {
				int nn = wh - 1;
				kontr = ActivityWebServicesReports.naimenovanieKontragenta(nn);
			}
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return kontr+", "+s;
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
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("from").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("to").value.property.value()), "dd.MM.yyyy");
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
		try {
			b = Bough.parseXML(xml);
		}
		catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		//dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		//territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		//whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("from").value.is("" + dateFrom.value());
		//b.child("to").value.is("" + dateTo.value());
		//b.child("who").value.is("" + whoPlus1.value());
		//b.child("territory").value.is("" + territory.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		Date mb = new Date();
		mb.setDate(1);
		b.child("from").value.is("" + mb.getTime());
		b.child("to").value.is("" + (d));
		//b.child("territory").value.is("" + (Cfg.territory().children.size()-1));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	private  String kod(int nn) {
		String r = "-123456789";
		if (nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}
	@Override
	public String composeRequest() {
		//long cu = new Date().getTime();
		/*int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String kp = "";
		if (whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			kp = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Контрагенты</Name>"//
					+ "\n							<Value>" + kod(nn) + "</Value>"//
					+ "\n							<Tipe>Массив</Tipe>"//
					+ "\n							<TipeElem>Число</TipeElem>"//
					+ "\n						</Param>";
		}*/
		String p=Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd")+"T00:00:01";
		String hrc=Cfg.whoCheckListOwner();
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">ПланПолевыхРаботДляТП</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + p + "</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + p+ "</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "\n					</m:Параметры>"//
				+ "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
				+ "\n				</m:getReport>" //
				+ "\n			</soap:Body>"//
				+ "\n		</soap:Envelope>";
		//System.out.println(xml);
		return xml;
	}
	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			/*RedactSingleChoice terr = new RedactSingleChoice(context);
			RedactSingleChoice kontragent = new RedactSingleChoice(context);
			

			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			
			terr.selection.is(territory);
			//terr.item("?");
			//System.out.println("territory " + ActivityReports.territory.children.size());
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				//System.out.println(s);
				terr.item(s);
			}*/
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					//.input(context, 3, Auxiliary.tapSize * 0.3, "Контрагент", kontragent)//
					//.input(context, 4, Auxiliary.tapSize * 0.3, "Территория", terr)//
			/*.input(context, 4, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
				@Override
				public void doTask() {
					expectRequery.start(activityReports);
				}
			}), Auxiliary.tapSize * 3)*/
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							Date mb = new Date();
							mb.setDate(1);
							dateFrom.value((double) mb.getTime());
							long d = new Date().getTime();
							dateFrom.value((double) d);
							//dateTo.value((double) d);
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
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
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 +1+ 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportPlanPolevihRabot.this, currentKey);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 *2+2+ 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
			/*
			propertiesForm//
					.child(new Decor(context)//
					.labelText.is(getMenuLabel())//
							.labelStyleLargeNormal()//
							.left().is(8)//
							.width().is(propertiesForm.width().property)//
							.height().is(propertiesForm.height().property)//
					)//
					
					.child(new Decor(context)//
					.labelText.is("Период с")//
						.labelAlignLeftBottom()//
						.left().is(8)//
						.top().is(1 * Auxiliary.tapSize)//
						.width().is(propertiesForm.width().property)//
						.height().is(Auxiliary.tapSize * 0.5)//
					)
					.child(new RedactDate(context)//
					.date.is(dateFrom)//
					.format.is("dd.MM.yyyy")//
						.left().is(8)//
						.top().is(1.5 * Auxiliary.tapSize)//
						.width().is(Auxiliary.tapSize * 5)//
						.height().is(Auxiliary.tapSize * 0.8)//
					)//
					//
					.child(new Decor(context)//
					.labelText.is("по")//
						.labelAlignLeftBottom()//
						.left().is(8)//
						.top().is(2.5 * Auxiliary.tapSize)//
						.width().is(propertiesForm.width().property)//
						.height().is(Auxiliary.tapSize * 0.5)//
					)//
					.child(new RedactDate(context)//
					.format.is("dd.MM.yyyy")//
					.date.is(dateTo)//
						.left().is(8)//
						.top().is(3 * Auxiliary.tapSize)//
						.width().is(Auxiliary.tapSize * 5)//
						.height().is(Auxiliary.tapSize * 0.8)//
					)//
					
					.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//final String htm = Cfg.pagePath(getFolderKey(), currentKey);
							expectRequery.start(activityReports);
						}
					})//
							.left().is(8)//
							//.top().is(6 * Auxiliary.tapSize)//
							.top().is(4.5 * Auxiliary.tapSize)//
							.width().is(Auxiliary.tapSize * 2)//
							.height().is(Auxiliary.tapSize * 0.8)//
					)//
			;
			*/
		}
		return propertiesForm;
	}
}
