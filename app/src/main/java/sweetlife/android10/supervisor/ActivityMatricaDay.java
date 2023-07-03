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

public class ActivityMatricaDay extends Activity {
	Layoutless layoutless;
	String _id = "";
	Note data = new Note();
	Numeric planUtro = new Numeric();
	Numeric planLetuchka = new Numeric();
	Numeric planSTM = new Numeric();
	//Numeric planItogo = new Numeric();
	//Numeric planNarItog = new Numeric();
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
				this.setTitle("Матрица " + _id);
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
				+ " s.data as data,planUtro as planUtro,planLetuchka as planLetuchka,planItogo as planItogo,planNarItog as planNarItog,planSTM as planSTM"//
				+ " from MatricaSvodX s"//
				+ " where s._id=" + _id;
		//System.out.println(sql);
		Bough r = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		Date d = new Date();
		d.setTime((long) Numeric.string2double(r.child("row").child("data").value.property.value()));
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, cccc");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		data.value(format.format(d));
		planUtro.value(Numeric.string2double(r.child("row").child("planUtro").value.property.value()));
		planLetuchka.value(Numeric.string2double(r.child("row").child("planLetuchka").value.property.value()));
		planSTM.value(Numeric.string2double(r.child("row").child("planSTM").value.property.value()));
		//planItogo.value(Numeric.string2double(r.child("row").child("planItogo").value.property.value()));
		//planNarItog.value(Numeric.string2double(r.child("row").child("planNarItog").value.property.value()));
	}
	void composeGUI() {
		layoutless.field(this, 0, "дата", new Decor(this).labelText.is("00.00.00").labelAlignLeftCenter().labelText.is(data));
		layoutless.field(this, 1, "план утро", new RedactNumber(this).number.is(planUtro));
		layoutless.field(this, 2, "план летучка", new RedactNumber(this).number.is(planLetuchka));
		layoutless.field(this, 3, "план СТМ", new RedactNumber(this).number.is(planSTM));
		//layoutless.field(this, 3, "план итого", new RedactNumber(this).number.is(planItogo));
		//layoutless.field(this, 4, "план нар. итого", new RedactNumber(this).number.is(planNarItog));
		/*layoutless.field(this, 5, "", new Knob(this).labelText.is("Сохранить").afterTap.is(new Task() {
			@Override
			public void doTask() {
				save();
			}
		}));
		layoutless.field(this, 6, "", new Knob(this).labelText.is("Удалить").afterTap.is(new Task() {
			@Override
			public void doTask() {
				delete();
			}
		}));*/
	}
	void save() {
		String sql = "update MatricaSvodX"//
				+ "\n	set planUtro=" + planUtro.value()//
				+ "\n	,planLetuchka=" + planLetuchka.value()//
				+ "\n	,planSTM=" + planSTM.value()//
				//+ "\n	,planItogo=" + planItogo.value() //
				//+ "\n	,planNarItog=" + planNarItog.value()//
				+ "\n	where _id=" + _id//
		;
		//System.out.println(sql);
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		//finish();
		finish();
	}
	void delete() {
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
