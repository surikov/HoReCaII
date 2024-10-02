package sweetlife.android10.ui;

import java.util.Calendar;

import sweetlife.android10.data.returns.ZayavkaNaVozvrat_Tovary;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;

import sweetlife.android10.*;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


public class Popup_EditReturnNomenclature extends BetterPopupWindow {

	public static final int BILL_NUMBER_WITH_DASH_LENGHT = 10;

	private EditText mEditForInput;
	CheckBox edit_tovarniy_vid;
	private EditText mEditBillNumber;
	private EditText mEditBillDate;
	private EditText mEditCount;

	private ZayavkaNaVozvrat_Tovary mTovar;
	private Calendar mBillDate;

	public Popup_EditReturnNomenclature(View anchor,
										OnCloseListener closeListener,
										ZayavkaNaVozvrat_Tovary tovar) {

		super(anchor, closeListener);

		mTovar = tovar;
		mBillDate = mTovar.getDataNakladnoy() == null ? Calendar.getInstance() : mTovar.getDataNakladnoy();

		InitializeCount(root);
		InitializeBillNumber(root);
		InitializeBillDate(root);

		mEditForInput = mEditCount;

		TextView textName = (TextView) root.findViewById(R.id.text_nomenclature);
		textName.setText(mTovar.getNomenklaturaNaimenovanie());

		edit_tovarniy_vid = (CheckBox) root.findViewById(R.id.edit_tovarniy_vid);
		if (mTovar.getPrichina() >= 100) {
			edit_tovarniy_vid.setChecked(true);
		} else {
			edit_tovarniy_vid.setChecked(false);
		}
		//edit_tovarniy_vid.setSelected(true);
		//System.out.println("edit_tovarniy_vid "+edit_tovarniy_vid.isSelected()+": "+edit_tovarniy_vid);
	}

