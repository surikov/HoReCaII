package sweetlife.android10;

import java.io.File;

import android.os.Environment;
import android.app.*;
import reactive.ui.Auxiliary;
import tee.binding.it.*;
import tee.binding.*;
import java.net.*;
import java.util.Vector;

public class Settings {
	//private String LOGS_EMAIL                   = "horeca.logs@gmail.com";
	//private String LOGS_PASSWORD                = "qwerty12345678";
	private final static String _SERVICE_APPROVE_ORDER = "ChangeOfOrders.1cws";
	public static Note emailToSend=new Note();
	public static Toggle emailPoINNToSend=new Toggle().value(true);
	public static Toggle emailGroupingToSend=new Toggle().value(true);
	public static Toggle emailPoSpecificacii=new Toggle().value(false);
	public static Numeric startVislatNakladnieNaPochtu=new Numeric();
	public static Numeric endVislatNakladnieNaPochtu=new Numeric();
	public static boolean DEBUG_MODE = false;
	public static int colorTop20=0xff9999ff;
	public static int colorNacenka25=0xffff9966;
	public static int colorOlder=0xffff6666;
	//private static String _primaryURL = "89.109.7.162";
	//private  static String _secondaryURL = "95.79.111.216";


	//private static String  _secondaryURL= "https://service.swlife.ru";
	//private  static String _primaryURL = "http://89.109.7.162";
	//private static String _primaryFileStoreURL="https://androbmen.swlife.ru/";//android/Update2.xml";
	//private static String _secondaryFileStoreURL="http://89.109.7.162/";//androbmen/android/Update2.xml";
	public static String photoURL = "https://files.swlife.ru/photo/";
	private static String _primaryURL = "https://service.swlife.ru";
	//private static String _primaryURL = "https://testservice.swlife.ru";

	private  static String _secondaryURL = "http://95.79.111.216";//"http://89.109.7.162";
	private static String _primaryFileStoreURL="https://androbmen.swlife.ru/";//android/Update2.xml";
	private static String _secondaryFileStoreURL="http://95.79.111.216/";//"http://89.109.7.162/";//androbmen/android/Update2.xml";
public static boolean isPrimaryGate=true;
	//http://89.109.7.162/androbmen/android/Update2.xml
	//https://androbmen.swlife.ru/android/Update2.xml

	/*
	private  static String _primaryURL = "http://89.109.7.162";
	private static String _secondaryURL = "https://service.swlife.ru";
*/

	private static String _base1c="hrc120107";
	//private static String _base1c="shatov";
	//private static String _base1c="GolovaNew";
	private static String _wsdlDiff="";
	private static String _baseURL = _primaryURL;
	private static String _baseFileStoreURL = _primaryFileStoreURL;
	private static Settings instance = null;
	private static String _SERVICE_DOLGI_PO_NKLADNIM = "GatDolgi.1cws";
	private static Bough configuration;
	public String TABLET_DATABASE_BACKUP = "/sdcard/horeca/reserve/swlife_database";
	private int FTP_PORT = 21;
	private String FTP_SERVER = "217.23.20.242";
	//private String FTP_SERVER                   = "213.177.113.10";
	private String FTP_USER = "bot1c";
	private String FTP_PASSWORD = "12345678";
	private String FTP_PATH = "android/";
	private String FTP_DELTA_NAME = "AndroidExchange_%s_%s_";
	private String UPDATE_XML_NAME = "Update3.xml";
	//private String SETTINGS_XML_NAME = "Settings.xml";
	private String APPLICATION_NAME = "Horeca3.apk";
	//private String _SERVICE_FIXED_PRICES = "RequestForDiscounttest.1cws";
	private String TABLET_WORKING_DIR = "/sdcard/horeca/";
	private String TABLET_RESERVE_DIR = "/sdcard/horeca/reserve/";
	private String TABLET_DELTA_DIR = "/sdcard/horeca/reserve/delta/";
	private String TABLET_DATABASE_FILE = "/sdcard/horeca/swlife_database";
	//private String _SERVICE_DISPOSALS = "WebRasporTest.1cws";
	private String TABLET_LOGGING_FILE = "/sdcard/horeca/logging";
	//http://10.10.0.17/ChangeOfOrders.1cws
	//private String _SERVICE_ORDERS = "wsuploadorders/wsuploadorders12.1cws";
	private String _SERVICE_ORDERS = "wsuploadorders/wsuploadorders12.1cws";
	//private String _SERVICE_ORDERS = "wsuploadorders/wsuploadorders12test.1cws";
	//private String _SERVICE_GPS_POINTS = "wsgetdebt/wsGPSAndroid.1cws";
	private String _SERVICE_VIZITS = "wsgetdebt/visitsAndroid.1cws";
	private String _SERVICE_RETURNS = "WsUploadOrders/wsuploadvozvHRC.1cws";
	private String _SERVICE_FIXED_PRICES = "RequestForDiscount.1cws";
	private String _SERVICE_DISPOSALS = "WebRaspor.1cws";
	private String _SERVICE_CLIENTS_STATUS = "wsclientstatus/wsclientstatus12.1cws";
	private String _SERVICE_AVAILABLE_AMOUNT = "wsGetDebt/wsDebtHRC.1cws";
	private String _SERVICE_REPORTS = "ReportAndroidtest.1cws";
	private String _SERVICE_CONTRACTS_CODES = "wsclientstatus/wsclientstatus12.1cws";
	private long MINIMAL_FREE_SPACE = 536870912;
	private int SPY_GPS_PERIOD = 15000;
	private long MAX_DISTANCE_TO_CLIENT = 100;
	private int PERIOD_CLEAR_GPS_DB_DATA = 604800000;
	private int PERIOD_CLEAR_DB = 604800000;



