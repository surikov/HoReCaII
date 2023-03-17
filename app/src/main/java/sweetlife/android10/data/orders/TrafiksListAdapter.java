package sweetlife.android10.data.orders;

import java.util.Date;

import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import sweetlife.android10.R;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrafiksListAdapter extends ZoomListArrayAdapter {

	private TraficsData mTraficsData;

	public TrafiksListAdapter( TraficsData trafiksData) {

		mTraficsData = trafiksData;
	}

	@Override
	public int getCount() {

		return mTraficsData.getCount();
	}

	@Override
	public Object getItem( int index ) {

		return mTraficsData.getTrafik(index);
	}

	@Override
	public long getItemId(int index) {

		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FoodStuffViewHolder holder = null;

		if (convertView == null) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_trafiks, null );

			holder = new FoodStuffViewHolder( convertView );

			convertView.setTag(holder);
		} 
		else {

			holder = (FoodStuffViewHolder)convertView.getTag();
		}

		ZayavkaPokupatelya_Trafik trafik = mTraficsData.getTrafik(position);

		holder.SetValues( trafik );

		return convertView;
	}

	public class FoodStuffViewHolder {

		private TextView mTextNumber = null;
		private TextView mTextArticle = null;
		private TextView mTextNomenclature = null;
		private TextView mTextPlaceCount = null;
		private TextView mTextUnit = null;
		private TextView mTextCount = null;
		private TextView mTextMinCount = null;
		private TextView mTextDate = null;

		FoodStuffViewHolder( View row ) {

			mTextNumber = (TextView)row.findViewById(R.id.text_number);
			mTextArticle = (TextView)row.findViewById(R.id.text_article);
			mTextNomenclature = (TextView)row.findViewById(R.id.text_nomenclature);
			mTextPlaceCount = (TextView)row.findViewById(R.id.text_place_count);
			mTextUnit = (TextView)row.findViewById(R.id.text_unit);
			mTextCount = (TextView)row.findViewById(R.id.text_count);
			mTextMinCount     = (TextView)row.findViewById(R.id.text_min_count);
			mTextDate     = (TextView)row.findViewById(R.id.text_date);
		}

		void SetValues( ZayavkaPokupatelya_Trafik trafik ) {

			mTextNumber.setText( String.valueOf(trafik.getNomerStroki()) );
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextArticle.setText( trafik.getArtikul() );
			mTextArticle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			if(trafik.vetSpravka){
				mTextNomenclature.setText( trafik.getNomenklaturaNaimenovanie() +" +вет.справка");
			}else{
				mTextNomenclature.setText( trafik.getNomenklaturaNaimenovanie() );
			}
			
			mTextNomenclature.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextPlaceCount.setText( DecimalFormatHelper.format(trafik.getKoefMest()) );
			mTextPlaceCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextUnit.setText( trafik.getEdinicaIzmereniyaName() );
			mTextUnit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextCount.setText( DecimalFormatHelper.format(trafik.getKolichestvo()) );
			mTextCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());


			mTextMinCount.setText( DecimalFormatHelper.format(trafik.getMinNorma()) );
			mTextMinCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			Date date = trafik.getData();

			if(date != null) { 

				mTextDate.setText( DateTimeHelper.UIDateString(trafik.getData()) );
				mTextDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
			}
		}
	}
}
