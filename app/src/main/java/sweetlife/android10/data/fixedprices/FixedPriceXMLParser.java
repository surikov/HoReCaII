package sweetlife.android10.data.fixedprices;

import org.w3c.dom.Document;

import sweetlife.android10.net.ParserBase;

import android.content.res.Resources;

public class FixedPriceXMLParser extends ParserBase {
	public String comment = "?";

	@Override
	protected void ParseData(Document document) {

		if (document.getElementsByTagName(RETURN).item(0).getChildNodes().item(0).getNodeValue().compareTo("Выполнено") == 0) {

			mParserResult = EParserResult.EComplete;
		} else {

			mParserResult = EParserResult.EError;
		}

		comment = document.getElementsByTagName(RETURN).item(0).getChildNodes().item(0).getNodeValue();
		//System.out.println("FixedPriceXMLParser.ParseData "+comment);

	}

	public String getResponseParseResult(Resources res) {
/*
		switch(mParserResult) {
		
		case EComplete:
			return res.getString(R.string.bid_uploaded_successful);
		
		case EError:
			return res.getString(R.string.bid_not_uploaded);

		default:
			return res.getString(R.string.bad_server_responce);
		}
		*/
		return comment;
	}
}
