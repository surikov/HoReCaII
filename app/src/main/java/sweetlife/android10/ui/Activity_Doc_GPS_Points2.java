package sweetlife.android10.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

import sweetlife.android10.*;
import tee.binding.task.*;
import tee.binding.it.*;
import reactive.ui.*;

public class Activity_Doc_GPS_Points2 extends Activity {
	Layoutless layoutless;
	DataGrid dataGrid;
	ColumnText columnUpload;
	ColumnText columnTime;
	ColumnText columnLat;
	ColumnText columnLong;
	MenuItem menuRefresh;
	MenuItem menuOtchety;
	MenuItem menuResetUpload;
	Numeric periodFrom = new Numeric();
	Numeric periodTo = new Numeric();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//System.out.println("onCreate");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle("GPS отметки");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuRefresh = menu.add("Обновить");
		menuOtchety = menu.add("Отчёты");
		menuResetUpload= menu.add("Повторить выгрузку");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuOtchety) {
			Intent intent = new Intent();
			intent.setClass(this, sweetlife.android10.supervisor.ActivityWebServicesReports.class);
			startActivity(intent);
			return true;
		}
		if (item == menuRefresh) {
			//OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
			return true;
		}
		if (item == menuResetUpload) {
			promptResetUpload();
			return true;
		}
		return true;
	}
	void promptResetUpload(){
		Auxiliary.pickConfirm(this, "Пометить все точки и визиты как невыгруженные", "Да", new Task(){
			@Override
			public void doTask() {
				String sql="update GPSPoints set upload=0;";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				sql="update vizits set upload=0 where date(BeginTime)>date('now','-2 days');";
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				//OnDateChanged(mFromPeriod.getTime(), mToPeriod.getTime());
			}
		});
	}
}
