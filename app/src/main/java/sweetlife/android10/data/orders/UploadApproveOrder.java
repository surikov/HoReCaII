package sweetlife.android10.data.orders;

import java.io.IOException;
import java.text.*;
import java.util.ArrayList;

import org.acra.ErrorReporter;
import org.apache.http.HttpStatus;

import tee.binding.*;

import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import sweetlife.android10.Settings;
import sweetlife.android10.database.Request_Contracts;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.*;

public class UploadApproveOrder extends ManagedAsyncTask<String> {
	public final static int ThatDone_APPROVE = 1;
	public final static int ThatDone_DROP = 2;
	public final static int ThatDone_CHANGE = 3;

	public final static int ThatDone_MOVE_DATE = 5;//DateOtgruz


	public final static int ThatDone_GET = -12345;
	private SQLiteDatabase mDB;
	//public static int SUCCESS = 0;
	//public static int ERROR = 1;
	private int TIMEOUT = 300 * 1000;
	int thatDone;
	String docNumber;
	String createDate;
	String cmnt;
	String shipDate;
	Bough body;

	//private static final String functionName = "Change";
	//private static final String CONTRACT_CODES_LIST = "СписокКодовДоговоров";
	//private static final String COD_CONTRACT = "CodContract";
	public UploadApproveOrder(String progressDialogMessage, Context appContext, SQLiteDatabase db//
			, int thatDone//
			, String mdocNumber//
			, String mcreateDate//

			, String mshipDate//
			, Bough mbody
			, String mComment//
	) {
		super(progressDialogMessage, appContext);
		mDB = db;
		this.thatDone = thatDone;
		this.docNumber = mdocNumber;
		this.createDate = reformat(mcreateDate);
		this.shipDate = reformat(mshipDate);
		cmnt = mComment;
		this.body = mbody;
		//createDate = reformat(createDate);
		//shipDate = reformat(shipDate);
	}

	String reformat(String d) {
		//System.out.println("d was "+d);
		try {
			DateFormat from = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat to = new SimpleDateFormat("yyyy-MM-dd");
			d = to.format(from.parse(d));
		} catch (Throwable t) {
			LogHelper.debug(this.getClass().getCanonicalName() + " reformat " + d + " " + t.getMessage());
		}
		//System.out.println("d now is "+d);
		return d;
	}

	public String SerializeXMLThatDone_APPROVE() throws IllegalArgumentException, IllegalStateException, IOException {
		Bough bough = new Bough().name.is("S:Envelope");
		bough.child("xmlns:S").attribute.is(true).value.is("http://schemas.xmlsoap.org/soap/envelope/");
		bough.child("S:Body").child("Change").child("xmlns").attribute.is(true).value.is("http://ws.swl/ChangeOrders");
		Bough head = bough.child("S:Body").child("Change").child("Docs").child("Head");
		head.child("Namber").attribute.is(true).value.is(docNumber);
		head.child("Comment").attribute.is(true).value.is(cmnt);
		head.child("ThatDone").attribute.is(true).value.is("1");
		head.child("Date").attribute.is(true).value.is(createDate);
		head.child("Polzov").value.is(sweetlife.android10.ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim());
		head.child("DateOtgruz").value.is(shipDate);
		bough.child("S:Body").child("Change").child("Docs").child("Table").value.is("");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		Bough.dumpXML(sb, bough, "");
		String xml = sb.toString();
		return xml;
	}

	public String SerializeXMLThatDone_DROP() throws IllegalArgumentException, IllegalStateException, IOException {
		Bough bough = new Bough().name.is("S:Envelope");
		bough.child("xmlns:S").attribute.is(true).value.is("http://schemas.xmlsoap.org/soap/envelope/");
		bough.child("S:Body").child("Change").child("xmlns").attribute.is(true).value.is("http://ws.swl/ChangeOrders");
		Bough head = bough.child("S:Body").child("Change").child("Docs").child("Head");
		head.child("Namber").attribute.is(true).value.is(docNumber);
		head.child("Comment").attribute.is(true).value.is(cmnt);
		head.child("ThatDone").attribute.is(true).value.is("2");
		head.child("Date").attribute.is(true).value.is(createDate);
		head.child("Polzov").value.is(sweetlife.android10.ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim());
		head.child("DateOtgruz").value.is(shipDate);
		bough.child("S:Body").child("Change").child("Docs").child("Table").value.is("");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		Bough.dumpXML(sb, bough, "");
		String xml = sb.toString();
		return xml;
	}

