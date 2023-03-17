package sweetlife.android10.data.nomenclature;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.nomenclature.Request_CR;

import sweetlife.android10.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CRListAdapter extends ZoomListCursorAdapter implements ITableColumnsNames {

	public CRListAdapter(Context context, Cursor c) {
		super(context, c);

	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		//System.out.println(this.getClass().getCanonicalName()+".bindView");
		CRHolder holder = (CRHolder)row.getTag();

		holder.populateFrom( cursor, getRowTextFontSize() );

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

		View row = LayoutInflater.from(context).inflate(R.layout.row_cr, view, false);

		CRHolder holder=new CRHolder(row);	

		row.setTag(holder);

		return(row);
	}

	public class CRHolder {
		
		private TextView mArticul = null;
		private TextView mNaimenovanie = null;
		private TextView mVendor = null;
		private TextView mPlaceCount = null;
		private TextView mUnit = null;
		private TextView mMinPrice = null;
		private TextView mMaxPrice = null;
		private TextView mMinCount = null;
		private TextView mPrice = null;

		CRHolder(View row) {

			mArticul       = (TextView)row.findViewById(R.id.text_article);
			mNaimenovanie  = (TextView)row.findViewById(R.id.text_nomenclature);
			mVendor        = (TextView)row.findViewById(R.id.text_vendor);
			mPlaceCount    = (TextView)row.findViewById(R.id.text_place_count);
			mUnit          = (TextView)row.findViewById(R.id.text_unit);
			mMinPrice      = (TextView)row.findViewById(R.id.text_minprice);
			mMaxPrice      = (TextView)row.findViewById(R.id.text_maxprice);
			mMinCount      = (TextView)row.findViewById(R.id.text_min_count);
			mPrice         = (TextView)row.findViewById(R.id.text_price);			
		}

		void populateFrom( Cursor cursor, float rowTextFontSize ) {

			mArticul.setText( Request_CR.getArtikul(cursor) );
			mArticul.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mNaimenovanie.setText( Request_CR.getNaimenovanie(cursor) );
			mNaimenovanie.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mVendor.setText( Request_CR.getProizvoditelNaimenovanie(cursor) );
			mVendor.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mPlaceCount.setText( Request_CR.getKoephphicient(cursor) );
			mPlaceCount.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mUnit.setText( Request_CR.getEdinicyIzmereniyaNaimenovanie(cursor) );
			mUnit.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mMinPrice.setText( Request_CR.getMinCena(cursor) );
			mMinPrice.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mMaxPrice.setText( Request_CR.getMaxCena(cursor) );
			mMaxPrice.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );

			mMinCount.setText( Request_CR.getMinNorma(cursor) );
			mMinCount.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );
			
			mPrice.setText( Request_CR.getCenaCR(cursor) );
			mPrice.setTextSize( TypedValue.COMPLEX_UNIT_PX, rowTextFontSize );
		}
	}
}
