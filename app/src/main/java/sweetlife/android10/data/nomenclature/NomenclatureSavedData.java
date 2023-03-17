package sweetlife.android10.data.nomenclature;

import java.util.Calendar;

import sweetlife.android10.database.nomenclature.Request_Search;


import android.database.Cursor;

public class NomenclatureSavedData {

	private Cursor   mCursorCR;
	private Cursor   mCursorNomenclature;
	private Cursor   mCursorGroupsNomenclature;
	private Cursor   mCursorSearch;
	private Cursor   mCursorHistory;
	private Cursor   mCursorMaxPrice;

	private int      mPositionCR;
	private int      mPositionNomenclature;
	private int      mPositionGroupNomenclature;
	private int      mPositionTwentySeven;
	private int      mPositionSearch;
	private int      mPositionHistory;
	private int      mPositionMaxPrice;

	private int      mSearchBy = Request_Search.SEARCH_ARTICLE;
	private String   mSearchString = "";


	private String   mHistorySearchString = "";
	private Calendar mHistoryDateFrom = null;
	private Calendar mHistoryDateTo = null;

	private int     mActiveTab = 0;

	private static NomenclatureSavedData instance;

	private NomenclatureSavedData() {

		mHistoryDateFrom = Calendar.getInstance();
		//mHistoryDateFrom.roll(Calendar.MONTH, -3);
		mHistoryDateFrom.add(Calendar.DAY_OF_YEAR, -90);
		mHistoryDateTo = Calendar.getInstance();

	}

	public static NomenclatureSavedData getInstance() {

		if (instance == null){

			instance = new NomenclatureSavedData();
		}   	

		return instance;
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();

		CleanData();
	}

	public void CleanData() {

		if( mCursorCR != null && !mCursorCR.isClosed() ) {

			mCursorCR.close();
			mCursorCR = null;
		}

		if( mCursorNomenclature != null && !mCursorNomenclature.isClosed() ) {

			mCursorNomenclature.close();
			mCursorNomenclature = null;
		}

		if( mCursorGroupsNomenclature != null && !mCursorGroupsNomenclature.isClosed() ) {

			mCursorGroupsNomenclature.close();
			mCursorGroupsNomenclature = null;
		}

		if( mCursorSearch != null && !mCursorSearch.isClosed() ) {

			mCursorSearch.close();
			mCursorSearch = null;
		}

		if( mCursorHistory != null && !mCursorHistory.isClosed() ) {

			mCursorHistory.close();
			mCursorHistory = null;
		}

		if( mCursorMaxPrice != null && !mCursorMaxPrice.isClosed() ) {

			mCursorMaxPrice.close();
			mCursorMaxPrice = null;
		}

		mPositionCR                = -1;
		mPositionNomenclature      = -1;
		mPositionGroupNomenclature = -1;
		mPositionTwentySeven       = -1;
		mPositionSearch            = -1;
		mPositionHistory           = -1;
		mPositionMaxPrice          = -1;

		mSearchBy                  = 0;
		mSearchString              = null;

		mHistorySearchString       = null;
		mHistoryDateFrom           = Calendar.getInstance();
		//mHistoryDateFrom.roll(Calendar.MONTH, -3);
		mHistoryDateFrom.add(Calendar.DAY_OF_YEAR, -90);
		mHistoryDateTo             = Calendar.getInstance();

		mActiveTab                 = 0;
	}

	public Cursor getCursorCR() {

		if(mCursorCR != null && mCursorCR.isClosed()) {

			mCursorCR = null;
		}

		return mCursorCR;
	}

	public void setCursorCR(Cursor cursorCR) {

		if( mCursorCR == null ) {

			mCursorCR = cursorCR;
		}
		else {

			if( !mCursorCR.equals(cursorCR) ) {

				if( !mCursorCR.isClosed() ) {

					mCursorCR.close();
				}

				mCursorCR = cursorCR;	
			}
		}
	}

	public Cursor getCursorNomenclature() {

		if(mCursorNomenclature != null && mCursorNomenclature.isClosed()) {

			mCursorNomenclature = null;
		}

		return mCursorNomenclature;
	}

	public void setCursorNomenclature(Cursor cursorNomenclature) {

		if( mCursorNomenclature == null ) {

			mCursorNomenclature = cursorNomenclature;
		}
		else {

			if( !mCursorNomenclature.equals(cursorNomenclature) ) {

				if( !mCursorNomenclature.isClosed() ) {

					mCursorNomenclature.close();
				}

				mCursorNomenclature = cursorNomenclature;	
			}
		}
	}


	public Cursor getCursorGroupsNomenclature() {

		if(mCursorGroupsNomenclature != null && mCursorGroupsNomenclature.isClosed()) {

			mCursorGroupsNomenclature = null;
		}

		return mCursorGroupsNomenclature;
	}

