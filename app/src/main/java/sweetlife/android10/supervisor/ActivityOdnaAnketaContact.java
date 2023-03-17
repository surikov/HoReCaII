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

public class ActivityOdnaAnketaContact extends Activity {
	String _id = null;
	String anketaId = null;
	Layoutless layoutless;
	Note FIO = new Note();
	Note Telefon = new Note();
	Note Dolztost = new Note();
	//Note Rol = new Note();
	MenuItem menuSave;
	MenuItem menuDelete;
	Numeric rolSelect = new Numeric();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String s = bundle.getString("_id");
			if (s != null) {
				_id = s;
			}
			s = bundle.getString("anketaId");
			if (s != null) {
				anketaId = s;
			}
		}
		createGUI();
		requery();
	}
	void createGUI() {
		layoutless.field(this, 0, "ФИО", new RedactText(this).text.is(FIO));
		layoutless.field(this, 1, "телефон", new RedactText(this).text.is(Telefon));
		layoutless.field(this, 2, "должность", new RedactText(this).text.is(Dolztost));
		layoutless.field(this, 3, "роль", new RedactSingleChoice(this)//
				.item(selectedRolName(0))//
				.item(selectedRolName(1))//
				.item(selectedRolName(2))//
				.item(selectedRolName(3))//
				.item(selectedRolName(4))//
				.item(selectedRolName(5))//
				.item(selectedRolName(6))//
				.selection.is(rolSelect));
	}
	void requery() {
		if (_id == null) {
			this.setTitle("Новый контакт");
		}
		else {
			this.setTitle("Редактирование контакта");
			String sql = "select * from AnketaKlientaContacts where _id=" + _id;
			Bough d = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			FIO.value(d.child("row").child("FIO").value.property.value());
			Telefon.value(d.child("row").child("Telefon").value.property.value());
			Dolztost.value(d.child("row").child("Dolztost").value.property.value());
			//Rol.value(d.child("row").child("Rol").value.property.value());
			//setRolSel(d.child("row").child("Rol").value.property.value());
			rolSelect.value(selectedRolNum(d.child("row").child("Rol").value.property.value()));
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuSave = menu.add("Сохранить");
		menuDelete = menu.add("Удалить");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item == menuSave) {
			save();
			return true;
		}
		if (item == menuDelete) {
			Auxiliary.pickConfirm(this, "Удалить контакт?", "Да", new Task() {
				@Override
				public void doTask() {
					delete();
				}
			});
		}
		return super.onOptionsItemSelected(item);
	}
	void save() {
		String sql = "update AnketaKlientaContacts set"//
				+ " FIO='" + FIO.value() + "'"//
				+ ", Telefon='" + Telefon.value() + "'"//
				+ ", Dolztost='" + Dolztost.value() + "'"//
				+ ", Rol='" + selectedRolName(rolSelect.value().intValue())
				//Rol.value() 
				+ "'"//
				+ " where _id=" + _id + ";";
		if (_id == null) {
			sql = "insert into AnketaKlientaContacts (anketaId,FIO,Telefon,Dolztost,Rol) values ("//
					+ anketaId//
					+ ", '" + FIO.value() + "'"//
					+ ", '" + Telefon.value() + "'"//
					+ ", '" + Dolztost.value() + "'"//
					+ ", '" + selectedRolName(rolSelect.value().intValue())
					//Rol.value() 
					+ "'"//
					+ ");";
		}
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		finish();
	}
	void delete() {
		ApplicationHoreca.getInstance().getDataBase().execSQL("delete from AnketaKlientaContacts where _id=" + _id);
		finish();
	}
	public static int selectedRolNum(String rol) {
		int nn = 0;
		if (rol.equals("Директор")) {
			nn = 1;
		}
		if (rol.equals("Для печати актов бонусов")) {
			nn = 2;
		}
		if (rol.equals("Закупщик")) {
			nn = 3;
		}
		if (rol.equals("Собственник")) {
			nn = 4;
		}
		if (rol.equals("Шеф-повар")) {
			nn = 5;
		}
		if (rol.equals("Приемщик")) {
			nn = 6;
		}
		return nn;
	}
	public static String selectedRolName(int nn) {
		String a = "Бухгалтер";
		if (nn == 1) {
			a = "Директор";
		}
		if (nn == 2) {
			a = "Для печати актов бонусов";
		}
		if (nn == 3) {
			a = "Закупщик";
		}
		if (nn == 4) {
			a = "Собственник";
		}
		if (nn == 5) {
			a = "Шеф-повар";
		}
		if (nn == 6) {
			a = "Приемщик";
		}
		return a;
	}
	/*public static String rolNameByKod(String kod) {
		String a = selectedRolName(0);
		if (kod.equals("00002")) {
			a = selectedRolName(1);
		}
		if (kod.equals("00006")) {
			a = selectedRolName(2);
		}
		if (kod.equals("00001")) {
			a = selectedRolName(3);
		}
		if (kod.equals("00005")) {
			a = selectedRolName(4);
		}
		if (kod.equals("00003")) {
			a = selectedRolName(5);
		}
		return a;
	}*/
	public static String rolKodByName(String rol) {
		String a = "00004";
		if (rol.equals(selectedRolName(1))) {
			a = "00002";
		}
		if (rol.equals(selectedRolName(2))) {
			a = "00006";
		}
		if (rol.equals(selectedRolName(3))) {
			a = "00001";
		}
		if (rol.equals(selectedRolName(4))) {
			a = "00005";
		}
		if (rol.equals(selectedRolName(5))) {
			a = "00003";
		}
		if (rol.equals(selectedRolName(6))) {
			a = "00008";
		}
		return a;
	}
}
