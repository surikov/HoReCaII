package sweetlife.android10.data.orders;

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

import sweetlife.android10.R;

public class BidsXMLParser extends DefaultHandler implements IParserBase {
	public class Deficit {
		public String article;
		public String naimenovanie;
		public String count;
	}

	private String TAG_HEADER_DOC = "HeaderDoc";
	private String TAG_RETURN = "return";
	private String TAG_HELD = "Held";
	private String ATR_NUMBER_DOC = "NumberDoc";
	private String ATR_COMMENT = "Comment";
	private String TAG_STRING_TP = "StringTP";
	private String TAG_ARTICLE = "Article";
	private String TAG_DEFICIT = "Deficit";
	private ArrayList<ZakazPokupatelya> mOrders;
	private ZakazPokupatelya mCurrentOrder;
	private boolean mIsTrafiksOnly;
	private ArrayList<Deficit> mDeficits;
	private Deficit mCurrentDeficit;
	private String mContentValue;

	public BidsXMLParser() {
		mIsTrafiksOnly = false;
	}

	@Override
	public EParserResult Parse(String xmlString) throws ParserConfigurationException, SAXException, IOException, NumberFormatException, ParseException, Exception {
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(this);
		InputSource inputSource = new InputSource(new StringReader(xmlString));
		reader.parse(inputSource);
		deleteEmpty();
		if (mOrders.size() > 0 || mIsTrafiksOnly) {
			return EParserResult.EComplete;
		}
		return EParserResult.EError;
	}

	@Override
	public String getResponseParseResult(Resources res) {
		if (mOrders.size() > 0) {
			String resultString = res.getString(R.string.bid_uploaded);
			for (ZakazPokupatelya order : mOrders) {
				resultString = resultString + " " + order.getComment() + "\n";
			}
			return resultString;
		} else if (mIsTrafiksOnly) {
			return res.getString(R.string.bid_trafik_uploaded);
		}
		return res.getString(R.string.bid_not_uploaded);
	}

	public ArrayList<Deficit> getDeficits() {
		return mDeficits;
	}

	private void deleteEmpty() {
		for (int i = 0; i < mOrders.size(); i++) {
			if (mOrders.get(i).getComment().length() == 0 && mOrders.get(i).getHeld().compareTo("false") == 0) {
				mOrders.remove(i);
			}
		}
	}

	@Override
	public void startDocument() throws SAXException {
		mOrders = new ArrayList<ZakazPokupatelya>();
		mDeficits = new ArrayList<Deficit>();
		super.startDocument();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (TAG_HEADER_DOC.compareToIgnoreCase(localName) == 0) {
			mCurrentOrder = new ZakazPokupatelya();
			mCurrentOrder.setNumberDoc(atts.getValue(atts.getIndex(ATR_NUMBER_DOC)));
			mCurrentOrder.setComment(atts.getValue(atts.getIndex(ATR_COMMENT)));
		} else if (TAG_RETURN.compareToIgnoreCase(localName) == 0) {
			mIsTrafiksOnly = true;
		} else if (TAG_STRING_TP.compareToIgnoreCase(localName) == 0) {
			mCurrentDeficit = new Deficit();
		}
		mContentValue = "";
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (TAG_HEADER_DOC.compareToIgnoreCase(localName) == 0) {
			mOrders.add(mCurrentOrder);
			mCurrentOrder = null;
		} else if (TAG_HELD.compareToIgnoreCase(localName) == 0) {
			mCurrentOrder.setHeld(mContentValue);
		} else if (TAG_ARTICLE.compareToIgnoreCase(localName) == 0) {
			mCurrentDeficit.article = mContentValue;
		} else if (TAG_DEFICIT.compareToIgnoreCase(localName) == 0) {
			mCurrentDeficit.count = mContentValue;
		} else if (TAG_STRING_TP.compareToIgnoreCase(localName) == 0) {
			if (mCurrentDeficit.count.compareTo("0") == 0) {
				mDeficits.add(mCurrentDeficit);
			}
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		mContentValue = mContentValue + new String(ch, start, length);
	}
}
