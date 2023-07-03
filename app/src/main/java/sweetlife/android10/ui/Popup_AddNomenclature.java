package sweetlife.android10.ui;

import sweetlife.android10.data.nomenclature.NomenclatureCountHelper;
import sweetlife.android10.data.nomenclature.NomenclaturePriceAndSale;
import sweetlife.android10.data.orders.FoodstuffsData;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;
import sweetlife.android10.database.nomenclature.Request_Search;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.widgets.BetterPopupWindow;

import sweetlife.android10.R;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Popup_AddNomenclature extends BetterPopupWindow {
	private EditText mEditForInput;
	private EditText mEditArticle;
	private EditText mEditCount;
	private TextView mTextCount;
	private Button mBtnPlus;
	private Button mBtnMinus;
	private NomenclatureCountHelper mCountHelper = new NomenclatureCountHelper(0, 0);
	private Cursor mSearchCursor = null;
	private FoodstuffsData mFoodStuffs = null;
	private SQLiteDatabase mDB = null;
	private boolean mIsSlashInputEnable = true;
	private boolean mIsCRAvailable = true;
	private String mNomenclatureNotFound = null;
	private String mNomenclatureExists = null;
	private boolean countByHandChanged = false;

	public Popup_AddNomenclature(View anchor//
			, OnCloseListener closeListener//
			, FoodstuffsData foodStuffs//
			, SQLiteDatabase db//
			, boolean isCRAvailable//
	) {
		super(anchor, closeListener);
		mNomenclatureNotFound = anchor.getResources().getString(R.string.nomenclature_not_found);
		mNomenclatureExists = anchor.getResources().getString(R.string.msg_already_in_list);
		mFoodStuffs = foodStuffs;
		mDB = db;
		mIsCRAvailable = isCRAvailable;
	}

	@Override
	protected void finalize() throws Throwable {
		if (mSearchCursor != null && !mSearchCursor.isClosed()) {
			mSearchCursor.close();
			mSearchCursor = null;
		}
		super.finalize();
	}

	@Override
	protected void onCreate() {
		LayoutInflater inflater = (LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_add_by_article, null);
		mEditArticle = (EditText) root.findViewById(R.id.edit_article);
		mEditArticle.setEnabled(true);
		mEditCount = (EditText) root.findViewById(R.id.edit_count);
		mEditCount.setEnabled(false);
		mTextCount = (TextView) root.findViewById(R.id.text_count);
		mEditForInput = mEditArticle;
		setButtons(root);
		setContentView(root);
	}

	private void setButtons(View root) {
		mBtnMinus = (Button) root.findViewById(R.id.btn_minus);
		mBtnMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rollCount(false);
			}
		});
		mBtnPlus = (Button) root.findViewById(R.id.btn_plus);
		mBtnPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rollCount(true);
			}
		});
		Button btn1 = (Button) root.findViewById(R.id.btn_1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "1");
			}
		});
		Button btn2 = (Button) root.findViewById(R.id.btn_2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "2");
			}
		});
		Button btn3 = (Button) root.findViewById(R.id.btn_3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "3");
			}
		});
		Button btn4 = (Button) root.findViewById(R.id.btn_4);
		btn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "4");
			}
		});
		Button btn5 = (Button) root.findViewById(R.id.btn_5);
		btn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "5");
			}
		});
		Button btn6 = (Button) root.findViewById(R.id.btn_6);
		btn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "6");
			}
		});
		Button btn7 = (Button) root.findViewById(R.id.btn_7);
		btn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "7");
			}
		});
		Button btn8 = (Button) root.findViewById(R.id.btn_8);
		btn8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "8");
			}
		});
		Button btn9 = (Button) root.findViewById(R.id.btn_9);
		btn9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "9");
			}
		});
		Button btn0 = (Button) root.findViewById(R.id.btn_0);
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText(mEditForInput.getText().toString() + "0");
			}
		});
		Button btnSlash = (Button) root.findViewById(R.id.btn_slash);
		btnSlash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				if (mIsSlashInputEnable) {
					mEditForInput.setText(mEditForInput.getText().toString() + "/");
				}
			}
		});
		Button btnPoint = (Button) root.findViewById(R.id.btn_point);
		btnPoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				String inputString = mEditForInput.getText().toString();
				if (!inputString.contains(".")) {
					mEditForInput.setText(mEditForInput.getText().toString() + ".");
				}
			}
		});
		Button btnDel = (Button) root.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				String searchString = mEditForInput.getText().toString();
				if (searchString.length() != 0) {
					mEditForInput.setText(searchString.substring(0, searchString.length() - 1));
				}
			}
		});
		Button btnClear = (Button) root.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckEditForClear();
				mEditForInput.setText("");
			}
		});
		Button btnEnter = (Button) root.findViewById(R.id.btn_enter);
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEditForInput.equals(mEditArticle)) {
					String searchString = mEditArticle.getText().toString();
					if (searchString.length() != 0 && searchString.compareToIgnoreCase(mNomenclatureExists) != 0 && searchString.compareToIgnoreCase(mNomenclatureNotFound) != 0) {
						Request_Search requestSearch = new Request_Search(Request_Search.SEARCH_ARTICLE, searchString, false);
						mSearchCursor = requestSearch.Request(mDB, null, false);
						if (mSearchCursor.moveToFirst()) {
							if (mFoodStuffs.IsFoodstuffAlreadyInList(Request_NomenclatureBase.getIDRRef(mSearchCursor))) {
								mEditArticle.setText(mNomenclatureExists);
								mEditArticle.setTextColor(Color.RED);
								mSearchCursor.close();
								mSearchCursor = null;
								return;
							}
							mEditArticle.setText(Request_NomenclatureBase.getNaimenovanie(mSearchCursor));
							mEditArticle.setTextColor(Color.BLACK);
							mCountHelper = new NomenclatureCountHelper(Double.parseDouble(Request_NomenclatureBase.getMinNorma(mSearchCursor)), Double
									.parseDouble(Request_NomenclatureBase.getKoephphicient(mSearchCursor)));
							mEditCount.setText(Request_NomenclatureBase.getKoephphicient(mSearchCursor));
							mEditForInput = mEditCount;
							mEditCount.setEnabled(true);
							mEditArticle.setEnabled(false);
							mIsSlashInputEnable = false;
							Resources res = anchor.getResources();
							mTextCount.setText(res.getString(R.string.count)
									+ "    " + res.getString(R.string.min_quantity) + " "
									+ Request_NomenclatureBase.getMinNorma(mSearchCursor)
									+ "  " + res.getString(R.string.koef_mest) + " "
									+ Request_NomenclatureBase.getKoephphicient(mSearchCursor));
							mBtnPlus.setEnabled(true);
							mBtnMinus.setEnabled(true);
							countByHandChanged = false;
						} else {
							mEditArticle.setText(mNomenclatureNotFound);
							mEditArticle.setTextColor(Color.RED);
							mSearchCursor.close();
							mSearchCursor = null;
						}
					}
				} else {
					if (mSearchCursor != null && !mSearchCursor.isClosed() && mSearchCursor.getCount() != 0) {
						CheckInputCount();
						SetNewFoodStaff();
						dismiss(0);
					}
				}
			}
		});
	}

	private void CheckEditForClear() {
		String editText = mEditForInput.getText().toString();
		if (mNomenclatureNotFound.compareToIgnoreCase(editText) == 0 || mNomenclatureExists.compareToIgnoreCase(editText) == 0) {
			mEditArticle.setTextColor(Color.BLACK);
			mEditForInput.setText("");
		}
		if (mEditForInput == mEditCount) {
			if (!countByHandChanged) {
				mEditForInput.setText("");
			}
			countByHandChanged = true;
		}
	}

	private void SetNewFoodStaff() {
		NomenclaturePriceAndSale priceHelper = new NomenclaturePriceAndSale(mSearchCursor, mIsCRAvailable);
		mFoodStuffs.newFoodstuff(//
				Request_NomenclatureBase.getIDRRef(mSearchCursor)//
				, Request_NomenclatureBase.getArtikul(mSearchCursor)//
				, Request_NomenclatureBase.getNaimenovanie(mSearchCursor)//
				, Request_NomenclatureBase.getEdinicyIzmereniyaID(mSearchCursor)//
				, Request_NomenclatureBase.getEdinicyIzmereniyaNaimenovanie(mSearchCursor)//
				, Double.parseDouble(mEditCount.getText().toString())//
				, Double.parseDouble(Request_NomenclatureBase.getCena(mSearchCursor))//
				, priceHelper.getPriceWithSale()//
				, priceHelper.getMinPrice()//
				, priceHelper.getMaxPrice()//
				, priceHelper.getSale()//
				, priceHelper.getVidSkidki()//
				, Double.parseDouble(Request_NomenclatureBase.getMinNorma(mSearchCursor))//
				, Double.parseDouble(Request_NomenclatureBase.getKoephphicient(mSearchCursor))//
				, Request_NomenclatureBase.getBasePrice(mSearchCursor)//
				, Request_NomenclatureBase.getLastPrice(mSearchCursor)//
				//,Request_NomenclatureBase.getSkidka(mSearchCursor)//
				//,Request_NomenclatureBase.getVidSkidki(mSearchCursor)//
		);
	}

	private void CheckInputCount() {
		double count = 0.00D;
		if (mEditCount.getText().toString().length() != 0) {
			count = Double.parseDouble(mEditCount.getText().toString().replace(',', '.'));
		}
		mEditCount.setText(DecimalFormatHelper.format(mCountHelper.ReCalculateCount(count)));
	}

	private void rollCount(boolean increase) {
		double count = 0.00D;
		if (mEditCount.getText().toString().length() != 0) {
			count = Double.parseDouble(mEditCount.getText().toString().replace(',', '.'));
		}

		if (increase) {
			if (count < mCountHelper.getPlaceCount()) {
				count = mCountHelper.getPlaceCount();
			} else {
				count = count + mCountHelper.getPlaceCount();
			}
		} else {
			count = count - mCountHelper.getPlaceCount();
			count = count < 0 ? 0 : count;
		}
		mEditCount.setText(DecimalFormatHelper.format(mCountHelper.ReCalculateCount(count)));
	}
}
