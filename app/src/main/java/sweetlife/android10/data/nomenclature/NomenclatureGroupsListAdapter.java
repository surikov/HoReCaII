package sweetlife.android10.data.nomenclature;

import sweetlife.android10.database.nomenclature.Request_NomeclatureSimple;

import sweetlife.android10.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorTreeAdapter;
import android.widget.TextView;

public class NomenclatureGroupsListAdapter extends CursorTreeAdapter {
	private SQLiteDatabase mDB;

	public NomenclatureGroupsListAdapter(Cursor cursor, Context context, boolean autoRequery, SQLiteDatabase db) {
		super(cursor, context, autoRequery);
		mDB = db;
	}
	@Override
	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
		TextView textNomenclature = (TextView) view.findViewById(R.id.text);
		textNomenclature.setText(Request_NomeclatureSimple.getNaimenovanie(cursor));
	}
	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
		TextView textNomenclature = (TextView) view.findViewById(R.id.text);
		textNomenclature.setText(Request_NomeclatureSimple.getNaimenovanie(cursor));
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		return Request_NomeclatureSimple.RequestNomenlatureGroupsWithParent(mDB, Request_NomeclatureSimple.getIDRRef(groupCursor));
	}
	@Override
	protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		View row = LayoutInflater.from(context).inflate(R.layout.list_child_element, parent, false);
		return (row);
	}
	@Override
	protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		View row = LayoutInflater.from(context).inflate(R.layout.list_group_element, parent, false);
		return (row);
	}
}
