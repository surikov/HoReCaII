package sweetlife.android10.data.returns;

import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.ui.Popup_EditReturnNomenclature;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow.OnCloseListener;

import sweetlife.android10.*;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ReturnNomenclatureListAdapter extends ZoomListArrayAdapter {

	public interface IRemoveItem {

		public void OnRemove(ZayavkaNaVozvrat_Tovary tovar);
	}


	private ReturnsNomenclatureData mReturnsNomenclature;
	private OnCloseListener mOnPopupCloseListener;
	private boolean mEditable;
	private IRemoveItem mOnRemoveItem;

	public ReturnNomenclatureListAdapter(ReturnsNomenclatureData data,
										 OnCloseListener closeListener,
										 boolean editable,
										 IRemoveItem onRemove) {

		mReturnsNomenclature = data;
		mOnPopupCloseListener = closeListener;
		mEditable = editable;
		mOnRemoveItem = onRemove;
	}

	@Override
	public int getCount() {

		return mReturnsNomenclature.getCount();
	}

	@Override
	public Object getItem(int index) {

		return mReturnsNomenclature.getNomenclature(index);
	}

	@Override
	public long getItemId(int index) {

		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ReturnsNomenclatureViewHolder holder = null;

		if (convertView == null) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_returns_nomenclature, null);

			if (mEditable) {

				convertView.setOnClickListener(mOnClick);
			}

			holder = new ReturnsNomenclatureViewHolder(convertView);

			convertView.setTag(holder);
		} else {

			holder = (ReturnsNomenclatureViewHolder) convertView.getTag();
		}

		ZayavkaNaVozvrat_Tovary tovar = mReturnsNomenclature.getNomenclature(position);

		holder.SetValues(tovar);

		return convertView;
	}

	private OnClickListener mOnClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Popup_EditReturnNomenclature popup = new Popup_EditReturnNomenclature(arg0,
					mOnPopupCloseListener,
					((ReturnsNomenclatureViewHolder) arg0.getTag()).getTovar());

			popup.showLikeQuickAction();
		}
	};

	public class ReturnsNomenclatureViewHolder {

		private ImageView mImageRemove = null;
		private TextView mTextNumber = null;
		private TextView mTextArticle = null;
		private TextView mTextNomenclature = null;
		private TextView mTextCount = null;
		private TextView mTextBill = null;
		private TextView mTextBillDate = null;
		private Spinner mSpnReasons = null;

		private ZayavkaNaVozvrat_Tovary mTovar = null;

		ReturnsNomenclatureViewHolder(View row) {

			mImageRemove = (ImageView) row.findViewById(R.id.image_remove);
			mTextNumber = (TextView) row.findViewById(R.id.text_n);
			mTextArticle = (TextView) row.findViewById(R.id.text_article);
			mTextNomenclature = (TextView) row.findViewById(R.id.text_nomenclature);
			mTextCount = (TextView) row.findViewById(R.id.text_count);
			mTextBill = (TextView) row.findViewById(R.id.text_bill);
			mTextBillDate = (TextView) row.findViewById(R.id.text_bill_date);

			mSpnReasons = (Spinner) row.findViewById(R.id.spinner_reason);

			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					row.getContext(), android.R.layout.simple_spinner_item, ZayavkaNaVozvrat_Tovary.ZayavkaNaVozvratPrichina);

			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			mSpnReasons.setAdapter(arrayAdapter);
		}

		void SetValues(ZayavkaNaVozvrat_Tovary tovar) {

			mTovar = tovar;

			mImageRemove.setTag(tovar);
			mImageRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {

					mOnRemoveItem.OnRemove((ZayavkaNaVozvrat_Tovary) view.getTag());
				}
			});

			mTextNumber.setText(String.valueOf(tovar.getNomerStroki()));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextArticle.setText(tovar.getArtikul());
			mTextArticle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextNomenclature.setText(tovar.getNomenklaturaNaimenovanie());
			mTextNomenclature.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			if (tovar.getKolichestvo() == 0) {

				mTextCount.setText("");
			} else {

				mTextCount.setText(DecimalFormatHelper.format3(tovar.getKolichestvo()));
			}
			mTextCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextBill.setText(tovar.getNomerNakladnoy());
			mTextBill.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextBillDate.setText(tovar.getDataNakladnoyUIString());
			mTextBillDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			int position = 0;
			int p = tovar.getPrichina();
			if (p >= 100) p = p - 100;
			//System.out.println("tovar.getPrichina "+p);

			switch (p) {
			/*case ZayavkaNaVozvrat_Tovary.REASON_BRAK:
				position = 1;
				break;*/
				case ZayavkaNaVozvrat_Tovary.REASON_NEUSTROILOKACHESTVO:
					position = 1;
					break;
				case ZayavkaNaVozvrat_Tovary.REASON_PERESORT:
					position = 2;
					break;
				case ZayavkaNaVozvrat_Tovary.REASON_CREDIT_NOTA:
					position = 3;
					break;
				case ZayavkaNaVozvrat_Tovary.REASON_KOROTKIE_SROKI:
					position = 4;
					break;
				case ZayavkaNaVozvrat_Tovary.REASON_OSHIBKA_PRI_ZAKAZE:
					position = 5;
					break;


			}
			//System.out.println("SetValues "+tovar.getPrichina()+": "+position);
			mSpnReasons.setSelection(position);
			mSpnReasons.setTag(tovar);
			mSpnReasons.setSelected(true);
			mSpnReasons.setEnabled(false);//mEditable);
			/*mSpnReasons.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> spinner, View arg1,
						int position, long id) {

					ZayavkaNaVozvrat_Tovary tovar = (ZayavkaNaVozvrat_Tovary)spinner.getTag();
					
					int reason = 0;
					switch(position) {
					case 1:
						reason = ZayavkaNaVozvrat_Tovary.REASON_BRAK;
						break;
					case 2:
						reason = ZayavkaNaVozvrat_Tovary.REASON_KOROTKIE_SROKI;
						break;
					case 3:
						reason = ZayavkaNaVozvrat_Tovary.REASON_OSHIBKA_ASSORTIMENTA;
						break;
					case 4:
						reason = ZayavkaNaVozvrat_Tovary.REASON_NET_V_KLASTERE;
						break;
					case 5:
						reason = ZayavkaNaVozvrat_Tovary.REASON_PERESORT;
						break;
					case 6:
						reason = ZayavkaNaVozvrat_Tovary.REASON_CREDIT_NOTA;
						break;
					}
					
					tovar.setPrichina(reason);
				}
				@Override public void onNothingSelected(AdapterView<?> arg0) {}

			});*/

		}

		public ZayavkaNaVozvrat_Tovary getTovar() {

			return mTovar;
		}
	}
}
