package sweetlife.android10.supervisor;

import android.content.*;

import java.util.*;

import reactive.ui.*;

import java.net.*;

import sweetlife.android10.Settings;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.io.*;

public class ReportVipolnenieDopMotivaciy extends Report_Base {
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric territory = new Numeric();

	public String getMenuLabel() {
		return "Выполнение доп. мотиваций";
	}

	public String getFolderKey() {
		return "vipolnenieDopMotivaciy";
	}

	public static String menuLabel() {
		return "Выполнение доп. мотиваций";
	}

	public static String folderKey() {
		return "vipolnenieDopMotivaciy";
	}

	public ReportVipolnenieDopMotivaciy(ActivityWebServicesReports p) {
		super(p);
	}

	@Override
	public String getOtherDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);

			int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			return s;
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
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));

	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());

		b.child("territory").value.is("" + territory.value());
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
		//b.child("territory").value.is("" + (Cfg.territory().children.size() - 1));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind) {
		//https://service.swlife.ru/hrc120107/hs/Report/ВыполнениеДопМотиваций/hrc288?param={"ДатаНачала":"20220901","ДатаОкончания":"20220930"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String from = Cfg.formatMills(dateFrom.value(), "yyyyMMdd");
		String to = Cfg.formatMills(dateTo.value(), "yyyyMMdd");
		String p = "{\"ДатаНачала\":\"" + from + "\",\"ДатаОкончания\":\"" + to + "\"}";
		String q = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Report/ВыполнениеДопМотиваций/" + hrc + "?param=";
		try {
			p = URLEncoder.encode("{\"ДатаНачала\":\"" + from + "\",\"ДатаОкончания\":\"" + to + "\"}");
			q = q + p;
			/*if (xls) {
				q = q + "&" + URLEncoder.encode("Формат", "UTF-8") + "=xls";
			}*/
			q=q+tagForFormat( queryKind);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return q;
	}

	/*@Override
	public String composeRequest() {
		//long cu = new Date().getTime();
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">ВыполнениеДопМотиваций</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
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
			//terr.item("?");
			//System.out.println("territory " + ActivityReports.territory.children.size());
			for (int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				//System.out.println(s);
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//

					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
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
							//dateFrom.value((double) d);
							dateTo.value((double) d);
							//checkHolding();
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
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//checkHolding();
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
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportVipolnenieDopMotivaciy.this, currentKey);
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
}
