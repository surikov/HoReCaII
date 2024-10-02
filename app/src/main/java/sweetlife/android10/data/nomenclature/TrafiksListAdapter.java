package sweetlife.android10.data.nomenclature;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.nomenclature.Request_Trafiks;
import sweetlife.android10.utils.DecimalFormatHelper;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sweetlife.android10.*;
import sweetlife.android10.*;

public class TrafiksListAdapter extends ZoomListCursorAdapter implements ITableColumnsNames {

	public TrafiksListAdapter(Context context, Cursor c) {
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

			list_row.setBackgroundColor(Color.rgb(16, 109, 206));
		}
		else {

			list_row.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {

		View row = LayoutInflater.from(context).inflate(R.layout.row_trafik, view, false);

		NomenclatureHolder holder=new NomenclatureHolder(row);	

		row.setTag(holder);

		return(row);
	}

	public class NomenclatureHolder {

		private TextView mArticul = null;
		private TextView mNaimenovanie = null;
		private TextView mVendor = null;
		private TextView mPlaceCount = null;
		private TextView mUnit = null;
		private TextView mMinCount = null;

		NomenclatureHolder(View row) {

			mArticul       = (TextView)row.findViewById(R.id.text_article);
			mNaimenovanie  = (TextView)row.findViewById(R.id.text_nomenclature);
			mVendor        = (TextView)row.findViewById(R.id.text_vendor);
			mPlaceCount    = (TextView)row.findViewById(R.id.text_place_count);
			mUnit          = (TextView)row.findViewById(R.id.text_unit);
			mMinCount      = (TextView)row.findViewById(R.id.text_min_count);
		}

		void populateFrom( Cursor cursor, float rowTextFontSize ) {

			mArticul.setText( Request_Trafiks.getArtikul(cursor) );
			mArticul.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mNaimenovanie.setText( Request_Trafiks.getNaimenovanie(cursor) );
			mNaimenovanie.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			//mVendor.setText( Request_Trafiks.getProizvoditelNaimenovanie(cursor) );
			mVendor.setText( Request_Trafiks.getProizvoditelNaimenovanie(cursor) //
					+" примерно "+Math.round(100.0*cursor.getDouble(cursor.getColumnIndex("MaxCena")))/100.0+"руб"
					);
			//mVendor.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			mVendor.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);

			mPlaceCount.setText( DecimalFormatHelper.format(Request_Trafiks.getKoephphicient(cursor)) );
			mPlaceCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mUnit.setText( Request_Trafiks.getEdinicyIzmereniyaNaimenovanie(cursor) );
			mUnit.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);

			mMinCount.setText( DecimalFormatHelper.format(Request_Trafiks.getMinNorma(cursor)) );
			mMinCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
		}
	}
}