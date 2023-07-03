package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.ZoomListArrayAdapter;
import sweetlife.android10.utils.DecimalFormatHelper;

import sweetlife.android10.R;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServicesListAdapter extends ZoomListArrayAdapter {

	private ServicesData mServices;

	public ServicesListAdapter(ServicesData services) {

		mServices = services;
	}

	@Override
	public int getCount() {

		return mServices.getCount();
	}

	@Override
	public Object getItem(int index) {

		return mServices.getService(index);
	}

	@Override
	public long getItemId(int index) {

		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ServicesViewHolder holder = null;

		if (convertView == null) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_services, null);

			holder = new ServicesViewHolder(convertView);

			convertView.setTag(holder);
		} else {

			holder = (ServicesViewHolder) convertView.getTag();
		}

		ZayavkaPokupatelya_Service service = mServices.getService(position);

		holder.SetValues(service.getNomerStroki(),
				service.getNomenklaturaNaimenovanie(),
				service.getSoderzhanie(),
				service.getKolichestvo(),
				service.getCena(),
				service.getSumma());

		return convertView;

	}

	public class ServicesViewHolder {

		private TextView mTextNumber = null;
		private TextView mTextNomenclature = null;
		private TextView mTextServicecontent = null;
		private TextView mTextCount = null;
		private TextView mTextPrice = null;
		private TextView mTextAmount = null;

		ServicesViewHolder(View row) {
			mTextNumber = (TextView) row.findViewById(R.id.text_number);
			mTextNomenclature = (TextView) row.findViewById(R.id.text_nomenclature);
			mTextServicecontent = (TextView) row.findViewById(R.id.text_servicecontent);
			mTextCount = (TextView) row.findViewById(R.id.text_count);
			mTextPrice = (TextView) row.findViewById(R.id.text_price);
			mTextAmount = (TextView) row.findViewById(R.id.text_amount);
		}

		public void SetValues(int nomerStroki, String nomenklaturaNaimenovanie,
							  String soderzhanie, double kolichestvo, double cena, double summa) {

			mTextNumber.setText(String.valueOf(nomerStroki));
			mTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextNomenclature.setText(nomenklaturaNaimenovanie);
			mTextNomenclature.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextServicecontent.setText(soderzhanie);
			mTextServicecontent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextCount.setText(String.valueOf((int) kolichestvo));
			mTextCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextPrice.setText(DecimalFormatHelper.format(cena));
			mTextPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());

			mTextAmount.setText(DecimalFormatHelper.format(summa));
			mTextAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
		}
	}

	;
}
