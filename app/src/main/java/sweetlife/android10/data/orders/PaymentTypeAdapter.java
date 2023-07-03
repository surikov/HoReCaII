package sweetlife.android10.data.orders;

import java.util.ArrayList;

import sweetlife.android10.log.*;
import sweetlife.android10.R;
import sweetlife.android10.supervisor.Cfg;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class PaymentTypeAdapter extends ArrayAdapter {
	ArrayList<PaymentTypeInfo> mData = new ArrayList<PaymentTypeInfo>();
	int mPosition;
	SQLiteDatabase mDB;

	public PaymentTypeAdapter(SQLiteDatabase db, Context context, int resource, String contactID) {
		super(context, resource);
		mDB = db;
		UpdatePaymentTypes(contactID);
	}

	public int GetItemPositionByID(String paymentType) {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).getID().compareToIgnoreCase(paymentType) == 0) {
				return i;
			}
		}
		return -1;
	}

	public void UpdatePaymentTypes(String contactID) {
		mData.clear();
		String sqlStr = "select"
				+ " case when (Podrazdelenie = x'00000000000000000000000000000000' or Podrazdelenie = x'00') then 0 else 1 end Podrazdelenie"
				+ ",ProcentPredoplaty from DogovoryKontragentov_strip where [PometkaUdaleniya]=x'00' and _IDRref = "
				//+",ProcentPredoplaty from DogovoryKontragentov_strip where 1=2 and _IDRref = "
				+ contactID;
		//System.out.println(sqlStr);
		Cursor paymentTypeCursor = mDB.rawQuery(sqlStr, null);
		if (paymentTypeCursor.moveToFirst()) {
			int Podrazdelenie = paymentTypeCursor.getInt(0);
			int ProcentPredoplaty = paymentTypeCursor.getInt(1);
			//String REFnalID = "x'B8A71648BE9E99D3492DAB9257E5D773'";
			//String REFbezNalID = "x'838D51B55D9490754FB77F3D5FE02C1E'";
			//String REFtovCheckID = "x'AB638B5B4E5DAB774EDC12B2542FFFED'";
			if ((Podrazdelenie == 0) && (ProcentPredoplaty == 0)) {
				LogHelper.debug(this.getClass().getCanonicalName() + " это договор оплаты по факту");
				mData.add(new PaymentTypeInfo(Cfg.tip_nalichnie, getContext().getString(R.string.cash)));
				mData.add(new PaymentTypeInfo(Cfg.tip_tovcheck, getContext().getString(R.string.cash_memo)));
			} else {
				if ((Podrazdelenie == 0) && (ProcentPredoplaty == 100)) {
					LogHelper.debug(this.getClass().getCanonicalName() + " это договор по предоплате");
					mData.add(new PaymentTypeInfo(Cfg.tip_nalichnie, getContext().getString(R.string.cash)));
					mData.add(new PaymentTypeInfo(Cfg.tip_beznal, getContext().getString(R.string.noncash)));
				} else {
					LogHelper.debug(this.getClass().getCanonicalName() + " это договор с отсрочкой");
					//mData.add(new PaymentTypeInfo(Cfg.tip_nalichnie, getContext().getString(R.string.cash)));
					mData.add(new PaymentTypeInfo(Cfg.tip_beznal, getContext().getString(R.string.noncash)));
				}
			}
			paymentTypeCursor.close();
		}
		if (mData.size() > 1) {
			mData.add(0, new PaymentTypeInfo("x'00'", ""));
		}
		/*mData.clear();
		String sqlStr = "select tog.TipOplaty" //
				+ " from DogovoryKontragentov dk "// 
				+ " inner join"//
				+ "    (select"//
				+ "        dk._IDRref"//
				+ "        , case"//
				+ "           when  dk.Podrazdelenie = x'00000000000000000000000000000000' and dk.ProcentPredoplaty = 0 then '0,2' "//
				+ "           when dk.Podrazdelenie = x'00000000000000000000000000000000' and dk.ProcentPredoplaty = 100 then '0,1' "//
				+ "           else case"//
				+ "              when yfl.Poryadok = 0 then '0,1'"//
				+ "              else '0'"//
				+ "              end"//
				+ "           end"//
				+ "          TipOplaty "//
				+ "     from DogovoryKontragentov dk "//
				+ "     inner join Kontragenty k on dk.Vladelec = k._IDRref "//
				+ "     inner join YurPhizLico yfl on yfl._IDRref = k.[YurPhizLico] "//
				+ "    ) tog on dk._IDRref = tog._IDRref "// 
				+ " where dk._IDRref = " + contactID;
		Cursor paymentTypeCursor = mDB.rawQuery(sqlStr, null);
		if (paymentTypeCursor.moveToFirst()) {
			PaymentTypeInfo data;
			if (paymentTypeCursor.getString(0).contains("0")) {
				data = new PaymentTypeInfo("x'B8A71648BE9E99D3492DAB9257E5D773'", getContext().getString(R.string.cash));
				mData.add(data);
			}
			if (paymentTypeCursor.getString(0).contains("1")) {
				data = new PaymentTypeInfo("x'838D51B55D9490754FB77F3D5FE02C1E'", getContext().getString(R.string.noncash));
				mData.add(data);
			}
			if (paymentTypeCursor.getString(0).contains("2")) {
				data = new PaymentTypeInfo("x'AB638B5B4E5DAB774EDC12B2542FFFED'", getContext().getString(R.string.cash_memo));
				mData.add(data);
			}
			paymentTypeCursor.close();
		}
		if (mData.size() > 1) {
			mData.add(0, new PaymentTypeInfo("x'00'", ""));
		}*/
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, null);
		}
		view = (TextView) convertView.findViewById(android.R.id.text1);
		view.setText(mData.get(position).getName());
		mPosition = position;
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_elem, null);
		}
		view = (TextView) convertView.findViewById(android.R.id.text1);
		view.setText(mData.get(position).getName());
		return convertView;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	public PaymentTypeInfo GetSelectedItem() {
		return mData.get(mPosition);
	}

	public boolean RemoveEmptyItem() {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).getName().compareTo("") == 0) {
				mData.remove(i);
				mPosition--;
				return true;
			}
		}
		return false;
	}
}
