package sweetlife.android10.supervisor;

import android.app.*;

import java.text.*;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.gps.GPSInfo;
import tee.binding.Bough;
import tee.binding.it.*;
import tee.binding.task.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

public class ActivityGPSMap extends Activity {

	Layoutless layoutless;
	Bitmap mapRoute;
	Bitmap newPointBitmap;
	Decor map;
	RedactSingleChoice terrRedactSingleChoice;
	Numeric territorySelection = new Numeric();
	Numeric from = new Numeric();
	Numeric to = new Numeric();
	ColumnDescription kontagentyColumn;
	DataGrid kontagentyGrid;
	Bough kontragentyData;
	Bough points = new Bough();
	//boolean showNow=false;
	Bough mapData = null;

	String curShirota = null;
	String curDolgota = null;
	//String kontragentKod = null;
	//Toggle canBrowse = new Toggle();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Preferences.init(this);
		Bough bundle = Auxiliary.activityExatras(this);
		String route = bundle.child("route").value.property.value();
		//System.out.println(route);
		if (route.length() > 0) {
			mapData = Bough.parseXML(route);
			//System.out.println(mapData.dumpXML());
		}
		//

		this.setTitle("Положение на карте");
		composeGUI();
	}

	void composeGUI() {
		int b = Auxiliary.tapSize;
		terrRedactSingleChoice = new RedactSingleChoice(this);
		layoutless.child(terrRedactSingleChoice.selection.is(territorySelection).left().is(8).top().is(8).height().is(b * 0.8).width().is(b * 6 - 8));
		layoutless.child(new Decor(this).labelText.is("от").labelAlignRightCenter().left().is(b * 6).top().is(8).height().is(b * 0.8).width().is(b * 1));
		layoutless.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(from).left().is(b * 7).top().is(8).height().is(b * 0.8).width().is(b * 3));
		layoutless.child(new RedactTime(this).time.is(from).left().is(b * 10).top().is(8).height().is(b * 0.8).width().is(b * 2));
		layoutless.child(new Decor(this).labelText.is("до").labelAlignRightCenter().left().is(b * 12).top().is(8).height().is(b * 0.8).width().is(b * 1));
		layoutless.child(new RedactDate(this).format.is("dd.MM.yyyy").date.is(to).left().is(b * 13).top().is(8).height().is(b * 0.8).width().is(b * 3));
		layoutless.child(new RedactTime(this).time.is(to).left().is(b * 16).top().is(8).height().is(b * 0.8).width().is(b * 2));
		//
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			Bough row = Cfg.territory().children.get(i);
			String s = row.child("territory").value.property.value()// 
					+ " (" + row.child("hrc").value.property.value().trim()//
					+ " / " + row.child("kod").value.property.value().trim()//
					+ ")";
			terrRedactSingleChoice.item(s);
		}
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
		c.set(Calendar.HOUR_OF_DAY, 9);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		from.value((double) c.getTimeInMillis());
		c.set(Calendar.HOUR_OF_DAY, 18);
		to.value((double) c.getTimeInMillis());
		map = new Decor(this);
		layoutless.child(map.left().is(Auxiliary.screenWidth(this) / 2 - 650 / 2).top().is(Auxiliary.screenHeight(this) / 2 - 450 / 2).height().is(450).width().is(650));
		kontagentyGrid = new DataGrid(this);
		kontagentyColumn = new ColumnDescription();
		Numeric split = new Numeric().value(Auxiliary.screenWidth(this));
		layoutless.child(new Decor(this)//
				.background.is(Auxiliary.colorBackground)//
				.left().is(split)//
				.width().is(Auxiliary.screenWidth(this))//
				.height().is(Auxiliary.screenHeight(this))//
		);
		/*layoutless.child(new Knob(this).labelText.is("Обновить").afterTap.is(new Task() {
			@Override
			public void doTask() {
				refreshMarshrut();
				requeryPoints();
			}
		})//
				                 .left().is(b * 18.5)//
				                 .top().is(8)//
				                 .height().is(b * 0.8)//
				                 .width().is(b * 2.5));
		*/
		layoutless.child(new SubLayoutless(this)//
				.child(new Knob(this).labelText.is("Обновить").afterTap.is(new Task() {
					@Override
					public void doTask() {
						refreshMarshrut();
						requeryPoints();
					}
				})//
						.height().is(b * 0.8)//
						.width().is(b * 2.5))//
				.left().is(b * 18.5)//
				.top().is(8)//
				.height().is(b * 0.8)//
				.width().is(b * 2.5));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
					@Override
					public void doTask() {
						if (curShirota == null) {
							Auxiliary.inform("Выберите контрагента в панели справа", ActivityGPSMap.this);
							refreshMarshrut();
						} else {
							String url = "https://yandex.ru/maps/?pt=" + curDolgota + "," + curShirota + "&z=18&l=map";
							System.out.println(url);
							ActivityGPSMap.this.startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)));
						}
					}
				}).labelText.is("Открыть в браузере")
						.left().is(layoutless.width().property.divide(2).minus(4 * Auxiliary.tapSize))
						.top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
						.width().is(4 * Auxiliary.tapSize)
						.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new Knob(this).afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptNewPoint();
					}
				}).labelText.is("Новые координаты")
						.left().is(layoutless.width().property.divide(2).minus(0 * Auxiliary.tapSize))
						.top().is(layoutless.height().property.minus(1.5 * Auxiliary.tapSize))
						.width().is(4 * Auxiliary.tapSize)
						.height().is(1 * Auxiliary.tapSize)
		);
		layoutless.child(new Decor(this)//
				.background.is(0xffffffff)//
				.top().is(0)//
				.left().is(split)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		layoutless.child(new SplitLeftRight(this)//
				.split.is(split)//
				.rightSide(kontagentyGrid//
						//.pageSize.is(33)//
						.noHead.is(true)//
						.columns(new Column[]{kontagentyColumn.title.is("День недели").width.is(500)})//
				)//
				//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		if (mapData != null) {
			String url = "http://static-maps.yandex.ru/1.x/?size=650,450&l=map";
			String z = "&pt=";

			Vector<Bough> clients = mapData.children("client");
			for (int i = 0; i < clients.size(); i++) {
				//System.out.println(clients.get(i).dumpXML());
				if (clients.get(i).child("longitude").value.property.value().length() > 2) {
					url = url + z + clients.get(i).child("longitude").value.property.value().replace(',', '.') //
							+ "," + clients.get(i).child("latitude").value.property.value().replace(',', '.') //
							+ ",vkgrm";
					z = "~";
				}
			}
			//System.out.println(mapData.child("car").dumpXML());
			if (mapData.child("car").child("longitude").value.property.value().length() > 2) {
				url = url + z + mapData.child("car").child("longitude").value.property.value().replace(',', '.') //
						+ "," + mapData.child("car").child("latitude").value.property.value().replace(',', '.') //
						+ ",flag";
			}
			//System.out.println(url);
			map.hidden().is(true);
			final String bmURL = url;
			new Expect().status.is("Поиск...")//
					.task.is(new Task() {
				@Override
				public void doTask() {
					//mapRoute = Auxiliary.loadBitmapFromURL(bmURL);
					mapRoute = Auxiliary.loadBitmapFromPublicURL(bmURL);
				}
			})//
					.afterDone.is(new Task() {
				@Override
				public void doTask() {
					if (mapRoute != null) {
						map.hidden().is(false);
						map.bitmap.is(mapRoute);
					}
				}
			}).start(this);
		} else {
			if (ApplicationHoreca.getInstance().getClientInfo() != null) {
				//System.out.println("start map");
				//System.out.println(ApplicationHoreca.getInstance().getClientInfo());
				//showNow=true;
				//requeryPoints();
				//refreshMarshrut();
				//requeryPoints();
				showMapFromServer(("" + ApplicationHoreca.getInstance().getClientInfo().getLon()).replace(',', '.')//
						, ("" + ApplicationHoreca.getInstance().getClientInfo().getLat()).replace(',', '.')
						, ApplicationHoreca.getInstance().getClientInfo().getName()
				);
			}
		}
		//System.out.println("done map");
	}

	void promptNewPoint() {
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
				promptSendPoint(shirota, dolgota
						, ApplicationHoreca.getInstance().getClientInfo().getName()
						, ApplicationHoreca.getInstance().getClientInfo().getKod()
						, beginTime);
			}
		}).start(this);
	}

	void promptSendPoint(final String shirota, final String dolgota, String name, final String kod, String beginTime) {
		if (newPointBitmap != null) {
			System.out.println(shirota+"/"+dolgota);

			String dt = Auxiliary.tryReFormatDate(beginTime, "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss dd.MM.yy");
			Auxiliary.pick(this, name + " (" + dt + ")", new SubLayoutless(this)//
							.child(new Decor(this)
									.bitmap.is(newPointBitmap)
									.labelAlignCenterCenter()
									.left().is(Auxiliary.tapSize * 0.5)
									.top().is(Auxiliary.tapSize * 0.5)
									.width().is(Auxiliary.tapSize * 10)
									.height().is(Auxiliary.tapSize * 6)
							)//
							.width().is(Auxiliary.tapSize * 10)//
							.height().is(Auxiliary.tapSize * 6)//
					, "Отправить новые координаты", new Task() {
						@Override
						public void doTask() {
							sendNewPoint(shirota, dolgota, kod);
						}
					}, null, null, null, null);
		}else{
			Auxiliary.warn("Нет карты для "+shirota+"x"+dolgota,this);
		}
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
						,ActivityGPSMap.this);
			}
		}).start(this);



	}

	void requeryPoints() {
		Bough row = Cfg.territory().children.get(territorySelection.value().intValue());
		String hrc = row.child("hrc").value.property.value().trim();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
		//Date date = new Date();
		c.setTimeInMillis(from.value().longValue());
		//System.out.println(c.toString());
		String txtFrom = format.format(c.getTime());
		c.setTimeInMillis(to.value().longValue());
		//System.out.println(c.toString());

		String txtTo = format.format(c.getTime());
		System.out.println("requeryPoints " + txtFrom + " =>" + txtTo);
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "\n	<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "\n		<soap:Body>"//
				+ "\n			<Get xmlns=\"http://ws.swlife.ru\">"//
				+ "\n				<Kod>" + hrc + "</Kod>"//
				+ "\n				<DataNach>" + txtFrom + "</DataNach>"//
				+ "\n				<DataKon>" + txtTo + "</DataKon>"//
				+ "\n			</Get>"//
				+ "\n		</soap:Body>"//
				+ "\n	</soap:Envelope>";
		//System.out.println(xml);
		final RawSOAP r = new RawSOAP();
		r.url.is(Settings.getInstance().getBaseURL() + "GPSTorgovogo.1cws").xml.is(xml)//
				.afterError.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("ops");
				Auxiliary.warn("" + r.exception.property.value(), ActivityGPSMap.this);
			}
		})//
				.afterSuccess.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("go showMapFromServer");
				points = r.data.child("soap:Body").child("m:GetResponse").child("m:return");
				if (points.children.size() > 2) {
					points.children = points.children("m:CoordGPS");
					points.children.sort(new java.util.Comparator<Bough>() {
						public int compare(Bough var1, Bough var2) {
							return var1.child("m:time").value.property.value().compareTo(var2.child("m:time").value.property.value());
						}
					});
					/*if(showNow){
						showMapFromServer(""+ApplicationHoreca.getInstance().getClientInfo().getLon(), ""+ApplicationHoreca.getInstance().getClientInfo().getLat());
					}else {
						showMapFromServer(null, null);
					}
					showNow=false;*/
					refreshMarshrut();
					showMapFromServer(null, null, null);
				} else {
					Auxiliary.warn("Нет данных", ActivityGPSMap.this);
				}
			}
		})//
				.startLater(ActivityGPSMap.this, "Загрузка", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
	}

	void showMapFromServer(String dolgota, String shirota, String klientName) {
		System.out.println("showMapFromServer " + dolgota + "/" + shirota);
		map.hidden().is(true);
		if (klientName == null) {
			this.setTitle("Положение на карте");
			this.curShirota = null;
			this.curDolgota = null;
		} else {
			this.setTitle("Положение на карте " + klientName);
			this.curShirota = shirota;
			this.curDolgota = dolgota;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		System.out.println("points " + points.dumpXML());
		try {
			String firstString = points.children.get(0).child("m:time").value.property.value();
			String lastString = points.children.get(points.children.size() - 2).child("m:time").value.property.value();
			System.out.println("showMapFromServer " + firstString + " =>" + lastString);
			from.value((double) format.parse(firstString).getTime());
			to.value((double) format.parse(lastString).getTime());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		String url = "http://static-maps.yandex.ru/1.x/?";
		if (dolgota != null && shirota != null) {
			url = url + "ll=" + dolgota + "," + shirota + "&z=18&";
		}
		url = url + "size=650,450&l=sat,skl";
		try {
			if (dolgota != null && shirota != null) {
				url = url + "&pt=" + dolgota + "," + shirota + ",flag";
			}
			if (points == null) {
				//
			} else {
				Vector<Bough> crds = points.children("m:CoordGPS");
				Bough r = crds.get(crds.size() - 1);
				url = url + "&pt=" + r.child("m:long").value.property.value() + "," + r.child("m:lat").value.property.value() + ",pmwts";
				url = url + "&pl=c:ff0000ff,w:3";
				int counter = 0;
				int step = (points.children.size() - 1) / 50 + 1;
				for (int i = points.children.size() - 2; i >= 0; i = i - step) {
					r = points.children.get(i);
					double mLong = Numeric.string2double(r.child("m:long").value.property.value());
					double mLat = Numeric.string2double(r.child("m:lat").value.property.value());
					if (mLong != 0 && mLat != 0) {
						url = url + ",";
						url = url + r.child("m:long").value.property.value();
						url = url + ",";
						url = url + r.child("m:lat").value.property.value();
						counter++;
					}
					if (counter > 49) {
						break;
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		final String bmURL = url;
		System.out.println("yandex url " + url);
		new Expect().status.is("Поиск...")//
				.task.is(new Task() {
			@Override
			public void doTask() {
				mapRoute = Auxiliary.loadBitmapFromPublicURL(bmURL);
			}
		})//
				.afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (mapRoute != null) {
					map.hidden().is(false);
					map.bitmap.is(mapRoute);
				}
			}
		}).start(this);
	}


	void refreshMarshrut() {
		Bough r = Cfg.territory().children.get(territorySelection.value().intValue());
		String hrc = r.child("hrc").value.property.value().trim();
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(from.value().longValue());
		int day = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek() + 1;
		//System.out.println("refreshMarshrut " + day + " / " + hrc);
		String sql = "select "// 
				+ "\n	kontragenty.Naimenovanie as name"//
				+ "\n		,kontragenty.GeographicheskayaShirota as shirota"//
				//+ "\n		,kontragenty.kod as kod"//
				+ "\n		,kontragenty.GeographicheskayaDolgota as dolgota"//
				+ "\n		,DenNedeli as day"//
				+ "\n	from MarshrutyAgentov"//
				+ "\n		join kontragenty on kontragenty._idrref=MarshrutyAgentov.kontragent"//
				+ "\n		join Polzovateli on Polzovateli._idrref=MarshrutyAgentov.Agent"//
				+ "\n	where DenNedeli=" + day//
				+ "\n		and trim(Polzovateli.kod)='" + hrc + "'"//
				+ "\n	order by DenNedeli,kontragenty.Naimenovanie"//
				+ "\n	limit 100"//
				;
		kontragentyData = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(data.dumpXML());
		kontagentyGrid.clearColumns();
		for (int i = 0; i < kontragentyData.children.size(); i++) {
			Bough row = kontragentyData.children.get(i);
			final String dolgota = row.child("dolgota").value.property.value();
			final String shirota = row.child("shirota").value.property.value();
			final String klientName = row.child("name").value.property.value();

			Task tap = new Task() {
				@Override
				public void doTask() {
					//System.out.println(dolgota + "x" + shirota);
					showMapFromServer(dolgota, shirota, klientName);
				}
			};
			String koord = row.child("shirota").value.property.value() + "/" + row.child("dolgota").value.property.value();
			if (row.child("shirota").value.property.value().equals("0")) {
				koord = "нет координат";
			} else {
				koord = "";
			}
			kontagentyColumn.cell((i + 1) + ": " + row.child("name").value.property.value(), tap, koord);
		}
		kontagentyGrid.refresh();
	}
}
