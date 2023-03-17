package sweetlife.android10.data.disposal;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;



import sweetlife.android10.data.fixedprices.FixedPricesNomenclatureData;
import sweetlife.android10.net.IXMLSerializer;
import sweetlife.android10.utils.CameraCaptureHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.utils.SystemHelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Xml;


public class DisposalsXMLSerializer implements IXMLSerializer {

	String TAG_GET_RASPOR      = "m:GetRaspor";
	String ATR_M_SCHEMA        = "http://ws.swl/Raspor";
	String TAG_HEADER          = "m:Header";
	String TAG_DOC             = "m:Doc";
	String TAG_SUMM            = "m:Summ";
	String TAG_USER            = "m:User";
	String TAG_COMMENT         = "m:Comment";
	String TAG_KLIENT          = "m:Klient";
	
	String TAG_TP_FILE         = "m:tpFile";
	String TAG_TP_STRING       = "m:tpString";
	String TAG_FILE            = "m:File";
	String TAG_RASSH           = "m:rassh";
	
	
	RasporyazhenieNaOtgruzku    mRasporyazhenie;
	FixedPricesNomenclatureData mFixedPricesNomenclatureData;
	
	public DisposalsXMLSerializer( SQLiteDatabase db, RasporyazhenieNaOtgruzku rasporyazhenie) {
	
		mRasporyazhenie = rasporyazhenie;
	}
	
	@Override
	public String SerializeXML() throws IllegalArgumentException, IllegalStateException, IOException {
		
		XmlSerializer serializer = Xml.newSerializer();
		
		StringWriter writer = new StringWriter(4096);
		serializer.setOutput(writer);
		
		serializer.startDocument(UTF_8, true);

		serializer.startTag(null, TAG_ENVELOPE);
		serializer.attribute(null, ATR_SOAP, ATR_SOAP_SCHEMA);

		serializer.startTag(null, TAG_BODY);
		
		serializer.startTag(null, TAG_GET_RASPOR);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);		
	
		serializer.startTag(null, TAG_DOC);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		
		SerializeRasporyazhenieNaOtgruzku(serializer);
		SerializeRasporyazhenieNaOtgruzku_Files(serializer);
		
		serializer.endTag(null, TAG_DOC);
		
		serializer.endTag(null, TAG_GET_RASPOR);		
		
		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE);       

		serializer.endDocument();
		
		return writer.toString();
	}
	private void SerializeRasporyazhenieNaOtgruzku(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		
		serializer.startTag(null, TAG_HEADER);
		
		serializer.startTag(null, TAG_USER);
		serializer.text( mRasporyazhenie.getOtvetstvennyyKod() );
		serializer.endTag(null, TAG_USER);
		
		serializer.startTag(null, TAG_COMMENT);
		serializer.text( mRasporyazhenie.getKommentariy() );
		serializer.endTag(null, TAG_COMMENT);
		
		serializer.startTag(null, TAG_KLIENT);
		serializer.text( mRasporyazhenie.getClientKod() );
		serializer.endTag(null, TAG_KLIENT);
		
		serializer.startTag(null, TAG_SUMM);
		serializer.text( DecimalFormatHelper.format(mRasporyazhenie.getSumma()) );
		serializer.endTag(null, TAG_SUMM);
		
		serializer.endTag(null, TAG_HEADER);
	}
	
    private void SerializeRasporyazhenieNaOtgruzku_Files(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {

		serializer.startTag(null, TAG_TP_FILE);
		
		ArrayList<String> files = mRasporyazhenie.getFiles();
		
		for(String file : files) {

			serializer.startTag(null, TAG_TP_STRING);
	
			serializer.startTag(null, TAG_FILE);
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() +"SerializeRasporyazhenieNaOtgruzku_Files begin "+ (new Long(System.currentTimeMillis())).toString() );
			//serializer.text( CameraCaptureHelper.BitmapToString(file) );
			serializer.text(Base64.encodeToString(SystemHelper.readBytesFromFile(new File(file)), Base64.DEFAULT));
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() +"SerializeRasporyazhenieNaOtgruzku_Files end "+ (new Long(System.currentTimeMillis())).toString() );
			
			serializer.endTag(null, TAG_FILE);
			
			serializer.startTag(null, TAG_RASSH);
			serializer.text( CameraCaptureHelper.JPEG_FILE_SUFFIX );
			serializer.endTag(null, TAG_RASSH);

			serializer.endTag(null, TAG_TP_STRING); 
		}
		
		serializer.endTag(null, TAG_TP_FILE);    	
	}
}
