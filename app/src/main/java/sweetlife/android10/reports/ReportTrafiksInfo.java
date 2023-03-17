package sweetlife.android10.reports;

public class ReportTrafiksInfo extends ReportInfo {

	private Boolean           mOnlyShipped;
	private Boolean           mOnlyNotShipped;
	
	public ReportTrafiksInfo( String reportName,
			String beginPeriod,
			String endPeriod,
			String agentKod,
			Boolean onlyShipped,
			Boolean onlyNotShipped) {
		
		super(reportName, beginPeriod, endPeriod, agentKod);
	
		mOnlyShipped = onlyShipped;
		mOnlyNotShipped = onlyNotShipped;
	}
	
	public Boolean getOnlyShipped() {

		return mOnlyShipped;
	}

	public void setOnlyShipped(Boolean onlyShipped) {

		mOnlyShipped = onlyShipped;
	}

	public Boolean getOnlyNotShipped() {

		return mOnlyNotShipped;
	}

	public void setOnlyNotShipped(Boolean onlyNotShipped) {

		mOnlyNotShipped = onlyNotShipped;
	}
}
