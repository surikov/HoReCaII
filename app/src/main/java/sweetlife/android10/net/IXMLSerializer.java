package sweetlife.android10.net;

import java.io.IOException;

public interface IXMLSerializer {

	public static final String TAG_ENVELOPE = "soap:Envelope";
	public static final String ATR_SOAP = "xmlns:soap";
	public static final String ATR_SOAP_SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String ATR_XSD = "xmlns:xsd";
	public static final String ATR_XSD_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String ATR_XSI = "xmlns:xsi";
	public static final String ATR_XSI_SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String TAG_BODY = "soap:Body";
	public static final String UTF_8 = "UTF-8";
	public static final String TAG_DOCUMENT = "m:Document";
	public static final String ATR_M = "xmlns:m";
	public static final String ATR_XMLNS = "xmlns";

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public String SerializeXML() throws IllegalArgumentException, IllegalStateException, IOException;
}
