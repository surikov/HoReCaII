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
import sweetlife.android10.Settings;
import java.util.*;

public class NomenclatureByGroupListAdapter extends ZoomListCursorAdapter implements ITableColumnsNames {
	private boolean mIsCRAvailable = true;
	public NomenclatureByGroupListAdapter(Context context, Cursor c, boolean isCRAvailable) {
		super(context, c);
		mIsCRAvailable = isCRAvailable;
	}
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		//System.out.println(this.getClass().getCanonicalName() + ".bindView");
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
		View row = LayoutInflater.from(context).inflate(R.layout.row_nomenclature_by_group, view, false);
		NomenclatureHolder holder = new NomenclatureHolder(row,artikuls,sklad);
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
		private TextView mPriceWithSale = null;
		private TextView mMinCount = null;
		private TextView mMinPrice = null;
		private TextView mMaxPrice = null;
		Vector<String>artikuls;Vector<String>sklad;
		NomenclatureHolder(View row,Vector<String>artikuls,Vector<String>sklad) {
			this.artikuls=artikuls;
			this.sklad=sklad;
			mArticul = (TextView) row.findViewById(R.id.text_article);
			mNaimenovanie = (TextView) row.findViewById(R.id.text_nomenclature);
			mVendor = (TextView) row.findViewById(R.id.text_vendor);
			mPlaceCount = (TextView) row.findViewById(R.id.text_place_count);
			mUnit = (TextView) row.findViewById(R.id.text_unit);
			mPrice = (TextView) row.findViewById(R.id.text_price);
			mPriceWithSale = (TextView) row.findViewById(R.id.text_pricewithsale);
			mMinCount = (TextView) row.findViewById(R.id.text_min_count);
			mMinPrice = (TextView) row.findViewById(R.id.text_minprice);
			mMaxPrice = (TextView) row.findViewById(R.id.text_maxprice);
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
			//System.out.println(this.getClass().getCanonicalName() + " populateFrom");
			mArticul.setText(Request_NomenclatureBase.getArtikul(cursor));
			mArticul.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			//mNaimenovanie.setText(Request_NomenclatureBase.getNaimenovanie(cursor)+", доступно "+this.findOstatok(Request_NomenclatureBase.getArtikul(cursor)));
			mNaimenovanie.setText(Request_NomenclatureBase.getNaimenovanie(cursor));
			mNaimenovanie.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mVendor.setText(Request_NomenclatureBase.getProizvoditelNaimenovanie(cursor));
			mVendor.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mPlaceCount.setText(Request_NomenclatureBase.getKoephphicient(cursor));
			mPlaceCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			mUnit.setText(Request_NomenclatureBase.getEdinicyIzmereniyaNaimenovanie(cursor));
			mUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			if(sweetlife.android10.ui.Activity_Bid.hideNacenkaStatus) {
				mPrice.setText(Request_NomenclatureBase.getCena(cursor));
				mPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			}else{
				double cena=cursor.getDouble(cursor.getColumnIndex(ITableColumnsNames.CENA));
				double basePrice=cursor.getDouble(cursor.getColumnIndex(ITableColumnsNames.BASE_PRICE));

				//int fakt = (int) (100 * (cena - foodstuff.getBasePrice()) / foodstuff.getBasePrice());
				int fakt = (int) (100 * (cena - basePrice) / basePrice);
				mPrice.setText(sweetlife.android10.utils.DecimalFormatHelper.format(cena) + "/" + fakt + "%");
				mPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowTextFontSize() - 5);
			}




			if (Request_NomenclatureBase.getVidSkidki(cursor).length() != 0) {
				mPriceWithSale.setText(Request_NomenclatureBase.getCenaSoSkidkoy(cursor));
				mPriceWithSale.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowTextFontSize);
			}
			else {
				mPriceWithSale.setText("");
			}
			mPriceWithSale.setText(Request_NomenclatureBase.getCenaSoSkidkoy(cursor));
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
			if (getSelectedPosition() == cursor.getPosition()) {
				mArticul.setBackgroundColor(Color.rgb(16, 109, 206));
				mNaimenovanie.setBackgroundColor(Color.rgb(16, 109, 206));
				mVendor.setBackgroundColor(Color.rgb(16, 109, 206));
				mPlaceCount.setBackgroundColor(Color.rgb(16, 109, 206));
				mUnit.setBackgroundColor(Color.rgb(16, 109, 206));
				mPrice.setBackgroundColor(Color.rgb(16, 109, 206));
				mPriceWithSale.setBackgroundColor(Color.rgb(16, 109, 206));
				mMinCount.setBackgroundColor(Color.rgb(16, 109, 206));
				mMinPrice.setBackgroundColor(Color.rgb(16, 109, 206));
				mMaxPrice.setBackgroundColor(Color.rgb(16, 109, 206));
			}
			else {
				//if (cursor.getInt(37) > 0) {//mustListId > 0
				if (cursor.getInt(cursor.getColumnIndex("mustListId")) > 0) {//mustListId > 0
					mArticul.setBackgroundColor(Settings.colorTop20);
					//System.out.println("change"+cursor.getString(2)+": "+cursor.getString(3)+": "+cursor.getInt(37));
					mNaimenovanie.setBackgroundColor(Settings.colorTop20);
					mVendor.setBackgroundColor(Settings.colorTop20);
					mPlaceCount.setBackgroundColor(Settings.colorTop20);
					mUnit.setBackgroundColor(Settings.colorTop20);
					mPrice.setBackgroundColor(Settings.colorTop20);
					mPriceWithSale.setBackgroundColor(Settings.colorTop20);
					mMinCount.setBackgroundColor(Settings.colorTop20);
					mMinPrice.setBackgroundColor(Settings.colorTop20);
					mMaxPrice.setBackgroundColor(Settings.colorTop20);
				}
				else {
					mArticul.setBackgroundColor(0xffe3e3e3);
					mNaimenovanie.setBackgroundColor(0xffe3e3e3);
					mVendor.setBackgroundColor(0xffe3e3e3);
					mPlaceCount.setBackgroundColor(0xffe3e3e3);
					mUnit.setBackgroundColor(0xffe3e3e3);
					mPrice.setBackgroundColor(0xffe3e3e3);
					mPriceWithSale.setBackgroundColor(0xffe3e3e3);
					mMinCount.setBackgroundColor(0xffe3e3e3);
					mMinPrice.setBackgroundColor(0xffe3e3e3);
					mMaxPrice.setBackgroundColor(0xffe3e3e3);
					if (Request_NomenclatureBase.nacenkaBolshe25(cursor)) {
						mNaimenovanie.setBackgroundColor(Settings.colorNacenka25);
					}
					else {
						mNaimenovanie.setBackgroundColor(0xffe3e3e3);
					}
					if (Request_NomenclatureBase.isOlderThen2week(cursor)) {
						mArticul.setBackgroundColor(Settings.colorOlder);
					}
					else {
						mArticul.setBackgroundColor(0xffe3e3e3);
					}
				}
			}
		}
	}
}
