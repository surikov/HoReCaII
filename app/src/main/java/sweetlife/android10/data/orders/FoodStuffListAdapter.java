package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.NomenclatureBasedItem;
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

import java.util.Comparator;

public class FoodStuffListAdapter extends ZoomListArrayAdapter {

	int screenWidth = 0;
	public FoodstuffsData mFoodStuffs;

	public FoodStuffListAdapter(FoodstuffsData foodStuffs, int screenWidth) {
		mFoodStuffs = foodStuffs;
		this.screenWidth = screenWidth;
		this.sortListByMode(0);
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
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_foodstuffs, null);
			holder = new FoodStuffViewHolder(convertView, screenWidth);
			convertView.setTag(holder);
		} else {
			holder = (FoodStuffViewHolder) convertView.getTag();
		}
		ZayavkaPokupatelya_Foodstaff foodstuff = mFoodStuffs.getFoodstuff(position);
		if (foodstuff.isCRAvailable()) {
			convertView.setBackgroundColor(Color.LTGRAY);
		} else {
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

		public float getRowTextFontSize() {
			return 19;
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
			mTextUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize()-5);
			double count = foodstuff.getKolichestvo();
			mTextCount.setText(DecimalFormatHelper.format(count) + "/" + DecimalFormatHelper.format(count * foodstuff.requestVes()) + "кг");
			mTextCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize() - 5);
			mTextPrice.setText(DecimalFormatHelper.format(foodstuff.getCena()));
			mTextPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			double lastPrice = foodstuff.getLastPrice();
			if (lastPrice > 0) {
				mTextLast.setText(DecimalFormatHelper.format(lastPrice));
				mTextLast.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize());
			} else {
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
			if (Activity_Bid.hideNacenkaStatus) {
				mTextPricewithsale.setText(DecimalFormatHelper.format(foodstuff.getCenaSoSkidkoy())+ "р.");
			} else {
				//int fakt = (int) (100 * (foodstuff.getCenaSoSkidkoy() - foodstuff.getBasePrice()) / foodstuff.getBasePrice());
				double fakt = 100 * (foodstuff.getCenaSoSkidkoy() - foodstuff.getBasePrice()) / foodstuff.getBasePrice();
				double val=(foodstuff.getCenaSoSkidkoy() - foodstuff.getBasePrice())/foodstuff.requestVes();
				mTextPricewithsale.setText(DecimalFormatHelper.format(foodstuff.getCenaSoSkidkoy())+ "р."
						+"\n" + String.format("%.2f", fakt) + "%"
						+"\n" + String.format("%.2f", val) + "р/кг"
				);
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
			//System.out.println("row " + foodstuff.getArtikul() + ", [" + foodstuff.getSkidka() + "], [" + foodstuff.getVidSkidki() + "]");
			if (foodstuff.getNomenklaturaNaimenovanie().endsWith("`")) {
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
			} else {
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
				//mTextSaleType.setBackgroundColor(0xffe3e3e3);
				mTextSaleType.setBackgroundColor(0xffe3e3e3);
			}
			//mTextSaleType.setTextColor(0xff000000);

			//mTextNomenclature.setBackgroundColor(0xff00ffff);
			//Auxiliary.screenWidth(this);
			mTextNumber.setTextColor(0xff000000);
			mTextArticle.setTextColor(0xff000000);
			mTextNomenclature.setTextColor(0xff000000);
			mTextPlaceCount.setTextColor(0xff000000);
			mTextUnit.setTextColor(0xff000000);
			mTextCount.setTextColor(0xff000000);
			mTextPrice.setTextColor(0xff000000);
			mTextLast.setTextColor(0xff000000);
			mTextPricewithsale.setTextColor(0xff000000);
			mTextAmount.setTextColor(0xff000000);
			mTextMinCount.setTextColor(0xff000000);
			mTextSale.setTextColor(0xff000000);
			mTextSaleType.setTextColor(0xff000000);
		}
	}

	public final static int sortByName = 0;
	public final static int sortByCount = 1;
	public final static int sortByPrice = 2;
	public final static int sortByLastPrice = 3;
	public final static int sortByNacenka = 4;
	public final static int sortByNum = 5;
	Comparator<NomenclatureBasedItem> comparatorNumOrder = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;
				return zz1.getNomerStroki() - zz2.getNomerStroki();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};
	Comparator<NomenclatureBasedItem> comparatorNacenka = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;

				double fakt1 = (zz1.getCenaSoSkidkoy() - zz1.getBasePrice()) / zz1.getBasePrice();
				double fakt2 = (zz2.getCenaSoSkidkoy() - zz2.getBasePrice()) / zz2.getBasePrice();

				return (int) (1000 * (fakt1 - fakt2));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};
	Comparator<NomenclatureBasedItem> comparatorLastPrice = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;
				return (int) (1000 * (zz1.getLastPrice() - zz2.getLastPrice()));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};
	Comparator<NomenclatureBasedItem> comparatorPrice = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;
				return (int) (1000 * (zz1.getBasePrice() - zz2.getBasePrice()));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};
	Comparator<NomenclatureBasedItem> comparatorCount = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;
				return (int) (1000 * (zz1.getKolichestvo() - zz2.getKolichestvo()));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};
	Comparator<NomenclatureBasedItem> comparatorName = new Comparator<NomenclatureBasedItem>() {
		public int compare(NomenclatureBasedItem o1, NomenclatureBasedItem o2) {
			try {
				ZayavkaPokupatelya_Foodstaff zz1 = (ZayavkaPokupatelya_Foodstaff) o1;
				ZayavkaPokupatelya_Foodstaff zz2 = (ZayavkaPokupatelya_Foodstaff) o2;
				return zz1.getNomenklaturaNaimenovanie().compareTo(zz2.getNomenklaturaNaimenovanie());
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return 0;
		}
	};

	public void sortListByMode(int sortMode) {
		if (sortMode == FoodStuffListAdapter.sortByName) {
			mFoodStuffs.mNomenclaureList.sort(comparatorName);
		} else {
			if (sortMode == FoodStuffListAdapter.sortByCount) {
				mFoodStuffs.mNomenclaureList.sort(comparatorCount);
			} else {
				if (sortMode == FoodStuffListAdapter.sortByPrice) {
					mFoodStuffs.mNomenclaureList.sort(comparatorPrice);
				} else {
					if (sortMode == FoodStuffListAdapter.sortByLastPrice) {
						mFoodStuffs.mNomenclaureList.sort(comparatorLastPrice);
					} else {
						if (sortMode == FoodStuffListAdapter.sortByNacenka) {
							mFoodStuffs.mNomenclaureList.sort(comparatorNacenka);
						} else {
							if (sortMode == FoodStuffListAdapter.sortByNum) {
								mFoodStuffs.mNomenclaureList.sort(comparatorNumOrder);
							}
						}
					}
				}
			}
		}
	}
}
