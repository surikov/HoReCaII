package sweetlife.android10.gps;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;
import sweetlife.android10.supervisor.Cfg;

import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;


public class GPSPointsXMLSerializer implements IXMLSerializer {

	private String ATR_M_SCHEMA = "http://ws.swlife.ru";
	private String TAG_GET = "m:Get";
	private String TAG_PAKET = "m:Paket";
	private String TAG_COORD_GPS = "m:CoordGPS";
	private String TAG_TIME = "m:time";
	private String TAG_LAT = "m:lat";
	private String TAG_LONG = "m:long";
	private String TAG_USER = "m:user";

	private String mUserKod;
	private ArrayList<CoordGPS> mCoordGPSList;

	public GPSPointsXMLSerializer(SQLiteDatabase db, ArrayList<CoordGPS> coordGPSList) {

		//mUserKod = Requests.getTPCode(db
		//, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()
		//		);
		mUserKod = Cfg.findFizLicoKod(Cfg.whoCheckListOwner());
		mCoordGPSList = coordGPSList;
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

		serializer.startTag(null, TAG_GET);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);

		serializer.startTag(null, TAG_PAKET);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);

		SerializeCoords(serializer);

		serializer.startTag(null, TAG_USER);
		serializer.text(mUserKod);
		serializer.endTag(null, TAG_USER);

		serializer.endTag(null, TAG_PAKET);

		serializer.endTag(null, TAG_GET);

		serializer.endTag(null, TAG_BODY);

		serializer.endTag(null, TAG_ENVELOPE);

		serializer.endDocument();

		return writer.toString();
	}

	private void SerializeCoords(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {

		int count = mCoordGPSList.size();
		CoordGPS coord = null;

		for (int i = 0; i < count; i++) {

			coord = mCoordGPSList.get(i);

			serializer.startTag(null, TAG_COORD_GPS);

			serializer.startTag(null, TAG_TIME);
			serializer.text(coord.getTime());
			serializer.endTag(null, TAG_TIME);

			serializer.startTag(null, TAG_LAT);
			serializer.text(String.valueOf(coord.getLatitude()));
			serializer.endTag(null, TAG_LAT);

			serializer.startTag(null, TAG_LONG);
			serializer.text(String.valueOf(coord.getLongitude()));
			serializer.endTag(null, TAG_LONG);

			serializer.endTag(null, TAG_COORD_GPS);
		}
	}
}
