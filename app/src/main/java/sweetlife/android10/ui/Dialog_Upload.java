package sweetlife.android10.ui;

import java.util.Observable;
import java.util.Observer;

import sweetlife.android10.gps.UploadTask;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;
import sweetlife.android10.utils.UIHelper;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dialog_Upload extends Activity_Base implements Observer {
	/*final String easterEggFlag = "/sdcard/horeca/1apr.txt";

	boolean needEasterEgg() {
		//Auxiliary.pickConfirm(this, "Test", "OK", null);
		java.util.Date d = new java.util.Date();
		int day = d.getDate();
		int month = d.getMonth();
		if (day == 1 && month == 3) {
			File f = new File(easterEggFlag);
			if (!f.exists()) {
				return true;
			}
		}
		return false;
	}
	void stopEasterEgg() {
		Auxiliary.writeTextToFile(new File(easterEggFlag), "done");
	}
	void showEasterEgg() {
		Auxiliary.pick3Choice(this, "Внимание!"//
				, "Индикатор статического электричества на GPS-антенте показывает недопустимый уровень"//
						+ " (возможно, это результат частого контакта планшета с одеждой из синтетических материалов)."//
						+ " Необходимо провести разблокировку GPS-датчика."//
				, "Отмена", null, "Разблокировать", new Task() {
					@Override
					public void doTask() {
						showEasterDialog1();
					}
				}, null, null);
	}
	void showEasterDialog1() {
		Auxiliary.pick3Choice(this, "Шаг 1/4", "Подсоедините кабель питания к планшету, но не подключайте вилку"//
				+ " к электросети."//
		, "Отмена", null, "Дальше", new Task() {
			@Override
			public void doTask() {
				showEasterDialog2();
			}
		}, null, null);
	}
	void showEasterDialog2() {
		Auxiliary.pick3Choice(this, "Шаг 2/4", "Одно рукой прикоснитесь к клеммам вилки питания. Одновременно,"//
				+ " прикоснитесь второй рукой к любому массивному металлическому предмету (например"//
				+ " к радиатору батареи центрального отопления или корпусу автомобиля)."//
		, "Отмена", null, "Назад", new Task() {
			@Override
			public void doTask() {
				showEasterDialog1();
			}
		}, "Дальше", new Task() {
			@Override
			public void doTask() {
				showEasterDialog3();
			}
		});
	}
	void showEasterDialog3() {
		Auxiliary.pick3Choice(this, "Шаг 3/4", "Третьей рукой или любой другой выступающей частью тела"//
				+ " нажмите кнопку 'Применить'."//
		, "Отмена", null, "Назад", new Task() {
			@Override
			public void doTask() {
				showEasterDialog2();
			}
		}, "Применить", new Task() {
			@Override
			public void doTask() {
				showEasterDialog4();
			}
		});
	}
	void showEasterDialog4() {
		stopEasterEgg();
		Auxiliary.pick3Choice(this, "Шаг 4/4", "Разблокировка успешно проведена. ИТ-отдел компании"//
				+ " 'Сладкая жизнь' поздравляет вас с 1 апреля."//
		, "Закрыть", null, null, null, null, null);
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_upload);
		super.onCreate(savedInstanceState);
		setTitle(R.string.upload);
		final Button btnGPSData = (Button) findViewById(R.id.btn_gps_data);
		//btnGPSData.setText("GPS ("+")");
		System.out.println("btnGPSData " + btnGPSData.getText());

		btnGPSData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogHelper.debug("onClick R.id.btn_gps_data");
				//if (needEasterEgg()) {
				//	showEasterEgg();
				//}
				//else {
				UploadTask task = new UploadTask(mDB, SystemHelper.getDiviceID(Dialog_Upload.this), getApplicationContext());
				AsyncTaskManager.getInstance().executeTask(Dialog_Upload.this, task);
				//}
			}
		});
		final Button btnBids = (Button) findViewById(R.id.btn_bids);
		btnBids.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Dialog_Upload.this, Activity_UploadBids.class);
				startActivity(intent);
				finish();
			}
		});
		final Button btnFixedPrices = (Button) findViewById(R.id.btn_fixed_prices);
		btnFixedPrices.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Dialog_Upload.this, Activity_UploadFixedPrices.class);
				startActivity(intent);
				finish();
			}
		});
		final Button btnReturns = (Button) findViewById(R.id.btn_returns);
		btnReturns.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Dialog_Upload.this, Activity_UploadReturns.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void update(Observable observable, Object data) {
		String result = ((Bundle) data).getString(ManagedAsyncTask.RESULT_STRING);
		if (result != null) {
			LogHelper.debug(this.getClass().getCanonicalName() + ".update: " + this.getString(R.string.confirm));
			UIHelper.MsgBox(ApplicationHoreca.getInstance().getCurrentAgent().getAgentName() + ", " + ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieName(), result, this);
			/*
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle( ApplicationHoreca.getInstance().getCurrentAgent().getAgentName()+", "+ ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieName() );
			builder.setMessage( result );

			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override

				public void onClick(DialogInterface dialog, int arg1) {

					dialog.dismiss();
				}
			});

			builder.create().show();*/
		}
	}
}
