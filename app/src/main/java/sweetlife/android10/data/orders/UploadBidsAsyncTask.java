package sweetlife.android10.data.orders;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.acra.ErrorReporter;
import org.apache.http.HttpStatus;

import reactive.ui.Auxiliary;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.database.Requests;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.ManagedAsyncTask;

import java.util.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import sweetlife.android10.*;
import tee.binding.*;

public class UploadBidsAsyncTask extends ManagedAsyncTask<String> {
	private int TIMEOUT = 300 * 1000;
	private ArrayList<NomenclatureBasedDocument> mDocumentsForUpload;
	private String mRequestURL;
	//private BidsXMLParser mParser;
	private SQLiteDatabase mDB;

	//private ArrayList<Deficit> mDeficits;
	public UploadBidsAsyncTask(SQLiteDatabase db, Context appContext, String dialogMessage, ArrayList<NomenclatureBasedDocument> documentsForUpload, String requestURL) {
		super(dialogMessage, appContext);
		mDB = db;
		mProgressDialogMessage = dialogMessage;
		mDocumentsForUpload = documentsForUpload;
		mRequestURL = requestURL;
		//mParser = new BidsXMLParser();
		//mDeficits = new ArrayList<BidsXMLParser.Deficit>();
	}

	public static void ___logToFile(String txt) {

		SimpleDateFormat sqliteTime = new SimpleDateFormat("yyyy.MM.DD_HH.mm.ss_SSS");
		String name = "/sdcard/horeca/log/" + sqliteTime.format(new Date()) + ".xml";
		//System.out.println("logToFile "+name);
		Auxiliary.createAbsolutePathForFile(name);
		Auxiliary.writeTextToFile(new File(name), txt, "utf-8");
	}

