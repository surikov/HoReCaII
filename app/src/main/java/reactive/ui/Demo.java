package reactive.ui;

import android.view.*;
import android.app.*;
import android.content.*;
import android.os.Bundle;

import java.util.*;
/*
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
*/
//import com.example.android.apis.R;

import sweetlife.android10.supervisor.Cfg;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
//import uniform.DataActivity;
//import uniform.DataEnvironment;


public class Demo extends Activity {
	Layoutless layoutless;
	DataGrid dataGrid;
	int gridPageSize = 30;
	Bough gridData;
	Numeric gridOffset = new Numeric();
	ColumnDescription columnName = new ColumnDescription();
	Expect requery = new Expect().status.is("...").task.is(new Task() {
		@Override
		public void doTask() {
			requeryData();
		}
	})//
			.afterDone.is(new Task() {
				@Override
				public void doTask() {
					refreshGUI();
				}
			})//
			;

	public void refreshGUI() {
		//System.out.println("refreshGUI");
		flipGrid();
		dataGrid.refresh();
	}

	void requeryGridData() {
		gridData = new Bough();
		for (int i = 0; i < gridPageSize * 3; i++) {
			Bough row = new Bough();
			row.child("cell").value.property.value("cell " + (i + gridOffset.value()));
			gridData.child(row);
		}
	}

	void flipGrid() {
		dataGrid.clearColumns();
		if (gridData != null) {
			for (int i = 0; i < gridData.children.size(); i++) {
				Bough row = gridData.children.get(i);
				columnName.cell("/" + row.child("cell").value.property.value());
			}
		}
	}

	public void requeryData() {
		//System.out.println("requeryData");
		requeryGridData();
	}

