package sweetlife.android10.reports;

import android.app.Activity;

import android.content.*;
import android.graphics.*;
import android.os.*;

import java.io.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;

//import reactive.ui.OrderItemInfo;

import sweetlife.android10.supervisor.*;
import tee.binding.task.*;
import tee.binding.*;
import tee.binding.it.*;

public class ActivityPhoto extends Activity{
	Layoutless layoutless;
	WebRender brwsr;
	public static String artikulField = "artikulField";
	public static String nameField = "nameField";
	//Bitmap bm = null;
	//String comment = "";
	String kommentariy = "";
	String Nabor = "";
	String SrokGodnosti = "";
	String EdinicyIzmereniyaNaimenovanie = "";
	String proizvoditel = "";
	Bough managers = null;

	String template1 = "<html>"
			+ "\n<head>"
			+ "\n	<meta charset='UTF-8'>"
			+ "\n	<style>"
			+ "\n		p {"
			+ "\n			padding-left: 16px;"
			+ "\n			padding-right: 16px;"
			+ "\n		}"
			+ "\n		.fotos {"
			+ "\n			width: inherit;"
			+ "\n			height: 320px;"
			+ "\n			justify-content: space-between;"
			+ "\n			display: flex;"
			+ "\n			align-items: center;"
			+ "\n			padding-top: 0px;"
			+ "\n			padding-bottom: 0px;"
			+ "\n			margin: 0px;"
			+ "\n		}"
			+ "\n		#foto {"
			+ "\n			max-width: 75%;"
			+ "\n			object-fit: contain;"
			+ "\n		}"
			+ "\n		.arrow {"
			+ "\n			font-size: 64px;"
			+ "\n			text-decoration: none"
			+ "\n		}"
			+ "\n	</style>"
			+ "\n</head>"
			+ "\n<body>";
	String template2 = ""
			+ "\n	<p><i>Производитель:</i> <b>PURATOS N.V.</b></p>"
			+ "\n	<p><i>Срок годности:</i> 365</p>"
			+ "\n	<p><i>Квант:</i> 8.5кг по 1.0кг</p>"
			+ "\n	<p><i>Состав:</i> Пудра сахарная, сахар, жир растительный пальмовый, вода питьевая, сироп глюкозный, сгущенное с сахаром цельное молоко (молоко нормализованное, сахар), влагоудерживающий агент - загуститель (Е406), ароматизатор, краситель (Е171), регулятор кислотности – сорбитовый сироп, эмульгаторы (Е472e, E475, Е471), соль, кислота лимонная, консервант - сорбат калия.</p>";
	String template3 = ""
			+ "\n	<div class='fotos'>"
			+ "\n		<a href=\"javascript:moveLeft();\" class='arrow'>&lt;</a>"
			+ "\n		<img id='foto' height=300px src=\"\" />"
			+ "\n		<a href=\"javascript:moveRight();\" class='arrow'>&gt;</a>"
			+ "\n	</div>";
	String template3ii = ""
			+ "\n	<div class='fotos'>"
			+ "\n		<span class='arrow'> </span>"
			+ "\n		<img id='foto' height=300px src=\"\" />"
			+ "\n		<span class='arrow'> </span>"
			+ "\n	</div>";
	String template4 = ""
			+ "\n	<p><i>Описание:</i> Глазурь кондитерская на жировой основе для глазирования поверхности хлебобулочных и кондитерских изделий, идеально подходит для глазирования берлинеров, донатсов, куличей и других хлебобулочных изделий Глазурь обеспечивает ровное покрытие продукта, имеет прекрасный блеск Эластична после застывания, не затвердевает Помаду можно смешивать с фруктовыми сиропами «Классик» для придания вкуса, цвета и аромата Возможно аэрирование помады для получения массы для декорирования кондитерских изделий Айсинг, Фудж Преимущества для клиента Глазурь готова к применению Помаду можно смешивать с фруктовыми сиропами «Классик» для придания вкуса, цвета и аромата Преимущества для потребителя Обеспечивает отличное покрытие продукта Эластична после застывания, не затвердевает Привлекательный внешний вид изделий</p>";
	String template5 = ""
			+ "\n	<p>Ответственные:</p>"
			+ "\n	<ul>";
	String template6 = ""
			+ "\n		<li>Ахметова Надежда Дмитриевна, Ассистент группы по поставщику: <i>Доб: 10-62, Mail: <a href='mailto:akhmetova@swlife.ru'>akhmetova@swlife.ru</a></i></li>"
			+ "\n		<li>Коржавина Дарья Игоревна, Менеджер группы: <i>Mail: <a href='mailto:korjavina@swlife.ru'>korjavina@swlife.ru</a>, Тел: 79040535346, Доб: 09-83</i></li>";
	String template7 = ""
			+ "\n	</ul>"
			+ "\n	<p><i>Глобальный каталог:</i>"
			+ "\n		<ul>";
	String template8 = ""
			+ "\n			<li>t° max хранения: 20</li>"
			+ "\n			<li>t° min хранения: 0</li>"
			+ "\n			<li>Без глютена: Нет</li>"
			+ "\n			<li>Бездрожжевые: Нет</li>"
			+ "\n			<li>Белки: 1</li>"
			+ "\n			<li>Веган: Нет</li>"
			+ "\n			<li>Вес БРУТТО: 12.85</li>"
			+ "\n			<li>ГОСТ или ТУ: ТУ 9125-127-40222408-2015 \"Глазури. Технические условия\"</li>"
			+ "\n			<li>Жиры: 13</li>"
			+ "\n			<li>Кошерный продукт: Нет</li>"
			+ "\n			<li>Производитель для ценника: АО Пуратос Россия 142121 Московская обл. Подольск ул. Станционная вблизи дер. Северово дом 18</li>"
			+ "\n			<li>Производитель (импортер или дистрибьютор): АО Пуратос Россия 142121 Московская обл. Подольск ул. Станционная вблизи дер. Северово дом 18</li>"
			+ "\n			<li>Углеводы: 75</li>"
			+ "\n			<li>Условия хранения: Хранить в чистых, сухих, хорошо вентилируемых помещениях при температуре от 0°С до +20°С и относительной влажности воздуха не более 75%. Плотно закрывать ведро после каждого использования.</li>"
			+ "\n			<li>Халяль: Нет</li>"
			+ "\n			<li>Калории: 417</li>"
			+ "\n			<li>Постный продукт: Нет</li>";
	String template9 = ""
			+ "\n		</ul>"
			+ "\n	</p>"
			+ "\n	<p>&nbsp;</p>"
			+ "\n</body>"
			+ "\n<script>"
			+ "\n	let idx = 0;"
			+ "\n	let fotos = [";
	String template10 = ""
			+ "\n		'https://files.swlife.ru/photo/104833.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_1.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_2.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_3.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_4.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_5.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_6.jpg'"
			+ "\n		, 'https://files.swlife.ru/photo/104833_7.jpg'";
	String template11 = ""
			+ "\n	];"
			+ "\n	function moveLeft() {"
			+ "\n		idx--;"
			+ "\n		changeFoto();"
			+ "\n	}"
			+ "\n	function moveRight() {"
			+ "\n		idx++;"
			+ "\n		changeFoto();"
			+ "\n	}"
			+ "\n	function changeFoto() {"
			+ "\n		if (idx < 0) idx = 0;"
			+ "\n		if (idx >= fotos.length) idx = fotos.length - 1;"
			+ "\n		if (idx < fotos.length && idx >= 0) {"
			+ "\n			document.getElementById('foto').src = fotos[idx];"
			+ "\n		}"
			+ "\n	}"
			+ "\n	changeFoto();"
			+ "\n	console.log('init', fotos);"
			+ "\n</script>"
			+ "\n</html>";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle extras = getIntent().getExtras();
		String artikul = extras.getString(artikulField);
		//comment = getComment(artikul);
		fillComment(artikul);
		String name = extras.getString(nameField);
		this.setTitle("артикул " + artikul + ", " + name);
		layoutless.setBackgroundColor(0xffffffff);
		loadPhoto(artikul);
	}

	void fillComment(String artikul){
		String sql = "select n.kommentariy as kommentariy"
				+ "\n		, n.Nabor as Nabor"
				+ "\n		, n.SrokGodnosti as SrokGodnosti"
				+ "\n		, n.kvant || n.skladEdIzm || ' по ' || n.skladEdVes || 'кг' as [EdinicyIzmereniyaNaimenovanie]"
				+ "\n		, pp.naimenovanie as proizvoditel"
				+ "\n	from nomenklatura n"
				+ "\n		join proizvoditel pp on pp._idrref=n.osnovnoyProizvoditel"
				+ "\n	where artikul='" + artikul.trim() + "';";
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		kommentariy = b.child("row").child("kommentariy").value.property.value();
		Nabor = b.child("row").child("Nabor").value.property.value();
		SrokGodnosti = b.child("row").child("SrokGodnosti").value.property.value();
		EdinicyIzmereniyaNaimenovanie = b.child("row").child("EdinicyIzmereniyaNaimenovanie").value.property.value();
		proizvoditel = b.child("row").child("proizvoditel").value.property.value();
		//String txt= b.child("row").child("k").value.property.value();
		//txt=txt.replace("Состав: ","\nСостав:\n");
		//txt=txt+"\n"+"---";
		//return txt;
	}

	void loadInfoAndShowPhoto(String artikul){
		final Note url = new Note().value(
				Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
						//"https://testservice.swlife.ru/shatov"
						+ "/hs/Planshet/DataGK/" + artikul
		);
		final Note result = new Note();
		final Note fotosHTML = new Note();
		final Numeric cnt=new Numeric();
		new Expect().task.is(new Task(){
			@Override
			public void doTask(){
				try{
					byte[] b = Auxiliary.loadFileFromPrivateURL(url.value(), Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String txt = new String(b, "UTF-8");

					Bough parsed = Bough.parseJSON(txt);
					//System.out.println(parsed.dumpXML());
					java.util.Vector<Bough> data = parsed.children("Data");
					//System.out.println("Data "+data.dumpXML());
					//fotosHTML.value("\n<div class='fotos'><nobr>");
					java.util.Vector<Bough> foto = parsed.children("Foto");
					//System.out.println("Foto "+foto.dumpXML());
					String del = "";
					for(int ii = 0; ii < foto.size(); ii++){
						//System.out.println("Foto "+ii+": "+foto.get(ii).value.property.value());
						//fotosHTML.value(fotosHTML.value() + "\n<img height=300px src=\"" + foto.get(ii).value.property.value() + "\"/>");
						fotosHTML.value(fotosHTML.value() + del + "'" + foto.get(ii).value.property.value() + "'");
						del = ",";
					}
					//fotosHTML.value(fotosHTML.value() + "\n</nobr></div>");
					cnt.value(foto.size());
					//result.value("\n<ul>");
					for(int ii = 0; ii < data.size(); ii++){
						result.value(result.value() + "\n<li>"
								+ data.get(ii).child("Пункт").value.property.value()
								+ ": " + data.get(ii).child("ЗначениеРесурса").value.property.value()
								.replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br/>")
								+ "</li>");
					}
					//result.value(result.value() + "<ul>");
				}catch(Throwable t){
					t.printStackTrace();
					result.value(t.getMessage() + "\n" + url.value());
				}

			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				//System.out.println("result " + result.dumpXML());
				showCatInfoPhoto(result.value(), fotosHTML.value(),cnt.value().intValue());
			}
		}).status.is("Подождите").start(this)
		;
	}

	void showCatInfoPhoto(String globalCat, String fotos,int cnt){
		/*if(bm != null){
			layoutless.child(new Decor(ActivityPhoto.this).bitmap.is(bm)//
					.width().is(bm.getWidth()).height().is(bm.getHeight())
					.left().is(Auxiliary.tapSize / 2).top().is(Auxiliary.tapSize / 2)//
			);
		}else{
			int l = (int)((layoutless.width().property.value() / 2 - 200) / 2);
			int t = (int)((layoutless.height().property.value() - 200) / 2);
			layoutless.child(new Decor(ActivityPhoto.this).labelText.is("Нет фотографии")//
					.width().is(200).height().is(200).left().is(l).top().is(t)//
			);
		}*/
		/*
		layoutless.child(new Decor(ActivityPhoto.this).background.is(0xffeeeeee)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize / 2))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.left().is(layoutless.width().property.divide(2))//
				.top().is(Auxiliary.tapSize * 0.5)//
		);*/
		brwsr = new WebRender(this).afterLink.is(new Task(){
			@Override
			public void doTask(){
				try{
					final android.net.Uri uri = android.net.Uri.parse(brwsr.url.property.value());
					System.out.println(uri);
					Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, uri);
					ActivityPhoto.this.startActivity(openUrlIntent);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		});
		layoutless.child(brwsr//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
				.left().is(0)//
				.top().is(0)//
		);
/*
		String description = "<html>"
				+ "	<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<style>"
				+ "			body {"
				+ "				padding: 0px;"
				+ "				margin: 0px;"
				+ "			}"
				+ "			p {"
				+ "				padding-left: 16px;"
				+ "				padding-right: 16px;"
				+ "			}"
				+ "			.fotos {"
				+ "				overflow-x: auto;"
				+ "				overflow-y: hidden;"
				+ "				width: inherit;"
				+ "				height: 320px;"
				+ "				border-style: none;"
				+ "				padding-left: 16px;"
				+ "				padding-right: 16px;"
				+ "				padding-top: 0px;"
				+ "				padding-bottom: 0px;"
				+ "				margin: 0px;"
				+ "				text-align: center;"
				+ "			}"
				+ "			img {"
				+ "				padding: 0px;"
				+ "				margin: 0px;"
				+ "			}"
				+ "		</style>"
				+ "	</head>"
				+ "	<body>"
				+ "		<p>&nbsp;</p>";
		*/
		String description = template1;
		if(proizvoditel.trim().length() > 0){
			description = description + "\n<p><i>Производитель:</i> <b>" + proizvoditel + "</b></p>";
		}
		if(SrokGodnosti.trim().length() > 0){
			description = description + "\n<p><i>Срок годности:</i> " + SrokGodnosti + "</p>";
		}
		if(EdinicyIzmereniyaNaimenovanie.trim().length() > 0){
			description = description + "\n<p><i>Квант:</i> " + EdinicyIzmereniyaNaimenovanie + "</p>";
		}
		if(Nabor.trim().length() > 0){
			description = description + "\n<p><i>Состав:</i> " + Nabor + "</p>";
		}
		//description = description + fotos;
		if(cnt>1){
			description = description + template3;
		}else{
			description = description + template3ii;
		}
		if(kommentariy.trim().length() > 0){
			description = description + "\n<p><i>Описание:</i> " + kommentariy.replace("\n", "<br/>") + "</p>";
		}
		description = description + template5;
		java.util.Vector<Bough> all = managers.children("Ответственные");
		if(all.size() > 0){
			//description = description + "\n<p>Ответственные:</p>";
			//description = description + "<ul>";
			for(int ii = 0; ii < all.size(); ii++){
				description = description + "\n<li>" + all.get(ii).child("ФИО").value.property.value()
						+ ", " + all.get(ii).child("Должность").value.property.value();
				java.util.Vector<Bough> kontakts = all.get(ii).children("Контакты");
				String delimtr = ": <i>";
				for(int kk = 0; kk < kontakts.size(); kk++){
					if(kontakts.get(kk).child("Контакт").value.property.value().length() > 3){
						String vid = kontakts.get(kk).child("ВидКонтакта").value.property.value().trim();
						String znach = kontakts.get(kk).child("Контакт").value.property.value().trim();
						description = description + delimtr + vid;
						if(isemail(znach)){
							description = description + ": <a href='mailto:" + znach + "'>" + znach + "</a>";
						}else{
							description = description + ": " + znach + "";
						}
						delimtr = ", ";
					}
				}
				description = description + "</i></li>";
			}
			//description = description + "</ul>";
		}
		description = description + template7;

		if(globalCat.trim().length() > 0){
			//description = description + "<p><i>Глобальный каталог:</i> " + globalCat + "</p>";
			description = description + globalCat;
		}
		description = description + template9;
		description = description + fotos;
		description = description + template11;
		//description = description + "<p>&nbsp;</p></body></html>";
		/*
		layoutless.child(new HTMLBox(ActivityPhoto.this)//.background.is(0xffccff99)//
				.htmlText.is(description + description + description)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize * 1.5))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 2))//
				.left().is(layoutless.width().property.divide(2).plus(Auxiliary.tapSize / 2))//
				.top().is(Auxiliary.tapSize)//
		);*/
		String page = Cfg.workFolder + "artikul.html";
		File html = new File(page);
		Auxiliary.writeTextToFile(html, description, "utf-8");
		brwsr.go("file://" + page);
	}

	boolean isemail(String emailStr){
		java.util.regex.Pattern emailRegex = java.util.regex.Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", java.util.regex.Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher matcher = emailRegex.matcher(emailStr);
		return matcher.matches();
	}

	void loadPhoto(final String a){
		new Expect().status.is("Подождите...").task.is(new Task(){
			@Override
			public void doTask(){
				String artikul = a;
				artikul = artikul.replace('/', '-');
				artikul = artikul.replace('\\', '-');
				System.out.println(Settings.getInstance().photoURL + artikul + ".bmp");
				//bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL + artikul + ".bmp");
				/*bm = Auxiliary.loadBitmapFromPublicURL(Settings.getInstance().photoURL + artikul + ".bmp");
				if(bm == null){
					//bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL + artikul + ".jpg");
					bm = Auxiliary.loadBitmapFromPublicURL(Settings.getInstance().photoURL + artikul + ".jpg");
				}
				if(bm == null){
					//bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL + artikul + ".gif");
					bm = Auxiliary.loadBitmapFromPublicURL(Settings.getInstance().photoURL + artikul + ".gif");
				}
				if(bm == null){
					//bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL + artikul + ".png");
					bm = Auxiliary.loadBitmapFromPublicURL(Settings.getInstance().photoURL + artikul + ".png");
				}
				if(bm == null){
					System.out.println("no bitmap");
				}else{
					double ww = layoutless.width().property.value() / 2 - Auxiliary.tapSize;
					double hh = layoutless.height().property.value() - Auxiliary.tapSize;
					double rw = ww / bm.getWidth();
					double newW = bm.getWidth() * rw;
					double newH = bm.getHeight() * rw;
					if(newH > hh){
						rw = hh / bm.getHeight();
						newW = bm.getWidth() * rw;
						newH = bm.getHeight() * rw;
					}
					bm = Bitmap.createScaledBitmap(bm, (int)newW, (int)newH, true);
				}*/

				try{
					String infoUrl = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/Otvetstvenie/GetFIO/" + artikul;
					//System.out.println("infoUrl "+infoUrl);
					byte[] raw = Auxiliary.loadFileFromPrivateURL(infoUrl, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), "UTF-8");
					String info = new String(raw, "UTF-8");
					managers = Bough.parseJSON(info);
					System.out.println(managers.dumpXML());
				}catch(Throwable ttt){
					ttt.printStackTrace();
					managers = new Bough();
					managers.value.is(ttt.getMessage());
				}
			}
		}).afterDone.is(new Task(){
			@Override
			public void doTask(){
				loadInfoAndShowPhoto(a);
			}
		}).afterCancel.is(new Task(){
			@Override
			public void doTask(){
				ActivityPhoto.this.finish();
			}
		}).start(this);
	}
}
