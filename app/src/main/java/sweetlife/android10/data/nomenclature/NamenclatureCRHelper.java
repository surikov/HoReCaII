package sweetlife.android10.data.nomenclature;

public class NamenclatureCRHelper {

	private double mMinPrice;
	private double mMaxPrice; 

	public NamenclatureCRHelper( double minPrice, double maxPrice ) {

		mMinPrice = minPrice;
		mMaxPrice = maxPrice;
	}

	public double ReCalculatePrice( double inputPrice ) {

		if( inputPrice > mMaxPrice ) {

			return mMaxPrice;
		}

		if( inputPrice < mMinPrice ) {

			return mMinPrice;
		}

		return inputPrice;
	}
}
