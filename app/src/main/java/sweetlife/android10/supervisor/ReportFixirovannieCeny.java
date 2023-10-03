package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Vector;
import java.util.regex.*;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.*;
import reactive.ui.RedactSingleChoice;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportFixirovannieCeny extends Report_Base {
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric territory = new Numeric();
	//Note artikul = new Note();
	//String clients="";
	RedactFilteredMultiChoise clientSelection;
	RedactFilteredMultiChoise nomenklatureSelection;

	Vector<CheckRow> kontragents = new Vector<CheckRow>();
	Vector<CheckRow> artikuls = new Vector<CheckRow>();

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
		System.out.println("ReportFixirovannieCeny.readForm");
		//kontragents = new Vector<CheckRow>();

		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		System.out.println("ReportFixirovannieCeny readForm " + xml);
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
		//artikul.value(b.child("artikul").value.property.value());
		String[] kods = b.child("kods").value.property.value().split(",");
		for (int ii = 0; ii < kontragents.size(); ii++) {
			CheckRow item = kontragents.get(ii);
			for (int kk = 0; kk < kods.length; kk++) {
				if (kods[kk].equals(item.id)) {
					item.checked = true;
					break;
				}
			}
		}
		clientSelection.items(this.kontragents);

		kods = b.child("arts").value.property.value().split(",");
		for (int ii = 0; ii < artikuls.size(); ii++) {
			CheckRow item = artikuls.get(ii);
			for (int kk = 0; kk < kods.length; kk++) {
				if (kods[kk].equals(item.id)) {
					item.checked = true;
					break;
				}
			}
		}
		nomenklatureSelection.items(this.artikuls);
	}

	void fillKods() {
		kontragents = new Vector<CheckRow>();
		for (int ii = 0; ii < Cfg.kontragenty().children.size(); ii++) {
			Bough item = Cfg.kontragenty().children.get(ii);
			CheckRow value = new CheckRow(false, item.child("kod").value.property.value()
					, item.child("kod").value.property.value()
					+ ": " + item.child("naimenovanie").value.property.value());
			kontragents.add(value);
		}
	}

	void fillArts() {
		artikuls = new Vector<CheckRow>();
		String sql = "select artikul as art, naimenovanie as name from nomenklatura_sorted nn where nn.EtoGruppa=x'00' ;";
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		for (int ii = 0; ii < data.children.size(); ii++) {
			Bough item = data.children.get(ii);
			CheckRow value = new CheckRow(false, item.child("art").value.property.value()
					, item.child("art").value.property.value()
					+ ": " + item.child("name").value.property.value());
			artikuls.add(value);
		}
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("territory").value.is("" + territory.value());
		//b.child("artikul").value.is(artikul.value());
		String kods = "";
		String delimiter = "";
		for (int ii = 0; ii < this.kontragents.size(); ii++) {
			if (this.kontragents.get(ii).checked) {
				kods = kods + delimiter + this.kontragents.get(ii).id;
				delimiter = ",";
			}
		}
		//kods="153481";
		b.child("kods").value.is(kods);
		kods = "";
		delimiter = "";
		for (int ii = 0; ii < this.artikuls.size(); ii++) {
			if (this.artikuls.get(ii).checked) {
				kods = kods + delimiter + this.artikuls.get(ii).id;
				delimiter = ",";
			}
		}
		b.child("arts").value.is(kods);
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		System.out.println("ReportFixirovannieCeny writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();
		//b.child("artikul").value.is("");
		b.child("territory").value.is("0");
		b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind) {
		//https://service.swlife.ru/hrc120107/hs/Report/ЗаявкиНаФиксированныеЦены/supernn_hrc?param={"ДатаНачала":"20230920","ДатаОкончания":"20230920","Контрагенты":["87687"],"Номенклатура":["112480"]}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String p = "{\"ДатаНачала\":\"" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd") + "\""//
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd") + "\""//
				;
		String list = "";
		String delimiter = "";
		for (int a = 0; a < kontragents.size(); a++) {
			if (kontragents.get(a).checked) {
				list = list + delimiter + "\"" + kontragents.get(a).id + "\"";
				delimiter = ",";
			}
		}
		if (list.length() > 0) {
			p = p + ",\"Контрагенты\":[" + list + "]";
		}
		list = "";
		delimiter = "";
		for (int a = 0; a < artikuls.size(); a++) {
			if (artikuls.get(a).checked) {
				list = list + delimiter + "\"" + artikuls.get(a).id + "\"";
				delimiter = ",";
			}
		}
		if (list.length() > 0) {
			p = p + ",\"Номенклатура\":[" + list + "]";
		}
		p = p + "}";
		String serviceName = "ЗаявкиНаФиксированныеЦены";
		String q = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Report/"
				+ serviceName + "/" + hrc + "?param=" + p
				+ tagForFormat(queryKind);
		return q;
	}

	//@Override
	public String __composeRequest() {
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
		/*if (artikul.value().length() > 1) {
			xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>Номенклатура</Name>"//
					+ "\n							<Value>" + artikul.value() + "</Value>"//
					+ "\n							<Tipe>Массив</Tipe>"//
					+ "\n							<TipeElem>Строка</TipeElem>"//
					+ "\n						</Param>"//
			;
		}*/
		for (int ii = 0; ii < nomenklatureSelection.rows.size(); ii++) {
			if (nomenklatureSelection.rows.get(ii).checked) {
				xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
						+ "\n							<Name>Номенклатура</Name>"//
						+ "\n							<Value>" + nomenklatureSelection.rows.get(ii).id + "</Value>"//
						+ "\n							<Tipe>Массив</Tipe>"//
						+ "\n							<TipeElem>Строка</TipeElem>"//
						+ "\n						</Param>"//
				;
			}
		}
		for (int ii = 0; ii < clientSelection.rows.size(); ii++) {
			if (clientSelection.rows.get(ii).checked) {
				xml = xml + "\n						<Param xmlns=\"http://ws.swl/Param\">"//
						+ "\n							<Name>Контрагент</Name>"//
						+ "\n							<Value>" + clientSelection.rows.get(ii).id + "</Value>"//
						+ "\n							<Tipe>Массив</Tipe>"//
						+ "\n							<TipeElem>Строка</TipeElem>"//
						+ "\n						</Param>"//
				;
			}
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
		System.out.println("ReportFixirovannieCeny.getParametersView");
		if (propertiesForm == null) {
			fillKods();
			clientSelection = new RedactFilteredMultiChoise(context);
			fillArts();
			nomenklatureSelection = new RedactFilteredMultiChoise(context);
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
					//.input(context, 4, Auxiliary.tapSize * 0.3, "Номенклатура", new RedactText(context).text.is(artikul))//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Номенклатура", nomenklatureSelection.items(artikuls), 9 * Auxiliary.tapSize)
					.input(context, 5, Auxiliary.tapSize * 0.3, "Контрагент", clientSelection.items(kontragents)
							/*.item(false,"1","srnn edyje 7 jkjeymj")
									.item(false,"2","dertyjn dr678k rt8ke 6j")
									.item(false,"3","seh s5ftyuk tyku dg")
									.item(true,"4","dft7yu gi, dcgfyhmn")
									.item(false,"5","yukftyuk dae hzae hzet ")
									.item(false,"6","567jftgymhu tym gf")
									.item(false,"7","ksmmsmsooaseasrbgsebrhstbnte")
									.item(true,"8","de6rtyjn dr678k rt8ke 6j")
									.item(false,"9","seh s5ft5 yuk tyku dg")
									.item(false,"10","dft457 7yu gi, dcgfyhmn")
									.item(false,"11","yukf 4e7tyuk dae hzae hzet ")
									.item(false,"12","567jftg47 ymhu tym gf")
									.item(false,"13","ksmmsmsooa74seasrbgsebrhstbnte")*/
							, 9 * Auxiliary.tapSize)//
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
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportFixirovannieCeny.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Утвердить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							ReportFixirovannieCeny.this.promptApprove(context);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 6 + 3 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

	void promptApprove(Context context) {
		int i = territory.value().intValue();
		String artText = "";
		String delimiter = "";
		for (int ii = 0; ii < nomenklatureSelection.rows.size(); ii++) {
			if (nomenklatureSelection.rows.get(ii).checked) {
				artText = artText + delimiter + "\"" + nomenklatureSelection.rows.get(ii).id + "\"";
				delimiter = ",";
			}
		}
		String kodsText = "";
		delimiter = "";
		for (int ii = 0; ii < clientSelection.rows.size(); ii++) {
			if (clientSelection.rows.get(ii).checked) {
				kodsText = kodsText + delimiter + clientSelection.rows.get(ii).id;
				delimiter = ",";
			}
		}
		if (kodsText.length() < 1 && artText.length() < 1) {
			Auxiliary.warn("Не выбраны ни номенклатура, ни контрагенты", context);
		} else {
			String msg = "Утвердить заявки на фикс цены\n";
			if (kodsText.length() > 0) {
				msg = msg + "- контрагенты: " + kodsText + "\n";
			}
			if (artText.length() > 0) {
				msg = msg + "- артикулы: " + artText + "\n";
			}
			msg = msg + "- период с " + Cfg.formatMills(dateCreateFrom.value(), "dd.MM.yy") + " по " + Cfg.formatMills(dateCreateTo.value(), "dd.MM.yy");
			//Auxiliary.warn(msg, context);
			//kodsText = "[" + kodsText + "]";
			//artText = "[" + artText + "]";
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/fixprice/UtvVse";
			System.out.println(url);

			String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
			String sql = "select pz.kod as kod from Polzovateli pp join Podrazdeleniya pz on pp.podrazdelenie=pz._idrref where pp.kod='" + hrc + "'";
			Bough rawdata = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			String podrkod = rawdata.child("row").child("kod").value.property.value();
			String body = "{\"НачДата\":\"" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd") + "\"";
			body = body + ",\"КонДата\":\"" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd") + "\"";
			//body = body + ",\"hrc\":\"" + Cfg.whoCheckListOwner() + "\"";
			body = body + ",\"hrc\":\"" + Cfg.whoCheckListOwner() + "\"";
			body = body + ",\"Подразделение\":\"" + podrkod + "\"";
			if (kodsText.length() > 0) {
				body = body + ",\"Контрагенты\":[" + kodsText + "]";
			}
			if (artText.length() > 0) {
				body = body + ",\"Номенклатура\":[" + artText + "]";
			}
			body = body + "}";
			final String data = body;

			Auxiliary.pickConfirm(context, msg, "Утвердить", new Task() {
				public void doTask() {
					sendApproveAll(context, url, data);
				}
			});
		}
	}

	public static void sendApproveAll(Context context, String url, String data) {
		final Note res = new Note();
		new Expect().task.is(new Task() {
			public void doTask() {
				System.out.println("sendApproveAll");
				System.out.println("url: " + url);
				System.out.println("body: " + data);
				System.out.println("login: " + Cfg.whoCheckListOwner());
				System.out.println("password: " + Cfg.hrcPersonalPassword());
				Bough bb = Auxiliary.loadTextFromPrivatePOST(url, data.getBytes(), 1000 * 60 * 5
						, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
						//,"bot28","Molgav1024"
						, true
				);
				System.out.println("sendApproveAll " + bb.dumpXML());
				res.value(bb.child("message").value.property.value());

			}
		}).afterDone.is(new Task() {
			public void doTask() {
				Auxiliary.warn(res.value(), context);
			}
		})
				.start(context);

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
