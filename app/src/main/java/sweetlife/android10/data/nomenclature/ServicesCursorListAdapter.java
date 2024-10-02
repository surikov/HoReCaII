package sweetlife.android10.data.nomenclature;

import sweetlife.android10.data.common.ZoomListCursorAdapter;

import sweetlife.android10.*;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServicesCursorListAdapter extends ZoomListCursorAdapter{

	public ServicesCursorListAdapter(Context context, Cursor c ) {
		super(context, c);

	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		ServiceHolder holder= (ServiceHolder)row.getTag();

		holder.populateFrom( cursor );

		View list_row = (View)row.findViewById(R.id.row);

		if( getSelectedPosition() == cursor.getPosition() ){

			list_row.setBackgroundColor(Color.rgb(16, 109, 206));
		}
		else {

			list_row.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_service_list, view, false);

		ServiceHolder holder = new ServiceHolder(row);	

		row.setTag(holder);

		return(row);
	}

	static class ServiceHolder {
		
		private TextView articul    = null;
		private TextView name       = null;
		private TextView price      = null;
		private TextView unit       = null;

		ServiceHolder(View row) {

			articul    = (TextView)row.findViewById(R.id.text_articul);
			name       = (TextView)row.findViewById(R.id.text_naimenovanie);
			price      = (TextView)row.findViewById(R.id.text_price);
			unit       = (TextView)row.findViewById(R.id.text_unit);

		}

		void populateFrom( Cursor cursor ) {

			articul.setText(cursor.getString(2));
			name.setText(cursor.getString(3));
			price.setText( ((Double)cursor.getDouble(4)).toString() );
			unit.setText("шт");
		}
	}
}