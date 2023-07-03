package sweetlife.android10.data.orders;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlSerializer;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.common.Sales;
import sweetlife.android10.database.Requests;
import sweetlife.android10.net.IXMLSerializer;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;

public class BidsXMLSerializer implements IXMLSerializer {
	private String TAG_UPLOAD_ORDER = "m:UploadOrder";
	private String ATR_M_SCHEMA = "http://ws.swl/wsuploadordersHRC";
	private String TAG_HEADER_DOC = "m:HeaderDoc";
	private String TAG_HELD = "m:Held";
	private String TAG_ON_FLY = "m:OnFly";
	private String TAG_PRICE_RESPONSE = "m:PriseResponse";
	private String TAG_TP_DOC = "m:TPDoc";
	private String TAG_TP_DOC_TRAF = "m:TPDocTraf";
	private String TAG_STRING_TP = "m:StringTP";
	private String ATR_COD_CLIENT = "CodClient";
	private String ATR_NUMBER_DOC = "NumberDoc";
	private String ATR_DATE_DOC = "DateDoc";
	private String ATR_CONTRACT = "Contract";
	private String ATR_TYPE_PAY = "TypePay";
	private String ATR_COD_SUBUNIT = "CodSubUnit";
	private String ATR_ZONE = "Zone";
	private String ATR_DATE_SHIPMENT = "DateShipment";
	private String ATR_COD_USER = "CodUser";
	private String ATR_COMMENT = "Comment";
	private String TAG_ARTICLE = "m:Article";
	private String TAG_QUANTITY = "m:Quantity";
	private String TAG_PRICE = "m:Price";
	private String TAG_SUM_STR = "m:SumStr";
	private String TAG_DISCONT_PRICE = "m:DiscountPrice";
	//	private String TAG_TYPE_OF_DISCONT   = "m:TypeOfDiscount";
	private String TAG_CR = "m:CR";
	private String TAG_DATE = "m:Date";
	private String TAG_COMMENT = "m:Comment";
	ZayavkaPokupatelya mZayavka;
	FoodstuffsData mFoodstuffData;
	ServicesData mServicesData;
	TraficsData mTraficsData;
	String mContractCode;

	public BidsXMLSerializer(SQLiteDatabase db, ZayavkaPokupatelya zayavka) {
		mZayavka = zayavka;
		mContractCode = Requests.getContractCodeByID(db, mZayavka.getDogovorKontragenta());
		mFoodstuffData = new FoodstuffsData(db, mZayavka);
		mServicesData = new ServicesData(db, mZayavka);
		mTraficsData = new TraficsData(db, mZayavka);
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
		serializer.startTag(null, TAG_UPLOAD_ORDER);
		serializer.attribute(null, ATR_M, ATR_M_SCHEMA);
		serializer.startTag(null, TAG_DOCUMENT);
		serializer.attribute(null, ATR_XSD, ATR_XSD_SCHEMA);
		serializer.attribute(null, ATR_XSI, ATR_XSI_SCHEMA);
		SerializeZayavka(serializer);
		serializer.startTag(null, TAG_TP_DOC);
		if (mFoodstuffData.getCount() != 0 || mServicesData.getCount() != 0) {
			SerializeZayavka_Tovary(serializer);
			SerializeZayavka_Uslugi(serializer);
		}
		serializer.endTag(null, TAG_TP_DOC);
		SerializeZayavka_Trafiks(serializer);
		serializer.endTag(null, TAG_DOCUMENT);
		serializer.endTag(null, TAG_UPLOAD_ORDER);
		serializer.endTag(null, TAG_BODY);
		serializer.endTag(null, TAG_ENVELOPE);
		serializer.endDocument();
		String xml = writer.toString();
		System.out.println(xml);
		/*if(xml.length()>0){
			int t=1/0;
		}*/
		return xml;
	}