	private void InitializeCount(View root) {


		mEditCount = (EditText) root.findViewById(R.id.edit_count);
		mEditCount.setText(mTovar.getKolichestvo() == 0 ? "" : DecimalFormatHelper.format(mTovar.getKolichestvo()));
		//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
		mEditCount.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
				//mEditBillNumber.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));
				mEditCount.setTextColor(Color.BLACK);
				mEditBillNumber.setTextColor(Color.LTGRAY);

				mEditForInput = mEditCount;
				return false;
			}
		});
	}

	private void InitializeBillNumber(View root) {

		mEditBillNumber = (EditText) root.findViewById(R.id.edit_bill_number);
		mEditBillNumber.setText(mTovar.getNomerNakladnoy());
		mEditBillNumber.setTextColor(Color.LTGRAY);
		//mEditBillNumber.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));
		mEditBillNumber.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				mEditBillNumber.setTextColor(Color.BLACK);
				mEditCount.setTextColor(Color.LTGRAY);
				//mEditBillNumber.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
				//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));

				mEditForInput = mEditBillNumber;
				return false;
			}
		});
	}

	private void InitializeBillDate(View root) {

		mEditBillDate = (EditText) root.findViewById(R.id.edit_returns_date);
		mEditBillDate.setOnClickListener(mBillDateClick);

		mBillDate = mTovar.getDataNakladnoy();

		if (mBillDate == null) {

			mBillDate = Calendar.getInstance();
		} else {

			mEditBillDate.setText(DateTimeHelper.UIDateString(mBillDate.getTime()));
		}

		mBillDate = mTovar.getDataNakladnoy() == null ? Calendar.getInstance() : mTovar.getDataNakladnoy();

		((Button) root.findViewById(R.id.btn_returns_date)).setOnClickListener(mBillDateClick);
	}

	private OnClickListener mBillDateClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			DatePickerDialog.OnDateSetListener dateSetListener =
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view,
											  int year,
											  int monthOfYear,
											  int dayOfMonth) {

							mBillDate.set(year, monthOfYear, dayOfMonth);

							mEditBillDate.setText(DateTimeHelper.UIDateString(mBillDate.getTime()));
						}
					};

			new DatePickerDialog(arg0.getContext(),
					dateSetListener,
					mBillDate.get(Calendar.YEAR),
					mBillDate.get(Calendar.MONTH),
					mBillDate.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	@Override
	protected void onCreate() {

		LayoutInflater inflater =
				(LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_edit_return_nomenclature, null);

		setButtons(root);

		setContentView(root);
	}

	private void EditBillNumberInput(String character) {

		String text = mEditBillNumber.getText().toString();

		if (text.length() == BILL_NUMBER_WITH_DASH_LENGHT) {

			return;
		}

		if (text.length() == 2) {

			character = "-" + character;
		}

		mEditBillNumber.setText(text + character);
	}

	private void setButtons(View root) {

		Button btn1 = (Button) root.findViewById(R.id.btn_1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("1");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "1");
			}
		});

		Button btn2 = (Button) root.findViewById(R.id.btn_2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("2");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "2");
			}
		});

		Button btn3 = (Button) root.findViewById(R.id.btn_3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("3");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "3");
			}
		});

		Button btn4 = (Button) root.findViewById(R.id.btn_4);
		btn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("4");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "4");
			}
		});

		Button btn5 = (Button) root.findViewById(R.id.btn_5);
		btn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("5");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "5");
			}
		});

		Button btn6 = (Button) root.findViewById(R.id.btn_6);
		btn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("6");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "6");
			}
		});

		Button btn7 = (Button) root.findViewById(R.id.btn_7);
		btn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("7");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "7");
			}
		});

		Button btn8 = (Button) root.findViewById(R.id.btn_8);
		btn8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("8");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "8");
			}
		});

		Button btn9 = (Button) root.findViewById(R.id.btn_9);
		btn9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("9");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "9");
			}
		});

		Button btn0 = (Button) root.findViewById(R.id.btn_0);
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditBillNumber)
					EditBillNumberInput("0");
				else
					mEditForInput.setText(mEditForInput.getText().toString() + "0");
			}
		});

		((Button) root.findViewById(R.id.btn_slash)).setEnabled(false);

		Button btnPoint = (Button) root.findViewById(R.id.btn_point);
		btnPoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mEditForInput == mEditCount) {

					String inputString = mEditForInput.getText().toString();

					if (!inputString.contains(".")) {

						mEditForInput.setText(mEditForInput.getText().toString() + ".");
					}
				}
			}
		});

		Button btnDel = (Button) root.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String searchString = mEditForInput.getText().toString();

				if (searchString.length() != 0) {

					mEditForInput.setText(searchString.substring(0, searchString.length() - 1));
				}

				if (mEditForInput == mEditBillNumber && mEditBillNumber.getText().toString().length() == 3) {

					mEditForInput.setText(searchString.substring(0, searchString.length() - 1));
				}
			}
		});

		Button btnClear = (Button) root.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText("");
			}
		});

		Button btnEnter = (Button) root.findViewById(R.id.btn_enter);
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String text = mEditCount.getText().toString();

				double count = 0;
				if (text.length() != 0) {

					count = Double.parseDouble(mEditCount.getText().toString());
					if (count > 1000000) count = 1000000;
				}
				mTovar.setKolichestvo(count);

				text = mEditBillNumber.getText().toString();
				if (text.length() == BILL_NUMBER_WITH_DASH_LENGHT) {

					mTovar.setNomerNakladnoy(text);
				} else {

					mTovar.setNomerNakladnoy(null);
				}

				text = mEditBillDate.getText().toString();
				if (text.length() != 0) {

					mTovar.setDataNakladnoy(mBillDate);
				} else {

					mTovar.setDataNakladnoy(null);
				}
				int pp = mTovar.getPrichina();
				if (pp >= 100) {
					pp = pp - 100;
				}
				if (edit_tovarniy_vid.isChecked()) {
					mTovar.mPrichina = pp + 100;
				} else {
					mTovar.mPrichina = pp;
				}
				//System.out.println("mTovar.mPrichina "+mTovar.mPrichina+", edit_tovarniy_vid.isSelected() "+edit_tovarniy_vid.isSelected());
				dismiss(0);
			}
		});
	}
}
