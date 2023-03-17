package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;

import java.util.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

import java.text.*;

public class ActivityMatricaEdit extends Activity {
	MenuItem menuDeleteMatrica;
	MenuItem menuNewKontragent;
	MenuItem menuFillInitial;
	MenuItem menuOtchety;
	MenuItem menuUpload;
	MenuItem menuLock;
	MenuItem menuUnlock;
	Layoutless layoutless;
	String _id = "";
	Numeric territorySelection = new Numeric();
	Numeric dvuhnedel = new Numeric();
	//Numeric period = new Numeric();
	Numeric periodMonth = new Numeric();
	Numeric periodYear = new Numeric();
	Numeric nacenkaprocent = new Numeric();
	Note kommentariy = new Note();
	int gridPageSize = 300;
	Numeric gridOffset = new Numeric();
	DataGrid gridMatrica;
	ColumnText mNN;
	ColumnText mKontragent;
	ColumnText mPn;
	ColumnText mVt;
	ColumnText mSr;
	ColumnText mCt;
	ColumnText mPt;
	ColumnText mSb;
	
	/*
	ColumnDate mPn1;
	ColumnDate mVt1;
	ColumnDate mSr1;
	ColumnDate mCt1;
	ColumnDate mPt1;
	ColumnDate mSb1;
	*/


	ColumnNumeric mPotencial;
	ColumnNumeric mTOM1;
	ColumnNumeric mTOM2;
	ColumnNumeric mTOM3;
	ColumnText mTipOplati;
	ColumnNumeric mPlan;
	//ColumnNumeric mNacenka;
	RedactSingleChoice terrRedactSingleChoice;
	RedactSingleChoice oddEven;
	RedactSingleChoice yearsRedactSingleChoice;
	RedactSingleChoice monthsRedactSingleChoice;
	Numeric avgPn = new Numeric();
	Numeric avgVt = new Numeric();
	Numeric avgSr = new Numeric();
	Numeric avgCt = new Numeric();
	Numeric avgPt = new Numeric();
	Numeric avgSb = new Numeric();

	Numeric avgPn1 = new Numeric();
	Numeric avgVt1 = new Numeric();
	Numeric avgSr1 = new Numeric();
	Numeric avgCt1 = new Numeric();
	Numeric avgPt1 = new Numeric();
	Numeric avgSb1 = new Numeric();

	Note footPn = new Note();
	Note footVt = new Note();
	Note footSr = new Note();
	Note footCt = new Note();
	Note footPt = new Note();
	Note footSb = new Note();


