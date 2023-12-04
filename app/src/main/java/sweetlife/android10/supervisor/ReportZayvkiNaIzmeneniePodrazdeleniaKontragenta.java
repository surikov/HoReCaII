package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.text.*;
import java.util.*;

import reactive.ui.Auxiliary;
import reactive.ui.*;
import reactive.ui.Knob;
import reactive.ui.RedactDate;
import reactive.ui.RedactSingleChoice;
import reactive.ui.SubLayoutless;
import sweetlife.android10.*;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.Task;

import android.content.Context;

public class ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta extends Report_Base{
	Numeric dateCreateFrom = new Numeric();
	Numeric dateCreateTo = new Numeric();

	public ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta(ActivityWebServicesReports p){
		super(p);
	}

	public static String menuLabel(){
		return "Заявки на изменение подразделения контрагента";
	}

	public static String folderKey(){
		return "zayvkiNaIzmeneniePodrazdeleniaKontragenta";
	}

	public String getMenuLabel(){
		return "Заявки на изменение подразделения контрагента";
	}

	public String getFolderKey(){
		return "zayvkiNaIzmeneniePodrazdeleniaKontragenta";
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String dfrom = Cfg.formatMills(Numeric.string2double(b.child("docFrom").value.property.value()), "dd.MM.yyyy");
			String dto = Cfg.formatMills(Numeric.string2double(b.child("docTo").value.property.value()), "dd.MM.yyyy");
			return dfrom + " - " + dto;
		}catch(Throwable t){
			//
		}
		return "?";
	}

	@Override
	public String getOtherDescription(String key){
		return "";
	}

	@Override
	public void readForm(String instanceKey){
		//System.out.println("readForm-----------------------------------");
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		try{
			b = Bough.parseXML(xml);

		}catch(Throwable t){
			//
		}
		if(b == null){
			b = new Bough();
		}

		dateCreateFrom.value(Numeric.string2double(b.child("docFrom").value.property.value()));
		dateCreateTo.value(Numeric.string2double(b.child("docTo").value.property.value()));

	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());

		b.child("docFrom").value.is("" + dateCreateFrom.value());
		b.child("docTo").value.is("" + dateCreateTo.value());

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		long d = new Date().getTime();

		b.child("docFrom").value.is("" + (d - 0 * 24 * 60 * 60 * 1000.0));
		b.child("docTo").value.is("" + (d + 0 * 24 * 60 * 60 * 1000.0));

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeGetQuery(int queryKind){
		String serviceName = "ЗаявкиНаИзменениеПодразделенияКонтрагента";
		String p = "{\"ДатаНачала\":\"" + Cfg.formatMills(dateCreateFrom.value(), "yyyyMMdd") + "\""
				+ ",\"ДатаОкончания\":\"" + Cfg.formatMills(dateCreateTo.value(), "yyyyMMdd") + "\""
				+ "}";
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" + Cfg.whoCheckListOwner()//
				+ "?param=" + p//
				;
		q = q + tagForFormat(queryKind);
		System.out.println("composeGetQuery " + q);
		return q;
	}

	@Override
	public String composeRequest(){
		return null;
	}


	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){


			propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 19)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Период с", new RedactDate(context).date.is(dateCreateFrom).format.is("dd.MM.yyyy"))//
					.input(context, 2, Auxiliary.tapSize * 0.3, "по", new RedactDate(context).date.is(dateCreateTo).format.is("dd.MM.yyyy"))//
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("Добавить заявку")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							addNewItem(context);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 0 + 0.5)))//
					.width().is(Auxiliary.tapSize * 3)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 1 + 0.5)))//
					.width().is(Auxiliary.tapSize * 3)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);

			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportZayvkiNaIzmeneniePodrazdeleniaKontragenta.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 2 + 0.5)))//
					.width().is(Auxiliary.tapSize * 3)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);


		}

		return propertiesForm;
	}

	void sendNewItem(Context context, String kodKlienta, String kodPodrazd, String monthDate, final String cmnt){
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()//
				+ "/hs/ZayavkaNaKlienta/IzmPodr"//
				+ "/" + kodKlienta//
				+ "/" + kodPodrazd//
				+ "/" + monthDate//
				;
		System.out.println(url);
		System.out.println(cmnt);
		Note msg = new Note();
		new Expect()
				.task.is(new Task(){
			@Override
			public void doTask(){
				try{
					Bough b = Auxiliary.loadTextFromPrivatePOST(url
							, cmnt.getBytes("UTF-8"), 32000
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
							//,"bot28","Molgav1024"
							, true
					);
					System.out.println(b.dumpXML());
					msg.value(b.child("message").value.property.value());
				}catch(Throwable t){
					msg.value("Ошибка отправки: " + t.getMessage());
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
					Auxiliary.warn("Результат: " + msg.value(), context);
				expectRequery.start(activityReports);
			}
		}).status.is("Ждите...").start(context);

	}

	void addNewItem(Context context){
		/*
https://service.swlife.ru/hrc120107/hs/ZayavkaNaKlienta/IzmPodrr/269866/ууу84/20231001
3 параметра:
269866 – код клиента
ууу84 – код нового подразделения
20231001 - с какого месяца

Тело запроса:
{"Prichina":"жжжжж ььььььь жж"}

		*/
		String sql = "select kontragenty.kod as kod,kontragenty.naimenovanie as naimenovanie from kontragenty"//
				+ " left join DogovoryKontragentov on DogovoryKontragentov.vladelec=kontragenty._idrref"//
				+ " left join Podrazdeleniya on Podrazdeleniya._idrref=DogovoryKontragentov.podrazdelenie"//
				+ " group by kontragenty.kod"//
				+ " order by kontragenty.naimenovanie";
		final Bough kontragentyRows = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		Numeric who = new Numeric();
		RedactFilteredSingleChoice kontragent = new RedactFilteredSingleChoice(context);
		kontragent.selection.is(who);
		for(int i = 0; i < kontragentyRows.children.size(); i++){
			kontragent.item(kontragentyRows.children.get(i).child("kod").value.property.value()
					+ ": " + kontragentyRows.children.get(i).child("naimenovanie").value.property.value());
		}
		sql = "select p1.kod as kod"
				+ "\n	,ifnull(p3.naimenovanie,'') || '/' || ifnull(p2.naimenovanie,'') || '/' || ifnull(p1.naimenovanie,'') as path"
				+ "\n from Podrazdeleniya p1"
				+ "\n	left join Podrazdeleniya p2 on p2._idrref=p1.roditel"
				+ "\n	left join Podrazdeleniya p3 on p3._idrref=p2.roditel"
				+ "\n	left join Podrazdeleniya p4 on p4._idrref=p3.roditel"
				+ "\n	left join Podrazdeleniya p5 on p5._idrref=p4.roditel"
				+ "\n where p1.EtoGruppa=x'00' and (p5.naimenovanie='HoReCa' or p4.naimenovanie='HoReCa' or p3.naimenovanie='HoReCa')"
				+ "\n order by p4.naimenovanie,p3.naimenovanie,p2.naimenovanie,p1.naimenovanie";
		final Bough terrRows = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		Numeric terr = new Numeric();
		RedactFilteredSingleChoice terrs = new RedactFilteredSingleChoice(context);
		terrs.selection.is(terr);
		for(int i = 0; i < terrRows.children.size(); i++){
			terrs.item(terrRows.children.get(i).child("path").value.property.value());
		}
		Numeric when = new Numeric().value((double)Calendar.getInstance().getTimeInMillis());
		Note txt = new Note();
		Auxiliary.pick(context, "Заявка на смену территории", new SubLayoutless(context)
						.child(kontragent.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 11).height().is(Auxiliary.tapSize * 0.7))
						.child(terrs.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 11).height().is(Auxiliary.tapSize * 0.7))
						.child(new RedactDate(context).date.is(when).format.is("MM.yyyy").left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 11).height().is(Auxiliary.tapSize * 0.7))
						.child(new RedactText(context).text.is(txt).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 11).height().is(Auxiliary.tapSize * 0.7))
						.width().is(Auxiliary.tapSize * 12)//
						.height().is(Auxiliary.tapSize * 7)//
				, "Отправить", new Task(){
					@Override
					public void doTask(){
						sendNewItem(context
								, kontragentyRows.children.get(who.value().intValue()).child("kod").value.property.value()
								, terrRows.children.get(terr.value().intValue()).child("kod").value.property.value()
								, (new SimpleDateFormat("yyyyMM")).format(new Date(when.value().longValue())) + "01"
								, "{\"Prichina\":\"" + txt.value().replace("\"", "'") + "\"}"
						);
					}
				}, null, null, null, null);
	}

	@Override
	public String interceptActions(String s){
		String[] strings = s.split("\n");
		for(int i = 0; i < strings.length; i++){
			String line = strings[i];
			if(i - 2 > -1 && i + 1 < strings.length - 1){
				String doc = extract(line, '№', '<');
				if(doc.length() > 2){
					int start = line.indexOf('№');
					int end = line.indexOf("<", start + 1);
					line = line.substring(0, start)//
							+ "<a href=\"hook"//
							+ "?kind=" + HOOKReportApproveTerrChange//
							+ "&" + FIELDDocumentNumber + "=" + doc
							+ "\">№" + doc + "</a>"
							+ line.substring(end);
					strings[i] = line;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			sb.append(strings[i]);
		}
		return sb.toString();
	}
}

