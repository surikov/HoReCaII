package sweetlife.android10.reports;

import android.app.Activity;

import android.content.*;
import android.graphics.*;
import android.os.Bundle;

import java.io.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;

//import reactive.ui.OrderItemInfo;

import sweetlife.android10.supervisor.*;
import tee.binding.task.*;
import tee.binding.*;

public class ActivityPhoto extends Activity{
	Layoutless layoutless;
	WebRender brwsr;
	public static String artikulField = "artikulField";
	public static String nameField = "nameField";
	Bitmap bm = null;
	//String comment = "";
	String kommentariy = "";
	String Nabor = "";
	String SrokGodnosti = "";
	String EdinicyIzmereniyaNaimenovanie = "";
	String proizvoditel = "";
	Bough managers = null;

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

	void showInfoPhoto(){
		if(bm != null){
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
		}
		layoutless.child(new Decor(ActivityPhoto.this).background.is(0xffeeeeee)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize / 2))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.left().is(layoutless.width().property.divide(2))//
				.top().is(Auxiliary.tapSize * 0.5)//
		);
		brwsr = new WebRender(this).afterLink.is(new Task() {
			@Override
			public void doTask() {
				try {
					final android.net.Uri uri = android.net.Uri.parse(brwsr.url.property.value());
					System.out.println(uri);
					Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, uri);
					ActivityPhoto.this.startActivity(openUrlIntent);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
		layoutless.child(brwsr//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize * 0.7))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 1.2))//
				.left().is(layoutless.width().property.divide(2).plus(Auxiliary.tapSize *0.1))//
				.top().is(Auxiliary.tapSize*0.6)//
		);

		String description = "<html><body>";
		description = description + "<p><i>Производитель:</i> <b>" + proizvoditel + "</b></p>";
		description = description + "<p><i>Срок годности:</i> " + SrokGodnosti + "</p>";
		description = description + "<p><i>Квант:</i> " + EdinicyIzmereniyaNaimenovanie + "</p>";
		description = description + "<p><i>Состав:</i> " + Nabor + "</p>";
		description = description + "<p><i>Описание:</i> " + kommentariy.replace("\n", "<br/>") + "</p>";
		java.util.Vector<Bough> all = managers.children("Ответственные");
		description = description + "<p>Ответственные:</p>";
		description = description + "<ul>";
		for(int ii = 0; ii < all.size(); ii++){
			description = description + "<li>" + all.get(ii).child("ФИО").value.property.value()
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
		description = description + "</ul>";
		description = description + "</body></html>";
		/*
		layoutless.child(new HTMLBox(ActivityPhoto.this)//.background.is(0xffccff99)//
				.htmlText.is(description + description + description)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize * 1.5))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize * 2))//
				.left().is(layoutless.width().property.divide(2).plus(Auxiliary.tapSize / 2))//
				.top().is(Auxiliary.tapSize)//
		);*/
		String page = Cfg.workFolder+"artikul.html";
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
				bm = Auxiliary.loadBitmapFromPublicURL(Settings.getInstance().photoURL + artikul + ".bmp");
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
				}

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
				showInfoPhoto();
			}
		}).afterCancel.is(new Task(){
			@Override
			public void doTask(){
				ActivityPhoto.this.finish();
			}
		}).start(this);
	}
}
