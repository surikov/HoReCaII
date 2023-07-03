package sweetlife.android10.gps;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;

import android.util.Xml;


public class VizitsXMLSerializer implements IXMLSerializer {

	private String ATR_M_SCHEMA = "http://ws.swlife.ru";
	private String ATR_M_SCHEMA_VIZITS = "http://ws.swlife.ru/visits";
	private String TAG_GET_VIZIT = "m:GetVisit";
	private String TAG_VIZIT = "m:Visit";
	private String TAG_PERSON = "Person";
	private String TAG_BEGIN = "Begin";
	private String TAG_END = "End";
	private String TAG_CLIENT = "Client";
	private String TAG_ACTIVITY = "Activity";
	private String TAG_EXT_NUMBER = "ExtNumber";
	private String TAG_Poyas = "Poyas";
	private String ATR_XMLNNS = "xmlns";

	private String mPerson;
	private String mBeginTime;
	private String mEndTime;
	private String mClient;
	private String mActivity;
	private String mExtNumber;

	public VizitsXMLSerializer(String person, String beginTime, String endTime,
							   String client, String activity, String extNumber) {
		System.out.println("VizitsXMLSerializer: " + beginTime + " -> " + endTime);
		mPerson = person;
		mBeginTime = beginTime;
		mEndTime = endTime;
		mClient = client;
		mActivity = activity;
		mExtNumber = extNumber;
	}

	@Override
	public String SerializeXML() throws IllegalArgumentException,
			IllegalStateException, IOException {
		TimeZone cuTZ = TimeZone.getDefault();
		XmlSerializer serializer = Xml.newSerializer();

		StringWriter writer = new StringWriter(4096);
		serializer.setOutput(writer);

		serializer.startDocument(UTF_8, true);

		serializer.startTag(null, TAG_ENVELOPE);
		serializer.attribute(null, ATR_SOAP, ATR_SOAP_SCHEMA);

		serializer.startTag(null, TAG_BODY);

		serializer.startTag(null, TAG_GET_VIZIT);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);

		serializer.startTag(null, TAG_VIZIT);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);

		serializer.startTag(null, TAG_PERSON);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text(mPerson);
		serializer.endTag(null, TAG_PERSON);

		serializer.startTag(null, TAG_BEGIN);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text(mBeginTime);
		serializer.endTag(null, TAG_BEGIN);

		serializer.startTag(null, TAG_END);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text(mEndTime);
		serializer.endTag(null, TAG_END);

		serializer.startTag(null, TAG_CLIENT);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text(mClient);
		serializer.endTag(null, TAG_CLIENT);

		serializer.startTag(null, TAG_ACTIVITY);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text("" + mActivity);
		serializer.endTag(null, TAG_ACTIVITY);

		serializer.startTag(null, TAG_EXT_NUMBER);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text(mExtNumber);
		serializer.endTag(null, TAG_EXT_NUMBER);


		//+"<m:Poyas>"	+ Math.round(cuTZ.getOffset(new Date().getTime())/(1000*60*60)) + "</m:Poyas>"//TAG_Poyas
		serializer.startTag(null, TAG_Poyas);
		serializer.attribute(null, ATR_XMLNNS, ATR_M_SCHEMA_VIZITS);
		serializer.text("" + Math.round(cuTZ.getOffset(new Date().getTime()) / (1000 * 60 * 60)));
		serializer.endTag(null, TAG_Poyas);

		serializer.endTag(null, TAG_VIZIT);

		serializer.endTag(null, TAG_GET_VIZIT);

		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE);

		serializer.endDocument();

		return writer.toString();
	}
}