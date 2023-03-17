package sweetlife.android10.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.DOMParserBase;

import android.util.Base64;


public class ReportsXMLParser extends DOMParserBase implements IReportConsts{

	private String mFilePath = null;

	@Override
	protected void ParseData(Document document) throws NumberFormatException,
	ParseException {

		NodeList dataNode = document.getElementsByTagName(TAG_DATA);

		String fileContent = dataNode.item(0).getTextContent();

		byte[] pdfAsBytes = Base64.decode(fileContent, 0);

		try {

			File filePath = File.createTempFile(
					REPORT_FILE_PREFFIX, 
					REPORT_FILE_EXTENSION, 
					new File(Settings.getInstance().getTABLET_WORKING_DIR()));

			FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
			LogHelper.debug(this.getClass().getCanonicalName()+" write pdf to "+filePath);
			fileOutputStream.write(pdfAsBytes);
			fileOutputStream.flush();
			fileOutputStream.close();

			mFilePath = filePath.getPath();
			mFilePath = filePath.getAbsolutePath();
			mFilePath = filePath.getName();
			mFilePath = filePath.getParent();
			mFilePath = filePath.getCanonicalPath();
			mFilePath = filePath.toString();
		} 
		catch (IOException e) {

			e.printStackTrace();
		}
	}

	public String getReportFilePath() {

		return mFilePath;
	}
}
