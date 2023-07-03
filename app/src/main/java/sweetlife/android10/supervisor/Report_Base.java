package sweetlife.android10.supervisor;

import android.content.*;
import android.util.Base64;

import java.net.URLEncoder;
import java.util.*;

import reactive.ui.*;
import sweetlife.android10.Settings;

import tee.binding.task.*;
import tee.binding.*;

import java.io.*;

public abstract class Report_Base {

	public final static String HOOKReportPoVoditelam = "HookReportPoVoditelam";
	public final static String HOOKReportOrderState = "HookReportOrderState";
	public final static String HOOKReportFixKoordinat = "HOOKReportFixKoordinat";
	public final static String HOOKReportDeleteSpec = "HOOKReportDeleteSpec";
	public final static String HOOKReportDegustacia = "HookReportDegustacia";
	public final static String HOOKReportLimity = "HookReportLimity";
	public final static String HOOKReportVzaimoraschety = "ReportVzaimoraschety";
	public final static String HOOKReportReturnState = "HookReportReturnState";
	public final static String HOOKReportStatusSKD = "HookReportStatusSKD";
	public final static String HOOkReportPerebitieNakladnoy = "HookReportPerebitieNakladnoy";
	public final static String HOOKReportReturnAnswer = "HookReportReturnAnswer";
	public final static String FIELDDocumentDate = "documentDate";
	public final static String FIELDShipDate = "shipDate";
	//public final static String FIELDKlientKod = "klientKod";
	public final static String FIELDDocumentNumber = "documentNumber";
	public final static String FIELDArtikul = "artikul";
	public final static String HOOKTekushieLimityTP = "tekushieLimityTP";
	public final static String HOOKApproveFix = "ApproveFix";
	public final static String FIELDFixNum = "num";
	public final static String FIELDFixArt = "art";
	public final static String FIELDFixRow = "row";
	public final static String FIELDGruppadogovorov = "gruppadogovorov";
	public final static String FIELDKontragent = "kontragent";
	public final static String FIELDKlientKod = "klientKod";
	public final static String FIELDDogovorOplata = "dogopl";
	static java.lang.Process pingProcess = null;
	public String currentKey = "";
	public SubLayoutless propertiesForm;
	public Expect expectRequery;
	ActivityWebServicesReports activityReports;
	//public boolean interceptURL=true;

	final static int ordinaryQuery = 0;
	final static int xlsQuery = 1;
	final static int pdfQuery = 2;

	public Report_Base(ActivityWebServicesReports p) {
		activityReports = p;

		expectRequery = new Expect()//
				.status.is("Подождите.....")//
				.task.is(new Task() {
					@Override
					public void doTask() {
						//System.out.println("expectRequery task");
						activityReports.reportGridScrollViewY = activityReports.reportGrid.scrollView.getScrollY();
						writeCurrentPage();
					}
				})//
				.afterDone.is(new Task() {
					@Override
					public void doTask() {
						//System.out.println("expectRequery afterDone");
						String htm = Cfg.pathToHTML(getFolderKey(), currentKey);
						activityReports.brwsr.go("file://" + htm);
						writeForm(currentKey);
						//activityReports.popParametersForm();
						//System.out.println("resetMenu");
						activityReports.resetMenu2();
						//Expect.this.cancel.is(false);
						expectRequery.cancel.is(false);
						activityReports.scrollBack();

					}
				})//
		//.start(activityReports);
		;
	}

