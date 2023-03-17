package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;

import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.task.*;
import tee.binding.*;

public class ActivityKartaKlienta extends Activity {
	Layoutless layoutless;
	DataGrid grid;
	ColumnDescription name= new ColumnDescription();
	//int gridPageSize = 30;
	Bough data;
	//Numeric gridOffset = new Numeric();
	MenuItem menuNew;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuNew = menu.add("Добавить");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuNew) {
			Intent intent = new Intent();
			//intent.putExtra("uin", uin);
			intent.setClass(ActivityKartaKlienta.this, sweetlife.android10.supervisor.ActivityKartaKlientaEdit.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		//ApplicationHoreca.getInstance().getClientInfo().getKod();
		//Bough b=Auxiliary.bundle2bough(getIntent().getExtras());
		//System.out.println(b.dumpXML());
		createGUI();
		}
	@Override
	protected void onResume() {
		super.onResume();
		fillGUI();
	}
	void createGUI() {
		String clinetName="?";
		if(ApplicationHoreca.getInstance().getClientInfo()!=null){
			clinetName=ApplicationHoreca.getInstance().getClientInfo().getName();
		}
		this.setTitle(clinetName);
		grid=new DataGrid(this);
		layoutless.child(grid//
				.center.is(true)//
				//.pageSize.is(gridPageSize)//
				//.dataOffset.is(gridOffset)//
				
				.columns(new Column[] { //
						//
						name.title.is("Карта клиента")//
						.width.is(10 * Auxiliary.tapSize)//
						}
				)//
						//.top().is(Auxiliary.tapSize)//
						.width().is(layoutless.width().property)//
						.height().is(layoutless.height().property)//
				);
	}
	void fillGUI() {
		String clinetID="?";
		if(ApplicationHoreca.getInstance().getClientInfo()!=null){
			clinetID=ApplicationHoreca.getInstance().getClientInfo().getID();
		}
		String sql="select"//
				+"\n		k.uin as uin, nazvanie as nazvanie, kommentarii as kommentarii ,number as number"//
				+"\n		,v.naimenovanie,v._idrref"//
				+"\n	from kartaKlientaDok2 d"//
				+"\n		join KartaKlientaKlient2 k on k.uin=d.uin"//
				+"\n		join kontragenty v on k.vladelec=v._idrref"//
				+"\n	where v._idrref="+clinetID//
				+"\n	group by k.uin"//
				+"\n	order by v.naimenovanie"//
				//+"\n	limit " + (gridPageSize * 3)+ " offset " + gridOffset.value().intValue();
				 ;
		//System.out.println(sql);
		data=Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		grid.clearColumns();
		for (int i = 0; i < data.children.size(); i++) {
			Bough row = data.children.get(i);
			final String uin=row.child("uin").value.property.value();
			Task tap=new Task(){

				@Override
				public void doTask() {
					Intent intent = new Intent();
					intent.putExtra("uin", uin);
					intent.setClass(ActivityKartaKlienta.this, sweetlife.android10.supervisor.ActivityKartaKlientaEdit.class);
					startActivity(intent);
					
				}};
			name.cell(row.child("nazvanie").value.property.value(),tap,row.child("kommentarii").value.property.value());
		}
		grid.refresh();
		//System.out.println("data "+data.dumpXML());
	}
}
