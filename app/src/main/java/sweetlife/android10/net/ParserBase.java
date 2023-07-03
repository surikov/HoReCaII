package sweetlife.android10.net;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class ParserBase implements IParserTags, IParserBase {

	protected EParserResult mParserResult;
	protected boolean mRequestStatus = false;

	public EParserResult Parse(String xmlString) throws ParserConfigurationException,
			SAXException, IOException, NumberFormatException, ParseException, Exception {

		Document document = InitializeDocument(xmlString);

		ParseData(document);

		return mParserResult;
	}

	protected Document InitializeDocument(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {

		StringReader stringReader = new StringReader(xmlString);

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);

			DocumentBuilder documentBuilder = docBuilderFactory
					.newDocumentBuilder();

			InputSource inputSource = new InputSource(stringReader);

			return documentBuilder.parse(inputSource);
		} finally {

			stringReader.close();
		}
	}

	protected String getElementValue(Element lmnt, String sElementName) {

		NodeList elementsList = lmnt.getElementsByTagName(sElementName);

		if (elementsList.getLength() == 0)
			return null;

		Element lmntElement = (Element) elementsList.item(0);
		elementsList = lmntElement.getChildNodes();

		int itemsCount = elementsList.getLength();

		if (itemsCount == 0)
			return null;

		String resultString = "";

		for (int i = 0; i < itemsCount; i++) {

			resultString = resultString
					+ ((Node) elementsList.item(i)).getNodeValue();
		}

		return resultString;
	}

	protected void ParseData(Document document) throws NumberFormatException,
			ParseException, Exception {

	}
}
