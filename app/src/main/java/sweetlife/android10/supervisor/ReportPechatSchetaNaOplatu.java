package sweetlife.android10.supervisor;

import java.io.File;
import java.util.Date;

import reactive.ui.*;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportPechatSchetaNaOplatu extends Report_Base {
	//Numeric territory = new Numeric();
	Numeric dateFrom = new Numeric();
	Numeric dateTo = new Numeric();
	Numeric zakazRealizacia = new Numeric();
	Note nomerDocumenta = new Note();
	Numeric summaDolg = new Numeric();
	Numeric whoPlus1 = new Numeric();

	static Integer tempInitKind=null;
	//static String tempInitNum=null;

	public static String menuLabel() {
		return "Печать счёта на оплату";
	}

	public static String folderKey() {
		return "pechatSchetaNaOplatu";
	}

	public String getMenuLabel() {
		return "Печать счёта на оплату";
	}

	public String getFolderKey() {
		return "pechatSchetaNaOplatu";
	}

	public ReportPechatSchetaNaOplatu(ActivityWebServicesReports p) {
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
			return dFrom + " - " + dTo;
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
			//int i = (int) Numeric.string2double(b.child("territory").value.property.value());
			String s = "";// b.child("nomerDocumenta").value.property.value();//Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			/*if (Numeric.string2double(b.child("zakazRealizacia").value.property.value()) > 0) {
				s = s + ", реализация";
			} else {
				s = s + ", заказ";
			}*/
			int selnum = (int) Numeric.string2double(b.child("zakazRealizacia").value.property.value());
			if (selnum == 0) {
				s = s + "заказ " + b.child("nomerDocumenta").value.property.value();
			} else {
				if (selnum == 1) {
					s = s + "реализация " + b.child("nomerDocumenta").value.property.value();
				} else {
					if (selnum == 2) {
						s = s + "задолженность";
					} else {
						s = s + "сумма " + b.child("summa").value.property.value();
					}
				}
			}
			return s;
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
		zakazRealizacia.value(Numeric.string2double(b.child("zakazRealizacia").value.property.value()));
		nomerDocumenta.value(b.child("nomerDocumenta").value.property.value());
		summaDolg.value(Numeric.string2double(b.child("summa").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		b.child("who").value.is("" + whoPlus1.value());
		//territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		if(EditOrderViaWeb.documentNumber.trim().length()>0){
			nomerDocumenta.value(EditOrderViaWeb.documentNumber.trim());
			EditOrderViaWeb.documentNumber="";
		}
		//if(tempInitNum!=null)b.child("nomerDocumenta").value.is(tempInitNum);
		if(tempInitKind!=null)b.child("zakazRealizacia").value.is(""+tempInitKind);
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//b.child("from").value.is("" + dateFrom.value());
		//b.child("to").value.is("" + dateTo.value());
		b.child("from").value.is("" + dateFrom.value());
		b.child("to").value.is("" + dateTo.value());
		b.child("summa").value.is("" + summaDolg.value());
		b.child("zakazRealizacia").value.is("" + zakazRealizacia.value());
		b.child("nomerDocumenta").value.is("" + nomerDocumenta.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		//long d = new Date().getTime();
		//b.child("from").value.is("" + (d - 30 * 24 * 60 * 60 * 1000.0));
		//b.child("to").value.is("" + (d));
		//b.child("ship").value.is("" + (d + 1 * 24 * 60 * 60 * 1000.0));

		long d = new Date().getTime();
		Date mb = new Date();
		mb.setDate(1);
		b.child("from").value.is("" + mb.getTime());
		b.child("to").value.is("" + d);

		if (EditOrderViaWeb.documentNumber != null && EditOrderViaWeb.documentNumber.length() > 1) {
			nomerDocumenta.value(EditOrderViaWeb.documentNumber);
			b.child("nomerDocumenta").value.is(nomerDocumenta.value());
			EditOrderViaWeb.documentNumber = "";
		}

		//if(tempInitNum!=null)b.child("nomerDocumenta").value.is(tempInitNum);
		if(tempInitKind!=null)b.child("zakazRealizacia").value.is(""+tempInitKind);
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println(xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
		tempInitKind=null;
		//tempInitNum=null;
		EditOrderViaWeb.documentNumber="";
	}

	//@Override
	public String ____composeRequest() {
		//long cu = new Date().getTime();
		//String dt=Cfg.formatMills(dateShip.value(), "yyyy-MM-dd");
		int zr = zakazRealizacia.value().intValue();
		//String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String xml = ""//
				+ "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
				+ "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n			<soap:Body>"//
				+ "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
				+ "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				+ "ПечатьСчетаНаОплату"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.whoCheckListOwner() + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>Тип</Name>"//
				+ "\n							<Value>" + zakazRealizacia.value() /*"0"*/ + "</Value>"//kod
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Число</TipeElem>"//
				+ "\n						</Param>";
		xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
				+ "\n							<Name>НомерДокумента</Name>"//
				+ "\n							<Value>" + nomerDocumenta.value() /*"12-0000025"*/ + "</Value>"//kod
				+ "\n							<Tipe>Значение</Tipe>"//
				+ "\n							<TipeElem>Строка</TipeElem>"//
				+ "\n						</Param>";
				/*
				xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
						+ "\n							<Name>ТипФайла</Name>"//
						+ "\n							<Value>" + "pdf" + "</Value>"//
						+ "\n							<Tipe>Значение</Tipe>"//
						+ "\n							<TipeElem>Строка</TipeElem>"//
						+ "\n						</Param>";
				*/
		xml = xml + "\n					</m:Параметры>"//
				+ "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
				+ "\n				</m:getReport>" //
				+ "\n			</soap:Body>"//
				+ "\n		</soap:Envelope>";
		//System.out.println(xml);
		return xml;
	}

	@Override
	public String composePostBody(int queryKind) {
		Bough kontragenty = Cfg.kontragentyForSelectedMarshrut();
		int nn = whoPlus1.value().intValue();
		nn = (nn < 0 || nn >= kontragenty.children.size()) ? 0 : nn;
		String kod ="";
		try {
			 kod = kontragenty.children.get(nn).child("kod").value.property.value();
		}catch(Throwable t){

		}

		String nomer = nomerDocumenta.value();
		String format = "html";
		//if (xls) format = "xls";
		if (queryKind==xlsQuery) format = "xls";
		if (queryKind==pdfQuery) format = "pdf";
		String nachalo = Cfg.formatMills(dateFrom.value(), "yyyyMMdd");
		String konec = Cfg.formatMills(dateTo.value(), "yyyyMMdd");
		String kontragent = kod;
		String imei = "";
		String summa = "" + summaDolg.value();
		String tip = "" + zakazRealizacia.value().intValue();
		String poZadoljennosti = "false";

		if (zakazRealizacia.value() == 0) {
			summa = "0";
			kontragent = "";
		} else {
			if (zakazRealizacia.value() == 1) {
				summa = "0";
				kontragent = "";
			} else {
				if (zakazRealizacia.value() == 2) {
					nomer = "";
					summa = "0";
					poZadoljennosti = "true";
				} else {
					nomer = "";
					poZadoljennosti = "false";
				}
			}
		}

		String q = "{"
				+ "\"НомерДокумента\":\"" + nomer.trim() + "\""
				+ ", \"Формат\":\"" + format.trim() + "\""
				+ ", \"НачалоПериода\":\"" + nachalo.trim() + "\""
				+ ", \"КонецПериода\":\"" + konec.trim() + "\""
				+ ", \"Контрагент\":\"" + kontragent.trim() + "\""
				//+ ", \"IMEI\":\"" + imei + "\""
				+ ", \"Сумма\":" + summa.trim() + ""
				+ ", \"Тип\":" + tip + ""
				+ ", \"ПоЗадолженности\":" + poZadoljennosti.trim() + ""
				+ "}";
		System.out.println("body " + q);
		return q;
	}

	@Override
	public String composeGetQuery(int queryKind) {
		//https://testservice.swlife.ru/velinsky_hrc/hs/SchetNaOplatu/ПечатьСчетаНаОплату/bot23
		String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/SchetNaOplatu/ПечатьСчетаНаОплату/" + Cfg.whoCheckListOwner() ;
		url=url+"?IMEI=null";
		if(queryKind==xlsQuery){
			url=url+"&ТипФайла=xls";
		}
		if(queryKind==pdfQuery){
			url=url+"&ТипФайла=pdf";
		}

		return url;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			RedactSingleChoice zr = new RedactSingleChoice(context);
			zr.selection.is(zakazRealizacia);
			zr.item("По заказу");
			zr.item("По реализации");
			zr.item("По задолженности");
			zr.item("По сумме");
			RedactSingleChoice kontr = new RedactSingleChoice(context);
			//kontr.hidden().is(zakazRealizacia.less(2));
			kontr.selection.is(whoPlus1);
			//kontr.item("[Все контрагенты]");
			for (int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontr.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Тип документа", zr)//

					.input(context, 2, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateFrom).format.is("dd.MM.yyyy"), zakazRealizacia.more(1))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateTo).format.is("dd.MM.yyyy"), zakazRealizacia.more(1))//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Номер документа", new RedactText(context).text.is(nomerDocumenta), zakazRealizacia.more(1))//

					.input(context, 2, Auxiliary.tapSize * 0.3, "Контрагент", kontr, zakazRealizacia.less(2))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Сумма", new RedactNumber(context).number.is(summaDolg), zakazRealizacia.less(3))//


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
							activityReports.promptDeleteRepoort(ReportPechatSchetaNaOplatu.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

}
