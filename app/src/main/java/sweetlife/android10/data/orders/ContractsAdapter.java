package sweetlife.android10.data.orders;

import java.util.ArrayList;

import sweetlife.android10.data.common.ContractInfo;
import sweetlife.android10.database.Request_Contracts;

import sweetlife.android10.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class ContractsAdapter extends ArrayAdapter {
	
	private ArrayList<ContractInfo> mData = new ArrayList<ContractInfo>();
	
	private int mPosition = 0;
	String clientID="";
	private SQLiteDatabase mDB;

	public ContractsAdapter( SQLiteDatabase db, Context context, int resource, String clientID ) {
		super(context,resource );
		this.clientID=clientID;
		mDB = db;
		
		Request_Contracts requestContracts = new Request_Contracts( mDB, clientID );
		
		mData = requestContracts.getContractsList();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = null;

		if (convertView == null) {
			
			convertView = (View)LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, null );
		}

		view = (TextView) convertView.findViewById(android.R.id.text1);
		view.setText( mData.get(position).getNaimenovanie() );
		//LogHelper.debug(this.getClass().getCanonicalName()+ ".getView "+position+": "+mData.get(position).getNaimenovanie());
		if( 
				mData.get(position).isClosed() ) {
			
			view.setTextColor(Color.RED);
		}
		else {
			
			view.setTextColor(Color.BLACK);
		}
		//view.setTextColor(Color.MAGENTA);
		mPosition = position;

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		TextView view = null;

		if (convertView == null) {
			
			convertView = (View)LayoutInflater.from(getContext()).inflate(R.layout.spinner_elem, null );
		}

		view = (TextView) convertView.findViewById(android.R.id.text1);
		view.setText( mData.get(position).getNaimenovanie() );
		//LogHelper.debug(this.getClass().getCanonicalName()+ ".getDropDownView "+position+": "+mData.get(position).getNaimenovanie());
		if( mData.get(position).isClosed() ) {
			
			view.setTextColor(Color.RED);
		}
		else {
			
			view.setTextColor(Color.BLACK);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		
		return mData.size();
	}

	public ContractInfo getSelectedItem() {
		//LogHelper.debug(this.getClass().getCanonicalName()+" getSelectedItem "+mPosition +" of "+mData.size()+" for "+clientID);
		return mData.get(mPosition);
	}
	
	public void setPosition( int position ) {
		
		mPosition = position;
	}


	@Override
	public Object getItem( int position ) {
		
		return mData.get(position);
	}
}