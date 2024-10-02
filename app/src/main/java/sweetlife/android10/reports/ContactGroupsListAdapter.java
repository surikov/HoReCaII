package sweetlife.android10.reports;

import sweetlife.android10.database.Request_ContactGroups;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import sweetlife.android10.*;


public class ContactGroupsListAdapter extends CursorAdapter{

	@SuppressWarnings("deprecation")
	public ContactGroupsListAdapter(Context context, Cursor c) {

		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		ContactGroupHolder holder=(ContactGroupHolder)row.getTag();

		holder.populateFrom( cursor );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);

		View row = inflater.inflate(R.layout.list_element, parent, false);

		ContactGroupHolder holder=new ContactGroupHolder(row);	

		row.setTag(holder);

		return(row);
	}

	private class ContactGroupHolder {

		private TextView textName = null;

		ContactGroupHolder( View row ) {

			textName = (TextView)row.findViewById(R.id.text);
		}

		void populateFrom(Cursor cursor ) {

			textName.setText( Request_ContactGroups.getContactsGroupName(cursor) );
		}
	}
}