	Numeric footPotencial = new Numeric();
	Numeric footTOM1 = new Numeric();
	Numeric footTOM2 = new Numeric();
	Numeric footTOM3 = new Numeric();
	Numeric footPlan = new Numeric();
	Numeric footNacenka = new Numeric();
	Numeric footPlanUtro = new Numeric();
	Numeric footPlanLetuchka = new Numeric();
	Numeric footPlanItogo = new Numeric();
	Numeric footPlanNar = new Numeric();
	Note footOsnov = new Note();
	Note terrName = new Note();
	Note periodName = new Note();
	DataGrid gridSvod;
	ColumnDate sDenNedeli;
	ColumnDate sData;
	ColumnNumeric sPlanUtro;
	ColumnNumeric sPlanLetuchka;
	ColumnNumeric sPlanItogo;
	ColumnNumeric sPlanNarItog;
	Bough currentMatrica;
	Bough currentSvod;
	Toggle filled = new Toggle();
	//int dataZagruzkiMarshruta = 0;
	long dataUpload = 0;
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

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
				this.setTitle("Матрица ТП: " + _id);
			} else {
				this.setTitle("Матрица ТП: ?");
			}
		}
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		Date d = new Date();
		String sql = "select periodDeystvia,dataUpload as dataUpload,kod, dvuhnedel  as dvuhnedel,nacenka as nacenka  from MatricaX where _id=" + _id;
		Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		dataUpload = (long) Numeric.string2double(bough.child("row").child("dataUpload").value.property.value());
		Calendar clndr = Calendar.getInstance();
		clndr.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		//System.out.println("clndr = " + clndr.getTime());
		clndr.setTimeInMillis((long) dataUpload);
		//System.out.println("dataUpload = " + dataUpload);
		//System.out.println("dataUpload clndr = " + clndr.getTime());
		d.setTime((long) Numeric.string2double(bough.child("row").child("periodDeystvia").value.property.value()));
		periodMonth.value(d.getMonth());
		if (periodMonth.value() > 11) {
			periodMonth.value(0);
		}
		periodYear.value(d.getYear() - 113);
		nacenkaprocent.value( Numeric.string2double(bough.child("row").child("nacenka").value.property.value()));
		String kod = bough.child("row").child("kod").value.property.value();
		dvuhnedel.value(Numeric.string2double(bough.child("row").child("dvuhnedel").value.property.value()));
		selectByKod(kod);
		//System.out.println(bough.dumpXML());
		composeGUI();
		//refillGrids();
		new Expect().task.is(new Task() {
			@Override
			public void doTask() {
				requeryData();
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				fillMatricaGrid();
				fillSvodGrid();
				checkFilled();
				gridMatrica.refresh();
				gridSvod.refresh();
				testMatrica();

			}
		}).status.is("Подождите").start(this);
	}

	void updateHeader() {
		int periodMonthInt = periodMonth.value().intValue();
		String periodMonthString = monthsRedactSingleChoice.items.get(periodMonthInt);
		int periodYearInt = periodYear.value().intValue();
		String periodYearString = yearsRedactSingleChoice.items.get(periodYearInt);
		periodName.value(periodMonthString + " " + periodYearString);//+", наценка "+nacenkaprocent.value()+"%");
		terrName.value("" + terrRedactSingleChoice.items.get(territorySelection.value().intValue()));
	}

	boolean valid9morningDay(String day, String tag) {
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			long s = (long) Numeric.string2double(row.child(tag).value.property.value());
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			c.setTimeInMillis(s);
			//c.get(Calendar.HOUR_OF_DAY);
			//Date d=new Date();
			//d.setTime(s);
			//System.out.println(tag+": "+s+": "+c);
			//System.out.println(d+".");
			if (c.get(Calendar.HOUR_OF_DAY) == 9 && c.get(Calendar.MINUTE) == 0) {
				return true;
			}
		}
		Auxiliary.warn(day + " нет визита в 9:00.", this);
		return false;
	}

	boolean validVRRab(String what) {
		if (what.trim().length() > 1) {
			String pattern = "\\s*([0-9]|0[0-9]|1[0-9]|2[0-4])\\s*:\\s*[0-5][0-9]\\s*-\\s*([0-9]|0[0-9]|1[0-9]|2[0-4])\\s*:\\s*[0-5][0-9]\\s*";
			//String what="12 :32 - 23:55";
			boolean n = what.matches(pattern);
			if (n) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	boolean valid9morning() {
		if (!valid9morningDay("В понедельник", "pn")) {
			return false;
		}
		if (!valid9morningDay("Во вторник", "vt")) {
			return false;
		}
		if (!valid9morningDay("В среду", "sr")) {
			return false;
		}
		if (!valid9morningDay("В четверг", "ct")) {
			return false;
		}
		if (!valid9morningDay("В пятницу", "pt")) {
			return false;
		}
		if (!valid9morningDay("В субботу", "sb")) {
			return false;
		}
		/*
		//HashMap<String, Integer> visitCount = new HashMap<String, Integer>();
		boolean no9;
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			//3240000
			Bough row = currentMatrica.children.get(i);
			String kontragent = row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value());
			//System.out.println(kontragent+": "+row.child("pn").value.property.value());
		}*/
		return true;
	}

	boolean validTimeDay(String msg, String curTimeString) {
		long curTimeMils = (long) Numeric.string2double(curTimeString);
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis(curTimeMils);
		if (c.get(Calendar.HOUR_OF_DAY) == 0 && c.get(Calendar.MINUTE) == 0) {
			return true;
		}
		Calendar from = Calendar.getInstance();
		from.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		from.setTimeInMillis(0);
		from.set(Calendar.HOUR_OF_DAY, 8);
		from.set(Calendar.MINUTE, 30);
		long fromS = from.getTimeInMillis();
		Calendar to = Calendar.getInstance();
		to.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		to.setTimeInMillis(0);
		to.set(Calendar.HOUR_OF_DAY, 19);
		to.set(Calendar.MINUTE, 0);
		long toS = to.getTimeInMillis();
		Calendar t = Calendar.getInstance();
		t.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		t.setTimeInMillis(0);
		t.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
		t.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
		curTimeMils = t.getTimeInMillis();

		if (curTimeMils < fromS || curTimeMils > toS) {
			Auxiliary.warn(msg, this);
			return false;
		}
		return true;
	}

	boolean validTime() {
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": понедельник: недопустимое время (должно быть от 8:30 до 19:00)", row.child("pn").value.property.value())) {
				return false;
			}
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": вторник: недопустимое время (должно быть от 8:30 до 19:00)", row.child("vt").value.property.value())) {
				return false;
			}
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": среда: недопустимое время (должно быть от 8:30 до 19:00)", row.child("sr").value.property.value())) {
				return false;
			}
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": четверг: недопустимое время (должно быть от 8:30 до 19:00)", row.child("ct").value.property.value())) {
				return false;
			}
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": пятница: недопустимое время (должно быть от 8:30 до 19:00)", row.child("pt").value.property.value())) {
				return false;
			}
			if (!validTimeDay(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
					+ ": суббота: недопустимое время (должно быть от 8:30 до 19:00)", row.child("sb").value.property.value())) {
				return false;
			}
		}
		return true;
	}

	boolean validNacenka() {
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			String kontragent = row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value());
			double nacenka = Numeric.string2double(row.child("nacenka").value.property.value());
			double planTysRub = Numeric.string2double(row.child("planTysRub").value.property.value());
			//if (planTysRub > 0) {
			if (nacenka < 7) {
				Auxiliary.warn("У контрагента " + kontragent + " наценка " + nacenka + "% (должна быть не меньше 7%).", this);
				return false;
			}
			//}
		}
		return true;
	}

	boolean validEquality() {
		double planKontragent = 0;
		double planSvod = 0;
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			//String kontragent = row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value());
			//double nacenka = Numeric.string2double(row.child("nacenka").value.property.value());
			double planTysRub = Numeric.string2double(row.child("planTysRub").value.property.value());
			//if (planTysRub > 0) {
			planKontragent = planKontragent + planTysRub;
			//System.out.println(kontragent + ": " + planTysRub + ": " + nacenka + ": " + val);
			//}
		}
		for (int i = 0; i < currentSvod.children.size(); i++) {
			Bough row = currentSvod.children.get(i);
			//String kontragent = row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value());
			//double nacenka = Numeric.string2double(row.child("nacenka").value.property.value());
			double plan = Numeric.string2double(row.child("planUtro").value.property.value()) + Numeric.string2double(row.child("planLetuchka").value.property.value());
			//if (planTysRub > 0) {
			planSvod = planSvod + plan;
			//System.out.println(kontragent + ": " + planTysRub + ": " + nacenka + ": " + val);
			//}
		}
		if (Math.round(planSvod) != Math.round(planKontragent)) {
			Auxiliary.warn("План по контрагентам (" + planKontragent + ") не равен плану по своду (" + planSvod + ").", this);
			return false;
		}
		return true;
	}

	/*boolean _validVisitCount() {
		//HashMap<String, Integer> visitCount = new HashMap<String, Integer>();
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			String kod = row.child("kontragent").value.property.value();
			String kontragent = kod + ": " + getKontragentName(kod);
			int cnt = 0;
			if (Numeric.string2double(row.child("pn").value.property.value()) > 0) {
				cnt++;
			}
			if (Numeric.string2double(row.child("vt").value.property.value()) > 0) {
				cnt++;
			}
			if (Numeric.string2double(row.child("sr").value.property.value()) > 0) {
				cnt++;
			}
			if (Numeric.string2double(row.child("ct").value.property.value()) > 0) {
				cnt++;
			}
			if (Numeric.string2double(row.child("pt").value.property.value()) > 0) {
				cnt++;
			}
			if (Numeric.string2double(row.child("sb").value.property.value()) > 0) {
				cnt++;
			}
			if (cnt > 2) {
				if (kontragent.equals("84541")// 
						|| kontragent.equals("82068")//
						|| kontragent.equals("18919")//
						|| kontragent.equals("13522")//
						|| kontragent.equals("82388")//
						|| kontragent.equals("84905")//
						|| kontragent.equals("84605")//
						|| kontragent.equals("85630")//
						|| kontragent.equals("85631")//
						|| kontragent.equals("85632")//
						|| kontragent.equals("83553")//
				) {
					if (cnt > 3) {
						Auxiliary.inform("У контрагента " + kontragent + " больше 3 визитов в неделю.", this);
						return false;
					}
				}
				else {
					Auxiliary.inform("У контрагента " + kontragent + " больше 2 визитов в неделю.", this);
					return false;
				}
			}
		}
		return true;
	}*/
	void composeGUI() {
		//feelTerritories();
		oddEven = new RedactSingleChoice(this);
		terrRedactSingleChoice = new RedactSingleChoice(this);
		yearsRedactSingleChoice = new RedactSingleChoice(this);
		monthsRedactSingleChoice = new RedactSingleChoice(this);
		terrRedactSingleChoice.selection.is(territorySelection);
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			Bough row = Cfg.territory().children.get(i);
			String s = row.child("territory").value.property.value()// 
					+ " (" + row.child("hrc").value.property.value().trim()// 
					+ " / " + row.child("kod").value.property.value().trim()//
					+ ")";
			terrRedactSingleChoice.item(s);
		}
		for (int i = 0; i < 12; i++) {
			monthsRedactSingleChoice.item(monthName(i));
		}
		monthsRedactSingleChoice.selection.is(periodMonth);
		yearsRedactSingleChoice.item("2013");
		yearsRedactSingleChoice.item("2014");
		yearsRedactSingleChoice.item("2015");
		yearsRedactSingleChoice.item("2016");
		yearsRedactSingleChoice.item("2017");
		yearsRedactSingleChoice.item("2018");
		yearsRedactSingleChoice.item("2019");
		yearsRedactSingleChoice.item("2020");
		yearsRedactSingleChoice.item("2021");
		yearsRedactSingleChoice.item("2022");
		yearsRedactSingleChoice.item("2023");
		yearsRedactSingleChoice.item("2024");
		yearsRedactSingleChoice.item("2025");
		yearsRedactSingleChoice.selection.is(periodYear);
		updateHeader();
		layoutless//
				.child(new Decor(this).labelText.is("территория").labelAlignRightCenter().labelStyleMediumNormal()//
						.left().is(0 * Auxiliary.tapSize).top().is(0 * Auxiliary.tapSize).width().is(2 * Auxiliary.tapSize - 8).height().is(Auxiliary.tapSize)//						
				)//
				.child(terrRedactSingleChoice.hidden().is(filled)//
						.left().is(2 * Auxiliary.tapSize)
						.top().is(8 + 0 * Auxiliary.tapSize)
						.width().is(8 * Auxiliary.tapSize)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(terrName).labelAlignLeftCenter().hidden().is(filled.not())//
						.left().is(2 * Auxiliary.tapSize)
						.top().is(8 + 0 * Auxiliary.tapSize)
						.width().is(8 * Auxiliary.tapSize)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is("период").labelAlignRightCenter().labelStyleMediumNormal()//
						.left().is(9.5 * Auxiliary.tapSize)
						.top().is(0 * Auxiliary.tapSize)
						.width().is(2 * Auxiliary.tapSize - 8)
						.height().is(Auxiliary.tapSize)//
				)//
				.child(monthsRedactSingleChoice.hidden().is(filled)//
						.left().is(11.5 * Auxiliary.tapSize)
						.top().is(8 + 0 * Auxiliary.tapSize)
						.width().is(2 * Auxiliary.tapSize)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(yearsRedactSingleChoice.hidden().is(filled)//
						.left().is(13.5 * Auxiliary.tapSize)
						.top()						.is(8 + 0 * Auxiliary.tapSize)
						.width().is(1.5 * Auxiliary.tapSize)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(periodName).labelAlignLeftCenter().hidden().is(filled.not())//
						.left().is(11.5 * Auxiliary.tapSize)
						.top().is(8 + 0 * Auxiliary.tapSize)
						.width().is(9* Auxiliary.tapSize)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is("наценка, %").labelAlignLeftCenter().labelStyleMediumNormal()
						.left().is(15.2 * Auxiliary.tapSize)
						.top().is(0 * Auxiliary.tapSize)
						.width().is(1.8 * Auxiliary.tapSize - 8)
						.height().is(Auxiliary.tapSize)//
				)
				.child(new RedactNumber(this).number.is(nacenkaprocent)
						.left().is(17.0 * Auxiliary.tapSize)
						.top().is(0 * Auxiliary.tapSize)
						.width().is(1.5 * Auxiliary.tapSize - 8)
						.height().is(0.9*Auxiliary.tapSize)//
				)

				.child(oddEven.item("однонедельный").item("двухнедельный").selection.is(dvuhnedel)
						.hidden().is(filled)//
						.left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))
						.top().is(8 + 0 * Auxiliary.tapSize)
						.width().is(3 * Auxiliary.tapSize - 8)
						.height().is(0.8 * Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is(dvuhnedel.value() > 0 ? "двухнедельный" : "однонедельный").labelAlignRightCenter().labelStyleMediumNormal().hidden().is(filled.not())//
						.left().is(layoutless.width().property.minus(3 * Auxiliary.tapSize))
						.top().is(0 * Auxiliary.tapSize)
						.width().is(3 * Auxiliary.tapSize - 8)
						.height().is(Auxiliary.tapSize)//
				)//
		;
		gridMatrica = new DataGrid(this);
		mNN = new ColumnText();
		mKontragent = new ColumnText();
		mPn = new ColumnText();//.format.is("HH:mm");
		mVt = new ColumnText();//.format.is("HH:mm");
		mSr = new ColumnText();//.format.is("HH:mm");
		mCt = new ColumnText();//.format.is("HH:mm");
		mPt = new ColumnText();//.format.is("HH:mm");
		mSb = new ColumnText();//.format.is("HH:mm");
		
		/*
		mPn1 = new ColumnDate().format.is("HH:mm");
		mVt1 = new ColumnDate().format.is("HH:mm");
		mSr1 = new ColumnDate().format.is("HH:mm");
		mCt1 = new ColumnDate().format.is("HH:mm");
		mPt1 = new ColumnDate().format.is("HH:mm");
		mSb1 = new ColumnDate().format.is("HH:mm");
		*/


		mPotencial = new ColumnNumeric().format.is("#########0.00");
		mTOM1 = new ColumnNumeric().format.is("#########0.00");
		mTOM2 = new ColumnNumeric().format.is("#########0.00");
		mTOM3 = new ColumnNumeric().format.is("#########0.00");
		mTipOplati = new ColumnText();
		mPlan = new ColumnNumeric().format.is("#########0.00");
		//mNacenka = new ColumnNumeric().format.is("#########0.00'%'");
		gridMatrica//
				.noFoot.is(false)//
				.columns(new Column[]{ //
						//
						mNN.title.is("№").width.is(0.5 * Auxiliary.tapSize)//
						, mKontragent.title.is("Контрагент").footer.is(footOsnov).width.is(6 * Auxiliary.tapSize)//
						, mTipOplati.title.is("Тип оплаты").width.is(1 * Auxiliary.tapSize)//


						, mPn.title.is(dayName(0)).footer.is(footPn).width.is(1.1 * Auxiliary.tapSize)//
						//, mPn1.title.is("ч."+dayName(0)).footer.is(footPn1.asNote()).width.is(1.1 * Auxiliary.tapSize)//
						, mVt.title.is(dayName(1)).footer.is(footVt).width.is(1.1 * Auxiliary.tapSize)//
						//, mVt1.title.is("ч."+dayName(1)).footer.is(footVt1.asNote()).width.is(1.1 * Auxiliary.tapSize)//
						, mSr.title.is(dayName(2)).footer.is(footSr).width.is(1.1 * Auxiliary.tapSize)//
						//, mSr1.title.is("ч."+dayName(2)).footer.is(footSr1.asNote()).width.is(1.1 * Auxiliary.tapSize)//
						, mCt.title.is(dayName(3)).footer.is(footCt).width.is(1.1 * Auxiliary.tapSize)//
						//, mCt1.title.is("ч."+dayName(3)).footer.is(footCt1.asNote()).width.is(1.1 * Auxiliary.tapSize)//
						, mPt.title.is(dayName(4)).footer.is(footPt).width.is(1.1 * Auxiliary.tapSize)//
						//, mPt1.title.is("ч."+dayName(4)).footer.is(footPt1.asNote()).width.is(1.1 * Auxiliary.tapSize)//
						, mSb.title.is(dayName(5)).footer.is(footSb).width.is(1.1 * Auxiliary.tapSize)//
						//, mSb1.title.is("ч."+dayName(5)).footer.is(footSb1.asNote()).width.is(1.1 * Auxiliary.tapSize)//


						, mPotencial.title.is("Потенциал ТТ").footer.is(footPotencial.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize)//
						, mTOM1.title.is(monthName(periodMonth.value().intValue() - 3)).footer.is(footTOM1.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize)//
						, mTOM2.title.is(monthName(periodMonth.value().intValue() - 2)).footer.is(footTOM2.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize)//
						, mTOM3.title.is(monthName(periodMonth.value().intValue() - 1)).footer.is(footTOM3.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize)//
						, mPlan.title.is("План, тыс.руб").footer.is(footPlan.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize) //
						//, mNacenka.title.is("Наценка").footer.is(footNacenka.asNote("########0.00")).width.is(1.2 * Auxiliary.tapSize) //
				});
		layoutless.child(gridMatrica//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
				.top().is(Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		Numeric split = new Numeric().value(Auxiliary.screenWidth(this));
		layoutless.child(new Decor(this)//
				.background.is(Auxiliary.colorBackground)//
				.left().is(split)//
				.width().is(Auxiliary.screenWidth(this))//
				.height().is(Auxiliary.screenHeight(this))//
		);
		gridSvod = new DataGrid(this);
		//sNN = new ColumnText();
		sDenNedeli = new ColumnDate().format.is("EE");
		sData = new ColumnDate().format.is("dd.MM.yyyy");
		sPlanUtro = new ColumnNumeric().format.is("#########0.00");
		sPlanLetuchka = new ColumnNumeric().format.is("#########0.00");
		sPlanItogo = new ColumnNumeric().format.is("#########0.00");
		sPlanNarItog = new ColumnNumeric().format.is("#########0.00");
		layoutless.child(new SplitLeftRight(this)//
				.split.is(split)//
				.rightSide(gridSvod.noFoot.is(false)//
						.columns(new Column[]{ //
								//
								sDenNedeli.title.is("День недели").width.is(1 * Auxiliary.tapSize) //
								, sData.title.is("Дата").width.is(2 * Auxiliary.tapSize) //
								, sPlanUtro.title.is("План утро").footer.is(footPlanUtro.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize) //
								, sPlanLetuchka.title.is("План летучка").footer.is(footPlanLetuchka.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize) //
								, sPlanItogo.title.is("План итого").footer.is(footPlanItogo.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize) //
								, sPlanNarItog.title.is("План нар. итог").footer.is(footPlanNar.asNote("########0.00")).width.is(1.5 * Auxiliary.tapSize) //
						}))//
				//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuFillInitial = menu.add("Заполнить маршрут");
		menuDeleteMatrica = menu.add("Удалить матрицу");
		menuNewKontragent = menu.add("Добавить контрагента");
		menuOtchety = menu.add("Отчёты");
		menuUpload = menu.add("Выгрузить маршрут");
		menuLock = menu.add("Заблокировать документ");
		menuUnlock = menu.add("Разблокировать документ");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuDeleteMatrica) {
			promptDropMatrica();
			return true;
		}
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if (item == menuFillInitial) {
			promptInitialFill();
			return true;
		}
		if (item == menuNewKontragent) {
			promptAddKontragent();
			return true;
		}
		if (item == menuUpload) {
			promptUpload();
			return true;
		}
		if (item == menuLock) {
			setLock();
			return true;
		}
		if (item == menuUnlock) {
			setUnlock();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	String getKod() {
		int i = territorySelection.value().intValue();
		String kod = Cfg.territory().children.get(i).child("kod").value.property.value().trim();
		return kod;
	}

	void selectByKod(String kod) {
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			Bough row = Cfg.territory().children.get(i);
			String tkod = row.child("kod").value.property.value();
			if (tkod.trim().equals(kod.trim())) {
				territorySelection.value(i);
				break;
			}
		}
	}

	public String pad2(int n) {
		String r = "" + n;
		if (n < 10) {
			r = "0" + r;
		}
		return r;
	}

	void promptInitialFill() {
		Auxiliary.pickConfirm(this, "Заполнение матрицы. Вы уверены?", "Да, очистить и заполнить", new Task() {
			@Override
			public void doTask() {
				initialFillMatrica();
			}
		});
	}

	void checkFilled() {
		Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select filled from matricaX where _id=" + _id, null));
		if (Numeric.string2double(b.child("row").child("filled").value.property.value()) > 0) {
			filled.value(true);
		} else {
			filled.value(false);
		}
	}

	void initialFillMatrica() {
		String territoryKod = getKod();
		String periodString = "" + (2013 + periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		//final String url = "https://testservice.swlife.ru/tsybin_hrc/hs/MatricaTP/Zapolnit/" + territoryKod + "/" + periodString;

		//final String url = "https://testservice.swlife.ru/tsybin_hrc/hs/MatricaTP/Zapolnit/" + territoryKod + "/" + periodString;
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/MatricaTP/Zapolnit/" + territoryKod + "/" + periodString;
		System.out.println("url " + url);
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Подождите").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					Bough json = Bough.parseJSON(msg);
					json.name.is("data");
					b.child("result").value.is(msg);
					b.child("json").children.add(json);
				} catch (Throwable t) {
					t.printStackTrace();
					b.child("error").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("data: "+b.child("json").child("data"));
				insertInitialFill(b.child("json").child("data"), true);
				refillGrids();
				checkFilled();
			}
		});
		expect.start(ActivityMatricaEdit.this);
	}

	/*
		void initialFillMatricaOld() {
			String periodString = "" + (2013 + periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
			save();
			final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
					+ "<soap:Body>"//
					+ "<Zapolnit xmlns=\"http://ws.swl/Matrica\">"//
					+ "<Podr>" + getKod() + "</Podr>"//
					+ "<Period>" + periodString + "</Period>"//
					+ "<Kod></Kod>"//
					+ "<Dvuhnedel>" + dvuhnedel.value().intValue() + "</Dvuhnedel>"//
					+ "</Zapolnit>"//
					+ "</soap:Body>"//
					+ "</soap:Envelope>";
			System.out.println(xml);
			System.out.println(Settings.getInstance().getBaseURL() + "WebMatrica.1cws");
			final RawSOAP r = new RawSOAP().timeout.is(10 * 60 * 1000);
			new Expect().status.is("Выполнение...").task.is(new Task() {
				@Override
				public void doTask() {
					r.url.is(Settings.getInstance().getBaseURL() + "WebMatrica.1cws")//
							//r.url.is(Settings.getInstance().getBaseURL() + "WebMatricatest.1cws")//
							.xml.is(xml);
					Report_Base.startPing();
					r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					if (r.exception.property.value() != null) {
						Auxiliary.warn("Ошибка: " + r.exception.property.value().toString() + " / " + r.exception.property.value().getMessage(), ActivityMatricaEdit.this);
					} else {
						if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
							//System.out.println(r.data.dumpXML());
							insertInitialFill(r.data, true);
							refillGrids();
							checkFilled();
						} else {
							Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityMatricaEdit.this);
						}
					}
				}
			}).afterCancel.is(new Task() {
				@Override
				public void doTask() {
					//
				}
			}).start(this);
		}
	*/
	public static String dayName(int n) {
		if (n > 6) {
			n = n - 7;
		}
		if (n < 0) {
			n = n + 7;
		}
		if (n == 0) {
			return "Пн";
		}
		if (n == 1) {
			return "Вт";
		}
		if (n == 2) {
			return "Ср";
		}
		if (n == 3) {
			return "Чт";
		}
		if (n == 4) {
			return "Пт";
		}
		if (n == 5) {
			return "Сб";
		}
		if (n == 6) {
			return "Вс";
		}
		return "";
	}

	public static String monthName(int n) {
		if (n < 0) {
			n = n + 12;
		}
		if (n > 11) {
			n = n - 12;
		}
		if (n == 0) {
			return "Январь";
		}
		if (n == 1) {
			return "Февраль";
		}
		if (n == 2) {
			return "Март";
		}
		if (n == 3) {
			return "Апрель";
		}
		if (n == 4) {
			return "Май";
		}
		if (n == 5) {
			return "Июнь";
		}
		if (n == 6) {
			return "Июль";
		}
		if (n == 7) {
			return "Август";
		}
		if (n == 8) {
			return "Сентябрь";
		}
		if (n == 9) {
			return "Октябрь";
		}
		if (n == 10) {
			return "Ноябрь";
		}
		if (n == 11) {
			return "Декабрь";
		}
		return "?";
	}

	String getKontragentName(String kod) {
		return Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select Naimenovanie as name from kontragenty where kod=" + kod, null)).child("row").child("name").value.property.value();
	}

	String getOsnovnoyKlientTorgovoyTochki(String kod) {
		return Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select OsnovnoyKlientTorgovoyTochki as OsnovnoyKlientTorgovoyTochki from kontragenty where kod=" + kod, null)).child("row").child("OsnovnoyKlientTorgovoyTochki").value.property.value();
	}

	void insertInitialFill(Bough data, boolean ZapolnitResponse) {
		System.out.println("insertInitialFill " + data.dumpXML());
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaRowsX where matrica_id=" + _id);
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaSvodX where matrica_id=" + _id);
		Vector<Bough> rows;
		String sql;
		if (ZapolnitResponse) {
			//rows = data.child("soap:Body").child("m:ZapolnitResponse").child("m:return").child("m:Plan");
			rows = data.children("Plan");
			Calendar probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), 1);
			probe.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			int dayCount = probe.getActualMaximum(Calendar.DAY_OF_MONTH);
			for (int i = 1; i <= dayCount; i++) {
				probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), i + 1);
				sql = "insert into MatricaSvodX ("//
						+ "matrica_id,data,planUtro,planLetuchka,planItogo,planNarItog"//
						+ ")\n values\n ("//
						+ _id//
						+ "," + probe.getTime().getTime() + ",0,0,0,0"//
						+ ")";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		} else {
			rows = data.children("Plan");
			//rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").child("m:Plan");
			//Bough svod = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").child("m:Svod");
			Vector<Bough> svod = data.children("Svod");
			//System.out.println(svod.dumpXML());
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T00:00:00'");
			f.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			//for (int i = 0; i < svod.children.size(); i++) {
			for (int i = 0; i < svod.size(); i++) {
				//Bough row = svod.children.get(i);
				Bough row = svod.get(i);
				try {
					//f.parse("");
					sql = "insert into MatricaSvodX ("//
							+ "matrica_id,data,planUtro,planLetuchka"//
							+ ")\n values\n ("//
							+ _id //
							+ "," + f.parse(row.child("Data").value.property.value()).getTime() //
							+ "," + Numeric.string2double(row.child("PlanHoch").value.property.value())//
							+ "," + Numeric.string2double(row.child("PlanLet").value.property.value())//
							+ ")";
					//System.out.println(sql);
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		//System.out.println(rows.dumpXML());
		DateFormat from = new SimpleDateFormat("'0001-01-01T'kk:mm:ss");
		from.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		//for (int i = 0; i < rows.children.size(); i++) {
		for (int i = 0; i < rows.size(); i++) {
			//Bough row = rows.children.get(i);
			Bough row = rows.get(i);
			String kontragentID = "";
			try {
				double t1 = Numeric.string2double(row.child("TOM1").value.property.value());
				double t2 = Numeric.string2double(row.child("TOM2").value.property.value());
				double t3 = Numeric.string2double(row.child("TOM3").value.property.value());
				//int t4 = (int) ((t1 + t2 + t3) / 3);
				double t4 = Numeric.string2double(row.child("Plan").value.property.value());
				int nacenka = 7;
				if (!ZapolnitResponse) {
					nacenka = (int) Numeric.string2double(row.child("PlanNac").value.property.value());
				}
				sql = "insert into MatricaRowsX ("//
						+ "matrica_id,udalit,kontragent,tipTT,tipOplaty,pn,vt,sr,ct,pt,sb,pn1,vt1,sr1,ct1,pt1,sb1,potencialTT,tom1,tom2,tom3,vdm1,vdm2,vdm3,planTysRub"//
						+ ",nacenka,uploaded,vrrab,email"//
						+ ")\n values\n ("//
						+ _id//
						+ ",0"//
						+ "," + row.child("Kontragent").value.property.value()//
						+ ",''"//
						+ ",'" + row.child("TipOplati").value.property.value() + "'"//
						+ "," + from.parse(row.child("Pn").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Vt").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Sr").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Ct").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Pt").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Sb").value.property.value()).getTime()//


						+ "," + from.parse(row.child("Pn1").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Vt1").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Sr1").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Ct1").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Pt1").value.property.value()).getTime()//
						+ "," + from.parse(row.child("Sb1").value.property.value()).getTime()//


						+ "," + Numeric.string2double(row.child("Potencial").value.property.value())//
						+ "," + Numeric.string2double(row.child("TOM1").value.property.value())//
						+ "," + Numeric.string2double(row.child("TOM2").value.property.value())//
						+ "," + Numeric.string2double(row.child("TOM3").value.property.value())//
						+ "," + Numeric.string2double(row.child("VDM1").value.property.value())//
						+ "," + Numeric.string2double(row.child("VDM2").value.property.value())//
						+ "," + Numeric.string2double(row.child("VDM3").value.property.value())//
						+ "," + t4//
						+ "," + nacenka//
						+ ",1"//
						+ ",'" + row.child("VRRab").value.property.value() + "'"//
						+ ",'" + row.child("Email").value.property.value() + "'"//
						//+ ",'" +dvuhnedel.value()+ "'"//
						+ ")";
				//System.out.println(sql);
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		ApplicationHoreca.getInstance().getDataBase().execSQL("update matricaX set filled=1 where _id=" + _id);
		ApplicationHoreca.getInstance().getDataBase().execSQL("update matricaX set nomer='"+data.child("Nomer").value.property.value()+"' where _id=" + _id);
		save();
		filled.value(true);
	}

	/*
		void insertInitialFillOld(Bough data, boolean ZapolnitResponse) {
			System.out.println(data.dumpXML());
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaRowsX where matrica_id=" + _id);
			ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaSvodX where matrica_id=" + _id);
			Bough rows;
			String sql;
			if (ZapolnitResponse) {
				rows = data.child("soap:Body").child("m:ZapolnitResponse").child("m:return").child("m:Plan");
				Calendar probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), 1);
				probe.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				int dayCount = probe.getActualMaximum(Calendar.DAY_OF_MONTH);
				for (int i = 1; i <= dayCount; i++) {
					probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), i + 1);
					sql = "insert into MatricaSvodX ("//
							+ "matrica_id,data,planUtro,planLetuchka,planItogo,planNarItog"//
							+ ")\n values\n ("//
							+ _id//
							+ "," + probe.getTime().getTime() + ",0,0,0,0"//
							+ ")";
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				}
			} else {
				rows = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").child("m:Plan");
				Bough svod = data.child("soap:Body").child("m:PerechitatResponse").child("m:return").child("m:Svod");
				//System.out.println(svod.dumpXML());
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T00:00:00'");
				f.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				for (int i = 0; i < svod.children.size(); i++) {
					Bough row = svod.children.get(i);
					try {
						//f.parse("");
						sql = "insert into MatricaSvodX ("//
								+ "matrica_id,data,planUtro,planLetuchka"//
								+ ")\n values\n ("//
								+ _id //
								+ "," + f.parse(row.child("m:Data").value.property.value()).getTime() //
								+ "," + Numeric.string2double(row.child("m:PlanHoch").value.property.value())//
								+ "," + Numeric.string2double(row.child("m:PlanLet").value.property.value())//
								+ ")";
						//System.out.println(sql);
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
			//System.out.println(rows.dumpXML());
			DateFormat from = new SimpleDateFormat("'0001-01-01T'kk:mm:ss");
			from.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			for (int i = 0; i < rows.children.size(); i++) {
				Bough row = rows.children.get(i);
				String kontragentID = "";
				try {
					double t1 = Numeric.string2double(row.child("m:TOM1").value.property.value());
					double t2 = Numeric.string2double(row.child("m:TOM2").value.property.value());
					double t3 = Numeric.string2double(row.child("m:TOM3").value.property.value());
					//int t4 = (int) ((t1 + t2 + t3) / 3);
					double t4 = Numeric.string2double(row.child("m:Plan").value.property.value());
					int nacenka = 7;
					if (!ZapolnitResponse) {
						nacenka = (int) Numeric.string2double(row.child("m:PlanNac").value.property.value());
					}
					sql = "insert into MatricaRowsX ("//
							+ "matrica_id,udalit,kontragent,tipTT,tipOplaty,pn,vt,sr,ct,pt,sb,pn1,vt1,sr1,ct1,pt1,sb1,potencialTT,tom1,tom2,tom3,vdm1,vdm2,vdm3,planTysRub"//
							+ ",nacenka,uploaded,vrrab,email"//
							+ ")\n values\n ("//
							+ _id//
							+ ",0"//
							+ "," + row.child("m:Kontragent").value.property.value()//
							+ ",''"//
							+ ",'" + row.child("m:TipOplati").value.property.value() + "'"//
							+ "," + from.parse(row.child("m:Pn").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Vt").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Sr").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Ct").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Pt").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Sb").value.property.value()).getTime()//


							+ "," + from.parse(row.child("m:Pn1").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Vt1").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Sr1").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Ct1").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Pt1").value.property.value()).getTime()//
							+ "," + from.parse(row.child("m:Sb1").value.property.value()).getTime()//


							+ "," + Numeric.string2double(row.child("m:Potencial").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:TOM1").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:TOM2").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:TOM3").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:VDM1").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:VDM2").value.property.value())//
							+ "," + Numeric.string2double(row.child("m:VDM3").value.property.value())//
							+ "," + t4//
							+ "," + nacenka//
							+ ",1"//
							+ ",'" + row.child("m:VRRab").value.property.value() + "'"//
							+ ",'" + row.child("m:Email").value.property.value() + "'"//
							//+ ",'" +dvuhnedel.value()+ "'"//
							+ ")";
					//System.out.println(sql);
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			ApplicationHoreca.getInstance().getDataBase().execSQL("update matricaX set filled=1 where _id=" + _id);
			save();
			filled.value(true);
		}
	*/
	void requeryData() {
		//System.out.println("requeryData start");
		String sql = "select"//
				+ " m._id, m.udalit, m.kontragent, m.tipTT, m.nacenka, m.tipOplaty"//
				+ ", m.pn as pn  , m.vt as vt  , m.sr as sr  , m.ct as ct  , m.pt as pt  , m.sb as sb "//
				+ ", m.pn1 as pn1, m.vt1 as vt1, m.sr1 as sr1, m.ct1 as ct1, m.pt1 as pt1, m.sb1 as sb1"//
				+ ", m.potencialTT"//
				+ ", m.tom1, m.tom2, m.tom3, m.vdm1, m.vdm2, m.vdm3, m.planTysRub"//
				+ ", m.nacenka,m.uploaded"//
				+ ", m.vrrab,m.email"//
				//+ ", m.dvuhnedel"//
				+ " from MatricaRowsX m"//
				+ " join kontragenty k on m.kontragent=k.kod "//
				+ " where m.matrica_id=" + _id//
				+ " order by k.naimenovanie";
		currentMatrica = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(currentMatrica.dumpXML());
		sql = "select _id,data,planUtro,planLetuchka,planItogo,planNarItog from MatricaSvodX where matrica_id=" + _id + " order by data";
		currentSvod = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//sql = "select dataZagruzkiMarshruta from matricaX where _id=" + _id;
		//06-07 17:57:30.261: I/System.out(537): update Matrica set dataZagruzkiMarshruta=518882838 where _id=4
		//Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//String s = b.child("dataZagruzkiMarshruta").value.property.value();
		//dataZagruzkiMarshruta = (int) Numeric.string2double(s);
		//System.out.println("requeryData done");
	}

	int extractTimeMills(String dateMills) {
		return extractTimeMills(Numeric.string2double(dateMills));
	}

	int extractTimeMills(double dateMills) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		c.setTimeInMillis((long) dateMills);
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		c.setTimeInMillis(0);
		c.set(Calendar.HOUR_OF_DAY, hours);
		c.set(Calendar.MINUTE, minutes);
		return (int) c.getTimeInMillis();
	}

	String formatTime(String m, String m1) {
		double n = Numeric.string2double(m);
		double n1 = Numeric.string2double(m1);
		String s = timeFormat.format(new Date((long) n));
		String s1 = timeFormat.format(new Date((long) n1));
		if (n == 0) {
			s = "     ";
		}
		if (n1 == 0) {
			s1 = "     ";
		}
		if (dvuhnedel.value() > 0) {
			return s + " `" + s1;
		} else {
			return s;
		}
	}

	void fillMatricaGrid() {
		//System.out.println("fillMatricaGrid start");
		avgPn.value(0);
		avgVt.value(0);
		avgSr.value(0);
		avgCt.value(0);
		avgPt.value(0);
		avgSb.value(0);


		avgPn1.value(0);
		avgVt1.value(0);
		avgSr1.value(0);
		avgCt1.value(0);
		avgPt1.value(0);
		avgSb1.value(0);


		footPotencial.value(0);
		footPlan.value(0);
		footTOM1.value(0);
		footTOM2.value(0);
		footTOM3.value(0);
		DateFormat to = new SimpleDateFormat("kk:mm");
		DateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
		Vector<String> parents = new Vector<String>();
		double sumVal = 0;
		double sumTov = 0;
		//double sumNac = 0;
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			double nacenka = Numeric.string2double(row.child("nacenka").value.property.value());
			double planTysRub = Numeric.string2double(row.child("planTysRub").value.property.value());
			if (planTysRub > 0) {
				double val = planTysRub * nacenka / 100;
				sumVal = sumVal + val;
				sumTov = sumTov + planTysRub;
			}
			final String _id = row.child("_id").value.property.value();
			Task tapMatricaRow = new Task() {
				@Override
				public void doTask() {
					tapMatricaRow(_id);
				}
			};
			try {
				String osnovnoyKlientTorgovoyTochki = getOsnovnoyKlientTorgovoyTochki(row.child("kontragent").value.property.value());
				boolean notFound = true;
				for (int p = 0; p < parents.size(); p++) {
					if (parents.get(p).equals(osnovnoyKlientTorgovoyTochki.trim())) {
						notFound = false;
						break;
					}
				}
				if (notFound || osnovnoyKlientTorgovoyTochki.trim().length() == 0) {
					parents.add(osnovnoyKlientTorgovoyTochki.trim());
				}
				int bg = Auxiliary.colorSelection;
				if (Numeric.string2double(row.child("uploaded").value.property.value()) > 0) {
					bg = 0x00000000;
				}
				mNN.cell("" + (i + 1), bg, tapMatricaRow);
				mKontragent.cell(row.child("kontragent").value.property.value() + ": " + getKontragentName(row.child("kontragent").value.property.value())//
						, bg, tapMatricaRow);
			/*
				if(dvuhnedel.value()>0){
					mPn.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("pn").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("pn1").value.property.value())))
							, bg, tapMatricaRow);
					mVt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("vt").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("vt").value.property.value())))
							, bg, tapMatricaRow);
					mSr.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("sr").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("sr").value.property.value())))
							, bg, tapMatricaRow);
					mCt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("ct").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("ct").value.property.value())))
							, bg, tapMatricaRow);
					mPt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("pt").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("pt").value.property.value())))
							, bg, tapMatricaRow);
					mSb.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("sb").value.property.value())))
							+" "+timeFormat.format(new Date((long) Numeric.string2double(row.child("sb").value.property.value())))
							, bg, tapMatricaRow);
				}else{
					mPn.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("pn").value.property.value()))), bg, tapMatricaRow);
					mVt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("vt").value.property.value()))), bg, tapMatricaRow);
					mSr.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("sr").value.property.value()))), bg, tapMatricaRow);
					mCt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("ct").value.property.value()))), bg, tapMatricaRow);
					mPt.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("pt").value.property.value()))), bg, tapMatricaRow);
					mSb.cell(timeFormat.format(new Date((long) Numeric.string2double(row.child("sb").value.property.value()))), bg, tapMatricaRow);
				}*/
				mPn.cell(formatTime(row.child("pn").value.property.value(), row.child("pn1").value.property.value()), bg, tapMatricaRow);
				mVt.cell(formatTime(row.child("vt").value.property.value(), row.child("vt1").value.property.value()), bg, tapMatricaRow);
				mSr.cell(formatTime(row.child("sr").value.property.value(), row.child("sr1").value.property.value()), bg, tapMatricaRow);
				mCt.cell(formatTime(row.child("ct").value.property.value(), row.child("ct1").value.property.value()), bg, tapMatricaRow);
				mPt.cell(formatTime(row.child("pt").value.property.value(), row.child("pt1").value.property.value()), bg, tapMatricaRow);
				mSb.cell(formatTime(row.child("sb").value.property.value(), row.child("sb1").value.property.value()), bg, tapMatricaRow);
				/*
				mPn.cell((long) Numeric.string2double(row.child("pn").value.property.value()), bg, tapMatricaRow);
				mVt.cell((long) Numeric.string2double(row.child("vt").value.property.value()), bg, tapMatricaRow);
				mSr.cell((long) Numeric.string2double(row.child("sr").value.property.value()), bg, tapMatricaRow);
				mCt.cell((long) Numeric.string2double(row.child("ct").value.property.value()), bg, tapMatricaRow);
				mPt.cell((long) Numeric.string2double(row.child("pt").value.property.value()), bg, tapMatricaRow);
				mSb.cell((long) Numeric.string2double(row.child("sb").value.property.value()), bg, tapMatricaRow);
				*/
				
				/*
				mPn1.cell((long) Numeric.string2double(row.child("pn1").value.property.value()), bg, tapMatricaRow);
				mVt1.cell((long) Numeric.string2double(row.child("vt1").value.property.value()), bg, tapMatricaRow);
				mSr1.cell((long) Numeric.string2double(row.child("sr1").value.property.value()), bg, tapMatricaRow);
				mCt1.cell((long) Numeric.string2double(row.child("ct1").value.property.value()), bg, tapMatricaRow);
				mPt1.cell((long) Numeric.string2double(row.child("pt1").value.property.value()), bg, tapMatricaRow);
				mSb1.cell((long) Numeric.string2double(row.child("sb1").value.property.value()), bg, tapMatricaRow);
				*/

				mPotencial.cell(Numeric.string2double(row.child("potencialTT").value.property.value()), bg, tapMatricaRow);
				mTOM1.cell(Numeric.string2double(row.child("tom1").value.property.value()), bg, tapMatricaRow);
				mTOM2.cell(Numeric.string2double(row.child("tom2").value.property.value()), bg, tapMatricaRow);
				mTOM3.cell(Numeric.string2double(row.child("tom3").value.property.value()), bg, tapMatricaRow);
				mTipOplati.cell(row.child("tipOplaty").value.property.value(), bg, tapMatricaRow);
				mPlan.cell(Numeric.string2double(row.child("planTysRub").value.property.value()), bg, tapMatricaRow);
				//mNacenka.cell(Numeric.string2double(row.child("nacenka").value.property.value()), bg, tapMatricaRow);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			//System.out.println(extractTimeMills(row.child("pn").value.property.value()));
			//if (Numeric.string2double(row.child("pn").value.property.value()) > 0) {
			if (extractTimeMills(row.child("pn").value.property.value()) > 0) {
				avgPn.value(avgPn.value() + 1);
			}
			if (extractTimeMills(row.child("vt").value.property.value()) > 0) {
				avgVt.value(avgVt.value() + 1);
			}
			if (extractTimeMills(row.child("sr").value.property.value()) > 0) {
				avgSr.value(avgSr.value() + 1);
			}
			if (extractTimeMills(row.child("ct").value.property.value()) > 0) {
				avgCt.value(avgCt.value() + 1);
			}
			if (extractTimeMills(row.child("pt").value.property.value()) > 0) {
				avgPt.value(avgPt.value() + 1);
			}
			if (extractTimeMills(row.child("sb").value.property.value()) > 0) {
				avgSb.value(avgSb.value() + 1);
			}


			if (extractTimeMills(row.child("pn1").value.property.value()) > 0) {
				avgPn1.value(avgPn1.value() + 1);
			}
			if (extractTimeMills(row.child("vt1").value.property.value()) > 0) {
				avgVt1.value(avgVt1.value() + 1);
			}
			if (extractTimeMills(row.child("sr1").value.property.value()) > 0) {
				avgSr1.value(avgSr1.value() + 1);
			}
			if (extractTimeMills(row.child("ct1").value.property.value()) > 0) {
				avgCt1.value(avgCt1.value() + 1);
			}
			if (extractTimeMills(row.child("pt1").value.property.value()) > 0) {
				avgPt1.value(avgPt1.value() + 1);
			}
			if (extractTimeMills(row.child("sb1").value.property.value()) > 0) {
				avgSb1.value(avgSb1.value() + 1);
			}


			footPotencial.value(footPotencial.value() + Numeric.string2double(row.child("potencialTT").value.property.value()));
			footTOM1.value(footTOM1.value() + Numeric.string2double(row.child("tom1").value.property.value()));
			footTOM2.value(footTOM2.value() + Numeric.string2double(row.child("tom2").value.property.value()));
			footTOM3.value(footTOM3.value() + Numeric.string2double(row.child("tom3").value.property.value()));
			footPlan.value(footPlan.value() + Numeric.string2double(row.child("planTysRub").value.property.value()));
		}
		if (dvuhnedel.value() > 0) {
			footPn.value(avgPn.value().intValue() + "`" + avgPn1.value().intValue());
			footVt.value(avgVt.value().intValue() + "`" + avgVt1.value().intValue());
			footSr.value(avgSr.value().intValue() + "`" + avgSr1.value().intValue());
			footCt.value(avgCt.value().intValue() + "`" + avgCt1.value().intValue());
			footPt.value(avgPt.value().intValue() + "`" + avgPt1.value().intValue());
			footSb.value(avgSb.value().intValue() + "`" + avgSb1.value().intValue());
		} else {
			footPn.value("" + avgPn.value().intValue());
			footVt.value("" + avgVt.value().intValue());
			footSr.value("" + avgSr.value().intValue());
			footCt.value("" + avgCt.value().intValue());
			footPt.value("" + avgPt.value().intValue());
			footSb.value("" + avgSb.value().intValue());
		}
		if (sumTov > 0) {
			footNacenka.value(100 * sumVal / sumTov);
		} else {
			footNacenka.value(0);
		}
		footOsnov.value("Количество основных клиентов торговой точки: " + parents.size());
		//System.out.println("fillMatricaGrid done");
	}

	void fillSvodGrid() {
		double narItog = 0;
		double utro = 0;
		double letuchka = 0;
		double itog = 0;
		footPlanUtro.value(0);
		footPlanLetuchka.value(0);
		footPlanItogo.value(0);
		footPlanNar.value(0);
		for (int i = 0; i < currentSvod.children.size(); i++) {
			Bough row = currentSvod.children.get(i);
			final String _id = row.child("_id").value.property.value();
			Task tapSvodRow = new Task() {
				@Override
				public void doTask() {
					tapSvodRow(_id);
				}
			};
			sDenNedeli.cell((long) Numeric.string2double(row.child("data").value.property.value()), tapSvodRow);
			sData.cell((long) Numeric.string2double(row.child("data").value.property.value()), tapSvodRow);
			utro = Numeric.string2double(row.child("planUtro").value.property.value());
			sPlanUtro.cell(utro, tapSvodRow);
			letuchka = Numeric.string2double(row.child("planLetuchka").value.property.value());
			sPlanLetuchka.cell(letuchka, tapSvodRow);
			itog = utro + letuchka;
			sPlanItogo.cell(itog, tapSvodRow);
			narItog = narItog + itog;
			sPlanNarItog.cell(narItog, tapSvodRow);
			footPlanUtro.value(footPlanUtro.value() + utro);
			footPlanLetuchka.value(footPlanLetuchka.value() + letuchka);
			footPlanItogo.value(footPlanItogo.value() + itog);
			footPlanNar.value(narItog);
		}
	}

	void refillGrids() {
		//System.out.println("refillGrids");
		requeryData();
		gridMatrica.clearColumns();
		fillMatricaGrid();
		gridMatrica.refresh();
		gridSvod.clearColumns();
		fillSvodGrid();
		gridSvod.refresh();
	}

	/*void _initalFill(Bough data) {
		gridMatrica.clearColumns();
		Bough rows = data.child("soap:Body").child("m:ZapolnitResponse").child("m:return").child("m:Plan");
		footPn.value(0);
		footVt.value(0);
		footSr.value(0);
		footCt.value(0);
		footPt.value(0);
		footSb.value(0);
		footPotencial.value(0);
		footTOM1.value(0);
		footTOM2.value(0);
		footTOM3.value(0);
		DateFormat to = new SimpleDateFormat("kk:mm");
		DateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
		DecimalFormat decimalFormat = new DecimalFormat("#########0.00");
		Vector<String> parents = new Vector<String>();
		for (int i = 0; i < rows.children.size(); i++) {
			Bough row = rows.children.get(i);
			Task tapMatricaRow = new Task() {
				@Override
				public void doTask() {
					tapMatricaRow("");
				}
			};
			try {
				String osnovnoyKlientTorgovoyTochki = getOsnovnoyKlientTorgovoyTochki(row.child("m:Kontragent").value.property.value());
				boolean notFound = true;
				for (int p = 0; p < parents.size(); p++) {
					if (parents.get(p).equals(osnovnoyKlientTorgovoyTochki.trim())) {
						notFound = false;
						break;
					}
				}
				if (notFound || osnovnoyKlientTorgovoyTochki.trim().length() == 0) {
					parents.add(osnovnoyKlientTorgovoyTochki.trim());
				}
				mNN.cell("" + (i + 1), tapMatricaRow);
				mKontragent.cell(row.child("m:Kontragent").value.property.value()//
						+ ": " + getKontragentName(row.child("m:Kontragent").value.property.value())//
				, tapMatricaRow);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
		footOsnov.value("Количество основных клиентов торговой точки: " + parents.size());
		gridMatrica.refresh();
		gridSvod.clearColumns();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Calendar probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), 1);
		int dayCount = probe.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dn = 0;
		for (int i = 0; i < dayCount; i++) {
			Task tapSvodRow = new Task() {
				@Override
				public void doTask() {
					tapSvodRow("");
				}
			};
			probe = new GregorianCalendar(2013 + periodYear.value().intValue(), periodMonth.value().intValue(), i + 1);
			dn++;
			if (dn > 6) {
				dn = 0;
			}
		}
		gridSvod.refresh();
	}*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refillGrids();
	}

	@Override
	public void onBackPressed() {
		if (!filled.value()) {
			save();
		}
		super.onBackPressed();
	}

	void save() {
		//checkFilled();
		Date d = new Date(periodYear.value().intValue() + 113, periodMonth.value().intValue(), 1);
		String sql = "update MatricaX set periodDeystvia=" + (d.getTime() - d.getTimezoneOffset() * 60 * 1000)//
				+ ", kod='" + getKod() + "'"//
				+ ", dvuhnedel='" + dvuhnedel.value() + "'"//
				+ ", nacenka=" + nacenkaprocent.value() + ""//
				+ " where _id=" + _id;
		//System.out.println(d);
		System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	void promptDropMatrica() {
		Auxiliary.pickConfirm(this, "Удаление матрицы. Вы уверены?", "Да, удалить", new Task() {
			@Override
			public void doTask() {
				dropMatrica();
			}
		});
	}

	void promptUpload() {
		/*if (!validVisitCount()) {
			return;
		}*/


		if (!valid9morning()) {
			return;
		}
		if (!validTime()) {
			return;
		}
		/*if (!validNacenka()) {
			return;
		}*/
		if (!validEquality()) {
			return;
		}


		//testMatrica();
		Auxiliary.pickString(this, "Комментарий", kommentariy, "Выгрузить", new Task() {
			//Auxiliary.pickConfirm(this, "Выгрузить маршрут. Вы уверены?", "Да, выгрузить", new Task() {
			@Override
			public void doTask() {
				uploadMatricaNew();
			}
		});
	}

	void promptAddKontragent() {
		String sql = "select kontragenty.kod as kod,kontragenty.naimenovanie as naimenovanie from kontragenty"//
				+ " left join DogovoryKontragentov on DogovoryKontragentov.vladelec=kontragenty._idrref"//
				+ " left join Podrazdeleniya on Podrazdeleniya._idrref=DogovoryKontragentov.podrazdelenie"//
				//+ " where Podrazdeleniya.kod='" + getKod() + "'"//
				+ " group by kontragenty.kod"//
				+ " order by kontragenty.naimenovanie";
		final Bough bough = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		//System.out.println(sql);
		//System.out.println(bough.dumpXML());
		Vector<String> names = new Vector<String>();
		final Vector<String> kods = new Vector<String>();
		for (int i = 0; i < bough.children.size(); i++) {
			boolean exists = false;
			Bough row = bough.children.get(i);
			String curKod = row.child("kod").value.property.value();
			//System.out.println("curKod "+curKod);
			for (int k = 0; k < currentMatrica.children.size(); k++) {
				String matricaKod = currentMatrica.children.get(k).child("kontragent").value.property.value();
				//System.out.println("matricaKod "+matricaKod);
				if (curKod.equals(matricaKod)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				names.add(bough.children.get(i).child("kod").value.property.value() + ": " + bough.children.get(i).child("naimenovanie").value.property.value());
				kods.add(bough.children.get(i).child("kod").value.property.value());
			}
		}
		//System.out.println(this.getKod());
		final Numeric sel = new Numeric();
		Auxiliary.pickFilteredChoice(ActivityMatricaEdit.this, names.toArray(new String[0]), sel//
				, new Task() {
					@Override
					public void doTask() {
						String kod = kods.get(sel.value().intValue());
						String sql = "insert into MatricaRowsX ("//
								+ "matrica_id,udalit,kontragent,tipTT,tipOplaty,pn,vt,sr,ct,pt,sb,potencialTT,tom1,tom2,tom3,vdm1,vdm2,vdm3,planTysRub"//
								+ ",nacenka"//
								+ ")\n values\n ("//
								+ _id// 
								+ ",0"// 
								+ "," + kod// 
								+ ",''"//
								+ ",''"//
								+ ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ",0" + ")";
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						refillGrids();
					}
				});
	}

	void promptReRead(final Bough redata) {
		Auxiliary.pickConfirm(this, "Матрица на сервере недавно редактировалась", "Перечитать", new Task() {
			@Override
			public void doTask() {
				//System.out.println(redata.dumpXML());
				insertInitialFill(redata, false);
				refillGrids();
				checkFilled();
			}
		});
	}

	void testMatrica() {
		System.out.println("testMatrica");
		String territoryKod = getKod();
		String periodString = "" + (2013 + periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		//SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date dataUploadDate=new Date(dataUpload);
		System.out.println(dataUploadDate);
		String d = f.format(dataUploadDate);
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/MatricaTP/Perechitat/" + territoryKod + "/" + periodString + "/" + d;
		//http://testservice.swlife.ru/tsybin_hrc/hs/MatricaTP/Perechitat/х0639/20220901/2021-10-18T12:05:01
		System.out.println("testMatrica url " + url);
		final Bough b = new Bough();
		Expect expect = new Expect().status.is("Проверка").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					System.out.println("testMatrica msg " + msg);
					Bough json = Bough.parseJSON(msg);
					json.name.is("data");
					b.child("result").value.is(msg);
					b.child("json").children.add(json);
				} catch (Throwable t) {
					t.printStackTrace();
					b.child("error").value.is(t.toString());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("error: "+b.child("error").dumpXML());
				//System.out.println("json: "+b.child("json").child("data").dumpXML());

				String kommentarii = b.child("json").child("data").child("Kommentarii").value.property.value();
				System.out.println("Kommentarii: " + kommentarii);
				if (kommentarii.trim().equals("ок")) {
					//
				} else {
					promptReRead(b.child("json").child("data"));
				}
			}
		});
		expect.start(ActivityMatricaEdit.this);
	}

	/*
		void testMatricaOld() {
			String Podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
			String Period = yearsRedactSingleChoice.items.get(periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
			Calendar c = Calendar.getInstance();
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			String d = f.format(dataUpload);
			//System.out.println("testMatrica " + d);
			final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
					+ "<soap:Body>"//
					+ "<Perechitat xmlns=\"http://ws.swl/Matrica\">"//
					+ "\n<Podr>" + Podrazdelenie + "</Podr>"//
					+ "\n<Period>" + Period + "</Period>"//
					+ "\n<Data>" + d + "</Data>"//
					+ "\n</Perechitat>"//
					+ "</soap:Body>"//
					+ "</soap:Envelope>";
			//System.out.println(xml);
			final RawSOAP r = new RawSOAP();
			new Expect().status.is("Проверка...").task.is(new Task() {
				@Override
				public void doTask() {
					r.url.is(Settings.getInstance().getBaseURL() + "WebMatrica.1cws")//
							//r.url.is(Settings.getInstance().getBaseURL() + "WebMatricatest.1cws")//
							.xml.is(xml);
					Report_Base.startPing();
					r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					if (r.exception.property.value() != null) {
						Auxiliary.warn("Ошибка: " + r.exception.property.value().toString() + " / " + r.exception.property.value().getMessage(), ActivityMatricaEdit.this);
					} else {
						if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
							//System.out.println("r.data.dumpXML(): " + r.data.dumpXML());
							String kommentarii = r.data.child("soap:Body").child("m:PerechitatResponse").child("m:return").child("m:Kommentarii").value.property.value();
							if (kommentarii.trim().equals("ок")) {
								//Auxiliary.inform(kommentarii, ActivityMatricaEdit.this);
							} else {
								promptReRead(r.data);
								//Auxiliary.inform("Статус: " + kommentarii, ActivityMatricaEdit.this);
								//System.out.println("r.data.dumpXML(): " + r.data.dumpXML());
								//insertInitialFill(r.data, false);
								//refillGrids();
								//checkFilled();
							}
						} else {
							Auxiliary.warn("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityMatricaEdit.this);
						}
					}
				}
			}).afterCancel.is(new Task() {
				@Override
				public void doTask() {
					//
				}
			}).start(this);
		}
	*/
	void setLock(){
		final Note result = new Note();
		matricaLock(result, new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(result.value(), ActivityMatricaEdit.this);
			}
		});
	}
	void setUnlock(){
		final Note result = new Note();
		matricaUnlock(result, new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(result.value(), ActivityMatricaEdit.this);
			}
		});
	}
	void uploadMatricaNew() {
		final Note result = new Note();
		matricaLock(result, new Task() {
			@Override
			public void doTask() {
				//System.out.println("sending");
				matricaSend(result, new Task() {
					@Override
					public void doTask() {
						matricaUnlock(result, new Task() {
							@Override
							public void doTask() {
								Auxiliary.warn(result.value(), ActivityMatricaEdit.this);
							}
						});
					}
				});
			}
		});
	}

	void matricaLock(final Note note, Task nextStep) {
		//int i = territorySelection.value().intValue();
		//String kod = Cfg.territory().children.get(i).child("kod").value.property.value().trim();
		//String podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
		String sql = "select terr.kod as kod,terr.naimenovanie as name from Podrazdeleniya terr join Polzovateli usr on usr.podrazdelenie=terr._idrref where usr.kod='" + Cfg.whoCheckListOwner() + "';";
		//String podrazdelenie = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null)).child("row").child("kod").value.property.value();
		//String podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
		String Period = yearsRedactSingleChoice.items.get(periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		String podrazdelenie=getKod();
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/MatricaTP/ChangeStart/" + podrazdelenie + "/" + Period + "";
		new Expect().status.is("Подождите...").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					System.out.println("testMatrica msg " + msg);
					Bough json = Bough.parseJSON(msg);
					json.name.is("data");
					String Kommentarii = json.child("Kommentarii").value.property.value();
					if (Kommentarii.equals("ок")) {
							note.value(note.value() + "Документ заблокирован: " + Kommentarii + "\n");
					} else {
						note.value(note.value() + "Ошибка блокировки: " + Kommentarii + "\n");
					}
				} catch (Throwable t) {
					t.printStackTrace();
					note.value(note.value() + "Ошибка блокировки: " + t.getMessage() + "\n");
				}
			}
		}).afterDone.is(nextStep).start(ActivityMatricaEdit.this);
	}

	void matricaUnlock(final Note note, Task nextStep) {

		//note.value(note.value() + "unlocked");
		//nextStep.doTask();
		String sql = "select terr.kod as kod,terr.naimenovanie as name from Podrazdeleniya terr join Polzovateli usr on usr.podrazdelenie=terr._idrref where usr.kod='" + Cfg.whoCheckListOwner() + "';";
		//String podrazdelenie = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null)).child("row").child("kod").value.property.value();
		//String podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
		String podrazdelenie=getKod();
		String Period = yearsRedactSingleChoice.items.get(periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/MatricaTP/ChangeEnd/" + podrazdelenie + "/" + Period + "";
		new Expect().status.is("Подождите").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					String msg = new String(bytes, "UTF-8");
					System.out.println("testMatrica msg " + msg);
					Bough json = Bough.parseJSON(msg);
					json.name.is("data");
					String Kommentarii = json.child("Kommentarii").value.property.value();
					if (Kommentarii.equals("ок")) {
						note.value(note.value() + "Документ разблокирован: " + Kommentarii + "\n");
					} else {
						note.value(note.value() + "Ошибка разблокировки: " + Kommentarii + "\n");
					}
				} catch (Throwable t) {
					t.printStackTrace();
					note.value(note.value() + "Ошибка разблокировки: " + t.getMessage() + "\n");
				}
			}
		}).afterDone.is(nextStep).start(ActivityMatricaEdit.this);
	}

	void matricaSend(final Note note, Task nextStep) {
		//note.value(note.value() + "отправка"+ "\n");
		final Note response = new Note();
		//final String url = "http://testservice.swlife.ru/tsybin_hrc/hs/MatricaTP/Vigruzit"  ;

		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/MatricaTP/Vigruzit";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		//String Podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
		String Podrazdelenie =getKod();
		SimpleDateFormat visitFormat = new SimpleDateFormat("HHmm");
		visitFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		StringBuilder xml = new StringBuilder();
		xml.append("{");
		xml.append("\n			\"Podrazdelenie\":\"" + Podrazdelenie + "\"");
		xml.append("\n			,\"RaspredelitPlany\":" + false + "");
		xml.append("\n			,\"MatricaUtv\":\"" + false + "\"");
		xml.append("\n			,\"SvodUtv\":\"" + false + "\"");
		xml.append("\n			,\"Plan\":[");
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			if (!validVRRab(row.child("vrrab").value.property.value())) {
				Auxiliary.warn("Для контрагента " + row.child("kontragent").value.property.value() + " время работы должно быть в формате 09:30-20:30", this);
				return;
			}
			if (i > 0) xml.append(",");
			xml.append("\n				{"
					+ "\n						\"Kontragent\":" + row.child("kontragent").value.property.value() + ""
					+ "\n						,\"Pn\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pn").value.property.value()))) + "\""
					+ "\n						,\"Vt\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("vt").value.property.value()))) + "\""
					+ "\n						,\"Sr\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sr").value.property.value()))) + "\""
					+ "\n						,\"Ct\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("ct").value.property.value()))) + "\""
					+ "\n						,\"Pt\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pt").value.property.value()))) + "\""
					+ "\n						,\"Sb\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sb").value.property.value()))) + "\""
					+ "\n						,\"Potencial\":\"" + row.child("potencialTT").value.property.value() + "\""
					+ "\n						,\"TOM1\":\"" + row.child("tom1").value.property.value() + "\""
					+ "\n						,\"TOM2\":\"" + row.child("tom2").value.property.value() + "\""
					+ "\n						,\"TOM3\":\"" + row.child("tom3").value.property.value() + "\""
					+ "\n						,\"TipOplati\":\"" + row.child("tipOplaty").value.property.value() + "\""
					+ "\n						,\"Udalit\":\"false\""
					+ "\n						,\"SKUPlan\":0"

					+ "\n						,\"SKUPotencial\":0"

					+ "\n						,\"PnStrog\":\"false\""
					+ "\n						,\"VtStrog\":\"false\""
					+ "\n						,\"SrStrog\":\"false\""
					+ "\n						,\"CtStrog\":\"false\""
					+ "\n						,\"PtStrog\":\"false\""
					+ "\n						,\"SbStrog\":\"false\""
					+ "\n						,\"Prichina\":\"None\""
					+ "\n						,\"Plan\":\"" + row.child("planTysRub").value.property.value() + "\""
					+ "\n						,\"VDM1\":\"" + Numeric.string2double(row.child("vdm1").value.property.value()) + "\""
					+ "\n						,\"VDM2\":\"" + Numeric.string2double(row.child("vdm2").value.property.value()) + "\""
					+ "\n						,\"VDM3\":\"" + Numeric.string2double(row.child("vdm3").value.property.value()) + "\""
					+ "\n						,\"NacM1\":\"0\""
					+ "\n						,\"NacM2\":\"0\""
					+ "\n						,\"NacM3\":\"0\""
					+ "\n						,\"PlanNac\":\"" + Numeric.string2double(row.child("nacenka").value.property.value()) + "\""
					+ "\n						,\"VRRab\":\"" + row.child("vrrab").value.property.value() + "\""
					+ "\n						,\"Email\":\"" + row.child("email").value.property.value() + "\""
					+ "\n						,\"Pn1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pn1").value.property.value()))) + "\""
					+ "\n						,\"Vt1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("vt1").value.property.value()))) + "\""
					+ "\n						,\"Sr1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sr1").value.property.value()))) + "\""
					+ "\n						,\"Ct1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("ct1").value.property.value()))) + "\""
					+ "\n						,\"Pt1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pt1").value.property.value()))) + "\""
					+ "\n						,\"Sb1\":\"" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sb1").value.property.value()))) + "\""
					+ "\n						,\"PnStrog1\":\"false\""
					+ "\n						,\"VtStrog1\":\"false\""
					+ "\n						,\"SrStrog1\":\"false\""
					+ "\n						,\"CtStrog1\":\"false\""
					+ "\n						,\"PtStrog1\":\"false\""
					+ "\n						,\"SbStrog1\":\"false\""
					+ "\n					}");
		}
		xml.append("\n	],\"Svod\":[");
		for (int i = 0; i < currentSvod.children.size(); i++) {
			Bough row = currentSvod.children.get(i);
			Date date = new Date();
			date.setTime((long) Numeric.string2double(row.child("data").value.property.value()));
			String data = simpleDateFormat.format(date);
			if (i > 0) xml.append("\n		,");
			xml.append("\n					{"//
					+ "\n						\"Data\":\"" + data + "\""
					+ "\n						,\"PlanHoch\":\"" + row.child("planUtro").value.property.value() + "\""
					+ "\n						,\"PlanLet\":\"" + row.child("planLetuchka").value.property.value() + "\""
					+ "\n					}");
		}
		xml.append("\n		]");
		String Period = yearsRedactSingleChoice.items.get(periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		xml.append("\n				,\"Kommentarii\":\"" + kommentariy.value() + "\""
				+ "\n				,\"Period\":\"" + Period + "\""
				+ "\n				,\"PlanNac\":\"" + nacenkaprocent.value() + "\""
				+ "\n				,\"Dvuhnedel\":" + dvuhnedel.value().intValue() + "");
		xml.append("\n}");
		final String txt = xml.toString();
		System.out.println(txt);
		Auxiliary.writeTextToFile(new java.io.File("/sdcard/horeca/test.txt"),txt);
		Expect expect = new Expect().status.is("Отправка").task.is(new Task() {
			@Override
			public void doTask() {
				Bough result = Auxiliary.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());

				response.value(result.child("raw").value.property.value());
				String ok=Bough.parseJSON(result.child("raw").value.property.value()).child("Сообщение").value.property.value();
				note.value(note.value()+""+response.value()+"\n");
				System.out.println("result: "+ok);
				if(ok.equals("ок")){
					System.out.println("save state");
					Calendar c = Calendar.getInstance();
					c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
					c.add(Calendar.MINUTE,+5);
					dataUpload = c.getTimeInMillis();
					String sql = "update MatricaX set dataUpload=" + dataUpload							+ " where _id=" + _id;
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
					sql = "update MatricaRowsX set uploaded=1"							+ " where matrica_id=" + _id;
					System.out.println(sql);
					ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				}
			}

		/*}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.warn(response.value(), ActivityMatricaEdit.this);
			}
		});*/
		}).afterDone.is(nextStep);
		expect.start(this);
	}