	public String SerializeXMLThatDone_CHANGE() throws IllegalArgumentException, IllegalStateException, IOException {

		Bough bough = new Bough().name.is("S:Envelope");
		bough.child("xmlns:S").attribute.is(true).value.is("http://schemas.xmlsoap.org/soap/envelope/");
		bough.child("S:Body").child("Change").child("xmlns").attribute.is(true).value.is("http://ws.swl/ChangeOrders");
		Bough head = bough.child("S:Body").child("Change").child("Docs").child("Head");
		head.child("Namber").attribute.is(true).value.is(docNumber);
		head.child("Comment").attribute.is(true).value.is(cmnt);
		head.child("ThatDone").attribute.is(true).value.is("3");
		head.child("Date").attribute.is(true).value.is(createDate);
		head.child("Polzov").value.is(sweetlife.android10.ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim());
		head.child("DateOtgruz").value.is(shipDate);
		bough.child("S:Body").child("Change").child("Docs").child(body);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		Bough.dumpXML(sb, bough, "");
		String xml = sb.toString();
		return xml;
	}

	public String SerializeXMLThatDone_GET() throws IllegalArgumentException, IllegalStateException, IOException {
		Bough bough = new Bough().name.is("S:Envelope");
		bough.child("xmlns:S").attribute.is(true).value.is("http://schemas.xmlsoap.org/soap/envelope/");
		bough.child("S:Body").child("Gat").child("xmlns").attribute.is(true).value.is("http://ws.swl/ChangeOrders");
		bough.child("S:Body").child("Gat").child("Namber").value.is(docNumber);
		bough.child("S:Body").child("Gat").child("Date").value.is(createDate);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		Bough.dumpXML(sb, bough, "");
		String xml = sb.toString();
		return xml;
	}

	public String SerializeXML() throws IllegalArgumentException, IllegalStateException, IOException {
		if (thatDone == UploadApproveOrder.ThatDone_APPROVE) {
			return SerializeXMLThatDone_APPROVE();
		}
		if (thatDone == UploadApproveOrder.ThatDone_DROP) {
			return SerializeXMLThatDone_DROP();
		}
		if (thatDone == UploadApproveOrder.ThatDone_GET) {
			return SerializeXMLThatDone_GET();
		}
		if (thatDone == UploadApproveOrder.ThatDone_CHANGE) {
			return SerializeXMLThatDone_CHANGE();
		}
		return "";
	}

	@Override
	protected String doInBackground(Object... params) {
		String ret = "";
		ArrayList<String> codConstractList = Request_Contracts.getContractsCodesForAllInRoute(mDB);
		//ContractsXMLSerializer serializer = new ContractsXMLSerializer(codConstractList);
		String requestString = null;
		try {
			requestString = SerializeXML();
			//LogHelper.debug(this.getClass().getCanonicalName() + " requestString is " + requestString);
		} catch (IOException e1) {
			ErrorReporter.getInstance().putCustomData("handled", "serialize except");
			ErrorReporter.getInstance().handleSilentException(e1);
			return e1.getMessage();
		}
		if (requestString != null && requestString.length() != 0) {
			HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_APPROVE_ORDER());
			request.setTimeOut(TIMEOUT);
			try {
				int status = request.Execute(requestString);
				LogHelper.debug(this.getClass().getCanonicalName() + " status is " + status);
				if (status != HttpStatus.SC_OK) {
					String txt = request.getResponse();
					LogHelper.debug(this.getClass().getCanonicalName() + txt);
					ErrorReporter.getInstance().putCustomData("handled", "execute except");
					ErrorReporter.getInstance().putCustomData("Contracts != SC_OK ", request.getResponse());
					ErrorReporter.getInstance().handleSilentException(null);
					return "Contracts != SC_OK ";
				}
			} catch (Exception e) {
				ErrorReporter.getInstance().putCustomData("handled", "execute except");
				ErrorReporter.getInstance().handleSilentException(e);
				return e.getMessage();
			}
			String responseString = request.getResponse();
			try {
				//System.out.println("<<<<<<<<<responseString<<<<<<<<<");
				//System.out.println(responseString);
				//System.out.println(">>>>>>>>>responseString>>>>>>>>>>>>");
				Bough bough = Bough.parseXML(responseString);
				if (thatDone == UploadApproveOrder.ThatDone_GET) {

					ret = responseString;
					//UIHelper.MsgBox("Interactive", xml , context);
				} else {
					ret = bough.child("soap:Body").child("m:ChangeResponse").child("m:return").value.property.value();
				}
			} catch (Exception e) {
				ErrorReporter.getInstance().putCustomData("handled", "parse except");
				ErrorReporter.getInstance().handleSilentException(e);
				return e.getMessage();
			}
		}
		return ret;
	}

	@Override
	protected void onPostExecute(String result) {

		//LogHelper.debug(this.getClass().getCanonicalName() + " result is " + result);
		Bundle resultData = new Bundle();
		resultData.putString(RESULT_STRING, result);
		mTaskListener.onComplete(resultData);
	}

	@Override
	public String getProgressMessage() {
		return mProgressDialogMessage;
	}
}
