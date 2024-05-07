//ReportAssortimentFlagmanov
package sweetlife.android10.supervisor;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import reactive.ui.*;
import sweetlife.android10.*;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import tee.binding.task.Task;

import android.content.Context;

public class ReportAssortimentFlagmanov extends Report_Base{
	Numeric dateRun = new Numeric();
	Numeric groupNum = new Numeric();
	static Bough foldersdata = null;

	public static String menuLabel(){
		return "Ассортимент по флагманам";//"Бонусы для ТП";
	}

	public static String folderKey(){
		return "assortimentPoFlagmanam";
	}

	public String getMenuLabel(){
		return "Ассортимент по флагманам";//"Бонусы для ТП";
	}

	public String getFolderKey(){
		return "assortimentPoFlagmanam";
	}

	public ReportAssortimentFlagmanov(ActivityWebServicesReports p){
		super(p);
	}

	@Override
	public String getShortDescription(String key){
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
		try{
			b = Bough.parseXML(xml);
			String dFrom = Cfg.formatMills(Numeric.string2double(b.child("dateRun").value.property.value()), "dd.MM.yyyy");
			return dFrom;
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
		Bough b = null;
		String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey))));
		//System.out.println("readForm " + xml);
		try{
			b = Bough.parseXML(xml);
		}catch(Throwable t){
			//
		}
		if(b == null){
			b = new Bough();
		}
		dateRun.value(Numeric.string2double(b.child("dateRun").value.property.value()));
		groupNum.value(Numeric.string2double(b.child("groupNum").value.property.value()));
	}

	@Override
	public void writeForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());

		b.child("dateRun").value.is("" + dateRun.value());
		b.child("groupNum").value.is("" + groupNum.value());

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		//System.out.println("writeForm " + xml);
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public void writeDefaultForm(String instanceKey){
		Bough b = new Bough().name.is(getFolderKey());
		//long d = new Date().getTime();
		Date mb = new Date();
		//mb.setDate(1);
		long t = mb.getTime();//+1000*60*60*24;
		b.child("dateRun").value.is("" + t);
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
		Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
	}

	@Override
	public String composeRequest(){
		return null;
	}

	@Override
	public String composeGetQuery(int queryKind){
		String pp = "{}";
		int nn = groupNum.value().intValue();
		if(nn > 0){
			if(this.foldersList() != null){
				if(nn - 1 < foldersList().children.size()){
					//pp = "{\"ДопИерархия\":\"" + foldersList().children.get(nn - 1).child("kod1").value.property.value() + "\"}";
					pp = "{\"Сегмент\":\"" + foldersList().children.get(nn - 1).child("kod").value.property.value() + "\"}";
				}
			}
		}
		String ee = "";
		try{
			ee = URLEncoder.encode(pp, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			ee = t.getMessage();
		}
		String serviceName = "АссортиментПоФлагманам";
		try{
			serviceName = URLEncoder.encode(serviceName, "UTF-8");
		}catch(Throwable t){
			t.printStackTrace();
			serviceName = t.getMessage();
		}
		//*https://service.swlife.ru/hrc120107/hs/Report/АссортиментПоФлагманам/hrc600?param={"Номенклатура":"чн180"}
		String q = Settings.getInstance().getBaseURL()//
				//+ "GolovaNew"//
				+ Settings.selectedBase1C()//
				+ "/hs/Report/"//
				+ serviceName + "/" + Cfg.DBHRC()//Cfg.whoCheckListOwner()//
				+ "?param=" + ee//
				;
		q = q + tagForFormat(queryKind);
		//System.out.println("composeGetQuery " + q);
		return q;
	}

	Bough foldersList(){
		if(foldersdata == null){
				/*String sql = "select itm.kod as kod, case when par.naimenovanie is null"
						+ "			then itm.naimenovanie"
						+ "			else itm.naimenovanie || ' / ' || par.naimenovanie"
						+ "		end as title"
						+ "	from nomenklatura itm"
						+ "		join nomenklatura par on par._idrref=itm.Roditel"
						+ "	where itm.EtoGruppa=x'01' and itm.PometkaUdaleniya=x'00'"
						+ "	order by par.naimenovanie,itm.naimenovanie;";*/
				/*
			String sql = "select * from ("
					+ "\n		select item.naimenovanie as item"
					+ "\n		,folder1.naimenovanie as folder1,folder1.kod as kod1"
					+ "\n		,folder2.naimenovanie as folder2,folder2.kod as kod2"
					+ "\n		,0 as cnt"
					+ "\n		from nomenklatura_sorted folder1"
					+ "\n		join nomenklatura_sorted item on item.roditel=folder1._idrref and item.EtoGruppa=x'00'"
					+ "\n	join nomenklatura_sorted folder2 on folder1.roditel=folder2._idrref"
					+ "\n	where folder1.EtoGruppa=x'01' and folder1.PometkaUdaleniya=x'00'"
					+ "\n	group by folder2._idrref"
					+ "\n	union select item.naimenovanie as item"
					+ "\n		,folder1.naimenovanie as folder1,folder1.kod as kod1"
					+ "\n		,folder2.naimenovanie as folder2,folder2.kod as kod2"
					+ "\n		,count(item._idrref) as cnt"
					+ "\n	from nomenklatura_sorted folder1"
					+ "\n	join nomenklatura_sorted item on item.roditel=folder1._idrref and item.EtoGruppa=x'00'"
					+ "\n	left join nomenklatura_sorted folder2 on folder1.roditel=folder2._idrref"
					+ "\n	where folder1.EtoGruppa=x'01' and folder1.PometkaUdaleniya=x'00'"
					+ "\n	group by folder1._idrref"
					+ "\n	) order by folder2,cnt;";
			*/
			String sql = "select segmentkod as kod, segmentname as name from PlanSegmentov group by Segmentkod order by segmentname";
			foldersdata = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		}
		return foldersdata;
	}

	@Override
	public SubLayoutless getParametersView(Context context){
		if(propertiesForm == null){
			RedactFilteredSingleChoice arts = new RedactFilteredSingleChoice(context);
			arts.selection.is(this.groupNum);

			arts.item("[Все]");
			for(int ii = 0; ii < foldersList().children.size(); ii++){
				//arts.item(data.children.get(ii).child("title").value.property.value() + ", код " + data.children.get(ii).child("kod").value.property.value() + "");
				String label = foldersList().children.get(ii).child("name").value.property.value();
				/*
				if(foldersList().children.get(ii).child("cnt").value.property.value().equals("0")){
					label = foldersList().children.get(ii).child("folder2").value.property.value();
				}else{
					if(foldersList().children.get(ii).child("folder2").value.property.value().length()>0){
						label = "- "+foldersList().children.get(ii).child("folder1").value.property.value();
					}else{
						label = foldersList().children.get(ii).child("folder1").value.property.value();
					}
				}
				*/
				//label=foldersList().children.get(ii).child("folder1").value.property.value()+foldersList().children.get(ii).child("folder2").value.property.value();

				arts.item(label);
			}
			propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
					.input(context, 1, Auxiliary.tapSize * 0.3, "Группа номенклатуры", arts, Auxiliary.tapSize * 9)//
			;
			propertiesForm.child(new Knob(context)//
					.labelText.is("Обновить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							expectRequery.start(activityReports);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 1 + 2)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
			propertiesForm.child(new Knob(context)//
					.labelText.is("Удалить")//
					.afterTap.is(new Task(){
						@Override
						public void doTask(){
							//expectRequery.start(activityReports);
							activityReports.promptDeleteRepoort(ReportAssortimentFlagmanov.this, currentKey);
						}
					})//
					.left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
					.top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 1 + 1 + 2)))//
					.width().is(Auxiliary.tapSize * 2.5)//
					.height().is(Auxiliary.tapSize * 0.8)//
			);
		}
		return propertiesForm;
	}

}
