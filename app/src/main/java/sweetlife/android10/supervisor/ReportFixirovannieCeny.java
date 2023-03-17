package sweetlife.android10.supervisor;

import java.io.File;
import java.util.Date;
import java.util.regex.*;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.*;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportFixirovannieCeny extends Report_Base {
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric territory = new Numeric();
	Note artikul = new Note();

	public static String menuLabel() {
		return "Фиксированные цены";
	}

	public static String folderKey() {
		return "fixirovannieCeny";
	}

	public String getMenuLabel() {
		return "Фиксированные цены";
	}

	public String getFolderKey() {
		return "fixirovannieCeny";
	}

	public ReportFixirovannieCeny(ActivityWebServicesReports p) {

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
			int i = (int) Numeric.string2double(b.child("territory").value.property.value());

			//System.out.println("artikul.value() '"+artikul.value()+"'");
			String s = b.child("artikul").value.property.value() + " " + Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
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
		dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		artikul.value(b.child("artikul").value.property.value());
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("territory").value.is("" + territory.value());
		b.child("artikul").value.is(artikul.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		b.child("artikul").value.is("");
		b.child("territory").value.is("0");
		b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
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
				+ "ЗаявкиНаФиксированныеЦены"//
				+ "</m:Имя>"//
				+ "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateFrom.value(), "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
				+ "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(dateCreateTo.value(), "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
				+ "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
				+ "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
				;
		if (artikul.value().length() > 1) {
			xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Номенклатура</Name>"//
					+ "\n							<Value>" + artikul.value() + "</Value>"//
					+ "\n							<Tipe>Массив</Tipe>"//
					+ "\n							<TipeElem>Строка</TipeElem>"//
					+ "\n						</Param>"//
			;
		}
		xml = xml + "\n					</m:Параметры>"//
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
				String s = Cfg.territory().children.get(i).child("territory").value.property.value()// 
						+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Номенклатура (артикул)", new RedactText(context).text.is(artikul))//
			/*
								.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
									@Override
									public void doTask() {
										expectRequery.start(activityReports);
									}
								}), Auxiliary.tapSize * 3)*/;
			propertiesForm.child(new Knob(context)//
					.labelText.is("На сегодня")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							long d = new Date().getTime();
							dateCreateFrom.value(d - 0 * 24 * 60 * 60 * 1000.0);
							dateCreateTo.value((double) d);
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 0.5)))//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportFixirovannieCeny.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

	@Override
	public String interceptActions(String s) {
		//String r=s.replaceAll("(№(\\S+)\\s*,\\s*+Арт(\\S+\\d+))"//
		//		,"<a href='hook?kind="+HOOKApproveFix+"&"+FIELDFixNum+"=$2&"+FIELDFixArt+"=$3'>$0</a>");
		//System.out.println("no "+r);
		//return r;
		Pattern p = Pattern.compile("№(\\S+)\\s*,\\s*+Арт(\\S+\\d+)");

		String[] strings = s.split("\n");
		/*java.util.Vector<Integer> counts = new java.util.Vector<Integer>();

		String predoc = "";
		int strnum = 0;
		int ff = 0;
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];
			String num = "№" + extract(line, '№', '<');
			if (num.length() > 2) {
				Matcher m = p.matcher(num);
				if (m.matches()) {
					counts.add(1);
					//System.out.println("add "+strnum+": "+counts.size());
					String doc = m.group(1);
					if (doc.equals(predoc)) {
						strnum++;
					} else {
						//System.out.println(ff-1+": "+(ff-strnum)+": "+strnum);
						if (strnum > 1){
							//System.out.println("summ "+strnum+": "+i+": "+ff);
							for (int kk = 0; kk <= strnum; kk++) {
								int idx = ff - 1 - kk;
								if (idx >= 0 && idx < counts.size()) {
									counts.set(idx, strnum + 1);
									//System.out.println("change "+idx+": "+(strnum + 1));
								}
							}
						}
						strnum = 0;
					}
					predoc = doc;
					ff++;
				}
			}
		}*/
/*
		for (int kk = 0; kk<counts.size(); kk++) {
			System.out.println(kk+": "+counts.get(kk));
		}*/


		//predoc = "";
		//strnum = 0;
		//ff = 0;
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];
			String num = "№" + extract(line, '№', '<');
			if (num.length() > 2) {
				int start = line.indexOf('№') + 1;
				int end = line.indexOf("<", start + 1);
				Matcher m = p.matcher(num);
				if (m.matches()) {
					String doc = m.group(1);
					String art = m.group(2);
					/*if (doc.equals(predoc)) {
						strnum++;
					} else {
						strnum = 0;
					}*/
					line = line.substring(0, start)
							+ "<a href='hook?kind=" + HOOKApproveFix
							+ "&" + FIELDFixNum + "=" + doc
							+ "&" + FIELDFixArt + "=" + art
							//+ "&" + FIELDFixRow + "=" + (counts.get(ff)-strnum-1)
							//+ "'>" + doc + ", Арт " + art + " cтр." + (counts.get(ff)-strnum) //+ "/" + counts.get(ff)
							+ "'>" + doc + ", Арт " + art
							+ "</a>" + line.substring(end);
					//System.out.println("set "+strnum+": "+i);
					//predoc = doc;
					//ff++;
					//System.out.println("url line: " + line);
				}

				strings[i] = line;
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}
