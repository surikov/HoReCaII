
package sweetlife.android10.supervisor;

        import java.io.File;
        import java.net.URLEncoder;
        import java.util.Date;

        import reactive.ui.Auxiliary;
        import reactive.ui.Decor;
        import reactive.ui.Knob;
		import reactive.ui.RedactSingleChoice;
		import reactive.ui.SubLayoutless;
		import sweetlife.android10.Settings;
        import tee.binding.Bough;
        import tee.binding.it.Numeric;
        import tee.binding.task.Task;
        import android.content.Context;

public class ReportVestnik extends Report_Base {
    Numeric territory = new Numeric();
    public static String menuLabel() {
        return "Вестник";
    }
    public static String folderKey() {
        return "vestnik";
    }
    public  String getMenuLabel() {
        return "Вестник";
    }
    public  String getFolderKey() {
        return "vestnik";
    }
    public ReportVestnik(ActivityWebServicesReports p) {
        super(p);
    }
    @Override
    public String getShortDescription(String key) {
        return "Вестник";
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
        territory.value(Numeric.string2double(b.child("territory").value.property.value()));
    }
    @Override
    public void writeForm(String instanceKey) {
        Bough b = new Bough().name.is(getFolderKey());
        b.child("territory").value.is("" + territory.value());
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
        //System.out.println("writeForm " + xml);
        Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
    }
    @Override
    public void writeDefaultForm(String instanceKey) {
        Bough b = new Bough().name.is(getFolderKey());
        long d = new Date().getTime();
        b.child("territory").value.is("0");
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" + b.dumpXML();
        Auxiliary.writeTextToFile(new File(Cfg.pathToXML(getFolderKey(), instanceKey)), xml, "utf-8");
    }
    @Override
    public String composeRequest() {
        return null;
        /*int i = territory.value().intValue();
        String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
        String xml = ""//
                + "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"//
                + "\n		<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
                + "\n			<soap:Body>"//
                + "\n				<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">"//
                + "\n					<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
                + "ТекущиеЛимитыТП"//
                + "</m:Имя>"//
                + "\n					<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(0, "yyyy-MM-dd") + "T00:00:00</m:НачалоПериода>"//
                + "\n					<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.formatMills(0, "yyyy-MM-dd") + "T23:59:59</m:КонецПериода>"//
                + "\n					<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + hrc + "</m:КодПользователя>"//
                + "\n					<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"//
                + "\n					</m:Параметры>"//
                + "\n					<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + Cfg.hrc_imei() + "</m:IMEI>"//
                + "\n				</m:getReport>" //
                + "\n			</soap:Body>"//
                + "\n		</soap:Envelope>";
        //System.out.println(xml);
        return xml;*/
    }

    @Override
    public String composeGetQuery(int queryKind) {
        int i = territory.value().intValue();
        String hrc = Cfg.territory().children.get(i).child("hrc").value.property.value().trim();
         String p = "{" //
                + "\"ВариантОтчета\":\"ДляПланшета\""//
                + "}";
        String e = "";
        try {
            e = URLEncoder.encode(p, "UTF-8");
        } catch(Throwable t) {
            t.printStackTrace();
            e = t.getMessage();
        }
        String serviceName = "Вестник";
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
            propertiesForm//
                    .input(context, 0, Auxiliary.tapSize * 0.3, "", new Decor(context).labelText.is(getMenuLabel()).labelStyleLargeNormal(), Auxiliary.tapSize * 9)//
                    .input(context, 1, Auxiliary.tapSize * 0.3, "Территория", terr)//
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
                    .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 + 0.5)))//
                    .width().is(Auxiliary.tapSize * 2.5)//
                    .height().is(Auxiliary.tapSize * 0.8)//
            );
            propertiesForm.child(new Knob(context)//
                    .labelText.is("Удалить")//
                    .afterTap.is(new Task() {
                        @Override
                        public void doTask() {
                            //expectRequery.start(activityReports);
                            activityReports.promptDeleteRepoort(ReportVestnik.this, currentKey);
                        }
                    })//
                    .left().is(propertiesForm.shiftX.property.plus(Auxiliary.tapSize * (0.3 + 0 * 2.5)))//
                    .top().is(propertiesForm.shiftY.property.plus(Auxiliary.tapSize * (1.5 * 2 +1+ 0.5)))//
                    .width().is(Auxiliary.tapSize * 2.5)//
                    .height().is(Auxiliary.tapSize * 0.8)//
            );
        }
        return propertiesForm;
    }

}
