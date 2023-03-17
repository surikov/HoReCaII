package sweetlife.android10.data.fixedprices;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;


public class FixedPricesXMLSerializer implements IXMLSerializer {

	String TAG_UPLOAD_REQUEST  = "m:UploadRequest";
	String ATR_M_SCHEMA        = "http://ws.swl/RequestForDiscount";
	String TAG_HEADER          = "m:Header";
	String TAG_HELD            = "m:Held";
	String TAG_NUMBER          = "m:Number";
	String TAG_DATE            = "m:Date";
	String TAG_USER            = "m:User";
	String TAG_COMMENT         = "m:Comment";
	String TAG_CLIENT          = "m:Client";
	String TAG_DATE_BEGIN      = "m:DateBegin";
	String TAG_DATE_END        = "m:DateEnd";
	String TAG_TYPE_OF_DISCONT = "m:TypeOfDiscount";
	String TAG_TP_FIX_PRICE    = "m:tpFixPrice";
	String TAG_TP_STRING       = "m:tpString";
	String TAG_ARTICLE         = "m:Article";
	String TAG_PRICE           = "m:Price";
	String TAG_OB              = "m:Ob";
	
	String CR                 = "ЦеновоеРеагирование";

	ZayavkaNaSkidki             mZayavka;
	FixedPricesNomenclatureData mFixedPricesNomenclatureData;
	
	public FixedPricesXMLSerializer( SQLiteDatabase db, ZayavkaNaSkidki zayavka) {
	
		mZayavka = zayavka;
		mFixedPricesNomenclatureData = new FixedPricesNomenclatureData(db,mZayavka);
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
		
		serializer.startTag(null, TAG_UPLOAD_REQUEST);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);		
	
		serializer.startTag(null, TAG_DOCUMENT);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		
		SerializeZayavkaNaSkidki(serializer);
		SerializeZayavkaNaSkidki_TovaryPhiksCen(serializer);
		
		serializer.endTag(null, TAG_DOCUMENT);
		
		serializer.endTag(null, TAG_UPLOAD_REQUEST);		
		
		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE);       

		serializer.endDocument();
		
		return writer.toString();
	}

	private void SerializeZayavkaNaSkidki(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		
		serializer.startTag(null, TAG_HEADER);
		
		
		serializer.startTag(null, TAG_NUMBER);
		serializer.text( mZayavka.getNomer() );
		serializer.endTag(null, TAG_NUMBER);
	
		serializer.startTag(null, TAG_DATE);
		serializer.text( DateTimeHelper.SQLDateString(mZayavka.getDate()) );
		serializer.endTag(null, TAG_DATE);
		
		serializer.startTag(null, TAG_USER);
		serializer.text( mZayavka.getOtvetstvennyyKod() );
		serializer.endTag(null, TAG_USER);
		
		serializer.startTag(null, TAG_COMMENT);
		serializer.text( mZayavka.getKommentariy() );
		serializer.endTag(null, TAG_COMMENT);
		
		serializer.startTag(null, TAG_CLIENT);
		serializer.text( mZayavka.getClientKod() );
		serializer.endTag(null, TAG_CLIENT);
		
		serializer.startTag(null, TAG_HELD);
		serializer.text( FALSE );
		serializer.endTag(null, TAG_HELD);
		
		serializer.startTag(null, TAG_DATE_BEGIN);
		serializer.text( DateTimeHelper.SQLDateString(mZayavka.getVremyaNachalaSkidkiPhiksCen()) + "T00:00:00" );
		serializer.endTag(null, TAG_DATE_BEGIN);
		
		serializer.startTag(null, TAG_DATE_END);
		serializer.text( DateTimeHelper.SQLDateString(mZayavka.getVremyaOkonchaniyaSkidkiPhiksCen()) + "T23:59:59" );
		serializer.endTag(null, TAG_DATE_END);
		
		serializer.startTag(null, TAG_TYPE_OF_DISCONT);
		serializer.text( CR );
		serializer.endTag(null, TAG_TYPE_OF_DISCONT);
		
		serializer.endTag(null, TAG_HEADER);
	}
	
    private void SerializeZayavkaNaSkidki_TovaryPhiksCen(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {

		serializer.startTag(null, TAG_TP_FIX_PRICE);
		
		int count = mFixedPricesNomenclatureData.getCount();
		ZayavkaNaSkidki_TovaryPhiksCen tovar = null;
		
		for(int i = 0; i < count; i++) {
			
			tovar = mFixedPricesNomenclatureData.getNomenclature(i);

			serializer.startTag(null, TAG_TP_STRING);
	
			serializer.startTag(null, TAG_ARTICLE);
			serializer.text( tovar.getArtikul() );
			serializer.endTag(null, TAG_ARTICLE);
			
			serializer.startTag(null, TAG_PRICE);
			serializer.text( DecimalFormatHelper.format(tovar.getCena()) );
			serializer.endTag(null, TAG_PRICE);
			
			serializer.startTag(null, TAG_OB);
			serializer.text( DecimalFormatHelper.format(tovar.getObyazatelstva()) );
			serializer.endTag(null, TAG_OB);
			
			serializer.endTag(null, TAG_TP_STRING); 
		}
		
		serializer.endTag(null, TAG_TP_FIX_PRICE);    	
	}
}
