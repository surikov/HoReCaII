package sweetlife.android10.reports;

import android.app.Activity;

import android.graphics.*;
import android.os.Bundle;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;

//import reactive.ui.OrderItemInfo;

import tee.binding.task.*;
import tee.binding.*;

public class ActivityPhoto extends Activity {
	Layoutless layoutless;
	public static String artikulField = "artikulField";
	public static String nameField = "nameField";
	Bitmap bm = null;
	//String comment = "";
	String kommentariy="";
	String Nabor="";
	String SrokGodnosti="";
	String EdinicyIzmereniyaNaimenovanie="";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle extras = getIntent().getExtras();
		String artikul = extras.getString(artikulField);
		 //comment = getComment(artikul);
		fillComment(artikul);
		String name = extras.getString(nameField);
		this.setTitle("артикул "+artikul + ", " + name);
		layoutless.setBackgroundColor(0xffffffff);
		loadPhoto(artikul);
	}
	void  fillComment(String artikul){
		String sql="select n.kommentariy as kommentariy, n.Nabor as Nabor, n.SrokGodnosti as SrokGodnosti,n.kvant || n.skladEdIzm || ' по ' || n.skladEdVes || 'кг' as [EdinicyIzmereniyaNaimenovanie] from nomenklatura n where artikul='"+artikul.trim()+"';";
		Bough b=Auxiliary.fromCursor(				ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		 kommentariy= b.child("row").child("kommentariy").value.property.value();
		 Nabor= b.child("row").child("Nabor").value.property.value();
		 SrokGodnosti= b.child("row").child("SrokGodnosti").value.property.value();
		EdinicyIzmereniyaNaimenovanie= b.child("row").child("EdinicyIzmereniyaNaimenovanie").value.property.value();
		//String txt= b.child("row").child("k").value.property.value();
		//txt=txt.replace("Состав: ","\nСостав:\n");
		//txt=txt+"\n"+"---";
		//return txt;
	}
	void loadPhoto(final String a) {
		new Expect().status.is("Подождите...")//
		.task.is(new Task() {
			@Override
			public void doTask() {
				String artikul = a;
				artikul = artikul.replace('/', '-');
				artikul = artikul.replace('\\', '-');
				//10.10.0.17
				System.out.println(Settings.getInstance().photoURL+ artikul + ".bmp");
				bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL+ artikul + ".bmp");
				if (bm == null) {
					bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL+ artikul + ".jpg");
				}
				if (bm == null) {
					bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL+ artikul + ".gif");
				}
				if (bm == null) {
					bm = sweetlife.android10.utils.UIHelper.loadImageFromURL(Settings.getInstance().photoURL+ artikul + ".png");
				}
				if (bm == null) {
					//System.out.println("no bitmap");
				}else{
					System.out.println("found " + bm.getWidth()+'x'+bm.getHeight());
					double ww=layoutless.width().property.value()/2-Auxiliary.tapSize;
					double hh=layoutless.height().property.value()-Auxiliary.tapSize;
					double rw=ww/bm.getWidth();
					double newW=bm.getWidth()*rw;
					double newH=bm.getHeight()*rw;
					if(newH>hh){
						rw=hh/bm.getHeight();
						newW=bm.getWidth()*rw;
						newH=bm.getHeight()*rw;
					}
					System.out.println("to " + ww+'x'+hh+" : "+rw+" / " + newW+'x'+newH);
					bm=Bitmap.createScaledBitmap(bm, (int)newW, (int)newH, true);
					System.out.println("now " + bm.getWidth()+'x'+bm.getHeight());
					/*if(bm.getHeight()>layoutless.height().property.value()){
						double r=0.7*layoutless.height().property.value()/bm.getHeight();
						bm=Bitmap.createScaledBitmap(bm, (int)(bm.getWidth()*r), (int)(bm.getHeight()*r), true);
					}*/
				}
			}
		})//
		.afterDone.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("ok " + bm);
				if (bm != null) {
					/*
					int w = bm.getWidth();
					int h = bm.getHeight();
					int l = (int) ((layoutless.width().property.value() - w) / 2);
					int t = (int) ((layoutless.height().property.value() - h) / 2);
					//System.out.println("ok: "+ w+"x"+h+": "+ bm);
					layoutless.child(new Decor(ActivityPhoto.this).bitmap.is(bm)//
							.width().is(w).height().is(h).left().is(l).top().is(t)//
							);
					*/
					layoutless.child(new Decor(ActivityPhoto.this).bitmap.is(bm)//
							.width().is(bm.getWidth()).height().is(bm.getHeight())
							.left().is(Auxiliary.tapSize/2).top().is(Auxiliary.tapSize/2)//
					);
				}
				else {
					//ActivityPhoto.this.finish();
					int l = (int) ((layoutless.width().property.value()/2 - 200) / 2);
					int t = (int) ((layoutless.height().property.value() - 200) / 2);
					layoutless.child(new Decor(ActivityPhoto.this).labelText.is("Нет фотографии")//
							.width().is(200).height().is(200).left().is(l).top().is(t)//
							);
				}
				layoutless.child(new Decor(ActivityPhoto.this).background.is(0xffeeeeee)//
						.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize/2))//
						.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.left().is(layoutless.width().property.divide(2))//
						.top().is(Auxiliary.tapSize*0.5)//
				);
				String comment="Описание: "+kommentariy
						+"\n\nСостав: "+Nabor
						+"\n\nСрок годности: "+SrokGodnosti+" сут."
						+"\n\nКвант: "+EdinicyIzmereniyaNaimenovanie
						;
				layoutless.child(new Decor(ActivityPhoto.this)//.background.is(0xffccff99)//
				.labelText.is(comment)//
						.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize*1.5))//
						.height().is(layoutless.height().property.minus(Auxiliary.tapSize*2))//
						.left().is(layoutless.width().property.divide(2).plus(Auxiliary.tapSize/2))//
						.top().is(Auxiliary.tapSize)//
						);
			}
		})//
		.afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("cancel");
				ActivityPhoto.this.finish();
			}
		})//
				.start(this);
	}
}
