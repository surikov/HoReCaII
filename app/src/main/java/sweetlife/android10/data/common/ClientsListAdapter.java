package sweetlife.android10.data.common;

import sweetlife.android10.database.Request_ClientsList;

//import sweetlife.android10.*;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ClientsListAdapter extends CursorAdapter {

	@SuppressWarnings("deprecation")
	public ClientsListAdapter(Context context, Cursor c) {

		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		ClientHolder holder = (ClientHolder) row.getTag();

		holder.populateFrom(cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);

		View row = inflater.inflate(sweetlife.android10.R.layout.list_element, parent, false);

		ClientHolder holder = new ClientHolder(row);

		row.setTag(holder);

		return (row);
	}

	private class ClientHolder {

		private TextView textName = null;

		ClientHolder(View row) {

			textName = (TextView) row.findViewById(sweetlife.android10.R.id.text);
		}

		void populateFrom(Cursor cursor) {

			textName.setText(Request_ClientsList.getClientName(cursor));
		}
	}
}

