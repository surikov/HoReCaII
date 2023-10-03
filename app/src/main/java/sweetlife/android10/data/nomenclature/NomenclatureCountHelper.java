package sweetlife.android10.data.nomenclature;


public class NomenclatureCountHelper {

	private double mMinCount;
	private double mPlaceCount;

	public NomenclatureCountHelper(double minCount, double placeCount) {

		mMinCount = minCount;
		mPlaceCount = placeCount;
	}

	public double getMinCount() {

		return mMinCount;
	}

	public double getPlaceCount() {

		return mPlaceCount;
	}

	public double ReCalculateCount(double inputCount) {

		if (mPlaceCount == 0 || mMinCount == 0) {

			return inputCount;
		}

		double resultCount = inputCount;

		int multiplicityMinCount = (int) ((inputCount * 100) / (mMinCount * 100));
		int multiplicityPlaceCount = (int) ((inputCount * 100) / (mPlaceCount * 100));

		if (multiplicityMinCount == 0 || multiplicityPlaceCount == 0) {

			if (multiplicityMinCount != 0) {

				resultCount = multiplicityMinCount * mMinCount;
			} else if (multiplicityPlaceCount != 0) {

				resultCount = multiplicityPlaceCount * mPlaceCount;
			} else {

				resultCount = mPlaceCount > mMinCount ? mMinCount : mPlaceCount;
			}
		} else {

			if (multiplicityMinCount * mMinCount > multiplicityPlaceCount * mPlaceCount) {

				resultCount = multiplicityMinCount * mMinCount;
			} else {

				resultCount = multiplicityPlaceCount * mPlaceCount;
			}
		}
		if (resultCount < mMinCount && mMinCount > 0) {
			resultCount = mMinCount;
		}
		return resultCount;
	}
}
