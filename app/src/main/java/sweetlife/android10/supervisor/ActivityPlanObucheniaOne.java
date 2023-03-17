package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;

import reactive.ui.*;
//import sweetlife.horeca.reports.ActivityReports;

import tee.binding.*;

public class ActivityPlanObucheniaOne  extends Activity {
	Layoutless layoutless;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bough in = bundle2bough(getIntent().getExtras());
		layoutless = new Layoutless(this);
		this.setTitle("План полевых обучений на "+in.child("den").value.property.value()+", "+in.child("data").value.property.value());
		setContentView(layoutless);
		createGUI();
		//ActivityWebServicesReports.initDB();
	}
	void createGUI(){}
	public static Bough bundle2bough(Bundle bundle) {
		Bough bough=new Bough();
		if (bundle == null) {
			bough.name.is("null");
		} else {
			bough.name.is("extra");
			for (String key : bundle.keySet()) {
				String value = bundle.getString(key);
				//System.out.println(key + ": " + value);
				bough.child(key).value.is(value);
			}
		}
		return bough;
	}
}
