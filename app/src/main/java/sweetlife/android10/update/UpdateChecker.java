package sweetlife.android10.update;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.acra.ErrorReporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.ftpClient;


/*
 XML Scheme
#######################################################
<Update>
   <App>
	  <Version>1.2.1.29</Version>
      <PathToFTPFile>android/SWLife.apk</PathToFTPFile>
   </App>
</Update>
#######################################################
 */
public class UpdateChecker 
{
	private static final String	NODE_APP 				= "App";
	private static final String	NODE_VERSION 			= "Version";
	private static final String	NODE_PATH_TO_FTP_FILE 	= "PathToFTPFile";

	private String	            mServerAppVersion = null;
	private static String              mPathToFTPFile = "Horeca3.apk";//null;
	private Settings            mSettings;

	public UpdateChecker() {

		mSettings = Settings.getInstance();
	}

	public boolean ReadUpdateFile() {

		return parseXML(mSettings.getTABLET_WORKING_DIR() + mSettings.getUPDATE_XML_NAME());
	}

	public boolean DownloadFile() {

		try {  

			RemoveFile(mSettings.getTABLET_WORKING_DIR() + mSettings.getUPDATE_XML_NAME());
			
			ftpClient.downloadFile( mSettings.getTABLET_WORKING_DIR(),
					mSettings.getFTP_PATH() + mSettings.getUPDATE_XML_NAME() );
		} 
		catch (Exception e) {
e.printStackTrace();
			ErrorReporter.getInstance().putCustomData("UpdateChecker", "DownloadFile");
			ErrorReporter.getInstance().handleSilentException(e);

			return false;
		}

		return true;
	}

	private void RemoveFile(String path ) {

		File fileForRemove = new File( path );

		if( fileForRemove.exists() ) {

			fileForRemove.delete();
		}		
	}
	
	public String getServerAppVersion() {

		return mServerAppVersion;
	}

	public static String getPathToFTPFile() {

		return mPathToFTPFile;
	}

	public boolean parseXML(String sPathToXMLFile) {

		try {

			DocumentBuilderFactory dbfFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbfFactory.newDocumentBuilder();

			File fUpdateXML = new File(sPathToXMLFile);

			if(!fUpdateXML.exists()) {

				throw new Exception("XML File not found!");
			}

			Document dDOM = dbBuilder.parse(fUpdateXML);
			Element root = dDOM.getDocumentElement();
			root.normalize();

			NodeList appList = root.getElementsByTagName(NODE_APP);

			Element appElement = (Element) appList.item(0);

			mServerAppVersion = getElementValue(appElement, NODE_VERSION);
			mPathToFTPFile = getElementValue(appElement, NODE_PATH_TO_FTP_FILE);
		}
		catch(Exception e) {
			LogHelper.debug(this.getClass().getCanonicalName() + ": "+e.getMessage());

			ErrorReporter.getInstance().putCustomData("UpdateChecker", "parseXML");
			ErrorReporter.getInstance().handleSilentException(e);

			return false;
		}

		return true;
	}

	private String getElementValue(Element lmnt, String sElementName) {

		NodeList elementsList = lmnt.getElementsByTagName(sElementName);

		Element lmntElement = (Element) elementsList.item(0);
		elementsList = lmntElement.getChildNodes();

		return ((Node)elementsList.item(0)).getNodeValue();
	}

	public boolean IsNeedAppUpdate( String appVersion ) {

		if( mServerAppVersion == null || mServerAppVersion.length() == 0 ) {

			return false;
		}

		return mServerAppVersion.compareTo(appVersion) == 0 ? false : true;		
	}
}
