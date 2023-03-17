package sweetlife.android10.net;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.res.Resources;

public interface IParserBase {
	
	String RETURN = "m:return";
	
	enum EParserResult {
		
		EComplete,
		EError
	};
	public EParserResult Parse(String xmlString) throws ParserConfigurationException,
	SAXException, IOException, NumberFormatException, ParseException, Exception;
	
	public String getResponseParseResult(Resources res);
}
