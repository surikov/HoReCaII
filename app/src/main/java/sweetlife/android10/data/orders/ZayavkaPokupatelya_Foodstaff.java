package sweetlife.android10.data.orders;

import sweetlife.android10.data.common.NomenclatureBasedCountItem;
import sweetlife.android10.data.common.Sales;
import sweetlife.android10.database.ISklady;
import sweetlife.android10.utils.Hex;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ZayavkaPokupatelya_Foodstaff extends NomenclatureBasedCountItem {
	private double mSumma;
	private double mSummaSoSkidkoy;
	private double mCena;
	private double mCenaSoSkidkoy;
	private double mMinimalnayaCena;
	private double mMaksimalnayaCena;
	private double mSkidka;
	private String mVidSkidki;
	private double basePrice;
	private double mLastPrice;
	//private boolean isMustList;
	public double ves = 0;

	//public boolean CRbyHands=false;
	public ZayavkaPokupatelya_Foodstaff(//
										int _id//
			, int nomerStroki//
			, String nomenklaturaID//
			, String artikul//
			, String nomenklaturaNaimenovanie//
			, String zayavka//
			, String edinicaIzmereniyaID//
			, String edinicaIzmereniyaName//
			, Double kolichestvo//
			, Double summa//
			, Double cena//
			, Double cenaSoSkidkoy//
			, double minimalnayaCena//
			, double maksimalnayaCena//
			, Double skidka//
			, String vidSkidki//
			, Double minNorma//
			, Double koefMest//
			, Double sebestoimost//
			, double lastPrice//
			, boolean New//
										//, boolean isml//
										//, String skidkaProcent//
										//, String skidkaNaimenovanie//
										//,boolean crByHands
	) {
		super(_id, nomerStroki, nomenklaturaID, artikul, nomenklaturaNaimenovanie, zayavka, minNorma, koefMest, edinicaIzmereniyaID, edinicaIzmereniyaName, kolichestvo, New//
				//,skidkaProcent
				//,skidkaNaimenovanie
		);
		//System.out.println(""+artikul+": lastPrice "+lastPrice+"/"+vidSkidki);
		//CRbyHands=crByHands;
		//isMustList=isml;
		mSumma = summa;
		mCena = cena;
		mCenaSoSkidkoy = cenaSoSkidkoy;
		mMinimalnayaCena = minimalnayaCena;
		mMaksimalnayaCena = maksimalnayaCena;
		mSkidka = skidka;
		mVidSkidki = vidSkidki;
		basePrice = sebestoimost;
		mLastPrice = lastPrice;
		if (summa == 0.00D) {
			mSumma = mKolichestvo * mCena;
		}
		/*if (hasSale() || isCRAvailable()) {
			setSummaSoSkidkoy(mCenaSoSkidkoy * mKolichestvo);
		}
		else {
			setSummaSoSkidkoy(mKolichestvo * mCena);
		}*/
		setSummaSoSkidkoy(mCenaSoSkidkoy * mKolichestvo);
		//System.out.println("new ZayavkaPokupatelya_Foodstaff: "+nomenklaturaNaimenovanie);
	}

	@Override
	public void setToDataBase(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		if (mNew) {
			values.put("NomerStroki", mNomerStroki);
			values.put("EdinicaIzmereniya", Hex.decodeHexWithPrefix(mEdinicaIzmereniyaID));
			values.put("Kolichestvo", new Double(mKolichestvo));
			values.put("Nomenklatura", Hex.decodeHexWithPrefix(mNomenklaturaID));
			values.put("Summa", mSummaSoSkidkoy);
			values.put("Cena", mCena);
			values.put("CenaSoSkidkoy", mCenaSoSkidkoy);
			values.put("MinimalnayaCena", mMinimalnayaCena);
			values.put("MaksimalnayaCena", mMaksimalnayaCena);
			values.put("Skidka", mSkidka);
			//System.out.println(">>>" + mVidSkidki + "<<<");
			if (//mVidSkidki.compareTo(Sales.CR_ID) == 0
					mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdOldCenovoyeReagirovanie + "'") == 0
							|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'") == 0
							|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdAutoReagirovanie + "'") == 0
							|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_Heroy + "'") == 0
							|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_TGCR + "'") == 0
			) {
				values.put("VidSkidki", Hex.decodeHexWithPrefix(mVidSkidki));
			} else {
				values.put("VidSkidki", Hex.decodeHexWithPrefix(Sales.DEFAULT_ID));
			}
			values.put("Sebestoimost", basePrice);
			values.put("_ZayavkaPokupatelyaIskhodyaschaya_IDRRef", Hex.decodeHexWithPrefix(mZayavka_IDRRef));
			values.put("_KeyField", mLastPrice);
			m_id = (int) db.insert("ZayavkaPokupatelyaIskhodyaschaya_Tovary", null, values);
			mNew = false;
		} else {
			values.put("Kolichestvo", new Double(mKolichestvo));
			values.put("Summa", mSummaSoSkidkoy);
			values.put("CenaSoSkidkoy", mCenaSoSkidkoy);
			values.put("MinimalnayaCena", mMinimalnayaCena);
			values.put("MaksimalnayaCena", mMaksimalnayaCena);
			values.put("Nacenka", 0);
			values.put("Skidka", mSkidka);
			//values.put("VidSkidki", this.CRbyHands?"CR":"  " );
			//Hex.decodeHexWithPrefix("");
			//values.put("VidSkidki", Hex.decodeHexWithPrefix(mVidSkidki));
			//if (mVidSkidki.compareTo(Sales.CR_ID) == 0) {
			//System.out.println("mVidSkidki '"+mVidSkidki+"'");
			if (mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdOldCenovoyeReagirovanie + "'") == 0
					|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'") == 0
					|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdAutoReagirovanie + "'") == 0
					|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_Heroy + "'") == 0
					|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_TGCR + "'") == 0
			) {
				//System.out.println("setToDataBase mVidSkidki '"+mVidSkidki+"'");
				values.put("VidSkidki", Hex.decodeHexWithPrefix(mVidSkidki));
			} else {
				values.put("VidSkidki", Hex.decodeHexWithPrefix(Sales.DEFAULT_ID));
			}

			db.update("ZayavkaPokupatelyaIskhodyaschaya_Tovary", values, "_id=" + String.valueOf(m_id), null);
		}
	}

	@Override
	public void setKolichestvo(Double kolichestvo) {
		mKolichestvo = kolichestvo;
		/*if (hasSale() || isCRAvailable()) {
			mSummaSoSkidkoy = mCenaSoSkidkoy * mKolichestvo;
		}
		else {
			mSummaSoSkidkoy = mKolichestvo * mCena;
		}*/
		mSummaSoSkidkoy = mCenaSoSkidkoy * mKolichestvo;
		mSumma = mKolichestvo * mCena;
		//System.out.println("setKolichestvo mSummaSoSkidkoy: " + mSummaSoSkidkoy);
	}

	/*public boolean isMustList(){
		return isMustList;
	}
	public void setMustList(boolean ml){
		 isMustList=ml;
	}*/
	public Double getSumma() {
		return mSumma;
	}

	public void setSumma(Double summa) {
		mSumma = summa;
	}

	public Double getCena() {
		return mCena;
	}

	public Double getCenaSoSkidkoy() {
		return mCenaSoSkidkoy;
	}

	public void setCenaSoSkidkoy(double cenaSoSkidkoy) {
		if (isCRAvailable()) {
			if (mCenaSoSkidkoy != cenaSoSkidkoy) {
				mSkidka = 0.0D;

				mCenaSoSkidkoy = cenaSoSkidkoy;
				mSummaSoSkidkoy = mCenaSoSkidkoy * mKolichestvo;
				if (
						(mVidSkidki.toUpperCase().equals(("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_Heroy + "'").toUpperCase()))
								|| (mVidSkidki.toUpperCase().equals(("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_TGCR + "'").toUpperCase()))
				) {
					mVidSkidki = "x'" + sweetlife.android10.supervisor.Cfg.skidkaId_TGCR + "'";
				} else {
					mVidSkidki = "x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";//Sales.CR_ID;
					/*
					mSkidka = 0.0D;
					mVidSkidki = "x'" + sweetlife.horeca.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";//Sales.CR_ID;
					mCenaSoSkidkoy = cenaSoSkidkoy;
					mSummaSoSkidkoy = mCenaSoSkidkoy * mKolichestvo;
					*/
					//this.CRbyHands=true;
					//System.out.println("          by hands");
				}
			}
		}
		//System.out.println("setCenaSoSkidkoy mSummaSoSkidkoy: " + mSummaSoSkidkoy);
	}

	public double getMinimalnayaCena() {

		return mMinimalnayaCena;
	}

	public void setMinimalnayaCena(double minimalnayaCena) {

		mMinimalnayaCena = minimalnayaCena;
	}

	public double getMaksimalnayaCena() {

		return Math.round(100.0 * mMaksimalnayaCena) / 100.0;
	}

	public void setMaksimalnayaCena(double maksimalnayaCena) {

		mMaksimalnayaCena = maksimalnayaCena;
	}

	public Double getSkidka() {
		return mSkidka;
	}

	public void setSkidka(Double skidka) {
		mSkidka = skidka;
	}

	public String getVidSkidki() {
		//if (mVidSkidki.compareTo(Sales.CR_ID) == 0) {
		//System.out.println("getVidSkidki "+mVidSkidki);
		if (mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdOldCenovoyeReagirovanie + "'") == 0
				|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'") == 0
				|| mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdAutoReagirovanie + "'") == 0
		) {
			return Sales.CR_NAME;
		} else {
			if (mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_Heroy + "'") == 0) {
				return Sales.hero_NAME;
			} else {
				if (mVidSkidki.compareToIgnoreCase("x'" + sweetlife.android10.supervisor.Cfg.skidkaId_TGCR + "'") == 0) {
					return Sales.CR_hero_NAME;
				} else {
					return "";
				}
			}
		}
	}

	public void setVidSkidki(String vidSkidki) {
		mVidSkidki = vidSkidki;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double sebestoimost) {
		basePrice = sebestoimost;
	}

	public boolean isCRAvailable() {

		if ((mMinimalnayaCena) != 0 && (mMinimalnayaCena <= mMaksimalnayaCena)) {
			return true;
		}
		return false;
	}

	public boolean wrongMinMax() {
		if (mMinimalnayaCena > mMaksimalnayaCena) {
			return true;
		}
		return false;
	}

	public boolean hasSale() {
		//if (mSkidka == 0 && mVidSkidki.compareTo(Sales.FIX_PRICE_ID) != 0) {
		if (mSkidka == 0 && mVidSkidki.compareTo("x'" + sweetlife.android10.supervisor.Cfg.skidkaIdFixirovannaya + "'") != 0) {
			return false;
		}
		return true;
	}

	public Double getSummaSoSkidkoy() {
		return mSummaSoSkidkoy;
	}

	public void setSummaSoSkidkoy(Double summaSoSkidkoy) {
		mSummaSoSkidkoy = summaSoSkidkoy;
		//System.out.println("setSummaSoSkidkoy mSummaSoSkidkoy: "+mSummaSoSkidkoy);
	}

	public double getBasePriceAmount() {
		//System.out.println("getBasePriceAmount: "+basePrice+" x "+mKolichestvo +" = "+(mKolichestvo * basePrice));
		return mKolichestvo * basePrice;
	}

	public double getLastPrice() {
		return mLastPrice;
	}

	public Double getSummaSoSkidkoyForStore(String sklad) {
		//System.out.println(this.mNomenklaturaNaimenovanie);
		try {
			String nn = mNomenklaturaNaimenovanie;
			if (nn.endsWith("`")) {
				nn = nn.substring(0, nn.length() - 1);
				//System.out.println("ignore ` "+nn);

			}
			nn = nn.substring(nn.length() - 3, nn.length() - 1);
			//System.out.println("'"+nn+"'");
			if (nn.equals("14") && sklad.equals(ISklady.KAZAN_sklad_14)) {
				return mSummaSoSkidkoy;
			}
			if (nn.equals("10") && sklad.equals(ISklady.HORECA_sklad_10)) {
				return mSummaSoSkidkoy;
			}
			if (nn.equals(" 8") && sklad.equals(ISklady.HORECA_sklad_8)) {
				return mSummaSoSkidkoy;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return 0.0;
	}
}
