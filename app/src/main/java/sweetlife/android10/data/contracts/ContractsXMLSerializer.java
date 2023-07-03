package sweetlife.android10.data.contracts;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;


import android.util.Xml;

public class ContractsXMLSerializer implements IXMLSerializer {

	private static final String GET_CLIENTS_STATUS = "ПолучитьСтатусКлиентов";
	private static final String CONTRACT_CODES_LIST = "СписокКодовДоговоров";
	private static final String COD_CONTRACT = "CodContract";

	private ArrayList<String> mCodConstractList;

	ContractsXMLSerializer(ArrayList<String> codConstractList) {

		mCodConstractList = codConstractList;
	}

	@Override
	public String SerializeXML() throws IllegalArgumentException, IllegalStateException, IOException {

		XmlSerializer serializer = Xml.newSerializer();

		StringWriter writer = new StringWriter(4096);
		serializer.setOutput(writer);

		serializer.startDocument(UTF_8, true);

		serializer.startTag(null, TAG_ENVELOPE);
		serializer.attribute(null, ATR_SOAP, ATR_SOAP_SCHEMA);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);

		serializer.startTag(null, TAG_BODY);

		serializer.startTag(null, GET_CLIENTS_STATUS);
		serializer.attribute(null, "xmlns", "http://ws.swl/wsclientstatus");

		serializer.startTag(null, CONTRACT_CODES_LIST);

		for (String code : mCodConstractList) {

			serializer.startTag(null, COD_CONTRACT);
			serializer.text(code);
			serializer.endTag(null, COD_CONTRACT);
		}

		serializer.endTag(null, CONTRACT_CODES_LIST);

		serializer.endTag(null, GET_CLIENTS_STATUS);

		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE);

		serializer.endDocument();

		return writer.toString();
	}
}
