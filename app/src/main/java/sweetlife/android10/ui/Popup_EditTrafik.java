package sweetlife.android10.ui;

import java.util.Calendar;

import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.data.orders.ZayavkaPokupatelya_Trafik;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;
import sweetlife.android10.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class Popup_EditTrafik extends BetterPopupWindow {
	private EditText mEditCount;
	private EditText mEditDate;
	private ZayavkaPokupatelya_Trafik mTrafik;
	private Calendar mDate;
	private NomenclatureCountHelper mCountHelper = new NomenclatureCountHelper(0, 0);
	CheckBox spr;

	public Popup_EditTrafik(View anchor, OnCloseListener closeListener, ZayavkaPokupatelya_Trafik trafik) {
		super(anchor, closeListener);
		mTrafik = trafik;
		InitializeCount(root);
		InitializeDate(root);
		TextView textName = (TextView) root.findViewById(R.id.text_nomenclature);
		textName.setText(mTrafik.getNomenklaturaNaimenovanie());
		if (mTrafik != null) {
			spr = (CheckBox) root.findViewById(R.id.chk_vetspravka);
			spr.setVisibility(View.VISIBLE);
			//System.out.println(mTrafik);
			if (mTrafik.vetSpravka) {
				spr.setChecked(true);
			} else {
				spr.setChecked(false);
			}
		}
	}

	private void InitializeCount(View root) {
		mCountHelper = new NomenclatureCountHelper(mTrafik.getMinNorma(), mTrafik.getKoefMest());
		mEditCount = (EditText) root.findViewById(R.id.edit_count);
		mEditCount.setText(DecimalFormatHelper.format(mTrafik.getKolichestvo()));
		//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_focus));
		Resources res = anchor.getResources();
		TextView textCount = (TextView) root.findViewById(R.id.text_count);
		textCount.setText(res.getString(R.string.count) + "    " + res.getString(R.string.min_quantity) + " " + mTrafik.getMinNorma() + "  " + res.getString(R.string.koef_mest) + " "
				+ mTrafik.getKoefMest());
	}

	private void InitializeDate(View root) {
		mEditDate = (EditText) root.findViewById(R.id.edit_date);
		mEditDate.setOnClickListener(mDateClick);
		mDate = Calendar.getInstance();
		if (mTrafik.getData() != null) {
			mDate.setTime(mTrafik.getData());
		}
		mEditDate.setText(DateTimeHelper.UIDateString(mDate.getTime()));
		((Button) root.findViewById(R.id.btn_date)).setOnClickListener(mDateClick);
	}

	private OnClickListener mDateClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mDate.set(year, monthOfYear, dayOfMonth);
					mEditDate.setText(DateTimeHelper.UIDateString(mDate.getTime()));
				}
			};
			new DatePickerDialog(arg0.getContext(), dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	@Override
	protected void onCreate() {
		LayoutInflater inflater = (LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_edit_trafik, null);
		setButtons(root);
		setContentView(root);
	}

	private void setButtons(View root) {
		Button btn1 = (Button) root.findViewById(R.id.btn_1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "1");
			}
		});
		Button btn2 = (Button) root.findViewById(R.id.btn_2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "2");
			}
		});
		Button btn3 = (Button) root.findViewById(R.id.btn_3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "3");
			}
		});
		Button btn4 = (Button) root.findViewById(R.id.btn_4);
		btn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "4");
			}
		});
		Button btn5 = (Button) root.findViewById(R.id.btn_5);
		btn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "5");
			}
		});
		Button btn6 = (Button) root.findViewById(R.id.btn_6);
		btn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "6");
			}
		});
		Button btn7 = (Button) root.findViewById(R.id.btn_7);
		btn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "7");
			}
		});
		Button btn8 = (Button) root.findViewById(R.id.btn_8);
		btn8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "8");
			}
		});
		Button btn9 = (Button) root.findViewById(R.id.btn_9);
		btn9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "9");
			}
		});
		Button btn0 = (Button) root.findViewById(R.id.btn_0);
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText(mEditCount.getText().toString() + "0");
			}
		});
		((Button) root.findViewById(R.id.btn_slash)).setEnabled(false);
		Button btnPoint = (Button) root.findViewById(R.id.btn_point);
		btnPoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputString = mEditCount.getText().toString();
				if (!inputString.contains(".")) {
					mEditCount.setText(mEditCount.getText().toString() + ".");
				}
			}
		});
		Button btnDel = (Button) root.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchString = mEditCount.getText().toString();
				if (searchString.length() != 0) {
					mEditCount.setText(searchString.substring(0, searchString.length() - 1));
				}
			}
		});
		Button btnClear = (Button) root.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditCount.setText("");
			}
		});
		Button btnEnter = (Button) root.findViewById(R.id.btn_enter);
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mEditCount.getText().toString();
				CheckInputCount();
				mTrafik.setKolichestvo(Double.parseDouble(mEditCount.getText().toString()));
				text = mEditDate.getText().toString();
				mTrafik.vetSpravka = spr.isChecked();
				if (text.length() != 0) {
					mTrafik.setData(mDate.getTime());
				} else {
					mTrafik.setData(null);
				}
				dismiss(0);
			}
		});
	}

	private void CheckInputCount() {
		double count = 0.00D;
		if (mEditCount.getText().toString().length() != 0) {
			count = Double.parseDouble(mEditCount.getText().toString().replace(',', '.'));
		}
		mEditCount.setText(DecimalFormatHelper.format(mCountHelper.ReCalculateCount(count)));
	}
}
