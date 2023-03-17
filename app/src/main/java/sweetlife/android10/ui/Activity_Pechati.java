package sweetlife.android10.ui;

import sweetlife.android10.supervisor.*;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.supervisor.Report_Base;
//import sweetlife.horeca.reports.KontragentInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import java.util.*;

import tee.binding.*;
import tee.binding.task.*;

public class Activity_Pechati extends Activity {
	Layoutless layoutless;
	int lastH = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(ApplicationHoreca.getInstance().getClientInfo().getKod() + ": " + ApplicationHoreca.getInstance().getClientInfo().getName());
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		resetGUI();
	}
	void resetGUI() {
		final RawSOAP rawSOAP = new RawSOAP();
		rawSOAP.url.is(Settings.getInstance().getBaseURL()+"WebPechati.1cws");
		rawSOAP.xml.is("<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "	<soap:Body>"//
				+ "		<Get xmlns=\"http://ws.swl/Sert\">"//
				+ "			<Kod>" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "</Kod>"//
				//+ "			<Kod>13522</Kod>"//
				+ "		</Get>"//
				+ "	</soap:Body>"//
				+ "</soap:Envelope>");
		System.out.println("url "+rawSOAP.url.property.value());
		System.out.println("xml "+rawSOAP.xml.property.value());
		new Expect().status.is("Поиск...").task.is(new Task() {
			@Override
			public void doTask() {
				Report_Base.startPing();
				
				rawSOAP.startNow(Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				System.out.println("rawSOAP.exception.property.value() "+rawSOAP.exception.property.value());
				System.out.println("rawSOAP.data.dumpXML() "+rawSOAP.data.dumpXML());
				if (rawSOAP.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + rawSOAP.exception.property.value().getMessage(), Activity_Pechati.this);
					rawSOAP.exception.property.value().printStackTrace();
				}
				else {
					if (rawSOAP.statusCode.property.value() >= 100 && rawSOAP.statusCode.property.value() <= 300) {
						//System.out.println(rawSOAP.data.dumpXML());
						Vector<Bough> rez = rawSOAP.data.child("soap:Body")//
								.child("m:GetResponse")//
								.child("m:return").children;
						//System.out.println(rez.size());
						for (int i = 0; i < rez.size(); i++) {
							Bough one = rez.get(i);
							String encoded = one.value.property.value();
							byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
							Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
							//System.out.println(bm.getWidth() + " x " + bm.getHeight());
							//Auxiliary.ba
							if (bm != null) {
								addBitmap(bm);
							}
						}
					}
				}
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				Activity_Pechati.this.finish();
			}
		})//
				.start(this);
	}
	void addBitmap(Bitmap bm) {
		int w = Auxiliary.screenWidth(this);
		Decor decor = new Decor(this);
		decor.bitmap.is(bm);
		decor.width().is(bm.getWidth());
		decor.height().is(bm.getHeight());
		decor.left().is((w - bm.getWidth()) / 2);
		decor.top().is(layoutless.shiftY.property.plus(lastH + Auxiliary.tapSize));
		layoutless.child(decor);
		lastH = lastH + bm.getHeight() + Auxiliary.tapSize;
		layoutless.innerHeight.is(lastH + Auxiliary.tapSize);
		//System.out.println("/"+(lastH + Auxiliary.tapSize));
	}
}
