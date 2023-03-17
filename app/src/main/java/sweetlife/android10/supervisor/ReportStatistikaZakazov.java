package sweetlife.android10.supervisor;

import java.io.File;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;
import android.content.Context;

public class ReportStatistikaZakazov extends Report_Base {
	Numeric dateShipFrom = new Numeric();
	Numeric dateShipTo = new Numeric();
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	//Numeric dateKontragent = new Numeric();
	Numeric territory = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel() {
		return "Статистика заказов";
	}
	public static String folderKey() {
		return "statistikaZakazov";
	}
	public  String getMenuLabel() {
		return "Статистика заказов";
	}
	public  String getFolderKey() {
		return "statistikaZakazov";
	}
	public ReportStatistikaZakazov(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			String shfrom = Cfg.formatMills(Numeric.string2double(b.child("shipFrom").value.property.value()), "dd.MM.yyyy");
			String shto = Cfg.formatMills(Numeric.string2double(b.child("shipTo").value.property.value()), "dd.MM.yyyy");
			return shfrom + " - " + shto + " от " + dfrom + " - " + dto;
		}
		catch (Throwable t) {
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
			String kontr = "все контрагенты";
			int wh = (int) Numeric.string2double(b.child("who").value.property.value());
			if (wh > 0) {
				int nn = wh - 1;
				kontr = ActivityWebServicesReports.naimenovanieKontragenta(nn);
			}
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return kontr + ", " + s;
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
		dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		dateShipFrom.value(Numeric.string2double(b.child("shipFrom").value.property.value()));
		dateShipTo.value(Numeric.string2double(b.child("shipTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("shipFrom").value.is("" + dateShipFrom.value());
		b.child("shipTo").value.is("" + dateShipTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("who").value.is("" + whoPlus1.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("docFrom").value.is("" + (d - 7 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		b.child("shipFrom").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("shipTo").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("0");
		b.child("who").value.is("0");
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
				+ "СтатистикеЗаказовHRC"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateShipFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateShipTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>ЗаказыПокупателей</Name>"//
				+ "\n							<Value>Истина</Value>"//
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Булево</TipeElem>"//
				+ "\n						</Param>"//
				+ "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>НачЗабития</Name>"//
				+ "\n							<Value>" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd") + "000000</Value>"//
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Дата</TipeElem>"//
				+ "\n						</Param>"//
				+ "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>КонЗабития</Name>"//
				+ "\n							<Value>" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd") + "235959</Value>"//
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Дата</TipeElem>"//
				+ "\n						</Param>"//	
				+ kp//
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
		//if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			RedactSingleChoice kontr = new RedactSingleChoice(context);
			kontr.selection.is(whoPlus1);
			kontr.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontr.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Доставка с", new RedactDate(context).date.is(dateShipFrom).format.is("dd.MM.yyyy"))//
					.input(context, 4, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateShipTo).format.is("dd.MM.yyyy"))//
					.input(context, 5, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 6, Auxiliary.tapSize * 0.3, "Контрагент", kontr)//
			/*
					.input(context, 7, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
						@Override
						public void doTask() {
							expectRequery.start(activityReports);
						}
					}), Auxiliary.tapSize * 3)//
					*/
			;
			/*propertiesForm.child(new RedactDate(context)//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * 0.3))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize *(0.5+ 1 * 1.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
					);*/
			propertiesForm.child(new Knob(context)//
					.labelText.is("На завтра")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							long d = new Date().getTime();
							dateCreateFrom.value(d - 7 * 24 * 60 * 60 * 1000.0);
							dateCreateTo.value(d + 0 * 24 * 60 * 60 * 1000.0);
							dateShipFrom.value(d + 1 * 24 * 60 * 60 * 1000.0);
							dateShipTo.value(d + 1 * 24 * 60 * 60 * 1000.0);
							expectRequery.start(activityReports);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 7 + 0.5)))//
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
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 7 + 1 + 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportStatistikaZakazov.this, currentKey);
						}
					})//
							.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 7 + 2 + 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
		//}
		propertiesForm.innerHeight.is(Auxiliary.tapSize * 14);
		return propertiesForm;
	}
}
