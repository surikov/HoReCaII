package sweetlife.android10.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sweetlife.android10.R;


public class Dialog_Documents extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.dialog_documents);

		super.onCreate(savedInstanceState);

		setTitle(R.string.documents);

		final Button btnVisits = (Button)findViewById(R.id.btn_visits);
		btnVisits.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Dialog_Documents.this, Activity_Doc_Visits.class);
				startActivity(intent);

				finish();
			}
		});
/*
		final Button btnOrders = (Button)findViewById(R.id.btn_orders);
		btnOrders.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Dialog_Documents.this, Activity_Doc_Orders.class);
				startActivity(intent);

				finish();
			}
		});
*/
		final Button btnBids = (Button)findViewById(R.id.btn_bids);
		btnBids.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Dialog_Documents.this, Activity_Doc_Bids.class);
				startActivity(intent);

				finish();
			}
		});

		final Button btnGPSPoints = (Button)findViewById(R.id.btn_gps_points);
		btnGPSPoints.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Dialog_Documents.this, Activity_Doc_GPS_Points.class);
				startActivity(intent);

				finish();
			}
		});
	}
}