/*
	void uploadMatricaOld() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String Podrazdelenie = Cfg.territory().children.get(territorySelection.value().intValue()).child("kod").value.property.value();
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" ?>" //
				+ "\n<S:Envelope xmlns:S=\"http://www.w3.org/2003/05/soap-envelope\">" //
				+ "\n	<S:Body>"// 
				+ "\n		<Vigruzit xmlns=\"http://ws.swl/Matrica\">"// 
				+ "\n			<Matrica>"// 
				+ "\n				<Podrazdelenie>" + Podrazdelenie + "</Podrazdelenie>");
		xml.append("\n				<Plan>");
		SimpleDateFormat visitFormat = new SimpleDateFormat("HHmm");
		//"dd.MM.yyyy HH:mm:ss");
		visitFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		for (int i = 0; i < currentMatrica.children.size(); i++) {
			Bough row = currentMatrica.children.get(i);
			if (!validVRRab(row.child("vrrab").value.property.value())) {
				Auxiliary.warn("Для контрагента " + row.child("kontragent").value.property.value() + " время работы должно быть в формате 09:30-20:30", this);
				return;
			}
			xml.append("\n					<StrPlan>"//
					+ "\n						<Kontragent>" + row.child("kontragent").value.property.value() + "</Kontragent>"//
					+ "\n						<Pn>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pn").value.property.value()))) + "</Pn>"//
					+ "\n						<Vt>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("vt").value.property.value()))) + "</Vt>"//
					+ "\n						<Sr>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sr").value.property.value()))) + "</Sr>"//
					+ "\n						<Ct>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("ct").value.property.value()))) + "</Ct>"//
					+ "\n						<Pt>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pt").value.property.value()))) + "</Pt>"//
					+ "\n						<Sb>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sb").value.property.value()))) + "</Sb>"//
					+ "\n						<Potencial>" + row.child("potencialTT").value.property.value() + "</Potencial>"//
					+ "\n						<TOM1>" + row.child("tom1").value.property.value() + "</TOM1>"//
					+ "\n						<TOM2>" + row.child("tom2").value.property.value() + "</TOM2>"//
					+ "\n						<TOM3>" + row.child("tom3").value.property.value() + "</TOM3>"//
					+ "\n						<TipOplati>" + row.child("tipOplaty").value.property.value() + "</TipOplati>"//
					+ "\n						<PnStrog>false</PnStrog>"//
					+ "\n						<VtStrog>false</VtStrog>"//
					+ "\n						<SrStrog>false</SrStrog>"//
					+ "\n						<CtStrog>false</CtStrog>"//
					+ "\n						<PtStrog>false</PtStrog>"//
					+ "\n						<SbStrog>false</SbStrog>"//
					+ "\n						<Plan>" + row.child("planTysRub").value.property.value() + "</Plan>"//
					+ "\n						<VDM1>" + Numeric.string2double(row.child("vdm1").value.property.value()) + "</VDM1>"//
					+ "\n						<VDM2>" + Numeric.string2double(row.child("vdm2").value.property.value()) + "</VDM2>"//
					+ "\n						<VDM3>" + Numeric.string2double(row.child("vdm3").value.property.value()) + "</VDM3>"//
					+ "\n						<NacM1>0</NacM1>"//
					+ "\n						<NacM2>0</NacM2>"//
					+ "\n						<NacM3>0</NacM3>"//
					+ "\n						<PlanNac>" + Numeric.string2double(row.child("nacenka").value.property.value()) + "</PlanNac>"//
					+ "\n						<VRRab>" + row.child("vrrab").value.property.value() + "</VRRab>"//
					+ "\n						<Email>" + row.child("email").value.property.value() + "</Email>"//


					+ "\n						<Pn1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pn1").value.property.value()))) + "</Pn1>"//00010101
					+ "\n						<Vt1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("vt1").value.property.value()))) + "</Vt1>"//
					+ "\n						<Sr1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sr1").value.property.value()))) + "</Sr1>"//
					+ "\n						<Ct1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("ct1").value.property.value()))) + "</Ct1>"//
					+ "\n						<Pt1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("pt1").value.property.value()))) + "</Pt1>"//
					+ "\n						<Sb1>" + visitFormat.format(new Date((long) Numeric.string2double(row.child("sb1").value.property.value()))) + "</Sb1>"//

					+ "\n						<PnStrog1>false</PnStrog1>"//
					+ "\n						<VtStrog1>false</VtStrog1>"//
					+ "\n						<SrStrog1>false</SrStrog1>"//
					+ "\n						<CtStrog1>false</CtStrog1>"//
					+ "\n						<PtStrog1>false</PtStrog1>"//
					+ "\n						<SbStrog1>false</SbStrog1>"//


					+ "\n					</StrPlan>");
		}
		xml.append("\n				</Plan>");
		xml.append("\n				<Svod>");
		for (int i = 0; i < currentSvod.children.size(); i++) {
			Bough row = currentSvod.children.get(i);
			Date date = new Date();
			date.setTime((long) Numeric.string2double(row.child("data").value.property.value()));
			String data = simpleDateFormat.format(date);
			xml.append("\n					<StrSvod>"//
					+ "\n						<Data>" + data + "</Data>"//
					+ "\n						<PlanHoch>" + row.child("planUtro").value.property.value() + "</PlanHoch>"//
					+ "\n						<PlanLet>" + row.child("planLetuchka").value.property.value() + "</PlanLet>"//
					+ "\n					</StrSvod>");
		}
		xml.append("\n				</Svod>");
		String Period = yearsRedactSingleChoice.items.get(periodYear.value().intValue()) + pad2(1 + periodMonth.value().intValue()) + "01";
		xml.append("\n				<Kommentarii>" + kommentariy.value() + "</Kommentarii>"//
				+ "\n				<Period>" + Period + "</Period>"//
				+ "\n				<Dvuhnedel>" + dvuhnedel.value().intValue() + "</Dvuhnedel>"//
				+ "\n			</Matrica>"//
				+ "\n		</Vigruzit>"//
				+ "\n	</S:Body>"//
				+ "\n</S:Envelope>");
		//System.out.println(xml.toString());
		final String request = xml.toString();
		System.out.println(request);
		final RawSOAP r = new RawSOAP();
		new Expect().status.is("Выполнение...").task.is(new Task() {
			@Override
			public void doTask() {
				r.url.is(Settings.getInstance().getBaseURL() + "WebMatrica.1cws")//
						//r.url.is(Settings.getInstance().getBaseURL() + "WebMatricatest.1cws")//
						.xml.is(request);
				Report_Base.startPing();

				r.startNow(Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				if (r.exception.property.value() != null) {
					Auxiliary.warn("Ошибка: " + r.exception.property.value().toString() + " / " + r.exception.property.value().getMessage(), ActivityMatricaEdit.this);
				} else {
					if (r.statusCode.property.value() >= 100 && r.statusCode.property.value() <= 300) {
						String rez = r.data.child("soap:Body").child("m:VigruzitResponse").child("m:return").value.property.value();
						//System.out.println(rez);
						//System.out.println(r.data.dumpXML());
						if (rez.trim().equals("ок")) {
							Auxiliary.warn(rez, ActivityMatricaEdit.this);
							//int cur = 0;
							Calendar c = Calendar.getInstance();
							c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
							dataUpload = c.getTimeInMillis();
							//System.out.println("set dataUpload = " + c.getTime());
							//System.out.println("dataUpload = " + dataUpload + " / " + c.getTimeInMillis());
							String sql = "update MatricaX set dataUpload=" + dataUpload//
									+ " where _id=" + _id;
							ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
							//System.out.println("Matrica "+Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery("select * from MatricaX where _id=" + _id, null)).dumpXML());
							sql = "update MatricaRowsX set uploaded=1"//
									+ " where matrica_id=" + _id;
							System.out.println(sql);
							ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
						} else {
							Auxiliary.warn("Ошибка: " + rez, ActivityMatricaEdit.this);
						}
					} else {
						Auxiliary.inform("Ошибка: " + r.statusCode.property.value() + ": " + r.statusDescription.property.value(), ActivityMatricaEdit.this);
					}
				}
			}
		}).afterCancel.is(new Task() {
			@Override
			public void doTask() {
				//
			}
		}).start(this);
	}
*/
	void dropMatrica() {
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaX where _id=" + _id);
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaRowsX where matrica_id=" + _id);
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from MatricaSvodX where matrica_id=" + _id);
		finish();
	}

	void tapMatricaRow(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMatricaKontragent.class);
		intent.putExtra("_id", "" + _id);
		intent.putExtra("dvuhnedel", dvuhnedel.value());
		this.startActivityForResult(intent, 0);
	}

	void tapSvodRow(String _id) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMatricaDay.class);
		intent.putExtra("_id", "" + _id);
		this.startActivityForResult(intent, 0);
	}
}