	private Settings() {
		MakeDefaulPaths();
		//ReadXMLFile(TABLET_WORKING_DIR + SETTINGS_XML_NAME);
		loadConfig();
	}

	public static long checkPrimaryAccess(Activity a){
		long time=-1;
		try {
			//URL mainURL = new URL("http://" + Settings._primaryURL + ":80");
			//URL secondURL = new URL("http://" + Settings._secondaryURL + ":80");
			URL mainURL = new URL("" + Settings._primaryURL + ":80");
			URL secondURL = new URL("" + Settings._secondaryURL + ":80");
			//time.value((double) Auxiliary.checkAccessToURL(mainURL, Activity_Login.this));
			time=Auxiliary.checkAccessToURL(mainURL, a);
			//System.out.println("connection delay is " + time);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return time;
	}

public static String selectedBase1C(){
		return Settings.getInstance()._base1c;
}

public static String selectedWSDL(){
		return Settings.getInstance()._wsdlDiff;
}

	public static int ndsById(String id) {
		/*
		18%: 9701531aae7e29e1418d1fb94bb4dd8d
		18% / 118%: 896ebdc306e4395048a15e1001835fd3
		10%: 8c35d1aa082d09c449482233639cb5dc
		10% / 110%: 8267317daeb1c9804a93e2accba8ba62
		0%: 805878f21622004e4e02ce8f6dfd246a
		Без НДС: 8540419f4ca125b141cfbf05c518610d
		20%: 96a72e469df2a8df4f5bf008c2577b7d
		20% / 120%: b751b731d8b9680e4f36dcf6accbc9bd
		 */
		if (id.equals("9701531aae7e29e1418d1fb94bb4dd8d")) {
			return 18;
		}
		if (id.equals("896ebdc306e4395048a15e1001835fd3")) {
			return 18;
		}
		if (id.equals("8c35d1aa082d09c449482233639cb5dc")) {
			return 10;
		}
		if (id.equals("8267317daeb1c9804a93e2accba8ba62")) {
			return 10;
		}
		if (id.equals("805878f21622004e4e02ce8f6dfd246a")) {
			return 0;
		}
		if (id.equals("8540419f4ca125b141cfbf05c518610d")) {
			return 0;
		}
		if (id.equals("96a72e469df2a8df4f5bf008c2577b7d")) {
			return 20;
		}
		if (id.equals("b751b731d8b9680e4f36dcf6accbc9bd")) {
			return 20;
		}
		return 0;
	}

	public static Bough cfg() {
		return getInstance().configuration;
	}

	static void loadConfig() {
		//System.out.println("Settings.configuration: loadConfig");
		configuration = null;//new Bough();
		try {
			String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/Horeca2.xml";
			System.out.println("loadConfig from "+path);
			File file=new File(path);
			Vector<String> strings=Auxiliary.readTextFromFile(file);
			String xml = Auxiliary.strings2text(strings);
			System.out.println("configuration is "+xml);
			try {
				configuration = Bough.parseXML(xml);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		if(configuration == null)
		{
			configuration = new Bough();
		}
		//System.out.println("Settings.configuration: " + configuration.dumpXML());
		try{
			String xml = Auxiliary.strings2text(Auxiliary.readTextFromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/Horeca2ip.xml")));
			Bough baseIP = Bough.parseXML(xml);
			if(baseIP.child("primary").value.property.value().trim().length()>1){
				_primaryURL=baseIP.child("primary").value.property.value().trim();
			}
			if(baseIP.child("secondary").value.property.value().trim().length()>1){
				_secondaryURL=baseIP.child("secondary").value.property.value().trim();
			}
			if(baseIP.child("base1C").value.property.value().trim().length()>1){
				_base1c=baseIP.child("base1C").value.property.value().trim();
			}
			if(baseIP.child("wsdlDiff").value.property.value().trim().length()>1){
				_wsdlDiff=baseIP.child("wsdlDiff").value.property.value().trim();
			}
			_baseURL = _primaryURL;
		}catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public void setPrimaryURL() {
		_baseURL = _primaryURL;
		_baseFileStoreURL = _primaryFileStoreURL;
		//System.out.println("Settings.baseURL " + Settings._baseURL);
		isPrimaryGate=true;
	}

	public void setSecondaryURL() {
		_baseURL = _secondaryURL;
		_baseFileStoreURL = _secondaryFileStoreURL;
		//System.out.println("Settings.baseURL " + Settings._baseURL);
		isPrimaryGate=false;
	}

	public String getBaseURL() {
		/*if(Cfg.hrcPersonalLogin==null) {
			return "http://" + _baseURL + "/";
		}else{
			return "http://" + _secondaryURL + "/";
		}*/
		//return "http://" + _baseURL + "/";
		return "" + _baseURL + "/";
	}
	public String getBaseFileStoreURL() {
		return _baseFileStoreURL;
	}

	public String getBaseIP() {
		return "service.swlife.ru";
	}

	/*public boolean update() {
		boolean result = false;
		BackupFile();
		if (DownloadFile(FTP_PATH + SETTINGS_XML_NAME)) {
			if ((result = ReadXMLFile(TABLET_WORKING_DIR + SETTINGS_XML_NAME)) == false) {
				RestoreBackupFile();
				ReadXMLFile(TABLET_WORKING_DIR + SETTINGS_XML_NAME);
			}
		}
		else {
			RestoreBackupFile();
		}
		RemoveFile(TABLET_RESERVE_DIR + SETTINGS_XML_NAME);
		return result;
	}*/
	private void MakeDefaulPaths() {
		new File(TABLET_WORKING_DIR).mkdirs();
		new File(TABLET_RESERVE_DIR).mkdirs();
		new File(TABLET_DELTA_DIR).mkdirs();
	}
	/*private boolean DownloadFile(String ftpPath) {
		try {
			ftpClient.downloadFile(TABLET_WORKING_DIR, FTP_PATH + SETTINGS_XML_NAME);
		}
		catch (Exception e) {
			e.printStackTrace();
			ErrorReporter.getInstance().putCustomData("Settings", "DownloadFile");
			ErrorReporter.getInstance().handleSilentException(e);
			return false;
		}
		return true;
	}*/
	/*private void RestoreBackupFile() {
		RemoveFile(TABLET_WORKING_DIR + SETTINGS_XML_NAME);
		File fileForRestore = new File(TABLET_RESERVE_DIR + SETTINGS_XML_NAME);
		if (fileForRestore.exists()) {
			MoveFile(TABLET_RESERVE_DIR + SETTINGS_XML_NAME, TABLET_WORKING_DIR + SETTINGS_XML_NAME);
		}
	}
	private void RemoveFile(String path) {
		File fileForRemove = new File(path);
		if (fileForRemove.exists()) {
			fileForRemove.delete();
		}
	}
	private void BackupFile() {
		RemoveFile(TABLET_RESERVE_DIR + SETTINGS_XML_NAME);
		File fileForBackup = new File(TABLET_WORKING_DIR + SETTINGS_XML_NAME);
		if (fileForBackup.exists()) {
			MoveFile(TABLET_WORKING_DIR + SETTINGS_XML_NAME, TABLET_RESERVE_DIR + SETTINGS_XML_NAME);
		}
	}
	private void MoveFile(String fromPath, String toPath) {
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		fromFile.renameTo(toFile);
	}
	public void CopyFile(String fromPath, String toPath) {
		try {
			FileInputStream inputStream = new FileInputStream(fromPath);
			FileOutputStream outputStream = new FileOutputStream(toPath);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		}
		catch (Exception e) {
			ErrorReporter.getInstance().putCustomData("Settings", "CopyFile");
			ErrorReporter.getInstance().handleSilentException(e);
		}
	}*/
	/*private boolean ReadXMLFile(String fileName) {
		if (fileName != null && fileName.length() != 0) {
			File xmlFile = new File(fileName);
			if (xmlFile.exists()) {
				try {
					SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
					XMLReader reader = saxParser.getXMLReader();
					reader.setContentHandler(new SettingsSAXParser());
					InputSource inputSource = new InputSource(new FileInputStream(xmlFile));
					reader.parse(inputSource);
					return true;
				}
				catch (Exception e) {
					ErrorReporter.getInstance().putCustomData("Settings", "ReadXMLFile");
					ErrorReporter.getInstance().handleSilentException(e);
				}
			}
		}
		return false;
	}*/
	public int getFTP_PORT() {
		return FTP_PORT;
	}
	public void setFTP_PORT(int value) {
		FTP_PORT = value;
	}
	public String getFTP_SERVER() {
		return FTP_SERVER;
	}
	public void setFTP_SERVER(String value) {
		FTP_SERVER = value;
	}
	public String getFTP_USER() {
		return FTP_USER;
	}
	public void setFTP_USER(String value) {
		FTP_USER = value;
	}
	public String getFTP_PASSWORD() {
		return FTP_PASSWORD;
	}
	public void setFTP_PASSWORD(String value) {
		FTP_PASSWORD = value;
	}
	public String getFTP_PATH() {
		return FTP_PATH;
	}
	public void setFTP_PATH(String value) {
		FTP_PATH = value;
	}
	public String getFTP_DELTA_NAME() {
		return FTP_DELTA_NAME;
	}
	public void setFTP_DELTA_NAME(String value) {
		FTP_DELTA_NAME = value;
	}
	public String getUPDATE_XML_NAME() {
		return UPDATE_XML_NAME;
	}
	/*public String getSETTINGS_XML_NAME() {
		return SETTINGS_XML_NAME;
	}*/
	public String getSERVICE_DOLGI_PO_NKLADNIM() {
		return _SERVICE_DOLGI_PO_NKLADNIM;
	}
	public String getAPPLICATION_NAME() {
		return APPLICATION_NAME;
	}
	public String getTABLET_WORKING_DIR() {
		return TABLET_WORKING_DIR;
	}
	public String getTABLET_RESERVE_DIR() {
		return TABLET_RESERVE_DIR;
	}
	public String getTABLET_DELTA_DIR() {
		return TABLET_DELTA_DIR;
	}
	public String getTABLET_DATABASE_FILE() {
		return TABLET_DATABASE_FILE;
	}
	public String getTABLET_LOGGING_FILE() {
		return TABLET_LOGGING_FILE;
	}
	/*
		public String getLOGS_EMAIL() {

			return LOGS_EMAIL;
		}

		public void setLOGS_EMAIL(String value) {

			LOGS_EMAIL = value;
		}

		public String getLOGS_PASSWORD() {

			return LOGS_PASSWORD;
		}

		public void setLOGS_PASSWORD(String value) {

			LOGS_PASSWORD = value;
		}
	*/
	public String getSERVICE_ORDERS() {
		return getBaseURL() + _SERVICE_ORDERS;
	}
	/*public void setSERVICE_ORDERS(String value) {
		SERVICE_ORDERS = value;
	}*/
	public String getSERVICE_CLIENTS_STATUS() {
		return getBaseURL() + _SERVICE_CLIENTS_STATUS;
	}
	/*public void setSERVICE_CLIENTS_STATUS(String value) {
		SERVICE_CLIENTS_STATUS = value;
	}*/
	public String getSERVICE_AVAILABLE_AMOUNT() {
		return getBaseURL() + _SERVICE_AVAILABLE_AMOUNT;
	}
	/*public void setSERVICE_AVAILABLE_AMOUNT(String value) {
		SERVICE_AVAILABLE_AMOUNT = value;
	}*/
	/*public String getSERVICE_GPS_POINTS() {
		return baseURL+_SERVICE_GPS_POINTS;
	}*/
	/*public void setSERVICE_GPS_POINTS(String value) {
		SERVICE_GPS_POINTS = value;
	}*/
	public String getSERVICE_VIZITS() {
		return getBaseURL() + _SERVICE_VIZITS;
	}
	/*public void setSERVICE_VIZITS(String value) {
		SERVICE_VIZITS = value;
	}*/
	public String getSERVICE_RETURNS() {
		return getBaseURL() + _SERVICE_RETURNS;
	}
	/*public void setSERVICE_RETURNS(String value) {
		SERVICE_RETURNS = value;
	}*/
	public String getSERVICE_APPROVE_ORDER() {
		return getBaseURL() + _SERVICE_APPROVE_ORDER;
	}
	/*public  String getSERVICE_DOLGI_PO_NKLADNIM() {
		return SERVICE_DOLGI_PO_NKLADNIM;
	}*/
	public String getSERVICE_FIXED_PRICES() {
		return getBaseURL() + _SERVICE_FIXED_PRICES;
	}
	/*public void setSERVICE_FIXED_PRICES(String value) {
		SERVICE_FIXED_PRICES = value;
	}*/
	public String getSERVICE_DISPOSALS() {
		return getBaseURL() + _SERVICE_DISPOSALS;
	}
	/*public void setSERVICE_DISPOSALS(String value) {
		SERVICE_DISPOSALS = value;
	}*/
	public String getSERVICE_REPORTS() {
		return getBaseURL() + _SERVICE_REPORTS;
	}
	/*public void setSERVICE_REPORTS(String value) {
		SERVICE_REPORTS = value;
	}*/
	public String getSERVICE_CONTRACTS_CODES() {
		return getBaseURL() + _SERVICE_CONTRACTS_CODES;
	}
	/*public void setSERVICE_CONTRACTS_CODES(String value) {
		SERVICE_CONTRACTS_CODES = value;
	}*/
	public long getMINIMAL_FREE_SPACE() {
		return MINIMAL_FREE_SPACE;
	}
	public void setMINIMAL_FREE_SPACE(long value) {
		MINIMAL_FREE_SPACE = value;
	}
	public int getSPY_GPS_PERIOD() {
		return SPY_GPS_PERIOD;
	}
	public void setSPY_GPS_PERIOD(int value) {
		SPY_GPS_PERIOD = value;
	}
	public long getMAX_DISTANCE_TO_CLIENT() {
		return MAX_DISTANCE_TO_CLIENT;
	}
	public void setMAX_DISTANCE_TO_CLIENT(long value) {
		MAX_DISTANCE_TO_CLIENT = value;
	}
	public int getPERIOD_CLEAR_GPS_DB_DATA() {
		return PERIOD_CLEAR_GPS_DB_DATA;
	}
	public void setPERIOD_CLEAR_GPS_DB_DATA(int value) {
		PERIOD_CLEAR_GPS_DB_DATA = value;
	}
	public int getPERIOD_CLEAR_DB() {
		return PERIOD_CLEAR_DB;
	}
	public void setPERIOD_CLEAR_DB(int value) {
		PERIOD_CLEAR_DB = value;
	}
	/*
		public class SettingsSAXParser extends DefaultHandler {
			private final String ftp_port = "ftp_port";
			private final String ftp_server = "ftp_server";
			private final String ftp_user = "ftp_user";
			private final String ftp_password = "ftp_password";
			private final String ftp_path = "ftp_path";
			private final String ftp_delta_name = "ftp_delta_name";
			private final String logs_email = "logs_email";
			private final String logs_password = "logs_password";
			private final String service_orders = "service_orders";
			private final String service_clients_status = "service_clients_status";
			private final String service_available_amount = "service_available_amount";
			private final String service_gps_points = "service_gps_points";
			private final String service_vizits = "service_vizits";
			private final String service_returns = "service_returns";
			private final String service_fixed_prices = "service_fixed_prices";
			private final String service_disposals = "service_disposals";
			private final String service_reports = "service_reports";
			private final String service_contracts_codes = "service_contracts_codes";
			private final String minimal_free_space = "minimal_free_space";
			private final String spy_gps_period = "spy_gps_period";
			private final String max_distance_to_client = "max_distance_to_client";
			private final String period_clear_gps_db_data = "period_clear_gps_db_data";
			private final String period_clear_db = "period_clear_db";

			@Override
			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
				if (ftp_port.compareToIgnoreCase(localName) == 0) {
					setFTP_PORT(Integer.parseInt(atts.getValue(0)));
				}
				else
					if (ftp_server.compareToIgnoreCase(localName) == 0) {
						setFTP_SERVER(atts.getValue(0));
					}
					else
						if (ftp_user.compareToIgnoreCase(localName) == 0) {
							setFTP_USER(atts.getValue(0));
						}
						else
							if (ftp_password.compareToIgnoreCase(localName) == 0) {
								setFTP_PASSWORD(atts.getValue(0));
							}
							else
								if (ftp_path.compareToIgnoreCase(localName) == 0) {
									setFTP_PATH(atts.getValue(0));
								}
								else
									if (ftp_delta_name.compareToIgnoreCase(localName) == 0) {
										setFTP_DELTA_NAME(atts.getValue(0));
									}
									else
										if (logs_email.compareToIgnoreCase(localName) == 0) {
											//setLOGS_EMAIL(atts.getValue(0));
										}
										else
											if (logs_password.compareToIgnoreCase(localName) == 0) {
												//setLOGS_PASSWORD(atts.getValue(0));
											}
											else
												if (service_orders.compareToIgnoreCase(localName) == 0) {
													setSERVICE_ORDERS(atts.getValue(0));
												}
												else
													if (service_clients_status.compareToIgnoreCase(localName) == 0) {
														setSERVICE_CLIENTS_STATUS(atts.getValue(0));
													}
													else
														if (service_available_amount.compareToIgnoreCase(localName) == 0) {
															setSERVICE_AVAILABLE_AMOUNT(atts.getValue(0));
														}
														else
															if (service_gps_points.compareToIgnoreCase(localName) == 0) {
																setSERVICE_GPS_POINTS(atts.getValue(0));
															}
															else
																if (service_vizits.compareToIgnoreCase(localName) == 0) {
																	setSERVICE_VIZITS(atts.getValue(0));
																}
																else
																	if (service_returns.compareToIgnoreCase(localName) == 0) {
																		setSERVICE_RETURNS(atts.getValue(0));
																	}
																	else
																		if (service_fixed_prices.compareToIgnoreCase(localName) == 0) {
																			setSERVICE_FIXED_PRICES(atts.getValue(0));
																		}
																		else
																			if (service_disposals.compareToIgnoreCase(localName) == 0) {
																				setSERVICE_DISPOSALS(atts.getValue(0));
																			}
																			else
																				if (service_reports.compareToIgnoreCase(localName) == 0) {
																					setSERVICE_REPORTS(atts.getValue(0));
																				}
																				else
																					if (service_contracts_codes.compareToIgnoreCase(localName) == 0) {
																						setSERVICE_CONTRACTS_CODES(atts.getValue(0));
																					}
																					else
																						if (minimal_free_space.compareToIgnoreCase(localName) == 0) {
																							setMINIMAL_FREE_SPACE(Long.parseLong(atts.getValue(0)));
																						}
																						else
																							if (spy_gps_period.compareToIgnoreCase(localName) == 0) {
																								setSPY_GPS_PERIOD(Integer.parseInt(atts.getValue(0)));
																							}
																							else
																								if (max_distance_to_client.compareToIgnoreCase(localName) == 0) {
																									setMAX_DISTANCE_TO_CLIENT(Long.parseLong(atts.getValue(0)));
																								}
																								else
																									if (period_clear_gps_db_data.compareToIgnoreCase(localName) == 0) {
																										setPERIOD_CLEAR_GPS_DB_DATA(Integer.parseInt(atts.getValue(0)));
																									}
																									else
																										if (period_clear_db.compareToIgnoreCase(localName) == 0) {
																											setPERIOD_CLEAR_DB(Integer.parseInt(atts.getValue(0)));
																										}
			}
		}*/
}
