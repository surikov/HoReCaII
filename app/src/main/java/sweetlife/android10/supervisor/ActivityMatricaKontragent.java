package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;

import java.util.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.text.*;

public class ActivityMatricaKontragent extends Activity {
	Layoutless layoutless;
	String _id = "";
	Note kontragent = new Note();
	Note tipTT = new Note();
	Note tipOplaty = new Note();
	Numeric pn = new Numeric();
	Numeric vt = new Numeric();
	Numeric sr = new Numeric();
	Numeric ct = new Numeric();
	Numeric pt = new Numeric();
	Numeric sb = new Numeric();
	
	Numeric pn1 = new Numeric();
	Numeric vt1 = new Numeric();
	Numeric sr1 = new Numeric();
	Numeric ct1 = new Numeric();
	Numeric pt1 = new Numeric();
	Numeric sb1 = new Numeric();
	
	Numeric potencialTT = new Numeric();
	//Numeric nacenka = new Numeric();
	//Numeric tom1 = new Numeric();
	//Numeric tom2 = new Numeric();
	//Numeric tom3 = new Numeric();
	Note tomValue = new Note();
	
	Note vrrab = new Note();
	Note email = new Note();
	
	double dvuhnedel=0;
	
	Numeric planTysRub = new Numeric();
	Numeric planSTM = new Numeric();
	//Note tom1Name = new Note();
	//Note tom2Name = new Note();
	//Note tom3Name = new Note();
	Note tomName = new Note();
	RedactSingleChoice tipOplatyChoise;
	MenuItem menuSave;
	MenuItem menuDelete;
	MenuItem menuOtchety;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String s = bundle.getString("_id");			
			if (s != null) {
				_id = s;
				dvuhnedel=bundle.getDouble("dvuhnedel");
				this.setTitle("Матрица " + _id+" ("+(dvuhnedel>0?"двухнедельный":"однонедельный")+" маршрут)");
			}
			else {
				//wrong
			}
		}
		composeGUI();
		fillData();
	}
	void fillData() {
		String sql = "select"//
				+ " k.naimenovanie as kontragent"//
				+ ",m.tipTT as tipTT"//
				+ ",m.tipOplaty as tipOplaty"//
				+ ",m.pn as pn"//
				+ ",m.vt as vt"//
				+ ",m.sr as sr"//
				+ ",m.ct as ct"//
				+ ",m.pt as pt"//
				+ ",m.sb as sb"//
				+ ",m.pn1 as pn1"//
				+ ",m.vt1 as vt1"//
				+ ",m.sr1 as sr1"//
				+ ",m.ct1 as ct1"//
				+ ",m.pt1 as pt1"//
				+ ",m.sb1 as sb1"//
				+ ",m.potencialTT as potencialTT"//
				+ ",m.tom1 as tom1"//
				+ ",m.tom2 as tom2"//
				+ ",m.tom3 as tom3"//
				+ ",m.nacenka as nacenka"//
				+ ",m.planTysRub as planTysRub"//
				+ ",m.planSTM as planSTM"//
				+ ",m.vrrab as vrrab"//
				+ ",m.email as email"//
				//+ ",m.upload as upload"//
				+ ",x.periodDeystvia as periodDeystvia"//
				+ " from MatricaRowsX m"//
				+ " join kontragenty k on m.kontragent=k.kod"//
				+ " join matricaX x on x._id=m.matrica_id"//
				+ " where m._id=" + _id;
		Bough r = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(r.dumpXML());
		kontragent.value(r.child("row").child("kontragent").value.property.value());
		tipTT.value(r.child("row").child("tipTT").value.property.value());
		tipOplaty.value(r.child("row").child("tipOplaty").value.property.value());
		
		vrrab.value(r.child("row").child("vrrab").value.property.value());
		email.value(r.child("row").child("email").value.property.value());
		
		
		pn.value(Numeric.string2double(r.child("row").child("pn").value.property.value()));
		vt.value(Numeric.string2double(r.child("row").child("vt").value.property.value()));
		sr.value(Numeric.string2double(r.child("row").child("sr").value.property.value()));
		ct.value(Numeric.string2double(r.child("row").child("ct").value.property.value()));
		pt.value(Numeric.string2double(r.child("row").child("pt").value.property.value()));
		sb.value(Numeric.string2double(r.child("row").child("sb").value.property.value()));
		
		
		
		pn1.value(Numeric.string2double(r.child("row").child("pn1").value.property.value()));
		vt1.value(Numeric.string2double(r.child("row").child("vt1").value.property.value()));
		sr1.value(Numeric.string2double(r.child("row").child("sr1").value.property.value()));
		ct1.value(Numeric.string2double(r.child("row").child("ct1").value.property.value()));
		pt1.value(Numeric.string2double(r.child("row").child("pt1").value.property.value()));
		sb1.value(Numeric.string2double(r.child("row").child("sb1").value.property.value()));
		
		potencialTT.value(Numeric.string2double(r.child("row").child("potencialTT").value.property.value()));
		//nacenka.value(Numeric.string2double(r.child("row").child("nacenka").value.property.value()));
		//tom1.value(Numeric.string2double(r.child("row").child("tom1").value.property.value()));
		//tom2.value(Numeric.string2double(r.child("row").child("tom2").value.property.value()));
		//tom3.value(Numeric.string2double(r.child("row").child("tom3").value.property.value()));
		tomValue.value(r.child("row").child("tom1").value.property.value() + " / " + r.child("row").child("tom2").value.property.value() + " / " + r.child("row").child("tom3").value.property.value());
		planTysRub.value(Numeric.string2double(r.child("row").child("planTysRub").value.property.value()));
		planSTM.value(Numeric.string2double(r.child("row").child("planSTM").value.property.value()));
		Date d = new Date();
		d.setTime((long) Numeric.string2double(r.child("row").child("periodDeystvia").value.property.value()));
		int p = d.getMonth();
		if (p > 11) {
			p = 0;
		}
		//tom1Name.value(ActivityMatricaEdit.monthName(p - 3));
		//tom2Name.value(ActivityMatricaEdit.monthName(p - 2));
		//tom3Name.value(ActivityMatricaEdit.monthName(p - 1));
		tomName.value(ActivityMatricaEdit.monthName(p - 3) + " / " + ActivityMatricaEdit.monthName(p - 2) + " / " + ActivityMatricaEdit.monthName(p - 1));
		tipOplatyChoise.item("").item("нал/отср").item("б/н").item("тов. чек").item("нал/факт").item("предоплата");
		if (tipOplaty.value().equals("нал/отср")) {
			tipOplatyChoise.selection.is(1);
		}
		if (tipOplaty.value().equals("б/н")) {
			tipOplatyChoise.selection.is(2);
		}
		if (tipOplaty.value().equals("тов. чек")) {
			tipOplatyChoise.selection.is(3);
		}
		if (tipOplaty.value().equals("нал/факт")) {
			tipOplatyChoise.selection.is(4);
		}
		if (tipOplaty.value().equals("предоплата")) {
			tipOplatyChoise.selection.is(5);
		}
	}
	void dayRow(String label,int row,final Numeric a,final Numeric b){
		layoutless.child(new Decor(this).labelText.is(label).labelAlignRightCenter()//
				.width().is(layoutless.width().property.multiply(0.3))//
				.height().is(0.8 * Auxiliary.tapSize)//
				.left().is(layoutless.shiftX.property)//
				.top().is(layoutless.shiftY.property.plus(0.8 * (row+0.25) * Auxiliary.tapSize)));
		double w=2.5;
		if(b==null){
			w=5;
		}
		layoutless.child(new RedactTime(this).time.is(a)//
			.width().is(w * Auxiliary.tapSize)//
			.height().is(0.8 * Auxiliary.tapSize)//
			.left().is(layoutless.width().property.multiply(0.3).plus(0.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
			.top().is(layoutless.shiftY.property.plus(0.8 * (row+0.25) * Auxiliary.tapSize)));
		if(b!=null){
		layoutless.child(new RedactTime(this).time.is(b)//
				.width().is(2.5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
				.left().is(layoutless.width().property.multiply(0.3).plus(2.6 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * (row+0.25) * Auxiliary.tapSize)));
		}
		
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				if(a!=null){
					a.value(0);
				}
				if(b!=null){
					b.value(0);
				}
			}
		}).width().is(0.8 * Auxiliary.tapSize)//
			.height().is(0.8 * Auxiliary.tapSize)//
			.left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
			.top().is(layoutless.shiftY.property.plus(0.8 * (row+0.25) * Auxiliary.tapSize)));
		
	}
	void composeGUI() {
		tipOplatyChoise = new RedactSingleChoice(this);
		layoutless.field(this, 0, "контрагент", new Decor(this).labelText.is(kontragent).labelAlignLeftCenter());
		layoutless.field(this, 1, "тип ТТ", new Decor(this).labelText.is(tipTT).labelAlignLeftCenter());
		layoutless.field(this, 2, "тип оплаты", tipOplatyChoise);
		//layoutless.field(this, 3, "пн", new RedactTime(this).time.is(pn));
		
		if(dvuhnedel>0){
			dayRow("пн (нечётн./чётн.)",3,pn,pn1);
			dayRow("вт (нечётн./чётн.)",4,vt,vt1);
			dayRow("ср (нечётн./чётн.)",5,sr,sr1);
			dayRow("чт (нечётн./чётн.)",6,ct,ct1);
			dayRow("пт (нечётн./чётн.)",7,pt,pt1);
			dayRow("сб (нечётн./чётн.)",8,sb,sb1);
		}else{
			
			
			dayRow("пн",3,pn,null);
			dayRow("вт",4,vt,null);
			dayRow("ср",5,sr,null);
			dayRow("чт",6,ct,null);
			dayRow("пт",7,pt,null);
			dayRow("сб",8,sb,null);
		}
		/*
		layoutless.child(new Decor(this).labelText.is("пн").labelAlignRightCenter()//
				.width().is(layoutless.width().property.multiply(0.3))//
				.height().is(0.8 * Auxiliary.tapSize)//
				.left().is(0)//
				.top().is(layoutless.shiftY.property.plus(0.8 * 3.25 * Auxiliary.tapSize)));
		
		layoutless.child(new RedactTime(this).time.is(pn)//
			.width().is(2.5 * Auxiliary.tapSize)//
			.height().is(0.8 * Auxiliary.tapSize)//
			.left().is(layoutless.width().property.multiply(0.3).plus(0.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
			.top().is(layoutless.shiftY.property.plus(0.8 * 3.25 * Auxiliary.tapSize)));
		layoutless.child(new RedactTime(this).time.is(pn1)//
				.width().is(2.5 * Auxiliary.tapSize)//
				.height().is(0.8 * Auxiliary.tapSize)//
				.left().is(layoutless.width().property.multiply(0.3).plus(2.6 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * 3.25 * Auxiliary.tapSize)));
		
		
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				pn.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize)//
			.height().is(0.8 * Auxiliary.tapSize)//
			.left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
			.top().is(layoutless.shiftY.property.plus(0.8 * 3.25 * Auxiliary.tapSize)));
		
		
		layoutless.field(this, 4, "вт", new RedactTime(this).time.is(vt));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				vt.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize).left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * 4.25 * Auxiliary.tapSize)));
		layoutless.field(this, 5, "ср", new RedactTime(this).time.is(sr));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				sr.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize).left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * 5.25 * Auxiliary.tapSize)));
		layoutless.field(this, 6, "чт", new RedactTime(this).time.is(ct));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				ct.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize).left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * 6.25 * Auxiliary.tapSize)));
		layoutless.field(this, 7, "пт", new RedactTime(this).time.is(pt));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				pt.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize)//
			.height().is(0.8 * Auxiliary.tapSize)//
			.left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
			.top().is(layoutless.shiftY.property.plus(0.8 * 7.25 * Auxiliary.tapSize)));//
		layoutless.field(this, 8, "сб", new RedactTime(this).time.is(sb));
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				sb.value(0);
			}
		}).width().is(0.8 * Auxiliary.tapSize).height().is(0.8 * Auxiliary.tapSize).left().is(layoutless.width().property.multiply(0.3).plus(5.1 * Auxiliary.tapSize).plus(layoutless.shiftX.property))//
				.top().is(layoutless.shiftY.property.plus(0.8 * 8.25 * Auxiliary.tapSize)));
		*/
		layoutless.field(this, 9, "потенциал ТТ", new RedactNumber(this).number.is(potencialTT));
		layoutless.field(this, 10, tomName, new Decor(this).labelText.is(tomValue).labelAlignLeftCenter());
		//layoutless.field(this, 10, tom1Name, new RedactNumber(this).number.is(tom1));
		//layoutless.field(this, 11, tom2Name, new RedactNumber(this).number.is(tom2));
		//layoutless.field(this, 12, tom3Name, new RedactNumber(this).number.is(tom3));
		layoutless.field(this, 11, "план, тыс. руб", new RedactNumber(this).number.is(planTysRub));
		layoutless.field(this, 12, "план СТМ, тыс. руб", new RedactNumber(this).number.is(planSTM));
		//RedactNumber r= new RedactNumber(this).number.is(nacenka);
		//r.setSelectAllOnFocus(true);
		//layoutless.field(this, 12, "наценка, %", new RedactNumber(this).number.is(nacenka).selectAllOnFocus.is(true));
		
		
		layoutless.field(this, 13, "время работы", new RedactText(this).text.is(vrrab).selectAllOnFocus.is(true));
		layoutless.field(this, 14, "e-mail", new RedactText(this).text.is(email).selectAllOnFocus.is(true));
		
		
		/*layoutless.field(this, 14, "", new Knob(this).labelText.is("Сохранить")).afterTap.is(new Task() {
			@Override
			public void doTask() {
				save();
			}
		});
		layoutless.field(this, 15, "", new Knob(this).labelText.is("Удалить")).afterTap.is(new Task() {
			@Override
			public void doTask() {
				delete();
			}
		});*/
		layoutless.innerHeight.is(0.8 * 17 * Auxiliary.tapSize);
	}
	void save() {
		DecimalFormat format = new DecimalFormat("##########");
		String sql = "update MatricaRowsX"//
				+ "\n	set tipTT='" + tipTT.value() + "'"//
				+ "\n	,tipOplaty='" + tipOplaty.value() + "'"//
				+ "\n	,pn=" + format.format(pn.value())//
				+ "\n	,vt=" + format.format(vt.value())//
				+ "\n	,sr=" + format.format(sr.value())//
				+ "\n	,ct=" + format.format(ct.value())//
				+ "\n	,pt=" + format.format(pt.value())//
				+ "\n	,sb=" + format.format(sb.value())//
				
				
				+ "\n	,pn1=" + format.format(pn1.value())//
				+ "\n	,vt1=" + format.format(vt1.value())//
				+ "\n	,sr1=" + format.format(sr1.value())//
				+ "\n	,ct1=" + format.format(ct1.value())//
				+ "\n	,pt1=" + format.format(pt1.value())//
				+ "\n	,sb1=" + format.format(sb1.value())//
				
				
				
				
				+ "\n	,potencialTT=" + potencialTT.value()//
				//+ "\n	,nacenka=" + nacenka.value()//
				+ "\n	,nacenka=''"
				
				
				+ "\n	,vrrab='" + vrrab.value()+"'"//	
				+ "\n	,email='" + email.value()+"'"//	
				
				//+ "\n	,tom1=" + tom1.value()//	
				//+ "\n	,tom2=" + tom2.value()//	
				//+ "\n	,tom3=" + tom3.value()//	
				+ "\n	,planTysRub=" + planTysRub.value()//
				+ "\n	,planSTM=" + planSTM.value()//
				+ "\n	where _id=" + _id//
		;
		//System.out.println("="+planTysRub.value());
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}
	void delete() {
		String sql="delete from MatricaRowsX where _id="+_id+" and ifnull(uploaded,0)=0;";
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}
	void promptDelete() {
		Auxiliary.pickConfirm(this, "Удалить запись. Вы уверены?", "Удалить", new Task() {
			@Override
			public void doTask() {
				delete();
			}
		});
	}
	@Override
	public void onBackPressed() {
		//save();
		super.onBackPressed();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuDelete = menu.add("Удалить");
		menuSave = menu.add("Сохранить");
		menuOtchety = menu.add("Отчёты");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuDelete) {
			promptDelete();
			return true;
		}
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if (item == menuSave) {
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
