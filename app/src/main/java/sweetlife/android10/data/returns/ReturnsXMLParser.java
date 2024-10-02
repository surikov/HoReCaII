package sweetlife.android10.data.returns;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import sweetlife.android10.net.IParserBase;

import android.content.res.Resources;

import sweetlife.android10.*;

public class ReturnsXMLParser extends DefaultHandler implements IParserBase {

	private String TAG_PRICHINA = "Prichina";

	StringBuilder result = null;


	private ArrayList<String> mReasons;
	private String mContentValue;

	@Override
	public EParserResult Parse(String xmlString)
			throws ParserConfigurationException, SAXException, IOException,
			NumberFormatException, ParseException, Exception {

		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

		XMLReader reader = saxParser.getXMLReader();

		reader.setContentHandler(this);

		InputSource inputSource = new InputSource(new StringReader(xmlString));

		reader.parse(inputSource);

		if (mReasons.size() > 0) {

			return EParserResult.EError;
		}
		return EParserResult.EComplete;
	}

	@Override
	public void startDocument() throws SAXException {

		mReasons = new ArrayList<String>();
		super.startDocument();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		mContentValue = "";
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		if (TAG_PRICHINA.compareToIgnoreCase(localName) == 0) {

			mReasons.add(mContentValue);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {

		mContentValue = mContentValue + new String(ch, start, length);
	}


	public String getResponseParseResult(Resources res) {

		if (mReasons.size() > 0) {

			String resultString = res.getString(R.string.bid_not_uploaded) + "\n";

			for (String reason : mReasons) {

				resultString = resultString + " " + reason + "\n";
			}

			return resultString;
		}
		return res.getString(R.string.bid_uploaded_successful);
	}
}
