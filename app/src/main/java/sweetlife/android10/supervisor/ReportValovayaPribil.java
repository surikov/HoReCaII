package sweetlife.android10.supervisor;
import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.Auxiliary;
import reactive.ui.Decor;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.*;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;
import android.content.Context;
public class ReportValovayaPribil  extends Report_Base {
	Numeric territory = new Numeric();
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();
	Numeric whoPlus1 = new Numeric();
	public static String menuLabel() {
		return "Валовая прибыль";
	}
	public static String folderKey() {
		return "valovayapribil";
	}
	public  String getMenuLabel() {
		return "Валовая прибыль";
	}
	public  String getFolderKey() {
		return "valovayapribil";
	}
	public ReportValovayaPribil(ActivityWebServicesReports p) {
		super(p);
	}
	@Override
	public String getShortDescription(String key) {
		try {
			Bough b = null;
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		}
		catch (Throwable t) {
			//
		}
		return "Валовая прибыль";
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
		try {
			b = Bough.parseXML(xml);
			dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
			dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));
		}
		catch (Throwable t) {
			//
		}
		if (b == null) {
			b = new Bough();
		}
		territory.value(Numeric.string2double(b.child("territory").value.property.value()));
		whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
	}
	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("territory").value.is("" + territory.value());
		b.child("docFrom").value.is("" + dateCreateFrom.value());
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
		Date mb = new Date();
		mb.setDate(1);
		b.child("docFrom").value.is("" + mb.getTime());
		d = new Date().getTime();
		b.child("docTo").value.is("" + d);
		b.child("territory").value.is("0");
		b.child("who").value.is("0");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}
	@Override
	public String composeRequest() {
		return null;
	}
	private String kod(int nn) {
		String r = "-123456789";
		if(nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}
	@Override
	public String composeGetQuery(int queryKind) {
		//https://service.swlife.ru/hrc120107/hs/Report/ВаловаяПрибыль/hrc495?param={"ВариантОтчета":"ВаловаяПрибыльПланшет"}
		int i = territory.value().intValue();
		String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		String p = "{"
				+"\"ВариантОтчета\":\"ВаловаяПрибыльПланшет\""
				+",\"ДатаНачала\":\""+Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd")+"\""
				+",\"ДатаОкончания\":\""+Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd")+"\"";
		if(whoPlus1.value().intValue() > 0) {
			int nn = whoPlus1.value().intValue() - 1;
			p=p+		",\"Контрагент\":\""+kod(nn)+"\"";
		}

		p=p+		"}";
		String e = p;
		/*try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}*/
		String serviceName = "ВаловаяПрибыль";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" //+ Cfg.currentHRC()//
				+ hrc + "?param=" + e//
				;
		q=q+tagForFormat( queryKind);
		//System.out.println("composeGetQuery " + q);
		return q;
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
			RedactFilteredSingleChoice kontr = new RedactFilteredSingleChoice(context);
			kontr.selection.is(whoPlus1);
			kontr.item("[Все контрагенты]");
			for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontr.item(Cfg.kontragenty().children.get(i).child("kod").value.property.value()+": "+Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Территория", terr)//
					.input(context, 2, Auxiliary.tapSize * 0.3, "Дата от", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 3, Auxiliary.tapSize * 0.3, "до", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
					.input(context, 4, Auxiliary.tapSize * 0.3, "Контрагент", kontr, Auxiliary.tapSize * 9)//
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
							activityReports.promptDeleteRepoort(ReportValovayaPribil.this, currentKey);
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
