package sweetlife.android10.data.route;

import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.Request_ClientsList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import sweetlife.android10.R;

public class RouteListAdapter extends ZoomListCursorAdapter {
	Request_ClientsList m_Helper;

	public RouteListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		RouteHolder holder = (RouteHolder) row.getTag();
		holder.populateFrom(cursor, getRowTextFontSize());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {
		View row = LayoutInflater.from(context).inflate(R.layout.row_constractorslist, view, false);
		RouteHolder holder = new RouteHolder(row);
		row.setTag(holder);
		return (row);
	}

	public class RouteHolder {
		private TextView name = null;
		private TextView code = null;
		private ImageView Mo = null;
		private ImageView Th = null;
		private ImageView Wd = null;
		private ImageView Ch = null;
		private ImageView Fr = null;
		private ImageView St = null;

		RouteHolder(View row) {
			name = (TextView) row.findViewById(R.id.constractor_name);
			code = (TextView) row.findViewById(R.id.code);
			Mo = (ImageView) row.findViewById(R.id.mo);
			Th = (ImageView) row.findViewById(R.id.th);
			Wd = (ImageView) row.findViewById(R.id.wd);
			Ch = (ImageView) row.findViewById(R.id.ch);
			Fr = (ImageView) row.findViewById(R.id.fr);
			St = (ImageView) row.findViewById(R.id.st);
		}

		void populateFrom(Cursor cursor, float rowTextFontSize) {
			//System.out.println("rowTextFontSize "+rowTextFontSize);
			//System.out.println("TypedValue.COMPLEX_UNIT_PX "+TypedValue.COMPLEX_UNIT_PX);
			name.setText(Request_ClientsList.getClientName(cursor));
			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			code.setText(Request_ClientsList.getClientCode(cursor));
			code.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			if (Request_ClientsList.getOpenContactsCount(cursor) > 0) {
				if (Request_ClientsList.getContactsCount(cursor) - Request_ClientsList.getOpenContactsCount(cursor) == 0) {
					name.setTextColor(Color.BLACK);
					code.setTextColor(Color.BLACK);
				} else {
					name.setTextColor(Color.BLUE);
					code.setTextColor(Color.BLUE);
				}
			} else {
				name.setTextColor(Color.RED);
				code.setTextColor(Color.RED);
			}
			if (Request_ClientsList.getMo(cursor) == 1) {
				Mo.setImageResource(android.R.drawable.checkbox_on_background);
			} else {
				Mo.setImageResource(android.R.drawable.checkbox_off_background);
			}
			if (Request_ClientsList.getTh(cursor) == 1) {
				Th.setImageResource(android.R.drawable.checkbox_on_background);
			} else {
				Th.setImageResource(android.R.drawable.checkbox_off_background);
			}
			if (Request_ClientsList.getWd(cursor) == 1) {
				Wd.setImageResource(android.R.drawable.checkbox_on_background);
			} else {
				Wd.setImageResource(android.R.drawable.checkbox_off_background);
			}
			if (Request_ClientsList.getCh(cursor) == 1) {
				Ch.setImageResource(android.R.drawable.checkbox_on_background);
			} else {
				Ch.setImageResource(android.R.drawable.checkbox_off_background);
			}
			if (Request_ClientsList.getFr(cursor) == 1) {
				Fr.setImageResource(android.R.drawable.checkbox_on_background);
			} else
				Fr.setImageResource(android.R.drawable.checkbox_off_background);
			if (Request_ClientsList.getSt(cursor) == 1)
				St.setImageResource(android.R.drawable.checkbox_on_background);
			else {
				St.setImageResource(android.R.drawable.checkbox_off_background);
			}
			if (Request_ClientsList.getClientVisitDone(cursor)) {
				name.setBackgroundColor(0x3300ff00);
			} else {
				if (Request_ClientsList.getClientVisitStartedNotFinished(cursor)) {
					name.setBackgroundColor(0x330000ff);
				} else {
					name.setBackgroundColor(0x00000000);
				}
			}

			if (Request_ClientsList.getClientName(cursor).startsWith("(не в маршруте)")) {
				name.setBackgroundColor(0x00000000);
				name.setTextColor(0xff999999);
				code.setTextColor(0xff999999);
				Mo.setVisibility(View.INVISIBLE);
				Th.setVisibility(View.INVISIBLE);
				Wd.setVisibility(View.INVISIBLE);
				Ch.setVisibility(View.INVISIBLE);
				Fr.setVisibility(View.INVISIBLE);
				St.setVisibility(View.INVISIBLE);
			} else {
				Mo.setVisibility(View.VISIBLE);
				Th.setVisibility(View.VISIBLE);
				Wd.setVisibility(View.VISIBLE);
				Ch.setVisibility(View.VISIBLE);
				Fr.setVisibility(View.VISIBLE);
				St.setVisibility(View.VISIBLE);
			}
		}
	}
}
