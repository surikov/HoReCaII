package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.ui.Activity_Bid;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.R;
import sweetlife.android10.Settings;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoodStuffListAdapter extends ZoomListArrayAdapter {

	int screenWidth = 0;
	private FoodstuffsData mFoodStuffs;

	public FoodStuffListAdapter(FoodstuffsData foodStuffs, int screenWidth) {
		mFoodStuffs = foodStuffs;
		this.screenWidth = screenWidth;
	}

	@Override
	public int getCount() {
		return mFoodStuffs.getCount();
	}

	@Override
	public Object getItem(int index) {
		return mFoodStuffs.getFoodstuff(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FoodStuffViewHolder holder = null;
		if(convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_foodstuffs, null);
			holder = new FoodStuffViewHolder(convertView, screenWidth);
			convertView.setTag(holder);
		}
		else {
			holder = (FoodStuffViewHolder) convertView.getTag();
		}
		ZayavkaPokupatelya_Foodstaff foodstuff = mFoodStuffs.getFoodstuff(position);
		if(foodstuff.isCRAvailable()) {
			convertView.setBackgroundColor(Color.LTGRAY);
		}
		else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		holder.SetValues(foodstuff);
		return convertView;
	}

	public class FoodStuffViewHolder {

		private TextView mTextNumber = null;
		private TextView mTextArticle = null;
		private TextView mTextNomenclature = null;
		private TextView mTextPlaceCount = null;
		private TextView mTextUnit = null;
		private TextView mTextCount = null;
		private TextView mTextPrice = null;
		private TextView mTextLast = null;
		private TextView mTextPricewithsale = null;
		private TextView mTextSale = null;
		private TextView mTextSaleType = null;
		private TextView mTextAmount = null;
		private TextView mTextMinCount = null;

		FoodStuffViewHolder(View row, int screenWidth) {
			mTextNumber = (TextView) row.findViewById(R.id.text_number);
			mTextArticle = (TextView) row.findViewById(R.id.text_article);
			mTextNomenclature = (TextView) row.findViewById(R.id.text_nomenclature);
			//mTextNomenclature.setTextColor(0xff3300ff);
			mTextPlaceCount = (TextView) row.findViewById(R.id.text_place_count);
			mTextUnit = (TextView) row.findViewById(R.id.text_unit);
			mTextCount = (TextView) row.findViewById(R.id.text_count);
			mTextPrice = (TextView) row.findViewById(R.id.text_price);
			mTextLast = (TextView) row.findViewById(R.id.text_last_price);
			mTextPricewithsale = (TextView) row.findViewById(R.id.text_pricewithsale);
			mTextAmount = (TextView) row.findViewById(R.id.text_amount);
			mTextSale = (TextView) row.findViewById(R.id.text_sale);
			mTextSaleType = (TextView) row.findViewById(R.id.text_sale_type);
			mTextMinCount = (TextView) row.findViewById(R.id.text_min_count);
			/*double w = screenWidth - Auxiliary.tapSize * (0.5 + 1.2 + 0 + 1.7 + 0.9 + 1.1 + 1 + 1.2 + 1.2 + 0.7 + 1 + 1.5+1.4);
			if(w < Auxiliary.tapSize * 2) {
				w = Auxiliary.tapSize * 2;
			}
			mTextNumber.setWidth((int) (Auxiliary.tapSize * 0.5));
			mTextArticle.setWidth((int) (Auxiliary.tapSize * 1.2));
			mTextNomenclature.setWidth((int) w);
			mTextCount.setWidth((int) (Auxiliary.tapSize * 1.7));
			mTextMinCount.setWidth((int) (Auxiliary.tapSize * 0.9));
			mTextPlaceCount.setWidth((int) (Auxiliary.tapSize * 1.1));
			mTextUnit.setWidth((int) (Auxiliary.tapSize * 1));
			mTextPrice.setWidth((int) (Auxiliary.tapSize * 1.2));
			mTextLast.setWidth((int) (Auxiliary.tapSize * 1.2));
			mTextSale.setWidth((int) (Auxiliary.tapSize * 0.7));
			mTextSaleType.setWidth((int) (Auxiliary.tapSize * 1));
			mTextPricewithsale.setWidth((int) (Auxiliary.tapSize * 1.5));
			mTextAmount.setWidth((int) (Auxiliary.tapSize * 1.4));*/
		}

		void SetValues(ZayavkaPokupatelya_Foodstaff foodstuff) {
			//System.out.println(this.getClass().getCanonicalName() + ": SetValues");
			mTextNumber.setText(String.valueOf(foodstuff.getNomerStroki()));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextArticle.setText(foodstuff.getArtikul());
			mTextArticle.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextNomenclature.setText(foodstuff.getNomenklaturaNaimenovanie());
			mTextNomenclature.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextPlaceCount.setText(DecimalFormatHelper.format(foodstuff.getKoefMest()));
			mTextPlaceCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextUnit.setText(foodstuff.getEdinicaIzmereniyaName());
			mTextUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			double count = foodstuff.getKolichestvo();
			mTextCount.setText(DecimalFormatHelper.format(count) + "/" + DecimalFormatHelper.format(count * foodstuff.ves) + "кг");
			mTextCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize() - 5);
			mTextPrice.setText(DecimalFormatHelper.format(foodstuff.getCena()));
			mTextPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			double lastPrice = foodstuff.getLastPrice();
			if(lastPrice > 0) {
				mTextLast.setText(DecimalFormatHelper.format(lastPrice));
				mTextLast.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			}
			else {
				mTextLast.setText("");
			}
			/*if (foodstuff.hasSale() || foodstuff.isCRAvailable()) {
				if (foodstuff.getSkidka() != 0) {
					mTextSale.setText(DecimalFormatHelper.format(foodstuff.getSkidka()));
					mTextSale.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
				}
				else {
					mTextSale.setText("");
				}
				//mTextSaleType.setText(Sales.GetSaleName(foodstuff.getVidSkidki()));
				mTextSaleType.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
				if (foodstuff.getCenaSoSkidkoy() != 0) {
					mTextPricewithsale.setText(DecimalFormatHelper.format(foodstuff.getCenaSoSkidkoy()));
					mTextPricewithsale.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
				}
				else {
					mTextPricewithsale.setText("");
				}
			}
			else {
				mTextSale.setText("");
				mTextSaleType.setText("");
				mTextPricewithsale.setText("");
			}*/
			if(Activity_Bid.hideStatus) {
				mTextPricewithsale.setText(DecimalFormatHelper.format(foodstuff.getCenaSoSkidkoy()));
			}
			else {
				int fakt = (int) (100 * (foodstuff.getCenaSoSkidkoy() - foodstuff.getBasePrice()) / foodstuff.getBasePrice());
				mTextPricewithsale.setText(DecimalFormatHelper.format(foodstuff.getCenaSoSkidkoy()) + "/" + fakt + "%");
				mTextPricewithsale.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize() - 5);
			}
			//if(cursor.getInt(37)>0){
			//mTextArticle.setBackgroundColor(0xff9999ff);
			//}
			mTextAmount.setText(DecimalFormatHelper.format(foodstuff.getSummaSoSkidkoy()));
			mTextAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextMinCount.setText(DecimalFormatHelper.format(foodstuff.getMinNorma()));
			mTextMinCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			mTextSale.setText("" + foodstuff.getSkidka());
			mTextSaleType.setText(foodstuff.getVidSkidki());
			System.out.println("row "+foodstuff.getArtikul()+", ["+foodstuff.getSkidka()+"], ["+foodstuff.getVidSkidki()+"]");
			if(foodstuff.getNomenklaturaNaimenovanie().endsWith("`")) {
				mTextNumber.setBackgroundColor(Settings.colorTop20);
				mTextArticle.setBackgroundColor(Settings.colorTop20);
				mTextNomenclature.setBackgroundColor(Settings.colorTop20);
				mTextPlaceCount.setBackgroundColor(Settings.colorTop20);
				mTextUnit.setBackgroundColor(Settings.colorTop20);
				mTextCount.setBackgroundColor(Settings.colorTop20);
				mTextPrice.setBackgroundColor(Settings.colorTop20);
				mTextLast.setBackgroundColor(Settings.colorTop20);
				mTextPricewithsale.setBackgroundColor(Settings.colorTop20);
				mTextAmount.setBackgroundColor(Settings.colorTop20);
				mTextMinCount.setBackgroundColor(Settings.colorTop20);
				mTextSale.setBackgroundColor(Settings.colorTop20);
				mTextSaleType.setBackgroundColor(Settings.colorTop20);
			}
			else {
				mTextNumber.setBackgroundColor(0xffe3e3e3);
				mTextArticle.setBackgroundColor(0xffe3e3e3);
				mTextNomenclature.setBackgroundColor(0xffe3e3e3);
				mTextPlaceCount.setBackgroundColor(0xffe3e3e3);
				mTextUnit.setBackgroundColor(0xffe3e3e3);
				mTextCount.setBackgroundColor(0xffe3e3e3);
				mTextPrice.setBackgroundColor(0xffe3e3e3);
				mTextLast.setBackgroundColor(0xffe3e3e3);
				mTextPricewithsale.setBackgroundColor(0xffe3e3e3);
				mTextAmount.setBackgroundColor(0xffe3e3e3);
				mTextMinCount.setBackgroundColor(0xffe3e3e3);
				mTextSale.setBackgroundColor(0xffe3e3e3);
				mTextSaleType.setBackgroundColor(0xffe3e3e3);
			}
			//mTextNomenclature.setBackgroundColor(0xff00ffff);
			//Auxiliary.screenWidth(this);
		}
	}
}
