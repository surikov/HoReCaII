package sweetlife.android10.data.nomenclature;

import reactive.ui.Auxiliary;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.ZoomListCursorAdapter;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.*;
import sweetlife.android10.Settings;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.*;

public class NomenclaturaAdapter extends ZoomListCursorAdapter implements ITableColumnsNames {
	private boolean mIsCRAvailable = true;

	int ww=0;
	public NomenclaturaAdapter(Context context, Cursor c, OnSelectedPositionChangeListener listener, boolean isCRAvailable,int w) {
		super(context, c, listener);
		mIsCRAvailable = isCRAvailable;
		ww = w;
	}
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		NomenclatureHolder holder = (NomenclatureHolder) row.getTag();
		holder.populateFrom(cursor, getRowTextFontSize());
		View list_row = (View) row.findViewById(R.id.row);
		if (cursor.getCount() == 1) {
			setSelectedPosition(0);
		}
		if (getSelectedPosition() == cursor.getPosition()) {
			list_row.setBackgroundColor(Color.rgb(16, 109, 206));
		}
		else {
			list_row.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {
		View row = LayoutInflater.from(context).inflate(R.layout.row_nomenclature, view, false);
		NomenclatureHolder holder = new NomenclatureHolder(row,ww,artikuls,sklad);
		row.setTag(holder);
		return (row);
	}

	public class NomenclatureHolder {
		private TextView mArticul = null;
		private TextView mNaimenovanie = null;
		private TextView mVendor = null;
		private TextView mPlaceCount = null;
		private TextView mUnit = null;
		private TextView mPrice = null;
		private TextView mPriceWithSkidka = null;
		private TextView mSale = null;
		private TextView mSaleType = null;
		private TextView mMinCount = null;
		private TextView mMinPrice = null;
		private TextView mMaxPrice = null;
		private TextView mLastPrice = null;
		Vector<String>artikuls;Vector<String>sklad;
		NomenclatureHolder(View row, int screenWidth,Vector<String>artikuls,Vector<String>sklad) {
			this.artikuls=artikuls;
			this.sklad=sklad;
			mArticul = (TextView) row.findViewById(R.id.text_article);
			mNaimenovanie = (TextView) row.findViewById(R.id.text_nomenclature);
			mVendor = (TextView) row.findViewById(R.id.text_vendor);
			mPlaceCount = (TextView) row.findViewById(R.id.text_place_count);
			mUnit = (TextView) row.findViewById(R.id.text_unit);
			mPrice = (TextView) row.findViewById(R.id.text_price);
			mPriceWithSkidka = (TextView) row.findViewById(R.id.text_pricewithsale);
			mSale = (TextView) row.findViewById(R.id.text_sale);
			mSaleType = (TextView) row.findViewById(R.id.text_sale_type);
			mMinCount = (TextView) row.findViewById(R.id.text_min_count);
			mMinPrice = (TextView) row.findViewById(R.id.text_minprice);
			mMaxPrice = (TextView) row.findViewById(R.id.text_maxprice);
			mLastPrice = (TextView) row.findViewById(R.id.text_last_price);

			double w = screenWidth - Auxiliary.tapSize * (1.2+0+2+0.9+1.1+1.2+1.4+0.7+1.0+1.4+1.4+1.4+1.4);
			if(w < Auxiliary.tapSize * 2) {
				w = Auxiliary.tapSize * 2;
			}
			mArticul.setWidth((int) (Auxiliary.tapSize * 1.2));
			mNaimenovanie.setWidth((int) w);
			mVendor.setWidth((int)(Auxiliary.tapSize * 2));
			mMinCount.setWidth((int) (Auxiliary.tapSize * 0.9) );
			mPlaceCount.setWidth((int) (Auxiliary.tapSize * 1.1));
			mUnit.setWidth((int) (Auxiliary.tapSize * 1.2));
			mPrice.setWidth((int) (Auxiliary.tapSize * 1.4));
			mSale.setWidth((int) (Auxiliary.tapSize * 0.7));
			mSaleType.setWidth((int) (Auxiliary.tapSize * 1.0));
			mLastPrice.setWidth((int) (Auxiliary.tapSize * 1.4));
			mMinPrice.setWidth((int) (Auxiliary.tapSize * 1.4));
			mMaxPrice.setWidth((int) (Auxiliary.tapSize * 1.4));
			mLastPrice.setWidth((int) (Auxiliary.tapSize * 1.4));
		}
		String findOstatok(String a){
			String r="?";
			for(int i=0;i<this.artikuls.size();i++){
				if(a.equals(this.artikuls.get(i))){
					r=this.sklad.get(i);
					break;
				}
			}
			return r;
		}
		void populateFrom(Cursor cursor, float rowTextFontSize) {
			//String clr=Request_NomenclatureBase.getArtikulColor(cursor);
			mArticul.setText(Request_NomenclatureBase.getArtikul(cursor));
			//System.out.println(mArticul.getText());
			//mArticul.setText(clr);
			mArticul.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			//mArticul.setBackgroundColor(Request_NomenclatureBase.getArtikulColor(cursor));
			//mNaimenovanie.setText(Request_NomenclatureBase.getNaimenovanie(cursor)+", доступно "+this.findOstatok(Request_NomenclatureBase.getArtikul(cursor)));
			mNaimenovanie.setText(Request_NomenclatureBase.getNaimenovanie(cursor));
			mNaimenovanie.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			//System.out.println(cursor.getString(2)+": "+cursor.getString(3)+": "+cursor.getInt(37));
			mVendor.setText(Request_NomenclatureBase.getProizvoditelNaimenovanie(cursor));
			mVendor.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mPlaceCount.setText(Request_NomenclatureBase.getKoephphicient(cursor));
			mPlaceCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mUnit.setText(Request_NomenclatureBase.getEdinicyIzmereniyaNaimenovanie(cursor));
			mUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			//mPrice.setText("?"+Request_NomenclatureBase.getCena(cursor));
			if(sweetlife.android10.ui.Activity_Bid.hideNacenkaStatus) {
				mPrice.setText(Request_NomenclatureBase.getCena(cursor));
			}
			else {
				double cena=cursor.getDouble(cursor.getColumnIndex(ITableColumnsNames.CENA));
				double basePrice=cursor.getDouble(cursor.getColumnIndex(ITableColumnsNames.BASE_PRICE));

				//int fakt = (int) (100 * (cena - foodstuff.getBasePrice()) / foodstuff.getBasePrice());
				int fakt = (int) (100 * (cena - basePrice) / basePrice);
				mPrice.setText(DecimalFormatHelper.format(cena) + "/" + fakt + "%");
				mPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize() - 5);
			}

			//mPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowTextFontSize);
			String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
			if (vidSkidki.length() != 0) {
				if (mPriceWithSkidka != null) {
					mPriceWithSkidka.setText(Request_NomenclatureBase.getCenaSoSkidkoy(cursor));
					mPriceWithSkidka.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				}
				String skidka = Request_NomenclatureBase.getSkidka(cursor);
				if (skidka != null && skidka.compareToIgnoreCase("0") != 0) { //Fixed price
					mSale.setText(skidka);
				}
				else {
					mSale.setText("");
				}
				mSale.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				mSaleType.setText(vidSkidki);
				mSaleType.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			}
			else {
				if (mPriceWithSkidka != null) {
					mPriceWithSkidka.setText("");
				}
				mSale.setText("");
				mSaleType.setText("");
			}
			mMinCount.setText(Request_NomenclatureBase.getMinNorma(cursor));
			mMinCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			if (mIsCRAvailable) {
				mMinPrice.setText(Request_NomenclatureBase.getMinCena(cursor));
				mMinPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
				mMaxPrice.setText(Request_NomenclatureBase.getMaxCena(cursor));
				mMaxPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			}
			else {
				mMinPrice.setText("");
				mMaxPrice.setText("");
			}
			String lastPrice = DecimalFormatHelper.format(Request_NomenclatureBase.getLastPrice(cursor));
			//System.out.println(lastPrice);
			if (mLastPrice != null) {
				mLastPrice.setText(lastPrice);
				mLastPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			}
			if (getSelectedPosition() == cursor.getPosition()) {
				mArticul.setBackgroundColor(Color.rgb(16, 109, 206));
				mNaimenovanie.setBackgroundColor(Color.rgb(16, 109, 206));
				mVendor.setBackgroundColor(Color.rgb(16, 109, 206));
				mPlaceCount.setBackgroundColor(Color.rgb(16, 109, 206));
				mUnit.setBackgroundColor(Color.rgb(16, 109, 206));
				mPrice.setBackgroundColor(Color.rgb(16, 109, 206));
				if (mPriceWithSkidka != null) {
					mPriceWithSkidka.setBackgroundColor(Color.rgb(16, 109, 206));
				}
				mSale.setBackgroundColor(Color.rgb(16, 109, 206));
				mSaleType.setBackgroundColor(Color.rgb(16, 109, 206));
				mMinCount.setBackgroundColor(Color.rgb(16, 109, 206));
				mMinPrice.setBackgroundColor(Color.rgb(16, 109, 206));
				mMaxPrice.setBackgroundColor(Color.rgb(16, 109, 206));
				mLastPrice.setBackgroundColor(Color.rgb(16, 109, 206));
			}
			else {
				//System.out.println(cursor.getColumnName(37)+"-------------------");
				//if (cursor.getInt(37) > 0) {//mustListId > 0
				if (cursor.getInt(cursor.getColumnIndex("mustListId")) > 0) {//mustListId > 0
					mArticul.setBackgroundColor(Settings.colorTop20);
					mNaimenovanie.setBackgroundColor(Settings.colorTop20);
					mVendor.setBackgroundColor(Settings.colorTop20);
					mPlaceCount.setBackgroundColor(Settings.colorTop20);
					mUnit.setBackgroundColor(Settings.colorTop20);
					mPrice.setBackgroundColor(Settings.colorTop20);
					if (mPriceWithSkidka != null) {
						mPriceWithSkidka.setBackgroundColor(Settings.colorTop20);
					}
					mSale.setBackgroundColor(Settings.colorTop20);
					mSaleType.setBackgroundColor(Settings.colorTop20);
					mMinCount.setBackgroundColor(Settings.colorTop20);
					mMinPrice.setBackgroundColor(Settings.colorTop20);
					mMaxPrice.setBackgroundColor(Settings.colorTop20);
					mLastPrice.setBackgroundColor(Settings.colorTop20);
				}
				else {
					mArticul.setBackgroundColor(0xffe3e3e3);
					mNaimenovanie.setBackgroundColor(0xffe3e3e3);
					mVendor.setBackgroundColor(0xffe3e3e3);
					mPlaceCount.setBackgroundColor(0xffe3e3e3);
					mUnit.setBackgroundColor(0xffe3e3e3);
					mPrice.setBackgroundColor(0xffe3e3e3);
					if (mPriceWithSkidka != null) {
						mPriceWithSkidka.setBackgroundColor(0xffe3e3e3);
					}
					mSale.setBackgroundColor(0xffe3e3e3);
					mSaleType.setBackgroundColor(0xffe3e3e3);
					mMinCount.setBackgroundColor(0xffe3e3e3);
					mMinPrice.setBackgroundColor(0xffe3e3e3);
					mMaxPrice.setBackgroundColor(0xffe3e3e3);
					mLastPrice.setBackgroundColor(0xffe3e3e3);
					if (Request_NomenclatureBase.isOlderThen2week(cursor)) {
						mArticul.setBackgroundColor(Settings.colorOlder);
					}
					else {
						mArticul.setBackgroundColor(0xffe3e3e3);
					}
					if (Request_NomenclatureBase.nacenkaBolshe25(cursor)) {
						mNaimenovanie.setBackgroundColor(Settings.colorNacenka25);
					}
					else {
						mNaimenovanie.setBackgroundColor(0xffe3e3e3);
					}
				}
			}
		}
	}
}
