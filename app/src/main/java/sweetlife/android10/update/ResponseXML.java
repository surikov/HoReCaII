package sweetlife.android10.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.consts.IDeltaTags;

import android.util.Xml;

public class ResponseXML implements IDeltaTags {
	private DeltaData mDeltaData;

	public ResponseXML(DeltaData data) {
		this.mDeltaData = data;
	}
	public void create(String sPathToFile) {
		if (mDeltaData == null) {
			return;
		}
		File response_xml_file = new File(sPathToFile);
		try {
			response_xml_file.createNewFile();
		}
		catch (IOException e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "ResponseXML Exception in createNewFile() method -" + e);
			return;
		}
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(response_xml_file);
		}
		catch (FileNotFoundException e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "ResponseXML Can't create FileOutputStream -" + e);
			return;
		}
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, NODE_MESSAGE);
			serializer.attribute(null, "xmlns:v8msg", "http://v8.1c.ru/messages");
			serializer.startTag(null, NODE_HEADER);
			serializer.startTag(null, NODE_EXCHANGE_PLAN);
			serializer.text(mDeltaData.getExchangePlan());
			//serializer.attribute(null, "attribute", "value");
			serializer.endTag(null, NODE_EXCHANGE_PLAN);
			serializer.startTag(null, NODE_TO);
			serializer.text(mDeltaData.getFrom());
			serializer.endTag(null, NODE_TO);
			serializer.startTag(null, NODE_FROM);
			serializer.text(mDeltaData.getTo());
			serializer.endTag(null, NODE_FROM);
			serializer.startTag(null, NODE_MESSAGE_NO);
			serializer.text(String.format("%d", mDeltaData.getReceivedNo() + 1));
			serializer.endTag(null, NODE_MESSAGE_NO);
			serializer.startTag(null, NODE_RECEIVE_NO);
			serializer.text(String.format("%d", mDeltaData.getMessageNo()));
			serializer.endTag(null, NODE_RECEIVE_NO);
			serializer.endTag(null, NODE_HEADER);
			serializer.startTag(null, NODE_BODY);
			serializer.endTag(null, NODE_BODY);
			serializer.endTag(null, NODE_MESSAGE);
			serializer.endDocument();
			serializer.flush();
			fileos.close();
		}
		catch (Exception e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "ResponseXML Error occurred while creating xml file -" + e);
			return;
		}
	}
}
