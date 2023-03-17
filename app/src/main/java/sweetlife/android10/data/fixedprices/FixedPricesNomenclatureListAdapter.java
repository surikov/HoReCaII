package sweetlife.android10.data.fixedprices;

import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.utils.DecimalFormatHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sweetlife.android10.R;

public class FixedPricesNomenclatureListAdapter extends ZoomListArrayAdapter {

	private FixedPricesNomenclatureData mFixedPricesNomenclature;
	
	public FixedPricesNomenclatureListAdapter( FixedPricesNomenclatureData data) {

		mFixedPricesNomenclature = data;
	}

	@Override
	public int getCount() {

		return mFixedPricesNomenclature.getCount();
	}

	@Override
	public Object getItem( int index ) {

		return mFixedPricesNomenclature.getNomenclature(index);
	}

	@Override
	public long getItemId(int index) {

		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FixedPricesNomenclatureViewHolder holder = null;

		if (convertView == null) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fixed_prices_nomenclatura, null );
			
			holder = new FixedPricesNomenclatureViewHolder( convertView );

			convertView.setTag(holder);
		} 
		else {

			holder = (FixedPricesNomenclatureViewHolder)convertView.getTag();
		}

		ZayavkaNaSkidki_TovaryPhiksCen tovar = mFixedPricesNomenclature.getNomenclature(position);

		holder.SetValues( tovar );
		
		return convertView;
	}

	public class FixedPricesNomenclatureViewHolder {

		private TextView mTextNumber = null;
		private TextView mTextArticle = null;
		private TextView mTextNomenclature = null;
		private TextView mTextCena = null;
		private TextView mTextTovarooborot = null;

		FixedPricesNomenclatureViewHolder( View row ) {

			mTextNumber = (TextView)row.findViewById(R.id.text_n);
			mTextArticle = (TextView)row.findViewById(R.id.text_article);
			mTextNomenclature = (TextView)row.findViewById(R.id.text_nomenclature);
			mTextCena = (TextView)row.findViewById(R.id.text_cena);
			mTextTovarooborot = (TextView)row.findViewById(R.id.text_tovarooborot);	
		}

		void SetValues( ZayavkaNaSkidki_TovaryPhiksCen tovar ) {

			mTextNumber.setText( String.valueOf(tovar.getNomerStroki()) );
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextArticle.setText( tovar.getArtikul() );
			mTextArticle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextNomenclature.setText( tovar.getNomenklaturaNaimenovanie() );
			mTextNomenclature.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			if( tovar.getCena() == 0) {
			
				mTextCena.setText("");
			}
			else {
				
				mTextCena.setText( DecimalFormatHelper.format(tovar.getCena()) );
			}
			mTextCena.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			if( tovar.getObyazatelstva() == 0) {
				
				mTextTovarooborot.setText("");
			}
			else {
				
				mTextTovarooborot.setText( DecimalFormatHelper.format(tovar.getObyazatelstva()) );
			}
			mTextTovarooborot.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
		}
	}
}