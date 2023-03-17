package sweetlife.android10.data.contracts;

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

import sweetlife.android10.consts.ITableColumnsNames;


public class ContractsXMLParser extends DefaultHandler implements ITableColumnsNames {
	
	public static final String TAG_STRING_STATUS = "StringTStatus";
	public static final String TAG_COD_CLIENT    = "rCodClient";
	public static final String TAG_CLIENT_STATUS = "rClientStatus";
	public static final String TAG_COD_CONTRACT  = "rCodContract";

	public static final String OPEN = "Открыт";
	public static final String DELETION_MARK = "Пом. Уд.";
	public static final String CLOSE = "Закрыт";

	private ArrayList<ContractStatus> mContractsList;
	private String                    mContentValue;
	private ContractStatus            mCurrentContract;
	
	public ArrayList<ContractStatus> Parse(String xmlString)
			throws ParserConfigurationException, SAXException, IOException,
			NumberFormatException, ParseException, Exception {

		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

		XMLReader reader = saxParser.getXMLReader();

		reader.setContentHandler(this);
		
		InputSource inputSource = new InputSource(new StringReader(xmlString));

		reader.parse(inputSource);

		return mContractsList;
	}

	@Override
	public void startDocument() throws SAXException {

		mContractsList = new ArrayList<ContractStatus>();
		super.startDocument();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if( TAG_STRING_STATUS.compareToIgnoreCase(localName) == 0 ) {

			mCurrentContract = new ContractStatus();
		}
		
		mContentValue = "";
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		if( TAG_CLIENT_STATUS.compareToIgnoreCase(localName) == 0 ) {

			mCurrentContract.setClose(getClose(mContentValue));
			mCurrentContract.setDeletionMark(getDeletionMark(mContentValue));
		}
		else if( TAG_COD_CONTRACT.compareToIgnoreCase(localName) == 0 ) {

			mCurrentContract.setCode(mContentValue);
		}
		else if( TAG_STRING_STATUS.compareToIgnoreCase(localName) == 0 ) {

			mContractsList.add(mCurrentContract);
		}
	}

	private String getClose( String status ) {
		
		if( OPEN.compareToIgnoreCase(status) == 0 ) {

			return "x'00'";
		}
		else {

			return "x'01'";
		}
	}
	
	private String getDeletionMark( String status ) {
		
		if( OPEN.compareToIgnoreCase(status) == 0 ||  CLOSE.compareToIgnoreCase(status) == 0  ) {

			return "x'00'";
		}
		else {

			return "x'01'";
		}		
	}
	
	@Override
	public void characters(char ch[], int start, int length) {

		mContentValue = mContentValue + new String( ch, start, length );
	}
}
