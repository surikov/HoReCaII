package sweetlife.android10.reports;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;

import android.util.Xml;


public class ReportsXMLSerializer implements IXMLSerializer {

	protected String ATR_M_SCHEMA            = "http://ws.swl/fileHRC";
	protected String TAG_GET_REPORT          = "m:getReport";

	protected String TAG_M_NAME              = "m:Имя";
	protected String TAG_M_BEGIN_PERIOD      = "m:НачалоПериода";
	protected String TAG_M_END_PERIOD        = "m:КонецПериода";
	protected String TAG_M_KOD_POLZOVATELYA  = "m:КодПользователя";
	protected String TAG_M_PARAMETERS        = "m:Параметры";
	protected String TAG_PARAM               = "Param";

	protected String ATR_PARAM_SCHEMA        = "http://ws.swl/Param";

	protected String TAG_NAME                = "Name";
	protected String TAG_VALUE               = "Value";
	protected String TAG_TIPE                = "Tipe";
	protected String TAG_TIPE_ELEM           = "TipeElem";

	protected String KONTRAGENTS            = "Контрагенты";
	protected String KONTRAGENT             = "Контрагент";
	protected String ARRAY                  = "Массив";
	protected String NUMBER                 = "Число";
	protected String EXPANDED               = "Развернутый";
	protected String VALUE                  = "Значение";
	protected String ONLY_GROUP_CONTRACTS   = "ТолькоДоговораГруппы";
	protected String ONLY_ONLY_SHIPPED      = "ТолькоОтгружено";
	protected String ONLY_ONLY_NOT_SHIPPED  = "ТолькоНеОтгружено";
	protected String ONLY_ONLY_NOT_COMPLETE = "ТолькоНеПроведенные";
	protected String SHIPPING_DATE_TODAY    = "ДатаОтгрузкиСегодня";
	protected String CLIENTS_ORDERS         = "ЗаказыПокупателей";
	protected String BEGIN_DATE             = "НачЗабития";
	protected String END_DATE               = "КонЗабития";
	protected String TRUE_RUS               = "Истина";
	protected String FALSE_RUS              = "Ложь";
	protected String BOOLEAN                = "Булево";
	protected String DATA                   = "Дата";

	protected ReportInfo mReportInfo;

	public ReportsXMLSerializer(ReportInfo info) {

		mReportInfo = info;
	}

	@Override
	public String SerializeXML() throws IllegalArgumentException,
	IllegalStateException, IOException {

		XmlSerializer serializer = Xml.newSerializer();

		StringWriter writer = new StringWriter(4096);
		serializer.setOutput(writer);

		serializer.startDocument(UTF_8, true);

		serializer.startTag(null, TAG_ENVELOPE);
		serializer.attribute(null, ATR_SOAP, ATR_SOAP_SCHEMA);

		serializer.startTag(null, TAG_BODY);

		serializer.startTag(null, TAG_GET_REPORT);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);	

		serializer.startTag(null, TAG_M_NAME);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		serializer.text( mReportInfo.getReportName() );
		serializer.endTag(null, TAG_M_NAME);

		serializer.startTag(null, TAG_M_BEGIN_PERIOD);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		serializer.text( mReportInfo.getBeginPeriod() );
		serializer.endTag(null, TAG_M_BEGIN_PERIOD);

		serializer.startTag(null, TAG_M_END_PERIOD);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		serializer.text( mReportInfo.getEndPeriod() );
		serializer.endTag(null, TAG_M_END_PERIOD);

		serializer.startTag(null, TAG_M_KOD_POLZOVATELYA);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		serializer.text( mReportInfo.getAgentKod() );
		serializer.endTag(null, TAG_M_KOD_POLZOVATELYA);

		serializer.startTag(null, TAG_M_PARAMETERS);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		
			
		SerializeParameters(serializer);

		serializer.endTag(null, TAG_M_PARAMETERS);
		
		serializer.endTag(null, TAG_GET_REPORT);

		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE); 

		serializer.endDocument();

		return writer.toString();
	}

	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {
		
	}
	
	private void SerializeParameter(XmlSerializer serializer, 
			String name, 
			String value,
			String tipe,
			String tipeElem ) 
					throws IllegalArgumentException, IllegalStateException, IOException {

		serializer.startTag(null, TAG_PARAM);
		serializer.attribute(null, ATR_XMLNS, ATR_PARAM_SCHEMA);	

		serializer.startTag(null, TAG_NAME);
		serializer.text( name );
		serializer.endTag(null, TAG_NAME);

		serializer.startTag(null, TAG_VALUE);
		serializer.text( value );
		serializer.endTag(null, TAG_VALUE);

		serializer.startTag(null, TAG_TIPE);
		serializer.text( tipe );
		serializer.endTag(null, TAG_TIPE);

		serializer.startTag(null, TAG_TIPE_ELEM);
		serializer.text( tipeElem );
		serializer.endTag(null, TAG_TIPE_ELEM);


		serializer.endTag(null, TAG_PARAM);
	}

	protected void SerializeArrayNumeric(XmlSerializer serializer, String name, String value ) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeParameter(serializer, name, value, ARRAY, NUMBER );
	}

	protected void SerializeValueNumeric(XmlSerializer serializer, String name, String value ) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeParameter(serializer, name, value, VALUE, NUMBER );
	}

	protected void SerializeValueBoolean(XmlSerializer serializer, String name, String value ) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeParameter(serializer, name, value, VALUE, BOOLEAN );
	}
	
	protected void SerializeValueDate(XmlSerializer serializer, String name, String value ) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeParameter(serializer, name, value, VALUE, DATA );
	}
}
