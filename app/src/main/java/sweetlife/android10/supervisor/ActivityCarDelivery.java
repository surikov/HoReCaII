package sweetlife.android10.supervisor;

import android.os.Bundle;
//import android.support.design.widget.*;
//import android.support.design.widget.*;
//import android.support.v7.app.*;
//import android.support.v7.widget.*;
import android.view.*;

//import sweetlife.android10.*;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import reactive.ui.*;

//import reactive.ui.OrderItemInfo;

//import android.view.animation.*;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;


import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.*;
import com.yandex.mapkit.map.*;
import com.yandex.mapkit.geometry.*;
import com.yandex.runtime.image.*;


public class ActivityCarDelivery extends //AppCompatActivity {
        Activity {
    //String wialonToken = "c406941b922274b17af7ecc2a22b6eea24CA477D8E59AF71C3C2DA99CDFF149446A97800";
    //String wialonToken = "c406941b922274b17af7ecc2a22b6eea796C78D49F4AA9E395344833433C7B6F51C9F678";
    //String wialonToken = "c406941b922274b17af7ecc2a22b6eea075A69DA993615C108242B288726460AEAFE7052";
	//String wialonToken = "b59fccaa778dfbf1af3debc7921a17c9F3CCFD40C584F030D602B0829550B5A63A0CF971";
    String wialonEID = "";
    String carGPSid = "";//6021";
    private MapView mapview;
    //private final String MAPKIT_API_KEY = "53d587be-32c1-479b-b31e-081102369e30";
    private final Point TARGET_LOCATION = new Point(56.254010, 43.924883);
    Point iconPoint = new Point(56.250000, 43.924883);
    //Point iconPoint = new Point(50.250000, 40.924883);
    //https://wialon.redwoodrus.ru/wialon/ajax.html?svc=token/login&params={%22token%22:%22c406941b922274b17af7ecc2a22b6eea24CA477D8E59AF71C3C2DA99CDFF149446A97800%22}
    PlacemarkMapObject placemarkMapObject;
    MenuItem menuRefreshMap;
    static boolean active = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuRefreshMap = menu.add("Обновить карту");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == menuRefreshMap) {
            moveCarPointer();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carGPSid = Auxiliary.activityExatras(this).child("carGPSid").value.property.value();
        this.setTitle(Auxiliary.activityExatras(this).child("title").value.property.value());
        //this.setTitle("Доставка на карте");
        MapKitFactory.setApiKey(Cfg.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView( sweetlife.android10.R.layout.activity_car_delivery);

        mapview = (MapView) findViewById( sweetlife.android10.R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(TARGET_LOCATION, 15.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        //Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),  sweetlife.android10.R.drawable.svlogoshadow);
        Bitmap bm = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);
        //mapview.getMap().getMapObjects().addPlacemark(TARGET_LOCATION, ImageProvider.fromResource(this, R.drawable.swltruck));
        placemarkMapObject = mapview.getMap().getMapObjects().addPlacemark(TARGET_LOCATION, ImageProvider.fromBitmap(bm));
        placemarkMapObject.setGeometry(iconPoint);
        /*com.yandex.mapkit.map.MapObjectTapListener tapclick = new com.yandex.mapkit.map.MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                System.out.println("onMapObjectTap");
                System.out.println(mapObject);
                System.out.println(point);
                afterClickMark();
                return true;
            }
        };
        placemarkMapObject.addTapListener(tapclick);*/
        moveMarker();
        System.out.println("----------------------------------------------");
//        System.out.println(tapclick);

    }

   // void afterClickMark() {
     //   System.out.println("afterClickMark");
       // Auxiliary.warn("carGPSid " + carGPSid, this);
    //}

    void moveMarker() {
        readSessionEID();
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

    void readSessionEID() {
        new Expect().task.is(new Task() {
            public void doTask() {
                //String url = "https://wialon.redwoodrus.ru/wialon/ajax.html?svc=token/login&params={%22token%22:%22" + wialonToken + "%22}";
				//String url = "https://wialon.swnn.ru/wialon/ajax.html?svc=token/login&params=%7b%22token%22:%22"+Cfg.wialonToken+"%22%7d";
				String url = "https://wialon.swnn.ru/wialon/ajax.html?svc=token/login&params=%7b%22token%22:%22"+Cfg.keshProgramnihInterfeysov1Ctokenwialon+"%22%7d";

                System.out.println("readSessionEID url "+url);
                try {
                    byte[] bytes = Auxiliary.loadFileFromPublicURL(url);
                    String txt = new String(bytes);
                    System.out.println("readSessionEID text "+txt);
                    Bough response = Bough.parseJSON(txt);
                    wialonEID = response.child("eid").value.property.value();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }).afterDone.is(new Task() {
            public void doTask() {
                moveCarPointer();
            }
        }).status.is("Чтение маршрута...").start(this);

    }

    void moveCarPointer() {
        new Expect().task.is(new Task() {
            public void doTask() {
                //String url = "https://wialon.redwoodrus.ru/wialon/ajax.html?svc=core/search_item&params={%22id%22:" + carGPSid + ",%22flags%22:1025}&sid=" + wialonEID;
				String url = "https://wialon.swnn.ru/wialon/ajax.html?svc=core/search_item&params={%22id%22:" + carGPSid + ",%22flags%22:1025}&sid=" + wialonEID;

				try {
                    byte[] bytes = Auxiliary.loadFileFromPublicURL(url);
                    System.out.println("moveCarPointer "+url);
                    String txt = new String(bytes);
                    System.out.println(txt);
                    Bough response = Bough.parseJSON(txt);
                    System.out.println(response.dumpXML());
                    String xval = response.child("item").child("pos").child("x").value.property.value();
                    String yval = response.child("item").child("pos").child("y").value.property.value();
                    double xx = Numeric.string2double(xval);
                    double yy = Numeric.string2double(yval);
                    iconPoint = new Point(yy, xx);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }).afterDone.is(new Task() {
            public void doTask() {
                //System.out.println();
                placemarkMapObject.setGeometry(iconPoint);
                float zoom = mapview.getMap().getCameraPosition().getZoom();
                float azimuth = mapview.getMap().getCameraPosition().getAzimuth();
                float tilt = mapview.getMap().getCameraPosition().getTilt();
                mapview.getMap().move(
                        new CameraPosition(iconPoint, zoom, azimuth, tilt)
                        , new Animation(Animation.Type.SMOOTH, 0)
                        , null);
                scheduleNext();
            }
        })
                .status.is("Обновление координат...")
                .start(this);
    }

    void scheduleNext() {
        if (active) {
            /*new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (active)moveCarPointer();
                }
            }, 12000);*/
            final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (active) moveCarPointer();
                }
            }, 34000);
        }
    }
}