	public static String tagForFormat(int queryKind) {
		String qq = "";
		try {
			if (queryKind == xlsQuery) {
				qq = qq + "&" + URLEncoder.encode("Формат", "UTF-8") + "=xls";
			} else {
				if (queryKind == pdfQuery) {
					qq = qq + "&" + URLEncoder.encode("Формат", "UTF-8") + "=pdf";
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return qq;
	}

	/*
		public String serviceLogin() {

			return Cfg.databaseHRC();
		}

		public String servicePassword() {
			return Cfg.hrcPersonalPassword();
		}
	*/
	public static String menuLabel() {
		return "menu";
	}

	public static String folderKey() {
		return "folder";
	}

	public static String extract(String line, int startSymbol, int endSymbol) {
		String r = "";
		int start = line.indexOf(startSymbol);
		if (start > -1) {
			int end = line.indexOf(endSymbol, start + 1);
			if (end > -1) {
				r = line.substring(start + 1, end);
			}
		}
		return r;
	}

	public static String extract(String line, String startSymbol, String endSymbol) {
		String r = "";
		int start = line.indexOf(startSymbol);
		if (start > -1) {
			int end = line.indexOf(endSymbol, start + startSymbol.length());
			if (end > -1) {
				r = line.substring(start + startSymbol.length(), end);
			}
		}
		return r;
	}

	public static void startPing() {
		/*
		System.out.println("startPing " + Settings.getInstance().getBaseIP());
		stopPing();
		new Thread() {
			public void run() {
				try {
					//pingProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1000 89.109.7.162");
					pingProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1000 " + Settings.getInstance().getBaseIP());
					pingProcess.waitFor();
					System.out.println("ping done");
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			;
		}.start();
		try {
			Thread.sleep(500);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.out.println("ping started to " + Settings.getInstance().getBaseIP());
		*/
	}

	public static void stopPing() {
		//System.out.println("stopPing");
		/*
		try {
			if (pingProcess != null) {
				pingProcess.destroy();
				System.out.println("stop ping");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		*/
	}

	public String getFileName() {
		return "export" + getMenuLabel();
	}

	public String getMenuLabel() {
		return "menu";
	}

	public String getFolderKey() {
		return "folder";
	}

	public String getShortDescription(String key) {
		return "?" + key;
	}

	public String getOtherDescription(String key) {
		return "?" + key;
	}

	public String writeNewReport() {
		String instanceKey = "" + new Date().getTime();
		String htm = Cfg.pathToHTML(getFolderKey(), instanceKey);
		writeDefaultForm(instanceKey);
		Auxiliary.writeTextToFile(new File(htm), htm, "utf-8");
		return instanceKey;
	}

	public String writeNewReport(Vector<Bough> parameters) {
		String instanceKey = "" + new Date().getTime();
		String htm = Cfg.pathToHTML(getFolderKey(), instanceKey);
		writeDefaultForm(instanceKey);
		Auxiliary.writeTextToFile(new File(htm), htm, "utf-8");
		return instanceKey;
	}

	public void readForm(String key) {
	}

	public void writeForm(String key) {
	}

	public void writeDefaultForm(String instanceKey) {
		String parameters = Cfg.pathToXML(getFolderKey(), instanceKey);
		Auxiliary.createAbsolutePathForFile(parameters);
		Auxiliary.writeTextToFile(new File(parameters), parameters, "utf-8");
	}

	public void setKey(String key) {
		currentKey = key;
	}

	public SubLayoutless getParametersView(Context context) {
		if (propertiesForm == null) {
			propertiesForm = new SubLayoutless(context);
			propertiesForm//
					.child(new Decor(context)//
							.labelText.is(getMenuLabel())//
							.labelStyleLargeNormal()//
							.left().is(8)//
							.width().is(propertiesForm.width().property)//
							.height().is(propertiesForm.height().property)//
					)//
					.innerHeight.is(Auxiliary.tapSize * 20);
			;
			//propertiesForm.innerHeight.is(Auxiliary.tapSize * 14);
			System.out.println();
		}
		return propertiesForm;
	}

	public String composeRequest() {
		return null;
	}

	public String composeGetQuery(int queryKind) {
		return "?";
	}

	public String composePostBody(int queryKind) {
		return null;
	}

	public String interceptActions(String html) {
		return html;
	}

	public String extractResult(Bough response) {
		System.out.println("extractResult");
		String rez = "";
		String s = response.child("soap:Body")//
				.child("m:getReportResponse")//
				.child("m:return")//
				.child("m:Data")//
				.value.property.value();
		System.out.println("m:Params " + response.child("soap:Body")//
				.child("m:getReportResponse")//
				.child("m:return")//
				.child("m:Params").value.property.value());
		try {
			rez = new String(Base64.decode(s, Base64.DEFAULT), "UTF-8");
			System.out.println("response size " + s.length() + "/" + rez.length());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return interceptActions(rez);
	}

	public void exportXLS(String to, Context context) {
		try {
			//String htm = Cfg.pathToHTML(getFolderKey(), currentKey);

			String xml = composeRequest();
			if (xml == null) {
				//String getQueryLink = composeGetQuery(true) + "&IMEI=" + Cfg.device_id();
				String getQueryLink = composeGetQuery(xlsQuery);
				System.out.println("export " + getQueryLink);
				//byte[] byte64 = Auxiliary.loadFileFromURL(getQueryLink);
				String postBody = composePostBody(xlsQuery);
				if (postBody == null) {
					byte[] raw = Base64.decode(Auxiliary.loadFileFromPrivateURL(getQueryLink // + "&IMEI=" + Cfg.device_id()
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()), Base64.DEFAULT);
					FileOutputStream fileOutputStream = null;
					fileOutputStream = new FileOutputStream(to);
					fileOutputStream.write(raw, 0, raw.length);
					fileOutputStream.close();
				} else {
					//byte[] raw = Base64.decode(Auxiliary.loadFileFromPrivateURL(getQueryLink , Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()), Base64.DEFAULT);
					Bough result = Auxiliary.loadTextFromPrivatePOST(getQueryLink, postBody.getBytes("UTF-8"), 30000, "UTF-8"
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					//, "bot28", "28bot");
					//System.out.println("result " + result.dumpXML());
					String txt = result.child("raw").value.property.value();
					if (txt.trim().length() < 1) {
						txt = result.child("code").value.property.value() + ": " + result.child("message").value.property.value();
					}
					FileOutputStream fileOutputStream = null;
					fileOutputStream = new FileOutputStream(to);
					try {
						byte[] raw = Base64.decode(txt, Base64.DEFAULT);
						fileOutputStream.write(raw, 0, raw.length);
					} catch (Throwable t2) {
						byte[] raw = txt.getBytes();
						fileOutputStream.write(raw, 0, raw.length);
					}
					fileOutputStream.close();
				}
				return;
			} else {
				//
			}
			String pdf = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>ТипФайла</Name>"//
					+ "\n							<Value>" + "xls"/*"pdf"*/ + "</Value>"//
					+ "\n							<Tipe>Значение</Tipe>"//
					+ "\n							<TipeElem>Строка</TipeElem>"//
					+ "\n						</Param>";
			xml = xml.replace("</m:Параметры>", pdf + "\n" + "</m:Параметры>");
			System.out.println("now " + xml);
			RawSOAP rawSOAP = new RawSOAP();
			rawSOAP.url.is(Settings.getInstance().getBaseURL()// 
					//+ "ReportAndroid.1cws")//
					//+ "ReportAndroid.1cws")//
					+ "ReportAndroid" + Settings.selectedWSDL() + ".1cws"//
			)
					.responseEncoding.is("cp-1251")//
					.xml.is(xml)//
					.timeout.is(3 * 60 * 1000)//
			;
			System.out.println("rawSOAP.startNow()");
			Report_Base.startPing();
			rawSOAP.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			System.out.println("rawSOAP.startNow() done");
			String txt = "";
			if (rawSOAP.statusCode.property.value() >= 100 //
					&& rawSOAP.statusCode.property.value() <= 300//
					&& rawSOAP.exception.property.value() == null//
					&& rawSOAP.data != null//
			) {
				//txt = extractResult(rawSOAP.data);
				//System.out.println(rawSOAP.data.dumpXML());
				String string64 = rawSOAP.data.child("soap:Body")//
						.child("m:getReportResponse")//
						.child("m:return")//
						.child("m:Data")//
						.value.property.value();
				byte[] byte64 = Base64.decode(string64, Base64.DEFAULT);
				FileOutputStream fileOutputStream = null;
				fileOutputStream = new FileOutputStream(to);
				fileOutputStream.write(byte64, 0, byte64.length);
				fileOutputStream.close();
				//System.out.println("wrote " + new String(byte64));
			} else {
				String ex = "";
				/*txt = "<p>Error<p><pre>"//
						      + "\nstatus: " + rawSOAP.statusCode.property.value()//
						      + "\ndescription: " + rawSOAP.statusDescription.property.value() //
						      + "\ndata: " + rawSOAP.data;*/
				if (rawSOAP.exception.property.value() != null) {
					//txt = txt + "\nexception: " + rawSOAP.exception.property.value().toString();
					ex = rawSOAP.exception.property.value().toString();
				}
				//txt = txt + "</pre>";
				txt = formatErrorMessage(rawSOAP.statusCode.property.value().toString()
						, rawSOAP.statusDescription.property.value()
						, rawSOAP.data == null ? "" : rawSOAP.data.dumpXML()
						, ex);
				Auxiliary.writeTextToFile(new File(to), txt, "utf-8");
			}
		} catch (Throwable t) {
			//			Auxiliary.warn("" + t.getMessage(), context);
			t.printStackTrace();
		}
	}

	public void exportPDF(String to, Context context) {
		try {
			String pdfrequest = composeRequest();
			if (pdfrequest == null) {
				String getQueryLink = composeGetQuery(pdfQuery);
				System.out.println("export " + getQueryLink);
				String postBody = composePostBody(pdfQuery);
				if (postBody == null) {
					byte[] raw = Base64.decode(Auxiliary.loadFileFromPrivateURL(getQueryLink //+ "&IMEI=" + Cfg.device_id()
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()), Base64.DEFAULT);
					FileOutputStream fileOutputStream = null;
					fileOutputStream = new FileOutputStream(to);
					fileOutputStream.write(raw, 0, raw.length);
					fileOutputStream.close();
				} else {
					Bough result = Auxiliary.loadTextFromPrivatePOST(getQueryLink, postBody.getBytes("UTF-8"), 30000, "UTF-8"
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = result.child("raw").value.property.value();
					if (txt.trim().length() < 1) {
						txt = result.child("code").value.property.value() + ": " + result.child("message").value.property.value();
					}
					FileOutputStream fileOutputStream = null;
					fileOutputStream = new FileOutputStream(to);
					try {
						byte[] raw = Base64.decode(txt, Base64.DEFAULT);
						fileOutputStream.write(raw, 0, raw.length);
					} catch (Throwable t2) {
						byte[] raw = txt.getBytes();
						fileOutputStream.write(raw, 0, raw.length);
					}
					fileOutputStream.close();
				}
				return;
			} else {
				//
			}
			String pdf = "\n						<Param xmlns=\"http://ws.swl/Param\">"//
					+ "\n							<Name>ТипФайла</Name>"//
					+ "\n							<Value>" + "pdf" + "</Value>"//
					+ "\n							<Tipe>Значение</Tipe>"//
					+ "\n							<TipeElem>Строка</TipeElem>"//
					+ "\n						</Param>";
			pdfrequest = pdfrequest.replace("</m:Параметры>", pdf + "\n" + "</m:Параметры>");
			System.out.println("now " + pdfrequest);
			RawSOAP rawSOAP = new RawSOAP();
			rawSOAP.url.is(Settings.getInstance().getBaseURL() + "ReportAndroid" + Settings.selectedWSDL() + ".1cws")
					.responseEncoding.is("cp-1251")//
					.xml.is(pdfrequest)//
					.timeout.is(3 * 60 * 1000)//
			;
			System.out.println("rawSOAP.startNow()");
			Report_Base.startPing();
			rawSOAP.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			System.out.println("rawSOAP.startNow() done");
			String txt = "";
			if (rawSOAP.statusCode.property.value() >= 100 //
					&& rawSOAP.statusCode.property.value() <= 300//
					&& rawSOAP.exception.property.value() == null//
					&& rawSOAP.data != null//
			) {
				String string64 = rawSOAP.data.child("soap:Body")//
						.child("m:getReportResponse")//
						.child("m:return")//
						.child("m:Data")//
						.value.property.value();
				byte[] byte64 = Base64.decode(string64, Base64.DEFAULT);
				FileOutputStream fileOutputStream = null;
				fileOutputStream = new FileOutputStream(to);
				fileOutputStream.write(byte64, 0, byte64.length);
				fileOutputStream.close();
				//System.out.println("wrote " + new String(byte64));
			} else {
				String ex = "";
				if (rawSOAP.exception.property.value() != null) {
					ex = rawSOAP.exception.property.value().toString();
				}
				txt = formatErrorMessage(rawSOAP.statusCode.property.value().toString()
						, rawSOAP.statusDescription.property.value()
						, rawSOAP.data == null ? "" : rawSOAP.data.dumpXML()
						, ex);
				Auxiliary.writeTextToFile(new File(to), txt, "utf-8");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void writeCurrentPage() {
		System.out.println("writeCurrentPage");
		String htm = Cfg.pathToHTML(getFolderKey(), currentKey);
		String txt = "";
		if (currentKey.length() > 0) {
			try {
				String xml = composeRequest();
				if (xml == null) {
					//String getQueryLink = composeGetQuery(false) + "&IMEI=" + Cfg.device_id();
					String getQueryLink = composeGetQuery(ordinaryQuery);
					txt = getQueryLink;
					String postBody = composePostBody(ordinaryQuery);
					if (postBody == null) {
						byte[] bytes = Auxiliary.loadFileFromPrivateURL(getQueryLink //+ "&IMEI=" + Cfg.device_id()
								, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						txt = new String(bytes);
					} else {
						System.out.println("getQueryLink " + getQueryLink);
						//Bough result=Auxiliary.loadTextFromPrivatePOST(getQueryLink, postBody.getBytes(), 30000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						Bough result = Auxiliary.loadTextFromPrivatePOST(getQueryLink, postBody.getBytes("UTF-8"), 30000, "UTF-8"
								, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
						//, "bot28", "28bot");
						System.out.println("result " + result.dumpXML());
						txt = result.child("raw").value.property.value();
						if (txt.trim().length() < 1) {
							txt = result.child("code").value.property.value() + ": " + result.child("message").value.property.value();
						}
					}
					//System.out.println(getQueryLink);
					txt = interceptActions(txt);
				} else {
					RawSOAP rawSOAP = new RawSOAP();
					rawSOAP.url.is(Settings.getInstance().getBaseURL()//
							// + "ReportAndroidtest.1cws"
							//+ "ReportAndroid.1cws")//
							+ "ReportAndroid" + Settings.selectedWSDL() + ".1cws"//
					)
							//+ "ReportAndroidtestLednev.1cws")//
							.responseEncoding.is("cp-1251")//
							//.responseEncoding.is("UTF-8")//
							.xml.is(xml)//
							.timeout.is(7 * 60 * 1000)//
					;
					System.out.println("rawSOAP.startNow " + rawSOAP.url.property.value() + " " + xml);
					Report_Base.startPing();
					rawSOAP.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					System.out.println("rawSOAP.startNow() done");
					if (rawSOAP.statusCode.property.value() >= 100 //
							&& rawSOAP.statusCode.property.value() <= 300//
							&& rawSOAP.exception.property.value() == null//
							&& rawSOAP.data != null//
					) {
						txt = extractResult(rawSOAP.data);
					} else {
						String ex = "";
						if (rawSOAP.exception.property.value() != null) {
							ex = rawSOAP.exception.property.value().toString();
						}
						txt = formatErrorMessage(rawSOAP.statusCode.property.value().toString(), rawSOAP.statusDescription.property.value()
								, rawSOAP.data == null ? "" : rawSOAP.data.dumpXML()
								, ex);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				txt = txt + " /" + t.getMessage();
			}
			Auxiliary.writeTextToFile(new File(htm), txt, "utf-8");
		}
	}

	String safe(String txt) {
		return txt.replace("<", "&lt;").replace(">", "&gt;");
	}

	String formatErrorMessage(String status, String description, String data, String exception) {
		String html = "<html><head><meta charset=\"UTF-8\"></head><body>";
		html = html + "<h1>Повторите запрос</h1>";
		html = html + "<p>Проверьте подключение к интернет</p>";
		//html=html+"<p>"+new Date()+"</p>";
		html = html + "<pre>";
		html = html + new Date() + "\n" + safe(status) + ": " + safe(description) + ": " + safe(data)//
				+ "\n: " + safe(exception)//
		;
		html = html + "</pre>";
		html = html + "</body></html>";
		return html;
	}
}
