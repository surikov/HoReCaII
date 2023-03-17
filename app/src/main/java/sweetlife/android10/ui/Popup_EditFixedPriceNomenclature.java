package sweetlife.android10.ui;

import sweetlife.android10.data.fixedprices.ZayavkaNaSkidki_TovaryPhiksCen;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;
import android.content.Context;
import android.graphics.Color;
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

public class Popup_EditFixedPriceNomenclature extends BetterPopupWindow {

	private EditText                      mEditForInput;

	private EditText                      mEditPrice;
	private EditText                      mEditTovarooborot;

	private ZayavkaNaSkidki_TovaryPhiksCen mTovar;

	public Popup_EditFixedPriceNomenclature( View anchor, 
			OnCloseListener closeListener, 
			ZayavkaNaSkidki_TovaryPhiksCen tovar) {

		super(anchor, closeListener);

		mTovar = tovar;

		InitializePrice(root);
		InitializeTovarooborot(root);

		mEditForInput = mEditPrice;

		TextView textName = (TextView) root.findViewById(R.id.text_nomenclature);
		textName.setText(mTovar.getNomenklaturaNaimenovanie());
	}

	private void InitializePrice(View root) {

		mEditPrice = (EditText) root.findViewById(R.id.edit_price);
		mEditPrice.setText(mTovar.getCena() == 0 ? "" : DecimalFormatHelper.format(mTovar.getCena()));
		//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
		mEditPrice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
				//mEditTovarooborot.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));
				mEditPrice.setTextColor(Color.BLACK);
				mEditTovarooborot.setTextColor(Color.LTGRAY);

				mEditForInput = mEditPrice;
				return false;
			}
		});
	}

	private void InitializeTovarooborot(View root) {

		mEditTovarooborot = (EditText) root.findViewById(R.id.edit_tovarooborot);
		mEditTovarooborot.setText( mTovar.getObyazatelstva() == 0 ? "" : DecimalFormatHelper.format(mTovar.getObyazatelstva()) );
		mEditTovarooborot.setTextColor(Color.LTGRAY);
		//mEditTovarooborot.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));
		mEditTovarooborot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				mEditTovarooborot.setTextColor(Color.BLACK);
				mEditPrice.setTextColor(Color.LTGRAY);
				//mEditTovarooborot.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_focus));
				//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable( R.drawable.editbox_normal));

				mEditForInput = mEditTovarooborot;
				return false;
			}
		});
	}

	@Override
	protected void onCreate() {

		LayoutInflater inflater =
				(LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_edit_fixedprice_nomenclature, null);

		setButtons(root);

		setContentView(root);
	}

	private void setButtons(View root) {

		Button btn1 = (Button)root.findViewById(R.id.btn_1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "1");
			}
		});

		Button btn2 = (Button)root.findViewById(R.id.btn_2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "2");
			}
		});

		Button btn3 = (Button)root.findViewById(R.id.btn_3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "3");
			}
		});

		Button btn4 = (Button)root.findViewById(R.id.btn_4);
		btn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "4");
			}
		});

		Button btn5 = (Button)root.findViewById(R.id.btn_5);
		btn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "5");
			}
		});

		Button btn6 = (Button)root.findViewById(R.id.btn_6);
		btn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "6");
			}
		});

		Button btn7 = (Button)root.findViewById(R.id.btn_7);
		btn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "7");
			}
		});

		Button btn8 = (Button)root.findViewById(R.id.btn_8);
		btn8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "8");
			}
		});

		Button btn9 = (Button)root.findViewById(R.id.btn_9);
		btn9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "9");
			}
		});

		Button btn0 = (Button)root.findViewById(R.id.btn_0);
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText(mEditForInput.getText().toString() + "0");
			}
		});

		((Button)root.findViewById(R.id.btn_slash)).setEnabled(false);

		Button btnPoint = (Button)root.findViewById(R.id.btn_point);
		btnPoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

					String inputString = mEditForInput.getText().toString();

					if( !inputString.contains(".") ) {

						mEditForInput.setText(mEditForInput.getText().toString() + ".");
					}
			}
		});

		Button btnDel = (Button)root.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String searchString = mEditForInput.getText().toString();			

				if( searchString.length() != 0 ) {

					mEditForInput.setText(searchString.substring(0, searchString.length() -1 ));
				}
			}
		});

		Button btnClear = (Button)root.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mEditForInput.setText("");
			}
		});

		Button btnEnter = (Button)root.findViewById(R.id.btn_enter);
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String text = mEditPrice.getText().toString();

				double cena = 0;
				if( text.length() != 0 ) {

					cena = Double.parseDouble(text);
					if( cena > 1000000 ) cena = 1000000;
				}
				mTovar.setCena(cena);

				text = mEditTovarooborot.getText().toString();

				double tovarooborot = 0;
				if( text.length() != 0 ) {

					tovarooborot = Double.parseDouble(text);
					if( tovarooborot > 1000000 ) tovarooborot = 1000000;
				}
				mTovar.setObyazatelstva(tovarooborot);
				
				dismiss( 0 );
			}
		});
	}
}
