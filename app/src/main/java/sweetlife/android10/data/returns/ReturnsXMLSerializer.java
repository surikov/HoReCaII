package sweetlife.android10.data.returns;

import java.io.*;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.net.IXMLSerializer;
import sweetlife.android10.utils.CameraCaptureHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.utils.SystemHelper;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Xml;

public class ReturnsXMLSerializer implements IXMLSerializer {
	private String TAG_UPLOAD_VOZV = "m:UploadVozv";
	private String ATR_M_SCHEMA = "http://ws.swl/wsuploadvozvHRC";
	private String TAG_HEADER_DOC = "m:HeaderDoc";
	private String TAG_HELD = "m:Held";
	private String TAG_TP_DOC = "m:TPDoc";
	private String TAG_STRING_TP = "m:StringTP";
	private String ATR_COD_CLIENT = "CodClient";
	private String ATR_NUMBER_DOC = "NumberDoc";
	private String ATR_DATE_DOC = "DateDoc";
	private String ATR_DATE_SHIPMENT = "DateShipment";
	private String ATR_COD_USER = "CodUser";
	private String ATR_COMMENT = "Comment";
	private String ATR_DATE_NAC = "DateNac";
	private String ATR_NUMBER_NAC = "NumberNac";
	private String TAG_ARTICLE = "m:Article";
	private String TAG_QUANTITY = "m:Quantity";
	private String TAG_PRIM = "m:Prim";
	private String TAG_ACT_CLAIMS = "m:ActClaims";
	private String TAG_NAME_ACT_CLAIMS = "m:NameActClaims";
	ZayavkaNaVozvrat mZayavka;
	ReturnsNomenclatureData mReturnsNomenclatureData;
	Activity mActivity;

	public ReturnsXMLSerializer(SQLiteDatabase db, ZayavkaNaVozvrat zayavka) {
		mZayavka = zayavka;
		mReturnsNomenclatureData = new ReturnsNomenclatureData(db, mZayavka);
	}

	@Override
	public String SerializeXML() throws IllegalArgumentException, IllegalStateException, IOException {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter(4096);
		serializer.setOutput(writer);
		serializer.startDocument(UTF_8, true);
		serializer.startTag(null, TAG_ENVELOPE);
		serializer.attribute(null, ATR_SOAP, ATR_SOAP_SCHEMA);
		serializer.startTag(null, TAG_BODY);
		serializer.startTag(null, TAG_UPLOAD_VOZV);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);
		serializer.startTag(null, TAG_DOCUMENT);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		SerializeZayavkaNaVozvrat(serializer);
		SerializeZayavkaNaVozvrat_Tovary(serializer);
		serializer.endTag(null, TAG_DOCUMENT);
		serializer.endTag(null, TAG_UPLOAD_VOZV);
		serializer.endTag(null, TAG_BODY);
		serializer.endTag(null, TAG_ENVELOPE);
		serializer.endDocument();
		//System.out.println("ReturnsXMLSerializer "+writer.toString());
		return writer.toString();
	}

	private void SerializeZayavkaNaVozvrat(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, TAG_HEADER_DOC);
		serializer.attribute(null, ATR_COD_CLIENT, mZayavka.getClientKod());
		serializer.attribute(null, ATR_NUMBER_DOC, mZayavka.getNomer());
		serializer.attribute(null, ATR_DATE_DOC, DateTimeHelper.SQLDateString(mZayavka.getDate()));
		serializer.attribute(null, ATR_DATE_SHIPMENT, DateTimeHelper.SQLDateString(mZayavka.getDataOtgruzki()));
		serializer.attribute(null, ATR_COD_USER, mZayavka.getOtvetstvennyyKod());
		serializer.attribute(null, ATR_COMMENT, mZayavka.getComment());
		serializer.startTag(null, TAG_HELD);
		serializer.text(TRUE);
		serializer.endTag(null, TAG_HELD);
		serializer.endTag(null, TAG_HEADER_DOC);
	}

	private void SerializeZayavkaNaVozvrat_Tovary(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, TAG_TP_DOC);
		int count = mReturnsNomenclatureData.getCount();
		ZayavkaNaVozvrat_Tovary tovar = null;
		for (int i = 0; i < count; i++) {
			tovar = mReturnsNomenclatureData.getNomenclature(i);

			int prichina = tovar.getPrichina();
			int TovarnyVid = 0;
			if (prichina >= 100) {
				prichina = prichina - 100;
				TovarnyVid = 1;
			}

			serializer.startTag(null, TAG_STRING_TP);
			serializer.attribute(null, "TovarnyVid", ("" + TovarnyVid));

			serializer.attribute(null, ATR_DATE_NAC, DateTimeHelper.SQLDateString(tovar.getDataNakladnoy().getTime()));

			serializer.attribute(null, ATR_NUMBER_NAC, tovar.getNomerNakladnoy());
			serializer.startTag(null, TAG_ARTICLE);
			serializer.text(tovar.getArtikul());
			serializer.endTag(null, TAG_ARTICLE);
			serializer.startTag(null, TAG_QUANTITY);
			serializer.text(DecimalFormatHelper.format3(tovar.getKolichestvo()));
			serializer.endTag(null, TAG_QUANTITY);
			serializer.startTag(null, TAG_PRIM);
			//serializer.text(String.valueOf(tovar.getPrichina()));
			serializer.text(String.valueOf(prichina));

			serializer.endTag(null, TAG_PRIM);
			/*
			String path = mZayavka.getAktPretenziyPath();
			String fileName = CameraCaptureHelper.getFileName(path);
			if (fileName.length() != 0) {
				String encodedFile="";
				try{
				encodedFile=Base64.encodeToString(SystemHelper.readBytesFromFile(new File(path)), Base64.DEFAULT);
				}catch(Throwable t){
					t.printStackTrace();
				}
				serializer.startTag(null, TAG_ACT_CLAIMS);
				//serializer.text( CameraCaptureHelper.BitmapToString(path) );
				serializer.text(encodedFile);
						//Base64.encodeToString(SystemHelper.readBytesFromFile(new File(path)), Base64.DEFAULT));
				serializer.endTag(null, TAG_ACT_CLAIMS);
				serializer.startTag(null, TAG_NAME_ACT_CLAIMS);
				serializer.text(fileName);
				serializer.endTag(null, TAG_NAME_ACT_CLAIMS);
			}
			*/
			serializer.endTag(null, TAG_STRING_TP);
		}
		serializer.endTag(null, TAG_TP_DOC);
		serializer.startTag(null, "m:tpFile");
		String path = mZayavka.getAktPretenziyPath();

		String fileName = CameraCaptureHelper.getFileName(path);
		if (fileName.length() != 0) {
			String encodedFile = "";
			String rash = ".txt";
			try {
				encodedFile = Base64.encodeToString(SystemHelper.readBytesFromFile(new File(path)), Base64.DEFAULT);
				String[] nameSplit = path.split("[.]");
				rash = "." + nameSplit[nameSplit.length - 1];
			} catch (Throwable t) {
				t.printStackTrace();
			}
			serializer.startTag(null, "m:tpString");
			serializer.startTag(null, "m:File");
			serializer.text(encodedFile);
			serializer.endTag(null, "m:File");
			serializer.startTag(null, "m:rassh");
			serializer.text(rash);
			serializer.endTag(null, "m:rassh");
			serializer.endTag(null, "m:tpString");
		}
		serializer.endTag(null, "m:tpFile");
	}
}
