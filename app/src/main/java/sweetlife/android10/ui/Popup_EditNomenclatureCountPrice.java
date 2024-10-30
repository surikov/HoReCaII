package sweetlife.android10.ui;

import reactive.ui.*;
import sweetlife.android10.data.common.Sales;
import sweetlife.android10.data.nomenclature.NamenclatureCRHelper;
import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.data.orders.ZayavkaPokupatelya_Foodstaff;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;
import sweetlife.android10.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.EditText;
import android.widget.TextView;

import tee.binding.it.*;
import tee.binding.task.*;

import android.database.sqlite.*;

import java.util.*;

import sweetlife.android10.*;
import sweetlife.android10.data.fixedprices.*;


public class Popup_EditNomenclatureCountPrice extends BetterPopupWindow{
	private EditText mEditForInput;
	private EditText mEditPrice;
	private EditText mEditCount;
	private NomenclatureCountHelper mCountHelper = new NomenclatureCountHelper(0, 0);
	private NamenclatureCRHelper mCRHelper;
	private ZayavkaPokupatelya_Foodstaff mFoodstaff;
	private boolean mIsCRAvailable = true;
	private boolean countByHandChanged = false;
	private boolean priceByHandChanged = false;
	CheckBox spr;
	static boolean vesHolding = false;
	long fixEndMs = 0;