	public void setCursorGroupsNomenclature(Cursor cursorGroupsNomenclature) {

		if( mCursorGroupsNomenclature == null ) {

			mCursorGroupsNomenclature = cursorGroupsNomenclature;
		}
		else {

			if( !mCursorGroupsNomenclature.equals(cursorGroupsNomenclature) ) {

				if( !mCursorGroupsNomenclature.isClosed() ) {

					mCursorGroupsNomenclature.close();
				}

				mCursorGroupsNomenclature = cursorGroupsNomenclature;	
			}
		}
	}

	public Cursor getCursorSearch() {

		if(mCursorSearch != null && mCursorSearch.isClosed()) {

			mCursorSearch = null;
		}

		return mCursorSearch;
	}

	public void setCursorSearch(Cursor cursorSearch) {

		if( mCursorSearch == null ) {

			mCursorSearch = cursorSearch;
		}
		else {

			if( !mCursorSearch.equals(cursorSearch) ) {

				if( !mCursorSearch.isClosed() ) {

					mCursorSearch.close();
				}

				mCursorSearch = cursorSearch;	
			}
		}
	}

	public Cursor getCursorHistory() {

		if(mCursorHistory != null && mCursorHistory.isClosed()) {

			mCursorHistory = null;
		}

		return mCursorHistory;
	}

	public void setCursorHistory(Cursor cursorHistory) {

		if( mCursorHistory == null ) {

			mCursorHistory = cursorHistory;
		}
		else {

			if( !mCursorHistory.equals(cursorHistory) ) {

				if( !mCursorHistory.isClosed() ) {

					mCursorHistory.close();
				}

				mCursorHistory = cursorHistory;	
			}
		}
	}

	public Cursor getCursorMaxPrice() {

		if(mCursorMaxPrice != null && mCursorMaxPrice.isClosed()) {

			mCursorMaxPrice = null;
		}

		return mCursorMaxPrice;
	}

	public void setCursorMaxPrice(Cursor cursorMaxPrice) {

		if( mCursorMaxPrice == null ) {

			mCursorMaxPrice = cursorMaxPrice;
		}
		else {

			if( !mCursorMaxPrice.equals(cursorMaxPrice) ) {

				if( !mCursorMaxPrice.isClosed() ) {

					mCursorMaxPrice.close();
				}

				mCursorMaxPrice = cursorMaxPrice;	
			}
		}
	}

	public int getPositionCR() {

		return mPositionCR;
	}

	public void setPositionCR(int positionCR) {

		mPositionCR = positionCR;
	}

	public int getPositionNomenclature() {

		return mPositionNomenclature;
	}

	public void setPositionNomenclature(int positionNomenclature) {

		mPositionNomenclature = positionNomenclature;
	}

	public int getPositionGroupNomenclature() {

		return mPositionGroupNomenclature;
	}

	public void setPositionGroupNomenclature(int positionGroupNomenclature) {

		mPositionGroupNomenclature = positionGroupNomenclature;
	}

	public int getPositionTwentySeven() {

		return mPositionTwentySeven;
	}

	public void setPositionTwentySeven(int positionTwentySeven) {

		mPositionTwentySeven = positionTwentySeven;
	}

	public int getPositionSearch() {

		return mPositionSearch;
	}

	public void setPositionSearch(int positionSearch) {

		mPositionSearch = positionSearch;
	}

	public int getPositionHistory() {

		return mPositionHistory;
	}

	public void setPositionHistory(int positionHistory) {

		mPositionHistory = positionHistory;
	}

	public int getPositionMaxPrice() {

		return mPositionMaxPrice;
	}

	public void setPositionMaxPrice(int positionMaxPrice) {

		mPositionMaxPrice = positionMaxPrice;
	}

	public int getSearchBy() {

		return mSearchBy;
	}

	public void setSearchBy(int searchBy) {

		mSearchBy = searchBy;
	}

	public String getSearchString() {

		return mSearchString;
	}

	public void setSearchString(String searchString) {

		mSearchString = searchString;
	}

	public String getHistorySearchString() {

		return mHistorySearchString;
	}

	public void setHistorySearchString(String historySearchString) {

		mHistorySearchString = historySearchString;
	}

	public Calendar getHistoryDateFrom() {

		return mHistoryDateFrom;
	}

	public void setHistoryDateFrom(Calendar historyDateFrom) {

		mHistoryDateFrom = historyDateFrom;
	}

	public Calendar getHistoryDateTo() {

		return mHistoryDateTo;
	}

	public void setHistoryDateTo(Calendar historyDateTo) {

		mHistoryDateTo = historyDateTo;
	}

	public int getActiveTab() {

		return mActiveTab;
	}

	public void setActiveTab(int activeTab) {

		mActiveTab = activeTab;
	}
}
