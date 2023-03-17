package sweetlife.android10.data.nomenclature;


public class NomenclatureCountHelper {

	private double mMinCount;
	private double mPlaceCount;

	public NomenclatureCountHelper( double minCount, double placeCount ) {

		mMinCount = minCount;
		mPlaceCount = placeCount;
	}

	public double getMinCount() {

		return mMinCount;
	}

	public double getPlaceCount() {

		return mPlaceCount;
	}

	public double ReCalculateCount( double inputCount ) {

		if(mPlaceCount == 0 || mMinCount == 0) {

			return inputCount;
		}

		int multiplicityMinCount = (int)((inputCount * 100) / (mMinCount*100));
		int multiplicityPlaceCount = (int)((inputCount*100) / (mPlaceCount*100));

		if(multiplicityMinCount == 0 || multiplicityPlaceCount == 0) {

			if(multiplicityMinCount != 0) {

				return multiplicityMinCount * mMinCount; 
			}
			else if(multiplicityPlaceCount != 0) {

				return multiplicityPlaceCount * mPlaceCount;
			}
			else {
				
				return mPlaceCount > mMinCount ? mMinCount : mPlaceCount;
			}
		}
		else {
			
			if( multiplicityMinCount * mMinCount > multiplicityPlaceCount * mPlaceCount ) {

				return multiplicityMinCount * mMinCount;
			}
			else {

				return multiplicityPlaceCount * mPlaceCount;
			}
		}
	}
}