	public Popup_EditNomenclatureCountPrice(View anchor, OnCloseListener closeListener, ZayavkaPokupatelya_Foodstaff foodstuff//
			, long fixEndMs
	){
		super(anchor, closeListener);
		//System.out.println("Popup_EditNomenclatureCountPrice "+fixEndMs);
		//this.vetspavka = invetspavka;
		this.fixEndMs = fixEndMs;

		mFoodstaff = foodstuff;
		mIsCRAvailable = foodstuff.isCRAvailable();
		InitializeCount(root);
		if(mIsCRAvailable){
			InitializePrice(root);
		}
		mEditForInput = mEditCount;
		TextView textName = (TextView)root.findViewById(R.id.text_nomenclature);
		textName.setText(mFoodstaff.getNomenklaturaNaimenovanie());
		if(mIsCRAvailable){
			spr = (CheckBox)root.findViewById(R.id.chk_vetspravka);
			spr.setVisibility(View.VISIBLE);
			spr.setText("весь холдинг");
			spr.setChecked(vesHolding);
			spr.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view){
					vesHolding = ((CheckBox)view).isChecked();
				}
			});
		}
		if(foodstuff.wrongMinMax()){
			Auxiliary.warn("Макс.цена ниже мин.цены (неверное значение в Ценах номенклатуры склада).", anchor.getContext());
		}
	}

	private void InitializeCount(View root){
		mCountHelper = new NomenclatureCountHelper(mFoodstaff.getMinNorma(), mFoodstaff.getKoefMest());
		mEditCount = (EditText)root.findViewById(R.id.edit_count);
		mEditCount.setText(DecimalFormatHelper.format(mFoodstaff.getKolichestvo()));
		//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_focus));
		mEditCount.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				CheckInputCount();
				if(mIsCRAvailable){
					CheckInputPrice();
					//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_focus));
					//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_normal));
					mEditCount.setTextColor(Color.BLACK);
					mEditPrice.setTextColor(Color.LTGRAY);
					mEditForInput = mEditCount;
				}
				return false;
			}
		});
		Resources res = anchor.getResources();
		TextView textCount = (TextView)root.findViewById(R.id.text_count);
		textCount.setText(res.getString(R.string.count) + "    " + res.getString(R.string.min_quantity) + " " + mFoodstaff.getMinNorma() + "  " + res.getString(R.string.koef_mest) + " "
				+ mFoodstaff.getKoefMest());
	}

	private void InitializePrice(View root){
		mCRHelper = new NamenclatureCRHelper(mFoodstaff.getMinimalnayaCena(), mFoodstaff.getMaksimalnayaCena());
		mEditPrice = (EditText)root.findViewById(R.id.edit_price);
		mEditPrice.setText(DecimalFormatHelper.format(mFoodstaff.getCenaSoSkidkoy()));
		mEditPrice.setVisibility(View.VISIBLE);
		mEditPrice.setTextColor(Color.LTGRAY);
		//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_normal));
		mEditPrice.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				CheckInputCount();
				CheckInputPrice();
				mEditPrice.setTextColor(Color.BLACK);
				mEditCount.setTextColor(Color.LTGRAY);
				//mEditPrice.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_focus));
				//mEditCount.setBackgroundDrawable(anchor.getResources().getDrawable(R.drawable.editbox_normal));
				mEditForInput = mEditPrice;
				return false;
			}
		});
		Resources res = anchor.getResources();
		TextView textMinMaxprice = (TextView)root.findViewById(R.id.text_price);
		textMinMaxprice.setVisibility(TextView.VISIBLE);
		textMinMaxprice.setText(res.getString(R.string.price) //
				+ "    " + res.getString(R.string.min_price) //
				+ ": " + mFoodstaff.getMinimalnayaCena() //
				+ "  " + res.getString(R.string.max_price) //
				+ ": " + mFoodstaff.getMaksimalnayaCena());
	}

	@Override
	protected void onCreate(){
		LayoutInflater inflater = (LayoutInflater)anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup root = (ViewGroup)inflater.inflate(R.layout.popup_edit_count, null);
		setButtons(root);
		setContentView(root);

	}

	private void resetInputByHand(){
		if(mEditForInput == mEditCount){
			if(!countByHandChanged){
				mEditForInput.setText("");
			}
			countByHandChanged = true;
		}else{
			if(mEditForInput == mEditPrice){
				if(!priceByHandChanged){
					mEditForInput.setText("");
				}
				priceByHandChanged = true;
			}
		}
		// mEditForInput.setText(mEditForInput.getText().toString() + "1");
	}

	private void setButtons(View root){
		Button btnMinus = (Button)root.findViewById(R.id.btn_minus);
		btnMinus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				rollCount(false);
			}
		});
		Button btnPlus = (Button)root.findViewById(R.id.btn_plus);
		btnPlus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				rollCount(true);
			}
		});
		Button btn1 = (Button)root.findViewById(R.id.btn_1);
		btn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "1");
			}
		});
		Button btn2 = (Button)root.findViewById(R.id.btn_2);
		btn2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "2");
			}
		});
		Button btn3 = (Button)root.findViewById(R.id.btn_3);
		btn3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "3");
			}
		});
		Button btn4 = (Button)root.findViewById(R.id.btn_4);
		btn4.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "4");
			}
		});
		Button btn5 = (Button)root.findViewById(R.id.btn_5);
		btn5.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "5");
			}
		});
		Button btn6 = (Button)root.findViewById(R.id.btn_6);
		btn6.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "6");
			}
		});
		Button btn7 = (Button)root.findViewById(R.id.btn_7);
		btn7.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "7");
			}
		});
		Button btn8 = (Button)root.findViewById(R.id.btn_8);
		btn8.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "8");
			}
		});
		Button btn9 = (Button)root.findViewById(R.id.btn_9);
		btn9.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "9");
			}
		});
		Button btn0 = (Button)root.findViewById(R.id.btn_0);
		btn0.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				mEditForInput.setText(mEditForInput.getText().toString() + "0");
			}
		});
		((Button)root.findViewById(R.id.btn_slash)).setEnabled(false);
		Button btnPoint = (Button)root.findViewById(R.id.btn_point);
		btnPoint.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				resetInputByHand();
				String inputString = mEditForInput.getText().toString();
				if(!inputString.contains(".")){
					mEditForInput.setText(mEditForInput.getText().toString() + ".");
				}
			}
		});
		Button btnDel = (Button)root.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String searchString = mEditForInput.getText().toString();
				if(searchString.length() != 0){
					mEditForInput.setText(searchString.substring(0, searchString.length() - 1));
				}
			}
		});
		Button btnClear = (Button)root.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mEditForInput.setText("");
			}
		});
		Button btnEnter = (Button)root.findViewById(R.id.btn_enter);
		btnEnter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				saveRow();
				dismiss(0);
			}
		});
		Button btnFixPrice = (Button)root.findViewById(R.id.btn_add_fix_price);
		btnFixPrice.setVisibility(View.VISIBLE);
		btnFixPrice.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(Cfg.noSmartPro(mFoodstaff.getArtikul(),anchor.getContext())){
					saveRow();
					promptFixPrice(mFoodstaff.getBasePrice());
					dismiss(0);
				}
			}
		});

	}

	void saveRow(){
		CheckInputCount();
		mFoodstaff.setKolichestvo(Double.parseDouble(mEditCount.getText().toString()));
		if(mIsCRAvailable){
			CheckInputPrice();
			mFoodstaff.setCenaSoSkidkoy(Double.parseDouble(mEditPrice.getText().toString()));
			if(vesHolding){
				if(mFoodstaff.getVidSkidki() == Sales.CR_NAME){
					Cfg.saveCRGroup(mFoodstaff.getArtikul(), mFoodstaff.getCenaSoSkidkoy());
				}
			}
		}
	}

	void promptFixPrice(double BASE_PRICE){
		final Numeric cena = new Numeric();

		Calendar today = java.util.Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY,1);
		today.set(Calendar.MINUTE,1);
		today.set(Calendar.SECOND,1);
		today.set(Calendar.MILLISECOND,1);
		today.add(Calendar.DAY_OF_MONTH,1);
		final Numeric fromMs = new Numeric().value((float)today.getTimeInMillis());

		Calendar next = java.util.Calendar.getInstance();
		next.setTimeInMillis((long)this.fixEndMs);
		next.set(Calendar.HOUR_OF_DAY,2);
		next.set(Calendar.MINUTE,2);
		next.set(Calendar.SECOND,2);
		next.set(Calendar.MILLISECOND,2);
		if(next.before(today)){
			next.setTimeInMillis(today.getTimeInMillis());
		}
		next.add(Calendar.DAY_OF_MONTH,1);
		final Numeric toMs = new Numeric().value((float)next.getTimeInMillis());



		//c.add(java.util.Calendar.DAY_OF_MONTH, 1);
		//mFoodstaff.getZayavka_IDRRef()
		//final Numeric to = new Numeric();
		//to.value((float)this.fixEndMs);
		//if(this.fixEndMs < from.value()){
		//	to.value(from.value());
		//}
		System.out.println("promptFixPrice " + today.getTimeInMillis()
				+" / "+this.fixEndMs + " / " + fromMs.value().longValue() + " / " + toMs.value().longValue());
		//final Numeric to = new Numeric().value((float)c.getTimeInMillis());
		Note cenaLabel=new Note().value("Цена");
		cena.afterChange(new Task(){
			public void doTask(){
				if(BASE_PRICE>0){
					double nacenka = (100.0 * (cena.value() - BASE_PRICE) / BASE_PRICE);
					cenaLabel.value("Цена (наценка " + String.format("%.2f", nacenka) + "%)");
				}else{
					cenaLabel.value("Цена (нет цены партий)");
				}
			}
		});
		Auxiliary.pick(anchor.getContext(), "Добавить в заявку на фикс. цену"
				, new SubLayoutless(anchor.getContext())//
						.child(new Decor(anchor.getContext()).labelText.is(cenaLabel).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactNumber(anchor.getContext()).number.is(cena).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new Decor(anchor.getContext()).labelText.is("Начало действия").left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(anchor.getContext()).date.is(fromMs).format.is("dd.MM.yyyy").left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new Decor(anchor.getContext()).labelText.is("Окончание действия").left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactDate(anchor.getContext()).date.is(toMs).format.is("dd.MM.yyyy").left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 4).width().is(Auxiliary.tapSize * 6 - Auxiliary.tapSize).height().is(Auxiliary.tapSize * 0.7))//
						.width().is(Auxiliary.tapSize * 6)//
						.height().is(Auxiliary.tapSize * 8)//
				, "Добавить", new Task(){
					@Override
					public void doTask(){
						System.out.println("save");
						SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
						String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
						ZayavkaNaSkidki mZayavka = new ZayavkaNaSkidki(clientID, mDB);
						FixedPricesNomenclatureData mFixedPricesNomenclatureData = new FixedPricesNomenclatureData(mDB, mZayavka);
						mFixedPricesNomenclatureData.newFixedPriceNomenclature(mFoodstaff.getNomenklaturaID(), mFoodstaff.getArtikul(), mFoodstaff.getNomenklaturaNaimenovanie());
						mFixedPricesNomenclatureData.getNomenclature(0).setCena(cena.value());
						mFixedPricesNomenclatureData.WriteToDataBase(mDB);
						java.util.Calendar c1 = java.util.Calendar.getInstance();
						c1.setTimeInMillis(fromMs.value().longValue());
						mZayavka.setVremyaNachalaSkidkiPhiksCen(c1.getTime());
						java.util.Calendar c2 = java.util.Calendar.getInstance();
						c2.setTimeInMillis(toMs.value().longValue());
						mZayavka.setVremyaOkonchaniyaSkidkiPhiksCen(c2.getTime());
						mZayavka.writeToDataBase(mDB);
					}
				}, null, null, null, null);
	}

	void promptFixPrice22(){
		final Numeric n = new Numeric();
		Auxiliary.pickNumber(this.anchor.getContext(), "Добавить в заявку на фикс. цену", n, "Добавить", new Task(){
			public void doTask(){
				/*System.out.println(n.value()+"/"+mFoodstaff.getNomenklaturaID()
						                   +"/"+mFoodstaff.getArtikul()
						                   +"/"+mFoodstaff.getNomenklaturaNaimenovanie());*/
				SQLiteDatabase mDB = ApplicationHoreca.getInstance().getDataBase();
				String clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
				ZayavkaNaSkidki mZayavka = new ZayavkaNaSkidki(clientID, mDB);
				FixedPricesNomenclatureData mFixedPricesNomenclatureData = new FixedPricesNomenclatureData(mDB, mZayavka);

				mFixedPricesNomenclatureData.newFixedPriceNomenclature(mFoodstaff.getNomenklaturaID(), mFoodstaff.getArtikul(), mFoodstaff.getNomenklaturaNaimenovanie());
				mFixedPricesNomenclatureData.getNomenclature(0).setCena(n.value());

				mFixedPricesNomenclatureData.WriteToDataBase(mDB);
				//Calendar c=Calendar.getInstance();
				//c.add(Calendar.DAY_OF_MONTH,1);


				//mZayavka.setVremyaNachalaSkidkiPhiksCen(c.getTime());
				//mZayavka.setVremyaOkonchaniyaSkidkiPhiksCen(c.getTime());
				//mZayavka.setKommentariy("");
				mZayavka.writeToDataBase(mDB);
			}
		}, null, null);
	}

	private void CheckInputCount(){
		double count = 0.00D;
		if(mEditCount.getText().toString().length() != 0){
			count = Double.parseDouble(mEditCount.getText().toString().replace(',', '.'));
		}
		mEditCount.setText(DecimalFormatHelper.format(mCountHelper.ReCalculateCount(count)));
	}

	private void rollCount(boolean increase){
		double count = 0.00D;
		if(mEditCount.getText().toString().length() != 0){
			count = Double.parseDouble(mEditCount.getText().toString().replace(',', '.'));
		}
		System.out.println("rollCount " + increase + ": " + count);
		if(increase){
			double nextKoefMest = mFoodstaff.getKoefMest() * (1 + Math.floor(count / mFoodstaff.getKoefMest()));
			double nextMinNorma = mFoodstaff.getMinNorma() * (1 + Math.floor(count / mFoodstaff.getMinNorma()));
			System.out.println("nextKoefMest " + nextKoefMest + ", nextMinNorma " + nextMinNorma);
			if(count < mFoodstaff.getMinNorma()){
				count = mFoodstaff.getMinNorma();
			}else{
				count = nextKoefMest < nextMinNorma ? nextKoefMest : nextMinNorma;
			}
		}else{
			double preKoefMest = mFoodstaff.getKoefMest() * Math.floor(count / mFoodstaff.getKoefMest());
			double preMinNorma = mFoodstaff.getMinNorma() * Math.floor(count / mFoodstaff.getMinNorma());
			System.out.println("preKoefMest " + preKoefMest + ", preMinNorma " + preMinNorma);
			if(preKoefMest == count){
				preKoefMest = preKoefMest - mFoodstaff.getKoefMest();
			}
			if(preMinNorma == count){
				preMinNorma = preMinNorma - mFoodstaff.getMinNorma();
			}
			count = preKoefMest < preMinNorma ? preMinNorma : preKoefMest;
			count = count < 0 ? 0 : count;
			if(count < mFoodstaff.getMinNorma()){
				count = mFoodstaff.getMinNorma();
			}
		}
		mEditCount.setText(DecimalFormatHelper.format(mCountHelper.ReCalculateCount(count)));
	}

	private void CheckInputPrice(){
		System.out.println("CheckInputPrice " + mEditPrice.getText().toString());
		double price = 0.00D;
		if(mEditPrice.getText().toString().length() != 0){
			price = Double.parseDouble(mEditPrice.getText().toString().replace(',', '.'));
		}
		if(mFoodstaff.getCenaSoSkidkoy() != price){
			if(price < mFoodstaff.getMinimalnayaCena()){
				Auxiliary.inform("Цена ниже минимальной!", anchor.getContext());
				//System.out.println("can't "+price+" < "+mFoodstaff.getMinimalnayaCena());
				mEditPrice.setText("" + mFoodstaff.getCena());
			}else{
				mEditPrice.setText(DecimalFormatHelper.format(mCRHelper.ReCalculatePrice(price)));
			}
		}
	}
}
