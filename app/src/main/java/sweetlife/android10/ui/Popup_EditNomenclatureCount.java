package sweetlife.android10.ui;

import sweetlife.android10.data.common.NomenclatureBasedCountItem;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import sweetlife.android10.R;

public class Popup_EditNomenclatureCount extends BetterPopupWindow {

	private EditText mEditCount;

	private NomenclatureCountHelper mCountHelper = new NomenclatureCountHelper(0, 0);
	private NomenclatureBasedCountItem mNomenclatureBasedCountItem;

	public Popup_EditNomenclatureCount(View anchor,
									   OnCloseListener closeListener,
									   NomenclatureBasedCountItem item) {

		super(anchor, closeListener);

		mNomenclatureBasedCountItem = item;

		InitializeCount(root);

		TextView textName = (TextView) root.findViewById(R.id.text_nomenclature);
		textName.setText(mNomenclatureBasedCountItem.getNomenklaturaNaimenovanie());
	}

	private void InitializeCount(View root) {

		mCountHelper = new NomenclatureCountHelper(mNomenclatureBasedCountItem.getMinNorma(), mNomenclatureBasedCountItem.getKoefMest());

		mEditCount = (EditText) root.findViewById(R.id.edit_count);
		mEditCount.setText(DecimalFormatHelper.format(mNomenclatureBasedCountItem.getKolichestvo()));
		//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
		mEditCount.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				CheckInputCount();

				return false;
			}
		});

		Resources res = anchor.getResources();

		TextView textCount = (TextView) root.findViewById(R.id.text_count);
		textCount.setText(res.getString(R.string.count) + "    " +
				res.getString(R.string.min_quantity) + " " +
				mNomenclatureBasedCountItem.getMinNorma() + "  " +
				res.getString(R.string.koef_mest) + " " +
				mNomenclatureBasedCountItem.getKoefMest());
	}

	@Override
	protected void onCreate() {

		LayoutInflater inflater =
				(LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_edit_count, null);

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

				CheckInputCount();

				mNomenclatureBasedCountItem.setKolichestvo(Double.parseDouble(mEditCount.getText().toString()));

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
