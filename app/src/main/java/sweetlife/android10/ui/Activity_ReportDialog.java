package sweetlife.android10.ui;

import sweetlife.android10.reports.IReportConsts;

import sweetlife.android10.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_ReportDialog extends Activity implements IReportConsts {

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		LogHelper.debug(this.getClass().getCanonicalName() + " onCreate");
		setContentView(R.layout.dialog_reports);

		super.onCreate(savedInstanceState);

		setTitle(R.string.reports);
		
		final Button btnBalance = (Button) findViewById(R.id.btn_balance);
		btnBalance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Activity_ReportDialog.this, Activity_ReportBalance.class);
				startActivity(intent);
				
				finish();
			}
		});

		final Button btnStatistics = (Button) findViewById(R.id.btn_statistics);
		btnStatistics.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Activity_ReportDialog.this, Activity_ReportStatistics.class);
				startActivity(intent);
				
				finish();
			}
		});

		final Button btnOrderStates = (Button) findViewById(R.id.btn_order_state);
		btnOrderStates.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Activity_ReportDialog.this, Activity_ReportOrderState.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnDistribution = (Button) findViewById(R.id.btn_distribution);
		btnDistribution.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_REPORT_NAME, getString(R.string.distribution));
				intent.putExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_DISTRIBUTION);
				intent.setClass(Activity_ReportDialog.this, Activity_ReportDate.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnTrafiks = (Button) findViewById(R.id.btn_trafiks);
		btnTrafiks.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Activity_ReportDialog.this, Activity_ReportTrafiks.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnDisposals = (Button) findViewById(R.id.btn_disposal_states);
		btnDisposals.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_REPORT_NAME, getString(R.string.disposal_states));
				intent.putExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_DISPOSALS);
				intent.setClass(Activity_ReportDialog.this, Activity_ReportDate.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnFixedPrices = (Button) findViewById(R.id.btn_fixed_prices);
		btnFixedPrices.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_REPORT_NAME, getString(R.string.fixed_prices));
				intent.putExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_FIXED_PRICES);
				intent.setClass(Activity_ReportDialog.this, Activity_ReportDate.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnKPI = (Button) findViewById(R.id.btn_kpi);
		btnKPI.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(Activity_ReportDialog.this, Activity_ReportKPI.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnDriversDelivery = (Button) findViewById(R.id.btn_drivers);
		btnDriversDelivery.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_REPORT_NAME, getString(R.string.drivers_delivery));
				intent.putExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_DRIVERS_DELIVERY);
				intent.setClass(Activity_ReportDialog.this, Activity_ReportDate.class);
				startActivity(intent);
				
				finish();
			}
		});
		
		final Button btnPredzakazTrafik = (Button) findViewById(R.id.btn_PredzakazTrafik);
		btnPredzakazTrafik.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(EXTRA_REPORT_NAME, "Предзаказы на трафик");
				intent.putExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_TRAFIKS);
				intent.setClass(Activity_ReportDialog.this, Activity_ReportPredzakazTrafik.class);
				startActivity(intent);
				
				finish();
			}
		});
	}
}