	void initAll() {
		Auxiliary.startSensorEventListener(this, new Task() {
			@Override
			public void doTask() {
				//System.out.println(Auxiliary.accelerometerX+"x"+Auxiliary.accelerometerY+"x"+Auxiliary.accelerometerZ);
			}
		});
		dataGrid = new DataGrid(this).center.is(true)//
				.pageSize.is(gridPageSize)//
				.dataOffset.is(gridOffset)//
				.beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryGridData();
						flipGrid();
					}
				});
		layoutless.child(dataGrid.noHead.is(true).center.is(true)//
				.columns(new Column[]{ //
						columnName.noHorizontalBorder.is(true).width.is(Auxiliary.tapSize * 37) //
				})//
				.left().is(0)//
				.top().is(Auxiliary.tapSize)//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);
		layoutless.child(new Knob(this).afterTap.is(new Task() {
			@Override
			public void doTask() {
				//System.out.println("test");
				//System.out.println(Auxiliary.loadTextFromResource(Demo.this, R.raw.test));
				final Note searchWord = new Note();
				Auxiliary.pickString(Demo.this, "Поиск", searchWord, "Найти", new Task() {
					@Override
					public void doTask() {
						Intent intent = new Intent(Demo.this, Demo.class);
						intent.putExtra("searchWord", searchWord.value());
						intent.putExtra("folderKey", "");
						intent.putExtra("folderPath", "");
						Demo.this.startActivity(intent);
					}
				});
			}
		}).width().is(100).height().is(100));
		Layoutless child = layoutless.child(new Knob(this).labelText.is("soap1").afterTap.is(new Task() {
			@Override
			public void doTask() {
				/*
				System.out.println(layoutless.getMeasuredHeight() + "/" + layoutless.height().property.value());
				layoutless.requestLayout();
				System.out.println(layoutless.getMeasuredHeight() + "/" + layoutless.height().property.value());
				layoutless.getParent().requestLayout();
				System.out.println(layoutless.getMeasuredHeight() + "/" + layoutless.height().property.value());
				layoutless.height().property.value(layoutless.getMeasuredHeight());
*/
				final RawSOAP rawSOAP = new RawSOAP();
				//rawSOAP.url.is("http://89.109.7.162/ReportAndroid.1cws");
				rawSOAP.url.is(sweetlife.android10.Settings.getInstance().getBaseURL()+"ReportAndroid.1cws");
				rawSOAP.xml.is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" //
						+ "	<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"//
						+ "		<soap:Body>\n" //
						+ "			<m:getReport xmlns:m=\"http://ws.swl/fileHRC\">\n"//
						+ "				<m:Имя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">ТекущиеЛимитыТП</m:Имя>\n"//
						+ "				<m:НачалоПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">1970-01-01T00:00:00</m:НачалоПериода>\n"//
						+ "				<m:КонецПериода xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">1970-01-01T23:59:59</m:КонецПериода>\n"//
						+ "				<m:КодПользователя xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">hrc300</m:КодПользователя>\n"//
						+ "				<m:Параметры xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"//
						+ "				</m:Параметры>\n"//
						+ "				<m:IMEI xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">hrc300:359514061296866</m:IMEI>\n"//
						+ "			</m:getReport>\n"//
						+ "		</soap:Body>\n" //
						+ "	</soap:Envelope>\n");
				rawSOAP.afterError.is(new Task() {
					@Override
					public void doTask() {
						//System.out.println("error " + rawSOAP.responseCode.property.value() + "/" + rawSOAP.statusCode.property.value() + "/" + rawSOAP.exception.property.value());
					}
				});
				rawSOAP.afterSuccess.is(new Task() {
					@Override
					public void doTask() {
						//System.out.println("success");
						//System.out.println(rawSOAP.responseCode.property.value());
						//System.out.println(rawSOAP.rawResponse);
						//System.out.println(rawSOAP.data.dumpXML());
				}
				});
				rawSOAP.startLater(Demo.this, "sending", Cfg.whoCheckListOwner(),Cfg.hrcPersonalPassword());
			}
		}).left().is(200).top().is(100).width().is(100).height().is(100));
		requery.start(Demo.this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//System.out.println("onCreate");
		Bough parameters = new Bough();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Set<String> ks = extras.keySet();
			Iterator<String> iterator = ks.iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				parameters.child(name).value.property.value(intent.getStringExtra(name));
			}
		}
		//System.out.println(Auxiliary.bundle2bough(this.getIntent().getExtras()).dumpXML());
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		initAll();
		this.setTitle("searchWord " + parameters.child("searchWord").value.property.value());
		//DataActivity.replaceVariables("{0123}456{78}{{901}2}");
		//String jsn="{  'lines': [  {  'apikey': '7649841b7E46112E3014EFd9889D62',  'start': '2017-07-17T06:37:29',  'lt': '53.20104215',  'ln': '44.99835479'  },  {  'apikey': '                              ',  'start': '2017-05-12T07:21:20',  'lt': '53.20104215',  'ln': '44.99835479'  },  {  'apikey': '                              ',  'start': '2017-05-11T07:14:11',  'lt': '53.20104215',  'ln': '44.99835479'  }  ],  'info': '104645'  }";
		//String jsn="{  \"lines\": [  {  \"apikey\": [{a:\"b\"},\"7649841b7E46112E3014EFd9889D62\"],  \"start\": \"2017-07-17T06:37:29\",  \"lt\": \"53.20104215\",  \"ln\": \"44.99835479\"  },  {  \"apikey\": \"                              \",  \"start\": \"2017-05-12T07:21:20\",  \"lt\": \"53.20104215\",  \"ln\": \"44.99835479\"  },  {  \"apikey\": \"                              \",  \"start\": \"2017-05-11T07:14:11\",  \"lt\": \"53.20104215\",  \"ln\": \"44.99835479\"  }  ],  \"info\": \"104645\"  }";
		String jsn = "{  \"lines\": [  {  \"apikey\": \"7649841b7E46112E3014EFd9889D62\",  \"start\": \"2017-07-17T06:37:29\",  \"lt\": 53.20104215,  \"ln\": 44.99835479  },  [\"a\",\"b\",{\"c\":123}],  {  \"apikey\": \"                              \",  \"start\": \"2017-05-12T07:21:20\",  \"lt\": 53.20104215,  \"ln\": 44.99835479  },  {  \"apikey\": \"                              \",  \"start\": \"2017-05-11T07:14:11\",  \"lt\": 53.20104215,  \"ln\": 44.99835479  }  ],  \"info\": \"104645\"  }";
		//System.out.println(jsn);
		Bough.parseJSON(jsn);
		//System.out.println(Bough.parseJSON(jsn).dumpXML());
	}

	@Override
	protected void onPause() {
		//System.out.println("onPause");
		super.onPause();
		//Preferences.save();
		Auxiliary.stopSensorEventListener(this);
	}

	@Override
	protected void onResume() {
		//System.out.println("onResume");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//System.out.println("onCreateOptionsMenu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//System.out.println("onOptionsItemSelected");
		return this.onOptionsItemSelected(item);
	}
}