	@Override
	protected String doInBackground(Object... arg0) {
		LogHelper.debug(this.getClass().getCanonicalName() + " doInBackground");
		StringBuilder resultString = new StringBuilder();
		StringBuilder resultDump = new StringBuilder();
		//boolean flagDeficit=false;
		for (NomenclatureBasedDocument document : mDocumentsForUpload) {
			//boolean dropDocument=true;
			String requestString = null;
			try {
				requestString = document.getSerializedXML(mDB);
			} catch (IOException e1) {
				//ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
				ErrorReporter.getInstance().handleSilentException(e1);
				continue;
			}
			boolean hasError = false;

			if (requestString != null && requestString.length() != 0) {
				//System.out.println("sweetlife.horeca.data.orders.UploadBidsAsyncTask upload "+TIMEOUT);
				//System.out.println("requestString: "+requestString);
				//System.out.println("mRequestURL: "+mRequestURL);
				HTTPRequest request = new HTTPRequest(mRequestURL);
				request.setTimeOut(TIMEOUT);
				//logToFile(requestString);
				//System.out.println(this.getClass().getCanonicalName()+": mRequestURL: "+mRequestURL);
				//System.out.println(this.getClass().getCanonicalName()+"\n"+mRequestURL+"\n requestString\n"+requestString);
				try {
					int r = request.Execute(requestString);
					//System.out.println("r: "+r);
					if (r != HttpStatus.SC_OK) {
						LogHelper.debug(this.getClass().getCanonicalName() + " HttpStatus " + r);
						resultString.append(mResources.getString(R.string.bad_server_responce)).append("\n");
						//Temporary
						try {
							//ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
							ErrorReporter.getInstance().putCustomData("Document != SC_OK upload ", request.getResponse());
							ErrorReporter.getInstance().handleSilentException(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//Temporary		
						continue;
					}
				} catch (Exception e) {
					LogHelper.debug(this.getClass().getCanonicalName() + " " + e.getMessage());
					//ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
					ErrorReporter.getInstance().handleSilentException(e);
					resultString.append(mResources.getString(R.string.bad_server_responce)).append("\n");
					continue;
				}
				String responseString = request.getResponse();
				resultDump.append(responseString);
				//System.out.println(this.getClass().getCanonicalName() + " responseString is " + responseString);
				//boolean dropDocument=true;
				try {
					Bough tree = Bough.parseXML(responseString);
					//System.out.println(tree.dumpXML());
					Vector<Bough> docs = tree.child("soap:Body")//
							.child("m:UploadOrderResponse")//
							.child("m:return")//
							.children("m:Document");
					for (int i = 0; i < docs.size(); i++) {
						//StringBuilder sb = new StringBuilder();
						//Bough.dumpXML(sb, docs.get(i), "");
						//System.out.println(sb.toString());
						String sDateDoc = docs.get(i).child("m:HeaderDoc").child("DateDoc").value.property.value();
						String sNumberDoc = docs.get(i).child("m:HeaderDoc").child("NumberDoc").value.property.value();
						String sCodClient = docs.get(i).child("m:HeaderDoc").child("CodClient").value.property.value();
						String sComment = docs.get(i).child("m:HeaderDoc").child("Comment").value.property.value();
						//if (sCodClient.trim().length() > 0) {
						if (sComment.startsWith("№")) {
							//System.out.println("=> " + sComment);
							document.mNomer = sComment + " " + document.mNomer;
						} else {
							//System.out.println("-> " + sComment);
						}
						if (sComment.toUpperCase().indexOf("ОШИБКА") > -1) {
							hasError = true;
							resultString.append(sComment).append("\n");
						}
						resultString.append("\n");
						resultString.append(sComment);
						Vector<Bough> strings = docs.get(i)//
								.child("m:TPDoc")//
								.children("m:StringTP");
						int iCodClient = 0;
						String kontragentName = "?";
						if (sCodClient.trim().length() > 0) {
							iCodClient = (int) Double.parseDouble(sCodClient);
							kontragentName = Requests.getKontragentyNaimenovaniePoKod(mDB, iCodClient);
						}
						resultString.append(" (");
						resultString.append(kontragentName);
						resultString.append(")");
						for (int row = 0; row < strings.size(); row++) {
							String sDeficit = strings.get(row).child("m:Deficit").value.property.value();
							String sArticle = strings.get(row).child("m:Article").value.property.value();
							String sQuantity = strings.get(row).child("m:Quantity").value.property.value();
							String sAnalog = strings.get(row).child("m:Analog").value.property.value();
							String sDataPrihoda = strings.get(row).child("m:DataPrihoda").value.property.value();
							if (sDataPrihoda.length() > 10) {
								sDataPrihoda = sDataPrihoda.substring(0, 10);
							}
							double dDeficit = 0;
							if (sDeficit.trim().length() > 0) {
								dDeficit = Double.parseDouble(sDeficit);
							}
							if (dDeficit > 0) {
									/*resultString.append(sComment);
									resultString.append(" (дефицит ");
									resultString.append(sDeficit);
									resultString.append(" ед.)");*/
								//System.out.println("sAnalog: " + sAnalog);
								String replacement = "";
								try {
									String[] analogs = sAnalog.split(";");
									if (analogs.length > 0) {
										for (int a = 0; a < analogs.length; a++) {
											//System.out.println("analogs[" + a + "]: " + analogs[a]);
											if (analogs[a].trim().length() > 0) {
												replacement = replacement + "\n- " + analogs[a] + "/" + Requests.getNomenclatureNameFromArtikul(mDB, analogs[a]) + ";";
											}
										}
									}
								} catch (Throwable t) {
									t.printStackTrace();
								}
								String msg = "\nЗаявка №" + sNumberDoc + "/" + sDateDoc//
										+ " от " + kontragentName //Requests.getKontragentyNaimenovaniePoKod(mDB, iCodClient)//
										+ ", " + sArticle + "/" + Requests.getNomenclatureNameFromArtikul(mDB, sArticle)//
										+ "\n- дефицит на " + sDeficit + "/" + sQuantity//
										+ "\n- ожидаемая дата прихода " + sDataPrihoda//
										;
								if (replacement.length() > 0) {
									msg = msg + "\nВозможная замена: " + replacement;
								}
								LogHelper.debug(this.getClass().getCanonicalName() + " " + msg);
								resultString.append(msg);
								resultString.append("\n\n");
								//dropDocument=false;
							}
						}
						//}
					}
					/*
					EParserResult result = mParser.Parse(responseString);
					resultString.append(String.format(mParser.getResponseParseResult(mResources), document.getNomer())).append("\n");
					if (result == EParserResult.EComplete) {
						LogHelper.debug(this.getClass().getCanonicalName() + " can't fill mDeficits");
						document.writeUploaded(mDB);
						mDeficits.addAll(mParser.getDeficits());
					}*/
				} catch (Exception e) {
					LogHelper.debug(this.getClass().getCanonicalName() + " " + e.getMessage());
					//ErrorReporter.getInstance().putCustomData("handled", mParser.getClass().getName());
					ErrorReporter.getInstance().handleSilentException(e);
					resultString.append(mResources.getString(R.string.bad_server_responce)).append("\n");
					continue;
				}
			}
			if (!hasError) {
				//document.writeToDataBase(mDB);
				document.writeUploaded(mDB);
			}
		}
		//LogHelper.debug(this.getClass().getCanonicalName() + " resultString "+resultString.toString());
		if (resultString.length() > 0) {
			return resultString.toString();
		} else {
			return "Заявки выгружены но не подтверждены сервером: " + resultDump.toString();
		}
	}

	/*private String getDeficits() {
		if (mDeficits.size() > 0) {
			StringBuilder string = new StringBuilder(mResources.getString(R.string.deficit_foostuff));
			string.append("\n");
			for (Deficit deficit : mDeficits) {
				string.append(Requests.getNomenclatureNamebyArtikul(mDB, deficit.article) + " - " + deficit.count).append("\n");
			}
		}
		return "";
	}*/
	@Override
	protected void onPostExecute(String result) {
		//result = result + getDeficits();
		LogHelper.debug(this.getClass().getCanonicalName() + " onPostExecute " + result);
		Bundle resultData = new Bundle();
		resultData.putString(RESULT_STRING, result);
		mTaskListener.onComplete(resultData);
	}

	@Override
	public String getProgressMessage() {
		return mProgressDialogMessage;
	}
}