	private void SerializeZayavka(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, TAG_HEADER_DOC);
		serializer.attribute(null, ATR_COD_CLIENT, mZayavka.getClientKod());
		serializer.attribute(null, ATR_NUMBER_DOC, mZayavka.getNomer());
		serializer.attribute(null, ATR_DATE_DOC, DateTimeHelper.SQLDateString(mZayavka.getDate()));
		serializer.attribute(null, ATR_CONTRACT, mContractCode);
		serializer.attribute(null, ATR_DATE_SHIPMENT, DateTimeHelper.SQLDateString(mZayavka.getShippingDate()));
		serializer.attribute(null, ATR_TYPE_PAY, mZayavka.getTipOplatyForUpload());

		//if(ApplicationHoreca.getInstance().currentHRCmarshrut.length()>1){
		if (Cfg.isChangedHRC()) {
			//serializer.attribute(null, ATR_COD_USER, ApplicationHoreca.getInstance().currentHRCmarshrut);
			serializer.attribute(null, ATR_COD_USER, Cfg.selectedOrDbHRC());
		} else {
			serializer.attribute(null, ATR_COD_USER, mZayavka.getOtvetstvennyyKod());
		}


		serializer.attribute(null, ATR_COD_SUBUNIT, ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieKod());
		serializer.attribute(null, ATR_ZONE, "");
		serializer.attribute(null, ATR_COMMENT, mZayavka.getComment());
		serializer.startTag(null, TAG_HELD);
		serializer.text(TRUE);
		serializer.endTag(null, TAG_HELD);
		serializer.startTag(null, TAG_ON_FLY);
		serializer.text(FALSE);
		serializer.endTag(null, TAG_ON_FLY);
		serializer.startTag(null, TAG_PRICE_RESPONSE);
		serializer.text(TRUE);
		serializer.endTag(null, TAG_PRICE_RESPONSE);
		serializer.startTag(null, "m:Samovivoz");
		if (mZayavka.dostavkaKind == mZayavka.DOSTAVKA_OBICHNAYA) {
			serializer.text(FALSE);
		} else {
			serializer.text(TRUE);
		}
		serializer.endTag(null, "m:Samovivoz");
		if (mZayavka.dostavkaKind == mZayavka.DOSTAVKA_OBICHNAYA) {
			//
		} else {
			serializer.startTag(null, "m:SposobPrinyatia");
			if (mZayavka.dostavkaKind == mZayavka.DOSTAVKA_DOVERENNOST) {
				serializer.text("Доверенность");
			} else {
				serializer.text("Печать");
			}
			serializer.endTag(null, "m:SposobPrinyatia");
			serializer.startTag(null, "m:KommentSamovivoz");
			serializer.text(mZayavka.dostvkaKoment);
			serializer.endTag(null, "m:KommentSamovivoz");
		}
		String DataNackl = "20141128";
		Date d = new Date();
		d.setTime((long) mZayavka.dostvkaVozvrNakl);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		DataNackl = format.format(d);
		//mZayavka.dostvkaVozvrNakl
		serializer.startTag(null, "m:DataNackl");
		serializer.text(DataNackl);
		serializer.endTag(null, "m:DataNackl");
		serializer.endTag(null, TAG_HEADER_DOC);
	}

	private void SerializeZayavka_Tovary(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		int count = mFoodstuffData.getCount();
		ZayavkaPokupatelya_Foodstaff foodstaff = null;
		for (int i = 0; i < count; i++) {
			foodstaff = mFoodstuffData.getFoodstuff(i);
			serializer.startTag(null, TAG_STRING_TP);
			serializer.startTag(null, TAG_ARTICLE);
			serializer.text(foodstaff.getArtikul());
			serializer.endTag(null, TAG_ARTICLE);
			serializer.startTag(null, TAG_QUANTITY);
			serializer.text(DecimalFormatHelper.format(foodstaff.getKolichestvo()));
			serializer.endTag(null, TAG_QUANTITY);
			serializer.startTag(null, TAG_PRICE);
			serializer.text(DecimalFormatHelper.format(foodstaff.getCena()));
			serializer.endTag(null, TAG_PRICE);
			serializer.startTag(null, TAG_SUM_STR);
			serializer.text(DecimalFormatHelper.format(foodstaff.getSummaSoSkidkoy()));
			serializer.endTag(null, TAG_SUM_STR);
			serializer.startTag(null, TAG_DISCONT_PRICE);
			serializer.text(DecimalFormatHelper.format(foodstaff.getCenaSoSkidkoy()));
			serializer.endTag(null, TAG_DISCONT_PRICE);
			serializer.startTag(null, TAG_CR);
			//System.out.println("::::::::::::::::::"+foodstaff.getVidSkidki());
			serializer.text(foodstaff.getVidSkidki().equals(Sales.CR_NAME) ? TRUE : FALSE);
			//serializer.text( foodstaff.getVidSkidki().compareTo(Sales.CR_ID) == 0 ? TRUE : FALSE );			
			//serializer.text(foodstaff.CRbyHands ? TRUE : FALSE );
			//System.out.println();
			serializer.endTag(null, TAG_CR);
			serializer.endTag(null, TAG_STRING_TP);
		}
	}

	private void SerializeZayavka_Uslugi(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		int count = mServicesData.getCount();
		ZayavkaPokupatelya_Service service = null;
		for (int i = 0; i < count; i++) {
			service = mServicesData.getService(i);
			serializer.startTag(null, TAG_STRING_TP);
			serializer.startTag(null, TAG_ARTICLE);
			serializer.text(service.getArtikul());
			serializer.endTag(null, TAG_ARTICLE);
			serializer.startTag(null, TAG_QUANTITY);
			serializer.text(DecimalFormatHelper.format(service.getKolichestvo()));
			serializer.endTag(null, TAG_QUANTITY);
			serializer.startTag(null, TAG_PRICE);
			serializer.text(DecimalFormatHelper.format(service.getCena()));
			serializer.endTag(null, TAG_PRICE);
			serializer.startTag(null, TAG_SUM_STR);
			serializer.text(DecimalFormatHelper.format(service.getSumma()));
			serializer.endTag(null, TAG_SUM_STR);
			serializer.startTag(null, TAG_DISCONT_PRICE);
			serializer.text(DecimalFormatHelper.format(service.getCena()));
			serializer.endTag(null, TAG_DISCONT_PRICE);
			serializer.startTag(null, TAG_CR);
			serializer.text(FALSE);
			serializer.endTag(null, TAG_CR);
			serializer.endTag(null, TAG_STRING_TP);
		}
	}

	private void SerializeZayavka_Trafiks(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, TAG_TP_DOC_TRAF);
		int count = mTraficsData.getCount();
		ZayavkaPokupatelya_Trafik trafik = null;
		for (int i = 0; i < count; i++) {
			trafik = mTraficsData.getTrafik(i);
			serializer.startTag(null, TAG_STRING_TP);
			serializer.startTag(null, TAG_ARTICLE);
			serializer.text(trafik.getArtikul());
			serializer.endTag(null, TAG_ARTICLE);
			serializer.startTag(null, TAG_QUANTITY);
			serializer.text(DecimalFormatHelper.format(trafik.getKolichestvo()));
			serializer.endTag(null, TAG_QUANTITY);
			serializer.startTag(null, TAG_DATE);
			serializer.text(DateTimeHelper.SQLDateString(trafik.getData()));
			serializer.endTag(null, TAG_DATE);
			serializer.startTag(null, TAG_COMMENT);
			serializer.text(String.valueOf(trafik.getKommentariy()));
			serializer.endTag(null, TAG_COMMENT);

			serializer.startTag(null, "m:vs");
			serializer.text(trafik.vetSpravka ? "да" : "нет");
			serializer.endTag(null, "m:vs");

			serializer.endTag(null, TAG_STRING_TP);
		}
		serializer.endTag(null, TAG_TP_DOC_TRAF);
	}
}
