package sweetlife.android10.supervisor;

import android.app.Activity;

import android.os.*;
import android.view.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;

public class ActivityLimitDogovor extends Activity {
	String id;
	Layoutless layoutless;
	Note Gruppa = new Note();
	Numeric TO1 = new Numeric();
	Numeric TO2 = new Numeric();
	Numeric TOPlan = new Numeric();
	Numeric Otsrochka = new Numeric();
	Numeric OtsrochkaPlan = new Numeric();
	Numeric Limit = new Numeric();
	Numeric LimitSV = new Numeric();
	Numeric LimitPlan = new Numeric();
	Note KommentariiSv = new Note();
	Note KommentariiFin = new Note();
	Numeric Poruchitelstvo = new Numeric();
	Numeric LimitPoDogovoru = new Numeric();
	//Note Territiriya = new Note();
	Numeric LimitRachet = new Numeric();
	Numeric PlanTO = new Numeric();
	Note KommentariiTP = new Note();
	MenuItem menuUpdate;
	MenuItem menuDelete;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuUpdate = menu.add("Сохранить");
		menuDelete = menu.add("Удалить");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuUpdate) {
			update();
		}
		if (item == menuDelete) {
			promptDelete();
		}
		return super.onOptionsItemSelected(item);
	}
	void update() {
		String sql = "update LimitiDogovor set"//
				+" TO1="+TO1.value()//
				+ ", TO2="+TO2.value()//
				+ ", TOPlan="+TOPlan.value()//
				+ ", Otsrochka="+Otsrochka.value()//
				+ ", OtsrochkaPlan="+OtsrochkaPlan.value()//
				+ ", LimitValue="+Limit.value()//
				+ ", LimitSV ="+LimitSV.value()//
				+ ", LimitPlan ="+LimitPlan.value()//
				+ ", KommentariiSv ='"+KommentariiSv.value()+"'"//
				+ ", KommentariiFin ='"+KommentariiFin.value()+"'"//
				+ ", Poruchitelstvo ="+Poruchitelstvo.value()//
				+ ", LimitPoDogovoru ="+LimitPoDogovoru.value()//
				+ ", LimitRachet ="+LimitRachet.value()//
				+ ", PlanTO ="+PlanTO.value()//
				+ ", KommentariiTP='"+KommentariiTP.value()+"'"//
				+ " where _id=" + id;
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}
	void promptDelete() {
		Auxiliary.pickConfirm(this, "Вы уверены?", "Удалить", new Task() {
			@Override
			public void doTask() {
				String sql = "delete from LimitiDogovor where _id=" + id;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				finish();
			}
		});
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String s = bundle.getString("id");
			if (s != null) {
				id = s;
			}
		}
		this.setTitle("Лимиты: " + id);
		composeGUI();
	}
	void composeGUI() {
		String sql = "select LimitiDogovor._id as _id"//
				+ ", LimitiList "//
				+ ", Gruppa "//
				+ ", TO1 "//
				+ ", TO2 "//
				+ ", TOPlan "//
				+ ", Otsrochka "//
				+ ", OtsrochkaPlan "//
				+ ", LimitValue "//
				+ ", LimitSV "//
				+ ", LimitPlan "//
				+ ", KommentariiSv "//
				+ ", KommentariiFin "//
				+ ", Poruchitelstvo "//
				+ ", LimitPoDogovoru "//
				+ ", Territiriya "//
				+ ", LimitRachet "//
				+ ", PlanTO "//
				+ ", KommentariiTP"//
				+ ", GruppyDogovorov.Naimenovanie as gruppaName"//
				+ " from LimitiDogovor"//
				+ " left join GruppyDogovorov on GruppyDogovorov.kod=Gruppa" //
				+ " where LimitiDogovor._id=" + id;
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		Gruppa.value(data.child("row").child("gruppaName").value.property.value());
		TO1.value(Numeric.string2double(data.child("row").child("TO1").value.property.value()));
		TO2.value(Numeric.string2double(data.child("row").child("TO2").value.property.value()));
		TOPlan.value(Numeric.string2double(data.child("row").child("TOPlan").value.property.value()));
		Otsrochka.value(Numeric.string2double(data.child("row").child("Otsrochka").value.property.value()));
		OtsrochkaPlan.value(Numeric.string2double(data.child("row").child("OtsrochkaPlan").value.property.value()));
		Limit.value(Numeric.string2double(data.child("row").child("LimitValue").value.property.value()));
		LimitSV.value(Numeric.string2double(data.child("row").child("LimitSV").value.property.value()));
		LimitPlan.value(Numeric.string2double(data.child("row").child("LimitPlan").value.property.value()));
		KommentariiSv.value(data.child("row").child("KommentariiSv").value.property.value());
		KommentariiFin.value(data.child("row").child("KommentariiFin").value.property.value());
		Poruchitelstvo.value(Numeric.string2double(data.child("row").child("Poruchitelstvo").value.property.value()));
		LimitPoDogovoru.value(Numeric.string2double(data.child("row").child("LimitPoDogovoru").value.property.value()));
		LimitRachet.value(Numeric.string2double(data.child("row").child("LimitRachet").value.property.value()));
		PlanTO.value(Numeric.string2double(data.child("row").child("PlanTO").value.property.value()));
		KommentariiTP.value(data.child("row").child("KommentariiTP").value.property.value());
		layoutless.field(this, 0, "Группа договоров", new Decor(this).labelText.is(Gruppa).labelAlignLeftCenter(),9*Auxiliary.tapSize);
		layoutless.field(this, 1, "TO1", new RedactNumber(this).number.is(TO1));
		layoutless.field(this, 2, "TO2", new RedactNumber(this).number.is(TO2));
		layoutless.field(this, 3, "TO план", new RedactNumber(this).number.is(TOPlan));
		layoutless.field(this, 4, "Отсрочка", new RedactNumber(this).number.is(Otsrochka));
		layoutless.field(this, 5, "Отсрочка план", new RedactNumber(this).number.is(OtsrochkaPlan));
		layoutless.field(this, 6, "Лимит", new RedactNumber(this).number.is(Limit));
		layoutless.field(this, 7, "Лимит СВ", new RedactNumber(this).number.is(LimitSV));
		layoutless.field(this, 8, "Лимит план", new RedactNumber(this).number.is(LimitPlan));
		layoutless.field(this, 9, "Комментарии СВ", new RedactText(this).text.is(KommentariiSv));
		layoutless.field(this, 10, "Комментарии Фин", new RedactText(this).text.is(KommentariiFin));
		layoutless.field(this, 11, "Поручительство", new RedactNumber(this).number.is(Poruchitelstvo));
		layoutless.field(this, 12, "Лимит по договору", new RedactNumber(this).number.is(LimitPoDogovoru));
		//layoutless.field(this, 13, "Territiriya", new RedactText(this).text.is(Territiriya));
		layoutless.field(this, 13, "Лимит расчёт", new RedactNumber(this).number.is(LimitRachet));
		layoutless.field(this, 14, "План TO", new RedactNumber(this).number.is(PlanTO));
		layoutless.field(this, 15, "Комментарии ТП", new RedactText(this).text.is(KommentariiTP));
		layoutless.innerHeight.is(Auxiliary.tapSize * 14);
	}
}
