package sweetlife.android10.supervisor;

import java.io.File;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.SubLayoutless;
import tee.binding.Bough;
import tee.binding.it.Numeric;

import android.content.*;

import java.util.*;

import reactive.ui.*;

import tee.binding.task.*;
import tee.binding.it.*;

public class ReportStatusyZakazov extends Report_Base {
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();
	Toggle inValidOnly = new Toggle();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel() {
		return "Статусы заказов";
	}

	public static String folderKey() {
		return "statusyZakazov";
	}

	public String getMenuLabel() {
		return "Статусы заказов";
	}

	public String getFolderKey() {
		return "statusyZakazov";
	}

	public ReportStatusyZakazov(ActivityWebServicesReports p) {
		super(p);
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
		} catch (Throwable t) {
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String validOnly = b.child("invalid").value.property.value().equals("true") ? "только непроведённые" : "проведённые и не проведённые";
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			//String s = ActivityWebServicesReports.territory.children.get(i).child("territory").value.property.value() + " (" + ActivityWebServicesReports.territory.children.get(i).child("hrc").value.property.value().trim() + ")";
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return validOnly + ", " + s;
		} catch (Throwable t) {
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
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		inValidOnly.value(b.child("invalid").value.property.value().equals("true") ? true : false);
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("invalid").value.is(inValidOnly.value() ? "true" : "false");
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("from").value.is("" + (d));
		b.child("to").value.is("" + (d));
		b.child("territory").value.is("0");
		b.child("invalid").value.is("false");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	private String kod(int nn) {
		String r = "-123456789";
		if (nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}

	@Override
	public String composeRequest() {
		//long cu = new Date().getTime();
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String kp = "";
		if (whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			kp = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Контрагент</Name>"//
					+ "\n							<Value>" + kod(nn) + "</Value>"//
					+ "\n							<Tipe>Значение</Tipe>"//
					+ "\n							<TipeElem>Число</TipeElem>"//
					+ "\n						</Param>";
		}
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "СтатусыЗаказов"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "\n						<Param xmlns=\"http://ws.swl/Param\">" //
				+ "\n							<Name>ТолькоНеПроведенные</Name>" //
				+ "\n							<Value>" + (inValidOnly.value() ? "Истина" : "Ложь") + "</Value>" //
				+ "\n							<Tipe>Значение</Tipe>" //
				+ "\n							<TipeElem>Булево</TipeElem>"//
				+ "\n						</Param>"//
				+ kp//
				+ "\n					</m:Параметры>"//
				+ "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
				+ "\n				</m:getReport>" //
				+ "\n			</soap:Body>"//
				+ "\n		</soap:Envelope>";
		return xml;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			//terr.item("?");
			//System.out.println("territory " + ActivityReports.territory.children.size());
			/*
			for (int i = 0; i < ActivityWebServicesReports.territory.children.size(); i++) {
				String s = ActivityWebServicesReports.territory.children.get(i).child("territory").value.property.value() + " (" + ActivityWebServicesReports.territory.children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			*/
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			RedactFilteredSingleChoice kontragent = new RedactFilteredSingleChoice(context);


			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()+": "+Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Контрагент", kontragent, Auxiliary.tapSize * 9)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 7 / 1.5, Auxiliary.tapSize * 0.3, "", new RedactToggle(context).labelText.is("только непроведённые").yes.is(inValidOnly))//
			/*.input(context, 6.5 / 1.5, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
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
							long d = new Date().getTime();
							dateFrom.value((double) d);
							dateTo.value((double) d);
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5.5 + 0.5)))//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5.5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportStatusyZakazov.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5.5 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

	@Override
	public String interceptActions(String s) {
		String[] strings = s.split("\n");
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];
			//System.out.println("line "+line);
			if (i - 2 > -1 && i + 1 < strings.length - 1) {
				String num = extract(line, '№', '<');
				String dat = extract(strings[i - 2], '>', '<');
				String ship = extract(strings[i + 1], '>', '<');
				String kname = extract(strings[i + 2], '>', '<');
				//System.out.println("kname "+kname);
				String[] partsNum = kname.split(":");
				if (num.length() > 2 && dat.trim().length() == 10 && ship.trim().length() == 10 && partsNum.length > 1) {
					String[] partsRemn = kname.split("/");
					if (partsRemn.length > 1) {
						//System.out.println(strings[i]);
						//System.out.println(strings[i+1]);
						//System.out.println(strings[i+2]);

						int start = line.indexOf('№');
						int end = line.indexOf("<", start + 1);
						String klientKod = partsNum[0].trim().replace("\"", "%22");
						klientKod = klientKod.replace("&", "%26");
						klientKod = klientKod.replace("'", "%27");
						//String klientName=parts[0].trim().replace("\"","%22");
						//klientName=klientName.replace("&","%26");
						//klientName=klientName.replace("'","%27");
						String dogOplName = partsRemn[1].trim() + ", " + partsRemn[2].trim();
						//FIELDKlientKod
						//System.out.println(line);
						line = line.substring(0, start)//
								+ "№<a href=\"hook"//
								+ "?kind=" + HOOKReportOrderState//
								+ "&" + FIELDDocumentNumber + "=" + num //
								+ "&" + FIELDDocumentDate + "=" + dat//
								+ "&" + FIELDShipDate + "=" + ship//
								//+ "&" + FIELDKlientKod + "=" + klientName//
								//+ "&" + FIELDKontragent + "=" + klientName//
								+ "&" + FIELDKlientKod + "=" + klientKod

								+ "&" + FIELDDogovorOplata + "=" + dogOplName//
								+ "\">" + num + "</a>"//
								+ line.substring(end);
						System.out.println("url line: "+line);
						//System.out.println("---");
						strings[i] = line;
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}
