package sweetlife.android10.supervisor;

import android.app.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.R;
import sweetlife.android10.Settings;
import sweetlife.android10.gps.GPSInfo;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.*;
import com.yandex.mapkit.map.*;
import com.yandex.mapkit.geometry.*;
import com.yandex.runtime.image.*;

public class ActivityYandexMapKontragent extends Activity {
	PlacemarkMapObject clientPplacemarkMapObject;
	PlacemarkMapObject lastPlacemarkMapObject;
	MapView mapview;
	Bitmap newPointBitmap;
	MenuItem menuOpenExMap;
	MenuItem menuNewPoint;
	MenuItem menuRefresh;

	float mapZoom=8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(ApplicationHoreca.getInstance().getClientInfo().getKod() + ": " + ApplicationHoreca.getInstance().getClientInfo().getName()
				//+ ": " + ApplicationHoreca.getInstance().getClientInfo().getLon() + ": " + ApplicationHoreca.getInstance().getClientInfo().getLat()
		);
		//Point TARGET_LOCATION = new Point(56.254010, 43.924883);
		//Point iconPoint = new Point(56.250000, 43.924883);
		Point clientPoint = new Point(ApplicationHoreca.getInstance().getClientInfo().getLat(), ApplicationHoreca.getInstance().getClientInfo().getLon());
		Bough pp = GPSInfo.getLastSavedGPSpoin();
		//System.out.println(pp.dumpXML());
		//final String beginTime = pp.child("row").child("beginTime").value.property.value();
		String shirota = pp.child("row").child("longitude").value.property.value();
		String dolgota = pp.child("row").child("latitude").value.property.value();
		float lat = (float) Numeric.string2double(shirota);
		float lon = (float) Numeric.string2double(dolgota);
		Point mePoint = new Point(lat, lon);
		//this.setTitle("Доставка на карте");
		MapKitFactory.setApiKey(Cfg.MAPKIT_API_KEY);
		MapKitFactory.initialize(this);
		setContentView(R.layout.activity_client_yandex_map);

		mapview = (MapView) findViewById(R.id.clientmapview);
		/*mapview.getMap().move(
				new CameraPosition(clientPoint, mapZoom, 0.0f, 0.0f),
				new Animation(Animation.Type.SMOOTH, 0),
				null);*/

		Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_flag);
		Bitmap bm = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);
		clientPplacemarkMapObject = mapview.getMap().getMapObjects().addPlacemark(clientPoint, ImageProvider.fromBitmap(bm));
		clientPplacemarkMapObject.setGeometry(clientPoint);

		/*
		Bitmap meBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.svlogoshadow);
		Bitmap mebm = Bitmap.createScaledBitmap(meBitmap, 100, 100, false);
		lastPlacemarkMapObject = mapview.getMap().getMapObjects().addPlacemark(mePoint, ImageProvider.fromBitmap(mebm));
		lastPlacemarkMapObject.setGeometry(mePoint);
*/

		doRefreshMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuOpenExMap = menu.add("Открыть в браузере");
		menuNewPoint = menu.add("Новые координаты");
		menuRefresh = menu.add("Показать карту");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuOpenExMap) {
			doOpenExMap();
			return true;
		}
		if (item == menuNewPoint) {
			doNewPoint();
			return true;
		}
		if (item == menuRefresh) {
			doRefreshMap();
			return true;
		}

		return true;
	}
