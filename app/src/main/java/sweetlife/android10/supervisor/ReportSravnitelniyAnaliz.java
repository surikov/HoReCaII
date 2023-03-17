package sweetlife.android10.supervisor;

		import java.io.File;
		import java.net.URLEncoder;
		import java.util.Date;

		import reactive.ui.Auxiliary;
		import reactive.ui.*;
		import reactive.ui.Knob;
		import reactive.ui.RedactDate;
		import reactive.ui.RedactSingleChoice;
		import reactive.ui.SubLayoutless;
		import sweetlife.android10.Settings;
		import tee.binding.Bough;
		import tee.binding.it.*;
		import tee.binding.task.Task;

		import android.content.Context;

public class ReportSravnitelniyAnaliz extends Report_Base {

	Numeric territory = new Numeric();
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric whoPlus1 = new Numeric();
	Toggle poVibrannymMesyasam=new Toggle();

	public static String menuLabel() {
		return "Сравнительный анализ";
	}

	public static String folderKey() {
		return "sravnitelniyanaliz";
	}

	public String getMenuLabel() {
		return "Сравнительный анализ";
	}

	public String getFolderKey() {
		return "sravnitelniyanaliz";
	}

	public ReportSravnitelniyAnaliz(ActivityWebServicesReports p) {
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
			String p = dfrom+" - "+dto;
			return p;
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
			String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
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
		try {
			b = Bough.parseXML(xml);

		} catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
		poVibrannymMesyasam.value(b.child("invalid").value.property.value().equals("true") ? true : false);
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("territory").value.is("" + territory.value());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());
		b.child("who").value.is("" + whoPlus1.value());
		b.child("invalid").value.is(poVibrannymMesyasam.value() ? "true" : "false");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		Date firstDay = new Date();
		firstDay.setDate(1);
		b.child("docFrom").value.is("" + (firstDay.getTime() - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (new Date().getTime() + 0 * 24 * 60 * 60 * 1000.0));
		b.child("territory").value.is("" + (Cfg.territory().children.size() - 1));
		b.child("who").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind) {
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String kontragent="";

		Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
		int nn = whoPlus1.value().intValue();
		if(nn>0) {
			nn=nn-1;
			nn = (nn < 0 || nn >= kontragenty.children.size()) ? 0 : nn;
			String kod = kontragenty.children.get(nn).child("kod").value.property.value();
			kontragent=",\"Контрагент\":\"" + kod + "\"";
		}

		String time=",\"ПоВыбраннымМесяцам\":\"" + (poVibrannymMesyasam.value() ? "Истина" : "Ложь") + "\"";

		String parameters = "{\"ВариантОтчета\":\"ВаловаяПрибыльПланшетПоПериодам\""
				+ ",\"ДатаНачала\":\"" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd")+ "\""
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd")+ "\""
				+kontragent
				+time
				+ "}";
		String encodedparameters = "";
		try {
			encodedparameters = URLEncoder.encode(parameters, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			encodedparameters = t.getMessage();
		}
		String serviceName = "ВаловаяПрибыль";
		String q = Settings.getInstance().getBaseURL()//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" + hrc + "?param=" + encodedparameters ;
		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public String composeRequest() {
		return null;
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
			RedactSingleChoice kontr = new RedactSingleChoice(context);
			kontr.selection.is(whoPlus1);
			Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
			kontr.item("[Все контрагенты]");
			for (int i = 0; i < kontragenty.children.size(); i++) {
				kontr.item(kontragenty.children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Контрагент", kontr)//
					.input(context, 4.75, Auxiliary.tapSize * 0.3, "", new RedactToggle(context).labelText.is("по выбранным месяцам").yes.is(poVibrannymMesyasam))//
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
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5.5 + 0 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);

			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task() {
						@Override
						public void doTask() {
							activityReports.promptDeleteRepoort(ReportSravnitelniyAnaliz.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 5.5 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
}

