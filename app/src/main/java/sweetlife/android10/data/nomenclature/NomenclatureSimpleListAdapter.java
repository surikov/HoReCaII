package sweetlife.android10.data.nomenclature;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sweetlife.android10.R;

public class NomenclatureSimpleListAdapter extends ZoomListCursorAdapter implements ITableColumnsNames {

	public NomenclatureSimpleListAdapter(Context context, Cursor c) {
		super(context, c);

	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {

		NomenclatureHolder holder=(NomenclatureHolder)row.getTag();

		holder.populateFrom( cursor, getRowTextFontSize() );

		View list_row = (View)row.findViewById(R.id.row);

		if( cursor.getCount() == 1 ) {

			setSelectedPosition(0);
		}

		if( getSelectedPosition() == cursor.getPosition() ) {

			list_row.setBackgroundColor(0xffffffff);//Color.rgb(16, 109, 206));
		}
		else {

			list_row.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_nomenclature_simple, view, false);

		NomenclatureHolder holder=new NomenclatureHolder(row);	

		row.setTag(holder);

		return(row);
	}

	public class NomenclatureHolder {

		private TextView mArticul = null;
		private TextView mNaimenovanie = null;
		private TextView mVendor = null;

		NomenclatureHolder(View row) {

			mArticul       = (TextView)row.findViewById(R.id.text_article);
			mNaimenovanie  = (TextView)row.findViewById(R.id.text_nomenclature);
			mVendor        = (TextView)row.findViewById(R.id.text_vendor);
		}

		void populateFrom( Cursor cursor, float rowTextFontSize ) {

			mArticul.setText( Request_NomenclatureBase.getArtikul(cursor) );
			mArticul.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mArticul.setTextColor(0xff000000);

			mNaimenovanie.setText( Request_NomenclatureBase.getNaimenovanie(cursor) );
			mNaimenovanie.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mNaimenovanie.setTextColor(0xff000000);

			mVendor.setText( Request_NomenclatureBase.getProizvoditelNaimenovanie(cursor) );
			mVendor.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mVendor.setTextColor(0xff000000);
		}
	}
}
