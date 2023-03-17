package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.*;
import sweetlife.android10.Settings;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.*;

public class ReportAcciiDlyaKlientov extends Report_Base {

	Toggle tolkoIndiv = new Toggle();
	Numeric who = new Numeric();

	public ReportAcciiDlyaKlientov(ActivityWebServicesReports p) {
		super(p);
	}

	public static String menuLabel() {
		return "Акции для клиентов";
	}

	public static String folderKey() {
		return "acciiDlyaKlientov";
	}

	public String getMenuLabel() {
		return "Акции для клиентов";
	}

	public String getFolderKey() {
		return "acciiDlyaKlientov";
	}

	@Override
	public String getOtherDescription(String key) {
		try {
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
			Bough b = Bough.parseXML(xml);
			//Bough kontragenty= Cfg.kontragenty();
			Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
			//if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
			//if (Cfg.isChangedHRC()) {
				//kontragenty= Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
				//kontragenty= Cfg.kontragentyByKod(Cfg.selectedKodPodrazdelenia());
			//}
			int nn=(int)Numeric.string2double(b.child("who").value.property.value());
					//whoPlus1.value().intValue();
			if(nn >= 0 && nn < kontragenty.children.size()) {

			}else{
				nn=0;
			}
			String name=kontragenty.children.get(nn).child("naimenovanie").value.property.value();
			//System.out.println("---getOtherDescription "+whoPlus1.value());
			return name;
		} catch(Throwable t) {
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
			String tolko = b.child("tolkoIndiv").value.property.value().equals("yes")?"только индивидуальные":"все";
//(tolkoIndiv.value()?"Истина":"Ложь")
			return tolko;
		} catch(Throwable t) {
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
		} catch(Throwable t) {
			//
		}
		if(b == null) {
			b = new Bough();
		}
		tolkoIndiv.value(b.child("tolkoIndiv").value.property.value().equals("yes"));
		who.value(Numeric.string2double(b.child("who").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("tolkoIndiv").value.is("" + (tolkoIndiv.value()?"yes":""));
		b.child("who").value.is("" + who.value());
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey) {
		Bough b = new Bough().name.is(getFolderKey());
		b.child("who").value.is("0");
		b.child("tolkoIndiv").value.is("no");
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	private String kod(int nn) {
		String r = "-123456789";
		if(nn >= 0 && nn < Cfg.kontragenty().children.size()) {
			r = Cfg.kontragenty().children.get(nn).child("kod").value.property.value();
		}
		return r;
	}

	@Override
	public String composeRequest() {
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind) {
		//int i = territory.value().intValue();
		//String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
		Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
		/*Bough kontragenty= Cfg.kontragenty();
		if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
			kontragenty= Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
		}*/
		int nn=who.value().intValue();
		nn=(nn < 0 ||nn >= kontragenty.children.size())?0:nn;

		String kod=kontragenty.children.get(nn).child("kod").value.property.value();
		String p = "{"
				           +"\"ТолькоИндивидуальные\":\"" + (tolkoIndiv.value()?"Истина":"Ложь") + "\""//
						   +",\"Контрагент\":\""+kod+"\""//
				           +"}";
		System.out.println("param: "+p);

		String e = "";

		try {
			e = URLEncoder.encode(p, "UTF-8");
		} catch(Throwable t) {
			t.printStackTrace();
			e = t.getMessage();
		}
		String serviceName="АкцииДляКлиентов";
		try {
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		} catch (Throwable t) {
			t.printStackTrace();
			serviceName=t.getMessage();
		}
		String q = Settings.getInstance().getBaseURL()//
				           //+ "GolovaNew"//
				           +Settings.selectedBase1C()//
				           + "/hs/Report/"//
				           + serviceName//
				           +"/" + Cfg.whoCheckListOwner()//
				           + "?param=" + e//
				;
		q=q+tagForFormat( queryKind);

		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public SubLayoutless getParametersView(Context context) {
		if(propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			/*RedactSingleChoice terr = new RedactSingleChoice(context);
			terr.selection.is(territory);
			for(int i = 0; i < Cfg.territory().children.size(); i++) {
				String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
				terr.item(s);
			}*/
			RedactSingleChoice kontragent = new RedactSingleChoice(context);
			kontragent.selection.is(who);
			//kontragent.item("[Все контрагенты]");
			for(int i = 0; i < Cfg.kontragenty().children.size(); i++) {
				kontragent.item(Cfg.kontragenty().children.get(i).child("naimenovanie").value.property.value());
			}
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Контрагент", kontragent)//
					//.input(context, 2, Auxiliary.tapSize * 0.3, "Только индивидуальные", tolkoIndiv)//
			.input(context, 2, Auxiliary.tapSize * 0.3, "", new RedactToggle(context).labelText.is("только индивидуальные").yes.is(tolkoIndiv))//
			//.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
			/*.input(context, 3, Auxiliary.tapSize * 0.3, "", new Knob(context).labelText.is("Обновить").afterTap.is(new Task() {
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
							//Date mb = new Date();
							//mb.setDate(1);
							long d = new Date().getTime();
							//dateFrom.value((double) d);
							//dateCreateFrom.value(d - 0 * 24 * 60 * 60 * 1000.0);
							//dateCreateTo.value(d - 0 * 24 * 60 * 60 * 1000.0);
							//checkHolding();
							expectRequery.start(activityReports);
						}
					})//
					                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 0.5)))//
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
					                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 1 + 0.5)))//
					                     .width().is(Auxiliary.tapSize * 2.5)//
					                     .height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					                     .labelText.is("Удалить")//
					                     .afterTap.is(new Task() {
						@Override
						public void doTask() {
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportAcciiDlyaKlientov.this, currentKey);
						}
					})//
					                     .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					                     .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 2 + 0.5)))//
					                     .width().is(Auxiliary.tapSize * 2.5)//
					                     .height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}
}
