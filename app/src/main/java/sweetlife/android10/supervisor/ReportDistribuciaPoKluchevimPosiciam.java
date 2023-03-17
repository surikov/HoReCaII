//ReportDistribuciaPoKluchevimPosiciam
package sweetlife.android10.supervisor;

import java.io.File;
import java.util.Date;

import reactive.ui.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;
import android.content.*;

public class ReportDistribuciaPoKluchevimPosiciam  extends Report_Base{
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric territory = new Numeric();
	Numeric vid = new Numeric();
	Numeric whoPlus1 = new Numeric();

	public static String menuLabel() {
		return "Дистрибуция по ключевым позициям";
	}
	public static String folderKey() {
		return "distribuciaPoKluchevimPosiciam";
	}
	public  String getMenuLabel() {
		return "Дистрибуция по ключевым позициям";
	}
	public  String getFolderKey() {
		return "distribuciaPoKluchevimPosiciam";
	}
	public ReportDistribuciaPoKluchevimPosiciam(ActivityWebServicesReports p) {
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
			if (wh > 0 && wh<=Cfg.kontragenty().children.size()) {
				//int nn = wh - 1;
				//kontr = ReportBallansPartnera.naimenovanie(nn)+" "+wh;
				kontr=Cfg.kontragenty().children.get(wh - 1).child("naimenovanie").value.property.value();
			}
			
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return s+", "+kontr;
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
			//String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			return dfrom;//+ " - " + dto;
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
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		vid.value(Numeric.string2double(b.child("vid").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("territory").value.is("" + territory.value());
		b.child("vid").value.is("" + vid.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("who").value.is("" + whoPlus1.value());
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
		//b.child("territory").value.is("" + (Cfg.territory().children.size() - 1));
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
				+ "ДистрибуцияПоКлючевымПозициямДляТП"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc//Cfg.currentHRC() 
				+ "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
					+ "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>ВидОтчета</Name>"//
				+ "\n							<Value>" +vid.value().intValue() + "</Value>"//
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Число</TipeElem>"//
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
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
RedactSingleChoice kontragent = new RedactSingleChoice(context);
			

			kontragent.selection.is(whoPlus1);
			kontragent.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Контрагент", kontragent)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 5, Auxiliary.tapSize * 0.3, "Вид отчёта", new RedactSingleChoice(context).selection.is(vid)//
							.item("По позициям")//
							.item("По клиентам")//

					)//
			//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
			/*.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
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
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 0.5)))//
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
							.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 1 + 0.5)))//
							.width().is(Auxiliary.tapSize * 2.5)//
							.height().is(Auxiliary.tapSize * 0.8)//
					);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportDistribuciaPoKluchevimPosiciam.this, currentKey);
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
