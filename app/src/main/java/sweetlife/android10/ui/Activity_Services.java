package sweetlife.android10.ui;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.nomenclature.ServicesCursorListAdapter;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Services extends Activity_Base implements ITableColumnsNames {
	private Cursor mServicesCursor;
	private EditText mEditCount;
	private EditText mEditServiceContent;
	ServicesCursorListAdapter mServicesListAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_services);
		setTitle(R.string.title_service);
		mEditCount = (EditText) findViewById(R.id.edit_count);
		mEditServiceContent = (EditText) findViewById(R.id.edit_service_content);
		mServicesCursor = RequestDataBaseData();
		mServicesListAdapter = new ServicesCursorListAdapter(this, mServicesCursor);
		ListView servicesList = (ListView) findViewById(R.id.list_services);
		servicesList.setAdapter(mServicesListAdapter);
		servicesList.setOnTouchListener(this);
		servicesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mEditCount.setText("1");
				mServicesListAdapter.setSelectedPosition(position);
			}
		});
		((Button) findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mEditCount.getText().length() == 0 || mEditCount.getText().toString().compareToIgnoreCase("0") == 0) {
					CreateErrorDialog(R.string.msg_error_count).show();
					return;
				}
				if (mServicesListAdapter.getSelectedPosition() == -1) {
					CreateErrorDialog(R.string.msg_error_nomenclature_id).show();
					return;
				}
				Intent resultIntent = new Intent();
				mServicesCursor.moveToPosition(mServicesListAdapter.getSelectedPosition());
				resultIntent.putExtra(NOMENCLATURE_ID, Hex.encodeHex(mServicesCursor.getBlob(1)));
				resultIntent.putExtra(NAIMENOVANIE, mServicesCursor.getString(3));
				resultIntent.putExtra(ARTIKUL, mServicesCursor.getString(2));
				resultIntent.putExtra(CENA, mServicesCursor.getDouble(4));
				resultIntent.putExtra(SODERGANIE, mEditServiceContent.getText().toString());
				resultIntent.putExtra(KOLICHESTVO, new Double(mEditCount.getText().toString()));
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}
	private Cursor RequestDataBaseData() {
		/*String sqlStr = "select n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie], c.Cena "+
				"from Nomenklatura n "+
				"inner join CenyNomenklaturySklada c on c.[Nomenklatura] = n._IDRRef "+
				"where n.[Usluga] = x'01'";*/
		String sqlStr = "select"// 
				+ "\n\t n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie]"//
				+ "\n\t ,ifnull(FiksirovannyeCeny.FixCena,(select max(Cena) from CenyNomenklaturySklada"// 					
				+ "\n\t  			where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef]"// 			
				+ "\n\t 			and Period=(select max(Period) from CenyNomenklaturySklada 			"//
				+ "\n\t 			where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))"//	
				+ "\n\t 			) as cena"//
				+ "\n\t from Nomenklatura n "//
				+ "\n\t	join (select '"// 
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) //
				+ "' as dataOtgruzki,"//
				+ ApplicationHoreca.getInstance().getClientInfo().getID()//
				+ " as kontragent,"//
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				+ " as polzovatel"//
				+ ") parameters					" //
				+ "\n\t join kontragenty k on k._idrref=parameters.kontragent"// 
				+ "\n\t left join FiksirovannyeCeny on Nomenklatura=n.[_IDRRef]"// 
				+ "\n\t 	and (PoluchatelSkidki=k.golovnoykontragent or PoluchatelSkidki=k._idrref)"// 
				+ "\n\t 	and Period=(select max(Period) from FiksirovannyeCeny"//		
				+ "\n\t  					where Nomenklatura=n.[_IDRRef]"// 
				+ "\n\t 					and (PoluchatelSkidki=k.golovnoykontragent or PoluchatelSkidki=k._idrref)"// 
				+ "\n\t 					and date(period)<=date(parameters.dataOtgruzki)"// 
				+ "\n\t 					and date(dataokonchaniya)>=date(parameters.dataOtgruzki)"//
				+ "\n\t 					)"//
				+ "\n\t where n.[Usluga] = x'01'";
		//System.out.println(this.getClass().getCanonicalName() + " request " + sqlStr);
		return mDB.rawQuery(sqlStr, null);
	}
}
