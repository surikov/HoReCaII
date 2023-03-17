package sweetlife.android10.supervisor;

import java.io.*;

import reactive.ui.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.*;

public class ReportRekomendaciiKlientam extends Report_Base {

    //Numeric dateFrom = new Numeric();
    //Numeric dateTo = new Numeric();
    Numeric territory = new Numeric();
    Numeric whoPlus1 = new Numeric();

    public ReportRekomendaciiKlientam(ActivityWebServicesReports p) {
        super(p);
    }

    public static String menuLabel() {
        return "Рекомендации клиентам";
    }

    public static String folderKey() {
        return "rekomendaciiKlientam";
    }

    public String getMenuLabel() {
        return "Рекомендации клиентам";
    }

    public String getFolderKey() {
        return "rekomendaciiKlientam";
    }

    @Override
    public String getShortDescription(String key) {
        return "-";
    }

    @Override
    public String getOtherDescription(String key) {
        try {
            String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Cfg.pathToXML(getFolderKey(), key))));
            Bough b = Bough.parseXML(xml);
			Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
           /* Bough kontragenty = Cfg.kontragenty();
            if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
                kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
            }*/
            int nn = (int) Numeric.string2double(b.child("who").value.property.value());
            //whoPlus1.value().intValue();
            if (nn >= 0 && nn < kontragenty.children.size()) {

            } else {
                nn = 0;
            }
            String name = kontragenty.children.get(nn).child("naimenovanie").value.property.value();
            //System.out.println("---getOtherDescription "+whoPlus1.value());
            return name;
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
        whoPlus1.value(Numeric.string2double(b.child("who").value.property.value()));
        territory.value(Numeric.string2double(b.child("territory").value.property.value()));
    }

    @Override
    public void writeForm(String instanceKey) {
        Bough b = new Bough().name.is(getFolderKey());
        b.child("who").value.is("" + whoPlus1.value());
        b.child("territory").value.is("" + territory.value());
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
        Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
    }

    @Override
    public void writeDefaultForm(String instanceKey) {
        Bough b = new Bough().name.is(getFolderKey());
        b.child("who").value.is("0");
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
        Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
    }

    @Override
    public String composeRequest() {
        return null;
    }

    @Override
    public String composeGetQuery(int queryKind) {
		Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
        /*Bough kontragenty = Cfg.kontragenty();
        if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
            kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
        }*/
        String kontragent = "";
        int nn = whoPlus1.value().intValue();
        nn = (nn < 0 || nn >= kontragenty.children.size()) ? 0 : nn;
        String kod = kontragenty.children.get(nn).child("kod").value.property.value();

        String podr = "";
        int i = territory.value().intValue();
        if(Cfg.territory().children.size()>i) {
            System.out.println(Cfg.territory().children.get(i).dumpXML());
            podr = Cfg.territory().children.get(i).child("kod").value.property.value().trim();
        }

        String q = "{\"КодПодразделения\":\"" + podr + "\", \"КодКлиента\":" + kod + "}";
        /*try {
            q = URLEncoder.encode(q, "UTF-8");
        } catch (Throwable t) {
            t.printStackTrace();
            q = t.getMessage();
        }*/
        q = "https://shclient.swlife.ru/SHclient/ru_RU/hs/SpreadSheet/РекомендацииКлиентам?select="+q;

        //String login = "hsreport";
        //String password = "PmIWBCQRZ5wSGb9w8Q1tb8IJE";
        System.out.println("composeGetQuery " + q);
        return q;
    }
    /*@Override
    public String serviceLogin() {
        return "hsreport";
    }
    @Override
    public String servicePassword() {
        return "PmIWBCQRZ5wSGb9w8Q1tb8IJE";
    }*/
    @Override
    public SubLayoutless getParametersView(Context context) {
        if (propertiesForm == null) {
            propertiesForm = new SubLayoutless(context);
            RedactSingleChoice terr = new RedactSingleChoice(context);
            terr.selection.is(territory);
            for (int i = 0; i < Cfg.territory().children.size(); i++) {
                String s = Cfg.territory().children.get(i).child("territory").value.property.value() + " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
                terr.item(s);
            }
            RedactSingleChoice kontr = new RedactSingleChoice(context);
            kontr.selection.is(whoPlus1);
            //kontr.item("[Все контрагенты]");
			Bough kontragenty= Cfg.kontragentyForSelectedMarshrut();
            /*Bough kontragenty = Cfg.kontragenty();
            if (ApplicationHoreca.getInstance().currentKodPodrazdelenia.length() > 0) {
                kontragenty = Cfg.kontragentyByKod(ApplicationHoreca.getInstance().currentKodPodrazdelenia);
            }*/
            //kontr.item("[Все контрагенты]");
            for (int i = 0; i < kontragenty.children.size(); i++) {
                kontr.item(kontragenty.children.get(i).child("naimenovanie").value.property.value());
            }


            propertiesForm//
                    .input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
                    .input(context, 1, Auxiliary.tapSize * 0.3, "Контрагент", kontr)//
                    .input(context, 2, Auxiliary.tapSize * 0.3, "Территория", terr)//
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
                    .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 0.5)))//
                    .width().is(Auxiliary.tapSize * 2.5)//
                    .height().is(Auxiliary.tapSize * 0.8)//
            );
            propertiesForm.child(new Knob(context)//
                    .labelText.is("Удалить")//
                    .afterTap.is(new Task() {
                        @Override
                        public void doTask() {
                            //expectRequery.start(activityReports);
                            activityReports.promptDeleteRepoort(ReportRekomendaciiKlientam.this, currentKey);
                        }
                    })//
                    .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
                    .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 3 + 1 + 0.5)))//
                    .width().is(Auxiliary.tapSize * 2.5)//
                    .height().is(Auxiliary.tapSize * 0.8)//
            );
        }
        return propertiesForm;
    }
}
