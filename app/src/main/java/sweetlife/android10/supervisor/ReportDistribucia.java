package sweetlife.android10.supervisor;

import android.content.*;

import java.util.*;

import reactive.ui.*;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

public class ReportDistribucia extends Report_Base {
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	//Numeric territory = new Numeric();
	They<Integer> territories = new They<Integer>();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel() {
		return "Дистрибуция";
	}

	public static String folderKey() {
		return "distribucia";
	}

	public String getMenuLabel() {
		return "Дистрибуция";
	}

	public String getFolderKey() {
		return "distribucia";
	}

	public ReportDistribucia(ActivityWebServicesReports p) {
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
			if (wh > 0 && wh <= Cfg.kontragenty().children.size()) {
				kontr = Cfg.kontragenty().children.get(wh - 1).child("naimenovanie").value.property.value();
			}
			//int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			//String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			//return kontr + ", " + s;
			return kontr;
		} catch (Throwable t) {
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
		} catch (Throwable t) {
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
		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		//territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		String data = b.child("territories").value.property.value();
		String[] arr = data.split(",");
		for (int i = 0; i < arr.length; i++) {
			territories.insert(i, (int) Numeric.string2double(arr[i]));
		}
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());
		b.child("who").value.is("" + whoPlus1.value());
		//b.child("territory").value.is("" + territory.value());
		String data = "";
		for (int i = 0; i < territories.size(); i++) {
			if (i > 0) data = data + ",";
			int nn = territories.at(i);
			data = data + nn;
		}
		b.child("territories").value.is(data);
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
		//int i = territory.value().intValue();
		//String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String hrc = Cfg.selectedOrDbHRC();
		String kp = "";
		if (whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			kp = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Контрагенты</Name>"//
					+ "\n							<Value>" + kod(nn) + "</Value>"//
					+ "\n							<Tipe>Массив</Tipe>"//
					+ "\n							<TipeElem>Число</TipeElem>"//
					+ "\n						</Param>";
		}
		String terrData = "";
		if (territories.size() > 0) {
			String data = "";
			for (int i = 0; i < territories.size(); i++) {
				if (i > 0) data = data + ",";
				int nn = territories.at(i);
				String thrc = Cfg.territory().children.get(nn).child("hrc").value.property.value();
				data = data + thrc;
			}
			terrData = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Hrc</Name>"//
					+ "\n							<Value>" + data + "</Value>"//
					+ "\n							<Tipe>Значение</Tipe>"//
					+ "\n							<TipeElem>Строка</TipeElem>"//
					+ "\n						</Param>";
		}
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Дистрибуция</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ kp//
				+ terrData
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

			RedactMultiChoice terr = new RedactMultiChoice(context);
			RedactSingleChoice kontragent = new RedactSingleChoice(context);

			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			terr.selection.bind(territories);
			//terr.selection.is(territory);
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Контрагент", kontragent)//
					.tallInput(context, 4, Auxiliary.tapSize * 0.3, new Note().value("Территория"), terr, new Numeric().value(5 * Auxiliary.tapSize))//
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
							dateTo.value((double) d);
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 0.5)))//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							activityReports.promptDeleteRepoort(ReportDistribucia.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
}