void doRefreshMap(){

	Point clientPoint = new Point(ApplicationHoreca.getInstance().getClientInfo().getLat(), ApplicationHoreca.getInstance().getClientInfo().getLon());
	//float zoom = mapview.getMap().getCameraPosition().getZoom();
	float azimuth = mapview.getMap().getCameraPosition().getAzimuth();
	float tilt = mapview.getMap().getCameraPosition().getTilt();
	System.out.println("doRefreshMap "+mapZoom+", "+azimuth+", "+tilt+", "+clientPoint.getLongitude()+"/"+clientPoint.getLatitude());
	mapview.getMap().move(
			new CameraPosition(clientPoint, mapZoom, azimuth, tilt)
			, new Animation(Animation.Type.SMOOTH, 0)
			, new Map.CameraCallback() {

				@Override
				public void onMoveFinished(boolean b) {
					System.out.println("doRefreshMap done " + b);
				}
			});
}
	void doOpenExMap() {
		Bough pp = GPSInfo.getLastSavedGPSpoin();
		//String shirota = pp.child("row").child("longitude").value.property.value();
		//String dolgota = pp.child("row").child("latitude").value.property.value();
		//String url = "https://yandex.ru/maps/?pt=" + dolgota + "," + shirota + "&z=18&l=map";
		String url = "https://yandex.ru/maps/?pt="
				+ ApplicationHoreca.getInstance().getClientInfo().getLon() + "," + ApplicationHoreca.getInstance().getClientInfo().getLat()
				+ "&z=18&l=map";
		System.out.println(url);
		ActivityYandexMapKontragent.this.startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)));
	}

	void doNewPoint() {
		//https://static-maps.yandex.ru/1.x/?size=650,450&l=sat,skl&pt=37.63778879,54.83044561,flag~37.61738879,54.82044561,ya_ru
		//getLastSavedGPSpoin
		Bough pp = GPSInfo.getLastSavedGPSpoin();
		System.out.println(pp.dumpXML());
		final String beginTime = pp.child("row").child("beginTime").value.property.value();
		final String shirota = pp.child("row").child("longitude").value.property.value();
		final String dolgota = pp.child("row").child("latitude").value.property.value();
		//System.out.println(beginTime+" "+shirota+" "+dolgota);
		//System.out.println(ApplicationHoreca.getInstance().getClientInfo().getName());
		//System.out.println(ApplicationHoreca.getInstance().getClientInfo().getKod());
		//System.out.println("lat "+ApplicationHoreca.getInstance().getClientInfo().getLat());
		//System.out.println("lon "+ApplicationHoreca.getInstance().getClientInfo().getLon());
		final String url = "https://static-maps.yandex.ru/1.x/?l=sat,skl&pt=" + dolgota + "," + shirota + ",flag&z=18";//{долгота},{широта},{стиль}{цвет}{размер}{контент}
		System.out.println("promptNewPoint "+url);
		newPointBitmap = null;
		new Expect().status.is("Поиск...").task.is(new Task() {
			@Override
			public void doTask() {
				newPointBitmap = Auxiliary.loadBitmapFromPublicURL(url);
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				promptSendPoint(shirota, dolgota, ApplicationHoreca.getInstance().getClientInfo().getName(), ApplicationHoreca.getInstance().getClientInfo().getKod(), beginTime);
			}
		}).start(this);
	}
	void promptSendPoint(final String shirota, final String dolgota, String name, final String kod, String beginTime) {
		//if (newPointBitmap != null) {
			System.out.println(shirota+"/"+dolgota);

			String dt = Auxiliary.tryReFormatDate(beginTime, "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss dd.MM.yy");
			Auxiliary.pick(this, name + " (GMT:0 " + dt + ")", new SubLayoutless(this)//
							.child(new Decor(this)
									.bitmap.is(newPointBitmap)
									.labelAlignCenterCenter()
									.left().is(Auxiliary.tapSize * 0.5)
									.top().is(Auxiliary.tapSize * 0.5)
									.width().is(Auxiliary.tapSize * 10)
									.height().is(Auxiliary.tapSize * 6)
							)//
							.width().is(Auxiliary.tapSize * 8)//
							.height().is(Auxiliary.tapSize * 9)//
					, "Отправить новые координаты", new Task() {
						@Override
						public void doTask() {
							sendNewPoint(shirota, dolgota, kod);
						}
					}, null, null, null, null);
		//}else{
		//	Auxiliary.warn("Нет карты для "+shirota+"x"+dolgota,this);
		//}
	}

	void sendNewPoint(String shirota, String dolgota, String kod) {
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
				+ "/hs/Planshet/GetKoordinateClient/"
				+kod
				+"/"+shirota
				+"/"+dolgota
				+"/"+Cfg.whoCheckListOwner();
		System.out.println(url);
		final Bough result=new Bough();
		new Expect().status.is("Отправка...").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String json = new String(bytes, "UTF-8");
					result.child("data").value.is(json);
					System.out.println(json);
				}catch(Throwable t){
					result.child("error").value.is(t.getMessage());
					t.printStackTrace();
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Bough bough = Bough.parseJSON(result.child("data").value.property.value());
				System.out.println(bough.dumpXML());
				Auxiliary.warn(bough.child("Message").value.property.value()
								+" "+result.child("error").value.property.value()
						,ActivityYandexMapKontragent.this);
			}
		}).start(this);



	}

}
