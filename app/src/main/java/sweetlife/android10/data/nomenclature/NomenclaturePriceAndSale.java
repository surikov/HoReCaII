package sweetlife.android10.data.nomenclature;

import sweetlife.android10.database.Request_FoodStuffList;
import sweetlife.android10.database.nomenclature.Request_NomenclatureBase;

import android.database.Cursor;

public class NomenclaturePriceAndSale {

	private double mSale = 0.00D;
	private double mPriceWithSale = 0.00D;
	private double mMinPrice = 0.00D;
	private double mMaxPrice = 0.00D;
	private String mVidSkidki = "x'00'";

	public NomenclaturePriceAndSale(Cursor nomenclatureCursor, boolean isCRAvailable) {

		String vidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);

		if (Request_NomenclatureBase.getMinCena(nomenclatureCursor) == null || !isCRAvailable) {

			mMinPrice = 0.00D;
			mMaxPrice = 0.00D;

			if (vidSkidki.length() > 0// != null //&& Sales.GetSaleName(vidSkidki).length() != 0
			) {

				mVidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);
				mPriceWithSale = Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(nomenclatureCursor));
			} else {

				mVidSkidki = "x'00'";
				mPriceWithSale = 0.00D;
			}
		} else {

			if (vidSkidki.length() > 0// != null && Sales.GetSaleName(vidSkidki).length() != 0
			) {

				mVidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);
				mPriceWithSale = Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(nomenclatureCursor));
			} else {

				mVidSkidki = "x'00'";

				mPriceWithSale = Double.parseDouble(Request_NomenclatureBase.getCena(nomenclatureCursor));
			}

			mMinPrice = Double.parseDouble(Request_NomenclatureBase.getMinCena(nomenclatureCursor));
			mMaxPrice = Double.parseDouble(Request_NomenclatureBase.getMaxCena(nomenclatureCursor));
		}

		String sale = Request_NomenclatureBase.getSkidka(nomenclatureCursor);

		if (sale != null) {

			mSale = Double.parseDouble(sale);
		} else {

			mSale = 0.00D;
		}


		//System.out.println("NomenclaturePriceAndSale "+mVidSkidki);
	}

	public void _____NomenclaturePriceAndSale(Cursor nomenclatureCursor, Cursor bidCursor, boolean isCRAvailable) {

		String saleString = Request_NomenclatureBase.getSkidka(nomenclatureCursor);

		if (Request_NomenclatureBase.getMinCena(nomenclatureCursor) == null || !isCRAvailable) {

			mVidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);

			if (saleString != null) {

				mSale = Double.parseDouble(saleString);
				mPriceWithSale = Double.parseDouble(Request_NomenclatureBase.getCenaSoSkidkoy(nomenclatureCursor));
			}
		} else {

			if (Request_FoodStuffList.getMinimalnayaCena(bidCursor) == 0) {

				if (saleString != null) {

					mVidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);
					mSale = Double.parseDouble(saleString);
				} else {

					mVidSkidki = "x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";//Sales.CR_ID;
				}

				mPriceWithSale = Double.parseDouble(Request_NomenclatureBase.getCenaCR(nomenclatureCursor));
				mMinPrice = Double.parseDouble(Request_NomenclatureBase.getMinCena(nomenclatureCursor));
				mMaxPrice = Double.parseDouble(Request_NomenclatureBase.getMaxCena(nomenclatureCursor));
			} else {

				if (saleString != null) {

					mVidSkidki = Request_NomenclatureBase.getVidSkidki(nomenclatureCursor);
					mSale = Double.parseDouble(saleString);
				} else {

					mVidSkidki = "x'" + sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie + "'";//Sales.CR_ID;
				}

				double minPrice = Double.parseDouble(Request_NomenclatureBase.getMinCena(nomenclatureCursor));
				double maxPrice = Double.parseDouble(Request_NomenclatureBase.getMaxCena(nomenclatureCursor));

				NamenclatureCRHelper cr = new NamenclatureCRHelper(minPrice, maxPrice);

				mPriceWithSale = cr.ReCalculatePrice(Request_FoodStuffList.getCenaSoSkidkoy(bidCursor));
				mMinPrice = minPrice;
				mMaxPrice = maxPrice;
			}
		}
	}

	public double getSale() {
		return mSale;
	}

	public void setSale(double sale) {
		mSale = sale;
	}

	public double getPriceWithSale() {
		return mPriceWithSale;
	}

	public void ___setPriceWithSale(double priceWithSale) {
		mPriceWithSale = priceWithSale;
	}

	public double getMinPrice() {
		return mMinPrice;
	}

	public void setMinPrice(double minPrice) {
		mMinPrice = minPrice;
	}

	public double getMaxPrice() {
		return mMaxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		mMaxPrice = maxPrice;
	}

	public String getVidSkidki() {
		return mVidSkidki;
	}

	public void setVidSkidki(String vidSkidki) {
		mVidSkidki = vidSkidki;
	}
}
