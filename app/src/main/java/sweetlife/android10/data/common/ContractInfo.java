package sweetlife.android10.data.common;

public class ContractInfo {

	private String mID;
	private String mKod;
	private String mNaimenovanie;
	private String mContractGroupName;
	private boolean mClosed;

	public ContractInfo(String id, String kod, String naimenovanie,
						String contractGroupName, boolean closed) {

		mID = id;
		mKod = kod;
		mNaimenovanie = naimenovanie;
		mContractGroupName = contractGroupName;
		mClosed = closed;
	}

	public String getID() {

		return mID;
	}

	public String getKod() {

		return mKod;
	}

	public String getNaimenovanie() {

		return mNaimenovanie;
	}

	public String getContractGroupName() {

		return mContractGroupName;
	}

	public boolean isClosed() {
		return mClosed;
	}
}
