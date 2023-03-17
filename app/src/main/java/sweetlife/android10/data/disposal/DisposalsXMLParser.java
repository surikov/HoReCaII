package sweetlife.android10.data.disposal;

import org.w3c.dom.Document;

import sweetlife.android10.net.ParserBase;

import android.content.res.Resources;

import sweetlife.android10.R;

public class DisposalsXMLParser extends ParserBase {

	@Override
	protected void ParseData( Document document ) {

		if(document.getElementsByTagName(RETURN).item(0).getChildNodes().item(0).getNodeValue().compareTo("true")== 0) {

			mParserResult = EParserResult.EComplete;
		}
		else {

			mParserResult = EParserResult.EError;
		}
	}

	public String getResponseParseResult(Resources res) {

		switch(mParserResult) {

		case EComplete:
			return res.getString(R.string.disposal_uploaded_successful);

		case EError:
			return res.getString(R.string.disposal_not_uploaded);

		default:
			return res.getString(R.string.bad_server_responce);
		}
	}
}
