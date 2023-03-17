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

public class ReportBonusyDlyaTP extends Report_Base {
	Numeric territory = new Numeric();
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric kind = new Numeric();
	public static String menuLabel() {
		return "Товарные бонусы";//"Бонусы для ТП";
	}
	public static String folderKey() {
		return "BonusyDlyaTP";
	}
	public  String getMenuLabel() {
		return "Товарные бонусы";//"Бонусы для ТП";
	}
	public  String getFolderKey() {
		return "BonusyDlyaTP";
	}
	public ReportBonusyDlyaTP(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getShortDescription(String key) {
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try {
			b = Bough.parseXML(xml);
			String dFrom = Cfg.formatMills(Numeric.string2double(b.child("from").value.property.value()), "dd.MM.yyyy");
			String dTo = Cfg.formatMills(Numeric.string2double(b.child("to").value.property.value()), "dd.MM.yyyy");
			int k = (int) Numeric.string2double(b.child("kind").value.property.value());
			String tipOpt = kindLabel(k);
			return dFrom + " - " + dTo + ", " + tipOpt;
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
		dateFrom.value(Numeric.string2double(b.child("from").value.property.value()));
		dateTo.value(Numeric.string2double(b.child("to").value.property.value()));
		kind.value(Numeric.string2double(b.child("kind").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("territory").value.is("" + territory.value());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());
		b.child("kind").value.is("" + kind.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//long d = new Date().getTime();
		Date mb = new Date();
		//mb.setDate(1);
		long tomorrow=mb.getTime()+1000*60*60*24;
		b.child("from").value.is("" + tomorrow);
		b.child("to").value.is("" + tomorrow);
		b.child("kind").value.is("1");
		b.child("territory").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public String composeRequest() {
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "БонусыДляТП"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
					+ "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>ТипОтчета</Name>"//
				+ "\n							<Value>" + kindParameter(kind.value().intValue()) + "</Value>"//
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Число</TipeElem>"//
				+ "\n						</Param>"//
				+ "\n					</m:Параметры>"//
				+ "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
				+ "\n				</m:getReport>" //
				+ "\n			</soap:Body>"//
				+ "\n		</soap:Envelope>";
		//System.out.println(xml);
		return xml;
	}
	String kindLabel(int key) {
		String tipOpt = "бонусы по заказам";
		if (key == 1) {
			tipOpt = "отгруженные бонусы ";
		}
		
		return tipOpt;
	}
	String kindParameter(int k) {
		if (k == 1) {
			return "1";
		}
		
		return "0";
	}
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
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Отгрузка с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Тип", new RedactSingleChoice(context).selection.is(kind)//
							.item(kindLabel(0))//
							.item(kindLabel(1))//
					)//
					
					
					
					
					.input(context, 4, Auxiliary.tapSize * 0.3, "Территория", terr)//					
			;
			propertiesForm.child(new Knob(context)//
			.labelText.is("Обновить")//
			.afterTap.is(new Task() {
				@Override
				public void doTask() {
					expectRequery.start(activityReports);
				}
			})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
	propertiesForm.child(new Knob(context)//
			.labelText.is("Удалить")//
			.afterTap.is(new Task() {
				@Override
				public void doTask() {
					//expectRequery.start(activityReports);
					activityReports.promptDeleteRepoort(ReportBonusyDlyaTP.this, currentKey);
				}
			})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 +1+ 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
	
}
