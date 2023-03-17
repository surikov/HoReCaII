package sweetlife.android10.supervisor;

import android.app.Activity;
import android.os.*;
import android.view.*;

import java.util.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

public class Activity_IzmenitMarshrutOne extends Activity {

	Layoutless layoutless;
	MenuItem menuUpload;
	MenuItem menuDelete;
	Numeric terrSel = new Numeric();
	String _id = null;
	Numeric marchrutDate = new Numeric();
	Note territoryKod = new Note();
	Note klientKod = new Note();
	String ponedelnik = "0";
	String vtornik = "0";
	String sreda = "0";
	String chetverg = "0";
	String pyatnisa = "0";
	String subbota = "0";
	RedactMultiChoice days;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuUpload = menu.add("Выгрузить");
		menuDelete = menu.add("Удалить");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuUpload) {
			this.doUpload();
			return true;
		}
		if (item == menuDelete) {
			this.promptDelete();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//System.out.println(Auxiliary.activityExatras(this).dumpXML());
		_id = Auxiliary.activityExatras(this).child("_id").value.property.value();
		this.setTitle("Заявка на добавление клиента в маршрут");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		//long d = new Date().getTime();
		//Calendar c=Calendar.getInstance();
		String sql = "select territoryKod"//
				+ ", klientKod"//
				+ ", marchrutDate"//
				+ ", editDate"//
				+ ", uploadDate"//
				+ ", ponedelnik"//
				+ ", vtornik"//
				+ ", sreda"//
				+ ", chetverg"//
				+ ", pyatnisa"//
				+ ", subbota"//
				+ " from AddKlientDayMarshrut where _id=" + _id + ";";
		Bough d = new Bough();
		try {
			d = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		} catch (Throwable t) {
			Auxiliary.warn(t.getMessage(), Activity_IzmenitMarshrutOne.this);
		}
		//System.out.println(d.dumpXML());
		//double dd=Numeric.string2double(d.child("row").child("marchrutDate").value.property.value());
		//System.out.println(dd);
		//System.out.println(dd);
		marchrutDate.value(Numeric.string2double(d.child("row").child("marchrutDate").value.property.value()));
		territoryKod.value(d.child("row").child("territoryKod").value.property.value());
		klientKod.value(d.child("row").child("klientKod").value.property.value());
		ponedelnik = d.child("row").child("ponedelnik").value.property.value();
		vtornik = d.child("row").child("vtornik").value.property.value();
		sreda = d.child("row").child("sreda").value.property.value();
		chetverg = d.child("row").child("chetverg").value.property.value();
		pyatnisa = d.child("row").child("pyatnisa").value.property.value();
		subbota = d.child("row").child("subbota").value.property.value();
		days = new RedactMultiChoice(this);
		RedactSingleChoice territories = new RedactSingleChoice(this).selection.is(terrSel);
		layoutless.child(new Decor(this).background.is(0xccff0000).labelText.is("Месяц маршрута").labelAlignRightCenter()//
				.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 1));
		layoutless.child(new RedactDate(this).date.is(marchrutDate).format.is("LLLL, yyyy")//
				.left().is(Auxiliary.tapSize * 8).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1));
		layoutless.child(new Decor(this).labelText.is("Территория").labelAlignRightCenter()//
				.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 1));
		layoutless.child(territories//
				.left().is(Auxiliary.tapSize * 8).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1));
		layoutless.child(new Decor(this).labelText.is("Код клиента").labelAlignRightCenter()//
				.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 1));
		layoutless.child(new RedactText(this).singleLine.is(true).text.is(klientKod)//
				.left().is(Auxiliary.tapSize * 8).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1));
		layoutless.child(new Decor(this).labelText.is("Дни визитов").labelAlignRightCenter()//
				.left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 1));
		layoutless.child(days//
				.item("понедельник").item("вторник").item("среда").item("четверг").item("пятница").item("суббота")//
				.left().is(Auxiliary.tapSize * 8).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1));
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			String s = Cfg.territory().children.get(i).child("territory").value.property.value()//
					+ " (" + Cfg.territory().children.get(i).child("hrc").value.property.value().trim() + ")";
			territories.item(s);
			if (territoryKod.value().trim().equals(Cfg.territory().children.get(i).child("kod").value.property.value().trim())) {
				terrSel.value(i);
				//System.out.println(Cfg.territory().children.get(i).child("kod").value.property.value());
			}
		}
		if (ponedelnik.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 0);
		}
		if (vtornik.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 1);
		}
		if (sreda.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 2);
		}
		if (chetverg.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 3);
		}
		if (pyatnisa.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 4);
		}
		if (subbota.trim().equals("1")) {
			days.selection.insert(days.selection.size(), 5);
		}
		//terrSel.value(0);


		Calendar c = Calendar.getInstance();
		//System.out.println("now "+c);
		c.setTimeInMillis(marchrutDate.value().longValue());
		//System.out.println("set "+c);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1; // Note: zero based!
		int day = c.get(Calendar.DAY_OF_MONTH);

		//System.out.println("res "+year+"/"+month+"/"+day);
	}

	@Override
	public void onPause() {
		doSave();
		super.onPause();
	}

	String daysRequest() {
		String r = "";
		String zp = "";
		if (ponedelnik.equals("1")) {
			r = r + zp + "{\"Day\":\"пн\",\"Time\":\"0001-01-01T11:11:00\"}";
			zp = ",";
		}
		if (vtornik.equals("1")) {
			r = r + zp + "{\"Day\":\"вт\",\"Time\":\"0001-01-01T12:12:00\"}";
			zp = ",";
		}
		if (sreda.equals("1")) {
			r = r + zp + "{\"Day\":\"ср\",\"Time\":\"0001-01-01T13:13:00\"}";
			zp = ",";
		}
		if (chetverg.equals("1")) {
			r = r + zp + "{\"Day\":\"чт\",\"Time\":\"0001-01-01T14:14:00\"}";
			zp = ",";
		}
		if (pyatnisa.equals("1")) {
			r = r + zp + "{\"Day\":\"пт\",\"Time\":\"0001-01-01T15:15:00\"}";
			zp = ",";
		}
		if (subbota.equals("1")) {
			r = r + zp + "{\"Day\":\"сб\",\"Time\":\"0001-01-01T16:16:00\"}";
			zp = ",";
		}
		return r;
	}

	String pad(int n) {
		String r = "" + n;
		if (r.length() < 2) r = "0" + r;
		return r;
	}

	void doUpload() {
		if (marchrutDate.value() > 0) {

			doSave();
			final Note res = new Note();
			new Expect().task.is(new Task() {
				@Override
				public void doTask() {
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(marchrutDate.value().longValue());
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH) + 1; // Note: zero based!
					int day = c.get(Calendar.DAY_OF_MONTH);
					String q = "{\"TerritoryKod\":\"" + findKod() + "\""//
							+ ",\"ClientKod\":\"" + klientKod.value() + "\""//
							+ ",\"Date\":\"" + year + "-" + pad(month) + "-" + pad(day) + "T00:00:00\""//
							+ ",\"Days\":["//
							+ daysRequest()//
							//+ "{\"Day\":\"вт\",\"Time\":\"0001-01-01T13:13:00\"}"//
							//+ ",{\"Day\": \"ср\",\"Time\":\"0001-01-01T14:14:00\"}"//
							+ "]"//
							+ ",\"UserKod\":\"" + Cfg.whoCheckListOwner() + "\"}";
					Bough b = Auxiliary.loadTextFromPrivatePOST(Settings.getInstance().getBaseURL() //
									//+ "/cehan_hrc/hs/DobavlenieKlientovVMatricu/"//
									//+ "hrc120107/hs/DobavlenieKlientovVMatricu/"//
									+ Settings.selectedBase1C() + "/hs/DobavlenieKlientovVMatricu/"//
							, q, 30 * 1000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					System.out.println(q);
					System.out.println(b.dumpXML());
					res.value(b.child("message").value.property.value() + ": " + b.child("raw").value.property.value());
					if (b.child("message").value.property.value().equals("OK")) {
						long d = new Date().getTime();
						String sql = "update AddKlientDayMarshrut set "//
								+ " uploadDate=" + d//
								+ " where _id=" + _id + ";";
						System.out.println("doSave " + sql);
						ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
					}
				}
			}).afterDone.is(new Task() {
				@Override
				public void doTask() {
					Auxiliary.warn(res.value(), Activity_IzmenitMarshrutOne.this);
				}
			})//
					.status.is("Подождите.....")//
					.start(Activity_IzmenitMarshrutOne.this);
		} else {
			Auxiliary.warn("Не заполнен месяц", this);
		}
	}

	String findKod() {
		String kod = Cfg.territory().children.get(0).child("kod").value.property.value();
		for (int i = 0; i < Cfg.territory().children.size(); i++) {
			if (i == terrSel.value().intValue()) {
				kod = Cfg.territory().children.get(i).child("kod").value.property.value();
				break;
			}
		}
		return kod;
	}

	void doSave() {
		ponedelnik = "0";
		vtornik = "0";
		sreda = "0";
		chetverg = "0";
		pyatnisa = "0";
		subbota = "0";
		for (int i = 0; i < days.selection.size(); i++) {
			int n = days.selection.at(i).intValue();
			if (n == 0) {
				ponedelnik = "1";
			}
			if (n == 1) {
				vtornik = "1";
			}
			if (n == 2) {
				sreda = "1";
			}
			if (n == 3) {
				chetverg = "1";
			}
			if (n == 4) {
				pyatnisa = "1";
			}
			if (n == 5) {
				subbota = "1";
			}
		}
		String sql = "update AddKlientDayMarshrut set "//
				+ " marchrutDate=" + marchrutDate.value().longValue()//
				+ " ,territoryKod='" + findKod() + "'"//
				+ " ,klientKod='" + klientKod.value() + "'"//
				+ " ,ponedelnik='" + ponedelnik + "'"//
				+ " ,vtornik='" + vtornik + "'"//
				+ " ,sreda='" + sreda + "'"//
				+ " ,chetverg='" + chetverg + "'"//
				+ " ,pyatnisa='" + pyatnisa + "'"//
				+ " ,subbota='" + subbota + "'"//
				+ " where _id=" + _id + ";";
		//System.out.println("doSave " + sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	void promptDelete() {
		Auxiliary.pickConfirm(this, "Удалить заявку на изменение маршрута", "Удалить", new Task() {
			@Override
			public void doTask() {
				String sql = "delete from AddKlientDayMarshrut where _id=" + Activity_IzmenitMarshrutOne.this._id + ";";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				Activity_IzmenitMarshrutOne.this.finish();
			}
		});
	}
}
